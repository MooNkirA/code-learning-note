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








