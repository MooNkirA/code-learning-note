> 本笔记主要介绍 Spring Boot 使用缓存的各种解决方案。
>
> 代码示例：https://github.com/MooNkirA/spring-boot-note/tree/spring-boot-2.5.x-sample/spring-boot-2.5.x-sample/14-spring-boot-caching

## 1. 概述

### 1.1. 缓存简介

企业级应用主要作用是信息处理，当需要读取数据时，由于受限于数据库的访问效率，导致整体系统性能偏低。应用程序直接与数据库打交道，访问效率低。

![](images/337271616246928.png)

为了改善上述现象，开发者通常会在应用程序与数据库之间建立一种临时的数据存储机制，该区域中的数据在内存中保存，读写速度较快，可以有效解决数据库访问效率低下的问题。这一块临时存储数据的区域就是**缓存**。使用缓存后，应用程序与缓存打交道，缓存与数据库打交道，数据访问效率提高

![](images/528802116239597.png)

综上所述，缓存是一种介于数据永久存储介质与应用程序之间的数据临时存储介质，使用缓存可以有效的减少低速数据读取过程的次数（例如磁盘IO），提高系统性能。此外缓存不仅可以用于提高永久性存储介质的数据读取效率，还可以提供临时的数据存储空间。而 Spring Boot 提供了对市面上几乎所有的缓存技术提供了整合的方案

### 1.2. Spring Boot 所支持的缓存方案

> 支持的缓存技术参考官网文档：https://docs.spring.io/spring-boot/docs/2.5.13/reference/html/features.html#features.caching.provider

Spring Boot 将缓存抽象成 `org.springframework.cache.Cache` 和 `org.springframework.cache.CacheManager` 接口，Spring Boot 会**按下面所示顺序**去尝试检测是否有相应的缓存实现

1. Generic
2. JCache (JSR-107) (EhCache 3, Hazelcast, Infinispan, and others)
3. EhCache 2.x
4. Hazelcast
5. Infinispan
6. Couchbase
7. Redis
8. Caffeine
9. Simple（默认值）

## 2. Spring Boot 内置缓存

Spring Boot 技术提供有内置的缓存解决方案，可以帮助开发者快速开启缓存技术，并使用缓存技术进行数据的快速操作，例如读取缓存数据和写入数据到缓存。

### 2.1. 基础示例工程准备

#### 2.1.1. 项目依赖

在项目的 pom.xml 文件引入相关依赖，为了方便，使用 h2 数据库的内存模式

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.4.3</version>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

#### 2.1.2. 项目配置与数据库脚本

- 创建项目项目文件 application.yml

```yml
server:
  port: 80
spring:
  h2:
    console:
      enabled: true # 开启 H2 数据库控制台访问程序
      path: /h2
  datasource:
    url: jdbc:h2:mem:h2_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE
    driver-class-name: org.h2.Driver
    username: root
    password: 123456
    schema: classpath:db/schema-h2.sql # H2 初始化表结构
    data: classpath:db/data-h2.sql # H2 初始化数据
mybatis-plus: # mybatis-plus 配置
  global-config:
    db-config:
      id-type: auto
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

- 创建数据库初始化表结构与数据脚本

```sql
-- schema-h2.sql
DROP TABLE IF EXISTS `book`;

CREATE TABLE `book`(
    `id`          BIGINT (20),
    `name`        VARCHAR(30),
    `type`        VARCHAR(10),
    `description` VARCHAR(200),
    PRIMARY KEY (`id`)
);

-- data-h2.sql
DELETE FROM `book`;

INSERT INTO `book`(`id`, `name`, `type`, `description`)
VALUES (1, 'SpringBoot入门', '计算机科学', '这是一本好书'),
       (2, 'Spring基础', '计算机科学', '人人都在看'),
       (3, '金田一少年之事件薄', '漫画', '最好的推理漫画');
```

#### 2.1.3. 基础业务代码

- 表映射实体类

```java
@Data
public class Book {
    private Integer id;
    private String type;
    private String name;
    private String description;
}
```

- 持久层接口

```java
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
```

- 业务层

```java
public interface BookService {
    Book getById(Integer id);
}

// 实现类
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Book getById(Integer id) {
        return bookMapper.selectById(id);
    }
}
```

- 请求控制层

```java
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("{id}")
    public Book get(@PathVariable Integer id) {
        return bookService.getById(id);
    }
}
```

- 启动类

### 2.2. 实现缓存数据库数据

#### 2.2.1. 引入缓存技术依赖

在 pom.xml 文件中引入 Spring Boot 提供的缓存技术相应的依赖 spring-boot-starter-cache

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

#### 2.2.2. 启用缓存

在引导类（或者配置类）上标注注解 `@EnableCaching`，配置 Spring Boot 程序启用缓存

```java
@SpringBootApplication
@EnableCaching // 开启缓存功能
public class EmbeddedCachingApplication {
    public static void main(String[] args) {
        SpringApplication.run((EmbeddedCachingApplication.class), args);
    }
}
```

#### 2.2.3. 业务方法使用缓存

在需要使用缓存的业务方法上，标识 `@Cacheable` 注解，声明当前方法的返回值放入缓存中，其中 `value` 属性指定缓存的存储位置；`key` 属性指定当前方法返回值保存在缓存中对应的名称（键名）。

```java
/*
 * @Cacheable 用于标识当前方法，将返回保存到缓存中
 *  value 属性：缓存存储的位置（存储空间名称）
 *  key 属性：方法返回结果保存的缓存名称。
 */
@Cacheable(value = "dbDataCache", key = "#id")
@Override
public Book getById(Integer id) {
    return bookMapper.selectById(id);
}
```

注：上例中 `value` 属性描述缓存的存储位置，可以理解为是一个存储空间名，`key` 属性描述了缓存中保存数据的名称，字符串 `#id` 代表读取形参中的 `id` 值作为缓存名称。

#### 2.2.4. 功能测试

运行测试。方法使用 `@Cacheable` 注解后，在被执行时，会有以下两种情况：

- 如果发现对应名称在缓存中没有数据，就正常读取数据，然后自动放入缓存
- 如果对应名称在缓存中有数据，就终止当前业务方法执行，直接返回缓存中的数据。

### 2.3. 实现缓存临时数据

缓存技术除将保存数据库的数据，还可以将一些临时数据缓存，以下示例是模拟使用缓存保存手机验证码的过程。

#### 2.3.1. 案例需求

- 输入手机号获取验证码，以短信形式发送给用户（后台模拟）
- 输入手机号和验证码验证结果

> 示例没有前端页面，使用 postman 等工具模拟请求

#### 2.3.2. 基础项目工程准备

复用上个章节的示例工程代码，使用 Spring Boot 提供的内置缓存技术实现案例需求

#### 2.3.3. 业务实现

要实现以上案例需求，创建两个表现层接口，一个用于模拟发送短信的过程，其实就是根据用户提供的手机号生成一个验证码，然后放入缓存；另一个用于模拟验证码校验的过程，其实就是使用传入的手机号和验证码进行匹配，并返回最终匹配结果。

- 创建定义短信验证码实体类，封装手机号与验证码两个属性

```java
@Data
public class SMSCode {
    private String tel;
    private String code;
}
```

- 创建业务层接口与实现，分别定义获取验证码与校验验证码方法

```java
public interface SMSCodeService {
    /**
     * 获取短信验证码
     */
    String sendSMSCode(String tel);

    /**
     * 校验短信验证码
     */
    boolean checkCode(SMSCode smsCode);
}
```

获取验证码后，当验证码失效时必须重新获取验证码，因此在获取验证码的功能上不能使用 `@Cacheable` 注解，因为 `@Cacheable` 注解是缓存中没有值则放入值，缓存中有值则取值。但获取验证码功能仅仅是生成验证码并放入缓存，再次调用时就不能从缓存中取值后返回，因此不能使用 `@Cacheable` 注解

根据上述分析，生成验证码文件需要使用 `@CachePut` 注解，该注解标识的方法仅具有向缓存中保存数据的功能，返回时并不会从缓存取值。`@CachePut` 注解的属性值作用与 `@Cacheable` 注解属性一样

```java
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    @Autowired
    private CodeUtil codeUtil;

    // @CachePut 注解标识的方法仅具有向缓存中保存数据的功能，返回时并不会从缓存取值
    @CachePut(value = "smsCodeCache", key = "#tel")
    @Override
    public String sendSMSCode(String tel) {
        // 生成验证码并返回，注：只会保存到缓存中，再次调用时并不会从缓存中读取
        return codeUtil.generateGode(tel);
    }

    @Override
    public boolean checkCode(SMSCode smsCode) {
        // 取出缓存中的验证码与接收的验证码比对，如果相同，返回true
        String code = smsCode.getCode();
        String codeCache = codeUtil.getCodeCache(smsCode.getTel());
        return code.equals(codeCache);
    }
}
```

- 生成验证码的方法只能往缓存中保存数据，所以需要再定义一个方法，用于读取缓存中的数据。因此创建一个工具类，定义读取缓存中验证码方法，该方法上标识 `@Cacheable` 注解，指定缓存空间名称与缓存数据的名称，方法返回 `null` 即可

```java
@Component
public class CodeUtil {

    private final String[] patch = {"000000", "00000", "0000", "000", "00", "0", ""};

    /** 简单的实现生成验证码 */
    public String generateGode(String tel) {
        int hash = tel.hashCode();
        int encryption = 20206666;
        long result = hash ^ encryption;
        long nowTime = System.currentTimeMillis();
        result = result ^ nowTime;
        long code = result % 1000000;
        code = code < 0 ? -code : code;
        String codeStr = code + "";
        int len = codeStr.length();
        return patch[len] + codeStr;
    }

    // 此方法只需要从缓存取值，所以在方法上标识 @Cacheable 注解，方法的返回值为 null 即可
    @Cacheable(value = "smsCodeCache", key = "#tel")
    public String getCodeCache(String tel) {
        return null;
    }
}
```

> <font color=red>**值得注意：为什么不将获取缓存的方法也定义在业务接口（`SMSCodeService`）中？因为 Spring Boot 对方法实现缓存功能是通过代理对象去调用方法来实现，如果在本类中定义读取缓存的方法，调用是通过 `this` 本身实例去调用方法，而不是通过代理去调用，只会调用原生的方法。所以需要将读取缓存的方法定义到其他类中，并且将其加入到 Spring 容器进行管理**</font>

- 创建请求 web 层接口，定义两个方法，一个方法用于提供手机号获取验证码，另一个方法用于提供手机号和验证码进行校验

```java
@RestController
@RequestMapping("/sms")
public class SMSCodeController {

    @Autowired
    private SMSCodeService smsCodeService;

    /* 获取验证码 */
    @GetMapping
    public String getCode(String tel){
        return smsCodeService.sendSMSCode(tel);
    }

    /* 校验验证码 */
    @PostMapping
    public boolean checkCode(@RequestBody SMSCode smsCode){
        return smsCodeService.checkCode(smsCode);
    }
}
```

#### 2.3.4. 功能测试

- 获取短信验证码。发送 GET 请求 http://localhost/sms?tel=13865845254
- 根据上一步返回值，发送 POST 请求校验接口，上送手机号与验证码

```json
POST http://localhost/sms

{
    "tel": "13865845254",
    "code": "199786"
}
```

## 3. Spring Boot 整合 Ehcache 缓存

Ehcache 是一种缓存技术，使用 Spring Boot 整合 Ehcache 本质就是变更一下缓存技术的实现方式

### 3.1. 基础示例工程准备

> 直接使用上面内置缓存的示例工程代码（只保留手机验证码功能，移除相关依赖）

### 3.2. 实现缓存功能

#### 3.2.1. 引入 Ehcache 依赖

在项目的 pom.xml 文件中引入 Ehcache 的依赖坐标，不需要指定版本，Spring Boot 父工程已经有依赖管理

```xml
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

> 这里引入的依赖不是以 starter-xxx 的格式，而是直接引入相应的技术坐标。原因是 Spring Boot 整合缓存技术做的是通用格式，不管是整合哪种第三方的缓存技术，只是改变了其具体的实现，原来的使用方式不变。这体现了 Spring Boot 统一同类技术整合方式的优点

#### 3.2.2. Ehcache 配置

在项目的 application.yml 文件中，通过 `spring.cache.type` 配置缓存技术实现类型为 `ehcache`。值得注意的是，当前 Spring Boot 可以整合的缓存技术中包含有 `ehcach`，所以才能这样配置。此 `type` 属性不可以随便写一个名称就可以整合相关缓存技术，需要当前 Spring Boot 支持。

```yml
spring:
  cache:
    type: ehcache # 指定缓存实现的类型
    ehcache:
      config: ehcache.xml # 配置 Ehcache 缓存技术专用的配置文件
```

由于 ehcache 的使用时，有其独立的配置文件格式，因此需要通过 `spring.cache.ehcache.config` 属性指定 ehcache 的配置文件，以便于读取相应配置。ehcache.xml 文件内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <diskStore path="E:\logs\ehcache"/>

    <!--
       默认缓存策略
         external：是否永久存在，设置为true则不会被清除，此时与timeout冲突，通常设置为false
         diskPersistent：是否启用磁盘持久化
         maxElementsInMemory：最大缓存数量
         overflowToDisk：超过最大缓存数量是否持久化到磁盘
         timeToIdleSeconds：最大不活动间隔，设置过长缓存容易溢出，设置过短无效果，可用于记录时效性数据，例如验证码
         timeToLiveSeconds：最大存活时间
         memoryStoreEvictionPolicy：缓存清除策略
     -->
    <defaultCache eternal="false"
                  diskPersistent="false"
                  maxElementsInMemory="1000"
                  overflowToDisk="false"
                  timeToIdleSeconds="60"
                  timeToLiveSeconds="60"
                  memoryStoreEvictionPolicy="LRU"/>

    <!-- 注意：cache 标签配置缓存 name 属性与代码是加入缓存空间名称要保持一致 -->
    <cache name="smsCodeCache"
           eternal="false"
           diskPersistent="false"
           maxElementsInMemory="1000"
           overflowToDisk="false"
           timeToIdleSeconds="10"
           timeToLiveSeconds="10"
           memoryStoreEvictionPolicy="LRU"/>

</ehcache>
```

值得注意 `<cache>` 标签的 `name` 属性要与案例中数据保存的位置名称（`smsCodeCache`）一致。这个设定需要保障 ehcache 中有一个缓存空间名称叫做 `smsCodeCache` 的配置，并且前后要统一。在企业开发过程中，通过设置不同名称的 `<cache>` 标识来设定不同的缓存策略，应用于不同的缓存数据。

#### 3.2.3. 功能测试

经过以上步骤，Spring Boot 整合 Ehcache 已完成。原始代码并没有任何修改，仅仅是加了一组配置就可以变更缓存供应商了，这也是 Spring Boot 提供了统一的缓存操作接口的优势，变更缓存实现并不影响原始编写的代码。

> 参考内置缓存案例的步骤进行测试即可

## 4. Spring Boot 整合 Redis 缓存

Spring Boot 整合 Redis 实现缓存步骤与上面整合 Ehcache 缓存技术一样，引入相关依赖后，修改缓存类型为 redis，在项目配置文件中设置 redis 相关信息

### 4.1. 基础示例工程准备

> 直接使用上面内置缓存的示例工程代码（只保留手机验证码功能，移除相关依赖）

### 4.2. 实现缓存功能

#### 4.2.1. 引入 redis 依赖

在项目的 pom.xml 文件中引入 spring-boot-starter-data-redis 的依赖坐标，不需要指定版本，Spring Boot 父工程已经有依赖管理

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 4.2.2. redis 配置

在项目的 application.yml 文件中，通过 `spring.cache.type` 配置缓存技术实现类型为 `redis`，另外还需要配置 redis 服务的连接信息。

```yml
spring:
  cache:
    type: redis # 指定缓存实现的类型
    redis:
      use-key-prefix: true # 配置是否给保存缓存的键增加前缀
      key-prefix: sms_ # 配置键前缀
      cache-null-values: true # 配置缓存值是否可以为 null
      time-to-live: 300s # 缓存键过期的时间
  redis: # redis 服务连接信息
    host: localhost
    port: 6379
```

> 注意：如果需要对 redis 作为缓存进行配置，注意不是对原始的 redis 节点进行配置，而是在 `spring.cache.redis` 节点下设置缓存相关配置，注意不要写错位置

#### 4.2.3. 功能测试

经过以上步骤，Spring Boot 整合 Redis 作为缓存已完成，变更缓存实现并不影响原始编写的代码。参考内置缓存案例的步骤进行测试即可

## 5. Spring Boot 整合 Memcached 缓存技术

### 5.1. Memcached 缓存服务概述

Memcached 是一个自由开源的，高性能，分布式内存对象缓存系统。基于内存的 key-value 存储，用来存储小块的任意数据（字符串、对象）。这些数据可以是数据库调用、API 调用或者是页面渲染的结果。

Memcached 简洁而强大。它的简洁设计便于快速开发，减轻开发难度，解决了大数据量缓存的很多问题。它的 API 兼容大部分流行的开发语言。

本质上，它是一个简洁的key-value存储系统。一般的使用其目的是，通过缓存数据库查询结果，减少数据库访问次数，以提高动态 Web 应用的速度、提高可扩展性。

Memcached 官网：https://www.memcached.org/

### 5.2. Memcached 缓存服务安装（windows 版）

官网上并未提供 Memcached 的 Windows 平台安装包。windows 版安装包下载地址：https://www.runoob.com/memcached/window-install-memcached.html

> 注：在 1.4.5 版本以前 memcached 可以作为一个服务安装，而在 1.4.5 及之后的版本删除了该功能。

- 下载的安装包是解压缩就能使用的zip文件，解压到指定目录即可。解压后的目录中可执行文件只有一个 memcached.exe。使用该文件可以将 memcached 作为系统服务启动
- 在 1.4.5 版本以前 memcached 可以作为一个服务安装，使用<font color=red>管理员权限运行 CMD 命令行窗口</font>，执行以下命令：

```bash
memcached目录路径\memcached.exe -d install
```

> 注：使用实际的路径替代 `memcached目录路径\`

- 服务安装完毕后，可以使用以下命令启动和停止 memcached 服务，memcached 默认对外服务端口11211。或者在运行中输入 `services.msc` 命令打开【服务】窗口，直接手动启动或者停止服务

```bash
memcached目录路径\memcached.exe -d start # 启动服务
memcached目录路径\memcached.exe -d stop  # 停止服务
```

![](images/547344423220470.png)

- 如果要修改 memcached 的配置项，可以运行中执行 `regedit` 命令，打开注册表并找到 "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\memcached" 来进行修改。如：要提供 memcached 使用的缓存配置可以修改 `ImagePath` 为：

```bash
"memcached目录路径\memcached.exe" -d runservice -m 512
```

> 注：-m 512 意思是设置 memcached 最大的缓存配置为512M。还可以通过使用 `memcached目录路径\memcached.exe -h` 命令查看更多的参数配置。

- 如需要卸载 memcached，可以打开管理员权限的命令行，执行以下命令：

```bash
memcached目录路径\memcached.exe -d uninstall
```

### 5.3. 整合 Memcached 缓存技术

与前面章节中整合的其他缓存技术不同，由于 Memcached 未被 Spring Boot 收录为缓存解决方案，因此整合 Memcached 需要通过手工硬编码的方式来实现。

Memcached 目前提供有三种客户端技术，分别是 Memcached Client for Java、SpyMemcached 和 Xmemcached，其中性能指标各方面最好的客户端是 Xmemcached，所以下面整合示例使用其作为客户端实现技术了。

#### 5.3.1. 基础示例工程准备

> 直接使用上面内置缓存的示例工程代码（只保留手机验证码功能，移除相关依赖）

#### 5.3.2. 引入 Xmemcached 客户端依赖

在项目的 pom.xml 文件中引入 Xmemcached 的依赖坐标，需要指定版本。在 [MavenRepository](https://mvnrepository.com/) 上查询

```xml
<dependency>
    <groupId>com.googlecode.xmemcached</groupId>
    <artifactId>xmemcached</artifactId>
    <version>2.4.7</version>
</dependency>
```

#### 5.3.3. 配置 memcached

- Spring Boot 并没有收录管理 memcached，因此配置文件中没有相关的设置项。但 Memcached 的使用也有必需的配置，所以在项目的 application.yml 文件定义 memcached 节点信息

```yml
# 自定义的 Memcached 配置，用于配置类读取
memcached:
  servers: localhost:11211
  poolSize: 10
  opTimeout: 3000
```

- 定义配置类，读取项目文件加载配置文件中 memcached 节点配置属性

```java
@Data
@Configuration
@ConfigurationProperties(prefix = "memcached")
public class XMemcachedProperties {
    private String servers;
    private int poolSize;
    private long opTimeout;
}
```

- 创建 memcached 的配置类，通过上面配置文件映射类，获取节点配置值，创建 Memcached 客户端实例 MemcachedClient

```java
@Configuration
public class XMemcachedConfig {

    // 注入配置文件映射类
    @Autowired
    private XMemcachedProperties memcachedProperties;

    /**
     * 创建 Memcached 客户端实例 MemcachedClient
     *
     * @return
     * @throws IOException
     */
    @Bean
    public MemcachedClient getMemcachedClient() throws IOException {
        // 创建 MemcachedClientBuilder，设置配置文件的节点信息
        MemcachedClientBuilder memcachedClientBuilder = new XMemcachedClientBuilder(memcachedProperties.getServers());
        memcachedClientBuilder.setConnectionPoolSize(memcachedProperties.getPoolSize());
        memcachedClientBuilder.setOpTimeout(memcachedProperties.getOpTimeout());
        return memcachedClientBuilder.build();
    }
}
```

#### 5.3.4. xmemcached 客户端的使用

在业务类中注入 `MemcachedClient` 对象，编写 xmemcached 客户端操作缓存代码，`MemcachedClient` 对象的 `set` 方法实现设置值到缓存中，再通过 `get` 方法来从缓存中获取值

```java
@Autowired
private MemcachedClient memcachedClient;

/**
 * 获取短信验证码
 */
@Override
public String sendSMSCode(String tel) {
    // 生成验证码
    String code = generateGode(tel);
    try {
        /*
         * 通过 set 方法，将验证保存到缓存中
         *  set(final String key, final int exp, final Object value)
         *      key 参数：保存在缓存中键名称
         *      exp 参数：缓存中过期时间，单位：秒
         *      value 参数：保存的值
         */
        memcachedClient.set(tel, 10, code);
    } catch (Exception e) {
        e.printStackTrace();
    }
    // 返回验证码
    return code;
}

/**
 * 校验短信验证码
 */
@Override
public boolean checkCode(SMSCode smsCode) {
    String code = smsCode.getCode();
    String codeCache = null;
    try {
        // 根据键获取缓存中的验证码
        codeCache = memcachedClient.get(smsCode.getTel());
    } catch (Exception e) {
        e.printStackTrace();
    }
    // 取出缓存中的验证码与接收的验证码比对，如果相同，返回true
    return code.equals(codeCache);
}
```

#### 5.3.5. 功能测试

1. 启动 Memcached 服务后，再启动示例项目
2. 发送 GET 请求 http://localhost/sms?tel=13865845254 ，获取短信验证码。
3. 根据上一步返回值，发送 POST 请求校验接口，上送手机号与验证码

```json
POST http://localhost/sms

{
    "tel": "13865845254",
    "code": "199786"
}
```

4. 观察返回结果，正常返回 `true`，继续发送多次请求，但10秒后，缓存失效，此时请求返回 `false`

## 6. Spring Boot 整合 Jetcache 缓存技术

### 6.1. 目前缓解方案存在的问题分析

上面章节中的缓存解决方案中，Redis 需要安装独立的服务器，连接时需要输入对应的服务器地址，属于**远程缓存**；而 Ehcache 是一个典型的内存级缓存，启动后导入 jar 包即有缓存功能，属于**本地缓存**。

Spring Boot 目前针对缓存技术的整合仅仅停留在使用缓存，因为目前整合的缓存技术，都不能同时支持远程缓存和本地缓存。

### 6.2. Jetcache 概述

> JetCache 官网：https://github.com/alibaba/jetcache

JetCache 是由阿里巴巴开源的一款通用缓存访问框架。它实现把别的缓存技术放到 jetcache 中管理，从而实现了既支持远程缓存，也支持本地缓存。

需要明确一点，Jetcache 并不是随意的缓存都能进行管理。目前 jetcache 支持的缓存方案有两款本地缓存，有两款远程缓存，分别如下：

- 本地缓存（Local）
  - LinkedHashMap
  - Caffeine
- 远程缓存（Remote）
  - Redis
  - Tair

### 6.3. 整合 Jetcache 示例

#### 6.3.1. 基础示例工程准备

> 直接使用上面内置缓存的示例工程代码（分别有临时数据与数据库数据的案例），将原有的缓存注解等相关代码移除

#### 6.3.2. 纯远程缓存方案

- 导入 Spring Boot 整合 jetcache 对应的坐标 starter，当前坐标默认使用的远程方案是 redis

```xml
<dependency>
    <groupId>com.alicp.jetcache</groupId>
    <artifactId>jetcache-starter-redis</artifactId>
    <version>2.6.2</version>
</dependency>
```

- 在 application.yml 文件中，配置远程缓存方案基本信息

```yml
jetcache:
  remote: # 设置远程缓存方案
    default: # 方案名称为 default，默认的
      type: redis # 缓存实现类型
      host: localhost
      port: 6379
      poolConfig:
        maxTotal: 50
```

> 注意：其中 `poolConfig` 是必配项，否则程序启动时会报错

- 在启动类或者配置类上标识 `@EnableCreateCacheAnnotation` 注解，配置 Spring Boot 程序中可以使用注解的形式创建缓存

```java
@SpringBootApplication
@EnableCreateCacheAnnotation // jetcache 启用缓存支持
public class JetcacheApplication {
    public static void main(String[] args) {
        SpringApplication.run((JetcacheApplication.class), args);
    }
}
```

- 在业务类中创建缓存对象 `Cache` 的属性，在该属性上标识 `@CreateCache` 注解，配置当前缓存对象的信息，然后使用 `Cache` 对象的 API 操作缓存，`put`方法用于写入缓存，`get`方法用于读缓存。

```java
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    /*
     *  Cache 类是 jetCache 的缓存对象
     *  @CreateCache 注解用于配置当前缓存对象的信息
     *      area 属性：设置选择配置文件中哪个缓存方案，默认值是 default
     *      name 属性：设置缓存键的前缀
     *      expire 属性：设置缓存的过期时间，默认单位是秒
     *      timeUnit 属性：设置过期时间的单位，默认值是秒，即 TimeUnit.SECONDS
     */
    @CreateCache(area = "default", name = "jetCache_", expire = 10, timeUnit = TimeUnit.SECONDS)
    private Cache<String, String> jetCache;

    /**
     * 获取短信验证码
     */
    @Override
    public String sendSMSCode(String tel) {
        // 生成验证码
        String code = generateGode(tel);
        // 使用 Cache 类的 put 方法，将数据加入缓存
        jetCache.put(tel, code);
        return code;
    }

    /**
     * 校验短信验证码
     */
    @Override
    public boolean checkCode(SMSCode smsCode) {
        String code = smsCode.getCode();
        // 使用 Cache 类的 get 方法，根据键从缓存获取数据
        String codeCache = jetCache.get(smsCode.getTel());
        return code.equals(codeCache);
    }

    // ...省略验证码生成方法部分代码
}
```

> 注：如果 `@CreateCache` 注解的 `area` 与 `timeUnit` 的值为默认值，则可以省略

启动程序进行测试，以上配置最终在 redis 生成的键结构如下：

![](images/178113422220471.png)

上述方案中使用的是配置中定义的 default 名称的缓存方案，default 这名称是 jetCache 在 `@CreateCache` 注解没有配置 `area` 属性时，默认使用的名称。因此使用者可以添加多个不同名称的缓存方案，参照如下配置进行：

```yml
jetcache:
  remote: # 设置远程缓存方案
    default: # 方案名称为 default，默认的
      type: redis # 缓存实现类型
      host: localhost
      port: 6379
      poolConfig:
        maxTotal: 50
    sms: # 可以设置多套缓存方案
      type: redis
      host: localhost
      port: 6379
      poolConfig:
        maxTotal: 50
```

如果想使用名称为 sms 的缓存方案，需要通过缓存对象属性的 `@CreateCache` 注解配置 `area` 属性，指定使用的缓存方案名称即可

```java
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    @CreateCache(area = "default", name = "jetCache_", expire = 10, timeUnit = TimeUnit.SECONDS)
    private Cache<String, String> jetCache;
    // 可以创建多套不同的缓存方案，其中 area 属性指定使用的缓存方案名称即可
    @CreateCache(area = "sms", name = "jetCache_", expire = 2, timeUnit = TimeUnit.MINUTES)
    private Cache<String, String> smsCache;
    
    // ...省略代码
}
```

#### 6.3.3. 纯本地缓存方案

纯本地缓存方案与远程方案的使用大致一致，只是配置从 `remote` 节点换成 `local` 节点，并且 `type` 属性缓存类型不相同

- 导入 Spring Boot 整合 jetcache 对应的坐标 starter（与纯远程缓存方案时一样）
- 在 application.yml 文件中，在 `jetcache.local` 节点中配置本地缓存方案基本信息

```yml
jetcache:
  local: # 设置本地缓存方案
    default: # 方案名称为 default，默认的
      type: linkedhashmap # 配置本地缓存的类型是 linkedhashmap
      keyConvertor: fastjson # 配置键类型转换器，推荐使用 fastjson
```

注：上述配置中，为了加速数据获取时 key 的匹配速度，jetcache 要求指定 key 的类型转换器(`keyConvertor`)。原因是假如给了一个 `Object` 类型作为 key 的话，则使用 key 的类型转换器将对象转换成字符串，然后再保存。等到获取数据时，仍然是先使用给定的 `Object` 类型转换成字符串，然后根据字符串匹配。由于 jetcache 是阿里的技术，因此推荐 key 的类型转换器使用阿里的 fastjson

- 在启动类或者配置类上标识 `@EnableCreateCacheAnnotation` 注解，开启使用注解的形式创建缓存的支持
- 与纯远程缓存方案一样，在业务类中缓存对象 `Cache` 的属性上标识 `@CreateCache` 注解，通过注解的 `cacheType` 属性来指定使用远程缓存还是本地缓存，默认是使用远程缓存（`CacheType.REMOTE`）。配置 `cacheType = CacheType.LOCAL` 即使用本地缓存。

```java
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    // cacheType 属性用于指定使用远程缓存还是本地缓存，默认是远程缓存(CacheType.REMOTE)
    @CreateCache(area = "default", name = "jetCache_", expire = 10, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.LOCAL)
    private Cache<String, String> jetCache;
    
    // ...省略代码
}
```

#### 6.3.4. 本地+远程方案

- 如果本地缓存与远程缓存同时存在时，可以配置文件中，同时配置两种缓存

```yml
jetcache:
  local: # 设置本地缓存方案
    default: # 方案名称为 default，默认的
      type: linkedhashmap # 配置本地缓存的类型是 linkedhashmap
      keyConvertor: fastjson # 配置键类型转换器，推荐使用 fastjson
  remote: # 设置远程缓存方案
    default: # 方案名称为 default，默认的
      type: redis # 缓存实现类型
      host: localhost
      port: 6379
      poolConfig:
        maxTotal: 50
    sms: # 可以设置多套缓存方案
      type: redis
      host: localhost
      port: 6379
      poolConfig:
        maxTotal: 50
```

- 在业务类中缓存对象 `Cache` 的属性的 `@CreateCache` 注解，配置 `cacheType = CacheType.BOTH`，即本地缓存与远程缓存同时使用。

```java
// cacheType = CacheType.BOTH 配置同时使用本地缓存和远程缓存
@CreateCache(area = "default", name = "jetCache_", expire = 10, cacheType = CacheType.BOTH)
```

> 注意：`@CreateCache` 注解的 `cacheType` 属性如果不进行配置，默认值是 `CacheType.REMOTE`，即仅使用远程缓存方案

#### 6.3.5. jetcache 的相关配置

|                             属性                             | 默认值 |                             说明                             |
| ----------------------------------------------------------- | ----- | ----------------------------------------------------------- |
| `jetcache.statIntervalMinutes`                              | 0     | 统计间隔，0 表示不统计                                         |
| `jetcache.hiddenPackages`                                   | 无     | 自动生成 name 时，隐藏指定的包名前缀                             |
| `jetcache.[local\|remote].${area}.type`                     | 无     | 缓存类型，本地支持 linkedhashmap、caffeine，远程支持 redis、tair |
| `jetcache.[local\|remote].${area}.keyConvertor`             | 无     | key 转换器，当前仅支持 fastjson                                |
| `jetcache.[local\|remote].${area}.valueEncoder`             | java  | 仅 remote 类型的缓存需要指定，可选 java 和 kryo                  |
| `jetcache.[local\|remote].${area}.valueDecoder`             | java  | 仅 remote 类型的缓存需要指定，可选 java 和 kryo                  |
| `jetcache.[local\|remote].${area}.limit`                    | 100   | 仅 local 类型的缓存需要指定，缓存实例最大元素数                   |
| `jetcache.[local\|remote].${area}.expireAfterWriteInMillis` | 无穷大 | 默认过期时间，毫秒单位                                          |
| `jetcache.local.${area}.expireAfterAccessInMillis`          | 0     | 仅 local 类型的缓存有效，毫秒单位，最大不活动间隔                  |

#### 6.3.6. 方法缓存

jetcache 与 spring cache 一样，提供了方法缓存方案，在方法上标识注解，方法即自动使用缓存，只是两者的注解的名称不一样。在对应的操作接口方法上使用注解 `@Cached` 即可

- 导入 Spring Boot 整合 jetcache 对应的坐标 starter（与上述缓存方案一样）
- 在 application.yml 文件中，同时本地缓存与远程缓存。

```java
jetcache:
  local: # 设置本地缓存方案
    default: # 方案名称为 default，默认的
      type: linkedhashmap # 配置本地缓存的类型是 linkedhashmap
      keyConvertor: fastjson # 配置键类型转换器，当前仅支持 fastjson
  remote: # 设置远程缓存方案
    default: # 方案名称为 default，默认的
      type: redis # 缓存实现类型
      host: localhost
      port: 6379
      keyConvertor: fastjson # 配置键类型转换器，当前仅支持 fastjson
      valueEncode: java # 配置值转码类型，此配置仅在 remote 类型时配置，可选 java 和 kryo
      valueDecode: java
      poolConfig:
        maxTotal: 50
```

<font color=purple>**值得注意的是：因为示例中方法的返回结果是对象，由于 redis 缓存中不支持保存对象，因此需要对 redis 设置当 `Object` 类型数据进入到 redis 中时如何进行类型转换。需要配置 `keyConvertor` 指定 key 的类型转换方式，同时标注 value 的转换类型方式，值进入 redis 时是 java 类型，标注 `valueEncode` 为 java，值从 redis 中读取时转换成java，标注 `valueDecode` 为 java。**</font>

- 注意如果需要缓存的方法返回是对象类型时，为了保证 `Object` 类型的值能存到 redis 中，`Object` 类型的数据必须实现序列化接口。

```java
public class Book implements Serializable {
    // ...省略
}
```

- 启用缓存的同时，使用 `@EnableMethodCache` 注解开启方法缓存功能，配置 `basePackages` 属性指定哪些包开启方法缓存。<font color=red>**注意：`@EnableCreateCacheAnnotation` 与 `@EnableMethodCache` 需要同时使用才能开启方法缓存功能，因为方法缓存也是基于注解的方式来实现**</font>

```java
@SpringBootApplication
@EnableCreateCacheAnnotation // jetcache 启用缓存支持
@EnableMethodCache(basePackages = "com.moon.springboot.caching") // 开启方法注解缓存支持，basePackages 指定扫描方法缓存注解的包路径
public class JetcacheApplication {
    // ...省略
}
```

- 在需要使用缓存的方法上，标识 `@Cached` 注解

```java
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    /*
     * @Cached 注解标识的方法代表该方法使用缓存功能
     *      name 属性：缓存键的前缀名称
     *      key 属性：缓存键的取值
     *      expire 属性：缓存键的过期时间，单位默认是秒
     *      cacheType 属性：缓存的类型，是远程、本地或者两者均使用
     */
    @Cached(name = "book_", key = "#id", expire = 300, cacheType = CacheType.REMOTE)
    @Override
    public Book getById(Integer id) {
        return bookMapper.selectById(id);
    }
}
```

#### 6.3.7. 远程方案的数据同步

由于远程方案中 redis 保存的数据可以被多个客户端共享，这就存在了数据同步问题。jetcache 提供了3个注解解决此问题，分别在更新、删除操作时同步缓存数据，和读取缓存时定时刷新数据

- 更新缓存

```java
/*
 * @CacheUpdate 注解用于更新缓存
 *      name 属性：缓存键的前缀名称
 *      key 属性：缓存键的取值
 *      value 属性：更新的值
 */
@CacheUpdate(name = "book_", key = "#book.id", value = "#book")
@Override
public boolean update(Book book) {
    return bookMapper.updateById(book) > 0;
}
```

- 删除缓存

```java
/*
 * @CacheInvalidate 注解用于删除缓存
 *      name 属性：缓存键的前缀名称
 *      key 属性：缓存键的取值
 */
@CacheInvalidate(name = "book_", key = "#id")
@Override
public boolean delete(Integer id) {
    return bookMapper.deleteById(id) > 0;
}
```

- 定时刷新缓存

```java
@Cached(name = "book_", key = "#id", expire = 60, cacheType = CacheType.REMOTE)
@CacheRefresh(refresh = 5) // 定时刷新缓存，refresh 属性指定刷新的间隔时间，单位默认是秒
@Override
public Book getById(Integer id) {
    return bookMapper.selectById(id);
}
```

#### 6.3.8. 数据报表

jetcache 还提供有简单的数据报表功能，帮助开发者快速查看缓存命中信息，只需要在配置 `jetcache.statIntervalMinutes` 选项即可，单位是分钟

```java
jetcache:
  statIntervalMinutes: 1 # 统计间隔，0 表示不统计
  ...省略
```

上述设置后，每1分钟在控制台输出缓存数据命中信息

```bash
[DefaultExecutor] c.alicp.jetcache.support.StatInfoLogger  : jetcache stat from 2022-05-01 09:32:15,892 to 2022-05-01 09:33:00,003
cache    |    qps|   rate|   get|    hit|   fail|   expire|   avgLoadTime|   maxLoadTime
---------+-------+-------+------+-------+-------+---------+--------------+--------------
book_    |   0.66| 75.86%|    29|     22|      0|        0|          28.0|           188
---------+-------+-------+------+-------+-------+---------+--------------+--------------
```

## 7. Spring Boot 整合 j2cache 缓存

jetcache 可以在限定范围内构建多级缓存，但是灵活性不足，不能随意搭配缓存方案。而 j2cache 则是一种可以随意搭配缓存解决方案的缓存整合框架。即可将 Ehcache、Caffeine、redis、Spring Cache 等进行整合。

### 7.1. j2cache 概述

J2Cache 是 OSChina 目前正在使用的开源的两级缓存框架（要求至少 Java 8）。J2Cache 的两级缓存结构如下：

- 第一级缓存 L1：进程内缓存（同时支持 Ehcache 2.x、Ehcache 3.x 和 Caffeine）
- 第二级缓存 L2：集中式缓存（支持 Redis(推荐)/Memcached）

数据读取顺序是 `L1 -> L2 -> DB`

由于大量的缓存读取会导致 L2 的网络成为整个系统的瓶颈，因此 L1 的目标是降低对 L2 的读取次数。该缓存框架主要用于集群环境中。单机也可使用，用于避免应用重启导致的缓存冷启动后导致的 ehcache 缓存数据丢失，对后端业务的造成冲击。

J2Cache 从 1.3.0 版本开始支持 JGroups 和 Redis Pub/Sub 两种方式进行缓存事件的通知。在某些云平台上可能无法使用 JGroups 组播方式，可以采用 Redis 发布订阅的方式。

### 7.2. 整合示例

下面的示例是 j2cache 整合 Ehcache 与 redis 为例：

#### 7.2.1. 基础示例工程准备

> 直接使用上面内置缓存的示例工程代码（只保留手机验证码功能，移除相关依赖）

#### 7.2.2. 引入相关依赖

在项目的 pom.xml 文件中引入 j2cache、redis、ehcache 等坐标

```xml
<!-- j2cache 核心依赖 -->
<dependency>
    <groupId>net.oschina.j2cache</groupId>
    <artifactId>j2cache-core</artifactId>
    <version>2.8.4-release</version>
</dependency>
<!-- j2cache 与 Spring Boot 整合依赖 -->
<dependency>
    <groupId>net.oschina.j2cache</groupId>
    <artifactId>j2cache-spring-boot2-starter</artifactId>
    <version>2.8.0-release</version>
</dependency>
<!-- ehcache 缓存技术依赖 -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

> Tips: j2cache 的 starter 中默认包含了 redis 坐标，官方也推荐使用 redis 作为二级缓存，因此无需导入 redis 坐标

#### 7.2.3. j2cache 配置

- 因为 j2cache 有其独立的配置文件格式，所以在项目 application.yml 文件中，只需要配置 j2cache 独立配置文件的位置即可

```yml
j2cache: # j2cache 配置
  config-location: j2cache.properties # j2cache 配置文件
```

- 编写 j2cache 独立的配置文件，文件名称是 j2cache.properties（*参考 j2cache-core-x.x.x-release.jar 包里的配置示例文件*）。主要配置一级与二级缓存，和一二级缓存间数据传递方式

```properties
# 配置1级缓存
j2cache.L1.provider_class = ehcache
ehcache.configXml = ehcache.xml

# 配置是否打开2级缓存，true 表示启用，false 表示不启用
j2cache.l2-cache-open = false

# 配置2级缓存
j2cache.L2.provider_class = net.oschina.j2cache.cache.support.redis.SpringRedisProvider
j2cache.L2.config_section = redis
redis.hosts = localhost:6379

# 配置1级缓存中的数据如何加载到二级缓存中
j2cache.broadcast = net.oschina.j2cache.cache.support.redis.SpringRedisPubSubPolicy

# 配置 redis 模式（默认值为 single），不配置启动时控制会有警告
redis.mode = single
# 配置 redis
redis.namespace = j2cache
```

> 上述配置需要参照官方给出的配置说明进行配置。例如1级供应商选择 ehcache，供应商名称仅仅是一个 ehcache，但是2级供应商选择 redis 时要写专用的 Spring 整合 Redis 的供应商全类名 `net.oschina.j2cache.cache.support.redis.SpringRedisProvider`，而且这个名称并不是所有的 redis 包中能提供的，也不是 spring 包中提供的。因此配置 j2cache 必须参照官方文档配置，而且还要去找专用的整合包，导入对应坐标才可以使用。
>
> 同理一级与二级缓存最重要的一个配置就是两者之间的数据交互方式，此类配置也不是随意配置的，并且不同的缓存解决方案提供的数据沟通方式差异化很大，需要查询官方文档进行设置。

- 另外，如果使用 ehcache 还需要单独添加其配置文件，配置内容参考上面整合 ehcache 章节

#### 7.2.4. 缓存功能实现

j2cache 的使用和 jetcache 比较类似，但是无需开启，直接定义缓存对象即可使用，缓存对象名 `CacheChannel`。在业务类中编写缓存的处理代码：

```java
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    /* 注入 j2cache 缓存操作对象 CacheChannel */
    @Autowired
    private CacheChannel cacheChannel;

    /** 获取短信验证码 */
    @Override
    public String sendSMSCode(String tel) {
        // 生成验证码
        String code = generateGode(tel);
        /*
         * CacheChannel 类的 set(String region, String key, Object value) 方法，将数据存入缓存
         *      region 参数：缓存键的前缀
         *      key 参数：缓存的键
         *      value 参数：缓存的值
         */
        cacheChannel.set("sms", tel, code);
        // 返回验证码
        return code;
    }

    /** 校验短信验证码 */
    @Override
    public boolean checkCode(SMSCode smsCode) {
        String code = smsCode.getCode();
        /*
         * CacheChannel 类的 get(String region, String key, boolean...cacheNullObject) 方法，从缓存中获取数据
         * 用户无需判断返回的对象是否为空
         *      region 参数：缓存键的前缀
         *      key 参数：缓存的键
         */
        String codeCache = cacheChannel.get("sms", smsCode.getTel()).asString();
        // 取出缓存中的验证码与接收的验证码比对，如果相同，返回true
        return code.equals(codeCache);
    }
    // ...省略
}
```

### 7.3. j2cache 配置示例

因为 j2cache 是一个整合型的缓存框架，配置是 j2cache 的核心。缓存相关的配置很多，可以查阅 j2cache-core 核心包中的 j2cache.properties 示例文件中的说明。如下：

```properties
#J2Cache configuration
#########################################
# Cache Broadcast Method
# values:
# jgroups -> use jgroups's multicast
# redis -> use redis publish/subscribe mechanism (using jedis)
# lettuce -> use redis publish/subscribe mechanism (using lettuce, Recommend)
# rabbitmq -> use RabbitMQ publisher/consumer mechanism
# rocketmq -> use RocketMQ publisher/consumer mechanism
# none -> don't notify the other nodes in cluster
# xx.xxxx.xxxx.Xxxxx your own cache broadcast policy classname that implement net.oschina.j2cache.cluster.ClusterPolicy
#########################################

j2cache.broadcast = redis

# jgroups properties
jgroups.channel.name = j2cache
jgroups.configXml = /network.xml

# RabbitMQ properties
rabbitmq.exchange = j2cache
rabbitmq.host = localhost
rabbitmq.port = 5672
rabbitmq.username = guest
rabbitmq.password = guest

# RocketMQ properties
rocketmq.name = j2cache
rocketmq.topic = j2cache
# use ; to split multi hosts
rocketmq.hosts = 127.0.0.1:9876

#########################################
# Level 1&2 provider
# values:
# none -> disable this level cache
# ehcache -> use ehcache2 as level 1 cache
# ehcache3 -> use ehcache3 as level 1 cache
# caffeine -> use caffeine as level 1 cache(only in memory)
# redis -> use redis as level 2 cache (using jedis)
# lettuce -> use redis as level 2 cache (using lettuce)
# readonly-redis -> use redis as level 2 cache ,but never write data to it. if use this provider, you must uncomment `j2cache.L2.config_section` to make the redis configurations available.
# memcached -> use memcached as level 2 cache (xmemcached),
# [classname] -> use custom provider
#########################################

j2cache.L1.provider_class = caffeine
j2cache.L2.provider_class = redis

# When L2 provider isn't `redis`, using `L2.config_section = redis` to read redis configurations
# j2cache.L2.config_section = redis

# Enable/Disable ttl in redis cache data (if disabled, the object in redis will never expire, default:true)
# NOTICE: redis hash mode (redis.storage = hash) do not support this feature)
j2cache.sync_ttl_to_redis = true

# Whether to cache null objects by default (default false)
j2cache.default_cache_null_object = true

#########################################
# Cache Serialization Provider
# values:
# fst -> using fast-serialization (recommend)
# kryo -> using kryo serialization
# json -> using fst's json serialization (testing)
# fastjson -> using fastjson serialization (embed non-static class not support)
# java -> java standard
# fse -> using fse serialization
# [classname implements Serializer]
#########################################

j2cache.serialization = json
#json.map.person = net.oschina.j2cache.demo.Person

#########################################
# Ehcache configuration
#########################################

# ehcache.configXml = /ehcache.xml

# ehcache3.configXml = /ehcache3.xml
# ehcache3.defaultHeapSize = 1000

#########################################
# Caffeine configuration
# caffeine.region.[name] = size, xxxx[s|m|h|d]
#
#########################################
caffeine.properties = /caffeine.properties

#########################################
# Redis connection configuration
#########################################

#########################################
# Redis Cluster Mode
#
# single -> single redis server
# sentinel -> master-slaves servers
# cluster -> cluster servers (\u6570\u636e\u5e93\u914d\u7f6e\u65e0\u6548\uff0c\u4f7f\u7528 database = 0\uff09
# sharded -> sharded servers  (\u5bc6\u7801\u3001\u6570\u636e\u5e93\u5fc5\u987b\u5728 hosts \u4e2d\u6307\u5b9a\uff0c\u4e14\u8fde\u63a5\u6c60\u914d\u7f6e\u65e0\u6548 ; redis://user:password@127.0.0.1:6379/0\uff09
#
#########################################

redis.mode = single

#redis storage mode (generic|hash)
redis.storage = generic

## redis pub/sub channel name
redis.channel = j2cache
## redis pub/sub server (using redis.hosts when empty)
redis.channel.host =

#cluster name just for sharded
redis.cluster_name = j2cache

## redis cache namespace optional, default[empty]
redis.namespace =

## redis command scan parameter count, default[1000]
#redis.scanCount = 1000

## connection
# Separate multiple redis nodes with commas, such as 192.168.0.10:6379,192.168.0.11:6379,192.168.0.12:6379

redis.hosts = 127.0.0.1:6379
redis.timeout = 2000
redis.password =
redis.database = 0
redis.ssl = false

## redis pool properties
redis.maxTotal = 100
redis.maxIdle = 10
redis.maxWaitMillis = 5000
redis.minEvictableIdleTimeMillis = 60000
redis.minIdle = 1
redis.numTestsPerEvictionRun = 10
redis.lifo = false
redis.softMinEvictableIdleTimeMillis = 10
redis.testOnBorrow = true
redis.testOnReturn = false
redis.testWhileIdle = true
redis.timeBetweenEvictionRunsMillis = 300000
redis.blockWhenExhausted = false
redis.jmxEnabled = false

#########################################
# Lettuce scheme
#
# redis -> single redis server
# rediss -> single redis server with ssl
# redis-sentinel -> redis sentinel
# redis-cluster -> cluster servers
#
#########################################

#########################################
# Lettuce Mode
#
# single -> single redis server
# sentinel -> master-slaves servers
# cluster -> cluster servers (\u6570\u636e\u5e93\u914d\u7f6e\u65e0\u6548\uff0c\u4f7f\u7528 database = 0\uff09
# sharded -> sharded servers  (\u5bc6\u7801\u3001\u6570\u636e\u5e93\u5fc5\u987b\u5728 hosts \u4e2d\u6307\u5b9a\uff0c\u4e14\u8fde\u63a5\u6c60\u914d\u7f6e\u65e0\u6548 ; redis://user:password@127.0.0.1:6379/0\uff09
#
#########################################

## redis command scan parameter count, default[1000]
#lettuce.scanCount = 1000
lettuce.mode = single
lettuce.namespace =
lettuce.storage = hash
lettuce.channel = j2cache
lettuce.scheme = redis
lettuce.hosts = 127.0.0.1:6379
lettuce.password =
lettuce.database = 0
lettuce.sentinelMasterId =
lettuce.maxTotal = 100
lettuce.maxIdle = 10
lettuce.minIdle = 10
# timeout in milliseconds
lettuce.timeout = 10000
# redis cluster topology refresh interval in milliseconds
lettuce.clusterTopologyRefresh = 3000

#########################################
# memcached server configurations
# refer to https://gitee.com/mirrors/XMemcached
#########################################

memcached.servers = 127.0.0.1:11211
memcached.username =
memcached.password =
memcached.connectionPoolSize = 10
memcached.connectTimeout = 1000
memcached.failureMode = false
memcached.healSessionInterval = 1000
memcached.maxQueuedNoReplyOperations = 100
memcached.opTimeout = 100
memcached.sanitizeKeys = false
```
