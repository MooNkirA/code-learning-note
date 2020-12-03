# PowerMock (单元测试模拟框架)

## 1. PowerMock 概述

### 1.1. 为什么要使用Mock工具

在做单元测试的时候，会发现在要测试的方法会引用很多外部依赖的对象，比如：（发送邮件，网络通讯，远程服务，文件系统等等）。而又没法控制这些外部依赖的对象，为了解决这个问题，就需要用到Mock工具来模拟这些外部依赖的对象，来完成单元测试。

### 1.2. PowerMock简介

PowerMock是一个扩展了其它如EasyMock等mock框架的、功能更加强大的框架。PowerMock使用一个自定义类加载器和字节码操作来模拟静态方法，构造函数，final类和方法，私有方法，去除静态初始化器等等。通过使用自定义的类加载器，简化采用的IDE或持续集成服务器不需要做任何改变。

PowerMock能够完美的弥补如今比较流行的jMock 、EasyMock 、Mockito三个Mock工具，不能mock静态、final、私有方法等的缺点

熟悉PowerMock支持的mock框架的开发人员会发现PowerMock很容易使用，因为对于静态方法和构造器来说，整个的期望API是一样的。PowerMock旨在用少量的方法和注解扩展现有的API来实现额外的功能。目前PowerMock支持EasyMock和Mockito。

## 2. PowerMock 基础使用

### 2.1. 相关注解

- `@RunWith(PowerMockRunner.class)`
- `@PrepareForTest( { YourClassWithEgStaticMethod.class })`






