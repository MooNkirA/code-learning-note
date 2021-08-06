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

## 3. Redis 的操作指令
### 3.1. Redis数据结构介绍

- redis是一种高级的key-value的存储系统，其中value支持五种数据类型。
    1. 字符串（String）**(key,json)**
    2. 哈希（hash散列类型）
    3. 字符串列表（list）
    4. 字符串集合（set）
    5. 有序字符串集合（sorted set）
- 而关于key的定义呢，需要注意的几点：
	1. key不要太长，最好不要操作1024个字节，这不仅会消耗内存还会降低查找效率
	2. key不要太短，如果太短会降低key的可读性
	3. 在项目中，key最好有一个统一的命名规范，如：`项目名_模块名_存储内容=""`

![Redis数据结构](images/20190511091019135_13726.jpg)

### 3.2. 全局命令（keys的通用操作）

所谓通用操作是指，不管value是五种类型中的哪一种类型，都可以用的操作

#### 3.2.1. 查询键

```bash
keys pattern
```

- 获取所有与pattern匹配的key，`*`表示任意0个或多个字符，`？`表示任意一个字符
- 收集：到目为此，`*`在哪些技术中出现过，分别表示什么意思？
- 正则表达式，`*`，0个或多个
- url-pattern，`*`，模糊匹配（前缀和后缀）

#### 3.2.2. 查询键总数

```bash
dbsize
```

- 查询目前存在的键的总数。**如果存在大量键，线上禁止使用此指令**

#### 3.2.3. 检查键是否存在

```bash
exists key
```

- 判断该key是否存在，1表示存在，0表示不存在


#### 3.2.4. 其他

- `del key1 key2……`
    - 删除指定的key
- `rename key newkey`
    - 将当前key重命名为newkey
- `expire key`
    - 设置key的过期时间，单位秒
    - 如果过期时间到，Redis自动删除这个key
- `ttl key`
    - 获取该key所剩余的超时时间，如果没有设置超时，返回-1，如果设置了超时，返回剩余的秒数
    - 就算时间未到，但关了Redis服务器端，在默认情况下从控制台插入的key=value键值对数据，自动销毁。
- `type key`
    - 获取指定key的类型，以字符串形式返回
    - 返回值为：string/hash/list/set/zset，如果key不存在，返回none
- select切换数据库
    - 选择数据库
    - 一个Redis服务器可以包括多个数据库，客户端可以指连接Redis中的的哪个数据库，就好比一个mysql服务器中创建多个数据库，客户端连接时指定连接到哪个数据库。
    - 一个Redis实例最多可提供16个数据库，下标为0到15，客户端默认连接第0个数据库，也可以通过select命令选择哪个数据库。
- move移key到其他数据库
    - 将key移到某个数据库
    - `move key 1` : 将当前数据库中的key移到1号数据库中


### 3.3. 存储String类型（重点）

字符串类型是Redis中最为基础的数据存储类型，它在Redis中是二进制安全的，这便意味着该类型**存入和获取的数据相同**。在Redis中字符串类型的Value最多可以容纳的数据长度是512M。

#### 3.3.1. 赋值

`set key value`：设定key持有指定的字符串value，如果该key存在则进行覆盖操作。总是返回”OK”

#### 3.3.2. 取值

- `get key`：获取key的value。如果与该key关联的value不是String类型，redis将返回错误信息，因为get命令只能用于获取String value；如果该key不存在，返回(nil)。
- `getset key value`：先获取该key的值，然后在设置该key的值。

#### 3.3.3. 删除

`del key`：删除指定key

#### 3.3.4. 数值增减

- `incr key`：将指定的key的value原子性的递增1.如果该key不存在，其初始值为0，在incr之后其值为1。如果value的值不能转成整型，如hello，该操作将执行失败并返回相应的错误信息。
- `decr key`：将指定的key的value原子性的递减1.如果该key不存在，其初始值为0，在incr之后其值为-1。如果value的值不能转成整型，如hello，该操作将执行失败并返回相应的错误信息。

#### 3.3.5. 扩展命令（了解）

- `incrby key increment`：将指定的key的value原子性增加increment，如果该key不存在，器初始值为0，在incrby之后，该值为increment。如果该值不能转成整型，如hello则失败并返回错误信息
- `decrby key decrement`：将指定的key的value原子性减少decrement，如果该key不存在，器初始值为0，在decrby之后，该值为decrement。如果该值不能转成整型，如hello则失败并返回错误信息
- `append key value`：拼凑字符串。如果该key存在，则在原有的value后追加该值；如果该key不存在，则重新创建一个key/value

### 3.4. 存储hash（了解）

Redis中的Hash类型可以看成具有String Key和String Value的map容器。所以该类型非常适合于存储值对象的信息。如Username、Password和Age等。如果Hash中包含很少的字段，那么该类型的数据也将仅占用很少的磁盘空间。每一个Hash可以存储4294967295个键值对。

#### 3.4.1. 赋值

- `hset key field value`：为指定的key设定field/value对（键值对）。
- `hmset key field value [field2 value2 …]`：设置key中的多个filed/value

#### 3.4.2. 取值

- `hget key field`：	返回指定的key中的field的值
- `hmget key fileds`：获取key中的多个filed的值
- `hgetall key`：获取key中的所有filed-vaule

#### 3.4.3. 删除

- `hdel key field [field … ]`：可以删除一个或多个字段，返回值是被删除的字段个数，当value都删除后，key也会删除了
- `del key`：删除对应的key的整个Hash

#### 3.4.4. 增加数字

`hincrby key field increment`：设置key中filed的值增加increment，如：age增加20

#### 3.4.5. 其他命令

- `hexists key field`：判断指定的key中的filed是否存在
- `hlen key`：获取key所包含的field的数量
- `hkeys key`：获得所有的key
- `hvals key`：获得所有的value

### 3.5. Redis存储数据

- 对于集合类型(List/Set/SortSet)，共同点：
    - **如果元素都没有，那么这个key自动从Redis中删除**
    - **如果强行删除key，那么原来的所有value也会被删除**

### 3.6. 存储List(了解)

在Redis中，list类型是按照插入顺序排序的字符串链表。我们可以在其头部（left）和尾部（right）添加新的元素。在插入时，如果该key不存在，Redis将为该key创建一个新的链表。与此相反，如果链表中的所有元素都被删除了，那么该key也将会被从数据库中删除。list中可以包含的最大元素数据量4294967295（十亿以上）。

List接口
	ArrayList
	LinkedList,redis中的list就类似于java中的LinkList

#### 3.6.1. 两端添加

- `lpush key value1 value2……`：在指定的key对应的list的头部插入所有的value，如果该key不存在，该命令在插入之前创建一个与该key对应的空链表，再从头部插入数据。插入成功，返回元素的个数。
- `rpush key value1 value2……`：在指定的key对应的list的尾部插入所有的value，如果该key不存在，该命令在插入之前创建一个与该key对应的空链表，再从尾部插入数据。插入成功，返回元素的个数。

#### 3.6.2. 查看列表

- `lrange key start end`：获取链表中从start到end的元素的值，start和end从0开始计数，如果为负数，-1表示倒数第一个元素，-2表示倒数第二个元素，以此类推。
- 查看所有列表：(0~-1就可以查看所有值)。例：`lrange key 0 -1`

![1](images/20190511095521734_6959.jpg)

#### 3.6.3. 两边弹出

- `lpop key`：返回并弹出指定的key对应链表中头部（left）第一个元素，如果该key不存在，返回nil。
- `rpop key`：返回并弹出指定的key对应链表中尾部（right）第一个元素，如果该key不存在，返回nil。

#### 3.6.4. 获取列表中元素的个数

`llen key`：返回指定key对应链表中元素的个数，l代表list，len代表length

### 3.7. 存储set(了解)

Redis中，我们可以将set类型看作是没有排序的字符集合，set中可以包含的最大元素数据量4294967295（十亿以上）。

**和list不同，set集合不允许出现重复元素，如果多次添加相同元素，set中仅保留一份。**

#### 3.7.1. 添加/删除元素

- `sadd key value1 value2……`：向set中添加数据，如果该key的值已存在，则不会重复添加，返回添加成功个数
- `srem key value1 value2……`：删除set中指定的成员，返回删除成功个数

#### 3.7.2. 获得集体中的元素

`smembers key`：获取set集合中所有元素

- 直接删除key，那么key对应的list-set-sortedset都会删除；
- 如果key对应的所有值删除了，那么key也会自动被删除

#### 3.7.3. 判断元素是否在集合中存在

- `sismember key value`：判断key中指定的元素是否在该set集合中存在。存在则返回1，不存在则返回0

### 3.8. 存储sortedset

sortedset和set类型极为类似，它们都是字符串的集合，都不允许重复的元素出现在一个set中。它们之间的主要区别是**sortedset中每一个元素都会有一个分数（score）与之关联，Redis正是通过分数来为集合中的元素进行从小到大的排序（默认）。**

**sortedset集合中的元素必须是唯一的，但分数（score）却是可以重复。**

#### 3.8.1. 添加元素

- `zadd key score value score value score value`
    - 将所有元素以及对应的分数，存放到sortedset集合中，如果该元素已存在则会用新的分数替换原来的分数。
    - 返回值是新加入到集合中的元素个数，不包含之前已经存在的元素。

#### 3.8.2. 查询元素（从小到大）

- `zrange key start end`
    - 获取集合中下标为start到end的元素，不带分数排序之后的sortedSet与list的位置是一样，位置从左到右是正数，从0开始，位置从右到左是负数，从-1开始，-1是倒数第一个元素，-2倒数第二个元素
- `zrange key start end withscores`
    - 获取集合中下标为start到end的元素，带分数按分数从小到大排序
    - 如果相同用户的话，不会再将用户名插入集合中，但分数可以替换原来的分数

#### 3.8.3. 查询元素（从大到小）

- `zrevrange key start end`：按照元素分数从大到小，获取集合中下标为start到end的元素，不带分数
- `zrevrange key start end withscores`：按照元素分数从大到小，获取集合中下标为start到end的元素，带分数

#### 3.8.4. 获取元素分数

`zscore key member`：返回指定元素的分数


#### 3.8.5. 获取元素数量

`zcard key`：获取集合中元素数量

#### 3.8.6. 删除元素

`zrem key member member member`：从集合中删除指定的元素

#### 3.8.7. 按照分数范围删除元素

`zremrangebyscore key min max`：按照分数范围删除元素

### 3.9. redis持久化
#### 3.9.1. 概述

Redis的高性能是由于其将所有数据都存储在了内存中，为了使Redis在重启之后仍能保证数据不丢失，需要将数据从内存中同步到硬盘中，这一过程就是持久化。

Redis支持两种方式的持久化机制，**RDB方式与AOF方式**。可以单独使用其中一种或将二者结合使用。

1. RDB持久化（默认支持，无需配置）：该机制是指在指定的时间间隔内将内存中的数据集快照写入磁盘。
2. AOF持久化：该机制将以日志的形式记录服务器所处理的每一个写操作，在Redis服务器启动之初会读取该文件来重新构建数据库，以保证启动后数据库中的数据是完整的。
3. 无持久化：我们可以通过配置的方式禁用Redis服务器的持久化功能，这样我们就可以将Redis视为一个功能加强版的memcached了。

#### 3.9.2. RDB

RDB 持久化把当前进程数据生成快照（.rdb）文件保存到硬盘的过程，有手动触发和自动触发

- 优势
	1. 一旦采用该方式，那么你的整个Redis数据库将只包含一个文件，这对于文件备份而言是非常完美的。比如，你可能打算每个小时归档一次最近24小时的数据，同时还要每天归档一次最近30天的数据。通过这样的备份策略，一旦系统出现灾难性故障，我们可以非常容易的进行恢复。
	2. 对于灾难恢复而言，RDB是非常不错的选择。因为我们可以非常轻松的将一个单独的文件压缩后再转移到其它存储介质上
	3. 性能最大化。对于Redis的服务进程而言，在开始持久化时，它唯一需要做的只是fork（分叉）出子进程，之后再由子进程完成这些持久化的工作，这样就可以极大的避免服务进程执行IO操作了。
	4. 相比于AOF机制，如果数据集很大，RDB的启动效率会更高。
- 劣势
	1. 如果你想保证数据的高可用性，即最大限度的避免数据丢失，那么RDB将不是一个很好的选择。因为系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。
	2. 由于RDB是通过fork子进程来协助完成数据持久化工作的，因此，如果当数据集较大时，可能会导致整个服务器停止服务几百毫秒，甚至是1秒钟

##### 3.9.2.1. 手动触发

手动触发有 `save` 和 `bgsave` 两命令

- `save` 命令：阻塞当前 Redis，直到 RDB 持久化过程完成为止，若内存实例比较大会造成长时间阻塞，线上环境不建议用它
- `bgsave` 命令：redis 进程执行 fork 操作创建子线程，由子线程完成持久化，阻塞时间很短（微秒级），是 save 的优化，在执行 `redis-cli shutdown` 关闭 redis 服务时，如果没有开
启 AOF 持久化，会自动执行 bgsave。显然 bgsave 是对 save 的优化。

bgsave 运行流程

![bgsave 运行流程图](images/20191114135226267_18687.png)

##### 3.9.2.2. 配置自动触发

- 修改配置文件redis.conf，配置快照参数

```conf
save 900 1     # 每900秒(15分钟)至少有1个key发生变化，则dump内存快照。
save 300 10    # 每300秒(5分钟)至少有10个key发生变化，则dump内存快照
save 60 10000  # 每60秒(1分钟)至少有10000个key发生变化，则dump内存快照
```

![1](images/20190511102807351_18651.jpg)

![2](images/20190511102821561_20719.jpg)

- 设置保存位置设置

![3](images/20190511102832208_16601.jpg)

![4](images/20190511102837561_26197.jpg)

##### 3.9.2.3. RDB 文件的操作

- 命令：`config set dir /usr/local`
    - 设置 rdb 文件保存路径
- 备份：`bgsave`
    - 将 dump.rdb 保存到 usr/local 下
- 恢复：将 dump.rdb 放到 redis 安装目录与 redis.conf 同级目录，重启 redis 即可
- 优点：
    1. 压缩后的二进制文，适用于备份、全量复制，用于灾难恢复
    2. 加载 RDB 恢复数据远快于 AOF 方式
- 缺点：
    1. 无法做到实时持久化，每次都要创建子进程，频繁操作成本过高
    2. 保存后的二进制文件，存在老版本不兼容新版本 rdb 文件的问题

#### 3.9.3. AOF

针对 RDB 不适合实时持久化，redis 提供了 AOF 持久化方式来解决

- 优势
	1. 该机制可以带来更高的数据安全性，即数据持久性。Redis中提供了3中同步策略，即**每秒同步、每修改同步和不同步**。事实上，每秒同步也是异步完成的，其效率也是非常高的，所差的是一旦系统出现宕机现象，那么这一秒钟之内修改的数据将会丢失。而每修改同步，我们可以将其视为同步持久化，即每次发生的数据变化都会被立即记录到磁盘中。可以预见，这种方式在效率上是最低的。至于无同步，无需多言，我想大家都能正确的理解它。
	2. 由于该机制对日志文件的写入操作采用的是append模式，因此在写入过程中即使出现宕机现象，也不会破坏日志文件中已经存在的内容。然而如果我们本次操作只是写入了一半数据就出现了系统崩溃问题，不用担心，在Redis下一次启动之前，我们可以通过redis-check-aof工具来帮助我们解决数据一致性的问题。
	3. 如果日志过大，Redis可以自动启用rewrite机制。即Redis以append模式不断的将修改数据写入到老的磁盘文件中，同时Redis还会创建一个新的文件用于记录此期间有哪些修改命令被执行。因此在进行rewrite切换时可以更好的保证数据安全性。
	4. AOF包含一个格式清晰、易于理解的日志文件用于记录所有的修改操作。事实上，我们也可以通过该文件完成数据的重建。
- 劣势
	1. 对于相同数量的数据集而言，AOF文件通常要大于RDB文件
	2. 根据同步策略的不同，AOF在运行效率上往往会慢于RDB。总之，每秒同步策略的效率是比较高的，同步禁用策略的效率和RDB一样高效。

##### 3.9.3.1. redis 的 AOF 配置详解

```conf
appendonly yes # 启用 aof 持久化方式

# appendfsync always # 每收到写命令就立即强制写入磁盘，最慢的，但是保证完全的持久化，不推荐使用
appendfsync everysec # 每秒强制写入磁盘一次，性能和持久化方面做了折中，推荐
# appendfsync no # 完全依赖 os，性能最好,持久化没保证（操作系统自身的同步）

no-appendfsync-on-rewrite yes # 正在导出 rdb 快照的过程中,要不要停止同步 aof
auto-aof-rewrite-percentage 100 # aof 文件大小比起上次重写时的大小,增长率100%时,重写
auto-aof-rewrite-min-size 64mb # aof 文件,至少超过 64M 时,重写
```

- 修改 redis.conf 设置文件，修改`appendonly yes` (默认AOF处于关闭，为 no)

![1](images/20190511103409610_11558.jpg)

- 手动开启

![2](images/20190511103435927_11796.jpg)

- 策略的选择：

```
always    #每次有数据修改发生时都会写入AOF文件
everysec  #每秒钟同步一次，该策略为AOF的缺省策略
no       #从不同步。高效但是数据不会被持久化
```

![3](images/20190511103515067_31126.jpg)

![4](images/20190511103520130_216.jpg)

- 默认文件名：appendfilename "appendonly.aof"。可以修改为指定文件名称

![5](images/20191114140531366_23137.png)

##### 3.9.3.2. AOF流程说明

1. 所有的写入命令(set hset)会 append 追加到 aof_buf 缓冲区中
2. AOF 缓冲区向硬盘做 sync 同步
3. 随着 AOF 文件越来越大，需定期对 AOF 文件 rewrite 重写，达到压缩
4. 当 redis 服务重启，可 load 加载 AOF 文件进行恢复

AOF持久化流程：命令写入(append)，文件同步(sync)，文件重写(rewrite)，重启加载(load)

![AOF持久化流程图](images/20191114141805514_32478.png)

##### 3.9.3.3. AOF 恢复

1. 设置 `appendonly yes`
2. 将 appendonly.aof 文件放到 dir 参数指定的目录
3. 启动 Redis，Redis 会自动加载 appendonly.aof 文件

#### 3.9.4. redis 重启时恢复加载 AOF 与 RDB 顺序及流程

1. 当 AOF 和 RDB 文件同时存在时，优先加载
2. 若关闭了 AOF，加载 RDB 文件
3. 加载 AOF/RDB 成功，redis 重启成功
4. AOF/RDB 存在错误，redis 启动失败并打印错误信息

### 3.10. Redis其他知识（了解）
#### 3.10.1. 服务器命令(自学)

- **ping**，测试连接是否存活
    - 执行下面命令之前，我们停止redis 服务器
    - `redis 127.0.0.1:6379> ping`
    - Could not connect to Redis at 127.0.0.1:6379: Connection refused
- **echo**，在命令行打印一些内容
- **select**，选择数据库。
    - Redis 数据库编号从0~15，可以选择任意一个数据库来进行数据的存取。
    - 当选择16 时，报错，说明没有编号为16 的这个数据库
- **quit**，退出连接。
- **dbsize**，返回当前数据库中key 的数目。
- **info**，获取服务器的信息和统计。
- **flushdb**，删除当前选择数据库中的所有key。
- **flushall**，删除所有数据库中的所有key。

#### 3.10.2. 消息订阅与发布

- 命令：`subscribe channel`
    - 订阅频道，例：subscribe mychat，订阅mychat这个频道
- 命令：`psubscribe channel*`
    - 批量订阅频道，例：`psubscribe s*`，订阅以”s”开头的频道
- 命令：publish channel content
    - 在指定的频道中发布消息，如 `publish mychat ‘today is a newday’`

其他详见day50笔记

#### 3.10.3. redis事务

- redis事务特征
    - 在事务中的所有命令都将会被串行化的顺序执行，事务执行期间，Redis不会再为其它客户端的请求提供任何服务
    - 在Redis事务中如果有某一条命令执行失败，其后的命令仍然会被继续执行。
- **multi**：开启事务用于标记事务的开始，其后执行的命令都将被存入命令队列，直到执行EXEC时，这些命令才会被原子的执行，类似与关系型数据库中的：begin transaction
- **exec**：提交事务，类似与关系型数据库中的：commit
- **discard**：事务回滚，类似与关系型数据库中的：rollback

详见day50笔记

## 4. Jedis
### 4.1. Jedis概述

Jedis就是Java语法操作Redis的技术，类似于JDBC

![Jedis操作流程](images/20190511103850050_28588.jpg)

### 4.2. Java连接Redis

- 导入jar包
    - commons-pool2-2.3.jar
    - jedis-2.7.0.jar

### 4.3. Jedis类相关方法
#### 4.3.1. 构造方法

```java
Jedis(String host, String port);
    // 获取Jedis对象
    // host：Redis服务器ip地址
    // port：Redis服务器端口
```

#### 4.3.2. 常用方法

```java
String set(String key, String value);
    // 设置键值，成功返回“ok”

String get(String key, String value);
    // 根据键获取值
```

### 4.4. Jedis连接池配置对象

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

### 4.5. JedisPool连接池对象

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

### 4.6. 单实例与Jedis连接池连接

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

### 4.7. Jedis连接池工具类

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

## 5. 扩展知识
### 5.1. 解决分布式锁的框架-redisson

redisson官网：https://redisson.org/

#### 5.1.1. 简介

与Jedis很相似的用于操作Redis的框架，其中实现一个功能是在高并发的情况，给Redis加上锁，并且在线程的执行过程中，判断当前线程是否已经执行结束，会自动给当前的锁增加存活时间，以便让当前线程完全执行后，再去释放锁

案例：

![分布式锁-案例](images/20190418230705829_26455.png)


## 6. Redis 相关网站

- [Redis 官网](https://redis.io/)
- [Redis 官方文档](https://redis.io/documentation)
- [Redis 官网国内中文翻译版](http://redis.cn/)
- [Redis 命令参考文档](http://redisdoc.com/)
- [Redisson 官网](https://redisson.org/)
