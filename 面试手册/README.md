> 以下是个人整理的面试相关资料，参考网络上的面试题库，再增加个人总结。

## 1. Java 专题

<ul class="docs">
  <li><a href="#/面试手册/面试题-Java基础">Java 基础面试题</a></li>
  <li><a href="#/面试手册/面试题-Java多线程">Java 多线程面试题</a></li>
  <li><a href="#/面试手册/面试题-JVM">Java虚拟机(JVM)面试题</a></li>
</ul>

## 2. 技术框架专题

<ul class="docs">
  <li><a href="#/面试手册/面试题-Spring">Spring 面试题</a></li>
</ul>

## 3. 数据库专题

<ul class="docs">
  <li><a href="#/面试手册/面试题-MySQL">MySQL 面试题</a></li>
  <li><a href="#/面试手册/面试题-Redis">Redis 面试题</a></li>
</ul>

## 4. 分布式与微服务

<ul class="docs">
  <li><a href="#/面试手册/面试题-分布式微服务">分布式微服务系统面试题</a></li>
  <li><a href="#/面试手册/面试题-Dubbo">Dubbo 面试题</a></li>
</ul>

## 5. 面试建议

### 5.1. 面试整体事项

1. 简历要准备好，联系方式一定要正确清晰醒目，项目经历按照时间倒序阐述，注意描述自己在项目中承担的职责，简历的模板尽量选择简洁的，毕竟程序员大部分还是喜欢简单明了的。
2. 推荐boss直聘，我觉得很好用（不是广告）。
3. 一般的整体面试流程都是电面->现场面->HR面->等着。
4. 不要觉得HR说让你回去等消息就是GG了，他们也要跟你之前的面试官讨论，再向领导汇报，如果说不急可能还要和其他候选人比较，所以HR让你回去等消息绝对不是说明你完蛋了。
5. 面试前准备好自我介绍，1分钟左右就可以，可以写在纸上，电面可以照着念，等你到了现场面了基本也都快背下来你的自我介绍了。
6. 准备好扎实的基础，这是一切的根源，没实力怎么都没用的。
7. 面试中你可以把你的面试官往你会的知识上引导。
8. 遇到了设计类题目不要着急，面试官不是为了让你几分钟设计一个高并发高可用设计模式完美的架构，只是想看看你的思路，看看你应变的能力，然后给你些提示看看你能否迅速的调整。
9. 不要着急，把面试当成一个交流的过程。

### 5.2. 自我介绍模版（参考）

自我介绍包含的信息点：基本信息，教育背景、工作经历、项目经历等。

**例子1**：

我叫xxx来自广东xx，今年xx岁，14年毕业xx学校本科xx专业，毕业至今已有x年工作经验，其中java开发x年，在java开发期间任职于x家公司，第一家公司是xxx，这家公司是做xxx的，在java这一块我主要负责/参与的是xxx项目，在这个项目中主要负责xxx功能模块，目前这个项目处于xxx阶段。在这家公司我收获成长最大的是xxx（技术和做事方面阐述），之所以离开这家公司是因为xxx,第二家公司也就是我上一任公司xxx，这家公司是做xxx，我在这家公司主要负责的是xxx，我在这家公司的收获是xxx，离开这家公司是因为xxx，以上这些事我工作方面的经历，在生活上我也是一个xxx样的人，喜欢xxx,我个人最大的优点是xxx(要举例子)，最大的缺点是xxx（举例子）。综合以上就是我个人的自我介绍，今天非常荣幸能来到贵公司面试。

参考网上一些大佬的建议：

我现在的个人介绍一般会包括：个性（比如偏安静）、做事风格（工作认真严谨、注重质量、善于整体思考）、最大优势（owner 意识、执行力、工程把控能力）、工作经历简述（在每个公司的工作负责什么、贡献了什么、收获了什么）。个人介绍简明扼要，无需赘言。

**例子2**：

面试官，您好！我叫xxx，目前有xx年的工作经验，熟练使用 Spring、MyBatis 等框架、了解 Java 底层原理比如 JVM 调优并且有着丰富的分布式开发经验。离开上一家公司是因为我想在技术上得到更多的锻炼。在上一个公司我参与了一个分布式xxxx系统的开发，负责搭建一整个项目的基础架构并且通过分库分表解决了原始数据库以及一些相关表过于庞大的问题，目前这入网站最高支持10万人同时访问。工作之余，我利用自已的业余时间写了一个简单的RPC框架，这个框架用到了Netty进行网络通信，自前我已经将这个项自开源，在GitHub上收获了2k的Star！说到业余爱好的话，我比较喜欢通过博客整理分享自已所学知识，现在已经是多个博客平台的认证作者。生活中我是一个比较积极乐观的人，一般会通过运动打球的方式来放松。我一直都非常想加入贵公司，我觉得贵公司的文化和技术氛围我都非常喜欢，期待能与你共事！

### 5.3. 一些小建议

1. 可以去leetcode上刷题换换思路。
2. 八大排序算法一定要手敲一遍（快排，堆排尤其重要）。
3. 了解一些新兴的技术。如
    1. JDK11的新特性，有什么优势。
    2. 区块链了解
    3. 如何设计双11交易总额面板，要做到高并发高可用。
4. 面试之后面试官都会问你有没有什么问题，千万不要没问题，也别傻乎乎的问一些敏感问题。
5. 了解你要面试的公司的产品及竞争产品。

## 6. 待整理的面试题

### 6.1. Java基础

1. HaspMap扩容是怎样扩容的，为什么都是2的N次幂的大小。
2. HashMap，HashTable，ConcurrentHashMap的区别。
3. 极高并发下HashTable和ConcurrentHashMap哪个性能更好，为什么，如何实现的。
4. HashMap在高并发下如果没有处理线程安全会有怎样的安全隐患，具体表现是什么。

### 6.2. 数据结构和算法

1. B+树
2. 快速排序，堆排序，插入排序（其实八大排序算法都应该了解
3. 一致性Hash算法，一致性Hash算法的应用

### 6.3. JVM

1. JVM的内存结构。
2. JVM方法栈的工作过程，方法栈和本地方法栈有什么区别。
3. JVM的栈中引用如何和堆中的对象产生关联。
4. 可以了解一下逃逸分析技术。
5. GC的常见算法，CMS以及G1的垃圾回收过程，CMS的各个阶段哪两个是Stop the world的，CMS会不会产生碎片，G1的优势。
6. 标记清除和标记整理算法的理解以及优缺点。
7. eden survivor区的比例，为什么是这个比例，eden survivor的工作过程。
8. JVM如何判断一个对象是否该被GC，可以视为root的都有哪几种类型。
9. 强软弱虚引用的区别以及GC对他们执行怎样的操作。
10. Java是否可以GC直接内存。
11. Java类加载的过程。
12. 双亲委派模型的过程以及优势。
13. 常用的JVM调优参数。
14. dump文件的分析。
15. Java有没有主动触发GC的方式（没有）。

### 6.4. 多线程

2. Callable和Future的了解。
3. 线程池的参数有哪些，在线程池创建一个线程的过程。
4. volitile关键字的作用，原理。
5. synchronized关键字的用法，优缺点。
6. Lock接口有哪些实现类，使用场景是什么。
7. 可重入锁的用处及实现原理，写时复制的过程，读写锁，分段锁（ConcurrentHashMap中的segment）。
8. 悲观锁，乐观锁，优缺点，CAS有什么缺陷，该如何解决。
9. ABC三个线程如何保证顺序执行。
10. 线程的状态都有哪些。
11. sleep和wait的区别。
12. notify和notifyall的区别。
13. ThreadLocal的了解，实现原理。

### 6.5. 数据库相关

1. 常见的数据库优化手段
2. 索引的优缺点，什么字段上建立索引
3. 数据库连接池。
4. durid的常用配置。

### 6.6. 计算机网络

1. TCP，UDP区别。
2. 三次握手，四次挥手，为什么要四次挥手。
3. 长连接和短连接。
4. 连接池适合长连接还是短连接。

### 6.7. 设计模式

1. 观察者模式
2. 代理模式
3. 单例模式，有五种写法，可以参考文章单例模式的五种实现方式
4. 可以考Spring中使用了哪些设计模式

### 6.8. 分布式相关

1. 分布式事务的控制。
2. 分布式锁如何设计。
3. 分布式session如何设计。
4. dubbo的组件有哪些，各有什么作用。
5. zookeeper的负载均衡算法有哪些。
6. dubbo是如何利用接口就可以通信的。

### 6.9. 缓存相关

1. redis和memcached的区别。
2. redis支持哪些数据结构。
3. redis是单线程的么，所有的工作都是单线程么。
4. redis如何存储一个String的。
5. redis的部署方式，主从，集群。
6. redis的哨兵模式，一个key值如何在redis集群中找到存储在哪里。
7. redis持久化策略。

### 6.10. 框架相关

1. SpringMVC的Controller是如何将参数和前端传来的数据一一对应的。
2. Mybatis如何找到指定的Mapper的，如何完成查询的。
3. Quartz是如何完成定时任务的。
4. 自定义注解的实现。
5. Spring使用了哪些设计模式。
6. Spring的IOC有什么优势。
7. Spring如何维护它拥有的bean。

## 7. 高频技术问题：

- 基础：数据结构与算法、网络；
- 微服务：技术体系、组件、基础设施等；
- Dubbo：Dubbo 整体架构、扩展机制、服务暴露、引用、调用、优雅停机等；
- MySQL：索引与事务的实现原理、SQL 优化、分库分表；
- Redis : 数据结构、缓存、分布式锁、持久化机制、复制机制；
- 分布式：分布式事务、一致性问题；
- 消息中间件：原理、对比；
- 架构： 架构设计方法、架构经验、设计模式；
- 性能优化： JVM、GC、应用层面的性能优化；
- 并发基础：ConcurrentHashMap, AQS, CAS，线程池等；
- 高并发：IO 多路复用；缓存问题及方案；
- 稳定性：稳定性的思想及经验；
- 生产问题：工具及排查方法。

### 7.1. 中高端职位

说起来，我这人可能有点不太自信。我是怀着“踏实做一个工程师”的思想投简历的。

对于大龄程序员，企业的期望更高。我的每一份“高级工程师”投递，自动被转换为“技术专家”或“架构师”。无力反驳，倍感压力。面试中高端职位，需要更多准备：

- 你有带团队经历吗？
- 在你 X 年的工作经历中，有多少时间用于架构设计？
- 架构过程是怎样的？你有哪些架构设计思想或方法论？
