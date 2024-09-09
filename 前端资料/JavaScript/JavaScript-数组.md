> TODO: 整理中  https://mp.weixin.qq.com/s/08n92fmYy9I6hGPcrNdlvA

## 1. JavaScript Array（数组）的概述

数组（Array）作为 JavaScript 位列第一的对象，其重要性可见一斑。官方对于 Array（数组）对象的解释：使用单独的变量名来存储一系列的值。简而言之就是把要储存的值都放在一个空间里，然后给它们每一个固定的序号，在找这些值的时候可以通过序号 0、1、2、3...... 快速的找到需要的数值。

在 JavaScript 中，数组是一种特殊的对象，等价于集合，用于表示和操作有序的数据集。数组是一种数据结构，可以存储多个值在一个变量中，并通过数字索引来访问这些值。

JavaScript 中的数组与其他编程语言中的数组有所不同，因为它具有动态大小，这意味着可以在运行时添加或删除元素。以下是一些关于 JavaScript 数组的基本特点：

1. 索引访问：可以通过索引访问数组中的元素，索引从0开始。例如，`arr[0]` 将访问数组的第一个元素。
2. 动态大小：可以随时向数组添加或删除元素，而不需要预先指定其大小。
3. 异质性：数组可以包含不同类型的元素。
4. 方法：JavaScript 数组有大量内置方法，如 push(), pop(), shift(), unshift(), splice(), slice(), map(), filter(), reduce() 等，这些方法可用于操作数组。
5. 多维数组：JavaScript 中的数组也可以是二维或多维的。
6. 关联数组：除了数字索引外，还可以使用字符串或其他对象作为键来存储和访问值。

## 2. 数组的创建

### 2.1. 一维数组

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

### 2.2. 二维数组

```js
var arr = [[x,x,x],[x,x,x],[x,x],……];
```

- 值：`arr[0] = [x,x,x]`
- 使用嵌套循环遍历

### 2.3. Array.of() 将一组值转换为数组

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

### 2.4. 数组中元素的类型

1. 数组中的元素类型可以各不相同
2. 数组的长度可以动态增长

```js
var arr = [1, 3, 2];
arr[2] = "hello";
arr[3] = new Date();
arr[5] = 99;
```

值得注意的是，如果跳过增加，则数组也会自动在空的位置加上 `undefined`。因此上面的案例最后结果是：

- 数组的长度是 6
- 数组的内容是`[1, 3, "hello", 时间对象, undefined, 99]`

### 2.5. Array.from() 通过给定的对象来创建数组

`Array.from()` 是一个静态方法，用于将一个**类数组对象**或者**可遍历对象(包括ES6新增的数据结构Set和Map)**转换成一个真正的数组。如果对象是数组返回 true，否则返回 false；此方法主要用在以下几个方面：

- **从类似数组对象创建数组**：当有一个类似数组的对象（例如一个 NodeList 或 HTMLCollection），就可以使用 `Array.from()` 来将它转换为真正的数组。
- **从非可迭代对象创建数组**：任何可迭代对象都可以使用 `Array.from()` 转换为数组。例如，一个字符串、一个 Map、一个 Set 等。
- **使用映射函数**：`Array.from()` 方法允许使用者提供一个映射函数，该函数会在每个元素上调用，然后将结果收集到一个新数组中。

> Tips: **类数组对象**，最基本的要求就是具有 `length` 属性的对象。

#### 2.5.1. 语法

```js
Array.from(arrayLike[, mapFunction[, thisArg]])
```

参数说明：

- arrayLike（必需）：要转换成数组的伪数组对象或可迭代对象。
- mapFunction（可选）：`mapFunction(item，index){…}` 是在集合中的每个项目上调用的函数。返回的值将插入到新集合中。
- thisArg（可选）：执行回调函数 mapFunction 时 this 对象。*此参数很少使用*。

简单的示例

```js
const someNumbers = { '0': 10, '1': 15, length: 2 };
Array.from(someNumbers, value => value * 2); // => [20, 30]
```

> 注：
>
> - `Array.from()`创建一个新的映射数组，而不改变原始数组。
> - `Array.from()`更适合从类似数组的对象进行映射。

#### 2.5.2. 类数组对象转换

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

#### 2.5.3. Set 结构的数据转换

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

#### 2.5.4. 字符串转换

```js
let str = 'hello world!';
console.log(Array.from(str)) // ["h", "e", "l", "l", "o", " ", "w", "o", "r", "l", "d", "!"]s
```

#### 2.5.5. 参数是一个真正的数组

如果 `Array.from` 的参数是一个真正的数据，则会返回一个一模一样的新数组。

```js
console.log(Array.from([12,45,47,56,213,4654,154]))
```

## 3. 数组的增删改

### 3.1. push() 在数组的尾部位置添加一个或更多元素

`push()` 方法可向数组的尾部添加一个或多个元素，并返回数组新的长度，<font color=red>**注：该方法将改变原数组**</font>。

```js
array.push(item1 [...，itemN])
```

示例：

```js
const names = ['moon']
names.push('abc')
console.log(names) // ["moon", "abc"]
```

### 3.2. unshift() 在数组的头部位置添加一个或更多元素

`unshift()` 方法可向数组的头部添加一个或更多元素，并返回数组新的长度，<font color=red>**注：该方法将改变原数组**</font>。

```js
array.unshift(item1[..., itemN])
```

> Notes: 将新项添加到数组末尾，请使用 push() 方法。

```js
const fruits = ["Banana", "Orange", "Apple", "Mango"];
fruits.unshift("Lemon", "Pineapple");
console.log(fruits) // 输出：Lemon,Pineapple,Banana,Orange,Apple,Mango
```

### 3.3. pop() 删除数组最后一个元素并返回删除的元素

`pop()` 方法用于删除数组的最后一个元素并返回该元素，<font color=red>**注：此方法会改变原数组的长度**</font>。

```js
array.pop()
```

> Notes: 移除数组第一个元素，请使用 `shift()` 方法。

```js
const colors = ['blue', 'green', 'black'];
const lastColor = colors.pop();
console.log(lastColor); // => 'black'
console.log(colors); // => ['blue', 'green']
```

### 3.4. shift() 删除并返回数组的第一个元素

`shift()` 方法用于移除数组的第一个元素，并将该元素值返回。<font color=red>**此方法改变数组的长度**</font>。

```js
array.shift()
```

> Notes: `array.shift()` 方法会改变原数组，并具有`O(n)`复杂度。*移除数组末尾的元素可以使用 `pop()` 方法。*

```js
const colors = ['blue', 'green', 'black'];
const firstColor = colors.shift();
console.log(firstColor); // => 'blue'
console.log(colors); // => ['green', 'black']
```

### 3.5. splice() 对数组删除/替换/插入元素

`splice()` 方法用于添加、删除、替换数组中的元素，返回增删改后的数组。<font color=red>**注：此方法会改变原始数组**</font>。

```js
array.splice(index, len, [item, ...itemN])
```

参数与返回值说明：

- `index`（必需）：数组开始下标，可选参数默认值为0
- `len`: 指定替换/删除的长度，必须是数字，可以是 0，默认值为`array.length`。如果未指定此参数，则替换/删除从 index 开始到原数组结尾的所有元素；
- `[item, ...itemN]`：替换的元素，删除操作的时候，item 为空
- 返回值：如果删除一个元素，则返回一个元素的数组；如果未删除任何元素，则返回空数组。

#### 3.5.1. 删除操作

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

#### 3.5.2. 替换操作

```js
// 替换起始下标为1，长度为1的一个值为‘ttt'，len设置的1
var arr = ['a', 'b', 'c', 'd'];
arr.splice(1, 1, 'ttt');
console.log(arr);   // ['a','ttt','c','d']

var arr2 = ['a', 'b', 'c', 'd'];
arr2.splice(1, 2, 'ttt');
console.log(arr2); // ['a','ttt','d'] 替换起始下标为1，长度为2的两个值为‘ttt'，len设置的1
```

#### 3.5.3. 添加操作

将 len 设置为 0，item 为添加的值

```js
var arr = ['a', 'b', 'c', 'd'];
arr.splice(1, 0, 'ttt');
console.log(arr); // ['a','ttt','b','c','d'] 表示在下标为1处添加一项'ttt'
```

### 3.6. delete 关键字删除元素

使用 `delete` 关键字删除掉数组中的元素后，会把该下标出的值置为 `undefined`，并且数组的长度不会变。

```js
var arr = ['a', 'b', 'c', 'd'];
delete arr[1];
arr; // ["a", undefined × 1, "c", "d"] 中间出现两个逗号，数组长度不变，有一项为 undefined
```

### 3.7. 展开操作符(...) 增删元素

#### 3.7.1. 增加元素

通过组合展开操作符和数组变量，以不可变的方式在数组中插入项。

- 在数组末尾追加一个元素

```js
const names = ['moon', 'kira']
const names2 = [...names, 'N']
console.log(names2) // ["moon", "kira", "N"]
```

- 在数组的开头追加一个元素

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

#### 3.7.2. 删除元素

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

### 3.8. array.length 属性清空数组

- array.length是保存数组长度的属性。 除此之外，array.length是可写的。
- 如果写一个小于当前长度的`array.length = newLength`，多余的元素从数组中移除

```js
const colors = ['blue', 'green', 'black'];
colors.length = 0;
console.log(colors); // []
```

### 3.9. fill() 使用一个固定值来填充数组

`fill()` 方法用于将一个固定元素填充数组中指定的位置，可以理解为使用默认内容初始化数组。<font color=red>**注：该方法会改变原始数组**</font>。


```js
array.fill(value, start, end)
```

- value（必需）：填充值
- start（可选）：填充起始位置，可选参数，默认值为0
- end（可选）：填充结束位置，可选参数，默认值为`array.length`，即实际结束位置是 end-1

```js
// 全部填充
['a', 'b', 'c'].fill(7)    // [7, 7, 7]

// 使用Array(length).fill(initial)来初始化特定长度和初始值的数组
new Array(3).fill(7)    // [7, 7, 7]

// 填充指定位置的元素
['a', 'b', 'c'].fill(7, 1, 2)    // ['a', 7, 'c']
```

### 3.10. copyWithin() 从数组的指定位置拷贝元素到数组的另一个指定位置中

`copyWithin()` 方法用于将数组的内部元素复制到数组的其他位置，覆盖数组的原有元素，而不会改变数组的长度，是一种移动数组的高效方法。

```js
array.copyWithin(target[, start[, end]])
```

- target（必需）：元素复制到指定目标索引位置
- start（可选）：读取数据的起始位置，默认为0。如果为负值，表示倒数
- end（可选）：停止读取数据的索引位置，默认等于数组长度。如果为负值，表示倒数

> Notes: <font color=red>**当 end 小于 start 时，该方法不生效！**</font>

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


## 4. 数组的连接与转换

### 4.1. concat() 连接两个或更多的数组

`concat()` 方法用于连接两个或多个数组，或者给数组增加元素，并返回新的结果数组。一般用于数组的拼接和增加元素操作。语法如下：

```js
array.concat(array1[, array2, ...])
```

> Notes: 
>
> - concat 方法不会改变现有的数组，而是返回一个新创建的数组。
> - 如果要进行 concat() 操作的参数是数组，那么添加的是数组中的元素，而不是数组。

```js
const arr1 = [1, 2];
const arr2 = ['moon', 'hehe'];

const concatArray = arr1.concat(arr2);
console.log(concatArray); // 1, 2, moon, hehe
```

### 4.2. 使用展开操作符(...)连接

可以使用展开操作符 `...` 与数组变量一起使用，用于连接数组，与`concat()`方法效果一样

```js
const array = [...array1, ...array2]
```

示例：

```js
const arr1 = [1, 2];
const arr2 = ['moon', 'hehe'];

const concatArray = [...arr1, ...arr2];
console.log(concatArray); // 1, 2, moon, hehe
```

### 4.3. join() 将数组的所有元素转换成一个字符串

`join()` 方法用于把数组中的所有元素转换一个字符串，元素是通过指定的分隔符进行分隔。语法如下：

```js
array.join(separator分隔符)
```

参数与返回值说明：

- `separator`（可选）：指定要使用的分隔符，如果省略该参数，则使用逗号作为分隔符。
- 返回值：返回一个字符串，该字符串是通过把 arrayObject 的每个元素转换为字符串，然后把这些字符串连接起来，在两个元素之间插入 `separator` 字符串而生成的。

```js
var a3 = [1, 2, 3]
var str = a3.join("+")
// 输出结果："1+2+3"
```

> Notes: join 方法正好与**将一个字符串切割成一个字符串数组的 `split(分隔符)` 方法**相反，join 方法是将数组通过分隔符，拼成一个字符串。

### 4.4. split() 将字符串转成数组

`split()` 方法的作用是，将字符串，按指定的分隔符截取成数组。（与 `join()` 方法相反）

```js
array.split(separator分隔符)
```

### 4.5. toString() 将数组转换为字符串

`toString()` 方法可把数组转换为字符串，并返回结果。

> Notes: 数组中的元素之间用逗号分隔。

```js
var fruits = ["Banana", "Orange", "Apple", "Mango"];
var str = fruits.toString();
console.log(str) // 输出：Banana,Orange,Apple,Mango
```

## 5. 查找元素

### 5.1. includes()

`includes()` 方法用来判断一个数组是否包含一个指定的值，如果是返回 true，否则返回 false。

```js
array.includes(itemToSearch[，fromIndex])
```

- itemToSearch（必须）：需要查找的元素值
- fromIndex（可选）：默认值为0，表示从该索引处开始查找 itemToSearch。如果为负值，则按升序从 array.length + fromIndex 的索引开始搜索，默认值为 0。

> Notes: 如果 fromIndex 大于等于数组长度 ，则返回 false；如果 fromIndex 为负值，计算出的索引将作为开始搜索 itemToSearch 的位置；如果计算出的索引小于 0，则整个数组都会被搜索。

示例：如果多个判断条件，则需要用更多的`||`来拓展条件语句，此时可以使用(Array.includes)重写多重判断条件语句。

```js
// condition
function test(fruit) {
  if (fruit == 'apple' || fruit == 'strawberry') {
    console.log('red');
  }
}

// best
function test2(fruit) {
  const redFruits = ['apple', 'strawberry', 'cherry', 'cranberries'];

  if (redFruits.includes(fruit)) {
    console.log('red');
  }
}
```

### 5.2. find() 和 findIndex() 查找第一个符合条件的数组元素/索引

`find()` 方法查找目标数组中满足回调函数的**第一个目标元素**；find() 方法会为数组中的每个元素依次调用一次传入的筛选条件，找到第一个满足条件的数组元素时，直接返回符合条件的元素，之后的元素不会再调用筛选函数，如果没有符合条件的元素返回 undefined。<font color=red>**注: find() 对于空数组，函数是不会执行，并且不会改变数组的原始值**</font> 

`findIndex()` 函数也是查找目标元素，找到就返回元素的位置（索引），找不到就返回 -1。<font color=red>**注: findIndex() 对于空数组，函数是不会执行，并且不会改变数组的原始值**</font> 

```js
array.find/findIndex(callback(currentValue, index, arr), thisArg)
```

- callback 函数参数，是查找数组每个元素需要依次执行的回调函数。
    - currentValue 必需：每一次迭代查找的数组元素
    - index 可选：每一次迭代查找的数组元素索引
    - arr 可选：当前被查找元素所属的数组对象。
- thisArg 可选：执行 callback 函数时使用的 this 值。

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

### 5.3. indexOf() 返回数组中某个指定的元素位置

`indexOf()` 方法将从头到尾地检索数组，看它是否含有对应的元素，开始检索的位置在数组 fromIndex 处或数组的开头（没有指定 fromIndex 参数时），如果找到一个 item，则返回 item 的第一次出现的位置，开始位置的索引为 0，如果在数组中没找到指定元素则返回 -1。语法如下：

```js
array.indexOf(itemToSearch[, fromIndex])
```

参数与返回值说明：

- itemToSearch（必须）：查找的元素
- fromIndex（可选参数）：表示开始搜索的索引位置，默认值为0，即如省略该参数，则将从数组的头部位置开始检索。
- 返回值：array 中第一个出现的 itemToSearch 的索引；如果找不到该项，则返回 -1

```js
const arr = ['a', 'b', 'c', 'd'];
const index = arr.indexOf('b');

console.log(index); // 1
```

> Notes: 
>
> - 如果想查找元素最后出现的位置，请使用 lastIndexOf() 方法。
> - `array.findIndex(predicate)` 方法是使用 predicate 函数查找索引的替代方法

### 5.4. lastIndexOf() 搜索数组指定元素最后出现的位置

`lastIndexOf()` 方法可返回一个指定的元素在数组中最后出现的位置，从该数组的后面向前查找，如果要检索的元素没有出现，则该方法返回 -1，该方法将从尾到头地检索数组中指定元素 item，开始检索的位置在数组的 start 处或数组的结尾（没有指定 start 参数时），如果找到一个 item，则返回 item 从尾向前检索第一个次出现在数组的位置，数组的索引开始位置是从 0 开始的，如果在数组中没找到指定元素则返回 -1。

```js
array.lastIndexOf(item, start)
```

参数说明：

- item（必需）：指定需检索的元素值
- start（可选，整数参数）：指定在数组中开始检索的位置，它的合法取值是 0 到 arrayObject.length - 1，如省略该参数，则将从数组的尾部处开始检索。

### 5.5. every() 检测数值元素的每个元素是否都符合条件方法

`every()` 方法用于检测数组中所有元素是否都通过指定的测试函数；如果数组中检测到有一个元素不满足，则整个表达式返回 false ，且剩余的元素不会再进行检测；如果所有元素都满足条件，则返回 true。

> Notes: `every()` 不会对空数组进行检测；并且不会改变原始数组。

```js
array.every(predicate(currentValue[, index[, array]]))
```

- predicate 函数（必须）：用于测试每个元素的函数。此函数应返回一个布尔值来指示元素是否通过测试。
    - currentValue 必须，当前元素的值；
    - index 可选，当前元素的索引值；
    - arr 可选，当前元素属于的数组对象；
- thisArg 可选，执行 predicate 函数时使用的 `this` 值。

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

### 5.6. some() 检测数组元素中是否有元素符合指定条件

`some()` 方法会为数组的每个元素依次执行 callback 函数（由调用者指定），检测数组中的元素是否满足指定条件。如果有一个元素满足条件，则表达式返回 true，剩余的元素不会再执行检测；如果没有满足条件的元素，则返回 false。

> Notes: some() 不会对空数组进行检测；并且不会改变原始数组。

```js
array.some(predicate(currentValue[, index[, array]]), thisArg)
```

- predicate 函数：数组中的每个元素都会执行此函数。
    - currentValue 必须，当前元素的值；
    - index 可选，当前元素的索引值；
    - arr 可选，当前元素属于的数组对象；
- thisArg 可选：对象作为该执行回调时使用，传递给函数，用作 `this` 的值，如果省略了 thisArg ，`this` 的值为 `undefined`。

示例：使用`Array.some`判断部分元素符合条件

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

### 5.7. at() 接收一个整数值并返回该索引对应的元素

`at()` 方法用于接收一个整数值并返回该索引对应的元素，允许正数和负数，负整数从数组中的最后一个元素开始倒数，匹配给定索引的数组中的元素，如果找不到指定的索引，则返回 undefined。

在传递非负数时，`at()` 方法等价于括号表示法。比如，`array[0]` 和 `array.at(0)` 均返回第一个元素。但是，当需要从数组的末端开始倒数时，则不能使用 Python 和 R 语言中支持的 `array[-1]`，因为方括号内的所有值都会被视为字符串属性，因此最终读取的是 `array["-1"]`，这只是一个普通的字符串属性而不是数组索引，通常的做法是访问 length 并将其减去从末端开始的相对索引，例如：`array[array.length - 1]`，而 at() 方法允许使用相对索引，因此上面的示例可以简化为 `array.at(-1)`，更正式地，当 index < 0 时，该方法将访问索引 `index + array.length`，at() 方法是通用的，其仅期望 this 具有 length 属性和以整数为键的属性。

```js
const array1 = [5, 12, 8, 130, 44]; 
let index1 = 2; 
strt1 = `索引号为 ${index1} 的值为 ${array1.at(index1)}`; 
let index2 = -2; 
strt2 = `索引号为 ${index2} 的值为 ${array1.at(index2)}`;
// 输出：
// 索引号为 2 的值为 8
// 索引号为 -2 的值为 130 
```

## 6. 数组的过滤与截取

### 6.1. slice() 选取数组的一部分

`slice()` 方法可从已有的数组中返回选定的元素；此方法也可提取字符串的某个部分，并以新的字符串返回被提取的部分。注意：<font color=red>**`slice()` 方法不会改变原始数组**</font>。语法如下：

```js
array.slice([fromIndex[，toIndex]])
```

参数与返回值说明

- fromIndex（可选参数）：截取的开始索引，默认值为 0。如果该参数为负数，则表示从原数组中的**倒数**第几个元素开始提取。例如，`slice(-2)` 表示提取原数组中的倒数第二个元素到最后一个元素（包含最后一个元素）
- toIndex（可选参数）：截取结束索引（不包括），默认值为`array.length`。即如果没有指定该参数，那么切分的数组包含从 fromIndex 到数组结束的所有元素；如果该参数为负数，则它表示在原数组中的倒数第几个元素结束抽取。例如，`slice(-2, -1)` 表示抽取了原数组中的倒数第二个元素到最后一个元素（不包含最后一个元素，也就是只有倒数第二个元素）。
- 返回值：返回一个新的数组，包含从 fromIndex（包括该元素）到 toIndex（不包括该元素）的 arrayObject 中的元素。

> Notes: **此方法与 `splice()` 方法很相似，但 `slice()` 方法是创建一个新数组，不会改变原数组的。**

```js
const names = ["moon", "abc", "kira", "N"]

const heroes = names.slice(0, 2)
const villains = names.splice(2)

console.log(heroes) // ["moon", "abc"]
console.log(villains) // ["kira", "N"]
```

### 6.2. filter() 过滤符合条件的数值元素

`filter()` 对原数组进行筛选过滤出旧数组中符合条件的元素，最终创建一个新的筛选后的数组，筛选条件由调用方提供。<font color=red>**注：`filter()` 不会对空数组进行检测，并且是创建一个新数组，不会改变原始数组**</font>

```js
array.filter(predicate(currentValue[, index[, arr]]), thisArg)
```

- predicate 函数（必需）：过滤条件函数，数组中的每个元素都会执行这个函数，执行为 true 的符合筛选条件。
    - currentValue 必需：当前元素的值；
    - index 可选：当前元素的索引值；
    - arr 可选：当前元素属于的数组对象；
- thisArg（可选）：执行 callback 函数时使用的 this 值。

```js
var arr = [1, 2, 3, 4];
var newarr = arr.filter((item, idx) => {
    return item % 2 === 0;
})
```

#### 6.2.1. 扩展：使用 Boolean 过滤数组中的所有假值

JS中的相关假值：`false`, `null`, `0`, `undefined`, `NaN`。可以使用 Boolean 构造函数来进行一次转换，快速将数组中假值过滤

```js
const compact = arr => arr.filter(Boolean);
compact([0, 1, false, 2, "", 3, "a", "e" * 23, NaN, "s", 34]);
// [ 1, 2, 3, 'a', 's', 34 ]
```

#### 6.2.2. thisArg 参数的具体作用

thisArg 参数是 `Array.prototype.filter()` 方法的一个可选参数。它是一个值，用于指定在回调函数中作为 this 上下文执行时的上下文对象。如果没有提供 thisArg 参数，回调函数将在全局对象上作为 this 上下文执行。当在回调函数中使用 thisArg 参数时，实际上是在让 JavaScript 在执行回调函数时应该使用哪个对象作为 this 的上下文。这对于那些依赖于特定上下文的对象（如对象方法）尤其有用。

```js
const numbers = [1, 2, 3, 4, 5, 6];  
  
const evenNumbers = numbers.filter(function(num) {  
  return this.isEven(num); // 这里假设有一个名为 isEven 的方法在 this 上 
}, { isEven: function(num) { return num % 2 === 0; } });  
  
console.log(evenNumbers); // 输出: [2, 4, 6] 
```

在这个例子中，假设存在一个名为 `isEven` 的方法，该方法用于检查一个数字是否为偶数。使用 thisArg 参数来指定一个对象，该对象具有一个名为 `isEven` 的方法。在 `filter()` 方法中，回调函数将在这个对象上作为 this 上下文执行，因此 `this.isEven(num)` 将调用该对象上的 `isEven` 方法。

## 7. 数组的遍历

### 7.1. for...of 循环

`for...of` 循环是用于遍历数组所有元素项。<font color=red>**注：此方式可以随时使用`break`语句停止遍历。**</font>

```js
for (const item of array) {
    // do something...
}
```

示例：

```js
const colors = ['blue', 'green', 'white'];

for (const color of colors) {
    console.log(color);
}
// 'blue'
// 'green'
// 'white'
```

### 7.2. for 循环

for 循环使用递增的索引变量的方式遍历数组所有元素项，index变量从0递增到`colors.length-1`。<font color=red>**注：此方式可以随时使用`break`语句停止遍历。**</font>

```js
for (let i; i < array.length; i++) {
    // do something...
}
```

示例：

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

### 7.3. forEach() 对数组每个元素都执行一次回调函数

forEach() 用于对数组中的每个元素执行一次提供的 callback 函数相关逻辑，此方法**不会改变数组的长度**。语法格式：

```js
array.forEach(callback(item[, index[, array]]), thisArg)
```

参数说明：

- callback 函数：数组中每个元素需要调用的函数。该函数参数包括：当前遍历项（item）、索引（index）和数组本身（array）。
- thisArg（可选）：执行 callback 函数时使用的 this 值。

```js
var arr = [1,2,3];
arr.forEach((item, index) => {
    console.log(item, index);
    // 也可以有其他的操作。
})
```

> Notes: 
>
> - `array.forEach()` 迭代中，不能使用 `break` 关键字来中断操作
> - `forEach()` 对于空数组是不会执行回调函数
> - forEach 方法与 map 方法的区别是：forEach 不返回值，只用来操作数据。

### 7.4. map() 通过指定函数处理数组的每个元素，并返回处理后的数组（ES6新特性）


map() 方法接收一个函数，将原数组中的所有元素用这个函数处理后，创建新的数组返回。新的数组中的元素为原始数组元素调用函数处理后的值，该方法按照原始数组元素顺序依次处理元素。

```js
array.map(callback(currentValue[, index[, arr]]), thisArg)
```

- callback(currentValue, index, arr)必须，函数，数组中的每个元素都会执行这个函数：
    - currentValue（必须）：当前元素的值；
    - index（可选）：当前元素的索引值；
    - arr（可选）：当前元素属于的数组对象；
- thisArg（可选）：执行 callback 函数时使用的 this 值。

> Notes: <font color=red>**`array.map()` 不会对空数组进行检测；并且创建一个新的映射数组，而不改变原始数组。**</font>

例：有一个字符串数组，将其转为 int 数组

```js
let arr = ['1', '20', '-5', '3'];
console.log(arr)

let newArr = arr.map(s => parseInt(s));
console.log(newArr)
```

![](images/20190421103633831_23300.png)

### 7.5. reduce() 数组元素累计器（从左到右，ES6新特性）

`reduce()` 方法是 ES6 新增的数组方法，其作用是：对数组中的每个元素依次执行一个回调函数，从左到右依次累积计算出一个最终的值。可以简单理解为对数组的所有元素项进行累积操作。reduce() 可以作为一个高阶函数，用于函数的 compose。

> Notes: `reduce()` 对于空数组是不会执行回调函数。

#### 7.5.1. 语法格式

```js
array.reduce(callback(accumulator, currentValue[, index[, arr]])[, initialValue])
```

参数解析：

- callback（必须）：用于遍历每个元素时执行的回调函数，包含以下4个参数：
    - accumulator（必选）：上一次 reduce 处理的结果返回值
    - currentValue（必选）：数组遍历中，当前要处理的元素
    - index（可选）：当前元素的索引
    - arr（可选）：当前元素所属的原始数组对象
- initialValue（可选）：传递给函数的累积器初始值

`reduce` 函数的执行过程如下：

1. 默认如果没有指定 `initialValue` 初始值作为起始参数，则将数组的第一个元素作为累积器的初始值，否则将 `initialValue` 作为累积器的初始值。
2. 从数组的第二个元素开始，依次对数组中的每个元素执行回调函数。
3. 将回调函数的返回值作为下一次回调函数执行时的累积器的值。
4. 对数组中的每个元素执行完回调函数后，`reduce` 函数返回最后一次回调函数的返回值，即最终的累积值。

#### 7.5.2. 示例

基础累积运算示例：

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

计算数组中每个元素出现的次数：

```js
const fruits = ['apple', 'banana', 'apple', 'orange', 'banana', 'apple']
const count = fruits.reduce((accumulator, currentValue) => {
    accumulator[currentValue] = (accumulator[currentValue] || 0) + 1
    return accumulator
}, {})
console.log(count) // Output: { apple: 3, banana: 2, orange: 1 }
```

拍平嵌套数组：

```js
const nestedArray = [
    [1, 2],
    [3, 4],
    [5, 6],
]
const flattenedArray = nestedArray.reduce(
    (accumulator, currentValue) => accumulator.concat(currentValue),
    []
)
console.log(flattenedArray) // Output: [1, 2, 3, 4, 5, 6]
```

按条件分组：

```js
const people = [
    { name: 'Alice', age: 25 },
    { name: 'Bob', age: 30 },
    { name: 'Charlie', age: 35 },
    { name: 'David', age: 25 },
    { name: 'Emily', age: 30 },
]
const groupedPeople = people.reduce((accumulator, currentValue) => {
    const key = currentValue.age
    if (!accumulator[key]) {
        accumulator[key] = []
    }
    accumulator[key].push(currentValue)
    return accumulator
}, {})
console.log(groupedPeople)
// Output: {
//   25: [{ name: 'Alice', age: 25 }, { name: 'David', age: 25 }],
//   30: [{ name: 'Bob', age: 30 }, { name: 'Emily', age: 30 }],
//   35: [{ name: 'Charlie', age: 35 }]
// }
```

将多个数组合并为一个对象：

```js
const keys = ['name', 'age', 'gender']
const values = ['Alice', 25, 'female']
const person = keys.reduce((accumulator, currentValue, index) => {
    accumulator[currentValue] = values[index]
    return accumulator
}, {})
console.log(person) // Output: { name: 'Alice', age: 25, gender: 'female' }
```

将字符串转换为对象：

```js
const str = 'key1=value1&key2=value2&key3=value3'
const obj = str.split('&').reduce((accumulator, currentValue) => {
    const [key, value] = currentValue.split('=')
    accumulator[key] = value
    return accumulator
}, {})
console.log(obj)
// Output: { key1: 'value1', key2: 'value2', key3: 'value3' }
```

将对象转换为查询字符串：

```js
const params = { foo: 'bar', baz: 42 }
const queryString = Object.entries(params)
    .reduce((acc, [key, value]) => {
        return `${acc}${key}=${value}&`
    }, '?')
    .slice(0, -1)
console.log(queryString) // "?foo=bar&baz=42"
```

打印斐波那契数列：

```js
const fibonacci = n => {
    return [...Array(n)].reduce((accumulator, currentValue, index) => {
        if (index < 2) {
            accumulator.push(index)
        } else {
            accumulator.push(accumulator[index - 1] + accumulator[index - 2])
        }
        return accumulator
    }, [])
}
console.log(fibonacci(10)) // Output: [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```

检查字符串是否是回文字符串：

```js
const str = 'racecar'
const isPalindrome = str.split('').reduce((accumulator, currentValue, index, array) => {
    return accumulator && currentValue === array[array.length - index - 1]
}, true)
console.log(isPalindrome) // Output: true
```

检查括号是否匹配：

```js
const str = '(()()())'
const balanced =
    str.split('').reduce((acc, cur) => {
        if (cur === '(') {
            acc++
        } else if (cur === ')') {
            acc--
        }
        return acc
    }, 0) === 0
console.log(balanced) // true
```

递归获取对象属性：

```js
const user = {
    info: {
        name: 'MooN',
        address: { home: 'kira', company: 'zero' },
    },
}
function get(config, path, defaultVal) {
    return path.split('.').reduce((config, name) => config[name], config) || defaultVal
    return fallback
}
get(user, 'info.name') // MooN
get(user, 'info.address.home') // kira
get(user, 'info.address.company') // zero
get(user, 'info.address.abc', 'default') // default
```

#### 7.5.3. 手写 reduce 实现

可以通过手写一个简单的 `reduce` 函数来更好地理解它的实现原理：

```js
function myReduce(arr, callback, initialValue) {
    let accumulator = initialValue === undefined ? arr[0] : initialValue
    for (let i = initialValue === undefined ? 1 : 0; i < arr.length; i++) {
        accumulator = callback(accumulator, arr[i], i, arr)
    }
    return accumulator
}
```

上面的代码中，`myReduce` 函数接受 3 个参数：要执行 `reduce` 操作的数组 `arr`、回调函数 `callback` 和累积器的初始值 `initialValue`。如果没有提供初始值，则将数组的第一个元素作为累积器的初始值。

接下来在循环中，如果有 initialValue，则从第一个元素开始遍历 callback，此时 callabck 的第二个参数是从数组的第一项开始的；如果没有 initialValue，则从第二个元素开始遍历 callback，此时 callback 的第二个参数是从数组的第二项开始的从数组的第二个元素开始，依次对数组中的每个元素执行回调函数，并将返回值作为下一次回调函数执行时的累积器的值。

最后，`myReduce` 函数返回最后一次回调函数的返回值，即最终的累积值。

> Notes: 此简易实现只为更好地理解 `reduce` 函数的实现原理，并没有考虑很多边界情况和复杂的应用场景。

### 7.6. reduceRight() 将数组元素计算为一个值（从右到左）

`reduceRight()` 方法的功能和 `reduce()` 功能是一样的，不同的是 `reduceRight()` 从数组的末尾向前将数组中的数组项做累加。

```js
array.reduceRight(callback(accumulator, currentValue[, index[, arr]])[, initialValue])
```

参数与 reduce() 一样。

## 8. 解构赋值

### 8.1. entries() 生成数组的可迭代对象

`entries()` 方法主要用于遍历数组或对象的**键值对**。在数组中，该方法返回一个新的数组迭代器对象，该对象包含数组中每个索引的键值对。例如：

```js
const arr = ["a", "b", "c"]
// 调用 arr.entries() 后，可以得到如下结果：
[  
  [0, "a"],  
  [1, "b"],  
  [2, "c"]  
] 

// 使用示例：
const fruits = ["Banana", "Orange", "Apple", "Mango"];
const arr = fruits.entries();
console.log((arr.next()).value) 
console.log((arr.next()).value[1]) 
// 输出：[0, 'Banana']
// 输出：Banana
```

### 8.2. keys() 返回数组的可迭代对象

keys() 方法用于从数组创建一个包含数组键的可迭代对象。

```js
const arr = ["Banana", "Orange", "Apple", "Mango"]; 
const newArr = arr.keys();
for (const iterator of newArr) { 
  console.log(iterator);
} 
// 输出：0 1 2 3
```

## 9. 数组的扁平化

### 9.1. flat() 方法

`flat()` 方法方法会按照一个可指定的深度递归遍历数组，并将所有元素与遍历到的子数组中的元素合并为一个新数组返回，flat() 方法返回一个包含将数组与子数组中所有元素的新数组，该方法可用于：扁平化嵌套数组，扁平化与数组空项。

> Notes: ：`array.flat()` 创建一个新数组，而不会改变原始数组

```js
array.flat([depth])
```

- depth（可选参数）：默认值为 1

```js
// arrays 包含数字和数字数组的混合。 arrays.flat()对数组进行扁平，使其仅包含数字。
const arrays = [0, [1, 3, 5], [2, 4, 6]];
const flatArray = arrays.flat();
console.log(flatArray); // [0, 1, 3, 5, 2, 4, 6]
```

### 9.2. flatMap() 使用映射函数映射每个元素，将结果压缩成新数组

`flatMap()` 方法首先使用映射函数映射每个元素，然后将结果压缩成一个新数组，它与 map 连着深度值为 1 的 flat 几乎相同，但 flatMap 通常在合并成一种方法的效率稍微高一些，flatMap() 方法一个新的数组，其中每个元素都是回调函数的结果，并且结构深度 depth 值为 1。

```js
array.flatMap(callback(currentValue, index, arr), thisArg)
```

参数说明：

- callback 可以生成一个新数组中的元素的函数，可以传入三个参数：
    - currentValue 必需：当前正在数组中处理的元素
    - index 可选：数组中正在处理的当前元素的索引
    - array 可选：被调用的 map 数组
- thisArg 可选：执行 callback 函数时 使用的this 值。

```js
// 箭头函数
flatMap((currentValue) => { /* … */ } )
flatMap((currentValue, index) => { /* … */ } )
flatMap((currentValue, index, array) => { /* … */ } )

// 回调函数
flatMap(callbackFn)
flatMap(callbackFn, thisArg)

// 行内回调函数
flatMap(function(currentValue) { /* … */ })
flatMap(function(currentValue, index) { /* … */ })
flatMap(function(currentValue, index, array){ /* … */ })
flatMap(function(currentValue, index, array) { /* … */ }, thisArg) 
```

flatMap 能用于在 map 期间增删项目（也就是修改 items 的数量），换句话说，它允许遍历很多项使之成为另一些项（靠分别把它们放进去来处理），而不是总是一对一，从这个意义上讲，它的作用类似于 filter 的对立面，只需返回一个 1 项元素数组以保留该项，返回一个多元素数组以添加项，或返回一个 0 项元素数组以删除该项。

```js
let arr1 = ["it's Sunny in", "", "California"];
arr1.map(x => x.split(" "));
// [["it's","Sunny","in"],[""],["California"]]
arr1.flatMap(x => x.split(" "));
// ["it's","Sunny","in", "", "California"] 
```

## 10. 数组的排序

### 10.1. sort()

`sort()` 方法作用是对数组的元素进行排序，如果数组是数字，也是默认按字符串的排序方法进行排序。默认排序顺序为按字母升序，语法格式如下：

```js
array.sort([compare])
```

- 可选参数 `compare(a, b)` 是一个自定义排序顺的回调函数。如果按数字排序，需要指定排序的函数（类似 java 的比较器），如下是函数的比较规则：
    - 如果 a 小于 b，在排序后的数组中 a 应该出现在 b 之前，就返回一个小于 0 的值。
    - 如果 a 等于 b，就返回 0。
    - 如果 a 大于 b，就返回一个大于 0 的值。

> Notes: `array.sort()` 会改变原数组。

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

### 10.2. reverse() 反转数组的元素顺序

reverse() 用于反转数组中元素的顺序，直接操作数组本身。

```js
const fruits = ["Banana", "Orange", "Apple", "Mango"];
console.log(fruits.reverse()) // 输出： ['Mango', 'Apple', 'Orange', 'Banana'] 
```

## 11. 综合应用

### 11.1. isArray() 判断一个对象是否为数组

`isArray()` 方法用于判断一个对象是否为数组，如果对象是数组返回 true，否则返回 false。

```js
Array.isArray(obj)
```

- obj（必需）：要判断的对象。

```js
const fruits = ["Banana", "Orange", "Apple", "Mango"];
console.log(Array.isArray(fruits)) // 输出：true 
```

### 11.2. valueOf() 返回数组对象的原始值

`valueOf()` 方法返回 Array 对象的原始值，该原始值由 Array 对象派生的所有对象继承，`valueOf()` 方法通常由 JavaScript 在后台自动调用，并不显式地出现在代码中，

`valueOf()` 方法不会改变原数组，是数组对象的默认方法，`array.valueOf()` 与 array 的返回值一样。

### 11.3. 数组的拷贝

#### 11.3.1. 方式1：使用展开操作符(...)

- 语法：`const clone = [...array]`
- 作用：复制一个新的数组
- > 注：`[...array]`是创建一个浅拷贝

```js
const colors = ['white', 'black', 'gray'];
const clone = [...colors];

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```

#### 11.3.2. 方式2：[].concat(array)

`[].concat(array)` 是另一种复制数组的方式。值得注意的是：`[].concat(array)` 是创建一个浅拷贝。

```js
const colors = ['white', 'black', 'gray'];
const clone = [].concat(colors);

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```

#### 11.3.3. 方式3：slice()

- 语法：`array.slice()`，利用slice的参数默认值，进行数组的复制
- > `array.slice()` 创建一个浅拷贝。

```js
const colors = ['white', 'black', 'gray'];
const clone = colors.slice();

console.log(clone); // => ['white', 'black', 'gray']
console.log(colors === clone); // => false
```
