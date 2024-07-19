## 1. JavaScript 正则表达式规则

|   符号   |                         作用                          |
| :-----: | ---------------------------------------------------- |
|  `\d`   | 数字(digital)                                         |
|  `\D`   | 非数字                                                |
|  `\w`   | 单词(word)：字母大写和小写，数字，下划线                   |
|  `\W`   | 非单词                                                |
|  `\s`   | 空白字符(看不见的字符)：空格、制表符(tab)、分页符、回车、换行 |
|  `\S`   | 非空白字符                                             |
|   `.`   | 通配符，任意字符，所以如果正则表达式中出现了"."要转义"\."    |
|  `{n}`  | 匹配n次个数 x = n次                                    |
| `{n,}`  | 最少n次，x>=n                                          |
| `{n,m}` | 在n和m之间，n<=x<=m                                    |
|   `+`   | 1~n次                                                 |
|   `*`   | 0~n次                                                 |
|   `?`   | 0~1次                                                 |
|   `^`   | 匹配开头                                               |
|   `$`   | 匹配结尾                                               |

## 2. 创建正则表达式的语法

在JS中正则表达式是一个对象：`RegExp` （Regular Expression 正则表达式）

### 2.1. 方式1

- 语法：`var reg = new RegExp("正则表达式","匹配模式");`
- 作用：直接创建一个正则表达式对象

```js
var reg = new RegExp("\\d{3}");
```

### 2.2. 方式2

- 语法：`var reg = /正则表达式/匹配模式;`

```js
var reg = /\d{3}/;
```

### 2.3. 方式1和方式2的区别

- 方式1中间是字符串，字符串`\`需要转义。
- 方式2不是字符串，所以`\`不用转义，**建议使用方式2。(Java中没有这种写法)**

## 3. 正则表达式匹配
### 3.1. test() 匹配正则

- 语法：`正则表达式对象.test("要匹配的字符串");`
- 方法返回值：boolean类型的值
- 作用：如果正则表达式匹配当前字符串返回true，否则返回false

```js
// 例：test匹配函数
let testString = "My test string";
let testRegex = /string/;
var result = testRegex.test(testString); // result为boolean类型
```

### 3.2. 匹配多个模式

`|`：代表匹配多个不同的字符串

```js
let testString = "no!"
const regex = /yes|no|maybe/;
var result = regex.test(testString); // true
```

### 3.3. 忽略大小写匹配

- `i`：代表忽略大小写比较

```js
/* 案例1 */
// 写法1
var reg = new RegExp("cat", "i");
// 写法2
var reg = /cat/i;
var b = reg.test("CAT");   // b为true;

/* 案例2 */
const caseInsensitiveRegex = /ignore case/i;
const testString = 'We use the i flag to iGnOrE CasE';
caseInsensitiveRegex.test(testString); // true
```

### 3.4. match() 提取变量的第一个匹配项

- 语法：`需要匹配的字符串.match(正则表达式)`
- 作用：返回字符串中第一个匹配正则的内容
- 返回值：String

```js
const match = "Hello World!".match(/hello/i); // 返回字符串："Hello"
```

### 3.5. 提取数组中的所有匹配项

标志符`g`：将字符串中匹配正则表达式的内容，以数组类型返回

```js
const testString = "Repeat repeat rePeAT";
const regexWithAllMatches = /Repeat/gi;
testString.match(regexWithAllMatches); // ["Repeat", "repeat", "rePeAT"]
```

### 3.6. 匹配任意字符

- 使用通配符`.`作为任何字符的占位符

```js
/ To match "cat", "BAT", "fAT", "mat"
const regexWithWildcard = /.at/gi;
const testString = "cat BAT cupcake fAT mat dog";
const allMatchingWords = testString.match(regexWithWildcard); // ["cat", "BAT", "fAT", "mat"] 用多种可能性匹配单个字符
```

### 3.7. 匹配一组字符

使用字符类，可以使用它来定义要匹配的一组字符，把它们放在方括号里`[]`

```js
// 匹配 "cat" "fat" and "mat" 但不匹配 "bat"
const regexWithCharClass = /[cfm]at/g;
const testString = "cat fat bat mat";
const allMatchingWords = testString.match(regexWithCharClass); // ["cat", "fat", "mat"]
```

### 3.8. 匹配字母表中的字母

使用字符集内的范围 `[a-z]`

```js
const regexWidthCharRange = /[a-e]at/;
const regexWithCharRange = /[a-e]at/;
const catString = "cat";
const batString = "bat";
const fatString = "fat";

regexWithCharRange.test(catString); // true
regexWithCharRange.test(batString); // true
regexWithCharRange.test(fatString); // false
```

### 3.9. 匹配特定的数字和字母

使用连字符来匹配数字

```js
const regexWithLetterAndNumberRange = /[a-z0-9]/ig;
const testString = "Emma19382";
testString.match(regexWithLetterAndNumberRange) // true
```

### 3.10. 匹配单个未知字符

要匹配不想拥有的一组字符，使用否定字符集 `^`

```js
const allCharsNotVowels = /[^aeiou]/gi;
const allCharsNotVowelsOrNumbers = /[^aeiou0-9]/gi;
```

### 3.11. 匹配一行中出现一次或多次的字符

使用 `+` 标志

```js
const oneOrMoreAsRegex = /a+/gi;
const oneOrMoreSsRegex = /s+/gi;
const cityInFlorida = "Tallahassee";

cityInFlorida.match(oneOrMoreAsRegex); // ['a', 'a', 'a'];
cityInFlorida.match(oneOrMoreSsRegex); // ['ss'];
```

### 3.12. 匹配连续出现零次或多次的字符

使用星号 `*`

```js
const zeroOrMoreOsRegex = /hi*/gi;
const normalHi = "hi";
const happyHi = "hiiiiii";
const twoHis = "hiihii";
const bye = "bye";

normalHi.match(zeroOrMoreOsRegex); // ["hi"]
happyHi.match(zeroOrMoreOsRegex); // ["hiiiiii"]
twoHis.match(zeroOrMoreOsRegex); // ["hii", "hii"]
bye.match(zeroOrMoreOsRegex); // null
```

### 3.13. 惰性匹配

- 字符串中与给定要求匹配的最小部分
- 默认情况下，正则表达式是贪婪的（匹配满足给定要求的字符串的最长部分）
- 使用 `?` 阻止贪婪模式(惰性匹配)

```js
const testString = "catastrophe";
const greedyRexex = /c[a-z]*t/gi;
const lazyRegex = /c[a-z]*?t/gi;

testString.match(greedyRexex); // ["catast"]
testString.match(lazyRegex); // ["cat"]
```

### 3.14. 匹配起始字符串模式

要测试字符串开头的字符匹配，请使用插入符号`^`，但要放大开头，不要放到字符集中

```js
const emmaAtFrontOfString = "Emma likes cats a lot.";
const emmaNotAtFrontOfString = "The cats Emma likes are fluffy.";
const startingStringRegex = /^Emma/;

startingStringRegex.test(emmaAtFrontOfString); // true
startingStringRegex.test(emmaNotAtFrontOfString); // false
```

### 3.15. 匹配结束字符串模式

使用 `$` 来判断字符串是否是以规定的字符结尾

```js
const emmaAtBackOfString = "The cats do not like Emma";
const emmaNotAtBackOfString = "Emma loves the cats";
const startingStringRegex = /Emma$/;

startingStringRegex.test(emmaAtBackOfString); // true
startingStringRegex.test(emmaNotAtBackOfString); // false
```

### 3.16. 匹配所有字母和数字

使用`\word`简写

```js
const longHand = /[A-Za-z0-9_]+/;
const shortHand = /\w+/;
const numbers = "42";
const myFavoriteColor = "magenta";

longHand.test(numbers); // true
shortHand.test(numbers); // true
longHand.test(myFavoriteColor); // true
shortHand.test(myFavoriteColor); // true
```

### 3.17. 除了字母和数字，其他的都要匹配

用`\W` 表示 `\w` 的反义

```js
const noAlphaNumericCharRegex = /\W/gi;
const weirdCharacters = "!_$!!";
const alphaNumericCharacters = "ab283AD";

noAlphaNumericCharRegex.test(weirdCharacters); // true
noAlphaNumericCharRegex.test(alphaNumericCharacters); // false
```

### 3.18. 匹配所有数字

可以使用字符集`[0-9]`，或者使用简写`\d`

```js
const digitsRegex = /\d/g;
const stringWithDigits = "My cat eats $20.00 worth of food a week.";

stringWithDigits.match(digitsRegex); // ["2", "0", "0", "0"]
```

### 3.19. 匹配所有非数字

用`\D` 表示 `\d` 的反义

```js
const nonDigitsRegex = /\D/g;
const stringWithLetters = "101 degrees";

stringWithLetters.match(nonDigitsRegex); // [" ", "d", "e", "g", "r", "e", "e", "s"]
```

### 3.20. 匹配空格

使用 `\s` 来匹配空格和回车符

```js
const sentenceWithWhitespace = "I like cats!";
var spaceRegex = /\s/g;

sentenceWithWhitespace.match(spaceRegex); // [" ", " "]
```

### 3.21. 匹配非空格

用`\S` 表示 `\s` 的反义

```js
const sentenceWithWhitespace = "C a t"
const nonWhiteSpaceRegex = /\S/g;

sentenceWithWhitespace.match(nonWhiteSpaceRegex); // ["C", "a", "t"]
```

### 3.22. 匹配的字符数

可以使用 `{下界，上界}` 指定一行中的特定字符数

```js
const regularHi = "hi";
const mediocreHi = "hiii";
const superExcitedHey = "heeeeyyyyy!!!";
const excitedRegex = /hi{1,4}/;

excitedRegex.test(regularHi); // true
excitedRegex.test(mediocreHi); // true
excitedRegex.test(superExcitedHey); //false
```

### 3.23. 匹配最低个数的字符数

使用`{下界, }`定义最少数量的字符要求，下面示例表示字母 i 至少要出现2次

```js
const regularHi = "hi";
const mediocreHi = "hiii";
const superExcitedHey = "heeeeyyyyy!!!";
const excitedRegex = /hi{2,}/;

excitedRegex.test(regularHi); // false
excitedRegex.test(mediocreHi); // true
excitedRegex.test(superExcitedHey); //false
```

### 3.24. 匹配精确的字符数

使用`{requiredCount}`指定字符要求的确切数量

```js
const regularHi = "hi";
const bestHi = "hii";
const mediocreHi = "hiii";
const excitedRegex = /hi{2}/;

excitedRegex.test(regularHi); // false
excitedRegex.test(bestHi); // true
excitedRegex.test(mediocreHi); //false
```

### 3.25. 匹配0次或1次

使用 `?` 匹配字符 0 次或1次

```js
const britishSpelling = "colour";
const americanSpelling = "Color";
const languageRegex = /colou?r/i;

languageRegex.test(britishSpelling); // true
languageRegex.test(americanSpelling); // true
```

## 4. 匹配上与 Java 中的不同

**Java在默认的情况下是精确匹配，在JS中默认是模糊匹配**，只要**包含**正则表达式中的字符串就返回true

|  正则表达式   | 匹配字符串 | Java中匹配结果 | JavaScript中匹配结果 |       说明        |
| :---------: | :-------: | :-----------: | :-----------------: | ---------------- |
|  `/\d{3}/`  |   a123b   |     false     |        true         | 只要包含3个数字即可 |
| `/^\d{3}/`  |   123b    |     false     |        true         | 以3个数字开头      |
| `/\d{3}$/`  |   a123    |     false     |        true         | 以3个数字结尾      |
| `/^\d{3}$/` |    123    |     true      |        true         | 只能匹配3个数字     |

***注：如果想完全匹配就`^$`包括起来***

## 5. 正则表达式案例

### 5.1. 一个正则表达式案例(练习)

![](images/20190515103801802_5650.jpg)

- 需求：用户注册，需要进行如下验证，请在JS中使用正则表达式进行验证。
    1. 用户名：只能由英文字母和数字组成，长度为4～16个字符，并且以英文字母开头
    2. 密码：大小写字母和数字6-20个字符
    3. 确认密码：两次密码要相同
    4. 电子邮箱：符合邮箱地址的格式`/^\w+@\w+(\.[a-zA-Z]{2,3}){1,2}$/`
    5. 手机号：`/^1[34578]\d{9}$/`
    6. 生日：生日的年份在1900～2009之间，生日格式为1980-5-12或1988-05-04的形式，`/^((19\d{2})|(200\d))-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1])$/`
- Code Demo:正则表达式验证用户输入信息案例部分，全部常见Day32LessonDemo

```js
// 验证所有注册项是否正确，再提交
function checkAll(){
    return checkUser() && checkPassword() && doubleCheckPwd() && checkEmail() && checkMobile() && checkBirth();
}

// 验证用户名是否正确
function checkUser(){
    // 创建验证的正则表达式
    // 只能由英文字母和数字组成，长度为4～16个字符，并且以英文字母开头
    var reg = /^[a-zA-Z][a-zA-Z0-9]{3,15}$/;

    // 获取文本框的值
    var user = document.getElementById("user").value;
    // 比较文本内容是否正确
    if(reg.test(user)){
        // 正确则显示勾，并返回true
        document.getElementById("userInfo").innerHTML = "<img src='../img/注册验证/img/gou.png' width='15'/>";
        return true;
    }else{
        // 错误则显示错误提示，并返回false
        document.getElementById("userInfo").innerHTML = "用户名格式不正确";
        return false;
    }
}
```

### 5.2. 取值范围为0-999.99的JS正则表达式

```js
var reg = /^([1-9]\d{2})|([1-9]\d{1})|\d(\.\d{1,2})?$/;
```

### 5.3. 手机号码

```js
$(document).ready(function(){
  //判断输入手机号码是否正确
  $("#telephone").focus(function(){
     document.getElementById("ph-hint").innerHTML = ":open_mouth:请输入11位手机号码";

  })
  $("#telephone").blur(function(){
     if(/^((13[0-9])|(15[^4,\D])|(18[0-9]))\d{8}$/.test(this.value)){
        document.getElementById("ph-hint").innerHTML = "";
     }else if(document.getElementById("telephone").value == ""){
         document.getElementById("ph-hint").innerHTML = " 咦，手机号不能为空哦";
     }else{
        document.getElementById("ph-hint").innerHTML = " 这好像不是一个手机号码哦";
        document.getElementById("telephone").value = "";
     }
  })
})
```

详解：当获取焦点时，提示文字请输入手机号，然后失去焦点时判断，那段正则表达式表示的是13几，这个几0-9都可以，或者15几，但是非4的都行，或者18几，0-9都可以，然后再加8位数字，符合条件则成功，否则根据情况判断提示文字；

### 5.4. 密码

```js
$(document).ready(function(){
  //判断输入密码格式是否正确
  $("#setpassword").focus(function(){
     document.getElementById("pw-hint").innerHTML = ":open_mouth:请您输入密码，6-15个字母数字和符号两种以上组合";

  })
  $("#setpassword").blur(function(){
     if(/^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?![,\.#%'\+\*\-:;^_`]+$)[,\.#%'\+\*\-:;^_`0-9A-Za-z]{6,15}$/.test(this.value)){
        document.getElementById("pw-hint").innerHTML = "";
     }else if(document.getElementById("setpassword").value == ""){
         document.getElementById("pw-hint").innerHTML = " 咦，密码不能为空哦";
     }else{
        document.getElementById("pw-hint").innerHTML = " 你的密码格式有误,请重新输入";
        document.getElementById("setpassword").value = "";
     }
  })
})
```

详解：当获取焦点时，提示文字请输入密码，然后失去焦点时判断，那段正则表达式表示的是可以输入0-9的数字和大小写的字母a-z,外加一些特殊符号，然后可以输入6-15位密码，符合条件则成功，否则根据情况判断提示文字；

以下格式我就不这样写了，简介一点，上面两段可以去参考。

### 5.5. 用户名

```js
//用户名正则，4到16位（字母，数字，下划线，减号）
var username = /^[a-zA-Z0-9_-]{4,16}$/;
//文字
var username=/[\d]/g；
```

### 5.6. 电子邮箱

```js
//对电子邮件的验证
var email = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
```

### 5.7. 身份证号

```js
//身份证号（18位）正则
var cP = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
```

### 5.8. 日期

```js
//日期正则，简单判定,未做月份及日期的判定
var time = /^\d{4}(\-)\d{1,2}\1\d{1,2}$/;
//日期正则，复杂判定
var time = /^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$/;
```

### 5.9. QQ号以及微信号

```js
//QQ号正则，5至11位
var qq = /^[1-9][0-9]{4,10}$/;
//微信号正则，6至20位，以字母开头，字母，数字，减号，下划线
var wx = /^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$/;
```

### 5.10. 座机号

```js
//座机号
var tel =/(^0\d{2}-8\d{7}$)|(^0\d{3}-3\d{6}$)/;
```

附加： 发送验证码倒计时写法

```js
<input class="login-code-send" id="login-code-send" type="button" name="sendcode" value="发送验证码" onclick="settime(this);"/>
//登录，忘记密码的验证码
var counts = 60;
function settime(val) {
  if (counts == 0) {
    val.removeAttribute("disabled");
    val.value = "获取验证码";
    counts = 60;
    return false;
  } else {
    val.setAttribute("disabled", true);
    val.value = "重新发送("+counts+")";
    counts--;
  }
  setTimeout(function () {
    settime(val);
  }, 1000);
}
```

## 6. !待整理资料

- [前端进阶必须知道的正则表达式知识](https://mp.weixin.qq.com/s?__biz=MzAxODE2MjM1MA==&mid=2651556814&idx=1&sn=164cf3384791b2aed49a9335ec37b76b&chksm=80255c0fb752d519ac23fecf41a92a59fbfa5bd44077f2f7cc73ebd1364db36cd400a085282a&mpshare=1&scene=1&srcid=&sharer_sharetime=1564238608466&sharer_shareid=6087581adbbb79acccd7e873962f1a09#rd)

