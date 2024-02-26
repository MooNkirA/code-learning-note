## 1. Hexo 简介

Hexo 是一个快速、简洁且高效的博客框架，利用 Markdown 即可自动生成静态网页。

1. 超快速度
    - Node.js 所带来的超快生成速度，让上百个页面在几秒内瞬间完成渲染。
2. 支持 Markdown
    - Hexo 支持 GitHub Flavored Markdown 的所有功能，甚至可以整合 Octopress 的大多数插件。
3. 一键部署
    - 只需一条指令即可部署到 GitHub Pages, Heroku 或其他网站。
4. 丰富的插件
    - Hexo 拥有强大的插件系统，安装插件可以让 Hexo 支持 Jade, CoffeeScript。

相关网址：

- 官网：https://hexo.io/
- 主题选择：https://hexo.io/themes/
- 使用 hexo 部署到github：https://levblanc.github.io/2015/07/13/building-github-pages-blog-with-hexo/
- hexo 部署到 github：https://www.jianshu.com/p/9ab44093364c

## 2. Hexo 安装

1. 安装

```bash
npm install hexo-cli -g
```

2. 初始化

```bash
hexo init blog
cd blog
npm install
```

3. 启动

```bash
hexo server
# 或者简写：
hexo s
```

4. 访问

打开`localhost:4000`即可打开默认的博客主页

## 3. Hexo 项目结构文件介绍

```
$ ll
total 125
-rw-r--r-- 1 R 1049089  1846 十二 18 14:12 _config.yml
-rw-r--r-- 1 R 1049089 21622 十二 18 14:15 db.json
drwxr-xr-x 1 R 1049089     0 十二 18 14:15 node_modules/
-rw-r--r-- 1 R 1049089   447 十二 18 14:15 package.json
drwxr-xr-x 1 R 1049089     0 十二 18 14:12 scaffolds/
drwxr-xr-x 1 R 1049089     0 十二 18 14:12 source/
drwxr-xr-x 1 R 1049089     0 十二 18 14:12 themes/
```

- `_config.yml`：博客主要的配置文件；
- `node_modules`：NodeJs的依赖模块；
- `scaffolds`：提供了三种不同的文章模版；
- `source`：博客文章默认所在的目录；
- `themes`：博客主题目录，默认：landscape；

## 4. 使用说明
### 4.1. 创建文章

创建文章。输入以下命令。这样默认为 post 文章，或者直接复制草稿模板至 source/_posts 目录

```bash
hexo new "My New Post"
```

注：

1. post是默认的layout，title是新post的md文件名和url对应地址。
2. title不要带空格，如果有空格，创建文件的时候文件名只会是最后一个空格后面的文字。它将会用在该post的url中，而且如果`_config.yml`中的post_asset_folder true（是否生成与每篇文章对应的资源文件夹，默认值为false），资源文件夹也会直接使用title值，所以建议最好使用英文来创建。真正的文章题目可以在md文件中修改。
3. post创建成功后，在 `/source/_posts` 文件夹下，会生成一个新的markdown文件，文件名就是你刚刚的title。

#### 4.1.1. 添加分类及标签

参考网址：https://www.jianshu.com/p/e17711e44e00

### 4.2. 生成静态网页

```bash
hexo generate
# 或者使用简写：
hexo g
```

这样就能在博客目录生成一个 public 目录。

```
$ ll
total 12
drwxr-xr-x 1 R 1049089    0 十二 18 14:40 2018/
drwxr-xr-x 1 R 1049089    0 十二 18 14:40 archives/
drwxr-xr-x 1 R 1049089    0 十二 18 14:40 css/
drwxr-xr-x 1 R 1049089    0 十二 18 14:40 fancybox/
-rw-r--r-- 1 R 1049089 6660 十二 18 14:40 index.html
drwxr-xr-x 1 R 1049089    0 十二 18 14:40 js/
```

如需要重新生成可以执行一下清理命令

```bash
hexo clean
```

### 4.3. 发布到服务器

如果使用了 Github 的 Pages 托管服务，那么需要配置 _config.yml 中的如下配置

```yml
deploy:
  type: git
  repo:
    - git@github.com:MooNkirA/MooNkirA.github.io.git
  branch: master
```

然后使用发布命令

```bash
hexo deploy
```

如果使用云服务器，将静态服务器的主目录设置为 public 这个目录即可

### 4.4. 切换主题

- 在 Hexo 的主题网站下载一个自己的主题，如选择主题是`icarus`。下载之后把它扔到`themes`目录，然后修改根目录的配置文件`_config.yml`中的默认主题即可。
- 目前选择的主题：
    - https://github.com/fan-lv/Fan（暂时无法使用）
    - https://github.com/AlynxZhou/hexo-theme-aria/blob/master/README.zh_CN.md

```bash
theme: Fan
```

也可以进入主题目录对这个主题进行自定义修改。

## 5. 问题
### 5.1. 在执行 `hexo deploy` 后,出现 `error deployer not found:github` 的错误

尝试使用hexo搭建github pages，本地调试没什么问题，但是最后部署时报“error deployer not found:github”的错误，到网上原因，发现是hexo的版本升级导致的。

**解决方案**

- 步骤一

安装依赖插件

```bash
npm install hexo-deployer-git --save
```
- 步骤二

修改_config.yml，deploy的type由github改为git

```yml
deploy:
  type: git
  repository: git@github.com:fengdroid/fengdroid.github.io.git
  branch: master
```

- 步骤三

重新部署`hexo deploy`

### 5.2. hexo引用本地图片无法显示

https://blog.csdn.net/xjm850552586/article/details/84101345

## 6. HEXO常用命令
### 6.1. 单条指令

```bash
hexo new "postName" # 新建文章
hexo new page "pageName" # 新建页面
hexo clean # 清除之前 public 文件夹的内容
hexo generate # 生成静态页面至public目录
hexo deploy # 将.deploy目录部署到GitHub
hexo server # 开启预览访问端口（默认端口4000，'ctrl + c'关闭server）
hexo help # 查看帮助
hexo version # 查看HEXO的版本
```

### 6.2. 简写指令

```bash
hexo n #等于hexo new
hexo g #等于hexo generate
hexo d #等于hexo deploy
hexo s #等于hexo server
hexo v #等于hexo version
```

### 6.3. 复合指令

```bash
hexo deploy -g #生成加部署
hexo server -g #生成加预览
hexo d --g 或者 hexo g --d #生成加部署
```
