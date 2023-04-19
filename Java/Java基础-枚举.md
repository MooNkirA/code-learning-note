# Java基础 - 枚举(Enum)

## 1. 枚举的概述

枚举类型是在JDK1.5后的新特性。<font color=red>**本质就是一个类，所有自定义的枚举类都默认是Enum的子类**</font>

> 关于枚举，阿里巴巴开发手册有这样两条建议：
>
> 1. 枚举类名带上 `Enum` 后缀，枚举成员名称需要全大写，单词间用下划线隔开。
> 2. 如果变量值仅在一个固定范围内变化用 `enum` 类型来定义。

### 1.1. JDK1.5之前实现类似枚举的功能（了解）

JDK1.5之前的解决输入非法成员变量值的方法：重新定义一个成员变量的类，在定义成员变量的时候，使用这个相应的成员变量类。将成员变量的类的构造方法私有，不能让使用者new对象。

<font color=red>**JKD1.5之后的解决方法：定义一个枚举类，解决调用构造方法创建对象时输入非法成员变量的情况。**</font>

### 1.2. Enum 类

```java
public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable
```

这是所有 Java 语言枚举类型的公共基本类

### 1.3. 枚举的作用

1. 提高代码的可读性
2. 控制某一数据类型的值不能乱写，避免产生垃圾值，保证数据有效性

### 1.4. 枚举的使用场景

当数据类型的值只能在给定的范围内进行选择时(数量不能太多的时候)。比如：性别、季节、月份、星期…

## 2. 枚举的基础使用

### 2.1. 枚举类的定义格式

```java
enum 枚举名称 {
    成员名称1, 成员名称2, 成员名称3
}
```

<font color=red>**枚举的底层实现，枚举的底层是一个类继承了`Enum`**</font>

### 2.2. 枚举的使用步骤

1. 定义枚举类
2. 在成员变量类型上面使用枚举类型
3. 设置枚举值(如`WeekDay.FRI`)，语法即`枚举名称.成员`
4. 可以做枚举比较`e.getResetDay() == WeekDay.STA`

总结：<font color=red>**枚举的作用是用来表示几个固定的值，可以使用枚举中成员**</font>

## 3. 枚举常用方法

```java
public final String name();
```

- 获得枚举名，返回此枚举常量的名称

```java
public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name)
```

- 根据枚举名字符串获得枚举值对象。返回带指定名称的指定枚举类型的枚举常量。名称必须与在此类型中声明枚举常量所用的标识符完全匹配。（不允许使用额外的空白字符。）*与通过"枚举类名.枚举项名称"去访问指定的枚举项得到相同的枚举对象*

```java
public final int ordinal()
```

- 返回此枚举常量的顺序（位置在枚举声明，在初始常数是零分序号）。不推荐使用，它被设计用于复杂的基于枚举的数据结构，比如 `EnumSet` 和 `EnumMap`

```java
public final int compareTo(E o)
```

- 比较此枚举与指定对象的顺序(索引值)，返回索引值的差值。在该对象小于、等于或大于指定对象时，分别返回负整数、零或正整数。

```java
public String toString()
```

- 返回枚举常量的名称

```java
public static <T extends Enum<T>> T[] values();
```

- 枚举中的一个特殊方法，可以将枚举类转变为一个该枚举类型的数组。<font color=red>*此方法虽然在JDK文档中查找不到，但每个枚举类都具有该方法，它遍历枚举类的所有枚举值非常方便*</font>

## 4. 枚举的特点与综合示例

- 定义枚举类要用关键字 `enum`
- 所有枚举类都是 `Enum` 的子类（默认是`Enum`的子类，不需要（能）再写`extends Enum`）
- 每一个枚举项其实就是该枚举的一个对象，通过 `枚举类名.枚举项名称` 方式去访问指定的枚举项
- 枚举也是一个类，也可以去定义成员变量
- 枚举值必须是枚举类的第一行有效语句。多个枚举值必须要用逗号(`,`)分隔。最后一个枚举项后的分号是可以省略的，但是如果枚举类有其他的东西，这个分号就不能省略。**建议不要省略**
- 枚举类可以有构造方法，但必须是`private`修饰的，它默认的也是 `private` 的
- 枚举项的用法比较特殊：可以定义为`枚举名称("xxx")`，但定义构造方法
- 枚举类也可以有抽象方法，但是枚举项必须重写该方法

```java
public class EnumDemo {

    // 普通枚举
    enum ColorEnum {
        RED, GREEN, BLUE;
    }

    // 带属性的枚举，示例中的数字就是延伸信息，表示一年中的第几个季节。
    enum SeasonEnum {
        SPRING(1), SUMMER(2), AUTUMN(3), WINTER(4);

        private final int seq;

        SeasonEnum(int seq) {
            this.seq = seq;
        }

        public int getSeq() {
            return seq;
        }
    }

    // 带抽象方法枚举，示例中的构造方法为类型的中文名称，在定义枚举值时需要实现抽象方法
    enum PayTypeEnum {
        WX_PAY("微信支付") {
            @Override
            public void doPay(BigDecimal money) {
                System.out.println("微信支付: " + money);
            }
        }, ALI_PAY("支付宝支付") {
            @Override
            public void doPay(BigDecimal money) {
                System.out.println("支付宝支付: " + money);
            }
        };

        private final String name;

        PayTypeEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        // 定义抽象方法
        public abstract void doPay(BigDecimal money);
    }
}
```

## 5. 枚举底层实现

### 5.1. 枚举编译后的代码

创建一个`ColorEnum`的枚举类，通过编译，再反编译看看它发生了哪些变化。

```java
public enum ColorEnum {
    RED,GREEN,BULE;
}
```

使用命令`javac ColorEnum.java`进行编译生成class文件，然后再用命令`javap -p ColorEnum.class`进行反编译。

![](images/20201105082910802_4924.png)

去掉包名，反编译后的内容如下：

```java
public final class ColorEnum extends Enum{
    public static final ColorEnum GREEN;
    public static final ColorEnum BULE;
    private static final ColorEnum[] $VALUES;
    public static ColorEnum[] values();
    public static ColorEnum valueOf(java.lang.String);
    private ColorEnum();
    static {};
}
```

### 5.2. 枚举源码特点总结

1. 枚举类被`final`修饰，因此枚举类不能被继承；
2. 枚举类默认继承了`Enum`类，java不支持多继承，因此枚举类不能继承其他类；
3. 枚举类的构造器是`private`修饰的，因此其他类不能通过构造器来获取对象；
4. 枚举类的成员变量是`static`修饰的，可以用`类名.变量`来获取对象；
5. `values()`方法是获取所有的枚举实例；
6. `valueOf(java.lang.String)`是根据名称获取对应的实例；

## 6. 枚举的应用示例

### 6.1. 枚举使用案例 - 消除if/else

假如要写一套加密接口，分别给小程序、app和web端来使用，但是这三种客户端的加密方式不一样。一般情况下我们会传一个类型type来判断来源，然后调用对应的解密方法即可。代码如下：

```java
if ("WEIXIN".equals(type)) {
    // dosomething
} else if ("APP".equals(type)) {
    // dosomething
} else if ("WEB".equals(type)) {
    // dosomething
}
```

使用枚举来代替if/else。写一个加密用的接口，有加密和解密两个方法。然后用不同的算法去实现这个接口完成加解密。

```java
public interface Util {
    // 解密
    String decrypt();

    // 加密
    String encrypt();
}
```

创建一个枚举类来实现这个接口

```java
public enum UtilEnum implements Util {

    WEIXIN {
        @Override
        public String decrypt() {
            return "微信解密";
        }

        @Override
        public String encrypt() {
            return "微信加密";
        }
    },
    APP {
        @Override
        public String decrypt() {
            return "app解密";
        }

        @Override
        public String encrypt() {
            return "app加密";
        }
    },
    WEB {
        @Override
        public String decrypt() {
            return "web解密";
        }

        @Override
        public String encrypt() {
            return "web加密";
        }
    };
}
```

最后，获取到type后，直接可以根据type调用解密方法即可

```java
String decryptMessage = UtilEnum.valueOf(type).decrypt();
```

### 6.2. 枚举创建线程安全的单例模式（扩展应用）

```java
public enum  SingletonEnum {

    INSTANCE;

    public void doSomething(){
        // dosomething...
    }
}
```

这样一个单例模式就创建好了，通过`SingletonEnum.INSTANCE`来获取对象就可以了。

#### 6.2.1. 序列化造成单例模式不安全

一个类如果如果实现了序列化接口，则可能破坏单例。每次反序列化一个序列化的一个实例对象都会创建一个新的实例。

枚举序列化是由JVM保证的，每一个枚举类型和定义的枚举变量在JVM中都是唯一的，在枚举类型的序列化和反序列化上，Java做了特殊的规定：在序列化时Java仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过`java.lang.Enum`的`valueOf`方法来根据名字查找枚举对象。同时，编译器是不允许任何对这种序列化机制的定制的并禁用了`writeObject`、`readObject`、`readObjectNoData`、`writeReplace`和`readResolve`等方法，从而保证了枚举实例的唯一性。

#### 6.2.2. 反射造成单例模式不安全

通过反射强行调用私有构造器来生成实例对象，造成单例模式不安全。

```java
Class<?> aClass = Class.forName("xx.xx.xx");
Constructor<?> constructor = aClass.getDeclaredConstructor(String.class);
SingletonEnum singleton = (SingletonEnum) constructor.newInstance("Demo");
```

但是使用枚举创建的单例完全不用考虑这个问题，以下为newInstance的源码

```java
public T newInstance(Object ... initargs)
    throws InstantiationException, IllegalAccessException,
IllegalArgumentException, InvocationTargetException
{
    if (!override) {
        if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
            Class<?> caller = Reflection.getCallerClass();
            checkAccess(caller, clazz, null, modifiers);
        }
    }
    // 如果是枚举类型，直接抛出异常，不让创建实例对象！
    if ((clazz.getModifiers() & Modifier.ENUM) != 0)
        throw new IllegalArgumentException("Cannot reflectively create enum objects");
    ConstructorAccessor ca = constructorAccessor;   // read volatile
    if (ca == null) {
        ca = acquireConstructorAccessor();
    }
    @SuppressWarnings("unchecked")
    T inst = (T) ca.newInstance(initargs);
    return inst;
}
```

如果是`enum`类型，则直接抛出异常`Cannot reflectively create enum objects`，无法通过反射创建实例对象！

## 7. 枚举的治理

### 7.1. 概述

#### 7.1.1. 为什么要使用枚举？

表中某个字段标识了这条记录的状态，通常会使用一些code值来标识，例如01代表成功，00代表失败。多状态共性的东西可以常量保存，例如：

```java
class Constants{
    public static final String success = "01";
    public static final String failure= "00";
}
```

然而在一些大型项目中，表的数量极多，一些表中需要维护的状态也极多，如果都在一个常量类中维护，当需要添加或修改一个状态值时，就需要在庞大的类中找到对应的块，操作十分不便利。因此可以使用枚举，每个枚举类就只负责对一个状态做维护，这样即可方便增删改。例如：

```java
public enum Payment {
    Payment_WX("010000", "微信支付"),
    Payment_ZFB("010001", "支付宝支付"),
    Payment_YL("010002", "银联支付");

    public static final Map<String, String> map = new HashMap<>();

    static {
        Payment[] values = Payment.values();
        if (values.length > 0) {
            for (Payment product : values) {
                map.put(product.getCode(), product.getName());
            }
        }
    }

    Payment(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;
	// ...省略 getter/setter
}
```

#### 7.1.2. 为什么需要枚举治理？

如果使用常量类，可以直接通过这个类的静态字段拿到值。当使用枚举时，尤其当枚举类逐渐增多时，此时不同的业务功能就可能需要获取不同的枚举类，然后再通过不同的枚举实例获取到不同的值。这种操作又会变得十分不便利。此时有如下的改进方法：

- 改进一：以上面示例为例，把每个枚举类中实例存入到 map 集合中，把其 code 和 name 值映射进去，然后调用时通过静态 map 对象，把 code 值作为 key 传入，即可获取到对应的描述。

> 然而以上的改进后，依旧需要找到相应的枚举类，然后去使用它的静态 Map 集合的成员属性，能不能只通过一个类进行统一治理呢？

- 改进二：通过一个类，把所有枚举都在该类中注册，然后通过该类直接获取到相应的枚举值及name描述。

### 7.2. 枚举治理的实现

#### 7.2.1. 枚举的场景说明

1. 通过枚举类中枚举名获取到枚举的code值，使用上面的枚举值定义为示例：`{"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}`

```java
if(param.equals(Payment.Payment_WX.getCode()){}
```

2. 通过枚举类中枚举的code值获取到对应的name描述，使用上面的枚举值定义为示例：`{"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}`

```java
Payment.map.get(Payment.Payment_WX.getCode());
```

#### 7.2.2. 枚举治理工具类的实现

```java
public class VelocityEnumTools {
    public static final Logger logger = LoggerFactory.getLogger(VelocityEnumTools.class);


    // 通过枚举获取枚举code值，例如：{"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}
    public static Map<String, Map<String, String>> mapKeyCode = new HashMap<>();

    // 通过code值获取枚举name，例如：{"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}
    public static Map<String, Map<String, String>> mapCodeName = new HashMap<>();

    /**
     * 初始化项目中需要管理的枚举类，如Payment。其他枚举也类似添加即可
     */
    static {
        // 通过枚举获取code值
        mapKeyCode.put(Payment.class.getSimpleName(), getEnumMap(Payment.class));
        // 通过code值获取枚举name
        mapCodeName.put(Payment.class.getSimpleName(), getEnumCodeMap(Payment.class));
    }

    /**
     * 通过枚举名称，取所有枚举实例名称(key) 与 code 属性值的映射集
     *
     * @param enumKey
     * @return
     */
    public static Map<String, String> getKeyCodeMapperInstance(String enumKey) {
        return mapKeyCode.get(enumKey);
    }

    /**
     * 通过枚举名称，获取所有枚举实例 code 与 name 属性值的映射集
     *
     * @param enumKey
     * @return
     */
    public static Map<String, String> getCodeNameMapperInstance(String enumKey) {
        return mapCodeName.get(enumKey);
    }

    /**
     * 根据枚举的类型，获取所有枚举实例名称(key) 与 code 属性值的映射集
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, String> getEnumMap(Class<T> clazz) {
        Map<String, String> map = new HashMap<>();
        try {
            if (clazz.isEnum()) {
                Object[] enumConstants = clazz.getEnumConstants(); // 获取所有枚举实例
                for (int i = 0; i < enumConstants.length; i++) {
                    T t = (T) enumConstants[i];
                    Field code = t.getClass().getDeclaredField("code"); // 获取 code 属性对象
                    code.setAccessible(true);
                    map.put(t.getClass().getDeclaredFields()[i].getName(), (String) code.get(t));
                }
            }
        } catch (NoSuchFieldException e) {
            logger.error("枚举工具启动报错：{}", e);
        } catch (IllegalAccessException e) {
            logger.error("枚举工具启动报错：{}", e);
        }
        return map;
    }

    /**
     * 根据枚举的类型，获取所有枚举实例 code 与 name 属性值的映射集
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> Map<String, String> getEnumCodeMap(Class<T> clazz) {
        Map<String, String> map = new HashMap<>();
        try {
            if (clazz.isEnum()) {
                Object[] enumConstants = clazz.getEnumConstants();
                for (int i = 0; i < enumConstants.length; i++) {
                    T t = (T) enumConstants[i];
                    Field code = t.getClass().getDeclaredField("code"); // 获取 code 属性对象
                    Field name = t.getClass().getDeclaredField("name"); // 获取 name 属性对象
                    code.setAccessible(true);
                    name.setAccessible(true);
                    map.put((String) code.get(t), (String) name.get(t));
                }
            }
        } catch (NoSuchFieldException e) {
            logger.error("枚举工具启动报错：{}", e);
        } catch (IllegalAccessException e) {
            logger.error("枚举工具启动报错：{}", e);
        }
        return map;
    }
}
```

测试：

```java
Map<String, String> enumMap = getEnumMap(Payment.class);
System.out.println(enumMap); // {"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}
Map<String, String> enumCodeMap = getEnumCodeMap(Payment.class);
System.out.println(enumCodeMap); // {"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}
```

### 7.3. 枚举治理的扩展 - 在 velocity 中使用枚举（了解）

Velocity 是一个基于 Java 的模板引擎，具有特定的语法，可以获取在 Java 语言中定义的对象，从而实现界面和 Java 代码的分离。本质就替代了以前老旧的 JSP 技术，是让后端人写前端页面逻辑的一种方式。*以下示例使用这种技术，个人没有实际使用过，了解即可。*

#### 7.3.1. 为什么会在velocity  velocity 中使用枚举

当涉及与前端的交互时，可能需要从前端把三种支付方式对应的code值传到后台。此时，如果在页面上直接写`010000`这样的值，那么页面的逻辑就很不直观了，具体代码的意义除了加注释别无办法。

因此为了解决后台可用，并且前端页面直观，可以尝试在页面上直接用枚举来解决问题。

#### 7.3.2. velocity 页面处理方式

```js
#set($payment=$enumTool.getCodeNameMapperInstance("Payment")) // 直接写明要获取的枚举类型名称
#if($payment.get("Payment_WX") == $param.code) // 通过枚举值获取其code值
    // 做微信支付页面逻辑
#end
```

#### 7.3.3. velocity 中配置 velocity-tools

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolbox>
    <tool>
        <key>enumTool</key>
        <class>com.moon.core.enumconstant.VelocityEnumTools</class>
    </tool>
    <tool>
        <key>stringTool</key>
        <class>org.apache.commons.lang.StringUtils</class>
    </tool>
    <tool>
        <key>dateTool</key>
        <class>org.apache.velocity.tools.generic.DateTool</class>
    </tool>
</toolbox>
```

经以上配置后，即可在页面中应用枚举治理工具类。例如：通过 code 值获取到相应描述

```js
$enumTool.getCodeNameMapperInstance("Payment").get($item.orderLoanStatus) // 显示“微信支付”
```

通过枚举获取到对应的 code 值

```js
#set($payment=$enumTool.getCodeNameMapperInstance("Payment")) // 拿到了Payment的map
$payment.get("Payment_WX")
```

至此可以实现系统的中的枚举治理，并且可在前端页面灵活应用。
