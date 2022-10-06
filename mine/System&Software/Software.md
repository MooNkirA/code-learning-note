# 软件教程

## 1. 常用软件大全

- windows GitHub 网址：https://github.com/Awesome-Windows/Awesome/blob/master/README-cn.md
- Linux GitHub 网址：https://github.com/luong-komorebi/Awesome-Linux-Software/blob/master/README_zh-CN.md
- MacOS GitHub 网址：https://github.com/jaywcjlove/awesome-mac/blob/master/README-zh.md

### 1.1. 待尝试的软件/系统

- unlocker：强力解除后台占用工具。当要删除某个文件或者文件夹是提示"文件被另一个程序打开"，但是却又不知道是哪个程序打开的，只能重启电脑然后才能删除，unlocker就是解决这个问题的一个工具。
- DropIt：一键分类文件
- Linux操作系统：Ubuntu和Deepin
- 外网映射工具：natapp、ngrok
- 代理工具：Shadowsocks
- 超炫酷的 Docker 终端 UI lazydocker
- [Cool PDF Reader](http://www.pdf2exe.com/reader.html) 号称最小的PDF阅读器
- Markdown, Please! 有两种使用方式，直接在官网输入想要转换的网页地址，或者使用小书签放置在书签栏上，当需要转换的时候，点一下就行了
> 如：`https://markdown.readmorejoy.com/\?url\=https://www.appinn.com/`
- Recuva：文件恢复工具
- 免费对比工具 - [Meld](http://meldmerge.org/)
- 免费梯子：[batternet](https://getintopc.com/softwares/security/betternet-vpn-free-download/)

### 1.2. 免费图床

- [ImgURL](https://imgurl.org/) - mgURL 对游客做出了上传限制。每日仅支持上传 10 张照片，每张照片的大小不能超过 5M，适合上传图片不多的用户。
- [upload.cc](https://upload.cc/) - upload.cc 对照片大小有限制。单张照片最大 5M，且仅支持常见格式，适合需要频繁分享小图的用户
- [SM.MS](https://sm.ms/) - SM.MS 支持上传单张最大 5M 的图片，每次上传最多支持 10 张图片，适合作为主力图床使用。
- [路过图床](https://imgchr.com/) - 它以「无限空间」和「无限流量」作为最大的亮点。路过图床仅对单次上传的大小做出了限制。游客只能上传小于 5M 的图片，注册后可上传 10M 内的图片，适合作为主力图床使用。
- [小贱贱图床](https://pic.xiaojianjian.net/) - 小贱贱图床是寄存于新浪图床的工具，宣称不限流量、永久免费。小贱贱图床对上传数量做出了限制。每日仅支持上传 20 张图片，适合上传需求较少的用户。
- [Postimage](https://postimages.org) - postimage 对文件大小暂时不设限，但官方的会对频繁传输大量图片的可疑账号进行监控甚至封禁。
- [鲜咕嘟](http://www.xiangudu.com/) - 鲜咕嘟支持单张最大 20M 的图片，适合需要上传大尺寸图片的用户。但过大尺寸的图片会被压缩到 1800px 的大小。
- [偶流](https://upload.ouliu.net/) - 偶流是国内的一个老牌图床工具，偶流支持单张最大 10M 的文件，但系统会不定期进行文件清除，比较适合临时使用。

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
