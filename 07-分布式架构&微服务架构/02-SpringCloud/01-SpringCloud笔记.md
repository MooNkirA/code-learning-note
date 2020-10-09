# 微服务与Spring Cloud简介

## 1. 微服务基础知识

### 1.1. 系统架构的演变

随着互联网的发展，网站应用的规模不断扩大，常规的应用架构已无法应对，分布式服务架构以及微服务架构势在必行，亟需一个治理系统确保架构有条不紊的演进。

#### 1.1.1. 单体应用架构

Web应用程序发展的早期，大部分web工程(包含前端页面，web层代码，service层代码，dao层代码)是将所有的功能模块，打包到一起并放在一个web容器中运行。比如搭建一个电商系统：客户下订单，商品展示，用户管理。这种将所有功能都部署在一个web容器中运行的系统就叫做单体架构。

面对业务发展与用户量增加带来的高并发访问，可以将单体应用进行集群部署，并增加负载均衡服务器（如Nginx等）。另外，还需要增加集群部署的缓存服务器和文件服务器，并将数据库读写分离

用负载均衡服务器分发高并发的网络请求，用户的访问被分派到不同的应用服务器，用户量增加时，添加应用服务器即可。通过添加缓存服务器来缓解数据库的数据以及数据库读取数据的压力。大多数的读取操作是由缓存完成，但仍然有少数读操作是从数据库读取的，例如缓存失效、实时数据等。

当有大量读写操作时，可以将数据库进行读写分离，例如MySQL的主从热备份，通过相关配置可以将主数据库服务器的数据同步到从数据库服务器，实现数据库的读写分离，改善数据库的负载能力。

![](images/20201007080316644_18705.png)

优点：

- 所有的功能集成在一个项目工程中
- 项目架构简单，前期开发成本低，周期短，小型项目的首选

缺点：

- 全部功能集成在一个工程中，对于大型项目不易开发、扩展及维护
- 系统性能扩展只能通过扩展集群结点，成本高、有瓶颈
- 技术栈受限

#### 1.1.2. 垂直应用架构

当访问量逐渐增大，单一应用增加机器带来的加速度越来越小，将应用拆成互不相干的几个应用，以提升效率

![](images/20201007080455987_32693.png)

优点：

- 项目架构简单，前期开发成本低，周期短，小型项目的首选
- 通过垂直拆分，原来的单体项目不至于无限扩大
- 不同的项目可采用不同的技术

缺点：

- 全部功能集成在一个工程中，对于大型项目不易开发、扩展及维护
- 系统性能扩展只能通过扩展集群结点，成本高、有瓶颈

#### 1.1.3. 分布式SOA架构

##### 1.1.3.1. SOA 概述

SOA 全称为 Service-Oriented Architecture，即面向服务的架构。它可以根据需求通过网络对松散耦合的粗粒度应用组件(服务)进行分布式部署、组合和使用。一个服务通常以独立的形式存在于操作系统进程中

站在功能的角度，把业务逻辑抽象成可复用、可组装的服务，通过服务的编排实现业务的快速再生，其主要目的是把原先固有的业务功能转变为通用的业务服务，实现业务逻辑的快速复用。

**SOA 架构特点：分布式、可重用、扩展灵活、松耦合**

##### 1.1.3.2. SOA 架构

当垂直应用越来越多，应用之间交互不可避免，将核心业务抽取出来，作为独立的服务，逐渐形成稳定的服务中心，使前端应用能更快速的响应多变的市场需求

![](images/20201007081021927_6653.png)

优点：

- 抽取公共的功能为服务，提高开发效率
- 对不同的服务进行集群化部署解决系统压力
- 基于ESB/DUBBO减少系统耦合

缺点：

- 抽取服务的粒度较大
- 服务提供方与调用方接口耦合度较高

#### 1.1.4. 微服务架构

![](images/20201007081252320_3964.png)

优点：

- 通过服务的原子化拆分，以及微服务的独立打包、部署和升级，小团队的交付周期将缩短，运维成本也将大幅度下降
- 微服务遵循单一原则。微服务之间采用Restful等轻量协议传输

缺点：

- 微服务过多，服务治理成本高，不利于系统维护
- 分布式系统开发的技术成本高（容错、分布式事务等）

#### 1.1.5. SOA与微服务的关系

- **SOA（ Service Oriented Architecture ）“面向服务的架构”**：是一种设计方法，其中包含多个服务，服务之间通过相互依赖最终提供一系列的功能。一个服务通常以独立的形式存在与操作系统进程中。各个服务之间通过网络调用。
- **微服务架构**：其实和 SOA 架构类似，微服务是在 SOA 上做的升华，微服务架构强调的一个重点是“业务需要彻底的组件化和服务化”，原有的单个业务系统会拆分为多个可以独立开发、设计、运行的小应用。这些小应用之间通过服务完成交互和集成。

![](images/20201007081840669_9515.png)

### 1.2. 分布式核心知识

#### 1.2.1. 分布式中的远程调用

在微服务架构中，通常存在多个服务之间的远程调用的需求。<font color=red>**远程调用通常包含两个部分：序列化和通信协议**</font>。常见的序列化协议包括json、xml、hession、protobuf、thrift、text、bytes等，**目前主流的远程调用技术有基于HTTP的RESTful接口以及基于TCP的RPC协议**。

##### 1.2.1.1. HTTP的RESTfull接口

REST，即Representational State Transfer的缩写，如果一个架构符合REST原则，就称它为RESTful架构

- **资源（Resources）**：就是网络上的一个实体，或者说是网络上的一个具体信息。可以用一个URI（统一资源定位符）指向它，每种资源对应一个特定的URI。要获取这个资源，访问它的URI就可以
- **表现层（Representation）**：把"资源"具体呈现出来的形式。
- **状态转化（State Transfer）**：访问一个网站，就代表了客户端和服务器的一个互动过程。在这个过程中，势必涉及到数据和状态的变化。如果客户端想要操作服务器，必须通过某种手段，让服务器端发生"状态转化"（State Transfer）。客户端用到的手段就是HTTP协议里面，四个表示操作方式的动词：GET、POST、PUT、DELETE。它们分别对应四种基本操作：GET用来获取资源，POST用来新建资源（也可以用于更新资源），PUT用来更新资源，DELETE用来删除资源。

RESTful架构总结：

- 每一个URI代表一种资源
- 客户端和服务器之间，传递这种资源的某种表现层
- 客户端通过四个HTTP动词，对服务器端资源进行操作，实现"表现层状态转化"

##### 1.2.1.2. RPC协议

RPC（Remote Procedure Call ）一种进程间通信方式。允许像调用本地服务一样调用远程服务。RPC框架负责屏蔽底层的传输方式（TCP或者UDP）、序列化方式（XML/JSON/二进制）和通信细节。使用时只需关心调用哪个远程服务接口即可

![](images/20201007085905172_30732.png)

##### 1.2.1.3. RESTful与RPC的区别

![](images/20201007091242500_8267.png)

- HTTP相对更规范，更标准，更通用，无论哪种语言都支持http协议。如果对外开放API，例如开放平台，外部的编程语言多种多样，需要对每种语言的支持，现在开源中间件，基本最先支持的几个协议都包含RESTful
- RPC 框架作为架构微服务化的基础组件，它能大大降低架构微服务化的成本，提高调用方与服务提供方的研发效率，屏蔽跨进程调用函数（服务）的各类复杂细节。让调用方感觉就像调用本地函数一样调用远端函数、让服务提供方感觉就像实现一个本地函数一样来实现服务

#### 1.2.2. 分布式中的CAP原理

对于多数大型互联网应用都是分页式系统（distributed system），分布式系统的最大难点，就是各个节点的状态如何同步。CAP 定理是这方面的基本定理，也是理解分布式系统的起点。

分布式系统的CAP理论，把分布式系统归纳成以下三个特性：

- **Consistency（一致性）**：数据一致更新，所有数据的变化都是同步的
- **Availability（可用性）**：在集群中一部分节点故障后，集群整体是否还能响应客户端的读写请求
- **Partition tolerance（分区容忍性）**：某个节点的故障，并不影响整个系统的运行

> 注：通过学习CAP理论，得知任何分布式系统只可同时满足二点，没法三者兼顾，既然一个分布式系统无法同时满足一致性、可用性、分区容错性三个特点，所以就需要抛弃其中一点

![](images/20201007092209688_28377.png)

| 选择 |                                                     说明                                                      |
| --- | ------------------------------------------------------------------------------------------------------------ |
| CA   | 放弃分区容错性，加强一致性和可用性，其实就是传统的关系型数据库的选择                                                 |
| AP   | 放弃一致性（这里说的一致性是强一致性），追求分区容错性和可用性，这是很多分布式系统设计时的选择，例如很多NoSQL系统就是如此 |
| CP   | 放弃可用性，追求一致性和分区容错性，基本不会选择，网络问题会直接让整个系统不可用                                      |

需要明确一点的是，**在一个分布式系统当中，分区容忍性和可用性是最基本的需求，所以在分布是系统中最当关注的就是A（可用性）P（容忍性），通过补偿的机制寻求数据的一致性**

### 1.3. 常见微服务框架

#### 1.3.1. SpringCloud

Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利性巧妙地简化了分布式系统基础设施的开发，如**服务发现注册、配置中心、消息总线、负载均衡、断路器、数据监控**等，都可以用Spring Boot的开发风格做到一键启动和部署。Spring Cloud并没有重复制造轮子，它只是将目前各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包

官网：https://spring.io/projects/spring-cloud

#### 1.3.2. ServiceComb

Apache ServiceComb 是业界第一个Apache微服务顶级项目， 是一个开源微服务解决方案,致力于帮助企业、用户和开发者将企业应用轻松微服务化上云，并实现对微服务应用的高效运维管理。其提供一站式开源微服务解决方案，融合SDK框架级、零侵入ServiceMesh场景并支持多语言

官网：http://servicecomb.apache.org/cn/

#### 1.3.3. ZeroC ICE

ZeroC IceGrid 是ZeroC公司的杰作，继承了CORBA的血统，是新一代的面向对象的分布式系统中间件。作为一种微服务架构，它基于RPC框架发展而来，具有良好的性能与分布式能力

官网：https://zeroc.com/products/ice

## 2. Spring Cloud 简介

微服务架构的系统是一个分布式系统，按业务领域划分为独立的服务单元，有自动化运维容错、快速演进的特点，它能够解决传统单体架构系统的痛点，同时也能满足复杂的业务需求。

Spring Cloud是java语言的微服务框架，依赖于Spring boot。

### 2.1. 微服务特点

- 按业务划分成一个独立运行的程序，即服务单元
- 服务之间通过HTTP协议相互通信
- 自动化部署
- 可以用不同的编程语言
- 可以用不同的存储技术
- 服务集中化管理
- 微服务是一个分布式系统，具有极强的横向扩展能力，微服务可以集群化部署

#### 2.1.1. 微服务通过HTTP相互通信

- 微服务单元之间的通信一般使用HTTP的通信机制，更多的时候使用RESTFUL api的，可以跨平台与语言
- 服务与服务之间也可以通过轻量级的消息总线来通信，如RabbitMQ，Kafaka等
- 服务与服务之间的通信格式一般为json、xml。这两种数据格式与平台、语言、通信协议无关

#### 2.1.2. 微服务的数据库独立

微服务都有自己独立的数据库，数据库之间没有任何联系。数据库的存储方式可以是关系型数据库，也可以是非关系数据库（如：MongoDB、Redis）

#### 2.1.3. 微服务的自动化部署

微服务架构有多少服务就需要部署多少次，所以微服务部署的核心是Docker容器技术，是微服务最佳部署的容器，需要使用Docker容器技术以及自动化部署工具（如：开源组件Jenkins）。DevOps是一种部署手段或理念。

### 2.2. 微服务的不足

#### 2.2.1. 微服务的复杂度

服务与服务之间相互依赖，如果修改某个服务，会对另一个服务产生影响。比如修改一个比较基础的服务，可能需要重启所有的服务才能完成测试

#### 2.2.2. 分布式事务

![分布式事务阶段](images/_分布式事务阶段_1537091322_12021.jpg)

分布式事务提交需要两个阶段

第1个阶段：Service-account发起一个分布式事务，交给事务协调器TC处理，事务协调器TC向所有参与的事务的节点发送处理事务操作的准备操作，将Undo和Redo信息写进日志，并向事务管理器返回准备操作是否成功

第2个阶段：事务管理器收集的呢节点的准备操作是否成功，如果都成功，则通知所有的节点执行提交操作；如果有一个失败，则执行回滚操作。但如果第1阶段都成功，而执行第2阶段的某一个节点失败，仍然导致数据的不准确。如果分布式事务涉及的节点很多，某个节点的网络出现异常会导致整个事务处于阻塞状态，大大降低数据库的性能。所以一般情况下，尽量少用分布式事务

### 2.3. 服务的注册与发现

**服务注册**：服务实例将自身服务信息注册到注册中心。这部分服务信息包括服务所在主机IP和提供服务的Port，以及暴露服务自身状态以及访问协议等信息。

**服务发现**：服务实例请求注册中心获取所依赖服务信息。服务实例通过注册中心，获取到注册到其中的服务实例的信息（如服务名、IP地址等），通过这些信息去请求它们提供的服务

一个服务既是服务提供者，也是服务消费者。服务消费者一般使用HTTP协议或者消费。

![](images/20201007102145318_28542.png)

Spring cloud 采用Eureka来注册服务和发现服务。也可以使用Zookeeper、Consul等服务集中化管理框架

### 2.4. 负载均衡

负载均衡是高可用网络基础架构的关键组件，通常用于将工作负载分布到多个服务器来提高网站、应用、数据库或其他服务的性能和可靠性

为了保证服务的高可用，服务单元往往是集群化部署。由于服务注册中心既要接收每个服务的定时心跳，而且每个服务会定期获取服务注册列表的信息，所以**一般做法将服务注册中心集群化，每个服务注册中心的数据实时同步**

服务提供者可以进行集群化部署，服务消费者集成负载均衡组件，该组件会向服务消费者获取服务注册列表信息，并每隔一段时间重新刷新获取列表。当消费服务时，负载均衡组件获取服务提供者所有注册信息，并通过负载均衡策略，选择一个服务提供者进行消费

![](images/20201007102352510_8720.png)

### 2.5. 熔断（服务的容错）

#### 2.5.1. 雪崩效应

分布式的服务相互依赖，如果一个服务出现故障或者网络延迟，在高并发的情况下，会导致线程阻塞，短时间内该服务的线程资源会消耗完，使用该服务不可用。由于服务的相互依赖性，可能会导致整个系统的不可用，这叫**“雪崩效应”**

![](images/20201007102912557_16518.png)

#### 2.5.2. 熔断机制

为了防止“雪崩效应”，分布式系统采用了**熔断机制**。当下游服务因访问压力过大而响应变慢或失败，上游服务为了保护系统整体的可用性，可以暂时切断对下游服务的调用，从而保全整体的措施就叫做熔断。在Spring Cloud构建的微服务系统中，采用了**熔断器**（即Hystrix组件的Ciruit Breaker）去做熔断。

例：当服务b出现故障，请求失败次数超过设定的阀值之后，服务b就会开启熔断器，之后服务b不进行任何的业务逻辑操作，执行快速失败并直接返回请求失败的信息。其他依赖b的服务就不会因为得不到响应而线程阻塞。此时除了服务b和依赖于服务b的部分功能不可用外，其他功能正常

熔断器还具有一个**自我修复的机制**。即当服务b熔断后，半打开的熔断器会检查一部分请求，其他请求执行快速失败响应，检查的部分如何成功，则会判定服务b正常，就会关闭服务b的熔断器；如果不正常，则继续打开熔断器。

熔断组件往往会提供一系列的监控，例如：服务是否可用，熔断器是否被打开、目前的吞吐量、网络延迟状态的监控等。

![熔断器机制](images/_熔断器机制_1537091564_20291.jpg)

熔断器机制的作用：

- 将资源进行隔离，只会隔离故障的API接口，不会影响其他的API接口
- 服务降级的功能。当大量请求剏并发时，熔断器会被打开，将服务降级，以免服务器因负载过高而出现故障
- 自我修复能力。

### 2.6. 链路追踪

微服务按照不同的维度进行拆分，一次请求往往需要涉及到多个服务。互联网应用构建在不同的软件模块集上，这些软件模块，有可能是由不同的团队开发、可能使用不同的编程语言来实现、有可能布在了几千台服务器，横跨多个不同的数据中心。因此，就需要对一次请求涉及的多个服务链路进行日志记录，性能监控即**链路追踪**

![](images/20201007105032273_1936.png)

### 2.7. 服务网关（API Gateway）

随着微服务的不断增多，不同的微服务一般会有不同的网络地址，而外部客户端可能需要调用多个服务的接口才能完成一个业务需求，如果让客户端直接与各个微服务通信可能出现以下问题：

- 客户端需要调用不同的url地址，增加难度
- 再一定的场景下，存在跨域请求的问题
- 每个微服务都需要进行单独的身份认证

微服务系统中，API接口资源通常是由服务网关（也称API网关）统一暴露，由网关层统一接入和输出。API Gateway 是一个服务器，也可以说是进入系统的唯一节点。

一个网关的基本功能有：统一接入、安全防护、协议适配、流量管控、长短链接支持、容错能力。有了网关之后，各个API服务提供团队可以专注于自己的的业务逻辑处理，而API网关更专注于安全、流量、路由等问题。

API Gateway 封装内部系统的架构，并且提供 API 给各个客户端。它还可以有其他功能，如授权、监控、负载均衡、缓存、请求分片和管理、静态响应处理等。网关层作用如下：

1. 将所有服务的API接口资源统一暴露
2. 实现用户身份认证、权限认证、防止非法请求操作API接口
3. 实现监控功能，实时日志输出，对请求进行记录
4. 做流量监控
5. API接口从内部分离出来

![](images/20201007115616837_15459.png)

**API Gateway 负责请求转发、合成和协议转换**。所有来自客户端的请求都要先经过 API Gateway，然后路由这些请求到对应的微服务。API Gateway 将经常通过调用多个微服务来处理一个请求以及聚合多个服务的结果。它可以在 web 协议与内部使用的非 Web 友好型协议间进行转换，如 HTTP 协议、WebSocket 协议。

### 2.8. SpringCloud 的核心组件

Spring Cloud 的本质是在 Spring Boot 的基础上，增加了一堆微服务相关的规范，并对应用上下文（Application Context）进行了功能增强。目前 Spring Cloud 规范已有 Spring 官方，Spring Cloud Netflix，Spring Cloud Alibaba等实现。通过组件化的方式，Spring Cloud 将这些实现整合到一起构成全家桶式的微服务技术栈。

**Spring Cloud Netflix组件**

| 组件名称 |              作用               |
| ------- | ------------------------------ |
| Eureka  | 服务注册中心，服务的注册和发现组件 |
| Ribbon  | 客户端负载均衡组件               |
| Feign   | 声明式服务调用                   |
| Hystrix | 客户端容错保护，熔断组件          |
| Zuul    | API服务网关                     |

**Spring Cloud Alibaba组件**

| 组件名称 |     作用      |
| -------- | ------------ |
| Nacos    | 服务注册中心   |
| Sentinel | 客户端容错保护 |

**Spring Cloud原生及其他组件**

|    组件名称    |             作用              |
| ------------- | ---------------------------- |
| Consul        | 服务注册中心                   |
| Config        | 分布式配置中心，配置文件统一管理 |
| Gateway       | API服务网关                   |
| Sleuth/Zipkin | 分布式链路追踪组件             |
| Security      | 服务单元用户验证和权限认证      |
| Stream        | Spring Cloud数据流操作包       |

### 2.9. SpringCloud的体系结构图

![](images/20201007121639484_27784.png)

Spring Cloud各个组件相互配合，合作支持了一套完整的微服务架构。

- **注册中心**负责服务的注册与发现，很好将各服务连接起来
- **断路器**负责监控服务之间的调用情况，连续多次失败进行熔断保护。
- **API网关**负责转发所有对外的请求和服务
- **配置中心**提供了统一的配置信息管理服务，可以实时的通知各个服务获取最新的配置信息
- **链路追踪**技术可以将所有的请求数据记录下来，方便进行后续分析
- 各个组件又提供了功能完善的**dashboard监控平台**,可以方便的监控各组件的运行状况

### 2.10. Spring Cloud 版本说明

#### 2.10.1. 版本命名说明

- 多数Spring项目都是以【`主版本号.次版本号.增量版本号.里程碑版本号`】的形式命名版本号的。如：Spring Framework稳定版本4.3.5.RELEASE、里程碑版本5.0.0.M4等
- 其中，主版本号表示项目的重大重构；次版本号表示新特性的添加和变化；增量版本号一般表示bug修复；里程碑版本号表示某版本号的里程碑
- 而Spring Cloud采用了伦敦地铁站的名字，根据字母表的顺序来对应版本时间顺序。以此方式的命名，为了**避免与子项目版本号的重名误解**

#### 2.10.2. 选择Spring Cloud版本与Spring Boot版本例子

- 在选择了Spring Cloud版本后，需要在官网上选择对应的子项目的版本。pom.xml的依赖配置案例如下：
  - Spring Cloud版本Finchley.RELEASE对应Spring boot版本为2.0.1.RELEASE
  - Spring Cloud版本Dalston.SR5对应Spring boot版本为1.5.1.RELEASE

![版本1](images/_版本1_1537092365_12871.jpg)

![版本2](images/_版本2_1537092381_3809.jpg)

#### 2.10.3. Spring Cloud 对应的子项目版本

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

### 2.11. Spring Cloud 框架的特点

- 约定优于配置
- 适用于各种环境。开发、部署在PC Server或各种云环境（如阿里云、AWS等）
- 隐藏了组件的复杂性，并提供声明式、无xml的配置方式
- 开箱即用，快速启动
- 轻量级的组件。Spring Cloud整合的组件大多比较轻量。如：Eureka、Zuul等等，都是各自领域轻量级的实现
- 组件丰富，功能齐全。Spring Cloud为微服务架构提供了非常完整的支持，如：配置管理、服务发现、熔断器、微服务网关等
- 选型中立、丰富。支持使用Eureka、Zookeeper或Consul实现服务发现
- 灵活。Spring Cloud的组成部分是解耦的

### 2.12. Spring Cloud和Spring Boot的关系

- Spring boot 是 Spring 的一套快速配置脚手架，可以基于spring boot 快速开发单个微服务，Spring Cloud是一个基于Spring Boot实现的云应用开发工具；
- Spring boot专注于快速、方便集成的单个个体，Spring Cloud是关注全局的服务治理框架；
- Spring boot使用了默认大于配置的理念，很多集成方案已经帮你选择好了，能不配置就不配置，Spring Cloud很大的一部分是基于Spring boot来实现，可以不基于Spring boot吗？不可以。
- Spring boot可以离开Spring Cloud独立使用开发项目，但是Spring Cloud离不开Spring boot，属于依赖的关系。
- spring -> spring booot -> spring cloud 这样的关系。

## 3. 微服务案例（不使用SpringCloud）

> **此案例代码地址：`spring-cloud-note\spring-cloud-greenwich-sample\01-microservice-no-springcloud\`**

微服务架构的分布式系统，微服务之间通过网络通信。通过服务提供者与服务消费者来描述微服务间的调用关系

> - 服务提供者：服务的被调用方，提供调用接口的一方
> - 服务消费者：服务的调用方，依赖于其他服务的一方

以电商系统中常见的用户下单为例，用户向订单微服务发起一个购买的请求。在进行保存订单之前需要调用商品微服务查询当前商品库存，单价等信息。在这种场景下，订单微服务就是一个服务消费者，商品微服务就是一个服务提供者

![](images/20201007163820362_13440.png)

### 3.1. 准备数据库表

创建`springcloud_sample_db`数据库，案例中所涉及的表完整建表语句在项目资料`spring-cloud-note\spring-cloud-greenwich-sample\document\sql`中

用户表：

```sql
CREATE TABLE `tb_user` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
	`username` VARCHAR ( 40 ) DEFAULT NULL COMMENT '用户名',
	`password` VARCHAR ( 40 ) DEFAULT NULL COMMENT '密码',
	`age` INT ( 3 ) DEFAULT NULL COMMENT '年龄',
	`balance` DECIMAL ( 10, 2 ) DEFAULT NULL COMMENT '余额',
	`address` VARCHAR ( 80 ) DEFAULT NULL COMMENT '地址',
	PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8;
```

商品表：

```sql
CREATE TABLE `tb_product` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
	`product_name` VARCHAR ( 40 ) DEFAULT NULL COMMENT '名称',
	`status` INT ( 2 ) DEFAULT NULL COMMENT '状态',
	`price` DECIMAL ( 10, 2 ) DEFAULT NULL COMMENT '单价',
	`product_desc` VARCHAR ( 255 ) DEFAULT NULL COMMENT '描述',
	`caption` VARCHAR ( 255 ) DEFAULT NULL COMMENT '标题',
	`inventory` INT ( 11 ) DEFAULT NULL COMMENT '库存',
	PRIMARY KEY ( `id` )
) ENGINE = INNODB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8;
```

订单表：

```sql
CREATE TABLE `tb_order` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
	`user_id` INT ( 11 ) DEFAULT NULL COMMENT '用户id',
	`product_id` INT ( 11 ) DEFAULT NULL COMMENT '商品id',
	`number` INT ( 11 ) DEFAULT NULL COMMENT '数量',
	`price` DECIMAL ( 10, 2 ) DEFAULT NULL COMMENT '单价',
	`amount` DECIMAL ( 10, 2 ) DEFAULT NULL COMMENT '总额',
	`product_name` VARCHAR ( 40 ) DEFAULT NULL COMMENT '商品名',
	`username` VARCHAR ( 40 ) DEFAULT NULL COMMENT '用户名',
PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8;
```

### 3.2. 搭建环境 - 聚合父工程

创建聚合父工程`01-microservice-no-springcloud`，修改pom.xml文件引入相关依赖

```xml
<modelVersion>4.0.0</modelVersion>
<groupId>com.moon</groupId>
<artifactId>01-microservice-no-springcloud</artifactId>
<version>1.0-SNAPSHOT</version>
<name>${project.artifactId}</name>
<packaging>pom</packaging>
<description>不使用Spring Cloud的情况下实现微服务架构，分析此方法存在的问题</description>

<!-- 引入 spring boot 父工程 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.6.RELEASE</version>
</parent>

<!-- 子模块 -->
<modules>
    <module>shop-service-common</module>
    <module>shop-service-product</module>
    <module>shop-service-order</module>
</modules>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <mysql-connector-java.version>5.1.32</mysql-connector-java.version>
    <lombok.version>1.18.4</lombok.version>
</properties>

<!-- 版本控制 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql-connector-java.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>

        <!-- 子模块版本号控制 -->
        <dependency>
            <groupId>com.moon</groupId>
            <artifactId>shop-service-common</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 公共依赖 -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<!-- 项目构建部分 -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 3.3. 搭建公共模块

#### 3.3.1. 引入依赖

创建`shop-service-common`工程模块，用于存放公共的实体类和工具类。*注：后面的order、product、user模块都会依赖common模块*

```xml
<dependencies>
    <!-- lombok 实体工具类库依赖 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    <!-- springboot jpa 持久层依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```

#### 3.3.2. 创建实体类

在`shop-service-common`工程中创建 `Product` 实体类

```java
@Data
@Entity // 标识为jpa实体类
@Table(name = "tb_product") // 建立实体类和表的映射关系
public class Product {
    @Id // 声明当前私有属性为数据库表的主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 配置主键的生成策略
    private Long id;
    private String productName;
    private Integer status;
    private BigDecimal price;
    private String productDesc;
    private String caption;
    private Integer inventory;
}
```

### 3.4. 搭建商品微服务模块

#### 3.4.1. 引入依赖

创建商品微服务模块`shop-service-product`，配置相关依赖

```xml
<dependencies>
    <!-- 公共模块依赖 -->
    <dependency>
        <groupId>com.moon</groupId>
        <artifactId>shop-service-common</artifactId>
    </dependency>

    <!-- springboot web 项目依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 数据库连接 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

#### 3.4.2. 编写dao接口

在`shop-service-product`中创建 `ProductDao` 接口

```java
// 继承JPA相关接口，其中JpaSpecificationExecutor是用于复杂动态查询
public interface ProductDao extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> { }
```

#### 3.4.3. 编写service层

在`shop-service-product`中创建 `ProductService` 业务接口与其实现

```java
public interface ProductService {
    /**
     * 根据id查询
     */
    Product findById(Long id);

    /**
     * 查询全部
     */
    List<Product> findAll();

    /**
     * 保存
     */
    void save(Product product);

    /**
     * 更新
     */
    void update(Product product);

    /**
     * 删除
     */
    void delete(Long id);
}
```

```java
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Override
    public Product findById(Long id) {
        return productDao.findById(id).get();
    }

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public void save(Product product) {
        productDao.save(product);
    }

    @Override
    public void update(Product product) {
        productDao.save(product);
    }

    @Override
    public void delete(Long id) {
        productDao.deleteById(id);
    }
}
```

#### 3.4.4. 编写controller层

在`shop-service-product`中创建 `ProductController` 控制类

```java
@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @PostMapping
    public String save(@RequestBody Product product) {
        productService.save(product);
        return "保存成功";
    }

    @PutMapping
    public String update(@RequestBody Product product) {
        productService.update(product);
        return "修改成功";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "删除成功";
    }
}
```

#### 3.4.5. 编写项目配置文件

在`resources`包下创建SpringBoot项目的核心配置文件`application.yml`

```yml
server:
  port: 9001 # 项目端口
spring:
  application:
    name: shop-service-product # 服务名称
  datasource: # 数据库配置
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springcloud_sample_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
  jpa: # jpa配置
    database: MySQL
    show-sql: true
    open-in-view: true
```

#### 3.4.6. 创建项目启动类

在`shop-service-product`中创建 `ProductApplication` 控制类

```java
@SpringBootApplication(scanBasePackages = "com.moon.product")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
```

### 3.5. 搭建其他微服务

创建订单微服务模块 `shop-service-order` 与用户微服务模块 `shop-service-user`，搭建的步骤与商品微服务模块一致

### 3.6. 服务的调用

多个基础的微服务中，在用户下单时需要调用商品微服务获取商品数据，此时需要调用商品微服务提供的HTTP接口。所以需要使用http请求的相关工具类完成，如常见的`HttpClient`、`OkHttp`、Spring提供的`RestTemplate`

#### 3.6.1. RestTemplate 简介

Spring框架提供的`RestTemplate`类可用于在应用中调用rest服务，它简化了与http服务的通信方式，统一了RESTful的标准，封装了http链接，只需要传入url及返回值类型即可

RestTemplate类的设计原则与许多其他Spring 模板类(例如`JdbcTemplate`、`JmsTemplate`)相同，简化复杂的操作。`RestTemplate`默认依赖JDK提供http连接的能力（`HttpURLConnection`），如果有需要的话也可以通过`setRequestFactory`方法替换为例如 Apache HttpComponents、Netty或OkHttp等其它HTTP library。

RestTemplate类是为调用REST服务而设计的，因此它的主要方法与REST的基础紧密相连。后者是HTTP协议的方法：HEAD、GET、POST、PUT、DELETE和OPTIONS。例如，`RestTemplate`类具有`headForHeaders()`、`getForObject()`、`postForObject()`、`put()`和`delete()`等方法。

#### 3.6.2. RestTemplate 方法介绍

![](images/20201008111041867_3376.png)

#### 3.6.3. 通过RestTemplate调用微服务

在`shop-service-order`工程，创建`HttpConfig`配置类，创建`RestTemplate`实例并注册到spring容器中

```java
@Configuration
public class HttpConfig {
    @Bean("restTemplate")
    public RestTemplate createRestTemplate() {
        return new RestTemplate();
    }
}
```

在`shop-service-order`工程`OrderController`控制类中，增加创建订单的方法

```java
@RestController
@RequestMapping("order")
public class OrderController {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    // 注入HTTP请求工具类RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据商品id创建订单
     */
    @PostMapping("/{id}")
    public String createOrder(@PathVariable Long id) {
        // 通过http请求，获取商品数据
        Product product = restTemplate.getForObject("http://127.0.0.1:9001/product/" + id, Product.class);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }
}
```

启动商品与订单微服务，测试调用接口

#### 3.6.4. 硬编码存在的问题

上面示例已经可以通过`RestTemplate`调用商品微服务的RESTFul API接口，但把提供者的网络地址（ip，端口）等硬编码到了代码中，这种做法存在许多问题：

- 应用场景有局限
- 无法动态调整

**解决方法：通过注册中心动态的对服务注册和服务发现**

# Eureka 服务注册和发现

> **Eureka案例代码地址：`spring-cloud-note\spring-cloud-greenwich-sample\02-springcloud-eureka\`**

## 1. 微服务的注册中心

注册中心相当于微服务架构中的“通讯录”，它记录了服务和服务地址的映射关系。在分布式架构中，服务会注册到这里，当服务需要调用其它服务时，就这里找到服务的地址，进行调用。

![](images/20201008120245097_22177.png)

### 1.1. 注册中心的主要作用

服务注册中心（下称注册中心）是微服务架构非常重要的一个组件，在微服务架构里主要起到了协调者的一个作用。注册中心一般包含如下几个功能：

1. 服务发现：
    - 服务注册/反注册：保存服务提供者和服务调用者的信息
    - 服务订阅/取消订阅：服务调用者订阅服务提供者的信息，最好有实时推送的功能
    - 服务路由（可选）：具有筛选整合服务提供者的能力。
2. 服务配置：
    - 配置订阅：服务提供者和服务调用者订阅微服务相关的配置
    - 配置下发：主动将配置推送给服务提供者和服务调用者
3. 服务健康检测
    - 检测服务提供者的健康情况

### 1.2. 常见的注册中心

- **Zookeeper**：它是一个分布式服务框架，是Apache Hadoop 的一个子项目，它主要是用来解决分布式应用中经常遇到的一些数据管理问题，如：统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等。简单来说<font color=red>**zookeeper=文件系统+监听通知机制**</font>。
- **Eureka**：是使用Java语言开发的，基于Restful Api开发的服务注册与发现组件，Springcloud Netflix中的重要组
- **Consul**：是由HashiCorp基于Go语言开发的支持多数据中心分布式高可用的服务发布和注册服务软件，采用Raft算法保证服务的一致性，且支持健康检查。
- **Nacos**：是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。简单来说<font color=red>**Nacos 就是注册中心 + 配置中心的组合**</font>，提供简单易用的特性集，解决微服务开发必会涉及到的服务注册与发现，服务配置，服务管理等问题。Nacos 还是 Spring Cloud Alibaba 组件之一

选择什么类型的服务注册与发现组件可以根据自身项目要求决定。常见的注册中心总结如下：

|   组件名   | 语言 | CAP | 一致性算法 | 服务健康检查 | 对外暴露接口 |
| --------- | ---- | --- | --------- | ----------- | ----------- |
| Eureka    | Java | AP  | 无        | 可配支持     | HTTP        |
| Consul    | go   | CP  | Raft      | 支持        | HTTP/DNS    |
| Zookeeper | Java | CP  | Paxos     | 支持        | 客户端      |
| Nacos     | Java | AP  | Raft      | 支持        | HTTP        |

### 1.3. 微服务的注册与发现流程图

![](images/20190501091354200_14049.jpg)

由上图可以看出：

1. 服务提供者将服务注册到注册中心
2. 服务消费者通过注册中心查找服务
3. 查找到服务后进行调用（这里就是无需硬编码url的解决方案）
4. 服务的消费者与服务注册中心保持心跳连接，一旦服务提供者的地址发生变更时，注册中心会通知服务消费者

## 2. Eureka 注册中心

Eureka是Netflix开发的服务发现框架，SpringCloud将它集成在自己的子项目spring-cloud-netflix中，实现SpringCloud的服务发现功能。Spring Cloud提供了多种注册中心的支持，如：Eureka、ZooKeeper等。推荐使用Eureka。

### 2.1. Eureka 的基本架构

![](images/20201008123337857_27461.png)

上图简要描述了Eureka的基本架构，由3个角色组成：

- `Eureka Server`：提供服务注册和发现
- `Service Provider`：服务提供者，将自身服务注册到Eureka，使服务消费方能够找到
- `Service Consumer`：服务消费者，从Eureka获取注册服务列表，消费服务

### 2.2. Eureka 的交互流程与原理

![](images/20201008123931375_6484.png)

上图是Eureka官方的架构图，大致描述了Eureka集群的工作过程

- Application Service 相当于服务提供者，Application Client 相当于服务消费者
- Make Remote Call 可以简单理解为调用RESTful API
- us-east-1c、us-east-1d等都是zone，它们都属于us-east-1这个region

<font color=red>**Eureka包含两个组件：`Eureka Server` 和 `Eureka Client`**</font>，作用如下：

- Eureka Client是一个Java客户端，用于简化与Eureka Server的交互。客户端同时也就别一个内置的、使用轮询(round-robin)负载算法的负载均衡器。
- Eureka Server提供服务发现的能力，各个微服务节点启动时，会通过Eureka Client向Eureka Server进行注册自己的信息（例如网络信息），Eureka Server服务注册表中将会存储所有可用服务节点的信息到内存中，服务节点的信息可以在管理平台界面中直观的看到
- 各个微服务节点启动后，会周期性地向Eureka Server发送心跳（默认周期为30秒）以续约自己的信息。如果Eureka Server在多个心跳周期内没有接收到某个微服务节点的心跳，Eureka Server将会注销该微服务节点（即把这个服务节点从服务注册表中移除，默认90秒）
- 每个Eureka Server同时也是Eureka Client，多个Eureka Server之间通过复制的方式完成服务注册表数据的同步
- Eureka Client还提供了客户端缓存机制，会缓存Eureka Server中的信息。即使所有的Eureka Server节点都宕掉，服务消费者依然可以使用缓存中的信息找到服务提供者。

Eureka通过心跳检查、客户端缓存等机制，确保了系统的高可用性、灵活性和可伸缩性。

## 3. Eureka 注册中心(单机版)使用示例

> 案例代码：spring-cloud-note\spring-cloud-greenwich-sample\02-springcloud-eureka\
>
> 沿用上面`01-microservice-no-springcloud`项目的代码

### 3.1. 引入 Spring Cloud 依赖

修改聚合工程pom.xml文件，增加spring cloud Greenwich 版本的依赖

```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud Greenwich 版本的依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwich.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3.2. 搭建Eureka注册中心

#### 3.2.1. 搭建Eureka服务中心

1. 创建`shop-server-eureka`子模块，引入eureka服务端的依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

2. 配置`application.yml`文件

```yml
server:
  port: 8001 # 项目端口
spring:
  application:
    name: shop-server-eureka # 服务名称
# eureka配置部分
eureka:
  instance:
    hostname: localhost
  client:
    # 是否将自己注册到Eureka服务中，如果不是高可用部署，则不需要注册（注：也可以写成驼峰命名registerWithEureka）
    register-with-eureka: false
    # 服务发现，是否从Eureka中获取注册信息
    fetch-registry: false
    service-url:
      # Eureka客户端与Eureka服务端的交互地址，高可用状态配置对方的地址，单机状态配置自己（如果不配置则默认本机8761端口）
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

配置说明：

- `registerWithEureka`：是否将自己注册到Eureka服务中，非高可用部署的情况，本身就是无需注册
- `fetchRegistry`：服务发现，是否从Eureka中获取注册信息
- `service-url.defaultZone`：Eureka客户端与Eureka服务端进行交互的地址

> 注：驼峰命名`registerWithEureka`与分隔符命名`register-with-eureka`的效果一样

3. 创建启动类`EurekaServerApplication`，使用`@EnableEurekaServer`注解标识开启Eureka服务端

```java
@SpringBootApplication
// 标识开启Eureka Server端
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

#### 3.2.2. 服务注册中心管理后台

访问`http://localhost:8761`即可进入EurekaServer内置的管理控制台

![](images/20201009103306998_27461.png)

### 3.3. 服务注册到Eureka注册中心

#### 3.3.1. 商品服务注册

1. 在`shop-service-product`工程的pom.xml文件增加eureka client的相关坐标

```xml
<!-- 引入 Eureka Client 端依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

2. 配置application.yml文件，添加Eureka Server的主机地址

```yml
# Eureka 客户端配置
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    instance-id: ${spring.application.name}:${server.port}  # 指定实例id
  client:
    register-with-eureka: true  # 服务注册开关
    fetch-registry: true  # 服务发现开关
    # Eureka server 地址，多个eureka server之间用,隔开
    service-url:
      defaultZone: http://localhost:8001/eureka/
```

3. 修改工程的启动类添加服务注册的注解`@EnableDiscoveryClient`或者 `@EnableEurekaClient`

```java
@SpringBootApplication(scanBasePackages = "com.moon.product")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
/* 从Spring Cloud Edgware版本开始，以下两个注解均可以省略不写。只需加上相关依赖与相应配置，即可注册服务 */
// @EnableEurekaClient // 基于spring-cloud-netflix依赖，只能为eureka作用
// @EnableDiscoveryClient // 基于spring cloud自身的依赖，可以作用于除了eureka之外的注册中心
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
```

> <font color=red>**注：从Spring Cloud Edgware版本开始，`@EnableDiscoveryClient` 或 `@EnableEurekaClient` 可省略。只需加上相关依赖，并进行相应配置，即可将微服务注册到服务发现组件上。**</font>

#### 3.3.2. 订单服务注册

和商品微服务一样，只需要引入坐标依赖，在工程的 `application.yml` 中添加Eureka Server的主机地址即可

#### 3.3.3. @EnableDiscoveryClient 与 @EnableEurekaClient 的区别

两个注解的用法上基本一致。

1. `@EnableDiscoveryClient`注解是基于spring-cloud-commons依赖，并且在classpath中实现；
2. `@EnableEurekaClient`注解是基于spring-cloud-netflix依赖，只能为eureka作用；
3. 更简单来说，就是如果选用的注册中心是eureka，那么就推荐`@EnableEurekaClient`，如果是其他的注册中心，那么推荐使用`@EnableDiscoveryClient`。

如果你的classpath中添加了eureka，则它们的作用是一样的。

注解`@EnableEurekaClient`上有`@EnableDiscoveryClient`注解，可以说基本就是`@EnableEurekaClient`有`@EnableDiscoveryClient`的功能，另外上面的注释中提到，其实`@EnableEurekaClient`注解就是一种方便使用eureka的注解而已，可以说使用其他的注册中心后，都可以使用`@EnableDiscoveryClient`注解，但是使用`@EnableEurekaClient`的情景，就是在服务采用eureka作为注册中心的时候，使用场景较为单一

## 4. Eureka的自我保护模式

微服务第一次注册成功之后，每30秒会发送一次心跳将服务的实例信息注册到注册中心。通知 Eureka Server 该实例仍然存在。默认情况下，如果Eureka Server在一定时间内没有接收到某个微服务实例的心跳，Eureka Server将会注销该实例（默认90秒）。但是当网络分区故障发生时，微服务与Eureka Server之间无法正常通信，这就可能变得非常危险了。因为微服务本身是健康的，此时本不应该注销这个微服务。

Eureka Server通过“自我保护模式”来解决这个问题。Eureka Server在运行期间，会统计心跳失败的比例在15分钟之内是否低于85%，如果出现低于的情况（在单机调试的时候很容易满足，实际在生产环境上通常是由于网络不稳定导致），Eureka Server节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。一旦进入该模式，Eureka Server就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务），同时提示以下警告。

![](images/20201009152105325_27258.png)

验证完自我保护机制开启后，并不会马上呈现到web上，而是默认需等待5分钟（可以通过`eureka.server.wait-time-in-ms-when-sync-empty` 配置），即 5 分钟后你会看到下面的提示信息：

当网络故障恢复后，该Eureka Server节点会自动退出自我保护模式。自我保护模式是一种对网络异常的安全保护措施。使用自我保护模式，而已让Eureka集群更加的健壮、稳定。

**一般进入自我保护模式，无需处理**。如果需要禁用自我保护模式，只需要在eureka server配置文件中添加配置：

```yml
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: false # 禁用自我保护模式
```

> 提示：如果禁用自我保护模式，在网络通信故障下会出现问题。

![](images/20201009153015124_9293.png)

## 5. Eureka中的元数据

### 5.1. 概念

Eureka的元数据有两种：**标准元数据和自定义元数据**

- **标准元数据**：主机名、IP地址、端口号、状态页和健康检查等信息，这些信息都会被发布在服务注册表中，用于服务之间的调用
- **自定义元数据**：可以使用`eureka.instance.metadata-map`配置，符合`KEY/VALUE`的存储格式。这些元数据可以在远程客户端中访问

在程序中可以使用 `org.springframework.cloud.client.discovery.DiscoveryClient` 获取指定微服务的所有元数据信息

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class EurekaTest {
    @Autowired
    private DiscoveryClient discoveryClient;

    /* Eureka元数据 */
    @Test
    public void eurekaMetadataTest() {
        // 调用discoveryClient的getInstances方法，根据微服务名称从注册中心获取相关的元数据信息
        List<ServiceInstance> instances = discoveryClient.getInstances("shop-service-product");
        // 循环所有元数据
        for (ServiceInstance instance : instances) {
            System.out.println("hostname: " + instance.getHost());
            System.out.println("instanceId: " + instance.getInstanceId());
            System.out.println("port: " + instance.getPort());
            System.out.println("scheme: " + instance.getScheme());
            System.out.println("serviceId: " + instance.getServiceId());
            System.out.println("uri: " + instance.getUri());
            System.out.println("================== 分隔线 =====================");
            // 循环
            for (Map.Entry<String, String> entry : instance.getMetadata().entrySet()) {
                System.out.println("key: " + entry.getKey() + " === value: " + entry.getValue());
            }
            System.out.println("================== 分隔线 =====================");
        }
    }
}
```

### 5.2. 通过Eureka的元数据实现服务调用

修改`shop-service-order`工程的`OrderController`，注入`DiscoveryClient`对象，获取商品服务的url，进行远程调用

```java
@RestController
@RequestMapping("order")
public class OrderController {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    // 注入HTTP请求工具类RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    // 注入注册中心服务对象
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 根据商品id创建订单
     */
    @PostMapping("/{id}")
    public String createOrder(@PathVariable Long id) {
        // 调用discoveryClient方法，根据服务名称获取所有的元数据
        List<ServiceInstance> instances = discoveryClient.getInstances("shop-service-product");
        // 因为当前商品服务只有一个，所以直接获取唯一的服务实例即可
        ServiceInstance productService = instances.get(0);
        // 拼接请求url
        String uri = String.format("http://%s:%s/product/%d", productService.getHost(), productService.getPort(), id);
        // 通过http请求，获取商品数据
        Product product = restTemplate.getForObject(uri, Product.class);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }
}
```

## 6. Eureka Server 高可用集群


## 7. Eureka中的常见问题














## 8. ======待删处理==========

### 8.1. 创建Eureka Server注册中心

参考案例，D:\code\IdeaProjects\spring-cloud-project，下面配置eureka server以此工程为案例

#### 8.1.1. 父工程

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

#### 8.1.2. 创建eureka-server工程，导入依赖（注：需要导入Spring Cloud的管理依赖。）

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

#### 8.1.3. 编写spring-cloud工程的启动类，`@EnableEurekaServer`注解开启Eureka Server的功能

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

#### 8.1.4. 编写application.yml配置文件

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

### 8.2. 将微服务注册到Eureka

将案例中的商品微服务注册到Eureka服务中

#### 8.2.1. 第一步：修改pom文件，引入Spring Cloud的管理依赖以及eureka服务依赖。

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

#### 8.2.2. 第二步，修改application.yml配置文件

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

#### 8.2.3. 第三步，修改启动类，增加`@EnableDiscoveryClient`或`@EnableEurekaClient`注解

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

#### 8.2.4. 启动测试

开启springcloud-microservice-item与springcloud-microservice-eureka工程，已经将微服务注册到Eureka server中

Staus为UP（在线），说明已经注册成功

![注册服务](images/注册服务_1537750423_7401.png)


### 8.3. 订单系统从Eureka发现服务

订单系统中是将商品微服务的地址进行了硬编码，将商品服务注册到Eureka后，现只需要从Eureka中发现服务即可

#### 8.3.1. 第一步，在订单系统中添加依赖

因为在父工程中配置了`spring-cloud-dependencies`的spring clound版本锁定，不需要指定版本

```xml
<!-- 导入Eureka服务的依赖 -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

#### 8.3.2. 第二步，修改application.yml配置文件

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

#### 8.3.3. 第三步，修改ItemService的实现逻辑

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

#### 8.3.4. 第四步，在启动类中添加`@EnableDiscoveryClient`或`@EnableEurekaClient`注解

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

#### 8.3.5. 第五步，启动测试

测试url：http://127.0.0.1:8082/order/59193738268961441

可以获取数据，但响应数据变成xml结构

#### 8.3.6. 解决响应变成xml的问题

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

### 8.4. 为Eureka添加用户认证

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

#### 8.4.1. 服务注册时设置账户信息

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


### 8.5. Eureka的高可用

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

### 8.6. 将服务注册到Eureka集群

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

### 8.7. 指定服务的IP地址

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

### 8.8. 指定实例id

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

# Ribbon

## 1. 使用Ribbon实现负载均衡

在Spring Cloud中推荐使用Ribbon来实现负载均衡。即使用客户端根据服务的负载情况去选择空闲或者访问压力小的服务

### 1.1. Ribbon简介

负载均衡分为服务端负载均衡和客户端负载均衡，SpringCloud Ribbon是基于客户端的负载均衡工具。

Ribbon是Netflix发布开源项目的负载均衡器，有助于控制HTTP和TCP客户端的行为。为Ribbon配置服务提供者地址列表后，Ribbon基于某种负载均衡得法，自动地帮助服务消费者去请求。主要功能是提供客户端的软件负载均衡算法，如轮询、随机等，它将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。也可为Ribbon实现自定义的负载均衡算法

### 1.2. Ribbon架构

![Ribbon架构](images/ribbon架构_1538697723_25876.jpg)

### 1.3. Ribbon使用案例
#### 1.3.1. 为itcast-microservice-order增加ribbon依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```

其实该依赖是可以省略的，因为spring-cloud-starter-eureka-server中已经包含了spring-cloud-starter-ribbon

> *注：在学成项目实践中，使用spring cloud的版本是Finchley.SR1，如果在微服务中引入spring-cloud-starter-ribbon会提示找不到版本号。需要将其修改为spring-cloud-starter-netflix-ribbon才能找到依赖的版本号*

#### 1.3.2. RestTemplate设置@LoadBalanced注解

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

### 1.4. Ribbon 源码解析
#### 1.4.1. 实现分析（待整理）

#### 1.4.2. 总结

Ribbon 的负载均衡主要是通 LoadBalancerClient 来实现，而 LoadBalancerClient 具体是交给 ILoadBalancer 来处理，ILoadBalancer 通过配置 IRule、IPing 等，向 EurekaClient 获取注册列表信息，默认每10秒向 EurekaClient 发送一次“ping” 请求，用于检查是否需要更新服务的注册列表信息。最后，在得到服务注册列表令牌后，ILoadBalancer 根据 IRule 的策略进行负载均衡。

在 RestTemplate 加上 `@LoadBalanced` 注解后，在远程调度时能够负载均衡，主要是维护了一个被 `@LoadBalanced` 注解的 RestTemplate 列表，并给该列表中的 RestTemplate 对象添加了拦截器。在拦截器的方法中，将远程调度方法交给了 Ribbon 的负载均衡器 LoadBalancerClient 去处理，从而达到了负载均衡的目的。







