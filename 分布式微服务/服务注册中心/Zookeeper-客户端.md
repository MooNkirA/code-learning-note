## 1. Zookeeper Java 客户端概述

业界中操作 Zookeeper 的 Java 客户端有很多，常用有：Zookeeper 官方客户端、Zkclient、Curator 等等

## 2. Zookeeper 官方客户端

> 以下章节只展示小部分示例代码，更多API使用示例详见：https://github.com/MooNkirA/dubbo-note/tree/master/zookeeper-sample

znode 是 Zookeeper 集合的核心组件，zookeeper API 提供了一小组方法使用 Zookeeper 集合来操纵 znode 的所有细节。

客户端应该遵循以步骤，与zookeeper服务器进行清晰和干净的交互。

- 连接到zookeeper服务器。zookeeper服务器为客户端分配会话ID
- 定期向服务器发送心跳。否则，zookeeper服务器将过期会话ID，客户端需要重新连接
- 只要会话ID处于活动状态，就可以获取/设置znode
- 所有任务完成后，断开与zookeeper服务器的连接。如果客户端长时间不活动，则zookeeper服务器将自动断开客户端

### 2.1. maven 依赖

```xml
<!-- https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.6.3</version>
</dependency>
```

### 2.2. 连接 Zookeeper 服务端

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

### 2.3. 新增节点

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

### 2.4. 更新节点

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

### 2.5. 删除节点

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

### 2.6. 查看节点

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

### 2.7. 查看子节点

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

### 2.8. 检查节点是否存在

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

## 3. Zkclient

### 3.1. maven依赖

```xml
<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.11</version>
</dependency>
```

### 3.2. 初始化ZKClient对象

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

### 3.3. 新增节点

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

### 3.4. 更新节点

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

### 3.5. 删除节点

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

### 3.6. 查看节点

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

### 3.7. 查看节点列表

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

### 3.8. 监测节点是否存在

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

## 4. Curator

curator 是 Netflix 公司开源的一个 zookeeper 客户端，后捐献给 apache，curator 框架在 zookeeper 原生 API 接口上进行了包装，解决了很多 ZooKeeper 客户端非常底层的细节开发。提供 ZooKeeper 各种应用场景(比如：分布式锁服务、集群领导选举、共享计数器、缓存机制、分布式队列等)的抽象封装，实现了 Fluent 风格的 API 接口，是最好用，最流行的 zookeeper 的客户端。

原生 zookeeperAPI 的不足：

- 连接对象异步创建，需要开发人员自行编码等待
- 连接没有自动重连超时机制
- watcher 一次注册生效一次
- 不支持递归创建树形节点

curator 特点：

- 解决 session 会话超时重连
- watcher 反复注册
- 简化开发 api
- 遵循 Fluent 风格的 API
- 提供了分布式锁服务、共享计数器、缓存机制等机制

### 4.1. maven依赖

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

### 4.2. 连接 Zookeeper 服务端

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

### 4.3. 新增节点

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

### 4.4. 更新节点

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

### 4.5. 删除节点

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


### 4.6. 查看节点

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

### 4.7. 查看子节点

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

### 4.8. 检查节点是否存在

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

### 4.9. Watcher API

Curator 客户端提供了两种 `Watcher`(Cache) 来监听结点的变化

- `NodeCache`：只是监听某一个特定的节点，监听节点的新增和修改
- `PathChildrenCache`：监控一个ZNode的子节点。当一个子节点增加、更新、删除时，`PathCache`会改变它的状态，会包含最新的子节点，子节点的数据和状态

值得注意，原生`ZooKeeper`客户端只能注册一次，数据变更后就不能再次监听。而`Curator`的`Watcher`的注册（反复注册）后可以一直监听节点的数据变更。

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

### 4.10. 事务

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

### 4.11. 分布式锁（待整理）

Curator 提供了分布式锁服务的抽象封装与实现

- `InterProcessMutex`：分布式可重入排它锁
- `InterProcessReadWriteLock`：分布式读写锁
