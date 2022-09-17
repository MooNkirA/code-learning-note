# Ant Design Mobile

- 官网：https://mobile.ant.design/index-cn

## 1. 实践项目相关问题
### 1.1. Tabs 组件设置标签的宽度

需求：在使用 Ant Design Mobile 框架的 Tabs 组件时，标签的宽度默认是占满整个Tabs组件，现在需要自定义标签的宽度，不会出现只有一个标签的时候，整个标签都占满全部空间

伪代码实现

```jsx
import { Tabs, WhiteSpace } from "antd-mobile"

function renderTabBar(props) {
  // 此处可以根据需求，进行业务逻辑动态设置宽度
  const HEIGHT_MAP = {
    1: "12.5%",
    2: "25%",
    3: "37.5%",
    4: "50%",
    5: "62.5%",
    6: "75%",
    7: "87.5%",
    8: "100%"
  }

  let height = "100%"
  const length = tabs.length
  if (length > 0 && length < 8) {
    height = HEIGHT_MAP[length]
  }

  return (
    <div style={{ height }}>
      <Tabs.DefaultTabBar {...props} page={8} />
    </div>
  )
}
const tabs = [
  { title: "First Tab" },
  { title: "Second Tab" },
  { title: "Third Tab" }
]

const TabExample = () => (
  <div>
    <WhiteSpace />
    <Tabs
      tabs={tabs}
      initalPage={"t2"}
      renderTabBar={renderTabBar}
      tabBarPosition='left'
      tabDirection='vertical'
      tabBarBackgroundColor='#f3f5f9'
      // 增加下划线的宽度与自定义的宽度一致
      tabBarUnderlineStyle={{ transform: "scaleX(0.x)" }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          height: "250px",
          backgroundColor: "#fff"
        }}
      >
        Content of first tab
      </div>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          height: "250px",
          backgroundColor: "#fff"
        }}
      >
        Content of second tab
      </div>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          height: "250px",
          backgroundColor: "#fff"
        }}
      >
        Content of third tab
      </div>
    </Tabs>
    <WhiteSpace />
  </div>
)
```

> - 相关的参考资料：
>
> - [Tabs的underline如何自定义宽度且能居中修改样式后的文字下面](https://github.com/ant-design/ant-design-mobile/issues/3322)
> - [tabs 下划线 指定样式方法](https://github.com/ant-design/ant-design-mobile/issues/3325)
> - [antd Mobile Tabs 垂直布局 后台传入数据](https://blog.csdn.net/qq_37786243/article/details/89280048)

