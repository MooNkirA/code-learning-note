# Spring Boot Testing

Spring Boot 提供了一些实用程序和注解来帮助测试应用程序。测试支持由两个模块提供：

- Spring-boot-test 包含核心项目
- Spring-boot-test-autoconfigure 支持测试的自动配置

## 1. 关于 Spring Boot 中 Junit 的概述

Spring Boot 2.2.x 往后版本开始引入 JUnit 5 作为单元测试默认库，在 Spring Boot 2.2.x 版本之前，`spring-boot-starter-test` 包含了 JUnit 4 的依赖，Spring Boot 2.2.x 版本之后替换成了 Junit Jupiter。

## 2. 添加 spring-boot-starter-test 依赖

在 Spring Boot 工程，直接添加 `spring-boot-starter-test` 依赖即可使用 Junit

```xml
<!-- spring boot 依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<!-- 配置测试启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

> 注意：Spring Boot 2.2.x 以前版本默认的测试库是 Junit4，在 2.2.x 其更高版本中，默认的测试库是 Junit5。如果工程是非 web 工程，则至少需要引入 `spring-boot-starter` 的依赖。

## 3. 基础使用步骤

### 3.1. Spring Boot 2.2.x-

在 Spring Boot 2.2.x 版本之前

1. 开启 Spring Boot 测试，在测试类加 `@RunWith(SpringRunner.class)`。`@RunWith` 是注解运行的主类
2. 测试类上添加`@SpringBootTest`注解，`classes` 属性要指定启动类的 class

### 3.2. Spring Boot 2.2.x+

从 2.2.x 版本开始，Spring Boot 使用 Junit 5 做为单元测试默认库。*与 Jnuit4 有一点不同*

开启 Spring Boot 的测试，只需要在测试类上加上 `@SpringBootTest` 注解即可。*已无 `@RunWith` 注解*

```java
@SpringBootTest(classes = {引导类.class})
public class XxxxTest {
    // 注入需要的 spring 容器的对象
    @Autowired
    private Environment environment;

    @Test
    public void testXxxx() {
        // 相关的测试代码...
    }
}
```

### 3.3. Spring Boot 测试注意问题

如果当前测试类所在包不在工程引导类所在包或其子包时，就会报错找到配置的错误：`java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) with your test`，有两种解决方法如下：

1. 在 `@SpringBootTest` 注解中，通过 `classes` 属性指定启动类

```java
@SpringBootTest(classes = {JunitApplication.class})
public class JunitTest2 {

    @Test
    public void testBasic() {
        System.out.println("spring boot 整合 junit，测试类不在启动类所在的包及其子包下....");
    }

}
```

2. 添加 `@ContextConfiguration` 注解，通过 `classes` 属性指定启动类

```java
@SpringBootTest
@ContextConfiguration(classes = {JunitApplication.class})
public class JunitTest2 {
    @Test
    public void testBasic() {
        System.out.println("spring boot 整合 junit，测试类不在启动类所在的包及其子包下....");
    }
}
```

## 4. 进阶使用示例

> 注：如下示例，在 Spring Boot 2.2.x 以前版本使用 Junit4，需要添加`@RunWith(SpringRunner.class)`注解，但在 2.2.x 后更高版本中，则不需要。

### 4.1. 指定 web 测试环境的端口

`@SpringBootTest `注解的 `webEnvironment` 属性，用于设置 web 测试环境的端口，如：`SpringBootTest.WebEnvironment.RANDOM_PORT`为随机端口

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

### 4.2. 指定启动类

`@SpringBootTest` 注解的 `classes` 属性，用于指定的是引导类的字节码对象，如：`@SpringBootTest(classes = Application.class)`。*其中 `Application.java` 是Spring boot的引导类*

```java
@RunWith(SpringRunner.class)
// 方式2：设置classes属性，指定SpringBoot启动类
@SpringBootTest(classes = Application.class)
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

注：`SpringRunner` 继承自 `SpringJUnit4ClassRunner`，使用哪一个 Spring 提供的测试测试引擎都可以

```java
public final class SpringRunner extends SpringJUnit4ClassRunner
```

### 4.3. 加载测试专用属性

很多情况下测试时需要模拟一些线上情况，或者模拟一些特殊情况。此时可以每次测试的时候都去修改源码 application.yml 中的配置进行测试。但每次测试前进行修改，测试后又需要改回去，这种做法太麻烦了。于是 Spring Boot 提供了在测试环境中创建一组临时属性，去覆盖源码中设定的属性，这样测试用例就相当于是一个独立的环境，能够独立测试，

#### 4.3.1. 临时属性

使用注解 `@SpringBootTest` 的 `properties` 属性，可以为当前测试用例添加临时的属性，覆盖源码配置文件中对应的属性值进行测试。具体使用示例如下：



