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

# 日志级别与记录日志规范

对于记录日志来说，只是在方法开始、结束、异常时输出一些什么，那是绝对不够的，这样的日志对于日志分析没有任何意义。如果在方法的开始和结束整个日志，那方法中呢？如果方法中没有日志的话，那就完全失去了日志的意义！如果应用出现问题要查找由什么原因造成的，也没有什么作用。

## 1. 常用的日志级别

- DEBUG
- INFO
- WARN
- ERROR

这是四个最经常用的日志级别，还有四个是不怎么用到的

### 1.1. DEBUG

DEBUG级别的主要输出调试性质的内容，该级别日志主要用于在开发、测试阶段输出。该级别的日志应尽可能地详尽，便于在开发、测试阶段出现问题或者异常时，对其进行分析。

### 1.2. INFO

INFO 级别的主要输出提示性质的内容，该级别日志主要用于生产环境的日志输出。该级别或更高级别的日志不要出现在循环中，可以在循环开始或者结束后输出循环的次数，以及一些其他重要的数据。

INFO 级别日志原则是在生产环境中，通过 INFO 和更高级别的日志，可以了解系统的运行状况，以及出现问题或者异常时，能快速地对问题进行定位，还原当时调用的上下文数据，能重现问题,那么INFO级别的日志主要是用于哪些方面呢？

1. 应用启动时所加载的配置参数值（比如：连接参数、线程池参数、超时时间等，以及一些与环境相关的配置，或者是整个配置参数）
2. 一些重要的依赖注入对象的类名
3. 方法（服务方法）的输入参数值、返回值，由于一些方法入参的值非常多，只在入口处输出一次就可以了，在服务方法内部或者调用非服务方法时就不需要再输出了
4. 方法中重要的部分，比如：从数据库中所获取较为重要的数据，以及调用第三方接口的输入参数值和接口返回值

这个INFO应该用来反馈系统的当前状态给最终用户的，所以，在这里输出的信息，应该对最终用户具有实际意义，也就是最终用户要能够看得明白是什么意思才行。从某种角度上说，INFO 输出的信息可以看作是软件产品的一部分（就像那些交互界面上的文字一样），所以需要谨慎对待，不可随便。此输出级别常用语业务事件信息。例如某项业务处理完毕，或者业务处理过程中的一些信息。

此输出级别也常用于输出一些对系统有比较大的影响的需要被看到的message，例如数据库更新，系统发送了额外的请求等。

### 1.3. WARN

WARN 级别的主要输出警告性质的内容，这些内容是可以预知且是有规划的，比如，某个方法入参为空或者该参数的值不满足运行该方法的条件时。在 WARN 级别的时应输出较为详尽的信息，以便于事后对日志进行分析，不要直接写成：

```java
log.warn("name is null");
```

因为除了输出警告的原因之外，还需要将其他参数内容都输出，以便于有更多的信息供为日志分析的参考。所以可以写成如下：

```java
log.warn("[{}] name is null, ignore the method, arg0: {}, arg1: {}", username, param1, param2);
```

### 1.4. ERROR

ERROR 级别主要针对于一些不可预知的信息，诸如：错误、异常等，比如，在 catch 块中抓获的网络通信、数据库连接等异常，若异常对系统的整个流程影响不大，可以使用 WARN 级别日志输出。在输出 ERROR 级别的日志时，尽量多地输出方法入参数、方法执行过程中产生的对象等数据，在带有错误、异常对象的数据时，需要将该对象一并输出.

这个时候也就是说，发生了必须马上处理的错误。此类错误出现以后可以允许程序继续运行，但必须马上修正，如果不修正，就会导致不能完成相应的业务。

## 2. 日志优先级别

优先级从高到低依次为: ERROR > WARN > INFO > DEBUG

如果将log level设置在某一个级别上，那么比此级别优先级高的log都能打印出来。例如：如果设置优先级为`WARN`那么`ERROR` `WARN` 2个级别的log能正常输出，而`INFO` `DEBUG`级别的log则会被忽略。

## 3. 基本日志规范

### 3.1. 一些个人整理的规范

- 在一个对象中通常只使用一个Logger对象，Logger应该是使用`private static final`修饰的，只有在少数需要在构造函数中传递logger的情况下才使用`private final`。

```java
private static final Logger_LOG = LoggerFactory.getLogger(Test.class)
```

- 输出Exceptions的全部Throwable信息，因为`logger.error(msg)`和`logger.error(msg,e.getMessage())`这样的日志输出方法会丢失掉最重要的StackTrace信息。

```java
try {
    ...
} catch (Exception e) {
    Log.error("xxx", e);
}
```

- 不允许记录日志后又抛出异常，因为这样会多次记录日志，只允许记录一次日志。以下是错误的示范：

```java
try {
    ...
} catch (Exception e) {
    Log.error("xxx", e);
    thorw new LogException("xxx", e);
}
```

- 日志性能的考虑，如果代码为核心代码，执行频率非常高，则输出日志建议增加判断，尤其是低级别的输出`debug`、`info`、`warn`。

### 3.2. 阿里巴巴Java开发手册日志规约（节选嵩山版）

- <font color=red>**【强制】**</font>应用中不可直接使用日志系统（Log4j、Logback）中的 API，而应依赖使用日志框架（SLF4J、JCL--Jakarta Commons Logging）中的 API，使用门面模式的日志框架，有利于维护和各个类的日志处理方式统一。*说明：日志框架（SLF4J、JCL--Jakarta Commons Logging）的使用方式（推荐使用 SLF4J）*

```java
/* 使用 SLF4J：*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
private static final Logger logger = LoggerFactory.getLogger(Test.class);

/* 使用 JCL：*/
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
private static final Log log = LogFactory.getLog(Test.class);
```

- <font color=red>**【强制】**</font>所有日志文件至少保存 15 天，因为有些异常具备以“周”为频次发生的特点。对于当天日志，以“`应用名.log`”来保存，保存在`/home/admin/应用名/logs/`目录下，过往日志格式为: `{logname}.log.{保存日期}`，日期格式：`yyyy-MM-dd`，以 aap 应用为例：

> - 日志保存位置：/home/admin/aapserver/logs/aap.log
> - 历史日志名称为：aap.log.2016-08-01

- <font color=red>**【强制】**</font>应用中的扩展日志（如打点、临时监控、访问日志等）命名方式：`appName_logType_logName.log`。这种命名的好处：通过文件名就可知道日志文件属于什么应用，什么类型，什么目的，也有利于归类查找。*说明：推荐对日志进行分类，如将错误日志和业务日志分开存放，便于开发人员查看，也便于通过日志对系统进行及时监控。*

> - `logType`: 日志类型，如 stats/monitor/access 等；
> - `logName`: 日志描述
>
> 示例：mppserver 应用中单独监控时区转换异常，如：`mppserver_monitor_timeZoneConvert.log`

- <font color=red>**【强制】**</font>在日志输出时，字符串变量之间的拼接使用占位符的方式。*说明：因为`String`字符串的拼接会使用`StringBuilder`的`append()`方式，有一定的性能损耗。使用占位符仅是替换动作，可以有效提升性能。*

```java
logger.debug("Processing trade with id: {} and symbol: {}", id, symbol);
```

- <font color=red>**【强制】**</font>对于 trace/debug/info 级别的日志输出，必须进行日志级别的开关判断。*说明：虽然在 `debug(参数)` 的方法体内第一行代码`isDisabled(Level.DEBUG_INT)`为真时（Slf4j 的常见实现 Log4j 和 Logback），就直接return，但是参数可能会进行字符串拼接运算。此外，如果`debug(getName())`这种参数内有`getName()`方法调用，无谓浪费方法调用的开销。*

```java
// 如果判断为真，那么可以输出 trace 和 debug 级别的日志
if (logger.isDebugEnabled()) {
    logger.debug("Current ID is: {} and name is: {}", id, getName());
}
```

- <font color=red>**【强制】**</font>避免重复打印日志，浪费磁盘空间，务必在日志配置文件中设置 `additivity=false`。

```java
<logger name="com.taobao.dubbo.config" additivity="false">
```

- <font color=red>**【强制】**</font>生产环境禁止直接使用 `System.out` 或 `System.err` 输出日志或使用 `e.printStackTrace()` 打印异常堆栈。*说明：标准日志输出与标准错误输出文件每次 Jboss 重启时才滚动，如果大量输出送往这两个文件，容易造成文件大小超过操作系统大小限制。*

- <font color=red>**【强制】**</font>日志打印时禁止直接用 JSON 工具将对象转换成 String。

> - 说明：如果对象里某些 `get` 方法被覆写，存在抛出异常的情况，则可能会因为打印日志而影响正常业务流程的执行。
> - 正例：打印日志时仅打印出业务相关属性值或者调用其对象的`toString()`方法。

- <font color=gold>**【推荐】**</font>谨慎地记录日志。生产环境禁止输出 debug 日志；有选择地输出 info 日志；如果使用 warn 来记录刚上线时的业务行为信息，一定要注意日志输出量的问题，避免把服务器磁盘撑爆，并记得及时删除这些观察日志。*说明：大量地输出无效日志，不利于系统性能提升，也不利于快速定位错误点。*
- <font color=gold>**【推荐】**</font>可以使用 warn 日志级别来记录用户输入参数错误的情况，避免用户投诉时，无所适从。如非必要，请不要在此场景打出 error 级别，避免频繁报警。*说明：注意日志输出的级别，error 级别只记录系统逻辑出错、异常或者重要的错误信息。*
