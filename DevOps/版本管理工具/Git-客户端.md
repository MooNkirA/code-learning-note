## 1. Git 相关图形化客户端

- Git 第三方 GUI Clients 官方推荐整理：https://git-scm.com/downloads/guis
- Sourcetree 网址：https://www.sourcetreeapp.com/
    - 使用教程：http://blog.cocoachina.com/article/71732
- GitHub Desktop 网址：https://desktop.github.com/
- TortoiseGit 网址：https://tortoisegit.org/
- Git Extensions 网址：https://gitextensions.github.io/

## 2. IDEA 集成 Git 客户端

### 2.1. 初始化配置 Git

1. 点击【File】菜单->【Settings】

![](images/161054208227337.png)

2. 选择【Version Control】->【Git】->在【Path to Git executable】中指定 git.exe 存放目录

![](images/226034308247503.png)

3. 点击【Test】按钮进行测试

![](images/409534508240172.png)

### 2.2. 创建本地仓库

1. 选择【VCS】->【Import into Version Control】->【Create Git Repository】或者

![](images/107195515227337.png)

2. 选择工程所在的目录后，即可创建本地仓库了

![](images/198535715247503.png)

3. 创建成功后，就会出现相关的 git 操作菜单

![](images/154835915240172.png)

### 2.3. Commit 提交代码

1. 右键选择【Git】->【Commit file...】，将当前项目代码提交到本地仓库

 ![](images/366334516232481.png)

或者点击 git 后边的对勾

![](images/420584716250361.png)

> 注意：项目中的配置文件不需要提交到本地仓库中，提交时需要忽略掉。在项目目录中创建忽略的文件`.gitignore`，文件内容如下：

```java
.idea
target
*.iml

.classpath
.project
.settings
Bin
```

2. 在提交框中，可以选择要提交的文件、填写提交信息、查看文件差异。

![](images/34324916247965.png)

3. 成功提交后，可以在底部的 Git 工具窗口查看提交的记录

![](images/489925316245467.png)

### 2.4. 版本切换

- 方式一：选择控制台【Git】(旧版本的idea叫“Version Control”)->【Log】->【Reset Current Branch...】->【Reset】。

![](images/133740017226708.png)

可以选择不同的回退方法，详情查看每个选项的说明

![](images/548270117249148.png)

这种**切换的特点是会抛弃原来的提交记录**

![](images/481510617223998.png)

- 方式二：选择控制台【Git】->【Log】->【Revert Commit】->【Merge】-> 处理代码 -> commit。

![](images/111810417244284.png)

![](images/95500517237830.png)

![](images/594310517230964.png)    

这种**切换的特点是会当成一个新的提交记录，之前的提交记录也都保留**。Revert 操作会当成一个新的提交记录，如果后悔了“回退”这个操作，也可以回退到没有回退之前的版本，因为历史记录还保留提交记录。

![](images/110760617221494.png)

### 2.5. 分支管理

#### 2.5.1. 创建分支

选择【VCS】(旧版本idea)->【Git】->【Branches】->【New Branch】-> 给分支起名字 ->【Creat】

![](images/306131517232945.png)

![](images/493561517225830.png)

![](images/136431617226439.png)

#### 2.5.2. 切换分支

- 选择右下角 Git 分支的名称按钮 -> 选择要切换的分支 ->【Checkout】

![](images/370461817253394.png)

#### 2.5.3. 合并分支

选择 Git(VCS)->【Merge...】

![](images/420832417235607.png)

选择要合并的分支 -> merge

![](images/447702617224905.png)

> Tips: 如果合并时出现代码冲突，需要处理冲突后再合并。

#### 2.5.4. 删除分支

点击右下角分支名称 -> 选中要删除的分支 ->【Delete】

![](images/354692817221156.png)

### 2.6. 克隆远程仓库到本地仓库

- 第一种方式(主窗口)：【Get from VSC】-> 选择【Version control: Git】-> 指定远程仓库的路径 -> 指定本地存放的路径 -> clone

![](images/352595117238424.png)

- 第二种方式(主菜单)：【Git】->【Clone...】-> 指定远程仓库的路径与本地存放的路径 -> clone

![](images/466855517239719.png)

### 2.7. 推送(push)本地仓库到远程仓库

1. 【Git】(VCS)->【Push...】

![](images/106143917221295.png)

2. 选择相应的分支，点击【Push】按钮，推送到远程仓库

![](images/57265917240721.png)

如果不是克隆远程仓库创建的，此时就需要点击 Define remote，设置 git 服务器远端地址。设置后，点击 Push 推送到远程仓库

![](images/478754017233385.png)

### 2.8. 更新(pull)远端仓库代码到本地仓库

选择菜单栏或者右键项目，选择【Pull...】，更新远程仓库

![](images/376451218240898.png)

### 2.9. 协同开发

模拟两个程序员同步代码与更新代码。

![](images/385075921221044.jpg)

![](images/472505921239470.jpg)

使用右边工具栏菜单：

![](images/86980022227337.jpg)

#### 2.9.1. 获取代码

点击 pull 图标获取远程仓库代码

![](images/303780122247503.jpg)

![](images/368070122240172.jpg)

#### 2.9.2. 推送代码

点击 push 图标推送代码到远程仓库

![](images/176570822236727.jpg)

#### 2.9.3. 解决冲突

两个程序员修改了同一个文件中的同一行代码，提交时

![](images/306291022232481.jpg) ![](images/438401022250361.jpg)

处理冲突:
![](images/592001122247965.jpg)

> Notes: <font color=red>**还需要重新push代码到远程仓库。**</font>

### 2.10. idea 取消项目与 git 仓库的关联

选择【file】->【settings】->【version control】，选择项目目录，再点“减号”即可解除 git 仓库的关联，然后去项目目录下删除`.git`这个文件夹即可

![](images/392491618226450.png)

## 3. Git 客户端 SourceTree 使用教程（待整理）

> 参考：https://www.cnblogs.com/Can-daydayup/p/13128633.html
