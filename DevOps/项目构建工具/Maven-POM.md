> POM 官方参考文档：https://maven.apache.org/pom.html

## 1. POM 的四个层次

### 1.1. 超级 POM

Maven 在构建过程中有很多默认的设定。例如：源文件存放的目录、测试源文件存放的目录、构建输出的目录等等。这些配置被 maven 定义在“**超级 POM**” 文件中。

> 官网关于超级 POM 的介绍如下：
>
> The Super POM is Maven's default POM. All POMs extend the Super POM unless explicitly set, meaning the configuration specified in the Super POM is inherited by the POMs you created for your projects.
>
> 译文：Super POM 是 Maven 的默认 POM。除非明确设置，否则所有 POM 都扩展 Super POM，这意味着 Super POM 中指定的配置由您为项目创建的 POM 继承。

### 1.2. 父 POM

和 Java 类一样，POM 之间其实也是单继承的。如果给一个 POM 指定了父 POM，那么继承关系如下图所示：

![](images/103965022227449)

### 1.3. 有效 POM

#### 1.3.1. 概念

在 POM 的继承关系中，子 POM 可以覆盖父 POM 中的配置；如果子 POM 没有覆盖，那么父 POM 中的配置将会被继承。按照这个规则，继承关系中的所有 POM 叠加到一起，就得到了一个最终生效的 POM。

Maven 执行构建操作就是按照这个最终生效的 POM 来运行的，此最终生效的 POM 就叫做**有效 POM (英文：effective POM)**

#### 1.3.2. 查看有效 POM

通过以下命令可以查看 maven 项目的 有效 pom

```bash
mvn help:effective-pom
```

### 1.4. 小结

使用和配置的 POM 分成四个层次组成的：

1. **超级 POM**：所有 POM 默认继承，只是有直接和间接之分。
2. **父级 POM**：主要看是否继承其他自定义的 pom，可以有一层，也可能有很多层。
3. **当前 POM**：即平时关注和最多使用的 pom.xml
4. **有效 POM**：隐含的一层，最终项目运行时实际真正生效的一层。

## 2. pom.xml 整体配置结构

最基础的 pom.xml 文件结构格式是，由 `<project>` 和 `<modelVersion>` 标签构成，其中 `<modelVersion>` 指定了 pom.xml 文件使用的 XML schema 版本，目前，其最新的版本是 4.0.0

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>example-proj</artifactId>
    <version>1.0.0</version>

    <name>Example Project</name>
    <description>This is an example Maven project.</description>
    <url>http://www.example.com/</url>

    <licenses>   <!-- 许可证 -->
        <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
        </license>
    </licenses>

    <developers>     <!-- 开发者信息 -->
        <developer>
            <id>developer1</id>
            <name>Developer One</name>
            <email>developer1@example.com</email>
            <organization>Example Organizations Inc.</organization>
            <organizationUrl>http://www.example-organizations.com/</organizationUrl>
            <roles>
                <role>developer</role>
            </roles>
            <timezone>-5</timezone>
        </developer>
    </developers>

    <dependencies>  <!-- 依赖项 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>5.2.1.RELEASE</version>
        </dependency>
    </dependencies>

    <build>   <!-- 项目构建 -->
        <plugins>   <!-- 插件配置 -->
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.example.App</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 3. 项目的基本信息相关标签

与项目的基本信息相关的标签，其中必填项如下：

- `<groupId>`：项目的组名，通常是反转的域名，比如 com.example。
- `<artifactId>`：项目的唯一标识符，通常是项目的名称。
- `<version>`：项目的版本号。
- `<packaging>`：项目的打包方式，通常是 jar、war 或 pom，如果没有指定 packaging，默认值是 jar。

除了上面的必须的标签，还有一些项目相关可选标签：

- `<name>`：项目名，可选项，提供项目的简短名称
- `<description>`：项目描述，可选项，提供项目的详细描述。
- `<version>`：项目主页，可选项，提供项目的网址。
- `<licenses>`： 许可证声明，可选项，声明项目所使用的一种或多种许可证
- `<developers>`：开发者信息，可选项，列出项目的开发人员。
- `<url>`：项目主页，可选项，提供项目的网址

另外还有一些特殊标签。

### 3.1. modules 标签

`<modules>` 标签用于声明当前 Maven 项目包含的模块子项目，每个子项目都是一个独立的 Maven 项目，具有自己的 pom.xml 文件，可以进行独立构建和测试。在父项目的 pom.xml 文件中，使用 标签来列出所有子项目的名称，如下所示：

```xml
<project>
    <groupId>com.example.parent</groupId>
    <artifactId>parent-project</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>child-project1</module>
        <module>child-project2</module>
        <module>child-project3</module>
    </modules>
</project>
```

示例配置表示当前项目是一个 Maven 的多模块项目：它包含了三个子项目 child-project1、child-project2 和 child-project3，这三个子项目与 parent-project 有相同的 groupId 和 version，但是 artifactId 不同，它们的 pom.xml 都位于 parent-project 的根目录下。当使用 Maven 命令在 parent-project 下执行构建时，Maven 会对每个子模块执行构建，最终生成子项目的构件并复制到 parent-project 的 target 目录下。

### 3.2. parent 标签

`<parent>` 标签用于声明当前 Maven 项目的父项目，它可以将若干个 Maven 项目组织成一个整体，指定版本号，插件版本号等，便于管理和维护，在一个 Maven 项目中，使用标签来引用父项目，如下所示：

```xml
<project>
    <groupId>com.example.child</groupId>
    <artifactId>child-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.example.parent</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0.0</version>
        <relativePath>../parent-project/pom.xml</relativePath>
    </parent>
</project>
```

示例配置表示当前项目 child-project 是 parent-project 的子项目，它的 groupId 和 version 都继承自 parent-project。`<relativePath>` 标签是一个可选项，它的值是父项目 pom.xml 文件到子项目 pom.xml 文件的相对路径，如果子项目 pom.xml 和父项目 pom.xml 在同一目录下，则可以省略此标签。

### 3.3. scm 标签

scm 又叫 Software Configuration Management，即软件配置管理，与版本控制有关，是 Maven 中用于指定源代码版本控制信息的标签。它可以帮助 Maven 获取源代码并将构建生成的二进制文件提交到版本控制系统中。`<scm>` 标签通常用于指定源代码管理系统的类型、URL、开发者连接等详细信息。示例如下：

```xml
<scm>
    <connection>scm:git:git://github.com/username/repo.git</connection>  <!-- 指定连接到SCM的URL，可以使用HTTP或者SSH协议 -->
    <!-- 指定开发者连接到SCM的URL，通常使用SSH协议 -->
    <developerConnection>
        scm:git:ssh://github.com/username/repo.git
    </developerConnection>
    <url>http://github.com/username/repo/tree/master</url> <!-- 指定SCM的web页面URL，方便开发者查看SCM信息 -->
    <tag>1.0.0</tag> <!-- 当前Maven构件的版本在SCM中对应的标记 -->
</scm>
```

`<developerConnection>` 与 `<connection>` 有些区别

- `<connection>` 标签可以指向一个本地文件系统路径，也可以是一个远程代码仓库的URL。
- `<developerConnection>` 标签则是开发者使用的版本控制系统的连接 URL。例如 `<connection>` 指向只读代码仓库，而 `<developerConnection>` 则指向可写代码仓库。

通过在 POM 文件中添加 `<scm>` 标签，Maven 可以获取源代码并构建项目，同时还可以自动将构建生成的文件提交到版本控制系统中，方便管理代码版本和协同开发。

### 3.4. properties 标签

`<properties>` 标签严格来说，配置的并不一定是项目本身的信息，而是人为设置的属性或者说宏，这个标签用来定义和管理项目中所需要的属性，其主要作用如下：

- 统一管理项目中的常用属性，比如版本号、路径、插件版本等，方便统一修改和管理。
- 可以在配置过程中使用 `${…}` 占位符引用这些属性，使得配置更加灵活和便捷。
- 避免硬编码，提高代码的可维护性和可读性

```xml
<properties>
    <project.name>demo-project</project.name>
    <project.version>1.0.0</project.version>
    <jdk.version>1.8</jdk.version>
</properties>

<!-- 通过 ${xxx} 使用 -->
<dependency>
    <groupId>com.example.demo</groupId>
    <artifactId>${project.name}-api</artifactId>
    <version>${project.version}</version>
</dependency>
```

## 4. 项目的依赖列表相关标签

### 4.1. dependency 标签

与项目的依赖列表相关标签最外层由 `<dependencies>` 来包裹，内部包含了各种具体的依赖  `<dependency>` 标签，该标签用于指定一个依赖项，它包含以下几个子标签：

- `<groupId>`：指定依赖项的 groupId，项目的组名
- `<artifactId>`：指定依赖项的 artifactId，项目的唯一标识符
- `<version>`：指定依赖项的版本号。
- `<scope>`：指定依赖项在项目中的使用范围。包含如下几种可选范围值：
    - `compile`：依赖库默认的值，表示该依赖库在编译、测试、运行时均需要使用。
    - `provided`：表示该依赖库只在编译和测试时需要使用，而在运行时已经被系统或者容器提供，所以不需要打包到最终的应用程序中
    - `runtime`：表示该依赖库只在运行时需要使用，而在编译和测试时则不需要。
    - `test`：表示该依赖库只在测试时需要使用，而在编译和运行时则不需要。

例如引入了 junit 包，该包只是用于测试，不需要在打包时包含，那么就可以将 junit 的 `<scope>` 设置为 `test`。

### 4.2. repository 标签

在本项目的 pom 文件中，支持使用 `<repositories>` 和 `<repository>` 标签指定 Maven 仓库，`<repository>` 用于指定一个 Maven 仓库，它包含以下几个子标签：

- `<id>`：指定 Maven 仓库的 ID。
- `<name>`：指定 Maven 仓库的名称。
- `<url>`：指定 Maven 仓库的 URL

## 5. 项目的构建配置相关标签

### 5.1. build 标签

`<build>` 标签用于定制构建过程规则，在超级 POM 中有相关默认配置，也可以在具体项目中的 pom.xml 中配置 `<build>` 标签覆盖默认值或补充配置。The Super POM 默认配置如下：

```xml
<build>
    <directory>${project.basedir}/target</directory>
    <outputDirectory>${project.build.directory}/classes</outputDirectory>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <testOutputDirectory>${project.build.directory}/test-classes</testOutputDirectory>
    <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
    <scriptSourceDirectory>${project.basedir}/src/main/scripts</scriptSourceDirectory>
    <testSourceDirectory>${project.basedir}/src/test/java</testSourceDirectory>
    <resources>
      <resource>
        <directory>${project.basedir}/src/main/resources</directory>
      </resource>
    </resources>
    <testResources>
      <testResource>
        <directory>${project.basedir}/src/test/resources</directory>
      </testResource>
    </testResources>
    <pluginManagement>
      <!-- NOTE: These plugins will be removed from future versions of the super POM -->
      <!-- They are kept for the moment as they are very unlikely to conflict with lifecycle mappings (MNG-4453) -->
      <plugins>
        <plugin>
          <artifactId>maven-antrun-plugin</artifactId>
          <version>1.3</version>
        </plugin>
        <plugin>
          <artifactId>maven-assembly-plugin</artifactId>
          <version>2.2-beta-5</version>
        </plugin>
        <plugin>
          <artifactId>maven-dependency-plugin</artifactId>
          <version>2.8</version>
        </plugin>
        <plugin>
          <artifactId>maven-release-plugin</artifactId>
          <version>2.5.3</version>
        </plugin>
      </plugins>
    </pluginManagement>
</build>
```

> Notes: 从本质上来说，在项目中的 pom.xml 中配置 `<build>` 标签都是对超级 POM 配置的叠加。

#### 5.1.1. 定义项目的目录结构

用于指定目录的子标签如下：

|           标签名           |          作用           |
| ------------------------- | ---------------------- |
| `<sourceDirectory>`       | 主体源程序存放目录        |
| `<scriptSourceDirectory>` | 脚本源程序存放目录        |
| `<testSourceDirectory>`   | 测试源程序存放目录        |
| `<outputDirectory>`       | 主体源程序编译结果输出目录 |
| `<testOutputDirectory>`   | 测试源程序编译结果输出目录 |
| `<resources>`             | 主体资源文件存放目录      |
| `<testResources>`         | 测试资源文件存放目录      |
| `<directory>`             | 构建结果输出目录          |

#### 5.1.2. 插件管理

通过 `<pluginManagement>` 子标签管理插件，用于在父工程中统一管理版本，子工程使用时可以省略版本号。（*类似于 `<dependencyManagement>` 标签*）

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.6.2</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

在子工程中使用插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

#### 5.1.3. 插件生命周期管理

`<plugins>` 标签存放在默认生命周期中实际用到的插件，插件坐标与依赖 jar 包的坐标一样，包含 `<artifactId>`、`<version>` 和 `<groupId>` 标签，作为 Maven 的自带插件可以省略 `<groupId>` 标签

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.1</version>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>default-testCompile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>testCompile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

`<executions>` 标签内可以配置多个 `<execution>` 标签，`<execution>` 标签用于指定构建生命周期中执行一组目标，包含以下子标签：

- `<id>`：指定唯一标识
- `<phase>`：指定关联的生命周期阶段。如果省略，目标会被绑定到源数据里配置的默认阶段
- `<goals>`：配置生命周期执行的一（多）个目标
    - `<goal>` 子标签，表示一个生命周期环节可以对应当前插件的多个目标。

下面示例是 maven-site-plugin 插件的 site 目标：

```xml
<execution>
    <id>default-site</id>
    <phase>site</phase>
    <goals>
        <goal>site</goal>
    </goals>
    <configuration>
        <outputDirectory>D:\workspace\maven-quickstart\target\site</outputDirectory>
        <reportPlugins>
            <reportPlugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-project-info-reports-plugin</artifactId>
            </reportPlugin>
        </reportPlugins>
    </configuration>
</execution>
```

`<configuration>` 标签内配置的子标签，是由插件本身来定义的。以 maven-site-plugin 插件为例，它的核心类是 `org.apache.maven.plugins.site.render.SiteMojo`，在该类中可以找到 `outputDirectory` 属性：

![](images/589801622221158)

SiteMojo 的父类是：`AbstractSiteRenderingMojo`，在父类中可以看到 `reportPlugins` 属性：

![](images/268531722239584)

> Notes: <font color=red>**重要结论，插件可设置的内容均由该插件来定义。**</font>

#### 5.1.4. 指定 JDK 版本

maven-compiler-plugin 插件，可以通过 `<configuration>` 标签来指定当前工程编译时所使用的 JDK 版本

```java
<!-- build 标签：自定义构建的行为 -->
<build>
    <!-- plugins 标签：构建时用到的插件集 -->
    <plugins>
        <!-- plugin 标签：具体的插件 -->
        <plugin>
            <!-- 插件的坐标。此处引用的 maven-compiler-plugin 插件不是第三方的，是一个 Maven 自带的插件。 -->
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            
            <!-- configuration 标签：配置 maven-compiler-plugin 插件 -->
            <configuration>
                <!-- 具体配置信息会因为插件不同、需求不同而有所差异 -->
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**参数说明**：

- `<source>`：编写源代码时，编译器所使用的 JDK 版本
- `<target>`：源文件编译后，所生成的 class 字节码文件的 JDK 版本

以上功能还可以通过在 `<properties>` 标签中配置 `maven.compiler.source`与 `maven.compiler.target` 属性来实现：

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**两种指定 JDK 版本配置方式的比较**：

- 在 settings.xml 全局配置中设置 JDK 版本，仅在本地生效，如果脱离当前 settings.xml 能够覆盖的范围，则无法生效。（例如部署到服务器中）
- 在当前 Maven 工程 pom.xml 中配置 JDK 版本，则项目无论在哪个环境执行编译等构建操作均能生效

#### 5.1.5. 插件的依赖

使用 Mybatis 的逆向工程需要使用如下配置，MBG 插件需要配置该插件所需的依赖：

```xml
<!-- 控制 Maven 在构建过程中相关配置 -->
<build>	
	<!-- 构建过程中用到的插件 -->
	<plugins>
		<!-- 具体插件，逆向工程的操作是以构建过程中插件形式出现的 -->
		<plugin>
			<groupId>org.mybatis.generator</groupId>
			<artifactId>mybatis-generator-maven-plugin</artifactId>
			<version>1.3.0</version>
			<!-- 插件的依赖 -->
			<dependencies>
				<!-- 逆向工程的核心依赖 -->
				<dependency>
					<groupId>org.mybatis.generator</groupId>
					<artifactId>mybatis-generator-core</artifactId>
					<version>1.3.2</version>
				</dependency>
					
				<!-- 数据库连接池 -->
				<dependency>
					<groupId>com.mchange</groupId>
					<artifactId>c3p0</artifactId>
					<version>0.9.2</version>
				</dependency>
					
				<!-- MySQL驱动 -->
				<dependency>
					<groupId>mysql</groupId>
					<artifactId>mysql-connector-java</artifactId>
					<version>5.1.8</version>
				</dependency>
			</dependencies>
		</plugin>
	</plugins>
</build>
```

### 5.2. plugins 标签

#### 5.2.1. 概述

`<plugins>` 标签的作用是定义 Maven 插件，主要用于扩展 Maven 的功能，帮助开发人员更方便地构建、打包、发布项目。插件可以通过 Maven 的插件中心或者构建的私有仓库来使用，能在构建过程中执行特定的任务，比如编译、打包、测试等。

<font color=red>**插件的配置可以分为两种方式：全局配置和项目配置**</font>。

- 全局配置是在 Maven 安装目录下的 conf/settings.xml 文件中进行配置，可以被所有的项目使用。
- 项目配置则是在项目的 pom.xml 文件中进行配置，只对当前项目生效。

插件的使用主要分为以下几个步骤：

1. 在 pom.xml 中声明插件依赖
2. 配置插件的参数
3. 运行插件命令

#### 5.2.2. 基础配置

Maven 插件定义 `<build>` -> `<plugins>` 的子标签 `<plugin>` 中，需要指定 `<groupId>`、`<artifactId>`、`<version>` 等基础信息，相应插件的具体配置在 `<configuration>` 标签中指定。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-xxxx-plugin</artifactId>
            <version>x.y.z</version>
            <configuration>
                <xxx></xxx>
                ...
            </configuration>
        </plugin>
    </plugins>
</build>
```

> Tips: 以下章节的示例配置均省略 `<build>` 与 `<plugins>` 标签！

#### 5.2.3. maven-compiler-plugin

maven-compiler-plugin 是编译功能插件，在 pom.xml 中的配置如下：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
                <showWarnings>true</showWarnings>
                <compilerArgs>
                    <arg>-Xlint:unchecked</arg>
                    <arg>-Xlint:deprecation</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

插件配置各子标签的作用说明：

- `<source>`：指定Java源代码的版本，例如1.8表示Java 8。
- `<target>`：指定编译后的字节码版本，例如1.8表示Java 8。
- `<encoding>`：指定源代码的编码格式。
- `<showWarnings>`：是否显示编译警告信息，true表示显示，false表示不显示。
- `<compilerArgs>`：可选参数，可以添加多个编译器参数，例如-Xlint选项用来启用编译器警告检查。

如果按示例中配置，即指定了编译器的源和目标版本为 1.8，当使用 `mvn compile` 命令时，该插件将会编译 Java 代码，并将编译后的 class 文件放置在 target 目录下。

#### 5.2.4. maven-surefire-plugin

maven-surefire-plugin 插件是 Maven 中的一个测试框架，用于执行 Java 单元测试和集成测试。主要作用是在构建过程中运行测试，并生成测试报告，在 pom.xml 中的配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M4</version>
    <configuration>
        <skipTests>true</skipTests>
        <forkCount>2</forkCount>
        <useSystemClassLoader>false</useSystemClassLoader>
        <reuseForks>true</reuseForks>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <systemProperties>
            <property>
                <name>testProp1</name>
                <value>value1</value>
            </property>
            <property>
                <name>testProp2</name>
                <value>value2</value>
            </property>
        </systemProperties>
    </configuration>
</plugin>
```

插件配置各子标签的作用说明：

- `<skipTests>`：设置是否跳过测试，默认值为 false。
- `<forkCount>`：设置并行运行测试的 JVM 进程数。
- `<useSystemClassLoader>`：设置是否使用系统类加载器加载测试类。
- `<reuseForks>`：设置是否重用已经启动的 JVM 进程。
- `<includes>`：设置测试文件的过滤规则，支持通配符。
- `<systemProperties>`：设置传递给测试环境的系统属性，可以在测试代码中通过 `System.getProperty()` 方法获取

#### 5.2.5. maven-jar-plugin

maven-jar-plugin 用于将项目打包为 JAR 文件，以下示例 pom.xml 中的配置了 Maven 将 com.example.MyApp 作为 JAR 文件的主类：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>com.example.MyApp</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

插件配置各子标签的作用说明：

- `<archive>`：JAR 文件的归档配置信息
- `<manifest>`：MANIFEST.MF 文件的配置信息
- `<addClasspath>`：是否将依赖项添加到 Class-Path 条目中
- `<mainClass>`：定义可执行 JAR 文件的入口类

#### 5.2.6. maven-install-plugin

当执行 `mvn install` 命令时，maven-install-plugin 用于将一个特定的文件安装到本地 Maven 仓库中，以便其他项目可以依赖和使用它，在 pom.xml中 的配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-install-plugin</artifactId>
    <version>3.0.0-M1</version>
    <configuration>
        <file>${project.build.directory}/example.jar</file>
        <groupId>com.example</groupId>
        <artifactId>example</artifactId>
        <version>1.0.0</version>
        <packaging>jar</packaging>
    </configuration>
</plugin>
```

插件配置各子标签的作用说明：

- `<file>`：用来指定要安装到本地 Maven 仓库中的文件的路径。该标签的值应该是一个文件的绝对或相对路径。需要注意的是，`<file>` 标签必须与 `<groupId>`、`<artifactId>` 和 `<version>` 标签一起使用，才能正确将该文件安装到本地 Maven 仓库中，并在其他项目中使用。
- `<groupId>`：通过该标签设置所安装文件的 groupId，通常表示项目的组织或组织部门的标识符。
- `<artifactId>`：同样是通过该标签设置所安装文件的 artifactId，通常是指该文件的名称。
- `<version>`：通过该标签设置所安装文件的版本号，通常采用三级版本号的格式，例如 "1.0.0"。
- `<packaging>`：通过该标签来指定所安装文件的打包类型，通常是 jar 或 war。
- `<classifier>`（可选配置项）：通过该标签指定所安装文件的分类器，例如"sources"或"javadoc"等，默认为null。
- `<localRepositoryPath>`（可选配置项）：通过该标签指定本地仓库的路径，默认为Maven默认的本地仓库路径。
- `<createChecksum>`（可选配置项）：是否在安装文件时创建 SHA-1 校验和，默认为true。
- `<skip>`（可选配置项）：是否跳过该插件的运行，默认为 false，即不跳过。

#### 5.2.7. maven-clean-plugin

maven-clean-plugin 用于清理 Maven 项目中的目标文件和构建临时文件，以便重新构建项目。它通常被用于在构建之前清理项目，以确保在构建时使用最新的代码和资源 jar 文件，在 pom.xml 中的配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-clean-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>clean-all</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
            <configuration>
                <excludeDefaultDirectories>true</excludeDefaultDirectories>
                <filesets>
                    <fileset>
                        <directory>target</directory>
                        <includes>
                            <include>**/*</include>
                        </includes>
                    </fileset>
                </filesets>
            </configuration>
        </execution>
    </executions>
</plugin>
```

上面示例配置中，maven-clean-plugin 的版本号是 3.1.0，它在 clean 阶段（`<phase>` 标签指定）执行，使用的目标是 clean。插件配置各子标签的作用说明：

- `<excludeDefaultDirectories>`：默认值为false，如果设置为true，则禁用清理操作中默认清理的目录（如target、bin等）。
- `<filesets>`：文件集合，可以指定多个文件或文件夹需要被清理。
- `<fileset>`：单个的文件或文件夹。
- `<directory>`：需要清理的文件夹路径。
- `<includes>`：需要包含的文件或文件夹，支持通配符。
- `<excludes>`：需要排除的文件或文件夹，支持通配符

通配符使用规则如下：

- `*`  匹配零个或多个字符
- `**` 匹配零个或多个目录

<font color=red>**需要注意的是，Maven 的通配符仅支持`*`和`**`，不支持其他通配符（例如`?`）。同时，通配符匹配的范围是相对于构建目录的，即默认情况下是相对于 pom.xml 文件的目录**</font>。

#### 5.2.8. maven-release-plugin

maven-release-plugin 用于在代码库中创建一个稳定的发布版本，并将其发布到 Maven 仓库中，同时更新开发版本号，以便于下次开发版本的迭代。在 pom.xml 中的配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>2.5.2</version>
    <configuration>
        <tagNameFormat>v@{project.version}</tagNameFormat>
        <tagBase>http://svn.example.com/tags</tagBase>
        <autoVersionSubmodules>true</autoVersionSubmodules>
        <releaseProfiles>release</releaseProfiles>
        <branchBase>http://svn.example.com/branches</branchBase>
    </configuration>
</plugin>
```

插件配置各子标签的作用说明：

- `<tagNameFormat>`：指定发布版本的标签格式，`@{project.version}` 会被替换为项目的版本号。在示例配置中，标签格式为`v@{project.version}`。
- `<tagBase>`：用于指定打标签的位置，默认值为 `${project.scm.url}`，即和项目的 SCM 地址相同。
- `<autoVersionSubmodules>`：是否自动更新子模块的版本号。如果设置为 true，则子模块的版本号会自动更新为父模块的版本号。
- `<releaseProfiles>`：指定触发发布的 Maven profile。只有在激活该 profile 后才会触发发布操作。在示例配置中，只有当 profile 名称为 release 时，才会触发发布操作。
- `<branchBase>`：用于指定创建分支的位置，默认值同 tagBase，即和项目的 SCM 地址相同。

### 5.3. profiles 标签

`<profiles>` 用于定义 Maven 运行时的不同配置环境，比如开发环境、测试环境、生产环境等。比如需要在不同的环境中使用不同的配置，在 pom 文件中进行如下配置：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                    <configuration>
                        <release>11</release>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
    <profile>
        <id>prod</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                    <configuration>
                        <release>11</release>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

各子标签的作用说明：

- `<id>`：指定 profile 的唯一标识符。
- `<activation>`：指定何时使用该 profile。在示例中，`<activeByDefault>` 设置为 true 表示默认启用该 profile。
- `<build>`：包含一组构建配置，这些配置将在激活 profile 时覆盖默认配置。在示例中，它定义了 maven-compiler-plugin 插件的版本和为 Java 11 设置编译器版本。

在 Maven 中，可以使用以下命令激活特定的 profile，例如激活 prod profile，覆盖默认构建配置：

```bash
mvn clean install -Pprod
```

## 6. POM 文件帮助文档(网上资源)

### 6.1. 附件1：POM文件总体配置说明

> 引用网上资源的最全面版本 pom.xml，后期根据理解修改整理

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd "> 

    <!-- 父项目的坐标。如果项目中没有规定某个元素的值，那么父项目中的对应值即为项目的默认值。
         坐标包括group ID，artifact ID和 version。 --> 
    <parent> 
        <!-- 被继承的父项目的构件标识符 --> 
        <artifactId>xxx</artifactId>

        <!-- 被继承的父项目的全球唯一标识符 -->
        <groupId>xxx</groupId> 

        <!-- 被继承的父项目的版本 --> 
        <version>xxx</version>

        <!-- 父项目的pom.xml文件的相对路径。相对路径允许你选择一个不同的路径。默认值是../pom.xml。
             Maven首先在构建当前项目的地方寻找父项目的pom，其次在文件系统的这个位置（relativePath位置），
             然后在本地仓库，最后在远程仓库寻找父项目的pom。 --> 
        <relativePath>xxx</relativePath> 
    </parent> 

    <!-- 声明项目描述符遵循哪一个POM模型版本。模型本身的版本很少改变，虽然如此，但它仍然是必不可少的，
         这是为了当Maven引入了新的特性或者其他模型变更的时候，确保稳定性。 --> 
    <modelVersion> 4.0.0 </modelVersion> 

    <!-- 项目的全球唯一标识符，通常使用全限定的包名区分该项目和其他项目。并且构建时生成的路径也是由此生成， 
         如com.mycompany.app生成的相对路径为：/com/mycompany/app --> 
    <groupId>xxx</groupId> 

    <!-- 构件的标识符，它和group ID一起唯一标识一个构件。换句话说，你不能有两个不同的项目拥有同样的artifact ID
         和groupID；在某个特定的group ID下，artifact ID也必须是唯一的。构件是项目产生的或使用的一个东西，Maven
         为项目产生的构件包括：JARs，源码，二进制发布和WARs等。 --> 
    <artifactId>xxx</artifactId> 

    <!-- 项目产生的构件类型，例如jar、war、ear、pom。插件可以创建他们自己的构件类型，所以前面列的不是全部构件类型 --> 
    <packaging> jar </packaging> 

    <!-- 项目当前版本，格式为:主版本.次版本.增量版本-限定版本号 --> 
    <version> 1.0-SNAPSHOT </version> 

    <!-- 项目的名称, Maven产生的文档用 --> 
    <name> xxx-maven </name> 

    <!-- 项目主页的URL, Maven产生的文档用 --> 
    <url> http://maven.apache.org </url> 

    <!-- 项目的详细描述, Maven 产生的文档用。 当这个元素能够用HTML格式描述时（例如，CDATA中的文本会被解析器忽略，
         就可以包含HTML标签）， 不鼓励使用纯文本描述。如果你需要修改产生的web站点的索引页面，你应该修改你自己的
         索引页文件，而不是调整这里的文档。 --> 
    <description> A maven project to study maven. </description> 

    <!-- 描述了这个项目构建环境中的前提条件。 --> 
    <prerequisites> 
        <!-- 构建该项目或使用该插件所需要的Maven的最低版本 --> 
        <maven></maven> 
    </prerequisites> 

    <!-- 项目的问题管理系统(Bugzilla, Jira, Scarab,或任何你喜欢的问题管理系统)的名称和URL，本例为 jira --> 
    <issueManagement> 
        <!-- 问题管理系统（例如jira）的名字， --> 
        <system> jira </system> 

        <!-- 该项目使用的问题管理系统的URL --> 
        <url> http://jira.baidu.com/banseon </url> 
    </issueManagement> 

    <!-- 项目持续集成信息 --> 
    <ciManagement> 
        <!-- 持续集成系统的名字，例如continuum --> 
        <system></system> 

        <!-- 该项目使用的持续集成系统的URL（如果持续集成系统有web接口的话）。 --> 
        <url></url> 

        <!-- 构建完成时，需要通知的开发者/用户的配置项。包括被通知者信息和通知条件（错误，失败，成功，警告） --> 
        <notifiers> 
            <!-- 配置一种方式，当构建中断时，以该方式通知用户/开发者 --> 
            <notifier> 
                <!-- 传送通知的途径 --> 
                <type></type> 

                <!-- 发生错误时是否通知 --> 
                <sendOnError></sendOnError> 

                <!-- 构建失败时是否通知 --> 
                <sendOnFailure></sendOnFailure> 

                <!-- 构建成功时是否通知 --> 
                <sendOnSuccess></sendOnSuccess> 

                <!-- 发生警告时是否通知 --> 
                <sendOnWarning></sendOnWarning> 

                <!-- 不赞成使用。通知发送到哪里 --> 
                <address></address> 

                <!-- 扩展配置项 --> 
                <configuration></configuration> 
            </notifier> 
        </notifiers> 
    </ciManagement> 

    <!-- 项目创建年份，4位数字。当产生版权信息时需要使用这个值。 --> 
    <inceptionYear /> 

    <!-- 项目相关邮件列表信息 --> 
    <mailingLists> 
        <!-- 该元素描述了项目相关的所有邮件列表。自动产生的网站引用这些信息。 --> 
        <mailingList> 
            <!-- 邮件的名称 --> 
            <name> Demo </name> 

            <!-- 发送邮件的地址或链接，如果是邮件地址，创建文档时，mailto: 链接会被自动创建 --> 
            <post> banseon@126.com </post> 

            <!-- 订阅邮件的地址或链接，如果是邮件地址，创建文档时，mailto: 链接会被自动创建 --> 
            <subscribe> banseon@126.com </subscribe> 

            <!-- 取消订阅邮件的地址或链接，如果是邮件地址，创建文档时，mailto: 链接会被自动创建 --> 
            <unsubscribe> banseon@126.com </unsubscribe> 

            <!-- 你可以浏览邮件信息的URL --> 
            <archive> http:/hi.baidu.com/banseon/demo/dev/ </archive> 
        </mailingList> 
    </mailingLists> 

    <!-- 项目开发者列表 --> 
    <developers> 
        <!-- 某个项目开发者的信息 --> 
        <developer> 
            <!-- SCM里项目开发者的唯一标识符 --> 
            <id> HELLO WORLD </id>

            <!-- 项目开发者的全名 --> 
            <name> banseon </name> 

            <!-- 项目开发者的email --> 
            <email> banseon@126.com </email> 

            <!-- 项目开发者的主页的URL --> 
            <url></url> 

            <!-- 项目开发者在项目中扮演的角色，角色元素描述了各种角色 --> 
            <roles> 
                <role> Project Manager </role> 
                <role> Architect </role> 
            </roles> 

            <!-- 项目开发者所属组织 --> 
            <organization> demo </organization> 

            <!-- 项目开发者所属组织的URL --> 
            <organizationUrl> http://hi.baidu.com/xxx </organizationUrl> 

            <!-- 项目开发者属性，如即时消息如何处理等 --> 
            <properties> 
                <dept> No </dept> 
            </properties> 

            <!-- 项目开发者所在时区， -11到12范围内的整数。 --> 
            <timezone> -5 </timezone> 
        </developer> 
    </developers> 

    <!-- 项目的其他贡献者列表 --> 
    <contributors> 
        <!-- 项目的其他贡献者。参见developers/developer元素 --> 
        <contributor> 
            <!-- 项目贡献者的全名 --> 
            <name></name>

            <!-- 项目贡献者的email -->
            <email></email>

            <!-- 项目贡献者的主页的URL -->
            <url></url>

            <!-- 项目贡献者所属组织 -->
            <organization></organization>

            <!-- 项目贡献者所属组织的URL -->
            <organizationUrl></organizationUrl>

            <!-- 项目贡献者在项目中扮演的角色，角色元素描述了各种角色 --> 
            <roles> 
                <role> Project Manager </role> 
                <role> Architect </role> 
            </roles>

            <!-- 项目贡献者所在时区， -11到12范围内的整数。 --> 
            <timezone></timezone>

            <!-- 项目贡献者属性，如即时消息如何处理等 --> 
            <properties> 
                <dept> No </dept> 
            </properties>  
        </contributor> 
    </contributors> 

    <!-- 该元素描述了项目所有License列表。 应该只列出该项目的license列表，不要列出依赖项目的 license列表。
         如果列出多个license，用户可以选择它们中的一个而不是接受所有license。 --> 
    <licenses> 
        <!-- 描述了项目的license，用于生成项目的web站点的license页面，其他一些报表和validation也会用到该元素。 --> 
        <license> 
            <!-- license用于法律上的名称 --> 
            <name> Apache 2 </name> 

            <!-- 官方的license正文页面的URL --> 
            <url> http://www.baidu.com/banseon/LICENSE-2.0.txt </url> 

            <!-- 项目分发的主要方式： 
                    repo，可以从Maven库下载 
                    manual， 用户必须手动下载和安装依赖 --> 
            <distribution> repo </distribution> 

            <!-- 关于license的补充信息 --> 
            <comments> A business-friendly OSS license </comments> 
        </license> 
    </licenses> 

    <!-- SCM(Source Control Management)标签允许你配置你的代码库，供Maven web站点和其它插件使用。 --> 
    <scm> 
        <!-- SCM的URL,该URL描述了版本库和如何连接到版本库。欲知详情，请看SCMs提供的URL格式和列表。该连接只读。 --> 
        <connection> 
            scm:svn:http://svn.baidu.com/banseon/maven/banseon/banseon-maven2-trunk(dao-trunk)
        </connection> 

        <!-- 给开发者使用的，类似connection元素。即该连接不仅仅只读 --> 
        <developerConnection> 
            scm:svn:http://svn.baidu.com/banseon/maven/banseon/dao-trunk 
        </developerConnection> 

        <!-- 当前代码的标签，在开发阶段默认为HEAD --> 
        <tag></tag> 

        <!-- 指向项目的可浏览SCM库（例如ViewVC或者Fisheye）的URL。 --> 
        <url> http://svn.baidu.com/banseon </url> 
    </scm> 

    <!-- 描述项目所属组织的各种属性。Maven产生的文档用 --> 
    <organization> 
        <!-- 组织的全名 --> 
        <name> demo </name> 

        <!-- 组织主页的URL --> 
        <url> http://www.baidu.com/banseon </url> 
    </organization> 

    <!-- 构建项目需要的信息 --> 
    <build> 
        <!-- 该元素设置了项目源码目录，当构建项目的时候，构建系统会编译目录里的源码。该路径是相对
             于pom.xml的相对路径。 --> 
        <sourceDirectory></sourceDirectory> 

        <!-- 该元素设置了项目脚本源码目录，该目录和源码目录不同：绝大多数情况下，该目录下的内容会
             被拷贝到输出目录(因为脚本是被解释的，而不是被编译的)。 --> 
        <scriptSourceDirectory></scriptSourceDirectory> 

        <!-- 该元素设置了项目单元测试使用的源码目录，当测试项目的时候，构建系统会编译目录里的源码。
             该路径是相对于pom.xml的相对路径。 --> 
        <testSourceDirectory></testSourceDirectory> 

        <!-- 被编译过的应用程序class文件存放的目录。 --> 
        <outputDirectory></outputDirectory> 

        <!-- 被编译过的测试class文件存放的目录。 --> 
        <testOutputDirectory></testOutputDirectory> 

        <!-- 使用来自该项目的一系列构建扩展 --> 
        <extensions> 
            <!-- 描述使用到的构建扩展。 --> 
            <extension> 
                <!-- 构建扩展的groupId --> 
                <groupId></groupId> 

                <!-- 构建扩展的artifactId --> 
                <artifactId></artifactId> 

                <!-- 构建扩展的版本 --> 
                <version></version> 
            </extension> 
        </extensions> 

        <!-- 当项目没有规定目标（Maven2 叫做阶段）时的默认值 --> 
        <defaultGoal></defaultGoal> 

        <!-- 这个元素描述了项目相关的所有资源路径列表，例如和项目相关的属性文件，这些资源被包含在
             最终的打包文件里。 --> 
        <resources> 
            <!-- 这个元素描述了项目相关或测试相关的所有资源路径 --> 
            <resource> 
                <!-- 描述了资源的目标路径。该路径相对target/classes目录（例如${project.build.outputDirectory}）。
                     举个例子，如果你想资源在特定的包里(org.apache.maven.messages)，你就必须该元素设置为
                    org/apache/maven/messages。然而，如果你只是想把资源放到源码目录结构里，就不需要该配置。 --> 
                <targetPath></targetPath> 

                <!-- 是否使用参数值代替参数名。参数值取自properties元素或者文件里配置的属性，文件在filters元素
                     里列出。 --> 
                <filtering></filtering>

                <!-- 描述存放资源的目录，该路径相对POM路径 --> 
                <directory></directory>

                <!-- 包含的模式列表，例如**/*.xml. --> 
                <includes>
                    <include></include>
                </includes>

                <!-- 排除的模式列表，例如**/*.xml -->
                <excludes>
                    <exclude></exclude>
                </excludes>
            </resource> 
        </resources> 

        <!-- 这个元素描述了单元测试相关的所有资源路径，例如和单元测试相关的属性文件。 --> 
        <testResources> 
            <!-- 这个元素描述了测试相关的所有资源路径，参见build/resources/resource元素的说明 --> 
            <testResource> 
                <!-- 描述了测试相关的资源的目标路径。该路径相对target/classes目录（例如${project.build.outputDirectory}）。
                     举个例子，如果你想资源在特定的包里(org.apache.maven.messages)，你就必须该元素设置为
                    org/apache/maven/messages。然而，如果你只是想把资源放到源码目录结构里，就不需要该配置。 --> 
                <targetPath></targetPath> 

                <!-- 是否使用参数值代替参数名。参数值取自properties元素或者文件里配置的属性，文件在filters元素
                     里列出。 --> 
                <filtering></filtering>

                <!-- 描述存放测试相关的资源的目录，该路径相对POM路径 --> 
                <directory></directory>

                <!-- 包含的模式列表，例如**/*.xml. --> 
                <includes>
                    <include></include>
                </includes>

                <!-- 排除的模式列表，例如**/*.xml -->
                <excludes>
                    <exclude></exclude>
                </excludes> 
            </testResource> 
        </testResources> 

        <!-- 构建产生的所有文件存放的目录 --> 
        <directory></directory> 

        <!-- 产生的构件的文件名，默认值是${artifactId}-${version}。 --> 
        <finalName></finalName> 

        <!-- 当filtering开关打开时，使用到的过滤器属性文件列表 --> 
        <filters></filters> 

        <!-- 子项目可以引用的默认插件信息。该插件配置项直到被引用时才会被解析或绑定到生命周期。给定插件的任何本
             地配置都会覆盖这里的配置 --> 
        <pluginManagement> 
            <!-- 使用的插件列表 。 --> 
            <plugins> 
                <!-- plugin元素包含描述插件所需要的信息。 --> 
                <plugin> 
                    <!-- 插件在仓库里的group ID --> 
                    <groupId></groupId> 

                    <!-- 插件在仓库里的artifact ID --> 
                    <artifactId></artifactId> 

                    <!-- 被使用的插件的版本（或版本范围） --> 
                    <version></version> 

                    <!-- 是否从该插件下载Maven扩展（例如打包和类型处理器），由于性能原因，只有在真需要下载时，该
                         元素才被设置成enabled。 --> 
                    <extensions>true/false</extensions> 

                    <!-- 在构建生命周期中执行一组目标的配置。每个目标可能有不同的配置。 --> 
                    <executions> 
                        <!-- execution元素包含了插件执行需要的信息 --> 
                        <execution> 
                            <!-- 执行目标的标识符，用于标识构建过程中的目标，或者匹配继承过程中需要合并的执行目标 --> 
                            <id></id>

                            <!-- 绑定了目标的构建生命周期阶段，如果省略，目标会被绑定到源数据里配置的默认阶段 --> 
                            <phase></phase>

                            <!-- 配置的执行目标 --> 
                            <goals></goals> 

                            <!-- 配置是否被传播到子POM --> 
                            <inherited>true/false</inherited> 

                            <!-- 作为DOM对象的配置 --> 
                            <configuration></configuration>
                        </execution> 
                    </executions> 

                    <!-- 项目引入插件所需要的额外依赖 --> 
                    <dependencies>
                        <!-- 参见dependencies/dependency元素 --> 
                        <dependency> 
                        </dependency> 
                    </dependencies> 

                    <!-- 任何配置是否被传播到子项目 --> 
                    <inherited>true/false</inherited>

                    <!-- 作为DOM对象的配置 --> 
                    <configuration></configuration>
                </plugin> 
            </plugins> 
        </pluginManagement> 

        <!-- 该项目使用的插件列表 。 --> 
        <plugins> 
            <!-- plugin元素包含描述插件所需要的信息。 --> 
            <plugin> 
                <!-- 插件在仓库里的group ID --> 
                <groupId></groupId> 

                <!-- 插件在仓库里的artifact ID --> 
                <artifactId></artifactId> 

                <!-- 被使用的插件的版本（或版本范围） --> 
                <version></version> 

                <!-- 是否从该插件下载Maven扩展（例如打包和类型处理器），由于性能原因，只有在真需要下载时，该元素才被设置成enabled。 --> 
                <extensions>true/false</extensions> 

                <!-- 在构建生命周期中执行一组目标的配置。每个目标可能有不同的配置。 --> 
                <executions> 
                    <!-- execution元素包含了插件执行需要的信息 --> 
                    <execution> 
                        <!-- 执行目标的标识符，用于标识构建过程中的目标，或者匹配继承过程中需要合并的执行目标 --> 
                        <id></id>

                        <!-- 绑定了目标的构建生命周期阶段，如果省略，目标会被绑定到源数据里配置的默认阶段 --> 
                        <phase></phase>

                        <!-- 配置的执行目标 --> 
                        <goals></goals> 

                        <!-- 配置是否被传播到子POM --> 
                        <inherited>true/false</inherited> 

                        <!-- 作为DOM对象的配置 --> 
                        <configuration></configuration>
                    </execution> 
                </executions> 

                <!-- 项目引入插件所需要的额外依赖 --> 
                <dependencies>
                    <!-- 参见dependencies/dependency元素 --> 
                    <dependency> 
                    </dependency> 
                </dependencies> 

                <!-- 任何配置是否被传播到子项目 --> 
                <inherited>true/false</inherited> 

                <!-- 作为DOM对象的配置 --> 
                <configuration></configuration> 
            </plugin> 
        </plugins>
    </build> 

    <!-- 在列的项目构建profile，如果被激活，会修改构建处理 --> 
    <profiles> 
        <!-- 根据环境参数或命令行参数激活某个构建处理 --> 
        <profile> 
            <!-- 构建配置的唯一标识符。即用于命令行激活，也用于在继承时合并具有相同标识符的profile。 --> 
            <id></id>

            <!-- 自动触发profile的条件逻辑。Activation是profile的开启钥匙。profile的力量来自于它能够
                 在某些特定的环境中自动使用某些特定的值；这些环境通过activation元素指定。activation元
                 素并不是激活profile的唯一方式。 --> 
            <activation> 
                <!-- profile默认是否激活的标志 --> 
                <activeByDefault>true/false</activeByDefault> 

                <!-- 当匹配的jdk被检测到，profile被激活。例如，1.4激活JDK1.4，1.4.0_2，而!1.4激活所有版本
                     不是以1.4开头的JDK。 --> 
                <jdk>jdk版本，如:1.7</jdk> 

                <!-- 当匹配的操作系统属性被检测到，profile被激活。os元素可以定义一些操作系统相关的属性。 --> 
                <os> 
                    <!-- 激活profile的操作系统的名字 --> 
                    <name> Windows XP </name> 

                    <!-- 激活profile的操作系统所属家族(如 'windows') --> 
                    <family> Windows </family> 

                    <!-- 激活profile的操作系统体系结构 --> 
                    <arch> x86 </arch> 

                    <!-- 激活profile的操作系统版本 --> 
                    <version> 5.1.2600 </version> 
                </os> 

                <!-- 如果Maven检测到某一个属性（其值可以在POM中通过${名称}引用），其拥有对应的名称和值，Profile
                     就会被激活。如果值字段是空的，那么存在属性名称字段就会激活profile，否则按区分大小写方式匹
                     配属性值字段 --> 
                <property> 
                    <!-- 激活profile的属性的名称 --> 
                    <name> mavenVersion </name> 

                    <!-- 激活profile的属性的值 --> 
                    <value> 2.0.3 </value> 
                </property> 

                <!-- 提供一个文件名，通过检测该文件的存在或不存在来激活profile。missing检查文件是否存在，如果不存在则激活 
                     profile。另一方面，exists则会检查文件是否存在，如果存在则激活profile。 --> 
                <file> 
                    <!-- 如果指定的文件存在，则激活profile。 --> 
                    <exists> /usr/local/hudson/hudson-home/jobs/maven-guide-zh-to-production/workspace/ </exists> 

                    <!-- 如果指定的文件不存在，则激活profile。 --> 
                    <missing> /usr/local/hudson/hudson-home/jobs/maven-guide-zh-to-production/workspace/ </missing> 
                </file> 
            </activation> 

            <!-- 构建项目所需要的信息。参见build元素 --> 
            <build> 
                <defaultGoal /> 
                <resources> 
                    <resource> 
                        <targetPath></targetPath>
                        <filtering></filtering>
                        <directory></directory>
                        <includes>
                            <include></include>
                        </includes>
                        <excludes>
                            <exclude></exclude>
                        </excludes>
                    </resource> 
                </resources> 
                <testResources> 
                    <testResource> 
                        <targetPath></targetPath>
                        <filtering></filtering>
                        <directory></directory>
                        <includes>
                            <include></include>
                        </includes>
                        <excludes>
                            <exclude></exclude>
                        </excludes> 
                    </testResource> 
                </testResources> 
                <directory></directory>
                <finalName></finalName>
                <filters></filters> 
                <pluginManagement> 
                    <plugins> 
                        <!-- 参见build/pluginManagement/plugins/plugin元素 --> 
                        <plugin> 
                            <groupId></groupId>
                            <artifactId></artifactId>
                            <version></version>
                            <extensions>true/false</extensions> 
                            <executions> 
                                <execution> 
                                    <id></id>
                                    <phase></phase>
                                    <goals></goals>
                                    <inherited>true/false</inherited>
                                    <configuration></configuration> 
                                </execution> 
                            </executions> 
                            <dependencies> 
                                <!-- 参见dependencies/dependency元素 --> 
                                <dependency> 
                                </dependency> 
                            </dependencies> 
                            <goals></goals>
                            <inherited>true/false</inherited>
                            <configuration></configuration>
                        </plugin> 
                    </plugins> 
                </pluginManagement> 
                <plugins> 
                    <!-- 参见build/pluginManagement/plugins/plugin元素 --> 
                    <plugin> 
                        <groupId></groupId>
                        <artifactId></artifactId>
                        <version></version>
                        <extensions>true/false</extensions> 
                        <executions> 
                            <execution> 
                                <id></id>
                                <phase></phase>
                                <goals></goals>
                                <inherited>true/false</inherited>
                                <configuration></configuration> 
                            </execution> 
                        </executions> 
                        <dependencies> 
                            <!-- 参见dependencies/dependency元素 --> 
                            <dependency> 
                            </dependency> 
                        </dependencies> 
                        <goals></goals>
                        <inherited>true/false</inherited>
                        <configuration></configuration> 
                    </plugin> 
                </plugins> 
            </build> 

            <!-- 模块（有时称作子项目） 被构建成项目的一部分。列出的每个模块元素是指向该模块的目录的
                 相对路径 --> 
            <modules>
                <!--子项目相对路径-->
                <module></module>
            </modules> 

            <!-- 发现依赖和扩展的远程仓库列表。 --> 
            <repositories> 
                <!-- 参见repositories/repository元素 --> 
                <repository> 
                    <releases> 
                        <enabled></enabled>
                        <updatePolicy></updatePolicy>
                        <checksumPolicy></checksumPolicy> 
                    </releases> 
                    <snapshots> 
                        <enabled></enabled>
                        <updatePolicy></updatePolicy>
                        <checksumPolicy></checksumPolicy> 
                    </snapshots> 
                    <id></id>
                    <name></name>
                    <url></url>
                    <layout></layout> 
                </repository> 
            </repositories> 

            <!-- 发现插件的远程仓库列表，这些插件用于构建和报表 --> 
            <pluginRepositories> 
                <!-- 包含需要连接到远程插件仓库的信息.参见repositories/repository元素 --> 
                <pluginRepository> 
                    <releases> 
                        <enabled></enabled>
                        <updatePolicy></updatePolicy>
                        <checksumPolicy></checksumPolicy> 
                    </releases> 
                    <snapshots> 
                        <enabled></enabled>
                        <updatePolicy></updatePolicy>
                        <checksumPolicy></checksumPolicy>
                    </snapshots> 
                    <id></id>
                    <name></name>
                    <url></url>
                    <layout></layout> 
                </pluginRepository> 
            </pluginRepositories> 

            <!-- 该元素描述了项目相关的所有依赖。 这些依赖组成了项目构建过程中的一个个环节。它们自动从项目定义的
                 仓库中下载。要获取更多信息，请看项目依赖机制。 --> 
            <dependencies> 
                <!-- 参见dependencies/dependency元素 --> 
                <dependency> 
                </dependency> 
            </dependencies> 

            <!-- 不赞成使用. 现在Maven忽略该元素. --> 
            <reports></reports> 

            <!-- 该元素包括使用报表插件产生报表的规范。当用户执行“mvn site”，这些报表就会运行。 在页面导航栏能看
                 到所有报表的链接。参见reporting元素 --> 
            <reporting></reporting> 

            <!-- 参见dependencyManagement元素 --> 
            <dependencyManagement> 
                <dependencies> 
                    <!-- 参见dependencies/dependency元素 --> 
                    <dependency> 
                    </dependency> 
                </dependencies> 
            </dependencyManagement> 

            <!-- 参见distributionManagement元素 --> 
            <distributionManagement> 
            </distributionManagement> 

            <!-- 参见properties元素 --> 
            <properties /> 
        </profile> 
    </profiles> 

    <!-- 模块（有时称作子项目） 被构建成项目的一部分。列出的每个模块元素是指向该模块的目录的相对路径 --> 
    <modules>
        <!--子项目相对路径-->
        <module></module>
    </modules> 

    <!-- 发现依赖和扩展的远程仓库列表。 --> 
    <repositories> 
        <!-- 包含需要连接到远程仓库的信息 --> 
        <repository> 
            <!-- 如何处理远程仓库里发布版本的下载 --> 
            <releases> 
                <!-- true或者false表示该仓库是否为下载某种类型构件（发布版，快照版）开启。 --> 
                <enabled></enabled> 

                <!-- 该元素指定更新发生的频率。Maven会比较本地POM和远程POM的时间戳。这里的选项是：always（一直），
                     daily（默认，每日），interval：X（这里X是以分钟为单位的时间间隔），或者never（从不）。 --> 
                <updatePolicy></updatePolicy> 

                <!-- 当Maven验证构件校验文件失败时该怎么做：ignore（忽略），fail（失败），或者warn（警告）。 --> 
                <checksumPolicy></checksumPolicy> 
            </releases> 

            <!-- 如何处理远程仓库里快照版本的下载。有了releases和snapshots这两组配置，POM就可以在每个单独的仓库中，
                 为每种类型的构件采取不同的策略。例如，可能有人会决定只为开发目的开启对快照版本下载的支持。参见repositories/repository/releases元素 --> 
            <snapshots> 
                <enabled></enabled>
                <updatePolicy></updatePolicy>
                <checksumPolicy></checksumPolicy> 
            </snapshots> 

            <!-- 远程仓库唯一标识符。可以用来匹配在settings.xml文件里配置的远程仓库 --> 
            <id> banseon-repository-proxy </id> 

            <!-- 远程仓库名称 --> 
            <name> banseon-repository-proxy </name> 

            <!-- 远程仓库URL，按protocol://hostname/path形式 --> 
            <url> http://192.168.1.169:9999/repository/ </url> 

            <!-- 用于定位和排序构件的仓库布局类型-可以是default（默认）或者legacy（遗留）。Maven 2为其仓库提供了一个默认
                 的布局；然而，Maven 1.x有一种不同的布局。我们可以使用该元素指定布局是default（默认）还是legacy（遗留）。 --> 
            <layout> default </layout> 
        </repository> 
    </repositories> 

    <!-- 发现插件的远程仓库列表，这些插件用于构建和报表 --> 
    <pluginRepositories> 
        <!-- 包含需要连接到远程插件仓库的信息.参见repositories/repository元素 --> 
        <pluginRepository> 
        </pluginRepository> 
    </pluginRepositories> 

    <!-- 该元素描述了项目相关的所有依赖。 这些依赖组成了项目构建过程中的一个个环节。它们自动从项目定义的仓库中下载。
         要获取更多信息，请看项目依赖机制。 --> 
    <dependencies> 
        <dependency> 
            <!-- 依赖的group ID --> 
            <groupId> org.apache.maven </groupId> 

            <!-- 依赖的artifact ID --> 
            <artifactId> maven-artifact </artifactId> 

            <!-- 依赖的版本号。 在Maven 2里, 也可以配置成版本号的范围。 --> 
            <version> 3.8.1 </version> 

            <!-- 依赖类型，默认类型是jar。它通常表示依赖的文件的扩展名，但也有例外。一个类型可以被映射成另外一个扩展
                 名或分类器。类型经常和使用的打包方式对应，尽管这也有例外。一些类型的例子：jar，war，ejb-client和test-jar。
                 如果设置extensions为 true，就可以在plugin里定义新的类型。所以前面的类型的例子不完整。 --> 
            <type> jar </type> 

            <!-- 依赖的分类器。分类器可以区分属于同一个POM，但不同构建方式的构件。分类器名被附加到文件名的版本号后面。例如，
                 如果你想要构建两个单独的构件成JAR，一个使用Java 1.4编译器，另一个使用Java 6编译器，你就可以使用分类器来生
                 成两个单独的JAR构件。 --> 
            <classifier></classifier> 

            <!-- 依赖范围。在项目发布过程中，帮助决定哪些构件被包括进来。欲知详情请参考依赖机制。 
                - compile ：默认范围，用于编译 
                - provided：类似于编译，但支持你期待jdk或者容器提供，类似于classpath 
                - runtime: 在执行时需要使用 
                - test: 用于test任务时使用 
                - system: 需要外在提供相应的元素。通过systemPath来取得 
                - systemPath: 仅用于范围为system。提供相应的路径 
                - optional: 当项目自身被依赖时，标注依赖是否传递。用于连续依赖时使用 --> 
            <scope> test </scope> 

            <!-- 仅供system范围使用。注意，不鼓励使用这个元素，并且在新的版本中该元素可能被覆盖掉。该元素为依赖规定了文件
                 系统上的路径。需要绝对路径而不是相对路径。推荐使用属性匹配绝对路径，例如${java.home}。 --> 
            <systemPath></systemPath> 

            <!-- 当计算传递依赖时， 从依赖构件列表里，列出被排除的依赖构件集。即告诉maven你只依赖指定的项目，不依赖项目的
                 依赖。此元素主要用于解决版本冲突问题 --> 
            <exclusions> 
                <exclusion> 
                    <artifactId> spring-core </artifactId> 
                    <groupId> org.springframework </groupId> 
                </exclusion> 
            </exclusions> 

            <!-- 可选依赖，如果你在项目B中把C依赖声明为可选，你就需要在依赖于B的项目（例如项目A）中显式的引用对C的依赖。
                 可选依赖阻断依赖的传递性。 --> 
            <optional> true </optional> 
        </dependency> 
    </dependencies> 

    <!-- 不赞成使用. 现在Maven忽略该元素. --> 
    <reports></reports> 

    <!-- 该元素描述使用报表插件产生报表的规范。当用户执行“mvn site”，这些报表就会运行。 在页面导航栏能看到所有报表的链接。 --> 
    <reporting> 
        <!-- true，则，网站不包括默认的报表。这包括“项目信息”菜单中的报表。 --> 
        <excludeDefaults /> 

        <!-- 所有产生的报表存放到哪里。默认值是${project.build.directory}/site。 --> 
        <outputDirectory /> 

        <!-- 使用的报表插件和他们的配置。 --> 
        <plugins> 
            <!-- plugin元素包含描述报表插件需要的信息 --> 
            <plugin> 
                <!-- 报表插件在仓库里的group ID --> 
                <groupId></groupId> 
                <!-- 报表插件在仓库里的artifact ID --> 
                <artifactId></artifactId> 

                <!-- 被使用的报表插件的版本（或版本范围） --> 
                <version></version> 

                <!-- 任何配置是否被传播到子项目 --> 
                <inherited>true/false</inherited> 

                <!-- 报表插件的配置 --> 
                <configuration></configuration> 

                <!-- 一组报表的多重规范，每个规范可能有不同的配置。一个规范（报表集）对应一个执行目标 。例如，
                     有1，2，3，4，5，6，7，8，9个报表。1，2，5构成A报表集，对应一个执行目标。2，5，8构成B报
                     表集，对应另一个执行目标 --> 
                <reportSets> 
                    <!-- 表示报表的一个集合，以及产生该集合的配置 --> 
                    <reportSet> 
                        <!-- 报表集合的唯一标识符，POM继承时用到 --> 
                        <id></id> 

                        <!-- 产生报表集合时，被使用的报表的配置 --> 
                        <configuration></configuration> 

                        <!-- 配置是否被继承到子POMs --> 
                        <inherited>true/false</inherited> 

                        <!-- 这个集合里使用到哪些报表 --> 
                        <reports></reports> 
                    </reportSet> 
                </reportSets> 
            </plugin> 
        </plugins> 
    </reporting> 

    <!-- 继承自该项目的所有子项目的默认依赖信息。这部分的依赖信息不会被立即解析,而是当子项目声明一个依赖
        （必须描述group ID和artifact ID信息），如果group ID和artifact ID以外的一些信息没有描述，则通过
            group ID和artifact ID匹配到这里的依赖，并使用这里的依赖信息。 --> 
    <dependencyManagement> 
        <dependencies> 
            <!-- 参见dependencies/dependency元素 --> 
            <dependency> 
            </dependency> 
        </dependencies> 
    </dependencyManagement> 

    <!-- 项目分发信息，在执行mvn deploy后表示要发布的位置。有了这些信息就可以把网站部署到远程服务器或者
         把构件部署到远程仓库。 --> 
    <distributionManagement> 
        <!-- 部署项目产生的构件到远程仓库需要的信息 --> 
        <repository> 
            <!-- 是分配给快照一个唯一的版本号（由时间戳和构建流水号）？还是每次都使用相同的版本号？参见
                 repositories/repository元素 --> 
            <uniqueVersion /> 
            <id> banseon-maven2 </id> 
            <name> banseon maven2 </name> 
            <url> file://${basedir}/target/deploy </url> 
            <layout></layout> 
        </repository> 

        <!-- 构件的快照部署到哪里？如果没有配置该元素，默认部署到repository元素配置的仓库，参见
             distributionManagement/repository元素 --> 
        <snapshotRepository> 
            <uniqueVersion /> 
            <id> banseon-maven2 </id> 
            <name> Banseon-maven2 Snapshot Repository </name> 
            <url> scp://svn.baidu.com/banseon:/usr/local/maven-snapshot </url> 
            <layout></layout>
        </snapshotRepository> 

        <!-- 部署项目的网站需要的信息 --> 
        <site> 
            <!-- 部署位置的唯一标识符，用来匹配站点和settings.xml文件里的配置 --> 
            <id> banseon-site </id> 

            <!-- 部署位置的名称 --> 
            <name> business api website </name> 

            <!-- 部署位置的URL，按protocol://hostname/path形式 --> 
            <url> 
                scp://svn.baidu.com/banseon:/var/www/localhost/banseon-web 
            </url> 
        </site> 

        <!-- 项目下载页面的URL。如果没有该元素，用户应该参考主页。使用该元素的原因是：帮助定位
             那些不在仓库里的构件（由于license限制）。 --> 
        <downloadUrl /> 

        <!-- 如果构件有了新的group ID和artifact ID（构件移到了新的位置），这里列出构件的重定位信息。 --> 
        <relocation> 
            <!-- 构件新的group ID --> 
            <groupId></groupId> 

            <!-- 构件新的artifact ID --> 
            <artifactId></artifactId> 

            <!-- 构件新的版本号 --> 
            <version></version> 

            <!-- 显示给用户的，关于移动的额外信息，例如原因。 --> 
            <message></message> 
        </relocation> 

        <!-- 给出该构件在远程仓库的状态。不得在本地项目中设置该元素，因为这是工具自动更新的。有效的值
             有：none（默认），converted（仓库管理员从Maven 1 POM转换过来），partner（直接从伙伴Maven 
             2仓库同步过来），deployed（从Maven 2实例部署），verified（被核实时正确的和最终的）。 --> 
        <status></status> 
    </distributionManagement> 

    <!-- 以值替代名称，Properties可以在整个POM中使用，也可以作为触发条件（见settings.xml配置文件里
         activation元素的说明）。格式是<name>value</name>。 -->
    <properties>
        <name>value</name>
    </properties>
</project>
```

### 6.2. 附件2：POM文件单项配置说明

#### 6.2.1. localRepository

```XML
 <!-- 本地仓库的路径。默认值为${user.home}/.m2/repository。 -->
 <localRepository>usr/local/maven</localRepository>
```

#### 6.2.2. interactiveMode

```XML
 <!--Maven是否需要和用户交互以获得输入。如果Maven需要和用户交互以获得输入，则设置成true，反之则应为false。默认为true。-->
 <interactiveMode>true</interactiveMode>
```

#### 6.2.3. usePluginRegistry

```XML
<!--Maven是否需要使用plugin-registry.xml文件来管理插件版本。如果需要让Maven使用文件${user.home}/.m2/plugin-registry.xml来管理插件版本，则设为true。默认为false。-->
 <usePluginRegistry>false</usePluginRegistry>
```

#### 6.2.4. offline

```XML
 <!--表示Maven是否需要在离线模式下运行。如果构建系统需要在离线模式下运行，则为true，默认为false。当由于网络设置原因或者安全因素，构建服务器不能连接远程仓库的时候，该配置就十分有用。 -->
 <offline>false</offline>
```

#### 6.2.5. pluginGroups

```XML
<!--当插件的组织Id（groupId）没有显式提供时，供搜寻插件组织Id（groupId）的列表。该元素包含一个pluginGroup元素列表，每个子元素包含了一个组织Id（groupId）。当我们使用某个插件，并且没有在命令行为其提供组织Id（groupId）的时候，Maven就会使用该列表。默认情况下该列表包含了org.apache.maven.plugins和org.codehaus.mojo -->
 <pluginGroups>
  <!--plugin的组织Id（groupId） -->
  <pluginGroup>org.codehaus.mojo</pluginGroup>
 </pluginGroups>
```

#### 6.2.6. proxies

```XML
<!--用来配置不同的代理，多代理profiles 可以应对笔记本或移动设备的工作环境：通过简单的设置profile id就可以很容易的更换整个代理配置。 -->
 <proxies>
  <!--代理元素包含配置代理时需要的信息-->
  <proxy>
   <!--代理的唯一定义符，用来区分不同的代理元素。-->
   <id>myproxy</id>
   <!--该代理是否是激活的那个。true则激活代理。当我们声明了一组代理，而某个时候只需要激活一个代理的时候，该元素就可以派上用处。 -->
   <active>true</active>
   <!--代理的协议。 协议://主机名:端口，分隔成离散的元素以方便配置。-->
   <protocol>http</protocol>
   <!--代理的主机名。协议://主机名:端口，分隔成离散的元素以方便配置。  -->
   <host>proxy.somewhere.com</host>
   <!--代理的端口。协议://主机名:端口，分隔成离散的元素以方便配置。 -->
   <port>8080</port>
   <!--代理的用户名，用户名和密码表示代理服务器认证的登录名和密码。 -->
   <username>proxyuser</username>
   <!--代理的密码，用户名和密码表示代理服务器认证的登录名和密码。 -->
   <password>somepassword</password>
   <!--不该被代理的主机名列表。该列表的分隔符由代理服务器指定；例子中使用了竖线分隔符，使用逗号分隔也很常见。-->
   <nonProxyHosts>*.google.com|ibiblio.org</nonProxyHosts>
  </proxy>
 </proxies>
```

#### 6.2.7. servers

```XML
<!--配置服务端的一些设置。一些设置如安全证书不应该和pom.xml一起分发。这种类型的信息应该存在于构建服务器上的settings.xml文件中。-->
 <servers>
  <!--服务器元素包含配置服务器时需要的信息 -->
  <server>
   <!--这是server的id（注意不是用户登陆的id），该id与distributionManagement中repository元素的id相匹配。-->
   <id>server001</id>
   <!--鉴权用户名。鉴权用户名和鉴权密码表示服务器认证所需要的登录名和密码。 -->
   <username>my_login</username>
   <!--鉴权密码 。鉴权用户名和鉴权密码表示服务器认证所需要的登录名和密码。密码加密功能已被添加到2.1.0 +。详情请访问密码加密页面-->
   <password>my_password</password>
   <!--鉴权时使用的私钥位置。和前两个元素类似，私钥位置和私钥密码指定了一个私钥的路径（默认是${user.home}/.ssh/id_dsa）以及如果需要的话，一个密语。将来passphrase和password元素可能会被提取到外部，但目前它们必须在settings.xml文件以纯文本的形式声明。 -->
   <privateKey>${usr.home}/.ssh/id_dsa</privateKey>
   <!--鉴权时使用的私钥密码。-->
   <passphrase>some_passphrase</passphrase>
   <!--文件被创建时的权限。如果在部署的时候会创建一个仓库文件或者目录，这时候就可以使用权限（permission）。这两个元素合法的值是一个三位数字，其对应了unix文件系统的权限，如664，或者775。 -->
   <filePermissions>664</filePermissions>
   <!--目录被创建时的权限。 -->
   <directoryPermissions>775</directoryPermissions>
  </server>
 </servers>
```

#### 6.2.8. mirrors

```XML
<!--为仓库列表配置的下载镜像列表。高级设置请参阅镜像设置页面 -->
 <mirrors>
  <!--给定仓库的下载镜像。 -->
  <mirror>
   <!--该镜像的唯一标识符。id用来区分不同的mirror元素。 -->
   <id>planetmirror.com</id>
   <!--镜像名称 -->
   <name>PlanetMirror Australia</name>
   <!--该镜像的URL。构建系统会优先考虑使用该URL，而非使用默认的服务器URL。 -->
   <url>http://downloads.planetmirror.com/pub/maven2</url>
   <!--被镜像的服务器的id。例如，如果我们要设置了一个Maven中央仓库（http://repo.maven.apache.org/maven2/）的镜像，就需要将该元素设置成central。这必须和中央仓库的id central完全一致。-->
   <mirrorOf>central</mirrorOf>
  </mirror>
 </mirrors>
```

#### 6.2.9. profiles

```XML
 <!--根据环境参数来调整构建配置的列表。settings.xml中的profile元素是pom.xml中profile元素的裁剪版本。它包含了id，activation, repositories, pluginRepositories和 properties元素。这里的profile元素只包含这五个子元素是因为这里只关心构建系统这个整体（这正是settings.xml文件的角色定位），而非单独的项目对象模型设置。如果一个settings中的profile被激活，它的值会覆盖任何其它定义在POM中或者profile.xml中的带有相同id的profile。 -->
 <profiles>
  <!--根据环境参数来调整的构件的配置-->
  <profile>
   <!--该配置的唯一标识符。 -->
   <id>test</id>
```

#### 6.2.10. Activation

```XML
<!--自动触发profile的条件逻辑。Activation是profile的开启钥匙。如POM中的profile一样，profile的力量来自于它能够在某些特定的环境中自动使用某些特定的值；这些环境通过activation元素指定。activation元素并不是激活profile的唯一方式。settings.xml文件中的activeProfile元素可以包含profile的id。profile也可以通过在命令行，使用-P标记和逗号分隔的列表来显式的激活（如，-P test）。-->
   <activation>
    <!--profile默认是否激活的标识-->
    <activeByDefault>false</activeByDefault>
    <!--当匹配的jdk被检测到，profile被激活。例如，1.4激活JDK1.4，1.4.0_2，而!1.4激活所有版本不是以1.4开头的JDK。-->
    <jdk>1.5</jdk>
    <!--当匹配的操作系统属性被检测到，profile被激活。os元素可以定义一些操作系统相关的属性。-->
    <os>
     <!--激活profile的操作系统的名字 -->
     <name>Windows XP</name>
     <!--激活profile的操作系统所属家族(如 'windows')  -->
     <family>Windows</family>
     <!--激活profile的操作系统体系结构  -->
     <arch>x86</arch>
     <!--激活profile的操作系统版本-->
     <version>5.1.2600</version>
    </os>
    <!--如果Maven检测到某一个属性（其值可以在POM中通过${name}引用），其拥有对应的name = 值，Profile就会被激活。如果值字段是空的，那么存在属性名称字段就会激活profile，否则按区分大小写方式匹配属性值字段-->
    <property>
     <!--激活profile的属性的名称-->
     <name>mavenVersion</name>
     <!--激活profile的属性的值 -->
     <value>2.0.3</value>
    </property>
    <!--提供一个文件名，通过检测该文件的存在或不存在来激活profile。missing检查文件是否存在，如果不存在则激活profile。另一方面，exists则会检查文件是否存在，如果存在则激活profile。-->
    <file>
     <!--如果指定的文件存在，则激活profile。 -->
     <exists>${basedir}/file2.properties</exists>
     <!--如果指定的文件不存在，则激活profile。-->
     <missing>${basedir}/file1.properties</missing>
    </file>
   </activation>
```

#### 6.2.11. Repositories

```XML
    <!--远程仓库列表，它是Maven用来填充构建系统本地仓库所使用的一组远程项目。 -->
    <repositories>
    <!--包含需要连接到远程仓库的信息 -->
    <repository>
     <!--远程仓库唯一标识-->
     <id>codehausSnapshots</id>
     <!--远程仓库名称 -->
     <name>Codehaus Snapshots</name>
     <!--如何处理远程仓库里发布版本的下载-->
     <releases>
      <!--true或者false表示该仓库是否为下载某种类型构件（发布版，快照版）开启。  -->
      <enabled>false</enabled>
      <!--该元素指定更新发生的频率。Maven会比较本地POM和远程POM的时间戳。这里的选项是：always（一直），daily（默认，每日），interval：X（这里X是以分钟为单位的时间间隔），或者never（从不）。 -->
      <updatePolicy>always</updatePolicy>
      <!--当Maven验证构件校验文件失败时该怎么做-ignore（忽略），fail（失败），或者warn（警告）。-->
      <checksumPolicy>warn</checksumPolicy>
     </releases>
     <!--如何处理远程仓库里快照版本的下载。有了releases和snapshots这两组配置，POM就可以在每个单独的仓库中，为每种类型的构件采取不同的策略。例如，可能有人会决定只为开发目的开启对快照版本下载的支持。参见repositories/repository/releases元素-->
     <snapshots>
      <enabled/><updatePolicy/><checksumPolicy/>
     </snapshots>
     <!--远程仓库URL，按protocol://hostname/path形式 -->
     <url>http://snapshots.maven.codehaus.org/maven2</url>
     <!--用于定位和排序构件的仓库布局类型-可以是default（默认）或者legacy（遗留）。Maven 2为其仓库提供了一个默认的布局；然而，Maven 1.x有一种不同的布局。我们可以使用该元素指定布局是default（默认）还是legacy（遗留）。 -->
     <layout>default</layout>
    </repository>
   </repositories>
   <!--发现插件的远程仓库列表。仓库是两种主要构件的家。第一种构件被用作其它构件的依赖。这是中央仓库中存储的大部分构件类型。另外一种构件类型是插件。Maven插件是一种特殊类型的构件。由于这个原因，插件仓库独立于其它仓库。pluginRepositories元素的结构和repositories元素的结构类似。每个pluginRepository元素指定一个Maven可以用来寻找新插件的远程地址。-->
   <pluginRepositories>
    <!--包含需要连接到远程插件仓库的信息.参见profiles/profile/repositories/repository元素的说明-->
          <pluginRepository>           
     <releases>      
      <enabled/><updatePolicy/><checksumPolicy/>
     </releases>
     <snapshots>
      <enabled/><updatePolicy/><checksumPolicy/>
     </snapshots>
     <id/><name/><url/><layout/>
          </pluginRepository>
        </pluginRepositories>
  </profile>
 </profiles>
```

#### 6.2.12. activeProfiles

```XML
<settings>
<!--手动激活profiles的列表，按照profile被应用的顺序定义activeProfile。 该元素包含了一组activeProfile元素，每个activeProfile都含有一个profile id。任何在activeProfile中定义的profile id，不论环境设置如何，其对应的 profile都会被激活。如果没有匹配的profile，则什么都不会发生。例如，env-test是一个activeProfile，则在pom.xml（或者profile.xml）中对应id的profile会被激活。如果运行过程中找不到这样一个profile，Maven则会像往常一样运行。 -->
   <activeProfiles>
    <activeProfile>env-test</activeProfile>
   </activeProfiles>
</settings>
```
