## 1. FastJson 简介

Fastjson 是一个 Java 库，可以将 Java 对象转换为 JSON 格式，也可以将 JSON 字符串转换为 Java 对象。

Fastjson 可以操作任何 Java 对象，即使是一些预先存在的没有源码的对象。

但不推荐使用FastJson，原因如下：

- FastJson 源码质量较低
- FastJson Bug、漏洞较多
- FastJson 牺牲多数场景下的稳定性而提高的效率

### 1.1. 官方仓库与文档

- [Fastjson Github](https://github.com/alibaba/fastjson)
- [Fastjson 中文 Wiki](https://github.com/alibaba/fastjson/wiki/Quick-Start-CN)

### 1.2. Fastjson 特性

- 提供服务器端、安卓客户端两种解析工具，性能表现较好。
- 提供了 `toJSONString()` 和 `parseObject()` 方法来将 Java 对象与 JSON 相互转换。
    - `toJSONString()` 方法：将对象转换成 JSON 字符串。
    - `parseObject()` 方法：将 JSON 字符串转换成对象。
- 允许转换预先存在的无法修改的对象（只有 class、无源代码）。
- Java 泛型的广泛支持。
- 允许对象的自定义表示、允许自定义序列化类。
- 支持任意复杂对象（具有深厚的继承层次和广泛使用的泛型类型）。

## 2. FastJson 基础使用

### 2.1. 引入 maven 依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>x.x.x</version>
</dependency>
```

其中 `x.x.x` 是版本号，根据需要使用特定版本，建议使用最新版本。

### 2.2. 序列化对象成 JSON 字符串

```java
User user = new User();
user.setName("校长");
user.setAge(3);
user.setSalary(new BigDecimal("123456789.0123"));
String jsonString = JSON.toJSONString(user);
System.out.println(jsonString);
// 输出 {"age":3,"name":"校长","old":false,"salary":123456789.0123}
```

### 2.3. 反序列化 JSON 字符串成 Java 对象

```java
String jsonString = "{\"age\":3,\"birthdate\":1496738822842,\"name\":\"校长\",\"old\":true,\"salary\":123456789.0123}";
 User u = JSON.parseObject(jsonString ,User.class);
 System.out.println(u.getName());
 // 输出 校长

String jsonStringArray = "[{\"age\":3,\"birthdate\":1496738822842,\"name\":\"校长\",\"old\":true,\"salary\":123456789.0123}]";
List<User> userList = JSON.parseArray(jsonStringArray, User.class);
System.out.println(userList.size());
// 输出 1
```

### 2.4. 对于日期的处理

默认序列化 `Date` 对象输出使用 `yyyy-MM-dd HH:mm:ss` 格式，可以用 `UseISO8601DateFormat` 特性换成 `yyyy-MM-dd` 或者 `HH:mm:ss` 格式。

```java
JSON.defaultTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
JSON.defaultLocale = Locale.US;
        
public static class Model {

    @JSONField(format = "MMM dd, yyyy h:mm:ss aa")
    private java.util.Date date;

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    @JSONField(format = "MMM-dd-yyyy h:mm:ss aa")
    public java.sql.Date date2;
}
```

### 2.5. BeanToArray

在 fastjson 中，支持一种叫做 BeanToArray 的映射模式。普通模式下，JavaBean 映射成 json object，BeanToArray 模式映射为 json array。

Sample 1：在 BeanToArray 模式下，少了 Key 的输出，节省了空间，json 字符串较小，性能也会更好。

```java
class Model {
   public int id;
   public String name;
}

Model model = new Model();
model.id = 1001;
model.name = "gaotie";

// {"id":1001,"name":"gaotie"}
String text_normal = JSON.toJSONString(model); 

// [1001,"gaotie"]
String text_beanToArray = JSON.toJSONString(model, SerializerFeature.BeanToArray); 

// support beanToArray & normal mode
JSON.parseObject(text_beanToArray, Feature.SupportArrayToBean); 
```

Sample 2：BeanToArray 可以局部使用。如果 Company 的属性 departments 元素很多，局部采用 BeanToArray 就可以获得很好的性能，而整体又能够获得较好的可读性。

```java
class Company {
     public int code;
     public List<Department> departments = new ArrayList<Department>();
}

@JSONType(serialzeFeatures=SerializerFeature.BeanToArray, parseFeatures=Feature.SupportArrayToBean)
class Department {
     public int id;
     public String name;
     public Department() {}
     public Department(int id, String name) {this.id = id; this.name = name;}
}


Company company = new Company();
company.code = 100;
company.departments.add(new Department(1001, "Sales"));
company.departments.add(new Department(1002, "Financial"));

// {"code":10,"departments":[[1001,"Sales"],[1002,"Financial"]]}
String text = JSON.toJSONString(company); 
```

Sample 3：示例2的改进版

```java
class Company {
     public int code;

     @JSONField(serialzeFeatures=SerializerFeature.BeanToArray, parseFeatures=Feature.SupportArrayToBean)
     public List<Department> departments = new ArrayList<Department>();
}
```

### 2.6. 设置字段名

```java
public class A {
    @JSONField(name="ID")
    private int id;

    public int getId() {return id;}
    public void setId(int value) {this.id = id;}
}
```

### 2.7. 设置是否不序列化某字段

```java
public class A {
    @JSONField(serialize=false)
    public Date date;
}

public class A {
    @JSONField(deserialize=false)
    public Date date;
}
```

### 2.8. 设置字段顺序

```java
public static class VO {
    @JSONField(ordinal = 3)
    private int f0;

    @JSONField(ordinal = 2)
    private int f1;

    @JSONField(ordinal = 1)
    private int f2;
}
```

### 2.9. 自定义序列化和反序列化

> 参考资料：
>
> - [fastjson SerializerFeature 详解](https://blog.csdn.net/u010246789/article/details/52539576)
> - [ObjectDeserializer_cn](https://github.com/alibaba/fastjson/wiki/ObjectDeserializer_cn)
