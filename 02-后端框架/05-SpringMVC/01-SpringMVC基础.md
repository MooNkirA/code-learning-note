# Spring MVC 基础

> 最新官方文档：https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web

## 1. Spring MVC 框架介绍

### 1.1. 概述

Spring Web MVC 是基于 Servlet API 上， MVC 的表现层的 Web 框架，用于 web 项目的开发，也可以称为 "Spring MVC"。是 Spring 框架的一个模块（一部分），包含在 Spring Framework 中 spring-webmvc 模块。Spring MVC 和 Spring 无需通过中间整合层进行整合。

与 Spring Web MVC 并行，Spring Framework 5.0 引入了一个新 Web 框架 Spring WebFlux，具体在 spring-webflux 模块。（*此框架本笔记中不涉及，详见其他笔记*）

> Spring MVC 的 jar 包：`spring-webmvc-x.x.x.RELEASE.jar` 或者 `spring-webmvc-x.x.x.jar`

![](images/359331311220567.png)



### 1.2. MVC 是什么(b/s系统)

mvc 是一种设计模式。模型（model） --> 视图（view） --> 控制器（controller），三层架构设计模式，主要用于实现前端页面的展现和后端业务数据处理逻辑分离

mvc 设计模式的优点：

1. 它是分层架构的设计，实现业务系统各个组件之间的解耦
2. 有利于系统的可扩展性，可维护性
3. 有利于实现系统的并行开发，提升开发效率

## 2. Spring MVC 框架重要组件

框架提供组件包含：

- DispatcherServlet：前端控制器
- HandlerMapping：处理器映射器
- Handler：处理器
- HandlerAdapter：处理器适配器
- ViewResolver：视图解析器
- View：视图

**在上述的组件中：处理器映射器（HandlerMapping）、处理器适配器（HandlerAdapter）、视图解析器（ViewResolver）称为 Spring MVC 的三大组件**。其中 handler 与 view 组件是由使用者来实现

### 2.1. 前端控制器 DispatcherServlet

Spring MVC 和其他许多 Web 框架一样，是围绕前端控制器模式设计的，`DispatcherServlet` 就是相当于一个中央处理器、转发器，作用是接收请求，响应结果。

用户请求到达前端控制器，它就相当于 MVC 模式中的 C，`DispatcherServlet` 是整个流程控制的中心，由它调用其它组件处理用户的请求，<font color=red>**`DispatcherServlet`的存在降低了组件之间的耦合性**</font>

### 2.2. 处理器映射器和处理器适配器

#### 2.2.1. 处理器映射器 HandlerMapping

HandlerMapping 是作用是负责根据用户请求 url 找到 Handler（即处理器的方法），Spring MVC 提供了不同的映射器实现不同的映射方式，例如：配置文件方式，实现接口方式，注解方式等

##### 2.2.1.1. 默认的处理器映射器

Spring MVC 提供了默认的处理器映射器，在 org.springframework.web.servlet 包下 DispatcherServlet.properties 文件中可查看相关的配置

![](images/476842014238993.png)

```properties
org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping,\
	org.springframework.web.servlet.function.support.RouterFunctionMapping
```

> 注意事项：旧版本中的默认的处理器映射器是 `org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping` 已经过时。在企业项目不推荐使用。新的版本已经换成 `RequestMappingHandlerMapping`

##### 2.2.1.2. 配置处理器映射器 (RequestMappingHandlerMapping)

旧版本 Spring MVC 中，需要手动在 xml 文件配置创建其对象实例

```xml
<!-- 配置处理器映射器(RequestMappingHandlerMapping) -->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping" />
```

> 注意事项：处理器映射器和处理器适配器必须配对使用。

#### 2.2.2. 处理器适配器 HandlerAdapter

按照指定的规则（处理器适配器 HandlerAdapter 设置的特定规则），执行 Handler 处理器的方法，这是适配器模式的应用，通过扩展适配器可以对更多类型的处理器进行执行。

##### 2.2.2.1. 默认的处理器适配器

Spring MVC 提供了默认的处理器适配器，在 org.springframework.web.servlet 包下 DispatcherServlet.properties 文件中可查看相关的配置

```properties
org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
	org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter,\
	org.springframework.web.servlet.function.support.HandlerFunctionAdapter
```

> 注意事项：旧版本中的默认的处理器映射器是 `org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter` 已经过时。在企业项目不推荐使用。新的版本已经换成 `RequestMappingHandlerAdapter`

##### 2.2.2.2. 配置处理器适配器 (RequestMappingHandlerAdapter)

旧版本 Spring MVC 中，需要手动在 xml 文件配置创建其对象实例

```xml
<!-- 配置处理器适配器(RequestMappingHandlerAdapter) -->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter" />
```

> 注意事项：处理器映射器和处理器适配器必须配对使用。

#### 2.2.3. 处理器映射器和处理器适配器同时配置方式

在 springmvc.xml 配置文件中，配置 `<mvc:annotation-driven>` 标签，表示基于注解驱动的方式配置处理器映射器、处理器适配器，相当于同时配置了 `RequestMappingHandlerMapping`/`RequestMappingHandlerAdapter`（*企业开发推荐使用*）

```xml
<mvc:annotation-driven></mvc:annotation-driven>
```

<font color=red>**注意事项：上面配置中，处理器映射器和处理器适配器必须配对使用**</font>。否则会报【HTTP Status 500 - No adapter for handler】的异常

![](images/204432014220567.jpg)

### 2.3. 处理器 Handler

编写 Handler 时按照 HandlerAdapter 的要求去完成，这样适配器才可以去正确执行 Handler

Handler 是继承 `DispatcherServlet` 前端控制器的**后端控制器**，在 `DispatcherServlet` 的控制下 Handler 对具体的用户请求进行处理。

> 由于 Handler 涉及到具体的用户业务请求，所以一般情况需要程序员根据业务需求开发 Handler。

### 2.4. 视图解析器 ViewResolver

View Resolver 作用是进行视图解析，把逻辑视图（在 controller 中设置的视图名称）解析成物理视图（在浏览器看到的实际页面，即 view）。View Resolver 首先根据逻辑视图名解析成物理视图名即具体的页面地址，再生成 View 视图对象，最后对 View 进行渲染将处理结果通过页面展示给用户。

#### 2.4.1. 默认的视图解析器

Spring MVC 提供了默认的视图解析器，在 org.springframework.web.servlet 包下 DispatcherServlet.properties 文件中可查看相关的配置

```properties
org.springframework.web.servlet.ViewResolver=org.springframework.web.servlet.view.InternalResourceViewResolver
```

> 注意事项：需要在 springmvc.xml 中配置两个属性

#### 2.4.2. 配置视图解析器 (InternalResourceViewResolver)

在 springmvc.xml 中配置视图解析器

```xml
<!-- 配置视图解析(InternalResourceViewResolver)  -->
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
	<!-- 配置视图的公共目录路径(前缀) -->
	<property name="prefix" value="/WEB-INF/jsp/"></property>
	<!-- 配置视图的扩展名称(后缀) -->
	<property name="suffix" value=".jsp"></property>
</bean>
```

在 Controller 接口中，直接返回视图的名称即可，会自动拼接上面配置的前后缀

```java
// 配置视图解析器后只需要设置视图名称即可。（原因是视图解析器进行拼接）
mav.setViewName("helloSpringMVC");
```

### 2.5. 视图view

View 是一个接口，实现类支持不同的 View 类型（jsp、freemarker、pdf...）

Spring MVC 框架提供了很多的 View 视图类型的支持，包括：jstlView、freemarkerView、pdfView 等。最常用的视图就是 jsp

一般情况下需要通过页面标签或页面模版技术将模型数据通过页面展示给用户，需要由程序员根据业务需求开发具体的页面。

## 3. Spring MVC 运行流程

Spring 的模型-视图-控制器（MVC）框架是围绕一个 `DispatcherServlet` 来设计的，这个 Servlet 会把请求分发给各个处理器，并支持可配置的处理器映射、视图渲染、本地化、时区与主题渲染等，甚至还能支持文件上传。

- 第1步：客户端发起请求到 `DispatcherServlet`（前端控制器）
- 第2步：`DispatcherServlet`（前端控制器）请求一个或多个 `HandlerMapping` 查找 Handler，可以根据 xml 配置、注解进行查找处理请求的 Controller
- 第3步：`HandlerMapping`（处理器映射器）向前端控制器返回 Handler
- 第4步：`DispatcherServlet`（前端控制器）调用 `HandlerAdapter`（处理器适配器）去执行 Handler
- 第5步：`HandlerAdapter`（处理器适配器）去执行 Handler
- 第6步：Handler 调用业务逻辑处理完成后，给 `HandlerAdapter`（处理器适配器）返回 `ModelAndView`
- 第7步：`HandlerAdapter`（处理器适配器）向 `DispatcherServlet`（前端控制器）返回 `ModelAndView`。`ModelAndView` 是 Spring MVC 框架的一个底层对象，包括 Model 和 View
- 第8步：`DispatcherServlet`（前端控制器）请求 `ViewResoler`（视图解析器）去进行视图解析，根据逻辑视图名解析成真正的视图(jsp)
- 第9步：`ViewResoler`（视图解析器）向 `DispatcherServlet`（前端控制器）返回 View
- 第10步：`DispatcherServlet`（前端控制器）进行视图渲染。视图渲染将模型数据(在 `ModelAndView` 对象中)填充到 request 域中
- 第11步：`DispatcherServlet`（前端控制器）向用户响应结果

![](images/20200918135458792_17168.png)

![](images/63723911226860.jpg)

![](images/20200918135212093_9963.jpg)

## 4. Spring MVC 入门程序（基于 xml 配置）

### 4.1. 编写入门程序步骤

1. 导入框架包
2. 准备主配置文件：springmvc.xml（不是固定，可修改）
3. 在web.xml文件配置前端控制器：DispatcherServlet
4. 准备jsp页面：hello.jsp
5. 准备控制器：HelloWolrd.java
6. 在springmvc.xml中配置组件扫描controller
7. 启动执行

### 4.2. 快速入门案例

#### 4.2.1. 创建maven项目，配置依赖

创建 war 类型的 maven 项目，配置 pom.xml 文件，加入依赖。除了需要加入 Spring MVC 相关依赖外，还需要配置jdk和tomcat7插件(用于打包部署，如果直接使用 idea 运行测试，则也可省略)

> 注：快速入门示例只需要依赖 spring-webmvc 依赖，因为此依赖包含了 Spring 的 aop、beans、context、core、expression、web 等

```xml
<modelVersion>4.0.0</modelVersion>
<groupId>com.moon</groupId>
<artifactId>01-spring-mvc-quickstart</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging>

<properties>
	<!-- spring版本号 -->
	<spring.version>5.3.19</spring.version>
	<!-- jstl标签版本 -->
	<jstl.version>1.2</jstl.version>
</properties>

<dependencies>
	<!-- springmvc 依赖包 -->
	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-webmvc</artifactId>
		<version>${spring.version}</version>
	</dependency>
	<!-- JSTL标签类 -->
	<dependency>
		<groupId>jstl</groupId>
		<artifactId>jstl</artifactId>
		<version>${jstl.version}</version>
	</dependency>
</dependencies>

<build>
	<pluginManagement>
		<!-- 设置jdk插件 -->
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.2</version>
				<configuration>
					<source>1.8</source>
					<target>1.8</target>
					<encoding>UTF-8</encoding>
					<showWarnings>true</showWarnings>
				</configuration>
			</plugin>
			<!-- 设置maven tomcat插件 -->
			<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
				<artifactId>tomcat7-maven-plugin</artifactId>
				<version>2.2</version>
				<configuration>
					<!-- 指定端口号 -->
					<port>8080</port>
					<!-- 指定请求路径 -->
					<path>/</path>
				</configuration>
			</plugin>
		</plugins>
	</pluginManagement>
</build>
```

#### 4.2.2. 创建相关的配置文件

- 创建`springmvc.xml`文件，springmvc框架的主配置文件（文件名称是可以修改的），配置组件扫描controller

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/mvc 
		http://www.springframework.org/schema/mvc/spring-mvc.xsd
		http://www.springframework.org/schema/context 
		http://www.springframework.org/schema/context/spring-context.xsd">
	<!-- 配置开启扫描controller -->
	<context:component-scan base-package="com.moon.controller" />
</beans>
```

- 创建`web.xml`文件，配置前端控制器DispatcherServlet

```xml
<!-- 配置前端控制器DispatcherServlet -->
<servlet>
	<servlet-name>dispatcherServlet</servlet-name>
	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

	<!-- 配置初始化加载springMVC主配置文件 -->
	<init-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:springmvc.xml</param-value>
	</init-param>

	<!-- 配置什么时候加载前端控制器，说明：
	  	 1.配置大于等于0的整数，表示web服务器启动的时候加载
	  	 2.配置小于0的整数，表示在第一次请求到达的时候加载
	-->
	<load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
	<servlet-name>dispatcherServlet</servlet-name>
	<!-- 配置拦截的url，说明：
		1.*.do，表示以.do结尾的请求，进入前端控制器
		2./，表示所有请求都进入前端控制器
	 -->
	<url-pattern>*.do</url-pattern>
</servlet-mapping>
```

#### 4.2.3. 创建控制器（controller）

相当于struts2框架中的action

```java
// 使用注解让spring容器管理
@Controller
public class DemoController {
	/*
	 * @RequestMapping：配置请求url，
	 * 	当请求的url为/hello.do，执行当前方法
	 * 
	 * ModelAndView：模型和视图。
	 * 	用于设置响应的模型数据;
	 * 	用于设置响应的视图
	 */
	@RequestMapping("/hello.do")
	public ModelAndView hello() {
		// 1.创建ModelAndView对象
		ModelAndView mav = new ModelAndView();
		/*
		 * 2.设置响应的模型与视图 
		 * addObject方法：设置响应的模型数据
		 *	attributeName参数：模型的名称
		 * 	attributeValue参数：模型数据
		 */
		mav.addObject("hello", "springMVC第1次测试使用");
		/*
		 * 3.设置响应的视图
		 * setViewName方法：设置响应的视图的名称
		 * 	viewName参数：视图的名称(页面的物理路径)
		 */
		mav.setViewName("/WEB-INF/jsp/helloSpringMVC.jsp");
		// 4.返回模型视图对象
		return mav;
	}
}
```

#### 4.2.4. 创建跳转的视图页面

创建`/WEB-INF/jsp/helloSpringMVC.jsp`文件

```jsp
<!-- 读取springMVC返回模型的数据 -->
<h1 style="color: red">${hello}</h1>
```

最后运行项目查看效果

## 5. DispatcherServlet 前端控制器配置

DispatcherServlet 和其他 Servlet 一样，<font color=red>**实质是一个 Servlet**</font>。需要通过使用 Java 编程式配置或在 web.xml 中根据 Servlet 规范进行声明和映射。通过配置来绑定请求映射、视图解析、异常处理等方面的组件。





















## 6. 拦截器

### 6.1. 拦截器介绍

拦截器相当于servlet中过滤器（filter）。可以对处理器方法执行预处理（在处理器方法执行前执行），可以对处理器方法执行后处理（在处理器方法执行后执行）

### 6.2. HandlerInterceptor 接口方法说明

```java
public interface HandlerInterceptor {
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
	}

	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}
}
```

- `preHandle`方法（通常关注此方法）：在处理器方法执行前，在响应jsp页面前执行，执行预处理。返回布尔类型的值，返回true，继续执行；返回false，终止执行。在企业项目中，可以在这个方法实现用户是否登录校验，是否有权限操作的校验。
- `postHandle`方法：在处理器方法执行后，在响应jsp页面前执行，执行后处理。企业项目中，可以在这个方法设置页面的公共模型数据，比如页面的头部信息，尾部信息。
- `afterCompletion`方法：在处理器方法执行后，在jsp页面响应后执行，执行后处理。在企业项目中，可以在这个方法实现用户访问日志的记录。

### 6.3. 自定义拦截器（基于xml配置文件）

自定义拦截器需要实现`HandlerInterceptor`接口，此接口比较特别有三个方法，都为默认方法，所以自定义拦截器时，可以选择性重写此三个方法即可

#### 6.3.1. 创建拦截器

```java
public class MyInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("preHandle方法执行中......");
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		System.out.println("postHandle方法执行中......");
		// 使用模型视图对象赋值
		modelAndView.addObject("message", "我是postHandle方法。。。");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("afterCompletion方法执行中......");
	}
}
```

#### 6.3.2. 配置拦截器

在springmvc.xml总配置文件中配置拦截器步骤：

1. 配置拦截器使用`<mvc:interceptors>`标签，子标签可以配置多个拦截器
2. 子标签`<mvc:interceptor>`标签用于配置一个拦截器
3. 在`<mvc:interceptor>`标签中配置`<mvc:mapping path=""/>`，配置拦截的url规则。
    - 拦截单个url：`/xxxx.do`
    - 拦截一组url：`/xxx/**`，表示拦截以`/xxx`开头的所有请求
4. 在`<mvc:interceptor>`标签中`<bean>`标签，创建自定义拦截器对象

```xml
<!-- 6.配置自定义拦截器 -->
<mvc:interceptors>
	<!-- 配置自定义拦截器1 -->
	<mvc:interceptor>
		<!-- 配置拦截的url规则，说明：
				1.拦截单个url：/interceptor.do，表示拦截请求/interceptor.do
				2.拦截一组url：/inter/**，表示拦截以/inter开头的请求
					如：/inter/user.do
					   /inter/user/hello.do
		 -->
		 <!-- 拦截所有请求 -->
		 <!-- <mvc:mapping path="/**"/> -->
		 <!-- 拦截单个请求 -->
		 <mvc:mapping path="/interceptor.do"/>
		 <!-- 配置自定义拦截器对象 -->
		 <bean class="com.moon.ssm.interceptor.MyInterceptor"></bean>
	</mvc:interceptor>
</mvc:interceptors>
```

#### 6.3.3. 自定义拦截器执行测试

- 测试拦截器方法

```java
/**
 * 拦截器测试专用
 * 	访问url：http://127.0.0.1:8080/ssm/interceptor.do
 */
@RequestMapping("/interceptor.do")
public String testInterceptor(Model model){
	System.out.println("测试拦截器--testInterceptor方法执行中......");
	model.addAttribute("message1", "我是testInterceptor方法......");
	return "inter/interceptor";
}
```

- 执行结果说明执行的顺序

![](images/20200922100149890_14095.jpg)

1. 执行拦截器的preHandle方法
2. 执行处理器的testInterceptor方法
3. 执行拦截器的postHandle方法
4. 响应jsp页面
5. 执行拦截器的afterCompletion方法

如果设置<font color=red>**preHandle方法返回值为false**</font>，则只会执行preHandle方法，终止执行。请求的方法都被拦截，不再执行

![](images/20200922100757124_22779.jpg)

### 6.4. 自定义拦截器（基于纯注解方式）

此部分内容详情《02-SpringMVC注解汇总.md》

### 6.5. 自定义多个拦截器

定义多个拦截器，测试拦截器执行的顺序

![](images/20200922101051530_24002.jpg)

#### 6.5.1. 配置多个拦截器

修改springmvc.xml文件

```xml
<!-- 6.配置自定义拦截器 -->
<mvc:interceptors>
	<!-- 配置自定义拦截器1 -->
	<mvc:interceptor>
		 <mvc:mapping path="/interceptor.do"/>
		 <bean class="com.moon.ssm.interceptor.MyInterceptor"></bean>
	</mvc:interceptor>

	<!-- 配置自定义拦截器2 -->
	<mvc:interceptor>
		<mvc:mapping path="/interceptor.do"/>
		<bean class="com.moon.ssm.interceptor.MyInterceptor2"></bean>
	</mvc:interceptor>
</mvc:interceptors>
```

#### 6.5.2. 多个拦截器的执行顺序测试

- 测试拦截器1返回true，拦截器2返回true

![](images/20200922101340052_27005.jpg)

执行结果说明：

1. 拦截器preHandle方法，按照配置的顺序执行
2. 拦截器postHandle方法，按照配置的逆序执行
3. 拦截器afterCompletion方法，按照配置的逆序执行

- 测试拦截器1返回true，拦截器2返回false

![](images/20200922101431122_11019.jpg)

执行结果说明：

1. 拦截器的afterCompletion方法，只要当前拦截器返回true，就可以得到执行。

### 6.6. 拦截器应用案例

#### 6.6.1. 案例需求

1. 访问商品列表数据，需要判断用户是否登录
2. 如果用户已经登录，直接让他访问商品列表
3. 如果用户未登录，先去登录页面进行登录，成功登录以后再访问商品列表
- 注：本demo只是模拟用户输入用户名和密码，没有进行数据库的校验，没有创建用户对象

#### 6.6.2. 准备用户登录页面login.jsp

```jsp
<form id="userForm"
	action="${pageContext.request.contextPath }/user/login.do"
	method="post">
	<table width="100%" border=1>
		<tr>
			<td>用户名</td>
			<td><input type="text" name="username" value="" /></td>
		</tr>
		<tr>
			<td>密码</td>
			<td><input type="password" name="userpwd" value="" /></td>
		</tr>
		<tr>
			<td colspan="2" align="center"><input type="submit" value="提交" />
			</td>
		</tr>
	</table>
</form>
```

#### 6.6.3. 用户登陆控制层方法

UserController.java编写跳转到登陆页面方法与登陆方法

涉及小知识：

- 如果是跨模块访问的话，使用绝对路径；因为不同模块的命名空间可能会不一样。如：用户模块`@Requestmapping("user")`和商品模块`@Requestmapping("item")`
- <font color=red>**如果是当前模块之间的访问，使用相对路径**</font>

```java
// 使用注解让spring容器管理
@Controller
// 使用命名空间
@RequestMapping("/user")
public class UserController {
	/**
	 * 1.跳转到登陆页面
	 * 	执行的url：127.0.0.1:8080/ssm/user/toLogin.do
	 */
	@RequestMapping("/toLogin.do")
	public String toLogin() {
		// 返回跳转登陆页面视图
		return "user/login";
	}
	/**
	 * 2.实现用户登录，登录跳转到
	 */
	@RequestMapping("login.do")
	public String login(String username, String userpwd, HttpSession session) {
		// 1.判断请求提交的参数,判断用户输入用户名和密码是否空
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(userpwd)) {
			//  用户登陆成功，将用户名放到域中
			session.setAttribute("user", username);
		}else {
			// 用户名或者密码为空，登陆失败。跳转到登陆页面
			return "user/login";
		}
		/*
		 * 2.成功登陆后，跳转访问商品列表
		 * 	因为用户名放到session域中，所有重定向即可
		 * 
		 * 相对路径与绝对路径：
		 * 		当前路径：http://127.0.0.1:8080/ssm/user/login.do
		 * 		1.不加斜杠是相对路径，相对于当前路径,下一步访问的路径：
		 * 			http://127.0.0.1:8080/ssm/user/+目标url
		 * 		2.加上斜杠是绝对路径，下一步访问的路径：
		 * 			http://127.0.0.1:8080/ssm/+目标url
		 */
		return "redirect:/queryItem.do";
	}
}
```

#### 6.6.4. 用户登陆拦截器

创建LoginInterceptor拦截器

```java
/**
 * preHandle方法在请求方法前执行，对用户登陆验证
 */
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	// 1.获取session对象
	HttpSession session = request.getSession();
	// 2.从session域中获取用户数据
	if(session.getAttribute("user") == null) {
		// 2.1 用户没有登陆，跳转到登陆页面
		System.out.println("用户没有登陆..."); // 用于测试打印
		response.sendRedirect(request.getContextPath() + "/user/toLogin.do");
		return false;
	}
	// 2.2 用户已登陆登录，直接放行
	System.out.println("用户已经登陆。。。放行。。。"); // 用于测试打印
	return true;
}
```

#### 6.6.5. 配置登陆拦截器

修改springmvc.xml配置文件

```xml
<!-- 6.配置自定义拦截器 -->
<mvc:interceptors>
	<!-- ...省略其他拦截器... -->
	<!-- 配置登陆拦截器 -->
	<mvc:interceptor>
		<!-- 拦截查询商品列表请求url -->
		<mvc:mapping path="/queryItem.do"/>
		<!-- 配置登陆拦截器对象 -->
		<bean class="com.moon.ssm.interceptor.LoginInterceptor"></bean>
	</mvc:interceptor>
</mvc:interceptors>
```

# SpringMVC 其他扩展知识整理

## 1. 名词解释

### 1.1. XxxVo 包装类

以Vo结尾的类，一般用于封装值的实体类。(Vo:value Object)

### 1.2. 相对路径与绝对路径

当前路径：http://127.0.0.1:8080/ssm/user/login.do

1. 不加斜杠是相对路径，相对于当前路径，下一步访问的路径：`http://127.0.0.1:8080/ssm/user/+目标url`
2. 加上斜杠是绝对路径，下一步访问的路径：`http://127.0.0.1:8080/ssm/+目标url`

## 2. Spring MVC 与 struts2 的区别

**相同点**：都是基于mvc的表现层框架，都用于web项目的开发

**不同点**：

1.	前端控制器不一样。
    - springmvc的前端控制器是servlet（DispatcherServlet）
    - struts2的前端控制器是filter（StrutsPrepareAndExecutorFilter）
2.	请求参数接收方式不一样。
    - springmvc是使用方法的形参接收请求的参数，基于方法的开发，线程安全，可以设计为单例或者多例模式的开发，推荐使用单例模式的开发，执行效率会更高（默认就是单例模式开发）。
    - strut2是使用类的成员变量来接收请求的参数数据，基于类的开发，是线程不安全的，只能设计为多例模式的开发。
    - 原因：springmvc传递参数操作的是方法的形参，多个线程操作不会影响到另一个线程。但strut2传递封装参数操作的是成员变量，但一个线程改变了变量的值，会影响到另一个线程的操作时变量的值。所以会出现线程不安全的问题，所以struts2框架的action都是设计成单例模式
3.	开发的方式不同
    - springmvc基于方法开发的。springmvc将url和controller里的方法映射。映射成功后springmvc生成一个Handler对象，对象中只包括了一个method。方法执行结束，形参数据销毁。springmvc的controller开发类似web service开发。
    - struts2基于类开发的。
4.	与spring整合不一样。
    - springmvc框架本身就是spring框架的一部分，不需要整合。





