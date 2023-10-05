## 1. JDK 概述

JDK (Java Development Kit) 是 Java 语言的软件开发工具包(SDK)，主要用于移动设备、嵌入式设备上的 Java 应用程序。JDK 是整个 Java 开发的核心，它包含了 JAVA 的运行环境（JVM+Java 系统类库）和 JAVA 工具。

> JDK 官网：https://www.oracle.com/java/

## 2. windows 系统安装 JDK

### 2.1. JDK变量环境配置

最好的配置方式：将位置切割成两段，一段用`JAVA_HOME`保存，一段用\bin保存。如下例：

```
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_91
%JAVA_HOME%\bin  相等于 --> C:\Program Files\Java\jdk1.8.0_91\bin
```

![](images/20220114141342322_31662.jpg)

### 2.2. 安装多个 JDK

安装过程都一样。只是配置环境变量时改动一下。分别将多个不同版本的jdk设置一个环境变量，然后最终让`JAVA_HOME`指定当前需要使用的版本的变量即可

```
JAVA_HOME_8 = D:\development\Java\jdk1.8.0_311
JAVA_HOME_11 = D:\development\Java\jdk-11.0.13
JAVA_HOME = %JAVA_HOME_8%
```

![](images/20220114152301619_11430.png)

### 2.3. JDK 11 手动生成 jre 目录

许多java软件的运行需要依赖jre，但是在安装jdk11后，发现jdk11并没有自动安装jre环境。其实 jdk11 的安装包里是自带 jre 的，只不过没有自动安装，手动安装一下就可以了。

使用cmd命令行窗口进入jdk 安装目录输入以下命令，即可生成 jre 目录：

```bash
bin\jlink.exe --module-path jmods --add-modules java.desktop --output jre
```

### 2.4. 注意事项

如果是安装版，请务必到以下位置删除这几个文件。(二者其一有)

> - C:\Program Files (x86)\Common Files\Oracle\Java\javapath
> - C:\Program Files\Common Files\Oracle\Java\javapath

如果不删除上面这些文件，直接到环境变量PATH，将下面这些引用删除即可：

```
C:\Program Files\Common Files\Oracle\Java\javapath
C:\Program Files (x86)\Common Files\Oracle\Java\javapath
C:\ProgramData\Oracle\Java\javapath
```

## 3. Linux 系统安装 JDK

详见《Linux》相关的笔记

## 4. Oracle JDK 与 Open JDK

Oracle JDK 是基于 Open JDK 源代码的商业版本。要学习 Java 新技术可以去 Open JDK 官网学习。

> Open JDK 官网：http://openjdk.java.net/

JDK Enhancement Proposals(JDK增强建议)。通俗的讲JEP就是JDK的新特性

## 5. JDK 版本号的选择

Oracle JDK 8u211 及以上版本进行具有商业用途(盈利目的)的应用或工具的开发时是要收费的

JDK8 最后一个免费版本，JDK 8u202。但推荐下载 JDK 8u201？！说好的最后一个免费版本，为什么写了两个版本号 8u201 和 8u202 呢？到底要用哪一个？

这就涉及到 Oracle 跟 Oracle JDK 的使用者之间的一个小小的约定或小常识了！**下载奇数版本！！！**

从 2014 年 10 月发布 Java SE 7 Update 71 (Java SE 7u71) 开始，Oracle 在发布 Oracle JDK 关键补丁更新 (CPUs：Critical Patch Updates) 的同时一般会发布相应的补丁集更新 (PSUs：Patch Set Updates)。那么 CPUs 和 PSUs 之间有什么区别呢？

Oracle JDK 关键补丁更新 (CPUs) 包含安全漏洞修复和重要漏洞修复，Oracle 强烈建议所有 Oracle JDK 用户及时升级到最新的 CPU 版本，Oracle JDK 关键补丁更新 (CPUs) 版本号采用奇数编号！

Oracle JDK 补丁集更新 (PSUs) 包含相应 CPUs 中的所有修复以及其他非重要修复，仅当您受到Oracle JDK关键补丁更新 (CPUs)版本之外的其他漏洞的影响时才应当使用相应的补丁集更新 (PSUs) ，Oracle JDK 补丁集更新 (PSUs) 版本号采用偶数编号！

所以，一般情况下只要下载奇数编号的最新版本更新就行了！但要记住：商业收费版本的不要用于商业用途！

## 6. 综合扩展

### 6.1. JRE、JDK、JVM 及 JIT 的区别

- JRE（Java run-time） 是 Java 运行时环境，是运行 Java 程序所必须的。
- JDK（Java development kit）是 Java 程序开发工具集，如 Java 编译器，它也包含 JRE。
- JVM（Java virtual machine）是 Java 虚拟机，它的责任是运行 Java 应用。
- JIT（Just In Time compilation）是即时编译，当代码执行的次数超过一定的阈值时，会将 Java 字节码转换为本地代码，如，主要的热点代码会被准换为本地代码，这样有利大幅度提高 Java 应用的性能。

### javap 反编译工具（待整理）

> 参考：[Java编程教程-理解javap工具](https://mp.weixin.qq.com/s/5iK2uuBtG6Amkc3b0SdQvw)
