# Linux 系统基础笔记

## 1. Linux 概述

### 1.1. Unix 介绍

Unix 是一个强大的多用户、多任务操作系统

### 1.2. Linux 介绍

- Linux 是一套免费的类 Unix 操作系统
- Linux 是一套免费使用和自由传播的类 Unix 操作系统,可以任意修改其源代码。
- Linux 存在着许多不同的 Linux 版本，但它们都使用了 Linux 内核。

Linux 的特点：它是多用户，多任务，丰富的网络功能，可靠的系统安全，良好的可移植性，具有标准兼容性，良好的用户界面，出色的速度性能

- Linux 的基本思想有两点：
    1. <font color=red>**一切都是文件**</font>
    2. 每个软件都有确定的用途

全字符界面，需要任何页面效果渲染，速度快。linux安全性好，开源

比如：window系统安装tomcat软件，网络中的其他的电脑默认都可以访问tomcat。linux如果安装了tomcat，网络中的其他的电脑默认是无法访问tomcat的，必须要通过设置相关操作才可以访问。所以linux安全性方面比window做的好，从这点可以看出安全性高的linux适合做服务器，安全性低的window适合普通个人用户使用。


### 1.3. Linux 系统的应用

- 服务器系统：<font color=red>**Web 应用服务器**</font>、数据库服务器、接口服务器、DNS、FTP 等等；
- 嵌入式系统：路由器、防火墙、手机、PDA、IP 分享器、交换器、家电用品的微电脑控制器等等，高性能运算、计算密集型应用：Linux 有强大的运算能力。
- 桌面应用系统
- 移动手持系统：android底层就是linux系统

### 1.4. Linux 的版本

Linux 的版本分为两种：内核版本(https://www.kernel.org/)和发行版本。

- <font color=red>**内核版本**</font>是指在 Linus 领导下的内核小组开发维护的系统内核的版本号
- <font color=red>**发行版本**</font>是一些组织和公司根据自己发行版的不同而自定的

目前学习使用的版本 CentOS是 Linux 发行版之一。Red Hat Enterprise Linux 提供的源代码编译而成。很多公司使用 CentOS 替代商业版 RedHat Linux

# 其他

## 1. 使用rm -f误删除后如何恢复（网上资料，待实践）

### 1.1. 模拟场景-删除

误删除服务器目录/root/selenium/Spider下的MySql.Data.dll文件：

```shell
> rm -f /root/selenium/Spider/MySql.Data.dll
> ll /root/selenium/Spider/MySql.Data.dll
ls: cannot access /root/selenium/Spider/MySql.Data.dll: No such file or directory
```

### 1.2. 恢复

1. 使用lsof命令查看当前是否有进程打开/root/selenium/Spider/MySql.Data.dll文件：

```shell
> lsof | grep /root/selenium/Spider/MySql.Data.dll
```

从上面可以看出，当前文件状态为已删除（deleted）。

2. 查看是否存在恢复数据：

> /proc/13067/fd：进程操作的文件描述符目录。  
> 86：文件描述符。

```shell
> cat /proc/13067/fd/86
```

3. 使用I/O重定向恢复文件：

```shell
> cat /proc/23778/fd/86 > /root/selenium/Spider/MySql.Data.dll
> ls -l /root/selenium/Spider/MySql.Data.dll
-rw-r--r-- 1 root root 702464 Feb 10 12:03 /root/selenium/Spider/MySql.Data.dll
```

重新运行程序：说明恢复的文件没有问题。

### 1.3. 分析

- 通过前面的模拟场景演示了恢复文件的整个过程，那么原理是什么，在什么情况下，文件才是可恢复的。
- 在Linux系统中，每个运行中的程序都有一个宿主进程彼此隔离，以/proc/进程号来体现（Linux本质上就是一个文件系统）。
- 比如：`ls -l /proc/13067` 查看进程PID为13067的进程信息；当程序运行时，操作系统会专门开辟一块内存区域，提供给当前进程使用，对于依赖的文件，操作系统会发放一个文件描述符，以便读写文件，当我们执行 rm -f 删除文件时，其实只是删除了文件的目录索引节点，对于文件系统不可见，但是对于打开它的进程依然可见，即仍然可以使用先前发放的文件描述符读写文件，正是利用这样的原理，所以我们可以使用I/O重定向的方式来恢复文件。

### 1.4. 总结

如果不小心误删了文件，不要着急，首先使用 `lsof` 查看打开该文件的进程，然后再使用 `cat /proc/进程号/fd/文件描述符` 查看恢复数据，最后使用I/O重定向的方式来恢复文件。

