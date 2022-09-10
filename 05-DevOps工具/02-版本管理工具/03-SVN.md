# SVN 文件版本控制软件

## 1. SVN 概述

版本控制(Revision control)是维护工程蓝图的标准做法，能追踪工程蓝图从诞生到定案的过程。

SVN 是基于客户/服务器模式，针对软件研发企业的软件生产过程而言，SVN 用于管理整个开发过程中的源码，进行版本控制。

## 2. SVN 服务的安装

### 2.1. Apache Subversion 官方服务端

> - 官方网站：http://subversion.apache.org/
> - 下载地址：http://subversion.apache.org/download.cgi

官方提供的服务端安装包，安装后需要通过命令行操作，适用于专业配置管理员使用

### 2.2. VisualSVN server 图形化服务端

图形化操作界面的 svn 服务端，适用于普通软件开发人员使用

> 下载地址：https://www.visualsvn.com/downloads/

【注意】如果出现端口被占用，可以采用以下两种解决办法：
		第一种：停止服务中 VMWare WorkStation Server 服务
		第二种：改变 svn 的端口号。
		SVN软件安装端口使用建议8888
		安装路径不能有中文和空格
安装后如果选择了不自动启动，需要使用的时候就在【服务】中点击开启


