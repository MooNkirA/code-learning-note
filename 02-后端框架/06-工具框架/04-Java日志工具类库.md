# Java日志工具类库

## 1. 主流日志框架简介
### 1.1. java.util.logging (JUL)

- JDK1.4 开始，通过 java.util.logging 提供日志功能。
- 它能满足基本的日志需要，但是功能没有 Log4j 强大，而且使用范围也没有 Log4j 广泛。

### 1.2. Log4j

- Log4j 是 apache 的一个开源项目
- Log4j 应该说是 Java 领域资格最老，应用最广的日志工具。从诞生之日到现在一直广受业界欢迎。
- Log4j 是高度可配置的，并可通过在运行时的外部文件配置。它根据记录的优先级别，并提供机制，以指示记录信息到许多的目的地，诸如：数据库，文件，控制台，UNIX 系统日志等。
- Log4j 中有三个主要组成部分：
    - loggers - 负责捕获记录信息。
    - appenders - 负责发布日志信息，以不同的首选目的地。
    - layouts - 负责格式化不同风格的日志信息。
- 官网地址：http://logging.apache.org/log4j/2.x/

#### 1.2.1. log4j使用步骤

1. 导入 log4j 的 jar 包
2. 复制 log4j 的配置文件 log4j.properties 到 src 文件夹下面
3. 设置日志级别
    - `info`：显示信息
    - `debug`：显示更详细信息

```properties
### set log levels - for more verbose logging change 'info' to 'debug' ###
log4j.rootLogger=info, stdout
```

> *注：在开发环境下日志级别要设置成debug，生产环境设置成info或error*

### 1.3. Logback

- Logback 是由 log4j 创始人 Ceki Gulcu 设计的又一个开源日记组件，目标是替代 log4j
- logback 当前分成三个模块：logback-core、logback-classic 和 logback-access。
    - logback-core - 是其它两个模块的基础模块。
    - logback-classic - 是 log4j 的一个 改良版本。此外 logback-classic 完整实现 SLF4J API 使你可以很方便地更换成其它日记系统如 log4j 或 JDK14 Logging。
    - logback-access - 访问模块与 Servlet 容器集成提供通过 Http 来访问日记的功能。
- 官网地址：http://logback.qos.ch/

### 1.4. Log4j2

- 官网地址：http://logging.apache.org/log4j/2.x/
- 按照官方的说法，Log4j2 是 Log4j 和 Logback 的替代

### 1.5. 各个日志框架比较

按照官方的说法，Log4j2 大大优于 Log4j 和 Logback。Log4j2的优势如下：

- Log4j2 旨在用作审计日志记录框架。Log4j 1.x 和 Logback 都会在重新配置时丢失事件。Log4j 2 不会。在 Logback 中，Appender 中的异常永远不会对应用程序可见。在 Log4j 中，可以将 Appender 配置为允许异常渗透到应用程序。
- Log4j2 在多线程场景中，异步 Loggers 的吞吐量比 Log4j 1.x 和 Logback 高 10 倍，延迟低几个数量级。
- Log4j2 对于独立应用程序是无垃圾的，对于稳定状态日志记录期间的 Web 应用程序来说是低垃圾。这减少了垃圾收集器的压力，并且可以提供更好的响应时间性能。
- Log4j2 使用插件系统，通过添加新的 Appender、Filter、Layout、Lookup 和 Pattern Converter，可以非常轻松地扩展框架，而无需对 Log4j 进行任何更改。
- 由于插件系统配置更简单。配置中的条目不需要指定类名。
- 支持自定义日志等级。
- 支持 lambda 表达式。
- 支持消息对象。
- Log4j 和 Logback 的 Layout 返回的是字符串，而 Log4j2 返回的是二进制数组，这使得它能被各种 Appender 使用。
- Syslog Appender 支持 TCP 和 UDP 并且支持 BSD 系统日志。
- Log4j2 利用 Java5 并发特性，尽量小粒度的使用锁，减少锁的开销

### 1.6. 日志门面
#### 1.6.1. 日志门面概念

日志门面是对不同日志框架提供的一个 API 封装，可以在部署的时候不修改任何配置即可接入一种日志实现方案

#### 1.6.2. common-logging

common-logging 是 apache 的一个开源项目。也称Jakarta Commons Logging，缩写 JCL

common-logging 的功能是提供日志功能的 API 接口，本身并不提供日志的具体实现（当然，common-logging 内部有一个 Simple logger 的简单实现，但是功能很弱，直接忽略），而是在运行时动态的绑定日志实现组件来工作（如 log4j、java.util.loggin）

官网地址：http://commons.apache.org/proper/commons-logging/

#### 1.6.3. slf4j

全称为 Simple Logging Facade for Java，即 java 简单日志门面

类似于 Common-Logging，slf4j 是对不同日志框架提供的一个 API 封装，可以在部署的时候不修改任何配置即可接入一种日志实现方案。但是，slf4j 在编译时静态绑定真正的 Log 库。使用 SLF4J 时，如果你需要使用某一种日志实现，那么你必须选择正确的 SLF4J 的 jar 包的集合（各种桥接包）

官网地址：http://www.slf4j.org/

#### 1.6.4. common-logging 与 slf4j 区别

slf4j 库类似于 Apache Common-Logging。但是，它在编译时静态绑定真正的日志库。这点似乎很麻烦，其实也不过是导入桥接 jar 包而已。

slf4j 一大亮点是提供了更方便的日志记录方式：不需要使用`logger.isDebugEnabled()`来解决日志因为字符拼接产生的性能问题。slf4j 的方式是使用{}作为字符串替换符，形式如下

```java
logger.debug("id: {}, name: {} ", id, name);
```

### 1.7. 总结

**综上所述，使用 slf4j + Logback 可谓是目前最理想的日志解决方案了**

## 2. 实施日志解决方案

- 使用日志解决方案基本可分为三步：
    1. 引入 jar 包
    2. 配置
    3. 使用 API
- 常见的各种日志解决方案的第 2 步和第 3 步基本一样，实施上的差别主要在第 1 步，也就是使用不同的库

### 2.1. 引入 jar 包（引入依赖）

**首选推荐使用 slf4j + logback 的组合**，也可以选择 common-logging + log4j

强烈建议不要直接使用日志实现组件(logback、log4j、java.util.logging)，因为无法灵活替换日志库

> 还有一种情况，老项目使用了 common-logging，或是直接使用日志实现组件。如果修改老的代码，工作量太大，需要兼容处理


### 2.2. slf4j 直接绑定日志组件
#### 2.2.1. slf4j + logback

pom.xml 添加依赖。logback-classic-1.0.13.jar 会自动将 slf4j-api-1.7.21.jar 和 logback-core-1.0.13.jar 也添加到项目中

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.0.13</version>
</dependency>
```

#### 2.2.2. slf4j + log4j

pom.xml 添加依赖。slf4j-log4j12-1.7.21.jar 会自动将 slf4j-api-1.7.21.jar 和 log4j-1.2.17.jar 也添加到项目中。

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.21</version>
</dependency>
```

#### 2.2.3. slf4j + java.util.logging

pom.xml 添加依赖。slf4j-jdk14-1.7.21.jar 会自动将 slf4j-api-1.7.21.jar 也添加到项目中

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.21</version>
</dependency>
```

### 2.3. slf4j 兼容非 slf4j 日志组件
#### 2.3.1. 桥接的概念

假如正在开发应用程序所调用的组件当中已经使用了 common-logging，这时需要 jcl-over-slf4j.jar 把日志信息输出重定向到 slf4j-api，slf4j-api 再去调用 slf4j 实际依赖的日志组件。这个过程称为桥接

无论老项目中使用的是 common-logging 或是直接使用 log4j、java.util.logging，都可以使用对应的桥接 jar 包来解决兼容问题

#### 2.3.2. slf4j 兼容 common-logging

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.12</version>
</dependency>
```

#### 2.3.3. slf4j 兼容 log4j

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>log4j-over-slf4j</artifactId>
    <version>1.7.12</version>
</dependency>
```

#### 2.3.4. slf4j 兼容 java.util.logging

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jul-to-slf4j</artifactId>
    <version>1.7.12</version>
</dependency>
```

### 2.4. spring 集成 slf4j

spring 使用的日志解决方案是 common-logging + log4j。所以，需要一个桥接 jar 包：logback-ext-spring。

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.1.3</version>
</dependency>
<dependency>
    <groupId>org.logback-extensions</groupId>
    <artifactId>logback-ext-spring</artifactId>
    <version>0.1.2</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.12</version>
</dependency>
```

### 2.5. common-logging 绑定日志组件

pom.xml 添加依赖

```xml
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

### 2.6. 使用 API
#### 2.6.1. slf4j 用法

使用 slf4j 的 API 很简单。使用LoggerFactory初始化一个Logger实例，然后调用 Logger 对应的打印等级函数就行了

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        String msg = "print log, current level: {}";
        log.trace(msg, "trace");
        log.debug(msg, "debug");
        log.info(msg, "info");
        log.warn(msg, "warn");
        log.error(msg, "error");
    }
}
```

#### 2.6.2. common-logging 用法

- common-logging 用法和 slf4j 几乎一样，但是支持的打印等级多了一个更高级别的：fatal
- common-logging 不支持`{}`替换参数，只能选择拼接字符串这种方式了

```java
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JclTest {
    private static final Log log = LogFactory.getLog(JclTest.class);
    public static void main(String[] args) {
        String msg = "print log, current level: ";
        log.trace(msg + "trace");
        log.debug(msg + "debug");
        log.info(msg + "info");
        log.warn(msg + "warn");
        log.error(msg + "error");
        log.fatal(msg + "fatal");
    }
}
```

## 3. log4j2 配置
### 3.1. 2.1. log4j2 基本配置形式

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Properties>
        <Property name="name1">value</Property>>
        <Property name="name2" value="value2"/>
    </Properties>
    <Filter type="type" ... />
    <Appenders>
        <Appender type="type" name="name">
            <Filter type="type" ... />
        </Appender>
        ...
    </Appenders>
    <Loggers>
        <Logger name="name1">
            <Filter type="type" ... />
        </Logger>
        ...
        <Root level="level">
            <AppenderRef ref="name"/>
        </Root>
    </Loggers>
</Configuration>
```

### 3.2. 配置示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="debug" strict="true" name="XMLConfigTest"
               packages="org.apache.logging.log4j.test">
    <Properties>
        <Property name="filename">target/test.log</Property>
    </Properties>
    <Filter type="ThresholdFilter" level="trace"/>
    <Appenders>
        <Appender type="Console" name="STDOUT">
            <Layout type="PatternLayout" pattern="%m MDC%X%n"/>
            <Filters>
                <Filter type="MarkerFilter" marker="FLOW" onMatch="DENY" onMismatch="NEUTRAL"/>
                <Filter type="MarkerFilter" marker="EXCEPTION" onMatch="DENY" onMismatch="ACCEPT"/>
            </Filters>
        </Appender>
        <Appender type="Console" name="FLOW">
            <Layout type="PatternLayout" pattern="%C{1}.%M %m %ex%n"/><!-- class and line number -->
            <Filters>
                <Filter type="MarkerFilter" marker="FLOW" onMatch="ACCEPT" onMismatch="NEUTRAL"/>
                <Filter type="MarkerFilter" marker="EXCEPTION" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </Appender>
        <Appender type="File" name="File" fileName="${filename}">
            <Layout type="PatternLayout">
                <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
            </Layout>
        </Appender>
    </Appenders>
    <Loggers>
        <Logger name="org.apache.logging.log4j.test1" level="debug" additivity="false">
            <Filter type="ThreadContextMapFilter">
                <KeyValuePair key="test" value="123"/>
            </Filter>
            <AppenderRef ref="STDOUT"/>
        </Logger>
        <Logger name="org.apache.logging.log4j.test2" level="debug" additivity="false">
            <AppenderRef ref="File"/>
        </Logger>
        <Root level="trace">
            <AppenderRef ref="STDOUT"/>
        </Root>
    </Loggers>
</Configuration>
```

## 4. logback 配置
### 4.1. 基本配置

SpringBoot工程自带 logback 和 slf4j 的依赖，所以重点放在编写配置文件上，需要引入什么依赖，日志依赖冲突统统都不需要管

logback框架会默认加载 classpath 下命名为 `logback-spring` 或 `logback` 的配置文件。将所有日志都存储在一个文件中文件大小也随着应用的运行越来越大并且不好排查问题，正确的做法应该是将error日志和其他日志分开，并且不同级别的日志根据时间段进行记录存储。

```xml
<?xml version="1.0" encoding="utf-8"?>
<configuration>
    <property resource="logback.properties"/>
    <appender name="CONSOLE-LOG" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符，彩色日志设置-->
            <!--<pattern>%red(%d{yyyy-MM-dd HH:mm:ss.SSS}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{36}) - %cyan(%msg%n)</pattern>-->
        </layout>
    </appender>
    <!-- 获取比info级别高(包括info级别)但除error级别的日志 -->
    <appender name="INFO-LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>DENY</onMatch>
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <encoder>
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
        </encoder>

        <!-- 滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 路径 -->
            <fileNamePattern>${LOG_INFO_HOME}//%d.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>
    <appender name="ERROR-LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
        </encoder>
        <!-- 滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 路径 -->
            <fileNamePattern>${LOG_ERROR_HOME}//%d.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <root level="info">
        <appender-ref ref="CONSOLE-LOG"/>
        <appender-ref ref="INFO-LOG"/>
        <appender-ref ref="ERROR-LOG"/>
    </root>
</configuration>
```

### 4.2. 配置文件各标签作用

#### 4.2.1. `<configuration>`

- 作用：`<configuration>` 是 logback 配置文件的根元素。
- 要点：它有 `<appender>`、`<logger>`、`<root>` 三个子元素。

#### 4.2.2. `<appender>`

- 作用：将记录日志的任务委托给名为 appender 的组件。
- 要点：可以配置零个或多个；它有 `<file>`、`<filter>`、`<layout>`、`<encoder>` 四个子元素。
- 属性：
    - name：设置 appender 名称。
    - class：设置具体的实例化类。

#### 4.2.3. `<file>`

- 作用：设置日志文件路径。

#### 4.2.4. `<filter>`

- 作用：设置过滤器。通过使用该标签指定过滤策略
- 要点：可以配置零个或多个。

#### 4.2.5. `<layout>`

- 作用：设置 appender。
- 要点：可以配置零个或一个。
- 属性：
    - class：设置具体的实例化类。

#### 4.2.6. `<encoder>`

- 作用：设置编码。使用该标签下的标签指定日志输出格式
- 要点：可以配置零个或多个。
- 属性：
    - class：设置具体的实例化类。

#### 4.2.7. `<logger>`

- 作用：设置 logger。
- 要点：可以配置零个或多个。
- 属性：
    - name
    - level：设置日志级别。不区分大小写。可选值：TRACE、DEBUG、INFO、WARN、ERROR、ALL、OFF。
    - additivity：可选值：true 或 false。

#### 4.2.8. `<appender-ref>`

- 作用：appender 引用。
- 要点：可以配置零个或多个。

#### 4.2.9. `<root>`

- 作用：设置根 logger。必填标签，用来指定最基础的日志输出级别
- 要点：只能配置一个；除了 level，不支持任何属性。level 属性和 `<logger>` 中的相同；有一个子元素 `<appender-ref>`，与 `<logger>` 中的相同。

### 4.3. logback 高级特性异步输出日志

之前的日志配置方式是基于同步的，每次日志输出到文件都会进行一次磁盘IO。采用异步写日志的方式而不让此次写日志发生磁盘IO，阻塞线程从而造成不必要的性能损耗。异步输出日志的方式很简单，添加一个基于异步写日志的appender，并指向原先配置的appender即可

```xml
<!-- 异步输出 -->
<appender name="ASYNC-INFO" class="ch.qos.logback.classic.AsyncAppender">
    <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
    <discardingThreshold>0</discardingThreshold>
    <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
    <queueSize>256</queueSize>
    <!-- 添加附加的appender,最多只能添加一个 -->
    <appender-ref ref="INFO-LOG"/>
</appender>

<appender name="ASYNC-ERROR" class="ch.qos.logback.classic.AsyncAppender">
    <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
    <discardingThreshold>0</discardingThreshold>
    <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
    <queueSize>256</queueSize>
    <!-- 添加附加的appender,最多只能添加一个 -->
    <appender-ref ref="ERROR-LOG"/>
</appender>
```

### 4.4. 完整的 logback.xml 参考示例

在下面的配置文件中，为项目代码（根目录：org.zp.notes.spring）设置了五种等级：TRACE、DEBUG、INFO、WARN、ERROR，优先级依次从低到高。

因为关注 spring 框架本身的一些信息，增加了专门打印 spring WARN 及以上等级的日志。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- logback中一共有5种有效级别，分别是TRACE、DEBUG、INFO、WARN、ERROR，优先级依次从低到高 -->
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="DIR_NAME" value="spring-helloworld"/>
    <!-- 将记录日志打印到控制台 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <!-- RollingFileAppender begin -->
    <appender name="ALL" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/all.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>30MB</maxFileSize>
        </triggeringPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/warn.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/info.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/debug.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="TRACE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/trace.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>TRACE</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="SPRING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 根据时间来制定滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${user.dir}/logs/${DIR_NAME}/springframework.%d{yyyy-MM-dd}.log
            </fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <!-- 根据文件大小来制定滚动策略 -->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <maxFileSize>10MB</maxFileSize>
        </triggeringPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%-5p] %c{36}.%M - %m%n</pattern>
        </encoder>
    </appender>
    <!-- RollingFileAppender end -->
    <!-- logger begin -->
    <!-- 本项目的日志记录，分级打印 -->
    <logger name="org.zp.notes.spring" level="TRACE" additivity="false">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="ERROR"/>
        <appender-ref ref="WARN"/>
        <appender-ref ref="INFO"/>
        <appender-ref ref="DEBUG"/>
        <appender-ref ref="TRACE"/>
    </logger>
    <!-- SPRING框架日志 -->
    <logger name="org.springframework" level="WARN" additivity="false">
        <appender-ref ref="SPRING"/>
    </logger>
    <root level="TRACE">
        <appender-ref ref="ALL"/>
    </root>
    <!-- logger end -->
</configuration>
```

## 5. log4j 配置
### 5.1. 完整的 log4j.xml 参考示例

log4j 的配置文件一般有 xml 格式或 properties 格式。这里为了和 logback.xml 做个对比，就不介绍 properties 了，其实也没太大差别

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j='http://jakarta.apache.org/log4j/'>
    <appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss,SSS\} [%-5p] [%t] %c{36\}.%M - %m%n"/>
        </layout>
        <!--过滤器设置输出的级别-->
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="levelMin" value="debug"/>
            <param name="levelMax" value="fatal"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>

    <appender name="ALL" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="${user.dir}/logs/spring-common/jcl/all"/>
        <param name="Append" value="true"/>
        <!-- 每天重新生成日志文件 -->
        <param name="DatePattern" value="'-'yyyy-MM-dd'.log'"/>
        <!-- 每小时重新生成日志文件 -->
        <!--<param name="DatePattern" value="'-'yyyy-MM-dd-HH'.log'"/>-->
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss,SSS\} [%-5p] [%t] %c{36\}.%M - %m%n"/>
        </layout>
    </appender>
    <!-- 指定logger的设置，additivity指示是否遵循缺省的继承机制-->
    <logger name="org.zp.notes.spring" additivity="false">
        <level value="error"/>
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="ALL"/>
    </logger>
    <!-- 根logger的设置-->
    <root>
        <level value="warn"/>
        <appender-ref ref="STDOUT"/>
    </root>
</log4j:configuration>
```

## 6. 实践过程需要注意的问题
### 6.1. 案例1

在MoonZero个人管理系统项目中，出现在开发环境记录中文日志正常，但到部署到tomcat后，记录的日志就是出现乱码

后来查询资料得出结论，因为当时是用tomcat9.0版本，好像tomcat8开始就默认是以nio作为通信模式，所以在配置logback日志时，需要增加`<charset class="java.nio.charset.Charset">UTF-8</charset>`，不然就会出现在开发环境正常，而到生产环境就出现中文乱码的问题

```xml
<!-- Console 输出设置 -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        <charset class="java.nio.charset.Charset">UTF-8</charset>
    </encoder>
</appender>

<!-- 按照每天生成日志文件 -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!--日志文件输出的文件名-->
        <fileNamePattern>${LOG_HOME}/jav.%d{yyyy-MM-dd}.log</fileNamePattern>
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        <!--
            注意：使用tomcat9.0版本，因为tomcat只支付nio，所以此处需要指定java.nio.charset.Charset才不会出现中文乱码的问题
         -->
        <charset class="java.nio.charset.Charset">UTF-8</charset>
    </encoder>
</appender>
```

### 6.2. 修改日志打印对象为JSON格式

在开发的时候，经常要打印日志，有的时候会在一些代码的关键节点处进行日志输出。

使用logback/log4j等原生的日志框架，在日志输出的时候可能会遇到一个问题，那就是打印对象的时候，如以下代码：

```java
log.info("req = {}", aRequest);
```

打印结果却是以下形式：

```console
com.hollis.java.ways.ApplyRequest@39ddf169
```

其实原因比较简单，那就是要打印的对象没有重写toString方法，这样无法将该对象的参数打印出来。所以，为了可以把对象的值都打印出来，一般要求对于自己定义的入参、出参等定义toString方法。

但是有些时候，使用的是外部定义的request和response对象，他们并没有覆盖toString，当对这些对象打印的时候，就会出现以上问题。一般简单的解决办法是，可以通过JSON把对象转成String，如：

```java
log.info("req = {}", JSON.toJSONString(aRequest));
```

**借助logback(log4j也有类似的功能)的MessageConverter。无侵入性的解决这个问题**

1. 自定义一个Layout

```java
/**
 * 参数JSON格式化类
 */
public class ArgumentJsonFormatLayout extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        try {
            return MessageFormatter.arrayFormat(event.getMessage(), Stream.of(event.getArgumentArray())
                .map(JSON::toJSONString).toArray()).getMessage();
        } catch (Exception e) {
            return event.getMessage();
        }
    }
}
```

2. 在logback中配置上这个Layout

```xml
<configuration>
    <conversionRule conversionWord="m" converterClass="com.moon.java.ways.ArgumentJsonFormatLayout"/>
 </configuration>
```

通过以上设置，就可以直接使用`log.info("req = {}", obj)`这样的形式记录日志了
