# MySQL安装与部署

## 1. Linux 系统安装MySQL

### 1.1. 下载Linux 安装包

下载地址：https://dev.mysql.com/downloads/mysql/5.7.html#downloads

### 1.2. 安装MySQL

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

### 1.3. 启动 MySQL 服务

```bash
service mysql start

service mysql stop

service mysql status

service mysql restart
```

### 1.4. 登录MySQL

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

## 2. CentOS 7.4 安装与配置MySql 5.7.21（项目B安装）

### 2.1. 环境

- **系统环境**：centos-7.4 64位
- **安装方式**：rpm安装
- **软件**：mysql-5.7.21-1.el7.x86_64.rpm-bundle.tar
- **描述**：上述的tar包中已经包含需要安装的rpm，所以只需要将其放置到系统中使用tar命令解包即可。
- **Mysql的下载地址**：http://dev.mysql.com/downloads/mysql/

![](images/20201201085609039_11261.jpg)

![](images/20201201085630998_10120.jpg)

### 2.2. 系统原mariadb版本

```bash
# 查看MySql与mariadb安装情况
# grep -i是不分大小写字符查询，只要含有mysql就显示
rpm -qa | grep -i mysql
rpm -qa | grep mariadb

# 卸载mariadb(会与mysql冲突)
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64
```

![](images/20201201085723353_1572.jpg)

### 2.3. 安装新MySQL

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

### 2.4. 配置MySQL

#### 2.4.1. 启动mysql

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

#### 2.4.2. 修改root密码

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

#### 2.4.3. 设置允许远程访问

```bash
#登录，密码为新修改的密码Root_123
mysql -uroot –p

#设置远程访问（使用root密码）：
mysql> grant all privileges on  *.*  to  'root' @'%'  identified by 'Root_123'; 
mysql> flush privileges;
```

#### 2.4.4. 设置3306端口可以被访问

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
