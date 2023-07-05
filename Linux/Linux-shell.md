## 1. Shell 脚本

### 1.1. 编写 shell 脚本

用 vi 命令，创建（或打开）test.sh，编写如下内容：

```shell
# vi test.sh
#!/bin/bash
echo "Hello world!"
```

> Notes: 第1行 `#!/bin/bash` 是指定解释器

### 1.2. 执行 shell 脚本的三种方式

方法1：直接用 bash 解释器执行，当前终端会新生成一个子 bash 去执行脚本。命令如下：

```bash
bash xxx.sh
```

方法2：添加可执行权限，这种方式默认根据脚本第一行指定的解释器处理，如果没写以当前默认 Shell 解释器执行。

```bash
[root@MoonKirA ~]# ll test.sh
-rw-r--r--. 1 root root 32 Aug 18 01:07 test.sh
[root@MoonKirA ~]# chmod +x test.sh
[root@MoonKirA ~]# ./test.sh
-bash: ./test.sh: Permission denied
```

方法3：source 命令执行，以当前默认 Shell 解释器执行。命令如下：

```bash
source xxx.sh
```

## 2. Shell 基础语法

### 2.1. 注释

Shell 注释很简单，只要在每行前面加个 `#` 号，即表示 Shell 忽略解释。

### 2.2. Shell 变量

#### 2.2.1. 系统变量

在命令行提示符，直接执行以下命令可查看系统或环境变量。

- `env` 显示用户环境变量
- `set` 显示 Shell 预先定义好的变量以及用户变量

通过 `export` 命令导出成用户变量。

**Shell 脚本常用的系统变量**：

- `$SHELL`：默认 Shell
- `$HOME`：当前用户家目录
- `$IFS`：内部字段分隔符
- `$LANG`：默认语言
- `$PATH`：默认可执行程序路径
- `$PWD`：当前目录
- `$UID`：当前用户 ID
- `$USER`：当前用户
- `$HISTSIZE`：历史命令大小，可通过 `HISTTIMEFORMAT` 变量设置命令执行时间
- `$RANDOM`：随机生成一个 0 至 32767 的整数
- `$HOSTNAME`：主机名

#### 2.2.2. 普通变量与临时环境变量

普通变量定义

```shell
VAR=value
```

临时环境变量定义：

```shell
export VAR=value
```

变量引用：

```shell
$VAR
```

#### 2.2.3. 位置变量

**位置变量**是指函数或脚本后跟的第 n 个参数。格式：`$1` ~ `$n`。

> Notes: <font color=red>**需要注意的是从第 10 个开始要用花括号调用，例如 `${10}`**</font>

`shift` 命令可对位置变量控制，每执行一次 `shift` 命令，位置变量个数就会减一，而变量值则提前一位。`shift n`：可设置向前移动 n 位。

示例脚本 test.sh 如下：

```shell
#!/bin/bash
echo "1: $1"
shift
echo "2: $2"
shift
echo "3: $3"
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh a b c
1: a
2: c
3:
```

#### 2.2.4. 特殊变量

- `$0`：脚本自身名字
- `$?`：返回上一条命令是否执行成功。0 为执行成功，非 0 则为执行失败
- `$#`：位置参数总数
- `$*`：所有的位置参数被看做一个字符串
- `$@`：每个位置参数被看做独立的字符串
- `$$`：当前进程 PID
- `$!`：上一条运行后台进程的 PID

### 2.3. 变量赋值

#### 2.3.1. 赋值运算符

- `=` 用于变量赋值。
- `+=` 用于两个变量拼接

```shell
# 变量赋值
desc="something text!"
app="test!"
# 变量拼接
app+=desc
```

#### 2.3.2. 将命令结果作为变量值

\`\` 与 $() 都是用于执行 Shell 命令，将命令的结果作为变量值。

```shell
# 使用 ``
var_a=`echo xxx`
# 使用 $()
var_b=$(echo xxx)
var_url=$(java -jar test.jar)
```

### 2.4. 变量引用

<font color=red>**Shell 中所有变量引用均使用 `$` 符，后跟变量名**</font>。例如：

```shell
$abc
```

个别特殊字符会影响变量正常引用，则需要使用 `{}` 进行包裹

```shell
${VAR}
```

有时候变量名与其他字符串连在一起，也会误认为是整个变量。如 `$var888`，需要改成 `${var}888`

### 2.5. 双引号和单引号

当在变量赋值时，如果值有空格，Shell 会把空格后面的字符串解释为命令。此时需要使用双引号或者单引号来包裹带有空格的值。

- 单引号（`''`）是让 Shell 忽略特殊字符
- 双引号（`""`）则解释特殊符号原有的意义，比如 `$`、`!`

示例脚本：

```shell
N=3
VAR="1 2 $N"
echo $VAR

VAR='1 2 $N'
echo $VAR
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
1 2 3
1 2 $N
```

## 3. Shell 字符串处理之 ${}

<font color=red>**`${}` 还有一个重要的功能，就是文本处理（单行文本）**</font>

### 3.1. 获取字符串长度

获取字段串长度语法：`${#变量名}`

示例脚本 test.sh

```shell
VAR='hello world!'
echo $VAR

echo ${#VAR}
```

执行测试：

```
hello world!
12
```

### 3.2. 字符串切片

字符串切片语法格式：

```shell
# 截取从 offset 个字符开始到最后的内容
${parameter:offset}
# 截取从 offset 个字符开始，向后 length 个字符的内容
${parameter:offset:length}
```

> Notes: 字符串开始的索引是 0，如果 offset 是负数，则是从末尾开始算

示例脚本

```shell
VAR='hello world!'
# 截取 hello 字符串：
echo ${VAR:0:5}

# 截取 wo 字符：
echo ${VAR:6:2}

# 截取 world!字符串：
echo ${VAR:5}

# 截取最后一个字符：
echo ${VAR:(-1)}

# 截取最后二个字符：
echo ${VAR:(-2)}

# 截取从倒数第 3 个字符后的 2 个字符：
echo ${VAR:(-3):2}
```

执行测试：

```
hello
wo
world!
!
d!
ld
```

### 3.3. 替换字符串

替换字符串语法格式：

```shell
${parameter/pattern/string}
```

参数说明：

- parameter：要进行替换的变量
- pattern：要被替换的字符串，可以是正则表达式。<font color=red>**注：前面一个正斜杠为只匹配第一个字符串，两个正斜杠为匹配所有字符。**</font>
- string：替换的内容

示例脚本

```shell
VAR='hello world world!'
# 将第一个 world 字符串替换  # 结果：WORLD：
echo ${VAR/world/WORLD}
# 将全部 world 字符串替换为 WORLD：
echo ${VAR//world/WORLD}

# 替换正则匹配为空：
VAR=123abc
echo ${VAR//[^0-9]/}
echo ${VAR//[0-9]/}
```

执行测试：

```
hello WORLD world!
hello WORLD WORLD!
123
abc
```

### 3.4. 字符串截取

字符串截取语法格式：

```shell
${parameter#word} # 删除匹配前缀
${parameter##word}
${parameter%word} # 删除匹配后缀
${parameter%%word}
```

参数说明：

- `#` 用于删除匹配前缀，去掉左边，最短匹配模式；`##` 则是最长匹配模式。
- `%` 用于删除匹配后缀，去掉右边，最短匹配模式；`%%` 则是最长匹配模式。

示例脚本

```shell
URL="http://www.baidu.com/baike/user.html"
# 以//为分隔符截取右边字符串：
echo ${URL#*//}

# 以/为分隔符截取右边字符串：
echo ${URL##*/}

# 以//为分隔符截取左边字符串：
echo ${URL%%//*}

# 以/为分隔符截取左边字符串：
echo ${URL%/*}

# 以.为分隔符截取左边：
echo ${URL%.*}

# 以.为分隔符截取右边：
echo ${URL##*.}
```

执行测试：

```
www.baidu.com/baike/user.html
user.html
http:
http://www.baidu.com/baike
http://www.baidu.com/baike/user
html
```

### 3.5. 变量状态赋值

变量状态赋值语法格式：

```shell
# 如果 VAR 变量为空则返回 string
${VAR:-string}
# 如果 VAR 变量不为空则返回 string
${VAR:+string}
# 如果 VAR 变量为空则重新赋值 VAR 变量值为 string
${VAR:=string}
# 如果 VAR 变量为空则将 string 输出到 stderr
${VAR:?string}
```

示例脚本

```shell
# 如果变量为空就返回 hello world!：
VAR=
echo ${VAR:-'hello world!'} 

# 如果变量不为空就返回 hello world!：
VAR="hello"
echo ${VAR:+'hello world!'}

# 如果变量为空就重新赋值：
VAR=
echo ${VAR:=hello}
echo $VAR

# 如果变量为空就将信息输出 stderr：
VAR=
echo ${VAR:?value is null}
```

执行测试：

```
hello world!
hello world!
hello
hello
-bash: VAR: value is null
```

## 4. Shell 表达式与运算符

### 4.1. 条件表达式

条件表达式用于进行条件判断，语法格式如下：

```shell
[ expression ]
[[ expression ]]
test expression
```

示例：

```shell
[ 1 -eq 1 ]
[[ 1 -eq 1 ]]
# 等同于[]
test 1 -eq 1
```

### 4.2. 整数比较符

- `-eq`/`equal`：等于
- `-ne`/`not equal`：不等于
- `-gt`/`greater than`：大于
- `-lt`/`lesser than`：小于
- `-ge`/`greater or equal`：大于或等于
- `-le`/`lesser or equal`：小于或等于

示例：

```shell
[ 1 -eq 1 ]  # 结果：true
[ 1 -ne 1 ]  # 结果：false
[ 2 -gt 1 ]  # 结果：true
[ 2 -lt 1 ]  # 结果：false
[ 2 -ge 1 ]  # 结果：true
[ 2 -le 1 ]  # 结果：false
```

### 4.3. 字符串比较符（待整理）

> TODO: 待整理

### 4.4. 文件测试

相关测试符号如下：

- `-e`：文件或目录是否存在
- `-f`：文件是否存在
- `-d`：目录是否存在
- `-r`：是否有读权限
- `-w`：是否有写权限
- `-x`：是否有执行权限
- `-s`：文件是否存在并且大小大于0

示例：

```shell
[ -e path ] # path 存在为 true
[ -f file_path ] # 文件存在为 true
[ -d dir_path ] # 目录存在为 true
[ -r file_path ] # file_path 有读权限为 true
[ -w file_path ] # file_path 有写权限为 true
[ -x file_path ] # file_path 有执行权限为 true
[ -s file_path ] # file_path 存在并且大小大于 0 为 true
```

### 4.5. 布尔运算符（待整理）

> TODO: 待整理

### 4.6. 逻辑判断符

相关判断符号如下：

- `&&`：逻辑和，在 `[[]]` 和 `(())` 表达式中或判断表达式是否为真时使用
- `||`：逻辑或，在 `[[]]` 和 `(())` 表达式中或判断表达式是否为真时使用

示例：

```shell
[[ 1 -eq 1 && 2 -eq 2 ]]  # 结果：true
(( 1 == 1 && 2 == 2 ))  # 结果：true
[ 1 -eq 1 ] && echo yes   # 结果：如果&&前面表达式为 true 则执行后面的

[[ 1 -eq 1 || 2 -eq 1 ]]  # 结果：true
(( 1 == 1 || 2 == 2 ))  # 结果：true
[ 1 -eq 2 ] || echo yes  # 结果：如果||前面表达式为 false 则执行后面的
```

### 4.7. 整数运算符

- `+`：加法
- `-`：减法
- `*`：乘法
- `/`：除法
- `%`：取余

### 4.8. 整数运算表达式

- `$(())` 例如，`$((1+1))`
- `$[]` 例如，`$[1+1]`

> Notes: 以上两种运算表达式都不支持浮点运算，`$(())`表达式还有一个用途，三目运算。值得注意的是，返回值不支持字符串

示例：

```shell
# 如果条件为真返回 1，否则返回 0
echo $((1<0))  # 结果：0
echo $((1>0))  # 结果：1
# 指定输出数字：
echo $((1>0?1:2))  # 结果：1
echo $((1<0?1:2))  # 结果：2
```


### 4.9. 其他运算工具（let/expr/bc）（待整理）

> TODO: 待整理

### 4.10. Shell 脚本的括号用途总结

- `( )`
    - 用途 1：在运算中，先计算小括号里面的内容
    - 用途 2：数组
    - 用途 3：匹配分组
- `(( ))`
    - 用途 1：表达式，不支持 `-eq` 等整数比较符，不支持 `-a` 和 `-o`；支持 `<=`、`>=`、`<`、`>` 等比较符和`&&`、`||` 等逻辑判断符
    - 用途 2：C 语言风格的 `for(())` 表达式
- `$( )`：执行 Shell 命令，与反撇号等效
- `$(( ))`：
    - 用途 1：简单算数运算
    - 用途 2：支持三目运算符，如，`$(( 表达式?数字:数字 ))`
- `[ ]`：条件表达式，里面不支持逻辑判断符
- `[[ ]]`：条件表达式，里面不支持 `-a` 和 `-o`，也不支持`<=`和`>=`比较符；支持 `-eq`、`<`、`>` 这类比较符。支持`=~`模式匹配，也可以不用双引号也不会影响原意，比`[]`更加通用
- `$[ ]`：简单算数运算
- `{ }`：对逗号（`,`）和点点（`...`）起作用，比如 `touch {1,2}` 创建 1 和 2 文件，`touch{1..3}` 创建 1、2 和 3 文件
- `${ }`：
    - 用途 1：引用变量
    - 用途 2：字符串处理

## 5. Shell 流程控制

### 5.1. 概述

流程控制是改变程序运行顺序的指令。

### 5.2. if 语句

#### 5.2.1. 语法格式

```shell
if list; then list; [ elif list; then list; ] ... [ else list; ] fi
```

#### 5.2.2. 单分支

语法：

```shell
if 条件表达式; then
	命令
fi
```

示例：

```shell
#!/bin/bash
N=10
if [ $N -gt 5 ]; then
	echo yes
fi
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
yes
```

#### 5.2.3. 双分支

语法：

```shell
if 条件表达式; then
	命令
else
	命令
fi
```

示例：

```shell
#!/bin/bash
N=10
if [ $N -lt 5 ]; then
	echo yes
else
	echo no
fi

# 判断 crond 进程是否运行
NAME=crond
NUM=$(ps -ef |grep $NAME |grep -vc grep)
if [ $NUM -eq 1 ]; then
	echo "$NAME running."
else
	echo "$NAME is not running!"
fi

# 检查主机是否存活
if ping -c 1 192.168.1.1 >/dev/null; then
	echo "OK."
else
	echo "NO!"
fi
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
no
crond running.
OK.
```

#### 5.2.4. 多分支

语法：

```shell
if 条件表达式; then
	命令
elif 条件表达式; then
	命令
elif ....
else
	命令
fi
```

> Notes: 如果第一个条件符合，就不再向下匹配

示例：根据 Linux 不同发行版使用不同的命令安装软件

```shell
#!/bin/bash
if [ -e /etc/redhat-release ]; then
	yum install wget -y
elif [ $(cat /etc/issue |cut -d' ' -f1) == "Ubuntu" ]; then
	apt-get install wget -y
else
	Operating system does not support.
	exit
fi
```

### 5.3. for 语句

#### 5.3.1. 语法格式

```shell
for name [ [ in [ word ... ] ] ; ] do list ; done
```

语法格式解析：

```shell
for 变量名 in 取值列表; do
	命令
done
```

#### 5.3.2. 基础使用

示例：

```shell
#!/bin/bash
for i in {1..3}; do
	echo $i
done
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
1
2
3
```

for 语句另一种写法。使用 `$@` 将位置参数作为循环列表，逐个来处理

```shell
#!/bin/bash
for i in "$@"; {
	echo $i
}
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh 1 2 3
1
2
3
```

#### 5.3.3. 指定分隔符

默认 for 循环的取值列表是<font color=red>**以空白符分隔**</font>。如果想指定分隔符，可以重新赋值 `$IFS` 变量。示例如下：

```shell
#!/bin/bash
OLD_IFS=$IFS
IFS=":"
for i in $(head -1 /etc/passwd); do
	echo $i
done

# 恢复原始 IFS 默认值
IFS=$OLD_IFS
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
root
x
0
0
root
/root
/bin/bash
```

#### 5.3.4. c 语言风格的 for 循环

for 循环的 c 语言风格语法，常用于计数、打印数字序列：

```shell
for (( expr1 ; expr2 ; expr3 )) ; do list ; done
```

示例：

```shell
#!/bin/bash
# 也可以 i--
for ((i=1;i<=5;i++)); do
	echo $i
done
```

### 5.4. while 语句

#### 5.4.1. 基础语法

```shell
while list; do list; done
```

语法格式解析：

```shell
while 条件表达式; do
	命令
done
```

当条件表达式为 false 时，终止循环；条件表达式也可以直接用 true，进行死循环。

扩展：与 while 关联的还有一个 until 语句，它与 while 不同之处在于，是当条件表达式为 false 时才循环，实际使用中比较少。

示例：

```shell
#!/bin/bash
N=0
while [ $N -lt 5 ]; do
	let N++
	echo $N
done
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
1
2
3
4
5
```

条件表达式用冒号（`:`），冒号在 Shell 中的含义是『不做任何操作』，但状态是 0，因此为 true。例如：

```shell
#!/bin/bash
while :; do
	echo "yes"
done
```

#### 5.4.2. 使用 while 循环逐行读取文件

方式1：

```shell
#!/bin/bash
cat ./a.txt | while read LINE; do
	echo $LINE
done
```

方式2：

```shell
#!/bin/bash
while read LINE; do
	echo $LINE
done < ./a.txt
```

方式3：

```shell
#!/bin/bash
# 读取文件作为标准输出
exec < ./a.txt
while read LINE; do
	echo $LINE
done
```

### 5.5. break 和 和 continue 语句

- `break` 是终止循环。
- `continue` 是跳出当前循环。

> Notes: `continue` 与 `break` 语句只能循环语句中使用。

### 5.6. case 语句

case 语句一般用于选择性来执行对应部分块命令。语法格式：

```shell
case word in [ [(] pattern [ | pattern ] ... ) list ;; ] ... esac
```

语法格式解析：

```shell
case 模式名 in
	模式 1)
		命令
		;;
	模式 2)
		命令
		;;
	*)
		不符合以上模式执行的命令
esac
```

> Notes: **每个模式必须以右括号结束，命令结尾以双分号结束。**

示例：

```shell
#!/bin/bash
case $1 in
	start)
		echo "start."
		;;
	stop)
		echo "stop."
		;;
	restart)
		echo "restart."
		;;
	*)
		echo "Usage: $0 {start|stop|restart}"
esac
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
Usage: test.sh {start|stop|restart}
[root@MoonKirA ~]# bash test.sh start
start.
[root@MoonKirA ~]# bash test.sh stop
stop.
[root@MoonKirA ~]# bash test.sh restart
restart.
```

### 5.7. select 语句

select 是一个类似于 for 循环的语句。语法格式：

```shell
select name [ in word ] ; do list ; done
```

语法格式解析：

```shell
select 变量 in 选项 1 选项 2; do
	break
done
```

示例：

```shell
#!/bin/bash
select mysql_version in 5.1 5.6; do
	echo $mysql_version
done
```

执行测试：

```bash
[root@MoonKirA ~]# bash test.sh
1) 5.1
2) 5.6
[root@MoonKirA ~]# ? 1
5.1
[root@MoonKirA ~]# ? 2
5.6
```

## 6. Shell 函数与数组

### 6.1. 函数

语法格式：

```shell
func() {
	command
}
```

> Notes: `function` 关键字可写，也可不写
