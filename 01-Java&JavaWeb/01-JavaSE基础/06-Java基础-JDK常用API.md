# JDK常用API

## 1. String 类
### 1.1. String 类的简述

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
}
```

String 字符串类，由多个字符组成的一串数据，**字符串其本质是一个字符数组**

1. "abc"是String类的一个实例,或者成为String类的一个对象
2. 字符串字面值"abc"也可以看成是一个字符串对象(相当于`char data[] = {'a', 'b', 'c'};`)
3. 字符串是常量，一旦被赋值，就不能被改变
4. 字符串本质是一个字符数组
5. String类是final关键字修改，此类不能被继承

### 1.2. String类的构造方法

```java
public String(String original)
```

- 把字符串数据封装成字符串对象，(*或者是简写成 `String s = "xxx";`，只有String类型才能直接赋值创建对象*)

```java
String(char[] value)
```

- 把字符数组的数据封装成字符串对象

```java
String(char[] value, int index, int count)
```

- 把字符数组中的一部分数据封装成字符串对象


### 1.3. format方法专题（java字符串格式化）

[JAVA字符串格式化-String.format()的使用](https://blog.csdn.net/lonely_fireworks/article/details/7962171/)

String类的format()方法用于创建格式化的字符串以及连接多个字符串对象，显示不同转换符实现不同数据类型到字符串的转换

```java
format(String format, Object... args)
```

- 新字符串使用本地语言环境，制定字符串格式和参数生成格式化的新字符串。

```java
format(Locale locale, String format, Object... args)
```

- 使用指定的语言环境，制定字符串格式和参数生成格式化的字符串。

## 2. Date 类 (在util包中)

通过该类可以获得当前的日期和时间。

### 2.1. Date 构造方法

```java
public Date()
```

- 获得当前的系统时间，直接输出对象的结果是系统默认的显示格式。

```java
public Date(long date)
```

- 根据指定的毫秒值创建日期对象，返回指定毫秒值的日期对象，(**从时间零点到指定时间为止**)

### 2.2. Date常用成员方法

```java
public long getTime();
```

- 获得当前时间的毫秒值 (**从时间零点到当前时间之间共多少毫秒**)
    - 毫秒的概念： 1秒 = 1000毫秒
    - 时间零点：`1970年1月1日  00:00:00`

```java
public void setTime(long time);
```

- 将时间设置到指定的毫秒值上

## 3. DateFormat 类 / SimpleDateFormat 子类
### 3.1. DateFormat概念

- DateFormat:日期格式化类；
    - 是一个抽象类，不能直接创建对象。
    - 可以**使用其子类SimpleDateFormat来创建对象**。重写了DateFormat所有抽象方法。
- SimpleDateFormat:实际使用的日期格式化子类；
    - **子类SimpleDateFormat** 用来格式化日期类对象，可以将日期对象格式化成字符串。

- 创建DateFormat的对象（多态），示例：
    - 无参：`SimpleDateFormat sdf = new SimpleDateFormat();`
    - 有参：`SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");`

### 3.2. SimpleDateFormat构造方法

```java
public SimpleDateFormat()
```

- 创建使用默认日期格式的对象(创建对象输出的时间是系统默认格式)

```java
public SimpleDateFormat(String pattern)
```

- 根据指定的时间模式创建日期格式对象（**传入的格式是有规范的**）
    - 常用时间模式，如：`yyyy-MM-dd  HH:mm:ss    年-月-日  时:分:秒`
- 格式规范列表

| **字母** |   **日期或时间元素**    |      **表示**       |                **示例**                 |
| -------- | ---------------------- | ------------------- | --------------------------------------- |
| G        | Era 标志符              | Text                | AD                                      |
| y        | 年                      | Year                | 1996; 96                                |
| M        | 年中的月份               | Month               | July; Jul; 07                           |
| w        | 年中的周数               | Number              | 27                                      |
| W        | 月份中的周数             | Number              | 2                                       |
| D        | 年中的天数               | Number              | 189                                     |
| d        | 月份中的天数             | Number              | 10                                      |
| F        | 月份中的星期             | Number              | 2                                       |
| E        | 星期中的天数             | Text                | Tuesday; Tue                            |
| a        | Am/pm 标记              | Text                | PM                                      |
| H        | 一天中的小时数（0-23）   | Number              | 0                                       |
| k        | 一天中的小时数（1-24）   | Number              | 24                                      |
| K        | am/pm 中的小时数（0-11） | Number              | 0                                       |
| h        | am/pm 中的小时数（1-12） | Number              | 12                                      |
| m        | 小时中的分钟数           | Number              | 30                                      |
| s        | 分钟中的秒数             | Number              | 55                                      |
| S        | 毫秒数                  | Number              | 978                                     |
| z        | 时区                    | General time   zone | Pacific Standard   Time; PST; GMT-08:00 |
| Z        | 时区                    | RFC 822   time zone | -0800                                   |

### 3.3. SimpleDateFormat常用方法和使用步骤
#### 3.3.1. 日期对象格式化成字符串

```java
public final String format(Date date)
```

- 将Date日期对象格式化为指定格式的日期/时间字符串**(这个方法是重写父类DateFormal的方法)**，输出的结果示例：`2017-10-12 12:12:12`
- 使用步骤：
    1. 创建SimpleDateFormat对象并指定日期模式
    2. 调用format()方法传入日期对象获得字符串

> 日期对象格式化成字符串案例

```java
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test09 {
	public static void main(String[] args) {
		// 获取时间格式化对象
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

		// 将时间格式化对象转成字符串输出
		String time = sdf.format(d);
		// 将格式化后的时间字符串输出
		System.out.println(time);
	}
}
```

#### 3.3.2. 将时间字符串转换成日期对象

```java
public Date parse(String source) throws ParseException
```

- 将日期时间字符串转换成日期对象**(重写父类DateFormal的方法)**，示例：`“2017-10-12” --> Date --> 运算`
- 使用步骤：
    1. 创建SimpleDateFormat对象并指定日期模式
    2. 调用parse()方法传入时间字符串获得日期对象
- **注意事项：时间字符串的格式和创建格式化时指定的日期模式要一致，否则会转换失败抛出异常**

> 时间字符串转换成日期对象示例

```java
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test09 {
	public static void main(String[] args) throws ParseException {
		// 时间字符串
		String timeStr = "2017-10-22  17:07:56";
		// 时间字符串的格式和创建格式化时指定的日期模式要一致，否则会转换失败抛出异常。
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Date d = sdf.parse(timeStr);
		// 直接输出d，输出是系统默认的格式 ： Sun Oct 22 17:07:56 CST 2017
		System.out.println(d);
		// 如果再次格式化成字符串再输出，可以输出原来的格式： 2017-10-22 17:07:56
		System.out.println(sdf.format(sdf.parse(timeStr)));
		// 如果想输出其他的格式，只能新创建一个SimpleDateFormat对象，定义其他格式
		// 这样输出：2017/10/22 17:07:56
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
		System.out.println(sdf2.format(sdf.parse(timeStr)));
		// 注意：上面format()方法里的用sdf调用parse()方法，因为要与时间字符串的格式一致！
	}
}
```


```java
public void applyPattern(String pattern)
```

- 将给定模式字符串应用于此日期格式。（**此方法用于SimpleDateFormal创建对象的时候没有指定格式，使用这方法可以给对象指定日期格式。**）

### 3.4. SimpleDateFormat存在问题与解决方案
#### 3.4.1. 存在线程安全问题

- SimpleDateFormat并不是一个线程安全的类。在多线程情况下，会出现异常
- 一般使用SimpleDateFormat的时候会把它定义为一个静态变量，避免频繁创建它的对象实例。因为把SimpleDateFormat定义为静态变量，那么多线程下SimpleDateFormat的实例就会被多个线程共享，B线程会读取到A线程的时间，就会出现时间差异和其它各种问题。SimpleDateFormat和它继承的DateFormat类也不是线程安全的。

- 通常使用 SimpleDateFormat，下面是一个常见的日期工具类。

```java
package com.moon.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateUtils() {
    }

    public static Date parse(String target) {
        try {
            return SIMPLE_DATE_FORMAT.parse(target);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(Date target) {
        return SIMPLE_DATE_FORMAT.format(target);
    }
}
```

- 使用单线程运行程序，输出的结果正确

```java
package com.moon.test;

public class TestSimpleDateFormat {

    public static void main(String[] args) {
        testSimpleDateFormatInSingleThread();    // 输出的结果：Mon Jul 29 00:00:00 CST 2019
    }

    private static void testSimpleDateFormatInSingleThread() {
        final String source = "2019-01-11";
        System.out.println(DateUtils.parse(source));
    }
}
```

- 使用多线程运行程序，输出结果就出现问题

```java
package com.moon.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestSimpleDateFormat {

    public static void main(String[] args) {
        testSimpleDateFormatWithThreads();
    }

    private static void testSimpleDateFormatWithThreads() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final String source = "2019-07-29";
        System.out.println(":: parsing date string ::");
        IntStream.rangeClosed(0, 20)
                .forEach((i) -> executorService.submit(() -> System.out.println(DateUtils.parse(source))));
        executorService.shutdown();
    }
}
```

输出结果

```console
:: parsing date string ::
Thu Jul 29 00:00:00 CST 41920
Mon Jul 29 00:00:00 CST 2019
Sun Jul 29 00:00:00 CST 7201
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
```

> 出现这种情况就是因为没有考虑到线程安全，以下是 Java 文档有关 SimpleDateFormat 的描述：
>
> “日期格式是非同步的。
> 建议为每个线程创建单独的日期格式化实例。
> 如果多个线程并发访问某个格式化实例，则必须保证外部调用同步性。”
>
> **提示**：使用实例变量时，应该每次检查这个类是不是线程安全。

#### 3.4.2. 解决方案1：ThreadLocal

- 可以使用 ThreadLocal 解决。Threadlocal 的 get() 方法会给当前线程提供正确的值。

```java
package com.moon.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtilsThreadLocal {
    public static final ThreadLocal SIMPLE_DATE_FORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    private DateUtilsThreadLocal() {
    }

    public static Date parse(String target) {
        try {
            return ((DateFormat) SIMPLE_DATE_FORMAT.get()).parse(target);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(Date target) {
        return ((DateFormat) SIMPLE_DATE_FORMAT.get()).format(target);
    }
}
```

- 使用多线程测试

```java
package com.moon.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestSimpleDateFormat {

    public static void main(String[] args) {
        testSimpleDateFormatThreadLocalWithThreads();
    }

    private static void testSimpleDateFormatThreadLocalWithThreads() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final String source = "2019-07-29";
        System.out.println(":: parsing date string ::");
        IntStream.rangeClosed(0, 10)
                .forEach((i) -> executorService.submit(() -> System.out.println(DateUtilsThreadLocal.parse(source))));
        executorService.shutdown();
    }
}
```

- 输出结果正确

```console
:: parsing date string ::
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
Mon Jul 29 00:00:00 CST 2019
```

#### 3.4.3. 解决方案2：Java 8 线程安全的时间日期 API

- Java8 引入了新的日期时间 API，SimpleDateFormat 有了更好的替代者。如果继续坚持使用 SimpleDateFormat 可以配合 ThreadLocal 一起使用。也可以使用新的 API
- Java 8 提供了几个线程安全的日期类，包括 DateTimeFormatter、OffsetDateTime、ZonedDateTime、LocalDateTime、LocalDate 和 LocalTime。Java 文档中这么描述：
    - > 这个类是具有不可变和线程安全的特点。

使用新的API，DateTimeFormatter类与LocalDate类改造示例

```java
package com.moon.test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtilsJava8 {
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtilsJava8() {
    }

    public static LocalDate parse(String target) {
        return LocalDate.parse(target, DATE_TIME_FORMATTER);
    }

    public static String format(LocalDate target) {
        return target.format(DATE_TIME_FORMATTER);
    }
}
```

使用多线程测试

```java
package com.moon.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestSimpleDateFormat {

    public static void main(String[] args) {
        testDateTimeFormatterWithThreads();
    }

    private static void testDateTimeFormatterWithThreads() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final String source = "2019-07-29";
        System.out.println(":: parsing date string ::");
        IntStream.rangeClosed(0, 10)
                .forEach((i) -> executorService.submit(() -> System.out.println(DateUtilsJava8.parse(source))));
        executorService.shutdown();
    }
}
```

程序输出结果

```console
:: parsing date string ::
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
2019-07-29
```

## 4. Calendar 类
### 4.1. Calendar 概述

Calendar是一个日历类，抽象类；不能直接创建对象

### 4.2. Calendar 日历类对象创建方法

```java
public static Calendar getInstance()
```

- 通过Calendar类的静态方法获取日历类对象
- Calendar一般不通过子类new对象，而通过Calendar类的静态方法获得该类对象。此方法相当于`new GregorianCalendar();`
- 创建 Calendar 对象，不使用构造方法，使用以下方法，支持语言敏感的问题，静态方法 getInstance，获取当前时间
- **注：返回值是Calendar对象，因为Calendar是抽象类，不能new对象，返回值的应该是它的子类对象，只是用父类Calendar来接收，这里使用多态的特性**
- *一般一些抽象类都有提供`getInstance()`方法，返回自己类型的对象*

### 4.3. Calendar 常用成员方法

```java
public final Date getTime()
```

- 获得日期对象，返回一个表示此 Calendar 时间值（从历元至现在的毫秒偏移量）

```java
public long getTimeInMillis()
```

- 返回此 Calendar 的时间值，以毫秒为单位。（时间零点至当前时间）

```java
public int get(int field);
```

- 根据指定的日历字段值获得对应的值。
- **注意事项**：
    - **月份是0至11，获得的月份需要+1才是得到我们实际的月份**。
    - **DAY_OF_WEEK是从星期日开始算起，如果是星期日的话，输出是“0”**；

```java
public void set(int field, int value);
```

- 设置指定日历字段的值。

```java
public final void set(int year, int month, int date);
```

- 设置日历字段 YEAR、MONTH 和 DAY_OF_MONTH 的值
- field是字段值，Calendar类中很多字段值用是static修饰，可以直接用`类名.`调用。取值：`Calendar.YEAR; Calendar.MONTH; Calendar.DAY_OF_MONTH(DATE);`
    - 注：get方法相当获得目前的时间，通过set方法可以相当将日历设置到我们需要的日期，再通过get的方法获取值。
    - 如果设置的字段值超出正常的范围，系统会自动将日期向前偏移。

```java
public abstract void add(int field, int amount);
```

- 将指定日历字段的值在当前的基础上偏移指定的值（整数向后偏移，负数向前偏移）
- 根据日历的规则，为给定的日历字段添加或减去指定的时间量。例如，要从当前日历时间减去5天，可以通过调用以下方法做到这一点：`add(Calendar.DAY_OF_MONTH, -5)`。

### 4.4. Calendar 使用示例

```java
import java.util.Calendar;
import java.util.Date;

public class Test09 {
	public static void main(String[] args) {
		// 创建日历类对象,获取当前时间
		Calendar c = Calendar.getInstance();
		// 直接打印效果：
		System.out.println(c);
		System.out.println("===================");
		// Calendar转成 Date对象
		Date time = c.getTime();
		// 打印效果：Sun Oct 22 17:58:30 CST 2017
		System.out.println(time);

		// 分别获取年、月、日的属性值
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		// 打印效果：2017年10月22日
		System.out.println(year + "年" + (month + 1) + "月" + day + "日");

		// 设置年月日,打印效果:Sun Jan 22 18:08:11 CST 2017
		c.set(2017, 0, 22);
		System.out.println(c.getTime());

		// 指定日历字段的值在当前的基础上偏移,打印效果：Fri Jan 22 18:10:20 CST 2016
		c.add(Calendar.YEAR, -1);
		System.out.println(c.getTime());
	}
}
```

## 5. Math 工具类
### 5.1. 常用方法

```java
public static int sqrt(double d);
```

- 返回 d 的算术平方根值（±√￣）

```java
public static double pow(double a, double b);
```

- 返回 a 的 b 次幂的值,返回 double 形式

```java
public static double floor(double d);
```

- 返回小于等于 d 的最大整数，返回该整数的小数形式

```java
public static double ceil(double d);
```

- 返回大于等于 d 的最小整数，返回该整数的小数形式

```java
public static int abs(int d);
```

- 返回 d 的绝对值

```java
public static double random()
```

- 返回带正号的 double 值，返回0至1的随机小数，包括0不包括1；
- 本质也是通过Random类获取随机数。

```java
public static long round(double d);
```

- 对 d 进行四舍五入，返回四舍五入后的值

*注：Math 是一个数字工具类，该类提供了大量与数学运算相关的方法。工具类都是static方法，可以直接用类点调用。如果遇到算术有关的内容，可以到API查找相关的方法。如：求最大，最小值，求三角余弦等*

## 6. System 工具类
### 6.1. 常用方法

```java
public static long currentTimeMillis();
```

- 获得系统当前时间毫秒值。(跟之前Date类getTime()方法和Calendar类 getTimeInMillis()方法获得的结果是一样的)
- 一般用来测试代码的执行时间。

```java
public static void exit(int status);
```

- 退出JVM，终止程序运行。参数用作状态码；根据惯例，非 0 的状态码表示异常终止。（**0 表示正常退出。-1 表示异常退出**）

```java
public static void gc();
```

- 运行垃圾回收器。通知垃圾回收器进行垃圾回收，垃圾回收器可能会回收，也可以不会回收。
- 当对象被垃圾回收器回收时，系统会自动调用方法protected void finalize() throws Throwable。

```java
public static Properties getProperties();
```

- 获得操作系统的相关的属性值，比如操作系统的名字。

```java
public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);
```

- 数组复制方法。参数列表解释如下：
    - `src`：源数组
    - `srcPos`：源数组的起始位置
    - `dest`：目标数组
    - `destPos`：目标数组的起始位置
    - `length`：复制元素的个数

### 6.2. System 类使用示例

```java
public class Test10 {
	public static void main(String[] args) {
		// 数组复制
		// 源数组
		int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
		// 调用方法打印源数组
		printArray(arr);
		System.out.println("===============");
		// 复制的数组
		int[] arrCopy = new int[arr.length];
		int[] arrCopy1 = new int[arr.length];
		int[] arrCopy2 = new int[3];

		// 使用System工具类的方法复制数组（全部复制）
		System.arraycopy(arr, 0, arrCopy, 0, arr.length);
		// 调用方法打印数组
		printArray(arrCopy);
		// 使用System工具类的方法复制数组（复制部分,从第2个元素复制3个，从新数组索引1开始放）;
		// 输出结果是[0 3 4 5 0 0 0 0 0 0]
		System.arraycopy(arr, 2, arrCopy1, 1, 3);
		printArray(arrCopy1);
		// 使用System工具类的方法复制数组（复制3个元素,从索引6开始复制3个，放到新数组中）
		// 输出结果是[7 8 9]
		System.arraycopy(arr, 6, arrCopy2, 0, 3);
		printArray(arrCopy2);
	}

	public static void printArray(int[] arr) {
		// 增强式for打印数组
		System.out.print("数组：[");
		for (int i : arr) {
			System.out.print(i + " ");
		}
		System.out.println("]");
	}
}
```

## 7. Date 类（SQL包的，是util包下Date的子类）

```java
public class Date extends Date
```

- 位置：java.sql 包
- 一个包装了毫秒值的瘦包装器，JDBC 将毫秒值标识为 SQL DATE 值

### 7.1. 构造方法

```java
public Date(long date);
```

使用给定毫秒时间值构造一个 Date 对象。直接输出对象是一个包装过的对象，格式：yyyy-MM-dd

### 7.2. 常用方法

```java
public static Date valueOf(String s);
```

- sql包下的Date类的静态方法，将 JDBC 日期“yyyy-MM-dd”格式转义形式的字符串转换成 Date 值。
- eg：`Date date = Date.valueOf("2017-10-10");`
- 获取一个Date对象，注意，但这个**Date类继承了java.util包下的Date类，所以也可以使用util包的Date父类接收**

```java
public String toString();
```

- 将日期对象格式化成 yyyy-mm-dd 的日期字符串

## 8. Time 类

```java
public class Time extends Date
```

- 位置：java.sql 包
- 继承java.util包下的Date类，瘦包装器，

### 8.1. 构造方法

```java
public Time(long time)
```

- 使用毫秒时间值构造 Time 对象。输出是经过包装的时间格式。输出对象是`HH:mm:ss`

### 8.2. 常用方法

```java
public String toString()
```

- 使用 JDBC 时间转义格式对时间进行格式化。

```java
public static Time valueOf(String s)
```

- 将使用 JDBC 时间转义格式的字符串转换为 Time 值。s使用 "`hh:mm:ss`" 格式的时间

### 8.3. sql包 Date和Time 示例

```java
import java.sql.Time;
import java.util.Date;

public class MoonZero {
	public static void main(String[] args) {
		Date d = new Date(System.currentTimeMillis());
		// 输出：Thu Nov 23 19:11:03 CST 2017 （util.Date）
		System.out.println(d);

		java.sql.Date d2 = new java.sql.Date(System.currentTimeMillis());
		// 输出：2017-11-23(sql.Date)
		System.out.println(d2);

		java.sql.Date d3 = java.sql.Date.valueOf("2011-11-11");
		// 输出：2011-11-11(sql.Date)
		System.out.println(d3);

		Time t = new Time(System.currentTimeMillis());
		// 输出：19:16:51（sql.Time）
		System.out.println(t);
		Time t2 = Time.valueOf("19:30:30");
		// 输出：19:30:30
		System.out.println(t2);
	}
}
```

## 9. Timestamp 时间戳

```java
public class Timestamp extends Date
```

- 位置：java.sql 包
- 一个与 java.util.Date 类有关的瘦包装器，可以生成时间戳

### 9.1. 构造方法

```java
public Timestamp(long time);
```

- 使用毫秒时间值构造 Timestamp 对象。

### 9.2. 常用方法

```java
public static Timestamp valueOf(String s);
```

- 将使用 JDBC 时间戳转义格式的 String 对象转换为 Timestamp 值。

```java
public String toString();
```

- 使用 JDBC 时间戳转义格式编排时间戳。`yyyy-mm-dd hh:mm:ss.fffffffff`，其中 `ffffffffff` 指示毫微秒。

## 10. DecimalFormat 数字格式化类

```java
public class DecimalFormat extends NumberFormat
```

- 位置：java.text 包
- DecimalFormat 是 NumberFormat 的一个具体子类，用于格式化十进制数字。

### 10.1. 构造方法

```java
public DecimalFormat(String pattern);
```

- 使用给定的模式和默认语言环境的符号创建一个 DecimalFormat。
- 参数：pattern - 一个非本地化的模式字符串。
- 传入一个字符串的格式。eg: `new DecimalFormat("￥#,###.00")`

### 10.2. 常用方法

```java
public final StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos)
```

- 格式化一个数，并将所得文本追加到给定的字符串缓冲区。该数可以是 Number 的任意子类。
- 示例如下

```java
// 输入结果是￥1,234.01
System.out.println(new DecimalFormat("￥#,###.00").format(1234.011));
```

## 11. UUID 类

java.util.UUID 所有已实现的接口：Serializable, `Comparable<UUID>`

### 11.1. 常用方法

```java
static UUID randomUUID()
```

- UUID类静态方法，获取类型 4（伪随机生成的）UUID 的静态工厂。返回是UUID对象

```java
public String toString()
```

- 返回表示此 UUID 的 String 对象

### 11.2. UUID的生成

`UUID.randomUUID().toString()` 是javaJDK提供的一个自动生成主键的方法。UUID(Universally Unique Identifier)全局唯一标识符,是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的，是由一个十六位的数字组成,表现出来的形式。由以下几部分的组合：当前日期和时间(UUID的第一个部分与时间有关，如果你在生成一个UUID之后，过几秒又生成一个UUID，则第一个部分不同，其余相同)，时钟序列，全局唯一的IEEE机器识别号（如果有网卡，从网卡获得，没有网卡以其他方式获得），UUID的唯一缺陷在于生成的结果串会比较长

## 12. Scanner 类
### 12.1. 常用方法

```java
public String nextLine()
```

- 此扫描器执行当前行，并返回跳过的输入信息。 此方法返回当前行的其余部分，不包括结尾处的行分隔符。当前位置移至下一行的行首

```java
public String next(String pattern)
```

- 如果下一个标记与从指定字符串构造的模式匹配，则返回下一个标记。如果匹配操作成功，则扫描器执行与该模式匹配的输入

```java
public boolean hasNextInt()
```

- 如果通过使用 `nextInt()` 方法，此扫描器输入信息中的下一个标记可以解释为默认基数中的一个 int 值，则返回 true

```java
public boolean hasNextInt(int radix)
```

- 如果通过使用 `nextInt()` 方法，此扫描器输入信息中的下一个标记可以解释为指定基数中的一个 int 值，则返回 true

### 12.2. Scanner类的next()与nextLine()区别

- `next();`
    - 从左往右开始扫描内容，在没有扫描到有效内容时遇到的空格，tab键,换行符都会被自己过滤掉。
    - 一旦读取到空格，tab键，换行符都会结束扫描。
- `nextLine();`
    - 从左到右开始扫描，不会过滤任何字符，直到扫描到换行符就结束一行扫描。

示例：

```java
int age = sc.nextInt();
String str = sc.nextLine();
如果第1句输入数字后再1个空格再回车，则下一句无法再接收接入，因为第1句在遇到空格时就结束，第2句nextLine()会读取到上一句的空格和换行符，就结束了扫描。因为无法再输入第2句
```

