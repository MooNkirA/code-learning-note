## VitePress 简介

### 概念

VitePress 是基于 Vite 与 Vue 3 的静态站点生成器（Static Site Generator，SSG），由 Vue 官方团队维护，是 VuePress 的下一代继任者。它把 Markdown 文件编译为静态 HTML 页面，默认主题开箱即用，非常适合搭建文档站、博客、组件库文档等以内容为主的网站。

VitePress 的工作流程是：**Markdown 源文件 → Vite 构建 → 静态 HTML**。开发模式下由 Vite 提供毫秒级热更新（HMR），构建时则预渲染为纯静态页面，可直接部署到 GitHub Pages、Netlify、Vercel 等任意静态托管平台。

### 特点

- 极快的冷启动与热更新：基于 Vite，开发体验接近原生 ESM
- 开箱即用的默认主题：导航、侧边栏、搜索、大纲、暗色模式一应俱全
- 构建时预渲染：SEO 友好，首屏即完整 HTML，无需客户端渲染
- 以 Markdown 为中心：支持 Frontmatter、代码高亮、自定义容器、Mermaid 等扩展
- 高度可定制：`.vitepress/theme` 可以深度定制主题与布局
- 生态插件化：基于 markdown-it 与 Vite 插件体系，可按需扩展

> [!note]
> 本仓库已从 docsify 迁移到 VitePress，在线地址为 <https://moonkira.github.io/>。本篇笔记结合仓库实际配置编写，迁移过程可参考《docsify》笔记了解迁移前的形态。

### 与 docsify 的区别

| 对比项 | VitePress | docsify |
| :---: | :---: | :---: |
| 渲染方式 | 构建时预渲染静态 HTML | 运行时解析 Markdown，动态渲染 |
| 首屏速度 | 快（纯静态页面） | 一般（依赖客户端解析） |
| SEO | 友好 | 较弱 |
| 开发模式 | Vite HMR，秒级热更新 | 刷新页面加载 |
| 配置方式 | `config.ts` 单文件配置 | `index.html` 内嵌 `window.$docsify` |
| 插件体系 | markdown-it 插件 + Vite 插件 | docsify 自定义插件 |
| 主题定制 | 完整 Vue 组件级定制 | 有限 |

## 快速开始

### 环境准备

VitePress 要求 Node.js 版本 18 及以上（本仓库使用 Node 22）。包管理器本仓库使用 pnpm 11，也可使用 npm 或 yarn。

```bash
node -v
pnpm -v
```

### 初始化项目

在项目根目录安装 VitePress 依赖：

```bash
pnpm add -D vitepress
```

官方还提供了交互式初始化向导，按提示选择即可：

```bash
npx vitepress init
```

> [!tip] 本仓库最初是用向导初始化后逐步改造的：新增了 `sidebar.ts` 自动生成侧边栏、`wikilink.ts` 解析 Obsidian wiki 链接、`html-guard.ts` 恢复彩色标签等定制能力，详见下文《进阶》章节。

### 目录结构

VitePress 的默认约定是把文档放在 `docs/` 目录，此时 `docs/` 就是内容根目录（srcDir）。**但本仓库没有使用 `docs/` 子目录，而是把仓库根目录直接作为 srcDir**——这样 Markdown 笔记就地存放，无需迁移目录结构。

```
Workspace/
├── .vitepress/          # VitePress 配置目录（config.ts、主题、插件、脚本）
│   ├── config.ts        # 主配置文件
│   ├── sidebar.ts       # 侧边栏自动生成器（自定义）
│   ├── wikilink.ts      # Obsidian wiki 链接解析插件（自定义）
│   ├── html-guard.ts    # 白名单标签恢复插件（自定义）
│   ├── fix-img-paths.cjs  # 图片路径修复脚本
│   ├── clean-broken-images.cjs  # 失效图片清理脚本
├── .script/             # Windows 本地辅助脚本（vitepress-本地开发预览.bat、vitepress-清理构建缓存.bat）
├── index.md             # 站点首页（home layout）
├── Java/                # 各主题笔记目录（一级分类）
├── 其他/
│   └── tools/           # 工具笔记目录
└── package.json         # 依赖与脚本
```

srcDir 的判定规则：`vitepress dev` 不带路径参数运行时，默认以配置文件所在目录（`.vitepress/`）的上一级作为 srcDir。本仓库 `config.ts` 中同样显式计算了一次，保证脚本路径一致：

```ts
// .vitepress/config.ts
import { dirname, resolve } from 'node:path'

const __dirname = dirname(fileURLToPath(import.meta.url))
// srcDir 即项目根目录（VitePress 默认以 config 文件所在目录的上一级为 srcDir）
const srcDir = resolve(__dirname, '..')
```

### 常用命令

package.json 中配置了两个核心脚本：

```json
{
  "scripts": {
    "docs:dev": "vitepress dev",
    "docs:build": "vitepress build"
  }
}
```

| 命令 | 作用 | 默认端口 |
| :---: | :--- | :---: |
| `pnpm docs:dev` | 本地开发预览，支持热更新 | 5173 |
| `pnpm docs:build` | 构建静态站点到 `.vitepress/dist/`（仅 CI 使用，本地禁止执行） | - |

> [!warning] 构建 OOM 问题
> 笔记数量多时（本仓库 400+ 篇），全量 build 在默认 Node 堆下会触发 heap out of memory（exit 134）。CI 已配置 `NODE_OPTIONS=--max-old-space-size=8192`；<span style="color: red;">**本仓库禁止在本地执行构建**</span>，本地验证只用 `pnpm docs:dev`。

### 页面与路由

VitePress 采用**文件即路由**：srcDir 下的每个 `.md` 文件对应一个页面，目录结构即 URL 结构。例如：

```
/Java/Java基础-集合.md  →  /Java/Java基础-集合
/其他/tools/VitePress.md  →  /其他/tools/VitePress
```

`config.ts` 中开启了 `cleanUrls: true`，生成的 URL 不带 `.html` 后缀。

页面之间支持三种链接方式：

- 相对链接：`[集合笔记](../Java/Java基础-集合.md)`，VitePress 会自动解析为最终 URL
- 绝对链接：`[集合笔记](/Java/Java基础-集合)`，以 `/` 开头指向 srcDir 根
- 本仓库特色：`[[Java基础-集合|Java 集合]]` Obsidian wiki 链接，由自定义插件在构建期转换

## 配置文件 config.ts

`config.ts` 是 VitePress 的唯一入口配置，导出 `defineConfig` 配置对象。本仓库的完整配置分模块说明如下。

### 站点信息

```ts
import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'MooN Code Note',        // 站点标题（浏览器标签页）
  description: '个人编程笔记',      // 站点描述（SEO meta）
  lang: 'zh-CN',                  // 页面语言
  base: '/',                      // 部署基础路径，Github Pages 用户站点为根路径
})
```

> [!info] base 路径说明
> 部署在 GitHub Pages 用户站点（`用户名.github.io`）时 `base` 为 `/`；如果是项目站点（`用户名.github.io/仓库名`），必须设置为 `/仓库名/`，否则静态资源全部 404。

### 导航 nav

顶部导航栏配置在 `themeConfig.nav` 中：

```ts
themeConfig: {
  nav: [
    { text: '首页', link: '/' },
    { text: '语雀', link: 'https://www.yuque.com/moonkira/code-note' },
    { text: '编程资源分享', link: '/其他/编程资源分享' },
    { text: 'Github', link: 'https://github.com/MooNkirA/MooNkirA.github.io' },
  ],
}
```

### 侧边栏 sidebar

本仓库没有手写 `_sidebar.md`（docsify 时代的做法），而是在 `config.ts` 中调用自定义的 `generateSidebar(srcDir)` 自动扫描目录树生成：

```ts
sidebar: generateSidebar(srcDir),
```

`sidebar.ts` 的核心逻辑：

- 定义 15 个一级分类目录（`AI`、`Java`、`后端框架`、`其他` 等），与 README 索引分区一一对应
- 递归扫描每个分类目录：一级目录下的 `.md` 文件作为顶层侧边栏项，子目录作为可折叠分组
- 使用自然排序 `localeCompare(..., { numeric: true })`，让 `10` 排在 `2` 后面
- 排除 `.obsidian`、`.trae`、`images`、`node_modules` 等非文档目录
- 文件名中的 `-`、`_` 自动替换为空格作为显示标题

```ts
// .vitepress/sidebar.ts（核心片段）
function buildSidebarTree(dir: string, basePath: string): SidebarItem[] {
  // 扫描目录：目录 → 可折叠分组，.md 文件 → 链接项
  // 子目录排序后递归构建，非空才加入分组
}
```

> [!tip] 采用自动生成后，新增笔记无需手动维护侧边栏，构建时自动出现在对应分类下。这是与 docsify 时代最明显的体验提升。

### 搜索 search

使用 VitePress 内置的本地全文搜索（基于 Minisearch），并配置了中文界面文案：

```ts
search: {
  provider: 'local',
  options: {
    translations: {
      button: { buttonText: '搜索', buttonAriaLabel: '搜索文档' },
      modal: {
        displayDetails: '显示详细列表',
        resetButtonTitle: '清除查询',
        backButtonTitle: '关闭搜索',
        noResultsText: '未找到结果',
        footer: { selectText: '选择', navigateText: '切换' },
      },
    },
  },
},
```

### 大纲与页脚

```ts
// 右侧大纲目录，展示 H2 ~ H4 层级
outline: {
  level: [2, 3, 4],
  label: '本页目录',
},

// 上一页 / 下一页
docFooter: { prev: '上一页', next: '下一页' },

// 页脚
footer: {
  message: 'I know where dry desert ends, green grass grows · MooN',
},

// 返回顶部按钮
returnToTopLabel: '返回顶部',

// 编辑链接（跳转到 GitHub 在线编辑）
editLink: {
  pattern: 'https://github.com/MooNkirA/MooNkirA.github.io/edit/main/:path',
  text: '在 GitHub 上编辑此页',
},
```

### 构建与排除规则

```ts
outDir: '.vitepress/dist',   // 构建输出目录
cleanUrls: true,             // URL 去掉 .html
ignoreDeadLinks: true,       // 忽略死链（旧笔记中有一些失效的图片/链接）

// 排除非文档文件与目录
srcExclude: [
  '**/images/**', '**/attachments/**', '**/resources/**',
  'AGENTS.md', '**/.obsidian/**', '**/.trae/**', '**/.claude/**',
],
```

## Frontmatter 配置

Frontmatter 是 `.md` 文件开头用 `---` 包裹的 YAML 元数据，用于控制单页的标题、布局、侧边栏等行为。

### 常用字段

| 字段 | 作用 |
| :---: | :--- |
| `title` | 页面标题，覆盖默认的文件名标题 |
| `description` | 页面描述，用于 SEO |
| `layout` | 页面布局，`doc`（默认）、`home`（首页）、`page`（全宽） |
| `sidebar` | 是否显示侧边栏（`false` 隐藏） |
| `outline` | 本页大纲层级，如 `[2, 3]` |
| `prev` / `next` | 自定义上一页 / 下一页链接 |
| `editLink` | 本页是否显示编辑链接 |

### 首页 layout: home

站点首页 `index.md` 使用 `home` 布局，配置 hero 区与 feature 卡片：

```yaml
---
layout: home

hero:
  name: MooNkirA Code Note
  text: 个人编程笔记
  tagline: AI · 全栈 · 分布式微服务 · 数据库 · DevOps
  actions:
    - theme: brand
      text: 开始浏览
      link: /AI/
    - theme: alt
      text: 基础入门
      link: /Java/

features:
  - title: AI 辅助开发
    details: AI 编程工具、Claude Code、Vibe Coding、Agent Skills 与 AI 辅助开发实践
    link: /AI/
    icon: 🤖
  - title: 其他资源
    details: 开发软件、编程工具、系统软件与编程资源分享
    link: /其他/
    icon: 🛠️
---
```

> [!note] 本仓库的大部分笔记没有 Frontmatter，仅首页与少量总览类文档使用。VitePress 会自动从标题推导页面标题，无 Frontmatter 也能正常渲染。

## Markdown 扩展

VitePress 基于 markdown-it，在标准 Markdown 之上扩展了大量语法。

### 代码块与行号

````markdown
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World"); // 输出 Hello World
    }
}
```
````

代码块支持指定语言高亮、标题栏（` ```java title="文件名" `）与行高亮（`{1,3-5}`）。本仓库 `config.ts` 中 `lineNumbers: false` 关闭了行号显示。

### 自定义容器（callout）

VitePress 支持 `::: 类型` 语法与 GitHub 风格 `> [!类型]` 两种容器写法。本仓库笔记遵循 Obsidian callout 风格，统一使用后者：

```markdown
> [!warning] 构建 OOM 问题
> CI 已配置 8 GB 堆；本仓库禁止本地执行全量构建，本地验证只用 `docs:dev`。

> [!tip] 常用 callout 类型
> - `note`：补充说明
> - `tip`：技巧建议
> - `info`：提示前提
> - `warning`：警告易错
> - `danger`：危险错误
```

### 表格

```markdown
| 命令 | 作用 | 默认端口 |
| :---: | :--- | :---: |
| `docs:dev` | 本地开发 | 5173 |
```

### 其他扩展语法

- 任务列表：`- [x] 已完成`、`- [ ] 未完成`
- 脚注：`文字[^1]`，文末 `[^1]: 注释内容`
- Emoji：`:rocket:` 或直接粘贴 Emoji
- 数学公式：需要额外安装 `markdown-it-mathjax3`，本仓库未启用
- 代码高亮扩展：`vue`、`ts` 等语言开箱即用

## 主题定制

默认主题的样式变量可通过 `.vitepress/theme/custom.css` 覆盖，然后在 `config.ts` 中引入：

```ts
import DefaultTheme from 'vitepress/theme'

export default {
  extends: DefaultTheme,
}
```

也可以在 `.vitepress/theme/index.ts` 中注册自定义 Vue 组件、改造布局。本仓库目前未深度定制主题样式，依赖默认主题的暗色模式与搜索能力。

## 部署到 GitHub Pages

### GitHub Actions 自动部署

本仓库通过 `.github/workflows/deploy.yml` 实现 push 到 `main` 分支后自动构建部署：

```yaml
name: Deploy VitePress to GitHub Pages

on:
  push:
    branches: [main]
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: pages
  cancel-in-progress: false

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Setup pnpm
        uses: pnpm/action-setup@v4
        with:
          version: 11

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: pnpm

      - name: Install dependencies
        run: pnpm install --frozen-lockfile

      - name: Build
        run: pnpm docs:build
        env:
          NODE_OPTIONS: --max-old-space-size=8192

      - name: Setup Pages
        uses: actions/configure-pages@v5

      - name: Upload artifact
        uses: actions/upload-pages-artifact@v3
        with:
          path: .vitepress/dist

  deploy:
    needs: build
    runs-on: ubuntu-latest
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}
    steps:
      - name: Deploy to GitHub Pages
        id: deployment
        uses: actions/deploy-pages@v4
```

> [!info] 部署要点
> - GitHub Pages 来源必须设置为 **GitHub Actions**，而不是分支部署
> - CI 中同样配置了 `NODE_OPTIONS=--max-old-space-size=8192`，避免 400+ 篇笔记全量构建 OOM
> - 支持在 Actions 页面手动 `workflow_dispatch` 触发

### .nojekyll 文件

仓库根目录的 `.nojekyll` 空文件用于关闭 GitHub Pages 的 Jekyll 处理，保证 `_` 开头文件与点目录（如 `.vitepress`）可以原样发布。

## 进阶：Markdown 插件开发

VitePress 的 Markdown 解析基于 markdown-it，可以通过 markdown-it 插件体系扩展语法。本仓库为此定制了三个插件，这是从 docsify 迁移过程中最有价值的部分。

### 插件机制

markdown-it 提供两类扩展点：

- **Ruler 链**：通过 `md.core.ruler.before/after` 在解析阶段对 token 流做处理
- **Renderer 覆写**：通过 `md.renderer.rules` 覆写指定 token 的渲染函数

本仓库的插件主要利用 `core.ruler` 在解析前改写源文本，或在渲染后处理 HTML。

### wikilink 插件：解析 Obsidian wiki 链接

仓库 400+ 篇笔记大量使用 Obsidian 双链语法 `[[文件名|别名]]`，而 VitePress 原生不支持。`wikilink.ts` 在构建期扫描所有 `.md` 文件建立「文件名 → URL」映射，把双链替换为标准 Markdown 链接：

```ts
// .vitepress/wikilink.ts（核心片段）
const regex = /\[\[([^\#\|\]]+)(?:#([^\|\]]+))?(?:\|([^\]]+))?\]\]/g

md.core.ruler.before('block', 'wikilinks', (state) => {
  state.src = state.src.replace(regex, (match, fileName, anchor, alias) => {
    const target = fileMap[fileName.trim()]
    if (!target) return match // 找不到文件，保留原文
    const display = (alias || fileName).trim()
    return `[${display}](${target}${anchor ? '#' + anchor.trim() : ''})`
  })
})
```

支持四种形态：

| 写法 | 转换结果 |
| :---: | :--- |
| `[[文件名]]` | `[文件名](/路径/文件名)` |
| `[[文件名\|别名]]` | `[别名](/路径/文件名)` |
| `[[文件名#章节]]` | `[文件名](/路径/文件名#章节)` |
| `[[文件名#章节\|别名]]` | `[别名](/路径/文件名#章节)` |

> [!warning] 文件名必须全仓库唯一
> `wikilink.ts` 以「不含扩展名的文件名」为映射键，同名文件在构建期**后者覆盖前者**，会导致 wiki 链接跳错。新增笔记前务必确认没有同名文件。当前仓库 413 篇零重名。

### html-guard 插件：转义与白名单恢复

VitePress 默认 `markdown.html: false` 会把笔记中的原始 HTML 全部转义，导致 `<span style="color: red;">` 这类彩色样式失效（裸 HTML 还可能引发 Vue 编译错误）。`html-guard.ts` 的策略是：**先全部转义，渲染后再恢复白名单标签**：

```ts
// .vitepress/html-guard.ts（核心片段）
const ALLOWED_TAGS = new Set([
  'span', 'font', 'b', 'i', 'em', 'strong', 'u', 'small', 'sub', 'sup', 'mark',
  'br', 'wbr',
  'details', 'summary', 'abbr', 'progress',
])

// 渲染后把 &lt;span ...&gt; 恢复为 <span ...>
function restoreAllowedTags(html: string): string {
  // 1. 先抠出 <pre> 代码块保护，避免误替换
  // 2. 再抠出 <code> 行内代码保护
  // 3. 正则匹配 &lt;标签名 属性&gt;，白名单内的恢复为真实 HTML
  // 4. 还原被保护的代码块与行内代码
}
```

该插件还承担另一项职责：在解析前把 `{{` 和 `}}` 转义为 `&#123;`，避免代码块中的 JSX 插值被 Vue 编译器误判为模板语法。

> [!danger] center 标签不能放行
> 实测 `MySQL-索引.md` 中的 `<center>` 居中标题在构建后内容整体丢失——Vue 编译器不识别 `center` 标签，会按自定义组件解析导致 SSR 产物为空。需要居中请用 `<span style="display:block;text-align:center;">`。

### 插件的挂载方式

在 `config.ts` 的 `markdown.config` 中挂载：

```ts
markdown: {
  lineNumbers: false,
  html: false, // 禁用原始 HTML，交给 html-guard 处理白名单
  config(md) {
    md.use(htmlGuardPlugin)
    md.use(wikilinkPlugin, { srcDir })
  },
},
```

## 进阶：集成 Mermaid 流程图

### 安装插件

本仓库通过 `vitepress-plugin-mermaid` 插件支持 Mermaid 图表：

```bash
pnpm add -D vitepress-plugin-mermaid
```

`config.ts` 中用 `withMermaid` 包裹配置：

```ts
import { withMermaid } from 'vitepress-plugin-mermaid'

export default withMermaid(
  defineConfig({ ... }),
)
```

之后即可在笔记中直接书写 Mermaid 代码块：

````markdown
```mermaid
flowchart LR
  A[Markdown 源文件] --> B[VitePress 构建]
  B --> C[静态 HTML]
  C --> D[GitHub Pages]
```
````

### optimizeDeps 预构建的坑

mermaid 依赖的 CJS 模块在 dev 模式下会出现 ESM 互操作问题（浏览器报 `does not provide an export named 'default'`）。原因是 Vite 按字符串精确匹配预构建条目，mermaid 内部实际导入的是 `fastdom/extensions/fastdom-promised.js`（带 `.js` 后缀），如果不带后缀则不会命中预构建，退化为直接下发原始 CJS 文件。

因此 `config.ts` 的 `vite.optimizeDeps.include` 中同时配置了带后缀与不带后缀的条目：

```ts
vite: {
  optimizeDeps: {
    include: [
      'dayjs',
      '@braintree/sanitize-url',
      'fastdom',
      'fastdom/extensions/fastdom-promised',
      'fastdom/extensions/fastdom-promised.js',
      'debug',
      'cytoscape',
      'cytoscape-cose-bilkent',
    ],
  },
},
```

> [!warning]
> <span style="color: red;">**必须写与 mermaid 源码中完全一致的导入写法（带 `.js` 后缀）**</span>，Vite 按精确字符串匹配，多一个少一个都不会命中预构建。

## 进阶：工程化维护脚本

除构建链路外，`.vitepress/` 下还沉淀了一批维护脚本：

| 脚本 | 作用 |
| :---: | :--- |
| `gen-index.cjs` | 为每个一级分类目录生成 `index.md` 入口文件 |
| `fix-img-paths.cjs` | 批量修复缺少 `images/` 前缀的图片引用 |
| `clean-broken-images.cjs` | 扫描并清理指向不存在文件的图片引用，替换为占位文本 |
| `../.script/vitepress-*.bat` | Windows 本地开发 / 清理脚本 |

图片修复脚本的核心逻辑：扫描所有 `.md` 文件，对每个图片引用先检查原路径是否存在，不存在时到同级的 `images/` 子目录按文件名查找，找到则自动补全路径。这也是仓库图片统一使用 `![](images/xxx.png)` 写法的保证。

## 常见问题与注意事项

- **构建 OOM**：笔记数量大时全量 build 会内存溢出（exit 134）；CI 已配置 8 GB 堆。<span style="color: red;">**本仓库禁止在本地执行构建**</span>，本地验证只用 `docs:dev`。
- **文件名唯一性**：wikilink 插件以文件名为键，<span style="color: red;">**新增笔记务必先确认全仓库无同名文件**</span>，否则构建期后者覆盖前者，wiki 链接全部跳错。
- **裸 HTML 与 Vue 编译**：`markdown.html: false` 已禁用原始 HTML。笔记中的 `<span style>` 等白名单标签会被 html-guard 恢复，块级标签（`<div>`、`<center>` 等）不会放行，应改写成标准 Markdown 语法。
- **换行规范**：仓库笔记遵循「换行靠空行」约定（`strictLineBreaks: true`），禁止行尾两空格硬换行，VitePress 渲染时行为与 Obsidian 保持一致。
- **本地开发建议**：使用 `pnpm docs:dev` 边写边预览；改动了 `.vitepress/` 下的插件或配置需要重启 dev 服务，改 Markdown 内容则热更新即时生效。
- **快速定位构建问题**：构建报错时先看是否与「新增同名文件」「裸 HTML 标签」「`{{ }}` 插值」三类问题相关，这是本仓库迁移过程中踩坑最集中的三个方向。

## 参考链接

- VitePress 官方文档：[vitepress.dev](https://vitepress.dev)
- VitePress 官方中文文档：[vitepress.dev/zh/](https://vitepress.dev/zh/)
- 本项目在线地址：[MooNkirA Code Note](https://moonkira.github.io/)
- 相关笔记：《docsify》文档类型网站搭建工具、《Mermaid》绘图语法
