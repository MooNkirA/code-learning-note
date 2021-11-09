# CSS 其他相关知识

## 1. CSS精灵技术（sprite）（了解）

> 详细参考《2017.07.05-Web前端入门教程》笔记

### 1.1. 精灵技术本质

简单地说，CSS精灵是一种处理网页背景图像的方式。它将一个页面涉及到的所有零星背景图像都集中到一张大图中去，然后将大图应用于网页，这样，当用户访问该页面时，只需向服务发送一次请求，网页中的背景图像即可全部展示出来。通常情况下，这个由很多小的背景图像合成的大图被称为精灵图（雪碧图）

## 2. 字体图标（iconfont）（了解）

> 详细参考《2017.07.05-Web前端入门教程》笔记

### 2.1. 字体图标优点

- 可以做出跟图片一样可以做的事情,改变透明度、旋转度等等...
- 但是本质其实是文字，可以很随意的改变颜色、产生阴影、透明效果等等...
- 本身体积更小，但携带的信息并没有削减。
- 几乎支持所有的浏览器
- 在移动端设备响应式放大和缩小不会失真

### 2.2. 字体图标使用流程

![](images/20200721213121168_8319.png)

icomoon字库。网站：http://www.iconfont.cn/

阿里icon font字库。网站：http://www.iconfont.cn/

## 3. 浏览器私有前缀

浏览器私有前缀是为了兼容老版本的写法，比较新版本的浏览器无须添加。

### 3.1. 私有前缀

- `-moz-`：代表 firefox 浏览器私有属性
- `-ms-`：代表 ie 浏览器私有属性
- `-webkit-`：代表 safari、chrome 私有属性
- `-o-`：代表 Opera 私有属性

### 3.2. 推荐写法

先写带私有前缀的属性，最后再写正常属性

```css
-moz-border-radius: 10px;
-webkit-border-radius: 10px;
-o-border-radius: 10px;
border-radius: 10px;
```

## 4. CSS书写规范

### 4.1. 空格规范

【强制】 `选择器` 与 `{` 之间必须包含空格。

```css
.selector { }
```

【强制】 `属性名` 与之后的 `:` 之间不允许包含空格， `:` 与 `属性值` 之间必须包含空格。

```css
font-size: 12px;
```

### 4.2. 选择器规范

【强制】 当一个 rule 包含多个 selector 时，每个选择器声明必须独占一行。

```css
/* good */
.post,
.page,
.comment {
    line-height: 1.5;
}

/* bad */
.post, .page, .comment {
    line-height: 1.5;
}
```

【建议】 选择器的嵌套层级应不大于 3 级，位置靠后的限定条件应尽可能精确。

```css
/* good */
#username input {}
.comment .avatar {}

/* bad */
.page .header .login #username input {}
.comment div * {}
```

### 4.3. 属性规范

【强制】 属性定义必须另起一行。

```css
/* good */
.selector {
    margin: 0;
    padding: 0;
}

/* bad */
.selector { margin: 0; padding: 0; }
```

【强制】 属性定义后必须以分号结尾。

```css
/* good */
.selector {
    margin: 0;
}

/* bad */
.selector {
    margin: 0
}
```

### 4.4. CSS 属性书写顺序(重点)

1. 布局定位属性：display / position / float / clear / visibility / overflow（建议 display 第一个写，毕竟关系到模式）
2. 自身属性：width / height / margin / padding / border / background
3. 文本属性：color / font / text-decoration / text-align / vertical-align / white-space / break-word
4. 其他属性（CSS3）：content / cursor / border-radius / box-shadow / text-shadow / background:linear-gradient ...

示例

```css
.jdc {
    display: block;
    position: relative;
    float: left;
    width: 100px;
    height: 100px;
    margin: 0 10px;
    padding: 20px 0;
    font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif;
    color: #333;
    background: rgba(0,0,0,.5);
    border-radius: 10px;
}
```
