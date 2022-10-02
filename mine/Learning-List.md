# 🔖 学习计划与遗留内容

## 1. 2020.02.18-Drools业务规则管理系统（BRMS）

待解决问题：

1. 若使用最新的 7.73.0.Final 版的 Drools，按教程的使用默认的配置方式无法进行规则校验？

## 2. 2021.10.07-MySQL数据库从入门到精通

- [x] 已完成基础篇
- [x] 已完成进阶篇
    - 待整理事务实现原理相关笔记
    - 待整理MVCC实现原理相关笔记
- [ ] 待开始 运维篇-01

## 3. 图灵第5期课程-01-源码框架专题

- [x] 01-Spring底层核心原理解析
- [x] 02-手写模拟Spring底层原理
- [x] 03-Spring之底层架构核心概念解析
- [ ] 04-Spring之Bean生命周期源码解析上 2021-09-08 周三  20:50
- [ ] 05-Spring之Bean生命周期源码解析下 2021-09-10 周五
- [ ] 06-Spring之依赖注入源码解析（上）2021-09-12 周日 
- [ ] 07-Spring之依赖注入源码解析（下）2021-09-15 周三
- [ ] 08-Spring之循环依赖底层源码解析 2021-09-17 周五
- [ ] 09-Spring之推断构造方法源码解析 2021-09-22 周三

## 4. 2022.02.17-Spring高级四十九讲

> Sping 官方文档：https://docs.spring.io/spring-framework/docs/current/reference/html/core.html

- [ ] 013 待补充 DestructionAwareBeanPostProcessor 与 InstantiationAwareBeanPostProcessor 两个接口的使用示例
- [ ] 034 待补充与测试 基于 agent 类加载实现的AOP案例
- [ ] 050~051 研究 findEligibleAdvisors 与 wrapIfNecessary 解析 Advisor
- [ ] 054~062 研究通知的调用流程，待整理 Spring MVC 运行流程
- [ ] 123~138 回头再加深了解 Spring Boot 执行流程内容
- [ ] 159~166 回头再加深了解 Spring 依赖注入底层实现内容  待补充 autowired 示例

## 5. 2019.09.04-万信金融企业级开发实战

- [ ] 项目实战部分-用户身份识别功能待完成，课程位置：day17-20 身份验证
- [ ] 排查交易中心服务中，用于控制幂等性存入redis的方法报错问题，《07-用户发标》章节
- [ ] 实战完成后，使用现在数据覆盖项目初始化的sql脚本
> ！！！推送代码前记得将wanxinp2p-file-service中的application.yml文件删除七牛云配置；最终项目上传apollo配置数据库删除百度智能云配置（或者不上传到代码仓库，自己私人备份即可）

## 6. 学习mybatis待整理内容

- [ ] day01 待补充 动态sql的示例
- [ ] day07 梳理与实现基础分页插件的示例与原理
- [ ] day09 分页插件与MyBatis-plus插件示例与原理
- [ ] 课外：学习与整理 Dynamic SQL 示例

## 7. 2019.12.12-并发编程 concurrent programming

- [x] Day01
- [x] Day02
- [x] Day03
- [ ] 进行中 Day04-14
- [ ] Day05
- [ ] Day06
- [ ] Day07

> 学习中断间隔很长，估计重新学习时需要回顾前面的内容

## 8. 享学3期-Zookeeper&Dubbo

ZooKeeper

- [ ] Day03(已完成第1节)、Day04 zookeeper应用场景示例

dubbo 待解决问题

- [ ] 1.待测试，多版本下，多次请求会请求到别的版本的服务接口
- [ ] 2.异步执行与异步调用的示例存在问题，待日后再整理

## 9. 享学3期-Redis

Day01

- [ ] 1.整理Redis命令（String类型/Hash类型/List类型）
- [ ] 2.记录课程上说的，使用hgetall命令获取某个hash类型中的所有field，因为redis读取与写入是单线程操作，如果该hash类型的field比较多的时候，会导致线程阻塞

Day02

- [ ] 1.整理Redis命令（Set类型/ZSet类型）
- [ ] 2.整理Redis示例，实现一个简易版消息中件间
- [ ] 3.Redis 高级数据结构

## 10. 享学3期-MySQL待整理内容

- [ ] Day02 mysql系统库的作用总结
- [ ] Day03 索引 B+tree 笔记
- [ ] Day08、Day09、Day10	MySQL 的查询成本计算
- [ ] Day11
- [ ] MySQL查询重写规则(子查询在 MySQL 的执行)
- [ ] InnoDB 引擎底层解析
- [ ] Day12、Day13	InnoDB 引擎底层解析(InnoDB 记录存储结构和索引页结构)

## 11. 2021.08.30-SpringBoot2全套课程

- [ ] 待排查 Spring Boot 整合 ehcache 缓存技术示例工程启动报错问题

# HM-Java后端课程学习清单

## 1. Java 基础

|     |                 课程名                 | 在线链接 |      备注      |
| :-: | ------------------------------------- | -------- | ------------- |
| ✅  | 2019.10.25-JavaJDK新特性详解-JDK8&JDK9  |          | 只学完JDK8部分 |
|     | 2019.10.11-JDK10新特性                 |          |               |
|     | 2019.10.11-JDK11&JDK12新特性           |          |               |
|     | Java基础-06-泛型                        |          |               |
|     | 2020.01.15-JDK8日期API解析              |          |               |
|     | 2020JAVA基础-深入系统的学习数据结构与算法 |          |               |
|     | 详细分析LinkedList数据链表的实现原理      |          |               |
|     | 07-数据结构与算法-【优化精简最终版】      |          |               |
|     | 2019.06.07-解密JVM                     |          |               |


## 2. 框架专题

### 2.1. Spring

|     |                   课程名                   |                                                 在线链接                                                  |                         备注                          |
| :-: | ----------------------------------------- | -------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| ✅  | 2019.08.20-Spring高级之注解驱动开发详解     |                                                                                                          |                                                      |
| ✅  | 2019.07.14-数据层全栈方案SpringData高级应用 |                                                                                                          | 只学习了 Spring Data JPA 与 Spring Data Redis 这两部分 |
|     | 2021.04.11-SSM框架                        | [B站](https://www.bilibili.com/video/BV1Fi4y1S7ix)                                                       |                                                      |
| ✅  | 2022.02.17-Spring高级四十九讲              | [官网](http://yun.itheima.com/course/997.html) &#124; [B站](https://www.bilibili.com/video/BV1P44y1N7QG) |                                                      |

### 2.2. Mybatis

|     |                    课程名                    |                                                 在线链接                                                  |     备注     |
| :-: | ------------------------------------------- | -------------------------------------------------------------------------------------------------------- | ------------ |
| ✅  | 2019.05.08-全面学习Mybatis插件之Mybatis-Plus | [官网](http://yun.itheima.com/course/582.html) &#124; [B站](https://www.bilibili.com/video/BV1rE41197jR) | aliyun已备份 |

### 2.3. Spring Boot

|     |               课程名                |                                                 在线链接                                                  |            备注             |
| :-: | ---------------------------------- | -------------------------------------------------------------------------------------------------------- | --------------------------- |
|     | 2019.12.10-SpringBoot高级原理分析   |                                                                                                          |                             |
| ✅  | 2019.09.10-黑马程序员springboot全套 |                                                                                                          |                             |
| ✅  | 1-2 SpringBoot微服务快速开发        |                                                                                                          | 2019-博学谷-JavaEE在职加薪课 |
| ✅  | 2021.08.30-SpringBoot2全套         | [官网](http://yun.itheima.com/course/944.html) &#124; [B站](https://www.bilibili.com/video/BV15b4y1a7yG) |                             |

### 2.4. 相关工具框架

|     |                   课程名                   |                                                 在线链接                                                  | 备注 |
| :-: | ------------------------------------------ | -------------------------------------------------------------------------------------------------------- | ---- |
| ✅  | 2019.12.10-史上最全java日志攻略              | [官网](http://yun.itheima.com/course/632.html) &#124; [B站](https://www.bilibili.com/video/BV1iJ411H74S) |      |
|     | 2019.03.21-ElasticStack 从入门到实践        |                                                                                                          |      |
|     | 2019.08.25-ELK搜索高级课程                  |                                                                                                          |      |
| ✅  | 2019.11.11-最新工作流引擎Activiti7基础与进阶 | [官网](http://yun.itheima.com/course/814.html) &#124; [B站](https://www.bilibili.com/video/BV1H54y167gf) |      |
|     | 2019.11.13-lucene营销视频                   |                                                                                                          |      |
|     | 2019.06-26-利刃出鞘-Tomcat核心原理解析       |                                                                                                          |      |

## 3. 数据库专题

|     |                    课程名                     |                                                 在线链接                                                  |                          备注                          |
| :-: | --------------------------------------------- | -------------------------------------------------------------------------------------------------------- | ------------------------------------------------------ |
|     | 2019.04.21-剑指MySQL性能优化                   |                                                                                                          | 学习中                                                  |
|     | 2-2 数据库优化                                 |                                                                                                          | 2019-博学谷-JavaEE在职加薪课                             |
|     | 2019.10.11-Redis入门到精通-Java核心缓存技术教程 | [官网](http://yun.itheima.com/course/611.html) &#124; [B站](https://www.bilibili.com/video/BV1CJ411m7Gc) |                                                        |
|     | 2019.11.14-MongoDB用起来-快速上手和集群搭建     |                                                                                                          |                                                        |
|     | 2019.08.29-Sharding-JDBC分布分表专题           |                                                                                                          |                                                        |
| ✅  | 2021.08.24-大数据MySQL8.0从入门到精通实战教程   | [官网](http://yun.itheima.com/course/963.html) &#124; [B站](https://www.bilibili.com/video/BV1iF411z7Pu) |                                                        |
|  ❗  | 2021.10.07-MySQL数据库从入门到精通              | [官网](http://yun.itheima.com/course/991.html) &#124; [B站](https://www.bilibili.com/video/BV1Kr4y1i7ru) | 部分内容快速过一次，后面再深入复盘                         |
|     | 2021.12.14-Redis入门到精通                     | [官网](http://yun.itheima.com/course/994.html) &#124; [B站](https://www.bilibili.com/video/BV1cr4y1671t) | 全面透析redis底层原理+redis分布式锁+企业解决方案+redis实战 |

## 4. 微服务专题

### 4.1. 微服务框架

|     |                         课程名                          |                                  在线链接                                  |            备注             |
| :-: | ------------------------------------------------------ | ------------------------------------------------------------------------- | --------------------------- |
| ✅  | 2019.09.24-4天从浅入深精通SpringCloud微服务架构体系       |                                                                           |                             |
|     | 2019.11.30-分布式链路追踪skywalking                      |                                                                           |                             |
|     | 2019.08.15-微服务架构的分布式事务控制解决方案              |                                                                           |                             |
|     | 2020.05.07-系统掌握阿里分布式服务架构流量控制组件-Sentinel |                                                                           |                             |
| ✅  | 2019.12.18-超全面讲解Spring Cloud Alibaba技术栈          |                                                                           |                             |
|     | 2019.10.31-Spring Cloud Alibaba Nacos配置中心与服务发现  |                                                                           |                             |
|     | 2019.09.26-带你玩转Zookeeper+Dubbo                      |                                                                           |                             |
|     | 2019.10.05-Elastic-Job分布式任务调度-服务中间件           |                                                                           |                             |
|     | 2019.11.14-nginx项目部署                                |                                                                           |                             |
|     | 1-19 Apache ServiceComb微服务框架课程                    |                                                                           | 2019-博学谷-JavaEE在职加薪课 |
|     | 1-21 华为云PaaS微服务治理课程（CSE Mesher开发）           |                                                                           | 2019-博学谷-JavaEE在职加薪课 |
|     | 2021.04.07-史上最全面的java微服务架构课                   |                                                                           | 综合性课程                   |
|     | Spring Cloud Alibaba 微服务架构电商项目实战               | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2870&courseType=1) | 博学谷在线课程               |

### 4.2. 消息中间件

|     |                        课程名                         | 在线链接 | 备注 |
| :-: | ---------------------------------------------------- | -------- | ---- |
|     | 2019.10.28-RabbitMQ深入浅出                           |          |      |
|     | 2019.06.17-全面解剖RocketMQ和项目实战                  |          |      |
|     | 2019.10.24-ActiveMQ                                  |          |      |
|     | 2019.09.23-《分布发布订阅消息系统》--Kafka深入探秘者来了 |          |      |

### 4.3. 分布式配置中心

|     |                  课程名                  |                                                 在线链接                                                  | 备注 |
| :-: | ---------------------------------------- | -------------------------------------------------------------------------------------------------------- | ---- |
|     | 2020.01.07-3天全面深入学习zookeeper       |                                                                                                          |      |
| ✅  | 2019.10.06-Apollo分布式配置中心-服务中间件 | [官网](http://yun.itheima.com/course/599.html) &#124; [B站](https://www.bilibili.com/video/BV1eE41187sS) |      |

## 5. 权限控制专题

|     |                           课程名                           |                                                 在线链接                                                  | 备注 |
| :-: | ---------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- | ---- |
|     | 2019.07.10-由浅入深手把手教你精通新版SpringSecurity           |                                                                                                          |      |
| ✅  | 2019.09.30-Spring Security Oauth2.0认证授权专题-热门技术框架 | [官网](http://yun.itheima.com/course/923.html) &#124; [B站](https://www.bilibili.com/video/BV1VE411h7aL) |      |
|     | 2020.03.10-由浅入深掌握Shiro权限框架                         |                                                                                                          |      |

## 6. 并发编程

|     |         课程名          |                                  在线链接                                  |     备注      |
| :-: | ---------------------- | ------------------------------------------------------------------------- | ------------- |
|     | 2019.12.12-并发编程     |                                                                           |               |
|     | Java并发编程高阶技术实践 | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=1515&courseType=1) | 博学谷在线课程 |

## 7. 网络编程

|     |          课程名          | 在线链接 |                   备注                   |
| :-: | ------------------------ | -------- | --------------------------------------- |
|     | Netty网络编程            |          | 2019-博学谷-JavaEE在职加薪课（已学习部分） |
|     | NIO与Netty编程           |          | 2019-博学谷-JavaEE在职加薪课（已学习部分） |
|     | 2020.05.04-Netty网络编程 |          |                                         |

## 8. DevOps专题

|     |                  课程名                  | 在线链接 |            备注             |
| :-: | --------------------------------------- | -------- | --------------------------- |
|     | 2018.05.14-Git版本控制                   |          |                             |
| ✅  | 2020.03.02-黑马程序员Git全套教程          |          |                             |
|     | 2019.09.18-深入解析docker容器化技术       |          |                             |
| ✅  | Docker容器化                             |          | 2019-博学谷-JavaEE在职加薪课 |
|     | 2018.09.03-持续集成与容器管理             |          | 2019-博学谷-JavaEE在职加薪课 |
|     | 2019.12.02-Jenkins从环境配置到项目开发    |          |                             |
|     | 2020.02.08-Kubernetes(K8S)超快速入门教程 |          |                             |
|     | 1-20 容器化进阶K8S                       |          | 2019-博学谷-JavaEE在职加薪课 |
| ✅  | 2019.12.08-Maven从基础到高级应用          |          |                             |

## 9. 项目实战

|     |                   课程名                    |                                                  在线链接                                                  |                   备注                   |
| :-: | ------------------------------------------ | --------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| ✅  | 2018.07.19-项目二：品优购V1.3.1              |                                                                                                           |                                         |
|     | 2018.09.12-学成在线项目实战                  |                                                                                                           | 2019-博学谷-JavaEE在职加薪课（已学习部分） |
|     | 2018.11.09-互联网全终端项目-好客租房项目      |                                                                                                           | 2019-博学谷-JavaEE在职加薪课（已学习部分） |
|     | 2018.11.22-传统行业解决方案SaaS-HRM项目      |                                                                                                           | 2019-博学谷-JavaEE在职加薪课              |
|     | 2019.06.04-传智健康项目                     | [官网](http://yun.itheima.com/course/903.html) &#124; [B站](https://www.bilibili.com/video/BV1Bo4y117zV)  |                                         |
| ✅  | 2019.09.04-万信金融企业级开发实战             | [官网](http://yun.itheima.com/course/902.html) &#124; [B站](https://www.bilibili.com/video/BV1Ub4y1f7rk)  |                                         |
|     | 2019.09.06-黑马头条项目实战课程              | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2131)                                              |                                         |
| ✅  | 2019.11.04-企业年会之红包雨场景实战           | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2146)                                              |                                         |
|     | 2019.11.28-闪聚支付项目                     | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2249)                                              |                                         |
|     | 2020.02.10-基于Java日志平台的访问链路追踪实战 | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2252)                                              | 部分视频有问题                            |
| ✅  | 2020.02.18-Drools业务规则管理系统（BRMS）    | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=2250)                                              |                                         |
|     | 2020.03.09-黑马电商3.0                      |                                                                                                           |                                         |
| ✅  | 2020.03.30-品达通用权限系统                  | [官网](http://yun.itheima.com/course/905.html) &#124; [B站](https://www.bilibili.com/video/BV1tw411f79E)  |                                         |
|     | 2020.12.21-集信达短信平台实战                | [官网](http://yun.itheima.com/course/906.html) &#124; [B站](https://www.bilibili.com/video/BV1Jb4y1d7GY)  |                                         |
|     | 2021.06.17-瑞吉外卖平台实战                  | [官网](http://yun.itheima.com/course/999.html) &#124; [B站](https://www.bilibili.com/video/BV13a411q753)  |                                         |
|     | 2021.08.27-Java百度地图全套教程              | [官网](http://yun.itheima.com/course/1006.html) &#124; [B站](https://www.bilibili.com/video/BV1ue4y1R712) |                                         |

## 10. 综合专题

|     |              课程名               |                                  在线链接                                  |     备注      |
| :-: | -------------------------------- | ------------------------------------------------------------------------- | ------------- |
|     | Java加薪派实战课（老学员专属）      | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=5020&courseType=1) | 博学谷在线课程 |
|     | WEB开发中常见安全漏洞分析与预防策略 | [博学谷](https://xuexi.boxuegu.com/video.html?courseId=1516&courseType=1) | 博学谷在线课程 |

## 11. 已删除记录

> 此部分是本地硬盘的课程资料均已删除

|     |                            课程名                            |                                                 在线链接                                                  |            备注             |
| :-: | ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------- | --------------------------- |
|     | ~~maven项目管理工具~~                                         |                                                                                                          | 2019-博学谷-JavaEE在职加薪课 |
| ✅  | ~~2019.10.07-Git零基础入门到实战详解~~                         |                                                                                                          |                             |
|     | ~~2018.04.08-Spring Boot+Vue.js+FastDFS实现分布式图片服务器~~  |                                                                                                          |                             |
| ✅  | ~~Gradle精品公开课~~                                          | [官网](http://yun.itheima.com/course/438.html) &#124; [B站](https://www.bilibili.com/video/BV1iW411C7CV) |                             |

# HM-前端课程学习清单

## 1. 前端基础（HTML/CSS/JS/TypeScript）

|     |                    课程名                     |                                                 在线链接                                                  |                  备注                  |
| :-: | -------------------------------------------- | -------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| ✅  | 2019.05.16-前端小白零基础入门HTML5+CSS3        | [官网](http://yun.itheima.com/course/602.html) &#124; [B站](https://www.bilibili.com/video/BV1RK4y1X7qd) |                                        |
|     | 2019.06.06-JavaScript核心DOM操作              |                                                                                                          | 与《Web APIs 实用案例》课程有部分内容相同 |
| ✅  | 2019.12.27-百度地图                           |                                                                                                          |                                        |
| ✅  | javaScript进阶面向对象ES6                     |                                                                                                          |                                        |
|     | 2019.12.06-零基础5天轻松入门TypeScript         |                                                                                                          |                                        |
|     | 2020-TypeScript                              |                                                                                                          |                                        |
|     | 2021.06.19-前端基础进阶-移动web                |                                                                                                          |                                        |
|     | 2021.08.26-Web APIs 实用案例                  |                                                                                                          |                                        |
|     | 2021.09.26-Web前端基础课程-HTML+CSS+小兔鲜项目 |                                                                                                          | 暂无在线视频                            |

## 2. 前端框架

### 2.1. JQuery

|     |       课程名       | 在线链接 | 备注 |
| :-: | ----------------- | -------- | ---- |
|     | 2020.01.18-JQuery |          |      |

### 2.2. Vue.js

|     |            课程名            |                                                 在线链接                                                  | 备注 |
| :-: | ---------------------------- | -------------------------------------------------------------------------------------------------------- | ---- |
| ✅  | 2021.07.06-Vue2+Vue3全套教程 | [官网](http://yun.itheima.com/course/933.html) &#124; [B站](https://www.bilibili.com/video/BV1zq4y1p7ga) |      |

### 2.3. electron

|     |                   课程名                    | 在线链接 | 备注 |
| :-: | ------------------------------------------- | -------- | ---- |
|     | 2020.11.05-玩转electron两小时轻松实现桌面应用 |          |      |

## 3. node.js & webpack

|     |           课程名                 | 在线链接 | 备注 |
| :-: | ------------------------------- | -------- | ---- |
|     | 2020.03.24-Node.js零基础入门教程 |          |      |

# 博学谷在线课程

- [ ] [MySQL数据库性能优化](https://xuexi.boxuegu.com/video.html?courseId=1251)
- [ ] [Java进阶大数据开发公开课](https://xuexi.boxuegu.com/video.html?courseId=1200)
- [ ] [Java秒杀系统实战(上)](https://xuexi.boxuegu.com/video.html?courseId=1292)
- [ ] [【Java】Java基础全精讲](https://xuexi.boxuegu.com/video.html?courseId=89)
- [ ] [百度地图API应用开发](https://xuexi.boxuegu.com/video.html?courseId=280)
- [ ] [HTML5 CSS3视频教程](https://xuexi.boxuegu.com/video.html?courseId=1044)
- [ ] [利用CSS3实现响应式项目开发](https://xuexi.boxuegu.com/video.html?courseId=1054)
- [ ] [狂野架构师（前置）](https://xuexi.boxuegu.com/video.html?courseId=3275)
- [ ] 2022年6月30号直播回放-[JDK源码分析](https://xuexi.boxuegu.com/video.html?courseId=5058)
- [ ] 2022年7月06号直播回放-[设计模式](https://xuexi.boxuegu.com/video.html?courseId=5087)
- [ ] 2022年7月07号直播回放-[leetCode必刷](https://xuexi.boxuegu.com/video.html?courseId=5085)
- [ ] 2022年7月14号直播回放-[高性能MySQL](https://xuexi.boxuegu.com/video.html?courseId=5093)
- [ ] 2022年7月18号直播回放-[SQL调优硬核面试真题](https://xuexi.boxuegu.com/video.html?courseId=5111)
- [ ] 2022年7月21号直播回放-[Redis解决高并发场景问题](https://xuexi.boxuegu.com/video.html?courseId=5114)
- [ ] 2022年7月27号直播回放-[Spring核心源码分析](https://xuexi.boxuegu.com/video.html?courseId=5136)
- [ ] 2022年8月10号直播回放-[常见数据结构与算法](https://xuexi.boxuegu.com/video.html?courseId=5211)
- [ ] 2022年8月19号直播回放-[大数据行业深度解读](https://xuexi.boxuegu.com/video.html?courseId=5223)
- [ ] 2022年8月25号直播回放-[大数据Apache](https://xuexi.boxuegu.com/video.html?courseId=5256)
- [ ] 2022年9月1号直播回放-[如何利用大数据割韭菜](https://xuexi.boxuegu.com/video.html?courseId=5273)
- [ ] 2022年9月8号直播回放-[大话网络通信，计算机之间是如何沟通的？](https://xuexi.boxuegu.com/video.html?courseId=5282)
- [ ] 2022年9月13号直播回放-[搞定MySQL索引底层原理详解，手撕面试官](https://xuexi.boxuegu.com/video.html?courseId=5284)

# 尚硅谷-Java学科课程学习清单

## 1. 项目实战

|     |               课程名                | 在线链接 | 备注 |
| :-: | ---------------------------------- | -------- | ---- |
|     | 2019.09.26-大型电商--谷粒商城        |          |      |
|     | 2021.12.01-Java在线支付实战-微信支付 |          |      |

# 零散学习内容

## 1. 相关知识点

- 数据库表设计
- 数据库优化
- solr全文检索
- Jenkins 是一个独立的开源自动化服务器，可用于自动化各种任务，如构建，测试和部署软件。Jenkins可以通过本机系统包Docker安装，甚至可以通过安装Java Runtime Environment的任何机器独立运行。

## 2. 需要整理的学习的java库

- Google Guava：Google Guava软件包中的库或多或少是对核心库的对应部分有增强功能，并使编程更加高效和有效。Guava 包括内存缓存、不可变集合、函数类型、图形库和可用于 I/O、散列、并发、原语、字符串处理、反射等等的API实用程序。
- XStream：当涉及将对象序列化到XML中时，这时常用XStream库, 开发人员通过XStream库可以轻松地将对象序列化为XML并返回。XStream的功能也很多，比如，大多数对象可以被序列化，并提供特定的映射，提供高性能和低内存占用，信息不重复，可自定义的转换策略，安全的框架，异常情况下的详细诊断等等。
- iText：基本Java中创建和操作PDF件的各种操作都能完成
- Apache PDF box：Apache PDFBox是另一个可用于操作PDF文件的开源库。PDFBox的主要功能使其成为超级库，其中包括PDF创建、将单个PDF分割为多个PDF文件、合并并提取PDF文本的Unicode文本，填写PDF表单，根据PDF/A标准验证PDF文件，将PDF保存为图像并对PDF进行数字签名。
- jsoup：用于处理和解析HTML。Jsoup提供了一个有用的用于提取数据的API。jsoup中实现的标准是WHATWG HTML5。和最新的浏览器作法一样，jsoup将HTML解析为DOM。它允许解析来自任何URL或文件的HTML，清理和操纵HTML元素和属性，以检索用户提交的数据并过滤掉XSS攻击属性，使用jsoup还可以完成更多功能。
- Gson
- Joda Time：简单但功能强大的库，它节省了大量的开发时间。Joda-Time是一个Java库，作为Java中日期和时间类的一个很好的替代品。Joda Time提供计算日期和时间的功能，并支持几乎所有需要的日期格式，而且肯定难以用简单的JDK方法进行复制
- Ok HTTP：用于通过HTTP协议有效地在现代应用程序之间交换数据。Okhttp在断网时恢复连接，在多个基于IP的服务中切换IP地址。okhttp的一个有用的功能是与现代TLS(SNI，ALPN)的自动连接，并且在发生故障时回到TLS 1.0。
- Quartz
