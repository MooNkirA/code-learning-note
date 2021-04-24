# MySQL安装与部署

## 1. MySQL目录结构

### 1.1. Linux版本（待补充）



### 1.2. windows版本

- bin：存放可执行文件，比如MySQL.exe
- data：存储的是MySQL默认的数据库
- include：存放的C语言的头文件
- lib：存放的C++的动态链接库
- my.ini：数据库的配置文件

## 2. 启动 MySQL 服务器程序的相关可执行文件

启动 MySOL 服务器程序的可执行文件有很多，大多在 MySQL 安装目录的 bin 目录下。

### 2.1. mysqld（不常用）

- `mysqld` 这个可执行文件就代表着 MySOL 服务器程序，运行这个可执行文件就可以直接启动一个服务器进程。

### 2.2. mysqld_safe

- `mysqld_safe`是一个启动脚本，它会间接的调用`mysqld`，而且还顺便启动了另外一个监控进程，这个监控进程在服务器进程挂了的时候，可以帮助重启它。
- 除外，使用`mysqld_safe`启动服务器程序时，它会将服务器程序的出错信息和其他诊断信息重定向到某个文件中，产生出错日志，方便找出发生错误的原因。

### 2.3. mysql.server

`mysql.server`也是一个启动脚本，它会间接的调用`mysqld_safe`。需要注意的是，这个`mysql.server`文件其实是一个链接文件，它的实际文件位置是`support-files/mysql.server`，所以如果在 bin 目录找不到，则到`support-files`去找，也可以自行用 `ln` 命令在 bin 创建一个链接。

在调用`mysql.server`时在后边指定`start`参数就可以启动服务器程序；如指定`stop`参数则关闭正在运行的服务器程序。

```bash
# 开启MySQL服务
mysql.server start
# 关闭MySQL服务
mysql.server stop
```

### 2.4. mysqld_multi

`mysqld_multi`可以一台计算机上也可以运行多个服务器实例（*即运行了多个MySQL服务器进程*）。此可执行文件可以对每一个服务器进程的启动或停止进行监控。

## 3. Linux 系统安装MySQL

### 3.1. 下载 Linux 安装包

下载地址：https://dev.mysql.com/downloads/mysql/5.7.html#downloads

### 3.2. 安装 MySQL

1. 卸载 centos 中预安装的 mysql

```bash
rpm -qa | grep -i mysql
rpm -e mysql-libs-5.1.71-1.el6.x86_64 --nodeps
```

2. 上传 mysql 的安装包

```
alt + p --> put  E:/test/MySQL-5.6.22-1.el6.i686.rpm-bundle.tar
```

3. 解压 mysql 的安装包

```bash
mkdir mysql
tar -xvf MySQL-5.6.22-1.el6.i686.rpm-bundle.tar -C /root/mysql
```

4. 安装依赖包

```bash
yum -y install libaio.so.1 libgcc_s.so.1 libstdc++.so.6 libncurses.so.5 --setopt=protected_multilib=false
yum  update libstdc++-4.4.7-4.el6.x86_64
```

5. 安装 mysql-client

```bash
rpm -ivh MySQL-client-5.6.22-1.el6.i686.rpm
```

6. 安装 mysql-server

```bash
rpm -ivh MySQL-server-5.6.22-1.el6.i686.rpm
```

### 3.3. 启动 MySQL 服务

```bash
service mysql start

service mysql stop

service mysql status

service mysql restart
```

### 3.4. 登录 MySQL

mysql 安装完成之后, 会自动生成一个随机的密码, 并且保存在一个密码文件中：`/root/.mysql_secret`

```bash
mysql -u root -p
```

登录之后，修改密码：

```bash
set password = password('123456');
```

进入mysql，授权远程访问：

```bash
grant all privileges on *.* to 'root' @'%' identified by '123456';
flush privileges;
```

如果此时使用SSH软件还是无法远程连接mysql数据库，可能就是linux系统的防火墙导致的，所以需要设置关闭防火墙

```bash
# 查询看防火墙状态
service iptables status

# 关闭防火墙（全部、暴力）
service iptables stop
```

## 4. CentOS 7.4 安装与配置MySql 5.7.21（项目B安装）

### 4.1. 环境

- **系统环境**：centos-7.4 64位
- **安装方式**：rpm安装
- **软件**：mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar
- **描述**：上述的tar包中已经包含需要安装的rpm，所以只需要将其放置到系统中使用tar命令解包即可。
- **Mysql的下载地址**：http://dev.mysql.com/downloads/mysql/

![](images/20201201085609039_11261.jpg)

![](images/20201201085630998_10120.jpg)

### 4.2. 系统原mariadb版本

```bash
# 查看MySql与mariadb安装情况
# grep -i是不分大小写字符查询，只要含有mysql就显示
rpm -qa | grep -i mysql
rpm -qa | grep mariadb

# 卸载mariadb(会与mysql冲突)
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64
```

![](images/20201201085723353_1572.jpg)

### 4.3. 安装新MySQL

使用winSCP将下载的“`mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar`”传到虚拟机系统的/root目录下：

![](images/20201201085759562_8205.jpg)

在终端上进入`/root`目录；解包`.tar`包

```bash
# 对” mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar”解包，不是压缩文件不需要解压缩
tar -xvf mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar
```

![](images/20201201085906279_29898.jpg)

执行如下安装命令：

```bash
# 1、安装 mysql-community-common
rpm -ivh mysql-community-common-5.7.21-1.el7.x86_64.rpm

# 2、安装 mysql-community-libs
rpm -ivh mysql-community-libs-5.7.21-1.el7.x86_64.rpm

# 3、安装 mysql-community-client
rpm -ivh mysql-community-client-5.7.21-1.el7.x86_64.rpm

# 4、安装 mysql-community-server
yum -y install perl
rpm -ivh mysql-community-server-5.7.21-1.el7.x86_64.rpm

# 5、安装 mysql-community-devel
rpm -ivh mysql-community-devel-5.7.21-1.el7.x86_64.rpm
```

安装完成。MySql默认安装文件位置：

```
/var/lib/mysql/    # 数据库目录
/usr/share/mysql   # 配置文件目录
/usr/bin           # 相关命令目录
/etc/my.cnf        # 核心配置文件
```

### 4.4. 配置MySQL

#### 4.4.1. 启动mysql

```bash
#启动mysql
service mysqld start
#重启mysql
service mysqld restart
#停止mysql
service mysqld stop
#查看mysql状态
service mysqld status

# 设置开机启动Mysql
systemctl enable mysqld

# 设置开机不启动Mysql
systemctl disable mysqld
```

#### 4.4.2. 修改root密码

MySQL安装成功后，会生成一个临时密码，第一次登录需要输入这个密码，所以查看该临时密码，然后修改密码。

```bash
# 查看临时密码(/var/log/mysqld.log)
grep password /var/log/mysqld.log

# 使用root登录
mysql –uroot –p

# 然后输入/var/log/mysqld.log文件中的临时密码。登录后；修改密码为Root_123
set password = password('Root_123');
```

<font color=red>**注意：密码必须包含大小写字母、数字、特殊符号**</font>

![](images/20201201090151187_1048.jpg)

#### 4.4.3. 设置允许远程访问

```bash
#登录，密码为新修改的密码Root_123
mysql -uroot –p

#设置远程访问（使用root密码）：
mysql> grant all privileges on  *.*  to  'root' @'%'  identified by 'Root_123'; 
mysql> flush privileges;
```

#### 4.4.4. 设置3306端口可以被访问

```bash
# 退出mysql，防火墙中打开3306端口
firewall-cmd --zone=public --add-port=3306/tcp --permanent
```

参数说明：

- `–zone`：作用域
- `-–add-port=3306/tcp`：添加端口，格式为：端口/通讯协议
- `-–permanent`：永久生效，没有此参数重启后失效

```bash
# 重启防火墙
firewall-cmd --reload
# 查看已经开放的端口
firewall-cmd --list-ports

# 停止防火墙
systemctl stop firewalld.service
# 启动防火墙
systemctl start firewalld.service
# 禁止防火墙开机启动
systemctl disable firewalld.service
```

## 5. MySQL的安装与运行（Windows版本）

### 5.1. 启动MySQL服务方式1

MySQL会以windows服务的方式为我们提供数据存储功能。开启和关闭服务的操作：

1. 右键点击我的电脑 --> 管理 --> 服务与应用程序 --> 服务 --> 找到MySQL服务开启或停止。
2. 或者：开始 --> 搜索 --> services.msc --> 服务 --> 可以找到MySQL服务开启或停止。

<font color="purple">（如果不需要开机时就启动MySQL，右键MySQL --> 属性 --> 启动类型选“手动”）</font>

![mysql服务](images/20190403145741573_20104.jpg)

### 5.2. 启动MySQL服务方式2

在DOS窗口，通过命令完成MySQL服务的启动和停止（必须以管理员身份运行cmd命令窗口）

- MySQL 启动: `net start mysql`
- MySQL 关闭: `net stop mysql`

![mysql服务](images/20190403145927600_25084.jpg)

### 5.3. 登录MySQL数据库

MySQL是一个需要账户名密码登录的数据库，登陆后使用，它提供了一个默认的root账号，使用安装时设置的密码即可登录。

1. 命令行操作登陆与退出数据库
- 方式1：cmd --> `mysql –u用户名 –p密码`

![命令行操作登陆库1](images/20190403150111866_8506.jpg)

- 方式2：cmd --> `mysql --user=用户名 --password=密码 --host=ip地址 --port=端口号`（这种方式一般用来登陆别人的数据库）

![命令行操作登陆库2](images/20190403150120807_7522.jpg)

- 退出MySQL: exit;

2. 通过第三方图形化界面操作
3. 通过Java代码操作

## 6. MySQL 图形化开发工具

### 6.1. SQLyog

安装：提供的SQLyog软件为免安装版，可直接使用

使用：输入用户名、密码，点击连接按钮，进行访问MySQL数据库进行操作

![SQLyog工具1](images/20190403150510919_18228.jpg)

在Query窗口中，输入SQL代码，选中要执行的SQL代码，按F8键运行，或按执行按钮运行。

![SQLyog工具2](images/20190403150517229_19216.jpg)

