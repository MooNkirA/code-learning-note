## 1. Gson介绍

Gson 是 Google 提供的用来在 Java 对象和 JSON 数据之间进行映射的 Java 类库。可以将一个 Json 字符转成一个 Java 对象，或者将一个 Java 转化为 Json 字符串。

特点：

1. 快速、高效
2. 代码量少、简洁
3. 面向对象
4. 数据传递和解析方便

### 1.1. 依赖包

- Gson工具类，需要导入jar包：`gson-2.2.4.jar`

## 2. Gson的创建方式

### 2.1. 无参构造方法

- `Gson gson = new Gson();`


## 3. Gson的基本用法

- `public String toJson(Objcet obj)`
    - 将任意类型转换成Json格式字符串
- `public T fromJson(String jsonStr, T.class)`
    - 将json字符串转化为指定类型为T的Java对象

## 4. Gson的一些案例
### 4.1. 案例-输出JSON字符串包含null值的属性

```java
public static void main(String[] args) {
   HashMap<String, Object> map = new HashMap<String, Object>();
   map.put("name", "mafly");
   map.put("age", "18");
   map.put("sex", null);

   String jsonString = new Gson().toJson(map);
   System.err.println(jsonString);
}
```

就是这个当`Sex=null`时，用Gson的tojson方法会把null值忽略，从而序列化之后输出不包含这个属性值的Json串。其实预期是输出`{"sex": ""}`或`{"sex": null}`，那怎么做呢？

**通过搜索引擎发现，网上大概存在以下三种解决方案：**

- 调用 `toJson(Object src, Type typeOfSrc, JsonWriter writer)` 方法
- 注册自定义 `TypeAdapter`
- 设置 `serializeNulls` 属性值（推荐)

使用第三种方式，源码如下：

```java
GsonBuilder gsonBuilder = new GsonBuilder();
String jsonString1 = gsonBuilder.serializeNulls().create().toJson(map);
System.err.println(jsonString1);
```

输出符合需求了，`{"sex": null}`

因为调用serializeNulls()后，Gson设置serializeNulls属性值为true，默认是false，源码:

```java
public GsonBuilder serializeNulls() {
   this.serializeNulls = true;
   return this;
}
```

#### 4.1.1. Gson其他使用要点

可以用@SerializedName注解给属性重命名，用@Expose注解标识属性不进行序列化，支持 Map 的 key 为复杂对象的形式，日期类型转化为特定格式，还有区分版本进行显示，这些在日常项目中都极常用到，只不过好像我们之前都是自己实现的。

简单建立了一个 UserInfo 实体类，里边包含了username、age、sex这三个属性值。试着测试了一下其中一两个：

1. `@Expose`想让谁输出谁输出，注解代码如图：

![Gson其他使用要点1](images/20190218234015564_5282.png)

设置excludeFieldsWithoutExposeAnnotation()，看下面第三行是输出

![Gson其他使用要点2](images/20190218234031364_17103.jpeg)

2. `@SerializedName`想怎么显示怎么显示，注解代码如图：

![Gson其他使用要点3](images/20190218234116016_20635.jpeg)

想把age再 Json序列化时显示maflyAge，现在就可以了，输出如下图

![Gson其他使用要点4](images/20190218234133891_22510.jpeg)

3. `@Since`、`@Until`不同版本不同数据，注解代码如图

![Gson其他使用要点5](images/20190218234226464_702.jpeg)

这里要设置setVersion(3.2)，`@Since`标注的在3.2版本或之后才会输出，`@Until`标注的只在3.2版本前才有。效果输出如下图

![Gson其他使用要点6](images/20190218234247603_1829.jpeg)

当然，还有好多特性呢，比如字段首字母大写阿、结果格式化阿等等

## 5. 参考资料

- [常用开发库 - JSON库详解](https://www.pdai.tech/md/develop/package/dev-package-x-json.html#gson%E7%AE%80%E4%BB%8B)
- [Gson用户指南（中文翻译）](https://www.jianshu.com/p/1e20b28c39d1)
