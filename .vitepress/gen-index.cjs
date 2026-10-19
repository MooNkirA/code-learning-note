// 为每个一级分类目录生成 index.md 入口文件
const fs = require('fs');
const path = require('path');

// 仓库根目录：.vitepress 的上一级
const root = path.resolve(__dirname, '..');

// 一级分类及其显示名称
const categories = {
  'AI': 'AI 辅助开发',
  'Database': '数据库',
  'DevOps': 'DevOps',
  'Frontend': '前端技术',
  'Java': 'Java 核心',
  'JavaWeb': 'JavaWeb',
  'JVM': 'JVM',
  'Linux': 'Linux',
  'Python': 'Python',
  '其他': '其他',
  '分布式微服务': '分布式微服务',
  '后端框架': '后端框架',
  '并发编程': '并发编程',
  '面试手册': '面试手册',
  '项目资料': '项目实战',
};

// 排除的目录
const excludeDirs = new Set(['images', '.obsidian', '.trae', '.claude', '.vscode']);

for (const [dirName, title] of Object.entries(categories)) {
  const dirPath = path.join(root, dirName);
  if (!fs.existsSync(dirPath)) continue;

  let content = `# ${title}\n\n`;

  // 收集顶层 .md 文件
  const topFiles = fs.readdirSync(dirPath)
    .filter(f => f.endsWith('.md') && f !== 'index.md')
    .sort();

  if (topFiles.length > 0) {
    content += `## 笔记\n\n`;
    for (const file of topFiles) {
      const name = file.replace('.md', '');
      content += `- [${name}](${name})\n`;
    }
    content += `\n`;
  }

  // 收集子目录
  const subDirs = fs.readdirSync(dirPath, { withFileTypes: true })
    .filter(d => d.isDirectory() && !excludeDirs.has(d.name))
    .map(d => d.name)
    .sort();

  if (subDirs.length > 0) {
    content += `## 分类\n\n`;
    for (const subDir of subDirs) {
      content += `### ${subDir}\n\n`;
      const subPath = path.join(dirPath, subDir);
      const mdFiles = fs.readdirSync(subPath)
        .filter(f => f.endsWith('.md'))
        .sort();

      for (const file of mdFiles) {
        const name = file.replace('.md', '');
        content += `- [${name}](${subDir}/${name})\n`;
      }
      content += `\n`;
    }
  }

  fs.writeFileSync(path.join(dirPath, 'index.md'), content, 'utf-8');
  console.log(`Created: ${dirName}/index.md`);
}

console.log('Done!');
