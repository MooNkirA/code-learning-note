# MySQL数据库-锁

## 1. MySQL 中的锁

按照 MySQL 官方的说法，InnoDB 中锁可以分为：

- Shared and Exclusive Locks
- Intention Locks
- Record Locks
- Gap Locks
- Next-Key Locks
- Insert Intention Locks
- AUTO-INC Locks
- Predicate Locks for Spatial Indexes

> 官网说明：https://dev.mysql.com/doc/refman/5.7/en/innodb-locking.html

![](images/20210601223942423_29188.png)

## 2. 解决并发事务问题

事务并发执行时可能带来的各种问题，最大的一个难点是：一方面要最大程度地利用数据库的并发访问，另外一方面还要确保每个用户能以一致的方式读取和修改数据，尤其是一个事务进行读取操作，另一个同时进行改动操作的情况下。

各个数据库厂商对 SQL 标准的支持都可能不一样，与 SQL 标准不同的一点就是，MySQL 在 REPEATABLE READ 隔离级别实际上就基本解决了幻读问题。解决脏读、不可重复读、幻读这些问题有两种可选的解决方案：

### 2.1. 方案一：读操作 MVCC，写操作进行加锁

MVCC 就是通过生成一个 ReadView，然后通过 ReadView 找到符合条件的记录版本（历史版本是由 undo 日志构建的），其实就像是在生成 ReadView 的那个时刻做了一个快照，查询语句只能读到在生成 ReadView 之前已提交事务所做的更改，在生成 ReadView 之前未提交的事务或者之后才开启的事务所做的更改是看不到的。而写操作肯定针对的是最新版本的记录，读记录的历史版本和改动记录的最新版本本身并不冲突，也就是采用MVCC 时，读-写操作并不冲突。




## 3. 数据库锁表

### 3.1. 锁表的原因分析

1. 锁表发生在insert、update、delete 中
2. 锁表的原理是 数据库使用独占式封锁机制，当执行上面的语句时，对表进行锁住，直到发生commit 或者 回滚 或者退出数据库用户
3. 锁表的原因：
	- 第一、 A程序执行了对 tableA 的 insert ，并还未 commit时，B程序也对tableA 进行insert 则此时会发生资源正忙的异常 就是锁表
	- 第二、锁表常发生于并发而不是并行（并行时，一个线程操作数据库时，另一个线程是不能操作数据库的，cpu 和i/o 分配原则）
4. 减少锁表的概率：
	1. 减少insert 、update 、delete 语句执行 到 commit 之间的时间。具体点批量执行改为单个执行、优化sql自身的非执行速度
	2. 如果异常对事物进行回滚

### 3.2. 如何判断数据库表已经锁表

查询语法：`select * from v$locked_object;`

可以获得被锁的对象的object_id及产生锁的会话sid。
