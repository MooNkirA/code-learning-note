## 1. 分布式全局唯一 ID

传统的单体架构，基本是单数据库、业务单表的结构。每个业务表的 ID 一般都是从 1 自增，通过 `AUTO_INCREMENT=1` 设置自增起始值，但是在分布式服务架构模式下分库分表的设计，使得多个库或多个表存储相同的业务数据。这种情况根据数据库的自增 ID 就会产生相同 ID 的情况，不能保证主键的唯一性。

例如，如果第一个订单存储在 DB1 上则订单 ID 为 1，当一个新订单又入库了存储在 DB2 上订单 ID 也为1 。系统的架构虽然是分布式的，但是在用户层应是无感知的，重复的订单主键显而易见是不被允许的。

分布式 ID 生成方式，大致分类的话可以分为两类：

- 类 DB 型的唯一 ID，根据设置不同起始值和步长来实现趋势递增，需要考虑服务的容错性和可用性。
- 类 snowflake 型的唯一 ID，这种就是将 64 位划分为不同的段，每段代表不同的涵义，基本就是时间戳、机器 ID 和序列数。这种方案就是需要考虑时钟回拨的问题以及做一些 buffer 的缓冲设计提高性能。

## 2. UUID

UUID（Universally Unique Identifier），通用唯一识别码的缩写。UUID 是由一组 32 位数的 16 进制数字所构成，所以 UUID 理论上的总数为 16<sup>32</sup> = 2<sup>128</sup> ≈ 3.4 x 10<sup>38。也就是说若每纳秒产生 1 兆个 UUID，要花 100 亿年才会将所有 UUID 用完。

生成的 UUID 是由 `8-4-4-4-12` 格式的数据组成，其中 32 个字符和 4个 连字符 `-`，一般使用的时候会将连字符删除 `uuid.toString().replaceAll("-","")`。

目前 UUID 的产生方式有 5 种版本，每个版本的算法不同，应用范围也不同：

1. **基于时间的 UUID**（版本1）：这个一般是通过当前时间、随机数、和本地 MAC 地址来计算出来，可以通过 `org.apache.logging.log4j.core.util` 包中的 `UuidUtil.getTimeBasedUuid()` 来使用或者其他包中工具。由于使用了 MAC 地址，因此能够确保唯一性，但是同时也暴露了 MAC 地址，私密性不够好。
2. **DCE 安全的 UUID**（版本2）：DCE（Distributed Computing Environment）安全的 UUID 和基于时间的 UUID 算法相同，但会把时间戳的前 4 位置换为 POSIX 的 UID 或 GID。这个版本的 UUID 在实际中较少用到。
3. **基于名字的 UUID（MD5）**（版本3）：基于名字的 UUID 通过计算名字和名字空间的 MD5 散列值得到。这个版本的 UUID 保证了，相同名字空间中不同名字生成的 UUID 的唯一性；不同名字空间中的 UUID 的唯一性；相同名字空间中相同名字的 UUID 重复生成是相同的。
4. **随机 UUID**（版本4）：根据随机数，或者伪随机数生成 UUID。这种 UUID 产生重复的概率是可以计算出来的，但是重复的可能性可以忽略不计，因此该版本也是被经常使用的版本。JDK 中使用的就是这个版本。
5. **基于名字的UUID（SHA1）**（版本5）：和基于名字的 UUID 算法类似，只是散列值计算使用 SHA1（Secure Hash Algorithm 1）算法。

示例：JDK 自带的 UUID 工具生成版本4 根据随机数的 UUID 和版本 3 基于名字的 UUID

```java
public static void main(String[] args) {
    // 获取一个版本4根据随机字节数组的UUID。
    UUID uuid = UUID.randomUUID();
    System.out.println(uuid.toString().replaceAll("-",""));

    // 获取一个版本3(基于名称)根据指定的字节数组的UUID。
    byte[] nbyte = {10, 20, 30};
    UUID uuidFromBytes = UUID.nameUUIDFromBytes(nbyte);
    System.out.println(uuidFromBytes.toString().replaceAll("-",""));
}
```

虽然 UUID 生成方便，本地生成没有网络消耗，但是也有一些缺点：

- 不易于存储：UUID 太长，16 字节 128 位，通常以 36 长度的字符串表示，很多场景不适用。
- 信息不安全：基于 MAC 地址生成 UUID 的算法可能会造成 MAC 地址泄露，暴露使用者的位置。
- 对 MySQL 索引不利：如果作为数据库主键，在 InnoDB 引擎下，UUID 的无序性可能会引起数据位置频繁变动，严重影响性能。

## 3. 数据库生成

由于分布式数据库的起始自增值一样，所以才会有冲突的情况发生，那么将分布式系统中数据库的同一个业务表的自增 ID 设计成不一样的起始值，然后设置固定的步长，步长的值即为分库的数量或分表的数量。

以 MySQL 为例，给字段设置相应的自增长属性来保证 ID 自增：

- `auto_increment_offset`：表示自增长字段从那个数开始，他的取值范围是 1~65535。
- `auto_increment_increment`：表示自增长字段每次递增的量，其默认值是 1，取值范围是 1~65535。

假设有三台机器，则 DB1 中 order 表的起始 ID 值为 1，DB2 中 order 表的起始值为 2，DB3 中 order 表的起始值为 3，它们自增的步长都为 3，则它们的 ID 生成范围如下图所示：

![](images/115854219240261.png)

这种方式的优势就是依赖于数据库自身不需要其他资源，并且 ID 号单调自增，可以实现一些对 ID 有特殊要求的业务。

但是缺点也很明显，首先它强依赖 DB，当 DB 异常时整个系统不可用。虽然配置主从复制可以尽可能的增加可用性，但是数据一致性在特殊情况下难以保证。主从切换时的不一致可能会导致重复发号。还有就是 ID 发号性能瓶颈限制在单台 MySQL 的读写性能。

## 4. redis 生成

Redis 实现分布式唯一 ID，主要是通过 `INCR` 和 `INCRBY` 的自增原子命令，由于 Redis 自身的单线程的特点，所以能保证生成的 ID 肯定是唯一有序的。

但是单机存在性能瓶颈，无法满足高并发的业务需求，所以可以采用集群的方式来实现。集群的方式又会涉及到和数据库集群同样的问题，所以也需要设置分段和步长来实现。为了避免长期自增后数字过大可以通过与当前时间戳组合起来使用，另外为了保证并发和业务多线程的问题可以采用 Redis + Lua 的方式进行编码，保证安全。

Redis 实现分布式全局唯一 ID，它的性能比较高，生成的数据是有序的，对排序业务有利，但是同样它依赖于 redis，需要系统引进 redis 组件，增加了系统的整体复杂性。所以如果其他业务已经引进了 Redis 集群，则可以资源利用考虑使用 Redis 来实现。

> 此部分内容详见[《Redis 场景应用设计》笔记](/Database/Redis/Redis-场景应用设计)

## 5. Snowflake（雪花算法）

Snowflake（雪花算法）是由 Twitter 开源的分布式 ID 生成算法，以划分命名空间的方式将 64 位分割成多个部分，每个部分代表不同的含义。而在 Java 中 SnowFlake 算法生成的 ID 是 64 bit 的 Long 类型整数。

- 第 1 位占用 1 bit，其值始终是 0，可看做是符号位不使用。
- 第 2 位开始的 41 位是时间戳，可表示 2<sup>41</sup> 个数，每个数代表毫秒，那么雪花算法可用的时间年限是 `(1L<<41)/(1000L360024*365)=69` 年的时间。
- 中间的 10 bit 可表示机器数，即 2<sup>10</sup> = 1024 台机器，但是一般情况下不会部署如何数量的机器。如果对 IDC（互联网数据中心）有需求，还可以将 10-bit 分 5-bit 给 IDC，分 5-bit 给工作机器。这样就可以表示 32 个 IDC，每个 IDC 下可以有 32 台机器，具体的划分可以根据自身需求定义。
- 最后 12 bit 是自增序列，可表示 2<sup>12</sup> = 4096 个数。

通过以上划分之后，相当于在 1 毫秒 1 个数据中心的一台机器上可产生 4096 个有序的不重复的ID。但是 IDC 和机器数肯定不止一个，所以毫秒内能生成的有序 ID 数是翻倍的。

> Snowflake 的 Twitter 官方原版是用以 Scala 编写，以下是 Java 版本的写法。

```java
/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class SnowflakeDistributeId {
    // ==============================Fields===========================================
    /**
     * 开始时间截 (2015-01-01)
     */
    private final long twepoch = 1420041600000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeDistributeId(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
```

测试的代码：

```java
public static void main(String[] args) {
    SnowflakeDistributeId idWorker = new SnowflakeDistributeId(0, 0);
    for (int i = 0; i < 1000; i++) {
        long id = idWorker.nextId();
        // System.out.println(Long.toBinaryString(id));
        System.out.println(id);
    }
}
```

雪花算法提供了一个很好的设计思想，雪花算法生成的 ID 是趋势递增，不依赖数据库等第三方系统，以服务的方式部署，稳定性更高，生成 ID 的性能也是非常高的，而且可以根据自身业务特性分配 bit 位，非常灵活。

但是雪花算法强依赖机器时钟，如果机器上时钟回拨，会导致发号重复或者服务会处于不可用状态。如果恰巧回退前生成过一些 ID，而时间回退后，生成的 ID 就有可能重复。官方对于此并没有给出解决方案，而是简单的抛错处理，这样会造成在时间被追回之前的这段时间服务不可用。

很多其他类雪花算法也是在此思想上的设计，然后改进规避它的缺陷，如百度 UidGenerator 和美团分布式 ID 生成系统 Leaf 中 snowflake 模式都是在此基础上演进出来。

## 6. 百度 - UidGenerator

百度的 UidGenerator 是百度开源基于 Java 语言实现的唯一 ID 生成器，是在雪花算法 snowflake 的基础上做了一些改进。UidGenerator 以组件形式工作在应用项目中，支持自定义 workerId 位数和初始化策略，适用于 docker 等虚拟化环境下实例自动重启、漂移等场景。

在实现上，UidGenerator 提供了两种生成唯一 ID 方式，分别是 `DefaultUidGenerator` 和 `CachedUidGenerator`，官方建议如果有性能考虑的话使用 `CachedUidGenerator` 方式实现。

UidGenerator 依然是以划分命名空间的方式将 64-bit 分割成多个部分，只不过它的默认划分方式有别于雪花算法 snowflake。它默认是由 `1-28-22-13` 的格式进行划分。可根据业务的情况和特点，调整各个字段占用的位数。

![](images/137835423240261.png)

- 第 1 位仍然占用 1 bit，其值始终是 0。
- 第 2 位开始的 28 位是时间戳，可表示 2<sup>28</sup> 个数，以秒为单位，每个数代表秒则可用 `(1L<<28) / (360024365) ≈ 8.51` 年的时间。
- 中间的 workId（数据中心+工作机器，可以其他组成方式）则由 22-bit 组成，可表示 2<sup>22</sup> = 4194304 个工作ID。
- 最后由 13-bit 构成自增序列，可表示 2<sup>13</sup> = 8192 个数。

其中 workId（机器 id），最多可支持约 420w 次机器启动。内置实现为在启动时由数据库分配（表名为 WORKER_NODE），默认分配策略为用后即弃，后续可提供复用策略。

```sql
DROP TABLE IF	EXISTS WORKER_NODE;
CREATE TABLE WORKER_NODE (
	ID BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
	HOST_NAME VARCHAR ( 64 ) NOT NULL COMMENT 'host name',
	PORT VARCHAR ( 64 ) NOT NULL COMMENT 'port',
	TYPE INT NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
	LAUNCH_DATE DATE NOT NULL COMMENT 'launch date',
	MODIFIED TIMESTAMP NOT NULL COMMENT 'modified time',
	CREATED TIMESTAMP NOT NULL COMMENT 'created time',
	PRIMARY KEY ( ID ) 
) COMMENT = 'DB WorkerID Assigner for UID Generator', ENGINE = INNODB;
```

### 6.1. DefaultUidGenerator 实现

`DefaultUidGenerator` 就是正常的根据时间戳和机器位、还有序列号的生成方式，和雪花算法很相似，对于时钟回拨也只是抛异常处理。仅有一些不同，如以秒为单位和支持 Docker 等虚拟化环境。

```java
protected synchronized long nextId() {
    long currentSecond = getCurrentSecond();

    // Clock moved backwards, refuse to generate uid
    if (currentSecond < lastSecond) {
        long refusedSeconds = lastSecond - currentSecond;
        throw new UidGenerateException("Clock moved backwards. Refusing for %d seconds", refusedSeconds);
    }

    // At the same second, increase sequence
    if (currentSecond == lastSecond) {
        sequence = (sequence + 1) & bitsAllocator.getMaxSequence();
        // Exceed the max sequence, we wait the next second to generate uid
        if (sequence == 0) {
            currentSecond = getNextSecond(lastSecond);
        }

    // At the different second, sequence restart from zero
    } else {
        sequence = 0L;
    }

    lastSecond = currentSecond;

    // Allocate bits for UID
    return bitsAllocator.allocate(currentSecond - epochSeconds, workerId, sequence);
}
```

如果要使用 `DefaultUidGenerator` 的实现方式的话，以上划分的占用位数可通过 spring 进行参数配置。

```xml
```

