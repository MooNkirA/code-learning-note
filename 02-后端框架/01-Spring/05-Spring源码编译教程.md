# Spring 源码编译教程

## 1. Gradle 项目构建工具

Spring 源码是使用 Gradle 进行构建，需要下载与安装 Gradle。

Gradle官网：https://gradle.org/

> 具体 Gradle 安装详见[《Gradle基础笔记》](/05-DevOps工具/01-项目构建工具/03-Gradle基础笔记)

## 2. Spring 源码编译流程

> 个人编译是使用 IDEA，可以直接参考源码工程中的《import-into-idea.md》文档

### 2.1. 修改源码项目配置

1. 进入 Spring 源码的根目录，打开 gradle/wrapper/gradle-wrapper.properties 配置文件。其中配置项说明如下：

- distributionUrl：表示的就是 gradle 的下载地址，默认配置的是一个远程 URL。
- distributionBase：下载后存放的目录，默认就是用户目录下的 .gradle 目录；
- zipStoreBase：解压后存放的目录

构建的过程中发现配置的下载地址速度非常慢，因此可以先下载 gradle 的压缩包到本地，然后修改配置文件从本地获取 gradle，这样一来编译构建就会快很多了。所以将配置修改为

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=file\D:/dev/spring-sample/gradle-7.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

上述配置的意思就是如果运行 gradlew 脚本会自动去 `D:/dev/spring-sample/gradle-7.5-bin.zip` 这个目录下载一个 gradle 到当前用户的 .gradle\wrapper\dists 目录并解压

> *注：windows 系统的路径写法待确认*

2. 修改源码项目的 `settings.gradle` 文件，增加阿里云仓库 spring 插件库

```gradle
pluginManagement {
	repositories {
    	// 阿里云仓库 spring-plugin (旧版)
        // maven { url 'https://maven.aliyun.com/repository/public' }
        // 阿里云仓库 spring-plugin (新版)
        maven { url 'https://maven.aliyun.com/repository/spring-plugin' }
		gradlePluginPortal()    
		maven { url 'https://repo.spring.io/plugins-release' }
	}
}
```

3. 修改源码项目的 `gradle.properties` 文件，修改一些参数配置

```properties
version=5.3.10
#org.gradle.jvmargs=-Xmx1536M
org.gradle.jvmargs=-Xmx2048M
org.gradle.caching=true
org.gradle.parallel=true
kotlin.stdlib.default.dependency=false
```

4. 修改源码项目的`build.gradle`文件，增加阿里云仓库

```gradle
repositories {
    // 添加 maven 本地仓库
    mavenLocal()
    // 阿里云仓库地址(老版)
	// maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
	// maven { url 'https://maven.aliyun.com/nexus/content/repositories/jcenter'}
    // 阿里云仓库地址(新版)
	maven { url 'https://maven.aliyun.com/repository/public' }
	maven { url 'https://maven.aliyun.com/repository/central'}
	mavenCentral()
    maven { url "https://repo.spring.io/libs-spring-framework-build" }
}
```

### 2.2. 预编译项目

参考源码工程提供的导入参考文档《import-into-idea.md》，在导入 idea 之前需要进行预编译。进入 Spring 源码工程根目录，执行以下 `gradle` 命令对 spring-oxm 与 spring-core 这两个模块进行预编译

```bash
./gradlew :spring-oxm:compileTestJava
./gradlew :spring-core:compileTestJava
```

> Tips: 如果使用 cmd 命令行窗口，可以省略“`./`”

编译模块，编译成功后会有“BUILD SUCCESSFUL”的提示。

![](images/414334014239268.png)

### 2.3. 导入源码

导入IDEA (File -> New -> Project from Existing Sources -> Navigate to directory -> Select build.gradle)

使用 IDEA 导入 Spring 源码，idea 默认会根据 ${spring.dir}\gradle\wrapper\gradle-wrapper.properties 文件的配置，自动下载 gradle，下载完 gradle 就会开始下载 Spring 源码依赖。若使用本机安装好的 gradle，可以直接取消这个过程。

![](images/20211010161333988_21107.png)

> Notes: 
>
> 1. 用 idea 打开 spring 源码工程，需要检查是否已经安装 kotlin 插件。如果安装，则需要重启 idea
> 2. 需要先按上一个步骤编译工程后再导入

### 2.4. IDEA 的 Gradle 配置

打开【Settings】面板，选择【Build, Execution, Deployment】->【Gradle】

- 设置【Gradle user home】，用于设置 Gradle 缓存、依赖、.gradle 压缩包的解压之后的文件等保存路径。比如设置为 `D:\.gradle`
- 设置【Build and run using】和【Run tests using】
    - `Gradle (Default)`：使用 Gradle 来构建项目和运行任务。
    - `IntelliJ IDEA`：选择使用 IDEA 来构建和运行项目，对于一些纯JAVA/Kotlin项目，使用这种方式可能会更快。但 IDEA 并不是支持所有 Gradle 插件，项目如果使用其中一些插件可能无法正确构建项目。

![](images/238292916220769.png)

> Tips: 此处如果测试使用 idea 进行编译运行没有问题，则推荐使用 idea 编译，会比使用 gradle 运行编译快。

- 设置【Use Gradle from】，用于配置使用的 Gradle 位置，具体选项如下：
    - `'gradle-wrapper.properties' file`：使用导入工程中的 gradle-wrapper.properties 文件中配置
    - `'wrapper' task in Gradle build script`：使用 Gradle 构建脚本中的'wrapper'任务
    - `Specified location`：指定 Gradle 的安装位置

最终配置示例如下（按需修改各个配置项）：

![](images/20211017141206871_19426.png)

保存修改后，会自动触发 gradle 的重新编译。

![](images/20211010163626944_11580.png)

如无自动触发编译，可以手动点击刷新

![](images/20211010163735375_1007.png)

正常情况下，此时gradle编译将比较快，会有一个索引文件过程，但是不需要额外的下载gradle和依赖了。编译成功效果如下：

![](images/20211010172758571_9328.png)

### 2.5. 排除 spring-aspects 模块

在源码编译的过程中，可能会出现以下错误：

![](images/157333322220769.png)

是因为 `spring-aspects` 模块编译时是依赖ajc编译器，这个 IntelliJ IDEA 没有默认安装。只要将 spring-aspects 模块排除即可。排除的步骤如下：

1. 右键项目，选择【Load/Unload Modules...】

![](images/356493822239195.png)

2. 选择 spring-aspects 模块 unloaded

![](images/422153922227062.png)

3. 修改工程的根目录的 settings.gradle 文件，移除 spring-aspects 模块

![](images/316384222247228.png)

4. 点击刷新，重新编译。

![](images/486434322239897.png)

### 2.6. AspectJ 编译器（非必须）

安装 AspectJ 编译器插件是为了解决使用 AOP 相关测试

#### 2.6.1. 下载与安装

AspectJ 官网下载 AspectJ 编译器

![](images/442705522236452.png)

值得注意：官网下载页面中，只有1.9.6版本，1.9.7往后的版本都在github仓库发布

- 1.9.6 及以前版本，下载地址：https://www.eclipse.org/aspectj/downloads.php
- 1.9.7+ 版本，下载地址：https://github.com/eclipse/org.aspectj/releases/

下载完成后，进入 jar 包所在目录吕，执行以下命令安装ajc编译器。

```bash
java -jar aspectj-1.9.5.jar
```

选择jdk目录

![](images/121430323232206.png)

![](images/269190323250086.png)

选择安装的目录安装即可

![](images/598140323247690.png)

#### 2.6.2. IDEA 设置 AspectJ 编译器

1. 打开 Idea，先安装 Aspectj 插件，并启用此插件

![](images/36420623245192.png)

2. 将 IDEA 编译器设置为 ajc，然后设置 AspectJ 安装目录下的 lib 文件夹中的 aspectjtools.jar 文件，再勾选【Delegate to Javac】选项。

![](images/573311123226433.png)

3. 点选【File】->【Project Structure】->【Modules】，将 spring-aop_main 和 spring-aspectjs_main 两个模块添加 AspectJ Facets，

![](images/496801323248873.png)

4. 把项目重新 build project，然后再成功运行 AOP 相关测试。即源码中可以使用 aspectj aop 了。

## 3. Spring 各版本的下载地址

### 3.1. Spring 5.3.10

此教程是基于个人 git 库的 Spring 5.3.10 源码编译的，并不是 Github 原生的 Spring 5.3.10 源码，有一些差别，但都是 gradle 配置文件的微小改动，比如把某些依赖从 optional 改成 compile 级别（主要是为了方便编译），其他都没改动。

编译使用的 IDEA 版本是 2021.1.3，用其他 IDEA 版本可能会遇到各种各样的问题。

git clone 的地址为：

- 源码：https://gitee.com/mirrors/Spring-Framework/tree/v5.3.10
- 个人注释版：https://github.com/MooNkirA/spring-note

### 3.2. Spring 5.2.8.RELEASE

- 源码下载地址：https://github.com/spring-projects/spring-framework
- 国内镜像：https://gitee.com/mirrors/spring-framework
- 目前源码学习的笔记更新至spring-framework 5.2.8.RELEASE，下载地址：https://gitee.com/mirrors/Spring-Framework/tree/v5.2.8.RELEASE

## 4. 注释的源码引用

### 4.1. 方式一：直接在源码工程中创建测试代码

像 maven 工程一样，在源码工程中创建自定义的测试示例等模块，直接依赖本源码工程各个模块即可。

> Tips: 此方式就很方便边看源码，边调试。但需要有 Gradle 工具的相关知识。

1. 创建新的 Grdle 模块

![](images/400172017239198.png)

2. 设置子模块的名称

![](images/463542617227065.png)

3. 修改自定义模块的 build.gradle 配置，引入源码中核心模块的依赖

```
plugins {
    id 'java'
}

group 'org.springframework'
version '5.3.10'

repositories {
    mavenCentral()
}

dependencies {
    // spring 核心依赖
    // compile(project(':spring-context')) // 注意：compile 语句已弃用，并已在 Gradle 7.0 中删除。
    // 后面版本改用 implementation 或 api
    implementation project(':spring-context')

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
}

test {
    useJUnitPlatform()
}
```

> Notes: 如果没有引入依赖时，在 Gradle 面板中刷新一下。还就是已经使用 Gradle 7.5 版本，因此 `compile` 语句已经被移除，改使用 `implementation` 引入依赖

4. 确认项目根目录的 settings.gradle 是否引入自定义创建的子模块

![](images/562032917247231.png)

5. 在子模块中编写测试用例，并观察在 Spring 源码中的断点是否生效

![](images/438273814220842.png)

### 4.2. 方式一：测试的工程引入编译打包后的源码包

把编译好的源码导入到工程中，这样可以在源码中写注释并且断点调试源码了。如果想让其他工程（或者maven工程）依赖自己注释过的源码包，需要进行如下操作：

1. 使用 gradle 在将相关模块的源码打成 jar

![](images/20201226100102742_32136.png)

2. 在【Project】侧边栏中的【External Libraries】，选择源码测试工程所依赖的 spring 相应的版本，右键选择【Open Library Settings】

![](images/20201226101626229_17268.png)

3. 选择【Libraries】里的 spring 源码包，在 Classes、Sources、Annotations 中选择编译打包后的源码

![](images/20201226102200321_32128.png)

4. 选择 Classes、Sources，删除原来maven仓库的jar，改成使用源码编译后jar包

![](images/20201226102458984_31395.png)

![](images/20201226102637109_31955.png)

![](images/20201226102647171_22314.png)

> Tips: 这种方式唯一的好处就是可以使用 maven 来构建测试工程，然后引用编译后 class 包。但这种方式不好的地方是，如果源码有任何修改，需要重新编译打包，示例项目需要重新进行引用。

## 5. 编译与测试可能出现的问题

### 5.1. 程序包jdk.jfr不存在

下载源码后，修改了 Gradle 配置后，执行 `gradlew :spring-oxm:compileTestJava` 命令可能会报以下的错误

![](images/542033408220771.png)

这个问题是因为 `jdk.jfr` 是 java9 后才有的模块，而 spring 源码 5.3.10 版本已经使用这个包，所以需要设置项目配置的 jdk 版本高于 9。目前本人是直接将本地换成的 JDK 11，然后就可以成功编译。注意IDE需要修改以下几个位置

![](images/66995010247763.png)

![](images/236405010245265.png)

![](images/525815010226506.png)

### 5.2. Gradle 版本

如果在编译的时候出现以下问题，可以尝试升级 Gradle

![](images/220920023220772.png)

### 5.3. 测试模块无法找到其他子模块

在创建测试模块可能出现如下的错误，这是因为创建的模块位置不正确，最快的解决方法是，将模块删除，然后在 spring 源码目录下创建新的模块即可。

![](images/119185409236525.png)

### 5.4. build 构建模块时出现 checkstyleMain

如果 build 之后可能会出现了错误，直接忽略即可

![](images/156384710250159.png)

## 6. 参考资源

> - [手把手带你编译Spring框架源码，让你的学习事半功倍](https://zhuanlan.zhihu.com/p/380397684)
> - 参考（待删）：https://blog.csdn.net/weixin_41657488/article/details/110223801