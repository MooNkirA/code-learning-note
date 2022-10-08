# 网页浏览器

## 1. Chrome 浏览器

### 1.1. Chrome 设置隐身模式与开启暗黑模式

- 隐身模式：图标右键属性，在“目标”后添加参数“` --incognito`”（注意是双短划线，不包括双引号，双短划线前加一个空格）就可以直接以隐身模式启动Chrome浏览器
- 暗黑模式：右键属性 -> 目标 -> 添加参数“` --force-dark-mode`”（注意双短划线前加一个空格），启动时强制打开暗黑模式

### 1.2. Chrome 常用快捷键

- Ctrl+T：打开新标签页
- Ctrl+Shift+T：重新打开上次关闭的标签页
- Ctrl+1 到 Ctrl+9 能快速切换到对应的第 1 至第 9 个标签页。但 Ctrl + 9 对应的不是第 9个而是最后一个标签页
- Ctrl+W：关闭当前标签页
- Ctrl+D：收藏网页
- Ctrl+Shift+B：打开/关闭书签栏
- Ctrl+Shift+O：打开书签管理器
- Ctrl+Shift+Delete：打开“清除浏览数据”对话框
- Alt+D 或者 F6：将光标定位于地址栏
- 输入地址，按下 Alt + 回车键，可将地址在新标签页中打开

> 参考：[Chrome（谷歌浏览器 ）使用总结（一）——快捷键](https://juejin.cn/post/6844903573717778439)

### 1.3. 扩展程序管理

- 地址栏输入`chrome://extensions/`并回车打开扩展程序页，点右上角的开发人员模式，可以看到每一个扩展下面都有个ID，后面跟着一串字母，到浏览器安装目录（`\Chrome\User Data\Default\Extensions\`）下找到一个同名的文件夹，进入，里面有一个以版本号命名的文件夹，把这个文件夹复制出来。
- 为了方便收藏，以及今后可以再安装，将这个版本号文件夹重新打包成crx文件：
- 在上述的【扩展程序】页，打开开发人员模式，再点“打包扩展程序”，“扩展程序根目录”选刚才的版本号文件夹，密钥留空，点确定。
- 会生成一个crx和一个pem，把crx文件珍藏好，pem可以丢掉了。

### 1.4. Chrome 实验功能

在地址栏输入 `chrome://flags/`，进入 Edge 实验功能页面。需要修改哪些功能，可以直接搜索，或者在 url 拼接关键字即可，如：

```
浏览器名://flags/#功能名称
```

一般将选项改成 `Enabled` 即可开启，需要重启浏览器。

- 启用 Chrome “隐藏”自带的多线程下载功能：`enable-parallel-downloading`
- chrome 浏览器 91+ 版本后 `SameSite by default cookies` 被移除，导致无法手动进行添加 cookie。修改 `Partitioned cookies` 设置项即可开启
- 让网页内容强制显示为深色主题，设置项：`enable-force-dark`
- 开启全局媒体播放控制。设置项：`global-media-controls`
- 鼠标悬停标签页展示网页预览图。设置项：`tab-hover-card-images`

### 1.5. 查看版本信息

地址栏输入 `chrome://version`

## 2. Edge 浏览器

### 2.1. Edge 与 IE 设置隐私模式

- IE：右键 -> 属性 -> 在打开的 IE 属性窗口中，找到目标文本框，在最后加入` -private`（*注：前面有一个空格*）
- Edge：右键 -> 属性 -> 在打开的属性窗口中，找到目标文本框，在最后加入` -InPrivate`（*注：前面有一个空格*）2.2. 

### 2.2. Edge 常用快捷键

- Ctrl+T：打开新标签页
- Ctrl+Shift+T：重新打开上次关闭的标签页
- Ctrl+1 到 Ctrl+9 能快速切换到对应的第 1 至第 9 个标签页。但 Ctrl + 9 对应的不是第 9个而是最后一个标签页
- Ctrl+W：关闭当前标签页
- Ctrl+D：收藏网页
- Ctrl+Shift+B：打开/关闭书签栏
- Ctrl+Shift+O：打开书签管理器
- Ctrl+Shift+Delete：打开“清除浏览数据”对话框
- Alt+D 或者 F6：将光标定位于地址栏
- 输入地址，按下 Alt + 回车键，可将地址在新标签页中打开

> 参考：[Chrome（谷歌浏览器 ）使用总结（一）——快捷键](https://juejin.cn/post/6844903573717778439)

### 2.3. 查看 Edge 版本信息

地址栏输入 `edge://version`

### 2.4. Edge 缓存位置

在**个人资料路径**，如：`C:\Users\win10\AppData\Local\Microsoft\Edge\User Data\Default`。 找到 Cache 和 Code Cache 就是缓存文件目录

### 2.5. 强制阅读模式

Edge 是支持阅读模式的，而且效果还相当不错。但阅读模式由 Edge 来判断是否能打开，很多网页无法被 Edge 识别开启。

在 Edge 地址栏中，为网页URL前面加上一个 `read:` 前缀，然后按下回车键，阅读模式就强制打开了！

### 2.6. Edge 实验功能

在地址栏输入 `edge://flags/`，进入 Edge 实验功能页面。需要修改哪些功能，可以直接搜索，或者在 url 拼接关键字即可，如：

```
浏览器名://flags/#功能名称
```

一般将选项改成 `Enabled` 即可开启，需要重启浏览器。

- 启用 Chrome / Edge “隐藏”自带的多线程下载功能：`enable-parallel-downloading`
- chrome 浏览器 91+ 版本后 `SameSite by default cookies` 被移除，导致无法手动进行添加 cookie。修改 `Partitioned cookies` 设置项即可开启
- 让网页内容强制显示为深色主题，设置项：`enable-force-dark`
- 开启全局媒体播放控制。设置项：`global-media-controls`
- 鼠标悬停标签页展示网页预览图。设置项：`tab-hover-card-images`
- 使用当前系统样式滚动条。设置项：`edge-overlay-scrollbars-win-style`
- 改善了新版Edge乃至Chrome的平滑滚动体验。设置项：`smooth-scrolling`
- 阻止视频自动播放。搜索【Show block option in autoplay settings】，设置项：`edge-autoplay-user-setting-block-option`。重启浏览器后，在Edge的“设置 -> Cookie和网站权限”页面中，找到“站点权限”下的“媒体自动播放”，将其设置为“限制”即可。
- 开启系统特色。设置项：`edge-visual-rejuv-show-settings`
