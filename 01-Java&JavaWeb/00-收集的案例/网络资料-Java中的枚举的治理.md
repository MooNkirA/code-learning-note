# 一、为啥用枚举&为啥要对枚举进行治理

## 1. 先来说说为啥用枚举

表中某个字段标识了这条记录的状态，我们往往使用一些code值来标识，例如01成功，00失败。

多状态共性的东西可以常量保存，例如

```java
class Constants{
    public static final String success = "01";
    public static final String failure= "00";
}
```

然而，在一些大型项目中，表的数量极多，一些表中需要维护的状态也极多，如果都在如上的Constants中维护，试想如果添加一个状态值，那么需要在整个篇幅中找到对应的块，然后去新增值；修改呢？同样麻烦！！！

所以我们使用枚举，每个枚举类就只负责对一个状态做维护，这样我们方便增删改。例如：

```java
/**
 * Created by Bright on 2017/3/13.
 *
 * @author :
 */
public enum Payment {
    Payment_WX("010000","微信支付"),
    Payment_ZFB("010001","支付宝支付"),
    Payment_YL("010002","银联支付");

    public static Map<String,String> map = new HashMap<String, String>();

    static{
        Payment[] values = Payment.values();
        if(values.length > 0){
            for(Payment product : values){
                map.put(product.getCode(),product.getName());
            }
        }
    }

    Payment(String code, String name){
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

## 2. 为啥要用java反射处理枚举呢？

我们之前看到了，使用Constants很方便，可以直接通过这个类的静态字段拿到值。当我们使用枚举时，当枚举类逐渐增多时，我们会发现，不同的地方我们需要获取不同的类，然后再通过不同的枚举获取到不同的值。这又势必是个头痛的事情。

那么我们想到了改进的方法：

改进一：把每个枚举类中放个map，把其code和name值映射进去，然后调用时通过静态map对象，把code值作为key传入，势必能获取到对应的描述。（如上段代码的map值）

然而，这个改进后，我们依旧需要找到这个类，然后去使用它的静态map，能不能只通过一个类进行统一治理呢？

改进二：通过一个类，把所有枚举在该类中注册，然后通过该类直接获取到相应的枚举值及name描述。

# 二、枚举治理的实现

## 1. 先弄清我们使用枚举的场景

### 1.1. 通过枚举类中枚举名获取到枚举的code值（使用上面的枚举值定义）

例如：`{"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}`

```java
if(param.equals(Payment.Payment_WX.getCode()){}
```

### 1.2. 通过枚举类中枚举的code值获取到对应的name描述（使用上面的枚举值定义）

例如：`{"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}`

```java
Payment.map.get(Payment.Payment_WX.getCode());
```

## 2. 枚举治理工具类的实现

```java
/**
 * Created by Bright on 2017/3/13.
 *
 * @author :
 */
public class VelocityEnumTools {

    public static final Logger logger = LoggerFactory.getLogger(VelocityEnumTools.class);


    //通过枚举获取枚举code值，例如：{"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}
    public static Map<String,Map<String,String>> mapKeyCode = new HashMap<String, Map<String, String>>();

    //通过code值获取枚举name，例如：{"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}
    public static Map<String, Map<String, String>> mapCodeName = new HashMap<String, Map<String, String>>();

    /**
     * 需要在页面控制的enum，如Payment类似添加即可
     */
    static {
        //通过枚举获取code值
        mapKeyCode.put(Payment.class.getSimpleName(), getEnumMap(Payment.class));
        //通过code值获取枚举name
        mapCodeName.put(Payment.class.getSimpleName(),getEnumCodeMap(Payment.class));
    }

    /**
     * 通过枚举获取code值
     * @param enumKey
     * @return
     */
    public static Map<String, String> getKeyCodeMapperInstance(String enumKey) {
        return mapKeyCode.get(enumKey);
    }

    /**
     * 通过code值获取枚举name
     * @param enumKey
     * @return
     */
    public static Map<String, String> getCodeNameMapperInstance(String enumKey) {
        return mapCodeName.get(enumKey);
    }

    public static <T> Map<String, String> getEnumMap(Class<T> clazz) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (clazz.isEnum()) {
                Object[] enumConstants = clazz.getEnumConstants();
                for (int i = 0; i < enumConstants.length; i++) {
                    T t = (T) enumConstants[i];
                    Field code = t.getClass().getDeclaredField("code");
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

    private static <T> Map<String,String> getEnumCodeMap(Class<T> clazz) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (clazz.isEnum()) {
                Object[] enumConstants = clazz.getEnumConstants();
                for (int i = 0; i < enumConstants.length; i++) {
                    T t = (T) enumConstants[i];
                    Field code = t.getClass().getDeclaredField("code");
                    Field name = t.getClass().getDeclaredField("name");
                    code.setAccessible(true);
                    name.setAccessible(true);
                    map.put((String) code.get(t),(String) name.get(t));
                }
            }
        } catch (NoSuchFieldException e) {
            logger.error("枚举工具启动报错：{}", e);
        } catch (IllegalAccessException e) {
            logger.error("枚举工具启动报错：{}", e);
        }
        return map;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Map<String, String> enumMap = getEnumMap(Payment.class);
        System.out.println(JSON.toJSONString(enumMap));//{"Payment_WX":"010000","Payment_YL":"010002","Payment_ZFB":"010001"}
        Map<String, String> enumCodeMap = getEnumCodeMap(Payment.class);
        System.out.println(JSON.toJSONString(enumCodeMap));//{"010002":"银联支付","010001":"支付宝支付","010000":"微信支付"}
    }
}
```

# 三、枚举治理的扩展-velocity中使用枚举

## 1. 为什么会在velocity中使用枚举

当涉及与前端的交互时，我们可能需要从前端把三种支付方式对应的code值传到后台。

此时，如果在页面上直接写010000这样的值，那么页面的逻辑就很不直观了，今天写的时候你还能认知，为了防止自己忘了，除了加注释别无办法。

故，为了解决后台可用，且前端页面直观，所以我们希望尝试在页面上直接用枚举来解决问题。

## 2. 看看页面如何处理（velocity页面中）

```js
#set($payment=$enumTool.getCodeNameMapperInstance("Payment"))//直接写明要获取的枚举类型名称
#if($payment.get("Payment_WX") == $param.code)//通过枚举值获取其code值
    //做微信支付页面逻辑
#end
```

## 3. velocity中配置velocity-tools

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolbox>
    <tool>
        <key>enumTool</key>
        <class>com.bright.core.enumconstant.VelocityEnumTools</class>
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

这样就可以简单的在页面中应用我们枚举治理工具了。

例如：通过code值获取到相应描述

```js
$enumTool.getCodeNameMapperInstance("Payment").get($item.orderLoanStatus)//显示“微信支付”
```

通过枚举获取到对应的code值

```js
#set($payment=$enumTool.getCodeNameMapperInstance("Payment"))//拿到了Payment的map
$payment.get("Payment_WX")
```

就此，我们可以实现系统的中的枚举治理，并且可在前端页面灵活应用。