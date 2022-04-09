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

## 8. Eureka替换方案（Consul 注册中心）

### 8.1. Eureka 的替换方案

- **Zookeeper**

ZooKeeper是一个分布式的，开放源码的分布式应用程序协调服务，是Google的Chubby一个开源的实现，是Hadoop和Hbase的重要组件。它是一个为分布式应用提供一致性服务的软件，提供的功能包括：配置维护、域名服务、分布式同步、组服务等。

- **Consul**

consul是近几年比较流行的服务发现工具。consul的三个主要应用场景：服务发现、服务隔离、服务配置

- **Nacos**

Nacos 是阿里巴巴推出来的一个新开源项目，这是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施

### 8.2. Consul 注册中心入门

Consul 注册中心组件的详细介绍与使用，详见`code-learning-note\07-分布式架构&微服务架构\02-SpringCloud\05-Spring-Cloud-Consul.md`

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

# Feign 服务调用

Feign组件的详细介绍与使用，详见`code-learning-note\07-分布式架构&微服务架构\02-SpringCloud\04-Spring-Cloud-OpenFeign.md`

# Hystrix 服务熔断

## 1. 微服务容错处理的引入

### 1.1. 微服务架构高并发导致系统负载过高存在的问题分析

在微服务架构中，会将业务拆分成一个个的服务，服务与服务之间可以相互调用，由于网络原因或者自身的原因，服务并不能保证服务的100%可用，如果单个服务出现问题，调用这个服务就会出现网络延迟，此时若有大量的网络涌入，会形成任务累计，导致服务瘫痪。

在SpringBoot程序中，默认使用内置tomcat作为web服务器。单tomcat支持最大的并发请求是有限的，如果某一接口阻塞，待执行的任务积压越来越多，那么势必会影响其他接口的调用

### 1.2. 搭建模拟高并发并请求响应过慢的示例

> 复用前面`01-microservice-no-springcloud`项目代码，模拟高并发请求的案例

### 1.3. 模拟服务接口响应慢

1. 修改`shop-service-product`服务的`ProductController`控制类的`findById`方法，令线程睡眠2秒，模拟响应慢

```java
@GetMapping("/{id}")
public Product findById(@PathVariable Long id) {
    // 睡眠2秒，模拟请求响应慢
    try {
        Thread.sleep(2000L);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return productService.findById(id);
}
```

2. 修改`shop-service-order`服务的`application.yml`配置，调小tomcat容器最大连接数，模拟并发请求数量超出web容器可承受最大连接数

```yml
server:
  port: 9002 # 项目端口
  tomcat:
    max-threads: 10 # 修改web容器最大连接数
```

3. 修改`shop-service-order`服务的`OrderController`控制类，增加一个普通的请求方法，用于测试其他请求接口阻塞时，此请求的响应时间

```java
@GetMapping(value = "/{id}")
public String findOrder(@PathVariable Long id) {
    System.out.println(Thread.currentThread().getName());
    return "根据id查询订单";
}
```

4. 启动product与order服务，使用jMeter压力测试工具，创建一个每秒50并发的POST请求一直请求创建订单的接口，然后在浏览器中访问`http://localhost:9002/order/1`查询订单接口，观察此接口响应时间

单独请求的响应时间

![](images/20201018084511683_1502.png)

开启并发请求创建订单接口后，再次访问此查询接口的响应时间

![](images/20201018084802914_26721.png)

## 2. 基于线程池的形式实现服务隔离

> 改造`07-springcloud-concurrency-isolation`工程，实现线程池的隔离

### 2.1. 引入依赖

为了方便实现线以线程池的形式完成资源隔离，需要在`shop-service-order`工程中引入如下依赖

```xml
<!-- hystrix 组件的依赖，此示例用于实现基于线程池的形式完成服务的隔离 -->
<dependency>
    <groupId>com.netflix.hystrix</groupId>
    <artifactId>hystrix-metrics-event-stream</artifactId>
    <version>1.5.12</version>
</dependency>
<dependency>
    <groupId>com.netflix.hystrix</groupId>
    <artifactId>hystrix-javanica</artifactId>
    <version>1.5.12</version>
</dependency>
```

### 2.2. 分配线程池

配置`HystrixCommand`接口的实现类，在实现类中可以对线程池资源进行分配

```java
package com.moon.order.command;

import com.moon.entity.Product;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.springframework.web.client.RestTemplate;

/**
 * HystrixCommand的原生实现方式，对服务进行服务降级限流
 */
public class OrderCommand extends HystrixCommand<Product> {

    private RestTemplate restTemplate;

    private Long id;

    public OrderCommand(RestTemplate restTemplate, Long id) {
        super(setter());
        this.restTemplate = restTemplate;
        this.id = id;
    }

    private static Setter setter() {

        // 服务分组
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("order_product");
        // 服务标识
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("product");
        // 线程池名称
        HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("order_product_pool");
        /*
         * 线程池配置
         *     withCoreSize : 线程池大小为10
         *     withKeepAliveTimeMinutes : 线程存活时间15秒
         *     withQueueSizeRejectionThreshold : 队列等待的阈值为100,超过100执行拒绝策略
         */
        // 注：测试案例设置了tomcat最大线程数为10，所以这里设置线程池大小为5，实现此接口的线程数量控制。
        HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties.Setter().withCoreSize(5)
                .withKeepAliveTimeMinutes(15).withQueueSizeRejectionThreshold(100);

        // 命令属性配置Hystrix 开启超时
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                // 采用线程池方式实现服务隔离
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                // 禁止
                .withExecutionTimeoutEnabled(false);
        return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties).andCommandPropertiesDefaults(commandProperties);

    }

    @Override
    protected Product run() throws Exception {
        return restTemplate.getForObject("http://127.0.0.1:9001/product/" + id, Product.class);
    }

    /**
     * 服务降级方法
     *
     * @return
     */
    @Override
    protected Product getFallback() {
        Product product = new Product();
        product.setProductName("服务降级方法返回的数据");
        return product;
    }
}
```

### 2.3. 服务调用的改造

修改`OrderController`，使用自定义的`OrderCommand`完成调用

```java
@PostMapping("/{id}")
public String createOrder(@PathVariable Long id) {
    // 使用OrderCommand调用远程服务
    OrderCommand orderCommand = new OrderCommand(restTemplate, id);
    Product product = orderCommand.execute();
    LOGGER.info("当前下单的商品是: ${}", product);
    return "创建订单成功";
}
```

### 2.4. 测试

按上面案例测试方式，启动product与order服务，使用jMeter压力测试工具，创建一个每秒50并发的POST请求一直请求创建订单的接口，然后在浏览器中访问`http://localhost:9002/order/1`查询订单接口，观察此接口响应时间。因为控制了请求product服务的线程数量，所以此请求响应不会被影响

![](images/20201018113952944_5550.png)

## 3. Hystrix 介绍

Hystrix是由Netflix开源的一个延迟和容错库，用于隔离访问远程系统、服务或者第三方库，防止级联失败，从而提升系统的可用性与容错性。Hystrix主要通过以下几点实现延迟和容错。

- **包裹请求**：使用HystrixCommand包裹对依赖的调用逻辑，每个命令在独立线程中执行。这使用了设计模式中的“命令模式”。
- **跳闸机制**：当某服务的错误率超过一定的阈值时，Hystrix可以自动或手动跳闸，停止请求该服务一段时间。
- **资源隔离**：Hystrix为每个依赖都维护了一个小型的线程池（或者信号量）。如果该线程池已满，发往该依赖的请求就被立即拒绝，而不是排队等待，从而加速失败判定。
- **监控**：Hystrix可以近乎实时地监控运行指标和配置的变化，例如成功、失败、超时、以及被拒绝的请求等。
- **回退机制**：当请求失败、超时、被拒绝，或当断路器打开时，执行回退逻辑。回退逻辑由开发人员自行提供，例如返回一个缺省值。
- **自我修复**：断路器打开一段时间后，会自动进入“半开”状态。

## 4. Hystrix 对 RestTemplate 实现服务的熔断

### 4.1. 示例项目搭建

复用`04-springcloud-ribbon`工程的代码，创建新的工程`08-springcloud-hystrix-resttemplate`。整理删除一些无用的依赖与代码

### 4.2. 配置hystrix依赖

在 `shop-service-order` 模块中添加Hystrix的相关依赖

```xml
<!--引入hystrix依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

### 4.3. 开启熔断支持

在`shop-service-order`工程中，在`OrderApplication`启动类中添加`@EnableCircuitBreaker`注解，代表开启对熔断的支持

```java
@SpringBootApplication(scanBasePackages = "com.moon.order")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
@EnableCircuitBreaker // 开启hystrix熔断支持
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

注：随着功能的增多，微服务的启动类中的注解也越来越多。所以<font color=red>**SpringCloud提供了一个组合注解注解：`@SpringCloudApplication`。此注解相关于`@SpringBootApplication` + `@EnableDiscoveryClient` + `@EnableCircuitBreaker`**</font>，注解源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public @interface SpringCloudApplication {
}
```

对工程的启动类进行以下的改造，其效果以上面配置一致

```java
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
@SpringCloudApplication // 此组合注解相当于 @SpringBootApplication + @EnableDiscoveryClient + @EnableCircuitBreaker
public class OrderApplication {
}
```

### 4.4. 配置熔断降级业务逻辑

改造`shop-service-order`工程的`OrderController`控制类，增加熔断部分的逻辑

#### 4.4.1. 开启熔断与配置单个降级方法

1. 编写一个服务降级（回退）方法`orderFallBack`方法。有以下注意点
    - 此降级方法要与相应的熔断方法具有相同的参数列表
    - 此降级方法要与相应的熔断方法具有相同的返回值类型
2. 需要熔断的方法上，标识注解`@HystrixCommand`，代表此方法需要熔断，然后在注解的`fallbackMethod`属性，指定熔断触发的降级方法名称

```java
@RestController
@RequestMapping("order")
public class OrderController {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    // 注入HTTP请求工具类RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    /*
     * 标识 @HystrixCommand 注解代表此方法配置熔断保护
     *      fallbackMethod属性：指定熔断之后的降级方法
     */
    @HystrixCommand(fallbackMethod = "orderFallBack")
    @PostMapping("/{id}")
    public String createOrder(@PathVariable Long id) {
        // 调用服务
        Product product = restTemplate.getForObject("http://shop-service-product/product/" + id, Product.class);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }

    /*
     * 降级方法
     *  和需要收到保护的方法的返回值一致
     *  方法参数一致
     */
    public String orderFallBack(Long id) {
        LOGGER.info("当前下单商品的id是: " + id + "，触发createOrder熔断的降级方法");
        return "当前下单商品的id是: " + id + "，触发createOrder熔断的降级方法";
    }
}
```

3. 测试，当`shop-service-product`微服务正常时，浏览器访问`http://127.0.0.1:9002/order/1`可以正常调用服务提供者获取数据。当将商品微服务停止时继续访问，

![](images/20201018221924533_10212.png)

#### 4.4.2. 开启熔断与配置统一降级方法

如果当前类中每个方法的降级方法逻辑都一样，可以在当前类上标识`@DefaultProperties`注解，并在`defaultFallback`属性中指定公共的降级方法名称。

注：如果过在`@DefaultProperties`指定了公共的降级方法，则相应配置`@HystrixCommand`熔断保护的方法不需要单独指定了降级方法

```java
@RestController
@RequestMapping("order")
/*
 * @DefaultProperties注解用于指定此接口中公共的熔断设置，
 *  defaultFallback属性：用于公共的降级方法名称
 *  如果过在@DefaultProperties指定了公共的降级方法，在相应的熔断保护@HystrixCommand中不需要单独指定降级方法
 */
@DefaultProperties(defaultFallback = "defaultFallBack")
public class OrderController {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    // 注入HTTP请求工具类RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    /*
     * 标识 @HystrixCommand 注解代表此方法配置熔断保护
     *      fallbackMethod属性：指定熔断之后的降级方法
     */
    @HystrixCommand // 使用统一降级方法，则不需要再指定fallbackMethod属性
    @PostMapping("/{id}")
    public String createOrder(@PathVariable Long id) {
        // 调用服务
        Product product = restTemplate.getForObject("http://shop-service-product/product/" + id, Product.class);
        LOGGER.info("当前下单的商品是: ${}", product);
        return "创建订单成功";
    }

    /*
     * 公共的降级方法
     *  注意: 1.此方法不能有形参
     *       2.如果使用统一的降级方法，则最好统一所有需要熔断保护的方法的返回类型
     */
    public String defaultFallBack() {
        LOGGER.info("触发熔断公共降级方法");
        return "触发熔断公共降级方法";
    }
}
```

![](images/20201018223720618_4832.png)

### 4.5. 超时设置

使用Hystrix组件，请求在超过规定的时间没有获取到微服务的数据，此时会自动触发熔断降级方法，默认值是1000（1秒）。可以在`application.yml`项目配置文件中，修改`timeoutInMilliseconds`属性来设置地超时处理时长

```yml
# hystrix 配置
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000 # 配置连接超时时长，默认的连接超时时间1秒，即若1秒没有返回数据，自动的触发降级逻辑
```

## 5. Hystrix 对 Feign 实现服务的熔断

SpringCloud Fegin 默认已整合了 hystrix，所以添加Feign依赖后就不用在添加hystrix依赖

### 5.1. 示例项目搭建

复用`06-springcloud-feign`工程的代码，创建新的工程`09-springcloud-hystrix-feign`。整理删除一些无用的依赖与代码

### 5.2. 开启 hystrix

在 Feign 中已经内置了 hystrix，但是默认是关闭的。修改项目的 application.yml 配置文件开启对 hystrix 的支持

```yml
feign:
  hystrix:
    enabled: true # 开启对hystrix的支持，默认值是false（关闭）
```

### 5.3. 配置FeignClient接口的实现类

基于Feign实现熔断降级，需要创建`FeignClient`接口的实现类，在实现类中定义降级方法

```java
/**
 * ProductFeignClient接口实现，此类中实现的方法为相应的降级方法
 */
// Hystrix组件会在spring容器中查找FeignClient相应的实现类，调用其降级方法，所在需要标识注解注册到spring容器中
@Component
public class ProductFeignClientCallBack implements ProductFeignClient {
    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductFeignClientCallBack.class);

    /**
     * 此方法为ProductFeignClient接口中相应方法的降级实现
     */
    @Override
    public Product findById(Long id) {
        LOGGER.info("当前下单商品的id是: " + id + "，触发ProductFeignClientCallBack类中熔断的findById降级方法");
        Product product = new Product();
        product.setProductName("feign调用触发熔断降级方法");
        return product;
    }
}
```

### 5.4. FeignClient 接口指定 hystrix 熔断方法

在接口上的`@FeignClient`注解，添加`fallback`属性，声明降级方法（接口的实现类）

```java
/*
 * @FeignClient 注解，用于标识当前接口为Feign调用微服务的核心接口
 *  value/name属性：指定需要调用的服务提供者的名称
 *  fallback属性：配置熔断时降级方法（实现类）
 */
@FeignClient(name = "shop-service-product", fallback = ProductFeignClientCallBack.class)
public interface ProductFeignClient {
    @GetMapping("/product/{id}")
    Product findById(@PathVariable("id") Long id);
}
```

## 6. Hystrix的监控平台

### 6.1. Hystrix 的设置实时监控

**注意细节**：当请求失败，被拒绝，超时的时候，都会进入到降级方法中。但进入降级方法并不意味着断路器已经被打开。

除了实现容错功能，Hystrix还提供了近乎实时的监控，`HystrixCommand`和`HystrixObservableCommand`在执行时，会生成执行结果和运行指标。比如每秒的请求数量，成功数量等。

这些状态会暴露在`Actuator`提供的`/health`端点中。只需为项目添加 `spring-boot-actuator` 依赖，重启项目，访问`http://localhost:9002/actuator/hystrix.stream`，即可看到实时的监控数据。

#### 6.1.1. 引入依赖

在`09-springcloud-hystrix-feign`示例中，修改`shop-service-order`工程maven依赖

```xml
<!-- 引入hystrix的监控信息 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- hystrix 核心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### 6.1.2. 配置开启Hystrix支持

在`shop-service-order`工程启动类中，标识`@EnableCircuitBreaker`开启Hystrix支持

```javas
@SpringBootApplication(scanBasePackages = "com.moon.order")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
@EnableFeignClients // 开启Feign的支持
@EnableCircuitBreaker // 开启Hystrix支持
public class OrderApplication {
    // ....省略
}
```

> **注：想开启Hystrix实现监控的功能，必须标识`@EnableCircuitBreaker`注解**

#### 6.1.3. 设置暴露所有监控接口

默认Hystrix只暴露Actuator部分接口，访问`http://localhost:9001/actuator`可以查询到默认暴露的接口

![](images/20201019134944141_11016.png)

配置暴露所有Actuator监控端点，需要修改项目的`application.yml`文件，添加以下配置

```yml
# 配置暴露所有Actuator监控端点
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

#### 6.1.4. 访问实时监控页面

启动项目，访问`http://localhost:9002/actuator/hystrix.stream`，可以看到实时的监控数据。

### 6.2. Hystrix DashBoard 监控平台

Hystrix的实时监控，访问`/hystrix.stream`接口获取的都是已文字形式展示的信息。但通过文字的方式很难直观的展示系统的运行状态，所以Hystrix官方还提供了基于图形化的DashBoard（仪表板）监控平台。Hystrix仪表板可以显示每个断路器（被`@HystrixCommand`注解的方法）的状态。

#### 6.2.1. 引入依赖

搭建Hystrix DashBoard监控，配置工程的maven引入相关依赖

```xml
<!-- 引入hystrix的监控信息 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- hystrix 核心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<!-- Hystrix DashBoard 监控平台依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

#### 6.2.2. 开启监控平台

在项目的启动类中添加`@EnableHystrixDashboard`注解，表示激活监控平台（仪表盘）

```java
@SpringBootApplication(scanBasePackages = "com.moon.order")
@EntityScan("com.moon.entity") // 指定扫描实体类的包路径
@EnableFeignClients // 开启Feign的支持
@EnableCircuitBreaker // 开启Hystrix支持
@EnableHystrixDashboard // 激活监控平台（仪表盘）
public class OrderApplication {
}
```

#### 6.2.3. 测试访问监控平台

访问相应项目的url，`http://localhost:9002/hystrix`，进去平台初始化页面。

![](images/20201019143815171_30988.png)

输入实时监控断点url：`http://localhost:9002/actuator/hystrix.stream`，以图形化方式展示监控的详细数据

![](images/20201019144947097_1413.png)

### 6.3. 断路器聚合监控 Turbine

在微服务架构体系中，每个服务都需要配置Hystrix DashBoard监控。如果每次只能查看单个实例的监控数据，就需要不断切换监控地址，这显然很不方便。要想看这个系统的Hystrix Dashboard数据就需要用到Hystrix Turbine。Turbine是一个聚合Hystrix 监控数据的工具，他可以将所有相关微服务的Hystrix监控数据聚合到一起，方便使用。引入Turbine后，整个监控系统架构如下：

![](images/20201019150719838_6878.png)

> <font color=red>**注：每个需要监控的微服务，都需要配置相应Hystrix DashBoard监控。只是访问时通过聚合监控项目可以查询所有节点的情况**</font>

#### 6.3.1. 搭建聚合监控工程

创建工程`shop-server-hystrix-turbine`，引入相关依赖坐标

```xml
<!-- Hystrix Turbine 聚合监控核心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

#### 6.3.2. 配置多个微服务的hystrix监控

修改项目的`application.yml`配置文件，开启turbine并进行相关配置

```yml
server:
  port: 8031
spring:
  application:
    name: shop-server-hystrix-turbine
# 配置eureka服务，用于获取相关监控的微服务地址列表
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:8001/eureka/
  instance:
    prefer-ip-address: true
# hystrix turbine 配置
turbine:
  # 配置需要监控的微服务列表，多个服务之前使用,分隔
  appConfig: shop-service-order
  clusterNameExpression: "'default'"
```

- eureka相关配置：指定注册中心地址
- turbine相关配置：指定需要监控的微服务列表

turbine会自动的从注册中心中获取需要监控的微服务，并聚合所有微服务中的 `/hystrix.stream` 数据

#### 6.3.3. 配置启动类

作为一个独立的监控项目，需要配置启动类，使用`@EnableHystrixDashboard`注解开启Hystrix Dashboard监控平台；并使用`@EnableTurbine`注解激活Turbine

```java
@SpringBootApplication
@EnableTurbine // 开启 Turbine
@EnableHystrixDashboard // 开启Hystrix Dashboard监控平台
public class TurbineServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineServerApplication.class, args);
    }
}
```

#### 6.3.4. 测试访问

浏览器访问`http://localhost:8031/hystrix`展示Hystrix Dashboard。并在url位置输入 `http://localhost:8031/turbine.stream`，动态根据turbine.stream数据展示多个微服务的监控数据

## 7. 熔断器的状态

### 7.1. 简介

熔断器有三个状态：`CLOSED`、`OPEN`、`HALF_OPEN`，熔断器默认是关闭状态。

当触发熔断后状态变更为`OPEN`，在等待到指定的时间，Hystrix会放开部分请求用于检测服务是否开启，这期间熔断器会变为`HALF_OPEN`半开启状态，当熔断探测服务可用时，则会将状态变更为`CLOSED`，关闭熔断器

![](images/20201019160521917_6966.png)

- `Closed`：关闭状态（断路器关闭），所有请求都正常访问。代理类维护了最近调用失败的次数，如果某次调用失败，则使失败次数加1。如果最近失败次数超过了在给定时间内允许失败的阈值，则代理类切换到断开(Open)状态。此时代理开启了一个超时时钟，当该时钟超过了该时间，则切换到半断开（Half-Open）状态。该超时时间的设定是给了系统一次机会来修正导致调用失败的错误。
- `Open`：打开状态（断路器打开），所有请求都会被降级。Hystix会对请求情况计数，当一定时间内失败请求百分比达到阈值，则触发熔断，断路器会完全关闭。默认失败比例的阈值是50%，请求次数最少不低于20次。
- `Half Open`：半开状态，open状态不是永久的，打开后会进入休眠时间（默认是5s）。随后断路器会自动进入半开状态。此时会释放1次请求通过，若这个请求是健康的，则会关闭断路器，否则继续保持打开，再次进行5秒休眠计时。

### 7.2. 状态的测试

在`08-springcloud-hystrix-resttemplate`示例工程中进行熔断状态的测试。

#### 7.2.1. 测试的准备

为了能够精确控制请求的成功或失败，在`shop-service-product`工程的`ProductController`类的`findById`方法中调用业务中加入一段逻辑：

```java
@GetMapping("/{id}")
public Product findById(@PathVariable Long id) {
    // 为了能够精确控制请求的成功或失败，当id不为1时，抛出异常
    if (id != 1) {
        throw new RuntimeException("服务器异常");
    }
    // ....省略
}
```

当id为1时，正常请求；id不为1时，请求异常

为了更好观察测试结果，在`shop-service-order`引入Hystrix DashBoard 监控平台，修改项目配置文件`application.yml`，修改熔断器相应的默认配置：

```yml
# hystrix 配置
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000 # 配置连接超时时长，默认的连接超时时间1秒，即若1秒没有返回数据，自动的触发降级逻辑
      circuitBreaker:
        requestVolumeThreshold: 5 # 配置触发熔断的最小请求次数，默认20
        sleepWindowInMilliseconds: 10000 # 配置熔断多少秒后去尝试请求 默认5秒   打开状态的时间
        errorThresholdPercentage: 50 # 触发熔断的失败请求最小占比，默认50%
```

配置说明：

- `requestVolumeThreshold`：触发熔断的最小请求次数，默认20
- `errorThresholdPercentage`：触发熔断的失败请求最小占比，默认50%
- `sleepWindowInMilliseconds`：熔断多少秒后去尝试请求，默认5秒


#### 7.2.2. 测试结果分析

当连续访问id为2的请求时（超过10次），就会触发熔断。断路器会端口，一切请求都会被降级处理。此时访问id为1的请求，会发现返回的也是失败，而且失败时间很短，只有20毫秒左右：

![](images/20201019164832955_18166.png)

![](images/20201019164856714_13200.png)

![](images/20201019164924699_31656.png)

## 8. 熔断器的隔离策略

### 8.1. 两种隔离策略

微服务使用Hystrix熔断器实现了服务的自动降级，让微服务具备自我保护的能力，提升了系统的稳定性，也较好的解决雪崩效应。**其使用方式目前支持两种策略**：

- **线程池隔离策略**：使用一个线程池来存储当前的请求，线程池对请求作处理，设置任务返回处理超时时间，堆积的请求堆积入线程池队列。这种方式需要为每个依赖的服务申请线程池，有一定的资源消耗，好处是可以应对突发流量（流量洪峰来临时，处理不完可将数据存储到线程池队里慢慢处理）
- **信号量隔离策略**：使用一个原子计数器（或信号量）来记录当前有多少个线程在运行，请求来先判断计数器的数值，若超过设置的最大线程个数则丢弃改类型的新请求，若不超过则执行计数操作请求来计数器+1，请求返回计数器-1。这种方式是严格的控制线程且立即返回模式，无法应对突发流量（流量洪峰来临时，处理的线程超过数量，其他的请求会直接返回，不继续去请求依赖的服务）

### 8.2. 线程池和型号量两种策略功能支持对比

![](images/20201019170508303_8541.png)

### 8.3. 切换不同策略的配置

- `hystrix.command.default.execution.isolation.strategy`：配置隔离策略
    - `ExecutionIsolationStrategy.SEMAPHORE`：信号量隔离
    - `ExecutionIsolationStrategy.THREAD`：线程池隔离
- `hystrix.command.default.execution.isolation.maxConcurrentRequests`：最大信号量上限

```yml
hystrix:
  command:
    default:
      execution:
        isolation:
          # 配置隔离策略，取值：ExecutionIsolationStrategy.SEMAPHORE（信号量隔离）、ExecutionIsolationStrategy.THREAD（线程池隔离）
          strategy: ExecutionIsolationStrategy.SEMAPHORE # 信号量隔离
          maxConcurrentRequests: 20 # 配置最大信号量上限
```

## 9. Hystrix 核心源码分析

Hystrix 底层基于 RxJava，RxJava 是响应式编程开发库，因此Hystrix的整个实现策略简单说即：把一个`HystrixCommand`封装成一个`Observable`（待观察者），针对自身要实现的核心功能，对`Observable`进行各种装饰，并在订阅各步装饰的`Observable`，以便在指定事件到达时，添加自己的业务。

![](images/20201019172428063_32564.jpg)

### 9.1. Hystrix主要有4种调用方式

- `toObservable()`：未做订阅，只是返回一个Observable
- `observe()`：调用 `toObservable()` 方法，并向 `Observable` 注册 `rx.subjects.ReplaySubject` 发起订阅
- `queue()`：调用 `toObservable()` 方法的基础上，调用：`Observable.toBlocking()` 和 `BlockingObservable.toFuture()` 返回 `Future` 对象
- `execute()`：调用 `queue()` 方法的基础上，调用 `Future.get()` 方法，同步返回 `run()` 方法的执行结果。

### 9.2. 主要的执行逻辑

1. 每次调用创建一个新的`HystrixCommand`，把依赖调用封装在`run()`方法中
2. 执行`execute()`/`queue()`做同步或异步调用.
3. 判断熔断器(circuit-breaker)是否打开，如果打开跳到步骤8，进行降级策略，如果关闭进入步骤
4. 判断线程池/队列/信号量是否跑满，如果跑满进入降级步骤8，否则继续后续步骤
5. 调用`HystrixCommand`的`run`方法。运行依赖逻辑，依赖逻辑调用超时，进入步骤8
6. 判断逻辑是否调用成功。返回成功调用结果；调用出错，进入步骤8
7. 计算熔断器状态，所有的运行状态(成功，失败，拒绝，超时)上报给熔断器，用于统计从而判断熔断器状态
8. `getFallback()`降级逻辑。以下四种情况将触发`getFallback`的调用：
    1. `run()`方法抛出非`HystrixBadRequestException`异常
    2. `run()`方法调用超时
    3. 熔断器开启拦截调用
    4. 线程池/队列/信号量是否跑满
    5. 没有实现`getFallback`的`Command`将直接抛出异常，`fallback`降级逻辑调用成功直接返回，降级逻辑调用失败抛出异常
9. 返回执行成功结果

### 9.3. HystrixCommand 注解

通过`@HystrixCommand`注解能够更加简单快速的实现Hystrix的应用，查看`@HystrixCommand`注解源码，其中包含了诸多参数配置，如执行隔离策略，线程池定义等
```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HystrixCommand {
    String groupKey() default "";

    String commandKey() default "";

    String threadPoolKey() default "";

    String fallbackMethod() default "";

    HystrixProperty[] commandProperties() default {};

    HystrixProperty[] threadPoolProperties() default {};

    Class<? extends Throwable>[] ignoreExceptions() default {};

    ObservableExecutionMode observableExecutionMode() default ObservableExecutionMode.EAGER;

    HystrixException[] raiseHystrixExceptions() default {};

    String defaultFallback() default "";
}
```

其定义了`fallbackMethod`属性，其作用是提供了一个定义回退方法映射，在异常触发时此方法名对应的method将被触发执行，从而实现服务的降级。被 `@HystrixCommand` 注解的方法将会执行切面处理。其切面类是`HystrixCommandAspect.java`，其切点定义如下

```java
@Aspect
public class HystrixCommandAspect {
    private static final Map<HystrixPointcutType, MetaHolderFactory> META_HOLDER_FACTORY_MAP;

    static {
        META_HOLDER_FACTORY_MAP = ImmutableMap.<HystrixPointcutType, MetaHolderFactory>builder()
                .put(HystrixPointcutType.COMMAND, new CommandMetaHolderFactory())
                .put(HystrixPointcutType.COLLAPSER, new CollapserMetaHolderFactory())
                .build();
    }

    @Pointcut("@annotation(com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand)")
    public void hystrixCommandAnnotationPointcut() {}

    @Pointcut("@annotation(com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser)")
    public void hystrixCollapserAnnotationPointcut() {}

    @Around("hystrixCommandAnnotationPointcut() || hystrixCollapserAnnotationPointcut()")
    public Object methodsAnnotatedWithHystrixCommand(final ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethodFromTarget(joinPoint);
        Validate.notNull(method, "failed to get method from joinPoint: %s", joinPoint);
        if (method.isAnnotationPresent(HystrixCommand.class) && method.isAnnotationPresent(HystrixCollapser.class)) {
            throw new IllegalStateException("method cannot be annotated with HystrixCommand and HystrixCollapser " +
                    "annotations at the same time");
        }
        MetaHolderFactory metaHolderFactory = META_HOLDER_FACTORY_MAP.get(HystrixPointcutType.of(method));
        MetaHolder metaHolder = metaHolderFactory.create(joinPoint);
        HystrixInvokable invokable = HystrixCommandFactory.getInstance().create(metaHolder);
        ExecutionType executionType = metaHolder.isCollapserAnnotationPresent() ?
                metaHolder.getCollapserExecutionType() : metaHolder.getExecutionType();

        Object result;
        try {
            if (!metaHolder.isObservable()) {
                result = CommandExecutor.execute(invokable, executionType, metaHolder);
            } else {
                result = executeObservable(invokable, executionType, metaHolder);
            }
        } catch (HystrixBadRequestException e) {
            throw e.getCause();
        } catch (HystrixRuntimeException e) {
            throw hystrixRuntimeExceptionToThrowable(metaHolder, e);
        }
        return result;
    }
    // ....省略
}
```

在`HystrixCommandAspect`的`methodsAnnotatedWithHystrixCommand`方法，此方法通过环绕通知的形式对目标方法进行增强，主要作用如下：

- `HystrixInvokable`：定义了后续真正执行`HystrixCommand`的`GenericCommand`实例
- 定义`metaHolder`，包含了当前被注解方法的所有相关有效信息
- 执行方法：在进入执行体前，其有一个判断条件，判断其是否是一个`Observable`模式（在Hystrix中，其实现大量依赖RXJAVA，会无处不在的看到`Observable`，其是一种观察者模式的实现，具体可以到RxJava项目官方做更多了解）

## 10. Hystrix 服务熔断的替换方案

18年底Netflix官方宣布 Hystrix 已经足够稳定并且不再开发，该项目将处于维护模式。就目前来看Hystrix是比较稳定的，并且Hystrix只是停止开发新的版本，并不是完全停止维护，出现bug问题依然会维护的。因此短期内，Hystrix依然是继续使用的。但从长远来看，Hystrix总会达到它的生命周期，那么Spring Cloud生态中是否有替代产品呢？

### 10.1. 替换方案介绍

**Alibaba Sentinel**

Sentinel 是阿里巴巴开源的一款断路器实现，目前在Spring Cloud的孵化器项目Spring Cloud Alibaba中的一员Sentinel本身在阿里内部已经被大规模采用，非常稳定。因此可以作为一个较好的替代品

**Resilience4J**

Resilicence4J 一款非常轻量、简单，并且文档非常清晰、丰富的熔断工具，这也是Hystrix官方推荐的替代产品。不仅如此，Resilicence4j还原生支持Spring Boot 1.x/2.x，而且监控也不像Hystrix一样弄Dashboard/Hystrix等一堆轮子，而是支持和Micrometer（Pivotal开源的监控门面，Spring Boot 2.x中的Actuator就是基于Micrometer的）、prometheus（开源监控系统，来自谷歌的论文）、以及Dropwizard metrics（Spring Boot曾经的模仿对象，类似于Spring Boot）进行整合

### 10.2. Spring Cloud Alibaba Sentinel

Spring Cloud Alibaba Sentinel 组件详细介绍与使用，详见`code-learning-note\07-分布式架构&微服务架构\02-SpringCloud\03-Spring-Cloud-Alibaba.md`

### 10.3. Resilience4J（待学习与整理）

*待学习与整理！*

# Zuul 微服务网关

## 1. Zuul简介

Zuul是Netflix开源的微服务网关，它可以和Eureka、Ribbon、Hystrix等组件配合使用，Zuul组件的核心是一系列的过滤器，这些过滤器可以完成以下功能：

- 动态路由：动态将请求路由到不同后端集群
- 压力测试：逐渐增加指向集群的流量，以了解性能
- 负载分配：为每一种负载类型分配对应容量，并弃用超出限定值的请求
- 静态响应处理：边缘位置进行响应，避免转发到内部集群
- 身份认证和安全：识别每一个资源的验证要求，并拒绝那些不符的请求。Spring Cloud对Zuul进行了整合和增强。

## 2. Zuul 网关服务器搭建准备

创建Zuul组件的示例工程`11-springcloud-zuul`，复用之前`06-springcloud-feign`工程的代码，删除一些无用的内容

### 2.1. 创建zuul服务引入依赖

创建zuul网关工程 `shop-server-zuul` ，并添加zuul组件的依赖

```xml
<!-- spring cloud netflix zuul 组件核心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

### 2.2. 开启zuul网关功能

创建启动类 `ZuulServerApplication`，在启动类上标识`@EnableZuulProxy`注解开启zuul网关功能

```java
@SpringBootApplication
@EnableZuulProxy // 开启zuul网关功能
public class ZuulServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplication.class, args);
    }
}
```

### 2.3. 项目配置

创建工程的配置文件`application.yml`，并添加基础配置

```yml
server:
  port: 8080 # 项目端口
spring:
  application:
    name: shop-server-zuul # 服务名称
```

## 3. Zuul 的路由转发

**路由转发**是指根据请求URL，将请求分配到对应的处理程序。在微服务体系中，Zuul负责接收所有的请求。根据不同的URL匹配规则，将不同的请求转发到不同的微服务处理。

### 3.1. 基础配置

修改工程的`application.yml`配置文件，配置路由规则

```yml
# zuul 路由配置
zuul:
  routes:
    # 商品微服务
    shop-service-product: # 路由名称，名称任意，保持所有路由名称唯一即可
      path: /product-service/** # 配置映射路径，当前示例即：localhost:8080/product-service/xxxx/xx
      url: http://127.0.0.1:9001 # 配置请求映射路径时转发到对应的实际微服务url地址，可以是实际ip或者域名
      sensitiveHeaders: # 默认zuul会屏蔽cookie，cookie不会传到下游服务，这里设置为空则取消默认的黑名单，如果设置了具体的头信息则不会传到下游服务
```

配置项说明：

- `shop-service-product`：配置路由id，可以随意取名，只要保持配置中所有路由id唯一即可
- `path`：配置映射路径，上面示例代表所有前缀为`/product-service`的请求，都会转发到`http://127.0.0.1:9001`
- `url`：映射路径对应的实际url地址

Zuul路由配置后，启动服务，在浏览器中输入`http://localhost:8080/product-service/product/1`，即可访问到商品微服务

### 3.2. 通过注册中心方式的路由配置

对于一个URL请求，最终会确认一个服务实例进行处理。在实际项目众多微服务情况下，如果对每个服务实例手动指定一个唯一访问地址，然后根据URL去手动实现请求匹配，显然不可能的。

Zuul支持与Eureka整合开发，根据`ServiceID`自动的从注册中心中获取服务地址并转发请求，此配置方式不仅可以通过单个端点来访问应用的所有服务，而且在添加或移除服务实例的时候不用修改Zuul的路由配置

1. 修改网关服务，添加注册中心Eureka客户端的依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

2. 开启Eureka客户端发现功能。在启动类中标识`@EnableDiscoveryClient`。*注：从Spring Cloud Edgware版本开始，开启服务发现的注解可以省略不写。只需加上相关依赖与相应配置，即可注册服务*
3. 在`application.yml`中添加Eureka配置，获取服务信息

```yml
# Eureka 客户端配置
eureka:
  instance:
    prefer-ip-address: true # 将当前服务的ip地址注册到Eureka服务中
    instance-id: ${spring.cloud.client.ip-address}:${server.port}  # 指定实例id
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/ # Eureka server 地址，多个eureka server之间用,隔开
      registry-fetch-interval-seconds: 5 # 配置获取服务列表的周期为5s，默认值是30s
```

4. 修改zuul的映射配置，通过服务名称获取。引入Eureka客户端后，可以从Eureka获取服务的地址信息，因此映射时无需指定IP地址，而是通过服务名称来访问，而且Zuul已经集成了Ribbon的负载均衡功能

```yml
zuul:
  routes:
    # 商品微服务
    shop-service-product: # 路由名称，名称任意，保持所有路由名称唯一即可
      path: /product-service/** # 配置映射路径，当前示例即：localhost:8080/product-service/xxxx/xx
      serviceId: shop-service-product # 配置请求转发相应的服务名称，网关会从eureka中获取该服务名称下的服务实例的地址
      sensitiveHeaders: # 默认zuul会屏蔽cookie，cookie不会传到下游服务，这里设置为空则取消默认的黑名单，如果设置了具体的头信息则不会传到下游服务
```

配置项说明：

- `serviceId`：指定请求需要转发的微服务实例名称（服务注册到eureka的名称）

依次启动Eureka，商品微服务，API网关，在浏览器上通过访问 http://localhost:8080/product-service/product/1 查看最终效果。

### 3.3. 路由配置的简化方式

传统面向服务的路由配置规则是：

- `zuul.routes.<route>.path=/xxx/**`：来指定映射路径。`<route>`是自定义的路由名
- `zuul.routes.<route>.serviceId=/product-service`：来指定服务名

有一种特殊的情况：如果当`<route>`路由名称和服务名定义一样的时候，Zuul提供了一种简化的配置语法：`zuul.routes.<serviceId>=<path>`。上面示例的配置简化后如下：

```yml
zuul:
  routes:
    # 当路由的名称和服务名称一样的时候，zuul路由配置可简化成 zuul.routes.<serviceId>=<path>
    shop-service-product: /product-service/**
```

### 3.4. 默认的路由规则

Zuul不单单提供的路由的简化配置，zuul还有默认的路由规则。<font color=red>**默认的路由规则是：一切服务的映射路径就是服务的名称**</font>

例如：服务的名称为`shop-service-order`，则默认的映射路径就是`shop-service-order/**`。所以示例工程不需要配置order服务，zuul也可以成功转发请求

![](images/20201024100244732_27588.png)

![](images/20201024100422212_22770.png)

### 3.5. 通过注册中心方式配置zuul路由的架构图

![](images/20201024090229529_11017.png)

## 4. Zuul 的过滤器

<font color=red>**Zuul组件包含了两个核心功能：对请求的路由和过滤**</font>。

- **路由功能**负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础
- **过滤器功能**负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础

其实路由功能在真正运行时，它的路由映射和请求转发同样也由几个不同的过滤器完成的。所以**过滤器可以说是Zuul实现API网关功能最为核心的部件**，每一个进入Zuul的HTTP请求都会经过一系列的过滤器处理链得到请求响应并返回给客户端。

### 4.1. ZuulFilter 简介

Zuul中的过滤器跟Servlet使用的`javax.servlet.Filter`不一样，`javax.servlet.Filter`只有一种类型，可以通过配置 `urlPatterns` 来拦截对应的请求。而Zuul中的过滤器总共有4种类型，且每种类型都有对应的使用场景。

1. `PRE`：此过滤器在请求被路由之前调用。可利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等。
2. `ROUTING`：此过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netfilx Ribbon请求微服务。
3. `POST`：此过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTPHeader、收集统计信息和指标、将响应从微服务发送给客户端等。
4. `ERROR`：在其他阶段发生错误时执行该过滤器。

### 4.2. 过滤器的生命周期

![](images/20201024104130123_700.png)

**正常流程**：

- 请求到达首先会经过pre类型过滤器，而后到达routing类型，进行路由，请求就到达真正的服务提供者，执行请求，返回结果后，会到达post过滤器。而后返回响应

**异常流程**：

- 整个过程中，pre或者routing过滤器出现异常，都会直接进入error过滤器，再error处理完毕后，会将请求交给POST过滤器，最后返回给用户。
- 如果是error过滤器自己出现异常，最终也会进入POST过滤器，而后返回。
- 如果是POST过滤器出现异常，会跳转到error过滤器，但是与pre和routing不同的时，请求不会再到达POST过滤器了

**不同过滤器的场景**：

- 请求鉴权：一般放在pre类型，如果发现没有访问权限，直接就拦截了
- 异常处理：一般会在error类型和post类型过滤器中结合来处理
- 服务调用时长统计：pre和post结合使用。

![](images/20201024104434210_3724.png)

### 4.3. zuul内置过滤器

![](images/20201024104911792_30718.png)

### 4.4. 自定义zuul过滤器

#### 4.4.1. 基础用法

Zuul提供了自定义过滤器的功能，只需要编写一个类去实现zuul提供的`ZuulFilter`接口即可。

`ZuulFilter`是过滤器的顶级父类，其中定义的4个最重要的方法如下：

```java
public abstract class ZuulFilter implements IZuulFilter, Comparable<ZuulFilter> {

    boolean shouldFilter(); // 继承于IZuulFilter接口

    Object run() throws ZuulException; // 继承于IZuulFilter接口

    abstract public String filterType();

    abstract public int filterOrder();
}
```

- `shouldFilter()`：返回一个 Boolean 值，判断该过滤器是否需要执行。返回true执行，返回false 不执行
- `run()`：过滤器的具体业务逻辑
- `filterType()`：返回字符串，代表过滤器的类型。包含以下4种：
    - `pre`：请求在被路由之前执行
    - `routing`：在路由请求时调用
    - `post`：在`routing`和`errror`过滤器之后调用
    - `error`：处理请求时发生错误调用
- `filterOrder()`：通过返回`int`类型的值来定义过滤器的执行顺序，数字越小优先级越高

#### 4.4.2. 自定义过滤器示例

示例需求：自定义一个过滤器，模拟一个登录的校验。基本逻辑：如果请求头中有`access-token`参数，则认为请求有效，放行。

在`shop-server-zuul`工程中创建`LoginFilter`类，实现`ZuulFilter`接口，编写请求被路由前的业务逻辑

```java
package com.moon.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * zuul自定义过滤器示例 - 请求登陆校验过滤
 */
@Component // 自定义zuul过滤器，需要使用@Component标识该bean给spring容器管理
// 自定义zuul过滤器需要继承抽象父类ZuulFilter
public class LoginFilter extends ZuulFilter {
    /**
     * 设置过滤器类型
     * pre：请求在被路由之前执行
     * routing：在路由请求时调用
     * post：在routing和errror过滤器之后调用
     * error：处理请求时发生错误调用
     *
     * @return 过滤器的类型字符串
     */
    @Override
    public String filterType() {
        // 登录校验，设置为请求路由之前拦截
        return "pre";
    }

    /**
     * 指定过滤器的执行顺序，返回值越小越被优先执行
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 过滤器是否需要执行
     *
     * @return true: 执行此过滤器，false: 不执行
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的业务逻辑方法
     * <p>
     * 此示例完成身份认证:
     * 1.所有的请求需要携带一个参数: access-token
     * 2.获取request请求头"access-token"
     * 3.判断token是否为空
     * 4.1 token==null : 身份验证失败
     * 4.2 token!=null : 执行后续操作
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        // 在zuul网关中，通过RequestContext的上下文对象，可以获取HttpServletRequest和HttpServletResponse对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        // 获取请求头中access-token字段
        String token = request.getHeader("access-token");

        if (StringUtils.isEmpty(token)) {
            // 如果请求头不包含Authorization，则拒绝访问
            context.setSendZuulResponse(false);
            // 设置响应状态码
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            // 设置JSON格式的响应信息
            context.setResponseBody("{\"code\": \"-1\", \"retMsg\": \"此操作需要登陆系统！\"}");
            // 设置contentType为json类型
            response.setContentType("application/json;charset=UTF-8");
            // 还可以进行其他的操作
        }
        // 校验通过，返回继续向后执行。（也可以考虑把用户信息放入上下文）
        return null;
    }
}
```

上面示例的`RequestContext`是zuul用于在过滤器之间传递消息。它的数据保存在每个请求的`ThreadLocal`中。它用于存储请求路由到哪里、错误、`HttpServletRequest`、`HttpServletResponse`都存储在`RequestContext`中。`RequestContext`扩展了`ConcurrentHashMap`，所以，任何数据都可以存储在上下文中

启动服务进行测试：

![](images/20201024141450825_9895.png)

## 5. 服务网关Zuul的核心源码解析

### 5.1. Zuul 网关处理请求的流程

![](images/20201024141803070_6037.png)

在Zuul中，整个请求的过程是：

1. 首先将请求给`ZuulServlet`处理，`ZuulServlet`中有一个`ZuulRunner`对象，该对象中初始化了`RequestContext`，作为存储整个请求的一些数据，并被所有的`ZuulFilter`共享。
2. `ZuulRunner`中还有`FilterProcessor`，`FilterProcessor`作为执行所有的`ZuulFilter`的管理器。`FilterProcessor`从`FilterLoader`中获取`ZuulFilter`，而`ZuulFilter`是被`FilterFileManager`所加载，并支持groovy热加载，采用了轮询的方式热加载。
3. 有了这些filter之后，`ZuulServlet`首先执行的Pre类型的过滤器，再执行route类型的过滤器，最后执行的是post类型的过滤器，如果在执行这些过滤器有错误的时候则会执行error类型的过滤器。
4. 执行完这些过滤器，最终将请求的结果返回给客户端

### 5.2. zuul 的初始化

SpringCloud 对 Zuul 的封装使得发布一个`ZuulServer`，根据自动装载原则可以在 `spring-cloud-netflix-zuul-2.1.0.RELEASE.jar` 下找到 `spring.factories` 文件

![](images/20201024144431040_7136.png)

项目启动时会加载`ZuulServerAutoConfiguration`和`ZuulProxyAutoConfiguration`类，这两个类是Zuul服务端的自动配置类。其中`ZuulProxyAutoConfiguration`继承了`ZuulServerAutoConfiguration`

```java
@Configuration
@Import({ RibbonCommandFactoryConfiguration.RestClientRibbonConfiguration.class,
		RibbonCommandFactoryConfiguration.OkHttpRibbonConfiguration.class,
		RibbonCommandFactoryConfiguration.HttpClientRibbonConfiguration.class,
		HttpClientConfiguration.class })
@ConditionalOnBean(ZuulProxyMarkerConfiguration.Marker.class)
public class ZuulProxyAutoConfiguration extends ZuulServerAutoConfiguration {
    ....
}
```

查看`ZuulServerAutoConfiguration`源码，里面创建了几个zuul服务的核心类

```java
@Configuration
@EnableConfigurationProperties({ ZuulProperties.class })
@ConditionalOnClass({ZuulServlet.class, ZuulServletFilter.class})
@ConditionalOnBean(ZuulServerMarkerConfiguration.Marker.class)
public class ZuulServerAutoConfiguration {
    ....
    @Bean
	@Primary
	public CompositeRouteLocator primaryRouteLocator(
			Collection<RouteLocator> routeLocators) {
		return new CompositeRouteLocator(routeLocators);
	}

	@Bean
	@ConditionalOnMissingBean(SimpleRouteLocator.class)
	public SimpleRouteLocator simpleRouteLocator() {
		return new SimpleRouteLocator(this.server.getServlet().getContextPath(),
				this.zuulProperties);
	}

	@Bean
	public ZuulController zuulController() {
		return new ZuulController();
	}
	....
	@Configuration
	protected static class ZuulFilterConfiguration {

		@Autowired
		private Map<String, ZuulFilter> filters;

		@Bean
		public ZuulFilterInitializer zuulFilterInitializer(
				CounterFactory counterFactory, TracerFactory tracerFactory) {
			FilterLoader filterLoader = FilterLoader.getInstance();
			FilterRegistry filterRegistry = FilterRegistry.instance();
			return new ZuulFilterInitializer(this.filters, counterFactory, tracerFactory, filterLoader, filterRegistry);
		}
	}
	....
}
```

- `CompositeRouteLocator`：组合路由定位器，看入参就知道应该是会保存好多个`RouteLocator`，构造过程中其实仅包括一个`DiscoveryClientRouteLocator`实例
- `SimpleRouteLocator`：默认的路由定位器，主要负责维护配置文件中的路由配置。
- `ZuulController`：Zuul创建的一个Controller，用于将请求交由`ZuulServlet`处理。
- `ZuulHandlerMapping`：此类会添加到SpringMVC的`HandlerMapping`链中，只有选择了`ZuulHandlerMapping`的请求才能出发到Zuul的后续流程。
- `ZuulFilterInitializer`：在内部类`ZuulFilterConfiguration`中，注册`ZuulFilterInitializer`实例，通过`FilterLoader`加载应用中所有的过滤器并将过滤器注册到`FilterRegistry`

以下就是`ZuulFilterInitializer`是如何将过滤器加载到应用中的源码

```java
public class ZuulFilterInitializer {

	private static final Log log = LogFactory.getLog(ZuulFilterInitializer.class);

	private final Map<String, ZuulFilter> filters;
	private final CounterFactory counterFactory;
	private final TracerFactory tracerFactory;
	private final FilterLoader filterLoader;
	private final FilterRegistry filterRegistry;

	public ZuulFilterInitializer(Map<String, ZuulFilter> filters,
								 CounterFactory counterFactory,
								 TracerFactory tracerFactory,
								 FilterLoader filterLoader,
								 FilterRegistry filterRegistry) {
		this.filters = filters;
		this.counterFactory = counterFactory;
		this.tracerFactory = tracerFactory;
		this.filterLoader = filterLoader;
		this.filterRegistry = filterRegistry;
	}

	@PostConstruct
	public void contextInitialized() {
		log.info("Starting filter initializer");

		TracerFactory.initialize(tracerFactory);
		CounterFactory.initialize(counterFactory);

		for (Map.Entry<String, ZuulFilter> entry : this.filters.entrySet()) {
			filterRegistry.put(entry.getKey(), entry.getValue());
		}
	}

	@PreDestroy
	public void contextDestroyed() {
		log.info("Stopping filter initializer");
		for (Map.Entry<String, ZuulFilter> entry : this.filters.entrySet()) {
			filterRegistry.remove(entry.getKey());
		}
		clearLoaderCache();

		TracerFactory.initialize(null);
		CounterFactory.initialize(null);
	}

	private void clearLoaderCache() {
		Field field = ReflectionUtils.findField(FilterLoader.class, "hashFiltersByType");
		ReflectionUtils.makeAccessible(field);
		@SuppressWarnings("rawtypes")
		Map cache = (Map) ReflectionUtils.getField(field, filterLoader);
		cache.clear();
	}
}
```

### 5.3. zuul 处理请求的转发

在Zuul的`ZuulServerAutoConfiguration`自动配置类中，创建了`ZuulHandlerMapping`实例，此类为SpringMVC中`HandlerMapping`的拓展实现，会自动的添加到`HandlerMapping`链中。

```java
public class ZuulHandlerMapping extends AbstractUrlHandlerMapping {

	private final RouteLocator routeLocator;
	private final ZuulController zuul;
	private ErrorController errorController;
	private PathMatcher pathMatcher = new AntPathMatcher();
	private volatile boolean dirty = true;

	public ZuulHandlerMapping(RouteLocator routeLocator, ZuulController zuul) {
		this.routeLocator = routeLocator;
		this.zuul = zuul;
		setOrder(-200);
	}
    // ....省略
	private void registerHandlers() {
		Collection<Route> routes = this.routeLocator.getRoutes();
		if (routes.isEmpty()) {
			this.logger.warn("No routes found from RouteLocator");
		}
		else {
			for (Route route : routes) {
				registerHandler(route.getFullPath(), this.zuul);
			}
		}
	}
}
```

`ZuulHandlerMapping`其主要目的就是把所有请求路径导入到`ZuulController`中。另外的功能是当觉察`RouteLocator`路由表变更，则更新自己`dirty`状态，重新注册所有`Route`到`ZuulController`

```java
public class ZuulController extends ServletWrappingController {

	public ZuulController() {
	    // 在这里已经设置了ZuulServlet
		setServletClass(ZuulServlet.class);
		setServletName("zuul");
		setSupportedMethods((String[]) null); // Allow all
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			// We don't care about the other features of the base class, just want to handle the request
			// 在这里面会调用ZuulServlet的service方法
			return super.handleRequestInternal(request, response);
		}
		finally {
			RequestContext.getCurrentContext().unset();
		}
	}
}
```

在 `ZuulController` 中的 `handleRequest` 方法，会调用已经注册的 `ZuulServlet` 完成业务请求

```java
public class ZuulServlet extends HttpServlet {
    // ....省略
    @Override
    public void service(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) throws ServletException, IOException {
        try {
            init((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

            // Marks this request as having passed through the "Zuul engine", as opposed to servlets
            // explicitly bound in web.xml, for which requests will not have the same data attached
            RequestContext context = RequestContext.getCurrentContext();
            context.setZuulEngineRan();

            try {
                preRoute();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                route();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                postRoute();
            } catch (ZuulException e) {
                error(e);
                return;
            }

        } catch (Throwable e) {
            error(new ZuulException(e, 500, "UNHANDLED_EXCEPTION_" + e.getClass().getName()));
        } finally {
            RequestContext.getCurrentContext().unset();
        }
    }
    // ....省略
}
```

### 5.4. zuul 内置的过滤器

Zuul默认注入的过滤器可以在 `spring-cloud-netflix-core.jar` 中找到

![](images/20201024152851036_6958.png)

## 6. Zuul网关的替换方案

### 6.1. Zuul网关存在的问题

- **性能问题**

Zuul 1.x版本本质上就是一个同步Servlet，采用多线程阻塞模型进行请求转发。即每来一个请求，Servlet容器要为该请求分配一个线程专门负责处理这个请求，直到响应返回客户端这个线程才会被释放返回容器线程池。如果后台服务调用比较耗时，那么这个线程就会被阻塞，阻塞期间线程资源被占用，不能干其它事情。而Servlet容器线程池的大小是有限制的，当前端请求量大，而后台慢服务比较多时，很容易耗尽容器线程池内的线程，造成容器无法接受新的请求

- **不支持任何长连接**，如`WebSocket`

### 6.2. 替换方案 - Zuul2.x版本

Zuul2.x版本是对zuul的升级版，但并没有被Spring Cloud收录

### 6.3. 替换方案 - Spring Cloud Gateway

Spring Cloud Gateway 是 Spring Cloud 单独推出的微服务网关组件，比 Zuul 1.x 系列的性能和功能整体要好

> 详细的Spring Cloud Gateway说明与使用详见`code-learning-note\07-分布式架构&微服务架构\02-SpringCloud\06-Spring-Cloud-Gateway.md`







