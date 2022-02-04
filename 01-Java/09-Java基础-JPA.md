# Java基础-JPA

## 1. JPA 概述

JPA（Java Persistence API）Java 持久化 API。是一套 Sun Java 官方制定的ORM标准

## 2. JPA 常用 API

### 2.1. Persistence 类

`Persistence` 类主要是用于读取配置文件，获得实体管理工厂。常用方法如下所示：

```java
public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName)
```

- 获得实体管理工厂。
    - `String persistenceUnitName`：配置文件中的 `persistenceUnitName`

示例：

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("crm");
```

### 2.2. EntityManagerFactory 接口

用于管理数据库的连接，获得操作对象实体管理类 `EntityManager`。`EntityManagerFactory` 是一个线程安全的对象，并且其创建极其浪费资源，所以编程的时候要保持它是单例的。常用方法如下所示：

```java
public EntityManager createEntityManager();
```

- 获取操作对象实体管理类

示例：

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("crm");
EntityManager entityManager = emf.createEntityManager();
```

### 2.3. EntityManager 接口

在 JPA 规范中，EntityManager 实体管理类是操作数据库的重要 API，它是线程不安全的，需要保持线程独有。常用方法如下所示：

```java
public EntityTransaction getTransaction();
```

- 获取事务对象，<font color=red>**但没有开启事务**</font>

```java
public void persist(Object entity);
```

- 插入数据

```java
public void remove(Object entity);
```

- 根据对象的主键id删除数据。<font color=red>**注意：使用JPA删除数据也必须使用持久化对象，即要先查询数据，再删除**</font>

```java
public <T> T merge(T entity);
```

- 如果数据库没有记录就保存，如果有记录就更新，重要的判断是 OID 是否相同，OID（Object ID）就是在配置文件配置为 `<id>` 属性。

```java
public <T> T find(Class<T> entityClass, Object primaryKey);
```

- 用于通过OID，获得一条记录，有延迟。相当于`get()`

```java
public <T> T getReference(Class<T> entityClass, Object primaryKey);
```

- 用于通过OID，获得一条记录，无延迟。相当于`load()`

```java
public Query createQuery(String qlString);
```

- 获取 JPQL 操作对象，参数是操作 HQL 语句，用于删除和更新

```java
public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass);
```

- 获取 JPQL 操作对象，用于查询操作

### 2.4. EntityTransaction 接口

用于管理事务（开始，提交，回滚）。获取事务（没有开启事务）：

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("crm");
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
```

常用方法如下所示：

```java
public void begin();
```

- 开启事务

```java
public void commit();
```

- 提交事务

```java
public void rollback();
```

- 回滚事务

### 2.5. TypedQuery 接口

`TypedQuery` 接口继承 `Query` 接口。用于操作 JPQL 的查询的。JPQL 和 HQL 一样。为什么 JPA 的标准，查询需要指定类型，目的就是为了让返回的数据没有没有警告

获取 `TypedQuery` 实例：

```java
EntityManager em = xxx;
TypedQuery<Xxx> query = em.createQuery("xxx", Xxx.class);
```

常用方法如下所示：

```java
int executeUpdate();
```

- 执行查询操作（好像不用）。此方法继承于 `Query` 接口

```java
TypedQuery<X> setParameter(int position, Object value);
```

- 设置 `?` 参数的值。
    - `int position`：是 `?` 占位符后面指定的下标
    - `Object value`：占位符的值

```java
TypedQuery<X> setParameter(String name, Object value);
```

- 设置命名参数的值。
    - `String name`：命名参数的名字，不带 `:` 号
    - `Object value`：命名参数的值

```java
TypedQuery<X> setFirstResult(int startPosition);
```

- 设置分页查询的起始数
    - `int startPosition`：查询语句 `limit` 的起始数

```java
TypedQuery<X> setMaxResults(int maxResult);
```

- 设置分页查询的数量
    - `int maxResult`：分页查询每页大小

```java
List<X> getResultList();
```

- 返回查询的结果List集合（查询所有的数据）

```java
X getSingleResult();
```

- 返回查询的结果是一条数据，常用聚合函数 `count()`，相当于 `uniqueResult()`

### 2.6. Query 接口

用于操作SQL的查询接口，执行没有返回数据的JPQL（增删改），<font color=red>**用于删除和更新**</font>

获取 `Query` 实例：

```java
EntityManager em = xxx;
Query query = em.createQuery("xxx");
```

常用方法如下所示：

```java
int executeUpdate();
```

- 执行删除和修改的操作

```java
Query setParameter(int position, Object value);
```

- 设置 `?` 参数的值。
    - `int position`：是 `?` 占位符后面指定的下标
    - `Object value`：占位符的值

```java
Query setParameter(String name, Object value);
```

- 设置命名参数的值。
    - `String name`：命名参数的名字，不带 `:` 号
    - `Object value`：命名参数的值

### 2.7. CriteriaBuilder 接口

用户使用标准查询接口 Criteria 查询接口

