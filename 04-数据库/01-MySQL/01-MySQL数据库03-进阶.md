# MySQL数据库-进阶

## 1. MySQL 的视图

### 1.1. 简述

视图（view）是一个虚拟表，非真实存在，其本质是根据SQL语句获取动态的数据集，并为其命名，用户使用时只需使用视图名称即可获取结果集，并可以将其当作表来使用。

数据库中只存放了视图的定义，而并没有存放视图中的数据。这些数据存放在原来的表中。

使用视图查询数据时，数据库系统会从原来的表中取出对应的数据。因此，视图中的数据是依赖于原来的表中的数据的。一旦表中的数据发生改变，显示在视图中的数据也会发生改变。

### 1.2. 视图的作用

- 简化代码，可以把重复使用的查询封装成视图重复使用，同时可以使复杂的查询易于理解和使用。
- 安全原因，如果一张表中有很多数据，很多信息不希望让所有人看到，此时可以使用视图视，如：社会保险基金表，可以用视图只显示姓名，地址，而不显示社会保险号和工资数等，可以对不同的用户，设定不同的视图。

### 1.3. 视图的创建语法

```sql
create [or replace] [algorithm = {undefined | merge | temptable}]
view view_name [(column_list)]
as select_statement
[with [cascaded | local] check option]
```

参数说明：

- `algorithm`：可选项，表示视图选择的算法。
- `view_name`：表示要创建的视图名称。
- `column_list`：可选项，指定视图中各个属性的名词，默认情况下与 `SELECT` 语句中的查询的属性相同。
- `select_statement`：表示一个完整的查询语句，将查询记录导入视图中。
- `[with [cascaded | local] check option]`：可选项，表示更新视图时要保证在该视图的权限范围之内。

示例：

```sql
-- 创建视图测试相关的表与数据
CREATE OR REPLACE VIEW view1_emp
AS
	SELECT
		ename,
		job
	FROM
		emp;

-- 查看表和视图，通过以下命令，可以区分开真实的表与视图
SHOW FULL TABLES;
```

### 1.4. 修改视图

修改视图是指修改数据库中已存在的表的定义。当基本表的某些字段发生改变时，可以通过修改视图来保持视图和基本表之间一致。MySQL中通过 `CREATE OR REPLACE VIEW` 语句和 `ALTER VIEW` 语句来修改视图。语法：

```sql
alter view 视图名 as select语句;
```

示例：

```sql
ALTER VIEW view1_emp
AS
	SELECT
		a.deptno,a.dname,a.loc,b.ename,b.sal
	FROM
		dept a,
		emp b
	WHERE
		a.deptno = b.deptno;
```

### 1.5. 更新视图

某些视图是可更新的。也就是说，可以在 `UPDATE`、`DELETE` 或 `INSERT` 等语句中使用它们，以更新基表的内容。对于可更新的视图，在视图中的行和基表中的行之间必须具有一对一的关系。

如果视图包含下述结构中的任何一种，那么它就是不可更新的：

- 聚合函数（SUM(), MIN(), MAX(), COUNT()等）
- DISTINCT
- GROUP BY
- HAVING
- UNION 或 UNION ALL
- 位于选择列表中的子查询
- JOIN
- FROM 子句中的不可更新视图
- WHERE 子句中的子查询，引用 FROM 子句中的表。
- 仅引用文字值（在该情况下，没有要更新的基本表）

> <font color=red>**视图中虽然可以更新数据，但是有很多的限制。一般情况下，最好将视图作为查询数据的虚拟表，而不要通过视图更新数据。因为，使用视图更新数据时，如果没有全面考虑在视图中更新数据的限制，就可能会造成数据更新失败。**</font>

示例：

```sql
--  ---------更新视图-------
create or replace view view1_emp
as
select ename,job from emp;

update view1_emp set ename = '周瑜' where ename = '鲁肃';  -- 可以修改
insert into view1_emp values('孙权','文员');  -- 不可以插入

-- ----------视图包含聚合函数不可更新--------------
create or replace view view2_emp
as
select count(*) cnt from emp;

insert into view2_emp values(100);
update view2_emp set cnt = 100;

-- ----------视图包含distinct不可更新---------
create or replace view view3_emp
as
select distinct job from emp;

insert into view3_emp values('财务');

-- ----------视图包含goup by 、having不可更新------------------
create or replace view view4_emp
as
select deptno ,count(*) cnt from emp group by deptno having  cnt > 2;

insert into view4_emp values(30,100);

-- ----------------视图包含union或者union all不可更新----------------
create or replace view view5_emp
as
select empno,ename from emp where empno <= 1005
union
select empno,ename from emp where empno > 1005;

insert into view5_emp values(1015,'韦小宝');

-- -------------------视图包含子查询不可更新--------------------
create or replace view view6_emp
as
select empno,ename,sal from emp where sal = (select max(sal) from emp);

insert into view6_emp values(1015,'韦小宝',30000);

-- ----------------------视图包含join不可更新-----------------
create or replace view view7_emp
as
select dname,ename,sal from emp a join  dept b  on a.deptno = b.deptno;

insert into view7_emp(dname,ename,sal) values('行政部','韦小宝',30000);

-- --------------------视图包含常量文字值不可更新-------------------
create or replace view view8_emp
as
select '行政部' dname,'杨过'  ename;

insert into view8_emp values('行政部','韦小宝');
```

### 1.6. 重命名视图

语法：

```sql
rename table 视图名 to 新视图名;
```

示例：

```sql
rename table view1_emp to my_view1;
```

### 1.7. 删除视图

语法：

```sql
drop view 视图名[, 视图名, ...];
```

> <font color=red>**注：删除视图时，只能删除视图的定义，不会删除数据。**</font>

示例：

```sql
drop view if exists view_student;
```

## 2. 存储过程

### 2.1. 简述

MySQL 5.0 版本开始支持存储过程。存储过程就是一组SQL语句集，功能强大，可以实现一些比较复杂的逻辑功能，类似于JAVA语言中的方法；

**存储过程就是数据库 SQL 语言层面的代码封装与重用。**

**特性**：

- 有输入输出参数，可以声明变量，有 if/else, case, while 等控制语句，通过编写存储过程，可以实现复杂的逻辑功能；
- 函数的普遍特性：模块化，封装，代码复用；
- 速度快，只有首次执行需经过编译和优化步骤，后续被调用可以直接执行，省去以上步骤；

### 2.2. 存储过程创建语法定义

创建语法：

```sql
delimiter 自定义结束符号
create procedure 储存名称([ in, out, inout ] 参数名 数据类型...)
begin
  sql语句
end 自定义的结束符合
delimiter ;
```

示例：

```sql
delimiter $$
create procedure proc01()
begin
  select empno,ename from emp;
end  $$
delimiter ;

-- 调用存储过程
call proc01();
```

<font color=red>**特别注意：在语法中，变量声明、游标声明、handler声明是必须按照先后顺序书写的，否则创建存储过程出错。**</font>

### 2.3. 局部变量定义

#### 2.3.1. 变量定义语法

用户可以自定义局部变量，在 begin/end 块内有效。声明变量语法：

```sql
declare var_name type [default var_value];
```

参数说明：

- `var_name`：变量名称
- `var_value`：指定默认值（非必须）

示例：

```sql
declare nickname varchar(32);

-- 示例
delimiter $$
create procedure proc02()
begin
    declare var_name01 varchar(20) default 'aaa';  -- 定义局部变量
    set var_name01 = 'MooN';
    select var_name01;
end $$
delimiter ;
-- 调用存储过程
call proc02();
```

#### 2.3.2. 变量赋值语法

MySQL 中还可以使用 `SELECT..INTO` 语句为变量赋值。其基本语法如下：

```sql
select
    col_name [...] into var_name[,...]
from
    table_name
where condition;
```

参数说明：

- `col_name` 参数表示查询的字段名称
- `var_name` 参数是变量的名称
- `table_name` 参数指表的名称
- `condition` 参数指查询条件

> <font color=purple>注意：当将查询结果赋值给变量时，该查询语句的返回结果只能是单行单列。</font>

示例：

```sql
delimiter $$
create procedure proc03()
begin
  declare my_ename varchar(20) ;
  select ename into my_ename from emp where empno=1001;
  select my_ename;
end $$
delimiter ;
-- 调用存储过程
call proc03();
```

### 2.4. 用户（会话）变量定义

用户自定义变量，当前会话（连接）有效。*类比java的成员变量 *。不需要提前声明，使用即声明。语法：

```sql
@变量名称
```

示例：

```sql
delimiter $$
create procedure proc04()
begin
    set @var_name01 = 'ZS'; -- 设置用户自定义会话变量
end $$
delimiter ;
call proc04() ;
select @var_name01; -- 可以看到结果
```

### 2.5. 存储过程的参数

- `in` 输入参数：该值传到存储过程的过程里面去，在存储过程中修改该参数的值不能被返回
- `out` 输出参数：该值可在存储过程内部被改变，并向外输出
- `inout` 输入输出参数：既能输入一个值又能传出来一个值

#### 2.5.1. in 类型参数

`in` 类型表示传入存储过程的参数，可以传入数值或者变量，即使传入变量，并不会更改变量的值，可以内部更改，仅仅作用在函数范围内。

示例：

```sql
-- 封装有参数的存储过程，传入员工编号，查找员工信息
delimiter $$
create procedure dec_param01(in param_empno varchar(20))
begin
        select * from emp where empno = param_empno;
end $$

delimiter ;
call dec_param01('1001');

-- 封装有参数的存储过程，可以通过传入部门名和薪资，查询指定部门，并且薪资大于指定值的员工信息
delimiter $$
create procedure dec_param0x(in dname varchar(50), in sal decimal(7,2))
begin
        select * from dept a, emp b where b.sal > sal and a.dname = dname;
end $$

delimiter ;
call dec_param0x('学工部',20000);
```

#### 2.5.2. out 类型参数

`out` 类型的参数表示从存储过程内部传值给调用者

示例：

```sql
-- ---------传出参数：out---------------------------------
use mysql7_procedure;
-- 封装有参数的存储过程，传入员工编号，返回员工名字
delimiter $$
create procedure proc08(in empno int , out out_ename varchar(50))
begin
  select ename into out_ename from emp where emp.empno = empno;
end $$
delimiter ;

call proc08(1001, @o_ename);
select @o_ename;

-- 封装有参数的存储过程，传入员工编号，返回员工名字和薪资
delimiter $$
create procedure proc09(in empno int, out out_ename varchar(50), out out_sal decimal(7,2))
begin
  select ename,sal into out_ename,out_sal from emp where emp.empno = empno;
end $$
delimiter ;

call proc09(1001, @o_dname,@o_sal);
select @o_dname;
select @o_sal;
```

#### 2.5.3. inout 类型参数

`inout` 类型表示从外部传入的参数经过修改后可以返回的变量，既可以使用传入变量的值也可以修改变量的值（即使函数执行完）

示例：

```sql
-- 传入员工名，拼接部门号，传入薪资，求出年薪
delimiter $$
create procedure proc10(inout inout_ename varchar(50), inout inout_sal int)
begin
  select  concat(deptno,"_",inout_ename) into inout_ename from emp where ename = inout_ename;
  set inout_sal = inout_sal * 12;
end $$
delimiter ;
set @inout_ename = '关羽';
set @inout_sal = 3000;

call proc10(@inout_ename, @inout_sal) ;
select @inout_ename ;
select @inout_sal ;
```

### 2.6. 流程控制 - if 条件判断

`IF` 语句包含多个条件判断，根据结果为 `TRUE`、`FALSE` 执行语句，与编程语言中的`if`、`else if`、`else` 语法类似，其语法格式如下：

```sql
if search_condition_1 then statement_list_1
    [elseif search_condition_2 then statement_list_2]
    ...
    [else statement_list_n]
end if
```

示例：

```sql
-- 判断存储流程输入
delimiter $$
create procedure proc_12_if(in score int)
begin
  if score < 60
      then
          select '不及格';
    elseif  score < 80
      then
          select '及格' ;
    elseif score >= 80 and score < 90
       then
           select '良好';
  elseif score >= 90 and score <= 100
       then
           select '优秀';
     else
       select '成绩错误';
  end if;
end $$
delimiter ;
-- 调用
call proc_12_if(80);

-- 查询数据
delimiter $$
create procedure proc12_if(in in_ename varchar(50))
begin
    declare result varchar(20);
    declare var_sal decimal(7,2);
        select sal into var_sal from emp where ename = in_ename;
    if var_sal < 10000
        then set result = '试用薪资';
    elseif var_sal < 30000
        then set result = '转正薪资';
    else
        set result = '元老薪资';
    end if;
    select result;
end$$
delimiter ;
-- 调用
call proc12_if('庞统');
```

### 2.7. 流程控制 - case 条件判断

`CASE` 是另一个条件判断的语句，类似于编程语言中的switch语法。语法结构如下：

```sql
-- 语法1（类比java的switch）：
case case_value
    when when_value then statement_list
    [when when_value then statement_list]
    ...
    [else statement_list]
end case

-- 语法2：
case
    when search_condition then statement_list
    [when search_condition then statement_list]
    ...
    [else statement_list]
end case
```

说明：

- 语法1：当 `case_value = when_value` 时，执行相应的 `statement_list` 逻辑
- 语法2：当 `search_condition` 为 `true` 时，执行相应的 `statement_list` 逻辑

示例：

```sql
-- 语法1
delimiter $$
create procedure proc14_case(in pay_type int)
begin
  case pay_type
      when 1 then select '微信支付';
      when 2 then select '支付宝支付';
      when 3 then select '银行卡支付';
      else select '其他方式支付';
	end case ;
end $$
delimiter ;
-- 调用
call proc14_case(2);
call proc14_case(4);

-- 语法2
delimiter $$
create procedure proc_15_case(in score int)
begin
  case
    when score < 60 then select '不及格';
    when score < 80 then select '及格';
    when score >= 80 and score < 90 then select '良好';
    when score >= 90 and score <= 100 then select '优秀';
    else select '成绩错误';
  end case;
end $$
delimiter ;
-- 调用
call proc_15_case(88);
```

### 2.8. 流程控制 - 循环

#### 2.8.1. 简述

- 循环是一段在程序中只出现一次，但可能会连续运行多次的代码。
- 循环中的代码会运行特定的次数，或者是运行到特定条件成立时结束循环

![](images/20211222113120889_26454.png)

**存储过程的循环分类**：

- while
- repeat
- loop

**循环控制（结束/跳过）**：

- `leave` 类似于java语言的 `break`，跳出，结束当前所在的循环
- `iterate` 类似于java语言的 `continue`，继续，结束本次循环，继续下一次

#### 2.8.2. while 循环

语法格式：

```sql
[标签名:]
WHILE
    循环条件
DO
    循环体;
END WHILE [标签名];
```

注：以上标签名可以省略。标签名一般在`leave`结束与`iterate`跳过循环时使用

示例：

```sql
-- -------存储过程-while
delimiter $$
create procedure proc16_while1(in insertcount int)
begin
    declare i int default 1;
    label:while i<=insertcount do
        insert into user(uid,username,`password`) values(i,concat('user-',i),'123456');
        set i=i+1;
    end while label;
end $$
delimiter ;

call proc16_while(10);

-- -------存储过程-while + leave
delimiter $$
create procedure proc16_while2(in insertcount int)
begin
    declare i int default 1;
    label:while i<=insertcount do
        insert into user(uid,username,`password`) values(i,concat('user-',i),'123456');
        if i=5 then leave label;
        end if;
        set i=i+1;
    end while label;
end $$
delimiter ;

call proc16_while2(10);

-- -------存储过程-while+iterate
delimiter $$
create procedure proc16_while3(in insertcount int)
begin
    declare i int default 1;
    label:while i<=insertcount do
        set i=i+1;
        if i=5 then iterate label;
        end if;
        insert into user(uid,username,`password`) values(i,concat('user-',i),'123456');
    end while label;
end $$
delimiter ;
call proc16_while3(10);
```

#### 2.8.3. repeat 循环

语法格式：

```sql
[标签名:]
REPEAT
    循环体;
    UNTIL 条件表达式
END REPEAT [标签名];
```

示例：

```sql
-- -------存储过程-循环控制-repeat
delimiter $$
create procedure proc18_repeat(in insertCount int)
begin
     declare i int default 1;
     label:repeat
         insert into user(uid, username, password) values(i,concat('user-',i),'123456');
         set i = i + 1;
         until  i  > insertCount
     end repeat label;
     select '循环结束';
end $$
delimiter ;

call proc18_repeat(100);
```

#### 2.8.4. loop 循环

语法格式：

```sql
[标签:]
LOOP
    循环体;
	IF
        条件表达式
	THEN
		LEAVE [标签];
	END IF;
END LOOP;
```

示例：

```sql
-- -------存储过程-循环控制-loop
delimiter $$
create procedure proc19_loop(in insertCount int)
begin
     declare i int default 1;
     label:loop
         insert into user(uid, username, password) values(i,concat('user-',i),'123456');
         set i = i + 1;
         if i > 5
          then
           leave label;
         end if;
     end loop label;
     select '循环结束';
end $$
delimiter ;

call proc19_loop(10);
```

### 2.9. 游标

游标(cursor)是用来存储查询结果集的数据类型，在存储过程和函数中可以使用游标对结果集进行循环的处理，相当于指针，指向一行一行数据。游标的使用包括游标的声明、`OPEN`、`FETCH` 和 `CLOSE`

```sql
-- 声明语法
declare cursor_name cursor for select_statement
-- 打开语法
open cursor_name
-- 取值语法
fetch cursor_name into var_name [, var_name] ...
-- 关闭语法
close cursor_name
```

参数说明：

- `cursor_name`：游标的名称
- `select_statement`：查询数据表返回的结果集
- `var_name`：游标循环结果集每一行数据时，赋值的变量

示例：

```sql
use mysql7_procedure;
delimiter $$
create procedure proc20_cursor(in in_dname varchar(50))
begin
 -- 定义局部变量
 declare var_empno varchar(50);
 declare var_ename varchar(50);
 declare var_sal  decimal(7,2);

 -- 声明游标
 declare my_cursor cursor for
  select empno , ename, sal
    from  dept a ,emp b
    where a.deptno = b.deptno and a.dname = in_dname;

    -- 打开游标
  open my_cursor;
  -- 通过游标获取每一行数据
  label:loop
        fetch my_cursor into var_empno, var_ename, var_sal;
        select var_empno, var_ename, var_sal;
    end loop label;

    -- 关闭游标
    close my_cursor;
end

-- 调用存储过程
call proc20_cursor('销售部');
```

示例2：游标的使用取每行记录(多字段)

```sql
delimiter $
create PROCEDURE phoneDeal()

BEGIN
	DECLARE  id varchar(64);   -- id
	DECLARE  phone1  varchar(16); -- phone
	DECLARE  password1  varchar(32); -- 密码
	DECLARE  name1 varchar(64);   -- id
	-- 遍历数据结束标志
	DECLARE done INT DEFAULT FALSE;
	-- 游标
	DECLARE cur_account CURSOR FOR select phone,password,name from account_temp;
	-- 将结束标志绑定到游标
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	-- 打开游标
	OPEN  cur_account;
	-- 遍历
	read_loop: LOOP
		-- 取值 取多个字段
		FETCH  NEXT from cur_account INTO phone1,password1,name1;
			IF done THEN
				LEAVE read_loop;
			END IF;
		-- 你自己想做的操作
		insert into account(id,phone,password,name) value(UUID(),phone1,password1,CONCAT(name1,'的家长'));
	END LOOP;
	CLOSE cur_account;
END $
```

***注意：delimiter关键字后面必须有空格，否则在某些环境或某些情况下使用shell脚本调用执行会出现问题***

### 2.10. 异常处理 - HANDLER 句柄

#### 2.10.1. 语法定义

MySql存储过程也提供了对异常处理的功能：通过定义 `HANDLER` 来完成异常声明的实现。

> 官方文档：https://dev.mysql.com/doc/refman/5.7/en/declare-handler.html

语法格式：

```sql
DECLARE handler_action HANDLER
    FOR condition_value [, condition_value] ...
    statement

-- handler_action 取值
handler_action: {
    CONTINUE -- 继续执行剩余的代码
  | EXIT     -- 直接终止程序
  | UNDO     -- 不支持
}
-- condition_value 取值
condition_value: {
    mysql_error_code
  | SQLSTATE [VALUE] sqlstate_value
  | condition_name
  | SQLWARNING
  | NOT FOUND
  | SQLEXCEPTION
}
```

#### 2.10.2. 存储过程中使用 handler

示例：

```sql
delimiter $$
create procedure proc20_cursor(in in_dname varchar(50))
begin
		-- 定义局部变量
    declare var_empno int;
    declare var_ename varchar(50);
    declare var_sal decimal(7,2);

    declare flag int default 1; -- ---------------------

    -- 声明游标
    declare my_cursor cursor for
        select empno,ename,sal
        from dept a, emp b
        where a.deptno = b.deptno and a.dname = in_dname;

    -- 定义句柄，当数据未发现时将标记位设置为0
    declare continue handler for NOT FOUND set flag = 0;
    -- 打开游标
    open my_cursor;
    -- 通过游标获取值
    label:loop
        fetch my_cursor into var_empno, var_ename,var_sal;
        -- 判断标志位
        if flag = 1 then
            select var_empno, var_ename,var_sal;
        else
            leave label;
        end if;
    end loop label;

    -- 关闭游标
    close my_cursor;
end $$;
delimiter ;

call proc21_cursor_handler('销售部');
```

### 2.11. 获取当前登陆用户

`user()` 函数用于是取得当前登陆的用户。一般在存储过程中使用，获取值。

```sql
select user() into 变量名;
```

### 2.12. 查询最后插入的数据的id

`last_insert_id()` 函数可以获得刚插入的数据的id值，这个是session 级的，并发没有问题。

```sql
insert xxxxx....;
select last_insert_id() into 变量名;
-- 上面语句可以将最近插入的数据id赋值给变量，后面可以进行对应的逻辑处理
```

### 2.13. 存储过程综合示例

```sql
/*
	创建下个月的每天对应的表user_2021_11_01、user_2021_11_02、...

	需求描述：
		我们需要用某个表记录很多数据，比如记录某某用户的搜索、购买行为(注意，此处是假设用数据库保存)，
		当每天记录较多时，如果把所有数据都记录到一张表中太庞大，需要分表，我们的要求是，每天一张表，存当天的统计数据，
		就要求提前生产这些表——每月月底创建下一个月每天的表！
*/
-- 思路：循环构建表名 user_2021_11_01 到 user_2020_11_30；并执行create语句。
drop procedure if exists proc22_demo;
delimiter $$
create procedure proc22_demo()
begin
    declare next_year int;
    declare next_month int;
    declare next_month_day int;

    declare next_month_str char(2);
    declare next_month_day_str char(2);

    -- 处理每天的表名
    declare table_name_str char(10);

    declare t_index int default 1;
    -- declare create_table_sql varchar(200);
    -- 获取下个月的年份
    set next_year = year(date_add(now(),INTERVAL 1 month));
    -- 获取下个月是几月
    set next_month = month(date_add(now(),INTERVAL 1 month));
    -- 下个月最后一天是几号
    set next_month_day = dayofmonth(LAST_DAY(date_add(now(),INTERVAL 1 month)));

    if next_month < 10
        then set next_month_str = concat('0',next_month);
    else
        set next_month_str = concat('',next_month);
    end if;


    while t_index <= next_month_day do
        if (t_index < 10)
            then set next_month_day_str = concat('0',t_index);
        else
            set next_month_day_str = concat('',t_index);
        end if;
				-- 2021_11_01
        set table_name_str = concat(next_year,'_',next_month_str,'_',next_month_day_str);
        -- 拼接create sql语句
        set @create_table_sql = concat(
                    'create table user_',
                    table_name_str,
                    '(`uid` INT ,`ename` varchar(50) ,`information` varchar(50)) COLLATE=\'utf8_general_ci\' ENGINE=InnoDB');
        -- FROM后面不能使用局部变量！
        prepare create_table_stmt FROM @create_table_sql;
        execute create_table_stmt;
        DEALLOCATE prepare create_table_stmt;

        set t_index = t_index + 1;

    end while;
end $$
delimiter ;

call proc22_demo();
```

## 3. 存储函数

### 3.1. 概述

MySQL存储函数（自定义函数），函数一般用于计算和返回一个值，可以将经常需要使用的计算或功能写成一个函数。存储函数和存储过程一样，都是在数据库中定义一些 SQL 语句的集合。

### 3.2. 存储函数与存储过程的区别

1. 存储函数有且只有一个返回值，而存储过程可以有多个返回值，也可以没有返回值。
2. 存储函数只能有输入参数，而且不能带`in`, 而存储过程可以有多个`in`,`out`,`inout`参数。
3. 存储过程中的语句功能更强大，存储过程可以实现很复杂的业务逻辑，而函数有很多限制，如不能在函数中使用`insert`,`update`,`delete`,`create`等语句；
4. 存储函数只完成查询的工作，可接受输入参数并返回一个结果，也就是函数实现的功能针对性比较强。
5. 存储过程可以调用存储函数。但函数不能调用存储过程。
6. 存储过程一般是作为一个独立的部分来执行(`call`调用)。而函数可以作为查询语句的一个部分来调用.

### 3.3. 创建语法

在MySQL中，创建存储函数使用 `create function` 关键字，其基本形式如下：

```sql
create function func_name ([param_name type[,...]])
returns type
	[characteristic ...]
begin
    routine_body
end;
```

参数说明：

- `func_name`：存储函数的名称。
- `param_name type`：可选项，指定存储函数的参数。`type`参数用于指定存储函数的参数类型，该类型可以是MySQL数据库中所有支持的类型。
- `RETURNS type`：指定返回值的类型。
- `characteristic`：可选项，指定存储函数的特性。
- `routine_body`：SQL代码内容。

示例：

```sql
-- 创建存储函数-没有输输入参数
drop function if exists myfunc1_emp;

delimiter $$
create function myfunc1_emp() returns int
begin
  declare cnt int default 0;
    select count(*) into  cnt from emp;
  return cnt;
end $$
delimiter ;
-- 调用存储函数
select myfunc1_emp();

-- 创建存储过程-有输入参数
drop function if exists myfunc2_emp;

delimiter $$
create function myfunc2_emp(in_empno int) returns varchar(50)
begin
    declare out_name varchar(50);
    select ename into out_name from emp where  empno = in_empno;
    return out_name;
end $$
delimiter ;

select myfunc2_emp(1008);
```

> 注意：如果创建时出现错误，执行以下命令
>
> ```sql
> set global log_bin_trust_function_creators=TRUE; -- 信任子程序的创建者
> ```

## 4. 触发器

### 4.1. 概述

触发器，就是一种特殊的存储过程。触发器和存储过程一样是一个能够完成特定功能、存储在数据库服务器上的SQL片段，但是触发器无需调用，当对数据库表中的数据执行DML操作时自动触发这个SQL片段的执行，无需手动调用。

**在MySQL中，只有执行`insert`,`delete`,`update`操作时才能触发触发器的执行**。

触发器的这种特性可以协助应用在数据库端确保数据的完整性，日志记录，数据校验等操作 。

使用别名 `OLD` 和 `NEW` 来引用触发器中发生变化的记录内容，这与其他的数据库是相似的。现在触发器还只支持行级触发，不支持语句级触发

### 4.2. 触发器的特性

1. 什么条件会触发：I、D、U
2. 什么时候触发：在增删改前或者后
3. 触发频率：针对每一行执行
4. 触发器定义在表上，附着在表上

![](images/20211222165042872_30271.png)

### 4.3. 创建触发器的语法

- 创建只有一个执行语句的触发器

```sql
CREATE TRIGGER 触发器名 BEFORE | AFTER 触发事件
ON 表名 FOR EACH ROW
执行语句;
```

- 创建有多个执行语句的触发器

```sql
CREATE TRIGGER 触发器名 BEFORE | AFTER 触发事件
ON 表名 FOR EACH ROW
BEGIN
	执行语句列表
END;
```

参数说明：

- 触发事件：取值：`insert` | `update` | `delete`


示例：

```sql
-- 如果触发器存在，则先删除
drop trigger if  exists trigger_test1;
-- 创建触发器trigger_test1
create trigger trigger_test1
after insert on user -- 触发时机：当添加user表数据时触发
for each row
    insert into user_logs values(NULL,now(), '有新用户注册');

-- 添加数据，触发器自动执行并添加日志代码
insert into user values(1,'张三','123456');

-- 如果触发器trigger_test2存在，则先删除
drop trigger if exists trigger_test2;
-- 创建触发器trigger_test2
delimiter $$
create trigger trigger_test2
after update on user  -- 触发时机：当修改user表数据时触发
for each row -- 每一行
begin
    insert into user_logs values(NULL,now(), '用户修改发生了修改');
end $$

delimiter ;

-- 添加数据，触发器自动执行并添加日志代码
update user set password = '888888' where uid = 1;
```

### 4.4. 操作关键字 (NEW|OLD)

MySQL 中定义了 `NEW` 和 `OLD`，用来表示触发器的所在表中，触发了触发器的那一行数据，来引用触发器中发生变化的记录内容。

|    触发器类型     |             触发器类型`NEW` 和 `OLD` 的使用             |
| :--------------: | ----------------------------------------------------- |
| `INSERT` 型触发器 | `NEW` 表示将要或者已经新增的数据                         |
| `UPDATE` 型触发器 | `OLD` 表示修改之前的数据，`NEW` 表示将要或已经修改后的数据 |
| `DELETE` 型触发器 | `OLD` 表示将要或者已经删除的数据                         |

使用方法：

- `NEW.columnName`：获取新增数据某一列的值，`columnName`为相应数据表某一列名

示例：

```sql
create trigger trigger_test3 after insert
on user for each row
insert into user_logs values(NULL,now(),concat('有新用户添加，信息为:',NEW.uid,NEW.username,NEW.password));

-- 测试
insert into user values(4,'赵六','123456');
```

### 4.5. 查看触发器

语法：

```sql
show triggers;
```

### 4.6. 删除触发器

语法：

```sql
drop trigger [if exists] trigger_name;
```

示例：

```sql
drop trigger if exists trigger_test1;
```

### 4.7. 触发器注意事项

1. MYSQL 中触发器中不能对本表进行 `insert`, `update`, `delete` 操作，以免递归循环触发
2. 尽量少使用触发器，假设触发器触发每次执行1s，insert table 500条数据，那么就需要触发500次触发器，光是触发器执行的时间就花费了500s，而insert 500条数据一共是1s，那么这个insert的效率就非常低了。
3. 触发器是针对每一行的；对增删改非常频繁的表上切记不要使用触发器，因为它会非常消耗资源。
