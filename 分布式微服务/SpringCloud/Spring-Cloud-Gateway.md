## 1. Spring Cloud Gateway 微服务网关

### 1.1. 简述

Spring Cloud Netflix Zuul 1.x 是一个基于阻塞 IO 的 API Gateway 以及 Servlet；直到2018年5月，Zuul 2.x（基于Netty，也是非阻塞的，支持长连接）才发布，但 Spring Cloud 暂时还没有整合计划。Spring Cloud Gateway 比 Zuul 1.x 系列的性能和功能整体要好。

Spring Cloud Gateway 是 Spring 官方基于 Spring 5.0，Spring Boot 2.0 和 Project Reactor 等技术开发的网关，旨在为微服务架构提供一种简单而有效的统一的 API 路由管理方式，统一访问接口。Spring Cloud Gateway 作为 Spring Cloud 生态系中的网关，目标是替代 Netflix ZUUL，其不仅提供统一的路由方式，并且基于 Filter 链的方式提供了网关基本的功能，例如：安全，监控/埋点，限流等。它是基于Nttey的响应式开发模式。

> Spring Cloud Gateway官方文档：https://spring.io/projects/spring-cloud-gateway#overview

|         组件         | RPS(request per second) |
| :------------------: | ----------------------- |
| Spring Cloud Gateway | Requests/sec: 32213.38  |
|       Zuul 1.x       | Requests/sec: 20800.13  |

上表为 Spring Cloud Gateway 与 Zuul 的性能对比，从结果可知，Spring Cloud Gateway 的 RPS 是 Zuul 的 1.6 倍

### 1.2. 优缺点

优点：

- 性能强劲：是第一代网关Zuul的1.6倍
- 功能强大：内置了很多实用的功能，例如转发、监控、限流等
- 设计优雅，容易扩展

缺点：

- 其实现依赖Netty与WebFlux，不是传统的Servlet编程模型，学习成本高
- 不能将其部署在Tomcat、Jetty等Servlet容器里，只能打成jar包执行
- 需要Spring Boot 2.0及以上的版本，才支持

### 1.3. 核心概念与架构

**路由（route）**：路由是网关最基础的部分，表示一个具体的路由信息载体。路由信息由一个ID、一个目的地URI、排序order、一组断言工厂和一组Filter组成。如果断言为真，则说明请求URL和配置的路由匹配。

![](images/20201024155248894_3162.png)

- id，路由标识符，唯一的，区别于其他 Route。
- uri，路由指向的目的地 uri，即客户端请求最终被转发到的微服务。
- order，用于多个 Route 之间的排序，数值越小排序越靠前，匹配优先级越高。
- **predicates（断言）**：Java8 中的断言函数，断言的作用是进行条件匹配，只有断言都返回真，才会真正的执行路由到目的地 uri。Spring Cloud Gateway 中的断言函数输入类型是 Spring5.0 框架中的`ServerWebExchange`。Spring Cloud Gateway 中的断言函数允许开发者去定义匹配来自 Http Request 中的任何信息，比如请求头和参数等。
- **filter（过滤器）**：一个标准的 Spring WebFilter，Spring Cloud Gateway 中的 Filter 分为两种类型，分别是`Gateway Filter`和`Global Filter`。过滤器`Filter`可以对请求和响应进行拦截处理。

### 1.4. 工作流程

![](images/26574209248968.png)

当客户端向 Spring Cloud Gateway 发出请求。如果 Gateway Handler Mapping 匹配到一个请求与路由，它将被发送到 Gateway Web Handler。然后会通过一系列特定于该请求的过滤器链来运行该请求。在代理请求发送之前，流程图中所有的虚线左侧的**前置过滤器**处理逻辑都被执行，然后发出代理请求，在代理请求发出后，虚线右侧的所有后置过滤器逻辑被运行。

更详细的流程图：

![](images/485031616257002.png)

执行流程梳理：

1. Gateway Client 向 Gateway Server 发送请求
2. 请求首先会被 HttpWebHandlerAdapter 进行提取组装成网关上下文
3. 然后网关的上下文会传递到 DispatcherHandler，它负责将请求分发给 RoutePredicateHandlerMapping
4. RoutePredicateHandlerMapping 负责路由查找，并根据路由断言判断路由是否可用
5. 如果过断言成功，由 FilteringWebHandler 创建过滤器链并调用
6. 请求会一次经过 PreFilter -> 微服务 -> PostFilter 的方法，最终返回响应

## 2. Spring Cloud Gateway 基础入门案例

### 2.1. 基于 Nacos 注册中心的实现

> 复用`spring-cloud-sample-zuul`工程的代码，删除zuul网关工程，创建`spring-cloud-sample-gateway`

#### 2.1.1. 创建工程导入依赖

在`spring-cloud-sample-gateway`项目中添加新的模块`sample-gateway-server-gateway`，并导入依赖

```xml
<!--
    Spring Cloud Gateway 服务网关的核心依赖
    注意：SpringCloud Gateway 内部使用的web框架为 netty + webflux
        webflux 与 spring-boot-starter-web 依赖存在冲突
 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

<font color=red>**注意：SpringCloud Gateway 内部使用的web框架为netty + webflux，和SpringMVC不兼容。引入的限流组件是hystrix。redis底层不再使用jedis，而是lettuce**</font>

#### 2.1.2. 配置启动类

```java
@SpringBootApplication
public class GatewayServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
```

> *注：Spring Cloud Gateway 组件不需要配置任何注解即可开启*

#### 2.1.3. 配置路由

创建 `application.yml` 配置文件，配置gateway的路由

```yml
server:
  port: 8080 # 项目端口
spring:
  application:
    name: shop-server-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      # 配置路由（包含的元素：路由id、路由到微服务的uri，断言【判断条件】）
      routes:
        # 路由配置都是多个，所以此处是一个数组
        - id: shop-service-product # 路由id
          uri: http://127.0.0.1:9001 # 路由到微服务的uri
          predicates:
            # 注意此path属性与zuul的path属性不一样，zuul只会将/**部分拼接到uri后面，而gateway会将全部拼接到uri后面
            - Path=/product/** # 断言，此处访问 http://127.0.0.1:8080/product/1 就会路由到 http://127.0.0.1:9001/product/1
```

配置属性说明：

- `id`：路由id，保证唯一即可
- `uri`：目标服务地址
- `predicates`：路由条件。Predicate 接受一个输入参数，返回一个布尔值结果。该接口包含多种默认方法来将 Predicate 组合成其他复杂的逻辑（比如：与，或，非）

上面示例的配置解释：配置了一个`id`为`shop-service-product`的路由规则，当访问网关请求地址以`product`开头时，会自动转发到地址：`http://127.0.0.1:9001/product/xxx`。配置完成启动项目即可在浏览器访问进行测试

### 2.2. 基于 Nacos 注册中心的实现

> 示例代码详见 `spring-cloud-note\spring-cloud-sample-gateway-nacos`

#### 2.2.1. 服务提供者

创建服务提供者，整合注册中心 Nacos，并提供一个接口 `/hello`

- 引入 nacos 注册中心客户端依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

- 配置 nacos

```yml
server:
  port: 8080
spring:
  application:
    name: service-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

- 创建启动类，使用 `@EnableDiscoveryClient` 注解开启服务注册客户端功能

```java
@SpringBootApplication
@EnableDiscoveryClient
public class SampleGatewayNacosServiceProvider {
    public static void main(String[] args) {
        SpringApplication.run(SampleGatewayNacosServiceProvider.class, args);
    }
}
```

- 测试接口

```java
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "hello " + name + "!";
    }
}
```

#### 2.2.2. 服务网关

创建 gateway 网关服务，整合注册中心 Nacos 与 Spring Cloud Gateway

- 添加 Spring Cloud Gateway 与 nacos 注册中心客户端依赖

```xml
<!-- Spring Cloud Gateway 服务网关的核心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- nacos 客户端 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

- 配置 nacos 与 Spring Cloud Gateway 基础路由。此示例通过 `spring.cloud.gateway.discovery.locator.enabled` 配置启用自动发现客户端网关集成的标志，默认值是false(关闭)。开启后根据服务名称自动转发。

```yml
server:
  port: 8081
spring:
  application:
    name: service-gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      discovery: # 配置自动根据注册中心的微服务名称进行路由转发
        locator:
          enabled: true # 开启根据服务名称自动转发，默认值是false
```

#### 2.2.3. 测试

启动nacos与服务提供者工程，测试 Gateway 路由转发。

在 nacos 控制台看到已注册的服务列表

![](images/270005907230543.png)

在浏览器通过 service-gateway 访问 `/hello` 接口

```
http://localhost:8081/service-provider/hello?name=moonkira
```

验证是否成功结果

## 3. 路由配置规则

### 3.1. 路由断言功能

Spring Cloud Gateway 的功能很强大，其内置了很多 `Predicates` 功能。在 Spring Cloud Gateway 中 Spring 利用 Predicate 的特性实现了各种路由匹配规则，可以通过`Header`、请求参数等不同的条件来进行作为条件匹配到对应的路由。

![](images/20201025121213735_25442.png)

Spring Cloud Gateway 包括许多内置路由断言工厂，所有这些断言都与 HTTP 请求的不同属性匹配。具体如下：

#### 3.1.1. 路由断言 - After

基于Datetime类型的断言工厂 `AfterRoutePredicateFactory`：接收一个日期参数，判断请求日期是否晚于指定日期

`After`的路由判断规则，用于匹配在指定日期时间之后发生的请求

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
          # 路由断言匹配在指定日期时间之后发生的请求
          - After=2017-01-20T17:42:47.789-07:00[America/Denver]
```

#### 3.1.2. 路由断言 - Before

基于Datetime类型的断言工厂 `BeforeRoutePredicateFactory`：接收一个日期参数，判断请求日期是否早于指定日期

`Before`的路由判断规则，用于匹配在指定日期时间之前发生的请求

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: before_route
        uri: https://example.org
        predicates:
          # 路由断言匹配在指定日期时间之前发生的请求
          - Before=2017-01-20T17:42:47.789-07:00[America/Denver]
```

#### 3.1.3. 路由断言 - Between

基于Datetime类型的断言工厂 `BetweenRoutePredicateFactory`：接收两个日期参数，判断请求日期是否在指定时间段内

`Between`的路由判断规则，用于两个指定日期时间之间发生的请求

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: between_route
        uri: https://example.org
        predicates:
          # 路由断言匹配在指定两个日期时间之间发生的请求
          - Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
```

#### 3.1.4. 路由断言 - Cookie

基于Cookie的断言工厂 `CookieRoutePredicateFactory`：接收两个参数，cookie 名字和一个正则表达式。判断请求cookie是否具有给定名称且值与正则表达式匹配。

`Cookie`的路由判断规则，用于Cookie匹配，此predicate匹配给定名称(`chocolate`)和正则表达式(`ch.p`)

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: cookie_route
        uri: https://example.org
        predicates:
          - Cookie=chocolate, ch.p
```

#### 3.1.5. 路由断言 - Header

基于Header的断言工厂 `HeaderRoutePredicateFactory`：接收两个参数，标题名称和正则表达式。判断请求 Header 是否具有给定名称且值与正则表达式匹配。

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: header_route
        uri: https://example.org
        predicates:
          - Header=X-Request-Id, \d+
```

上面示例 `Header` 的路由判断规则是，header名称匹配`X-Request-Id`，且正则表达式匹配`\d+`

#### 3.1.6. 路由断言 - Host

基于Host的断言工厂 `HostRoutePredicateFactory`：接收一个参数，主机名模式。判断请求的 Host 是否满足匹配规则。

`Host`的路由判断规则，用于Host匹配，匹配指定的Host主机列表，`**`代表可变参数

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: host_route
        uri: https://example.org
        predicates:
          - Host=**.somehost.org,**.anotherhost.org
```

#### 3.1.7. 路由断言 - Method

基于Method请求方法的断言工厂 `MethodRoutePredicateFactory`：接收一个参数，判断请求类型是否跟指定的类型匹配。

`Method`的路由判断规则，用于请求 Method 匹配，匹配的是请求的 HTTP 方法

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: method_route
        uri: https://example.org
        predicates:
          # 如果请求方法是GET或POST，则匹配路由
          - Method=GET,POST
```

#### 3.1.8. 路由断言 - Path

基于 Path 请求路径的断言工厂 `PathRoutePredicateFactory`：接收一个参数，判断请求的URI部分是否满足路径规则。

`Path` 的路由判断规则，用于请求url匹配，其中 `{segment}` 为可变参数

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: path_route
        uri: https://example.org
        predicates:
          - Path=/red/{segment},/blue/{segment}
```

#### 3.1.9. 路由断言 - Query

基于 Query 请求参数的断言工厂 `QueryRoutePredicateFactory`：接收两个参数，请求 param 和正则表达式， 判断请求参数是否具有给定名称且值与正则表达式匹配。

`Query`的路由判断规则，用于匹配请求参数，将请求的参数`param(green)`进行匹配，也可以进行regexp正则表达式匹配 (参数包含`red`，并且`red`的值匹配`green`或者`greet`都可以 )

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: query_route
        uri: https://example.org
        predicates:
          # 请求包含绿色查询参数，则匹配路由
          - Query=green
          # 请求包含red的请求参数，并且值为gree时，匹配路由；如进行regexp正则表达式匹配，则green或者greet均匹配
          #- Query=red, gree.
```

#### 3.1.10. 路由断言 - RemoteAddr

基于远程地址的断言工厂 `RemoteAddrRoutePredicateFactory`：接收一个IP地址段，判断请求主机地址是否在地址段中

`RemoteAddr`的路由判断规则，用于远程IP地址匹配，将匹配`192.168.1.1`~`192.168.1.254`之间的ip地址，其中`24`为子网掩码位数即`255.255.255.0`

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: remoteaddr_route
        uri: https://example.org
        predicates:
          # 匹配192.168.1.1~192.168.1.254之间的ip地址
          - RemoteAddr=192.168.1.1/24
```

#### 3.1.11. 路由断言 - Weight

基于路由权重的断言工厂 `WeightRoutePredicateFactory`：接收一个[组名,权重], 然后对于同一个组内的路由按照权重转发

`Weight`的路由判断规则，用于请求权重匹配。其中有两个参数`group`和`weight`，均为int数值类型，用于计算权重

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: weight_high
        uri: https://weighthigh.org
        predicates:
          - Weight=group1, 8
      - id: weight_low
        uri: https://weightlow.org
        predicates:
          - Weight=group1, 2
```

以上示例配置表示：会将大约 80% 的流量转发到 weighthigh.org，将大约 20% 的流量转发到 weightlow.org。

### 3.2. 路由动态获取服务地址

和 zuul 网关类似，在 Spring Cloud GateWay 组件也支持根据服务名称，动态获取其服务器地址：即自动的从注册中心中获取服务列表并访问

#### 3.2.1. 基于 Eureka 注册中心动态获取路由服务地址

##### 3.2.1.1. 添加注册中心依赖（Eureka）

在`12-springcloud-gateway`工程的 pom 文件中添加注册中心的客户端依赖（此示例以 Eureka 做为注册中心）

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

##### 3.2.1.2. 配置服务名称

修改 `application.yml` 配置文件，添加 eureka 注册中心的相关配置，并修改访问映射的 URL 为服务名称

```yml
spring:
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      # 配置路由（包含的元素：路由id、路由到微服务的uri，断言【判断条件】）
      routes:
        # 路由配置都是多个，所以此处是一个数组
        - id: shop-service-product # 路由id
          uri: lb://shop-service-product # 方式二：根据微服务名称从注册中心拉取服务的地址与端口，格式： lb://服务名称（服务在注册中心上注册的名称）
          predicates:
            # 注意此path属性与zuul的path属性不一样，zuul只会将/**部分拼接到uri后面，而gateway会将全部拼接到uri后面
            - Path=/product/** # 断言，此处访问 http://127.0.0.1:8080/product/1 就会路由到 http://127.0.0.1:9001/product/1

# Eureka 配置
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    instance-id: ${spring.application.name}:${server.port}  # 指定实例id
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/ # Eureka server 地址，多个eureka server之间用,隔开
```

<font color=red>**配置动态路由要点：配置uri属性以`lb://`开头（lb代表从注册中心获取服务），后面接的就是需要转发到的服务名称**</font>

测试访问网关请求地址以`product`开头时，会通过注册中心获取转发的地址，自动转发到地址：`http://127.0.0.1:9001/product/xxx`。配置完成启动项目即可在浏览器访问进行测试

#### 3.2.2. 基于 Nacos 注册中心动态获取路由服务列表

##### 3.2.2.1. 添加注册中心依赖（Nacos）

在`spring-cloud-alibaba-sample\alibaba-sample-api-gateway`工程的 pom 文件中添加注册中心的客户端依赖（此示例以 Nacos 做为注册中心）

```xml
<!-- nacos 客户端 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

##### 3.2.2.2. 开启 nacos 客户端

在项目启动类或者配置上添加注解 `@EnableDiscoveryClient`，开启 nacos 客户端

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

##### 3.2.2.3. 配置服务名称

修改项目的 `application.yml` 配置文件，具体修改内容如下：

1. 添加 nacos 注册中心的相关配置，将网关服务注册到 nacos 中，
2. 修改访问映射的uri，改为注册中心上相应的服务名称

```yml
server:
  port: 7000 # 项目端口
spring:
  application:
    name: api-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      discovery:
        locator:
          enabled: true # 配置开启让 gateway 从 nacos 注册中心中获取服务信息列表
      # 配置路由数组（包含的元素：路由id、路由到微服务的uri，断言【判断条件】）
      routes:
        # 路由配置都是多个，所以此处是一个数组
        - id: service-product # 路由id
          # 方式二：根据微服务名称从注册中心拉取服务的地址与端口，格式： lb://服务名称（服务在注册中心上注册的名称）。
          # lb 是 Load Balance 的缩写，gateway 遵循实现了负载均衡策略
          uri: lb://service-product
          order: 1 # 路由的优先级,数字越小级别越高
          predicates: # 断言(就是路由转发要满足的条件)
            # 注意此path属性与zuul的path属性不一样，zuul只会将/**部分拼接到uri后面，而gateway会将全部拼接到uri后面
            # 断言，此处访问 http://127.0.0.1:7000/api-product/product/1 就会路由到 http://127.0.0.1:8081/api-product/product/1(在未配置RewritePath属性前、StripPrefix 过滤器之前)
            - Path=/api-product/**
          filters: # 过滤器，请求在传递过程中可以通过过滤器对其进行一定的修改
            - StripPrefix=1 # 此过滤器配置的作用是，在请求转发之前去掉第1层路径，即以上请求转化路径会变成 http://127.0.0.1:8081/product/1
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 配置 Nacos server 的地址，将网关服务注册到 nacos 中
```

测试访问网关请求地址以`api-product`开头时，会通过注册中心获取转发的地址，自动转发到地址：`http://127.0.0.1:8081/product/xxx`。配置完成启动项目即可在浏览器访问进行测试

### 3.3. 简化路径配置（根据微服务名称转发请求）

Spring Cloud Gateway 提供了可以直接从注册中心，根据相应的服务名称来进行请求路径转发的简化配置

#### 3.3.1. 传统手动配置路由转发

在未配置开启从注册中心自动根据服务名称映射请求转发路径前，通过网关访问订单服务，会转发失败。因为没有配置相应服务的路由匹配规则

![](images/20201030094305528_7738.png)

#### 3.3.2. 配置根据服务名称自动转发

##### 3.3.2.1. 基于 eureka 注册中心的配置示例

![](images/20201030095931692_5394.png)

通过 `spring.cloud.gateway.discovery.locator.enabled` 配置自动根据服务名称进行请求转发，**默认的匹配规则是：请求路径以服务名称开头，会自动匹配到相应服务的ip+端口，并且将服务名称以后的部分拼接到服务的url的后面**。此时访问相应微服务名称的路径时，请求会转发到相应的服务。

请求测试：

![](images/20201030094951563_21178.png)

##### 3.3.2.2. 基于 nacos 注册中心的配置示例

当直接通过服务注册的名称来请求，路由配置时则可以省略 `routes` 的相关的配置，通过 `spring.cloud.gateway.discovery.locator.enabled` 配置自动根据服务名称进行请求转发，默认转发请求规则是：`http://网关服务地址/微服务名称/接口uri`。如下：

```yml
server:
  port: 7000 # 项目端口
spring:
  application:
    name: api-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      discovery:
        locator:
          enabled: true # 配置开启让 gateway 从 nacos 注册中心中获取服务信息列表
      # 通过服务注册的名称请求，则可以省略不配置。即 http://127.0.0.1:7000/service-product/product/xx
#      routes:
#        - id: service-product # 路由id
#          uri: lb://service-product
#          order: 1
#          predicates:
#            - Path=/api-product/**
#          filters:
#            - StripPrefix=1
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 配置 Nacos server 的地址，将网关服务注册到 nacos 中
```

测试，原来的请求路径已经无效了。

![](images/20220105102635258_11205.png)

测试通过服务注册的名称，按照`http://网关服务地址/微服务名称/接口uri`的格式去访问，路由可以成功转发。即`http://127.0.0.1:7000/service-product/product/xx` 默认相当于转发 `http://127.0.0.1:8081/product/xx`。如果请求服务集群，也是能生效。

![](images/20220105102718532_5629.png)

### 3.4. 自定义路由断言

Spring Cloud GateWay 内置的断言基本上已经满足大部分的需要，但有些可能还是需要自定义一些路由断言逻辑。例如

1. Query 断言中，只能判断一组参数和值，如果需要判断2组怎么办？
2. Between 断言中，判断的是精准的时间范围，如果需要每天夜里12点至4点不允许下单怎么办？

Spring Cloud Gateway 官方文档中并没有说明如何自定义断言，但可以学习模仿内置断言来开发一个自定义断言。

#### 3.4.1. 内置断言开发思路分析

![](images/123143209248969.png)

通过查看源码可以看到，自定义断言的核心点：

- 集成抽象类 `AbstractRoutePredicateFactory`，是泛型，需要指定一个类。此处源码使用了一个内部类 `Config`，对应的是配置文件中 After 的值。
- `shortcutFieldOrder` 方法用于定义 After 配置值中各项的顺序
- apply 方法处理断言逻辑
- 此类需要定义为 Bean

#### 3.4.2. 自定义路由断言开发步骤

案例需求：仅仅让请求参数 `range` 在 `(min,max)` 之间的人来访问。具体实现步骤如下：

- 自定义路由断言配置。修改网关项目的 application.yml 配置文件，添加自定义的断言：

```yml
server:
  port: 7000
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: service-product # 路由id
          uri: lb://service-product
          predicates: # 断言
            - Path=/api-product/**
            # 自定义路由断言，具体实现range限制范围只有在18到26岁之间请求能访问
            - Custom=18,26
          filters:
            - StripPrefix=1
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 配置 Nacos server 的地址，将网关服务注册到 nacos 中
```

- 自定义一个路由断言工厂类，实现断言判断逻辑。断言工厂类实现需要满足以下两个要求：

1. 类的名称必须是以 `RoutePredicateFactory` 结尾。即 `自定义断言配置名称+RoutePredicateFactory`
2. 类必须继承 `AbstractRoutePredicateFactory<配置类>` 抽象类
- > 技巧提示：如不知道如何实现这些接口，可以通过参考框架自身内置的实现来进行改造^_^

```java
// AbstractRoutePredicateFactory 的泛型 C 是一个配置类，配置类用于接收中在配置文件（properties/yml）中的配置值
@Component
public class CustomRoutePredicateFactory extends AbstractRoutePredicateFactory<CustomRoutePredicateFactory.Config> {

    // 构造函数，调用父类构造器，并传入配置类
    public CustomRoutePredicateFactory() {
        super(CustomRoutePredicateFactory.Config.class);
    }

    /*
     * 读取配置文件的中参数值，赋值到配置类中的属性上
     */
    @Override
    public List<String> shortcutFieldOrder() {
        // 注意，定义返回集合中元素的位置的顺序，必须跟配置文件中的参数值的顺序相对应
        return Arrays.asList("min", "max");
    }

    /**
     * 断言核心的处理逻辑
     *
     * @param config
     * @return
     */
    @Override
    public Predicate<ServerWebExchange> apply(CustomRoutePredicateFactory.Config config) {
        // Predicate<T> 是 jdk8 的函数式接口，可以直接使用 lambda 表达式
        return serverWebExchange -> {
            // 获取请求传的参数 range 的值
            String range = serverWebExchange.getRequest().getQueryParams().getFirst("range");
            // 对范围参数进行判断
            if (StringUtils.hasText(range)) {
                // 非空，则进行范围的判断
                int num = Integer.parseInt(range);
                return num >= config.getMin() && num <= config.getMax();
            }
            return false;
        };
    }

    /**
     * 配置类，用于接收在配置文件中的对应参数值
     */
    public static class Config {
        private int min;
        private int max;

        public Config() {
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
```

- 测试。请求带上参数`range`，如果超出范围，则请求转发失败

```
http://127.0.0.1:7000/api-product/product/2
http://127.0.0.1:7000/api-product/product/2?range=19
http://127.0.0.1:7000/api-product/product/2?range=33
```

![](images/20220105113251508_24769.png)

## 4. 过滤器

Spring Cloud Gateway 除了具备请求路由功能之外，也支持对请求的过滤。与 Zuul 网关类似，也是通过过滤器的形式来实现的

### 4.1. 过滤器基础概述

过滤器就是在请求的传递过程中，对 **转发请求服务之前** 和 **获取响应之后返回结果之前** 做一些处理

#### 4.1.1. 过滤器的生命周期

Spring Cloud Gateway 的 `Filter` 的生命周期不像 Zuul 的那么丰富，它只有两个：`pre` 和 `post`

- `PRE`：这种过滤器在请求被路由之前调用。可利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等。
- `POST`：这种过滤器在路由到微服务以后执行。可用来为响应添加标准的 HTTP Header、收集统计信息和指标、将响应从微服务发送给客户端等。

![](images/20201030101706521_30318.png)

#### 4.1.2. 过滤器类型

Spring Cloud Gateway 的 Filter 从作用范围可分为另外两种 `GatewayFilter` 与 `GlobalFilter`。

- `GatewayFilter`（局部过滤器）：应用到单个路由或者一个分组的路由上。*如上面基础入门配置重写转发路径的示例，就是使用了`GatewayFilter`，只作用指定的路由配置上*
- `GlobalFilter`（全局过滤器）：应用到所有的路由上

### 4.2. 局部过滤器

#### 4.2.1. 简介

局部过滤器（`GatewayFilter`），是针对单个路由的过滤器。可以对访问的 URL 过滤，进行切面处理。在 Spring Cloud Gateway 中通过`GatewayFilter`的形式内置了很多不同类型的局部过滤器。

*注：一般在配置局部过滤器针对单个路由设置一些过滤规则时，都会使用 Spring Cloud Gateway 内置的过滤器*

#### 4.2.2. 内置局部过滤器（示例配置使用时再补充整理）

|          过滤器工厂           |                                         作用                                          |                                参数                                |
| :-------------------------: | ------------------------------------------------------------------------------------ | ----------------------------------------------------------------- |
|      AddRequestHeader       | 为原始请求添加Header                                                                    | Header的名称及值                                                   |
|     AddRequestParameter     | 为原始请求添加请求参数                                                                   | 参数名称及值                                                        |
|      AddResponseHeader      | 为原始响应添加Header                                                                    | Header的名称及值                                                   |
|    DedupeResponseHeader     | 剔除响应头中重复的值                                                                     | 需要去重的Header名称及去重策略                                        |
|           Hystrix           | 为路由引入Hystrix的断路器保护                                                            | `HystrixCommand`的名称                                             |
|       FallbackHeaders       | 为fallbackUri的请求头中添加具体的异常信息                                                 | Header的名称                                                       |
|         PrefixPath          | 为原始请求路径添加前缀                                                                   | 前缀路径                                                           |
|     PreserveHostHeader      | 为请求添加一个`preserveHostHeader=true`的属性，路由过滤器会检查该属性以决定是否要发送原始的Host | 无                                                                |
|     RequestRateLimiter      | 用于对请求限流，限流算法为令版桶                                                           | keyResolver、rateLimiter、statusCode、denyEmptyKey、emptyKeyStatus |
|         RedirectTo          | 将原始请求重定向到指定的UR                                                               | http状态码及重定向的url                                             |
| RemoveHopByHopHeadersFilter | 为原始请求删除IETF组织规定的一系列Header                                                  | 默认就会启用，可以通过配置指定仅删除哪些Header                           |
|     RemoveRequestHeader     | 为原始请求删除某个Header                                                                | Header名称                                                         |
|    RemoveResponseHeader     | 为原始响应删除某个Header                                                                | Header名称                                                         |
|         RewritePath         | 重写原始的请求路径                                                                      | 原始路径正则表达式以及重写后路径的正则表达式                             |
|    RewriteResponseHeader    | 重写原始响应中的某个Header                                                               | Header名称，值的正则表达式，重写后的值                                 |
|         SaveSession         | 在转发请求之前，强制执行`WebSession::save`操作                                            | 无                                                                |
|        secureHeaders        | 为原始响应添加一系列起安全作用的响应头                                                      | 无，支持修改这些安全响应头的值                                         |
|           SetPath           | 修改原始的请求路径                                                                      | 修改后的路径                                                        |
|      SetResponseHeader      | 修改原始响应中某个Header的值                                                             | Header名称，修改后的值                                              |
|          SetStatus          | 修改原始响应的状态码                                                                     | HTTP 状态码，可以是数字，也可以是字符串                                 |
|         StripPrefix         | 用于截断原始请求的路径                                                                   | 使用数字表示要截断的路径的数量                                         |
|            Retry            | 针对不同的响应进行重试                                                                   | retries、statuses、methods、series                                 |
|         RequestSize         | 设置允许接收最大请求包的大小。如果请求包大小超过设置的值，则返回`413 Payload Too Large`         | 请求包大小，单位为字节，默认值为5M                                     |
|      ModifyRequestBody      | 在转发请求之前修改原始请求体内容                                                           | 修改后的请求体内容                                                   |
|     ModifyResponseBody      | 修改原始响应体的内容                                                                     | 修改后的响应体内容                                                   |

以上每个过滤器工厂都对应一个实现类，并且这些类的名称必须以 `GatewayFilterFactory` 结尾，这是 Spring Cloud Gateway 的一个约定，例如 `AddRequestHeader` 对应的实现类为 `AddRequestHeaderGatewayFilterFactory`。对于这些过滤器的使用方式可以参考官方文档

> 官方内置局部过滤器参考(2.2.5.RELEASE版本)：https://docs.spring.io/spring-cloud-gateway/docs/2.2.5.RELEASE/reference/html/#gatewayfilter-factories

##### 4.2.2.1. AddRequestHeader

AddRequestHeader 过滤器作用是，接收 2 个参数：name、value，作为新的请求头，添加到当前请求中。配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          filters:
            - AddRequestHeader=X-Request-red, blue
```

还是用于匹配路径或主机的 URI 变量。URI 变量可以在运行过滤时被使用。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
            - Path=/red/{segment}
          filters:
            - AddRequestHeader=X-Request-Red, Blue-{segment}
```

在服务提供者中定义测试接口

```java
@RestController
public class TestController {
    @GetMapping("/test/head")
    public String testGatewayHead(HttpServletRequest request, HttpServletResponse response) {
        String head = request.getHeader("X-Request-red");
        return "X-Request-red : " + head;
    }
}
```

启动网关服务，访问接口 `http://localhost:8081/test/head`，请求头已经增加相应的内容

![](images/77095011236836.png)

##### 4.2.2.2. AddRequestParameter

AddRequestParameter 过滤器作用是，接收2个参数：name、value，作为新的请求参数，添加到当前请求中。配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_parameter_route
          uri: http://localhost:8001/test/param
          predicates:
            - Method=GET
          filters:
            - AddRequestParameter=red, blue
```

在服务提供者中定义测试接口

```java
@GetMapping("/test/param")
public String testGatewayParam(HttpServletRequest request, HttpServletResponse response) {
    String val = request.getParameter("red");
    return "param red : " + val;
}
```

启动网关服务，访问接口 `http://localhost:8081/test/param`，请求增加相应的参数。

##### 4.2.2.3. AddResponseHeader

AddResponseHeader 过滤器作用是，接收2个参数：name、value，作为新的响应头信息，添加到当前请求的响应中。配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        # 测试 AddResponseHeader
        - id: add_response_header_route
          uri: http://localhost:8001/hello
          predicates:
            - Method=GET
          filters:
            - AddResponseHeader=X-Response-Red, Blue
```

启动网关服务，访问接口 `http://localhost:8081/hello`，请求增加相应的参数。在其响应信息中可以看到我们添加的内容。

![](images/415095614230543.png)

##### 4.2.2.4. RemoveRequestHeader

RemoveRequestHeader 过滤器作用是，接收1个参数：name，删除当前请求中指定的头信息。配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: removerequestheader_route
          uri: http://localhost:8001/hello
          predicates:
            - Method=GET
          filters:
            - RemoveRequestHeader=X-Request-red
```

##### 4.2.2.5. StripPrefix

StripPrefix 过滤器作用是，接收1个参数：数字，含义为从请求路径中截取掉前面的指定参数个数部分。配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: strip_prefix_route
          uri: http://localhost:8001/hello
          predicates:
            - Path=/red/**
          filters:
            - StripPrefix=2
```

测试访问 `http://localhost:8081/red/blue/hello`，路径中的 `/red/blue` 会被截取掉，相当于访问 `/hello`

##### 4.2.2.6. RewritePath

`RewritePath GatewayFilter factory` 接收2个参数：路径的`正则表达式参数`和`替换参数字符串`。通过正则表达式来提供了一种灵活的方式来重写请求路径。以下`RewritePath GatewayFilter`配置示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: rewritepath_route
          uri: http://localhost:8001/hello
          predicates:
            - Path=/red/**
          filters:
            - RewritePath=/red(?<segment>/?.*), $\{segment}
```

以上示例是：访问 `http://localhost:8081/red/hello`，在请求相应下游服务前，将请求路径 `/red/hello` 重写成 `/hello`，相当于访问 `/hello`。<font color=red>**请注意，由于YAML规范，应将`$`替换为`$\`**</font>

#### 4.2.3. 重写转发路径(内置局部过滤器应用案例)

在 Spring Cloud Gateway 中，路由转发是直接将匹配的路由（path）直接拼接到映射路径（uri）之后，那么在微服务开发中一般会通过 `RewritePath` 机制来进行路径重写。

> `RewritePath` 过滤器的配置与使用详见前面章节

**案例改造**：

修改`12-springcloud-gateway`工程`application.yml`配置文件，将匹配路径`Path`改为 `/shop-service-product/**`。重新启动网关服务，在浏览器访问`http://127.0.0.1:8080/shop-service-product/product/1`时会抛出404。这是由于路由转发规则默认转发到商品微服务（`http://127.0.0.1:9001/shop-service-product/product/1`）路径上，而商品微服务又没有`shop-service-product`对应的映射配置。

在配置文件中添加`RewritePath`属性重写转发路径规则，通过RewritePath配置重写转发的url，将`/shop-service-product/(?.*)`，重写为`{segment}`，然后转发到订单微服务。比如在网页上请求`http://localhost:8080/shop-service-product/product`，此时会将请求转发到`http://127.0.0.1:9001/product/1`（**需要注意的是在yml格式中`$`要写成`$\`**）

```yml
spring:
  application:
    name: shop-server-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      # 配置路由（包含的元素：路由id、路由到微服务的uri，断言【判断条件】）
      routes:
        # 路由配置都是多个，所以此处是一个数组
        - id: shop-service-product # 路由id
          uri: lb://shop-service-product # 方式二：根据微服务名称从注册中心拉取服务的地址与端口，格式： lb://服务名称（服务在注册中心上注册的名称）
          predicates:
            - Path=/shop-service-product/**
          filters: # 配置路由过滤器
            # 配置路径重写的过滤器，通过正则表达式将 http://127.0.0.1:8080/shop-service-product/product/2 重写为 http://127.0.0.1:9001/product/2（注：在yml格式中，$ 需要写写成 $\）
            - RewritePath=/shop-service-product/(?<segment>.*), /$\{segment}
```

> <font color=red>*注：属性名称对大小写敏感，在做示例的就将`Path`属性写成`path`，结果后台一直报错说无法映射路径*</font>

### 4.3. 自定义局部过滤器

#### 4.3.1. 普通过滤器源码分析

![](images/571001915248969.png)

核心代码分析：

- 继承了 `AbstractNameValueGatewayFilterFactory`
- 过滤器的核心方法就是 `apply`，其参数为 `NameValueConfig`，可以方便的取得配置文件中指定的 name、value
- `exchange.getRequest().mutate()` 可以获取 request 进行修改
- `exchange.mutate()` 可以修改 exchange
- `chain.filter` 方法用于传递过滤器

#### 4.3.2. 自定义过滤器实现步骤

> 自定义局部过滤器与自定义路由断言工厂的步骤一样

案例需求：给请求增加一个是否开启日志记录的操作。具体步骤如下

- 修改网关项目的 application.yml 配置文件，添加自定义的局部过滤器：

```yml
server:
  port: 7000
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: service-product # 路由id
          uri: lb://service-product
          predicates: # 断言
            - Path=/api-product/**
          filters:
            - StripPrefix=1
            - Log=true,false # 自定义局部过滤器，具体示例实现是否开启日志记录
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 配置 Nacos server 的地址，将网关服务注册到 nacos 中
```

- 自定义一个自定义局部过滤器，实现相关功能处理逻辑。自定义局部过滤器实现需要满足以下两个要求：
    1. 类的名称必须是以`GatewayFilterFactory`结尾。即`自定义局部过滤器名称+GatewayFilterFactory`
    2. 类必须继承 `AbstractGatewayFilterFactory<配置类>` 抽象类

```java
// AbstractGatewayFilterFactory 的泛型 C 是一个配置类，配置类用于接收中在配置文件（properties/yml）中的配置值
@Component
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {

    // 构造函数，调用父类构造器，并传入配置类
    public LogGatewayFilterFactory() {
        super(LogGatewayFilterFactory.Config.class);
    }

    /**
     * 读取配置文件的中参数值，赋值到配置类中的属性上
     *
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("consoleLog", "cacheLog");
    }

    /**
     * 过滤器的核心处理逻辑
     *
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(LogGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {
            // 对配置的参数判断
            if (config.isCacheLog()) {
                System.out.println("cacheLog已经开启了....");
            }
            if (config.isConsoleLog()) {
                System.out.println("consoleLog已经开启了....");
            }
            // 传递过滤器链
            return chain.filter(exchange);
        };
    }

    /**
     * 配置类，用于接收在配置文件中的对应参数值
     */
    public static class Config {
        private boolean consoleLog;
        private boolean cacheLog;

        public Config() {
        }

        public boolean isConsoleLog() {
            return consoleLog;
        }

        public void setConsoleLog(boolean consoleLog) {
            this.consoleLog = consoleLog;
        }

        public boolean isCacheLog() {
            return cacheLog;
        }

        public void setCacheLog(boolean cacheLog) {
            this.cacheLog = cacheLog;
        }
    }
}
```

- 测试

![](images/20220105144335849_22685.png)

### 4.4. 全局过滤器

#### 4.4.1. 简介

全局过滤器（`GlobalFilter`）作用于所有路由，Spring Cloud Gateway 定义了`GlobalFilter`接口，也可以自定义实现自己的`GlobalFilter`。通过全局过滤器可以实现对权限的统一校验，安全性验证等功能，并且全局过滤器也是使用比较多的过滤器。

> Tips: 一般使用全局过滤器，在配置中并没有使用 filter，因为

#### 4.4.2. 内置全局过滤器（示例配置使用时再补充整理）

Spring Cloud Gateway 内部也是通过一系列的内置全局过滤器对整个路由转发进行处理如下：

![](images/20201030105155813_18563.png)

> 官方内置全局过滤器参考(2.2.5.RELEASE版本)： https://docs.spring.io/spring-cloud-gateway/docs/2.2.5.RELEASE/reference/html/#global-filters

##### 4.4.2.1. LoadBalancerClientFilter

LoadBalancerClientFilter 全局过滤器作用是：以负载均衡的方式获取实际的 uri 地址。

示例：

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: myRoute
          uri: lb://service
          predicates:
            - Path=/service/**
```

`LoadBalancerClientFilter` 发现配置的 uri 的前缀为 `lb` 的时候，就会使用 `LoadBalancerClient` 获取服务实例的 IP、port，替换为 uri，达到负载均衡的效果。

### 4.5. 自定义全局过滤器

Spring Cloud Gateway 内置的过滤器已经可以完成大部分的功能，但是对于企业开发的一些业务功能处理，还是需要自己编写过滤器来实现的，下面示例通过自定义一个全局过滤器，完成统一的权限校验。

#### 4.5.1. 全局过滤器源码分析

![](images/353623915236836.png)

核心代码分析：

- 实现了2个接口 `GlobalFilter`、`Ordered`
- `filter` 方法处理过滤逻辑
- `getOrder` 方法定义了此过滤器的优先级，数字越小，优先级越高
- `exchange` 用法与普通过滤器中介绍的一样

#### 4.5.2. 案例 - 统一鉴权逻辑

实现项目开发中的鉴权逻辑一般如下：

- 当客户端第一次请求服务时，服务端对用户进行信息认证（登录）
- 认证通过，将用户信息进行加密形成token，返回给客户端，作为登录凭证
- 以后每次请求，客户端都携带认证的token
- 服务端对token进行解密，判断是否有效。

![](images/20201030110827654_2019.png)

如上图所示，对于验证用户是否已经登录鉴权的过程可以在网关层统一检验。检验的标准就是请求中是否携带token凭证以及token的正确性。

#### 4.5.3. 案例实现

在`12-springcloud-gateway`工程，定义全局过滤器`AuthorizeFilter`，实现`GlobalFilter`与`Ordered`接口。主要逻辑是去校验所有请求的请求参数中是否包含“token”，如果不包含请求参数“token”则不转发路由，否则执行正常的逻辑。

```java
package com.moon.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Spring Cloud Gateway 自定义一个全局过滤器，需要实现 GlobalFilter, Ordered接口
 */
@Component // 要自定义全局过滤器生效，需要将全局过滤器注册到spring容器中
public class AuthorizeFilter implements GlobalFilter, Ordered {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeFilter.class);

    /**
     * 此方法是过滤器执行的主要逻辑
     *
     * @param exchange ServerWebExchange是当前请求和响应的上下文对象，存放着重要的请求-响应属性、请求实例和响应实例等等。(相当于zuul中的RequestContext)
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info("自定义全局过滤器AuthorizeFilter开始执行了....");
        // 通过ServerWebExchange对象可以获取请求与响应实例
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取请求头中access-token字段
        String token = request.getHeaders().getFirst("access-token");

        // 简单的模拟校验
        if (StringUtils.isEmpty(token)) {
            // 如果请求头不包含Authorization，则认证失败。记录一下日志
            LOGGER.error("请求{}, 登陆认证失败", request.getURI());
            // 设置响应状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 设置请求结束
            return response.setComplete();
        }

        // 如果认证通过，需要调用 chain.filter() 方法才继续向下游执行
        return chain.filter(exchange);
    }

    /**
     * 指定过滤器的执行顺序。返回值越小，执行优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

示例相关说明：

- 自定义全局过滤器需要实现`GlobalFilter`和`Ordered`接口
- `filter(ServerWebExchange exchange, GatewayFilterChain chain)`方法中完成过滤器的业务逻辑处理
- `getOrder()`方法用于指定此过滤器的优先级，返回值越大级别越低
- `ServerWebExchange` 就相当于当前请求和响应的上下文，存放着重要的请求-响应属性、请求实例和响应实例等等。一个请求中的`request`，`response`都可以通过 `ServerWebExchange` 获取
- 在过滤器的`filter()`方法中，如果要继续向下游执行，需调用`chain.filter()`方法进行放行

#### 4.5.4. 测试

测试在请求头中没有设置`access-token`，请求被拦截

![](images/20201030144343558_8781.png)

在请求头中设置`access-token`，请求成功

![](images/20201030144524165_16614.png)

## 5. 动态路由

### 5.1. 概述

路由规则是网关的核心内容，配置在应用的属性配置文件中，<font color=red>**启动的时候将路由规则加载到内存，属于静态路由方式**</font>。在高可靠架构中，网关服务都会部署多个实例，此时静态路由方式就出现不足，例如更新路由规则时，需要重启所有的网关服务实例，造成系统中断。

### 5.2. 采用 Nacos 实现动态路由的分析

#### 5.2.1. 动态路由实现思路分析

![](images/384182116249671.png)

#### 5.2.2. 源码分析

Gateway 提供了修改路由的接口 `RouteDefinitionWriter`, 有了这个接口就能动态修改路由。

```java
public interface RouteDefinitionWriter {

	Mono<Void> save(Mono<RouteDefinition> route);

	Mono<Void> delete(Mono<String> routeId);
}
```

该接口中定义两个方法，其中 `save` 方法用于保存路由规则，`delete` 方法用于删除路由规则。查询默认的实现类 `org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository`

```java
public class InMemoryRouteDefinitionRepository implements RouteDefinitionRepository {

	private final Map<String, RouteDefinition> routes = synchronizedMap(
			new LinkedHashMap<String, RouteDefinition>());

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return route.flatMap(r -> {
			if (StringUtils.isEmpty(r.getId())) {
				return Mono.error(new IllegalArgumentException("id may not be empty"));
			}
			routes.put(r.getId(), r);
			return Mono.empty();
		});
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			if (routes.containsKey(id)) {
				routes.remove(id);
				return Mono.empty();
			}
			return Mono.defer(() -> Mono.error(
					new NotFoundException("RouteDefinition not found: " + routeId)));
		});
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		return Flux.fromIterable(routes.values());
	}
}
```

可以看到，最终路由规则是保存到内存中 Map 集合里。

#### 5.2.3. 实现方案分析

![](images/268862216246226.png)

#### 5.2.4. 开发步骤分析

![](images/588212216241980.png)

### 5.3. 基于 Nacos 配置中心实现动态路由功能

> 示例代码详见 `spring-cloud-sample-gateway-nacos` 工程

#### 5.3.1. 引入依赖

在 gateway 服务工程中，引入相关依赖。*其中 Spring Boot Actuator 应用运行状态监控是为了方便观察配置是否动态修改成功*。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

#### 5.3.2. 项目配置

修改示例工程的 application.yml 配置文件，设置端点相关的配置，用于查询是否成功动态更新规则

```yml
server:
  port: 8081
spring:
  application:
    name: service-gateway
management:
  endpoints:
    web:
      exposure:
        include: '*'
  security:
    enabled: false
logging:
  level:
    root: debug
```

#### 5.3.3. 代码逻辑

创建一个事件发布类，实现 spring 的 `ApplicationEventPublisherAware` 接口，注入 `org.springframework.cloud.gateway.route.RouteDefinitionWriter` 路由操作接口，分别定义新增与删除规则的方法，在更新路由规则后，需要发布事件

```java
package com.moon.gateway.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MyRouteWriter implements ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 添加路由
     *
     * @param definition 路由配置信息
     * @return
     */
    public void add(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 更新路由
     *
     * @param definition 路由配置信息
     * @return
     */
    public void update(RouteDefinition definition) {
        try {
            // 根据 routeID 删除现有路由
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
            // 添加更新后的路由
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

创建 nacos 连接操作类，在服务启动后的读取路由配置，并且创建监听器，监听 nacos 路由配置的变化。

```java
package com.moon.gateway.dynamic;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class RouteNacos {

    /* 定义连接 Nacos 相关常量 */
    private final String NACOS_SERVER_ADDR = "localhost:8848";
    private final String NACOS_DATA_ID = "gateway-route";
    private final String NACOS_DATA_GROUP = "GATEWAY_GROUP";

    @Autowired
    private MyRouteWriter myRouteWriter;

    /**
     * 服务启动后的处理逻辑
     * 1. 对接 nacos
     * 2. 加载 nacos 已有路由配置
     * 3. 监听 nacos 路由配置的变化
     */
    @PostConstruct
    public void dynamicRoute() {
        try {
            // 1. 连接 nacos，构造 nacos config service
            ConfigService configService = NacosFactory.createConfigService(NACOS_SERVER_ADDR);

            // 2. 加载 nacos 已有路由信息，初始化路由配置
            String config = configService.getConfig(NACOS_DATA_ID, NACOS_DATA_GROUP, 5000);
            log.debug("---route config: {}", config);

            RouteDefinition routeDefinition = JSON.parseObject(config, RouteDefinition.class);

            myRouteWriter.add(routeDefinition);

            // 3. 设置 nacos 监听，配置变动后更新本地配置
            configService.addListener(NACOS_DATA_ID, NACOS_DATA_GROUP, new NacosRouteListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NacosRouteListener implements Listener {

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            log.debug("---route change: {}", configInfo);

            RouteDefinition routeDefinition = JSON.parseObject(configInfo, RouteDefinition.class);

            myRouteWriter.update(routeDefinition);
        }
    }
}
```

#### 5.3.4. 测试验证

1. Nacos 中创建路由配置，在【配置列表】中，创建新的配置

- Data ID: gateway-route
- Group: GATEWAY_GROUP
- 配置格式: 选择json
- 配置内容: 增加如下配置

```json
{
    "filters": [],
    "id": "baidu_route",
    "order": 10,
    "predicates": [
        {
            "args": { "pattern": "/baidu"},
            "name": "Path"
        }
    ],
    "uri": "http://wwww.baidu.com"
}
```

![](images/369552022230543.png)

2. 启动网关服务，应正确加载 Nacos 中的路由配置

![](images/421413222248969.png)

3. Nacos 中修改路由配置，应用能正确更新路由配置。可以访问状态监控 `http://127.0.0.1:8081/actuator/gateway/routes` 来观察是否修改成功

![](images/91003622236836.png)

## 6. 网关限流

### 6.1. 常见的限流算法

#### 6.1.1. 计数器限流算法

计数器限流算法是最简单的一种限流实现方式。其本质是通过维护一个单位时间内的计数器，每次请求计数器加1，当单位时间内计数器累加到大于设定的阈值，则之后的请求都被拒绝，直到单位时间已经过去，再将计数器重置为零

![](images/20201030151415332_9293.png)

#### 6.1.2. 漏桶算法

漏桶算法可以很好地限制容量池的大小，从而防止流量暴增。漏桶可以看作是一个带有常量服务时间的单服务器队列，如果漏桶（包缓存）溢出，那么数据包会被丢弃。在网络中，漏桶算法可以控制端口的流量输出速率，平滑网络上的突发流量，实现流量整形，从而为网络提供一个稳定的流量

![](images/20201030151518627_10542.png)

为了更好的控制流量，<font color=red>**漏桶算法需要通过两个变量进行控制：一个是桶的大小，支持流量突发增多时可以存多少的水（burst），另一个是水桶漏洞的大小（rate）**</font>

#### 6.1.3. 令牌桶算法

令牌桶算法是对漏桶算法的一种改进，桶算法能够限制请求调用的速率，而令牌桶算法能够在限制调用的平均速率的同时还允许一定程度的突发调用。

在令牌桶算法中，存在一个桶，用来存放固定数量的令牌。算法中存在一种机制，以一定的速率往桶中放令牌。每次请求调用需要先获取令牌，只有拿到令牌，才有机会继续执行，否则选择选择等待可用的令牌、或者直接拒绝。

放令牌这个动作是持续不断的进行，如果桶中令牌数达到上限，就丢弃令牌，所以就存在这种情况，桶中一直有大量的可用令牌，这时进来的请求就可以直接拿到令牌执行，比如设置qps为100，那么限流器初始化完成一秒后，桶中就已经有100个令牌了，这时服务还没完全启动好，等启动完成对外提供服务时，该限流器可以抵挡瞬时的100个请求。所以，只有桶中没有令牌时，请求才会进行等待，最后相当于以一定的速率执行

![](images/20201030152045455_1850.png)

### 6.2. 基于 Filter 的限流

Spring Cloud Gateway 官方就提供了基于令牌桶的限流支持。基于其内置的过滤器工厂 `RequestRateLimiterGatewayFilterFactory` 实现。在过滤器工厂中是通过Redis和lua脚本结合的方式进行流量控制。

```java
@ConfigurationProperties("spring.cloud.gateway.filter.request-rate-limiter")
public class RequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestRateLimiterGatewayFilterFactory.Config> {
    ....
}
```

#### 6.2.1. 环境准备

因为Spring Cloud Gateway的令牌桶限流是基于Redis和lua脚本实现的，所以需要准备redis服务端。*本示例项目使用windows版本的redis*

![](images/20201106162444138_8121.png)

打开redis-cli客户端，输入`monitor`命令，开启redis的监控功能

![](images/20201106162619033_23547.png)

#### 6.2.2. 添加 redis 的 reactive 依赖

在`shop-server-gateway`工程的pom文件中引入SpringBoot监控平台的起步依赖和redis的reactive依赖，代码如下：

```xml
<!-- 监控依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- redis的依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

#### 6.2.3. 修改 application.yml 配置文件

在`shop-server-gateway`工程的application.yml配置文件中加入限流的配置

```yml
spring:
  application:
    name: shop-server-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      # 配置路由（包含的元素：路由id、路由到微服务的uri，断言【判断条件】）
      routes:
        # 路由配置都是多个，所以此处是一个数组
        - id: shop-service-product # 路由id
          uri: lb://shop-service-product # 方式二：根据微服务名称从注册中心拉取服务的地址与端口，格式： lb://服务名称（服务在注册中心上注册的名称）
          predicates:
            # 注意此path属性与zuul的path属性不一样，zuul只会将/**部分拼接到uri后面，而gateway会将全部拼接到uri后面
            - Path=/shop-service-product/**
          filters: # 配置路由过滤器
            - name: RequestRateLimiter # 配置使用限流过滤器，是Spring Cloud Gateway提供的内置过滤器
              args:
                # 使用SpEL表达式，从spring容器中获取bean名称为keyResolver的对象，此对象就是KeyResolver接口的实例
                key-resolver: '#{@keyResolver}'
                # 令牌桶每秒填充平均速率，示例配置表示：每秒往令牌桶填充1个令牌
                redis-rate-limiter.replenishRate: 1
                # 令牌桶的上限（总容量），示例配置表示：令牌桶的总容量为3上令牌
                redis-rate-limiter.burstCapacity: 3
            # 配置路径重写的过滤器，通过正则表达式将 http://127.0.0.1:8080/shop-service-product/product/2 重写为 http://127.0.0.1:9001/product/2
            - RewritePath=/shop-service-product/(?<segment>.*), /$\{segment}
```

在 application.yml 中添加了redis的信息，并配置了`RequestRateLimiter`的限流过滤器，以下是配置参数的说明：

- `key-resolver`：用于配置提供用于限流的存储在redis的键的解析器的 Bean 对象的名字。它使用 SpEL 表达式根据`#{@beanName}`从 Spring 容器中获取 Bean 对象
- `redis-rate-limiter.replenishRate`：令牌桶每秒填充平均速率
- `redis-rate-limiter.burstCapacity`：令牌桶总容量

#### 6.2.4. 创建 KeyResolver 键解析器对象

为了达到不同的限流效果和规则，可以通过实现 `KeyResolver` 接口，定义不同请求类型的限流键

```java
package com.moon.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * KeyResolver配置类，创建 KeyResolver 接口实例，定义不同请求类型的限流键与规则
 */
@Configuration
public class KeyResolverConfiguration {
    /**
     * 基于请求路径的限流
     *
     * @return
     */
    @Bean("keyResolver")
    public KeyResolver pathKeyResolver() {
        // 示例：根据请求路径做限流依据（路径的值会作为redis的key）
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                return Mono.just(exchange.getRequest().getPath().toString());
            }
        };
    }

    /**
     * 基于请求参数的限流
     *
     * @return
     */
    // @Bean("keyResolver")
    public KeyResolver userKeyResolver() {
        // 示例：根据请求参数中的userId做限流依据（userId的值会作为redis的key）
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }

    /**
     * 基于请求ip地址的限流
     *
     * @return
     */
    // @Bean("keyResolver")
    public KeyResolver ipKeyResolver() {
        // 示例：根据请求ip做限流依据（ip的值会作为redis的key）
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));
    }
}
```

#### 6.2.5. 测试

使用Jmetter模拟5组线程访问

![](images/20201107103337876_2134.png)

![](images/20201107102546131_2240.png)

因为之前加了自定义过滤器进行权限校验，所以这里要加上请求头信息（*也可以将工程中的过滤注释掉*）

![](images/20201107103123860_4115.png)

结果如下，当达到令牌桶的总容量3时，其他的请求会返回429错误。

![](images/20201107103737641_18492.png)

通过reids的MONITOR可以监听redis的执行过程。这时候Redis中会有对应的数据：

![](images/20201107104108105_18731.png)

大括号中就是限流Key，这边是IP，本地的就是localhost

- `timestamp`：存储的是当前时间的秒数，也就是`System.currentTimeMillis()/1000`或者`Instant.now().getEpochSecond()`
- `tokens`：存储的是当前这秒钟的对应的可用的令牌数量

#### 6.2.6. 总结

Spring Cloud Gateway目前提供的限流还是相对比较简单的，在实际项目中限流策略会有很多种情况，比如：

- 对不同接口的限流
- 被限流后的友好提示

这些可以通过自定义 RedisRateLimiter 来实现自己的限流策略

### 6.3. 基于 Sentinel 的限流

#### 6.3.1. 概述

Sentinel 支持对 Spring Cloud Gateway、Zuul 等主流的 API Gateway 进行限流。

![](images/20201109140057733_8923.png)

##### 6.3.1.1. 资源维度

从 1.6.0 版本开始，Sentinel 提供了 Spring Cloud Gateway 的适配模块，可以提供两种资源维度：

- **route 维度**：即在 Spring 配置文件中配置的路由条目，资源名为对应的 routeId。例如下例中的 `product_route`：

```yml
routes:
  - id: product_route
    uri: http://localhost:8001/
    predicates:
      - Path=/product/**
```

- **自定义 API 维度**：用户可以利用 Sentinel 提供的 API 来自定义一些 API 分组。例如，请求 path 模式为 `/foo/**` 和 `/baz/**` 的都归到 `my_api` 这个 API 分组下面，限流的时候可以针对 `my_api` 进行限流。

##### 6.3.1.2. 集成 Sentinel 整体结构

![](images/497295207230544.png)

#### 6.3.2. 环境搭建

复用`spring-cloud-sample-gateway`工程的代码创建`spring-cloud-sample-gateway-sentinel`项目，移除不需要的依赖，导入 Sentinel 的相关依赖

1. 在父聚合项目中引入 Spring Cloud Alibaba 的依赖版本管理

```xml
<!-- Spring Cloud Alibaba 相应 Spring Cloud Greenwich 版本的依赖 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>2.1.2.RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. 在`shop-server-gateway`网关工程中引入sentinel的限流依赖

```xml
<!-- sentinel限流 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
</dependency>
```

> 注：也可以不引入`spring-cloud-alibaba-dependencies`的依赖，直接在gateway工程中依赖`sentinel-spring-cloud-gateway-adapter`，指定版本号即可（待测试！）

```java
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
    <version>1.7.1</version>
</dependency>
```

#### 6.3.3. 编写Sentinel的配置类

```java
package com.moon.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sentinel限流的配置类
 */
@Configuration
public class SentinelConfiguration {

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    /**
     * 构造方法，用于初始化 List<ViewResolver> 与 ServerCodecConfigurer
     *
     * @param viewResolversProvider
     * @param serverCodecConfigurer
     */
    public SentinelConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                 ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 配置限流的异常处理器: SentinelGatewayBlockExceptionHandler
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 配置初始化限流过滤器：GlobalFilter
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 配置初始化的限流参数，用于指定资源的限流规则，需要的配置项如下：
     * 1. 资源名称 (路由id)
     * 2. 配置统计时间
     * 3. 配置限流阈值
     */
    @PostConstruct
    public void initGatewayRules() {
        // 创建限流规则 GatewayFlowRule 实例的set集合（因为可以指定多个规则）
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("shop-service-product") // 指定限流的资源名称
                .setCount(1) // 设置限流的阈值
                .setIntervalSec(1)  // 设置统计时间，单位是秒，默认是 1 秒
        );
        // 添加限流规则到 GatewayRuleManager 管理器
        GatewayRuleManager.loadRules(rules);
    }
}
```

配置说明：

- 基于 Sentinel 的 Gateway 限流是通过Sentinel内置提供的`Filter`来完成的，使用时只需配置注入对应的 `SentinelGatewayFilter` 实例以及 `SentinelGatewayBlockExceptionHandler` 实例即可
- `@PostConstruct`注解定义初始化的加载方法，用于指定资源的限流规则。上面的示例的资源的名称为`shop-service-product`，统计时间是1秒内，限流阈值是1。表示每秒只能访问一个请求。

#### 6.3.4. 网关限流配置

修改 `shop-server-gateway` 的 `application.yml` 配置文件，删除基于 Spring Cloud Gateway 的 Filter 的限流配置，只保留路由断言与路由重写的配置即可。**注：路由ID需要与限流设置的一致**

```yml
server:
  port: 8080 # 项目端口
spring:
  application:
    name: shop-server-gateway # 服务名称
  cloud:
    # Spring Cloud Gateway 配置
    gateway:
      routes:
        - id: shop-service-product # 路由id
          uri: lb://shop-service-product # 方式二：根据微服务名称从注册中心拉取服务的地址与端口，格式： lb://服务名称（服务在注册中心上注册的名称）
          predicates:
            - Path=/shop-service-product/**
          filters: # 配置路由过滤器
            - RewritePath=/shop-service-product/(?<segment>.*), /$\{segment}
```

#### 6.3.5. 测试

在一秒钟内多次访问`http://127.0.0.1:8080/shop-service-product/product/2`，就可以看到限流生效了。

![](images/20201111171238326_12649.png)

#### 6.3.6. 自定义异常提示

当触发限流后页面显示的是`Blocked by Sentinel: FlowException`。为了展示更加友好的限流提示，Sentinel支持自定义异常处理。只需要在`GatewayCallbackManager`的静态方法`setBlockHandler`注册回调中进行定制即可：

```java
public final class GatewayCallbackManager {
    // .....
    public static void setBlockHandler(BlockRequestHandler blockHandler){
        AssertUtil.notNull(blockHandler, "blockHandler cannot be null");
        GatewayCallbackManager.blockHandler = blockHandler;
    }
    // .....
}
```

静态方法`setBlockHandler`：是注册函数用于实现自定义的逻辑处理被限流的请求，对应接口为`BlockRequestHandler`。默认实现为 `DefaultBlockRequestHandler` ，当被限流时会返回类似于下面的错误信息：`Blocked by Sentinel: FlowException`。

在`shop-server-gateway`工程的`SentinelConfiguration`配置类中，增加初始化后执行的方法，注册自定义异常处理逻辑

```java
/**
 * 自定义限流处理器，用于定制异常处理的逻辑
 */
@PostConstruct
public void initBlockHandlers() {
    GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("code", -1);
        map.put("message", "不好意思,限流啦");
        // 通过 serverWebExchange 上下文对象，设置相应的响应内容
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(map));
    });
}
```

测试结果

![](images/20201112083546595_6059.png)

#### 6.3.7. 参数限流

以上的配置都是针对整个路由来限流的，也可以通过使用参数限流方式，针对某个路由的某个参数做限流。具体的实现是：在配置限流参数`GatewayFlowRule`时，增加对特定的参数限制规则`setParamItem`即可

```java
@PostConstruct
public void initGatewayRules() {
    // 创建限流规则 GatewayFlowRule 实例的set集合（因为可以指定多个规则）
    Set<GatewayFlowRule> rules = new HashSet<>();
    rules.add(new GatewayFlowRule("shop-service-product") // 指定限流的资源名称
            .setCount(1) // 设置限流的阈值
            .setIntervalSec(1)  // 设置统计时间，单位是秒，默认是 1 秒
            .setParamItem(new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                    .setFieldName("id")) // 指定参数限流，示例是通过指定PARAM_PARSE_STRATEGY_URL_PARAM表示从url中获取参数，setFieldName指定参数名称
    );
    // 添加限流规则到 GatewayRuleManager 管理器
    GatewayRuleManager.loadRules(rules);
}
```

#### 6.3.8. 自定义 API 分组

自定义API分组的限流规则，就是用户定义针对不同的请求实现限流的规则，是一种更细粒度的限流规则定义。*示例实现的限流效果与上面一样*
+
- 在商品微服务定义以下测试接口

![](images/20220105164856178_27240.png)

- 以下代码是 `spring-cloud-alibaba-2.1.x-sample` 项目的示例代码。

```java
/**
 * 配置初始化的限流参数，用于指定资源的限流规则，需要的配置项如下：
 * 1. 资源名称 (路由id)
 * 2. 配置统计时间
 * 3. 配置限流阈值
 */
@PostConstruct
public void initGatewayRules() {
    // 创建限流规则 GatewayFlowRule 实例的 set 集合（因为可以指定多个规则）
    Set<GatewayFlowRule> rules = new HashSet<>();
    /* 创建以下自定义的API限流分组规则，并注册到限流规则管理器中 */
    rules.add(new GatewayFlowRule("product_group1").setCount(1).setIntervalSec(1));
    rules.add(new GatewayFlowRule("product_group2").setCount(1).setIntervalSec(1));
    // 添加限流规则到 GatewayRuleManager 管理器
    GatewayRuleManager.loadRules(rules);
}

/*
 * 自定义API限流分组，
 *      1.定义分组
 *      2.对小组配置限流规则
 */
@PostConstruct
private void initCustomizedApis() {
    Set<ApiDefinition> definitions = new HashSet<>();
    ApiDefinition api1 = new ApiDefinition("product_group1")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/api-product/product/group1/**"). // 以 /product/group1/ 开头都的所有url
                        setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
            }});
    ApiDefinition api2 = new ApiDefinition("product_group2")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/api-product/product/group2/flowlimit1")); // 完全匹配 /product/group2/flowlimit1 的url
            }});
    definitions.add(api1);
    definitions.add(api2);
    // 添加到 GatewayApiDefinitionManager 接口定义管理器
    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
}
```

- 以下代码 `spring-cloud-greenwich-sample` 项目的示例代码。

```java
@PostConstruct
public void initGatewayRules() {
    // 创建限流规则 GatewayFlowRule 实例的set集合（因为可以指定多个规则）
    Set<GatewayFlowRule> rules = new HashSet<>();
    // 创建以下自定义的API限流分组规则，并注册到限流规则管理器中
    rules.add(new GatewayFlowRule("product_api").setCount(1).setIntervalSec(1));
    // 添加限流规则到 GatewayRuleManager 管理器
    GatewayRuleManager.loadRules(rules);
}

/*
 * 自定义API限流分组，
 *      1.定义分组
 *      2.对小组配置限流规则
 */
@PostConstruct
private void initCustomizedApis() {
    Set<ApiDefinition> definitions = new HashSet<>();
    ApiDefinition api1 = new ApiDefinition("product_api")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/shop-service-product/product/**"). // 以 /shop-service-product/product/ 开头都的所有url
                        setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
            }});
    ApiDefinition api2 = new ApiDefinition("order_api")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/shop-service-order/order")); // 完全匹配 /shop-service-order/order 的url
            }});
    definitions.add(api1);
    definitions.add(api2);
    // 添加到 GatewayApiDefinitionManager 接口定义管理器
    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
}
```

## 7. 网关高可用

**高可用HA**（High Availability）是分布式系统架构设计中必须考虑的因素之一，它通常是指，通过设计减少系统不能提供服务的时间。单点服务设计往往是系统高可用最大的风险点，应该尽量在系统设计的过程中避免单点服务设计。方法论上，高可用保证的原则是“集群化”，或者叫“冗余”：只有一个单点，挂掉后整个服务会受影响；如果有冗余备份，挂了还有其他备用节点能够顶上。

![](images/20201109171300708_23038.png)

实际使用 Spring Cloud Gateway 的方式如上图，同时启动多个 Gateway 实例进行负载，不同的客户端使用不同的负载将请求分发到后端的 Gateway 服务，Gateway 再通过HTTP调用后端服务，最后对外输出。因此为了保证 Gateway 的高可用性，可以请求到达 Gateway 前的使用 Nginx 或者 F5 进行负载转发以达到高可用性。

### 7.1. 配置多个Gateway工程

修改`13-springcloud-gateway-sentinel`工程`shop-server-gateway`的application.yml配置文件，配置通过参数指定项目的端口号：

```java
server:
  port: ${PORT:8080} # 项目端口
```

通过配置不同的`PORT`参数，启动多个网关服务，请求端口分别为8080和8081。浏览器验证发现效果是一致的

### 7.2. 配置nginx

修改nginx配置文件，`nginx-1.18.0\conf\nginx.conf`，添加以下配置

```
# 配置多台服务器（这里只在一台服务器上的不同端口）
upstream gateway {
    server 127.0.0.1:8081;
    server 127.0.0.1:8080;
}
# 请求转向gateway 定义的服务器列表
location / {
    proxy_pass http://gateway;
}
```

在浏览器上通过访问`http://127.0.0.1/shop-service-product/product/2`请求的效果和之前是一样的。关闭一台网关服务器，还是可以支持部分请求的访问。

## 8. Spring Cloud Gateway 执行流程分析

![](images/20201112100011250_7292.png)

Spring Cloud Gateway 核心处理流程如上图所示

1. Gateway 的客户端向 Spring Cloud Gateway 发送请求，请求首先被 `HttpWebHandlerAdapter` 进行提取组装成网关上下文，然后网关的上下文会传递到`DispatcherHandler`。
2. `DispatcherHandler` 是所有请求的分发处理器，`DispatcherHandler`主要负责分发请求对应的处理器。比如请求分发到对应的 `RoutePredicateHandlerMapping` （路由断言处理映射器）。
3. `RoutePredicateHandlerMapping` 路由断言处理映射器主要作用用于路由查找，根据路由断言判断路由是否可用，以及找到路由后返回对应的`FilterWebHandler`。
4. `FilterWebHandler` 主要负责组装 Filter 链，先调用执行一系列的 PreFilter 处理，然后再把请求转到后端对应的代理服务处理，处理完毕之后再执行一系列的 Post Filter，最后将`Response`返回到 Gateway 客户端。
