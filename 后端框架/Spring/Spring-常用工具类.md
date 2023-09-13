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

待整理：BeanUtils.copyProperties的 11个坑 https://mp.weixin.qq.com/s/UtjBePHy0Kxxo7aca17C0w

## 3. 待整理参考资料

> [Spring自带常用工具类](https://mp.weixin.qq.com/s/kaM2CrPOcujW-I-3_s8QUg)
