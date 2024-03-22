## 1. Hibernate 概述

Hibernate 是应用在 javaee 三层结构中 dao 层，开源的轻量级持久层框架。Hibernate 是一个不用写 SQL，直接使用对象实现对数据库 CRUD 操作的框架！！！！

Hibernate 底层是对 JDBC 进行封装，使用 Hibernate 的好处是，不需要写复杂 JDBC 代码，也不需要写 sql 语句就可以实现一部分功能。

每种数据库的 SQL 语句是有差异的。如果 SQL 语句由 Hibernate 框架生成，那么就屏蔽了不同数据库 SQL 语法的差异。这样，使用 Hibernate 开发出来的代码，一套代码可以兼容多个数据库。除了主要的屏蔽 SQL 差异的功能，还支持大量功能组件。

1. 支持快照
2. 支持缓存
3. 支持第三方的连接池
4. 支持线程绑定

回顾 JavaEE 三层结构

- web 层：struts2 框架
- service 层：spring 框架
- dao 层：hibernate 框架。对数据库进行 CRUD 操作

![](images/562462220250166.jpg)

### 1.1. Hibernate 版本

- Hibernate 3.x
- Hibernate 4.x：过渡版本，比较少用
- Hibernate 5.x：目前使用，要求JDK 6+

### 1.2. Hibernate 文件结构

- documentation：hibernate 相关文档
- lib：hibernate 相关 jar 包
- project：hibernate 相关开源代码

### 1.3. ORM 思想

ORM：（Object Relational Mapping）对象关系映射。在访问数据库前，<font color=red>**将实体类的类名和数据库表的表名关联起来**</font>。通过**操作实体类的对象**，直接由框架生成操作数据库的 SQL 操作数据库表。

ORM 是一种实现使用实体类对象操作数据库表的设计思想。

1. 让实体类和数据库表进行一一对应关系。
    - 让实体类首先和数据库表对应。
    - 让实体类属性和表里面字段对应。
2. 不需要直接操作数据库表，而操作表对应的实体类对象。
    - 通过使用配置文件方式实现，不需要操作表，而直接使用 hibernate 封装的对象 Session 直接操作实体类对象。

**应用场景**：

开发产品型项目（兼容多种数据库的项目）：使用 Hibernate 写一套代码就可以实现多种数据库的操作。

**ORM 的操作流程**：

![](images/567251823259372.jpg)

## 2. Hibernate 基础使用

### 2.1. 配置流程

![](images/196420919240246.jpg)

1. Hibernate 需要一个总配置文件（hibernate.cfg.xml）用于存储连接数据库的信息。
2. 总配置文件的信息需要一个读取它类，Configuration。
3. 配置类可以通过 `buildSessionFactory()` 方法获得会话工厂。通过会话工厂管理数据库的连接。
4. 通过会话工厂可以获得操作对象会话（Session），Session 可以操作数据库（增删改查）。
5. 会话的增删改查必须要依赖映射文件，操作前必须配置一个实体类，然后配置实体类对应的映射文件。

### 2.2. 搭建 Hibernate 环境（传统方式）

#### 2.2.1. Step 1：导入 hibernate 相关 jar 包

1. 引入 hibernate 核心的 jar 包。
    - 必须的支撑包：hibernate-release-5.0.12.Final\lib\required
    - JPA 实现包：hibernate-release-5.0.12.Final\lib\jpa
    - 可选的组件（如：第三方缓存，连接池）：hibernate-release-5.0.12.Final\lib\optional
2. 使用 hibernate 时，有日志信息输出，hibernate 本身没有日志输出的 jar 包，所以需要导入其他日志的 jar 包。如：log4j-1.2.16.jar、slf4j-api-1.6.1.jar 等
3. 数据库 MySQL 驱动的 jar 包，如：mysql-connector-java-5.1.44-bin.jar。

#### 2.2.2. Step 2：创建实体类

<font color=red>**hibernate 要求实体类有一个属性是唯一的，一般都是用作 id（对应数据表的主键）**</font>。注：使用 hibernate 时，不需要手动创建表，hibernate 会自动创建表格。

#### 2.2.3. Step 3：配置实体类和数据库表 - 对应关系(映射关系)

1. 创建 xml 格式配置文件实现映射关系，这个映射配置文件名称和位置没有固定要求。<font color=red>*建议：在实体类所在包里面创建。命名：`实体类名称.hbm.xml`*</font>
2. 配置文件是 xml 格式，在配置文件中首先引入 xml 约束。
    - 在 hibernate 里面引入的约束目前都是 dtd 约束
    - 使用网络约束：从【`http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`】可下载 dtd 约束文件 `hibernate-mapping-3.0.dtd`。或者使用本址约束：解压 hibernate-core-5.0.12.Final.jar 再搜索 `*.dtd`

![](images/244495719258672.jpg)

3. 配置映射关系。`<hibernate-mapping></hibernate-mapping>` -- 根标签

![](images/191295819246539.jpg)

Code Dome: User.hbm.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Hibernate配置文件引入xml约束 -->
<!DOCTYPE hibernate-mapping PUBLIC 
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<!-- 配置hibernate映射关系 -->
<hibernate-mapping>
	<!-- 1.配置类和表对应
		class标签
		name属性：实体类全路径
		table属性：数据库表名称
	 -->
	<class name="com.moonzero.entity.User" table="t_user">
		<!-- 2.配置实体类id和表id对应
			hibernate要求实体类有一个属性唯一值
			hibernate要求表有字段作为唯一值
			id标签
			name属性：实体类里面id属性名称
			column属性：生成的表字段名称
		 -->
		 <id name="uid" column="uid">
		 	<!-- generator设置数据库表id生成策略 
		 		native:生成表id值就是主键并自动增长
		 	-->
		 	<generator class="native"></generator>
		 </id>
		 <!-- 配置其他属性和表字段对应
		 	name属性：实体类属性名称
		 	column属性：生成表字段名称
		  -->
		 <property name="username" column="username"></property>
		 <property name="password" column="password"></property>
		 <property name="address" column="address"></property>
	</class>
</hibernate-mapping>
```

#### 2.2.4. Step 4：创建 hibernate 的核心配置文件

1. 配置文件，默认是放在 classpath 根目录下（即 src 目录下），文件名默认必须为 `hibernate.cfg.xml`
2. 引人 dtd 约束。查询文件：hibernate-configuration-3.0.dtd
3. hibernate 操作过程中，只会加载核心配置文件，其他配置文件不会加载。
    - 第一部分：配置数据库信息（必须的）
    - 第二部分：配置hibernate信息（可选的）
    - 第三部分：把映射文件放到核心配置文件中（必须的）。注：是文件路径，配置后按 `ctrl+左键` 点击可以链接到文件即可

Code Dome: hibernate.cfg.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
	"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
	"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
	<session-factory>
		<!-- 第一部分：配置数据库信息（必须的） -->
		<property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
		<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernateTest</property>
		<property name="hibernate.connection.username">root</property>
		<property name="hibernate.connection.password">123456</property>
		
		<!-- 第二部分：配置hibernate信息（可选的） -->
		<!-- 输出底层sql语句 -->
		<property name="hibernate.show_sql">true</property>
		<!-- 输出底层sql语句格式 -->
		<property name="hibernate.format_sql">true</property>
		<!-- 需要配置此属性后，hibernate才会自动创建表
			update：如果已经有表，则更新；如果没有表，则创建表
		 -->
		<property name="hibernate.hbm2ddl.auto">update</property>
		<!-- 配置数据库方言
			让hibernate框架识别不同数据库自己特有的语句（关键字）
		 -->
		<property name="hibernate.dialect">org.hibernate.dialect.MySQL5InnoDBDialect</property>
		
		<!-- 第三部分：把映射文件放到核心配置文件中（必须的） -->
		<mapping resource="com/moonzero/entity/User.hbm.xml"/>
	</session-factory>
</hibernate-configuration>
```

### 2.3. 使用 Maven 方式搭建 Hibernate

#### 2.3.1. 引入依赖

引入 hibernate 的 core、c3p0（数据源），MySQL 数据库的驱动等依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>${hibernate.version}</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-c3p0 -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-c3p0</artifactId>
        <version>${hibernate.version}</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/junit/junit -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit4.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.26</version>
    </dependency>
</dependencies>
```

#### 2.3.2. 数据库映射配置文件与实体类

```java
public class Customer {
    private long custId; // bigint(32) NOT NULL AUTO_INCREMENT COMMENT '客户编号(主键)',
    private String custName; // varchar(32) NOT NULL COMMENT '客户名称(公司名称)',
    private String custSource; // varchar(32) DEFAULT NULL COMMENT '客户信息来源',
    private String custIndustry; // varchar(32) DEFAULT NULL COMMENT '客户所属行业',
    private String custLevel; // varchar(32) DEFAULT NULL COMMENT '客户级别',
    // ...省略 getter/setter
}
```

在 resources 目录下，创建数据表与实现类的映射配置文件 `hbm/Customer.hbm.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "hibernate-mapping-3.0.dtd" >
<!-- 配置hibernate映射关系 -->
<hibernate-mapping>
    <!--
        配置类与表的对应
        class标签
        name属性：实体类全路径
        table属性：数据库表名称
     -->
    <class name="com.moon.hibernate.entity.Customer" table="cst_customer">
        <!-- 2.配置实体类id和表id对应
            hibernate要求实体类有一个属性唯一值
            hibernate要求表有字段作为唯一值
            id标签
            name属性：实体类里面id属性名称
            column属性：生成的表字段名称
         -->
        <id name="custId" column="cust_id">
            <!-- generator设置数据库表id生成策略
                native:生成表id值就是主键并自动增长
            -->
            <generator class="native"></generator>
        </id>
        <!-- 配置其他属性和表字段对应
            name属性：实体类属性名称
            column属性：生成表字段名称
         -->
        <property name="custName" column="cust_name"></property>
        <property name="custSource" column="cust_source"></property>
        <property name="custIndustry" column="cust_industry"></property>
        <property name="custLevel" column="cust_level"></property>
    </class>
</hibernate-mapping>
```

#### 2.3.3. hibernate 总配置文件

在 resources 目录下创建 hibernate 总配置文件 hibernate.cfg.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "hibernate-configuration-3.0.dtd" >
<hibernate-configuration>
    <session-factory>
        <!-- 配置数据连接信息(4要素)(必须) -->
        <!-- 任何框架只有是参数名信息，必须可以在代码里面找到对应的声明代码 org.hibernate.cfg.Environment -->
        <!-- 数据库连接驱动 -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <!-- 连接字符串 -->
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernate_db?allowPublicKeyRetrieval=true&amp;useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8</property>
        <!-- 用户名 -->
        <property name="hibernate.connection.username">root</property>
        <!-- 密码 -->
        <property name="hibernate.connection.password">123456</property>

        <!-- 配置hibernate可选常用属性 -->
        <!-- 输出sql语句 -->
        <property name="hibernate.show_sql">true</property>
        <!-- 格式化sql语句 -->
        <property name="hibernate.format_sql">true</property>
        <!-- 需要配置此属性后，hibernate才会自动创建表
              update：如果已经有表，则更新；如果没有表，则创建表,
              create:如果有表覆盖，没有就创建
              create-drop:启动就创建表，如果关闭sessionFactory会删除表（一般不使用）
            作用：通过代码生成表结构，可以实现快速部署项目。因为不用创建表结构。
       -->
        <property name="hibernate.hbm2ddl.auto">update</property>
        <!--
            数据库方言 ：
            所有的数据库的SQL语言都有在标准的SQL语言的基础做了扩展。我们将每一种数据库的SQL语言称为方言。
           我们知道，每一种数据库的方言都和标准的SQL语言有差异的。
          Hibernate将每一种数据库不同的实现写在方言类里面

          数据库的方言其实已经默认根据连接的信息自动加载了。不用配置也可以。
          但是我们还是建议手工配置
          因为，自动加载的方言不是最优化的。
         -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL5InnoDBDialect</property>

        <!-- 加载映射文件(必须) -->
        <mapping resource="hbm/Customer.hbm.xml"/>
    </session-factory>
</hibernate-configuration>
```

#### 2.3.4. c3p0 连接池的配置（额外）

Hibernate 内置的连接池功能是很弱的，所以 Hibernate 支持第三方的连接池。而 c3p0 是开源最流行的连接池之一，使用连接池的作用是为了提高并发访问数据库的效率。

- 若使用传统方式是导入 hibernate 集成 c3p0 包：`hibernate-c3p0-5.0.12.Final.jar`（以前旧版本可能还需要 c3p0 本身的 jar 包，如 `c3p0-0.9.2.1.jar`、`mchange-commons-java-0.2.3.4.jar`）。
- 若使用 maven 的方式创建的项目，则只需要引入 hibernate-c3p0 依赖即可

修改总配置 hibernate.cfg.xml，增加 c3p0 相关的配置：

```xml
<!-- c3p0连接池配置 -->
<!-- 设置连接的提供者 -->
<property name="hibernate.connection.provider_class">org.hibernate.c3p0.internal.C3P0ConnectionProvider</property>
<!-- 设置c3p0参数 -->
<!-- 设置最大连接数 -->
<property name="hibernate.c3p0.max_size">10</property>
<!-- 设置启动连接池支持的连接数量 -->
<property name="hibernate.c3p0.min_size">5</property>
<!-- 设置操作时间，单位：毫秒。 -->
<property name="hibernate.c3p0.timeout">300000</property>
<!-- 最大操作数据statement,不能大于最大连接数，一般配置95%左右 -->
<property name="hibernate.c3p0.max_statements">9</property>
```

运行测试程序时，控制台会有输出以下信息，则说明 c3p0 连接池配置成功

![](images/142694213267265.png)

扩展：配置 c3p0 连接池后，可能有一个数据库会出现异常。

```java
INFO: HHH000424: Disabling contextual LOB creation as createClob() method threw error : java.lang.reflect.InvocationTargetException
```

出现的原因是因为 c3p0 的类型检查和 Hibernate 默认的类型检查不同。解决方案：忽略 Hibernate 的类型检查。

> Tips: 强调一下，如果没有出现这个错误的数据库不用配置以下代码！！！！！！

```xml
<!--
    出现：creation as createClob() method threw error异常的原因
    答：是因为c3p0和hibernate类型检查不兼容导致。 
    如何解决？
    答：关闭hibernate的类型检查

    由于方言的自动加载是基于Hibernate类型检查的。如果关闭了类型检查，导致无法自动加载方言。
    所以，配置了关闭hibernate的类型检查。必须要手动设置方言
-->
<!-- 所在位置org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator -->
<property name="hibernate.temp.use_jdbc_metadata_defaults">false</property>
```

### 2.4. Hibernate 使用步骤

<font color=red>**前 4 步和后 2 步都是固定，第 5 步是根据实际情况而变化。**</font>

> Notes: 这里的 Session 与 Javaweb 中的 Session 是两个不同的东西。

#### 2.4.1. Step 1：加载 hibernate 核心配置文件

```java
Configuration cfg = new Configuration();
cfg.configure();
```

执行的过程：先到 src 下面找到名称是 hibernate.cfg.xml，把该文件放入这个对象中，然后调用方法执行加载配置文件。也可以指定配置文件名字 `cfg.configure(config.xml);`，但不推荐。

#### 2.4.2. Step 2：创建 SessionFactory 对象

```java
SessionFactory sessionFactory = cfg.buildSessionFactory();
```

读取 hibernate 核心配置文件内容，创建 `sessionFactory`。在这过程中，根据映射关系，在配置的数据库里面把表创建。

#### 2.4.3. Step 3：使用 SessionFactory 创建 Session 对象

```java
Session session = sessionFactory.openSession();
```

类似于连接(可认为是和数据库建立的一个连接)

#### 2.4.4. Step 4：开启事务

```java
Transaction transaction = session.beginTransaction();
```

#### 2.4.5. Step 5：写具体逻辑 CRUD 操作

直接对对象进行操作，最后调用 Session 的方法实现操作。例如，添加数据：

```java
session.save(xxx对象);
```

#### 2.4.6. Step 6：提交事务

```java
transaction.commit();
```

#### 2.4.7. Step 7：关闭资源

```java
session.close();
sessionFactory.close();
```

### 2.5. 创建 Hibernate 工具类

<font color=red>**注意：要理解为什么，一个项目只能有一个连接池。如果出现多个连接池，有可能导致事务处理不同步。**</font>

hibernate 工具类示例：

```java
package hibernate.demo.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	// 使用static的关键字，整个项目共享一个对象。
	// 如果一个项目里面出现多个连接池，有可能导致事务处理不同步。事务同步提交的前提是同一个连接。
	// 创建静态Session工厂成员变量
	public static SessionFactory sessionFactory = HibernateUtil.createSessionFactory();

	// 获得会话工厂
	private static SessionFactory createSessionFactory() {
		// 1.创建Configuration对象
		Configuration cfg = new Configuration();
		// 2.读取配置文件，获得框架信息，默认读取classpath根目录hibernate.cfg.xml
		cfg.configure();
		// 3.获得会话工厂,如果要连接数据库必须需要连接数据库的信息（四要素）
		// 获得会话工厂必须要在读取配置文件之后
		return cfg.buildSessionFactory();
	}

	// 获取会话操作对象
	public static Session getSession() {
		// 每次打开一个新连接
		return sessionFactory.openSession();
	}
}
```

## 3. Hibernate 核心类

### 3.1. Hibernate 架构与核心类概述

Hibernate 架构是分层的，Hibernate 利用数据库以及配置数据来为应用程序提供持续性服务（以及持续性对象）。下图是 Hibernate 应用程序体系结构视图以及一些重要的类：

![](images/178015513240349.jpg)

Hibernate 使用不同的现存 Java API，比如 JDBC，Java 事务 API（JTA），以及 Java 命名和目录界面（JNDI）。JDBC 提供了一个基本的抽象级别的通用关系数据库的功能，Hibernate 支持几乎所有带有 JDBC 驱动的数据库。JNDI 和 JTA 允许 Hibernate 与 J2EE 应用程序服务器相集成。Hibernate 常用的 API：

- Configuration：配置类。是在任何 Hibernate 应用程序中创造的第一个 Hibernate 对象，并且经常只在应用程序初始化期间创造。它代表了 Hibernate 所需一个配置或属性文件。配置对象提供了两种基础组件：
    - **数据库连接**：由 Hibernate 支持的一个或多个配置文件处理。这些文件是 hibernate.properties 和 hibernate.cfg.xml。
    - **类映射设置**：这个组件建立了 Java 类和数据库表格之间的联系。
- SessionFactory：会话工厂。通过 Configuration 创造一个 SessionFactory 对象，使用提供的配置文件为应用程序依次配置 Hibernate，并允许实例化一个会话对象。
- Session：会话被用于与数据库的物理连接。
- Transaction：事务处理类
- Environment：用于存储所有配置的环境参数
- Query：使用 SQL 或者 Hibernate 查询语言（HQL）字符串在数据库中来检索数据并创造对象。一个查询的实例被用于连结查询参数，限制由查询返回的结果数量，并最终执行查询。
- Criteria：被用于创造和执行面向规则查询的对象来检索对象。

### 3.2. Configuration 类

`org.hibernate.cfg.Configuration` 类的作用是对 Hibernate 进行配置，以及对它进行启动。在 Hibernate 的启动过程中，`Configuration` 类的实例首先定位映射文档的位置，读取这些配置，然后创建一个 `SessionFactory` 对象。虽然 `Configuration` 类在整个 Hibernate 项目中只扮演着一个很小的角色，但它是启动 hibernate 时所遇到的第一个对象。

总结：Configuration 类用于获得框架的信息，构建将会话工厂。

#### 3.2.1. 构造函数

```java
public Configuration() {
	this( new BootstrapServiceRegistryBuilder().build() );
}

public Configuration(BootstrapServiceRegistry serviceRegistry) {
	this.bootstrapServiceRegistry = serviceRegistry;
	this.metadataSources = new MetadataSources( serviceRegistry );
	reset();
}

public Configuration(MetadataSources metadataSources) {
	this.bootstrapServiceRegistry = getBootstrapRegistry( metadataSources.getServiceRegistry() );
	this.metadataSources = metadataSources;
	reset();
}
```

#### 3.2.2. 常用方法

```java
public Configuration configure() throws HibernateException
```

- 获得默认的配置文件，直接获得 src 目录下 hibernate.hbm.xml 的配置。

```java
public Configuration configure(String resource) throws HibernateException
```

- 获得非默认名称的配置文件，例如 `xxx.xml`，则 `config.configure(xxx.xml);`

```java
public SessionFactory buildSessionFactory() throws HibernateException
```

- 获取一个 `SessionFactory` 对象

#### 3.2.3. 示例

不使用配置文件获得框架的信息示例（*不推荐，了解*）。

> 原因：配置放在类里面，编译后 java 文件就变成了 class 文件。导致不可修改配置的信息

```java
// 获得一个会话工厂
private static SessionFactory createSessionFactory(){
	// 1. 创建一个Configuration的对象
	Configuration config = new Configuration();
	// 2. 设置配置的信息
	// 2.1. 创建一个键值对
	Properties p = new Properties();
	p.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
	p.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/hibernate_db?useSSL=false&useUnicode=true&characterEncoding=UTF-8");
	p.setProperty("hibernate.connection.username", "root");
	p.setProperty("hibernate.connection.password", "123456");
	p.setProperty("hibernate.show_sql", "true");
	p.setProperty("hibernate.format_sql", "true");
	// 2.2. 将设置的属性加入到配置
	config.addProperties(p);
	
	// 2.3. 加载映射文件
	config.addResource("com/moon/pojo/hbm/Customer.hbm.xml");

	// 3. 构造一个会话工厂
	SessionFactory sessionFactory = config.buildSessionFactory();
	return sessionFactory;
}
```

### 3.3. SessionFactory 接口

#### 3.3.1. 概述

```java
public interface SessionFactory extends EntityManagerFactory, HibernateEntityManagerFactory, Referenceable, Serializable, java.io.Closeable
```

`org.hibernate.SessionFactory` 的作用类似 `DataSource`，用于管理数据库的连接，它充当数据存储源的代理，并负责创建 Session 对象。`SessionFactory` 对象可以通过 `Configuration` 类的 `buildSessionFactory()` 工厂方法获取。SessionFactory 是一个线程安全对象并由应用程序所有的线程所使用。

需要注意的是，SessionFactory 是一个重量级对象，一般情况下，一个项目通常只需要一个 SessionFactory 就足够，所以通常它都是在应用程序启动时创造然后保存为以后使用。每个数据库需要一个 SessionFactory 对象使用一个单独的配置文件。所以如果使用多个（种）数据库，那么就要每个（种）数据库指定一个 SessionFactory 对象。

#### 3.3.2. 常用方法

```java
Session openSession() throws HibernateException;
```

- 获取 `Session` 操作对象，相当于创建一次连接。

```java
Session getCurrentSession() throws HibernateException;
```

- 获得当前线程的 session（线程绑定）

```java
void close() throws HibernateException;
```

- 关闭会话工厂

### 3.4. Session 接口

#### 3.4.1. 概述

```java
public interface Session extends SharedSessionContract, EntityManager, HibernateEntityManager, AutoCloseable, Closeable
```

`org.hibernate.Session` 接口的作用类似 JDBC 的 Statement。用于操作数据库的数据。<font color=red>**Session 操作是必须先映射，后操作**</font>。Session 对象是轻量级的，并被设计为每次实例化都需要与数据库的交互。持久对象通过 Session 对象保存和检索。

Session 对象不应该长时间保持开启状态因为它们通常情况下并非线程安全，并且它们应该按照所需创造和销毁。

Session接口负责执行被持久化对象的CRUD操作(CRUD的任务是完成与数据库的交流，包含了很多常见的SQL语句)。但需要注意的是Session对象是非线程安全的。同时，Hibernate的session不同于JSP应用中的HttpSession。这里当使用session这个术语时，其实指的是Hibernate中的session，而以后会将HttpSession对象称为用户session。

#### 3.4.2. 常用方法

```java
Serializable save(Object object);
Serializable save(String entityName, Object object);
```

- 保存数据到数据库

```java
void saveOrUpdate(Object object);
void saveOrUpdate(String entityName, Object object);
```

- 如果数据库没有记录就保存，如果有记录就更新，重要的判断依据是 OID 是否相同，OID（Object ID）就是在配置文件配置为 `<id>` 属性。<font color=purple>**注：如果想修改某个字段，保留其他字段，必须先查询再修改**</font>。

```java
void delete(Object object);
void delete(String entityName, Object object);
```

- 用于通过 OID 删除数据库记录

```java
void update(Object object);
void update(String entityName, Object object);
```

- 用于通过 OID 更新数据的记录

```java
<T> T load(Class<T> theClass, Serializable id, LockMode lockMode);
<T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions);
Object load(String entityName, Serializable id, LockMode lockMode);
Object load(String entityName, Serializable id, LockOptions lockOptions);
<T> T load(Class<T> theClass, Serializable id);
Object load(String entityName, Serializable id);
void load(Object object, Serializable id);
```

- 用于通过 OID，获得一条记录，<font color=red>**有延迟**</font>。

```java
<T> T get(Class<T> entityType, Serializable id);
<T> T get(Class<T> entityType, Serializable id, LockMode lockMode);
<T> T get(Class<T> entityType, Serializable id, LockOptions lockOptions);
Object get(String entityName, Serializable id);
Object get(String entityName, Serializable id, LockMode lockMode);
Object get(String entityName, Serializable id, LockOptions lockOptions);
```

- 用于通过 OID，获得一条记录，<font color=red>**无延迟**</font>。

```java
Transaction beginTransaction();
```

- 打开并且启动事务（增删改操作必须），并返回关联事务对象。继承自 `org.hibernate.SharedSessionContract`。<font color=purple>**注：在同一个 session 对象中，如果多次 `beginTransaction()`，并且在事务没有提交的情况下，都是同一个事务对象。**</font>

```java
Transaction getTransaction();
```

- 打开了事务，但没有开启。继承自 `org.hibernate.SharedSessionContract`


```java
void clear();
```

- 清除 session 所有对象的持久态。包含所有已加载的实例，取消所有待处理的保存、更新和删除。但不会关闭已开启打开的 iterators 或 ScrollableResults 实例。

```java
void close() throws HibernateException;
```

- 关闭 session 对象，继承自 `org.hibernate.SharedSessionContract`

```java
void evict(Object object);
```

- 清除指定的对象的持久态

```java
// 继承自 org.hibernate.query.QueryProducer，用 QueryProducer 中暴露的类型覆盖 JPA 返回类型
@Override
<T> org.hibernate.query.Query<T> createQuery(CriteriaQuery<T> criteriaQuery);

@Override
org.hibernate.query.Query createQuery(CriteriaUpdate updateQuery);

@Override
org.hibernate.query.Query createQuery(CriteriaDelete deleteQuery);

@Override
<T> org.hibernate.query.Query<T> createQuery(String queryString, Class<T> resultType);

// 继承自 org.hibernate.SharedSessionContract
@Override
org.hibernate.query.Query createQuery(String queryString);
```

- 获得使用 HQL 查询的 Query 对象

```java
@Override
NativeQuery createSQLQuery(String queryString);
// 继承自 org.hibernate.query.QueryProducer
@Deprecated
default SQLQuery createSQLQuery(String queryString)
```

- 获得使用 SQL 查询的 Query 对象。继承自 `org.hibernate.query.QueryProducer`

```java
@Deprecated
Criteria createCriteria(Class persistentClass);
@Deprecated
Criteria createCriteria(Class persistentClass, String alias);
@Deprecated
Criteria createCriteria(String entityName);
@Deprecated
Criteria createCriteria(String entityName, String alias);
```

- 获得标准查询的 Criteria 对象（已过时）。继承自 `org.hibernate.SharedSessionContract`

#### 3.4.3. 绑定 Session 到当前线程

##### 3.4.3.1. 将 Session 当参数的传递的方式实现

```java
@Test
public void testSession() {
	// 获取会话对象
	Session session = HibernateUtil.getSession();
	System.out.println(session.hashCode());
	// 开启事务
	Transaction transaction = session.beginTransaction();

	// 新增数据
	HibernateDao dao = new HibernateDao(session);
	Customer c1 = new Customer();
	Customer c2 = new Customer();

	c1.setCustName("试试");
	dao.save(c1);
	c2.setCustName("xx");
	dao.save(c2);
	// 提交事务
	transaction.commit();
	// 关闭资源
	session.close();
}

public class HibernateDao {
	// 定义session成员变量
	private Session session;

	// 通过构造函数传递Session
	public HibernateDao(Session session) {
		this.session = session;
	}

	// 增加数据方法
	public void save(Customer customer) {
		System.out.println(session.hashCode() + "--dao层");
		// 保存数据
		session.save(customer);
	}
}
```

##### 3.4.3.2. Hibernate 内置实现

由于项目几乎都使用 Spring 框架，不能使用 new 创建对象。所以不能使用构造函数传递 session 给 DAO，可以使用线程绑定来实现外层的 session 和 DAO 的 session 是同一个对象。

所谓的线程绑定，就是使用一个线程变量将对象 `ThreadLoad` 保存起来，从而保证同一条线程获得同一个对象。<font color=red>**线程绑定的作用，就是让同一条线程获得 session 对象是相同的，不需要参数传递 session！！！**</font>实现步骤如下：

- 修改全局配置文件配置

```java
<!-- 配置绑定线程 -->
<property name="hibernate.current_session_context_class">thread</property>
```

- 修改工具类获取 Session 代码

```java
public static Session getSession() {
	// 每次打开一个新连接
	return sessionFactory.getCurrentSession();
	// return sessionFactory.openSession();
}
```

**注意事项**：

1. session 不能手动关闭。如果 session 绑定到当前线程，会随线程启动而启动，随线程结束而结束。因此不能手工关闭。
2. 所有的操作都要打开事务，包括查询（原来查询是不需要启动事务的）。配置了线程绑定，Hibernate 就要验证事务激活状态，导致了所有的操作都要开启事务，包括查询！！

##### 3.4.3.3. 使用 ThreadLocal 实现

使用 `ThreadLocal` 类实现线路绑定。注意，自定义实现线程绑定是不需要配置 Hibernate 的线程绑定的。修改前面示例中 `HibernateUtil` 的核心部分：

```java
public class HibernateUtil {
	// 使用static的关键字，整个项目共享一个对象。
	// 创建静态Session工厂成员变量
	public static SessionFactory sessionFactory = HibernateUtil.createSessionFactory();
	// 创建线程共享对象
	private static ThreadLocal<Session> local = new ThreadLocal<Session>();

	// 获得会话工厂
	private static SessionFactory createSessionFactory() {
		// 1.创建Configuration对象
		Configuration cfg = new Configuration();
		// 2.读取配置文件，获得框架信息，默认读取classpath根目录hibernate.cfg.xml
		cfg.configure();
		// 3.获得会话工厂,如果要连接数据库必须需要连接数据库的信息（四要素）
		// 获得会话工厂必须要在读取配置文件之后
		return cfg.buildSessionFactory();
	}

	// 获取会话操作对象
	public static Session getSession() {
		// 判断线程共享对象是否有值
		if (local.get() == null) {
			Session session = sessionFactory.openSession();
			// 将Session存入线程共享对象中
			local.set(session);
		}
		// 返回线路共享值
		return local.get();
	}
}
```

#### 3.4.4. 小结

##### 3.4.4.1. get 和 load 方法的区别（了解）

`get()` 方法：

1. 由于 get 方法是无延迟的，所以查不到数据直接返回 null。
2. 由于 get 方法是无延迟的，查询的时候数据已经在对象里面，所以关闭 session，数据依然可以使用。

`load()` 方法：

1. load 是有延迟，基于延迟的机制，如果找不到数据就报异常。
2. load 是有延迟，意味着在对象的时候才去查询数据库。导致在使用对象前关闭数据库，报错。
3. load 方法可以通过修改全局配置文件，将 `<class lazy="false">` 修改为无延迟获得数据，修改方法如下：

```xml
<!-- 设置 load 直接查询。设置 class 标签的 lazy 为 false -->
<class name="com.moon.pojo.Customer" table="cst_customer" lazy="false">
```

> Tips: 由于 load 方法拿不到数据报异常，又需要处理异常导致代码增加。所以开发都是使用 get 方法

#### 3.4.5. 示例

基础的增删改查

```java
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.moon.entiry.Customer;
import com.moon.utils.HibernateUtil;

/**
 * hibernate 测试类
 */
public class TestHibernate {
	@Test
	public void testSession() {
		// 获取会话对象
		Session session = HibernateUtil.getSession();
		System.out.println(session);
	}

	@Test
	public void save() {
		// 获取会话
		Session session = HibernateUtil.getSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();

		// 进行添加数据操作
		Customer c = new Customer();
		c.setCustName("MooN");
		session.save(c);
		
		// 提交事务
		transaction.commit();
		// 关闭资源
		session.close();
	}
	
	@Test
	public void update() {
		// 获取会话
		Session session = HibernateUtil.getSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();

		// 进行修改数据操作
		Customer c = new Customer();
		c.setCustId(1L);
		// c.setCustName("xxoo");
		c.setCustLevel("一级");
		session.saveOrUpdate(c);
		
		// 提交事务
		transaction.commit();
		// 关闭资源
		session.close();
	}
	
	@Test
	public void update2() {
		// 获取会话
		Session session = HibernateUtil.getSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();

		// 进行修改数据操作，如果想修改某个字段，保留其他字段，必须先查询再修改
		Customer c = session.get(Customer.class, 1L);
		c.setCustName("xxoo");
		session.saveOrUpdate(c);
		
		// 提交事务
		transaction.commit();
		// 关闭资源
		session.close();
	}
	
	@Test
	public void delete() {
		// 获取会话
		Session session = HibernateUtil.getSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();

		// 进行修改数据操作，如果想修改某个字段，保留其他字段，必须先查询再修改
		Customer c = new Customer();
		c.setCustId(2L);
		session.delete(c);
		
		// 提交事务
		transaction.commit();
		// 关闭资源
		session.close();
	}
	
	@Test
	public void get() {
		// 获取会话
		Session session = HibernateUtil.getSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();

		// 进行修改数据操作，如果想修改某个字段，保留其他字段，必须先查询再修改
		Customer c = session.get(Customer.class, 1L);
		System.out.println(c);
		// 提交事务
		transaction.commit();
		// 关闭资源
		session.close();
	}
}
```

### 3.5. Transaction 接口

#### 3.5.1. 概述

```java
public interface Transaction extends EntityTransaction
```

一个事务代表了与数据库工作的一个单元，目前大部分 RDBMS 均支持事务功能。

`org.hibernate.Transaction` 接口主要用于处理事务。Transaction 接口是 Hibernate 对实际事务实现的一个抽象，由底层事务管理器和事务处理，这些实现包括 JDBC 的事务、JTA 中的 UserTransaction、甚至可以是 CORBA 事务。这样设计是能让开发者能够使用一个统一事务的操作界面，使得自己的项目可以在不同的环境和容器之间方便地移植。

Transaction 是一个可选的 API，因此 Hibernate 应用程序可以选择不使用这个接口，取而代之的是 Hibernate 的设计者在应用程序中自己实现底层事务处理代码。

#### 3.5.2. 常用方法

```java
public void begin();
```

- 开启事务。继承自 `javax.persistence.EntityTransaction`

```java
public void commit();
```

- 提交事务。继承自 `javax.persistence.EntityTransaction`

```java
public void rollback();
```

- 回滚事务。继承自 `javax.persistence.EntityTransaction`

## 4. Hibernate 持久化类

### 4.1. 概述

Hibernate 的核心工作就是提取 Java 类属性中的值，并且将它们保存到数据库表相应的字段中。而实体类映射文件能够帮助 Hibernate 确定如何从该类中提取值，并将它们映射在表格和相关域中。

在 Hibernate 中，其对象或实例将会被存储在数据库表单中的 Java 类被称为**持久化类**。若该类遵循一些简单的规则或者符合 Plain Old Java Object (POJO) 编程模型，Hibernate 将会处于其最佳运行状态。以下所列就是持久化类的主要规则，但值得注意的是，在这些规则中，没有一条是硬性要求。

#### 4.1.1. 编写规则

持久化类就是实体类(符合 JavaBean 的规则)，编写规则如下：

- 所有将被持久化的 Java 类都需要一个默认的构造函数。
- 为了使对象能够在 Hibernate 和数据库中容易识别，所有类都需要包含一个代表 ID 的字段（名称可随意）。此属性映射到数据库表的主键列。
- 所有将被持久化的属性声明必须使用私有的 `private`，并具有由 JavaBean 风格定义的 getXxx 和 setXxx 方法，用于设置和获得属性值。
- Hibernate 的一个重要特征为代理，因此所有持久化类属性的声明<font color=red>**不能使用 `final` 关键字**</font>修饰，所有方法都声明为 `public`。
- 所有的类是不可扩展或按 EJB 要求实现的一些特殊的类和接口。
- [**可选**]建议实现一个序列接口 `Serializable`。<font color=red>**如果需要将对象缓存到本地文件，必须加上**</font>。

> Tips: POJO 的名称用于强调一个给定的对象是普通的 Java 对象，而不是特殊的对象，尤其不是一个 Enterprise JavaBean。

#### 4.1.2. 常用的分包规则

- `xxx.entity`：实体
- `xxx.pojo`：简单的 Java 对象
- `xxx.domian`：域对象

#### 4.1.3. 一个简单的 POJO 的例子

基于以上所述规则，定义如下 POJO 类：

```java
public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private int salary;

    public Employee() {
        // ...省略
    }

    // ...省略所有 getter / setter 方法
}
```

### 4.2. 持久化类的三种状态

由于 Hibernate 框架是一个<font color=red>**先映射，后操作**</font>的框架，因此实体类（持久化类）对象是有状态的。所谓的状态就是实体类的<font color=red>**对象和数据库是否有关联的情况**</font>。

1. 瞬时态/自由态（transient）：与数据库的表没有任何关联关系的实体对象。瞬时态对象不存在持久化标识 OID（相当于主键值），并且尚未与 Hibernate Session 关联。
2. 持久态：正在与数据库保持连接的关系，通常是将一个瞬时状态实例通过与一个 Session 关联的方式将其转化为持久状态实例。持久态对象存在持久化标识 OID，加入到了 Session 缓存中，并且相关联的 Session 没有关闭，在数据库中有对应的记录。
3. 游离态：曾经被 session 操作过，但 session 失效了、关闭、清除。脱管态对象存在持久化标识 OID，并且仍然与数据库中的数据存在关联，只是失去了与当前 Session 的关联。

### 4.3. 状态的转换

- 当使用 `new` 关键字创建实体对象，此时实体对象与数据库没有任何关系，对象的状态为瞬时态（自由态）。
- 当调用 session 对象的 `save(实体对象)` 方法，即对象被 session 操作过，对象的状态为持久态（托管态）。
- 当调用 `session.close()` 方法后，session 会失效，被该 session 操作过的实体对象的状态为游离态。

![](images/176762516266808.jpg)

#### 4.3.1. 持久态的获取

持久类的其他不重要，只要了解如何获得持久态对象即可。有以下情况可以获得持久态：

- 保存（save / persist）
- 更新（update / saveOrUpdate）
- 查询（get / load，Criteria 和 HQL 查询返回的对象） 

对于 `persist()` 方法，按照官方文档的说明：

1. `persist()` 方法把一个瞬时态的实例持久化，但是并不保证标识符被立刻填入到持久化实例中，标识符的填入可能被推迟到 flush 的时间。
2. `persist()` 方法保证当它在一个事务外部被调用的时候并不触发一个 INSERT 语句，当需要封装一个长会话流程的时候，`persist()` 方法是很有必要的。
3. `save()` 方法不保证第 2 条，它要返回标识符，所以它会立即执行 INSERT 语句，不管是在事务内部还是外部。至于 `lock()` 方法和 `update()` 方法的区别，`update()` 方法是把一个已经更改过的脱管状态的对象变成持久状态；`lock()` 方法是把一个没有更改过的脱管状态的对象变成持久状态。

#### 4.3.2. 持久态支持的功能

Hibernate 持久态的对象就是正在和数据表关联的状态对象，有如下的功能：

1. 支持缓存
2. 支持快照
3. 支持导航导航查询：即通过一个表直接获得关联的表的数据。

## 5. 原生 SQL

Hibernate 建议如果使用 Criteria 和 HQL 可以解决的问题，就不要使用 SQL 操作。但是有一些复杂的需求，使用 HQL 实现不了，那么可以使用原生的 SQL 操作。

如果想使用数据库特定的功能如查询提示或 Oracle 中的 CONNECT 关键字的话，可以使用原生 SQL 数据库来表达查询。Hibernate 3.x 允许为所有的创建，更新，删除和加载操作指定手写 SQL，包括存储过程。

### 5.1. SQLQuery 接口

`org.hibernate.SQLQuery` 接口是用于操作原生 SQL。

```java
@Deprecated
public interface SQLQuery<T> extends Query<T>, SynchronizeableQuery<T>
```

#### 5.1.1. SQLQuery 对象的获取

使用 Session 接口的 `createSQLQuery` 方法来获取 SQL 查询对象 `SQLQuery`，参数 `queryString` 是 SQL 查询语句。

```java
NativeQuery createSQLQuery(String queryString);
```

当通过一个包含 SQL 查询的 `createsqlquery()` 方法的字符串时，可以将 SQL 的结果与现有的 Hibernate 实体，一个连接或一个标量结果分别使用 `addEntity()`，`addJoin()` 和 `addScalar()` 方法进行关联。

#### 5.1.2. SQLQuery 常用方法

```java
SQLQuery<T> addEntity(String entityName);
SQLQuery<T> addEntity(String tableAlias, String entityName);
SQLQuery<T> addEntity(String tableAlias, String entityName, LockMode lockMode);
SQLQuery<T> addEntity(Class entityType);
SQLQuery<T> addEntity(String tableAlias, Class entityType);
SQLQuery<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode);
```

- 关联需要操作的实体字节码对象，使用 SQL 需要手动关联实体类。

```java
List<R> list();
```

- 返回查询的结果 List 集合（查询所有的数据）。继承于 `org.hibernate.Query`

```java
R uniqueResult();
```

- 使用聚合函数查询，返回 `BigInteger` 类型。继承于 `org.hibernate.Query`

> Notes: 由于 SQL 查询返回的是一个 Object 数组，如果需要指定的实体类接收，要强制绑定。

### 5.2. 标量查询

最基本的 SQL 查询是从一个或多个列表中获取一个标量（值）列表。

```java
SQLQuery query = session.createSQLQuery("SELECT first_name, salary FROM EMPLOYEE");
query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
List results = query.list();
```

### 5.3. 实体查询

标量查询都是返回标量值，只是基础性地返回结果集中的“原始”值。通过 `addEntity()` 方法可以从原生 SQL 查询中获取实体对象。

```java
SQLQuery query = session.createSQLQuery("SELECT * FROM EMPLOYEE");
query.addEntity(Employee.class);
List<Employee> results = query.list(); 
```

### 5.4. 指定 SQL 查询

通过 addEntity() 方法和使用指定 SQL 查询，从原生 SQL 查询中来获取实体对象。

```java
SQLQuery query = session.createSQLQuery("SELECT * FROM EMPLOYEE WHERE id = :employee_id");
query.addEntity(Employee.class);
query.setParameter("employee_id", 10);
List results = query.list();  
```

### 5.5. 综合示例

```java
public class HibernateSQLTest {
	// 1.查询所有的数据
	public void findAll() {
		// 获取session操作对象
		Session session = HibernateUtil.getSession();
		// 获取SQLQuery操作对象
		SQLQuery sqlQuery = session.createSQLQuery("select * from cst_customer");
		// 使用SQL需要手动关联实体类
		// 由于SQL查询返回的是一个Object数组，如果需要指定的实体类接收，需求强制绑定
		sqlQuery.addEntity(Customer.class);
		List<Customer> list = sqlQuery.list();
		// 关闭资源
		session.close();
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
	}

	// 2.需求：查询客户表客户名有“剑”数据，通过SQL
	public void findByName() {
		// 获取session操作对象
		Session session = HibernateUtil.getSession();
		// 获取SQLQuery操作对象,Hibernate支持命名参数
		SQLQuery sqlQuery = session.createSQLQuery("select * from cst_customer c where c.cust_name like :name");
		// 使用SQL需要手动关联实体类
		// 由于SQL查询返回的是一个Object数组，如果需要指定的实体类接收，需求强制绑定
		sqlQuery.addEntity(Customer.class);
		sqlQuery.setString("name", "%剑%");
		List<Customer> list = sqlQuery.list();
		// 关闭资源
		session.close();
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
	}

	// 需求3：查询第3条开始，取4条件数据
	public void findByPage() {
		// 获取session操作对象
		Session session = HibernateUtil.getSession();
		// 获取SQLQuery操作对象,Hibernate支持命名参数
		SQLQuery sqlQuery = session.createSQLQuery("select * from cst_customer");
		// 使用SQL需要手动关联实体类
		// 由于SQL查询返回的是一个Object数组，如果需要指定的实体类接收，需求强制绑定
		sqlQuery.addEntity(Customer.class);
		// 设置分页参数
		sqlQuery.setFirstResult(2);
		sqlQuery.setMaxResults(4);
		List<Customer> list = sqlQuery.list();
		// 关闭资源
		session.close();
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
	}

	// 需求：查询返回表里面的记录数
	public void count() {
		// 获取session操作对象
		Session session = HibernateUtil.getSession();
		// 获取SQLQuery操作对象,Hibernate支持命名参数
		SQLQuery sqlQuery = session.createSQLQuery("select count(*) from cst_customer");
		// 使用SQL需要手动关联实体类
		// 由于SQL查询返回的是一个Object数组，如果需要指定的实体类接收，需求强制绑定
		BigInteger count = (BigInteger) sqlQuery.uniqueResult();
		// 关闭资源
		session.close();
		System.out.println(count);
	}
}
```

## 6. Hibernate 缓存与快照

缓存是关于应用程序性能的优化，降低了应用程序对物理数据源访问的频次，从而提高应用程序的运行性能。Hibernate 使用了如下解释的多级缓存方案：

![](images/585641222240345.jpg)

### 6.1. 一级缓存

#### 6.1.1. 概述

Hibernate 是支持一级缓存，即 Session 级别的缓存。<font color=red>同一个 session 查询同样的数据，只查询一次数据库</font>。如果出现同多次同样的查询（get/load），直接返回缓存的数据。

Hibernate 默认情况就支持一级缓存。如果支持缓存，使用多次 get 查方法只有一条 sql 语句，说明只查询一次数据库，其余次数的数据直接在缓存中获得。

#### 6.1.2. 清空缓存的方法

调用以下三个方法，会清空缓存：

1. 关闭 `close()`
2. 清空 `clear()`。没有关闭 session，只是将缓存清空
3. 清空指定的实体态对象 `evit()`

<font color=red>**注意：close，clear，evit 清空缓存只是将持久态转成游离态，清空的是数据和数据库的关联，而不是清空数据。**</font>

### 6.2. 二级缓存

#### 6.2.1. 概述

1. HQL 支持缓存，不过是二级缓存，如果返回的记录是多条数据，需要使用二级缓存。
2. 一级缓存是 session 级别的缓存。

hql 使用的使用 query 对象，需要配置二级缓存。

#### 6.2.2. 二级缓存的配置

1. 导入包 `hibernate-release-5.x.x.Final.jar`（源码在 `\lib\optional\ehcache`）
2. 在 hibernate.cfg.xml 配置文件中，配置打开缓存。

```xml
<!-- 配置支持二级缓存 -->
<!-- 用于指定使用的缓存框架 -->
<property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
<!-- 打开二级缓存 -->
<property name="hibernate.cache.use_second_level_cache">true</property>
<!-- 打开支持查询的二级缓存,如果要缓存查询的数据，必须打开 -->
<property name="hibernate.cache.use_query_cache">true</property>
```

3. 代码中设置查询对象使用二级缓存

```java
// 设置当前 query 对象使用二级缓存
// 二级缓存是 sessionFactory 级别的缓存，只要连接池不关闭，一直存在。
query.setCacheable(true);
```

4. <font color=red>**注意：配置二级缓存，实体类必须要实现序列化接口**</font>9.7. 
### 6.3. 查询层次缓存

Hibernate 也实现了一个和二级缓存密切集成的查询结果集缓存。这是一个可选择的、并且需要两个额外的物理缓存区域，它们保存着缓存的查询结果和表单上一次更新时的时间戳。这仅对以同一个参数频繁运行的查询来说是有用的。

### 6.4. 快照机制

#### 6.4.1. 概述

当对象变成<font color=red>持久态对象</font>的时候，和数据库表关联后。在 session 中<font color=red>**会保存<u>两份数据的副本</u>：一份是缓存，一个是快照**</font>。缓存与快照的区别如下：

- 缓存的作用：用于提高查询的效率。
- 快照的作用：用于更新数据，用于对比使用。

<font color=red>**快照的支持就是持久态对象直接可以通过直接修改属性更新数据库表的数据，不需要 update 方法**</font>。示例：

```java
public void update2() {
	// 获取会话
	Session session = HibernateUtil.getSession();
	// 开启事务
	Transaction transaction = session.beginTransaction();

	// 进行修改数据操作，如果想修改某个字段，保留其他字段，必须先查询再修改
	Customer c = session.get(Customer.class, 1L);
	// 持久态对象修改了属性，直接提交就可以更新到数据库里面
	c.setCustIndustry("xxoo");
	// session.saveOrUpdate(c);
	
	// 提交事务
	transaction.commit();
	// 关闭资源
	session.close();
}
```

#### 6.4.2. 实现原理

当获得一个持久态对象，此时 Session 保存两份副本，一份是快照数据(不能手动修改)，一份是缓存数据。此时再调用对象的 set 方法修改某个属性，<font color=red>修改属性的同时修改缓存的数据，但快照数据保持不变</font>。最后提交事务，就会<font color=red>对比快照与缓存中的数据，如果数据不一样，则更新数据库</font>（不需要再将使用 `update()` 方法）。

![](images/216724816259477.jpg)

## 7. Hibernate 拦截器

### 7.1. 概述

在 Hibernate 中，一个对象将被创建和保持。一旦对象已经被修改，它必须被保存到数据库里。这个过程持续直到下一次对象被需要，它将被从持久的存储中加载。

Hibernate 提供了一个 Interceptor 接口，可以实现在持久化对象不同的生命周期阶段，被调用进行一些所需要的任务的方法。这些方法是从会话到应用程序的回调函数，允许应用程序在持久化对象被保存，更新，删除或上传之前，对其进行检查或操作。

### 7.2. Interceptor 接口

`org.hibernate.Interceptor` 常用方法如下：

```java
int[] findDirty(Object entity, Serializable id, Object[] currentState,
		    Object[] previousState, String[] propertyNames, Type[] types);
```

- 在当 `flush()` 方法在一个 Session 对象上被调用时，会调用此方法。

```java
Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException;
```

- 在一个持续的类被实例化时被调用。

```java
void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
```

- 在一个对象被删除前被调用。

```java
boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, 
            Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException;
```

- 在当 Hibernate 探测到一个对象在一次 flush（例如，更新操作）中是脏操作（例如，被修改）时被调用。

```java
boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
```

- 在一个对象被初始化之前被调用。

```java
boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
```

- 在一个对象被保存前被调用。

```java
void preFlush(Iterator entities) throws CallbackException;
```

- 在一次 flush 前被调用。

```java
void postFlush(Iterator entities) throws CallbackException;
```

- 在一次 flush 已经发生并且一个对象已经在内存中被更新后被调用。

```java
isUnsaved()
```

- （没有找到此方法，待确认）

### 7.3. 拦截器的基础使用

自定义拦截器可以直接实现 `Interceptor` 类或者继承 `EmptyInterceptor` 类。以下是简单的使用 Hibernate 拦截器功能的步骤。

#### 7.3.1. 基础代码准备

1. 在数据库中创建 EMPLOYEE 表
2. 根据 EMPLOYEE 表的字段，创建相应的 Employee 类
3. 创建 Hibernate 总配置文件 hibernate.cfg.xml
3. 创建 Mapping 配置文件 Employee.hbm.xml，定义类与表的映射关系。（也可以直接使用 JPA 注解的方法来定义映射关系）

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
 "-//Hibernate/Hibernate Mapping DTD//EN"
 "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd"> 

<hibernate-mapping>
   <class name="Employee" table="EMPLOYEE">
      <meta attribute="class-description">
         This class contains the employee detail. 
      </meta>
      <id name="id" type="int" column="id">
         <generator class="native"/>
      </id>
      <property name="firstName" column="first_name" type="string"/>
      <property name="lastName" column="last_name" type="string"/>
      <property name="salary" column="salary" type="int"/>
   </class>
</hibernate-mapping>
```

#### 7.3.2. 创建拦截器

例子中继承 `EmptyInterceptor`，当 Employee 对象被创建和更新时拦截器的方法将自动被调用。*也可以根据需求实现更多的方法*。

```java
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

public class MyInterceptor extends EmptyInterceptor {
   private int updates;
   private int creates;
   private int loads;

   public void onDelete(Object entity,
                     Serializable id,
                     Object[] state,
                     String[] propertyNames,
                     Type[] types) {
       // do nothing
   }

   // This method is called when Employee object gets updated.
   public boolean onFlushDirty(Object entity,
                     Serializable id,
                     Object[] currentState,
                     Object[] previousState,
                     String[] propertyNames,
                     Type[] types) {
       if ( entity instanceof Employee ) {
          System.out.println("Update Operation");
          return true; 
       }
       return false;
   }
   public boolean onLoad(Object entity,
                    Serializable id,
                    Object[] state,
                    String[] propertyNames,
                    Type[] types) {
       // do nothing
       return true;
   }
   // This method is called when Employee object gets created.
   public boolean onSave(Object entity,
                    Serializable id,
                    Object[] state,
                    String[] propertyNames,
                    Type[] types) {
       if ( entity instanceof Employee ) {
          System.out.println("Create Operation");
          return true; 
       }
       return false;
   }
   //called before commit into database
   public void preFlush(Iterator iterator) {
      System.out.println("preFlush");
   }
   //called after committed into database
   public void postFlush(Iterator iterator) {
      System.out.println("postFlush");
   }
}
```

#### 7.3.3. 创建测试程序

测试程序需要注意，当创建 `Session` 对象时使用 `Interceptor` 类作为参数。

```java
import java.util.List; 
import java.util.Date;
import java.util.Iterator; 

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
   private static SessionFactory factory; 
   public static void main(String[] args) {
      try{
         factory = new Configuration().configure().buildSessionFactory();
      }catch (Throwable ex) { 
         System.err.println("Failed to create sessionFactory object." + ex);
         throw new ExceptionInInitializerError(ex); 
      }

      ManageEmployee ME = new ManageEmployee();

      /* Add few employee records in database */
      Integer empID1 = ME.addEmployee("Zara", "Ali", 1000);
      Integer empID2 = ME.addEmployee("Daisy", "Das", 5000);
      Integer empID3 = ME.addEmployee("John", "Paul", 10000);

      /* List down all the employees */
      ME.listEmployees();

      /* Update employee's records */
      ME.updateEmployee(empID1, 5000);

      /* Delete an employee from the database */
      ME.deleteEmployee(empID2);

      /* List down new list of the employees */
      ME.listEmployees();
   }
   /* Method to CREATE an employee in the database */
   public Integer addEmployee(String fname, String lname, int salary){
      Session session = factory.openSession( new MyInterceptor() );
      Transaction tx = null;
      Integer employeeID = null;
      try{
         tx = session.beginTransaction();
         Employee employee = new Employee(fname, lname, salary);
         employeeID = (Integer) session.save(employee); 
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
      return employeeID;
   }
   /* Method to  READ all the employees */
   public void listEmployees( ){
      Session session = factory.openSession( new MyInterceptor() );
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         List employees = session.createQuery("FROM Employee").list(); 
         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
            Employee employee = (Employee) iterator.next(); 
            System.out.print("First Name: " + employee.getFirstName()); 
            System.out.print("  Last Name: " + employee.getLastName()); 
            System.out.println("  Salary: " + employee.getSalary()); 
         }
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
   }
   /* Method to UPDATE salary for an employee */
   public void updateEmployee(Integer EmployeeID, int salary ){
      Session session = factory.openSession( new MyInterceptor() );
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         Employee employee = 
                    (Employee)session.get(Employee.class, EmployeeID); 
         employee.setSalary( salary );
         session.update(employee); 
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
   }
   /* Method to DELETE an employee from the records */
   public void deleteEmployee(Integer EmployeeID){
      Session session = factory.openSession( new MyInterceptor() );
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         Employee employee = 
                   (Employee)session.get(Employee.class, EmployeeID); 
         session.delete(employee); 
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
   }
}
```

测试结果：

```
$java ManageEmployee
.......VARIOUS LOG MESSAGES WILL DISPLAY HERE........

Create Operation
preFlush
postFlush
Create Operation
preFlush
postFlush
Create Operation
preFlush
postFlush
First Name: Zara  Last Name: Ali  Salary: 1000
First Name: Daisy  Last Name: Das  Salary: 5000
First Name: John  Last Name: Paul  Salary: 10000
preFlush
postFlush
preFlush
Update Operation
postFlush
preFlush
postFlush
First Name: Zara  Last Name: Ali  Salary: 5000
First Name: John  Last Name: Paul  Salary: 10000
preFlush
postFlush
```

数据库 EMPLOYEE 表被创建，并且查询到以下结果：

```sql
mysql> select * from EMPLOYEE;
+----+------------+-----------+--------+
| id | first_name | last_name | salary |
+----+------------+-----------+--------+
| 29 | Zara       | Ali       |   5000 |
| 31 | John       | Paul      |  10000 |
+----+------------+-----------+--------+
```

## 8. Hibernate 相关扩展知识

### 8.1. eclipse 创建 XML 的配置 DTD 规则文件生成配置文件

大部分的框架规则文件都在核心包，Hibernate 的规则文件也是在 core 核心包里面找。有注意事项如下：

1. 任何的框架只要提供了 XML 配置文件，那么必定会提供对应的规则文件：dtd 或者 schema 规则文件 ！！！！
2. 必须要学会在 Eclipse 里面配置规则文件，在 Eclipse 里面配置规则文件的作用。在框架代码（org.hibernate.cfg.Environment）里面找到配置需要的属性
    1. 通过规则文件生成 xml 文件
    2. 让 Eclipse 工具有提示标签的功能
3. Hibernate 的规则文件都在核心包里面 hibernate-core-5.0.12.Final.jar
4. 配置文件最重要的步骤，就是在核心包里面 cfg 找到对应的属性！！！！！

#### 8.1.1. 关联本地 dtd 约束文件的方式1

![](images/542580520266705.jpg) ![](images/330700620259374.jpg)

Key的值打开 DTD 文件中获得（注：不能复制双引号）

![](images/213850720255929.jpg)

新建 xml 文件时，引用 DTD 模版即可

![](images/182760820251683.jpg) ![](images/247240820269563.jpg)

![](images/546910820267167.jpg) ![](images/2310920264669.jpg)

![](images/247020920245910.jpg) ![](images/319800920268350.jpg)

使用本地约束的好处是，不需要联网，在编写的过程都有标签的提示。

配置文件的参数的属性名去哪里找？

任何的框架，只有是配置文件里面的属性值，必定在代码里面可以找到。直接通过查询 jar 包的方式查看配置 name 属性的名称。

hibernate-core-5.0.12.Final.jar -> org.hibernate.cfg -> AvailableSettings.class

![](images/416761020263486.jpg)

#### 8.1.2. 关联本地 dtd 约束文件的方式2

关联 dtd 文件时选择 url，对应是 hibernate/struts 的 dtd 下载网址（注意前后不能有空格和 `""` 双引号）

![](images/276871120257032.jpg)

### 8.2. Eclipse 插件 ERMaster 根据数据库表生成 JavaBean 代码

- 将 ERMaster 解压到 Eclipse 里面

![](images/286174812258770.jpg)

- 创建一个 ERMaster 文件，创建一个 ER 图

![](images/160524912246637.jpg)

- 导入数据库

![](images/417284912266803.jpg)

- 配置信息

![](images/136035012259472.jpg)

- 选择数据库驱动

![](images/433625012256027.jpg)

- 选择需要的组件，导出 JavaBean

![](images/361075112251781.jpg)

![](images/455655112269661.jpg)

### 8.3. Hibernate 的 session 跟 servlet 的 session 的 区别

- servlet 的 session 保持的是请求状态的。（用于返回当前用户的数据到页面）
- hibernate 的 session 保持程序和数据库连接的状态。（用于操作数据库）

### 8.4. Hibernate 使用 JDBC（了解，基本不用）

```java
// 使用 JDBC 操作（了解）
public void findALLByJDBC(){
	// 1.获得 Session 对象
	Session session = HibernateUtils.getSession();
	session.doWork(new Work() {
		@Override
		public void execute(Connection connection) throws SQLException {
			// 2.获得操作对象
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM cst_customer");
			while(rs.next()){
				System.out.println("客户名 ：" + rs.getString("cust_name"));
			}
			rs.close();
			statement.close();
			// 注意，不能在里面关闭连接，必须由session关闭
		}
	});
	session.close();
}
```

### 8.5. 逆向工程

逆向工程是通过数据库的结构生成代码。下面是以 eclipse 为例：

- 创建项目，修改为不用任何类库。

![](images/27440909246645.jpg) ![](images/114270909266811.jpg)

- 创建表，获得数据库的连接。

![](images/219271109259480.jpg) ![](images/318331109256035.jpg)

- 修改关联关系，设置实体类的注解格式。

![](images/171561309251789.jpg) ![](images/274821309269669.jpg)

- 修改单个实体类的属性。主要修改实体类名，可以生成实体类与 persistence.xml 配置文件。

![](images/344631509267273.jpg) ![](images/435941509264775.jpg)
