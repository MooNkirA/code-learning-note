# Guava（Google基于Java类库扩展）

Guava 是一个 Google 开发的 基于 java 的类库集合的扩展项目，包括 collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, 等等. 这些高质量的 API 可以使你的JAVA代码更加优雅，更加简洁

## 1. Guava 简介
### 1.1. 类库基础包

- annotations：普通注解类型
- base：基本工具类库和接口
- Cache：缓存工具包，非常简单易用且功能强大的JVM内缓存
- collect：带泛型的集合接口扩展和实现，以及工具类，这里你会发现很多好玩的集合
- eventbus：发布订阅风格的事件总线
- hash： 哈希工具包
- io：IO工具包
- math：数学计算工具包
- net：网络工具包
- primitives：八种原始类型和无符号类型的静态工具包
- reflect：反射工具包
- concurrent：并发编程工具包
- escape：转义工具
- graph：处理基于图的数据结构
- HTML：Html字符串转义
- XML：xml字符串转义

## 2. Guava 基础使用
### 2.1. maven 依赖

```xml
<!-- Google Guava 类库 https://mvnrepository.com/artifact/com.google.guava/guava -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>28.0-jre</version>
</dependency>
```


