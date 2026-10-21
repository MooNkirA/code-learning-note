# AGENTS.md

本文件为 AI 编码助手（Claude Code、Qoder、Trae 等）在本仓库中工作时提供指导说明。

## 项目概述

这是一个个人编程笔记仓库（“MooNkirA 的代码笔记”），按技术主题组织成体系化的中文技术文档。全仓库约 390 篇正文 Markdown 笔记（另有 24 个 `index.md` 入口页），同时是 **Obsidian 笔记库** 和 **VitePress 静态文档站**（在线地址 https://moonkira.github.io/ ）。

内容重心：`分布式微服务/`（86 篇）、`项目资料/`（78 篇）、`后端框架/`（47 篇）、`Frontend/`（35 篇），均不含 `index.md`。

## 写作风格：动手前必读

<span style="color: red;">**本仓库笔记的书写风格由一个 Skill 单独维护，不要凭感觉改写。**</span>

- 权威来源：`.trae/skills/moon-note-style/SKILL.md`（核心约定 + 骨架模板 + 12 项校对清单），细则见同目录 `reference.md`。
- 新建、改写、补全、校对任何一篇笔记前，先读该 Skill 并按其产出；本文件不重复其规则，以免两处漂移。

最容易被违反的几条，先行提示：

| 约定 | 说明 |
| :--- | :--- |
| 正文从 `##` 起 | 不用 `#`（H1）当正文主标题；标题也不手动编号 |
| 样式只用 `<span style="...">` | `<font color=red>`、`<u>` 是遗留写法，**已废弃**，改到时顺手替换 |
| 红色只用于最关键警示 | 重要结论用紫字，次要用粉字，不要滥用彩色 |
| 新笔记用 Obsidian callout | 不再用 `> Notes:` / `> Tips:`；改老笔记时整篇风格保持一致 |
| 代码块必须标语言，注释写中文 | |
| 中英文、中文与数字之间加半角空格 | `线程池是 JDK 1.5 后的新特性` |
| 换行一律靠空行 | 禁止行尾两空格硬换行；callout 内多段用只含 `>` 的空行分隔 |

## 仓库结构

```text
Workspace/
├── AI/ Database/ DevOps/ Frontend/ Java/ JavaWeb/ JVM/ Linux/ Python/
├── 分布式微服务/ 后端框架/ 并发编程/ 面试手册/ 项目资料/ 其他/
├── attachments/             # 模板、思维导图、历史学习笔记、README 打赏图（images/）
├── .script/                 # 本地辅助脚本：语雀打包（pack_yuque_notes.py、打包笔记-语雀.bat）、VitePress（vitepress-本地开发预览.bat、vitepress-清理构建缓存.bat）
├── .trae/skills/            # moon-note-style 等 Agent Skill
├── .obsidian/               # Obsidian 配置与插件（已纳入版本管理）
├── .vitepress/              # VitePress 配置（config.ts、sidebar.ts、wikilink.ts、html-guard.ts、gen-index.cjs）
├── .github/workflows/       # deploy.yml 部署 Pages；mirror-gitee.yml 同步 main/dev 到 Gitee
├── index.md                 # VitePress 首页（home layout）
├── README.md                # 手工维护的索引（标准 Markdown 链接，含打赏二维码）
├── package.json             # VitePress 依赖与脚本（pnpm 管理）
└── AGENTS.md                # 本文件；CLAUDE.md 通过 @AGENTS.md 指向此处
```

## 命名与链接约定

- **文件命名**：主导模式是 `主题-子主题.md`，例如 `Java基础-IO编程.md`、`并发编程-线程池.md`、`JVM-GC.md`。子主题在 Java 系用中文、Python 系多用英文，各目录内部保持一致即可。
- **正文文件名需全仓库唯一**：<span style="color: purple;">**VitePress 的 wikilink 插件以“不含扩展名的文件名”为键**</span>，同名文件在构建期后者覆盖前者，会导致 wiki 链接跳错。`index.md` 入口页是例外：当前共有 24 个（15 个一级分类 + 9 个二级目录），因此不要用 `[[index]]` 做跨笔记链接。
- **项目实战类笔记**允许保留教程原始序号风格：`Day07-商品录入模块.md`、`15-项目部署.md`。
- **跨笔记链接**：Obsidian wiki 链接，常见形式为 `[[文件名]]`、`[[文件名|显示别名]]`、`[[文件名#章节|别名]]`；正文里提到其他笔记/章节用书名号《》。VitePress 构建期由 `.vitepress/wikilink.ts` 自动将其转为标准 Markdown 链接。
- **图片**：`![](images/文件名.png)`，引用**与笔记同级的 `images/` 子目录**，不要臆造路径。
- 笔记**通常不带 YAML frontmatter**，也不写手动 TOC 目录；资料引用可放在正文末尾的 `## 参考` 章节。

## Obsidian 配置

**已启用的社区插件（11 个）**：`dataview`、`templater-obsidian`、`obsidian-hover-editor`、`obsidian-outliner`、`obsidian-quiet-outline`、`easy-typing-obsidian`、`obsidian-style-settings`、`table-editor-obsidian`、`tag-wrangler`、`obsidian-custom-attachment-location`、`oz-clear-unused-images`。

- `templater-obsidian`：`templates_folder = attachments/templates`，`trigger_on_file_creation = true`。
- `oz-clear-unused-images`：清理未引用图片，删除到 `.trash`，排除 `attachments,resources`。
- `header-enhancer` 已安装但处于**禁用**状态。`moon-note-style` 约定“标题不手动编号，编号交给插件”，但当前编号不会渲染；改写笔记时**仍然不要手动加编号**。

**影响编辑行为的设置（`.obsidian/app.json`）：**

- `newLinkFormat: "shortest"` + `useMarkdownLinks: false` → 新建链接默认写成最短形式的 `[[wiki 链接]]`。
- `alwaysUpdateLinks: true` → 重命名/移动文件时 Obsidian 会自动改写链接；用命令行或脚本移动文件则**不会**更新链接，需谨慎。
- `strictLineBreaks: true` → 换行靠空行；`newFileLocation: "current"`、`trashOption: "system"`、`livePreview: false`。
- `attachmentFolderPath: "attachments"` 被 `obsidian-custom-attachment-location` 覆盖：实际附件落到 **`./images`（笔记同级）**，文件名格式为 17 位时间戳 `YYYYMMDDHHmmssSSS`。

**主题与外观**：主题 AnuPpuccin（`.obsidian/appearance.json`，`cssTheme: "AnuPpuccin"`）；启用片段 `snippets/extended-colorschemes.css`（另有两枚护眼背景片段未启用）。

## VitePress 文档站

站点由 VitePress（`^1.6.4`）驱动，配置在 `.vitepress/` 目录下，包管理用 pnpm。

> [!warning] 禁止在本地执行构建
> 本机执行 `pnpm docs:build` 会耗尽内存并导致系统卡死，**禁止在本地运行**；不要通过调大 `NODE_OPTIONS` 重试。构建与部署统一交给 GitHub Actions，本地只用 `pnpm docs:dev` 验证页面。

**核心配置文件（`.vitepress/`）：**

| 文件 | 作用 |
| :--- | :--- |
| `config.ts` | 主配置：站点标题、导航、搜索、页脚、构建输出等 |
| `sidebar.ts` | 自动扫描目录树生成侧边栏，按一级分类分组 |
| `wikilink.ts` | 构建期扫描所有 `.md` 建立文件名→URL 映射，将 `[[文件名]]` 转为标准 Markdown 链接 |
| `html-guard.ts` | 从被转义的文本中恢复白名单 `<span>`/`<font>` 标签（旧笔记中的彩色样式） |
| `gen-index.cjs` | 生成 15 个一级分类的 `index.md` 入口页；根目录从脚本位置推导，运行前确认 `categories` 与当前目录一致 |
| `../.script/vitepress-*.bat` | Windows 本地辅助脚本：`vitepress-本地开发预览.bat` 启动开发、`vitepress-清理构建缓存.bat` 清理 `dist`/`cache` |
| `clean-broken-images.cjs` / `fix-img-paths.cjs` | 维护期脚本（清理坏图、修正图片路径） |

**常用命令（在仓库根目录执行）：**

```bash
pnpm docs:dev       # 本地开发预览（唯一推荐的本地命令）
# pnpm docs:build   # 禁止本地执行；构建由 GitHub Actions 完成
```

**依赖与安全**：`pnpm-workspace.yaml` 通过 `overrides` 强制 `vite ^6.4.3`、`esbuild ^0.25.0`，用于修复已知 CVE（VitePress 1.6.4 声明的 vite 5.x 已无修复版本）。**不要移除这些 overrides**；升级 VitePress 时先确认上游已包含修复版本。

**`config.ts` 实际生效项：**

- `base: '/'`、`cleanUrls: true`、`outDir: '.vitepress/dist'`。
- 首页是根目录 `index.md`（VitePress home layout），不是 `README.md`。
- 搜索用 VitePress 内置 `provider: 'local'`（Minisearch），中文界面；Mermaid 由 `vitepress-plugin-mermaid` 提供。
- 侧边栏由 `sidebar.ts` 自动生成，**不需要手写 `_sidebar.md`**。
- `srcExclude` 排除了 `**/images/**`、`**/attachments/**`、`**/resources/**`、`AGENTS.md`、`**/.obsidian/**`、`**/.trae/**`、`**/.claude/**`。
- `ignoreDeadLinks: true`（旧笔记中的失效图片/链接不报错）。
- `markdown.html: false`（禁用原始 HTML，避免旧笔记中的裸 HTML 导致 Vue 编译错误）。

**Wiki 链接处理**：`.vitepress/wikilink.ts` 在构建期自行扫描仓库内所有 `.md` 文件（排除 `.obsidian`、`.trae`、`.claude`、`.vscode`、`.git`、`node_modules`、`.vitepress`、`resources`、`attachments`），建立映射后将 `[[文件名#章节|别名]]` 替换为标准链接。**Wikilink 映射无需生成脚本，也不需要 `file-map.json`**；`gen-index.cjs` 只负责生成分类入口页，不是构建的前置步骤。

## 笔记打包（语雀导入）

`.script/` 是本地打包工具，不参与 VitePress 构建。

- `pack_yuque_notes.py`：仅用 Python 3 标准库。自动扫描仓库根下所有非隐藏一级目录（排除 `attachments`、`resources`、`images`、`node_modules`），按原目录结构收集其中全部 `.md`（含各级 `index.md`，不含根目录的 README/index/AGENTS），在临时副本中把相对图片路径改写为 Gitee raw 地址后打包；**原笔记不会被修改**。远程图片、`<img>`、`![[...]]` 和代码块内图片不改写。
- 输出默认是仓库根下的 `code-note-语雀导入.zip`；可用 `--zip` 覆盖，`--dry-run` 只统计不写文件。产物上传语雀后手动删除，**不要提交到仓库**。
- 用法：

  ```bash
  python .script/pack_yuque_notes.py --dry-run
  python .script/pack_yuque_notes.py
  ```

  Windows 可直接双击 `.script/打包笔记-语雀.bat`。
- 脚本根目录由 `.script/` 所在位置上溯一级推导，**不要移动脚本目录**；`GITEE_RAW_BASE` 固定指向 Gitee 的 `dev` 分支，换仓库/分支时同步修改。图片必须已推送到 Gitee 对应路径，否则语雀导入后图片会 404。
- 新增一级笔记目录会被自动发现并打包；但 VitePress 侧边栏和入口页仍需按「新增笔记流程」手动登记。

## 索引与部署

- **`README.md`**：手工维护的笔记索引，12 个 `##` 分区，条目使用标准 Markdown 相对链接 `[名称](路径.md)`，**不使用 wikilink**；末尾含打赏二维码（引用 `attachments/images/wechat_pay.png`、`ali_pay.jpg`）。分区名称以文件现状为准（AI、Java 核心技术、后端框架、前端资料、数据库、DevOps 工具、Linux、分布式 & 微服务架构、并发编程、Python、其他、收集的项目实战资料）。新增重要笔记应补进对应分区；目前仍有缺口（`面试手册/` 的 10 篇面试题、`AI/AI-Assistance/Agent-Skills` 等尚未收录）。
- **首页是 `index.md`**（VitePress home layout），不是 `README.md`。
- **发布方式**：GitHub Actions 自动部署，配置在 `.github/workflows/deploy.yml`。push 到 `main` 分支后自动触发：ubuntu runner 上 `pnpm install` → `pnpm docs:build`（CI 已配 8 GB 堆）→ 上传 `.vitepress/dist` 为 artifact → `deploy-pages` 发布到 GitHub Pages。也支持在 Actions 页面手动 `workflow_dispatch` 触发。**本地不执行 build**。
- **Gitee 镜像**：`.github/workflows/mirror-gitee.yml` 在 push `main`/`dev` 或手动触发时，通过 SSH 部署公钥将对应分支同步到 [moonzero/code-note](https://gitee.com/moonzero/code-note)。Gitee 端为**只读镜像**，不在 Gitee 上做任何修改；同步依赖 GitHub Secret `GITEE_SSH_PRIVATE_KEY` 与 Gitee 仓库部署公钥（需启用推送）。工作流使用 `fetch-depth: 0` 拉取完整历史（仓库约 700 MiB），运行时间会长于 Pages 构建；轮换密钥时需同时更新 Gitee 部署公钥和 GitHub Secret。该工作流与 Pages 发布相互独立。
- `.nojekyll`（空文件）用于关闭 Jekyll 处理，保证 `_` 开头文件与点目录原样发布。
- `.gitignore` 忽略编辑器与构建产物（`.vscode`、`*.class`、`target`、`*.iml`、VNote 缓存、`desktop.ini`、`node_modules/`、`.vitepress/dist/`、`.vitepress/cache/` 等），不忽略笔记内容；语雀打包 zip 未加入忽略，使用后手动清理。

## 新增一篇笔记的完整流程

1. 读 `.trae/skills/moon-note-style/SKILL.md`，按其骨架与约定撰写。
2. 确认文件名符合 `主题-子主题.md` 且**全仓库无同名正文笔记**（`index.md` 除外；wikilink 插件以文件名为键，重名会覆盖）。
3. 放到对应主题目录（一层深，如 `Java/`、`分布式微服务/SpringBoot/`）；跨主题的概览笔记可放在主题目录顶层。
4. 若新增一级主题目录，还要把目录名加入 `.vitepress/sidebar.ts` 的 `CATEGORIES`，并按需更新 `.vitepress/gen-index.cjs` 的 `categories`，否则侧边栏/入口页不会生成。
5. 图片放到该笔记同级的 `images/` 目录，用 `![](images/xxx.png)` 引用。
6. 用 `[[文件名|别名]]` 关联相关笔记（VitePress 构建期自动解析为标准链接，无需手动维护映射表）。
7. 在 `README.md` 的相应分区补一条索引，沿用标准 Markdown 相对链接格式。
8. 校对：对照 Skill 的 12 项清单自查，尤其 `<font>`/`<u>`、H1、行尾硬换行、代码块语言标注。

## 已知问题与待确认项

- `header-enhancer` 已禁用，与“编号交给插件”的风格约定冲突（见上文）。
- `AI/AI-Experience/` 是空目录。
- 本地禁止执行 `pnpm docs:build`（曾导致内存耗尽、系统死机）；构建统一走 CI，CI 已配置 8 GB 堆。
- `gen-index.cjs` 会覆盖 15 个一级分类的 `index.md`；运行前确认 `categories` 列表与实际目录一致。
- `.script` 打包产物 `code-note-语雀导入.zip` 默认写在仓库根，上传语雀后手动删除；当前未加入 `.gitignore`。

## 协作注意事项

- 笔记正文与技术表述一律**中文**，改动应贴合既有风格，避免把作者连贯的笔记改写成机械化的模板文本。
- 大量笔记仍是旧的 `<font>`/`> Notes:` 风格。除非任务明确要求，**不要做大范围批量风格改写**；顺手修改时保持单篇内部风格一致。
- 内容仓库以“可长期复习”为目标，技术结论、表格与代码示例的准确性优先于排版。
