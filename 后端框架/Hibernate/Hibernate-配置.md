## 1. Hibernate 配置概述

Hibernate 需要一套相关数据库和其它相关参数的配置设置，这些映射信息定义了 Java 类怎样关联到数据库表。所有的核心配置信息有以下两种提供方式：

- 作为一个标准的 Java 属性文件提供的，文件默认名称：hibernate.properties
- 作为 XML 文件提供的，文件默认名称：hibernate.cfg.xml

## 2. Hibernate 总配置

总配置以 XML 格式文件 hibernate.cfg.xml 为例，核心配置主要是设置 `<hibernate-configuration>` -> `<session-factory>` -> `<property>` 标签的 `name` 属性

### 2.1. 常用配置项

- `hibernate.connection.driver_class`：设置数据库驱动。例如 MySQL 的配置值是 `com.mysql.cj.jdbc.Driver`
- `hibernate.connection.url`：数据库连接字符串。例如 `jdbc:mysql://localhost:3306/xxx`
- `hibernate.connection.username`：数据库用户名。
- `hibernate.connection.password`：数据库密码。
- `hibernate.show_sql`：设置是否显示操作的 SQL 语句，可选值是 true/false
- `hibernate.format_sql`：格式化输出 SQL 语句，前提是 `show_sql` 为 true。可选值是 true/false
- `hibernate.dialect`：配置方言。例如 MySQL 默认选择 `org.hibernate.dialect.MySQL5InnoDBDialect`。更多各种重要数据库同源语属性类型详见下表。

|           数据库           |                   方言属性                   |
| ------------------------- | ------------------------------------------ |
| DB2                       | org.hibernate.dialect.DB2Dialect           |
| HSQLDB                    | org.hibernate.dialect.HSQLDialect          |
| HypersonicSQL             | org.hibernate.dialect.HSQLDialect          |
| Informix                  | org.hibernate.dialect.InformixDialect      |
| Ingres                    | org.hibernate.dialect.IngresDialect        |
| Interbase                 | org.hibernate.dialect.InterbaseDialect     |
| Microsoft SQL Server 2000 | org.hibernate.dialect.SQLServerDialect     |
| Microsoft SQL Server 2005 | org.hibernate.dialect.SQLServer2005Dialect |
| Microsoft SQL Server 2008 | org.hibernate.dialect.SQLServer2008Dialect |
| MySQL                     | org.hibernate.dialect.MySQLDialect         |
| Oracle (any version)      | org.hibernate.dialect.OracleDialect        |
| Oracle 11g                | org.hibernate.dialect.Oracle10gDialect     |
| Oracle 10g                | org.hibernate.dialect.Oracle10gDialect     |
| Oracle 9i                 | org.hibernate.dialect.Oracle9iDialect      |
| PostgreSQL                | org.hibernate.dialect.PostgreSQLDialect    |
| Progress                  | org.hibernate.dialect.ProgressDialect      |
| SAP DB                    | org.hibernate.dialect.SAPDBDialect         |
| Sybase                    | org.hibernate.dialect.SybaseDialect        |
| Sybase Anywhere           | org.hibernate.dialect.SybaseAnywhereDialec |

- `hibernate.hbm2ddl.auto`：配置通过代码生成数据库的表结构，可以实现快速部署项目。因为不用创建表结构，大大减少部署的时间。可选值如下：
    - `create`：不管数据库里是否有表，覆盖
    - `update`：数据库有表就使用原来的表，没有的表就增加新表
    - `drop-create`：如果显示关闭 SessionFactory，会在程序结束后删除表
- `hibernate.current_session_context_class`：配置绑定线程，取值是 `thread`
- `hibernate.temp.use_jdbc_metadata_defaults`：因为 c3p0 和 hibernate 类型检查不兼容，会导致出现 `creation as createClob() method threw error` 异常，因此需要关闭 hibernate 的类型检查。由于方言的自动加载是基于 Hibernate 类型检查的。如果关闭了类型检查，导致无法自动加载方言。所以配置了关闭 hibernate 的类型检查，必须要手动设置方言，取值：`false`。
- `hibernate.connection.provider_class`：设置连接的提供者。如，`org.hibernate.c3p0.internal.C3P0ConnectionProvider`。

### 2.2. c3p0 连接池相关配置项

- `hibernate.c3p0.max_size`：设置c3p0参数：最大连接数（如：10）
- `hibernate.c3p0.min_size`：设置c3p0参数：设置启动连接池支持的连接数量（如：5）
- `hibernate.c3p0.timeout`：设置c3p0参数：设置操作时间，单位：毫秒（如：300000）
- `hibernate.c3p0.max_statements`：设置c3p0参数：最大操作数据statement,不能大于最大连接数，一般配置95%左右（如：9）

### 2.3. 导入其他 xml 文件

配置加载 xml 文件

```xml
<mapping resource="实体类映射文件相对路径"/>
```

注：相对路径是指相对 hibernate.cfg.xml 文件的路径，路径的最前面没有 "`/`"。例如：

```xml
<mapping resource="demo/entity/hbm/Customer.hbm.xml"/>
```

## 3. Hibernate 实体类映射配置

### 3.1. 映射文件

在传统项目中，一个对象/关系型映射一般定义在 XML 文件中。映射文件指示 Hibernate 如何将已经定义的类或类组与数据库中的表对应起来。

> Notes: **目前的项目都是直接使用全注解(指 JPA 的注解)的方式来配置对象与表的映射关系，xml 映射配置已过时**。

例如，有如下 RDBMS 表

```sql
create table EMPLOYEE (
    id INT NOT NULL auto_increment,
    first_name VARCHAR(20) default NULL,
    last_name  VARCHAR(20) default NULL,
    salary     INT  default NULL,
    PRIMARY KEY (id)
);
```

对于想通过 ORM 框架将数据库的数据映射到相应的持久性的对象，需要根据上表的字段来定义 POJO 类：

```java
public class Employee {
    private int id;
    private String firstName; 
    private String lastName;   
    private int salary;  

    // ...省略无参构造、有参构造、setter/getter方法
}
```

基于前面的表与持久化类，可以定义下列映射文件来指示 Hibernate 如何将已定义的类或类组与数据库表匹配。

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

Hibernate 默认情况下，需要以格式 `<classname>.hbm.xml` 保存映射文件。示例中保存映射配置在 Employee.hbm.xml 文件中。其中映射文件中使用的一些基础的标签：

- 映射文件是一个以 `<hibernate-mapping>` 为根元素的 XML 文件，里面包含所有 `<class>` 标签。
- `<class>` 标签是用来定义从一个 Java 类到数据库表的特定映射。Java 的类名使用 `name` 属性来表示，数据库表明用 `table` 属性来表示。
- `<meta>` 标签是一个可选元素，可以被用来修饰类。
- `<id>` 标签将类中独一无二的 ID 属性与数据库表中的主键关联起来。标签中的 `name` 属性引用类的属性名，`column` 属性引用数据库表的列名。`type` 属性保存 Hibernate 映射的类型，这个类型会将从 Java 转换成 SQL 数据类型。
- 在`<id>` 标签中，包含的 `<generator>` 子标签用来自动生成主键值。标签中的 `class` 属性可以设置 `native`, `identity`, `sequence` 或 `hilo` 算法，会根据底层数据库的情况来创建主键。
- `<property>` 标签用来将 Java 类的属性与数据库表的列匹配（除主键外的列）。标签中 `name` 属性引用的是类的性质，`column` 属性引用的是数据库表的列。`type` 属性保存 Hibernate 映射的类型，这个类型会将从 Java 转换成 SQL 数据类型。

### 3.2. 主键生成策略

主键生成策略，就是 Hibernate 提供了多种生成主键值的方法。常用的 Hibernate 的主键生成策略：

- `increment`：不使用数据库本地的自增长策略，而是由程序(Hibernate 框架)产生一个自增长的ID 值，赋予数据库
- `idenitty`：指定使用数据库里面的 ID 自增长策略
    - 只能用于有 ID 自增长功能的数据库，如：MySQL, SQLServer...
    - 不支持没有 ID 自增长策略的数据库，如：Oracle, DB2...
- `sequence`：使用序列的实现 ID 生成策略
    - 主要用于有序列的数据库，如：Oracle, DB2...
    - 如果不支持序列的数据库（如：MYSQL），该策略会使用一个表模拟序列。
- `native`：使用数据库本地的策略，就是数据库里面使用怎么样的策略就用什么策略，HIbernate 不做任何的判断。如 MySQL 数据库使用了 `increment_auto`(自增长策略)，配置 `native` 则表示直接调用数据库里面的`increment_auto` 策略
- `uuid`：是数据库的主键是使用一个唯一的字符串的来存储。这个唯一的字符串就是 UUID。
- `assigned`：就是不使用主键生成策略，由手工输入 ID

配置示例：

```xml
<id name="custId" column="cust_id">
    <generator class="native"></generator>
</id>
```

#### 3.2.1. increment

increment 策略是指<font color=red>**不使用数据库本地的自增长策略**</font>，而是由 Hibernate 框架产生一个自增长的 ID 值，赋予数据库的主键。

![](images/309750115258775.jpg)

实现原理：每次的 sql 语句，都先获得 id 的最大值（如：`select max(id) form 表名`），再在此 ID 值基础上加 1 做为插入数据的 id 值。

- 好处：兼容好，可以支持各种数据库。
- 缺点：由于主键值由框架生成，所有效率相对低。

应用场景：适合一些需要支持多种数据库的产品型项目。

#### 3.2.2. identity（重要，一般用于 MySQL）

identity 策略指定使用数据库里面的 ID 自增长策略。但只能用于有 ID 自增长功能的数据库，如:MySQL, SQLServer 等；不支持没有 ID 自增长策略的数据库，如 Oracle, DB2 等。

- 好处：默认就认为数据库是使用 ID 自增长策略，效率高。
- 缺陷：兼容差。不支持使用序列实现 ID 自增长的数据库。

![](images/17110415246642.jpg)

#### 3.2.3. sequence（重要，一般用于 Oracle）

sequence 是使用序列的实现 ID 生成策略，主要用于有序列的数据库，如：Oracle, DB2 等；如果不支持序列的数据库（如：MYSQL），该策略会使用一个表模拟序列。

为什么有序列的数据库（Oracle）使用 SEQUENCE 策略而不用 native 策略？因为 SEQUENCE 策略是可以设置序列的名字，步长等参数的，而 Native 策略是不可以。另外 Native 策略生成的序列名统一使用 Hibernate_sequence。如果出现多个表的情况，就导致多个表使用同一个序列，这样会导致表的 ID 值断号不连续。

<font color=red>**使用 SEQUENCE 策略可以设置每一个表对应的一个序列的参数，参数设置参数名！！！！**</font>

#### 3.2.4. native

native 是使用数据库本地的策略，就是数据库里面使用怎么样的策略就用什么策略，HIbernate 不做任何的判断。如 MySQL 数据库使用了 `increment_auto`(自增长策略)；如果 Oracle 使用 native 则使用序列生成主键值。

<font color=red>**注意：Oracle 使用 native 这种方式不好的地方是，序列名为 Hibernate_SEQUENCE.而且多个表使用一个序列。会导致序列断号。**</font>

#### 3.2.5. uuid

uuid 主键策略就是使用一个唯一的字符串的来存储数据库的主键，这个唯一的字符串就是 UUID。**UUID 用于存储基础数据的表。所谓的基础数据，就是系统必须依赖的数据。**

应用场景：如果几个不同开发人员一起开发一个项目，使用 UUID 可以确保 ID 的冲突。

#### 3.2.6. assigned

assigned 就是不使用 ID 自动生成值的策略，需要手工写入 ID 的值。

> Notes: 此策略创建的实体类对象必须提供 `setXxId(xx)` 方法，如果调用代码没有 ID 值，会报异常。

### 3.3. 映射类型

在 Hibernate 映射文件中，Java 数据类型会映射到相应的 RDBMS 数据格式。在映射文件中声明被使用的 types 不是 Java 数据类型，也不是 SQL 数据库类型。这种类型被称为 Hibernate 映射类型，可以从 Java 翻译成 SQL，反之亦然。

**原始类型**

|   映射类型    |           Java 类型           |     ANSI SQL 类型     |
| ----------- | ---------------------------- | -------------------- |
| integer     | int 或 java.lang.Integer     | INTEGER              |
| long        | long 或 java.lang.Long       | BIGINT               |
| short       | short 或 java.lang.Short     | SMALLINT             |
| float       | float 或 java.lang.Float     | FLOAT                |
| double      | double 或 java.lang.Double   | DOUBLE               |
| big_decimal | java.math.BigDecimal         | NUMERIC              |
| character   | java.lang.String             | CHAR(1)              |
| string      | java.lang.String             | VARCHAR              |
| byte        | byte 或 java.lang.Byte       | TINYINT              |
| boolean     | boolean 或 java.lang.Boolean | BIT                  |
| yes/no      | boolean 或 java.lang.Boolean | CHAR(1) ('Y' or 'N') |
| true/false  | boolean 或 java.lang.Boolean | CHAR(1) ('T' or 'F') |

**日期和时间类型**

|    映射类型     |               Java 类型               | ANSI SQL 类型 |
| ------------- | ------------------------------------ | ------------ |
| date          | java.util.Date 或 java.sql.Date      | DATE          |
| time          | java.util.Date 或 java.sql.Time      | TIME          |
| timestamp     | java.util.Date 或 java.sql.Timestamp | TIMESTAMP     |
| calendar      | java.util.Calendar                   | TIMESTAMP     |
| calendar_date | java.util.Calendar                   | DATE          |

**二进制和大型数据对象**

|    映射类型    |                      Java 类型                       |    ANSI SQL 类型     |
| ------------ | --------------------------------------------------- | ------------------- |
| binary       | byte[]                                              | VARBINARY (or BLOB) |
| text         | java.lang.String                                    | CLOB                |
| serializable | any Java class that implements java.io.Serializable | VARBINARY (or BLOB) |
| clob         | java.sql.Clob                                       | CLOB                |
| blob         | java.sql.Blob                                       | BLOB                |

**JDK 相关类型**

|  映射类型  |      Java 类型      | ANSI SQL 类型 |
| -------- | ------------------ | ------------- |
| class    | java.lang.Class    | VARCHAR       |
| locale   | java.util.Locale   | VARCHAR       |
| timezone | java.util.TimeZone | VARCHAR       |
| currency | java.util.Currency | VARCHAR       |

