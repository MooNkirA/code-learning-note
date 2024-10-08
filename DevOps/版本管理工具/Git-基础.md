## 1. Git 概述

### 1.1. Git 简介与特点

Git是分布式的版本控制工具，其特点如下：

- 速度快
- 简单的设计
- 对非线性开发模式的强力支持（允许上千个并行开发的分支）
- 完全分布式
- 有能力高效管理类似 Linux 内核一样的超大规模项目（速度快和数据多）

### 1.2. Git 与 SVN 对比

#### 1.2.1. SVN

SVN是<font color=red>**集中式版本控制软件**</font>，版本库是集中放在中央服务器的。每个程序员使用自己电脑，首先要从中央服务器哪里得到最新的版本，编程完将完成的代码推送到中央服务器。集中式版本控制系统是必须联网才能工作

标准的集中式版本控制工具管理方式：

![](images/20201105231923850_1529.png)

集中管理方式在一定程度上看到其他开发人员在干什么，而管理员也可以很轻松掌握每个人的开发权限。但是相较于其优点而言，集中式版本控制工具缺点很明显：

1. 服务器单点故障，一旦宕机无法提交代码，即容错性较差
3. 离线无法提交代码，无法及时记录开发人员的提交行为

<font color=red>每天都同步一次即可，需要手动同步上传代码</font>

#### 1.2.2. Git

Git是<font color=red>**分布式版本控制软件**</font>，它可以没有中央服务器，每个人的电脑就是一个完整的版本库。工作的时候就不需要联网了，因为版本都是在自己的电脑上。当多人开发时，只需把各自的修改推送到远程仓库，就可以所有成员都可以看到修改了。

分布式版本控制工具管理方式：

![](images/20201105232113544_767.png)

#### 1.2.3. 总结

1. svn 是集中式版本控制工具，git 是分布式版本控制工具
2. svn 不支持离线提交，git 支持离线提交代码

### 1.3. Git 工作流程

1. 从远程仓库中克隆 Git 资源作为本地仓库。
2. 从本地仓库中checkout代码然后进行代码修改
3. 在提交前先将代码提交到暂存区。
4. 提交修改。提交到本地仓库。本地仓库中保存修改的各个历史版本。
5. 在修改完成后，需要和团队成员共享代码时，可以将代码push到远程仓库。

Git 的工作流程图

![](images/20201105232233988_19200.jpg)

#### 1.3.1. 代码提交和同步代码

![](images/99655608240247.png)

#### 1.3.2. 代码撤销和撤销同步

![](images/395145608258673.png)

### 1.4. Git 相关概念

**相关名词**

- 本地仓库：是在开发人员自己电脑上的Git仓库，存放开发者的代码(.git 隐藏文件夹就是本地仓库)
- 远程仓库：是在远程服务器上的Git仓库，存放代码(可以是github.com或者gitee.com 等开源代码仓库，或者自家公司的服务器)
- 工作区: 本地编写代码(文档)的地方
- 暂存区: 在本地仓库中的一个特殊的文件(index) 叫做暂存区，临时存储开发者即将要提交的文件

![](images/20211226103105701_26936.png)

**相关操作**

- `Clone`：克隆，就是将远程仓库复制到本地仓库
- `Push`：推送，就是将本地仓库代码上传到远程仓库
- `Pull`：拉取，就是将远程仓库代码下载到本地仓库，并将代码克隆到本地工作区

## 2. Git 安装

> 此部分内容详见[《Git 笔记 - 安装篇》](/DevOps/版本管理工具/Git-安装)

## 3. Git 本地仓库

### 3.1. 创建版本库

什么是版本库呢？版本库又名仓库，英文名 repository，可以简单理解成一个目录，这个目录里面的所有文件都可以被 Git 管理起来，每个文件的修改、删除，Git 都能跟踪，以便任何时刻都可以追踪历史，或者在将来某个时刻可以“还原”。由于 git 是分布式版本管理工具，所以 git 在不需要联网的情况下也具有完整的版本管理能力。

创建一个版本库非常简单，可以使用 git bash 命令窗口也可以使用其他图形化客户端。

1. 创建一个空目录（如 `D:\git_repo`）
2. 在当前目录中点击右键中选择【Git Bash Here】来启动

![](images/450735316249062.png)

3. 输入 `git init` 命令创建仓库

![](images/554305516244198.png)

### 3.2. 仓库内容介绍

初始化的仓库，会生成一个 `.git` 的隐藏目录，里面相关 git 的配置、钩子、记录等等信息

![](images/327885816237744.png)

- 版本库：`.git` 目录就是版本库，提交的文件都需要保存到版本库中。
- 工作目录：包含 `.git` 文件夹的目录，也就是 `.git` 目录的上一级目录就是工作目录。只有工作目录中的文件才能保存到版本库中。

> Notes: 一个git版本库一般只存放一个项目（一个项目对应一个文件夹）。

### 3.3. 工作区和暂存区

Git 和其它版本控制软件如 SVN 的一个不同之处就是有<font color=red>**暂存区**</font>的概念。

#### 3.3.1. 工作区 (Working Directory)

工作区就是在本地电脑里能看到的 git 目录，例如上例的 git_repo 文件夹其实就是一个工作区，在这个目录中的 `.git` 隐藏文件夹才是版本库。

#### 3.3.2. 暂存区 (stage)

Git 的版本库里存了很多东西，其中最重要的就是称为 stage（或者叫 index）的暂存区，还有 Git 自动创建的第一个分支 master，以及指向 master 的一个指针叫 HEAD，HEAD 用来指向当前的分支。

![](images/27921311263006.jpg)

把文件往 Git 版本库里添加的时候，是分两步执行的：

- 第一步是用 `git add` 命令把文件添加进去，实际上就是把文件修改添加到暂存区；
- 第二步是用 `git commit` 命令提交更改，实际上就是把暂存区的所有内容提交到当前分支。

因为在创建 Git 版本库时，Git 自动创建了唯一一个 master 分支，所以现在 `git commit` 就是往 master 分支上提交更改。可以简单理解为，需要提交的文件修改通通放到暂存区，然后一次性提交暂存区的所有修改。

### 3.4. Git 仓库的特殊文件

#### 3.4.1. .gitkeep 文件

`.gitkeep`文件是用来保证当前目录即使为空，也会上传到远程仓库（如 github）上

#### 3.4.2. .gitignore 忽略规则文件（待整理）

`.gitignore` 的文件用于声明忽略文件或不忽略文件的规则，**规则对当前目录及其子目录生效**。

> 注意：该文件因为没有文件名，没办法直接在windows目录下直接创建（*win10系统后来一些版本可以直接创建没有文件名的文件*），可以通过命令行 Git Bash 来 `touch` 指令来创建。

##### 3.4.2.1. 忽略文件语法规范

- 忽略所有 `.a` 的文件

```
*.a
```

- 否定忽略 `lib.a`，尽管已经在前面忽略了所有 `.a` 文件

```
!lib.a
```

- 忽略 `build/` 文件夹下的所有文件

```
/build/
```

- 忽略指定目录下的所有 `.txt` 文件（不包含其子目录）。例如：`/doc/notes.txt`，但不包括 `doc/server/arch.txt`

```
/doc/*.txt
```

- 忽略所有在 doc 目录及其子目录的 `.pdf` 文件

```
doc/**/*.pdf
```

##### 3.4.2.2. Gitignore 参考模板

初用 Git 的工程师，都有着一个苦恼，每次都得针对不同项目、不同语言类型来重复写 .gitignore，以忽略一些无需纳入 Git 管理的文件。[Gitignore](https://github.com/github/gitignore)项目就是帮工程师解决这个问题的。每次需要为项目创建 .gitignore 文件时，只需要打开这个项目，针对你当前所用编程语言或框架，去寻找对应 .gitignore 模板替换即可。

如果觉得挨个模板查阅很费劲，推荐一个网站：[gitignore.io](https://www.toptal.com/developers/gitignore)，支持一键搜索你所需的 gitignore 模板。

![](images/38921110220966.png)

#### 3.4.3. .gitconfig 配置文件

在 OS X 和 Linux 下，Git 的配置文件储存在 `~/.gitconfig`。在 windows 系统中，存储在`%HOMEPATH%\.gitconfig`

## 4. Git 远程仓库

在本地创建了一个 Git 仓库，又想让其他人来协作开发，此时就可以把本地仓库同步到远程仓库，同时还增加了本地仓库的一个备份。

市面上常用的远程仓库有 github、gitee、gitlab 等等

> Notes: 此部分的内容详见[《GitHub 笔记》](/DevOps/版本管理工具/GitHub)

## 5. 分支 (branch)（整理中）

### 5.1. 分支的概念

几乎所有的版本控制系统都以某种形式支持分支。使用分支意味着可以把开发者本地工作区从开发主线上分离开来，避免影响开发主线。多线程开发，可以同时开启多个任务的开发，多个任务之间互不影响。

### 5.2. 创建分支

> TODO: 整理中

## 6. 标签 (tag)（整理中）

### 6.1. 标签的概念

如果项目达到一个重要的阶段，并希望永远记住那个特别的提交快照，可以给它打上标签(tag)

比如说，想为项目发布一个"1.0"版本。可以给最新一次提交打上（HEAD）"v1.0"的标签。标签可以理解为项目里程碑的一个标记，一旦打上了这个标记则，表示当前的代码将不允许提交

### 6.2. 标签的创建（图形化界面操作）

标签的创建和分支的创建操作几乎一样

### 6.3. 标签的切换与删除

> TODO: 整理中

## 7. Git 命令

掌握并熟练使用了命令行模式操作 git 的话，会发现某些操作命令行比窗口化操作要简单。一般来说，日常使用 git 只要记住下图6个命令，就可以了。

![](images/518713620239669.png)

> Notes: <font color=red>以下章节的命令中，`[]` 包裹的代表可以省略</font>

### 7.1. 常用命令速查表

![](images/3660922239387.jpg)

### 7.2. config - 配置命令

Git 的设置文件为 `.gitconfig`，它可以在用户主目录下（全局配置），也可以在项目目录下（项目配置）。在安装好后首次使用前，需要先进行全局配置。通过相关的命令设置全局信息会保存在 `~/.gitconfig` 文件中。

#### 7.2.1. 配置全局的用户信息

在空白位置打开右键菜单，点击【Git Bash Here】，打开Git命令行窗口。输入以下命令配置提交时全局的用户名与邮箱

```bash
$ git config --global user.name "用户名"
$ git config --global user.email "邮箱地址"
```

> Tips: `--global` 可以省略

#### 7.2.2. 编辑 Git 配置文件

```bash
$ git config -e [--global]
```

> Notes: `--global` 表示编辑全局配置，省略则表示编辑当前仓库的配置。

#### 7.2.3. 查看配置信息

```bash
# 查看当前（全局） git 全部的配置信息
$ git config [--global] --list

# 查看当前（全局） git 指定的配置信息
$ git config [--global] user.name
```

#### 7.2.4. 设置 Git 短命令

设置短命令可以很好的提高效率。有以下两种设置短命令的方式：

- 方式一：使用命令配置

```bash
git config --global alias.ps push
```

- 方式二：通过 .gitconfig 全局配置文件设置

```bash
# 打开全局配置文件
vim ~/.gitconfig
```

写入内容，例如：

```properties
[alias] 
    co = checkout
    ps = push
    pl = pull
    mer = merge --no-ff
    cp = cherry-pick
```

通过过以上配置后，使用效果如下：

```bash
# 等同于 git cherry-pick <commitHash>
git cp <commitHash>
```

### 7.3. 基础操作命令

#### 7.3.1. init - 本地仓库初始化

执行以下命令，初始化 git 仓库，即由 git 来管理此目录

```bash
# 在空的目录中
$ git init

# 新建一个目录，将其初始化为 Git 代码库
$ git init [project-name]

# 初始化仓库不带工作区
$ git init --bare
```

> 执行之后会在项目目录下创建【`.git`】的隐藏目录，这个目录是Git所创建的，不能删除，也不能随意更改其中的内容。

#### 7.3.2. add - 添加文件到缓存区

`git add` 指令，用于添加单个（多个）文件到缓存区。

添加单个文件

```bash
$ git add 文件名
```

同时添加多个文件

```bash
$ git add 文件名1 文件名2 文件名3 …
```

添加指定目录到暂存区，包括子目录

```bash
$ git add 目录名称
```

添加当前目录中所有文件到缓存区。

```bash
$ git add .
```

添加每个变化前，都会要求确认。对于同一个文件的多处变化，可以实现分次提交

```bash
$ git add -p
```

#### 7.3.3. reset - 取消缓存区的文件

将暂存区的文件取消暂存指定的文件(取消 `add`)

```bash
$ git reset 文件名
```

#### 7.3.4. rm - 删除本地工作区文件

删除工作区文件，并且将这次删除放入暂存区

```bash
$ git rm <文件名1> <文件名2>...
```

停止追踪指定文件，但该文件会保留在工作区

```bash
$ git rm --cached <文件名>
```

#### 7.3.5. mv - 修改文件名

修改文件名称，并且将这个改名后文件放入暂存区

```bash
$ git mv <file-original> <file-renamed>
```

#### 7.3.6. commit - 提交至版本库

`git commit` 指令，将当前缓存区的内容提交到版本库。

- `-m` 参数用于指定本次提交的注释。**注意：注释的内容必须使用`""`英文双引号包裹**
- `<文件名>` 非必需，可以指定提交某个文件

```bash
# 提交指定的文件到与注释信息到版本库
$ git commit -m "注释内容" <文件名>

# 提交暂存区的指定文件到版本库
$ git commit [file1] [file2] ... -m [message]

# 提交工作区自上次commit之后的变化，直接到版本库
$ git commit -a

# 提交时显示所有diff信息
$ git commit -v

# 使用一次新的commit，替代上一次提交。如果代码没有任何新变化，则用来改写上一次commit的提交信息
$ git commit --amend -m [message]

# 重做上一次commit，并包括指定文件的新变化
$ git commit --amend [file1] [file2] ...
```

### 7.4. 查看信息命令

#### 7.4.1. status - 查看仓库当前状态

用于查询当前仓库中有那些文件存在变动

```bash
# 查看状态
$ git status
# 查看状态 使输出信息更加简洁
git status –s
```

#### 7.4.2. log - 查看版本（提交）记录

`git log` 指令，用于查看当前分支 git 的提交记录（版本）

```bash
# 显示当前分支的版本历史
$ git log
```

参数 `--pretty`，用于指定显示的格式。

```bash
# 将每个 git 的提交记录在一行显示
$ git log --pretty=oneline

# 指定其他显示格式
$ git log [tag] HEAD --pretty=format:%s
```

其他使用方式

```bash
# 显示commit历史，以及每次commit发生变更的文件
$ git log --stat

# 搜索提交历史，根据关键词
$ git log -S [keyword]

# 显示某个commit之后的所有变动，其"提交说明"必须符合搜索条件
$ git log [tag] HEAD --grep feature

# 显示某个文件的版本历史，包括文件改名
$ git log --follow [file]
$ git whatchanged [file]

# 控制显示条数。例如：显示过去5次提交
$ git log -5 --pretty=oneline
```

图形化展示查看当前分支的版本演变信息

```bash
git log --graph
```

按作者名字查看提交记录

```bash
git log --author="MooN"
```

按日期查看

```bash
git log --after="2023-2-1"
```

搜索过滤合并提交

```bash
git log --no-merges 
# 或者
git log --merges
```

按提交信息。例如，团队规范要求在提交信息中包括相关的 issue 编号，即可以使用如下命令来显示这个 issue 相关的所有提交。

```bash
git log --grep="JRA-224:"
```

#### 7.4.3. reflog - 查看历史版本

如果发生版本回退的操作，此时使用`git log`就无法查询回退到此版本之前的操作记录，所以需要使用 `git reflog` 指令来查看历史操作(包括所有分支的commit，以及已经被删除的commit)，获取最新的 commit id

```bash
$ git reflog
```

#### 7.4.4. 显示修改信息

```bash
# 显示所有提交过的用户，按提交次数排序
$ git shortlog -sn

# 显示指定文件是什么人在什么时间修改过
$ git blame [file]

# 显示某次提交的元数据和内容变化
$ git show [commit]

# 显示某次提交发生变化的文件
$ git show --name-only [commit]

# 显示某次提交时，某个文件的内容
$ git show [commit]:[filename]
```

#### 7.4.5. diff - 查看文件差异

```bash
# 显示指定文件相关的每一次diff
$ git log -p [file]

# 显示暂存区和工作区的差异
$ git diff

# 显示暂存区和上一个commit的差异
$ git diff --cached [file]

# 显示工作区与当前分支最新commit之间的差异
$ git diff HEAD

# 显示两次提交之间的差异
$ git diff [first-branch]...[second-branch]

# 显示今天修改的代码行数
$ git diff --shortstat "@{0 day ago}"
```

### 7.5. 远程仓库操作命令

#### 7.5.1. clone - 克隆远程仓库到本地

`clone` 指令用于克隆远程仓库到本地

```bash
$ git clone 远程Git仓库地址
```

> 注：远程仓库分两种，基于http/https协议或者基于ssh协议

#### 7.5.2. remote - 查看远程仓库

- `remote` 指令用于查看远程，列出指定的每一个远程服务器的简写

```bash
$ git remote
```

- 查看远程，列出简称和地址

```bash
$ git remote -v
```

- 查看远程仓库详细地址

```bash
$ git remote show <仓库简称>
```

#### 7.5.3. remote add - 添加远程仓库

- 添加远程仓库

```bash
$ git remote add <shortname> <url>
```

#### 7.5.4. remote rm - 移除远程仓库

- 移除远程仓库和本地仓库的关系。

```bash
$ git remote rm <shortname>
```

> Notes: <font color=red>**只是从本地移除远程仓库的关联关系，并不会真正影响到远程仓库**</font>

#### 7.5.5. remote update - 更新远程分支列表

- 更新远程仓库，

```bash
git remote update origin(仓库源名称) --prune
```

#### 7.5.6. 拉取远程仓库版本

##### 7.5.6.1. fetch - 手动拉取与合并

从远程仓库拉取 (拉取到.git 目录，不会合并到工作区，工作区发生变化)

```bash
$ git fetch <remote> <分支名称>
```

手动合并。把某个版本的某个分支合并到当前工作区

```bash
$ git merge <remote>/<分支名称>
```

##### 7.5.6.2. pull - 自动拉取与合并工作区

`pull` 指令用于拉取远程仓库的版本记录。(拉取到 .git 目录，合并到工作区，工作区不发生变化。相当于 `fetch` + `merge`)

```bash
$ git pull <shortname> <分支名称>
```

> 拉取时如果出现版本冲突，需要手动进行冲突的合并

注意：如果当前本地仓库不是从远程仓库克隆，而是本地创建的仓库，并且仓库中存在文件，此时再从远程仓库拉取文件的时候会报错（fatal: refusing to merge unrelated histories），解决此问题可以在 `git pull` 命令后加入参数 `--allow-unrelated-histories`

```bash
# 强制拉取合并
$ git pull <shortname> <分支名称> --allow-unrelated-histories
```

#### 7.5.7. push - 推送到远程仓库

- `push` 指令用于将修改的版本推送到远程仓库某个分支

```bash
$ git push [remote-name] [branch-name]
```

- 基于http/https协议的远程仓库，首次提交时可能会出现403无权限的错误，需要修改【.git/config】文件内容，增加相应远程仓库的有效的用户名与密码

```
# 原配置
[remote "origin"]
	url = https://github.com/用户名/仓库名.git

# 修改为
[remote "origin"]
	url = https://用户名:密码@github.com/用户名/仓库名.git
```

> 注：推送到基于ssh协议的远程仓库，在push的时候并没有提示要求输入帐号密码，因为公私玥已经实现了用户身份鉴权，也不需要修改此配置文件。

- 强行推送当前分支到远程仓库(即使有冲突也推送)

```bash
$ git push [remote] --force
```

- 推送所有分支到远程仓库

```bash
$ git push [remote] --all
```

### 7.6. 版本回退与撤销命令

#### 7.6.1. checkout 恢复工作区

```bash
$ git checkout<文件名>
# 恢复暂存区的指定文件到工作区
$ git checkout head [file]

# 恢复某个commit的指定文件到暂存区和工作区
$ git checkout [commit] [file]

# 恢复暂存区的所有文件到工作区
$ git checkout .
```

#### 7.6.2. reset 版本回退

`git reset` 指令用于版本的回退

```bash
# 重置暂存区的指定文件，与上一次commit保持一致，但工作区不变
$ git reset [file]

# 重置暂存区与工作区，与上一次commit保持一致
$ git reset --hard

# 重置当前分支的指针为指定commit，同时重置暂存区，但工作区不变
$ git reset [commit]

# 重置当前分支的HEAD为指定commit，同时重置暂存区和工作区，与指定commit一致
$ git reset --hard [commit]

# 重置当前HEAD为指定commit，但保持暂存区和工作区不变
$ git reset --keep [commit]
```

> Notes: 
>
> - `commit` 参数是指“提交编号”，可以通过 `git log` 指令查看
> - 在使用回退指令的时候 commit id 可以不用写全，git 会自动识别，但是至少需要写前 4 位字符

HEAD 说明：

- `HEAD` 表示当前版本
- `HEAD^` 上一个版本
- `HEAD^^` 上上一个版本
- `HEAD^^^` 上上上一个版本

以此类推...

- `HEAD~0` 表示当前版本
- `HEAD~1` 上一个版本
- `HEAD^2` 上上一个版本
- `HEAD^3` 上上上一个版本

以此类推...

#### 7.6.3. revert

新建一个 commit，用来撤销指定 commit。后者的所有变化都将被前者抵消，并且应用到当前分支

```bash
$ git revert [commit]
```

#### 7.6.4. stash

暂时将未提交的变化移除，稍后再移入

```bash
$ git stash
$ git stash pop
```

### 7.7. branch - 分支管理命令

默认分支名称为 master。*2020年Github开始修改为 main*

#### 7.7.1. 查看分支

- 查看分支基础语法

```bash
$ git branch
```

> 注意：当前的分支前面有个标记“`*`”。

列出所有远程分支

```bash
$ git branch -r
```

列出所有本地分支和远程分支

```bash
$ git branch -a
```

#### 7.7.2. 创建分支

新建一个分支，但依然停留在当前分支

```bash
$ git branch <分支名>
```

新建一个分支，并切换到该分支

```bash
$ git checkout -b <分支名>
```

新建一个分支，指向指定 commit

```bash
$ git branch [branch] [commit]
```

新建一个分支，与指定的远程分支建立追踪关系

```bash
$ git branch --track [branch] [remote-branch]
```

#### 7.7.3. checkout 切换分支

使用 `checkout` 命令切换分支

```bash
# 切换到指定分支，并更新工作区
$ git checkout <分支名>

# 切换到上一个分支
$ git checkout -
```

#### 7.7.4. 分支追踪

在现有分支与指定的远程分支之间建立追踪关系。

```bash
$ git branch --set-upstream [branch] [remote-branch]
```

#### 7.7.5. 删除分支

- 删除本地仓库的分支

```bash
$ git branch -d <分支名>
```

> Notes: **在删除分支的时候，一定要先退出要删除的分支（或切换到其他分支），然后才能删除该分支。或者如果分支已经修改过，则不允许删除。**

- 强制删除分支（**慎用**）

```bash
$ git branch -D <分支名>
```

- 删除远程仓库分支

```bash
# 简写
$ git push origin –d 分支名称
# 全写
$ git push origin --delete [branch-name]
# 直接根据远程分支的全称来删除
$ git branch -dr [remote/branch]
```

#### 7.7.6. merge 合并分支

合并指定分支到当前分支。合并分支前需要先使用 `checkout` 切换到待合并到的分支，然后再使用 `merge` 指令指定要合并的分支。

```bash
$ git merge <被合并的分支名>
```

> Notes: <font color=red>**合并本地所有分支之后，需要使用 `push` 指令推送到远程仓库中**</font>

选择一个 commit，合并进当前分支

```bash
$ git cherry-pick [commit]
```

#### 7.7.7. push 提交分支至远程仓库

```bash
$ git push <仓库简称> <分支名称>
```

#### 7.7.8. Merge 还是 rebase

merge 与 rebase 是两种『合并』的方式。

`merge` 是普通的合并，和传统的 VCS 一样，它会把一个分支合并到目标分支，在顶上建立一个 commit 用来合并，两个分支里已有的 commit 不会有变化。

![](images/381716222255566.gif)

`rebase` 是会从分支分出来的地方切开，嫁接到目标分支的顶端上。（感觉 rebase 应该翻译成嫁接，而不是“变基”。）

![](images/461432744370687.gif)

还有一种叫 squash，也就是选择一个分支里的一些 commit，压扁成一个 commit。这个任何时间都能做，即便不是为了合并也行。*在 TortoiseGit 里，这叫“combine into one commit”*。

![](images/567629795752719.gif)

以上操作组合之后，就得到 4 种操作。但是“squash 再 merge”没有任何意义，所以就剩下“不 squash 只 merge”, “不 squash 只 rebase”，以及“squash 再 rebase”。其实还有一种叫 amend『修订与否』。但这个更多的是发生在 merge 之前的过程。amend（修订），表示当提交的时候，是不是要覆盖掉上一个 commit。打开的话，提交之后还会只有一个 commit，而不是两个。例如：如下是关闭 amend

![](images/315682820834526.gif)

打开 amend

![](images/370972918281685.gif)

最终如何选择使用那种方式？

- 要是处理的是长生命周期的分支，比如团队的 develop 分支、develop 分支、main 分支，合乎逻辑的选择是 merge。因为它们的结构需要保留，而且合并后分支也不打算消失。
- 对于 feature 分支，不同团队可以有不同选择。一个 feature 分支里可以有多个 commits，但它们只有合在一起的时候才会成为一个 feature。中间的 commit 以后就再也用不到了。留着只会浪费空间和时间。所以逻辑上最高效，开销最低的做法是：这些 commit 就需要被 squash，对于 feature 到 develop 的合并来说，rebase 是最佳选择。
- 如果早晚需要把多个 commit 合成一个，那就该用 amend。一路 amend 过去，比最后才来 squash 更好。首先，rebase 一个 commit，会比 rebase 一串来得容易得多，特别是有代码冲突的时候。其次，如果 MR 的最后才 squash & merge，那 commit 的消息就是没有经过 review 的，增加了犯错的风险。

所有这些操作都可以在本地完成。这比在 Web UI 上操作远程的 repo 要容易而且高效。总结起来，这里的最佳实践是：

1. 在开发过程中可以用 commit 或者 amend commit
2. 在发出 MR 的时候 squash 成一个 commit
3. 在 MR 的迭代内持续用 amend commit
4. 在 MR 通过后用 rebase 进行合并

### 7.8. tag - 标签管理命令

#### 7.8.1. 查看 tag 信息

- 列出所有 tag

```bash
$ git tag
```

- 查看 tag 详细信息

```bash
$ git show [tagName]
```

#### 7.8.2. 新建 tag

在当前 commit 中新建一个标签（tag）

```bash
$ git tag [tagName]
```

在指定 commit 中新建一个标签（tag）

```bash
$ git tag [tag] [commit]
```

#### 7.8.3. 新建一个分支指向某个 tag

```bash
$ git checkout -b [branch] [tag]
```

#### 7.8.4. 删除本地 tag

```bash
$ git tag -d [tag]
```

#### 7.8.5. 删除远程 tag

```bash
$ git push origin :refs/tags/[tagName]
```

> Notes: **注意空格**

#### 7.8.6. 提交 tag

提交指定标签（tag）

```bash
$ git push [remote] [tagName]
```

> Notes: `[remote]` 是指远程仓库简称

提交所有标签（tag）

```bash
$ git push [remote] --tags
```

#### 7.8.7. 新建分支指向某个 tag

新建一个分支，并指向某个 tag

```bash
$ git checkout -b <分支名称> <标签名称>
```

### 7.9. 其他命令

#### 7.9.1. archive

生成一个可供发布的压缩包

```bash
$ git archive
```

#### 7.9.2. subtree

Git subtree 是 Git 的一个子命令，可以在一个仓库中嵌入另一个仓库。这对于管理项目的不同部分，或者在多个项目之间共享代码非常有用。

##### 7.9.2.1. 添加一个仓库

要添加一个新的仓库到项目队伍中，需要使用 add 命令。这个命令需要两个参数：一个是想要添加的仓库 URL，另一个是想要将这个仓库添加到的队伍中的位置（目录）。

例如，如果想要将一个名为 library 的仓库添加到队伍中，可以使用以下命令：

```shell
git subtree add --prefix=src/library https://github.com/example/library.git master
```

这个命令会将 library 的 master 分支添加到队伍中的 library 位置。

##### 7.9.2.2. 更新一个仓库

要更新一个仓库，可以使用 pull 命令。这个命令需要和 add 命令相同的参数。

例如，如果想要更新 library 这个仓库，可以使用以下命令：

```shell
git subtree pull --prefix=src/library https://github.com/example/library.git master
```

此命令会从 library 的 master 分支拉取最新的超能力（更改），并将它们合并到队伍中的 library 位置。

##### 7.9.2.3. 将仓库（更改）推送回仓库

如果在项目中发现了一些新的变更（做了一些更改），并且你想要将这些变更推送回原始的仓库，可以使用 push 命令。这个命令需要和 add 命令相同的参数。

例如，如果想要将 library 变更推送回原始的仓库中，可以使用以下命令：

```shell
git subtree push --prefix=src/library https://github.com/example/library.git master
```

这个命令会将在 library 位置发现的变更推送到 library 的 master 分支。

#### 7.9.3. 小结

- Git subtree 就像是一个项目队伍的管理者，它可以帮助更好地管理项目。
- 通过使用 Git subtree，可以在一个项目中嵌入另一个项目，而不需要将它们合并成一个大的仓库。

## 8. Git Bash 操作远程仓库（以 Github 为例）

### 8.1. Github 仓库同步

在仓库所在的目录（例如 D:\git_repo）点击右键选择【Git Bash Here】，启动 git bash 程序。

![](images/60003723249081.jpg)

然后在 git bash 中执行如下命令：

- 添加远程库

```shell
# git remote add  远端名称  远程库ssh地址
git remote add mytest git@github.com:MooNkirA/mytest.git
```

- 同步代码到远程库

```shell
# git push -u 远端名称 分支名称
git push -u mytest master
```

注意：如果之前已经创建过仓库，可能会提示已经存在同名称的远程仓库，直接删除再测试即可。*其中红色字体部分需要替换成个人的用户名*。

![](images/255141508249082.jpg)

- 删除远程库

```shell
# git remote rm 远端名称
git remote rm mytest
```

![](images/531661708236949.jpg)

### 8.2. Github 仓库克隆

克隆远程仓库也就是从远程把仓库复制一份到本地，克隆后会创建一个新的本地仓库。选择一个任意部署仓库的目录，然后克隆远程仓库。

```bash
$ git clone git@github.com:仓库名称/mytest.git
```

![](images/300332008257115.jpg)

### 8.3. Github 拉取代码

使用以下命令可以从 github 上拉取代码

- `git fetch`：相当于是从远程获取最新版本到本地，不会自动 merge（合并代码）
- `git pull`：相当于是从远程拉取最新版本并 merge 到本地

![](images/163533008249784.jpg)

## 9. 版本库 .git 隐藏文件夹

### 9.1. git 的管理文件夹

在工程会有个管理仓库的文件夹 `.git`，里面保存了版本管理的所有数据。

![](images/564494657973022.png)

### 9.2. git 的版本管理基本知识

- commit：一次对本地仓库的提交。这个提交有个唯一识别 id，最长 40 位，但是使用这个 id，只要位数足够可以唯一代表这个 commit，就不一定最长到 40 位。
- branch/tag：一次仓库的副本，这个副本有一串历史 commit，是仓库的另一种快照，tag 是仓库的里程碑。处在分支的代码是安全的，否则不在分支的孤立 commit 可能被 git 当垃圾清理掉
- 文件内容变更：变更保存的是内容的差异值
- 提交历史：每个 commit 的有依赖父子关系，形成了一串提交历史
- 本地/远程：本地对应的是本地仓库，并且维护了一个跟远端的关联关系
- 映射关系：本地仓库和官方远程仓库进行同步，同步的方法：如分支的映射关系、当前提交

### 9.3. git 仓库配置

`git clone xxxx` 克隆一个远端分支会产生一个 `./git/config`，一个最简 config 配置如下：

![](images/406485356872110.png)

#### 9.3.1. [core]分区

core 保存的是与分支无关的配置

- `filemode = true`，忽略文件权限变化带来的 diff
- `repositoryformatversion = 0`，一种配置风格
- `logallrefupdates = true`，记录所有 ref 的更新。ref 是描述当前仓库所有本地分支，每一个文件名对应相应的分支，文件内部存储了当前分支最新的 commit hash 值。因此新建分支，只要往`.git/refs/heads/分支名`写入 commit hash 值即可。
- `core.bare = false`，默认不创建裸仓库，裸仓库是创建的仓库并不包含工作区 ，在裸仓库上执行 Git 命令，而从裸仓库 clone 下来的本地仓库可以进行正常的 push 操作，但是从一般仓库 clone 下来的本地仓库却不行。所以裸仓库一般是作为远端的中心仓库。使用 `git init --bare <repo>` 可以创建一个裸仓库，并且这个仓库是可以被 clone 和 push，裸仓库不包含工作区，所以在裸仓库不能直接提交变更。

#### 9.3.2. [remote]分区、[branch]分区

[remote "origin"] 和 [branch "master"] 指的是本地如何与远程仓库做交互。

![](images/280054709086963.png)

[remote] 的 url 选项指定远程拉取地址，fetch 的字符串格式是 `+源分支:目的分支`。比如说例子的 `fetch = +refs/heads/*:refs/remotes/origin/*`，意思是 refs/heads/目录下的任意分支最新 commit 都关联到 `:refs/remotes/origin/*` 的同名文件。

[branch] 使用 remote 指定 master 分支关联到 `remote/origin分支`。merge 指定了要 merge 的源。

这两个配置跟 git fetch、git pul 命令有关系，这两个命令就是在这个配置找映射关系。比如说：

- `git fetch orign` 会查找 `.git/config` 文件中的 [remote origin] 的配置 url，按照 fetch 规则把最新远端所有的分支的 commit id 更新到`./git/refs/remotes/origin文件夹`中。
- `git merge` 会去找`./git/refs/remotes/origin/某个分支`，合并到 `refs/heads/某个分支`

### 9.4. git 目录结构

#### 9.4.1. refs 文件夹（分支管理）

![](images/123156062873112.png)

在 refs 文件夹存在着 3 个子文件夹，分别是：

- `.git/refs/heads`：本地分支
- `.git/refs/remotes`：远端分支，remotes 文件夹将所有（比如 git remote）命令创建的所有远程分支存储为单独的子目录。在每个子目录中，可以发现被 fetch 进仓库的对应的远程分支。
- `.git/refs/tags`：里程碑分支，描述当前仓库的 tag 信息，其工作原理与 heads 一致。

![](images/374734601202446.png) ![](images/434503271353307.png)

`git branch` 创建一个分支（git branch tmp），这里其实就是在 ref/heads 创建一个文件，这个文件内容是个当前 commit id。ref 是描述当前仓库所有本地分支，每一个文件名对应相应的分支，文件内部存储了当前分支最新的 commit hash 值。因此新建分支，只要往 `.git/refs/heads/分支名` 写入 commit hash 值。

没被关联的分支执行 fetch 会报错。

![](images/360314098528656.png)

#### 9.4.2. objects 文件夹（文件内容管理）

##### 9.4.2.1. object 角色

git 定义的 object 有 3 种角色：commit、tree、blob。角色类型可以用 `git cat-file -t <commit id>` 找到。角色间的关系如下：commit 对应一个 tree，tree 里面可以包含另一个 tree，tree 的叶子节点是一个 blob，blog 代表这个文件的某个版本状态。

![](images/594304366884052.png)

白色的节点是 blob，blob 存储存储的是文件的内容，只要文件内容一样是，不管文件名不一样，blob 一样的，这是 git 的一个设计特点。同一个文件有不同的状态，用 version 区分，tree 表示的 git 当时的一个快照状态。这个状态描述了各个文件的状态。tree 是文件夹，blob 是文件。

![](images/50316608297360.png) ![](images/106862996086047.png)

![](images/302556723674213.png)

##### 9.4.2.2. objects 目录结构

- objects/ folder 存储了对象信息。包括文件内容，提交 id，树，和 tag 里程碑信息。
- objects/[0-9a-f][0-9a-f] folders

存放文件内容的 object，unpacked 或者 loss 对象，这些对象放在 256 个子目录。子目录名的前两个字母是 commit id 的头两个字母，commit id 剩下的字符串作为文件名字。

![](images/374787147904007.png)

#### 9.4.3. hooks 文件夹（脚本设置管理）

钩子函数脚本用于自定义一些 git 命令执行时候触发的脚本。比如 commit、applypatch、push、rebase 被执行会触发相关脚本。要使这些生效，把文件的 sample 后缀去掉。

![](images/47116077265154.png)

#### 9.4.4. logs 文件夹（日志管理）

logs 目录夹存储 refs 文件夹的改变，这些日志包括 commit 关联关系、提交人、提交时间等，执行 `git reflog` 命令可以管理查看这些 log。

- logs/refs/heads/ folder 分支管理
- logs/refs/tags/ folder tag 管理

#### 9.4.5. pack-refs 文件（缓存效能管理）

是 git 效能优化的文件，文件包含索引并且压缩 object，能达到随机访问对象。

branch 和 tag 的变更（统称为 ref）是每个 ref 在目录下的（子）目录中存储一个文件 `$GIT_DIR/refs`。尽管许多分支往往会经常更新，但是大多数 tag 和某些分支从未更新。当存储库具有成百上千个 ref 时，这种“每个引用一个文件”的格式既浪费存储空间，又损害性能。pack 文件夹正是为了解决这个问题而生。

此命令用于通过将 ref 存储在单个文件中来解决存储和性能问题 `$GIT_DIR/packed-refs`。当传统 `$GIT_DIR/refs` 目录层次结构中缺少 ref 时，将在此文件中查找该引用并在找到后使用。

分支的持续 commit 总是在 `$GIT_DIR/refs` 目录层次结构下创建新文件 。有过多 ref 的存储库的做法是将其 ref 打包 --all 一次，并偶尔运行 `git pack-refs`。根据定义，tag 是固定的，并且不会更改。branch 头将带有首字母 `pack-refs --all`，但只有当前分支 head 将被解包，而下一个 pack-refs（不带--all）将使它们解包。

#### 9.4.6. objects/info 文件夹

存储额外扩展信息。info/exclude 用于配置本地分支的 例外文件夹，该文件夹不会被 git 仓库管理。和 gitignore 的区别在于，exclude 只在本地分支生效，不会被同步到远端的 repo。

#### 9.4.7. index 文件（暂存区管理）

index 是个二进制文件夹，对应着暂存区。暂存区：英文叫 stage 或 index。一般存放在 ".git目录下" 下的 index 文件（`.git/index`）中，所以我们把暂存区有时也叫作索引（index）。暂存区保存元数据包括时间戳、文件名、SHA id。

![](images/599166521713166.png)

![](images/72947036980311.png)

#### 9.4.8. 头指针相关（HEAD、FETCH_HEAD、ORIG_HEAD等）

- HEAD 记录了当前的头指针所对应的 commit id。比如说想退回到当前提交的前一个提交，就可以这样方便表示：`git reset HEAD^`
- FETCH_HEAD 记录了远端获取到的状态。最新从远程分支获取的分支。FETCH_HEAD 文件存储的是远程分支的最新的 commit 信息。
- ORIG_HEAD 记录了 HEAD 的状态，比如说要进行一些改变头指针的命令。例如像 merge、rebase 或 reset 之類
- 可以中途恢复到原来 HEAD 状态
- MERGE_HEAD：执行 merge 操作的 commit id。比如说交互操作时，用于记住这个 merge 状态。
- CHERRY_PICK_HEAD：执行 cherry_pick，意义同 MERGE_HEAD。
- 类似的还有 BISECT_HEAD, REVERT_HEAD，REJECT_NON_FF_HEAD

#### 9.4.9. modules 文件夹

modules 包含了第三方的库，比如一个工程包含了另一个 repo

![](images/574436293179729.png)

#### 9.4.10. decription 文件

decription 用于 git 在 web 界面用于搜索。

## 10. Git 扩展知识

### 10.1. git 分支命名规范

**Git 常用分支命名**

|   分支   |       命名       |                       说明                        |
| :-----: | :-------------: | ------------------------------------------------ |
|  主分支  | `master`/`main` | 主分支，所有提供给用户使用的正式版本，都在这个主分支上发布 |
| 开发分支 | `dev`/`develop` | 开发分支，永远是功能最新最全的分支                     |
| 功能分支 |   `feature-*`   | 新功能分支，某个功能点正在开发阶段                     |
| 发布版本 |   `release-*`   | 发布定期要上线的功能                                 |
| 修复分支 |     `bug-*`     | 修复线上代码的 bug                                  |

#### 10.1.1. main / master 分支

`master` / `main`：主分支，永远是可用的、稳定的、可直接发布的版本，不能直接在该分支上开发。只有计划发布的版本功能在`develop`分支上全部完成，而且测试没有问题了才会合并到`master`上。

#### 10.1.2. develop 分支

`develop`：开发主分支，代码永远是最新，所有新功能以这个分支来创建自己的开发分支，该分支只做合并操作，不能直接在该分支上开发

> - 例如要开发一个注册功能，那么就会从`develop`分支上创建一个`feature`分支`fb-register`，在`fb-register`分支上将注册功能完成后，将代码合并到`develop`分支上。这个`fb-register`就完成了它的使命，可以删除了。如果再需要开发一个登录功能，按刚刚上述操作流程再次进行
> - 大项目可选的团队 develop 分支。对于跨团队的大项目，每个团队都有自己的兴趣点和发布周期。很常见的做法是，每个团队有自己的 develop 分支。每过一段时间合并到总的 develop 分支。一般来说，中等大小的团队，专注于 repo 的某一部分，可以采取这样的分支形式。小团队或者个人没有必要有自己的 develop 分支。那样反而会浪费时间和增加合并过程中的风险。   
    
![](images/20844982457772.jpg)

#### 10.1.3. feature 分支

`feature-xxx`：功能开发分支，在`develop`上创建分支，以自己开发功能模块命名，功能测试正常后合并到`develop`分支

Feature 分支是生命期很短的分支，专注于单个特性的开发。和其他 VCS 不一样的是，在 git 里开分支开销非常低，所以可以高频地创建分支和合并分支。在做一个特性的时候，常规的流程是这样的：

1. 从 develop 分支上新建一个 feature 分支
2. 提交一些关于此 feature 的代码
3. 开发并测试完成后，合并至 develop 分支
4. 删除此 feature 分支

对于本地 repo 里的 feature 分支，可以做任何事。常见的用法是在开发过程中非常频繁地提交，走一小步就提交一次。在发出 MR 之前，先合并成一个 commit，把这个分支变整洁，方便后续操作。

当 feature 分支合并之后，绝对不存在任何理由让这个分支仍然存在于服务器上。记住：服务器上只是一个端点，删掉那边的一个分支不会影响本地 repo。如果有后续工作需要在那个分支上做，就继续在本地的分支上完成即可。这和服务端有没有这个分支一点关系都没有。

![](images/529103603166750.png)

因为每个分支都是平等的，可以在任何一个分支上都可以新建分支。比如，如果特性B依赖于特性A，不用等特性A合并了才开始做特性B。即便特性A不是自己开发的分支，也只要在特性A的分支上建立一个特性B的分支即可。等到特性A合并了，把特性B的分支 `rebase` 一下即可。少了等待环节，效率提高很多，也不必催人做 code review。

能建立大量 feature 分支，对于提高工作效率非常关键。每个特性建立一个 feature 分支，在上面完成特性开发后，发出 MR。在 code review 通过之前，已经可以新建另一个特性专用的 feature 分支，切换过去，开始做另一个特性。在 code review 过程中还能来回切换，同时做多个特性。其他 VCS 是做不到这一点的，效率也自然低很多。

#### 10.1.4. release 分支群

`release`：预分布分支，在合并好`feature`分支的`develop`分支上创建，主要是用来测试 bug 的分支，修改好 bug 并确定稳定之后合并到`develop`和`master`分支，然后发布`master`分支

当发现一个 bug 的时候，在 main 或者 develop 分支修好，然后 cherry-pick 到 release 分支里。这种单向的处理可以方便管理，并且不用担心某个 commit 是不是只有 release 分支有。

`release-fix`：功能 bug 修复分支，在`release`上创建分支修复，修复好测试出来的 bug 之后合并回`release`分支。

#### 10.1.5. hotfix 分支

`hotfix-xxx`：紧急 bug 修改分支，项目上线之后可以会遇到一些环境问题需要紧急修复，在`master`分支上创建，流程跟`release`分支相似，修复完成后合并到`develop`和`master`分支

### 10.2. 开发项目时 git 分支管理流程图

![](images/20190316175236283_27122.jpg)

***注意事项：***

- 一个分支尽量开发一个功能模块，不要多个功能模块在一个分支上开发。
- 开发过程中，如果组员A开发的功能依赖组员B正在开发的功能，可以待组员B开发好相关功能之后，组员A直接pull组员B的分支下来开发，不需要先将组员B的分支merge到develop分支。
- feature 分支在申请合并之前，最好是先 pull 一下 develop 主分支下来，看一下有没有冲突，如果有就先解决冲突后再申请合并。

### 10.3. git 提交规范

#### 10.3.1. 中文式提交格式参考

```
<新功能|bug修复|文档改动|格式化|重构|测试代码>: (影响范围) <主题>
# 解释为什么要做这些改动
issue #?
```

#### 10.3.2. 提交类型

关于 commit 时类别，一般通用的如下

- feat：新功能（feature）
- fix：修复bug
- docs：文档（documentation）变动
- style：格式（不影响代码运行的变动），如格式化，缺失分号等，但不包括生产时紧急的bug修复
- refactor：重构（即不是新增功能，也不是修改bug的代码变动）
- perf：代码优化
- test：增加测试
- build：构建工具或外部依赖的更改，比如后端maven，前端package，json等
- ci：更改项目级的配置文件或脚本
- chore：构建过程或辅助工具的变动，除上面之外的修改
- revert：撤销先前的提交

#### 10.3.3. 提交注释示例

```
<类型>: <主题>

# 解释为什么要做这些改动（限制每行72个字）

# ----------------------------------------------
# 注意：
# 提交模板的内容，如果最左侧带"#"，内容不会提交到git中
# 主题和内容以一个空行分隔
# 主题限制为最大50个字
# 主题行结束不用标点
# 内容每行72个字
# 内容用于解释为什么和是什么，而不是怎么做
# ----------------- 例子 -------------------------
# feat: 增加账号激活功能
#
# 因为账号初始化时......
# ......
# 所以......
# ---------------- 例子结束 -----------------------
```

### 10.4. 免费源代码托管网站

#### 10.4.1. GitHub

> 官网：https://github.com/

#### 10.4.2. 码云（Gitee）

> 网址：https://gitee.com/

开源中国旗下的代码托管平台：码云（Gitee）。是开源中国社区团队（深圳市奥思网络科技有限公司）推出的基于Git的免费代码托管平台，并且为开发者提供云端软件开发协作平台。无论是个人、团队、或者是企业，都能够用码云实现代码托管、项目管理与协作开发。

码云提供开源软件服务和企业服务，是最为接近GitHub功能的国产开发平台。码云上目前已经有超过10万家企业/机构入驻，其中不乏招商银行、比亚迪汽车等大牌厂商。码云近期也通过了通过 ISO27001:2013 信息安全管理体系认证及 ISO9001:2015 质量管理体系认证，可以说是国内比较先发、规模比较领先的代码托管平台。

7 月 14 日，工业和信息化部技术发展司公布了 「2020 年开源托管平台项目」的招标结果，由深圳市奥思网络科技有限公司（开源中国）牵头，与国家工业信息安全发展研究中心等 10 家单位组成的联合体中标该项目，联合体将依托码云建设中国独立的开源托管平台。

#### 10.4.3. Coding.net

> 网址：https://coding.net/

Coding.net是深圳市腾云扣钉科技有限公司推出的产品。与GitHub和码云的定位不同，Coding.net重点面向团队和企业的代码协作开发服务，而没有将开源软件作为重点。Coding.net的特色包括在最开始就主推Web IDE的开发模式，通过与腾讯云的合作，提供了免费及时部署调试的环境，形成了差异化的竞争。

Coding.net的项目管理包含任务、讨论、文件等功能，支持多成员协作，并且深度集成了代码仓库的操作与状态。此外，还提供社会化协作功能，支持移动客户端。

随着Github免费策略的推进，CODING 也已经顺势开放所有基础功能（项目协同、代码托管、CI/CD 等）免费使用，不限成员数，帮助国内开发者零成本开始研发协作。

#### 10.4.4. Agit.ai

> 网址：https://agit.ai/

国内人工智能开发者绝对不能错过的平台：Agit.ai。是阿吉特云计算有限公司（澳门）推出的产品，其定位上更为国际化。Agit.ai的界面是熟悉的GitHub风格，并且同样也是面向开源软件。从平台更新历史上看，Agit.ai是2020年7月上线的新平台，在项目管理和其他协同开发功能上具备基本的Git服务，但与码云还是略有差距，积累的开源仓库也不够丰富。

但Agit.ai独特的定位让人眼前一亮。如同其首页文字所述，Agit.ai专门面向AI开发人员。AI开发有何不同？Agit.ai提供了仓库代码运行服务，可直接选择算力启动AI算法的训练，并支持在线Tensorboard观察训练过程。另外，Agit.ai目前的算力是免费使用，其中还包括了有4块GPU卡的算力（不知道这样的算力羊毛能薅多久）。

Agit.ai为开发者提供集成了Tensorflow、Pytorch、Ray等常用AI库的开发环境镜像，以及一键式运行的的分布式计算资源，每一个为了环境搭建而苦恼的算法攻城狮，看到之后都应该会倍感欣慰。独特的匿名分享功能简直就是为了论文投稿的双盲评审而生，也可以从中看到浓浓的学术背景。

#### 10.4.5. BitBucket

> 网址：https://bitbucket.org/

它可以免费提供无限制的私人和公共仓库，可以为最多五个会员使用。它的功能包括，代码搜索、BitBucket 管道、合并请求、智能镜像、问题单跟踪、灵活的部署模式、IP 白名单以及保护工作成果的分支权限。

值得注意的是，它让用户可以使用任何 Git 客户端或 Git 命令行来推送文件。并且 BitBucket 可以部署在云端、数据中心或本地服务器上。

### 10.5. git 钩子

#### 10.5.1. 本地禁止提交(commit)到 master 分支

一般 master 分支只能从其他分支合并代码，为了防止意外提交和开发者随意提交到 master 分支。可以通过提交前钩子来禁止提交到 master 分支，在 git 项目目录下添加 `.git/hooks/pre-commit` 文件，添加内容如下：

```
#!/bin/sh
branch=$(git rev-parse --symbolic --abbrev-ref HEAD)
if [ "master" == "$branch" ]; then
  echo ".git/hooks: 不能commit到 $branch 分支"
  exit 1
fi
```

### 10.6. 减小 Git 仓库 .git 文件大小（待测试）

> 后面待测试与修改，参考：https://blog.csdn.net/LOI_QER/article/details/107911115

#### 10.6.1. 起因

使用 git 储存本地音频、相片时，其中很多文件超过 10Mb，更有很多超过 50Mb，这些文件都添加到 git 的历史记录中`(git add . && git commit`)，就算后面删除了这些文件本身，但其提交记录永久的留在了 .git 中，被 git 保存为了 Blob 对象储存起来了，导致 .git 目录超过 35 Gb，占用过多硬盘空间，所以需要进行“瘦身”。

#### 10.6.2. git gc 修剪历史提交

当运行 `git gc` 命令时，Git 会收集所有松散对象并将它们存入 packfile，合并这些 packfile 进一个大的 packfile，然后将不被任何 `commit` 引用并且已存在一段时间 (数月) 的对象删除。 此外， Git 还会将所有引用 (references) 并入一个单独文件。

1. 在仓库根目录，运行 `gc` ，生成 `pack` 文件（后面的 `--prune=now` 表示对之前的所有提交做修剪，有的时候仅仅 `gc` 一下 `.git` 文件就会小很多）

```bash
$ git gc --prune=now
```

2. 找出最大的三个文件（看自己需要）

```bash
$ git verify-pack -v .git/objects/pack/*.idx | sort -k 3 -n | tail -3
# 示例输出：
#1debc758cf31a649c2fc5b0c59ea1b7f01416636 blob   4925660 3655422 14351
#c43a8da9476f97e84b52e0b34034f8c2d93b4d90 blob   154188651 152549294 12546842
#2272096493d061489349e0a312df00dcd0ec19a2 blob   155414465 153754005 165096136
```

3. 查看那些大文件究竟是谁（c43a8da 是上面大文件的hash码）

```bash
$ git rev-list --objects --all | grep c43a8da
# c43a8da9476f97e84b52e0b34034f8c2d93b4d90 data/bigfile
```

4. 移除对该文件的引用（也就是 data/bigfile）

```bash
$ git filter-branch --force --index-filter "git rm --cached --ignore-unmatch 'data/bigfile'"  --prune-empty --tag-name-filter cat -- --all
```

5. 进行 `repack`

```bash
$ git for-each-ref --format='delete %(refname)' refs/original | git update-ref --stdin
$ git reflog expire --expire=now --all
$ git gc --prune=now
```

6. 查看 `pack` 的空间使用情况

```bash
$ git count-objects -v
```

### 10.7. 将项目同时提交到多个远程仓库

由于 GitHub 的访问速度慢，因此会有需求将一套开源代码同时提交到多个开源平台（例如 GitHub 和 Gitee）。以下是一套代码同时提交到 GitHub 和 Gitee 为示例，配置 Git 达到同时上传代码到多个平台。

1. 分别在 GitHub 与 Gitee 上创建仓库（最好是同名称）。
> Tips: 在创建 Gitee 仓库时，除了填写必要信息之后，最下面一栏选择『导入已有仓库』，然后将 GitHub 上的仓库地址（Https 形式）copy 过来，直接粘贴在对应位置。Gitee 会检测并给出提示。点击创建后稍等片刻，发现仓库已经被完美同步过来了。（*以后也可以每次手动同步 Github 的提交记录，这种操作适合非实时同步，可能隔一段时间自己登录账号进行一次同步。*）
2. 将 GitHub 的仓库 clone 到本地，命令如下：

```bash
git clone git@github.com:MooNkirA/demo.git
```

3. 进入本地项目的根目录，在根目录下 `.git` 的隐藏目录中找到 config 文件，对文件进行如下修改：

原文件：

```
[core]
	repositoryformatversion = 0
	filemode = false
	bare = false
	logallrefupdates = true
	symlinks = false
	ignorecase = true
[remote "origin"]
	url = git@github.com:MooNkirA/demo.git
	fetch = +refs/heads/*:refs/remotes/origin/*
[branch "develop"]
	remote = origin
	merge = refs/heads/develop
```

在原来的 github 仓库地址下面再添加一个 url 配置，指向 gitee 的地址。修改后：

```
[core]
	repositoryformatversion = 0
	filemode = false
	bare = false
	logallrefupdates = true
	symlinks = false
	ignorecase = true
[remote "origin"]
	url = git@github.com:MooNkirA/demo.git
	url = git@gitee.com:moonzero/demo.git
	fetch = +refs/heads/*:refs/remotes/origin/*
[branch "develop"]
	remote = origin
	merge = refs/heads/develop
```

> Notes: 这里的 GitHub 和 gitee 远程源仓库地址 url 如果是以 https 开头，都是用的是 HTTPS 协议，在从本地推送到远程仓库时候需要输入用户名和密码；也可以使用 GitHub 和 gitee 中的 ssh 协议，在本地配置好 github 和 gitee 的 ssh 秘钥（需要保持一致）后可以免密从本地推送到远程仓库。

4. 使用 `git remote -v` 命令，可以看到本地仓库与两个远程仓库关联

## 11. Git 常见问题及解决方法汇总

### 11.1. fatal detected dubious ownership in repository 解决办法

问题描述：在git仓库中执行 `git pull` 命令时，提示：`fatal: detected dubious ownership in repository`。这是因为 git 担心的权限安全策略导致的报错，可以按提示把某个（或多个）目录添加到信任列表

```shell
git config --global --add safe.directory D:/www/your-project
git config --global --add safe.directory D:/www/other-project
```

也可以通过加通配符为`*`，将所有文件夹都添加到信任列表。需要注意，该处理方法一般适用于只有本人一个用户使用的电脑，确保无其它用户，否则存在安全问题。

```shell
git config --global --add safe.directory "*"
```

### 11.2. git 显示和提交中文乱码

#### 11.2.1. git status 无法显示中文

使用 `git status` 查看有改动但未提交的文件时总只显示数字串，显示不出中文文件名。这是因为在默认设置下，中文文件名在工作区状态输出，中文名不能正确显示，而是显示为八进制的字符编码。

![](images/163713712249391.png)

**解决办法**：将 git 配置文件 `core.quotepath` 项设置为 false。其中，`quotepath` 表示引用路径，加上`--global`参数表示全局配置。在 git bash 终端输入命令：

```bash
git config --global core.quotepath false
```

#### 11.2.2. git bash 终端显示中文乱码

git bash 终端需要设置成中文和utf-8编码，才能正确显示中文。

在 git bash 的界面中右击空白处，弹出菜单，选择【选项】->【文本】->【本地Locale】，设置为`zh_CN`，字符集选框选为`UTF-8`。

![](images/279364112245946.png)

> Tips: 英文界面显示则是：【Options】->【Text】->【Locale】改为 `zh_CN`，Character set 改为 `UTF-8`

#### 11.2.3. 修改配置文件方式解决中文乱码

还可以通过直接修改配置文件的方式来解决中文乱码问题。进入 git 的安装目录，编辑 etc\gitconfig 文件，在文件末尾增加以下内容：

```properties
[gui]  
    encoding = utf-8  
    # 代码库统一使用utf-8  
[i18n]  
    commitencoding = utf-8  
    # log编码  
[svn]  
    pathnameencoding = utf-8  
    # 支持中文路径  
[core]
    quotepath = false 
    # status引用路径不再是八进制（反过来说就是允许显示中文了）
```

> Tips: 也有些 windows 系统是存放在`C:\Users\Administrator\.gitconfig`路径或`安装盘符:\Git\mingw64\etc\gitconfig`

编辑 etc\git-completion.bash 文件末尾增加以下内容：

```properties
# 让ls命令能够正常显示中文
alias ls='ls --show-control-chars --color=auto' 
```

编辑 etc\inputrc 文件，修改 `output-meta` 和 `convert-meta` 属性值：

```properties
set output-meta on  # bash可以正常输入中文  
set convert-meta off
```

编辑 profile 文件，在文件末尾添加如下内容：

```properties
export LESSHARESET=utf-8
```

> Notes: 注意以上内容配置时，删除`#`号的注释

### 11.3. Git 在跨平台 CRLF 和 LF 的解决方案

- mac 与 Unix 系统的换行使用 LF
- windows 系统换行使用 CRLF

因为不同系统的换行不一致，在多人协作共同开发的时候，可能导致提交代码时候产生问题。当使用 git 库提交代码的时候，不同的开发环境如果都是按照自己系统的方式任意修改换行类型，难免会让代码库整体混乱或者产生许多没有必要的代码更新。

Git 可以可以使用 `core.autocrlf` 命令设置在 push 时自动地把行结束符 CRLF 转换成 LF，而在 pull 代码时把 LF 转换成 CRLF。如果是在 Windows 系统上，把它设置成 `true`，这样当签出代码时，LF 会被转换成 CRLF

```bash
git config --global core.autocrlf true
```

将 `core.autocrlf` 设置成 `input`，在 push 时把 CRLF 转换成 LF，pull 时不转换

```bash
git config --global core.autocrlf input
```

将 `core.autocrlf` 设置成 `false`，则在本地和代码库中都保留 CRLF，无论 pull 还是 push 都不变

```bash
git config --global core.autocrlf false
```

小结：

- git 的 Windows 客户端基本都会默认设置 `core.autocrlf=true`，只要保持工作区都是纯 CRLF 文件，编辑器用 CRLF 换行，就不会出现相关警告。
- Linux 最好不要设置 `core.autocrlf`，因为该配置算是为 Windows 平台定制。
- Windows 上设置 `core.autocrlf=false`，仓库里也没有配置 `.gitattributes`，很容易引入 CRLF 或者混合换行符（Mixed Line Endings，一个文件里既有 LF 又有CRLF）到版本库，这样就可能产生各种奇怪的问题。

### 11.4. 解决合并冲突常用规则

当出现合并冲突的时候，最好的方式是先把 feature 分支 rebase 到目标分支的顶端，这时候解决冲突，然后 force push。

#### 11.4.1. 命令方式

> 只要所有开发者都遵守这个规则，那么解决冲突是一件非常容易的事情

1. 例如有 dev 分支。把代码修改完成了，现在不知道有没有冲突。
2. 在 dev 分支里面，执行命令 `git merge origin/master`，把远程的 master 分支合并到当前 dev 分支中。如果没有任何报错，那么直接转到第5步。
3. 如果有冲突，根据提示，把冲突解决，保存文件。然后执行命令 `git add xxx` 把你修改的文件添加到缓存区。然后执行命令 `git commit -m "xxx"` 添加 commit 信息。
4. 执行如下命令，切换到 master 分支：`git checkout master`。
5. 执行命令 `git pull` 确保当前 master 分支是最新代码。
6. 把 dev 分支的代码合并回 master 分支：`git merge dev`。
7. 提交代码：`git push`。

#### 11.4.2. 常见错误

- **解决合并冲突后建了个新的 MR**：因为冲突解决的错误行为，有可能在解决之后，修改被提交到了一个新的分支。这时候应该把你的分支 reset 到新的去，force push，再删掉新的；而不是关掉原先的 MR，在新分支上开个新 MR。
- **把分支搞乱**：如果真的遇到了多分支复杂交错的情况，有两个方法可以尝试清理出来。<font color=red>*注：两个方法最后都需要force push。*</font>
    - 强制rebase。Fetch一下整个repo；把你的分支rebase到目标分支上的时候勾选force；这时候在列表里选要拿去rebase的commit。大部分时候这都能行。但有时候git因为分支太错综复杂而搞不清楚commit，在列表里会有遗漏。
    - Cherry-pick。在目标分支上新建一个临时分支；把有用的commit都cherry-pick过去；把你的分支reset到那个临时分支上；最后删掉那个临时分支。

## 12. Git 学习资源分享

- [阮一峰 Git 教程](https://www.bookstack.cn/read/git-tutorial/README.md)
- [Learn Git Branching](https://learngitbranching.js.org/?locale=zh_CN) - 最好用的 Git 在线学习工具。提供了多个学习章节，适合不同水平的学习者。如果是初学者，可以按照顺序逐步学习。每个章节都设计有明确的学习目标和提示信息，帮助理解并实践 Git 的各种操作。
