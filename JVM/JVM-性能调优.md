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
- `-XX:SurvivorRatio=8`：设置新生代 Eden 和 Survivor 比例为 8:2，具体 `Edem : from : to = 8 : 1 : 1`；
- `–XX:+UseParNewGC`：指定使用 ParNew + Serial Old 垃圾回收器组合；
- `-XX:+UseParallelOldGC`：指定使用 ParNew + ParNew Old 垃圾回收器组合；
- `-XX:+UseConcMarkSweepGC`：指定使用 CMS + Serial Old 垃圾回收器组合；
- `-XX:+PrintGC`：开启打印 gc 信息；
- `-XX:+PrintGCDetails`：打印 gc 详细信息。

### 2.1. -XX:+UseCompressedOops

当应用从 32 位的 JVM 迁移到 64 位的 JVM 时，由于对象的指针从 32 位增加到了 64 位，因此堆内存会突然增加差不多翻倍，这也会对 CPU 缓存（容量比内存小很多）的数据产生不利的影响。

迁移到 64 位的 JVM 主要动机在于可以指定最大堆大小，通过压缩 OOP 可以节省一定的内存。通过 `-XX:+UseCompressedOops` 选项，JVM 会使用 32 位的 OOP，而不是 64 位的 OOP。

## 3. 常用的 JVM 调优命令

### 3.1. jps（虚拟机进程状况工具）

`jps`：JVM Process Status Tool，列出本机所有正在运行的虚拟机（Java）进程。显示执行主类(Main Class, main()函数所在的类）的名称，以及进程的本地虚拟机的唯一ID。常用参数如下：

- `-m` 输出虚拟机进程启动时传递给主类 main 方法的参数。
- `-l` 输出完整的包名和应用主类名。如果进程执行是 jar 包，输出 jar 路径。
- `-v` 输出虚拟机进程启动时 JVM 参数。
- `-q` 只输出lvmid，省略主类的名称

```bash
jps -lvm
```

### 3.2. jstack

`jstack`：查看某个 Java 进程内当前时刻的线程堆栈信息。使用参数 `-l` 可以打印额外的锁信息，发生死锁时可以使用 `jstack -l pid` 观察锁持有情况。

```bash
jstack -l 4124 | more
```

### 3.3. jstat（虚拟机统计信息工具）

`jstat`：JVM statistics Monitoring，用于监视虚拟机各种**运行状态信息（类装载、内存、垃圾收集、JIT编译等运行数据）**。命令语法如下：

```bash
jstat [option vmid [interval[s|ms] [count]] ]
```

参数说明：

- `option` 代表用户希望查询的虚拟机信息，主要分3类：
    - 类装载
    - 垃圾收集
    - 运行期编译状况
- `vmid` 虚拟机id
- `interval` 查询间隔
- `count` 查询次数

> Tips: 如果不指定 `interval` 与 `count` 此两个参数，就默认查询一次。

#### 3.3.1. option 选项取值说明

- `-class` 监视类装载、卸载数量、总空间及类装载锁消耗的时间
- `-gc` 监视 Java 堆状况，包括 Eden 区，2 个 survivor 区、老年代
- `-gccapacity` 监视内容与 `-gc` 基本相同，但输出主要关注Java堆各个区域使用的最大和最小空间
- `-gcutil` 监视内容与 `-gc` 基本相同，主要关注已经使用空间站空间百分比
- `-gccause` 与 `-gcutil` 功能一样，但是会额外输出导致上一次 GC 产生的原因
- `-gcnew` 监视新生代的 GC 的状况
- `-gcnewcapacity` 监视内容与 `-gcnew` 基本相同，输出主要关注使用到的最大最小空间
- `-gcold` 监视老年代的 GC 情况
- `-gcoldcapacity` 监控内容与 `-gcold` 基本相同，主要关注使用到的最大最小空间
- `-compiler` 输出 JIT 编译器编译过的方法、耗时等信息

#### 3.3.2. 示例及输出信息说明

例如使用参数 `-gcuitl` 可以查看垃圾回收的统计信息。

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

### 3.4. jmap（内存映像工具）

`jmap`：JVM Memory Map，查看运行中的堆内存的快照（heap dump 文件），从而可以对堆内存进行离线分析。例如，查询进程 4124

```bash
jmap -heap 4124
```

### 3.5. jhat

`jhat`：JVM Heap Analysis Tool，此命令是与 `jmap` 搭配使用，用来分析 `jmap` 生成的 dump，`jhat` 内置了一个微型的 HTTP/HTML 服务器，生成 dump 的分析结果后，可以在浏览器中查看

### 3.6. jinfo（配置信息工具）

`jinfo`：JVM Configuration info，实时查看当前的应用 JVM 运行参数配置。语法格式：

```bash
jinfo [option] pid
```

例如：

```bash
jinfo -flags 1
```

> Tips: 与 `jps -v` 命令比较，该只是查看虚拟机启动时显式指定的参数列表。

### 3.7. 查询 JVM 相关参数值

- 查看所有参数的最终值，*初始值可能被修改掉*。

```bash
java -XX:+PrintFlagsFinal -version
```

- 查看所有参数的初始值

```bash
java -XX:+PrintFlagsInitial
```
