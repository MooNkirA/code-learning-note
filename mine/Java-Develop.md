# java-develop 本地开发资源信息

> Java 本地开发资源信息（开发环境密码、连接地址、端口等信息）

## 1. 本地开发软件

### 1.1. MySQL

#### 1.1.1. MySQL Server 8.0.27.1（3306）

- 安装位置：`D:\development\MySQL\MySQL Server 8.0\`
- 端口号：3306
- 用户名/密码：root/123456

#### 1.1.2. MySQL Server 5.7.25 免安装版（3307）

- 安装位置：`D:\development\MySQL\mysql-5.7.25-winx64\`
- 端口号：3307
- 用户名/密码：root/123456

### 1.2. Nacos 服务注册中心（8848）

- 版本：2.0.3
- 安装位置：`D:\deployment-environment\nacos\`
- 默认端口号：8848
- 访问地址：http://127.0.0.1:8848/nacos
- 用户名/密码：nacos/nacos

服务启动脚本：

```bash
cd /d D:\deployment-environment\nacos\bin\
startup.cmd -m standalone
```

> 已配置使用本地mysql保存数据，保存的数据库是 `nacos`

### 1.3. Sentinel 分布式服务架构的流量控制组件（9898）

- 版本：1.8.0
- 安装位置：`D:\deployment-environment\sentinel\`
- 端口号：9898 (默认是8080)
- 访问地址：http://127.0.0.1:9898
- 用户名/密码：sentinel/sentinel

服务启动脚本：

```bash
cd /d D:\deployment-environment\sentinel\
java -Dserver.port=9898 -Dcsp.sentinel.dashboard.server=localhost:9898 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.0.jar
```

### 1.4. redis (windows版 server)（6379）

- 安装位置：`D:\development\Redis-x64-3.2.100\`1.4. - 端口：6379
- 启动脚本：

```bash
cd /d D:\development\Redis-x64-3.2.100
start call redis-server redis.windows.conf
```

### 1.5. zipkin 日志数据收集（9411）

- 版本：2.23.16
- 安装位置：`E:\deployment-environment\zipkin\`
- 端口：9411
- 访问地址：http://127.0.0.1:9411
- 启动命令：

```bash
java -jar E:\deployment-environment\zipkin\zipkin-server-2.23.16-exec.jar
```

### 1.6. ActiveMQ 消息中间件

- 版本：5.15.3
- 安装位置：`E:\deployment-environment\apache-activemq-5.15.3\`
- 端口：61616/8161/5672/61613/61614/1883
- 后台访问地址： http://127.0.0.1:8161
- 账号/密码：admin/admin
- 启动命令：

```bash
E:\deployment-environment\apache-activemq-5.15.3\bin\win64\activemq.bat
```

### 1.7. RocketMQ 消息中间件

#### 1.7.1. 服务端（9876）

- 版本：4.9.2
- 安装位置：`E:\deployment-environment\RocketMQ\rocketmq-4.9.2\`
- 端口：9876

#### 1.7.2. 控制台（7777）

- 版本：1.0.0
- 安装位置：`E:\deployment-environment\RocketMQ\rocketmq-console\target\`
- 端口：7777
- 访问地址：http://127.0.0.1:7777
- 启动命令：

```bash
java -jar rocketmq-console-ng-1.0.0.jar --server.port=7777 --rocketmq.config.namesrvAddr=127.0.0.1:9876
```

> 注：这是 spring boot 工程打包后的 jar，打包时没有指定端口号和 RocketMQ 服务地址，所以可能通过启动命令来修改相应的配置

### 1.8. kafka 分布式的发布-订阅消息系统

> 注：放到 E 盘的 deployment-environment 目录中，启动时会提示“输入行太长”，所以直接放到 E 盘根目录中

- 版本：2.8.1
- 安装位置：`E:\kafka_2.13-2.8.1\`
- 端口：zk 占用 2181 / kafka 占用 9092
- 启动命令

```shell
# 进入根目录
cd /d E:\kafka_2.13-2.8.1\

# 启动 zk
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
# 启动 kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### 1.9. Seata 服务（9000）

- 版本：0.9.0
- 安装位置：`E:\deployment-environment\seata\`
- 端口：9000

> 注：seata 配置了使用 nacos 作为注册与配置中心，所以需要先启动 nacos 服务。

### 1.10. Apollo 配置中心

#### 1.10.1. 基本信息

> 目前使用 v1.6.1 版本（学习apollo课程、万信金融项目使用，待转用高版本后删除），v1.9.1版本（未启用）

- v1.6.1 版本安装地址：`E:\deployment-environment\apollo\`
- v1.9.1 版本安装地址：`E:\deployment-environment\apollo-v1.9.1\`
- 占用的端口：8070（`apollo-portal`）, 8080（`apollo-configservice`）, 8090（`apollo-adminservice`）
- 账号/密码：
    - apollo/admin（默认）
    - - moon/123456

#### 1.10.2. 启动脚本

> 在 apollo 目录下执行如下命令(根据实际情况修改)。可通过 `-Dserver.port=xxxx` 修改默认端口

- 启动 apollo-configservice

```bash
java ‐Xms256m ‐Xmx256m ‐Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloConfigDB?characterEncoding=utf8 ‐Dspring.datasource.username=root ‐Dspring.datasource.password=123456 ‐jar apollo-configservice-1.6.1.jar
```

- 启动 apollo-adminservice

```bash
java ‐Xms256m ‐Xmx256m ‐Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloConfigDB?characterEncoding=utf8 ‐Dspring.datasource.username=root ‐Dspring.datasource.password=123456 ‐jar apollo-adminservice-1.6.1.jar
```

- 启动 apollo-portal

```bash
java ‐Xms256m ‐Xmx256m ‐Ddev_meta=http://localhost:8080/ ‐Dserver.port=8070 ‐Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloPortalDB?characterEncoding=utf8 ‐Dspring.datasource.username=root ‐Dspring.datasource.password=123456 ‐jar apollo-portal-1.6.1.jar
```

- 启动全部3个服务（按需修改数据库连接地址，数据库以及密码，日志保存位置等）

```bash
echo

set url="localhost:3306"
set username="root"
set password="123456"

start "configService" java -Xms256m -Xmx256m -Dapollo_profile=github -Dspring.datasource.url=jdbc:mysql://%url%/ApolloConfigDB?characterEncoding=utf8 -Dspring.datasource.username=%username% -Dspring.datasource.password=%password% -Dlogging.file=.\logs\apollo-configservice.log -jar .\apollo-configservice-1.6.1.jar
start "adminService" java -Xms256m -Xmx256m -Dapollo_profile=github -Dspring.datasource.url=jdbc:mysql://%url%/ApolloConfigDB?characterEncoding=utf8 -Dspring.datasource.username=%username% -Dspring.datasource.password=%password% -Dlogging.file=.\logs\apollo-adminservice.log -jar .\apollo-adminservice-1.6.1.jar
start "ApolloPortal" java -Xms256m -Xmx256m -Dapollo_profile=github,auth -Ddev_meta=http://localhost:8080/ -Dserver.port=8070 -Dspring.datasource.url=jdbc:mysql://%url%/ApolloPortalDB?characterEncoding=utf8 -Dspring.datasource.username=%username% -Dspring.datasource.password=%password% -Dlogging.file=.\logs\apollo-portal.log -jar .\apollo-portal-1.6.1.jar
```

### 1.11. Nginx

- 版本：1.20.2
- 安装位置：`D:\development\nginx-1.20.2\`
- 端口：80
- 启动/关闭脚本：

```bash
set NGINX_PATH=D:\development\nginx-1.20.2

echo.开启Nginx代理......
cd /d %NGINX_PATH%
start nginx
echo.开启Nginx代理成功！
pause 
```

```bash
echo.关闭Nginx代理......
cd /d %NGINX_PATH%
nginx -s stop
echo.关闭Nginx代理成功！
pause
```

### 1.12. ElasticStack

#### 1.12.1. ElasticSearch

- 版本：7.17.1
- 安装位置：`E:\deployment-environment\ElasticStack\elasticsearch-7.17.1\`
- 端口：9200
- 运行命令：

```bash
E:\deployment-environment\ElasticStack\elasticsearch-7.17.1\bin\elasticsearch.bat
```

#### 1.12.2. elasticsearch-head

- 版本：0.0.0
- 安装位置：`E:\deployment-environment\ElasticStack\elasticsearch-head\`
- 端口：9100
- 访问地址：http://localhost:9100
- 运行命令：

```bash
cd /d E:\deployment-environment\ElasticStack\elasticsearch-head\
npm run start
```

#### 1.12.3. logstash

- 版本：7.17.1
- 安装位置：`E:\deployment-environment\ElasticStack\logstash-7.17.1\`
- 运行命令：

```bash
cd /d E:\deployment-environment\ElasticStack\logstash-7.17.1\bin\
.\logstash.bat -f ..\config\mysql-es.conf
```

### 1.13. ZooKeeper

- 版本：3.6.3
- 安装位置：`E:\deployment-environment\apache-zookeeper-3.6.3-bin\`
- 端口：
    - Zookeeper Server：2181
    - Zookeeper AdminServer：9999
- 运行命令：

```bash
cd /d E:\deployment-environment\apache-zookeeper-3.6.3-bin\bin\
zkServer.cmd
```

> 注：Zookeeper 3.5 的新特性：会启动 Zookeeper AdminServer，默认使用 8080 端口，已修改为 9999

### 1.14. Memcached 缓存服务

- 版本：1.4.4-14
- 安装位置：`E:\deployment-environment\memcached\`
- 端口：11211
- 安装与卸载命令（**以管理员身份运行cmd**）：

```bash
# 安装服务
E:\deployment-environment\memcached\memcached.exe -d install
# 卸载服务
E:\deployment-environment\memcached\memcached.exe -d uninstall
```

- 启用和停止命令（正常运行cmd 即可），也可以直接在【服务】面板中右键运行或者停止

```bash
# 启动服务
E:\deployment-environment\memcached\memcached.exe -d start
# 停止服务
E:\deployment-environment\memcached\memcached.exe -d stop
```

## 2. linux系统

### 2.1. 练习使用的linux系统（centOS）

#### 2.1.1. 账号/IP/虚拟网卡

- 用户名：root  密码：123456
- 本机IP：192.168.12.1
- 虚拟机IP：192.168.12.132
- 虚拟机使用的网卡（VMnet 8）

#### 2.1.2. redis（port: 6379）

- redis-4.0.12版本，安装位置：/usr/local/redis
- redis (docker 版本) 脚本存放的位置（红包雨场景实战）：`/usr/local/script/redis.sh`

```shall
docker run -id --name=redis -p 6379:6379 redis
```

#### 2.1.3. zookeeper（port: 2181）

- zookeeper (docker 版本)
    - 脚本存放的位置（红包雨场景实战）：`/usr/local/script/zookeeper.sh`

```shall
docker run -id --name=zookeeper -v /opt/data/zksingle:/data -p 2181:2181 -e ZOO_LOG4J_PROP="INFO,ROLLINGFILE" zookeeper
```

#### 2.1.4. RabbitMQ（prot: 4369）

- 【docker版本】rabbitmq:management。映射的端口0.0.0.0:4369->4369/tcp, 0.0.0.0:5671-5672->5671-5672/tcp, 0.0.0.0:15671-15672->15671-15672/tcp, 0.0.0.0:25672->25672/tcp

```bash
# 启动容器
docker run -d -p 15672:15672  -p 5672:5672  -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin --name rabbitmq --hostname=rabbitmqhostone  rabbitmq:management
# 防火墙中打开4369端口
firewall-cmd --zone=public --add-port=4369/tcp --permanent
```

参数说明：

- 15672 ：表示 RabbitMQ 控制台端口号，可以在浏览器中通过控制台来执行 RabbitMQ 的相关操作。
- 5672 : 表示 RabbitMQ 所监听的 TCP 端口号，应用程序可通过该端口与 RabbitMQ 建立 TCP 连接，完成后续的异步消息通信
- RABBITMQ_DEFAULT_USER：用于设置登陆控制台的用户名，这里我设置 admin
- RABBITMQ_DEFAULT_PASS：用于设置登陆控制台的密码，这里我设置 admin
- 容器启动成功后，可以在浏览器输入地址：http://ip:15672/ 访问控制台

如果不修改默认账号与密码，则初始账号/密码是：guest/guest

管理界面地址：http://192.168.12.132:15672/#/

#### 2.1.5. MySQL

- 【docker版本】MySQL（port:3306）

```bash
# 搜索镜像
docker search mysql
# 拉取镜像
docker pull mysql:5.7
# 创建守护式容器
docker run -id --name=mysql3306 -p 3306:3306 -v /usr/docker/mysql3306/data:/var/lib/mysql -v /usr/docker/mysql3306/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=123456 --privileged=true mysql:5.7
# 防火墙中打开3306端口
firewall-cmd --zone=public --add-port=3306/tcp --permanent
```

红包雨场景项目使用的脚本如下： 脚本存放的位置（红包雨场景实战）：`/usr/local/script/mysql.sh`

```bash
docker run -id --name=mysql3306 -v /opt/data/mysql:/var/lib/mysql -p3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7.4
```

- 【docker版本】MySQL（port:3307）

```bash
# 创建守护式容器
docker run -id --name=mysql3307 -p 3307:3306 -v /usr/docker/mysql3307/data:/var/lib/mysql -v /usr/docker/mysql3307/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=123456 --privileged=true mysql:5.7
# 防火墙中打开3307端口
firewall-cmd --zone=public --add-port=3307/tcp --permanent
```

##### 2.1.5.1. 安装时出现问题

用docker创建MySQL无法访问的问题（WARNING: IPv4 forwarding is disabled. Networking will not work）

有两种解决

1. 在 `/usr/lib/sysctl.d/00-system.conf` 配置文件末尾添加如下脚本

```
net.ipv4.ip_forward=1
```

2 在 `/etc/sysctl.conf` 配置文件末尾添加如下脚本

```
net.ipv4.ip_forward=1
```

重启网络

```bash
systemctl restart network
```

#### 2.1.6. MongoDB(未安装)

- 部署地址：http://192.168.12.132:27017
- 使用docker安装，版本是4.0.18
- 用户所属数据库：admin 用户名：root 密码：123

> ps. 相关的安装过程详见

#### 2.1.7. RocketMQ

- 安装目录：/usr/local/software/rocketmq-all-4.4.0-bin-release
- 端口：9876

#### 2.1.8. nginx（port: 80）

- 【docker版本】nginx

```bash
# 搜索镜像
docker search nginx
# 拉取镜像
docker pull nginx
# 创建守护式容器
docker run --name nginx -v /opt/data/nginx/html:/usr/share/nginx/html:ro -v /opt/app/back/upload:/usr/share/nginx/upload:ro -v /opt/data/nginx/nginx.conf:/etc/nginx/nginx.conf:ro -p 80:80 --privileged=true -id nginx
```

映射的配置文件位置：`/opt/data/nginx/nginx.conf`

红包雨场景项目使用的脚本如下： 脚本存放的位置（红包雨场景实战）：`/usr/local/script/nginx.sh`

> 注意问题，因为是挂载nginx的配置文件，首次创建容器时会提示挂载失败，因为docker将 nginx.conf 当作目录，所以需要进入挂载的目录将“nginx.conf”目录删除，再创建“nginx.conf”文件即可，参考脚本如下：

```bash
cd /opt/data/nginx/
rm -rf nginx.conf/
# 创建文件
touch nginx.conf
```

#### 2.1.9. Jenkins（未安装）

yum 方式安装，安装命令如下：

```bash
yum install jenkins
```

### 2.2. 项目二linux部署地址（pinyougou）

- 本机IP：192.168.12.1
- 虚拟机对应的网卡（VMnet 8）
  - 账号：root
  - 密码：123456
  - IP：192.168.12.131

> 注：镜像已上传到百度云盘与阿里云盘，本地已删除

### 2.3. 练习使用的linux系统（CentOS 8）

- 本机IP：192.168.12.1
- 虚拟机对应的网卡（VMnet 8）
  - 用户名：root  密码：123456
  - 用户名2：moon 密码：123456
  - 虚拟机IP：192.168.12.133

### 2.4. 好客租房项目使用的linux系统（Ubuntu(x64)-haoke）

- 本机IP：192.168.12.1
- 虚拟机对应的网卡（VMnet 8）
  - 用户名：root  密码：123456
  - 用户名2：moonkira 密码：123456
  - 虚拟机IP：192.168.12.134

## 3. 常用软件默认端口号

- tomcat：8080
    - eclipse默认的tomcat端口（ports）：
    - Tomcat admin port：8005
    - HTTP/1.1：8080
    - AJP/1.3：8009
- mysql：3306
- redis：6379
    - 连接虚拟机的redis地址:192.168.187.10
- oracle：1521
- solr：8983
    - 默认提供的demo访问地址：http://127.0.0.1:8983/solr
    - 自定义部署到tomcat服务器上：http://127.0.0.1:8082/solr
        - Tomcat admin port：8006
        - HTTP/1.1：8082
        - AJP/1.3：8010
    - 部署solr服务时，将tomcat环境变量名称修改了
- MongoDB：27017
- RabbitMQ：15672
- zipkin：9411

## 4. 部署tomcat配置虚拟目录（图片）

- 部署到tomcat服务器上：apache-tomcat-9.0.5_virtualPic
  - 部署的虚拟目录1：http://127.0.0.1:8000/pic
  - 部署的虚拟目录2：http://127.0.0.1:8000/
  - Tomcat admin port：8001
  - HTTP/1.1：8000
  - AJP/1.3：8009

## 5. oracle

- 口令：moon
  - SCOTT的口令:123456
  - HR的口令:123456

- oracle连接的虚拟网络
  - 子网IP:192.168.187.0	子网掩码:255.255.255.0
  - win10设置VMware Network Adapter VMnet11：
    - 子网IP:192.168.187.1	子网掩码:255.255.255.0
  - 虚拟机XP系统设置
    - 子网IP:192.168.187.10	子网掩码:255.255.255.0

## 6. SVN

本地搭建的仓库地址：

- Repository Type: FSFS
- Repository Name: svn_repository
- Repository URL:
    - https://MoonKirA:8888/svn/svn_repository
    - https://MoonKirA:8888/svn/svn_repository/testProject/trunk
- username: moon
- pw: 123456

## 7. git

- linux系统远程仓库地址：ssh://git@192.168.217.128/home/git/mytest.git
- 用户名：git   密码：git

## 8. ActiveMQ

- 端口号：8161
- 启动访问：http://localhost:8161/admin
- 账号密码都是admin

## 9. 常用网址

- web服务：http://www.webxml.com.cn/zh_cn/index.aspx
	- 天气预报接口：http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl

# JAVA转义字符

```
\b退格键;\t Tab键;\n换行符号;\f进纸;\r回车键;\\反斜杠;\'单引号;\"双引号;
```

# 工具jar包
## 1. java第三方工具jar包
### 1.1. BeanUtils 需要导入 jar 包

1. commons-beanutils-1.9.3.jar		工具核心包
2. commons-logging-1.2.jar			日志记录组件
3. commons-collections-3.2.2.jar	增强的集合包

### 1.2. JUnit

使用Eclipse自带jar带，@Test再导入

### 1.3. IO流工具包相关jar包

- commons-io-2.4.jar（使用）
- commons-io-2.6.jar (requires JDK 1.7+)

### 1.4. DOM4J 导入 jar包

dom4j-1.6.1.jar

### 1.5. MySQL驱动包相关jar包

mysql-connector-java-5.1.44

### 1.6. C3P0连接池相关jar包

- c3p0-0.9.5.2.jar
- mchange-commons-java-0.2.11.jar

### 1.7. DBCP连接池相关jar包

- commons-dbcp-1.4.jar	核心包
- commons-pool-1.6.jar	连接池包

### 1.8. DbUtils工具类相关jar包

- commons-dbutils-1.7.jar  核心包
- commons-logging-1.1.1.jar  日志记录包
- 或 commons-logging-1.2.jar

### 1.9. 处理文件上传的工具类jar包

- commons-io-1.4.jar
- commons-fileupload-1.2.1.jar

### 1.10. JSON-LIB 工具

- json-lib 是将 java 对象与 json 数据相互转换的工具。
    - 第三方工具，使用时需要导入 jar 包
- commons-lang-2.6.jar
- ezmorph-1.0.6.jar
- json-lib-2.4-jdk15.jar

### 1.11. Java连接Redis，需要导入jar包

- commons-pool2-2.3.jar
- jedis-2.7.0.jar

### 1.12. Gson工具类，需要导入jar包

- gson-2.2.4.jar

### 1.13. PDF文档处理java组件
- iText
    - iText是著名的开放源码的站点sourceforge一个项目，是用于生成PDF文档的一个java类库。通过iText不仅可以生成PDF或rtf的文档，而且可以将XML、Html文件转化为PDF文件
- PDFBox是Java实现的PDF文档协作类库，提供PDF文档的创建、处理以及文档内容提取功能，也包含了一些命令行实用工具。主要特性包括：
	- 从PDF提取文本
	- 合并PDF文档
	- PDF 文档加密与解密
	- 与[Lucene](http://www.oschina.net/p/lucene)搜索引擎的集成
	- 填充PDF/XFDF表单数据
	- 从文本文件创建PDF文档
	- 从PDF页面创 建图片
	- 打印PDF文档

## 2. SSH框架相关jar包

### 2.1. Hibernate 导入核心jar包

- 必须的支撑包：hibernate-release-5.0.12.Final\lib\required
- JPA实现包：hibernate-release-5.0.12.Final\lib\jpa
- 可选的组件（如：第三方缓存，连接池）：hibernate-release-5.0.12.Final\lib\optional
- 数据库MySQL驱动的jar包：mysql-connector-java-5.1.44-bin.jar

### 2.2. struts2 导入的核心jar包

- 在struts-2.3.32\apps\struts2-blank.war的空项目，里面lib的jar就是struts2框架的最小配置包
- 支持注解的插件包:struts-2.3.32\lib\struts2-convention-plugin-2.3.32.jar

### 2.3. spring 核心jar包

- 导入核心jar包(IOC)
	- spring-beans-4.2.4.RELEASE.jar
	- spring-context-4.2.4.RELEASE.jar
	- spring-core-4.2.4.RELEASE.jar
	- spring-expression-4.2.4.RELEASE.jar
- 基于注解的配置的jar包
    - spring-aop-4.2.4.RELEASE.jar
- 2个支撑jar包
    - commons-logging-1.2.jar
    - dom4j-1.6.1.jar
- Spring的aop操作基本jar包：
    - spring-aop-4.2.4.RELEASE.jar
    - spring-aspects-4.2.4.RELEASE.jar
- aop相关的jar包
    - aopalliance-1.0.jar
    - aspectjweaver-1.8.7.jar
- JdbcTemplate相关jar包
    - spring-jdbc-4.2.4.RELEASE.jar
    - spring-orm-4.2.4.RELEASE.jar
    - spring-tx-4.2.4.RELEASE.jar

### 2.4. Spring框架xml配置schema约束

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:tx="http://www.springframework.org/schema/tx"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
							http://www.springframework.org/schema/beans/spring-beans.xsd
							http://www.springframework.org/schema/context
							http://www.springframework.org/schema/context/spring-context.xsd
							http://www.springframework.org/schema/aop
							http://www.springframework.org/schema/aop/spring-aop.xsd
							http://www.springframework.org/schema/tx
							http://www.springframework.org/schema/tx/spring-tx.xsd">

</beans>
```

### 2.5. SpringMVC框架xml配置schema约束

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context-4.0.xsd">

</beans>
```

## 3. JavaScript相关插件js

### 3.1. jQuery 相关JS文件

- jquery-1.8.3.min.js
- jquery-2.1.0.min.js

### 3.2. 表单验证的validate插件

- jquery-1.8.3.min.js
- jquery.validate.min.js 验证框架(在 dist 目录下)
- messages_cn.js 一些默认的错误提示(在 dist\localization\目录下)

### 3.3. Bootstrap

1. 复制三个文件夹(css、fonts、js)的内容到项目目录下
2. 复制jQuery.js文件到项目中，建议使用1.9以上的版本


### 3.4. 页面使用JSTL

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

### 3.5. 页面使用OGNL

<%@ taglib prefix="s" uri="/struts-tags" %>

### 3.6. Bootstrap模板

``` html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Bootstrap模板</title>
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/jquery-2.1.0.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
  </head>
  <body>
  </body>
</html>
```

### 3.7. EasyUI的使用

下载程序库并导入EasyUI的CSS和Javascript文件到页面。

```html
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easyui.min.js"></script>
// 导入本地化显示文件
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui-lang-zh_CN.js"></script>
```

- 注意：上述 CSS 和 JS 路径以实际路径为准。引入css文件时顺序不限，但引入js文件时，先引入jquery的，后引入easyui的
- 导入文件：
    - js：jquery-1.11.3.min.js/jquery.easyui.min.js
    - themes文件夹

### 3.8. zTree的使用

- 导入包：zTreeStyle.css/jquery-1.4.4.min.js/jquery.ztree.core-3.5.js/js/jquery.ztree.excheck-3.5.js

``` html
<link rel="stylesheet" href="../../../css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="../../../js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="../../../js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript" src="../../../js/jquery.ztree.excheck-3.5.js"></script>
```

# 其他

## 1. maven私服配置(setting.xml)

- 说明：以下两个配置都需要在教室使用。

1. 外网地址是在大家使用学校帐号登录连接外网使用
2. 内网地址是在大家不能上网的时候使用（建议大家都配置内网地址）
3. 阿里云仓库地址，可以上外网的情况下使用（大家以后在工作中，可以使用这个，下载速度要比maven中央仓库快）

```xml

外网使用（在教室，使用学校分配的帐号登录上网以后使用）：
<mirror>
	<id>nexus</id>
	<mirrorOf>*</mirrorOf>
	<url>http://172.16.2.20:8081/nexus/content/repositories/central/</url>
 </mirror>
内网使用（在教室，不能上网使用）：
<mirror>
	<id>nexus</id>
	<mirrorOf>*</mirrorOf>
	<url>http://192.168.50.20:8081/nexus/content/repositories/central/</url>
 </mirror>
 阿里云（只要可以上网，就能使用）：
<mirror>
  <id>nexus</id>
  <mirrorOf>*</mirrorOf>
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
</mirror>
```

## 2. 脚本备份

### 2.1. 内外网IP切换（适用win7系统）

```bash
@echo off

　　rem //设置变量

　　set NAME="本地连接"

　　rem //以下属性值可以根据需要更改

　　set ADDR=192.168.14.73

　　set MASK=255.255.254.0

　　set GATEWAY=192.168.14.1

　　set DNS1=10.17.65.13

　　set DNS2=10.202.253.28

　　rem //以上属性依次为IP地址、子网掩码、网关、首选DNS、备用DNS

　　echo 当前可用操作有：

　　echo 1 设置为静态IP

　　echo 2 设置为动态IP

　　echo 3 退出

　　echo 请选择后回车：

　　set /p operate=

　　if %operate%==1 goto 1

　　if %operate%==2 goto 2

　　if %operate%==3 goto 3

　　:1

　　echo 正在设置静态IP,请稍等…

　　rem //可以根据你的需要更改

　　echo IP地址 = %ADDR%

　　echo 掩码 = %MASK%

　　echo 网关 = %GATEWAY%

　　netsh interface ipv4 set address name=%NAME% source=static addr=%ADDR% mask=%MASK% gateway=%GATEWAY% gwmetric=0 >nul

　　echo 首选DNS = %DNS1%

　　netsh interface ipv4 set dns name=%NAME% source=static addr=%DNS1% register=PRIMARY >nul

　　echo 备用DNS = %DNS2%

　　netsh interface ipv4 add dns name=%NAME% addr=%DNS2% index=2 >nul

　　echo 静态IP已设置!

　　pause

　　goto 3

　　:2

　　echo 正在设置动态IP,请稍等…

　　echo 正在从DHCP自动获取IP地址…

　　netsh interface ip set address "本地连接" dhcp

　　echo 正在从DHCP自动获取DNS地址…

　　netsh interface ip set dns "本地连接" dhcp

　　echo 动态IP已设置!

　　pause

　　goto 3

　　:3

　　exit
```

### 2.2. 内外网IP切换（适用win10系统）.20171122

```bash
@echo off
rem //设置变量 
set NAME="以太网"
rem //以下属性值可以根据需要更改
set ADDR=192.168.14.73
set MASK=255.255.254.0
set GATEWAY=192.168.14.1
set DNS1=10.17.65.13
set DNS2=10.202.253.28
rem //以上属性依次为IP地址、子网掩码、网关、首选DNS、备用DNS


echo 当前可用操作有：
echo 1 设置为静态IP
echo 2 设置为动态IP
echo 3 退出
echo 请选择后回车：
set /p operate=
if %operate%==1 goto 1
if %operate%==2 goto 2
if %operate%==3 goto 3


:1
echo 正在设置静态IP，请稍等...
rem //可以根据你的需要更改 
echo IP地址 = %ADDR%
echo 掩码 = %MASK%
echo 网关 = %GATEWAY%
netsh interface ipv4 set address %NAME% static %ADDR% %MASK% %GATEWAY% 
echo 首选DNS = %DNS1% 
netsh interface ipv4 set dns %NAME% static %DNS1%
echo 备用DNS = %DNS2% 
if "%DNS2%"=="" (echo DNS2为空) else (netsh interface ipv4 add dns %NAME% %DNS2%) 
echo 静态IP已设置！
pause
goto 3


:2
echo 正在设置动态IP，请稍等...
echo 正在从DHCP自动获取IP地址...
netsh interface ip set address %NAME% dhcp
echo 正在从DHCP自动获取DNS地址...
netsh interface ip set dns %NAME% dhcp 
echo 动态IP已设置！
pause
goto 3


:3
exit
```

## 3. 一些软件

※宝典1：源代码管理软件-“CODEHELP”

- CodeHelp是专门为我们程序员设计的一款源代码管理软件。它能方便的管理您在编程和学习中有用的源代码，减少经常到处查找资料的劳动，节省您在开发中的时间和精力。网上有很多下载地址，而且有绿色版的，保存数据库为Access格式，可以备份和还原数据库，很方便！

- XML Marker(xml查看编辑工具)

XML Marker是国外的一款非常实用的xml查看编辑工具。软件功能强大，纯文本调试输出和日志文件，你可以有效增加修改你的程序才能产生XML格式他们的作用。你也可以使用XML标记的图形功能，以现场隐藏的趋势，并更快地解决你的错误。更多的功能包括表格排序，语法高亮编辑器和自动缩进，经常编辑XML文件的用户可以下载本软件使用。

- .tar.gz

以.tar.gz为扩展名的是一种压缩文件，在Linux和OSX下常见，Linux和OSX都可以直接解压使用这种压缩文件。windows下的WinRAR也可以使用。相当于常见的RAR和ZIP格式
.tar只是将文件打包，文件的大小没什么变化，一般用tar -cvf filename.tar filename格式；.tar.gz是加入了gzip的压缩命令，会将文件压缩存放，可以有效压缩文件的大小，以便于缩短传输时间或者释放磁盘空间，一般用tar -czvf filename.tar.gz filename。同样的解包的时候使用 tar -xvf filename.tar和tar -xzvf filename.tar.gz。


## 4. 开启服务

- MySQL 手动开启--服务名称：MySQL
- nexus Maven私服 手动开启--服务名称：nexus-webapp
- VisualSVN Server 手动开启--服务名称：VisualSVNServer

## 5. 架构师相关推荐书籍

- 领域驱动设计
- 架构整洁之道
- 微服务设计
- 架构即未来：现代企业可扩展的Web架构、流程和组织
- 淘宝技术这十年（已有，但需要找更好资源替换）
- 分布式服务框架：原理与实践
- 大型网站技术架构：核心原理与案例分析（已有）
- 大型网站系统与Java中间件实践（已有）
- 企业IT架构转型之道：阿里巴巴中台战略思想与架构实战
- 尽在双11：阿里巴巴技术演进与超越

# 代码仓库
## 1. github仓库

### 1.1. java学习阶段练习案例代码备份仓库

clone地址：git@github.com:MooNkirA/java-code-back-up.git

### 1.2. maven仓库备份

clone地址：git@github.com:MooNkirA/maven-repository.git

### 1.3. MoonZero个人管理系统项目

- 前端UI仓库：git@github.com:MooNkirA/jav-project-ui.git
- 后端管理仓库：git@github.com:MooNkirA/moonzero-system.git

## 2. 开源项目

### 2.1. ngx-admin

- 基于 Angular 2, Bootstrap 4 和 Webpack 的后台管理面板框架
- Clone地址：it@github.com:akveo/ngx-admin.git

### 2.2. vue-Element-Admin

- 一个基于 vue2.0 和 Eelement 的控制面板 UI 框架。
- Clone地址：git@github.com:PanJiaChen/vue-element-admin.git

### 2.3. iview-admin

- 基于 iView 的 Vue 2.0 控制面板。
- Clone地址：git@github.com:iview/iview-admin.git

### 2.4. AdminLTE

- 非常流行的基于 Bootstrap 3.x 的免费的后台 UI 框架。
- Clone地址：git@github.com:almasaeed2010/AdminLTE.git

### 2.5. alita

- 一套把React Native代码转换成微信小程序代码的转换引擎工具
- 仓库地址：https://github.com/areslabs/alita

# 【待处理】

## 1. 学习遗留问题

- Day04Exercise2_04
	- 为什么for循环会提示超出数组范围
- day12 test01_06_07
- day13 test10 使用递归
- day14
- day15
- day16训练案例6 优化
- day17 test01 修改
- day18 test02_03后 、做一下BeanUtils的日期转换器
- day19 test01_02后
- day20 Day20Test02_01需要修改
- day21 关卡2题目1 倒序显示、关卡3题目1
- day22 关卡3题目2
- day23 关卡3题目1
- day24 关卡3题目1
- day25 关卡3题目1
- day29 关卡3题目2
- day30 结合课堂案例：制作会动的时钟
- day31 关卡2
- day32 剩下两个文档
- day38 完善添加图书案例，将添加的图书显示在添加的页面中。
- day40 选做：实现简易版本购物车/作业的最后一题动物管理系统
- day41 人力管理系统增加filter过滤
- 添加数据：增加返回值，判断是否新增成功
- 在数据库没有数据时，查询所有会报错
- 修改批量删除

```
上网查询一下关于日期的正则表达式。
/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/
""''


综合案例：商品管理系统
http://localhost:8080/ECSystem/admin/home.jsp
http://localhost:8080/ECSystem_page/admin/home.jsp

javaweb综合项目
​	第一次：http://localhost:8080/javawebshop/
​	后台版：http://localhost:8080/javaweb_back
​	在线支付版：http://localhost:8080/javawebshop_pay
​	第二次练习：http://localhost:8080/testShop/
待优化项：修改购物车商品数量是不能为负数或0

linux部署项目
http://192.168.34.128:8080/day47_EasyUI

使用Jquery判断价格不能为负数
优化：在ProductService层进行那个筛选条件的判断。然后再传递那个拼接好的sql语句和占位符参数数组


补充day61最后内容

day68-spring-05-aop-hibernate-annotation ,无法同时查询再修改？

day71-crm系统整合中没有放入此包：commons-beanutils-1.8.3.jar

查询OGNL回显的解释
新增客户，如果不选择下拉框，会触发外键约束，报错
查询pageBean定义方式
```

## 2. 项目一

### 2.1. 项目一:各系统网址

- 后台系统：localhost:8080/ilcps_web
	- 角色权限树需要开启redis
- 电子报运功能（模拟平台,使用）：localhost:8000/jk_export
	- 电子报运功能需要开启海关项目，（修改了eclipse中的tomcat端口）
- 消息队列系统：localhost:8081/ilcps_jms
	- 需要开启activeMQ服务器
- redis
	- IP（虚拟机）:192.168.187.10
	- 端口号：6379
- oracle
  - IP（虚拟机）:192.168.187.10
  - 端口号：1521
- 前端系统：localhost:9000/

### 2.2. 项目一需要补充的问题：

1. 补充新增用户页面直属领导。。
2. 修复新增角色，需要判断数据库是否存在此角色
3. 优化：生成报运单中，修改合同的已报运状态

```
// ***!!!!从货物对象中获取合同（货物对象是从数据库出现，是持久态）
// 将购销合同状态设置为2：已报运（但存在问题，会出现多次修改）
cp.getContract().setState(2);
```

4. 补充报运单的删除功能
5. 补充货物附件增删改功能
6. 补充购销合同条件查询功能
7. 优化交期发送提醒邮件的功能
8. 增加shiro功能需求
    1. 整合EhCache。如果在校验权限的时候不走Realm的授权方法。说明整合成功了。校验权限只在登录的时候走一次授权的方法。之后都是从缓存里面读取了。如果缓存修改了，就重新走一次授权的方法。
    2. 同步更新用户权限。在一个浏览器用户A(超级管理员)里面修改另外一个浏览器用户B(普通用户)的权限，在用户B不重新登录的情况下，失去操作的权限。
9. 整理项目使用过的相关工具类


### 2.3. 项目一实战登陆网址

- 【消息系统】http://localhost:9000/
- 【前端系统】http://localhost:8090/
- 【后台系统】http://localhost:8080/ilcps_web

【SVN】

- Repository Type: FSFS
- Repository Name: ilcps
- Repository URL:
    - https://192.168.83.22:8888/svn/ilcps/trunk
    - https://MoonKirA:8888/svn/ilcps/trunk

### 2.4. 后面需要整合的代码

1. 4个核心模块，（装箱单模块）
2. 登录日志的添加，登陆IP次数的统计图（前10名）
	- 04_onlineIpCount.html 页面
	- StatChartAction.java增加需求4方法、接口、实现类
	- 增加LoginLog对象对应数据库的表

### 2.5. 其他

"1"经md5加密后是：c4ca4238a0b923820dcc509a6f75849b

## 3. 待处理

### 3.1. 待试用的软件或者插件

- AIXcoder智能编程助手：https://www.aixcoder.com/#/

# 需要学习加强的内容

## 1. 相关知识点

- 数据库表设计
- 数据库优化
- solr全文检索
- vue+springboot项目 有参考价值：https://github.com/lenve/vhr
- jenkins
    - Jenkins是一个独立的开源自动化服务器，可用于自动化各种任务，如构建，测试和部署软件。Jenkins可以通过本机系统包Docker安装，甚至可以通过安装Java Runtime Environment的任何机器独立运行。

## 2. 需要整理的学习的java库

1. JUnit
2. SLF4J
3. Log4j
4. Google Guava：Google Guava软件包中的库或多或少是对核心库的对应部分有增强功能，并使编程更加高效和有效。Guava 包括内存缓存、不可变集合、函数类型、图形库和可用于 I/O、散列、并发、原语、字符串处理、反射等等的API实用程序。
5. XStream：当涉及将对象序列化到XML中时，这时常用XStream库, 开发人员通过XStream库可以轻松地将对象序列化为XML并返回。XStream的功能也很多，比如，大多数对象可以被序列化，并提供特定的映射，提供高性能和低内存占用，信息不重复，可自定义的转换策略，安全的框架，异常情况下的详细诊断等等。
6. iText：基本Java中创建和操作PDF件的各种操作都能完成
7. Apache PDF box：Apache PDFBox是另一个可用于操作PDF文件的开源库。PDFBox的主要功能使其成为超级库，其中包括PDF创建、将单个PDF分割为多个PDF文件、合并并提取PDF文本的Unicode文本，填写PDF表单，根据PDF/A标准验证PDF文件，将PDF保存为图像并对PDF进行数字签名。
8. jsoup：用于处理和解析HTML。Jsoup提供了一个有用的用于提取数据的API。jsoup中实现的标准是WHATWG HTML5。和最新的浏览器作法一样，jsoup将HTML解析为DOM。它允许解析来自任何URL或文件的HTML，清理和操纵HTML元素和属性，以检索用户提交的数据并过滤掉XSS攻击属性，使用jsoup还可以完成更多功能。
9. Gson
10. Joda Time：简单但功能强大的库，它节省了大量的开发时间。Joda-Time是一个Java库，作为Java中日期和时间类的一个很好的替代品。Joda Time提供计算日期和时间的功能，并支持几乎所有需要的日期格式，而且肯定难以用简单的JDK方法进行复制
11. Ok HTTP：用于通过HTTP协议有效地在现代应用程序之间交换数据。Okhttp在断网时恢复连接，在多个基于IP的服务中切换IP地址。okhttp的一个有用的功能是与现代TLS(SNI，ALPN)的自动连接，并且在发生故障时回到TLS 1.0。
12. Quartz

