# Lodash 工具函数库

## 1. 简介

一个一致性、模块化、高性能的 JavaScript 实用工具库。

官网：https://www.lodashjs.com/

### 1.1. 安装

- 引入js

```html
<script src="lodash.js"></script>
```

- npm 方式安装

```bash
$ npm i --save lodash
```

- Node.js 引入

```js
// Load the full build.
var _ = require('lodash');
// Load the core build.
var _ = require('lodash/core');
// Load the FP build for immutable auto-curried iteratee-first data-last methods.
var fp = require('lodash/fp');
 
// Load method categories.
var array = require('lodash/array');
var object = require('lodash/fp/object');
 
// Cherry-pick methods for smaller browserify/rollup/webpack bundles.
var at = require('lodash/at');
var curryN = require('lodash/fp/curryN');
```

## 2. 对象相关方法

### 2.1. isEqual

```js
_.isEqual(value, other)
```

深比较两者（对象）的值是否相等。

> **注意: **这个方法支持比较 arrays, array buffers, booleans, date objects, error objects, maps, numbers, Object objects, regexes, sets, strings, symbols, 以及 typed arrays. Object 对象值比较自身的属性，不包括继承的和可枚举的属性。 不支持函数和DOM节点比较。

**参数说明**：

- value (*): 用来比较的值
- other (*): 另一个用来比较的值

**返回**：boolean 类型，如果两个值完全相同，那么返回 true，否则返回 false

**示例**：

```js
var object = { 'a': 1 };
var other = { 'a': 1 };
 
_.isEqual(object, other);
// => true
 
object === other;
// => false
```
