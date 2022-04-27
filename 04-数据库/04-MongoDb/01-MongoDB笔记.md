# MongoDB 安装与使用

## 1. MongoDB 介绍

MongoDB 是一个跨平台的，面向文档的数据库，是当前 NoSQL 数据库产品中最热门的一种。它介于关系数据库和非关系数据库之间，是非关系数据库当中功能最丰富，最像关系数据库的产品。它支持的数据结构非常松散，是类似 JSON 的 BSON 格式，因此可以存储比较复杂的数据类型。

## 2. MongoDB 官网与下载

MongoDB的官方网站地址：https://www.mongodb.com/

MongoDB的下载地址：http://dl.mongodb.org/dl/win32/x86_64


## 3. MongoDB（windows版）
### 3.1. 安装 MongoDB

在 win7 系统安装 mongodb 需要 vc++ 运行库，如果没有则会提示“无法启动此程序，因为计算机中丢失“VCRUNTIME140.dll”。上网查找安装

运行 mongodb-win32-x86_64-2008plus-ssl-4.0.8-signed.msi 安装

![安装MongoDB](images/20190408115140944_32544.png)

![安装MongoDB](images/20190408115318126_10187.png)

![安装MongoDB](images/20190408123145139_2442.png)

直接默认即可，点击next

![安装MongoDB](images/20190408123313179_30135.png)

取消勾选，不安装图形化工具，否则时间非常非常长。

![安装MongoDB](images/20190408123431914_20189.png)

### 3.2. 启动 MongoDB

#### 3.2.1. 进入MongoDB的安装目录，创建相关文件

有几个文件夹具体如下（如果安装的版本没有，则手动创建）

- 数据库路径（data目录）
- 日志路径（log目录）
- 日志文件（log目录下，创建mongo.log文件）

![MongoDB目录结构](images/20190408123816591_13462.png)

#### 3.2.2. 创建配置文件mongo.conf

增加如下内容

```conf
# 数据库路径
dbpath=D:\development\MongoDB\Server\4.0\data
# 日志输出文件路径
logpath=D:\development\MongoDB\Server\4.0\log\mongo.log
# 错误日志采用追加模式
logappend=true
# 启用日志文件，默认启用
journal=true
# 这个选项可以过滤掉一些无用的日志信息，若需要调试使用请设置为false
quiet=true
# 端口号 默认为27017
port=27017
```

#### 3.2.3. 安装 MongoDB服务

通过执行`bin/mongod.exe`，使用`--install`选项来安装服务，使用`--config`选项来指定之前创建的配置文件。cmd进入D:\development\MongoDB\Server\4.0\bin

```shell
mongod.exe --config "D:\development\MongoDB\Server\4.0\mongo.conf" --install
```

#### 3.2.4. 启动MongoDB服务（需要使用管理员打开）

```shell
# 使用显示名称
net start "MongoDB Server"
# 或者使用服务名称
net start MongoDB
```

#### 3.2.5. 关闭MongoDB服务（需要使用管理员打开）

```shell
# 使用显示名称
net stop "MongoDB Server"
# 或者使用服务名称
net start MongoDB
```

#### 3.2.6. 移除MongoDB服务

```shell
"d:\MongoDB\Server\3.4\bin\mongod.exe" --remove
```

#### 3.2.7. 测试是否启动成功

启动mongodb服务，命令执行后，浏览器中输入http://127.0.0.1:27017看到如下界面即说明启动成功

![](images/20190408130442382_5581.png)

也可以通过bin目录下的mongo.exe连接mongodb

## 4. MongoDB（docker版）
### 4.1. 查看可用的 MongoDB 版本

访问 MongoDB 镜像库地址： https://hub.docker.com/_/mongo?tab=tags&page=1。

### 4.2. 安装dokcer版本MongoDB

- 创建MongoDB容器

```bash
# 搜索镜像
docker search mongo
# 拉取镜像
docker pull mongo:4.0.18
# 查看本地的镜像
docker images

# 创建容器运行挂载的目录
mkdir -pv /usr/local/software/mongodb/data/db
mkdir -pv /usr/local/software/mongodb/log

# 运行容器。--auth：需要密码才能访问容器服务。
docker run -id --name mongo -v /usr/local/software/mongodb/data/db:/usr/local/software/mongodb/data/db -v /usr/local/software/mongodb/log:/usr/local/software/mongodb/log -p 27017:27017 mongo:4.0.18 --auth
```

- 查看容器运行情况

```bash
docker ps -a
```

![](images/20200419085029850_6991.png)

### 4.3. 进入容器

- 连接容器

```bash
# 登陆容器
docker exec -it mongo /bin/bash
```

![](images/20200419085440761_14675.png)

- 修改配置文件

```bash
# docker版本的mongoDb配置文件位置 /etc/mongod.conf.orig
cd /etc
# 更新源
apt-get update
# 安装 vim(因为容器没有vim)
apt-get install vim
# 修改mongoDb的配置文件
vim /etc/mongod.conf.orig
```

**修改注意点**

```
1.确保注释掉`# bindIp: 127.0.0.1` 或者改成`bindIp: 0.0.0.0` 即可开启远程连接
2.开启权限认证
security：
  authorization: enabled # 注意缩进，参照其他的值来改，若是缩进不对可能导致后面服务不能重启
```

> 注：以下配置文件的格式是v4.0版本以后的配置

![](images/20200419093039481_3503.png)

- 重启容器

```bash
docker restart mongo
```

### 4.4. 进入mongoDB

- 启动容器之后，使用admin进入

```bash
docker exec -it mongo mongo admin
```

- 创建管理员用户

```bash
# 切换数据库
use admin
# 创建一个名为 root，密码为 123 的用户。
db.createUser({ user:'root',pwd:'123',roles:[ { role:'root', db: 'admin'}]});
# 尝试使用上面创建的用户信息进行连接。
db.auth('root', '123')
# 退出
exit
```

![](images/20200419122053157_3366.png)

- 开放27017端口

```bash
firewall-cmd --zone=public --add-port=27017/tcp --permanent
```

### 4.5. 测试docker容器是否已经对外开放服务

![](images/20200419094225020_23794.png)

### 4.6. 使用Navicat连接测试（连接有问题，暂未解决）

![](images/20200419095323556_12399.png)

如果连接时报以下错误

![](images/20200419103130479_29841.png)

- 出错的原因：操作系统开启了ipv6支持，但是mongodb未开启ipv6支持
- 解决办法有2个内
1. 删除操作系统中ipv6解析（注意是mongoDB容器的系统中的hosts）

删除`/etc/hosts`中 `::1 localhost` 这一行的内容

![](images/20200419104013816_713.png)

2. 开启mongod的ipv6支持，修改参数如下：

```
A、
    ./bin/mongod --filePermissions 0777 --ipv6
B、
    filePermissions=0777
    ipv6=true
```

> <font color="red">**注：按网上的方法暂时无法解决，但使用studio3t是可以连接**</font>


## 5. studio3t

- studio3t是mongodb优秀的客户端工具。官方网站：https://studio3t.com/
- 下载安装后运行程序，创建一个新连接：

![](images/20190408142406535_26007.png)

![](images/20190408142504574_1493.png)

- 填写连接信息：

![](images/20190408142716469_10071.png)

![](images/20190408142802612_7888.png)

- 修改字体：默认Studio3t的字体太小，需要修改字体。点击菜单：Edit --> Preferences

![](images/20190408142622245_12800.png)


# MongoDb 基础笔记

## 1. MongoDb 入门
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

在mongodb中是通过数据库、集合、文档的方式来管理数据，下边是mongodb与关系数据库的一些概念对比：

| SQL 术语/概念 | MongoDB术语/概念 | 解释/说明                                          |
| :-----------: | :--------------: | -------------------------------------------------- |
|   database    |     database     | 数据库                                             |
|     table     |    collection    | 数据库表/集合                                      |
|      row      |     document     | 数据记录行/文档                                    |
|    column     |      field       | 数据字段/域                                        |
|     index     |      index       | 索引                                               |
|  table joins  |        \         | 表连接（MongoDB不支持）                            |
|  primary key  |   primary key    | 主键。MongoDB自动在每个集合中添加名称为“_id”的主键 |

1. 一个mongodb实例可以创建多个数据库
2. 一个数据库可以创建多个集合
3. 一个集合可以包括多个文档。

## 2. 连接mongodb

mongodb的使用方式是客户服务器模式，即使用一个客户端连接mongodb数据库（服务端）。

### 2.1. 命令格式连接

```shell
mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
```

- 参数解释
    - `mongodb://` 固定前缀
    - `username`：账号，可不填
    - `password`：密码，可不填
    - `host`：主机名或ip地址，只有host主机名为必填项。
    - `port`：端口，可不填，默认27017
    - `/database`：连接某一个数据库
    - `?options`：连接参数，key/value对

> 例子

```shell
mongodb://localhost     # 连接本地数据库27017端口
mongodb://root:itcast@localhost     # 使用用户名root密码为itcast连接本地数据库27017端口
mongodb://localhost,localhost:27018,localhost:27019    # 连接三台主从服务器，端口为27017、27018、27019
```

### 2.2. 使用mongodb自带的javascript shell（mongo.exe）连接

- windows 版本的mongodb安装成功，在安装目录下的bin目录有mongo.exe客户端程序
- cmd状态执行mongo.exe：

![](images/20190408144445592_10748.png)

- 此时就可以输入命令来操作mongodb数据库了，javascript shell可以运行javascript程序。


### 2.3. 使用studio3T连接

内容参考本笔记的 《5.studio3t》

### 2.4. 使用java程序连接

- 详细参数：http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/connect-to-mongodb/
- 添加依赖：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo‐java‐driver</artifactId>
    <version>3.4.3</version>
</dependency>
```

- 测试程序：

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
#再执行删除
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


#### 4.4.2. 使用studio 3t软件创建

1. 右键点击需要增加索引的集合

![](images/20200420230413176_23718.png)

- 选择索引相应的域

![](images/20200420230849873_9104.png)

- 生成唯一索引

![](images/20200420231216319_18685.png)

![](images/20200420231441375_4007.png)

## 5. 文档操作
### 5.1. 插入文档

- mongodb中文档的格式是json格式，下边就是一个文档，包括两个key：_id主键和name

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

**注：每个文档默认以_id作为主键，主键默认类型为ObjectId（对象类型），mongodb会自动生成主键值。**

- 例子：往student集合中插入一条文档

```
db.student.insert({"name":"moon","age":10})
```

**注意：同一个集合中的文档的key名称与个数可以不相同！但是建议设置为相同的。**

### 5.2. 更新文档

- 命令格式：

```
db.COLLECTION_NAME.update(<query>, <update>, <options>)
# 参数query：查询条件，相当于sql语句的where
# 参数update：更新文档内容
# 参数options：选项
```

#### 5.2.1. 替换文档

将符合条件 "name":"moon"的第一个文档替换为{"name":"斩月","age":10}。

```
db.student.update({"name":"moon"},{"name":"斩月","age":10})
```

#### 5.2.2. $set修改器

- 使用$set修改器指定要更新的key，key不存在则创建，存在则更新。
- 将符合条件 "name":"斩月" 的所有文档更新name和age的值。

```
db.student.update({"name":"斩月"},{$set:{"name":"天锁斩月","age":10}},{multi:true})
```

*注：参数multi：false表示更新第一个匹配的文档，true表示更新所有匹配的文档*

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

**注：1代表显示，0代表不显示**

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

- 例子：创建root用户，角色为root

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

为了安全需要，Mongodb要打开认证开关，即用户连接Mongodb要进行认证，其中就可以通过账号密码方式进行认证。

1. 在mongo.conf中设置`auth=true`

```conf
# 开启认证登陆
auth=true
```

2. 重启Mongodb
3. 使用账号和密码连接数据库

1）mongo.exe连接

```
.\mongo.exe -u root -p 123 --authenticationDatabase admin
```

2）Studio 3T连接

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

- 例子：删除test1用户

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

- 例子：修改test1用户的密码为123

```
use admin
db.changeUserPassword("test1","123")
```
