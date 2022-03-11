# MySQL安装与部署

## 1. MySQL 的版本

### 1.1. 版本分类

MySQL 提供了以下版本：

- MySQL Community Server：社区版本，免费，但是MySQL不提供官方技术支持。
- MySQL Enterprise Edition：商业版，该版本是收费版本，可以试用30天，官方提供技术支持
- MySQL Cluster：集群版，开源免费，可将几个MySQL Server封装成一个Server。
- MySQL Cluster CGE：高级集群版，需付费。

MySQL 的图形化客户端

- MySQL Workbench（GUI TOOL）：一款专为MySQL设计的ER/数据库建模工具。MySQL Workbench又分为两个版本：
    - 社区版（MySQL Workbench OSS）
    - 商用版（MySQL Workbench SE）

### 1.2. 版本号

MySQL的命名机制使用由3个数字和一个后缀组成的版本号。例如，mysql-8.0.26的版本号的含义如下：

- 第1个数字(8)是主版本号，描述了文件格式。所有版本5的发行都有相同的文件格式。
- 第2个数字(0)是发行级别。主版本号和发行级别组合到一起便构成了发行序列号。
- 第3个数字(26)是在此发行系列的版本号，随每个新分发版递增。

> 目前，MySQL的最新版本为MySQL 8.0。


## 2. MySQL目录结构

### 2.1. Linux版本（待补充）



### 2.2. windows版本

- bin：存放可执行文件，比如MySQL.exe
- data：存储的是MySQL默认的数据库
- include：存放的C语言的头文件
- lib：存放的C++的动态链接库
- my.ini：数据库的配置文件

## 3. 启动 MySQL 服务器程序的相关可执行文件

启动 MySOL 服务器程序的可执行文件有很多，大多在 MySQL 安装目录的 bin 目录下。

### 3.1. mysqld（不常用）

- `mysqld` 这个可执行文件就代表着 MySOL 服务器程序，运行这个可执行文件就可以直接启动一个服务器进程。

### 3.2. mysqld_safe

- `mysqld_safe`是一个启动脚本，它会间接的调用`mysqld`，而且还顺便启动了另外一个监控进程，这个监控进程在服务器进程挂了的时候，可以帮助重启它。
- 除外，使用`mysqld_safe`启动服务器程序时，它会将服务器程序的出错信息和其他诊断信息重定向到某个文件中，产生出错日志，方便找出发生错误的原因。

### 3.3. mysql.server

`mysql.server`也是一个启动脚本，它会间接的调用`mysqld_safe`。需要注意的是，这个`mysql.server`文件其实是一个链接文件，它的实际文件位置是`support-files/mysql.server`，所以如果在 bin 目录找不到，则到`support-files`去找，也可以自行用 `ln` 命令在 bin 创建一个链接。

在调用`mysql.server`时在后边指定`start`参数就可以启动服务器程序；如指定`stop`参数则关闭正在运行的服务器程序。

```bash
# 开启MySQL服务
mysql.server start
# 关闭MySQL服务
mysql.server stop
```

### 3.4. mysqld_multi

`mysqld_multi`可以一台计算机上也可以运行多个服务器实例（*即运行了多个MySQL服务器进程*）。此可执行文件可以对每一个服务器进程的启动或停止进行监控。

## 4. Linux 系统安装MySQL

### 4.1. 下载 Linux 安装包

下载地址：https://dev.mysql.com/downloads/mysql/

### 4.2. 安装 MySQL

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

### 4.3. 启动 MySQL 服务

```bash
service mysql start

service mysql stop

service mysql status

service mysql restart
```

### 4.4. 登录 MySQL

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

## 5. CentOS 7.4 安装与配置MySql 5.7.21（项目B安装）

### 5.1. 环境

- **系统环境**：centos-7.4 64位
- **安装方式**：rpm安装
- **软件**：mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar
- **描述**：上述的tar包中已经包含需要安装的rpm，所以只需要将其放置到系统中使用tar命令解包即可。
- **Mysql的下载地址**：http://dev.mysql.com/downloads/mysql/

![](images/20201201085609039_11261.jpg)

![](images/20201201085630998_10120.jpg)

### 5.2. 系统原mariadb版本

```bash
# 查看MySql与mariadb安装情况
# grep -i是不分大小写字符查询，只要含有mysql就显示
rpm -qa | grep -i mysql
rpm -qa | grep mariadb

# 卸载mariadb(会与mysql冲突)
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64
```

![](images/20201201085723353_1572.jpg)

### 5.3. 安装新MySQL

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

### 5.4. 配置MySQL

#### 5.4.1. 启动mysql

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

#### 5.4.2. 修改root密码

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

#### 5.4.3. 设置允许远程访问

```bash
#登录，密码为新修改的密码Root_123
mysql -uroot –p

#设置远程访问（使用root密码）：
mysql> grant all privileges on  *.*  to  'root' @'%'  identified by 'Root_123'; 
mysql> flush privileges;
```

#### 5.4.4. 设置3306端口可以被访问

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

## 6. MySQl 安装（Windows 免安装版）

### 6.1. 解压与创建配置文件

- 将MySQL软件包解压在没有中文和空格的目录下
- 在解压目录创建my.ini文件并添加内容如下：

![](images/20211217093346507_32325.png)

### 6.2. 配置环境变量

- 在【我的电脑】右键 -> 选择【高级系统设置】 -> 选择【高级】 -> 【环境变量】。设置环境变量将【`MYSQL_HOME`】添加到`PATH`环境变量

![](images/20211217093608617_7862.png)

![](images/20211217093705460_28294.png)

### 6.3. 配置系统开启服务

使用管理员权限进入DOS，在cmd中，进入解压目录下的bin目录依次执行以下命令：

1. 对mysql进行初始化。*请注意，这里会生成一个临时密码，后边要使用这个临时密码*

```bash
mysqld --initialize --user=mysql --console
```

2. 安装mysql服务

```bash
mysqld --install
```

3. 启动mysql服务

```bash
net start mysql
```

4. 登录mysql，*这里需要使用之前生成的临时密码。如果窗口关了没有记录临时密码，可以将mysql目录下的data目录删除，然后再进行初始化*

```bash
mysql -uroot –p
```

5. 修改root用户密码

```bash
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';
```

6. 修改root用户权限

```bash
create user 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```

### 6.4. mysql 服务启动时报“某些服务在未由其他服务或程序使用时将自动停止”

在配置文件中有 `secure-file-priv` 的配置，如果设置了此目录，本来解压后是没有此目录的，如果不手动创建，启动时会报错。

![](images/331451322220343.png)

当时排查很久都不知道是什么原因，后来查询资料，发现 `mysqld --console` 命令可以将错误信息输出到控制台上，然后就看到具体的无法启动的报错日志

![](images/362281622238769.png)

然后在配置的位置手动创建相应的目录，问题就解决了。

![](images/146971922246802.png)

## 7. 安装 MySQL 8.0（Windows版本）

### 7.1. 安装包下载

下载地址：https://downloads.mysql.com/archives/installer/

![](images/20211222083853944_30257.png)

### 7.2. 安装步骤（截图，不完整，日后优化）

> 此安装示例使用 mysql-installer-community-8.0.27.1.msi

#### 7.2.1. 自定义安装

![](images/20211222085845035_1714.png)

![](images/20211222090654823_5739.png)

![](images/20211222090847571_381.png)

![](images/20211222090825345_15353.png)

> 注：以上可以不选择相关文档与示例的组件

![](images/20211222091158404_26711.png)

![](images/20211222091242610_16324.png)

![](images/20211222091300866_16123.png)

![](images/20211222091412403_29841.png)

![](images/20211222091515760_12053.png)

![](images/20211222091557114_20197.png)

![](images/20211222092005836_7631.png)

![](images/20211222092031143_4271.png)

![](images/20211222092100137_6434.png)

![](images/20211222092123436_7362.png)

![](images/20211222092207043_14953.png)

![](images/20211222092338341_10645.png)

![](images/20211222092359352_32411.png)

![](images/20211222092414639_26396.png)

![](images/20211222092428439_18771.png)

#### 7.2.2. 默认选择安装

![](images/20211217095541834_10609.png)

![](images/20211217095550711_9997.png)

当所有的状态都变成Complete之后，点击 Next

![](images/20211217095603573_14238.png)

![](images/20211217095626696_29810.png)

![](images/20211217095634392_4688.png)

![](images/20211217095641040_7125.png)

![](images/20211217095646944_30035.png)

> 设置密码，用于登陆数据，建议使用学习的设置简单的密码

### 7.3. 配置环境变量

配置环境变量与windows免安装版一样

## 8. 开启服务与登陆数据库（Windows版本）

### 8.1. 启动MySQL服务方式1

MySQL会以windows服务的方式为我们提供数据存储功能。开启和关闭服务的操作：

1. 右键点击我的电脑 --> 管理 --> 服务与应用程序 --> 服务 --> 找到MySQL服务开启或停止。
2. 或者：开始 --> 搜索 --> services.msc --> 服务 --> 可以找到MySQL服务开启或停止。

<font color="purple">（如果不需要开机时就启动MySQL，右键MySQL --> 属性 --> 启动类型选“手动”）</font>

![mysql服务](images/20190403145741573_20104.jpg)

### 8.2. 启动MySQL服务方式2

在DOS窗口，通过命令完成MySQL服务的启动和停止（必须以管理员身份运行cmd命令窗口）

- MySQL 启动: `net start mysql`
- MySQL 关闭: `net stop mysql`

![mysql服务](images/20190403145927600_25084.jpg)

### 8.3. 登录MySQL数据库

MySQL是一个需要账户名密码登录的数据库，登陆后使用，它提供了一个默认的root账号，使用安装时设置的密码即可登录。

1. 命令行操作登陆与退出数据库
- 方式1：cmd --> `mysql –u用户名 –p密码`

![命令行操作登陆库1](images/20190403150111866_8506.jpg)

- 方式2：cmd --> `mysql --user=用户名 --password=密码 --host=ip地址 --port=端口号`（这种方式一般用来登陆别人的数据库）

![命令行操作登陆库2](images/20190403150120807_7522.jpg)

- 退出MySQL: exit;

2. 通过第三方图形化界面操作
3. 通过Java代码操作

## 9. 卸载MySQL（Windows版本）

1. 先停止MySQL服务
    - 方式1：输入命令行`net stop mysql`。*注：命令中的`mysql`是服务名称，需要输入根据实际的名称*
    - 方式2：【win+r】打开“运行”面板，输入`services.msc`，进行服务窗口中关闭mysql服务
2. 卸载程序。到【控制面板】中的【程序和功能】中卸载，或者使用第三方的卸载工具，如：Geek Uninstaller
3. 删除根文件夹。进入sql安装位置，手动删除mysql的解压（安装）的文件夹
4. 删除C盘隐藏的数据文件夹（可选）：”C:\ProgramData\MySQL“。*注：如果安装或者改了配置文件，此数据文件夹目录可能不一样*
5. 删除注册表信息。【win+r】打开“运行”面板，输入`regedit`命令打开注册表窗口，删除以下文件：

![](images/20211217101232541_26723.png)

6. 删除环境变量的配置。进行【高级系统设置】中的【环境变量】，删除安装后配置`MYSQL_HOME`变量和删除path变量中的mysql路径
7. 删除MySQL服务。使用管理员方式打开cmd命令行窗口，输入命令`sc delete Mysql服务名称`

## 10. MySQL 常用图形管理工具

### 10.1. Navicat

Navicat是一套快速、可靠的数据库管理工具，Navicat 是以直觉化的图形用户界面而建的，可以兼容多种数据库，支持多种操作系统。

官网：http://www.navicat.com.cn/download/navicat-premium

### 10.2. SQLyog

SQLyog 是一个快速而简洁的图形化管理MySQL数据库的工具，它能够在任何地点有效地管理你的数据库，由业界著名的Webyog公司出品。

官网：https://sqlyog.en.softonic.com/

安装：提供的SQLyog软件为免安装版，可直接使用

使用：输入用户名、密码，点击连接按钮，进行访问MySQL数据库进行操作

![SQLyog工具1](images/20190403150510919_18228.jpg)

在Query窗口中，输入SQL代码，选中要执行的SQL代码，按F8键运行，或按执行按钮运行。

![SQLyog工具2](images/20190403150517229_19216.jpg)

### 10.3. MySQL Workbench

MySQL Workbench MySQL 是官方提供的图形化管理工具，分为社区版和商业版，社区版完全免费，而商业版则是按年收费。支持数据库的创建、设计、迁移、备份、导出和导入等功能，并且支持 Windows、Linux 和 mac 等主流操作系统。

### 10.4. DataGrip

DataGrip 是一款由JetBrains公司出品的数据库管理客户端工具，方便连接到数据库服务器，执行sql、创建表、创建索引以及导出数据等

### 10.5. 其他工具

- phpMyAdmin
- MySQLDumper
- MySQL GUI Tools
- MySQL ODBC Connector

