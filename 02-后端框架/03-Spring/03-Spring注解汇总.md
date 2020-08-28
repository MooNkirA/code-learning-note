# Spring 注解开发使用详解汇总

> 注：注解的源码注释参考项目 https://github.com/MooNkirA/spring-note/tree/master/Spring-Framework

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

## 2. IOC的常用注解 - 用于注解驱动的注解

### 2.1. @Configuration

#### 2.1.1. 作用

它是在spring3.0版本之后加入的。此注解是Spring支持注解驱动开发的一个标志。表示当前类是Spring的一个配置类，作用是替代传统主Spring的`applicationContext.xml`配置文件。

从它的源码可以看出，其本质就是`@Component`注解，被此注解修饰的类，也会被存入spring的ioc容器。

#### 2.1.2. 相关属性

- `value`：用于存入spring的Ioc容器中Bean的id

#### 2.1.3. 使用场景

在注解驱动开发时，用于编写配置的类，通常可以使用此注解。一般情况下，配置也会分为主从配置，`@Configuration`一般出现在主配置类上。

例如，在上面快递入门案例中的`SpringConfiguration`类上。值得注意的是，构建ioc容器（`AnnotationConfigApplicationContext`）使用的是传入字节码的构造函数，此注解可以省略。

```java
ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

// @Configuration /* 标识当前类为配置类 */
@Import(JdbcConfig.class)
@PropertySource("classpath:jdbc.properties")
public class SpringConfiguration {
}
```

但是如果使用基础包扫描的构造函数创建`AnnotationConfigApplicationContext`，则配置类中的`@Configuration `注解则不能省略。

```java
ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample");

@Configuration /* 标识当前类为配置类 */
@Import(JdbcConfig.class) /* @Import注解是写在类上的，通常是和注解驱动的配置类一起使用的。其作用是引入其他的配置类 */
@PropertySource("classpath:jdbc.properties") /* 用于指定读取资源文件的位置。不仅支持properties，也支持xml文件 */
public class SpringConfiguration {
}
```

#### 2.1.4. 示例

- 创建配置类

```java
package com.moon.springsample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * spring项目的配置类，用于代替传统的xml配置文件
 * <p>没有applicationContext.xml，就没法在xml中配置spring创建容器要扫描的包了。</p>
 * <p>那么可以创建一些类，通过注解配置到ioc容器中也无法实现了。此时就可以使用此注解来代替spring的配置文件。</p>
 */
@Configuration("springConfiguration") /* 标识当前类为配置类 */
@ComponentScan("com.moon.springsample") /* 配置开启包扫描 */
// @Import(Xxxxx.class) /* 通过@Import注解导入其他的配置类 */
// @PropertySource("classpath:xxxx.properties") /* 通过@PropertySource注解导入配置文件，如.properties、.xml等 */
public class SpringConfiguration {
}
```

- 测试

```java
package com.moon.springannotation.test;

import com.moon.springsample.config.SpringConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * `@Configuration` 注解使用测试
 *
 * @author MooNkirA
 * @version 1.0
 * @date 2020-7-31 23:40
 * @description
 */
public class ConfigurationTest {
    /* Configuratio注解使用测试 */
    public static void main(String[] args) {
        // 方式一：1. 获取基于注解的spinrg容器，使用基础包basePackages的构造函数创建=容器，此时SpringConfiguration类上必须加上@Configuration注解
        // ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample");
        // 方式二：1. 获取基于注解的spinrg容器，使用传入字节码的构造函数创建容器，此时SpringConfiguration类上可以不加@Configuration注解
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        // 2. 根据id或者类型去获取对应的bean实例
        SpringConfiguration springConfiguration = (SpringConfiguration) context.getBean("springConfiguration");
        System.out.println(springConfiguration);
    }
}
```

### 2.2. @ComponentScan

#### 2.2.1. 作用与使用场景

用于指定创建容器时要扫描的包。该注解在指定扫描的位置时，可以指定包名，也可以指定扫描的类。同时支持定义扫描规则，例如包含哪些或者排除哪些。同时，它还支持自定义Bean的命名规则

在注解驱动开发时，编写的类都使用注解的方式进行配置，但想让加上相关spring注解(如：`@Controller`、`@Service`、`@Repository`、`@Component`)的类添加到spring的ioc容器中，就需要使用`@ComponentScan`注解来实现组件的扫描。

<font color=red>**注意：在spring4.3版本之后还加入了一个`@ComponentScans`的注解，该注解相当于支持配置多个`@ComponentScan`**</font>

#### 2.2.2. 相关属性

- `value`：用于指定要扫描的包。当指定了包的名称之后，spring会扫描指定的包及其子包下的所有类
- `basePackages`：与value作用一样
- `basePackageClasses`：指定具体要扫描的类的字节码，spring会扫描指定字节码的类所在的包及其子包下的所有类。
- `nameGenrator`：指定扫描bean对象存入容器时的命名规则。详情参考《Spring源码分析》的BeanNameGenerator及其实现类。
- `scopeResolver`：用于处理并转换检测到的Bean的作用范围。
- `soperdProxy`：用于指定bean生成时的代理方式。默认是Default，则不使用代理，可选值有四个：`DEFAULT`，`NO`，`INTERFACES`，`TARGET_CLASS`。详情请可查看spring源码的ScopedProxyMode枚举。
- `resourcePattern`：用于指定符合组件检测条件的类文件，默认是包扫描下的`**/*.class`
- `useDefaultFilters`：是否对带有@Component @Repository @Service @Controller注解的类开启检测，默认是开启的。
- `includeFilters`：自定义组件扫描的过滤规则，用于扫描组件。注解的是`Filter`注解数组，`Filter`的`type`属性是`FilterType`的枚举，有5种类型：
    - `ANNOTATION`：注解类型（默认）
    - `ASSIGNABLE_TYPE`：指定固定类
    - `ASPECTJ`：ASPECTJ类型
    - `REGEX`：正则表达式
    - `CUSTOM`：自定义类型
- `excludeFilters`：自定义组件扫描的排除规则。
- `lazyInit`：组件扫描时是否采用懒加载 ，默认不开启。

#### 2.2.3. 包扫描配置示例

##### 2.2.3.1. 不指定扫描包的使用

- 创建配置类

```java
@Configuration /* 标识当前类为配置类 */
@ComponentScan /* 配置开启包扫描，不写扫描的包路径，则默认扫描当前@ComponentScan注解的类所在的包及其下的所有子包 */
public class SpringConfiguration {
}
```

- 测试代码

```java
@Test
public void componentScanBaseTest() {
    // 1. 获取基于注解的spinrg容器，使用传入字节码的构造函数创建容器。（这里故意不使用传入基础包的构造函数，如果这里配置了扫描包包含了测试层的位置，则看不出效果）
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    // 2. 根据id或者类型去获取对应的bean实例
    UserService userService = context.getBean("userService", UserService.class);
    // 3. 调用对象方法
    userService.saveUser();
}
```

测试结果，`@ComponentScan`不指定扫描包，只会扫描当前配置类所在的包及其下的所有子包

![](images/20200805234044677_8448.png)

##### 2.2.3.2. value 与 basePackages 属性

- 创建简单模拟的业务层代码

```java
package com.moon.springsample.service;

public interface UserService {
    /* 模拟保存用户 */
    void saveUser();
}
```

```java
package com.moon.springsample.service.impl;

import com.moon.springsample.service.UserService;
import org.springframework.stereotype.Service;

@Service("userService") /* 配置当前类交给spring ioc容器管理，其中value为对象在容器中的名称 */
public class UserServiceImpl implements UserService {
    @Override
    public void saveUser() {
        System.out.println("成功保存用户");
    }
}
```

- 创建配置类，使用`@ComponentScan`注解

```java
package com.moon.springsample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * spring项目的配置类
 */
@Configuration /* 标识当前类为配置类 */
// @ComponentScan("com.moon.springsample") /* 配置开启包扫描，配置value属性，如果没有配置，则可以省略不写value="xxxx" */
@ComponentScan(basePackages = {"com.moon.springsample"}) /* 配置开启包扫描，配置basePackages属性，效果与value一样，但不能与value属性同时存在 */
public class SpringConfiguration {
}
```

- 测试

```java
@Test
public void componentScanBasePackagesTest() {
    // 1. 获取基于注解的spinrg容器，使用传入字节码的构造函数创建容器。（这里故意不使用传入基础包的构造函数，如果这里配置了扫描包包含了测试层的位置，则看不出效果）
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    // 2. 根据id或者类型去获取对应的bean实例
    UserService userService = context.getBean("userService", UserService.class);
    // 3. 调用对象方法
    userService.saveUser();
}
```

##### 2.2.3.3. basePackageClasses 属性

- 配置`basePackageClasses`属性，指定扫描类的字节码

```java
@Configuration /* 标识当前类为配置类 */
@ComponentScan(basePackageClasses = UserService.class) /* 配置开启包扫描，指定具体要扫描的类的字节码，spring会扫描指定字节码的类所在的包及其子包下的所有类。 */
public class SpringConfiguration {
}
```

- 测试

```java
@Test
public void componentScanBasePackageClassesTest() {
    // 1. 获取基于注解的spinrg容器，使用基础包的构造函数，只扫描配置类所在的包。
    ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample.config");
    // 2. 根据id或者类型去获取对应的bean实例
    UserService userService = context.getBean("userService", UserService.class);
    // 3. 调用对象方法
    userService.saveUser();
    // 使用basePackageClasses方法扫描，测试指定字节码类所在的包及其子包所有的类是否被扫描到
    AccountService accoutService = context.getBean("accountService", AccountService.class);
    accoutService.deleteAccount();
}
```

测试结果：扫描到UserService所在的包及其子包

![](images/20200809001235062_3991.png)

#### 2.2.4. 自定义BeanNameGenerator生成规则

##### 2.2.4.1. nameGenrator属性

通过查看`@ComponentScan`注解的源码，有`nameGenrator`属性，用来定义bean在spring容器中的名称。属性的值是一个`BeanNameGenerator`接口，spring有默认实现的生成名称，其实现类为`AnnotationBeanNameGenerator`。

其中`AnnotationBeanNameGenerator`实现的逻辑主要通过类上的注解元数据，在获取注解中的value值，如果value有值，则以value的值为baen的名称。如果value没有值，则将获取类名，将首字母转成小写，用作bean的名称

> 注：在《Spring源码分析》中有`BeanNameGenerator`的详细介绍

##### 2.2.4.2. 自定义beanName生成规则示例

- 创建自定义beanName生成规则类`com.moon.springsample.custom.CustomBeanNameGenerator`，实现`BeanNameGenerator`接口。里面的逻辑可以参考源码

```java
package com.moon.springsample.custom;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.util.Map;
import java.util.Set;

/**
 * 自定义BeanName生成规则实现类，需实现spring框架的BeanNameGenerator接口
 */
public class CustomBeanNameGenerator implements BeanNameGenerator {

    /* Component注解的全类名 */
    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

    /* 自定义beanName前缀 */
    private static final String NAME_PREFIX = "MooN_";

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        // 0. 定义返回的beanName
        String beanName = null;

        // 1. 判断当前BeanDefinition对象是否为注解
        if (definition instanceof AnnotatedBeanDefinition) {
            // 2. 将BeanDefinition对象强转成注解的BeanDefinition对象
            AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) definition;
            // 3. 通过BeanDefinition对象获取注解的元信息（AnnotationMetadata）
            AnnotationMetadata amd = abd.getMetadata();
            // 4. 获取元信息中所有注解的Set集合
            Set<String> annotationTypes = amd.getAnnotationTypes();
            // 5. 遍历AnnotationType的集合
            for (String type : annotationTypes) {
                // 6. 获取注解的属性，（AnnotationAttributes是继承LinkedHashMap）
                AnnotationAttributes attributes = AnnotationAttributes
                        .fromMap(amd.getAnnotationAttributes(type, false));
                // 7. 判断注解属性attributes的是否为空，并且必须是@Component注解或者其衍生注解
                if (attributes != null && isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
                    // 8. 获取注解中value属性的值
                    Object value = attributes.get("value");
                    if (value instanceof String) {
                        // 9. 如果是字符串类型，强转
                        String strVal = (String) value;
                        // 10. 判断value值是否为空
                        if (StringUtils.hasLength(strVal)) {
                            // 11. 如果beanName为空，并且与注解中的value值不相同，抛出异常
                            if (beanName != null && !strVal.equals(beanName)) {
                                throw new IllegalStateException("Stereotype annotations suggest inconsistent " +
                                        "component names: '" + beanName + "' versus '" + strVal + "'");
                            }
                            // 因为是直接参考spring的实现，为了看到自定义beanName生成规则的效果，这里加上前缀，以作区分
                            beanName = NAME_PREFIX + strVal;
                        }
                    }
                }
            }
        }
        // 这里生成默认的名称也加上前缀，以作区分
        return beanName == null ? NAME_PREFIX + buildDefaultBeanName(definition) : beanName;
    }

    /**
     * 用于判断注解是否为@Component注解或者其衍生注解
     */
    private boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes,
                                              @Nullable Map<String, Object> attributes) {

        boolean isStereotype = annotationType.equals(COMPONENT_ANNOTATION_CLASSNAME) ||
                metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) ||
                annotationType.equals("javax.annotation.ManagedBean") ||
                annotationType.equals("javax.inject.Named");

        return (isStereotype && attributes != null && attributes.containsKey("value"));
    }

    /**
     * 创建一个默认的beanName(spring的原方法)
     */
    private String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return Introspector.decapitalize(shortClassName);
    }
}
```

- 在配置类中的`@ComponentScan`注解，加入`nameGenrator`属性，值为自定义的规则实现类`CustomBeanNameGenerator`

```java
@Configuration
/* 配置bean对象存入容器时自定义的命名规则 */
@ComponentScan(basePackages = {"com.moon.springsample"}, nameGenerator = CustomBeanNameGenerator.class)
public class SpringConfiguration {
}
```

- 测试代码与结果

```java
 @Test
public void componentScanNameGeneratorTest() {
    // 1. 获取基于注解的spinrg容器，使用基础包的构造函数，只扫描配置类所在的包。
    ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample.config");
    // 2. 根据id去获取对应的bean实例，因为是自定义baeanName命名规则，所以需要使用自定义的beanName才能获取到相应的spring容器的对象
    UserService userService = context.getBean("MooN_userService", UserService.class);
    // 3. 调用对象方法
    userService.saveUser();
    // 使用spring架构默认的命名规则的名称去获取容器中的实例，报错！
    AccountService accoutService = context.getBean("accountService", AccountService.class);
    accoutService.deleteAccount();
}
```

![](images/20200811230734059_29542.png)

#### 2.2.5. resourcePattern 属性配置扫描规则

`resourcePattern` 属性的默认值是包扫描下的` **/*.class`。可以通过该属性修改包扫描的规则

```java
@Configuration
@ComponentScan(basePackages = {"com.moon.springsample"}, resourcePattern = "*/*.class")
public class SpringConfiguration {
}
```

```java
/* 测试resourcePattern属性 */
@Test
public void componentScanNameResourcePatternTest() {
    // 1. 获取基于注解的spinrg容器，使用基础包的构造函数，只扫描配置类所在的包。
    ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample.config");
    // 2. 因为是配置了resourcePattern属性为"*/*.class"，所以扫描"com.moon.springsample"下任意包的任意class文件，所以扫描不到service包下的impl包中的注解
    UserService userService = context.getBean("userService", UserService.class);
    // 3. 因为没有扫描到实现类的注解，所有无法加入到spring容器中，对象为null，调用对象方法时报错
    userService.saveUser();
}
```

![](images/20200824232433304_30206.png)

#### 2.2.6. 自定义组件扫描过滤规则

##### 2.2.6.1. 基础使用

- `includeFilters`：指定包含的过滤规则，不会影响spring扫描其他规则
- `excludeFilters`：指定排除的过滤规则，指定后该扫描规则会被过滤，不会被扫描加入spring容器

```java
/* includeFilters用于指定自定义组件扫描的过滤规则，表示包含某些规则，不会排除其他的规则 */
// @ComponentScan(basePackages = {"com.moon.springsample"}, includeFilters = @ComponentScan.Filter(value = Service.class))
/* excludeFilters用于指定组件扫描的排除规则，排除后不会加入到spring容器 */
@ComponentScan(basePackages = {"com.moon.springsample"}, excludeFilters = @ComponentScan.Filter(value = Service.class))
public class SpringConfiguration {
}
```

测试

```java
/* 测试includeFilters、excludeFilters属性 */
@Test
public void componentScanFiltersTest() {
    // 1. 获取基于注解的spinrg容器，使用基础包的构造函数，只扫描配置类所在的包。
    ApplicationContext context = new AnnotationConfigApplicationContext("com.moon.springsample.config");
    /*
     *  配置includeFilters属性为@ComponentScan.Filter(value = Service.class)，
     *      代表过滤规则包含@Service注解，不会影响其他注解的扫描，如LogUtil类上的@Component注解
     *  配置excludeFilters属性为@ComponentScan.Filter(value = Service.class)，
     *      代表过滤规则会排除@Service注解，有该注解的类不会被扫描也不会加入到spring容器，如UserService类上的@Service注解
     */
    UserService userService = context.getBean("userService", UserService.class);
    LogUtil logUtil = context.getBean("logUtil", LogUtil.class);
    // 3. 如果配置excludeFilters排除@Service注解后，执行程序会报[No bean named 'userService' available]的错误
    userService.saveUser();
    logUtil.printLog();
}
```

![](images/20200825231904111_18299.png)

![](images/20200825232151925_20711.png)

##### 2.2.6.2. FilterType枚举

```java
public enum FilterType {
	/**
	 * 过滤标记指定注解类型的对象 (默认)
	 */
	ANNOTATION,

	/**
	 * 过滤指定固定类
	 */
	ASSIGNABLE_TYPE,

	/**
	 * 过滤 ASPECTJ 类型
	 */
	ASPECTJ,

	/**
	 * 过滤正则表达式匹配的类
	 */
	REGEX,

	/**
	 * 过滤自定义类型
	 */
	CUSTOM
}
```

##### 2.2.6.3. TypeFilter接口

TypeFilter接口，自定义过滤器必须实现的基础接口

```java
/* Spring 过滤器必须实现的基础接口 */
@FunctionalInterface
public interface TypeFilter {
	/**
	 * Determine whether this filter matches for the class described by
	 * the given metadata.
	 * @param metadataReader the metadata reader for the target class
	 * @param metadataReaderFactory a factory for obtaining metadata readers
	 * for other classes (such as superclasses and interfaces)
	 * @return whether this filter matches
	 * @throws IOException in case of I/O failure when reading metadata
	 *
	 * 此方法是用于判断过滤器是否与目标类匹配，返回值是boolean类型。
	 * 		true: 表示该类加入到spring的容器中
	 * 		false: 表示该类不加入容器
	 * 	@param metadataReader 元数据读取器，读取到的当前正在扫描的类的信息
	 * 	@param metadataReaderFactory 元数据读取器的工厂，可以获得到其他任何类的信息
	 */
	boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException;
}
```

##### 2.2.6.4. Spring提供的过滤规则 - AnnotationTypeFilter

spring框架本身就提供了一些过滤规则的实现，比如`AnnotationTypeFilter`，用于筛选指定的标识了指定类型注解的类

当在项目开发中，spring提供的容器分为`RootApplicationContext`和`ServletApplicationContext`。此时如果不希望`RootApplicationContext`容器创建时把Controller层的类加入到容器中，就可以使用过滤规则排除`@Controller`注解配置的Bean对象。

```java
@Configuration
@ComponentScan(value = {"com.moon.springsample"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class}))
public class SpringConfiguration {
}
```

##### 2.2.6.5. 自定义过滤器案例模拟场景分析

在实际开发中，有很多下面这种业务场景：一个业务需求根据环境的不同，可能要加载不同的实现。比如以下案例：

> - 在店欢活动中，会员用户下单优惠50元，平台提成15%；普通用户下单优惠10元，平台提成8%
> - 平时正常的营业，会员用户下单优惠10元，平台提成9%；普通用户下单优惠0元，平台提成2%；

此时应该考虑采用桥接设计模式，把将涉及到场景差异的模块功能单独抽取到代表场景功能的接口中。针对不同场景进行实现。并且在扫描组件注册到容器中时，采用哪个场景的具体实现，应该采用配置文件配置起来。而自定义TypeFilter就可以实现注册指定场景的组件到spring容器中。

##### 2.2.6.6. 相关代码准备

- **定义场景的注解**

```java
package com.moon.springsample.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识不同场景的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scene {
    /**
     * 用于指定场景的名称
     */
    String value() default "";
}
```

- **定义功能的接口，即案例中的下单优惠、平台提成功能**

```java
/**
 * 用户下单业务接口
 */
public interface OrderService {
    /**
     * 计算下单优惠的金额
     * @param userType 用户的类型
     */
    void calcOrderDiscount(String userType);
}

/**
 * 运营平台的业务接口
 */
public interface PlatformService {
    /**
     * 计算运营平台提成的数量
     * @param userType 用户的类型
     */
    void calcSalePercentage(String userType);
}
```

- **分不同场景，编写不同实现类**

```java
/**
 * 周年欢活动场景，订单业务实现类
 */
@Service("orderService")
@Scene("anniversary") // 使用自定义注解，标识当前是哪种场景的实现
public class AnniversaryOrderImpl implements OrderService {
    @Override
    public void calcOrderDiscount(String userType) {
        // 判断用户类型
        if ("member".equalsIgnoreCase(userType)) {
            System.out.println("周年庆活动，会员用户下单优惠50元");
        } else if ("normal".equalsIgnoreCase(userType)) {
            System.out.println("周年庆活动，普通用户下单优惠10元");
        }
    }
}

/**
 * 周年欢场景 运营平台业务实现类
 */
@Service("platformService")
@Scene("anniversary") // 使用自定义注解，标识当前是哪种场景的实现
public class AnniversaryPlatformImpl implements PlatformService {

    @Override
    public void calcSalePercentage(String userType) {
        if ("member".equalsIgnoreCase(userType)) {
            System.out.println("周年庆活动，会员用户下单平台提成15%");
        } else if ("normal".equalsIgnoreCase(userType)) {
            System.out.println("周年庆活动，普通用户下单平台提成8%");
        }
    }
}

/**
 * 正常营业场景，订单业务实现类
 */
@Service("orderService")
@Scene("normal") // 使用自定义注解，标识当前是哪种场景的实现
public class NormalOrderImpl implements OrderService {
    @Override
    public void calcOrderDiscount(String userType) {
        if ("member".equalsIgnoreCase(userType)) {
            System.out.println("正常营业，会员用户下单优惠10元");
        } else if ("normal".equalsIgnoreCase(userType)) {
            System.out.println("正常营业，普通用户下单无优惠");
        }
    }
}

/**
 * 正常营业场景，运营平台业务实现类
 */
@Service("platformService")
@Scene("normal") // 使用自定义注解，标识当前是哪种场景的实现
public class NormalPlatformImpl implements PlatformService {
    @Override
    public void calcSalePercentage(String userType) {
        if ("member".equalsIgnoreCase(userType)) {
            System.out.println("正常营业，会员用户下单平台提成9%");
        } else if ("normal".equalsIgnoreCase(userType)) {
            System.out.println("正常营业，普通用户下单平台提成2%");
        }
    }
}
```

##### 2.2.6.7. 不使用过滤器测试

- **编写项目的配置类**

```java
@Configuration
@ComponentScan(value = {"com.moon.springsample"})
public class SpringConfiguration {
}
```

- **如果不配置使用自定义过滤器扫描，区分加载不同场景的实现。此时会出现实现类名称相同的错误**

```java
@Test
public void noTypeFiltertest() {
    // 1. 传入项目配置类字节码方式，创建基于注解的spinrg容器
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    // 2. 根据bean名称，从容器中获取订单与平台业务实现类，并调用方法
    OrderService orderService = context.getBean("orderService", OrderService.class);
    orderService.calcOrderDiscount("member");

    PlatformService platformService = context.getBean("platformService", PlatformService.class);
    platformService.calcSalePercentage("normal");
}
```

![](images/20200828000712072_18714.png)

##### 2.2.6.8. 使用自定义过滤器实现不同场景不同实现（重点）

- **编写自定义扫描过滤规则**。可以通过实现顶级接口`TypeFilter`，但也可以选择继承其他的抽象类（如`AbstractTypeHierarchyTraversingFilter`），因为抽象类已经实现部分逻辑，这样减少一些代码的编写

```java
package com.moon.springsample.typefilter;

import com.moon.springsample.annotations.Scene;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;

import java.util.Properties;

/**
 * spring的自定义扫描规则 - 实现不同场景下不同实现
 */
public class SceneTypeFilter extends AbstractTypeHierarchyTraversingFilter {

    // 定义路径校验类对象（Spring框架提供的工具类）
    private PathMatcher pathMatcher;

    /*
     *  定义场景名称，此值应该是通过配置去修改，不能使用硬编码方式
     *      需要注意：这里使用 @Value 注解的方式是获取不到配置值，
     *      因为Spring的生命周期里，负责填充属性值的 InstantiationAwareBeanPostProcessor 与 TypeFilter 的实例化过程两者没有任何关系
     */
    // @Value("${common.scene.name}")
    private String sceneName;

    /**
     * 定义构造函数，因为父类没有无参构造函数，所以必须要定义构造函数并调用父类的构造器
     */
    public SceneTypeFilter() {
        /*
         * 调用父类构造函数
         *  第1个参数considerInherited: 不考虑基类
         *  第2个参数considerInterfaces: 不考虑接口上的信息
         */
        super(false, false);
        // 借助Spring默认的Resource通配符路径方式
        this.pathMatcher = new AntPathMatcher();
        // 此处使用硬编码读取配置信息（实现应用时再想使用其他方式实现）
        try {
            // 使用spring工具类PropertiesLoaderUtils，读取根目录下的配置文件
            Properties properties = PropertiesLoaderUtils.loadAllProperties("scene.properties");
            // 读取配置文件中的场景名称
            this.sceneName = properties.getProperty("common.scene.name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写父类方法，注意此方法的作用是，将指定的类将注册为Exclude（排除过滤）, 返回
     *
     * @param className 校验的类的名称
     * @return true 代表排除此类，不加入到 spring 容器中；false 代表不排除
     */
    @Override
    protected boolean matchClassName(String className) {
        try {
            /*
             * 判断当前传入的类的名称（className）是否在指定的包路径上的类（即只处理本案例中相关的不场景的业务类）
             */
            if (!isPotentialPackageClass(className)) {
                // 类路径不符合本过滤器定义的扫描路径规则，即表示此类不需要排除，直接返回false
                return false;
            }
            /* 以上逻辑是：判断当前类上标识的自定义场景注解是否与配置文件中的场景名称一致，如不一致，则排除，不能注册到spring容器中 */
            // 根据类名称获取字节码对象
            Class<?> clazz = ClassUtils.forName(className, SceneTypeFilter.class.getClassLoader());
            // 通过反射获取当前类上的 @Scene 注解对象
            Scene scene = clazz.getAnnotation(Scene.class);
            // 判断此类上是否有 @Scene 注解
            if (scene == null) {
                // 无标识此注解，不需要排除
                return false;
            }
            // 获取标识 @Scene 注解中的value值
            String sceneValue = scene.value();
            // 校验，如果此类上标识的value属性值与配置文件中场景名称一致，则注册到spring ioc 容器中（即返回false）。排除则返回true
            return !this.sceneName.equalsIgnoreCase(sceneValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /*
     * 定义潜在的满足条件的类的名称（本类逻辑能处理的类）, 指定在哪个 package下
     *  这里为了实现可以支持通配符方式的，借用spring框架中 ClassUtils 工具类
     */
    private static final String PATTERN_STANDARD =
            ClassUtils.convertClassNameToResourcePath("com.moon.springsample.service.impl.*.*");

    /**
     * 判断当前传入的类名称，是否在本类（本过滤规则）逻辑中可以处理的类
     *
     * @param className 待判断类的名称
     * @return true: 表示可以处理
     */
    private boolean isPotentialPackageClass(String className) {
        // 1. 将类名转换为资源路径, 以进行匹配测试
        String resourcePath = ClassUtils.convertClassNameToResourcePath(className);
        // 2. 使用工具类对资源的路径进行匹配校验
        return pathMatcher.match(PATTERN_STANDARD, resourcePath);
    }
}
```

- **编写properties配置文件，指定场景**

```properties
# resources\scene.properties
# 配置当前使用的场景
common.scene.name=anniversary
```

- **修改配置类，增加排除规则**

```java
package com.moon.springsample.config;

import com.moon.springsample.typefilter.SceneTypeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * spring项目的配置类
 */
@Configuration
// 添加排除属性excludeFilters，选择自定义规则（FilterType.CUSTOM），指定自定义过滤器的class
@ComponentScan(value = {"com.moon.springsample"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = SceneTypeFilter.class))
public class SpringConfiguration {
}
```

- 再次运行上面的测试方法，结果成功只执行配置文件中的场景实现

![](images/20200829001505989_13028.png)

### 2.3. @Bean

#### 2.3.1. 源码


































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

