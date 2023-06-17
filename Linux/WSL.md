## 1. WSL 概述

> 微软 WSL 官方文档：https://learn.microsoft.com/zh-cn/windows/wsl/

### 1.1. 什么是 WSL？

WSL 是 Windows Subsystem for Linux 的缩写，意思是适用于 Linux 的 Windows 子系统。WSL 可让开发人员直接在 Windows 上按原样运行 GNU/Linux 环境（包括大多数命令行工具、实用工具和应用程序），且不会产生传统虚拟机或双启动设置开销。

WSL 可以用于：

- 在 Microsoft Store 中选择你偏好的 GNU/Linux 分发版。
- 运行常用的命令行软件工具（例如 grep、sed、awk）或其他 ELF-64 二进制文件。
- 运行 Bash shell 脚本和 GNU/Linux 命令行应用程序，包括：
    - 工具：vim、emacs、tmux
    - 语言：NodeJS、Javascript、Python、Ruby、C/C++、C# 与 F#、Rust、Go 等
    - 服务：SSHD、MySQL、Apache、lighttpd、MongoDB、PostgreSQL。
- 使用自己的 GNU/Linux 分发包管理器安装其他软件。
- 使用类似于 Unix 的命令行 shell 调用 Windows 应用程序。
- 在 Windows 上调用 GNU/Linux 应用程序。
- 运行直接集成到 Windows 桌面的 GNU/Linux 图形应用程序
- 将 GPU 加速用于机器学习、数据科学场景等

使用 WSL 的好处是：

- 与在虚拟机下使用 Linux 相比，WSL 占用资源更少，更加流畅；
- WSL 可以对 Windows 文件系统下的文件直接进行读写，文件传输更方便；
- 剪贴板互通，可以直接在 Windows 下其它地方复制文本内容，粘贴到 WSL；

### 1.2. 什么是 WSL 2？

WSL 2 是适用于 Linux 的 Windows 子系统体系结构的一个新版本，它支持适用于 Linux 的 Windows 子系统在 Windows 上运行 ELF64 Linux 二进制文件。它的主要目标是**提高文件系统性能**，以及添加**完全的系统调用兼容性**。

单个 Linux 分发版可以在 WSL 1 或 WSL 2 体系结构中运行。每个分发版可随时升级或降级，并且可以并行运行 WSL 1 和 WSL 2 分发版。WSL 2 使用全新的体系结构，该体系结构受益于运行真正的 Linux 内核。

### 1.3. WSL 版本对比

目前 WSL 的版本分别是：WSL 1 和 WSL 2

WSL 1 和 WSL 2 之间的主要区别在于，在托管 VM 内使用实际的 Linux 内核、支持完整的系统调用兼容性以及跨 Linux 和 Windows 操作系统的性能。 WSL 2 是安装 Linux 发行版时的当前默认版本，它使用最新最好的虚拟化技术在轻量级实用工具虚拟机 (VM) 内运行 Linux 内核。

功能对比表：

|                    功能                     | WSL 1 | WSL 2 |
| ------------------------------------------ | :---: | :---: |
| Windows 和 Linux 之间的集成                  |  ✅   |  ✅   |
| 启动时间短                                   |  ✅   |  ✅   |
| 与传统虚拟机相比，占用的资源量少                |  ✅   |  ✅   |
| 可以与当前版本的 VMware 和 VirtualBox 一起运行 |  ✅   |  ✅   |
| 托管 VM                                     |  ❌   |  ✅   |
| 完整的 Linux 内核                            |  ❌   |  ✅   |
| 完全的系统调用兼容性                          |  ❌   |  ✅   |
| 跨 OS 文件系统的性能                          |  ✅   |  ❌   |

> Notes: WSL 2 适用于 [VMware 15.5.5+](https://blogs.vmware.com/workstation/2020/05/vmware-workstation-now-supports-hyper-v-mode.html) 和 [VirtualBox 6+](https://www.virtualbox.org/wiki/Changelog-6.0)。有关详细信息，请参阅[常见问题解答](https://learn.microsoft.com/zh-cn/windows/wsl/faq#will-i-be-able-to-run-wsl-2-and-other-3rd-party-virtualization-tools-such-as-vmware--or-virtualbox-)。

### 1.4. Microsoft Store 中的 WSL

WSL 已将更新功能从 Windows OS 映像提取到一个包中，该包可通过 Microsoft Store 获得。一旦更新和服务可用就会进行快速更新并提供服务，而无需等待 Windows 操作系统的更新。

WSL 最初作为可选组件包含在 Windows 操作系统中，需要启用该组件才能安装 Linux 发行版。 Microsoft Store 中的 WSL 具有相同的用户体验，并且是相同的产品，但作为商店包而不是整个 OS 更新接收更新和服务。从 Windows 版本 19044 或更高版本开始，运行 `wsl.exe --install` 命令将从 Microsoft Store 安装 WSL 服务更新。 如果已经在使用 WSL，则可以更新以确保通过运行 `wsl.exe --update` 从商店接收最新的 WSL 功能和服务。

## 2. WSL 的基本命令

以下 WSL 命令以 PowerShell 或 Windows 命令提示符支持的格式列出。若要通过 Bash/Linux 发行版命令行运行这些命令，必须将 `wsl` 替换为 `wsl.exe`。若要获取完整的命令列表，请运行 `wsl --help`。

### 2.1. Help 命令

```PowerShell
wsl --help
```

查看 WSL 中可用的选项和命令列表。

### 2.2. 安装

安装 WSL 和 Linux 的默认 Ubuntu 发行版：

```PowerShell
wsl --install
```

安装其他 Linux 发行版：

```PowerShell
wsl --install <Distribution Name>
```

命令选项说明：

- `--distribution`：指定要安装的 Linux 发行版。可以通过运行 `wsl --list --online` 来查找可用的发行版。
- `--no-launch`：安装 Linux 发行版，但不自动启动它。
- `--web-download`：通过联机渠道安装，而不是使用 Microsoft Store 安装。

未安装 WSL 时，选项包括：

- `--inbox`：使用 Windows 组件（而不是 Microsoft Store）安装 WSL。*（WSL 更新将通过 Windows 更新接收，而不是通过 Microsoft Store 中推送的可用更新来接收）。*
- `--enable-wsl1`：在安装 Microsoft Store 版本的 WSL 的过程中也启用“适用于 Linux 的 Windows 子系统”可选组件，从而启用 WSL 1。
- `--no-distribution`：安装 WSL 时不安装发行版。

> Notes: 如果在 Windows 10 或更低版本上运行 WSL，可能需要在 `--install` 命令中包含 `-d` 标志以指定发行版：`wsl --instal -d <distribution name>`。

### 2.3. 查询 Linux 发行版

#### 2.3.1. 列出可用的 Linux 发行版

```PowerShell
wsl --list --online
```

查看可通过在线商店获得的 Linux 发行版列表。此命令也可以简化为：

```PowerShell
wsl -l -o
```

#### 2.3.2. 列出已安装的 Linux 发行版

```PowerShell
wsl --list --verbose
```

查看安装在 Windows 计算机上的 Linux 发行版列表，其中包括状态（发行版是正在运行还是已停止）和运行发行版的 WSL 版本（WSL 1 或 WSL 2）。此命令也可以简化为：

```PowerShell
wsl -l -v
```

#### 2.3.3. 其他选项

可与 list 命令一起使用的其他选项包括：`--all`（列出所有发行版）、`--running`（仅列出当前正在运行的发行版）或 `--quiet`（仅显示发行版名称）。

### 2.4. WSL 设置

#### 2.4.1. 设置 WSL 版本

```PowerShell
wsl --set-version <distribution name> <versionNumber>
```

指定运行 Linux 发行版的 WSL 版本（1 或 2）。参数说明：

- `<distribution name>` 发行版的名称
- `<versionNumber>` 版本号

#### 2.4.2. 设置默认 WSL 版本

```PowerShell
wsl --set-default-version <Version>
```

设置默认使用安装的 Linux 发行版的版本。参数说明：

- `<Version>` 版本号，取值为数字 1 或 2。

例如设置默认使用 WSL 2 版本，`wsl --set-default-version 2`。

#### 2.4.3. 设置默认 Linux 发行版

```PowerShell
wsl --set-default <Distribution Name>
```

设置 WSL 命令将用于运行的默认 Linux 发行版。参数说明：

- `<Distribution Name>` Linux 发行版的名称。

#### 2.4.4. 设置发行版的默认用户

```PowerShell
<DistributionName> config --default-user <Username>
```

设置用于发行版登录的默认用户。注意：用户必须已经存在于发行版中才能成为默认用户。例如：

```PowerShell
ubuntu config --default-user johndoe
```

将 Ubuntu 发行版的默认用户更改为“johndoe”用户。

> 警告：此命令不适用于导入的发行版，因为这些发行版没有可执行启动器。可以改为使用 `/etc/wsl.conf` 文件来更改导入的发行版的默认用户。

### 2.5. WSL 操作

#### 2.5.1. 通过 PowerShell 或 CMD 运行特定的 Linux 发行版

```PowerShell
wsl --distribution <Distribution Name> --user <User Name>
```

通过特定用户运行特定 Linux 发行版。参数说明：

- `<Distribution Name>` 指定 Linux 发行版的名称（例如 Debian）
- `<User Name>` 现有用户的名称（例如 root）。如果指定 WSL 发行版中不存在该用户，将报错误。

> Tips: 通过 `whoami` 命令可以输出当前用户名。

#### 2.5.2. 更新 WSL

```PowerShell
wsl --update
```

更新 WSL 到最新版本。选项包括：

- `--web-download`：从 GitHub 而不是 Microsoft Store 下载最新更新。

#### 2.5.3. 检查 WSL 状态

```PowerShell
wsl --status
```

查看有关 WSL 配置的常规信息，例如默认发行版类型、默认发行版和内核版本。

#### 2.5.4. 检查 WSL 版本

```PowerShell
wsl --version
```

检查有关 WSL 及其组件的版本信息。

#### 2.5.5. 以特定用户的身份运行

```PowerShell
wsl -u <Username>
# 或者
wsl --user <Username>
```

指定用户身份运行 WSL。参数说明：

- `<Username>` WSL 发行版中存在的用户名

#### 2.5.6. 关闭 WSL

```PowerShell
wsl --shutdown
```

立即终止所有正在运行的发行版和 WSL 2 轻量级实用工具虚拟机。例如更改内存使用限制或更改 .wslconfig 文件等需要重启 WSL 2 虚拟机环境的情形时，必须使用此命令。

#### 2.5.7. 终止运行 WSL

```PowerShell
wsl --terminate <Distribution Name>
```

终止或阻止运行指定的发行版。参数说明：

- `<Distribution Name>` 目标发行版的名称。

#### 2.5.8. 将目录更改为主页

```PowerShell
wsl ~
```

`~` 可与 wsl 一起使用，以在用户的主目录中启动。若要在 WSL 命令提示符中从任何目录跳回到主目录，可使用命令 `cd ~`。

### 2.6. 导入和导出发行版

```PowerShell
# 导出
wsl --export <Distribution Name> <FileName>
# 导入
wsl --import <Distribution Name> <InstallLocation> <FileName>
```

将指定 tar 文件导入和导出为新的发行版。在标准输入中，文件名可以是 `-`。选项包括：

- `--vhd`：指定导入/导出发行版应为 .vhdx 文件，而不是 tar 文件
- `--version`：（仅导入）指定将发行版导入为 WSL 1 还是 WSL 2 发行版

也可以将指定的 .vhdx 文件导入为新的发行版。虚拟硬盘必须采用 ext4 文件系统类型格式：

```PowerShell
wsl --import-in-place <Distribution Name> <FileName>
```

### 2.7. 注销或卸载 Linux 发行版

可以通过 Microsoft Store 安装 Linux 发行版，但无法通过 Store 将其卸载。注销并卸载 WSL 发行版需要执行以下命令：

```PowerShell
wsl --unregister <DistributionName>
```

参数说明：

- `<DistributionName>` 指定目标 Linux 发行版的名称，将从 WSL 取消注册该发行版，以便可以重新安装或清理它。

> **警告**：取消注册后，与该分发版关联的所有数据、设置和软件将永久丢失。从 Store 重新安装会安装分发版的干净副本。例如：`wsl --unregister Ubuntu` 将从可用于 WSL 的发行版中删除 Ubuntu。运行 `wsl --list` 将会显示它不再列出。

还可以像卸载任何其他应用商店应用程序一样卸载 Windows 计算机上的 Linux 发行版应用。若要重新安装，请在 Microsoft Store 中找到该发行版，然后选择“启动”。

### 2.8. 装载磁盘或设备

```PowerShell
wsl --mount <DiskPath>
```

通过将 `<DiskPath>` 指为物理磁盘所在的目录 `\` 文件路径，在所有 WSL2 发行版中附加和装载该磁盘。参数说明：

- `--vhd`：指定 `<Disk>` 引用虚拟硬盘。
- `--name`：使用装入点的自定义名称装载磁盘。
- `--bare`：将磁盘附加到 WSL2，但不进行装载。
- `--type <Filesystem>`：装载磁盘时使用的文件系统类型默认为 ext4（如果未指定）。此命令也可输入为：`wsl --mount -t <Filesystem>`。可以使用 `blkid <BlockDevice>` 命令检测文件系统类型，例如：`blkid <dev/sdb1>`。
- `--partition <Partition Number>`：要装载的分区的索引号默认为整个磁盘（如果未指定）。
- `--options <MountOptions>`：装载磁盘时，可以包括一些特定于文件系统的选项。例如，`wsl --mount -o "data-ordered"` 或 `wsl --mount -o "data=writeback` 之类的 ext4 装载选项。但是，目前仅支持特定于文件系统的选项。不支持通用选项，例如 `ro`、`rw` 或 `noatime`。

> Notes: 如果运行的是 32 位进程来访问 wsl.exe（一种 64 位工具），那么可能需要按如下方式运行此命令：`C:\Windows\Sysnative\wsl.exe --command`。

### 2.9. 卸载磁盘

```PowerShell
wsl --unmount <DiskPath>
```

卸载磁盘路径中给定的磁盘，如果未提供磁盘路径，则此命令将卸载并分离所有已装载的磁盘。

## 3. 参考资料

- [WSL Linux 子系统完整实操](https://zhuanlan.zhihu.com/p/146545159)
