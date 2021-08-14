# Redis 笔记

## 1. NoSQL 概述

### 1.1. 什么是NoSQL

NoSQL(NoSQL = Not Only SQL)，意即“不仅仅是SQL”，是一项全新的数据库理念，泛指非关系型的数据库。

关系型数据库指的是，表与表之间有主外键，这种表之间有关系的数据，我们叫做关系型数据库。

### 1.2. 为什么需要NoSQL

随着互联网web2.0网站的兴起，非关系型的数据库现在成了一个极其热门的新领域，非关系数据库产品的发展非常迅速。而传统的关系数据库在应付web2.0网站，特别是超大规模和高并发的SNS类型的web2.0纯动态网站已经显得力不从心，暴露了很多难以克服的问题，例如：

1. **High performance - 对数据库高并发读写的需求**
    - web2.0网站要根据用户个性化信息来实时生成动态页面和提供动态信息，所以基本上无法使用动态页面静态化技术，因此数据库并发负载非常高，往往要达到每秒上万次读写请求。关系数据库应付上万次SQL查询还勉强顶得住，但是应付上万次SQL写数据请求，硬盘IO就已经无法承受了。其实对于普通的BBS网站，往往也存在对高并发写请求的需求，例如网站的实时统计在线用户状态，记录热门帖子的点击次数，投票计数等，因此这是一个相当普遍的需求。
2. **Huge Storage - 对海量数据的高效率存储和访问的需求**
    - 类似Facebook，twitter，Friendfeed这样的SNS网站，每天用户产生海量的用户动态，以Friendfeed为例，一个月就达到了2.5亿条用户动态，对于关系数据库来说，在一张2.5亿条记录的表里面进行SQL查询，效率是极其低下乃至不可忍受的。再例如大型web网站的用户登录系统，例如腾讯，盛大，动辄数以亿计的帐号，关系数据库也很难应付。
3. **High Scalability && High Availability - 对数据库的高可扩展性和高可用性的需求**
    - 在基于web的架构当中，数据库是最难进行横向扩展的，当一个应用系统的用户量和访问量与日俱增的时候，你的数据库却没有办法像web server和app server那样简单的通过添加更多的硬件和服务节点来扩展性能和负载能力。对于很多需要提供24小时不间断服务的网站来说，对数据库系统进行升级和扩展是非常痛苦的事情，往往需要停机维护和数据迁移，为什么数据库不能通过不断的添加服务器节点来实现扩展呢？

**NoSQL数据库的产生就是为了解决大规模数据集合多重数据种类**带来的挑战，尤其是大数据应用难题。

### 1.3. 主流NoSQL产品分类

NoSQL数据库的四大分类如下：

- **键值(Key-Value)存储数据库**
    - 相关产品： Tokyo Cabinet/Tyrant、**Redis**、Voldemort、Berkeley DB
    - 典型应用： 内容缓存，主要用于处理大量数据的高访问负载。
    - 数据模型： 一系列键值对
    - 优势： 快速查询
    - 劣势： 存储的数据缺少结构化
- **列存储数据库**
    - 相关产品：Cassandra, HBase, Riak
    - 典型应用：分布式的文件系统
    - 数据模型：以列簇式存储，将同一列数据存在一起
    - 优势：查找速度快，可扩展性强，更容易进行分布式扩展
    - 劣势：功能相对局限
- **文档型数据库**
    - 相关产品：CouchDB、**MongoDB**
    - 典型应用：Web应用（与Key-Value类似，Value是结构化的）
    - 数据模型： 一系列键值对
    - 优势：数据结构要求不严格
    - 劣势： 查询性能不高，而且缺乏统一的查询语法
- **图形(Graph)数据库**
    - 相关数据库：Neo4J、InfoGrid、Infinite Graph
    - 典型应用：社交网络
    - 数据模型：图结构
    - 优势：利用图结构相关算法。
    - 劣势：需要对整个图做计算才能得出结果，不容易做分布式的集群方案。

### 1.4. NoSQL特点

在大数据存取上具备关系型数据库无法比拟的性能优势，例如：

1.	易扩展
	- NoSQL数据库种类繁多，但是一个共同的特点都是去掉关系数据库的关系型特性。数据之间无关系，这样就非常容易扩展。也无形之间，在架构的层面上带来了可扩展的能力。
2.	大数据量，高性能
	- NoSQL数据库都具有非常高的读写性能，尤其在大数据量下，同样表现优秀。这得益于它的无关系性，数据库的结构简单。
3.	灵活的数据模型
	- NoSQL无需事先为要存储的数据建立字段，随时可以存储自定义的数据格式。而在关系数据库里，增删字段是一件非常麻烦的事情。如果是非常大数据量的表，增加字段简直就是一个噩梦。这点在大数据量的Web2.0时代尤其明显。
4.	高可用
	- NoSQL在不太影响性能的情况，就可以方便的实现高可用的架构。比如Cassandra，HBase模型，通过复制模型也能实现高可用。

综上所述，NoSQL的非关系特性使其成为了后Web2.0时代的宠儿，助力大型Web2.0网站的再次起飞，是一项全新的数据库革命性运动。


## 2. Redis 基础入门

### 2.1. Redis 介绍

redis 是一种基于键值对（key-value）数据库，其中 value 可以为 string、hash、list、set、zset 等多种数据结构，可以满足很多应用场景。还提供了键过期，发布订阅，事务，流水线等附加功能

> 流水线：Redis 的流水线功能允许客户端一次将多个命令请求发送给服务器，并将被执行的多个命令请求的结果在一个命令回复中全部返回给客户端，使用这个功能可以有效地减少客户端在执行多个命令时需要与服务器进行通信的次数

### 2.2. Redis 特性

1. 速度快，数据放在内存中，官方给出的读写性能 10 万/S，与机器性能也有关
    - 数据放内存中是速度快的主要原因
    - C 语言实现，与操作系统距离近
    - 使用了单线程架构，预防多线程可能产生的竞争问题
2. 键值对的数据结构服务器
3. 丰富的功能：键过期，发布订阅，事务，流水线等等
4. 简单稳定：单线程
5. 持久化：发生断电或机器故障，数据可能会丢失，持久化到硬盘
6. 主从复制：实现多个相同数据的 redis 副本
7. 高可用和分布式：哨兵机制实现高可用，保证 redis 节点故障发现和自动转移
8. 客户端语言多：java php python c c++ nodejs 等

### 2.3. redis的应用场景

- **缓存**：数据查询、短连接、新闻内容、商品内容等等（最多使用）。合理使用缓存加快数据访问速度，降低后端数据源压力
- 应用排行榜：按照热度排名，按照发布时间排行，主要用到列表和有序集合
- 计数器应用：视频网站播放数，网站浏览数统计，使用 redis 计数
- 社交网络：赞、踩、粉丝、下拉刷新、聊天室的在线好友列表。
- 消息队列：发布和订阅，如：秒杀、抢购、12306等等
- 数据过期处理（可以精确到毫秒）
- 分布式集群架构中的session分离。

### 2.4. 重大版本

1. 版本号第二位为奇数，为非稳定版本（2.7、2.9、3.1）
2. 第二为偶数，为稳定版本（2.6、2.8、3.0）
3. 当前奇数版本是下一个稳定版本的开发版本，如 2.9 是 3.0 的开发版本

## 3. Redis数据结构介绍

- redis是一种高级的key-value的存储系统，其中value支持多种类型的数据结构：

1. **字符串（strings）**：二进制安全的字符串，
2. **散列（hashes）**：由`field`和关联的`value`组成的map数据结构。`field`和`value`都是字符串的。
3. **列表（lists）**：按插入顺序排序的字符串元素的集合。基本上就是链表（linked lists）。
4. **集合（sets）**：不重复且无序的字符串元素的集合。
5. **有序集合（sorted sets）**：类似Sets，但是每个字符串元素都关联到一个叫`score`浮动数值（floating number value）。里面的元素总是通过score进行着排序，所以不同的是，它是可以检索的一系列元素。
6. **Bit arrays (或者说 simply bitmaps)**：通过特殊的命令，可以将 String 值当作一系列 bits 处理：可以设置和清除单独的 bits，数出所有设为 1 的 bits 的数量，找到最前的被设为 1 或 0 的 bit，等等。
7. **HyperLogLogs**：这是被用于估计一个 set 中元素数量的概率性的数据结构。

![Redis数据结构](images/20190511091019135_13726.jpg)

### 3.1. Redis keys（键）

- 而关于key的定义呢，需要注意的几点：

1. key不要太长，最好不要操作1024个字节，这不仅会消耗内存还会降低查找效率
2. key不要太短，如果太短会降低key的可读性
3. 在项目中，key最好有一个统一的命名规范，如：`项目名_模块名_存储内容=""`



# Java 操作 Redis

## 1. Jedis

### 1.1. Jedis概述

Jedis就是Java语法操作Redis的技术，类似于JDBC

![Jedis操作流程](images/20190511103850050_28588.jpg)

### 1.2. Java连接Redis

- 导入jar包
    - commons-pool2-2.3.jar
    - jedis-2.7.0.jar

### 1.3. Jedis类相关方法
#### 1.3.1. 构造方法

```java
Jedis(String host, String port);
    // 获取Jedis对象
    // host：Redis服务器ip地址
    // port：Redis服务器端口
```

#### 1.3.2. 常用方法

```java
String set(String key, String value);
    // 设置键值，成功返回“ok”

String get(String key, String value);
    // 根据键获取值
```

### 1.4. Jedis连接池配置对象

- **构造方法**

```java
JedisPoolConfig config = new JedisPoolConfig();
```

- **常用设置初始参数方法**

```java
void setMaxTotal(int maxTotal);
    // 设置连接池最大连接数，参数为int类型

void setMaxWaitMillis(long maxWaitMillis);
    // 设置最大等待时间，参数为long类型毫秒值
```

### 1.5. JedisPool连接池对象

- **构造方法**

```java
JedisPool(JedisPoolConfig poolConfig, String host, int port);
    // poolConfig：连接池配置对象，需要设置相关初始化参数
    // host：Redis数据库ip地址
    // port：Redis数据库端口
```

- **JedisPool 常用方法**

```java
Jedis getResource();
    // 获取Jedis对象
```

### 1.6. 单实例与Jedis连接池连接

```java
package lessonDemo;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis {
    // 单例连接
    @Test
    public void testJedis() {
        // 设置ip地址和端口，获取jedis对象
        Jedis jedis = new Jedis("192.168.34.128", 6379);

        // 设置数据
        String n = jedis.set("gender", "man");
        System.out.println(n);
        // 获取值
        String value = jedis.get("gender");
        System.out.println(value);
        // 释放资源
        jedis.close();
    }

    // 连接池连接
    @Test
    public void testJedisPool() {
        // 获取连接池配置对象，设置配置项
        JedisPoolConfig config = new JedisPoolConfig();

        // 最大连接数
        config.setMaxTotal(30);
        // 最大空闲连接数
        config.setMaxIdle(10);

        // 获取连接池
        JedisPool jedisPool = new JedisPool(config, "192.168.34.128", 6379);

        // 获取jedis对象
        Jedis jedis = jedisPool.getResource();
        // 设置数据
        jedis.set("java", "kaka2");
        System.out.println(jedis.get("java"));

        // 释放资源
        jedis.close();
        jedisPool.close();
    }
}
```

### 1.7. Jedis连接池工具类

```java
package com.moonzero.utils;

import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis连接工具类
 */
public class JedisUtil {
    // 设置静态连接池成员变量
    // 最大连接数
    private static String maxTotal;
    // 最大等待时间
    private static String maxWaitMillis;
    // Redis数据库ip地址
    private static String host;
    // Redis数据库端口号
    private static String port;
    // 定义连接池对象
    private static JedisPool pool;

    // 静态代码块
    static {
        // 创建ResourceBundle对象，读取redis.properites配置文件
        ResourceBundle rb = ResourceBundle.getBundle("jedis");
        maxTotal = rb.getString("maxTotal");
        maxWaitMillis = rb.getString("maxWaitMillis");
        host = rb.getString("host");
        port = rb.getString("port");

        // Jedis配置对象，设置初始参数
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(maxTotal));
        config.setMaxWaitMillis(Long.parseLong(maxWaitMillis));

        // 获取Jedis连接池对象
        pool = new JedisPool(config, host, Integer.parseInt(port));
    }

    // 获取Jedis连接对象方法
    public static Jedis getJedis() {
        return pool.getResource();
    }
}
```

配置文件jedis.properties

```properties
maxTotal=10
maxWaitMillis=2000
host=192.168.34.128
port=6379
```

# 扩展知识

## 1. Redis 最佳实践

### 1.1. 键名的生产实践

Redis 没有命令空间，而且也没有对键名有强制要求。设计合理的键名，有利于防止键冲突和项目的可维护性。

- 推荐使用键命名方式具有可读性和可管理性，建议以业务名(或数据库名)为前缀(防止key冲突)，用冒号分隔。例如：`业务名:对象名:id:[属性]`
- 推荐保持键的简洁性。在保证语义的前提下，控制key的长度，当key较多时，内存占用也不容忽视。例如：`user:{uid}:friends:messages:{mid}`可简化为`u:{uid}:fr:m:{mid}`，从而减少由于键过长的内存浪费
- **不能包含特殊字符**。反例：包含空格、换行、单双引号以及其他转义字符

## 2. Redis 相关网站

- [Redis 官网](https://redis.io/)
- [Redis 官方文档](https://redis.io/documentation)
- [Redis 官网国内中文翻译版](http://redis.cn/)
- [Redis 命令参考文档](http://redisdoc.com/)
- [Redisson 官网](https://redisson.org/)

## 3. 解决分布式锁的框架-redisson

redisson官网：https://redisson.org/

### 3.1. 简介

与Jedis很相似的用于操作Redis的框架，其中实现一个功能是在高并发的情况，给Redis加上锁，并且在线程的执行过程中，判断当前线程是否已经执行结束，会自动给当前的锁增加存活时间，以便让当前线程完全执行后，再去释放锁

案例：

![分布式锁-案例](images/20190418230705829_26455.png)

