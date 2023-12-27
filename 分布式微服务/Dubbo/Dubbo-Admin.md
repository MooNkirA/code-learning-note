## 1. Dubbo Admin 控制台概述

Dubbo Admin 是 Dubbo 官方提供的可视化 Web 交互控制台，基于 Admin 可以实时监测集群流量、服务部署状态、排查诊断问题。

- 查询服务、应用或机器状态
- 创建项目、服务测试、文档管理等
- 查看集群实时流量、定位异常问题等
- 流量比例分发、参数路由等流量管控规则下发

> 2.6 版本前，在 dubbo 源码包里，有一个 admin 的 war 包，将其部署到 tomcat 即可。从 2.6 版本之后，dubbo 控制台已单独版本管理（目前只到0.1版本），使用了前后端分离的模式。前端使用 Vue 和 Vuetify 分别作为 Javascript 框架和 UI 框架，后端采用 Spring Boot 框架。
>
> 部署参考官网：[Admin 控制台操作手册](https://cn.dubbo.apache.org/zh-cn/overview/reference/admin/)

拉取项目源码：`git clone https://github.com/apache/dubbo-admin.git`

## 2. Maven 方式部署

- 安装，此方式即将前端 vue 产出的静态内容集成到 Spring Boot 包内

```bash
cd dubbo-admin
mvn clean package
# 如果打包不成功，则尝试跳过test。mvn clean package -Dmaven.test.skip=true
cd dubbo-admin-distribution/target
java -jar dubbo-admin-0.1.jar
```

- 访问`http://localhost:8080`

## 3. 前后端分离部署

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

## 4. 配置

- 配置文件为：`dubbo-admin-server/src/main/resources/application.properties`
- 主要的配置

```properties
admin.config-center=zookeeper://127.0.0.1:2181
admin.registry.address=zookeeper://127.0.0.1:2181
admin.metadata-report.address=zookeeper://127.0.0.1:2181
```

三个配置项分别指定了配置中心，注册中心和元数据中心的地址，关于这三个中心的详细说明，可以参考这里。 也可以和 Dubbo 2.7 一样，在配置中心指定元数据和注册中心的地址，以 zookeeper 为例，配置的路径和内容如下：

```properties
# /dubbo/config/dubbo/dubbo.properties
dubbo.registry.address=zookeeper://127.0.0.1:2181
dubbo.metadata-report.address=zookeeper://127.0.0.1:2181
```

配置中心里的地址会覆盖掉本地 application.properties 的配置

## 5. 服务治理

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
