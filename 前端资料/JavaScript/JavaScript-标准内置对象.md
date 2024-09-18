## 1. JavaScript 标准内置对象概述

> 参考：https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects

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

### 2.1. 基础使用

`Intl.NumberFormat` 基本的使用示例，用于格式化一个数字：

```js
const number = 123456.789
const formatter = new Intl.NumberFormat()
console.log(formatter.format(number)) // 输出结果：123,456.789
```

在上面的示例中，首先定义了一个数字变量 `number`，然后创建了一个 `Intl.NumberFormat` 实例，并将其存储在变量 `formatter` 中。最后使用 `formatter.format()` 方法来格式化数字，并将结果打印到控制台中。此示例没有提供任何参数来创建 `Intl.NumberFormat` 实例。这意味着它将使用默认设置来格式化数字。

### 2.2. 构造实例参数

通过参数，可以创建自定义格式：

- `locale`：用于格式化数字的语言环境。默认值为当前用户的语言环境。
- `style`：数字格式的样式。可以是`decimal`（十进制）、`currency`（货币）或`percent`（百分比）。默认值为`decimal`。
- `currency`：如果样式为`currency`，则使用的货币代码。默认值为当前用户的货币代码。
- `currencyDisplay`：如果样式为`currency`，则货币符号的显示位置。可以是`symbol`（符号）、`code`（代码）或`name`（名称）。默认值为`symbol`。
- `minimumIntegerDigits`：数字的最小整数位数。默认值为 1。
- `minimumFractionDigits`：数字的最小小数位数。默认值为 0。
- `maximumFractionDigits`：数字的最大小数位数。默认值为 3。
- `minimumSignificantDigits`：数字的最小有效数字位数。默认值为 1。
- `maximumSignificantDigits`：数字的最大有效数字位数。默认值为 21。

```js
const number = 123456.789
const formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})
console.log(formatter.format(number)) // 输出结果：$123,456.79
```

以上示例使用了 en-US 作为语言环境，并将样式设置为 `currency`。还指定了货币代码为 USD，最小小数位数为 2，最大小数位数为 2。这使得输出结果为美元货币格式。

### 2.3. 货币格式化

货币格式化是将数字格式化为特定货币的格式。使用 `Intl.NumberFormat` 可以轻松地将数字格式化为任何货币，并在不同的语言环境中进行显示。例如将格式化数字为美元货币格式：

```js
const number = 1234.56
const formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})
console.log(formatter.format(number)) // 输出结果：$1,234.56
```

示例中，使用了 en-US 作为语言环境，并将样式设置为 `currency`，还指定了货币代码为 USD。此时输出结果为美元货币格式。

### 2.4. 小数格式化

`Intl.NumberFormat` 可以将数字格式化为任何特定小数位数的格式，并在不同的语言环境中进行显示。例如将格式化数字为只有两个小数位：

```js
const number = 1234.567
const formatter = new Intl.NumberFormat('en-US', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})
console.log(formatter.format(number)) // 输出结果：1,234.57
```

示例中使用了 en-US 作为语言环境，并将最小小数位数和最大小数位数都设置为 2。此时输出结果只有两个小数位。

### 2.5. 整数格式化

`Intl.NumberFormat` 可以将数字格式化为特定整数位数的格式，并在不同的语言环境中进行显示。例如将格式化数字为只有四个整数位：

```js
const number = 12345.6789
const formatter = new Intl.NumberFormat('en-US', {
  minimumIntegerDigits: 4,
})
console.log(formatter.format(number)) // 输出结果：12,346
```

示例中使用了 en-US 作为语言环境，并将最小整数位数设置为 4。此时输出结果只有四个整数位。

### 2.6. 百分比格式化

`Intl.NumberFormat` 可以将数字格式化为任何百分比格式，并在不同的语言环境中进行显示。例如将格式化数字为百分比格式：

```js
const number = 0.75
const formatter = new Intl.NumberFormat('en-US', {
  style: 'percent',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})
console.log(formatter.format(number)) // 输出结果：75.00%
```

示例中使用了 en-US 作为语言环境，并将样式设置为 `percent`，还指定了最小小数位数为 2，最大小数位数为 2。此时输出结果为百分比格式。

### 2.7. 计数格式化

`Intl.NumberFormat` 可以将数字格式化为任何计数形式的格式，并在不同的语言环境中进行显示。例如将格式化数字为计数形式的格式：

```js
const number = 12345
const formatter = new Intl.NumberFormat('en-US', {
  notation: 'compact',
})
console.log(formatter.format(number)) // 输出结果：12K
```

### 2.8. 多语言支持

`Intl.NumberFormat` 还支持多语言。使用该 API 可以根据用户的语言环境设置数字格式。例如将格式化数字为德语货币格式：

```js
const number = 123456.789
const formatter = new Intl.NumberFormat('de-DE', {
  style: 'currency',
  currency: 'EUR',
})
console.log(formatter.format(number)) // 输出结果：123.456,79 €
```

示例中使用了 de-DE 作为语言环境，并将样式设置为 currency，还指定了货币代码为 EUR。此时输出结果为德语货币格式。
