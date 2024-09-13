## 1. JavaScript 标准内置对象概述

> 参考：https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects
>
> https://segmentfault.com/a/1190000043627884

术语“全局对象”（或标准内置对象）不应与 **global 对象**混淆。这里的“全局对象”指的是**处在全局作用域里的多个对象**。

### 1.1. 值属性

这些全局属性返回一个简单值，这些值没有自己的属性和方法。

- `globalThis`
- `Infinity`
- `NaN`
- `undefined`

### 1.2. 函数属性

全局函数可以直接调用，不需要在调用时指定所属对象，执行结束后会将结果直接返回给调用者。

- `eval()`
- `isFinite()`
- `isNaN()`
- `parseFloat()`
- `parseInt()`
- `decodeURI()`
- `decodeURIComponent()`
- `encodeURI()`
- `encodeURIComponent()`
- `escape()` 已弃用
- `unescape()` 已弃用

### 1.3. 基本对象

基本对象是定义或使用其他对象的基础。

- `Object`
- `Function`
- `Boolean`
- `Symbol`

### 1.4. 错误对象

错误对象是一种特殊的基本对象。它们拥有基本的 `Error` 类型，同时也有多种具体的错误类型。

- `Error`
- `AggregateError`
- `EvalError`
- `RangeError`
- `ReferenceError`
- `SyntaxError`
- `TypeError`
- `URIError`
- `InternalError` 非标准

### 1.5. 数字和日期对象

用来表示数字、日期和执行数学计算的对象。

- `Number`
- `BigInt`
- `Math`
- `Date`

### 1.6. 字符串

这些对象表示字符串并支持操作字符串。

- `String`
- `RegExp`

### 1.7. 可索引的集合对象

这些对象表示按照索引值来排序的数据集合，包括数组和类型数组，以及类数组结构的对象。

- `Array`
- `Int8Array`
- `Uint8Array`
- `Uint8ClampedArray`
- `Int16Array`
- `Uint16Array`
- `Int32Array`
- `Uint32Array`
- `BigInt64Array`
- `BigUint64Array`
- `Float32Array`
- `Float64Array`

### 1.8. 使用键的集合对象

这些集合对象在存储数据时会使用到键，包括可迭代的 Map 和 Set，支持按照插入顺序来迭代元素。

- `Map`
- `Set`
- `WeakMap`
- `WeakSet`

### 1.9. 结构化数据

这些对象用来表示和操作结构化的缓冲区数据，或使用 JSON（JavaScript Object Notation）编码的数据。

- `ArrayBuffer`
- `SharedArrayBuffer`
- `Atomics`
- `DataView`
- `JSON`

### 1.10. 内存管理对象

这些对象会与垃圾回收机制产生交互。

- `WeakRef`
- `FinalizationRegistry`

### 1.11. 控制抽象对象

控件抽象对象可以帮助构造代码，尤其是异步代码（例如不使用深度嵌套的回调）。

- `Iterator`
- `AsyncIterator`
- `Promise`
- `GeneratorFunction`
- `AsyncGeneratorFunction`
- `Generator`
- `AsyncGenerator`
- `AsyncFunction`

### 1.12. 反射

- `Reflect`
- `Proxy`

### 1.13. 国际化

ECMAScript 核心的附加功能，用于支持多语言处理。

- `Intl`
- `Intl.Collator`
- `Intl.DateTimeFormat`
- `Intl.DisplayNames`
- `Intl.DurationFormat`
- `Intl.ListFormat`
- `Intl.Locale`
- `Intl.NumberFormat`
- `Intl.PluralRules`
- `Intl.RelativeTimeFormat`
- `Intl.Segmenter`

## 2. Intl.NumberFormat

`Intl.NumberFormat` 是一个用于格式化数字的 JavaScript 国际化 API。它使开发人员可以根据用户的语言环境和地区设置自定义数字格式。这意味着可以使用该 API 来格式化数字，以便在不同的语言和地区中进行显示。





