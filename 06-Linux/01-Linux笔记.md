# 01-Linux 系统笔记

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

