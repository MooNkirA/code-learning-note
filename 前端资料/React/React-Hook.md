## 1. React Hook 概述

很久很久之前，函数式组件由于没有 this，故不能获取 state, refs 等，因此函数式组件不被看好。但是 React 16.8 版本引入了全新的 API，叫做 React Hooks，颠覆了以前的用法，自此函数式组件崛起。

### 1.1. 类式组件的缺点

React 的核心是组件。v16.8 版本之前，组件的标准写法是类（class）。下面是一个简单的组件类。

```jsx
import React, { Component } from "react";

export default class Button extends Component {
  constructor() {
    super();
    this.state = { buttonText: "Click me, please" };
    this.handleClick = this.handleClick.bind(this);
  }
  handleClick() {
    this.setState(() => {
      return { buttonText: "Thanks, been clicked!" };
    });
  }
  render() {
    const { buttonText } = this.state;
    return <button onClick={this.handleClick}>{buttonText}</button>;
  }
}
```

这个组件类仅仅是一个按钮，但可以看出代码已经很"重"了。真实的 React App 由多个类按照层级，一层层构成，复杂度成倍增长。组件类主要有以下几个缺点：

- 大型组件很难拆分和重构，也很难测试。
- 业务逻辑分散在组件的各个方法之中，导致重复逻辑或关联逻辑。
- 组件类引入了复杂的编程模式，比如 render props 和高阶组件。

### 1.2. 函数式组件

React 团队希望，组件不要变成复杂的容器，最好只是数据流的管道。因此**组件的最佳写法应该是函数，而不是类**。早期的 React 也是支持函数组件：

```jsx
function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}
```

但是早期这种写法有重大限制，必须是纯函数，不能包含状态，也不支持生命周期方法，因此无法取代类。然后在 v16.8 版本后，推出 React Hooks，即加强版函数组件，完全不使用"类"，就能写出一个全功能的组件。

### 1.3. React Hooks

Hook 是 React 16.8.0 版本增加的新特性/新语法，可以在函数组件中使用 state，refs 以及其他的 React 特性。React 在 v16.8 版本后，建议组件尽量写成纯函数，如果需要外部功能和副作用，就用钩子把外部代码"钩"进来。React Hooks 就是那些钩子。

所有的钩子都是为函数引入外部功能，所以 React 约定，钩子一律使用 `use` 前缀命名，便于识别。即使用 xxx 功能，钩子就命名为 useXxx。需要什么功能，就使用什么钩子。React 默认提供了一些常用钩子，开发者也可以封装自己的钩子。

React 默认提供的四个最常用的钩子：

- useState()
- useContext()
- useReducer()
- useEffect()

## 2. 函数式组件

### 2.1. 概述

组件是 React 的核心概念之一，它们是构建用户界面（UI）的基础。

React 允许将标签、CSS 和 JavaScript 组合成自定义『组件』，即**应用程序中可复用的 UI 元素**。

### 2.2. 定义组件 

<font color=red>**React 组件是一段可以使用标签进行扩展的 JavaScript 函数**</font>。例如：

```jsx
export default function Profile() {
  return (
    <img
      src="https://i.imgur.com/MK3eW3Am.jpg"
      alt="Katherine Johnson"
    />
  )
}
```

#### 2.2.1. 导出组件

`export default` 前缀是一种 JavaScript 标准语法（非 React 的特性）。它允许导出一个文件中的主要函数以便可以在其他文件引入它。

#### 2.2.2. 定义函数

使用 `function Profile() { }` 定义名为 Profile 的 JavaScript 函数。

> Notes: React 组件是常规的 JavaScript 函数，但<font color=red>**组件的名称必须以大写字母开头**</font>，否则它们将无法运行！

#### 2.2.3. 添加标签

示例中的组件，使用了 JSX 语法，在 JavaScript 中嵌入标签，并最终返回一个带有 `src` 和 `alt` 属性的 `<img />` 标签。返回语句可以全写在一行上，如下面组件中所示：

```jsx
return <img src="https://i.imgur.com/MK3eW3As.jpg" alt="Katherine Johnson" />;
```

如果标签和 `return` 关键字不在同一行，则必须把它包裹在一对括号中，如下所示：

```jsx
return (
  <div>
    <img src="https://i.imgur.com/MK3eW3As.jpg" alt="Katherine Johnson" />
  </div>
);
```

> Notes: 没有括号包裹的话，任何在 `return` 下一行的代码都将<u>**被忽略**</u>！

### 2.3. 使用组件

定义了 Profile 组件后，就可以在其他组件中使用它。例如，可以导出一个内部使用了多个 Profile 组件的 Gallery 组件：

```jsx
function Profile() {
  return (
    <img src="https://i.imgur.com/MK3eW3As.jpg" alt="Katherine Johnson" />
  );
}

export default function Gallery() {
  return (
    <section>
      <h1>了不起的科学家</h1>
      <Profile />
      <Profile />
      <Profile />
    </section>
  );
}
```

### 2.4. 组件的注意事项

基于上面的示例：

- `<section>` 是小写的，所以 React 知道指的是 HTML 标签。
- `<Profile />` 以大写 P 开头，所以 React 知道想要使用名为 Profile 的组件。

值得注意的是，最终在浏览器中渲染的都只有组件中包含的 html 标签，而不会看到自定义的标签（组件）。

### 2.5. 嵌套和组织组件

#### 2.5.1. 概述

组件是常规的 JavaScript 函数，所以可以将多个组件保存在同一份文件中。当组件相对较小或彼此紧密相关时，这是一种省事的处理方式。随着这个文件变得臃肿，也可以随时将里面部分组件移动到单独的文件中。

#### 2.5.2. 嵌套陷阱

组件可以渲染其他组件，但是**不要嵌套他们的定义**。例如：

```jsx
export default function Gallery() {
  // 🔴 永远不要在组件中定义组件
  function Profile() {
    // ...
  }
  // ...
}
```

上面这段代码**非常慢，并且会导致 bug 产生**。因此应该在顶层定义每个组件：

```jsx
export default function Gallery() {
  // ...
}

// ✅ 在顶层声明组件
function Profile() {
  // ...
}
```

建议当子组件需要使用父组件的数据时，可以通过 props 的形式进行传递，而不是嵌套定义。


