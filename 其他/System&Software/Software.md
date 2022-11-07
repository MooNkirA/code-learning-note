# 软件教程

## 1. 常用软件大全

- windows GitHub 网址：https://github.com/Awesome-Windows/Awesome/blob/master/README-cn.md
- Linux GitHub 网址：https://github.com/luong-komorebi/Awesome-Linux-Software/blob/master/README_zh-CN.md
- MacOS GitHub 网址：https://github.com/jaywcjlove/awesome-mac/blob/master/README-zh.md

## 2. Everything 使用技巧

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

> 使用教程：
>
> - 官方帮助(中文)：[Searching - voidtools](https://www.voidtools.com/zh-cn/support/everything/searching/)
> - [《高效搜索神器Everything最全使用技巧(一篇看全)及详细功能帮助教程》](https://zhuanlan.zhihu.com/p/409783518)

## 3. foobar2000

### 3.1. foobar2000不支持APE文件格式的解决方法

这是因为缺少相应的播放插件，需要安装一个插件--monkey's audio(ape)才可以播放。foobar从某个版本起，把原来默认内置的一些插件去掉了，原因不明，可能是为了控制安装包的体积。

插件下载地址：http://www.foobar2000.org/components/view/foo_input_monkey

点击这个页面的 Download，下载后解开压缩包得到 foo_input_monkey.dll 文件。把这个文件放到foobar安装目录里的 compontents 文件夹里。重新打开foobar后就可以播放APE文件了。	

## 4. Rime 输入法设置

特殊字符输入

- `zzbd`：中文对符号，如：“”、《》、『』
- `zzde`：А、
- `zzdl`：Ⅰ、Ⅳ
- `zzds`：⒈、⒉
- `zzdw`：单位符号
- `zzdx`：ΔΞΠ
- `zzfs`：⑸⒀
- `zzhb`：货币
- `zzjp` & `zzpj`：日文
- `zzpp`：中文部首
- `zzpy`：中文拼音
- `zzsx`：数学符号
- `zzts`：特殊符号
- `zzxe`：
- `zzxl`：
- `zzxx`；
- `zzys`
- `zzzs`
- `zzzy`：台湾注音
