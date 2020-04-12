# Dubbo 笔记

## 1. RPC 场景和过程
### 1.1. RPC场景

在微服务环境下，会存在大量的跨 JVM （不同服务器之间）进行方法调用的场景，服务结构如下图：

![分布式服务结构](images/20200122110030436_24328.png)

具体到一次调用过程来说， A 机器能通过网络，调用 B 机器内的某个服务方法，并得到返回值

![服务调用](images/20200122134326828_23754.png)


### 1.2. RPC 的逐步实现过程
#### 1.2.1. 服务接口的本地调用

从本质上来讲，某个 JVM 内的对象方法，是无法在 JVM 外部被调用的，如下例：

```java
public interface OrderService {
    /**
     * 使用反射调用时，需要明确目标对象，方法，参数
     */
    OrderEntiry getDetail(String id);
}

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public OrderEntiry getDetail(String id) {
        LOGGER.info("{}被调用一次：{}", super.getClass().getName(), System.currentTimeMillis());

        OrderEntiry orderEntiry = new OrderEntiry();
        orderEntiry.setId("O0001");
        orderEntiry.setMoney(1000);
        orderEntiry.setUserId("U0001");

        return orderEntiry;
    }
}
```

在本地调用接口

```java
@SpringBootApplication
public class ProviderApplication {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderApplication.class);

    public static void main(String[] args) {
        // 启动容器
        ConfigurableApplicationContext context = SpringApplication.run(ProviderApplication.class, args);

        LOGGER.info("---------spring启动成功--------");

        /* 本地调用接口 */
        LOGGER.info("============ 调用本地接口 ==============");
        OrderService orderService = context.getBean(OrderService.class); // 根据接口类型获取实例
        OrderEntiry entiry = orderService.getDetail("1");
        LOGGER.info("测试orderService.getDetail调用功能，调用结果：{}", JSON.toJSONString(entiry));
    }
}
```

> 注：以上示例`orderService.getDetail("1")`的这一行程序调用，是无法脱离本地 jvm 环境被调用的。

#### 1.2.2. RPC 的实现切入口 - 反射调用

- 为了解决不同 JVM 环境（不同服务器）之间的 JAVA 对象方法的调用，可以使用反射模式来解决
- 可以设想，只要通过网络传输，传入反射需要的目标对象(如：OrderService)、方法名称（如：getDetail）、方法的参数值（如："1"），这样就可以告知另一个JVM环境（服务器）需要调用反射的信息（target/method/arg），通过反射将 orderService.getDetail 调用起来

封装反射工具类

```java
package com.moon.utils;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 反射调用方法工具类
 */
public class InvokeUtils {

    /**
     * java反射
     */
    public static Object call(Object target, String methodName, Class[] argTypes, Object[] args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = target.getClass().getMethod(methodName, argTypes);
        // 反射调用方法
        return method.invoke(target, args);
    }

    /* 重载方法 */
    public static Object call(Map<String, String> info, ApplicationContext context) {
        String targetStr = info.get("target"); // 获取需要调用的接口全限定名
        String methodName = info.get("methodName"); // 获取调用的方法名
        String arg = info.get("arg"); // 获取调用方法的参数值

        try {
            // context.getBean(Class.forName(targetStr)) 此方法根据接口的类型，获取spring容器中对应的实现类实例
            return call(context.getBean(Class.forName(targetStr)), methodName, new Class[]{String.class}, new Object[]{arg});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

通过反射调用方法

```java
/* 通过反射调用本地接口 */
LOGGER.info("============ 反射调用本地接口 ==============");
Map<String, String> info = new HashMap<>();
info.put("target", "com.moon.service.OrderService");
info.put("methodName", "getDetail");
info.put("arg", "1");
Object result = InvokeUtils.call(info, context);
LOGGER.info("测试InvokeUtils.call调用功能，调用结果：{}", JSON.toJSONString(result));
```

#### 1.2.3. 网络通信传递反射信息

- 在上一节的步骤中，可以知道，只要传递 target/method/arg等三要素，就可以执行想要的目标服务方法，所以目前只需要解决target/method/arg 三种信息的网络传输问题即可
- 网络通信的方法很多，如 http/rmi/webservice 等等。本次示例选用 JDK 的 rmi 方式，其使用方式如下

##### 1.2.3.1. 定义一个接口继承自remote接口

- 创建InfoService接口

```java
package com.moon.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * 基础RPC测试网络传输远程请求接口，继承remote接口
 */
public interface InfoService extends Remote {

    /* 网络传输url */
    String RMI_URL = "rmi://127.0.0.1:9080/InfoService";
    /* 端口号 */
    int port = 9080;

    Object sayHello(String name) throws RemoteException;

    /**
     * 传输信息方法
     *
     * @param info 需要调用的方法的反射信息
     * @return 远程方法执行的结果
     * @throws RemoteException
     */
    Object passInfo(Map<String, String> info) throws RemoteException;
}
```

- 创建一个实现类（为简化实现，继承 UnicastRemoteObject 类）

```java
package com.moon.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * 网络传输远程请求接口实现
 */
public class InfoServiceImpl extends UnicastRemoteObject implements InfoService {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoServiceImpl.class);

    public InfoServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Object sayHello(String name) throws RemoteException {
        return name + "，成功调用InfoService.sayHello()方法";
    }

    @Override
    public Object passInfo(Map<String, String> info) throws RemoteException {
        LOGGER.info("成功调通了InfoService.passInfo()方法，参数：{}", JSON.toJSONString(info));
        // 增加返回的信息
        info.put("msg", "你好，调通了！");
        return info;
    }
}
```

##### 1.2.3.2. 服务提供方通过 RMI 开放服务到指定 URL

只需要将实例绑定注册到指定的 `URL` 和 `port` 上，远程即可调用此实例

```java
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException {
        // ignore some code...
        /* 3. 通过rmi网络通信进行调用接口 */
        initProtocol();
    }

    /**
     * 初始化RmiServer，将Rmi实例绑定注册
     */
    private static void initProtocol() throws RemoteException, AlreadyBoundException, MalformedURLException {
        InfoService infoService = new InfoServiceImpl();
        // 注冊通讯端口
        LocateRegistry.createRegistry(InfoService.port);
        // 注冊通讯路径
        Naming.bind(InfoService.RMI_URL, infoService);
        LOGGER.info("初始化RMI绑定");
    }
}
```

##### 1.2.3.3. 消费端通过 RMI 远程 URL 连接并调用

```java
package com.moon;

import com.alibaba.fastjson.JSON;
import com.moon.service.InfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务消费者
 */
@SpringBootApplication
public class ConsumerApplication {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // 启动容器
        SpringApplication.run(ConsumerApplication.class, args);

        LOGGER.info("---------spring启动成功--------");

        /* 如果不是远程调用其他服务的接口，想实现功能，只能编写与服务提供方一模一样的代码，一样的接口实现类 */
        /*LOGGER.info("============ 调用本地接口 ==============");
        OrderService orderService = context.getBean(OrderService.class);
        OrderEntiry entiry = orderService.getDetail("1");
        LOGGER.info("测试orderService.getDetail调用功能，调用结果：{}", JSON.toJSONString(entiry));*/

        /* 1. 测试远程服务调用实现 */
        // 获取远程服务实现
        InfoService infoService = (InfoService) Naming.lookup(InfoService.RMI_URL);
        Object ret = infoService.sayHello("MooNzerO");
        LOGGER.info("测试远程调用功能InfoService.sayHello()，调用结果：{}", JSON.toJSONString(ret));

        /* 2. 测试远程服务反射调用实现 */
        // 封装需要网络传输的反射调用信息
        Map<String, String> info = new HashMap<>();
        info.put("target", "com.moon.service.OrderService");
        info.put("methodName", "getDetail");
        info.put("arg", "1");
        // 调用传输数据方法
        Object result = infoService.passInfo(info);
        LOGGER.info("测试远程调用功能InfoService.passInfo()，调用结果：{}", JSON.toJSONString(result));
    }
}
```

> 至此，可以实现了通过RMI跨机器传递需要调用的反射信息（target/method/arg）

#### 1.2.4. 远程调用的融合

经过前两步的实践，已经实现了跨机器的反射信息收发和反射动作调用。现在只需要在InfoService的实现上，对传递过来的info信息，直接发起反射调用即可

```java
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException {
        // ignore some code...
        /* 4. 改造，初始化RmiServer时，重写InfoService.passInfo()方法，增加获取网络传输的反射信息后，调用相应方法的逻辑 */
        initProtocol2(context);
    }

    /**
     * 获取反射信息后调用相应的方法
     *
     * @param context spring 上下文容器
     */
    private static void initProtocol2(ApplicationContext context) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // 创建InfoService实现
        InfoService infoService = new InfoServiceImpl() {
            // 重写passInfo方法
            @Override
            public Object passInfo(Map<String, String> info) throws RemoteException {
                // 调用父类方法，info包含对象，方法，参数等反射需要的信息
                super.passInfo(info);

                // 使用反射进去调用
                Object result = InvokeUtils.call(info, context);
                LOGGER.info("测试调用passInfo()获取反射信息，并反射调用相应方法。调用结果：{}", JSON.toJSONString(result));
                return result;
            }
        };

        // 注冊通讯端口
        LocateRegistry.createRegistry(InfoService.port);
        // 注冊通讯路径
        Naming.bind(InfoService.RMI_URL, infoService);
        LOGGER.info("初始化RMI绑定");
    }
}
```

现在远程机器只要通过infoService传递信息过来，就自动将目标服务反射调用，并返回结果值回去，整个RPC过程完成

#### 1.2.5. 对客户端友好的透明化封装

经过前面的测试示例，RPC的整个调用链条已经拉通，但是还有一个易出错的地方，就是客户端封装反射信息的地方，功能不够内聚，容易出现错误，代码易读性也很差

```java
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // ignore some code...
        /* 2. 测试远程服务反射调用实现 */
        // 封装需要网络传输的反射调用信息
        Map<String, String> info = new HashMap<>();
        info.put("target", "com.moon.service.OrderService");
        info.put("methodName", "getDetail");
        info.put("arg", "1");
        // 调用传输数据方法
        Object result = infoService.passInfo(info);
        LOGGER.info("测试远程调用功能InfoService.passInfo()，调用结果：{}", JSON.toJSONString(result));
    }
}
```

改造思路：其实反射需要的target/method/arg这三个信息，全部都可以从接口方法调用`OrderService.getDetail("1")`中得到，所以可以为此接口做一个**静态代理对象**，在代理对象内部完成反射信息的包装

```java
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // ignore some code...
        /* 3. 优化改造，使用静态代理封装参数准备的过程 */
        OrderService orderService = getService(infoService);
        // 透明化调用，不需要调用者去关心调用的准备工作
        OrderEntiry entiry = orderService.getDetail("1");
        LOGGER.info("测试静态代理远程调用InfoService.passInfo()方法，调用结果：{}", JSON.toJSONString(entiry));
    }

    /**
     * 静态代理
     *
     * @param infoService 网络传输类
     * @return 返回OrderService代理示例
     */
    private static OrderService getService(InfoService infoService) {
        // 返回接口实现
        return new OrderService() {
            @Override
            public OrderEntiry getDetail(String id) {
                Map<String, String> info = new HashMap<>();
                // 因为知道反射的目标与参数，使用静态代理直接封装相应参数
                info.put("target", "com.moon.service.OrderService"); // 调用对象
                info.put("methodName", "getDetail"); // 调用方法
                info.put("arg", id); // 调用参数

                OrderEntiry result = null;
                try {
                    result = (OrderEntiry) infoService.passInfo(info);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return result;
            }
        };
    }
}
```

优化后，客户端远程传递反射信息的过程，直接变成调用接口的代理对象即可。调用者，甚至不再需要区分，此接口代理对象到底是谁，像调用正常的本地服务一样使用即可

## 2. Dubbo简介

- Apache Dubbo™ 是一款高性能Java RPC框架
- 官网：http://dubbo.apache.org/zh-cn/index.html

### 2.1. Dubbo 实现RPC框架

分布式服务架构下，各个服务间的相互RPC调用会越来越复杂。最终形成网状结构，此时服务的治理极为关键

Dubbo是一个带有服务治理功能的RPC框架，提供了一套较为完整的服务治理方案，其底层直接实现了RPC调用的全过程，并尽力使RPC远程对使用者透明。下图展示了Dubbo服务治理的功能

![dubbo-service-governance](images/20200125181329487_3748.jpg)

简单的说，Dubbo本质上就是个服务调用的框架，如果没有分布式的需求，其实是不需要用的，只有在分布式的时候，才有使用Dubbo这样的分布式服务框架的需求。其核心部分包含：

- 远程通讯：提供对多种基于长连接的NIO框架抽象封装，包括多种线程模型、序列化以及“请求-响应”模式的信息交换方式。
- 集群容错：提供基于接口方法的透明远程过程调用，包括多协议支持以及软负载均衡，失败容错、地址路由、动态配置等集群支持。
- 自动发现：基于注册中心目录服务，使服务消费方能动态的查找到×××提供方，使地址透明，使服务提供方可以平滑增加或减少机器。

### 2.2. Dubbo 的架构及特点

dubbo 的整体结构如下图：

![Dubbo架构设计图](images/20200125183026192_30674.png)

> 参考来源：http://dubbo.apache.org/zh-cn/docs/dev/design.html

Dubbo总体架构设计一共划分了10层，而最上面的Service层是留给实际想要使用Dubbo开发分布式服务的开发者实现业务逻辑的接口层。图中左边淡蓝背景的为服务消费方使用的接口，右边淡绿色背景的为服务提供方使用的接口，位于中轴线上的为双方都用到的接口

- 服务接口层(Service)：该层是与实际业务逻辑相关的，根据服务提供方和服务消费方的业务设计对应的接口和实现。
- 配置层(Config)：对外配置接口，以ServiceConfig和ReferenceConfig为中心，可以直接new配置类，也可以通过Spring解析配置生成配置类。
- 服务代理层(Proxy)：服务接口透明代理，生成服务的客户端Stub和服务器端Skeleton，以ServiceProxy为中心，扩展接口为ProxyFactory。
- 服务注册层(Registry)：封装服务地址的注册与发现，以服务URL为中心，扩展接口为RegistryFactory、Registry和RegistryService。可能没有服务注册中心，此时服务提供方直接暴露服务。
- 集群层(Cluster)：封装多个提供者的路由及负载均衡，并桥接注册中心，以Invoker为中心，扩展接口为Cluster、Directory、Router和LoadBalance。将多个服务提供方组合为一个服务提供方，实现对服务消费方透明，只需要与一个服务提供方进行交互。
- 监控层(Monitor)：RPC调用次数和调用时间监控，以Statistics为中心，扩展接口为MonitorFactory、Monitor和MonitorService。
- 远程调用层(Protocol)：封将RPC调用，以Invocation和Result为中心，扩展接口为Protocol、Invoker和Exporter。Protocol是服务域，它是Invoker暴露和引用的主功能入口，它负责Invoker的生命周期管理。Invoker是实体域，它是Dubbo的核心模型，其他模型都向它靠扰，或转换成它，它代表一个可执行体，可向它发起invoke调用。它有可能是一个本地的实现，也可能是一个远程的实现，也可能是一个集群实现。
- 信息交换层(Exchange)：封装请求响应模式，同步转异步，以Request和Response为中心，扩展接口为Exchanger、ExchangeChannel、ExchangeClient和ExchangeServer。
- 网络传输层(Transport)：抽象mina和netty为统一接口，以Message为中心，扩展接口为Channel、Transporter、Client、Server和Codec。
- 数据序列化层(Serialize)：可复用的一些工具，扩展接口为Serialization、 ObjectInput、ObjectOutput和ThreadPool。

从上图可以看出，Dubbo对于服务提供方和服务消费方，从框架的10层中分别提供了各自需要关心和扩展的接口，构建整个服务生态系统(服务提供方和服务消费方本身就是一个以服务为中心的)

### 2.3. Dubbo 服务的角色关系

服务提供方和服务消费方之间的调用关系，如图所示：

![提供方和消费方的调用关系](images/20200125184203559_27446.png)

- 节点角色说明

|   节点    |              角色说明               |
| --------- | ---------------------------------- |
| Provider  | 暴露服务的服务提供方                 |
| Consumer  | 调用远程服务的服务消费方             |
| Registry  | 服务注册与发现的注册中心             |
| Monitor   | 统计服务的调用次数和调用时间的监控中心 |
| Container | 服务运行容器                        |

- 调用关系说明
    1. 服务容器负责启动，加载，运行服务提供者。
    2. 服务提供者在启动时，向注册中心注册自己提供的服务。
    3. 服务消费者在启动时，向注册中心订阅自己所需的服务。
    4. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
    5. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
    6. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心

## 3. Dubbo 基础配置使用

### 3.1. xml 文件配置方式
#### 3.1.1. dubbo 功能标签集

![Dubbo标签关系图](images/20200126122444209_30129.jpg)

> 1. 标签属性有继承关系，即：下层有设置则使用，未配置则沿用上一级的设置
> 2. timeout/retries/loadbalance消费方未设置，则沿用服务方的设置

**Dubbo 各标签作用**

- `<dubbo:service/>` 服务配置，用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心。
- `<dubbo:reference/>` 引用配置，用于创建一个远程服务代理，一个引用可以指向多个注册中心。
- `<dubbo:protocol/>` 协议配置，用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受。
- `<dubbo:application/>` 应用配置，用于配置当前应用信息，不管该应用是提供者还是消费者。
- `<dubbo:registry/>` 注册中心配置，用于配置连接注册中心相关信息。
- `<dubbo:module/>` 模块配置，用于配置当前模块信息，可选。
- `<dubbo:monitor/>` 监控中心配置，用于配置连接监控中心相关信息，可选。
- `<dubbo:provider/>` 提供方的缺省值，当ProtocolConfig和ServiceConfig某属性没有配置时，采用此缺省值，可选。
- `<dubbo:consumer/>` 消费方缺省配置，当ReferenceConfig某属性没有配置时，采用此缺省值，可选。
- `<dubbo:method/>` 方法配置，用于ServiceConfig和ReferenceConfig指定方法级的配置信息。
- `<dubbo:argument/>` 用于指定方法参数配置。

#### 3.1.2. 标签详解

所有配置项分为三大类，参见以下各个标签作用表中的"作用"一列

- 服务发现：表示该配置项用于服务的注册与发现，目的是让消费方找到提供方
- 服务治理：表示该配置项用于治理服务间的关系，或为开发测试提供便利条件
- 性能调优：表示该配置项用于调优性能，不同的选项对性能会产生影响
- 所有配置最终都将转换为URL表示，并由服务提供方生成，经注册中心传递给消费方，各属性对应URL的参数，参见配置项一览表中的"对应URL参数"列。

> URL 格式：`protocol://username:password@host:port/path?key=value&key=value`

**注意：只有group，interface，version是服务的匹配条件，三者决定是不是同一个服务，其它配置项均为调优和治理参数。**

> **各个标签详细属性配置参考官网：http://dubbo.apache.org/zh-cn/docs/user/references/xml/introduction.html**

##### 3.1.2.1. dubbo:service【常用】

服务提供者暴露服务配置。对应的配置类：`org.apache.dubbo.config.ServiceConfig`。常用属性如下：

|   属性    | 对应URL参数 |  类型  | 是否必填 | 缺省值 |   作用   |                                        描述                                         |    兼容性     |
| --------- | ---------- | ------ | ------- | ----- | ------- | ---------------------------------------------------------------------------------- | ------------ |
| interface |            | class  | **必填**     |       | 服务发现 | 服务接口名                                                                          | 1.0.0以上版本 |
| ref       |            | object | **必填**     |       | 服务发现 | 服务对象实现引用                                                                     | 1.0.0以上版本 |
| version   | version    | string | 可选     | 0.0.0 | 服务发现 | 服务版本，建议使用两位数字版本，如：1.0，通常在接口不兼容时版本号才需要升级                | 1.0.0以上版本 |
| rotocol   |            | string | 可选     |       | 配置关联 | 使用指定的协议暴露服务，在多协议时使用，值为<dubbo:protocol>的id属性，多个协议ID用逗号分隔 | 2.0.5以上版本 |

##### 3.1.2.2. dubbo:reference【常用】

服务消费者引用服务配置。对应的配置类：`org.apache.dubbo.config.ReferenceConfig`。常用属性如下：

|   属性    | 对应URL参数 |  类型  | 是否必填 | 缺省值 |   作用   |      描述      |    兼容性     |
| --------- | ----------- | ------ | ------- | ----- | ------- | ------------- | ------------ |
| id        |             | string | **必填**     |       | 配置关联 | 服务引用BeanId | 1.0.0以上版本 |
| interface |             | class  | **必填**     |       | 服务发现 | 服务接口名     | 1.0.0以上版本 |

##### 3.1.2.3. dubbo:protocol【常用】

服务提供者协议配置。对应的配置类：`org.apache.dubbo.config.ProtocolConfig`。同时，如果需要支持多协议，可以声明多个 `<dubbo:protocol>` 标签，并在 `<dubbo:service>` 中通过 protocol 属性指定使用的协议。常用属性如下：

| 属性 |  对应URL参数  |  类型  | 是否必填 |                                                                                                       缺省值                                                                                                       |   作用   |                                                     描述                                                     |    兼容性     |
| ---- | ------------ | ------ | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------- | ----------------------------------------------------------------------------------------------------------- | ------------ |
| id   |              | string | 可选     | dubbo                                                                                                                                                                                                              | 配置关联 | 协议BeanId，可以在`<dubbo:service protocol="">`中引用此ID，如果ID不填，缺省和name属性值一样，重复则在name后加序号。 | 2.0.5以上版本 |
| name | `<protocol>` | string | **必填**     | dubbo                                                                                                                                                                                                              | 性能调优 | 协议名称                                                                                                     | 2.0.5以上版本 |
| port | `<port>`     | int    | 可选     | dubbo协议缺省端口为20880，rmi协议缺省端口为1099，http和hessian协议缺省端口为80；如果没有配置port，则自动采用默认端口，如果配置为-1，则会分配一个没有被占用的端口。Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。 | 服务发现 | 服务端口                                                                                                     | 2.0.5以上版本 |
| host | `<host>`     | string | 可选     | 自动查找本机IP                                                                                                                                                                                                      | 服务发现 | -服务主机名，多网卡选择或指定VIP及域名时使用，为空则自动查找本机IP，-**建议不要配置，让Dubbo自动获取本机IP**         | 2.0.5以上版本 |

##### 3.1.2.4. dubbo:registry【常用】

注册中心配置。对应的配置类：`org.apache.dubbo.config.RegistryConfig`。同时如果有多个不同的注册中心，可以声明多个 `<dubbo:registry>` 标签，并在 `<dubbo:service>` 或 `<dubbo:reference>` 的 registry 属性指定使用的注册中心。常用属性如下：

|   属性   |  对应URL参数   |  类型  | 是否必填  | 缺省值 |   作用   |                                                                      描述                                                                       |     兼容性     |
| -------- | ------------- | ------ | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| id       |               | string | 可选     |       | 配置关联 | 注册中心引用BeanId，可以在`<dubbo:service registry="">`或`<dubbo:reference registry="">`中引用此ID                                                | 1.0.16以上版本 |
| address  | `<host:port>` | string | **必填** |       | 服务发现 | 注册中心服务器地址，如果地址没有端口缺省为9090，同一集群内的多个地址用逗号分隔，如：`ip:port,ip:port`，不同集群的注册中心，请配置多个`<dubbo:registry>`标签 | 1.0.16以上版本 |
| protocol | `<protocol>`  | string | 可选     | dubbo | 服务发现 | 注册中心地址协议，支持dubbo, multicast, zookeeper, redis, consul(2.7.1), sofa(2.7.2), etcd(2.7.2), nacos(2.7.2)等协议                             | 2.0.0以上版本  |


##### 3.1.2.5. dubbo:monitor

监控中心配置。对应的配置类：`org.apache.dubbo.config.MonitorConfig`

##### 3.1.2.6. dubbo:application【常用】

应用信息配置。对应的配置类：`org.apache.dubbo.config.ApplicationConfig`。常用属性如下：

| 属性 | 对应URL参数  |  类型  | 是否必填  | 缺省值 |   作用   |                                                                                                                                                       描述                                                                                                                                                        |     兼容性     |
| ---- | ----------- | ------ | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| name | application | string | **必填** |       | 服务治理 | 当前应用名称，用于注册中心计算应用间依赖关系，**注意：消费者和提供者应用名不要一样，此参数不是匹配条件**，你当前项目叫什么名字就填什么，和提供者消费者角色无关，比如：kylin应用调用了morgan应用的服务，则kylin项目配成kylin，morgan项目配成morgan，可能kylin也提供其它服务给别人使用，但kylin项目永远配成kylin，这样注册中心将显示kylin依赖于morgan | 1.0.16以上版本 |


##### 3.1.2.7. dubbo:module

模块信息配置。对应的配置类：`org.apache.dubbo.config.ModuleConfig`

##### 3.1.2.8. dubbo:provider

服务提供者缺省值配置。对应的配置类：`org.apache.dubbo.config.ProviderConfig`。同时该标签为 `<dubbo:service>` 和 `<dubbo:protocol>` 标签的缺省值设置

##### 3.1.2.9. dubbo:consumer

服务消费者缺省值配置。配置类：`org.apache.dubbo.config.ConsumerConfig`。同时该标签为 `<dubbo:reference>` 标签的缺省值设置

##### 3.1.2.10. dubbo:method

方法级配置。对应的配置类：`org.apache.dubbo.config.MethodConfig`。同时该标签为 `<dubbo:service>` 或 `<dubbo:reference>` 的子标签，用于控制到方法级。如下例：

```xml
<dubbo:reference interface="com.xxx.XxxService">
    <dubbo:method name="findXxx" timeout="3000" retries="2" />
</dubbo:reference>
```

##### 3.1.2.11. dubbo:argument

方法参数配置。对应的配置类：`org.apache.dubbo.config.ArgumentConfig`。该标签为 `<dubbo:method>` 的子标签，用于方法参数的特征描述。如下例：

```xml
<dubbo:method name="findXxx" timeout="3000" retries="2">
    <dubbo:argument index="0" callback="true" />
</dubbo:method>
```

##### 3.1.2.12. dubbo:parameter

选项参数配置。对应的配置类：`java.util.Map`。同时该标签为`<dubbo:protocol>`或`<dubbo:service>`或`<dubbo:provider>`或`<dubbo:reference>`或`<dubbo:consumer>`的子标签，用于配置自定义参数，该配置项将作为扩展点设置自定义参数使用。如下例：

```xml
<dubbo:protocol name="napoli">
    <dubbo:parameter key="http://10.20.160.198/wiki/display/dubbo/napoli.queue.name" value="xxx" />
</dubbo:protocol>
```

或者：

```xml
<dubbo:protocol name="jms" p:queue="xxx" />
```

##### 3.1.2.13. dubbo:config-center

配置中心。对应的配置类：`org.apache.dubbo.config.ConfigCenterConfig`

#### 3.1.3. xml配置使用示例

> - xml的配置使用示例。详细参考busi-xml-server与busi-xml-client工程
> - Springmvc的集成Dubbo的使用示例，详细参考busi-mvc工程

### 3.2. 注解配置方式

> 详细示例参考：busi-annotation-consumer与busi-annotation-provider工程

- 注解方式的底层与XML一致，只是表现形式上的不同。
- 目标都是配置Dubbo基础信息，主要涉及以下五个必不可少的信息：`ApplicationConfig`、`ProtocolConfig`、`RegistryConfig`、`service`、`reference`

#### 3.2.1. @EnableDubbo 开启服务

`@EnableDubbo` 注解：开启注解Dubbo功能，其中可以加入scanBasePackages属性配置包扫描的路径，用于扫描并注册bean。其中封装了组件`@DubboComponentScan`，来扫描Dubbo框架的 `@Service` 注解暴露 Dubbo 服务，以及扫描Dubbo框架的 `@Reference` 字段或者方法注入 Dubbo 服务代理。

#### 3.2.2. @Configuration方式配置公共信息

`@Configuration`指定配置类，在类中分别将ApplicationConfig、ProtocolConfig、RegistryConfig等类创建到IOC容器中即可，提供者配置（消费者配置方式一样）示例如下：

```java
package com.moon.dubbo.annotation.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务提供者配置类 - 用于配置提供者需要几个主要注解
 */
@Configuration
// 开启dubbo注解扫描，指定Spring扫描包路径
@EnableDubbo(scanBasePackages = "com.moon.dubbo.annotation.service")
public class ProviderConfiguration {

    /**
     * 提供者全局配置，用于减少重复的配置
     * 相当于xml配置文件中的<dubbo:provider />标签
     *
     * @return ProviderConfig
     */
    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(1000);
        return providerConfig;
    }

    /**
     * 必需配置。服务提供方应用名称
     * 相当于xml配置文件中的<dubbo:application />标签
     *
     * @return ApplicationConfig
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("busi-annotation-provider");
        return applicationConfig;
    }

    /**
     * 必需配置。注册中心配置
     * 相当于xml配置文件中的<dubbo:registry />标签
     *
     * @return RegistryConfig
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("127.0.0.1");
        registryConfig.setPort(2181);
        return registryConfig;
    }

    /**
     * 必需配置。通信协议与监听端口
     * 相当于xml配置文件中的<dubbo:protocol />标签
     *
     * @return ProtocolConfig
     */
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(20880);
        return protocolConfig;
    }
}
```

#### 3.2.3. property方式自动装配公共信息

`@PropertySource`注解方式：使用Springboot属性文件方式，由Dubbo自动将文件信息配置入容器，就类似spring boot自动装配一样，提供者示例如下：

1. 创建dubbo-provider.properties文件

```properties
dubbo.application.name=busi-annotation-property-provider
dubbo.registry.address=zookeeper://127.0.0.1:2181
dubbo.protocol.name=dubbo
dubbo.protocol.port=20880
```

2. 使用`@PropertySource`注解读取properties文件

```java
package com.moon.dubbo.annotation.config;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 服务提供者配置类 - 用于配置提供者需要几个主要注解
@Configuration
// 开启dubbo注解扫描，指定Spring扫描包路径
@EnableDubbo(scanBasePackages = "com.moon.dubbo.annotation.service")
// 自动装配dubbo公共信息
@PropertySource("classpath:/dubbo-provider.properties")
public class ProviderPropertyConfiguration {
}
```

### 3.3. API 配置方式

> 详细示例参考：busi-api-consumer与busi-api-provider工程

API 属性与配置项一对一，各属性含义。详情参考官网：http://dubbo.apache.org/zh-cn/docs/user/configuration/api.html

![提供者](images/20200127123511449_27286.png)

- 提供者

```java
package com.moon.dubbo.api;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.moon.dubbo.api.service.OrderServiceImpl;
import com.moon.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * dubbo服务提供者 - 基于API配置方式
 */
@SpringBootApplication
public class ApiProvider {
    public static void main(String[] args) {
        SpringApplication.run(ApiProvider.class, args);
        // 服务提供者暴露服务配置
        ServiceConfig<OrderService> config = new ServiceConfig<>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        config.setApplication(new ApplicationConfig("busi-api-provider"));
        // 设置注册中心，多个注册中心可以用setRegistries()
        config.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        config.setInterface(OrderService.class);
        config.setRef(new OrderServiceImpl());
        // 暴露及注册服务
        config.export();
        System.out.println("busi-api-provider is running...");
    }
}
```

![消费者](images/20200127123537171_23898.png)

- 消费者

```java
package com.moon.dubbo.api;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.moon.entity.OrderEntiry;
import com.moon.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ubbo服务消费者 - 基于API配置方式
 */
@SpringBootApplication
public class ApiConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApiConsumer.class, args);
        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
        // 引用远程服务
        ReferenceConfig<OrderService> reference = new ReferenceConfig<>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(new ApplicationConfig("busi-api-consumer"));
        // 设置注册中心，多个注册中心可以用setRegistries()
        reference.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        reference.setInterface(OrderService.class);
        System.out.println("busi-api-consumer is running...");
        // 和本地bean一样使用xxxService
        OrderService orderService = reference.get();
        OrderEntiry entiry = orderService.getDetail("2");
        System.out.println("基于API配置的消费者调用OrderService.getDetail()接口成功，result: " + entiry.getMoney());
    }
}
```

## 4. Dubbo控制台部署（前后端分离版本）

- 2.6版本前，在dubbo源码包里，有一个admin的war包，使用tomcat将其部署即可
- 从2.6版本之后，dubbo控制台已单独版本管理（目前只到0.1版本），使用了前后端分离的模式。前端使用Vue和Vuetify分别作为Javascript框架和UI框架，后端采用Spring Boot框架。

> 部署参考官网：http://dubbo.apache.org/zh-cn/docs/admin/introduction.html

拉取项目源码：`git clone https://github.com/apache/dubbo-admin.git`

### 4.1. Maven方式部署

- 安装，此方式即将前端vue产出的静态内容集成到springboot包内

```bash
cd dubbo-admin
mvn clean package
# 如果打包不成功，则尝试路过test。mvn clean package -Dmaven.test.skip=true
cd dubbo-admin-distribution/target
java -jar dubbo-admin-0.1.jar
```

- 访问`http://localhost:8080`

### 4.2. 前后端分离部署

- 前端

```bash
cd dubbo-admin-ui
npm install
npm run dev
```

- 后端

```bash
cd dubbo-admin-server
mvn clean package
cd target
java -jar dubbo-admin-server-0.1.jar
```

- 访问：`http://localhost:8081`

### 4.3. 配置

- 配置文件为：`dubbo-admin-server/src/main/resources/application.properties`
- 主要的配置

```properties
admin.config-center=zookeeper://127.0.0.1:2181
admin.registry.address=zookeeper://127.0.0.1:2181
admin.metadata-report.address=zookeeper://127.0.0.1:2181
```

三个配置项分别指定了配置中心，注册中心和元数据中心的地址，关于这三个中心的详细说明，可以参考这里。 也可以和Dubbo2.7一样，在配置中心指定元数据和注册中心的地址，以zookeeper为例，配置的路径和内容如下:

```properties
# /dubbo/config/dubbo/dubbo.properties
dubbo.registry.address=zookeeper://127.0.0.1:2181
dubbo.metadata-report.address=zookeeper://127.0.0.1:2181
```

配置中心里的地址会覆盖掉本地application.properties的配置

### 4.4. 服务治理

服务治理主要作用是改变运行时服务的行为和选址逻辑，达到限流，权重配置等目的，主要有以下几个功能：

- 应用级别的服务治理
- 标签路由
- 条件路由
- 黑白名单
- 动态配置
- 权重调节
- 负载均衡
- 配置管理

> 详细用法参考官网：http://dubbo.apache.org/zh-cn/docs/admin/serviceGovernance.html

## 5. Dubbo 高级特性进阶 - 常用配置策略用法
### 5.1. 启动时检查

- Dubbo缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止Spring初始化完成，以便上线时，能及早发现问题，默认`check="true"`
- 可以通过`check="false"`关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动
- 另外，如果Spring容器是懒加载的，或者通过API编程延迟引用服务，也需要关闭check属性，否则服务临时不可用时，会抛出异常，拿到null引用，如果`check="false"`，总是会返回引用，当服务恢复时，能自动连上

#### 5.1.1. 通过 spring 配置文件

- 关闭某个服务的启动时检查 (没有提供者时报错)：

```xml
<dubbo:reference interface="com.foo.BarService" check="false" />
```

- 关闭所有服务的启动时检查 (没有提供者时报错)：

```xml
<dubbo:consumer check="false" />
```

- 关闭注册中心启动时检查 (注册订阅失败时报错)：

```xml
<dubbo:registry check="false" />
```

#### 5.1.2. 通过 dubbo.properties

```properties
dubbo.reference.com.foo.BarService.check=false
dubbo.reference.check=false # 强制改变所有 reference 的 check 值，就算配置中有声明，也会被覆盖
dubbo.consumer.check=false # 是设置 check 的缺省值，如果配置中有显式的声明，如：<dubbo:reference check="true"/>，不会受影响
dubbo.registry.check=false # 前面两个都是指订阅成功，但提供者列表是否为空是否报错，如果注册订阅失败时，也允许启动，需使用此选项，将在后台定时重试
```

#### 5.1.3. 通过 -D 参数

```shell
java -Ddubbo.reference.com.foo.BarService.check=false
java -Ddubbo.reference.check=false
java -Ddubbo.consumer.check=false
java -Ddubbo.registry.check=false
```

### 5.2. Dubbo超时重连
#### 5.2.1. 超时

Dubbo消费端在发出请求后，需要有一个临界时间界限来判断服务端是否正常。这样消费端达到超时时间，那么Dubbo会进行重试机制，不合理的重试在一些特殊的业务场景下可能会引发很多问题，需要合理设置接口超时时间。Dubbo超时和重试配置示例如下：

```xml
<!-- 服务调用超时设置为5秒，超时不重试 -->
<dubbo:reference id="xxxService" interface="com.xxx.XxxService" retries="0" timeout="5000"/>
```

#### 5.2.2. 重连

- Dubbo在调用服务不成功时，*默认会重试2次，即`retries="2"`*。通过设置`<dubbo:reference>`标签中`retries="0"`属性控制重试次数
- Dubbo的路由机制，会把超时的请求路由到其他机器上，而不是本机尝试，所以Dubbo的重试机制也能一定程度的保证服务的质量

### 5.3. 集群容错

当消费端某次调用失败是一些环境偶然因素造成的（如网络抖动），dubbo还给予了容错补救机会。在集群调用失败时，Dubbo 提供了多种容错方案，缺省为 `failover` 重试

![容错方案关系图](images/20200129162636712_10255.png)

#### 5.3.1. 集群容错模式

- Failover Cluster: 失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 `retries="2"` 来设置重试次数(不含第一次)。

```xml
<!--
    方式1: 重试次数配置
    cluster="failover"属性可以不指定，是默认配置
-->
<dubbo:service retries="2" />
<!-- 方式2 -->
<dubbo:reference retries="2" />
<!-- 方式3 -->
<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>
```

- Failfast Cluster: 快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录
- Failsafe Cluster: 失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。
- Failback Cluster: 失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
- Forking Cluster: 并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 `forks="2"` 来设置最大并行数。
- Broadcast Cluster: 广播调用所有提供者，逐个调用，任意一台报错则报错 [2]。通常用于通知所有提供者更新缓存或日志等本地资源信息

#### 5.3.2. 集群模式配置

按照以下示例在服务提供方和消费方配置集群模式

```xml
<dubbo:service cluster="failsafe" />
<dubbo:reference cluster="failsafe" />
```

### 5.4. 负载均衡

在集群负载均衡时，Dubbo 提供了多种均衡策略，缺省为 random 随机调用

#### 5.4.1. 负载均衡策略

- Random LoadBalance
    - 随机，按权重设置随机概率。
    - 在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。
- RoundRobin LoadBalance
    - 轮询，按公约后的权重设置轮询比率。
    - 存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。
- LeastActive LoadBalance
    - 最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
    - 使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
- ConsistentHash LoadBalance
    - 一致性 Hash，相同参数的请求总是发到同一提供者。
    - 当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。

> 注意：配置时负载均衡策略的单词都是**全小写**，如果出现大写会报错

#### 5.4.2. 配置示例

- 服务端服务级别

```xml
<dubbo:service interface="..." loadbalance="roundrobin" />
```

- 客户端服务级别

```xml
<dubbo:reference interface="..." loadbalance="roundrobin" />
```

- 服务端方法级别

```xml
<dubbo:service interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:service>
```

- 客户端方法级别

```xml
<dubbo:reference interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:reference>
```

### 5.5. 只订阅/只注册
#### 5.5.1. 只订阅

场景：在本地开发的时候，不能把自己机器的未开发好的服务注册到开发环境，但是又需要使用注册中心的其他服务。服务提供者配置禁止注册`register="false"`

为方便开发测试，经常会在线下共用一个所有服务可用的注册中心，这时，如果一个正在开发中的服务提供者注册，可能会影响消费者不能正常运行。示例如下：

```xml
<!-- 禁用注册配置 -->
<dubbo:registry address="10.20.153.10:9090" register="false" />
<!-- 或者 -->
<dubbo:registry address="10.20.153.10:9090?register=false" />
```

#### 5.5.2. 只注册

如果有两个镜像环境，两个注册中心，有一个服务只在其中一个注册中心有部署，另一个注册中心还没来得及部署，而两个注册中心的其它应用都需要依赖此服务。这个时候，可以让服务提供者方只注册服务到另一注册中心，而不从另一注册中心订阅服务

比如开发环境为了省机器，没有部署某个服务，如短信/邮件功能。但整个系统又必须要调用它。此时可以借用一下测试环境的此服务（不能影响测试环境原本的服务闭环）。将测试环境的短信/邮件服务也向开发环境注册一份，只注册（其依赖的服务必须还是测试环境的）。服务提供者配置禁止订阅`subscribe="false"`

```xml
<!-- 禁用订阅配置 -->
<dubbo:registry id="hzRegistry" address="10.20.153.10:9090" />
<dubbo:registry id="qdRegistry" address="10.20.141.150:9090" subscribe="false" />
<!-- 或者 -->
<dubbo:registry id="hzRegistry" address="10.20.153.10:9090" />
<dubbo:registry id="qdRegistry" address="10.20.141.150:9090?subscribe=false" />
```

### 5.6. 服务分组

如果想在测试、开发环境等多套环境中共用同一个注册中心。或者当一个接口有多种实现时，可以用 group 区分

```xml
<!-- 服务提供方 -->
<dubbo:service group="feedback" interface="com.xxx.IndexService" />
<dubbo:service group="member" interface="com.xxx.IndexService" />
<!-- 引用消费方 -->
<dubbo:reference id="feedbackIndexService" group="feedback" interface="com.xxx.IndexService" />
<dubbo:reference id="memberIndexService" group="member" interface="com.xxx.IndexService" />

<!-- 2.2.0 以上版本支持，总是只调一个可用组的实现 -->
<dubbo:reference id="barService" interface="com.foo.BarService" group="*" />
```

### 5.7. 多版本

- 当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用
- 服务端提供接口的实现升级时，可由dubbo的版本号操作进行过渡。如果上线上测试新版本接口有缺陷，为了不影响业务，要迅速切回原版本接口，最大程度减少损失。

```xml
<!-- 服务方 -->
<!--版本1接口-->
<dubbo:service interface="com.xxx.XxxServices" ref="xxxService" version="1.0"/>
<!--版本2接口-->
<dubbo:service interface="com.xxx.XxxServices" ref="xxxService2" version="2.0"/>

<!-- 消费方 -->
<dubbo:reference id="xxxService1.0" interface="com.xxx.XxxServices" version="2.0"/>
```

### 5.8. 结果缓存

结果缓存（*2.1.0 以上版本支持*），用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量

#### 5.8.1. 缓存类型

- `lru`：基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
- `threadlocal`：当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
- `jcache`：与 JSR107 集成，可以桥接各种缓存实现

#### 5.8.2. 配置示例

```xml
<!-- 以消费方为例，可以配置全局缓存策略，这样所有服务引用都启动缓存 -->
<dubbo:consumer cache="lru"/>

<!-- 仅对某个服务引用配置缓存策略 -->
<dubbo:reference interface="com.foo.BarService" cache="lru" />

<!-- 还支持对单个方法启用缓存策略 -->
<dubbo:reference interface="com.foo.BarService">
    <dubbo:method name="findBar" cache="lru" />
</dubbo:reference>
```

> **服务方配置方法与消费端完全一样**


### 5.9. 异步调用(Consumer端调用)

> 从v2.7.0开始，Dubbo的所有异步编程接口开始以CompletableFuture为基础

Dubbo的异步调用是非阻塞的NIO调用，一个线程可同时并发调用多个远程服务，每个服务的调用都是非阻塞的，线程立即返回。就是对java中Futrue模式的扩展支持

![异步调用流程图](images/20200129192643662_18150.png)

如上图，userThread发出调用后，IOThread会立即返回，并在RPC上下文RpcContext中设置Future。userThread后续可以从RpcContext中取得此Future，然后wait这个Future其它的事情都由IOThread完成

总之，userThread发出调用后IOThread会立刻返回，而不是等调用在服务端执行完代码、返回结果后返回。用户返回后可以去做点其它事情，比如调用另外一个服务，然后回头等待前一个调用完成。从上图可以看出，异步调用完全是Consumer端的行为

> 详细案例参考busi-mall工程中的dubbo.xml与IndexController

#### 5.9.1. 在消费端配置

```xml
<dubbo:reference id="asyncService" interface="org.apache.dubbo.samples.governance.api.AsyncService">
      <dubbo:method name="sayHello" async="true" />
</dubbo:reference>
```

- 也可以设置是否等待消息发出，即是否等待IOThread发送完Request后再返回
    - `sent="true"` 等待消息发出去再返回，如果消息发送失败将抛出异常。
    - `sent="false"` 不等待消息发出，将消息放入 IO 队列，即刻返回。

```xml
<dubbo:method name="findFoo" async="true" sent="true" />
```

- 如果你只是想异步，完全忽略返回值，可以配置 `return="false"`，以减少 Future 对象的创建和管理成本：

```xml
<dubbo:method name="findFoo" async="true" return="false" />
```

#### 5.9.2. 调用代码

```java
// 此调用会立即返回null
asyncService.sayHello("world");
// 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
CompletableFuture<String> helloFuture = RpcContext.getContext().getCompletableFuture();
// 为Future添加回调
helloFuture.whenComplete((retValue, exception) -> {
    if (exception == null) {
        System.out.println(retValue);
    } else {
        exception.printStackTrace();
    }
});
```

或者

```java
CompletableFuture<String> future = RpcContext.getContext().asyncCall(
    () -> {
        asyncService.sayHello("oneway call request1");
    }
);

future.get();
```

> 注意：如果xml配置文件中没有对消费标签配置`async="true"`属性，则以上示例代码不生效，还是同步调用。获取到的Future对象为null

### 5.10. 事件通知

在调用之前、调用之后、出现异常时，会触发 oninvoke、onreturn、onthrow 三个事件，可以配置当事件发生时，通知哪个类的哪个方法。在Consumer端，可以为三个事件指定事件处理方法

#### 5.10.1. 服务消费者 Callback 接口

```java
interface Notify {
    public void onreturn(Person msg, Integer id);
    public void onthrow(Throwable ex, Integer id);
}
```

#### 5.10.2. 服务消费者 Callback 实现

```java
class NotifyImpl implements Notify {
    public Map<Integer, Person>    ret    = new HashMap<Integer, Person>();
    public Map<Integer, Throwable> errors = new HashMap<Integer, Throwable>();

    /**
     * 成功调用后回调方法
     *
     * @param msg    返回结果值
     * @param ig     原方法调用的入参
     */
    public void onreturn(Person msg, Integer id) {
        System.out.println("onreturn:" + msg);
        ret.put(id, msg);
    }

    /**
     * 调用失败后回调方法
     *
     * @param ex  出现异常返回结果值
     * @param id  原方法调用的入参
     */
    public void onthrow(Throwable ex, Integer id) {
        errors.put(id, ex);
    }
}
```

#### 5.10.3. 服务消费者 Callback 配置

```xml
<bean id ="demoCallback" class = "org.apache.dubbo.callback.implicit.NofifyImpl" />
<dubbo:reference id="demoService" interface="org.apache.dubbo.callback.implicit.IDemoService" version="1.0.0" group="cn" >
      <dubbo:method name="get" async="true" onreturn="demoCallback.onreturn" onthrow="demoCallback.onthrow" />
</dubbo:reference>
```

#### 5.10.4. 配置几种组合情况

- callback 与 async 功能正交分解，async=true 表示结果是否马上返回，onreturn 表示是否需要回调。两者叠加存在以下几种组合情况
    - 异步回调模式：`async=true onreturn="xxx"`
    - 同步回调模式：`async=false onreturn="xxx"`
    - 异步无回调：`async=true`
    - 同步无回调：`async=false`

## 6. 服务化最佳实践

> 详情参考官网：http://dubbo.apache.org/zh-cn/docs/user/best-practice.html

### 6.1. 在 Provider 端应尽量配置的属性

Dubbo的属性配置优先度上，遵循顺序：`reference属性 --> service属性 --> Consumer 属性`

其中reference和Consumer是消费端配置，service是服务端配置

而对于服务调用的超时时间、重试次数等属性，服务的提供方比消费方更了解服务性能，因此我们应该在 Provider 端尽量多配置 Consumer 端属性，让其漫游到消费端发挥作用

#### 6.1.1. 在 Provider 端尽量多配置 Consumer 端属性

- Provider 端尽量多配置 Consumer 端的属性，让 Provider 的实现者一开始就思考 Provider 端的服务特点和服务质量等问题

```xml
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService"
    timeout="300" retries="2" loadbalance="random" actives="0" />

<dubbo:service interface="com.alibaba.hello.api.WorldService" version="1.0.0" ref="helloService"
    timeout="300" retries="2" loadbalance="random" actives="0" >
    <dubbo:method name="findAllPerson" timeout="10000" retries="9" loadbalance="leastactive" actives="5" />
<dubbo:service/>
```

- 建议在 Provider 端配置的 Consumer 端属性有：
    1. `timeout`：方法调用的超时时间
    2. `retries`：失败重试次数，缺省是 2
    3. `loadbalance`：负载均衡算法，缺省是随机 random。还可以配置轮询 roundrobin、最不活跃优先 leastactive 和一致性哈希 consistenthash 等
    4. `actives`：消费者端的最大并发调用限制，即当 Consumer 对一个服务的并发调用到上限后，新调用会阻塞直到超时，在方法上配置 dubbo:method 则针对该方法进行并发限制，在接口上配置 dubbo:service，则针对该服务进行并发限制

#### 6.1.2. 在 Provider 端配置合理的 Provider 端属性

```xml
<dubbo:protocol threads="200" /> 
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService"
    executes="200" >
    <dubbo:method name="findAllPerson" executes="50" />
</dubbo:service>
```

- 建议在 Provider 端配置的 Provider 端属性有：
    1. `threads`：服务线程池大小
    2. `executes`：一个服务提供者并行执行请求上限，即当 Provider 对一个服务的并发调用达到上限后，新调用会阻塞，此时 Consumer 可能会超时。在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制

### 6.2. 服务拆分最佳实现
#### 6.2.1. 分包

建议将服务接口、服务模型、服务异常等均放在 API 包中，因为服务模型和异常也是 API 的一部分，这样做也符合分包原则：重用发布等价原则(REP)，共同重用原则(CRP)。

如果需要，也可以考虑在 API 包中放置一份 Spring 的引用配置，这样使用方只需在 Spring 加载过程中引用此配置即可。配置建议放在模块的包目录下，以免冲突，如：com/alibaba/china/xxx/dubbo-reference.xml。

#### 6.2.2. 粒度

服务接口尽可能大粒度，每个服务方法应代表一个功能，而不是某功能的一个步骤，否则将面临分布式事务问题，Dubbo 暂未提供分布式事务支持。

服务接口建议以业务场景为单位划分，并对相近业务做抽象，防止接口数量爆炸。

不建议使用过于抽象的通用接口，如：Map query(Map)，这样的接口没有明确语义，会给后期维护带来不便。

#### 6.2.3. 版本

每个接口都应定义版本号，为后续不兼容升级提供可能，如：`<dubbo:service interface="com.xxx.XxxService" version="1.0" />`

建议使用两位版本号，因为第三位版本号通常表示兼容升级，只有不兼容时才需要变更服务版本。

当不兼容时，先升级一半提供者为新版本，再将消费者全部升为新版本，然后将剩下的一半提供者升为新版本。

#### 6.2.4. 异常

建议使用异常汇报错误，而不是返回错误码，异常信息能携带更多信息，并且语义更友好。

如果担心性能问题，在必要时，可以通过 override 掉异常类的 `fillInStackTrace()` 方法为空方法，使其不拷贝栈信息。

查询方法不建议抛出 checked 异常，否则调用方在查询时将过多的 `try...catch`，并且不能进行有效处理。

服务提供方不应将 DAO 或 SQL 等异常抛给消费方，应在服务实现中对消费方不关心的异常进行包装，否则可能出现消费方无法反序列化相应异常。

## 7. SPI机制原理

Dubbo框架是建立的SPI机制之上

### 7.1. Java SPI 机制
#### 7.1.1. SPI 简述

SPI全称Service Provider Interface，是Java提供的一套用来被第三方实现或者扩展的API，它可以用来启用框架扩展和替换组件

![Java SPI 机制调用流程图](images/20200130184515935_6858.jpg)

可以看到，SPI的本质，其实是帮助程序，为某个特定的接口寻找它的实现类。而且哪些实现类的会加载，是个动态过程（不是提前预定好的）

有点类似IOC的思想，就是将装配的控制权移到程序之外，在模块化设计中这个机制尤其重要。所以SPI的核心思想就是**解耦**

- 常见的例子：
    - 数据库驱动加载接口实现类的加载。JDBC加载不同类型数据库的驱动
    - 日志门面接口实现类加载，SLF4J加载不同提供商的日志实现类
    - Spring中大量使用了SPI，比如：对servlet3.0规范对ServletContainerInitializer的实现、自动类型转换Type Conversion SPI(Converter SPI、Formatter SPI)等

#### 7.1.2. 基础使用

- 要使用Java SPI，需要遵循如下约定：
    1. 当服务提供者提供了接口的一种具体实现后，在jar包的`META-INF/services`目录下创建一个以“接口全限定名”为命名的文件，内容为实现类的全限定名
    2. 接口实现类所在的jar包放在主程序的classpath中
    3. 程序通过`java.util.ServiceLoder`动态装载实现模块，它通过扫描`META-INF/services`目录下的配置文件找到实现类的全限定名，把类加载到JVM
    4. SPI的实现类必须有无参构造方法

> 详情示例参考dubbo-thought示例工程

- 先定义一个接口

```java
public interface InfoService {
    Object sayHello(String name);
}
```

- 定义多个接口的实现

```java
public class InfoServiceAImpl implements InfoService {
    @Override
    public Object sayHello(String name) {
        System.out.println(name + ",你好，调通了A实现！");
        return name + ",你好，调通了A实现！";
    }
}

public class InfoServiceBImpl implements InfoService {
    @Override
    public Object sayHello(String name) {
        System.out.println(name + ",你好，调通了B实现！");
        return name + ",你好，调通了B实现！";
    }
}
```

- 创建接口的全限定名文件`com.moon.service.InfoService`，内容为该接口的实现类名称

```
com.moon.service.impl.info.InfoServiceAImpl
com.moon.service.impl.info.InfoServiceBImpl
```

![实现约定](images/20200131125629336_8727.jpg)

- 测试

```java
@Test
public void testBaseSpi() {
    //服务加载器，加载实现类
    ServiceLoader<InfoService> serviceLoader = ServiceLoader.load(InfoService.class);
    // ServiceLoader是实现了Iterable的迭代器，直接遍历实现类
    for (InfoService service : serviceLoader) {
        Object result = service.sayHello("Moon"); // 依次调用文件中配置的所有实现类
    }
}
```

#### 7.1.3. 核心功能类

需要指出的是，java之所以能够顺利根据配置加载这个实现类，完全依赖于jdk内的一个核心类：

![SPI的核心类](images/20200130231835343_15049.jpg)

### 7.2. Dubbo SPI机制

- Java SPI机制非常简单，就是读取指定的配置文件，将所有的类都加载到程序中。而这种机制，存在很多缺陷，比如：
    1. 所有实现类无论是否使用，直接被加载，可能存在浪费
    2. 不能够灵活控制什么时候什么时机，匹配什么实现，功能太弱
- Dubbo框架就基于自己的需要，对JAVA的SPI机制进行增强

#### 7.2.1. @SPI 注解

与 Java SPI 实现类配置不同，Dubbo SPI 是通过键值对的方式进行配置，这样就可以按需加载指定的实现类。另外，需要在接口上标注 `@SPI` 注解。表明此接口是SPI的扩展点：

```java
/**
 * SPI 全称为 Service Provider Interface，是一种服务发现机制，目标是为接口寻找实现类。
 * Java SPI 的作法：
 * 1.在类路径下META-INF/service下创建文件，名称为接口的全限定名。
 * 2.将接口实现类的全限定名配置在文件中
 * 3.服务启动时，将由服务加载器读取配置文件，并加载实现类。
 * <p>
 * Dubbo SPI的作法：
 * 1.Dubbo 增强原生的SPI机制来更好的满足拓展要求，其以键值对的方式对接口的实现进行配置管理。
 * 2.Dubbo引入三个注解： @SPI、@Adaptive和@Activate。
 * <p>
 * 只有接口上标注 @SPI 注解，才能被Dubbo框架管理起来
 */
@SPI("b") // @SPI注解的值是指定默认的实现类对应的key值
public interface InfoService {
    Object sayHello(String name);

    Object passInfo(String msg, URL url);
}
```

- Dubbo SPI 所需的配置文件需放置在 `META-INF/dubbo` 路径下，配置文件的名称为接口的全限定名。文件的内容以key-value的形式，配置该接口对应的实现类的全限定名

![dubbo的SPI机制使用约定](images/20200131150555960_4262.jpg)

- 测试

```java
/**
 * dubbo SPI类加载验证
 * extensionLoader.getExtension("a")  --> 取到key对应的扩展类
 * extensionLoader.getDefaultExtension() --> 取得在接口上@SPI注解指定的默认扩展类
 */
@Test
public void dubboSPI() {
    // 获取InfoService的 Loader 实例
    ExtensionLoader<InfoService> extensionLoader = ExtensionLoader.getExtensionLoader(InfoService.class);
    // 取得a拓展类
    InfoService infoServiceA = extensionLoader.getExtension("a");
    infoServiceA.sayHello("AAAA");
    // 取得接口上@SPI注解指定的默认拓展类（b拓展类）
    InfoService infoServiceB = extensionLoader.getDefaultExtension();
    infoServiceB.sayHello("I'm default");
}
```

测试结果

```console
AAAA,你好，调通了A实现！
I'm default,你好，调通了B实现！
```

#### 7.2.2. @Activate 注解

Dubbo的Spi机制虽然对原生SPI有了增强，但功能还远远不够。

在工作中，某种时候存在这样的情形，需要同时启用某个接口的多个实现类，如Filter过滤器。希望某种条件下启用这一批实现，而另一种情况下启用那一批实现，比如：希望的RPC调用的消费端和服务端，分别启用不同的两批Filter，这样就需要使用`@Activate`注解

`@Activate`注解表示一个扩展是否被激活(使用)，可以放在类定义和方法上，dubbo用它在spi扩展类定义上，表示这个扩展实现激活条件和时机。它有两个设置过滤条件的字段，`group`，`value` 都是字符数组。用来指定这个扩展类在什么条件下激活。简单示例如下：

```java
// 表示如果过滤器使用方（通过group指定）属于Constants.PROVIDER（服务提供方）或者Constants.CONSUMER（服务消费方）就激活使用这个过滤器
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class testActivate1 implements Filter {
}

// 表示如果过滤器使用方（通过group指定）属于Constants.PROVIDER（服务提供方）并且 URL中有参数 Constants.TOKEN_KEY（token）时就激活使用这个过滤器
@Activate(group = Constants.PROVIDER, value = Constants.TOKEN_KEY)
public class testActivate2 implements Filter {
}
```

> 详细示例参考dubbo-thought项目的base-spi和busi-logic工程

#### 7.2.3. Javassist动态编译

- 在SPI寻找实现类的过程中，`getAdaptiveExtension`方法得到的对象，只是个接口代理对象，此代理对象是由临时编译的类来实现的。
- javassist动态编译类有两种方式，此动态编译生成类是没有class文件

- 方式一：Javassist是一个开源的分析、编辑和创建Java字节码的类库。
    - Javassist中最为重要的是ClassPool，CtClass ，CtMethod 以及 CtField这几个类。
    - `ClassPool`：一个基于HashMap实现的CtClass对象容器，其中键是类名称，值是表示该类的CtClass对象。默认的ClassPool使用与底层JVM相同的类路径，因此在某些情况下，可能需要向ClassPool添加类路径或类字节。
    - `CtClass`：表示一个类，这些CtClass对象可以从ClassPool获得。
    - `CtMethods`：表示类中的方法。
    - `CtFields`：表示类中的字段。

```java
/**
 * javassist动态生成类示例
 */
@Test
public void createClassByJavassist() throws Exception {
    // 1. ClassPool：Class对象的容器
    ClassPool pool = ClassPool.getDefault();

    // 2. 通过ClassPool生成一个public类，指定生成的全限定名
    CtClass ctClass = pool.makeClass("com.moon.service.DemoImpl");

    // 3. 给生成的类添加属性 private String name
    CtField nameFild = new CtField(pool.getCtClass("java.lang.String"), "name", ctClass);
    nameFild.setModifiers(Modifier.PRIVATE);
    ctClass.addField(nameFild);

    // 添加属性 private int age
    CtField ageField = new CtField(pool.getCtClass("int"), "age", ctClass);
    ageField.setModifiers(Modifier.PRIVATE);
    ctClass.addField(ageField);

    // 4. 为属性添加getXXX和setXXX方法
    ctClass.addMethod(CtNewMethod.getter("getName", nameFild));
    ctClass.addMethod(CtNewMethod.setter("setName", nameFild));
    ctClass.addMethod(CtNewMethod.getter("getAge", ageField));
    ctClass.addMethod(CtNewMethod.setter("setAge", ageField));

    // 5. 添加方法，如：void sayHello(String name) {...}
    CtMethod ctMethod = new CtMethod(CtClass.voidType, "sayHello", new CtClass[]{}, ctClass);
    // 将方法设置为public
    ctMethod.setModifiers(Modifier.PUBLIC);
    // 设置方法体内容
    ctMethod.setBody("{\nSystem.out.println(\"hello \" + getName() + \" !\");\n}");
    ctClass.addMethod(ctMethod); // 设置方法到类中

    // 6. 生成Class类对象
    Class clazz = ctClass.toClass();

    // 通过反射去创建实例
    Object obj = clazz.newInstance();
    // 反射执行方法sayHello
    obj.getClass().getMethod("setName", new Class[]{String.class}).invoke(obj, new Object[]{"moon"});
    obj.getClass().getMethod("sayHello", new Class[]{}).invoke(obj, new Object[]{});
}
```

- 方式二：动态编译是从Java 6开始支持的，主要是通过一个JavaCompiler接口来完成的。通过这种方式我们可以直接编译一个已经存在的java文件，也可以在内存中动态生成Java代码，动态编译执行

```java
/**
 * JavassistCompiler 动态编译类示例
 */
@Test
public void createClassByCompile() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    // 1. 创建JavassistCompiler
    JavassistCompiler compiler = new JavassistCompiler();
    // 2. 直接字符串的形式编写一个类，转成Class
    Class<?> clazz = compiler.compile("public class DemoImpl implements DemoService {     public String sayHello(String name) {        System.out.println(\"hello \" + name);        return \"Hello, \" + name;   }}", JavassistTest.class.getClassLoader());
    // 3. 创建对象
    Object obj = clazz.newInstance();
    // 4. 反射 执行方法sayHello
    obj.getClass().getMethod("sayHello", new Class[]{String.class}).invoke(obj, new Object[]{"moon"});
}
```

#### 7.2.4. @Adaptive 注解

扩展点对应的实现类不能在程序运行时动态指定，就是`extensionLoader.getExtension`方法写死了扩展点对应的实现类，不能在程序运行期间根据运行时参数进行动态改变。而在程序使用时会希望对实现类进行懒加载，并且能根据运行时情况来决定，应该启用哪个扩展类。为了解决这个问题，dubbo引入了`@Adaptive`注解，也就是dubbo的自适应机制

1. `@Adaptive`注解在加在具体的扩展实现类上，此类会当成最佳的适配

```java
// @Adaptive // 有@Adaptive注解的实现类，优先选择做为SPI扩展接口的适配类
public class InfoServiceCImpl implements InfoService {
    ......
}

/* 获取扩展点接口的加载器对象 */
ExtensionLoader<InfoService> loader = ExtensionLoader.getExtensionLoader(InfoService.class);
InfoService adaptiveExtension = loader.getAdaptiveExtension();
URL url = URL.valueOf("test://localhost/test");
adaptiveExtension.passInfo("moon", url)
```

2. `@Adaptive`注解在接口的某个方法上，使用时，需要在URL对象中增加选择规则参数，如：`info.service=a`。参数名格式是接口类xxxXxxx的驼峰大小写拆分

```java
@SPI("b") // @SPI注解的值是指定默认的实现类对应的key值
public interface InfoService {
    @Adaptive
    Object passInfo(String msg, URL url);
}

/* 获取扩展点接口的加载器对象 */
ExtensionLoader<InfoService> loader = ExtensionLoader.getExtensionLoader(InfoService.class);
InfoService adaptiveExtension = loader.getAdaptiveExtension();
URL url = URL.valueOf("test://localhost/test?info.service=a");
adaptiveExtension.passInfo("moon", url)
```

3. `@Adaptive({"xxxx"})`注解在接口的某个方法，直接指定url对象后的参数名称。

```java
@SPI("b") // @SPI注解的值是指定默认的实现类对应的key值
public interface InfoService {
    @Adaptive({"abc"})
    Object passInfo(String msg, URL url);
}

/* 获取扩展点接口的加载器对象 */
ExtensionLoader<InfoService> loader = ExtensionLoader.getExtensionLoader(InfoService.class);
InfoService adaptiveExtension = loader.getAdaptiveExtension();
URL url = URL.valueOf("test://localhost/test?info.service=a&abc=c");
adaptiveExtension.passInfo("moon", url)
```

**注意：`ExtensionLoader<xxx>.getAdaptiveExtension()`方法获取的不是实例对象，而是静态代理对象**

> 详细示例参考dubbo-thought项目

#### 7.2.5. Dubbo SPI的依赖注入

Dubbo SPI的核心实现类为ExtensionLoader，此类的使用几乎遍及Dubbo的整个源码体系

- ExtensionLoader有三个重要的入口方法，分别与`@SPI`、`@Activate`、`@Adaptive`注解对应
    - `getExtension`方法，对应加载所有的实现
    - `getActivateExtension`方法，对应解析加载`@Activate`注解对应的实现
    - `getAdaptiveExtension`方法，对应解析加载`@Adaptive`注解对应的实现
- 其中，`@Adaptive`注解作的自适应功能，还涉及到了代理对象（而Dubbo的代理机制，有两种选择，jdk动态代理和javassist动态编译类）

##### 7.2.5.1. Dubbo SPI 依赖注入场景

Dubbo的SPI机制，除上以上三种注解的用法外，还有一个重要的功能依赖注入，下面是依赖注入的一个示例：

- 一个接口扩展点

```java
@SPI("Zero")
public interface OrderService {
    String getDetail(String id, URL url);
}
```

- 在其实现类中，引入了另一个扩展点接口对象InfoService

```java
public class OrderServiceImpl implements OrderService {
    /* 是dubbo的扩展点，是spring的bean接口 */
    private InfoService infoService;

    public void setInfoService(InfoService infoService) {
        this.infoService = infoService;
    }

    @Override
    public String getDetail(String name, URL url) {
        infoService.passInfo(name, url);
        System.out.println(name + ",OrderServiceImpl订单处理成功！");
        return name + ",你好，OrderServiceImpl订单处理成功！";
    }
}
```

- 测试OrderService加载过程

```java
@Test
public void iocSPI() {
    // 获取OrderService的 Loader 实例
    ExtensionLoader<OrderService> loader = ExtensionLoader.getExtensionLoader(OrderService.class);
    // 取得默认拓展类
    OrderService orderService = loader.getDefaultExtension();
    URL url = URL.valueOf("test://localhost/test?info.service=b");
    orderService.getDetail("MoonZero", url);
}
```

> 最后dubbo调用了InfoService的B实现

##### 7.2.5.2. Dubbo依赖注入的过程分析

从`loader.getDefaultExtension();`开始，通过`getExtensionClasses();`方法获取所有扩展类Class对象，如果第一次创建，则参过`instance = createExtension(name);`创建实例

![创建扩展实例](images/20200202185605860_9906.png)

先创建扩展实例，此时实例中的引用扩展对象的值为null。通过`injectExtension()`方法中的`Object object = objectFactory.getExtension(pt, property);`进行扩展属性对象依赖注入

```java
@SPI
public interface ExtensionFactory {
    /**
     * Get extension.
     *
     * @param type object type.
     * @param name object name.
     * @return object instance.
     */
    <T> T getExtension(Class<T> type, String name);
}
```

这是dubbo的一个扩展点工厂接口，只有一个方法，作用是根据class和name查找实现类。这个接口，是dubbo一个扩展点，接下来看看此接口的实现类

![! 修改文件](images/20200202191251020_579.jpg)

ExtensionFactory接口有两个实现类，一个适配类（adaptive，接口的默认实现）。AdaptiveExtensionFactory在内部持有了所有的factory实现工厂，即`SpiExtensionFactory`与`SpringExtensionFactory`两个实现类。一个为SPI工厂（依赖类是扩展接口时发挥作用），一个为Spring工厂（依赖的是springbean时发挥作用）。于是，当需要为某个生成的对象注入依赖时，直接调用此对象即可。从而实现Dubbo SPI的IOC功能

## 8. Dubbo 启动过程源码分析
### 8.1. Spring 框架知识回顾 - BeanDefinition

- 在Java中，一切皆对象。在JDK中使用java.lang.Class来描述类这个对象。
- 而在Spring中，bean对象是操作核心。那么Spring也需要一个东西来描述bean这个对象，它就是BeanDefinition

![BeanDefinition继承类图](images/20200202224653579_25712.jpg)

```java
// 1. 定义Bean信息
RootBeanDefinition beanDef = new RootBeanDefinition();

// 2. 设置BeanDefinition相关属性
beanDef.setBeanClass(DemoServiceImpl.class);
beanDef.setBeanClassName(DemoServiceImpl.class.getName());
beanDef.setScope(BeanDefinition.SCOPE_SINGLETON); // 单例

// 3. 设置bean的属性
MutablePropertyValues propertyValues = beanDef.getPropertyValues();
propertyValues.addPropertyValue("type", "自定义");

// 4. 注册bean到spring容器
applicationContext.registerBeanDefinition("demoService", beanDef);

// 5. 测试获取刚注册的bean功能
DemoService demoService = (DemoService) applicationContext.getBean("demoService");
demoService.sayHello("自定义bean注册");
```

从示例可以看出，最后的spring动作`applicationContext.registerBeanDefinition`会在IOC容器内创建描述的bean对象。后续Dubbo的所有对象创建，皆以此形式委托给Spring来创建

### 8.2. Dubbo的配置解析过程

dubbo的配置解析，不论是xml方式的配置，还是注解方式的配置，目标都是把配置的属性值提取出来，变成dubbo的组件bean（先由BeanDefinition描述，然后委托spring生成组件bean）

下表以xml的标签为例，每个标签配置所对应要解析成为的目标组件bean

![dubbo自定义标签与处理类关系图](images/20200202232758212_30866.jpg)

#### 8.2.1. xml配置的解析过程

1. dubbo自定义了spring标签描述约束文件dubbo.xsd。在dubbo-config-spring模块下的 `src/main/resouce/META-INF`目录下
2. 在spring.handlers、spring.schemas中指定标签解析类，将标签引入spring中管理
3. `DubboBeanDefinitionParser`继承了spring的BeanDefinitionParser接口，spring会调用parse方法来读取每个标签配置，将属性值装入对应的BeanDefinition定义中，后续spring会根据此BeanDefinition定义生成dubbo的组件bean

![xml配置文件解析初始化流程](images/20200202233810397_13273.jpg)

#### 8.2.2. 注解的解析过程

Dubbo注解解析的目标，与xml一致，都是将配置信息变为BeanDefinition定义，交由spring生成组件bean

spring项目启动的时候，最开始的切入点是`@EnableDubbo`注解，此注解用于启用dubbo配置。而此注解又引入`@EnableDubboConfig`和`@DubboComponentScan`两个注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableDubboConfig
@DubboComponentScan
public @interface EnableDubbo {
    @AliasFor(annotation = DubboComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    @AliasFor(annotation = DubboComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};

    @AliasFor(annotation = EnableDubboConfig.class, attribute = "multiple")
    boolean multipleConfig() default false;
}
```

- `@EnableDubboConfig`：主要用于处理dubbo中全局性的组件配置，一般在`.properties`文件中的配置项，如Application/Registry/Protocol/Provider/Consumer
- `@DubboComponentScan`：负责扫描项目源代码，处理业务类上的`@Reference`、`@Service`注解

##### 8.2.2.1. @EnableDubboConfig 注解解析过程

`@EnableDubboConfig`注解主要用来解析属性文件中的配置，一般在springboot项目中比较常用，解析过程如下：

1. 通过`DubboConfigConfigurationSelector`类，连接到`DubboConfigConfiguration`类，此处配置了它支持解析的所有注解组件

```java
public class DubboConfigConfiguration {
    /**
     * Single Dubbo {@link AbstractConfig Config} Bean Binding
     */
    @EnableDubboConfigBindings({
            // 这里定义了properties文件对应的属性前缀的值，设置到相应的类中
            @EnableDubboConfigBinding(prefix = "dubbo.application", type = ApplicationConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.module", type = ModuleConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.registry", type = RegistryConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.protocol", type = ProtocolConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.monitor", type = MonitorConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.provider", type = ProviderConfig.class),
            @EnableDubboConfigBinding(prefix = "dubbo.consumer", type = ConsumerConfig.class)
    })
    public static class Single { }
    ......
}
```

2. 此处为每个dubbo组件绑定了属性文件的前缀值。具体的处理过程在`@EnableDubboConfigBinding`注解中，最终引入到`DubboConfigBindingRegistrar`类来完成组件bean注册

##### 8.2.2.2. @DubboComponentScan 注解解析过程

`@DubboComponentScan`注解是用于解析标注在业务Service实现上的注解，主要是暴露业务服务的`@Service`和引入服务的`@Reference`。总入口在`DubboComponentScanRegistrar`类上。可以看到，两个注解是分开处理的

###### 8.2.2.2.1. @Service注解

- `@Service`注解的处理，最终由`ServiceAnnotationBeanPostProcessor`来处理。dubbo会先调用spring扫描包处理

```java
// ServiceAnnotationBeanPostProcessor类
private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
    ......
    // 标注@Service的类也在Spring容器中实例化
    scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

    for (String packageToScan : packagesToScan) {

        // Registers @Service Bean first
        scanner.scan(packageToScan);
    ......
}
```

```java
// ServiceAnnotationBeanPostProcessor类
 private AbstractBeanDefinition buildServiceBeanDefinition(Service service, Class<?> interfaceClass,
                                                           String annotatedServiceBeanName) {

    BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);

    // 创建封装serviceBean的信息的BeanDefinition对象
    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

    MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

    // 父类组件信息，已在前一步组装完成。这里将以下属性忽略
    String[] ignoreAttributeNames = of("provider", "monitor", "application", "module", "registry", "protocol", "interface");

    propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(service, environment, ignoreAttributeNames));

    // References "ref" property to annotated-@Service Bean
    // 将References标签对应的ref值与Service的BeanName关系绑定起来
    addPropertyReference(builder, "ref", annotatedServiceBeanName);
    // Set interface
    builder.addPropertyValue("interface", interfaceClass.getName());
    ......
}
```

- servicebean有很多父级组件信息引入，这些组件已经在属性文件处理部分完成。
- servicebean中将ref指向业务bean

###### 8.2.2.2.2. @Reference注解

- `@Reference`注解的处理，最终由`ReferenceAnnotationBeanPostProcessor`来处理。
- `ReferenceAnnotationBeanPostProcessor`类继承于`AnnotationInjectedBeanPostProcessor`类，实现了`MergedBeanDefinitionPostProcessor`接口，方法`postProcessMergedBeanDefinition()`在创建bean实例前会被调用（用来找出bean中含有`@Reference`注解的Field和Method）

然后在`metadata.inject(bean, beanName, pvs)`方法中，对字段和方案进行反射绑定。

当Spring完成bean的创建后会调用`AbstractAutowireCapableBeanFactory#populateBean`方法完成属性的填充

### 8.3. ServiceBean与ReferenceBean关系图

![Bean关系图](images/20200205114447730_30887.png)

### 8.4. Dubbo的服务暴露过程

> 源码导读参考官网：http://dubbo.apache.org/zh-cn/docs/source_code_guide/export-service.html

在dubbo的组件中，`ServiceBean`和`ReferenceBean`是比较特殊的。这两个组件，将完成dubbo服务的远程RPC过程

`ServiceBean`作为服务端，会在bean创建成功后，发起服务暴露流程。其过程如下：

- 实现的InitializingBean接口中，spring调用afterPropertiesSet方法，发起服务的暴露动作。

```java
@Override
@SuppressWarnings({"unchecked", "deprecation"})
public void afterPropertiesSet() throws Exception {
    ......
    if (!isDelay()) {
        // 将dubbo服务暴露到rpc网络
        export();
    }
}

@Override
public void export() {
    super.export();
    // Publish ServiceBeanExportedEvent
    publishExportEvent();
}
```

- 父类`ServiceConfig<T>`最终执行此暴露的动作

```java
private void doExportUrlsFor1Protocol(ProtocolConfig protocolConfig, List<URL> registryURLs) {
    ......
    // 包装成服务实例。此ref是ServiceBean的属性，是@Service注解对应的类，目标业务类
    Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString()));
    DelegateProviderMetaDataInvoker wrapperInvoker = new DelegateProviderMetaDataInvoker(invoker, this);

    // 暴露服务
    Exporter<?> exporter = protocol.export(wrapperInvoker);
    exporters.add(exporter);
    ......
}
```

先将本地服务ref包装成Invoker，然后由protocol网络协议将invoker连通到网络上。其核心，即是一旦有protocol网络传输过来的请求，则拉起invoker动作，并将动作传递到ref服务上进行响应。在这个整个过程中，dubbo不希望每一个具体的协议ptotocol去关心目标服务是谁（耦合性太强），于是中间插入了一个invoker概念实体来解耦双方的绑定关系（重点）

#### 8.4.1. Dubbo 框架的 Invoker 机理

- 先明确Dubbo引入Invoker的目标，Invoker的本义是，invoke方法会转嫁到对象到目标ref上，接口定义如下

```java
public interface Invoker<T> extends Node {

    /**
     * get service interface.
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     * @throws RpcException
     */
    /* 实现此接口方法，最终会将这个执行逻辑传递到目标对象ref上 */
    Result invoke(Invocation invocation) throws RpcException;
}
```

- 协议使用方，希望如此调用Invoker：希望dubbo中的所有协议都按此模式，将网络请求转给invoker对象即可，剩下的protocol协议不要去关心

```java
/* 自定义协议示例 */
public class RmiProtocol implements Protocol {

    private static final int DEFAULT_PORT = 1099;

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    /* 以rmi协议为示例，暴露服务 */
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        // 创建spring rmi服务
        final RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();

        // 设置
        rmiServiceExporter.setRegistryPort(invoker.getUrl().getPort());
        rmiServiceExporter.setServiceName(invoker.getUrl().getPath());
        rmiServiceExporter.setServiceInterface(invoker.getInterface());

        /*
         * 此时目标服务没有，需要通过invoker调通，使用动态代理
         *   这里希望通过invoker对象，制作一个target服务代理来使用
         */
        T service = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{invoker.getInterface()},
                new InvokerInvocationHandler(invoker));

        /*
         * 如果能获取目标Service接口，直接设置即可
         *   protocol协议的目标，都是要将调用转到目标target服务上，但是当前环境并没有这个目标target
         */
        rmiServiceExporter.setService(service);
        try {
            rmiServiceExporter.afterPropertiesSet();
        } catch (RemoteException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return null;
    }
    ......
}
```


- Invoker如何转请求到target目标服务呢？就是交给Invoker的实现类去具体实现，如下例，invoker中本身持有目标target服务

```java
URL url = URL.valueOf("rmi://127.0.0.1:9001/" + DemoService.class.getName());

/**
 * Protocol连接服务端invoker
 * 将目标服务调用信息，包装成为invoker实体，暴露到网络上
 * <p>
 * 当网络信息到达，将触发invoker的invoke方法，最终将调用转到目标service上
 */
@Test
public void invoker2protocol() throws IOException {
    DemoService service = new DemoServiceImpl();

    // 这里将目标对象（即DemoService实例）引入到Invoker中
    Invoker<DemoService> invoker = new SimpleInvoker(service, DemoService.class, url);
    Protocol protocol = new RmiProtocol();
    // 暴露对象。协议对象只需要调用即可
    protocol.export(invoker);
    System.out.println("Dubbo server 启动");
    // 保证服务一直开着
    System.in.read();
}
```

- 后续的调用是通过java反射将方法转给target服务

```java
public class SimpleInvoker<T> implements Invoker<T> {
    private T target;
    private Class<T> type;
    private URL url;

    public SimpleInvoker(T service, Class<T> type, URL url) {
        this.target = service;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        Method method = null;
        try {
            method = DemoService.class.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            // 通过反射调用目标target
            return new RpcResult(method.invoke(target, invocation.getArguments()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    ......
}
```

#### 8.4.2. 总结服务暴露调用过程

1. protocol组件收到网络请求到来时，它需要将请求发向target目标服务（如果当前环境中有此服务就好了）。
2. 因为当前环境中没有target对象，于是它创建了一个target的代理对象proxy，将请求转给了此代理proxy对象，而此proxy对象只会干一件事，将调用转给了invoker对象的invoke方法
3. invoker对象发现自己内部有target对象，于是它使用java反射，将请求发向了target服务

> **注：如果让protocol中持有target服务，直接转向请求到target要简单得多，但这样一来，每一个ptotocol服务要对接千千万万的业务service接口，耦合性太强。于是，dubbo专门设计了invoker实体来解开两者间的直接耦合（工作中可否借鉴？）**

### 8.5. Dubbo的服务引入过程

> 源码导读参考官网：http://dubbo.apache.org/zh-cn/docs/source_code_guide/refer-service.html

- Dubbo服务的引入过程，是在Referencebean的实例化过程中实现的。当dubbo启动过程中，遇到`@Reference`，即会创建一个Referencebean的实例
- 此实例一样实现了`InitializingBean`接口，在其调用的afterPropertiesSet方法中，会为服务调用方创建一个远程代理对象
- ref是通过interface和url信息生成的代理

```java
/* ReferenceConfig<T> 类 */
/* 用于获取服务提供的代理对象 */
public synchronized T get() {
    if (destroyed) {
        throw new IllegalStateException("Already destroyed!");
    }
    if (ref == null) {
        init();
    }
    return ref;
}
```

- protocol协议制作了一个invoker对象，你可以通过invoker对象，向protocol协议发送信息（网络传输）。

```java
/* ReferenceConfig<T>#get() --> init() --> T createProxy(Map<String, String> map) */
// 通过网络协议获取一个Invoker对象
invoker = refprotocol.refer(interfaceClass, urls.get(0));
```

- 使用springrmi协议示例来说明一个这个过程

```java
private <T> T doRefer(Class<T> type, URL url) throws RpcException {
    final RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
    if (url.getParameter(Constants.DUBBO_VERSION_KEY, Version.getProtocolVersion()).equals(Version.getProtocolVersion())) {
        rmiProxyFactoryBean.setRemoteInvocationFactory(new RemoteInvocationFactory() {
            @Override
            public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
                return new RmiRemoteInvocation(methodInvocation);
            }
        });
    }
    rmiProxyFactoryBean.setServiceUrl(url.toIdentityString());
    rmiProxyFactoryBean.setServiceInterface(type);
    rmiProxyFactoryBean.setCacheStub(true);
    rmiProxyFactoryBean.setLookupStubOnStartup(true);
    rmiProxyFactoryBean.setRefreshStubOnConnectFailure(true);
    rmiProxyFactoryBean.afterPropertiesSet();
    return (T) rmiProxyFactoryBean.getObject();
}
```

- 一个protocol协议建立后，会得到一个object输出对象，输出到网络信息的动作，都由此对象进行。本来，此对象类型已经是业务接口类型，可以直接使用此对象进行通信了。但是，考虑到protocol本身不应该跟具体的业务接口耦合，于是，dubbo再次插入了invoker实体来解耦双方
    1. 将protocol生成的输出对象object，包装成invoker对象
    2. 在业务操作端，为了方便操作，再做一个代理对象，来转请求到invoker上

```java
/**
 * Protocol连接消费端invoker
 * 将要调用的信息，包装成invoker实体，向网络发送
 * <p>
 * 本地调用接口代理时，最终方法被转到invoker的invoke方法上，向网络发送
 */
@Test
public void protocol2Invoker() {
    // 创建协议
    Protocol protocol = new RmiProtocol();

    // 创建消费端invoker，负责发送协议调用信息
    Invoker<DemoService> invoker = protocol.refer(DemoService.class, url);

    // 做一个动态代理，将调用目标指向invoker即可
    DemoService service = (DemoService) Proxy
            .newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{invoker.getInterface()},
                    new InvokerInvocationHandler(invoker)); // 反射逻辑

    String result = service.sayHello("moon");
    System.out.println(result);
}
```

> **注：Dubbo中的invoker概念，作用不仅仅于此，它统一了dubbo中各组件间相互交流的规范，统一都用invoker进行粘合（书同文、车同轴）**

## 9. 其他
### 9.1. dubbo框架使用示例

dubbo框架使用示例项目参考：D:\code\dubbo-note\dubbo-sample\

### 9.2. 相关RPC服务框架（HSF） -- 网络资料

高速服务框架 HSF (High-speed Service Framework)，是在阿里巴巴内部广泛使用的分布式 RPC 服务框架。

HSF 联通不同的业务系统，解耦系统间的实现依赖。HSF 从分布式应用的层面，统一了服务的发布/调用方式，从而帮助方便、快速的开发分布式应用，以及提供或使用公共功能模块，并屏蔽了分布式领域中的各种复杂技术细节，如：远程通讯、序列化实现、性能损耗、同步/异步调用方式的实现等

HSF 纯客户端架构的 RPC 框架，本身是没有服务端集群的，所有的 HSF 服务调用都是服务消费方（Consumer）与服务提供方（Provider）点对点进行的。然而，为了实现整套分布式服务体系，HSF 还包含了其他组件，完整的组件如下：

**Provider——服务提供者**

通常是某个业务系统，提供相关的业务服务，一般都是个服务集群。业务系统初期使用HSF，通过引入SDK的方式。后期阿里定制化改造了容器(tomcat/docker)，通过配置完成HSF的接入使用，作为架构中的服务提供方。对业务系统本身(war/jar)不造成侵入性。

**Consumer——服务消费者**

通常也是某个业务系统集群，跟服务提供者非常类型，只是服务、消费角度区别。

**ConfigServer——配置服务器**

HSF 依赖注册中心进行服务发现，如果没有注册中心，HSF 只能完成简单的点对点调用。因为作为服务提供端，没有办法将自己的服务信息对外发布，让外界知晓；作为服务消费端，可能已经知道需要调用的服务，但是无法获取能够提供这些服务的机器。而ConfigServer就是服务信息的中介，提供服务发现的能力。

- 注册与订阅
    - 服务注册——在服务提供方启动时，完成该服务的注册——上报当前服务的ip:port、服务类名方法名、版本号、分组等信息。
    - 服务订阅——在服务消费方启动时，完成某些服务的订阅——通过订阅的服务标识（类名+方法名+版本号等），在内存中查找相关服务，一旦找到则将服务提供者的IP端口等信息推送给当前服务消费方，以便消费。
- 长连接与内存级
    - 服务提供方与服务消费方均与配置服务器保持长连接，一旦服务提供方发生变化，会立即被通知，更新内存中的列表数据，并同步给订阅该服务的服务消费方。

**Diamond——持久化配置中心**

持久化的配置中心用于存储 HSF 服务的各种治理规则，HSF 客户端在启动的过程中会向持久化配置中心订阅各种服务治理规则，如白名单、权限、路由规则、归组规则、权重规则等，从而根据规则对调用过程的选址逻辑进行干预。持久化配置中心的角色是由 Diamond组件承担的。

当需要时，用户通过在界面上设计规则，Diamond会快速的将新的规则推送给相关的服务提供方

**addressServer——地址服务**

地址服务器的职责是保存上文的ConfigServer与Diamond服务器的地址列表。在consumer、provider启动时，会首先以域名访问的方式访问地址服务器，从地址服务器获取到ConfigServer与Diamond服务器的地址列表信息，以便访问。

**元数据存储**

并没有在图中展示出来。元数据是指 HSF 服务对应的方法列表以及参数结构等信息，元数据不会对 HSF 的调用过程产生影响，因此元数据存储中心也并不是必须的。但考虑到服务运维的便捷性，HSF客户端在启动时会将元数据上报到元数据存储中心，以便提供给服务运维使用。元数据存储中心的角色是由 Redis 承担的。

完成初始化后，服务调用者内保存了相关的服务信息，通过内部设置的调用方式（如随机访问一台，权重等）可以进行点对点的调用。其内部通过NIO多路复用（Netty）+Hessian序列化实现RPC调用，使用Hessian主要是压测表现出稳定性以及高效的综合特性。

