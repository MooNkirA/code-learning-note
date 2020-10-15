# Spring Cloud Netflix

**Spring Cloud Netflix组件**包括了Eureka、Ribbon、Feign、Hystrix、Zuul

# Eureka 服务注册和发现

> **Eureka案例代码地址：`spring-cloud-note\spring-cloud-greenwich-sample\02-springcloud-eureka\`**

## 1. Eureka 注册中心

Eureka是Netflix开发的服务发现框架，SpringCloud将它集成在自己的子项目spring-cloud-netflix中，实现SpringCloud的服务发现功能。Spring Cloud提供了多种注册中心的支持，如：Eureka、ZooKeeper等。推荐使用Eureka。

### 1.1. Eureka 的基本架构

![](images/20201008123337857_27461.png)

上图简要描述了Eureka的基本架构，由3个角色组成：

- `Eureka Server`：提供服务注册和发现
- `Service Provider`：服务提供者，将自身服务注册到Eureka，使服务消费方能够找到
- `Service Consumer`：服务消费者，从Eureka获取注册服务列表，消费服务

### 1.2. Eureka 的交互流程与原理

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

## 2. Eureka 注册中心使用示例

> 案例代码：spring-cloud-note\spring-cloud-greenwich-sample\02-springcloud-eureka\
>
> 沿用上面`01-microservice-no-springcloud`项目的代码

### 2.1. 引入 Spring Cloud 依赖

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

### 2.2. 搭建Eureka注册中心(单节点版)

#### 2.2.1. 搭建Eureka服务中心

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

#### 2.2.2. 服务注册中心管理后台

访问`http://localhost:8761`即可进入EurekaServer内置的管理控制台

![](images/20201009103306998_27461.png)

### 2.3. 服务注册到Eureka注册中心

#### 2.3.1. 商品服务注册

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

#### 2.3.2. 订单服务注册

和商品微服务一样，只需要引入坐标依赖，在工程的 `application.yml` 中添加Eureka Server的主机地址即可

#### 2.3.3. @EnableDiscoveryClient 与 @EnableEurekaClient 的区别

两个注解的用法上基本一致。

1. `@EnableDiscoveryClient`注解是基于spring-cloud-commons依赖，并且在classpath中实现；
2. `@EnableEurekaClient`注解是基于spring-cloud-netflix依赖，只能为eureka作用；
3. 更简单来说，就是如果选用的注册中心是eureka，那么就推荐`@EnableEurekaClient`，如果是其他的注册中心，那么推荐使用`@EnableDiscoveryClient`。

如果你的classpath中添加了eureka，则它们的作用是一样的。

注解`@EnableEurekaClient`上有`@EnableDiscoveryClient`注解，可以说基本就是`@EnableEurekaClient`有`@EnableDiscoveryClient`的功能，另外上面的注释中提到，其实`@EnableEurekaClient`注解就是一种方便使用eureka的注解而已，可以说使用其他的注册中心后，都可以使用`@EnableDiscoveryClient`注解，但是使用`@EnableEurekaClient`的情景，就是在服务采用eureka作为注册中心的时候，使用场景较为单一

## 3. Eureka的自我保护模式

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

## 4. Eureka中的元数据

### 4.1. 概念

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

### 4.2. 通过Eureka的元数据实现服务调用

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

## 5. Eureka Server 高可用集群

在单节点的Eureka Server的服务中，Eureka Client会定时连接Eureka Server，获取注册表中的信息并缓存到本地。微服务在消费远程API时总是使用本地缓存中的数据。因此一般来说，即使Eureka Server发生宕机，也不会影响到服务之间的调用。

但如果EurekaServer宕机时，某些微服务也出现了不可用的情况，Eureka Server中的缓存若不被刷新，就可能会影响到微服务的调用，甚至影响到整个应用系统的高可用。因此，在生成环境中，通常会部署一个高可用的Eureka Server集群。

![](images/20201009225810260_24479.png)

Eureka Server可以通过运行多个实例并相互注册的方式实现高可用部署，Eureka Server实例会彼此增量地同步信息，从而确保所有节点数据一致。事实上，节点之间相互注册是Eureka Server的默认行为

### 5.1. 搭建 Eureka Server 高可用集群

> 复用`02-springcloud-eureka`工程的代码，在原有基础上增加集群部分配置。详细示例详见`spring-cloud-note\spring-cloud-greenwich-sample\03-springcloud-eureka-cluster\`

*注：使用idea开启多个服务的方法有多种，下面是通过定义不同的profiles的方式，还有通过启动多个实例，直接修改配置文件；还有通过启动时定义配置文件的参数方式，详细参考学成项目的配置方式*

#### 5.1.1. 修改相关配置

1. 修改本机host属性

Eureka配置server集群时需要执行host地址，为了方便配置，增加本地ip与域名的映射

```
127.0.0.1  eureka01
127.0.0.1  eureka02
```

2. 修改 `shop-server-eureka` 工程中的yml配置文件，添加如下配置属性

```yml
spring:
  application:
    name: shop-server-eureka # 服务名称
# 定义两个不同环境的profiles配置，模拟两个EurekaServer，端口号分别是8001、8002。两个EurekaServer相应注册
---
# EurekaServer01 配置
spring:
  profiles: eureka01
server:
  port: 8001 # 项目端口
eureka:
  instance:
    hostname: eureka01 # 配置域名
  client:
    # 是否将自己注册到Eureka服务中，eureka集群需要注册，默认为true，因此可以省略不写（注：也可以写成驼峰命名registerWithEureka）
    register-with-eureka: true
    # 服务发现，是否从Eureka中获取注册信息。eureka集群需要相互间获取注册信息，所在设置为true，默认值为true，因此可以省略不写
    fetch-registry: true
    service-url:
      # Eureka客户端与Eureka服务端的交互地址，高可用状态配置对方的地址，可以多个eureka server，多个eureka server之间用,隔开
      defaultZone: http://eureka02:8002/eureka/
---
# EurekaServer02 配置
spring:
  profiles: eureka02
server:
  port: 8002 # 项目端口
eureka:
  instance:
    hostname: eureka02 # 配置域名
  client:
    # 是否将自己注册到Eureka服务中，eureka集群需要注册，默认为true，因此可以省略不写
    # register-with-eureka: true
    # 服务发现，是否从Eureka中获取注册信息。eureka集群需要相互间获取注册信息，所在设置为true，默认值为true，因此可以省略不写
    # fetch-registry: true
    service-url:
      # Eureka客户端与Eureka服务端的交互地址，高可用状态配置对方的地址，可以多个eureka server，多个eureka server之间用,隔开
      defaultZone: http://eureka01:8001/eureka/
```

<font color=purple>**说明：在配置文件中通过连字符（`---`）将文件分为三个部分，第一部分为应用名称，第二部分和第三部分是根据不同的`profiles`选项动态添加，可以在IDEA启动时进行激活配置**</font>

#### 5.1.2. 启动服务

使用IDEA启动两次`EurekaServerApplicaion`分别激活`eureka01`和`eureka02`配置

![](images/20201009234838757_27999.png)

访问`http://eureka01:8001`和`http://eureka02:8002/`。此时注册中心 SHOP-SERVER-EUREKA 应用已经有两个节点，并且 `registered-replicas` (相邻集群复制节点)中已经包含其他节点

![](images/20201009235133859_20051.png)

### 5.2. 服务注册到Eureka Server集群

如果需要将微服务注册到Eureka Server集群，只需要修改yml配置文件中的`eureka.client.service-url.defaultZone`属性，指定集群各个Eureka Server的地址，多个地址中间用“,”分隔

```yml
# Eureka 客户端配置
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    instance-id: ${spring.application.name}:${server.port}  # 指定实例id
  client:
    # Eureka server 地址，多个eureka server之间用,隔开
    service-url:
      defaultZone: http://eureka01:8001/eureka/,http://eureka02:8002/eureka/
```

## 6. Eureka中的常见细节问题

### 6.1. 服务注册慢

默认情况下，服务注册到Eureka Server的过程较慢。SpringCloud官方文档中给出了详细的原因：

大致含义：服务的注册涉及到心跳，默认心跳间隔为30s。在实例、服务器、客户端都在本地缓存中具有相同的元数据之前，服务不可用于客户端发现（所以可能需要3次心跳）。可以通过配置`eureka.instance.leaseRenewalIntervalInSeconds` (心跳频率)加快客户端连接到其他服务的过程。在生产中，最好使用默认值，因为在服务器内部有一些计算，官方开发团队对续约做出假设。

### 6.2. 监控页面显示服务的ip

在Eureka Server的管控台中，显示的服务实例名称默认情况下是微服务定义的名称和端口。为了更好的对所有服务进行定位，微服务注册到Eureka Server的时候可以手动配置示例ID。

通过修改`eureka.instance.instance-id`属性指定显示的名称，使用el表达式`spring.cloud.client.ip-address`可以获取当前客户端的ip地址，配置方式如下：

```yml
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    # instance-id: ${spring.application.name}:${server.port}  # 指定实例id，这里只是显示服务名称+端口
    instance-id: ${spring.cloud.client.ip-address}:${server.port}  # 通过 spring.cloud.client.ip-address 可以获取ip地址
```

![](images/20201010094344963_13130.png)

### 6.3. 服务节点剔除问题

默认情况下，由于Eureka Server剔除失效服务间隔时间为90s且存在自我保护的机制。所以不能有效而迅速的剔除失效节点，开发或测试时希望可以马上剔除不用的服务。解决方案如下：

- Eureka Server：配置关闭自我保护，设置剔除无效节点的时间间隔

```yml
eureka:
  instance:
    hostname: eureka01 # 配置域名
  client:
    service-url:
      defaultZone: http://eureka02:8002/eureka/
  server:
    enable-self-preservation: false # 关闭自我保护(注：一般只用于开发与测试阶段，生产部署还是开启比较好)
    eviction-interval-timer-in-ms: 4000 # 剔除时间间隔,单位:毫秒
```

- Eureka Client：配置开启健康检查，并设置续约时间

```yml
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    instance-id: ${spring.cloud.client.ip-address}:${server.port}  # 通过 spring.cloud.client.ip-address 可以获取ip地址
    lease-renewal-interval-in-seconds: 5 # 发送心跳续约间隔时间
    lease-expiration-duration-in-seconds: 10 # eureka client发送心跳给server端后，续约到期时间（默认90秒）
  client:
    # healthcheck: true # 开启健康检查(需要依赖spring-boot-actuator)
    service-url:
      defaultZone: http://eureka01:8001/eureka/,http://eureka02:8002/eureka/
```

### 6.4. 解决响应为xml格式的问题

有些版本会出现请求响应返回数据会变成xml格式。

- **原因**：引入了eureka server的依赖，导致破坏了之前SpringMVC默认的配置，从而导致了响应成了xml
- **解决方法**：在order工程中pom.xml，排除eureka server中的xml依赖

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

### 6.5. 为Eureka添加用户认证

#### 6.5.1. Eureka服务端配置用户认证

添加用户认证，即需要密码才能访问查询注册中心的信息

1. 为shop-server-eureka添加安全认证依赖

```xml
<!-- eureka安全认证依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2. 修改application.yml配置文件，增加开启基于HTTP basic的认证部分配置

```yml
spring:
  application:
    name: shop-server-eureka # 服务名称
  # 开启基于HTTP basic的认证
  security:
    basic:
      enabled: true
    # 配置用户的账号信息
    user:
      name: moon
      password: moon
server:
  port: 8001 # 项目端口
eureka:
  instance:
    hostname: eureka01 # 配置域名
  client:
    # 是否将自己注册到Eureka服务中，eureka集群需要注册，默认为true，因此可以省略不写（注：也可以写成驼峰命名registerWithEureka）
    register-with-eureka: true
    # 服务发现，是否从Eureka中获取注册信息。eureka集群需要相互间获取注册信息，所在设置为true，默认值为true，因此可以省略不写
    fetch-registry: true
    service-url:
      # Eureka客户端与Eureka服务端的交互地址，高可用状态配置对方的地址，可以多个eureka server，多个eureka server之间用,隔开
      defaultZone: http://eureka02:8002/eureka/
  server:
    enable-self-preservation: false # 关闭自我保护(注：一般只用于开发与测试阶段，生产部署还是开启比较好)
    eviction-interval-timer-in-ms: 4000 # 剔除时间间隔,单位:毫秒
```

3. 重新启动Eureka服务进行测试，输入正确的用户名密码即可登录

**注：服务提供者注册到Eureka时会报错，需要在服务注册时也设置用户名和密码**

```cmd
2018-10-01 07:24:51.730  INFO 3652 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SPRINGCLOUD-MICROSERVICE-ITEM/localhost:springcloud-microservice-item:8081: registering service...
2018-10-01 07:24:51.745  WARN 3652 --- [nfoReplicator-0] c.n.d.s.t.d.RetryableEurekaHttpClient    : Request execution failure with status code 401; retrying on another server if available
2018-10-01 07:24:51.745  WARN 3652 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SPRINGCLOUD-MICROSERVICE-ITEM/localhost:springcloud-microservice-item:8081 - registration failed Cannot execute request on any known server
```

#### 6.5.2. 服务提供者注册时设置账户信息

服务注册到有认证需求的注册中心时，需要设置如下信息

例子：`http://USER:PASSWORD@127.0.0.1:8001/eureka/`，将原来`eureka.client.serviceUrl.defaultZone`的配置增加用户与密码，与服务配置的密码一致

```yml
# 配置注册eureka服务
eureka:
  client:
    registerWithEureka: true  # 是否将自己注册到Eureka服务中，默认为true
    fetchRegistry: true # 是否从Eureka中获取注册信息，默认为true
    serviceUrl: # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://moon:moon@127.0.0.1:8001/eureka/
  instance:
    prefer-ip-address: true # 将自己的ip地址注册到Eureka服务中
```

## 7. Eureka源码解析

### 7.1. SpringBoot 中的自动装载原理

#### 7.1.1. ImportSelector 接口

`ImportSelector`接口是Spring导入外部配置的核心接口，在SpringBoot的自动化配置和`@EnableXxx`(功能性注解)中起到了决定性的作用。当在`@Configuration`标注的Class上使用`@Import`引入了一个`ImportSelector`实现类后，会把实现类中定义所有标识`@Bean`注解的方法所返回的类实例都注册到Spring容器中。

```java
public interface ImportSelector {
	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);
}
```

`DeferredImportSelector`接口继承`ImportSelector`，它和`ImportSelector`的区别在于装载bean的时机上，`DeferredImportSelector`需要等所有的`@Configuration`都执行完毕后才会进行装载

```java
public interface DeferredImportSelector extends ImportSelector
```

而SpringBoot的`@EnableAutoConfiguration`注解中`@Import`引入的`AutoConfigurationImportSelector`就是实现了`DeferredImportSelector`接口

#### 7.1.2. ImportSelector 基础使用示例

1. 定义Bean对象

```java
@Data
public class User {
    private String name;
    private Integer age;
}
```

2. 定义配置类，不标识任何spring注解

```java
/**
 * 配置类，注意：此类不标识@Component、@Service、@Repository、@Controller等注解，
 * spring扫描的时候并不会装载该类，待使用@Import注解引入一个ImportSelector接口实现类，在实现类中处理注册到容器中
 * 注意事项：实现了ImportSelector接口的类不会被解析成一个Bean注册到容器中，只会将里面标识的@Bean注解的方法创建实例注册到容器
 */
public class UserConfiguration {
    /* 创建User实例 */
    @Bean
    public User getUser() {
        User user = new User();
        user.setAge(12);
        user.setName("石原里美");
        return user;
    }
}
```

3. 定义`ImportSelector`接口的实现类

```java
public class UserImportSelector implements ImportSelector {
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 返回需要加载的配置类名称数组，此示例直接返回UserConfiguration类全限定名
        return new String[]{UserConfiguration.class.getName()};
    }
}
```

4. 定义`EnableXxx`注解，并使用`@Import`注解引入自定义的`ImportSelector`接口的实现类

```java
/**
 * 自定义注解，使用@Import注解引入ImportSelector接口实现类
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(UserImportSelector.class)
public @interface EnableUserBean {}
```

5. 测试

```java
@EnableUserBean
public class ImportSelectorTest {
    public static void main(String[] args) {
        /*
         * 创建注解扫描容器，执行流程如下：
         *  创建注解扫描容器 --> 扫描到@EnableUserBean注解 --> 通过注解上@Import注解找到ImportSelector实现类UserImportSelector
         *  --> 调用ImportSelector接口实现的selectImports方法，返回需要注册到容器的类全限定名称数组 --> 注册UserConfiguration类实例到容器
         *  --> 通过@Bean注解将User类实例注册到容器
         */
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportSelectorTest.class);
        // 从容器获取User实例
        User user = context.getBean(User.class);
        System.out.println(user);
        // UserConfiguration实例有注册到spring容器中
        UserConfiguration config = context.getBean(UserConfiguration.class);
        System.out.println(config);
        // 此处会报错：No qualifying bean of type 'com.moon.importselector.UserImportSelector' available
        // 说明实现了ImportSelector接口或者ImportBeanDefinitionRegistrar接口的类不会被解析成一个Bean注册到容器中
        UserImportSelector importSelector = context.getBean(UserImportSelector.class);
        System.out.println(importSelector);
    }
}
```

> 示例结论：`UserConfiguration`类并没有使用Spring的相关的对象创建注解声明（`@Controller`，`@Service`，`@Repostiroty`等），而是使用编程的方式动态的载入bean

#### 7.1.3. ImportSelector 接口的调用时机

`ConfigurationClassParser`类的`processImports`方法中进行`ImportSelector`接口的处理

![](images/20201011114217359_5447.png)

分析源码可以看到，`ImportSelector`接口的返回值会递归进行解析，把解析到的类全名按照`@Configuration`进行处理

#### 7.1.4. springBoot自动装载的实现

SpringBoot开箱即用的特点（自动装载机制），是基于`ImportSelector`实现。

1. 在SpringBoot启动类中配置的主要注解`@SpringBootApplication`，在此注解中声明了一个`@EnableAutoConfiguration`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
}
```

2. 在`@EnableAutoConfiguration`中通过`@Import`引入了SpringBoot定义的`AutoConfigurationImportSelector`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
}
```

3. `AutoConfigurationImportSelector`会执行里面的`selectImports`方法

```java
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
		ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
    // ....省略代码
    @Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return NO_IMPORTS;
		}
		AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
				.loadMetadata(this.beanClassLoader);
		// 此方法是主要处理逻辑，获取需要加载的bean全限定名集合
		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(autoConfigurationMetadata,
				annotationMetadata);
		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
	}

	protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata,
			AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return EMPTY_ENTRY;
		}
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
		// 通过getCandidateConfigurations方法获取所有需要加载的bean全限定名集合
		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
		// 去重处理
		configurations = removeDuplicates(configurations);
		// 获取不需要加载的bean,这里我们可以通过spring.autoconfigure.exclude人为配置
		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
		checkExcludedClasses(configurations, exclusions);
		configurations.removeAll(exclusions);
		configurations = filter(configurations, autoConfigurationMetadata);
		// 发送事件，通知所有的AutoConfigurationImportListener进行监听
		fireAutoConfigurationImportEvents(configurations, exclusions);
		return new AutoConfigurationEntry(configurations, exclusions);
	}

    // 这里是获取bean渠道的地方，重点是SpringFactoriesLoader#loadFactoryNames方法
	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		// 此处的getSpringFactoriesLoaderFactoryClass()最终返回EnableAutoConfiguration.class
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
				getBeanClassLoader());
		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
				+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
	// ....省略代码
}
```

`SpringFactoriesLoader.loadFactoryNames`方法，会读取相应jar定义的`META-INF/spring.factories`，jar被加载的同时`spring.factories`里面定义的bean就可以自动被加载

```java
public final class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    // ....省略代码
    public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
        // 通过factoryClassName获取相应的bean全称
		String factoryClassName = factoryClass.getName();
		return loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
	}

    private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
		MultiValueMap<String, String> result = cache.get(classLoader);
		if (result != null) {
			return result;
		}

		try {
			// 获取工程中所有META-INF/spring.factories文件,将其中的键值组合成Map
			Enumeration<URL> urls = (classLoader != null ?
					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			result = new LinkedMultiValueMap<>();
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				UrlResource resource = new UrlResource(url);
				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
				for (Map.Entry<?, ?> entry : properties.entrySet()) {
					String factoryClassName = ((String) entry.getKey()).trim();
					for (String factoryName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
						result.add(factoryClassName, factoryName.trim());
					}
				}
			}
			cache.put(classLoader, result);
			return result;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load factories from location [" +
					FACTORIES_RESOURCE_LOCATION + "]", ex);
		}
	}
	// ....省略代码
}
```

### 7.2. Eureka服务注册核心源码解析

#### 7.2.1. @EnableEurekaServer 注解的作用

通过 `@EnableEurekaServer` 注解激活Eureka Server。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EurekaServerMarkerConfiguration.class)
public @interface EnableEurekaServer {
}
```

此注解有一个重要作用：使用`@Import`注解导入`EurekaServerMarkerConfiguration`配置类，实例化了一个Marker的bean对象，此对象是实例化核心配置类的前提条件。**跟踪源码会知道，因为后面Eureka server的自动配置类`EurekaServerAutoConfiguration`标识了`@ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class)`注解，设置了当容器中存在`Marker`实例后，才初始化该类**

```java
@Configuration
public class EurekaServerMarkerConfiguration {
	@Bean
	public Marker eurekaServerMarkerBean() {
		return new Marker();
	}

	class Marker {
	}
}
```

#### 7.2.2. 自动装载核心配置类

Spring Cloud 对 Eureka Server 做了封装。根据自动装载原则，Eureka Server项目的启动时，会在到`spring-cloud-netflix-eureka-server-x.x.x.RELEASE.jar`中找`META-INF/spring.factories`配置文件，加载此配置文件中定义的类

![](images/20201011221059474_31721.png)

```java
@Configuration
@Import(EurekaServerInitializerConfiguration.class)
@ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class)
@EnableConfigurationProperties({ EurekaDashboardProperties.class,
		InstanceRegistryProperties.class })
@PropertySource("classpath:/eureka/server.properties")
public class EurekaServerAutoConfiguration extends WebMvcConfigurerAdapter {
    // ....省略代码
}
```

`EurekaServerAutoConfiguration` 是Eureka服务端的自动配置类，从源码可以分析到以下的要点：

1. 此配置类实例化的前提条件是上下文中存在 `EurekaServerMarkerConfiguration.Marker` 的实例
2. 通过`@EnableConfigurationProperties({ EurekaDashboardProperties.class, InstanceRegistryProperties.class })`又导入了两个配置类
    - `EurekaDashboardProperties` 配置 Eureka Server 的管控台
    - `InstanceRegistryProperties` 配置期望续约数量和默认的通信数量
3. 通过`@Import(EurekaServerInitializerConfiguration.class)`引入启动配置类

#### 7.2.3. EurekaServerInitializerConfiguration 启动配置类

`EurekaServerInitializerConfiguration`实现了`SmartLifecycle`，也就意味着Spring容器启动时会去执行`start()`方法。加载所有的Eureka Server的配置

```java
@Configuration
public class EurekaServerInitializerConfiguration
		implements ServletContextAware, SmartLifecycle, Ordered {
    // ....省略部分代码
    @Override
	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//TODO: is this class even needed now?
					eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext);
					log.info("Started Eureka Server");

					publish(new EurekaRegistryAvailableEvent(getEurekaServerConfig()));
					EurekaServerInitializerConfiguration.this.running = true;
					publish(new EurekaServerStartedEvent(getEurekaServerConfig()));
				}
				catch (Exception ex) {
					// Help!
					log.error("Could not initialize Eureka servlet context", ex);
				}
			}
		}).start();
	}
    // ....省略部分代码
}
```

#### 7.2.4. EurekaServerAutoConfiguration 自动配置类

在此配置类中，主要实例化一些bean，其中有以下比较重要的bean实例化

- 实例化了Eureka Server的管控台的Controller类 `EurekaController`
- 实例化`EurekaServerBootstrap`类
- 实例化`jersey`相关配置类。<font color=red>**jersey是RESTful的Web服务框架**</font>

```java
public class EurekaServerAutoConfiguration extends WebMvcConfigurerAdapter {
    // ....省略部分代码
    @Bean
    @ConditionalOnProperty(prefix = "eureka.dashboard", name = "enabled", matchIfMissing = true)
    public EurekaController eurekaController() {
    	return new EurekaController(this.applicationInfoManager);
    }
    // ....省略部分代码

    @Bean
	public EurekaServerBootstrap eurekaServerBootstrap(PeerAwareInstanceRegistry registry,
			EurekaServerContext serverContext) {
		return new EurekaServerBootstrap(this.applicationInfoManager,
				this.eurekaClientConfig, this.eurekaServerConfig, registry,
				serverContext);
	}

    @Bean
	public FilterRegistrationBean jerseyFilterRegistration(
			javax.ws.rs.core.Application eurekaJerseyApp) {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new ServletContainer(eurekaJerseyApp));
		bean.setOrder(Ordered.LOWEST_PRECEDENCE);
		bean.setUrlPatterns(
				Collections.singletonList(EurekaConstants.DEFAULT_PREFIX + "/*"));

		return bean;
	}

    @Bean
	public javax.ws.rs.core.Application jerseyApplication(Environment environment,
			ResourceLoader resourceLoader) {

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false, environment);

		// Filter to include only classes that have a particular annotation.
		//
		provider.addIncludeFilter(new AnnotationTypeFilter(Path.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(Provider.class));

		// Find classes in Eureka packages (or subpackages)
		//
		Set<Class<?>> classes = new HashSet<>();
		for (String basePackage : EUREKA_PACKAGES) {
			Set<BeanDefinition> beans = provider.findCandidateComponents(basePackage);
			for (BeanDefinition bd : beans) {
				Class<?> cls = ClassUtils.resolveClassName(bd.getBeanClassName(),
						resourceLoader.getClassLoader());
				classes.add(cls);
			}
		}

		// Construct the Jersey ResourceConfig
		//
		Map<String, Object> propsAndFeatures = new HashMap<>();
		propsAndFeatures.put(
				// Skip static content used by the webapp
				ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX,
				EurekaConstants.DEFAULT_PREFIX + "/(fonts|images|css|js)/.*");

		DefaultResourceConfig rc = new DefaultResourceConfig(classes);
		rc.setPropertiesAndFeatures(propsAndFeatures);

		return rc;
	}
	// ....省略部分代码
}
```

在 `jerseyApplication` 方法中，会往容器中存放了一个`jerseyApplication`对象，`jerseyApplication()`方法里的东西和Spring源码里扫描`@Component`逻辑类似，扫描`@Path`和`@Provider`标签，然后封装成BeanDefinition，封装到Application的set容器里。通过filter过滤器来过滤url进行映射到对象的Controller

#### 7.2.5. 暴露的服务端接口

集成了`Jersey`，可以找到在EurekaServer的依赖包中的 `eureka-core-x.x.x.jar`，可以看到一系列`XXXResource`的类。<font color=red>**这些类都是通过`Jersey`发布了供客户端调用的服务接口**</font>

![](images/20201011225911509_18018.png)

##### 7.2.5.1. 服务端接受客户端的注册

`ApplicationResource`类是处理客户端的注册，通过`addInstance()`方法中`this.registry.register(info, "true".equals(isReplication));`这一逻辑完成客户端的注册。具体的逻辑由`PeerAwareInstanceRegistryImpl`类实现

```java
@Singleton
public class PeerAwareInstanceRegistryImpl extends AbstractInstanceRegistry implements PeerAwareInstanceRegistry {
    // ....省略部分代码
    @Override
    public void register(final InstanceInfo info, final boolean isReplication) {
        // 默认有效时长90ms
        int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
        if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
            leaseDuration = info.getLeaseInfo().getDurationInSecs();
        }
        // 注册实例
        super.register(info, leaseDuration, isReplication);
        // 同步到其他Eureka Server服务
        replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);
    }
    // ....省略部分代码
}
```

跟踪`super.register(info, leaseDuration, isReplication);`方法，调用了父类`AbstractInstanceRegistry`的方法，里面的就是整个注册的过程

```java
public abstract class AbstractInstanceRegistry implements InstanceRegistry {
    // ....省略部分代码
    // 使用了线程安全的map，存放所有注册的示例对象
    private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
            = new ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>();

    public void register(InstanceInfo registrant, int leaseDuration, boolean isReplication) {
        try {
            read.lock();
            Map<String, Lease<InstanceInfo>> gMap = registry.get(registrant.getAppName());
            REGISTER.increment(isReplication);
            // 如果第一个实例注册会给registry put 进去一个空的
            if (gMap == null) {
                final ConcurrentHashMap<String, Lease<InstanceInfo>> gNewMap = new ConcurrentHashMap<String, Lease<InstanceInfo>>();
                gMap = registry.putIfAbsent(registrant.getAppName(), gNewMap);
                if (gMap == null) {
                    gMap = gNewMap;
                }
            }
            // 根据注册的示例对象id，获取已存在的Lease
            Lease<InstanceInfo> existingLease = gMap.get(registrant.getId());
            // Retain the last dirty timestamp without overwriting it, if there is already a lease
            if (existingLease != null && (existingLease.getHolder() != null)) {
                Long existingLastDirtyTimestamp = existingLease.getHolder().getLastDirtyTimestamp();
                Long registrationLastDirtyTimestamp = registrant.getLastDirtyTimestamp();
                logger.debug("Existing lease found (existing={}, provided={}", existingLastDirtyTimestamp, registrationLastDirtyTimestamp);

                // this is a > instead of a >= because if the timestamps are equal, we still take the remote transmitted
                // InstanceInfo instead of the server local copy.
                if (existingLastDirtyTimestamp > registrationLastDirtyTimestamp) {
                    logger.warn("There is an existing lease and the existing lease's dirty timestamp {} is greater" +
                            " than the one that is being registered {}", existingLastDirtyTimestamp, registrationLastDirtyTimestamp);
                    logger.warn("Using the existing instanceInfo instead of the new instanceInfo as the registrant");
                    registrant = existingLease.getHolder();
                }
            } else {
                // The lease does not exist and hence it is a new registration
                synchronized (lock) {
                    if (this.expectedNumberOfClientsSendingRenews > 0) {
                        // Since the client wants to register it, increase the number of clients sending renews
                        this.expectedNumberOfClientsSendingRenews = this.expectedNumberOfClientsSendingRenews + 1;
                        updateRenewsPerMinThreshold();
                    }
                }
                logger.debug("No previous lease information found; it is new registration");
            }
            Lease<InstanceInfo> lease = new Lease<InstanceInfo>(registrant, leaseDuration);
            if (existingLease != null) {
                lease.setServiceUpTimestamp(existingLease.getServiceUpTimestamp());
            }
            // 将lease存入gMap
            gMap.put(registrant.getId(), lease);
            synchronized (recentRegisteredQueue) {
                recentRegisteredQueue.add(new Pair<Long, String>(
                        System.currentTimeMillis(),
                        registrant.getAppName() + "(" + registrant.getId() + ")"));
            }
            // This is where the initial state transfer of overridden status happens
            if (!InstanceStatus.UNKNOWN.equals(registrant.getOverriddenStatus())) {
                logger.debug("Found overridden status {} for instance {}. Checking to see if needs to be add to the "
                                + "overrides", registrant.getOverriddenStatus(), registrant.getId());
                if (!overriddenInstanceStatusMap.containsKey(registrant.getId())) {
                    logger.info("Not found overridden id {} and hence adding it", registrant.getId());
                    overriddenInstanceStatusMap.put(registrant.getId(), registrant.getOverriddenStatus());
                }
            }
            InstanceStatus overriddenStatusFromMap = overriddenInstanceStatusMap.get(registrant.getId());
            if (overriddenStatusFromMap != null) {
                logger.info("Storing overridden status {} from map", overriddenStatusFromMap);
                registrant.setOverriddenStatus(overriddenStatusFromMap);
            }

            // Set the status based on the overridden status rules
            InstanceStatus overriddenInstanceStatus = getOverriddenInstanceStatus(registrant, existingLease, isReplication);
            registrant.setStatusWithoutDirty(overriddenInstanceStatus);

            // If the lease is registered with UP status, set lease service up timestamp
            if (InstanceStatus.UP.equals(registrant.getStatus())) {
                lease.serviceUp();
            }
            registrant.setActionType(ActionType.ADDED);
            recentlyChangedQueue.add(new RecentlyChangedItem(lease));
            registrant.setLastUpdatedTimestamp();
            invalidateCache(registrant.getAppName(), registrant.getVIPAddress(), registrant.getSecureVipAddress());
            logger.info("Registered instance {}/{} with status {} (replication={})",
                    registrant.getAppName(), registrant.getId(), registrant.getStatus(), isReplication);
        } finally {
            read.unlock();
        }
    }
    // ....省略部分代码
}
```

##### 7.2.5.2. 服务端接受客户端的续约

在`InstanceResource`类中，处理客户端的续约逻辑。通过`renewLease`方法中完成客户端的心跳（续约）处理

```java
@Produces({"application/xml", "application/json"})
public class InstanceResource {
    // ....省略部分代码
    @PUT
    public Response renewLease(
            @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication,
            @QueryParam("overriddenstatus") String overriddenStatus,
            @QueryParam("status") String status,
            @QueryParam("lastDirtyTimestamp") String lastDirtyTimestamp) {
        boolean isFromReplicaNode = "true".equals(isReplication);
        boolean isSuccess = registry.renew(app.getName(), id, isFromReplicaNode);

        // Not found in the registry, immediately ask for a register
        if (!isSuccess) {
            logger.warn("Not Found (Renew): {} - {}", app.getName(), id);
            return Response.status(Status.NOT_FOUND).build();
        }
        // Check if we need to sync based on dirty time stamp, the client
        // instance might have changed some value
        Response response;
        if (lastDirtyTimestamp != null && serverConfig.shouldSyncWhenTimestampDiffers()) {
            response = this.validateDirtyTimestamp(Long.valueOf(lastDirtyTimestamp), isFromReplicaNode);
            // Store the overridden status since the validation found out the node that replicates wins
            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                    && (overriddenStatus != null)
                    && !(InstanceStatus.UNKNOWN.name().equals(overriddenStatus))
                    && isFromReplicaNode) {
                registry.storeOverriddenStatusIfRequired(app.getAppName(), id, InstanceStatus.valueOf(overriddenStatus));
            }
        } else {
            response = Response.ok().build();
        }
        logger.debug("Found (Renew): {} - {}; reply status={}", app.getName(), id, response.getStatus());
        return response;
    }
    // ....省略部分代码
}
```

其中最关键的逻辑就是`this.registry.renew(this.app.getName(), this.id, isFromReplicaNode)`，具体的逻辑由`PeerAwareInstanceRegistryImpl`类实现

```java
public boolean renew(final String appName, final String id, final boolean isReplication) {
    // 客户端续约
    if (super.renew(appName, id, isReplication)) {
        // 同步到其他的Eureka Server服务
        replicateToPeers(Action.Heartbeat, appName, id, null, null, isReplication);
        return true;
    }
    return false;
}
```

跟踪父类`AbstractInstanceRegistry`的`renew`方法，实现了整个续约的过程

```java
public abstract class AbstractInstanceRegistry implements InstanceRegistry {
    // ....省略部分代码
    public boolean renew(String appName, String id, boolean isReplication) {
        RENEW.increment(isReplication);
        Map<String, Lease<InstanceInfo>> gMap = registry.get(appName);
        // 从内存map中根据id获取示例对象的Lease对象
        Lease<InstanceInfo> leaseToRenew = null;
        if (gMap != null) {
            leaseToRenew = gMap.get(id);
        }
        if (leaseToRenew == null) {
            RENEW_NOT_FOUND.increment(isReplication);
            logger.warn("DS: Registry: lease doesn't exist, registering resource: {} - {}", appName, id);
            return false;
        } else {
            // 获取示例对象
            InstanceInfo instanceInfo = leaseToRenew.getHolder();
            if (instanceInfo != null) {
                // touchASGCache(instanceInfo.getASGName());
                InstanceStatus overriddenInstanceStatus = this.getOverriddenInstanceStatus(
                        instanceInfo, leaseToRenew, isReplication);
                if (overriddenInstanceStatus == InstanceStatus.UNKNOWN) {
                    logger.info("Instance status UNKNOWN possibly due to deleted override for instance {}"
                            + "; re-register required", instanceInfo.getId());
                    RENEW_NOT_FOUND.increment(isReplication);
                    return false;
                }
                if (!instanceInfo.getStatus().equals(overriddenInstanceStatus)) {
                    logger.info(
                            "The instance status {} is different from overridden instance status {} for instance {}. "
                                    + "Hence setting the status to overridden status", instanceInfo.getStatus().name(),
                                    instanceInfo.getOverriddenStatus().name(),
                                    instanceInfo.getId());
                    // 设置示例状态
                    instanceInfo.setStatusWithoutDirty(overriddenInstanceStatus);

                }
            }
            // 设置续约次数
            renewsLastMin.increment();
            leaseToRenew.renew();
            return true;
        }
    }
    // ....省略部分代码
}
```

#### 7.2.6. 服务剔除

在`AbstractInstanceRegistry.postInit()`方法，在此方法里开启了一个每60秒调用一次`EvictionTask.evict()`的定时器

```java
protected void postInit() {
    renewsLastMin.start();
    if (evictionTaskRef.get() != null) {
        evictionTaskRef.get().cancel();
    }
    evictionTaskRef.set(new EvictionTask());
    evictionTimer.schedule(evictionTaskRef.get(),
            serverConfig.getEvictionIntervalTimerInMs(),
            serverConfig.getEvictionIntervalTimerInMs());
}
```

![](images/20201012093533007_12200.png)

```java
public void evict(long additionalLeaseMs) {
    logger.debug("Running the evict task");

    if (!isLeaseExpirationEnabled()) {
        logger.debug("DS: lease expiration is currently disabled.");
        return;
    }

    // We collect first all expired items, to evict them in random order. For large eviction sets,
    // if we do not that, we might wipe out whole apps before self preservation kicks in. By randomizing it,
    // the impact should be evenly distributed across all applications.
    List<Lease<InstanceInfo>> expiredLeases = new ArrayList<>();
    for (Entry<String, Map<String, Lease<InstanceInfo>>> groupEntry : registry.entrySet()) {
        Map<String, Lease<InstanceInfo>> leaseMap = groupEntry.getValue();
        if (leaseMap != null) {
            for (Entry<String, Lease<InstanceInfo>> leaseEntry : leaseMap.entrySet()) {
                Lease<InstanceInfo> lease = leaseEntry.getValue();
                if (lease.isExpired(additionalLeaseMs) && lease.getHolder() != null) {
                    expiredLeases.add(lease);
                }
            }
        }
    }

    // To compensate for GC pauses or drifting local time, we need to use current registry size as a base for
    // triggering self-preservation. Without that we would wipe out full registry.
    int registrySize = (int) getLocalRegistrySize();
    int registrySizeThreshold = (int) (registrySize * serverConfig.getRenewalPercentThreshold());
    int evictionLimit = registrySize - registrySizeThreshold;

    int toEvict = Math.min(expiredLeases.size(), evictionLimit);
    if (toEvict > 0) {
        logger.info("Evicting {} items (expired={}, evictionLimit={})", toEvict, expiredLeases.size(), evictionLimit);

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < toEvict; i++) {
            // Pick a random item (Knuth shuffle algorithm)
            int next = i + random.nextInt(expiredLeases.size() - i);
            Collections.swap(expiredLeases, i, next);
            Lease<InstanceInfo> lease = expiredLeases.get(i);

            String appName = lease.getHolder().getAppName();
            String id = lease.getHolder().getId();
            EXPIRED.increment();
            logger.warn("DS: Registry: expired lease for {}/{}", appName, id);
            internalCancel(appName, id, false);
        }
    }
}
```

### 7.3. Eureka服务发现核心源码解析

#### 7.3.1. 自动装载

在服务消费者导入的坐标中，找到 `spring-cloud-netflix-eureka-client-x.x.x.RELEASE.jar` 包下的 `spring.factories`，里面配置了所有自动装载的配置类

![](images/20201012134905556_20327.png)

#### 7.3.2. 客户端的创建

查看自动装载的配置类`EurekaClientAutoConfiguration`，里面的有一个创建`EurekaDiscoveryClient`类的方法，此类是就是Eureka Client客户端

```java
@Bean
public DiscoveryClient discoveryClient(EurekaClient client, EurekaClientConfig clientConfig) {
	return new EurekaDiscoveryClient(client, clientConfig);
}
```

#### 7.3.3. 服务注册

在`eureka-client-x.x.x.jar`包中的`DiscoveryClient`类，其中`register()`就是实现了服务的注册

```java
@Singleton
public class DiscoveryClient implements EurekaClient {
    // ....省略部分代码
    boolean register() throws Throwable {
        logger.info(PREFIX + "{}: registering service...", appPathIdentifier);
        EurekaHttpResponse<Void> httpResponse;
        try {
            httpResponse = eurekaTransport.registrationClient.register(instanceInfo);
        } catch (Exception e) {
            logger.warn(PREFIX + "{} - registration failed {}", appPathIdentifier, e.getMessage(), e);
            throw e;
        }
        if (logger.isInfoEnabled()) {
            logger.info(PREFIX + "{} - registration status: {}", appPathIdentifier, httpResponse.getStatusCode());
        }
        return httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();
    }
    // ....省略部分代码
}
```

#### 7.3.4. 服务下架

在`eureka-client-x.x.x.jar`包中的`DiscoveryClient`类，其中`register()`就是实现了服务的注册

```java
@Singleton
public class DiscoveryClient implements EurekaClient {
    // ....省略部分代码
    @PreDestroy
    @Override
    public synchronized void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            logger.info("Shutting down DiscoveryClient ...");

            if (statusChangeListener != null && applicationInfoManager != null) {
                applicationInfoManager.unregisterStatusChangeListener(statusChangeListener.getId());
            }

            cancelScheduledTasks();

            // If APPINFO was registered
            if (applicationInfoManager != null
                    && clientConfig.shouldRegisterWithEureka()
                    && clientConfig.shouldUnregisterOnShutdown()) {
                applicationInfoManager.setInstanceStatus(InstanceStatus.DOWN);
                unregister();
            }

            if (eurekaTransport != null) {
                eurekaTransport.shutdown();
            }

            heartbeatStalenessMonitor.shutdown();
            registryStalenessMonitor.shutdown();

            logger.info("Completed shut down of DiscoveryClient");
        }
    }
    // ....省略部分代码
}
```

#### 7.3.5. 心跳续约

在`com.netflix.discovery.DiscoveryClient.HeartbeatThread`中`renew()`方法，实现了续约的操作。具体的流程：首先向注册中心执行了心跳续约的请求，`StatusCode`为200成功，若为404则执行`register()`重新注册操作;

```java
@Singleton
public class DiscoveryClient implements EurekaClient {
    // ....省略部分代码
    private class HeartbeatThread implements Runnable {

        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }
    }

    boolean renew() {
        EurekaHttpResponse<InstanceInfo> httpResponse;
        try {
            httpResponse = eurekaTransport.registrationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
            logger.debug(PREFIX + "{} - Heartbeat status: {}", appPathIdentifier, httpResponse.getStatusCode());
            if (httpResponse.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
                REREGISTER_COUNTER.increment();
                logger.info(PREFIX + "{} - Re-registering apps/{}", appPathIdentifier, instanceInfo.getAppName());
                long timestamp = instanceInfo.setIsDirtyWithTime();
                boolean success = register();
                if (success) {
                    instanceInfo.unsetIsDirty(timestamp);
                }
                return success;
            }
            return httpResponse.getStatusCode() == Status.OK.getStatusCode();
        } catch (Throwable e) {
            logger.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, e);
            return false;
        }
    }
    // ....省略部分代码
}
```

#### 7.3.6. Eureka 客户端流程总结

1. 根据配置文件初始化bean，创建客户端实例信息类`InstanceInfo`
2. 第一次全量拉取注册中心服务列表(`url=/apps`)，初始化周期任务：
    1.  `CacheRefreshThread` 定时刷新本地缓存服务列表，若是客户端第一次拉取，则会全量拉取，后面则增量拉取。若增量拉取失败则全量拉取，配置属性为`eureka.client.registryFetchIntervalSeconds=30`，即默认30s拉取一次
    2. `HeartbeatThread` 通过`renew()`方法实现续约任务，维持于注册中心的心跳(`url=/apps/{id}`)，若返回状态码为404则说明该服务实例没有在注册中心注册，执行`register()`向注册中心注册实例信息
    3. `ApplicationInfoManager.StatusChangeListener` 注册实例状态监听类，监听服务实例状态变化，向注册中心同步实例状态
    4. `InstanceInfoReplicator`定时刷新实例状态，并向注册中心同步，默认`eureka.client.instanceInfoReplicationIntervalSeconds=30`，即30s执行一次。若实例状态有变更，则重新执行注册

# Ribbon 服务调用

## 1. Ribbon 概述

当启动某个服务的时候，可以通过HTTP的形式将信息注册到注册中心，并且可以通过SpringCloud提供的工具获取注册中心的服务列表。但是还存在很多的问题，如服务之间的如何调用，多个微服务的提供者如何选择，如何负载均衡等。Spring Cloud提供了Ribbon组件的解决方案

### 1.1. Ribbon 是什么

Ribbon 组件是 Netflixfa 发布的一个负载均衡器，有助于控制 HTTP 和 TCP 客户端行为。在Spring Cloud中推荐使用Ribbon来实现负载均衡。即使用客户端根据服务的负载情况去选择空闲或者访问压力小的服务

负载均衡分为**服务端负载均衡**和**客户端负载均衡**，<font color=red>**SpringCloud Ribbon是基于客户端的负载均衡工具**</font>

在 SpringCloud 中，Eureka一般配合Ribbon进行使用，Ribbon提供了客户端负载均衡的功能，Ribbon自动的从注册中心（如Eureka）中获取服务提供者的列表信息，在调用服务节点提供的服务时，并提供客户端的软件负载均衡算法，如轮询、随机等，会合理的进行负载请求服务。

Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。也可为Ribbon实现自定义的负载均衡算法

### 1.2. Ribbon 的主要作用

#### 1.2.1. 服务调用

基于Ribbon实现服务调用，是通过拉取到的所有服务列表组成（服务名-请求路径的）映射关系。借助estTemplate 最终进行调用

#### 1.2.2. 负载均衡

当有多个服务提供者时，Ribbon可以根据负载均衡的算法自动的选择需要调用的服务地址

### 1.3. Ribbon 架构

![Ribbon架构](images/ribbon架构_1538697723_25876.jpg)

## 2. Ribbon 基础使用示例

需求：改造上面Eureka注册中心（单机版）示例，基于Ribbon组件实现订单调用商品服务

> 复制上面`02-springcloud-eureka`工程的代码，在原有基础进行修改。改造后示例源代码详见：`spring-cloud-note\spring-cloud-greenwich-sample\04-springcloud-ribbon\`

### 2.1. Ribbon 的依赖

在SpringCloud提供的服务工程，主要引入eureka组件，无论是服务端还是客户端，其jar都已经包含了Ribbon的依赖。所以使用Ribbon组件不需要导入任何额外的坐标

![](images/20201012154059447_22112.png)

### 2.2. 服务项目使用Ribbon组件

#### 2.2.1. 改造步骤

1. 在创建`RestTemplate`实例的时候，声明Ribbon组件的`@LoadBalanced`注解
2. 在使用`RestTemplate`调用远程微服务接口时，不需要手动拉普拉斯微服务的url，只需要将指定待请求的服务名称即可

#### 2.2.2. 服务提供者

修改`shop-service-product`工程中`ProductController`控制器的`findById()`方法，在返回数据中增加当时服务的ip与端口号（这里用于后面测试Ribbon负载均衡调用测试）

```java
@RestController
@RequestMapping("product")
public class ProductController {
    // ....省略部分代码
    // 注入配置文件中当前服务的端口号
    @Value("${server.port}")
    private String port;
    /*
     * 注解当前服务的ip地址
     *  使用el表达式：spring.cloud.client.ip-address
     *  spring cloud 自动的获取当前应用的ip地址
     */
    @Value("${spring.cloud.client.ip-address}")
    private String ip;

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        /* 设置当前被调用的服务的ip与端口，用于测试ribbon的负载均衡 */
        product.setProductDesc(product.getProductDesc() + "===当前被调用的product服务的ip: " + ip + " ,端口: " + port);
        return product;
    }
    // ....省略部分代码
}
```

#### 2.2.3. 服务消费者

修改服务消费者`shop_service_order`工程配置类`HttpConfig`，在创建`RestTemplate`方法上添加 `@LoadBalanced` 注解

```java
@LoadBalanced // 是Ribbon组件提供的负载均衡的注解，声明此注解后就可以基于Ribbon的服务调用与负载均衡
@Bean("restTemplate")
public RestTemplate createRestTemplate() {
    return new RestTemplate();
}
```

修改`OrderController`中的`createOrder`方法，将原来的ip地址改相应的服务名称，`RestTemplate`完成调用服务

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
        /*
         * 通过http请求，获取商品数据
         * 拼接请求url，将原来使用ip+端口调用的方式，改成要调用的服务对应的名称即可
         * 服务提供者名称在其项目配置文件的spring.application.name属性中定义
         */
        Product product = restTemplate.getForObject("http://shop-service-product/product/" + id, Product.class);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }
}
```

#### 2.2.4. 代码测试

发起post请求`http://127.0.0.1:9002/order/1`，查看后端日志输出，已经可以在订单微服务中以服务名称的形式调用商品微服务获取数据

## 3. Ribbon 的负载均衡

### 3.1. 负载均衡的定义

负载均衡是一种基础的网络服务，其原理是通过运行在前面的负载均衡服务，按照指定的负载均衡算法，将流量分配到后端服务集群上，从而为系统提供并行扩展的能力

负载均衡的应用场景包括流量包、转发规则以及后端服务，由于该服务有内外网个例、健康检查等功能，能够有效提供系统的安全性和可用性

![](images/20201012165205002_10570.png)

### 3.2. 客户端负载均衡与服务端负载均衡

#### 3.2.1. 服务端负载均衡

先发送请求到负载均衡服务器或者软件，然后通过负载均衡算法，在多个服务器之间选择一个进行访问。即在服务器端再进行负载均衡算法分配

#### 3.2.2. 客户端负载均衡

客户端会有一个服务器地址列表，在发送请求前通过负载均衡算法选择一个服务器，然后进行访问，这是客户端负载均衡。即在客户端就进行负载均衡算法分配

### 3.3. 基于Ribbon实现服务负载均衡的示例

#### 3.3.1. 搭建多服务实例

修改 `shop-service-product` 的 `application.yml` 配置文件，将端口号设置为变量，在启动应用时指定变量值，从而实现模拟多实例

```yml
server:
  port: ${PORT:9001} # 项目端口，设置为变量，指定默认值为9001
# ....以下部分省略
```

启动服务时，指定端口号

![](images/20201013103825899_15324.png)

![](images/20201013103935048_30213.png)

![](images/20201013104009863_4157.png)

ribbon默认的负载均衡策略是轮询，所以使用order服务去调用商品服务，观察后端日志输出的端口，每次访问换一台服务器

![](images/20201013133355958_11290.png)

#### 3.3.2. 负载均衡策略配置

Ribbon内置了多种负载均衡策略，内部负责复杂均衡的顶级接口为`com.netflix.loadbalancer.IRule`，有以下的实现类：

- `com.netflix.loadbalancer.RoundRobinRule`：以轮询的方式进行负载均衡【默认】
- `com.netflix.loadbalancer.RandomRule`：随机策略
- `com.netflix.loadbalancer.RetryRule`：重试策略
- `com.netflix.loadbalancer.WeightedResponseTimeRule`：权重策略。会计算每个服务的权重，越高的被调用的可能性越大
- `com.netflix.loadbalancer.BestAvailableRule`：最佳策略。遍历所有的服务实例，过滤掉故障实例，并返回请求数最小的实例返回
- `com.netflix.loadbalancer.AvailabilityFilteringRule`：可用过滤策略。过滤掉故障和请求数超过阈值的服务实例，再从剩下的实力中轮询调用

**策略选择：**

1. 如果服务部署的每个机器配置性能差不多，则建议不修改策略，让每台服务器平均分担压力 (推荐)
2. 如果部分机器配置强，则可以改为 `WeightedResponseTimeRule` 权重策略，让性能高的服务器承担更多的请求

在服务消费者的`application.yml`配置文件中修改负载均衡策略即可，增加以下配置：

格式：`服务名.ribbon.NFLoadBalancerRuleClassName: 策略全限定名`

```yml
# Ribbon配置
shop-service-product:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 修改策略为随机
```

![](images/20201013143107548_3493.png)

#### 3.3.3. 请求重试配置

请求重试的机制是：当服务消费者去请求多个服务提供者时，如果当前请求的服务A出现网络的波动或者宕机的情况，此时，请求就会出现报错或者请求不到数据。请求重试就是根据当前用户设置的参数（如：请求连接超时时间、获取数据返回超时时间等），如果超出了用户设置的限制，就会直接重新发起新的请求到另一台服务。

Ribbon配置的请求重试的步骤如下：

1. 在服务消费者工程引入spring的重试组件，以订单服务（`shop-service-order`）为例：

```xml
<!-- Spring 框架的重试组件 -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
```

![](images/20201013144404585_28140.png)

2. 修改消费者工程的配置文件

```yml
# 配置日志级别，用于观察请求重试时输出的日志
logging:
  level:
    root: debug
# Ribbon配置
shop-service-product:
  ribbon:
    ConnectTimeout: 250 # Ribbon的连接超时时间
    ReadTimeout: 1000 # Ribbon的数据读取超时时间
    OkToRetryOnAllOperations: true # 是否对所有操作都进行重试
    MaxAutoRetriesNextServer: 1 # 切换实例的重试次数
    MaxAutoRetries: 1 # 对当前实例的重试次数
```

3. 测试，启动两个服务提供者，正常请求消费后再停用其中一个服务，再请求后观察后台日志

![](images/20201013161008081_12757.png)

## 4. Ribbon 的负载均衡源码解析

### 4.1. Ribbon中的关键组件

![](images/20201013161729522_7569.png)

- `ServerList`：可以响应客户端的特定服务的服务器列表
- `ServerListFilter`：可以动态获得的具有所需特征的候选服务器列表的过滤器
- `ServerListUpdater`：用于执行动态服务器列表更新
- `Rule`：负载均衡策略，用于确定从服务器列表返回哪个服务器
- `Ping`：客户端用于快速检查服务器当时是否处于活动状态
- `LoadBalancer`：负载均衡器，负责负载均衡调度的管理

### 4.2. @LoadBalanced 注解

```java
/**
 * Annotation to mark a RestTemplate bean to be configured to use a LoadBalancerClient.
 * @author Spencer Gibb
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface LoadBalanced {
}
```

通过注释可以知道，`@LoadBalanced`注解是给`RestTemplate`做标记，方便对`RestTemplate`对象添加一个`LoadBalancerClient`，以实现客户端负载均衡。

所以使用Ribbon完成客户端负载均衡就是直接给创建`RestTemplate`方法标识上该注解即可

```java
@LoadBalanced // 是Ribbon组件提供的负载均衡的注解，声明此注解后就可以基于Ribbon的服务调用与负载均衡
@Bean("restTemplate")
public RestTemplate createRestTemplate() {
    return new RestTemplate();
}
```

### 4.3. 自动装配

根据SpringBoot中的自动装配规则，在 `spring-cloud-netflix-ribbon-2.1.0.RELEASE.jar` 中找到 `META-INF\spring.factories`文件，自动初始化`RibbonAutoConfiguration`自动装配类

![](images/20201013170624489_17129.png)

在 `RibbonAutoConfiguration` 类中，通过`@AutoConfigureBefore`注解会在自动装配前引入 `LoadBalancerAutoConfiguration` 配置类到spring容器中

```java
@Configuration
@Conditional(RibbonAutoConfiguration.RibbonClassesConditions.class)
@RibbonClients
@AutoConfigureAfter(name = "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration")
@AutoConfigureBefore({LoadBalancerAutoConfiguration.class, AsyncLoadBalancerAutoConfiguration.class})
@EnableConfigurationProperties({RibbonEagerLoadProperties.class, ServerIntrospectorProperties.class})
public class RibbonAutoConfiguration {
    // ....省略部分代码
    @Bean
	public SpringClientFactory springClientFactory() {
		SpringClientFactory factory = new SpringClientFactory();
		factory.setConfigurations(this.configurations);
		return factory;
	}

	@Bean
	@ConditionalOnMissingBean(LoadBalancerClient.class)
	public LoadBalancerClient loadBalancerClient() {
		return new RibbonLoadBalancerClient(springClientFactory());
	}
    // ....省略部分代码
}
```

### 4.4. 负载均衡的实现

#### 4.4.1. LoadBalancerAutoConfiguration 自动配置类

`LoadBalancerAutoConfiguration`类是负载均衡的自动化配置类。

```java
@Configuration
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnBean(LoadBalancerClient.class)
@EnableConfigurationProperties(LoadBalancerRetryProperties.class)
public class LoadBalancerAutoConfiguration {
    // ....省略部分代码
    @Configuration
	@ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
	static class LoadBalancerInterceptorConfig {
		@Bean
		public LoadBalancerInterceptor ribbonInterceptor(
				LoadBalancerClient loadBalancerClient,
				LoadBalancerRequestFactory requestFactory) {
			return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
		}

		@Bean
		@ConditionalOnMissingBean
		public RestTemplateCustomizer restTemplateCustomizer(
				final LoadBalancerInterceptor loadBalancerInterceptor) { // LoadBalancerInterceptor拦截器用于实现对客户端发起请求时进行拦截，以实现客户端负载均衡
			// 创建RestTemplateCustomizer类，该类是函数式接口，主要处理的逻辑是给 RestTemplate 增加 LoadBalancerInterceptor 拦截器
			return restTemplate -> {
                List<ClientHttpRequestInterceptor> list = new ArrayList<>(
                        restTemplate.getInterceptors());
                list.add(loadBalancerInterceptor);
                restTemplate.setInterceptors(list);
            };
		}
	}
	// ....省略部分代码
}
```

主要实现以下逻辑：

- 创建了一个 `LoadBalancerInterceptor` 的Bean，用于实现对客户端发起请求时进行拦截，以实现客户端负载均衡。
- 创建了一个 `RestTemplateCustomizer` 的Bean，用于给 `RestTemplate` 增加 `LoadBalancerInterceptor` 拦截器。
- 维护了一个被 `@LoadBalanced` 注解修饰的 `RestTemplate` 对象列表，并在这里进行初始化，通过调用 `RestTemplateCustomizer` 的实例来给需要客户端负载均衡的 `RestTemplate` 增加 `LoadBalancerInterceptor` 拦截器

#### 4.4.2. LoadBalancerInterceptor 负载均衡拦截器

```java
public class LoadBalancerInterceptor implements ClientHttpRequestInterceptor {

	private LoadBalancerClient loadBalancer;
	private LoadBalancerRequestFactory requestFactory;

	public LoadBalancerInterceptor(LoadBalancerClient loadBalancer, LoadBalancerRequestFactory requestFactory) {
		this.loadBalancer = loadBalancer;
		this.requestFactory = requestFactory;
	}

	public LoadBalancerInterceptor(LoadBalancerClient loadBalancer) {
		// for backwards compatibility
		this(loadBalancer, new LoadBalancerRequestFactory(loadBalancer));
	}

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		// 获取URI实例
		final URI originalUri = request.getURI();
		// 获取host主机服务（使用ribbon调用时，host名称是相应服务提供者的名称）
		String serviceName = originalUri.getHost();
		Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
		return this.loadBalancer.execute(serviceName, requestFactory.createRequest(request, body, execution));
	}
}
```

在之前的`LoadBalancerAutoConfiguration`自动化配置类中，给`RestTemplate`对象增加了`LoadBalancerInterceptor`拦截器，在拦截器中注入了 `LoadBalancerClient` 的实现。

当一个被 `@LoadBalanced` 注解修饰的 `RestTemplate` 对象向外发起HTTP请求时，会被 `LoadBalancerInterceptor` 类的 `intercept` 函数所拦截。由于在使用`RestTemplate`时候采用了服务名作为host，所以直接从 `HttpRequest` 的URI对象中通过`getHost()`方法就可以拿到服务名，然后调用 `execute` 函数去根据服务名来选择实例并发起实际的请求

#### 4.4.3. LoadBalancerClient 负载均衡具体逻辑接口

`LoadBalancerClient` 是负载均衡器的抽象接口，其实现类是`org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient`

```java
public class RibbonLoadBalancerClient implements LoadBalancerClient {
    // ....省略部分代码
    /**
	 * New: Select a server using a 'key'.
	 */
	public ServiceInstance choose(String serviceId, Object hint) {
		Server server = getServer(getLoadBalancer(serviceId), hint);
		if (server == null) {
			return null;
		}
		return new RibbonServer(serviceId, server, isSecure(server, serviceId),
				serverIntrospector(serviceId).getMetadata(server));
	}

    /**
	 * New: Execute a request by selecting server using a 'key'.
	 * The hint will have to be the last parameter to not mess with the `execute(serviceId, ServiceInstance, request)`
	 * method. This somewhat breaks the fluent coding style when using a lambda to define the LoadBalancerRequest.
	 */
	public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint) throws IOException {
		ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
		Server server = getServer(loadBalancer, hint);
		if (server == null) {
			throw new IllegalStateException("No instances available for " + serviceId);
		}
		RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
				serviceId), serverIntrospector(serviceId).getMetadata(server));

		return execute(serviceId, ribbonServer, request);
	}

    /**
	 * Note: This method could be removed?
	 */
	protected Server getServer(String serviceId) {
		return getServer(getLoadBalancer(serviceId), null);
	}

	protected Server getServer(ILoadBalancer loadBalancer) {
	    return getServer(loadBalancer, null);
	}

	protected Server getServer(ILoadBalancer loadBalancer, Object hint) {
		if (loadBalancer == null) {
			return null;
		}
		// Use 'default' on a null hint, or just pass it on?
		return loadBalancer.chooseServer(hint != null ? hint : "default");
	}

	protected ILoadBalancer getLoadBalancer(String serviceId) {
		return this.clientFactory.getLoadBalancer(serviceId);
	}
	// ....省略部分代码
}
```

- `ServiceInstance choose(String serviceId)`：根据传入的服务id，从负载均衡器中为指定的服务选择一个服务实例
- `T execute(String serviceId, LoadBalancerRequest request)`：根据传入的服务id，指定的负载均衡器中的服务实例执行请求
- `T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest request)`：根据传入的服务实例，执行请求。

通过`RibbonLoadBalancerClient`的源码分析可知，实际负载均衡的是通过 `ILoadBalancer` 来实现的

![](images/20201014085920424_23044.png)

```java
@Configuration
@EnableConfigurationProperties
@Import({HttpClientConfiguration.class, OkHttpRibbonConfiguration.class, RestClientRibbonConfiguration.class, HttpClientRibbonConfiguration.class})
public class RibbonClientConfiguration {
    // ....省略部分代码
    @Bean
	@ConditionalOnMissingBean
	public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
			ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
			IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
		if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
			return this.propertiesFactory.get(ILoadBalancer.class, config, name);
		}
		return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
				serverListFilter, serverListUpdater);
	}
    // ....省略部分代码
}
```

### 4.5. 总结

Ribbon 的负载均衡主要是通 `LoadBalancerClient` 来实现，而 `LoadBalancerClient` 具体是交给 `ILoadBalancer` 来处理，`ILoadBalancer` 通过配置 `IRule`、`IPing` 等，向 `EurekaClient` 获取注册列表信息，默认每10秒向 `EurekaClient` 发送一次“ping” 请求，用于检查是否需要更新服务的注册列表信息。最后，在得到服务注册列表令牌后，`ILoadBalancer` 根据 `IRule` 的策略进行负载均衡。

在 `RestTemplate` 加上 `@LoadBalanced` 注解后，在远程调度时能够负载均衡，主要是维护了一个被 `@LoadBalanced` 注解的 `RestTemplate` 列表，并给该列表中的 `RestTemplate` 对象添加了拦截器。在拦截器的方法中，将远程调度方法交给了 Ribbon 的负载均衡器 `LoadBalancerClient` 去处理，从而达到了负载均衡的目的。

# Consul 注册中心（Eureka替换方案）

## 1. Eureka 的替换方案

- **Zookeeper**

ZooKeeper是一个分布式的，开放源码的分布式应用程序协调服务，是Google的Chubby一个开源的实现，是Hadoop和Hbase的重要组件。它是一个为分布式应用提供一致性服务的软件，提供的功能包括：配置维护、域名服务、分布式同步、组服务等。

- **Consul**

consul是近几年比较流行的服务发现工具。consul的三个主要应用场景：服务发现、服务隔离、服务配置

- **Nacos**

Nacos 是阿里巴巴推出来的一个新开源项目，这是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施

## 2. consul 基础入门

官网：https://www.consul.io/

### 2.1. consul 概述

Consul 是 HashiCorp 公司推出的开源工具，用于实现分布式系统的服务发现与配置。与其它分布式服务注册与发现的方案，Consul 的方案更“一站式”，内置了服务注册与发现框架、分布一致性协议实现、健康检查、Key/Value 存储、多数据中心方案，不再需要依赖其它工具（比如 ZooKeeper 等）。使用起来也较 为简单。Consul 使用 Go 语言编写，因此具有天然可移植性(支持Linux、windows和Mac OS X)；安装包仅包含一个可执行文件，方便部署，与 Docker 等轻量级容器可无缝配合

**Consul 的优势**：

- 使用 Raft 算法来保证一致性, 比复杂的 Paxos 算法更直接. 相比较而言, zookeeper 采用的是Paxos, 而 etcd 使用的则是 Raft。
- 支持多数据中心，内外网的服务采用不同的端口进行监听。 多数据中心集群可以避免单数据中心的单点故障,而其部署则需要考虑网络延迟, 分片等情况等。 zookeeper 和 etcd 均不提供多数据中心功能的支持。
- 支持健康检查。 etcd 不提供此功能。
- 支持 http 和 dns 协议接口。 zookeeper 的集成较为复杂, etcd 只支持 http 协议。
- 官方提供 web 管理界面, etcd 无此功能。

**Consul 的特性**：

- 服务发现
- 健康检查
- Key/Value 存储
- 多数据中心

### 2.2. consul与Eureka的区别

1. 一致性

Consul强一致性（CP）

- 服务注册相比Eureka会稍慢一些。因为Consul的raft协议要求必须过半数的节点都写入成功才认为注册成功
- Leader挂掉时，重新选举期间整个consul不可用。保证了强一致性但牺牲了可用性

Eureka保证高可用和最终一致性（AP）

- 服务注册相对要快，因为不需要等注册信息replicate到其他节点，也不保证注册信息是否replicate成功
- 当数据出现不一致时，虽然A, B上的注册信息不完全相同，但每个Eureka节点依然能够正常对外提供服务，这会出现查询服务信息时如果请求A查不到，但请求B就能查到。如此保证了可用性但牺牲了一致性

2. 开发语言和使用

- eureka就是个servlet程序，跑在servlet容器中
- Consul则是go编写而成，安装启动即可

### 2.3. consul的下载与安装

Consul 不同于 Eureka 需要单独安装，访问 Consul 官网下载 Consul 的最新版本（本次示例安装consul1.5x版）

> 已下载的安装包位置：`\07-编程工具资料\注册中心-consul\`

#### 2.3.1. Linux系统安装Consul

输入以下命令

```bash
## 从官网下载最新版本的Consul服务
wget https://releases.hashicorp.com/consul/1.5.3/consul_1.5.3_linux_amd64.zip
## 使用unzip命令解压
unzip consul_1.5.3_linux_amd64.zip
## 将解压好的consul可执行命令拷贝到/usr/local/bin目录下
cp consul /usr/local/bin
## 测试
consul
```

启动consul服务

```bash
##已开发者模式快速启动，-client指定客户端可以访问的ip地址
[root@node01 ~]# consul agent -dev -client=0.0.0.0
==> Starting Consul agent...
    Version: 'v1.5.3'
    Node ID: '49ed9aa0-380b-3772-a0b6-b0c6ad561dc5'
    Node name: 'node01'
    Datacenter: 'dc1' (Segment: '<all>')
    Server: true (Bootstrap: false)
    Client Addr: [127.0.0.1] (HTTP: 8500, HTTPS: -1, gRPC: 8502, DNS: 8600)
    Cluster Addr: 127.0.0.1 (LAN: 8301, WAN: 8302)
    Encrypt: Gossip: false, TLS-Outgoing: false, TLS-Incoming: false, Auto-Encrypt-TLS: false
```

启动成功之后访问：`http://linux系统ip:8500`，可以看到 Consul 的管理界面

#### 2.3.2. window系统安装Consul

1. 将window版压缩包`consul_1.5.3_windows_amd64.zip`解压到没有中文和空格的目录
2. 进入目录，运行命令行。输入以下命令，启动consul服务

```bash
# 以开发者模式快速启动
consul agent -dev -client=0.0.0.0
```

![](images/20201014140655017_29915.png)

3. 启动成功后访问`http://127.0.0.1:8500`，进入consul管理界面

### 2.4. consul 的基本使用

Consul 支持健康检查，并提供了 HTTP 和 DNS 调用的API接口完成服务注册，服务发现，以及K/V存储这些功能。以下是基于通过发送HTTP请求的形式来实现Consul的基础使用

> *官方的API接口文档地址：https://www.consul.io/api/catalog.html#catalog_register*

#### 2.4.1. 注册服务

通过postman发送PUT请求到`http://192.168.74.101:8500/v1/catalog/register`地址可以完成服务注册，请求参数如下：

```json
{
    "Datacenter": "dc1",
    "Node": "node01",
    "Address": "192.168.74.102",
    "Service": {
        "ID": "mysql-01",
        "Service": "mysql",
        "tags": [
            "master",
            "v1"
        ],
        "Address": "192.168.74.102",
        "Port": 3306
    }
}
```

#### 2.4.2. 服务查询

通过postman发送GET请求到`http://192.168.74.101:8500/v1/catalog/services`查看所有的服务列表

![](images/20201014144011035_32225.png)

通过postman发送GET请求到`http://192.168.74.101:8500/v1/catalog/service/`服务名查看具体的服务详情

![](images/20201014144036460_8373.png)

#### 2.4.3. 服务删除

通过postman发送PUT请求到`http://192.168.74.101:8500/v1/catalog/deregister`删除服务

```json
{
    "Datacenter": "dc1",
    "Node": "node01",
    "ServiceID": "mysql-01"
}
```

#### 2.4.4. Consul的K/V存储

可以参照Consul提供的KV存储的API完成基于Consul的数据存储

|   含义    |   请求路径    | 请求方式 |
| --------- | :-----------: | :-----: |
| 查看key   | `v1/kv/:key`  |   GET   |
| 保存或更新 | `v1/kv/:key`  |   PUT   |
| 删除      | `/v1/kv/:key` | DELETE  |

- key值中可以带`/`, 可以看做是不同的目录结构
- value的值经过了base64_encode,获取到数据后base64_decode才能获取到原始值。数据不能大于512Kb
- 不同数据中心的kv存储系统是独立的，使用`dc=?`参数指定。

## 3. 基于consul的服务注册与发现示例

### 3.1. 示例工程的准备

复用之前eureka单机版的示例项目`02-springcloud-eureka`，将里面eureka子模块、相关的配置与依赖都删除。命名为`05-springcloud-consul`

### 3.2. 引入 consul 依赖

修改服务提供者与消费者微服务的`pom.xml`文件，添加SpringCloud提供的基于Consul的依赖

```xml
<!-- springcloud 提供的对基于Consul的服务发现 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
<!-- actuator的健康检查（心跳检查） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- `spring-cloud-starter-consul-discovery` 是SpringCloud提供的对consul支持的相关依赖
- `spring-boot-starter-actuator` 适用于完成心跳检测响应的相关依赖

### 3.3. 配置服务注册

修改服务提供者与消费者微服务的`application.yml`配置文件，添加consul服务注册的相关配置信息

```yml
spring:
  # ...省略其他配置
  # Consul的服务注册配置
  cloud:
    consul:
      host: 127.0.0.1 # consul服务器的主机ip地址
      port: 8500 # consul服务器的端口
      discovery:
        register: true # 是否需要注册，默认值是true
        instance-id: ${spring.application.name}:${server.port} # 注册的实例ID (唯一标志)
        service-name: ${spring.application.name} # 服务的名称
        port: ${server.port} # 服务的请求端口
        prefer-ip-address: true # 是否开启ip地址注册，默认值是false
        ip-address: ${spring.cloud.client.ip-address} # 当前服务的请求ip
        health-check-path: /actuator/health # 健康检查路径，默认值就是/actuator/health
        health-check-interval: 15s # 健康检查时间间隔，默认值是10s
```

`spring.cloud.consul` 属性是添加consul的相关配置

- `host`：表示Consul的Server的请求地址
- `port`：表示Consul的Server的端口
- `discovery`：服务注册与发现的相关配置
    - `instance-id`：实例的唯一id（推荐必填），spring cloud官网文档的推荐，为了保证生成一个唯一的id，也可以换成`${spring.application.name}:${spring.cloud.client.ip-address}`
    - `prefer-ip-address`：开启ip地址注册
    - `ip-address`：当前微服务的请求ip

### 3.4. Consul 控制台查看服务列表

启动相关的微服务，打开ConsulServer的管理控制台，相关的微服务已经全部注册到Consul中了

![](images/20201014155442987_9782.png)

### 3.5. 基于consul的服务发现

由于SpringCloud对Consul进行了封装。对于在消费者端获取服务提供者信息和Eureka是一致的。同样使用 `DiscoveryClient` 完成调用获取微服务实例信息，也可以使用 Ribbon 完成服务的调用。

![](images/20201014160048541_5289.png)

下面以Ribbon的方式完成服务的调用示例

#### 3.5.1. 修改配置类增加负载均衡

修改消费者的配置类`HttpConfig`，给`RestTemplate`对象增加Ribbon组件的`@LoadBalanced`注解标识

```java
@LoadBalanced // springcloud对consul进行了封装，集成了Ribbon的支持
@Bean("restTemplate")
public RestTemplate createRestTemplate() {
    return new RestTemplate();
}
```

#### 3.5.2. 修改消费者请求方式

修改消费者`OrderController`使用Ribbon请求方式

```java
@Autowired
private RestTemplate restTemplate;

/**
 * 根据商品id创建订单
 *
 * @param id 商品的id
 * @return
 */
@PostMapping("/{id}")
public String createOrder(@PathVariable Long id) {
    /*
     * 通过http请求，获取商品数据
     * 拼接请求url，将原来使用ip+端口调用的方式，改成要调用的服务对应的名称即可
     * 服务提供者名称在其项目配置文件的spring.application.name属性中定义
     */
    Product product = restTemplate.getForObject("http://shop-service-product/product/" + id, Product.class);
    LOGGER.info("当前下单的商品是: ${}", product);
    return "创建订单成功";
}
```

## 4. consul高可用集群

### 4.1. consul 集群架构图

![](images/20201014160743484_12697.png)

上图是官网提供的一个事例系统图，图中的Server是consul服务端高可用集群，Client是consul客户端。consul客户端不保存数据，客户端将接收到的请求转发给响应的Server端。Server之间通过局域网或广域网通信实现数据一致性。每个Server或Client都是一个consul agent。Consul集群间使用了GOSSIP协议通信和raft一致性算法。上面这张图涉及到了很多术语：

- Agent：agent是一直运行在Consul集群中每个成员上的守护进程。通过运行 consul agent来启动。agent可以运行在client或者server模式。指定节点作为client或者server是非常简单的，除非有其他agent实例。所有的agent都能运行DNS或者HTTP接口，并负责运行时检查和保持服务同步。
- Client：一个Client是一个转发所有RPC到server的代理。这个client是相对无状态的。client唯一执行的后台活动是加入LAN gossip池。这有一个最低的资源开销并且仅消耗少量的网络带宽。
- Server：一个server是一个有一组扩展功能的代理，这些功能包括参与Raft选举，维护集群状态，响应RPC查询，与其他数据中心交互WANgossip和转发查询给leader或者远程数据中心。
- DataCenter：虽然数据中心的定义是显而易见的，但是有一些细微的细节必须考虑。例如，在EC2中，多个可用区域被认为组成一个数据中心？我们定义数据中心为一个私有的，低延迟和高带宽的一个网络环境。这不包括访问公共网络，但是对于我们而言，同一个EC2中的多个可用区域可以被认为是一个数据中心的一部分。
- Consensus：在我们的文档中，我们使用Consensus来表明就leader选举和事务的顺序达成一致。由于这些事务都被应用到有限状态机上，Consensus暗示复制状态机的一致性。
- Gossip：Consul建立在Serf的基础之上，它提供了一个用于多播目的的完整的gossip协议。Serf提供成员关系，故障检测和事件广播。更多的信息在gossip文档中描述。这足以知道gossip使用基于UDP的随机的点到点通信
- LAN Gossip：它包含所有位于同一个局域网或者数据中心的所有节点。
- WAN Gossip：它只包含Server。这些server主要分布在不同的数据中心并且通常通过因特网或者广域网通信

在每个数据中心，client和server是混合的。一般建议有3-5台server。这是基于有故障情况下的可用性和性能之间的权衡结果，因为越多的机器加入达成共识越慢。然而，并不限制client的数量，它们可以很容易的扩展到数千或者数万台。

同一个数据中心的所有节点都必须加入gossip协议。这意味着gossip协议包含一个给定数据中心的所有节点。这服务于几个目的：第一，不需要在client上配置server地址。发现都是自动完成的。第二，检测节点故障的工作不是放在server上，而是分布式的。这是的故障检测相比心跳机制有更高的可扩展性。第三：它用来作为一个消息层来通知事件，比如leader选举发生时。

每个数据中心的server都是Raft节点集合的一部分。这意味着它们一起工作并选出一个leader，一个有额外工作的server。leader负责处理所有的查询和事务。作为一致性协议的一部分，事务也必须被复制到所有其他的节点。因为这一要求，当一个非leader得server收到一个RPC请求时，它将请求转发给集群leader。

server节点也作为WAN gossip Pool的一部分。这个Pool不同于LAN Pool，因为它是为了优化互联网更高的延迟，并且它只包含其他Consul server节点。这个Pool的目的是为了允许数据中心能够以low-touch的方式发现彼此。这使得一个新的数据中心可以很容易的加入现存的WAN gossip。因为server都运行在这个pool中，它也支持跨数据中心请求。当一个server收到来自另一个数据中心的请求时，它随即转发给正确数据中想一个server。该server再转发给本地leader。

这使得数据中心之间只有一个很低的耦合，但是由于故障检测，连接缓存和复用，跨数据中心的请求都是相对快速和可靠的。

<font color=red>**总结：**</font>

`agent`命令用于启动一个consul的守护进程，其中有3个参数：`dev`、`client`、`server`

- `dev`：是开发者模式
- `client`：用于高可用集群，是consul的代理，主要作用是和consul server进行交互。一般一个微服务会绑定一个client，相当于微服务和client部署到同一台机器上
- `server`：用于高可用集群，所有操作都由此服务进行。官方建议一般部署3-5个server进行集群，如果过多server会因为数据的同步导致注册比较慢

### 4.2. Consul的核心知识

#### 4.2.1. Gossip协议

传统的监控，如ceilometer，由于每个节点都会向server报告状态，随着节点数量的增加server的压力随之增大。在所有的Agent之间（包括服务器模式和普通模式）运行着Gossip协议。服务器节点和普通Agent都会加入这个Gossip集群，收发Gossip消息。每隔一段时间，每个节点都会随机选择几个节点发送Gossip消息，其他节点会再次随机选择其他几个节点接力发送消息。这样一段时间过后，整个集群都能收到这条消息。示意图如下：

![](images/20201014170754150_14336.png)

Gossip协议实现效果动态图如下：

![](images/20201014162817624_12895.gif)

#### 4.2.2. RAFT协议（一致性算法）

![](images/20201014171328705_9010.png)

为了实现集群中多个ConsulServer中的数据保持一致性，consul使用了基于强一致性的RAFT算法。

在Raft中，任何时候一个服务器可以扮演下面角色之一：

1. Leader：处理所有客户端交互，日志复制等，一般一次只有一个Leader
2. Follower：类似选民，完全被动
3. Candidate（候选人）：可以被选为一个新的领导人

Leader全权负责所有客户端的请求，以及将数据同步到Follower中（同一时刻系统中只存在一个Leader）。Follower被动响应请求RPC，从不主动发起请求RPC。Candidate由Follower向Leader转换的中间状态

> 关于RAFT一致性算法有一个经典的动画，其中详细介绍了选举，数据同步的步骤。网址：http://thesecretlivesofdata.com/raft/

### 4.3. Consul 集群搭建

![](images/20201014171726368_29074.png)

首先需要有一个正常的Consul集群，有Server，有Leader。这里在服务器Server1、Server2、Server3上分别部署了Consul Server。（这些服务器上最好只部署Consul程序，以尽量维护Consul Server的稳定）

服务器Server4和Server5上通过Consul Client分别注册Service A、B、C，这里每个Service分别部署在了两个服务器上，这样可以避免Service的单点问题。（一般微服务和Client绑定）

在服务器Server6中Program D需要访问Service B，这时候Program D首先访问本机Consul Client提供的HTTP API，本机Client会将请求转发到Consul Server，Consul Server查询到Service B当前的信息返回

#### 4.3.1. 准备环境

| 服务器ip       | consul类型 | Node（节点名称） | 序号 |
| -------------- | ---------- | ---------------- | ---- |
| 192.168.74.101 | server     | server-1         | s1   |
| 192.168.74.102 | server     | server-2         | s2   |
| 192.168.74.103 | server     | server-3         | s3   |
| 192.168.71.1   | client     | clent-1          | s4   |

- Agent 以 client 模式启动的节点。在该模式下，该节点会采集相关信息，通过 RPC 的方式向 server 发送。Client模式节点有无数个，官方建议搭配微服务配置
- Agent 以 server 模式启动的节点。一个数据中心中至少包含 1 个 server 节点。不过官方建议使用 3 或 5 个 server 节点组建成集群，以保证高可用且不失效率。server 节点参与 Raft、维护会员信息、注册服务、健康检查等功能。

#### 4.3.2. 安装consul并启动

- 在每个consul节点上安装consul服务，下载安装过程和单节点一致。

```shell
## 从官网下载最新版本的Consul服务
wget https://releases.hashicorp.com/consul/1.5.3/consul_1.5.3_linux_amd64.zip
## 使用unzip命令解压
unzip consul_1.5.3_linux_amd64.zip
## 将解压好的consul可执行命令拷贝到/usr/local/bin目录下
cp consul /usr/local/bin
## 测试一下
consul
```

- 启动每个consul server节点

```shell
# 登录s1虚拟机，以server形式运行
consul agent -server -bootstrap-expect 3 -data-dir /etc/consul.d -node=server-1 -bind=192.168.74.101 -ui -client 0.0.0.0 &
# 登录s2 虚拟机，以server形式运行
consul agent -server -bootstrap-expect 2 -data-dir /etc/consul.d -node=server-2 -bind=192.168.74.102 -ui -client 0.0.0.0 &
# 登录s3 虚拟机，以server形式运行
consul agent -server -bootstrap-expect 2 -data-dir /etc/consul.d -node=server-3 -bind=192.168.74.103 -ui -client 0.0.0.0 &
```

参数说明：

- `-server`：以server身份启动。
- `-bootstrap-expect`：集群要求的最少server数量，当低于这个数量，集群即失效。
- `-data-dir`：data存放的目录，更多信息请参阅consul数据同步机制
- `-node`：节点id，在同一集群不能重复。
- `-bind`：监听的ip地址。
- `-client`：客户端的ip地址(0.0.0.0表示不限制)
- `&`：在后台运行，此为linux脚本语法

至此三个Consul Server模式服务全部启动成功

- 启动 Consul Client

```shell
# 在本地电脑中使用client形式启动consul
consul agent -client=0.0.0.0  -data-dir /etc/consul.d -node=client-1
```

#### 4.3.3. 每个节点加入集群

在s2，s3，s4 服务其上通过consul join 命令加入 s1中的consul集群中

```shell
# 加入consul集群
consul join 192.168.74.101
```

#### 4.3.4. 测试

在任意一台服务器中输入 `consul members` 查看集群中的所有节点信息

```shell
# 查看consul集群节点信息
consul members
```

![](images/20201014172501670_23487.png)

![](images/20201014172506497_25688.png)

## 5. Consul 常见问题

### 5.1. 节点和服务注销

当服务或者节点失效，Consul不会对注册的信息进行剔除处理，仅仅标记已状态进行标记（并且不可使用）。如果担心失效节点和失效服务过多影响监控，可以通过调用HTTP API的形式进行处理。节点和服务的注销可以使用HTTP API：

- 注销任意节点和服务：`/catalog/deregister`
- 注销当前节点的服务：`/agent/service/deregister/:service_id`

如果某个节点不继续使用了，也可以在本机使用`consul leave`命令，或者在其它节点使用 `consul force-leave` 节点Id

### 5.2. 健康检查与故障转移

在集群环境下，健康检查是由服务注册到的Agent来处理的，那么如果这个Agent挂掉了，那么此节点的健康检查就处于无人管理的状态

从实际应用看，节点上的服务可能既要被发现，又要发现别的服务，如果节点挂掉了，仅提供被发现的功能实际上服务还是不可用的。当然发现别的服务也可以不使用本机节点，可以通过访问一个Nginx实现的若干Consul节点的负载均衡来实现

# Feign 服务调用

## 1. Feign简介

Feign是Netflix开发的声明式，模板化的HTTP客户端，其灵感来自Retrofit，JAXRS-2.0以及WebSocket

- Feign可更加便捷，优雅的调用HTTP API
- 在SpringCloud中，使用Feign非常简单——创建一个接口，并在接口上添加一些注解，代码就完成了
- Feign支持多种注解，例如Feign自带的注解或者JAX-RS注解等
- SpringCloud对Feign进行了增强，使Feign支持了SpringMVC注解，并整合了Ribbon和Eureka，从而让Feign的使用更加方便

## 2. 基于Feign的服务调用示例

### 2.1. 示例工程准备

复用之前eureka单机版的示例项目`02-springcloud-eureka`，命名为`06-springcloud-feign`

### 2.2. 引入Feign依赖

在服务消费者 `shop-service-order` 工程添加Feign依赖

```xml
<!-- SpringCloud整合的openFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 2.3. 开启Feign的支持

在服务消费者的启动类上，通过`@EnableFeignClients`注解开启Spring Cloud Feign的支持功能

```java
@SpringBootApplication(scanBasePackages = "com.moon.order")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
@EnableFeignClients // 开启Feign的支持
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

### 2.4. 创建Feign服务调用的接口

#### 2.4.1. 基础使用步骤

在服务消费者 `shop-service-order` 创建一个Feign接口，此接口是在Feign中调用微服务的核心接口。

```java
/*
 * @FeignClient 注解，用于标识当前接口为Feign调用微服务的核心接口
 *  value/name属性：指定需要调用的服务提供者的名称
 */
@FeignClient("shop-service-product") // 或者：@FeignClient(name = "shop-service-product")
public interface ProductFeignClient {

    /*
     * 创建需要调用的微服务接口方法，SpringCloud 对 Feign 进行了增强兼容了 SpringMVC 的注解
     *  在使用的两个注意点：
     *  1. FeignClient 接口有参数时，必须在参数加@PathVariable("XXX")和@RequestParam("XXX")注解，并且必须要指定对应的参数值（原来SpringMVC是可以省略）
     *  2. feignClient 返回值为复杂对象时，其对象类型必须有无参构造函数
     */
    @GetMapping("/product/{id}")
    Product findById(@PathVariable("id") Long id);

}
```

#### 2.4.2. 基础使用步骤总结

1. 启动类添加`@EnableFeignClients`注解，表示开启对Feign的支持，Spring会扫描标记了`@FeignClient`注解的接口，并生成此接口的代理对象
2. `@FeignClient`注解通过`name/value`属性指定需要调用的微服务的名称，用于创建Ribbon的负载均衡器。所以Ribbon从注册中心中获取服务列表，并通过负载均衡算法调用相应名称的服务。如：`@FeignClient("service-xxx")`即指定了服务提供者的名称`service-xxx`，Feign会从注册中心获取服务列表，并通过负载均衡算法进行服务调用名为`service-xxx`的服务
3. 在接口方法中使用`@GetMapping("/xxxx")`等SpringMVC的注解，指定调用的url，Feign将根据url进行远程调用

#### 2.4.3. Feign组件使用注意事项

- 定义接口方法对于形参绑定时，`@PathVariable`、`@RequestParam`、`@RequestHeader`等可以指定参数属性，在Feign中绑定参数必须通过`value`属性来指明具体的参数名，不然会抛出异常
- `FeignClient` 返回值为复杂对象时，其对象类型必须有无参构造函数

### 2.5. 配置消费者调用服务接口

修改消费者`shop-service-order`的`OrderController`控制类，注入`ProductFeignClient`接口实例，并在相应的方法中使用`ProductFeignClient`实例方法完成微服务调用即可

```java
@RestController
@RequestMapping("order")
public class OrderController {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    // 注入FeignClient服务调用接口
    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 根据商品id创建订单
     *
     * @param id 商品的id
     * @return
     */
    @PostMapping("/{id}")
    public String createOrder(@PathVariable Long id) {
        // 使用Feign组件实现服务远程调用，直接调用FeignClient的接口定义的相应方法即可
        Product product = productFeignClient.findById(id);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }
}
```

启动相应的服务，进行测试

## 3. Feign 和 Ribbon 的联系

- Ribbon 是一个基于 HTTP 和 TCP 客户端的负载均衡的工具。它可以在客户端配置`RibbonServerList`（服务端列表），使用 `HttpClient` 或 `RestTemplate` 模拟http请求，步骤比较繁琐
- Feign 是在 Ribbon 的基础上进行了一次改进，是一个使用起来更加方便的 HTTP 客户端。采用接口的方式，只需要创建一个接口，然后在上面添加注解即可，将需要调用的其他服务的方法定义成抽象方法即可，不需要自己构建http请求。然后就像是调用自身工程的方法调用，而感觉不到是调用远程方法，使得编写客户端变得非常容易

## 4. Feign 的负载均衡

Feign中本身已经集成了Ribbon依赖和自动配置，因此不需要额外引入依赖，也不需要再注册 `RestTemplate` 对象。x

配置负载均衡的方式与使用Ribbon的配置方式一致，即也可以通过修改项目配置文件中 `ribbon.xx` 来进行全局配置。也可以通过`服务名.ribbon.xx` 来对指定服务配置

启动两个`shop-service-product`服务，重新测试可以发现使用Ribbon的轮询策略进行负载均衡

![](images/20201015140621794_15061.png)

## 5. Feign 相关配置

### 5.1. Feign 可配置项说明

从Spring Cloud Edgware 版本开始，Feign支持使用属性自定义Feign。对于一个指定名称的Feign Client（例如该Feign Client的名称为 feignName ），Feign支持如下配置项：

```yml
# Feign 属性配置
feign:
  client:
    config:
      shop-service-product:  # 需要调用的服务名称
        connectTimeout: 5000 # 相当于Request.Options
        readTimeout: 5000 # 相当于Request.Options
        loggerLevel: full # 配置Feign的日志级别，相当于代码配置方式中的Logger
        errorDecoder: com.example.SimpleErrorDecoder # Feign的错误解码器，相当于代码配置方式中的ErrorDecoder
        retryer: com.example.SimpleRetryer # 配置重试，相当于代码配置方式中的Retryer
        requestInterceptors: # 配置拦截器，相当于代码配置方式中的RequestInterceptor
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false
```

部分属性配置说明：

- `feignName`：FeignClient的名称，即上面例子的`shop-service-product`
- `connectTimeout`：建立链接的超时时长
- `readTimeout`：读取超时时长
- `loggerLevel`：Feign的日志级别
- `errorDecoder`：Feign的错误解码器
- `retryer`：配置重试
- `requestInterceptors`：添加请求拦截器
- `decode404`：配置熔断不处理404异常

### 5.2. 请求压缩配置

Spring Cloud Feign 支持对请求和响应进行GZIP压缩，以减少通信过程中的性能损耗。通过下面的参数即可开启请求与响应的压缩功能：

```yml
feign:
  compression: # Feign 请求压缩配置
    request:
      enabled: true # 开启请求压缩
    response:
      enabled: true # 开启响应压缩
```

也可以对请求的数据类型，以及触发压缩的大小下限进行设置：

```yml
feign:
  compression: # Feign 请求压缩配置
    request:
      enabled: true # 开启请求压缩
      mime-types: text/html,application/xml,application/json # 设置压缩的数据类型
      min-request-size: 2048 # 设置触发压缩的大小下限
```

> 注：上面的数据类型、压缩大小下限均为默认值。

### 5.3. 日志级别

如果在开发或者运行阶段希望看到Feign请求过程的日志记录，默认情况下Feign的日志是没有开启的。要想用属性配置方式来达到日志效果，只需在 `application.yml` 中添加如下内容即可：

```yml
# 配置feign日志的输出
feign:
  client:
    config:
      shop-service-product:  # 需要调用的服务名称
        loggerLevel: full # 配置Feign的日志级别，相当于代码配置方式中的Logger
# 日志配置
logging:
  level:
    # 配置只输出ProductFeignClient接口的日志
    com.moon.order.feign.ProductFeignClient: debug
```

配置参数说明：

- `logging.level.xx: debug`：配置Feign只会对日志级别为debug的做出响应
- `feign.client.config.服务名称.loggerLevel`： 配置Feign的日志级别，其中Feign有以下四种日志级别：
    - `NONE`【性能最佳，适用于生产】：不记录任何日志（默认值）
    - `BASIC`【适用于生产环境追踪问题】：仅记录请求方法、URL、响应状态代码以及执行时间
    - `HEADERS`：记录BASIC级别的基础上，记录请求和响应的header。
    - `FULL`【比较适用于开发及测试环境定位问题】：记录请求和响应的header、body和元数据。

![](images/20201015210138012_24744.png)

## Feign 源码分析

