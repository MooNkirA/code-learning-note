## 1. MongoDb 概述

MongoDB 是一个跨平台的，面向文档的数据库，是当前 NoSQL 数据库产品中最热门的一种。它介于关系数据库和非关系数据库之间，是非关系数据库当中功能最丰富，最像关系数据库的产品。它支持的数据结构非常松散，是类似 JSON 的 BSON 格式，因此可以存储比较复杂的数据类型。

### 1.1. MongoDB 特点

MongoDB 最大的特点是他支持的查询语言非常强大，其语法有点类似于面向对象的查询语言，几乎可以实现类似关系数据库单表查询的绝大部分功能，而且还支持对数据建立索引。它是一个面向集合的，模式自由的文档型数据库，具体特点总结如下：

1. 面向集合存储，易于存储对象类型的数据
2. 模式自由
3. 支持动态查询
4. 支持完全索引，包含内部对象
5. 支持复制和故障恢复
6. 使用高效的二进制数据存储，包括大型对象（如视频等）
7. 自动处理碎片，以支持云计算层次的扩展性
8. 支持 Python，PHP，Ruby，Java，C，C#，Javascript，Perl 及 C++语言的驱动程序，社区中也提供了对 Erlang 及.NET 等平台的驱动程序
9. 文件存储格式为 BSON（一种 JSON 的扩展）

### 1.2. MongoDB 体系结构

MongoDB 的逻辑结构是一种层次结构。主要由：数据库(database)、集合(collection)、文档(document)这三部分组成的。逻辑结构是面向用户的，用户使用 MongoDB 开发应用程序使用的就是逻辑结构。

1. MongoDB 的文档（document），相当于关系数据库中的一行记录。
2. 多个文档组成一个集合（collection），相当于关系数据库的表。
3. 多个集合（collection），逻辑上组织在一起，就是数据库（database）。
4. 一个 MongoDB 实例支持多个数据库（database）。

数据库(database)、集合(collection)、文档(document)的层次结构如下图

![MongoDB 体系结构](images/20190408143212537_9428.png)

在 mongodb 中是通过数据库、集合、文档的方式来管理数据，下边是 mongodb 与关系数据库的一些概念对比：

| SQL 术语/概念 | MongoDB 术语/概念 |                    解释/说明                    |
| :----------: | :--------------: | ---------------------------------------------- |
|   database   |     database     | 数据库                                          |
|    table     |    collection    | 数据库表/集合                                    |
|     row      |     document     | 数据记录行/文档                                  |
|    column    |      field       | 数据字段/域                                      |
|    index     |      index       | 索引                                            |
| table joins  |        \         | 表连接（MongoDB 不支持）                          |
| primary key  |   primary key    | 主键。MongoDB 自动在每个集合中添加名称为`\_id`的主键 |

1. 一个 mongodb 实例可以创建多个数据库
2. 一个数据库可以创建多个集合
3. 一个集合可以包括多个文档。

## 2. 连接 mongodb

mongodb 的使用方式是客户服务器模式，即使用一个客户端连接 mongodb 数据库（服务端）。

### 2.1. 命令格式连接

```shell
mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
```

参数解释：

- `mongodb://` 固定前缀
- `username`：账号，可不填
- `password`：密码，可不填
- `host`：主机名或 ip 地址，只有 host 主机名为必填项。
- `port`：端口，可不填，默认 27017
- `/database`：连接某一个数据库
- `?options`：连接参数，key/value 对

例子：

```shell
mongodb://localhost     # 连接本地数据库27017端口
mongodb://root:123456@localhost     # 使用用户名root密码为123456连接本地数据库27017端口
mongodb://localhost,localhost:27018,localhost:27019    # 连接三台主从服务器，端口为27017、27018、27019
```

### 2.2. 使用 mongodb 自带的 javascript shell（mongo.exe）连接

windows 版本的 mongodb 安装成功，在安装目录下的 bin 目录有 mongo.exe 客户端程序。进入 cmd 窗口执行 mongo.exe：

![](images/20190408144445592_10748.png)

此时就可以输入命令来操作 mongodb 数据库了，javascript shell 可以运行 javascript 程序。

### 2.3. 使用 studio3T 连接

内容参考本笔记的[《MongoDB 安装与使用》](/数据库/MongoDb/MongoDB-安装与使用)的《studio3t》章节

### 2.4. 使用 java 程序连接

> 详细参数参考：http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/connect-to-mongodb/

添加依赖：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo‐java‐driver</artifactId>
    <version>3.4.3</version>
</dependency>
```

测试程序：

```java
@Test
public void testConnection() {
    //创建mongodb 客户端
    MongoClient mongoClient = new MongoClient("localhost", 27017);
    //或者采用连接字符串
    //MongoClientURI connectionString = new MongoClientURI("mongodb://root:root@localhost:27017");

    //MongoClient mongoClient = new MongoClient(connectionString);
    //连接数据库
    MongoDatabase database = mongoClient.getDatabase("test");
    // 连接collection
    MongoCollection<Document> collection = database.getCollection("student");
    // 查询第一个文档
    Document myDoc = collection.find().first();
    //得到文件内容 json串
    String json = myDoc.toJson();
    System.out.println(json);
}
```

## 3. 数据库操作

### 3.1. 查询数据库

- 查询全部数据库

```
show dbs
```

- 显示当前数据库

```
db
```

### 3.2. 创建/切换数据库

- 命令格式：

```
use DATABASE_NAME
```

- 例子：

```
use test02
# 有test02数据库则切换到此数据库，没有则创建。
```

**注意：新创建的数据库不显示，需要至少包括一个集合。**

### 3.3. 删除数据库（慎用！！！）

命令格式：

```
db.dropDatabase()
```

例子：

```
# 删除test02数据库
# 先切换数据库
use test02
# 再执行删除
db.dropDatabase()
```

## 4. 集合操作

集合相当于关系数据库中的表，一个数据库可以创建多个集合，一个集合是将相同类型的文档管理起来。

### 4.1. 创建集合

```
# 语法：
db.createCollection(name, options)
# 参数name: 新创建的集合名称
# 参数options: 创建参数
```

### 4.2. 删除集合

```
# 语法：
db.集合名称.drop()
# 例子：删除student集合
db.student.drop()
```

### 4.3. 显示当前数据库中集合

```
# 语法：
show tables
```

### 4.4. 给集合创建索引

#### 4.4.1. 命令语句创建

#### 4.4.2. 使用 studio 3t 软件创建

1. 右键点击需要增加索引的集合

![](images/20200420230413176_23718.png)

- 选择索引相应的域

![](images/20200420230849873_9104.png)

- 生成唯一索引

![](images/20200420231216319_18685.png)

![](images/20200420231441375_4007.png)

## 5. 文档操作

### 5.1. 插入文档

- mongodb 中文档的格式是 json 格式，下边就是一个文档，包括两个 key：\_id 主键和 name

```json
{
    "_id" : ObjectId("5b2cc4bfa6a44812707739b5"),
    "name" : "moon"
}
```

- 插入命令格式：

```
db.COLLECTION_NAME.insert(document)
```

**注：每个文档默认以 `_id` 作为主键，主键默认类型为 `ObjectId`（对象类型），mongodb 会自动生成主键值。**

- 例子：往 student 集合中插入一条文档

```
db.student.insert({"name":"moon","age":10})
```

**注意：同一个集合中的文档的 key 名称与个数可以不相同！但是建议设置为相同的。**

### 5.2. 更新文档

- 命令格式：

```
db.COLLECTION_NAME.update(<query>, <update>, <options>)
# 参数query：查询条件，相当于sql语句的where
# 参数update：更新文档内容
# 参数options：选项
```

#### 5.2.1. 替换文档

将符合条件 `"name":"moon"` 的第一个文档替换为 `{"name":"斩月","age":10}`。

```
db.student.update({"name":"moon"},{"name":"斩月","age":10})
```

#### 5.2.2. $set 修改器

- 使用 `$set` 修改器指定要更新的 key，key 不存在则创建，存在则更新。
- 将符合条件 "name":"斩月" 的所有文档更新 name 和 age 的值。

```
db.student.update({"name":"斩月"},{$set:{"name":"天锁斩月","age":10}},{multi:true})
```

_注：参数 multi：false 表示更新第一个匹配的文档，true 表示更新所有匹配的文档_

### 5.3. 删除文档

- 命令格式：

```shell
db.COLLECTION_NAME.remove(<query>)
# 参数query：删除条件，相当于sql语句中的where
```

1. 删除所有文档

```
db.COLLECTION_NAME.remove({})
```

2. 删除符合条件的文档

```
db.COLLECTION_NAME.remove({"name":"moon"})
```

### 5.4. 查询文档

- 命令格式：

```
db.COLLECTION_NAME.find(query, projection)
# query：查询条件，可不填
# projection：投影查询key，可不填
```

1. 查询全部

```
db.COLLECTION_NAME.find()
```

2. 查询符合条件的记录

```
# 查询name等为"斩月"的文档。
db.COLLECTION_NAME.find({"name":"斩月"})
```

3. 投影查询

```
# 只显示name和age两个key，_id主键不显示。
db.COLLECTION_NAME.find({"name":"斩月"}, {name:1, age:1, _id:0})
```

**注：1 代表显示，0 代表不显示**

## 6. 用户

### 6.1. 创建用户

- 语法格式

```
mongo>db.createUser(
    {
        user: "<name>",
        pwd: "<cleartext password>",
        customData: { <any information> },
        roles: [
            { role: "<role>", db: "<database>" } | "<role>",
            ...
        ]
    }
)
```

- 例子：创建 root 用户，角色为 root

```shell
# 切换到admin数据库
use admin
# 创建用户
db.createUser(
    {
        user:"root",
        pwd:"123",
        roles:[{role:"root",db:"admin"}]
    }
)
```

- 内置角色如下：
  1. 数据库用户角色：read、readWrite;
  2. 数据库管理角色：dbAdmin、dbOwner、userAdmin；
  3. 集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
  4. 备份恢复角色：backup、restore；
  5. 所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
  6. 超级用户角色：root

### 6.2. 认证登录

为了安全需要，Mongodb 要打开认证开关，即用户连接 Mongodb 要进行认证，其中就可以通过账号密码方式进行认证。

1. 在 mongo.conf 中设置`auth=true`

```conf
# 开启认证登陆
auth=true
```

2. 重启 Mongodb
3. 使用账号和密码连接数据库

1）mongo.exe 连接

```
.\mongo.exe -u root -p 123 --authenticationDatabase admin
```

2）Studio 3T 连接

![Studio 3T使用账户登陆](images/20190408154600799_31808.png)

### 6.3. 查询用户

查询当前库下的所有用户：

```
show users
```

### 6.4. 删除用户

- 语法格式：

```
db.dropUser("用户名")
```

- 例子：删除 test1 用户

```
db.dropUser("test1")
```

### 6.5. 修改用户

- 语法格式：

```
db.updateUser(
	"<username>",
  {
    customData : { <any information> },
    roles : [
      { role: "<role>", db: "<database>" } | "<role>",
      ...
    ],
    pwd: "<cleartext password>"
  },
  writeConcern: { <write concern> })
```

- 例子：

```
# 先创建test1用户：
db.createUser(
    {
        user:"test1",
        pwd:"test1",
        roles:[{role:"root",db:"admin"}]
    }
)

# 修改test1用户的角色为readWriteAnyDatabase
use admin
db.updateUser("test1",{roles:[{role:"readWriteAnyDatabase",db:"admin"}]})
```

### 6.6. 修改密码

- 语法格式：

```
db.changeUserPassword("username","newPasswd")
```

- 例子：修改 test1 用户的密码为 123

```
use admin
db.changeUserPassword("test1","123")
```
