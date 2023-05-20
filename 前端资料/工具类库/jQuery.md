## 1. jQuery 概述

jQuery 是一个 JS 框架，运行在 JS 之上。 **jQuery 就是一组 JS 编写的方法和功能，可以提高开发 JS 的效率，降低代码的开发难度**。JS 框架很多， jQuery 只是其中一个比较流行的框架。

**使用JS框架的好处**：

1. 不同的厂商开发的浏览器之间是有差异。使用框架可以减少甚至消除浏览器之间的差异
2. 减少 javascript 程序开发的工作量

**jQuery框架特点**

1. 轻量级框架：文件体积小，占用系统资源少，效率高。
2. 只要写一套代码，几乎兼容所有主流浏览器，如：chrome，firefox，safari、ie、edge、Opera
3. jQuery本身还支持大量的插件，进一步提升jQuery功能。如：easyUI 必须运行在 jQuery 之上；Bootstrap 表示层框架等
4. 宗旨：write less do more

> Tips: 一些jQuery制作效果网站：www.17sucai.com

### 1.1. jQuery 基础使用

官网：www.jquery.com

1. 对于后台程序员来说，版本之间差别不大。
2. 2.x 以后不支持IE/6/7/8
3. min：压缩版，在使用上，压缩版功能上与正常的版本是一样的。其他主要的特点如下：
	1. 没有注释，没有换行，没有空格。
	2. 所有的变量名都尽可能的短，一般只有一个字母

![](images/202210211440119667_3132.jpg)

Code Demo: jQuery的导入并测试是否成功

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>JQ测试</title>
		<!--导入jQuery脚本文件-->
		<script src="js/jquery-1.8.3.min.js" type="text/javascript"></script>
	</head>
	<body>
		<script type="text/javascript">
			// JQ函数的起点
			$(function(){
				// 代码写在匿名函数中
				alert("我是JQ");
			})
		</script>
	</body>
</html>
```

> Notes: 如果将 `<script>` 的代码都写到JS文件中，只需要在 HTML 文件中导入 JS 文件路径及 JQ 文件路径既可

例如：

```html
<!--导入jQuery脚本文件-->
<script src="js/jquery-1.8.3.min.js" type="text/javascript"></script>
<!--导入js脚本文件-->
<script src="js/xxx.js" type="text/javascript"></script>
```

### 1.2. JavaScript 对象与 jQuery 对象的区别

如果使用 jQuery 框架的话，将使用的是 jQuery 对象，以前使用的是 JS 对象。jQuery 对象中的方法与 JS 对象中的方法是不同的。

#### 1.2.1. 事件写法的区别

两者的区别：

- JS 中事件使用 `on` 开头属性，如：`onclick = funcation()`
- JQ 中所有的事件是方法，如：`click(function())`

#### 1.2.2. 加载次数的区别

- 加载代码的编写方式：
    - JS：`window.onload = function()`
    - JQ：`$(function())`
- 效率：
    - JS：效率更低，等到页面上所有的资源加载完毕才运行，包括引用的链接的图片等
    - JQ：效率更高，只要当前页面上所有的元素加载完毕就会执行。
- 加载次数
    - JS 中只运行最后的 `onload` 方法 1 次
    - JQ 中每个`$()`都会执行

### 1.3. jQuery 对象与 JS 对象之间的转换

原则：如果使用 JQ 框架，尽量使用 JQ 方法和对象。如果是 JS 对象要使用 JQ 对象的方法的话，必须将 JS 对象转换成 JQ 对象；同样，如果 JQ 对象要调用 JS 方法也要进行转换。

JS -> jQuery 转换操作：

```js
$(JS对象);
```

jQuery -> JS 转换操作：

```js
JQ对象[0];
// 或者
JQ对象.get(0);
```

> Notes: <font color=red>**JQ 对象在低层是一个 JS 数组对象，只需要取出第0个元素即可**</font>

### 1.4. jQuery 对象与 DOM 对象之间的转换

在 jQuery 对象中无法使用 DOM 对象的任何方法。此时需要进行转换：

jQuery 对象转换成 DOM 对象：

jquery 提供了两种方法将一个jquery对象转换成一个dom对象，即`[index]`和`get(index)`。因 jQuery 对象就是一个数组对象

DOM 对象转换成 jQuery 对象：对于一个 DOM 对象，只需要用`$()`把 DOM 对象包装起来，就可以获得一个 jQuery 对象了，即`var ele = $(dom对象);`
