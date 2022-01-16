# Maven 笔记

## 1. Maven 概述

### 1.1. Maven 是什么

Maven 的 Apache 公司开源项目，是项目管理、构建工具。用来依赖管理

Maven 采用 Project Object Modle（POM、项目对象模型）概念来管理项目，即将项目开发和管理过程抽象成一个项目对象模型(POM)，所有的项目配置信息都定义在 pom.xml 文件中。

![](images/20220115153602269_5827.png)

Maven 是用于建立 jar 包仓库，使用依赖管理，就是对 jar 包统一管理，maven 项目中如果需要使用一个 jar 包，只需要在 maven 项目中配置需要 jar 包坐标信息，maven 程序根据 jar 包坐标的信息去 jar 包仓库中查找 jar 包

### 1.2. 什么是项目构建

项目构建是一个项目从编写源代码到编译、测试、运行、打包、部署、运行的过程

#### 1.2.1. 传统项目构建过程

传统的使用 IDE 构建项目过程如下：

1. 在 IDE 中创建一个 java web 工程
2. 在工程中编写源代码及配置文件等
3. 对源代码进行编译，java 文件编译成 class 文件
4. 执行 Junit 单元测试
5. 将工程打成 war 包部署至 tomcat 运行

#### 1.2.2. maven 项目构建过程

maven 将项目构建的过程进行标准化，每个阶段使用一个命令完成

![](images/20220115223548069_27553.jpg)

上图中部分阶段对应命令如下：

- 清理阶段对应 maven 的命令是 `clean`，清理输出的 class 文件
- 编译阶段对应 maven 的命令是 `compile`，将 java 代码编译成 class 文件。
- 打包阶段对应 maven 的命令是 `package`，java 工程可以打成 jar 包，web 包可以打成 war 包
- 运行一个 maven 工程（web工程）需要一个命令：`tomat:run`

**maven工程构建的优点**：

1. 一个命令完成构建、运行，方便快捷。
2. 使用maven可以分模块化的构建项目
3. maven对每个构建阶段进行规范，非常有利于大型团队协作开发。

### 1.3. 什么是依赖管理

依赖：一个 java 项目可能要使用一些第三方的 jar 包才可以运行，那么我们说这个 java 项目依赖了这些第三方的jar包

依赖管理：对项目所有依赖的 jar 包进行规范化管理

#### 1.3.1. 传统项目的依赖管理

传统的项目工程要管理所依赖的jar包完全靠人工进行，程序员从网上下载jar包添加到项目工程中

例如：程序员手工将 Hibernate、struts2、spring 的jar添加到工程中的 WEB-INF/lib 目录下

![](images/20220115223743902_26171.jpg)

手工拷贝jar包添加到工程中存在的问题是：

1. 没有对jar包的版本统一管理，容易导致版本冲突。
2. 从网上找jar包非常不方便，有些jar找不到。
3. jar包添加到工程中导致工程过大。

#### 1.3.2. maven 项目的依赖管理

maven 项目管理所依赖的 jar 包不需要手动向工程添加 jar 包，只需要在 pom.xml（maven工程的配置文件）添加 jar 包的坐标，自动从 maven 仓库中下载 jar 包、运行

![](images/20220115223852366_26108.jpg)

**使用maven依赖管理添加jar的好处**：

1. 通过pom.xml文件对jar包的版本进行统一管理，可避免版本冲突。
2. maven团队维护了一个非常全的maven仓库，里边包括了当前使用的jar包，maven工程可以自动从maven仓库下载jar包，非常方便。

### 1.4. 使用maven的好处

1. 一步构建。maven对项目构建的过程进行标准化，通过一个命令即可完成构建过程
2. 依赖管理。maven工程不用手动导jar包，通过在pom.xml中定义坐标从maven仓库自动下载，方便且不易出错
3. maven的跨平台，可在window、linux上使用
4. maven遵循规范开发有利于提高大型团队的开发效率，降低项目的维护成本，大公司都会考虑使用maven来构建项目

### 1.5. Maven 的坐标

#### 1.5.1. 定义

坐标(GAV)：作为查找定位jar包（项目/组件）的唯一依据。例如：`struts2-core-2.3.24.jar`

坐标的规则：`Apache(公司名称)+struts2(项目名称)+2.3.24(版本信息)`

#### 1.5.2. Maven 坐标主要组成

- `groupId`：定义当前Maven项目隶属项目、组织
- `artifactId`：定义实际项目中的一个模块
- `version`：定义当前项目的当前版本
- `packaging`：定义该项目的打包方式(pom/jar/war，默认是jar包)

### 1.6. Maven 的两大核心

- **项目构建**：项目在编码完成后，对项目进行编译、测试、打包、部署等一系列的操作都通过**命令**来实现
- **依赖管理**：对jar包管理过程

## 2. Maven 的安装与配置

maven 程序的安装前提是：先安装JDK，它的运行依赖JDK

### 2.1. 下载 Maven 安装包

- 官方网站：https://maven.apache.org/
- 官方最新版本下载地址：https://maven.apache.org/download.cgi
- 历史版本下载地址：https://archive.apache.org/dist/maven/maven-3/
- Maven Releases History（Maven 历史版本）：https://maven.apache.org/docs/history.html

### 2.2. 环境要求

Maven 3.3+ 需要使用jdk 1.7+；请使用 `java -version` 命令检查本机的 JDK 安装信息。

### 2.3. Maven 的目录结构

下载 maven 的压缩包 `apache-maven-3.x.x-bin.zip`，解压到本地磁盘（解压目录不要有中文、空格）。maven 目录结构如下：

![](images/20220115151251934_13011.jpg)

- 【bin】：含有mvn运行的脚本。其中 mvn.bat（以run方式运行项目）、mvnDebug.bat（以debug方式运行项目）
- 【boot】：maven运行需要 plexus-classworlds 类加载器框架
- 【conf】：包含 settings.xml 配置文件，整个maven工具核心配置文件
    - > settings.xml 中默认的用户库: `${user.home}/.m2/repository`。通过maven下载的jar包都会存储到此仓库中。可以手动修改指定的保存路径。
- 【lib】：maven 运行依赖 jar 包

### 2.4. 配置环境变量

> 电脑上需先安装java环境，至少JDK1.7+版本（将`JAVA_HOME/bin`配置环境变量path）。

如果需要使用 maven 命令行，就必须配置环境变量。注：maven环境变量的配置是可选，如果使用eclipse/idea关联运行项目，可以需要配置也能正常使用。但建议还是配置使用比较方便。

- 配置 Maven 环境变量：`MAVEN_HOME`，值为本机 Maven 根目录路径。

![](images/20220115152425164_32613.jpg)

![](images/20220115152434135_15933.jpg)

- 在 path 系统变量中，增加 `%MAVEN_HOME%\bin`

![](images/20220115152346454_30533.png)

![](images/20220115152219668_14196.png)

- *可选配置（注意：可以不配置）*

配置 MAVEN_OPTS 的环境变量，值为 `-Xms256m -Xmx512m`

![](images/20220115152621134_12862.jpg)

### 2.5. 验证是否安装成功

查询maven的版本信息，用于检验 maven 是否配置成功。打开命令行，输入以下命令：

```bash
mvn -v
```

![](images/20220115153032526_25105.jpg)

## 3. maven 仓库

### 3.1. 仓库的类型

<font color=red>本地仓库</font>

- 用来存储从远程仓库或中央仓库下载的插件和jar包，项目使用一些插件或jar包，优先从本地仓库查找
- 默认本地仓库位置在 `${user.dir}/.m2/repository`，`${user.dir}`表示windows用户目录

<font color=red>远程仓库（私服）</font>

- 如果本地需要插件或者jar包，本地仓库没有，默认去远程仓库下载
- 远程仓库可以在互联网内也可以在局域网内
- 私服在一定范围内共享资源，仅对内部开放，不对外共享。可以保存具有版权的资源，包含购买或自主研发的jar。因为中央仓库中的jar都是开源的，不能存储具有版权的资源。

<font color=red>中央仓库</font>

在maven软件中内置一个远程仓库地址 http://repo1.maven.org/maven2，它是中央仓库，服务于整个互联网，它是由Maven团队自己维护，里面存储了非常全的jar包，它包含了世界上大部分流行的开源项目构件

![](images/20220115231724050_11833.png)

### 3.2. 配置阿里云远程仓库

因为中央仓库的服务是在国外，访问中央仓库比较慢，所以可以修改配置，当访问阿里云的公共库。中央库里面有的jar包，阿里云仓库几乎都有

修改 maven 的 `\apache-maven-3.x.x\conf\settings.xml` 配置文件，在 `<mirrors>` 标签体内增加阿里云仓库的镜像地址：

```xml
<!-- 阿里云仓库地址(老版) -->
<mirror>
	<id>nexus</id>
	<mirrorOf>*</mirrorOf>
	<url>https://maven.aliyun.com/nexus/content/groups/public/</url>
</mirror>

<!-- 阿里云Maven中央仓库 (最新)
	https://maven.aliyun.com/mvn/guide
-->
<mirror>
  <!-- 镜像的唯一标识符，用于区分不同的 mirror 元素（名称随意，不重复即可） -->
  <id>aliyunmaven</id>
  <!-- 对哪种仓库进行镜像，即指定替代哪种仓库 -->
  <mirrorOf>*</mirrorOf>
  <!-- 镜像名称（不配置也可以） -->
  <name>阿里云公共仓库</name>
  <!-- 镜像url -->
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

![](images/20220115233136704_31857.png)






























# 扩展资料

## 1. Maven 常用命令

### 1.1. maven命令格式与参数

maven 命令的格式为 `mvn [plugin-name]:[goal-name]`，可以接受的参数如下。

- `-D` 指定参数，如 `-Dmaven.test.skip=true` 跳过单元测试
- `-P` 指定 Profile 配置，可以用于区分环境
- `-e` 显示maven运行出错的信息
- `-o` 离线执行命令，即不去远程仓库更新包
- `-X` 显示maven允许的debug信息
- `-U` 强制去远程更新snapshot的插件或依赖，默认每天只更新一次。

### 1.2. 常用maven命令

- 创建maven项目：`mvn archetype:create`
- 指定 group：`-DgroupId=packageName`
- 指定 artifact：`-DartifactId=projectName`
- 创建web项目：`-DarchetypeArtifactId=maven-archetype-webapp`
- 创建maven项目：`mvn archetype:generate`
- 验证项目是否正确：`mvn validate`
- maven 打包：`mvn package`
- 只打jar包：`mvn jar:jar`
- 生成源码jar包：`mvn source:jar`
- 产生应用需要的任何额外的源代码：`mvn generate-sources`
- 编译源代码：`mvn compile`
- 编译测试代码：`mvn test-compile`
- 运行测试：`mvn test`
- 运行检查：`mvn verify`
- 清理maven项目：`mvn clean`
- 生成eclipse项目：`mvn eclipse:eclipse`
- 清理eclipse配置：`mvn eclipse:clean`
- 生成idea项目：`mvn idea:idea`
- 安装项目到本地仓库：`mvn install`
- 发布项目到远程仓库：`mvn deploy`
- 在集成测试可以运行的环境中处理和发布包：`mvn integration-test`
- 显示maven依赖树：`mvn dependency:tree`
- 显示maven依赖列表：`mvn dependency:list`
- 下载依赖包的源码：`mvn dependency:sources`
- 安装本地jar到本地仓库：`mvn install:install-file -DgroupId=packageName -DartifactId=projectName -Dversion=version -Dpackaging=jar -Dfile=path`
- 生成项目相关信息的网站：`mvn site`
- 清除以前的包后重新打包，跳过测试类：`mvn clean package -Dmaven.test.skip=true`

### 1.3. web项目相关命令

- 启动tomcat：`mvn tomcat:run`
- 启动jetty：`mvn jetty:run`
- 运行打包部署：`mvn tomcat:deploy`
- 撤销部署：`mvn tomcat:undeploy`
- 启动web应用：`mvn tomcat:start`
- 停止web应用：`mvn tomcat:stop`
- 重新部署：`mvn tomcat:redeploy`
- 部署展开的war文件：`mvn war:exploded tomcat:exploded`

## 2. 手动添加 jar 包到本地 Maven 仓库

使用Maven的过程中，经常碰到有些jar包在中央仓库没有的情况。如果公司有私服，那么就把jar包安装到私服上。如果没有私服，那就把jar包安装到本地Maven仓库。有2种安装jar包到本地Maven仓库的方法

### 2.1. 使用 Maven 命令安装 jar 包

前提：在windows操作系统中配置好了Maven的环境变量。在windows的cmd命令下，参考下面安装命令安装jar包。注意：这个命令不能换行，中间用空格来分割的

安装指定文件到本地仓库命令：`mvn install:install-file`

- `-DgroupId=<groupId>`       : 设置项目代码的包名(一般用组织名)
- `-DartifactId=<artifactId>`  : 设置项目名或模块名
- `-Dversion=1.0.0`           : 版本号
- `-Dpackaging=jar `          : 什么类型的文件(jar包)
- `-Dfile=<myfile.jar>`       : 指定jar文件路径与文件名(同目录只需文件名)

安装命令实例：

```bash
$ mvn install:install-file -DgroupId=com.baidu -DartifactId=ueditor -Dversion=1.0.0 -Dpackaging=jar -Dfile=ueditor-1.1.2.jar
```

执行完命令后，可看到SUCCESS字样提示，代表安装成功，可以在本地仓库找到jar包

### 2.2. 使用 eclipse 安装 jar 包

使用eclipse安装也有个前提，就是eclipse的Maven要先配置好。具体操作：

1. File -->import -->Maven -->instal or deploy an artifact to a Maven repository

![](images/20201104152735125_9512.png)

2. 填写相关信息，如Maven坐标，具体参考下图。完成后点击Finish

![](images/20201104152759117_23200.png)

### 2.3. 两种方案的比较

方案一：缺点就是很麻烦，比如要配置环境变量，需要写很长的安装命令。不过，如果你配置好了环境变量，那就简单了。copy个安装的命令的示例，然后就直接安装了。

方案二：需要eclipse，个人推荐这种方式，比较简单。

## 3. 批量删除maven项目错误后生成`*.lastUpdated`文件

在项目运行错误后，在本地仓库下生成`.lastUpdated`文件，会影响项目的运行。使用批处理文件可以将里面文件删除，<font color=red>**注：将`REPOSITORY_PATH`变量改成本地仓库的路径**</font>

```bat
set REPOSITORY_PATH=D:\development\maven\repository
rem 正在搜索...
for /f "delims=" %%i in ('dir /b /s "%REPOSITORY_PATH%\*lastUpdated*"') do (
    del /s /q %%i
)
rem 搜索完毕
pause
```

复制以上代码，保存批处理命令`cleanLastUpdated.bat`文件

