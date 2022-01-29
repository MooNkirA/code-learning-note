# Day16 Spring Security Oauth2 认证方案 & JWT 研究

## 1. 用户认证需求分析

### 1.1. 用户认证与授权

截至目前，项目已经完成了在线学习功能，用户通过在线学习页面点播视频进行学习。如何去记录学生的学习过程呢？要想掌握学生的学习情况就需要知道用户的身份信息，记录哪个用户在什么时间学习什么课程；如果用户要购买课程也需要知道用户的身份信息。所以，去管理学生的学习过程最基本的要实现用户的身份认证

- 什么是用户身份认证？
    - 用户身份认证即用户去访问系统资源时系统要求验证用户的身份信息，身份合法方可继续访问。常见的用户身份认证表现形式有：用户名密码登录，指纹打卡等方式
- 什么是用户授权？
    - 用户认证通过后去访问系统的资源，系统会判断用户是否拥有访问资源的权限，只允许访问有权限的系统资源，没有权限的资源将无法访问，这个过程叫用户授权

### 1.2. 单点登录需求

本项目包括多个子项目，如：学习系统，教学管理中心、系统管理中心等，为了提高用户体验性需要实现用户只认证一次便可以在多个拥有访问权限的系统中访问，这个功能叫做单点登录

单点登录（Single Sign On），简称为 SSO，是目前比较流行的企业业务整合的解决方案之一。SSO的定义是在多个应用系统中，用户只需要登录一次就可以访问所有相互信任的应用系统

下图是SSO的示意图，用户登录学成网一次即可访问多个系统

![SSO的示意图](images/20190819175503415_11619.jpg)

### 1.3. 第三方认证需求

作为互联网项目难免需要访问外部系统的资源，同样本系统也要访问第三方系统的资源接口，例如有一个这样的场景：一个微信用户没有在学成在线注册，本系统可以通过请求微信系统来验证该用户的身份，验证通过后该用户便可在本系统学习，它的基本流程如下

![](images/20190819181033355_30471.jpg)

从上图可以看出，微信不属于本系统，本系统并没有存储微信用户的账号、密码等信息，本系统如果要获取该用户的基本信息则需要首先通过微信的认证系统（微信认证）进行认证，微信认证通过后本系统便可获取该微信用户的基本信息，从而在本系统将该微信用户的头像、昵称等信息显示出来，该用户便不用在本系统注册却可以直接学习

什么是第三方认证（跨平台认证）？

当需要访问第三方系统的资源时需要首先通过第三方系统的认证（例如：微信认证），由第三方系统对用户认证通过，并授权资源的访问权限

![](images/20190819181525484_8312.jpg)

## 2. 用户认证技术方案

### 2.1. 单点登录技术方案

分布式系统要实现单点登录，通常将认证系统独立抽取出来，并且将用户身份信息存储在单独的存储介质，比如：MySQL、Redis，考虑性能要求，通常存储在Redis中

![单点登录架构](images/20190819183932460_11003.jpg)

- 单点登录的特点
    1. 认证系统为独立的系统
    2. 各子系统通过Http或其它协议与认证系统通信，完成用户认证
    3. 用户身份信息存储在Redis集群
- Java中有很多用户认证的框架都可以实现单点登录
    1. Apache Shiro
    2. CAS
    3. Spring security CAS

### 2.2. Oauth2 认证
#### 2.2.1. Oauth2 认证流程

第三方认证技术方案最主要是解决认证协议的通用标准问题，因为要实现跨系统认证，各系统之间要遵循一定的接口协议

OAUTH协议为用户资源的授权提供了一个安全的、开放而又简易的标准。同时，任何第三方都可以使用OAUTH认证服务，任何服务提供商都可以实现自身的OAUTH认证服务，因而OAUTH是开放的。业界提供了OAUTH的多种实现如PHP、JavaScript，Java，Ruby等各种语言开发包，大大节约了程序员的时间，因而OAUTH是简易的。互联网很多服务如Open API，很多大公司如Google，Yahoo，Microsoft等都提供了OAUTH认证服务，这些都足以说明OAUTH标准逐渐成为开放资源授权的标准

Oauth协议目前发展到2.0版本，1.0版本过于复杂，2.0版本已得到广泛应用。

> 参考：https://baike.baidu.com/item/oAuth/7153134?fr=aladdin
>
> Oauth 协议：https://tools.ietf.org/html/rfc6749

#### 2.2.2. Oauth2 微信认证示例

![微信认证的过程1](images/20190819185430717_26910.png)

1. 客户端请求第三方授权

用户进入黑马程序的登录页面，点击微信的图标以微信账号登录系统，用户是自己在微信里信息的资源拥有者

![](images/20190819185517610_32546.png)

点击“微信”出现一个二维码，此时用户扫描二维码，开始给黑马程序员授权

![](images/20190819185529382_20350.png)

2. 资源拥有者同意给客户端授权

资源拥有者扫描二维码表示资源拥有者同意给客户端授权，微信会对资源拥有者的身份进行验证， 验证通过后，微信会询问用户是否给授权黑马程序员访问自己的微信数据，用户点击“确认登录”表示同意授权，微信认证服务器会颁发一个授权码，并重定向到黑马程序员的网站

![](images/20190820082150103_4205.png)

3. 客户端获取到授权码，请求认证服务器申请令牌。此过程用户看不到，客户端应用程序请求认证服务器，请求携带授权码。
4. 认证服务器向客户端响应令牌
    - 认证服务器验证了客户端请求的授权码，如果合法则给客户端颁发令牌，令牌是客户端访问资源的通行证。
    - 此交互过程用户看不到，当客户端拿到令牌后，用户在黑马程序员看到已经登录成功。
5. 客户端请求资源服务器的资源
    - 客户端携带令牌访问资源服务器的资源。
    - 黑马程序员网站携带令牌请求访问微信服务器获取用户的基本信息。
6. 资源服务器返回受保护资源
    - 资源服务器校验令牌的合法性，如果合法则向用户响应资源信息内容。

> 注意：资源服务器和认证服务器可以是一个服务也可以分开的服务，如果是分开的服务资源服务器通常要请求认证服务器来校验令牌的合法性

#### 2.2.3. Oauth2.0 认证流程

> 引自Oauth2.0协议rfc6749 https://tools.ietf.org/html/rfc6749

![Oauth2.0认证流程](images/20190820083110286_26081.png)

Oauth2包括以下角色：

1. 客户端
    - 本身不存储资源，需要通过资源拥有者的授权去请求资源服务器的资源，比如：学成在线Android客户端、学成在线Web客户端（浏览器端）、微信客户端等。
2. 资源拥有者
    - 通常为用户，也可以是应用程序，即该资源的拥有者。
3. 授权服务器（也称认证服务器）
    - 用来对资源拥有的身份进行认证、对访问资源进行授权。客户端要想访问资源需要通过认证服务器由资源拥有者授权后方可访问。
4. 资源服务器
    - 存储资源的服务器，比如，学成网用户管理服务器存储了学成网的用户信息，学成网学习服务器存储了学生的学习信息，微信的资源服务存储了微信的用户信息等。客户端最终访问资源服务器获取资源信息

#### 2.2.4. Oauth2 在本项目的应用

- Oauth2是一个标准的开放的授权协议，应用程序可以根据自己的要求去使用Oauth2，本项目使用Oauth2实现如下目标：
    1. 学成在线访问第三方系统的资源
    2. 外部系统访问学成在线的资源
    3. 学成在线前端（客户端） 访问学成在线微服务的资源。
    4. 学成在线微服务之间访问资源，例如：微服务A访问微服务B的资源，B访问A的资源

### 2.3. Spring security Oauth2 认证解决方案

本项目采用 Spring security + Oauth2 完成用户认证及用户授权，Spring security 是一个强大的和高度可定制的身份验证和访问控制框架，Spring security 框架集成了Oauth2协议，下图是项目认证架构图

![认证架构图](images/20190820085942097_6956.jpg)

1. 用户请求认证服务完成认证。
2. 认证服务下发用户身份令牌，拥有身份令牌表示身份合法。
3. 用户携带令牌请求资源服务，请求资源服务必先经过网关。
4. 网关校验用户身份令牌的合法，不合法表示用户没有登录，如果合法则放行继续访问。
5. 资源服务获取令牌，根据令牌完成授权。
6. 资源服务完成授权则响应资源信息

## 3. JWT 研究

### 3.1. 传统令牌校验

传统校验令牌的方法，如下图

![传统令牌校验流程](images/20190819113525442_8980.png)

传统授权方法的问题是用户每次请求资源服务，资源服务都需要携带令牌访问认证服务去校验令牌的合法性，并根据令牌获取用户的相关信息，性能低下

### 3.2. JWT 令牌授权流程

相对传统的授权校验流程，JWT的解决思路是：用户认证通过会得到一个JWT令牌，JWT令牌中已经包括了用户相关的信息，客户端只需要携带JWT访问资源服务，资源服务根据事先约定的算法自行完成令牌校验，无需每次都请求认证服务完成授权。JWT令牌授权过程如下图：

![JWT令牌授权流程图](images/20190819122401904_5504.png)

### 3.3. 什么是 JWT

- JSON Web Token（JWT）是一个开放的行业标准（RFC 7519），它定义了一种简介的、自包含的协议格式，用于在通信双方传递json对象，传递的信息经过数字签名可以被验证和信任。JWT可以使用HMAC算法或使用RSA的公钥/私钥对来签名，防止被篡改
- 官网：https://jwt.io/
- 标准：https://tools.ietf.org/html/rfc7519

#### 3.3.1. JWT 的优缺点

- JWT令牌的优点：
    1. jwt基于json，非常方便解析。
    2. 可以在令牌中自定义丰富的内容，易扩展。
    3. 通过非对称加密算法及数字签名技术，JWT防止篡改，安全性高。
    4. 资源服务使用JWT可不依赖认证服务即可完成授权。
- 缺点：
    1. JWT令牌较长，占存储空间比较大。

#### 3.3.2. 令牌结构

JWT令牌由三部分组成，每部分中间使用点（`.`）分隔，比如：`xxxxx.yyyyy.zzzzz`

- 第一部分：Header（头部）
    - 包括令牌的类型（即JWT）及使用的哈希算法（如HMAC SHA256或RSA）

```json
{
    "alg": "HS256",
    "typ": "JWT"
}
```

> 例：上面是Header部分的内容，将上边的内容使用Base64Url编码，得到一个字符串就是JWT令牌的第一部分

- 第二部分：Payload（负载）
    - 内容也是一个json对象，它是存放有效信息的地方，它可以存放jwt提供的现成字段，比如：iss（签发者）,exp（过期时间戳）, sub（面向的用户）等，也可自定义字段
    - **此部分不建议存放敏感信息**，因为此部分可以解码还原原始内容
    - 最后将第二部分负载使用Base64Url编码，得到一个字符串就是JWT令牌的第二部分

```json
{
    "sub": "1234567890",
    "name": "456",
    "admin": true
}
```

- 第三部分：Signature（签名）
    - 此部分用于防止jwt内容被篡改
    - 这个部分使用base64url将前两部分进行编码，编码后使用点（`.`）连接组成字符串，最后使用header中声明签名算法进行签名

```java
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

> - base64UrlEncode(header) ：jwt令牌的第一部分。
> - base64UrlEncode(payload)：jwt令牌的第二部分。
> - secret：签名所使用的密钥

### 3.4. JWT 使用入门

Spring Security 提供对JWT的支持，本项目使用Spring Security 提供的JwtHelper来创建JWT令牌，校验JWT令牌等操作

#### 3.4.1. 生成私钥和公钥

JWT令牌生成采用非对称加密算法

##### 3.4.1.1. 生成密钥证书

- Keytool是JDK自带提供的证书管理工具，用于密钥和证书管理的工具，进入java安装目录的bin文件夹，输入以下命令查询keytool的帮助

```bash
PS D:\development\Java\jdk1.8.0_144\bin> keytool -help
密钥和证书管理工具

命令:
 -certreq            生成证书请求
 -changealias        更改条目的别名
 -delete             删除条目
 -exportcert         导出证书
 -genkeypair         生成密钥对
 -genseckey          生成密钥
 -gencert            根据证书请求生成证书
 -importcert         导入证书或证书链
 -importpass         导入口令
 -importkeystore     从其他密钥库导入一个或所有条目
 -keypasswd          更改条目的密钥口令
 -list               列出密钥库中的条目
 -printcert          打印证书内容
 -printcertreq       打印证书请求的内容
 -printcrl           打印 CRL 文件的内容
 -storepasswd        更改密钥库的存储口令

使用 "keytool -command_name -help" 获取 command_name 的用法
```

- 输入以下命令生成密钥证书，采用RSA算法每个证书包含公钥和私钥

```bash
keytool -genkeypair -alias xckey -keyalg RSA -keypass xuecheng -keystore xc.keystore -storepass xuechengkeystore
```

![生成密钥证书](images/20190819130417200_3589.png)

![生成密钥证书](images/20190819133923510_12430.png)

- 参数说明：
    - `-alias`：密钥的别名
    - `-keyalg`：使用的hash算法
    - `-keypass`：密钥的访问密码
    - `-keystore`：密钥库文件名，xc.keystore保存了生成的证书
    - `-storepass`：密钥库的访问密码

##### 3.4.1.2. 查询证书信息

```bash
keytool -list -keystore xc.keystore
```

![查询证书信息](images/20190819134144197_18557.png)

##### 3.4.1.3. 删除别名

```bash
keytool -delete -alias xckey -keystore xc.keystore
```

#### 3.4.2. 导出公钥

- openssl是一个加解密工具包，这里使用openssl来导出公钥信息。
- 下载安装包 openssl：http://slproweb.com/products/Win32OpenSSL.html
- 安装包在资料目录下的 Win64OpenSSL-1_1_0g.exe
- 配置openssl的path环境变量，例如：配置在D:\OpenSSL-Win64\bin
- cmd进入xc.keystore文件所在目录执行如下命令

```bash
keytool ‐list ‐rfc ‐‐keystore xc.keystore | openssl x509 ‐inform pem ‐pubkey
```

输入密钥库密码后，解析出来下面的内容，

![解密解析](images/20190819144223812_14786.png)

将上边的公钥拷贝到文本文件中，合并为一行，就是公钥内容

```
-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----
```

#### 3.4.3. 生成JWT令牌

```java
package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {
    /**
     * 创建JWT令牌
     * 创建密钥证书的命令：keytool -genkeypair -alias xckey -keyalg RSA -keypass xuecheng -keystore xc.keystore -storepass xuechengkeystore
     */
    @Test
    public void testCreateJwt() {
        // 密钥库文件
        String key_location = "xc.keystore";
        // 密钥库密码
        String keystore_password = "xuechengkeystore";

        // 获取密钥库文件路径对象
        ClassPathResource resource = new ClassPathResource(key_location);
        // 密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keystore_password.toCharArray());

        // 密钥别名
        String alias = "xckey";
        // 密钥的访问密码
        String keypass = "xuecheng";
        // 密钥对（公钥和私钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypass.toCharArray());
        // 获取私钥(因为密钥是使用RSA算法生成，此处强转为RSAPrivateKey)
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 定义JWT令牌的payload内容
        Map<String, String> token_payload = new HashMap<>();
        token_payload.put("id", "123");
        token_payload.put("name", "斩月");
        token_payload.put("reles", "R01,RO2");
        token_payload.put("ext", "扩展内容");

        // 使用spring security框架的JwtHelper类，生成JWT令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(token_payload), new RsaSigner(rsaPrivateKey));

        // 生成jwt令牌编码
        String jwtEncoded = jwt.getEncoded();
        System.out.println(jwtEncoded);
    }
}
```

#### 3.4.4. 验证JWT令牌

```java
@Test
public void testVerify() {
    // 获取公钥
    String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
    // 需要校验的令牌
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOiLmianlsZXlhoXlrrkiLCJyZWxlcyI6IlIwMSxSTzIiLCJuYW1lIjoi5pap5pyIIiwiaWQiOiIxMjMifQ.j-BIyMt5Lt6pT-CT5wvLsNSzHsizaNsIF4dZLZIQgBRtRv3W87UjjvMQbMOcICZ_QPHIZwc60HuZ9mpvLk1PVvXrJaAFboJKhkCRtpamyhNz2tsfcP15yB-guBuM-TiSk6RdzCX_Pmb7a5UY2gz0nefueqJVxMfngxYXELe878fW5lQf7PkYfrZ196A2xy6Rbt3Yy75vkX82JfPOBB-yBFVw4QqCKqrk4WCe-kMKJun30rPaZmE5nyYjXrFJe2pbtowvBzeNO-Ppl-0JKUbg3NZ_k87u6WmV5oevx88J5KVgOtppFvKZIecuxoZql7O6tpyzxs9LxXoBx_uJa7iQ3A";

    // 使用spring security框架的JwtHelper类，校验JWT令牌
    Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
    // 获取JWT令牌中的自定义内容（payload）
    String jwtClaims = jwt.getClaims();
    System.out.println(jwtClaims);
}
```

## 4. Spring Security Oauth2 研究
### 4.1. 实现目标

本项目认证服务基于Spring Security Oauth2进行构建，并在其基础上作了一些扩展，采用JWT令牌机制，并自定义了用户身份信息的内容。在项目中集成Spring Security Oauth2的方法和流程，通过
spring Security Oauth2的研究需要理解以下内容

1. Oauth2的授权码认证流程及密码认证的流程
2. spring Security Oauth2的工作流程
3. 资源服务集成spring Security框架完成Oauth2认证的流程

### 4.2. 搭建认证服务器

#### 4.2.1. 导入基础工程

创建 xc-service-ucenter-auth 工程，该工程是基于Spring Security Oauth2的一个二次封装的工程，导入此工程研究Oauth2认证流程。

> 直接导入使用已经准备好的工程，【\day16  Spring Security Oauth2\资料\xc-service-ucenter-auth.zip】

#### 4.2.2. 创建数据库

导入资料目录下的 xc_user.sql，创建用户数据库

![用户数据库](images/20190820105731166_30600.png)

以“oauth_”开头的表都是 spring Security 自带的表

> - 本项目中spring Security 主要使用oauth_client_details表
>     - client_id：客户端id
>     - resource_ids：资源id（暂时不用）
>     - client_secret：客户端密码
>     - scope：范围
>     - access_token_validity：访问token的有效期（秒）
>     - refresh_token_validity：刷新token的有效期（秒）
>     - authorized_grant_type：授权类型，authorization_code,password,refresh_token,client_credential

### 4.3. Oauth2 授权码模式

#### 4.3.1. Oauth2 支持的授权模式种类

Oauth2有以下授权模式：

- 授权码模式（Authorization Code）
- 隐式授权模式（Implicit）
- 密码模式（Resource Owner Password Credentials）
- 客户端模式（Client Credentials）

> 其中授权码模式和密码模式应用较多，本次暂时使用授权码模式

#### 4.3.2. 授权码授权流程

上面示例网站使用微信认证的过程就是授权码模式，流程如下：

1. 客户端请求第三方授权
2. 用户(资源拥有者)同意给客户端授权
3. 客户端获取到授权码，请求认证服务器申请令牌
4. 认证服务器向客户端响应令牌
5. 客户端请求资源服务器的资源，资源服务校验令牌合法性，完成授权
6. 资源服务器返回受保护资源

#### 4.3.3. 申请授权码

- 请求认证服务获取授权码(GET请求)

```
http://localhost:40400/auth/oauth/authorize?client_id=XcWebApp&response_type=code&scop=app&redirect_uri=http://localhost
```

- 参数列表如下：
    - client_id：客户端id，和授权配置类中设置的客户端id一致。
    - response_type：授权码模式固定为code
    - scop：客户端范围，和授权配置类中设置的scop一致。
    - redirect_uri：跳转uri，当授权码申请成功后会跳转到此地址，并在后边带上code参数（授权码）

1. 请求上面的url后，首先会跳转到登录页面

![申请授权码步骤1](images/20190820112446834_22055.png)

输入账号和密码，点击 Login。Spring Security接收到请求会调用UserDetailsService接口的loadUserByUsername方法查询用户正确的密码。

当前导入的基础工程中读取xc-user数据库中oauth_client_details表，可以使用“XcWebApp”用户，密码是“XcWebApp”

![申请授权码步骤2](images/20190820112825932_26084.png)

2. 登录成功后，进入授权页面

![申请授权码步骤3](images/20190820113026708_24528.png)

点击“Authorize”，同意授权。接下来返回授权码，**认证服务会携带授权码跳转redirect_uri**。如本次访问的授权码就是“ZfC27C”

![申请授权码步骤4](images/20190820113119929_13015.png)

#### 4.3.4. 申请令牌

拿到授权码后，申请令牌（Post请求）

```
http://localhost:40400/auth/oauth/token
```

请求参数

- grant_type：授权类型，填写authorization_code，表示授权码模式
- code：授权码，就是上一步获取的授权码，注意：授权码只使用一次就无效了，需要重新申请
- redirect_uri：申请授权码时的跳转url，**注意一定和申请授权码时用的redirect_uri一致**

**注意：此链接需要使用 http Basic 认证**

以上测试使用postman完成

- 使用http basic认证：客户端Id和客户端密码会匹配数据库oauth_client_details表中的客户端id及客户端密码

![申请令牌请求1](images/20190820122856683_21883.png)

- 填写post请求参数

![申请令牌请求2](images/20190820123258528_13395.png)

点击发送，申请令牌成功

![令牌请求返回](images/20190820123340220_9206.png)

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6IlhjV2ViQXBwIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTY2MzE4ODA1LCJqdGkiOiIyNzY5MGYyNi1jNmJlLTQxOWEtYTRiYS05ZTlmNDJkZTczYzYiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.YmGsRiHPcdRIdnhVxGm-qRGUrvw5_IPBEx3jdgpLQou7gi2CFlZoleTfGn2ud7HOyoE0Cs6Gc8jgo8Kr2boOqXCY1aO0qHA5ZVRYrR6fwlgqaWUFwEpmX1Mb5tv-9I6QD7a7bArzraDGsB5JYTpnQ4KXWVGq3AedpzUOxCsD3ZaZN6l5WYgjM6RNG_ZdPnHWZ0cD8tgqbHhNtH7sgVLjttV1nRwC0_jpuQPo6f9zX-AUQjB7vdTZclPc7G7cQTHXFiQbrdRM-ElyxrXBS6Mx8wa8tu86yojUi94loSVWs6xL-HAoZ4Co7ONuk5z1FpvASIVyrHFnM0RM-B0I-oxv7w",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6IlhjV2ViQXBwIiwic2NvcGUiOlsiYXBwIl0sImF0aSI6IjI3NjkwZjI2LWM2YmUtNDE5YS1hNGJhLTllOWY0MmRlNzNjNiIsIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTY2MzE4ODA0LCJqdGkiOiJjYWQ3YWNkOC1iZDBlLTQ0ODItYTJkYi01NWE2YjljNGVhYjMiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.RlziJpx3oCPx7BVho2wBmQYdRFNIGAFkFGlAj10X2qREwgeBLLbQyffDXAD-NoC18JH6MO5e_LJ0ZHGlpXNV9Sh59kqHY6XfdvleEAVnSu8kYIr4MA_12zDJICj-mQ3M0Ge15xZDDCwzkp5DWKkSbwYGS1aDpNEuoiXHkpnOS-92QSl9E7gCTVh3OnM8nJhLcfO5J60pYuIGykLAKiZ4hgGJtAYRKy3A_LLxDNTaWVTodC0b5OyiukaB2gufUlbnXekqDlMKykQjG9AgkmxBO8HfO8KEUKiolOxx2oLQvItjuAE71ytkRMamIBB-jZqxh8o_1nzzFmUcc3SX-3C0Rg",
    "expires_in": 43199,
    "scope": "app",
    "jti": "27690f26-c6be-419a-a4ba-9e9f42de73c6"
}
```

- 以上参数解释：
    - access_token：访问令牌，携带此令牌访问资源
    - token_type：有MAC Token与Bearer Token两种类型，两种的校验算法不同，RFC 6750建议Oauth2采用 Bearer Token（http://www.rfcreader.com/#rfc6750）
    - refresh_token：刷新令牌，使用此令牌可以延长访问令牌的过期时间
    - expires_in：过期时间，单位为秒
    - scope：范围，与定义的客户端范围一致
    - jti：用户身份的令牌，用于校验用户是否认证

##### 4.3.4.1. 什么是http Basic认证？

> http协议定义的一种认证方式，将客户端id和客户端密码按照“`客户端ID:客户端密码`”的格式拼接，并用base64编码，放在header中请求服务端  
> 如：`Authorization：Basic WGNXZWJBcHA6WGNXZWJBcHA=`。其中：“WGNXZWJBcHA6WGNXZWJBcHA=”是`用户名:密码`的base64编码  
> 如果认证失败服务端返回`401 Unauthorized`

#### 4.3.5. 资源服务授权

##### 4.3.5.1. 资源服务授权流程

资源服务拥有要访问的受保护资源，客户端携带令牌访问资源服务，如果令牌合法则可成功访问资源服务中的资源

![资源服务授权流程图](images/20190820125019376_30313.jpg)

上图业务流程解释

1. 客户端请求认证服务申请令牌
2. 认证服务生成令牌
    - 认证服务采用非对称加密算法，使用私钥生成令牌。
3. 客户端携带令牌访问资源服务
    - 客户端在Http header 中添加： `Authorization：Bearer 令牌`。
4. 资源服务请求认证服务校验令牌的有效性
    - 资源服务接收到令牌，使用公钥校验令牌的合法性。
5. 令牌有效，资源服务向客户端响应资源信息

##### 4.3.5.2. 资源服务授权配置

基本上所有微服务都是资源服务，以课程管理服务（xc-service-manage-course）为例，配置授权控制，当配置了授权控制后如要访问课程信息则必须提供令牌

1. 配置公钥
    - 认证服务生成令牌采用非对称加密算法，认证服务采用私钥加密生成令牌，对外向资源服务提供公钥，资源服务使用公钥 来校验令牌的合法性
    - 将公钥拷贝到 publickey.txt文件中，将此文件拷贝到资源服务工程的classpath下

![复制公钥文件](images/20190820133842880_32174.png)

2. 在pom.xml文件中，添加oauth2依赖

```xml
<!-- oauth2依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>
```

3. 在config包下创建ResourceServerConfig类，配置oauth2相关参数

```java
package com.xuecheng.manage_course.confing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * spring cloud Oauth2 配置类
 */
@Configuration
@EnableResourceServer   // 标识开启Oauth2服务
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)   // 激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    // 配置公钥
    private static final String PUBLIC_KEY = "publickey.txt";

    // 定义JwtTokenStore，使用jwt令牌
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    // 定义JJwtAccessTokenConverter，使用jwt令牌
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());
        return converter;
    }

    /**
     * 获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    // Http安全配置，对每个到达系统的http请求链接进行校验
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 所有请求必须认证通过
        http.authorizeRequests().anyRequest().authenticated();
    }
}
```

##### 4.3.5.3. 资源服务授权测试

测试课程图片查询，访问url：http://localhost:31200/course/coursepic/list/4028e58161bd3b380161bd3bcd2f0000。请求时没有携带令牌则报错：

![授权测试](images/20190820141037708_20058.png)

请求时携带令牌（使用postman）。在http header中添加 `Authorization: Bearer 令牌`

![授权测试](images/20190820141524298_30042.png)

当输入错误的令牌也无法正常访问资源。

##### 4.3.5.4. 解决swagger-ui无法访问

当课程管理加了授权之后再访问swagger-ui则报错。修改授权配置类ResourceServerConfig的configure方法，针对swagger-ui的请求路径进行放行

```java
// Http安全配置，对每个到达系统的http请求链接进行校验
@Override
public void configure(HttpSecurity http) throws Exception {
    // 所有请求必须认证通过
    http.authorizeRequests()
            // 配置下边的路径放行
            .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui",
                    "/swagger-resources", "/swagger-resources/configuration/security",
                    "/swagger-ui.html", "/webjars/**").permitAll()
            .anyRequest().authenticated();
}
```

> 注意：通过上边的配置虽然可以访问swagger-ui，但是无法进行单元测试，除非去掉认证的配置或在上边配置中添加所有请求均放行（"/**"）。

### 4.4. Oauth2 密码模式授权

密码模式（Resource Owner Password Credentials）与授权码模式的区别是申请令牌不再使用授权码，而是直接通过用户名和密码即可申请令牌

- post请求访问url：http://localhost:40400/auth/oauth/token
- 请求参数
    - grant_type：密码模式授权填写password
    - ~~redirect_uri(非必须)：http://localhost~~
    - username：账号
    - password：密码
- 此链接需要使用 http Basic认证

![密码模式授权申请](images/20190820143131848_14073.png)

上边参数使用x-www-form-urlencoded方式传输，使用postman测试如下

![密码模式授权申请](images/20190820231730131_27562.png)

密码授权是执行UserDetailsService的实现类UserDetailsServiceImpl，目前后端是写死用户名与密码，没有查询数据库

![密码模式授权申请](images/20190820231619386_7146.png)

> 注意：当令牌没有过期时同一个用户再次申请令牌则不再颁发新令牌

### 4.5. 校验令牌

- Spring Security Oauth2提供校验令牌的接口，get 请求：`http://localhost:40400/auth/oauth/check_token?token=`
- 请求参数
    - token：令牌

使用postman请求测试

![校验令牌](images/20190820144457376_13549.png)

请求结果：

```json
{
    "companyId": null,
    "userpic": null,
    "user_name": "XcWebApp",
    "scope": [
        "app"
    ],
    "name": null,
    "utype": null,
    "active": true,
    "id": null,
    "exp": 1566318805,
    "jti": "27690f26-c6be-419a-a4ba-9e9f42de73c6",
    "client_id": "XcWebApp"
}
```

- exp：过期时间，long类型，距离1970年的秒数（`new Date().getTime()`可得到当前时间距离1970年的毫秒数）
- user_name：用户名
- client_id：客户端Id，在oauth_client_details中配置
- scope：客户端范围，在oauth_client_details表中配置
- jti：与令牌对应的唯一标识
- companyId、userpic、name、utype、id：这些字段是本认证服务在Spring Security基础上扩展的用户身份信息

### 4.6. 刷新令牌

刷新令牌是当令牌快过期时重新生成一个令牌，它于授权码授权和密码授权生成令牌不同，刷新令牌不需要授权码也不需要账号和密码，只需要一个刷新令牌、客户端id和客户端密码

- post 请求：http://localhost:40400/auth/oauth/token
- 参数
    - grant_type：固定为 refresh_token
    - refresh_token：刷新令牌（注意不是access_token，而是refresh_token）

![刷新令牌](images/20190820145609491_12934.png)

- 刷新令牌成功，会重新生成新的访问令牌和刷新令牌，令牌的有效期也比旧令牌长
- 刷新令牌通常是在令牌快过期时进行刷新

## 5. 认证接口开发
### 5.1. 需求分析

![用户登录的流程图](images/20190820165456365_27542.jpg)

- 执行流程：
    1. 用户登录，请求认证服务
    2. 认证服务认证通过，生成jwt令牌，将jwt令牌及相关信息写入Redis，并且将身份令牌写入cookie
    3. 用户访问资源页面，带着cookie到网关
    4. 网关从cookie获取token，并查询Redis校验token,如果token不存在则拒绝访问，否则放行
    5. 用户退出，请求认证服务，清除redis中的token，并且删除cookie中的token
- 使用redis存储用户的身份令牌有以下作用：
    1. 实现用户退出注销功能，服务端清除令牌后，即使客户端请求携带token也是无效的。
    2. 由于jwt令牌过长，不宜存储在cookie中，所以将jwt令牌存储在redis，由客户端请求服务端获取并在客户端存储。

### 5.2. Redis 配置
#### 5.2.1. Redis 服务安装

参考资料：\【04】数据库\【03】Redis\02-Redis 安装.md

#### 5.2.2. 引入redis依赖

修改认证服务的pom.xml文件，引入spring-data-redis的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 5.2.3. redis 连接配置

在认证服务（xc-service-ucenter-auth）的application.yml文件中添加如下redis的配置

```yml
spring:
  application:
    name: xc-service-ucenter-auth
  redis:
    host: ${REDIS_HOST:127.0.0.1}
    port: ${REDIS_PORT:6379}
    timeout: 5000 # 连接超时，单位：毫秒
    jedis:
      pool:
        maxActive: 3
        maxIdle: 3
        minIdle: 1
        maxWait: -1 # 连接池最大等行时间，-1代表没有限制
```

#### 5.2.4. redis 测试

```java
package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    /* 注入Redis操作对象，本次只操作字符串类型，所以引入专门操作字符的Redis对象 */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        // 定义存储Redis的key
        String key = "user_token:abde2951-76ab-4a24-8f5e-08604bb46f97";
        // 定义key对应的value
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("jwt", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6IlhjV2ViQXBwIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTY2MzE4ODA1LCJqdGkiOiIyNzY5MGYyNi1jNmJlLTQxOWEtYTRiYS05ZTlmNDJkZTczYzYiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.YmGsRiHPcdRIdnhVxGm-qRGUrvw5_IPBEx3jdgpLQou7gi2CFlZoleTfGn2ud7HOyoE0Cs6Gc8jgo8Kr2boOqXCY1aO0qHA5ZVRYrR6fwlgqaWUFwEpmX1Mb5tv-9I6QD7a7bArzraDGsB5JYTpnQ4KXWVGq3AedpzUOxCsD3ZaZN6l5WYgjM6RNG_ZdPnHWZ0cD8tgqbHhNtH7sgVLjttV1nRwC0_jpuQPo6f9zX-AUQjB7vdTZclPc7G7cQTHXFiQbrdRM-ElyxrXBS6Mx8wa8tu86yojUi94loSVWs6xL-HAoZ4Co7ONuk5z1FpvASIVyrHFnM0RM-B0I-oxv7w");
        valueMap.put("refresh_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6IlhjV2ViQXBwIiwic2NvcGUiOlsiYXBwIl0sImF0aSI6IjI3NjkwZjI2LWM2YmUtNDE5YS1hNGJhLTllOWY0MmRlNzNjNiIsIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTY2MzE4ODA0LCJqdGkiOiJjYWQ3YWNkOC1iZDBlLTQ0ODItYTJkYi01NWE2YjljNGVhYjMiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.RlziJpx3oCPx7BVho2wBmQYdRFNIGAFkFGlAj10X2qREwgeBLLbQyffDXAD-NoC18JH6MO5e_LJ0ZHGlpXNV9Sh59kqHY6XfdvleEAVnSu8kYIr4MA_12zDJICj-mQ3M0Ge15xZDDCwzkp5DWKkSbwYGS1aDpNEuoiXHkpnOS-92QSl9E7gCTVh3OnM8nJhLcfO5J60pYuIGykLAKiZ4hgGJtAYRKy3A_LLxDNTaWVTodC0b5OyiukaB2gufUlbnXekqDlMKykQjG9AgkmxBO8HfO8KEUKiolOxx2oLQvItjuAE71ytkRMamIBB-jZqxh8o_1nzzFmUcc3SX-3C0Rg");
        String value = JSON.toJSONString(valueMap);

        // 校验key是否存在，如果不存在则返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);

        // 存储数据，并设置30秒后过期
        stringRedisTemplate.boundValueOps(key).set(value, 30, TimeUnit.SECONDS);
        // 获取数据
        String readValue = stringRedisTemplate.opsForValue().get(key);
        System.out.println(readValue);
    }
}
```

### 5.3. 认证服务
#### 5.3.1. 需求分析

- 认证服务需要实现的功能如下
1. 登录接口
    - 前端post提交账号、密码等，用户身份校验通过，生成令牌，并将令牌存储到redis
    - 将令牌写入cookie
2. 退出接口
    - 校验当前用户的身份为合法并且为已登录状态
    - 将令牌从redis删除
    - 删除cookie中的令牌
- 业务流程如下

![认证服务业务流程图](images/20190821095853015_27503.jpg)

#### 5.3.2. API 接口

在xc-service-api工程，创建com.xuecheng.api.auth.AuthControllerApi接口，定义登陆与退出的接口

```java
package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户认证接口
 */
@Api(value = "用户认证", description = "用户认证接口")
public interface AuthControllerApi {

    @ApiOperation("登陆")
    public LoginResult login(LoginRequest loginRequest);

    @ApiOperation("退出")
    public ResponseResult logout();
}
```

#### 5.3.3. 配置参数

在xc-service-ucenter-auth工程，配置application.yml的auth验证参数

```yml
auth:
  tokenValiditySeconds: 1200  # token存储到redis的过期时间
  clientId: XcWebApp
  clientSecret: XcWebApp
  cookieDomain: xuecheng.com
  cookieMaxAge: -1
```

#### 5.3.4. 申请令牌测试

为了不破坏Spring Security的代码，在Service方法中通过RestTemplate请求Spring Security所暴露的申请令牌接口来申请令牌

```java
package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 申请令牌测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {
    /* 注入http调用接口 */
    @Autowired
    private RestTemplate restTemplate;
    /* 注入Ribbon负载均衡器核心类 */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 申请令牌测试
     */
    @Test
    public void testClient() {
        /*
         * 采用客户端负载均衡，从eureka获取认证服务的ip和端口(一个实例的地址)
         *  因为spring security在认证服务中
         */
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);

        // 获取服务的url，此地址格式是http://ip:port
        URI uri = serviceInstance.getUri();
        // 拼接令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        /*
         * 定义header部分(使用spring框架封装的Map集合)，包括了http basic认证信息
         *      interface MultiValueMap<K, V> extends Map<K, List<V>>
         *      class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable, Cloneable
         *      相当于一键多值的使用场景，在使用该数据结构之前，通常会自己定义Map<K, List<V>>
         */
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization", httpBasic);

        // 定义body部分
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", "moon");
        body.add("password", "123");
        // body.add("password", "1231212");    // 测试错误情况

        /*
         * HttpEntity类，用于Http消息发送或接受的实体
         *  构造方法：public HttpEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers)
         */
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

        // 指定restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // 当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        // 远程调用申请令牌
        ResponseEntity<Map> responseEntity = restTemplate
                .exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        // 获取申请的令牌
        Map entityBody = responseEntity.getBody();
        System.out.println(entityBody);
    }

    /**
     * 获取HttpBasic的字符串
     *
     * @param clientId     客户端ID
     * @param clientSecret 客户端密码
     * @return 返回base64编码后的“客户端ID:客户端密码”
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        // 拼接HttpBasic的字符串，按“客户端id:客户端密码”格式
        String httpBasicStr = String.format("%s:%s", clientId, clientSecret);
        // 进行base64编码
        byte[] base64Encode = Base64Utils.encode(httpBasicStr.getBytes());
        return String.format("Basic %s", new String(base64Encode));
    }
}
```

#### 5.3.5. dao 层

*暂时使用静态数据，待用户登录调通再连接数据库校验用户信息*

#### 5.3.6. service 层    

此分层主要的处理逻辑是：调用认证服务申请令牌，并将令牌存储到redis

1. 在xc-framework-model工程中，创建AuthToken模型类，存储申请的令牌，包括身份令牌、刷新令牌、jwt令牌
    - 身份令牌：用于校验用户是否认证
    - 刷新令牌：JWT令牌快过期时执行刷新令牌
    - JWT令牌：用于授权

```java
package com.xuecheng.framework.domain.ucenter.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * AuthToken模型类，存储申请的令牌，包括身份令牌、刷新令牌、jwt令牌
 */
@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    String access_token;    // 访问token
    String refresh_token;   // 刷新token
    String jwt_token;   // jwt令牌
}
```

2. 在xc-service-ucenter-auth工程中，创建AuthService类，编写申请令牌的方法

```java
package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证授权业务逻辑层
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    /* 注入application文件中验证的相关配置 */
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    /* 注入http调用接口 */
    @Autowired
    private RestTemplate restTemplate;
    /* 注入Ribbon负载均衡器核心类 */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /* 注入字符串类型的redis操作对象 */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登陆认证，申请令牌，将令牌存储到redis
     *
     * @param username     用户名
     * @param password     用户密码
     * @param clientId     客户端ID
     * @param clientSecret 客户端密码
     * @return AuthToken验证模型类
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 调用方法，申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        // 判断是否成功返回令牌
        if (authToken == null) {
            // 抛出申请令牌失败异常
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        // 获取用户身份验证
        String access_token = authToken.getAccess_token();
        // 存储到redis中的内容
        String content = JSON.toJSONString(authToken);
        // 将令牌数据存储到Redis中
        boolean saveResult = this.saveTokenToRedis(access_token, content, this.tokenValiditySeconds);
        if (!saveResult) {
            // 存储失败，抛出存储令牌失败异常
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 将JWT令牌存储到Redis中
     *
     * @param access_token         用户身份令牌
     * @param content              内容是AuthToken对象转成的JSON字符串
     * @param tokenValiditySeconds redis中数据的过期时间
     * @return 操作失败标识
     */
    private boolean saveTokenToRedis(String access_token, String content, long tokenValiditySeconds) {
        // 组合Redis存储的key
        String key = String.format("user_token:%s", access_token);
        // 存储到redis中
        stringRedisTemplate.boundValueOps(key).set(content, tokenValiditySeconds, TimeUnit.SECONDS);
        // 获取key的过期时间
        Long expire = stringRedisTemplate.getExpire(key);
        return expire > 0;
    }

    /**
     * 申请令牌
     *
     * @param username     用户名
     * @param password     用户密码
     * @param clientId     客户端ID
     * @param clientSecret 客户端密码
     * @return AuthToken验证模型类
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        /*
         * 采用客户端负载均衡，从eureka获取认证服务的ip和端口(一个实例的地址)
         *  认证服务在eureka中注册的名称是：xc-service-ucenter-auth
         */
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);

        // 判断是否调用到服务
        if (serviceInstance == null) {
            LOGGER.error("choose an auth instance fail");
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_AUTHSERVER_NOTFOUND);
        }

        // 获取服务的url，此地址格式是http://ip:port
        URI uri = serviceInstance.getUri();
        // 拼接令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        // 定义header部分(使用spring框架封装的Map集合)，包括了http basic认证信息
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", this.getHttpBasic(clientId, clientSecret));

        // 定义body部分
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password"); // 授权方式
        body.add("username", username); // 账号
        body.add("password", password); // 密码

        // HttpEntity类，用于Http消息发送或接受的实体
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

        // 指定restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // 当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });


        Map tokenMap = null;
        try {
            // 远程调用申请令牌
            ResponseEntity<Map> responseEntity = restTemplate
                    .exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
            // 获取申请的令牌
            tokenMap = responseEntity.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            LOGGER.error("request oauth_token_password error: {}", e.getMessage());
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        // 判断返回的令牌信息
        if (tokenMap == null
                || tokenMap.get("access_token") == null
                || tokenMap.get("refresh_token") == null
                || tokenMap.get("jti") == null) {   // jti是jwt令牌的唯一标识作为用户身份令牌
            // 其中一个信息为空，则是授权失败，返回Null
            return null;
        }

        // 成功授权，设置令牌信息
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) tokenMap.get("jti"));    // jti，作为用户的身份标识，验证用户是否认证
        authToken.setRefresh_token((String) tokenMap.get("refresh_token")); // 刷新令牌(jwt)
        authToken.setJwt_token((String) tokenMap.get("access_token"));  // 访问令牌(JWT)
        return authToken;
    }

    /**
     * 获取HttpBasic的字符串
     *
     * @param clientId     客户端ID
     * @param clientSecret 客户端密码
     * @return 返回base64编码后的“客户端ID:客户端密码”
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        // 拼接HttpBasic的字符串，按“客户端id:客户端密码”格式
        String httpBasicStr = String.format("%s:%s", clientId, clientSecret);

        // 进行base64编码
        byte[] base64Encode = Base64Utils.encode(httpBasicStr.getBytes());
        return String.format("Basic %s", new String(base64Encode));
    }
}
```

#### 5.3.7. Controller 层

在xc-service-ucenter-auth工程中，创建AuthController类实现AuthControllerApi接口

```java
package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户验证授权控制层
 */
@RestController
public class AuthController implements AuthControllerApi {

    /* 注入授权业务逻辑层 */
    @Autowired
    private AuthService authService;

    /* 注入配置文件数据 */
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    /**
     * 用户授权登陆
     *
     * @param loginRequest 登陆请求数据
     * @return 登陆结果
     */
    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        // 校验账号是否输入成功
        if (loginRequest == null || StringUtils.isBlank(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        // 校验密码是否输入成功
        if (StringUtils.isBlank(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }

        // 调用业务层方法，申请令牌
        AuthToken authToken = authService.login(loginRequest.getUsername(),
                loginRequest.getPassword(), this.clientId, this.clientSecret);

        // 获取用户身份令牌
        String access_token = authToken.getAccess_token();
        // 将令牌存储到cookie中
        this.saveCookie(access_token);


        return null;
    }

    /**
     * 将用户身份令牌存储到cookie
     *
     * @param access_token 用户身份令牌jti
     */
    private void saveCookie(String access_token) {
        // 获取HttpServletResponse对象
        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        // 调用工具类设置cookie的方法
        // CookieUtil.addCookie(response, cookieDomain, "/", "uid", access_token, cookieMaxAge, false);

        // 将工具类封装的方法拿出来自己设置一次
        Cookie cookie = new Cookie("uid", access_token);
        cookie.setDomain(this.cookieDomain);
        cookie.setPath("/");
        cookie.setMaxAge(this.cookieMaxAge);
        // HttpOnly参数设置为false，表示允许浏览器获取
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    /**
     * 用户退出
     *
     * @return
     */
    @Override
    public ResponseResult logout() {
        return null;
    }
}
```

#### 5.3.8. 登录url放行

- 认证服务默认都要校验用户的身份信息，所以需要将登录url放行。
- 在 WebSecurityConfig 类中重写 configure(WebSecurity web)方法

```java
@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/userlogin");
    }
}
```

#### 5.3.9. 测试认证接口

使用postman测试，Post请求：http://localhost:40400/auth/userlogin

![测试认证接口](images/20190821182115505_10614.png)

![测试认证接口](images/20190821182134816_6121.png)

#### 5.3.10. 测试写入Cookie

cookie最终会写到xuecheng.com域名下，可通过nginx代理进行认证，测试cookie是否写成功

1. 配置nginx代理（conf/nginx.conf），在ucenter.xuecheng.com下配置代理路径，并启动nginx

```conf
server {
    listen       80;
    server_name  ucenter.xuecheng.com;
	ssi on;
	ssi_silent_errors on;

	# 认证授权
	location ^~ /openapi/auth/ {
		proxy_pass http://auth_server_pool/auth/;
	}
}

# 认证服务
upstream auth_server_pool {
	server 127.0.0.1:40400 weight=10;
}
```

2. 修改本地host文件，添加`ucenter.xuecheng.com`域名映射

```
127.0.0.1  ucenter.xuecheng.com
```

3. 使用postman测试，post请求：http://ucenter.xuecheng.com/openapi/auth/userlogin

![测试写入Cookie](images/20190821184946525_28299.png)
