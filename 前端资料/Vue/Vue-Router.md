## 1. vue-router 概述

### 1.1. 什么是 vue-router

vue-router 是 vue.js 官方推荐的路由解决方案。它只能结合 vue 项目进行使用，能够轻松的管理 SPA 项目中组件的切换。

### 1.2. vue-router 的版本

vue-router 目前有 3.x 的版本和 4.x 的版本。其中：

- vue-router 3.x（只能结合 vue2 进行使用）官方文档地址：https://router.vuejs.org/zh/
- vue-router 4.x（只能结合 vue3 进行使用）的官方文档地址：https://next.router.vuejs.org/zh/

### 1.3. vue-router 安装

#### 1.3.1. 直接下载 / CDN

下载地址：https://unpkg.com/vue-router/dist/vue-router.js

在 Vue 后面加载 vue-router，它会自动安装的：

```html
<script src="/path/to/vue.js"></script>
<script src="/path/to/vue-router.js"></script>
```

#### 1.3.2. NPM 安装方式

```shell
npm install vue-router -S
```

如果在一个模块化工程中使用它，必须要通过 `Vue.use()` 明确地安装路由功能：

```js
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)
```

> 如果使用全局的 script 标签，则无须如此 (手动安装)。

### 1.4. vue-router 基础使用步骤

用 Vue.js + Vue Router 创建单页应用，然后将组件 (components) 映射到路由 (routes)，配置 Vue Router 在哪里渲染它们。

#### 1.4.1. 路由实例的创建和配置

```js
// 1. 如果使用模块化机制编程，导入Vue和VueRouter，用于调用 Vue.use(VueRouter)
import Vue from 'vue'
import Router from 'vue-router'

// 2. 调用 Vue.use() 函数，将 VueRouter 安装成 Vue 的插件
Vue.use(Router)

// 3. 定义路由
// 每个路由应该映射一个组件。 其中"component" 可以是通过 Vue.extend() 创建的组件构造器，或者，只是一个组件配置对象。
const routes = [
  { path: '/foo', component: Foo },
  { path: '/bar', component: Bar }
]

// 3. 创建 router 实例，然后传 `routes` 配置或别的配置参数
const router = new VueRouter({
  routes // (缩写) 相当于 routes: routes
})

// 4. 创建和挂载根实例。通过 router 配置参数注入路由，从而让整个应用都有路由功能
const app = new Vue({
  router
}).$mount('#app')
```

在整个项目中，通过注入路由器，可以在任何组件内通过 `this.$router` 访问路由器。即不需要在每个独立封装路由的组件中都导入路由。

> 注：正常的项目中，**路由对象的创建和配置都会独立到一个文件中（或一个模块中）**，建议在项目的src目录下创建router文件，在此目录下创建js文件，创建与配置路由实例并导出

#### 1.4.2. 路由组件的渲染

只要在项目中安装和配置了 vue-router，即可使用内置组件 `<router-view>`，该组件用于 html 或 vue 文件中的模板页面中，该标签是路由出口，路由匹配到的组件将渲染此标签中，相当于页面渲染区域的“占位符”

```html
<p>
  <!-- 使用 router-link 组件来导航。通过传入 `to` 属性指定链接。 -->
  <!-- <router-link> 默认会被渲染成一个 `<a>` 标签 -->
  <router-link to="/foo">Go to Foo</router-link>
  <router-link to="/bar">Go to Bar</router-link>
</p>

<!-- 路由出口 -->
<!-- 路由匹配到的组件将渲染在这里 -->
<router-view></router-view>
```

> <font color=red>**注意：当 `<router-link>` 对应的路由匹配成功，将自动设置 `class` 属性值 `.router-link-active`。**</font>

## 2. vue-router 基础用法

### 2.1. 声明路由的匹配规则

在 `new VueRouter()` 的构造器可以传入构建选项对象，有以下属性：

#### 2.1.1. routes 属性

`routes` 属性类型: `Array<RouteConfig>`，用于定义路由的映射

`RouteConfig` 的类型定义：

```js
interface RouteConfig = {
  path: string,
  component?: Component,
  name?: string, // 命名路由
  components?: { [name: string]: Component }, // 命名视图组件
  redirect?: string | Location | Function,
  props?: boolean | Object | Function,
  alias?: string | Array<string>,
  children?: Array<RouteConfig>, // 嵌套路由
  beforeEnter?: (to: Route, from: Route, next: Function) => void,
  meta?: any,

  // 2.6.0+
  caseSensitive?: boolean, // 匹配规则是否大小写敏感？(默认值：false)
  pathToRegexpOptions?: Object // 编译正则的选项
}
```

#### 2.1.2. mode 属性

用于配置路由模式

- 类型: `string`
- 默认值: "`hash`" (浏览器环境) | "`abstract`" (Node.js 环境)
- 可选值: "`hash`" | "`history`" | "`abstract`"
    - `hash`: 使用 URL hash 值来作路由。支持所有浏览器，包括不支持 HTML5 History Api 的浏览器。
    - `history`: 依赖 HTML5 History API 和服务器配置。查看 HTML5 History 模式。
    - `abstract`: 支持所有 JavaScript 运行环境，如 Node.js 服务器端。如果发现没有浏览器的 API，路由会自动强制进入这个模式。

#### 2.1.3. base 属性

用于配置应用的基路径。

- 类型: `string`
- 默认值: "`/`"

> 例如，如果整个单页应用服务在 `/app/` 下，然后 `base` 就应该设为 "`/app/`"。

#### 2.1.4. linkActiveClass 属性

全局配置 `<router-link>` 默认的激活的 `class`

- 类型: `string`
- 默认值: "`router-link-active`"

#### 2.1.5. linkExactActiveClass 属性

全局配置 `<router-link>` 默认的精确激活的 `class`

- 类型: `string`
- 默认值: "`router-link-exact-active`"

#### 2.1.6. scrollBehavior 属性

- 类型: `Function`

```ts
type PositionDescriptor =
  { x: number, y: number } |
  { selector: string } |
  void

type scrollBehaviorHandler = (
  to: Route,
  from: Route,
  savedPosition?: { x: number, y: number }
) => PositionDescriptor | Promise<PositionDescriptor>
```

#### 2.1.7. parseQuery / stringifyQuery 属性

提供自定义查询字符串的解析/反解析函数。覆盖默认行为。

- 类型: `Function`

#### 2.1.8. fallback 属性

- 类型: `boolean`
- 默认值: `true`

当浏览器不支持 `history.pushState` 控制路由是否应该回退到 `hash` 模式。默认值为 `true`。

在 IE9 中，设置为 `false` 会使得每个 `<router-link>` 导航都触发整页刷新。它可用于工作在 IE9 下的服务端渲染应用，因为一个 `hash` 模式的 URL 并不支持服务端渲染。

### 2.2. router-view 渲染视图组件

`<router-view>` 组件是一个 functional 组件，渲染路径匹配到的视图组件。`<router-view>` 渲染的组件还可以内嵌自己的 `<router-view>`，根据嵌套路径，渲染嵌套组件。

它也是个组件，所以可以配合 `<transition>` 和 `<keep-alive>` 使用。如果两个结合一起用，要确保在内层使用 `<keep-alive>`：

```html
<transition>
  <keep-alive>
    <router-view></router-view>
  </keep-alive>
</transition>
```

`<router-view>` 组件的 Props 属性如下：

#### 2.2.1. name 属性

- 类型: `string`
- 默认值: "`default`"

如果 `<router-view>` 设置了名称，则会渲染对应的路由配置中 `components` 下的相应组件。

### 2.3. 声明式导航

在浏览器中，点击链接实现导航的方式，叫做**声明式导航**。如：普通网页中点击 `<a>` 链接、vue 项目中点击 `<router-link>` 都属于声明式导航

#### 2.3.1. router-link 导航组件

`<router-link>` 组件支持用户在具有路由功能的应用中 (点击) 导航。 通过 `to` 属性指定目标地址，默认渲染成带有正确链接的 `<a>` 标签，可以通过配置 `tag` 属性生成别的标签。另外，当目标路由成功激活时，链接元素自动设置一个表示激活的 CSS 类名。

`<router-link>` 比 `<a>` 标签优劣如下：

- 无论是 HTML5 history 模式还是 hash 模式，它的表现行为一致，所以，当要切换路由模式，或者在 IE9 降级使用 hash 模式，无须作任何变动。
- 在 HTML5 history 模式下，`router-link` 会守卫点击事件，让浏览器不再重新加载页面。
- 当你在 HTML5 history 模式下使用 `base` 选项之后，所有的 `to` 属性都不需要写 (基路径) 了

#### 2.3.2. router-link 相关属性

##### 2.3.2.1. to

- 类型: string | Location
- required（必需）

表示目标路由的链接。当被点击后，内部会立刻把 `to` 的值传到 `router.push()`，所以这个值可以是一个字符串或者是描述目标位置的对象。

```html
<!-- 字符串 -->
<router-link to="home">Home</router-link>
<!-- 渲染结果 -->
<a href="home">Home</a>

<!-- 使用 v-bind 的 JS 表达式 -->
<router-link v-bind:to="'home'">Home</router-link>

<!-- 不写 v-bind 也可以，就像绑定别的属性一样 -->
<router-link :to="'home'">Home</router-link>

<!-- 同上 -->
<router-link :to="{ path: 'home' }">Home</router-link>

<!-- 命名的路由 -->
<router-link :to="{ name: 'user', params: { userId: 123 }}">User</router-link>

<!-- 带查询参数，下面的结果为 /register?plan=private -->
<router-link :to="{ path: 'register', query: { plan: 'private' }}">Register</router-link>
```

##### 2.3.2.2. replace

- 类型: boolean
- 默认值: false

设置 `replace` 属性后，当点击时，会调用 `router.replace()` 而不是 `router.push()`，于是导航后不会留下 `history` 记录。

```html
<router-link :to="{ path: '/abc'}" replace></router-link>
```

##### 2.3.2.3. append

- 类型: boolean
- 默认值: false

设置 `append` 属性后，则在当前 (相对) 路径前添加基路径。例如，从 `/a` 导航到一个相对路径 `b`，如果没有配置 `append`，则路径为 `/b`，如果配了，则为 `/a/b`

```html
<router-link :to="{ path: 'relative/path'}" append></router-link>
```


##### 2.3.2.4. tag

- 类型: string
- 默认值: "`a`"

有时候想要 `<router-link>` 渲染成某种标签，例如 `<li>`。 于是使用 `tag` 属性指定何种标签，同样它还是会监听点击，触发导航。

```html
<router-link to="/foo" tag="li">foo</router-link>
<!-- 渲染结果 -->
<li>foo</li>
```

##### 2.3.2.5. active-class

- 类型: string
- 默认值: "`router-link-active`"

设置链接激活时使用的 CSS 类名。默认值可以通过路由的构造选项 `linkActiveClass` 来全局配置。

##### 2.3.2.6. exact

- 类型: boolean
- 默认值: false

“是否激活”默认类名的依据是包含匹配。 举个例子，如果当前的路径是 `/a` 开头的，那么 `<router-link to="/a">` 也会被设置 CSS 类名。

按照这个规则，每个路由都会激活 `<router-link to="/">`，想要链接使用“精确匹配模式”，则使用 `exact` 属性：

```html
<!-- 这个链接只会在地址为 / 的时候被激活 -->
<router-link to="/" exact></router-link>
```

##### 2.3.2.7. event

- 类型: string | `Array<string>`
- 默认值: '`click`'

声明可以用来触发导航的事件。可以是一个字符串或是一个包含字符串的数组。


##### 2.3.2.8. exact-active-class

- 类型: string
- 默认值: "`router-link-exact-active`"

配置当链接被精确匹配的时候应该激活的 class。注意默认值也是可以通过路由构造函数选项 `linkExactActiveClass` 进行全局配置的。

##### 2.3.2.9. aria-current-value

- 类型: '`page`' | '`step`' | '`location`' | '`date`' | '`time`' | '`true`' | '`false`'
- 默认值: "`page`"

当链接根据精确匹配规则激活时配置的 `aria-current` 的值。这个值应该是 ARIA 规范中允许的 `aria-current` 的值。在绝大多数场景下，默认值 `page` 应该是最合适的。

### 2.4. 编程式导航

在浏览器中，调用 API 方法实现导航的方式，叫做**编程式导航**。例如：普通网页中调用 `location.href` 跳转到新页面的方式，属于编程式导航

除了使用 `<router-link>` 创建 `<a>` 标签来定义导航链接，还可以借助 `router` 的实例方法，通过编写代码来实现。vue-router 提供了许多编程式导航的 API，其中最常用的导航 API 分别是：

1. `this.$router.push('hash 地址')` : 跳转到指定 hash 地址，并增加一条历史记录
2. `this.$router.replace('hash 地址')` : 跳转到指定的 hash 地址，并替换掉当前的历史记录
3. `this.$router.go(数值n)` : 实现导航历史前进、后退

#### 2.4.1. router.push

```js
router.push(location, onComplete?, onAbort?)
```

注意：在 Vue 实例内部，可以通过 `$router` 访问路由实例。因此可以调用 `this.$router.push`

想要导航到不同的 URL，则使用 `router.push` 方法。这个方法会向 `history` 栈添加一个新的记录，所以，当用户点击浏览器后退按钮时，则回到之前的 URL。

点击 `<router-link :to="...">` 等同于 `router.push(...)` 方法在内部调用。该方法的参数可以是一个字符串路径，或者一个描述地址的对象。例如：

```js
// 字符串
router.push('home')

// 对象
router.push({ path: 'home' })

// 命名的路由
router.push({ name: 'user', params: { userId: '123' }})

// 带查询参数，变成 /register?plan=private
router.push({ path: 'register', query: { plan: 'private' }})
```

注意：如果提供了 `path`，`params` 会被忽略，上述例子中的 `query` 并不属于这种情况。取而代之的是下面例子的做法，需要提供路由的 `name` 或手写完整的带有参数的 `path`：

```js
const userId = '123'
router.push({ name: 'user', params: { userId }}) // -> /user/123
router.push({ path: `/user/${userId}` }) // -> /user/123
// 这里的 params 不生效
router.push({ path: '/user', params: { userId }}) // -> /user
```

> *同样的规则也适用于 `router-link` 组件的 `to` 属性。*

在 2.2.0+，可选的在 `router.push` 或 `router.replace` 中提供 `onComplete` 和 `onAbort` 回调作为第二个和第三个参数。这些回调将会在导航成功完成 (在所有的异步钩子被解析之后) 或终止 (导航到相同的路由、或在当前导航完成之前导航到另一个不同的路由) 的时候进行相应的调用。在 3.1.0+，可以省略第二个和第三个参数，此时如果支持 `Promise`，`router.push` 或 `router.replace` 将返回一个 `Promise`

注意：如果目的地和当前路由相同，只有参数发生了改变 (比如从一个用户资料到另一个 `/users/1` -> `/users/2`)，需要使用 `beforeRouteUpdate` 来响应这个变化 (比如抓取用户信息)

#### 2.4.2. router.replace

```js
router.replace(location, onComplete?, onAbort?)
```

`router.replace` 方法可以跳转到指定的 hash 地址，从而展示对应的组件页面。跟 `router.push` 用法一致，唯一的不同就是，它不会向 `history` 添加新记录，而是替换掉当前的 `history` 记录。

编程式的 `router.replace(...)` 方法相当于声明式的 `<router-link :to="..." replace>`

#### 2.4.3. router.go

这个方法的参数是一个整数，意思是在 `history` 记录中向前或者后退多少步，类似 `window.history.go(n)`

```js
// 在浏览器记录中前进一步，等同于 history.forward()
router.go(1)

// 后退一步记录，等同于 history.back()
router.go(-1)

// 前进 3 步记录
router.go(3)

// 如果 history 记录不够用，那就默默地失败呗
router.go(-100)
router.go(100)
```

#### 2.4.4. router.go 的简化用法

在实际开发中，一般只会前进和后退一层页面。因此 vue-router 提供了如下两个便捷方法：

- `$router.back()` : 在历史记录中，后退到上一个页面
- `$router.forward()` : 在历史记录中，前进到上一个页面

### 2.5. 命名路由

通过一个名称来标识一个路由称为命名路由。在创建 `Router` 实例的时候，在 `routes` 配置中给某个路由设置名称。

```js
const router = new VueRouter({
  routes: [
    {
      path: '/user/:userId',
      name: 'user',
      component: User
    }
  ]
})
```

声明式导航跳转命名路由，给 `router-link` 的 `to` 属性传一个对象：

```html
<router-link :to="{ name: 'user', params: { userId: 123 }}">User</router-link>
```

编程式导航跳转命名路由，在 `push` 方法中传入一个对象

```js
router.push({ name: 'user', params: { userId: 123 } })
```

### 2.6. 重定向和别名

#### 2.6.1. 路由重定向

路由重定向指的是：用户在访问地址 `/A` 的时候，强制用户跳转到地址 `/B`，从而展示特定的组件页面。

重定向也是通过 `routes` 配置来完成，，下面例子是从 `/a` 重定向到 `/b`：

```js
const router = new VueRouter({
  routes: [
    { path: '/a', redirect: '/b' }
  ]
})
```

重定向的目标也可以是一个命名的路由：

```js
const router = new VueRouter({
  routes: [
    { path: '/a', redirect: { name: 'foo' }}
  ]
})
```

重定向也可以是一个方法，动态返回重定向目标：

```js
const router = new VueRouter({
  routes: [
    { path: '/a', redirect: to => {
      // 方法接收 目标路由 作为参数
      // return 重定向的 字符串路径/路径对象
    }}
  ]
})
```

> 注意：导航守卫并没有应用在跳转路由上，而仅仅应用在其目标上。

#### 2.6.2. 别名

`/a` 的别名是 `/b`，意味着，当用户访问 `/b` 时，URL 会保持为 `/b`，但是路由匹配则为 `/a`，就像用户访问 `/a` 一样。具体的路由配置是：

```js
const router = new VueRouter({
  routes: [
    { path: '/a', component: A, alias: '/b' }
  ]
})
```

“别名”的功能让你可以自由地将 UI 结构映射到任意的 URL，而不是受限于配置的嵌套路由结构。

### 2.7. 嵌套路由

通过路由实现组件的嵌套展示，叫做嵌套路由。

实际项目应用界面，通常由多层嵌套的组件组合而成。同样地，URL 中各段动态路径也按某种结构对应嵌套的各层组件，例如：

```
/user/foo/profile                     /user/foo/posts
+------------------+                  +-----------------+
| User             |                  | User            |
| +--------------+ |                  | +-------------+ |
| | Profile      | |  +------------>  | | Posts       | |
| |              | |                  | |             | |
| +--------------+ |                  | +-------------+ |
+------------------+                  +-----------------+
```

![](images/20211202164459732_11608.png)

1. 点击父级路由链接显示模板内容`<router-view>`
2. 模板内容中又有子级路由链接`<router-link>`
3. 点击子级路由链接显示子级模板内容`<router-view>`

使用步骤：

- 创建父级组件。此组件中的 `<router-view>` 是最顶层的出口，渲染最高级路由匹配到的组件。

```html
<div id="app">
  <router-view></router-view>
</div>
```

```js
const User = {
  template: '<div>User {{ $route.params.id }}</div>'
}

const router = new VueRouter({
  routes: [{ path: '/user/:id', component: User }]
})
```

- 在一个被渲染组件包含嵌套 `<router-view>`。例如，在 User 组件的模板添加一个 `<router-view>`：

```js
const User = {
  template: `
    <div class="user">
      <h2>User {{ $route.params.id }}</h2>
      <router-view></router-view>
    </div>
  `
}
```

- 最后要在嵌套的出口中渲染组件，需要在 `VueRouter` 的参数中使用 `children` 配置：

```js
const router = new VueRouter({
  routes: [
    {
      path: '/user/:id',
      component: User,
      children: [
        {
          // 当 /user/:id/profile 匹配成功，
          // UserProfile 会被渲染在 User 的 <router-view> 中
          path: 'profile',
          component: UserProfile
        },
        {
          // 当 /user/:id/posts 匹配成功
          // UserPosts 会被渲染在 User 的 <router-view> 中
          path: 'posts',
          component: UserPosts
        }
      ]
    }
  ]
})
```

<font color=red>**值得注意：以 `/` 开头的嵌套路径会被当作根路径，所以子路由不能以“`/`”开头，框架会自动拼接到上级路径的后面。这样的设计是为了使用嵌套组件而无须设置嵌套的路径。**</font>

`children` 配置就是像 `routes` 配置一样的路由配置数组，所以可以嵌套多层路由。

基于上面的配置，当访问 `/user/foo` 时，User 的出口是不会渲染任何东西，这是因为没有匹配到合适的子路由。如果想要渲染点什么，可以提供一个“空的”子路由：

```js
const router = new VueRouter({
  routes: [
    {
      path: '/user/:id',
      component: User,
      children: [
        // 当 /user/:id 匹配成功，
        // UserHome 会被渲染在 User 的 <router-view> 中
        { path: '', component: UserHome }

        // ...其他子路由
      ]
    }
  ]
})
```

### 2.8. 动态路由匹配

动态路由指的是：把 Hash 地址中可变的部分定义为参数项，从而提高路由规则的复用性。

在 vue-router 的路由路径中使用“动态路径参数”(dynamic segment) 来匹配到的所有路由，全都映射到同个组件。

```js
const User = {
  template: '<div>User</div>'
}

const router = new VueRouter({
  routes: [
    // 动态路径参数 以冒号开头
    { path: '/user/:id', component: User }
  ]
})
```

一个“路径参数”使用冒号 `:` 标记。当匹配到一个路由时，动态匹配的参数值会被设置到 `this.$route.params`，可以在每个组件内使用。

```js
const User = {
  template: '<div>User {{ $route.params.id }}</div>'
}
```

也可以在一个路由中设置多段“路径参数”，对应的值都会设置到 `$route.params` 中

|             模式              |       匹配路径       |             $route.params              |
| ----------------------------- | ------------------- | -------------------------------------- |
| /user/:username               | /user/evan          | `{ username: 'evan' }`                 |
| /user/:username/post/:post_id | /user/evan/post/123 | `{ username: 'evan', post_id: '123' }` |

除了 `$route.params` 外，`$route` 对象还提供了其它有用的信息，例如，`$route.query` (如果 URL 中有查询参数)、`$route.hash` 等等。

#### 2.8.1. 开启 props 传参

为了简化路由参数的获取形式，vue-router 允许在路由规则中开启 `props` 传参。在理由配置规则中增加 `props: true` 属性，直接将路径参数绑定到组件的 `props` 中

```js
const router = new VueRouter({
  routes: [
    // 动态路径参数 以冒号开头
    { path: '/user/:id', component: User, props: true }
  ]
})
```

组件中直接通过 `props` 中可以获取

```js
const User = {
  props: ['id'],
  template: '<div>User {{ id }}</div>'
}
```

#### 2.8.2. 响应路由参数的变化

当使用路由参数时，例如从 /user/foo 导航到 /user/bar，原来的组件实例会被复用。因为两个路由都渲染同个组件，比起销毁再创建，复用则显得更加高效。不过，这也意味着组件的生命周期钩子不会再被调用。

复用组件时，想对路由参数的变化作出响应的话，可以简单地 `watch` (监测变化) `$route` 对象：

```js
const User = {
  template: '...',
  watch: {
    $route(to, from) {
      // 对路由变化作出响应...
    }
  }
}
```

或者使用 2.2 中引入的 `beforeRouteUpdate` 导航守卫：

```js
const User = {
  template: '...',
  beforeRouteUpdate(to, from, next) {
    // react to route changes...
    // don't forget to call next()
  }
}
```

#### 2.8.3. 捕获所有路由或 404 Not found 路由

常规参数只会匹配被 `/` 分隔的 URL 片段中的字符。如果想匹配任意路径，可以使用通配符 (`*`)：

```js
{
  // 会匹配所有路径
  path: '*'
}
{
  // 会匹配以 `/user-` 开头的任意路径
  path: '/user-*'
}
```

当使用通配符路由时，请确保路由的顺序是正确的，也就是说含有通配符的路由应该放在最后。路由 `{ path: '*' }` 通常用于客户端 404 错误。如果使用了History 模式，请确保正确配置的服务器。

当使用一个通配符时，`$route.params` 内会自动添加一个名为 `pathMatch` 参数。它包含了 URL 通过通配符被匹配的部分：

```js
// 给出一个路由 { path: '/user-*' }
this.$router.push('/user-admin')
this.$route.params.pathMatch // 'admin'
// 给出一个路由 { path: '*' }
this.$router.push('/non-existing')
this.$route.params.pathMatch // '/non-existing'
```

## 3. vue-router 进阶用法

### 3.1. 导航守卫

vue-router 提供的导航守卫主要用来通过跳转或取消的方式守卫导航。有多种机会植入路由导航过程中：全局的，单个路由独享的，或者组件级的。

<font color=red>**参数或查询的改变并不会触发进入/离开的导航守卫**</font>。可以通过观察 `$route` 对象来应对这些变化，或使用 `beforeRouteUpdate` 的组件内守卫。

> 导航守卫可以用于**控制路由的访问权限**

#### 3.1.1. 全局前置守卫

可以使用 `router.beforeEach` 注册一个全局前置守卫。每次发生路由的导航跳转时，都会触发全局前置守卫。因此，在全局前置守卫中，可以对每个路由进行类似访问权限的控制：

```js
const router = new VueRouter({ ... })

router.beforeEach((to, from, next) => {
  // ...
})
```

当一个导航触发时，全局前置守卫按照创建顺序调用。守卫是异步解析执行，此时导航在所有守卫 `resolve` 完之前一直处于**等待中**状态。

- `to`: `Route`路由对象，即将要进入的目标
- `from`: `Route`路由对象，当前导航正要离开的路由
- `next`: Function回调函数，一定要调用该方法来 `resolve` 这个钩子。执行效果依赖 `next` 方法的调用参数。
    - `next()`: 进行管道中的下一个钩子。如果全部钩子执行完了，则导航的状态就是 confirmed (确认的)。
    - `next(false)`: 中断当前的导航。如果浏览器的 URL 改变了 (可能是用户手动或者浏览器后退按钮)，那么 URL 地址会重置到 from 路由对应的地址。
    - `next('/')` 或者 `next({ path: '/' })`: 跳转到一个不同的地址。当前的导航被中断，然后进行一个新的导航。可以向 `next` 传递任意位置对象，且允许设置诸如 `replace: true`、`name: 'home'` 之类的选项以及任何用在 `router-link` 的 `to` 属性或 `router.push` 中的选项。
    - `next(error)`: (2.4.0+) 如果传入 `next` 的参数是一个 `Error` 实例，则导航会被终止且该错误会被传递给 `router.onError()` 注册过的回调。

确保 `next` 函数在任何给定的导航守卫中都被严格调用一次。它可以出现多于一次，但是只能在所有的逻辑路径都不重叠的情况下，否则钩子永远都不会被解析或报错。示例如下：

```js
// BAD
router.beforeEach((to, from, next) => {
  if (to.name !== 'Login' && !isAuthenticated) next({ name: 'Login' })
  // 如果用户未能验证身份，则 `next` 会被调用两次
  next()
})

// GOOD
router.beforeEach((to, from, next) => {
  if (to.name !== 'Login' && !isAuthenticated) next({ name: 'Login' })
  else next()
})
```

#### 3.1.2. 全局解析守卫(待整理)

#### 3.1.3. 全局后置钩子(待整理)

#### 3.1.4. 路由独享的守卫(待整理)

#### 3.1.5. 组件内的守卫(待整理)

#### 3.1.6. 完整的导航解析流程

1. 导航被触发。
2. 在失活的组件里调用 `beforeRouteLeave` 守卫。
3. 调用全局的 `beforeEach` 守卫。
4. 在重用的组件里调用 `beforeRouteUpdate` 守卫 (2.2+)。
5. 在路由配置里调用 `beforeEnter`。
6. 解析异步路由组件。
7. 在被激活的组件里调用 `beforeRouteEnter`。
8. 调用全局的 `beforeResolve` 守卫 (2.5+)。
9. 导航被确认。
10. 调用全局的 `afterEach` 钩子。
11. 触发 DOM 更新。
12. 调用 `beforeRouteEnter` 守卫中传给 `next` 的回调函数，创建好的组件实例会作为回调函数的参数传入。
