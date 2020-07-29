# Spring 注解开发使用详解汇总

## 1. Spring注解驱动开发入门

### 1.1. 简介

Spring在2.5版本引入了注解配置的支持，同时从Spring 3.x版本开始，Spring JavaConfig项目提供的许多特性成为核心Spring框架的一部分。因此，可以使用Java而不是XML文件来定义应用程序类外部的bean。在Spring的官方文档里提供了四个基本注解`@Configuration`，`@Bean`，`@Import`，`@DependsOn`用于驱动开发

### 1.2. 注解驱动入门案例

#### 1.2.1. 案例需求

1. 需求：实现保存一条数据到数据库。
2. 示例使用的表结构：

```sql
create table account(
    id int primary key auto_increment,
    name varchar(50),
    money double(7,2)
);
```

3. 要求：使用spring框架中的JdbcTemplate和DriverManagerDataSource，使用纯注解配置spring的ioc

#### 1.2.2. 代码实现

##### 1.2.2.1. 导入依赖

创建示例项目，pom.xml文件引入相关依赖

```xml
<dependencies>
    <!-- spring核心依赖 -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.1.6.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>5.1.6.RELEASE</version>
    </dependency>
    <!-- mysql驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.45</version>
    </dependency>
</dependencies>
```

##### 1.2.2.2. 编写配置类

创建spring的配置类，用于代替xml配置文件

```java
package com.moon.springsample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * spring项目的配置类，用于代替传统的xml配置文件
 */
@Configuration /* 标识当前类为配置类 */
@Import(JdbcConfig.class) /* @Import注解是写在类上的，通常是和注解驱动的配置类一起使用的。其作用是引入其他的配置类 */
@PropertySource("classpath:jdbc.properties") /* 用于指定读取资源文件的位置。不仅支持properties，也支持xml文件 */
public class SpringConfiguration {
}
```

```java
package com.moon.springsample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * 连接数据库的配置类
 */
public class JdbcConfig {
    // @Value注解用于注入基本类型和String类型的数据。它支持spring的EL表达式，可以通过${}的方式获取配置文件中的数据
    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    /* 创建JdbcTemplate对象 */
    @Bean("jdbcTemplate") // @Bean注解用于方法上，表示把当前方法的返回值存入spring的ioc容器
    public JdbcTemplate createJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /* 创建DataSource对象 */
    @Bean("dataSource")
    public DataSource createDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
```

##### 1.2.2.3. 编写配置文件

在resources目录下，创建数据库连接参数的配置文件jdbc.properties

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/tempdb?characterEncoding=utf-8
jdbc.username=root
jdbc.password=123456
```

##### 1.2.2.4. 测试

```java
/* 测试spring全注解开发示例 */
public static void main(String[] args) {
    // 1. 获取基于注解的spinrg容器
    ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample");
    // 2. 测试根据id获取spring容器中的对象
    JdbcTemplate jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);
    // 3. 进行数据库操作
    jdbcTemplate.update("insert into account(name,money) values (?,?)","Moon", 1888);
}
```






# 其他暂存

## 1. `@ControllerAdvice` 注解

`@ControllerAdvice`，是spring3.2提供的新注解，大体的作用是控制器增强。还有`@RestControllerAdvice`注解，是`@ControllerAdvice`与`@ResponseBody`的组合体

**使用`@ControllerAdvice`，不用任何的配置，只要把这个类放在项目中配置了Spring包扫描的位置（`<context:component-scan>`）。就可以实现全局异常的回调。**

官方定义说明

- `@ControllerAdvice`是一个`@Component`，用于定义@`ExceptionHandler`，`@InitBinder`和`@ModelAttribute`方法，适用于所有使用`@RequestMapping`方法。
- Spring4之前，`@ControllerAdvice`在同一调度的Servlet中协助所有控制器。Spring4已经改变：`@ControllerAdvice`支持配置控制器的子集，而默认的行为仍然可以利用。
- 在Spring4中，` @ControllerAdvice`通过`annotations()`, `basePackageClasses()`, `basePackages()`方法定制用于选择控制器子集。

此注解一般配合`@ExceptionHandler`使用，异常处理器，此注解的作用是当出现其定义的异常时进行处理的方法，其可以使用springmvc提供的数据绑定，比如注入HttpServletRequest等，还可以接受一个当前抛出的Throwable对象。

例：

```java
/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限校验失败 如果请求为ajax返回json，普通请求跳转页面
     */
    @ExceptionHandler(AuthorizationException.class)
    public Object handleAuthorizationException(HttpServletRequest request, AuthorizationException e) {
        log.error(e.getMessage(), e);
        if (ServletUtils.isAjaxRequest(request)) {
            return AjaxResult.error(PermissionUtils.getMsg(e.getMessage()));
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("/error/unauth");
            return modelAndView;
        }
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public AjaxResult handleException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult notFount(RuntimeException e) {
        log.error("运行时异常:", e);
        return AjaxResult.error("运行时异常:" + e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("服务器错误，请联系管理员");
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public AjaxResult businessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }
}
```

*注：AjaxResult.error是自定义的一个方法，封装了一个返回的map，用来返回前端是一个json对象*

## 2. 参考资料

1. http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247491621&idx=1&sn=332712bcb7146cb9f5f3f280e3bb1c2b&chksm=eb506513dc27ec05d2143663720c1a3b7d9b610954763cb7cea3f8f31a6bad5064d8ef5b13c1&mpshare=1&scene=1&srcid=&sharer_sharetime=1574220624476&sharer_shareid=6087581adbbb79acccd7e873962f1a09#rd

