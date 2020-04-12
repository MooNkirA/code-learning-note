# Day03 CMS页面管理开发

## 1. 自定义条件查询
### 1.1. 需求分析

- 在页面输入查询条件，查询符合条件的页面信息。
    - 站点Id：精确匹配
    - 模板Id：精确匹配
    - 页面别名：模糊匹配

### 1.2. 条件查询服务端
#### 1.2.1. Dao 层条件查询 API 测试

- 使用Spring Data Mongodb中提供的`<S extends T> List<S> findAll(Example<S> example, Sort sort);`方法实现
- 测试findAll方法实现自定义条件查询

```java
/**
 * 自定义条件查询测试
 */
@Test
public void testFindAllByExample() {
    // 创建并设置查询条件值(如果设置多个属性，则默认是合并几个条件来查询)
    CmsPage cmsPage = new CmsPage();

    // 条件1：要查询5a751fab6abb5044e0d19ea1站点的页面
    // cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
    // 条件2：设置模板id条件
    // cmsPage.setTemplateId("5a962bf8b00ffc514038fafa");
    // 条件3：设置页面别名
    cmsPage.setPageAliase("轮播");

    // 创建条件匹配器
    ExampleMatcher exampleMatcher = ExampleMatcher.matching();
    /*
     *  用于设置某些字段进行模糊查询
     *  ExampleMatcher withMatcher(String propertyPath, GenericPropertyMatcher genericPropertyMatcher);
     *      参数propertyPath：需要条件查询的对应字段名（实体类）
     *      参数genericPropertyMatcher：条件查询的方式
     *      返回值ExampleMatcher：增加查询方式后的匹配器副本
     *          例如：
     *              ExampleMatcher.GenericPropertyMatchers.contains() // 包含关键字
     *              ExampleMatcher.GenericPropertyMatchers.startsWith() // 前缀匹配
     *
     *  下面设置了“页面别名”模糊查询，需要自定义字符串的匹配器实现模糊查询
     */
    exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

    // 创建条件对象
    Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

    // 创建分页参数
    Pageable pageable = PageRequest.of(0, 10);

    // 调用查询
    Page<CmsPage> cmsPages = cmsPageRepository.findAll(example, pageable);
    System.out.println("总记录数===" + cmsPages.getTotalElements());
    System.out.println("查询内容===" + cmsPages.getContent());
}
```

#### 1.2.2. Service 层

在PageService的findlist方法中增加自定义条件查询代码

```java
public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
    // 判断查询条件
    if (queryPageRequest == null) {
        queryPageRequest = new QueryPageRequest();
    }

    // 创建条件匹配器，其中“页面名称”模糊查询，需要自定义字符串的匹配器实现模糊查询
    ExampleMatcher exampleMatcher = ExampleMatcher.matching()
            .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

    // 创建与设置条件值
    CmsPage cmsPage = new CmsPage();
    //设置条件值（站点id）
    if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
        cmsPage.setSiteId(queryPageRequest.getSiteId());
    }
    //设置模板id作为查询条件
    if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
        cmsPage.setTemplateId(queryPageRequest.getTemplateId());
    }
    //设置页面别名作为查询条件
    if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
        cmsPage.setPageAliase(queryPageRequest.getPageAliase());
    }

    // 创建条件查询对象
    Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

    // 判断分页参数，如果为空，给默认值
    if (page <= 0) {
        page = 1;
    }
    // 为了适应mongodb的接口将页码减1
    page = page - 1;
    if (size <= 0) {
        size = 10;
    }
    // 创建分页对象
    // Pageable pageable = new PageRequest(page, size); // 此方式过时
    Pageable pageable = PageRequest.of(page, size);

    // 分页 + 条件查询
    Page<CmsPage> cmsPages = cmsPageRepository.findAll(example, pageable);

    // 创建返回结果对象
    QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
    // 设置数据列表
    cmsPageQueryResult.setList(cmsPages.getContent());
    // 设置数据总记录数
    cmsPageQueryResult.setTotal(cmsPages.getTotalElements());

    // 返回结果
    return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
}
```

#### 1.2.3. controller层

无需修改，直接使用swagger测试：http://localhost:31001/swagger-ui.html

### 1.3. 条件查询前端
#### 1.3.1. 页面管理页面

1. 增加查询表单

```html
<!--查询表单-->
<el-form :model="params">
  <!-- 下拉框暂时写死数据，无查询后台 -->
  <el-select v-model="params.siteId" placeholder="请选择站点">
    <el-option
      v-for="item in siteList"
      :key="item.siteId"
      :label="item.siteName"
      :value="item.siteId"
    ></el-option>
  </el-select>页面别名：
  <el-input v-model="params.pageAliase" style="width: 100px"></el-input>
  <el-button type="primary" size="small" v-on:click="query">查询</el-button>
</el-form>
```

2. 修改查询数据模型对象params，增加siteList、pageAliase、siteId

```js
data() {
    return {
      siteList: [],  //站点列表
      list: [], // 表格数据
      total: 0,  // 总记录数
      params: {
        siteId: '',
        pageAliase: '',
        page: 1,  // 页码
        size: 10   // 每页显示个数
      }
    }
}
```

3. 在钩子方法中构建siteList站点列表（死数据）

```js
mounted() {
    // 当DOM元素渲染完成后调用query
    this.query()
    //初始化站点列表
    this.siteList = [
      {
        siteId: '5a751fab6abb5044e0d19ea1',
        siteName: '门户主站'
      },
      {
        siteId: '102',
        siteName: '测试站'
      }
    ]
}
```

#### 1.3.2. API 调用

1. 向服务端传递查询条件，修改 cms.js

```js
// public是对axios的工具类封装，定义了http请求方法
import http from './../../../base/api/public'
// 导入配置文件
let sysConfig = require('@/../config/sysConfig')
// 获取学成api请求url前缀，sysConfig.xcApiUrlPre = '/api'
let apiUrl = sysConfig.xcApiUrlPre;
// 导入node的URL查询字符串工具
import querystring from 'querystring'

export const page_list = (page, size, params) => {
  // 将params对象数据拼装成key-value字符串
  let queryString = querystring.stringify(params)

  // 拼接后的url为/api/cms/page/list/xx/xx，代理后为请求http://localhost:31001/cms/page/list/xx/xx
  return http.requestQuickGet(apiUrl + '/cms/page/list/' + page + '/' + size + '?' + queryString)
}
```

注：这里对查询对象进行转换成“aa=xx&bb=xx&...”的格式，使用了node.js的 `querystring`（查询字符串）API

2. 页面调用api方法

```js
// 查询后台方法
query() {
  cmsApi.page_list(this.params.page, this.params.size, this.params).then(response => {
    console.log("cmsList返回值===" + JSON.stringify(response))
    this.list = response.queryResult.list
    this.total = response.queryResult.total
  })
}
```

#### 1.3.3. node.js 的 querystring 工具类
##### 1.3.3.1. 引入模块

querystring 模块提供用于解析和格式化 URL 查询字符串的实用工具。 它可以使用以下方式访问：

```js
const querystring = require('querystring');
```

##### 1.3.3.2. 常用方法

```js
querystring.stringify(obj[, sep[, eq[, options]]])
```

- 参数`obj <Object>`：要序列化为 URL 查询字符串的对象。
- 参数`sep <string>`：用于在查询字符串中分隔键值对的子字符串。默认值: `'&'`。
- 参数`eq <string>`：用于在查询字符串中分隔键和值的子字符串。默认值: `'='`。
- 参数`options`
    - `encodeURIComponent <Function>` 在查询字符串中将 URL 不安全字符转换为百分比编码时使用的函数。默认值: `querystring.escape()`。

querystring.stringify() 方法通过迭代对象的自身属性从给定的 obj 生成 URL 查询字符串。

它序列化了传入 obj 中的以下类型的值：`<string> | <number> | <boolean> | <string[]> | <number[]> | <boolean[]>`。 任何其他输入值都将被强制转换为空字符串。

```js
querystring.stringify({ foo: 'bar', baz: ['qux', 'quux'], corge: '' });
// 返回 'foo=bar&baz=qux&baz=quux&corge='

querystring.stringify({ foo: 'bar', baz: 'qux' }, ';', ':');
// 返回 'foo:bar;baz:qux'
```

默认情况下，查询字符串中需要百分比编码的字符将编码为 UTF-8。 如果需要其他编码，则需要指定其他 encodeURIComponent 选项：

```js
// 假设 gbkEncodeURIComponent 函数已存在。
querystring.stringify({ w: '中文', foo: 'bar' }, null, null,
                      { encodeURIComponent: gbkEncodeURIComponent });
```

## 2. 新增页面
### 2.1. 新增页面接口定义

1. 定义响应模型

```java
@Data
public class CmsPageResult extends ResponseResult {
    CmsPage cmsPage;

    public CmsPageResult(ResultCode resultCode, CmsPage cmsPage) {
        super(resultCode);
        this.cmsPage = cmsPage;
    }
}
```

2. 定义添加Api，在api工程中CmsPageControllerApi添加接口方法

```java
/**
 * 新增页面
 * @param cmsPage
 * @return
 */
@ApiOperation("新增页面")
public CmsPageResult add(CmsPage cmsPage);
```

### 2.2. 新增页面服务端
#### 2.2.1. 页面唯一索引

在cms_page集中上创建页面名称、站点Id、页面webpath为唯一索引

#### 2.2.2. Dao 层

1. 在CmsPageRepository接口，添加根据页面名称、站点Id、页面webpath查询页面方法，此方法用于校验页面是否存在。注：无需实现，spring-data-mongodb默认实现了

```java
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
    /**
     * 根据页面名称、站点Id、页面webpath查询
     *
     * @param pageName
     * @param siteId
     * @param pageWebPath
     * @return
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String pageWebPath);
}
```

2. 使用 CmsPageRepository 提供的save方法

#### 2.2.3. Service 层

```java
/**
 * 新增页面
 *
 * @param cmsPage 新增的页面对象
 * @return 处理结果
 */
public CmsPageResult add(CmsPage cmsPage) {
    // 根据页面名称、站点Id、页面webpath去查询cms_page集合，如果查到说明此页面已经存在，如果查询不到再继续添加
    CmsPage cmsPageDb = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath
            (cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

    if (cmsPageDb == null) {
        // 添加页面主键由spring data自动生成
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        // 返回成功结果
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    // 返回添加失败结果
    return new CmsPageResult(CommonCode.FAIL, null);
}
```

#### 2.2.4. Controller 层

```java
/**
 * 新增页面
 *
 * @param cmsPage
 * @return
 */
@Override
@PostMapping("/add")
public CmsPageResult add(@RequestBody CmsPage cmsPage) {
    return pageService.add(cmsPage);
}
```

#### 2.2.5. 接口测试

使用Postman进行测试，post请求：http://localhost:31001/cms/page/add。请求内容为json数据，测试数据如下：

```json
{
  "dataUrl": "string",
  "htmlFileId": "string",
  "pageAliase": "string",
  "pageHtml": "string",
  "pageName": "测试页面",
  "pageParameter": "string",
  "pagePhysicalPath": "string",
  "pageStatus": "string",
  "pageTemplate": "string",
  "pageType": "string",
  "pageWebPath": "string",
  "siteId": "string",
  "templateId": "string"
}
```

### 2.3. 新增页面前端
#### 2.3.1. 编写page_add.vue页面

使用Element-UI的form组件编写添加表单内容，步骤如下：

1. 创建page_add.vue页面
2. 配置路由（/module/cms/router/index.js），在cms模块的路由文件中配置“新增页面”的路由：

```js
import Home from '@/module/home/page/home.vue';
import page_list from '@/module/cms/page/page_list.vue';
import page_add from '@/module/cms/page/page_add.vue';
export default [{
  path: '/cms',
  component: Home,
  name: 'CMS内容管理',
  hidden: false,
  children: [{
      path: '/cms/page/list',
      name: '页面列表',
      component: page_list,
      hidden: false
    },
    {
      path: '/cms/page/add',
      name: '新增页面',
      component: page_add,
      hidden: true // 注意：由于“添加页面”不需要显示为一个菜单，这里hidden设置为true隐藏菜单。
    }
  ]
}]
```

测试，在浏览器地址栏输入http://localhost:11000/#/cms/page/add

3. 在页面列表添加“添加页面”的按钮。实际情况是用户进入页面查询列表，点击“新增页面”按钮进入新增页面窗口。在查询按钮的旁边添加：

```js
<router-link :to="{path:'/cms/page/add'}">
    <el-button type="primary" size="small">新增页面</el-button>
</router-link>
```

说明：router-link是vue提供的路由功能，用于在页面生成路由链接，最终在html渲染后就是`<a>`标签。参数to：目标路由地址

4. 修改新增页面page_add.vue，使用表单组件，详情属性及事件参考http://element.eleme.io/#/zh-CN/component/form

```html
<div>
    <el-form :model="pageForm" label-width="80px">
        <el-form-item label="所属站点" prop="siteId">
            <el-select v-model="pageForm.siteId" placeholder="请选择站点">
                <el-option
                    v-for="item in siteList"
                    :key="item.siteId"
                    :label="item.siteName"
                    :value="item.siteId"
                ></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="选择模版" prop="templateId">
            <el-select v-model="pageForm.templateId" placeholder="请选择">
                <el-option
                    v-for="item in templateList"
                    :key="item.templateId"
                    :label="item.templateName"
                    :value="item.templateId"
                ></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="页面名称" prop="pageName">
            <el-input v-model="pageForm.pageName" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="别名" prop="pageAliase">
            <el-input v-model="pageForm.pageAliase" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="访问路径" prop="pageWebPath">
            <el-input v-model="pageForm.pageWebPath" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="物理路径" prop="pagePhysicalPath">
            <el-input v-model="pageForm.pagePhysicalPath" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="类型">
            <el-radio-group v-model="pageForm.pageType">
                <el-radio class="radio" label="0">静态</el-radio>
                <el-radio class="radio" label="1">动态</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="创建时间">
            <el-date-picker
                type="datetime"
                placeholder="创建时间"
                v-model="pageForm.pageCreateTime"
            ></el-date-picker>
        </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addSubmit">提交</el-button>
    </div>
</div>
```

5. 增加数据对象

```js
data() {
    return {
        // 站点列表
        siteList: [],
        // 模版列表
        templateList: [],
        // 新增界面数据
        pageForm: {
            siteId: '',
            templateId: '',
            pageName: '',
            pageAliase: '',
            pageWebPath: '',
            pageParameter: '',
            pagePhysicalPath: '',
            pageType: '',
            pageCreateTime: new Date()
        }
    }
},
methods: {
    addSubmit() {
        alert('提交了')
    }
}
```

6. 站点及模板数据（先使用静态数据）。在created钩子方法中定义，created是在html渲染之前执行，这里推荐使用created。

```js
created() {
    // 初始化站点列表
    this.siteList = [
        {
            siteId: '5a751fab6abb5044e0d19ea1',
            siteName: '门户主站'
        },
        {
            siteId: '102',
            siteName: '测试站'
        }
    ]
    // 模板列表
    this.templateList = [
        {
            templateId: '5a962b52b00ffc514038faf7',
            templateName: '首页'
        },
        {
            templateId: '5a962bf8b00ffc514038fafa',
            templateName: '轮播图'
        }
    ]
},
```

7. 运行预览新增页面

#### 2.3.2. 添加返回

进入新增页面后只能通过菜单再次进入页面列表，可以在新增页面添加“返回”按钮，点击返回按钮返回到页面列表。

1. 修改page_list.vue页面，给新增页面按钮带上参数

```html
<router-link
    :to="{path:'/cms/page/add', query:{page:this.params.page, siteId:this.params.siteId}}"
>
    <el-button type="primary" size="small">新增页面</el-button>
</router-link>
```

说明：`<router-link>`标签，query表示在路由url上带上参数

2. 在page_add.vue上定义返回按钮和返回方法

```html
<el-button type="primary" @click="go_back">返回</el-button>
```

```js
// 返回查询列表页面
go_back() {
    this.$router.push({
        path: '/cms/page/list',
        query: {
            page: this.$route.query.page, // 取出路由中的参数
            siteId: this.$route.query.siteId
        }
    })
}
```

- **说明：this.$route.query 表示取出路由上的参数列表，有两个取路由参数的方法**
    1. 通过在路由上添加key/value串使用`this.$route.query`来取参数，例如：`/router1?id=123`,`/router1?id=456`可以通过`this.$route.query.id`获取参数id的值。
    2. 通过将参数作为路由一部分进行传参数使用`this.$route.params`来获取，例如：定义的路由为`/router1/:id`，请求`/router1/123`时可以通过`this.$route.params.id`来获取，此种情况用`this.$route.query.id`是拿不到的。

3. 查询列表支持回显。进入查询列表，从url中获取页码和站点id并赋值给数据模型对象，从而实现页面回显。

url例子：http://localhost:11000/#/cms/page/list?page=2&siteId=5a751fab6abb5044e0d19ea1

```js
created() {
    // 取出路由中的参数，赋值给查询数据对象
    this.params.page = Number.parseInt(this.$route.query.page || 1)
    this.params.siteId = this.$route.query.siteId || ''
},
```

**小技巧：使用`||`返回第一个有效值**

#### 2.3.3. 表单校验

1. 配置校验规则，Element-UI的Form组件提供表单校验的方法：

在form属性上配置rules（表单验证规则）

```html
<el-form :model="pageForm" label-width="80px" :rules="pageFormRules" ref="pageForm">
    ...
</el-form>
```

在数据模型中配置校验规则：

```js
data() {
    return {
        ...
        pageFormRules: {
            siteId: [
                { required: true, message: '请选择站点', trigger: 'blur' }
            ],
            templateId: [
                { required: true, message: '请选择模版', trigger: 'blur' }
            ],
            pageName: [
                { required: true, message: '请输入页面名称', trigger: 'blur' }
            ],
            pageWebPath: [
                { required: true, message: '请输入访问路径', trigger: 'blur' }
            ],
            pagePhysicalPath: [
                { required: true, message: '请输入物理路径', trigger: 'blur' }
            ]
        }
    }
},
```

更多的校验规则参考http://element.eleme.io/#/zh-CN/component/form中 “表单验证”的例子。

2. 点击提交按钮触发校验。
    1. 在form表单上添加 ref属性（`ref="pageForm"`）在校验时引用此表单对象
    2. 在点击提交按钮后，请求后端前执行校验

    ```js
    addSubmit() {
        // 校验表单
        this.$refs.pageForm.validate(valid => {
            if (valid) {
                alert("表单验证成功，提交！")
            } else {
                alert("表单验证失败！")
            }
        })
    },
    ```

#### 2.3.4. Api调用

1. 在cms.js中定义page_add方法。

```js
/* 新增页面 */
export const page_add = params => {
  return http.requestPost(apiUrl + '/cms/page/add', params)
}
```

2. 添加事件，修改addSubmit方法

```js
import * as cmsApi from '../api/cms'

...

addSubmit() {
    // 校验表单
    this.$refs.pageForm.validate(valid => {
        if (valid) {
            // 表单校验成功
            this.$confirm('您确认提交吗?', '提示', {}).then(() => {
                // 调用page_add方法请求服务端的新增页面接口
                cmsApi.page_add(this.pageForm).then(response => {
                    // 解析服务端响应内容
                    console.log(response);
                    if (response.success) {
                        // 新增成功
                        this.$message({ message: '提交成功', type: 'success' })
                        // 清空表单
                        this.$refs.pageForm.resetFields()
                    } else {
                        this.$message.error('提交失败')
                    }
                })
            })
        }
    })
}
```

## 3. 修改页面

修改页面用户操作流程：

1. 用户进入修改页面，在页面上显示了修改页面的信息
2. 用户修改页面的内容，点击“提交”，提示“修改成功”或“修改失败

### 3.1. 修改页面接口定义

CmsPageControllerApi.java，定义的修改页面与根据id查询的API如下：

```java
/**
 * 根据页面id查询页面信息
 *
 * @param id
 * @return
 */
@ApiOperation("根据页面id查询页面信息")
public CmsPage findById(String id);

/**
 * 修改页面
 *
 * @param id
 * @param cmsPage
 * @return
 */
@ApiOperation("修改页面")
public CmsPageResult edit(String id, CmsPage cmsPage);
```

**说明：提交数据使用post、put都可以，只是根据http方法的规范，put方法是对服务器指定资源进行修改，所以这里使用put方法对页面修改进行修改。**

### 3.2. 修改页面服务端
#### 3.2.1. Dao 层

- 使用 Spring Data 提供的findById方法完成根据主键查询
- 使用 Spring Data 提供的save方法完成数据保存

#### 3.2.2. Service 层

```java
/**
 * 根据页面id查询页面
 *
 * @param id 页面id
 * @return CmsPage对象
 */
public CmsPage getById(String id) {
    Optional<CmsPage> optional = cmsPageRepository.findById(id);
    if (optional.isPresent()) {
        return optional.get();
    }
    return null;
}

/**
 * 修改页面
 *
 * @param id      页面id
 * @param cmsPage 修改的页面数据对象
 * @return 处理结果
 */
public CmsPageResult update(String id, CmsPage cmsPage) {
    // 根据id从数据库查询页面信息
    CmsPage original = getById(id);

    // 判断当前记录是否存在
    if (original != null) {
        // 准备更新数据，设置要修改的数据
        // 更新模板id
        original.setTemplateId(cmsPage.getTemplateId());
        // 更新所属站点
        original.setSiteId(cmsPage.getSiteId());
        // 更新页面别名
        original.setPageAliase(cmsPage.getPageAliase());
        // 更新页面名称
        original.setPageName(cmsPage.getPageName());
        // 更新访问路径
        original.setPageWebPath(cmsPage.getPageWebPath());
        // 更新物理路径
        original.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

        // 提交修改
        CmsPage saveCmsPage = cmsPageRepository.save(original);
        if (saveCmsPage != null) {
            // 如果成功修改，返回的对象不会为空
            return new CmsPageResult(CommonCode.SUCCESS, saveCmsPage);
        }
    }

    // 修改失败
    return new CmsPageResult(CommonCode.FAIL, null);
}
```

#### 3.2.3. Controller 层

- 实现根据id查询页面接口方法

```java
@Override
@GetMapping("/get/{id}")
public CmsPage findById(@PathVariable("id") String id) {
    return pageService.getById(id);
}
```

- 实现保存页面信息接口方法

```java
@Override
@PutMapping("/edit/{id}")   // 这里使用put方法，http方法中put表示更新
public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
    return pageService.update(id, cmsPage);
}
```

### 3.3. 修改页面前端
#### 3.3.1. 页面处理流程

1. 进入页面，通过钩子方法请求服务端获取页面信息，并赋值给数据模型对象
2. 页面信息通过数据绑定在表单显示
3. 用户修改信息点击“提交”请求服务端修改页面信息接口

#### 3.3.2. 编写page_edit页面

修改页面的布局同添加页面，可以直接复制添加页面，在添加页面基础上修改

1. page_edit.vue页面代码（略）
2. 配置路由，进入修改页面时传入pageId

```js
import page_edit from '@/module/cms/page/page_edit.vue';
export default [{
  ...
  children: [
    ...
    {
      path: '/cms/page/edit/:pageId',
      name: '修改页面',
      component: page_edit,
      hidden: true
    }
  ]
}]
```

3. 在页面列表添加“编辑”链接，参考table组件的例子，在page_list.vue上添加“操作”列

```html
<el-table-column label="操作" width="80">
    <template slot-scope="page">
        <el-button size="small" type="text" @click="edit(page.row.pageId)">编辑</el-button>
    </template>
</el-table-column>
```

4. page_list.vue编写编辑按钮绑定事件edit方法

```js
// 修改数据的方法
edit(pageId) {
    // 打印修改页面，传递pageId
    this.$router.push({
        path: '/cms/page/edit/' + pageId,
        query: { page: this.params.page, siteId: this.params.siteId }
    })
}
```

#### 3.3.3. 页面内容显示

1. 进入修改页面立即显示要修改的页面信息。修改cms.js文件，定义api方法

```js
/* 根据id查询页面 */
export const page_get = id => {
  return http.requestQuickGet(apiUrl + '/cms/page/get/' + id)
}
```

2. 定义数据对象。进入修改页面传入pageId参数，在数据模型中添加pageId

```js
data() {
    return {
        ...
        // 页面id
        pageId: '',
        ...
},
```

3. 在created钩子方法中调用根据id查询页面信息方法

```js
created() {
    ...
    // 页面参数通过路由传入，这里通过this.$route.params来获取
    this.pageId = this.$route.params.pageId
    // 根据主键查询页面信息
    cmsApi.page_get(this.pageId).then(res => {
        console.log(res)
        if (res.success) {
            this.pageForm = res.cmsPage
        }
    })
}
```

### 3.4. Api调用修改页面数据

1. cms.js定义修改的api方法

```js
/* 修改页面提交 */
export const page_edit = (id, params) => {
  return http.requestPut(apiUrl + '/cms/page/edit/' + id, params)
}
```

2. 添加提交按钮

```html
<el-button type="primary" @click.native="editSubmit" :loading="addLoading">提交</el-button>
```

3. 提交按钮事件内容

```js
editSubmit() {
    // 校验表单
    this.$refs.pageForm.validate(valid => {
        if (valid) {
            // 表单校验成功
            this.$confirm('您确认提交吗?', '提示', {}).then(() => {
                this.addLoading = true
                // 调用page_add方法请求服务端的新增页面接口
                cmsApi.page_edit(this.pageId, this.pageForm).then(response => {
                    // 解析服务端响应内容
                    console.log(response);
                    if (response.success) {
                        // 移除遮罩
                        this.addLoading = false
                        // 新增成功
                        this.$message({ message: '提交成功', type: 'success' })
                        // 修改成功后返回列表页面
                        this.go_back()
                    } else {
                        this.$message.error('提交失败')
                    }
                })
            })
        }
    })
},
```

## 4. 删除页面

- 用户操作流程：
    1. 用户进入用户列表，点击“删除”
    2. 执行删除操作，提示“删除成功”或“删除失败”

### 4.1. 删除页面接口定义

```java
/**
 * 删除页面
 *
 * @param id 页面id
 * @return 响应结果
 */
@ApiOperation("删除页面")
public ResponseResult delete(String id);
```

### 4.2. 删除页面服务端
#### 4.2.1. Dao 层

使用 Spring Data 提供的deleteById方法完成删除操作

#### 4.2.2. Service 层

```java
/**
 * 根据id删除页面
 *
 * @param id 页面id
 * @return 处理结果
 */
public ResponseResult delete(String id) {
    // 先根据id查询
    Optional<CmsPage> optional = cmsPageRepository.findById(id);

    if (optional.isPresent()) {
        // 如果数据存在，调用数据访问层删除方法
        cmsPageRepository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    // 操作失败
    return new ResponseResult(CommonCode.FAIL);
}
```

#### 4.2.3. Controller 层

```java
@Override
@DeleteMapping("/del/{id}")
public ResponseResult delete(@PathVariable("id") String id) {
    return pageService.delete(id);
}
```

### 4.3. 删除页面前端
#### 4.3.1. Api方法

```js
/* 删除页面 */
export const page_del = (id) => {
  return http.requestDelete(apiUrl + '/cms/page/del/' + id)
}
```

#### 4.3.2. 编写页面

1. 在page_list.vue页面添加删除按钮

```html
<el-button size="small" type="text" @click="del(page.row.pageId)">删除</el-button>
```

2. 添加删除事件

```js
// 删除页面方法
del(pageId) {
    this.$confirm('您确认删除吗?', '提示', {}).then(() => {
        // 调用服务端接口
        cmsApi.page_del(pageId).then(res => {
            if (res.success) {
                this.$message.success("删除成功")
                // 刷新页面
                this.query()
            } else {
                this.$message.error("删除失败")
            }
        })
    })
}
```

## 5. 异常处理
### 5.1. 异常处理的问题分析

- 从PageService服务层中的添加页面的add方法中分析存在问题：
    1. 上边的代码只要操作不成功仅向用户返回“错误代码：11111，失败信息：操作失败”，无法区别具体的错误信息。
    2. service方法在执行过程出现异常在哪捕获？在service中需要都加`try/catch`，如果在controller也需要添加`try/catch`，代码冗余严重且不易维护。
- 解决方案：
    1. 在Service方法中的编码顺序是先校验判断，有问题则抛出具体的异常信息，最后执行具体的业务操作，返回成功信息。
    2. 在统一异常处理类中去捕获异常，无需controller捕获异常，向用户返回统一规范的响应信息。

### 5.2. 异常处理流程

系统对异常的处理使用统一的异常处理流程：

1. 自定义异常类型。
2. 自定义错误代码及错误信息。
3. 对于可预知的异常由程序员在代码中主动抛出，由SpringMVC统一捕获。
    - 可预知异常是程序员在代码中手动抛出本系统定义的特定异常类型，由于是程序员抛出的异常，通常异常信息比较齐全，程序员在抛出时会指定错误代码及错误信息，获取异常信息也比较方便。
4. 对于不可预知的异常（运行时异常）由SpringMVC统一捕获Exception类型的异常。
    - 不可预知异常通常是由于系统出现bug、或一些不要抗拒的错误（比如网络中断、服务器宕机等），异常类型为RuntimeException类型（运行时异常）。
5. 可预知的异常及不可预知的运行时异常最终会采用统一的信息格式（错误代码+错误信息）来表示，最终也会随请求响应给客户端。

异常抛出及处理流程:

![异常处理流程图](images/20190519105040784_31656.png)

1. 在controller、service、dao中程序员抛出自定义异常；springMVC框架抛出框架异常类型
2. 统一由异常捕获类捕获异常，并进行处理
3. 捕获到自定义异常则直接取出错误代码及错误信息，响应给用户。
4. 捕获到非自定义异常类型首先从Map中找该异常类型是否对应具体的错误代码，如果有则取出错误代码和错误信息并响应给用户，如果从Map中找不到异常类型所对应的错误代码则统一为99999错误代码并响应给用户。
5. 将错误代码及错误信息以Json格式响应给用户。

### 5.3. 可预知异常处理
#### 5.3.1. 自定义异常类

在common工程定义异常类型

```java
/**
 * 自定义异常类型
 */
public class CustomException extends RuntimeException {

    // 错误代码
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
```

#### 5.3.2. 异常抛出类

```java
/**
 * 异常抛出工具类
 */
public class ExceptionCast {
    /* 使用此静态方法抛出自定义异常 */
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
```

#### 5.3.3. 异常捕获类

使用`@ControllerAdvice`和`@ExceptionHandler`注解来捕获指定类型的异常

```java
/**
 * 统一异常捕获类
 */
// @ControllerAdvice // 控制器增强，在spring 3.2中新增的注解
// 如果类中所有方法都返回json格式。可以使用@RestControllerAdvice注解，是@ControllerAdvice与@ResponseBody的组合体
@RestControllerAdvice
public class ExceptionCatch {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    /**
     * 捕获CustomException此类异常
     *
     * @param customException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    // @ResponseBody
    public ResponseResult customException(CustomException customException) {
        // 记录日志
        LOGGER.error("catch exception:{}", customException.getMessage());

        // 获取捕获的自定义异常信息，创建返回对象
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
}
```

#### 5.3.4. 异常处理测试
##### 5.3.4.1. 定义错误代码

每个业务操作的异常使用异常代码去标识。使用cms模块做为示例

```java
@ToString
public enum CmsCode implements ResultCode {
    CMS_ADDPAGE_EXISTSNAME(false,24001,"页面名称已存在！"),
    CMS_GENERATEHTML_DATAURLISNULL(false,24002,"从页面信息中找不到获取数据的url！"),
    CMS_GENERATEHTML_DATAISNULL(false,24003,"根据页面的数据url获取不到数据！"),
    CMS_GENERATEHTML_TEMPLATEISNULL(false,24004,"页面模板为空！"),
    CMS_GENERATEHTML_HTMLISNULL(false,24005,"生成的静态html为空！"),
    CMS_GENERATEHTML_SAVEHTMLERROR(false,24005,"保存静态html出错！"),
  	CMS_PAGE_NOTEXISTS(false,24006,"页面不存在"),
    CMS_COURSE_PERVIEWISNULL(false,24007,"预览页面为空！");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private CmsCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
```

##### 5.3.4.2. 自定义异常处理测试

1. 抛出异常。在controller、service、 dao中都可以抛出异常。例如：修改PageService的add方法，添加抛出异常的代码

```java
public CmsPageResult add(CmsPage cmsPage) {

    if (cmsPage == null) {
        // 抛出异常，非法参数异常..指定异常信息的内容
    }

    // 根据页面名称、站点Id、页面webpath去查询cms_page集合，如果查到说明此页面已经存在，如果查询不到再继续添加
    CmsPage cmsPageDb = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath
            (cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

    if (cmsPageDb != null) {
        // 页面已经存在。抛出异常，异常内容就是页面已经存在
        ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
    }

    // 添加页面主键由spring data自动生成
    cmsPage.setPageId(null);
    cmsPageRepository.save(cmsPage);
    // 返回成功结果
    return new CmsPageResult(CommonCode.SUCCESS, cmsPage);

}
```

2. 修改工程入口，扫描到异常捕获的类ExceptionCatch所在的包

```java
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms") // 扫描实体类所在包
@ComponentScan(basePackages = {"com.xuecheng.api", "com.xuecheng.framework"}) // 扫描接口，common工程所在包
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"}) // 扫描本项目下的所有类（其实不写好像也是可以扫描的）
public class ManageCmsApplication {
    ...
}
```

3. 修改前端展示异常信息

```js
addSubmit() {
    // 校验表单
    this.$refs.pageForm.validate(valid => {
        if (valid) {
            // 表单校验成功
            this.$confirm('您确认提交吗?', '提示', {}).then(() => {
                // 调用page_add方法请求服务端的新增页面接口
                cmsApi.page_add(this.pageForm).then(response => {
                    // 解析服务端响应内容
                    console.log(response);
                    if (response.success) {
                        // 新增成功
                        this.$message({ message: '提交成功', type: 'success' })
                        // 清空表单
                        this.$refs.pageForm.resetFields()
                    } else if (response.message) {
                        // 后端有返回异常信息
                        this.$message.error(response.message)
                    } else {
                        this.$message.error('提交失败')
                    }
                })
            })
        }
    })
},
```

### 5.4. 不可预知异常处理
#### 5.4.1. 异常抛出测试

使用postman测试添加页面，不输入cmsPost信息，提交，报错信息如下：

```
org.springframework.http.converter.HttpMessageNotReadableException
此异常是springMVC在进行参数转换时报的错误。

具体的响应的信息为：
{
    "timestamp": 1528712906727,
    "status": 400,
    "error": "Bad Request",
    "exception": "org.springframework.http.converter.HttpMessageNotReadableException",
    "message": "Required request body is missing: public com.xuecheng.framework.domain.cms.response.CmsPageResult com.xuecheng.manage_cms.web.controller.CmsPageController.add(com.xuecheng.framework.domain.cms.CmsPage)",
    "path": "/cms/page/add"
}
```

上边的响应信息在客户端是无法解析的。所以需要在异常捕获类中添加对Exception异常的捕获

#### 5.4.2. 异常捕获方法

- 针对上边的问题其解决方案是：
    1. 在map中配置HttpMessageNotReadableException和错误代码。
    2. 在异常捕获类中对Exception异常进行捕获，并从map中获取异常类型对应的错误代码，如果存在错误代码则返回此错误，否则统一返回99999错误。
- 具体的开发实现如下：
1. 在通用错误代码类CommCode中配置非法参数异常

```java
INVALID_PARAM(false, 10003, "非法参数！"),
```

2. 在异常捕获类中配置 HttpMessageNotReadableException 为非法参数异常。

```java
@RestControllerAdvice
public class ExceptionCatch {

    /* 日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    // 使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    // 定义map的builder对象，使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    static {
        // 定义加入一些基础的异常类型判断，异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }

    ...

    /**
     * 捕获Exception此类异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    // @ResponseBody
    public ResponseResult exception(Exception exception) {

        // 记录日志
        LOGGER.error("catch exception:{}", exception.getMessage());

        // 判断ImmutableMap对像是否已经构建
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build();
        }

        // 从ImmutableMap中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null) {
            return new ResponseResult(resultCode);
        } else {
            // 其他未知异常，统一返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }
}
```

## 6. 待完善功能
### 6.1. 查询条件完善

- 页面查询条件增加：页面名称、页面类型。
    - 页面名称对应CmsPage模型类中的pageName属性。
    - 页面类型对应CmsPage模型类中的pageType属性。
- 查询要求：
    - 页面名称：模糊查询
    - 页面类型：精确匹配，页面类型包括：静态和动态，在数据库中静态用“0”表示，动态用“1”表示。

### 6.2. 页面属性增加DataUrl

- 在CmsPage.java模型类型中有一个dataUrl属性，此属性在页面静态化时需要填写。
- 本需求要求：
    1. 在新增页面增加dataUrl输入框，并支持添加。
    2. 在修改页面增加dataUrl输入框，并支持修改。
