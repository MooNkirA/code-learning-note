# 设计模式

## 1. 简介

设计模式（Design Pattern），是经过高度抽象化的在编程中可以被反复使用的代码设计经验的总结，是解决特定问题的一系列套路，是一种编程思想。它不是语法规定，而是一套用来提高代码可复用性、可维护性、可读性、稳健性以及安全性的解决方案。

正确使用设计模式能有效提高代码的可读性、可重用性和可靠性，编写符合设计模式规范的代码不但有利于自身系统的稳定、可靠，还有利于外部系统的对接。在使用了良好的设计模式的系统工程中，无论是对满足当前的需求，还是对适应未来的需求，无论是对自身系统间模块的对接，还是对外部系统的对接，都有很大的帮助。

随着软件工程的不断演进，针对不同的需求，新的设计模式不断被提出（比如大数据领域中这些年不断被大家认可的数据分片思想），但设计模式的原则不会变。基于设计模式的原则，可以使用已有的设计模式，也可以根据产品或项目的开发需求在现有的设计模式基础上组合、改造或重新设计自身的设计模式。

## 2. 设计模式的原则

设计模式有 7 个原则：单一职责原则、开闭原则、里氏代换原则、依赖倒转原则、接口隔离原则、合成／聚合复用原则、迪米特法则。具体内容如下：

1. **单一职责原则**，又称单一功能原则，它规定一个类只有一个职责。如果有多个职责（功能）被设计在一个类中，这个类就违反了单一职责原则。
2. **开闭原则**，规定软件中的对象（类、模块、函数等）对扩展开放，对修改封闭，这意味着一个实体允许在不改变其源代码的前提下改变其行为，该特性在产品化的环境下是特别有价值的，在这种环境下，改变惊代码需要经过代码审查、单元测试等过程，以确保产品的使用质量。遵循这个原则的代码在扩展时并不发生改变，因此不需要经历上述过程。
3. **里氏代换原则**，是对开闭原则的补充，规定了在任意父类可以出现的地方，子类都一定可以出现。实现开闭原则的关键就是抽象化，父类与子类的继承关系就是抽象化的具体表现，所以里氏代换原则是对实现抽象化的具体步骤的规范。
4. **依赖倒转原则**，指程序要依赖于抽象（Java 中的抽象类和接口），而不依赖于具体的实现（Java 中的实现类）。简单地说，就是要求对抽象进行编程，不要求对实现进行编程，这就降低了用户与实现模块之间的耦合度。
5. **接口隔离原则**，指通过将不同的功能定义在不同的接口中来实现接口的隔离，这样就避免了其他类在依赖该接口（接口上定义的功能）时依赖其不需要的接口，可减少接口之间依赖的冗余性和复杂性。
6. **合成／聚合复用原则**，指通过在一个新的对象中引人（注入）已有的对象以达到类的功能复用和扩展的目的。它的设计原则是要尽量使用合成或聚合而不要使用继承来扩展类的功能。
7. **迪米特法则**，指一个对象尽可能少地与其他对象发生相互了解或依赖。其核心思想在于降低模块之间的耦合度，提高模块的内聚性。迪米特法则规定每个模块对其他模块都要有尽可能少的了解和依赖，因此很容易使系统模块之间功能独立，这使得各个模块的独立运行变得更简单，同时使得各个模块之间的组合变得更容易。

## 3. 设计模式的分类

设计模式按照其功能和使用场景可以分为三大类：**创建型模式（Creational Pattern）**、**结构型模式（Structural Pattern）**和**行为型模式（Behavioral Pattern）**

- **创建型模式**：提供了多种优雅创建对象的方法
  - [ ] 工厂模式（Factory Pattern）
  - [ ] 抽象工厂模式（A bstract Factory Pattern）
  - [x] 单例模式（Singleton Pattern）
  - [ ] 建造者模式（Builder Pattern）
  - [ ] 原型模式（Prototype Pattern）
- **结构型模式**：通过类和接口之间的继承和引用实现创建复杂结构对象的功能
  - [x] 适配器模式（Adapter Pattern ）
  - [ ] 桥接模式（Bridge Pattern）
  - [ ] 过滤器模式（Filter 、Criteria Pattern）
  - [ ] 组合模式（Composite Pattern）
  - [ ] 装饰器模式（Decorator Pattern）
  - [ ] 外观模式（Facade Pattern）
  - [ ] 享元模式（Flyweight Pattern）
  - [ ] 代理模式（Proxy Pattern）
- **行为型模式**：通过类之间不同的通信方式实现不同的行为方式
  - [ ] 责任链模式（Chain of Responsibility Pattern）
  - [ ] 命令模式（Command Pattern）
  - [ ] 解释器模式（Interpreter Pattern）
  - [ ] 迭代器模式（Iterator Pattern）
  - [ ] 中介者模式（Mediator Pattern）
  - [ ] 备忘录模式（Memento Pattern）
  - [ ] 观察者模式（Observer Pattern）
  - [ ] 状态模式（State Pattern）
  - [ ] 策略模式（Strategy Pattern）
  - [x] 模板模式（Template Pattern）
  - [ ] 访问者模式（Visitor Pattern）

# 单例设计模式（Singleten Pattern）

> 框架中会经常出现单例类

## 1. 单例的特点

1. 类在程序的运行过程中只有一个对象(实例)。
2. 私有构造方法，不能让外界创建该类的对象。
3. 类必须提供一个静态公共方法返回该类的对象。

## 2. 单例实现方式（饿汉式）

当类加载时，就立即创建该单例对象。饿汉式是线程安全的。实现步骤如下：

1. 定义一个静态的对象成员变量
2. 要私有构造方法，如果不处理，系统会自动提供一个无参的构造方法。外界就可以直接new对象
3. 定义一个静态的成员方法，用来获取创建好的对象成员，一般命名用 `getInstance()`

```java
/**
 * 单例类，饿汉式：非懒加载。
 * 当类加载时，就立即创建该单例对象。以空间换时间
 */
public class EagerSingleton {

    // 定义一个静态的成员变量，在类加载完成之后都已经完成了初始化赋值的操作。
    private static EagerSingleton instance = new EagerSingleton();

    // 注意：要私有构造方法，如果不处理，系统会自动提供一个无参的构造方法。
    // 保证其他类对象使用时不能直接new一个新的实例
    private EagerSingleton() {
    }

    // 定义一个静态的成员方法，用来获取创建好的成员对象
    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

> 涉及知识点提示：所有的静态成员在类加载完成之后都已经完成了初始化赋值的操作。也就是`EagerSingleton`类的私有构造函数会被调用，实例会被创建。还有就是构造函数要使用`private`修饰，为了防止使用`new`关键字来创建一个新的实例，就会变成“多例”并存的情况。

## 3. 单例实现方式（懒汉式）

### 3.1. 实现前置分析

当外界第一次要使用该类的对象时，如果还没有创建出来，则创建该单例对象。懒汉式是线程不安全的，要处理线程安全问题，实际上创建对象要进过如下几个步骤：

1. 分配内存空间
2. 调用构造器，初始化实例
3. 返回地址给引用

因为创建对象是一个非原子操作，编译器可能会重排序⌈构造函数可能在整个对象初始化完成前执行完毕，即赋值操作（只是在内存中开辟一片存储区域后直接返回内存的引用）在初始化对象前完成⌋。而线程C在线程A赋值完时判断instance就不为null了，此时C拿到的将是一个没有初始化完成的半成品。这样是很危险的。因为极有可能线程C会继续拿着个没有初始化的对象中的数据进行操作，此时容易触发 NPE

另外还由于可见性问题，线程A在自己的工作线程内创建了实例，但此时还未同步到主存中；此时线程C在主存中判断 instance 还是 null，那么线程C又将在自己的工作线程中创建一个实例，这样就创建了多个实例。

### 3.2. 具体实现

综合以上分析后，推荐使用<font color=red>**volatile 双重检查模式**</font>来创建单例对象。从性能上考虑，一般选择同步代码块去处理线程安全问题。具体实现步骤如下：

1. 定义一个静态的对象成员变量
2. 要私有构造方法，如果不处理，系统会自动提供一个无参的构造方法。外界就可以直接 new 对象
3. 定义一个静态的成员方法，用来获取对象成员，一般命名用 `getInstance`
4. 需要考虑线程安全的问题。
5. 第一种是使用同步方法，增加 `synchronized`，在方法内再进行判断对象是否为空，如果为空，就直接创建。
6. 第二种是使用同步代码块，一开始先判断对象是否为空，为了性能的问题，为了后面的线程不再需要加锁；同步对象写，`LazySingleton.class` 保证锁对象被所有对象共享；

完整的示例实现代码如下：

```java
package com.moon.design.singleten;

/**
 * 单例类，懒加载
 * 懒汉式：当外界第一次要使用该类的对象时，如果还没有创建出来，则创建该单例对象。
 */
public class LazySingleton {

    // 定义一个静态的对象成员变量。注意：采用 volatile 关键字修饰也是很有必要
    private static volatile LazySingleton instance;

    // 要私有构造方法，如果不处理，系统会自动提供一个无参的构造方法。
    private LazySingleton() {
    }

    // 定义一个静态的成员方法，用来获取创建好的成员对象(同步代码块)
    public static LazySingleton getInstance1() {
        // 判断如果之前对象不存在就进行创建，再加个判断，为了性能的问题，为了后面的线程不再需要加锁
        if (instance == null) {
            // 为了解决线程安全问题，需要同步代码块，LazySingleton.class保证锁对象被所有对象共享
            // 如果在 instance==null 前已经有多个线程进来，所以同步方法块中的if判断不能省略
            // 在synchronized锁定代码中，需要再次进行是否为null检查。这种方法叫做双重检查锁定（Double-Check Locking）。
            synchronized (LazySingleton.class) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }

    // 定义一个静态的成员方法，用来获取创建好的成员对象(同步方法)
    public static synchronized LazySingleton getInstance2() {
        // 判断如果之前对象不存在就进行创建
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```

### 3.3. 小结

虽然懒汉模式在资源利用率上有一定优势，但是懒汉模式在高并发场景，也就是多线程场景下，遇到的问题也比较多。为了防止多个线程同时调用`getInstance`方法，需要在该方法前面增加关键字`synchronized`进行线程访问锁定。但是这样就引出了新的问题，在高并发场景下，每次调用都进行线程访问锁定判断，会对系统性能产生较大的负面影响。

上面示例中，为了解决高并发中的线程安全问题，在`synchronized`锁定代码中，需要再次进行是否为null检查，因为可能在`instance == null`前已经多个线程进来，如果不做判断，当前一个线程创建对象后，其他线程也可以抢到锁再进入创建对象。这种方法叫做双重检查锁定（Double-Check Locking）。另外，静态的对象成员变量 instance 采用 `volatile` 关键字修饰也是很有必要，使用 `volatile` 可以禁止 JVM 的指令重排，并且在第一个获取锁的线程创建实例后，保证其他线程的可见性，从而让程序在多线程环境下正常运行。

## 4. 单例实现方式（静态内部类实现式）

> mybatis框架，在创建`VFS`类单例实例使用了此种方式。具体参考框架的`org.apache.ibatis.io.VFS`类

静态内部类的实例方式，是利用 JVM 加载静态类的特性来实现。JVM 在类初始化阶段（即在Class被加载后，且线程使用之前），会执行类的初始化。在执行类的初始化期间，JVM 会去获取一个锁。这个锁可以同步多个线程对同一个类的初始化。因此**静态内部类实现式是线程安全的**。具体的实现步骤如下：

1. 定义一个静态的内部类
2. 要私有构造方法，如果不处理，系统会自动提供一个无参的构造方法。外界就可以直接 `new` 对象
3. 定义一个静态的成员方法，用来获取对象成员，一般命名用 `getInstance`
4. 在内部类中定义静态的成员变量，类型是外部类类型，初始化时调用外部类的构造函数，创建外部类的实例，是真正的实例变量。
5. 在`getInstance`的静态方法中，返回内部类的静态成员变量（*外部类的实例*）

```java
package com.moon.design.singleten;

/**
 * 单例类实现，静态内部类实现式
 */
public class HolderSingleton {

    private HolderSingleton() {
    }

    /*
     * 当getInstance方法第一次被调用的时候，它第一次读取InstanceHolder.INSTANCE时，会触发InstanceHolder类的初始化。
     * 而InstanceHolder类在装载并被初始化的时候，会初始化它的静态成员变量、静态域，从而创建HolderSingleton的实例。
     * 由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。
     * 这个模式的优势在于，getInstance方法并没有做线程同步控制，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。
     */
    public static HolderSingleton getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /* 内部类前面加static关键字，表示的是类级内部类，类级内部类只有在使用时才会被加载 */
    private static class InstanceHolder {
        // 静态变量的初始化是由JVM保证线程安全的，在类的加载时就完成了静态变量的赋值
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }
}
```

**小结**：

上述代码中：虽然内部类中的成员变量`INSTANCE`是被`static`修改，但这个是懒加载的，原因是<font color=red>**内部类前面加`static`关键字，表示的是类级内部类，类级内部类只有在使用时才会被加载**</font>。

具体的执行流程是：当`getInstance`方法第一次被调用的时候，它第一次读取`InstanceHolder.INSTANCE`时，会触发`InstanceHolder`类的初始化。而`InstanceHolder`类在装载并被初始化的时候，会初始化它的静态成员变量、静态域，从而创建`HolderSingleton`的实例。由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并静态变量的初始化是由虚拟机（JVM）来保证它的线程安全性，在内存只会存在一份，<u>*jvm的初始化时是线程互斥的（待日后理解）*</u>。这个模式的优势在于，`getInstance`方法并没有做线程同步控制，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。

通过对比基于 `volatile` 的双重检查锁定方案和基于类初始化方案的对比，会发现基于类初始化的方案的实现代码更简洁。但是基于 volatile 的双重检查锁定方案有一个额外的优势：**除了可以对静态字段实现延迟加载初始化外，还可以对实例字段实现延迟初始化**。

## 5. 单例实例方式（枚举式）

使用枚举来实现单例模式

```java
public enum EnumSingleton {
    instance;
}
```

首先创建 Enum 时，编译器会自动生成一个继承自`java.lang.Enum`的类，枚举成员声明中被`static`和`final`所修饰，虚拟机会保证这些静态成员在多线程环境中被正确的加锁和同步，所以是线程安全的。编译后生成的类代码如下：

```java
class EnumSingleton extends Enum {
    public static final EnumSingleton instance;
    ....省略
}
```

`Enum`的构造方法本身就是`private`修饰的，所以也防止了使用`new`关键字创建新实例。从Enum类的声明中也可以看出，Enum是提供了序列化的支持的，在某些需要序列化的场景下，提供了非常大的便利。另一个重要功能就是反序列化仍然可以保证对象在虚拟机范围内是单例的。

## 6. 单例实例方式（单例注册表式）

单例注册表来实现单例模式，Spring框架就是最有代表性的使用者。

![](images/20210318144052471_27465.png)

其实实现原理很容易理解，以一个`HashMap`（Spring 是使用了线程安全的`ConcurrentHashMap`）来存储目前已生成的类的实例，如果可以根据类名找到对象，就返回这个对象，不再创建新对象。如果找不到，就利用反射机制创建一个，并加入到 Map 中。以上只是一个示意代码，作为 Spring 核心理念IoC的重要部分，单例注册表在 Spring 中的源码要复杂的多，也做了很多性能上的优化，具体可以参考 Spring 中`AbstractBeanFactory`类的源码。

## 7. 涉及使用单例类的场景

- 一个类只要一个对象的时候
- 视频播放器，音频播放器
- 数据库的连接
- 设置信息

## 8. 其他小问题

开发中一般使用饿汉式，面试的时候使用懒汉式

# 享元模式（Flyweight Pattern）（!整理中）

## 1. 定义与特点

享元模式（Flyweight Pattern）：主要通过对象的复用来减少对象创建的次数和数量，以减少系统内存的使用和降低系统的负载。享元模式属于<font color=red>**结构型模式**</font>，在系统需要一个对象时，享元模式首先在系统中查找并尝试重用现有的对象，如果未找到匹配的对象，则创建新对象并将其缓存在系统中以便下次使用。

> wikipedia: A flyweight is an object that minimizes memory usage by sharing as much data as possible with other similar objects

享元模式会把其中共同的部分抽象出来，如果有相同的业务请求，则直接返回内存中已有的对象，避免频繁重复创建和销毁大量的对象，造成系统资源的浪费。

## 2. 模式的结构

![](images/467742009230263.png)

## 3. 基础实现

> TODO: 待整理！

# 模板方法设计模式（Template Method）

## 1. 定义与特点

模板方法（Template Method）模式的定义：定义一个操作中的算法骨架，而将算法的一些步骤延迟到子类中，使得子类可以不改变该算法结构的情况下重定义该算法的某些特定步骤。它是一种类行为型模式。

模式的主要优点如下：

1. 它封装了不变部分，扩展可变部分。它把认为是不变部分的算法封装到父类中实现，而把可变部分算法由子类继承实现，便于子类继续扩展。
2. 它在父类中提取了公共的部分代码，便于代码复用。
3. 部分方法是由子类实现的，因此子类可以通过扩展方式增加相应的功能，符合开闭原则。

该模式的主要缺点如下：

1. 对每个不同的实现都需要定义一个子类，这会导致类的个数增加，系统更加庞大，设计也更加抽象，间接地增加了系统实现的复杂度。
2. 父类中的抽象方法由子类实现，子类执行的结果会影响父类的结果，这导致一种反向的控制结构，它提高了代码阅读的难度。
3. 由于继承关系自身的缺点，如果父类添加新的抽象方法，则所有子类都要改一遍。

## 2. 模式的结构

模板方法模式，需要抽象类与具体子类之间的协作，利用多态来实现。模板方法模式包含以下主要角色：

### 2.1. 抽象类/抽象模板（Abstract Class）

抽象模板类，负责给出一个算法的轮廓和骨架。它由一个模板方法和若干个基本方法构成。这些方法的定义如下：

- **模板方法**：定义了算法的骨架，按某种顺序调用其包含的基本方法。
- **基本方法**：是整个算法中的一个步骤，包含以下几种类型。
    - **抽象方法**：在抽象类中声明，由具体子类实现。
    - **具体方法**：在抽象类中已经实现，在具体子类中可以继承或重写它。
    - **钩子方法**：在抽象类中已经实现，包括用于判断的逻辑方法和需要子类重写的空方法两种。

### 2.2. 具体子类/具体实现（Concrete Class）

具体实现类，实现抽象类中所定义的抽象方法和钩子方法，它们是一个抽象类中模板方法逻辑的其中一个组成步骤。

### 2.3. 模板方法模式的结构图

![](images/61195621220557.gif)

## 3. 基础实现

- 定义抽象类，类中定义主要的模板方法与抽象方法

```java
public abstract class AbstractClass {

    // 模板方法。定义了方法逻辑的骨架，按某种顺序调用其包含的基本方法。
    public void TemplateMethod() {
        abstractMethod1(); // 抽象方法
        hookMethod1();
        if (hookMethod2()) { // 抽象类已实现，但子类可以进行修改
            SpecificMethod(); // 具体方法
        }
        abstractMethod2(); // 抽象方法
    }

    // 具体方法
    public void SpecificMethod() {
        System.out.println("抽象类中的具体方法被调用...");
    }

    // 钩子方法1，方法无处理逻辑，由子类来重写
    public void hookMethod1() {
    }

    // 钩子方法2
    public boolean hookMethod2() {
        return true;
    }

    // 抽象方法1，由实现类来处理
    public abstract void abstractMethod1();

    // 抽象方法2，由实现类来处理
    public abstract void abstractMethod2();
}
```

- 创建实现类，

```java
public class ConcreteClass extends AbstractClass {

    @Override
    public void abstractMethod1() {
        System.out.println("抽象方法1的实现被调用...");
    }

    @Override
    public void abstractMethod2() {
        System.out.println("抽象方法2的实现被调用...");
    }

    @Override
    public void hookMethod1() {
        System.out.println("钩子方法1被重写...");
    }

    @Override
    public boolean hookMethod2() {
        // 改变抽象类中原有方法逻辑
        return false;
    }
}
```

- 测试调用模板方法

```java
public class TemplateMethodPattern {

    public static void main(String[] args) {
        AbstractClass tm = new ConcreteClass();
        tm.TemplateMethod();
    }
}
```

输出结果

```
抽象方法1的实现被调用...
钩子方法1被重写...
抽象方法2的实现被调用...
```

# 适配器模式（Adapter Pattern）

## 1. 定义与特点

适配器模式（Adapter）的定义：将一个类的接口转换成另外一个接口，使得原本由于接口不兼容而不能一起工作的那些类能一起工作。<font color=red>**适配器模式分为类结构型模式和对象结构型模式**</font>两种，前者类之间的耦合度比后者高，且要求程序员了解现有组件库中的相关组件的内部结构，所以应用相对较少些。

- 该模式的主要优点如下：
1. 客户端通过适配器可以透明地调用目标接口。
2. 复用了现存的类，程序员不需要修改原有代码而重用现有的适配者类。
3. 将目标类和适配者类解耦，解决了目标类和适配者类接口不一致的问题。
4. 在很多业务场景中符合开闭原则。

- 其缺点是：
1. 适配器编写过程需要结合业务场景全面考虑，可能会增加系统的复杂性。
2. 增加代码阅读难度，降低代码可读性，过多使用适配器会使系统代码变得凌乱。

## 2. 模式结构

类适配器模式可采用多重继承方式实现。Java 不支持多继承，但可以定义一个适配器类来实现当前系统的业务接口，同时又继承现有组件库中已经存在的组件。

对象适配器模式可釆用将现有组件库中已经实现的组件引入适配器类中，该类同时实现当前系统的业务接口。

### 2.1. 主要角色

适配器模式（Adapter）包含以下主要角色

1. 目标（Target）接口：当前系统业务所期待的接口，它可以是抽象类或接口。
2. 适配者（Adaptee）类：它是被访问和适配的现存组件库中的组件接口。
3. 适配器（Adapter）类：它是一个转换器，通过继承或引用适配者的对象，把适配者接口转换成目标接口，让客户端按目标接口的格式访问适配者。

### 2.2. 结构类图

**类适配器模式的结构图**

![](images/120982410226859.jpg)

**对象适配器模式的结构图**

![](images/196202410247025.jpg)

## 3. 基础实现

### 3.1. 准备公共接口与适配器

创建目标接口，最终客户端调用

```java
public interface Target {

    void request();

}
```

创建需要待适配者（类）

```java
public class Adaptee {

    public void specificRequest() {
        System.out.println("适配者中的业务代码被调用！");
    }
}
```

### 3.2. 类适配器模式

创建类适配器。继承适配者（类），并实现目标接口。在目标接口中，调用适配者中相应的逻辑。

```java
public class ClassAdapter extends Adaptee implements Target {

    public void request() {
        // 调用适配者的方法
        super.specificRequest();
    }
}
```

测试程序

```java
public static void main(String[] args) {
    System.out.println("类适配器模式测试：");
    // 创建适配器
    Target target = new ClassAdapter();
    // 调用目标接口方法，实际是调用了被适配类中相应的方法
    target.request();
}
```

测试结果

```
类适配器模式测试：
适配者中的业务代码被调用！
```

### 3.3. 对象适配器模式

创建对象适配器，只需要实现目标接口。在类中定义适配者类型的属性，通过构造函数或者 setter 方法获取到适配者对象，在调用目标接口方法时，通过适配者对象调用相应的方法

```java
public class ObjectAdapter implements Target {

    private final Adaptee adaptee;

    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    public void request() {
        // 通过适配者对象调用
        adaptee.specificRequest();
    }
}
```

测试程序

```java
public static void main(String[] args) {
    System.out.println("对象适配器模式测试：");
    Adaptee adaptee = new Adaptee();
    Target target = new ObjectAdapter(adaptee);
    // 调用目标接口方法，实际是调用了被适配者对象的方法
    target.request();
}
```

测试结果

```
对象适配器模式测试：
适配者中的业务代码被调用！
```

# 建造者模式（Builder Pattern）（整理中！）    

## 1. 定义与特点

建造者（Builder）模式的定义：指将一个复杂对象的构造与它的表示分离，使同样的构建过程可以创建不同的表示，这样的设计模式被称为建造者模式。它是将一个复杂的对象分解为多个简单的对象，然后一步一步构建而成。它将变与不变相分离，即产品的组成部分是不变的，但每一部分是可以灵活选择的。

- 该模式的主要优点如下：
1. 封装性好，构建和表示分离。
2. 扩展性好，各个具体的建造者相互独立，有利于系统的解耦。
3. 客户端不必知道产品内部组成的细节，建造者可以对创建过程逐步细化，而不对其它模块产生任何影响，便于控制细节风险。

- 其缺点如下：
1. 产品的组成部分必须相同，这限制了其使用范围。
2. 如果产品的内部变化复杂，如果产品内部发生变化，则建造者也要同步修改，后期维护成本较大。

建造者（Builder）模式和工厂模式的关注点不同：建造者模式注重零部件的组装过程，而工厂方法模式更注重零部件的创建过程，但两者可以结合使用。

## 2. 结构与实现

<font color=red>**建造者（Builder）模式由产品、抽象建造者、具体建造者、指挥者等 4 个要素构成**</font>

# 工厂设计模式

## 1. 定义

在 Java 中，万物皆对象，这些对象都需要创建，如果创建的时候直接 new 该对象，就会对该对象耦合严重，假如要更换对象，所有 new 对象的地方都需要修改一遍，这显然违背了软件设计的**开闭原则**。如果使用工厂来生产对象，只需要和工厂打交道即可，彻底和对象解耦，如果要更换对象，直接在工厂里更换该对象即可，达到了与对象解耦的目的；所以工厂模式最大的优点就是：**解耦**。

> 开闭原则：**对扩展开放，对修改关闭**。在程序需要进行拓展的时候，不能去修改原有的代码，实现一个热插拔的效果。简言之，是为了使程序的扩展性好，易于维护和升级。

**三种工厂模式**：

- 简单工厂模式
- 工厂方法模式
- 抽象工厂模式

### 1.1. 案例需求概述

需求：设计一个咖啡店点餐系统。设计一个咖啡类（Coffee），并定义其两个子类（美式咖啡【AmericanCoffee】和拿铁咖啡【LatteCoffee】）；再设计一个咖啡店类（CoffeeStore），咖啡店具有点咖啡的功能。

具体类的设计如下：

![](images/402620418249396.jpg)

> 类图的元素说明：
>
> 1. 类图中的符号：
>     - `+`：表示 public 权限方法
>     - `-`：表示 private 权限方法
>     - `#`：表示 protected 权限方法
> 2. 泛化关系(继承)用带空心三角箭头的实线来表示
> 3. 依赖关系使用带箭头的虚线来表示

### 1.2. 无设计模式实现

- 定义接口：

```java
public interface Coffee {
    /**
     * 获取名字
     */
    String getName();

    /**
     * 加牛奶
     */
    void addMilk();

    /**
     * 加糖
     */
    void addSuqar();
}


public class LatteCoffee implements Coffee {
    /**
     * 获取名字
     */
    @Override
    public String getName() {
        return "latteCoffee";
    }

    /**
     * 加牛奶
     */
    @Override
    public void addMilk() {
        System.out.println("LatteCoffee...addMilk...");
    }

    /**
     * 加糖
     */
    @Override
    public void addSuqar() {
        System.out.println("LatteCoffee...addSuqar...");
    }
}
```

- 定义实现类

```java
public class AmericanCoffee implements Coffee {
    /**
     * 获取名字
     */
    @Override
    public String getName() {
        return "americanCoffee";
    }

    /**
     * 加牛奶
     */
    @Override
    public void addMilk() {
        System.out.println("AmericanCoffee...addMilk...");
    }

    /**
     * 加糖
     */
    @Override
    public void addSuqar() {
        System.out.println("AmericanCoffee...addSuqar...");
    }
}
```

- 无使用设计模式实现获取不同实现类

```java
public class CoffeeStore {

    public static void main(String[] args) {
        Coffee coffee = orderCoffee("latte");
        System.out.println(coffee.getName());
    }


    public static Coffee orderCoffee(String type) {
        Coffee coffee = null;
        if ("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if ("latte".equals(type)) {
            coffee = new LatteCoffee();
        }

        // 对生产的对象做其他处理
        coffee.addMilk();
        coffee.addSuqar();
        return coffee;
    }
}
```

## 2. 简单工厂模式

简单工厂不是一种设计模式，反而比较像是一种编程习惯。

### 2.1. 涉及角色

简单工厂包含如下角色：

- 抽象产品：定义了产品的规范，描述了产品的主要特性和功能。
- 具体产品：实现或者继承抽象产品的子类
- 具体工厂：提供了创建产品的方法，调用者通过该方法来获取产品。

### 2.2. 具体实现

使用简单工厂模式对上面案例进行改进，类图如下：

![](images/46424318237263.png)

- 创建静态工厂类

```java
public class SimpleCoffeeFactory {

    public static Coffee createCoffee(String type) {
        Coffee coffee = null;
        if ("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if ("latte".equals(type)) {
            coffee = new LatteCoffee();
        }
        return coffee;
    }
}
```

> Tips: 在开发中，经常会将工厂类中的创建对象的功能定义为静态的，这就是静态工厂模式，它也不属于 23 种设计模式。

- 改进使用简单静态工厂模式

```java
public class CoffeeStore {

    public static void main(String[] args) {
        // 使用简单静态工厂模式
        Coffee coffee1 = simpleOrderCoffee("american");
        System.out.println(coffee1.getName());
    }

    public static Coffee simpleOrderCoffee(String type) {
        // 通过工厂获得对象，不需要知道对象实现的细节
        Coffee coffee = SimpleCoffeeFactory.createCoffee(type);

        if (Objects.nonNull(coffee)) {
            coffee.addMilk();
            coffee.addSuqar();
        }
        return coffee;
    }
}
```

总结：工厂（factory）处理创建对象的细节，一旦有了 SimpleCoffeeFactory，CoffeeStore 类中的 simpleOrderCoffee() 就变成此对象的客户，后期如果需要 Coffee 对象直接从工厂中获取即可。这样也就解除了和 Coffee 实现类的耦合，同时又产生了新的耦合，CoffeeStore 对象和 SimpleCoffeeFactory 工厂对象的耦合，工厂对象和商品对象的耦合。

后期如果再加新品种的咖啡，势必要需求修改 SimpleCoffeeFactory 的代码，违反了开闭原则。工厂类的客户端可能有很多，比如创建美团外卖等，这样只需要修改工厂类的代码，省去其他的修改操作。

### 2.3. 优缺点

- **优点**：封装了创建对象的过程，可以通过参数直接获取对象。把对象的创建和业务逻辑层分开，这样以后就避免了修改客户代码，如果要实现新产品直接修改工厂类，而不需要在原代码中修改，这样就降低了客户代码修改的可能性，更加容易扩展。
- **缺点**：增加新产品时还是需要修改工厂类的代码，违背了“开闭原则”。

## 3. 工厂方法模式

针对上例中的缺点，使用工厂方法模式就可以完美的解决，完全遵循开闭原则。

### 3.1. 概述

工厂方法模式，需要定义一个用于创建对象的接口，让子类决定实例化哪个产品类对象。工厂方法使一个产品类的实例化延迟到其工厂的子类。

### 3.2. 涉及角色

工厂方法模式的主要角色：

- 抽象工厂（Abstract Factory）：提供了创建产品的接口，调用者通过它访问具体工厂的工厂方法来创建产品。
- 具体工厂（ConcreteFactory）：主要是实现抽象工厂中的抽象方法，完成具体产品的创建。
- 抽象产品（Product）：定义了产品的规范，描述了产品的主要特性和功能。
- 具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它同具体工厂之间一一对应。

### 3.3. 具体实现

使用工厂方法模式对上例进行改进，类图如下：

![](images/113274319257429.png)

- 抽象工厂

```java
public interface CoffeeFactory {
    /**
     * 创建咖啡
     */
    Coffee createCoffee();
}
```

- 定义具体的工厂实现

```java
public class AmericanCoffeeFactory implements CoffeeFactory {
    /**
     * 创建美式咖啡
     */
    @Override
    public Coffee createCoffee() {
        return new AmericanCoffee();
    }
}

public class LatteCoffeeFactory implements CoffeeFactory {
    /**
     * 创建拿铁咖啡
     */
    @Override
    public Coffee createCoffee() {
        return new LatteCoffee();
    }
}
```

- 改造通过不同的工厂生成相应的对象

```java
public class CoffeeStore {

    public static void main(String[] args) {
        // 使用工厂方法模式
        CoffeeStore coffeeStore = new CoffeeStore(new LatteCoffeeFactory());

        Coffee coffee2 = coffeeStore.methodOrderCoffee();
        System.out.println(coffee2.getName());
    }


    private CoffeeFactory coffeeFactory;

    public CoffeeStore(CoffeeFactory coffeeFactory){
        this.coffeeFactory = coffeeFactory;
    }

    public Coffee methodOrderCoffee() {
        // 可以根据不同的工厂，创建不同的产品
        Coffee coffee = coffeeFactory.createCoffee();

        if (Objects.nonNull(coffee)) {
            coffee.addMilk();
            coffee.addSuqar();
        }
        return coffee;
    }
}
```

总结：从以上编写的代码可以看到，工厂方法模式是简单工厂模式的进一步抽象。由于使用了多态性，要增加产品类时也要相应地增加工厂类，不需要修改工厂类的代码。工厂方法模式保持了简单工厂模式的优点，而且解决了简单工厂模式的缺点。

### 3.4. 优缺点

**优点：**

- 用户只需要知道具体工厂的名称就可得到所要的产品，无须知道产品的具体创建过程；
- 在系统增加新的产品时只需要添加具体产品类和对应的具体工厂类，无须对原工厂进行任何修改，满足开闭原则；

**缺点：**

- 每增加一个产品就要增加一个具体产品类和一个对应的具体工厂类，这增加了系统的复杂度。

## 4. 抽象工厂模式

这些工厂只生产同种类产品，同种类产品称为同等级产品，也就是说：工厂方法模式只考虑生产同等级的产品，但是在现实生活中许多工厂是综合型的工厂，能生产多等级（种类） 的产品，如电器厂既生产电视机又生产洗衣机或空调，大学既有软件专业又有生物专业等。










# 策略模式（Strategy Pattern）(整理中！)

> TODO: 待整理

# 装饰器模式(整理中！)

## 1. 定义与特点

装饰器（Decorator）模式的定义：指在不改变现有对象结构的情况下，动态地给该对象增加一些职责（即增加其额外功能）的模式，它属于对象结构型模式。

装饰器模式的主要优点有：

- 装饰器是继承的有力补充，比继承灵活，在不改变原有对象的情况下，动态的给一个对象扩展功能，即插即用
- 通过使用不用装饰类及这些装饰类的排列组合，可以实现不同效果
- 装饰器模式完全遵守开闭原则

其主要缺点是：装饰器模式会增加许多子类，过度使用会增加程序得复杂性。

# 责任链模式（Chain of Responsibility）

## 1. 定义与特点

责任链（Chain of Responsibility）模式（也叫职责链模式）的定义：为了避免请求发送者与多个请求处理者耦合在一起，于是将所有请求的处理者通过前一对象记住其下一个对象的引用而连成一条链；当有请求发生时，可将请求沿着这条链传递，直到有对象处理它为止。

责任链模式的本质是解耦请求与处理，让请求在处理链中能进行传递与被处理。责任链模式的独到之处是将其节点处理者组合成了链式结构，并允许节点自身决定是否进行请求处理或转发，相当于让请求流动起来。

责任链模式是一种对象行为型模式，其主要优点如下：

1. 降低了对象之间的耦合度。该模式使得一个对象无须知道到底是哪一个对象处理其请求以及链的结构，发送者和接收者也无须拥有对方的明确信息。
2. 增强了系统的可扩展性。可以根据需要增加新的请求处理类，满足开闭原则。
3. 增强了给对象指派职责的灵活性。当工作流程发生变化，可以动态地改变链内的成员或者调动它们的次序，也可动态地新增或者删除责任。
4. 责任链简化了对象之间的连接。每个对象只需保持一个指向其后继者的引用，不需保持其他所有处理者的引用，这避免了使用众多的 if 或者 if···else 语句。
5. 责任分担。每个类只需要处理自己该处理的工作，不该处理的传递给下一个对象完成，明确各类的责任范围，符合类的单一职责原则。

其主要缺点如下：

1. 不能保证每个请求一定被处理。由于一个请求没有明确的接收者，所以不能保证它一定会被处理，该请求可能一直传到链的末端都得不到处理。
2. 对比较长的职责链，请求的处理可能涉及多个处理对象，系统性能将受到一定影响。
3. 职责链建立的合理性要靠客户端来保证，增加了客户端的复杂性，可能会由于职责链的错误设置而导致系统出错，如可能会造成循环调用。

## 2. 模式结构

### 2.1. 主要角色

职责链模式主要包含以下角色：

- 抽象处理者（Handler）角色：定义一个处理请求的接口，包含抽象处理方法和一个后继连接。
- 具体处理者（Concrete Handler）角色：实现抽象处理者的处理方法，判断能否处理本次请求，如果可以处理请求则处理，否则将该请求转给它的后继者。
- 客户类（Client）角色：创建处理链，并向链头的具体处理者对象提交请求，它不关心处理细节和请求的传递过程。

### 2.2. 结构图

![](images/1364513220566.jpg)

![](images/492494513238992.jpg)

## 3. 基础实现

抽象处理者角色

```java
public abstract class Handler {

    private Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public Handler getNext() {
        return next;
    }

    // 使用模板模式，定义具体处理请求的流程的方法
    public final void handleRequest(String request) {
        // 调用子类实现的处理流程
        if (!doHandle(request)) {
            if (getNext() != null) {
                getNext().handleRequest(request);
            } else {
                System.out.println("没有人处理该请求！");
            }
        }
    }

    // 定义抽象的模板方法，由每个子类具体实现
    public abstract boolean doHandle(String request);
}
```

创建具体处理者角色x（其他都可以复制里面的代码用于测试）

```java
public class ConcreteHandler1 extends Handler {
    @Override
    public boolean doHandle(String request) {
        System.out.println("流转至 ConcreteHandler1");
        boolean b = request.equals("one");
        if (b) {
            System.out.println("具体处理者1负责处理该请求！");
        }
        return b;
    }
}
```

职责链模式测试

```java
@Test
public void chainOfResponsibilityPatternTest() {
    // 初始化责任链
    Handler handler1 = new ConcreteHandler1();
    Handler handler2 = new ConcreteHandler2();
    Handler handler3 = new ConcreteHandler3();
    handler1.setNext(handler2);
    handler2.setNext(handler3);

    // 调用键进行处理
    handler1.handleRequest("three");
}
```

测试结果：

```
流转至 ConcreteHandler1
流转至 ConcreteHandler2
流转至 ConcreteHandler3
具体处理者3负责处理该请求！
```

# 门面模式（整理中！）

> 在软件开发领域有这样一句话：计算机科学领域的任何问题都可以通过增加一个间接的中间层来解决。而门面模式就是对于这句话的典型实践。

门面模式（Facade Pattern），也称之为外观模式，其核心为：外部与一个子系统的通信必须通过一个统一的外观对象进行，使得子系统更易于使用。

# 其他

## 1. 网络参考资料

- [设计模式内容聚合](https://mp.weixin.qq.com/s?__biz=MzI4Njc5NjM1NQ==&mid=2247487936&idx=2&sn=02c48b88fa45a32e55b99fa0be610a29&chksm=ebd62eecdca1a7faccf9f26d17bd2108e1b8939421640f71f1ebf5f14b621448ac05ad168a00&mpshare=1&scene=1&srcid=#rd)
- ~~[Java设计模式：23种设计模式全面解析（超级详细）](http://c.biancheng.net/design_pattern/)~~，内容被清空了？
- [Java Design Patterns (中文)](https://java-design-patterns.com/zh/)，软件设计模式，编程原则还有代码片段
