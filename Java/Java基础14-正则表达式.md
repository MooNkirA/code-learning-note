# Java 正则表达式

## 1. 正则表达式概述和作用

**正则表达式是专门解决字符串规则匹配的工具。正则表达式也是一个字符串，用来定义匹配规则。参照帮助文档，在 Pattern 类中有简单的规则定义，可以结合字符串类的方法使用。**

### 1.1. String类中与正则表达式相关的方法

```java
public boolean matches(String regex);
    // 使用String类来进行正则表达式规则判断。
    // 概述：一个用来定义规则的字符串；
    // 作用：用来校验某个字符串是否符合指定的规则。

public String[] split(String regex);

public String replaceAll(String regex, String replacement);
    // 这两个方法传入的字符也适用正则表达式。如涉及到“通配符”，“转义字符”等，(. \d,\t …)需要在前面加上“\”。即(\\. , \\d, \\t …)
```

**注：上述相关方法，参数regex是正则表达式的字符串，方法则按正则表达式的规则去执行**

### 1.2. 正则对象 Pattern 和匹配器 Matcher

- **范例**：

```java
Pattern p = Pattern.compile("a*b");
Matcher m = p.matcher("aaaaab");
boolean b = m.matches();
```

- **使用步骤**：
    1. 先将正则表达式编译成正则对象。使用的是Pattern类一个静态的方法。`compile(regex);`
    2. 让正则对象和要操作的字符串相关联，通过matcher方法完成，并返回匹配器对象。
    3. 通过匹配器对象的方法将正则模式作用到字符串上对字符串进行针对性的功能操作

### 1.3. 总结

1. 正则判断可以直接使用String字符串的matches方法，或者使用正则对象
2. java的正则表达式都是字符串，而javaScript可以直接 `var reg = /正则表达式/匹配模式;` 创建一个正则表达式对象

## 2. 正则表达式语法规则

### 2.1. 语法

|        **构造**        | **匹配**                                                     |
| :--------------------: | :----------------------------------------------------------- |
|          字符          | 要完全匹配                                                   |
|          `x`           | 匹配字符x                                                    |
|          `\`           | 转义字符                                                     |
|          `\t`          | tab键，匹配制表符                                            |
|          `\n`          | 换行，匹配换行                                               |
|          `\r`          | 回车，匹配回车                                               |
|        **构造**        | **匹配**                                                     |
|    **字符类[记忆]**    | 中括号中可以放n个字符，但只能匹配一类字符中的**其中一个**    |
|        `[abc]`         | 匹配abc中任意一个字符                                        |
|        `[^abc]`        | **（取反）**匹配除abc之外的任意一个字符                      |
|       `[a-zA-Z]`       | 匹配所有的字母中的任意一个**（必须是从小到大）**             |
|        `[0-9]`         | 匹配所有数字字符的任意一个                                   |
|     `[a-zA-Z_0-9]`     | 匹配所有的数字和字母和下划线的任意一个                       |
|        **构造**        | **匹配**                                                     |
| **预定义字符类[记忆]** |                                                              |
|          `.`           | 是通配符，除了`\r`和`\n`以外的所有的任意一个字符             |
|         `\\d`          | 匹配所有数字字符的任意一个，相当于`[0-9]`；**(`\\D`就是取反)** |
|         `\\w`          | 匹配所有的数字和字母和下划线的任意一个,相当于`[a-zA-Z_0-9]`；**(`\\w`就是取反)** |
|         `\\s`          | 匹配空格 `[ \t\n\x0B\f\r]`                                   |
|        **构造**        | **匹配**                                                     |
|    **数量词[记忆]**    |                                                              |
|          `X?`          | x出现0或1次，`?`修饰最近的字符                               |
|          `X*`          | x出现0或n次， x可以出现任意次                                |
|          `X+`          | x出现1或n次， x可以出现1次以上                               |
|         `X{n}`         | x刚好出现n次                                                 |
|        `X{n,}`         | x至少出现n次，包含n次                                        |
|        `X{n,m}`        | x出现n至m次，包含n和m次                                      |
|        **构造**        | **匹配**                                                     |
|         `(X)`          | X，作为捕获组                                                |

### 2.2. 常用正则例子

- 汉字：`[\u4e00-\u9fa5]`
- 手机：`0?(13|14|15|17|18)[0-9]{9}`
- 邮箱：`\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}`
- IP：`^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$`
- 网址：`^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$`

## 3. 正则表达式案例

### 3.1. 匹配邮箱(Java 与 js)

- js代码

```js
var pattern = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/g,
str = '';
console.log(pattern.test(str));
```

- java代码

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {
  public static void main(String args[]) {
    String str = "";
    String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(str);
    System.out.println(m.matches());
  }
}
```

### 3.2. 替换字符串中的回车、换行符、空格(Java 与 js)

- js代码

```js
var resultStr = testStr.replace(/\ +/g, ""); //去掉空格
resultStr = testStr.replace(/[ ]/g, "");    //去掉空格
resultStr = testStr.replace(/[\r\n]/g, ""); //去掉回车换行
resultStr = testStr.replace(/[\n]/g, ""); //去掉换行
resultStr = testStr.replace(/[\r]/g, ""); //去掉回车

/************************************************/
// 原始字符串
var string = "欢迎访问!\r\nhangge.com    做最好的开发者知识平台";

// 去掉所有的换行符(注：需要同时执行两个语句)
string = string.replace(/\r\n/g, "")
string = string.replace(/\n/g, "");

// 去掉所有的空格（中文空格、英文空格都会被替换）
string = string.replace(/\s/g, "");
```

- java代码

```java
// String类的replaceAll就有正则替换功能。 \t为制表符 \n为换行 \r为回车
String str = "sds  ss";
str = str.replaceAll("[\\t\\n\\r]", "");  // 不替换空格

/************************************************/
public class StringUtil {
  public static String getStringNoBlank(String str) {
    if(str!=null && !"".equals(str)) {
      Pattern p = Pattern.compile("\\s*|\t|\r|\n");
      Matcher m = p.matcher(str);
      String strNoBlank = m.replaceAll("");
      return strNoBlank;
    } else {
      return str;
    }
  }
}
```
