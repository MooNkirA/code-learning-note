# Visual Studio Code

## 1. 常用快捷键

- `ctrl+shift+L` 批量修改变量名称

### 1.1. 多行编辑操作

VScode对多行编辑有两种模式。

#### 1.1.1. 第一种模式

竖列选择：长按`Alt+Shift`，按左键拖动鼠标选择多行。这种模式下只可以选择竖列，不可以随意插入光标。所以只限制于同一列且不间隔的情况下

#### 1.1.2. 第二种模式

- 竖列选择：`Shift+Ctrl`
- 选择多个编辑位点：`Ctrl+光标点击`

这种模式下不仅可以选择竖列，同时还可以在多个地方插入光标

#### 1.1.3. 两种模式的切换

使用`Ctrl+Shift+p`快捷键调用查询输入栏，输入“cursor”，列表中会出现“切换多行修改键”这个选项。选择这个选项就可以在两种模式下切换

## 2. VSCode拓展推荐（前端开发）【网络资源】

https://github.com/varHarrie/varharrie.github.io/issues/10

> 最后更新于：2018-08-20 11:13:21

## 3. 个人首选项配置（网络资源，仅供参考）

```json
{
  "breadcrumbs.enabled": true,
  "editor.tabSize": 2,
  "editor.renderWhitespace": "boundary",
  "editor.cursorBlinking": "smooth",
  "editor.minimap.renderCharacters": false,
  "editor.fontFamily": "'Fira Code', 'Droid Sans Mono', 'Courier New', monospace, 'Droid Sans Fallback'",
  "editor.fontLigatures": true,
  "explorer.confirmDragAndDrop": false,
  "extensions.autoUpdate": false,
  "files.insertFinalNewline": true,
  "git.autofetch": true,
  "git.path": "F:\\Program Files\\Git\\cmd\\git.exe",
  "search.exclude": {
    "**/node_modules": true,
    "**/dist": true
  },
  "typescript.locale": "en",
  "window.titleBarStyle": "custom",
  "window.title": "${dirty}${activeEditorMedium}${separator}${rootName}",
  "window.zoomLevel": 1,
  "workbench.activityBar.visible": true,
  "workbench.colorTheme": "Plastic - deprioritised punctuation",
  "workbench.iconTheme": "vscode-great-icons",
  "workbench.startupEditor": "newUntitledFile",
  "eslint.autoFixOnSave": true,
  "eslint.validate": ["javascript", "javascriptreact", "vue"],
  "vsicons.projectDetection.autoReload": true,
  "vsicons.dontShowNewVersionMessage": true,
  "tslint.autoFixOnSave": true,
  "debugwrapper.wrappers": {
    "default": "console.log('$eSEL', $SEL)"
  },
  "prettier.tslintIntegration": true,
  "cSpell.userWords": [
    "Unmount"
  ],
  "jest.autoEnable": false,
}
```





