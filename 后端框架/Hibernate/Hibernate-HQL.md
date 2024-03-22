## 1. HQL（Hibernate Query Language）概述

Hibernate 查询语言（HQL, Hibernate Query Language）是一种面向对象的查询语言，类似于 SQL，但不是去对表和列进行操作，而是面向对象和它们的属性，操作的是持久化对象。HQL 查询最终会被 Hibernate 翻译为传统的 SQL 查询从而对数据库进行操作。

注意：尽管能直接使用本地 SQL 语句，但还是建议尽可能的使用 HQL 语句。因为<font color=red>使用 HQL 操作数据库表的好处是，不同的关系型数据库使用的 HQL 的语法是相同的，从而避免不同数据库关于可移植性的麻烦，并且体现了 Hibernate 的 SQL 生成和缓存策略。（推荐）</font>

**关于大小写敏感性问题**：在 HQL 中一些关键字比如 SELECT，FROM 和 WHERE 等，是不区分大小写的，但是一些属性（比如 Java 类与属性的名称、表名、列名）是区分大小写。

## 2. Query 接口

### 2.1. 获取 Query 对象

`org.hibernate.Query` 接口可以方便地对数据库及持久对象进行查询，它可以有两种表达方式：HQL 语言或本地数据库的 SQL 语句。Query 经常被用来绑定查询参数、限制查询记录数量，并最终执行查询操作。

```java
public interface Query<R> extends TypedQuery<R>, CommonQueryContract
```

使用 Session 对象的 `createQuery` 方法获取 HQL 查询对象 `Query`，其中参数 `queryString` 是 HQL 查询语句。

```java
Query query = session.createQuery(String queryString);
```

### 2.2. Query 接口的常用方法

```java
List<R> list();
```

- 返回查询的结果 List 集合（查询所有符合条件的数据）

```java
default Query<R> setString(int position, String val)
default Query<R> setString(String name, String val)
```

- 根据参数位置/参数名称，绑定字符串类型的参数值

> Tips: 接口中还有其他数据类型 `setXxx` 方法，都是用于设置不同类型的参数，用法几乎一样。

```java
R uniqueResult();
```

- 返回与查询匹配的单个单个结果，如果查询没有返回结果，则返回 null。常用聚合函数，如 `count()`

```java
Query<R> setFirstResult(int startPosition);
```

- 设置分页查询的当前页首行记录的索引值（查询索引是从 0 开始）

```java
Query<R> setMaxResults(int maxResult);
```

- 设置分页查询的每页固定记录数，即 maxResults 个对象。

```java
int executeUpdate();
```

- 执行修改和删除的操作，返回成功执行的行数。继承自 `javax.persistence.Query`

## 3. FROM 语句

FROM 语句用于在存储中加载一个完整并持久的对象。以下是 FROM 语句的一些简单的语法：

- `from com.moon.pojo.Cat`：指定对象类全名
- `from Cat`：对象类名
- `from Cat c`：给类起别名 c

<font color=red>**注意：如果需要使用 `select` 查询所有数据，那么返回的是一个对象。不能使用 `*`**</font>。例如：

```sql
from Cat
-- 或者
select c from Cat c
```

在 HQL 可以直接指定类名，如下：

```java
String hql = "FROM Employee";
Query query = session.createQuery(hql);
List results = query.list();
```

在 HQL 中的 From 语句可以使用全限定名或者给类指定别名，如下：

```java
// 全限定名
Query query = session.createQuery("from Customer");
List<Customer> list = query.list(query);
// 给类指定别名
Query query2 = session.createQuery("select c from Customer c");
List<Customer> list2 = query.list(query2);
```

## 4. AS 语句

在 HQL 中 AS 语句用于给类分配别名，尤其是在长查询的情况下。例如之前的例子：

```java
Query query = session.createQuery("FROM Employee AS E");
List results = query.list();
```

关键字 `AS` 是可省略的，并且也可以在类名后直接指定一个别名，如下：

```java
Query query = session.createQuery("FROM Employee E");
List results = query.list();
```

## 5. SELECT 语句

SELECT 语句比 FROM 语句提供了更多的对结果集的控制。如果只想得到对象的其中的几个属性而不是整个对象时，则需要使用 SELECT 语句。例如仅需要获取 Employee 对象的 first_name 字段：

```java
Query query = session.createQuery("SELECT e.firstName FROM Employee e");
List results = query.list();
```

> Notes: 需要注意 `firstName` 是 `Employee` 对象的属性，而不是 `EMPLOYEE` 表的字段。

## 6. WHERE 语句

如果要精确地从数据库存储中返回特定对象，则需要使用 WHERE 语句。

```java
Query query = session.createQuery("FROM Employee e WHERE e.id = 10");
List results = query.list();
```

## 7. ORDER BY 语句

使用 ORDER BY 语句可以给 HQL 查询结果进行排序。可以利用任意一个属性对结果进行排序，包括升序或降序排序。

```java
Query query = session.createQuery("FROM Employee e WHERE e.id > 10 ORDER BY e.salary DESC");
List results = query.list();
```

对多个属性进行排序，只需要在 ORDER BY 语句后面添加要进行排序的属性，并且用逗号进行分隔：

```java
String hql = "FROM Employee e WHERE e.id > 10 " +
             "ORDER BY e.firstName DESC, e.salary DESC";
Query query = session.createQuery(hql);
List results = query.list();
```

## 8. GROUP BY 语句

GROUP BY 语句允许 Hibernate 将从数据库中获取的数据基于某种属性的值来进行分组。通常，该语句会使用得到的结果来包含一个聚合值。

```java
Query query = session.createQuery("SELECT SUM(e.salary), e.firtName FROM Employee e GROUP BY e.firstName");
List results = query.list();
```

## 9. 动态参数

### 9.1. ?（问号）占位符

通过 `?` 占位符来动态设置参数值，通过 `setString(int position, String val) ` 方法根据占位符位置来设置参数值。

```java
// 获取Query查询接口
Query query = session.createQuery("from Customer c where c.custName like ?");
// 设置 ? 占位符的值，如果是多个 ? 号，索引是从 0 开始
query.setString(0, "%剑%");
List<Customer> list = query.list(query);
```

### 9.2. 命名参数

Hibernate 的 HQL 查询功能支持命名参数。声明一个命名参数，例如 `:name`(注意参数前有冒号 `:`)，通过 `setString(String name, String val)` 方法来设置参数值。注意：设置命名参数时，不用冒号。

```java
// 声明一个命名参数，:name(注意参数前有冒号 :)
Query query = session.createQuery("from Customer c where c.custName like :name");
// 设置命名参数的值，注意：调用命名参数，不用冒号
query.setString("name", "%剑%");
List<Customer> list = query.list(query);

// 示例2 使用 setParameter 方法
Query query = session.createQuery("FROM Employee e WHERE e.id = :employee_id");
query.setParameter("employee_id", 10);
List results = query.list();
```

## 10. 聚合函数

HQL 类似于 SQL，支持一系列的聚合函数，它们以同样的方式在 HQL 和 SQL 中工作。常用的聚合函数如下：

- `avg(属性名)`：属性的平均值
- `sum(属性名)`：属性值的总和
- `min(属性名)`：属性值的最大值
- `max(属性名)`：属性值的最小值
-` count(*)`, `count(属性名)`, `count(distinct 属性名)`, `count(all 属性名)`：属性在结果中出现的次数

示例：

```java
// 获取Query查询接口
Query query = session.createQuery("select count(*) from Customer");
// 调用Query查询方法,查询返回只有一条数据uniqueResult
Long count = (Long) query.uniqueResult();

// distinct 关键字表示只计算行集中的唯一值。下面的查询只计算唯一的值：
String hql = "SELECT count(distinct E.firstName) FROM Employee E";
Query query = session.createQuery(hql);
List results = query.list();
```

> Notes: 聚合函数 `count(*)` 是一个固定写法，统计记录数，返回的是 Long 类型。

## 11. 分页查询

通过 `setFirstResult` 和 `setMaxResults` 方法进行分页查询。

```java
// 获取Query查询接口
Query query = session.createQuery("from Customer");
// 设置分页条件，分页的开始位置，下标从0开始
query.setFirstResult(2);
// 设置每页显示的数量
query.setMaxResults(4);
// 调用Query查询方法
List<Customer> list = query.list();
```

## 12. 投影查询（了解）

当查询的记录不是所有字段，而是指定的字段。如果需要使用一个实体类接收，那么需要一个有参数的构造方法（包含这指定的字段）。这种<font color=purple>有构造方法参数的查询，称为投影查询</font>。

```java
// 需求：查询客户表，只需要,custName,custSource
public void findCustomer() {
	// 获取Session对象
	Session session = HibernateUtil.getSession();
	// 获取Query查询接口
	Query query = session.createQuery("select new Customer(c.custName, c.custSource) from Customer c");
	// 调用Query查询方法
	List<Customer> list = query.list();
	// 关闭资源
	session.close();
	// 遍历输出结果
	for (Customer c : list) {
		System.out.println(c.getCustName());
	}
}

public void findCustomer2() {
	// 获取Session对象
	Session session = HibernateUtil.getSession();
	// 获取Query查询接口
	Query query = session.createQuery("select c.custName,c.custSource from Customer c");
	// 调用Query查询方法 返回的是一个Object[]类型。取数据不方便
	List<Object[]> list = query.list();
	// 关闭资源
	session.close();
	// 遍历输出结果
	for (Object[] obj : list) {
		System.out.println(obj[0] + "===" + obj[1]);
	}
}
```

## 13. 增删改操作

注意：HQL 是没有插入语法的，因为插入数据 `save()`/`saveOrUpdate()`/`update()` 不需条件判断，所以 Hibernate 没有实现 HQL 插入的语法。HQL 操作数据库的语法是更新和删除。

HQL Hibernate 3 较 HQL Hibernate 2，新增了批量更新功能和选择性删除工作的功能。`org.hibernate.Query` 查询接口包含一个 `executeUpdate()` 方法，可以执行 HQL 的 UPDATE 或 DELETE 语句。

### 13.1. UPDATE 语句

UPDATE 语句能够更新一个或多个对象的一个或多个属性。

```java
// 更新一个字段，需求，更新，客户名为剑圣的客户，的来源为：Dota2
public void update() {
	// 获取Session对象
	Session session = HibernateUtil.getSession();
	// 增删改操作，需要开启事务
	Transaction transaction = session.beginTransaction();
	// 获取Query操作接口
	Query query = session.createQuery("update Customer c set c.custSource = :source where c.custName = :name");
	// 设置命名参数的值
	query.setString("source", "Dota2");
	query.setString("name", "剑圣");
	// 执行操作,返回成功影响的行数
	int row = query.executeUpdate();
	System.out.println(row);
	// 提交事务
	transaction.commit();
	// 关闭资源
	session.close();
}
```

### 13.2. DELETE 语句

DELETE 语句可以用来删除一个或多个对象。

```java
// 删除，通过客户名字删除
public void delete() {
	// 获取Session对象
	Session session = HibernateUtil.getSession();
	// 增删改操作，需要开启事务
	Transaction transaction = session.beginTransaction();
	// 获取Query操作接口
	Query query = session.createQuery("delete from Customer c where c.custName = :name");
	// 设置命名参数的值
	query.setString("name", "拿来删除");
	// 执行操作,返回成功影响的行数
	int row = query.executeUpdate();
	System.out.println(row);
	// 提交事务
	transaction.commit();
	// 关闭资源
	session.close();
}
```

### 13.3. INSERT 语句

HQL 只有当记录从一个对象插入到另一个对象时才支持 INSERT INTO 语句。

```java
String hql = "INSERT INTO Employee(firstName, lastName, salary)"  + 
             "SELECT firstName, lastName, salary FROM old_employee";
Query query = session.createQuery(hql);
int result = query.executeUpdate();
System.out.println("Rows affected: " + result);
```
