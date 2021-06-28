# Dubbo源码分析

## 1. 源码构建

dubbo源码仓库：https://github.com/apache/dubbo/

Dubbo 使用 maven 作为构建工具。下载源码后，使用以下命令进行构建：

```bash
mvn clean install -Dmaven.test.skip
```

通过以下命令以构建 Dubbo 的源代码 jar 包

```bash
mvn clean source:jar install -Dmaven.test.skip
```

并且修改样例项目中的 dubbo 依赖为本地仓库的 SANPSHOT 版本，然后使用远程 debug 来调试 dubbo。



