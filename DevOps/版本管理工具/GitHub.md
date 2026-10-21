## GitHub 概述

> 官网：https://github.com/

GitHub 全球最大的开源社区，是一个面向开源及私有软件项目的托管平台。

## 创建 GitHub 仓库

### 注册账号

使用邮箱注册即可

![](images/464364117221070.png)

### 创建仓库

登录 github，选择【New repository】，创建一个仓库

![](images/132462717221408.jpg)

填写仓库相关的信息

![](images/534702717223912.jpg)

点击【create repository】按钮仓库就创建成功

![](images/277242817232859.jpg)

Github 支持两种协议【https】和【ssh】。如果使用 https 很简单基本不需要配置就可以使用，但是每次提交代码和下载代码时都需要输入用户名和密码。而且如果是公司配置的私有 git 服务器一般不提供 https 方式访问，都采用 ssh

### SSH 密钥生成

SSH是英文Secure Shell的简写形式。通过使用SSH，它可以把所有传输的数据进行加密，这样可以防止DNS欺骗和IP欺骗。最早的时候，互联网通信都是明文通信，一旦被截获，内容就暴露无疑。1995年，芬兰学者Tatu Ylonen设计了SSH协议，将登录信息全部加密，成为互联网安全的一个基本解决方案，迅速在全世界获得推广，目前已经成为Linux系统的标准配置。

在 windows 系统下，可以使用 Git Bash.exe 来生成密钥，在任意目录下右键菜单点击【Git Bash Here】。

![](images/502343617253308.png)

执行命令 `ssh-keygen -t rsa`，生成公钥和私钥

![](images/427853817235521.png)

生成的密钥对在 `c:/users/当前用户/.ssh` 目录中

![](images/502233817224819.jpg)

### 配置指定 SSH 密钥（非必须）

编辑 SSH 的配置文件 `~/.ssh/config`，并加入以下配置：

```
Host github.com
    HostName github.com
    User git
    IdentityFile /root/.ssh/xxx
    IdentitiesOnly yes
```

参数解析：

- `Host github.com` 用于指定该密钥的主机名，这里必须使用代码库的主机名 github.com。
- `HostName github.com` 指定主机对应的具体域名，与 Host 保持一致。Host 和 HostName 可以不同，但是 Host 必须与代码库的主机名匹配，也就是 Git 在需要时会使用代码库的主机名来在 SSH 配置文件中查找匹配的 Host，找到后就使用该配置，访问的具体域名将采用 HostName。
- `User git` 说明该配置的用户必须是 git。
- `IdentityFile /root/.ssh/xxx` 这一行非常关键，需要指定要使用那个 SSH 密钥文件，这里的密钥文件一定是私钥文件，不是有 pub 后缀的那个。
- `IdentitiesOnly yes` 请配置为 yes。

### ssh 密钥配置

密钥生成后，需要在 github 上配置 ssh 的公钥，本地才可以使用 ssh 方式进行代码拉取与提交。

![](images/459433317225744.png)

使用文本编辑器打开刚刚生成的公钥(id_rsa.pub)，在 key 部分将 id_rsa.pub 文件内容添加进去，然后点击【Add SSH key】按钮完成配置

![](images/198073417226353.png)

## 常用操作含义

- watch：会持续收到该项目的动态
- fork，复制某个项目到自己的Github仓库中
- star，可以理解为点赞
- clone，将项目下载至本地
- follow，关注你感兴趣的作者，会收到他们的动态

### Fork

Fork 也是 git 服务网站的功能，用来简化协作流程。在没有 fork 的时候，如果想往开源项目里修 bug 或者加 feature，会需要这样的流程：

1. 克隆 repo
2. 修改代码
3. 生成补丁
4. 发到论坛或者支持的邮件列表
5. 找作者来 review，合并补丁

很多项目到现在还是这么做的。如果有了 fork，可以简化成：

1. Fork 并克隆 repo
2. 修改代码
3. 发出 merge request 或者 pull request

虽然 fork 很有用，但这仍然不是 git 的一部分。它用到的是 git 的分布式能力。本质上，在 fork 的时候，它会克隆一份 repo，把原来的 repo 设置成上游。所以其实如果目标不是为了继续把 repo 放在网络服务上，那就克隆到本地即可。太多的人把 fork 当作 like 来用，根本就是错的。如果没打算改代码，fork 是没意义的。

### Merge request / Pull request

Github 上叫 Pull Request，gitlab 上叫 merge request，其实是一个东西的不同视角。这些都是 code review 和合并的流程，不是 git 的一部分。

需要注意的是，它们的重点在“request”，而不是 merge 或者 pull。如果要把一个分支 merge 到自身，没必要开一个 MR 然后自己给自己通过。在本地 merge 就是了，更简单更快。

## Github 搜索技巧

### 通过 in 关键词限制搜索范围

语法：

- `in:name xxx`：项目名包含xxx的
- `in:description xxx`：项目描述包含xxx的
- `in:readme xxx`：项目的readme文件中包含xxx的

也可以通过 `in:name,desciption xxx` 来组合使用。示例：

```
# 查出仓库名中有『python』的项目
in:name python
# 与 in:name python 一样效果
python in:name
# 查询仓库名或者项目描述中有『python』的项目
in:name,desciption python
# 查询readme.md文件中包含『python』的项目
in:readme python
# 查询 octocat 的 hello-world 项目（指定了某个人的某个项目）
repo:octocat/hello-world
```

### 通过辅助限定词查询项目

可以通过限制 `项目大小(size)`、`拥护者数(followers)`、`fork数(forks)`、`stars数(stars)`、`创建时间(created)`、`更新时间(pushed)`、`项目所用语言(language)`、`topic标签(topic)`、`topic标签数(topics)`...来筛选项目

辅助限定词可以多个并用，用空格隔开，可以搭配限定词使用，也可单独使用。通过通配符 `>`、`<`、`=` 指定搜索区间，区间范围内可通过`num1..num2`。

#### 根据 stars 或 fork 数量去查找

语法：

- `stars:>xxx` stars 数大于 xxx
- `stars:a..b` stars 数在 a 与 b 之间
- `forks:>xxx` forks 数大于 xxx
- `forks:a..b` forks 数在 a 与 b 之间

示例：

```
# 查找star数大于等于5000的springboot项目
spring boot stars:>=5000
# 查找fork数大于500的springcloud项目
spring cloud forks:>500
# 查找fork在100到200之间并且stars数在80到100之间的springboot项目
spring boot forks:100..200 stars:80..100
# 通过指定名称来进一步缩小范围，Star数量过滤，要求Star数量大于3000
in:name spring boot starts:>3000
```

#### 按照地区和语言进行搜索

语法：

- `location:地区名称` 查询指定地区的项目
- `language:语言` 查询指定语言的项目

示例：

```
# 搜索语言为javaScript 的项目
language:javaScript
# 搜索包含 moon 关键字的 javaScript 语言项目
language:javaScript moon
# 地区为 china 的项目
location:China
# 组合使用，索地区在北京的Java方向的用户
location:beijing language:java
```

#### 根据仓库大小搜索

语法：

- `size:> 数字`

> [!note] 其中数字的单位是 Kb

示例：

```
# 搜索项目大小等于 1MB 的项目
size:1000
# 搜索项目大小小于 1MB 的项目
size:<=1000
# 搜索项目名字中有 python 且小于 1MB 的项目
in:name python size:<=1000
# 搜索仓库大小在50kb到120kb之间的项目
size:50..120
```

#### 根据仓库更新与创建时间进行搜索

语法：

- `pushed:> YYYY-MM-DD` 最后上传日期大于YYYY-MM-DD
- `created:> YYYY-MM-DD` 创建日期大于YYYY-MM-DD

示例：

```
# 搜索2020年最新更新的 Spring Boot 项目
pushed:>2020-01-03 Spring Boot
# 搜索仓库中包含 wechat 关键字并且是在2023年之前创建的项目（也支持时分秒，支持大于小于区间等）
created:<2023-01-01 wechat
```

#### 根据某个人或组织进行搜索

语法：

- `user:{USERNAME}`：查询指定用户的内容
- `org:{ORGNAME}`：查询指定组织的内容
- `followers:>=xxx` 查找关注者数量超过xxx的开发者

示例：

```bash
# 查询用户为 1335951413 的 stars 少于 10 的项目
user:1335951413 stars:<10
# 查询 spring 组织名下的项目
org:spring
# 搜索follow都大于1000的项目（支持大于小于区间等）
followers:>1000
```

#### 根据仓库的 LICENSE 搜索

开源项目的 License（项目授权协议） 有的开源项目作者明确禁止商用。“非常友好”的协议，比较出名的有这几种：BSD、MPL（Mozilla）、Apache、MIT。这些协议不但允许项目的使用者使用开源库，有些还允许对开源库进行修改并重新分发。以下是详细介绍了各个License的区别：

> http://choosealicense.com/licenses/

语法：

- `license:对应协议`

示例：

```
# 搜索开源协议是为 Apache License 2 的代码
license:apache-2.0 Spring Boot
```

#### 其他条件搜索

语法：

- `topic:主题名称` 包含指定主题的项目（项目描述下面的内容，相关于标签、分类）
- `topics:n` 指定主题数量的项目（支持大于小于区间等）
- `archived:true/false` 是否已封存的项目（已经不再维护了项目）
- `is:public/private` 是公开还是私有的项目（私有的其他人是无法搜索，因此这个是用来搜索自己的私有项目）

示例：

```
# 搜索含有 moon 这个 topic 的项目
topic:moon
# 搜索有5个以上 topic 的项目
topics:>5
# 搜索已经封存并且含有 java 关键字的项目
archived:true java
# 搜索仓库中所有spring组织公开的项目
is:public org:spring
# 搜索包含 spring 关键字的私有项目（自己或者是成员的私有项目）
is:private spring
```

### awesome + 关键字

Awesome 似乎已经成为不少 GitHub 项目喜爱的命名之一，Awesome 往往整合了大量的同一领域的资料。

通过搜索和关键字匹配的优秀项目。如：`awesome springboot`，搜索优秀的 springboot 相关的项目，包括框架、教程等

### 分享项目中某一行的代码

只需要在具体的网址后面拼接`#Lxx`(xx为行数)

如：需要分享这个类中的@SprintBootApplication注解，值需要在后面拼接上#L6 即可

```
https://github.com/lxy-go/SpringBoot/.../JpaApplication.java#L6
```

浏览器访问时发现高亮显示了

![分享项目中某一行的代码1](images/20190421130608145_19338.jpg)

当然也可以段落进行高亮显示

```
https://github.com/lxy-go/SpringBoot/blob/master/SpringBoot/DataJpaApplication.java#L6-L10
```

![分享项目中某一行的代码2](images/20190421130615530_6615.jpg)

### 项目内搜索

打开想要搜索的项目，然后按一下【`T`】键。会跳转至一个新的网页，

![项目内搜索1](images/20190421130854204_15235.jpg)

如想要搜索该项目的配置文件 application.yml

![项目内搜索2](images/20190421130901747_7542.jpg)

类似的页面内快捷键还有很多，常用的还有【`L`】, 在文件内跳转到多少行。更多快捷键可以查看官方文档：https://help.github.com/en/articles/using-keyboard-shortcuts

### 热门搜索（GitHub Trend 和 GitHub Topic）

GitHub Trend 页面总结了每天/每周/每月周期的热门 Repositories 和 Developers，可以看到在某个周期处于热门状态的开发项目和开发者

> https://github.com/trending

GitHub Topic 展示了最新和最流行的讨论主题，在这里不仅能够看到开发项目，还能看到更多非开发技术的讨论主题

> https://github.com/topics

## 推荐一些 Github 插件（chrome浏览器插件）

### Octotree

平时浏览github代码时，每个文件都需要点击才能查看，用了这个插件可以将项目的目录结构以树形结构显示，点击之后会自动跳转到相应的目录。墙裂推荐！

### OctoLinker

该插件作用是：在浏览代码像ide那样可以链接跳转。只需要ctrl+点击变量名即可。

### Sourcegraph

一个可以在github上浏览和搜索代码的工具，安装好插件之后会出现一个view Repository的按钮。

点击进去之后，可以随意在项目中搜索，可以查看变量和方法，以及进行跳转等等。

## GitHub 项目定时发布最新 Hosts

项目地址：https://github.com/521xueweihan/GitHub520

该项目会自动发布在指定的地址上，结合软件使用，可以完全自动化，无需持续更新

### 手动修改

hosts 文件在每个系统的位置如下：

- Windows 系统：C:\Windows\System32\drivers\etc\hosts
- Linux 系统：/etc/hosts
- Mac（苹果电脑）系统：/etc/hosts
- Android（安卓）系统：/system/etc/hosts
- iPhone（iOS）系统：/etc/hosts

```
# GitHub520 Host Start
185.199.108.154               github.githubassets.com
199.232.68.133                camo.githubusercontent.com
199.232.68.133                github.map.fastly.net
199.232.69.194                github.global.ssl.fastly.net
140.82.113.4                  github.com
140.82.113.5                  api.github.com
199.232.68.133                raw.githubusercontent.com
199.232.68.133                user-images.githubusercontent.com
199.232.68.133                favicons.githubusercontent.com
199.232.68.133                avatars5.githubusercontent.com
199.232.68.133                avatars4.githubusercontent.com
199.232.68.133                avatars3.githubusercontent.com
199.232.68.133                avatars2.githubusercontent.com
199.232.68.133                avatars1.githubusercontent.com
199.232.68.133                avatars0.githubusercontent.com
# Star me GitHub url: https://github.com/521xueweihan/GitHub520
# GitHub520 Host End

数据更新时间：2020-07-25T20:14:26+08:00
```

### 激活生效

大部分情况下是直接生效，如未生效可尝试下面的办法，刷新 DNS：

1. Windows：在 CMD 窗口输入：`ipconfig /flushdns`
2. Linux 命令：`sudo rcnscd restart`
3. Mac 命令`：sudo killall -HUP mDNSResponder`

> [!tip] 上述方法无效可以尝试重启机器。

### 自动修改方式

> [!tip] 推荐 SwitchHosts 工具管理 hosts。以 SwitchHosts 为例，自动修改配置参考下面：

- Title: 随意
- Type: Remote
- URL: https://gitee.com/xueweihan/codes/6g793pm2k1hacwfbyesl464/raw?blob_name=GitHub520.yml
- Auto Refresh: 最好选 1 hour

## Github 常用设置

### 关闭 GitHub Copilot 使用个人数据训练大模型

GitHub Copilot 宣布自 4 月 24 日起，将使用**个人交互数据来训练大模型**，被用于训练大模型的交互数据包括输入、输出、代码片段及相关上下文。受影响用户：

- Copilot Free
- Copilot Pro
- Copilot Pro+

主要是个人用户，Copilot Business 和 Copilot Enterprise 用户不受此次更新的影响。如果没用过 Copilot 就没事，如果用过 Copilot 的话，不管是 Copilot 个人付费用户，还是 Copilot 个人免费用户，所有交互数据都被默认用来训练模型了。

> [!warning] 这个开关是<span style="color: red;">**默认开启**</span>的，需要自己去个人设置中关闭。

禁用大模型训练设置，进入 Copilot Features 菜单 `https://github.com/settings/copilot/features`。在 Privacy 中关闭 `Allow GitHub to use my data for AI model training` 选项，此选项默认是 `Enabled` 的，手动禁用即可。

![](images/595917542180285.png)

## Github 扩展内容

### Github 登陆双重验证配置(configuring two factor authentication)

> docs: https://docs.github.com/en/authentication/securing-your-account-with-two-factor-authentication-2fa/configuring-two-factor-authentication

1. 使用`Authenticator app`方式：手机下载app `Microsoft Authenticator`（不需要注册，用来实时扫码就行）
    - 第一次 github2FA 扫码会生成一个`github-recovery-codes.txt`，保存到本地，用来恢复账号。
2. 再次打开，使用`Security keys`方式：按提示输入一个`key name`，windows 用户直接用锁屏 pin 码验证即可。

### 解决 GitHub Pages 访问不了

GitHub正常访问，但GitHub Pages无法访问。尝试过修改hosts域名的映射，没有作用。后来直接修改网卡的DNS服务器地址为`114.114.114.114`可访问（*不确保百分百有效*）

![](images/20201105231308260_23184.png)

### 一键生成 Github 简历

通过 http://resume.github.io/ 这个网站可以一键生成一个在线的 Github 简历。

### 个性化 Github 首页

创建一个和你的 Github 账户同名的仓库，然后自定义README.md的内容，该内容就展示在主页上。

通过 `github-readme-stats` 这个开源项目，可以在 README 中展示动态生成的 GitHub 统计信息。

### 自定义项目徽章

通过 https://shields.io/ 这个网站生成的，还可以动态读取你项目的状态并生成对应的徽章。

### Github 表情

表情素材参考：https://www.webfx.com/tools/emoji-cheat-sheet/

### 自动为 Markdown 文件生成目录

如果想为 Github 上的 Markdown 文件生成目录的话，通过 VS Code 的 Markdown Preview Enhanced 这个插件就可以了。

生成的目录后，可以直接点击目录中的链接即可跳转到文章对应的位置，可以优化阅读体验。

### Github 项目无法通过 ssh 连接，无法 push pull

#### 问题描述

2024年01月20日，最近这两天 github 遇到问题，就是以前正常使用的 git 项目，现在无法 push 或 pull 了。错误提示如下：

```
ssh: connect to host github.com port 22: Connection timed out
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.
```

#### 解决方法

解决方案是修改 SSH 配置文件，通过端口 443 以 SSH 方式登录 git@ssh.github.com，则可以覆盖 SSH 设置，强制通过该服务器和端口连接 GitHub.com。（可以参考官方说明，[Using SSH over the HTTPS port](https://docs.github.com/en/authentication/troubleshooting-ssh/using-ssh-over-the-https-port)）

要测试是否可以通过 HTTPS 端口传输 SSH，运行以下命令：

```bash
$ ssh -T -p 443 git@ssh.github.com
> Hi USERNAME! You've successfully authenticated, but GitHub does not provide shell access.
```

如果运行不成功，则在 `~/.ssh/config` 中添加以下配置，以前的项目就都能正常使用了。

```
Host github.com
    Hostname ssh.github.com
    Port 443
    User git
```

再次连接到 GitHub.com 来测试是否正常工作：

```bash
$ ssh -T git@github.com
> Hi USERNAME! You've successfully authenticated, but GitHub does not provide shell access.
```

改用端口 443 后，第一次与 GitHub 交互时，可能会收到一条警告信息，称在已知主机中找不到该主机，或以其他名称找到了该主机。

```bash
> The authenticity of host '[ssh.github.com]:443 ([140.82.112.36]:443)' can't be established.
> ED25519 key fingerprint is SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU.
> This host key is known by the following other names/addresses:
>     ~/.ssh/known_hosts:32: github.com
> Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

假设 SSH 指纹与 GitHub 公布的指纹之一相符，输入 “yes” 即可。

后面要克隆版本库，可以运行以下命令（主要增加 443 端口号）：

```bash
git clone ssh://git@ssh.github.com:443/YOUR-USERNAME/YOUR-REPOSITORY.git
```

## GitHub 仓库同步到 Gitee 镜像

### 场景说明

GitHub 访问不稳定、团队主要使用 Gitee 时，可以保留 GitHub 作为主仓库，把指定分支自动同步到 Gitee，形成只读镜像。

这里的镜像只同步 Git 提交历史。Issues、Pull Requests、Wiki、Releases、Actions Secrets、Pages 等平台数据不会被同步；Gitee Pages 也不在同步范围内。

> [!warning]
> 建议把 Gitee 仓库定位为只读镜像，所有提交只在 GitHub 进行。Gitee 上的独立提交会在下一次强制同步时被覆盖。

### 实现原理

整体链路如下：

1. 开发者向 GitHub 的 `main` 或 `dev` 分支推送提交。
2. GitHub Actions 在 `ubuntu-latest` 上启动工作流。
3. Runner 拉取完整 Git 历史。
4. Runner 从 GitHub Secret 读取私钥，写入临时 SSH 配置。
5. Runner 把 Gitee 仓库添加为远程仓库，并按需拉取目标分支。
6. Runner 执行 `git push --force-with-lease`，把当前分支推送到 Gitee 同名分支。

角色划分：

| 角色 | 作用 |
| :---: | --- |
| GitHub 仓库 | 主仓库，唯一可信源 |
| GitHub Actions | 执行同步任务 |
| GitHub Secret | 保存 SSH 私钥 |
| Gitee 部署公钥 | 授权 Actions 向 Gitee 推送 |
| Gitee 仓库 | 只读镜像 |

核心原则：<span style="color: purple;">**GitHub 是唯一可信源，Gitee 只接收推送，不产生任何提交。**</span>

#### 为什么用 SSH 部署公钥

- 权限范围小：部署公钥只作用于一个 Gitee 仓库，不是整个账号。
- 不需要账号密码：避免在 Actions 中保存 Gitee 账号密码。
- 便于轮换：私钥只保存在 GitHub Secret（本地副本可删除），公钥只存在 Gitee；更换密钥不影响其他仓库。
- 便于管理：部署公钥可以单独命名，出问题时能快速定位和删除。

#### force-with-lease 的作用

`git push --force-with-lease` 会检查远程分支是否仍指向本地记录的版本；如果远程在 fetch 之后被别人更新，推送会被拒绝，避免覆盖并发修改。

需要注意的是：工作流在推送前刚刚 fetch 过 Gitee 分支，因此 `--force-with-lease` 只能防止 fetch 与 push 之间的并发更新，不能防止 Gitee 在更早之前就已经存在的独立提交。如果 Gitee 端有人手工提交，下一次同步仍然会覆盖它。

如果希望“发现 Gitee 有独立提交就终止”，可以在 push 前加祖先检查：

```bash
if git rev-parse --verify "refs/remotes/gitee/${GITHUB_REF_NAME}" >/dev/null 2>&1; then
  if ! git merge-base --is-ancestor "refs/remotes/gitee/${GITHUB_REF_NAME}" HEAD; then
    echo "Gitee 分支存在独立提交，终止同步。"
    exit 1
  fi
fi
```

### 方案对比

| 方案 | 配置位置 | 优点 | 限制 |
| :---: | --- | --- | --- |
| GitHub Actions 推送 | 仓库内 `.github/workflows/` | 配置版本化、按分支触发、可控性强 | 需要配置 Secret 和部署公钥；每次运行要拉取完整历史；消耗 Actions 时间 |
| Gitee 仓库镜像 | Gitee 仓库设置 | 不需要写工作流 | 自动同步频率、权限受 Gitee 套餐和功能限制；分支与强制策略可控性较弱；配置不在仓库内 |
| 本地定时推送 | 本地脚本或计划任务 | 实现简单 | 依赖本地机器在线；密钥管理分散；不适合多人协作 |

> [!tip]
> 需要精确控制同步分支、保留完整配置历史时，优先选择 GitHub Actions；只想要一个简单备份、且 Gitee 套餐支持自动镜像时，可以考虑 Gitee 仓库镜像。

### 前置条件

- GitHub 仓库已启用 Actions，当前账号有仓库管理员权限。
- Gitee 仓库已创建，且确认没有需要保留的独立提交；首次同步前建议先备份。
- 两边分支名称一致，示例使用 `main` 和 `dev`。
- Gitee 仓库的默认分支建议与 GitHub 主分支一致（通常是 `main`），否则仓库首页展示的可能是旧分支。
- Gitee 仓库没有会阻止强制推送的分支保护规则。
- 仓库没有超过 Gitee 单文件或仓库容量限制；镜像会带上完整历史。
- Windows 环境建议使用 Git Bash 执行 SSH 相关命令。

### 实施步骤

#### 生成专用 SSH 密钥

在本地终端执行：

```bash
ssh-keygen -t ed25519 -C "github-actions:gitee-mirror" -f ~/.ssh/gitee_mirror -N ""
```

参数说明：

- `-t ed25519`：使用 Ed25519 算法，密钥更短，安全性足够。
- `-C`：添加注释，便于在 Gitee 上识别这把密钥。
- `-f`：指定输出文件；会生成 `gitee_mirror`（私钥）和 `gitee_mirror.pub`（公钥）。
- `-N ""`：不设置口令。GitHub Actions 无法交互式输入口令，因此 CI 专用密钥通常不设口令。

生成的私钥和公钥：

```text
~/.ssh/gitee_mirror
~/.ssh/gitee_mirror.pub
```

Windows 下路径为 `C:\Users\<用户名>\.ssh\`。可以在 Git Bash 中执行 `ls -l ~/.ssh/gitee_mirror*` 确认文件。

验证私钥没有口令：

```bash
ssh-keygen -y -P "" -f ~/.ssh/gitee_mirror
```

命令会输出对应的公钥；如果命令报错（提示 `incorrect passphrase`），说明生成时设置了口令，需要重新生成，或者改用 `ssh-agent` 方案。

> [!tip] 不要复用个人日常使用的 SSH 密钥。为镜像单独生成一把部署密钥，泄露时只需在 Gitee 删除公钥并轮换。

#### 在 Gitee 添加部署公钥

1. 打开 Gitee 镜像仓库，进入「管理」→「部署公钥」。
2. 点击「添加部署公钥」。
3. 把 `gitee_mirror.pub` 的内容完整粘贴到公钥输入框。
4. 勾选「启用推送」，否则只能读取，不能 push。
5. 保存。

> [!warning]
> 部署公钥是仓库级权限，只对当前仓库生效。不要使用 Gitee 账号级 SSH 密钥，避免一把密钥拥有账号下所有仓库的权限。

测试认证：

```bash
ssh -i ~/.ssh/gitee_mirror -T git@gitee.com
```

如果配置正确，会看到类似 `Welcome to Gitee.com` 的提示；Gitee 不提供 shell 访问，所以不会进入交互式终端。出现 `Permission denied (publickey)` 时，依次检查：

- 公钥是否粘贴完整；
- 是否勾选「启用推送」；
- `-i` 指定的私钥是否与公钥配对；
- 本地是否有多把密钥干扰（可以加 `-o IdentitiesOnly=yes`）。

#### 在 GitHub 配置 Secret

1. 打开 GitHub 仓库，进入「Settings」→「Secrets and variables」→「Actions」。
2. 点击「New repository secret」。
3. 名称填写 `GITEE_SSH_PRIVATE_KEY`。
4. 值填写 `gitee_mirror` 私钥文件的完整内容，包含 `-----BEGIN OPENSSH PRIVATE KEY-----` 和 `-----END OPENSSH PRIVATE KEY-----`。
5. 保存。

> [!warning]
> 私钥只保存在 GitHub Secret 中。不要提交到仓库、不要写进工作流文件，也不要在 Actions 日志中 `echo` 或 `cat` 私钥。

<span style="color: red;">**私钥一旦泄露，应立即在 Gitee 删除对应部署公钥，并更新 GitHub Secret。**</span>

#### 编写镜像工作流

在 GitHub 仓库中新建 `.github/workflows/mirror-gitee.yml`：

```yaml
# 将 main/dev 分支镜像同步到 Gitee（Gitee 端只读）
name: Mirror to Gitee

on:
  workflow_dispatch:
  push:
    branches:
      - main
      - dev

permissions:
  contents: read

concurrency:
  group: gitee-mirror-${{ github.ref }}
  cancel-in-progress: false

jobs:
  mirror:
    name: Push ${{ github.ref_name }} to Gitee
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v5
        with:
          # 必须拉取完整历史，避免推送时出现 shallow update not allowed
          fetch-depth: 0

      - name: Configure SSH
        env:
          GITEE_SSH_PRIVATE_KEY: ${{ secrets.GITEE_SSH_PRIVATE_KEY }}
        run: |
          if [ -z "$GITEE_SSH_PRIVATE_KEY" ]; then
            echo "GITEE_SSH_PRIVATE_KEY is not configured."
            exit 1
          fi

          mkdir -p ~/.ssh
          chmod 700 ~/.ssh
          # 从 Windows 复制时可能带 CRLF，先去掉 \r
          printf '%s\n' "$GITEE_SSH_PRIVATE_KEY" | tr -d '\r' > ~/.ssh/gitee_deploy_key
          chmod 600 ~/.ssh/gitee_deploy_key
          ssh-keyscan gitee.com >> ~/.ssh/known_hosts
          chmod 644 ~/.ssh/known_hosts

          cat > ~/.ssh/config <<'EOF'
          Host gitee.com
            HostName gitee.com
            User git
            IdentityFile ~/.ssh/gitee_deploy_key
            IdentitiesOnly yes
          EOF

      - name: Mirror branch
        run: |
          # 替换为你的 Gitee 仓库地址
          git remote add gitee git@gitee.com:your-gitee-name/your-mirror-repo.git
          if git ls-remote --exit-code --heads gitee "${GITHUB_REF_NAME}" >/dev/null 2>&1; then
            git fetch --no-tags gitee "+refs/heads/${GITHUB_REF_NAME}:refs/remotes/gitee/${GITHUB_REF_NAME}"
          fi
          git push --force-with-lease gitee "HEAD:refs/heads/${GITHUB_REF_NAME}"
```

关键配置说明：

- `on.push.branches`：只监听 `main` 和 `dev`；其他分支不会触发。
- `workflow_dispatch`：支持在 Actions 页面手动运行。
- `permissions.contents: read`：只读取 GitHub 仓库内容，不需要写权限。
- `concurrency`：同一分支的同步串行执行，避免多个工作流运行同时向 Gitee 推送。
- `fetch-depth: 0`：拉取完整历史，保证 push 包含完整对象。
- `Configure SSH`：把 Secret 写入临时私钥文件，并生成 `known_hosts` 和 SSH 配置。
- `git remote add`：把 Gitee 镜像添加为远程仓库，示例地址使用占位符。
- `git ls-remote`：判断 Gitee 上是否已有对应分支；没有就跳过 fetch，首次 push 会创建分支。
- `git push --force-with-lease`：把当前提交强制同步到 Gitee 同名分支。

> [!note] `${{ ... }}` 是 GitHub Actions 表达式，`${GITHUB_REF_NAME}` 是 runner 的环境变量，两者不要混淆。

> [!tip]
> 如果仓库使用 Git LFS，需要在 `Checkout` 步骤加上 `lfs: true`；如果包含 Git submodule，需要加上 `submodules: recursive`，否则镜像可能只包含指针或缺少子模块内容。

#### 提交工作流并触发同步

只提交工作流文件，不要提交私钥或本地临时文件。

> [!info]
> 推送前先完成「在 Gitee 添加部署公钥」和「在 GitHub 配置 Secret」两步配置，否则首次运行会在 SSH 认证阶段失败。

```bash
git add .github/workflows/mirror-gitee.yml
git commit -m "ci: 同步 main 和 dev 到 Gitee 镜像"
git push origin dev
```

推送 `dev` 后，工作流会立即触发一次，把 `dev` 分支同步到 Gitee。

要让 `main` 也自动同步，需要把工作流文件带到 `main` 分支：

```bash
git checkout main
git cherry-pick <工作流提交的 commit id>
git push origin main
```

> [!warning]
> <span style="color: red;">**工作流文件必须存在于对应分支，push 触发才会生效。**</span>如果只在 `dev` 上添加了文件，之后 push `main` 不会触发同步。

手动触发（`workflow_dispatch`）需要在默认分支上存在该工作流文件。默认分支通常是 `main`，因此建议先把工作流合并到 `main`，再在 Actions 页面点击「Run workflow」。

首次同步时：

- 如果 Gitee 分支是 GitHub 分支的祖先，push 会快进，不会产生额外合并提交。
- 如果 Gitee 分支有完全不同的历史，<span style="color: red;">**`--force-with-lease` 会覆盖它**</span>，操作前务必确认 Gitee 端没有需要保留的内容。

#### 验证同步结果

1. 打开 GitHub Actions，确认 `Mirror to Gitee` 运行成功。
2. 查看 `Mirror branch` 步骤日志，确认 push 输出包含目标分支。
3. 对比两边的分支提交号，确认同一个分支的 commit id 完全一致。
4. 打开 Gitee 仓库的提交记录，确认最新提交和提交信息与 GitHub 一致。
5. 做一次小改动验证自动化：在 `dev` 提交并 push，等待工作流完成，再次对比 `dev` 的 commit id。
6. 验证 `main`：把工作流合并到 `main` 后，push 一次 `main`，确认 Gitee `main` 更新。

对比命令：

```bash
# GitHub 分支
git ls-remote https://github.com/your-github-name/your-repo.git refs/heads/main refs/heads/dev

# Gitee 镜像分支
git ls-remote https://gitee.com/your-gitee-name/your-mirror-repo.git refs/heads/main refs/heads/dev
```

同一个分支在两边的 commit id 应该完全一致。

> [!note] 上面的 HTTPS 地址适用于公开仓库；如果仓库是私有的，请换成已配置凭据的 SSH 地址或带认证的 HTTPS 地址。

验证结果可以用下表快速核对：

| 检查项 | 预期结果 |
| :---: | --- |
| Actions 运行状态 | 成功（绿色） |
| Gitee `dev` commit id | 与 GitHub `dev` 一致 |
| Gitee `main` commit id | 与 GitHub `main` 一致 |
| Gitee 提交记录 | 能看到 GitHub 的最新提交信息 |
| 日志中的私钥 | 不出现明文 |

### 密钥轮换

需要更换 SSH 密钥时，按以下顺序操作，避免同步中断：

1. 在本地生成新的密钥对。
2. 把新公钥添加到 Gitee 部署公钥，暂时保留旧公钥。
3. 把 GitHub Secret `GITEE_SSH_PRIVATE_KEY` 更新为新私钥。
4. 手动触发一次同步（或 push 一次），确认新密钥可用。
5. 确认无误后，在 Gitee 删除旧公钥。
6. 如果本地也使用同一把密钥操作 Gitee，同步更新本地 `~/.ssh/config` 中的 `IdentityFile`。

> [!warning] 只更新 Gitee 公钥而不更新 GitHub Secret，或只更新 Secret 而不添加新公钥，都会导致 `Permission denied (publickey)`。

### 常见问题

| 现象 | 可能原因 | 处理方式 |
| :---: | --- | --- |
| `Permission denied (publickey)` | 公钥未添加、未启用推送、Secret 与公钥不配对 | 检查 Gitee 部署公钥和 GitHub Secret |
| `GITEE_SSH_PRIVATE_KEY is not configured` | Secret 名称拼错或未在当前仓库配置 | 按 `GITEE_SSH_PRIVATE_KEY` 重新添加 |
| `Host key verification failed` | `known_hosts` 生成失败或网络异常 | 检查 `ssh-keyscan gitee.com` 是否成功；重跑工作流 |
| `shallow update not allowed` | `fetch-depth` 不是 `0` | 改为 `fetch-depth: 0` |
| 工作流没有触发 | 文件不在当前分支、Actions 被禁用、分支名不匹配 | 确认分支与 `on.push.branches` |
| `protected branch` 或 push 被拒绝 | Gitee 分支保护规则阻止强制推送 | 调整保护规则或改用允许强推的分支 |
| `stale info` | fetch 后远程分支又被更新 | 重新运行工作流 |
| 工作流成功但 Gitee 没变化 | 远程地址写错、分支名不一致、push 显示 `up-to-date` | 检查 `git remote`、分支名和日志 |
| 运行时间过长或超时 | 仓库历史大，完整 clone 耗时 | 评估 Gitee 镜像方案或优化仓库历史 |
| 私钥疑似泄露 | 密钥被提交或打印到日志 | 立即删除 Gitee 公钥、更新 Secret，并检查仓库历史 |

### 注意事项与安全建议

- **Gitee 只读**：所有修改在 GitHub 完成，Gitee 端不要直接提交，否则会被下一次强制同步覆盖。
- **最小权限**：使用仓库级部署公钥，不要用 Gitee 账号级 SSH 密钥；工作流只申请 `contents: read`。
- **私钥保护**：私钥只放在 GitHub Secret；不要写入仓库、日志、Issue 或聊天记录。
- **不要嵌入 PAT**：不要把 GitHub PAT 或 Gitee 密码拼进远程 URL。`git remote -v` 会明文显示 URL 中的凭据；优先使用 SSH 或系统凭据管理器。
- **force-with-lease 的边界**：它能防止 fetch 与 push 之间的并发覆盖，但不能防止 Gitee 上早已存在的独立提交被覆盖；需要保护时加祖先检查。
- **分支范围**：示例只同步 `main` 和 `dev`；标签和其他分支不会自动同步，需要时扩展 `branches` 或增加额外步骤。
- **平台数据不同步**：Issues、Pull Requests、Wiki、Releases、Actions Secrets 等不会镜像；Gitee Pages 也不在同步范围内。
- **容量限制**：镜像会携带完整历史，同步前确认 Gitee 的单文件大小和仓库容量限制，避免推送被拒绝。
- **全量拉取成本**：`fetch-depth: 0` 每次都会拉取完整历史，仓库较大时运行时间会明显增加；这是保证 push 成功的必要代价。
- **密钥轮换**：更换密钥时同时更新 Gitee 公钥和 GitHub Secret，先加新、再验证、最后删旧。

### 小结

使用 GitHub Actions 把 GitHub 分支推送到 Gitee，核心只有三件事：

1. 用 SSH 部署公钥给 Actions 提供最小化的 Gitee 推送权限。
2. 用 GitHub Secret 保存私钥，并在工作流中临时还原成 SSH 配置。
3. 用 `fetch-depth: 0` + `git push --force-with-lease` 把指定分支同步到 Gitee。

<span style="color: purple;">**最终效果：GitHub 保持唯一可信源，Gitee 作为只读镜像自动跟随；配置、历史和权限都可以在仓库与平台设置中审计。**</span>
