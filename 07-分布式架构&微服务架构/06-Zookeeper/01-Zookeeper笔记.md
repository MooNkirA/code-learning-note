# Zookeeper 基础

## 1. Zookeeper 概念

**Zookeeper 是一个分布式协调服务，可用于服务发现，分布式锁，分布式领导选举，配置管理等**。Zookeeper 提供了一个类似于 Linux 文件系统的树形结构（可认为是轻量级的内存文件系统，但只适合存少量信息，完全不适合存储大量文件或者大文件），同时提供了对于每个节点的监控与通知机制。

## 2. Zookeeper 角色

Zookeeper 集群是一个基于主从复制的高可用集群，每个服务器承担如下三种角色中的一种

### 2.1. Leader

1. 一个 Zookeeper 集群同一时间只会有一个实际工作的 Leader，它会发起并维护与各 Follwer 及 Observer 间的心跳。
2. **所有的写操作必须要通过 Leader 完成再由 Leader 将写操作广播给其它服务器。只要有超过半数节点（不包括 observeer 节点）写入成功，该写请求就会被提交（类 2PC 协议）。**

### 2.2. Follower

1. 一个 Zookeeper 集群可能同时存在多个 Follower，它会响应 Leader 的心跳
2. **Follower 可直接处理并返回客户端的读请求，同时会将写请求转发给 Leader 处理，并且负责在 Leader 处理写请求时对请求进行投票。**

### 2.3. Observer

角色与 Follower 类似，但是无投票权。Zookeeper 需保证高可用和强一致性，为了支持更多的客户端，需要增加更多 Server；Server 增多，投票阶段延迟增大，影响性能；引入 Observer，Observer 不参与投票； Observers 接受客户端的连接，并将写请求转发给 leader 节点； 加入更多 Observer 节点，提高伸缩性，同时不影响吞吐率。

## 3. Zookeeper 工作原理（原子广播）

1. **Zookeeper 的核心是原子广播**，这个机制保证了各个 server 之间的同步。实现这个机制的协议叫做 Zab 协议。**Zab 协议有两种模式，它们分别是恢复模式和广播模式。**
2. 当服务启动或者在领导者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多数 server 的完成了和 leader 的状态同步以后，恢复模式就结束了。
3. 状态同步保证了 leader 和 server 具有相同的系统状态
4. **一旦 leader 已经和多数的 follower 进行了状态同步后，他就可以开始广播消息了，即进入广播状态。**这时候当一个 server 加入 zookeeper 服务中，它会在恢复模式下启动，发现 leader，并和 leader 进行状态同步。待到同步结束，它也参与消息广播。Zookeeper 服务一直维持在 Broadcast 状态，直到 leader 崩溃了或者 leader 失去了大部分的 followers 支持。
5. 广播模式需要保证 proposal 被按顺序处理，因此 zk 采用了递增的事务 id 号(zxid)来保证。所有的提议(proposal)都在被提出的时候加上了 zxid。
6. 实现中 zxid 是一个 64 为的数字，它高 32 位是 epoch 用来标识 leader 关系是否改变，每次一个 leader 被选出来，它都会有一个新的 epoch。低 32 位是个递增计数。
7. 当 leader 崩溃或者 leader 失去大多数的 follower，这时候 zk 进入恢复模式，恢复模式需要重新选举出一个新的 leader，让所有的 server 都恢复到一个正确的状态。

### 3.1. ZAB 协议（了解）

#### 3.1.1. 事务编号 Zxid （事务请求 计数器 + epoch）

在 ZAB ( ZooKeeper Atomic Broadcast , ZooKeeper 原子消息广播协议） 协议的事务编号 Zxid 设计中，Zxid 是一个 64 位的数字，其中低 32 位是一个简单的单调递增的计数器，**针对客户端每一个事务请求，计数器加 1**；而高 32 位则代表 Leader 周期 epoch 的编号，**每个当选产生一个新的 Leader 服务器，就会从这个 Leader 服务器上取出其本地日志中最大事务的 ZXID，并从中读取 epoch 值，然后加 1，以此作为新的 epoch**，并将低 32 位从 0 开始计数。

Zxid（Transaction id）类似于 RDBMS 中的事务 ID，用于标识一次更新操作的 Proposal（提议）ID。为了保证顺序性，该 zkid 必须单调递增。

#### 3.1.2. epoch

**epoch**：可以理解为当前集群所处的年代或者周期，每个 leader 就像皇帝，都有自己的年号，所以每次改朝换代，leader 变更之后，都会在前一个年代的基础上加 1。**这样就算旧的 leader 崩溃恢复之后，也没有人听他的了，因为 follower 只听从当前年代的 leader 的命令**。

#### 3.1.3. Zab 协议有两种模式 - 恢复模式（选主）、广播模式（同步）

Zab协议有两种模式，它们分别是**恢复模式（选主）和广播模式（同步）**。当服务启动或者在领导者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多数 Server 完成了和 leader 的状态同步以后，恢复模式就结束了。状态同步保证了 leader 和 Server 具有相同的系统状态。

ZAB 提交事务并不像 2PC 一样需要全部 follower 都 ACK，**只需要得到超过半数的节点的 ACK 就可以了**。

#### 3.1.4. ZAB 协议 4 阶段

##### 3.1.4.1. Leader election （选举阶段 - 选出准 Leader）

**Leader election（选举阶段）**：节点在一开始都处于选举阶段，只要有一个节点得到超半数节点的票数，它就可以当选准 leader。只有到达 广播阶段（broadcast） 准 leader 才会成为真正的 leader。这一阶段的目的是就是为了选出一个准 leader，然后进入下一个阶段。

##### 3.1.4.2. Discovery （发现阶段 - 接受提议、生成 epoch 、接受 epoch）

**Discovery（发现阶段）**：在这个阶段，followers 跟准 leader 进行通信，同步 followers 最近接收的事务提议。这个一阶段的主要目的是发现当前大多数节点接收的最新提议，并且准 leader 生成新的 epoch，让 followers 接受，更新它们的 accepted Epoch

一个 follower 只会连接一个 leader，如果有一个节点 f 认为另一个 follower p 是 leader，f 在尝试连接 p 时会被拒绝，f 被拒绝之后，就会进入重新选举阶段。

##### 3.1.4.3. Synchronization （同步阶段 - 同步 follower 副本）

**Synchronization（同步阶段）**：同步阶段主要是利用 leader 前一阶段获得的最新提议历史，同步集群中所有的副本。只有当 大多数节点都同步完成，准 leader 才会成为真正的 leader。follower 只会接收 zxid 比自己的 lastZxid 大的提议。

##### 3.1.4.4. Broadcast （广播阶段 -leader 消息广播）

**Broadcast（广播阶段）**：到了这个阶段，Zookeeper 集群才能正式对外提供事务服务，并且 leader 可以进行消息广播。同时如果有新的节点加入，还需要对新节点进行同步。

#### 3.1.5. ZAB 协议 JAVA 实现（ FLE-发现阶段和同步合并为 Recovery Phase（恢复阶段））

协议的 Java 版本实现跟上面的定义有些不同，选举阶段使用的是 Fast Leader Election（FLE），它包含了 选举的发现职责。因为 FLE 会选举拥有最新提议历史的节点作为 leader，这样就省去了发现最新提议的步骤。实际的实现将 发现阶段 和 同步合并为 Recovery Phase（恢复阶段）。所以，ZAB 的实现只有三个阶段：Fast Leader Election；Recovery Phase；Broadcast Phase。

### 3.2. 投票机制（了解）

**每个 sever 首先给自己投票，然后用自己的选票和其他 sever 选票对比，权重大的胜出，使用权重较大的更新自身选票箱**。具体选举过程如下：

1. 每个 Server 启动以后**都询问其它的 Server 它要投票给谁**。对于其他 server 的询问，server 每次根据自己的状态都回复自己推荐的 leader 的 id 和上一次处理事务的 zxid（系统启动时每个 server 都会推荐自己）
2. 收到所有 Server 回复以后，就**计算出 zxid 最大的哪个 Server**，并将这个 Server 相关信息设置成下一次要投票的 Server。
3. 计算这过程中**获得票数最多的的 sever 为获胜者**，如果获胜者的票数超过半数，则改 server 被选为 leader。否则，继续这个过程，直到 leader 被选举出来
4. leader 就会开始等待 server 连接
5. Follower 连接 leader，将最大的 zxid 发送给 leader
6. Leader 根据 follower 的 zxid 确定同步点，至此选举阶段完成。
7. 选举阶段完成 Leader 同步后通知 follower 已经成为 uptodate 状态
8. Follower 收到 uptodate 消息后，又可以重新接受 client 的请求进行服务了

> **举例**：目前有 5 台服务器，每台服务器均没有数据，它们的编号分别是 1,2,3,4,5,按编号依次启动，它们的选择举过程如下：
>
> 1. 服务器 1 启动，给自己投票，然后发投票信息，由于其它机器还没有启动所以它收不到反馈信息，服务器 1 的状态一直属于 Looking。
> 2. 服务器 2 启动，给自己投票，同时与之前启动的服务器 1 交换结果，由于服务器 2 的编号大所以服务器 2 胜出，但此时投票数没有大于半数，所以两个服务器的状态依然是 LOOKING。
> 3. 服务器 3 启动，给自己投票，同时与之前启动的服务器 1,2 交换信息，由于服务器 3 的编号最大所以服务器 3 胜出，此时投票数正好大于半数，所以服务器 3 成为领导者，服务器 1,2 成为小弟。
> 4. 服务器 4 启动，给自己投票，同时与之前启动的服务器 1,2,3 交换信息，尽管服务器 4 的编号大，但之前服务器 3 已经胜出，所以服务器 4 只能成为小弟。
> 5. 服务器 5 启动，后面的逻辑同服务器 4 成为小弟。

## 4. Znode 有四种形式的目录节点

1. **PERSISTENT**：持久的节点。
2. **EPHEMERAL**：暂时的节点。（与Session有关，如果Session销毁，则节点数据销毁）
3. **PERSISTENT_SEQUENTIAL**：持久化顺序编号目录节点。
4. **EPHEMERAL_SEQUENTIAL**：暂时化顺序编号目录节点。

## 5. Zookeeper 安装

### 5.1. 安装包方式

#### 5.1.1. 安装

- zookeeper底层依赖于jdk，zookeeper用户登录后，根目录下先进行jdk的安装，jdk使用jdk-8u131-linux-x64.tar.gz版本，上传并解压jdk

```bash
# 解压jdk
tar -xzvf jdk-8u131-linux-x64.tar.gz
```

- 配置jdk环境变量

```bash
# vim打开 .bash_profile文件
vi .bash_profile

# 文件中加入如下内容
JAVA_HOME=/home/zookeeper/jdk1.8.0_131
export JAVA_HOME
​
PATH=$JAVA_HOME/bin:$PATH
export PATH
​
# 使环境变量生效
. .bash_profile

# 检测jdk安装
java -version
```

- zookeeper使用zookeeper-x.x.x.tar.gz，上传并解压

```bash
tar -xzvf zookeeper-3.4.10.tar.gz
```

- 为zookeeper准备配置文件

```bash
# 进入conf目录
cd /home/zookeeper/zookeeper-x.x.x/conf
# 复制配置文件
cp zoo_sample.cfg zoo.cfg
# zookeeper根目录下新建data目录
mkdir data
# vi 修改配置文件中的dataDir
# 此路径用于存储zookeeper中数据的内存快照、及事物日志文件
dataDir=/home/zookeeper/zookeeper-x.x.x/data
```

### 5.2. docker 方式（待整理）

整理中...

### 5.3. Zookeeper 配置

修改 conf 目录的 zoo.cfg 文件，可以设置 zk 相关配置。如下是本人配置参考：

```properties
# The number of milliseconds of each tick
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
dataDir=E:/deployment-environment/apache-zookeeper-3.6.3-bin/data
# the port at which the clients will connect
clientPort=2181
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the 
# administrator guide before turning on autopurge.
#
# http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1
# Zookeeper AdminServer port
admin.serverPort=9999

## Metrics Providers
#
# https://prometheus.io Metrics Exporter
#metricsProvider.className=org.apache.zookeeper.metrics.prometheus.PrometheusMetricsProvider
#metricsProvider.httpPort=7000
#metricsProvider.exportJvmInfo=true
# 日志文件
dataLogDir=E:/deployment-environment/apache-zookeeper-3.6.3-bin/log
```

> 注：Zookeeper3.5的新特性：会启动 Zookeeper AdminServer，默认使用 8080 端口。可以通过配置文件的 `admin.serverPort=8888` 修改 AdminServer 的端口

## 6. Zookeeper 服务端常用命令

修改了相应的配置之后，可以直接通过 zkServer.sh 这个脚本进行服务的相关操作

```bash
# 进入zookeeper的bin目录
cd /home/zookeeper/zookeeper-3.x.x/bin

# 启动 ZK 服务
sh bin/zkServer.sh start
# 查看 ZK 服务状态
sh bin/zkServer.sh status
# 停止 ZK 服务
sh bin/zkServer.sh stop
# 重启 ZK 服务
sh bin/zkServer.sh restart
```

## 7. Zookeeper 常用操作命令

### 7.1. 新增节点

命令格式：

```bash
# 其中-s 为有序节点，-e 临时节点
create [-s] [-e] path data
```

- 创建持久化节点并写入数据。**不带任何参数，默认是新增持久节点**

```bash
create /hadoop "123456"
```

- 创建持久化有序节点，此时创建的节点名为`指定节点名 + 自增序号`

```bash
[zk: localhost:2181(CONNECTED) 2] create -s /a "aaa"
Created /a0000000000
[zk: localhost:2181(CONNECTED) 3] create -s /b "bbb"
Created /b0000000001
[zk: localhost:2181(CONNECTED) 4] create -s /c "ccc"
Created /c0000000002
```

- 创建临时节点，临时节点会在会话过期后被删除

```bash
[zk: localhost:2181(CONNECTED) 5] create -e /tmp "tmp"
Created /tmp
```

- 创建临时有序节点，临时节点会在会话过期后被删除

```bash
[zk: localhost:2181(CONNECTED) 6] create -s -e /aa 'aaa'
Created /aa0000000004
[zk: localhost:2181(CONNECTED) 7] create -s -e /bb 'bbb'
Created /bb0000000005
[zk: localhost:2181(CONNECTED) 8] create -s -e /cc 'ccc'
Created /cc0000000006
```

> 注意：临时节点不能创建子节点

```bash
[zk: localhost:2181(CONNECTED) 19] create /test/test1
Ephemerals cannot have children: /test/test1
```

### 7.2. 更新节点

更新节点的命令是`set`，语法如下：

```bash
set [-s] [-v version] path data
```

可以直接进行修改

```bash
[zk: localhost:2181(CONNECTED) 3] set /hadoop "345"
cZxid = 0x4
ctime = Thu Dec 12 14:55:53 CST 2019
mZxid = 0x5
mtime = Thu Dec 12 15:01:59 CST 2019
pZxid = 0x4
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
```

也可以基于版本号进行更改，此时类似于乐观锁机制，当传入的数据版本号 (dataVersion) 和当前节点的数据版本号不符合时，zookeeper 会拒绝本次修改：

```bash
[zk: localhost:2181(CONNECTED) 12] set -v 1 /hadoop "asss"
version No is not valid : /hadoop
```

### 7.3. 删除节点

删除节点的语法：

```bash
# 删除单个节点，该节点下不能有子节点，否则拒绝删除
delete [-v version] path
# 级联删除，包含删除该节点下所有子节点
deleteall path [-b batch size]
```

和更新节点数据一样，也可以传入版本号，当传入的数据版本号 (dataVersion) 和当前节点的数据版本号不符合时，zookeeper 不会执行删除操作。

```bash
[zk: localhost:2181(CONNECTED) 21] delete -v 1 /hadoop
version No is not valid : /hadoop
```

如果某个节点下有子节点，使用`delete`命令无法删除该节点，需要使用`deleteall`命令

```java
[zk: localhost:2181(CONNECTED) 30] delete /moon
Node not empty: /moon
[zk: localhost:2181(CONNECTED) 31] deleteall /moon
```

### 7.4. 查看节点

查看节点语法：

```bash
get [-s] [-w] path
```

- 参数 `-s` 列举出节点详情
- 参数 `-w` 添加一个 watch（监视器）

查询某个节点详细信息

```bash
[zk: localhost:2181(CONNECTED) 4] get -s /hadoop
345
cZxid = 0x6a
ctime = Thu Jun 17 22:37:55 CST 2021
mZxid = 0x6c
mtime = Thu Jun 17 22:38:12 CST 2021
pZxid = 0x6a
cversion = 0
dataVersion = 2
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
```

节点各个属性如下表。其中一个重要的概念是 Zxid(ZooKeeper Transaction Id)，ZooKeeper 节点的每一次更改都具有唯一的 Zxid，如果 Zxid1 小于 Zxid2，则 Zxid1 的更改发生在 Zxid2 更改之前。

|                状态属性                 |                                        说明                                        |
| -------------------------------------- | --------------------------------------------------------------------------------- |
| cZxid                                  | 数据节点创建时的事务 ID                                                              |
| ctime                                  | 数据节点创建时的时间                                                                 |
| <font color=red>**mZxid**</font>       | 数据节点最后一次更新时的事务 ID                                                      |
| <font color=red>**mtime**</font>       | 数据节点最后一次更新时的时间                                                         |
| pZxid                                  | 数据节点的子节点最后一次被修改时的事务 ID                                              |
| cversion                               | 子节点的更改次数                                                                    |
| <font color=red>**dataVersion**</font> | 节点数据的更改次数                                                                  |
| aclVersion                             | 节点的 ACL 的更改次数                                                               |
| ephemeralOwner                         | 如果节点是临时节点，则表示创建该节点的会话的 SessionID；如果节点是持久节点，则该属性值为 0 |
| dataLength                             | 数据内容的长度                                                                      |
| numChildren                            | 数据节点当前的子节点个数                                                             |

> 注：重点关注红色加粗的属性值

### 7.5. 查看节点状态

查看节点状态语法：

```bash
stat [-w] path
```

- 参数 `-w` 添加一个 watch（监视器）

注：`stat` 命令查看节点状态，它的返回值和 `get` 命令类似，但不会返回节点数据

```bash
[zk: localhost:2181(CONNECTED) 5] stat /hadoop
cZxid = 0x6a
ctime = Thu Jun 17 22:37:55 CST 2021
mZxid = 0x6c
mtime = Thu Jun 17 22:38:12 CST 2021
pZxid = 0x6a
cversion = 0
dataVersion = 2
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
```

### 7.6. 查看节点列表

查看节点列表语法：

```bash
ls [-s] [-w] [-R] path
```

- 参数 `-s` 列举出节点详情
- 参数 `-w` 添加一个 watch（监视器）
- 参数 `-R` 列举出节点的级联节点（需要注意：参数必须大写）

```bash
[zk: localhost:2181(CONNECTED) 2] ls /
[dubbo, hadoop, moon0000000004, zero0000000005, zookeeper]
```

### 7.7. 监听器

注册的监听器能够在节点内容发生改变的时候，向客户端发出通知。<font color=red>**需要注意的是 zookeeper 的触发器是一次性的 (One-time trigger)，即触发一次后就会立即失效**</font>。

可以注册监听器的操作分别有：查询节点（`get`）、查询节点状态（`stat`）、查询节点列表（`ls`）

#### 7.7.1. 查看节点时注册监听器

使用`get -w path`命令注册的监听器能够在节点内容发生改变的时候，会向客户端发出一次通知

```bash
[zk: localhost:2181(CONNECTED) 4] get -w /hadoop
[zk: localhost:2181(CONNECTED) 5] set /hadoop 888
WATCHER::
WatchedEvent state:SyncConnected type:NodeDataChanged path:/hadoop
```

#### 7.7.2. 查看节点状态时注册监听器

使用`stat -w path`命令注册的监听器能够在节点状态发生改变的时候，会向客户端发出一次通知

```bash
[zk: localhost:2181(CONNECTED) 6] stat -w /hadoop
[zk: localhost:2181(CONNECTED) 7] set /hadoop 2020
WATCHER::
WatchedEvent state:SyncConnected type:NodeDataChanged path:/hadoop
```

#### 7.7.3. 查看节点列表时注册监听器

使用`ls -w path`命令注册的监听器能够<font color=red>**监听该节点下所有子节点的增加和删除等操作**</font>，会向客户端发出一次通知

```bash
[zk: localhost:2181(CONNECTED) 11] ls -R /hadoop
/hadoop
/hadoop/moon
/hadoop/moon/zero
[zk: localhost:2181(CONNECTED) 12] ls -w /hadoop
[moon]
[zk: localhost:2181(CONNECTED) 13] deleteall /hadoop/moon
WATCHER::
WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/hadoop
```

## 8. Zookeeper的ACL权限控制

### 8.1. ACL 概述

zookeeper 类似文件系统，client 可以创建节点、更新节点、删除节点。Zookeeper的ACL（access control list 访问控制列表）就是实现对节点的权限的控制

ACL 权限控制的基础语法：

```
scheme:id:permission
```

主要涵盖3个维度：

- 权限模式（`scheme`）：授权的策略
- 授权对象（`id`）：授权的对象
- 权限（`permission`）：授予的权限

其特性：

- zooKeeper的权限控制是基于每个znode节点的，需要对每个节点设置权限
- 每个znode支持设置多种权限控制方案和多个权限
- 子节点不会继承父节点的权限，客户端无权访问某节点，但可能可以访问它的子节点

示例：

```bash
# 将节点权限设置为IP:192.168.60.130的客户端可以对节点进行增、删、改、查、管理权限
setAcl /test2 ip:192.168.60.130:crwda
```

### 8.2. scheme 权限模式

定义采用何种方式授权

| 方案值  |                       描述                       |
| :----: | ------------------------------------------------ |
| world  | 只有一个用户：anyone，代表登录zokeeper所有人（默认） |
|   ip   | 对客户端使用IP地址认证                             |
|  auth  | 使用已添加认证的用户认证                           |
| digest | 使用“用户名:密码”方式认证                          |


### 8.3. id 授权的对象

授权对象ID是指，权限赋予的实体，即给谁授予权限。例如：IP 地址或用户。

### 8.4. permission 权限

permission 用于指定授予什么类型的权限

create、delete、read、writer、admin也就是 增、删、改、查、管理权限，这5种权限简写为`cdrwa`。**注意：这5种权限中，delete是指对子节点的删除权限，其它4种权限指对自身节点的操作权限**

|  权限  | ACL简写 |             描述              |
| :----: | :-----: | ---------------------------- |
| create |    c    | 可以创建子节点                 |
| delete |    d    | 可以删除子节点（仅下一级节点）   |
|  read  |    r    | 可以读取节点数据及显示子节点列表 |
| write  |    w    | 可以设置节点数据               |
| admin  |    a    | 可以设置节点访问控制列表权限     |

### 8.5. ACL 授权命令

|  命令   |    描述     |
| :-----: | ----------- |
| getAcl  | 读取ACL权限  |
| setAcl  | 设置ACL权限  |
| addauth | 添加认证用户 |

### 8.6. ACL 权限控制示例

#### 8.6.1. world 授权模式

命令

```bash
setAcl <path> world:anyone:<acl>
```

示例

```bash
[zk: localhost:2181(CONNECTED) 1] create /node1 "node1"
Created /node1
[zk: localhost:2181(CONNECTED) 2] getAcl /node1
'world,'anyone   # world方式对所有用户进行授权
: cdrwa          # 增、删、改、查、管理
[zk: localhost:2181(CONNECTED) 3] setAcl /node1 world:anyone:cdrwa
cZxid = 0x2
ctime = Fri Dec 13 22:25:24 CST 2021
mZxid = 0x2
mtime = Fri Dec 13 22:25:24 CST 2021
pZxid = 0x2
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 5
numChildren = 0
```

#### 8.6.2. IP授权模式

命令

```bash
setAcl <path> ip:<ip>:<acl>
```

示例

```bash
[zk: localhost:2181(CONNECTED) 18] create /node2 "node2"
Created /node2
​
[zk: localhost:2181(CONNECTED) 23] setAcl /node2 ip:192.168.60.129:cdrwa
cZxid = 0xe
ctime = Fri Dec 13 22:30:29 CST 2021
mZxid = 0x10
mtime = Fri Dec 13 22:33:36 CST 2021
pZxid = 0xe
cversion = 0
dataVersion = 2
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 20
numChildren = 0
​
[zk: localhost:2181(CONNECTED) 25] getAcl /node2
'ip,'192.168.60.129
: cdrwa
​
# 使用IP非 192.168.60.129 的机器
[zk: localhost:2181(CONNECTED) 0] get /node2
Authentication is not valid : /node2 # 提示没有权限
```

> 注意：远程登录zookeeper命令是：`./zkCli.sh -server ip`

#### 8.6.3. Auth授权模式

命令

```bash
addauth digest <user>:<password> # 添加认证用户
setAcl <path> auth:<user>:<acl>
```

示例

```bash
zk: localhost:2181(CONNECTED) 2] create /node3 "node3"
Created /node3
​
# 添加认证用户
[zk: localhost:2181(CONNECTED) 4] addauth digest MooN:123456
[zk: localhost:2181(CONNECTED) 1] setAcl /node3 auth:MooN:cdrwa
cZxid = 0x15
ctime = Fri Dec 13 22:41:04 CST 2021
mZxid = 0x15
mtime = Fri Dec 13 22:41:04 CST 2021
pZxid = 0x15
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 5
numChildren = 0

[zk: localhost:2181(CONNECTED) 0] getAcl /node3
'digest,'MooN:673OfZhUE8JEFMcu0l64qI8e5ek=
: cdrwa

# 添加认证用户后可以访问
[zk: localhost:2181(CONNECTED) 3] get /node3
node3
cZxid = 0x15
ctime = Fri Dec 13 22:41:04 CST 2021
mZxid = 0x15
mtime = Fri Dec 13 22:41:04 CST 2021
pZxid = 0x15
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 5
numChildren = 0
```

#### 8.6.4. Digest 授权模式

命令

```bash
setAcl <path> digest:<user>:<password>:<acl>
```

这里密码是经过SHA1及BASE64处理的密文，在SHELL中可以通过以下命令计算：

```bash
echo -n <user>:<password> | openssl dgst -binary -sha1 | openssl base64
```

示例

```bash
[zk: localhost:2181(CONNECTED) 4] create /node4 "node4"
Created /node4

# 使用是上面算好的密文密码添加权限：
[zk: localhost:2181(CONNECTED) 5] setAcl /node4 digest:MooN:qlzQzCLKhBROghkooLvb+Mlwv4A=:cdrwa
cZxid = 0x1c
ctime = Fri Dec 13 22:52:21 CST 2021
mZxid = 0x1c
mtime = Fri Dec 13 22:52:21 CST 2021
pZxid = 0x1c
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 5
numChildren = 0
​
[zk: localhost:2181(CONNECTED) 6] getAcl /node4
'digest,'MooN:qlzQzCLKhBROghkooLvb+Mlwv4A=
: cdrwa
​
[zk: localhost:2181(CONNECTED) 3] get /node4
Authentication is not valid : /node4 # 没有权限
[zk: localhost:2181(CONNECTED) 4] addauth digest MooN:123456 # 添加认证用户
[zk: localhost:2181(CONNECTED) 5] get /node4
1 # 成功读取数据
cZxid = 0x1c
ctime = Fri Dec 13 22:52:21 CST 2019
mZxid = 0x1c
mtime = Fri Dec 13 22:52:21 CST 2019
pZxid = 0x1c
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 5
numChildren = 0
```

#### 8.6.5. 多种模式授权

同一个节点可以同时使用多种模式授权

```bash
[zk: localhost:2181(CONNECTED) 0] create /node5 "node5"
Created /node5
[zk: localhost:2181(CONNECTED) 1] addauth digest MooN:123456 # 添加认证用户
[zk: localhost:2181(CONNECTED) 2] setAcl /node5 ip:192.168.60.129:cdra,auth:MooN:cdrwa,digest:MooN:qlzQzCLKhBROghkooLvb+Mlwv4A=:cdrwa
```

### 8.7. ACL 超级管理员

zookeeper的权限管理模式有一种叫做super，该模式提供一个超管可以方便的访问任何权限的节点。通过以下步骤

1. 假设这个超管是：super:admin，需要先为超管生成密码的密文

```bash
echo -n super:admin | openssl dgst -binary -sha1 | openssl base64
```

2. 打开zookeeper目录下的`/bin/zkServer.sh`服务器脚本文件，找到如下一行：

```shell
nohup $JAVA "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}"
```

该脚本中启动zookeeper的命令，默认只有以上两个配置项，需要加一个超管的配置项

```shell
"-Dzookeeper.DigestAuthenticationProvider.superDigest=super:xQJmxLMiHGwaqBvst5y6rkB6HQs="
```

3. 修改以后这条完整命令变成

```bash
nohup $JAVA "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" "-Dzookeeper.DigestAuthenticationProvider.superDigest=super:xQJmxLMiHGwaqBvst5y6rkB6HQs="\ -cp "$CLASSPATH" $JVMFLAGS $ZOOMAIN "$ZOOCFG" > "$_ZOO_DAEMON_OUT" 2>&1 < /dev/null &
```

4. 启动zookeeper服务，输入如下命令添加认证用户权限

```bash
addauth digest super:admin
```

# Zookeeper 客户端编程

以下章节只展示小部分示例代码，更多API使用示例详见：https://github.com/MooNkirA/dubbo-note/tree/master/zookeeper-sample

## 1. Java客户端 Zookeeper 的使用（官方）

znode是zooKeeper集合的核心组件，zookeeper API提供了一小组方法使用zookeeper集合来操纵znode的所有细节。

客户端应该遵循以步骤，与zookeeper服务器进行清晰和干净的交互。

- 连接到zookeeper服务器。zookeeper服务器为客户端分配会话ID
- 定期向服务器发送心跳。否则，zookeeper服务器将过期会话ID，客户端需要重新连接
- 只要会话ID处于活动状态，就可以获取/设置znode
- 所有任务完成后，断开与zookeeper服务器的连接。如果客户端长时间不活动，则zookeeper服务器将自动断开客户端

### 1.1. maven依赖

```xml
<!-- https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.6.3</version>
</dependency>
```

### 1.2. 连接 Zookeeper 服务端

`ZooKeeper`类构造函数实现连接到zookeeper服务端

```java
ZooKeeper(String connectionString, int sessionTimeout, Watcher watcher)
```

- `connectionString`：zookeeper主机
- `sessionTimeout`：会话超时（以毫秒为单位)
- `watcher`：实现“监视器”对象。zookeeper集合通过监视器对象返回连接状态。

示例：

```java
public static final String CONNECTION_STR = "127.0.0.1:2181";
@Test
public void testZooKeeperConnection() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        // 会话编号
        System.out.println("会话编号: " + zooKeeper.getSessionId());
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.3. 新增节点

使用`ZooKeeper`对象的`create`方法新增节点

```java
// 同步方式
public String create(final String path, byte data[], List<ACL> acl, CreateMode createMode)
// 异步方式
public void create(final String path, byte data[], List<ACL> acl, CreateMode createMode, StringCallback cb, Object ctx)
```

- `path`：znode路径。例如，/node1 /node1/node11
- `data[]`：要存储在指定znode路径中的数据
- `acl`：要创建的节点的访问控制列表。zookeeper API提供了一个静态接口 `ZooDefs.Ids` 来获取一些基本的acl列表。例如，`ZooDefs.Ids.OPEN_ACL_UNSAFE`返回打开znode的acl列表。
- `createMode`：节点的类型，这是一个**枚举**。
- `cb`：异步回调接口
- `ctx`：传递上下文参数

示例：

```java
@Test
public void testZooKeeperCreate() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 创建节点
         * String create(final String path, byte data[], List<ACL> acl, CreateMode createMode)
         *   path: 节点的路径
         *   data[]: 节点的数据
         *   acl: 权限列表。 示例取值：world:anyone:cdrwa
         *   createMode: 节点类型。 示例取值：持久化节点
         */
        zooKeeper.create("/create", "MooN".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.4. 更新节点

使用`ZooKeeper`对象的`setData`方法更新修改节点

```java
// 同步方式
public Stat setData(final String path, byte data[], int version)
// 异步方式
public void setData(final String path, byte data[], int version, StatCallback cb, Object ctx)
```

- `path`：znode路径。
- `data[]`：要存储在指定znode路径中的数据
- `version`：znode的当前版本。每当数据更改时，ZooKeeper会更新znode的版本号。
- `cb`：异步回调接口
- `ctx`：传递上下文参数

示例：

```java
@Test
public void testZooKeeperUpdate() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 同步方式，更新节点
         * Stat setData(final String path, byte data[], int version)
         *   参数 path: 节点的路径
         *   参数 data[]: 节点修改的数据
         *   参数 version: 版本号 -1代表版本号不作为修改条件
         * 如果设置版本号不正确，会报错 “KeeperErrorCode = BadVersion for xxx”
         */
        Stat stat = zooKeeper.setData("/set/node1", "node13".getBytes(), 2);
        // 节点的版本号
        System.out.println(stat.getVersion());
        // 节点的创建时间
        System.out.println(stat.getCtime());
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.5. 删除节点

使用`ZooKeeper`对象的`delete`方法删除节点

```java
// 同步方式
public void delete(final String path, int version)
// 异步方式
public void delete(final String path, int version, VoidCallback cb, Object ctx)
```

- `path`：znode路径
- `version`：znode的当前版本号。`-1`代表删除节点时不考虑版本信息
- `cb`：异步回调接口
- `ctx`：传递上下文参数

示例：

```java
@Test
public void testZooKeeperDelete() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 同步方式，更新节点
         * Stat setData(final String path, byte data[], int version)
         *   参数 path: 节点的路径
         *   参数 data[]: 节点修改的数据
         *   参数 version: 版本号 -1代表版本号不作为修改条件
         * 如果设置版本号不正确，会报错 “KeeperErrorCode = BadVersion for xxx”
         */
        Stat stat = zooKeeper.setData("/set/node1", "node13".getBytes(), 2);
        // 节点的版本号
        System.out.println(stat.getVersion());
        // 节点的创建时间
        System.out.println(stat.getCtime());
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.6. 查看节点

使用`ZooKeeper`对象的`getData`方法查询节点信息

```java
// 同步方式
public byte[] getData(String path, boolean watch, Stat stat)
// 异步方式
public void getData(String path, boolean watch, DataCallback cb, Object ctx)

// 同步方式（可指定监听器）
public byte[] getData(final String path, Watcher watcher, Stat stat)
// 异步方式（可指定监听器）
public void getData(final String path, Watcher watcher, DataCallback cb, Object ctx)
```

- `path`：znode路径
- `watch`：是否使用连接对象中注册的监视器
- `stat`：返回znode的元数据
- `cb`：异步回调接口
- `ctx`：传递上下文参数
- `watcher`：监听器实现

示例：

```java
@Test
public void testZooKeeperGetData() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 同步方式，查看节点
         * byte[] getData(String path, boolean watch, Stat stat)
         *   参数 path: 节点的路径
         *   参数 watch: 是否使用连接对象中注册的监视器
         *   参数 stat: 读取节点属性的对象
         */
        Stat stat = new Stat();
        byte[] bys = zooKeeper.getData("/getData/node1", false, stat);
        // 打印数据
        System.out.println(new String(bys));
        // 版本信息
        System.out.println(stat.getVersion());
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.7. 查看子节点

使用`ZooKeeper`对象的`getChildren`方法查询子节点信息

```java
// 同步方式
public List<String> getChildren(String path, boolean watch, Stat stat)
// 异步方式
public void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx)

// 同步方式（可指定监听器）
public List<String> getChildren(final String path, Watcher watcher, Stat stat)
// 异步方式（可指定监听器）
public void getChildren(final String path, Watcher watcher, ChildrenCallback cb, Object ctx)
```

- `path`：znode路径
- `watch`：是否使用连接对象中注册的监视器
- `stat`：返回znode的元数据
- `cb`：异步回调接口
- `ctx`：传递上下文参数
- `watcher`：监听器实现

示例：

```java
@Test
public void testZooKeeperGetChildren() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 同步方式，查看子节点
         * List<String> getChildren(String path, boolean watch)
         *   参数 path: 节点的路径
         *   参数 watch: 是否使用连接对象中注册的监视器
         */
        List<String> list = zooKeeper.getChildren("/getData", false);
        // 打印数据
        for (String str : list) {
            System.out.println(str);
        }
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 1.8. 检查节点是否存在

使用`ZooKeeper`对象的`exists`方法检查节点是否存在

```java
// 同步方式
public Stat exists(String path, boolean watch)
// 异步方式
public void exists(String path, boolean watch, StatCallback cb, Object ctx)
```

- `path`：znode路径
- `watch`：是否使用连接对象中注册的监视器
- `cb`：异步回调接口
- `ctx`：传递上下文参数

示例：

```java
@Test
public void testZooKeeperExists() {
    try {
        // 创建计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        /* 创建 ZooKeeper 实例即可连接到zookeeper服务端。建立连接本身是一个异步过程 */
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("当前状态：" + event.getState());
                // 判断当前状态是否连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // 连接成功后，释放所有等待的线程
                    countDownLatch.countDown();
                }
            }
        });

        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        /*
         * 同步方式，检查节点
         * Stat exists(String path, boolean watch)
         *   参数 path: 节点的路径
         *   参数 watch: 是否使用连接对象中注册的监视器
         */
        Stat stat = zooKeeper.exists("/exists1", false);
        // stat 为null，代表不存在
        System.out.println(stat.getVersion());
        // 关闭连接
        zooKeeper.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

## 2. Java客户端 Zkclient 的使用

### 2.1. maven依赖

```xml
<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.11</version>
</dependency>
```

### 2.2. 初始化ZKClient对象

建立与zookeeper服务端链接，是同步方法。需要注意的是，原生的zookeeper客户端连接初始化时是一个异步操作（`Zookeeper zk = new Zookeeper()`）。

`ZkClient`提供了7中创建会话的方法：

```java
public ZkClient(String serverstring)

public ZkClient(String zkServers, int connectionTimeout)

public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout)

public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer)

public ZkClient(final String zkServers, final int sessionTimeout, final int connectionTimeout, final ZkSerializer zkSerializer, final long operationRetryTimeout)

public ZkClient(IZkConnection connection)

public ZkClient(IZkConnection connection, int connectionTimeout)

public ZkClient(IZkConnection zkConnection, int connectionTimeout, ZkSerializer zkSerializer)

public ZkClient(final IZkConnection zkConnection, final int connectionTimeout, final ZkSerializer zkSerializer, final long operationRetryTimeout)
```

示例：

```java
@Test
public void testZkclientConnection() throws Exception {
    // 创建 ZkClient 实例即可连接到zookeeper服务端。
    // 需要注意，与原生ZooKeeper创建异步的过程不一样，ZkClient建立连接是同步的
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);

    // 创建文件输出流
    OutputStream ops = new FileOutputStream("E:\\00-Downloads\\zkFolders.txt");
    // 读取zookeeper服务端的文件夹
    zkClient.showFolders(ops);

    // 关闭连接
    zkClient.close();
}
```

> 值得注意的量，与原生`ZooKeeper`创建连接的异步过程不一样，`ZkClient`建立连接是同步的

### 2.3. 新增节点

ZkClient提供了15个创建节点的方法，以下只列出几个常用的方法：

```java
public void createPersistent(String path)

public void createPersistent(String path, boolean createParents)

public void createPersistent(String path, boolean createParents, List<ACL> acl)

public void createPersistent(String path, Object data)

public void createPersistent(String path, Object data, List<ACL> acl)

public String createPersistentSequential(String path, Object data)

public String createPersistentSequential(String path, Object data, List<ACL> acl)

public void createEphemeral(final String path)

public void createEphemeral(final String path, final List<ACL> acl)

public String create(final String path, Object data, final CreateMode mode)

public String create(final String path, Object data, final List<ACL> acl, final CreateMode mode)

public void createEphemeral(final String path, final Object data)

public void createEphemeral(final String path, final Object data, final List<ACL> acl)

public String createEphemeralSequential(final String path, final Object data)

public String createEphemeralSequential(final String path, final Object data, final List<ACL> acl)
```

示例：

```java
@Test
public void testZkclientCreate() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);
    // 创建持久节点
    zkClient.createPersistent("/zkclient", "zkclient");
    // 创建临时节点
    zkClient.createEphemeral("/zkclientTemp", "zkclientTemp");
    // 手动指定权限与类型
    zkClient.create("/zkclient/create", "MooN", ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    // 关闭连接
    zkClient.close();
}
```

### 2.4. 更新节点

更新操作可以通过以下接口来实现：

```java
public void writeData(String path, Object object)

public void writeData(final String path, Object datat, final int expectedVersion)

public Stat writeDataReturnStat(final String path, Object datat, final int expectedVersion)
```

示例：

```java
@Test
public void testZkclientWriteData() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);
    /*
     * void writeData(final String path, Object datat, final int expectedVersion)
     *  参数 path：节点的路径
     *  参数 datat：节点修改的数据
     *  参数 expectedVersion：版本号 -1代表版本号不作为修改条件
     * 如果修改的版本号与当前路径版本不一致，会报错：“KeeperErrorCode = BadVersion”
     */
    zkClient.writeData("/set/node2", "MooN...", -1);
    // 关闭连接
    zkClient.close();
}
```

### 2.5. 删除节点

删除节点提供了以下方法：

```java
public boolean delete(final String path)

public boolean delete(final String path, final int version)

public boolean deleteRecursive(String path)
```

示例：

```java
@Test
public void testZkclientDelete() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);
    /*
     * boolean delete(final String path, final int version)
     *  参数 path：节点的路径
     *  参数 version：版本号 -1代表版本号不作为修改条件
     * 如果删除的版本号与当前路径版本不一致，会报错：“KeeperErrorCode = BadVersion”
     */
    zkClient.delete("/delete/node2", -1);
    // 关闭连接
    zkClient.close();
}
```

### 2.6. 查看节点

获取节点内容有以下接口方法：

```java
public <T extends Object> T readData(String path)

public <T extends Object> T readData(String path, boolean returnNullIfPathNotExists)

public <T extends Object> T readData(String path, Stat stat)
```

通过方法返回参数的定义，就可以得知，返回的结果（节点的内容）已经被反序列化成对象了。

对本接口实现监听的接口为`IZkDataListener`，分别提供了处理数据变化和删除操作的监听：

```java
public void handleDataChange(String dataPath, Object data) throws Exception;

public void handleDataDeleted(String dataPath) throws Exception;
```

示例：

```java
@Test
public void testZkclientReadData() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端，另外 ZkSerializer 用于定义节点存储的数据序列化
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 500000, 5000, new ZkSerializer() {
        @Override
        public byte[] serialize(Object data) throws ZkMarshallingError {
            return new byte[0];
        }

        @Override
        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            return new String(bytes);
        }
    });
    /*
     * <T extends Object> T readData(String path)
     *  参数 path：节点的路径
     * 这里默认会调用另一个重载的方法，returnNullIfPathNotExists 参数值为 false
     * 即如果节点不存在，则会直接抛出异常
     */
    Object data = zkClient.readData("/getData/node1");
    System.out.println(data.toString());
    // 关闭连接
    zkClient.close();
}
```

> 注意读取节点数据的方法，如果创建zkclient连接时没有传入`ZkSerializer`接口的实现，定义节点存储数据的序列化，在读取数据时会抛出“java.io.StreamCorruptedException: invalid stream header”的异常

### 2.7. 查看节点列表

```java
public List<String> getChildren(String path)
```

此接口返回子节点的相对路径列表。比如节点路径为/test/a1和/test/a2，那么当path为/test时，返回的结果为[a1,a2]。

其中在原始API中，对节点注册Watcher，当节点被删除或其下面的子节点新增或删除时，会通知客户端。在ZkClient中，通过Listener监听来实现，后续会将到具体的使用方法。

可以注册的Listener，接口`IZkChildListener`下面的方法来实现：

```java
public void handleChildChange(String parentPath, List<String> currentChilds)
```

示例：

```java
@Test
public void testZkclientConnection() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);
    /*
     * List<String> getChildren(String path)
     *  参数 path：节点的路径
     */
    List<String> children = zkClient.getChildren("/getData/node4");
    for (String child : children) {
        System.out.println(child);
    }
    // 关闭连接
    zkClient.close();
}
```

### 2.8. 监测节点是否存在

此API比较简单，调用以下方法即可：

```java
public boolean exists(final String path)

protected boolean exists(final String path, final boolean watch)
```

示例：

```java
@Test
public void testZkclientExists() throws Exception {
    // 创建 ZkClient 实例连接到zookeeper服务端
    ZkClient zkClient = new ZkClient(CONNECTION_STR, 5000);
    /*
     * boolean exists(final String path)
     *  参数 path：节点的路径
     */
    System.out.println(zkClient.exists("/notExists"));
    System.out.println(zkClient.exists("/exists1"));
    // 关闭连接
    zkClient.close();
}
```

## 3. Java客户端 Curator 的使用

curator是Netflix公司开源的一个zookeeper客户端，后捐献给apache，curator框架在zookeeper原生API接口上进行了包装，解决了很多zooKeeper客户端非常底层的细节开发。提供zooKeeper各种应用场景(比如：分布式锁服务、集群领导选举、共享计数器、缓存机制、分布式队列等)的抽象封装，实现了Fluent风格的API接口，是最好用，最流行的zookeeper的客户端。

原生zookeeperAPI的不足：

- 连接对象异步创建，需要开发人员自行编码等待
- 连接没有自动重连超时机制
- watcher一次注册生效一次
- 不支持递归创建树形节点

curator特点：

- 解决session会话超时重连
- watcher反复注册
- 简化开发api
- 遵循Fluent风格的API
- 提供了分布式锁服务、共享计数器、缓存机制等机制

### 3.1. maven依赖

```xml
<!-- curator 客户端依赖 -->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>2.12.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>2.12.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-test</artifactId>
    <version>2.12.0</version>
</dependency>
```

### 3.2. 连接 Zookeeper 服务端

使用`CuratorFrameworkFactory`工厂的建造者方式创建zookeeper服务端连接

示例：

```java
@Test
public void testCuratorConnection() {
    // 创建连接对象，可使用建造者链接编程方式
    // 与原生ZooKeeper连接对象异步创建不一样，该连接对象的创建是同步
    CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(CONNECTION_STR) // 服务端IP地址与端口号
            .sessionTimeoutMs(5000) // 会话超时时间
            // 设置重连机制。
            // .retryPolicy(new RetryOneTime(3000)) // session重连策略：3秒后重连一次，只重连1次
            // .retryPolicy(new RetryNTimes(3, 3000)) // session重连策略：每3秒重连一次，重连3次
            // .retryPolicy(new RetryUntilElapsed(10000, 3000)) // session重连策略：每3秒重连一次，总等待时间超过10秒后停止重连
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)) // session重连策略：baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))
            .namespace("MooN") // 命名空间
            .build(); // 构建连接对象
    // 打开连接
    client.start();
    System.out.println(client.getState()); // 客户端状态
    System.out.println(client.isStarted()); // 客户端是否连接
    // 关闭连接
    client.close();
}
```

### 3.3. 新增节点

示例：

```java
public class CuratorCreateDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 新增节点 */
    @Test
    public void create1() throws Exception {
        String result = client.create()
                // 节点的类型
                .withMode(CreateMode.PERSISTENT)
                // 节点的权限列表 world:anyone:cdrwa
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                /*
                 * String forPath(final String givenPath, byte[] data)
                 * 指定新增节点，具体实例是 CreateBuilderImpl
                 *  参数 path：节点的路径
                 *  参数 data：节点的数据
                 */
                .forPath("/node1", "MooN".getBytes());
        System.out.println("result is " + result);
    }

    /* 新增节点，设置自定义权限 */
    @Test
    public void create2() throws Exception {
        // 权限列表
        List<ACL> list = new ArrayList<ACL>();
        // 授权模式和授权对象
        Id id = new Id("ip", "127.0.0.1");
        list.add(new ACL(ZooDefs.Perms.ALL, id));
        String result = client.create()
                .withMode(CreateMode.PERSISTENT)
                // 节点的自定义权限列表
                .withACL(list)
                .forPath("/node2", "Zero".getBytes());
        System.out.println("result is " + result);
    }

    /* 递归创建节点树 */
    @Test
    public void create3() throws Exception {
        String result = client.create()
                // 递归节点的创建，如果父节点不存在，将自动创建
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath("/node4/node41/node412/node413", "kira".getBytes());
        System.out.println("result is " + result);
    }

    /* 异步方式创建节点 */
    @Test
    public void create4() throws Exception {
        String result = client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点的路径
                        System.out.println(curatorEvent.getPath());
                        // 事件的类型
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node5", "haha".getBytes());
        Thread.sleep(5000);
        System.out.println("result is " + result);
    }
}
```

### 3.4. 更新节点

示例：

```java
public class CuratorSetDataDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 更新节点 */
    @Test
    public void setData1() throws Exception {
        Stat stat = client.setData()
                /*
                 * Stat forPath(String path, byte[] data)
                 * 指定更新的节点，具体实现是 SetDataBuilderImpl
                 *  参数 path：节点的路径
                 *  参数 data：节点的数据
                 */
                .forPath("/node1", "abc".getBytes());
        System.out.println("节点数据的更改次数: " + stat.getVersion());
        System.out.println("数据节点最后一次更新时的事务 ID : " + stat.getMzxid());
    }

    /* 指定版本号更新节点 */
    @Test
    public void setData2() throws Exception {
        Stat stat = client.setData()
                // 指定版本号，如果版本与修改的节点版本号不一致，会报错“KeeperErrorCode = BadVersion”
                // -1代表版本号不作为修改条件
                .withVersion(-1)
                .forPath("/node1", "L&N".getBytes());
        System.out.println("节点数据的更改次数: " + stat.getVersion());
        System.out.println("数据节点最后一次更新时的事务 ID : " + stat.getMzxid());
    }

    /* 异步方式修改节点数据 */
    @Test
    public void setData3() throws Exception {
        client.setData()
                .withVersion(-1)
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点的路径
                        System.out.println(curatorEvent.getPath());
                        // 事件的类型
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node1", "L?".getBytes());
        Thread.sleep(5000);
        System.out.println("更新节点结束");
    }
}
```

### 3.5. 删除节点

示例：

```java
public class CuratorDeleteDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 删除节点 */
    @Test
    public void delete1() throws Exception {
        client.delete()
                /*
                 * Void forPath(String path)
                 * 指定删除节点，具体实现是 DeleteBuilderImpl
                 *  参数 path：节点的路径
                 */
                .forPath("/node1");
        System.out.println("删除操作结束");
    }

    /* 指定版本号删除节点 */
    @Test
    public void delete2() throws Exception {
        client.delete()
                // 指定版本号，如果版本与修改的节点版本号不一致，会报错“KeeperErrorCode = BadVersion”
                // -1代表版本号不作为修改条件
                .withVersion(-1)
                .forPath("/node2");
        System.out.println("删除操作结束");
    }

    /* 删除节点，包含其子节点 */
    @Test
    public void delete3() throws Exception {
        client.delete()
                // 设置删除其子节点
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath("/node3");
        System.out.println("删除操作结束");
    }

    /* 异步方式删除节点 */
    @Test
    public void delete4() throws Exception {
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点的路径
                        System.out.println(curatorEvent.getPath());
                        // 事件的类型
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node4");
        Thread.sleep(5000);
        System.out.println("删除操作结束");
    }
}
```


### 3.6. 查看节点

示例：

```java
public class CuratorGetDataDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 读取节点数据 */
    @Test
    public void getData1() throws Exception {
        byte[] bys = client.getData()
                /*
                 * byte[] forPath(String path)
                 * 查看指定的节点，具体实现是 GetDataBuilderImpl
                 *  参数 path：节点的路径
                 */
                .forPath("/node1");
        System.out.println(new String(bys));
    }

    /* 读取数据时，读取节点的属性 */
    @Test
    public void getData2() throws Exception {
        // 创建节点属性对象
        Stat stat = new Stat();
        byte[] bys = client.getData()
                // 读取属性
                .storingStatIn(stat)
                .forPath("/node1");
        System.out.println("节点的数据" + new String(bys));
        System.out.println("节点数据的更改次数: " + stat.getVersion());
        System.out.println("数据节点最后一次更新时的事务 ID : " + stat.getMzxid());
    }

    /* 异步方式读取节点的数据 */
    @Test
    public void getData3() throws Exception {
        client.getData()
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点的路径
                        System.out.println(curatorEvent.getPath());
                        // 事件的类型
                        System.out.println(curatorEvent.getType());
                        // 节点的数据
                        System.out.println(new String(curatorEvent.getData()));
                    }
                })
                .forPath("/node1");
        Thread.sleep(5000);
        System.out.println("读取节点的数据结束");
    }
}
```

### 3.7. 查看子节点

示例：

```java
public class CuratorGetChildrenDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 读取子节点数据 */
    @Test
    public void getChildren1() throws Exception {
        List<String> list = client.getChildren()
                /*
                 * List<String> forPath(String path)
                 * 查看指定节点的子节点列表，具体实现是 GetChildrenBuilderImpl
                 *  参数 path：节点的路径
                 *  参数 data：节点的数据
                 */
                .forPath("/node5");
        for (String str : list) {
            System.out.println(str);
        }
    }

    /* 异步方式读取子节点数据 */
    @Test
    public void getChildren2() throws Exception {
        client.getChildren()
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点路径
                        System.out.println(curatorEvent.getPath());
                        // 事件类型
                        System.out.println(curatorEvent.getType());
                        // 读取子节点数据
                        List<String> list = curatorEvent.getChildren();
                        for (String str : list) {
                            System.out.println(str);
                        }
                    }
                })
                .forPath("/node5");
        Thread.sleep(5000);
        System.out.println("异步读取子节点列表结束");
    }
}
```

### 3.8. 检查节点是否存在

示例：

```java
public class CuratorCheckExistsDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* 判断节点是否存在 */
    @Test
    public void checkExists1() throws Exception {
        Stat stat = client.checkExists()
                /*
                 * Stat forPath(String path)
                 * 指定更新的节点，具体实现是 ExistsBuilderImpl
                 *  参数 path：节点的路径
                 */
                .forPath("/node211");
        if (stat == null) {
            System.out.println("节点不存在");
        } else {
            System.out.println("节点数据的更改次数: " + stat.getVersion());
            System.out.println("数据节点最后一次更新时的事务 ID : " + stat.getMzxid());
        }
    }

    /* 异步方式判断节点是否存在 */
    @Test
    public void checkExists2() throws Exception {
        client.checkExists()
                // 异步回调接口
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点路径
                        System.out.println(curatorEvent.getPath());
                        // 事件类型
                        System.out.println(curatorEvent.getType());
                        // 结果码resultCode，存在返回值为0，不存在返回值为-101
                        System.out.println("resultCode: " + curatorEvent.getResultCode());
                        // 获取节点的属性
                        Stat stat = curatorEvent.getStat();
                        if (stat == null) {
                            System.out.println("节点不存在");
                        } else {
                            System.out.println("节点数据的更改次数: " + stat.getVersion());
                            System.out.println("数据节点最后一次更新时的事务 ID : " + stat.getMzxid());
                        }
                    }
                })
                .forPath("/node211");
        Thread.sleep(5000);
        System.out.println("异步方式判断节点结束");
    }
}
```

### 3.9. Watcher API

Curator 客户端提供了两种 `Watcher`(Cache) 来监听结点的变化

- `NodeCache`：只是监听某一个特定的节点，监听节点的新增和修改
- `PathChildrenCache`：监控一个ZNode的子节点。当一个子节点增加、更新、删除时，`PathCache`会改变它的状态，会包含最新的子节点，子节点的数据和状态

值得注意，原生`ZooKeeper`客户端只能注册一次，数据变更后就不能再次监听。而`Curator`的`Watcher`的注册（反复注册）后可以一直监听节点的数据变更

示例：

```java
public class CuratorWatcherDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    @Test
    public void testNodeCache() throws Exception {
        /*
         * 创建NodeCache对象，监视某个节点的数据变化，构造函数如下：
         * NodeCache(CuratorFramework client, String path)
         *   参数 client: 连接对象
         *   参数 path: 连接对象
         * 需要注意的是：如果CuratorFramework设置了namespace,则监听的节点是"namespace+path"
         */
        final NodeCache nodeCache = new NodeCache(client, "/node1");
        // 启动监视器对象
        nodeCache.start();
        // 注册监听器
        nodeCache.getListenable()
                .addListener(new NodeCacheListener() {
                    // 节点每次变化时都回调此方法
                    public void nodeChanged() throws Exception {
                        // 从 NodeCache 对象可获取到节点修改后信息
                        System.out.println(nodeCache.getCurrentData().getPath());
                        System.out.println(new String(nodeCache.getCurrentData().getData()));
                    }
                });

        // 对被监听的节点进行多次修改
        client.setData()
                .forPath("/node1", "我改一下中文看看".getBytes(StandardCharsets.UTF_8));
        client.setData()
                .forPath("/node1", "我改第二次".getBytes(StandardCharsets.UTF_8));

        Thread.sleep(10000); // 休眠主线程，等待监视器的结果输出
        System.out.println("示例结束");
        // 关闭监视器对象
        nodeCache.close();
    }

    @Test
    public void testPathChildrenCache() throws Exception {
        /*
         * 创建 PathChildrenCache 对象，监视某个节点的所有子节点的数据变化，构造函数如下：
         * PathChildrenCache(CuratorFramework client, String path, boolean cacheData)
         *   参数 client: 连接对象
         *   参数 path: 监视的节点路径
         *   参数 cacheData: 事件中是否可以获取节点的数据
         */
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/node5", true);
        // 启动监听
        pathChildrenCache.start();
        // 注册监听器
        pathChildrenCache.getListenable()
                .addListener(new PathChildrenCacheListener() {
                    // 当某个子节点数据变化时，回调此方法（注意是所有子节点都会回调）
                    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                        // 节点的事件类型
                        System.out.println(pathChildrenCacheEvent.getType());
                        // 节点的路径
                        System.out.println(pathChildrenCacheEvent.getData().getPath());
                        // 节点的数据
                        System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
                    }
                });

        // 对被监听的节点进行多次修改
        client.setData()
                .forPath("/node5/node51", "MooNkirA".getBytes());

        Thread.sleep(10000); // 休眠主线程，等待监视器的结果输出
        System.out.println("示例结束");
        // 关闭监听
        pathChildrenCache.close();
    }
}
```

### 3.10. 事务

示例：

```java
public class CuratorTransactionDemo {

    private CuratorFramework client;

    @Before
    public void initConnection() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR) // 服务端IP地址与端口号
                .sessionTimeoutMs(5000) // 会话超时时间
                .retryPolicy(retryPolicy) // 设置重连机制
                .namespace("MooN") // 命名空间
                .build(); // 构建连接对象
        client.start();
    }

    @After
    public void closeConnection() throws Exception {
        client.close();
    }

    /* Curator 控制事务，示例会创建两个节点，并在第二个节点中故意制造语法错误，观察是否支持事务 */
    @Test
    public void testTransaction() throws Exception {
        // 通过 inTransaction() 方法创建 CuratorTransaction 事务操作对象，开启事务
        client.inTransaction()
                .create()
                .forPath("/node6", "MooN".getBytes())
                .and()
                .create()
                .forPath("node7", "Zero".getBytes())
                .and()
                // 提交事务
                .commit();
    }
}
```

### 3.11. 分布式锁（待整理）

Curator 提供了分布式锁服务的抽象封装与实现

- `InterProcessMutex`：分布式可重入排它锁
- `InterProcessReadWriteLock`：分布式读写锁

示例：

```java
```

## 4. Zookeeper 事件监听机制（Watcher）

### 4.1. watcher概念

zookeeper提供了数据的发布/订阅功能，多个订阅者可同时监听某一特定主题对象，当该主题对象的自身状态发生变化时(例如节点内容改变、节点下的子节点列表改变等)，会实时、主动通知所有订阅者

zookeeper采用了Watcher机制实现数据的发布/订阅功能。该机制在被订阅对象发生变化时会异步通知客户端，因此客户端不必在Watcher注册后轮询阻塞，从而减轻了客户端压力。

watcher机制实际上与观察者模式类似，也可看作是一种观察者模式在分布式场景下的实现方式。


### 4.2. watcher架构

Watcher实现由三个部分组成：

- Zookeeper服务端
- Zookeeper客户端
- 客户端的ZKWatchManager对象

客户端首先将`Watcher`注册到服务端，同时将`Watcher`对象保存到客户端的Watch管理器中。当ZooKeeper服务端监听的数据状态发生变化时，服务端会主动通知客户端，接着客户端的Watch管理器会触发相关`Watcher`来回调相应处理逻辑，从而完成整体的数据发布/订阅流程。

![](images/20210622185757976_8975.png)

### 4.3. watcher特性

|     特性      |                                                        说明                                                        |
| ------------ | ----------------------------------------------------------------------------------------------------------------- |
| 一次性         | watcher是一次性的，一旦被触发就会移除，再次使用时需要重新注册                                                           |
| 客户端顺序回调 | watcher回调是顺序串行化执行的，只有回调后客户端才能看到最新的数据状态。一个watcher回调逻辑不应该太多，以免影响别的watcher执行 |
| 轻量级         | WatchEvent是最小的通信单元，结构上只包含通知状态、事件类型和节点路径，并不会告诉数据节点变化前后的具体内容                   |
| 时效性         | watcher只有在当前session彻底失效时才会无效，若在session有效期内快速重连成功，则watcher依然存在，仍可接收到通知              |

### 4.4. Watcher接口设计

`Watcher`是一个接口，任何实现了`Watcher`接口的类就是一个新的`Watcher`。`Watcher`内部包含了两个枚举类：`KeeperState`、`EventType`

![](images/20210622190050999_27895.png)


- `Watcher`通知状态(`KeeperState`)

`KeeperState`是客户端与服务端连接状态发生变化时对应的通知类型。路径为`org.apache.zookeeper.Watcher.Event.KeeperState`，是一个枚举类，其枚举属性如下：

|    枚举属性    |          说明          |
| ------------- | --------------------- |
| SyncConnected | 客户端与服务器正常连接时 |
| Disconnected  | 客户端与服务器断开连接时 |
| Expired       | 会话session失效时      |
| AuthFailed    | 身份认证失败时          |

- `Watcher`事件类型(`EventType`)

`EventType`是数据节点(znode)发生变化时对应的通知类型。其路径为`org.apache.zookeeper.Watcher.Event.EventType`，是一个枚举类，`EventType`变化时`KeeperState`永远处于`SyncConnected`通知状态下；当`KeeperState`发生变化时，`EventType`永远为`None`。

枚举属性如下：

|       枚举属性       |                         说明                         |
| ------------------- | ---------------------------------------------------- |
| None                | 无                                                   |
| NodeCreated         | Watcher监听的数据节点被创建时                          |
| NodeDeleted         | Watcher监听的数据节点被删除时                          |
| NodeDataChanged     | Watcher监听的数据节点内容发生变更时(无论内容数据是否变化) |
| NodeChildrenChanged | Watcher监听的数据节点的子节点列表发生变更时              |

注：客户端接收到的相关事件通知中只包含状态及类型等信息，不包括节点变化前后的具体内容，变化前的数据需业务自身存储，变化后的数据需调用get等方法重新获取

### 4.5. 捕获相应的事件

|               注册方式               | Created | ChildrenChanged | Changed | Deleted |
| :---------------------------------: | ------- | --------------- | ------- | ------- |
|   `zk.exists(“/node-x”,watcher)`    | 可监控  |                 | 可监控  | 可监控  |
|   `zk.getData(“/node-x”,watcher)`   |         |                 | 可监控  | 可监控  |
| `zk.getChildren(“/node-x”,watcher)` |         | 可监控          |         | 可监控  |

### 4.6. 注册Watcher使用示例

#### 4.6.1. 客服端与服务器的连接状态监听

KeeperState 通知状态：

- `SyncConnected`：客户端与服务器正常连接时
- `Disconnected`：客户端与服务器断开连接时
- `Expired`：会话session失效时
- `AuthFailed`：身份认证失败时
​
事件类型为:None

示例：

```java
public class ZKConnectionWatcher implements Watcher {

    // 计数器对象
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    private ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        try {
            // 事件类型
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    countDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接！");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时!");
                    zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new ZKConnectionWatcher());
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败！");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testWatcher() {
        try {
            zooKeeper = new ZooKeeper(CONNECTION_STR, 5000, new ZKConnectionWatcher());
            // 阻塞线程等待连接的创建
            countDownLatch.await();
            // 会话id
            System.out.println(zooKeeper.getSessionId());
            // 添加授权用户
            // zooKeeper.addAuthInfo("digest1", "MooN:123456".getBytes());
            byte[] bs = zooKeeper.getData("/MooN/node1", false, null);
            System.out.println(new String(bs));
            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("结束");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
```

#### 4.6.2. 检查节点是否存在

使用`ZooKeeper`对象的`exists`方法可以指定监听器`Watcher`。可以监听以下状态

- NodeCreated:节点创建
- NodeDeleted:节点删除
- NodeDataChanged:节点内容发生变化

```java
// 使用连接对象的监视器
Stat exists(String path, boolean watch)
// 自定义监视器
Stat exists(final String path, Watcher watcher)
```

- `path`：znode路径。
- `watch`：是否使用连接对象中注册的监视器
- `watcher`：监视器对象

示例：

```java
public class ZKExistsWatcher {

    private ZooKeeper zooKeeper = null;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 连接zookeeper客户端
        zooKeeper = new ZooKeeper(CONNECTION_STR, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象的参数!");
                // 连接成功
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherExists1() throws KeeperException, InterruptedException {
        /*
         *  使用连接对象的监视器
         *  Stat exists(String path, boolean watch)
         *      参数 path:节点的路径
         *      参数 watch:使用连接对象中的watcher
         */
        zooKeeper.exists("/watcher1", true);
        Thread.sleep(50000);
        System.out.println("结束");
    }


    @Test
    public void watcherExists2() throws KeeperException, InterruptedException {
        /*
         * 使用自定义监视器
         * Stat exists(final String path, Watcher watcher)
         *      参数 path:节点的路径
         *      参数 watcher:自定义watcher对象
         */
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherExists3() throws KeeperException, InterruptedException {
        // watcher一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("自定义watcher");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    zooKeeper.exists("/watcher1", this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher1", watcher);
        Thread.sleep(80000);
        System.out.println("结束");
    }


    @Test
    public void watcherExists4() throws KeeperException, InterruptedException {
        // 注册多个监听器对象
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("1");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        Thread.sleep(80000);
        System.out.println("结束");
    }
}
```

#### 4.6.3. 查看节点

使用`ZooKeeper`对象的`getData`方法可以指定监听器`Watcher`。可以监听以下状态

- NodeDeleted:节点删除
- NodeDataChanged:节点内容发生变化

```java
// 使用连接对象的监视器
byte[] getData(String path, boolean watch, Stat stat)
// 自定义监视器
byte[] getData(final String path, Watcher watcher, Stat stat)
```

- `path`：znode路径。
- `watch`：是否使用连接对象中注册的监视器
- `watcher`：监视器对象
- `Stat`：返回znode的元数据

示例：

```java
public class ZKGetDataWatcher {

    private ZooKeeper zooKeeper = null;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 连接zookeeper客户端
        zooKeeper = new ZooKeeper(CONNECTION_STR, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象的参数!");
                // 连接成功
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherGetData1() throws KeeperException, InterruptedException {
        /*
         * 使用连接对象的监视器
         * byte[] getData(String path, boolean watch, Stat stat)
         *      参数 path:节点的路径
         *      参数 watch:使用连接对象中的watcher
         */
        zooKeeper.getData("/watcher2", true, null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetData2() throws KeeperException, InterruptedException {
        /*
         * 自定义监视器
         * byte[] getData(final String path, Watcher watcher, Stat stat)
         *      参数 path:节点的路径
         *      参数 watcher:自定义watcher对象
         */
        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        }, null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetData3() throws KeeperException, InterruptedException {
        // 一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("自定义watcher");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeDataChanged) {
                        zooKeeper.getData("/watcher2", this, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        zooKeeper.getData("/watcher2", watcher, null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetData4() throws KeeperException, InterruptedException {
        // 注册多个监听器对象
        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("1");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeDataChanged) {
                        zooKeeper.getData("/watcher2", this, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, null);
        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("2");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeDataChanged) {
                        zooKeeper.getData("/watcher2", this, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, null);
        Thread.sleep(50000);
        System.out.println("结束");
    }
}
```

#### 4.6.4. 查看子节点

使用`ZooKeeper`对象的`getChildren`方法可以指定监听器`Watcher`。可以监听以下状态

- NodeDeleted:节点删除
- NodeChildrenChanged:子节点发生变化

```java
// 使用连接对象的监视器
List<String> getChildren(String path, boolean watch)
// 自定义监视器
List<String> getChildren(final String path, Watcher watcher)
```

- `path`：znode路径。
- `watch`：是否使用连接对象中注册的监视器
- `watcher`：监视器对象
- `Stat`：返回znode的元数据

示例：

```java
public class ZKGetChildWatcher {

    private ZooKeeper zooKeeper = null;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch connectedSemaphore = new CountDownLatch(1);
        // 连接zookeeper客户端
        zooKeeper = new ZooKeeper(CONNECTION_STR, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象的参数!");
                // 连接成功
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connectedSemaphore.countDown();
                }
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        connectedSemaphore.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherGetChild1() throws KeeperException, InterruptedException {
        /*
         * 使用连接对象的监视器
         * List<String> getChildren(String path, boolean watch)
         *      参数 path:节点的路径
         *      参数 watch:使用连接对象中的watcher
         */
        zooKeeper.getChildren("/watcher3", true);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChild2() throws KeeperException, InterruptedException {
        /*
         * 自定义监视器
         * List<String> getChildren(final String path, Watcher watcher)
         *      参数 path:节点的路径
         *      参数 watcher:自定义watcher对象
         */
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChild3() throws KeeperException, InterruptedException {
        // 一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("自定义watcher");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        zooKeeper.getChildren("/watcher3", this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        zooKeeper.getChildren("/watcher3", watcher);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChild4() throws KeeperException, InterruptedException {
        // 多个监视器对象
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("1");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        zooKeeper.getChildren("/watcher3", this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("2");
                    System.out.println("path=" + event.getPath());
                    System.out.println("eventType=" + event.getType());
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        zooKeeper.getChildren("/watcher3", this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }
}
```
