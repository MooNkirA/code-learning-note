## 1. Mybatis-Flex 是什么

Mybatis-Flex 是一个优雅的 Mybatis 增强框架，它非常轻量、同时拥有极高的性能与灵活性。可以轻松的使用 Mybaits-Flex 链接任何数据库，其内置的 QueryWrapper【亮点】极大的减少了 SQL 编写的工作的同时，减少出错的可能性。

> 官网文档：https://mybatis-flex.com/

### 1.1. 特征

1. **轻量**：除了 MyBatis，没有任何第三方依赖（轻依赖）、没有任何拦截器，其原理是通过 SqlProvider 的方式实现的（轻实现）。同时，在执行的过程中没有任何的 Sql 解析（Parse）（轻运行）。有如下好处：
    1. 极高的性能；
    2. 极易对代码进行跟踪和调试；
    3. 更高的把控性。
2. **灵活**：支持 Entity 的增删改查、以及分页查询的同时，MyBatis-Flex 提供了 Db + Row（灵活）工具，可以无需实体类对数据库进行增删改查以及分页查询。与此同时，MyBatis-Flex 内置的 QueryWrapper（灵活）可以轻易的实现**多表查询、链接查询、子查询**等等常见的 SQL 场景。
3. **强大**：支持任意关系型数据库，还可以通过方言持续扩展，同时支持**多（复合）主键、逻辑删除、乐观锁配置、数据脱敏、数据审计、数据填充**等等功能。

## 2. 快速开始

### 2.1. 测试环境准备

创建数据库表

```sql
CREATE TABLE IF NOT EXISTS `tb_account` (
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       INTEGER,
    `birthday`  DATETIME
);

INSERT INTO tb_account(id, user_name, age, birthday)
VALUES (1, '张三', 18, '2020-01-11'),(2, '李四', 19, '2021-03-21');
```
