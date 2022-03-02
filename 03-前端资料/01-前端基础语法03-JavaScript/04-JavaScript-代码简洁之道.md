# JavaScript 代码简洁之道

代码质量与其整洁度成正比。干净的代码，既在质量上较为可靠，也为后期维护、升级奠定了良好基础。

本文并不是代码风格指南，而是关于代码的**可读性、复用性、扩展性**探讨。

我们将从几个方面展开讨论：

> 1. 变量
> 2. 函数
> 3. 对象和数据结构
> 4. 类
> 5. SOLID
> 6. 测试
> 7. 异步
> 8. 错误处理
> 9. 代码风格
> 10. 注释

## 1. 变量

### 1.1. 用有意义且常用的单词命名变量

**Bad:**

```js
const yyyymmdstr = moment().format('YYYY/MM/DD');
```

**Good:**

```js
const currentDate = moment().format('YYYY/MM/DD');
```

### 1.2. 保持统一

可能同一个项目对于获取用户信息，会有三个不一样的命名。应该保持统一，如果你不知道该如何取名，可以去 codelf 搜索，看别人是怎么取名的。

**Bad:**

```js
getUserInfo();
getClientData();
getCustomerRecord();
```

**Good:**

```js
getUser()
```

### 1.3. 每个常量都该命名

可以用 buddy.js 或者 ESLint 检测代码中未命名的常量。

**Bad:**

```js
// 三个月之后你还能知道 86400000 是什么吗?
setTimeout(blastOff, 86400000);
```

**Good:**

```js
const MILLISECOND_IN_A_DAY = 86400000;
setTimeout(blastOff, MILLISECOND_IN_A_DAY);
```

### 1.4. 可描述

通过一个变量生成了一个新变量，也需要为这个新变量命名，也就是说每个变量当你看到他第一眼你就知道他是干什么的。

**Bad:**

```js
const ADDRESS = 'One Infinite Loop, Cupertino 95014';
const CITY_ZIP_CODE_REGEX = /^[^,\\]+[,\\\s]+(.+?)\s*(\d{5})?$/;
saveCityZipCode(ADDRESS.match(CITY_ZIP_CODE_REGEX)[1],
                ADDRESS.match(CITY_ZIP_CODE_REGEX)[2]);
```

**Good:**

```js
const ADDRESS = 'One Infinite Loop, Cupertino 95014';
const CITY_ZIP_CODE_REGEX = /^[^,\\]+[,\\\s]+(.+?)\s*(\d{5})?$/;
const [, city, zipCode] = ADDRESS.match(CITY_ZIP_CODE_REGEX) || [];
saveCityZipCode(city, zipCode);
```

### 1.5. 直接了当

**Bad:**

```js
const locations = ['Austin', 'New York', 'San Francisco'];
locations.forEach((l) => {
  doStuff();
  doSomeOtherStuff();
  // ...
  // ...
  // ...
  // 需要看其他代码才能确定 'l' 是干什么的。
  dispatch(l);
});
```

**Good:**

```js
const locations = ['Austin', 'New York', 'San Francisco'];
locations.forEach((location) => {
  doStuff();
  doSomeOtherStuff();
  // ...
  // ...
  // ...
  dispatch(location);
});
```

### 1.6. 避免无意义的前缀

如果创建了一个对象 car，就没有必要把它的颜色命名为 carColor。

**Bad:**

```js
  const car = {
    carMake: 'Honda',
    carModel: 'Accord',
    carColor: 'Blue'
  };

  function paintCar(car) {
    car.carColor = 'Red';
  }
```

**Good:**

```js
const car = {
  make: 'Honda',
  model: 'Accord',
  color: 'Blue'
};

function paintCar(car) {
  car.color = 'Red';
}
```

### 1.7. 使用默认值

**Bad:**

```js
function createMicrobrewery(name) {
  const breweryName = name || 'Hipster Brew Co.';
  // ...
}
```

**Good:**

```js
function createMicrobrewery(name = 'Hipster Brew Co.') {
  // ...
}
```

## 2. 函数

### 2.1. 参数越少越好

如果参数超过两个，使用 ES2015/ES6 的解构语法，不用考虑参数的顺序。

**Bad:**

```js
function createMenu(title, body, buttonText, cancellable) {
  // ...
}
```

**Good:**

```js
function createMenu({ title, body, buttonText, cancellable }) {
  // ...
}

createMenu({
  title: 'Foo',
  body: 'Bar',
  buttonText: 'Baz',
  cancellable: true
});
```

### 2.2. 只做一件事情

这是一条在软件工程领域流传久远的规则。严格遵守这条规则会让你的代码可读性更好，也更容易重构。如果违反这个规则，那么代码会很难被测试或者重用。

**Bad:**

```js
function emailClients(clients) {
  clients.forEach((client) => {
    const clientRecord = database.lookup(client);
    if (clientRecord.isActive()) {
      email(client);
    }
  });
}
```

**Good:**

```js
function emailActiveClients(clients) {
  clients
    .filter(isActiveClient)
    .forEach(email);
}
function isActiveClient(client) {
  const clientRecord = database.lookup(client);    
  return clientRecord.isActive();
}
```

### 2.3. 顾名思义

看函数名就应该知道它是干啥的。

**Bad:**

```js
function addToDate(date, month) {
  // ...
}

const date = new Date();

// 很难知道是把什么加到日期中
addToDate(date, 1);
```

**Good:**

```js
function addMonthToDate(month, date) {
  // ...
}

const date = new Date();
addMonthToDate(1, date);
```

### 2.4. 只需要一层抽象层

如果函数嵌套过多会导致很难复用以及测试。

**Bad:**

```js
function parseBetterJSAlternative(code) {
  const REGEXES = [
    // ...
  ];

  const statements = code.split(' ');
  const tokens = [];
  REGEXES.forEach((REGEX) => {
    statements.forEach((statement) => {
      // ...
    });
  });

  const ast = [];
  tokens.forEach((token) => {
    // lex...
  });

  ast.forEach((node) => {
    // parse...
  });
}
```

**Good:**

```js
function parseBetterJSAlternative(code) {
  const tokens = tokenize(code);
  const ast = lexer(tokens);
  ast.forEach((node) => {
    // parse...
  });
}

function tokenize(code) {
  const REGEXES = [
    // ...
  ];

  const statements = code.split(' ');
  const tokens = [];
  REGEXES.forEach((REGEX) => {
    statements.forEach((statement) => {
      tokens.push( /* ... */ );
    });
  });

  return tokens;
}

function lexer(tokens) {
  const ast = [];
  tokens.forEach((token) => {
    ast.push( /* ... */ );
  });

  return ast;
}
```

### 2.5. 删除重复代码

很多时候虽然是同一个功能，但由于一两个不同点，让你不得不写两个几乎相同的函数。

要想优化重复代码需要有较强的抽象能力，错误的抽象还不如重复代码。所以在抽象过程中必须要遵循 SOLID 原则（SOLID 是什么？稍后会详细介绍）。

**Bad:**

```js
function showDeveloperList(developers) {
  developers.forEach((developer) => {
    const expectedSalary = developer.calculateExpectedSalary();
    const experience = developer.getExperience();
    const githubLink = developer.getGithubLink();
    const data = {
      expectedSalary,
      experience,
      githubLink
    };

    render(data);
  });
}

function showManagerList(managers) {
  managers.forEach((manager) => {
    const expectedSalary = manager.calculateExpectedSalary();
    const experience = manager.getExperience();
    const portfolio = manager.getMBAProjects();
    const data = {
      expectedSalary,
      experience,
      portfolio
    };

    render(data);
  });
}
```

**Good:**

```js
function showEmployeeList(employees) {
  employees.forEach(employee => {
    const expectedSalary = employee.calculateExpectedSalary();
    const experience = employee.getExperience();
    const data = {
      expectedSalary,
      experience,
    };

    switch(employee.type) {
      case 'develop':
        data.githubLink = employee.getGithubLink();
        break
      case 'manager':
        data.portfolio = employee.getMBAProjects();
        break
    }
    render(data);
  })
}
```

### 2.6. 对象设置默认属性

**Bad:**

```js
const menuConfig = {
  title: null,
  body: 'Bar',
  buttonText: null,
  cancellable: true
};

function createMenu(config) {
  config.title = config.title || 'Foo';
  config.body = config.body || 'Bar';
  config.buttonText = config.buttonText || 'Baz';
  config.cancellable = config.cancellable !== undefined ? config.cancellable : true;
}

createMenu(menuConfig);
```

**Good:**

```js
const menuConfig = {
  title: 'Order',
  // 'body' key 缺失
  buttonText: 'Send',
  cancellable: true
};

function createMenu(config) {
  config = Object.assign({
    title: 'Foo',
    body: 'Bar',
    buttonText: 'Baz',
    cancellable: true
  }, config);

  // config 就变成了: {title: "Order", body: "Bar", buttonText: "Send", cancellable: true}
  // ...
}

createMenu(menuConfig);
```

### 2.7. 不要传 flag 参数

通过 flag 的 true 或 false，来判断执行逻辑，违反了一个函数干一件事的原则。

**Bad:**

```js
function createFile(name, temp) {
  if (temp) {
    fs.create(`./temp/${name}`);
  } else {
    fs.create(name);
  }
}
```

**Good:**

```js
function createFile(name) {
  fs.create(name);
}
function createFileTemplate(name) {
  createFile(`./temp/${name}`)
}
```

### 2.8. 避免副作用（第一部分）

函数接收一个值返回一个新值，除此之外的行为我们都称之为副作用，比如修改全局变量、对文件进行 IO 操作等。

当函数确实需要副作用时，比如对文件进行 IO 操作时，请不要用多个函数/类进行文件操作，有且仅用一个函数/类来处理。也就是说副作用需要在唯一的地方处理。

副作用的三大天坑：随意修改可变数据类型、随意分享没有数据结构的状态、没有在统一地方处理副作用。

**Bad:**

```js
// 全局变量被一个函数引用
// 现在这个变量从字符串变成了数组，如果有其他的函数引用，会发生无法预见的错误。
var name = 'Ryan McDermott';

function splitIntoFirstAndLastName() {
  name = name.split(' ');
}

splitIntoFirstAndLastName();

console.log(name); // ['Ryan', 'McDermott'];
```

**Good:**

```js
var name = 'Ryan McDermott';
var newName = splitIntoFirstAndLastName(name)

function splitIntoFirstAndLastName(name) {
  return name.split(' ');
}

console.log(name); // 'Ryan McDermott';
console.log(newName); // ['Ryan', 'McDermott'];
```

### 2.9. 避免副作用（第二部分）

在 JavaScript 中，基本类型通过赋值传递，对象和数组通过引用传递。以引用传递为例：

假如我们写一个购物车，通过 addItemToCart() 方法添加商品到购物车，修改 购物车数组。此时调用 purchase() 方法购买，由于引用传递，获取的 购物车数组 正好是最新的数据。

看起来没问题对不对？

如果当用户点击购买时，网络出现故障， purchase() 方法一直在重复调用，与此同时用户又添加了新的商品，这时网络又恢复了。那么 purchase() 方法获取到 购物车数组 就是错误的。

为了避免这种问题，我们需要在每次新增商品时，克隆 购物车数组 并返回新的数组。

**Bad:**

```js
const addItemToCart = (cart, item) => {
  cart.push({ item, date: Date.now() });
};
```

**Good:**

```js
const addItemToCart = (cart, item) => {
  return [...cart, {item, date: Date.now()}]
};
```

### 2.10. 不要写全局方法

在 JavaScript 中，永远不要污染全局，会在生产环境中产生难以预料的 bug。举个例子，比如你在 Array.prototype 上新增一个 diff 方法来判断两个数组的不同。而你同事也打算做类似的事情，不过他的 diff 方法是用来判断两个数组首位元素的不同。很明显你们方法会产生冲突，遇到这类问题我们可以用 ES2015/ES6 的语法来对 Array 进行扩展。

**Bad:**

```js
Array.prototype.diff = function diff(comparisonArray) {
  const hash = new Set(comparisonArray);
  return this.filter(elem => !hash.has(elem));
};
```

**Good:**

```js
class SuperArray extends Array {
  diff(comparisonArray) {
    const hash = new Set(comparisonArray);
    return this.filter(elem => !hash.has(elem));        
  }
}
```

### 2.11. 比起命令式我更喜欢函数式编程

函数式变编程可以让代码的逻辑更清晰更优雅，方便测试。

**Bad:**

```js
const programmerOutput = [
  {
    name: 'Uncle Bobby',
    linesOfCode: 500
  }, {
    name: 'Suzie Q',
    linesOfCode: 1500
  }, {
    name: 'Jimmy Gosling',
    linesOfCode: 150
  }, {
    name: 'Gracie Hopper',
    linesOfCode: 1000
  }
];

let totalOutput = 0;

for (let i = 0; i < programmerOutput.length; i++) {
  totalOutput += programmerOutput[i].linesOfCode;
}
```

**Good:**

```js
const programmerOutput = [
  {
    name: 'Uncle Bobby',
    linesOfCode: 500
  }, {
    name: 'Suzie Q',
    linesOfCode: 1500
  }, {
    name: 'Jimmy Gosling',
    linesOfCode: 150
  }, {
    name: 'Gracie Hopper',
    linesOfCode: 1000
  }
];
let totalOutput = programmerOutput
  .map(output => output.linesOfCode)
  .reduce((totalLines, lines) => totalLines + lines, 0)
```

### 2.12. 封装条件语句

**Bad:**

```js
if (fsm.state === 'fetching' && isEmpty(listNode)) {
  // ...
}
```

**Good:**

```js
function shouldShowSpinner(fsm, listNode) {
  return fsm.state === 'fetching' && isEmpty(listNode);
}

if (shouldShowSpinner(fsmInstance, listNodeInstance)) {
  // ...
}
```

### 2.13. 尽量别用“非”条件句

**Bad:**

```js
function isDOMNodeNotPresent(node) {
  // ...
}

if (!isDOMNodeNotPresent(node)) {
  // ...
}
```

**Good:**

```js
function isDOMNodePresent(node) {
  // ...
}

if (isDOMNodePresent(node)) {
  // ...
}
```

### 2.14. 避免使用条件语句

Q：不用条件语句写代码是不可能的。

A：绝大多数场景可以用多态替代。

Q：用多态可行，但为什么就不能用条件语句了呢？

A：为了让代码更简洁易读，如果你的函数中出现了条件判断，那么说明你的函数不止干了一件事情，违反了函数单一原则。

**Bad:**

```js
class Airplane {
  // ...

  // 获取巡航高度
  getCruisingAltitude() {
    switch (this.type) {
      case '777':
        return this.getMaxAltitude() - this.getPassengerCount();
      case 'Air Force One':
        return this.getMaxAltitude();
      case 'Cessna':
        return this.getMaxAltitude() - this.getFuelExpenditure();
    }
  }
}
```

**Good:**

```js
class Airplane {
  // ...
}
// 波音777
class Boeing777 extends Airplane {
  // ...
  getCruisingAltitude() {
    return this.getMaxAltitude() - this.getPassengerCount();
  }
}
// 空军一号
class AirForceOne extends Airplane {
  // ...
  getCruisingAltitude() {
    return this.getMaxAltitude();
  }
}
// 赛纳斯飞机
class Cessna extends Airplane {
  // ...
  getCruisingAltitude() {
    return this.getMaxAltitude() - this.getFuelExpenditure();
  }
}
```

### 2.15. 避免类型检查（第一部分）

JavaScript 是无类型的，意味着你可以传任意类型参数，这种自由度很容易让人困扰，不自觉的就会去检查类型。仔细想想是你真的需要检查类型还是你的 API 设计有问题？

**Bad:**

```js
function travelToTexas(vehicle) {
  if (vehicle instanceof Bicycle) {
    vehicle.pedal(this.currentLocation, new Location('texas'));
  } else if (vehicle instanceof Car) {
    vehicle.drive(this.currentLocation, new Location('texas'));
  }
}
```

**Good:**

```js
function travelToTexas(vehicle) {
  vehicle.move(this.currentLocation, new Location('texas'));
}
```

### 2.16. 避免类型检查（第二部分）

如果你需要做静态类型检查，比如字符串、整数等，推荐使用 TypeScript，不然你的代码会变得又臭又长。

**Bad:**

```js
function combine(val1, val2) {
  if (typeof val1 === 'number' && typeof val2 === 'number' ||
      typeof val1 === 'string' && typeof val2 === 'string') {
    return val1 + val2;
  }

  throw new Error('Must be of type String or Number');
}
```

**Good:**

```js
function combine(val1, val2) {
  return val1 + val2;
}
```

### 2.17. 不要过度优化

现代浏览器已经在底层做了很多优化，过去的很多优化方案都是无效的，会浪费你的时间，想知道现代浏览器优化了哪些内容，请点这里。

**Bad:**

```js
// 在老的浏览器中，由于 `list.length` 没有做缓存，每次迭代都会去计算，造成不必要开销。
// 现代浏览器已对此做了优化。
for (let i = 0, len = list.length; i < len; i++) {
  // ...
}
```

**Good:**

```js
for (let i = 0; i < list.length; i++) {
  // ...
}
```

### 2.18. 删除弃用代码

很多时候有些代码已经没有用了，但担心以后会用，舍不得删。

如果你忘了这件事，这些代码就永远存在那里了。

放心删吧，你可以在代码库历史版本中找他它。

**Bad:**

```js
function oldRequestModule(url) {
  // ...
}

function newRequestModule(url) {
  // ...
}

const req = newRequestModule;
inventoryTracker('apples', req, 'www.inventory-awesome.io');
```

**Good:**

```js
function newRequestModule(url) {
  // ...
}

const req = newRequestModule;
inventoryTracker('apples', req, 'www.inventory-awesome.io');
```

## 3. 对象和数据结构

### 3.1. 用 get、set 方法操作数据

这样做可以带来很多好处，比如在操作数据时打日志，方便跟踪错误；在 set 的时候很容易对数据进行校验…

**Bad:**

```js
function makeBankAccount() {
  // ...

  return {
    balance: 0,
    // ...
  };
}

const account = makeBankAccount();
account.balance = 100;
```

**Good:**

```js
function makeBankAccount() {
  // 私有变量
  let balance = 0;

  function getBalance() {
    return balance;
  }

  function setBalance(amount) {
    // ... 在更新 balance 前，对 amount 进行校验
    balance = amount;
  }

  return {
    // ...
    getBalance,
    setBalance,
  };
}

const account = makeBankAccount();
account.setBalance(100);
```

### 3.2. 使用私有变量

可以用闭包来创建私有变量

**Bad:**

```js
const Employee = function(name) {
  this.name = name;
};

Employee.prototype.getName = function getName() {
  return this.name;
};

const employee = new Employee('John Doe');
console.log(`Employee name: ${employee.getName()}`); 
// Employee name: John Doe
delete employee.name;
console.log(`Employee name: ${employee.getName()}`);
 // Employee name: undefined
```

**Good:**

```js
function makeEmployee(name) {
  return {
    getName() {
      return name;
    },
  };
}

const employee = makeEmployee('John Doe');
console.log(`Employee name: ${employee.getName()}`); 
// Employee name: John Doe
delete employee.name;
console.log(`Employee name: ${employee.getName()}`); 
// Employee name: John Doe
```

## 4. 类

### 4.1. 使用 class

在 ES2015/ES6 之前，没有类的语法，只能用构造函数的方式模拟类，可读性非常差。

**Bad:**

```js
// 动物
const Animal = function(age) {
  if (!(this instanceof Animal)) {
    throw new Error('Instantiate Animal with `new`');
  }

  this.age = age;
};

Animal.prototype.move = function move() {};

// 哺乳动物
const Mammal = function(age, furColor) {
  if (!(this instanceof Mammal)) {
    throw new Error('Instantiate Mammal with `new`');
  }

  Animal.call(this, age);
  this.furColor = furColor;
};

Mammal.prototype = Object.create(Animal.prototype);
Mammal.prototype.constructor = Mammal;
Mammal.prototype.liveBirth = function liveBirth() {};

// 人类
const Human = function(age, furColor, languageSpoken) {
  if (!(this instanceof Human)) {
    throw new Error('Instantiate Human with `new`');
  }

  Mammal.call(this, age, furColor);
  this.languageSpoken = languageSpoken;
};

Human.prototype = Object.create(Mammal.prototype);
Human.prototype.constructor = Human;
Human.prototype.speak = function speak() {};
```

**Good:**

```js
// 动物
class Animal {
  constructor(age) {
    this.age = age
  };
  move() {};
}

// 哺乳动物
class Mammal extends Animal{
  constructor(age, furColor) {
    super(age);
    this.furColor = furColor;
  };
  liveBirth() {};
}

// 人类
class Human extends Mammal{
  constructor(age, furColor, languageSpoken) {
    super(age, furColor);
    this.languageSpoken = languageSpoken;
  };
  speak() {};
}
```

### 4.2. 链式调用

这种模式相当有用，可以在很多库中发现它的身影，比如 jQuery、Lodash 等。它让你的代码简洁优雅。实现起来也非常简单，在类的方法最后返回 this 可以了。

**Bad:**

```js
class Car {
  constructor(make, model, color) {
    this.make = make;
    this.model = model;
    this.color = color;
  }

  setMake(make) {
    this.make = make;
  }

  setModel(model) {
    this.model = model;
  }

  setColor(color) {
    this.color = color;
  }

  save() {
    console.log(this.make, this.model, this.color);
  }
}

const car = new Car('Ford','F-150','red');
car.setColor('pink');
car.save();
```

**Good:**

```js
class Car {
  constructor(make, model, color) {
    this.make = make;
    this.model = model;
    this.color = color;
  }

  setMake(make) {
    this.make = make;
    return this;
  }

  setModel(model) {
    this.model = model;
    return this;
  }

  setColor(color) {
    this.color = color;
    return this;
  }

  save() {
    console.log(this.make, this.model, this.color);
    return this;
  }
}

const car = new Car('Ford','F-150','red')
  .setColor('pink');
  .save();
```

### 4.3. 不要滥用继承

很多时候继承被滥用，导致可读性很差，要搞清楚两个类之间的关系，继承表达的一个属于关系，而不是包含关系，比如 Human->Animal vs. User->UserDetails

**Bad:**

```js
class Employee {
  constructor(name, email) {
    this.name = name;
    this.email = email;
  }

  // ...
}

// TaxData（税收信息）并不是属于 Employee（雇员），而是包含关系。
class EmployeeTaxData extends Employee {
  constructor(ssn, salary) {
    super();
    this.ssn = ssn;
    this.salary = salary;
  }

  // ...
}
```

**Good:**

```js
class EmployeeTaxData {
  constructor(ssn, salary) {
    this.ssn = ssn;
    this.salary = salary;
  }

  // ...
}

class Employee {
  constructor(name, email) {
    this.name = name;
    this.email = email;
  }

  setTaxData(ssn, salary) {
    this.taxData = new EmployeeTaxData(ssn, salary);
  }
  // ...
}
```

## 5. SOLID

SOLID 是几个单词首字母组合而来，分别表示 单一功能原则、开闭原则、里氏替换原则、接口隔离原则以及依赖反转原则。

### 5.1. 单一功能原则

如果一个类干的事情太多太杂，会导致后期很难维护。我们应该厘清职责，各司其职减少相互之间依赖。

**Bad:**

```js
class UserSettings {
  constructor(user) {
    this.user = user;
  }

  changeSettings(settings) {
    if (this.verifyCredentials()) {
      // ...
    }
  }

  verifyCredentials() {
    // ...
  }
}
```

**Good:**

```js
class UserAuth {
  constructor(user) {
    this.user = user;
  }
  verifyCredentials() {
    // ...
  }
}

class UserSetting {
  constructor(user) {
    this.user = user;
    this.auth = new UserAuth(this.user);
  }
  changeSettings(settings) {
    if (this.auth.verifyCredentials()) {
      // ...
    }
  }
}
}
```

### 5.2. 开闭原则

“开”指的就是类、模块、函数都应该具有可扩展性，“闭”指的是它们不应该被修改。也就是说你可以新增功能但不能去修改源码。

**Bad:**

```js
class AjaxAdapter extends Adapter {
  constructor() {
    super();
    this.name = 'ajaxAdapter';
  }
}

class NodeAdapter extends Adapter {
  constructor() {
    super();
    this.name = 'nodeAdapter';
  }
}

class HttpRequester {
  constructor(adapter) {
    this.adapter = adapter;
  }

  fetch(url) {
    if (this.adapter.name === 'ajaxAdapter') {
      return makeAjaxCall(url).then((response) => {
        // 传递 response 并 return
      });
    } else if (this.adapter.name === 'httpNodeAdapter') {
      return makeHttpCall(url).then((response) => {
        // 传递 response 并 return
      });
    }
  }
}

function makeAjaxCall(url) {
  // 处理 request 并 return promise
}

function makeHttpCall(url) {
  // 处理 request 并 return promise
}
```

**Good:**

```js
class AjaxAdapter extends Adapter {
  constructor() {
    super();
    this.name = 'ajaxAdapter';
  }

  request(url) {
    // 处理 request 并 return promise
  }
}

class NodeAdapter extends Adapter {
  constructor() {
    super();
    this.name = 'nodeAdapter';
  }

  request(url) {
    // 处理 request 并 return promise
  }
}

class HttpRequester {
  constructor(adapter) {
    this.adapter = adapter;
  }

  fetch(url) {
    return this.adapter.request(url).then((response) => {
      // 传递 response 并 return
    });
  }
}
```

### 5.3. 里氏替换原则

名字很唬人，其实道理很简单，就是子类不要去重写父类的方法。

**Bad:**

```js
// 长方形
class Rectangle {
  constructor() {
    this.width = 0;
    this.height = 0;
  }

  setColor(color) {
    // ...
  }

  render(area) {
    // ...
  }

  setWidth(width) {
    this.width = width;
  }

  setHeight(height) {
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

// 正方形
class Square extends Rectangle {
  setWidth(width) {
    this.width = width;
    this.height = width;
  }

  setHeight(height) {
    this.width = height;
    this.height = height;
  }
}

function renderLargeRectangles(rectangles) {
  rectangles.forEach((rectangle) => {
    rectangle.setWidth(4);
    rectangle.setHeight(5);
    const area = rectangle.getArea(); 
    rectangle.render(area);
  });
}

const rectangles = [new Rectangle(), new Rectangle(), new Square()];
renderLargeRectangles(rectangles);
```

Good:

```js
class Shape {
  setColor(color) {
    // ...
  }

  render(area) {
    // ...
  }
}

class Rectangle extends Shape {
  constructor(width, height) {
    super();
    this.width = width;
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

class Square extends Shape {
  constructor(length) {
    super();
    this.length = length;
  }

  getArea() {
    return this.length * this.length;
  }
}

function renderLargeShapes(shapes) {
  shapes.forEach((shape) => {
    const area = shape.getArea();
    shape.render(area);
  });
}

const shapes = [new Rectangle(4, 5), new Rectangle(4, 5), new Square(5)];
renderLargeShapes(shapes);
```

### 5.4. 接口隔离原则

JavaScript 几乎没有接口的概念，所以这条原则很少被使用。官方定义是“客户端不应该依赖它不需要的接口”，也就是接口最小化，把接口解耦。

**Bad:**

```js
class DOMTraverser {
  constructor(settings) {
    this.settings = settings;
    this.setup();
  }

  setup() {
    this.rootNode = this.settings.rootNode;
    this.animationModule.setup();
  }

  traverse() {
    // ...
  }
}

const $ = new DOMTraverser({
  rootNode: document.getElementsByTagName('body'),
  animationModule() {} // Most of the time, we won't need to animate when traversing.
  // ...
});
```

**Good:**

```js
class DOMTraverser {
  constructor(settings) {
    this.settings = settings;
    this.options = settings.options;
    this.setup();
  }

  setup() {
    this.rootNode = this.settings.rootNode;
    this.setupOptions();
  }

  setupOptions() {
    if (this.options.animationModule) {
      // ...
    }
  }

  traverse() {
    // ...
  }
}

const $ = new DOMTraverser({
  rootNode: document.getElementsByTagName('body'),
  options: {
    animationModule() {}
  }
});
```

### 5.5. 依赖反转原则

说就两点：

高层次模块不能依赖低层次模块，它们依赖于抽象接口。
抽象接口不能依赖具体实现，具体实现依赖抽象接口。
总结下来就两个字，解耦。

**Bad:**

```js
// 库存查询
class InventoryRequester {
  constructor() {
    this.REQ_METHODS = ['HTTP'];
  }

  requestItem(item) {
    // ...
  }
}

// 库存跟踪
class InventoryTracker {
  constructor(items) {
    this.items = items;

    // 这里依赖一个特殊的请求类，其实我们只是需要一个请求方法。
    this.requester = new InventoryRequester();
  }

  requestItems() {
    this.items.forEach((item) => {
      this.requester.requestItem(item);
    });
  }
}

const inventoryTracker = new InventoryTracker(['apples', 'bananas']);
inventoryTracker.requestItems();
```

**Good:**

```js
// 库存跟踪
class InventoryTracker {
  constructor(items, requester) {
    this.items = items;
    this.requester = requester;
  }

  requestItems() {
    this.items.forEach((item) => {
      this.requester.requestItem(item);
    });
  }
}

// HTTP 请求
class InventoryRequesterHTTP {
  constructor() {
    this.REQ_METHODS = ['HTTP'];
  }

  requestItem(item) {
    // ...
  }
}

// webSocket 请求
class InventoryRequesterWS {
  constructor() {
    this.REQ_METHODS = ['WS'];
  }

  requestItem(item) {
    // ...
  }
}

// 通过依赖注入的方式将请求模块解耦，这样我们就可以很轻易的替换成 webSocket 请求。
const inventoryTracker = new InventoryTracker(['apples', 'bananas'], new InventoryRequesterHTTP());
inventoryTracker.requestItems();
```

## 6. 测试

随着项目变得越来越庞大，时间线拉长，有的老代码可能半年都没碰过，如果此时上线，你有信心这部分代码能正常工作吗？测试的覆盖率和你的信心是成正比的。

PS: 如果你发现你的代码很难被测试，那么你应该优化你的代码了。

### 6.1. 单一化

**Bad:**

```js
import assert from 'assert';

describe('MakeMomentJSGreatAgain', () => {
  it('handles date boundaries', () => {
    let date;

    date = new MakeMomentJSGreatAgain('1/1/2015');
    date.addDays(30);
    assert.equal('1/31/2015', date);

    date = new MakeMomentJSGreatAgain('2/1/2016');
    date.addDays(28);
    assert.equal('02/29/2016', date);

    date = new MakeMomentJSGreatAgain('2/1/2015');
    date.addDays(28);
    assert.equal('03/01/2015', date);
  });
});
```

**Good:**

```js
import assert from 'assert';

describe('MakeMomentJSGreatAgain', () => {
  it('handles 30-day months', () => {
    const date = new MakeMomentJSGreatAgain('1/1/2015');
    date.addDays(30);
    assert.equal('1/31/2015', date);
  });

  it('handles leap year', () => {
    const date = new MakeMomentJSGreatAgain('2/1/2016');
    date.addDays(28);
    assert.equal('02/29/2016', date);
  });

  it('handles non-leap year', () => {
    const date = new MakeMomentJSGreatAgain('2/1/2015');
    date.addDays(28);
    assert.equal('03/01/2015', date);
  });
});
```

## 7. 异步

### 7.1. 不再使用回调

不会有人愿意去看嵌套回调的代码，用 Promises 替代回调吧。

**Bad:**

```js
import { get } from 'request';
import { writeFile } from 'fs';

get('https://en.wikipedia.org/wiki/Robert_Cecil_Martin', (requestErr, response) => {
  if (requestErr) {
    console.error(requestErr);
  } else {
    writeFile('article.html', response.body, (writeErr) => {
      if (writeErr) {
        console.error(writeErr);
      } else {
        console.log('File written');
      }
    });
  }
});
```

**Good:**

```js
get('https://en.wikipedia.org/wiki/Robert_Cecil_Martin')
  .then((response) => {
    return writeFile('article.html', response);
  })
  .then(() => {
    console.log('File written');
  })
  .catch((err) => {
    console.error(err);
  });
```

### 7.2. Async/Await 比起 Promises 更简洁

**Bad:**

```js
import { get } from 'request-promise';
import { writeFile } from 'fs-promise';

get('https://en.wikipedia.org/wiki/Robert_Cecil_Martin')
  .then((response) => {
    return writeFile('article.html', response);
  })
  .then(() => {
    console.log('File written');
  })
  .catch((err) => {
    console.error(err);
  });
```

**Good:**

```js
import { get } from 'request-promise';
import { writeFile } from 'fs-promise';

async function getCleanCodeArticle() {
  try {
    const response = await get('https://en.wikipedia.org/wiki/Robert_Cecil_Martin');
    await writeFile('article.html', response);
    console.log('File written');
  } catch(err) {
    console.error(err);
  }
}
```

## 8. 错误处理

### 8.1. 不要忽略抛异常

**Bad:**

```js
try {
  functionThatMightThrow();
} catch (error) {
  console.log(error);
}
```

**Good:**

```js
try {
  functionThatMightThrow();
} catch (error) {
  // 这一种选择，比起 console.log 更直观
  console.error(error);
  // 也可以在界面上提醒用户
  notifyUserOfError(error);
  // 也可以把异常传回服务器
  reportErrorToService(error);
  // 其他的自定义方法
}
```

### 8.2. 不要忘了在 Promises 抛异常

**Bad:**

```js
getdata()
  .then((data) => {
    functionThatMightThrow(data);
  })
  .catch((error) => {
    console.log(error);
  });
```

**Good:**

```js
getdata()
  .then((data) => {
    functionThatMightThrow(data);
  })
  .catch((error) => {
    // 这一种选择，比起 console.log 更直观
    console.error(error);
    // 也可以在界面上提醒用户
    notifyUserOfError(error);
    // 也可以把异常传回服务器
    reportErrorToService(error);
    // 其他的自定义方法
  });
```

## 9. 代码风格

代码风格是主观的，争论哪种好哪种不好是在浪费生命。市面上有很多自动处理代码风格的工具，选一个喜欢就行了，我们来讨论几个非自动处理的部分。

### 9.1. 常量大写

**Bad:**

```js
const DAYS_IN_WEEK = 7;
const daysInMonth = 30;

const songs = ['Back In Black', 'Stairway to Heaven', 'Hey Jude'];
const Artists = ['ACDC', 'Led Zeppelin', 'The Beatles'];

function eraseDatabase() {}
function restore_database() {}

class animal {}
class Alpaca {}
```

**Good:**

```js
const DAYS_IN_WEEK = 7;
const DAYS_IN_MONTH = 30;

const SONGS = ['Back In Black', 'Stairway to Heaven', 'Hey Jude'];
const ARTISTS = ['ACDC', 'Led Zeppelin', 'The Beatles'];

function eraseDatabase() {}
function restoreDatabase() {}

class Animal {}
class Alpaca {}
```

### 9.2. 先声明后调用

就像我们看报纸文章一样，从上到下看，所以为了方便阅读把函数声明写在函数调用前面。

**Bad:**

```js
class PerformanceReview {
  constructor(employee) {
    this.employee = employee;
  }

  lookupPeers() {
    return db.lookup(this.employee, 'peers');
  }

  lookupManager() {
    return db.lookup(this.employee, 'manager');
  }

  getPeerReviews() {
    const peers = this.lookupPeers();
    // ...
  }

  perfReview() {
    this.getPeerReviews();
    this.getManagerReview();
    this.getSelfReview();
  }

  getManagerReview() {
    const manager = this.lookupManager();
  }

  getSelfReview() {
    // ...
  }
}

const review = new PerformanceReview(employee);
review.perfReview();
```

**Good:**

```js
class PerformanceReview {
  constructor(employee) {
    this.employee = employee;
  }

  perfReview() {
    this.getPeerReviews();
    this.getManagerReview();
    this.getSelfReview();
  }

  getPeerReviews() {
    const peers = this.lookupPeers();
    // ...
  }

  lookupPeers() {
    return db.lookup(this.employee, 'peers');
  }

  getManagerReview() {
    const manager = this.lookupManager();
  }

  lookupManager() {
    return db.lookup(this.employee, 'manager');
  }

  getSelfReview() {
    // ...
  }
}

const review = new PerformanceReview(employee);
review.perfReview();
```

## 10. 注释

### 10.1. 只有业务逻辑需要注释

代码注释不是越多越好。

**Bad:**

```js
function hashIt(data) {
  // 这是初始值
  let hash = 0;

  // 数组的长度
  const length = data.length;

  // 循环数组
  for (let i = 0; i < length; i++) {
    // 获取字符代码
    const char = data.charCodeAt(i);
    // 修改 hash
    hash = ((hash << 5) - hash) + char;
    // 转换为32位整数
    hash &= hash;
  }
}
```

Good:

```js
function hashIt(data) {
  let hash = 0;
  const length = data.length;

  for (let i = 0; i < length; i++) {
    const char = data.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;

    // 转换为32位整数
    hash &= hash;
  }
}
```

### 10.2. 删掉注释的代码

git 存在的意义就是保存你的旧代码，所以注释的代码赶紧删掉吧。

**Bad:**

```js
doStuff();
// doOtherStuff();
// doSomeMoreStuff();
// doSoMuchStuff();
```

**Good:**

```js
doStuff();
```

javascript

不要记日记
记住你有 git！，git log 可以帮你干这事。

**Bad:**

```js
/**
 * 2016-12-20: 删除了 xxx
 * 2016-10-01: 改进了 xxx
 * 2016-02-03: 删除了第12行的类型检查
 * 2015-03-14: 增加了一个合并的方法
 */
function combine(a, b) {
  return a + b;
}
```

**Good:**

```js
function combine(a, b) {
  return a + b;
}
```

### 10.3. 注释不需要高亮

注释高亮，并不能起到提示的作用，反而会干扰你阅读代码。

**Bad:**

```js
////////////////////////////////////////////////////////////////////////////////
// Scope Model Instantiation
////////////////////////////////////////////////////////////////////////////////
$scope.model = {
  menu: 'foo',
  nav: 'bar'
};

////////////////////////////////////////////////////////////////////////////////
// Action setup
////////////////////////////////////////////////////////////////////////////////
const actions = function() {
  // ...
};
```

**Good:**

```js
$scope.model = {
  menu: 'foo',
  nav: 'bar'
};

const actions = function() {
  // ...
};
```
