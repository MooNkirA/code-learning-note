# Spring Boot 整合 Spring Cloud

## 1. 整合示例

### 1.1. 创建Maven父工程（pom）

- 采用Maven多Module的形式。创建Maven工程，在主Maven工和的pom文件引入Spring Boot的版本（根据需求定版本），Spring cloud版本（根据需求定版本），并可以指定一些公共的依赖，还有一些项目的公共属性（如项目的编码）
- 其余的Module工程的pom文件继承主Maven工程，拥有父工程的公共的依赖和配置
- 此案例是基于父工程通过Spring Boot整合Spring Cloud，单独使用Spring Boot工程整合Spring Cloud方法一样

父工程的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!-- 父项目的坐标 -->
    <groupId>com.moon</groupId>
    <artifactId>spring-cloud-project</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>spring-boot-demo</module>
        <module>其他模块</module>
        .......
    </modules>

    <!-- 引入Spring Boot的父项目依赖 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/><!-- lookup parent from repository -->
    </parent>

    <!-- 配置相关属性 -->
    <properties>
        <!-- 指定整个项目的编码是UTF-8 -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <!-- 配置JDK编译环境，因在是Spring Boot，不需要配置JDK插件 -->
        <java.version>1.8</java.version>
        <!-- 指定spring-cloud的版本 -->
        <spring-cloud.version>Dalston.RELEASE</spring-cloud.version>
    </properties>

    <!-- 锁定依赖的版本 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- 配置公共的依赖 -->
    <dependencies>
        <!-- Spring Boot应用程序用于测试包括JUnit，Hamcrest和Mockito -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

其他的子模块的pom.xml，继承父工程即可(下面是例子)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <!-- 配置继承父工程 -->
    <parent>
        <groupId>com.moon</groupId>
        <artifactId>spring-cloud-project</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <!-- 本模块坐标 -->
    <modelVersion>4.0.0</modelVersion>
    <artifactId>spring-boot-demo</artifactId>
    <packaging>jar</packaging>
    <name>spring-boot-demo</name>
    <description>Demo project for Spring Boot</description>

    <!-- 配置子模块的特有依赖 -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 1.2. 整合开发

更多配置与开发详见 spring cloud 示例项目 https://github.com/MooNkirA/spring-cloud-note

#### 1.2.1. RestTemplate 对象

从Spring3.0开始，Spring为创建Rest API提供了良好的支持。借助 RestTemplate，Spring应用能够方便地使用REST资源

RestTemplate定义了36个与REST资源交互的方法，其中的大多数都对应于HTTP的方法。

相关的方法

- `delete()` 在特定的URL上对资源执行HTTP DELETE操作
- `exchange()`
  - 在URL上执行特定的HTTP方法，返回包含对象的ResponseEntity，这个对象是从响应体中映射得到的
- `execute()` 在URL上执行特定的HTTP方法，返回一个从响应体映射得到的对象
- `getForEntity()` 发送一个HTTP GET请求，返回的ResponseEntity包含了响应体所映射成的对象
- `getForObject()` 发送一个HTTP GET请求，返回的请求体将映射为一个对象
- `postForEntity()`
  - POST 数据到一个URL，返回包含一个对象的ResponseEntity，这个对象是从响应体中映射得到的
- `postForObject()` POST 数据到一个URL，返回根据响应体匹配形成的对象
- `headForHeaders()` 发送HTTP HEAD请求，返回包含特定资源URL的HTTP头
- `optionsForAllow()` 发送HTTP OPTIONS请求，返回对特定URL的Allow头信息
- `postForLocation()` POST 数据到一个URL，返回新创建资源的URL
- `put()` PUT 资源到特定的URL


