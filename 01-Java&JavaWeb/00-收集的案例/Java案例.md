# Java案例
## 1. 将小数类型转成金额形式的字符串

使用NumberFormat类进行数值格式，再对截取字符串

```java
package com.moon.testproj;

import java.text.NumberFormat;
import java.util.Locale;

public class Test {
  public static void main(String[] args) {
    double num = 1102834.83343434;
    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
    // 输出：Locale.CHINA：￥1,102,834.83
    System.out.println("Locale.CHINA："+format.format(num));
    StringBuilder sb = new StringBuilder(format.format(num));
    sb.deleteCharAt(0);
    // 输出：1102834.83343434
    System.out.println(num);
    // 输出：1,102,834.83
    System.out.println(sb.toString());
  }
}
```

## 2. 获取项目的所在的操作系统

```java
String os = System.getProperties().getProperty("os.name");
if (os.startsWith("win") || os.startsWith("Win")) {
  prefixFont = "C:\\Windows\\Fonts" + File.separator;
} else {
  prefixFont = "/usr/share/fonts/chinese" + File.separator;
}
```