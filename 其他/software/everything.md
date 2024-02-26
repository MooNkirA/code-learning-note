## 1. everything 概述

> 使用教程：
>
> - 官方帮助(中文)：[Searching - voidtools](https://www.voidtools.com/zh-cn/support/everything/searching/)
> - [《高效搜索神器Everything最全使用技巧(一篇看全)及详细功能帮助教程》](https://zhuanlan.zhihu.com/p/409783518)

## 2. 常用配置

### 2.1. 定义热键

打开工具菜单，点击选项 -> 点击快捷键页面 -> 选择热键方式 -> 按下新的热键组合 -> 点击确定

> Tips: Everything 必须后台运行时热键才能工作。无法覆盖已存在热键。

禁用 Windows 默认热键，例如 Win + F：

- 打开运行面板输入 `regedit`，打开注册表编辑器

```
HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\Advanced
```

- 创建新字符串值 `DisabledHotkeys`
- 设置 `DisabledHotkeys` 数据为想要释放的字母，例如：F
- 重启计算机后，在 Everything 中应用释放的热键 Win + F

## 3. 基础搜索

要将结果限制为某个驱动器，只需搜索该驱动器，例如，要查找`D:`驱动器上的文件和文件夹，请搜索：

```
d:
```

包含路径限制结果到一个文件夹，例如在文件夹 D:\Downloads 中查找文件和文件夹，搜索：

```
d:\downloads\
```

### 3.1. 示例

- 查找空文件夹：`empty:`
- 查找重复文件：`dupe:`
- 使用正则语法搜索：通过“搜索”菜单选择“使用正则表达式”或直接按下 Ctrl+R 组合键均可启用此功能。

```
a|b a或b
gr(a|e)y 匹配 gray 或 grey
. 任一字符
[abc] 任一字符：a或b或c
[^abc] 任一字符，但不包括a、b、c
[a-z] 从a到z之间的任一字符
[a-zA-Z]从a到z，及从A到Z之间的任一字符
^ 文件名开始
$ 文件名结束
* 前一项内容重复0或多次
? 前一项内容重复0或1次
+ 前一项内容重复1或多次
{x} 前一项内容重复x次
{x,} 前一项内容重复x或更多次
{x,y} 前一项内容重复次数介于x和y之间
```