# Spring Boot笔记

## 1. Spring Boot 简介

Spring 诞生时是 Java 企业版（Java Enterprise Edition，JEE，也称 J2EE）的轻量级代替品。无需开发重量级的 Enterprise JavaBean（EJB），Spring 为企业级Java 开发提供了一种相对简单的方法，通过依赖注入和面向切面编程，用简单的Java 对象（Plain Old Java Object，POJO）实现了 EJB 的功能。

虽然 Spring 的组件代码是轻量级的，但它的配置却是重量级的。

- 第一阶段：xml配置

在Spring 1.x时代，使用Spring开发满眼都是xml配置的Bean，随着项目的扩大，我们需要把xml配置文件放到不同的配置文件里，那时需要频繁的在开发的类和配置文件之间进行切换

- 第二阶段：注解配置

在Spring 2.x 时代，随着JDK1.5带来的注解支持，Spring提供了声明Bean的注解（例如@Controller、@Service），大大减少了配置量。主要使用的方式是应用的基本配置（如数据库配置）用xml，业务配置用注解

- 第三阶段：java配置

Spring 3.0 引入了基于 Java 的配置能力，这是一种类型安全的可重构配置方式，可以代替 XML。我们目前刚好处于这个时代。现在Spring和Springboot都推荐使用java配置。

所有这些配置都代表了开发时的损耗。 因为在思考 Spring 特性配置和解决业务问题之间需要进行思维切换，所以写配置挤占了写应用程序逻辑的时间。除此之外，项目的依赖管理也是件吃力不讨好的事情。决定项目里要用哪些库就已经够让人头痛的了，你还要知道这些库的哪个版本和其他库不会有冲突，这难题实在太棘手。并且，依赖管理也是一种损耗，添加依赖不是写应用程序代码。一旦选错了依赖的版本，随之而来的不兼容问题毫无疑问会是生产力杀手。

Spring Boot 让这一切成为了过去。

Spring Boot其设计目的是用来简化Spring应用的初始搭建以及开发采用约定优于配置，只需要“run”就能创建一个独立的、生产级别的Spring应用。Spring Boot为Spring平台及第三方库提供开箱即用的设置（提供默认设置），这样我们就可以简单的开始。多数Spring Boot应用只需要很少的Spring配置。

我们可以使用SpringBoot创建java应用，并使用java –jar 启动它，或者采用传统的war部署方式。

### 1.1. 核心功能

- 核心能力：Spring容器、日志、<font color=red>**自动配置AutoCongfiguration、Starters**</font>
- web应用的能力：MVC、嵌入式容器
- 数据访问(持久化)：关系型数据库、非关系型数据库
- 强大的整合其他技术的能力
- 测试：强悍的应用测试

### 1.2. 开发环境要求（2.1.7.RELEASES）

- Spring Boot的2.1.7.RELEASES正式发行版，必须要使用Java8或 Java 11，Spring版本也必须是5.1.8及以上
- 构建工具版本：Maven ，版本要求是3.3及以上
- SpringBoot 支持如下的嵌入式Servlet容器，Spring Boot应用程序最低支持到Servlet 3.1的容器。

## 2. Spring Boot 入门

### 2.1. 环境准备

- DataBase：MySQL
- IDE：Eclipse/idea
- SpringBoot：
  - 1.5.6 (官方网站：http://spring.io/) 2017年7月27日
  - 2.0.3版本 2018年6月16日
- SpringBoot参考文档：
  - https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/
- Maven：3.3.9 （官方声明Springboot 1.5.6版本需要Maven 3.2+）
- Gradle：4.0.2 (官方网站https://gradle.org/)

- 本地仓库：需要使用资料中的仓库 【仓库(SpringBoot).zip】
- 引用资料中的仓库，重新构建索引：

![重建索引1](images/_重建索引1_1536458505_22077.png)

![重建索引2](images/_重建索引2_1536458518_15991.png)

### 2.2. 起步依赖-eclipse

#### 2.2.1. 创建Maven工程

![创建Maven工程](images/_创建maven工程_1536458626_13081.png)

#### 2.2.2. 添加spring boot依赖方式1 - 使用`<parent>`标签

![添加依赖](images/_添加依赖_1536458647_27503.png)

在pom.xml中添加依赖，效果如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <!-- 配置父级工程 -->
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
  </parent>
  <groupId>com.moon</groupId>
  <artifactId>springboot01-test</artifactId>
  <version>0.0.1-SNAPSHOT</version>

  <!-- 配置依赖关系 -->
  <dependencies>
    <!-- 配置WEB启动器 SpringMVC、Restful、jackson -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
</project>
```

工程自动添加了好多jar包，而这些jar包正是开发时需要导入的jar包。

![依赖jar包](images/_依赖jar包_1536458793_25902.png)

这些jar包被刚才加入的spring-boot-starter-web所引用了，所以添加spring-boot-starter-web后会自动把依赖传递过来。

#### 2.2.3. 添加spring boot依赖方式2 - 定义范围`<scope>`为import

在Spring boot 项目的POM文件中，可以通过在POM文件中继承 Spring-boot-starter-parent 来引用 Srping boot 默认依赖的jar包。但使用parent这种继承的方式，只能继承一个 spring-boot-start-parent。实际开发中，很可能需要继承自己公司的标准parent配置，此时可以使用 `<scope>import</scope>` 来实现多继承。如下例：

```xml
<dependencyManagement>
    <dependencies>
        <!-- Override Spring Data release train provided by Spring Boot -->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-releasetrain</artifactId>
            <version>Fowler-SR2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.2.4.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2.3. 起步依赖-idea

#### 2.3.1. 创建项目（Maven方式）

#### 2.3.2. 创建项目（Spring Intialzr方式）

idea可以直接新建Spring boot项目。（注意，此创建方式需要联网）

1. 新建Spring Intialzr项目

![新建Spring Intialzr项目](images/_新建springin_1536459902_24858.jpg)

2. 填写项目信息，选择Packaging为Jar。

![idea创建spring boot2](images/_idea创建spri_1536460757_13180.jpg)

3. 选择项目使用的技术

![idea创建spring boot3](images/_idea创建spri_1536460771_30945.jpg)

4. 填写项目名称
5. 将项目设置为Maven项目
6. 项目结构与依赖与手动新建maven项目一致

![idea创建spring boot4](images/_idea创建spri_1536460785_14977.jpg)

### 2.4. 变更JDK版本(非必需)

- 默认情况下工程的JDK版本是1.6，但是通常使用的是1.7的版本

![jdk版本1](images/_jdk版本1_1536461108_16861.jpg)

- 修改JDK为1.7，需要在pom.xml中添加以下配置

```xml
<!-- 定义全局属性 -->
<properties>
  <!-- 定义更改JDK版本属性 -->
  <java.version>1.7</java.version>
</properties>
```

- 使用Maven更新工程后，就发现版本已经变成1.7

![jdk版本2](images/_jdk版本2_1536461119_32443.jpg)

- 注意：
  - 虽然JDK1.6或者1.7都可以使用SpringBoot，但SpringBoot官方建议使用JDK1.8，要使用JDK1.8，首先必须要配置JDK1.8后，才可以使用上述方法设置。
  - **如果是传统的maven项目，是需要配置jdk插件，但spring boot项目中，只需要配置`<properties>`属性即可**

### 2.5. 启动类

创建Application启动类：

```java
@SpringBootApplication // 代表为SpringBoot应用的运行主类
public class Application {
  public static void main(String[] args) {
    /** 运行SpringBoot应用 */
    SpringApplication.run(Application.class, args);
  }
}
```

- @SpringBootApplication：代表为SpringBoot应用的运行主类

```java
@SpringBootApplication( // SpringBoot应用启动类
    scanBasePackages={"com.moon.springboot"}) // 指定扫描的基础包
```

<font color="purple">注：如果配置@SpringBootApplication注解，不指定注解扫描的包，默认约定是扫描当前引导类所在的同级包下的所有包和所有类以及下级包的类（若为JPA项目还可以扫描标注@Entity的实体类），建议入口类放置的位置在groupId+arctifactID组合的包名下；如果需要指定扫描包使用注解`@SpringBootApplication(scanBasePackages = 'xxx.xxx.xx')`</font>

上面的`@SpringBootApplication`相当于下面的3个注解

- `@Configuration`：用于定义一个配置类
- `@EnableAutoConfiguration`：Spring Boot会自动根据你jar包的依赖来自动配置项目
- `@ComponentScan`：告诉Spring 哪个packages 的用注解标识的类会被spring自动扫描并且装入bean 容器。

Banner直接启动，控制台会出现Spring启动标识。

```java
@SpringBootApplication // 代表为SpringBoot应用的运行主类
public class Application {
  public static void main(String[] args) {
    /** 创建SpringApplication应用对象 */
    SpringApplication springApplication = new SpringApplication(Application.class);
    /** 设置横幅模式(设置关闭) */
    springApplication.setBannerMode(Banner.Mode.OFF);
    /** 运行 */
    springApplication.run(args);
  }
}
```

参考附录二的banner设置，可以通过修改配置文件制定自己的标识。

### 2.6. 入门程序

需求：使用Spring MVC实现Hello World输出

#### 2.6.1. 原来的实现

现在开始使用spring MVC 框架，实现json 数据的输出。如果按照我们原来的做法，需要在web.xml 中添加一个DispatcherServlet 的配置，还需要添加一个spring的配置文件，配置文件如下配置

spring 加入配置

```xml
<!-- controller注解扫描 -->
<context:component-scan base-package="com.moon.springboot.controller" />
<!-- 注解驱动 -->
<mvc:annotation-driven />
```

web.xml加入配置

```xml
<!-- 配置前端控制器 -->
<servlet>
  <servlet-name> springboot01-test</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring/*.xml</param-value>
  </init-param>
</servlet>
<servlet-mapping>
  <servlet-name> springboot01-test</servlet-name>
  <url-pattern>/</url-pattern>
</servlet-mapping>
```

还要编写Controller。。。

#### 2.6.2. SpringBoot的实现

我们不需要配置文件，直接编写Controller类即可

```java
@RestController
public class HelloController {
  // http://localhost:8080/hello
  @GetMapping("/hello")
  public String hello(){
    return "Hello World";
  }
}
```

- @RestController注解:其实就是@Controller和@ResponseBody注解加在一起
- 启动方式一：启动编写的引导类即可
- 启动方式二：使用Maven命令spring-boot:run执行即可
  - 选择 Maven Build

![Spring Boot启动方式二1](images/_springboot_1536463255_21573.jpg)

  - 在浏览器地址栏输入http://localhost:8080/hello 即可看到运行结果

![Spring Boot启动方式二2](images/_springboot_1536463286_8169.jpg)

### 2.7. Spring Boot依赖引入实现原理

在项目的pom.xml文件中继承父工程`spring-boot-starter-parent`

```xml
<!-- Inherit defaults from Spring Boot -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.13.RELEASE</version>
</parent>
```

通过maven的依赖传递从而实现继承spring boot的父依赖后，可以依赖spring boot项目相关的jar

![](images/20200308174207439_392.png)

![](images/20200308174144668_872.png)

## 3. Spring Boot 配置文件

### 3.1. Spring boot 支持配置文件的类型

- 默认情况下，Spring Boot会加载resources目录下的application.properties或application.yml来获得配置的参数。
- SpringBoot支持一种由SpringBoot框架自制的配置文件格式。后缀为yml。yml后缀的配置文件的功能和properties后缀的配置文件的功能是一致的。
- Spring Boot能支持两种配置文件：
  - application.propertie：键值对风格配置文件
  - application.yml：层级树键值对风格配置文件

### 3.2. application.properties

#### 3.2.1. 单配置文件

```properties
# 配置数据源
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.type=com.mchange.v2.c3p0.ComboPooledDataSource
```

#### 3.2.2. 多配置文件

- 第一步：在application.properties配置文件下，增加多个application-xxx.properties文件名的配置文件，其中xxx是一个任意的字符串。

```properties
application-database.properties
application-jpa.properties
application-freemarker.properties
```

- 第二步：在application.properties总配置文件指定，加载的多个配置文件。**需要在application.properties中指定其它配置文件：**

```properties
spring.profiles.active=database,jpa,freemarker
```

### 3.3. application.yml

#### 3.3.1. yml配置文件简介

YML文件格式是YAML (YAML Aint Markup Language)层级树键值对格式文件。YAML是一种直观的能够被电脑识别的的数据数据序列化格式，并且容易被人类阅读，容易和脚本语言交互的，可以被支持YAML库的不同的编程语言程序导入，比如： C/C++, Ruby, Python, Java, Perl, C#, PHP等。YML文件是以数据为核心的，比传统的xml方式更加简洁。

YML文件的扩展名可以使用.yml或者.yaml。

#### 3.3.2. yml配置文件的语法

##### 3.3.2.1. 配置普通数据

语法： `key: value`。注意：value之前有一个空格

```yml
name: haohao
```

##### 3.3.2.2. 配置对象数据

- 语法：

```yml
# 方式一：
key:
    key1: value1
    key2: value2
# 方式二：
key: {key1: value1,key2: value2}
```

- 示例代码：

```yml
# 方式一：
person:
    name: haohao
    age: 31
    addr: beijing
# 方式二：
person: {name: haohao,age: 31,addr: beijing}
```

- <font color=red>**注意：key1前面的空格个数不限定，在yml语法中，相同缩进代表同一个级别**</font>

##### 3.3.2.3. 配置Map数据

**同上面的对象写法**

##### 3.3.2.4. 配置数组（List、Set）数据

- 语法：

```yml
# 方式一：
key:
    - value1
    - value2
# 方式二：
key: [value1,value2]
```

- 示例代码：

```yml
city:
    - beijing
    - tianjin
    - shanghai
    - chongqing
# 或者
city: [beijing,tianjin,shanghai,chongqing]
# 集合中的元素是对象形式
student:
    - name: zhangsan
      age: 18
      score: 100
    - name: lisi
      age: 28
      score: 88
    - name: wangwu
      age: 38
      score: 90
```

- **注意：value1与之间的“-”之间存在一个空格**

#### 3.3.3. 单配置文件

```yml
# 配置数据源
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot_db
    driverClassName: com.mysql.jdbc.Driver
    username: root
    password: root
    type: com.mchange.v2.c3p0.ComboPooledDataSource
```

其实application.yml配置文件就是将原来application.properties使用（.）分割的方式，改为树状结构，使用（:）分割。

**注意：最后key的字段与值之间的冒号（:）后面一定要有一个空格。**

#### 3.3.4. 多配置文件

- 第一步：在application.yml配置文件下，增加多个application-xxx.yml文件名的配置文件，其中xxx是一个任意的字符串。

```
application-database.yml
application-jpa.yml
application-freemarker.yml
```

- 第二步：在application.yml总配置文件指定，加载的多个配置文件。**需要在application.yml中指定其它配置文件：**

```yml
spring:
  profiles:
    active: database,jpa,freemarker
```

### 3.4. 使用xml配置

Spring Boot推荐无xml配置，但实际项目中，可能有一些特殊要求必须使用xml配置，在引导类中通过Spring提供的`@ImportResource`来加载xml配置

```java
@SpringBootApplication(scanBasePackages = {"com.moon.controller"})
@ImportResource({"classpath:xxx1.xml", "classpath:xxx2.xml"})
public class SpringbootdemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootdemoApplication.class, args);
  }
}
```

### 3.5. 读取配置文件
#### 3.5.1. 读取核心配置文件方式一：Environment 对象

- 在工程的src/main/resources 下修改核心配置文件
- application.properties, 添加内容如下

```properties
name=月之哀伤
url=http://www.moon.com
```

- 在Controller中添加：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
  @Autowired
  private Environment environment;
  // http://localhost:8080/hello
  @GetMapping("/hello")
  public String hello(){
    System.out.println(environment.getProperty("name"));
    System.out.println(environment.getProperty("url"));
    return "Hello World";
  }
}
```

就可以直接把配置文件信息打印出来。注意包名是：`org.springframework.core.env.Environment`

#### 3.5.2. 读取核心配置文件方式二：@value 注解

还是上面的例子，我们只需要是一个`@value`注解即可获取配置文件内容

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    @Autowired
    private Environment environment;
    @Value("${name}")
    private String name;
    @Value("${url}")
    private String url;
    // http://localhost:8080/hello
    @GetMapping("/hello")
    public String hello(){
        System.out.println(environment.getProperty("name"));
        System.out.println(environment.getProperty("url"));
        System.out.println(name);
        System.out.println(url);
        return "Hello World";
    }
}
```

> *注：使用`@Value`注解，只能映射配置文件的字符串类型的值，不能映射对象与数组*

#### 3.5.3. 读取自定义配置文件方式三：@ConfigurationProperties 注解

- 通过注解`@ConfigurationProperties(prefix="配置文件中的key的前缀")`可以将配置文件中的配置自动与实体进行映射
- 在application.properties或者application.yml配置以下

```properties
person.name=zhangsan
person.age=18
```

```yml
person:
  name: zhangsan
  age: 18
```

- 在类（实体Bean类）中引用

```java
@Controller
@ConfigurationProperties(prefix = "person")
public class QuickStartController {
    private String name;
    private Integer age;
    @RequestMapping("/quick")
    @ResponseBody
    public String quick(){
        return "springboot 访问成功! name="+ name +",age=" + age;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
```

> **注意：使用`@ConfigurationProperties`方式可以进行配置文件与实体字段的自动映射，但需要字段必须提供set方法才可以，而使用`@Value`注解修饰的字段不需要提供set方法**

#### 3.5.4. 控制方法定义在引导类中

引导类可以定义引导入口与视图跳转的控制方法

```java
@SpringBootApplication
@RestController
public class SpringbootdemoApplication {
   // 读取配置文件
   @Value("${book.author}")
   private String bookAuthor;
   @Value("${book.name}")
   private String bookName;
   // 主页请求控制
   @GetMapping("/")
   public String index() {
      return "book name is " + bookName + " and book author is: " + bookAuthor;
   }
   public static void main(String[] args) {
      SpringApplication.run(SpringbootdemoApplication.class, args);
   }
}
```

#### 3.5.5. 将配置文件的属性赋给实体类（基于properties与yml文件）

1. **方式1：读取默认配置文件**
    - 在自定义配置类中，加上注解`@ConfigurationProperties`，表明该类为配置类，并加上属性prefix，配置前缀
    - 加上`@Component`注解，Spring boot在启动时通过包扫描将该类作为一个Bean注入ioc容器
    - 在引导类中（或者本身自定义配置类），加上`@EnableConfigurationProperties`注解，并指明需要引用的JavaBean类
2. **方式2：读取自定义的配置文件**
    - 需要在自定义配置类上加`@Configuration、@PropertySource、@ConfigurationProperties`的3个注解。*需要注意，如果在Spring Boot版本为1.4或1.4之前，则需要@PropertySource注解上加location属性，并指明该配置文件的路径*
    - 在引导类中（或者本身自定义配置类），加上`@EnableConfigurationProperties`注解，并指明需要引用的JavaBean类

##### 3.5.5.1. 方式1：读取默认配置文件(yml与properties格式均可用)

- 上面使用`@Value`注入每个配置在实际项目中麻烦。通过注解`@ConfigurationProperties(prefix="配置文件中的key的前缀")`可以将配置文件中的配置自动与实体进行映射
- Spring Boot将此方式称为：基于类型安全的配置方式，通过@ConfigurationProperties将properties属性和一个Bean及其属性关联，从而实现类型安全的配置

- 在pom.xml文件引入configuration-processor的依赖

```xml
<!-- @ConfigurationProperties执行器的配置 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

- 创建类型安全的配置类

```java
package com.moon.springbootdemo.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "book")
// 注：如果使用@Configuration注解，则同时需要加上@EnableConfigurationProperties指定加载的配置类
// @Configuration
// @EnableConfigurationProperties(BookSettings.class)
public class BookSettings {
    private String name;
    private String author;
    // ..省略getter、setter
}
```

- 在引导类注入安全配置类，读取配置文件内容

```java
@SpringBootApplication
@RestController
public class SpringbootdemoApplication {
    // 注入安全配置类
    @Autowired
    private BookSettings bookSettings;
    // 主页请求控制
    @GetMapping("/")
    public String index() {
        return "book name is " + bookSettings.getName() + " and book author is: " + bookSettings.getAuthor();
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringbootdemoApplication.class, args);
    }
}
```

##### 3.5.5.2. 方式2：读取自定义的配置文件(只能读取properties格式，该注解并不支持加载yml！)

- 上面方式1是写在默认配置文件application.properties中，如果属性太多，实际项目中不合适，需要自定义配置文件
- **注：方式1的读取默认配置文件的方式，此方式也可以实现。省略@PropertySource注解即可**

- 在pom.xml文件中引入configuration-processor依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

- 创建配置文件config/book.properties

```properties
book.author=moonzero
book.name=moon in kira
```

- 读取自定义配置文件的实体类

```java
@Configuration
@PropertySource("classpath:config/book.properties")
// prefix用来选择属性的前缀，也就是在book.properties文件中的“book”以下的属性
// ignoreUnknownFields是用来告诉SpringBoot在有属性不能匹配到声明的域时抛出异常
@ConfigurationProperties(prefix = "book", ignoreUnknownFields = false)
// @EnableConfigurationProperties(BookSettings.class)    // 在此配置上加上@EnableConfigurationProperties后，在启动类或者控制层类都不再需要加上此注解
public class BookSettings {
    private String name;
    private String author;
    // ..省略getter、setter
}
```

- 引导类与视图控制

```java
@RestController
@SpringBootApplication
@EnableConfigurationProperties({BookSettings.class})
public class SpringbootdemoApplication {
    // 与上面方式的写法一致
}
```

### 3.6. 自定义配置类

使用`@Configuration`和`@Bean`方式

```java
@Configuration
public class MyConfig {
    // 将方法的返回值添加到容器中；容器中这个组件默认的id就是方法名
    @Bean
    public MySQLInfo mySQLInfo(){
        System.out.println("配置类@Bean给容器中添加组件了...");
        return new MySQLInfo();
    }
}
```

### 3.7. SpringBoot配置信息的查询

- SpringBoot的配置文件，主要的目的就是对配置信息进行修改的，但在配置时的key从哪里去查询呢？我们可以查阅SpringBoot的官方文档
- 文档URL：https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/#common-application-properties

### 3.8. 启动时使用命令行注入参数到配置文件
#### 3.8.1. SpringBoot属性加载顺序

1. 在命令行中传入的参数
2. SPRING_APPLICATION_JSON中的属性。SPRING_APPLICATION_JSON是以JSON的格式配置在系统环境变量中的内容
3. `java:comp/env`中的JNDI属性
4. Java的系统属性，可以通过`System.getProperties()`获得的内容
5. 操作系统的环境变量
6. 通过`random.*`配置的随机属性
7. 位于当前应用jar包之外，针对不同`{profile}`环境的配置文件内容，例如`application-{profile}.properties`或是YAML定义的配置文件
8. 位于当前应用jar包之内，针对不同`{profile}`环境的配置文件内容，例如`application-{profile}.properties`或是YAML定义的配置文件
9. 位于当前应用jar包之外的application.properties和YAML配置内容
10. 位于当前应用jar包之内的application.properties和YAML配置内容
11. 在`@Configuration`注解修改的类中，通过`@PropertySource`注解定义的属性
12. 应用默认属性，使用SpringApplication.setDefaultProperties定义的内容

#### 3.8.2. 项目启动示例

- 准备两套环境的配置文件，application-dev.yml和application-pro.yml
- 设置总的配置文件，application.yml
    - **注：如果使用`${}`占位符，在开发过程中可以根据输入的参数切换，但在打包输入命令是无法替换，后来发现使用`@@`包裹才能实现**

```yml
spring:
  profiles:
    active: ${activeName} # 启动时配置相关的参数，在打包时需要替换成@activeName@
```

- 输入启动的命令，带上与配置文件相应对应的参数名，直接使用jar启动输入以下命令

```shell
# 使用dev开发环境配置
java -jar moon-project.jar --spring.profiles.active=dev
# 使用pro生产环境配置
java -jar moon-project.jar --spring.profiles.active=pro
```

#### 3.8.3. 使用idea启动示例

点击项目下拉按钮后选择"Edit Configurations"，在"Configuration"下的VM options中填入需要的属性值，填写的格式如下：

```shell
# 示例，前面是配置文件没有配置，直接指定参数值；后面是配置文件使用${}指定参数名
-Dserver.port=8888 -Dspring.redis.port=6378 -D"你想配置的参数名"="参数值"
```

#### 3.8.4. 使用junit测试配置启动参数示例

可以通过@SpringBootTest注解的properties属性向Environment中设置新的属性，也可以通过使用EnvironmentTestUtils工具类来向ConfigurableEnvironment中添加新的属性。

```java
@SpringBootTest(properties = {"activeName=dev"})
@RunWith(SpringRunner.class)
public class JavMainTest {
}
```

#### 3.8.5. 个人项目实践示例
##### 3.8.5.1. 打包项目赋值参数命令

- 因为配置开发与正式版本的两套配置文件，所以开发时运行需要修改`Environment`的`VM options`的参数为：`-DactiveName=dev`，切换到开发环境的配置，再运行main方法启动
    - **注意：此方式只适用于`${}`占位符情况，如果使用`@@`，则不能使用**
- 为了兼容项目打包，配置文件是使用`@@`作为占位符，所以启动需要使用命令`spring-boot:run`

```shell
# 以开发环境配置启动
spring-boot:run -DactiveName=dev -Dmaven.test.skip=true

# 以正式环境配置启动
spring-boot:run -DactiveName=pro -Dmaven.test.skip=true
```

**使用`${}`作为动态参数的解决方案**

从spring-boot-starter-parent的pom.xml文件中查看 `delimiter that doesn't clash with Spring ${}`

```xml
<properties>
    <java.version>1.6</java.version>
    <resource.delimiter>@</resource.delimiter> <!-- delimiter that doesn't clash with Spring ${} placeholders -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
</properties>
```

若项目使用了 spring-boot-starter-parent 做项目版本管理，替换resource.delimiter属性

```xml
<!-- 使用spring-boot-starter-parent管理jar包版本 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.2.5.RELEASE</version>
</parent>

<!-- 需要在<properties>中添加 -->
<properties>
    <resource.delimiter>${}</resource.delimiter>
</properties>
```

**注意：使用些方式后，使用mvn命令打包时，不使用默认值方式`${参数名:默认值}`**

##### 3.8.5.2. 项目打包命令

- 需要将依赖的公共包安装到本地仓库，到时需要依赖打包到war包中
- 项目打包：参考5.1将前端部署后，因为配置了开发环境与正式版本环境的两套配置文件，使用maven命令打包时，需要输入配置文件的参数，进行打包即可，完成后将war包放到tomcat运行部署

```shell
# 项目安装
mvn clean install -DactiveName=pro -Dmaven.test.skip=true

# 项目打包
mvn clean package -DactiveName=pro -Dmaven.test.skip=true
```

### 3.9. 实现SpringBoot配置文件放在jar外部的方式
#### 3.9.1. 通过命令行指定

SpringApplication会默认将命令行选项参数转换为配置信息。例如，启动时命令参数指定：

```bash
java -jar myproject.jar --server.port=9000
```

从命令行指定配置项的优先级最高，不过可以通过setAddCommandLineProperties来禁用

```java
SpringApplication.setAddCommandLineProperties(false)
```

#### 3.9.2. 外置配置文件

- Spring程序会按优先级从下面这些路径来加载application.properties配置文件
    - 当前目录下的`/config`目录
    - 当前目录
    - classpath里的`/config`目录
    - classpath根目录
- 因此，要外置配置文件就很简单了，在jar所在目录新建config文件夹，然后放入配置文件，或者直接放在配置文件在jar目录

#### 3.9.3. 自定义配置文件

如果不想使用application.properties作为配置文件，输入以下命令

```bash
java -jar myproject.jar --spring.config.location=classpath:/default.properties,classpath:/override.properties
```

或者

```bash
java -jar -Dspring.config.location=D:\config\config.properties springbootrestdemo-0.0.1-SNAPSHOT.jar
```

也可以直接在代码里指定

```java
@SpringBootApplication
@PropertySource(value={"file:config.properties"})
public class SpringbootrestdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootrestdemoApplication.class, args);
    }
}
```

#### 3.9.4. 按Profile不同环境读取不同配置

- 不同环境的配置设置一个配置文件，例如：
    - dev环境下的配置配置在application-dev.properties中
    - prod环境下的配置配置在application-prod.properties中
- 在application.properties中指定使用哪一个文件

```properties
spring.profiles.active=dev
```

- 也可以在运行的时候手动指定：

```bash
java -jar myproject.jar --spring.profiles.active=prod
```

## 4. 热部署
### 4.1. 热部署配置

在开发中反复修改类、页面等资源，每次修改后都是需要重新启动才生效，这样每次启动都很麻烦，浪费了大量的时间。

1. 可以设置在修改代码后不重启就能生效，在pom.xml 中添加如下配置就可以实现这样的功能，称为热部署

```xml
<!-- 使用spring-boot-devtools提供的开发者工具，配置devtools开启热部署 -->
<dependency>
    <!--
       devtools可以实现页面热部署（即页面修改后会立即生效，这个可以直接在application.properties文件中配置spring.thymeleaf.cache=false来实现），
       实现类文件热部署（类文件修改后不会立即生效），实现对属性文件的热部署。
       即devtools会监听classpath下的文件变动，并且会立即重启应用（发生在保存时机），注意：因为其采用的虚拟机机制，该项重启是很快的
    -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <!-- 表示依赖不会向下传递 -->
    <optional>true</optional>
</dependency>
```

2. 仅仅加入devtools在我们的eclipse中还不起作用，这时候还需要添加的spring-boot-maven-plugin

```xml
<build>
    <plugins>
        <!--
            用于将应用打成可直接运行的jar（该jar就是用于生产环境中的jar） 值得注意的是，如果没有引用spring-boot-starter-parent做parent，
               且采用了上述的第二种方式，这里也要做出相应的改动
         -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <!-- fork : 如果没有该项配置，这个devtools不会起作用，即应用不会restart -->
                <fork>true</fork>
            </configuration>
        </plugin>
    </plugins>
</build>
```

> 将依赖关系标记为可选`<optional>true</optional>`是一种最佳做法，可以防止使用项目将devtools传递性地应用于其他模块。

注意：IDEA进行SpringBoot热部署失败原因

出现这种情况，并不是热部署配置问题，其根本原因是因为Intellij IEDA默认情况下不会自动编译，需要对IDEA进行自动编译的设置，如下：

![](images/20190501093352875_18330.png)

然后`Shift+Ctrl+Alt+/`，选择Registry

![](images/20190501093422694_17866.png)

### 4.2. 排除静态资源文件

- 静态资源文件在改变之后有时候没必要触发应用程序重启，例如thymeleaf模板文件就可以实时编辑，默认情况下，更改/META-INF/maven, /META-INF/resources ,/resources ,/static ,/public 或/templates下的资源不会触发重启，而是触发live reload（devtools内嵌了一个LiveReload server，当资源发生改变时，浏览器刷新）
- 但是如果想完成静态文件变化后，要应用重启那么可以配置如下

```properties
spring.devtools.restart.exclude=static/**,public/**
# 如果想保留默认配置，同时增加新的配置，则可使用
# spring.devtools.restart.additional-exclude属性
```

### 4.3. 使用一个触发文件

若不想每次修改都触发自动重启，可以在application.xml设置`spring.devtools.restart.trigger-file`指向某个文件，只有更改这个文件时才触发自动重启。示例如下：

![文件位置](images/20190913082207240_30019.png)

```yml
spring:
  devtools: # 配置更改指定文件时才触发自动重启
    restart:
      trigger-file: .trigger  # 文件所在路径是 /resources/META-INF/.trigger
```

### 4.4. 全局设置

可以通过向`$HOME`文件夹添加名为`.spring-boot-devtools.properties`的文件来配置全局devtools设置（请注意，文件名以“`.`”开头）。 添加到此文件的任何属性将适用于计算机上使用devtools的所有 Spring Boot应用程序。 例如，要配置重启始终使用触发器文件 ，可以添加以下内容：

```
〜/ .spring-boot-devtools.properties
```

### 4.5. Devtools 在 Spring Boot 中的可选配置

```properties
# Whether to enable a livereload.com-compatible server.
spring.devtools.livereload.enabled=true

# Server port.
spring.devtools.livereload.port=35729

# Additional patterns that should be excluded from triggering a full restart.
spring.devtools.restart.additional-exclude=

# Additional paths to watch for changes.
spring.devtools.restart.additional-paths=

# Whether to enable automatic restart.
spring.devtools.restart.enabled=true

# Patterns that should be excluded from triggering a full restart.
spring.devtools.restart.exclude=META-INF/maven/**,META-INF/resources/**,resources/**,static/**,public/**,templates/**,**/*Test.class,**/*Tests.class,git.properties,META-INF/build-info.properties

# Whether to log the condition evaluation delta upon restart.
spring.devtools.restart.log-condition-evaluation-delta=true

# Amount of time to wait between polling for classpath changes.
spring.devtools.restart.poll-interval=1s

# Amount of quiet time required without any classpath changes before a restart is triggered.
spring.devtools.restart.quiet-period=400ms

# Name of a specific file that, when changed, triggers the restart check. If not specified, any classpath file change triggers the restart.
spring.devtools.restart.trigger-file=
```

### 4.6. 其他热部署工具

由于Spring Boot应用只是普通的Java应用，所以JVM热交换（hot-swapping）也能开箱即用。不过JVM热交换能替换的字节码有限制，想要更**彻底的解决方案可以使用Spring Loaded项目或JRebel**。spring-boot-devtools 模块也支持应用快速重启(restart)。

## 5. 运行状态监控Actuator（待学习）

Spring Boot的Actuator 提供了运行状态监控的功能。Actuator的监控数据可以通过Rest、运程shell和JMX方式获得

## 6. Spring Boot 自动配置原理分析

Spring Boot框架是一个将整合框架的整合代码都写好了的框架。所以要知道它的工作原理才能够，找到各种整合框架可以配置的属性，以及属性对应的属性名。

### 6.1. spring-boot-starter-parent 父工程依赖管理原理

创建SpringBoot项目，继承了SpringBoot的父工程`spring-boot-starter-parent`后，查看工程的依赖关系，父工程依赖了`spring-boot-dependencies`工程，`spring-boot-denpendencies`的pom管理所有公共Starter依赖的版本，并且通过`<dependencyManagement>`标签实现jar版本管理

因为继承父工程`spring-boot-starter-parent`后，可以根据需要，直接引用相应的starter即可，不需要配置版本号

![](images/20201006095224766_3600.png)

#### 6.1.1. starters的原理

starters是依赖关系的整理和封装，是一套依赖坐标的整合。只要导入相关的starter即可该功能及其相关必需的依赖

> 举例：JPA or Web开发，只需要导入 `spring-boot-starter-data-jpa` 或 `spring-boot-starter-web` 即可

每个Starter包含了当前功能下的许多必备依赖坐标，这些依赖坐标是项目开发，上线和运行必须的。同时这些依赖也支持依赖传递。例如：`spring-boot-starter-web` 包含了所有web开发必须的依赖坐标

![](images/20201006100215636_11014.png)

**starter的命名规范**：

- 官方的starter命名：`spring-boot-starter-*`
- 非官方的starter命名：`thirdpartyproject-spring-boot-starter`

官方提供的Starter详见官方文档：https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/using-spring-boot.html#using-boot-starter

### 6.2. 自动配置信息位置说明

每个Starter基本都会有自动配置`AutoConfiguration`，`AutoConfiguration`的jar包定义了约定的默认配置信息。SpringBoot采用约定大于配置设计思想。

- SpringBoot的`spring-boot-autoconfigure-x.x.x.RELEASE.jar`中编写了所有内置支持的框架的相关的默认配置
    - `additional-spring-configuration-metadata.json`：默认配置
    - `spring.factories`：定义了自动配置相关的处理类的映射关系。在项目启动的时候会将相关映射的处理类加载到spring容器中

![](images/20201006141108089_25191.png)

![](images/20201006143251521_21845.png)

- 所有支持的框架根据功能类型来划分包，每个包都有一个`XxxAutoConfiguration`配置类，都是一个基于纯注解的配置类，是各种框架整合的代码。如图所示：

![](images/20201006145254228_20074.png)

- 如果配置的框架有默认的配置参数，都放在一个命名为`XxxProperties`的属性类，如图所示：

![](images/20201006145402145_17341.png)

- 通过项目的resources下的`application.properties`或`application.yml`文件可以修改每个整合框架的默认属性，从而实现了快速整合的目的。

![](images/20201006150503441_32641.png)

### 6.3. 配置流程说明

- 第一步：配置一个内置整合框架的参数，先到`spring-boot-autoconfigure-x.x.x.RELEASE.jar`找到对应的模块。
- 第二步：如果该框架有可以配置的参数，那么对应的整合模块中一定有一个XxxProperties类，在里面可以找可以设置的参数。
- 第三步：在resources源目录下的`application.properties`文件里面可以修改XxxProperties类中默认的参数。

![配置流程说明](images/_配置流程说明_1537025667_9599.jpg)

### 6.4. 自动配置流程分析

查看启动类注解`@SpringBootApplication`，可以跟踪加载的步骤

1. 需要标识`@EnableAutoConfiguration`注解
2. 该注解会使用`@Import(AutoConfigurationImportSelector.class)`注解引入自动配置处理类
3. 自动配置会读取`spring-boot-autoconfigure-x.x.x.RELEASE.jar`包下的`spring.factories`文件，获取需要加载的处理类
4. 比如内置web容器的处理类`EmbeddedWebServerFactoryCustomizerAutoConfiguration`，类上引入`@EnableConfigurationProperties({ServerProperties.class})`注解，用于加载默认配置类的参数

![](images/20201006152054124_172.png)

## 7. Spring Boot 视图

### 7.1. 静态资源html视图

- SpringBoot默认有四个静态资源文件夹：
  - classpath:/static/
  - classpath:/public/
  - classpath:/resources/
  - classpath:/META-INF/resources/
- 在spring-boot-autoconfigure-1.5.6.RELEASE.jar的web包的

ResourceProperties类中作了默认的配置：

```java
private static final String[] CLASSPATH_RESOUTCE_LOCATIONS = {
    "classpth:/META-INF/resources/", "classpath:/resources/",
    "classpath:/static/", "classpath:/public/" };
```

- 第一步：提供src/main/resources/public/html/user.html

```html
<!DOCTYPE html>
<html>
  <head>
    <title>SpringBoot</title>
    <meta charset="UTF-8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <link rel="shortcut icon" type="image/x-icon" href="logo.ico"/>
  </head>
  <body>
    user
  </body>
</html>
```

- 第二步：提供HelloController处理器

```java
@Controller
public class HelloController {
    @Autowired
    private Environment environment;
    @Value("${name}")
    private String name;
    @Value("${url}")
    private String url;

    // 响应数据为json格式：http://localhost:8080/hello
    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        System.out.println(environment.getProperty("name"));
        System.out.println(environment.getProperty("url"));
        System.out.println(name);
        System.out.println(url);
        return "Hello World";
    }

    // 响应数据为静态html页面： http://localhost:8080/user
    @GetMapping("/user")
    public String user(){
        return "/html/user.html";
    }
}
```

### 7.2. Jsp视图(不推荐)
- 第一步：创建Maven项目(war包)
- 第二步：配置依赖

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                        http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
  </parent>
  <groupId>com.moon</groupId>
  <artifactId>springboot02-jsp-test</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>war</packaging>

  <!-- 定义全局属性 -->
  <properties>
    <!-- 更改JDK版本 -->
    <java.version>1.7</java.version>
  </properties>

  <!-- 配置依赖关系 -->
  <dependencies>
    <!-- 配置web启动器(spring mvc) -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 配置devtools实现热部署 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
    </dependency>
    <!-- 配置servlet-api、jsp-api、el-api依赖 -->
    <dependency>
      <groupId>org.apache.tomcat.embed</groupId>
      <artifactId>tomcat-embed-jasper</artifactId>
      <scope>provided</scope>
    </dependency>
    <!-- 配置jstl依赖 -->
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>jstl</artifactId>
    </dependency>
  </dependencies>
</project>
```

- 第三步：提供application.properties属性文件

```properties
# 开启jsp视图
# 设置视图前缀
spring.mvc.view.prefix=/WEB-INF/jsp/
# 设置视图后缀
spring.mvc.view.suffix=.jsp
```

- 第四步：开发处理器ItemController

```java
@Controller
public class ItemController {
  /** 查询得到数据 */
  @GetMapping("/item")
  public String item(Model model){
    /** 添加响应数据 */
    model.addAttribute("itemArr", new String[]{"iphone7手机","华为手机","小米手机"});
    /** 返回视图 */
    return "item";
  }
}
```

- 第五步：提供src/main/webapp/WEB-INF/jsp/item.jsp

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>SpringBoot</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <ul>
      <c:forEach items="${itemArr}" var="item">
        <li>${item }</li>
      </c:forEach>
    </ul>
  </body>
</html>
```

- 第六步：开发Application作为SpringBoot引启类

```java
@SpringBootApplication // 代表为SpringBoot应用的运行主类
public class Application {
  public static void main(String[] args) {
    /** 创建SpringApplication应用对象 */
    SpringApplication springApplication = new SpringApplication(Application.class);
    /** 设置横幅模式(设置关闭) */
    springApplication.setBannerMode(Banner.Mode.OFF);
    /** 运行 */
    springApplication.run(args);
  }
}
```

访问地址：http://localhost:8080/item

### 7.3. FreeMarker视图

详见Spring Boot整合FreeMarker部分。

## 8. Spring Boot 部署

### 8.1. 配置 SpringBoot 构建插件

SpringBoot项目打包都需要配置spring-boot-maven-plugin插件：

```xml
<!-- 构建部分 -->
<build>
  <plugins>
    <!-- spring-boot-maven-plugin构建插件 -->
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```

### 8.2. 打成jar包部署

- 修改项目pom.xml文件中的打包类型，默认是jar

```xml
<packaging>jar</packaging>
```

- 执行maven打包命令或者使用IDEA的Maven工具点击package，会在项目根目录的target文件夹中生成jar包

```bash
## 移动至项目根目录，与pom.xml同级
mvn clean package
## 或者执行下面的命令 排除测试代码后进行打包
mvn clean package -Dmaven.test.skip=true
```

- 到jar包所在目录下，运行启动命令：

```bash
java -jar target/springboot_demo.jar
# 启动命令的时候也可以配置jvm参数。然后查看一下Java的参数配置结果
java -Xmx80m -Xms20m -jar target/springboot_demo.jar
```

> 注：启动之前先检查自己的pom.xml文件中是否有springboot的maven插件

进入到这个目录用压缩软件打开此jar包，其中发现了一个叫lib的文件夹，打开lib文件夹发现此文件夹下全是工程依赖的jar包，甚至还有tomcat。这种包含有jar包的jar包，称之为fatJAR(胖jar包)。由于fatJAR本身就包括tomcat，所以不需要另外部署了，直接在命令行就可以把应用启动起来，在命令行，进入到jar包所在的目录，可以通过`java –jar`命令来执行此jar包

![](images/20201006160709959_2233.png)

### 8.3. 打war包

spring-boot默认提供内嵌的tomcat，所以打包直接生成jar包，用`java -jar`命令就可以启动。但是有时候更希望一个tomcat来管理多个项目，这种情况下就需要项目是war格式的包而不是jar格式的包。

按照以下步骤完成对工程的改造

- 第一步：修改pom.xml

1. 将打包方式改为war
2. 添加的Tomcat依赖配置，覆盖Spring Boot自带的Tomcat依赖
3. 可选：在`<build></build>`标签内配置项目名（该配置类似于`server.context-path=xxx`）

```xml
<!-- 配置打包方式为war包 -->
<packaging>war</packaging>

<!-- 配置tomcat启动器(tomcat我们自己提供) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
  <scope>provided</scope>
</dependency>

<build>
    ...
    <finalName>MoonZero</finalName>
</build>
```

> 说明：spring-boot-starter-tomcat 是原来被传递过来的依赖，默认会打到包里，所以我们再次引入此依赖，并指定依赖范围为provided，这样tomcat 相关的jar就不会打包到war 里了。
> 目的：用自己tomcat，不用它内嵌的tomcat，这样内嵌的tomcat相关jar包就不需要。

- 第二步：添加ServletInitializer

```java
package com.moon.jav;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 定义此类作为web.xml使用
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        /* 设置启动类 */
        builder.sources(Application.class);
        /* 设置横幅模式 */
        builder.bannerMode(Banner.Mode.OFF);
        /* 返回SpringBoot应用启动对象 */
        return builder;
    }
}
```

> 说明：由于我们采用web3.0 规范，是没有web.xml 的，而此类的作用与web.xml相同。*注意：Application.class是本项目Spring Boot的启动类*

- 第三步：运行`mvn clean package`打包命令，在target目录下生成war包。将生成后将war包放入tomcat，启动tomcat，测试完成的功能是否可以使用。

![生成war包](images/20190829142838873_3136.png)

### 8.4. 内嵌tomcat参数
在application.properties设置相关参数即可，如：

```properties
# 设置tomcat端口
server.port=8080
# 设置服务地址
server.address=127.0.0.1
# 设置超时时间
server.connection-timeout=1000
# 设置上下文路径
server.contextPath=/boot
```
请求地址：http://127.0.0.2/boot/findAll

---

# Spring Boot 整合
## 1. 创建Spring Boot父项目（整合Spring Cloud）
### 1.1. 创建Maven父工程（pom）

- 采用Maven多Module的形式。创建Maven工程，在主Maven工和的pom文件引入Spring Boot的版本（根据需求定版本），Spring cloud版本（根据需求定版本），并可以指定一些公共的依赖，还有一些项目的公共属性（如项目的编码）
- 其余的Module工程的pom文件继承主Maven工程，拥有父工程的公共的依赖和配置
- 此案例是基于父工程通过Spring Boot整合Spring Cloud，单独使用Spring Boot工程整合Spring Cloud方法一样

父工程的pom.xml

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
        <module>其他模块</module>
        .......
    </modules>

    <!-- 引入Spring Boot的父项目依赖 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
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
        <spring-cloud.version>Dalston.RELEASE</spring-cloud.version>
    </properties>

    <!-- 锁定依赖的版本 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
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

其他的子模块的pom.xml，继承父工程即可(下面是例子)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <!-- 配置继承父工程 -->
    <parent>
        <groupId>com.moon</groupId>
        <artifactId>spring-cloud-project</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <!-- 本模块坐标 -->
    <modelVersion>4.0.0</modelVersion>
    <artifactId>spring-boot-demo</artifactId>
    <packaging>jar</packaging>
    <name>spring-boot-demo</name>
    <description>Demo project for Spring Boot</description>

    <!-- 配置子模块的特有依赖 -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 1.2. 整合开发
更多配置与开发详见工程`D:\code\IdeaProjects\spring-cloud-project`

#### 1.2.1. RestTemplate 对象

从Spring3.0开始，Spring为创建Rest API提供了良好的支持。借助 RestTemplate，Spring应用能够方便地使用REST资源

RestTemplate定义了36个与REST资源交互的方法，其中的大多数都对应于HTTP的方法。

相关的方法

- **delete()** 在特定的URL上对资源执行HTTP DELETE操作
- **exchange()**
  - 在URL上执行特定的HTTP方法，返回包含对象的ResponseEntity，这个对象是从响应体中映射得到的
- **execute()** 在URL上执行特定的HTTP方法，返回一个从响应体映射得到的对象
- **getForEntity()** 发送一个HTTP GET请求，返回的ResponseEntity包含了响应体所映射成的对象
- **getForObject()** 发送一个HTTP GET请求，返回的请求体将映射为一个对象
- **postForEntity()**
  - POST 数据到一个URL，返回包含一个对象的ResponseEntity，这个对象是从响应体中映射得到的
- **postForObject()** POST 数据到一个URL，返回根据响应体匹配形成的对象
- **headForHeaders()** 发送HTTP HEAD请求，返回包含特定资源URL的HTTP头
- **optionsForAllow()** 发送HTTP OPTIONS请求，返回对特定URL的Allow头信息
- **postForLocation()** POST 数据到一个URL，返回新创建资源的URL
- **put()** PUT 资源到特定的URL

## 2. 整合junit

1. 开启Spring Boot测试，在测试类加`@RunWith(SpringRunner.class)`。`@RunWith`是注解运行的主类
2. 测试类上添加`@SpringBootTest`注解，classes属性要指定启动类的class
    - 配置属性方式1：在`@SpringBootTest`注解加上属性webEnvironment设置web测试环境的端口，`SpringBootTest.WebEnvironment.RANDOM_PORT`为随机端口
    - 配置属性方式2：在`@SpringBootTest`注解加上属性classes指定的是引导类的字节码对象，如：`@SpringBootTest(classes = Application.class)`。*其中Application.java是Spring boot的引导类*

### 2.1. pom.xml添加test依赖

```xml
<!-- 配置测试启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2.2. 编写测试类(方式1)

```java
@RunWith(SpringRunner.class)
// 方式1：webEnvironment设置web测试环境的端口，SpringBootTest.WebEnvironment.RANDOM_PORT 为随机端口
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringbootdemoApplicationTests {
   @LocalServerPort
   private int port;
   private URL base;

   // TestRestTemplate是RestTemplate的测试类
   @Autowired
   private TestRestTemplate template;

   @Before
   public void setUp() throws Exception {
      this.base = new URL("http://localhost" + port + "/");
   }

   @Test
   public void getHello() {
      System.out.println(base.toString());
      System.out.println("1111");
   }
}
```

### 2.3. 编写测试类(方式2)

```java
@RunWith(SpringRunner.class)
// 方式2：设置classes属性，指定SpringBoot启动类
// @SpringBootTest(classes = Application.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void test() {
        List<User> users = userMapper.queryUserList();
        System.out.println(users);
    }
}
```

注：SpringRunner继承自SpringJUnit4ClassRunner，使用哪一个Spring提供的测试测试引擎都可以

```java
public final class SpringRunner extends SpringJUnit4ClassRunner
```

**注：之前的教程是写“@RunWith(SpringJUnit4ClassRunner.class) // 指定运行的主类”，但是使用的时候一直报错，不知道是不是其它地方有配置错误，还是说本来spring boot整合junit时不能这么配置。先暂时记录**

## 3. 整合c3p0
### 3.1. 自定义DataSourceConfiguration

```java
@Configuration // 定义配置信息类
public class DataSourceConfiguration {
    /** 定义创建数据源方法 */
    @Bean(name="dataSource") // 定义Bean
    @Primary // 主要的候选者
    @ConfigurationProperties(prefix="spring.datasource.c3p0") // 配置属性
    public DataSource getDataSource(){
        return DataSourceBuilder.create() // 创建数据源构建对象
               .type(ComboPooledDataSource.class) // 设置数据源类型
               .build(); // 构建数据源对象
    }
}
```

### 3.2. 在application.properties配置c3p0

```properties
# 配置c3p0
spring.datasource.c3p0.driverClass=com.mysql.jdbc.Driver
spring.datasource.c3p0.jdbcUrl=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.c3p0.user=root
spring.datasource.c3p0.password=123456
spring.datasource.c3p0.maxPoolSize=30
spring.datasource.c3p0.minPoolSize=10
spring.datasource.c3p0.initialPoolSize=10
```

## 4. 整合Spring-Data-JPA
### 4.1. 环境准备

- **第一步：导入数据库表**
    - 运行SpringBoot\准备资料\springboot.sql文件创建数据库表及表中数据
- **第二步：加入Spring-Data-JPA的启动器**

```xml
<!-- 配置web启动器(spring mvc) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- 配置devtools实现热部署 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>

<!-- 配置Spring-Data-JPA启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- 配置mysql驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!-- 配置c3p0连接池 -->
<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5.2</version>
</dependency>
```

- **第三步：application.properties配置文件**
    - 参考spring-boot-autoconfigure-1.5.6.RELEASE.jar中orm.jpa包中属性文件类**JpaProperties**
    - 官方文档

```properties
# 配置自定义的c3p0数据源
spring.datasource.c3p0.driverClass=com.mysql.jdbc.Driver
spring.datasource.c3p0.jdbcUrl=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.c3p0.user=root
spring.datasource.c3p0.password=123456
spring.datasource.c3p0.maxPoolSize=20
spring.datasource.c3p0.minPoolSize=10
spring.datasource.c3p0.initialPoolSize=10

# JPA
spring.jpa.showSql=true
spring.jpa.properties.hibernate.format_sql=true
```

- 注：
    - 其中，数据源（原生的datasource也可以，将c3p0去掉即可）配置包括driverClass(驱动类)、url(数据库地址)、user\password (用户名与密码)、其它数据源的相关参数(如：maxPoolSize等等)
    - JPA的配置包括：如showSql(是否显示sql语句)、format_sql(是否格式式sql)、hibernate.ddl-auto(配置为create时，程序启动时会在MySQ数据库中建表；配置为update时，在程序启动时不会在MySQL数据库中建表)等等


**将application.properties文件修改成application.yml文件**

```yml
spring:
    datasource:
        c3p0:
            driverClass: com.mysql.jdbc.Driver
            jdbcUrl: jdbc:mysql://localhost:3306/springboot_db
            user: root
            password: 123456
            maxPoolSize: 20
            minPoolSize: 10
            initialPoolSize: 10
    jpa:
        showSql: false
        properties:
            hibernate:
                format_sql: true
```

### 4.2. 整合开发

案例：使用Spring Boot + Spring MVC + Spring Data JPA 查询所有公告

- **第一步：创建entity**

```java
@Entity @Table(name="notice")
public class Notice implements Serializable {
    private static final long serialVersionUID = 5679176319867604937L;
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="title")
    private String title;
    @Column(name="content")
    private String content;
    /** setter and getter method */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
```

- **第二步：创建数据访问Dao**

```java
@Repository
public interface NoticeDao extends JpaRepository<Notice, Long>{
}
```

- **第三步：创建业务处理**

```java
public interface NoticeService {
    /** 查询所有的公告 */
    public List<Notice> findAll();
}

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeDao noticeDao;
    /** 查询所有的公告 */
    public List<Notice> findAll(){
        return noticeDao.findAll();
    }
}
```

- **第四步：创建处理器**

```java
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    /** 查询全部公告 */
    @GetMapping("/findAll")
    public List<Notice> findAll(Model model){
        return noticeService.findAll();
    }
}
```

- **第五步：第六步：编写启动类**
- **第六步：测试**
    - 浏览器地址栏输入：http://localhost:8080/findAll

## 5. 整合FreeMarker
### 5.1. 加入依赖

```xml
<!-- FreeMarker启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```

### 5.2. 编写处理器

```java
@Controller
public class UserController {
    /* 查询 */
    @GetMapping("/user")
    public String user(Model model){
        /* 添加响应数据 */
        model.addAttribute("tip", "用户数据");
        /* 返回视图 */
        return "user";
    }
}
```

### 5.3. 编写模板

在src\main\resources\templates路径下创建user.ftl模板，内容如下。最后运行启动类测试效果

```html
<!DOCTYPE html>
<html>
    <head>
        <title>Spring Boot</title>
        <meta charset="UTF-8"/>
    </head>
    <body>
        ${tip}
    </body>
</html>
```

### 5.4. 属性配置

参考spring-boot-autoconfigure-1.5.6.RELEASE.jar中freemarker包中属性文件类**FreeMarkerProperties**

```properties
# 配置FreeMarker
# 配置模版文件加载的基础路径，多个路径中间用逗号分隔
spring.freemarker.templateLoaderPath=classpath:/templates/,classpath:/template/
# 配置模版文件的后缀名
spring.freemarker.suffix=.ftl
# 配置模版文件的编码
spring.freemarker.charset=utf-8
# 配置模版文件使用缓存
spring.freemarker.cache=true
```

**注意：也可以直接注入FreeMarkerConfigurer操作FreeMarker。**

## 6. 整合Redis
### 6.1. 加入依赖

在pom.xml中加入依赖

```xml
<!-- 配置redis启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-redis</artifactId>
    <version>1.4.7.RELEASE</version>
</dependency>
```

### 6.2. 配置连接Redis

在application.properties文件中添加相关配置。

```properties
# 配置Redis单机版
spring.redis.host=192.168.12.128
spring.redis.port=6379

# 配置Redis集群版
#spring.redis.cluster.nodes=192.168.12.128:7001,192.168.12.128:7002,192.168.12.128:7003,192.168.12.128:7004,192.168.12.128:7005,192.168.12.128:7006
```

**说明：切换到集群版，注释掉单机版配置信息即可。**

### 6.3. 注入RedisTemplate测试redis操作

只需要直接注入RedisTemplate即可使用以下方法操作redis的五种不同的数据类型

![RedisTemplate操作对象](images/20190501085255786_31692.jpg)

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() throws JsonProcessingException {
        // 从redis缓存中获得指定的数据
        String userListData = redisTemplate.boundValueOps("user.findAll").get();
        // 如果redis中没有数据的话
        if (null == userListData) {
            //查询数据库获得数据
            List<User> all = userRepository.findAll();
            // 转换成json格式字符串
            ObjectMapper om = new ObjectMapper();
            userListData = om.writeValueAsString(all);
            // 将数据存储到redis中，下次在查询直接从redis中获得数据，不用在查询数据库
            redisTemplate.boundValueOps("user.findAll").set(userListData);
            System.out.println("===============从数据库获得数据===============");
        } else {
            System.out.println("===============从redis缓存中获得数据===============");
        }
        System.out.println(userListData);
    }
}
```

## 7. 整合Solr
### 7.1. 加入依赖

在pom.xml中加入依赖：

```xml
<!-- 配置solr启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-solr</artifactId>
</dependency>
```

**注意：solr-solrj的版本必须为5.0以上才可以用。**

### 7.2. 配置连接Solr

在application.properties中配置

```properties
# 配置Solr单机版
#spring.data.solr.host=http://192.168.12.128:8080/solr

# 配置Solr集群版
spring.data.solr.zkHost=192.168.12.128:3181,192.168.12.128:3182,192.168.12.128:3183
```

**说明：切换到单机版，注释掉集群版配置信息即可。**

### 7.3. SolrClient操作Solr

只需要直接注入SolrClient即可使用以下方法操Solr

![操作SolrClient](images/20190501085740120_19316.jpg)

## 8. 整合ActiveMQ
### 8.1. 加入依赖

在pom.xml中加入依赖

```xml
<!-- 配置ActiveMQ启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

### 8.2. 配置连接ActiveMQ

在application.properties文件中添加

```properties
# ActiveMQ消息服务器连接地址
spring.activemq.brokerUrl=tcp://192.168.12.128:61616
# 开启发布与订阅模式(默认为点对点模式)
spring.jms.pubSubDomain=true
```

### 8.3. 创建队列

在启动类中添加以下方法，创建队列

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /* 创建点对点消息队列 */
    @Bean
    public Queue queue() {
        return new ActiveMQQueue("SpringBoot.queue");
    }
}
```

### 8.4. 发送消息

编写Controller，注入JmsTemplate发送消息

```java
@RestController
@RequestMapping("/queue")
public class QueueController {
    /* 注入JmsTemplate消息发送模版对象 */
    @Autowired
    private JmsTemplate jmsTemplate;
    /* 注入目标消息模式 */
    @Autowired
    private Destination destination;

    /* 发送消息的方法 */
    @RequestMapping("/send/{message}")
    public String send(@PathVariable String message) {
        /* 发送消息 */
        this.jmsTemplate.convertAndSend(destination, message);
        return "消息发送成功!消息内容：" + message;
    }
}
```

### 8.5. 接收消息

编写bean，在类上加@Component注解让spring管理这个bean。消费消息方法：加@JmsListener注解

```java
@Component
public class ItemMessageListener {
    /* 消费消息方法 */
    @JmsListener(destination = "SpringBoot.queue")
    public void readMessage(String message) {
        System.out.println("接受到的消息是：" + message);
    }
}
```

## 9. !!整合Swagger2(使用的时再总结，在深入理解spring cloud书中4.7章节)

Swagger2是一个功能强大的在线API文档的框架，目前版本为2.x，Swagger2提供了在线文档的查阅和测试功能。利用Swagger2很容易构建RESTful风格的API

