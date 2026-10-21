# MooN Note Style — 风格速查手册

本文件是 [SKILL.md](SKILL.md) 的配套参考，收录完整的类型/语义清单与可复制示例。写作时按需查阅。

## 一、Obsidian Callout 类型语义表

新写笔记的提示块统一用下列类型。**内容只有一两句时，直接把内容写在类型后面，不换行；内容较多、需要「标题 + 正文」，或正文包含列表/多段时，才换行。**

| 类型 | 语义 | 典型用途 |
| :---: | --- | --- |
| `note` | 补充说明 | 对正文的额外解释、旁注 |
| `info` | 提示 / 前提 | 前置条件、背景信息、"注意事项" |
| `tip` | 技巧 / 建议 | 最佳实践、经验、推荐做法 |
| `todo` | 待办 | 待整理、待完善的占位 |
| `abstract` | 摘要 / 总览 | 章节 TLDR、要点汇总 |
| `question` | 疑问 / FAQ | 常见问题、待解答疑点 |
| `quote` | 引述 / 出处 | 引用官方说法、注明来源 |
| `example` | 示例 | 举例说明 |
| `success` | 成功 / 完成 | 正确做法、验证通过 |
| `warning` | 警告 / 注意 | 易错、需谨慎的操作 |
| `failure` | 失败 / 缺失 | 错误做法、反例 |
| `danger` | 危险 / 错误 | 高风险操作、严重错误 |
| `bug` | 缺陷 | 已知 bug、坑点 |

示例：

```markdown
> [!tip] 国内建议直接通过 NPM 方式安装

> [!info] 注意事项（中文乱码问题）：
> 需要在启动参数中追加 `-Dfile.encoding=UTF-8`。

> [!warning] <span style="color: red;">**同样的警告，不到万不得已的时候不要这么做。**</span>

> [!note] 这是标题
> - 第一条内容
> - 第二条内容
```

> [!note] 旧笔记兼容
> 仓库早期笔记使用 `> Notes:` / `> Tips:` / `> 注：` / `> 注意：` 这类简单引用块。**改写这类整篇旧风格笔记时，保持整篇一致即可**，不要在一篇里新旧混用；除非目标就是把整篇统一升级为 callout。

## 二、彩色强调与下划线完整清单

所有文字样式一律用 `<span style="...">` 实现，按语义择用。**红色最重，仅用于最关键处；整体不要滥用彩色。**

> [!danger] 禁用遗留标签
> `<font>`（如 `<font color=red>`）和 `<u>` 是早期笔记遗留的旧写法，**已废弃**。新写与改写笔记一律用下表的 `<span style="...">` 写法：`<font color=red>` → `<span style="color: red;">`；`<u>` → `<span style="text-decoration: underline;">`。

| 写法 | 效果 | 语义 |
| --- | --- | --- |
| `**加粗**` | 粗体 | 一般重点 |
| `*斜体*` | 斜体 | 旁注、补充说明（如 `*注：……*`） |
| `` `代码` `` | 行内代码 | 类名/方法/关键字/配置/路径/命令 |
| `<span style="color: red;">**红字**</span>` | 粗体红字 | 最高优先级警示、必知要点、易错点 |
| `<span style="color: purple;">**紫字**</span>` | 粗体紫字 | 重要结论、关键定义、"注意/总结/值得注意" |
| `<span style="color: violet;">**粉字**</span>` | 粗体粉字 | 次一级需要留意的点 |
| `<span style="text-decoration: underline;">**下划线**</span>` | 下划线 | 需要划重点的短语 |
| `<span style="text-decoration: underline; text-decoration-style: dashed;">**虚下划线**</span>` | 虚下划线 | 弱化的标注 |
| `<span style="text-decoration: underline; text-decoration-style: dotted;">**点下划线**</span>` | 点下划线 | 弱化的标注 |
| `<span style="text-decoration: underline; text-decoration-style: double;">**双下划线**</span>` | 双下划线 | 强调的标注 |
| `<span style="text-decoration: underline; text-decoration-style: wavy;">**波浪下划线**</span>` | 波浪下划线 | 强调的标注 |

实际用法示例：

```markdown
实际开发中，理想状态应该是：<span style="color: red;">**在编译时不依赖，在运行时才依赖**</span>

<span style="color: purple;">**注意：只有 String 类型才能直接赋值创建对象**</span>

<span style="color: violet;">**值得注意：局部注册的组件在其子组件中不可用**</span>。
```

## 三、换行、中英文空格与标点细则

- **换行一律用空行**：段落之间空一行；<span style="color: red;">**不用行尾两个空格的硬换行**</span>
- **引用块 / callout 内多行或多段**：用一个只含 `>` 的空行分隔，如：

```markdown
> [!note] 比较链接 `.vscode` 与 `.vscode\extensions` 的区别：
>
> - **直接链接整个 `.vscode`（推荐）**
> - 单独链接 `.vscode\extensions`
```

- 中文与**英文单词**之间加半角空格：`使用 Executor 框架`
- 中文与**阿拉伯数字**之间加半角空格：`JDK 1.5`、`分为以下 4 个部分`
- 数字与英文单位紧贴：`8081 端口`、`400 行`、`30%`（百分号紧贴）
- 行内代码与中文之间**不强制**加空格，但整篇保持一致；代码/路径/命令内部一律不加空格
- 中文句子用全角标点 `。，、：；？！（）「」《》`
- 括注：中文语境用全角 `（）`，如 `控制反转（IoC）`；代码/英文语境用半角 `()`
- 跨笔记/章节引用用书名号 `《》`：`详见《ThreadPoolExecutor》章节`

## 四、文件命名与目录约定

- 文件名格式：`主题-子主题.md`，用连字符分段
  - `Java基础-集合.md`、`Java扩展-设计模式.md`
  - `MySQL-索引.md`、`Spring-Boot-部署运维篇.md`
  - `并发编程-线程池.md`
- 概述类用 `Overview` 或 `xxx概述`；索引类用 `README`（如 `面试-README.md`）
- 按主题归入对应顶层目录：`Java/`、`JVM/`、`并发编程/`、`后端框架/Spring/`、`分布式微服务/SpringBoot/`、`Database/MySQL/` 等
- 图片放在**笔记同级的 `images/` 子目录**，引用写 `![](images/文件名.png)`
- 图片命名沿用现有习惯：纯数字 ID（`458725421230465.jpg`）或 `时间戳_序号`（`20220113152744323_17038.png`）；有图注时 `![生成war包](images/xxx.png)`

## 五、完整片段示例（可参照仿写）

一段典型的「概念 → 好处 → 组成 → 状态说明 + 引用」结构：

```markdown
## 线程池简介

### 线程池的概念

线程池是 JDK 1.5 后的新特性，是用来创建和管理线程对象的容器。

线程池主要特点是：**线程复用、控制最大并发数、管理线程**。

**使用线程池的好处**：

- 降低资源消耗：重用存在的线程，避免频繁创建和销毁线程对象带来的系统开销。
- 提高响应速度：任务到达时不需要等待线程创建就能立即执行。

### 线程池的组成

一般的线程池主要分为以下 4 个组成部分：

1. 线程池管理器：用于创建并管理线程池
2. 工作线程：线程池中执行具体任务的线程
3. 任务接口：每个任务必须实现的接口。*注：只有实现该接口，任务才能被线程池调度。*
4. 任务队列：用于存放待处理的任务，提供一种缓冲机制。

### 线程池的状态

- `RUNNING`：接受新任务，处理等待队列中的任务。
- `SHUTDOWN`：不接受新任务，但会继续处理等待队列中的任务。

> [!note] 此状态定义在 `ThreadPoolExecutor` 类中，详见后面《ThreadPoolExecutor》章节。
```

一段带表格速查 + 代码块 + callout 的结构：

```markdown
### 访问控制关键字

以下是 Java 访问控制关键字汇总表。

|    关键字    |   说明   |
| :---------: | -------- |
|  `private`  | 私有的   |
| `protected` | 受保护的 |
|  `public`   | 公共的   |

​```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World"); // 输出 Hello World
    }
}
​```

> [!tip] 如果代码块 `{}` 内只有一行代码，可以省略花括号。
```
