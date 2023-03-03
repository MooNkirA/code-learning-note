# JUnit

## 1. 概述

JUnit 是 Java 语言编写第三方单元测试框架。简单理解为可以用于取代使用 java 的 main 方法来测试代码。JUnit 属于第三方工具，一般情况下需要导入jar 包。不过多数 Java 开发环境已经集成了JUnit 作为单元测试工具。

### 1.1. 单元测试

在 Java中，一个类就是一个单元。开发者编写的一小段代码，用来检验某个类中的某个方法是否正确执行。

### 1.2. 作用

可以让符合条件的方法独立运行(不依赖main方法)

## 2. JUnit 的基础使用步骤

整体的使用步骤：

1. 编写业务类
2. 编写测试类（编写测试方法）

### 2.1. 导入 JUnit

- 普通的 Java 项目：可以直接通过导入 jar 包的方式进行使用
- maven 项目：在 pom.xml 文件中，引入 JUnit 依赖

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>${junit.version}</version>
    <!-- 设置 junit 的作用范围 -->
    <scope>test</scope>
</dependency>
```

### 2.2. 编写测试方法

```java
public class BasicTest {
    /* 测试方法 */
    @Test
    public void testInitializationSequence() {
        InitSequenceBean bean = new InitSequenceBean();
        System.out.println(bean);
    }
}
```

> Notes: <font color=red>**测试方法必须是 `public` 修饰，没有返回值，没有方法参数，必须使用 `@Test` 注解修饰**</font>

### 2.3. 运行测试方法

elipse：选中（方法/类/项目），右键 -> Run As -> JUnit Test

- **选中方法**：只执行该测试方法
- **选中类**：执行类中所有的测试方法
- **选中项目**：执行项目中所有测试类中的测试方法

### 2.4. 查看测试结果

- 输出标识是<font color=green>**绿色**</font>：表示测试没有问题
- 输出标识是<font color=red>**红色**</font>：表示测试失败，有 Bug

### 2.5. 辅助判断错误的方法

示例代码：

```java
@Test
public void testSaveProduct(){
	// 创建产品对象
	boolean b = dao.saveProduct(new Product());
	/* 
	 * 断言：预先判断某个条件一定成立，如果不成立，则直接崩溃。
	 *   message：当期望值和实际值不一致，显示的提示信息
	 *   expected：期望值
	 *   actual：实际值
	 *   java.lang.AssertionError: 期望值和实际值不一致 expected:<true> but was:<false>
	 */
	Assert.assertEquals("期望值和实际值不一致", false, b);
	System.out.println(b);
}
```

## 3. JUnit 常用注解

### 3.1. @Test

用于标识测试方法。

### 3.2. @Before

修饰方法，该方法会在**每一个测试方法执行之前执行一次**。一般用于初始化测试的数据。

### 3.3. @After

修饰方法，该方法**会在每一个测试方法执行之后执行一次**。一般用于测试后关闭资源。

### 3.4. @BeforeClass

修饰静态方法，该方法**会在所有的测试方法执行之前执行一次**。注意必须是 `static` 修饰的方法，一般用于初始化。

### 3.5. @AfterClas

修饰静态方法，该方法**会在所有的测试方法执行之后执行一次**。注意必须是 `static` 修饰的方法，一般用于关闭资源。

## 4. JUnit 使用规范

### 4.1. 测试类规范

测试类命名规范：一般建议 `Test+业务类名`

### 4.2. 测试方法规范

测试方法命名规范：`Test+被测试方法的名字`，使用驼峰命名方式

