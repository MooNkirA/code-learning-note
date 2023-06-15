## 1. Linux 软件安装前置工作

### 1.1. 上传软件安装包

如果通过安装包方式安装所需的软件，则要通过 SSH 远程连接工具，上传安装包文件到 Linux 系统。

![](images/355172716249251.jpg)

### 1.2. rz / sz 命令

rz、sz 命令用于上传、下载数据。但此两个命令非系统自带，需要安装 lrzsz。可以通过 yum 工具在线安装

```bash
yum -y install lrzsz 
```

> ~~使用 `rz` 命令上传的默认放到根目录（`/`）下（如果使用其他用户上传的话，再验证是默认存放到哪个的目录中？？？）~~

## 2. JDK 的安装

### 2.1. 解压版 JDK

- 步骤1：查看当前 Linux 系统是否已经安装 java

```bash
rpm -qa | grep java
```

![](images/422023416252970.jpg)

> Notes: rpm 与软件相关命令，相当于 window 下的软件助手，用来管理软件

- 步骤2：卸载两个 openJDK

```bash
rpm -e --nodeps 要卸载的软件
```

![](images/529123616255382.jpg)

- 步骤3：上传 jdk 到 linux 系统。通过 rz 命令或者其他 ftp 工具
- 步骤4：解压 jdk 到 `/usr/local` 下

```bash
tar –zxvf jdk-7u71-linux-i586.tar.gz –C /usr/local
```
![](images/529693816254270.jpg)

- 步骤5：配置 jdk 环境变量，打开 `/etc/profile` 配置文件

```bash
cd etc/
vim profile
# 或者
vim /etc/profile
```

将下面配置拷贝进去

```properties
#set java environment
JAVA_HOME=/usr/local/jdk1.7.0_71
CLASSPATH=.:$JAVA_HOME/lib.tools.jar
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME CLASSPATH PATH
```

![](images/316274016246189.jpg)

- 步骤6：重新加载 `/etc/profile` 配置文件，并测试

```bash
source /etc/profile
java -version
```

![](images/135044116234646.jpg)

### 2.2. 安装版 JDK

使用 rz 命令上传 JDK 安装包后，到安装包所在目录，执行以下命令进行安装。

```bash
rpm -ivh jdk-8u162-linux-x64.rpm
```

JDK 默认安装在 `/usr/java` 目录。执行 `java -version` 命令查看信息是否正常，验证安装是否成功。

### 2.3. 配置 JDK 环境变量（安装版与解压版一致）

安装完 jdk-xxxx-linux-x64.rpm 后不用配置环境变量也可以正常执行 `javac`、`java–version` 等操作，则可不进行 JDK 环境变量的配置。如有需要则可如下配置：

- 修改系统环境变量文件

```
vi /etc/profile
```

- 向文件里面追加以下内容（根据显示安装的位置与版本修改）

```properties
JAVA_HOME=/usr/java/jdk1.8.0_181-amd64
JRE_HOME=/usr/java/jdk1.8.0_181-amd64/jre
PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
CLASSPATH=:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export JAVA_HOME JRE_HOME PATH CLASSPATH
```

- 使修改内容立即生效

```bash
source /etc/profile
```

- 查看系统环境状态（PATH 值）

```bash
echo $PATH
```

## 3. MySQL 的安装

### 3.1. 安装前置检查

- 查看 CentOS 自带的 MySql 与 mariadb 安装情况。

```bash
rpm -qa | grep -i mysql
rpm -qa | grep mariadb
```

![](images/14011017242667.jpg)

> Tips: `grep -i` 是不分大小写字符查询，只要含有 mysql 就显示

- 卸载自带的 mysql 与 mariadb（如有）

```bash
rpm -e --nodeps mysql-libs-5.1.71.1.e16.i686
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64
```

> Notes: 通常要卸载 mariadb，因为会与 MySQL 冲突

### 3.2. 安装服务器与客户端

- 上传 MySQL 压缩包到 linux
- 解压 MySQL 到 `/usr/local/` 下的 mysql 目录（mysql目录需要手动创建）内

```bash
cd /usr/local
mkdir mysql
tar -xvf MySQL-5.6.22-1.el6.i686.rpm-bundle.tar -C /usr/local/mysql
```

![](images/349321617257965.jpg)

![](images/446691617231765.jpg)

- 在 `/usr/local/mysql` 目录下安装 MySQL

```shell
# 先安装服务器端
rpm -ivh MySQL-server-5.6.22-1.el6.i686.rpm
# 后安装客户端
rpm -ivh MySQL-client-5.6.22-1.el6.i686.rpm
```

![](images/343101817247556.jpg)

![](images/415291817252339.jpg)

![](images/537551817244581.jpg)

- 安装完成后，查看 mysql 进程

```bash
ps –ef | grep mysql
```

![](images/363681917234045.jpg)

### 3.3. 启动 MySQL

```bash
service mysql start
```

![](images/141292017261722.jpg)

### 3.4. 配置系统服务与开机自动启动

将 MySQL 加到系统服务中并设置开机启动

```shell
# 加入到系统服务
chkconfig --add mysql
# 设置自动启动
chkconfig mysql on
```

![](images/133982217260806.jpg)

### 3.5. 登录 MySQL

MySQL 安装好后会生成一个临时随机密码，存储位置在 `/root/.mysql_secret`。可以使用软件查看密码后，

![](images/488262517248622.jpg)

使用文本打开可以查看

```
# The random password set for the root user at Thu Dec 28 12:07:32 2017 (local time): bOoVxlBLrrjIsqIt
```

登陆 MySQL 后再修改密码即可。

```bash
mysql –u root –p
```








