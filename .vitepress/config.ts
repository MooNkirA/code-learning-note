import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'
import { wikilinkPlugin } from './wikilink'
import { generateSidebar } from './sidebar'
import { htmlGuardPlugin } from './html-guard'
import { fileURLToPath } from 'node:url'
import { dirname, resolve } from 'node:path'

const __dirname = dirname(fileURLToPath(import.meta.url))
// srcDir 即项目根目录（VitePress 默认以 config 文件所在目录的上一级为 srcDir）
const srcDir = resolve(__dirname, '..')

export default withMermaid(
  defineConfig({
    // ===== 站点信息 =====
    title: 'MooN Code Note',
    description: '个人编程笔记 —— AI、分布式、全栈、数据库与 DevOps',
    lang: 'zh-CN',
    // 用户站点（MooNkirA.github.io），base 为根路径
    base: '/',

    // ===== 主题配置 =====
    themeConfig: {
      siteTitle: 'MooNkirA Code Note',

      // 顶部导航：顶级项 + hover 下拉子菜单（参考 Vue 官网导航结构）
      nav: [
        { text: '首页', link: '/' },
        {
          text: 'AI 编程',
          items: [
            { text: 'AI 总览', link: '/AI/' },
            {
              text: 'AI 基础',
              items: [
                { text: 'AI 概览', link: '/AI/AI-基础' },
                { text: 'AI 编程工具', link: '/AI/AI-编程工具' },
                { text: 'Vibe Coding', link: '/AI/Vibe-Coding' },
              ]
            },
            { text: 'Python', link: '/Python/' },
            {
              text: 'AI 技巧经验',
              items: [
                { text: 'Agent Skills', link: '/AI/AI-Assistance/Agent-Skills' },
              ]
            },
            {
              text: 'AI 编程工具',
              items: [
                { text: 'Claude Code 教程', link: '/AI/AI-Develop/Claude-Code-教程' },
                { text: 'OpenCode 教程', link: '/AI/AI-Develop/OpenCode' },
              ]
            }
          ],
        },
        {
          text: '全栈开发',
          items: [
            { text: 'Java 核心', link: '/Java/' },
            { text: 'JavaWeb', link: '/JavaWeb/' },
            { text: 'JVM', link: '/JVM/' },
            { text: '并发编程', link: '/并发编程/' },
            { text: '后端框架', link: '/后端框架/' },
            { text: '前端技术', link: '/Frontend/' },
          ],
        },
        {
          text: '分布式微服务',
          items: [
            { text: '分类总览', link: '/分布式微服务/' },
            {
              text: '核心框架',
              items: [
                { text: 'Spring Boot', link: '/分布式微服务/SpringBoot/' },
                { text: 'Spring Cloud', link: '/分布式微服务/SpringCloud/' },
                { text: 'Dubbo', link: '/分布式微服务/Dubbo/Dubbo-基础' },
              ],
            },
            {
              text: '中间件',
              items: [
                { text: '消息中间件', link: '/分布式微服务/消息中件间/消息中间件-Overview' },
                { text: '服务注册中心', link: '/分布式微服务/服务注册中心/分布式服务注册中心-概述' },
                { text: '配置中心', link: '/分布式微服务/配置中心/分布式配置中心概述' },
                { text: '任务调度', link: '/分布式微服务/任务调度/Task-Scheduling' },
              ],
            },
            {
              text: '架构与治理',
              items: [
                { text: '系统架构', link: '/分布式微服务/系统架构/架构-基础' },
                { text: '分布式事务', link: '/分布式微服务/分布式事务/分布式事务基础理论' },
                { text: '分库分表', link: '/分布式微服务/分库分表/分库分表综合概述' },
                { text: '分布式文件系统', link: '/分布式微服务/分布式文件系统/Distributed-File-System-Overview' },
                { text: '分布式链路追踪', link: '/分布式微服务/分布式链路追踪/链路追踪综合概述' },
                { text: '微服务监控', link: '/分布式微服务/微服务监控/Prometheus' },
              ],
            },
            {
              text: '基础设施',
              items: [
                { text: 'Nginx', link: '/分布式微服务/Nginx/Nginx-基础' },
                { text: '认证与授权', link: '/分布式微服务/Authorization-Certification/Authorization-Overview' },
                { text: 'Elastic Stack', link: '/分布式微服务/ElasticStack/ElasticStack' },
              ],
            },
          ],
        },
        {
          text: '数据与运维',
          items: [
            { text: '数据库', link: '/Database/' },
            { text: 'DevOps', link: '/DevOps/' },
            { text: 'Linux', link: '/Linux/' },
          ],
        },
        {
          text: '项目实战',
          items: [
            { text: '项目总览', link: '/项目资料/' },
            {
              text: '实战项目',
              items: [
                { text: '品优购', link: '/项目资料/品优购/' },
                { text: '万信金融', link: '/项目资料/万信金融/' },
                { text: '学成在线', link: '/项目资料/学成在线/' },
                { text: '好客租房', link: '/项目资料/好客租房/好客租房-README' },
                { text: '品达通用权限系统', link: '/项目资料/品达通用权限系统/品达通用权限-README' },
                { text: '国际物流云商系统', link: '/项目资料/国际物流云商系统/国际物流云商-README' },
                { text: '红包雨场景案例', link: '/项目资料/红包雨场景案例/红包雨场景案例-README' },
                { text: '自用信息管理系统', link: '/项目资料/自用信息管理系统/MoonSystem项目开发笔记' },
              ],
            },
          ],
        },
        {
          text: '更多',
          items: [
            { text: '面试手册', link: '/面试手册/' },
            { text: '编程资源分享', link: '/其他/编程资源分享' },
            { text: '语雀笔记', link: 'https://www.yuque.com/moonkira/code-note' },
          ],
        },
      ],

      // 社交链接：右上角 GitHub 官方图标（参考 Vue 官网）
      socialLinks: [
        { icon: 'github', link: 'https://github.com/MooNkirA/MooNkirA.github.io' },
      ],

      // 侧边栏：自动生成
      sidebar: generateSidebar(srcDir),

      // 大纲（右侧目录）
      outline: {
        level: [2, 3, 4],
        label: '本页目录',
      },

      // 本地全文搜索（替代 docsify search 插件）
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

      // 上一页 / 下一页
      docFooter: {
        prev: '上一页',
        next: '下一页',
      },

      // 页脚
      footer: {
        message: 'I know where dry desert ends, green grass grows · MooN',
      },

      // 返回顶部
      returnToTopLabel: '返回顶部',

      // 编辑链接（可选，指向 GitHub）
      editLink: {
        pattern: 'https://github.com/MooNkirA/MooNkirA.github.io/edit/main/:path',
        text: '在 GitHub 上编辑此页',
      },
    },

    // ===== Markdown 增强 =====
    markdown: {
      lineNumbers: false,
      html: false, // 禁用原始 HTML，避免旧笔记中的裸 HTML 导致 Vue 编译错误
      config(md) {
        // html-guard 在渲染后处理：从被转义的文本中恢复白名单 span/font 标签
        md.use(htmlGuardPlugin)
        md.use(wikilinkPlugin, { srcDir })
      },
    },

    // ===== Vue 编译器配置 =====
    // 注意：不要修改 delimiters，会破坏 VitePress 主题组件自身的 {{ }} 插值渲染
    // 笔记中的 JSX {{ }} 问题通过在 markdown 代码块中自然处理
    vue: {
      template: {
        compilerOptions: {},
      },
    },

    // ===== 构建输出 =====
    outDir: '.vitepress/dist',
    cleanUrls: true,

    // 不监听的目录
    ignoreDeadLinks: true, // 忽略死链（旧笔记中有一些失效的图片/链接引用）

    // 排除非文档文件和不需要处理的目录
    srcExclude: [
      '**/images/**',
      '**/attachments/**',
      '**/resources/**',
      'AGENTS.md',
      '**/.obsidian/**',
      '**/.trae/**',
      '**/.claude/**',
      '**/.github/**',
      '**/.script/**',
      '**/.vscode/**',
      '**/.vitepress/**',
    ],

    // ===== Vite 配置 =====
    // 强制预构建 CJS 模块，解决 dev 模式下 ESM 互操作问题
    // 注意：必须写与 mermaid 源码中完全一致的导入写法（带 .js 后缀）。
    // mermaid 的 dist 里是 `import ... from 'fastdom/extensions/fastdom-promised.js'`，
    // Vite 按精确字符串匹配预构建条目，不带 .js 的条目不会命中，会退化为直接
    // 下发原始 CJS 文件，导致浏览器报 "does not provide an export named 'default'"。
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
  }),
)
