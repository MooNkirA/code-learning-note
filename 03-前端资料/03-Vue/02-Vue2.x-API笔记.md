# Vue.js API 笔记

## 1. 指令

### 1.1. v-text

#### 1.1.1. 相关参数

- 预期：`string`

#### 1.1.2. 用法

更新元素的 `textContent`。如果要更新部分的` textContent`，需要使用` {{ Mustache }}` 插值。

#### 1.1.3. 示例

```html
<span v-text="msg"></span>
<!-- 和下面的一样 -->
<span>{{msg}}</span>
```

### 1.2. v-html

#### 1.2.1. 相关参数

- 预期：`string`

#### 1.2.2. 用法

更新元素的 `innerHTML`。**注意：内容按普通 HTML 插入 - 不会作为 Vue 模板进行编译**。如果试图使用 `v-html` 组合模板，可以重新考虑是否通过使用组件来替代。


### 1.3. v-on

#### 1.3.1. 相关参数

- 缩写：`@`
- 预期：`Function | Inline Statement | Object`
- 参数：`event`

#### 1.3.2. 修饰符

- `.stop` - 调用 `event.stopPropagation()`。
- `.prevent` - 调用 `event.preventDefault()`。
- `.capture` - 添加事件侦听器时使用 capture 模式。
- `.self` - 只当事件是从侦听器绑定的元素本身触发时才触发回调。
- `.{keyCode | keyAlias}` - 只当事件是从特定键触发时才触发回调。
- `.native` - 监听组件根元素的原生事件。
- `.once` - 只触发一次回调。
- `.left` - (2.2.0) 只当点击鼠标左键时触发。
- `.right` - (2.2.0) 只当点击鼠标右键时触发。
- `.middle` - (2.2.0) 只当点击鼠标中键时触发。
- `.passive` - (2.3.0) 以 { passive: true } 模式添加侦听器

#### 1.3.3. 用法

- 绑定事件监听器。事件类型由参数指定。表达式可以是一个方法的名字或一个内联语句，如果没有修饰符也可以省略。
- 用在普通元素上时，只能监听**原生 DOM 事件**。用在自定义元素组件上时，也可以监听子组件触发的**自定义事件**。
- 在监听原生DOM事件时，方法以事件为唯一的参数。如果使用内联语句，语句可以访问一个`$event`属性：`v-on:click="handle('ok', $event)"`。
- 从 `2.4.0` 开始，`v-on` 同样支持不带参数绑定一个事件/监听器键值对的对象。注意当使用对象语法时，是不支持任何修饰器的。

#### 1.3.4. 示例

```html
<!-- 方法处理器 -->
<button v-on:click="doThis"></button>

<!-- 动态事件 (2.6.0+) -->
<button v-on:[event]="doThis"></button>

<!-- 内联语句 -->
<button v-on:click="doThat('hello', $event)"></button>

<!-- 缩写 -->
<button @click="doThis"></button>

<!-- 动态事件缩写 (2.6.0+) -->
<button @[event]="doThis"></button>

<!-- 停止冒泡 -->
<button @click.stop="doThis"></button>

<!-- 阻止默认行为 -->
<button @click.prevent="doThis"></button>

<!-- 阻止默认行为，没有表达式 -->
<form @submit.prevent></form>

<!--  串联修饰符 -->
<button @click.stop.prevent="doThis"></button>

<!-- 键修饰符，键别名 -->
<input @keyup.enter="onEnter">

<!-- 键修饰符，键代码 -->
<input @keyup.13="onEnter">

<!-- 点击回调只会触发一次 -->
<button v-on:click.once="doThis"></button>

<!-- 对象语法 (2.4.0+) -->
<button v-on="{ mousedown: doThis, mouseup: doThat }"></button>
```

在子组件上监听自定义事件 (当子组件触发“my-event”时将调用事件处理器)：

```html
<my-component @my-event="handleThis"></my-component>

<!-- 内联语句 -->
<my-component @my-event="handleThis(123, $event)"></my-component>

<!-- 组件中的原生事件 -->
<my-component @click.native="onClick"></my-component>
```

#### 1.3.5. 关于右键点击的问题

**从vue 2.2.0版本开始可以使用`@click.right="方法"`属性来指定当鼠标右键点击时才触发的事件，但这种方式点击右键后会出现win系统的右键菜单，如果不想出现右键菜单的话，则需要使用`@contextmenu.prevent="方法"`即可**


