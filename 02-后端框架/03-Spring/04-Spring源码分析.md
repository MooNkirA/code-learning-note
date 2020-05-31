# Spring 源码分析

## 1. Spring 源码分析准备工作

> 注：下载spring源码并写注释，里面会标识相应方法的重要程度：1~5。
>
> - 0：不重要，可以不看
> - 1：一般重要，可看可不看
> - 5：非常重要，一定要看

### 1.1. Spring 源码下载

1. 到github下载源码
    - 源码下载地址：https://github.com/spring-projects/spring-framework
    - 国内镜像：https://gitee.com/mirrors/spring-framework
2. 下载 gradle，需要 JDK8 及以上的版本
3. 到下载的 spring 源码路径执行 gradle 命令，`gradlew :spring-oxm:compileTestJava`
4. 用 idea 打开 spring 源码工程，在 idea 中安装插件 kotlin，重启 idea
5. 把编译好的源码导入到工程中，这样可以在源码中写注释并且断点调试源码了。

### 1.2. 把源码导入到工程

1. 选择源码测试工程所依赖的spring相应的版本，右键选择【Open Library Settings】

![导入源码步骤1](images/20191215155948627_17845.png)

2. 选择Libraries里的spring源码包，在Classes、Sources、Annotations中增加编译好的源码

![导入源码步骤2](images/20191215160339293_2908.png)


3. 选择Classes

![导入源码步骤3](images/20191215181733460_8930.png)

4. 选择Sources

![导入源码步骤4](images/20191215181830565_31571.png)


### 1.3. 创建 Spring 示例项目

- 创建maven项目，修改pom.xml导入 spring 依赖。其中 spring 中最核心的4个jar如下
    - spring-beans
    - spring-core
    - spring-context
    - spring-expression
- 一个最简单的 spring 工程，理论上就只需要依赖一个 spring-context 就足够了

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <artifactId>mz-system-learning</artifactId>
        <groupId>com.moon</groupId>
        <version>0.3.1</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>mz-learning-springsource</artifactId>
    <packaging>war</packaging>
    <name>${project.artifactId}</name>
    <description>Spring Framework 源码学习案例工程</description>

    <!-- 版本号管理 -->
    <properties>
        <spring.version>5.1.3.RELEASE</spring.version>
        <junit.version>4.12</junit.version>
    </properties>

    <dependencies>
        <!--
            spring框架最核心的依赖，一个最基本的spring项目只需要引入此依赖即可
                此依赖包括：spring-context,spring-aop,spring-beans,spring-core,spring-expression
        -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
    </dependencies>

</project>
```

> spring-context 包本身就依赖了，spring-aop，spring-beans，spring-core 等模块jar包

- 一个空的 spring 工程是不能打印日志的，要导入 spring 依赖的日志 jar 包

```xml
<!-- spring 框架输出日志的依赖包 -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>LATEST</version>
</dependency>
```

## 2. Spring 基础使用
### 2.1. spring 配置文件中xsd文件引入

XSD 是编写 xml 文件的一种规范，有了这个规范才能校验当前 xml 文件是否准确，在 spring 中同样有 XSD 规范。


### 2.2. spring 容器加载方式
#### 2.2.1. ClassPathXmlApplicationContext(类路径获取配置文件上下文对象)

比较常用的上下文对象，用于启动时读取上下文对象

```java
/* 类路径获取配置文件上下文对象（ClassPathXmlApplicationContext） */
@Test
public void ClassPathXmlApplicationContextTest() {
    // 读取spring类路径下的配置文件
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
    Student student = (Student) applicationContext.getBean("student");
    System.out.println(student.getUserName());
}
```

#### 2.2.2. FileSystemXmlApplicationContext(文件系统路径【绝对路径】获取配置文件上下文对象)

此上下文对象很少使用，一般都使用类路径读取配置文件的上下文对象

```java
/* 文件系统路径获取配置文件【绝对路径】上下文对象（FileSystemXmlApplicationContext）【基本上不用】 */
@Test
public void FileSystemXmlApplicationContextTest() {
    // 读取spring的配置文件，需要绝对路径
    FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext("D:\\code\\moonzero-system\\mz-system-learning\\mz-learning-springsource\\src\\main\\resources\\spring.xml");
    Student student = (Student) applicationContext.getBean("student");
    System.out.println(student.getUserName());
}
```

#### 2.2.3. AnnotationConfigApplicationContext(无配置文件加载容器上下文对象)

此上下文对象也比较少用，一般在测试用例中使用比较多，因为可以直接扫描指定的包，获取包下所有有spring注解标识的类实例

```java
private static final String BASE_PACKAGE = "com.moon.learningspring";

/* 无配置文件加载容器上下文对象（AnnotationConfigApplicationContext） */
@Test
public void AnnotationConfigApplicationContextTest() {
    // 注解扫描上下文对象
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BASE_PACKAGE);
    Student student = (Student) applicationContext.getBean("student");
    System.out.println(student.getUserName());
}
```

#### 2.2.4. EmbeddedWebApplicationContext(springboot 加载容器)

此上下文对象是spring boot的框架，启动的时候可以创建一个嵌入式的tomcat

```xml
<!-- springboot web 的依赖，用于引入EmbeddedWebApplicationContext类  -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot</artifactId>
    <version>1.5.13.RELEASE</version>
</dependency>
```

```java
/* springboot 加载容器的上下文对象（EmbeddedWebApplicationContext） */
@Test
public void EmbeddedWebApplicationContextTest() {
    // springboot在启动的时候就会用到此上下文对象，启动spring容器，创建一个嵌入式的tomcat
    ApplicationContext applicationContext = (ApplicationContext) new EmbeddedWebApplicationContext();
    Student student = (Student) applicationContext.getBean("student");
    System.out.println(student.getUserName());
}
```

## 3. Spring 框架涉及的设计模式
### 3.1. 设计模式1 - 模板设计模式

在 spring 中大量的使用了模板设计模式，可以说是用得最多的设计模式。

模板设计模式demo代码详见：moonzero-system项目中的mz-learning-springsource模块，`com.moon.learningspring.designPattern.template`包下的demo

**模板设计模式的核心是：创建抽象类或者接口，定义一个主业务方法，而主业务方法有些业务逻辑可以抽象类已经实现，在主业务方法中预留了一些抽象方法，这些抽象方法由子类继承(或实现)的时候实现该方法的业务逻辑，到根据不同的业务场景，使用不同的子类，从而在调用父类主业务方法时，实现不同的（子类）业务逻辑**

> 注：模板类中的主业务可以使用final声明此方法，从而子类不可以重写，只能继承使用。至于其他的抽象方法，子类可以实现自己的业务逻辑，

spring框架中使用模板设计模式案例

![spring 框架使用模板设计模式](images/20191221190743024_24584.png)

### 3.2. 设计模式2 - 委托模式

有两个对象参与处理同一个请求，接受请求的对象将请求委托给另一个对象来处理

### 3.3. 设计模式3 - 装饰模式

- 装饰模式主要分几个元素
    1. 被装饰者：已存在的具体对象，需要被增强的对象
    2. 抽象装饰者：具体对象与装饰对象的共同父接口
    3. 装饰者对象：对具体对象进行功能的增强，进行装饰的类。对方法进行增强。(自定义的类)

装饰者设计模式demo详见：moonzero-system项目中的mz-learning-springsource模块，`com.moon.learningspring.designPattern.decorator`包下的demo

### 3.4. SPI 设计思想

自定义标签的解析就是一个 SPI 设计思想，即通过加装全文配置文件，做到代码灵活的调用。实现步骤如下：

1. 定义一个服务提供接口

```java
package com.moon.learningspring.spi;

/**
 * service provider interface
 * <P>服务提供接口，需要提供一个可配置的服务接口的实现类</P>
 */
public interface SpiService {
    String query(String param);
}
```

2. 编写服务接口的实现类

```java
package com.moon.learningspring.spi;

/**
 * SPI服务接口实现类
 */
public class SpiServiceImpl implements SpiService {
    @Override
    public String query(String param) {
        System.out.println("=======SpiServiceImpl.query()方法执行了======");
        return "OK";
    }
}
```

3. 在 resources 目录下创建 META-INF/services 文件夹，创建文件（文件的名称为服务接口全限定名）

![服务接口文件](images/20200108165533678_12262.png)

![服务接口文件](images/20200108165644374_29698.png)

4. 这样就可以通过这个接口，找到配置在文件中的所有该接口的实现类（可以是多个实现类）。
    - **这种设计的好处是：实现业务代码解耦，扩展性高。**核心的业务不需要再修改，日后增加新的业务需求时，可以通过增加新的实现类与修改配置文件即可
    - 缺点是：粒度不够细，通过配置的方式不能唯一确定一个实现类

```java
package com.moon.learningspring.spi;

import java.util.ServiceLoader;

/**
 * SPI 服务接口测试
 */
public class SpiTest {
    /**
     * 此设计的好处是：实现业务代码解耦，扩展性高。核心的业务不需要再修改，日后增加新的业务需求时，可以通过增加新的实现类与修改配置文件即可
     * 缺点是：粒度不够细，通过配置的方式不能唯一确定一个实现类
     */
    public static void main(String[] args) {
        // 通过jdk的api，ServiceLoader获取配置文件中定义所有实现类实例
        ServiceLoader<SpiService> load = ServiceLoader.load(SpiService.class);
        // 调用实现类的业务方法
        for (SpiService spiService : load) {
            spiService.query("呵呵");
        }
    }
}
```

#### 3.4.1. spring 框架对spi设计的运用

spring 中自定义标签的解析就是这种 SPI 设计的运用，在自定义标签中解析的过程中，spring 会去加载 META-INF/spring.handlers 文件，然后建立映射关系，程序在解析标签头的时候，如：`<context:>`这种的标签头。会拿到一个 namespaceUri，然后再从映射关系中找到这个 namespaceUri 所对应的处理类

![spi设计思想运用](images/20200109133154692_23233.png)

#### 3.4.2. 扩展：dubbo对spi的优化(有时间研究)

dubbo在spi的配置文件中，设置为key-value的形式，这样在xml配置文件中配置相关属性，就可以唯一的确认一个实现类。

## 4. Spring 框架解析xml文件流程
### 4.1. 解析xml文件入口

此次分析源码如何解析xml文件的的入口选择了比较常用的`ClassPathXmlApplicationContext`类，点击查看此类的构造方法

1. 此方法先调用父类的构造方法
2. 再创建解析器，解析configLocations属性
3. **调用父类核心方法`refresh()`，该方法是spring容器初始化的核心方法。是spring容器初始化的核心流程，spring容器要加载必须执行该方法**

```java
public ClassPathXmlApplicationContext(
		String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
		throws BeansException {

	// 调用父类的构造方法
	super(parent);
	// 创建解析器，解析configLocations
	setConfigLocations(configLocations);
	// 是否自己刷新spring context
	if (refresh) {
		// 调用父类AbstractApplicationContext的refresh()的方法，是核心方法
		refresh();
	}
}
```

### 4.2. 解析xml文件流程

1. 通过构造函数，创建对应的上下文对象。调用父类AbstractApplicationContext中的`refresh()`方法
2. 做了一些初始化容器的准备工作后，调用父类AbstractApplicationContext的`obtainFreshBeanFactory()`方法，返回`ConfigurableListableBeanFactory`对象
3. 在`obtainFreshBeanFactory()`方法中，有模板方法`refreshBeanFactory()`，由子类去实现具体业务。而此ClassPathXmlApplicationContext读取配置文件是由AbstractRefreshableApplicationContext类去实现

> 注：如何判断钩子方法是那个调用那个类的方法，通过创建出来的对象，*如：ClassPathXmlApplicationContext对象*的类关系去分析即可

![ClassPathXmlApplicationContext类关系图](images/20191223100557813_9264.jpg)

`refresh()`方法中的`ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();`。该方法主要进行 xml 解析工作，流程如下：

1. 创建 XmlBeanDefinitionReader 对象

![xml文件解析流程](images/20191222120310814_5603.png)

2. 通过 Reader 对象加载配置文件

![xml文件解析流程](images/20191222120435332_8429.png)

3. 根据加载的配置文件把配置文件封装成 document 对象

![xml文件解析流程](images/20191222120708779_22348.png)

4. 创建 BeanDefinitionDocumentReader 对象，DocumentReader 负责对 document 对象解析

![xml文件解析流程](images/20191222120933729_19630.png)

5. `parseDefaultElement(ele, delegate);`负责常规标签解析
6. `delegate.parseCustomElement(ele);`负责自定义标签解析

![xml文件解析流程](images/20191222174025008_27590.png)

7. 最终解析的标签封装成 BeanDefinition 并缓存到容器中

**Xml 流程分析图**

![xml流程分析图](images/20191222113802450_32288.png)

### 4.3. 自定义标签解析

![自定义标签解析入口](images/20200109144928626_7171.png)

spring框架是通过spi设计思想来解决自定义标签解析。在DefaultBeanDefinitionDocumentReader类中的`parseBeanDefinitions()`方法中实现，具体的解析委托给BeanDefinitionParserDelegate类来实现，实现流程如下：

1. 获取自定义标签的 namespace 命令空间。如：`xmlns:context="http://www.springframework.org/schema/context"`

```java
// 根据node获取到node的命名空间，形如：http://www.springframework.org/schema/p
String namespaceUri = getNamespaceURI(node);
```

2. 根据命令空间获取 NamespaceHandler 对象。NamespaceUri 和 NamespaceHandler 之间会建立一个映射，spring 会从所有的 spring 的 jar 包中扫描 spring.handlers 文件，建立映射关系。

![](images/20200109150646047_16467.png)

spring.handler 文件，其实就是 namespaceUri 和类的完整限定名的映射

![](images/20200109142022356_24375.png)

3. 反射获取 NamespaceHandler 实例

```java
// 通过反射实例化对象
NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
```

4. 调用 init 方法

```java
// 调用处理类的init方法，在init方法中完成标签元素解析类的注册
namespaceHandler.init();
```

5. 返回处理类的实例对象后，调用 parse 方法

```java
handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
```

## 5. BeanDefinition
### 5.1. BeanDefinition 简介

BeanDefinition 在 spring 中贯穿全部，spring 要根据 BeanDefinition 对象来实例化 bean，只要把解析的标签，扫描的注解类封装成 BeanDefinition 对象，spring 才能实例化 bean

### 5.2. BeanDefinition 实现类

- ChildBeanDefinition
    - ChildBeanDefinition 是一种 bean definition，它可以继承它父类的设置，即ChildBeanDefinition 对 RootBeanDefinition 有一定的依赖关系
    - ChildBeanDefinition 从父类继承构造参数值，属性值并可以重写父类的方法，同时也可以增加新的属性或者方法。(类同于 java 类的继承关系)。若指定初始化方法，销毁方法或者静态工厂方法，ChildBeanDefinition 将重写相应父类的设置。`depends on`，`autowire mode`，`dependency check`，`sigleton`，`lazy init` 一般由子类自行设定。
- **GenericBeanDefinition（源码分析的重点关注的实现类）**
    - 注意：从 spring 2.5 开始，提供了一个更好的注册 bean definition 类 GenericBeanDefinition，它支持动态定义父依赖，方法是GenericBeanDefinition对象中`public void setParentName(@Nullable String parentName);`，GenericBeanDefinition 可以在绝大分部使用场合有效的替代 ChildBeanDefinition
    - GenericBeanDefinition 是一站式的标准 bean definition，除了具有指定类、可选的构造参数值和属性参数这些其它 bean definition 一样的特性外，它还具有通过 parenetName 属性来灵活设置 parent bean definition
    - 通常，GenericBeanDefinition 用来注册用户可见的 bean definition(可见的bean definition意味着可以在该类bean definition上定义post-processor来对bean进行操作，甚至为配置 parent name 做扩展准备)。RootBeanDefinition / ChildBeanDefinition 用来预定义具有 parent/child 关系的 bean definition。
- RootBeanDefinition
    - 一个 RootBeanDefinition 定义表明它是一个可合并的 bean definition：即在 spring beanFactory 运行期间，可以返回一个特定的 bean。RootBeanDefinition 可以作为一个重要的通用的 bean definition 视图。
    - RootBeanDefinition 用来在配置阶段进行注册 bean definition。然后，从 spring 2.5 后，编写注册 bean definition 有了更好的的方法：GenericBeanDefinition。GenericBeanDefinition 支持动态定义父类依赖，而非硬编码作为 root bean definition。

### 5.3. GenericBeanDefinition 创建实例测试

手动创建`BeanDefinition`对象并注册到spring容器中，定义一个被spring容器管理的类，实现`BeanDefinitionRegistryPostProcessor`接口，实现`postProcessBeanDefinitionRegistry`方法，在方法里设置需要实例化的类即可

```java
package com.moon.learningspring.beanDefinition;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * BeanDefinition 创建测试
 */
@Component
public class BeanDefinitionTest implements BeanDefinitionRegistryPostProcessor {
    /**
     * 在spring容器加载的执行此方法，可以手动创建BeanDefinition对象并注册到spring容器中
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 创建GenericBeanDefinition对象
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        // 设置需要实例化的类
        genericBeanDefinition.setBeanClass(BeanClass.class);

        // 如果需要实例化的类中属性赋值，需要获取MutablePropertyValues属性，赋值到此属性中
        MutablePropertyValues propertyValues = genericBeanDefinition.getPropertyValues();
        propertyValues.addPropertyValue("userName", "moon");

        // 将BeanDefinition对象注册到spring容器中，spring实例化对象，必须将beanName与BeanDefinition对象进行映射。（即添加到beanDefinitionMap属性中）
        registry.registerBeanDefinition("beanClass", genericBeanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
```

### 5.4. BeanDefinition 中的属性
#### 5.4.1. 属性图示

原文件在\code-learning-note\note attachments\02-后端框架\03-Spring\BeanDefinition属性结构图.xmind

![BeanDefinition相关属性](images/20191231112210755_22971.png)

#### 5.4.2. 属性作用解释

- 【id】：Bean 的唯一标识名。它必须是合法的 XMLID，在整个 XML 文档中唯一
- 【class】：用来定义类的全限定名（包名+类名）。只有子类 Bean 不用定义该属性
- 【name】：用来为 id 创建一个或多个别名。它可以是任意的字母符合。多个别名之间用逗号或空格分开
- 【parent】：子类 Bean 定义它所引用它的父类 Bean。这时前面的 class 属性失效。子类 Bean 会继承父类 Bean 的所有属性，子类 Bean 也可以覆盖父类 Bean 的属性。注意：子类 Bean 和父类 Bean 是同一个 Java 类
- 【abstract】：默认为“false”。用来定义 Bean 是否为抽象 Bean。它表示这个 Bean 将不会被实例化，一般用于父类 Bean，因为父类 Bean 主要是供子类 Bean 继承使用
- 【lazy-init】：默认为“default”。用来定义这个 Bean 是否实现懒初始化。如果为“true”，它将在 BeanFactory 启动时初始化所有的 SingletonBean。反之，如果为“false”,它只在 Bean 请求时才开始创建 SingletonBean
- 【autowire】：自动装配，默认为“default”。它定义了 Bean 的自动装载方式。
    - `no`：不使用自动装配功能
    - `byName`：通过 Bean 的属性名实现自动装配
    - `byType`：通过 Bean 的类型实现自动装配
    - `constructor`：类似于`byType`，但它是用于构造函数的参数的自动组装
    - `autodetect`：通过 Bean 类的反省机制（introspection）决定是使用`constructor`还是使用`byType`
- 【autowire-candidate】：**采用 xml 格式配置 bean 时**，将`<bean/>`元素的 autowire-candidate 属性设置为 false，这样容器在查找自动装配对象时，将不考虑该 bean，即它不会被考虑作为其它 bean 自动装配的候选者，但是该 bean 本身还是可以使用自动装配来注入其它 bean 的。
    - 主要的使用场景是：如果一个接口有多个实现类，但不希望某一个类自动注入的时候可以使用此配置，*注意，只有在用xml配置的时候生效*
- 【depends-on】：依赖对象。这个 Bean 在初始化时依赖的对象，这个对象会在这个 Bean 初始化之前创建
- 【init-method】：用来定义 Bean 的初始化方法，它会在 Bean 组装之后调用。它**必须是一个无参数的方法**
- 【primary】：用于定义某个实现类是否优先被选择注入。当一个接口有多个实现类时，如果在xml配置文件中将primary的值设置为true，并在某一个实现类上加上`@Primary`注解，此时spring容器在需要自动注入该接口时，优先选择此实现类进行注入
- 【destroy-method】：用来定义 Bean 的销毁方法，它在 BeanFactory 关闭时调用。同样，它也**必须是一个无参数的方法，而且只能应用于 singletonBean**
- 【factory-method】：定义创建该 Bean 对象的工厂方法。它用于相应的属性“factory-bean”，表示这个 Bean 是通过工厂方法创建。此时，“class”属性失效
- 【factory-bean】：定义创建该 Bean 对象的工厂类。如果使用了“factory-bean”则“class”属性失效
- 【MutablePropertyValues】：用于封装`<property>`标签的信息，其实类里面就是有一个 list，list里面是 PropertyValue 对象，PropertyValue 就是一个 name 和 value 属性，用于封装`<property>`标签的名称和值信息
- 【ConstructorArgumentValues】：用于封装`<constructor-arg>`标签的信息，其实类里面就是有一个 map，map 中用构造函数的参数顺序作为 key，值作为 value 存储到 map 中
- 【MethodOverrides】：用于封装 bean 标签下的 lookup-method 和 replaced-method 等子标签的信息，同样的类里面有一个 Set 对象添加 LookupOverride 对象和 ReplaceOverride 对象

### 5.5. BeanDefinition 创建过程

主要在`BeanDefinitionParserDelegate`类的`parseBeanDefinitionElement()`方法中进行对xml配置文件里面的bean标签进行解析，并创建BeanDefinition对象。

1. 创建BeanDefinition对象

```java
// 创建GenericBeanDefinition对象
AbstractBeanDefinition bd = createBeanDefinition(className, parent);
```

2. 解析属性

```java
// 解析bean标签的属性，并把解析出来的属性设置到BeanDefinition对象中
parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
```

3. 解析子标签

```java
// 解析bean中的meta标签
parseMetaElements(ele, bd);

// 解析bean中的lookup-method标签  重要程度【2】，可看可不看
parseLookupOverrideSubElements(ele, bd.getMethodOverrides());

// 解析bean中的replaced-method标签  重要程度【2】，可看可不看
parseReplacedMethodSubElements(ele, bd.getMethodOverrides());

// 解析bean中的constructor-arg标签  重要程度【2】，可看可不看
parseConstructorArgElements(ele, bd);

// 解析bean中的property标签  重要程度【2】，可看可不看
parsePropertyElements(ele, bd);
```

**解析过程重点记忆：MutablePropertyValues属性**。如果想要设置类的属性值，那么就需要往这个对象中添加 PropertyValue 对象

## 6. Bean 的实例化过程
### 6.1. BeanDefinitionRegistryPostProcessor 接口

在AbstractApplicationContext类的`refresh()`方法中，调用`invokeBeanFactoryPostProcessors(beanFactory)`方法

BeanDefinitionRegistryPostProcessor 这个接口的调用分为三步：

1. 调用实现了 PriorityOrdered 排序接口
2. 调用实现了 Ordered 排序接口
3. 没有实现接口的调用

这个接口的理解：获取 BeanDefinitionRegistry 对象，获取到这个对象就可以获取这个对象中注册的所有 BeanDefinition 对象，所以可以知道，拥有这个对象就可以完成里面所有 BeanDefinition 对象的修改和新增操作

### 6.2. BeanPostProcessor 的注册

1. 在AbstractApplicationContext类的`refresh()`方法中，调用`registerBeanPostProcessors(beanFactory);`这个方法里面。会拿到 BeanFactory 中所有注册的 BeanDefinition 对象的名称 beanName。

```java
public static void registerBeanPostProcessors(
		ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

	// 获取到工程里面所有实现了BeanPostProcessor接口的类，获取到BeanDefinition的名称
	String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
	....
}
```

2. 然后判断是否实现了 `PriorityOrdered` 排序接口、`Ordered` 排序接口，getBean 是将该 ppName 对应的 BeanDefinition 对象实例化

```java
// 提前实例化BeanPostProcessor类型的bean，然后bean进行排序
for (String ppName : postProcessorNames) {
	if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
		// getBean是实例化方法，是bean实例化过程
		BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
		priorityOrderedPostProcessors.add(pp);
		// 判断类型是否为MergedBeanDefinitionPostProcessor，如果是则代码是内部使用的
		if (pp instanceof MergedBeanDefinitionPostProcessor) {
			internalPostProcessors.add(pp);
		}
	}
	else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
		orderedPostProcessorNames.add(ppName);
	}
	else {
		nonOrderedPostProcessorNames.add(ppName);
	}
}
```

3. 把对应的 `BeanPostProcessor` 对象注册到 BeanFactory 中，BeanFactory 中有一个 List 容器(`private final List<BeanPostProcessor> beanPostProcessors`)接收

```java
// 注册到BeanFactory中
registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

/* 注册的BeanPostProcessor所有实例都存放在BeanFactory的 private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>(); 容器中 */
```

### 6.3. getSingleton 方法（获取单例）
#### 6.3.1. 代码所在位置

- 核心代码位置：`AbstractBeanFactory.doGetBean()` 方法中

```java
......

// Create bean instance.
if (mbd.isSingleton()) {
	// 此逻辑是重点，因为大部分情况都是单例的
	sharedInstance = getSingleton(beanName, () -> {
		try {
			// 创建bean实例核心逻辑
			return createBean(beanName, mbd, args);
		}
		catch (BeansException ex) {
			// Explicitly remove instance from singleton cache: It might have been put there
			// eagerly by the creation process, to allow for circular reference resolution.
			// Also remove any beans that received a temporary reference to the bean.
			destroySingleton(beanName);
			throw ex;
		}
	});
	// 此方法是FactoryBean接口的调用入口
	bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
}

......
```

方法里面核心要点：

![](images/20200503082103048_32300.png)

把 beanName 添加到 singletonsCurrentlyInCreation Set 容器中，在这个集合里面的 bean 都是正在实例化的 bean，就是实例化还没做完的 BeanName

```java
/**
 * Callback before singleton creation.
 * <p>The default implementation register the singleton as currently in creation.
 * @param beanName the name of the singleton about to be created
 * @see #isSingletonCurrentlyInCreation
 */
protected void beforeSingletonCreation(String beanName) {
	// 把beanName添加到singletonsCurrentlyInCreation的Set容器中，在这个集合里面的bean都是正在实例化的bean
	if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
		// 如果需要创建的beanName不在inCreationCheckExclusions容器（存储排除创建的）中，并且singletonsCurrentlyInCreation容器（存储正在创建的）已经存在，抛出异常
		throw new BeanCurrentlyInCreationException(beanName);
	}
}
```

在执行`Object getSingleton(String beanName, ObjectFactory<?> singletonFactory)`方法的过程中，调用了`singletonObject = singletonFactory.getObject();`方法(即会调用外层的lambda表达式的逻辑)。调到 getObject 方法，完成 bean 的实例化。

![](images/20200502171135831_22205.png)

![](images/20200502171240975_217.png)

getObject 调用完后，就代表着 Bean 实例化已经完成了，此还需要进行以下两步操作：

![](images/20200503082651108_14373.png)

1. 调用`singletonsCurrentlyInCreation`方法，把 beanName 从这个集合中删除

```java
/**
 * Callback after singleton creation.
 * <p>The default implementation marks the singleton as not in creation anymore.
 * @param beanName the name of the singleton that has been created
 * @see #isSingletonCurrentlyInCreation
 */
protected void afterSingletonCreation(String beanName) {
	if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
		throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
	}
}
```

2. 调用`addSingleton`方法，把 bean 缓存到一级缓存中

```java
/**
 * Add the given singleton object to the singleton cache of this factory.
 * <p>To be called for eager registration of singletons.
 * @param beanName the name of the bean
 * @param singletonObject the singleton object
 */
protected void addSingleton(String beanName, Object singletonObject) {
	synchronized (this.singletonObjects) {
		this.singletonObjects.put(beanName, singletonObject);
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);
	}
}
```

#### 6.3.2. 涉及相关重要的核心属性

在`DefaultSingletonBeanRegistry`类中的`singletonObjects`属性，此属性是Map结构容器，用于存在完全实例化的对象。

> 完全实例化对象：指的是此类创建出对象，并且类里的所有属性与DI（依赖注入）都全部已经完成

### 6.4. createBean 方法

代码位置：`AbstractBeanFactory.doGetBean()` 方法中，此方法是 bean 实例化核心方法

![](images/20200503083345738_31690.png)

在实例化bean的方法中，会把 bean 实例化，并且包装成 BeanWrapper。*注：但此时不涉及DI（依赖注入）*

![](images/20200503085619659_19844.png)

![](images/20200503085137982_4511.png)


#### 6.4.1. FactoryMethodName 属性的处理

进入`createBeanInstance(beanName, mbd, args);`方法中，会有判断是否配置了`FactoryMethodName`属性的处理

![](images/20200503093139082_665.png)

这个方法是反射调用类中的 factoryMethod 方法。这要知道 `@Bean` 方法的原理，实际上 spring 会扫描有 `@bean` 注解的方法，然后把方法名称设置到 BeanDefinition 的 factoryMethod
属性中，接下来就会调到上面截图中的方法实现 `@Bean` 方法的调用。该方法里面的参数解析过程不需要了解。

#### 6.4.2. 实例化的类存有参构造函数时的处理方法

这个方法是 `BeanPostProcessor` 接口类的首次应用，最终会调到 `AutowiredAnnotationBeanPostProcessor` 类的方法，在方法中会扫描有注解的构造函数然后完成装配过程。

```java
// Candidate constructors for autowiring?
// 寻找当前正在实例化的bean中是否有 @Autowired 注解的构造函数。核心代码，重要程度【5】
Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName); // 此方法获取类的构造函数（多个，返回数组）
if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
		mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
	// 如果ctors不为空，就说明构造函数上有@Autowired注解
	return autowireConstructor(beanName, mbd, ctors, args);
}
```

进入`determineConstructorsFromBeanPostProcessors()`方法中。

```java
@Nullable
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
		throws BeansException {
	if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
		// getBeanPostProcessors()方法获取所有注册到BeanFactory里的BeanPostProcessor，在此处进行循环调用
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
				SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
				// 此处只有AutowiredAnnotationBeanPostProcessor类会启作用，其他的实现类（不关注此功能的）只需要返回null即可
				Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);
				if (ctors != null) {
					return ctors;
				}
			}
		}
	}
	return null;
}
```

**总结：凡是使用`@Autowired`注解，如果参数是一个引用的类型，就会触发这个引用类型的getBean操作**

#### 6.4.3. 无参构造函数的实例化

这就是简单的反射实例化。大部分类的实例化都会走无参构造的逻辑

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
	......

	// 通过无参构造函数的实例化bean，实际上大部分的实例都是采用的无参构造函数的方式实例化
	// No special handling: simply use no-arg constructor.
	return instantiateBean(beanName, mbd);
}
```

#### 6.4.4. 类中注解的收集

实例化完成后，接下来就需要对类中的属性进行依赖注入操作。类里面属性和方法的依赖注入往往用 `@Autowired` 或者 `@Resource` 注解

```java
// Allow post-processors to modify the merged bean definition.
synchronized (mbd.postProcessingLock) {
	if (!mbd.postProcessed) {
		try {
			/*
			 * 此方法是对实例化的bean的注解收集，对类中注解的装配过程。重要程度【5】
			 * 	这个接口是BeanPostProcessor接口的典型运用，需要重点理解，其中BeanPostProcessor接口的实现类相应处理的注解如下：
			 * 		CommonAnnotationBeanPostProcessor  支持了@PostConstruct，@PreDestroy，@Resource注解
			 * 		AutowiredAnnotationBeanPostProcessor 支持 @Autowired，@Value注解
			 */
			applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Post-processing of merged bean definition failed", ex);
		}
		mbd.postProcessed = true;
	}
}
```

注解的收集，也是通过不同的 `BeanPostProcessor` 接口类型实例来一个个循环处理的。

![](images/20200505171610617_18011.png)


##### 6.4.4.1. CommonAnnotationBeanPostProcessor

第1个是调用 `CommonAnnotationBeanPostProcessor` 类，这个类完成了 `@Resource` 注解的属性或者方法的收集。这个类还对 `@PostConstruct` 和 `@PreDestory` 等注解的支持

![](images/20200505172241784_18237.png)

其中收集`@Resource`注解的过程如下：

1. 看缓存里面有没有 `InjectionMetadata` 对象
2. 从类中获取所有 `Field` 对象，循环 `field` 对象，判断 `field` 有没有 `@Resource` 注解，如果有注解封装成 `ResourceElement` 对象
3. 从类中获取所有 `Method` 对象，循环 `Method` 对象，判断 `Method` 有没有 `@Resource` 注解，如果有注解封装成 `ResourceElement` 对象
4. 最终把两个 `field` 和 `Method` 封装的对象集合封装到 `InjectionMetadata` 对象中

##### 6.4.4.2. AutowiredAnnotationBeanPostProcessor

`AutowiredAnnotationBeanPostProcessor` 类，对 `@Autowired` 注解的属性和方法的收集。收集过程基本上跟 `@Resource` 注解的收集差不多

#### 6.4.5. IOC\DI 依赖注入
##### 6.4.5.1. @Resource 和 @Autowired 注解依赖注入

![](images/20200511233417561_3122.png)

相应的方法代码块

```java
populateBean(beanName, mbd, instanceWrapper);
```

进入方法，核心代码位置在红框中

![](images/20200511234427945_22372.png)

上面又是一个 `BeanPostProcessor` 类型接口的运用，前面有对`@Resource`、`@Autowired`注解的收集，那么这个方法就是根据收集到的注解进行反射调用。研究其中的子类`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor`的实现逻辑

![](images/20200511234732131_22606.png)

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
	// 获取之前进行的@Autowired注解收集的元数据（此方法在之前的注解收集过程里研究过）
	InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
	try {
		// 进行反射完成依赖注入
		metadata.inject(bean, beanName, pvs);
	}
	catch (BeanCreationException ex) {
		throw ex;
	}
	catch (Throwable ex) {
		throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
	}
	return pvs;
}
```

循环收集到的 `metaData` 对象中的List集合，调用里面的每个 InjectedElement 的 inject 方法完成依赖注入。

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
	Collection<InjectedElement> checkedElements = this.checkedElements;
	Collection<InjectedElement> elementsToIterate =
			(checkedElements != null ? checkedElements : this.injectedElements);
	if (!elementsToIterate.isEmpty()) {
		// 循环所有需要注入的元素
		for (InjectedElement element : elementsToIterate) {
			if (logger.isTraceEnabled()) {
				logger.trace("Processing injected element of bean '" + beanName + "': " + element);
			}
			// 对元素/方法进行注入
			element.inject(target, beanName, pvs);
		}
	}
}
```

![](images/20200512233942404_30191.png)

其中 value 值的获取，如果依赖的属性是一个引用类型必定会触发该属性的`BeanFactory.getBean()`操作，从 spring 容器中获取到对应的实例。

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
	Field field = (Field) this.member;
	......
	if (value != null) {
		ReflectionUtils.makeAccessible(field);
		field.set(bean, value);
	}
}
```

方法的依赖注入类似的逻辑

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues) throws Throwable {
	if (checkPropertySkipping(pvs)) {
		return;
	}
	Method method = (Method) this.member;
	......

	if (arguments != null) {
		try {
			ReflectionUtils.makeAccessible(method);
			method.invoke(bean, arguments);
		}
		catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}
}
```

以上就是对注解 `@Resource` 和 `@Autowired` 的依赖注入的实现逻辑

##### 6.4.5.2. xml 配置的依赖注入

比如在spring的xml配置文件的 `<bean>` 标签中配置以下属性

```xml
<!-- property子标签测试（此方式几乎不使用，直接使用@Value实现） -->
<bean class="com.moon.spring.bean.PropertyBean" id="propertyBean">
    <property name="username" value="MoonZero"/>
    <property name="password" value="123"/>
</bean>
```

标签的依赖注入实现逻辑代码位置如下：

![](images/20200514231626392_26218.png)

这块逻辑是专门做 xml 配置依赖注入的，基本上现在基于 xml 配置的依赖很少使用，暂时不研究

#### 6.4.6. bean 实例化后的操作

核心代码位置在`AbstractAutowireCapableBeanFactory`类中的`doCreateBean()`方法中

```java
// bean实例化+ioc依赖注入完以后的调用，非常重要，重要程度【5】
exposedObject = initializeBean(beanName, exposedObject, mbd);
```

##### 6.4.6.1. InitializingBean 接口介绍

实现`InitializingBean`接口的类，spring会在实例化该类以后，调用接口的`afterPropertiesSet()`方法

```java
package com.moon.spring.bean;

import org.springframework.beans.factory.InitializingBean;

/**
 * Spring 框架中 InitializingBean 接口的示例
 * <p>
 * 如果需要在一个类实例化以后去做一些逻辑处理，那么就可以借助这个InitializingBean接口来完成。
 *
 * @author MoonZero
 * @version 1.0
 * @date 2020-5-18 23:31
 * @description
 */
public class InitMethodBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("****** 实现InitializingBean接口的afterPropertiesSet()方法执行了 ******");
    }

    /*
     * 在xml配置文件中<bean>标签中，配置init-method属性
     *  注意：通过xml配置文件的方式实现类创建后执行init-method，相应的方法的执行顺序是在实现了InitializingBean接口的afterPropertiesSet()方法执行之后
     */
    public void initMethod() {
        System.out.println("======= InitMethodBean.initMethod()方法执行了 =========");
    }

}
```

也可以通过xml配置文件的方式，实现与`InitializingBean`接口一样的效果，只要在`<bean>`标签中配置`init-method`属性，值为需要执行的方法名称

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       xmlns:moon="http://www.moon.com/schema/mytags"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.moon.com/schema/mytags
       http://www.moon.com/schema/mytags.xsd">
    ......
    <!-- 配置init-method属性示例
            用于定义 Bean 的初始化方法，会在 Bean 组装之后调用。注意：该方法必须是一个无参数的方法
     -->
    <bean id="initMethodBean" class="com.moon.spring.bean.InitMethodBean" init-method="initMethod"/>
</beans>
```

> 注意：通过xml配置文件的方式实现类创建后执行`init-method`，相应的方法的执行顺序是在实现了`InitializingBean`接口的`afterPropertiesSet()`方法执行之后

启动spring，运行结果如下

![](images/20200519232135597_12874.png)

##### 6.4.6.2. 对某些 Aware 接口的调用(实例化Bean后执行流程1)

此步骤主要是对于当前实现Aware的接口的方法调用

![](images/20200524183014736_11831.png)

```java
private void invokeAwareMethods(final String beanName, final Object bean) {
	if (bean instanceof Aware) {
		// 调用实现BeanNameAware接口的setBeanName()方法，可以获取当前实例化的beanName
		if (bean instanceof BeanNameAware) {
			((BeanNameAware) bean).setBeanName(beanName);
		}
		if (bean instanceof BeanClassLoaderAware) {
			ClassLoader bcl = getBeanClassLoader();
			if (bcl != null) {
				((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
			}
		}
		// 调用实现BeanFactoryAware接口的setBeanFactory()方法，可以获取当前BeanFactory对象
		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
		}
	}
}
```

##### 6.4.6.3. @PostConstruct 注解方法的调用(实例化Bean后执行流程2)

此处又是一个 `BeanPostProcessor` 接口的运用。核心代码位置

```java
/* AbstractAutowireCapableBeanFactory类中的initializeBean()方法 */
Object wrappedBean = bean;
if (mbd == null || !mbd.isSynthetic()) {
	// 对类中某些特殊方法的调用，比如@PostConstruct，Aware接口。重要程度【5】
	wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
}
```

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
		throws BeansException {
	Object result = existingBean;
	/*
	 * 循环容器中所有BeanPostProcessor，着重理解以下几个实现类
	 *  1、ApplicationContextAwareProcessor  对某个Aware接口方法的调用
	 *  2、InitDestroyAnnotationBeanPostProcessor  @PostConstruct注解方法的调用
	 *  3、ImportAwareBeanPostProcessor  对ImportAware类型实例setImportMetadata方法调用。（这个对理解springboot有很大帮助。此时暂时不深入了解）
	 */
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessBeforeInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```

从前面了解到，有 `@PostConstruct` 注解的方法会收集到一个 `metaData` 对象中，现在就是通过 `BeanPostProcessor` 接口调到 `CommonAnnotationBeanPostProcessor` 类，然后在类中拿到 `metaData` 对象，根据对象里面的容器来反射调用有注解的方法。核心逻辑（*以`InitDestroyAnnotationBeanPostProcessor`为例，此类用于调用`@PostConstruct`相应的方法*）代码如下：

```java
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	// 获取生命周期相关方法的Metadata对象
	LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
	try {
		// 调用@PostConstruct注解的方法
		metadata.invokeInitMethods(bean, beanName);
	}
	catch (InvocationTargetException ex) {
		throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
	}
	catch (Throwable ex) {
		throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
	}
	return bean;
}
```

```java
public void invokeInitMethods(Object target, String beanName) throws Throwable {
	Collection<LifecycleElement> checkedInitMethods = this.checkedInitMethods;
	Collection<LifecycleElement> initMethodsToIterate =
			(checkedInitMethods != null ? checkedInitMethods : this.initMethods); // 获取之前收集 @PostConstruct 注解的 initMethods 容器中
	if (!initMethodsToIterate.isEmpty()) {
		for (LifecycleElement element : initMethodsToIterate) {
			if (logger.isTraceEnabled()) {
				logger.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
			}
			// 反射调用
			element.invoke(target);
		}
	}
}
```

有 `@PostConstruct` 注解的会收集到 `initMethods` 容器中，接下来就是方法的反射调用。

```java
public void invoke(Object target) throws Throwable {
	ReflectionUtils.makeAccessible(this.method);
	this.method.invoke(target, (Object[]) null);
}
```

##### 6.4.6.4. InitializingBean 接口和 init-method 属性的调用(实例化Bean后执行流程3)

执行流程往下就是对实现了 `InitializingBean` 接口的类与在xml配置文件中`<bean>`标签中配置了 `init-method` 属性的相应方法的调用

![](images/20200525230453678_20366.png)

从源码可以看出，实现了 `InitializingBean` 接口的类就必然会调用到 `afterPropertiesSet()` 方法，且 `Init-method` 属性调用是在 `afterPropertiesSet()` 方法之后

```java
protected void invokeInitMethods(String beanName, final Object bean, @Nullable RootBe
		throws Throwable {
	// 判断是否为InitializingBean接口实现
	boolean isInitializingBean = (bean instanceof InitializingBean);
	// 首先调用实现InitializingBean接口的afterPropertiesSet()方法
	if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("aft
		if (logger.isTraceEnabled()) {
			logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanNa
		}
		if (System.getSecurityManager() != null) {
			try {
				AccessController.doPrivileged((PrivilegedExceptionAction<Object>) ()
					((InitializingBean) bean).afterPropertiesSet();
					return null;
				}, getAccessControlContext());
			}
			catch (PrivilegedActionException pae) {
				throw pae.getException();
			}
		}
		else {
			// 直接调用afterPropertiesSet()方法
			((InitializingBean) bean).afterPropertiesSet();
		}
	}
	// 然后调用xml配置文件中的init-method属性相应的方法
	if (mbd != null && bean.getClass() != NullBean.class) {
		// 获取调用的方法名称
		String initMethodName = mbd.getInitMethodName();
		if (StringUtils.hasLength(initMethodName) &&
				!(isInitializingBean && "afterPropertiesSet".equals(initMethodName))
				!mbd.isExternallyManagedInitMethod(initMethodName)) {
			// 此方法为调用xml配置文件中的init-method属性相应的方法
			invokeCustomInitMethod(beanName, bean, mbd);
		}
	}
}
```

`afterPropertiesSet`、`Init-method`和有`@PostConstruct`注解的方法其实核心功能都是一样的，只是调用时序不一样而已，都是在该类实例化和 IOC 做完后调用的，可以在这些方法中做一些在 spring 或者 servlet 容器启动的时候的初始化工作。比如缓存预热，比如缓存数据加载到内存，比如配置解析，等等初始化工作。

在这个方法里面还有一个重要的逻辑

```java
/* AbstractAutowireCapableBeanFactory */
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
    ......
    // 此部分也是一个 BeanPostProcessor 接口的运用，在这里会返回 bean 的代理实例，这个就是 AOP 的入口【暂未研究】
    if (mbd == null || !mbd.isSynthetic()) {
    	wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    return wrappedBean;
}
```

也是一个 `BeanPostProcessor` 接口的运用，在这里会返回 bean 的代理实例，这个就是 AOP 的入口。

##### 6.4.6.5. FactoryBean 接口

接口方法触发入口位置：`AbstractBeanFactory --> doGetBean()`

```java
// Create bean instance.
if (mbd.isSingleton()) {
	/* 此逻辑是重点，因为大部分情况都是单例的 */
	sharedInstance = getSingleton(beanName, () -> {
		try {
			// 创建bean实例核心逻辑
			return createBean(beanName, mbd, args);
		}
		catch (BeansException ex) {
			// Explicitly remove instance from singleton cache: It might have been put there
			// eagerly by the creation process, to allow for circular reference resolution.
			// Also remove any beans that received a temporary reference to the bean.
			destroySingleton(beanName);
			throw ex;
		}
	});
	// 此方法是FactoryBean接口的调用入口
	bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
}
```

该接口的作用是：在实例化和 IOC/DI 做完后，就会调用 FactoryBean 类型的接口，重写`getObject()`，方法，可以返回不同的bean类型，此bean实例会被Spring容器管理

- 如果要获取到 FactoryBean 接口实现类本身，就必须加上`&`符号，比如：`beanFactory.getBean("&beanName")`。
- `BeanFactory.getBean("beanName")` 只能获取到 `getObject()` 方法返回的实例

示例如下：

```java
package com.moon.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * Spring 框架 FactoryBean 接口使用示例
 */
@Component
public class FactoryBeanDemo implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        /*
         *  此处可以进行一些其他的逻辑处理，然后返回一个新的bean
         *   注：此处返回的新的实例与原来实现了FactoryBean接口的此类的实例互不干扰
         */
        return new FactoryBeanOther();
    }

    @Override
    public Class<?> getObjectType() {
        return FactoryBeanOther.class;
    }
}


/* 测试 */
@Autowired
private ApplicationContext applicationContext;

/* FactoryBean接口实现测试 */
@Test
public void factoryBeanTest() {
    // 实现了FactoryBean接口的类，通过bean的id只能获取该类实现了getObject()方法返回的对象实例
    FactoryBeanOther other = (FactoryBeanOther) applicationContext.getBean("factoryBeanDemo");
    System.out.println(other); // com.moon.spring.factorybean.FactoryBeanOther@4cc8eb05
    // 如果要获取实现了FactoryBean接口的类的实例，只能通过【"&" + beanName】来获取实例
    FactoryBeanDemo factoryBeanDemo = (FactoryBeanDemo) applicationContext.getBean("&factoryBeanDemo");
    System.out.println(factoryBeanDemo); // com.moon.spring.factorybean.FactoryBeanDemo@51f116b8
}
```

##### 6.4.6.6. 循环依赖
###### 6.4.6.6.1. 循环依赖流程图

循环依赖参照流程图（引用其他资料。）<font color="red">有时间自己再重新整理</font>

![](images/20200531165112369_24084.png)

> 图片出处：https://www.processon.com/view/link/5df9ce52e4b0c4255ea1a84f

###### 6.4.6.6.2. 循环依赖需要注意的问题

- 循环依赖只会出现在单例实例无参构造函数实例化情况下
- 有参构造函数的加 `@Autowired` 的方式循环依赖会直接报错的，多例的循环依赖也是直接报错的。

###### 6.4.6.6.3. 循环依赖步骤总结

1. A 类无参构造函数实例化后，将实例设置到三级缓存中
2. A 类执行 `populateBean()` 方法进行依赖注入，这里触发了 B 类属性的 `getBean()` 操作
3. B 类无参构造函数实例化后，将实例设置到三级缓存中
4. B 类执行 `populateBean()` 方法进行依赖注入，这里又会触发了 A 类属性的 `getBean()` 操作
5. A 类之前正在实例化，`singletonsCurrentlyInCreation` 集合中有已经有这个 A 类的实例了，三级缓存里面也有了，所以这时候是从三级缓存中拿到的提前暴露的 A 实例，该实例还没有进行 B 类属性的依赖注入的，B 类属性为空。
6. B 类拿到了 A 的提前暴露实例注入到引入 A 类属性中了
7. B 类实例化已经完成，B 类的实例化是由 A 类实例化中 B 属性的依赖注入触发的 `getBean()` 操作进行的，现在 B 已经实例化，所以 A 类中 B 属性就可以完成依赖注入了，这时候 A 类 B 属性已经有值了
8. B 类 A 属性指向的就是 A 类实例堆空间，所以这时候 B 类 A 属性也会有值了。

## 7. BeanPostProcessor 接口理解

BeanPostProcessor 接口类型实例是针对某种特定功能的埋点，在这个点会根据接口类型来过滤掉不关注这个点的其他类，只有真正关注的类才会在这个点进行相应的功能实现。










