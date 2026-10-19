# vitepress配置指引

本文档是 `MooNkirA.github.io` 项目（VitePress 文档站）的配置说明与维护教程，覆盖站点配置、顶部导航、侧边栏、首页、索引文件维护与部署验证。**日后的配置变更与维护以本文档为准。**

> [!warning] 验证方式红线
> 任何配置变更的测试与验证，一律通过 `pnpm docs:dev` 本地开发服务器进行，<span style="color: red;">**严禁本地执行 `docs:build`**</span>。

---

## 项目概览

本仓库同时是 Obsidian 笔记库与 VitePress 静态文档站，约 413 篇中文技术笔记，部署在 GitHub Pages（https://moonkira.github.io/）。

### 技术栈

| 依赖 | 版本 | 用途 |
|:---|:---|:---|
| VitePress | ^1.6.4 | 静态文档站框架 |
| vitepress-plugin-mermaid | ^2.0.17 | Mermaid 图表渲染 |
| pnpm | - | 包管理器 |
| Node.js | - | 运行环境（全量构建需 8GB 堆内存） |

### 关键文件

| 文件 | 定位 |
|:---|:---|
| `.vitepress/config.ts` | 站点主配置（导航、社交链接、搜索、页脚等） |
| `.vitepress/sidebar.ts` | 侧边栏自动生成器（扫描目录树） |
| `.vitepress/wikilink.ts` | 构建期把 `[[wiki 链接]]` 转为标准链接 |
| `.vitepress/html-guard.ts` | 恢复白名单 `<span>` 彩色样式标签 |
| `.vitepress/theme/` | 自定义主题（Layout + 主题色下拉切换器 + custom.css） |
| `index.md` | 站点首页（home layout） |
| `README.md` | GitHub 仓库首页索引 |
| `vitepress配置指引.md` | 本文档，配置说明与维护教程 |

### 常用命令

```bash
pnpm docs:dev       # 本地开发预览（默认 http://localhost:5173）
# pnpm docs:build   # 仅 GitHub Actions 使用；本地禁止执行
```

<span style="color: purple;">**注意：本地验证只用 `docs:dev`；`docs:build` 仅供 GitHub Actions 使用，本地禁止执行。**</span>

---

## 站点配置（config.ts）

主配置位于 `.vitepress/config.ts`，用 `defineConfig` 包裹，整体结构如下：

### 站点信息

| 配置项 | 当前值 | 说明 |
|:---|:---|:---|
| `title` | MooN Code Note | 浏览器标签页标题 |
| `description` | 个人编程笔记 | SEO 描述 |
| `lang` | zh-CN | 站点语言 |
| `base` | / | 部署根路径 |

### 顶部导航（nav）

顶部导航参考 Vue 官网结构，采用**顶级项 + hover 下拉子菜单**模式。顶级项渲染为下拉按钮（鼠标划过展开），分类首页入口放在 `items` 第一项；「首页」是唯一的直接链接项。

<span style="color: red;">**重要限制：VitePress 1.6 中顶级项不要同时写 `link` 和 `items`**</span>——同时存在时组件会按链接渲染并丢弃 `items`，导致下拉不出现（本项目早期已踩坑）。需要"分类直达"时，把 `link` 放进 `items` 第一项即可。

当前导航结构如下：

| 顶级项 | 类型 | 子项 |
|:---|:---|:---|
| 首页 | 直接链接 | 无（访问首页时该项自动隐藏，其他页面显示） |
| AI 编程 | 下拉 | AI 总览 / AI 概览 / AI 编程工具 / Vibe Coding / Agent Skills / Claude Code 教程 / Python |
| 全栈开发 | 下拉 | Java 核心 / JavaWeb / JVM / 并发编程 / 后端框架 / 前端技术 |
| 分布式微服务 | 下拉 | 分类总览、核心框架（Spring Boot / Spring Cloud / Dubbo）、中间件（消息中间件 / 服务注册中心 / 配置中心 / 任务调度）、架构与治理（系统架构 / 分布式事务 / 分库分表 / 分布式文件系统 / 分布式链路追踪 / 微服务监控）、基础设施（Nginx / 认证与授权 / Elastic Stack） |
| 数据与运维 | 下拉 | 数据库 / DevOps / Linux |
| 项目实战 | 下拉 | 实战项目总览、大型实战（品优购 / 万信金融 / 学成在线）、更多项目（好客租房 / 品达通用权限系统 / 国际物流云商系统 / 红包雨场景案例 / 自用信息管理系统） |
| 更多 | 下拉 | 面试手册 / 编程资源分享 / 语雀笔记 |

**维护方法：**

新增一个顶级下拉项：

```ts
{
  text: '顶级项名称',
  items: [
    { text: '分类首页', link: '/对应目录/' },   // 分类入口作为第一项
    { text: '子项名称', link: '/路径' },
  ],
}
```

分组下拉（子项再分组，渲染为分组标题，如「分布式微服务」）：

```ts
{
  text: '顶级项名称',
  items: [
    {
      text: '分组标题',
      items: [
        { text: '子项名称', link: '/路径' },
      ],
    },
  ],
}
```

> [!tip] 维护要点
> 1. 站内链接统一写 `cleanUrls` 形式（不带 `.md` 后缀），如 `/AI/AI-基础`
> 2. 新增子项后确认目标 `.md` 文件真实存在，避免死链
> 3. 顶级项数量控制在 5 ~ 7 个，子项 4 ~ 8 个为宜，过多时用分组折叠
> 4. 顶级项只写 `text` + `items`，不写 `link`（见上方红色警示）

### 社交链接（socialLinks）

导航栏右侧的官方图标链接，与 Vue 官网一致：

```ts
socialLinks: [
  { icon: 'github', link: 'https://github.com/MooNkirA/MooNkirA.github.io' },
],
```

`icon` 支持 VitePress 内置图标（`github`、`x`、`discord` 等），新增时查 VitePress 文档确认图标名。

### 其他主题配置

| 配置项 | 当前值 | 说明 |
|:---|:---|:---|
| `sidebar` | `generateSidebar(srcDir)` | 侧边栏自动生成，见下一章 |
| `outline` | level [2, 3, 4] | 右侧大纲目录层级 |
| `search` | provider: local | 本地全文搜索（Minisearch） |
| `docFooter` | 上一页 / 下一页 | 页脚翻页文案 |
| `footer.message` | 自定义文案 | 站点页脚 |
| `editLink` | GitHub edit 链接 | 「在 GitHub 上编辑此页」 |

### Markdown 增强

```ts
markdown: {
  lineNumbers: false,
  html: false,     // 禁用原始 HTML，避免旧笔记裸 HTML 导致编译错误
  config(md) {
    md.use(htmlGuardPlugin)  // 恢复白名单 span/font 样式标签
    md.use(wikilinkPlugin, { srcDir })  // wiki 链接转标准链接
  },
},
```

### srcExclude 与 ignoreDeadLinks

```ts
srcExclude: ['**/images/**', '**/attachments/**', '**/resources/**',
  'AGENTS.md', '**/.obsidian/**', '**/.trae/**', '**/.claude/**'],
ignoreDeadLinks: true,  // 忽略旧笔记中的失效图片/链接
```

### vite 预构建（mermaid 兼容）

```ts
vite: {
  optimizeDeps: {
    include: ['dayjs', '@braintree/sanitize-url', 'fastdom', 'debug', 'cytoscape', 'cytoscape-cose-bilkent'],
  },
},
```

> [!warning] 不要动这个配置
> mermaid 的 dist 里是 `import ... from 'fastdom/extensions/fastdom-promised.js'`，Vite 按精确字符串匹配预构建条目。删除或改写这里会导致浏览器报 `does not provide an export named 'default'`。

---

## 侧边栏自动生成（sidebar.ts）

侧边栏**不需要手动维护**，由 `.vitepress/sidebar.ts` 扫描项目目录树自动生成：

- 一级分类目录 → 侧边栏分组
- `.md` 文件 → 分组内的链接项
- 目录内文件按自然排序（`10` 排在 `2` 后面）

### CATEGORIES 维护

```ts
const CATEGORIES = [
  'AI', 'Java', 'JavaWeb', 'JVM', '后端框架', 'Frontend', 'Database',
  'DevOps', '分布式微服务', '并发编程', 'Linux', 'Python', '面试手册',
  '项目资料', '其他',
]
```

<span style="color: purple;">**新增或重命名一个一级分类目录时，必须同步更新 `CATEGORIES` 数组**</span>，否则侧边栏不会出现该分类。

### 排除规则

以下目录不会进入侧边栏：`.obsidian`、`.trae`、`.claude`、`.vscode`、`.git`、`node_modules`、`.vitepress`、`resources`、`attachments`、`images`。

---

## 首页配置（index.md）

根目录 `index.md` 使用 VitePress home layout，frontmatter 分为 `hero` 与 `features` 两块：

```yaml
---
layout: home

hero:
  name: MooNkirA Code Note
  text: 个人编程笔记
  tagline: AI 辅助开发 · Java 全栈 · 分布式微服务 · 数据库与 DevOps，系统化沉淀的技术知识库
  actions:
    - theme: brand
      text: 开始浏览
      link: /AI/
    - theme: alt
      text: 基础入门
      link: /Java/

features:
  - title: AI 辅助开发
    details: ...
    link: /AI/
    icon: 🤖
---
```

| 字段 | 说明 |
|:---|:---|
| `hero.name` | 品牌名（渐变大字） |
| `hero.text` | 定位语（主标题） |
| `hero.tagline` | 一句话简介 |
| `hero.actions` | 按钮组，`theme` 取 `brand`（主色）/ `alt`（次要） |
| `features` | 功能卡片，`title` / `details` / `link` / `icon` |

**维护要点：** 新增笔记不需要动首页；只有新增一级分类时才需要补一张 features 卡片，并同步更新 `README.md` 与 `sidebar.ts` 的 `CATEGORIES`。

### 自定义主题样式（theme/）

需要修改默认主题视觉时，在 `.vitepress/theme/` 下扩展：

```
.vitepress/theme/
├── index.ts                    # 主题入口：extends 默认主题 + 挂载自定义 Layout
├── Layout.vue                  # 覆盖布局：插入 ThemeSwitcher + 首页项按路由显隐
├── custom.css                  # 主题色变量分组、导航布局重排与全局样式
└── components/
    └── ThemeSwitcher.vue       # 主题色切换器（下拉菜单，蓝为默认 + 绿 / 紫 / 青 / 橙）
```

- `index.ts`：`extends` VitePress 默认主题并挂载自定义 `Layout`
- `Layout.vue`：复用默认 `Layout`，用 `nav-bar-content-after` 插槽注入切换器；同时监听路由，访问首页时在 `<html>` 上加 `home-hide-nav-home` 类
- `ThemeSwitcher.vue`：下拉按钮形式，点击展开 5 个主题选项；**默认蓝色**（`:root` 变量即蓝色），选择写入 `localStorage`（键 `vp-theme-color`），刷新后保持；点击页面其他区域自动收起
- `custom.css`：按 `html[data-theme='xxx']` 分组定义品牌色变量（亮 / 暗各一组）；通过 flex `order` 把切换器重排到明暗切换（Appearance）旁边，GitHub 图标移至最右

**「首页」菜单项按路由显隐：** 访问首页时隐藏顶部导航的「首页」项（避免重复），访问其他页面时显示，便于返回首页。由 `Layout.vue` 的 `watch` 监听路由、切换 `<html>` 上的 `home-hide-nav-home` 类，配合 `custom.css` 中的选择器实现（桌面 `VPNavBarMenu` 与移动端 `VPNavScreenMenu` 两处）。

**主题色变量机制：** 品牌色全部走 VitePress 的 `--vp-c-brand-*` 变量，每套主题在亮色（`html[data-theme]`）与暗色（`html[data-theme].dark`）各定义一组；`--theme-hero-gradient` 控制首页标题的动态渐变，随主题切换。

**新增一套主题色：**

1. 在 `ThemeSwitcher.vue` 的 `THEMES` 数组中加一项（key / label / color）
2. 在 `custom.css` 中新增 `html[data-theme='新key']` 与 `html[data-theme='新key'].dark` 两组变量

> [!tip] 调试提示
> 样式选择器需匹配 VitePress 生成的结构（如 `.VPHomeHero .name`），覆盖不生效时用浏览器开发者工具确认实际类名。

---

## 索引文件维护

### 索引文件层级与定位

```
Workspace/
├── index.md                  # VitePress 站点首页（home layout，总入口）
├── README.md                 # GitHub 仓库首页（完整目录索引，给看代码的人）
│
├── Java/index.md             # 一级分类导航页
├── 后端框架/index.md          # 一级分类导航页
│   └── Spring/index.md       # 二级子目录导航页（仅内容多的子目录才有）
└── ...
```

| 文件 | 定位 | 受众 | 内容风格 |
|:---|:---|:---|:---|
| 根目录 index.md | VitePress 站点首页 | 网站访客 | home layout，features 卡片总览 |
| README.md | GitHub 仓库首页 | 直接浏览代码的人 | 完整的笔记目录索引 |
| 一级目录 index.md | 各技术分类的导航页 | 浏览网站的读者 | 按逻辑分组的笔记链接列表 |
| 二级子目录 index.md | 内容较多的子领域导航 | 深入该子领域的读者 | 更细粒度的分类导航 |

### 新增一篇笔记的维护步骤

**场景：** 在 `分布式微服务/SpringBoot/` 下新增一篇 `Spring-Boot-XXX.md`

**必做步骤：**

1. 笔记文件就位：放到对应目录，文件名符合 `主题-子主题.md` 规范
2. 更新二级子目录 `index.md`：在对应分组下添加链接
   ```markdown
   - [新笔记标题](新笔记文件名.md)
   ```
3. 更新一级目录 `index.md`：如果该子目录在 `分布式微服务/index.md` 中有对应分组，确认链接正确
4. 更新 `README.md`：在对应分类下添加一条链接
   ```markdown
   - [新笔记标题](分布式微服务/SpringBoot/新笔记文件名.md)
   ```

**无需操作：**

- 侧边栏会由 `sidebar.ts` 自动扫描生成，无需手动维护
- 根目录 `index.md` 的 features 卡片不需要每篇都加，只在新增大分类时调整
- 顶部导航只在新增一级分类或需要直达入口时才调整

### 新增二级子目录的决策

| 笔记数量 | 是否创建 index.md | 说明 |
|:---|:---|:---|
| < 5 篇 | ❌ 不创建 | 侧边栏导航已足够清晰 |
| 5 ~ 10 篇 | ⚠️ 可选 | 内容分类复杂时再创建 |
| ≥ 10 篇 | ✅ 必须创建 | 需要专门的导航页做分组 |

创建 `index.md` 的模板：

```markdown
# 子目录名称

一句话简介，说明这个子领域是做什么的。

## 分组一

- [笔记标题](笔记文件名.md)
- [笔记标题](笔记文件名.md)

## 分组二

- [笔记标题](笔记文件名.md)
```

### 命名与链接规范

**文件命名：**

- 主导模式：`主题-子主题.md`，如 `Java基础-IO编程.md`、`并发编程-线程池.md`
- <span style="color: red;">**文件名必须全仓库唯一**</span>：VitePress 的 wikilink 插件以文件名为键，重名会导致链接跳错
- 项目实战类允许序号风格：`Day07-商品录入模块.md`、`15-项目部署.md`

**链接格式：**

| 文件类型 | 链接格式 | 示例 |
|:---|:---|:---|
| index.md 内部 | 相对路径（带或不带 .md 均可） | `[标题](Spring-Boot-基础篇.md)` |
| README.md | 相对路径（带目录前缀） | `[标题](分布式微服务/SpringBoot/Spring-Boot-基础篇.md)` |
| 笔记正文之间互链 | Obsidian wiki 链接（VitePress 自动转换） | `[[Java基础-IO编程|IO 编程]]` |

**注意事项：**

- 不要在 `index.md` 中使用 Obsidian wiki 链接，用原生 Markdown 链接
- `README.md` 中的链接必须是完整相对路径，不能只用文件名
- 图片引用：`![](images/xxx.png)`，引用同级 `images/` 子目录

### 常见操作速查

**新增一篇笔记：**

- [ ] 放到对应目录，文件名唯一
- [ ] 更新所在目录的 `index.md`
- [ ] 更新 `README.md` 对应分区
- [ ] 侧边栏自动生成，无需操作

**新增一个二级子目录：**

- [ ] 创建子目录，放入笔记文件
- [ ] 笔记 ≥ 10 篇 → 创建 `index.md`
- [ ] 在父一级目录的 `index.md` 中确认分组正确
- [ ] 更新 `README.md`

**调整一篇笔记的分类：**

- [ ] 移动文件到新目录
- [ ] Obsidian 中移动会自动更新链接
- [ ] 命令行移动需手动更新所有引用
- [ ] 检查 `index.md` 和 `README.md` 中的链接路径

**删除一篇笔记：**

- [ ] 从对应 `index.md` 中移除链接
- [ ] 从 `README.md` 中移除链接
- [ ] 删除文件本身
- [ ] 清理未引用的图片（Obsidian 插件可自动完成）

### 已创建的二级子目录 index.md 清单

目前已创建二级子目录 `index.md` 的共 **9 个**：

| 子目录路径 | 笔记数 | 分组方式 |
|:---|:---|:---|
| `分布式微服务/SpringBoot/` | 21 篇 | 基础配置 / 数据持久化 / 核心功能 / 监控部署 / 进阶源码 |
| `分布式微服务/SpringCloud/` | 18 篇 | 概述 / Netflix / Alibaba / 核心组件 |
| `后端框架/Spring/` | 11 篇 | 核心功能 / 源码分析 |
| `后端框架/工具框架/` | 13 篇 | 日志工具 / 网络引擎 / 文档映射 / JSON / 其他 |
| `Database/MySQL/` | 13 篇 | 安装架构 / 基础语法 / 进阶特性 / 运维优化 |
| `项目资料/品优购/` | 24 篇 | 项目概述 / 后台管理 / 商品管理 / 搜索静态化 / 前台门户 / 支付高并发 / 附录 |
| `项目资料/万信金融/` | 20 篇 | 项目概述 / 环境用户 / 核心技术 / 业务模块 / 部署总结 |
| `项目资料/学成在线/` | 14 篇 | 项目概述 / CMS 内容 / 消息课程 / 微服务搜索 / 认证授权 |
| `其他/software/` | 13 篇 | 开发 IDE / 系统软件 / 效率工具 |

---

## 写作风格规范

笔记正文与配置文档的书写风格由仓库级 Skill 维护，动手前先读：

```text
.trae/skills/moon-note-style/SKILL.md
```

最容易被违反的几条约定：

| 约定 | 说明 |
|:---|:---|
| 正文从 `##` 起 | 不用 `#`（H1）当正文主标题，标题不手动编号 |
| 样式只用 `<span style="...">` | `<font>`、`<u>` 已废弃，改写时顺手替换 |
| 红色只用于最关键警示 | 重要结论用紫字，次要用粉字 |
| 新笔记用 Obsidian callout | 不用 `> Notes:` / `> Tips:` |
| 代码块必须标语言 | 注释写中文 |
| 中英文、中文与数字间加半角空格 | `线程池是 JDK 1.5 后的新特性` |
| 换行一律靠空行 | 禁止行尾两空格硬换行 |

---

## 验证与部署

### 本地验证（唯一方式）

配置变更后按以下流程验证：

```bash
pnpm docs:dev
```

浏览器打开 `http://localhost:5173`，检查：

- 顶部导航：hover 是否正常展开下拉、子项链接是否可达
- 右上角 GitHub 图标是否显示、点击可达
- 首页、侧边栏、搜索、页面渲染无报错

> [!warning] 禁止项
> 本地验证 <span style="color: red;">**严禁执行 `docs:build`**</span>。全量构建耗时长且需要 8GB 堆内存，仅 CI 部署流程使用。

### 部署

- 部署方式：GitHub Actions 自动部署（`.github/workflows/deploy.yml`）
- 触发条件：push 到 `main` 分支
- 流程：`pnpm install` → `pnpm docs:build`（已配 8GB 堆）→ 上传 `.vitepress/dist` → GitHub Pages 发布
- 也支持在 Actions 页面手动 `workflow_dispatch` 触发

---

## 常见问题

### 构建 OOM（exit 134）

本仓库禁止在本地执行全量构建；CI 已配置 `NODE_OPTIONS=--max-old-space-size=8192`，约 390 篇笔记的构建由 GitHub Actions 完成。本地只用 `pnpm docs:dev` 验证，不要通过调大内存重试。

### 链接跳错 / 页面 404

- 检查文件名是否全仓库唯一（wikilink 以文件名为键，重名覆盖）
- 站内链接用 `cleanUrls` 形式（无 `.md` 后缀），并确认目标文件存在
- 顶部导航或 index.md 改动后用 `docs:dev` 逐一点击验证

### mermaid 图表渲染报错

报 `does not provide an export named 'default'` 时，检查 `config.ts` 中 `vite.optimizeDeps.include` 是否包含 `fastdom/extensions/fastdom-promised.js`（必须带 `.js` 后缀的精确写法），不要擅自删改该配置。
