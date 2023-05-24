## 1. Shell 基础语法

### 1.1. 编写 shell 脚本

用 vi 命令，创建（或打开）test.sh，编写如下内容：

```shell
# vi test.sh
#!/bin/bash
echo "Hello world!"
```

> Notes: 第1行 `#!/bin/bash` 是指定解释器

### 1.2. 执行 shell 脚本的三种方法

方法1：直接用 bash 解释器执行，当前终端会新生成一个子 bash 去执行脚本。命令如下：

```bash
bash xxx.sh
```

方法2：添加可执行权限，这种方式默认根据脚本第一行指定的解释器处理，如果没写以当前默认 Shell 解释器执行。

```bash
[root@MoonKirA ~]# ll test.sh
-rw-r--r--. 1 root root 32 Aug 18 01:07 test.sh
[root@MoonKirA ~]# chmod +x test.sh
[root@MoonKirA ~]# ./test.sh
-bash: ./test.sh: Permission denied
```

方法3：source 命令执行，以当前默认 Shell 解释器执行。命令如下：

```bash
source xxx.sh
```
