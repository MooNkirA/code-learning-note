## 1. JavaScript( ECMA Script )

JavaScript 这门语言属于一系列 Web 标准套件中的一部分，这些标准定义出了可互操作的浏览器平台。此部分的内容主要是 ES6 以前的语法，ES6 及其更后的版本详见[《ECMAScript6 基础笔记》](/前端资料/JavaScript/ECMAScript6)

## 2. JavaScript 数据类型

### 2.1. 5种基本数据类型

|    关键字    |                                                  数据类型                                                   |
| :---------: | ---------------------------------------------------------------------------------------------------------- |
|  `Number`   | 数值型（整数和浮点数）                                                                                        |
|  `String`   | 字符串类型(字符和字符串)，可以使用单引号或双引号                                                                  |
|  `Boolean`  | 布尔类型(true/false)                                                                                        |
|  `Object`   | 对象类型，对象类型格式：(定义对象，称为JSON对象<br/>(JavaScript Object Notation, JS对象标记)是一种轻量级的数据交换格式 |
| `undefined` | 未初始化的类型，如果一个变量没有赋值它的类型是不确定的                                                              |

- Object 示例

```json
const obj = {
    name: "张三",
    age: 18
}
```

**`null`与`undefined`的区别**

- `null`：其实是Object对象类型，这个对象只是没有值，为空
- `undefined`：没有初始化的类型，并不知道是什么数据类型

### 2.2. typeof操作符

- 作用：用于判断变量是什么数据类型
- 语法格式：`typeof(变量名)` 或者 `typeof 变量名`
- 返回值：返回这个变量的数据类型

Code Demo

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title></title>
	</head>
	<body>
		<!--引用外部的js文件，文件代码以下面的代码一样，在引用的时候执行js中的代码-->
		<script src="js/test.js" type="text/javascript" charset="utf-8"></script>

		<hr />

		<script type="text/javascript">
			//整数
			var i = 10;

			//双精度
			var d = 3.14;

			//字符串和字符
			var str = "abc";
			var c = 'a';

			//布尔类型
			var b = true;

			//js创建对象方法
			var o = new Object();
			var o = {name:"xx", age:18};
			var u;
			var n = null;

			document.write("i的类型：" + typeof(i) + "<br/>");
			document.write("d的类型：" + typeof(d) + "<br/>");
			document.write("s的类型：" + typeof(str) + "<br/>");
			document.write("c的类型：" + typeof(c) + "<br/>");
			document.write("b的类型：" + typeof(b) + "<br/>");
			document.write("o的姓名：" + o.name + "年龄：" + o.age + "<br/>");
			document.write("o的类型：" + typeof(o) + "<br/>");
			document.write("u的类型：" + typeof(u) + "<br/>");
			document.write("n的类型：" + typeof(n) + "<br/>");
		</script>
	</body>
</html>
```

### 2.3. 类型强制转换

#### 2.3.1. String 强制转换为数字

- 可以用 `*1` 来转化为数字(实际上是调用`.valueOf`方法)。然后使用` Number.isNaN` 来判断是否为`NaN`，或者使用 `a!==a` 来判断是否为 `NaN`，因为 `NaN !== NaN`

```js
"32" * 1;  // 32
"ds" * 1;  // NaN
null * 1;  // 0
undefined * 1;  // NaN
1 * { valueOf: () => "3" };  // 3
```

- 常用：也可以使用 `+` 来转化字符串为数字

```js
+ "123"  // 123
+ "ds"  // NaN
+ ""    // 0
+ null  // 0
+ undefined  // NaN
+ { valueOf: () => "3" }  // 3
```

#### 2.3.2. 取整 `|0`

对一个数字 `|0` 可以取整，负数也同样适用，`num|0`

```js
const a = 1.3 | 0;  // 1
const b = -1.9 | 0; // -1
```

#### 2.3.3. 判断奇偶数 `&1`

对一个数字 `&1` 可以判断奇偶数，负数也同样适用，`num&1`

```js
const num = 3;
const b1 = !!(num & 1);  // true
const b2 = !!(num % 2);  // true
```

## 3. 运算符

### 3.1. 双位运算符 `~~`

可以使用双位操作符来替代 `Math.floor()`。双否定位操作符的优势在于它执行相同的操作运行速度更快。

```js
Math.floor(4.9) === 4;  // true

// 简写为：
~~4.9 === 4;  // true
```

不过要注意，对整数来说 `~~` 运算结果与 `Math.floor()` 运算结果相同，而对于负数来说不相同：

```js
~~4.5;  // 4
Math.floor(4.5);  // 4

~~-4.5;  // -4
Math.floor(-4.5);  // -5
```

### 3.2. 短路运算符

#### 3.2.1. 常规用法 - 逻辑判断

逻辑与 `&&` 与逻辑或 `||` 是短路运算符。短路运算符就是从左到右的运算中前者满足要求，就不再执行后者了

- `&&` 为取假运算，从左到右依次判断，如果遇到一个假值，就返回假值，以后不再执行，否则返回最后一个真值
- `||` 为取真运算，从左到右依次判断，如果遇到一个真值，就返回真值，以后不再执行，否则返回最后一个假值

```js
let param1 = expr1 && expr2;
let param2 = expr1 || expr2;
```

#### 3.2.2. 其他用法

- 变量赋初值。如下例：如果variable1是真值就直接返回了，后面短路就不会被返回了，如果为假值，则会返回后面的 foo

```js
let variable1;
let variable2 = variable1 || "foo";
```

- 也可以用来进行简单的判断，取代冗长的if语句。如下例：如果 param 如果为真值则返回 `param.prop` 属性，否则返回 param 这个假值，这样在某些地方防止 param 为 `undefined` 的时候还取其属性造成报错。

```js
let variable = param && param.prop;
```

## 4. 函数

### 4.1. 函数的定义与语法格式

#### 4.1.1. 函数的定义

- 第1种方式：全名函数

```js
function 函数名(参数列表) {
    代码块;
    // 如果有返回值就加return
    [return 返回值;]
}
```

- 第2种方式：匿名函数

```js
var 变量名 = function (参数列表) {
    代码块;
    // 如果有返回值就加return
    [return 返回值;]
}
```

示例

```js
// 方式一 定义函数
function sayHi(name){
    // 弹出个新窗口显示 window可能省略
    window.alert("和 " + name + " 打个鸟招呼！");
}

// 定义一个name变量
var realName = "birdMan";
// 调用函数 或者直接sayHi("birdMan");
sayHi(realName);

// 方式二 匿名函数
var sayHi2 = function (name){
    window.alert("和 " + name + " 打第2个鸟招呼！");
}

// 调用函数，输入形式变量
sayHi2("鸟人");
```

#### 4.1.2. 参数类型与返回值

1. 形参的类型。在定义函数时，形参的类型是不需要指定的，因是可以变化的类型
2. 函数的返回值。function前面没有返回类型，如果有返回值，使用return，如果没有返回值则不用写。

#### 4.1.3. 函数的重载

在JS中没有函数的重载，后面定义的同名函数会覆盖前面的函数，只有最后一个函数起作用。

形参的个数与实参个数没有关系

#### 4.1.4. arguments隐式数组对象

在每一个函数的内部都有一个隐式的数组对象，名字叫arguments，用来保存用户提交的实际参数。函数运行的时候形参从数组中去取值，再进行运算。

实参个数与形参个数无关

- 调用函数时，隐式数组的执行流程
1. 调用的时候，先会将形参的值赋值给arguments数组
2. 执行的时候，实参从arguments数组中按顺序取出值，再进行计算

<font color=red>**结论：函数的形参的个数与调用时实参的个数不用相等，可以不同。建议还是写成相同的个数。**</font>

Code Demo: 输出arguments的长度和数组中的每个元素

```js
// 输出隐式数组
function innerArray(n1,n2){
    // 输出函数中的数组
    document.write("数组的长度是：" + arguments.length + "<br/>");
    // 输出数组中每个元素
    for (var i = 0; i<arguments.length; i++) {
        document.write(arguments[i] + "&nbsp;");
    }

    document.write("<br/>");
    document.write("n1 = " + n1 + "<br/>");
    document.write("n2 = " + n2 + "<br/>");
}

// 调用函数
innerArray(10);
innerArray(10,20);
innerArray(10,20,30);
```

输出结果：

```
数组的长度是：1
10
n1 = 10
n2 = undefined
数组的长度是：2
10 20
n1 = 10
n2 = 20
数组的长度是：3
10 20 30
n1 = 10
n2 = 20
```

### 4.2. 函数内 this 的指向

函数内 this 的指向，是当调用函数的时候确定的。调用方式的不同决定了this 的指向不同。一般指向调用者

![](images/20200823171520434_1035.png)

```js
// 函数的不同调用方式决定了this 的指向不同
// 1. 普通函数 this 指向window
function fn() {
  console.log("普通函数的this" + this)
}
window.fn()
// 2. 对象的方法 this指向的是对象 o
var o = {
  sayHi: function () {
    console.log("对象方法的this:" + this)
  },
}
o.sayHi()
// 3. 构造函数 this 指向 moon 这个实例对象原型对象里面的this 指向的也是moon这个实例对象
function Star() {}
Star.prototype.sing = function () {}
var moon = new Star()
// 4. 绑定事件函数 this 指向的是函数的调用者 btn这个按钮对象
var btn = document.querySelector("button")
btn.onclick = function () {
  console.log("绑定时间函数的this:" + this)
}
// 5. 定时器函数 this 指向的也是window
window.setTimeout(function () {
  console.log("定时器的this:" + this)
}, 1000)
// 6. 立即执行函数 this还是指向window
;(function () {
  console.log("立即执行函数的this" + this)
})()
```

JavaScript 专门提供了一些函数方法来处理函数内部 this 的指向问题，常用的有 bind()、call()、apply() 三种方法

#### 4.2.1. call 方法

语法：

```js
fun.call(thisArg, arg1, arg2, ...)
```

- `thisArg`：在 fun 函数运行时指定的 this 值
- `arg1，arg2`：传递的其他参数
- 返回值就是函数的返回值，因为它就是调用函数

```js
// 改变函数内this指向，js提供了三种方法：call()  apply()  bind()
// 1. call()方法
var o = {
  name: "MooN",
}
function fn(a, b) {
  console.log(this)
  console.log(a + b)
}
// call 第一个可以调用函数 第二个可以改变函数内的this 指向
fn.call(o, 1, 2)
// call 的主要作用可以实现继承
function Father(uname, age, sex) {
  this.uname = uname
  this.age = age
  this.sex = sex
}
function Son(uname, age, sex) {
  Father.call(this, uname, age, sex)
}
var son = new Son("路飞", 18, "男")
console.log(son)
```

#### 4.2.2. apply 方法

`apply()` 方法调用一个函数。简单理解为调用函数的方式，但是它可以改变函数的 this 指向。语法如下：

```js
fun.apply(thisArg, [argsArray])
```

- `thisArg`：在fun函数运行时指定的 this 值
- `argsArray`：传递的值，必须包含在数组里面
- 返回值就是函数的返回值，因为它就是调用函数
- `apply()` 函数主要跟数组有关系，比如使用 `Math.max()` 求数组的最大值

```js
// 改变函数内this指向  js提供了三种方法  call()  apply()  bind()
// 2. apply()  应用 运用的意思
var o = {
  name: "MooN",
}
function fn(arr) {
  console.log(this)
  console.log(arr) // 'pink'
}
fn.apply(o, ["pink"])
// 1. 也是调用函数 第二个可以改变函数内部的this指向
// 2. 但是他的参数必须是数组(伪数组)
// 3. apply 的主要应用 比如说可以利用 apply 借助于数学内置对象求数组最大值
// Math.max();
var arr = [1, 66, 3, 99, 4]
var arr1 = ["red", "pink"]
// var max = Math.max.apply(null, arr);
var max = Math.max.apply(Math, arr)
var min = Math.min.apply(Math, arr)
console.log(max, min)
```

#### 4.2.3. bind 方法

`bind()` 方法不会调用函数。但是能改变函数内部 this 指向。语法如下：

```js
fun.bind(thisArg, arg1, arg2, ...)
```

- `thisArg`：在 fun 函数运行时指定的 this 值
- `arg1，arg2`：传递的其他参数
- 返回由指定的 this 值和初始化参数改造的原函数拷贝
- 因此当只是想改变 this 指向，并且不想调用这个函数的时候，可以使用 `bind()`

```js
// 改变函数内this指向  js提供了三种方法  call()  apply()  bind()
// 3. bind()方法 绑定
var o = {
  name: "MooN",
}
function fn(a, b) {
  console.log(this)
  console.log(a + b)
}
var f = fn.bind(o, 1, 2)
f()
// 1. 不会调用原来的函数，可以改变原来函数内部的this 指向
// 2. 返回的是原函数改变this之后产生的新函数
// 3. 如果有的函数不需要立即调用,但是又想改变这个函数内部的this指向此时用bind
// 示例：有一个按钮,当点击了之后,就禁用这个按钮,3秒钟之后开启这个按钮
/* var btn1 = document.querySelector("button")
btn1.onclick = function () {
  this.disabled = true // 这个this 指向的是 btn 这个按钮
  // var that = this;
  setTimeout(
    function () {
      // that.disabled = false; // 定时器函数里面的this 指向的是window
      this.disabled = false // 此时定时器函数里面的this 指向的是btn
    }.bind(this),
    3000
  ) // 这个this 指向的是btn 这个对象
} */
// 多个按钮
var btns = document.querySelectorAll("button")
for (var i = 0; i < btns.length; i++) {
  btns[i].onclick = function () {
    this.disabled = true
    setTimeout(
      function () {
        this.disabled = false
      }.bind(this),
      2000
    )
  }
}
```

#### 4.2.4. 改变函数内部 this 指向三种方式总结

- 相同点：
    - 都可以改变函数内部的this指向
- 区别点：
    - call 和 apply 会调用函数，并且改变函数内部this指向
    - call 和 apply 传递的参数不一样，call 传递参数 `arg1, arg2..`形式。apply 必须数组形式[arg]
    - bind 不会调用函数，可以改变函数内部this指向
- 主要应用场景：
    - `call`：经常做继承
    - `apply`：经常跟数组有关系。比如借助于数学对象实现数组最大值最小值
    - `bind`：不调用函数，但是还想改变this指向/比如改变定时器内部的this指向















## 5. JavaScript 的对象

类就是对象的模板，对象就是类的实例。在 ES6之前，JS 中并没用引入类的概念。

在 ES6之前，对象不是基于类创建的，而是用一种称为<font color=red>**构建函数**</font>的特殊函数来定义对象和它们的特征。

创建对象可以通过以下三种方式：

1. 对象字面量。例如：`var obj2 = {}`
2. `new Object()`。例如：`var obj1 = new Object()`
3. 自定义构造函数

### 5.1. 构造函数

**构造函数**是一种特殊的函数，主要用来初始化对象，即为对象成员变量赋初始值，它总与 `new` 关键字一起使用。可以把对象中一些公共的属性和方法抽取出来，然后封装到这个函数里面

```js
// ES6之前，利用构造函数创建对象
function Star(uname, age) {
  this.uname = uname
  this.age = age
  this.sing = function () {
    console.log(this.uname + "会唱歌")
  }
}
var shiyuan = new Star("石原里美", 18)
var jieye = new Star("新垣结衣", 20)
console.log(shiyuan)
shiyuan.sing()
jieye.sing()
```

在 JS 中，使用构造函数时要注意以下两点：

- 构造函数用于创建某一类对象，<font color=red>**其首字母要大写**</font>
- <font color=red>**构造函数要和`new`一起使用**</font>才有意义

**new 对象在执行时会做四件事情**：

1. 在内存中创建一个新的空对象。
2. 让 `this` 指向这个新的对象。
3. 执行构造函数里面的代码，给这个新对象添加属性和方法。
4. 返回这个新对象（所以构造函数里面不需要 return ）。

### 5.2. 静态成员和实例成员

JavaScript 的构造函数中可以添加一些成员，可以在构造函数本身上添加，也可以在构造函数内部的 `this` 上添加。通过这两种方式添加的成员，就分别称为静态成员和实例成员。

- **静态成员**：在构造函数本上添加的成员称为静态成员，<font color=red>**只能由构造函数本身来访问**</font>
- **实例成员**：在构造函数内部创建的对象成员称为实例成员，<font color=red>**只能由实例化的对象来访问**</font>

```js
// 构造函数中的属性和方法称为成员, 成员可以添加
function Star(uname, age) {
  this.uname = uname
  this.age = age
  this.sing = function () {
    console.log("我会唱歌")
  }
}
var shiyuan = new Star("石原里美", 18)
// 1.实例成员就是构造函数内部通过this添加的成员 uname age sing 就是实例成员
// 实例成员只能通过实例化的对象来访问
console.log(shiyuan.uname)
shiyuan.sing()
// console.log(Star.uname); // 不可以通过构造函数来访问实例成员
// 2. 静态成员在构造函数本身直接添加的成员，就是静态成员。比较这个sex属性
Star.sex = "女"
console.log(Star.sex) // 静态成员只能通过构造函数来访问
console.log(ldh.sex) // 不能通过对象来访问
```

### 5.3. 原型

#### 5.3.1. 构造函数原型 prototype

JavaScript 规定，<font color=red>**每一个构造函数都有一个 `prototype` 属性**</font>，指向另一个对象。注意这个 `prototype` 就是一个对象，这个对象的所有属性和方法，都会被构造函数所拥有，所有通过此构造函数生成的对象都共享此`prototype`对象。

所以可以把那些不变的方法，直接定义在 `prototype` 对象上，这样所有对象的实例就可以共享这些方法。

```js
// 1. 构造函数的问题.
function Star(uname, age) {
  this.uname = uname
  this.age = age
  // 直接定义到构造函数上的函数，会造成浪费内存空间的问题
  /* this.sing = function () {
    console.log("我会唱歌")
  } */
}
/*
  2. 一般情况下，的公共属性定义到构造函数里面, 公共的方法放到原型对象身上
      这样可以使所有实例都共享这些公共的方法，节省内存空间
*/
Star.prototype.sing = function () {
  console.log("我会唱歌")
}
var shiyuan = new Star("石原里美", 18)
var jieye = new Star("新垣结衣", 20)
console.log(shiyuan.sing === jieye.sing)
console.dir(Star); // 通过输入到控制台可以看到构造函数中的prototype属性对象
shiyuan.sing()
jieye.sing()
```

#### 5.3.2. 对象原型 __proto__

<font color=red>**对象都会有一个属性 `__proto__` 指向构造函数的 `prototype` 原型对象**</font>

- `__proto__`对象原型和原型对象 `prototype` 是等价的
- `__proto__`对象原型的意义就在于为对象的查找机制提供一个方向，或者说一条路线，但是它是一个非标准属性，因此实际开发中，不可以使用这个属性，它只是内部指向原型对象 `prototype`

```js
function Star(uname, age) {
  this.uname = uname
  this.age = age
}
Star.prototype.sing = function () {
  console.log("我会唱歌")
}
var shiyuan = new Star("石原里美", 18)
shiyuan.sing()
console.log(shiyuan) // 对象身上系统自己添加一个 __proto__ 指向构造函数的原型对象 prototype
console.log(shiyuan.__proto__ === Star.prototype)
/*
  方法的查找规则: 首先先看实例对象身上是否有 sing 方法,如果有就执行这个对象上的sing
  如果么有sing 这个方法,因为有__proto__ 的存在,就去构造函数原型对象prototype身上去查找sing这个方法
 */
```

#### 5.3.3. 构造函数 constructor

对象原型（`__proto__`）和构造函数（`prototype`）原型对象里面都有一个属性 `constructor` 属性 `，constructor` 称为构造函数，因为它指回构造函数本身。

`constructor` 主要用于记录该对象引用于哪个构造函数，它可以让原型对象重新指向原来的构造函数

<font color=red>**一般情况下，对象的方法都在构造函数的原型对象中设置**</font>。如果有多个对象的方法，可以给原型对象采取对象形式赋值，但是这样就会覆盖构造函数原型对象原来的内容，这样修改后的原型对象 `constructor`  就不再指向当前构造函数了。此时，我们可以在修改后的原型对象中，添加一个 `constructor` 指向原来的构造函数。

```js
function Star(uname, age) {
  this.uname = uname
  this.age = age
}
// 很多情况下,需要手动的利用constructor 这个属性指回 原来的构造函数
/* Star.prototype.sing = function() {
    console.log('我会唱歌');
};
Star.prototype.movie = function() {
    console.log('我会演电影');
} */
Star.prototype = {
  // 如果修改了原来的原型对象，给原型对象赋值的是一个对象，则必须手动的利用constructor指回原来的构造函数
  constructor: Star,
  sing: function () {
    console.log("我会唱歌")
  },
  movie: function () {
    console.log("我会演电影")
  },
}
var shiyuan = new Star("石原里美", 18)
console.log(Star.prototype)
console.log(shiyuan.__proto__)
console.log(Star.prototype.constructor)
console.log(shiyuan.__proto__.constructor)
```

#### 5.3.4. 构造函数、实例、原型对象三者之间的关系

![](images/20200823105900008_3346.png)

#### 5.3.5. 原型链

![](images/20200823105941956_20146.png)

```js
function Star(uname, age) {
  this.uname = uname
  this.age = age
}
Star.prototype.sing = function () {
  console.log("我会唱歌")
}
var shiyuan = new Star("石原里美", 18)
// 1. 只要是对象就有__proto__ 原型, 指向原型对象
console.log(Star.prototype)
console.log(Star.prototype.__proto__ === Object.prototype)
// 2.我们Star原型对象里面的__proto__原型指向的是 Object.prototype
console.log(Object.prototype.__proto__)
// 3. 我们Object.prototype原型对象里面的__proto__原型  指向为 null
```

### 5.4. JavaScript 的成员查找机制(规则)

1. 当访问一个对象的属性（包括方法）时，首先查找这个对象自身有没有该属性。
2. 如果没有就查找它的原型（也就是 `__proto__` 指向的 `prototype` 原型对象）。
3. 如果还没有就查找原型对象的原型（Object的原型对象）。
4. 依此类推一直找到 Object 为止（null）。
5. `__proto__`对象原型的意义就在于为对象成员查找机制提供一个方向，或者说一条路线。

```js
function Star(uname, age) {
  this.uname = uname
  this.age = age
}
Star.prototype.sing = function () {
  console.log("我会唱歌")
}
// Object.prototype.sex = '男';
Star.prototype.sex = "男"
var shiyuan = new Star("石原里美", 18)
shiyuan.sex = "女"
console.log(shiyuan.sex)
console.log(Object.prototype)
console.log(shiyuan)
console.log(Star.prototype)
console.log(shiyuan.toString())
```

### 5.5. 原型对象this指向

- 构造函数中的 `this` 指向实例对象
- **原型对象**里面放的是方法，这个方法里面的 `this` 指向的是这个方法的调用者，也就是这个实例对象

```js
function Star(uname, age) {
  this.uname = uname
  this.age = age
}
var that
Star.prototype.sing = function () {
  console.log("我会唱歌")
  that = this
}
var shiyuan = new Star("石原里美", 18)
// 1. 在构造函数中，里面this指向的是对象实例
shiyuan.sing()
// 2.原型对象函数里面的 this 指向的是实例对象
console.log(that === shiyuan)
```

### 5.6. 扩展 JS 内置对象

可以通过原型对象，对JS原来的内置对象进行扩展自定义的方法。

```js
// 原型对象的应用 扩展内置对象方法
Array.prototype.sum = function () {
  var sum = 0
  for (var i = 0; i < this.length; i++) {
    sum += this[i]
  }
  return sum
}
// 使用此方式扩展内置对象，会覆盖原来其他方法。
/* Array.prototype = {
  sum: function () {
    var sum = 0
    for (var i = 0; i < this.length; i++) {
      sum += this[i]
    }
    return sum
  },
} */
var arr = [1, 2, 3]
console.log(arr.sum())
console.log(Array.prototype)
var arr1 = new Array(11, 22, 33)
console.log(arr1.sum())
```

> 注意：数组和字符串内置对象不能给原型对象覆盖操作` Array.prototype = {}` ，只能是 `Array.prototype.xxx = function(){}` 的方式。

### 5.7. 对象常用方法

#### 5.7.1. Object.assign() 对象属性复制，浅拷贝

- 使用Object.assign方法可以浅克隆一个对象：

```js
let cloneObj = Object.assign({}, targetObj)
```

- 简单的深克隆可以使用`JSON.parse()`和`JSON.stringify()`，这两个api是解析json数据的，所以**只能解析除symbol外的原始类型及数组和对象**

```js
let cloneObj = JSON.parse(JSON.stringify(targetObj))
```

#### 5.7.2. Object.keys() 获取对象全部属性

`Object.keys()` 用于获取对象自身所有的属性名，返回对象所有属性名的数组

```js
Object.keys(obj);
```

示例：

```js
// 用于获取对象自身所有的属性
var obj = {
  id: 1,
  pname: "小米",
  price: 1999,
  num: 2000,
}
var arr = Object.keys(obj)
console.log(arr)
arr.forEach(function (value) {
  console.log(value)
})
```

#### 5.7.3. Object.values() 获取对象全部（属性）值

`Object.values(对象)`方法作用是，获取对象内的所有值，返回值的数组。

```js
const user = {
  name: "Alex",
  age: 23,
  isOnline: false
}

console.log(Object.values(user));
// output: ["Alex", 23, false]
```

#### 5.7.4. Object.entries() 获取对象的键值

`Object.entries(对象)`方法的作用是同时获取对象的键和值，返回一个键和值的多维数组。

```js
const user = {
  name: "Alex",
  age: 23,
  isOnline: false
}

console.log(Object.entries(user));
// output: [["name", "Alex"], ["age", 23], ["isOnline", false]]
```

#### 5.7.5. Object.defineProperty() 定义属性

`Object.defineProperty()` 定义对象中新属性或修改原有的属性。语法如下：

```js
Object.defineProperty(obj, prop, descriptor)
```

- `obj`：必需。目标对象
- `prop`：必需。需定义或修改的属性的名字
- `descriptor`：必需。目标属性所拥有的特性。以对象形式`{}`书写
    - `value`：设置属性的值，默认为undefined
    - `writable`：值是否可以重写。取值：true|false，默认为false
    - `enumerable`：目标属性是否可以被枚举。取值：true|false，默认为 false
    - `configurable`：目标属性是否可以被删除或是否可以再次修改特性。取值：true|false，默认为false

```js
// Object.defineProperty() 定义新属性或修改原有的属性
var obj = {
  id: 1,
  pname: "小米",
  price: 1999,
}
// 1. 以前的对象添加和修改属性的方式
// obj.num = 1000;
// obj.price = 99;
// console.log(obj);
// 2. Object.defineProperty() 定义新属性或修改原有的属性
Object.defineProperty(obj, "num", {
  value: 1000,
  enumerable: true,
})
console.log(obj)
Object.defineProperty(obj, "price", {
  value: 9.9,
})
console.log(obj)
Object.defineProperty(obj, "id", {
  // 如果值为false 不允许修改这个属性值 默认值也是false
  writable: false,
})
obj.id = 2
console.log(obj)
Object.defineProperty(obj, "address", {
  value: "中国山东蓝翔技校xx单元",
  // 如果只为false 不允许修改这个属性值 默认值也是false
  writable: false,
  // enumerable 如果值为false 则不允许遍历, 默认的值是 false
  enumerable: false,
  // configurable 如果为false 则不允许删除这个属性 不允许在修改第三个参数里面的特性 默认为false
  configurable: false,
})
console.log(obj)
console.log(Object.keys(obj))
delete obj.address
console.log(obj)
delete obj.pname
console.log(obj)
Object.defineProperty(obj, "address", {
  value: "中国山东蓝翔技校xx单元",
  // 如果值为false 不允许修改这个属性值 默认值也是false
  writable: true,
  // enumerable 如果值为false 则不允许遍历, 默认的值是 false
  enumerable: true,
  // configurable 如果为false 则不允许删除这个属性 默认为false
  configurable: true,
})
console.log(obj.address)
```

#### 5.7.6. Object.freeze() 防止对象数据修改

`Object.freeze(对象)`方法作用是防止对象中的数据突变。即不能将`Object.freeze()`作为参数传递给对象，更不能添加，更新或删除对象的属性。

```js
const employee = {
  name: "James",
  age: 25,
  available: true
}

// Freezing the object.
Object.freeze(employee);
// updating and adding properties.
employee.name = "Brad";
employee.newProp = "Hard Worker";
console.log(employee);
// Output: {name: "James", age: 25, available: true}
```

#### 5.7.7. Object.seal() 防止对象增加属性

`Object.seal()`方法有点类似于`Object.freeze()`，它可以防止向对象添加新属性，但是可以更改和更新现有属性。

```js
const user = {
  name: "Alex",
  age: 23,
  isOnline: false
}

// 使用Object.seal（）
Object.seal(user);
// 更新属性。
user.isOnline = true;
// 添加一个属性。
user.active = false;
console.log(user);
// 输出：{名称：“ Alex”，年龄：23，isOnline：true}
```

> 示例中：该对象的`isOnline`属性值已更新，但是无法将新的`active`属性添加到对象中

#### 5.7.8. Object.create() 创建一个新对象

`Object.create()`作用是，从另一个现有对象的原型创建一个新对象。

```js
const user = {
  firstName: "John",
  lastName: "Doe",
  age: 25,
  fullName(){
    return `${this.firstName} ${this.lastName}`;
  }
}

// 新对象
let newObject = Object.create(user);
// 更新属性
newObject.firstName = "Mehdi";
newObject.lastName = "Aoussiad";
// 也可以在此新对象中使用user的fullName方法。
newObject.fullName();  // 输出：Mehdi Aoussiad
console.log(newObject);
// 输出：{firstName：“ Mehdi”，lastName：“ Aoussiad”}
```


## 6. 继承（ES6以前的实现方式）

ES6之前并没有提供 `extends` 继承。但可以<font color=red>**通过构造函数+原型对象模拟实现继承，被称为组合继承**</font>

### 6.1. call() 函数

通过`call()`去调用某个函数, 并且修改函数运行时的 `this` 指向

```js
fun.call(thisArg, arg1, arg2, ...)
```

- `thisArg`：当前调用函数 this 的指向对象
- `arg1，arg2`：传递的其他参数

```js
// call 方法
function fn(x, y) {
  console.log("我想喝手磨咖啡")
  console.log(this)
  console.log(x + y)
}
var o = {
  name: "斩月",
}
// 直接调用函数
// fn();
// 1. call() 可以调用函数
// fn.call();
// 2. call() 可以改变这个函数的this指向 此时这个函数的this 就指向了o这个对象
fn.call(o, 1, 2)
```

### 6.2. 借用构造函数继承父类型属性

核心原理：通过 `call()` 把父类型的this指向子类型的this，这样就可以实现子类型继承父类型的属性。

```js
// 借用父构造函数继承属性
// 1. 父构造函数
function Father(uname, age) {
  // this 指向父构造函数的对象实例
  this.uname = uname
  this.age = age
}
// 2 .子构造函数
function Son(uname, age, score) {
  // this 指向子构造函数的对象实例
  Father.call(this, uname, age)
  this.score = score
}
var son = new Son("敌法师", 18, 100)
console.log(son)
```

### 6.3. 借用原型对象继承父类型方法

<font color=red>**一般情况下，对象的方法都在构造函数的原型对象中设置，通过构造函数无法继承父类方法**</font>。实现的核心原理：

1. 将子类所共享的方法提取出来，让子类的 `prototype 原型对象 = new 父类()`
2. 本质：子类原型对象等于是实例化父类，因为父类实例化之后另外开辟空间，就不会影响原来父类原型对象
3. 将子类的 constructor 从新指向子类的构造函数

```js
// 借用父构造函数继承属性
// 1. 父构造函数
function Father(uname, age) {
  // this 指向父构造函数的对象实例
  this.uname = uname
  this.age = age
}
Father.prototype.money = function () {
  console.log(100000)
}
// 2 .子构造函数
function Son(uname, age, score) {
  // this 指向子构造函数的对象实例
  Father.call(this, uname, age)
  this.score = score
}
// 这样直接赋值会有问题，如果修改了子原型对象，父原型对象也会跟着一起变化
// Son.prototype = Father.prototype
Son.prototype = new Father()
// 如果利用对象的形式修改了原型对象,别忘了利用constructor 指回原来的构造函数
Son.prototype.constructor = Son
// 这个是子构造函数专门的方法
Son.prototype.exam = function () {
  console.log("孩子要考试")
}
var son = new Son("剑圣主宰", 18, 100)
console.log(son)
console.log(Father.prototype)
console.log(Son.prototype.constructor)
```









## 7. 数组对象Array

在JS中，数组等价于集合

### 7.1. 创建数组的方式
#### 7.1.1. 一维数组

1. 创建一个长度为0的数组

```js
var arr = new Array();
```

2. 创建一个长度为num的数组

```js
var arr = new Array(num);
```

3. 创建一个指定元素的数组

```js
var arr = new Array(x1,x2,x3……);
```

4. 使用中括号创建数组，指定数组中的每个元素

```js
var arr = [x1,x2,x3,……];
```

#### 7.1.2. 二维数组

```js
var arr = [[x,x,x],[x,x,x],[x,x],……];
```

- 值：`arr[0] = [x,x,x]`
- 使用嵌套循环遍历

#### 7.1.3. Array.of() 方法

`Array.of()`方法用于将一组值，转换为数组。这个方法的主要目的，是弥补数组构造函数`Array()`的不足。因为参数个数的不同，会导致`Array()`的行为有差异。

```js
/* new Array()的构造方法创建数组
 *    Array方法没有参数、一个参数、三个参数时，返回结果都不一样。
 *    只有当参数个数不少于 2 个时，Array()才会返回由参数组成的新数组。参数个数只有一个时，实际上是指定数组的长度。
 */
Array() // []
Array(3) // [, , ,]
Array(3, 11, 8) // [3, 11, 8]

// 使用Array.of()方法创建数组
Array.of(3, 11, 8) // [3,11,8]
Array.of(3) // [3]
Array.of(undefined)    // [ undefined ]
Array.of(3).length // 1
```

- `Array.of()`方法基本上可以用来替代`Array()`或`new Array()`，并且不存在由于参数不同而导致的重载。它的行为非常统一。
- `Array.of()`方法总是返回参数值组成的数组。如果没有参数，就返回一个空数组。

### 7.2. 数组中元素的类型

1. 数组中的元素类型可以各不相同
2. 数组的长度可以动态增长

```js
var arr = [1, 3, 2];
arr[2] = "hello";
arr[3] = new Date();
arr[5] = 99;
```

- 上面的案例最后的结果是：如果跳过增加，则数组也会自动在空的位置加上undefined
- 数组的长度是6
- 数组的内容是`[1, 3, "hello", 时间对象, undefined, 99]`

### 7.3. 数组的遍历
#### 7.3.1. for...of 循环

- 语法：`for(const item of array)`
- 作用：循环遍历数组所有元素项
- > 注：此方式可以随时使用`break`语句停止遍历

```js
const colors = ['blue', 'green', 'white'];

for (const color of colors) {
    console.log(color);
}
// 'blue'
// 'green'
// 'white'
```

#### 7.3.2. for 循环

- 语法：`for(let i; i < array.length; i++)`
- 作用：循环使用递增的索引变量的方式遍历数组所有元素项，index变量从0递增到`colors.length-1`
- > 注：此方式可以随时使用`break`语句停止遍历

```js
const colors = ['blue', 'green', 'white'];

for (let index = 0; index < colors.length; index++) {
    const color = colors[index];
    console.log(color);
}
// 'blue'
// 'green'
// 'white'
```

#### 7.3.3. forEach()方法

- 语法：

```js
array.forEach(callback(item[, index[, array]]))
```

- 参数callback函数，该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- 作用：forEach是Array新方法中最基本的一个，就是遍历，循环。对数组中的每一项元素调用callback函数，执行相关逻辑来遍历数组所有元素项
- forEach方法与map方法的区别是：forEach不返回值，只用来操作数据。

```js
var arr = [1,2,3];
arr.forEach((item, index) => {
    console.log(item, index);
    // 也可以有其他的操作。
})
```

> 注：`array.forEach()`迭代中，不能使用`break`来中断操作

### 7.4. 数组的映射

#### 7.4.1. map()方法 - ES6新特性

- 语法：`map(callback(item[, index[, array]]))`
    - 参数callback函数，该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- 作用：接收一个函数，将原数组中的所有元素用这个函数处理后，创建新的数组返回。
- > 注：`array.map()`创建一个新的映射数组，而不改变原始数组。

例：有一个字符串数组，将其转为int数组

```js
let arr = ['1', '20', '-5', '3'];
console.log(arr)

let newArr = arr.map(s => parseInt(s));
console.log(newArr)
```

![数组的map方法](images/20190421103633831_23300.png)

#### 7.4.2. Array.from()方法

`Array.from()`方法就是将一个**类数组对象**或者**可遍历对象(包括ES6新增的数据结构Set和Map)**转换成一个真正的数组。

**类数组对象**，最基本的要求就是具有length属性的对象。

##### 7.4.2.1. 语法

```js
Array.from(arrayLike[, mapFunction[, thisArg]])
```

- arrayLike：必传参数，想要转换成数组的伪数组对象或可迭代对象。
- mapFunction：可选参数，`mapFunction(item，index){…}` 是在集合中的每个项目上调用的函数。返回的值将插入到新集合中。
- thisArg：可选参数，执行回调函数 mapFunction 时 this 对象。这个参数很少使用。

简单的示例

```js
const someNumbers = { '0': 10, '1': 15, length: 2 };
Array.from(someNumbers, value => value * 2); // => [20, 30]
```

> 注：
>
> - `Array.from()`创建一个新的映射数组，而不改变原始数组。
> - `Array.from()`更适合从类似数组的对象进行映射。

##### 7.4.2.2. 用法1：将类数组对象转换为真正数组

```js
let arrayLike = {
    0: 'tom',
    1: '65',
    2: '男',
    3: ['jane', 'john', 'Mary'],
    'length': 4
}

// ES5的写法
let arrEs5 = [].slice.call(arrayLike);

// ES6的写法
let arr = Array.from(arrayLike)
console.log(arr)    // ['tom', '65', '男', ['jane','john','Mary']]
```

```js
// 如果将上面代码中length属性去掉，将会是一个长度为0的空数组
let arrayLike = {
    'name': 'tom',
    'age': '65',
    'sex': '男',
    'friends': ['jane','john','Mary'],
}
let arr = Array.from(arrayLike)
console.log(arr)  // [ ]
```

```js
// 如果具有length属性，但是对象的属性名不再是数字类型的，而是其他字符串型的。输出结果是长度为4，元素均为undefined的数组
let arrayLike = {
    'name': 'tom',
    'age': '65',
    'sex': '男',
    'friends': ['jane','john','Mary'],
    length: 4
}
let arr = Array.from(arrayLike)
console.log(arr)  // [ undefined, undefined, undefined, undefined ]
```

- **总结，将一个类数组对象转换为一个真正的数组，必须具备以下条件：**
    1. 该类数组对象必须具有length属性，用于指定数组的长度。如果没有length属性，那么转换后的数组是一个空数组
    2. 该类数组对象的属性名必须为数值型或字符串型的数字
- ps: 该类数组对象的属性名可以加引号，也可以不加引号

##### 7.4.2.3. 用法2：将Set结构的数据转换为真正的数组

```js
let arr = [12,45,97,9797,564,134,45642]
let set = new Set(arr)
console.log(Array.from(set))  // [ 12, 45, 97, 9797, 564, 134, 45642 ]
```

Array.from还可以接受第二个参数，作用类似于数组的map方法，用来对每个元素进行处理，将处理后的值放入返回的数组。

```js
let arr = [12,45,97,9797,564,134,45642]
let set = new Set(arr)
console.log(Array.from(set, item => item + 1)) // [ 13, 46, 98, 9798, 565, 135, 45643 ]
```

##### 7.4.2.4. 用法3：将字符串转换为数组

```js
let str = 'hello world!';
console.log(Array.from(str)) // ["h", "e", "l", "l", "o", " ", "w", "o", "r", "l", "d", "!"]s
```

##### 7.4.2.5. Array.from参数是一个真正的数组

Array.from会返回一个一模一样的新数组

```js
console.log(Array.from([12,45,47,56,213,4654,154]))
```

### 7.5. 数据的简化
#### 7.5.1. reduce()方法 - ES6新特性

- 语法：`array.reduce(callback(accumulator, item[, index[, array]])[, initialValue])`
    - 接收一个函数callback（必须）和一个初始值initialValue（可选）
    - callback函数接收4个参数：
        - accumulator（必选）：第一个参数是上一次 reduce处理的结果
        - item（必选）：第二个参数是数组中要处理的下一个元素
        - index（可选）：当前元素的索引
        - array（可选）：当前元素所属的数组对象
- 作用：将数组进行处理返回一个值，可以理解为对数组的所有元素项进行累积操作
- 具体流程：`reduce()` 会从左到右依次把数组中的元素用reduce处理，并把处理的结果作为下次reduce的第一个参数。如果是第一次，会把前两个元素作为计算参数，或者把用户指定的初始值作为起始参数
- > 注：如果没有使用 initialValue 来设置初始值，则默认使用数组的第一个元素作为初始值。

```js
const arr = [1, 20, -5, 3];

/* 没有初始值 */
let result1 = arr.reduce((a, b) => a + b);        // 结果：19
let result2 = arr.reduce((a, b) => a * b);        // 结果：-300
/* 指定初始值 */
let result3 = arr.reduce((a, b) => a * b, 0);     // 结果：-0
let result4 = arr.reduce((a, b) => a * b, 1);     // 结果：-300
let result5 = arr.reduce((a, b) => a * b, -1);    // 结果：300
```

### 7.6. 数据的连接
#### 7.6.1. concat()方法

- 语法：`array.concat(array1[, array2, ...])`
- 作用：连接两个或更多的数组，或者给数组增加元素，并返回结果。一般用于数组的拼接和增加元素操作
- > 注：concat方法是创建一个新的数组，而不改变原来的数组

```js
const arr1 = [1, 2];
const arr2 = ['moon', 'hehe'];

const concatArray = arr1.concat(arr2);
console.log(concatArray); // 1, 2, moon, hehe
```

#### 7.6.2. 展开操作符(...)

- 语法：`[...array1, ...array2]`
- 作用：可以使用展开操作符与数组变量一起使用来连接数组，与`concat()`方法效果一样

```js
const arr1 = [1, 2];
const arr2 = ['moon', 'hehe'];

const concatArray = [...arr1, ...arr2];
console.log(concatArray); // 1, 2, moon, hehe
```

#### 7.6.3. join() 方法 - 将数组转成字符串

- 语法：`array.join(separator分隔符)`
- join方法正好与**字符串的方法`split(分隔符)`（方法将一个字符串切割成一个字符串数组）**相反，将数组通过分隔符，拼成一个字符串。

```js
var a3 = [1, 2, 3]
var str = a3.join("+")
// 输出结果："1+2+3"
```

#### 7.6.4. split() 方法 - 将字符串转成数组

- 语法：`array.split(separator分隔符)`
- 作用：将字符串，按指定的分隔符截取成数组，与join()方法相反

### 7.7. 过滤（获取）数组的部分数据
#### 7.7.1. slice() 方法

- 语法：`array.slice([fromIndex[，toIndex]])`
    - 可选参数fromIndex：截取的开始索引，默认值为0
    - 可选参数toIndex：截取结束索引（不包括），默认值为`array.length`
- 作用：返回数组的一个片段，该片段从fromIndex开始，以toIndex结尾（不包括toIndex本身）
- > **注意，此方法与splice() 方法很相似，但slice()方法是创建一个新数组，不会改变原数组的。**

```js
const names = ["moon", "abc", "kira", "N"]

const heroes = names.slice(0, 2)
const villains = names.splice(2)

console.log(heroes) // ["moon", "abc"]
console.log(villains) // ["kira", "N"]
```

#### 7.7.2. filter()方法

- 语法：

```js
array.filter(predicate(item[, index[, array]]))
```

- 参数predicate函数，该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- 作用：对数组进行筛选出符合条件的项，最终得到一个新的筛选后的数组

```js
var arr = [1, 2, 3, 4];
var newarr = arr.filter((item, idx) => {
    return item % 2 === 0;
})
```

> 注：`array.filter()` 创建一个新数组，而不改变原始数组

##### 7.7.2.1. 扩展：使用Boolean过滤数组中的所有假值

JS中的相关假值：`false`, `null`, `0`, `undefined`, `NaN`。可以使用Boolean构造函数来进行一次转换，快速将数组中假值过滤

```js
const compact = arr => arr.filter(Boolean);
compact([0, 1, false, 2, "", 3, "a", "e" * 23, NaN, "s", 34]);
// [ 1, 2, 3, 'a', 's', 34 ]
```


### 7.8. 数组的拷贝
#### 7.8.1. 方式1：使用展开操作符(...)

- 语法：`const clone = [...array]`
- 作用：复制一个新的数组
- > 注：`[...array]`是创建一个浅拷贝

```js
const colors = ['white', 'black', 'gray'];
const clone = [...colors];

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```

#### 7.8.2. 方式2：[].concat(array)

- 语法：`[].concat(array)`是另一种复制数组的方式
- > 注：`[].concat(array)`是创建一个浅拷贝。

```js
const colors = ['white', 'black', 'gray'];
const clone = [].concat(colors);

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```

#### 7.8.3. 方式3：slice()

- 语法：`array.slice()`，利用slice的参数默认值，进行数组的复制
- > `array.slice()` 创建一个浅拷贝。

```js
const colors = ['white', 'black', 'gray'];
const clone = colors.slice();

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```

### 7.9. 查找数组中元素
#### 7.9.1. includes()方法

- 语法：`Array.includes(itemToSearch[，fromIndex])`
    - 参数itemToSearch：数据包含的元素
    - 参数fromIndex（可选）：默认值为0，表示开始搜索的索引。
- 作用：返回一个布尔值，判断数组是否包含itemToSearch。如数组array包含itemToSearch，则返回true
- Array.includes可以使用在多重判断中

```js
// condition
function test(fruit) {
  if (fruit == 'apple' || fruit == 'strawberry') {
    console.log('red');
  }
}
```

- 如果多个判断条件，则用更多的`||`来拓展条件语句，此时可以使用(Array.includes)重写条件语句。

```js
function test(fruit) {
  const redFruits = ['apple', 'strawberry', 'cherry', 'cranberries'];

  if (redFruits.includes(fruit)) {
    console.log('red');
  }
}
```


#### 7.9.2. find() 和 findIndex() 方法

- 语法：`array.find/findIndex((value, index, arr) => {})`
- 函数参数都是一个查找的回调函数。
    - value：每一次迭代查找的数组元素
    - index：每一次迭代查找的数组元素索引
    - arr：被查找的数组。
- `find()`函数用来查找满足回调函数的**第一个目标元素**，找到就返回该元素，找不到返回undefined
- `findIndex()`函数也是查找目标元素，找到就返回元素的位置（索引），找不到就返回-1

```js
/* 1. 查找元素，返回找到的值，找不到返回undefined */
const arr1 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
var ret1 = arr1.find((value, index, arr) => {
  return value > 4
})

var ret2 = arr1.find((value, index, arr) => {
  return value > 14
})
console.log('%s', ret1)    // 5
console.log('%s', ret2)    // undefined

/* 2. 查找元素，返回找到的index，找不到返回-1 */
var ret3 = arr1.findIndex((value, index, arr) => {
  return value > 4
})

var ret4 = arr1.findIndex((value, index, arr) => {
  return value > 14
})
console.log('%s', ret3)    // 4
console.log('%s', ret4)    // -1
```

#### 7.9.3. indexOf() 方法

- 语法：`array.indexOf(itemToSearch[, fromIndex])`
    - 参数itemToSearch：查找的元素
    - 可选参数fromIndex：表示开始搜索的索引，默认值为0
- 作用：返回array中第一个出现的itemToSearch的索引；如果找不到该项，则返回-1
- 注：`array.findIndex(predicate)`方法是使用predicate函数查找索引的替代方法

```js
const arr = ['a', 'b', 'c', 'd'];
const index = arr.indexOf('b');

console.log(index); // 1
```

#### 7.9.4. every() 方法

- 语法：`array.every(predicate(item[, index[, array]]))`
    - 参数predicate函数，该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- `every()`是对数组中每一项运行给定函数，如果该函数对每一项返回true，则最终返回true。

```js
const fruits = [
    { name: 'apple', color: 'red' },
    { name: 'banana', color: 'yellow' },
    { name: 'grape', color: 'purple' }
];

// 传统处理方式
function test() {
  let isAllRed = true;

  // 条件：所有水果都是红色
  for (let f of fruits) {
    if (!isAllRed) break;
    isAllRed = (f.color == 'red');
  }

  console.log(isAllRed); // false
}

// 使用Array.every()方法判断所有元素都符合条件
function test() {
  const isAllRed = fruits.every(f => f.color == 'red');

  console.log(isAllRed); // false
}
```

#### 7.9.5. some() 方法

- 语法：`array.every(predicate(item[, index[, array]]))`
    - 参数predicate函数，该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- `some()`是对数组中每一项运行给定函数，如果该函数对任一项返回true，则返回true。
- 使用`Array.some`判断部分元素符合条件

```js
const fruits = [
    { name: 'apple', color: 'red' },
    { name: 'banana', color: 'yellow' },
    { name: 'grape', color: 'purple' }
];

function test() {
  // 条件：任何一个水果是红色
  const isAnyRed = fruits.some(f => f.color == 'red');

  console.log(isAnyRed); // true
}
```

### 7.10. 数组的增删改操作
#### 7.10.1. push() 方法增加元素

- 语法：`array.push(item1 [...，itemN])`
- 作用：将一个或多个项追加到数组的末尾，并返回新的长度
- > 注：此方法会改变原数组

```js
const names = ['moon']
names.push('abc')
console.log(names) // ["moon", "abc"]
```

#### 7.10.2. unshift() 方法增加元素

- 语法：`array.unshift(item1[..., itemN])`
- 作用：将一个或多个项追加到数组的开头，并返回新的长度
- > 注：此方法会改变原数组

```js
const names = ['moon']
names.unshift('abc')
console.log(names) // ["abc", "moon"]
```

#### 7.10.3. 展开操作符(...)增加元素

- 通过组合展开操作符和数组变量，以不可变的方式在数组中插入项
- 在数组末尾追加一个项

```js
const names = ['moon', 'kira']
const names2 = [...names, 'N']
console.log(names2) // ["moon", "kira", "N"]
```

- 在数组的开头追加一个项

```js
const names = ['moon', 'kira']
const names2 = ['N', ...names]
console.log(names2) // ["N", "moon", "kira"]
```

- 在任何索引处插入元素

```js
const names = ['moon', 'kira']
const indexToInsert = 1
const names2 = [
  ...names.slice(0, indexToInsert),
  'newWord',
  ...names.slice(indexToInsert)
]
console.log(names2)  // ["moon", "newWord", "kira"]
```

#### 7.10.4. pop() 方法删除元素

- 语法：`array.pop()`
- 作用：从数组中删除最后一个元素，然后返回该元素
- 注：`array.pop()`方法会改变原数组

```js
const colors = ['blue', 'green', 'black'];
const lastColor = colors.pop();
console.log(lastColor); // => 'black'
console.log(colors); // => ['blue', 'green']
```

#### 7.10.5. shift() 方法删除元素

- 语法：`array.shift()`
- 作用：从数组中删除第一个元素，然后返回该元素
- 注：`array.shift()`方法会改变原数组，并具有O(n)复杂度。

```js
const colors = ['blue', 'green', 'black'];
const firstColor = colors.shift();
console.log(firstColor); // => 'blue'
console.log(colors); // => ['green', 'black']
```

#### 7.10.6. splice() 方法，对数组删除/替换/插入某一项

- 语法：`array.splice(index, len, [item, ...itemN])`
- 作用：返回增删改后的数组，**该方法会改变原始数组**
- 参数：splice有3个参数，它也可以用来替换/删除/添加数组内某一个或者几个值
    - `index`：数组开始下标，可选参数默认值为0
    - `len`: 替换/删除的长度，可选参数，默认值为`array.length`
    - `item`：替换的值，删除操作的时候，item为空

- 删除操作

```js
// 删除起始下标为1，长度为1的一个值(len设置1，如果为0，则数组不变)
var arr = ['a', 'b', 'c', 'd'];
arr.splice(1, 1);
console.log(arr); // ['a','c','d'];

// 删除起始下标为1，长度为2的一个值(len设置2)
var arr2 = ['a', 'b', 'c', 'd']
arr2.splice(1, 2);
console.log(arr2); // ['a','d']

// 清空数组
const colors = ['blue', 'green', 'black'];
colors.splice(0);
console.log(colors); // []
```

- 替换操作

```js
// 替换起始下标为1，长度为1的一个值为‘ttt'，len设置的1
var arr = ['a', 'b', 'c', 'd'];
arr.splice(1, 1, 'ttt');
console.log(arr);   // ['a','ttt','c','d']

var arr2 = ['a', 'b', 'c', 'd'];
arr2.splice(1, 2, 'ttt');
console.log(arr2); // ['a','ttt','d'] 替换起始下标为1，长度为2的两个值为‘ttt'，len设置的1
```

- 添加操作 -- len设置为0，item为添加的值

```js
var arr = ['a', 'b', 'c', 'd'];
arr.splice(1, 0, 'ttt');
console.log(arr); // ['a','ttt','b','c','d'] 表示在下标为1处添加一项'ttt'
```

- delete方法删除掉数组中的元素后，会把该下标出的值置为undefined,数组的长度不会变

```js
var arr = ['a', 'b', 'c', 'd'];
delete arr[1];
arr; // ["a", undefined × 1, "c", "d"] 中间出现两个逗号，数组长度不变，有一项为undefined
```

#### 7.10.7. 展开操作符号(...)

可以通过组合展开操作符和数据字面量以不可变的方式从数组中删除项

```js
const names = ['moon', 'kira', 'N', 'L+N']
const fromIndex = 1
const removeCount = 2

const newNames = [
   ...names.slice(0, fromIndex),
   ...names.slice(fromIndex + removeCount)
]

console.log(newNames) // ["moon", "L+N"]
```

#### 7.10.8. array.length 属性清空数组

- array.length是保存数组长度的属性。 除此之外，array.length是可写的。
- 如果写一个小于当前长度的`array.length = newLength`，多余的元素从数组中移除

```js
const colors = ['blue', 'green', 'black'];
colors.length = 0;
console.log(colors); // []
```

#### 7.10.9. fill() 方法填充数组

- 语法：`arr.fill(value, start, end)`
    - value：填充值
    - start：填充起始位置，可选参数，默认值为0
    - end：填充结束位置，可选参数，默认值为`array.length`，即实际结束位置是end-1
- 作用：`fill()`使用制定的元素填充数组，其实就是用默认内容初始化数组

```js
// 全部填充
['a', 'b', 'c'].fill(7)    // [7, 7, 7]

// 使用Array(length).fill(initial)来初始化特定长度和初始值的数组
new Array(3).fill(7)    // [7, 7, 7]

// 填充指定位置的元素
['a', 'b', 'c'].fill(7, 1, 2)    // ['a', 7, 'c']
```

#### 7.10.10. copyWithin() 方法

- 语法：`arr.copyWithin(target[, start[, end]])`
- 参数：
    - target（必需）：从该位置开始替换数据
    - start（可选）：从该位置开始读取数据，默认为0。如果为负值，表示倒数
    - end（可选）：到该位置前停止读取数据，默认等于数组长度。如果为负值，表示倒数

```js
let numbers = [1, 2, 3, 4, 5];

numbers.copyWithin(-2);
// [1, 2, 3, 1, 2]

numbers.copyWithin(0, 3);
// [4, 5, 3, 4, 5]

numbers.copyWithin(0, 3, 4);
// [4, 2, 3, 4, 5]

numbers.copyWithin(-2, -3, -1);
// [1, 2, 3, 3, 4]

[].copyWithin.call({length: 5, 3: 1}, 0, 3);
// {0: 1, 3: 1, length: 5}

// ES2015 Typed Arrays are subclasses of Array
var i32a = new Int32Array([1, 2, 3, 4, 5]);

i32a.copyWithin(0, 2);
// Int32Array [3, 4, 5, 4, 5]

// On platforms that are not yet ES2015 compliant:
[].copyWithin.call(new Int32Array([1, 2, 3, 4, 5]), 0, 3, 4);
// Int32Array [4, 2, 3, 4, 5]
```

### 7.11. 数组的扁平化
#### 7.11.1. flat() 方法

- 语法：`array.flat([depth])`
  
    - 参数depth：可选参数，默认值为1
- 作用：通过递归扁平属于数组的项直到一定深度来创建新数组
- > 注：`array.flat()` 创建一个新数组，而不会改变原始数组

```js
// arrays 包含数字和数字数组的混合。 arrays.flat()对数组进行扁平，使其仅包含数字。
const arrays = [0, [1, 3, 5], [2, 4, 6]];
const flatArray = arrays.flat();
console.log(flatArray); // [0, 1, 3, 5, 2, 4, 6]
```

### 7.12. 数组的排序
#### 7.12.1. sort()方法

- 语法：`array.sort([compare])`
    - 可选参数compare(a, b)是一个自定义排序顺的回调函数。如果按数字排序，需要指定排序的函数（类似java的比较器），如下是函数的比较规则：
        - 如果a小于b，在排序后的数组中a应该出现在b之前，就返回一个小于0的值。
        - 如果a等于b，就返回0。
        - 如果a大于b，就返回一个大于0的值。
- 作用：对数组进行排序，如果数组是数字，也是默认按字符串的排序方法进行排序
- > 注：`array.sort()` 会改变原数组。

```js
// 对数组 numbers 以升序对数字进行排序
const numbers = [4, 3, 1, 2];
numbers.sort();
console.log(numbers); // => [1, 2, 3, 4]

// 使用比较函数，让偶数排在奇数前面
const numbers = [4, 3, 1, 2];

function compare(n1, n2) {
  if (n1 % 2 === 0 && n2 % 2 !== 0) {
    return -1;
  }
  if (n1 % 2 !== 0 && n2 % 2 === 0) {
    return 1;
  }
  return 0;
}

numbers.sort(compare);
console.log(numbers); // => [4, 2, 3, 1]
```

#### 7.12.2. reverse()方法

- 对数组进行反转，直接操作数组本身

## 8. 字符串(String)

### 8.1. trim() 方法

`trim()`方法会从一个字符串的两端删除空白字符。

```js
str.trim();
```

> <font color=red>**注：`trim()`方法并不影响原字符串本身，它返回的是一个新的字符串。**</font>

## 9. JavaScript 一些语法应用案例

### 9.1. 字符串转对象的三种方式
#### 9.1.1. 方式1：`JSON.parse(str)`

```js
// 定义一个对象字符串
var str = '{"error":1,"data":"用户不存在"}';
// 使用JSON.parse()
var obj = JSON.parse(str);
// 输出该对象
console.log(obj);
```

#### 9.1.2. 方式2：`eval("("+str+")")`

```js
// 定义一个对象字符串
var str = '{"error":1,"data":"用户不存在"}';
// 使用eval();
var obj = eval("(" + str + ")")
// 输出该对象
console.log(obj);
```

#### 9.1.3. 方式3：`(new Function("return " + str))()`

```js
// 定义一个对象字符串
var str = '{"error":1,"data":"用户不存在"}';
// 使用Function
var obj = (new Function("return " + str))();
// 输出该对象
console.log(obj);
```

## 10. 其他

### 10.1. EventLoop

#### 10.1.1. JavaScript 是单线程的语言

JavaScript 是一门单线程执行的编程语言。也就是说，同一时间只能做一件事情。

![](images/20211205104054852_26007.png)

> 单线程执行任务队列的问题：如果前一个任务非常耗时，则后续的任务就不得不一直等待，从而导致程序假死的问题。

#### 10.1.2. 同步任务和异步任务

JavaScript 把待执行的任务分为了两类：

1. 同步任务（synchronous）
    - 又称为**非耗时任务**，指的是在主线程上排队执行的那些任务
    - 只有前一个任务执行完毕，才能执行后一个任务
2. 异步任务（asynchronous）
    - 又称为**耗时任务**，异步任务由 JavaScript 委托给宿主环境进行执行
    - 当异步任务执行完成后，会通知 JavaScript 主线程执行异步任务的回调函数

#### 10.1.3. EventLoop 的基本概念

**同步任务和异步任务的执行过程**

![](images/20211205104342254_17359.png)

1. 同步任务由 JavaScript 主线程次序执行
2. 异步任务委托给宿主环境执行
3. 已完成的异步任务对应的回调函数，会被加入到任务队列中等待执行
4. JavaScript **主线程的执行栈被清空后，会读取任务队列中的回调函数，次序执行**
5. JavaScript **主线程不断重复上面的第 4 步**

<font color=red>**JavaScript 主线程从“任务队列”中读取异步任务的回调函数，放到执行栈中依次执行。这个过程是循环不断的，所以整个的这种运行机制又称为 EventLoop（事件循环）。**</font>

#### 10.1.4. 结合 EventLoop 分析输出的顺序案例（面试题）

```js
import { getResult } from '../utils/util.js'

console.log('A')

// 模拟请求获取数据
getResult(3).then(res => console.log('B'))

setTimeout(() => console.log('C'), 0)

console.log('D')
```

上面示例最终的输出结果是：ADCB

- A 和 D 属于同步任务。会根据代码的先后顺序依次被执行
- C 和 B 属于异步任务。它们的回调函数会被加入到任务队列中，等待主线程空闲时再执行

### 10.2. 宏任务和微任务

#### 10.2.1. 什么是宏任务和微任务

JavaScript 把异步任务又做了进一步的划分，异步任务又分为两类，分别是：

![](images/20211205110318506_26479.png)

1. 宏任务（macrotask）：异步 Ajax 请求、`setTimeout`、`setInterval`、文件操作等
2. 微任务（microtask）：`Promise.then`、`Promise.catch`、`Promise.finally`、`process.nextTick` 等

#### 10.2.2. 宏任务和微任务的执行顺序

![](images/20211205110505881_9639.png)

每一个宏任务执行完之后，都会**检查是否存在待执行的微任务**。如果有，则执行完所有微任务之后，再继续执行下一个宏任务。

#### 10.2.3. 宏任务和微任务执行顺序分析案例（面试题）

##### 10.2.3.1. 案例1

```js
setTimeout(() => console.log('1'))

new Promise(resolve => {
  console.log('2')
  resolve()
}).then(res => console.log('3'))

console.log('4')
```

最终结果输出是：2431

1. 先执行所有的同步任务输出：2、4
2. 再执行微任务输出：3
3. 再执行下一个宏任务输出：1

##### 10.2.3.2. 案例2

```js
console.log('1')

setTimeout(() => {
  console.log('2')
  new Promise(resolve => {
    console.log('3')
    resolve()
  }).then(() => console.log('4'))
})

new Promise(resolve => {
  console.log('5')
  resolve()
}).then(() => console.log('6'))

setTimeout(() => {
  console.log('7')
  new Promise(resolve => {
    console.log('8')
    resolve()
  }).then(() => console.log('9'))
})
```

最终结果输出是：156234789

1. 先执行所有的同步任务输出：1、5
2. 再执行第1个宏任务中的同步任务输出：2、3
3. 再执行第1个宏任务的微任务输出：4
4. 再执行第2个宏任务中的同步任务输出：7、8
5. 再执行第2个宏任务的微任务输出：9

### 10.3. API 接口案例

#### 10.3.1. 案例需求

基于 MySQL 数据库 + Express 对外提供用户列表的 API 接口服务。用到的技术点如下：

- 第三方包 express 和 mysql2
- ES6 模块化
- Promise
- async/await

#### 10.3.2. 搭建项目的基本结构

1. 启用 ES6 模块化支持，在 package.json 中声明 `"type": "module"`
2. 安装第三方依赖包。`npm install express@4.17.1 mysql2@2.2.5 -S`

#### 10.3.3. 创建基本的服务器

在项目的根目录下创建 app.js 入口文件，创建基础的服务器

```js
import express from 'express'

const app = express()

app.listen(80, () => {
  console.log('server running at http://127.0.0.1')
})
```

运行以下命令测试是否能开启服务

```bash
nodemon app.js
```

如 nodemon 没有安装，先运行全局安装命令

```bash
npm install -g nodemon
```

卸载则运行

```bash
npm uninstall -g nodemon
```

#### 10.3.4. 创建 db 数据库操作模块

创建`db/index.js`，配置数据库的连接信息

```js
import mysql from 'mysql2'

const pool = mysql.createPool({
  host: '127.0.0.1',
  port: 3306,
  database: 'tempdb',
  user: 'root',
  password: '123456',
})

export default pool.promise()
```

#### 10.3.5. 创建请求的处理方法

```js
import db from '../db/index.js'

// 使用 ES6 的按需导出语法，将 getAllUser 方法导出
export async function getAllUser(req, res) {
  try {
    // db.query() 函数的返回值是 Promise 实例对象。所以可以使用 async/await 进行异步处理简化
    const [rows] = await db.query('select id, name, gender from user')
    res.send({
      status: 0,
      message: '获取用户列表数据成功！',
      data: rows,
    })
  } catch (err) {
    res.send({
      status: 1,
      message: '获取用户列表数据失败！',
      desc: err.message,
    })
  }
}
```

#### 10.3.6. 配置路由

```js
import express from 'express'
import { getAllUser } from '../controller/UserController.js'

// 创建路由对象
const router = new express.Router()

// 挂载路由规则
router.get('/user', getAllUser)

// 使用 ES6 默认导出语法，将路由对象导出
export default router
```

#### 10.3.7. 导入并挂载路由模块

在 app.js 文件中导入并挂载路由模块

```js
import express from 'express'
// 默认导入路由对象
import userRouter from './router/user_router.js'

const app = express()

// 挂载用户路由模块
app.use('/api', userRouter)

app.listen(80, () => {
  console.log('server running at http://127.0.0.1')
})
```

#### 10.3.8. 测试

使用postman等请求工具，请求`http://127.0.0.1/api/user`，获取返回数据

## 11. console 对象使用

### 11.1. 常用方法

1. `console.log()`

```js
// 用于输出普通信息，最常用
console.log("%c%s", "color:red;font-size:20px", "结果是：这样的哟");
```

2. `console.info()`

```js
// 用于输出提示性信息
console.info("%s", "color:red;这是结果哟");
```

3. `console.error()`

```js
// 用于输出错误信息
console.error("错误");
```

4. `console.warn()`：用于输出警示信息
5. `console.count()`：统计代码被执行的次数
6. `console.assert()`：对输入的表达式进行断言，只有表达式为false时，才输出相应的信息到控制台
7. `console.group()`：输出一组信息的开头
8. `console.groupEnd()`：结束一组输出信息
9. `console.dir()`：直接将该DOM结点以DOM树的结构进行输出，可以详细查对象的方法发展等等
10. `console.time()`：计时开始
11. `console.timeEnd()`：计时结束
12. `console.profile()` 和 `console.profileEnd()`：一起使用来查看CPU使用相关信息
13. `console.timeLine()` 和 `console.timeLineEnd()`：一起使用记录一段时间轴
14. `console.trace()`：堆栈跟踪相关的调试

### 11.2. 格式化符号

|   格式化符号   |            实现的功能            |
| :----------: | ------------------------------ |
|     `%s`     | 格式化成字符串输出                |
| `%d` or `%i` | 格式化成数值输出                  |
|     `%f`     | 格式化成浮点数输出                |
|     `%o`     | 转化成展开的DOM元素输出            |
|     `%O`     | 转化成JavaScript对象输出          |
|     `%c`     | 把字符串按照你提供的样式格式化后输入 |

## 12. ES2020 新特性

### 12.1. 可选链操作符

可选链操作符(`?.`)允许读取位于连接对象链深处的属性的值，而不必明确验证链中的每个引用是否有效。`?.`操作符的功能类似于`.`链式操作符，不同之处在于，在引用为空(`nullish`) (`null`或者`undefined`) 的情况下不会引起错误，该表达式短路返回值是`undefined`。与函数调用一起使用时，如果给定的函数不存在，则返回`undefined`。

当尝试访问可能不存在的对象属性时，可选链操作符将会使表达式更短、更简明。

```js
const adventurer = {
  name: "Alice",
  cat: {
    name: "Dinah",
  },
};

const dogName = adventurer.dog?.name;
console.log(dogName);
// expected output: undefined

const catName = adventurer.cat?.name;
console.log(catName);
// expected output: Dinah

console.log(adventurer.someNonExistentMethod?.());
// expected output: undefined
```

**语法**：

```js
obj?.prop
obj?.[expr]
arr?.[index]
func?.(args)
```

参考：https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Optional_chaining
