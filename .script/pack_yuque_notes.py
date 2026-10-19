#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
把 MooN 笔记仓库下的全部 .md 笔记，按原目录结构打包成 zip（用于批量导入语雀）。

【方案一：自动扫描 + 黑名单排除】
- 自动扫描 SRC_ROOT 下所有顶层目录，仅排除隐藏目录(.开头的)和 EXCLUDE_TOP_DIRS 黑名单；
  新增 / 删除 / 更名笔记目录都自动跟随，无需维护白名单清单。
- 根级 md（README.md、index.md 等）不打包（只扫顶层目录，不扫根文件）。

【相对路径，跨电脑可用】
- SRC_ROOT 由本脚本所在位置自动推导（脚本位于 <仓库根>/.script/，上溯一级即仓库根），
  因此只要把整个仓库一起拷贝/换电脑，无需修改任何配置。
- 默认 zip 也输出到仓库根下（相对路径），随仓库走。

【其它要点（沿用已确认方案）】
1. 只打包 .md，完整保留目录结构；图片等其它文件不入包。
2. 在【副本】中把标准 markdown 图片语法 `![](url)` 的相对路径链接改写成 gitee 绝对地址，
   【绝不修改原文档】。
3. 只处理相对路径图片（`images/`、`./images/`、`../images/`）；已是 http(s):// 等远程绝对地址的
   不重复改写；`../` 上溯超出仓库根时保留原样并统计。
4. `<img src>` HTML 标签、`![[...]]` wikilink、以及代码块(fence)内的图片写法一律不动。
5. 图片路径解析：相对该 md 所在目录解析出"相对仓库根"路径，前面拼接 GITEE_RAW_BASE 并做 URL 编码。
   例：`a/b/示例.md` 里的 `![](images/xxx.jpg)`
   → `https://gitee.com/moonzero/code-note/raw/dev/a/b/images/xxx.jpg`。

用法：
    python pack_yuque_notes.py                 # 执行打包
    python pack_yuque_notes.py --dry-run       # 只预览统计，不生成任何文件
    python pack_yuque_notes.py --zip "路径"    # 覆盖默认 zip 输出位置
"""

import argparse
import re
import shutil
import tempfile
import urllib.parse
import zipfile
from pathlib import Path

# ======================= 配置区（按需修改） =======================
# SRC_ROOT 由脚本位置自动推导：脚本位于 <仓库根>/.script/，上溯一级即仓库根。
# 整仓换电脑无需改这里；如需指向其它仓库，填 SRC_ROOT_OVERRIDE。
SCRIPT_DIR = Path(__file__).resolve().parent
SRC_ROOT_OVERRIDE = None  # 留空=自动；可填绝对路径，如 Path(r"D:\MyRepo")
SRC_ROOT = SRC_ROOT_OVERRIDE or SCRIPT_DIR.parent

# 黑名单：要【排除】的顶层目录名（隐藏目录 .开头的会自动跳过，无需列出）。
EXCLUDE_TOP_DIRS = [
    "attachments",  # 附件区
    "resources",
    "images",
    "node_modules",
]

# zip 输出位置（相对 SRC_ROOT，随仓库走；可用 --zip 覆盖）
ZIP_SAVE_PATH = SRC_ROOT / "code-note-语雀导入.zip"
# gitee 平台仓库根（对应仓库根 /，分支 dev）
GITEE_RAW_BASE = "https://gitee.com/moonzero/code-note/raw/dev/"
# 可选：精确排除某些相对路径（目录或文件），留空表示不排除。
# 例：EXCLUDE = ["分布式微服务/SpringBoot/旧版笔记.md"]
EXCLUDE = []
# ================================================================

# 标准 markdown 图片语法：![alt](url) 或 ![alt](url "title")
IMG_RE = re.compile(
    r"!\[([^\]]*)\]\(\s*([^)\s\"']+)(?:\s+[\"']([^\"']*)[\"'])?\s*\)"
)


def is_remote_absolute(url: str) -> bool:
    """是否为远程绝对地址（http(s)://、协议相对 //、data:、其它 scheme:）"""
    return bool(re.match(r"^//", url) or re.match(r"^[a-zA-Z][a-zA-Z0-9+.\-]*:", url))


def resolve_to_gitee(url: str, md_rel_dir: Path):
    """
    把相对图片路径解析为 gitee 绝对 URL。
    md_rel_dir：该 md 文件所在目录（相对 SRC_ROOT 的 Path）。
    返回改写后的 URL；若上溯超出仓库根则返回 None。
    """
    raw = url.replace("\\", "/")
    if raw.startswith("/"):
        base = Path(".")  # 以仓库根为基准
        ref_raw = raw.lstrip("/")
    else:
        base = md_rel_dir
        ref_raw = raw
    ref = Path(ref_raw)
    resolved = base / ref
    parts = []
    for part in resolved.parts:
        if part in (".", ""):
            continue
        if part == "..":
            if parts:
                parts.pop()
            else:
                return None  # 超出仓库根
        else:
            parts.append(part)
    if not parts:
        return None
    rel = "/".join(parts)
    encoded = urllib.parse.quote(rel, safe="/%")
    return GITEE_RAW_BASE.rstrip("/") + "/" + encoded


def read_text_with_bom(path: Path):
    """读取 UTF-8 文本，返回 (文本, 是否有 BOM)。解码失败返回 None。"""
    data = path.read_bytes()
    has_bom = data.startswith(b"\xef\xbb\xbf")
    try:
        text = data.decode("utf-8-sig" if has_bom else "utf-8")
    except UnicodeDecodeError:
        return None
    return text, has_bom


def rewrite_md_text(text: str, md_rel_dir: Path, stats: dict) -> str:
    """在文本副本上改写标准图片链接；跳过代码块(fence)内内容。返回改写后的文本。"""
    in_fence = False

    def repl(m):
        alt = m.group(1)
        url = m.group(2).strip()
        title = m.group(3)
        if is_remote_absolute(url):
            stats["absolute"] += 1
            return m.group(0)
        new_url = resolve_to_gitee(url, md_rel_dir)
        if new_url is None:
            stats["out_of_root"] += 1
            return m.group(0)
        stats["rewritten"] += 1
        if title:
            return f'![{alt}]({new_url} "{title}")'
        return f"![{alt}]({new_url})"

    out_lines = []
    for line in text.split("\n"):
        stripped = line.lstrip()
        # 检测围栏代码块（``` 或 ~~~）
        if stripped.startswith("```") or stripped.startswith("~~~"):
            in_fence = not in_fence
            out_lines.append(line)
            continue
        if in_fence:
            out_lines.append(line)
            continue
        out_lines.append(IMG_RE.sub(repl, line))
    return "\n".join(out_lines)


def discover_note_dirs() -> list:
    """方案一：扫描 SRC_ROOT 下所有顶层目录，排除隐藏目录与黑名单。"""
    dirs = []
    for p in sorted(SRC_ROOT.iterdir()):
        if not p.is_dir():
            continue
        name = p.name
        if name.startswith("."):
            continue  # 隐藏/系统目录（含 .script 自身）
        if name in EXCLUDE_TOP_DIRS:
            continue
        dirs.append(p)
    return dirs


def collect_md_files(dirs: list) -> list:
    """在发现的笔记目录下递归收集所有 .md 文件（应用 EXCLUDE 精确排除）。"""
    files = []
    for base in dirs:
        for p in sorted(base.rglob("*.md")):
            if not p.is_file():
                continue
            rel = p.relative_to(SRC_ROOT).as_posix()
            if any(rel == e or rel.startswith(e.rstrip("/") + "/") for e in EXCLUDE):
                continue
            files.append(p)
    return files


def build_copy_and_rewrite(files: list, tmp_root: Path) -> dict:
    """把每个 md 复制到临时目录（副本），并在副本上改写图片链接。返回统计。"""
    stats = {"rewritten": 0, "absolute": 0, "out_of_root": 0, "unreadable": 0, "files": 0}
    for src in files:
        rel = src.relative_to(SRC_ROOT)
        dst = tmp_root / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        res = read_text_with_bom(src)
        if res is None:
            shutil.copyfile(src, dst)
            stats["unreadable"] += 1
            print(f"  [警告] 无法按 UTF-8 解码，按原样复制：{rel}")
            continue
        text, has_bom = res
        md_rel_dir = rel.parent
        new_text = rewrite_md_text(text, md_rel_dir, stats)
        out_bytes = new_text.encode("utf-8-sig" if has_bom else "utf-8")
        dst.write_bytes(out_bytes)
        stats["files"] += 1
    return stats


def make_zip(tmp_root: Path, zip_path: Path):
    zip_path.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as zf:
        for f in sorted(tmp_root.rglob("*.md")):
            zf.write(f, arcname=f.relative_to(tmp_root).as_posix())


def per_dir_counts(files: list) -> dict:
    counts = {}
    for p in files:
        rel = p.relative_to(SRC_ROOT)
        key = rel.parts[0]
        counts[key] = counts.get(key, 0) + 1
    return counts


def main():
    ap = argparse.ArgumentParser(description="自动扫描并打包笔记 md 为 zip，改写图片链接为 gitee 地址")
    ap.add_argument("--dry-run", action="store_true", help="只统计预览，不生成任何文件")
    ap.add_argument("--zip", type=str, default=None, help="覆盖 zip 输出位置")
    args = ap.parse_args()

    zip_path = Path(args.zip) if args.zip else ZIP_SAVE_PATH

    print(f"仓库根（自动定位）：{SRC_ROOT}")
    note_dirs = discover_note_dirs()
    print(f"自动发现的笔记顶层目录（{len(note_dirs)} 个）：")
    for d in note_dirs:
        print(f"    {d.name}")
    print(f"已排除的黑名单目录：{EXCLUDE_TOP_DIRS}")
    print("收集 .md 文件……")
    files = collect_md_files(note_dirs)
    total = len(files)
    print(f"共收集到 {total} 个 .md 文件：")
    for k, v in per_dir_counts(files).items():
        print(f"    {k}: {v} 个")

    if args.dry_run:
        print("\n[dry-run] 只统计预览：对每个文件做只读解析，不写临时目录、不生成 zip。")
        stats = {"rewritten": 0, "absolute": 0, "out_of_root": 0, "unreadable": 0, "files": 0}
        for src in files:
            rel = src.relative_to(SRC_ROOT)
            res = read_text_with_bom(src)
            if res is None:
                stats["unreadable"] += 1
                continue
            text, _ = res
            rewrite_md_text(text, rel.parent, stats)
            stats["files"] += 1
        print("  解析统计：")
        print(f"    文件数        : {stats['files']}")
        print(f"    将改写为gitee : {stats['rewritten']}")
        print(f"    已是绝对地址跳过: {stats['absolute']}")
        print(f"    越界保留原样   : {stats['out_of_root']}")
        print(f"    无法解码按原样复制: {stats['unreadable']}")
        print("  （本次未生成任何文件）")
        return

    print("\n创建临时副本并改写图片链接……")
    tmp_root = Path(tempfile.mkdtemp(prefix="yuque_zip_"))
    try:
        stats = build_copy_and_rewrite(files, tmp_root)
        print("  统计：")
        print(f"    副本文件数      : {stats['files']}")
        print(f"    改写为gitee地址 : {stats['rewritten']}")
        print(f"    已是绝对地址跳过 : {stats['absolute']}")
        print(f"    越界保留原样    : {stats['out_of_root']}")
        if stats["unreadable"]:
            print(f"    无法解码按原样复制: {stats['unreadable']}")
        print(f"打包 zip：{zip_path}")
        make_zip(tmp_root, zip_path)
        size_mb = zip_path.stat().st_size / 1024 / 1024
        print(f"完成。zip 大小约 {size_mb:.2f} MB，共 {total} 个 .md 文件。")
    finally:
        shutil.rmtree(tmp_root, ignore_errors=True)


if __name__ == "__main__":
    main()
