## 1. RPC（Remote Procedure Call Protocol）

### 1.1. RPC 的概念

RPC（Remote Procedure Call Protocol）远程过程调用协议，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。简言之，RPC使得程序能够像访问本地系统资源一样，去访问远端系统资源。比较关键的一些方面包括：通讯协议、序列化、资源（接口）描述、服务框架、性能、语言支持等。

简单的说，**RPC就是从一台机器(客户端)上通过参数传递的方式调用另一台机器(服务器)上的一个函数或方法(可以统称为服务)并得到返回的结果。**

**RPC 使用场景**：在微服务环境下，会存在大量的跨 JVM （不同服务器之间）进行方法调用的场景，服务结构如下图：

![分布式服务结构](images/20200122110030436_24328.png)

具体到一次调用过程来说， A 机器能通过网络，调用 B 机器内的某个服务方法，并得到返回值

![服务调用](images/20200122134326828_23754.png)

### 1.2. RPC 实现原理

#### 1.2.1. RPC 核心要点

实现 RPC 功能，主要需要解决以下几点：

1. 网络连接通讯模块：负责连接建立、管理和消息的传输。
2. 编解码模块：因为网络传输的是字节码，所以需要使用的对象序列化和反序列化。
3. 服务器端：暴露开放的服务接口。
4. 客户端：生成调用服务接口的代理实现，该代理实现负责收集数据、编码并传输给服务器然后等待结果返回。

#### 1.2.2. RPC 架构图

![](images/20190409090340345_9140.jpg)

#### 1.2.3. RPC 架构组件

一个基本的 RPC 架构里面应该至少包含以下4个组件：

1. **客户端（Client）**：服务调用方（服务消费者）
2. **客户端存根（Client Stub）**：存放服务端地址信息，将客户端的请求参数数据信息打包成网络消息，再通过网络传输发送给服务端
3. **服务端存根（Server Stub）**：接收客户端发送过来的请求消息并进行解包，然后再调用本地服务进行处理
4. **服务端（Server）**：服务的真正提供者

![RPC实现流程](images/20190409084601897_15104.jpg)

具体调用过程：

1. 服务消费者（client客户端）通过调用本地服务的方式调用需要消费的服务；
2. 客户端存根（client stub）接收到调用请求后负责将方法、入参等信息序列化（组装）成能够进行网络传输的消息体；
3. 客户端存根（client stub）找到远程的服务地址，并且将消息通过网络发送给服务端；
4. 服务端存根（server stub）收到消息后进行解码（反序列化操作）；
5. 服务端存根（server stub）根据解码结果调用本地的服务进行相关处理；
6. 本地服务执行具体业务逻辑并将处理结果返回给服务端存根（server stub）；
7. 服务端存根（server stub）将返回结果重新打包成消息（序列化）并通过网络发送至消费方；
8. 客户端存根（client stub）接收到消息，并进行解码（反序列化）；
9. 服务消费方得到最终结果。

而 RPC 框架的实现目标则是将上面的第 2-10 步完好地封装起来，也就是把调用、编码/解码的过程给封装起来，让用户感觉上像调用本地服务一样的调用远程服务。

### 1.3. 服务接口的本地调用

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

### 1.4. RPC 调用实践

#### 1.4.1. RPC 的实现切入口 - 反射调用

为了解决不同 JVM 环境（不同服务器）之间的 JAVA 对象方法的调用，可以使用反射模式来解决。

可以设想，只要通过网络传输，传入反射需要的目标对象(如：OrderService)、方法名称（如：getDetail）、方法的参数值（如："1"），这样就可以告知另一个JVM环境（服务器）需要调用反射的信息（target/method/arg），通过反射将 orderService.getDetail 调用起来

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

#### 1.4.2. 网络通信传递反射信息

在上一节的步骤中，可以知道，只要传递 target/method/arg等三要素，就可以执行想要的目标服务方法，所以目前只需要解决target/method/arg 三种信息的网络传输问题即可。

网络通信的方法很多，如 http/rmi/webservice 等等。本次示例选用 JDK 的 rmi 方式，其使用方式如下

##### 1.4.2.1. 定义一个接口继承自 remote 接口

- 创建 InfoService 接口

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

##### 1.4.2.2. 服务提供方通过 RMI 开放服务到指定 URL

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

##### 1.4.2.3. 消费端通过 RMI 远程 URL 连接并调用

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

#### 1.4.3. 远程调用的融合

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

#### 1.4.4. 对客户端友好的透明化封装

经过前面的测试示例，RPC 的整个调用链条已经拉通，但是还有一个易出错的地方，就是客户端封装反射信息的地方，功能不够内聚，容易出现错误，代码易读性也很差

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

Apache Dubbo™ 是一款高性能 Java RPC 框架。致力于提供高性能和透明化的 RPC 远程服务调用方案，以及 SOA 服务治理方案。

> - 官网（中文）：https://cn.dubbo.apache.org/zh-cn/
> - 最新官方文档：https://cn.dubbo.apache.org/zh-cn/overview/home/

### 2.1. Dubbo 实现 RPC 框架

分布式服务架构下，各个服务间的相互RPC调用会越来越复杂。最终形成网状结构，此时服务的治理极为关键。

Dubbo 是一个带有服务治理功能的 RPC 框架，提供了一套较为完整的服务治理方案，其底层直接实现了 RPC 调用的全过程，并尽力使 RPC 远程对使用者透明。下图展示了Dubbo服务治理的功能。

![dubbo-service-governance](images/20200125181329487_3748.jpg)

简单的说，Dubbo 本质上就是个服务调用的框架，如果没有分布式的需求，其实是不需要用的，只有在分布式的时候，才有使用 Dubbo 这样的远程调用分布式服务框架的需求。其核心部分包含：

- 远程通讯：提供对多种基于长连接的NIO框架抽象封装，包括多种线程模型、序列化以及“请求-响应”模式的信息交换方式。
- 集群容错：提供基于接口方法的透明远程过程调用，包括多协议支持以及软负载均衡，失败容错、地址路由、动态配置等集群支持。
- 自动发现：基于注册中心目录服务，使服务消费方能动态的查找到×××提供方，使地址透明，使服务提供方可以平滑增加或减少机器。

### 2.2. Dubbo 的架构及特点

dubbo 的整体结构如下图：

![Dubbo架构设计图](images/20200125183026192_30674.png)

Dubbo 总体架构设计一共划分了 10 层，而最上面的 Service 层是留给实际想要使用 Dubbo 开发分布式服务的开发者实现业务逻辑的接口层。图中左边淡蓝背景的为服务消费方使用的接口，右边淡绿色背景的为服务提供方使用的接口，位于中轴线上的为双方都用到的接口

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

从上图可以看出，Dubbo 对于服务提供方和服务消费方，从框架的 10 层中分别提供了各自需要关心和扩展的接口，构建整个服务生态系统(服务提供方和服务消费方本身就是一个以服务为中心的)

### 2.3. Dubbo 服务的角色关系

服务提供方和服务消费方之间的调用关系，如图所示：

![](images/20200125184203559_27446.png)

**节点角色说明**：

|    节点    |             角色说明              |
| --------- | -------------------------------- |
| Provider  | 暴露服务的服务提供方                |
| Consumer  | 调用远程服务的服务消费方             |
| Registry  | 服务注册与发现的注册中心             |
| Monitor   | 统计服务的调用次数和调用时间的监控中心 |
| Container | 服务运行容器                       |

**调用关系说明**：

0. 服务容器负责启动，加载，运行服务提供者。
1. 服务提供者在启动时，向注册中心注册自己提供的服务。
2. 服务消费者在启动时，向注册中心订阅自己所需的服务。
3. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
4. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
5. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。

## 3. 快速入门

### 3.1. 基于 Dubbo Spring Boot Starter（使用 Nacos 注册中心）微服务实践

基于 Nacos 作为注册中心的 Dubbo x Spring Boot 的微服务开发快速开始示例，用于了解 Dubbo x Spring Boot 配置方式。

#### 3.1.1. 启动 nacos

启动 nacos 作为服务注册中心，nacos 的使用参考《Spring-Cloud-Alibaba-Nacos 笔记》

#### 3.1.2. 创建聚合工程与 API 模块

创建示例聚合工程，引入以下依赖：

```xml
 <!-- 版本号管理 -->
<properties>
    <!-- maven插件版本 -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <!-- spring boot 版本 -->
    <spring-boot.version>2.3.10.RELEASE</spring-boot.version>
    <!-- Spring Cloud Alibaba 版本 -->
    <spring-cloud-alibaba.version>2.1.4.RELEASE</spring-cloud-alibaba.version>
    <!--ncaos 注册dubbo 服务 -->
    <dubbo-registry-nacos.version>2.7.5</dubbo-registry-nacos.version>
</properties>

<!-- 锁定依赖版本 -->
<dependencyManagement>
    <dependencies>
        <!-- SpringBoot的依赖配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- Spring Cloud Alibaba -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${spring-cloud-alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- dubbo 与 nacos 整合 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>${dubbo-registry-nacos.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Package as an executable jar -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

![](images/259742823230556.png)

> Notes: 此示例 Spring Cloud Alibaba 包含的 dubbo 版本是 2.7.8

创建服务接口工程 dubbo-nacos-api，定义用于测试的接口

```java
public interface HelloService {
    String hello(String name);
}
```

#### 3.1.3. 服务提供者

1. 引入服务接口工程、dubbo、nacos 等相关依赖

```xml
 <dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 服务接口工程 -->
    <dependency>
        <groupId>com.moon</groupId>
        <artifactId>dubbo-nacos-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <!-- nacos 注册中心依赖 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <!-- dubbo 依赖 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-dubbo</artifactId>
    </dependency>
    <!-- dubbo 注册到 nacos 依赖 -->
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo-registry-nacos</artifactId>
    </dependency>
</dependencies>
```

2. 创建服务接口的实现类，在类中添加了 `@DubboService` 注解，通过这个配置可以基于 Spring Boot 去发布 Dubbo 服务。

> Notes: 要注意使用 dubbo 注解，而不是使用 Spring 的注解。其中 dubbo 以前版本的 `@Service` 注解已经过时，示例使用了官方推荐的 `@DubboService`。

```java
package com.moon.dubbo.nacos.service;

import org.apache.dubbo.config.annotation.DubboService;

// @Service // org.apache.dubbo.config.annotation.Service 注解已过时
@DubboService // 标识当前类为 dubbo 的服务提供者
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
```

3. 创建启动类，并使用 `@EnableDiscoveryClient` 开启服务发现功能

```java
@EnableDiscoveryClient // 开启服务发现功能
@SpringBootApplication
public class DubboNacosProvider {
    public static void main(String[] args) {
        SpringApplication.run(DubboNacosProvider.class, args);
    }
}
```

4. 在 resources 资源文件夹下建立 application.yml 文件，定义了 Dubbo 的应用名、Dubbo 协议信息、Dubbo 使用的注册中心地址。定义如下：

```yml
server:
  port: 8080
spring:
  application:
    name: dubbo-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
dubbo:
  registry:
    address: spring-cloud://localhost
  scan:
    base-packages: com.moon.dubbo.nacos.service
  protocol:
    name: dubbo
    port: -1
```

#### 3.1.4. 服务消费者

1. 服务消费端同样引入服务接口工程、dubbo、nacos 等相关依赖。*具体参考“服务提供者”*
2. 创建启动类，并使用 `@EnableDiscoveryClient` 开启服务发现功能。*具体参考“服务提供者”*
3. 在 resources 资源文件夹下建立 application.yml 文件，定义了 Dubbo 的应用名、Dubbo 协议信息、Dubbo 使用的注册中心地址。定义如下：

```java
server:
  port: 8081
spring:
  application:
    name: dubbo-consumer
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
dubbo:
  registry:
    address: spring-cloud://localhost
  protocol:
    name: dubbo
    port: -1
```

> Tips: 此配置与服务提供者不同的地方是，不需要配置 `dubbo.scan.base-packages` 服务接口扫描包路径。

4. 在 Spring Boot 模式下还可以基于 CommandLineRunner 去创建消费端请求的任务

```java
import com.moon.dubbo.nacos.service.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 dubbo RPC 调用接口
 */
@RestController
public class TestController {
    // @Reference(check=false) // @Reference 注解用于引用服务接口（已过时）
    // 使用 dubbo 的 @DubboReference 注解引用服务接口，其中 check 属性为 false时，启动时不会去检查是否有可用的服务接口引用
    @DubboReference(check = false)
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        // 调用服务接口
        return helloService.hello(name);
    }
}
```

扩展：在 Spring Boot 模式下还可以基于 `CommandLineRunner` 去创建消费端请求的任务，即可以在消费端启动后进行远程服务接口的调用。在 Task 类中，通过 `@DubboReference` 从 Dubbo 获取了一个 RPC 订阅，可以像本地接口一样直接调用 `HelloService`，在 `run` 方法中创建了一个线程进行调用。

```java
@Component
public class Task implements CommandLineRunner {

    @DubboReference
    private HelloService helloService;

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println(new Date() + " Receive result ======> " + helloService.hello("world"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
```

#### 3.1.5. 测试

启动 nacos、dubbo-nacos-provider、dubbo-nacos-consumer。在 nacos 控制台可以看到相应的服务列表：

![](images/344912916248981.png)

消费端启动后在任务中输出了调用远程服务接口的内容：

![](images/47143016236848.png)

调用消费端的测试接口，可以成功调用远程服务接口并返回结果：

![](images/547453016257014.png)

### 3.2. Dubbo 整合 Nacos 配置中心实践

基于上个章节『基于 Dubbo Spring Boot Starter（使用 Nacos 注册中心）微服务实践』，将项目修改为使用 Nacos Config 来管理项目配置。具体实现步骤如下：

#### 3.2.1. 添加 Nacos Config 依赖

在 dubbo-nacos-consumer、dubbo-nacos-provider 均添加 Nacos Config 的依赖。

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

#### 3.2.2. 更改本地与远程配置

以服务提供端（dubbo-nacos-provider）为例，移除（修改为别的名称）原来的 application.yml 文件，创建优先级最高的 bootstrap.yml 文件，只需要配置 nacos 相关选项：

```yml
spring:
  application:
    name: dubbo-provider
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

在 nacos 管理后台中，创建配置 dubbo-provider.yaml，添加原来 application.yml 文件的内容

![](images/272684816246238.png)

![](images/469864716249683.png)

成功修改后，按上个章节步骤测试即可

### 3.3. 基于 Spring XML 微服务实践

基于 Dubbo x Spring XML 的微服务快速开始示例，用于了解 Dubbo x Spring XML 配置方式。

> 参考官网：https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/quick-start/spring-xml/

### 3.4. 基于 Dubbo API 微服务实践

> 参考官网：https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/quick-start/api/

基于 Dubbo 的纯 API 的微服务快速开始示例，

## 4. Dubbo 基础配置使用

### 4.1. xml 文件配置方式

#### 4.1.1. dubbo 标签关系图

![](images/20200126122444209_30129.jpg)

> 1. 标签属性有继承关系，即：下层有设置则使用，未配置则沿用上一级的设置
> 2. timeout/retries/loadbalance消费方未设置，则沿用服务方的设置

#### 4.1.2. dubbo 各标签作用汇总表

|          标签           |    用途     |                                       解释                                        |
| ---------------------- | ---------- | -------------------------------------------------------------------------------- |
| `<dubbo:service/>`     | 服务配置     | 用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心 |
| `<dubbo:reference/>`   | 引用配置     | 用于创建一个远程服务代理，一个引用可以指向多个注册中心                                    |
| `<dubbo:protocol/>`    | 协议配置     | 用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受                               |
| `<dubbo:application/>` | 应用配置     | 用于配置当前应用信息，不管该应用是提供者还是消费者                                        |
| `<dubbo:module/>`      | 模块配置     | 用于配置当前模块信息，可选                                                            |
| `<dubbo:registry/>`    | 注册中心配置 | 用于配置连接注册中心相关信息                                                          |
| `<dubbo:monitor/>`     | 监控中心配置 | 用于配置连接监控中心相关信息，可选                                                     |
| `<dubbo:provider/>`    | 提供方配置   | 当 `ProtocolConfig` 和 `ServiceConfig` 某属性没有配置时，采用此缺省值，可选             |
| `<dubbo:consumer/>`    | 消费方配置   | 当 `ReferenceConfig` 某属性没有配置时，采用此缺省值，可选                               |
| `<dubbo:method/>`      | 方法配置     | 用于 `ServiceConfig` 和 `ReferenceConfig` 指定方法级的配置信息                        |
| `<dubbo:argument/>`    | 参数配置     | 用于指定方法参数配置                                                                 |

#### 4.1.3. 标签详解

所有配置项分为三大类，参见以下各个标签作用表中的"作用"一列

- 服务发现：表示该配置项用于服务的注册与发现，目的是让消费方找到提供方
- 服务治理：表示该配置项用于治理服务间的关系，或为开发测试提供便利条件
- 性能调优：表示该配置项用于调优性能，不同的选项对性能会产生影响
- 所有配置最终都将转换为URL表示，并由服务提供方生成，经注册中心传递给消费方，各属性对应URL的参数，参见配置项一览表中的"对应URL参数"列。

> URL 格式：`protocol://username:password@host:port/path?key=value&key=value`

**注意：只有group，interface，version是服务的匹配条件，三者决定是不是同一个服务，其它配置项均为调优和治理参数。**

> **各个标签详细属性配置参考官网：http://dubbo.apache.org/zh-cn/docs/user/references/xml/introduction.html**

##### 4.1.3.1. dubbo:service【常用】

服务提供者暴露服务配置。对应的配置类：`org.apache.dubbo.config.ServiceConfig`。常用属性如下：

|    属性    | 对应URL参数 |  类型   | 是否必填  | 缺省值 |   作用   |                                       描述                                        | 兼容性  |
| --------- | ---------- | ------ | -------- | ----- | ------- | --------------------------------------------------------------------------------- | ------ |
| interface |            | class  | **必填** |       | 服务发现 | 服务接口名                                                                         | 1.0.0+ |
| ref       |            | object | **必填** |       | 服务发现 | 服务对象实现引用                                                                    | 1.0.0+ |
| version   | version    | string | 可选     | 0.0.0 | 服务发现 | 服务版本，建议使用两位数字版本，如：1.0，通常在接口不兼容时版本号才需要升级                  | 1.0.0+ |
| timeout   | timeout    | int    | 可选     | 1000  | 性能调优 | 远程服务调用超时时间(毫秒)                                                            | 2.0.0+ |
| protocol  |            | string | 可选     |       | 配置关联 | 使用指定的协议暴露服务，在多协议时使用，值为`<dubbo:protocol>`的id属性，多个协议ID用逗号分隔 | 2.0.5+ |

> 全部属性列表详见官方文档（2.7版本）：https://dubbo.apache.org/zh/docs/v2.7/user/references/xml/dubbo-service/

##### 4.1.3.2. dubbo:reference【常用】

服务消费者引用服务配置。对应的配置类：`org.apache.dubbo.config.ReferenceConfig`。常用属性如下：

|    属性    | 对应URL参数 |  类型   | 是否必填  | 缺省值 |   作用   |      描述      |    兼容性     |
| --------- | ---------- | ------ | -------- | ----- | ------- | ------------- | ------------ |
| id        |            | string | **必填** |       | 配置关联 | 服务引用BeanId | 1.0.0以上版本 |
| interface |            | class  | **必填** |       | 服务发现 | 服务接口名     | 1.0.0以上版本 |

##### 4.1.3.3. dubbo:protocol【常用】

服务提供者协议配置。对应的配置类：`org.apache.dubbo.config.ProtocolConfig`。同时，如果需要支持多协议，可以声明多个 `<dubbo:protocol>` 标签，并在 `<dubbo:service>` 中通过 protocol 属性指定使用的协议。常用属性如下：

| 属性  |  对应URL参数  |  类型   | 是否必填  |                                                                                                  缺省值                                                                                                   |   作用   |                                                   描述                                                    |    兼容性     |
| ---- | ------------ | ------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- | -------------------------------------------------------------------------------------------------------- | ------------ |
| id   |              | string | 可选     | dubbo                                                                                                                                                                                                    | 配置关联 | 协议BeanId，可以在`<dubbo:service protocol="">`中引用此ID，如果ID不填，缺省和name属性值一样，重复则在name后加序号。 | 2.0.5以上版本 |
| name | `<protocol>` | string | **必填** | dubbo                                                                                                                                                                                                    | 性能调优 | 协议名称                                                                                                  | 2.0.5以上版本 |
| port | `<port>`     | int    | 可选     | dubbo协议缺省端口为20880，rmi协议缺省端口为1099，http和hessian协议缺省端口为80；如果没有配置port，则自动采用默认端口，如果配置为-1，则会分配一个没有被占用的端口。Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。 | 服务发现 | 服务端口                                                                                                  | 2.0.5以上版本 |
| host | `<host>`     | string | 可选     | 自动查找本机IP                                                                                                                                                                                             | 服务发现 | -服务主机名，多网卡选择或指定VIP及域名时使用，为空则自动查找本机IP，-**建议不要配置，让Dubbo自动获取本机IP**           | 2.0.5以上版本 |

##### 4.1.3.4. dubbo:registry【常用】

注册中心配置。对应的配置类：`org.apache.dubbo.config.RegistryConfig`。同时如果有多个不同的注册中心，可以声明多个 `<dubbo:registry>` 标签，并在 `<dubbo:service>` 或 `<dubbo:reference>` 的 registry 属性指定使用的注册中心。常用属性如下：

|   属性    |  对应URL参数   |  类型   | 是否必填  | 缺省值 |   作用   |                                                                   描述                                                                    |     兼容性     |
| -------- | ------------- | ------ | -------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| id       |               | string | 可选     |       | 配置关联 | 注册中心引用BeanId，可以在`<dubbo:service registry="">`或`<dubbo:reference registry="">`中引用此ID                                            | 1.0.16以上版本 |
| address  | `<host:port>` | string | **必填** |       | 服务发现 | 注册中心服务器地址，如果地址没有端口缺省为9090，同一集群内的多个地址用逗号分隔，如：`ip:port,ip:port`，不同集群的注册中心，请配置多个`<dubbo:registry>`标签 | 1.0.16以上版本 |
| protocol | `<protocol>`  | string | 可选     | dubbo | 服务发现 | 注册中心地址协议，支持dubbo, multicast, zookeeper, redis, consul(2.7.1), sofa(2.7.2), etcd(2.7.2), nacos(2.7.2)等协议                         | 2.0.0以上版本  |


##### 4.1.3.5. dubbo:monitor

监控中心配置。对应的配置类：`org.apache.dubbo.config.MonitorConfig`

##### 4.1.3.6. dubbo:application【常用】

应用信息配置。对应的配置类：`org.apache.dubbo.config.ApplicationConfig`。常用属性如下：

| 属性  | 对应URL参数  |  类型   | 是否必填  | 缺省值 |   作用   |                                                                                                                                                   描述                                                                                                                                                    |     兼容性     |
| ---- | ----------- | ------ | -------- | ----- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| name | application | string | **必填** |       | 服务治理 | 当前应用名称，用于注册中心计算应用间依赖关系，**注意：消费者和提供者应用名不要一样，此参数不是匹配条件**，你当前项目叫什么名字就填什么，和提供者消费者角色无关，比如：kylin应用调用了morgan应用的服务，则kylin项目配成kylin，morgan项目配成morgan，可能kylin也提供其它服务给别人使用，但kylin项目永远配成kylin，这样注册中心将显示kylin依赖于morgan | 1.0.16以上版本 |


##### 4.1.3.7. dubbo:module

模块信息配置。对应的配置类：`org.apache.dubbo.config.ModuleConfig`

##### 4.1.3.8. dubbo:provider

服务提供者缺省值配置。对应的配置类：`org.apache.dubbo.config.ProviderConfig`。同时该标签为 `<dubbo:service>` 和 `<dubbo:protocol>` 标签的缺省值设置

##### 4.1.3.9. dubbo:consumer

服务消费者缺省值配置。配置类：`org.apache.dubbo.config.ConsumerConfig`。同时该标签为 `<dubbo:reference>` 标签的缺省值设置

##### 4.1.3.10. dubbo:method

方法级配置。对应的配置类：`org.apache.dubbo.config.MethodConfig`。同时该标签为 `<dubbo:service>` 或 `<dubbo:reference>` 的子标签，用于控制到方法级。如下例：

```xml
<dubbo:reference interface="com.xxx.XxxService">
    <dubbo:method name="findXxx" timeout="3000" retries="2" />
</dubbo:reference>
```

##### 4.1.3.11. dubbo:argument

方法参数配置。对应的配置类：`org.apache.dubbo.config.ArgumentConfig`。该标签为 `<dubbo:method>` 的子标签，用于方法参数的特征描述。如下例：

```xml
<dubbo:method name="findXxx" timeout="3000" retries="2">
    <dubbo:argument index="0" callback="true" />
</dubbo:method>
```

##### 4.1.3.12. dubbo:parameter

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

##### 4.1.3.13. dubbo:config-center

配置中心。对应的配置类：`org.apache.dubbo.config.ConfigCenterConfig`

#### 4.1.4. xml配置使用示例

> - xml的配置使用示例。详细参考dubbo-sample-xml工程
> - Springmvc的集成Dubbo的使用示例，详细参考busi-mvc工程

### 4.2. 属性配置方式

如果项目应用足够简单，例如，不需要多注册中心或多协议，并且需要在 spring 容器中共享配置。可以直接使用 `dubbo.properties` 作为默认配置。

### 4.3. 注解配置方式

> 详细示例参考：dubbo-sample-annotation工程

- 注解方式的底层与XML一致，只是表现形式上的不同。
- 目标都是配置Dubbo基础信息，主要涉及以下五个必不可少的信息：`ApplicationConfig`、`ProtocolConfig`、`RegistryConfig`、`service`、`reference`

#### 4.3.1. @EnableDubbo 开启服务

`@EnableDubbo` 注解：开启注解 Dubbo 功能，其中可以加入 `scanBasePackages` 属性配置包扫描的路径，用于扫描并注册 bean。其中封装了组件 `@DubboComponentScan`，来扫描Dubbo框架的 `@Service` 注解暴露 Dubbo 服务，以及扫描 Dubbo 框架的 `@Reference` 字段或者方法注入 Dubbo 服务代理。

#### 4.3.2. @Configuration 方式配置公共信息

`@Configuration` 指定配置类，在类中分别将 ApplicationConfig、ProtocolConfig、RegistryConfig 等类创建到 IOC 容器中即可，提供者配置（消费者配置方式一样）示例如下：

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

#### 4.3.3. property 属性配置方式自动装配公共信息

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

### 4.4. API 配置方式

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

## 5. Dubbo 控制台部署（前后端分离版本）

- 2.6版本前，在dubbo源码包里，有一个admin的war包，使用tomcat将其部署即可
- 从2.6版本之后，dubbo控制台已单独版本管理（目前只到0.1版本），使用了前后端分离的模式。前端使用Vue和Vuetify分别作为Javascript框架和UI框架，后端采用Spring Boot框架。

> 部署参考官网：http://dubbo.apache.org/zh-cn/docs/admin/introduction.html

拉取项目源码：`git clone https://github.com/apache/dubbo-admin.git`

### 5.1. Maven方式部署

- 安装，此方式即将前端vue产出的静态内容集成到springboot包内

```bash
cd dubbo-admin
mvn clean package
# 如果打包不成功，则尝试跳过test。mvn clean package -Dmaven.test.skip=true
cd dubbo-admin-distribution/target
java -jar dubbo-admin-0.1.jar
```

- 访问`http://localhost:8080`

### 5.2. 前后端分离部署

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

### 5.3. 配置

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

### 5.4. 服务治理

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

## 6. SPI机制原理

Dubbo框架是建立的SPI机制之上

### 6.1. Java SPI 机制

#### 6.1.1. SPI 简述

SPI全称Service Provider Interface，是Java提供的一套用来被第三方实现或者扩展的API，它可以用来启用框架扩展和替换组件

![Java SPI 机制调用流程图](images/20200130184515935_6858.jpg)

可以看到，SPI的本质，其实是帮助程序，为某个特定的接口寻找它的实现类。而且哪些实现类的会加载，是个动态过程（不是提前预定好的）

有点类似IOC的思想，就是将装配的控制权移到程序之外，在模块化设计中这个机制尤其重要。所以SPI的核心思想就是**解耦**

- 常见的例子：
    - 数据库驱动加载接口实现类的加载。JDBC加载不同类型数据库的驱动
    - 日志门面接口实现类加载，SLF4J加载不同提供商的日志实现类
    - Spring中大量使用了SPI，比如：对servlet3.0规范对ServletContainerInitializer的实现、自动类型转换Type Conversion SPI(Converter SPI、Formatter SPI)等

#### 6.1.2. 基础使用

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

#### 6.1.3. 核心功能类

需要指出的是，java之所以能够顺利根据配置加载这个实现类，完全依赖于jdk内的一个核心类：

![SPI的核心类](images/20200130231835343_15049.jpg)

### 6.2. Dubbo SPI机制

- Java SPI机制非常简单，就是读取指定的配置文件，将所有的类都加载到程序中。而这种机制，存在很多缺陷，比如：
    1. 所有实现类无论是否使用，直接被加载，可能存在浪费
    2. 不能够灵活控制什么时候什么时机，匹配什么实现，功能太弱
- Dubbo框架就基于自己的需要，对JAVA的SPI机制进行增强

#### 6.2.1. @SPI 注解

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

#### 6.2.2. @Activate 注解

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

#### 6.2.3. Javassist动态编译

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

#### 6.2.4. @Adaptive 注解

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

#### 6.2.5. Dubbo SPI的依赖注入

Dubbo SPI的核心实现类为ExtensionLoader，此类的使用几乎遍及Dubbo的整个源码体系

- ExtensionLoader有三个重要的入口方法，分别与`@SPI`、`@Activate`、`@Adaptive`注解对应
    - `getExtension`方法，对应加载所有的实现
    - `getActivateExtension`方法，对应解析加载`@Activate`注解对应的实现
    - `getAdaptiveExtension`方法，对应解析加载`@Adaptive`注解对应的实现
- 其中，`@Adaptive`注解作的自适应功能，还涉及到了代理对象（而Dubbo的代理机制，有两种选择，jdk动态代理和javassist动态编译类）

##### 6.2.5.1. Dubbo SPI 依赖注入场景

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

##### 6.2.5.2. Dubbo依赖注入的过程分析

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

## 7. 整合 Sentinel 系统防护

### 7.1. 概述

在复杂的生产环境下可能部署着成千上万的 Dubbo 服务实例，流量持续不断地进入，服务之间进行相互调用。但是分布式系统中可能会因流量激增、系统负载过高、网络延迟等一系列问题，导致某些服务不可用，如果不进行相应的控制可能导致级联故障，影响服务的可用性，因此如何对流量进行合理的控制，成为保障服务稳定性的关键。

Sentinel 是阿里中间件团队开源的，面向分布式服务架构的轻量级流量控制产品，主要以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度来帮助用户保护服务的稳定性。Dubbo 可整合 Sentinel 进行流量控制。

### 7.2. Sentinel Dubbo Adapter

Sentinel 提供 Dubbo 的相关适配 Sentinel Dubbo Adapter，用于适配 Dubbo 的资源，主要包括针对 Service Provider 和 Service Consumer 实现的 Filter。不同的 Dubbo 版本，相关模块也不同：

- `sentinel-apache-dubbo3-adapter`（兼容 Apache Dubbo 3.0.5 及以上版本，自 Sentinel 1.8.5 开始支持）
- `sentinel-apache-dubbo-adapter`（兼容 Apache Dubbo 2.7.x 及以上版本，自 Sentinel 1.5.1 开始支持）
- `sentinel-dubbo-adapter`（兼容 Dubbo 2.6.x 版本）

对于 Apache Dubbo 3.0.5 及以上版本，使用时需引入以下模块（以 Maven 为例）：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-apache-dubbo3-adapter</artifactId>
    <version>x.y.z</version>
</dependency>
```

对于 Apache Dubbo 2.7.x 及以上版本，使用时需引入以下模块（以 Maven 为例）：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-apache-dubbo-adapter</artifactId>
    <version>x.y.z</version>
</dependency>
```

对于 Dubbo 2.6.x 及以下版本，使用时需引入以下模块（以 Maven 为例）：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-dubbo-adapter</artifactId>
    <version>x.y.z</version>
</dependency>
```

引入此依赖后，Dubbo 的服务接口和方法（包括调用端和服务端）就会成为 Sentinel 中的资源，在配置了规则后就可以自动享受到 Sentinel 的防护能力。

### 7.3. Dubbo 整合 Sentinel 步骤

基于前面快速入门示例中『基于 Dubbo Spring Boot Starter（使用 Nacos 注册中心）微服务实践』工程代码，添加以下步骤：

#### 7.3.1. 添加 Sentinel 相关依赖

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-apache-dubbo-adapter</artifactId>
</dependency>
```

#### 7.3.2. 添加 sentinel 相关属性配置



```yml
```

## 8. 服务化最佳实践

### 8.1. 在 Provider 端应尽量配置的属性

Dubbo的属性配置优先度上，遵循顺序：`reference属性 -> service属性 -> Consumer 属性`

其中 reference 和 Consumer 是消费端配置，service 是服务端配置。

而对于服务调用的超时时间、重试次数等属性，服务的提供方比消费方更了解服务性能，因此我们应该在 Provider 端尽量多配置 Consumer 端属性，让其漫游到消费端发挥作用

#### 8.1.1. 在 Provider 端尽量多配置 Consumer 端属性

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
    4. `actives`：消费者端的最大并发调用限制，即当 Consumer 对一个服务的并发调用到上限后，新调用会阻塞直到超时，在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制

#### 8.1.2. 在 Provider 端配置合理的 Provider 端属性

```xml
<dubbo:protocol threads="200" />
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" executes="200">
    <dubbo:method name="findAllPerson" executes="50" />
</dubbo:service>
```

建议在 Provider 端配置的 Provider 端属性有：

1. `threads`：服务线程池大小
2. `executes`：一个服务提供者并行执行请求上限，即当 Provider 对一个服务的并发调用达到上限后，新调用会阻塞，此时 Consumer 可能会超时。在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制

### 8.2. 服务拆分最佳实现

#### 8.2.1. 分包

建议将服务接口、服务模型、服务异常等均放在 API 包中，因为服务模型和异常也是 API 的一部分，这样做也符合分包原则：重用发布等价原则(REP)，共同重用原则(CRP)。

如果需要，也可以考虑在 API 包中放置一份 Spring 的引用配置，这样使用方只需在 Spring 加载过程中引用此配置即可。配置建议放在模块的包目录下，以免冲突，如：com/alibaba/china/xxx/dubbo-reference.xml。

#### 8.2.2. 粒度

服务接口尽可能大粒度，每个服务方法应代表一个功能，而不是某功能的一个步骤，否则将面临分布式事务问题，Dubbo 暂未提供分布式事务支持。

服务接口建议以业务场景为单位划分，并对相近业务做抽象，防止接口数量爆炸。

不建议使用过于抽象的通用接口，如：Map query(Map)，这样的接口没有明确语义，会给后期维护带来不便。

#### 8.2.3. 版本

每个接口都应定义版本号，为后续不兼容升级提供可能，如：`<dubbo:service interface="com.xxx.XxxService" version="1.0" />`

建议使用两位版本号，因为第三位版本号通常表示兼容升级，只有不兼容时才需要变更服务版本。

当不兼容时，先升级一半提供者为新版本，再将消费者全部升为新版本，然后将剩下的一半提供者升为新版本。

#### 8.2.4. 异常

建议使用异常汇报错误，而不是返回错误码，异常信息能携带更多信息，并且语义更友好。

如果担心性能问题，在必要时，可以通过 override 掉异常类的 `fillInStackTrace()` 方法为空方法，使其不拷贝栈信息。

查询方法不建议抛出 checked 异常，否则调用方在查询时将过多的 `try...catch`，并且不能进行有效处理。

服务提供方不应将 DAO 或 SQL 等异常抛给消费方，应在服务实现中对消费方不关心的异常进行包装，否则可能出现消费方无法反序列化相应异常。

## 9. 其他

### 9.1. dubbo框架使用示例

dubbo 框架使用示例项目参考：dubbo-note\dubbo-sample\

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
