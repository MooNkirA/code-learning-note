## 1. Mockito 简介

Mockito 是最流行的 Java mock 框架之一.

### 1.1. 官方资料

- [Mockito 官方网站](https://site.mockito.org/)
- [PowerMockito Github](https://github.com/powermock/powermock/)

### 1.2. 什么是 Mock 测试

在做单元测试的时候，会发现在要测试的方法会引用很多外部依赖的对象，比如：（发送邮件，网络通讯，远程服务，文件系统等等），而又没法控制这些外部依赖的对象。为了解决这个问题，就需要用到 Mock 工具

Mock 工具就是在测试过程中，对于某些不容易构造（如 `HttpServletRequest` 必须在 Servlet 容器中才能构造出来）或者不容易获取比较复杂的对象（如 JDBC 中的 ResultSet 对象），创建一个虚拟的对象（Mock 对象）来替代这些外部依赖的对象，以便完成单元测试。

Mock 最大的功能是把单元测试的耦合分解开，如果代码对另一个类或者接口有依赖，它能够模拟这些依赖，并验证所调用的依赖的行为。

### 1.3. Mock 适用场景

对于一些应用场景，是非常适合使用 Mock 的：

- 真实对象具有不可确定的行为(产生不可预测的结果，如股票的行情)
- 真实对象很难被创建(比如具体的web容器)
- 真实对象的某些行为很难触发(比如网络错误)
- 真实情况令程序的运行速度很慢
- 真实对象有用户界面
- 测试需要询问真实对象它是如何被调用的(比如测试可能需要验证某个回调函数是否被调用了)
- 真实对象实际上并不存在(当需要和其他开发小组，或者新的硬件系统打交道的时候，这是一个普遍的问题)

也有一些不得不使用 Mock 的场景：

- 一些比较难构造的 Object：这类 Object 通常有很多依赖，在单元测试中构造出这样类通常花费的成本太大。
- 执行操作的时间较长 Object：有一些 Object 的操作费时，而被测对象依赖于这一个操作的执行结果，例如大文件写操作，数据的更新等等，出于测试的需求，通常将这类操作进行 Mock。
- 异常逻辑：一些异常的逻辑往往在正常测试中是很难触发的，通过 Mock 可以人为的控制触发异常逻辑。

在一些压力测试的场景下，也不得不使用 Mock，例如在分布式系统测试中，通常需要测试一些单点（如 namenode，jobtracker）在压力场景下的工作是否正常。而通常测试集群在正常逻辑下无法提供足够的压力（主要原因是受限于机器数量），这时候就需要应用 Mock 去满足。

## 2. Mockito 基础使用

### 2.1. 引入 maven 依赖

注意：mockito 5 以上版本需要 jdk 11。如果还是使用 JDK 8，只能使用 mockito 4.x 版本。

```xml
<dependencies>
    <!-- https://mvnrepository.com/artifact/junit/junit -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.mockito/mockito-core -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.11.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2. 前置准备

创建两个简单的类用于测试

```java
public class DemoDao {
    public int getDemoStatus() {
        return new Random().nextInt();
    }
}

public class DemoService {

    private DemoDao demoDao;

    public DemoService(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    public int getDemoStatus(){
        return demoDao.getDemoStatus();
    }
}
```

### 2.3. 编写测试用例

```java
import com.moon.mockito.dao.DemoDao;
import com.moon.mockito.service.DemoService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * mockito 基础使用示例
 */
public class MockitoQuickstart {
    @Test
    public void testBasic() {
        // mock DemoDao instance
        DemoDao mockDemoDao = Mockito.mock(DemoDao.class);

        // 使用 mockito 对 getDemoStatus 方法打桩
        Mockito.when(mockDemoDao.getDemoStatus()).thenReturn(1);

        // 调用 mock 对象的 getDemoStatus 方法，结果永远是 1
        Assert.assertEquals(1, mockDemoDao.getDemoStatus());

        // mock DemoService
        DemoService mockDemoService = new DemoService(mockDemoDao);
        Assert.assertEquals(1, mockDemoService.getDemoStatus());
    }
}
```

## 3. mock 方法

此处涉及一种是类测试，另一种是接口测试。

```java
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 使用 mock 方法示例
 */
public class MockClassTest {
    @Test
    public void mockClassTest() {
        Random mockRandom = mock(Random.class);

        // 默认值: mock 对象的方法的返回值默认都是返回类型的默认值
        System.out.println(mockRandom.nextBoolean()); // false
        System.out.println(mockRandom.nextInt()); // 0
        System.out.println(mockRandom.nextDouble()); // 0.0

        // mock: 指定调用 nextInt 方法时，永远返回 100
        when(mockRandom.nextInt()).thenReturn(100);
        Assert.assertEquals(100, mockRandom.nextInt());
        Assert.assertEquals(100, mockRandom.nextInt());
    }

    @Test
    public void mockInterfaceTest() {
        List mockList = mock(List.class);

        // 接口的默认值：和类方法一致，都是默认返回值
        Assert.assertEquals(0, mockList.size());
        Assert.assertEquals(null, mockList.get(0));

        // 注意：调用 mock 对象的写方法，是没有效果的
        mockList.add("a");
        Assert.assertEquals(0, mockList.size());      // 没有指定 size() 方法返回值，这里结果是默认值
        Assert.assertEquals(null, mockList.get(0));   // 没有指定 get(0) 返回值，这里结果是默认值

        // mock值测试
        when(mockList.get(0)).thenReturn("a");          // 指定 get(0)时返回 a
        Assert.assertEquals(0, mockList.size());        // 没有指定 size() 方法返回值，这里结果是默认值
        Assert.assertEquals("a", mockList.get(0));      // 因为上面指定了 get(0) 返回 a，所以这里会返回 a
        Assert.assertEquals(null, mockList.get(1));     // 没有指定 get(1) 返回值，这里结果是默认值
    }
}
```

执行结果：

```
false
0
0.0
```

## 4. @Mock 注解

使用该注解时，要使用 `MockitoAnnotations.initMocks` 方法，让注解生效。比如放在 @Before方法中初始化。

比较优雅优雅的写法是用 `MockitoJUnitRunner`，它可以自动执行 `MockitoAnnotations.initMocks` 方法。

> `@Mock` 注解可以理解为对 mock 方法的一个替代。

```java
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

import static org.mockito.Mockito.when;

/**
 * 使用 @Mock 注解示例
 */
@RunWith(MockitoJUnitRunner.class)
public class MockAnnotationTest {
    @Mock
    private Random random;

    @Test
    public void test() {
        when(random.nextInt()).thenReturn(100);
        Assert.assertEquals(100, random.nextInt());
    }
}
```

## 5. 参数匹配

如果参数匹配既声明了精确匹配，也声明了模糊匹配；又或者同一个值的精确匹配出现了两次，使用时会匹配符合匹配条件的最新声明的匹配。

### 5.1. 使用示例

```java
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * 参数匹配测试示例
 */
@RunWith(MockitoJUnitRunner.class)
public class ParameterTest {
    @Mock
    private List<String> testList;

    @Test
    public void testParameter() {
        // 精确匹配 0
        when(testList.get(0)).thenReturn("a");
        Assert.assertEquals("a", testList.get(0));

        // 精确匹配 0
        when(testList.get(0)).thenReturn("b");
        Assert.assertEquals("b", testList.get(0));

        // 模糊匹配
        when(testList.get(anyInt())).thenReturn("c");
        Assert.assertEquals("c", testList.get(0));
        Assert.assertEquals("c", testList.get(1));
    }
}
```

### 5.2. mockito 的匹配函数

上例中的 `anyInt` 只是用来匹配参数的工具之一，目前 mockito 有多种匹配函数，部分如下：

|        函数名         |                匹配类型                 |
| -------------------- | -------------------------------------- |
| `any()`              | 所有对象类型                             |
| `anyInt()`           | 基本类型 int、非 null 的 Integer 类型     |
| `anyChar()`          | 基本类型 char、非 null 的 Character 类型  |
| `anyShort()`         | 基本类型 short、非 null 的 Short 类型     |
| `anyBoolean()`       | 基本类型 boolean、非 null 的 Boolean 类型 |
| `anyDouble()`        | 基本类型 double、非 null 的 Double 类型   |
| `anyFloat()`         | 基本类型 float、非 null 的 Float 类型     |
| `anyLong()`          | 基本类型 long、非 null 的 Long 类型       |
| `anyByte()`          | 基本类型 byte、非 null 的 Byte 类型       |
| `anyString()`        | String 类型(不能是 null)                 |
| `anyList()`          | `List<T>`类型(不能是 null)               |
| `anyMap()`           | `Map<K, V>`类型(不能是 null)             |
| `anyCollection()`    | `Collection<T>`类型(不能是 null)         |
| `anySet()`           | `Set<T>`类型(不能是 null)                |
| `any(Class<T> type)` | type 类型的对象(不能是 null)              |
| `isNull()`           | 是 null                                |
| `notNull()`          | 非 null                                |
| `isNotNull()`        | 非 null                                |

## 6. Mock 异常

### 6.1. thenThrow 方法

Mockito 使用 `thenThrow` 让方法抛出异常。

如下示例代码中，一个是单个异常处理示例，一个是多个异常处理示例。

```java
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mock 异常测试示例
 */
public class ThenThrowTest {
    /**
     * 例子1： thenThrow 用来让函数调用抛出异常.
     */
    @Test
    public void throwTest1() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt()).thenThrow(new RuntimeException("异常"));

        try {
            mockRandom.nextInt();
            Assert.fail();  // 上面会抛出异常，所以不会走到这里
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常", ex.getMessage());
        }
    }

    /**
     * thenThrow 中可以指定多个异常。在调用时异常依次出现。若调用次数超过异常的数量，再次调用时抛出最后一个异常。
     */
    @Test
    public void throwTest2() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt()).thenThrow(new RuntimeException("异常1"), new RuntimeException("异常2"));

        try {
            mockRandom.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常1", ex.getMessage());
        }

        try {
            mockRandom.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常2", ex.getMessage());
        }
    }
}
```

### 6.2. doThrow 方法

对应返回类型是 `void` 的函数，`thenThrow` 是无效的，要使用 `doThrow`。

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.doThrow;

/**
 * DoThrow 处理 void 返回值方法异常的示例
 */
public class DoThrowTest {

    static class ExampleService {
        public void hello() {
            System.out.println("Hello");
        }
    }

    @Mock
    private ExampleService exampleService;

    @Test
    public void test() {
        // 这种写法可以达到效果
        doThrow(new RuntimeException("异常")).when(exampleService).hello();

        try {
            exampleService.hello();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("异常", ex.getMessage());
        }
    }
}
```

此外还有，可以查看官方文档

- `doAnswer(Answer)`
- `doNothing()`
- `doCallRealMethod()`

## 7. spy 和 @Spy 注解

### 7.1. spy 基础使用

spy 和 mock 不同，不同点是：

- spy 的参数是对象实例；mock 的参数是 class。
- 被 spy 的对象，调用其方法时默认会走真实方法。mock 对象不会。

```java
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * spy 基础使用示例与 Mock 的对比
 */
public class SpyTest {
    // 测试 spy
    @Test
    public void test_spy() {
        ExampleService spyExampleService = spy(new ExampleService());
        // 默认会走真实方法
        Assert.assertEquals(3, spyExampleService.add(1, 2));
        // 打桩后，不会走了
        when(spyExampleService.add(1, 2)).thenReturn(10);
        Assert.assertEquals(10, spyExampleService.add(1, 2));
        // 但是参数比匹配的调用，依然走真实方法
        Assert.assertEquals(3, spyExampleService.add(2, 1));
    }

    // 测试 mock
    @Test
    public void test_mock() {
        ExampleService mockExampleService = mock(ExampleService.class);
        // 默认返回结果是返回类型int的默认值
        Assert.assertEquals(0, mockExampleService.add(1, 2));
    }
}

class ExampleService {
    int add(int a, int b) {
        return a + b;
    }
}
```

### 7.2. @Spy 基础使用

spy 对应注解 `@Spy`，和 `@Mock` 是一样用的。

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.when;

/**
 * 注解 @Spy 示例
 */
public class SpyAnnotationTest {
    @Spy
    private SpyAnnotationDemoService spyAnnotationDemoService;

    @Test
    public void testSpy() {
        MockitoAnnotations.initMocks(this);

        Assert.assertEquals(3, spyAnnotationDemoService.add(1, 2));

        when(spyAnnotationDemoService.add(1, 2)).thenReturn(10);
        Assert.assertEquals(10, spyAnnotationDemoService.add(1, 2));
    }
}

class SpyAnnotationDemoService {
    int add(int a, int b) {
        return a + b;
    }
}
```

对于 `@Spy`，如果发现修饰的变量是 null，会自动调用类的无参构造函数来初始化。因此下面两种写法是等价的：

```java
// 写法1
@Spy
private ExampleService spyExampleService;
// 写法2
@Spy
private ExampleService spyExampleService = new ExampleService();
```

如果没有无参构造函数，必须使用写法2。例子：

```java
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

class ExampleService {
    private int a;

    public ExampleService(int a) {
        this.a = a;
    }

    int add(int b) {
        return a+b;
    }
}

public class MockitoDemo {
    @Spy
    private ExampleService spyExampleService = new ExampleService(1);

    @Test
    public void test_spy() {
        MockitoAnnotations.initMocks(this);

        Assert.assertEquals(3, spyExampleService.add(2));
    }
}
```

## 8. 测试隔离

根据 JUnit 单测隔离，当 Mockito 和 JUnit 配合使用时，也会将非 `static` 变量或者非单例隔离开。比如使用 `@Mock` 修饰的 mock 对象在不同的单测中会被隔离开。

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Mcok 测试隔离
 */
@RunWith(MockitoJUnitRunner.class)
public class MockIsolationTest {
    static class ExampleService {
        public int add(int a, int b) {
            return a + b;
        }
    }

    @Mock
    private ExampleService exampleService;

    @Test
    public void test01() {
        System.out.println("---call test01---");

        System.out.println("打桩前: " + exampleService.add(1, 2));

        when(exampleService.add(1, 2)).thenReturn(100);

        System.out.println("打桩后: " + exampleService.add(1, 2));
    }

    @Test
    public void test02() {
        System.out.println("---call test02---");

        System.out.println("打桩前: " + exampleService.add(1, 2));

        when(exampleService.add(1, 2)).thenReturn(100);

        System.out.println("打桩后: " + exampleService.add(1, 2));
    }
}
```

将两个单测一起运行，其运行结果是：

```
---call test01---
打桩前: 0
打桩后: 100
---call test02---
打桩前: 0
打桩后: 100
```

test01 先被执行，打桩前调用 `add(1, 2)` 的结果是 0，打桩后是 100。然后 test02 被执行，打桩前调用 `add(1, 2)` 的结果是 0，而非 100。








## 9. PowerMock (Mockito 框架增强)

> TODO: 详细使用参考阿里的《Java工程师必读手册.pdf》电子书的[Java单元测试系列]章节

### 9.1. PowerMock 概述

PowerMock 是一个扩展了其它如 EasyMock 等 mock 框架的、功能更加强大的框架。PowerMock 使用一个自定义类加载器和字节码操作来模拟静态方法，构造函数，final 类和方法，私有方法，去除静态初始化器等等。通过使用自定义的类加载器，简化采用的 IDE 或持续集成服务器不需要做任何改变。

PowerMock 能够完美的弥补如今比较流行的 jMock 、EasyMock 、Mockito 三个 Mock 工具，不能 mock 静态、final、私有方法等的缺点

熟悉 PowerMock 支持的 mock 框架的开发人员会发现 PowerMock 很容易使用，因为对于静态方法和构造器来说，整个的期望 API 是一样的。PowerMock 旨在用少量的方法和注解扩展现有的 API 来实现额外的功能。目前PowerMock 支持 EasyMock 和 Mockito。

### 9.2. PowerMock 基础使用

#### 9.2.1. 相关注解

- `@RunWith(PowerMockRunner.class)`
- `@PrepareForTest({ YourClassWithEgStaticMethod.class })`

#### 9.2.2. 引入依赖

```xml
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-module-junit4</artifactId>
    <version>2.0.9</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-api-mockito2</artifactId>
    <version>2.0.9</version>
    <scope>test</scope>
</dependency>
```

> Notes: **使用 powermock 依赖后，已经包含 mockito 相关依赖，因此不需要再单独引入 mockito-core 依赖，否则会出现冲突。**

### 9.3. PowerMock 支持静态方法 Mock 功能

#### 9.3.1. Mockito 不支持 mock 静态方法

因为 Mockito 使用继承的方式实现 mock 功能，用 CGLIB 生成 mock 对象代替真实的对象进行执行，为了 mock 实例的方法，可以在 subclass 中覆盖它。而 static 方法是不能被子类覆盖的，所以 Mockito 不能 mock 静态方法。

比如在 `ExampleService` 类中定义静态方法 `add()`：

```java
public class ExampleService {
    public static int add(int a, int b) {
        return a+b;
    }
}
```

尝试给静态方法打桩，会报错：

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockitoDemo {
    @Test
    public void test() {
        when(ExampleService.add(1, 2)).thenReturn(100); // 会报错
    }
}
```

#### 9.3.2. Mock 静态方法示例

而 PowerMock 弥补 Mockito 缺失的静态方法 mock 功能，因为它直接在 bytecode 上工作。

```java
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;

/**
 * Powermock 支持 mock 静态方法示例
 */
@RunWith(PowerMockRunner.class) // 这必须设置使用 PowerMockRunner 运行器，替代 MockitoJUnitRunner，会兼容 @Mock 等注解。
@PrepareForTest(PowermockStaticDemoService.class)  // 声明要处理的类
public class PowermockStaticTest {
    @Test
    public void test() {
        PowerMockito.mockStatic(PowermockStaticDemoService.class);  // 声明模拟的类，这也是必须的
        when(PowermockStaticDemoService.add(1, 2)).thenReturn(100);
        Assert.assertEquals(100, PowermockStaticDemoService.add(1, 2));
    }
}

class PowermockStaticDemoService {
    public static int add(int a, int b) {
        return a + b;
    }
}
```

### 9.4. PowerMockRunner 支持 Mockito 的 @Mock 等注解

可以使用 PowerMockRunner 运行器替代 MockitoJUnitRunner。PowerMock 兼容 `@Mock` 等注解。例如：

```java
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Random;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class MockitoDemo {
    @Mock
    private Random random;

    @Test
    public void test() {
        when(random.nextInt()).thenReturn(1);
        Assert.assertEquals(1,  random.nextInt());
    }
}
```

## 10. 参考资料

- [万字长文：一文详解单元测试干了什么](https://mp.weixin.qq.com/s/9_TQbVSl1CQLQzuUrsrHLQ)
