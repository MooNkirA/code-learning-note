## 1. Nacos

### 1.1. Nacos 简介

Nacos(全称：Name and Config Service) 致力于发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，快速实现动态服务发现、服务配置、服务元数据及流量管理。更敏捷和容易地构建、交付和管理微服务平台。Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

Nacos 的核心功能 = 服务注册 + 动态配置，也可以理解为 Nacos = Spring Cloud Eureka + Spring Cloud Config

> 官网：https://nacos.io/zh-cn/index.html

### 1.2. Nacos 的关键特性

Nacos 支持几乎所有主流类型的“服务”的发现、配置和管理：

- Kubernetes Service
- gRPC & Dubbo RPC Service
- Spring Cloud RESTful Service

Nacos 的关键特性包括:

- **服务发现和服务健康监测**

Nacos 支持基于 DNS 和基于 RPC 的服务发现。服务提供者使用 [原生SDK](https://nacos.io/zh-cn/docs/v2/guide/user/sdk.html)、[OpenAPI](https://nacos.io/zh-cn/docs/v2/guide/user/open-api.html)、或一个[独立的Agent TODO](https://nacos.io/zh-cn/docs/v2/guide/user/other-language.html)注册 Service 后，服务消费者可以使用[DNS TODO](https://nacos.io/zh-cn/docs/v2/xx) 或[HTTP&API](https://nacos.io/zh-cn/docs/v2/guide/user/open-api.html)查找和发现服务。

Nacos 提供对服务的实时的健康检查，阻止向不健康的主机或服务实例发送请求。Nacos 支持传输层 (PING 或 TCP)和应用层 (如 HTTP、MySQL、用户自定义）的健康检查。 对于复杂的云环境和网络拓扑环境中（如 VPC、边缘网络等）服务的健康检查，Nacos 提供了 agent 上报模式和服务端主动检测2种健康检查模式。Nacos 还提供了统一的健康检查仪表盘，帮助您根据健康状态管理服务的可用性及流量。

- **动态配置服务**

动态配置服务可以让您以中心化、外部化和动态化的方式管理所有环境的应用配置和服务配置。

动态配置消除了配置变更时重新部署应用和服务的需要，让配置管理变得更加高效和敏捷。

配置中心化管理让实现无状态服务变得更简单，让服务按需弹性扩展变得更容易。

Nacos 提供了一个简洁易用的UI ([控制台样例 Demo](http://console.nacos.io/nacos/index.html)) 帮助您管理所有的服务和应用的配置。Nacos 还提供包括配置版本跟踪、金丝雀发布、一键回滚配置以及客户端配置更新状态跟踪在内的一系列开箱即用的配置管理特性，帮助您更安全地在生产环境中管理配置变更和降低配置变更带来的风险。

- **动态 DNS 服务**

动态 DNS 服务支持权重路由，让您更容易地实现中间层负载均衡、更灵活的路由策略、流量控制以及数据中心内网的简单DNS解析服务。动态DNS服务还能让您更容易地实现以 DNS 协议为基础的服务发现，以帮助您消除耦合到厂商私有服务发现 API 上的风险。

Nacos 提供了一些简单的 [DNS APIs TODO](https://nacos.io/zh-cn/docs/v2/xx) 帮助您管理服务的关联域名和可用的 IP:PORT 列表.

- **服务及其元数据管理**

Nacos 能让您从微服务平台建设的视角管理数据中心的所有服务及元数据，包括管理服务的描述、生命周期、服务的静态依赖分析、服务的健康状态、服务的流量管理、路由及安全策略、服务的 SLA 以及最首要的 metrics 统计数据。

### 1.3. Nacos 架构

#### 1.3.1. 基本架构及概念

![](images/492972018248778.jpeg)

**服务 (Service)**

服务是指一个或一组软件功能（例如特定信息的检索或一组操作的执行），其目的是不同的客户端可以为不同的目的重用（例如通过跨进程的网络调用）。Nacos 支持主流的服务生态，如 Kubernetes Service、gRPC|Dubbo RPC Service 或者 Spring Cloud RESTful Service。

**服务注册中心 (Service Registry)**

服务注册中心，它是服务，其实例及元数据的数据库。服务实例在启动时注册到服务注册表，并在关闭时注销。服务和路由器的客户端查询服务注册表以查找服务的可用实例。服务注册中心可能会调用服务实例的健康检查 API 来验证它是否能够处理请求。

**服务元数据 (Service Metadata)**

服务元数据是指包括服务端点(endpoints)、服务标签、服务版本号、服务实例权重、路由规则、安全策略等描述服务的数据。

**服务提供方 (Service Provider)**

是指提供可复用和可调用服务的应用方。

**服务消费方 (Service Consumer)**

是指会发起对某个服务调用的应用方。

**配置 (Configuration)**

在系统开发过程中通常会将一些需要变更的参数、变量等从代码中分离出来独立管理，以独立的配置文件的形式存在。目的是让静态的系统工件或者交付物（如 WAR，JAR 包等）更好地和实际的物理运行环境进行适配。配置管理一般包含在系统部署的过程中，由系统管理员或者运维人员完成这个步骤。配置变更是调整系统运行时的行为的有效手段之一。

**配置管理 (Configuration Management)**

在数据中心中，系统中所有配置的编辑、存储、分发、变更管理、历史版本管理、变更审计等所有与配置相关的活动统称为配置管理。

**名字服务 (Naming Service)**

提供分布式系统中所有对象(Object)、实体(Entity)的“名字”到关联的元数据之间的映射管理服务，例如 ServiceName -> Endpoints Info, Distributed Lock Name -> Lock Owner/Status Info, DNS Domain Name -> IP List, 服务发现和 DNS 就是名字服务的2大场景。

**配置服务 (Configuration Service)**

在服务或者应用运行过程中，提供动态配置或者元数据以及配置管理的服务提供者。

#### 1.3.2. 逻辑架构及其组件介绍

![](images/17322018230352.png)

- 服务管理：实现服务CRUD，域名CRUD，服务健康状态检查，服务权重管理等功能
- 配置管理：实现配置管CRUD，版本管理，灰度管理，监听管理，推送轨迹，聚合数据等功能
- 元数据管理：提供元数据CURD 和打标能力
- 插件机制：实现三个模块可分可合能力，实现扩展点SPI机制
- 事件机制：实现异步化事件通知，sdk数据变化异步通知等逻辑
- 日志模块：管理日志分类，日志级别，日志可移植性（尤其避免冲突），日志格式，异常码+帮助文档
- 回调机制：sdk通知数据，通过统一的模式回调用户处理。接口和数据结构需要具备可扩展性
- 寻址模式：解决ip，域名，nameserver、广播等多种寻址模式，需要可扩展
- 推送通道：解决server与存储、server间、server与sdk间推送性能问题
- 容量管理：管理每个租户，分组下的容量，防止存储被写爆，影响服务可用性
- 流量管理：按照租户，分组等多个维度对请求频率，长链接个数，报文大小，请求流控进行控制
- 缓存机制：容灾目录，本地缓存，server缓存机制。容灾目录使用需要工具
- 启动模式：按照单机模式，配置模式，服务模式，dns模式，或者all模式，启动不同的程序+UI
- 一致性协议：解决不同数据，不同一致性要求情况下，不同一致性机制
- 存储模块：解决数据持久化、非持久化存储，解决数据分片问题
- Nameserver：解决namespace到clusterid的路由问题，解决用户环境与nacos物理环境映射问题
- CMDB：解决元数据存储，与三方cmdb系统对接问题，解决应用，人，资源关系
- Metrics：暴露标准metrics数据，方便与三方监控系统打通
- Trace：暴露标准trace，方便与SLA系统打通，日志白平化，推送轨迹等能力，并且可以和计量计费系统打通
- 接入管理：相当于阿里云开通服务，分配身份、容量、权限过程
- 用户管理：解决用户管理，登录，sso等问题
- 权限管理：解决身份识别，访问控制，角色管理等问题
- 审计系统：扩展接口方便与不同公司审计系统打通
- 通知系统：核心数据变更，或者操作，方便通过SMS系统打通，通知到对应人数据变更
- OpenAPI：暴露标准Rest风格HTTP接口，简单易用，方便多语言集成
- Console：易用控制台，做服务管理、配置管理等操作
- SDK：多语言sdk
- Agent：dns-f类似模式，或者与mesh等方案集成
- CLI：命令行对产品进行轻量化管理，像git一样好用

### 1.4. Nacos 相关资源

- [Nacos开发团队 - Nacos 架构 & 原理（语雀）](https://www.yuque.com/nacos/ebook)

## 2. Nacos 快速开始

### 2.1. 版本选择

Nacos 1.X 是老版本，将来会停止维护。建议使用2.X版本。

在Nacos的[release notes](https://github.com/alibaba/nacos/releases)及[博客](https://nacos.io/zh-cn/blog/index.html)中找到每个版本支持的功能的介绍，当前推荐的稳定版本为2.1.1。（*更新于2023.03.11*）

### 2.2. Nacos 服务端搭建

> Nacos 服务端搭建只需要下载官方安装包，github 仓库下载地址：https://github.com/alibaba/nacos/releases
>
> Tips: Github 在国内下载速度比较慢，可以到『Gitee 极速下载/Nacos』下载安装包或者源码。地址：https://gitee.com/mirrors/Nacos

#### 2.2.1. 预备环境准备

Nacos 依赖 Java 环境来运行。如果是从代码开始构建并运行 Nacos，还需要为此配置 Maven 环境，请确保是在以下版本环境中安装使用:

1. 64 bit OS，支持 Linux/Unix/Mac/Windows，推荐选用 Linux/Unix/Mac。
2. 64 bit JDK 1.8+
3. Maven 3.2.x+

#### 2.2.2. 下载源码或者安装包

可以通过源码和发行包两种方式来获取 Nacos。

**从 Github 上下载源码方式**

```bash
git clone https://github.com/alibaba/nacos.git
cd nacos/
mvn -Prelease-nacos -Dmaven.test.skip=true clean install -U  
ls -al distribution/target/

// change the $version to your actual path
cd distribution/target/nacos-server-$version/nacos/bin
```

**下载编译后压缩包方式**

可以直接从官方仓库（github）下载 `nacos-server-x.x.x.zip` 包（*笔记编写时最新版本是2.0.3*）。下载后压缩，进行bin目录运行即可

```bash
unzip nacos-server-$version.zip 或者 tar -xvf nacos-server-$version.tar.gz
cd nacos/bin
```

> 注：命令中的`$version`为nacos的版本号

#### 2.2.3. 启动服务器

**Windows 环境下的启动命令：**

```bash
startup.cmd -m standalone
```

> 注：命令参数 `standalone` 代表着单机模式运行，非集群模式。*在2.0.3版本，如果直接点击startup.cmd脚本启动会报错，必须通过命令行方式指定参数运行*

**Linux/Unix/Mac 环境下的启动命令：**

```shell
sh startup.sh -m standalone
```

如果使用的是ubuntu系统，或者运行脚本报错提示[[符号找不到，可尝试如下运行：

```bash
bash startup.sh -m standalone
```

#### 2.2.4. 关闭服务器

**Windows 环境**

```bash
shutdown.cmd
```

或者双击shutdown.cmd运行文件。

**Linux/Unix/Mac 环境**

```shell
sh shutdown.sh
```

### 2.3. 访问 nacos 服务

打开浏览器输入 `http://127.0.0.1:8848/nacos`，即可访问服务， 默认账号和密码是nacos/nacos

### 2.4. 服务提供者注册到 Nacos（Spring Cloud 项目）

> 前提条件：需要先下载 Nacos 并启动 Nacos server。

以下示例是在 Spring Cloud 项目中启动 Nacos 的服务发现功能。

![](images/20220101184857948_362.png)

修改商品模块的代码，将其注册到nacos服务上。

- 修改 pom.xml 添加 nacos 注册中心客户端依赖

```xml
<!-- nacos客户端 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

> 注意：版本 2.1.x.RELEASE 对应的是 Spring Boot 2.1.x 版本。版本 2.0.x.RELEASE 对应的是 Spring Boot 2.0.x 版本，版本 1.5.x.RELEASE 对应的是 Spring Boot 1.5.x 版本。

- 在项目的 application.yml/application.properties 配置文件中，配置 Nacos server 的地址。

```yml
server:
  port: 8081
spring:
  application:
    name: service-product
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 配置 Nacos server 的地址
```

```properties
server.port=8070
spring.application.name=service-provider

spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

- 在项目的启动类或者配置类中，通过 Spring Cloud 原生注解 `@EnableDiscoveryClient` 开启服务注册发现功能：

```java
@SpringBootApplication
// Spring Cloud 原生注解 @EnableDiscoveryClient 开启服务注册发现功能
@EnableDiscoveryClient
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
```

通过以上步骤后，即配置了服务提供者，从而服务提供者可以通过 Nacos 的服务注册发现功能将其服务注册到 Nacos server 上。服务注册成功后，启动服务能在 nacos 服务端的控制面板中看到相应注册的微服务。

![](images/20220101190918925_30556.png)

### 2.5. 服务消费者注册到 Nacos（Spring Cloud 项目）

> 服务消费者注册到 Nacos 服务端的步骤与服务提供者一样。

Spring Cloud 的 `DiscoveryClient` 接口是专门负责服务注册和发现的，可以通过它获取到注册到注册中心的所有服务实例。

修改服务消费者（订单服务），通过nacos注册中心获取到服务提供者（商品服务）的地址与端口，再通过 `RestTemplate` 进行服务调用。

```java
@Autowired
private DiscoveryClient discoveryClient;

@GetMapping("/add/serviceInstance/{pid}")
public Order serviceInstancedAdd(@PathVariable("pid") Long pid) {
    log.info("接收到ID为{}的商品下单请求", pid);

    /*
     * 通过 DiscoveryClient 的 getInstances 方法可以获取到指定的服务实例对象（返回值是集合，因为服务会有集群的情况）。
     * 通过服务实例对象，可获取服务的地址和端口
     */
    List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
    // 此处测试只有一个商品微服务，所以直接获取服务列表第一个是没有问题，但如果是集群服务的话，这种调用方式显然有问题
    ServiceInstance serviceInstance = instances.get(0);
    String host = serviceInstance.getHost(); // 地址
    int port = serviceInstance.getPort(); // 端口
    Product product = restTemplate
            .getForObject(String.format("http://%s:%d/product/%s", host, port, pid), Product.class);

    Order order = new Order();
    if (product != null) {
        order.setProductId(pid);
        order.setNumber(1);
        order.setPrice(product.getPrice());
        order.setAmount(product.getPrice());
        order.setProductName(product.getProductName());
        // 暂写死用户
        order.setUserId(Long.parseLong("0"));
        order.setUsername("测试用户");

        // 创建订单
        orderService.createOrder(order);
    }

    return order;
}
```

启动服务提供者（商品服务）和服务消费者（订单服务），观察nacos的控制面板中是否已注册相应的服务，然后通过访问消费者服务验证调用是否成功

## 3. 基于 Ribbon 实现负载均衡

> Ribbon 是 Spring Cloud 的一个组件，用于实现服务调用的负载均衡

Nacos Discovery Starter 默认集成了 Ribbon ，所以对于使用了 Ribbon 做负载均衡的组件，可以直接使用 Nacos 的服务发现。具体实现步骤如下：

- 在配置类中生成 `RestTemplate` 实例方法上添加 `@LoadBalanced` 注解，使得 `RestTemplate` 接入 Ribbon

```java
@Bean
@LoadBalanced // @LoadBalanced 注解让 RestTemplate 接入 Ribbon 实现负载均衡
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

- 修改服务调用方法，直接通过在nacos上注册的服务名称进行调用

```java
/* RestTemplate 接入 Ribbon 后，将原来的url+端口替换成注册中心上的服务名称 */
Product product = restTemplate
        .getForObject(String.format("http://service-product/product/%s", pid), Product.class);
```

- 通过修改 application.yml 配置项`被调用的服务名称.ribbon.NFLoadBalancerRuleClassName`，可以调整 Ribbon 的负载均衡策略。*如果不配置，则默认的负载均衡策略是轮询*

```yml
# 配置 Ribbon 的负载均衡策略
service-product: # 调用的提供者的名称
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 随机选择一个server
```

Ribbon 内置了多种负载均衡策略，内部负载均衡的顶级接口为 `com.netflix.loadbalancer.IRule`，具体的负载策略如下所示:

![](images/20220101224145518_11177.png)

## 4. 基于 Feign 实现服务调用

### 4.1. Feign 概述

Feign 是 Spring Cloud 提供的一个声明式、模板化的伪 Http 客户端，它使得调用远程服务就像调用本地服务一样简单，只需要创建一个接口并添加一个注解即可。

> 注：Feign 的详细使用文档，详见[《Spring-Cloud-OpenFeign.md》](/分布式微服务/SpringCloud/Spring-Cloud-OpenFeign)

Nacos 很好的兼容了 Feign，Feign 默认集成了 Ribbon，所以在 Nacos 下使用 Fegin 默认就实现了负载均衡的效果。

### 4.2. Feign 与 Nacos 整合实现服务调用

- 修改服务消费者（订单模块），添加 Fegin 的依赖

```xml
<!-- openfeign 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

- 在项目启动类或者配置类上，添加 `@EnableFeignClients` 注解，开启 Feign 功能

```java
@SpringBootApplication
// Spring Cloud 原生注解 @EnableDiscoveryClient 开启服务注册发现功能
@EnableDiscoveryClient
// 开启fegin的客户端
@EnableFeignClients
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

- 创建一个 Feign 的服务接口，使用 `@FeignClient` 注解的接口，Feign 会生成此接口的代理对象。

```java
/*
 * @FeignClient 注解，用于标识当前接口为Feign调用微服务的核心接口
 *  value/name属性：指定需要调用的服务提供者注册的名称
 */
@FeignClient("service-product") // 或者：@FeignClient(name = "待调用的服务注册名称")
public interface ProductFeignClient {
    /*
     * 创建需要调用的微服务接口方法，SpringCloud 对 Feign 进行了增强兼容了 SpringMVC 的注解
     * @FeignClient 的 value值 + @RequestMapping 的 value 值，相当于服务的请求地址："http://service-product/product/" + pid
     *  在使用的两个注意点：
     *  1. FeignClient 接口有参数时，必须在参数加@PathVariable("XXX")和@RequestParam("XXX")注解，并且必须要指定对应的参数值（原来SpringMVC是可以省略）
     *  2. feignClient 返回值为复杂对象时，其对象类型必须有无参构造函数
     *  3. 方法的名称不需要与被调用的服务接口名称一致
     */
    @GetMapping("/product/{pid}")
    Product findById(@PathVariable("pid") Long id);
}
```

> 注：Feign 定义调用的服务接口的方法是根据，`@FeignClient` 的 `value` 值 + `@RequestMapping` 的 `value` 值，相当于服务的请求地址。

- 修改服务消费者，通过 Feign 的接口调用远程服务。

```java
@RestController
@RequestMapping("order")
@Slf4j
public class OrderFeignController {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderService orderService;

    /**
     * 下单请求方法，通过 Feign 实现负载均衡的服务调用
     *
     * @param pid
     * @return
     */
    @GetMapping("/feign/{pid}")
    public Order feignLoadBalanced(@PathVariable("pid") Long pid) {
        log.info("接收到ID为{}的商品下单请求", pid);

        /* 通过 FeignClient 接口调用本地方法的方式，实现服务的调用。 */
        Product product = productFeignClient.findById(pid);

        Order order = new Order();
        if (product != null) {
            order.setProductId(pid);
            order.setNumber(1);
            order.setPrice(product.getPrice());
            order.setAmount(product.getPrice());
            order.setProductName(product.getProductName());
            // 暂写死用户
            order.setUserId(Long.parseLong("0"));
            order.setUsername("测试用户");
            // 创建订单
            orderService.createOrder(order);
        }
        return order;
    }
}
```

## 5. Nacos Discovery 相关配置项

|     配置项     |                       key                        |           默认值           |                                          说明                                          |
| -------------- | ------------------------------------------------ | ------------------------- | ------------------------------------------------------------------------------------- |
| 服务端地址     | `spring.cloud.nacos.discovery.server-addr`       |                           |                                                                                       |
| 服务名         | `spring.cloud.nacos.discovery.service`           | `spring.application.name` |                                                                                       |
| 权重           | `spring.cloud.nacos.discovery.weight`            | 1                         | 取值范围 1 到 100，数值越大，权重越大                                                     |
| 网卡名         | `spring.cloud.nacos.discovery.network-interface` |                           | 当IP未配置时，注册的IP为此网卡所对应的IP地址，如果此项也未配置，则默认取第一块网卡的地址       |
| 注册的IP地址   | `spring.cloud.nacos.discovery.ip`                |                           | 优先级最高                                                                             |
| 注册的端口     | `spring.cloud.nacos.discovery.port`              | -1                        | 默认情况下不用配置，会自动探测                                                            |
| 命名空间       | `spring.cloud.nacos.discovery.namespace`         |                           | 常用场景之一是不同环境的注册的区分隔离，例如开发测试环境和生产环境的资源（如配置、服务）隔离等。 |
| AccessKey      | `spring.cloud.nacos.discovery.access-key`        |                           |                                                                                       |
| SecretKey      | `spring.cloud.nacos.discovery.secret-key`        |                           |                                                                                       |
| Metadata       | `spring.cloud.nacos.discovery.metadata`          |                           | 使用Map格式配置                                                                         |
| 日志文件名     | `spring.cloud.nacos.discovery.log-name`          |                           |                                                                                       |
| 接入点         | `spring.cloud.nacos.discovery.endpoint`          | UTF-8                     | 地域的某个服务的入口域名，通过此域名可以动态地拿到服务端地址                                 |
| 是否集成Ribbon | `ribbon.nacos.enabled`                           | true                      |                                                                                       |

## 6. Nacos Config 概述

### 6.1. 简介

Nacos 提供用于存储配置和其他元数据的 key/value 存储，为分布式系统中的外部化配置提供服务器端和客户端支持。使用 Spring Cloud Alibaba Nacos Config 可以在 Nacos Server 集中管理 Spring Cloud 应用的外部属性配置。

Spring Cloud Alibaba Nacos Config 是 Config Server 和 Client 的替代方案，客户端和服务器上的概念与 Spring Environment 和 PropertySource 有着一致的抽象，在特殊的 bootstrap 阶段，配置被加载到 Spring 环境中。当应用程序通过部署管道从开发到测试再到生产时，可以管理这些环境之间的配置，并确保应用程序具有迁移时需要运行的所有内容。

### 6.2. nacos config 相关概念

![](images/20220108214758067_5476.png)

- **命名空间(Namespace)**：命名空间可用于进行不同环境的配置隔离。一般一个环境划分到一个命名空间

![](images/20220108175545845_6946.png)

- **配置分组(Group)**：配置分组用于将不同的服务可以归类到同一分组。一般将一个项目的配置分到一组

![](images/20220108175635513_13524.png)

- **配置集(Data ID)**：在系统中，一个配置文件通常就是一个配置集。一般微服务的配置就是一个配置集

## 7. Nacos Config 快速开始

使用 nacos 作为配置中心，其实就是将 nacos 当做一个服务端，将各个微服务看成是客户端，将各个微服务的配置文件统一存放在 nacos 服务器上，然后各个微服务从 nacos 上拉取配置即可。*下面以商品服务做为示例*

### 7.1. Nacos 服务端搭建

> 此部分详见 Nacos Discovery 章节

### 7.2. 修改微服务工程

- 修改项目 pom.xml 文件，引入 Nacos Config Starter 依赖

```xml
<!-- nacos 配置中心客户端 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

- 在应用的 `/src/main/resources/` 目录下，创建 bootstrap.properties/bootstrap.yml 配置文件，并配置 Nacos Config 元数据

```yml
# 配置一些必需项，其他的从注册中心获取
spring:
  application:
    name: service-product
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848 # nacos 的服务端地址
        file-extension: yaml # dataID后缀及内容文件格式，默认值是 properties
  profiles:
    active: dev # 环境标识
```

> 更多配置项详见《Nacos Config 相关配置项》章节或者项目示例代码
>
> 注意事项：
>
> - **注意：不能使用原来的 application.yml 作为配置文件，必须新建一个 bootstrap.properties/bootstrap.yml 作为配置文件**
> - **配置文件优先级(由高到低)**：bootstrap.properties -> bootstrap.yml -> application.properties -> application.yml
> 注意当使用域名的方式来访问 Nacos 时，`spring.cloud.nacos.config.server-addr` 配置的方式为 `域名:port`。 例如 Nacos 的域名为`abc.com.nacos`，监听的端口为 `80`，则 `spring.cloud.nacos.config.server-addr=abc.com.nacos:80`。 注意 `80` 端口不能省略。

- 完成上述两步后，应用会从 Nacos Config 中获取相应的配置，并添加在 Spring Environment 的 `PropertySources` 中。现在可以使用 `@Value` 注解来将对应的配置注入到 Spring 容器的相关实例中。示例创建 `NacosConfigSampleController` 类，定义 userName 和 age 字段用于读取配置中心设置的值，并添加 `@RefreshScope` 注解打开动态刷新功能。

```java
@RestController
@RequestMapping("nacos-config")
@RefreshScope // Spring Cloud 原生注解，实现配置文件的动态加载。
public class NacosConfigSampleController implements EnvironmentAware {

    @Value("${user.name}") // 动态注入配置中心相应的配置项
    private String userName;

    @Value("${user.age}")
    private int age;

    private Environment environment;

    @GetMapping("value-annotation")
    public String getByValueAnnotation() {
        return String.format("通过@Value注解获取配置值，name: %s, age: %d", userName, age);
    }


    @GetMapping("environment")
    public String getByEnvironment() {
        String userName = environment.getProperty("name");
        String age = environment.getProperty("age");
        return String.format("通过 Environment 对象获取配置值，name: %s, age: %s", userName, age);
    }

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
```

### 7.3. 在 nacos 控制台配置并测试

- 点击配置列表，点击右边 + 号，新建配置。

![](images/20220108171021072_10404.png)

- 配置内容注意下面的细节
    - 注意 DataID 与 bootstrap 配置文件相应关系
    - 配置文件格式要跟配置文件的格式对应，且目前仅仅支持YAML和Properties
    - 配置内容按照上面选定的格式书写

![](images/20220108173812062_26625.png)

![](images/20220108171931119_31604.png)

- 删除本地项目的 application.yml 配置，启动程序进行测试。项目能成功启动，说明 nacos 的配置中心功能已经实现。

## 8. Nacos Config 高级使用

### 8.1. 配置动态刷新

通过项目的配置项 `spring.cloud.nacos.config.refresh-enabled` 来控制是否开启监听和自动刷新，默认是true（开启）。程序中动态获取最新配置的方法如下：

#### 8.1.1. 方式一：硬编码方式

通过 `org.springframework.context.ConfigurableApplicationContext` 对象获取 `Environment` 实例，可以根据key值获取到相应的配置值

```java
@Autowired
private ConfigurableApplicationContext applicationContext;

@GetMapping("configurableApplicationContext")
public String getByConfigurableApplicationContext() {
    String userName = applicationContext.getEnvironment().getProperty("user.name");
    String age = applicationContext.getEnvironment().getProperty("user.age");
    return String.format("通过 ConfigurableApplicationContext.getEnvironment() 获取 Environment对象，获取配置值，name: %s, age: %s", userName, age);
}
```

> 注：此方式与快速开始示例中的实现 `EnvironmentAware` 接口获取 `Environment` 实例的效果一样。

#### 8.1.2. 方式二：注解方式(推荐)

```java
@RestController
@RequestMapping("nacos-config")
@RefreshScope // Spring Cloud 原生注解，实现配置文件的动态加载。。只需要在需要动态读取配置的类上添加此注解即可。
public class NacosConfigSampleController implements EnvironmentAware {
    @Value("${user.name}") // 动态注入配置中心相应的配置项
    private String userName;

    @Value("${user.age}")
    private int age;

    @GetMapping("value-annotation")
    public String getByValueAnnotation() {
        return String.format("通过@Value注解获取配置值，name: %s, age: %d", userName, age);
    }
}
```

### 8.2. 配置共享

配置共享，即将公共配置的部分抽取出来，在其他配置文件中引入。

#### 8.2.1. 同一个微服务的不同环境配置文件之间共享配置

在同一个微服务的不同环境之间实现配置共享，只需要提取一个以 `spring.application.name` 命名的配置文件，然后将其所有环境的公共配置放在里面即可。*以商品微服务为例*

- 新建一个名为 `service-product.yaml` 配置商品微服务的公共配置项

![](images/20220108182242453_13869.png)

- 新建一个名为 `service-product-test.yaml` 配置测试环境的配置项

![](images/20220108182708074_20425.png)

- 删除 `service-product-dev.yaml` 原来公共部分的配置
- 修改本地的 `spring.profiles.active` 配置项的，切换不同的环境的配置文件进行测试。

![](images/20220108183138237_20271.png)

#### 8.2.2. 不同微服务之间共享配置

不同为服务之间实现配置共享的原理类似于文件引入，就是定义一个公共配置，然后在不同的服务的配置中引入。*下面以商品微服务为例*

- 在 nacos 配置中心中定义一个 DataID 为 all-service.yaml 的配置，用于所有微服务共享

![](images/20220108183519723_15552.png)

- 在配置中心中，将商品服务的公共配置项删除

![](images/20220108183653748_32626.png)

- 修改商品服务本地的 bootstrap.yml/bootstrap.properties 配置文件，配置引入公共配置

```yml
# 配置一些必需项，其他的从注册中心获取
spring:
  application:
    name: service-product
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848 # nacos 的服务端地址
        file-extension: yaml # dataID后缀及内容文件格式，默认值是 properties
        # 引入公共配置，已经过时
        shared-dataids: all-service.yaml # 配置要引入的配置
        refreshable-dataids: all-service.yaml # 配置要实现动态配置刷新的配置
  profiles:
    active: dev # 环境标识
```

- 启动商品微服务进行测试

## 9. Nacos Config 相关配置项

|         配置项          |                     key                     |           默认值           |                                   说明                                   |
| ---------------------- | ------------------------------------------- | ------------------------- | ----------------------------------------------------------------------- |
| 服务端地址               | `spring.cloud.nacos.config.server-addr`     |                           |                                                                         |
| DataId前缀             | `spring.cloud.nacos.config.prefix`          | `spring.application.name` |                                                                         |
| Group                  | `spring.cloud.nacos.config.group`           | DEFAULT_GROUP             |                                                                         |
| dataID后缀及内容文件格式 | `spring.cloud.nacos.config.file-extension`  | properties                | dataId 的后缀，同时也是配置内容的文件格式，目前只支持 properties             |
| 配置内容的编码方式       | `spring.cloud.nacos.config.encode`          | UTF-8                     | 配置的编码                                                               |
| 获取配置的超时时间       | `spring.cloud.nacos.config.timeout`         | 3000                      | 单位为 ms                                                                |
| 配置的命名空间           | `spring.cloud.nacos.config.namespace`       |                           | 常用场景之一是不同环境的配置的区分隔离，例如开发测试环境和生产环境的资源隔离等。 |
| AccessKey              | `spring.cloud.nacos.config.access-key`      |                           |                                                                         |
| SecretKey              | `spring.cloud.nacos.config.secret-key`      |                           |                                                                         |
| 相对路径                | `spring.cloud.nacos.config.context-path`    |                           | 服务端 API 的相对路径                                                     |
| 接入点                  | `spring.cloud.nacos.config.endpoint`        | UTF-8                     | 地域的某个服务的入口域名，通过此域名可以动态地拿到服务端地址                   |
| 是否开启监听和自动刷新   | `spring.cloud.nacos.config.refresh-enabled` | true                      |                                                                         |
