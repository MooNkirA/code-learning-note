# Spring 其他相关资料

Spring 开发相关资料整理

## 1. eclipse 中的 Spring 配置文件中提示配置信息

> 本小节是针对旧版本 eclipse 对于 spring xml 配置文件无提示的问题。配置跟 hibernate 和 struts2 创建 xml 约束一样的操作

打开【Preferences】 -> 【XML Catalog】 -> 【Add...】

![](images/283360423226846.jpg)

- Location：选择【spring-beans-4.2.xsd】文件所在路径
- Key type：选择Schema location
- Key：填写【http://www.springframework.org/schema/beans/spring-beans.xsd】

![](images/412730423247012.jpg)

> <font color=red>**注：约束文件的路径在【\spring-framework-4.2.4.RELEASE\schema\beans】，如果使用其他类型，就根据类型选择不同的文件夹**</font>


