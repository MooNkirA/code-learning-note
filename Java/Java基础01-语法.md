# Java 基础语法

一个 Java 程序可以认为是一系列对象的集合，而这些对象通过调用彼此的方法来协同工作。一个基础的程序涉及如下的概念：

- **对象**：对象是类的一个实例，有状态和行为。例如，一条狗是一个对象，它的状态有：颜色、名字、品种；行为有：摇尾巴、叫、吃等。
- **类**：类是一个模板，它描述一类对象的行为和状态。
- **方法**：方法就是行为，一个类可以有很多方法。逻辑运算、数据修改以及所有动作都是在方法中完成的。
- **实例变量**：每个对象都有独特的实例变量，对象的状态由这些实例变量的值决定。

编写 Java 程序时，应注意以下几点：

- 大小写敏感：Java 是大小写敏感的，这就意味着标识符 `Hello` 与 `hello` 是不同的。
- 类名：对于所有的类来说，类名的首字母应该大写。如果类名由若干单词组成，那么每个单词的首字母应该大写，例如 `MyFirstJavaClass`
- 方法名：所有的方法名都应该以小写字母开头。如果方法名含有若干单词，则后面的每个单词首字母大写。
- 源文件名：源文件名必须和类名相同。当保存文件的时候，你应该使用类名作为文件名保存（切记 Java 是大小写敏感的），文件名的后缀为 `.java`。（如果文件名和类名不相同则会导致编译错误）。
- 主方法入口：所有的 Java 程序由 `public static void main(String[] args)` 方法开始执行。

Java 源程序与编译型运行区别

![](images/371723816239279.png)

## 1. Java 关键字

以下是 Java 关键字汇总表。*其中保留字不能用于常量、变量、和任何标识符的名称*。

> 后续在各个知识点中再详细说明

### 1.1. 访问控制

|    关键字    |   说明   |
| :---------: | -------- |
|  `private`  | 私有的   |
| `protected` | 受保护的 |
|  `public`   | 公共的   |
|  `default`  | 默认     |

### 1.2. 类、方法和变量修饰符

|     关键字      |             说明             |
| :------------: | ---------------------------- |
|   `abstract`   | 声明抽象                      |
|    `class`     | 类                           |
|   `extends`    | 扩充，继承                    |
|    `final`     | 最终值，不可改变的             |
|  `implements`  | 实现（接口）                  |
|  `interface`   | 接口                         |
|    `native`    | 本地，原生方法（非 Java 实现） |
|     `new`      | 新，创建                      |
|    `static`    | 静态                         |
|   `strictfp`   | 严格，精准                    |
| `synchronized` | 线程，同步                    |
|  `transient`   | 短暂                         |
|   `volatile`   | 易失                         |

实现一些其他的功能，Java 也提供了许多非访问修饰符。

- `static` 修饰符，用来修饰类方法和类变量。
- `final` 修饰符，用来修饰类、方法和变量，final 修饰的类不能够被继承，修饰的方法不能被继承类重新定义，修饰的变量为常量，是不可修改的。
- `abstract` 修饰符，用来创建抽象类和抽象方法。
- `synchronized` 和 `volatile` 修饰符，主要用于线程的编程。

### 1.3. 程序控制语句

|    关键字     |           说明            |
| :----------: | ------------------------ |
|   `break`    | 跳出循环                  |
|  `continue`  | 继续                      |
|  `default`   | 默认                      |
|     `do`     | 运行                      |
|    `else`    | 否则                      |
|    `for`     | 循环                      |
|     `if`     | 如果                      |
| `instanceof` | 实例                      |
|   `return`   | 返回                      |
|   `switch`   | 根据值选择执行             |
|    `case`    | 定义一个值以供 switch 选择 |
|   `while`    | 循环                      |

### 1.4. 错误处理

|   关键字   |         说明         |
| :-------: | -------------------- |
| `assert`  | 断言表达式是否为真     |
|  `catch`  | 捕捉异常              |
| `finally` | 有没有异常都执行       |
|  `throw`  | 抛出一个异常对象       |
| `throws`  | 声明一个异常可能被抛出 |
|   `try`   | 捕获异常              |

### 1.5. 包相关

|   关键字   | 说明 |
| :-------: | ---- |
| `import`  | 引入 |
| `package` | 包   |

### 1.6. 基本类型

|   关键字   |    说明    |
| :-------: | --------- |
| `boolean` | 布尔型     |
|  `byte`   | 字节型     |
|  `char`   | 字符型     |
| `double`  | 双精度浮点 |
|  `float`  | 单精度浮点 |
|   `int`   | 整型       |
|  `long`   | 长整型     |
|  `short`  | 短整型     |

### 1.7. 变量引用

|  关键字  |   说明    |
| :-----: | --------- |
| `super` | 父类，超类 |
| `this`  | 本类      |
| `void`  | 无返回值   |

### 1.8. 保留关键字

|  关键字  |        说明         |
| :-----: | ------------------ |
| `goto`  | 是关键字，但不能使用 |
| `const` | 是关键字，但不能使用 |

## 2. Java 基本数据类型（整理中）

变量就是申请内存来存储值。也就是说，当创建变量的时候，需要在内存中申请空间。

内存管理系统根据变量的类型为变量分配存储空间，分配的空间只能用来储存该类型数据。

![](images/171915709220856.png)

因此，通过定义不同类型的变量，可以在内存中储存整数、小数或者字符。Java 的两大数据类型:

- 内置数据类型
- 引用数据类型

### 2.1. 内置数据类型

#### 2.1.1. char

char 类型是一个单一的 16 位 Unicode 字符。最小值是 \u0000（十进制等效值为 0）；最大值是 \uffff（即为 65535）。

char 数据类型可以储存任何字符；<font color=red>**值得注意的是：字符是使用`''`单引号包裹**</font>

```java
char letter = 'A';
```

> 番外：字符型常量和字符串常量的区别
>
> - 形式上：字符常量是单引号引起的一个字符；字符串常量是双引号引起的若干个字符
> - 含义上：字符常量相当于一个整形值(ASCII值)，可以参加表达式运算；字符串常量代表一个地址值(该字符串在内存中存放位置)
> - 占内存大小：字符常量只占一个字节；字符串常量占若干个字节(至少一个字符结束标志)

## 3. 权限修饰符

**权限大小顺序**：`private < 默认 < protected < public`

- public: 任意包下任意类都可以访问；
- protected: 任意包下任意子类都可以访问或同包下的任意类
- 默认(包权限): 同包下的任意类都可以访问
- private: 只能在本类中使用

**修饰符权限列表图**：

|                       | public | protected | 空的（default） | private |
| --------------------- | :----: | :-------: | :-------------: | :-----: |
| 同一类中               |   ✔    |     ✔     |       ✔       |    ✔    |
| 同一包中（子类与无关类） |   ✔    |     ✔     |       ✔       |         |
| 不同包的子类            |   ✔    |     ✔     |                 |         |
| 不同包中的无关类         |   ✔    |           |                 |         |

> 类的成员不写访问修饰时默认为`default`。默认对于同一个包中的其他类相当于公开（`public`），对于不是同一个包中的其他类相当于私有（`private`）。受保护（`protected`）对子类相当于公开，对不是同一包中的没有父子关系的类相当于私有。

- **总结：在日常开发过程中，编写的类、方法、成员变量的访问**
    - 要想仅能在本类中访问使用 `private` 修饰;
    - 要想本包中的类都可以访问不加修饰符即可;
    - 要想本类与子类可以访问使用 `protected` 修饰;
    - 要想任意包中的任意类都可以访问使用 `public` 修饰;
- **注意项总结**：
    - 如果类用 `public` 修饰，则类名必须与文件名相同。一个文件中只能有一个 `public` 修饰的类。
    - Java 中，外部类的修饰符只能是 `public` 或默认 ，类的成员（包括内部类）的修饰符可以是以上四种。

## 4. 数组

### 4.1. 概述

数组对于每一门编程语言来说都是重要的数据结构之一，Java 语言中提供的数组是用来存储固定大小的同类型元素。

### 4.2. 数组声明（定义）语法

声明数组变量的语法，有如下两种方式：

- 方式一(**推荐**)：

```java
数据类型[] 数组变量名;
```

- 方式二：

```java
数据类型 数组变量名[];
```

> Notes: 建议使用 `dataType[] arrayRefVar` 的声明风格声明数组变量。`dataType arrayRefVar[]` 风格是来自 C/C++ 语言，在 Java 中采用是为了让 C/C++ 程序员能够快速理解 Java 语言。

示例：

```java
// 定义了一个int类型的数组，数组名是arr
int[] arr;
double[] myList;
// 定义了一个int类型的变量，变量名是arr数组
int arr[];
double myList[];
```

### 4.3. 数组初始化(创建数组)

数组初始化，就是为数组开辟内存空间，并为数组中的每个元素赋予初始值。Java 语言使用 `new` 关键字来创建数组，有以下两种方式可以实现数组的初始化：

1. 动态初始化：只给数组规定长度，由系统给出初始化值。例如：

```java
int[] arr = new int[3]
```

2. 静态初始化：给数组初始化值，由系统决定长度。例如：

```java
int[] arr = new int[]{1,2,3};
// 简化版
int[] arr = {1,2,3};
```

### 4.4. 数组的使用

#### 4.4.1. 数组的长度属性

数组提供了一个属性：`length`，用于获取数组中数元素的个数。

```java
int arrLength = 数组变量名.length
```

#### 4.4.2. 数组的索引

数组的元素是通过索引访问的。数组索引从 0 开始，因此索引值范围从 0 到 `数组.length-1`。

```java
int[] arr = {1,2,3};
// 根据索引获取数组第二个元素
int a = arr[1];
```

#### 4.4.3. 循环数组

数组的元素类型和数组的大小都是确定的，所以当处理数组元素时候，通常使用基本循环或者 For-Each 循环。

- 基础 for 循环

```java
double[] myList = {1.9, 2.9, 3.4, 3.5};

// 打印所有数组元素
for (int i = 0; i < myList.length; i++) {
    System.out.println(myList[i] + " ");
}
// 计算所有元素的总和
double total = 0;
for (int i = 0; i < myList.length; i++) {
    total += myList[i];
}
System.out.println("Total is " + total);
// 查找最大元素
double max = myList[0];
for (int i = 1; i < myList.length; i++) {
    if (myList[i] > max) max = myList[i];
}
System.out.println("Max is " + max);
```

- JDK 1.5 引进了一种新的循环类型，被称为 For-Each 循环或者加强型循环，它能在不使用下标的情况下遍历数组。语法格式如下：

```java
for(数据类型 数组中元素变量: 数组变量名) {
    // do something...
}
```

示例

```java
double[] myList = {1.9, 2.9, 3.4, 3.5};
 
// 打印所有数组元素
for (double element: myList) {
    System.out.println(element);
}
```

#### 4.4.4. 数组常见小问题

- ArrayIndexOutOfBoundsException（数组索引越界异常），产生的原因是访问了不存在的索引
- NullPointerException（空指针异常），产生的原因是数组已经不在指向堆内存的数据了，还使用数组变量去访问元素

### 4.5. 二维数组【了解】

二维数组，就是一个特殊的一维数组，该数组的元素是一维数组。

#### 4.5.1. 定义语法

有以下三种声明二维数组的方式：

1. `数据类型[][] 数组名;` (推荐的方式)
2. `数据类型 数组名[][];`
3. `数据类型[] 数组名[];`

#### 4.5.2. 初始化语法

二维数组的初始化语法与普通数组一样，有以下两种方式：

1. 动态初始化

```java
数据类型[][] 数组名 = new 数据类型[m][n];
```

- 参数 `m` 表示的是二维数组中一维数组的个数
- 参数 `n` 表示的是一维数组中的元素个数

2. 静态初始化

```java
数据类型[][] 数组名 = new 数据类型[][]{{元素...},{元素...},{元素...},...};
// 简化格式：
数据类型[][] 数组名 = {{元素...},{元素...},{元素...},...};
```

#### 4.5.3. 数据操作

二维数组名配合索引可以获取到每一个元素（一维数组）。每一个一维数组配合索引名可以获取到数组中的元素。

假如有一个二维数组：arr

- 要从中获取某个元素（一维数组）：`arr[索引]`
- 要从中获取二维数组的元素：`arr[索引][索引]`

## 5. static 关键字

### 5.1. 概述

`static` 是一个修饰符。可以用于修饰成员变量，成员方法以及代码块。

<font color=red>**`static` 关键字的主要意义是，在于创建独立于具体对象的域变量或者方法。以致于即使没有创建对象，也能使用属性和调用方法！还有一个比较关键的作用就是，用来形成静态代码块以优化程序性能。**</font>

### 5.2. static 修饰变量

有 static 修饰的变量，称为静态变量（类变量）。没有 static 修饰的变量，称为成员变量（实例变量）。

static 关键字用来声明的静态变量是独立于对象的，静态成员变量是属于类，不再属于某个对象，会被该类的所有对象共享。无论一个类实例化多少对象，它的静态变量只有一份拷贝。若有一个对象修改了静态变量的值，其他对象会受影响。

```java
private static 类型 变量名称 = 值;
```

建议使用类名访问(`类名.xxx`)静态属性，不推荐使用对象访问(`对象名.xxx`)。

> Notes: <font color=red>**static 修饰的成员变量，在类的加载过程中，JVM只为静态变量分配一次内存空间。而初始化的顺序是按照定义顺序来进行**</font>

### 5.3. static 修饰方法

使用 static 修饰的方法，称为静态方法（类方法）。没有 static 修饰的方法，称为成员方法（实例对象方法）。

static 关键字用来声明独立于对象的静态方法。静态方法中不能使用类的非静态变量。静态方法从参数列表得到数据，然后计算这些数据。

```java
public static void foo() {
    // ...
}
```

可以通过类名直接调用静态方法（推荐），即 `类名.静态方法`；也可以使用实例调用（不推荐），即 `对象.静态方法`

### 5.4. static 修饰代码块

被 `static` 修饰的代码块称为“静态代码块”，可以定义在类中的任意位置，并且可以定义多个。在类初次被加载的时候，会按照定义的顺序来执行每个 static 代码块，并且只会执行一次。静态代码块的执行优先级高于非静态的代码块。

```java
// 静态代码块
static {
    // ...
}

// 非静态代码块
{
    // ...
}
```

> Tips: 根据静态代码只会在类加载的时候执行一次的特性。因此最常见的应用场景就是，将一些只需要进行一次的初始化操作都放在 static 代码块中进行。

### 5.5. static 修饰类【只能修饰内部类也就是静态内部类】（待整理）

static 修饰类，只能用于修饰内部类也就是静态内部类

### 5.6. 静态导包（待整理）

TODO: 待整理

### 5.7. 总结

#### 5.7.1. 静态成员变量和成员变量的区别

1. 语法区别
	- 静态成员变量：有 static 修饰的;
	- 成员变量：没有 static 修饰的;
2. 数量区别
	- 静态成员变量：在内存中只存在一份，在类的加载过程中，JVM只为静态变量分配一次内存空间并初始化一次，会受每一个对象的影响
	- 成员变量：每创建一个对象，都会为成员变量分配内存。每一个对象都有一份自己的成员变量。互不干扰的。
3. 生命周期区别
	- 静态成员变量：在类加载的时候完成内存分配并初始化。(类只会加载一次)。跟随类的卸载面销毁。
	- 成员变量：在创建对象的时候完成内存分配和初始化。跟随对象的销毁而销毁。
4. 访问方式区别
	- 静态成员变量：可以通过类名访问(`类.xxx`)，也可以通过对象名访问(不推荐);
	- 成员变量：只能通过对象名访问(`对象名.xxx`);

#### 5.7.2. 静态方法和成员方法的区别

1. 调用方式
    - 静态方法可以通过类名调用(`类.xxx`)，也可以通过对象名调用(不推荐);
    - 成员方法只能通过对象名调用(`对象名.xxx`);
2. 成员的访问限制
    - 静态方法中不能访问非静态成员(成员变量和成员方法)，只能访问带有 `static` 修饰的静态变量
    - 成员方法则无成员的访问限制。如果在本类中，直接通过成员变量名或方法名来使用，在其他类中，需要(`类名.成员变量名`)或(`类.方法名`)才能使用;

#### 5.7.3. static 的注意事项

静态方法中不能使用 `this` 和 `super` 关键字。(因为 `this` 是代表当前对象的引用，如果没有创建对象， `this` 没有任何意义。)

#### 5.7.4. static 使用场景

**静态变量**

- 当某个成员变量在值需要在该类的所有对象共享时就可以将该变量定义为静态成员变量

**静态方法**

- 如果方法中没有使用任何非静态成员，就可以将该方法定义为静态方法;(因为静态方法可以直接用类名调用(`类.xxx)`，比较方便)
- 定义工具类时，如果一个类中的所有方法都是静态方法，则该类可以认为是一个工具类

## 6. final 关键字

### 6.1. final 概述

final 也是一个修饰符。可以修饰变量、方法、类

### 6.2. final 修饰变量

- 修饰基本数据类型的变量：被它修饰的变量其实是一个常量(常量命名是全部字母大写，多个单词用“_”分隔)，只能赋值一次，不能再修改。
    - 常见格式: `public static final int NUM = 10;`
- 修饰引用数据类型变量：此时该引用变量就不能再指向其他对象，但可以修改已经指向对象的成员变量的值(只要该成员变量不是使用 final 修饰)。(相当于此引用变量的地址值固定，不能改变)

### 6.3. final 修饰方法

- 该方法不能被子类重写。使用final修饰方法有以下两个原因
    - 第一个原因是把方法锁定，以防任何继承类修改它的含义；
    - 第二个原因是效率。在早期的 Java 实现版本中，会将final 方法转为内嵌调用。但是如果方法过于庞大，可能看不到内嵌调用带来的任何性能提升（现在的 Java 版本已经不需要使用 final 方法进行这些优化了）。类中所有的 private 方法都隐式地指定为 final。

```java
// 原代码
public static void test(){
    String s1 = "包夹方法a";
    a();
    String s2 = "包夹方法a";
}

public static final void a(){
    System.out.println("我是方法a中的代码");
    System.out.println("我是方法a中的代码");
}

// 经过编译后
public static void test(){
    String s1 = "包夹方法a";
    System.out.println("我是方法a中的代码");
    System.out.println("我是方法a中的代码");
    String s2 = "包夹方法a";
}
```

### 6.4. final 修饰类

- 使用 final 修饰类的目的简单明确：表明该类不能再被其他类继承。
- 被 final 修饰的类中，所有成员方法都会被隐式地指定为 final 方法。(就不存在方法重写的情况，只能创建对象和调用。)

```java
public final class Xxx{
}
```

### 6.5. final 注意事项

当引用变量使用 final 修饰时，表示其指向的地址值不能发生改变，但指向对象的成员变量值可以改变。

### 6.6. 常量治理

虽然推荐在 java 中使用枚举来对数据字典及常量进行控制，但是有些时候，还是使用常量控制更为便捷。比如，对于数据字典，可以使用枚举值来处理；对于一些其他的信息，还是会使用常量保存和使用。

#### 6.6.1. 代码的坏味道

很多程序中的常量类，随着业务与功能的迭代，类中定义的常量越来越多，导致类中行数过多，造成查找内容十分不方便。例如：


```java
public class Constants {
    public static final String REAL_NAME1 = "v1";
    public static final String REAL_NAME2 = "v2";
    public static final String REAL_NAME3 = "v3";
    public static final String REAL_NAME4 = "v4";
    public static final String REAL_NAME5 = "v5";
    public static final String REAL_NAME5 = "v6";
    public static final String REAL_NAME7 = "v7";
    public static final String REAL_NAME8 = "v8";
    public static final String REAL_NAME9 = "v9";
    ......
}
```

一个无穷尽的常量类，设想这个类篇幅巨长，这样的常量管理会带来的问题如下：

1. 不好维护，相关的代码写到一起，此时，常量的篇幅较长导致找不到对应的常量块进行维护；
2. 虽然是在不同的业务场景下，但是有些常量的名称还是有可能重复；
3. 有时为了减少常量的定义，就得共用一些常量，而这样的共用会导致某种业务场景下需要对该常量进行修改，而导致另外一些业务场景下的常量使用产生歧义；

因此常能看到一些常量类的“代码里的坏味道”，假如常量名称如上所示，名称类似的很多；名称不明确的也很多，还没有注释，这样的歧义也是因为代码不好管理造成的。想对这种常量类进行新增或修改内容，是十分困难。

#### 6.6.2. 初级治理 - 使用内部类

使用 java 的内部类进行常量的初步治理，对常量根据不同的业务模块进行管理，代码如下：

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

#### 6.6.3. 中级治理 - 集中管理

在初级治理中，是无法实现通过 value 来获取到常量 key 。进而出现中级治理，主要是通过 map，每个内部类都会存为 map 中的一个 entry，每个 entry 又都是 map 类型的集合，集合中包含该内部类的所有常量。

代码如下：

```java
public class Constants {
    public static final Map<String, Map<String, String>> keyValueMapCons = new LinkedHashMap<>();

    public static final Map<String, Map<String, String>> valueKeyMapCons = new LinkedHashMap<>();

    /* 初始化所有常量 */
    static {
        try {
            // 获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyValueMap = new LinkedHashMap<>(); // 存放 key 和 value 的 map
                Map<String, String> valueKeyMap = new LinkedHashMap<>(); // 存放 value 和 key 的 map，每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    keyValueMap.put(fd.getName(), fd.get(cls).toString()); // 注解对象空，其值为该 field 的值
                    valueKeyMap.put(fd.get(cls).toString(), fd.getName());
                }
                // 以内部类的名称作为 key 保存
                keyValueMapCons.put(cls.getSimpleName(), keyValueMap);
                valueKeyMapCons.put(cls.getSimpleName(), valueKeyMap);
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

示例中在 `Constants` 类中维护了两个 Map 常量集合：

- 一个名为 `keyValueMapCons` 的 Map 集合，按照内部类的名称作为 key 存储，其 value 是一个常量 Map 集合，该结构是将常量的名字作为 map 的 key，值作为 map 的 value；
- 一个名为 `valueKeyMapCons` 的 Map 集合，按照内部类的名称作为 key 存储，其 value 是一个常量 Map 集合，该结构是将常量的值作为 map 的 key，常量的名字作为 map 的 key。

这样就可以通过这两个 map 集合，根据不同的的使用需求来获取相应的常量。使用示例代码如下：

```java
@Test
public void test1(){
    System.out.println(Constants.keyValueMapCons.get("TOKEN_FLAG_ONE").get("REAL_NAME")); // v1
    System.out.println(Constants.valueKeyMapCons.get("TOKEN_FLAG_ONE").get("v5")); // GUR
}
```

#### 6.6.4. 中高级治理 - 使用注解

目前实现了通过 key 获取到 value，也可以通过 value 获取到 key 了。但在使用常量时，不光要有常量的定义、常量的值，还应该有对常量的描述，而传统的对于常量的定义，往往只在通过 ide 的文档功能来查看常量的描述。

可以通过注解的方式来实现存储常量描述的功能，例如自定义注解类型如下：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConstantAnnotation {
    String value(); // 用于记录常量的描述
}
```

通过自定义的注解，将常量的中文描述都记录到注解的 `value` 属性中。

```java
public class Constants {
    private static final String CONSTANT_STRING = "这是一条短信消息";

    public static final class TOKEN_FLAG_ONE {
        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING) // 把描述与注解分离
        public static final String GUR = "v5";
    }
}
```

比如，我们希望在某个业务场景下，符合gur常量的业务，发送一条短信消息，而这个消息我们就可以定义在我们的自定义注解中。例如GUR这个常量，我们把它的描述声明成一个常量，这个常量可用来存放对应的短信消息。我们的常量类中如果再有一个通过常量获取到描述的map，这是不是就完美了？

于是，我们有了下面的代码：

```java
public class Constants {

    // 存储常量描述的 Map 集合，根据常量名称来获取常量描述的值
    public static final Map<String, Map<String, String>> keyDescMapCons = new LinkedHashMap<>();

    /* 初始化所有常量 */
    static {
        try {
            // 获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyDescMap = new LinkedHashMap<>(); // 存放常量 key 和 desc 的 map

                // 每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    // 每个属性获取指定的 annotation 的注解对象
                    ConstantAnnotation ca = fd.getAnnotation(ConstantAnnotation.class);
                    if (ca != null) {
                        keyDescMap.put(fd.getName(), ca.value()); // 注解对象不空，常量的描述即为注解对象中的值
                    }
                }
                keyDescMapCons.put(cls.getSimpleName(), keyDescMap);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String CONSTANT_STRING = "这是一条短信消息";

    public static final class TOKEN_FLAG_ONE {
        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING) // 把描述与注解分离
        public static final String GUR = "v5";
    }
}
```

经过改造后，可以通过 keyDescMap 集合，根据常量的名称获取到该常量对应的描述了。使用示例代码如下：

```java
@Test
public void test1(){
    System.out.println(Constants.keyDescMapCons.get("TOKEN_FLAG_ONE").get("GUR")); // 打印输出“这是一条短消息”
}
```

#### 6.6.5. 综合治理（终极治理）

目前有了常量的治理，有了注解的描述，有时需要通过key获取到value，有时需要通过value获取描述，有时需要通过key获取到描述等等。排列组合共6种形式，以下是综合治理的实现（虽然实际项目中不一定有用得上）。代码如下：

```java
public class Constants {

    public static final Map<String, Map<String, String>> keyValueMapCons = new LinkedHashMap<>();
    public static final Map<String, Map<String, String>> keyDescMapCons = new LinkedHashMap<>();
    public static final Map<String, Map<String, String>> descValueMapCons = new LinkedHashMap<>();
    public static final Map<String, Map<String, String>> descKeyMapCons = new LinkedHashMap<>();
    public static final Map<String, Map<String, String>> valueDescMapCons = new LinkedHashMap<>();
    public static final Map<String, Map<String, String>> valueKeyMapCons = new LinkedHashMap<>();

    /* 初始化所有常量 */
    static {
        try {
            // 获取所有内部类
            for (Class cls : Constants.class.getClasses()) {
                Map<String, String> keyDescMap = new LinkedHashMap<>(); // 存放key和desc的map
                Map<String, String> keyValueMap = new LinkedHashMap<>(); // 存放key和value的map
                Map<String, String> valueKeyMap = new LinkedHashMap<>(); // 存放value和key的map
                Map<String, String> valueDescMap = new LinkedHashMap<>(); // 存放value和desc的map
                Map<String, String> descValueMap = new LinkedHashMap<>(); // 存放desc和value的map
                Map<String, String> descKeyMap = new LinkedHashMap<>(); // 存放desc和key的map
                // 每个内部类-获取所有属性（不包括父类的）
                for (Field fd : cls.getDeclaredFields()) {
                    // 每个属性获取指定的 annotation的 注解对象
                    ConstantAnnotation ca = fd.getAnnotation(ConstantAnnotation.class);
                    keyValueMap.put(fd.getName(), fd.get(cls).toString()); // 获取该 field 的名称
                    valueKeyMap.put(fd.get(cls).toString(), fd.getName());
                    if (ca != null) {
                        keyDescMap.put(fd.getName(), ca.value()); // 注解对象不空，常量的描述即为注解对象中的值
                        valueDescMap.put(fd.get(cls).toString(), ca.value());
                        descValueMap.put(ca.value(), fd.get(cls).toString());
                        descKeyMap.put(ca.value(), fd.getName());
                    }
                }
                keyValueMapCons.put(cls.getSimpleName(), keyValueMap);
                keyDescMapCons.put(cls.getSimpleName(), keyDescMap);
                descValueMapCons.put(cls.getSimpleName(), descValueMap);
                descKeyMapCons.put(cls.getSimpleName(), descKeyMap);
                valueDescMapCons.put(cls.getSimpleName(), valueDescMap);
                valueKeyMapCons.put(cls.getSimpleName(), valueKeyMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String CONSTANT_STRING = "这是一条短信消息";

    public static final class TOKEN_FLAG_ONE {
        @ConstantAnnotation("实名")
        public static final String REAL_NAME = "v1";

        @ConstantAnnotation("证书")
        public static final String CRET = "v2";

        @ConstantAnnotation(CONSTANT_STRING) // 把描述与注解分离
        public static final String GUR = "v5";
    }
}
```

以上就是综合治理后的常量类，使用先拿常量的声明，再用对应的map，然后指定内部类名称，根据具体需要获取相应的内容。

> Tips: <font color=red>**枚举治理和常量治理结合使用，可能才是系统开发时的最佳实践**</font>

## 7. 包 package

### 7.1. 包的概述

包就是文件夹。分包管理是组织软件项目结构的基本方式。将同类功能放到一个包中，方便管理。并且日常项目的分工也是以包作为边界。

包在文件系统中是以文件夹的形式存在的。类中定义的包必须与实际class件所在的文件夹情况相统一，即定义包时类在a包下，则生成的.class 文件必须在a文件夹下，否则找不到类。

### 7.2. 包的作用

- 避免类命名冲突。
- 将功能相似或相关的类和接口组织在同一个文件夹，方便类的查找和使用。

### 7.3. 包的定义格式

- 定义格式：`package com.qq.包名1.xxx...;`
- 规范：一般以公司域名倒着写，最后是功能内容的分类。即 `www.qq.com ===> com.qq.login`
- 多级包使用“.”分割，包名全部小写英文字母，一般不用数字。

### 7.4. 包的注意事项

- 定义包的语句必须是类中第一行语句。
- 如果定义多个类，只能有一个是 public 修饰。且文件名一定要与 public 修饰的类名一致。
- 类和所生成的 class 必须在相同的目录结构下。

### 7.5. 导包格式

导包位置：**package 的下面，class 的上面**

```java
import 包名.包名.xxx...类名;
import java.util.*;	// 将指定包下的所有类导入
```

### 7.6. 类的访问方式

**带包类全名访问**：

当在一个类中需要使用两个不同包下同名的类时，只能有一个类被导包，另一个类只能使用类全名方式访问。

**不带包名访问**：

- 被使用的类在java.lang包下。
- 被使用的类和当前类是在同一个包下。
- 使用导包方式访问。

> 跨包导包访问注意事项：如果在一个类中需要使用不同包下相同类名的类，只能有一个被导包，其他只能通过类全名访问。

code demo

```java
package com.moon.packaeg;
import com.moon.statci.Student;

public class Test01 {
    public static void main(String[] args) {
        // 创建Person对象
        // 带包类全名访问:不需要导入
        com.moon.packaeg.Person p = new com.moon.packaeg.Person();

        // 创建学生对象
        Student s = new Student();

        // 在java.lang包下的类：不需要导包
        String str = "nihao";

        com.moon.finla.Student s1 = new com.moon.finla.Student();
        s1.sleep();

        Person p1 = new Person();
    }
}
```

### 7.7. JDK 中常用的包

- java.lang：系统的基础类
- java.io：所有输入输出有关的类，比如文件操作等
- java.nio：为了完善 io 包中的功能，提高 io 包中性能而写的一个新包
- java.net：与网络有关的类
- java.util：系统辅助类，特别是集合类
- java.sql：数据库操作的类

> Tips: java 开头的包是较原始的 JavaAPI，javax 开头的包是扩展的 API

## 8. 可变参数

### 8.1. 可变参数概述

- **JDK1.5的新特性**，方法的参数类型相同，但是个数变化。
- **可变参数可以多个，也可以不传值**。

### 8.2. 可变参数格式

- 使用前提：数据类型明确，参数个数任意。
- 语法定义：`数据类型…` **【注意是3个点(.)】**

```java
修饰符 返回值类型 方法名(数据类型… 变量名) {
}
```

与普通方法相比在参数类型后面添加`…`

### 8.3. 可变参数的本质

可变参数方法本质是数组。所以不可以与数组类型参数重载。

### 8.4. 可变参数注意事项

1. 参数列表中只能有一个可变参数
2. 如果出现不同类型的参数，可变参数必须放在参数列表的最后

## 9. Java类的初始化顺序

Java类的创建，相应的初始化顺序是：`静态变量` -> `静态代码块` -> `成员变量（全局变量）` -> `初始化代码块` -> `构造函数`。测试示例如下：

```java
public class InitSequenceBean {

    private static String staticStr = initStaticMember();
    private String str = initOrdinaryMember();

    static {
        System.out.println("静态代码块执行了....");
    }

    {
        System.out.println("初始化代码块执行了....");
    }

    public InitSequenceBean() {
        System.out.println("无参构造函数执行了....");
    }

    private static String initStaticMember() {
        System.out.println("静态成员变量初始化....");
        return "123";
    }

    private String initOrdinaryMember() {
        System.out.println("普通成员变量初始化....");
        return "abc";
    }
}
```

测试代码与结果

```java
@Test
public void testInitializationSequence() {
    InitSequenceBean bean = new InitSequenceBean();
    System.out.println(bean);
    /*
     * 测试结果：
     *  静态成员变量初始化....
     *  静态代码块执行了....
     *  普通成员变量初始化....
     *  初始化代码块执行了....
     *  无参构造函数执行了....
     *  com.moon.java.basic.InitSequenceBean@ba8a1dc
     */
}
```

## 10. Java 运算符

计算机的最基本用途之一就是执行数学运算，作为一门计算机语言，Java 也提供了一套丰富的运算符来操纵变量。运算符主分成以下几类：

- 算术运算符
- 关系运算符
- 位运算符
- 逻辑运算符
- 赋值运算符
- 其他运算符

### 10.1. 算术运算符

算术运算符用在数学表达式中，它们的作用和在数学中的作用一样。下表列出了所有的算术运算符。

> 表格中的实例假设整数变量A的值为10，变量B的值为20：

| 操作符 |              描述               |          例子          |
| :----: | ------------------------------ | ---------------------- |
|  `+`   | 加法 - 相加运算符两侧的值         | `A + B` 等于 30        |
|  `-`   | 减法 - 左操作数减去右操作数       | `A – B` 等于 -10       |
|  `*`   | 乘法 - 相乘操作符两侧的值         | `A * B` 等于 200       |
|  `/`   | 除法 - 左操作数除以右操作数       | `B / A` 等于 2         |
|  `％`  | 取余 - 左操作数除以右操作数的余数 | `B%A` 等于 0           |
|  `++`  | 自增: 操作数的值增加1            | `B++` 或 `++B` 等于 21 |
|  `--`  | 自减: 操作数的值减少1            | `B--` 或 `--B` 等于 19 |

示例：

```java
public class Test {
    public static void main(String args[]) {
        int a = 10;
        int b = 20;
        int c = 25;
        int d = 25;
        System.out.println("a + b = " + (a + b)); // a + b = 30
        System.out.println("a - b = " + (a - b)); // a - b = -10
        System.out.println("a * b = " + (a * b)); // a * b = 200
        System.out.println("b / a = " + (b / a)); // b / a = 2
        System.out.println("b % a = " + (b % a)); // b % a = 0
        System.out.println("c % a = " + (c % a)); // c % a = 5
        System.out.println("a++   = " + (a++)); // a++ = 10
        System.out.println("a--   = " + (a--)); // a-- = 11
        // 观察 d++ 与 ++d 的不同
        System.out.println("d++   = " + (d++)); // d++ = 25
        System.out.println("++d   = " + (++d)); // ++d = 27
    }
} 
```

#### 10.1.1. 自增自减运算符

自增（`++`）自减（`--`）运算符是一种特殊的算术运算符，在算术运算符中需要两个操作数来进行运算，而自增自减运算符是一个操作数。又分以下两种：

- 前缀自增自减法(`++a`,`--a`)：先进行自增或者自减运算，再进行表达式运算。
- 后缀自增自减法(`a++`,`a--`)：先进行表达式运算，再进行自增或者自减运算

```java
public static void main(String args[]) {
    int a = 5; // 定义一个变量；
    int b = 5;
    int x = 2 * ++a;
    int y = 2 * b++;
    System.out.println("自增运算符前缀运算后a=" + a + ",x=" + x); // a=6,x=12
    System.out.println("自增运算符后缀运算后b=" + b + ",y=" + y); // b=6,y=10
}
```

### 10.2. 关系运算符

算术运算符用在数学表达式中，它们的作用和在数学中的作用一样。下表列出了所有的算术运算符。

> 表格中的实例假设整数变量A的值为10，变量B的值为20：

| 运算符 |                           描述                           |        例子        |
| :----: | -------------------------------------------------------- | ----------------- |
|  `==`  | 检查如果两个操作数的值是否相等，如果相等则条件为真            | `A == B` 为 false |
|  `!=`  | 检查如果两个操作数的值是否相等，如果值不相等则条件为真         | `A != B` 为 true  |
|  `>`   | 检查左操作数的值是否大于右操作数的值，如果是那么条件为真       | `A > B` 为 false  |
|  `<`   | 检查左操作数的值是否小于右操作数的值，如果是那么条件为真       | `A < B` 为 true   |
|  `>=`  | 检查左操作数的值是否大于或等于右操作数的值，如果是那么条件为真 | `A >= B` 为 false |
|  `<=`  | 检查左操作数的值是否小于或等于右操作数的值，如果是那么条件为真 | `A <= B` 为 true  |

示例：

```java
public static void main(String args[]) {
    int a = 10;
    int b = 20;
    System.out.println("a == b = " + (a == b)); // a == b = false
    System.out.println("a != b = " + (a != b)); // a != b = true
    System.out.println("a > b = " + (a > b)); // a > b = false
    System.out.println("a < b = " + (a < b)); // a < b = true
    System.out.println("b >= a = " + (b >= a)); // b >= a = true
    System.out.println("b <= a = " + (b <= a)); // b <= a = false
}
```

### 10.3. 位运算符

Java 定义了位运算符，应用于整数类型(int)，长整型(long)，短整型(short)，字符型(char)，和字节型(byte)等类型。

> 下表列出了位运算符的基本运算，假设整数变量 A 的值为 60 和变量 B 的值为 13：

| 操作符 |                                   描述                                   |              例子              |
| :----: | ------------------------------------------------------------------------ | ------------------------------ |
|  `&`  | 如果相对应位都是1，则结果为1，否则为0                                       | `A & B` 得到12，即0000 1100    |
|  `|`   | 如果相对应位都是0，则结果为0，否则为1                                       | `A | B` 得到61，即 0011 1101   |
|  `^`   | 如果相对应位值相同，则结果为0，否则为1                                       | `A ^ B` 得到49，即 0011 0001   |
|  `〜`  | 按位取反运算符翻转操作数的每一位，即0变成1，1变成0                            | `〜A` 得到-61，即1100 0011     |
|  `<<`  | 按位左移运算符。左操作数按位左移右操作数指定的位数                            | `A << 2` 得到240，即 1111 0000 |
|  `>>`  | 按位右移运算符。左操作数按位右移右操作数指定的位数                            | `A >> 2` 得到15，即 1111       |
| `>>>`  | 按位右移补零操作符。左操作数的值按右操作数指定的位数右移，移动得到的空位以零填充 | `A>>>2` 得到15，即0000 1111    |

位运算符作用在所有的位上，并且按位运算。它们的二进制格式表示将如下：

```
A = 0011 1100
B = 0000 1101
-----------------
A & B = 0000 1100
A | B = 0011 1101
A ^ B = 0011 0001
~A = 1100 0011
```

示例

```java
public static void main(String args[]) {
    int a = 60; /* 60 = 0011 1100 */
    int b = 13; /* 13 = 0000 1101 */
    int c = 0;
    c = a & b;       /* 12 = 0000 1100 */
    System.out.println("a & b = " + c); // a & b = 12

    c = a | b;       /* 61 = 0011 1101 */
    System.out.println("a | b = " + c); // a | b = 61

    c = a ^ b;       /* 49 = 0011 0001 */
    System.out.println("a ^ b = " + c); // a ^ b = 49

    c = ~a;          /*-61 = 1100 0011 */
    System.out.println("~a = " + c); // ~a = -61

    c = a << 2;     /* 240 = 1111 0000 */
    System.out.println("a << 2 = " + c); // a << 2 = 240

    c = a >> 2;     /* 15 = 1111 */
    System.out.println("a >> 2  = " + c); // a >> 2  = 15

    c = a >>> 2;     /* 15 = 0000 1111 */
    System.out.println("a >>> 2 = " + c); // a >>> 2 = 15
}
```

### 10.4. 逻辑运算符

下表列出了逻辑运算符的基本运算，假设布尔变量A为真，变量B为假

| 操作符 |                                     描述                                      |        例子         |
| :----: | ----------------------------------------------------------------------------- | ------------------- |
|  `&&`  | 称为逻辑与运算符。当且仅当两个操作数都为真，条件才为真                              | `A && B` 为 false   |
|  `||`  | 称为逻辑或操作符。如果任何两个操作数任何一个为真，条件为真                          | `A || B` 为 true    |
|  `!`   | 称为逻辑非运算符。用来反转操作数的逻辑状态。如果条件为true，则逻辑非运算符将得到false | `!(A && B)` 为 true |

示例：

```java
public static void main(String args[]) {
    boolean a = true;
    boolean b = false;
    System.out.println("a && b = " + (a && b)); // a && b = false
    System.out.println("a || b = " + (a || b)); // a || b = true
    System.out.println("!(a && b) = " + !(a && b)); // !(a && b) = true
}
```

#### 10.4.1. 短路逻辑运算符

当使用与逻辑运算符时，在两个操作数都为 true 时，结果才为 true，但是当得到第一个操作为 false 时，其结果就必定是 false，这时候就不会再判断第二个操作了。

示例

```java
public static void main(String args[]) {
    int a = 5; // 定义一个变量；
    boolean b = (a < 4) && (a++ < 10);
    System.out.println("使用短路逻辑运算符的结果为" + b); // false
    System.out.println("a的结果为" + a); // 5
}
```

> 解析：该程序使用到了短路逻辑运算符(`&&`)，首先判断 `a<4` 的结果为 `false`，则 b 的结果必定是 `false`，所以不再执行第二个操作 `a++ < 10` 的判断，所以 a 的值为 5

### 10.5. 赋值运算符

下面是Java语言支持的赋值运算符：

| 操作符 |                          描述                          |                   例子                    |
| :----: | ------------------------------------------------------ | ---------------------------------------- |
|  `=`   | 简单的赋值运算符，将右操作数的值赋给左侧操作数              | `C = A + B`将把`A + B`得到的值赋给C        |
|  `+=`  | 加和赋值操作符，它把左操作数和右操作数相加赋值给左操作数     | `C + = A`等价于`C = C + A`                |
|  `-=`  | 减和赋值操作符，它把左操作数和右操作数相减赋值给左操作数     | `C -= A`等价于`C = C - A`                 |
|  `*=`  | 乘和赋值操作符，它把左操作数和右操作数相乘赋值给左操作数     | `C *= A`等价于`C = C * A`                 |
|  `/=`  | 除和赋值操作符，它把左操作数和右操作数相除赋值给左操作数     | `C /= A`，C 与 A 同类型时等价于`C = C / A` |
| `%=`  | 取模和赋值操作符，它把左操作数和右操作数取模后赋值给左操作数 | `C %= A`等价于`C = C%A`                   |
| `<<=`  | 左移位赋值运算符                                         | `C << = 2`等价于`C = C << 2`              |
| `>>=`  | 右移位赋值运算符                                         | `C >> = 2`等价于`C = C >> 2`              |
| `&=`  | 按位与赋值运算符                                         | `C &= 2`等价于`C = C&2`                   |
|  `^=`  | 按位异或赋值操作符                                       | `C ^= 2`等价于`C = C ^ 2`                 |
|  `|=`  | 按位或赋值操作符                                         | `C |= 2`等价于`C = C | 2`                 |

示例：

```java
public static void main(String args[]) {
    int a = 10;
    int b = 20;
    int c = 0;
    c = a + b;
    System.out.println("c = a + b, c = " + c); // 30
    c += a;
    System.out.println("c += a, c = " + c); // 40
    c -= a;
    System.out.println("c -= a, c = " + c); // 30
    c *= a;
    System.out.println("c *= a, c = " + c); // 300
    a = 10;
    c = 15;
    c /= a;
    System.out.println("c /= a, c = " + c); // 1
    a = 10;
    c = 15;
    c %= a;
    System.out.println("c %= a , c = " + c); // 5
    c <<= 2;
    System.out.println("c <<= 2, c = " + c); // 20
    c >>= 2;
    System.out.println("c >>= 2, c = " + c); // 5
    c >>= 2;
    System.out.println("c >>= 2, c = " + c); // 1
    c &= a;
    System.out.println("c &= a, c = " + c); // 0
    c ^= a;
    System.out.println("c ^= a, c = " + c); // 10
    c |= a;
    System.out.println("c |= a, c = " + c); // 10
}
```

### 10.6. 条件运算符（三元运算符）

条件运算符也被称为三元运算符。该运算符有3个操作数，并且需要判断布尔表达式的值。该运算符的主要是决定哪个值应该赋值给变量。

```java
variable x = (expression) ? value if true : value if false
```

示例：

```java
public static void main(String args[]) {
    int a, b;
    a = 10;
    // 如果 a 等于 1 成立，则设置 b 为 20，否则为 30
    b = (a == 1) ? 20 : 30;
    System.out.println("Value of b is : " + b);

    // 如果 a 等于 10 成立，则设置 b 为 20，否则为 30
    b = (a == 10) ? 20 : 30;
    System.out.println("Value of b is : " + b);
}
```

#### 10.6.1. 三元表达式的类型转化规则

> 引用阿里巴巴Java开发手册的规则：
>
> 【强制】三目运算符 `condition ? 表达式 1：表达式 2` 中，高度注意表达式 1 和 2 在类型对齐时，可能抛出因自动拆箱导致的 NPE 异常。说明：以下两种场景会触发类型对齐的拆箱操作：
>
> 1. 表达式 1 或 表达式 2 的值只要有一个是原始类型。
> 2. 表达式 1 或 表达式 2 的值的类型不一致，会强制拆箱升级成表示范围更大的那个类型。

由于三元表达式拆包，有可能引发空指针异常，而三元表达式的类型转化规则如下：

- 若两个表达式类型相同，返回值类型为该类型
- 若两个表达式类型不同，但类型不可转换，返回值类型为 Object 类型
- 若两个表达式类型不同，但类型可以转化，先把包装数据类型转化为基本数据类型，然后按照基本数据类型的转换规则（`byte < short(char) < int < long < float < double`）来转化，返回值类型为优先级最高的基本数据类型。

#### 10.6.2. 三元表达式使用建议

1. 如果三元表达式中有包装数据类型的算术计算，尽量避免使用三元表达式，可以考虑利用 if-else 语句代替。
2. 如果在三元表达式中有算术计算，尽量使用基本数据类型，避免包装数据类型的拆装包。

### 10.7. instanceof 运算符

`instanceof` 运算符用于操作对象实例，检查该对象是否是一个特定类型（类类型或接口类型）。即判断父类引用指定的到底是哪一个子类类型的对象。使用格式如下：

```java
// 父类引用对象 instanceof 子类类名或接口名;
( Object reference variable ) instanceof  (class/interface type)
```

如果运算符左侧变量所指的对象，是操作符右侧类或接口(class/interface)的一个对象，那么结果为真。

示例

```java
String name = "James";
boolean result = name instanceof String; // 由于 name 是 String 类型，所以返回真

// public class Car extends Vehicle
Vehicle a = new Car();
boolean result = a instanceof Car; // true
```

#### 10.7.1. 注意事项

毫无关系的两个对象不能进行判断。`instanceof` 关键字前面的对象和后面的类型必须是子父类关系或类实现接口关系

### 10.8. Java 运算符优先级

当多个运算符出现在一个表达式中，就涉及到运算符的优先级别的问题。在一个多运算符的表达式中，运算符优先级不同会导致最后得出的结果差别甚大。

下表中具有最高优先级的运算符在的表的最上面，最低优先级的在表的底部。

| 类别     | 操作符                                                           | 关联性   |
| :------- | :-------------------------------------------------------------- | :------- |
| 后缀     | `()`、`[]`、`.`(点操作符)                                         | 左到右   |
| 一元     | `expr++`、`expr--`                                              | 从左到右 |
| 一元     | `++expr`、`--expr`、`+`、`-`、`～`、`!`                           | 从右到左 |
| 乘性     | `*`、`/`、`%`                                                    | 左到右   |
| 加性     | `+`、`-`                                                        | 左到右   |
| 移位     | `>>`、`>>>`、`<<`                                                | 左到右   |
| 关系     | `>`、`>=`、`<`、`<=`                                             | 左到右   |
| 相等     | `==`、`!=`                                                      | 左到右   |
| 按位与   | `&`                                                             | 左到右   |
| 按位异或 | `^`                                                             | 左到右   |
| 按位或   | `|`                                                             | 左到右   |
| 逻辑与   | `&&`                                                            | 左到右   |
| 逻辑或   | `||`                                                            | 左到右   |
| 条件     | `? :`                                                           | 从右到左 |
| 赋值     | `=`、`+=`、`-=`、`*=`、`/=`、`%=`、`>>=`、`<<=`、`&=`、`^=`、`|=` | 从右到左 |
| 逗号     | `,`                                                             | 左到右   |

## 11. Java 包装类（整理中）

### 11.1. 概述

一般地，当需要使用数字的时候，通常使用内置数据类型，如：byte、int、long、double 等。所有的包装类（Integer、Long、Byte、Double、Float、Short）都是抽象类 Number 的子类。

![](images/197444010239282.png)

基本类型包装类的产生原因：其实就是基本类型对应的引用类型(包装类)。在实际开发中，用户输入的内容都是以字符串形式存在，需要参与数学运算时需要将字符串转换成对应的基本数据类型。

### 11.2. 八种基本类型对应的包装类

| 基本类型 | 引用类型(包装类) | 基本类型 | 引用类型(包装类) |
| -------- | --------------- | -------- | --------------- |
| char     | Character       | long     | Long            |
| int      | Integer         | float    | Float           |
| byte     | Byte            | double   | Double          |
| short    | Short           | boolean  | Boolean         |

> Tips: 除了 char 与 int 的包装类之外，其他包装类类名称均为基本类型名称的首字母大写

```java
// 数据转换示例
int n = Integer.parseInt(String str);
double d = Double.parseDouble(String str);
```

### 11.3. 自动装箱和自动拆箱

JDK1.5后的新特性: 自动拆装箱

- 自动装箱：Java自动将基本数据类型转换成其对应的包装类的过程就是自动装箱。

```java
Integer i = 10; // 自动装箱
```

- 自动拆箱：Java自动将包装类转换为其对应的基本数据类型的过程就是自动拆箱。

```java
int a = i; // 自动拆箱
```

自动装拆箱的好处：基本数据类型的变量可以直接和对应的包装类引用变量进行数学运算。

#### 11.3.1. 注意事项

1. 自动拆箱和自动装箱是由编译器自动完成，根据语法来决定是否需要装箱和拆箱。
2. 如果整型字面量的值在 -128 到 127 之间，那么自动装箱时不会创建新的 `Integer` 对象，而是直接引用常量池中的 `Integer` 对象，若超过范围才会创建新的对象

```java
Integer a = new Integer(3);
Integer b = 3; // 将3自动装箱成Integer类型
int c = 3;

System.out.println(a == b); // false 两个引用没有引用同一对象
System.out.println(a == c); // true a自动拆箱成int类型再和c比较
System.out.println(b == c); // true

Integer a1 = 128;
Integer b1 = 128;
System.out.println(a1 == b1); // false

Integer a2 = 127;
Integer b2 = 127;
System.out.println(a2 == b2); // true
```


# Java 的面向对象

Java 语言提供类、接口和继承等面向对象的特性，为了简单起见，只支持类之间的单继承，但支持接口之间的多继承，并支持类与接口之间的实现机制（关键字为 `implements`）。Java 语言全面支持动态绑定，而 C++语言只对虚函数使用动态绑定。总之，Java语言是一个纯的面向对象程序设计语言。

## 1. 面向对象概念

### 1.1. 面向对象五大基本原则

- 单一职责原则SRP(Single Responsibility Principle)：类的功能要单一
- 开放封闭原则OCP(Open－Close Principle)：一个模块对于拓展是开放的，对于修改是封闭的
- 里式替换原则LSP(the Liskov Substitution Principle LSP)：子类可以替换父类出现在父类能够出现的任何地方
- 依赖倒置原则DIP(the Dependency Inversion Principle DIP)：高层次的模块不应该依赖于低层次的模块，他们都应该依赖于抽象。抽象不应该依赖于具体实现，具体实现应该依赖于抽象
- 接口分离原则ISP(the Interface Segregation Principle ISP)：设计时采用多个与特定客户类有关的接口比采用一个通用的接口要好

## 2. Java 对象和类

### 2.1. 概念

- **类**：是一个模板，它描述一类对象的行为和状态。
- **对象**：是类的一个实例，有状态和行为。

### 2.2. 类的定义

使用 `class` 关键字来定义类。语法如下：

```java
public class 类的名称 {
}
```

#### 2.2.1. 类的内容

一个类可以包含以下类型变量：

- **局部变量**：在方法、构造方法或者语句块中定义的变量被称为局部变量。变量声明和初始化都是在方法中，方法结束后，变量就会自动销毁。
- **成员变量**：成员变量是定义在类中，方法体之外的变量。这种变量在创建对象的时候实例化。成员变量可以被类中方法、构造方法和特定类的语句块访问。
- **类变量**：类变量也声明在类中，方法体之外，但必须声明为 `static` 类型。

一个类可以拥有多个方法，在上面的例子中：`eat()`、`run()`、`sleep()` 都是 Dog 类的方法。

示例：

```java
public class Dog {
    String breed;
    int size;
    static String color;
    int age;
 
    void eat(String food) {
    }
 
    void run() {
    }
 
    void sleep(){
    }
}
```

#### 2.2.2. 构造方法

每个类都有构造方法。如果没有显式地为类定义构造方法，Java 编译器将会为该类提供一个默认无参构造方法。

在创建一个对象的时候，至少要调用一个构造方法。构造方法的名称必须与类同名，一个类可以有多个构造方法。

```java
public class Puppy{
    // 默认无参构造方法
    public Puppy(){
    }
 
    // 这个构造器仅有一个参数：name
    public Puppy(String name){    
    }
}
```

**构造方法的特性**：

- 名字与类名相同
- 没有返回值，也不能使用 void 声明构造函数
- 创建类的对象时自动调用

> Notes: 
>
> 1. 构造方法会在创建对象的时候自动调用
> 2. 在创建的时候，构造方法只会被调用一次。而类的其他方法，可以通过对象来反复调用多次
> 3. 如果在类中手动定义了构造方法后，则**不会再为该类提供默认的无参构造方法**。

### 2.3. 对象

使用一个类，其实就是使用该类的成员(成员变量和成员方法)。而要想使用一个类的成员，就必须首先拥有该类的对象。

#### 2.3.1. 创建对象

对象是根据类创建的。在Java中，使用关键字 new 来创建一个新的对象。创建对象需要以下三步：

- 声明：声明一个对象，包括对象名称和对象类型。
- 实例化：使用关键字 `new` 来创建一个对象。
- 初始化：使用 `new` 创建对象时，会调用构造方法初始化对象。

```java
类名 对象名 = new 类名();
```

#### 2.3.2. 访问实例变量和方法

通过已创建的对象来访问成员变量和成员方法，如下所示：

```java
/* 实例化对象 */
Object referenceVariable = new Constructor();
/* 访问类中的变量 */
referenceVariable.variableName;
/* 访问类中的方法 */
referenceVariable.methodName();
```

> 注：示例的变量与方法均为 `public` 修饰

#### 2.3.3. Java中使用变量，遵循“就近原则”

在使用变量时需要遵循的原则为：**就近原则**。顺序如下：

局部 -> 本类成员 -> 父类成员 -> Object 有就使用，没有就报错。

### 2.4. 成员变量和局部变量的区别

1. 在类中的位置不同
    - 成员变量：类中，方法外
    - 局部变量：方法中或者方法声明上(形式参数)
2. 作用域不同
    - 成员变量：针对整个类有效
    - 局部变量：只在某个范围内有效。(一般指的就是方法块内)
3. 在内存中存储的位置不同
    - 成员变量：存储在堆内存中
    - 局部变量：存储在栈内存中
4. 生命周期不同
    - 成员变量：随着对象的创建而存在，随着对象的消失而消失
    - 局部变量：在方法被调用，或者语句被执行的时候存在；当方法调用完，或者语句结束后，就自动释放
5. 初始化值的问题
    - 成员变量：有默认值(int/byte/short/lnng 默认是 0；char 默认是 `\u0000`；double/float 默认是 0.0；boolean 默认是 false)
    - 局部变量：没有默认值。必须先定义，赋值，才能使用

## 3. Java 方法

Java方法是语句的集合，它们在一起执行一个功能。

- 方法是解决一类问题的步骤的有序组合
- 方法包含于类或对象中
- 方法在程序中被创建，在其他地方被引用

### 3.1. 方法的定义语法

一般情况下，定义一个方法包含以下语法：

```java
修饰符 返回值类型 方法名(参数类型 参数名){
    ...
    方法体
    ...
    return 返回值;
}
```

方法包含一个方法头和一个方法体。下面是一个方法的所有部分：

- 修饰符：修饰符，这是可选的，告诉编译器如何调用该方法。定义了该方法的访问类型。
- 返回值类型 ：方法可能会返回值。`returnValueType` 是方法返回值的数据类型。有些方法执行所需的操作，但没有返回值。在这种情况下，`returnValueType` 是关键字 `void`。
- 方法名：是方法的实际名称。方法名和参数表共同构成方法签名。
- 参数类型：参数像是一个占位符。当方法被调用时，传递值给参数。这个值被称为实参或变量。参数列表是指方法的参数类型、顺序和参数的个数。参数是可选的，方法可以不包含任何参数。
- 方法体：方法体包含具体的语句，定义该方法的功能。

![](images/584475916220855.jpg)

### 3.2. 形参 & 实参

参数在程序语言中分为：

- **实参（实际参数）**：用于传递给函数/方法的参数，必须有确定的值。
- **形参（形式参数）**：用于定义函数/方法，接收实参，不需要有确定的值。

```java
String hello = "Hello!";
// hello 为实参
sayHello(hello);
// str 为形参
void sayHello(String str) {
    System.out.println(str);
}
```

### 3.3. 值传递 & 引用传递

程序设计语言将实参传递给方法（或函数）的方式分为两种：

- **值传递**：指的是在方法调用时，方法接收的是实参值的拷贝，会创建副本，传递后就互不相关了。
- **引用传递**：指的是在方法调用时，方法接收的直接是实参所引用的对象在堆中的地址，不会创建副本，即传递前和传递后都指向同一个引用（也就是同一个内存空间）。对形参的修改将影响到实参。

很多程序设计语言（比如 C++、 Pascal）提供了两种参数传递的方式，<font color=red>**值得注意的是，在 Java 中只有值传递**</font>。

### 3.4. Java 只有通过值传递参数

调用一个方法时必须按照参数列表指定的顺序提供参数。

> Notes: 程序设计语言中有关将参数传递给方法（或函数）的一些专业术语。**按值调用(call by value)表示方法接收的是调用者提供的值，而按引用调用（call by reference)表示方法接收的是调用者提供的变量地址。一个方法可以修改传递引用所对应的变量值，而不能修改传递值调用所对应的变量值。**它用来描述各种程序设计语言（不只是Java)中方法参数传递方式。

Java 程序设计语言总是采用按值调用。即：<font color=red>**方法得到的是所有参数值的一个拷贝，方法不能修改传递给它的任何参数变量的内容**</font>。

案例证明：

```java
public class Person {
    private String name;
   // 省略构造函数、Getter&Setter方法
}

public static void main(String[] args) {
    Person xiaoZhang = new Person("小张");
    Person xiaoLi = new Person("小李");
    swap(xiaoZhang, xiaoLi);
    System.out.println("xiaoZhang:" + xiaoZhang.getName());
    System.out.println("xiaoLi:" + xiaoLi.getName());
}

public static void swap(Person person1, Person person2) {
    Person temp = person1;
    person1 = person2;
    person2 = temp;
    System.out.println("person1:" + person1.getName());
    System.out.println("person2:" + person2.getName());
}
```

输出结果：

```
person1:小李
person2:小张
xiaoZhang:小张
xiaoLi:小李
```

解析：两个引用类型的形参互换并没有影响实参啊！因为 swap 方法的参数 person1 和 person2 只是拷贝的实参 xiaoZhang 和 xiaoLi 的地址。因此，person1 和 person2 的互换只是拷贝的两个地址的互换罢了，并不会影响到实参 xiaoZhang 和 xiaoLi。

![](images/409091916220871.png)

## 4. 组合和继承

**Java中类与类之间常见的关系**

- 组合关系
- 继承关系
- 代理模式关系

### 4.1. 组合

一个类型A中的成员变量的数据类型是类型B时，此时A和B就是组合关系。

即A类中的成员变量的数据类型是B类。例如：Student 类中 String 类型的属性就是组合关系。

### 4.2. 继承

#### 4.2.1. 概念

继承就是子类继承父类的特征和行为，使得子类对象（实例）具有父类的实例域和方法，或子类从父类继承方法，使得子类具有父类相同的行为。

继承是面向对象四大特征(封装、继承、多态、抽象)之一，面向对象重点。是类与类之间关系的一种。从类与类之间设计角度看，子类必须是父类的一种才可能使用继承

> Tips: 继承是实现多态的前提。

#### 4.2.2. 继承的好处

1. 解决代码复用的常用方式
2. 提高了代码的扩展性
3. 为多态提供前提条件

#### 4.2.3. 继承的语法格式 (extends 关键字)

使用 extends 关键字来实现继承，而且所有的类都是继承于 `java.lang.Object`，当一个类没有继承相关的关键字时，则默认继承 Object（这个类在 java.lang 包中，不需要 `import` 导入）祖先类。

```java
public class 子类名 extends 父类名{
}
```

### 4.3. 继承的类型

Java 只<font color=red>**支持单继承，不支持多继承**</font>(即 `extends` 后面不能跟多个父类)。但支持多层继承，例如：

```java
Foo extends Bar;
// 这样 Foo 就是间接继承了父类 SuperBar
Bar extends SuperBar;
```

![](images/441301609220853.png)

### 4.4. 继承的特点

1. <font color=red>子类拥有(除构造方法以外)父类的所有成员(成员变量和成员方法)</font>
2. <font color=red>子类能够直接访问父类非 `private` 修饰的成员</font>
3. 子类可以在父类的基础上添加特有属性
4. 子类可以对父类的方法进行功能扩展(方法重写)
5. 子类可以在父类的基础上添加特有的方法
6. 构造方法不能被继承，但是子类可以<font color=red>通过 `super` 关键字间接调用父类的构造方法</font>。实际中一般成员变量都用 `private` 修饰，父类的成员变量要提供相应的 getXxx/setXxx 方法让子类调用来访问

### 4.5. 继承的注意事项

- 每个类都直接或者间接继承 Object 父类。当一个类没有明显继承其他类时，都隐藏一个 `extends Object` 的父类
- Object 类是所有类的父类（超类）
- 子类并不是父类的一个子集。实际上，一个子类通常比它的父类包含更多的信息和方法。一般情况下，最好能为每个类提供一个无参构造方法，以便于对该类进行扩展，同时避免错误。

## 5. 方法重写(Override)与方法重载(Overload)

### 5.1. 方法重载(Overload)

方法重载(overloading) 是在一个类里面，定义方法名字相同，而参数不同、返回类型可以相同也可以不同的相关方法

每个重载的方法（或者构造函数）都必须有一个独一无二的参数类型列表。*最常用的地方就是构造器的重载*。

#### 5.1.1. 方法重载的规则

- 被重载的方法必须改变参数列表(参数个数、类型、顺序不一样)
- 被重载的方法可以改变返回类型
- 被重载的方法可以改变访问修饰符
- 被重载的方法可以声明新的或更广的检查异常
- 方法能够在同一个类中或者在一个子类中被重载

> Notes: <font color=red>**不能以返回值类型作为重载函数的区分标准**</font>

### 5.2. 方法重写(Override)

重写是子类对父类的允许访问的方法的实现逻辑进行重新编写，**即跟父类的方法声明完全一样，只是方法体核心逻辑不一样！**方法重写有如下要求：

- 方法名相同
- 参数列表相同(类型和顺序要一致)
- 返回值类型相同，或者是原方法返回的子类类型
- 子类重写的方法权限不能低于父类的方法权限
- 重写方法不能抛出新的检查异常或者比被重写方法申明更加宽泛的异常。
    - > 例如：父类的一个方法申明了一个检查异常 `IOException`，但是在重写这个方法的时候不能抛出 `Exception` 异常，因为 `Exception` 是 `IOException` 的父类，抛出 `IOException` 异常或者 `IOException` 的子类异常。

> Notes: 与方法重载不一样地方是：方法名、参数列表、返回值全部一致。（*返回值可以是子类*）

#### 5.2.1. 方法重写的注意事项

- 方法名相同，参数列表相同，返回值类型相同。
- 子类重写父类的方法后，通过子类对象调用的是重写后的方法。
- <font color=red>子类在重写父类方法时，访问权限修饰符要大于等于父类的方法访问权限修饰符</font>。(`private < 默认 < protected < public`)。一般重写方法都修饰符一致即可
- 如果父类的方法用 `private` 修饰，则不能被子类重写，即使子类有声明一样的方法，也不属于重写，属于定义了一个同名的新方法
- 当需要扩展父类方法(父类方法功能不能满足需求时)，就使用方法重写
- 最常用的用法，在重写的方法中用 `super.xxx()`，这样可以保留父类的功能，达到增强功能的效果。

```java
@Override
public void xxx(){
    super.xxx();
    ....
}
```

- 被 `final` 关键字修饰的方法不能被重写
- 构造方法不能被重写
- 如果不能继承一个类，则不能重写该类的方法
- 子类和父类在同一个包中，那么子类可以重写父类中除了声明为 private 和 final 的所有其他方法。
- 子类和父类不在同一个包中，那么子类只能够重写父类的声明为 public 和 protected 的非 final 方法。
- 声明为 `static` 的方法不能被重写，但是能够被再次声明

#### 5.2.2. @Override 注解

`@Override`	注解用来修饰方法，表示该方法是重写父类的方法。如果修饰的方法在父类中没有找到，则编译失败。

```java
class Animal{
    public void move(){
        System.out.println("动物可以移动");
    }
}
 
class Dog extends Animal{
    @Override
    public void move(){
        System.out.println("狗可以跑和走");
    }
}
```



### 5.3. 重写与重载的区别

|  区别点  |  重载方法  |                  重写方法                  |
| -------- | --------- | ----------------------------------------- |
| 参数列表 | 不能相同   | 必须一致                                   |
| 返回类型 | 可以不相同 | 必须一致                                   |
| 异常     | 可以不相同 | 可以减少或删除，一定不能抛出新的或者更广的异常 |
| 访问权限 | 可以不相同 | 一定不能做更严格的限制（可以降低限制）         |

![](images/266762909239279.png)

## 6. 内部类

### 6.1. 概述

- 将一个类定义在另一个类中或另一个类的方法中的类，该类就称为内部类。内部类是一个相对概念。
- 内部类可以直接访问任何外部类的成员，包括 private 修饰的。
- 外部类编译后会出现两个 class 文件。内部类生成的 class 文件的命名：`外部类名$内部类名.class`
- 内部类分为成员内部类与局部内部类。定义时是一个正常定义类的过程，同样包含各种修饰符、继承与实现关系等。
- 在日常的企业级开发中，我们很少会使用到内部类来实现业务逻辑，一般用匿名内部类或成员内部类

### 6.2. 内部类4种类型

- 静态内部类
- 成员内部类
- 局部内部类
- 匿名内部类

### 6.3. 静态内部类（理解）

定义在类内部的静态类，就是静态内部类。

```java
public class Out {
    private static int a;
    private int b;

    public static class Inner {
        public void print() {
            System.out.println(a);
        }
    }
}
```

1. 静态内部类可以访问外部类所有的静态变量和方法，即使是 private 的也一样。
2. 静态内部类和一般类一致，可以定义静态变量、方法，构造方法等。
3. 其它类使用静态内部类需要使用“**外部类.静态内部类**”方式，如下所示：

```java
Out.Inner inner = new Out.Inner();
inner.print();
```

4. **Java集合类HashMap内部就有一个静态内部类Entry**。Entry是HashMap存放元素的抽象，HashMap 内部维护 Entry 数组用了存放元素，但是 Entry 对使用者是透明的。像这种和外部类关系密切的，且不依赖外部类实例的，都可以使用静态内部类。

### 6.4. 成员内部类（理解）

#### 6.4.1. 成员内部类定义

- **定义位置**：定义在成员位置的，与成员变量同级
- **定义格式**：

```java
public class Outer{
    private String a;
    class Inner{
        // 其他代码
        // 成员变量
        // 成员方法
    }
}
```

成员内部类可以访问外部类所有的变量和方法，包括静态和非静态，私有和公有。

- 访问方式1：间接访问；在外部为中提供一个方法，在该方法中创建内部类的对象。
- 访问方式2：直接访问。`外部类.内部类 变量名 = new 外部类().new 内部类();`

```java
Outer.Inner x = new Outer().new Inner();
```

#### 6.4.2. 成员内部类使用场景

- 当一个类只被一个类使用时，就可以该类定义为某一个类的内部类。
- 在描述事物A时，发现事物A中还包含了另一类事物B，而且事物B要使用到事物A的一些成员数据，此时就可以将事物 B定义为事物A的内部类。

#### 6.4.3. 成员内部类使用注意事项

当内部类和外部类出现同名的成员时，默认访问的是内部类的成员，如果要访问外部类的成员，则需要使用以下格式：

```java
外部类.this.成员变量;
外部类.this.成员方法(参数列表);
```

**注：内部类不能定义静态成员变量**

#### 6.4.4. 使用案例

```java
public class TTTT {
    public static void main(String[] args) {
        // 创建汽车对象
        Car c = new Car(true);
        Car.Engine e = c.new Engine();
        // 将创建出来的发动机对象调用work方法
        e.work();
        System.out.println("================");
        // 另一种创建内部类对象的方法
        Car.Engine e1 = new Car(false).new Engine();
        e1.work();
    }
}

// 新建一个发动机类
class Car {
    private boolean status;

    public Car(boolean status) {
        super();
        this.status = status;
    }

    public Car() {
        super();
    }

    // 新建一个发动机内部类
    class Engine {
        // 直接使用car类中的成员变量
        public void work() {
            if (status) {	//	内部类可以直接访问任何外部类的成员，包括 private 修饰的。
                System.out.println("发动机就飞速旋转。");
            } else {
                System.out.println("发动机停止工作。");
            }
        }
    }
}
```

### 6.5. 局部内部类（了解）

- **定义位置**：定义在外部类的某一个成员方法中的类
- **定义格式**：

```java
public class Outer{
    public void method{
        class Inner{
            //其他代码
        }
    }
}
```

定义在实例方法中的局部类可以访问外部类的所有变量和方法；定义在静态方法中的局部类只能访问外部类的静态变量和方法

- **访问格式**：只能在成员方法内部创建该内部类的对象，调用相关方法。
- **使用场景**：如果一个类只在某个方法中使用，则可以考虑使用局部类。
- **注意事项**：
    - 局部内部类不能使用权限修饰符。
    - 局部内部类中如果要访问方法中的局部变量，
        - JDK1.8之前该局部变量需要使用 final 修饰。
        - JDK1.8之后该局部变量可以不用 final 修饰，但也不能修改。

### 6.6. 匿名内部类（重点）

#### 6.6.1. 概述与前提

- 没有明确的类定义语法，直接创建已知的子类对象或接口的实现类对象。
- 匿名内部类我们必须要继承一个父类或者实现一个接口，当然也仅能只继承一个父类或者实现一个接口。同时它也是没有class关键字，这是因为匿名内部类是直接使用new来生成一个对象的引用。
- 匿名内部类是局部内部类的一种。

#### 6.6.2. 匿名内部类格式

- 将定义子类与创建子类对象两个步骤由一个格式一次完成，多使用匿名对象的方式。虽然是两个步骤，但是两个步骤是连在一起的、即时的。
- 匿名内部类如果不定义变量引用，则也是匿名对象。格式如下：

```java
new (父)类或接口(){
    //重写需要重写的方法
};
```

#### 6.6.3. 匿名内部类使用说明

- **过程**：
    - 临时定义一个类型的子类
    - 定义后即刻创建刚刚定义的这个类的对象
- **目的**：
    - 匿名内部类是创建某个类型子类对象的快捷方式。
    - 我们为了临时定义一个类的子类，并创建这个子类的对象而使用匿名内部类。
- **常见问题**：
    - 匿名内部类无法定义构造方法，因为没有类名；
    - 匿名内部类可以定义特有的方法和变量；但是无法去访问。所以一般不会这样添加。
- **特点**：
    1. 匿名内部类编译后也会出现两个 class 文件。匿名内部类生成的 class 文件的命名：`外部类名$1.class`, 如果第2次创建的话，就是`外部类名$2.class`，如此类推。例：`Test2_02$1.class`
    2. 创建出来的匿名内部类对象可以直接使用一次。可以用于直接赋值，也可以将地址值赋给父类/接口类型的对象。这样可以多次使用。匿名内部类主要是省了不用创建子类.java。
    3. 匿名内部类必须继承一个抽象类或者实现一个接口
    4. 匿名内部类不能定义任何静态成员和静态方法
    5. 当所在的方法的形参需要被匿名内部类使用时，必须声明为 final。因为**生命周期不一致**，局部变量直接存储在栈中，当方法执行结束后，非final的局部变量就被销毁。而局部内部类对局部变量的引用依然存在，如果局部内部类要调用局部变量时，就会出错。声明 final 可以确保局部内部类使用的变量与外层的局部变量区分开，解决生命周期不一致的问题。
    6. 匿名内部类不能是抽象的，它必须要实现继承的类或者实现的接口的所有抽象方法

```java
public static void main(String[] args) {
    // 定义匿名内部类，调用参加运动会的方法
    Sport s = new Sport() {
        @Override
        public void run() {}
    };
    enter(s);
    System.out.println("===========================");
    // 匿名内部类，其实就是在一个类中直接创建一个匿名的子类对象，可以直接使用。
    // 如果需要多次使用的话，要用一个父类对象或接口对象接收。
    enter(new Sport() {
        @Override
        public void run() {}
    });
}
// 定义一个方法，以接口作为参数，输入参加是否参加运会
public static void enter(Sport s) {
    System.out.println("参加运动会，奔跑吧。");
}
```

### 6.7. 内部类的优点

- 一个内部类对象可以访问创建它的外部类对象的内容，包括私有数据！
- 内部类不为同一包的其他类所见，具有很好的封装性；
- 内部类有效实现了“多重继承”，优化 java 单继承的缺陷。
- 匿名内部类可以很方便的定义回调。

### 6.8. 内部类的应用场景

1. 一些多算法场合
2. 解决一些非面向对象的语句块
3. 适当使用内部类，使得代码更加灵活和富有扩展性
4. 当某个类除了它的外部类，不再被其他的类使用时

## 7. 抽象类与抽象方法

### 7.1. 抽象方法的概念

使用 `abstract` 修饰，没有方法体的方法就是抽象方法。抽象方法没有定义，方法名后面直接跟一个分号，而不是花括号。
	
**抽象方法定义格式**：

```java
权限修饰符(public) abstract 返回值类型 方法名(参数列表);
```

> Notes: 该方法的具体实现由它的子类确定

### 7.2. 抽象类的概念

被 `abstract` 修饰的类就是抽象类，具有抽象方法的类就是必须是一个抽象类。抽象类除了不能实例化对象之外，类的其它功能依然存在，成员变量、成员方法和构造方法的访问方式和普通类一样。

**抽象类的作用**是：用来描述一种类型应该具备的基本特征和行为(功能/方法)，实现这些功能就由子类通过方法重写来完成。

**抽象类的定义格式**：

```java
权限修饰符(public) abstract class 类名{
	// ...
}
```

> Tips: <font color=red>**子类继承抽象类的话，就必须要重写抽象类中的抽象方法**</font>

### 7.3. 抽象类的特点

抽象类和抽象方法必须使用 `abstract` 修饰。

抽象类不能直接创建对象。只能通过子类继承抽象类，进行创建子类对象，调用重写方法等操作。

> 不能直接创建的原因是，如果可以创建对象后，就是说对象可以调用抽象类中的抽象方法，但抽象方法没有什么作用，这样调用就没有意义。为了避免这个不必要的操作，就规定抽象类不能直接创建对象。

### 7.4. 抽象类的常见问题

- 抽象类不能被实例化(初学者很容易犯的错)，如果被实例化，就会报错，编译无法通过。只有抽象类的非抽象子类可以创建对象
- 抽象类中是可以不定义抽象方法(*但一般不会这样操作，在适配器模式会使用到*)，但是有抽象方法的类必定是抽象类
- 构造方法，类方法（用 `static` 修饰的方法）不能声明为抽象方法
- 子类继承了抽象类时必须重写抽象类中的所有抽象方法，否则该子类也要定义为抽象类。
- 抽象类中可以有构造方法，意义是子类可以能过 `super` 调用父类的构造方法给父类的成员变量赋值
- 一个抽象类不作为父类出现，没有任何意义
- 抽象类中可以定义普通方法(非抽象方法)
- 抽象关键字 `abstract` 不能和 `private` 关键字一起使用。<font color=red>因为一般继承了抽象类的子类，要求要重写全部的抽象方法，如果用 `private` 关键字修饰了，就不能重写该方法，就相互矛盾。</font>
- 什么情况下要定义抽象类和抽象方法？当某种功能(方法)无法确定的时候，子类都需要重写该方法的时候，就将方法定义成抽象方法

## 8. this / super 关键字

### 8.1. this 关键字的作用

Java中使用变量，遵循“就近原则”：局部 -> 本类成员 -> 父类成员 -> Object 有就使用，没有就报错。

`this` 关键字可以用来解决局部变量和成员变量重名的问题。如：

- 代表本类当前对象的引用（谁调用，this 就代表谁）
- 调用本类的其他构造方法
- 访问本类的其他成员（成员变量和成员方法）

使用时注意问题：不能在静态方法中使用 `this` 关键字。

### 8.2. this 与 super 的区别

- `super`：它引用当前对象的直接父类中的成员（用来访问直接父类中被隐藏的父类中成员数据或函数，基类与派生类中有相同成员定义时如：`super.变量名`、`super.成员函数据名`（实参）
- `this`：它代表当前对象名（在程序中易产生二义性之处，应使用 this 来指明当前对象；如果函数的形参与类中的成员数据同名，这时需用 this 来指明成员变量名）
- `super()` 和 `this()` 的用法类似，均需放在构造方法内第一行。区别是，`super()` 在子类中调用父类的构造方法，`this()` 在本类内调用本类的其它构造方法。
- `super()` 和 `this()` 不能同时出现在一个构造函数里面，因为 this 必然会调用其它的构造函数，其它的构造函数必然也会有 super 语句的存在，所以在同一个构造函数里面有相同的语句，就失去了语句的意义，编译器也不会通过。
- `this()` 和 `super()` 都指的是对象，所以，均不可以在 `static` 环境中使用。包括：`static` 变量、`static` 方法、`static` 语句块。

尽管可以用this调用一个构造器，但却不能调用两个。从本质上讲，this 是一个指向本对象的指针，然而 super 是一个 Java 关键字。

### 8.3. 构造方法调用注意事项

1. 当通过子类创建对象时，默认会先调用父类的无参数构造方法。子类的每一个构造方法中，如果方法体没有显示指定父类构造方法，都默认首行隐藏 `super();` 语句来调用父类的无参构造方法。
2. 如果需要调用父类的有参构造方法，就在创建子类对象时的有参构造方法体首行中写上 `super(xxx, xxx, ...);` 语句。当通过手动调用父类构造方法时，就不会默认再调用父类无参数构造方法;
3. 通过 `super` 调用父类的构造方法时，该语句必须是第一行有效语句，必须在构造方法中使用
4. 不能在子类的非构造方法中通过 `super` 调用父类的构造方法

<font color=red>**总结：`this` 用于调用本类的构造方法，`super` 用于调用父类的构造方法**</font>

> Tips: 
>
> - 创建子类对象，**不会创建父类对象，默认调用父类的构造方法只是为了给父类的成员变量赋值**。
> - Java 程序在执行子类的构造方法之前，如果该构造方法中没有显示使用 `super()` 来调用父类特定的构造方法，则会默认调用父类中“无参构造方法”。如果父类中只定义了有参数的构造方法，而在子类的构造方法中又没有用 `super()` 来调用父类中特定的构造方法，则编译时将发生错误，因为 Java 程序在父类中找不到无参的构造方法可供执行。因此建议父类里显式加上无参构造方法，从而避免编译错误。


## 9. 接口


## 10. 多态

### 10.1. 多态的概述

同一种事物表现出来的多种形态。是面向对象的三大特征之一。

所谓多态就是指程序中定义的引用变量所指向的具体类型和通过该引用变量具体调用的方法实现在编程时并不确定，而是在程序运行期间才确定，即一个引用变量到底会指向哪个类的实例对象，该引用变量发出的方法调用到底是哪个类中实现的方法，必须在由程序运行期间才能决定。

在 Java 中有两种形式可以实现多态：**继承**（多个子类对同一方法的重写）和**接口**（实现接口并覆盖接口中同一方法）。

![](images/169910314220853.jpg)

#### 10.1.1. 多态的前提

- 必须要子父类关系(继承)或者类实现接口的关系
- 要有方法重写
- 要有父类引用指向子类对象。如：`Parent p = new Child();`

#### 10.1.2. 多态的优缺点

**多态的好处(优点)**

- 提高了代码的维护性
- 提高了代码的扩展性
- 提高了代码的复用性
- 消除类型之间的耦合关系

**多态的弊端(缺点)**

- 多态情况下，不能访问子类特有的成员变量和成员方法。即用父类和接口去作为对象类型时，创建出来的对象不能调用子类特有的方法，因为子类的方法不存在父类和接口中

### 10.2. 多态的定义格式

基础语法定义：

```java
父类名 对象名 = new 子类类名();
接口名 对象名 = new 接口实现类类名();
```

示例：

```java
public class Person{
	method1;
}

public class Student extends Person{
	@Override
	method1;
}

// 创建学生对象
// Student stu = new Student();
Person stu = new Student();	// 多态：父类引用指向子类对象


// 另外一种方法 (接口可以理解为Student的父类)
public interface Play{
	public void playGame();
}

public class Student implements Play{
	@Override
	public void playGame(){
		....
	}
}

// 创建学生对象
Play p = new Student();
```

### 10.3. 接口和抽象类的对比

从设计层面来说，抽象类是对类的抽象，是一种模板设计；接口是行为的抽象，是一种行为的规范。

#### 10.3.1. 相同点

- 接口和抽象类都不能创建对象
- 接口和抽象类都包含抽象方法，其子类都必须覆写这些抽象方法

#### 10.3.2. 不同点

1. 声明的方式
    - 抽象类使用 `abstract` 关键字声明
    - 接口使用 `interface` 关键字声明
2. 子类继承/实现方式
    - 子类使用 `extends` 关键字来继承抽象类。如果子类不是抽象类的话，它需要提供抽象类中所有声明的方法的实现
    - 子类使用 `implements` 关键字来实现接口。它需要提供接口中所有声明的方法的实现
3. 构造方法
    - 抽象类可以有构造方法
    - 接口不能有构造方法
4. 方法访问修饰符
    - 抽象类中的方法可以是任意访问修饰符，但抽象方法不能定义为 `private`
    - 接口方法默认修饰符是 `public`，并且不允许定义为 `private` 或者 `protected`
5. 多继承
    - 一个类最多只能继承一个抽象类
    - 一个类可以实现多个接口
6. 成员变量
    - 抽象类可以定义成员变量(包括常量)
    - 接口不可以定义成员变量，但是可以定义常量。接口的字段默认都是 `static` 和 `final` 修饰的
7. 普通方法
    - 抽象类可以有普通方法
    - 接口在JDK1.8之前只能有抽象方法，JDK1.8之后可以定义默认方法和静态方法。如果有普通方法，普通方法也是可以由现实类重写。
8. 抽象类与接口之间的继承性
    - 抽象类可以实现多个接口。如果抽象类实现接口，则可以把接口中方法映射到抽象类中作为抽象方法而不必实现，而在抽象类的子类中实现接口中方法
    - 接口不可以继承抽象类(但可以继承多个接口)

#### 10.3.3. 如何选择接口和抽象类

明确该方法是否是某种数据类型的共性内容。

- 如果是共性内容，该方法就应该放到该种类型的父类中。然后再考虑该方法父类知不知道如何实现，如果父类不知道如何实现并且要求子类必须重写的，则将该方法定义为抽象方法，该父类就必须定义为抽象类。即当“我是你的一种时”使用继承(抽象类)，父类是通过不断的抽取共性内容而得出来的。
- 如果不是共性内容，该方法就应该定义到接口中，然后需要该功能的类实现接口重写方法即可。当“我应该像你一样具备某种功能时”使用接口，接口是功能的集合，只描述功能具备的方法，由实现类通过方法重写来完成，这个功能不是抽象类都有的功能的时候。

接口和抽象类各有优缺点，在接口和抽象类的选择上，必须遵守这样一个原则：

- 行为模型应该总是通过接口而不是抽象类定义，所以通常是优先选用接口，尽量少用抽象类。
- 选择抽象类的时候通常是如下情况：需要定义子类的行为，又要为子类提供通用的功能。

# 泛型

## 1. 泛型概念

Java 泛型（generics）是 JDK 1.5 中引入的一个新特性，提供了编译时类型安全检测机制，该机制允许程序员在编译时检测到非法的类型。

<font color=red>**泛型的本质是参数化类型，也就是说所操作的数据类型被指定为一个参数**</font>。泛型只在<font color=red>**编译阶段**</font>有效，并且值得注意的是，<font color=red>**泛型类型必须是引用类型**</font>

比如要写一个排序方法，能够对整型数组、字符串数组甚至其他任何类型的数组进行排序，就可以使用 Java 泛型。

- **泛型的应用场景**：在定义类（方法、接口）的时候不确定类型，在使用类（方法、接口）的时候才确定类型
- 泛型定义的位置
    1. 类
    2. 方法
    3. 接口
    4. 参数

## 2. 泛型变量（标识）

泛型变量可以理解为数据类型的占位符，也可以理解为某种数据类型的变量。

泛型变量的命名规则：只要是合法的标识符即可，一般使用一个大写字母。常用的泛型标识：T、E、K、V

## 3. 泛型类

### 3.1. 泛型类的概念

泛型类的声明和非泛型类的声明类似，除了在类名后面添加了类型参数声明部分。泛型类的类型参数声明部分也包含一个或多个类型参数，参数间用英文逗号（`,`）隔开。一个泛型参数，也被称为一个类型变量，是用于指定一个泛型类型名称的标识符。因为他们接受一个或多个参数，这些类被称为参数化的类或参数化的类型。

泛型类的特点：

- 在类定义上使用了泛型变量的类就是泛型类。
- **当创建该类的对象的时候，传入类型，此时类上的泛型被确定。**

### 3.2. 泛型类定义语法

```java
public class 类名<泛型标识1, 泛型标识2, ...> {
    // ...
}
```

语法说明：

1. 泛型标识：可以定义任意的标识号，用于标识指定的泛型的类型
2. 在 `<>` 中的定义的泛型类型数量不限
3. 在 `<>` 中的定义泛型标识，如；`E, T, ...` 能够做为类型在该类内部被使用

### 3.3. 泛型类使用语法

创建泛型类对象时指定泛型变量的具体数据类型

```java
泛型类<具体的数据类型> 对象名 = new 泛型类<具体的数据类型>();
```

在 java 1.7 以后，后面的`<>`中的具体的数据类型可以省略不写

```java
泛型类<具体的数据类型> 对象名 = new 泛型类<>();
```

### 3.4. 使用示例

- 定义泛型类

```java
package com.moon.basic;

/**
 * 泛型类的定义
 *
 * @param <T> 此处 T 可以随便写为任意标识，常见的如 T、E、K、V 等形式的参数常用于表示泛型
 *            在实例化泛型类时，必须指定T的具体类型
 */
public class Generic<T> {

    // 此成员变量的类型为T，T 的类型在创建实例时指定
    private T key;

    // 泛型构造方法形参的类型也为T，T 的类型在创建实例时指定
    public Generic(T key) {
        this.key = key;
    }

    // 泛型方法的返回值类型为 T，T 的类型在创建实例时指定
    public T getKey() {
        return key;
    }

    // 泛型方法的形参类型也为T，T 的类型在创建实例时指定
    public void setKey(T key) {
        this.key = key;
    }
}
```

- 使用泛型类

```java
// 在使用泛型类的时候去确定泛型类形参的数据类型<数据类型>，Java 1.7以后，右边<>的数据类型可以省略
Generic<String> strGen = new Generic<>("abc");
String str = strGen.getKey();
System.out.println(str);

// 泛型的类型参数只能是类类型（包括自定义类），不能是基本数据类型
Generic<Integer> intGen = new Generic<Integer>(123);
int num = intGen.getKey();
System.out.println(num);

// 泛型类如果没有指定数据类型，那么会按 Object 类型来处理
Generic generic = new Generic("abc");
Object key = generic.getKey();
System.out.println(key);
```

### 3.5. 泛型类的注意事项

- <font color=red>**泛型的类型参数只能是引用类型**</font>，不能是基本数据类型
- 泛型类的泛型变量的具体数据类型是在创建对象时确定。
- 如果在创建泛型类对象时没有指定泛型变量的具体数据时，默认是 Object 类型。**(不推荐)**
- 静态方法中不能使用类定义的泛型变量，如果静态中要使用到泛型变量，则需要将该方法定义成泛型方法。不要使用类上使用的泛型变量。(因为静态方法不需要对象就可以调用。)

## 4. 泛型类派生子类

### 4.1. 语法定义

如果定义的类，继承了一个泛型父类，则出现以下几种情况：

1. 子类也是泛型类，则子类和父类的泛型类型要一致

```java
class 子类<T> extends 父类<T>
```

2. 子类不是泛型类，则父类要明确泛型的数据类型

```java
class 子类 extends 父类<数据类型>
```

> Notes: 子类重写父类的方法，需要和父类类型保持一致

### 4.2. 使用示例

- 泛型父类

```java
public class Parent<E> {

    private E value;

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}
```

- 泛型类派生子类情况一：子类也是泛型类，那么子类的泛型标识要和父类一致。

```java
public class ChildFirst<T> extends Parent<T> {
    @Override
    public T getValue() {
        return super.getValue();
    }
}
```

- 泛型类派生子类情况二：如果子类不是泛型类，那么父类要明确数据类型

```java
public class ChildSecond extends Parent<Integer> {
    @Override
    public Integer getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value);
    }
}
```

- 测试

```java
// 子类是泛型类，则需要与父类类型保持一致
ChildFirst<String> childFirst = new ChildFirst<>();
childFirst.setValue("abc");
String value = childFirst.getValue();
System.out.println(value);
System.out.println("---------------------------------");

// 子类非泛型类，将泛型父类需要指定类型
ChildSecond childSecond = new ChildSecond();
childSecond.setValue(100);
Integer value1 = childSecond.getValue();
System.out.println(value1);
```

## 5. 泛型接口

### 5.1. 泛型接口概念

在接口定义上使用了泛型变量的接口

### 5.2. 泛型接口语法

```java
public interface 接口名 <泛型变量> {
    // 接口方法
}
```

### 5.3. 泛型接口实现

1. 实现类不是泛型类，则泛型接口要明确泛型变量的具体数据类型。

```java
public class BaseDao implements Dao<Student>{
}
```

2. 实现类也是泛型类，则实现类和接口的泛型类型要一致。泛型变量具体数据类型在创建该类的对象时指定。**(推荐)**

```java
public class BaseDao<T> implements Dao<T>{
}
```

> Tips: **指定泛型变量时只写接口`<>`处，不指定泛型变量需要类名和接口名两边都写`<>`**

### 5.4. 使用示例

- 泛型接口

```java
public interface GenericInterface<T> {
    T getKey();
}
```

- 实现泛型接口的类情况一：不是泛型类，需要明确实现泛型接口的数据类型。

```java
public class GenericInterfaceImplFirst implements GenericInterface<String> {
    @Override
    public String getKey() {
        return "Hello GenericInterface implement";
    }
}
```

- 实现泛型接口的实现类情况二：是一个泛型类，那么要保证实现接口的泛型类泛型标识包含泛型接口的泛型标识

```java
public class GenericInterfaceImplSecond<T, E> implements GenericInterface<T> {

    private T key;
    private E value;

    public GenericInterfaceImplSecond(T key, E value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public T getKey() {
        return key;
    }

    public E getValue() {
        return value;
    }
}
```

- 测试

```java
// 实现类非泛型类，需要明确实现泛型接口的数据类型。
GenericInterfaceImplFirst genericImpl1 = new GenericInterfaceImplFirst();
System.out.println(genericImpl1.getKey());

System.out.println("---------------------------------");
// 实现类是泛型类，则要保证实现接口的泛型类泛型标识包含泛型接口的泛型标识
GenericInterfaceImplSecond<String, Integer> genericImpl2 = new GenericInterfaceImplSecond<>("count", 100);
System.out.println(genericImpl2.getKey() + "=" + genericImpl2.getValue());
```

## 6. 泛型方法

### 6.1. 泛型方法的概念

泛型方法，是在调用方法的时候指明泛型的具体类型。<font color=red>**在方法的修改符与返回值之间是否存在 `<>` 标识，是判断泛型方法的唯一依据。**</font>

### 6.2. 泛型方法定义

#### 6.2.1. 语法格式

```java
修饰符 <泛型变量> 返回值类型 方法名(参数列表) {
    // 方法上的泛型定义在返回值的前面
}
```

- 修饰符与返回值中间的泛型标识`<泛型变量>`非常重要，可以理解成以此为标识，声明此方法为泛型方法。
- 只有声明了的方法才是泛型方法，泛型类中的**使用了泛型的成员方法并不是泛型方法**。
- `<泛型变量>` 表明该方法将使用泛型类型，此时才可以在方法中使用泛型类型。
- 与泛型类的定义一样，此处`<泛型变量>`可以定义任意标识，常见的如 T、E、K、V 等形式的参数常用于表示泛型。

#### 6.2.2. 定义示例

```java
public <T> T 方法名(T 参数变量名) {
    // 方法上的泛型定义在返回值的前面
}

public <T> void 方法名(T 参数变量名) {
    // 返回值也可以是void
}

public static <T,E,K> void printType(T t, E e, K k) {
    // 静态的泛型方法，采用多个泛型类型
}
```

### 6.3. 泛型使用格式

**调用方法时，由参数类型确定泛型的类型**

```java
// 例1：API中的ArrayList集合中的方法：
public <T> T[] toArray(T[] a){  }
// 该方法，用来把集合元素存储到指定数据类型的数组中，返回已存储集合元素的数组

// 例2：
ArrayList<String> list = new ArrayList<String>();
String[] arr = new String[100];
String[] result = list.toArray(arr);
// 此时，变量T的值就是String类型。变量T，可以与定义集合的泛型不同
public <String> String[] toArray(String[] a){  }

// 例3：
ArrayList<String> list = new ArrayList<String>();
Integer[] arr = new Integer[100];
Integer[] result = list.toArray(arr);
// 此时，变量T的值就是Integer类型。变量T，可以与定义集合的泛型不同
public <Integer> Integer[] toArray(Integer[] a){  }
```

### 6.4. 泛型方法须知

- 泛型方法上泛型变量的具体数据类型是什么，取决于方法调用时传入的参数。
- 泛型变量具体数据类型**不能是基本数据类型**，如果要使用基本数据类型，要**使用对应的包装类类型**

```java
import java.util.Arrays;

public class Test2_04 {
    public static void main(String[] args) {
        // 定义一个数组
        String[] arr = { "a", "b", "c", "d", "e", "A", "B", "C", "D", "E" };
        // 泛型方法必须放基本数据类型对应的包装类型，否则报错。
        Integer[] arrInt = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12 };
        // 调用方法测试String数组
        swap(arr, 0, 9);
        // 调用方法测试Integer数组
        swap(arrInt, 0, 9);
        // 如果传入错误的索引就输出错误信息
        swap(arr, 0, 19);
    }
    public static <E> void swap(E[] arr, int i, int j) {
        if ((i >= 0 && i < arr.length) && (j >= 0 && j < arr.length)) {
            E temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            System.out.println(Arrays.toString(arr));
        } else {
            System.out.println("你输入的索引错误！");
        }
    }
}
```

### 6.5. 泛型方法与泛型类的成员方法

泛型类中可以定义泛型方法

```java
public class GenericMethod<T> {

    private T key;

    // 此方法非泛型方法，这是一个普通方法，它的数据类型遵从泛型类的类型
    public T getKey() {
        return key;
    }

    // 此方法是泛型方法，不会依赖父类的泛型类型，即使以 <T> 作为泛型变量，也与泛型类无关
    public <E> void show(E[] e) {
        for (int i = 0; i < e.length; i++) {
            System.out.println(e[i]);
        }
    }
}
```

在泛型类中，`public T getKey()` 是采用泛型类类型的普通方法，不是泛型方法，并且不支持 static；`public <E> void show(E[] e)` 是泛型方法，也可以定义为静态的

### 6.6. 泛型方法与可变参数

在泛型方法中，可以使用泛型的可变参数。

```java
public <E> void print(E... e) {
    for (E e1 : e) {
        System.out.println(e);
    }
}
```

## 7. 泛型通配符“?”

### 7.1. 概述

**泛型通配符**：代表可以匹配任意类型，一般用来表示泛型的上下限。具有以下特点：

- 可以直接使用，不用定义。
- 不能使用泛型类、泛型方法和泛型接口定义上。
- 一般不会单独使用，一般会结合泛型上下限使用。

> Notes: 类型通配符一般是使用 `?` 代替具体的类型实参。所以类型通配符 `?` 是类型实参，而不是类型形参。

- **定义方式**：参考 ArrayList 类的构造方法，无法在类中使用
- **使用方式**：调用方法时可以给予任意类型。参照 Arraylist 的构造方法

### 7.2. 泛型的上限

语法：

```java
类/接口<? extends 形参类型>
```

如 `<? extends E>`，代表传递 E 类型或 E 的子类，即该通配符所代表的类型是 E 类型的子类。**还有一种特殊的形式，可以指定其不仅要是指定类型的子类，而且还要实现某些接口**。

例如：`List<? extends A>` 表明这是 A 某个具体子类的 List，保存的对象必须是 A 或 A 的子类。对于 `List<? extends A>` 列表，由于只知道父类但无法得知使用哪个子类，因此不能添加 A 或 A 的子类对象，只能获取 A 的对象。

示例代码：

```java
public class GenericityUpTest {

    // 泛型类基础使用测试
    @Test
    public void test01() {
        List<Animal> animals = new ArrayList<>();
        List<Cat> cats = new ArrayList<>();
        List<MiniCat> miniCats = new ArrayList<>();

        cats.addAll(cats);
        cats.addAll(miniCats);
        // showAnimal(animals); // 报错
        showAnimal(cats);
        showAnimal(miniCats);
    }

    /**
     * 泛型上限通配符，调用该方法时，传递的集合类型只能是Cat或Cat的子类类型。
     */
    public void showAnimal(List<? extends Cat> list) {
        // 这里泛型形参集合不能添加元素。
        // 因为 <? extends Cat> 表示未知的子类，程序无法确定这个类型是什么，所以无法将任何对象添加到集合中
        // list.add(new Animal()); // 报错
        // list.add(new Cat()); // 报错
        // list.add(new MiniCat()); // 报错
        // 因此，这种指定通配符上限的集合，只能从集合中读取元素（取出的元素总是上限类型），不能向集合中添加元素。
        for (int i = 0; i < list.size(); i++) {
            Cat cat = list.get(i);
            System.out.println(cat);
        }
    }
}
```

### 7.3. 泛型的下限

语法：

```java
类/接口<? super 形参类型>
```

如 `<? super E>`，可以传递 E 类型或 E 的父类，即该通配符所代表的类型是 E 类型的父类。

例如：`List<? super A>` 表明这是 A 某个具体父类的 List，保存的对象必须是 A 或 A 的超类。对于 `List<? super A>` 列表，由于编译器已知集合元素是下限类型，因此能够添加 A 或 A 的子类对象；而具体是那种父类型不能确定，所以获取内容时只能通过 Object 类型来接收。

示例代码：

```java
public class GenericityDownTest {

    // 泛型类基础使用测试
    @Test
    public void test01() {
        List<Animal> animals = new ArrayList<>();
        List<Cat> cats = new ArrayList<>();
        List<MiniCat> miniCats = new ArrayList<>();

        showAnimal(animals);
        showAnimal(cats);
        // showAnimal(miniCats); // 报错
    }

    /**
     * 泛型下限通配符，调用该方法时，要求集合只能是Cat或Cat的父类类型
     */
    public void showAnimal(List<? super Cat> list) {
        // 对于指定下限的泛型集合来说，编译器只知道集合元素是下限的父类型，但具体是那种父类型不确定。因此，这种泛型集合能向其中添加元素
        list.add(new Cat("小白", 3));
        list.add(new MiniCat("小黑", 2, 1));
        // 而指定泛型下限的集合元素，循环读取的内容只能通过 Object 类型来接收
        for (Object o : list) {
            System.out.println(o);
        }
    }
}
```

### 7.4. PECS（Producer Extends Consumer Super）原则

- 作为生产者提供数据（往外读取）时，适合用上界通配符（extends）
- 作为消费者消费数据（往里写入）时，适合用下界通配符（super）

> Notes: 在日常编码中，比较常用的是上界通配符（extends），用于限定泛型类型的父类。

## 8. 类型擦除

### 8.1. 概念

**Java 中的泛型基本上都是在编译器这个层次来实现的。在生成的 Java 字节代码中是不包含泛型中的类型信息的**。使用泛型的时候加上的类型参数，会被编译器在编译的时候去掉。这个过程就称为类型擦除。

如在代码中定义的 `List<Object>` 和 `List<String>` 等类型，在编译之后都会变成 List。JVM 看到的只是 List，而由泛型附加的类型信息对 JVM 来说是不可见的。类型擦除的基本过程也比较简单，首先是找到用来替换类型参数的具体类。这个具体类一般是 Object。如果指定了类型参数的上界的话，则使用这个上界。把代码中的类型参数都替换成具体的类。

### 8.2. 无限制类型擦除

![](images/184120417221057.png)

### 8.3. 有限制类型擦除

![](images/372550417239483.png)

### 8.4. 擦除方法中类型定义的参数

![](images/550870417227350.png)

### 8.5. 桥接方法

![](images/192090617247516.png)

## 9. 集合中使用泛型-常用案例

### 9.1. 集合中泛型的使用

在创建集合的同时指定集合要存储的对象的数据类型。`集合类<数据类型> 对象名 = new 集合类<数据类型>();`

### 9.2. 集合使用泛型的好处

- 强制只能存储一种数据类型对象，提高程序的安全性
- 将运行时错误转换为编译时错误。(及早发现错误)
- 省去类型强制转换的麻烦。

### 9.3. 集合使用泛型的注意事项

- 在指定泛型变量时，要么指定左边，要么两边都指定，但两边指定的类型一定要一致。
- 泛型中没有多态的概念，要么两边一致，要么指定左边(JDK1.7后可以不指定右边)
- 强烈推荐两边都指定，并且数据类型要一致。

## 10. 泛型的总结

1. 泛型用来灵活地将数据类型应用到不同的类、方法、接口当中。将数据类型作为参数传递。
2. 泛型是数据类型的一部分，将类名与泛型合并一起看做数据类型

```java
ArrayList<String> array = new ArrayList<String>();
// ArrayList<String> 看作数据类型
```

3. 泛型的定义：定义泛型可以在类中预支地使用未知的类型。
4. 泛型的使用：一般在创建对象时，将未知的类型确定具体的类型。当没有指定泛型时，默认类型为 Object 类型。

```java
ArrayList array = new ArrayList();
array.add("abc");
array.add(1);
// 由于在定义集合时没有指定泛型，add()方法的形参为 Object 类型，所以可以往集合中添加任意任意类型的数据(多态特点)。
```

5. 可以声明带泛型的数组，但不能直接创建带泛型的数组，必须强制转型

```java
ArrayList[] list = new ArrayList[5];
// 可以声明带泛型的数组，但能直接创建其对象
// ArrayList<String>[] listArr = new ArrayList<String>[5]; // 报错
// 但可以创建数组，强制类型
ArrayList<String>[] listArr = new ArrayList[5];
```

6. 可以通过 `java.lang.reflect.Array` 的静态方法 `newInstance(Class<T>，int)` 创建 T 类型的数组，但需要强制转型

```java
public class Fruit<T> {
    private T[] array;

    public Fruit(Class<T> clz, int length){
        // 通过Array.newInstance创建泛型数组
        array = (T[])Array.newInstance(clz, length);
    }

    /**
     * 填充数组
     */
    public void put(int index, T item) {
        array[index] = item;
    }

    /**
     * 获取数组元素
     */
    public T get(int index) {
        return array[index];
    }

    public T[] getArray() {
        return array;
    }
}
```

# 注解

## 1. 注解概念和作用

### 1.1. 注解概念

- 注解是JDK1.5之后的新特性。
- 注解相当于一个标记，也属于类的组成部分，通过注解可以给带携带一些额外信息。
- 注解可以标记在类、接口、方法、成员变量，构造方法，局部变量等等元素上。
- 注解是给编译器或JVM查看的。编译器或JVM可以根据标记执行对应的功能。
- Annatation(注解)是一个接口，程序可以通过反射来获取指定程序中元素的 Annotation 对象，然后通过该 Annotation 对象来获取注解中的元数据信息。

### 1.2. 注解作用

#### 1.2.1. 注解使用例子

- 编译检查，如`@Override`。
- 生成帮忙文档
- **做为框架的配置方案（重点）**
    - XML配置
    - 注解配置

#### 1.2.2. 扩展：框架的两种配置方案优缺点

注：框架 = 代码 + 配置（个性化）。框架（struts2,hibernate,spring)都提供了两种配置方案

- XML 配置：
    - 优点：配置信息和类分离，降低程序的耦合性（扩展性更好）
    - 缺点：每一个类需要对应一个XML文件，如果类很多，而XML文件也会很多。XML 维护成本高（可读性差）
- 注解配置：
    - 优点：将配置信息和类写在一起，可读性高，开发效率相对较高。
    - 缺点：程序耦合性高

## 2. Java常用内置注解的使用
### 2.1. @Override 注解

该注解只能用于修饰方法声明，表示该方法是限定重写父类方法。该注解只能用于方法

### 2.2. @Deprecated 注解

用于表示某个程序中的元素(类，方法等)已经过时。不建议继续使用，还是可以使用。

### 2.3. @SuppressWarnings 注解

`@SuppressWarnings` 注解的作用是抑制编译器警告。常用警告名称：

1. deprecation 忽略过时
2. rawtypes 忽略类型安全
3. unused 忽略不使用
4. unchecked 忽略安全检查
5. null 忽略空指针
6. all 忽略所有编译器警告

**注：如果多个警告就使用`{}`将多个警告包括起来，封装成字符串数组**

## 3. 自定义注解

属性的作用：可以给每个注解加上多个不同的属性，用户使用注解的时候，可以传递参数给属性，让注解的功能更加强大

### 3.1. 自定义注解格式

```java
修饰符 @interface 注解名 {

}
```

### 3.2. 注解的属性
#### 3.2.1. 属性定义格式

- 第1种定义方式：`数据类型 属性名();`
- 第2种定义方式：`数据类型 属性名() default 默认值;`
- **注意事项**：
    - **如果注解有定义了属性，且属性没有默认值，则在使用注解的时候，就需要给属性赋值**
    - **如果属性有默认值，则使用注解的时候，这个属性就可以不赋值。也可以重新赋值，覆盖原有的默认值**

#### 3.2.2. 注解支持的数据类型

- 8种数据类型都支持
- String
- Enum
- Class
- Annotation
- 以及上面类型的数组形式

### 3.3. 特殊属性名 value

- 如果注解中只有一个属性且属性名为 `value` 时，在使用注解时可以直接给出属性值而不需要给属性名。(省略 `value=` 部分)
- 无论这个 value 是单个元素还是数组，都可以省略。
- 如果注解中除了 value 属性还有其他属性，且其他属性中至少有一个属性没有默认值时，则 value 属性名不能省略。

```java
public @interface T_T {
    String value(); // 书名
    double price() default 100; // 价格
    String[] authors(); // 作者
}
```

![注解使用案例](images/20190507110437937_17647.jpg)

## 4. 元注解

### 4.1. 元注解概念

Java 默认提供的注解，用于标识在注解上的注解，用来约束注解的功能，称为元注解。Java 所有的内置注解定义都使用了元注解。元注解有以下4种分类：

- `@Target`
- `@Retention`
- `@Inherited`
- `@Documented`

### 4.2. @Target 元注解

- **`@Target` 作用**：标识注解使用范围【*Annotation可被用于 packages、types（类、接口、枚举、Annotation 类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch 参数）*】，如果不写默认是任何地方都可以使用。元注解可选的值来自于ElemetnType枚举类。(写在自定义注解的类上)
- **格式**：`@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})`
- **元注解的默认值**：
    - `ElementType.TYPE`: 用在类和接口上
    - `ElementType.FIELD`：用在成员变量上
    - `ElementType.METHOD`: 用在成员方法上
    - `ElementType.PARAMETER`：用在方法参数（形式参数）上
    - `ElementType.CONSTRUCTOR`：用在构造方法上
    - `ElementType.LOCAL_VARIABLE`：用在局部变量上

### 4.3. @Retention 元注解

**`@Retention` 作用**：用来标识注解的生命周期（有效作用范围），可选取值来自 `RetentionPolicy` 枚举类：

- `RetentionPolicy.SOURCE`：注解只存在于 Java 源代码中，编译生成字节码文件和程序运行时就不存在了。（即源文件保留）
- `RetentionPolicy.CLASS`：注解存在于 Java 源代码、编译以后的字节码文件中，运行的时候内存就不存在，此注解是默认值。（即 class 保留）
- `RetentionPolicy.RUNTIME`：注解存在于 Java 源代码中、编译以后的字节码文件中、运行时的内存中，程序可以通过反射获取该注解。（即运行时保留）

### 4.4. @Inherited 元注解

**`@Inherited` 作用**：表示该注解可以被子类继承。如果一个使用了 `@Inherited` 修饰的 annotation 类型被用于一个 class，则这个 annotation 将被用于该 class 的子类。

### 4.5. @Documented 元注解

**`@Documented` 作用**：表示该注解会出现在帮忙文档（javadoc）中。描述其它类型的 annotation 应该被作为被标注的程序成员的公共 API，因此可以被例如 javadoc 此类的工具文档化。

## 5. 注解的原理

### 5.1. Annotation 接口

所有注解类型的公共接口，所有注解都是 `java.lang.annotation.Annotation` 的子类(类似所有类都 Object 的子类)

### 5.2. AnnotatedElement 接口

该接口中定义了一系列与注解解析相关的方法。**注：当前对象是指方法调用者**

```java
boolean isAnnotationPresent(Class<T> annotationClass)
```

- 判断当前对象是否使用了指定annotationClass的注解。如果使用了，则返回true，否则返回false

```java
T getAnnotation(Class<T> annotationClass)
```

- 根据注解的Class类型获得当前对象上指定的注解对象（注解类的对象)。需要向下转型成注解的类型，然后才能调用注解里的属性。

```java
Annotation[] getAnnotations()
```

- 获得当前对象及其从父类上继承的所有的注解对象数组

```java
Annotation[] getDeclaredAnnotations()
```

- 获得类中所有声明的注解，不包括父类的

### 5.3. 注解原理简述

注解本质是一个继承了 `Annotation` 的特殊接口，其具体实现类是 Java 运行时生成的动态代理类。通过反射获取注解时，返回的是 Java 运行时生成的动态代理对象。通过代理对象调用自定义注解的方法，最终会调用 `AnnotationInvocationHandler` 的 `invoke` 方法。该方法会从 `memberValues` 这个 Map 中索引出对应的值。而 `memberValues` 的来源是 Java 常量池。

## 6. 注解解析

### 6.1. 解析原则

注解作用在哪个成员上，就通过该成员对应的对象获得注解对象。

> 比如，注解作用在成员方法上，则通过成员方法对应的Method对象获得；作用在类上的，则通过Class对象获得

```java
// 得到方法对象
Method method = clazz.getDeclaredMethod("方法名");
// 得到方法上的注解
注解类 xx = (注解类) method.getAnnotation(注解类名.class);
```

### 6.2. 注解解析案例

```java
package level02.test02;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

/*
 * 关卡2训练案例2
 * 	定义一个注解：Book
        * 包含属性：String value() 书名
        * 包含属性：double price() 价格，默认值为 100
        * 包含属性：String[] authors()  多位作者
    * 1、定义类在成员方法上使用 Book 注解
    * 2、解析获得该成员方法上使用注解的属性值。
*/
public class Day20Test02_02 {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) throws Exception {
        // 使用反射获取Method对象
        Class clazz = Class.forName("level02.test02.Day20Test02_02");
        Method m = clazz.getDeclaredMethod("method");
        // 获取到的注解的对象，需要向下转型成注解的类型
        Book book = (Book) m.getAnnotation(Book.class);

        // 获取注解的值
        System.out.println(book.value());
        // 使用包装类的toString方法转到字符串再输出（直接输出也可以）
        System.out.println(Double.toString(book.price()));
        System.out.println(book.price());
        // 使用Arrays工具类的toString方法将数组输出
        System.out.println(Arrays.toString(book.authors()));
    }

    @Book(value = "傻的吗", authors = { "真的傻", "还是真的是傻的" })
    public static void method() {
        System.out.println("试试调用我呀");
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Book {
    String value();

    double price() default 100;

    String[] authors();
}
```

# 枚举类(Enum)

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

# 序列化与反序列化

## 1. 对象的序列化与反序列化概述

> 引用维基百科对于“序列化”的介绍：
>
> 序列化（serialization）在计算机科学的数据处理中，是指将数据结构或对象状态转换成可取用格式（例如存成文件，存于缓冲，或经由网络中发送），以留待后续在相同或另一台计算机环境中，能恢复原先状态的过程。依照序列化格式重新获取字节的结果时，可以利用它来产生与原始对象相同语义的副本。对于许多对象，像是使用大量引用的复杂对象，这种序列化重建的过程并不容易。面向对象中的对象序列化，并不概括之前原始对象所关系的函数。这种过程也称为对象编组（marshalling）。从一系列字节提取数据结构的反向操作，是反序列化（也称为解编组、deserialization、unmarshalling）。

对象并不只是存在内存中，还需要在传输网络或者持久化到文件，下次再加载出来用，这些场景都需要用到 Java 序列化技术。

- <font color=red>**序列化：将数据结构或对象转换成二进制字节流的过程**</font>。要实现对象的序列化需要使用的流：`ObjectOutputStream` 继承 `OutputStream`
- <font color=red>**反序列化：将在序列化过程中所生成的二进制字节流的过程转换成数据结构或者对象的过程**</font>。要实现对象的反序列化需要使用的流：`ObjectInputStream` 继承 `InputStream`

**Java 序列化技术**正是将对象转变成一串由二进制字节组成的数组，可以通过将二进制数据保存到磁盘或者传输网络，磁盘或者网络接收者可以在对象的属类的模板上来反序列化类的对象，达到对象持久化的目的。

![](images/49242616239297.png)

### 1.1. 序列化协议对应于 TCP/IP 四层模型中的层级

网络通信的双方必须要采用和遵守相同的协议。TCP/IP 四层模型如下：

1. 应用层
2. 传输层
3. 网络层
4. 网络接口层

![](images/146613316227164.png)

如上图所示，OSI 七层协议模型中，表示层做的事情主要就是对应用层的用户数据进行处理转换为二进制流。反过来的话，就是将二进制流转换成应用层的用户数据。因此，OSI 七层协议模型中的应用层、表示层和会话层对应的都是 TCP/IP 四层模型中的应用层，所以**序列化协议属于 TCP/IP 协议应用层的一部分**。

### 1.2. 实际开发中序列化和反序列化的应用场景

1. 对象在进行网络传输（比如远程方法调用 RPC 的时候）之前需要先被序列化，接收到序列化的对象之后需要再进行反序列化
2. 将对象存储到文件中的时候需要进行序列化，将对象从文件中读取出来需要进行反序列化
3. 将对象存储到缓存数据库（如 Redis）时需要用到序列化，将对象从缓存数据库中读取出来需要反序列化

## 2. 序列化接口

### 2.1. Serializable

#### 2.1.1. 概述

```java
package java.io;

public interface Serializable {
}
```

`Serializable`接口，没有任何方法，该接口属于标记性接口，仅用于标识可序列化的语义。接口的作用是，能够保证实现了该接口的类的对象可以直接被序列化到文件中

> Notes: <font color=red>**被保存的对象要求实现 `Serializable` 接口，否则不能直接保存到文件中。否则会出现`java.io.NotSerializableException`。**</font>

#### 2.1.2. serialVersionUID 概述

序列化是将对象的状态信息转换为可存储或传输的形式的过程。虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化 ID 是否一致，这个所谓的序列化 ID，就是在代码中定义的 `serialVersionUID`。

序列化号 serialVersionUID 属于版本控制的作用。序列化的时候 serialVersionUID 也会被写入二级制序列，当反序列化时会检查 serialVersionUID 是否和当前类的 serialVersionUID 一致。如果 serialVersionUID 不一致则会抛出 `InvalidClassException` 异常。强烈推荐每个序列化类都手动指定其 serialVersionUID，如果不手动指定，那么编译器会动态生成默认的序列化号

### 2.2. Externalizable

Java 中还提供了 `Externalizable` 接口，也可以实现它来提供序列化能力。

```java
package java.io;

public interface Externalizable extends java.io.Serializable {
    
    void writeExternal(ObjectOutput out) throws IOException;

    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
}
```

`Externalizable` 继承自 Serializable，该接口中定义了两个抽象方法：`writeExternal()` 与 `readExternal()`。当使用 `Externalizable` 接口来进行序列化与反序列化的时候需要开发人员重写该方法。否则所有变量的值都会变成默认值。

## 3. 对象序列化流 ObjectOutputStream 类

### 3.1. ObjectOutputStream 类作用

对象输出流，将 Java 的对象保存到文件中

### 3.2. 构造方法

```java
public ObjectOutputStream(OutputStream out);
```

根据指定的字节输出`OutputStream`对象来创建`ObjectOutputStream`。如：

```java
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stu.txt"));
```

### 3.3. 相关方法

```java
public final void writeObject(Object obj)
```

将对象Obj写出到流关联的目标文件中

### 3.4. 序列化步骤

1. 定义类，实现Serializable接口，自定义一个Serializable接口序列号

```java
public class Student implements Serializable {}
```

2. 创建对象
3. 创建对象输出流`ObjectOutputStream`
4. 调用`writeObject`将对象写入文件中
5. 关流

## 4. 对象反序列化流ObjectInputStream

### 4.1. ObjectInputStream作用

将文件中的对象读取到程序中，将对象从文件中读取出来，实现对象的反序列化操作。

### 4.2. 构造方法

```java
ObjectInputStream(InputStream in)
```

通过字节输入`InputStream`对象创建`ObjectInputStream`

### 4.3. 普通方法

```java
public final Object readObject()
```

从流关联的的文件中读取对象

### 4.4. 反序列化步骤

1. 创建对象输入流
2. 调用`readObject()`方法读取对象
3. 关流

## 5. 序列化和反序列化的注意事项

### 5.1. InvalidClassException 异常

`java.io.InvalidClassException`: 无效的类异常。此异常是<font color=red>**序列号冲突**</font>。

- 出错的核心问题：**类改变后，类的序列化号也改变，就和文件中的序列化号不一样**
- 解决方法：**修改类的时候,让序列化号不变，自定义一个序列号，不要系统随机生成序列号。**

![](images/20201105141312805_17748.png)

### 5.2. 瞬态关键字 transient

序列化对象时，如果不想保存某一个成员变量的值，该如何处理？

#### 5.2.1. 关键字 transient 的作用

`transient`关键字作用是用于指定**序列化对象时不保存某个成员变量的值**

用 `transient` 修饰成员变量，能够保证该成员变量的值不能被序列化到文件中。当对象被反序列化时，被 `transient` 修饰的变量值会设为初始值，如 int 型的是 0，对象型的是 null。

#### 5.2.2. 使用 static 修饰的成员变量（不建议使用）

可以将该成员变量定义为静态的成员变量。因为对象序列化只会保存对象自己的信息，静态成员变量是属于类的信息，所有不会被保存

#### 5.2.3. 注意点

`transient` 只能修饰变量，不能修饰类和方法

### 5.3. 其它要点

- 序列化对象必须实现序列化接口。
- 序列化对象里面的属性是对象的话也要实现序列化接口。
- 类的对象序列化后，类的序列化ID不能轻易修改，不然反序列化会失败。
- 类的对象序列化后，类的属性有增加或者删除不会影响序列化，只是值会丢失。
- 如果父类序列化了，子类会继承父类的序列化，子类无需添加序列化接口。
- 如果父类没有序列化，子类序列化了，子类中的属性能正常序列化，但父类的属性会丢失，不能序列化。
- 用Java序列化的二进制字节数据只能由Java反序列化，不能被其他语言反序列化。如果要进行前后端或者不同语言之间的交互一般需要将对象转变成Json/Xml通用格式的数据，再恢复原来的对象。
- 如果某个字段不想序列化，在该字段前加上`transient`关键字即可

## 6. 常见序列化协议对比

常见的序列化协议有：JDK 自带的序列化，比较常用第三方的序列化协议：hessian、kyro、protostuff。

其中 JDK 自带的序列化一般很少用，因为序列化效率低并且部分版本有安全漏洞，主要原因有两个：

- 不支持跨语言调用：如果调用的是其他语言开发的服务的时候就不支持了。
- 性能差：相比于其他序列化框架性能更低，主要原因是序列化之后的字节数组体积较大，导致传输成本加大。

## 7. 序列化对象 - 网上案例

要序列化一个对象，这个对象所在类就必须实现Java序列化的接口：`java.io.Serializable`。

### 7.1. 类添加序列化接口

```java
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -8475669200846811112L;

    private String username;
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
```

### 7.2. 序列化/反序列化

可以借助commons-lang3工具包里面的类实现对象的序列化及反序列化，无需自己写

```java
import org.apache.commons.lang3.SerializationUtils;

public class Test {
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("Java");
        user.setAddress("China");
        byte[] bytes = SerializationUtils.serialize(user);

        User u = SerializationUtils.deserialize(bytes);
        System.out.println(u);
    }
}
```

输出结果：

```
User{username='Java', address='China'}
```

上例通过序列化对象字节到内存然后反序列化，当然里面也提供了序列化磁盘然后再反序列化的方法，原理都是一样的，只是目标地不一样。

# 其他综合内容

## 1. JAVA 转义字符

- 退格键：`\b`
- Tab键：`\t`
- 换行符号：`\n`
- 进纸：`\f`
- 回车键：`\r`
- 反斜杠：`\\`
- 单引号：`\'`
- 双引号：`\"`

## 2. 实体类 VO、PO、DO、DTO、BO、QO、DAO、POJO 的概念

- DO（ Data Object）领域对象：与数据库表结构一一对应，通过DAO层向上传输数据源对象。
- PO（persistant object）持久对象：在 o/r 映射的时候出现的概念，如果没有 o/r 映射，没有这个概念存在了。通常对应数据模型 ( 数据库 ), 本身还有部分业务逻辑的处理。可以看成是与数据库中的表相映射的 java 对象。最简单的 PO 就是对应数据库中某个表中的一条记录，多个记录可以用 PO 的集合。 PO 中应该不包含任何对数据库的操作
- DTO（ Data Transfer Object）数据传输对象：Service或Manager向外传输的对象。这个概念来源于J2EE的设计模式，原来的目的是为了EJB的分布式应用提供粗粒度的数据实体，以减少分布式调用的次数，从而提高分布式调用的性能和降低网络负载，但在这里，泛指用于展示层与服务层之间的数据传输对象。
- BO（ Business Object）业务对象：由Service层输出的封装业务逻辑的对象。
- AO（ Application Object）应用对象：在Web层与Service层之间抽象的复用对象模型，极为贴近展示层，复用度不高。
- VO（ View Object）显示层对象：通常是Web向模板渲染引擎层传输的对象。
- POJO（ Plain Ordinary Java Object）：POJO专指只有setter/getter/toString的简单类，包括DO/DTO/BO/VO等。
- Query：数据查询对象，各层接收上层的查询请求。注意超过2个参数的查询封装，禁止使用Map类来传输。

## 3. 递归

### 3.1. 递归概念

递归，指在当前方法内调用自己的这种现象

### 3.2. 递归分类

- **直接递归**：方法A调用方法A。
- **间接递归**：A 方法调用 B 方法，B 方法调用 C 方法，C 方法调用 A 方法。（间接递归实际开发中比较少用。）

### 3.3. 递归注意事项

- 递归一定要有出口；要有结束递归的条件。
- 递归次数不能太多。
- 构造方法中不能使用递归。

递归算法：方法自身调用方法自身，<font color=red>**必须有方法出口(可以结束方法的条件)，递归次数不宜过多，会有 stackoverflow (栈内存溢出错误)。**</font>

### 3.4. 递归扩展知识

**递归是程序控制的另一种形式，实质上就是不用循环控制的重复**

递归会产生相当大的系统开销，它耗费了太多时间并占用了太多的内存。有些本质上有递归特性的问题用其他方法很难解决。如果迭代能解决的方案，就使用迭代。迭代通常比递归效率更高

递归方法是一个直接或间接调用自己的方法。要终止一个递归方法，必须有一个或多个基础情况（程序出口）。

## 4. 分层开发思想

### 4.1. 三层开发结构

什么是分层：开发中，常使用分层思想。不同的层解决不同的问题，层与层之间组成严密的封闭系统，<font color=red>不同层级结构彼此平等。不能出现跨层访问</font>。

- 表示层(View)：直接跟用户打交道，展示数据给用户查看或收集用户输入的信息。
- 业务逻辑层(service)：对数据进行处理，比如筛选数据、判断数据准确性……
- 数据访问层(dao)：直接对数据库进行增删改查操作。

### 4.2. 分层的好处

- 提高代码的复用性：不同层之间进行功能调用时，相同的功能可以重复使用。
- 提高代码的维护性：提高软件的可维护性，对现有的功能进行修改和更新时不会影响原有的功能。
- 提高代码的扩展性：提升软件的可扩展性，添加新的功能的时候不会影响到现有的功能。
- 降低代码的耦合性：降低层与层之间的耦合性。

### 4.3. 如何分层

**不同的层使用不同的包**，例如：

- 表现层：`com.xxx.view`
- 业务逻辑层：`com.xxx.service`
- 数据访问层：`com.xxx.dao`
- 工具包：`com.xxx.utils`。一般用来存放工具类，不属于任何一层，可以被所有层调用
- 测试包：`com.xxx.test`
- 实体包：`com.xxx.entity`、`com.xxx.domain`等。用于存放一些自定义的JavaBean类

### 4.4. 访问顺序

用户 -> 表现层 -> 业务层 -> 数据访问层 -> 数据库

### 4.5. 开发流程

一般从下向上开发：dao 数据访问层 -> service 业务逻辑层 -> view 表示层

## 5. 层污染

### 5.1. 什么是层污染

某一层中使用到了本不应该出现在该层的代码，则称为层污染。如：`Connection` 对象，应该出现在 DAO 层，而不应该出现在业务层。所以想办法把 `Connection` 对象从业务层中移出。

如果调用工具类的方法，不存在层污染，因为工具类属于每个层可以使用的。

### 5.2. 如何解决层污染的问题

以事务处理为例：

1. 数据库工具类添加开启事务，提交事务，回滚事务的方法
2. 提交事务或回滚事务以后，关闭连接，并且从当前线程中删除 `Connection` 对象。
3. 业务层调用工具类中的方法操作事务，如果没有异常则提交事务，出现异常则事务回滚。并且抛出运行时异常给表示层。

## 6. GUI 图形用户界面 (了解)

Graphical User Interface，简称 GUI

```java
JOptionPane.showMessageDialog(null, x);
```
x就是要显示的文本字符串，如："Welcome to Java."	用于弹出消息对话框，需要导包

```java
JOptionPane.showMessageDialog(null, x, y, JOptionPane.INFORMATION_MESSAGE);
```

X是显示的文本字符串，y是表示消息对话框标题的字符串。第四个参数可以是 `JOptionPane.INFORMATION_MESSAGE` (它是为了让消息框能够显示图标(`!`))

如果是 `JOptionPane.QUESTION_MESSAGE` 让消息框能够显示图标(`?`)

从输入对话框获取输入

```java
JOptionPane.showInputDialog("xxx")
```
