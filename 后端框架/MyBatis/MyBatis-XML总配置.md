## 1. MyBatis 总配置文件

### 1.1. 总配置内容概述

MyBatis 总配置文件（SqlMapConfig.xml）中配置的内容和顺序如下：（必须按顺序，否则为报错）

- configuration（配置根标签）
    - properties（属性）
    - settings（设置全局配置参数）
    - typeAliases（类型别名）
    - typeHandlers（类型处理器）
    - objectFactory（对象工厂）
    - plugins（插件）
    - environments（环境集合配置）
        - environment（环境子属性对象）
            - transactionManager（事务管理器）
            - dataSource（数据源）
    - databaseIdProvider（数据库厂商标识）
    - mappers（映射器）

### 1.2. Mybatis 3.5.0 全配置示例(demo项目配置更新优先)

> 参考Mybatis源码学习项目：https://github.com/MooNkirA/mybatis-note
>
> \mybatis-note\mybatis-demo-2019\src\main\resources\mybatis-config-3.5.0-all.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- 参数设置 -->
    <settings>
        <!-- 这个配置使全局的映射器启用或禁用缓存 -->
        <setting name="cacheEnabled" value="true"/>
        <!-- 全局启用或禁用延迟加载。当禁用时，所有关联对象都会即时加载 -->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 当启用时，有延迟加载属性的对象在被调用时将会完全加载任意属性。否则，每种属性将会按需要加载 -->
        <setting name="aggressiveLazyLoading" value="true"/>
        <!-- 允许或不允许多种结果集从一个单独的语句中返回（需要适合的驱动） -->
        <setting name="multipleResultSetsEnabled" value="true"/>
        <!-- 使用列标签代替列名。不同的驱动在这方便表现不同。参考驱动文档或充分测试两种方法来决定所使用的驱动 -->
        <setting name="useColumnLabel" value="true"/>
        <!-- 允许JDBC支持生成的键。需要适合的驱动。如果设置为true则这个设置强制生成的键被使用，尽管一些驱动拒绝兼容但仍然有效（比如Derby） -->
        <setting name="useGeneratedKeys" value="true"/>
        <!-- 指定MyBatis如何自动映射列到字段/属性。PARTIAL只会自动映射简单，没有嵌套的结果。FULL会自动映射任意复杂的结果（嵌套的或其他情况） -->
        <setting name="autoMappingBehavior" value="PARTIAL"/>
        <!--当检测出未知列（或未知属性）时，如何处理，默认情况下没有任何提示，这在测试的时候很不方便，不容易找到错误。 NONE : 不做任何处理
            (默认值) WARNING : 警告日志形式的详细信息 FAILING : 映射失败，抛出异常和详细信息 -->
        <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
        <!-- 配置默认的执行器。SIMPLE执行器没有什么特别之处。REUSE执行器重用预处理语句。BATCH执行器重用语句和批量更新 -->
        <setting name="defaultExecutorType" value="SIMPLE"/>
        <!-- 设置超时时间，它决定驱动等待一个数据库响应的时间 -->
        <setting name="defaultStatementTimeout" value="25000"/>
        <!--设置查询返回值数量，可以被查询数值覆盖 -->
        <setting name="defaultFetchSize" value="100"/>
        <!-- 允许在嵌套语句中使用分页 -->
        <setting name="safeRowBoundsEnabled" value="false"/>
        <!-- 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射。 -->
        <setting name="mapUnderscoreToCamelCase" value="false"/>
        <!--MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。
            默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession
            的不同调用将不会共享数据。 -->
        <setting name="localCacheScope" value="SESSION"/>
        <!-- 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。 某些驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如
            NULL、VARCHAR OTHER。 -->
        <setting name="jdbcTypeForNull" value="OTHER"/>
        <!-- 指定哪个对象的方法触发一次延迟加载。 -->
        <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    </settings>

    <!-- 别名定义 -->
    <typeAliases>
        <typeAlias alias="pageAccessURL" type="com.lgm.mybatis.model.PageAccessURL"/>
    </typeAliases>

    <!-- 自定义类型处理器 -->
    <typeHandlers>
        <!-- <typeHandler handler="com.xhm.util.BooleanTypeHandlder" /> -->
        <!-- 扫描整个包下的自定义类型处理器 -->
        <package name="com.xhm.util"/>
    </typeHandlers>

    <!--plugins插件之 分页拦截器 -->
    <plugins>
        <plugin interceptor="com.xhm.util.PageInterceptor"></plugin>
    </plugins>

    <!--配置environment环境 -->
    <environments default="development">
        <!-- 环境配置1，每个SqlSessionFactory对应一个环境 -->
        <environment id="development1">
            <!-- 事务配置 type = JDBC、MANAGED
                    1.JDBC:这个配置直接简单使用了JDBC的提交和回滚设置。它依赖于从数据源得到的连接来管理事务范围。
                    2.MANAGED:这个配置几乎没做什么。它从来不提交或回滚一个连接。而它会让容器来管理事务的整个生命周期（比如Spring或JEE应用服务器的上下文）。
                    默认情况下它会关闭连接。然而一些容器并不希望这样，因此如果你需要从连接中停止它，将closeConnection属性设置为false
            -->
            <transactionManager type="JDBC"/>
            <!--< transactionManager type="MANAGED">
                <property name="closeConnection"
                          value="false"/>
            </transactionManager> -->
            <!-- 数据源类型：type = UNPOOLED、POOLED、JNDI
                    1.UNPOOLED：这个数据源的实现是每次被请求时简单打开和关闭连接。它有一点慢，这是对简单应用程序的一个很好的选择，因为它不需要及时的可用连接。
                    不同的数据库对这个的表现也是不一样的，所以对某些数据库来说配置数据源并不重要，这个配置也是闲置的
                    2.POOLED：这是JDBC连接对象的数据源连接池的实现，用来避免创建新的连接实例时必要的初始连接和认证时间。
                    这是一种当前Web应用程序用来快速响应请求很流行的方法。
                    3.JNDI：这个数据源的实现是为了使用如Spring或应用服务器这类的容器，容器可以集中或在外部配置数据源，然后放置一个JNDI上下文的引用
             -->
            <dataSource type="UNPOOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/xhm"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
                <!-- 默认连接事务隔离级别 -->
                <!-- <property name="defaultTransactionIsolationLevel" value=""/> -->
            </dataSource>
        </environment>

        <!-- 环境配置2 -->
        <environment id="development2">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/xhm"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
                <!-- 在任意时间存在的活动（也就是正在使用）连接的数量 -->
                <property name="poolMaximumActiveConnections" value="10"/>
                <!-- 任意时间存在的空闲连接数 -->
                <property name="poolMaximumIdleConnections" value="5"/>
                <!-- 在被强制返回之前，池中连接被检查的时间 -->
                <property name="poolMaximumCheckoutTime" value="20000"/>
                <!-- 这是给连接池一个打印日志状态机会的低层次设置，还有重新尝试获得连接，这些情况下往往需要很长时间（为了避免连接池没有配置时静默失败） -->
                <property name="poolTimeToWait" value="20000"/>
                <!-- 发送到数据的侦测查询，用来验证连接是否正常工作，并且准备接受请求。 -->
                <property name="poolPingQuery" value="NO PING QUERY SET"/>
                <!-- 这是开启或禁用侦测查询。如果开启，你必须用一个合法的SQL语句（最好是很快速的）设置poolPingQuery属性 -->
                <property name="poolPingEnabled" value="false"/>
                <!-- 这是用来配置poolPingQuery多次时间被用一次。这可以被设置匹配标准的数据库连接超时时间，来避免不必要的侦测 -->
                <property name="poolPingConnectionsNotUsedFor" value="0"/>
            </dataSource>
        </environment>

        <!-- 环境配置3 -->
        <environment id="development3">
            <transactionManager type="JDBC"/>
            <dataSource type="JNDI">
                <property name="data_source" value="java:comp/env/jndi/mybatis"/>
                <property name="env.encoding" value="UTF8"/>
                <!-- <property name="initial_context" value=""/>
                <property name="env.encoding" value="UTF8"/> -->
            </dataSource>
        </environment>
    </environments>

    <!-- 映射文件，mapper的配置文件 -->
    <mappers>
        <!-- 直接映射到相应的mapper文件 -->
        <mapper resource="com/xhm/mapper/UserMapper.xml"/>
        <!-- 扫描包路径下所有xxMapper.xml文件 -->
        <package name="com.xhm.mapper"/>
    </mappers>

</configuration>
```

## 2. properties 属性标签

### 2.1. 配置属性

1. `resource`属性：指定本地属性文件的位置
2. `url`属性：指定网络上的配置文件位置（用于放置多个服务器统一使用一份配置文件）
3. 使用内部`property`标签，定义属性。子标签示例：`<property name="" value=""/>`
4. 加载顺序：先加载内部property标签定义的属性；再加上属性文件中定义的属性。如果有相同的属性，属性文件中的属性会覆盖内部property标签定义的属性

> 注：在配置文件中使用el表达式获取值

sqlMapConfig.xml文件`<properties>`标签示例：

```xml
<!-- 加载属性文件和定义属性 ,说明：
	1.resource：指定本地的配置文件位置
	2.url：指定网络上的配置文件位置（http://127.0.0.1:8080/db.properties）
	3.使用内部property标签，定义属性
	4.加载顺序：先加载内部property标签定义的属性；再加上属性文件中定义的属性。如果有相同的属性，
		属性文件中的属性，覆盖内部property标签定义的属性
-->
<!-- <properties resource="db.properties" /> -->
<!-- <properties resource="http://localhost:8080/db.properties" /> -->
<properties resource="db.properties">
	<property name="username" value="rootX"/>
	<property name="password" value="123456"/>
</properties>
...省略内容
<dataSource type="POOLED">
	<property name="driver" value="${db.driver}" />
	<property name="url" value="${db.url}" />
	<property name="username" value="${db.username}" />
	<property name="password" value="${db.password}" />
</dataSource>
```

除了通过`<properties>`标签的方式，也可以在 `SqlSessionFactoryBuilder.build()` 方法中传入属性值。

```java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
// ... 或者 ...
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, props);
```

<font color=red>**注意：如果一个属性在不只一个地方进行了配置，MyBatis将按照下面的顺序来加载属性**</font>：

1. 在properties元素体内定义的属性首先被读取。
2. 然后会读取properties元素中resource或url加载的属性，它会覆盖已读取的同名属性。
3. 最后读取作为方法参数传递的属性，并覆盖之前读取过的同名属性。

因此，<font color=red>**通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的则是 properties 元素中指定的属性。**</font>

### 2.2. 属性默认值

从 MyBatis 3.4.2 开始，可以为占位符指定一个默认值。例如：

```xml
<dataSource type="POOLED">
  ...省略内容
  <property name="username" value="${username:ut_user}"/> <!-- 如果属性 'username' 没有被配置，'username' 属性的值将为 'ut_user' -->
</dataSource>
```

这个特性默认是关闭的。要启用这个特性，需要添加一个特定的属性来开启这个特性。例如：

```xml
<properties resource="org/mybatis/example/config.properties">
  ...省略内容
  <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/> <!-- 启用默认值特性 -->
</properties>
```

### 2.3. 属性名中的“:”字符处理

**提示**：如果在属性名中使用了"`:`"字符（如：`db:username`），或者在 SQL 映射中使用了 OGNL 表达式的三元运算符（如：`${tableName != null ? tableName : 'global_constants'}`），就需要设置特定的属性来修改分隔属性名和默认值的字符。例如：

```xml
<properties resource="org/mybatis/example/config.properties">
  ...省略内容
  <property name="org.apache.ibatis.parsing.PropertyParser.default-value-separator" value="?:"/> <!-- 修改默认值的分隔符 -->
</properties>
```

```xml
<dataSource type="POOLED">
  ...省略内容
  <property name="username" value="${db:username?:ut_user}"/>
</dataSource>
```

## 3. settings 设置标签

### 3.1. 配置示例

1. 在`<settings>`标签中，配置用生成的主键值

```xml
<setting name="useGeneratedKeys" value="true" />
```

2. 在`<settings>`标签中，配置开启驼峰命名(方便自动映射)。

```xml
<setting name="mapUnderscoreToCamelCase" value="true" />
```

> 效果说明：当数据库表的字段为dept_id，会自动转换成deptId

### 3.2. 相关设置含义清单

> 这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。 下表描述了设置中各项设置的含义、默认值等。(官网提供)

https://mybatis.org/mybatis-3/zh/configuration.html#%E8%AE%BE%E7%BD%AE%EF%BC%88settings%EF%BC%89

一个配置完整的 settings 元素的示例如下：

```xml
<settings>
  <setting name="cacheEnabled" value="true"/>
  <setting name="lazyLoadingEnabled" value="true"/>
  <setting name="multipleResultSetsEnabled" value="true"/>
  <setting name="useColumnLabel" value="true"/>
  <setting name="useGeneratedKeys" value="false"/>
  <setting name="autoMappingBehavior" value="PARTIAL"/>
  <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
  <setting name="defaultExecutorType" value="SIMPLE"/>
  <setting name="defaultStatementTimeout" value="25"/>
  <setting name="defaultFetchSize" value="100"/>
  <setting name="safeRowBoundsEnabled" value="false"/>
  <setting name="mapUnderscoreToCamelCase" value="false"/>
  <setting name="localCacheScope" value="SESSION"/>
  <setting name="jdbcTypeForNull" value="OTHER"/>
  <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
</settings>
```

## 4. typeAliases 类型别名标签

### 4.1. mybatis 本身所支持的别名

|    别名     | 映射类型 |    别名     |  映射类型   |
| ---------- | ------- | ---------- | ---------- |
| `_byte`    | byte    | double     | Double     |
| `_long`    | long    | float      | Float      |
| `_short`   | short   | boolean    | Boolean    |
| `_int`     | int     | date       | Date       |
| `_integer` | int     | decimal    | BigDecimal |
| `_double`  | double  | bigdecimal | BigDecimal |
| `_float`   | float   | object     | Object     |
| `_boolean` | boolean | map        | Map        |
| string     | String  | hashmap    | HashMap    |
| byte       | Byte    | list       | List       |
| long       | Long    | arraylist  | ArrayList  |
| short      | Short   | collection | Collection |
| int        | Integer | iterator   | Iterator   |
| integer    | Integer |            |            |

说明：

1. 内置别名可以直接使用
2. 别名不区分大小写

*注：mybatis内置别名中，没有配置set集合，所以mybatis中使用List比较多*

### 4.2. 自定义别名

#### 4.2.1. 方式1：typeAlias 子标签

1. `type`属性：指定别名的类型
2. 默认使用类的名称，作为别名的名称
3. `alias`：指定别名的名称

sqlMapConfig.xml文件配置示例：

```xml
<!-- 自定义别名配置 -->
<typeAliases>
	<!-- 配置用户的别名，说明：
		1.type：指定别名的类型
		2.默认使用类的名称，作为别名的名称
		3.alias：指定别名的名称
	 -->
	<typeAlias type="com.moon.entity.User" alias="user"/>
</typeAliases>
```

Mapper.xml 文件配置示例：

```xmL
<select id="findUserById" parameterType="int" resultType="user">
	select * from `user` where id=#{id}
</select>
```

#### 4.2.2. 方式2：package 子标签

1. `name`属性：配置要扫描的包
2. 默认都使用类的名称作为别名的名称
3. 如果有多个包，在同一个父包，配置父包即可
4. 如果不在同一个父下，配置多个`<package>`

sqlMapConfig.xml文件包扫描方式配置别名示例：

```xml
<typeAliases>
	<!-- 包扫描方式配置别名，说明：
		1.name：配置要扫描的包
		2.默认都使用类的名称作为别名的名称
		3.如果有多个包，在同一个父包，配置父包即可
		4.如果不在同一个父包下，配置多个package
		5.在企业项目中，推荐使用包扫描方式
	 -->
	 <package name="com.moon.entity"/>
</typeAliases>
```

<font color=purple>*注：实现开发中，推荐使用包扫描方式*</font>

每一个在包`com.moon.entity`中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。比如`com.moon.entity.Author`的别名为`author`；若有注解，则别名为其注解值。见下面的例子：

```java
@Alias("author")
public class Author {
    ...
}
```

### 4.3. 配置别名示例

sqlMapConfig.xml文件综合配置别名示例：

```xmL
<typeAliases>
	<!-- 单个别名定义 -->
	<typeAlias alias="user" type="com.moon.mybatis.pojo.User" />
	<!-- 批量别名定义，扫描整个包下的类，别名为类名（大小写不敏感） -->
	<package name="com.moon.mybatis.pojo" />
	<package name="其它包" />
</typeAliases>
```

## 5. （待整理）environments 环境配置标签

> TODO: 待整理

## 6. mappers 映射器标签

`<mappers>`映射器标签用于定义 SQL 映射语句，可以使用相对于类路径的资源引用，或完全限定资源定位符（包括 `file:///` 形式的 URL），或类名和包名等。

### 6.1. mapper 标签的 resource 属性

使用相对于类路径的资源（现在的使用方式），resource：指定配置文件的位置。如下：

```xml
<mapper resource="sqlmap/User.xml" />
```

### 6.2. mapper 标签的 url 属性

url引用的完全限定名（包括`file:///URLs`）

```xml
<!-- 使用完全限定资源定位符（URL） -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
```

### 6.3. mapper 标签的 class 属性

使用mapper接口类路径。注意：此种方法要求mapper接口名称和mapper映射文件名称相同，且放在同一个目录中。

```xml
<mappers>
	<!--
		<mapper class=" " />子标签
		使用mapper接口类路径
		此种方法要求mapper接口名称和mapper映射文件名称相同，且放在同一个目录中。
	-->
	<mapper class="com.moon.mapper.UserMapper"/>
</mappers>
```

### 6.4. package 标签的 name 属性

包扫描方式配置映射文件，扫描该包下的所有mapper接口。其中`name`属性是配置需要扫描的包名。包扫描方式使用要求如下：

1. 前提是mapper代理开发方法
2. 要求mapper映射文件，与mapper接口要放在同一目录
3. 要求mapper映射文件的名称，与mapper接口的名称要一致

```xml
<mappers>
	<!-- 包扫描方式配置映射文件，说明：
		1.前提是mapper代理开发方法
		2.要求mapper映射文件，与mapper接口要放在同一目录
		3.要求mapper映射文件的名称，与mapper接口的名称要一致
	-->
	<package name="com.moon.mapper"/>
</mappers>
```