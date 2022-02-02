虽然推崇在java中使用枚举（可查看[《Java中的枚举的治理》](http://www.cnblogs.com/shizhanming/p/6564805.html)）来对数据字典及常量进行控制，但是有些时候，我们还是会觉得常量控制更为便捷。

比如，对于数据字典，我们可以使用枚举值来处理；对于一些其他的信息，我们会使用常量保存和使用。

# 一、常量遇到的问题

## 1. 苗条的常量类

这里使用苗条形容下我们程序中的常量类，别看它宽度，就只看她长度，滚起屏来，那叫一个长啊，修长的身材，令你如痴如醉。（省略号里的东西，我就不贴了！！！）

例如：


```java
public class Constants {

        public static final String REAL_NAME1 = "v1";
        public static final String REAL_NAME2 = "v2";
        public static final String REAL_NAME3 = "v3";
        public static final String REAL_NAME4 = "v4";
        public static final String REAL_NAME5 = "v5";
        public static final String 6 = "v6";
        public static final String 7 = "v7";
        public static final String REAL_NAME8 = "v8";
        public static final String REAL_NAME9 = "v9";
        ......
}
```

一个无穷尽的常量类，设想这个类篇幅巨长，你想加点什么不知道该加在哪；你想改点什么，不知道去哪改；你想骂街也不知道去哪骂！！！

这样的常量管理，带来的问题如下：

1. 不好维护，相关的代码写到一起，此时，常量的篇幅较长导致你找不到对应的常量块进行维护；
2. 虽然是在不同的业务场景下，但是有些常量的名称还是有可能重复；
3. 有时为了减少常量的定义，就得共用一些常量，而这样的共用会导致某种业务场景下需要对该常量进行修改，而导致另外一些业务场景下的常量使用产生歧义；
4. 其他你能想到的骂街的理由

## 2. 代码的坏味道

引用下“代码的坏味道”这个词，我们常能看到一些常量类的坏味道里，假如常量名称如上所示，名称类似的很多；名称不明确的也很多，还没有注释，这样的歧义也是因为代码不好管理造成的。

# 二、常量如何治理

## 1. 初级治理——使用内部类

使用java的内部类进行常量的初步治理，代码如下：

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
public class Constants {

    public static final class TOKEN_FLAG_ONE {

        public static final String REAL_NAME = "v1";

        public static final String CRET = "v2";

        public static final String GUR = "v5";

    }

}
```

这样的好处是，通过常量的内部类的名称，可以直接获取对应模块的常量的引用信息。使用代码如下：

```java
@Test
public void test(){
	System.out.println(Constants.TOKEN_FLAG_ONE.REAL_NAME);
}
```

## 2. 中级治理——集中管理

初级治理中，我们的想法还是不错的，但是看起来比较low，而且当我们希望通过value获取到key时，你却无能为力，于是我们有了中级治理。

中级治理我们主要是通过map，每个内部类都会存为map中的一个entry，每个entry又都是map类型的集合，集合中包含该内部类的所有常量。

代码如下：

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
public class Constants {

    public static final Map<String,Map<String,String>> keyValueMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> valueKeyMapCons = new LinkedHashMap<String, Map<String, String>>();
　　/**
     * 初始化所有常量
     */
    static {
        try {
            //获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyValueMap = new LinkedHashMap<String, String>();//存放key和value的map
                Map<String, String> valueKeyMap = new LinkedHashMap<String, String>();//存放value和key的map//每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    keyValueMap.put(fd.getName(), fd.get(cls).toString());//注解对象空，其值为该field的值
                    valueKeyMap.put(fd.get(cls).toString(),fd.getName());
                }
                keyValueMapCons.put(cls.getSimpleName(),keyValueMap);
                valueKeyMapCons.put(cls.getSimpleName(),valueKeyMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final class TOKEN_FLAG_ONE {
		public static final String REAL_NAME = "v1";
		public static final String CRET = "v2";
        public static final String GUR = "v5";
    }
}
```


好了，我们在Constants中有了两个常量集：一个集keyValueMapCons，按照内部类的名称，把常量的名字作为map的key，值作为map的value；一个集valueKeyMapCons，按照内部类的名称，把常量的值作为map的key，常量的名字作为map的key。

这样一来，我们可以使用常量的这两个集满足我们对常量的使用需求。使用代码如下：

```java
@Test
public void test1(){
    System.out.println(Constants.keyValueMapCons.get("TOKEN_FLAG_ONE").get("REAL_NAME"));//v1
    System.out.println(Constants.valueKeyMapCons.get("TOKEN_FLAG_ONE").get("v5"));//GUR
}
```

## 3. 中高级治理——使用注解

我们可以通过key获取到value，也可以通过value获取到key了。现在有这么个问题，我们使用常量时，不光要有常量的定义、常量的值，还应该有对常量的描述，而传统的对于常量的定义，往往使我们无从存放对常量的描述。

此时，我们希望通过注解来改变这种情况。

我们来自定义注解类型如下：

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConstantAnnotation {
    String value();
}
```

有了这个注解，我们就可以把描述些到常量的上面了

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
public class Constants {
    private static final String CONSTANT_STRING = "这是一条短信消息";

    public static final class TOKEN_FLAG_ONE {

        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING)
        public static final String GUR = "v5";//把描述与注解分开

    }
}
```

我们看到，每个常量上面有了对应的中文描述，这样的中文描述可以用来干嘛呢？

比如，我们希望在某个业务场景下，符合gur常量的业务，发送一条短信消息，而这个消息我们就可以定义在我们的自定义注解中。例如GUR这个常量，我们把它的描述声明成一个常量，这个常量可用来存放对应的短信消息。我们的常量类中如果再有一个通过常量获取到描述的map，这是不是就完美了？

于是，我们有了下面的代码：

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
public class Constants {

    public static final Map<String,Map<String,String>> keyDescMapCons = new LinkedHashMap<String, Map<String, String>>();
/**
     * 初始化所有常量
     */
    static {
        try {
            //获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyDescMap = new LinkedHashMap<String, String>();//存放key和desc的map

                //每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    //每个属性获取指定的annotation的注解对象
                    ConstantAnnotation ca = fd.getAnnotation(ConstantAnnotation.class);
                    if(ca != null){
                        keyDescMap.put(fd.getName(), ca.value());//注解对象不空，其值为注解对象中的值
                    }
                }
                                keyDescMapCons.put(cls.getSimpleName(),keyDescMap);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * key-value-desc
     *
     * 常量名-常量值-注解描述
     *
     * 实现通过key获取value
     *
     * 实现通过key获取desc
     *
     * 实现通过value获取desc
     */
    private static final String CONSTANT_STRING = "这是一条短消息";

    public static final class TOKEN_FLAG_ONE {

        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING)
        public static final String GUR = "v5";//把描述与注解分开

    }

}
```

现在，我们通过Constants的keyDescMap可以获取到这个常量对应的描述了。使用代码如下：

```java
    @Test
    public void test1(){
        System.out.println(Constants.keyDescMapCons.get("TOKEN_FLAG_ONE").get("GUR"));//打印输出“这是一条短消息”
    }
```

## 4. 综合治理（终极治理）

现在我们有了常量的治理，有了注解的描述，有时我们需要通过key获取到value，有时我们需要通过value获取描述，有时我们需要通过key获取到描述，等等。排列组合共6种形式，暂且不说什么时候会用到，我们先给他来个综合治理的实现。代码如下：

```java
/**
 * Created by SZBright on 2017/3/1.
 *
 * @author :
 */
public class Constants {

    public static final Map<String,Map<String,String>> keyValueMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> keyDescMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> descValueMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> descKeyMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> valueDescMapCons = new LinkedHashMap<String, Map<String, String>>();

    public static final Map<String,Map<String,String>> valueKeyMapCons = new LinkedHashMap<String, Map<String, String>>();


    /**
     * 初始化所有常量
     */
    static {
        try {
            //获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyDescMap = new LinkedHashMap<String, String>();//存放key和desc的map
                Map<String, String> keyValueMap = new LinkedHashMap<String, String>();//存放key和value的map
                Map<String, String> valueKeyMap = new LinkedHashMap<String, String>();//存放value和key的map
                Map<String, String> valueDescMap = new LinkedHashMap<String, String>();//存放value和desc的map
                Map<String, String> descValueMap = new LinkedHashMap<String, String>();//存放desc和value的map
                Map<String, String> descKeyMap = new LinkedHashMap<String, String>();//存放desc和key的map
                //每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    //每个属性获取指定的annotation的注解对象
                    ConstantAnnotation ca = fd.getAnnotation(ConstantAnnotation.class);
                    keyValueMap.put(fd.getName(), fd.get(cls).toString());//注解对象空，其值为该field的值
                    valueKeyMap.put(fd.get(cls).toString(),fd.getName());
                    if(ca != null){
                        keyDescMap.put(fd.getName(), ca.value());//注解对象不空，其值为注解对象中的值
                        valueDescMap.put(fd.get(cls).toString(),ca.value());
                        descValueMap.put(ca.value(),fd.get(cls).toString());
                        descKeyMap.put(ca.value(),fd.getName());
                    }
                }
                keyValueMapCons.put(cls.getSimpleName(),keyValueMap);
                keyDescMapCons.put(cls.getSimpleName(),keyDescMap);
                descValueMapCons.put(cls.getSimpleName(),descValueMap);
                descKeyMapCons.put(cls.getSimpleName(),descKeyMap);
                valueDescMapCons.put(cls.getSimpleName(),valueDescMap);
                valueKeyMapCons.put(cls.getSimpleName(),valueKeyMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String CONSTANT_STRING = "这是一条短消息";

    public static final class TOKEN_FLAG_ONE {

        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING)
        public static final String GUR = "v5";//把描述与注解分开

    }
}
```

好了，有了上面这个类，你就啥都不用愁了，用的时候先拿常量的声明，再用对应的map，然后指定内部类名称，想获取什么，获取什么。

ps：枚举治理和常量治理结合使用，可能是我们系统开发时的最佳实践

