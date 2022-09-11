# Apache Commons IO

## 1. 简介

Apache Commons IO是第三方IO操作工具类库

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
    <version>2.8.0</version>
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

