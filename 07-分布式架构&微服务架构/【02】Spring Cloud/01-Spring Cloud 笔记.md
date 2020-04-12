# 微服务与Spring Cloud简介
## 1. 微服务简介

微服务架构的系统是一个分布式系统，按业务领域划分为独立的服务单元，有自动化运维容错、快速演进的特点，它能够解决传统单体架构系统的痛点，同时也能满足复杂的业务需求。

### 1.1. 单体架构使用服务器集群及存在的不足

面对业务发展与用户量增加带来的高并发访问，可以将单体应用进行集群部署，并增加负载均衡服务器（如Nginx等）。另外，还需要增加集群部署的缓存服务器和文件服务器，并将数据库读写分离

![单体服务的集群化](images/_单体服务的集群化_1536457262_17924.png)

- 用负载均衡服务器分发高并发的网络请求，用户的访问被分派到不同的应用服务器，用户量增加时，添加应用服务器即可。通过添加缓存服务器来缓解数据库的数据以及数据库读取数据的压力。大多数的读取操作是由缓存完成，但仍然有少数读操作是从数据库读取的，例如缓存失效、实时数据等。
- 当有大量读写操作时，可以将数据库进行读写分离，例如MySQL的主从热备份，通过相关配置可以将主数据库服务器的数据同步到从数据库服务器，实现数据库的读写分离，改善数据库的负载能力。
- 该架构存在不足：
  - 代码可读性和可维护性很差
  - 数据库成为瓶颈
  - 持续交付能力差

### 1.2. 微服务特点

- 按业务划分成一个独立运行的程序，即服务单元
- 服务之间通过HTTP协议相互通信
- 自动化部署
- 可以用不同的编程语言
- 可以用不同的存储技术
- 服务集中化管理
- 微服务是一个分布式系统，具有极强的横向扩展能力，微服务可以集群化部署

### 1.3. 微服务通过HTTP相互通信

- 微服务单元之间的通信一般使用HTTP的通信机制，更多的时候使用RESTFUL api的，可以跨平台与语言
- 服务与服务之间也可以通过轻量级的消息总线来通信，如RabbitMQ，Kafaka等
- 服务与服务之间的通信格式一般为json、xml。这两种数据格式与平台、语言、通信协议无关

### 1.4. 微服务的数据库独立

微服务都有自己独立的数据库，数据库之间没有任何联系。数据库的存储方式可以是关系型数据库，也可以是非关系数据库（如：MongoDB、Redis）

### 1.5. 微服务的自动化部署

微服务架构有多少服务就需要部署多少次，所以需要使用Docker容器技术以及自动化部署工具（如：开源组件Jenkins）

### 1.6. “雪崩效应”

分布式的服务相互依赖，如果一个服务出现故障或者网络延迟，在高并发的情况下，会导致线程阻塞，短时间内该服务的线程资源会消耗完，使用该服务不可用。由于服务的相互依赖性，可能会导致整个系统的不可用，这叫**“雪崩效应”**

### 1.7. 服务集中化管理

Spring cloud 采用Eureka来注册服务和发现服务。也可以使用Zookeeper、Consul等服务集中化管理框架

### 1.8. “熔断机制”

为了防止“雪崩效应”，分布式系统采用了**熔断机制**。在Spring Cloud构建的微服务系统中，采用了**熔断器**（即Hystrix组件的Ciruit Breaker）去做熔断

例：当服务b出现故障，请求失败次数超过设定的阀值之后，服务b就会开启熔断器，之后服务b不进行任何的业务逻辑操作，执行快速失败并直接返回请求失败的信息。其他依赖b的服务就不会因为得不到响应而线程阻塞。此时除了服务b和依赖于服务b的部分功能不可用外，其他功能正常

熔断器还具有一个**自我修复的机制**。即当服务b熔断后，半打开的熔断器会检查一部分请求，其他请求执行快速失败响应，检查的部分如何成功，则会判定服务b正常，就会关闭服务b的熔断器；如果不正常，则继续打开熔断器。

熔断组件往往会提供一系列的监控，例如：服务是否可用，熔断器是否被打开、目前的吞吐量、网络延迟状态的监控等。

### 1.9. 微服务的不足
#### 1.9.1. 微服务的复杂度

服务与服务之间相互依赖，如果修改某个服务，会对另一个服务产生影响。比如修改一个比较基础的服务，可能需要重启所有的服务才能完成测试

#### 1.9.2. 分布式事务

分布式CAP理论：即同时满足“一致性”“可用性”和“分区容错”是一件不可能的事，应该有所取舍

- Consistency：指数据的强一致必。如果写入某个数据成功，之后读取到的都是新写入的数据；如果写入失败，之后读取的都不是写入的数据
- Availability：指服务的可用性
- Partition-tolerance：指分区容错

![分布式事务阶段](images/_分布式事务阶段_1537091322_12021.jpg)

分布式事务提交需要两个阶段

第1个阶段：Service-account发起一个分布式事务，交给事务协调器TC处理，事务协调器TC向所有参与的事务的节点发送处理事务操作的准备操作，将Undo和Redo信息写进日志，并向事务管理器返回准备操作是否成功

第2个阶段：事务管理器收集的呢节点的准备操作是否成功，如果都成功，则通知所有的节点执行提交操作；如果有一个失败，则执行回滚操作
但如果第1阶段都成功，而执行第2阶段的某一个节点失败，仍然导致数据的不准确。如果分布式事务涉及的节点很多，某个节点的网络出现异常会导致整个事务处于阻塞状态，大大降低数据库的性能。所以一般情况下，尽量少用分布式事务

#### 1.9.3. 服务的划分

领域驱动设计

#### 1.9.4. 服务的部署

微服务是核心；Docker容器技术，是微服务最佳部署的容器；DevOps是一种部署手段或理念。

## 2. Spring Cloud 简介

Spring Cloud是java语言的微服务框架，依赖于Spring boot。

### 2.1. 微服务具备的功能
#### 2.1.1. 服务的注册与发现

- 服务注册是指向服务注册中心注册一个服务实例，服务提供者将自己的服务信息（如服务名、IP地址等）告知服务注册中心。
- 服务发现是指当服务消费者需要消费另外一个服务时，服务注册中心能够告知服务消费者所需要消费服务的实例信息（如服务名、IP地址等）。
- 一个服务既是服务提供者，也是服务消费者。服务消费者一般使用HTTP协议或者消费。

#### 2.1.2. 服务的负载均衡

- 为了保证服务的高可用，服务单元往往是集群化部署
- 由于服务注册中心既要接收每个服务的定时心跳，而且每个服务会定期获取服务注册列表的信息，所以**一般做法将服务注册中心集群化，每个服务注册中心的数据实时同步**
- 服务提供者可以进行集群化部署，服务消费者集成负载均衡组件，该组件会向服务消费者获取服务注册列表信息，并每隔一段时间重新刷新获取列表。当消费服务时，负载均衡组件获取服务提供者所有注册信息，并通过负载均衡策略，选择一个服务提供者进行消费

![服务注册中心](images/_服务注册中心_1537091487_12049.jpg)

#### 2.1.3. 服务的容错（熔断器机制）

![熔断器机制](images/_熔断器机制_1537091564_20291.jpg)

熔断器机制的作用：

- 将资源进行隔离，只会隔离故障的API接口，不会影响其他的API接口
- 服务降级的功能。当大量请求剏并发时，熔断器会被打开，将服务降级，以免服务器因负载过高而出现故障
- 自我修复能力。

#### 2.1.4. 服务网关（API Gateway）

- 微服务系统中，API接口资源通常是由服务网关（也称API网关）统一暴露。API Gateway 是一个服务器，也可以说是进入系统的唯一节点。
- API Gateway 封装内部系统的架构，并且提供 API 给各个客户端。它还可能有其他功能，如授权、监控、负载均衡、缓存、请求分片和管理、静态响应处理等
- 网关层作用：
	1. 网关将所有服务的API接口资源统一暴露
	2. 网关可以实现用户身份认证、权限认证、防止非法请求操作API接口
	3. 网关可以实现监控功能，实时日志输出，对请求进行记录
	4. 网关可以做流量监控
	5. API接口从内部分离出来

![API网关示意图](images/20190508104750511_32130.png)

**API Gateway 负责请求转发、合成和协议转换**。所有来自客户端的请求都要先经过 API Gateway，然后路由这些请求到对应的微服务。API Gateway 将经常通过调用多个微服务来处理一个请求以及聚合多个服务的结果。它可以在 web 协议与内部使用的非 Web 友好型协议间进行转换，如 HTTP 协议、WebSocket 协议。

### 2.2. Spring Cloud 常用组件

1. 服务注册和发现组件Eureka
2. 熔断组件Hystrix
3. 负载均衡组件Ribbon
4. 路由网关Zuul
    - 上述4个组件都是Netflix公司，统一称为Spring Cloud Netflix
5. 配置文件统一管理Spring Cloud Config
6. 服务单元用户验证和权限认证Spring Cloud Security
7. 分布式链路追踪组件Spring Cloud Sleuth
8. Spring Cloud数据流操作包Spring Cloud Stream

### 2.3. Spring Cloud 版本说明
#### 2.3.1. 版本命名说明

- 多数Spring项目都是以【`主版本号.次版本号.增量版本号.里程碑版本号`】的形式命名版本号的。如：Spring Framework稳定版本4.3.5.RELEASE、里程碑版本5.0.0.M4等
- 其中，主版本号表示项目的重大重构；次版本号表示新特性的添加和变化；增量版本号一般表示bug修复；里程碑版本号表示某版本号的里程碑
- 而Spring Cloud采用了伦敦地铁站的名字，根据字母表的顺序来对应版本时间顺序。以此方式的命名，为了**避免与子项目版本号的重名误解**

#### 2.3.2. 选择Spring Cloud版本与Spring Boot版本例子

- 在选择了Spring Cloud版本后，需要在官网上选择对应的子项目的版本。pom.xml的依赖配置案例如下：
  - Spring Cloud版本Finchley.RELEASE对应Spring boot版本为2.0.1.RELEASE
  - Spring Cloud版本Dalston.SR5对应Spring boot版本为1.5.1.RELEASE

![版本1](images/_版本1_1537092365_12871.jpg)

![版本2](images/_版本2_1537092381_3809.jpg)

#### 2.3.3. Spring Cloud 对应的子项目版本

官网截图（2018.6.24）：https://projects.spring.io/spring-cloud/#quick-start

![版本](images/_版本_1537092443_14039.jpg)

对应子项目的说明，红色部门是重点掌握

|         Component         | 备注                                                                                            |
| :-----------------------: | :---------------------------------------------------------------------------------------------- |
|     spring-cloud-aws      | 用于简化整合Amazon Web Service的组件                                                              |
|     spring-cloud-bus      | 事件、消息总线，用于传播集群中的状态变化或事件。                                                     |
|     spring-cloud-cli      | 用于在Groovy平台创建Spring Cloud应用。                                                            |
|   spring-cloud-commons    | 服务发现、负载均衡、熔断机制这种模式为Spring Cloud客户端提供了一个通用的抽象层。                        |
|   spring-cloud-contract   |                                                                                                 |
|  **spring-cloud-config**  | **配置管理工具，支持使用git、svn等存储配置文件。并在支持客户端配置信息的刷新，加密解密配置内容等。** |
| **spring-cloud-netflix**  | **核心组件，对多个Netflix OSS开源套件进行整合。**                                                 |
|   spring-cloud-security   | 安全工具包。                                                                                     |
| spring-cloud-cloudfoundry | 整合Pivotal Cloudfoundry（Vmware推出的业界第一个开源PaaS云平台）支持。                              |
|    spring-cloud-consul    | 服务发现与配置管理工具                                                                            |
|  **spring-cloud-sleuth**  | **Spring Cloud应用的分布式跟踪实现。**                                                           |
|  **spring-cloud-stream**  | **通过Redis、RabbitMQ、Kafka实现的消息微服务。**                                                 |
|  spring-cloud-zookeeper   | 基于ZooKeeper的服务发现与配置管理组件。                                                            |
|      **spring-boot**      |                                                                                                 |
|     spring-cloud-task     | 用于快速构建数据处理的应用。                                                                       |
|    spring-cloud-vault     |                                                                                                 |
|   spring-cloud-gateway    | Spring Cloud网关相关的整合实现。                                                                  |

### 2.4. Spring Cloud 框架的特点

- 约定优于配置
- 适用于各种环境。开发、部署在PC Server或各种云环境（如阿里云、AWS等）
- 隐藏了组件的复杂性，并提供声明式、无xml的配置方式
- 开箱即用，快速启动
- 轻量级的组件。Spring Cloud整合的组件大多比较轻量。如：Eureka、Zuul等等，都是各自领域轻量级的实现
- 组件丰富，功能齐全。Spring Cloud为微服务架构提供了非常完整的支持，如：配置管理、服务发现、熔断器、微服务网关等
- 选型中立、丰富。支持使用Eureka、Zookeeper或Consul实现服务发现
- 灵活。Spring Cloud的组成部分是解耦的

### 2.5. Spring Cloud和Spring Boot的关系

- Spring boot 是 Spring 的一套快速配置脚手架，可以基于spring boot 快速开发单个微服务，Spring Cloud是一个基于Spring Boot实现的云应用开发工具；
- Spring boot专注于快速、方便集成的单个个体，Spring Cloud是关注全局的服务治理框架；
- Spring boot使用了默认大于配置的理念，很多集成方案已经帮你选择好了，能不配置就不配置，Spring Cloud很大的一部分是基于Spring boot来实现，可以不基于Spring boot吗？不可以。
- Spring boot可以离开Spring Cloud独立使用开发项目，但是Spring Cloud离不开Spring boot，属于依赖的关系。
- spring -> spring booot -> spring cloud 这样的关系。

### 2.6. Kubernetes简介

Kubernetes是一个容器集群管理系统，为容器化的应用程序提供部署运行、维护、扩展、资源调度、服务发现等功能

---

# Eureka 服务注册和发现
## 1. 微服务的注册与发现

![注册与发现](images/20190501091354200_14049.jpg)

由上图可以看出：

1. 服务提供者将服务注册到注册中心
2. 服务消费者通过注册中心查找服务
3. 查找到服务后进行调用（这里就是无需硬编码url的解决方案）
4. 服务的消费者与服务注册中心保持心跳连接，一旦服务提供者的地址发生变更时，注册中心会通知服务消费者

## 2. 注册中心Eureka

Spring Cloud提供了多种注册中心的支持，如：Eureka、ZooKeeper等。推荐使用Eureka。

- Eureka包含两个组件：Eureka Server和Eureka Client。
- 其中主要包括以下3种角色
  - Register Service：服务注册中心，是Eureka Server，提供服务注册与发现的服务
  - Provider Service：服务提供者，是Eureka Client，提供服务
  - Consumer Service：服务消费者，是Eureka Client，消费服务

### 2.1. 原理

- Eureka Server提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。
- Eureka Client是一个java客户端，用于简化与Eureka Server的交互，客户端同时也就别一个内置的、使用轮询(round-robin)负载算法的负载均衡器。
- 在应用启动后，将会向Eureka Server发送心跳,默认周期为30秒，如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除(默认90秒)。
- Eureka Server之间通过复制的方式完成数据的同步，Eureka还提供了客户端缓存机制，即使所有的Eureka Server都挂掉，客户端依然可以利用缓存中的信息消费其他服务的API。综上，Eureka通过心跳检查、客户端缓存等机制，确保了系统的高可用性、灵活性和可伸缩性。

### 2.2. 创建Eureka Server注册中心

参考案例，D:\code\IdeaProjects\spring-cloud-project，下面配置eureka server以此工程为案例

#### 2.2.1. 父工程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!-- 父项目的坐标 -->
    <groupId>com.moon</groupId>
    <artifactId>spring-cloud-project</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>spring-boot-demo</module>
        <module>springcloud-microservice-item</module>
        <module>springcloud-microservice-order</module>
        <module>springcloud-microservice-eureka</module>
    </modules>

    <!-- 引入Spring Boot的父项目依赖 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.13.RELEASE</version>
        <relativePath/><!-- lookup parent from repository -->
    </parent>

    <!-- 配置相关属性 -->
    <properties>
        <!-- 指定整个项目的编码是UTF-8 -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <!-- 配置JDK编译环境，因在是Spring Boot，不需要配置JDK插件 -->
        <java.version>1.8</java.version>
        <!-- 指定spring-cloud的版本 -->
        <spring-cloud.version>Dalston.SR5</spring-cloud.version>
    </properties>

    <!-- 锁定依赖的版本 -->
    <dependencyManagement>
        <dependencies>
            <!-- 锁定spring cloud版本 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- 配置公共的依赖 -->
    <dependencies>
        <!-- Spring Boot应用程序用于测试包括JUnit，Hamcrest和Mockito -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

#### 2.2.2. 创建eureka-server工程，导入依赖（注：需要导入Spring Cloud的管理依赖。）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-project</artifactId>
        <groupId>com.moon</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>springcloud-microservice-eureka</artifactId>
    <name>springcloud-microservice-eureka</name>

    <dependencies>
        <!-- 导入Eureka服务的依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <!-- 资源文件拷贝插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
            <!-- java编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 2.2.3. 编写spring-cloud工程的启动类，`@EnableEurekaServer`注解开启Eureka Server的功能

```java
package com.moon.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka服务启动类
 */
@EnableEurekaServer    // 申明此类为eureka服务
@SpringBootApplication
public class EurekaServer {
  public static void main(String[] args) {
    SpringApplication.run(EurekaServer.class, args);
  }
}
```

#### 2.2.4. 编写application.yml配置文件

```yml
server:
  port: 6868 # 服务端口
eureka:
  client:
    registerWithEureka: false # 是否将自己注册到Eureka服务中，本身就是所有无需注册
    fetchRegistry: false # 是否从Eureka中获取注册信息
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://127.0.0.1:${server.port}/eureka/
```

#### 2.2.5. 启动程序做测试

![eureka服务](images/eureka服务_1537678040_28740.png)

### 2.3. 将微服务注册到Eureka

将案例中的商品微服务注册到Eureka服务中

#### 2.3.1. 第一步：修改pom文件，引入Spring Cloud的管理依赖以及eureka服务依赖。

因为在父工程中配置了`spring-cloud-dependencies`的spring clound版本锁定，不需要指定版本

```xml
<!-- 配置Spring Boot依赖 -->
<dependencies>
  ......
  <!-- 导入Eureka服务的依赖 -->
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
  </dependency>
</dependencies>
```

#### 2.3.2. 第二步，修改application.yml配置文件

```yml
server:
  port: 8081 # 配置服务端口

# 指定服务名
spring:
  application:
    name: springcloud-microservice-item

# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl:
      defaultZone: http://127.0.0.1:6868/eureka/ # Eureka客户端与Eureka服务端进行交互的地址
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
```

#### 2.3.3. 第三步，修改启动类，增加`@EnableDiscoveryClient`或`@EnableEurekaClient`注解

```java
// @EnableEurekaClient	// 开启Eureka Client功能，基于spring-cloud-netflix依赖，只能为eureka作用
@EnableDiscoveryClient	// 申明是一个eureka client，基于spring-cloud-commons依赖，并且在classpath中实现
@SpringBootApplication	// 申明此类为Spring Boot项目
public class ItemApplication {
  public static void main(String[] args) {
    SpringApplication.run(ItemApplication.class, args);
  }
}
```

#### 2.3.4. 启动测试

开启springcloud-microservice-item与springcloud-microservice-eureka工程，已经将微服务注册到Eureka server中

Staus为UP（在线），说明已经注册成功

![注册服务](images/注册服务_1537750423_7401.png)

### 2.4. 注解@EnableDiscoveryClient，@EnableEurekaClient的区别

两种注解的用法上基本一致。

1. `@EnableDiscoveryClient`注解是基于spring-cloud-commons依赖，并且在classpath中实现；
2. `@EnableEurekaClient`注解是基于spring-cloud-netflix依赖，只能为eureka作用；
3. 更简单来说，就是如果选用的注册中心是eureka，那么就推荐`@EnableEurekaClient`，如果是其他的注册中心，那么推荐使用`@EnableDiscoveryClient`。

如果你的classpath中添加了eureka，则它们的作用是一样的。

注解`@EnableEurekaClient`上有`@EnableDiscoveryClient`注解，可以说基本就是`@EnableEurekaClient`有`@EnableDiscoveryClient`的功能，另外上面的注释中提到，其实`@EnableEurekaClient`注解就是一种方便使用eureka的注解而已，可以说使用其他的注册中心后，都可以使用`@EnableDiscoveryClient`注解，但是使用`@EnableEurekaClient`的情景，就是在服务采用eureka作为注册中心的时候，使用场景较为单一

### 2.5. 订单系统从Eureka发现服务

订单系统中是将商品微服务的地址进行了硬编码，将商品服务注册到Eureka后，现只需要从Eureka中发现服务即可

#### 2.5.1. 第一步，在订单系统中添加依赖

因为在父工程中配置了`spring-cloud-dependencies`的spring clound版本锁定，不需要指定版本

```xml
<!-- 导入Eureka服务的依赖 -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

#### 2.5.2. 第二步，修改application.yml配置文件

```yml
server:
  port: 8082 # 定义订单微服务端口

# 指定服务名
spring:
  application:
    name: springcloud-microservice-order

# 配置发现eureka服务
eureka:
  client:
    registerWithEureka: false  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl:
      defaultZone: http://127.0.0.1:6868/eureka/ # Eureka客户端与Eureka服务端进行交互的地址
```

#### 2.5.3. 第三步，修改ItemService的实现逻辑

```java
import com.moon.microservice.order.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ItemService {
  // Spring框架对RESTful方式的http请求做了封装，来简化操作
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private DiscoveryClient discoveryClient;

  // 模拟调用item微服务查询商品数据的方法
  public Item queryItemById(Long id) {
    // 定义注册中心服务的id
    String serviceId = "springcloud-microservice-item";
    // 获取服务实例
    List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
    if (instances.isEmpty()) {
      return null;
    }

    // 获取其中一个实例看效果
    ServiceInstance instance = instances.get(0);
    String url = instance.getHost() + ":" + instance.getPort();

    // 拼接url
    StringBuilder builder = new StringBuilder("http://");
    builder.append(url).append("/item/").append(id);

    return restTemplate.getForObject(builder.toString(), Item.class);
	}
}
```

#### 2.5.4. 第四步，在启动类中添加`@EnableDiscoveryClient`或`@EnableEurekaClient`注解

```java
@EnableEurekaClient // 申明为eureka client服务
// @EnableDiscoveryClient // 申明为eureka client服务
@SpringBootApplication
public class OrderApplication {
  // 该注解只能写在方法上，表明使用此方法创建RestTemplate对象，并且交给spring管理
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(OrderApplication.class, args);
  }
}
```

#### 2.5.5. 第五步，启动测试

测试url：http://127.0.0.1:8082/order/59193738268961441

可以获取数据，但响应数据变成xml结构

#### 2.5.6. 解决响应变成xml的问题

原因：引入了eureka server的依赖，导致破坏了之前SpringMVC默认的配置，从而导致了响应成了xml

解决方法：在order工程中pom.xml，排除eureka server中的xml依赖，

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
  <exclusions>
    <exclusion>
      <groupId>com.fasterxml.jackson.dataformat</groupId>
      <artifactId>jackson-dataformat-xml</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

### 2.6. 为Eureka添加用户认证

添加用户认证，即需要密码才能访问查询注册中心的信息

1. 为springcloud-microservice-eureka添加安全认证依赖

```xml
<!-- eureka安全认证依赖 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2. 增加application.yml配置文件

```yml
# 开启基于HTTP basic的认证
security:
  basic:
    enabled: true
# 配置用户的账号信息
  user:
    name: moon
    password: moon
```

3. 重新启动Eureka服务进行测试，输入正确的用户名密码即可登录

**注：服务提供者注册到Eureka时会报错，需要在服务注册时也设置用户名和密码**

```cmd
2018-10-01 07:24:51.730  INFO 3652 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SPRINGCLOUD-MICROSERVICE-ITEM/localhost:springcloud-microservice-item:8081: registering service...
2018-10-01 07:24:51.745  WARN 3652 --- [nfoReplicator-0] c.n.d.s.t.d.RetryableEurekaHttpClient    : Request execution failure with status code 401; retrying on another server if available
2018-10-01 07:24:51.745  WARN 3652 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SPRINGCLOUD-MICROSERVICE-ITEM/localhost:springcloud-microservice-item:8081 - registration failed Cannot execute request on any known server
```

#### 2.6.1. 服务注册时设置账户信息

服务注册到有认证需求的注册中心时，需要设置如下信息

例子：`http://USER:PASSWORD@127.0.0.1:6868/eureka/`

将原来eureka-->client-->serviceUrl-->defaultZone的配置增加用户与密码，与服务配置的密码一致
```yml
# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:6868/eureka/
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
```

### 2.7. Eureka的自我保护模式

![自我保护](images/自我保护_1538353302_14088.png)

默认情况下，如果Eureka Server在一定时间内没有接收到某个微服务实例的心跳，Eureka Server将会注销该实例（默认90秒）。但是当网络分区故障发生时，微服务与Eureka Server之间无法正常通信，这就可能变得非常危险了----因为微服务本身是健康的，此时本不应该注销这个微服务。

Eureka Server通过“自我保护模式”来解决这个问题----当Eureka Server节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。一旦进入该模式，Eureka Server就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。当网络故障恢复后，该Eureka Server节点会自动退出自我保护模式。

自我保护模式是一种对网络异常的安全保护措施。使用自我保护模式，而已让Eureka集群更加的健壮、稳定。

**一般进入自我保护模式，无需处理**。如果需要禁用自我保护模式，只需要在eureka server配置文件中添加配置:
```yml
eureka:
  client:
    registerWithEureka: false #是否将自己注册到Eureka服务中，本身就是所有无需注册
    fetchRegistry: false #是否从Eureka中获取注册信息
    serviceUrl: #Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://127.0.0.1:${server.port}/eureka/
  server:
    enable-self-preservation: false # 禁用自我保护模式
```
**提示**，如果禁用自我保护模式，在网络通信故障下会出现问题。

![关闭自我保护](images/关闭自我保护_1538541399_2503.png)

### 2.8. Eureka的高可用

Eureka服务是一个单点服务，在生产环境就会出现单点故障，为了确保Eureka服务的高可用，需要搭建Eureka服务的集群。

搭建Eureka集群非常简单，只要启动多个Eureka服务并且让这些服务之间彼此进行注册即可实现。

- 第一步，修改itcast-microservice-eureka的application.yml文件：

```yml
server:
  port: 6868 #服务端口
spring:
  application:
    name: springcloud-microservice-eureka
eureka:
  client:
    registerWithEureka: true #是否将自己注册到Eureka服务中，如需要搭建Eureka服务的集群，必须注册到Eureka服务中
    fetchRegistry: true #是否从Eureka中获取注册信息
    serviceUrl: #Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:6869/eureka/
  server:
    enable-self-preservation: true # 是否禁用自我保护模式

# 开启基于HTTP basic的认证
security:
  basic:
    enabled: true
# 配置用户的账号信息
  user:
    name: moon
    password: moon
```

- 第二步，修改配置文件(设置server.port为6869，defaultZone端口改成6868)，再启动一个Eureka服务，进行重启测试：

### 2.9. 将服务注册到Eureka集群

服务注册到Eureka集群时，可以指定多个，也可以指定一个Eureka服务（因为Eureka服务集群间彼此互联）。

修改案例配置，将集群url配置到defaultZone中

```yml
server:
  port: 8081 # 配置服务端口

# 指定服务名
spring:
  application:
    name: springcloud-microservice-item

logging:
  level:
    org.springframework: debug

# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:6868/eureka/,http://moon:moon@127.0.0.1:6869/eureka/
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
```

可以通过停止其中一个Eureka服务进行测试，结果会发现集群是高可用。

### 2.10. 指定服务的IP地址

在服务的*提供者*(即案例中的item商品服务)配置文件中可以指定ip地址(ip-address属性)

```yml
server:
  port: 8081 # 配置服务端口

# 指定服务名
spring:
  application:
    name: springcloud-microservice-item

# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:6868/eureka/
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
    ip-address: 127.0.0.1
```

![指定ip](images/指定ip_1538614353_14508.png)

### 2.11. 指定实例id

通过instance-id 参数指定服务注册到Eureka中的服务实例id：

```yml
server:
  port: 8081 # 配置服务端口

# 指定服务名
spring:
  application:
    name: springcloud-microservice-item

# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:6868/eureka/
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
    ip-address: 127.0.0.1
    instance-id: ${spring.application.name}:${server.port}   # 指定实例id
```

## 3. 使用Ribbon实现负载均衡

在Spring Cloud中推荐使用Ribbon来实现负载均衡。即使用客户端根据服务的负载情况去选择空闲或者访问压力小的服务

### 3.1. Ribbon简介

负载均衡分为服务端负载均衡和客户端负载均衡，SpringCloud Ribbon是基于客户端的负载均衡工具。

Ribbon是Netflix发布开源项目的负载均衡器，有助于控制HTTP和TCP客户端的行为。为Ribbon配置服务提供者地址列表后，Ribbon基于某种负载均衡得法，自动地帮助服务消费者去请求。主要功能是提供客户端的软件负载均衡算法，如轮询、随机等，它将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。也可为Ribbon实现自定义的负载均衡算法

### 3.2. Ribbon架构

![Ribbon架构](images/ribbon架构_1538697723_25876.jpg)

### 3.3. Ribbon使用案例
#### 3.3.1. 为itcast-microservice-order增加ribbon依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```

其实该依赖是可以省略的，因为spring-cloud-starter-eureka-server中已经包含了spring-cloud-starter-ribbon

> *注：在学成项目实践中，使用spring cloud的版本是Finchley.SR1，如果在微服务中引入spring-cloud-starter-ribbon会提示找不到版本号。需要将其修改为spring-cloud-starter-netflix-ribbon才能找到依赖的版本号*

#### 3.3.2. RestTemplate设置@LoadBalanced注解

`@LoadBalanced`注解用于负载均衡，使用在Spring为创建Rest API对象

```java
// 该注解只能写在方法上，表明使用此方法创建RestTemplate对象，并且交给spring管理
@Bean
// 开启负载均衡
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}
```

### 3.4. Ribbon 源码解析
#### 3.4.1. 实现分析（待整理）

#### 3.4.2. 总结

Ribbon 的负载均衡主要是通 LoadBalancerClient 来实现，而 LoadBalancerClient 具体是交给 ILoadBalancer 来处理，ILoadBalancer 通过配置 IRule、IPing 等，向 EurekaClient 获取注册列表信息，默认每10秒向 EurekaClient 发送一次“ping” 请求，用于检查是否需要更新服务的注册列表信息。最后，在得到服务注册列表令牌后，ILoadBalancer 根据 IRule 的策略进行负载均衡。

在 RestTemplate 加上 `@LoadBalanced` 注解后，在远程调度时能够负载均衡，主要是维护了一个被 `@LoadBalanced` 注解的 RestTemplate 列表，并给该列表中的 RestTemplate 对象添加了拦截器。在拦截器的方法中，将远程调度方法交给了 Ribbon 的负载均衡器 LoadBalancerClient 去处理，从而达到了负载均衡的目的。







