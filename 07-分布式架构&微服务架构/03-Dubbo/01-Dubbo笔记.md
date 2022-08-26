# Dubbo 基础笔记

## 1. RPC 场景和过程

### 1.1. RPC 场景

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
info.put("target", "com.moon.dubbo.service.OrderService");
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
        info.put("target", "com.moon.dubbo.service.OrderService");
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
                info.put("target", "com.moon.dubbo.service.OrderService"); // 调用对象
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

## 2. Dubbo 简介

- Apache Dubbo™ 是一款高性能Java RPC框架
- 官网：https://dubbo.apache.org/zh/

### 2.1. Dubbo 实现RPC框架

分布式服务架构下，各个服务间的相互RPC调用会越来越复杂。最终形成网状结构，此时服务的治理极为关键

Dubbo 是一个带有服务治理功能的 RPC 框架，提供了一套较为完整的服务治理方案，其底层直接实现了 RPC 调用的全过程，并尽力使 RPC 远程对使用者透明。下图展示了Dubbo服务治理的功能

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

- **Service 服务接口层**：该层是与实际业务逻辑相关的，根据服务提供方和服务消费方的业务设计对应的接口和实现
- **Config 配置层**：对外配置接口，以 `ServiceConfig`, `ReferenceConfig` 为中心，可以直接初始化配置类，也可以通过 spring 解析配置生成配置类
- **Proxy 服务代理层**：服务接口透明代理，生成服务的客户端 Stub 和服务器端 Skeleton, 以 `ServiceProxy` 为中心，扩展接口为 `ProxyFactory`
- **Registry 注册中心层**：封装服务地址的注册与发现，以服务 URL 为中心，扩展接口为 `RegistryFactory`, `Registry`, `RegistryService`。可能没有服务注册中心，此时服务提供方直接暴露服务。
- **Cluster 路由层**：封装多个提供者的路由及负载均衡，并桥接注册中心，以 `Invoker` 为中心，扩展接口为 `Cluster`, `Directory`, `Router`, `LoadBalance`。将多个服务提供方组合为一个服务提供方，实现对服务消费方透明，只需要与一个服务提供方进行交互。
- **Monitor 监控层**：RPC 调用次数和调用时间监控，以 `Statistics` 为中心，扩展接口为 `MonitorFactory`, `Monitor`, `MonitorService`
- **Protocol 远程调用层**：封装 RPC 调用，以 `Invocation`, `Result` 为中心，扩展接口为 `Protocol`, `Invoker`, `Exporter`
    - Protocol 是服务域，它是 Invoker 暴露和引用的主功能入口，它负责 Invoker 的生命周期管理。
    - Invoker 是实体域，它是 Dubbo 的核心模型，其他模型都向它靠扰，或转换成它，它代表一个可执行体，可向它发起invoke调用。它有可能是一个本地的实现，也可能是一个远程的实现，也可能是一个集群实现。
- **Exchange 信息交换层**：封装请求响应模式，同步转异步，以 `Request`, `Response` 为中心，扩展接口为 `Exchanger`, `ExchangeChannel`, `ExchangeClient`, `ExchangeServer`
- **Transport 网络传输层**：抽象 mina 和 netty 为统一接口，以 `Message` 为中心，扩展接口为 `Channel`, `Transporter`, `Client`, `Server`, `Codec`
- **Serialize 数据序列化层**：可复用的一些工具，扩展接口为 `Serialization`, `ObjectInput`, `ObjectOutput`, `ThreadPool`

从上图可以看出，Dubbo对于服务提供方和服务消费方，从框架的10层中分别提供了各自需要关心和扩展的接口，构建整个服务生态系统(服务提供方和服务消费方本身就是一个以服务为中心的)

### 2.3. Dubbo 服务的角色关系

服务提供方和服务消费方之间的调用关系，如图所示：

![提供方和消费方的调用关系](images/20200125184203559_27446.png)

节点角色说明

|   节点    |              角色说明               |
| --------- | ---------------------------------- |
| Provider  | 暴露服务的服务提供方                 |
| Consumer  | 调用远程服务的服务消费方             |
| Registry  | 服务注册与发现的注册中心             |
| Monitor   | 统计服务的调用次数和调用时间的监控中心 |
| Container | 服务运行容器                        |

调用关系说明

0. 服务容器负责启动，加载，运行服务提供者。
1. 服务提供者在启动时，向注册中心注册自己提供的服务。
2. 服务消费者在启动时，向注册中心订阅自己所需的服务。
3. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
4. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
5. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心

## 3. Dubbo 基础配置使用

### 3.1. xml 文件配置方式

#### 3.1.1. dubbo 标签关系图

![](images/20200126122444209_30129.jpg)

> 1. 标签属性有继承关系，即：下层有设置则使用，未配置则沿用上一级的设置
> 2. timeout/retries/loadbalance消费方未设置，则沿用服务方的设置

#### 3.1.2. dubbo 各标签作用汇总表

|          标签          |    用途     |                                          解释                                          |
| ---------------------- | ----------- | ------------------------------------------------------------------------------------- |
| `<dubbo:service/>`     | 服务配置     | 用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心 |
| `<dubbo:reference/>`   | 引用配置     | 用于创建一个远程服务代理，一个引用可以指向多个注册中心                                      |
| `<dubbo:protocol/>`    | 协议配置     | 用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受                                 |
| `<dubbo:application/>` | 应用配置     | 用于配置当前应用信息，不管该应用是提供者还是消费者                                          |
| `<dubbo:module/>`      | 模块配置     | 用于配置当前模块信息，可选                                                               |
| `<dubbo:registry/>`    | 注册中心配置 | 用于配置连接注册中心相关信息                                                             |
| `<dubbo:monitor/>`     | 监控中心配置 | 用于配置连接监控中心相关信息，可选                                                        |
| `<dubbo:provider/>`    | 提供方配置   | 当 `ProtocolConfig` 和 `ServiceConfig` 某属性没有配置时，采用此缺省值，可选                |
| `<dubbo:consumer/>`    | 消费方配置   | 当 `ReferenceConfig` 某属性没有配置时，采用此缺省值，可选                                  |
| `<dubbo:method/>`      | 方法配置     | 用于 `ServiceConfig` 和 `ReferenceConfig` 指定方法级的配置信息                           |
| `<dubbo:argument/>`    | 参数配置     | 用于指定方法参数配置                                                                     |

#### 3.1.3. 标签详解

所有配置项分为三大类，参见以下各个标签作用表中的"作用"一列

- 服务发现：表示该配置项用于服务的注册与发现，目的是让消费方找到提供方
- 服务治理：表示该配置项用于治理服务间的关系，或为开发测试提供便利条件
- 性能调优：表示该配置项用于调优性能，不同的选项对性能会产生影响
- 所有配置最终都将转换为URL表示，并由服务提供方生成，经注册中心传递给消费方，各属性对应URL的参数，参见配置项一览表中的"对应URL参数"列。

> URL 格式：`protocol://username:password@host:port/path?key=value&key=value`

**注意：只有group，interface，version是服务的匹配条件，三者决定是不是同一个服务，其它配置项均为调优和治理参数。**

> **各个标签详细属性配置参考官网：http://dubbo.apache.org/zh-cn/docs/user/references/xml/introduction.html**

##### 3.1.3.1. dubbo:service【常用】

服务提供者暴露服务配置。对应的配置类：`org.apache.dubbo.config.ServiceConfig`。常用属性如下：

|   属性    | 对应URL参数 |  类型  | 是否必填  | 缺省值 |   作用   |                                         描述                                          | 兼容性  |
| --------- | ---------- | ------ | -------- | ----- | ------- | ------------------------------------------------------------------------------------ | ------ |
| interface |            | class  | **必填** |       | 服务发现 | 服务接口名                                                                            | 1.0.0+ |
| ref       |            | object | **必填** |       | 服务发现 | 服务对象实现引用                                                                       | 1.0.0+ |
| version   | version    | string | 可选     | 0.0.0 | 服务发现 | 服务版本，建议使用两位数字版本，如：1.0，通常在接口不兼容时版本号才需要升级                  | 1.0.0+ |
| timeout   | timeout    | int    | 可选     | 1000  | 性能调优 | 远程服务调用超时时间(毫秒)                                                              | 2.0.0+ |
| protocol  |            | string | 可选     |       | 配置关联 | 使用指定的协议暴露服务，在多协议时使用，值为`<dubbo:protocol>`的id属性，多个协议ID用逗号分隔 | 2.0.5+ |

> 全部属性列表详见官方文档（2.7版本）：https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-service/

##### 3.1.3.2. dubbo:reference【常用】

服务消费者引用服务配置。对应的配置类：`org.apache.dubbo.config.ReferenceConfig`。常用属性如下：

|   属性    | 对应URL参数 |  类型  | 是否必填 | 缺省值 |   作用   |      描述      |    兼容性     |
| --------- | ----------- | ------ | ------- | ----- | ------- | ------------- | ------------ |
| id        |             | string | **必填**     |       | 配置关联 | 服务引用BeanId | 1.0.0以上版本 |
| interface |             | class  | **必填**     |       | 服务发现 | 服务接口名     | 1.0.0以上版本 |

##### 3.1.3.3. dubbo:protocol【常用】

服务提供者协议配置。对应的配置类：`org.apache.dubbo.config.ProtocolConfig`。同时，如果需要支持多协议，可以声明多个 `<dubbo:protocol>` 标签，并在 `<dubbo:service>` 中通过 protocol 属性指定使用的协议。常用属性如下：

| 属性 |  对应URL参数  |  类型  | 是否必填 |                                                                                                       缺省值                                                                                                       |   作用   |                                                     描述                                                     |    兼容性     |
| ---- | ------------ | ------ | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------- | ----------------------------------------------------------------------------------------------------------- | ------------ |
| id   |              | string | 可选     | dubbo                                                                                                                                                                                                              | 配置关联 | 协议BeanId，可以在`<dubbo:service protocol="">`中引用此ID，如果ID不填，缺省和name属性值一样，重复则在name后加序号。 | 2.0.5以上版本 |
| name | `<protocol>` | string | **必填**     | dubbo                                                                                                                                                                                                              | 性能调优 | 协议名称                                                                                                     | 2.0.5以上版本 |
| port | `<port>`     | int    | 可选     | dubbo协议缺省端口为20880，rmi协议缺省端口为1099，http和hessian协议缺省端口为80；如果没有配置port，则自动采用默认端口，如果配置为-1，则会分配一个没有被占用的端口。Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。 | 服务发现 | 服务端口                                                                                                     | 2.0.5以上版本 |
| host | `<host>`     | string | 可选     | 自动查找本机IP                                                                                                                                                                                                      | 服务发现 | -服务主机名，多网卡选择或指定VIP及域名时使用，为空则自动查找本机IP，-**建议不要配置，让Dubbo自动获取本机IP**         | 2.0.5以上版本 |

##### 3.1.3.4. dubbo:registry【常用】

注册中心配置。对应的配置类：`org.apache.dubbo.config.RegistryConfig`。同时如果有多个不同的注册中心，可以声明多个 `<dubbo:registry>` 标签，并在 `<dubbo:service>` 或 `<dubbo:reference>` 的 registry 属性指定使用的注册中心。常用属性如下：

|   属性   |  对应URL参数   |  类型  | 是否必填  | 缺省值 |   作用   |                                                                      描述                                                                       |     兼容性     |
| -------- | ------------- | ------ | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| id       |               | string | 可选     |       | 配置关联 | 注册中心引用BeanId，可以在`<dubbo:service registry="">`或`<dubbo:reference registry="">`中引用此ID                                                | 1.0.16以上版本 |
| address  | `<host:port>` | string | **必填** |       | 服务发现 | 注册中心服务器地址，如果地址没有端口缺省为9090，同一集群内的多个地址用逗号分隔，如：`ip:port,ip:port`，不同集群的注册中心，请配置多个`<dubbo:registry>`标签 | 1.0.16以上版本 |
| protocol | `<protocol>`  | string | 可选     | dubbo | 服务发现 | 注册中心地址协议，支持dubbo, multicast, zookeeper, redis, consul(2.7.1), sofa(2.7.2), etcd(2.7.2), nacos(2.7.2)等协议                             | 2.0.0以上版本  |


##### 3.1.3.5. dubbo:monitor

监控中心配置。对应的配置类：`org.apache.dubbo.config.MonitorConfig`

##### 3.1.3.6. dubbo:application【常用】

应用信息配置。对应的配置类：`org.apache.dubbo.config.ApplicationConfig`。常用属性如下：

| 属性 | 对应URL参数  |  类型  | 是否必填  | 缺省值 |   作用   |                                                                                                                                                       描述                                                                                                                                                        |     兼容性     |
| ---- | ----------- | ------ | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| name | application | string | **必填** |       | 服务治理 | 当前应用名称，用于注册中心计算应用间依赖关系，**注意：消费者和提供者应用名不要一样，此参数不是匹配条件**，你当前项目叫什么名字就填什么，和提供者消费者角色无关，比如：kylin应用调用了morgan应用的服务，则kylin项目配成kylin，morgan项目配成morgan，可能kylin也提供其它服务给别人使用，但kylin项目永远配成kylin，这样注册中心将显示kylin依赖于morgan | 1.0.16以上版本 |


##### 3.1.3.7. dubbo:module

模块信息配置。对应的配置类：`org.apache.dubbo.config.ModuleConfig`

##### 3.1.3.8. dubbo:provider

服务提供者缺省值配置。对应的配置类：`org.apache.dubbo.config.ProviderConfig`。同时该标签为 `<dubbo:service>` 和 `<dubbo:protocol>` 标签的缺省值设置

##### 3.1.3.9. dubbo:consumer

服务消费者缺省值配置。配置类：`org.apache.dubbo.config.ConsumerConfig`。同时该标签为 `<dubbo:reference>` 标签的缺省值设置

##### 3.1.3.10. dubbo:method

方法级配置。对应的配置类：`org.apache.dubbo.config.MethodConfig`。同时该标签为 `<dubbo:service>` 或 `<dubbo:reference>` 的子标签，用于控制到方法级。如下例：

```xml
<dubbo:reference interface="com.xxx.XxxService">
    <dubbo:method name="findXxx" timeout="3000" retries="2" />
</dubbo:reference>
```

##### 3.1.3.11. dubbo:argument

方法参数配置。对应的配置类：`org.apache.dubbo.config.ArgumentConfig`。该标签为 `<dubbo:method>` 的子标签，用于方法参数的特征描述。如下例：

```xml
<dubbo:method name="findXxx" timeout="3000" retries="2">
    <dubbo:argument index="0" callback="true" />
</dubbo:method>
```

##### 3.1.3.12. dubbo:parameter

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

##### 3.1.3.13. dubbo:config-center

配置中心。对应的配置类：`org.apache.dubbo.config.ConfigCenterConfig`

#### 3.1.4. xml配置使用示例

> - xml的配置使用示例。详细参考dubbo-sample-xml工程
> - Springmvc的集成Dubbo的使用示例，详细参考busi-mvc工程

### 3.2. 属性配置方式

如果项目应用足够简单，例如，不需要多注册中心或多协议，并且需要在spring容器中共享配置。可以直接使用 `dubbo.properties` 作为默认配置。

### 3.3. 注解配置方式

> 详细示例参考：dubbo-sample-annotation工程

- 注解方式的底层与XML一致，只是表现形式上的不同。
- 目标都是配置Dubbo基础信息，主要涉及以下五个必不可少的信息：`ApplicationConfig`、`ProtocolConfig`、`RegistryConfig`、`service`、`reference`

#### 3.3.1. @EnableDubbo 开启服务

`@EnableDubbo` 注解：开启注解 Dubbo 功能，其中可以加入 `scanBasePackages` 属性配置包扫描的路径，用于扫描并注册bean。其中封装了组件 `@DubboComponentScan`，来扫描Dubbo框架的 `@Service` 注解暴露 Dubbo 服务，以及扫描 Dubbo 框架的 `@Reference` 字段或者方法注入 Dubbo 服务代理。

#### 3.3.2. @Configuration 方式配置公共信息

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
        applicationConfig.setName("dubbo-annotation-provider");
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

#### 3.3.3. property 属性配置方式自动装配公共信息

`@PropertySource`注解方式：使用Springboot属性文件方式，由Dubbo自动将文件信息配置入容器，就类似spring boot自动装配一样，提供者示例如下：

1. 创建dubbo-provider.properties文件

```properties
dubbo.application.name=dubbo-annotation-property-provider
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

### 3.4. API 配置方式

以API 配置的方式来配置你的 Dubbo 应用

> 详细示例参考：dubbo-sample-api工程

API 属性与xml配置项一对一，各属性含义。详情参考官网：https://dubbo.apache.org/zh/docs/v2.7/user/configuration/api/

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
        config.setApplication(new ApplicationConfig("dubbo-api-provider"));
        // 设置注册中心，多个注册中心可以用setRegistries()
        config.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        config.setInterface(OrderService.class);
        config.setRef(new OrderServiceImpl());
        // 暴露及注册服务
        config.export();
        System.out.println("dubbo-api-provider is running...");
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
        reference.setApplication(new ApplicationConfig("dubbo-api-consumer"));
        // 设置注册中心，多个注册中心可以用setRegistries()
        reference.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        reference.setInterface(OrderService.class);
        System.out.println("dubbo-api-consumer is running...");
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
# 如果打包不成功，则尝试跳过test。mvn clean package -Dmaven.test.skip=true
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

# Dubbo 高级特性进阶 - 常用配置策略用法

## 1. 启动时检查

Dubbo缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止Spring初始化完成，以便上线时，能及早发现问题，默认`check="true"`

可以通过`check="false"`关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动

另外，如果Spring容器是懒加载的，或者通过API编程延迟引用服务，也需要关闭`check`属性，否则服务临时不可用时，会抛出异常，拿到null引用，如果`check="false"`，总是会返回引用，当服务恢复时，能自动连上

### 1.1. 通过 spring 配置文件

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

### 1.2. 通过 dubbo.properties

```properties
dubbo.reference.com.foo.BarService.check=false
dubbo.reference.check=false # 强制改变所有 reference 的 check 值，就算配置中有声明，也会被覆盖
dubbo.consumer.check=false # 是设置 check 的缺省值，如果配置中有显式的声明，如：<dubbo:reference check="true"/>，不会受影响
dubbo.registry.check=false # 前面两个都是指订阅成功，但提供者列表是否为空是否报错，如果注册订阅失败时，也允许启动，需使用此选项，将在后台定时重试
```

### 1.3. 通过 -D 参数

```bash
java -Ddubbo.reference.com.foo.BarService.check=false
java -Ddubbo.reference.check=false
java -Ddubbo.consumer.check=false
java -Ddubbo.registry.check=false
```

### 1.4. 配置的含义

- `dubbo.reference.check=false`，强制改变所有 reference 的 check 值，就算配置中有声明，也会被覆盖。
- `dubbo.consumer.check=false`，是设置 check 的缺省值，如果配置中有显式的声明，如：`<dubbo:reference check="true"/>`，不会受影响。
- `dubbo.registry.check=false`，前面两个都是指订阅成功，但提供者列表是否为空是否报错，如果注册订阅失败时，也允许启动，需使用此选项，将在后台定时重试。

## 2. Dubbo超时重连

Dubbo 服务在尝试调用一次之后，如出现非业务异常(服务突然不可用、超时等)，Dubbo 默认会进行额外的最多2次重试。重试次数支持两种自定义配置：

1. 通过注解或者xml配置进行固定配置
2. 通过上下文进行运行时动态配置

### 2.1. 重试次数配置

Dubbo消费端在发出请求后，需要有一个临界时间界限来判断服务端是否正常。这样消费端达到超时时间，那么Dubbo会进行重试机制，不合理的重试在一些特殊的业务场景下可能会引发很多问题，需要合理设置接口超时时间。Dubbo超时和重试配置示例如下：

- xml配置

```xml
<!-- 服务调用超时设置为5秒，超时不重试 -->
<dubbo:reference id="xxxService" interface="com.xxx.XxxService" retries="0" timeout="5000"/>
```

- 注解配置

```java
@Reference(retries = 3)
private XxxService xxxService;
```

- 通过RpcContext进行运行时动态配置，优先级高于注解或者xml进行的固定配置(两者都配置的情况下，以RpcContext配置为准)

```java
// dubbo服务调用前，通过RpcContext动态设置本次调用的重试次数
RpcContext rpcContext = RpcContext.getContext();
rpcContext.setAttachment("retries", 5);
```

### 2.2. 重试机制注意点

- Dubbo在调用服务不成功时，*默认会重试2次，即`retries="2"`*。通过设置`<dubbo:reference>`标签中`retries="0"`属性控制重试次数
- Dubbo的路由机制，会把超时的请求路由到其他机器上，而不是本机尝试，所以Dubbo的重试机制也能一定程度的保证服务的质量

## 3. 集群容错

当消费端某次调用失败是一些环境偶然因素造成的（如网络抖动），dubbo还给予了容错补救机会。在集群调用失败时，Dubbo 提供了多种容错方案，缺省为 `failover` 重试。容错方案关系图如下：

![](images/20200129162636712_10255.png)

各节点关系：

- 这里的 `Invoker` 是 `Provider` 的一个可调用 `Service` 的抽象，`Invoker` 封装了 `Provider` 地址及 `Service` 接口信息
- `Directory` 代表多个` Invoker`，可以把它看成 `List<Invoker>` ，但与 `List` 不同的是，它的值可能是动态变化的，比如注册中心推送变更
- `Cluster` 将 `Directory` 中的多个 `Invoker` 伪装成一个 `Invoker`，对上层透明，伪装过程包含了容错逻辑，调用失败后，重试另一个
- `Router` 负责从多个 `Invoker` 中按路由规则选出子集，比如读写分离，应用隔离等
- `LoadBalance` 负责从多个 `Invoker` 中选出具体的一个用于本次调用，选的过程包含了负载均衡算法，调用失败后，需要重选

### 3.1. 集群容错模式

#### 3.1.1. Failover Cluster

失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 `retries="2"` 来设置重试次数(不含第一次)。

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

> <font color=red>**注：如果服务提供方与消费方都设置了重试次数，最终与消费方的重试次数为准**</font>

#### 3.1.2. Failfast Cluster

快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录

#### 3.1.3. Failsafe Cluster

失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

#### 3.1.4. Failback Cluster

失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

#### 3.1.5. Forking Cluster

并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 `forks="2"` 来设置最大并行数。

#### 3.1.6. Broadcast Cluster

广播调用所有提供者，逐个调用，任意一台报错则报错。通常用于通知所有提供者更新缓存或日志等本地资源信息

### 3.2. 集群模式配置

按照以下示例在服务提供方和消费方配置集群模式

```xml
<dubbo:service cluster="failsafe" />
<dubbo:reference cluster="failsafe" />
```

## 4. 负载均衡

在集群负载均衡时，Dubbo 提供了多种均衡策略，缺省为 `random` 随机调用

### 4.1. 负载均衡策略

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
    - 缺省只对第一个参数 Hash，如果要修改，请配置 `<dubbo:parameter key="hash.arguments" value="0,1" />`
    - 缺省用 160 份虚拟节点，如果要修改，请配置 `<dubbo:parameter key="hash.nodes" value="320" />`

> 注意：配置时负载均衡策略的单词都是**全小写**，如果出现大写会报错

### 4.2. 配置示例

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

## 5. 线程模型

### 5.1. 配置 Dubbo 中的线程模型

如果事件处理的逻辑能迅速完成，并且不会发起新的 IO 请求，比如只是在内存中记个标识，则直接在 IO 线程上处理更快，因为减少了线程池调度。

但如果事件处理逻辑较慢，或者需要发起新的 IO 请求，比如需要查询数据库，则必须派发到线程池，否则 IO 线程阻塞，将导致不能接收其它请求。

如果用 IO 线程处理事件，又在事件处理过程中发起新的 IO 请求，比如在连接事件中发起登录请求，会报“可能引发死锁”异常，但不会真死锁。

![](images/20210712214936913_20605.png)

需要通过不同的派发策略和不同的线程池配置的组合来应对不同的场景：

```xml
<dubbo:protocol name="dubbo" dispatcher="all" threadpool="fixed" threads="100" />
```

### 5.2. Dispatcher 属性

- `all` 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
- `direct` 所有消息都不派发到线程池，全部在 IO 线程上直接执行。
- `message` 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
- `execution` 只有请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
- `connection` 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池。

### 5.3. ThreadPool 属性

- `fixed` 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
- `cached` 缓存线程池，空闲一分钟自动删除，需要时重建。
- `limited` 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
- `eager` 优先创建Worker线程池。在任务数量大于`corePoolSize`但是小于`maximumPoolSize`时，优先创建Worker来处理任务。当任务数量大于`maximumPoolSize`时，将任务放入阻塞队列中。阻塞队列充满时抛出`RejectedExecutionException`。(相比于`cached`:`cached`在任务数量超过`maximumPoolSize`时直接抛出异常而不是将任务放入阻塞队列)

## 6. 直连提供者

在开发及测试环境下，经常需要绕过注册中心，只测试指定服务提供者，这时候可能需要点对点直连，点对点直连方式，将以服务接口为单位，忽略注册中心的提供者列表，A 接口配置点对点，不影响 B 接口从注册中心获取列表。

> <font color=red>**为了避免复杂化线上环境，不要在线上使用这个功能，只应在测试阶段使用。**</font>

### 6.1. 通过 XML 配置

如果是线上需求需要点对点，可在 `<dubbo:reference>` 中配置 url 指向提供者，将绕过注册中心，多个地址用分号隔开，配置如下：

```xml
<dubbo:reference id="xxxService" interface="com.alibaba.xxx.XxxService" url="dubbo://localhost:20890" />
```

### 6.2. 通过 -D 参数指定

在JVM启动参数中加入`-D`参数映射服务地址，如：

```bash
java -Dcom.alibaba.xxx.XxxService=dubbo://localhost:20890
```

> key 为服务名，value 为服务提供者 url，此配置优先级最高

### 6.3. 通过文件映射

如果服务比较多，也可以用文件映射，用 `-Ddubbo.resolve.file` 指定映射文件路径，此配置优先级高于 `<dubbo:reference>` 中的配置，如：

```bash
java -Ddubbo.resolve.file=xxx.properties
```

然后在映射文件 xxx.properties 中加入配置，其中 key 为服务名，value 为服务提供者 URL：

```bash
com.alibaba.xxx.XxxService=dubbo://localhost:20890
```

> 1.0.15 及以上版本支持，2.0 以上版本自动加载`${user.home}/dubbo-resolve.properties`文件，不需要配置

## 7. 只订阅

### 7.1. 只订阅不注册

场景：在本地开发的时候，不能把自己机器的未开发好的服务注册到开发环境，但是又需要使用注册中心的其他服务。服务提供者配置禁止注册`register="false"`

为方便开发测试，经常会在线下共用一个所有服务可用的注册中心，这时，如果一个正在开发中的服务提供者注册，可能会影响消费者不能正常运行。可以让服务提供者开发方，只订阅服务(开发的服务可能依赖其它服务)，而不注册正在开发的服务，通过直连测试正在开发的服务。

![](images/20210714204839930_470.png)

禁用注册配置如下：

```xml
<dubbo:registry address="10.20.153.10:9090" register="false" />
<!-- 或者 -->
<dubbo:registry address="10.20.153.10:9090?register=false" />
```

### 7.2. 只注册(已废弃？)

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

## 8. 多协议

Dubbo 允许配置多协议，在不同服务上支持不同协议或者同一服务上同时支持多种协议。

### 8.1. 不同服务不同协议

不同服务在性能上适用不同协议进行传输，比如大数据用短连接协议，小数据大并发用长连接协议

```xml
<dubbo:application name="world"  />
<dubbo:registry id="registry" address="10.20.141.150:9090" username="admin" password="hello1234" />
<!-- 多协议配置 -->
<dubbo:protocol name="dubbo" port="20880" />
<dubbo:protocol name="rmi" port="1099" />
<!-- 使用dubbo协议暴露服务 -->
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" protocol="dubbo" />
<!-- 使用rmi协议暴露服务 -->
<dubbo:service interface="com.alibaba.hello.api.DemoService" version="1.0.0" ref="demoService" protocol="rmi" />
```

### 8.2. 多协议暴露服务

同一个服务，使用多个协议暴露

```xml
<!-- 多协议配置 -->
<dubbo:protocol name="dubbo" port="20880" />
<dubbo:protocol name="hessian" port="8080" />
<!-- 使用多个协议暴露服务 -->
<dubbo:service id="helloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" protocol="dubbo,hessian" />
```

## 9. 多注册中心

Dubbo 支持同一服务向多注册中心同时注册，或者不同服务分别注册到不同的注册中心上去，甚至可以同时引用注册在不同注册中心上的同名服务。另外，注册中心是支持自定义扩展的。

### 9.1. 多注册中心注册

比如：中文站有些服务来不及在青岛部署，只在杭州部署，而青岛的其它应用需要引用此服务，就可以将服务同时注册到两个注册中心。

```xml
dubbo:application name="world" />
<!-- 多注册中心配置 -->
<dubbo:registry id="hangzhouRegistry" address="10.20.141.150:9090" />
<dubbo:registry id="qingdaoRegistry" address="10.20.141.151:9010" default="false" />
<!-- 向多个注册中心注册 -->
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" registry="hangzhouRegistry,qingdaoRegistry" />
```

### 9.2. 不同服务使用不同注册中心

比如：CRM 有些服务是专门为国际站设计的，有些服务是专门为中文站设计的。

```xml
<dubbo:application name="world"  />
<!-- 多注册中心配置 -->
<dubbo:registry id="chinaRegistry" address="10.20.141.150:9090" />
<dubbo:registry id="intlRegistry" address="10.20.154.177:9010" default="false" />
<!-- 向中文站注册中心注册 -->
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" registry="chinaRegistry" />
<!-- 向国际站注册中心注册 -->
<dubbo:service interface="com.alibaba.hello.api.DemoService" version="1.0.0" ref="demoService" registry="intlRegistry" />
```

### 9.3. 多注册中心引用

比如：CRM 需同时调用中文站和国际站的 PC2 服务，PC2 在中文站和国际站均有部署，接口及版本号都一样，但连的数据库不一样。

```xml
<dubbo:application name="world"  />
<!-- 多注册中心配置 -->
<dubbo:registry id="chinaRegistry" address="10.20.141.150:9090" />
<dubbo:registry id="intlRegistry" address="10.20.154.177:9010" default="false" />
<!-- 引用中文站服务 -->
<dubbo:reference id="chinaHelloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" registry="chinaRegistry" />
<!-- 引用国际站站服务 -->
<dubbo:reference id="intlHelloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" registry="intlRegistry" />
```

如果只是测试环境临时需要连接两个不同注册中心，使用竖号分隔多个不同注册中心地址：

```xml
<dubbo:application name="world" />
<!-- 多注册中心配置，竖号分隔表示同时连接多个不同注册中心，同一注册中心的多个集群地址用逗号分隔 -->
<dubbo:registry address="10.20.141.150:9090|10.20.154.177:9010" />
<!-- 引用服务 -->
<dubbo:reference id="helloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" />
```

## 10. 服务分组

**使用服务分组区分服务接口的不同实现**

如果想在测试、开发环境等多套环境中共用同一个注册中心。或者当一个接口有多种实现时，可以用 group 区分

- 服务提供方

```xml
<dubbo:service group="feedback" interface="com.xxx.IndexService" />
<dubbo:service group="member" interface="com.xxx.IndexService" />
```

- 引用消费方

```xml
<dubbo:reference id="feedbackIndexService" group="feedback" interface="com.xxx.IndexService" />
<dubbo:reference id="memberIndexService" group="member" interface="com.xxx.IndexService" />
```

- 任意组：2.2.0 以上版本支持，总是只调一个可用组的实现

```xml
<dubbo:reference id="barService" interface="com.foo.BarService" group="*" />
```

## 11. 分组聚合

dubbo提供了通过分组对结果进行聚合并返回聚合后的结果的功能。用`group`区分同一接口的多种实现，现在消费方需从每种`group`中调用一次并返回结果，对结果进行合并之后返回

dubbo提供了以下类型的合并的实现，是根据服务接口的返回值的类型去找相应的实现

> <font color=red>**值得注意：这个服务返回值合并只是一次PRC调用，如果其中某个服务执行失败，则调用结果失败。**</font>

![](images/20210717095903124_3599.png)

### 11.1. 配置示例

- 搜索所有分组

```xml
<dubbo:reference interface="com.xxx.MenuService" group="*" merger="true" />
```

- 合并指定分组

```xml
<dubbo:reference interface="com.xxx.MenuService" group="aaa,bbb" merger="true" />
```

- 指定方法合并结果，其它未指定的方法，将只调用一个 Group

```xml
<dubbo:reference interface="com.xxx.MenuService" group="*">
    <dubbo:method name="getMenuItems" merger="true" />
</dubbo:reference>
```

- 某个方法不合并结果，其它都合并结果

```xml
<dubbo:reference interface="com.xxx.MenuService" group="*" merger="true">
    <dubbo:method name="getMenuItems" merger="false" />
</dubbo:reference>
```

- 指定合并策略，缺省根据返回值类型自动匹配，如果同一类型有两个合并器时，需指定合并器的名称

```xml
<dubbo:reference interface="com.xxx.MenuService" group="*">
    <dubbo:method name="getMenuItems" merger="mymerge" />
</dubbo:reference>
```

- 指定合并方法，将调用返回结果的指定方法进行合并，合并方法的参数类型必须是返回结果类型本身

```xml
<dubbo:reference interface="com.xxx.MenuService" group="*">
    <dubbo:method name="getMenuItems" merger=".addAll" />
</dubbo:reference>
```

### 11.2. 聚合实现示例

官方没有提供`String`类型的实现，如合并字符串类型，需要自己实现。具体的实现步骤如下：

- 根据dubbo的SPI机制（参考`com.alibaba.dubbo.rpc.cluster.Merger`文件），在创建与官方一样的文件`com.alibaba.dubbo.rpc.cluster.Merger`，定义字符串合并与实现类的映射

![](images/20210717101939101_13028.png)

- 编写合并逻辑的实现类，需要实现dubbo的`Merger<T>`接口。

```java
public class StringMerger implements Merger<String> {

    /**
     * 定义了所有group实现类返回值的合并规则。
     * 注：此示例简单实现
     *
     * @param items
     * @return
     */
    @Override
    public String merge(String... items) {
        if (items.length == 0) {
            return null;
        }
        StringJoiner joiner = new StringJoiner("|", "[", "]");
        for (String s : items) {
            joiner.add(s);
        }
        return joiner.toString();
    }
}
```

- 服务引用配置`group`属性，通过 parameters 属性修改为 `{"merger", "true"}`，开启返回结果合并

```java
@RestController
@RequestMapping("group")
public class GroupController {

    /*
     * group属性：指定相应的服务接口的实现
     *          如果取值为"*"，则代表任意组，是随机调用不同的实现
     * 如果需要将服务分组的返回结果进行合并，只需修改 parameters 属性，{"merger", "true"}
     */
    // @Reference(group = "groupB")
    @Reference(group = "*", parameters = {"merger", "true"})
    private GroupService groupService;

    @GetMapping
    public List<String> testGroup() {
        return groupService.queryGroupData();
    }

    @GetMapping("mergerString")
    public String testMergerString() {
        return groupService.getGroupMessage();
    }

}
```

测试结果

![](images/20210717103446097_9776.png)

## 12. 静态服务

**将 Dubbo 服务标识为非动态管理模式**。如希望人工管理服务提供者的上线和下线，此时需将注册中心标识为非动态管理模式。

```xml
<dubbo:registry address="10.20.141.150:9090" dynamic="false" />
<!-- 或者 -->
<dubbo:registry address="10.20.141.150:9090?dynamic=false" />
```

服务提供者初次注册时为禁用状态，需人工启用。断线时，将不会被自动删除，需人工禁用。（*通过代码调用接口的方式去操作`Registry`注册服务*）

如果是一个第三方服务提供者，比如 memcached，可以直接向注册中心写入提供者地址信息，消费者正常使用：

```java
RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
registry.register(URL.valueOf("memcached://10.20.153.11/com.foo.BarService?category=providers&dynamic=false&application=foo"));
```

## 13. 多版本

- 当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用
- 服务端提供接口的实现升级时，可由dubbo的版本号操作进行过渡。如果上线上测试新版本接口有缺陷，为了不影响业务，要迅速切回原版本接口，最大程度减少损失。

版本迁移步骤：

1. 在低压力时间段，先升级一半提供者为新版本
2. 再将所有消费者升级为新版本
3. 然后将剩下的一半提供者升级为新版本

- 提供者配置

```xml
<!-- 版本1接口 -->
<dubbo:service interface="com.xxx.XxxServices" ref="xxxService" version="1.0.0"/>
<!-- 版本2接口 -->
<dubbo:service interface="com.xxx.XxxServices" ref="xxxService2" version="2.0.0"/>
```

- 服务消费者配置

```xml
<!-- 指定版本 -->
<dubbo:reference id="xxxService1.0" interface="com.xxx.XxxServices" version="2.0.0"/>
<!-- 不区分版本 -->
<dubbo:reference id="xxxService" interface="com.xxx.XxxServices" version="*"/>
```

## 14. 参数校验

dubbo的参数验证功能是基于 JSR303 实现的，用户只需标识 JSR303 标准的验证 annotation，并通过声明 filter 来实现验证。

### 14.1. Maven 依赖

```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>7.0.0.Final</version>
</dependency>
```

### 14.2. 参数校验示例





## 15. 结果缓存

结果缓存（*2.1.0 以上版本支持*），用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量

### 15.1. 缓存类型

- `lru`：基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
- `threadlocal`：当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
- `jcache`：与 JSR107 集成，可以桥接各种缓存实现

### 15.2. 配置示例

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

## 16. 泛化调用

### 16.1. 使用泛化调用

实现一个通用的服务测试框架，可通过 `GenericService` 调用所有服务实现。

泛化接口调用方式主要用于客户端没有 API 接口及模型类元的情况，参数及返回值中的所有 POJO 均用 `Map` 表示，通常用于框架集成，比如：实现一个通用的服务测试框架，可通过 `GenericService` 调用所有服务实现。

> 注：此功能一般只是用于开发/测试阶段。

#### 16.1.1. 通过 Spring 使用泛化调用

在 Spring 配置申明 `generic="true"`：

```xml
<dubbo:reference id="barService" interface="com.foo.BarService" generic="true" />
```

在 Java 代码获取 barService 并开始泛化调用：

```java
GenericService barService = (GenericService) applicationContext.getBean("barService");
Object result = barService.$invoke("sayHello", new String[] { "java.lang.String" }, new Object[] { "World" });
```

#### 16.1.2. 通过 API 方式使用泛化调用

```java
import org.apache.dubbo.rpc.service.GenericService;
...

// 引用远程服务
// 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
// 弱类型接口名
reference.setInterface("com.xxx.XxxService");
reference.setVersion("1.0.0");
// 声明为泛化接口
reference.setGeneric(true);

// 用org.apache.dubbo.rpc.service.GenericService可以替代所有接口引用
GenericService genericService = reference.get();

// 基本类型以及Date,List,Map等不需要转换，直接调用
Object result = genericService.$invoke("sayHello", new String[] {"java.lang.String"}, new Object[] {"world"});

// 用Map表示POJO参数，如果返回值为POJO也将自动转成Map
Map<String, Object> person = new HashMap<String, Object>();
person.put("name", "xxx");
person.put("password", "yyy");
// 如果返回POJO将自动转成Map
Object result = genericService.$invoke("findPerson", new String[]{"com.xxx.Person"}, new Object[]{person});
...
```

POJO

```java
@Data
public class PersonImpl implements Person {
    private String name;
    private String password;
}
```

则 POJO 请求数据：

```java
Person person = new PersonImpl();
person.setName("xxx");
person.setPassword("yyy");
```

请求时可用下面 Map 表示：

```java
Map<String, Object> map = new HashMap<String, Object>();
// 注意：如果参数类型是接口，或者List等丢失泛型，可通过class属性指定类型。
map.put("class", "com.xxx.PersonImpl");
map.put("name", "xxx");
map.put("password", "yyy");
```

### 16.2. 实现泛化调用示例

通过实现 `GenericService` 接口处理所有服务请求

泛接口实现方式主要用于服务器端没有 API 接口及模型类元的情况，参数及返回值中的所有 POJO 均用 Map 表示，通常用于框架集成，比如：实现一个通用的远程服务 Mock 框架，可通过实现 `GenericService` 接口处理所有服务请求。

以下是一个实现调用其他业务层方法的简单思路。

```java
@Service
public class MyGenericService implements GenericService, ApplicationContextAware {

    private ApplicationContext context;

    /**
     * Generic invocation
     *
     * @param method         Method name, e.g. findPerson. If there are overridden methods, parameter info is
     *                       required, e.g. findPerson(java.lang.String)
     * @param parameterTypes Parameter types
     * @param args           Arguments
     * @return invocation return value
     * @throws Throwable potential exception thrown from the invocation
     */
    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        StringJoiner joiner = new StringJoiner("; ", "[ ", " ]");
        joiner.add("method name is " + method);
        if (parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                joiner.add("parameterType[" + i + "] is " + parameterTypes[i]);
            }
        }
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                joiner.add("args[" + i + "] is " + args[i]);
            }
        }
        String result = joiner.toString();
        System.out.println("泛化调用 MyGenericService 实现==> " + result);

        /*
         * 这里做简单的判断，直接调用spring容器中的实例方法。
         * 注：这是写死的调用，实际项目中的应用是通过反射或者从spring容器去调用相应的方法
         */
        if ("getResult".equals(method)) {
            GenericCallService genericCallService = context.getBean(GenericCallService.class);
            result = genericCallService.getResult((String) args[0]);
        }

        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
```

#### 16.2.1. 通过 Spring 暴露泛化实现

在 Spring 配置申明服务的实现：

```xml
<bean id="genericService" class="com.foo.MyGenericService" />
<dubbo:service interface="com.foo.BarService" ref="genericService" />
```

#### 16.2.2. 通过 API 方式暴露泛化实现

```java
...
// 用org.apache.dubbo.rpc.service.GenericService可以替代所有接口实现
GenericService xxxService = new XxxGenericService();

// 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
ServiceConfig<GenericService> service = new ServiceConfig<GenericService>();
// 弱类型接口名
service.setInterface("com.xxx.XxxService");
service.setVersion("1.0.0");
// 指向一个通用服务实现
service.setRef(xxxService);

// 暴露及注册服务
service.export();
```

## 17. 回声测试

回声测试用于检测服务是否可用，回声测试按照正常请求流程执行，能够测试整个调用是否通畅，可用于监控。

所有服务自动实现 `EchoService` 接口，只需将任意服务引用强制转型为 `EchoService`，即可使用。

Spring 配置：

```xml
<dubbo:reference id="memberService" interface="com.xxx.MemberService" />
```

代码：

```java
// 远程服务引用
MemberService memberService = ctx.getBean("memberService");
EchoService echoService = (EchoService) memberService; // 强制转型为EchoService
// 回声测试可用性
String status = echoService.$echo("OK");
assert(status.equals("OK"));
```

## 18. 上下文信息

上下文中存放的是当前调用过程中所需的环境信息。所有配置信息都将转换为 URL 的参数。

`RpcContext` 是一个 `ThreadLocal` 的临时状态记录器，当接收到 RPC 请求，或发起 RPC 请求时，`RpcContext` 的状态都会变化。比如：A 调 B，B 再调 C，则 B 机器上，在 B 调 C 之前，`RpcContext` 记录的是 A 调 B 的信息，在 B 调 C 之后，`RpcContext` 记录的是 B 调 C 的信息。

> <font color=red>**注：每一次RPC调用的上下文信息对象都不一样。**</font>

### 18.1. 服务消费方

```java
// 远程调用
xxxService.xxx();
// 本端是否为消费端，这里会返回true
boolean isConsumerSide = RpcContext.getContext().isConsumerSide();
// 获取最后一次调用的提供方IP地址
String serverIP = RpcContext.getContext().getRemoteHost();
// 获取当前服务配置信息，所有配置信息都将转换为URL的参数
String application = RpcContext.getContext().getUrl().getParameter("application");
// 注意：每发起RPC调用，上下文状态会变化
yyyService.yyy();
```

### 18.2. 服务提供方

```java
public class XxxServiceImpl implements XxxService {
    public void xxx() {
        // 本端是否为提供端，这里会返回true
        boolean isProviderSide = RpcContext.getContext().isProviderSide();
        // 获取调用方IP地址
        String clientIP = RpcContext.getContext().getRemoteHost();
        // 获取当前服务配置信息，所有配置信息都将转换为URL的参数
        String application = RpcContext.getContext().getUrl().getParameter("application");
        // 注意：每发起RPC调用，上下文状态会变化
        yyyService.yyy();
        // 此时本端变成消费端，这里会返回false
        boolean isProviderSide = RpcContext.getContext().isProviderSide();
    }
}
```

## 19. 上下文信息 - 隐式参数

在 Dubbo 中，可以通过 `RpcContext` 上的 `setAttachment` 和 `getAttachment` 来设置在服务消费方和提供方之间隐式传递参数。

> 注意：path, group, version, dubbo, token, timeout 几个 key 是保留字段，请使用其它值。

![](images/20210721222611679_8883.png)

### 19.1. 在服务消费方端设置隐式参数

`setAttachment` 设置的 KV 对，在完成下面一次远程调用会被清空，即多次远程调用要多次设置。

```java
RpcContext.getContext().setAttachment("index", "1"); // 隐式传参，后面的远程调用都会隐式将这些参数发送到服务器端，类似cookie，用于框架集成，不建议常规业务使用
xxxService.xxx(); // 远程调用
// ...
```

### 19.2. 在服务提供方端获取隐式参数

```java
public class XxxServiceImpl implements XxxService {
    public void xxx() {
        // 获取客户端隐式传入的参数，用于框架集成，不建议常规业务使用
        String index = RpcContext.getContext().getAttachment("index");
    }
}
```

## 20. 异步

> 注意：
>
> Provider 端异步执行和 Consumer 端异步调用是相互独立的，你可以任意正交组合两端配置
>
> - Consumer同步 - Provider同步
> - Consumer异步 - Provider同步
> - Consumer同步 - Provider异步
> - Consumer异步 - Provider异步

### 20.1. 异步执行

Dubbo 服务提供方的异步执行。Provider端异步执行将阻塞的业务从Dubbo内部线程池切换到业务自定义线程，避免Dubbo线程池的过度占用，有助于避免不同服务间的互相影响。<font color=red>**异步执行无益于节省资源或提升RPC响应性能（只是提高了应用的吞量）**</font>，因为如果业务执行需要阻塞，则始终还是要有线程来负责执行。

#### 20.1.1. 定义 CompletableFuture 签名的接口

服务接口定义

```java
public interface AsyncService {
    CompletableFuture<String> doAsync(String name);
}
```

服务实现

```java
@Service
public class AsyncServiceImpl implements AsyncService {
    /**
     * 定义 CompletableFuture 签名的接口.
     * 通过 return CompletableFuture.supplyAsync() ，
     * 业务执行已从 Dubbo 线程切换到业务线程，避免了对 Dubbo 线程池的阻塞。
     *
     * @param name
     * @return
     */
    @Override
    public CompletableFuture<String> doAsync(String name) {
        System.out.println("[annotation provider] AsyncService 接口实现 doAsync 方法执行...");
        RpcContext savedContext = RpcContext.getContext();
        // 建议为supplyAsync提供自定义线程池，避免使用JDK公用线程池。
        // 业务执行已从 Dubbo 线程切换到业务线程，避免了对 Dubbo 线程池的阻塞。
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("receive form consumer: " + savedContext.getAttachment("consumer-key"));
            try {
                Thread.sleep(30000); // 休眠，模拟处理复杂业务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "async response from annotation provider doAsync: " + name;
        });
    }
}
```

注：通过 `return CompletableFuture.supplyAsync()`，业务执行已从 Dubbo 线程切换到业务线程，避免了对 Dubbo 线程池的阻塞。

#### 20.1.2. 使用AsyncContext（2.7.0版本后）

Dubbo 在2.7.0版本后提供了一个类似 Serverlet 3.0 的异步接口`AsyncContext`，在没有 `CompletableFuture` 签名接口的情况下，也可以实现 Provider 端的异步执行。

服务接口定义：

```java
public interface AsyncService {
    String doAsyncOther(String name);
}
```

服务暴露，和普通服务完全一致：

```java
<bean id="asyncService" class="org.apache.dubbo.samples.governance.impl.AsyncServiceImpl"/>
<dubbo:service interface="org.apache.dubbo.samples.governance.api.AsyncService" ref="asyncService"/>
```

服务实现：

```java
@Service
public class AsyncServiceImpl implements AsyncService {
    /**
     * 2.7.0版本 使用AsyncContext
     * Dubbo 提供了一个类似 Serverlet 3.0 的异步接口AsyncContext，
     * 在没有 CompletableFuture 签名接口的情况下，也可以实现 Provider 端的异步执行。
     *
     * @param name
     * @return
     */
    @Override
    public String doAsyncOther(String name) {
        System.out.println("[annotation provider] AsyncService 接口实现 doAsyncOther 方法执行...");
        // 以下方式是 dubbo 2.7.0 版本后的全异步编程
        final AsyncContext asyncContext = RpcContext.startAsync();
        new Thread(() -> {
            // 如果要使用上下文，则必须要放在第一句执行
            asyncContext.signalContextSwitch();
            try {
                Thread.sleep(15000); // 休眠，模拟处理复杂业务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 写回响应
            asyncContext.write("Hello " + name + ", async response from annotation provider.");
        }).start();
        return "async response from annotation provider doAsyncOther: " + name;
    }
}
```

### 20.2. 异步调用(Consumer端调用)

> 从v2.7.0开始，Dubbo的所有异步编程接口开始以CompletableFuture为基础

Dubbo的异步调用是非阻塞的NIO调用，一个线程可同时并发调用多个远程服务，每个服务的调用都是非阻塞的，线程立即返回。就是对java中Futrue模式的扩展支持。异步调用流程图如下：

![](images/20200129192643662_18150.png)

如上图，userThread发出调用后，IOThread会立即返回，并在RPC上下文RpcContext中设置Future。userThread后续可以从RpcContext中取得此Future，然后wait这个Future其它的事情都由IOThread完成

总之，userThread发出调用后IOThread会立刻返回，而不是等调用在服务端执行完代码、返回结果后返回。用户返回后可以去做点其它事情，比如调用另外一个服务，然后回头等待前一个调用完成。从上图可以看出，异步调用完全是Consumer端的行为

> 详细案例参考busi-mall工程中的dubbo.xml与IndexController

#### 20.2.1. 在消费端配置

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

#### 20.2.2. 调用代码

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

## 21. 本地调用

在 Dubbo 中进行本地调用。本地调用使用了 injvm 协议，是一个伪协议，它不开启端口，不发起远程调用，只在 JVM 内直接关联，但执行 Dubbo 的 Filter 链。

### 21.1. 配置

定义 injvm 协议

```xml
<dubbo:protocol name="injvm" />
```

设置默认协议

```xml
<dubbo:provider protocol="injvm" />
```

设置服务协议

```xml
<dubbo:service protocol="injvm" />
```

优先使用 injvm

```xml
<dubbo:consumer injvm="true" .../>
<dubbo:provider injvm="true" .../>
```

或

```xml
<dubbo:reference injvm="true" .../>
<dubbo:service injvm="true" .../>
```

> 注：Dubbo 从 2.2.0 每个服务默认都会在本地暴露，无需进行任何配置即可进行本地引用，如果不希望服务进行远程暴露，只需要在 provider 将 protocol 设置成 injvm 即可

### 21.2. 自动暴露、引用本地服务

从 2.2.0 开始，每个服务默认都会在本地暴露。在引用服务的时候，默认优先引用本地服务。如果希望引用远程服务可以使用一下配置强制引用远程服务。

```xml
<dubbo:reference ... scope="remote" />
```

## 22. 参数回调

通过参数回调从服务器端调用客户端逻辑。参数回调方式与调用本地 `callback` 或 `listener` 相同，只需要在 Spring 的配置文件中声明哪个参数是 `callback` 类型即可。Dubbo 将基于长连接生成反向代理，这样就可以从服务器端调用客户端逻辑。

### 22.1. 服务接口示例

- 定义回调的接口

```java
public interface CallbackParameterService {
    void addListener(String key, CallbackListener listener);
    String doSomething(String param);
}
```

```java
public interface CallbackListener {
    void changed(String msg);
}
```

- 服务提供示例

```java
/*
 * @Method 指定方法的名称
 * @Argument 指定回调参数的信息。
 *      index属性：设置回调的参数位置
 *      callback属性：是否为回调的方法，设置true则定义回调方法
 */
@Service(methods = {@Method(name = "addListener", arguments = {@Argument(index = 1, callback = true)})})
public class CallbackParameterServiceImpl implements CallbackParameterService {
    @Override
    public void addListener(String key, CallbackListener listener) {
        // CallbackListener 是消费端去实现的回调方法
        listener.changed(doSomething(key));
    }

    /* 与回调无关的其他方法 */
    @Override
    public String doSomething(String param) {
        return param + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
```

### 22.2. 服务消费者

- 配置示例

```xml
<dubbo:reference id="callbackService" interface="com.moon.dubbo.service.callback.CallbackParameterService" />
```

- 调用示例

```java
@Reference(check = false)
private CallbackParameterService callbackParameterService;

@GetMapping
public String testCallbackParameter() {
    callbackParameterService.addListener("MooN", new CallbackListener() {
        // 此方法会在服务端回调
        @Override
        public void changed(String msg) {
            System.out.println("consumer callbackParameter: " + msg);
        }
    });
    return "success!";
}
```

## 23. 事件通知(未整理)

在调用之前、调用之后、出现异常时，会触发 `oninvoke`、`onreturn`、`onthrow` 三个事件，可以配置当事件发生时，通知哪个类的哪个方法。在Consumer端，可以为三个事件指定事件处理方法

### 23.1. 服务消费者 Callback 接口

```java
interface Notify {
    public void onreturn(Person msg, Integer id);
    public void onthrow(Throwable ex, Integer id);
}
```

### 23.2. 服务消费者 Callback 实现

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

### 23.3. 服务消费者 Callback 配置

```xml
<bean id ="demoCallback" class = "org.apache.dubbo.callback.implicit.NofifyImpl" />
<dubbo:reference id="demoService" interface="org.apache.dubbo.callback.implicit.IDemoService" version="1.0.0" group="cn" >
      <dubbo:method name="get" async="true" onreturn="demoCallback.onreturn" onthrow="demoCallback.onthrow" />
</dubbo:reference>
```

### 23.4. 配置几种组合情况

- callback 与 async 功能正交分解，async=true 表示结果是否马上返回，onreturn 表示是否需要回调。两者叠加存在以下几种组合情况
    - 异步回调模式：`async=true onreturn="xxx"`
    - 同步回调模式：`async=false onreturn="xxx"`
    - 异步无回调：`async=true`
    - 同步无回调：`async=false`

## 24. 本地存根

在 Dubbo 中利用本地存根在客户端执行部分逻辑

远程服务后，客户端通常只剩下接口，而实现全在服务器端，但提供方有些时候想在客户端也执行部分逻辑，比如：做 ThreadLocal 缓存，提前验证参数，调用失败后伪造容错数据等等，此时就需要在 API 中带上 Stub，客户端生成 Proxy 实例，会把 Proxy 通过构造函数传给 Stub，然后把 Stub 暴露给用户，Stub 可以决定要不要去调 Proxy。

![](images/20210727223739272_31076.png)

### 24.1. 服务端示例

在 spring 配置文件中按以下方式配置：

```xml
<dubbo:service interface="com.foo.BarService" stub="true" />
```

或

```xml
<dubbo:service interface="com.foo.BarService" stub="com.foo.BarServiceStub" />
```

使用注解方式的示例，使用`@Service`注解暴露服务

```java
// Stub接口
public interface LocalStubService {
    String execute(String params);
}

// 服务端Stub接口实现
@Service
public class LocalStubServiceImpl implements LocalStubService {
    @Override
    public String execute(String params) {
        System.out.println("[annotation provider] LocalStubService 接口实现 execute 方法执行...");
        return "Annotation provider LocalStubServiceImpl execute: " + params;
    }
}
```

### 24.2. 客户端示例

在客户端创建与服务提供者同一个接口的实现类，此类会接管服务提供者的接口调用。通过此代理可以实现要不要远程调用服务提供方

```java
public class LocalStubProxy implements LocalStubService {

    private final LocalStubService localStubService;

    // 注意，此构造函数必须定义，用于传入真正的远程代理对象
    public LocalStubProxy(LocalStubService localStubService) {
        this.localStubService = localStubService;
    }

    @Override
    public String execute(String params) {
        System.out.println("[annotation consumer] LocalStubService 接口实现 execute 方法执行...");

        // 此代码在客户端执行, 你可以在客户端做ThreadLocal本地缓存，或预先验证参数是否合法，等等
        try {
            if ("local".equalsIgnoreCase(params)) {
                // 模拟业务，不调用远程接口
                return "Annotation consumer LocalStubProxy execute: " + params;
            } else if ("remote".equalsIgnoreCase(params)) {
                // 模拟业务，调用远程服务
                return localStubService.execute(params);
            }
        } catch (Exception e) {
            // 可以容错，可以做任何AOP拦截事项
            e.printStackTrace();
            return "服务器出错了";
        }
        return null;
    }
}
```

> 注：
>
> 1. Stub （即上示例`LocalStubProxy`类）必须有可传入 Proxy 的构造函数。
> 2. 在 interface 本地 Stub 实现，它与服务端实现同一个接口，并有一个传入远程接口实现实例的构造函数
> 3. 如果引入服务接口时配置属性`stub = "true"`，则需要本地的实现类与接口在同一个包路径下，并且类名必须是`接口名+Stub`结尾，如`LocalStubServiceStub`

使用`@Reference`注解方式引入测试接口，其中`stub`属性用于指定测试接口的本地实现类。

```java
@Reference(check = false, stub = "com.moon.dubbo.annotation.stub.LocalStubProxy")
private LocalStubService localStubService;

@GetMapping
public String testLocalStub(@RequestParam("params") String params) {
    return localStubService.execute(params);
}
```

## 25. 本地伪装

本地伪装通常用于服务降级，比如某验权服务，当服务提供方全部挂掉后，客户端不抛出异常，而是通过 Mock 数据返回授权失败。

### 25.1. 配置

在 spring 配置文件中按以下方式配置：

```xml
<dubbo:reference interface="com.foo.BarService" mock="true" />
```

或

```xml
<dubbo:reference interface="com.foo.BarService" mock="com.foo.BarServiceMock" />
```

> 注：
>
> 1. `Mock` 是 `Stub` 的一个子集，便于服务提供方在客户端执行容错逻辑，因经常需要在出现 `RpcException` (比如网络失败，超时等)时进行容错，而在出现业务异常(比如登录用户名密码错误)时不需要容错，如果用 `Stub`，可能就需要捕获并依赖 `RpcException` 类，而用 `Mock` 就可以不依赖 `RpcException`，因为它的约定就是只有出现 `RpcException` 时才执行。
> 2. 在 interface 本地 Mock 实现，它与服务端实现同一个接口，并提供一个无参构造函数
> 3. 如果引入服务接口时配置属性`mock = "true"`，则需要本地的实现类与接口在同一个包路径下，并且类名必须是`接口名+Mock`，如`LocalMockServiceMock`

### 25.2. 基础示例

- 编写一个测试接口
- 在服务提供者工程中，编写一个测试接口的实现，并在相应的方法中模拟出现异常（如通过`Thread.sleep`模拟超时）
- 在服务消费者工程中，提供 Mock 实现

```java
/**
 * 消费者，本地伪装降级类。
 * <p>
 * 这个是配置 mock="true"时默认加载的降级类，其名称必须是 “接口名 + Mock”。
 * 这个类中方法调用的前提是出现了远程调用异常（RpcException），此时才会调用此类其中的方法
 */
public class LocalMockServiceMock implements LocalMockService {
    @Override
    public String mock(String params) {
        System.out.println("[annotation consumer] LocalMockService 接口实现 mock 方法执行...");
        // 返回伪造容错数据，此方法只在远程调用出现 RpcException 时被执行
        return "[annotation consumer] LocalMockServiceMock: " + params;
    }
}
```

- 在消费者工程

```java
/*
 * mock属性：用于配置本地伪装
 *  mock = "true"，默认加载的降级类，其名称必须是 “接口名 + Mock”，并且与接口在同一个包下
 *  mock = "类全限定名"，指定降级类，此类可以在任意包下，名称也没有固定限制
 */
// @Reference(check = false, mock = "true")
@Reference(check = false, mock = "com.moon.dubbo.service.LocalMockServiceMock")
private LocalMockService localMockService;

@GetMapping
public String testLocalMock(@RequestParam("params") String params) {
    return localMockService.mock(params);
}
```

### 25.3. 进阶用法

#### 25.3.1. return

使用 `return` 来返回一个字符串表示的对象，作为 Mock 的返回值。合法的字符串可以是：

- empty: 代表空，基本类型的默认值，或者集合类的空值
- null: `null`
- true: `true`
- false: `false`
- JSON 格式: 反序列化 JSON 所得到的对象

```java
@Reference(check = false, mock = "return null")
@Reference(check = false, mock = "return true")
@Reference(check = false, mock = "return MooN")
```

#### 25.3.2. throw

使用 `throw` 来返回一个 `Exception` 对象，作为 Mock 的返回值。当调用出错时，抛出一个默认的 `RPCException`:

```xml
<dubbo:reference interface="com.foo.BarService" mock="throw" />
```

当调用出错时，抛出指定的 `Exception`：

```xml
<dubbo:reference interface="com.foo.BarService" mock="throw com.foo.MockException" />
```

#### 25.3.3. force 和 fail

在 2.6.6 以上的版本，可以开始在 Spring XML 配置文件中使用 `fail:` 和 `force:`。

- `force:` 代表强制使用 Mock 行为，在这种情况下不会走远程调用。
- `fail:` 与默认行为一致，只有当远程调用发生错误时才使用 Mock 行为。

注：`force:` 和 `fail:` 都支持与 `throw` 或者 `return` 组合使用。

强制返回指定值：

```xml
<dubbo:reference interface="com.foo.BarService" mock="force:return fake" />
```

强制抛出指定异常：

```xml
<dubbo:reference interface="com.foo.BarService" mock="force:throw com.foo.MockException" />
```

#### 25.3.4. 在方法级别配置 Mock

Mock 可以在方法级别上指定，假定 `com.foo.BarService` 上有好几个方法，可以单独为 `sayHello()` 方法指定 Mock 行为。具体配置如下所示，在本例中，只要 `sayHello()` 被调用到时，强制返回 “fake”:


```xml
<dubbo:reference id="demoService" check="false" interface="com.foo.BarService">
    <dubbo:parameter key="sayHello.mock" value="force:return fake"/>
</dubbo:reference>
```

## 26. 延迟暴露

如果服务需要预热时间，比如初始化缓存，等待相关资源就位等，可以使用 `delay` 进行延迟暴露。在 Dubbo 2.6.5 版本中对服务延迟暴露逻辑进行了细微的调整，将需要延迟暴露（`delay > 0`）服务的倒计时动作推迟到了 Spring 初始化完成后进行。在使用 Dubbo 的过程中，并不会感知到此变化。

### 26.1. Dubbo 2.6.5 之前版本

延迟到 Spring 初始化完成后，再暴露服务（基于 Spring 的 `ContextRefreshedEvent` 事件触发暴露 ）

```xml
<dubbo:service delay="-1" />
```

延迟 5 秒暴露服务

```xml
<dubbo:service delay="5000" />
```

### 26.2. Dubbo 2.6.5 及以后版本

所有服务都将在 Spring 初始化完成后进行暴露，如果不需要延迟暴露服务，无需配置 `delay`。如：延迟 5 秒暴露服务

```xml
<dubbo:service delay="5000" />
```

### 26.3. Spring 2.x 初始化死锁问题

**触发条件**：在 Spring 解析到 `<dubbo:service />` 时，就已经向外暴露了服务，而 Spring 还在接着初始化其它 Bean。如果这时有请求进来，并且服务的实现类里有调用 `applicationContext.getBean()` 的用法。

1. 请求线程的 `applicationContext.getBean()` 调用，先同步 `singletonObjects` 判断 Bean 是否存在，不存在就同步 `beanDefinitionMap` 进行初始化，并再次同步 `singletonObjects` 写入 Bean 实例缓存。

![](images/20210801170323412_19834.jpg)

2. 而 Spring 初始化线程，因不需要判断 Bean 的存在，直接同步 `beanDefinitionMap` 进行初始化，并同步 `singletonObjects` 写入 Bean 实例缓存。

![](images/20210801170338894_11770.jpg)

这样就导致 `getBean` 线程，先锁 `singletonObjects`，再锁 `beanDefinitionMap`，再次锁 `singletonObjects`。而 Spring 初始化线程，先锁 `beanDefinitionMap`，再锁 `singletonObjects`。反向锁导致线程死锁，不能提供服务，启动不了。

**规避办法**：

1. 强烈建议不要在服务的实现类中有 `applicationContext.getBean()` 的调用，全部采用 IoC 注入的方式使用 Spring的Bean。
2. 如果实在要调 `getBean()`，可以将 Dubbo 的配置放在 Spring 的最后加载。
3. 如果不想依赖配置顺序，可以使用 `<dubbo:provider delay=”-1” />`，使 Dubbo 在 Spring 容器初始化完后，再暴露服务。
4. 如果大量使用 `getBean()`，相当于已经把 Spring 退化为工厂模式在用，可以将 Dubbo 的服务隔离单独的 Spring 容器。

## 27. 并发控制

### 27.1. 配置示例

- 限制 `com.foo.BarService` 的每个方法，服务器端并发执行（或占用线程池线程数）不能超过 10 个：

```xml
<dubbo:service interface="com.foo.BarService" executes="10" />
```

- 限制 `com.foo.BarService` 的 `sayHello` 方法，服务器端并发执行（或占用线程池线程数）不能超过 10 个：

```xml
<dubbo:service interface="com.foo.BarService">
    <dubbo:method name="sayHello" executes="10" />
</dubbo:service>
```

- 限制 `com.foo.BarService` 的每个方法，每客户端并发执行（或占用连接的请求数）不能超过 10 个：

```xml
<dubbo:service interface="com.foo.BarService" actives="10" />
<!-- 或者 -->
<dubbo:reference interface="com.foo.BarService" actives="10" />
```

- 限制 `com.foo.BarService` 的 `sayHello` 方法，每客户端并发执行（或占用连接的请求数）不能超过 10 个：

```xml
<dubbo:service interface="com.foo.BarService">
    <dubbo:method name="sayHello" actives="10" />
</dubbo:service>
<!-- 或者 -->
<dubbo:reference interface="com.foo.BarService">
    <dubbo:method name="sayHello" actives="10" />
</dubbo:service>
```

> 注：如果 `<dubbo:service>` 和 `<dubbo:reference>` 都配置`actives`属性，则以`<dubbo:reference>` 优先。详见：


### 27.2. Load Balance 均衡

配置服务的客户端的 `loadbalance` 属性为 `leastactive`，此 `Loadbalance` 会调用并发数最小的 Provider（Consumer端并发数）。

```xml
<dubbo:reference interface="com.foo.BarService" loadbalance="leastactive" />
<!-- 或者 -->
<dubbo:service interface="com.foo.BarService" loadbalance="leastactive" />
```

## 28. 连接控制（未整理示例代码）

### 28.1. 服务端连接控制

限制服务器端接受的连接不能超过指定的数量

```xml
<dubbo:provider protocol="dubbo" accepts="10" />
<!-- 或者 -->
<dubbo:protocol name="dubbo" accepts="10" />
```

### 28.2. 客户端连接控制

限制客户端服务使用连接不能超过指定的数量

```xml
<dubbo:reference interface="com.foo.BarService" connections="10" />
<!-- 或者 -->
<dubbo:service interface="com.foo.BarService" connections="10" />
```

> 注：
>
> 1. 如果 `<dubbo:service>` 和 `<dubbo:reference>` 都配置了`connections`属性，则以`<dubbo:reference>`优先
> 2. 因为连接是在服务提供者Server上，所以配置在 Provider 上
> 3. 如果是长连接，比如 Dubbo 协议，connections 表示该服务对每个提供者建立的长连接数

## 29. 延迟连接（未整理示例代码）

在 Dubbo 中配置延迟连接用于减少长连接数。当有调用发起时，再创建长连接。

```xml
用于减少长连接数。当有调用发起时，再创建长连接。
```

> **提示**：该配置只对使用长连接的 dubbo 协议生效。

## 30. 粘滞连接（未整理示例代码）

粘滞连接用于有状态服务，尽可能让客户端总是向同一提供者发起调用，除非该提供者挂了，再连另一台。粘滞连接将自动开启**延迟连接**，以减少长连接数。

```xml
<dubbo:reference id="xxxService" interface="com.xxx.XxxService" sticky="true" />
```

Dubbo 支持方法级别的粘滞连接，如果想进行更细粒度的控制，还可以这样配置。

```xml
<dubbo:reference id="xxxService" interface="com.xxx.XxxService">
    <dubbo:mothod name="sayHello" sticky="true" />
</dubbo:reference>
```

## 31. 令牌验证（未整理示例代码）

通过令牌验证在注册中心控制权限，以决定要不要下发令牌给消费者，可以防止消费者绕过注册中心访问提供者，另外通过注册中心可灵活改变授权方式，而不需修改或升级提供者

![](images/20210803220649659_32538.jpg)

全局设置开启令牌验证：

```xml
<!--随机token令牌，使用UUID生成-->
<dubbo:provider interface="com.foo.BarService" token="true" />
<!-- 或者 -->
<!--固定token令牌，相当于密码-->
<dubbo:provider interface="com.foo.BarService" token="123456" />
```

服务级别设置开启令牌验证：

```xml
<!--随机token令牌，使用UUID生成-->
<dubbo:service interface="com.foo.BarService" token="true" />
<!-- 或者 -->
<!--固定token令牌，相当于密码-->
<dubbo:service interface="com.foo.BarService" token="123456" />
```

## 32. 配置规则

在 Dubbo 中配置应用级治理规则和服务级治理规则

### 32.1. 旧版本的规则配置方式

2.2.0 以上版本支持，向注册中心写入动态配置覆盖规则。该功能通常由监控中心或治理中心的页面完成。

```java
RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
registry.register(URL.valueOf("override://0.0.0.0/com.foo.BarService?category=configurators&dynamic=false&application=foo&timeout=1000"));
```

URL分解说明

- `override://` 表示数据采用覆盖方式，支持 `override` 和 `absent`，可扩展，**必填**。
- `0.0.0.0` 表示对所有 IP 地址生效，如果只想覆盖某个 IP 的数据，请填入具体 IP，**必填**。
- `com.foo.BarService` 表示只对指定服务生效，**必填**。
- `category=configurators` 表示该数据为动态配置类型，**必填**。
- `dynamic=false` 表示该数据为持久数据，当注册方退出时，数据依然保存在注册中心，**必填**。
- `enabled=true` 覆盖规则是否生效，可不填，缺省生效。
- `application=foo` 表示只对指定应用生效，可不填，表示对所有应用生效。
- `timeout=1000` 表示将满足以上条件的 `timeout` 参数的值覆盖为 `1000`。如果想覆盖其它参数，直接加在 `override` 的 URL 参数上。

其他示例：

- 禁用提供者：(通常用于临时踢除某台提供者机器，相似的，禁止消费者访问请使用路由规则)

```
override://10.20.153.10/com.foo.BarService?category=configurators&dynamic=false&disbaled=true
```

- 调整权重：(通常用于容量评估，缺省权重为 100)

```
override://10.20.153.10/com.foo.BarService?category=configurators&dynamic=false&weight=200
```

- 调整负载均衡策略：(缺省负载均衡策略为 random)

```
override://10.20.153.10/com.foo.BarService?category=configurators&dynamic=false&loadbalance=leastactive
```

- 服务降级：(通常用于临时屏蔽某个出错的非关键服务)

```
override://0.0.0.0/com.foo.BarService?category=configurators&dynamic=false&application=foo&mock=force:return+null
```

### 32.2. 新版本规则配置

覆盖规则是 Dubbo 设计的在无需重启应用的情况下，动态调整 RPC 调用行为的一种能力。2.7.0 版本开始，支持从服务和应用两个粒度来调整动态配置。

#### 32.2.1. 概览

一般在**服务治理控制台**查看或修改覆盖规则。

应用粒度

```yml
# 将应用demo（key:demo）在20880端口上提供（side:provider）的所有服务（scope:application）的权重修改为1000（weight:1000）。
---
configVersion: v2.7
scope: application
key: demo
enabled: true
configs:
- addresses: ["0.0.0.0:20880"]
  side: provider
  parameters:
  weight: 1000
  ...
```

服务粒度

```yml
# 所有消费（side:consumer）DemoService服务（key:org.apache.dubbo.samples.governance.api.DemoService）的应用实例（addresses:[0.0.0.0]），超时时间修改为6000ms
---
configVersion: v2.7
scope: service
key: org.apache.dubbo.samples.governance.api.DemoService
enabled: true
configs:
- addresses: [0.0.0.0]
  side: consumer
  parameters:
  timeout: 6000
  ...
```

#### 32.2.2. 规则配置模板详解

```yml
---
configVersion: v2.7
scope: application/service
key: app-name/group+service+version
enabled: true
configs:
- addresses: ["0.0.0.0"]
  providerAddresses: ["1.1.1.1:20880", "2.2.2.2:20881"]
  side: consumer
  applications/services: []
  parameters:
    timeout: 1000
    cluster: failfase
    loadbalance: random
- addresses: ["0.0.0.0:20880"]
  side: provider
  applications/services: []
  parameters:
    threadpool: fixed
    threads: 200
    iothreads: 4
    dispatcher: all
    weight: 200
...
```

- `configVersion` 表示 dubbo 的版本
- `scope` 表示配置作用范围，分别是应用（application）或服务（service）粒度。**必填**。
- `key` 指定规则体作用在哪个服务或应用。**必填**。
- `scope=service`时，key取值为`[{group}:]{service}[:{version}]`的组合
- `scope=application`时，key取值为application名称
- `enabled=true` 覆盖规则是否生效，可不填，缺省生效。
- `configs` 定义具体的覆盖规则内容，可以指定`n（n>=1）`个规则体。**必填**。
    - side
    - applications
    - services
    - parameters
    - addresses
    - providerAddresses

#### 32.2.3. 规则配置示例

- 禁用提供者：(通常用于临时踢除某台提供者机器，相似的，禁止消费者访问请使用路由规则)

```yml
---
configVersion: v2.7
scope: application
key: demo-provider
enabled: true
configs:
- addresses: ["10.20.153.10:20880"]
  side: provider
  parameters:
    disabled: true
...
```

- 调整权重：(通常用于容量评估，缺省权重为 200)

```yml
---
configVersion: v2.7
scope: application
key: demo-provider
enabled: true
configs:
- addresses: ["10.20.153.10:20880"]
  side: provider
  parameters:
    weight: 200
...
```

- 调整负载均衡策略：(缺省负载均衡策略为 random)

```yml
---
configVersion: v2.7
scope: application
key: demo-consumer
enabled: true
configs:
- side: consumer
  parameters:
    loadbalance: random
...
```

- 服务降级：(通常用于临时屏蔽某个出错的非关键服务)

```yml
---
configVersion: v2.7
scope: service
key: org.apache.dubbo.samples.governance.api.DemoService
enabled: true
configs:
- side: consumer
 parameters:
   force: return null
...
```

# 服务化最佳实践

> 详情参考官网：http://dubbo.apache.org/zh-cn/docs/user/best-practice.html

## 1. 在 Provider 端应尽量配置的属性

Dubbo的属性配置优先度上，遵循顺序：`reference属性 --> service属性 --> Consumer 属性`

其中reference和Consumer是消费端配置，service是服务端配置

而对于服务调用的超时时间、重试次数等属性，服务的提供方比消费方更了解服务性能，因此我们应该在 Provider 端尽量多配置 Consumer 端属性，让其漫游到消费端发挥作用

### 1.1. 在 Provider 端尽量多配置 Consumer 端属性

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

### 1.2. 在 Provider 端配置合理的 Provider 端属性

```xml
<dubbo:protocol threads="200" />
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" executes="200">
    <dubbo:method name="findAllPerson" executes="50" />
</dubbo:service>
```

建议在 Provider 端配置的 Provider 端属性有：

1. `threads`：服务线程池大小
2. `executes`：一个服务提供者并行执行请求上限，即当 Provider 对一个服务的并发调用达到上限后，新调用会阻塞，此时 Consumer 可能会超时。在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制

## 2. 服务拆分最佳实现

### 2.1. 分包

建议将服务接口、服务模型、服务异常等均放在 API 包中，因为服务模型和异常也是 API 的一部分，这样做也符合分包原则：重用发布等价原则(REP)，共同重用原则(CRP)。

如果需要，也可以考虑在 API 包中放置一份 Spring 的引用配置，这样使用方只需在 Spring 加载过程中引用此配置即可。配置建议放在模块的包目录下，以免冲突，如：com/alibaba/china/xxx/dubbo-reference.xml。

### 2.2. 粒度

服务接口尽可能大粒度，每个服务方法应代表一个功能，而不是某功能的一个步骤，否则将面临分布式事务问题，Dubbo 暂未提供分布式事务支持。

服务接口建议以业务场景为单位划分，并对相近业务做抽象，防止接口数量爆炸。

不建议使用过于抽象的通用接口，如：Map query(Map)，这样的接口没有明确语义，会给后期维护带来不便。

### 2.3. 版本

每个接口都应定义版本号，为后续不兼容升级提供可能，如：`<dubbo:service interface="com.xxx.XxxService" version="1.0" />`

建议使用两位版本号，因为第三位版本号通常表示兼容升级，只有不兼容时才需要变更服务版本。

当不兼容时，先升级一半提供者为新版本，再将消费者全部升为新版本，然后将剩下的一半提供者升为新版本。

### 2.4. 异常

建议使用异常汇报错误，而不是返回错误码，异常信息能携带更多信息，并且语义更友好。

如果担心性能问题，在必要时，可以通过 override 掉异常类的 `fillInStackTrace()` 方法为空方法，使其不拷贝栈信息。

查询方法不建议抛出 checked 异常，否则调用方在查询时将过多的 `try...catch`，并且不能进行有效处理。

服务提供方不应将 DAO 或 SQL 等异常抛给消费方，应在服务实现中对消费方不关心的异常进行包装，否则可能出现消费方无法反序列化相应异常。

## 3. SPI机制原理

Dubbo框架是建立的SPI机制之上

### 3.1. Java SPI 机制

#### 3.1.1. SPI 简述

SPI全称Service Provider Interface，是Java提供的一套用来被第三方实现或者扩展的API，它可以用来启用框架扩展和替换组件

![Java SPI 机制调用流程图](images/20200130184515935_6858.jpg)

可以看到，SPI的本质，其实是帮助程序，为某个特定的接口寻找它的实现类。而且哪些实现类的会加载，是个动态过程（不是提前预定好的）

有点类似IOC的思想，就是将装配的控制权移到程序之外，在模块化设计中这个机制尤其重要。所以SPI的核心思想就是**解耦**

- 常见的例子：
    - 数据库驱动加载接口实现类的加载。JDBC加载不同类型数据库的驱动
    - 日志门面接口实现类加载，SLF4J加载不同提供商的日志实现类
    - Spring中大量使用了SPI，比如：对servlet3.0规范对ServletContainerInitializer的实现、自动类型转换Type Conversion SPI(Converter SPI、Formatter SPI)等

#### 3.1.2. 基础使用

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

#### 3.1.3. 核心功能类

需要指出的是，java之所以能够顺利根据配置加载这个实现类，完全依赖于jdk内的一个核心类：

![SPI的核心类](images/20200130231835343_15049.jpg)

### 3.2. Dubbo SPI机制

- Java SPI机制非常简单，就是读取指定的配置文件，将所有的类都加载到程序中。而这种机制，存在很多缺陷，比如：
    1. 所有实现类无论是否使用，直接被加载，可能存在浪费
    2. 不能够灵活控制什么时候什么时机，匹配什么实现，功能太弱
- Dubbo框架就基于自己的需要，对JAVA的SPI机制进行增强

#### 3.2.1. @SPI 注解

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

#### 3.2.2. @Activate 注解

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

#### 3.2.3. Javassist动态编译

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

#### 3.2.4. @Adaptive 注解

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

#### 3.2.5. Dubbo SPI的依赖注入

Dubbo SPI的核心实现类为ExtensionLoader，此类的使用几乎遍及Dubbo的整个源码体系

- ExtensionLoader有三个重要的入口方法，分别与`@SPI`、`@Activate`、`@Adaptive`注解对应
    - `getExtension`方法，对应加载所有的实现
    - `getActivateExtension`方法，对应解析加载`@Activate`注解对应的实现
    - `getAdaptiveExtension`方法，对应解析加载`@Adaptive`注解对应的实现
- 其中，`@Adaptive`注解作的自适应功能，还涉及到了代理对象（而Dubbo的代理机制，有两种选择，jdk动态代理和javassist动态编译类）

##### 3.2.5.1. Dubbo SPI 依赖注入场景

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

##### 3.2.5.2. Dubbo依赖注入的过程分析

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

## 4. 其他

### 4.1. dubbo框架使用示例

dubbo框架使用示例项目参考：D:\code\dubbo-note\dubbo-sample\

### 4.2. 相关RPC服务框架（HSF） -- 网络资料

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

