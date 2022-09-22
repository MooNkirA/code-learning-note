# Drools 开源规则引擎

## 1. 业务规则问题的引出

现有一个在线申请信用卡的业务场景，用户需要录入个人信息，如下图所示：

![](images/341675715247412.png)

通过上图可以看到，用户录入的个人信息包括姓名、性别、年龄、学历、电话、所在公司、职位、月收入、是否有房、是否有车、是否有信用卡等。录入完成后点击申请按钮提交即可。用户提交申请后，需要在系统的服务端进行**用户信息合法性检查**(是否有资格申请信用卡)，只有通过合法性检查的用户才可以成功申请到信用卡(注意：不同用户有可能申请到的信用卡额度不同)。

检查用户信息合法性的规则如下：

| 规则编号 |           名称           |                                         描述                                          |
| :------: | ----------------------- | ------------------------------------------------------------------------------------ |
|    1     | 检查学历与薪水1           | 如果申请人既没房也没车，同时学历为大专以下，并且月薪少于5000，那么不通过                     |
|    2     | 检查学历与薪水2           | 如果申请人既没房也没车，同时学历为大专或本科，并且月薪少于3000，那么不通过                   |
|    3     | 检查学历与薪水3           | 如果申请人既没房也没车，同时学历为本科以上，并且月薪少于2000，同时之前没有信用卡的，那么不通过 |
|    4     | 检查申请人已有的信用卡数量 | 如果申请人现有的信用卡数量大于10，那么不通过                                              |

用户信息合法性检查通过后，还需要根据如下**信用卡发放规则**确定用户所办信用卡的额度：

| 规则编号 | 名称  |                                       描述                                       |
| :------: | ----- | ------------------------------------------------------------------------------- |
|    1     | 规则1 | 如果申请人有房有车，或者月收入在20000以上，那么发放的信用卡额度为15000                 |
|    2     | 规则2 | 如果申请人没房没车，但月收入在10000~20000之间，那么发放的信用卡额度为6000              |
|    3     | 规则3 | 如果申请人没房没车，月收入在10000以下，那么发放的信用卡额度为3000                      |
|    4     | 规则4 | 如果申请人有房没车或者没房但有车，月收入在10000以下，那么发放的信用卡额度为5000         |
|    5     | 规则5 | 如果申请人有房没车或者是没房但有车，月收入在10000~20000之间，那么发放的信用卡额度为8000 |

实现上面的业务逻辑，最简单就是使用分支判断(if-else)来实现，例如通过如下伪代码来检查用户信息合法性：

```java
// 检查用户信息合法性，返回true表示检查通过，返回false表示检查不通过
public boolean checkUser(User user){
    // 如果申请人既没房也没车，同时学历为大专以下，并且月薪少于5000，那么不通过
    if(user.getHouse() == null && user.getcar() == null 
       && user.getEducation().equals("大专以下") && user.getSalary < 5000){
        return false;
    }
    // 如果申请人既没房也没车，同时学历为大专或本科，并且月薪少于3000，那么不通过
    else if(user.getHouse() == null && user.getcar() == null && user.getEducation().equals("大专或本科") 
       && user.getSalary < 3000){
        return false;
    }
    // 如果申请人既没房也没车，同时学历为本科以上，并且月薪少于2000，同时之前没有信用卡的，那么不通过
    else if(user.getHouse() == null && user.getcar() == null && user.getEducation().equals("本科以上") 
       && user.getSalary < 2000 && user.getHasCreditCard() == false){
        return false;
    }
    // 如果申请人现有的信用卡数量大于10，那么不通过
    else if(user.getCreditCardCount() > 10){
        return false;
    }
    return true;
}
```

如果用户信息合法性检查通过后，还需要通过如下代码确定用户所办信用卡的额度：

```java
// 根据用户输入信息确定信用卡额度
public Integer determineCreditCardLimit(User user){
    // 如果申请人有房有车，或者月收入在20000以上，那么发放的信用卡额度为15000
    if((user.getHouse() != null && user.getcar() != null) || user.getSalary() > 20000){
        return 15000;
    }
    // 如果申请人没房没车，并且月收入在10000到20000之间，那么发放的信用卡额度为6000
    else if(user.getHouse() == null && user.getcar() == null
       && user.getSalary() > 10000 && user.getSalary() < 20000){
        return 6000;
    }
    // 如果申请人没房没车，并且月收入在10000以下，那么发放的信用卡额度为3000
    else if(user.getHouse() == null && user.getcar() == null
       && user.getSalary() < 10000){
        return 3000;
    }
    // 如果申请人有房没车或者没房但有车，并且月收入在10000以下，那么发放的信用卡额度为5000
    else if((((user.getHouse() != null && user.getcar() == null) || (user.getHouse() == null && user.getcar() != null))
       && user.getSalary() < 10000){
        return 5000;
    }
    // 如果申请人有房没车或者没房但有车，并且月收入在10000到20000之间，那么发放的信用卡额度为8000
    else if((((user.getHouse() != null && user.getcar() == null) || (user.getHouse() == null && user.getcar() != null))
       && (user.getSalary() > 10000 && user.getSalary() < 20000)){
        return 8000;
    }
}
```

通过上面的伪代码可以看到，通过Java代码的方式实现的业务规则检查，这种实现方式存在如下问题：

1. 硬编码实现业务规则难以维护
2. 硬编码实现业务规则难以应对变化
3. 业务规则发生变化需要修改代码，重启服务后才能生效

为了优化以上业务规则所存在的问题，需要使用“**规则引擎**”来实现上面的业务场景

## 2. 规则引擎概述

### 2.1. 什么是规则引擎

**规则引擎**，全称为**业务规则管理系统**，英文名为BRMS(即Business Rule Management System)。规则引擎的主要思想是将应用程序中的业务决策部分从应用程序代码中分离出来，并使用预定义的语义模块编写业务决策（业务规则），由用户或开发者在需要时进行配置、管理。**规则引擎其实就是一个输入输出平台**，接收数据输入，解释业务规则，并根据业务规则做出业务决策。

前面的申请信用卡业务场景使用规则引擎后效果如下：

![](images/141331516236636.png)

系统中引入规则引擎后，业务规则不再以程序代码的形式驻留在系统中，取而代之的是处理规则的规则引擎，业务规则存储在规则库中，完全独立于程序。业务人员可以像管理数据一样对业务规则进行管理，比如查询、添加、更新、统计、提交业务规则等。业务规则被加载到规则引擎中供应用系统调用。

> Notes: 规则引擎并不是一个具体的技术框架，而是指的一类系统，即业务规则管理系统。目前市面上具体的规则引擎产品有：drools、VisualRules、iLog 等。

### 2.2. 规则引擎的优势

使用规则引擎的优势如下：

1. 业务规则与系统代码分离，实现业务规则的集中管理
2. 在不重启服务的情况下可随时对业务规则进行扩展和维护
3. 可以动态修改业务规则，从而快速响应需求变更
4. 规则引擎是相对独立的，只关心业务规则，使得业务分析人员也可以参与编辑、维护系统的业务规则
5. 减少了硬编码业务规则的成本和风险
6. 使用规则引擎提供的规则编辑工具，使复杂的业务规则实现变得的简单

### 2.3. 规则引擎应用场景

对于一些存在比较复杂的业务规则并且业务规则会频繁变动的系统比较适合使用规则引擎，如下：

1. 风险控制系统：风险贷款、风险评估
2. 反欺诈项目：银行贷款、征信验证
3. 决策平台系统：财务计算
4. 促销平台系统：满减、打折、加价购

## 3. Drools 快速入门

### 3.1. Drools 简介

drools 是一款由 JBoss 组织提供的基于 Java 语言开发的开源规则引擎，可以将复杂且多变的业务规则从硬编码中解放出来，以规则脚本的形式存放在文件或特定的存储介质中(例如存放在数据库中)，使得业务规则的变更不需要修改项目代码、重启服务器就可以在线上环境立即生效。

- drools 官网地址：https://drools.org/
- drools 源码仓库：https://github.com/kiegroup/drools

### 3.2. Drools 的使用步骤

在项目中使用 drools 时，即可以单独使用也可以整合 spring 使用。如果单独使用只需要导入如下 maven 坐标即可：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.drools</groupId>
            <artifactId>drools-bom</artifactId>
            <type>pom</type>
            <version>7.10.0.Final</version>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-compiler</artifactId>
    <scope>runtime</scope>
</dependency>
```

*学习使用时，当时使用最新版本 7.73.0.Final，按默认的配置方式没有生效，后面需要排查原因*

> Tips: 如果使用 IDEA/eclipse 开发 drools 应用，需要单独安装 drools 插件。但早期的 IDEA 中会集成 drools 插件，较新版本需要自行安装
>
> ![](images/43681217250270.png)

### 3.3. drools API 开发步骤

![](images/65142916232390.png)

### 3.4. 入门案例

#### 3.4.1. 案例实现的业务场景说明

业务场景：消费者在图书商城购买图书，下单后需要根据以下的规则计算优惠后的价格，并在支付页面显示该价格。具体优惠规则如下：

| 规则编号 | 规则名称 |               描述               |
| :------: | -------- | -------------------------------- |
|    1     | 规则一   | 所购图书总价在100元以下的没有优惠  |
|    2     | 规则二   | 所购图书总价在100到200元的优惠20元 |
|    3     | 规则三   | 所购图书总价在200到300元的优惠50元 |
|    4     | 规则四   | 所购图书总价在300元以上的优惠100元 |

#### 3.4.2. 引入依赖

创建 maven 工程 drools-quickstart 并导入 drools 相关坐标

```xml
<!-- https://mvnrepository.com/artifact/org.drools/drools-compiler -->
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-compiler</artifactId>
    <version>7.73.0.Final</version>
</dependency>
```

#### 3.4.3. drools 默认配置

根据 drools 要求，在 resources/META-INF 目录中，创建 kmodule.xml 配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<kmodule xmlns="http://www.drools.org/xsd/kmodule">
    <!--
        name 属性: 指定kbase的名称，任意命名，但必须保证唯一
        packages 属性: 指定规则文件的目录，需要根据实际情况填写，否则无法加载到规则文件
        default 属性: 指定当前kbase是否为默认
    -->
    <kbase name="myKbase1" packages="rules" default="true">
        <!--
            name 属性: 指定ksession名称，任意命名，但必须保证唯一
            default 属性: 指定当前session是否为默认
        -->
        <ksession name="ksession-rule" default="true"/>
    </kbase>
</kmodule>
```

> Notes: <font color=red>**drools 默认配置文件的名称和位置都是固定写法，不能更改**</font>

#### 3.4.4. 创建数据输入输出的实体类

创建实体类Order，用于 drools 接收数据输入与解释业务规则后返回

```java
package com.moon.drools.entity;

public class Order {
    private Double originalPrice; // 订单原始价格，即优惠前价格
    private Double realPrice; // 订单真实价格，即优惠后价格

    public String toString() {
        return "Order{" +
                "originalPrice=" + originalPrice +
                ", realPrice=" + realPrice +
                '}';
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(Double realPrice) {
        this.realPrice = realPrice;
    }
}
```

#### 3.4.5. 创建规则文件

在 resources/rules 目录中，创建规则文件 bookDiscount.drl（任意命名）

```java
// 图书优惠规则
package book.discount // 定义包（逻辑，非真实）
import com.moon.drools.entity.Order // 导入实体类

/*
    这是一个多行注释
*/
// 规则一：所购图书总价在100元以下的没有优惠
rule "book_discount_1"
    when
        $order:Order(originalPrice < 100)
    then
        $order.setRealPrice($order.getOriginalPrice()); // 模式匹配，到规则引擎中（工作内存）查找Order对象
        System.out.println("成功匹配到规则一：所购图书总价在100元以下的没有优惠");
end

// 规则二：所购图书总价在100到200元的优惠20元
rule "book_discount_2"
    when
        $order:Order(originalPrice < 200 && originalPrice >= 100)
    then
        $order.setRealPrice($order.getOriginalPrice() - 20);
        System.out.println("成功匹配到规则二：所购图书总价在100到200元的优惠20元");
end

// 规则三：所购图书总价在200到300元的优惠50元
rule "book_discount_3"
    when
        $order:Order(originalPrice <= 300 && originalPrice >= 200)
    then
        $order.setRealPrice($order.getOriginalPrice() - 50);
        System.out.println("成功匹配到规则三：所购图书总价在200到300元的优惠50元");
end

// 规则四：所购图书总价在300元以上的优惠100元
rule "book_discount_4"
    when
        $order:Order(originalPrice >= 300)
    then
        $order.setRealPrice($order.getOriginalPrice() - 100);
        System.out.println("成功匹配到规则四：所购图书总价在300元以上的优惠100元");
end
```

> Tips: 安装 drools 插件后，文件的图标会改变，输入内容时会有提示信息
>
> ![](images/49980423220953.png)

#### 3.4.6. 测试

编写单元测试

```java
@Test
public void test1() {
    // 获取 KieServices
    KieServices kieServices = KieServices.Factory.get();
    // 获得 KieContainer（容器）对象
    KieContainer kieContainer = kieServices.newKieClasspathContainer();
    // 从 KieContainer（容器）对象中获取会话对象，用于和规则引擎交互
    KieSession session = kieContainer.newKieSession();

    // 构造订单对象（Fact对象，事实对象），设置原始价格，由规则引擎根据优惠规则计算优惠后的价格
    Order order = new Order();
    order.setOriginalPrice(500d);

    // 将数据提供给规则引擎（放入工作内存中），规则引擎会根据提供的数据进行规则匹配
    session.insert(order);
    System.out.println("没有激活规则前，优惠价格属性是：" + order.getRealPrice());

    // 激活规则，由Drools框架自动进行规则匹配，如果规则匹配成功，则执行当前规则
    session.fireAllRules();

    // 关闭会话
    session.dispose();
    System.out.println("优惠后价格：" + order.getRealPrice());
}
```

输出结果

```
没有激活规则前，优惠价格属性是：null
成功匹配到规则四：所购图书总价在300元以上的优惠100元
优惠后价格：400.0
```

#### 3.4.7. 小结

通过上面的入门案例可以发现，使用 drools 规则引擎主要工作就是编写规则文件，在规则文件中定义相关的业务规则（例如入门案例定义的 就是图书优惠规则）。规则定义好后就需要调用drools 提供的 API 将数据提供给规则引擎进行规则模式匹配，规则引擎会执行匹配成功的规则计算的结果并将返回

使用 drools 框架后，不需要在代码中编写规则的判断逻辑，而是在规则文件中编写了业务规则。使用规则引擎时业务规则可以做到动态管理。业务人员可以像管理数据一样对业务规则进行管理，比如查询、添加、更新、统计、提交业务规则等。这样就可以做到在不重启服务的情况下调整业务规则。

## 4. Drools 介绍

### 4.1. 规则引擎构成

drools 规则引擎由以下三部分构成：

- Working Memory（工作内存）
- Rule Base（规则库）
- Inference Engine（推理引擎）

其中 Inference Engine（推理引擎）又包括：

- Pattern Matcher（匹配器）
- Agenda（议程）
- Execution Engine（执行引擎）

如下图所示：

![](images/156540523239379.png)

### 4.2. 相关概念说明

- **Working Memory**：工作内存，drools 规则引擎会从 Working Memory 中获取数据并和规则文件中定义的规则进行模式匹配，所以我们开发的应用程序只需要将我们的数据插入到 Working Memory 中即可，例如本案例中调用 `kieSession.insert(order)` 就是将 order 对象插入到了工作内存中。
- **Fact**：事实，是指在 drools 规则应用当中，将一个普通的 JavaBean 插入到 Working Memory 后的对象就是 Fact 对象，例如本案例中的 Order 对象就属于 Fact 对象。Fact 对象是应用和规则引擎进行数据交互的桥梁或通道。
- **Rule Base**：规则库，我们在规则文件中定义的规则都会被加载到规则库中。
- **Pattern Matcher**：匹配器，将 Rule Base 中的所有规则与 Working Memory 中的 Fact 对象进行模式匹配，匹配成功的规则将被激活并放入 Agenda 中。
- **Agenda**：议程，用于存放通过匹配器进行模式匹配后被激活的规则。
- **Execution Engine**：执行引擎，执行 Agenda 中被激活的规则。

### 4.3. 规则引擎执行过程

![](images/37321023227246.png)

### 4.4. KIE 介绍

在操作 Drools 时经常使用的 API 以及它们之间的关系如下图：

![](images/389241208220959.png)

通过上面的核心 API 可以发现，大部分类名都是以 Kie 开头。Kie 全称为 Knowledge Is Everything，即"知识就是一切"的缩写，是 Jboss 一系列项目的总称。如下图所示，Kie 的主要模块有 OptaPlanner、Drools、UberFire、jBPM。

![](images/452491408239385.png)

通过上图可以看到，Drools 是整个 KIE 项目中的一个组件，Drools 中还包括一个 Drools-WB 的模块，它是一个可视化的规则编辑器。

## 5. Drools 基础语法

### 5.1. 规则文件构成

在使用 Drools 时非常重要的一个工作就是编写规则文件，通常规则文件的后缀为 `.drl`。drl 是 Drools Rule Language 的缩写。在规则文件中编写具体的规则内容。

一套完整的规则文件内容构成如下：

|  关键字   |                            描述                            |
| :------: | --------------------------------------------------------- |
| package  | 包名，只限于逻辑上的管理，同一个包名下的查询或者函数可以直接调用 |
|  import  | 用于导入类或者静态方法                                       |
|  global  | 全局变量                                                   |
| function | 自定义函数                                                 |
|  query   | 查询                                                       |
| rule end | 规则体                                                     |

<font color=purple>**Drools 支持的规则文件，除了 drl 形式，还有 Excel 文件类型的。**</font>

### 5.2. 规则体语法结构

规则体是规则文件内容中的重要组成部分，是进行业务规则判断、处理业务结果的部分。规则体语法结构如下：

```drl
rule "ruleName"
    attributes
    when
        LHS
    then
        RHS
end
```

**关键字与名词解释**：

- `rule`：关键字，表示规则开始，参数为规则的唯一名称。
- `attributes`：规则属性，是rule与when之间的参数，为可选项。
- `when`：关键字，后面跟规则的条件部分。
- `LHS`(Left Hand Side)：是规则的条件部分的通用名称。它由零个或多个条件元素组成。如果LHS为空，则它将被视为始终为true的条件元素。
- `then`：关键字，后面跟规则的结果部分。
- `RHS`(Right Hand Side)：是规则的后果或行动部分的通用名称。
- `end`：关键字，表示一个规则结束

### 5.3. 注释

在 drl 形式的规则文件中使用注释和 Java 类中使用注释一致，分为单行注释和多行注释。

单行注释用"`//`"进行标记，多行注释以"`/*`"开始，以"`*/`"结束。如下示例：

```drl
// 规则rule1的注释，这是一个单行注释
rule "rule1"
    when
    then
        System.out.println("rule1触发");
end
​
/*
  规则rule2的注释，
  这是一个多行注释
*/
rule "rule2"
    when
    then
        System.out.println("rule2触发");
end
```

### 5.4. Pattern 模式匹配

Drools 中的匹配器可以将 Rule Base 中的所有规则与 Working Memory 中的 Fact 对象进行模式匹配，那么就需要在规则体的 LHS 部分定义规则并进行模式匹配。LHS 部分由一个或者多个条件组成，条件又称为 pattern。

pattern 的语法结构为：`绑定变量名:Object(Field约束)`

其中绑定变量名可以省略，通常绑定变量名的命名一般建议以`$`开始。如果定义了绑定变量名，就可以在规则体的 RHS 部分使用此绑定变量名来操作相应的 Fact 对象。Field 约束部分是需要返回 true 或者 false 的0个或多个表达式。以快速入门为案例来说明：

```drl
//规则二：所购图书总价在100到200元的优惠20元
rule "book_discount_2"
    when
        //Order为类型约束，originalPrice为属性约束
        $order:Order(originalPrice < 200 && originalPrice >= 100)
    then
        $order.setRealPrice($order.getOriginalPrice() - 20);
        System.out.println("成功匹配到规则二：所购图书总价在100到200元的优惠20元");
end
```

案例必须同时满足当前规则才有可能被激活，匹配条件为：

1. 工作内存中必须存在 Order 这种类型的 Fact 对象 - 类型约束
2. Fact 对象的 originalPrice 属性值必须小于 200 - 属性约束
3. Fact 对象的 originalPrice 属性值必须大于等于 100 - 属性约束

**绑定变量既可以用在对象上，也可以用在对象的属性上**。例如：

```drl
rule "book_discount_2"
    when
        $order:Order($op:originalPrice < 200 && originalPrice >= 100)
    then
        System.out.println("$op=" + $op);
        $order.setRealPrice($order.getOriginalPrice() - 20);
        System.out.println("成功匹配到规则二：所购图书总价在100到200元的优惠20元");
end
```

LHS 部分还可以**定义多个 pattern，之间可以使用 and 或者 or 进行连接，也可以不写，默认连接为 and**。例如：

```drl
rule "book_discount_2"
    when
        $order:Order($op:originalPrice < 200 && originalPrice >= 100) and
        $customer:Customer(age > 20 && gender=='male')
    then
        System.out.println("$op=" + $op);
        $order.setRealPrice($order.getOriginalPrice() - 20);
        System.out.println("成功匹配到规则二：所购图书总价在100到200元的优惠20元");
end
```

### 5.5. 比较操作符

Drools 提供的比较操作符，如下表：

|     符号     |                            说明                             |
| :----------: | ---------------------------------------------------------- |
|     `>`      | 大于                                                        |
|     `<`      | 小于                                                        |
|     `>=`     | 大于等于                                                    |
|     `<=`     | 小于等于                                                    |
|     `==`     | 等于                                                        |
|     `!=`     | 不等于                                                      |
|   contains   | 检查一个Fact对象的某个属性值是否包含一个指定的对象值             |
| not contains | 检查一个Fact对象的某个属性值是否不包含一个指定的对象值           |
|   memberOf   | 判断一个Fact对象的某个属性是否在一个或多个集合中                |
| not memberOf | 判断一个Fact对象的某个属性是否不在一个或多个集合中              |
|   matches    | 判断一个Fact对象的属性是否与提供的标准的Java正则表达式进行匹配   |
| not matches  | 判断一个Fact对象的属性是否不与提供的标准的Java正则表达式进行匹配 |

#### 5.5.1. 语法

- contains | not contains 语法结构

```java
Object(Field[Collection/Array] contains value)
Object(Field[Collection/Array] not contains value)
```

- memberOf | not memberOf 语法结构

```java
Object(field memberOf value[Collection/Array])
Object(field not memberOf value[Collection/Array])
```

- matches | not matches 语法结构

```java
Object(field matches "正则表达式")
Object(field not matches "正则表达式")
```

#### 5.5.2. 使用示例

- 创建用于测试比较操作符的实体类

```java
public class ComparisonOperatorEntity {
    private String names;
    private List<String> list;
    // ...省略getter/setter
}
```

- 在 /resources/rules 目录中，创建比较操作符示例使用的规则文件 comparisonOperator.drl

```drl
package comparisonOperator
import com.moon.drools.entity.ComparisonOperatorEntity

// 当前规则文件用于测试Drools提供的比较操作符
// 测试比较操作符contains
rule "rule_comparison_contains"
    when
        ComparisonOperatorEntity(names contains "张三")
        ComparisonOperatorEntity(list contains names)
    then
        System.out.println("规则：rule_comparison_contains触发了...");
end

// 测试比较操作符not contains
rule "rule_comparison_notcontains"
    when
        ComparisonOperatorEntity(names not contains "张三")
        ComparisonOperatorEntity(list not contains names)
    then
        System.out.println("规则：rule_comparison_notcontains触发了...");
end

// 测试比较操作符memberOf
rule "rule_comparison_memberOf"
    when
        ComparisonOperatorEntity(names memberOf list)
    then
        System.out.println("规则：rule_comparison_memberOf触发了...");
end

// 测试比较操作符 not memberOf
rule "rule_comparison_not memberOf"
    when
        ComparisonOperatorEntity(names not memberOf list)
    then
        System.out.println("规则：rule_comparison_not memberOf触发了...");
end

// 测试比较操作符 matches
rule "rule_comparison_matches"
    when
        ComparisonOperatorEntity(names matches "王.*")
    then
        System.out.println("规则：rule_comparison_matches触发了...");
end

// 测试比较操作符 not matches
rule "rule_comparison_not matches"
    when
        ComparisonOperatorEntity(names not matches "王.*")
    then
        System.out.println("规则：rule_comparison_not matches触发了...");
end
```

- 编写单元测试

```java
@Test
public void test2() {
    // 获取 KieServices
    KieServices kieServices = KieServices.Factory.get();
    // 获得 KieContainer（容器）对象
    KieContainer kieContainer = kieServices.newKieClasspathContainer();
    // 从 KieContainer（容器）对象中获取会话对象，用于和规则引擎交互
    KieSession session = kieContainer.newKieSession();

    // 构造订单对象（Fact对象，事实对象），设置原始价格，由规则引擎根据优惠规则计算优惠后的价格
    ComparisonOperatorEntity fact = new ComparisonOperatorEntity();
    fact.setNames("李四");
    List<String> list = new ArrayList<>();
    list.add("张三2");
    fact.setList(list);

    // 将数据提供给规则引擎（放入工作内存中），规则引擎会根据提供的数据进行规则匹配
    session.insert(fact);

    // 激活规则，由Drools框架自动进行规则匹配，如果规则匹配成功，则执行当前规则
    session.fireAllRules();

    // 关闭会话
    session.dispose();
}
```




