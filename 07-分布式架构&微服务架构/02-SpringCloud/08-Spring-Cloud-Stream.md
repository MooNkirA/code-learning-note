# Spring Cloud Stream

> Spring Cloud Stream 官网地址：https://spring.io/projects/spring-cloud-stream

在实际的企业开发中，消息中间件是至关重要的组件之一。消息中间件主要解决应用解耦，异步消息，流量削锋等问题，实现高性能，高可用，可伸缩和最终一致性架构。

但不同的中间件其实现方式，内部结构是不一样的。如常见的RabbitMQ和Kafka，由于这两个消息中间件的架构上的不同，像RabbitMQ有exchange，kafka有Topic，partitions分区，这些中间件的差异性导致在实际项目开发给开发人员造成了一定的困扰，如果用了两个消息队列的其中一种，后面的业务需求，想往另外一种消息队列进行迁移，这时候无疑就是一个灾难性的，一大堆东西都要重新推倒重新做，因为它跟项目系统耦合了

此时 Spring Cloud Stream 提供了一套用于消息中间件与系统应用解耦合的解决方案。

## 1. Spring Cloud Stream 概述

### 1.1. 简介

Spring Cloud Stream 由一个中间件中立的核组成。应用通过Spring Cloud Stream插入的input(相当于消费者consumer，它是从队列中接收消息的)和output(相当于生产者producer，它是从队列中发送消息的)通道与外界交流。通道通过指定中间件的Binder实现与外部代理连接。

业务开发者不再关注具体消息中间件，只需关注Binder对应用程序提供的抽象概念来使用消息中间件实现业务即可。

![](images/20201113164639372_15923.png)

> 说明：最底层是消息服务，中间层是绑定层，绑定层和底层的消息服务进行绑定，顶层是消息生产者和消息消费者，顶层可以向绑定层生产消息和和获取消息消费

### 1.2. 核心概念

#### 1.2.1. 绑定器

`Binder` 绑定器是Spring Cloud Stream中一个非常重要的概念。在没有绑定器这个概念的情况下，Spring Boot应用要直接与消息中间件进行信息交互的时候，由于各消息中间件构建的初衷不同，它们的实现细节上会有较大的差异性，这使得实现的消息交互逻辑就会非常笨重，因为对具体的中间件实现细节有太重的依赖，当中间件有较大的变动升级、或是更换中间件的时候，就需要付出非常大的代价来实施。

通过定义绑定器作为中间层，实现了应用程序与消息中间件(Middleware)细节之间的隔离。通过向应用程序暴露统一的Channel通过，使得应用程序不需要再考虑各种不同的消息中间件的实现。当需要升级消息中间件，或者是更换其他消息中间件产品时，需要做的就是更换对应的Binder绑定器而不需要修改任何应用逻辑，甚至可以任意的改变中间件的类型而不需要修改一行代码。

Spring Cloud Stream支持各种binder实现，*下面包含GitHub项目的链接*。

- [RabbitMQ](https://github.com/spring-cloud/spring-cloud-stream-binder-rabbit)
- [Apache Kafka](https://github.com/spring-cloud/spring-cloud-stream-binder-kafka)
- [Kafka Streams](https://github.com/spring-cloud/spring-cloud-stream-binder-kafka/tree/master/spring-cloud-stream-binder-kafka-streams)
- [Amazon Kinesis](https://github.com/spring-cloud/spring-cloud-stream-binder-aws-kinesis)
- [Google PubSub (partner maintained)](https://github.com/spring-cloud/spring-cloud-gcp/tree/master/spring-cloud-gcp-pubsub-stream-binder)
- [Solace PubSub+ (partner maintained)](https://github.com/SolaceProducts/spring-cloud-stream-binder-solace)
- [Azure Event Hubs (partner maintained)](https://github.com/microsoft/spring-cloud-azure/tree/master/spring-cloud-azure-stream-binder/spring-cloud-azure-eventhubs-stream-binder)
- [Apache RocketMQ (partner maintained)](https://github.com/alibaba/spring-cloud-alibaba/wiki/RocketMQ)

通过配置把应用和 Spring Cloud Stream 的 binder 绑定在一起，之后只需要修改 binder 的配置来达到动态修改topic、exchange、type等一系列信息而不需要修改一行代码。

#### 1.2.2. 发布/订阅模型

在 Spring Cloud Stream 中的消息通信方式遵循了**发布-订阅模式**，当一条消息被投递到消息中间件之后，它会通过共享的 `Topic` 主题进行广播，消息消费者在订阅的主题中收到它并触发自身的业务逻辑处理。

这里所提到的 `Topic` 主题是 Spring Cloud Stream 中的一个抽象概念，用来代表发布共享消息给消费者的地方。在不同的消息中间件中，`Topic` 可能对应着不同的概念，比如：在RabbitMQ中的它对应了`Exchange`、而在Kakfa中则对应了Kafka中的`Topic`。

![](images/20201113172849475_16449.png)

### 1.3. 版本关系（更新于2020.11.13）

| Spring Cloud Stream | Spring Boot | Spring Cloud |
| ------------------- | ----------- | ------------ |
| Horsham [3.0.x]     | 2.2.x       | Hoxton       |
| Germantown [2.2.x]  | 2.1.x       | Greenwich    |
| Fishtown [2.1.x]    | 2.1.x       | Greenwich    |
| Elmhurst [2.0.x]    | 2.0.x       | Finchley     |

## 2. 快速入门案例

### 2.1. 案例准备

本次Spring Cloud Stream案例是通过RabbitMQ作为消息中间件，需要先准备RabbitMQ的环境

> 更多RabbitMQ的内容详见：[\07-分布式架构&微服务架构\03-分布式消息中件间\03-RabbitMQ.md](/07-分布式架构&微服务架构/03-分布式消息中件间/03-RabbitMQ)

创建`15-springcloud-stream`聚合工程，引入SpringBoot父工程与SpringCloud版本控制

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>15-springcloud-stream</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>${project.artifactId}</name>
    <packaging>pom</packaging>
    <description>Spring Cloud Stream 消息发送组件基础使用示例项目</description>

    <!-- 引入 spring boot 父工程 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
    </parent>

    <!-- 版本控制 -->
    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud Greenwich 版本的依赖 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- 项目构建部分 -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2.2. 消息生产者

#### 2.2.1. 创建工程引入依赖

创建消息生产者子模块`stream-producer`，引入Spring Cloud Stream对于支持绑定RabbitMQ的依赖

```xml
<!-- Spring Cloud Stream 支持绑定 RabbitMQ 的依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

#### 2.2.2. 定义 binding 消息绑定器

Spring Cloud Stream 发送消息时需要定义一个接口，此接口方法的返回对象是`MessageChannel`。此示例直接使用Spring Cloud Stream内置的接口`Source`：

```java
package org.springframework.cloud.stream.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Bindable interface with one output channel.
 */
public interface Source {

	String OUTPUT = "output";

	@Output(Source.OUTPUT)
	MessageChannel output();
}
```

这就接口声明了一个 binding 命名为 `output`。这个binding声明了一个消息输出流，也就是消息的生产者。

#### 2.2.3. 修改项目配置

修改application.yml配置文件，配置项目的基本信息、RabbitMQ、Spring Cloud Stream的消息发送的绑定器

```yml
server:
  port: 7001 # 服务端口
spring:
  application:
    name: stream-producer # 指定服务名
  rabbitmq:
    addresses: 192.168.12.132
    username: guest
    password: guest
  cloud:
    stream: # Spring Cloud Stream 配置
      bindings:
        output: # Spring Cloud Stream内置的发送消息的通道（名称为output）
          destination: stream-sample-default  # 指定消息发送的目的地，在RabbitMQ中，发送到一个stream-sample-default的exchange中
          contentType: text/plain # 用于指定消息的类型
      binders:  # 配置绑定器
        defaultRabbit:
          type: rabbit # 指定绑定消息中间件的类型
```

配置说明：

- `spring.cloud.stream.bindings.消息输出流名称.contentType`：用于指定消息的类型。具体可以参考 [spring cloud stream docs](https://cloud.spring.io/spring-cloud-static/spring-cloud-stream/2.2.1.RELEASE/spring-cloud-stream.html#_common_binding_properties)
- `spring.cloud.stream.bindings.消息输出流名称.destination`：指定了消息发送的目的地，对应 RabbitMQ，会发送到 exchange 是 `stream-sample-default` 的所有消息队列中。

#### 2.2.4. 创建消息发送工具类

```java
package com.moon.stream.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 消息发送工具类，用于负责向消息中间件发送消息
 */
@Component // 注册到spring容器中
@EnableBinding(Source.class) // 绑定消息通道，此示例绑定的是Spring Cloud Stream内置的Source接口
public class MessageSender {
    /* 注入消息通道对象 */
    @Autowired
    /*
     * 此处需要指定注入bean的名称，否则会报错：
     *  org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.messaging.MessageChannel' available:
     *  expected single matching bean but found 3: output,nullChannel,errorChannel
     */
    @Qualifier("output")
    private MessageChannel messageChannel;

    /**
     * 发送消息
     *
     * @param obj 发送的内容
     */
    public void send(Object obj) {
        // 通过消息通过对象，发送MQ消息
        messageChannel.send(MessageBuilder.withPayload(obj).build());
    }
}
```

> <font color=red>**注：`@EnableBinding`注解绑定消息发送通道。可以标识在Spring容器管理的配置bean或者入口类上，但一般建议标识在消息发送相关的类上。**</font>

#### 2.2.5. 测试发送消息

- 创建消息生产者项目启动类

```java
package com.moon.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Cloud Stream 消息生产者（producer）启动类
 * <p>
 * Spring Cloud Stream 消息生产者的实现步骤：
 * 1. 引入Spring Cloud Stream的依赖
 * 2. 修改application.yml配置消息中间件与stream的相关配置
 * 3. 定义一个通道接口，通过接口中内置的messagechannel
 * 4. 标识@EnableBinding注解绑定对应通道
 * 5. 通过绑定的接口（或内置的Source接口）获取MessageChannel示例，再发送消息
 */
@SpringBootApplication
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}
```

- 编写测试方法

```java
package com.moon.stream.test;

import com.moon.stream.message.MessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 消息生产者测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerTest {
    @Autowired
    private MessageSender messageSender;

    /* 测试发送消息 */
    @Test
    public void sendMessage() {
        messageSender.send("Hello, Spring Cloud Stream Producer!");
    }
}
```

#### 2.2.6. 番外：不编写工具类，直接在启动类完成消息的发送

```java
package com.moon.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Spring Cloud Stream 消息生产者（producer）启动类
 */
@SpringBootApplication
@EnableBinding(Source.class) // 绑定消息通道，此示例绑定的是Spring Cloud Stream内置的Source接口
public class ProducerApplication implements CommandLineRunner {
    /* 注入消息通道对象 */
    @Autowired
    @Qualifier("output") // 此处需要指定注入bean的名称，否则会报错：Field messageChannel in com.moon.stream.ProducerApplication required a single bean, but 3 were found
    private MessageChannel messageChannel;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     */
    @Override
    public void run(String... args) throws Exception {
        // 通过消息通过对象，发送MQ消息
        messageChannel.send(MessageBuilder.withPayload("Hello, Spring Cloud Stream Producer!").build());
    }
}
```

> 注：实现了`CommandLineRunner`接口的`run()`方法会在所有的 Spring Beans 都初始化之后，`SpringApplication.run()`之前执行，适合应用程序启动之初的数据初始化工作。

### 2.3. 消息消费者

#### 2.3.1. 创建工程引入依赖

创建消息消费者子模块`stream-consumer`，引入Spring Cloud Stream对于支持绑定RabbitMQ的依赖。*与消费生产者依赖一样*

```xml
<!-- Spring Cloud Stream 支持绑定 RabbitMQ 的依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

#### 2.3.2. 定义 binding 消息绑定器

Spring Cloud Stream 接收消息与发送消息一致，也需要定义一个接口，此接口方法的返回对象是`SubscribableChannel`。此示例直接使用Spring Cloud Stream内置的接口`Sink`：

```java
package org.springframework.cloud.stream.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Bindable interface with one input channel.
 */
public interface Sink {

	String INPUT = "input";

	@Input(Sink.INPUT)
	SubscribableChannel input();
}
```

`@Input` 注解对应的方法，需要返回 `SubscribableChannel` 实例，并且指定一个参数值。这就接口声明了一个 binding 命名为 `input`

#### 2.3.3. 修改项目配置

修改application.yml配置文件，配置项目的基本信息、RabbitMQ、Spring Cloud Stream的消息获取的绑定器

```yml
server:
  port: 7002 # 服务端口
spring:
  application:
    name: stream-consumer # 指定服务名
  rabbitmq:
    addresses: 192.168.12.132
    username: guest
    password: guest
  cloud:
    stream: # Spring Cloud Stream 配置
      bindings:
        input: # Spring Cloud Stream内置的获取消息的通道（名称为input）
          destination: stream-sample-default # 指定消息获取的目的地，在RabbitMQ中，从一个stream-sample-default的exchange中获取消息
      binders:  # 配置绑定器
        defaultRabbit:
          type: rabbit # 指定绑定消息中间件的类型
```






