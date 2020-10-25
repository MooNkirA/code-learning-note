# Spring Cloud Gateway 微服务网关

Spring Cloud Netflix Zuul 1.x 是一个基于阻塞 IO 的 API Gateway 以及 Servlet；直到2018年5月，Zuul 2.x（基于Netty，也是非阻塞的，支持长连接）才发布，但 Spring Cloud 暂时还没有整合计划。Spring Cloud Gateway 比 Zuul 1.x 系列的性能和功能整体要好。

## 1. Spring Cloud Gateway 简介

### 1.1. 简介

Spring Cloud Gateway 是 Spring 官方基于 Spring 5.0，Spring Boot 2.0 和 Project Reactor 等技术开
发的网关，旨在为微服务架构提供一种简单而有效的统一的 API 路由管理方式，统一访问接口。Spring
Cloud Gateway 作为 Spring Cloud 生态系中的网关，目标是替代 Netflix ZUUL，其不仅提供统一的路
由方式，并且基于 Filter 链的方式提供了网关基本的功能，例如：安全，监控/埋点，和限流等。它是基
于Nttey的响应式开发模式。

> Spring Cloud Gateway官方文档：https://spring.io/projects/spring-cloud-gateway#overview

|         组件         | RPS(request per second) |
| -------------------- | ----------------------- |
| Spring Cloud Gateway | Requests/sec: 32213.38  |
| Zuul 1.x             | Requests/sec: 20800.13  |

上表为Spring Cloud Gateway与Zuul的性能对比，从结果可知，Spring Cloud Gateway的RPS是Zuul的1.6倍

### 1.2. 核心概念

![](images/20201024155248894_3162.png)

- **路由（route）**：路由是网关最基础的部分，路由信息由一个ID、一个目的URL、一组断言工厂和一组Filter组成。如果断言为真，则说明请求URL和配置的路由匹配。
- **断言（predicates）**：Java8中的断言函数，Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架中的`ServerWebExchange`。Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自Http Request中的任何信息，比如请求头和参数等。
- **过滤器（filter）**：一个标准的Spring WebFilter，Spring Cloud Gateway中的Filter分为两种类型，分别是`Gateway Filter`和`Global Filter`。过滤器`Filter`可以对请求和响应进行处理。

## 2. Spring Cloud Gateway 基础入门案例

> 复用`11-springcloud-zuul`工程的代码，删除zuul网关工程，创建`12-springcloud-gateway`

### 2.1. 创建工程导入依赖

在`12-springcloud-gateway`项目中添加新的模块`shop-server-gateway`，并导入依赖

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

### 2.2. 配置启动类

```java
@SpringBootApplication
public class GatewayServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
```

> *注：Spring Cloud Gateway 组件不需要配置任何注解即可开启*

### 2.3. 配置路由

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

## 3. 路由配置规则

### 3.1. 路由断言功能

Spring Cloud Gateway 的功能很强大，其内置了很多 `Predicates` 功能。在 Spring Cloud Gateway 中 Spring 利用 Predicate 的特性实现了各种路由匹配规则，可以通过`Header`、请求参数等不同的条件来进行作为条件匹配到对应的路由。

![](images/20201025121213735_25442.png)

#### 3.1.1. 路由断言 - After

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

`Header`的路由判断规则，用于Header匹配，header名称匹配`X-Request-Id`，且正则表达式匹配`\d+`

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

#### 3.1.6. 路由断言 - Host

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

`Method`的路由判断规则，用于请求Method匹配，匹配的是请求的HTTP方法

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

`Path`的路由判断规则，用于请求url匹配，`{segment}`为可变参数

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

`Query`的路由判断规则，用于匹配请求参数，将请求的参数`param(green)`进行匹配，也可以进行regexp正则表达式匹配 (参数包含`red`,并且`red`的值匹配`green`或者`greet`都可以 )

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
        # 请求包含red的请求参数，并且值为gree时，匹配路由；如进行regexp正则表达式匹配，则geeen
        #- Query=red, gree.
```

#### 3.1.10. 路由断言 - RemoteAddr

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

以上示例配置表示：会将大约80％的流量转发到weighthigh.org，将大约20％的流量转发到weightlow.org。














### 3.2. 动态路由



### 3.3. 重写转发路径










