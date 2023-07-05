# 品优购项目资料与整体架构配置笔记

## 1. 项目文档

<ul class="docs">
  <li><a href="#/项目资料/品优购/Day01-分布式服务框架-Dubbo">Day01-分布式服务框架-Dubbo</a></li>
  <li><a href="#/项目资料/品优购/Day02-前端框架AngularJS、通用Mapper">Day02-前端框架AngularJS、通用Mapper</a></li>
  <li><a href="#/项目资料/品优购/Day03-Nginx&品牌管理模块功能">Day03-Nginx&品牌管理模块功能</a></li>
  <li><a href="#/项目资料/品优购/Day04-Angular前端分层&规格及模板管理">Day04-Angular前端分层&规格及模板管理</a></li>
  <li><a href="#/项目资料/品优购/Day05-SpringSecurity安全框架与商家入驻审核功能">Day05-SpringSecurity安全框架与商家入驻审核功能</a></li>
  <li><a href="#/项目资料/品优购/Day06-商品录入模块（一）（富文本&FastDFS）">Day06-商品录入模块（一）（富文本&FastDFS）</a></li>
  <li><a href="#/项目资料/品优购/Day07-商品录入模块（二）（选择商品分类&选择品牌&选择规格&SKU）">Day07-商品录入模块（二）（选择商品分类&选择品牌&选择规格&SKU）</a></li>
  <li><a href="#/项目资料/品优购/Day08-商品修改与审核&注解式事务配置&Nginx静态资源服务器">Day08-商品修改与审核&注解式事务配置&Nginx静态资源服务器</a></li>
  <li><a href="#/项目资料/品优购/Day09-广告管理&SpringDataRedis缓存方案">Day09-广告管理&SpringDataRedis缓存方案</a></li>
  <li><a href="#/项目资料/品优购/Day10-搜索解决方案（一）-SpringDataSolr">Day10-搜索解决方案（一）-SpringDataSolr</a></li>
  <li><a href="#/项目资料/品优购/Day11-搜索解决方案（二）-高亮&显示分类规格&搜索过滤">Day11-搜索解决方案（二）-高亮&显示分类规格&搜索过滤</a></li>
  <li><a href="#/项目资料/品优购/Day12-搜索解决方案（三）-分页&价格区间筛选&排序&更新索引库">Day12-搜索解决方案（三）-分页&价格区间筛选&排序&更新索引库</a></li>
  <li><a href="#/项目资料/品优购/Day13-Freemarker-网页静态化解决方案">Day13-Freemarker-网页静态化解决方案</a></li>
  <li><a href="#/项目资料/品优购/Day14-JMS-消息中间件解决方案&FreeMarker静态化">Day14-JMS-消息中间件解决方案&FreeMarker静态化</a></li>
  <li><a href="#/项目资料/品优购/Day15-SpringBoot框架&短信微服务&用户中心注册功能">Day15-SpringBoot框架&短信微服务&用户中心注册功能</a></li>
  <li><a href="#/项目资料/品优购/Day16-CAS-单点登录解决方案&CAS集成SpringSecurity">Day16-CAS-单点登录解决方案&CAS集成SpringSecurity</a></li>
  <li><a href="#/项目资料/品优购/Day17-购物车解决方案&子系统集成CAS">Day17-购物车解决方案&子系统集成CAS</a></li>
  <li><a href="#/项目资料/品优购/Day18-CORS跨域请求解决方案&结算页&保存订单">Day18-CORS跨域请求解决方案&结算页&保存订单</a></li>
  <li><a href="#/项目资料/品优购/Day19-生成二维码&微信扫码支付&支付日志">Day19-生成二维码&微信扫码支付&支付日志</a></li>
  <li><a href="#/项目资料/品优购/Day20-秒杀解决方案">Day20-秒杀解决方案</a></li>
  <li><a href="#/项目资料/品优购/Day21-SpringTask任务调度&Nginx负载均衡&MyCat分库分表">Day21-SpringTask任务调度&Nginx负载均衡&MyCat分库分表</a></li>
  <li><a href="#/项目资料/品优购/附录1-项目涉及技术框架API">附录1-项目涉及技术框架API</a></li>
  <li><a href="#/项目资料/品优购/附录2-项目扩展知识">附录2-项目扩展知识</a></li>
  <li><a href="#/项目资料/品优购/项目2整体架构配置笔记">项目整体架构配置笔记</a></li>
</ul>

## 2. 相关配置模块

### 2.1. linux系统部署服务

- 本机IP：192.168.12.1
- 虚拟机对应的网卡（VMnet 8） - centOS练习安装
  - 用户名：root  密码：123456
  - 虚拟机IP：192.168.12.132
- 虚拟机对应的网卡（VMnet 8） - pinyougou项目使用
  - 用户名：root  密码：123456
  - 虚拟机IP：192.168.12.131
- linux系统（192.168.12.131）的MySQL
  - 账号：root
  - 密码：Root_123
- linux系统上开启动zookeeper注册中心
  - 命令：/usr/local/dubbo-zookeeper/bin/zkServer.sh start
  - 查看是否开启：/usr/local/dubbo-zookeeper/bin/zkServer.sh status
- linux系统上的Dubbo监控中心
  - linux系统开启服务命令：/usr/local/web/tomcat-dubbo-monitor/bin/startup.sh
  - url：http://192.168.12.131:8080
  - 用户名：root 密码：root
- linux系统上的solr中心
  - linux系统开启solr（单机版）：/usr/local/solr/tomcat-solr/bin/startup.sh
  - /usr/local/solr/tomcat-solr/bin/shutdown.sh
  - url：http://192.168.12.131:8088/solr/
  - linux系统开启solr（集群版）
  - url：http://192.168.12.131:8081/solr/
- ActiveMQ消息中间件
	- url：http://192.168.12.131:8161/admin/
	- 账号：admin
	- 密码：admin

### 2.2. 项目各模块配置url与域名

- 本机pinyougou-sellergoods  商家服务聚合模块
  - url：http://127.0.0.1:9001
  - dubbo暴露服务端口：20880
- 本机pinyougou-content 内容服务聚合模块
  - url：http://127.0.0.1:9002
  - dubbo暴露服务端口：20881
- 本机pinyougou-search  搜索服务
  - url：http://127.0.0.1:9003
  - dubbo暴露服务端口：20882
- 本机pinyougou-sms 短信微服务
    - url：http://127.0.0.1:9004
- 本机pinyougou-user 用户服务
    - url：http://127.0.0.1:9005
    - dubbo暴露服务端口：20883
- 本机pinyougou-cart 购物车服务
    - url：http://127.0.0.1:9006
    - dubbo暴露服务端口：20884
- 本机pinyougou-order 订单服务
    - url：http://127.0.0.1:9007
    - dubbo暴露服务端口：20885
- 本机pinyougou-pay 支付服务
    - url：http://127.0.0.1:9008
    - dubbo暴露服务端口：20886
- 本机pinyougou-seckill 秒杀服务
    - url：http://127.0.0.1:9009
    - dubbo暴露服务端口：20887
- 本机pinyougou-task 任务调度服务
    - url:http://127.0.0.1:9010

---

- 本机pinyougou-manager-web  运营商后台管理
  - url：http://127.0.0.1:9101
  - 配置域名：manager.moon.com
- 本机pinyougou-shop-web  商家后台管理
  - url：http://127.0.0.1:9102
  - 配置域名：shop.moon.com
- 本机pinyougou-portal-web  门户网站
  - url：http://127.0.0.1:9103
  - 配置域名：www.moon.com
- 本机pinyougou-search-web  搜索系统
  - url：http://127.0.0.1:9104
  - 配置域名：search.moon.com
- 本机pinyougou-item-web 商品详情系统
    - url：http://127.0.0.1:9105
    - 配置域名：item.moon.com
- 本机pinyougou-user-web 用户中心系统
    - url：http://127.0.0.1:9106
    - 配置域名：user.moon.com
- 本机CAS 中央认证系统（tomcat部署）
    - url：http://127.0.0.1:9107
    - 配置域名：sso.moon.com
    - 初始用户名：admin 密码：123456
- 本机pinyougou-cart-web 购物车系统
    - url：http://127.0.0.1:9108
    - 配置域名：cart.moon.com
- 本机pinyougou-seckill-web 秒杀系统
    - url：http://127.0.0.1:9109
    - 配置域名：seckill.moon.com
- Nginx相关命令(window系统)

```shell
# 用cmd进入nginx所在的根目录：
start nginx # 启动
nginx -s stop # 停止
nginx -s reload # 重新启动
```

## 3. 项目总结、技术列表

### 3.1. 项目总结

#### 3.1.1. 电商行业

- 模式：
    - B2C: 商家对个人
    - B2B: 商家对商家
    - C2C: 个人对个人
    - P2P: 个人对个人的信贷
    - O2O: 线上与线下
    - F2C: 工厂对个人
    - B2B2C: 商家对商家对个人
- 技术特点：
    - 技术新、技术范围广、分布式、高并发、高可用、集群、负载均衡、海量数据。
- 相关概念：
    1. 高可用(High Availability)：通常来描述一个系统经过专门的设计，从而减少停工时间，而保持其服务的高度可用性。
    2. 分布式：一个业务拆分成多个子业务，部署在不同的服务器上。
    3. 高并发：并发数量高，访问量大。
    4. 集群：指的是将多台服务器集中在一起，完成同一业务。
    5. 负载均衡：集群中所有的节点都处于活动状态，它们分摊系统的工作负载。

#### 3.1.2. DUBBO分布式服务框架

- 连接方式: 注册中心(zookeeper)
- 监控中心: 查看当前的服务(暴露的服务、引用的服务)
    - 说明：dubbo底层采用 RPC + SOA 实现。
    - RPC: Remote Procedure Call Protocol远程过程调用协议
    - SOA: Service Oriented Architecture面向服务架构
- dubbo的RPC传输数据序列化方式：Hessian二进制序列化方式.
- Hessian是一个轻量级的remoting onhttp工具，
- 使用简单的方法提供了RMI的功能。
- 相比WebService，Hessian更简单、快捷。
- 采用的是二进制RPC协议，因为采用的是二进制协议，
- 所以它很适合于发送二进制数据。

#### 3.1.3. MyBatis相关

1. 通用Mapper(数据访问接口需继承`Mapper<T>`)，简化单表的CRUD操作
2. 分页插件PageHelper

#### 3.1.4. Nginx反向代理服务器

- 静态资源(服务器)
- 负载均衡(集群)
    - 调度算法: 轮询、权重(weight)、ip哈希值(ip_hash)

#### 3.1.5. FastDFS分布式文件服务器

1. tracker server 追踪服务器
2. storage server 存储服务器
- 操作相关：
   - `storageClient.upload_file()` 上传文件
   - `storageClient.download_file()` 下载文件
   - `storageClient.delete_file()` 删除文件

#### 3.1.6. Redis非关系型数据库(mongoDB)

1. redis是一个开源的高性能键值对数据库.
2. 值的数据类型
    - 字符串string
    - 散列类型hash (字段与值)
    - 列表类型list (有序元素可以重复)
    - 集合类型set (无序元素不能重复)
    - 有序集合zset (有序元素不能重复)
3. 订阅与发布
4. redis存储方案(rdb, aof)
5. redis主从配置
6. 使用RedisTemplate操作redis(默认端口6379)
7. Linux搭建Redis集群(6台Redis)
    - 3台主服务器 + 3台从服务器
        - 3台主服务器分片存储数据，达到高并发
        - 3台从服务器做备份，达到高可用
    - 最多可使用16384台Redis做集群(16384个节点)

#### 3.1.7. Spring-Data-Redis整合Jedis

1. 单机版配置
2. 集群版配置

#### 3.1.8. 单点登录(CAS)

1. 实现登录功能
    1. 生成TGC存入Cookie
    2. 将用户登录信息TGT存储到Session
    3. 返回ST服务票据给客户端
    4. 客户端通过ST获取登录名，把登录名存储到客户端Session
2. 实现退出功能
    1. 配置单点退出过滤器
    2. 删除用户浏览器TGC登录凭证的Cookie
    3. 删除CAS服务端Session中TGT登录票据
    4. 删除所有客户端Session中的登录用户名

#### 3.1.9. js跨域(ip或者端口不同)

解决方案CORS(jsonp)

#### 3.1.10. Solr(全文检索服务器)

1. SolrHome: Solr存储所有引索库的目录(主目录).
2. SolrCore: Solr存储一个索引库的目录.
    - data: 索引库文件夹
    - config(schema.xml 字段与字段类型配置文件、solrconfig.xml 当前SolrCore全局配置文件)
3. 通过solrj操作solr.
4. solr集群solrcloud
    - 3台zookeeper + 4台solr
    - 3台zookeeper通过选举选出1个leader(领袖)与2个follower(追随)，达到高可用. (至少3台zookeeper)
    - 4台Solr: 分成两片达到高并发，其中每一片都有主(master)、从(slave)达到高可用.

#### 3.1.11. ActiveMQ消息队列(服务器与服务器之间通信)

把一些耗时业务进行异步处理，提高系统的吞吐量。

1. JMS(Java Message Service)java消息服务
2. 消息模式(点对点queue、订阅与发布topic)
3. 消息生产者MessageProducer(生产消息，发送到ActiveMQ消息服务器)
4. 消息消费者MessageConsumer(消费消息，接收ActiveMQ消息服务器上的消息)

- 注意：
    - 消息队列采用先进先出，从而保证先进的消息先消费。
- 说明：实现商品数据同步到【搜索系统】与【商品详情系统】
    - 运营商后台管理系统发送mq消息，搜索系统与商品详情系统接收mq消息，并进行消息的异步处理
    - 搜索系统实现商品的索引同步.
    - 商品详情系统实现商品的静态化页面同步.

#### 3.1.12. FreeMarker模版引擎

1. freemarker可充当视图呈现技术使用
2. 基本语法、表达式与运算符、内置指令、内置函数、处理空值
    - 说明：动态页面静态化，提高服务器的响应性能.

#### 3.1.13. 购物车系统

1. 登录状态实现购物车(将数据存储到redis中)
2. 未登录实现购物车(将数据存储到Cookie中)
3. 购物车数据合并

### 3.2. 部署相关

1. 一个子系统(一台tomcat)，并发数为：200-300/s(每秒)
    - 说明：如果要处理并数为2000/s (需用10台tomcat集群)
2. 服务器配置(至少三台)：
    - 第一台服务器：数据库 + Redis: 8核 + 2T + 32G
    - 第二台服务器：服务层(tomcat) + FastDFS + Solr: 8核 + 2T + 32G
    - 第三台服务器：表现层(tomcat) + Nginx: 8核 + 1T + 32G

### 3.3. 技术列表

1. Spring(4.3.10): JavaEE核心框架
2. Spring MVC(4.3.10): MVC框架
3. MyBatis(3.4.1): 数据持久层框架
4. Spring Security(4.2.3)：安全框架
5. Spring Boot(1.5.6): 微框架
6. dubbo(2.5.7): 分布式服务框架
7. zookeeper(3.4.11): 分布式协调服务
8. Redis(4.0.8): 非关系型数据库(缓存服务器)
9. Jedis(2.9.0): 操作Redis的Java客户端框架
10. Spring Data Redis(1.8.6): 操作Redis框架
11. CAS(4.2.7): 单点登录服务
12. Nginx(1.11.13): 反向代理服务器
13. Lucene(4.10.3): 全文检索框架
14. Solr(4.10.3): 全文检索服务
15. SolrJ(4.10.3): 操作Solr的Java客户端框架
16. Spring Data Solr(1.5.6): 操作Solr框架
17. FastDFS(1.25): 分布文件服务器
18. HttpClient(4.5.3): 可以在Java代码中发送http请求或获取响应的框架
19. ActiveMQ(5.14.5)【RabbitMQ. Kafka】: 消息队列，服务器之间通信
20. FreeMarker(2.3.26): 模版引擎，视图呈现技术，可以作为静态化技术
21. Mycat(1.6): 数据库中间件(分库分表)
22. kindeditor(4.1.10): 富文本编辑器
23. angular(1.6.9)：MVC前端框架
24. jQuery(1.11.3): Ajax框架

## 4. 项目在linux系统中软件启动命令

```shell
1. dubbo注册中心zookeeper:

   /usr/local/dubbo-zookeeper/bin/zkServer.sh start   [启动]
   /usr/local/dubbo-zookeeper/bin/zkServer.sh restart [重启]
   /usr/local/dubbo-zookeeper/bin/zkServer.sh stop    [停止]
   /usr/local/dubbo-zookeeper/bin/zkServer.sh status  [状态]

   查看zookeeper进程：ps -ef | grep zookeeper
   强制杀死进程：kill -9 进程号


2. dubbo监控中心：

   /usr/local/web/tomcat-dubbo-monitor/bin/startup.sh
   /usr/local/web/tomcat-dubbo-monitor/bin/shutdown.sh

   查看tomcat进程：ps -ef | grep tomcat


3. fastdfs分布式文件系统：

   a. 启动tracker追踪服务器
      /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf restart

   b. 启动storaged存储服务器
      /usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart

   c. 启动nginx访问storaged中存储的文件
      /usr/local/nginx/sbin/nginx

   d. 文件存储路径
	  /home/fastdfs/fdfs_storage/data

      查看fastdfs进程号: ps -ef | grep fdfs
      查看nginx进程号：ps -ef | grep nginx
      强制杀死进程：kill -9 进程号

4. redis数据库：

   a. redis单机版启动：
      cd /usr/local/redis/bin && ./redis-server redis.conf
      单机版redis的ip与端口
      	192.168.12.131:6379

   b. redis集群版启动(6台redis实现分布式缓存)：
      cd /usr/local/redis/redis-cluster && ./start-all.sh
      查看redis进程号： ps -ef | grep redis
      集群版redis的ip与端口
          192.168.12.131:7001
          192.168.12.131:7002
          192.168.12.131:7003
          192.168.12.131:7004
          192.168.12.131:7005
          192.168.12.131:7006

5. solr全文检索服务器：
   a. solr单机版启动：
      /usr/local/solr/tomcat-solr/bin/startup.sh
      /usr/local/solr/tomcat-solr/bin/shutdown.sh
   b. solr集群版启动(分布式检索):
      3台zookeeper集群:
      /usr/local/solr/solr-cloud/zookeeper01/bin/zkServer.sh restart
      /usr/local/solr/solr-cloud/zookeeper02/bin/zkServer.sh restart
      /usr/local/solr/solr-cloud/zookeeper03/bin/zkServer.sh restart

      /usr/local/solr/solr-cloud/zookeeper01/bin/zkServer.sh status
      /usr/local/solr/solr-cloud/zookeeper02/bin/zkServer.sh status
      /usr/local/solr/solr-cloud/zookeeper03/bin/zkServer.sh status

      4台tomcat集群:
      /usr/local/solr/solr-cloud/tomcat-solr01/bin/startup.sh
      /usr/local/solr/solr-cloud/tomcat-solr02/bin/startup.sh
      /usr/local/solr/solr-cloud/tomcat-solr03/bin/startup.sh
      /usr/local/solr/solr-cloud/tomcat-solr04/bin/startup.sh

      /usr/local/solr/solr-cloud/tomcat-solr01/bin/shutdown.sh
      /usr/local/solr/solr-cloud/tomcat-solr02/bin/shutdown.sh
      /usr/local/solr/solr-cloud/tomcat-solr03/bin/shutdown.sh
      /usr/local/solr/solr-cloud/tomcat-solr04/bin/shutdown.sh

      查看tomcat进程：ps -ef | grep tomcat

6. ActiveMQ消息中间件：

   /usr/local/activemq/bin/linux-x86-64/activemq start
   /usr/local/activemq/bin/linux-x86-64/activemq restart
   /usr/local/activemq/bin/linux-x86-64/activemq stop
   /usr/local/activemq/bin/linux-x86-64/activemq status

   查看activemq进程号： ps -ef | grep activemq

# 总的启动命令：
/usr/local/sbin/pyg_start.sh

# 把全部启动命令写入pyg_start.sh文件中：
vi /usr/local/sbin/pyg_start.sh

/usr/local/solr/solr-cloud/zookeeper01/bin/zkServer.sh restart
/usr/local/solr/solr-cloud/zookeeper02/bin/zkServer.sh restart
/usr/local/solr/solr-cloud/zookeeper03/bin/zkServer.sh restart
/usr/local/dubbo-zookeeper/bin/zkServer.sh restart
/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf restart
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart
/usr/local/nginx/sbin/nginx
cd /usr/local/redis/bin && ./redis-server redis.conf
cd /usr/local/redis/redis-cluster && ./start-all.sh
/usr/local/solr/tomcat-solr/bin/startup.sh
/usr/local/activemq/bin/linux-x86-64/activemq restart
/usr/local/solr/solr-cloud/tomcat-solr01/bin/startup.sh
/usr/local/solr/solr-cloud/tomcat-solr02/bin/startup.sh
/usr/local/solr/solr-cloud/tomcat-solr03/bin/startup.sh
/usr/local/solr/solr-cloud/tomcat-solr04/bin/startup.sh

# 添加可执行权限：
chmod u+x /usr/local/sbin/pyg_start.sh

# 动态查看tomcat启动日志
tail -f /usr/local/solr/tomcat/logs/catalina.out

# 停止防火墙
systemctl stop firewalld.service

# 启动防火墙
systemctl start firewalld.service

# 禁止防火墙开机启动
systemctl disable firewalld.service

# 启用防火墙开机启动
systemctl enable firewalld.service

# 查看Linux版本号
cat /etc/redhat-release
```

## 5. 配置hosts文件

```
# 项目二配置域名
127.0.0.1  manager.moon.com
127.0.0.1  www.moon.com
127.0.0.1  moon.com
127.0.0.1  shop.moon.com
127.0.0.1  search.moon.com
127.0.0.1  image.pinyougou.com
127.0.0.1  item.moon.com
127.0.0.1  user.moon.com
127.0.0.1  sso.moon.com
127.0.0.1  cart.moon.com
127.0.0.1  seckill.moon.com
```

## 6. 补充

- day06 运营商后台-商品分类功能（新增、修改、删除）
- day07 优化最后商品录入的逻辑，修改为不要循环插入数据
- day08
  1. 修改商品时回显的bug，因为新增时监听分类下拉框的值变化，变化就清空相应的扩展属性与规格选项的值，导致修改时数据商品的三级分类不能回显。如果不监听清空数据，双会出现分类下拉框数据变化后，扩展属性与规格选项的显示与值有问题。
  2. 待优化：运营商在商品审核模块中，删除商品时，只将商品SPU表（tb_goods）的is_delete字段修改为1，但没有修改对应的SKU表（tb_item）的数据，没有将对应good_ids下的所有状态修改为1
  3. 商家后台的商品删除功能未完成
  4. 待优化运营商、商家的修改商品的审核、删除、上下架的状态的sql，尝试能不能全部共用一条修改的sql
- day09
    - 在广告管理中也涉及到文件上传的功能，目前的做法是将商家后台的文件上传uploadController复制到运营商后台，这个如果需要修改的地方则要重复写两次，想是否能将文件上传部分的做到一个服务（或者微服务，可以实践使用spring cloud）
    - 在广告内容修改有一个bug，不勾选“是否有效”时，还是绑定到对象是勾选的值。
- day12
    - 关键字搜索“销量排序”与“评价排序”未完成
    - 分布式事务处理的问题：在最后一节更新索引索的内容中，在服务运营商的工程做商品审核中，分别调用了商家服务审核与搜索服务更新索引，如果更新索引时发生了异常，之前的商家服务审核商品的事务不会回滚，因为框架配置的事务管理中在同一个服务、同一个service中才会回滚。所以后面需要研究分布式事务如何处理【后面引入消息中件间，将更新索引的与运营商后台解耦了，但还是存在商品审核通过后，但索引库更新失败的问题】
- day15
    - 注册时表单都没有做详细的校验，还有需要完成获取短信没有做倒数90秒无法操作的设置
- day18
    - 订单的收件人地址增加、修改与删除功能待实现
- day19
  - 支付日志显示，在运营商后台显示的功能未实现
- day20
    - 秒杀商品后台管理功能
        - 商家提交秒杀商品申请，录入秒杀商品数据，主要包括：商品标题、原价、秒杀价、商品图片、介绍等信息。
        - 运营商审核秒杀申请，审核后在秒杀页面显示

## 7. Git库-项目仓库相关分支说明

### 7.1. Github仓库

- shh://git@github.com:MooNkirA/pinyougou.git
    - master分支：空内容
    - ITheima-develop分支：原版本
    - moon-develop分支：第二次重做项目的版本（已按教程完成）
    - moon-cloud-develop分支：将原来的项目，修改成SpringBoot与SpringCloud架构（由moon-develop分支创建）
    - pyg-test-develop分支：品优购相关技术测试项目
