# Spring Security

- 官网：https://spring.io/projects/spring-security
- 官方源码仓库地址：https://github.com/spring-projects/spring-security
- 官方示例仓库地址：https://github.com/spring-projects/spring-security-samples

## 1. Spring Security 概述

Spring Security 是一个能够为基于 Spring 的企业应用系统提供声明式的安全访问控制解决方案的安全框架。由于它是 Spring 生态系统中的一员，因此它伴随着整个 Spring 生态系统不断修正、升级，在 spring boot 项目中加入 spring security 更是十分简单，使用 Spring Security 减少了为企业系统安全控制编写大量重复代码的工作。

## 2. Spring Security 快速开始

此示例基于 Maven 构建的 Spring MVC 项目

### 2.1. 初始化示例项目

创建 maven 工程 spring-security-5.1.x，工程结构如下：


#### 2.1.1. 引入依赖

在 spring mvc 项目的基础上增加 spring-security 的依赖，完整依赖如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <artifactId>spring-security-sample</artifactId>
        <groupId>com.moon</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>spring-security-5.1.x</artifactId>
    <packaging>war</packaging>
    <description>
        Spring Security 5.1.x 版本使用示例工程（基于 Spring MVC 的 Web 项目）
    </description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>

        <spring-security.version>5.1.4.RELEASE</spring-security.version>
        <spring.version>5.1.5.RELEASE</spring.version>
    </properties>

    <dependencies>
        <!-- Spring Security 依赖 -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-web</artifactId>
            <version>${spring-security.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-config</artifactId>
            <version>${spring-security.version}</version>
        </dependency>

        <!-- Spring MVC 依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.0.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>${artifactId}</finalName>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.tomcat.maven</groupId>
                    <artifactId>tomcat7-maven-plugin</artifactId>
                    <version>2.2</version>
                </plugin>
                <!-- 编译插件 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <configuration>
                        <source>1.8</source>
                        <target>1.8</target>
                    </configuration>
                </plugin>
                <!-- 资源打包插件 -->
                <plugin>
                    <artifactId>maven-resources-plugin</artifactId>
                    <configuration>
                        <encoding>utf-8</encoding>
                        <useDefaultDelimiters>true</useDefaultDelimiters>
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
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

</project>
```

#### 2.1.2. 配置 Spring 容器

创建 `com.moon.spring.security.config.ApplicationConfig` 类，用于替代 applicationContext.xml 配置文件。对应在 web.xml 中的 `ContextLoaderListener` 等配置。

```java
@Configuration
@ComponentScan(basePackages = "com.moon.spring.security",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class ApplicationConfig {
}
```

#### 2.1.3. 配置 Servlet Context

本案例采用 Servlet3.0 无 web.xml 方式，创建 `com.moon.spring.security.config.WebConfig` 类，它对应于 web.xml  文件中的 `DispatcherServlet` 配置

> 这里因为使用了 Spring Security 框架，所以不需要自定义登陆授权的拦截器，由框架来实现

```java
/**
 * 此配置类相当于 spring-mvc.xml 配置文件，相应 DispatcherServlet 的配置。
 * 在此类中配置 Spring MVC 的视频解析器
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.moon.spring.security",
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class WebConfig {
    // 视图解析器
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
}
```

#### 2.1.4. 配置加载 Spring 容器

在 init 包下创建 Spring 容器初始化类 `SpringApplicationInitializer`，此类实现 `WebApplicationInitializer` 接口，Spring 容器启动时加载 `WebApplicationInitializer` 接口的所有实现类。

注：一般自定义的初始化类会继承抽象现实类 `org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer` ，它实现了 `WebApplicationInitializer` 接口。

```java
/**
 * 实现 WebApplicationInitializer 接口。用于初始化 Spring 容器。
 * 此类相当于 web.xml 配置文件，使用了 servlet3.0 开发则不需要再定义 web.xml
 */
public class SpringApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    /* 指定rootContext的配置类 */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ApplicationConfig.class};
    }

    /* 指定servletContext的配置类 */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    /* 配置 servlet 访问地址映射 */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```

### 2.2. 实现认证功能

#### 2.2.1. 认证登陆页面

Spring Security 框架默认提供认证页面，不需要使用者额外开发。

![](images/68400117232067.png)

- 登陆的url：`http://项目访问域名/login`
- 登出的url：`http://项目访问域名/logout`

#### 2.2.2. Spring Security 安全配置

Spring Security 提供了用户名密码登录、退出、会话管理等认证功能，只需要配置即可使用。

- 在 config 包下创建 Spring Security 的配置类，继承 `org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter` 抽象类，安全配置类主要需要设置的内容包括：**用户信息**(即用户登陆的账号与密码)、**密码编码器**(即密码加密后比较的方式，也可以直接比较)、**安全拦截机制**(即配置需要拦截校验的url)。

```java
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 创建 UserDetailsService 实例。
     * 用于定义用户信息服务（查询用户信息）
     *
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // 正常情况是查询数据库，此示例为了简单直接创建保存在内存中的用户信息服务
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 创建用户信息与配置权限标识
        manager.createUser(User.withUsername("admin").password("123").authorities("p1").build());
        manager.createUser(User.withUsername("moon").password("456").authorities("p2").build());
        return manager;
    }

    /**
     * 密码编码器，即设置登陆时密码的校验
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 此示例为了方便，暂时使用密码无转码的方式来验证
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 安全拦截机制（最重要）
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/check/p1").hasAuthority("p1") // 设置不同权限访问的url
                .antMatchers("/check/p2").hasAuthority("p2") // 设置不同权限访问的url
                .antMatchers("/check/**").authenticated() // 设置所有 /check/** 的请求必须认证通过
                .anyRequest().permitAll()  // 设置除了上面配置的 /check/**，其它的请求可以访问
                .and()
                .formLogin() // 允许表单登录
                .successForwardUrl("/login-success"); // 自定义登录成功的页面地址

    }
}
```

以上 Spring Security 的配置主要是在 `userDetailsService()` 方法中，返回了一个 `UserDetailsService` 给 spring 容器，Spring Security 会使用它来获取用户信息。示例暂时使用 `InMemoryUserDetailsManager` 实现类，并在其中分别创建了“admin”、“moon”两个用户，并设置密码和权限。

而在 `configure()` 方法中，通过 `HttpSecurity` 设置了安全拦截规则，其中包含了以下内容：

1. url匹配/r/**的资源，经过认证后才能访问。
2. 其他url完全开放。
3. 支持form表单认证，认证成功后转向/login-success。



### 2.3. 附录：HttpSecurity 配置列表

![](images/406040423220166.png)
