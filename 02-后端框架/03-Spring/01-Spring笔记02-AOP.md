# Spring AOP（面向切面编程）

## 1. AOP 概述

AOP：全称是 Aspect Oriented Programming。即：面向切面编程

### 1.1. AOP 作用与实现原理

它就是把程序重复的代码抽取出来，在需要执行的时候，使用**动态代理**的技术，在不修改源码的基础上，对已有方法进行增强

**AOP的作用**：在程序运行期间，不修改源码对已有方法进行增强

**AOP的优势**：

- 减少重复代码
- 提高开发效率
- 维护方便

**AOP实现方式**：基于动态代理技术

### 1.2. 动态代理特点

- **特点**：字节码随用随创建，随用随加载。
- **分类**：
    1. 基于接口的动态代理
    2. 基于子类的动态代理
- **作用**：不修改源码的基础上对方法增强

动态代理与静态代理的区别是：静态代理是字节码一上来就创建好，并完成加载。

**装饰者模式就是静态代理的一种体现。**

### 1.3. 动态代理常用的有两种方式

#### 1.3.1. 基于接口的动态代理【推荐】

JDK 官方有一套动态代理的实现，使用要求是被代理类最少实现一个接口。动态代理主要类是 `Proxy`

```java
public class Proxy implements java.io.Serializable {
    ...
}
```

创建代理对象的方法：

```java
@CallerSensitive
public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException
```

- 方法参数`ClassLoader loader`：类加载器，用于加载代理对象的字节码的。和被代理对象使用相同的类加载器即可，固定写法。
- 方法参数`Class<?>[] interfaces`：被代理类实现的所有接口的字节码数组，用于给代理对象提供方法，和被代理对象具有相同的方法。也是有以下的固定写法。
    - 如果被代理类是一个普通类：`被代理类对象.getClass().getInterfaces();`
    - 如果被代理类是一个接口：`new Class[]{被代理类.class}`。
- 方法参数`InvocationHandler h`：要增强的方法。此处是一个接口，需要提供它的实现类。通常写的是匿名内部类，增强的代码谁用谁写。

基础使用示例如下：

```java
/**
 * 使用JDK官方的Proxy类创建代理对象
 */
public class Client_Proxy {
	public static void main(String[] args) {
		// 获取接口实现类
		IActor actor = new ActorImpl();

		System.out.println("=============没有使用动态代理模式前=============");
		actor.basicAct(108.89F);
		actor.wonderfulAct(3000.1F);
		System.out.println("=============没有使用动态代理模式后=============");

		// 获取代理
		IActor proxy = (IActor) Proxy.newProxyInstance(ActorImpl.class.getClassLoader(),
				ActorImpl.class.getInterfaces(), new InvocationHandler() {
					// 重写拦截的方法
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// 获取调用方法的名字
						String mothodName = method.getName();
						// 获取调用方法的参数
						float money = (float) args[0];

						// 开始判断
						if ("basicAct".equals(mothodName)) {
							// 对象调用了basicAct方法
							if (money > 2000) {
								// 满足条件才执行方法
							  // 注：此处直接引用外面的IActoer对象，如果是在jdk1.7，则前端创建的对象需要使用final关键字修饰
								proxy = method.invoke(actor, money / 2);
							}
						} else if ("wonderfulAct".equals(mothodName)) {
							// 对象调用了wonderfulAct方法
							if (money > 5000) {
								// 满足条件才执行方法
								proxy = method.invoke(actor, money / 2);
							}
						}
						// 真实方法返回值
						return proxy;
					}
				});

		// 使用代理调用方法
		proxy.basicAct(1003F);
		proxy.wonderfulAct(6234F);
	}
}

/**
 * 接口实现类
 */
public class ActorImpl implements IActor {

	@Override
	public void basicAct(float money) {
		System.out.println("拿到 " + money + " 元，开始基本的表演!!");
	}

	@Override
	public void wonderfulAct(float money) {
		System.out.println("拿到 " + money + " 元，开始精彩的表演!!");
	}
}
```

#### 1.3.2. 基于子类的动态代理

- 提供者：第三方的 cglib，在使用时需要先导包(maven工程导入坐标即可)。*如果报 asmxxxx 异常，缺少jar包，需要导入jar包：`asm.jar`和`cglib-2.1.3.jar`。*
- 使用要求：被代理类不能用 final 修饰的类（最终类）
- 涉及的类：`Enhancer`
- 涉及的方法：`create(Class arg, Callback arg1)`
    - 参数`Class`：与被代理对像的字节码对象。可以创建被代理对象的子类，还可以获取被代理对象的类加载器。
    - 参数`CallBack`：是接口，里面写的也是增加的策略，要使用一个子接口：`MethodInterceptor`。通常都是写一个接口的实现类或者匿名内部类。

- Callback中没有任何方法，所以一般使用它的子接口，即使用内部类创建`MethodInterceptor`对象，重写`intercept()`方法
- 涉及的需要重写的拦截方法：`intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)`
    - 此方法也具有拦截功能
    - 前面三个参数与基于接口的动态代理的`InvocationHandler`中的`invoke`方法的参数一模一样
    - `proxy`:代理对象的引用【一般不用】
    - `method`:拦截的方法
    - `args`:拦截的方法中的参数
    - `methodProxy`: 方法代理对象的引用【一般不用】

```java
/**
 * 使用cglib，基于子类的动态代理
 */
public class Client_Cglib {
    public static void main(String[] args) {
        // 获取被代理对象
        Actor actor = new Actor();

        /**
         * 来自cglib 要求： 被代理对象不能是最终类
         * 涉及的类：Enhancer
         * 涉及方法：create
         * 涉及参数：
         *        Class : 与被代理对像的字节码对象
         *        CallBack：接口来的，里面写的也是策略
         *                  要使用一个子接口：MethodInterceptor
         */
        Actor cglibActor = (Actor) Enhancer.create(actor.getClass(), new MethodInterceptor() {
            /**
             * 此方法也具有拦截功能
             *
             * 前面三个参数与基于接口的动态代理的InvocationHandler中的invoke方法的参数一模一样
             *  参数解释：
             *          proxy:代理对象的引用【一般不用】
             *          method:拦截的方法
             *          args: 拦截的方法中的参数
             *          methodProxy : 方法代理对象的引用【一般不用】
             */
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                // 获取执行的方法名
                String methodName = method.getName();
                // 获取调用的方法的参数，知道只有一个参数
                float money = (float) args[0];
                // 定义返回对象
                Object result = null;

                // 判断执行的方法
                if ("basicAct".equals(methodName)) {
                    // 对象调用了basicAct方法
                    if (money > 1000F) {
                        // 满足条件才执行方法
                        result = method.invoke(actor, money / 2);
                    }
                } else if ("wonderfulAct".equals(methodName)) {
                    // 对象调用了wonderfulAct方法
                    if (money > 2000F) {
                        // 满足条件才执行方法
                        result = method.invoke(actor, money / 4);
                    }
                }
                // 返回执行方法后的对象
                return result;
            }
        });

        // 使用代理调用方法
        cglibActor.basicAct(1200F);
        cglibActor.wonderfulAct(4290F);
    }
}

public class Actor {
    public void basicAct(float money) {
        System.out.println("拿到 " + money + " 元，开始基本的表演!!");
    }

    public void wonderfulAct(float money) {
        System.out.println("拿到 " + money + " 元，开始精彩的表演!!");
    }
}
```



### 1.4. AOP 相关术语

- **Joinpoint(连接点)**：是指那些被拦截到的点，即目标对象中所有的方法或者拦截的具体某个方法。在spring中，这些点指的是方法，因为spring只支持方法类型的连接点。
- **Pointcut(切入点)**：是指要对哪些 Joinpoint 进行拦截的定义。即真正需要增强的方法的集合，也可以理解为连接点（Joinpoint）的集合。
- **Advice(通知/增强)**：
    - 通知是指拦截到Joinpoint之后所要做的事情就是通知。<font color=red>**即要增强的功能代码**</font>
    - 通知的类型（<font color=red>**可以理解成拦截到的方法，什么时候进行增强**</font>），主要以下几种类型：
        - *前置通知：在切入点之前执行*
        - *后置通知：在切入点之后执行*
        - *异常通知：切入点出现异常才执行*
        - *最终通知：切入点不管有无异常都会执行*
        - *环绕通知：在方法之前和之后执行*
- **Introduction(引介)**：是一种特殊的通知在不修改类代码的前提下，Introduction 可以在运行期为类动态地添加一些方法或 Field
- **Target(目标对象)**：代理的目标对象
- **Weaving(织入)**：是指把增强应用到目标对象来创建新的代理对象的过程。spring 采用动态代理织入，而 AspectJ 采用编译期织入和类装载期织入
- **Proxy（代理）**：一个类被 AOP 织入增强后，就产生一个结果代理类
- **Aspect(切面)**：是切入点(Pointcut)和通知（引介）(Advice)的结合。把增强功能应用到具体方法上面（即将增强用到切入点的过程），这过程称为切面

![AOP术语图解](images/20190402102724932_294.jpg)

## 2. Spring 的 AOP 概述

### 2.1. Spring 中的 AOP 关于两种代理方式的选择

在 spring 中，框架会根据目标类是否实现了接口来决定采用哪种动态代理的方式

#### 2.1.1. JDK 动态接口代理

JDK 动态代理主要涉及到 java.lang.reflect 包中的两个类：`Proxy` 和 `InvocationHandler`。**`InvocationHandler` 是一个接口，通过实现该接口定义横切逻辑，并通过反射机制调用目标类的代码，动态将横切逻辑和业务逻辑编制在一起。`Proxy` 利用 `InvocationHandler` 动态创建一个符合某一接口的实例，生成目标类的代理对象。**

#### 2.1.2. CGLib 动态代理

CGLib 全称为 Code Generation Library，是一个强大的高性能，高质量的代码生成类库，**可以在运行期扩展 Java 类与实现 Java 接口**，CGLib 封装了 asm，可以再运行期动态生成新的 class。**和 JDK 动态代理相比较：JDK 创建代理有一个限制，就是只能为接口创建代理实例，而对于没有通过接口定义业务方法的类，则可以通过 CGLib 创建动态代理**。

### 2.2. Spring 的 AOP 操作

Spring 里面进行 AOP 操作是使用 aspectj 实现，但 AspectJ 不是 spring 的组成部分，只是一起使用进行 aop 操作而已。使用 AspectJ 实现 aop 的有以下两种方式：

1. 基于 aspectj 的xml配置
2. 基于 aspectj 的注解方式

Spring 的 aop 操作基本 jar 包：

- spring-aop-x.x.x.RELEASE.jar
- spring-aspects-x.x.x.RELEASE.jar

aop 相关的 jar 包

- aopalliance-x.x.jar
- aspectjweaver-x.x.x.jar

### 2.3. Spring AOP 总结

#### 2.3.1. 使用 spring 中的 AOP 要明确的事

开发阶段（由开发者完成）

- 编写核心业务代码（开发主线）：大部分程序员来做，要求熟悉业务需求。
- 把公用代码抽取出来，制作成通知。（开发阶段最后再做）：AOP 编程人员来做。
- 在配置文件中，声明切入点与通知间的关系，即切面：AOP 编程人员来做。

运行阶段（由 Spring 框架完成的）

- Spring 框架监控切入点方法的执行。一旦监控到切入点方法被运行，使用代理机制，动态创建目标对象的代理对象，根据通知类别，在代理对象的对应位置，将通知对应的功能织入，完成完整的代码逻辑运行

#### 2.3.2. Spring AOP 核心要素总结

- 在Spring框架中，**Aspect(切面)会封装成`Advisor`**，并且必须包含`Pointcut`(切入点)和`Advice`(增强)两个要素
- **Pointcut(切入点)**的作用是：<font color=red>**匹配、拦截**</font>，`ClassFilter`是用于类的拦截；`MethodMatcher`是用过匹配需要增强的方法。主要是用在以下两个节点：
    1. <font color=red>**初始化时，校验相应的类上是否有切面，并生成代理**</font>
    2. <font color=red>**当代理对象调用方法的时候，进行匹配拦截**</font>
- **Advice(增强)**就是具体的增强的逻辑

### 2.4. 扩展 - AOP 其他实现方式

#### 2.4.1. 基于 ajc 编译器

- <font color=red>**编译器增强能突破代理仅能通过方法重写增强的限制：可以对构造方法、静态方法等实现增强**</font>
- 这种使用编译器修改 class 实现增强，<font color=red>**需要使用 aspectj-maven-plugin 插件进行编译**</font>

引入依赖，基于 ajc 编译器方式实现 AOP，不依赖于 Spring。

```xml
<dependencies>
    <!-- AspectJ 依赖 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
    </dependency>
    <!-- 扩展内容：基于 ajc 编译器与基于 agent 类加载实现的 AOP 的依赖 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
    </dependency>

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.14.0</version>
            <configuration>
                <complianceLevel>1.8</complianceLevel>
                <source>8</source>
                <target>8</target>
                <showWeaveInfo>true</showWeaveInfo>
                <verbose>true</verbose>
                <Xlint>ignore</Xlint>
                <encoding>UTF-8</encoding>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <!-- use this goal to weave all your main classes -->
                        <goal>compile</goal>
                        <!-- use this goal to weave all your test classes -->
                        <goal>test-compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

准备被增强的测试类

```java
public class AjcCompilerDemo {

    public AjcCompilerDemo() {
        System.out.println("AjcCompilerDemo 的构造方法执行了...");
    }

    public void normalMethod() {
        System.out.println("AjcCompilerDemo 类的普通方法执行了...");
    }

    public static void staticMethod() {
        System.out.println("AjcCompilerDemo 类的静态方法执行了...");
    }
}
```

编写切面类，使用 `@Aspect` 注解标识当前类为切面类，定义相应切面方法

```java
@Aspect // 注意此切面与常规使用 Spring Aop 不一样，此切面不需要依赖 Spring 管理
public class AjcCompilerAspect {

    @Before("execution(* com.moon.springsample.service.AjcCompilerDemo.normalMethod())")
    public void beforeNormal() {
        System.out.println("基于 ajc 编译器实现的 AOP 切面对普通方法前置增强");
    }

    @Before("execution(* com.moon.springsample.service.AjcCompilerDemo.staticMethod())")
    public void beforeStatic() {
        System.out.println("基于 ajc 编译器实现的 AOP 切面对静态方法前置增强");
    }

    // TODO: 构造方法的切面暂时不知道如何写
}
```

测试程序

```java
@Test
public void testAjcCompilerAop() {
    // 不依赖 Spring，通过修改编译的文件实现
    AjcCompilerDemo ajcCompilerDemo = new AjcCompilerDemo();
    // 3.执行普通方法，观察是否有增强
    ajcCompilerDemo.normalMethod();
    // 4.执行静态方法，观察是否有增强
    AjcCompilerDemo.staticMethod();
}
```

> 注意
>
> - 版本选择 java 8，因为目前的 aspectj-maven-plugin 1.14.0 最高只支持到 java 16
> - 一定要用 maven 的 compile或者test 来编译，直接使用 idea 不会调用 ajc 编译器

测试运行结果：

```
AjcCompilerDemo 的构造方法执行了...
基于 ajc 编译器实现的 AOP 切面对普通方法前置增强
AjcCompilerDemo 类的普通方法执行了...
基于 ajc 编译器实现的 AOP 切面对静态方法前置增强
AjcCompilerDemo 类的静态方法执行了...
```

查看编译后的 class 文件，实际是编译时直接对被增强的方法进行了修改

![](images/313363420220561.png)

#### 2.4.2. (待整理)基于 agent 类加载






## 3. 基于纯 XML 的 AOP 配置和使用

### 3.1. 配置步骤

#### 3.1.1. 第1步 添加相关的依赖(或拷贝jar包到lib目录)

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>${spring.version}</version>
</dependency>
```

#### 3.1.2. 第2步 创建 spring 的配置文件并导入约束

- 导入约束时需要多导入一个**aop名称空间下的约束**。
- 导入schema文件：【spring-aop-4.2.xsd】
- 约束文件所在路径：
    - `根目录\schema\aop`
- 约束文件文档：
    - 根目录`\docs\spring-framework-reference\html\xsd-configuration.html`
    - 打开文件在html，约束位置【40.2.7 the aop schema】
- 约束内容（黄字为新增内容）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
					http://www.springframework.org/schema/beans/spring-beans.xsd
					http://www.springframework.org/schema/aop
					http://www.springframework.org/schema/aop/spring-aop.xsd">
</beans>
```

#### 3.1.3. 第3步 将相关需要创建对象资源配置到 spring 容器中
#### 3.1.4. 第4步 制作通知类（增强类）

编写用于增强业务逻辑层核心方法相关功能的类。

#### 3.1.5. 第5步 配置bean.xml文件，设置通知类执行方式

1. 使用`<bean>`标签配置通知(增强)类给spring管理
2. 使用 `aop:config` 声明 aop 配置
3. 使用 `aop:pointcut` 配置切入点表达式（可以写在切面标签内外都可以，但必须在配置通知前）
4. 使用 `aop:aspect` 配置切面
5. 使用 `aop:before/after-returning/after-throwing/after/around` 配置通知方式

注：AOP的约束提醒必需配置两个xsd约束（beans-4.2.xsd和tool-4.2.xsd）

![](images/20190402123359838_7330.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
							http://www.springframework.org/schema/beans/spring-beans.xsd
							http://www.springframework.org/schema/aop
							http://www.springframework.org/schema/aop/spring-aop.xsd ">
	<!-- 配置需要创建对象给spring管理 -->
	<bean id="customerService" class="com.moonzero.service.impl.CustomerServiceImpl"></bean>

	<!-- 配置通知类给spring管理 -->
	<bean id="logger" class="com.moonzero.utils.Logger"></bean>

	<!-- 声明开始aop的配置 -->
	<aop:config>
		<!-- 配置的代码都写在此处 -->

		<!--
			1. 配置切面：此标签要出现在aop:config内部
				id：给切面提供一个唯一标识
				ref：引用的是通知类的 bean 的 id
		 -->
		<aop:aspect id="logActive" ref="logger">
			<!-- 配置通知的类型写在此处 -->

			<!--
				2. 配置切入点 aop:pointcut 配置切入点表达式(写在切面aspect内外都可以)
					expression：用于定义切入点表达式,切入点表达式都写在 execution() 中。
					id：用于给切入点表达式提供一个唯一标识。
				表达式语法：execution([修饰符] 返回值类型 包名.类名.方法名(参数))
			-->
			<!-- 全匹配方式，public可以省略 -->
			<aop:pointcut expression="execution(public void com.moonzero.service.impl.CustomerServiceImpl.saveCustomer())" id="pt1"/>
			<!-- 使用通配方式 -->
			<!-- <aop:pointcut expression="execution(* com.moonzero.service.impl.CustomerServiceImpl.updateCustomer(..))" id="pt2"/> -->
			<!-- <aop:pointcut expression="execution(* *.*.*.*.CustomerServiceImpl.updateCustomer(..))" id="pt2"/> -->
			<!-- <aop:pointcut expression="execution(* *..CustomerServiceImpl.updateCustomer(..))" id="pt2"/> -->
			<!-- <aop:pointcut expression="execution(* *..*Impl.updateCustomer(..))" id="pt2"/> -->
			<!-- <aop:pointcut expression="execution(* *..*.updateCustomer(..))" id="pt2"/> -->
			<!-- <aop:pointcut expression="execution(* *..*.*Customer(..))" id="pt2"/> -->
			<aop:pointcut expression="execution(* *..*.*(..))" id="pt2"/>

			<!--
				用于配置前置通知：指定增强的方法在切入点方法之前执行
					method:用于指定通知类中的增强方法名称
					pointcut-ref：用于指定切入点的表达式的引用
					pointcut:直接写切入点表达式
			-->
			<!-- 前置通知 -->
			<aop:before method="beforePrintLog" pointcut-ref="pt1"/>
			<!-- 后置通知 -->
			<aop:after method="afterReturnningPrintLog" pointcut-ref="pt2"/>
			<!-- 异常通知 -->
			<aop:after-throwing method="afterThrowingPrintLog" pointcut-ref="pt1"/>
			<!-- 最终通知 -->
			<aop:after method="afterPrintLog" pointcut-ref="pt1"/>

			<!-- 环绕通知-->
			<aop:around method="aroundPrintLog" pointcut-ref="pt1"/>

		</aop:aspect>
	</aop:config>
</beans>
```

```java
public class Logger {
	/**
	 * 前置通知 作用：在业务层执行核心方法之前执行此方法记录日志
	 */
	public void beforePrintLog() {
		System.out.println("前置通知：正在记录日志。。。。。。");
	}

	/**
	 * 后置通知 作用：在业务层执行核心方法之后执行此方法记录日志
	 */
	public void afterReturnningPrintLog() {
		System.out.println("后置通知：正在记录日志。。。。。。");
	}

	/**
	 * 异常通知 作用：在业务层执行核心方法出现异常后执行此方法记录日志
	 */
	public void afterThrowingPrintLog() {
		System.out.println("异常通知：正在记录日志。。。。。。");
	}

	/**
	 * 最终通知 作用：在业务层执行核心方法最终执行此方法记录日志
	 */
	public void afterPrintLog() {
		System.out.println("最终通知：正在记录日志。。。。。。");
	}

	/**
	 * 环绕通知:是spring提供给我们的特殊通知，需要我们手动调用目标方法
	 * 		在调用目标方法之前的输出的就是前置通知
	 * 		在调用目标方法之后的输出的就是后置通知
	 * 		在调用目标方法之后有异常的输出的就是异常通知
	 * 		不管有无异常都会执行的代码是最终通知
	 */
	public Object aroundPrintLog(ProceedingJoinPoint pjp) {
		// 获取方法参数列表
		Object[] args = pjp.getArgs();
		// 定义返回变量
		Object result = null;
		try {
			// 前置通知
			System.out.println("前：记录日志。。。");
			// 手动调用目标方法
			result = pjp.proceed(args);
			// 后置通知
			System.out.println("后：记录日志。。。");
		} catch (Throwable e) {
			e.printStackTrace();
			// 异常通知
			System.out.println("异：记录日志。。。");
		} finally {
			// 最终通知
			System.out.println("终：记录日志。。。");
		}
		return result;
	}
}

public class ClientTest {
	public static void main(String[] args) {
		// 获取spring容器操作对象
		ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
		// 根据id获取对象
		ICustomerService cs = (ICustomerService) context.getBean("customerService");
		// 打印对象查看是否为代理对象
		System.out.println(cs.getClass().getName());

		// 要使用基于子类的，对象不能实现接口
		// CustomerServiceImpl cs = (CustomerServiceImpl) context.getBean("customerService");

		// 调用方法
		cs.saveCustomer();
		//cs.updateCustomer(10);
	}
}
```

<font color="purple">**注意：如果使用基于子类的方式创建动态代理，对象就不能实现接口，创建的代理需要自己用子类去接收。**</font>

### 3.2. 常用标签

|          标签名          | 属性                                                                                             | 作用                  |
| :---------------------: | :----------------------------------------------------------------------------------------------- | :------------------- |
|     `<aop:config>`      | \                                                                                                | 用于声明开始aop的配置 |
|     `<aop:aspect>`      | id：给切面提供一个唯一标识</br>ref：引用配置好的通知类 bean 的 id                                  | 用于配置切面          |
|    `<aop:pointcut>`     | expression：用于定义切入点表达式</br>id：用于给切入点表达式提供一个唯一标识                         | 用于配置切入点表达式  |
|     `<aop:before>`      | method：指定通知中方法的名称</br>pointct：定义切入点表达式</br>pointcut-ref：指定切入点表达式的引用 | 用于配置前置通知      |
| `<aop:after-returning>` | 同上                                                                                             | 用于配置后置通知      |
| `<aop:after-throwing>`  | 同上                                                                                             | 用于配置异常通知      |
|      `<aop:after>`      | 同上                                                                                             | 用于配置最终通知      |
|     `<aop:around>`      | method：指定通知中方法的名称</br>pointct：定义切入点表达式</br>pointcut-ref：指定切入点表达式的引用 | 用于配置环绕通知      |

eg.

```xml
<aop:before method="beforePrintLog" pointcut-ref="pt1"/>
<aop:after-returning method="afterReturningPrintLog" pointcut-ref="pt1"/>
<aop:after-throwing method="afterThrowingPrintLog" pointcut-ref="pt1"/>
<aop:after method="afterPrintLog" pointcut-ref="pt1"/>
<aop:around method="aroundPringLog" pointcut-ref="pt1"/>
```

### 3.3. 通知的类型

#### 3.3.1. 配置通知的类型说明

- `aop:before`：用于配置前置通知。前置通知的执行时间点：切入点方法执行之前执行
- `aop:after-returning`：用于配置后置通知。后置通知的执行时间点：切入点方法正常执行之后。它和异常通知只能有一个执行
- `aop:after-throwing`：用于配置异常通知。异常通知的执行时间点：切入点方法执行产生异常后执行。它和后置通知只能执行一个。
- `aop:after`：用于配置最终通知。最终通知的执行时间点：无论切入点方法执行时是否有异常，它都会在其后面执行。类似try-catch中的finally

#### 3.3.2. 环绕通知的特殊说明(了解)

- **环绕通知aop:around（了解）**
    - 用于配置环绕通知。他和前面四个不一样，他不是用于指定通知方法何时执行的。是Spring提供给我们的特殊通知，需要我们手动调用目标方法
    - 在通知方法的参数中，需要定义一个接口`ProceedingJoinPoint`，此接口的实现类，spring会注入
- 问题:
    - 当我们配置了环绕通知之后，增强的代码执行了，业务核心方法没有执行。
- 分析：
    - 通过动态代理我们知道在invoke方法中，有明确调用业务核心方法：`method.invoke()`。
    - 我们配置的环绕通知中，没有明确调用业务核心方法。
- 解决：
    - spring 框架为我们提供了一个接口：`ProceedingJoinPoint`，它可以作为环绕通知的方法参数
    - 在环绕通知执行时，spring 框架会为我们提供该接口的实现类对象，我们直接使用就行。
    - 该接口中有一个方法`proceed()`，执行目标方法，此方法就相当于`method.invoke()`
    - `getArgs()`方法：获取参数列表
        - 在调用目标方法之前的输出的就是前置通知
        - 在调用目标方法之后的输出的就是后置通知
        - 在调用目标方法之后有异常的输出的就是异常通知
        - 不管有无异常都会执行的代码是最终通知

## 4. 基于纯注解方式的 AOP 配置和使用

Spring 支持 AspectJ 的注解式切面编程，使用的整体步骤

1. 使用 `@Aspect` 声明是一个切面
2. 使用 `@After`、`@Before`、`@Around` 等注解定义建言（advice），可以直接将拦截规则（切点）作为参数
3. 拦截规则为切点(PointCut)，可使用 `@PointCut` 定义好拦截规则，然后在 `@After`、`@Before`、`@Around` 的参数中调用
4. 符合条件的每一个被拦截处为连接点（JoinPoint）

### 4.1. 基于 execution 表达式拦截的使用步骤

#### 4.1.1. 第1步 添加相关的依赖(或拷贝jar包到lib目录)

![](images/20190402104959246_4203.png)

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>${spring.version}</version>
</dependency>
```

#### 4.1.2. 第2步 创建 spring 的配置文件并导入约束，指定 spring 要扫描的包

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/aop
			http://www.springframework.org/schema/aop/spring-aop.xsd
			http://www.springframework.org/schema/context
			http://www.springframework.org/schema/context/spring-context.xsd">
	<!-- 配置创建spring容器需要扫描的基础包 -->
	<context:component-scan base-package="com.moonzero"></context:component-scan>
</beans>
```

#### 4.1.3. 第3步 使用注解配置相关资源和通知类给spring来管理

```java
// 使用注解配置使用spring创建对象
@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {
    ......
}
```

#### 4.1.4. 第4步 在通知类上使用@Aspect 注解声明为切面
#### 4.1.5. 第5步 在增强的方法上使用@Before 注解配置前置通知

```java
// 配置通知类给spring管理
@Component("logger")
// 配置通知类
@Aspect
public class Logger {

	// 定义切入点
	@Pointcut("execution(* *..*.CustomerServiceImpl.saveCustomer())")
	public void pt1() {
	}

	@Pointcut(value="execution(* *..*.*(..))")
	public void pt2() {
	}

	/**
	 * 前置通知 作用：在业务层执行核心方法之前执行此方法记录日志
	 */
	// 指定切入点表达式的引用
	// 注意:在注解因为定义切入是在方法上，所以value值的id需要写上()
	@Before("pt1()")
	public void beforePrintLog(JoinPoint point) {
		System.out.println("前置通知：正在记录日志。。。。。。");
	}

	/**
	 * 后置通知 作用：在业务层执行核心方法之后执行此方法记录日志
	 */
	// 直接指定切入点表达式
	@AfterReturning("execution(* *..*.*(..))")
	public void afterReturnningPrintLog() {
		System.out.println("后置通知：正在记录日志。。。。。。");
	}

	/**
	 * 异常通知 作用：在业务层执行核心方法出现异常后执行此方法记录日志
	 */
	@AfterThrowing("pt1()")
	public void afterThrowingPrintLog() {
		System.out.println("异常通知：正在记录日志。。。。。。");
	}

	/**
	 * 最终通知 作用：在业务层执行核心方法最终执行此方法记录日志
	 */
	@After("pt1()")
	public void afterPrintLog() {
		System.out.println("最终通知：正在记录日志。。。。。。");
	}

	/**
	 * 环绕通知:
	 */
	@Around("pt1()")
	public Object aroundPrintLog(ProceedingJoinPoint pjp) {
		// 获取方法参数列表
		Object[] args = pjp.getArgs();
		// 定义返回变量
		Object result = null;

		try {
			// 前置通知
			System.out.println("前：记录日志。。。");
			// 手动调用目标方法
			result = pjp.proceed(args);
			// 后置通知
			System.out.println("后：记录日志。。。");
		} catch (Throwable e) {
			e.printStackTrace();
			// 异常通知
			System.out.println("异：记录日志。。。");
		} finally {
			// 最终通知
			System.out.println("终：记录日志。。。");
		}

		return result;
	}
}
```

#### 4.1.6. 第6步 在 spring 配置文件中开启 spring 对注解 AOP 的支持

```xml
<!-- 开启spring容器对AOP注解的支持 -->
<aop:aspectj-autoproxy />
```

*注意：为什么切入点表达式是写在通知增加方法上，而不是写在切入点所在的类的方法上？从aop理念就可以理解，如果写在被增强的类上，就是违反原来不修改源代码而得到增强的理念。*

### 4.2. 常用注解

|      **注解名**       |                      **属性**                       |        **作用**        |
| :-------------------: | :-------------------------------------------------: | :-------------------: |
|     **`@Aspect`**     |                          \                          |   把当前类声明为切面类   |
|     **`@Before`**     | value：用于指定切入点表达式，还可以指定切入点表达式的引用 | 把当前方法看成是前置通知 |
| **`@AfterReturning`** |                         同上                         | 把当前方法看成是后置通知 |
| **`@AfterThrowing`**  |                         同上                         | 把当前方法看成是异常通知 |
|     **`@After`**      |                         同上                         | 把当前方法看成是最终通知 |
|     **`@Around`**     |                         同上                         | 把当前方法看成是环绕通知 |
|    **`@Pointcut`**    |                value：指定表达式的内容                |     指定切入点表达式     |

**注意：在注解因为定义切入点`@Pointcut`是在成员方法上，所以通知用于指定切入点表达式的引用的值需要写上“()”！！**

```java
@Pointcut("execution(* *..*.*(..))")
public void pt1() {
}

@Before("pt1()")
public void beforePrintLog() {
    xxx;
}
```

### 4.3. 基于自定义注解拦截的使用步骤

1. 使用注解式拦截首先编写一个注解，如

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    public String name() default "";
}
```

2. 编写切面（类），如：

```java
@Aspect
@Component
public class DataScopeAspect {
    // 配置织入点
    @Pointcut("@annotation(com.moon.common.annotation.DataScope)")
    public void dataScopePointCut() {}

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataScope annotation = method.getAnnotation(DataScope.class);
        System.out.println("注解属性name值：" + method.getName());
    }
}
```

3. 在需要拦截的方法上加上注解，如

```java
// 使用aop拦截方法
@DataScope(name = "u")
public List<SysRole> selectRoleList(SysRole role) {
    return roleMapper.selectRoleList(role);
}
```

**注：注解式拦截与方法规则拦截的用法是一样的，使用方法规则拦截不需要定义注解。区别在于定义切入点（@PointCut）注解时的参数不一样，使用注解式拦截的参数是：`"@annotation(自定义的注解全类名)"`，使用方法规则拦截的参数是：`"execution(* *..*.*(..))"`**

## 5. 基于注解的 AOP 配置（不使用xml）

将 xml 配置删除，换成配置类 SpringConfiguration.java

```java
/**
 * 代替bean.xml文件的配置类
 */
// 用于指定当前类是一个配置类
@Configuration
// 用于指定spring在初始化容器时要扫描的包
@ComponentScan("com.moonzero")
// 开启spring对象注解aop的支持
@EnableAspectJAutoProxy
public class SpringConfiguration {
}

/**
 * 配置测试
 */
public class ClientTest {
	public static void main(String[] args) {
		// 获取spring容器操作对象（纯注解方式）
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		// 根据id获取对象
		ICustomerService cs = (ICustomerService) context.getBean("customerService");
		// 调用方法
		cs.saveCustomer();
	}
}
```

## 6. Spring AOP 建言的执行顺序

### 6.1. 当方法正常执行时的执行顺序

![1](images/20190402140837769_19036.png)

### 6.2. 当方法出现异常时的执行顺序

![2](images/20190402140843555_2239.png)

### 6.3. 当同一个方法被多个注解`@Aspect`类拦截时

可以通过在为上注解`@Order`指定Aspect类的执行顺序。例如Aspect1上注解了Order(1)，Aspect2上注解了Order(2)，则建言的执行顺序为：

![3](images/20190402140848492_31445.jpg)

## 7. 切入点表达式

### 7.1. 切入点表达式概念及作用

- 概念：指的是遵循特定的语法用于捕获每一个种类的可使用连接点的语法。
- 作用：用于对符合语法格式的连接点进行增强。

### 7.2. 按照用途分类

- 方法执行：`execution(MethodSignature)`
- 方法调用：`call(MethodSignature)`
- 构造器执行：`execution(ConstructorSignature)`
- 构造器调用：`call(ConstructorSignature)`
- 类初始化：`staticinitialization(TypeSignature)`
- 属性读操作：`get(FieldSignature)`
- 属性写操作：`set(FieldSignature)`
- 例外处理执行：`handler(TypeSignature)`
- 对象初始化：`initialization(ConstructorSignature)`
- 对象预先初始化：`preinitialization(ConstructorSignature)`

> **在spring aop主要只支持方法的增强，所以只用到`execution(MethodSignature)`**

### 7.3. 切入点表达式的关键字

支持的 AspectJ 切入点指示符如下：

- `execution`：用于匹配方法执行的连接点；
- `within`：用于匹配指定类型内的方法执行；
- `this`：用于匹配当前 AOP 代理对象类型的执行方法；注意是 AOP 代理对象的类型匹配，这样就可能包括引入接口也类型匹配；
- `target`：用于匹配当前目标对象类型的执行方法；注意是目标对象的类型匹配，这样就不包括引入接口也类型匹配；
- `args`：用于匹配当前执行的方法传入的参数为指定类型的执行方法；
- `@within`：用于匹配所以持有指定注解类型内的方法；
- `@target`：用于匹配当前目标对象类型的执行方法，其中目标对象持有指定的注解；
- `@args`：用于匹配当前执行的方法传入的参数持有指定注解的执行；
- `@annotation`：用于匹配当前执行方法持有指定注解的方法；
- `bean`：Spring AOP 扩展的，AspectJ 没有对于指示符，用于匹配特定名称的 Bean 对象的执行方法；
- `reference pointcut`：表示引用其他命名切入点，只有 `@ApectJ` 风格支持，Schema 风格不支持。

### 7.4. 切入点表达式的通配符

AspectJ类型匹配的通配符：

- `*`：匹配任何数量字符；
- `..`：匹配任何数量字符的重复，如在类型模式中匹配任何数量子包；而在方法参数模式中匹配任何数量参数。
- `+`：匹配指定类型的子类型；仅能作为后缀放在类型模式后边。

示例：

- `java.lang.String`：匹配String类型
- `java.*.String`：匹配java包下的任何“一级子包”下的String类型。如匹配java.lang.String，但不匹配java.lang.ss.String
- `java..*`：匹配java包及任何子包下的任何类型。如匹配java.lang.String、java.lang.annotation.Annotation
- `java.lang.*ing`：匹配任何java.lang包下的以ing结尾的类型
- `java.lang.Number+`：匹配java.lang包下的任何Number的子类型。如匹配java.lang.Integer，也匹配java.math.BigInteger

### 7.5. 切入点表达式的逻辑条件

- `&&`：与（and）
- `||`：或（or）
- `!`：非（not）

### 7.6. 使用说明

execution 表达式，用于匹配方法的执行(常用)。**表达式语法**如下：

```java
execution([修饰符] 返回值类型 包名.类名.方法名(参数))
```

**写法示例说明**：

1. 全匹配方式（修饰符public可以省略）：`public void com.moon.service.impl.CustomerServiceImpl.saveCustomer()`
2. 访问修饰符可以省略：`void com.moon.service.impl.CustomerServiceImpl.saveCustomer()`
3. 返回值可以使用`*`号，表示任意返回值：`* com.moon.service.impl.CustomerServiceImpl.saveCustomer()`
4. 包名：
    - 可以使用`*`号，表示任意包，但是有几级包，需要写几个`*`：`* *.*.*.*.CustomerServiceImpl.saveCustomer()`
    - 使用`..`来表示当前包，及其子包：`* com..CustomerServiceImpl.saveCustomer()`
5. 类名：
	- 可以使用`*`号，表示任意类：`* com..*.saveCustomer()`
	- 可以使用部分通配的方法：`* com..*Impl.saveCustomer()`
6. 方法名：
	- 可以使用`*`号，表示任意方法：`* com..*.*()`
	- 可以使用部分通配的方法：`* com..*.*all()`
7. 参数列表：
	- 参数列表可以使用`*`，表示参数可以是任意数据类型，但是必须有参数：`* com..*.*(*)`
	- 参数列表可以使用 `..` 表示有无参数均可，有参数可以是任意类型：`* com..*.*(..)`
8. **全通配方式**：`* *..*.*(..)`
