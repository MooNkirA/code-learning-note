# Windows 系统相关资料

## 1. 常用DOS命令

- d: 回车	盘符切换
- dir(directory):列出当前目录下的文件以及文件夹
- cd (change directory)改变指定目录(进入指定目录)
    - 进入	cd 目录；cd 多级目录\\多级目录2
    - ​	回退	cd.. ；cd\
- `cls` : (clear screen)清屏
- `exit` : 退出dos命令行
- `ipconfig` ：查询IP的命令
    - `ipconfig /release` ：释放本机现有IP
    - `ipconfig /renew` ：向DHCP服务器（可以简单理解成你家的路由器）重新申领一个IP
    - `ipconfig /all` ：显示完整版IP信息
- `telnet` ：测试映射端口或远程访问主机
    - `telnet towel.blinkenlights.nl`：播放ASCII版《星球大战》
    - > *注：这项功能需要telnet支持，telnet不是Windows的默认内置组件，因此当你看到错误提示时，需要首先进入“设置” --> “应用” --> “程序和功能” --> “启用或关闭Windows功能”手工安装它（Telnet Client）*
- `msg` ：向对方电脑发送一条文本提示
    - `msg /server:对方电脑IP * 对方电脑屏幕要弹出的文本`
- `net user` ：查看本机账户情况
    - 衍生的命令后缀，比方说“`net user xxx 123456 /add`”，输入后就会在系统中创建一个名为“xxx”的新用户，而新用户密码则是“123456”。类似的还有“`net user xxx /del`”（删除xxx用户）、“`net user xxx /active:no`”（禁用xxx用户）、“`net user xxx`”（查看xxx用户的详细情况）等
- `net share` ：查看共享资源
    - `net share 要共享的文件夹` ：指定共享文件
    - `net share 要删除的共享文件夹 /delete` ：删除共享文件
- `nslookupn` ：检查网站IP地址 
    - `nslookup 对方网站域名`
- `netsh wlan show` ：探秘Wi-Fi配置文件
    - `netsh wlan show profile SSID key=clear`，输入完成后Windows会自动返回当前已连接WIFI的详细信息，包括SSID和连接密码。当前这里有一个前提，那就是你现在已经成功连接了。
- `color` ：更改CMD文字颜色
- `|` ：将命令结果输出到剪贴板
    - 具体命令是，在需要导出结果的命令后方添加“`|`”，再加入导出位置就可以了。比方说“`| clip`”是导出到剪贴板，“`| xxx.txt`”是导出到xxx.txt。
- `&&` ：将多个命令“连接”起来，一步运行多组命令

### 1.1. windows 常用命令

```bash
# 查询端口
netstat -ano
# 查询指定端口
netstat -ano |findstr "端口号"
# 根据进程PID查询进程名称
tasklist |findstr "进程PID号"
# 根据PID杀死任务
taskkill /F /PID "进程PID号"
# 根据进程名称杀死任务
taskkill -f -t -im "进程名称"
```

### 1.2. 系统脚本

#### 1.2.1. 内外网IP切换（适用win10系统）.20171122

```bash
@echo off
rem //设置变量 
set NAME="以太网"
rem //以下属性值可以根据需要更改
set ADDR=192.168.14.73
set MASK=255.255.254.0
set GATEWAY=192.168.14.1
set DNS1=10.17.65.13
set DNS2=10.202.253.28
rem //以上属性依次为IP地址、子网掩码、网关、首选DNS、备用DNS


echo 当前可用操作有：
echo 1 设置为静态IP
echo 2 设置为动态IP
echo 3 退出
echo 请选择后回车：
set /p operate=
if %operate%==1 goto 1
if %operate%==2 goto 2
if %operate%==3 goto 3


:1
echo 正在设置静态IP，请稍等...
rem //可以根据你的需要更改 
echo IP地址 = %ADDR%
echo 掩码 = %MASK%
echo 网关 = %GATEWAY%
netsh interface ipv4 set address %NAME% static %ADDR% %MASK% %GATEWAY% 
echo 首选DNS = %DNS1% 
netsh interface ipv4 set dns %NAME% static %DNS1%
echo 备用DNS = %DNS2% 
if "%DNS2%"=="" (echo DNS2为空) else (netsh interface ipv4 add dns %NAME% %DNS2%) 
echo 静态IP已设置！
pause
goto 3


:2
echo 正在设置动态IP，请稍等...
echo 正在从DHCP自动获取IP地址...
netsh interface ip set address %NAME% dhcp
echo 正在从DHCP自动获取DNS地址...
netsh interface ip set dns %NAME% dhcp 
echo 动态IP已设置！
pause
goto 3


:3
exit
```

#### 1.2.2. 内外网IP切换（适用win7系统）

```bash
@echo off

rem //设置变量
set NAME="本地连接"

rem //以下属性值可以根据需要更改
set ADDR=192.168.14.73
set MASK=255.255.254.0
set GATEWAY=192.168.14.1
set DNS1=10.17.65.13
set DNS2=10.202.253.28

rem //以上属性依次为IP地址、子网掩码、网关、首选DNS、备用DNS
echo 当前可用操作有：
echo 1 设置为静态IP
echo 2 设置为动态IP
echo 3 退出
echo 请选择后回车：

set /p operate=
if %operate%==1 goto 1
if %operate%==2 goto 2
if %operate%==3 goto 3

:1
echo 正在设置静态IP,请稍等…
rem //可以根据你的需要更改
echo IP地址 = %ADDR%
echo 掩码 = %MASK%
echo 网关 = %GATEWAY%
netsh interface ipv4 set address name=%NAME% source=static addr=%ADDR% mask=%MASK% gateway=%GATEWAY% gwmetric=0 >nul
echo 首选DNS = %DNS1%
netsh interface ipv4 set dns name=%NAME% source=static addr=%DNS1% register=PRIMARY >nul
echo 备用DNS = %DNS2%
netsh interface ipv4 add dns name=%NAME% addr=%DNS2% index=2 >nul
echo 静态IP已设置!
pause
goto 3

:2
echo 正在设置动态IP,请稍等…
echo 正在从DHCP自动获取IP地址…
netsh interface ip set address "本地连接" dhcp
echo 正在从DHCP自动获取DNS地址…
netsh interface ip set dns "本地连接" dhcp
echo 动态IP已设置!
pause
goto 3

:3
exit
```

#### 1.2.3. 一键删除电脑中的空文件夹脚本（未测试！！）

在任意目录中创建“xxx.bat”的批处理文件，复制以下脚本代码再双击运行即可。

- 批量（循环）删除指定目录下所有空文件夹代码，例如删除F:\盘下的所有空文件夹：

```bash
@echo off

for /f "delims=" %%a in ('dir /ad /b /s F:\^|sort /r') do (
rd "%%a">nul 2>nul &&echo 空目录"%%a"成功删除！
)

pause
```

- 批量删除多个磁盘的空文件夹，例如删除c、d、e、f区中所有的空文件夹：

```bash
@echo off

for %%i in (c d e f) do (
if exist %%i:\ (
for /f "delims=" %%a in ('dir /ad /b /s "%%i:\"^|sort /r') do (
rd "%%a"
)
)
)

pause
```

### 1.3. 批处理(bat)脚本命令汇总（待整理）

> 参考：[详细的批处理文件bat脚本命令](https://blog.csdn.net/ankang654321/article/details/103644637)

## 2. 系统运行命令

> 以下均为运行面板(Win+R)中输入的命令

### 2.1. 如何使用 WIN+R 运行自定义命令启动程序

首先在任意盘符下建立一个文件夹，比如在D盘建立名字为shortcut的文件夹

设置环境变量：选择计算机->右键选择属性->选择系统高级设置->选择“环境变量->双击path->添加刚刚建立的文件夹D:\shortcut(如果有多个则在每个文件夹路径后面加英文状态下的分号`;`)

将桌面上所有的快捷方式都剪切到shortcut文件夹即可,以后有快捷方式也直接扔进去

> 注意事项:如果想更加简单的使用Win+R打开程序,可以将shortcut下的文件名称更改为自己熟悉的(支持中文哦)

### 2.2. 自定义的快捷命令

`moon` 查看快捷方式文件夹

|   快捷键   |                   程序                   |
| --------- | ---------------------------------------- |
| youdao    | 网易有道词典                              |
| ps        | Adobe Photoshop CS6 ~~或者 CS2~~         |
| ie        | Internet Explorer                        |
| ++        | Notepad++                                |
| wx        | 微信                                     |
| tim       | TIM                                      |
| foo       | foobar2000                               |
| ym        | 网易云音乐                                |
| et        | Everything                               |
| spdf      | SumatraPDF                               |
| fd        | Free Download Manager                    |
| by        | 百度网盘                                  |
| jg        | 坚果云                                   |
| wy        | 腾讯微云                                  |
| vn        | VNote                                    |
| id        | IntelliJ IDEA                            |
| ec        | eclipse                                  |
| vsc       | VS Code                                  |
| nc        | Navicat Premium                          |
| stree     | Sourcetree                               |
| vm        | VMware Workstation Pro                   |
| qt        | QTranslate                               |
| bat       | 自己写的批处理命令，用来设置ip、开启服务     |
| crt       | SecureCRT（远程连接工具）                 |
| copy      | FastCopy                                 |
| jv        | JsonView(JSON格式化小工具)                |
| redis     | Redis Desktop Manager                    |
| fsc       | Faststone Capture 9.2 中文版(截图工具)    |
| draw      | draw.io（流程图工具）                     |
| ad        | 阿里云盘                                  |
| pic       | PicGo 图床工具                            |
| yq        | 语雀文档                                  |
| ~~pscc~~  | ~~Adobe Photoshop CC 2019~~              |
| ~~lr~~    | ~~Adobe Lightroom Classic 2019~~         |
| ~~serv~~  | ~~系统服务(自定义)~~                      |
| ~~mi~~    | ~~小米云服务~~                            |
| ~~qm~~    | ~~QQ音乐~~                               |
| ~~l1~~    | ~~Listen1~~                              |
| ~~yn~~    | ~~有道云笔记~~                            |
| ~~ut~~    | ~~utorrent~~                             |
| ~~idm~~   | ~~Internet Download Manager~~            |
| ~~mo~~    | ~~Motrix~~                               |
| ~~ty~~    | ~~天翼云盘~~                              |
| ~~ditto~~ | ~~Ditto~~                                |
| ~~moba~~  | ~~MobaXterm（远程连接工具）~~             |
| ~~ol~~    | ~~欧路词典~~                              |
| ~~ebook~~ | ~~Icecream Ebook Reader（电子书阅读器）~~ |

### 2.3. 安装第三方软件内设快捷运行

|  快捷键   |           程序           |
| -------- | ------------------------ |
| xshell   | xShell 6                 |
| xftp     | Xftp 6                   |
| snipaste | windows截图工具           |
| calibre  | calibre 开源电子书管理工具 |
| typora   | Typora                   |

### 2.4. window 系统常用原生命令

|                 快捷键                 |                   程序                   |
| ------------------------------------- | ---------------------------------------- |
| cmd                                   | 命令行                                   |
| regedit                               | 注册表                                   |
| services.msc                          | 系统服务                                  |
| control                               | 所有控制面版项                            |
| calc                                  | 启动计算器                                |
| mspaint                               | 画图                                     |
| notepad                               | 打开记事本                                |
| `Shutdown -s -t 600`                  | 表示600秒后自动关机                       |
| `Shutdown -a`                         | 可取消定时关机                            |
| `Shutdown -r -t 600`                  | 表示600秒后自动重启                       |
| `rundll32 user32.dll,LockWorkStation` | 表示锁定计算机                            |
| wt                                    | Microsoft.WindowsTerminal（需要手动安装） |

## 3. windows常用快捷键

|     功能      |           快捷键           |
| ------------- | ------------------------- |
| 搜索          | Win + S                   |
| 多重剪贴板     | Win + V                   |
| 显示桌面       | Win + D                   |
| 打开资源管理器 | Win + E                   |
| 锁屏          | Win + L                   |
| 切“桌面”       | Win + Ctrl + →/←          |
| 截图          | Win + Shift + S           |
| 白板          | Win + W（需要下载白板应用） |
| 显示日历       | Win + Alt + D             |
| 投影          | Win + P                   |
| 连智能电视     | Win + K                   |

## 4. windows 系统相关设置

### 4.1. C盘可清理内容

1. **PerfLogs**文件夹，系统的信息日志，文件夹可删。
2. **Windows**文件夹
    - `C:\Windows\WinSxS`，装载了电脑从新装到现在的所有补丁文件，不能删除。但里面有一个“backup”备份文件夹，是可删的。
    - `C:\Windows\Help`，帮忙文件，可删
3. **用户**文件夹：`C:\Users\用户名称\AppData\Local\Temp`。这个是Windows存留安装软件时解压的源文件，方便下次安装直接调取使用，节省解压时间，可删除。

### 4.2. win7 系统的Temporary Internet Files清空问题

1. `cmd.exe`
2. `cd AppData\Local\Microsoft\Windows\Temporary Internet Files`（或者如果有Content.IE5目录的话，cd Content.IE5）
3. `del /s/q/f *.*`

### 4.3. 开始菜单分组

#### 4.3.1. 分组名称

- Window Software
- Images Tool
- Music
- Translation Tools
- Network Disk & Download
- Social Networking Software
- Tools
- Java Development Tools

#### 4.3.2. 备份开始菜单的方法

1. 按下Win+R打开运行窗口，输入命令powershell，然后点击确定按钮
2. 这时就会打开Windows Powershell窗口，在这里输入命令`Export-startlayout –path E:\start.xml`，可以根据自己实际情况来设置相应的路径
3. 按下回车键后，就会备份好开始菜单的布局文件
4. 如果需要恢复开始菜单布局的话，只需要再次打开Windows Powershell命令行窗口，然后输入命令`import-startlayout -layoutpath E:\start.xml -mountpath c:`，按下回车键后，就会马上把其还原回来了

### 4.4. 电脑护眼颜色设置

win7系统：

1. 桌面->右键->属性->外观->高级->项目选择（窗口）
2. 颜色1（L）选择（其它）将色调改为：85。饱和度：123。亮度：205->添加到自定义颜色->在自定义颜色选定点确定->确定
3. 另一种相近的颜色设置：`R:204 G:232 B:207`

win10系统：

1. windows+R键调出运行窗口（或者鼠标右击开始键，选择运行），在运行窗口中输入`regedit`调出注册表编辑器
2. 按照如下顺序找到windows：[HKEY_CURRENT_USER\Control Panel\Colors] windows。双击windows 进入编辑状态 将原本数值删除并输入：`202 234 206`。点击确定退出注册表。
3. 按照如下顺序找到 window：[HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Themes\DefaultColors\Standard]。双击 window 打开编辑窗口，默认是勾选十六进制（若不是请勾选十六进制），将原始数据改为：`caeace`。点击确定退出注册表。

### 4.5. 这个可能与ACHI有关系吧。你先去修改到 compatible（兼容模式）进入系统

AHCI开启方法：

1. 依次展开：“开始” -> “运行”（或使用Win+R) -> 键入“regedit” -> “确定”后 -> 启动注册表编辑器 -> 展开到`[HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\services\msahci]`分支。
2. 在右侧双击“Start” -> “编辑DWORD值” -> 将“数值数据”的键值由“3”改为“0” -> 单击“确定”。
3. 关闭“注册表编辑器”窗口，并重新启动电脑。
4. 然后出来看看BIOS里面的硬盘模式，修改为ACHI后（如果没有就算了）
5. 然后在把SATA Operation Mode改为 enhanced（增强模式）



### 4.6. window系统的hosts文件位置

C:\Windows\System32\drivers\etc

### 4.7. win10锁屏壁纸位置

C:\Users\win10\AppData\Local\Packages\Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy\LocalState\Assets

### 4.8. NSIS：使用netsh advfirewall屏蔽某程序访问网络

- 关闭防火墙

```bash
nsExec::Exec 'cmd /c netsh advfirewall set allprofiles state off'
```

- 开启防火墙

```bash
nsExec::Exec 'cmd /c netsh advfirewall set allprofiles state on'
```

- 删除屏蔽

```bash
nsExec::Exec 'cmd /c netsh advfirewall firewall Delete rule name="TIM"'
```

- 添加屏蔽

```bash
nsExec::Exec 'cmd /c netsh advfirewall firewall add rule name="TIM" dir=out action=block program="C:\Program Files\TIM Lite\Bin\TIM.exe"'
```

### 4.9. 删掉 WIN10 回收站右键菜单的固定到＂开始＂屏幕！

- 删除：打开注册表，定位到 HKEY_LOCAL_MACHINE\SOFTWARE\Classes\Folder\shellex\ContextMenuHandlers，删除其子键 PintoStartScreen
- 恢复：在 HKEY_LOCAL_MACHINE\SOFTWARE\Classes\Folder\shellex\ContextMenuHandlers 上单击右键，新建项 PintoStartScreen，修改其默认值为 {470C0EBD-5D73-4d58-9CED-E91E22E23282}

### 4.10. 限制保留宽带设置

1. 按“WIN+R”，打开【运行】对话框；
2. 输入“regedit”，回车，打开注册表编辑器；
3. 依次展开“HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\RunMRU”
4. 按“WIN+R”，打开【运行】对话框，输入gpedit.msc
5. 计算机配置－管理模板－网络－qos数据包计划程序－限制保留宽带
6. 选择已启用。一般默认是20，直接把它改成0。

### 4.11. win10 系统任务栏设置时间显示秒

1. 按“WIN+R”，打开【运行】对话框；
2. 输入“regedit”，回车，打开注册表编辑器；
3. 在注册表中定位到以下子健：HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Advanced
4. 后在Advanced上鼠标右键点击呼出菜单，选择 --> 新建（N） --> DWORD(32位)值。也可以左键点击Advanced，在右边区域点击空白处点击鼠标右键呼出菜单选择 --> 新建（N） --> DWORD(32位)值。
5. 将新建 DWORD(32位)值，命名为ShowSecondsInSystemClock，双击打开将数值数据改为1，并点击确定，关闭注册表。

*如果想恢复不显示秒，则将创建的ShowSecondsInSystemClock删除即可*

### 4.12. Win10系统删除无用的服务

1. 运行 --> regedit，打开注册表编辑器
2. 定位到【计算机\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services】，选择服务名称，右键删除即可

### 4.13. 修改window默认系统安装目录

Windows10系统更改软件程序默认安装目录的方法

1. 运行 --> regedit，打开注册表编辑器
2. 进入注册表【HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion】目录下，并左键单击：CurrentVersion；
3. 在CurrentVersion对应的右侧窗口，找到ProgramFilesDir，并左键双击ProgramFilesDir打开编辑字符串对话框，把Program Files的数值数据从C:\Program Files更改为D:\Program Files，再点击：确定；
4. 如果安装的是Windows10的64位系统，在CurrentVersion对应的右侧窗口，找到ProgramFilesDir（x86），并左键双击ProgramFilesDir（x86）打开编辑字符串对话框，把Program Files（x86）的数值数据从C:\Program Files（x86）更改为D:\Program Files（x86），再点击：确定；

修改系统存储的保存位置

1. 左键点击系统桌面左下角的“开始”，在开始菜单中点击：设置
2. 在打开的设置窗口，点击：系统 --> 窗口左侧的“存储”
3. 在存储对应的右侧窗口，用鼠标左键按住右侧的滑块向下拖动，找到：保存位置，在保存位置下，点击：新的应用将保存到此电脑（C:）后面的小勾
4. 修改成D盘。之后打开磁盘(D:\)，可以看到磁盘(D:\)中新增了三个文件夹：MoonZero（用户文件：文档、音乐、图片和视频）、Program Files（程序文件）和Windows Apps（窗口应用程序）；

### 4.14. win10一般禁用的服务

1. 运行输入【services.msc】打开服务面板，禁用以下服务
    1. Connected User Experiences and Telemetry
    2. Diagnostic Execution Service
    3. Diagnostic Policy Service
    4. Diagnostic Service Host
    5. Diagnostic System Host
    6. SysMain（以前的Windows Superfetch 感觉SSD上效果不大，不想禁用的可以改为“手动启动”）
    7. Windows Search （关联了Win10里的很多新功能，而且对于SSD影响也不大，可以不禁用）
2. 右击“此电脑” --> “属性” --> “高级系统设置” --> “高级” --> “性能”
3. 点击“设置” --> “更新与安全” --> “Windows预览体验计划”，退出Windows Insider计划。
4. 右击任务栏空白处选择“任务管理器”，切换到“启动”标签，将没必要的自启动程序全部禁用。

### 4.15. 修复win10右键无新建txt文本文件

```bat
Windows Registry Editor Version 5.00
[HKEY_CLASSES_ROOT\.txt]
@="txtfile"
"Content Type"="text/plain"
[HKEY_CLASSES_ROOT\.txt\ShellNew]
"NullFile"="" [HKEY_CLASSES_ROOT\txtfile]
@="文本文档"
[HKEY_CLASSES_ROOT\txtfile\shell]
[HKEY_CLASSES_ROOT\txtfile\shell\open]
[HKEY_CLASSES_ROOT\txtfile\shell\open\command]
@="NOTEPAD.EXE %1"
```

打开记事本，复制以上内容，另存为`xxx.reg`。点击文件，确认操作后，重启电脑生效

### 4.16. 关闭cmd命令行窗口的中文输入法

运行`regedit`命令，打开注册表窗口，修改注册表：`HKEY_CURRENT_USER\Console\LoadConIme` 的键值由`1`改为`0`

### 4.17. 修改cmd/powershell命令行窗口默认编码

**临时修改**

- 使用`chcp`命令可以输出当前编码的数值，如：`GBK`是936，`UTF-8`是65001

**修改注册表**

- **修改powershell默认编码**：运行`regedit`命令打开注册表，展开注册表`计算机\HKEY_CURRENT_USER\Console`项。选择powershell，点击修改右边窗口中`CodePage`项，选择十进制，修改值为`65001`。修改后就每次启动都默认改成UTF-8的编码
- **修改cmd编码**：运行`regedit`命令打开注册表，展开注册表`计算机\HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Command Processor`项。如果右边窗口没有`autorun`字符串值，则右键新建字符串值，数值名称：`autorun`，数值数据：`chcp 65001`。修改后就每次启动都默认改成UTF-8的编码

### 4.18. 彻底关闭Cortana小娜

- **关闭Cortana小娜的权限**

Win10的设置菜单 -> "应用" -> 在应用列表中搜索找到Cortana -> 高级选项 -> 可以将Cortana小娜的麦克风、后台以及开机启动的权限全部关闭

- **彻底关闭Cortana小娜**

运行`regedit`进入注册表 -> `计算机\HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Microsoft\Windows` -> 用右键点击“Windows”目录，选择“新建”，新建一个“项”。将这个项命名为“Windows Search” -> 右键点击“Windows Search”，新建一个“DWORD(32位)值” -> 将这个值命名为“AllowCortana”，然后双击这个值，确认它的数值为“0”，然后按下确定保存 -> 之后，Cortana就会被禁用了。这时候再打开Cortana，就会看到禁用的提示

- **完全删除Cortana小娜**

以管理员模式运行Powershell -> 运行以下代码删除

```bash
Get-AppxPackage -allusers Microsoft.549981C3F5F10 | Remove-AppxPackage
```

### 4.19. 关闭 Win11/ Win 10 内存压缩

Win11默认开启了内存压缩功能。可以压缩内存中的数据，让内存占用更少，同时减少Swap频次，带来更高的I/O效率。但CPU性能较弱的设备，例如轻薄本，开启内存压缩可能会造成卡顿缓慢。同时，内存压缩需要消耗额外的CPU资源，带来更多耗电发热，这对注重续航的设备来说也是不合适的。

- **通过任务管理器查看内存压缩的开启状态**。如果开启了内存压缩，那么在任务管理器中，就会显示压缩内存的数据
- **通过命令行查看内存压缩的开启状态**。使用系统管理员权限，打开PowerShell，然后输入命令 `Get-MMAgent` 后。如果看到“MemoryCompression”这一项是“Ture”，那么说明内存压缩已经开启。
- **关闭内存压缩**。使用系统管理员权限，打开PowerShell，然后输入命令 `Disable-MMAgent -mc` 后，重启系统，内存压缩就关闭了。
- **重新打开内存压缩**。使用系统管理员权限，打开PowerShell，然后输入命令 `Enable-MMAgent -mc` 后，重启系统，内存压缩就重新开启。

### 4.20. 清除电脑的运行记录

1. win+R 打开运行窗口，输入 `regedit` 打开注册表编辑器
2. 展开 `HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\RunMRU`在右侧除了默认
3. 将其他选项都删除掉

## 5. Office资料

### 5.1. Excel资料

1. Excel表公式换行
    - 例：`d1=A1&CHAR(10)&A2&CHAR(10)&A3`单元格对齐要设置为“自动换行”
2. Excel表身份证号区分男女
    - MOD函数（MOD函数是取余数的函数）取第17位数字除以2的余数，如果余数是0，则第17位是偶数，也就是该身份证是女性；反之，如果余数是1则说明身份证是男性。我们嵌套IF函数如下：
    - `=IF(MOD(MID(A2,17,1),2),"男","女")`
3. 身份证号中提取出生年月日
    - `=MID(B4,7,4)&"年"&MID(B4,11,2)&"月"&MID(B4,13,2)&"日"`
    - `=TEXT(MID(B4,7,8),"0年00月00日")`
4. 身份证号计算年龄
    - `=DATEDIF(TEXT(MID(B4,7,8),"0000-00-00"),TODAY(),"y")`
5. 合并单元格内容的函数是CONCATENATE (text1,text2,...)，如合并一列则是：PHONETIC(C1:C26)
6. 绝对值=ABS(B1-A1)
7. excel输入√符号
    - 按住Alt键不放，再输入小数字键盘上的数字41420，松开Alt键就\
    - 输入×号的话就是按小键盘上的41409。
8. 数字按长度补0，并转换为文本，单纯的改为数字（单元格格式，自定义，示例8个0），只是显示8位，实际导入数据库的时候还是实际的位数
    - 公式：`=REPT(补位内容,总位数-LEN(A2))&A2`
    - REPT函数第一个参数是补位的内容
    - REPT函数第二个参数是需要补充的位数，即等于总位数-选择的单元格长度

### 5.2. word资料

1. 取消超链接快捷键：Ctrl+Shift+F9
2. 取消代码模式 Alt+F9

## 6. Chrome 与 Edge浏览器

### 6.1. Edge与IE设置隐私模式

- IE：右键 -> 属性 -> 在打开的IE属性窗口中，找到目标文本框，在最后加入` -private`（*注：前面有一个空格*）
- Edge：右键 -> 属性 -> 在打开的属性窗口中，找到目标文本框，在最后加入` -InPrivate`（*注：前面有一个空格*）

### 6.2. Chrome 与 Edge 常用快捷键

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

### 6.3. Chrome 设置隐身模式与开启暗黑模式

隐身模式：图标右键属性，在“目标”后添加参数“` --incognito`”（注意是双短划线，不包括双引号，双短划线前加一空格）就可以直接以隐身模式启动Chrome浏览器

暗黑模式：右键属性 -> 目标 -> 添加参数“` --force-dark-mode`”（注意双短划线前加一空格），启动时强制打开暗黑模式

### 6.4. 扩展程序管理

- 地址栏输入`chrome://extensions/`并回车打开扩展程序页，点右上角的开发人员模式，可以看到每一个扩展下面都有个ID，后面跟着一串字母，到浏览器安装目录（`\Chrome\User Data\Default\Extensions\`）下找到一个同名的文件夹，进入，里面有一个以版本号命名的文件夹，把这个文件夹复制出来。
- 为了方便收藏，以及今后可以再安装，将这个版本号文件夹重新打包成crx文件：
- 在上述的【扩展程序】页，打开开发人员模式，再点“打包扩展程序”，“扩展程序根目录”选刚才的版本号文件夹，密钥留空，点确定。
- 会生成一个crx和一个pem，把crx文件珍藏好，pem可以丢掉了。

### 6.5. Chrome / Edge 实验功能

在地址栏输入以下地址进入实验功能页面

```
# Edge
edge://flags/
# chrome
chrome://flags/
```

需要修改哪些功能，可以直接搜索，或者在 url 拼接关键字即可，如：`浏览器名://flags/#功能名称`。一般都改成 `Enabled` 即可开启，需要重启浏览器。

- 启用 Chrome / Edge “隐藏”自带的多线程下载功能：`enable-parallel-downloading`
- chrome 浏览器 91+ 版本后 `SameSite by default cookies` 被移除，导致无法手动进行添加 cookie。修改 `Partitioned cookies` 设置项即可开启
- 让网页内容强制显示为深色主题，设置项：`enable-force-dark`
- 开启全局媒体播放控制。设置项：`global-media-controls`
- 鼠标悬停标签页展示网页预览图。设置项：`tab-hover-card-images`
- 使用当前系统样式滚动条。设置项：`edge-overlay-scrollbars-win-style`
- 改善了新版Edge乃至Chrome的平滑滚动体验。设置项：`smooth-scrolling`
- 阻止视频自动播放。搜索【Show block option in autoplay settings】，设置项：`edge-autoplay-user-setting-block-option`。重启浏览器后，在Edge的“设置 -> Cookie和网站权限”页面中，找到“站点权限”下的“媒体自动播放”，将其设置为“限制”即可。
- 开启系统特色。设置项：`edge-visual-rejuv-show-settings`
- 关闭Chrome自带广告（Edge暂时未发现有），将 “Enable promos for sync trusted vault passphrase” 的设置项，将其改为disable关闭。

> 注意：有些设置项的 `edge-` 开头，Chrome 有些不适用

### 6.6. 强制阅读模式

Edge 是支持阅读模式的，而且效果还相当不错。但阅读模式需要Edge自己判断是否能打开，很多网页无法被Edge识别开启。

在 Edge 地址栏中，为网页URL前面加上一个“`read:`”前缀，然后按下回车键，阅读模式就强制打开了！

### 6.7. 查看版本信息

- Chrome：`chrome://version`
- Edge：`edge://version`

### 6.8. 缓存位置

在**个人资料路径**，如：`C:\Users\win10\AppData\Local\Microsoft\Edge\User Data\Default`。 找到 Cache 和 Code Cache 就是缓存文件目录

## 7. Rime输入法设置

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

## 8. Everything 使用技巧

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

## 9. foobar2000

### 9.1. foobar2000不支持APE文件格式的解决方法

这是因为缺少相应的播放插件，需要安装一个插件--monkey's audio(ape)才可以播放。foobar从某个版本起，把原来默认内置的一些插件去掉了，原因不明，可能是为了控制安装包的体积。

插件下载地址：http://www.foobar2000.org/components/view/foo_input_monkey

点击这个页面的 Download，下载后解开压缩包得到 foo_input_monkey.dll 文件。把这个文件放到foobar安装目录里的 compontents 文件夹里。重新打开foobar后就可以播放APE文件了。	

## 10. 待尝试的软件/系统

- unlocker：强力解除后台占用工具。当要删除某个文件或者文件夹是提示"文件被另一个程序打开"，但是却又不知道是哪个程序打开的，只能重启电脑然后才能删除，unlocker就是解决这个问题的一个工具。
- DropIt：一键分类文件
- Linux操作系统：Ubuntu和Deepin
- 外网映射工具：natapp、ngrok
- 代理工具：Shadowsocks
- 超炫酷的 Docker 终端 UI lazydocker
- [Cool PDF Reader](http://www.pdf2exe.com/reader.html) 号称最小的PDF阅读器
- uTools：windows工具插件包
- 免费开源图像编辑器 GIMP
- Markdown, Please! 有两种使用方式，直接在官网输入想要转换的网页地址，或者使用小书签放置在书签栏上，当需要转换的时候，点一下就行了
> 如：`https://markdown.readmorejoy.com/\?url\=https://www.appinn.com/`
- Recuva：文件恢复工具
- 免费对比工具 - [Meld](http://meldmerge.org/)
- 免费梯子：[batternet](https://getintopc.com/softwares/security/betternet-vpn-free-download/)
- [MiniBin](https://minibin.e-sushi.net) - 把回收站放到任务栏
- clean reader - mobi电子书阅读软件

### 10.1. 免费图床

- [ImgURL](https://imgurl.org/) - mgURL 对游客做出了上传限制。每日仅支持上传 10 张照片，每张照片的大小不能超过 5M，适合上传图片不多的用户。
- [upload.cc](https://upload.cc/) - upload.cc 对照片大小有限制。单张照片最大 5M，且仅支持常见格式，适合需要频繁分享小图的用户
- [SM.MS](https://sm.ms/) - SM.MS 支持上传单张最大 5M 的图片，每次上传最多支持 10 张图片，适合作为主力图床使用。
- [路过图床](https://imgchr.com/) - 它以「无限空间」和「无限流量」作为最大的亮点。路过图床仅对单次上传的大小做出了限制。游客只能上传小于 5M 的图片，注册后可上传 10M 内的图片，适合作为主力图床使用。
- [小贱贱图床](https://pic.xiaojianjian.net/) - 小贱贱图床是寄存于新浪图床的工具，宣称不限流量、永久免费。小贱贱图床对上传数量做出了限制。每日仅支持上传 20 张图片，适合上传需求较少的用户。
- [Postimage](https://postimages.org) - postimage 对文件大小暂时不设限，但官方的会对频繁传输大量图片的可疑账号进行监控甚至封禁。
- [鲜咕嘟](http://www.xiangudu.com/) - 鲜咕嘟支持单张最大 20M 的图片，适合需要上传大尺寸图片的用户。但过大尺寸的图片会被压缩到 1800px 的大小。
- [偶流](https://upload.ouliu.net/) - 偶流是国内的一个老牌图床工具，偶流支持单张最大 10M 的文件，但系统会不定期进行文件清除，比较适合临时使用。

### 10.2. 常用软件大全

- windows GitHub 网址：https://github.com/Awesome-Windows/Awesome/blob/master/README-cn.md
- Linux GitHub 网址：https://github.com/luong-komorebi/Awesome-Linux-Software/blob/master/README_zh-CN.md
- MacOS GitHub 网址：https://github.com/jaywcjlove/awesome-mac/blob/master/README-zh.md
