## 1. Spring Cloud Bus 概述

Spring Cloud Bus 是将分布式系统的节点与一个轻量级的消息代理联系起来，用于广播状态变化（如配置变化）或其他管理指令变化，实现微服务应用配置信息的动态更新，也可用于监控。例如，在配置中心如果修改了某个配置文件，发送一次请求，所有的客户端便会重新读取配置文件。

该项目包括 AMQP 和 Kafka 代理的实现。另外，在 classpath 上找到的任何 Spring Cloud Stream 绑定器都可以作为传输工具开箱即用。

> 官网：https://spring.io/projects/spring-cloud-bus

