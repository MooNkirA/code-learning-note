## 1. Postman 接口测试工具简介

Postman 是一款功能强大的、支持 HTTP 协议的接口调试与测试工具，可以完成各种 HTTP 请求的功能测试，使用简单且易用性好。无论是开发人员进行接口调试，还是测试人员做接口测试，Postman 都是的首选工具之一。

- post请求参数设置
    - form-data：将表单的数据转为键值对，并且可以包括文件
    - x-www-form-urlencoded: content-type为application/x-www-from-urlencoded，将表单的数据转为键值对
    - raw：请求text、json、xml、html，比如如果请求json数据则使用此格式
    - binary：content-type为application/octet-stream，可用于上传文件。

### 1.1. 官网

> 官网地址：https://www.postman.com/

### 1.2. 安装

postman 在 2018 年之后就不再支持浏览器版本，想要使用它就必须先下载客户端再安装使用，下面以 Windows 系统为例进行安装。

双击下载的安装包，进入到安装界面，直到用户登录和注册界面。若个人使用，选择跳过即可，这时会进入到 postman 主界面，安装成功。

## 2. 参考资料

- [一文全面解析 Postman 工具](https://mp.weixin.qq.com/s/kyCeJw03UIOgExi_RzQg7g)