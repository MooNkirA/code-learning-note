# 万信金融 项目资料

> 万信金融项目文档、相关组件与服务部署信息

## 1. 项目文档

<ul class="docs">
  <li><a href="#/00-项目资料/07-万信金融/01-项目介绍与开发环境搭建">第01章 项目介绍与开发环境搭建</a></li>
  <li><a href="#/00-项目资料/07-万信金融/02-前端环境搭建与短信校验注册功能">第02章 前端环境搭建与短信校验注册功能</a></li>
  <li><a href="#/00-项目资料/07-万信金融/03-用户登陆与认证授权">第03章 用户登陆与认证授权</a></li>
  <li><a href="#/00-项目资料/07-万信金融/04-分布式事务解决方案-Hmily">第04章 分布式事务解决方案-Hmily</a></li>
  <li><a href="#/00-项目资料/07-万信金融/05-用户开户">第05章 用户开户</a></li>
  <li><a href="#/00-项目资料/07-万信金融/06-分库分表解决方案-Sharding-JDBC">第06章 分库分表解决方案 Sharding-JDBC</a></li>
  <li><a href="#/00-项目资料/07-万信金融/07-用户发标">第07章 用户发标</a></li>
  <li><a href="#/00-项目资料/07-万信金融/08-标的检索">第08章 标的检索</a></li>
  <li><a href="#/00-项目资料/07-万信金融/09-用户投标">第09章 用户投标</a></li>
  <li><a href="#/00-项目资料/07-万信金融/10-投标放款">第10章 投标放款</a></li>
  <li><a href="#/00-项目资料/07-万信金融/11-用户还款">第11章 用户还款</a></li>
  <li><a href="#/00-项目资料/07-万信金融/12-投标充值">第12章 投标充值</a></li>
  <li><a href="#/00-项目资料/07-万信金融/13-还款提现">第13章 还款提现</a></li>
  <li><a href="#/00-项目资料/07-万信金融/14-用户身份认证">第14章 用户身份认证</a></li>
  <li><a href="#/00-项目资料/07-万信金融/15-项目部署">第15章 项目部署</a></li>
  <li><a href="#/00-项目资料/07-万信金融/16-服务调用链路追踪">第16章 服务调用链路追踪</a></li>
</ul>

## 2. 项目git库与分支管理

### 2.1. 项目git库(包含前端、后端工程)

仓库地址：https://github.com/MooNkirA/wanxinp2p-project

### 2.2. 本项目的分支命名规范

- `master`：主分支，可直接发布的版本，不能直接在该分支上开发
- `develop`：开发主分支，代码永远是最新，所有新功能以这个分支来创建自己的开发分支，该分支只做只合并操作，不能直接在该分支上开发
- `feature-xxx`：功能开发分支，在develop上创建分支，以自己开发功能模块命名，功能测试正常后合并到develop分支

## 3. 项目目录说明

```lua
wanxinp2p-project 
├── document
├── wanxinp2p
│  ├── sms-server
│  ├── wanxinp2p-account-service
│  ├── wanxinp2p-api
│  ├── wanxinp2p-common
│  ├── wanxinp2p-consumer-service
│  ├── wanxinp2p-discover-server
│  └── wanxinp2p-gateway-server
├── wanxinp2p-frontend
│  ├── wanxinp2p-web-app
│  └── wanxinp2p-web-manager
```

## 4. 技术选型

### 4.1. 后端技术

|        技术框架         |      说明       |
| ---------------------- | --------------- |
| Spring Boot            | 容器+MVC框架     |
| Spring Cloud           | 微服务框架       |
| MyBatis                | ORM 框架        |
| MyBatis Plus           | MyBatis增强插件  |
| Spring Security Oauth2 | 认证和授权框架   |
| RocketMQ               | 消息队列         |
| Druid                  | 数据库连接池     |
| apollo                 | 配置中心         |
| elastic-job            | 分布式任务调度   |
| hmily-springcloud      | 分布式事务       |
| JWT                    | 令牌            |
| Lombok                 | 简化对象封装工具 |
| Swagger2               | 接口文档生成框架 |
| fastjson               | JSON 操作工具   |
|                        |                 |

### 4.2. 前端技术


## 5. 项目本地开发环境搭建（windows 系统）

### 5.1. 项目整体模块

|      模块名称       |      描述       |
| ------------------ | --------------- |
| wanxinp2p          | 项目后端服务工程 |
| wanxinp2p-frontend | 项目前端工程     |

### 5.2. 后端工程

#### 5.2.1. 后端服务与端口

|              工程名称               |         描述          | Port  |
| ---------------------------------- | --------------------- | :---: |
| wanxinp2p-discover-server          | 服务注册中心（Eureka） | 53000 |
| wanxinp2p-gateway-service          | 网关微服务             | 53010 |
| wanxinp2p-uaa-service              | UAA 服务              | 53020 |
| wanxinp2p-account-service          | 统一账号微服务         | 53030 |
| wanxinp2p-consumer-service         | 用户中心微服务         | 53050 |
| wanxinp2p-transaction-service      | 交易中心微服务         | 53060 |
| wanxinp2p-depository-agent-service | 存管代理微服务         | 53070 |
| wanxinp2p-repayment-service        | 还款微服务             | 53080 |
| wanxinp2p-content-search-service   | 内容搜索微服务         | 53090 |
| wanxinp2p-file-service             | 文件微服务             | 56082 |
| wanxindepository                   | 银行存管系统           | 55010 |

#### 5.2.2. 后端服务部署

### 5.3. 第三方组件服务

|     组件服务名称      |             描述             |   Port    |
| -------------------- | ---------------------------- | :-------: |
| apollo-portal        | apollo 配置中心              |   8070    |
| apollo-configservice | apollo 配置中心              |   8080    |
| apollo-adminservice  | apollo 配置中心              |   8090    |
| redis                | redis 非关系型数据库         |   6379    |
| sailing              | 短信验证码服务               |   56083   |
| RocketMQ             | 消息中间件服务               |   9876    |
| RocketMQ Console     | RocketMQ 图形化管理后台      |   7777    |
| ElasticSearch        | 全文检索工具                 |   9200    |
| ElasticSearch-head   | ElasticSearch 图形化管理工具 |   9200    |
| ZooKeeper            | Elastic-job                  | 2181/9999 |
| zipkin               | 日志采集工具                 |   9411    |

### 5.4. 前端工程

#### 5.4.1. 前端服务与端口

|        工程名称        |       描述       | Port |
| --------------------- | ---------------- | :--: |
| wanxinp2p-web-app     | 前端跨平台应用    | 8081 |
| wanxinp2p-web-manager | P2P平台管理端后台 | 8079 |

#### 5.4.2. 前端项目部署

- wanxinp2p-web-app 项目启动

```bash
cd wanxinp2p-frontend/wanxinp2p-web-app
npm run dev:h5
```

- wanxinp2p-web-manager 项目启动

```bash
cd wanxinp2p-frontend/wanxinp2p-web-manager
npm run dev
```



