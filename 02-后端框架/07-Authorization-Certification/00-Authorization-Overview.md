# Authorization Overview（授权认证概述）

此部分是对于授权认证的综合概述，是各种授权认证框架的公共介绍。具体授权框架的使用教程详见各个框架笔记

## 1. 基本概念

### 1.1. 什么是认证

认证 ：一般是指用户认证，就是判断一个用户的身份是否合法的过程，用户去访问系统资源时系统要求验证用户的身份信息，身份合法方可继续访问，不合法则拒绝访问。认证是为了保护系统的隐私数据与资源，用户的身份合法方可访问该系统的资源。

常见的用户身份认证方式有：用户名密码登录，二维码登录，手机短信登录，指纹认证等方式。

### 1.2. 什么是会话

用户认证通过后，为了避免用户的每次操作都进行认证可将用户的信息保证在会话中。会话就是系统为了保持当前用户的登录状态所提供的机制，常见的有基于session方式、基于token方式等。

#### 1.2.1. 基于 session 的认证方式

交互流程是，用户认证成功后，在服务端生成用户相关的数据保存在session(当前会话)中，发给客户端的 sesssion_id 存放到 cookie 中，这样用户客户端请求时带上 session_id 就可以验证服务器端是否存在 session 数据，以此完成用户的合法校验，当用户退出系统或 session 过期销毁时，客户端的 session_id 也就无效了。

![](images/377015221220163.png)

#### 1.2.2. 基于 token 的认证方式

交互流程是，用户认证成功后，服务端生成一个token发给客户端，客户端可以放到 cookie 或 localStorage 等存储中，每次请求时带上 token，服务端收到 token 通过验证后即可确认用户身份。

![](images/49445321238589.png)

#### 1.2.3. 两种方式的区别

- 基于 session 的认证方式由 Servlet 规范定制，服务端要存储 session 信息需要占用内存资源，客户端需要支持 cookie；
- 基于 token 的方式则一般不需要服务端存储 token，并且不限制客户端的存储方式。

如今移动互联网时代更多类型的客户端需要接入系统，系统多是采用前后端分离的架构进行实现，所以基于 token 的方式更适合。

### 1.3. 什么是授权

授权是用户认证通过后，根据用户的权限来控制用户访问资源的过程，拥有资源的访问权限则正常访问，没有权限则拒绝访问。

认证是为了保证用户身份的合法性，授权则是为了更细粒度的对隐私数据进行划分，授权是在认证通过后发生的，控制不同的用户能够访问不同的资源。

### 1.4. 授权的数据模型

授权即对用户访问资源进行控制。

#### 1.4.1. 数据模型的类型

授权是有相关的数据模型，主要分成以下几种类型：

- 主体（Subject），一般是指用户，也可以是程序，需要访问系统中的资源。
- 资源（Resource），如系统菜单、页面、按钮、代码方法、系统商品信息、系统订单信息等。系统菜单、页面、按钮、代码方法都属于系统功能资源，对于web系统每个功能资源通常对应一个URL；系统商品信息、系统订单信息都属于实体资源（数据资源），实体资源由资源类型和资源实例组成，比如商品信息为资源类型，商品编号 为001的商品为资源实例。
- 权限/许可（Permission），规定了用户对资源的操作许可，权限离开资源没有意义，如用户查询权限、用户添加权限、某个代码方法的调用权限、编号为001的用户的修改权限等，通过权限可知用户对哪些资源都有哪些操作许可。

#### 1.4.2. 数据模型之间的关系

对于权限的控制，一般是会引入角色这个概念，定义一个角色所拥有那些权限，再根据权限去访问相应的资源。相关的数据模型如下：

- 主体表（用户id、账号、密码、...）
- 资源表（资源id、资源名称、访问地址、...）
- 权限表（权限id、权限标识、权限名称、资源id、...）
- 角色表（角色id、角色名称、...）
- 角色和权限关系表（角色id、权限id、...）
- 主体（用户）和角色关系表（用户id、角色id、...）

关系如下图：

![](images/281551622226456.png)

通常企业开发中会设计将资源和权限表合并为一张权限表，如下：

- 权限表（权限id、权限标识、权限名称、资源名称、资源访问地址、...）

修改后数据模型之间的关系如下图：

![](images/43872622246622.png)

### 1.5. RBAC 授权方案

#### 1.5.1. 基于角色的访问控制

RBAC 基于角色的访问控制（Role-Based Access Control）是按角色进行授权。

例如：定义角色A可以进行删除某些资源的操作，那么代码的处理逻辑就是，在删除这些资源之前，就需要判断当前用户是否为角色A。如果是，则可以删除资源；否则不能删除。但如果当前有新需求需要增加角色B，并且也可以进行删除资源的操作，此时就需要修改代码，将删除资源需要判断当前用户是否为角色A或者角色B，如果是，则可以删除资源。

根据上面的例子发现，当需要修改角色的权限时就需要修改授权的相关代码，系统可扩展性差。

#### 1.5.2. 基于资源的访问控制

RBAC 基于资源的访问控制（Resource-Based Access Control）是按资源（或权限）进行授权。

例如：同样是上面的示例，如果调整为拥有权限A的才可以进行删除资源操作，那么代码的实现逻辑是判断当前用户的角色A是否有权限A，如果有，则可以删除资源。那么当新增一个角色B时，如果也需要进行删除操作，则需要在表中新增记录，将角色B与权限A绑定，则角色B就拥有了删除资源的权限，此时是不需要修改原代码的判断逻辑。

根据上面的例子可发现，根据系统设计时定义好相关权限关系，即使修改主体相关的角色，也不需要修改原授权的逻辑代码，系统可扩展性强。

## 2. 基于 Session 实现认证的示例

本案例工程使用maven进行构建，使用SpringMVC、Servlet3.0实现。

### 2.1. 认证流程

> 基于Session认证方式的流程描述详见《[基于 session 的认证方式](#_121-基于-session-的认证方式)》

基于 Session 的认证机制由 Servlet 规范定制，Servlet 容器已实现，用户通过 `HttpSession` 的操作方法即可实现，如下是 `HttpSession` 常用的相关操作API。

|                      方法                      |            作用             |
| --------------------------------------------- | -------------------------- |
| `HttpSession getSession(Boolean create)`      | 获取当前 `HttpSession` 对象 |
| `void setAttribute(String name,Object value)` | 向 session 中存放对象       |
| `object getAttribute(String name)`            | 从 session 中获取对象       |
| `void removeAttribute(String name);`          | 移除 session 中对象         |
| `void invalidate()`                           | 使 `HttpSession` 失效       |

### 2.2. 创建示例工程

#### 2.2.1. 创建 maven 工程

创建 maven 工程：security-session-sample。最终项目结构如下：

![](images/307784215223120.png)

引入如下依赖如下，注意：

1. 由于示例项目是web工程，所以 `packaging` 设置为 `war`
2. 使用 tomcat7-maven-plugin 插件来运行工程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.moon</groupId>
    <artifactId>security-session-sample</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>${artifactId}</name>
    <packaging>war</packaging>
    <description>
        基于 Session 实现授权认证的示例工程
    </description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.1.5.RELEASE</version>
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

#### 2.2.2. 创建 Spring 容器配置类

创建 `com.moon.security.session.config.ApplicationConfig` 类，用于替代 applicationContext.xml 配置文件。对应在 web.xml 中的 `ContextLoaderListener` 等配置。

```java
/**
 * 替代 applicationContext.xml 配置文件
 * 在此类中配置除了Controller的其它bean，比如：数据库链接池、事务管理器、业务bean等。
 */
@Configuration
@ComponentScan(basePackages = "com.moon.security.session",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class ApplicationConfig {
}
```

#### 2.2.3. 创建 ServletContext 配置类

本案例采用 Servlet3.0 无 web.xml 方式，创建 `com.moon.security.session.config.WebConfig` 类，它对应于 web.xml  文件中的 `DispatcherServlet` 配置

```java
/**
 * 此配置类相当于在 web.xml 的 DispatcherServlet 配置。
 * 在此类中配置 Spring MVC 的视频解析器
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.moon.security.session",
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class WebConfig implements WebMvcConfigurer {

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

#### 2.2.4. 加载 Spring 容器

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

`SpringApplicationInitializer` 相当于 web.xml，使用了 servlet3.0 开发则不需要再定义 web.xml，`ApplicationConfig.class` 对应以下配置的 application-context.xml，`WebConfig.class` 对应以下配置的 spring-mvc.xml，上面示例配置相当于 web.xml 的内容参考如下：

```xml
<web‐app>
    <listener>
        <listener‐class>org.springframework.web.context.ContextLoaderListener</listener‐class>
    </listener>
    <context‐param>
        <param‐name>contextConfigLocation</param‐name>
        <param‐value>/WEB‐INF/application‐context.xml</param‐value>
    </context‐param>

    <servlet>
        <servlet‐name>springmvc</servlet‐name>
        <servlet‐class>org.springframework.web.servlet.DispatcherServlet</servlet‐class>
        <init‐param>
            <param‐name>contextConfigLocation</param‐name>
            <param‐value>/WEB‐INF/spring‐mvc.xml</param‐value>
        </init‐param>
        <load‐on‐startup>1</load‐on‐startup>
    </servlet>

    <servlet‐mapping>
        <servlet‐name>springmvc</servlet‐name>
        <url‐pattern>/</url‐pattern>
    </servlet‐mapping>
</web‐app>
```

### 2.3. 认证功能的实现

#### 2.3.1. 认证页面

在 webapp/WEB-INF/views 下创建认证页面 login.jsp，本案例只是测试认证流程，页面没有添加css样式，只实现可填入用户名，密码，触发登录将提交表单信息至 /login，内容如下：

```jsp
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
<form action="login" method="post">
    用户名：<input type="text" name="username"/><br/>
    密&nbsp;&nbsp;&nbsp;码:
    <input type="password" name="password"/><br/>
    <input type="submit" value="登录">
</form>
</body>
</html>
```

在 `WebConfig` 类中新增如下配置，将url `/` 重定向到 login.jsp 页面：

```java
/**
 * 配置 url 的与页面的映射关系
 *
 * @param registry
 */
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    // 增加 url 为 '/' 时，映射到 login.jsp 页面
    registry.addViewController("/").setViewName("login");
}
```

#### 2.3.2. 创建认证接口

用户进入认证页面，输入账号和密码，点击登录，请求 /login 进行身份认证。

- 创建接收请求参数、响应参数的实体类

认证请求实体类：

```java
@Data
public class AuthenticationRequest {
    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
```

认证成功后返回的用户详细信息，也就是当前登录用户的信息：

```java
@Data
@AllArgsConstructor
public class UserDto {
    // 用户身份信息
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
```

- 定义认证检验业务接口，此接口用于对传来的用户名、密码校验，若成功则返回该用户的详细信息，否则抛出错误异常：

> *注：此处只是模拟登陆，没有查询数据库，代码写死用户*

```java
public interface AuthenticationService {
    /**
     * 用户认证
     *
     * @param authenticationRequest 用户认证请求，账号和密码
     * @return 认证成功的用户信息
     */
    UserDto authentication(AuthenticationRequest authenticationRequest);
}
```

实现类

```java
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public UserDto authentication(AuthenticationRequest authenticationRequest) {
        // 校验参数是否为空
        if (authenticationRequest == null
                || StringUtils.isEmpty(authenticationRequest.getUsername())
                || StringUtils.isEmpty(authenticationRequest.getPassword())) {
            throw new RuntimeException("账号和密码为空");
        }

        // 根据账号去查询数据库，这里测试程序采用模拟方法
        UserDto user = getUserDto(authenticationRequest.getUsername());
        // 判断用户是否为空
        if (user == null) {
            throw new RuntimeException("查询不到该用户");
        }
        // 校验密码
        if (!authenticationRequest.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        // 认证通过，返回用户身份信息
        return user;
    }


    // 模拟根据账号查询用户信息
    private UserDto getUserDto(String userName) {
        return userMap.get(userName);
    }

    // 保存用户信息的map
    private final Map<String, UserDto> userMap = new HashMap<>();
    // 初始化一些测试使用的用户信息到内存中
    {
        userMap.put("admin", new UserDto("1000", "admin", "123", "管理员", "133443"));
        userMap.put("moon", new UserDto("1011", "moon", "456", "月之哀伤", "144553"));
    }
}
```

- 创建请求控制类，对 /login 请求处理，并调用 `AuthenticationService` 完成认证并返回登录结果提示信息：

```java
@RestController
public class LoginController {
    @Autowired
    AuthenticationService authenticationService;

    /**
     * 用户登陆
     *
     * @param authenticationRequest 登陆请求参数
     * @return
     */
    @PostMapping(value = "/login", produces = "text/plain;charset=utf-8")
    public String login(AuthenticationRequest authenticationRequest) {
        UserDto userDto = authenticationService.authentication(authenticationRequest);
        return userDto.getUsername() + "登录成功";
    }
}
```

#### 2.3.3. 启动项目测试

使用 maven 命令启动项目，以下是使用idea为示例：

![](images/531833813220164.png)

```bash
clean tomcat7:run
```

输入网址：http://localhost:8080/security-session-sample 进行测试

填入错误的用户信息，页面返回错误信息：

![](images/22280014238590.png)

填入正确的用户信息，页面提示登录成功：

![](images/328115913220164.png)

以上的测试全部符合预期，到目前为止最基础的认证功能已经完成，但目前仅仅实现了对用户身份凭证的校验，若某用户认证成功，只能说明他是该系统的一个合法用户而已。

### 2.4. 实现会话功能

会话是指用户登入系统后，系统会记住该用户的登录状态，可以在系统连续操作直到退出系统的过程。

认证的目的是对系统资源的保护，每次对资源的访问，系统必须得知道是谁在访问资源，才能对该请求进行合法性拦截。因此，在认证成功后，一般会把认证成功的用户信息放入Session中，在后续的请求中，系统能够从 Session 中获取到当前用户，用这样的方式来实现会话机制。

#### 2.4.1. 增加会话控制逻辑

在UserDto 定义一个 `SESSION_USER_KEY` 常量，作为 Session 中存放登录用户信息的 key。

```java
public class UserDto {
    // 用户session的key
    public static final String SESSION_USER_KEY = "_user";
    ...
}
```

修改 `LoginController`，在认证成功后，增加将用户信息放入当前会话的逻辑

```java
@PostMapping(value = "/login", produces = "text/plain;charset=utf-8")
public String login(AuthenticationRequest authenticationRequest, HttpSession httpSession) {
    UserDto userDto = authenticationService.authentication(authenticationRequest);
    // 将用户信息存入session中
    httpSession.setAttribute(UserDto.SESSION_USER_KEY, userDto);
    return userDto.getUsername() + "登录成功";
}
```

在 `LoginController` 类中增加用户登出方法，只需要将session设置为失效即可

```java
@GetMapping(value = "/logout",produces = {"text/plain;charset=UTF-8"})
public String logout(HttpSession session){
    session.invalidate();
    return "退出成功";
}
```

#### 2.4.2. 增加 session 测试方法

在 `LoginController` 类中增加 session 的校验方法，根据用户的 key 值从当前会话 session 中获取当前登录用户，并返回提示信息给前台。

```java
@GetMapping(value = "/check", produces = {"text/plain;charset=UTF-8"})
public String checkSession(HttpSession session) {
    String fullname = null;
    // 从 session 中获取用户信息
    Object object = session.getAttribute(UserDto.SESSION_USER_KEY);
    if (object == null) {
        fullname = "匿名"; // 无登陆
    } else {
        UserDto userDto = (UserDto) object;
        fullname = userDto.getFullname(); // 有登陆
    }
    return fullname + "访问资源";
}
```

#### 2.4.3. 测试

- 未登录情况下直接访问测试资源 /check，返回结果是“匿名访问资源”
- 成功登录的情况下访问测试资源 /check，返回结果是“管理员访问资源”

测试结果说明，在用户登录成功时，该用户信息已被成功放入 session，并且后续请求可以正常从 session 中获取当前登录用户信息，符合预期结果。

### 2.5. 实现授权功能

通过上次两个步骤，已经实现了用户登陆与使用 session 保存用户的登陆状态。然后现在需要完成如下功能：

- 匿名用户（未登录用户）访问拦截：禁止匿名用户访问某些资源。
- 登录用户访问拦截：根据用户的权限决定是否能访问某些资源。

#### 2.5.1. 给用户增加权限

- 在 `UserDto` 类中增加权限属性，用于表示该登录用户所拥有的权限

```java
public class UserDto {
    ...
    /** 用户权限 */
    private Set<String> authorities;
}
```

- 在 `AuthenticationServiceImpl` 中为模拟用户初始化权限，分别给两个用户p1和p2权限

```java
{
    Set<String> authorities1 = new HashSet<>();
    authorities1.add("p1"); // 模拟增加权限1
    Set<String> authorities2 = new HashSet<>();
    authorities2.add("p2"); // 模拟增加权限2
    // 分别给两个用户设置不同的权限标识
    userMap.put("admin", new UserDto("1000", "admin", "123", "管理员", "133443", authorities1));
    userMap.put("moon", new UserDto("1011", "moon", "456", "月之哀伤", "144553", authorities2));
}
```

#### 2.5.2. 增加不同权限测试请求资源

在 `LoginController` 类分别定义两个针对不同的用户访问的不同资源

```java
/**
 * 测试当前用户是否有登陆
 *
 * @param session
 * @return
 */
@GetMapping(value = "/check/p1", produces = {"text/plain;charset=UTF-8"})
public String checkSession(HttpSession session) {
    String fullname = null;
    // 从 session 中获取用户信息
    Object object = session.getAttribute(UserDto.SESSION_USER_KEY);
    if (object == null) {
        fullname = "匿名"; // 无登陆
    } else {
        UserDto userDto = (UserDto) object;
        fullname = userDto.getFullname(); // 有登陆
    }
    return fullname + "访问资源p1";
}

/**
 * 测试当前用户是否有登陆
 *
 * @param session
 * @return
 */
@GetMapping(value = "/check/p2", produces = {"text/plain;charset=UTF-8"})
public String checkSession2(HttpSession session) {
    String fullname = null;
    // 从 session 中获取用户信息
    Object object = session.getAttribute(UserDto.SESSION_USER_KEY);
    if (object == null) {
        fullname = "匿名"; // 无登陆
    } else {
        UserDto userDto = (UserDto) object;
        fullname = userDto.getFullname(); // 有登陆
    }
    return fullname + "访问资源p2";
}
```

#### 2.5.3. 授权拦截器

在 interceptor 包下创建 `AuthenticationInterceptor` 拦截器，需要实现 `org.springframework.web.servlet.HandlerInterceptor` 接口，实现简单授权拦截，主要处理的逻辑如下：

1. 校验用户是否登录
2. 校验用户是否拥有操作权限

```java
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    /**
     * 请求前置拦截逻辑。在此方法中校验用户请求的url是否在用户的权限范围内
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户身份信息
        Object object = request.getSession().getAttribute(UserDto.SESSION_USER_KEY);

        if (object == null) {
            // 用户无登陆
            responseContent(response, "请登录");
            return false;
        }

        UserDto userDto = (UserDto) object;
        // 请求的url
        String requestURI = request.getRequestURI();
        if (userDto.getAuthorities().contains("p1") && requestURI.contains("/check/p1")) {
            return true;
        }
        if (userDto.getAuthorities().contains("p2") && requestURI.contains("/check/p2")) {
            return true;
        }
        responseContent(response, "没有权限，拒绝访问");

        return false;
    }

    /**
     * 响应处理
     *
     * @param response
     * @param msg
     */
    private void responseContent(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(msg);
        writer.close();
    }
}
```

#### 2.5.4. 配置拦截器

在 `WebConfig` 配置类中，重写 `addInterceptors` 方法，配置自定义的授权拦截器，匹配 `/check/**` 的资源为受保护的系统资源，访问该资源的请求进入 `AuthenticationInterceptor` 拦截器。

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.moon.security.session",
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    // 视图解析器
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * 配置 url 的与页面的映射关系
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 增加 url 为 '/' 时，映射到 login.jsp 页面
        registry.addViewController("/").setViewName("login");
    }

    /**
     * 增加自定义的授权拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/check/**");
    }
}
```

#### 2.5.5. 测试

- 未登陆情况：访问 `/check/p1` 与 `/check/p2`，均提示先登陆
- 登陆 admin 账号，测试可以访问 `/check/p1`，无权限访问 `/check/p2`
- 登陆 moon 账号，测试可以访问 `/check/p2`，无权限访问 `/check/p1`

![](images/427563923238591.png)

### 2.6. 小结

基于 Session 的认证方式是一种常见的认证方式，至今还有非常多的系统在使用。以上示例使用 Spring MVC 技术对它进行简单实现，通过此示例可能了解用户认证、授权以及会话的功能意义及实现套路。

而在正式生产项目中，往往会考虑使用第三方安全框架（如 spring security，shiro 等安全框架）来实现认证授权功能，因为使用这些成熟框架在一定程度提高生产力，提高软件标准化程度，另外往往这些框架的可扩展性考虑的非常全面。但是缺点也非常明显，这些通用化组件为了提高支持范围会增加很多可能不需要的功能，结构上也会比较抽象，如果不够了解它，一旦出现问题，将会很难定位。

