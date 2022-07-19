# Lombok 工具插件

## 1. 简介

Lombok 是一款 Java 开发插件，使得 Java 开发者可以通过其定义的一些注解来消除业务工程中冗长和繁琐的代码，尤其对于简单的 Java 模型对象（POJO）。在开发环境中使用 Lombok 插件后，Java 开发人员可以节省出重复构建，诸如 hashCode 和 equals 这样的方法以及各种业务对象模型的 accessor 和 toString 等方法的大量时间。对于这些方法，Lombok 能够在编译源代码期间自动帮我们生成这些方法，但并不会像反射那样降低程序的性能

## 2. Lombok 安装

### 2.1. Gradle

在 build.gradle 文件中添加 lombok 依赖

```gradle
dependencies {
    compileOnly 'org.projectlombok:lombok:1.18.10'
    annotationProcessor 'org.projectlombok:lombok:1.18.10'
}
```

### 2.2. Maven

在 Maven 项目的 pom.xml 文件中添加 lombok 依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.10</version>
    <scope>provided</scope>
</dependency>
```

### 2.3. Ant

假设在 lib 目录中已经存在 lombok.jar，然后设置 javac 任务

```xml
<javac srcdir="src" destdir="build" source="1.8">
    <classpath location="lib/lombok.jar" />
</javac>
```

### 2.4. IDE 插件安装

直接引入 lombok 依赖，此时 IDE 中的工程会因为没有相应的实体方法而报错，因此需要安装对应的 lombok 插件。插件项目官方地址：https://www.projectlombok.org/

#### 2.4.1. eclipse 安装

在官网中下载 lombok.jar 包，双击安装，选择 eclipse 的目录

#### 2.4.2. idea 安装

1. 打开 IntelliJ IDEA 后点击菜单栏中的【File】 -> 【Settings】进入到设置页面
2. 点击设置页面中的 Plugins 进行插件的安装，在右侧选择 【Browse repositories...】，然后在搜索页面输入 lombok，可以查询到下方的 Lombok Plugin，鼠标点击 Lombok Plugin 可在右侧看到 【Install】 按钮，点击该按钮便可安装

> Tips: 在较新版本的 IDEA 中，已经自带 lombok 插件

## 3. 使用详解

> Notes: 以下示例所使用的 Lombok 版本是 1.18.10。示例代码在编译后可以使用反编译工具查看生成的 class 文件内容

### 3.1. lombok 常用注解汇总表

|            注解             |                                                         说明                                                         |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| `@Setter`                  | 注解在类或属性，注解在类时为所有属性生成setter方法，注解在属性上时只为该属性生成setter方法                                   |
| `@Getter`                  | 使用方法同`@Setter`，区别在于生成的是getter方法                                                                         |
| `@ToString`                | 注解在类，添加`toString`方法                                                                                          |
| `@EqualsAndHashCode`       | 注解在类，生成`hashCode`和`equals`方法                                                                                |
| `@NoArgsConstructor`       | 注解在类，生成无参的构造方法                                                                                           |
| `@RequiredArgsConstructor` | 注解在类，为类中需要特殊处理的属性生成构造方法，比如`final`和被`@NonNull`注解的属性                                        |
| `@AllArgsConstructor`      | 注解在类，生成包含类中所有属性的构造方法                                                                                 |
| `@Data`                    | 注解在类，生成setter/getter、equals、canEqual、hashCode、toString方法，如为`final`修饰的属性，则不会为该属性生成setter方法 |
| `@Slf4j`                   | 注解在类，生成log变量，用于记录日志。注意工程需要引入相应的slf4j的具体实现                                                 |
| `@Builder`                 | 将类转变为建造者模式                                                                                                  |

### 3.2. @Getter/@Setter 注解

使用 `@Getter` 或 `@Setter` 注释任何类或字段，Lombok 会自动生成默认的 getter/setter 方法

- `@Getter` 注解源码

```java
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Getter {
    lombok.AccessLevel value() default lombok.AccessLevel.PUBLIC;
    AnyAnnotation[] onMethod() default {};
    boolean lazy() default false;
}
```

- `@Setter` 注解源码

```java
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Setter {
    lombok.AccessLevel value() default lombok.AccessLevel.PUBLIC;
    AnyAnnotation[] onMethod() default {};
    AnyAnnotation[] onParam() default {};
}
```

#### 3.2.1. 使用示例

```java
package com.moon.testmodule.lombok;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class GetterAndSetterDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
}
```

以上代码经过 Lombok 编译后，会生成如下代码

```java
package com.moon.testmodule.lombok;

public class GetterAndSetterDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;

    public GetterAndSetterDemo() {
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    // 省略其他getter与setter...
}
```

#### 3.2.2. Lazy Getter

`@Getter` 注解支持一个 `lazy` 属性，该属性默认为 false。当设置为 true 时，会启用延迟初始化，即当首次调用 getter 方法时才进行初始化

```java
package com.moon.testmodule.lombok;

import lombok.Getter;

public class LazyGetterDemo {
    public static void main(String[] args) {
        LazyGetterDemo m = new LazyGetterDemo();
        System.out.println("Main instance is created");
        m.getLazy();
    }

    @Getter
    private final String notLazy = createValue("not lazy");

    @Getter(lazy = true)
    private final String lazy = createValue("lazy");

    private String createValue(String name) {
        System.out.println("createValue(" + name + ")");
        return null;
    }
}

```

以上代码经过 Lombok 编译后，会生成如下代码

```java
package com.moon.testmodule.lombok;

public class LazyGetterDemo {
    private final String notLazy = this.createValue("not lazy");
    private final AtomicReference<Object> lazy = new AtomicReference();

    public String getNotLazy() {
        return this.notLazy;
    }

    public String getLazy() {
        Object value = this.lazy.get();
        if (value == null) {
            synchronized(this.lazy) {
                value = this.lazy.get();
                if (value == null) {
                    String actualValue = this.createValue("lazy");
                    value = actualValue == null ? this.lazy : actualValue;
                    this.lazy.set(value);
                }
            }
        }
        return (String)((String)(value == this.lazy ? null : value));
    }
}
```

运行结果

```console
createValue(not lazy)
Main instance is created
createValue(lazy)
```

> 通过以上代码可知，调用 getLazy 方法时，若发现 value 为 null，则会在同步代码块中执行初始化操作

### 3.3. Constructor Annotations(构造方法注解)
#### 3.3.1. @NoArgsConstructor 注解

使用 `@NoArgsConstructor` 注解可以为指定类，生成默认的构造函数，`@NoArgsConstructor` 源码定义如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NoArgsConstructor {
    String staticName() default "";
    AnyAnnotation[] onConstructor() default {};

    AccessLevel access() default lombok.AccessLevel.PUBLIC;

    boolean force() default false;
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "getInstance")
public class NoArgsConstructorDemo {
    private long id;
    private String name;
    private int age;
}
```

以上代码经过 Lombok 编译后，会生成如下代码；

```java
package com.moon.testmodule.lombok;

public class NoArgsConstructorDemo {
    private long id;
    private String name;
    private int age;

    private NoArgsConstructorDemo() {
    }

    public static NoArgsConstructorDemo getInstance() {
        return new NoArgsConstructorDemo();
    }
}
```

#### 3.3.2. @AllArgsConstructor 注解

使用 `@AllArgsConstructor` 注解可以为指定类，生成包含所有成员的构造函数，`@AllArgsConstructor` 注解的源码定义如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AllArgsConstructor {
    String staticName() default "";
    AnyAnnotation[] onConstructor() default {};
    AccessLevel access() default lombok.AccessLevel.PUBLIC;
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AllArgsConstructorDemo {
    private long id;
    private String name;
    private int age;
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class AllArgsConstructorDemo {
    private long id;
    private String name;
    private int age;

    public AllArgsConstructorDemo(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
```

#### 3.3.3. @RequiredArgsConstructor 注解

使用 `@RequiredArgsConstructor` 注解可以为指定类必需初始化的成员变量，如 final 成员变量，生成对应的构造函数，`@RequiredArgsConstructor` 注解的源码定义如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RequiredArgsConstructor {
    String staticName() default "";
    AnyAnnotation[] onConstructor() default {};
    AccessLevel access() default lombok.AccessLevel.PUBLIC;
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequiredArgsConstructorDemo {
    private final long id;
    private String name;
    private int age;
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class RequiredArgsConstructorDemo {
    private final long id;
    private String name;
    private int age;

    public RequiredArgsConstructorDemo(long id) {
        this.id = id;
    }
}
```

### 3.4. @EqualsAndHashCode 注解

使用 `@EqualsAndHashCode` 注解可以为指定类生成 equals 和 hashCode 方法，`@EqualsAndHashCode` 注解的源码定义如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EqualsAndHashCode {
    String[] exclude() default {};

    String[] of() default {};

    boolean callSuper() default false;

    boolean doNotUseGetters() default false;

    AnyAnnotation[] onParam() default {};

    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    @interface AnyAnnotation {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Exclude {}

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Include {
        String replaces() default "";
    }
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode
public class EqualsAndHashCodeDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

import java.time.LocalDate;

public class EqualsAndHashCodeDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;

    public EqualsAndHashCodeDemo() {
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EqualsAndHashCodeDemo)) {
            return false;
        } else {
            EqualsAndHashCodeDemo other = (EqualsAndHashCodeDemo) o;
            if (!other.canEqual(this)) {
                return false;
            } else {

            }
        }

        public int hashCode () {
            int PRIME = true;
            int result = 1;
            Object $firstName = this.firstName;
            int result = result * 59 + ($firstName == null ? 43 : $firstName.hashCode());
            Object $lastName = this.lastName;
            result = result * 59 + ($lastName == null ? 43 : $lastName.hashCode());
            Object $dateOfBirth = this.dateOfBirth;
            result = result * 59 + ($dateOfBirth == null ? 43 : $dateOfBirth.hashCode());
            return result;
        }
    }
}
```

### 3.5. @ToString 注解

使用 `@ToString` 注解可以为指定类生成 `toString` 方法，`@ToString` 注解的源码定义如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ToString {
    boolean includeFieldNames() default true;

    String[] exclude() default {};

    String[] of() default {};

    boolean callSuper() default false;

    boolean doNotUseGetters() default false;

    boolean onlyExplicitlyIncluded() default false;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Exclude {}

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Include {
        int rank() default 0;
        String name() default "";
    }
}
```

使用示例

```java
package com.moon.testmodule.lombok;

import lombok.ToString;

import java.time.LocalDate;

@ToString(exclude = {"dateOfBirth"}) // toString方法不包含dateOfBirth字段
public class ToStringDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
}
```

以上代码经过 Lombok 编译后，会生成如下代码；

```java
package com.moon.testmodule.lombok;

import java.time.LocalDate;

public class ToStringDemo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;

    public ToStringDemo() {
    }

    public String toString() {
        return "ToStringDemo(firstName=" + this.firstName + ", lastName=" +
                this.lastName + ")";
    }
}
```

### 3.6. @Data 注解

- `@Data` 注解与同时使用以下的注解的效果是一样的，`@Data` 注解的源码定义如下
    - `@ToString`
    - `@Getter`
    - `@Setter`
    - `@RequiredArgsConstructor`
    - `@EqualsAndHashCode`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Data {
    String staticConstructor() default "";
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.Data;

@Data
public class DataDemo {
    private Long id;
    private String summary;
    private String description;
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class DataDemo {
    private Long id;
    private String summary;
    private String description;

    public DataDemo() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DataDemo)) {
            return false;
        } else {
            DataDemo other = (DataDemo) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DataDemo;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $summary = this.getSummary();
        result = result * 59 + ($summary == null ? 43 : $summary.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "DataDemo(id=" + this.getId() + ", summary=" + this.getSummary() + ", description=" + this.getDescription() + ")";
    }
}
```

### 3.7. @Log 注解及其衍生注解

若将 `@Log` 的相关衍生注解标识在类上（适用于所使用的日志记录系统的任何一种），被修改的类将拥有一个静态的 final log 字段，然后就可以使用该字段来输出日志

```java
@Log
private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());

@Log4j
private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogExample.class);

@Log4j2
private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LogExample.class);

@Slf4j
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);

@XSlf4j
private static final org.slf4j.ext.XLogger log = org.slf4j.ext.XLoggerFactory.getXLogger(LogExample.class);

@CommonsLog
private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LogExample.class);
```

### 3.8. @Synchronized 注解

`@Synchronized` 是同步方法修饰符的更安全的变体。与 synchronized 一样，该注解只能应用在静态和实例方法上。它的操作类似于 synchronized 关键字，但是它锁定在不同的对象上。synchronized 关键字应用在实例方法时，锁定的是 this 对象，而应用在静态方法上锁定的是类对象。对于 `@Synchronized` 注解声明的方法来说，它锁定的是 `$LOCK` 或 `$lock`。`@Synchronized` 注解的源码定义如下：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Synchronized {
    String value() default "";
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.Synchronized;

public class SynchronizedDemo {
    private final Object readLock = new Object();
    @Synchronized
    public static void hello() {
        System.out.println("world");
    }
    @Synchronized
    public int answerToLife() {
        return 42;
    }
    @Synchronized("readLock")
    public void foo() {
        System.out.println("bar");
    }
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class SynchronizedDemo {
    private static final Object $LOCK = new Object[0];
    private final Object $lock = new Object[0];
    private final Object readLock = new Object();

    public SynchronizedDemo() {
    }

    public static void hello() {
        synchronized ($LOCK) {
            System.out.println("world");
        }
    }

    public int answerToLife() {
        synchronized (this.$lock) {
            return 42;
        }
    }

    public void foo() {
        synchronized (this.readLock) {
            System.out.println("bar");
        }
    }
}
```

### 3.9. @Builder 注解

使用 `@Builder` 注解可以为指定类实现建造者模式，该注解可以放在类、构造函数或方法上。`@Builder` 注解的源码定义如下：

```java
@Target({TYPE, METHOD, CONSTRUCTOR})
@Retention(SOURCE)
public @interface Builder {
    @Target(FIELD)
    @Retention(SOURCE)
    public @interface Default {}

    String builderMethodName() default "builder";

    String buildMethodName() default "build";

    String builderClassName() default "";

    boolean toBuilder() default false;

    AccessLevel access() default lombok.AccessLevel.PUBLIC;

    @Target({FIELD, PARAMETER})
    @Retention(SOURCE)
    public @interface ObtainVia {
        String field() default "";
        String method() default "";
        boolean isStatic() default false;
    }
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.Builder;

@Builder
public class BuilderDemo {
    private final String firstname;
    private final String lastname;
    private final String email;
}
```

```java
//建造者模式
BuilderDemo builderDemo = BuilderDemo.builder().firstname("MooN").lastname("kirA").email("moon@moon.com").build();
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class BuilderDemo {
    private final String firstname;
    private final String lastname;
    private final String email;

    BuilderDemo(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public static BuilderDemo.BuilderDemoBuilder builder() {
        return new BuilderDemo.BuilderDemoBuilder();
    }

    public static class BuilderDemoBuilder {
        private String firstname;
        private String lastname;
        private String email;

        BuilderDemoBuilder() {
        }

        public BuilderDemo.BuilderDemoBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public BuilderDemo.BuilderDemoBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public BuilderDemo.BuilderDemoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public BuilderDemo build() {
            return new BuilderDemo(this.firstname, this.lastname, this.email);
        }

        public String toString() {
            return "BuilderDemo.BuilderDemoBuilder(firstname=" + this.firstname + ", lastname=" + this.lastname + ", email=" + this.email + ")";
        }
    }
}
```

### 3.10. @SneakyThrows 注解

`@SneakyThrows` 注解用于自动抛出已检查的异常，而无需在方法中使用 throw 语句显式抛出。`@SneakyThrows` 注解的源码定义如下：

```java
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface SneakyThrows {
    Class<? extends Throwable>[] value() default java.lang.Throwable.class;
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.SneakyThrows;

public class SneakyThrowsDemo {
    @SneakyThrows
    @Override
    protected Object clone() {
        return super.clone();
    }
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

public class SneakyThrowsDemo {
    public SneakyThrowsDemo() {
    }

    protected Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (Throwable var2) {
            throw var2;
        }
    }
}
```

### 3.11. @NonNull 注解

在方法或构造函数的参数上使用 `@NonNull` 注解，它将会为你自动生成非空校验语句。`@NonNull` 注解的定义如下：

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface NonNull {
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class NonNullDemo {
    @Getter
    @Setter
    @NonNull
    private String name;
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
package com.moon.testmodule.lombok;

import lombok.NonNull;

public class NonNullDemo {
    @NonNull
    private String name;

    public NonNullDemo() {
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            this.name = name;
        }
    }
}
```

### 3.12. @Clean 注解

`@Clean` 注解用于自动管理资源，用在局部变量之前，在当前变量范围内即将执行完毕退出之前会自动清理资源，自动生成 try-finally 这样的代码来关闭流。注解的源码定义如下：

```java
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.SOURCE)
public @interface Cleanup {
    String value() default "close";
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.Cleanup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CleanupDemo {
    public static void main(String[] args) throws IOException {
        @Cleanup InputStream in = new FileInputStream(args[0]);
        @Cleanup OutputStream out = new FileOutputStream(args[1]);
        byte[] b = new byte[10000];
        while (true) {
            int r = in.read(b);
            if (r == -1) break;
            out.write(b, 0, r);
        }
    }
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
public class CleanupDemo {

    public CleanupDemo() {
    }

    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream(args[0]);

        try {
            FileOutputStream out = new FileOutputStream(args[1]);

            try {
                byte[] b = new byte[10000];

                while (true) {
                    int r = in.read(b);
                    if (r == -1) {
                        return;
                    }
                    out.write(b, 0, r);
                }
            } finally {
                if (Collections.singletonList(out).get(0) != null) {
                    out.close();
                }
            }
        } finally {
            if (Collections.singletonList(in).get(0) != null) {
                in.close();
            }
        }
    }
}
```

### 3.13. @With 注解(1.18.10版本后才出现)

在类的字段上应用 `@With` 注解之后，将会自动生成一个 `withFieldName(newValue)` 的方法，该方法会基于 newValue 调用相应构造函数，创建一个当前类对应的实例。`@With` 注解的源码定义如下：

```java
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface With {
    AccessLevel value() default AccessLevel.PUBLIC;

    With.AnyAnnotation[] onMethod() default {};

    With.AnyAnnotation[] onParam() default {};

    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    @Target({})
    public @interface AnyAnnotation {
    }
}
```

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.With;

public class WithDemo {
    @With(AccessLevel.PROTECTED)
    @NonNull
    private final String name;
    @With
    private final int age;

    public WithDemo(String name, int age) {
        if (name == null) throw new NullPointerException();
        this.name = name;
        this.age = age;
    }
}
```

以上代码经过 Lombok 编译后，会生成如下代码：

```java
public class WithDemo {
    @NonNull
    private final String name;
    private final int age;

    public WithDemo(String name, int age) {
        if (name == null) {
            throw new NullPointerException();
        } else {
            this.name = name;
            this.age = age;
        }
    }

    protected WithDemo withName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            return this.name == name ? this : new WithDemo(name, this.age);
        }
    }

    public WithDemo withAge(int age) {
        return this.age == age ? this : new WithDemo(this.name, age);
    }
}
```

### 3.14. 其它特性

val 用在局部变量前面，相当于将变量声明为 final，此外 Lombok 在编译时还会自动进行类型推断。val 的使用示例：

使用示例：

```java
package com.moon.testmodule.lombok;

import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;

public class ValExample {
    public String example() {
        val example = new ArrayList<String>();
        example.add("Hello, World!");
        val foo = example.get(0);
        return foo.toLowerCase();
    }

    public void example2() {
        val map = new HashMap<Integer, String>();
        map.put(0, "zero");
        map.put(5, "five");
        for (val entry : map.entrySet()) {
            System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
        }
    }
}
```

以上代码等价于：

```java
public class ValExample {
    public String example() {
        final ArrayList<String> example = new ArrayList<String>();
        example.add("Hello, World!");
        final String foo = example.get(0);
        return foo.toLowerCase();
    }

    public void example2() {
        final HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "zero");
        map.put(5, "five");
        for (final Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
        }
    }
}
```

## 4. 其他

> 建议阅读猿码道大佬 十分钟搞懂Lombok使用与原理 这篇文章：https://juejin.im/post/5a6eceb8f265da3e467555fe
>
> 作者 ：semlinker
>
> 链接：https://segmentfault.com/a/1190000020864572
>
> 项目地址：Github - springboot2-lombok
