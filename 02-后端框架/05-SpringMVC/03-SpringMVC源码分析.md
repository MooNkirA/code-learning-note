# Spring MVC 源码分析

> 最新官方文档：https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web

## 1. 概述

Spring Web MVC 是建立在 Servlet API 上的原始 Web 框架，包含在 Spring Framework 中 spring-webmvc 模块，可以称为 "Spring MVC"。

与 Spring Web MVC 并行，Spring Framework 5.0 引入了一个新 Web 框架 Spring WebFlux，具体在 spring-webflux 模块。（*此框架本笔记中不涉及，详见其他笔记*）

### 1.1. Spring MVC 时序图

![](images/20200917172707993_20103.png)

### 1.2. 官方流程图

![](images/20200917172659185_21224.png)

## 2. web 项目初始化过程分析

### 2.1. Servlet 3.0 规范加入的内容

- Servlet3.0规范提供的标准接口，在web容器启动的时候，首先触发`ServletContainerInitializer`此接口的实现类的`onStartup()`方法

```java
/* Servlet3.0规范提供的标准接口 */
public interface ServletContainerInitializer {
    /*
     * 启动容器是做一些初始化操作，例如注册Servlet、Filter、Listener等等
     */
    public void onStartup(Set<Class<?>> c, ServletContext ctx)
        throws ServletException;
}
```

- `@HandlesTypes`注解用于标识在`ServletContainerInitializer`的实现类中，指定要加载到`ServletContainerInitializer`接口实现类中的字节码，在`onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext);`方法的入参`webAppInitializerClasses`中可以获取到此注解的值

```java
/* 用于指定要加载到ServletContainerInitializer接口实现类中的字节码 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlesTypes {
    /*
     * 指定要加载到ServletContainerInitializer实现类的onStartUp方法中类的字节码。
     * 字节码可以是接口，抽象类或者普通类。
     */
    Class<?>[] value();
}
```

### 2.2. SpringMVC 框架使用 Servlet 3.0 规范

任何要使用Servlet3.0规范且脱离web.xml的配置，在使用时都必须在对应的jar包的`META-INF/services`目录创建一个名为`javax.servlet.ServletContainerInitializer`的文件，文件内容指定具体的`ServletContainerInitializer`实现类，那么，当web容器启动时就会运行这个初始化器做一些组件内的初始化工作。

![](images/20200917170758911_20768.png)

### 2.3. AbstractDispatcherServletInitializer 中的 onStartUp 方法

`AbstractDispatcherServletInitializer`类是Spring MVC提供的`WebApplicationInitializer`接口的实现抽象类。

### 2.4. 注册 DisptatcherServlet

# SpringMVC 中各组件详解及源码分析

## 1. Spring MVC 框架重要组件

框架提供组件包含：

- DispatcherServlet：前端控制器
- HandlerMapping：处理器映射器
- Handler：处理器
- HandlerAdapter：处理器适配器
- ViewResolver：视图解析器
- View：视图

**在上述的组件中：处理器映射器（HandlerMapping）、处理器适配器（HandlerAdapter）、视图解析器（ViewResolver）称为 Spring MVC 的三大组件**。其中 handler 与 view 组件是由使用者来实现

## 2. 前端控制器 DispatcherServlet

### 2.1. 简介

在 web.xml 中配置，<font color=red>**实质是一个 Servlet**</font>

作用：接收请求，响应结果。相当于转发器，中央处理器

用户请求到达前端控制器，它就相当于 MVC 模式中的 C，`DispatcherServlet` 是整个流程控制的中心，由它调用其它组件处理用户的请求，<font color=red>**`DispatcherServlet`的存在降低了组件之间的耦合性**</font>

### 2.2. 执行过程分析

#### 2.2.1. doService方法

此方法在接收到请求首先执行的方法，通过跟踪源码得知，它重写父类`FrameworkServlet`的，`FrameworkServlet`是继承了`HttpServlet`，所以它就相当于执行了Servlet中的service方法

#### 2.2.2. doDispatche方法

在doService方法执行的逻辑中，会调用doDispatche方法，此方法是处理请求分发的核心方法。它负责通过反射调用控制器方法、执行拦截器和处理结果视图

## 3. 处理器映射器 HandlerMapping

### 3.1. 作用

`HandlerMapping`负责根据用户请求找到相应的Handler（即处理器），SpringMVC提供了不同的映射器实现不同的映射方式，例如：配置文件方式，实现接口方式，注解方式等。

### 3.2. RequestMappingHandlerMapping 的执行时机

`RequestMappingHandlerMapping`是`HandlerMapping`接口的实现，是在项目启动的时候就进行

因为`DispatcherServlet`本质是一个Servlet，所以在项目启动时必定会触发init方法。通过源码可以看到，`init()`方法的具体实现在定义在`HttpServlet`抽象类中，该方法又调用子类`FrameworkServlet`的`initServletBean()`方法，该中有一个无实现的`onRefresh()`，这个是方法由子类`DispatcherServlet`来实现，方法实现如下：

```java
@Override
protected void onRefresh(ApplicationContext context) {
	initStrategies(context);
}
/**
 * Initialize the strategy objects that this servlet uses.
 * <p>May be overridden in subclasses in order to initialize further strategy objects.
 */
/* 此方法就是DispatcherServlet中的初始化方法 */
protected void initStrategies(ApplicationContext context) {
	// 初始化文件上传的处理器
	initMultipartResolver(context);
	// 初始化国际化资源的处理器
	initLocaleResolver(context);
	// 初始化主题资源处理器
	initThemeResolver(context);
	// 初始化控制器映射的处理器，重点程度【5】
	initHandlerMappings(context);
	// 初始化适配器
	initHandlerAdapters(context);
	// 初始化异常解析器
	initHandlerExceptionResolvers(context);
	initRequestToViewNameTranslator(context);
	// 初始化视图解析器
	initViewResolvers(context);
	initFlashMapManager(context);
}
```

处理控制器映射的方法源码

```java
/* 初始化处理器映射器方法 */
private void initHandlerMappings(ApplicationContext context) {
	this.handlerMappings = null;
	if (this.detectAllHandlerMappings) {
		// Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
		/*
		 * 此方法会初始化三个处理器
		 * 	RequestMappingHandlerMapping：请求映射处理器
		 * 		此对象中MappingRegistry属性（LinkedHashMap结构），就是用来存储所有@RequestMapping中定义的url与相应的方法的映射
		 * 	BeanNameUrlHandlerMapping：beanName与url映射处理器
		 * 	SimpleUrlHandlerMapping：资源处理器
		 */
		Map<String, HandlerMapping> matchingBeans =
				BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.handlerMappings = new ArrayList<>(matchingBeans.values());
			// We keep HandlerMappings in sorted order.
			AnnotationAwareOrderComparator.sort(this.handlerMappings);
		}
	}
	else {
		try {
			HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
			this.handlerMappings = Collections.singletonList(hm);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we'll add a default HandlerMapping later.
		}
	}
	// Ensure we have at least one HandlerMapping, by registering
	// a default HandlerMapping if no other mappings are found.
	if (this.handlerMappings == null) {
		this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
		if (logger.isTraceEnabled()) {
			logger.trace("No HandlerMappings declared for servlet '" + getServletName() +
					"': using default strategies from DispatcherServlet.properties");
		}
	}
}
```

## 4. 处理器适配器 HandlerAdapter

### 4.1. 作用

### 4.2. 适配器模式

适配器模式就是把一个类的接口变换成客户端所期待的另一种接口，从而使原本因接口原因不匹配而无法一起工作的两个类能够一起工作。适配类可以根据参数返还一个合适的实例给客户端。

通过`HandlerAdapter`对处理器进行执行，这是适配器模式的应用，通过扩展适配器可以对更多类型的处理器进行执行。


### 4.3. SpringMVC控制器的三种编写方式

#### 4.3.1. 使用Controller注解

```java
@Controller
public class BasicController {
    @RequestMapping("/hello")
    public String sayHello() {
        System.out.println("BasicController控制器sayHello()方法执行了...");
        return "success";
    }
}
```

#### 4.3.2. 实现Controller接口（少用）

此实现方式的，返回值也是让spring mvc框架来处理后生成的ModelAndView

```java
/* 编写SpringMVC控制器，可以实现此接口 */
@FunctionalInterface
public interface Controller {
	/* 用于处理请求并返回ModelAndView */
	@Nullable
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```

#### 4.3.3. 实现HttpRequestHandler接口（少用）

此实现方式的与实现`Controller`接口方式的区别在于，返回值是让使用者通过response来处理

```java
/* 编写SpringMVC控制器，可以实现此接口 */
@FunctionalInterface
public interface HttpRequestHandler {
	/* 用于处理器请求，并由使用者提供相应返回处理 */
	void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;
}
```

## 5. 视图解析器 ViewResovler 和 View

> 注：现在互联网项目，都不会使用直接响应视图的方式返回。都是前后端分离，将数据以流的方式返回到前端，在html中显示。所以此部分的内容很少用，只作了解即可

### 5.1. View

视图的作用是渲染模型数据，将模型里的数据以某种形式呈现给用户。

为了实现视图模型和具体实现技术的解耦，Spring在`org.springframework.web.servlet`包中定义了一个高度抽象的`View`接口。视图是无状态的，无状态是指对于每一个请求，都会创建一个`View`对象，所以他们不会有线程安全的问题。

在SpringMVC中常用的视图类型：

|   分类    |         视图类型         |                                     说明                                      |
| --------- | ----------------------- | ---------------------------------------------------------------------------- |
| URL视图    | InternalResourceView    | 将JSP或者其他资源封装成一个视图，是InternaleResourceViewResolver默认使用的视图类型 |
|           | JstlView                | 它是当我们在页面中使用了JSTL标签库的国际化标签后，需要采用的类型                    |
| 文档类视图 | AbstractPdfView         | PDF文档视图的抽象类                                                            |
|           | AbstarctXlsView         | Excel文档视图的抽象类，该类是4.2版本之后才有的。之前使用的是AbstractExcelView      |
| JSON视图   | MappingJackson2JsonView | 将模型数据封装成Json格式数据输出。它需要借助Jackson开源框架                        |
| XML视图    | MappingJackson2XmlView  | 将模型数据封装成XML格式数据。它是从4.1版本之后才加入的                             |

### 5.2. ViewResolver

`ViewResolver`负责将处理结果生成`View`视图，`ViewResolver`首先根据逻辑视图名解析成物理视图名即具体的页面地址，再生成`View`视图对象，最后对`View`进行渲染将处理结果通过页面展示给用户。视图对象是由视图解析器负责实例化。

视图解析器的作用是将逻辑视图转为物理视图，所有的视图解析器都必须实现`ViewResolver`接口

SpringMVC为逻辑视图名的解析提供了不同的策略，可以在Spring WEB上下文中配置一种或多种解析策略，并指定他们之间的先后顺序。每一种映射策略对应一个具体的视图解析器实现类。程序员可以选择一种视图解析器或混用多种视图解析器。可以通过order属性指定解析器的优先顺序，order越小优先级越高，SpringMVC会按视图解析器顺序的优先顺序对逻辑视图名进行解析，直到解析成功并返回视图对象，否则抛出`ServletException`异常。

|      分类       |          解析器类型           |                                      说明                                      |
| --------------- | ---------------------------- | ----------------------------------------------------------------------------- |
| 解析为Bean的名称 | BeanNameViewResolver         | Bean的id即为逻辑视图名称                                                        |
| 解析为URL文件    | InternalResourceViewResolver | 将视图名解析成一个URL文件，一般就是一个jsp或者html文件。文件一般都存放在WEB-INF目录中 |
| 解析指定XML文件  | XmlViewResolver              | 解析指定位置的XML文件，默认在/WEB-INF/views.xml                                  |
| 解析指定属性文件 | ResourceBundleViewResolver   | 解析properties文件                                                             |

## 6. 请求参数封装的源码分析

### 6.1. 传统表单数据封装原理(!待整理)

![](images/20200921233526749_10621.png)

### 6.2. @RequestBody注解执行原理(!待整理)

![](images/20200922082020581_18801.png)

### 6.3. @PathVariable注解实现原理(!待整理)

![](images/20200922082144603_21222.png)

## 7. 拦截器的执行时机和调用过程

### 7.1. 拦截器的执行流程图

![](images/20200922152943953_20761.jpg)

### 7.2. 拦截器的源码执行过程分析

![](images/20200922153214836_27754.png)


### 7.3. 拦截器的责任链模式

责任链模式是一种常见的行为模式。它是使多个对象都有处理请求的机会，从而避免了请求的发送者和接收者之间的耦合关系。将这些对象串成一条链，并沿着这条链一直传递该请求，直到有对象处理它为止。

- 优点：
    - 解耦了请求与处理
    - 请求处理者（节点对象）只需关注自己感兴趣的请求进行处理即可，对于不感兴趣的请求，直接转发给下一级节点对象
    - 具备链式传递处理请求功能，请求发送者无需知晓链路结构，只需等待请求处理结果
    - 链路结构灵活，可以通过改变链路结构动态地新增或删减责任
    - 易于扩展新的请求处理类（节点），符合开闭原则
- 缺点：
    - 责任链路过长时，可能对请求传递处理效率有影响
    - 如果节点对象存在循环引用时，会造成死循环，导致系统崩溃

## 8. SpringMVC中的文件上传

### 8.1. MultipartFile

#### 8.1.1. 源码

```java
/* SpringMVC中对上传文件的封装 */
public interface MultipartFile extends InputStreamSource {
	/* 获取临时文件名称 */
	String getName();

	/* 获取真实（原始）文件名称 */
	@Nullable
	String getOriginalFilename();

	/* 获取上传文件的MIME类型 */
	@Nullable
	String getContentType();

	/* 是否是空文件 */
	boolean isEmpty();

	/* 获取上传文件的字节大小 */
	long getSize();

	/* 获取上传文件的字节数组 */
	byte[] getBytes() throws IOException;

	/* 获取上传文件的字节输入流 */
	@Override
	InputStream getInputStream() throws IOException;

	/* 把上传文件转换成一个Resource对象 */
	default Resource getResource() {
		return new MultipartFileResource(this);
	}

	/* 把临时文件移动到指定位置并重命名，参数是一个文件对象 */
	void transferTo(File dest) throws IOException, IllegalStateException;

	/* 把临时文件移动到指定位置并重命名，参数是一个文件路径 */
	default void transferTo(Path dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(getInputStream(), Files.newOutputStream(dest));
	}
}
```

#### 8.1.2. commons-fileupload的实现

`MultipartFile`的实现类其中一个实现是`CommonsMultipartFile`，通过导包就看出了，此类是借助apache的commons-fileupload实现的文件上传

### 8.2. MultipartResolver

#### 8.2.1. 源码

```java
/*
 * 它是SpringMVC中文件解析器的标准
 * 通过一个接口规定了文件解析器中必须包含的方法
 */
public interface MultipartResolver {

	/* 判断是否支持文件上传 */
	boolean isMultipart(HttpServletRequest request);

	/* 解析HttpServletRequest */
	MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

	/* 删除临时文件和一些清理操作 */
	void cleanupMultipart(MultipartHttpServletRequest request);
}
```

#### 8.2.2. CommonsFileUploadResolver

`MultipartResolver`的实现类是`CommonsMultipartResolver`，此类继承抽象类`CommonsFileUploadSupport`，解析`CommonsMultipartFile`逻辑在此抽象类

```java
public class CommonsMultipartResolver extends CommonsFileUploadSupport
		implements MultipartResolver, ServletContextAware {
}
```

从导包能得知，也是借助apache的commons-fileupload实现的文件上传。具体方法作用，查看源码工程注释

