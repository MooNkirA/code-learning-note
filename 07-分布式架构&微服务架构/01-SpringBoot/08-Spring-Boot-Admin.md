# Spring Boot Admin 监控工具

## 1. Spring Boot Admin 简介

- 官网：https://github.com/codecentric/spring-boot-admin
- 官方文档：https://codecentric.github.io/spring-boot-admin/

### 1.1. 为什么要使用 Spring Boot Admin

Spring Boot Actuator 提供了对单个 Spring Boot 应用的监控，信息包含应用状态、内存、线程、堆栈等，比较全面的监控了 Spring Boot 应用的整个生命周期，可以有效的帮我解决众多服务的健康检查、指标监控问题、配置管理、日志聚合问题、异常排查问题等等。

### 1.2. Spring Boot Admin来源背景

codecentric 的 Spring Boot Admin 是一个社区项目，用于管理和监视您的 Spring Boot®应用程序。这些应用程序在我们的 Spring Boot Admin Client 中注册（通过HTTP），或者是通过Spring Cloud®（例如Eureka，Consul）发现的。UI只是 Spring Boot Actuator 端点之上的 Vue.js 应用程序。

### 1.3. Spring Boot Admin功能介绍

Spring Boot Admin提供了很多服务治理方面的功能，利用它能节省我们很多在治理服务方面的时间和精力Spring Boot Admin提供了如下功能（包括但不限于）：

- 显示健康状态及详细信息，如JVM和内存指标、数据源指标、缓存指标
- 跟踪并下载日志文件
- 查看jvm系统-和环境属性
- 查看Spring启动配置属性方便loglevel管理
- 查看线程转储视图http-traces
- 查看http端点查看计划任务
- 查看和删除活动会话(使用spring-session)
- 状态更改通知(通过电子邮件、Slack、Hipchat…)
- 状态变化的事件日志(非持久性)
- 下载 heapdump
- 查看 Spring Boot 配置属性
- 支持 Spring Cloud 的环境端点和刷新端点
- 支持 K8s
- 易用的日志级别管理
- 与JMX-beans交互
- 查看线程转储
- 查看http跟踪
- 查看auditevents
- 查看http-endpoints
- 查看计划任务
- 查看和删除活动会话（使用 Spring Session ）
- 查看Flyway/Liquibase数据库迁移
- 状态变更通知（通过电子邮件，Slack，Hipchat等，支持钉钉）
- 状态更改的事件日志（非持久化）

## 2. Spring Boot Admin 基础使用

Spring Boot Admin 有两个角色，客户端(Client)和服务端(Server)。

- 应用程序作为Spring Boot Admin Client向为Spring Boot Admin Server注册
- Spring Boot Admin Server 的 UI 界面将 Spring Boot Admin Client 的 Actuator Endpoint 上的一些监控信息。

### 2.1. 使用步骤总结

admin-server 服务

1. 创建 admin-server 模块
2. 导入依赖坐标 admin-starter-server
3. 在引导类上添加 `@EnableAdminServer` 注解，启用监控功能

admin-client 服务

1. 创建 admin-client 模块
2. 导入依赖坐标 admin-starter-client
3. 配置相关信息：server地址等

启动 server 和 client 服务，访问 server 项目地址


