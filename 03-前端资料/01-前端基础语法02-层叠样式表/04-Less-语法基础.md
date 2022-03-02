# Less 语法基础

Less中文网址：http://lesscss.cn/

## 1. Less 简述

### 1.1. css 的弊端

CSS 是一门非程序式语言，没有变量、函数、SCOPE（作用域）等概念。

- CSS 需要书写大量看似没有逻辑的代码，CSS 冗余度是比较高的。
- 不方便维护及扩展，不利于复用。
- CSS 没有很好的计算能力
- 非前端开发工程师来讲，往往会因为缺少 CSS 编写经验而很难写出组织良好且易于维护的 CSS 代码项目。

### 1.2. Less 简介

Less （Leaner Style Sheets 的缩写） 是一门向后兼容的 CSS 扩展语言。它是 CSS 预处理语言，即包含 CSS 原有的功能，在原有的语法基础之上又增加了变量、Mixin、函数等特性，使 CSS 更易维护和扩展，为 CSS 加入程序式语言的特性。

> Less 可以运行在 Node 或浏览器端。
>
> 常见的CSS预处理器：Sass、Less、Stylus

总结：<font color=red>**Less 是一门 CSS 预处理语言，它扩展了CSS的动态特性。**</font>

### 1.3. Less 安装

- 安装 nodejs，一般推荐选择版本LTS版本（相对最新版而言，比较稳定）。下载网址：http://nodejs.cn/download/
- 检查是否安装成功，使用命令行工具（win10 是 window +r 打开 运行输入cmd），输入 `node –v` 查看版本即可
- 基于 nodejs 在线安装Less，使用命令行输入 `npm install -g less` 即可
- 检查是否安装成功，使用命令行输入 `lessc -v` 查看版本即可

### 1.4. Less 编译

本质上，Less 包含一套自定义的语法及一个解析器，用户根据这些语法定义自己的样式规则，这些规则最终会通过解析器，编译生成对应的 CSS 文件。

所以，需要把 less 文件，编译生成为 css 文件，这样 html 页面才能引入使用。

#### 1.4.1. 使用 nodejs 的 less 工具编译

使用命令行输入`lessc`命令可以将less文件编译成css文件。*前提是已经安装node与less*

```
lessc style.less style.css
```

#### 1.4.2. vocode 相关 Less 插件编译

- Easy LESS 插件：用来把less文件编译为css文件。安装完毕插件，重新加载下VSCode。只要保存一下 Less 文件，会自动生成 CSS 文件。
- **Sass/Less/Stylus/Pug/Jade/Typescript/Javascript Compile Hero Pro**插件：同样实现 less 文件与其实语言文件的编译功能，还有带有语言的格式化功能。

## 2. 变量（Variables）

变量是指没有固定的值，可以改变的。因为 CSS 中的一些颜色和数值等经常使用。

### 2.1. 语法定义

- 定义变量的语法：

```less
@变量名: 值;
```

- 使用时的语法：

```less
选择器 {
    xx属性: @变量名;
}
```

### 2.2. 变量命名规范

- 必须有`@`为前缀
- 不能包含特殊字符
- 不能以数字开头
- 大小写敏感

## 3. 嵌套规则（Nesting）

### 3.1. 嵌套概述

Less 提供了使用嵌套（nesting）代替层叠或与层叠结合使用的能力。用 Less 书写的代码更加简洁，并且模仿了 HTML 的组织结构。

它是一组 CSS 属性，允许将一个类的属性用于另一个类，并且包含类名作为其属性。 在 LESS 中，可以使用类或 id 选择器以与 CSS 样式相同的方式声明 mixin。 它可以存储多个值，并且可以在必要时在代码中重复使用。

### 3.2. 嵌套写法

#### 3.2.1. 常规嵌套

```less
#header {
  color: black;
  .navigation {
    font-size: 12px;
  }
  .logo {
    width: 300px;
  }
}
```

编译成 CSS 代码

```css
#header {
  color: black;
}
#header .navigation {
  font-size: 12px;
}
#header .logo {
  width: 300px;
}
```

#### 3.2.2. 结合伪选择器的嵌套

交集|伪类|伪元素选择器

- 内层选择器的前面没有 `&` 符号，则它被解析为父选择器的后代
- 如果有 `&` 符号，它就被解析为父元素自身或父元素的伪类

```less
a {
    &:hover {
        color: red;
    }
}
```

编译后：

```css
a:hover {
    color: red;
}
```

还可以使用此方法将伪选择器（pseudo-selectors）与混合（mixins）一同使用。下面是一个经典的 clearfix 技巧，重写为一个混合（mixin） (`&` 表示当前选择器的父级）：

```less
.clearfix {
  display: block;
  zoom: 1;

  &:after {
    content: " ";
    display: block;
    font-size: 0;
    height: 0;
    clear: both;
    visibility: hidden;
  }
}
```

### 3.3. LESS 嵌套规则示例

```html
<html>
<head>
   <title>Nested Directives</title>
   <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body class="container">
   <h1>Example using Nested Directives</h1>
   <p class="myclass">LESS enables customizable, manageable and reusable style sheet for web site.</p>
</body>
</html>
```

```less
.container {
    h1 {
        font-size: 25px;
        color: #e45456;
    }
    p {
        font-size: 25px;
        color: #3c7949;
    }

    .myclass {
        h1 {
            font-size: 25px;
            color: #e45456;
        }
        p {
            font-size: 25px;
            color: #3c7949;
        }
    }
}
```

编译后生成的css文件

```css
.container h1 {
    font-size: 25px;
    color: #e45456;
}
.container p {
    font-size: 25px;
    color: #3c7949;
}
.container .myclass h1 {
    font-size: 25px;
    color: #e45456;
}
.container .myclass p {
    font-size: 25px;
    color: #3c7949;
}
```

## 4. 运算（Operations）

算术运算符 加（`+`）、减（`-`）、乘（`*`）、除（`/`）可以对任何数字、颜色或变量进行运算。

### 4.1. 运算的基础使用

如果可能的话，算术运算符在加、减或比较之前会进行单位换算。计算的结果以最左侧操作数的单位类型为准。如果单位换算无效或失去意义，则忽略单位。无效的单位换算例如：px 到 cm 或 rad 到 % 的转换。

```less
// 所有操作数被转换成相同的单位
@conversion-1: 5cm + 10mm; // 结果是 6cm
@conversion-2: 2 - 3cm - 5mm; // 结果是 -1.5cm

// conversion is impossible
@incompatible-units: 2 + 5px - 3cm; // 结果是 4px

// example with variables
@base: 5%;
@filler: @base * 2; // 结果是 10%
@other: @base + @filler; // 结果是 15%
```

乘法和除法不作转换。因为这两种运算在大多数情况下都没有意义，一个长度乘以一个长度就得到一个区域，而 CSS 是不支持指定区域的。Less 将按数字的原样进行操作，并将为计算结果指定明确的单位类型。

```less
@base: 2cm * 3mm; // 结果是 6cm
```

可以对颜色进行算术运算：

```less
@color: #224488 / 2; //结果是 #112244
background-color: #112244 + #111; // 结果是 #223355
```

### 4.2. 运算的注意事项

- 乘号（`*`）和除号（`/`）的写法
- 运算符中间左右有个空格隔开。如：`1px + 5`
- 对于两个不同的单位的值之间的运算，运算结果的值取第一个值的单位
- 如果两个值之间只有一个值有单位，则运算结果就取该单位

### 4.3. less 运算示例

```less
@baseFont: 50px;
html {
    font-size: @baseFont;
}

// 1. 运算符的左右两侧必须敲一个空格隔开
@border: 5px + 5;
div {
    // 2. 两个数参与运算，如果只有一个数有单位，则最后的结果就以这个单位为准
    width: 200px - 50;
    height: (200px + 50px) * 2;
    border: @border solid red;
    background-color: #666 - #222;
}
img {
    // 3. 两个数参与运算，如果2个数都有单位，而且不一样的单位，最后的结果以第一个单位为准
    width: 82rem / @baseFont;
    height: 82rem / @baseFont;
}
```

编译成css

```css
html {
    font-size: 50px;
}
div {
    width: 150px;
    height: 500px;
    border: 10px solid red;
    background-color: #444444;
}
img {
    width: 82rem / 50px;
    height: 82rem / 50px;
}
```

## 5. 导入（Importing）

Less 语法可以导入一个 `.less` 文件，此文件中的所有变量就可以全部使用了。如果导入的文件是 `.less` 扩展名，则可以将扩展名省略掉：

```less
@import "library"; // library.less
@import "typo.css";
```