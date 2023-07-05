# Day12 搜索解决方案（三）-分页&价格区间筛选&排序&更新索引库

## 1. 按价格区间筛选

### 1.1. 需求分析

点击搜索面板上的价格区间，实现按价格筛选

### 1.2. 价格筛选-前端部分

#### 1.2.1. 前端控制层

- 修改pinyougou-search-web的searchController.js搜索条件封装对象，增加价格price属性

```js
/* 定义json对象封装页面的搜索参数 */
$scope.searchParam = {'keywords': '', 'category': '', 'brand': '', 'spec': {}, 'price': ''};
```

- 修改pinyougou-search-web的searchController.js添加搜索项和删除搜索项的方法，增加价格部分逻辑代码

```js
/* 定义添加搜索项的方法 */
$scope.addSearchItem = (key, value) => {
    // 判断页面点击的是商品分类或者品牌、价格
    if (key == 'category' || key == 'brand' || key == 'price') {
        $scope.searchParam[key] = value;
    ......
};

/* 定义删除搜索项的方法 */
$scope.removeSearchItem = (key) => {
    // 断点击移除的是商品分类或者品牌、价格
    if (key == 'category' || key == 'brand' || key == 'price') {
        $scope.searchParam[key] = '';
    ......
};
```

#### 1.2.2. 页面

- 修改search.html，在价格的标签上调用方法(大约207行)

```html
<div class="type-wrap" ng-if="searchParam.price == '' && searchParam.keywords != ''">
	<div class="fl key">价格</div>
	<div class="fl value">
		<ul class="type-list">
			<li>
				<a ng-click="addSearchItem('price', '0-500');">0-500元</a>
			</li>
			<li>
				<a ng-click="addSearchItem('price', '500-1000');">500-1000元</a>
			</li>
			<li>
				<a ng-click="addSearchItem('price', '1000-1500');">1000-1500元</a>
			</li>
			<li>
				<a ng-click="addSearchItem('price', '1500-2000');">1500-2000元</a>
			</li>
			<li>
				<a ng-click="addSearchItem('price', '2000-3000');">2000-3000元 </a>
			</li>
			<li>
				<a ng-click="addSearchItem('price', '3000-*');">3000元以上</a>
			</li>
		</ul>
	</div>
	<div class="fl ext">
	</div>
</div>
```

- 修改search.html，增加面包屑显示选择的价格区间(约167行)

```html
<li class="tag" ng-if="searchParam.price != ''"
    ng-click="removeSearchItem('price');">
    价格：{{ searchParam.price }}
    <i class="sui-icon icon-tb-close"></i>
</li>
```

### 1.3. 价格筛选-后端部分

- 修改pinyougou-search-service服务的ItemSearchServiceImpl现实类的search()方法，增加价格的过滤条件

```java
@Override
public Map<String, Object> search(Map<String, Object> params) {
    try {
        ......
        // 判断关键字是否为空
        if (StringUtils.isNoneBlank(keywords)) {
            ......
            /* =========增加过滤查询========== */
            // 商品分类过滤，获取页面传递的分类搜索
            // 商品品牌过滤，获取页面传递的品牌搜索
            // 规格选项过滤 spec_*，获取页面传递的规格搜索参数

            // 价格区间过滤
            String price = (String) params.get("price");
            if (StringUtils.isNoneBlank(price)) {
                // 将价格区间字符串转成价格范围的数组
                String[] priceArr = price.split("-");
                // 判断价格的起点值不等于0
                if (!"0".equals(priceArr[0])) {
                    // 创建条件对象，并添加过滤条件
                    Criteria criteriaStart = new Criteria("price").greaterThanEqual(priceArr[0]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteriaStart));
                }

                // 判断价格的结束值不等于*
                if (!"*".equals(priceArr[1])) {
                    // 创建条件对象，并添加过滤条件
                    Criteria criteriaEnd = new Criteria("price").lessThanEqual(priceArr[1]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteriaEnd));
                }
            }
            /* =========增加过滤查询========== */
            ......
        } else {
            ......
        }
        // 返回查询数据
        return data;
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
```

## 2. 搜索结果分页

### 2.1. 需求分析

在上述功能基础上实现分页查询

### 2.2. 分页搜索-后端部分

修改pinyougou-search-service工程ItemSearchServiceImpl现实类的search()方法，增加分页搜索条件

```java
@Override
public Map<String, Object> search(Map<String, Object> params) {
    try {
        ......
        // 获取页面传递的关键字
        String keywords = (String) params.get("keywords");

        // 获取当前页码
        Integer page = (Integer) params.get("page");
        if (page == null || page < 1) {
            // 设置默认页码
            page = 1;
        }
        // 获取每页显示的记录数
        Integer rows = (Integer) params.get("rows");
        if (rows == null || rows <= 0) {
            // 设置默认每页显示20条记录
            rows = 20;
        }

        // 判断关键字是否为空
        if (StringUtils.isNoneBlank(keywords)) {
            /* =========有关键字，设置高亮========== */
            // 创建高亮查询对象
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            ......
            /* =========设置高亮========== */
            ......
            /* =========增加过滤查询========== */

            // 设置分页起始记录数
            highlightQuery.setOffset((page - 1) * rows);
            // 设置每页显示的记录数
            highlightQuery.setRows(rows);

            // 获取高亮分页对象
            HighlightPage highlightPage = solrTemplate
                    .queryForHighlightPage(highlightQuery, SolrItem.class);

            ......

            // 获取高亮分页查询的数据，设置到map集合中
            data.put("rows", highlightPage.getContent());
            // 获取总页码
            data.put("totalPages", highlightPage.getTotalPages());
            // 获取总记录数
            data.put("total", highlightPage.getTotalElements());

        } else {
            /* 没有传递关键字，使用简单查询(不包含高亮) */
            // 创建普通的查询对象（使用Query接口或者实现类SimpleQuery都可以）
            SimpleQuery simpleQuery = new SimpleQuery("*:*");

            // 设置分页起始记录数
            simpleQuery.setOffset((page - 1) * rows);
            // 设置每页显示的记录数
            simpleQuery.setRows(rows);

            // 设置分页对象
            ScoredPage<SolrItem> scoredPage = solrTemplate
                    .queryForPage(simpleQuery, SolrItem.class);

            // 获取分页查询的数据，设置到map集合中
            List<SolrItem> solrItemList = scoredPage.getContent();

            // 返回查询数据
            data.put("rows", solrItemList);
            /* 输入关键字，将设置商品分类为null（可以不用设置）
            data.put("categoryList", null); */
            // 获取总页码
            data.put("totalPages", scoredPage.getTotalPages());
            // 获取总记录数
            data.put("total", scoredPage.getTotalElements());
        }
        ......
    } catch (Exception e) {
        ......
    }
}
```

### 2.3. 分页搜索-前端部分

#### 2.3.1. 初始化分页标签

- 如果需要修改默认页码和每页记录数，可以修改searchController.js的searchParam对象，为搜索对象添加属性

```js
/* 定义json对象封装页面的搜索参数 */
$scope.searchParam = {'keywords': '', 'category': '', 'brand': '',
    'spec': {}, 'price': '', 'page': 1, 'rows': 20};
```

- 修改searchController.js，增加页面显示的页码初始化的initPageNum()方法

```js
/* 定义初始化页码方法(定义为var变量，本js内使用即可，不需要页面使用) */
var initPageNum = function () {
    // 定义页数数组
    $scope.pageNums = [];
    // 总页数
    var totalPages = $scope.resultMap.totalPages;
    // 初始页面显示的开始页码
    var firstPage = 1;
    // 初始页面显示的结束页码
    var lastPage = totalPages;

    // 当前选择的页码
    var curPage = $scope.searchParam.page;

    /* 判断如果总页数大于5，显示部分页码 */
    if (totalPages > 5) {
        // 当前页码位置靠前面位置
        if (curPage <= 3) {
            // 页面显示页码共5页
            lastPage = 5
        } else if (curPage >= totalPages - 2) {
            // 当前页码位置靠后面，则显示后面5页即可
            firstPage = totalPages - 4;
        } else {
            // 当前页码在中间的位置，则前后都显示2页
            firstPage = curPage - 2;
            lastPage = curPage + 2;
        }
    }
    /* 循环生成页码 */
    for (let i = firstPage; i <= lastPage; i++) {
        $scope.pageNums.push(i);
    }
};
```

- 修改search()搜索方法，查询后调用页面初始化方法

```js
/* 定义搜索方法 */
$scope.search = function () {
    // 发送post异步请求，传递搜索参数，查询商品
    baseService.sendPost('/Search', $scope.searchParam)
        .then(function (response) {
            // 获取响应数据
            $scope.resultMap = response.data;
            // 调用初始化页码方法
            initPageNum();
        });
};
```

- 修改search.html，循环生成分页页码(327行)

```html
<div class="sui-pagination pagination-large">
	<ul>
		<li class="prev disabled">
			<a href="#">«上一页</a>
		</li>
        <li class="{{ searchParam.page == page ? 'active' : ''}}"
            ng-repeat="page in pageNums">
            <a href="#">{{ page }}</a>
        </li>
		<!--<li class="dotted"><span>...</span></li>-->
		<li class="next">
			<a href="#">下一页»</a>
		</li>
	</ul>
	<div>
        <span>共{{ resultMap.totalPages }}页&nbsp;</span>
        <span>到第<input type="text" class="page-num">页
            <button class="page-confirm" onclick="alert(1)">确定</button>
        </span>
    </div>
</div>
```

- 显示搜索总条数与搜索条件(145行)

```html
<div class="bread">
	<ul class="fl sui-breadcrumb">
		<li>
            按"<span style="color: red;">{{ searchParam.keywords }}</span>"关键字，
            搜索到<b>{{ resultMap.total }}</b>条记录.
        </li>
	</ul>
	<ul class="tags-choose">
	......
</div>
```

#### 2.3.2. 提交页码查询

- 在searchController.js增加方法，点击分页导航，页码改变执行查询

```js
/* 定义根据页码搜索的方法 */
$scope.pageSearch = function (page) {
    // 将入参的页码转在数字类型
    page = parseInt(page);

    // 判断页码参数
    if (page >= 1 && page <= $scope.resultMap.totalPages
            && page != $scope.searchParam.page) {
        // 将当前页码设置到搜索条件中
        $scope.searchParam.page = page;
        // 执行搜索方法
        $scope.search();
    }
};
```

- 修改search.html页面，给页码绑定点击事件，调用增加页码搜索的pageSearch()方法

```html
<div class="sui-pagination pagination-large">
	<ul>
		<li class="prev disabled">
            <a href="javascript:;" ng-click="pageSearch(searchParam.page - 1);">«上一页</a>
		</li>
        <li class="{{ searchParam.page == page ? 'active' : ''}}"
            ng-repeat="page in pageNums">
            <a href="javascript:;" ng-click="pageSearch(page);">{{ page }}</a>
        </li>
		<!--<li class="dotted"><span>...</span></li>-->
		<li class="next">
            <a href="javascript:;" ng-click="pageSearch(searchParam.page + 1);">下一页»</a>
		</li>
	</ul>
	<div>
        <span>共{{ resultMap.totalPages }}页&nbsp;</span>
        <span>到第<input type="text" style="width: 25px;" ng-model="jumpPage">页
            <button class="page-confirm" ng-click="pageSearch(jumpPage);">确定</button>
        </span>
    </div>
</div>
```

#### 2.3.3. 显示省略号

- 在初始化initPageNum方法中，增加分页省略号的处理

```js
var initPageNum = function () {
    ......
    // 初始页面显示的结束页码
    var lastPage = totalPages;

    /* 页码前面显示点的标识 */
    $scope.firstDot = true;
    /* 页码后面显示点的标识 */
    $scope.lastDot = true;

    /* 判断如果总页数大于5，显示部分页码 */
    if (totalPages > 5) {
        // 当前页码位置靠前面位置
        if (curPage <= 3) {
            // 页面显示页码共5页
            lastPage = 5
            // 前面不显示加点号
            $scope.firstDot = false;
        } else if (curPage >= totalPages - 2) {
            // 当前页码位置靠后面，则显示后面5页即可
            firstPage = totalPages - 4;
            // 后面不显示加点号
            $scope.lastDot = false;
        } else {
            // 当前页码在中间的位置，则前后都显示2页
            firstPage = curPage - 2;
            lastPage = curPage + 2;
        }
    } else {
        // 如果总页数小于5，则前后都不需要显示点号
        $scope.firstDot = false;
        $scope.lastDot = false;
    }
    /* 循环生成页码 */
    for (let i = firstPage; i <= lastPage; i++) {
        $scope.pageNums.push(i);
    }
};
```

- 修改search.html页面，在页码导航前后加省略号(334行)：

```html
<li class="dotted" ng-if="firstDot"><span>...</span></li>
<li class="{{ searchParam.page == page ? 'active' : ''}}"
    ng-repeat="page in pageNums">
    <a href="javascript:;" ng-click="pageSearch(page);">{{ page }}</a>
</li>
<li class="dotted" ng-if="lastDot"><span>...</span></li>
```

#### 2.3.4. 页码不可用样式

- 修改search.html页面(340行)，分别在第1页与最后1页时，上一页与下一页不可操作

```html
<ul>
	<li class="prev {{ searchParam.page == 1 ? 'disabled' : ''}}">
        <a href="javascript:;" ng-click="pageSearch(searchParam.page - 1);">«上一页</a>
	</li>
    <li class="dotted" ng-if="firstDot"><span>...</span></li>
    <li class="{{ searchParam.page == page ? 'active' : ''}}"
        ng-repeat="page in pageNums">
        <a href="javascript:;" ng-click="pageSearch(page);">{{ page }}</a>
    </li>
	<li class="dotted" ng-if="lastDot"><span>...</span></li>
	<li class="next {{ searchParam.page == resultMap.totalPages ? 'disabled' : ''}}">
        <a href="javascript:;" ng-click="pageSearch(searchParam.page + 1);">下一页»</a>
	</li>
</ul>
<div>
    <span>共{{ resultMap.totalPages }}页&nbsp;</span>
    <span>到第<input type="text" style="width: 25px;" ng-model="jumpPage">页
        <button class="page-confirm" ng-click="pageSearch(jumpPage);">确定</button>
    </span>
</div>
```

#### 2.3.5. 搜索起始页码处理

测试：如果先按照“手机”关键字进行搜索，得出的页数是5页，点击第5页进行查询；然后再根据“小米”关键字搜索，会发现没有结果显示。是因为当前页仍然为5，而小米的结果只有2页，所以无法显示。因此需要在每次点击查询时将页码设置为1。

修改search.html页面搜索按钮(82行)，调用搜索前将起始页码设置为1

```html
<button class="sui-btn btn-xlarge btn-danger" type="button"
        ng-click="searchParam.page=1;search();">搜索</button>
```

## 3. 多关键字搜索

### 3.1. 多关键字搜索规则

之前测试都是使用单一的词（比如手机）来进行搜索，如果输入的关键字是一个复合的词组（比如手机小米），那solr如何进行搜索呢？

经过测试：

> 搜索“手机”是87条记录  
> 搜索“小米”是39条记录  
> 搜索“手机小米”是96条记录

经过查看，发现结果中也包含了关键字只有手机和小米的记录，由此得出结论，solr在搜索时是将搜索关键字进行分词，然后按照或的关系来进行搜索的。

为什么不是并的关系而是或的关系呢？如果你是电商网站的运营者，肯定希望给用户更多的选择，因为如果采用并的关系来进行搜索时极有可能查询到很少的记录甚至查询不到任何记录。另外这里还有他智能的排序策略，就是按照关键字匹配度来进行排序，也就是说如果记录中同时包含了手机和小米，那么这部分数据会排列在前面显示，而只包含小米和只包含手机的记录会显示在后边。

### 3.2. 多关键字搜索空格处理

有些用户会在关键字中间习惯性的输入一些空格，而这个空格输入后，很有可能查询不到结果了。我们测试输入“手机 小米”结果并没有查询到任何结果。所以我们还要对空格至于做一下处理，删除关键字中的空格

修改pinyougou-search-service的ItemSearchServiceImpl的search()搜索方法

```java
@Override
public Map<String, Object> search(Map<String, Object> params) {
        ......
        // 判断关键字是否为空
        if (StringUtils.isNoneBlank(keywords)) {
            /* 对关键字去掉空格处理 */
            keywords = keywords.replace(" ", "");
        ......
}
```

## 4. 搜索排序

### 4.1. 按价格排序

实现价格的排序（升降序可切换）

#### 4.1.1. 价格排序-后端部分

pinyougou-search-service的ItemSearchServiceImpl的搜索方法，添加排序代码

```java
@Override
public Map<String, Object> search(Map<String, Object> params) {
        ......

        // 判断关键字是否为空
        if (StringUtils.isNoneBlank(keywords)) {
            ......
            // 商品分类过滤
            // 商品品牌过滤
            // 规格选项过滤
            // 价格区间过滤
            /* =========增加过滤查询========== */

            /* ==========增加排序========== */
            // 获取请求的正倒序 ASC DESC
            String sortValue = (String) params.get("sort");
            // 获取请求的排序字段
            String sortField = (String) params.get("sortField");

            // 按价格排序
            if (StringUtils.isNoneBlank(sortValue)
                    && StringUtils.isNoneBlank(sortField)) {
                // 创建排序对象
                Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue)
                        ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                // 添加排序查询条件
                highlightQuery.addSort(sort);
            }
            /* ==========增加排序========== */
            ......
}
```

#### 4.1.2. 价格排序-前端部分

- 修改searchController.js的查询对象searchParam，增加排序属性

```js
$scope.searchParam = {
    'keywords': '', 'category': '', 'brand': '',
    'spec': {}, 'price': '', 'page': 1, 'rows': 20,
    'sort': '', 'sortField': ''};
```

- 修改searchController.js，定义增加sortSearch()方法，实现查询增加排序条件

```js
/* 定义排序搜索方法 */
$scope.sortSearch = function (sortField, sort) {
    $scope.searchParam.sortField = sortField;
    $scope.searchParam.sort = sort;
    // 执行搜索方法
    $scope.search();
};
```

- 修改search.html页面(300行)

```html
<!-- 排序部分 -->
<div class="navbar-inner filter" ng-if="searchParam.keywords != ''">
	<ul class="sui-nav">
		<li class="{{ searchParam.sortField == '' ? 'active' : '' }}">
			<a href="javascript:;"
               ng-click="sortSearch('', '');">综合</a>
		</li>
		<li>
			<a href="#">销量</a>
		</li>
		<li>
			<a href="#">新品</a>
		</li>
		<li>
			<a href="#">评价</a>
		</li>
        <li class="{{ searchParam.sortField == 'price'
                && searchParam.sort == 'ASC' ? 'active' : '' }}">
            <a href="javascript:;"
               ng-click="sortSearch('price', 'ASC');">价格↑</a>
		</li>
        <li class="{{ searchParam.sortField == 'price'
                && searchParam.sort == 'DESC' ? 'active' : '' }}">
            <a href="javascript:;"
               ng-click="sortSearch('price', 'DESC');">价格↓</a>
		</li>
	</ul>
</div>
```

### 4.2. 按上架时间排序

修改search.html页面(296行)，给“新品”超链接绑定点击事件

```html
<li class="{{ searchParam.sortField == 'updateTime' ? 'active' : '' }}">
	<a href="javascript:;"
       ng-click="sortSearch('updateTime', 'DESC');">新品</a>
</li>
```

### 4.3. (!待完成)按销量排序（实现思路）

1. 增加域item_salecount 用于存储每个SKU的销量数据。
2. 编写定时器程序，用于更新每个SKU的销量数据（查询近1个月的销量数据，不是累计数据）。
3. 定时器每天只需执行一次，可以设定为凌晨开始执行。

### 4.4. (!待完成)按评价排序（实现思路）

与按销量排序思路基本相同，有一个细节需要注意：

评论分为好评、中评、差评，我们不能简单地将评论数相加，而是应该根据每种评论加权进行统计。比如好评的权重是3 ，中评的权重是1，而差评的权重是 -3，这样得出的是评价的综合得分。

## 5. 隐藏品牌列表

### 5.1. 需求分析

需求：如果用户输入的是品牌的关键字，则隐藏品牌列表

### 5.2. 修改搜索系统前端部分

- 修改searchController.js，增加判断关键字是否包含品牌

```js
/* 判断关键字中是否为品牌 */
$scope.keywordsIsBrand = function () {
    // 迭代品牌列表
    for (let i = 0; i < $scope.resultMap.brandList.length; i++) {
        var brandText = $scope.resultMap.brandList[i].text;
        // 判断关键字中是否为品牌
        if ($scope.searchParam.keywords.indexOf(brandText) >= 0) {
            return true;
        }
    }
    return false;
};
```

- 修改search.html页面(185行)

```html
<!-- 判断显示搜索关键字所包含的品牌 -->
<div class="type-wrap logo"
     ng-if="resultMap.brandList != null && searchParam.brand == '' && !keywordsIsBrand()">
	<div class="fl key brand">品牌</div>
	......
</div>
```

## 6. 搜索页与首页对接

### 6.1. 需求分析

用户在首页的搜索框输入关键字，点击搜索后自动跳转到搜索页查询

### 6.2. 首页与搜索页传递搜索关键字

#### 6.2.1. 首页传入关键字

- 修改pinyougou-portal-web工程的contentController.js增加搜索跳转的方法search()

```js
/* 跳转到搜索系统的方法 */
$scope.search = function () {
    var keyword = $scope.keywords ? $scope.keywords : "";
    location.href = 'http://search.moon.com?keywords=' + keyword;
};
```

- 修改pinyougou-portal-web工程的index.html(72行)；给搜索按钮绑定点击事件，绑定搜索并跳转搜索系统

```html
<!--searchAutoComplete-->
<div class="input-append">
	<input type="text" id="autocomplete" type="text"
           ng-model="keywords"
           class="input-error input-xxlarge" />
	<button class="sui-btn btn-xlarge btn-danger" type="button"
            ng-click="search();">搜索</button>
</div>
```

#### 6.2.2. 搜索页接收关键字

- 修改pinyougou-search-web的searchController.js，添加$location服务用于接收首页传递的参数

```js
/* 定义搜索控制器 */
app.controller('searchController', function ($scope, $location, baseService) {
    ......
}
```

- searchController定义getkeywords()方法，接收首页传递的参数并进行查询

```js
/* 获取首页传过来的搜索关键字 */
$scope.getkeywords = function () {
    $scope.searchParam.keywords = $location.search().keywords;
    // 执行搜索方法
    $scope.search();
};
```

- 修改search.html进行初始化

```html
<!-- 加入base标签  -->
<base href="/"/>
<script type="text/javascript">
    /* 配置位置提供者 */
    app.config(function ($locationProvider) {
        $locationProvider.html5Mode(true);
    });
</script>

<!-- 导入控制器，并初始化搜索方法 -->
<body ng-app="pinyougou" ng-controller="searchController"
      ng-init="getkeywords();">
```

## 7. 更新索引库

### 7.1. 需求分析

在进行商品审核后更新到solr索引库,在商品删除后删除solr索引库中相应的记录.

### 7.2. 查询审核商品（SKU）列表-服务接口层与实现层

- 修改pinyougou-sellergoods服务工程的GoodsService接口与GoodsServiceImpl实现类，新增根据SPU商品id查询所有SKU商品数据方法

```java
/**
 * 根据SPU商品id与状态查询SKU具体商品数据
 *
 * @param ids    SPU商品ids
 * @param status 状态码
 * @return SKU具体商品数据
 */
List<Item> findItemByGoodsIdAndStatus(Long[] ids, String status);

@Override
public List<Item> findItemByGoodsIdAndStatus(Long[] ids, String status) {
    try {
        // 创建示范对象
        Example example = new Example(Item.class);
        // 创建查询条件对象
        Example.Criteria criteria = example.createCriteria();
        // 查询条件1：goods_id in (x,x,...)
        criteria.andIn("goodsId", Arrays.asList(ids));
        // 查询条件2：status = 1 状态为正常
        criteria.andEqualTo("status", status);

        // 返回查询结果
        return itemMapper.selectByExample(example);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
```

### 7.3. 商品审批时更新到索引库

#### 7.3.1. 搜索服务pinyougou-search工程接口层与实现层

修改pinyougou-search服务工程的ItemSearchService接口与ItemSearchServiceImpl现实类，增加添加或修改索引库的方法

```java
/**
 * 添加或修改索引库
 *
 * @param solrItemList solr索引库封装实体集合
 */
void saveOrUpdate(List<SolrItem> solrItemList);

@Override
public void saveOrUpdate(List<SolrItem> solrItemList) {
    // 批量添加或修改
    UpdateResponse response = solrTemplate.saveBeans(solrItemList);

    // 判断状态码
    if (response.getStatus() == 0) {
        solrTemplate.commit();
    } else {
        solrTemplate.rollback();
    }
}
```

#### 7.3.2. 运营商后台pinyougou-manager-web控制层

- 修改pinyougou-manager-web的pom.xml配置文件，引入依赖pinyougou-search-interface工程

```xml
<!-- 依赖pinyougou-search-interface模块 -->
<dependency>
    <groupId>com.moon</groupId>
    <artifactId>pinyougou-search-interface</artifactId>
    <version>${project.version}</version>
</dependency>
```

- 修改pinyougou-manager-web工程的GoodsController原有审核方法updateStatus()，增加审核后更新索引库部分

```java
/* 注入搜索服务 */
@Reference(timeout = 30000)
private ItemSearchService itemSearchService;

@GetMapping("/updateStatus")
public boolean updateStatus(@RequestParam("ids") Long[] ids,
                            @RequestParam("status") String status) {
    try {
        // 调用服务层批量修改方法
        goodsService.updateStatus(ids, status);

        // 判断状态码，是否审核通过
        if ("1".equals(status)) {
            // 根据SPU商品的id与审核状态查询SKU具体商品的数据
            List<Item> itemList = goodsService.findItemByGoodsIdAndStatus(ids, status);

            // 判断查询结果
            if (itemList != null && itemList.size() > 0) {
                List<SolrItem> solrItems = new ArrayList<SolrItem>();

                // 遍历sku商品集合，循环把List<Item> 转化成 List<SolrItem>
                for (Item item : itemList) {
                    SolrItem solrItem = new SolrItem();
                    // 使用spring框架提供的BeanUtils工具类，复制实体对象
                    BeanUtils.copyProperties(item, solrItem);
                    /*
                     * 把spec规格选项的json字符串转化成Map集合
                     *      {"网络":"联通4G","机身内存":"64G"}
                     */
                    Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                    // 设置规格选项动态域数据到solrItem中
                    solrItem.setSpecMap(specMap);
                    // 将封装索引库对象添加到集合中
                    solrItems.add(solrItem);
                }

                // 同步更新SKU具体商品数据到Solr索引库
                itemSearchService.saveOrUpdate(solrItems);
            }
        }
        return true;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
```

### 7.4. 商品删除同步索引数据

#### 7.4.1. 搜索服务pinyougou-search工程接口层与实现层

修改pinyougou-search服务工程ItemSearchService接口层与ItemSearchServiceImpl实现层，增加删除索引库指定商品的方法

```java
/**
 * 批量删除索引库中索引
 *
 * @param ids 指定sku商品的id集合
 */
void delete(List<Long> ids);

@Override
public void delete(List<Long> ids) {
    System.out.println("删除的SPU商品ID：" + ids);
    // 创建solr查询对象
    Query query = new SimpleQuery();
    // 创建条件对象
    Criteria criteria = new Criteria("goodsId").in(ids);
    // 添加条件
    query.addCriteria(criteria);

    // 根据查询条件删除指定的索引数据
    UpdateResponse response = solrTemplate.delete(query);

    // 判断状态码
    if (response.getStatus() == 0) {
        solrTemplate.commit();
    } else {
        solrTemplate.rollback();
    }
}
```

#### 7.4.2. 运营商后台pinyougou-manager-web控制层

修改pinyougou-manager-web的GoodsController原有deleteGoods()方法，增加删除索引库部分逻辑代码

```java
@GetMapping("/delete")
public boolean deleteGoods(@RequestParam("ids") Long[] ids) {
    try {
        // 1. 调用服务层批量删除方法
        goodsService.deleteGoods(ids);
        // 2. 调用搜索服务，删除索引库数据
        itemSearchService.delete(Arrays.asList(ids));
        return true;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
```
