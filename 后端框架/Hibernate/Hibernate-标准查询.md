## 1. Criteria（Hibernate 标准查询）概述

```java
public interface Criteria extends CriteriaSpecification
```

`org.hibernate.Criteria` 接口与 `Query` 接口非常类似，允许创建并执行面向对象的标准化查询 API（QBC: Query By Criteria），是 Hibernate 提供了操纵对象和相应的 RDBMS 表中可用的数据的替代方法。它允许建立一个标准的可编程查询对象来应用过滤规则和逻辑条件。简单的理解就是完全是 Java 代码查询数据库，不用写 SQL。

值得注意的是 Criteria 接口也是轻量级的，它不能在 Session 之外使用。

## 2. Criteria 对象获取

`Session` 是 `Criteria` 实例的工厂，通过其 `createCriteria` 方法获取 `Criteria` 对象。

```java
@Deprecated
Criteria createCriteria(Class persistentClass);
@Deprecated
Criteria createCriteria(Class persistentClass, String alias);
@Deprecated
Criteria createCriteria(String entityName);
@Deprecated
Criteria createCriteria(String entityName, String alias);
```

其中参数 `persistentClass` 是指定查询的类字节码对象。

## 3. 查询所有

```java
public List list() throws HibernateException;
```

- 返回查询的结果 List 集合（查询所有的数据）

## 4. 条件查询

### 4.1. 增加条件

```java
public Criteria add(Criterion criterion);
```

- 添加查询条件，用于检索结果集。参数 `Criterion criterion` 是查询的限制条件，相当于 SQL 语句中 where 后面的条件，取值是通过 `org.hibernate.criterion.Restrictions` 相应静态方法获取的 `Criterion` 对象。

### 4.2. Restrictions 类（QBC 限制条件）

QBC 限制条件 Restrictions 类常用方法：

```java
public static SimpleExpression eq(String propertyName, Object value)
```

- 等同于 SQL 语句查询条件的`=`（等于）判断

```java
public static Criterion allEq(Map<String,?> propertyNameValues)
```

- 等同于 SQL 语句查询条件使用 Map 中 key/value 进行多个字段`=`（等于）的判断 

```java
public static SimpleExpression gt(String propertyName, Object value)
```

- 等同于 SQL 语句查询条件的`>`（大于）判断

```java
public static SimpleExpression ge(String propertyName, Object value)
```

- 等同于 SQL 语句查询条件的`>=`（大于等于）判断

```java
public static SimpleExpression lt(String propertyName, Object value)
```

- 等同于 SQL 语句查询条件的`<`（小于）判断

```java
public static SimpleExpression le(String propertyName, Object value)
```

- 等同于 SQL 语句查询条件的`<=`（小于等于）判断

```java
public static Criterion between(String propertyName, Object low, Object high)
```

- 等同于 SQL 语句查询条件的 `between` 子句

```java
public static SimpleExpression like(String propertyName, Object value)
public static SimpleExpression like(String propertyName, String value, MatchMode matchMode)
```

- 等同于 SQL 语句查询条件的 `like` 子句

```java
public static Criterion in(String propertyName, Object... values)
public static Criterion in(String propertyName, Collection values)
```

- 等同于 SQL 语句查询条件的 `in` 子句

```java
public static LogicalExpression and(Criterion lhs, Criterion rhs)
public static Conjunction and(Criterion... predicates)
```

- 等同于 SQL 语句查询条件的 `and` 关系

```java
public static LogicalExpression or(Criterion lhs, Criterion rhs)
public static Disjunction or(Criterion... predicates)
```

- 等同于 SQL 语句查询条件的 `or` 关系

```java
public static Criterion sqlRestriction(String sql, Object[] values, Type[] types)
public static Criterion sqlRestriction(String sql, Object value, Type type)
public static Criterion sqlRestriction(String sql)
```

- SQL 限定查询 

## 5. 分页查询

```java
public Criteria setFirstResult(int firstResult);
```

- 设置分页查询的首页页码（查询索引是从0开始）

```java
public Criteria setMaxResults(int maxResults);
```

- 设置分页查询的每页记录数

## 6. 统计查询

```java
public Criteria setProjection(Projection projection);
```

- QBC 使用聚合函数统计查询。参数 `Projection`：要添加的查询投影。

### 6.1. Projection

```java
public interface Projection extends Serializable 
```

`org.hibernate.criterion.Projection` 接口是作为离线查询对象的聚合函数条件。

### 6.2. Projections 工具类常用方法

```java
public static CountProjection count(String propertyName)
```

- 设置查询聚合函数语句

```java
public static Projection rowCount()
```

- 返回 `Projection` 对象，设置离线对象的查询条件，相当于 `count(*)`。

### 6.3. 示例

```java
// QBC使用聚合函数：统计查询
@Test
public void testProjection() {
	// 获取Session对象
	Session session = HibernateUtil.getSession();
	// 开启事务
	Transaction tx = session.beginTransaction();
	// 获取Criteria对象,相当于from Customer 或 select * from cst_customer
	Criteria c = session.createCriteria(Customer.class);
	// 想办法把 select * 变成 select count(*)
	c.setProjection(Projections.count("custId"));
	Long total = (Long) c.uniqueResult();
	System.out.println(total);
	// 提交事务
	tx.commit();
	// 关闭资源
	session.close();
}
```

## 7. 离线查询 DetachedCriteria

### 7.1. 概述

Criteria 对象是一个在线对象，它是由一个可用的（活动的）Session 对象获取的出来的。当 session 失效时，就无法再获取该对象了。

有与 Criteria 相对的一个对象，它也可以用于设置条件，但是获取的时候并不需要 Session 对象。该对象就叫做离线对象：`DetachedCriteria`。使用该对象进行的查询就叫做：**离线查询**。

### 7.2. 常用方法

```java
public static DetachedCriteria forClass(Class clazz);
public static DetachedCriteria forClass(Class clazz, String alias);
```

- 通过 `DetachedCriteria` 类中的 `forClass` 静态方法，根据传入持久化实体类创建离线查询对象。相当于：`select * from Xxx表`

```java
public DetachedCriteria add(Criterion criterion)
```

- 给离线查询对象增加查询条件。参数 `Criterion` 是 QBC 限制条件的语句。

```java
public DetachedCriteria setProjection(Projection projection)
```

- 给离线查询对象增加聚合函数的查询条件。例如，`dc.setProjection(Projections.rowCount())`；如果去掉聚合函数条件则 `dc.setProjection(null)`

### 7.3. 基础示例

```java
// 离线查询
@Test
public void testDetachedCriteria() {
	// 模拟一次web操作: 浏览器发送请求——调用servlet——调用service——调用dao——拿到结果到jsp上展示
	List list = servletFindAllCustomer();
	for (Object o : list) {
		System.out.println(o);
	}
}

// 模拟 servlet
public List<Customer> servletFindAllCustomer() {
	// 离线对象
	DetachedCriteria dCriteria = DetachedCriteria.forClass(Customer.class);
	// 设置条件：和 Criteria 是一样的
	dCriteria.add(Restrictions.like("custName", "%剑%"));
	return serviceFindAllCustomer(dCriteria);
}

public List<Customer> serviceFindAllCustomer(DetachedCriteria dCriteria) {
	return daoFindAllCustomer(dCriteria);
}

public List<Customer> daoFindAllCustomer(DetachedCriteria dCriteria) {
	Session s = HibernateUtil.getSession();
	Transaction tx = s.beginTransaction();
	// 把离线对象使用可用 Session 激活
	Criteria c = dCriteria.getExecutableCriteria(s);
	List<Customer> list = c.list();
	tx.commit();
	return list;
}
```

### 7.4. HibernateTemplate 关于离线查询的方法

```java
public class HibernateTemplate implements HibernateOperations, InitializingBean
```

`org.springframework.orm.hibernate5.HibernateTemplate` 是 Spring 提供的操作 Hibernate 的模板对象。在设置离线查询条件后，需要使用 HibernateTemplate 激活离线查询。常用的方法如下：

```java
public List<?> findByCriteria(DetachedCriteria criteria) throws DataAccessException
```

- 激活离线查询条件，返回 `List<?>`。

```java
public List<?> findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults) throws DataAccessException
```

- 根据离线查询条件 criteria、开始记录索引 firstResult、每页最大记录数 maxResults 查询返回对象集合。

## 8. 使用示例

```java
public class TestHibernate_Criteria {
	// 1.查询所有的数据，QBC
	public void findAll() {
		// 获取Session对象
		Session session = HibernateUtil.getSession();
		// 获取Criteria对象，必须指定查询的类.class
		Criteria criteria = session.createCriteria(Customer.class);
		// 调用list方法，查询所有数据，返回对象list集合
		List<Customer> list = criteria.list();

		// 遍历集合
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
		// 关闭资源
		session.close();
	}

	// 通过条件查询,通过客户名模糊查询
	public void findByName() {
		// 获取Session对象
		Session session = HibernateUtil.getSession();
		// 获取Criteria对象，必须指定查询的类.class
		Criteria criteria = session.createCriteria(Customer.class);
		
		// 设置查询条件
		criteria.add(Restrictions.like("custName", "%剑%"));
		
		// 调用list方法，查询所有数据，返回对象list集合
		List<Customer> list = criteria.list();

		// 遍历集合
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
		// 关闭资源
		session.close();
	}
	
	// 查询第3条开始，取4条件数据
	public void findByPage() {
		// 获取Session对象
		Session session = HibernateUtil.getSession();
		// 获取Criteria对象，必须指定查询的类.class
		Criteria criteria = session.createCriteria(Customer.class);
		
		// 设置首页开始索引
		criteria.setFirstResult(2);
		// 设置分页显示的数据条数
		criteria.setMaxResults(4);

		// 调用list方法，查询所有数据，返回对象list集合
		List<Customer> list = criteria.list();

		// 遍历集合
		for (Customer c : list) {
			System.out.println(c.getCustName());
		}
		// 关闭资源
		session.close();
	}
}
```