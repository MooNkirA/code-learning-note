# 附录1：项目涉及技术框架API

## 1. RedisTemplate API
### 1.1. RedisTemplate相关方法

- `void delete(K key)`
    - 根据key删除所有的value，String类型、set类型、hash类型、list类型
- `BoundValueOperations<K,V> boundValueOps(K key)`
    - 获取指定key的ValueOperations操作对象，用于String类型的简单K-V操作
- `BoundSetOperations<K,V> boundSetOps(K key)`
    - 获取指定key的SetOperations操作对象，用于set类型数据进行K-V操作
- `BoundListOperations<K,V> boundListOps(K key)`
    - 获取指定key的ListOperations操作对象，用于list类型数据进行K-V操作
- `BoundHashOperations<K,HK,HV> boundHashOps(K key)`
    - 获取指定key的HashOperations操作对象，用于hash类型数据进行K-V操作

### 1.2. BoundValueOperations：简单K-V操作

- `void set(V v);`
    - 设置BoundValueOperations对象的值
- `V get();`
    - 获取BoundValueOperations对象的值

### 1.3. BoundSetOperations：set类型数据操作

- `Long add(V... vs)`
    - 给指定key的BoundSetOperations操作对象的set集合增加值
- `Long remove(Object... objects)`
    - 删除指定key的BoundSetOperations操作对象的set集合中指定的值
- `Set<V> members()`
    - 获取指定key的BoundSetOperations操作对象的set集合

### 1.4. BoundListOperations：list类型的数据操作

- `Long rightPush(V v)`
    - 给指定key的BoundListOperations操作对象的List集合右压栈增加值，即后添加的对象排在后边
- `Long leftPush(V v)`
    - 给指定key的BoundListOperations操作对象的List集合左压栈增加值，即后添加的对象排在前边
- `Long remove(long l, Object o)`
    - 移除指定key的BoundListOperations操作对象的List集合类型中的某个元素
- `List<V> range(long l, long l1)`
    - 获取指定key的BoundListOperations操作对象的List集合中指定范围的所有元素的值
        - 第1个参数：value值的索引
        - 第2个参数：获取的个数；range(0, -1)获取全部
- `V index(long l)`
    - 查询指定key的BoundListOperations操作对象的List集合中某个索引的元素值

### 1.5. BoundHashOperations：hash类型的数据操作

- `void put(HK hk, HV hv)`
    - 设置指定key的BoundHashOperations操作对象的HashMap集合元素值，设置的值是Map<K, V>格式
- `Long delete(Object... objects)`
    - 删除指定key的BoundHashOperations操作对象的HashMap集合指定的key的元素
- `Set<HK> keys()`
    - 获取指定key的BoundHashOperations操作对象的HashMap集合，所有的key
- `List<HV> values()`
    - 获取指定key的BoundHashOperations操作对象的HashMap集合，所有的值
- `HV get(Object o)`
    - 获取获取指定key的BoundHashOperations操作对象的HashMap集合，指定key的值

### 1.6. （！暂无整理）ZSetOperations：zset类型数据操作

---

## 2. Spring Data Solr的API
### 2.1. SolrTemplate 操作对象

- `UpdateResponse saveBean(Object obj)`
    - 添加或者修改对象到索引库，返回UpdateResponse响应对象
- `UpdateResponse deleteById(final String id)`
    - 根据id删除指定索引
- `UpdateResponse delete(SolrDataQuery query)`
    - 根据查询对象删除索引，SolrDataQuery对象指定查询的语句
- `UpdateResponse saveBeans(Collection<?> beans)`
    - 批量添加或者修改对象到索引库
- `<T> T getById(Serializable id, Class<T> clazz)`
    - 根据id查询，返回封装对象。参数clazz：表示将查询的数据封装到的pojo类
- `<T> ScoredPage<T> queryForPage(Query query, Class<T> clazz)`
    - 获取分页对象，
        - 参数query：查询对象（设置查询条件、设置分页条件）
        - 参数clazz：表示将查询的数据封装到pojo类
- `<T> HighlightPage<T> queryForHighlightPage(HighlightQuery query, Class<T> clazz)`
    - 获取高亮的分页对象，
        - 参数query：高亮查询对象（设置查询条件、设置分页条件）
        - 参数clazz：表示将查询的数据封装到pojo类
- `<T> GroupPage<T> queryForGroupPage(Query query, Class<T> clazz)`
    - 获取分组的分页对象
        - 参数query：分组查询对象（设置查询条件、设置分页条件）
        - 参数clazz：表示将查询的数据封装到pojo类
- `void commit()`
    - 提交SolrTemplate操作
- `void rollback()`
    - 回滚SolrTemplate操作

### 2.2. UpdateResponse 响应对象

- `int getStatus()`
    - 获取操作索引库返回的响应状态码，0代表成功，-1代表成功

### 2.3. Query接口 查询对象，继承SolrDataQuery接口
#### 2.3.1. 实现类SimpleQuery

- `<T extends Query> T setOffset(Integer var1)`
    - 设置分页查询的开始索引
- `<T extends Query> T setRows(Integer var1)`
    - 设置分页查询每页显示的大小
- `<T extends SolrDataQuery> T addCriteria(Criteria var1);`
    - 设置查询条件，传入查询对象Criteria
- `<T extends Query> T setGroupOptions(GroupOptions groupOptions)`
    - 设置分组查询条件，传入分组选项对象
- `<T extends Query> T addFilterQuery(FilterQuery filterQuery)`
    - 设置过滤查询条件，传入过滤查询对象
- `<T extends Query> T addSort(Sort sort)`
    - 设置排序条件，传入排序Sort对象

### 2.4. Criteria 查询对象

- `Criteria is(Object o)`
    - 设置查询条件：等于参数o的内容
- `Criteria contains(String s)`
    - 设置查询条件：包含参数s的内容
- `Crotch and(String fieldname)`
    - 增加and条件

### 2.5. ScoredPage 分页对象

`interface ScoredPage<T> extends Page<T>`

- `int getTotalPages();`
    - 获取分页的总页数
- `long getTotalElements();`
    - 获取分页数据的总记录数
- `List<T> getContent();`
    - 获取分页对象封装的所有数据集合

### 2.6. HighlightQuery 高亮显示接口

`interface HighlightQuery extends Query`

高亮显示接口，通过`SolrTemplate`操作对象的`queryForHighlightPage(HighlightQuery query, Class<T> clazz)`方法创建

相关方法：

- `<T extends SolrDataQuery> T setHighlightOptions(HighlightOptions highlightOptions)`
    - 高亮查询对象，设置高亮选项
- `<T extends SolrDataQuery> T addCriteria(Criteria criteria);`
    - 高亮查询对象，设置查询条件对象

### 2.7. HighlightOptions 高亮显示选项类

- `HighlightOptions setSimplePrefix(String prefix)`
    - 设置高亮的html标签前缀
- `HighlightOptions setSimplePostfix(String postfix)`
    - 设置高亮的html标签后缀
- `HighlightOptions addField(String fieldname)`
    - 设置需要高亮的字段

### 2.8. `HighlightPage<T>` 高亮分页接口

`interface HighlightPage<T> extends Page<T>`

高亮分页接口，创建时使用实现类SolrResultPage<T>

相关方法：

- `List<HighlightEntry<T>> getHighlighted();`
    - 获取高亮选项集合
- `List<T> getContent();`
    - 继承父接口`Slice <T>`的方法，获取高亮分页查询的数据集合
- `long getTotalElements();`
    - 获取分页数据的总记录数
- `int getTotalPages()`
    - 获取分页数据的总页数

### 2.9. `HighlightEntry<T>` 高亮实体

`class HighlightEntry<T>`

相关方法：

- `T getEntity()`
    - 获取原来的高亮对象
- `List<HighlightEntry.Highlight> getHighlights()`
    - 获取所有的Field的高亮集合

`HighlightEntry.Highlight`内部类的相关方法

- `List<String> getSnipplets()`
    - 获取高亮内容

### 2.10. GroupOptions 分组选项类

`class GroupOptions`

相关方法：

- `GroupOptions addGroupByField(String fieldName)`
    - 设置分组的字段名称

### 2.11. `GroupPage<T>` 分组分页接口

`interface GroupPage<T> extends Page<T>`

相关方法：

- `GroupResult<T> getGroupResult(String var1)`
    - 根据Field字段名称，获取分组结果集

### 2.12. `GroupResult<T>` 分组结果集接口

`interface GroupResult<T>`分组结果集接口

相关方法：

- `Page<GroupEntry<T>> getGroupEntries();`
    - 通过分组结果集获取分页对象

### 2.13. `Page<T>` 分页接口

`interface Page<T> extends Slice<T>`	分页接口

相关方法：

- `List<T> getContent()`
    - 获取分页选项集合

### 2.14. `GroupEntry<T>` 分页选项接口

`interface GroupEntry<T>`	分页选项接口

相关方法：

- `String getGroupValue();`
    - 获取指定的字段分组值

### 2.15. FilterQuery 过滤查询接口

实现类：`class SimpleFilterQuery extends AbstractQuery implements FilterQuery`

- `SimpleFilterQuery(Criteria criteria)`
    - 构造方法，传递查询对象。创建过滤查询对象

### 2.16. Sort 排序类

`class Sort implements Iterable<Sort.Order>, Serializable`

使用枚举Sort.Direction.ASC或Sort.Direction.DESC创建对象








