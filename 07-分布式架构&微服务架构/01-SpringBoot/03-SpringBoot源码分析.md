# Spring Boot 源码分析

> 源码分析的笔记基于 spring-boot-2.2.2.RELEASE 版本
>
> 源码下载地址：https://github.com/spring-projects/spring-boot/releases

## 1. SpringBoot源码构建

### 1.1. 方式1：maven命令编译项目

1. 进入到下载的源码目录执行如下命令：

```bush
mvn -Dmaven.test.skip=true clean install
```

> `-Dmaven.test.skip=true` 在打包过程中会忽略testcase，不然编译过程漫长，而且可能会发生一些异常，初次编译的目的削减掉这一步，如果正常编译通过，后面可以对那测试案例做测试。

2. 如果出现报错找不到spring-javaformat插件 执行`mvn spring-javaformat:apply`命令即可。
3. 如果再出现错误，执行`mvn -Dmaven.test.failure.ignore=true -Dmaven.test.skip=true clean install`

### 1.2. 使用 mvnwrapper 编译项目

1. 进入到下载的源码目录执行命令：`mvnw clean install -DskipTests -Pfast`

![](images/20200218000353682_15308.png)

2. 如果出现报错找不到 spring-javaformat 插件 执行 `mvn spring-javaformat:apply` 命令即可。命令执行成功后，再次执行指令：`mvnw clean install -DskipTests -Pfast`

## 2. Spring Boot 自动配置原理分析

Spring Boot 框架是一个将整合框架的整合代码都写好了的框架。所以要知道它的工作原理才能够找到各种整合框架可以配置的属性，以及属性对应的属性名。

### 2.1. 准备阶段

Spring Boot 是一个可快速整合各种技术的框架，Spring Boot 会大量收录行业内相关技术的技术相关配置、技术初始化等信息，将其收集整理成一个依赖、配置、技术初始化的技术列表集合

### 2.2. spring-boot-dependencies 父工程依赖管理原理

创建 Spring Boot 项目，继承了 Spring Boot 的父工程 spring-boot-starter-parent 后，查看工程的依赖关系，父工程又依赖了 spring-boot-dependencies 模块，而此 spring-boot-denpendencies 模块中的 pom 文件，管理所有公共 Starter 与相关技术的依赖，并且通过 `<dependencyManagement>` 标签实现 jar 版本管理

因为继承父工程 spring-boot-starter-parent 后，可以根据需要，直接引用相应的 starter 即可，不需要配置版本号

![](images/20201006095224766_3600.png)

### 2.3. spring-boot-starters 工程

#### 2.3.1. starters 的原理

starters 是依赖关系的整理和封装，是一套依赖坐标的整合。只要导入相关的 starter 即可该功能及其相关必需的依赖

![](images/262634513220551.png)

> 举例：进行 JPA or Web 开发，只需要导入 spring-boot-starter-data-jpa 或 spring-boot-starter-web 即可

每个 starter 包含了当前功能下的许多必备依赖坐标，这些依赖坐标是项目开发，上线和运行必须的。同时这些依赖也支持依赖传递。例如：spring-boot-starter-web 包含了所有 web 开发必须的依赖坐标

![](images/456984913226844.png)

#### 2.3.2. 自定义 starter 的命名规范

- 官方的 starter 命名：`spring-boot-starter-*`
- 非官方的 starter 命名：`thirdpartyproject-spring-boot-starter`

官方提供的 starter 详见官方文档：https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/using-spring-boot.html#using-boot-starter

### 2.4. 自动配置信息位置说明

每个 starter 都有相应自动配置处理在 spring-boot-autoconfigure 模块中，在此模块的 `src/main/resources/META-INF` 中定义了所有内置支持的技术框架及其相关的约定的默认配置：

- `additional-spring-configuration-metadata.json`：默认配置，Spring Boot 采用约定大于配置设计思想。
- `spring.factories`：定义了自动配置相关的处理类的映射关系。在项目启动的时候会将相关映射的处理类加载到 Spring 容器中

![](images/20201006141108089_25191.png)

![](images/20201006143251521_21845.png)

- 在 spring-boot-autoconfigure 模块中，所有支持的框架根据功能类型来划分包，每个包都有一个 `XxxAutoConfiguration` 配置类，都是一个基于纯注解的配置类，是各种框架整合的代码。如图所示：

![](images/20201006145254228_20074.png)

- 如果配置的框架有默认的配置参数，都放在一个命名为`XxxProperties`的属性类，如图所示：

![](images/20201006145402145_17341.png)

- 通过项目的 resources 下的 `application.properties` 或 `application.yml` 文件可以修改每个整合框架的默认属性，从而实现了快速整合的目的。

![](images/20201006150503441_32641.png)

### 2.5. 自动配置流程分析

查看启动类注解 `@SpringBootApplication`，可以跟踪自动配置加载的实现步骤

#### 2.5.1. 自定义配置的注解

Spring Boot 启动注解 `@SpringBootApplication` 包含若干个注解，其中 `@SpringBootConfiguration` 与 `@ComponentScan` 均为 Spring 基础的注解，而实现自动配置的关键是 `@EnableAutoConfiguration` 注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
    ...
}
```

查看 `@EnableAutoConfiguration` 注解源码，分别包含了 `@AutoConfigurationPackage` 与 `@Import(AutoConfigurationImportSelector.class)`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    ...
}
```

#### 2.5.2. @AutoConfigurationPackage 注解

在 `@AutoConfigurationPackage` 注解中，通过 `@Import` 注解导入 `AutoConfigurationPackages.Registrar` 的内部类

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
    
}
```

`AutoConfigurationPackages.Registrar` 内部类，实现了 `ImportBeanDefinitionRegistrar` 接口，Spring 容器启动时会调用 `registerBeanDefinitions` 方法

```java
@Override
public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
	register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
}
```

该方法会分别将当前 `@AutoConfigurationPackages` 所有位置的包及其子包、与其 `basePackages`、`basePackageClasses` 属性配置的都包路径，都注册到 Spring 的 `BeanDefinitionRegistry` 中进行包扫描

![](images/235671714247010.png)

![](images/105952014239679.png)

#### 2.5.3. AutoConfigurationImportSelector 类

在 `@EnableAutoConfiguration` 注解中，通过 `@Import` 注解引入自动配置处理类 `AutoConfigurationImportSelector`，该类实现了 `DeferredImportSelector` 接口，而 `DeferredImportSelector` 接口又继承了 `ImportSelector` 接口。Spring 容器初始化时会调用 `selectImports` 方法

```java
@Override
public String[] selectImports(AnnotationMetadata annotationMetadata) {
	if (!isEnabled(annotationMetadata)) {
		return NO_IMPORTS;
	}
	AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
			.loadMetadata(this.beanClassLoader);
	// 此方法是主要处理逻辑，获取需要加载的bean全限定名集合
	AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(autoConfigurationMetadata,
			annotationMetadata);
	return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
}
```

在 `getAutoConfigurationEntry` 方法中通过 `getCandidateConfigurations` 方法获取所有需要加载的 bean 全限定名集合

```java
protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata,
		AnnotationMetadata annotationMetadata) {
	if (!isEnabled(annotationMetadata)) {
		return EMPTY_ENTRY;
	}
	AnnotationAttributes attributes = getAttributes(annotationMetadata);
	// 通过 getCandidateConfigurations 方法获取所有需要加载的 bean 全限定名集合
	List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
	// 去重处理
	configurations = removeDuplicates(configurations);
	// 获取不需要加载的 bean,这里我们可以通过 spring.autoconfigure.exclude 人为配置
	Set<String> exclusions = getExclusions(annotationMetadata, attributes);
	checkExcludedClasses(configurations, exclusions);
	configurations.removeAll(exclusions);
	configurations = filter(configurations, autoConfigurationMetadata);
	// 发送事件，通知所有的 AutoConfigurationImportListener 进行监听
	fireAutoConfigurationImportEvents(configurations, exclusions);
	return new AutoConfigurationEntry(configurations, exclusions);
}
```

跟踪源码可知，最终会读取资源目录中的 `META-INF/spring.factories` 文件，即前面所说的定义了自动配置相关的处理类的映射关系

![](images/396074714236234.png)

读取 `spring.factories` 文件，获取所有自动处理类全限定名，然而 Spring Boot 不会将所有处理类都实例化，每个配置类上都会有 `@ConditionalOn*` 的条件注解来控制 bean 的加载，下面以 `RedisAutoConfiguration` 为例：

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {
    ...
}
```

当导入了 spring-boot-starter-data-redis 的依赖后，项目存在 `RedisOperations` 字节码文件后，此时才会实例化 `RedisAutoConfiguration` 类

![](images/330292115249868.png)

双比如内置 web 容器的处理类 `EmbeddedWebServerFactoryCustomizerAutoConfiguration`，类上引入 `@EnableConfigurationProperties({ServerProperties.class})` 注解，用于加载默认配置类的参数。然后内部类中通过 `@ConditionalOnClass` 注解来控制加载哪种类型的 web 容器

![](images/20201006152054124_172.png)

#### 2.5.4. 小结

Spring Boot 启动时先加载 spring.factories 文件中的 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 配置项，然后根据定义在类上的 `@ConditionalOn*` 条件注解来决定哪些 bean 需要加载。

对于正常加载成 bean 的类，通常会通过 `@EnableConfigurationProperties` 注解初始化对应的配置属性类并加载对应的配置。而配置属性类上通常会通过 `@ConfigurationProperties` 加载指定前缀的配置，并且这些配置通常都有默认值。

### 2.6. 变更自动配置

Spring Boot 支持对自动配置的流程做一些高级定制，比如禁用一些自动配置的加载。具体操作有如下几种方式：

#### 2.6.1. 方式1：配置文件排除

通过修改 Spring Boot 配置文件的 `spring.autoconfigure.exclude` 选项，排除指定的自动配置处理类

```yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
```

#### 2.6.2. 方式2：注解属性排除

通过 `@EnableAutoConfiguration` 排除指定的自动配置处理类

- `exclude` 属性：指定排除的多个配置处理类字节码（数组）
- `excludeName` 属性：指定排除的多个配置处理类全限定名称（数组）

```java
@EnableAutoConfiguration(
        exclude = {MongoDataAutoConfiguration.class, DataSourceAutoConfiguration.class}, 
        excludeName = {"org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration", "org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration"}
)
// 或者
@SpringBootApplication(
        exclude = {MongoDataAutoConfiguration.class, DataSourceAutoConfiguration.class}, 
        excludeName = {"org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration", "org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration"}
)
```

#### 2.6.3. 排除坐标（应用面较窄）

此时可以通过检测条件的控制来管理自动配置是否启动。例如 web 程序启动时会自动启动 tomcat 服务器，可以通过排除坐标的方式，让加载 tomcat 服务器的条件失效。

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- web起步依赖环境中，排除Tomcat起步依赖，匹配自动配置条件 -->
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!-- 添加Jetty起步依赖，匹配自动配置条件 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jetty</artifactId>
    </dependency>
</dependencies>
```

> 不过需要值得注意的是，如把 tomcat 排除掉，记得要增加一个新的可以运行的服务器依赖。


## 3. SpringBoot 项目启动流程

### 3.1. SpringBoot 应用启动流程图

![](images/20211227162258538_26216.png)

Spring Boot 的启动流程，本质就是运行一个 Spring 容器的环境。总体来说，就是先做容器的初始化，并将对应的 bean 实例初始化后放入容器。Spring Boot 初始化的参数根据参数的提供方，划分成如下3个大类，每个大类的参数又被封装了各种各样的对象，具体如下：

- 环境属性（Environment）
- 系统配置（spring.factories）
- 参数（Arguments、application.properties）

### 3.2. ConfigurableApplicationContext 

添加 Spring Boot 最基础的依赖与编写最基础的入口，启动后用于源码的断点跟踪。

```xml
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-parent</artifactId>
	<version>2.2.2.RELEASE</version>
	<relativePath>../spring-boot-project/spring-boot-parent</relativePath>
</parent>

<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter</artifactId>
	</dependency>
</dependencies>
```

```java
@SpringBootApplication
public class TestApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
}
```

启动 Spring Boot 项目，实现是执行 `SpringApplication.run` 的静态方法并返回 `ConfigurableApplicationContext` 实例

```java
public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
	return new SpringApplication(primarySources).run(args);
}
```

`ConfigurableApplicationContext` 接口类关系图

![](images/49300310220553.jpg)

### 3.3. SpringApplication

Spring Boot 启动流程的首先是通过 `SpringApplication` 类的构造函数，创建其对象，并加载各种配置信息，初始化各种配置对象；

```java
public SpringApplication(Class<?>... primarySources) {
	this(null, primarySources);
}

@SuppressWarnings({ "unchecked", "rawtypes" })
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
	// 初始化资源加载器
	this.resourceLoader = resourceLoader;
	// 断言主类不为空
	Assert.notNull(primarySources, "PrimarySources must not be null");
	// 初始化配置类的类名信息（由原来数组转换成 Set 集合）
	this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
	// 确认当前容器加载的类型
	this.webApplicationType = WebApplicationType.deduceFromClasspath();
	// getSpringFactoriesInstances 方法，用于读取资源目录 META-INF/spring.factories 文件
	// 获取 ApplicationContextInitializer 类型的实例
	setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
	// 初始化监听器，对初始化过程及运行过程进行干预
	setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
	// 初始化了引导类类名信息，备用
	this.mainApplicationClass = deduceMainApplicationClass();
}
```

#### 3.3.1. WebApplicationType 加载容器的类型

`WebApplicationType` 枚举类，定义 Spring Boot 容器的类型

```java
public enum WebApplicationType {

	/**
	 * The application should not run as a web application and should not start an
	 * embedded web server.
	 */
	NONE,

	/**
	 * The application should run as a servlet-based web application and should start an
	 * embedded servlet web server.
	 */
	SERVLET,

	/**
	 * The application should run as a reactive web application and should start an
	 * embedded reactive web server.
	 */
	REACTIVE;
	...
}
```

其中 `WebApplicationType.deduceFromClasspath()` 静态方法用于判断当前容器加载的类型

![](images/93142010238979.png)

导入不同的 starter，判断不同的类型

![](images/316312610247012.png)

#### 3.3.2. getSpringFactoriesInstances 方法

`getSpringFactoriesInstances` 是 `SpringApplication` 类的方法，在加载配置的过程中多次被调用

```java
private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
	return getSpringFactoriesInstances(type, new Class<?>[] {});
}

private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
	// 获取类加载器
	ClassLoader classLoader = getClassLoader();
	// Use names and ensure unique to protect against duplicates
	// 根据类型，获取 spring.factories 文件中配置的相关实现类全限定名称集合
	Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
	// 根据全限定名，实例化
	List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
	// 将实例化的对象排序
	AnnotationAwareOrderComparator.sort(instances);
	return instances;
}
```

`SpringFactoriesLoader.loadFactoryNames(type, classLoader)` 方法，读取资源目录中的 META-INF/spring.factories 文件。（前面自动配置原理分析章节中已经看过）

![](images/418254510239681.png)

以传入的类型为 key，获取配置中相应的 value，即该类型的所有实现类全限定名称。

![](images/547434610236236.png)

#### 3.3.3. 自定义监听器

从上面的源码可以看到，在创建 `SpringApplication` 对象中的 `setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));` 这里会进行初始化监听器，同样是读取 spring.factories 文件中配置的实现，因此使用者可以自定义监听器的实现，并将监听器类全限名称配置到 spring.factories 文件中即可

- 创建自定义监听器，需要实现 `org.springframework.context.ApplicationListener` 接口

```java
public class CustomListener implements ApplicationListener<ApplicationStartingEvent> {
	/**
	 * Handle an application event.
	 *
	 * @param event the event to respond to
	 */
	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {
		System.out.println("自定义事件处理逻辑, source: " + event.getSource());
	}
}
```

- spring.factories 文件中配置监听器映射

```properties
org.springframework.context.ApplicationListener=com.moon.springboot.listener.CustomListener
```

加载到容器中

![](images/282950811231990.png)

### 3.4. 初始化 Spring 容器

在 `SpringApplication` 对象创建并加载配置信息、初始化各种配置对象后，然后调用对象的 `run(String... args)` 方法，用于初始化容器，并得到 `ConfigurableApplicationContext` 对象，这也是核心部分

```java
public ConfigurableApplicationContext run(String... args) {
	// 设置计时器
	StopWatch stopWatch = new StopWatch();
	// 开始计时
	stopWatch.start();
	ConfigurableApplicationContext context = null;
	Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
	// 模拟输入输出信号，避免出现因缺少外设导致的信号传输失败，进而引发错误（模拟显示器，键盘，鼠标...）
	// 设置 java.awt.headless=true
	configureHeadlessProperty();
	// 获取当前注册的所有监听器
	SpringApplicationRunListeners listeners = getRunListeners(args);
	// 执行容器启动中事件的监听器，即调用 ApplicationListener 接口的 onApplicationEvent 方法
	listeners.starting();
	try {
		// 获取启动应用的参数
		ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
		// 将前期读取的数据加载成了一个环境对象 ConfigurableEnvironment，用来描述信息
		ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
		// 配置忽略的信息，暂不知道什么作用
		configureIgnoreBeanInfo(environment);
		// 初始化项目启动时的 logo
		Banner printedBanner = printBanner(environment);
		// 创建容器对象，根据前期配置的容器类型进行判定并创建
		context = createApplicationContext();
		// 读取 spring.factories 文件，获取 SpringBootExceptionReporter 类型的实例
		exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
				new Class[] { ConfigurableApplicationContext.class }, context);
		// 对容器进行设置，参数来源于前期的设定
		prepareContext(context, environment, listeners, applicationArguments, printedBanner);
		// 刷新容器环境
		refreshContext(context);
		// 刷新完毕后做后置处理
		afterRefresh(context, applicationArguments);
		// 计时结束
		stopWatch.stop();
		// 判定是否记录启动时间的日志
		if (this.logStartupInfo) {
			// 创建日志对应的对象，输出日志信息，包含启动时间
			new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
		}
		// 监听器执行了对应的操作步骤
		listeners.started(context);
		// 调用运行器
		callRunners(context, applicationArguments);
	}
	catch (Throwable ex) {
		handleRunFailure(context, ex, exceptionReporters, listeners);
		throw new IllegalStateException(ex);
	}

	try {
		// 执行容器运行中事件的监听器
		listeners.running(context);
	}
	catch (Throwable ex) {
		handleRunFailure(context, ex, exceptionReporters, null);
		throw new IllegalStateException(ex);
	}
	return context;
}
```

### 3.5. Spring Boot 监听机制

Spring Boot 启动过程由于存在着不同的处理过程阶段，如果设计接口就要设计十余个标准接口，这样对开发者不友好，同时整体过程管理分散，十余个过程在不同地方调用，管理难度大，过程过于松散。然后 Spring Boot 采用了监听器设计模式来解决此问题

#### 3.5.1. 内置监听器

Spring Boot 将自身的启动过程当成一个大的事件，该事件是由若干个小的事件组成的。例如：

- `org.springframework.boot.context.event.ApplicationStartingEvent`
  - 应用启动事件，在应用运行但未进行任何处理时，将发送 ApplicationStartingEvent
- `org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent`
  - 环境准备事件，当 Environment 被使用，且上下文创建之前，将发送 ApplicationEnvironmentPreparedEvent
- `org.springframework.boot.context.event.ApplicationContextInitializedEvent`
  - 上下文初始化事件
- `org.springframework.boot.context.event.ApplicationPreparedEvent`
  - 应用准备事件，在开始刷新之前，bean 定义被加载之后发送 ApplicationPreparedEvent
- `org.springframework.context.event.ContextRefreshedEvent`
  - 上下文刷新事件
- `org.springframework.boot.context.event.ApplicationStartedEvent`
  - 应用启动完成事件，在上下文刷新之后且所有的应用和命令行运行器被调用之前发送 ApplicationStartedEvent
- `org.springframework.boot.context.event.ApplicationReadyEvent`
  - 应用准备就绪事件，在应用程序和命令行运行器被调用之后，将发出 ApplicationReadyEvent，用于通知应用已经准备处理请求
- `org.springframework.context.event.ContextClosedEvent`
    - 上下文关闭事件，对应容器关闭

上述列出的仅仅是部分事件，当应用启动后走到某一个过程点时，监听器监听到某个事件触发，就会执行对应的事件。除了系统内置的事件处理，用户还可以根据需要自定义开发当前事件触发时要做的其他动作。

