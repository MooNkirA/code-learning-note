# SpringBoot 整合 Junit

## 1. 关于 Spring Boot 中 Junit 的概述

Spring Boot 2.2.x 往后版本开始引入 JUnit 5 作为单元测试默认库，在 Spring Boot 2.2.x 版本之前，`spring-boot-starter-test` 包含了 JUnit 4 的依赖，Spring Boot 2.2.x 版本之后替换成了 Junit Jupiter。

## 2. 添加 spring-boot-starter-test 依赖

在 Spring Boot 工程，直接添加 `spring-boot-starter-test` 依赖即可使用 Junit。

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

> 注意：Spring Boot 2.2.x 以前版本默认的测试库是 Junit4，在 2.2.x 其更高版本中，默认的测试库是 Junit5。如果工程是非web工程，则至少需要引入 `spring-boot-starter` 的依赖。

## 3. 使用步骤

### 3.1. Spring Boot 2.2.x-

在 Spring Boot 2.2.x 版本之前

1. 开启Spring Boot测试，在测试类加`@RunWith(SpringRunner.class)`。`@RunWith`是注解运行的主类
2. 测试类上添加`@SpringBootTest`注解，classes属性要指定启动类的class
    - 配置属性方式1：在 `@SpringBootTest `注解加上属性 `webEnvironment` 设置web测试环境的端口，`SpringBootTest.WebEnvironment.RANDOM_PORT`为随机端口
    - 配置属性方式2：在 `@SpringBootTest` 注解加上属性 `classes` 指定的是引导类的字节码对象，如：`@SpringBootTest(classes = Application.class)`。*其中 `Application.java` 是Spring boot的引导类*

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

## 4. Spring Boot 测试注意问题

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

## 5. 使用示例

> 注：如下示例，在 Spring Boot 2.2.x 以前版本使用 Junit4，需要添加`@RunWith(SpringRunner.class)`注解，但在 2.2.x 后更高版本中，则不需要。

### 5.1. 指定 web 测试环境的端口

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

### 5.2. 指定启动类

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

注：SpringRunner继承自SpringJUnit4ClassRunner，使用哪一个Spring提供的测试测试引擎都可以

```java
public final class SpringRunner extends SpringJUnit4ClassRunner
```



