# SpringBoot注解汇总

## 1. Spring Boot 相关的 spring 注解

### 1.1. @Configuration

这是 Spring 3.0 添加的一个注解，用于定义配置类，用来代替 applicationContext.xml 配置文件，所有这个配置文件里面能做到的事情都可以通过这个注解所在类来进行注册。被注解的类内部包含有一个或多个被`@Bean`注解的方法，这些方法将会被`AnnotationConfigApplicationContext`或`AnnotationConfigWebApplicationContext`类进行扫描，并用于构建bean定义，初始化Spring容器。

```java
@Configuration
public class TaskAutoConfiguration {
    @Bean
    @Profile("biz-electrfence-controller")
    public BizElectrfenceControllerJob bizElectrfenceControllerJob() {
        return new BizElectrfenceControllerJob();
    }

    @Bean
    @Profile("biz-consume-1-datasync")
    public BizBikeElectrFenceTradeSyncJob bizBikeElectrFenceTradeSyncJob() {
        return new BizBikeElectrFenceTradeSyncJob();
    }
}
```

### 1.2. @ComponentScan

这是 Spring 3.1 添加的一个注解，用来代替配置文件中的 component-scan 配置，开启组件扫描，即自动扫描包路径下的 `@Component` 注解进行注册 bean 实例到 context 中。

对于`@Controller`，`@Service`，`@Repository`注解，查看其源码会发现，它们中有一个共同的注解`@Component`，其实 `@ComponentScan` 注解默认就会装配标识了`@Controller`，`@Service`，`@Repository`，`@Component`注解的类到spring容器中。

```java
package com.moon.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

// 只有value属性时，value可以省略不写。@ComponentScan("com.moon.check.api")
@ComponentScan(value = "com.moon.check.api")
public class CheckApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckApiApplication.class, args);
    }
}
```

`@SpringBootApplication` 注解也包含了 `@ComponentScan` 注解，所以在使用中也可以通过 `@SpringBootApplication` 注解的 `scanBasePackages` 属性进行配置

```java
package com.moon.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.moon.check.api", "com.moon.check.service"})
public class CheckApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckApiApplication.class, args);
    }
}
```

### 1.3. @Import

这是 Spring 3.0 添加的新注解，用来导入一个或者多个 `@Configuration` 注解修饰的类，此注解在 Spring Boot 里面应用很多。

通过导入的方式实现把实例加入springIOC容器中。可以在需要时将没有被Spring容器管理的类导入至Spring容器中。

```java
//类定义
public class Square {
}

public class Circular {
}

//导入
@Import({Square.class, Circular.class})
@Configuration
public class MainConfig {
}
```

### 1.4. @ImportResource

Spring 3.0 添加的新注解，用来导入一个或者多个 Spring  配置文件，这对 Spring Boot 兼容老项目非常有用，因为有些配置无法通过 Java Config 的形式来配置就只能用这个注解来导入。

和`@Import`类似，区别就是`@ImportResource`导入的是配置文件

```java
package com.moon.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ImportResource;

// 导入xml配置
@ImportResource("classpath:spring-redis.xml")
public class CheckApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckApiApplication.class, args);
    }
}
```

### 1.5. @Component

`@Component`是一个spring框架的“元注解”，意思是可以注解其他类注解，如`@Controller`、`@Service`、`@Repository`。带此注解的类被看作组件，当使用基于注解的配置和类路径扫描的时候，这些类就会被实例化。其他类级别的注解也可以被认定为是一种特殊类型的组件，比如`@Controller` 控制器（注入服务）、`@Service`服务（注入dao）`、@Repository` dao（实现dao访问）。`@Component`泛指组件，当组件不好归类的时候，可以使用这个注解进行标注，作用就相当于 XML配置`<bean id="" class=""/>`

## 2. Spring Boot 核心注解
### 2.1. @CrossOrigin (Spring Boot解决跨域问题)

> SpringBoot 与 SpringMVC 一样可以使用`@CrossOrigin`注解解决跨域问题，均要求在Spring4.2及以上的版本

#### 2.1.1. @CrossOrigin 源码解析(翻译参考网络)

```java
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {

    String[] DEFAULT_ORIGINS = { "*" };

    String[] DEFAULT_ALLOWED_HEADERS = { "*" };

    boolean DEFAULT_ALLOW_CREDENTIALS = true;

    long DEFAULT_MAX_AGE = 1800;

    /**
     * 同origins属性一样
     */
    @AliasFor("origins")
    String[] value() default {};

    /**
     * 所有支持域的集合，例如"http://domain1.com"。
     * <p>这些值都显示在请求头中的Access-Control-Allow-Origin
     * "*"代表所有域的请求都支持
     * <p>如果没有定义，所有请求的域都支持
     * @see #value
     */
    @AliasFor("value")
    String[] origins() default {};

    /**
     * 允许请求头重的header，默认都支持
     */
    String[] allowedHeaders() default {};

    /**
     * 响应头中允许访问的header，默认为空
     */
    String[] exposedHeaders() default {};

    /**
     * 请求支持的方法，例如"{RequestMethod.GET, RequestMethod.POST}"}。
     * 默认支持RequestMapping中设置的方法
     */
    RequestMethod[] methods() default {};

    /**
     * 是否允许cookie随请求发送，使用时必须指定具体的域
     */
    String allowCredentials() default "";

    /**
     * 预请求的结果的有效期，默认30分钟
     */
    long maxAge() default -1;

}
```

#### 2.1.2. @CrossOrigin 注解相关属性

- origin属性
    - "*"代表所有域名都可访问
    - {"a","b",..}，指定多个域名可以访问
- maxAge属性
    - 飞行前响应的缓存持续时间的最大年龄，简单来说就是Cookie的有效期 单位为秒。若maxAge是负数，则代表为临时Cookie，不会被持久化，Cookie信息保存在浏览器内存中，浏览器关闭Cookie就消失

#### 2.1.3. 几种使用方式

1. 如果想要对某一接口配置CORS，可以在方法上添加 `@CrossOrigin` 注解

```java
@CrossOrigin(origins = {"http://localhost:9000", "null"})
@RequestMapping(value = "/test", method = RequestMethod.GET)
public String greetings() {
    return "{\"project\":\"just a test\"}";
}
```

2. 如果想对一系列接口添加 CORS 配置，可以在类上添加注解，对该类声明所有接口都有效

```java
@CrossOrigin(origins = {"http://localhost:9000", "null"})
@RestController
@SpringBootApplication
public class SpringBootCorsTestApplication {
    ......
}
```

3. 如果想添加全局配置，则需要添加一个配置类

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }
}
```

4. 还可以通过添加 Filter 的方式，配置 CORS 规则，并手动指定对哪些接口有效

```java
@Bean
public FilterRegistrationBean corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);   config.addAllowedOrigin("http://localhost:9000");
    config.addAllowedOrigin("null");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config); // CORS 配置对所有接口都有效
    FilterRegistrationBean bean = newFilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
}
```

### 2.2. @SpringBootApplication

Spring Boot 最最最核心的注解，用在 Spring Boot 主类上，标识这是一个 Spring Boot 应用，用来开启 Spring Boot 的各项能力

实际上这个注解就是 `@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan` 这三个注解的组合，也可以用这三个注解来代替 `@SpringBootApplication` 注解。由于这些注解一般都是一起使用，所以Spring Boot提供了一个统一的注解@SpringBootApplication。

```java
package com.moon.test;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        MybatisAutoConfiguration.class,
        MailSenderAutoConfiguration.class,
})
public class SpringBootApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationDemo.class, args);
    }
}
```

### 2.3. @EnableAutoConfiguration

允许 Spring Boot 自动配置注解，开启这个注解之后，Spring Boot 就能根据当前类路径下的包或者类来配置 Spring Bean。

> 如：当前类路径下有 Mybatis 这个 JAR 包，MybatisAutoConfiguration 注解就能根据相关参数来配置 Mybatis 的各个 Spring Bean。

`@EnableAutoConfiguration` 实现的关键在于引入了AutoConfigurationImportSelector，其核心逻辑为selectImports方法，逻辑大致如下：

- 从配置文件`META-INF/spring.factories`加载所有可能用到的自动配置类；
- 去重，并将 exclude 和 excludeName 属性携带的类排除；
- 过滤，将满足条件（`@Conditional`）的自动配置类返回；

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
// 导入AutoConfigurationImportSelector的子类
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	String[] excludeName() default {};
}
```

### 2.4. @SpringBootConfiguration

此注解就是 `@Configuration` 注解的变体，只是用来修饰是 Spring Boot 配置而已，或者可利于 Spring Boot 后续的扩展。

### 2.5. @Conditional

Spring 4.0 新提供的注解，用来标识一个 Spring Bean 或者  Configuration 配置文件，当满足指定的条件才开启配置。

通过 `@Conditional` 注解可以根据代码中设置的条件装载不同的bean，在设置条件注解之前，先要把装载的bean类去实现Condition接口，然后对该实现接口的类设置是否装载的条件。Spring Boot注解中的`@ConditionalOnProperty`、`@ConditionalOnBean`等以`@Conditional*`开头的注解，都是通过集成了`@Conditional`来实现相应功能的。

#### 2.5.1. @ConditionalOnBean

组合 `@Conditional` 注解，当容器中有指定的 Bean 才开启配置

`@ConditionalOnBean(A.class)` 仅仅在当前上下文中存在A对象时，才会实例化一个Bean，也就是说只有当A.class 在spring的applicationContext中存在时，这个当前的bean才能够创建

```java
package com.moon.test;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class SpringBootConfigurationDemo {
    // 当前环境上下文存在DefaultMQProducer实例时，才能创建RocketMQProducerLifecycle这个Bean
    @ConditionalOnBean(DefaultMQProducer.class)
    @Bean
    public RocketMQProducerLifecycle rocketMQLifecycle() {
        return new RocketMQProducerLifecycle();
    }
}
```

#### 2.5.2. @ConditionalOnMissingBean

组合 `@Conditional` 注解，和 `@ConditionalOnBean` 注解相反，当容器中没有指定的 Bean 才开启配置。

```java
@SpringBootConfiguration
public class SpringBootConfigurationDemo {
    // 仅当当前环境上下文缺失RocketMQProducer对象时，才允许创建RocketMQProducer Bean对象
    @ConditionalOnMissingBean(RocketMQProducer.class)
    @Bean
    public RocketMQProducer mqProducer() {
        return new RocketMQProducer();
    }
}
```

#### 2.5.3. @ConditionalOnClass

组合 `@Conditional` 注解，当容器中有指定的 Class 才开启配置。可以仅当某些类存在于classpath上时候才创建某个Bean。

```java
@SpringBootConfiguration
public class SpringBootConfigurationDemo {
    // 当classpath中存在类HealthIndicator时，才创建HealthIndicator Bean对象
    @ConditionalOnClass(HealthIndicator.class)
    @Bean
    public HealthIndicator rocketMQProducerHealthIndicator(Map<String, DefaultMQProducer> producers) {
        if (producers.size() == 1) {
            return new RocketMQProducerHealthIndicator(producers.values().iterator().next());
        }
    }
}
```

#### 2.5.4. @ConditionalOnMissingClass

组合 `@Conditional` 注解，和 `@ConditionalOnMissingClass` 注解相反，当容器（classpath）中没有指定的 Class 才开启配置

#### 2.5.5. @ConditionalOnWebApplication

组合 `@Conditional` 注解，当前项目类型是 WEB 项目才开启配置。当前项目有以下 3 种类型。

```java
enum Type {
    /**
     * Any web application will match.（任何Web项目都匹配）
     */
    ANY,
    /**
     * Only servlet-based web application will match.（仅但基础的Servelet项目才会匹配）
     */
    SERVLET,
    /**
     * Only reactive-based web application will match.（只有基于响应的web应用程序才匹配）
     */
    REACTIVE
}
```

#### 2.5.6. @ConditionalOnNotWebApplication

组合 `@Conditional` 注解，和 `@ConditionalOnWebApplication` 注解相反，当前项目类型不是 WEB 项目才开启配置。

#### 2.5.7. @ConditionalOnProperty

组合 `@Conditional` 注解，当指定的属性有指定的值时才开启配置。具体操作是通过其两个属性name以及havingValue来实现的，其中name用来从`application.properties`中读取某个属性值，如果该值为空，则返回false；如果值不为空，则将该值与havingValue指定的值进行比较，如果一样则返回true，否则返回false。如果返回值为false，则该configuration不生效；为true则生效。

```java
// 匹配属性rocketmq.producer.enabled值是否为true，完全匹配后创建RocketMQProducer对象
@ConditionalOnProperty(value = "rocketmq.producer.enabled", havingValue = "true", matchIfMissing = true)
@Bean
public RocketMQProducer mqProducer() {
    return new RocketMQProducer();
}
```

```java
@Data
@ConditionalOnProperty("rocketmq.consumer")
public class RocketMQConsumerProperties extends RocketMQProperties {

    private boolean enabled = true;

    private String consumerGroup;

    private MessageModel messageModel = MessageModel.CLUSTERING;

    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

    private int consumeThreadMin = 20;

    private int consumeThreadMax = 64;

    private int consumeConcurrentlyMaxSpan = 2000;

    private int pullThresholdForQueue = 1000;

    private int pullInterval = 0;

    private int consumeMessageBatchMaxSize = 1;

    private int pullBatchSize = 32;
}
```

#### 2.5.8. @ConditionalOnExpression

组合 `@Conditional` 注解，当 SpEL 表达式为 true 时才开启配置。

```java
@Configuration
@ConditionalOnExpression("${enabled:false}")
public class SpringBootConfigurationDemo {
    @Bean
    public OrderMessageMonitor orderMessageMonitor(ConfigContext configContext) {
        return new OrderMessageMonitor(configContext);
    }
}
```

#### 2.5.9. @ConditionalOnJava

组合 `@Conditional` 注解，当运行的 Java JVM 在指定的版本范围时才开启配置。

#### 2.5.10. @ConditionalOnResource

组合 `@Conditional` 注解，当类路径下有指定的资源才开启配置。

```java
@Bean
@ConditionalOnResource(resources="classpath:shiro.ini")
protected Realm iniClasspathRealm(){
    return new Realm();
}
```

#### 2.5.11. @ConditionalOnJndi

组合 `@Conditional` 注解，当指定的 JNDI 存在时才开启配置。

#### 2.5.12. @ConditionalOnCloudPlatform

组合 `@Conditional` 注解，当指定的云平台激活时才开启配置。

#### 2.5.13. @ConditionalOnSingleCandidate

组合 `@Conditional` 注解，当指定的 class 在容器中只有一个 Bean，或者同时有多个但为首选时才开启配置。


### 2.6. @ConfigurationProperties

用来加载额外的配置（如 `*.properties` 文件），可用在 `@Configuration` 注解类，或者 `@Bean` 注解方法上面，即使用注解的方式将自定义的properties文件映射到实体bean中

### 2.7. @EnableConfigurationProperties

一般要配合 `@ConfigurationProperties` 注解使用，用来开启对 `@ConfigurationProperties` 注解配置 Bean 的支持。

当 `@EnableConfigurationProperties` 注解应用到你的 `@Configuration` 时，任何被 `@ConfigurationProperties` 注解的beans将自动被Environment属性配置。这种风格的配置特别适合与SpringApplication的外部YAML配置进行配合使用。

```java
@Configuration
@EnableConfigurationProperties({
        RocketMQProducerProperties.class,
        RocketMQConsumerProperties.class,
})
@AutoConfigureOrder
public class RocketMQAutoConfiguration {
    @Value("${spring.application.name}")
    private String applicationName;
}
```

### 2.8. @AutoConfigureAfter

用在自动配置类上面，表示该自动配置类需要在另外指定的自动配置类配置完之后。如 Mybatis 的自动配置类，需要在数据源自动配置类之后。

```java
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisAutoConfiguration {
}
```

### 2.9. @AutoConfigureBefore

这个和 `@AutoConfigureAfter` 注解使用相反，表示该自动配置类需要在另外指定的自动配置类配置之前。

### 2.10. @AutoConfigureOrder

Spring Boot 1.3.0中有一个新的注解@AutoConfigureOrder，用于确定配置加载的优先级顺序。

```java
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE) // 自动配置里面的最高优先级
@Configuration
@ConditionalOnWebApplication // 仅限于web应用
@Import(ReactiveWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class) // 导入内置容器的设置
public class EmbeddedServletContainerAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class})
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class, search = SearchStrategy.CURRENT)
    public static class EmbeddedTomcat {
        // ...
    }

    @Configuration
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class, search = SearchStrategy.CURRENT)
    public static class EmbeddedJetty {
        // ...
    }
}
```
