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

- 将一个 JavaBean 中所有的属性转成 Map 集合，并且多出一个 class 属性键值对。如操作后，类中会多出一个属性 `class = class com.itheima.entity.User`

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
