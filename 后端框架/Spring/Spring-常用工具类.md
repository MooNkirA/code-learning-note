## 1. Spring 常用 API

### 1.1. 获取超类中的泛型

- 准备测试的相关bean

```java
// 带泛型的类
public class BaseBean<T> {}

// 普通类
public class Address {}

// 定义子类，并指定父类中泛型
public class NormalBean extends BaseBean<Address> {}
```

- 使用 jdk 原生 API，通过反射获取父类的信息

```java
// 获取父类对象
Type type = NormalBean.class.getGenericSuperclass();
System.out.println(type); // 结果 com.moon.beans.BaseBean<com.moon.beans.Address>
// 判断是否为 ParameterizedType 类型
if (type instanceof ParameterizedType ) {
    // 通过 ParameterizedType 类的 getActualTypeArguments 方法获取相应的泛型
    System.out.println(((ParameterizedType)type).getActualTypeArguments()[0]); // 结果 class com.moon.beans.Address
}
```

- 使用 `org.springframework.core.GenericTypeResolver` 类的 `resolveTypeArgument` 工具方法，获取超类的泛型信息

```java
// 参数1：要检查的目标类类型；参数2：超类或者接口的类型
Class<?> t = GenericTypeResolver.resolveTypeArgument(NormalBean.class, BaseBean.class);
System.out.println(t); // 结果 class com.moon.beans.Address
```

- 使用 `org.springframework.core.ResolvableType` 类获取超类的泛型信息

```java
Class<?> resolve = ResolvableType.forClass(NormalBean.class).getSuperType().getGeneric().resolve();
System.out.println(resolve); // 结果 class com.moon.beans.Address
```

## 2. 对象操作工具

### 2.1. BeanUtils

#### 2.1.1. copyProperties 方法的使用注意事项

1. 属性的类型不匹配，无法进行复制。

```java
@Data
public class SourceBean {
    private Long age;
}

@Data
public class TargetBean {
    private String age;
}

public class Test {
    public static void main(String[] args) {
        SourceBean source = new SourceBean();
        source.setAge(25L);

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target);

        System.out.println(target.getAge());  // 拷贝赋值失败，输出null
    }
}
```

2. 对象复制是浅拷贝。如果源对象某个属性是引用类型，使用 `copyProperties` 方法进行属性复制时，实际上只复制了引用，即复制后的目标对象与和源对象的属性引用指向同一个对象。因此，当修改源对象的引用属性时，目标对象的引用属性也会被修改。
3. 源对象与目录对象属性名称不一致，无法复制这些属性。（即无法忽略大小写自动匹配）
4. 默认情况下，会将源对象中的 null 值属性覆盖到目标对象中。如果不希望 null 值覆盖目标对象中的属性，可以使用 `copyProperties` 方法的重载方法，并传入一个自定义的 `ConvertUtilsBean` 实例来进行配置。

```java
@Data
public class SourceBean {
    private String name;
    private String address;
}

@Data
public class TargetBean {
    private String name;
    private String address;
}

public class Test {
    public static void main(String[] args) {
        SourceBean source = new SourceBean();
        source.setName("kirA");
        source.setAddress(null);

        TargetBean target = new TargetBean();
        target.setAddress("月球");
        BeanUtils.copyProperties(source, target);

        System.out.println(target.getAddress());  // 输出为 null
    }
}
```

5. 注意引入的包名称。Spring 与 apache 的 BeanUtils 方法参数位置不一样。

```java
//org.springframework.beans.BeanUtils(源对象在左边，目标对象在右边)
public static void copyProperties(Object source, Object target) throws BeansException 
//org.apache.commons.beanutils.BeanUtils（源对象在右边，目标对象在左边）
public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException
```

6. 因为当属性类型为 `boolean` 时，属性名以 `is` 开头，默认属性名会去掉前面的 `is`，导致源对象和目标对象属性对不上，无法复制。

```java
@Data
public class SourceBean {
    private boolean isSale;
}

@Data
public class TargetBean {
    private Boolean isSale;
}

public class Test {
    public static void main(String[] args) {
        SourceBean source = new SourceBean();
        source.setSale(true);

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target);
        System.out.println(target.getIsSale()); // 输出为 null
    }
}
```

7. `copyProperties` 方法进行对象复制，会导致在 IDE 中无法进行全文搜索该对象的所有 set 方法，在哪些地方引用到。
8. 不同内部类，即使相同属性，也是赋值失败。该内部类是在不同的类中，因此 Spring 会认为属性不同，不会进行复制。如果要复制成功，只要让两个类的属性指向同一个内部类。

```java
@Data
public class CopySource {
    public String outerName;
    public CopySource.InnerClass innerClass;

    @Data
    public static class InnerClass {
        public String InnerName;
    }
}

@Data
public class CopyTarget {
    public String outerName;
    public CopyTarget.InnerClass innerClass;

    @Data
    public static class InnerClass {
        public String InnerName;
    }
}

public class Test {
    public static void main(String[] args) {
        CopySource test1 = new CopySource();
        test1.outerName = "MooN";

        CopySource.InnerClass innerClass = new CopySource.InnerClass();
        innerClass.InnerName = "kirA";
        test1.innerClass = innerClass;

        System.out.println(test1);
        CopyTarget test2 = new CopyTarget();
        BeanUtils.copyProperties(test1, test2);

        System.out.println(test2);  // 输出 CopyTarget(outerName=MooN, innerClass=null)
    }
}
```

9. 拷贝属性值成功，需要源对象和目标对象都要有 getter 和 setter 方法。因为 `copyProperties` 方法是用反射拿到 set 和 get 方法再去拿属性值和设置属性值。
10. `copyProperties` 方法拷贝包含泛型属性时，若源对象和目标对象的泛型属性类型不匹配，会拷贝赋值失败。

```java
@Data
public class CopySource {
    public String outerName;
    public List<CopySource.InnerClass> clazz;

    @Data
    public static class InnerClass {
        public String InnerName;
    }
}

@ToString
@Data
public class CopyTarget {
    public String outerName;
    public List<CopyTarget.InnerClass> clazz;

    @Data
    public static class InnerClass {
        public String InnerName;
    }
}

public class Test {
    public static void main(String[] args) {
        CopySource test1 = new CopySource();
        test1.outerName = "MooN";

        CopySource.InnerClass innerClass = new CopySource.InnerClass();
        innerClass.InnerName = "Zero";

        List<CopySource.InnerClass> clazz = new ArrayList<>();
        clazz.add(innerClass);
        test1.setClazz(clazz);

        System.out.println(test1);
        CopyTarget test2 = new CopyTarget();
        BeanUtils.copyProperties(test1, test2);

        System.out.println(test2);  // 输出 CopyTarget(outerName=MooN, clazz=null)
    }
}
```

11. 因为采用反射机制实现对象的复制，会对程序的效率有影响。

## 3. 待整理参考资料

> [Spring自带常用工具类](https://mp.weixin.qq.com/s/kaM2CrPOcujW-I-3_s8QUg)
