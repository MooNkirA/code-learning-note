# Hmily - TCC 分布式事务解决方案

- Hmily 官方仓库：https://github.com/yu199195/hmily
- Hmily 官方文档：https://dromara.org/zh/projects/hmily/overview/

## 1. Hmily 概述

Hmily 是一款高性能，零侵入，金融级分布式事务解决方案，目前主要提供柔性事务的支持，包含 `TCC`, `TAC`(自动生成回滚SQL) 方案，未来还会支持 `XA` 等方案。

![](images/394434814220265.png)

### 1.1. 功能

Hmily 是一个高性能分布式事务tcc开源框架。基于java语言来开发（JDK1.8），支持多种 rpc 框架进行分布式事务。它目前支持以下特性：

- 高可靠性 ：支持分布式场景下，事务异常回滚，超时异常恢复，防止事务悬挂。
- 易用性 ：提供零侵入性式的 `Spring-Boot`, `Spring-Namespace` 快速与业务系统集成。
- 高性能 ：去中心化设计，与业务系统完全融合，天然支持集群部署。
- 可观测性 ：Metrics多项指标性能监控，以及admin管理后台UI展示。
- 多种RPC ： 支持 `Dubbo`, `SpringCloud`,`Motan`, `brpc`, `tars` 等知名RPC框架。
- 日志存储 ： 支持 `mysql`, `oracle`, `mongodb`, `redis`, `zookeeper` 等方式。
- 复杂场景 ： 支持RPC嵌套调用事务。

Hmily 利用 AOP 对参与分布式事务的本地方法与远程方法进行拦截处理，通过多方拦截，事务参与者能透明的调用到另一方的 Try、Confirm、Cancel 方法；传递事务上下文；并记录事务日志，酌情进行补偿，重试等。

Hmily 不需要事务协调服务，但需要提供一个数据库(mysql/mongodb/zookeeper/redis/file)来进行日志存储。Hmily 实现的 TCC 服务与普通的服务一样，只需要暴露一个接口，也就是它的 Try 业务。Confirm/Cancel 业务逻辑，只是因为全局事务提交/回滚的需要才提供的，因此 Confirm/Cancel 业务只需要被 Hmily 事务框架发现即可，不需要被调用它的其他业务服务所感知。

### 1.2. 使用必要前提

- 必须使用 `JDK8+`
- TCC 模式下，用户必须要使用一款 `RPC` 框架, 比如 : `Dubbo`, `SpringCloud`,`Motan`
- TAC 模式下，用户必须使用关系型数据库, 比如：`mysql`, `oracle`, `sqlsever`

### 1.3. TCC 模式

TCC模式是经典的柔性事务解决方案，需要使用者提供 `try`, `confirm`, `cancel` 三个方法， 真正的情况下会执行 `try`, `confirm`, 异常情况下会执行`try`, `cancel`。 `confirm` 方法并不是 必须的，完全依赖于用户的`try` 方法如何去写。 `confirm`, `cancel` 2个方法也需要用户去保证幂等性, 这会附加一定的工作量，由于在`try`方法完成之后，数据已经提交了，因此它并不保证数据的隔离性。但是这样，它的 性能相对较高，一个好的系统设计，是非常适用适用`TCC`模式。下面是`Hmily` 框架的 `TCC` 流程图

![](images/20795514238691.jpg)

当使用`TCC`模式的时候,用户根据自身业务需求提供 `try`, `confirm`, `cancel` 等三个方法， 并且 `confirm`, `cancel` 方法由自身完成实现，框架只是负责来调用，来达到事务的一致性。

- 在极端异常情况下，比如服务突然宕机，超时异常等，依赖与自身的调用任务，来进行日志的事务恢复。
- 在`confirm`, `cancel` 阶段，如果有任何异常会继续执行相应的阶段，如果超过最大重试次数还未成功，将不再进行重试，需要人工介入。
- 在服务集群的情况下，`confirm`, `cancel` 2个方法用户去尽量保证其幂等性。

### 1.4. TAC 模式

`TAC`模式其实是`TCC`模式的变种,顾名思义 `TAC` 模式被称为自动回滚,相比于 `TCC`模式，用户完全不用关心 回滚方法如何去写，减少了用户的开发量，对用户完全透明。

![](images/416755514226558.jpg)

当用户使用`TAC`模式的时候，用户必须使用关系型数据库来进行业务操作，框架会自动生成`回滚SQL`, 当业务异常的时候，会执行`回滚SQL`来达到事务的一致性

- `TAC` 模式只适合于关系型数据库。
- `TAC` 模式会拦截用户的 SQL 语句生成反向回滚 SQL，SQL 的兼容度也会是一大考验。

## 2. Hmily 快速入门（Spring-Cloud 版本）

> 注：项目使用不同的分布式框架，其引入的依赖与配置有不一样，此示例是使用 Spring Cloud 框架。可参考 [官方文档 - SpringCloud用户指南](https://dromara.org/zh/projects/hmily/user-springcloud/)

### 2.1. 案例业务说明

本案例通过hmily框架实现 TCC 分布式事务，模拟两个账户的转账交易过程。两个账户分别在不同的银行(张三在bank1、李四在bank2)，bank1、bank2是两个微服务。对于交易过程中的每个操作，要么都成功，要么都失败。
 
![](images/414135215235948.jpg)

### 2.2. 环境搭建

#### 2.2.1. 环境要求

- 数据库：MySQL 5.7.25+
- JDK： jdk1.8+
- 微服务：spring-boot-2.1.3、spring-cloud-Greenwich.RELEASE
- hmily：hmily-springcloud.2.0.4-RELEASE

#### 2.2.2. 数据库

执行以下脚本，创建测试数据库、表与测试数据

```sql
-- 创建 bank1 库，并导入以下表结构和数据：
DROP DATABASE IF EXISTS `bank1`;
CREATE DATABASE `bank1` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';

USE bank1;
DROP TABLE IF EXISTS `account_info`;
CREATE TABLE `account_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '户主姓名',
  `account_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '银行卡号',
  `account_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '帐户密码',
  `account_balance` double NULL DEFAULT NULL COMMENT '帐户余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

INSERT INTO `account_info` VALUES (1, '张三', '1', '', 10000);

-- 创建bank2库，并导入以下表结构和数据：
DROP DATABASE IF EXISTS `bank2`;
CREATE DATABASE `bank2` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';

USE bank2;
DROP TABLE IF EXISTS `account_info`;
CREATE TABLE `account_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '户主姓名',
  `account_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '银行卡号',
  `account_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '帐户密码',
  `account_balance` double NULL DEFAULT NULL COMMENT '帐户余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;
INSERT INTO `account_info` VALUES (2, '李四', '2', NULL, 0);
```

> <font color=purple>**Hmily 用来存储日志的数据表由它自动创建，在使用的过程中，会在项目的数据库中创建相应的表**</font>

### 2.3. 创建 Maven 示例工程

#### 2.3.1. 聚合工程

- 创建 pom 聚合工程 tcc-hmily-demo，进行依赖管理

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>hmily-springcloud</artifactId>
            <version>2.0.4-RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwich.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.1.3.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>javax.interceptor</groupId>
            <artifactId>javax.interceptor-api</artifactId>
            <version>1.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.0</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.16</version>
        </dependency>

        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <finalName>${project.name}</finalName>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <includes>
                <include>**/*</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
    </resources>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>

        <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <configuration>
                <encoding>utf-8</encoding>
                <useDefaultDelimiters>true</useDefaultDelimiters>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### 2.3.2. 服务注册中心

- 创建 hmily-demo-discover-server 工程，作为服务注册中心，引入相关依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

- 项目配置文件

```yml
spring:
  application:
    name: hmily-demo-discovery
server:
  port: 56080 #启动端口

eureka:
  server:
    enable-self-preservation: false    #关闭服务器自我保护，客户端心跳检测15分钟内错误达到80%服务会保护，导致别人还认为是好用的服务
    eviction-interval-timer-in-ms: 10000 # 清理间隔（单位毫秒，默认是60*1000）5秒将客户端剔除的服务在服务注册列表中剔除#
    shouldUseReadOnlyResponseCache: true # eureka是CAP理论种基于AP策略，为了保证强一致性关闭此切换CP 默认不关闭 false关闭
    response-cache-update-interval-ms: 3000  # eureka server刷新readCacheMap的时间，注意，client读取的是readCacheMap，这个时间决定了多久会把readWriteCacheMap的缓存更新到readCacheMap上 #eureka server刷新readCacheMap的时间，注意，client读取的是readCacheMap，这个时间决定了多久会把readWriteCacheMap的缓存更新到readCacheMap上默认30s
    response-cache-auto-expiration-in-seconds: 180   # eureka server缓存readWriteCacheMap失效时间，这个只有在这个时间过去后缓存才会失效，失效前不会更新，过期后从registry重新读取注册服务信息，registry是一个ConcurrentHashMap。
  client:
    register-with-eureka: false  # false:不作为一个客户端注册到注册中心
    fetch-registry: false      # 为true时，可以启动，但报异常：Cannot execute request on any known server
    instance-info-replication-interval-seconds: 10
    serviceUrl:
      defaultZone: http://localhost:${server.port}/eureka/
  instance:
    hostname: ${spring.cloud.client.ip-address}
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}:${spring.application.instance_id:${server.port}}
    lease-renewal-interval-in-seconds: 5    # 续约更新时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 10 # 续约到期时间（默认90秒）
```

- 启动类

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServer {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServer.class, args);
    }
}
```

#### 2.3.3. 创建微服务

- 创建 hmily-demo-bank1 工程，负责张三账户操作；创建 hmily-demo-bank2 工程，负责李四账户操作。同样引入以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.dromara</groupId>
        <artifactId>hmily-springcloud</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

### 2.4. 功能实现

此部分两个微服务工程的具体实现

#### 2.4.1. hmily-demo-bank1 转出操作工程

##### 2.4.1.1. 项目配置文件

- 项目配置 application.yml，*重点关注 hmily 部分的配置*

```yml
server:
  servlet:
    context-path: /bank1
  port: 56081

eureka:
  instance:
    preferIpAddress: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}:${spring.application.instance_id:${server.port}}
    lease-renewal-interval-in-seconds: 5    # 续约更新时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 10 # 续约到期时间（默认90秒）
  client:
    registry-fetch-interval-seconds: 5 # 抓取服务列表
    serviceUrl:
      defaultZone: http://localhost:56080/eureka/

spring:
  application:
    name: hmily-demo-bank1
  datasource:
    url: jdbc:mysql://localhost:3306/bank1?useUnicode=true&useSSL=true
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
# hmily 配置
org:
  dromara:
    hmily:
      serializer: kryo # 序列化工具
      retryMax: 2 # 最大重试次数
      repositorySupport: db # 持久化方式
      started: true # 是否事务发起方
      hmilyDbConfig:
        driverClassName: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/bank1?useUnicode=true&useSSL=true
        username: root
        password: 123456

ribbon:
  ConnectTimeout: 60000 # 设置连接超时时间 default 2000
  ReadTimeout: 60000   # 设置读取超时时间  default 5000
  OkToRetryOnAllOperations: true # 对所有操作请求都进行重试  default false
  MaxAutoRetriesNextServer: 2    # 切换实例的重试次数  default 1
  MaxAutoRetries: 1     # 对当前实例的重试次数 default 0
```

##### 2.4.1.2. 持久层相关接口与实体类

- 创建数据库表实体

```java
@Data
public class AccountInfo implements Serializable {
    private Long id;
    private String accountName;
    private String accountNo;
    private String accountPassword;
    private Double accountBalance;
}
```

- 创建数据库持久接口，分别定义增加、减少账户余额的方法，直接使用注解的方式定义sql语句

```java
@Mapper
@Repository
public interface AccountInfoDao {

    @Update("update account_info set account_balance = account_balance + #{amount} where account_no = #{accountNo}")
    int addAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);

    @Update("update account_info set account_balance = account_balance - #{amount} where account_no = #{accountNo}")
    int subtractAccountBalance(@Param("accountNo") String accountNo, @Param("amount") Double amount);
}
```

##### 2.4.1.3. feign 远程调用接口

- 创建 feign 远程调用接口 `Bank2Client`，使用分布式事务的接口需要标识 `@Hmily` 注解

```java
@FeignClient(value = "hmily-demo-bank2") // 调用的服务id
public interface Bank2Client {

    @GetMapping("/bank2/transfer")
    // @Hmily 注解为hmily分布式事务接口标识，表示该接口参与hmily分布式事务
    @Hmily
    Boolean transfer(@RequestParam("amount") Double amount);
}
```

##### 2.4.1.4. Hmily 配置类

- 创建 Hmily 配置类 `HmilyConfig`，创建 `HmilyTransactionBootstrap` 实例，设置配置文件中相关内容

```java
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class HmilyConfig {

    @Autowired
    private Environment env;

    @Bean
    public HmilyTransactionBootstrap hmilyTransactionBootstrap(HmilyInitService hmilyInitService) {
        HmilyTransactionBootstrap hmilyTransactionBootstrap = new HmilyTransactionBootstrap(hmilyInitService);
        hmilyTransactionBootstrap.setSerializer(env.getProperty("org.dromara.hmily.serializer"));
        hmilyTransactionBootstrap.setRetryMax(Integer.parseInt(env.getProperty("org.dromara.hmily.retryMax")));
        hmilyTransactionBootstrap.setRepositorySupport(env.getProperty("org.dromara.hmily.repositorySupport"));
        hmilyTransactionBootstrap.setStarted(Boolean.parseBoolean(env.getProperty("org.dromara.hmily.started")));
        HmilyDbConfig hmilyDbConfig = new HmilyDbConfig();
        hmilyDbConfig.setDriverClassName(env.getProperty("org.dromara.hmily.hmilyDbConfig.driverClassName"));
        hmilyDbConfig.setUrl(env.getProperty("org.dromara.hmily.hmilyDbConfig.url"));
        hmilyDbConfig.setUsername(env.getProperty("org.dromara.hmily.hmilyDbConfig.username"));
        hmilyDbConfig.setPassword(env.getProperty("org.dromara.hmily.hmilyDbConfig.password"));
        hmilyTransactionBootstrap.setHmilyDbConfig(hmilyDbConfig);
        return hmilyTransactionBootstrap;
    }
}
```

##### 2.4.1.5. 付款业务的 try、confirm、cancel 各个阶段实现

- 创建业务接口，分别实现转账业务功能 `try` 方法、成功提交 `confirm` 方法、失败回滚 `cancel` 方法

```java
@Service
public class AccountInfoTccServiceImpl implements AccountInfoTccService {

    @Autowired
    private AccountInfoDao accountInfoDao;

    @Autowired
    private Bank2Client bank2Client;

    /**
     * 业务方法，相当于 TCC 中的 try 阶段。
     * 在此方法上需要标识 @Hmily 注解，指定成功提交与失败回滚的方法
     */
    @Override
    @Hmily(confirmMethod = "commit", cancelMethod = "rollback")
    public void transfer(String accountNo, double amount) {
        System.out.println("******** Bank1 Service transfer begin...  ");
        // 执行账户扣减方法
        accountInfoDao.subtractAccountBalance(accountNo, amount);

        // 远程调用 bank2 收款方法
        if (!bank2Client.transfer(amount)) {
            throw new RuntimeException("bank2 exception");
        }
    }

    /**
     * 成功确认方法，在 try 阶段成功后执行
     */
    @Override
    public void commit(String accountNo, double amount) {
        System.out.println("******** Bank1 Service commit...");
    }

    /**
     * 失败回滚方法，在 try 阶段出现异常后执行
     */
    @Override
    public void rollback(String accountNo, double amount) {
        // 转账失败，调用账户增加方法
        accountInfoDao.addAccountBalance(accountNo, amount);
        System.out.println("******** Bank1 Service rollback...  ");
    }
}
```

> <font color=red>**注意：Try、Confirm、Cancel 的方法参数必须保持一致。**</font>

##### 2.4.1.6. 请求控制类与启动类

- 创建 bank1 的请求控制类，调用转账业务接口

```java
@RestController
public class Bank1Controller {

    @Autowired
    private AccountInfoTccService accountInfoTccService;

    @RequestMapping("/transfer")
    public String test(@RequestParam("amount") Double amount) {
        accountInfoTccService.transfer("1", amount);
        return "bank1向bank2转账:" + amount;
    }

}
```

> 只作测试，硬编码写死账号

- 创建项目启动类，在类中标识开启eureka与feign支持的注解，配置扫描 hmily 的包路径

```java
@SpringBootApplication(exclude = MongoAutoConfiguration.class, scanBasePackages = {"com.moon.hmilydemo.bank1", "org.dromara.hmily"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.moon.hmilydemo.bank1.feignClient"})
public class Bank1HmilyServer {
    public static void main(String[] args) {
        SpringApplication.run(Bank1HmilyServer.class, args);
    }
}
```

#### 2.4.2. hmily-demo-bank2 转入操作工程

##### 2.4.2.1. 项目配置文件

- 项目配置 application.yml，*重点关注 hmily 部分的配置*

```yml
server:
  servlet:
    context-path: /bank2
  port: 56082

eureka:
  instance:
    preferIpAddress: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}:${spring.application.instance_id:${server.port}}
    lease-renewal-interval-in-seconds: 5    # 续约更新时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 10 # 续约到期时间（默认90秒）
  client:
    registry-fetch-interval-seconds: 5 # 抓取服务列表
    serviceUrl:
      defaultZone: http://localhost:56080/eureka/

spring:
  application:
    name: hmily-demo-bank2
  datasource:
    url: jdbc:mysql://localhost:3306/bank2?useUnicode=true&useSSL=true
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
# hmily 配置
org:
  dromara:
    hmily:
      serializer: kryo # 序列化工具
      retryMax: 2 # 最大重试次数
      repositorySupport: db # 持久化方式
      started: false # 是否事务发起方，因为被调用方，所以不是事务的发起方
      hmilyDbConfig:
        driverClassName: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/bank2?useUnicode=true&useSSL=true
        username: root
        password: 123456
```

##### 2.4.2.2. 持久层相关接口与实体类

- 创建数据库表实体与数据库持久接口。*与 hmily-demo-bank1 工程一样*

##### 2.4.2.3. Hmily 配置类

- 创建 Hmily 配置类 `HmilyConfig`，创建 `HmilyTransactionBootstrap` 实例，设置配置文件中相关内容。*与 hmily-demo-bank1 工程一样*

##### 2.4.2.4. 收款业务实现

- 创建业务接口，分别实现转账业务功能 `try` 方法、成功提交 `confirm` 方法、失败回滚 `cancel` 方法

```java
@Service
public class AccountInfoTccServiceImpl implements AccountInfoTccService {

    @Autowired
    private AccountInfoDao accountInfoDao;

    /**
     * 业务方法，相当于 TCC 中的 try 阶段。
     * 在此方法上需要标识 @Hmily 注解，指定成功提交与失败回滚的方法
     */
    @Override
    @Transactional  // 本地事务，hmily 只会回滚远程调用时发现异常的事务。这里还是要处理本地事务
    @Hmily(confirmMethod = "commit", cancelMethod = "rollback")
    public Boolean updateAccountBalance(String accountNo, double amount) {
        System.out.println("******** Bank2 Service updateAccountBalance begin...  ");
        // 执行账户增加方法
        accountInfoDao.addAccountBalance(accountNo, amount);

        // 模拟出现异常
        if (Double.compare(amount, 44) == 0) {
            throw new RuntimeException("模拟异常！！！");
        }

        return true;
    }

    /**
     * 成功确认方法，在 try 阶段成功后执行
     */
    @Override
    public Boolean commit(String accountNo, double amount) {
        System.out.println("******** Bank2 Service commit...");
        return true;
    }

    /**
     * 失败回滚方法，在 try 阶段出现异常后执行
     */
    @Override
    public Boolean rollback(String accountNo, double amount) {
        // 在更新后失败，调用账户扣减方法
        accountInfoDao.subtractAccountBalance(accountNo, amount);
        System.out.println("******** Bank2 Service rollback...  ");
        return true;
    }
}
```

> <font color=red>**注意：这里的业务方法加入 `@Transactional` 注解是为了解决本地更新数据后可能会出现的异常，让本地事务回滚，因为 hmily 只会回滚远程调用服务时出现的异常**</font>

##### 2.4.2.5. 请求控制类与启动类

- 创建 bank2 的请求控制类，调用业务接口

```java
@RestController
public class Bank2Controller {

    @Autowired
    private AccountInfoTccService accountInfoTccService;

    @RequestMapping("/transfer")
    public Boolean transfer(@RequestParam("amount") Double amount) {
        return accountInfoTccService.updateAccountBalance("2", amount);
    }
}
```

> 只作测试，硬编码写死账号

- 创建项目启动类，在类中标识开启 eureka 支持的注解，配置扫描 hmily 的包路径

```java
@SpringBootApplication(exclude = MongoAutoConfiguration.class, scanBasePackages = {"com.moon.hmilydemo.bank2", "org.dromara.hmily"})
@EnableDiscoveryClient
public class Bank2HmilyServer {
    public static void main(String[] args) {
        SpringApplication.run(Bank2HmilyServer.class, args);
    }
}
```

### 2.5. 功能测试场景

- bank1与bank2都执行成功
- bank1执行成功，bank2出现异常，此时bank1回滚

## 3. 其他

### 3.1. 与 feign 框架冲突的问题

这个问题在万信金融项目实战中发现，在项目中使用了 Hmily 保证分布式事务的一致性，但其他不需要使用 Hmily 的 Feign 接口调用时会报 NullPointerException，具体问题与解决方案详见 [《第05章 用户开户》笔记](/项目资料/万信金融/05-用户开户)
