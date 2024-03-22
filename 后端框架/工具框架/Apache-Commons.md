# Apache Common 包简介

Apache Commons 是对 JDK 的拓展，包含了很多开源的工具，用于解决平时编程经常会遇到的问题，减少重复劳动。

> 官网： https://commons.apache.org/

常用包：

- Commons BeanUtils
- Commons Codec
- Commons Collections
- Commons Compress
- Commons Configuration
- Commons CSV
- Commons Daemon
- Commons DBCP
- Commons DBUtils
- Commons Digester
- Commons Email
- Commons Exec
- Commons FileUpload
- Commons IO
- Commons JCI
- Commons Lang3
- Commons Logging
- Commons Math
- Commons Net
- Commons Pool
- Commons Primitives
- Commons Validator
- Apache HttpClient

# Apache Commons IO

## 1. 简介

Apache Commons IO 是第三方 IO 操作工具类库，jar 包名称是 `commons-io-x.x.jar`

- 官网：http://commons.apache.org/proper/commons-io/index.html
- 官方API文档：http://commons.apache.org/proper/commons-io/apidocs/index.html

## 2. 基础使用

### 2.1. 直接导入jar包

Apache Commons IO包的下载地址：http://commons.apache.org/proper/commons-io/download_io.cgi

### 2.2. maven 依赖

```xml
<!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>x.y.z</version>
</dependency>
```

## 3. FilenameUtils 工具类常用方法

### 3.1. getExtension

```java
static String getExtension(String filename);
```

- 获取路径中的扩展名(`.后缀名`)

### 3.2. getName

```java
static String getName(String filename);
```

- 获取路径中的文件名包括(`.后缀名`)

### 3.3. isExtension

```java
static boolean isExtension(String filename, String extension);
```

- 判断路径是否符合后缀名(extension)，是返回`true`，否则返回`fale`

## 4. FileUtils 工具类常用方法

### 4.1. copyDirectory

```java
static String readFileToString(File file);
```

- 读取文件中的内容转成字符串，使用默认编码GBK。可以定义使用什么编码。如下例：

```java
String str = FileUtils.readFileToString(new File("a.txt"), "utf8");
```

### 4.2. write

```java
void write(File file, CharSequence data);
```

- 将指定的字符串写出到目标文件中

### 4.3. writeStringToFile

```java
static void writeStringToFile(File file, String data);
```

- 将指定字符串写出到目标文件中

### 4.4. copyFile

```java
public static void copyFile(final File srcFile, final File destFile) throws IOException
```

- 将`srcFile`源文件复制到`destFile`目标位置中，如`destFile`不存在则会创建目标文件

```java
public static void copyFile(final File srcFile, final File destFile,
                                final boolean preserveFileDate) throws IOException
```

- 将`srcFile`源文件复制到`destFile`目标位置中，如`destFile`不存在则会创建目标文件
    - `preserveFileDate`参数：用于指定复制后的文件修改日期是否与源文件一样，`true`代表与源文件一样。

```java
public static long copyFile(final File input, final OutputStream output) throws IOException
```

- 将`input`源文件夹对象转成`output`输入流对象，写到目标文件夹中，如目标文件不存在，则会创建该文件

### 4.5. copyDirectory

```java
public static void copyDirectory(final File srcDir, final File destDir) throws IOException
```

- 将`srcDir`源文件夹中内容复制到另一个`destDir`目标文件夹中

```java
public static void copyDirectory(final File srcDir, final File destDir,
                                     final boolean preserveFileDate)
```

- 将`srcDir`源文件夹中内容复制到另一个`destDir`目标文件夹中
    - `preserveFileDate`参数：用于指定复制后的文件修改日期是否与源文件一样，`true`代表与源文件一样。

```java
public static void copyDirectory(final File srcDir, final File destDir,
                                     final FileFilter filter) throws IOException
```

- 将`srcDir`源文件夹中内容复制到另一个`destDir`目标文件夹中
    - `FileFilter`参数：是一个函数式接口，用于实现对象目录的过滤功能，入参为当前源文件对象，返回`true`时表示复制该文件，否则表示不复制

```java
public static void copyDirectory(final File srcDir, final File destDir,
                                     final FileFilter filter, final boolean preserveFileDate) throws IOException
```

- 将`srcDir`源文件夹中内容复制到另一个`destDir`目标文件夹中
    - `FileFilter`参数：是一个函数式接口，用于实现对象目录的过滤功能，入参为当前源文件对象，返回`true`时表示复制该文件，否则表示不复制
    - `preserveFileDate`参数：用于指定复制后的文件修改日期是否与源文件一样，`true`代表与源文件一样。

### 4.6. copyDirectoryToDirectory

```java
static copyDirectoryToDirectory(File srcDir,File destDir);
```

- 将srcDir源文件夹中内容复制到另一个destDir目标文件夹中(多一个父级文件夹destDir)

## 5. IOUtils 类常用方法

### 5.1. copy

```java
static int copy(InputStream input, OutputStream output)
```

- 将一个字节输入流复制到一个字节输出流，返回复制的字节数

# Apache Commons Lang

## 1. 简介

Apache Commons Lang 是第三方 Java 基础 API 增强的工具包，特别是字符串操作方法，基本数值方法，对象反射，并发，创建和序列化以及系统属性。此外，它还包含对`java.util.Date`的基本增强，以及一系列专用于构建方法的实用程序，例如 `hashCode`，`toString` 和 `equals`。

- 官网：http://commons.apache.org/proper/commons-lang/
- 官方API文档：http://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html

## 2. 基础使用

### 2.1. 直接导入jar包

Apache Commons Lang 包的下载地址：http://commons.apache.org/proper/commons-lang/download_lang.cgi

### 2.2. maven 依赖

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>x.y</version>
</dependency>
```

# Apache Commons Lang3

是处理 Java 基本对象方法的工具类包，该类包提供对字符、数组等基本对象的操作，弥补了 `java.lang api` 基本处理方法上的不足。

[官方 API 文档](https://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html)

功能结构：

```
ArrayUtils – 用于对数组的操作，如添加、查找、删除、子数组、倒序、元素类型转换等；

BitField – 用于操作位元，提供了一些方便而安全的方法；

BooleanUtils – 用于操作和转换boolean或者Boolean及相应的数组；

CharEncoding – 包含了Java环境支持的字符编码，提供是否支持某种编码的判断；

CharRange – 用于设定字符范围并做相应检查；

CharSet – 用于设定一组字符作为范围并做相应检查；

CharSetUtils – 用于操作CharSet；

CharUtils – 用于操作char值和Character对象；

ClassUtils – 用于对Java类的操作，不使用反射；

ObjectUtils – 用于操作Java对象，提供null安全的访问和其他一些功能；

RandomStringUtils – 用于生成随机的字符串；

SerializationUtils – 用于处理对象序列化，提供比一般Java序列化更高级的处理能力；

StringEscapeUtils – 用于正确处理转义字符，产生正确的Java、JavaScript、HTML、XML和SQL代码；

StringUtils – 处理String的核心类，提供了相当多的功能；

SystemUtils – 在java.lang.System基础上提供更方便的访问，如用户路径、Java版本、时区、操作系统等判断；

Validate – 提供验证的操作，有点类似assert断言；

WordUtils – 用于处理单词大小写、换行等。
```

# Apache Commons BeanUtils

## 1. BeanUtils 概述

BeanUtils 是 Apache commons 组件的成员之一，是 Apache 组织开发的一款方便操作 JavaBean 的工具类。主要使用场景如下：

- 使用 BeanUtils 给 JavaBean 对象的属性赋值和取值。
- 使用 BeanUtils 将一个 JavaBean 对象的属性值赋值给另一个 JavaBean 对象。
- 使用 BeanUtils 将一个 Map 集合中数据封装的一个 JavaBean 对象中。

## 2. 使用步骤

### 2.1. 直接导入 jar 包

1. 下载地址：http://commons.apache.org/
2. 使用第三方工具，需要导入 jar 包：
	- commons-beanutils-1.9.3.jar -- 工具核心包
	- commons-logging-1.2.jar -- 日志记录组件
	- commons-collections-3.2.2.jar -- 增强的集合包

### 2.2. （待补充）maven 依赖

```xml

```

## 3. BeanUtils 对象工具类

`org.apache.commons.beanutils.BeanUtils` 提供一些操作 JavaBean 的静态工具方法：

```java
public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
```
- 给指定的 JavaBean 对象的属性赋值。<font color=red>如果指定的属性不存在，不抛异常，也不赋值</font>。
    - `Object bean`: 要接收的对象
    - `String name`: 对象的属性名
    - `Object value`：要设置的值

```java
public static String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
```

- 获得指定对象(bean)中指定属性(name)的值。<font color=red>**如果属性不存在，直接报错**</font>。

```java
public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException
```

- 将一个 JavaBean 中所有的属性复制赋值给另一个 JavaBean。复制的<font color=red>**两个 JavaBean 的属性不必完全相同，只会对相同属性名进行复制赋值**</font>。*注意：目标在前面的参数*
    - `Object dest`：目标对象
    - `Object orig`：源对象

```java
public static void populate(Object bean, Map<String, ? extends Object> properties) throws IllegalAccessException, InvocationTargetException
```

- 将 Map 集合中的键值对，以键名对应属性名的方式复制给 bean。
> Tips: 使用 `BeanUtils` 中 `populate` 的方法时，通常会再次封装成一个工具类，可以对它不能处理的数据再进行处理。

```java
public static Map<String, String> describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
```

- 将一个 JavaBean 中所有的属性转成 Map 集合，并且多出一个 class 属性键值对。如操作后，类中会多出一个属性 `class = class com.moon.entity.User`

## 4. ConvertUtils 日期转换器

`org.apache.commons.beanutils.ConvertUtils` 是一用于日期操作、转换等工具类库
	
```java
public void setPatterns(final String[] patterns);
```
		
- `DateConverter` 父类方法，用来定义日期格式。
    - `String[] patterns`：存放定义日期格式的字符串数组
	
```java
public static void register(Converter converter, Class<?> clazz)
```

- 注册日期转换器

两个方法配合使用示例：

```java
// 创建日期转换器对象
DateConverter dateC = new DateConverter();
// 设置日期模式
dateC.setPatterns(new String[]{"yyyy-MM-dd", "yyyy年MM月dd日", "yyyy/MM/dd"});
// 注册日期转换器
ConvertUtils.register(dateC, Date.class);
// 将map集合的数据封装的u对象中
BeanUtils.populate(bean, map);
```

## 5. 扩展：与 Spring 提供的 BeanUtils 的区别

`org.springframework.beans.BeanUtils` 与 `org.apache.commons.beanutils.BeanUtils` 都提供了 `copyProperties` 方法，作用是将一个 JavaBean 对象中的数据复制到另一个属性结构相似的 JavaBean 对象中。两者的使用注意事项如下：

1. 两者的 `copyProperties` 方法参数位置不同

```java
// org.springframework.beans.BeanUtils
public static void copyProperties(Object source, Object target) throws BeansException

// org.apache.commons.beanutils.BeanUtils
public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException
```

2. 要求两个 JavaBean 的属性名相同，且有对应的 setter 和 getter 方法。其实底层原理是使用 source 对象的 getXxx 方法和 target 对象的 setXxx 方法。
3. source 对象有的属性而 target 对象没有的属性，不会封装到 target 对象中；target 对象有的属性而 source 对象没有的属性，也会给 target 对象赋值，但数据为默认值（注意基本类型默认值与引用类型默认值不同）。
4. 类型转换问题。
    - 基本类型与其对应的封装类型可以相互转换
    - `org.springframework.beans.BeanUtils` 与 `org.apache.commons.beanutils.BeanUtils` 对于 String 和 Date 类型转换的情况是不同的。但无论是那个工具类，默认情况下都不能将 `String` 类型转成 `Date` 类型

# Commons DBUtils

## 1. DbUtils 概述

`DbUtils` 是 Apache 组织开发的一个开源 JDBC 工具类库。是一款方便操作数据库的工具。

使用 DBUtils 能简化 JDBC 操作数据库复杂的代码，同时也不会影响程序的性能。使用需要导入 jar 包：

- commons-dbutils-x.x.jar  核心包
- commons-logging-x.x.x.jar  日志记录包

目前使用的是：commons-dbutils-1.7.jar

## 2. DbUtils 工具的核心类

### 2.1. DbUtils 类

提供了装载 JDBC 驱动程序、关闭资源和处理事务的相关静态方法

```java
public static void close(…) throws java.sql.SQLException;
```

DbUtils 类提供了三个重载的关闭方法。这些方法检查所提供的参数是不是 `NULL`，如果不是的话，它们就关闭 `Connection`、`Statement` 和 `ResultSet`。

```java
public static void closeQuietly(…);
```

这一类方法不仅能在 `Connection`、`Statement` 和 `ResultSet` 为 `NULL` 情况下避免关闭，还能隐藏一些在程序中抛出的 `SQLException`。

### 2.2. QueryRunner 类

用来对数据库执行CRUD(增删改查)操作

#### 2.2.1. QueryRunner 增删改操作方式1：传入数据源对象

**构造方法**

```java
QueryRunner(DataSource ds);
```

根据数据源创建查询器对象，方法参数：

- `ds`: 连接池对象，数据源

**增删改的方法**

```java
int update(String sql);
int update(String sql, Object param);
int update(String sql, Object...params);
```

执行增删改操作，返回影响的行数。

方法参数：

- `sql`：需要执行的sql语句
- `params`：实际参数(真实参数)，给sql语句中的占位符赋值

> 注：以上方法在内部都有释放资源的代码，所以<font color=red>**无需关闭连接**</font>等操作


#### 2.2.2. QueryRunner 增删改操作方式2：没有传入任何对象

**构造方法**

```java
QueryRunner();
```

创建查询器对象

**增删改的方法，在方法中指定 `Connection` 对象**

```java
int update(Connection conn, String sql);
int update(Connection conn, String sql, Object...params);
```

参数说明：

- `conn`: 数据库连接对象
- `sql`: 需要执行的sql语句
- `params`: 实际参数(真实参数)，给sql语句中的占位符赋值

> 注：以上方法没有释放资源的代码，<font color=red>**需要操作者手动关闭连接**</font>

#### 2.2.3. QueryRunner 查询操作方式1：没有连接对象

构造方法

```java
QueryRunner(DataSource ds);
```

根据数据源创建查询器对象。

参数说明：

- `ds`: 连接池对象，数据源

```java
Object query(String sql, ResultSetHandler rsh)
Object query(String sql, ResultSetHandler rsh, Object... params)
```

> 注：以上方法在内部都有释放资源的代码，所以<font color=red>**无需关闭连接**</font>等操作

#### 2.2.4. QueryRunner 查询操作方式2：有连接对象，需要手动关闭资源

构造方法

```java
QueryRunner();
```

创建查询器对象

```java
Object query(Connection conn, String sql, ResultSetHandler rsh)
Object query(Connection conn, String sql, ResultSetHandler rsh, Object... params)
```

> 注：以上方法没有释放资源的代码，<font color=red>**需要操作者手动关闭连接**</font>

#### 2.2.5. QureyRunner 的操作多个数据方法

```java
int[] batch(String sql, Object[][] params)
```

用于同一个sql语句进行执行多次的方法。

参数说明：

- `params`：二维数组
    - 一维：sql语句要执行多次
    - 二维：就是每条sql语句中`?`存储的占位符的参数，二维长度是`?`参数的个数

> <font color=red>**注：批量处理，是访问数据库一次，一次性执行重复多个sql语句处理，这样可以减少数据库访问次数的压力(如果使用逐条删除的方式，每次删除都访问数据库一次)。**</font>

示例：

```java
/**
 * 批量删除商品
 *
 * @param pids 商品id数组
 */
public void delProductBatch(String[] pids) {
	// 删除数据的sql语句，只需要将删除单个数据的语句执行多次，每次根据不同的id删除即可
	String sql = "delete from product where pid=?;";

	// DBUtils.runner.batch(sql, params),用于同一个sql语句进行执行多次的方法
	// params 二维数组
	// 一维：sql语句要执行多少次
	// 二维：就是每个sql语句中？存储的占位符的值

	// 创建二维数组,长度是传入数组的长度，每个元素数组长度是1
	Object[][] params = new Object[pids.length][1];
	// 使用循环给二维数组赋值
	for (int i = 0; i < params.length; i++) {
		params[i][0] = pids[i];
	}

	// 使用查询器方法操作多次操作
	try {
		qr.batch(sql, params);
	} catch (SQLException e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
}
```

### 2.3. ResultSetHandler 接口

用来定义如何封装查询结果集

#### 2.3.1. 接口的方法

```java
Object handle(ResultSet rs);
```

方法调用时机：调用`query(String sql, ResultSetHandler rsh);`方法查询到结果之后会触发结果集处理器对象的该方法，按需要重写方法返回自定义的结果。

方法内如何处理由操作者定义。当`DbUtils`提供的常用实现类不能满足要求的时，再定义匿名内部类重写该方法

Code Demo: 实现 `ResultSetHandler` 接口，重写 `handle` 方法

```java
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import jdbc.C3P0Utils;

/*
 * 关卡1训练案例10
 	* 查询用户表的所有用户数据，要求如下：
 	* 1.只查询用户名和性别两个字段信息。
 	* 2.查询结果是一个集合，集合中存放所有的用户对象。
	 	* 操作步骤
	 	* 1. 通过 C3P0Utils 工具类获得数据源对象
	 	* 2. 根据数据源对象创建 QueryRunner 对象
	 	* 3. 编写查询的 SQL 语句。
	 	* 4. 调用 QueryRunner 的对象的 query 方法进行查询
	 	* 5. 获得查询结果。
 */
public class QueryRunnerTest {
	public static void main(String[] args) {
		// 使用c3p0工具类获取数据源对象
		DataSource ds = C3P0Utils.getDataSource();

		// 获取QueryRunner对象
		QueryRunner qr = new QueryRunner(ds);

		// 准备sql语句
		String sql = "select name,gender from users;";

		// 调用query文件进行查询，重写handle方法，返回一个对象集合
		try {
			List<User> list = qr.query(sql, new ResultSetHandler<List<User>>() {

				@Override
				public List<User> handle(ResultSet rs) throws SQLException {
					// 创建集合用来存放对象
					List<User> list = new ArrayList<>();
					// 使用循环记取数据库返回的结果集
					while (rs.next()) {
						// 创建用户对象
						User u = new User();
						u.setName(rs.getString("name"));
						u.setGender(rs.getString("gender"));
						list.add(u);
					}
					return list;
				}
			});

			// 遍历集合
			for (User u : list) {
				System.out.println(u);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
```

### 2.4. 常用的 ResultSetHandler 接口的实现类

#### 2.4.1. 封装成 JavaBean (BeanHandler / BeanListHandler)

<font color=red>**前提：表的列名与 JavaBean 属性名要相同**</font>

```java
T BeanHandler<T>(Class clazz);
```

把结果集的一行数据封装成 JavaBean。常用于查询一条记录的情况。如果SQL语句是查询多个记录，则返回查询到的第一行记录。

```java
List<T> BeanListHandler<T>(Class clazz);
```

把结果集的每一行数据封装成 JavaBean，把这个 JavaBean 放入 `List` 中返回

Code Demo:

```java
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import day25.level01.User;
import jdbc.DBCPUtils;

/*
 * 关卡2训练案例3
 	* 查询用户表中的第一条用户记录并将该记录封装成一个 JavaBean 对象。
 	* 注意事项：JavaBean 属性名和用户表的列名要相同。
 */
public class Test02_03 {
	public static void main(String[] args) throws SQLException {
		// 使用工具类得到数据源对象,并创建QueryRunner对象
		QueryRunner qr = new QueryRunner(DBCPUtils.getDataSource());

		// 准备sql语句
		String sql = "select * from users";

		// 准备好bean对象，属性名与用户表列相同，调用query方法封装JavaBean对象
		User u = qr.query(sql, new BeanHandler<>(User.class));

		// 输入bean对象
		System.out.println(u);
	}
}

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import day25.level01.User;
import jdbc.DBCPUtils;

/*
 * 关卡2训练案例4
 	* 1.查询用户表的所有用户记录并将每一条记录封装成 JavaBean 对象存放到集合中。
*/
public class Test02_04 {
	public static void main(String[] args) throws SQLException {
		// 创建无参QueryRunner对象
		QueryRunner qr = new QueryRunner();

		// 准备sql语句 查询用户表全部记录
		String sql = "select * from users;";

		// 使用工具类获取连接对象，创建BeanListHandler对象，执行sql语句
		Connection conn = DBCPUtils.getConnection();
		List<User> list = qr.query(conn, sql, new BeanListHandler<>(User.class));

		// 遍历集合将Bean对象输出
		for (User u : list) {
			System.out.println(u);
		}

		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}
}
```

#### 2.4.2. 封装成 Map (MapHandler / MapListHandler)

<font color=red>**可用于表连接查询的时候**</font>

```java
Map<String, Object> MapHandler();
```

将结果集中的第一行数据封装到一个 `Map` 里，`key` 是列名，`value` 就是对应的值。

```java
List<Map<String, Object>> MapListHandler();
```

将结果集中的每一行数据都封装到一个 `Map` 里，然后再存放到 `List`

Code Demo:

```java
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import jdbc.DBCPUtils;

/*
 * 关卡2训练案例6
 	* 1.定义一个方法：查询用户表获取第一条用户记录并封装成 Map 集合(key 是字段名称，value是字段值)。
 	* 2.定义一个方法：查询用户表获取所有用户记录并返回一个集合，集合中存放的都是 Map 对象，
 		* 一个 Map 对象封装对应一个用户记录。
 */
public class Test02_06 {
	public static void main(String[] args) throws SQLException {
		// 创建无参的QueryRunner对象
		QueryRunner qr = new QueryRunner();

		// 定义执行的sql操作语句
		String sql = "select * from users";

		// 查询用户表第一个用户记录并封装成map集合
		testMapHandler(qr, sql);
		System.out.println("**********************");

		// 查询用户表获取所有用户记录并返回一个集合，集合中存放的都是 Map对象
		testMapListHandler(qr, sql);
	}

	public static void testMapListHandler(QueryRunner qr, String sql) throws SQLException {
		// 使用工具类获取Connection对象
		Connection conn = DBCPUtils.getConnection();
		// 创建MapHandler对象，执行sql语句
		List<Map<String, Object>> list = qr.query(conn, sql, new MapListHandler());

		// 遍历List集合
		for (Map<String, Object> map : list) {
			Set<String> key = map.keySet();
			for (String k : key) {
				System.out.println(k + " = " + map.get(k));
			}
			System.out.println("==========");
		}
		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}

	public static void testMapHandler(QueryRunner qr, String sql) throws SQLException {
		// 使用工具类获取Connection对象
		Connection conn = DBCPUtils.getConnection();
		// 创建MapHandler对象，执行sql语句
		Map<String, Object> map = qr.query(conn, sql, new MapHandler());

		// 遍历Map集合
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			System.out.println(key + " = " + map.get(key));
		}
		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}
}
```

#### 2.4.3. 封装成数组(ArrayHandler / ArrayListHandler)

```java
Object[] ArrayHandler();
```

把结果集的第一行数据封装成对象数组。(常用于只有一条记录的情况)

```java
List<Object[]> ArrayListHandler();
```

把结果集的每一行数据封装对象数组，把这个对象数组放入 `List` 中

Code Demo:

```java
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import jdbc.DBCPUtils;

/*
 * 关卡2训练案例1
 	* 查询用户表中的第一条数据。并将数据封装成对象数组
 		* 操作步骤
	 	* 1.通过 C3P0Utils 工具类获得数据源对象
	 	* 2.创建 QueryRunner 对象
	 	* 3.编写 SQL 语句
	 	* 4.调用 QueryRunner 对象的 query 方法传入 SQL 语句和 ArrayHandler 对象
	 	* 5.接收方法返回值即对象数组。
 */
public class Test02_01 {
	public static void main(String[] args) throws SQLException {
		// 准备sql语句
		String sql = "select * from users";

		// 使用DBCP工具类获取数据源，并创建QueryRunner对象
		QueryRunner qr = new QueryRunner(DBCPUtils.getDataSource());
		// 使用ArrayHandler获取第一行数据并封装成对象数组
		Object[] arr = qr.query(sql, new ArrayHandler());

		// 直接输出数组
		System.out.println(Arrays.toString(arr));

		System.out.println("================");
		// 使用无参构造方法创建QueryRunner对象
		QueryRunner qr2 = new QueryRunner();
		// 使用工具类获取Connection对象，传入query方法执行sql语句,
		Connection conn = DBCPUtils.getConnection();
		// 使用ArrayListHandler返回一个对象数组集合
		List<Object[]> arr2 = qr2.query(conn, sql, new ArrayListHandler());

		// 使用DbUtils方法关闭资源
		DbUtils.closeQuietly(conn);

		// 遍历对象数组集合
		for (Object[] objs : arr2) {
			System.out.println(Arrays.toString(objs));
		}

	}
}
```

#### 2.4.4. 封装单行单列数据 (ScalarHandler)

```java
T ScalarHandler<T>();
```

把结果集的第一行第一列取出。通常用于只有单行单列的聚合函数查询查询结果集。

> <font color=red>*注：用来统计数量是时返回的数据类型是`long`*</font>

Code Demo:

```java
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import jdbc.DBCPUtils;

/*
 * 关卡2训练案例4
 	* 查询用户表中用户记录的数量。
 */
public class Test02_04 {
	public static void main(String[] args) throws SQLException {
		// 创建无参QueryRunner对象
		QueryRunner qr = new QueryRunner();

		// 准备sql语句 查询用户表全部记录
		String sql = "select COUNT(*) from users;";

		// 使用工具类获取连接对象，创建ScalarHandler对象统计用户数量，执行sql语句
		Connection conn = DBCPUtils.getConnection();
		long count = qr.query(conn, sql, new ScalarHandler<Long>());
		System.out.println("用户数量是：" + count);
		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}
}
```

#### 2.4.5. 封装多行单列数据 (ColumnListHandler)

```java
List<T> ColumnListHandler<T>();
```

只封装一列的时候，将这一列的数据封装成 `List` 集合，集合中的元素类型与列的类型相同。其中 `new ColumnListHandler<String>("列名")`。通常用于多行单列的查询结果集。

如果查询多列的话，只默认返回第一列

Code Demo:

```java
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import jdbc.C3P0Utils;

/*
 * 关卡2训练案例5
 	* 1.定义一个方法，查询用户表，获得所有用户的名字存放到集合中。(ColumnListHandler)
*/
public class Test02_05 {
	public static void main(String[] args) throws SQLException {
		// 创建QueryRunner无参对象
		QueryRunner qr = new QueryRunner();

		// 准备sql语句
		// 查询用户表中所有用户名
		String sql = "select name from users;";

		// 使用工具类获取连接对象，创建ColumnListHandler，获取所有用户名集合
		Connection conn = C3P0Utils.getConnection();
		List<String> list = qr.query(conn, sql, new ColumnListHandler<String>());

		// 遍历集合
		for (String s : list) {
			System.out.println(s);
		}

		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}
}
```

#### 2.4.6. KeyedHandler

```java
Map<String, Map<String, Object>> KeyedHandler<K>(String s);
```

将多条记录封装成一个 `Map`，取其中的一列做为键，记录本身做为值，这个值是 `Map` 集合，封装这一条记录。即：`Map<某列类型,Map<字段名,字段值>>`，其中 `KeyedHandler` 指定为`<某列的类型>(列名)`。

不指定，默认以第1列的值做为键。一般指定唯一的值的列做为列

示例：

```java
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.KeyedHandler;

import jdbc.C3P0Utils;

/*
 * 关卡2训练案例5
 	* 2.定义一个方法，查询用户表，获得所有用户的所有信息，返回一个 Map 集合(Map 集合的
 		* key 是用户 id，value 是每一个用户的信息，也是一个 Map 集合。)
 */
public class Test02_05 {
	public static void main(String[] args) throws SQLException {
		// 创建QueryRunner无参对象
		QueryRunner qr = new QueryRunner();

		// 准备sql语句
		// 查询用户表中所有用户,封装成一个Map集合
		String sql = "select * from users;";

		// 使用工具类获取连接对象，创建KeyedHandler，获取所有用户Map集合
		Connection conn = C3P0Utils.getConnection();
		Map<Integer, Map<String, Object>> map = qr.query(conn, sql, new KeyedHandler<Integer>("id"));

		// 遍历Map集合
		Set<Integer> set = map.keySet();
		for (Integer key : set) {
			System.out.println(key + " = " + map.get(key));
		}

		// 使用DbUtils工具类关闭资源
		DbUtils.closeQuietly(conn);
	}
}
```

## 3. DbUtils 事务操作

### 3.1. DbUtils 事务处理方式

**自动提交**：每条SQL语句执行后自动提交事务。无法通过回滚撤消操作。

**手动提交**：

1. 创建 `Connection` 对象和 `QueryRunner` 对象，`QueryRunner` 对象不能使用数据源。
2. 先调用 `conn.setAutoCommit(false);` 开启事务，取消自动提交。
3. 在 SQL 执行完后调用 `commitAndCloseQuietly(conn);` 提交事务，如果出现异常则调用 `rollbackAndCloseQuietly(conn);` 回滚事务。

### 3.2. 与事务处理相关的方法（待修改完善）

```java
conn.setAutoCommit(false);
```

- 禁止自动提交事务，开启事务

```java
new QueryRunner();
```

- 创建核心类，不传数据源(手动管理连接)

```java
query(conn , sql , handler, params);
update(conn, sql , params);

```
- 手动传递连接执行查询或更新的操作

```java
DbUtils.commitAndClose(conn);
DbUtils.commitAndCloseQuietly(conn);
```

- 提交并关闭连接

```java
DbUtils.rollbackAndClose(conn);
DbUtils.rollbackAndCloseQuietly(conn);
```

- 回滚并关闭连接


# Commons Collections

是一个集合组件，扩展了 Java 标准 Collections API，对常用的集合操作进行了很好的封装、抽象和补充，在保证性能的同时大大简化代码。

相关组件结构

```
org.apache.commons.collections – CommonsCollections自定义的一组公用的接口和工具类

org.apache.commons.collections.bag – 实现Bag接口的一组类

org.apache.commons.collections.bidimap – 实现BidiMap系列接口的一组类

org.apache.commons.collections.buffer – 实现Buffer接口的一组类

org.apache.commons.collections.collection –实现java.util.Collection接口的一组类

org.apache.commons.collections.comparators– 实现java.util.Comparator接口的一组类

org.apache.commons.collections.functors –Commons Collections自定义的一组功能类

org.apache.commons.collections.iterators – 实现java.util.Iterator接口的一组类

org.apache.commons.collections.keyvalue – 实现集合和键/值映射相关的一组类

org.apache.commons.collections.list – 实现java.util.List接口的一组类

org.apache.commons.collections.map – 实现Map系列接口的一组类

org.apache.commons.collections.set – 实现Set系列接口的一组类
```

- Bag、Buffer、BidiMap、OrderedMap 等，可以作为容器类的补充
- CollectionUtils、IteratorUtils、ListUtils、SetUtils 等，可以作为操作类的补充
- MapIterator、Closure、Predicate、Transformer 等，可以作为辅助类的补充

# Commons Email

是邮件操作组件，该组件依赖 Java Mail API，并对其进行了封装，提供了常用的邮件发送和接收类，简化邮件操作。

# Commons FileUpload

为 Web 应用程序或 Servlet 提供文件上传功能，Struts2 和 SpringMVC 的文件上传组件。

# Commons Logging

提供统一的日志接口，同时兼顾轻量级和不依赖于具体的实现。类包给中间件/日志工具开发者一个简单的日志操作抽象，允许程序开发人员使用不同的具体日志实现工具。
