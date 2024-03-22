## 1. Jackson 简介

### 1.1. 相关文档

- [Jackson Github](https://github.com/FasterXML/jackson)
- [Jackson Wiki](http://wiki.fasterxml.com/JacksonHome)
- [Jackson 文档](https://github.com/FasterXML/jackson-docs)

### 1.2. Jackson 的组件

#### 1.2.1. 核心模块

- Streaming：jackson-core jar，定义了底层的 streaming API 和实现了 Json 特性。
- Annotations：jackson-annotations jar，包含了标准的 Jackson 注解。
- Databind：jackson-databind jar，实现了数据绑定和对象序列化，它依赖于 streaming 和 annotations 的包

#### 1.2.2. 第三方数据类型模块

这些扩展是插件式的 Jackson 模块，用 `ObjectMapper.registerModule()` 注册，并且通过添加 serializers 和 deserializers 以便 Databind 包（ObjectMapper / ObjectReader / ObjectWriter）可以读写这些类型，来增加对各种常用的Java库的数据类型的支持。

#### 1.2.3. 数据格式模块

Jackson 也有处理程序对 JAX-RS 标准实现者例如 Jersey, RESTeasy, CXF 等提供了数据格式支持。处理程序实现了 MessageBodyReader 和 MessageBodyWriter，目前支持的数据格式包括 JSON, Smile, XML, YAML和 CBOR。

数据格式提供了除了 Json 之外的数据格式支持，它们绝大部分仅仅实现了 streaming API abstractions，以便数据绑定组件可以按照原来的方式使用。另一些（几乎不需要）提供了 databind 标准功能来处理，例如 schemas。

## 2. Jackson 基础使用

### 2.1. 引入 maven 依赖

```xml
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.10.1</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.10.1</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.10.1</version>
</dependency>
```

### 2.2. 序列化对象成 JSON 字符串

```java
public void toJson() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();

    City case1 = new City();
    case1.setCity("GZ");
    case1.setAge(123);

    String jsonStr = mapper.writeValueAsString(case1);
    System.out.println("JSON:" + jsonStr);
}
// 输出：JSON:{"city":"GZ","age":123}
```

### 2.3. 反序列化 JSON 字符串成 Java 对象

```java
public void toObj() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    String inputjsonstr = "{\"city\":\"GZ\",\"age\":123}";
    
    City readcase = mapper.readValue(inputjsonstr, City.class);

    System.out.println("city info:" + readcase);
}
```

如果里面有未知属性，比如 json 中有 desc 字段，但是 `City` 类中没有相应字段，会报错，需要如下设置：

```java
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
```

## 3. 常用注解

- `@JsonProperty("xxx")`：将当前的属性名在 json 字符串中重新命名为当前设置的值。比如将 age 转为 mAge
- `@JsonIgnore`：将被标注的属性在生成 json 字符串的时候，直接忽略。
- `@JsonInclude`：是一个类级别的设置，`JsonInclude.Include.NON_EMPTY` 标识只有非 NULL 的值才会被纳入 json string 之中，其余的都被忽略，比如这里的 location 属性，并没有出现在最终的结果字符串中。
- `@JsonSerialize`：使用自定义的类来实现自定义的字段转换。（用于写入操作）
- `@JsonDeserialize`：解析的时候，自定义的转换器。（用于读取操作）
- `@JsonAutoDetect`：设置类的访问策略，是否所有的属性都可以，还是按照一定的方式来提取。
- `@JsonRawValue`：无转换的将属性值写入到 json 字符串中。（用于写入操作）
- `@JsonValue`：标注方法，用以替代缺省的方法，由该方法来完成 json 的字符输出。
