## 1. JVM 性能调优工具

JDK 自带了很多监控工具，都位于 JDK 的 bin 目录下，其中最常用的是 jconsole 和 jvisualvm 这两款视图监控工具。第三方有：MAT(Memory Analyzer Tool)、GChisto。

- jconsole（Java Monitoring and Management Console）：从 Java5 开始，在 JDK 中自带的 java 监控和管理控制台。用于对 JVM 中的内存、线程和类等进行监控
- jvisualvm：JDK 自带的全能分析工具，可以分析内存快照、线程快照、程序死锁、监控内存的变化、gc 变化等。
- MAT（Memory Analyzer Tool）：一个基于 Eclipse 的内存分析工具，是一个快速、功能丰富的 Javaheap 分析工具，可以帮助开发者查找内存泄漏和减少内存消耗。
- GChisto：一款专业分析 gc 日志的工具

## 2. 常用的 JVM 调优的参数

- Java SE 8 版本 JVM 虚拟机参数设置参考文档地址：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html

- `-Xms2g`：初始化推大小为 2g；
- `-Xmx2g`：堆最大内存为 2g；
- `-XX:NewSize=1`：设置新生代大小
- `-XX:NewRatio=4`：设置年轻的和老年代的内存比例为 1:4；
- `-XX:SurvivorRatio=8`：设置新生代 Eden 和 Survivor 比例为 8:2；
- `–XX:+UseParNewGC`：指定使用 ParNew + Serial Old 垃圾回收器组合；
- `-XX:+UseParallelOldGC`：指定使用 ParNew + ParNew Old 垃圾回收器组合；
- `-XX:+UseConcMarkSweepGC`：指定使用 CMS + Serial Old 垃圾回收器组合；
- `-XX:+PrintGC`：开启打印 gc 信息；
- `-XX:+PrintGCDetails`：打印 gc 详细信息。

### 2.1. -XX:+UseCompressedOops

当应用从 32 位的 JVM 迁移到 64 位的 JVM 时，由于对象的指针从 32 位增加到了 64 位，因此堆内存会突然增加差不多翻倍，这也会对 CPU 缓存（容量比内存小很多）的数据产生不利的影响。

迁移到 64 位的 JVM 主要动机在于可以指定最大堆大小，通过压缩 OOP 可以节省一定的内存。通过 `-XX:+UseCompressedOops` 选项，JVM 会使用 32 位的 OOP，而不是 64 位的 OOP。

## 3. 常用的 JVM 调优命令

### 3.1. jps

`jps`：JVM Process Status Tool，列出本机所有 Java 进程的进程号。常用参数如下：

- `-m` 输出 main 方法的参数
- `-l` 输出完全的包名和应用主类名
- `-v` 输出 JVM 参数

```bash
jps -lvm
```

### 3.2. jstack

`jstack`：查看某个 Java 进程内当前时刻的线程堆栈信息。使用参数 `-l` 可以打印额外的锁信息，发生死锁时可以使用 `jstack -l pid` 观察锁持有情况。

```bash
jstack -l 4124 | more
```

### 3.3. jstat

`jstat`：JVM statistics Monitoring，用于监视虚拟机各种**运行状态信息（类装载、内存、垃圾收集、JIT编译等运行数据）**。使用参数 `-gcuitl` 可以查看垃圾回收的统计信息。

```bash
jstat -gcutil 4124

S0 S1 E O M CCS YGC YGCT FGC FGCT GCT
0.00 0.00 67.21 19.20 96.36 94.96 10 0.084 3 0.191 0.275
```

> 参数说明：
>
> - S0： Survivor0 区当前使用比例
> - S1： Survivor1 区当前使用比例
> - E： Eden 区使用比例
> - O：老年代使用比例
> - M：元数据区使用比例
> - CCS：压缩使用比例
> - YGC：年轻代垃圾回收次数
> - FGC：老年代垃圾回收次数
> - FGCT：老年代垃圾回收消耗时间
> - GCT：垃圾回收消耗总时间

### 3.4. jmap

`jmap`：JVM Memory Map，查看运行中的堆内存的快照（heap dump 文件），从而可以对堆内存进行离线分析。例如，查询进程 4124

```bash
jmap -heap 4124
```

### 3.5. jhat

`jhat`：JVM Heap Analysis Tool，此命令是与 `jmap` 搭配使用，用来分析 `jmap` 生成的 dump，`jhat` 内置了一个微型的 HTTP/HTML 服务器，生成 dump 的分析结果后，可以在浏览器中查看

### 3.6. jinfo

`jinfo`：JVM Configuration info，实时查看当前的应用 JVM 运行参数配置。

```bash
jinfo -flags 1
```

### 3.7. 查询 JVM 相关参数值

- 查看所有参数的最终值，*初始值可能被修改掉*。

```bash
java -XX:+PrintFlagsFinal -version
```

- 查看所有参数的初始值

```bash
java -XX:+PrintFlagsInitial
```
