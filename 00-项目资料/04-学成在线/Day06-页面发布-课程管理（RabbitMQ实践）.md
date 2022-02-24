# Day06 页面发布-课程管理（RabbitMQ实践）

## 1. 页面发布
### 1.1. 技术方案

- 本项目使用MQ实现页面发布的技术方案如下

    ![页面发布方案](images/20190529162042908_1987.png)

- 技术方案说明
    1. 平台包括多个站点，页面归属不同的站点。
    2. 发布一个页面应将该页面发布到所属站点的服务器上。
    3. 每个站点服务部署cms client程序，并与交换机绑定，绑定时指定站点Id为routingKey。指定站点id为routingKey就可以实现cms client只能接收到所属站点的页面发布消息。
    4. 页面发布程序向MQ发布消息时指定页面所属站点Id为routingKey，将该页面发布到它所在服务器上的cms client
- 路由模式分析如下
    - 发布一个页面，需发布到该页面所属的每个站点服务器，其它站点服务器不发布。
    - 比如：发布一个门户的页面，需要发布到每个门户服务器上，而用户中心服务器则不需要发布。
    - 所以本项目采用routing模式，用站点id作为routingKey，这样就可以匹配页面只发布到所属的站点服务器上。
- 页面发布流程图如下

    ![页面发布流程图](images/20190529163258985_1144.png)

    1. 前端请求cms执行页面发布。
    2. cms执行静态化程序生成html文件。
    3. cms将html文件存储到GridFS中。
    4. cms向MQ发送页面发布消息
    5. MQ将页面发布消息通知给Cms Client
    6. Cms Client从GridFS中下载html文件
    7. Cms Client将html保存到所在服务器指定目录

### 1.2. 页面发布消费方
#### 1.2.1. 需求分析

- 功能分析
    - 创建Cms Client工程作为页面发布消费方，将Cms Client部署在多个服务器上，它负责接收到页面发布的消息后从GridFS中下载文件在本地保存
- 需求分析
    1. 将Cms Client部署在服务器，配置队列名称和站点ID
    2. Cms Client连接RabbitMQ并监听各自的“页面发布队列”
    3. Cms Client接收页面发布队列的消息
    4. 根据消息中的页面id从mongodb数据库下载页面到本地
        - 调用dao查询页面信息，获取到页面的物理路径，调用dao查询站点信息，得到站点的物理路径
        - 页面物理路径 = 站点物理路径 + 页面物理路径 + 页面名称
        - 从GridFS查询静态文件内容，将静态文件内容保存到页面物理路径下

#### 1.2.2. 创建 Cms Client 工程

1. 创建maven工程，修改pom.xml文件，添加相关依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>xc-framework-parent</artifactId>
        <groupId>com.xuecheng</groupId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../xc-framework-parent/pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>xc-service-manage-cms-client</artifactId>
    <name>xc-service-manage-cms-client</name>

    <dependencies>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc-framework-model</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
        </dependency>
    </dependencies>
</project>
```

2. 配置文件。在resources下配置application.yml和logback-spring.xml，配置application.yml内容如下

```yml
server:
  port: 31000
spring:
  application:
    name: xc-service-manage-cms-client
  data:
    mongodb:
      uri: mongodb://root:123@localhost:27017
      database: xc_cms
  rabbitmq:
    host: 192.168.12.132
    port: 5672
    username: guest
    password: guest
    virtualHost: /
xuecheng:
  mq:
    queue: queue_cms_postpage_01  # cms客户端监控的队列名称（不同的客户端监控的队列不能重复）
    routingKey: 5a751fab6abb5044e0d19ea1    # 此routingKey为门户站点ID
```

**说明：在配置文件中配置队列的名称，每个 cms client 在部署时注意队列名称不要重复**

3. 创建启动类

```java
/**
 * CMS Client 内容管理服务客户端工程启动类
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms") // 扫描实体类所在包
@ComponentScan(basePackages = {"com.xuecheng.framework"}) // 扫描接口，common工程所在包
@ComponentScan(basePackages = {"com.xuecheng.manage_cms_client"}) // 扫描本项目下的所有类（其实不写好像也是可以扫描的）
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
```

#### 1.2.3. RabbitmqConfig 配置类

- 消息队列设置如下：
    1. 创建『ex_cms_postpage』交换机
    2. 每个Cms Client创建一个队列与交换机绑定
    3. 每个Cms Client程序配置队列名称和routingKey，将站点ID作为routingKey。

```java
/**
 * CMS Client RabbitMQ消费端配置类
 */
@Configuration
public class RabbitmqConfig {

    // 定义队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    // 定义监听的交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    // 读取配置文件中消息队列的名称（部署到不同的服务器时，修改配置文件即可监听不同的消息队列）
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;
    //读取配置文件中消息队列的routingKey 即站点Id（部署到不同的服务器时，修改配置文件即可监听不同的消息队列）
    @Value("${xuecheng.mq.routingKey}")
    public String routingKey;

    /* 声明交换机，配置使用direct类型 */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    /* 声明队列 */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE() {
        return new Queue(queue_cms_postpage_name);
    }

    /**
     * 绑定队列到交换机
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
                                            @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
}
```

#### 1.2.4. 定义消息格式

消息内容采用json格式存储数据，如：`{页面id：发布页面的id}`

```json
{
    "pageId": ""
}
```

#### 1.2.5. MongoConfig 配置类

```java
/**
 * MongoDB 配置类
 */
@Configuration
public class MongoConfig {
    /* 读取配置文件的数据库名称 */
    @Value("${spring.data.mongodb.database}")
    private String db;

    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(db);
        return GridFSBuckets.create(database);
    }
}
```

#### 1.2.6. PageDao 接口层

1. 使用 CmsPageRepository 查询页面信息

```java
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
}
```

2. 使用 CmsSiteRepository 查询站点信息，主要获取站点物理路径

```java
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
}
```

#### 1.2.7. pageService 业务层

在Service中定义保存页面静态文件到服务器物理路径方法

```java
/**
 * 页面服务 业务逻辑层
 */
@Service
public class PageService {

    /* 记录日志 */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    /* 注入相关的dao层接口 */
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /* 注入MongoDB持久化文件操作对象 */
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 保存html页面到服务器物理路径
     *
     * @param pageId 页面id
     */
    public void savePageToServerPath(String pageId) throws CustomException {
        // 1. 根据pageId查询cmsPage
        CmsPage cmsPage = findCmsPageById(pageId);
        // 上面方法已经进行判空，直接获取静态化html文件的htmlFileId
        String htmlFileId = cmsPage.getHtmlFileId();

        // 2. 根据id从gridFS中查询html文件
        InputStream inputStream = getFileById(htmlFileId);
        if (inputStream == null) {
            // 记录日志
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}", htmlFileId);
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        // 3. 获取页面的物理路径【页面物理路径 = 站点物理路径 + 页面物理路径 + 页面名称】
        // 获取站点id
        String siteId = cmsPage.getSiteId();
        // 查询数据库，获取页面所属的站点信息
        CmsSite cmsSite = findCmsSiteById(siteId);
        // 拼接页面物理路径
        String pagePath = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

        // 4. 将html文件保存到服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            // 将文件内容保存到服务物理路径
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 根据站点id查询站点信息
     *
     * @param siteId 站点id
     * @return CmsSite
     */
    private CmsSite findCmsSiteById(String siteId) throws CustomException {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);

        if (optional.isPresent()) {
            return optional.get();
        }
        // 查询无记录，抛出业务异常
        ExceptionCast.cast(CmsCode.CMS_SITE_NOTEXISTS);
        return null;
    }

    /**
     * 根据文件id从GridFS中查询文件内容
     *
     * @param fileId 文件存储id
     * @return 文件的输入流
     */
    private InputStream getFileById(String fileId) {
        // 从GridFS中取模板文件内容，根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        // 打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        // 创建GridFsResource对象，用于获取输入流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据页面id查询页面信息
     *
     * @param pageId 页面id
     * @return CmsPage
     */
    private CmsPage findCmsPageById(String pageId) throws CustomException {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            return optional.get();
        }
        // 查询无记录，抛出业务异常
        ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        return null;
    }
}
```

#### 1.2.8. ConsumerPostPage 监听消息队列类

在cms client工程的mq包下创建ConsumerPostPage类，ConsumerPostPage作为发布页面的消费客户端，监听页面发布队列的消息，收到消息后从mongodb下载文件，保存在本地

```java
/**
 * 监听RabbitMQ，接收页面发布消息
 */
@Component
public class ConsumerPostPage {

    /* 记录日志 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    /* 注入页面服务层 */
    @Autowired
    private PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        // 解析消息字符串
        Map map = JSON.parseObject(msg, Map.class);
        // 输入日志
        LOGGER.info("receive cms post page:{}", map.toString());

        // 从消息体中获取页面id
        String pageId = (String) map.get("pageId");

        // 校验页面id是否合法已经在保存页面到指定的路径的方法中已经做校验，所以直接调用
        try {
            // 调用service方法将页面从GridFs中下载到服务器
            pageService.savePageToServerPath(pageId);
        } catch (CustomException e) {
            e.printStackTrace();
            LOGGER.error("generate page has error:{}", e.getMessage());
        }
    }
}
```

### 1.3. 页面发布生产方
#### 1.3.1. 需求分析

- 管理员通过cms系统发布“页面发布”的按钮，发送请求，cms系统作为页面发布的生产方。需求如下：
    1. 管理员进入管理界面点击“页面发布”，前端请求cms页面发布接口。
    2. cms页面发布接口执行页面静态化，并将静态化页面存储至GridFS中。
    3. 静态化成功后，向消息队列发送页面发布的消息。
        1. 获取页面的信息及页面所属站点ID。
        2. 设置消息内容为页面ID。（采用json格式，方便日后扩展）
        3. 发送消息给ex_cms_postpage交换机，并将站点ID作为routingKey。

#### 1.3.2. RabbitMQ 配置

1. 修改xc-service-manage-cms工程的application.yml配置文件，添加Rabbitmq的连接参数配置

```yml
spring:
  ......
  rabbitmq:
    host: 192.168.12.132
    port: 5672
    username: guest
    password: guest
    virtualHost: /
```

2. 在pom.xml添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

3. 创建RabbitMQConfig配置类

```java
/**
 * CMS 服务 RabbitMQ生产端配置类
 */
@Configuration
public class RabbitmqConfig {

    // 定义消息发送的交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    /**
     * 声明交换机，配置使用direct类型
     *
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
}
```

#### 1.3.3. Api 接口

在api工程CmsPageControllerApi类中定义页面发布接口

```java
 /* 页面发布 */
@ApiOperation("页面发布")
@ApiImplicitParams({
        @ApiImplicitParam(name = "pageId", value = "页面ID", required = true, dataType = "String"),
})
public ResponseResult post(String pageId);
```

#### 1.3.4. PageService 业务层

在PageService中定义页面发布方法

```java
/* 注入RabbitMQ操作对象 */
@Autowired
private RabbitTemplate rabbitTemplate;

/**
 * 页面发布
 *
 * @param pageId 页面id
 * @return 处理结果
 */
public ResponseResult postPage(String pageId) {
    // 1. 调用方法，执行生成页面静态化
    String pageHtml = this.getPageHtml(pageId);
    if (StringUtils.isBlank(pageHtml)) {
        ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
    }

    // 先根据id查询页面信息
    CmsPage cmsPage = this.getById(pageId);
    if (cmsPage == null) {
        ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
    }

    // 2. 将页面静态化文件存储到GridFs中
    this.saveHtml(cmsPage, pageHtml);

    // 3. 向消息队列发送消息
    sendPostPage(cmsPage);

    return new ResponseResult(CommonCode.SUCCESS);
}

/**
 * 发送队列消息
 *
 * @param cmsPage 更新生成新的静态页面后的页面信息对象
 */
private void sendPostPage(CmsPage cmsPage) {
    // 创建消息对象
    Map<String, String> msg = new HashMap<>();
    msg.put("pageId", cmsPage.getPageId());
    // 转成json字符串
    String jsonString = JSON.toJSONString(msg);

    // 发送消息，用站点ID作为routingKey
    String siteId = cmsPage.getSiteId();
    rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, jsonString);
}

/**
 * 保存html文件到GridFS
 *
 * @param cmsPage     页面信息
 * @param htmlContent 生成的静态化页面字符串
 */
private void saveHtml(CmsPage cmsPage, String htmlContent) {
    // 存储新生成的页面之前先删除之前旧的静态页面
    String htmlFileId = cmsPage.getHtmlFileId();
    if (StringUtils.isNoneBlank(htmlFileId)) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
    }

    InputStream inputStream = null;
    try {
        // 将htmlContent内容转成输入流
        inputStream = IOUtils.toInputStream(htmlContent, "UTF-8");
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 将html文件内容保存到GridFS
    ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
    // 将保存html文件后返回的id更新到cmsPage中
    cmsPage.setHtmlFileId(objectId.toHexString());
    cmsPageRepository.save(cmsPage);
}
```

#### 1.3.5. CmsPageController 控制层

编写Controller实现api接口，接收页面请求，调用service执行页面发布

```java
/**
 * 发布页面
 *
 * @param pageId 页面id
 * @return 响应结果
 */
@Override
@PostMapping("/postPage/{pageId}")
public ResponseResult post(@PathVariable("pageId") String pageId) {
    return pageService.postPage(pageId);
}
```

### 1.4. 页面发布前端

- 用户操作流程：
    1. 用户进入cms页面列表。
    2. 点击“发布”请求服务端接口，发布页面。
    3. 提示“发布成功”，或发布失败。

#### 1.4.1. API方法

在 src\module\cms\api\cms.js 前端添加 api 方法。

```js
/* 发布页面 */
export const page_postPage = id => {
  return http.requestPost(apiUrl + '/cms/page/postPage/' + id)
}
```

#### 1.4.2. 页面

- 修改page_list.vue，添加发布按钮

```html
<el-table-column label="发布" width="70">
    <template slot-scope="page">
        <el-button size="small" type="primary" @click="postPage(page.row.pageId)">发布</el-button>
    </template>
</el-table-column>
```

- 添加页面发布点击事件

```js
// 发布页面
postPage(pageId) {
    this.$confirm('确认发布该页面吗?', '提示', {}).then(() => {
        // 调用服务端接口
        cmsApi.page_postPage(pageId).then(res => {
            if (res.success) {
                console.log('发布页面id:' + pageId)
                this.$message.success("发布成功，请稍后查看结果")
            } else {
                this.$message.error("发布失败")
            }
        })
    }).catch(() => {
        console.log('发生异常')
    })
}
```

### 1.5. 测试

这里测试轮播图页面修改、发布的流程：

1. 修改轮播图页面模板或修改轮播图地址
    - 注意：先修改页面原型，页面原型调试正常后再修改页面模板。
2. 执行页面预览
3. 执行页面发布，查看页面是否写到网站目录
4. 刷新门户首页并观察轮播图是否变化。

### 1.6. 问题思考

1. 如果发布到服务器的页面内容不正确怎么办？
2. 一个页面需要发布很多服务器，点击“发布”后如何知道详细的发布结果？
3. 一个页面发布到多个服务器，其中有一个服务器发布失败时怎么办？

## 2. 课程管理
### 2.1. 需求分析

在线教育平台的课程信息相当于电商平台的商品。课程管理是后台管理功能中最重要的模块。本项目为教学机构提供课程管理功能，教学机构可以添加属于自己的课程，供学生在线学习。

- 课程管理包括如下功能需求：
    1. 分类管理
    2. 新增课程
    3. 修改课程
    4. 预览课程
    5. 发布课程
- 用户的操作流程如下：
    1. 进入我的课程

    ![我的课程操作流程1](images/20190602141254057_14366.png)

    2. 点击“添加课程”，进入添加课程界面

    ![我的课程操作流程2](images/20190602141309811_11563.png)

    3. 输入课程基本信息，点击提交
    4. 课程基本信息提交成功，自动进入“管理课程”界面，点击“管理课程”也可以进入“管理课程”界面

    ![我的课程操作流程3](images/20190602141322218_5895.png)

    5. 编辑图片。上传课程图片。

    ![我的课程操作流程4](images/20190602141330624_10333.png)

    6. 编辑课程营销信息，营销信息主要是设置课程的收费方式及价格

    ![我的课程操作流程5](images/20190602141341744_18891.png)

    7. 编辑课程计划。添加课程计划

    ![我的课程操作流程6](images/20190602141410506_24227.png)

    ![我的课程操作流程7](images/20190602141417895_2150.png)

### 2.2. 此部分的实践

本模块对课程信息管理功能的采用实战方法，旨在通过实战提高接口编写的能力，具体方法如下：

1. 前后端工程导入
    - 教学管理前端工程采用与系统管理工程相同的技术，直接导入后在此基础上开发。
    - 课程管理服务端工程采用Spring Boot技术构建，技术层技术使用Spring data Jpa（与Spring data Mongodb类似）、Mybatis，直接导入后在此基础上开发。
2. 课程计划功能
    - 课程计划功能采用全程实践。
3. 我的课程、新增课程、修改课程、课程营销
    - 我的课程、新增课程、修改课程、课程营销四个功能采用实战方式，会记录每个功能的需求及技术点
4. 参考文档
    - 实战结束提供每个功能的开发文档，参考文档并修正功能缺陷

### 2.3. 环境搭建
#### 2.3.1. 搭建数据库环境

1. 创建数据库
    - 课程管理使用MySQL数据库，创建课程管理数据库：xc_course
    - 导入课程资料的xc_course.sql脚本
2. 数据表介绍，参考课堂笔记，《2.3.1 搭建数据库环境 2. 数据表介绍》

#### 2.3.2. 导入课程管理服务工程

1. 课程管理服务使用MySQL数据库存储课程信息，持久层技术介绍如下
    1. spring data jpa：用于表的基本CRUD
    2. mybatis：用于复杂的查询操作
    3. druid：使用阿里巴巴提供的 spring boot 整合 druid 包 druid-spring-boot-starter 管理连接池。druid-spring-boot-starter地址：https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter
2. 导入工程。导入资料下的 “xc-service-manage-course.zip”

#### 2.3.3. 导入课程管理前端工程

课程管理属于**教学管理子系统**的功能，使用用户为教学机构的管理人员和老师，为保证系统的可维护性，单独创建一个教学管理前端工程。 教学管理前端工程与系统管理前端的工程结构一样，也采用vue.js框架来实现。

从课程资料目录拷贝xc-ui-pc-teach.zip到工程，运行项目

```shell
# 如何运行时出现以下的错误，是因为node版本/node版本太高会造成node-sass不兼容的问题，那么就再install node-sass一下就行了。
# Module build failed: Error: Node Sass does not yet support your current environment: Windows 64-bit with Unsupported runtime (64)
For more information on which environments are supported please see:

npm i node-sass -D
```

## 3. 课程计划
### 3.1. 需求分析

- 什么是课程计划？
    - 课程计划定义了课程的章节内容，学生通过课程计划进行在线学习，下图中右侧显示的就是课程计划。
    - 课程计划包括两级，第一级是课程的大章节、第二级是大章节下属的小章节，每个小章节通常是一段视频，学生点击小章节在线学习。
- 教学管理人员对课程计划如何管理？
    - 功能包括：添加课程计划、删除课程计划、修改课程计划等

### 3.2. 课程计划查询
#### 3.2.1. 需求分析

- 课程计划查询是将某个课程的课程计划内容完整的显示出来

![课程显示效果](images/20190609225510974_26853.png)

- 左侧显示的就是课程计划，课程计划是一个树型结构，方便扩展课程计划的级别。
- 在上边页面中，点击“添加课程计划”即可对课程计划进行添加操作。
- 点击修改可对某个章节内容进行修改。
- 点击删除可删除某个章节。

#### 3.2.2. 页面原型
##### 3.2.2.1. tree组件介绍

本功能使用element-ui的tree组件来完成，详细使用参考安官网文档：https://element.eleme.cn/#/zh-CN/component/tree

##### 3.2.2.2. webstorm配置JSX

- element-ui的tree组件用到了JSX语法
- JSX 是Javascript和XML结合的一种格式，它是React的核心组成部分，JSX和XML语法类似，可以定义属性以及子元素。唯一特殊的是可以用大括号来加入JavaScript表达式。遇到 HTML 标签（以 `<` 开头），就用 HTML 规则解析；遇到代码块（以 `{` 开头），就用 JavaScript 规则解析。

设置方法如下

1. Javascript version 选择 React JSX （如果没有就选择JSX Harmony）

![webstorm配置JSX1](images/20190602230037864_6707.png)

2. HTML 类型文件中增加vue

![webstorm配置JSX2](images/20190602230029880_22576.png)

3. preferences --> Editor --> File Types 中找到上边框中HTML 在下边加一个 *.vue。如果已经在 vue template 中已存在.vue 则把它改为.vue2(因为要在Html中添加.vue)

![webstorm配置JSX3](images/20190602230022504_6045.png)

#### 3.2.3. API接口
##### 3.2.3.1. 数据模型

课程计划为树型结构，由树根（课程）和树枝（章节）组成，为了保证系统的可扩展性，在系统设计时将课程计划设置为树型结构。

```java
@Data
@ToString
@Entity
@Table(name = "teachplan")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Teachplan implements Serializable {
    private static final long serialVersionUID = -916357110051689485L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String pname;
    private String parentid;
    private String grade;
    private String ptype;
    private String description;
    private String courseid;
    private String status;
    private Integer orderby;
    private Double timelength;
    private String trylearn;
}
```

##### 3.2.3.2. 自定义模型类

- 前端页面需要树型结构的数据来展示Tree组件，如下：

```json
[{
  id: 1,
  label: '一级 1',
  children: [{
    id: 4,
    label: '二级 1-1',
    children: [{
      id: 9,
      label: '三级 1-1-1'
    }, {
      id: 10,
      label: '三级 1-1-2'
    }]
  }]
},
...
]
```

- 自定义课程计划结点类如下：

```java
@Data
@ToString
public class TeachplanNode extends Teachplan {
    List<TeachplanNode> children;
}
```

##### 3.2.3.3. 接口定义

根据课程id查询课程的计划接口如下，在api工程创建course包，创建CourseControllerApi接口类并定义接口方法

```java
/**
 * 课程管理接口
 */
@Api(value = "课程管理接口", description = "课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);
}
```

#### 3.2.4. 课程管理服务
##### 3.2.4.1. 查询的sql语句

课程计划是树型结构，采用表的自连接方式进行查询，sql语句如下

```sql
SELECT
	a.id one_id,
	a.pname one_pname,
	b.id two_id,
	b.pname two_pname,
	c.id three_id,
	c.pname three_pname
FROM
	teachplan a
	LEFT JOIN teachplan b ON a.id = b.parentid
	LEFT JOIN teachplan c ON b.id = c.parentid
WHERE
	a.parentid = '0'
	AND a.courseid = '402885816243d2dd016243f24c030002'
ORDER BY
	a.orderby,
	b.orderby,
	c.orderby
```

##### 3.2.4.2. dao层

1. 创建TeachplanMapper接口

```java
/**
 * 课程计划mapper接口
 */
// @Mapper标记该类是一个mybatis的mapper接口，可以被spring boot自动扫描到spring上下文中
@Mapper
public interface TeachplanMapper {
    /**
     * 课程计划查询
     *
     * @param courseId
     * @return
     */
    public TeachplanNode selectList(String courseId);
}
```

2. 在src/main/resources目录下创建com/xuecheng/manage_course/dao/TeachplanMapper.xml映射文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.xuecheng.manage_course.dao.TeachplanMapper">

    <!-- 定义课程计划树型结构的映射关系 -->
    <resultMap id="teachplanMap" type="com.xuecheng.framework.domain.course.ext.TeachplanNode">
        <id column="one_id" property="id"/>
        <result column="one_pname" property="pname"/>
        <!-- 定义二级节点集合 -->
        <collection property="children" ofType="com.xuecheng.framework.domain.course.ext.TeachplanNode">
            <id column="two_id" property="id"/>
            <result column="two_pname" property="pname"/>
            <!-- 定义三级节点集合 -->
            <collection property="children" ofType="com.xuecheng.framework.domain.course.ext.TeachplanNode">
                <id column="three_id" property="id"/>
                <result column="three_pname" property="pname"/>
            </collection>
        </collection>
    </resultMap>

    <select id="selectList" resultMap="teachplanMap" parameterType="java.lang.String">
        SELECT
            a.id one_id,
            a.pname one_pname,
            b.id two_id,
            b.pname two_pname,
            c.id three_id,
            c.pname three_pname
        FROM
            teachplan a
            LEFT JOIN teachplan b ON a.id = b.parentid
            LEFT JOIN teachplan c ON b.id = c.parentid
        WHERE
            a.parentid = '0'
            <if test="_parameter != null and _parameter != ''">
                AND a.courseid = #{courseId}
            </if>
        ORDER BY
            a.orderby,
            b.orderby,
            c.orderby
    </select>
</mapper>
```

**说明：针对输入参数为简单类型`#{}`中可以是任意类型，判断参数是否为空要用`_parameter`（它属于mybatis的内置参数）**

##### 3.2.4.3. service层

创建CourseService类，定义查询课程计划方法

```java
/**
 * 课程管理服务层
 */
@Service
public class CourseService {
    /* 注入课程计划mapper接口 */
    @Autowired
    private TeachplanMapper teachplanMapper;

    /**
     * 查询课程计划
     *
     * @param courseId 课程id
     * @return 课程计划节点
     */
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }
}
```

##### 3.2.4.4. controller层

```java
/**
 * 课程管理控制层
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    /* 注入课程服务层 */
    @Autowired
    private CourseService courseService;

    /**
     * 课程计划查询
     *
     * @param courseId 课程id
     * @return 课程树形节点数据
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }
}
```

##### 3.2.4.5. 测试

- 使用postman或swagger-ui测试查询接口
- Get 请求：http://localhost:31200/course/teachplan/list/402885816243d2dd016243f24c030002

#### 3.2.5. 前端页面
##### 3.2.5.1. Api方法

修改\src\module\course\api\course.js，定义课程计划查询的api方法

```js
/* 查询课程计划 */
export const findTeachplanList = courseid => {
  return http.requestQuickGet(apiUrl + '/course/teachplan/list/' + courseid)
}
```

##### 3.2.5.2. Api调用

1. 修改src\module\course\page\course_manage\course_plan.vue，定义查询课程计划的方法，赋值给数据对象teachplanList

```js
findTeachplan() {
    // 先清空树形数据
    this.teachplanList = []
    // 查询课程计划
    courseApi.findTeachplanList(this.courseid).then(res => {
        if (res && res.children) {
            this.teachplanList = res.children;
        }
    })
}
```

2. 在mounted钩子中查询课程计划

```js
mounted() {
    // 课程id
    this.courseid = this.$route.params.courseid;
    // 查询课程计划
    this.findTeachplan()
}
```

3. 修改树结点的标签属性。课程计划信息中pname为结点的名称，需要修改树结点的标签属性方可正常显示课程计划名称，如下

```js
data() {
    return {
        ......
        teachplanList: [],
        defaultProps: {
            children: 'children',
            label: 'pname'
        },
        ......
    }
},
```

##### 3.2.5.3. 测试

将course_list.vue的页面中测试数据改成id:'297e7c7c62b888f00162b8a7dec20000'，跳转可以查询数据

### 3.3. 添加课程计划
#### 3.3.1. 需求分析

- 用户操作流程：
1. 进入课程计划页面，点击“添加课程计划”
2. 打开添加课程计划页面，输入课程计划信息

![添加课程计划](images/20190705184412103_11590.png)

- 上级结点说明：
    - 不选择上级结点表示当前添加的课程计划的父结点为课程的根结点
    - 当添加该课程时，在课程计划中还没有节点时候要自动添加该课程的根结点
3. 点击提交

#### 3.3.2. 页面原型说明

1. 添加课程计划采用弹出窗口组件Dialog

```html
<el-dialog title="添加课程计划" :visible.sync="teachplayFormVisible">
    <el-form
        ref="teachplanForm"
        :model="teachplanActive"
        label-width="140px"
        style="width:600px;"
        :rules="teachplanRules"
    >
        <el-form-item label="上级结点">
            <el-select v-model="teachplanActive.parentid" placeholder="不填表示根结点">
                <el-option
                    v-for="item in teachplanList"
                    :key="item.id"
                    :label="item.pname"
                    :value="item.id"
                ></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="章节/课时名称" prop="pname">
            <el-input v-model="teachplanActive.pname" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="课程类型">
            <el-radio-group v-model="teachplanActive.ptype">
                <el-radio class="radio" label="1">视频</el-radio>
                <el-radio class="radio" label="2">文档</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="学习时长（分钟）  请输入数字">
            <el-input
                type="number"
                v-model="teachplanActive.timelength"
                auto-complete="off"
            ></el-input>
        </el-form-item>
        <el-form-item label="排序字段">
            <el-input v-model="teachplanActive.orderby" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="章节/课时介绍" prop="description">
            <el-input type="textarea" v-model="teachplanActive.description"></el-input>
        </el-form-item>

        <el-form-item label="状态" prop="status">
            <el-radio-group v-model="teachplanActive.status">
                <el-radio class="radio" label="0">未发布</el-radio>
                <el-radio class="radio" label="1">已发布</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" v-on:click="addTeachplan">提交</el-button>
            <el-button type="primary" v-on:click="resetForm">重置</el-button>
        </el-form-item>
    </el-form>
</el-dialog>
```

2. 设置数据模型，在定义新增操作的相关变量

```js
data() {
    return {
        ......
        teachplayFormVisible: false,//控制添加窗口是否显示
        teachplanRules: {
            pname: [
                { required: true, message: '请输入课程计划名称', trigger: 'blur' }
            ],
            status: [
                { required: true, message: '请选择状态', trigger: 'blur' }
            ]
        },
        teachplanActive: {},
        ......
    }
},
```

3. 添加按钮，通过变量 teachplayFormVisible 控制弹出窗口是否显示

```html
<el-button type="primary" @click="teachplayFormVisible = true">添加课程计划</el-button>
```

4. 定义表单提交方法和重置方法

```js
// 提交课程计划
addTeachplan() {
    console.log('提交课程计划')
},
// 重置表单
resetForm() {
    this.teachplanActive = {}
},
```

#### 3.3.3. 后端 API 接口

在 xc-service-api 工程 com.xuecheng.api.course.CourseControllerApi 增加课程计划新增接口

```java
@ApiOperation("添加课程计划")
public ResponseResult addTeachplan(Teachplan teachplan);
```

#### 3.3.4. 课程管理服务
##### 3.3.4.1. dao 层

使用 Spring Data JPA 框架，创建dao层接口，定义查询方法。

```java
/**
 * 课程计划数据接口（Spring Data JPA）
 */
public interface TeachplanRepository extends JpaRepository<Teachplan, String> {
    /**
     * 根据课程id和父结点id查询出结点列表，可以使用此方法实现查询根结点
     *
     * @param courseId 课程id
     * @param parentId 父结点id
     * @return 课程计划集合
     */
    List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
```

*注：上面的查询方法相当于`SELECT * FROM teachplan WHERE courseid = '297e7c7c62b888f00162b8a7dec20000' AND parentid='0'`*

##### 3.3.4.2. service 层

```java
@Service
public class CourseService {

    /* 注入课程计划mapper接口 */
    @Autowired
    private TeachplanMapper teachplanMapper;
    /* 注入基础课程接口 */
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    /* 注入课程计划JPA接口 */
    @Autowired
    private TeachplanRepository teachplanRepository;

    ......

    /**
     * 添加课程计划
     *
     * @param teachplan 课程计划实体类
     * @return 响应结果
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {

        // 校验上送的数据中课程id和课程计划名称
        if (teachplan == null || StringUtils.isBlank(teachplan.getCourseid()) ||
                StringUtils.isBlank(teachplan.getPname())) {
            // 抛出非法参数的自定义异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 获取上送的课程id
        String courseid = teachplan.getCourseid();
        // 获取上送的父结点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isBlank(parentid)) {
            // 判断如果父结点为空，则获取课程的根结点
            parentid = getTeachplanRoot(courseid);
        }

        // 根据课程计划id，查询父结点的信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        if (!optional.isPresent()) {
            // 查询不到父结点数据，则抛出非法参数异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 获取父结点课程计划
        Teachplan teachplanParent = optional.get();
        // 父结点级别
        String parentGrade = teachplanParent.getGrade();

        // 设置新增的课程课程计划的父结点id
        teachplan.setParentid(parentid);
        // 设置状态为未发布
        teachplan.setStatus("0");
        // 根据父结点级别判断子结点级别
        switch (parentGrade) {
            case "1":
                teachplan.setGrade("2");
                break;
            case "2":
                teachplan.setGrade("3");
                break;
            default:
                break;
        }

        // 保存课程计划
        teachplanRepository.save(teachplan);
        return ResponseResult.SUCCESS();
    }

    /**
     * 获取课程根结点，如果没有则添加根结点
     *
     * @param courseId 课程id
     * @return 根结点id
     */
    public String getTeachplanRoot(String courseId) {
        // 校验课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return "";
        }

        // 获取查询基础课程实体
        CourseBase courseBase = optional.get();

        // 调用dao查询teachplan表得到该课程的根结点（一级结点）
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            // 如果没有当前课程结点，则新增一个根结点
            Teachplan teachplan = new Teachplan();
            teachplan.setPname(courseBase.getName());
            teachplan.setParentid("0");
            teachplan.setGrade("1");    // 设置为一级结点
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");   // 设置状态为'0'，未发布

            // 调用课程计划JPA接口保存
            teachplanRepository.save(teachplan);
            // 返回保存后的课程计划id
            return teachplan.getId();
        }

        // 返回数据库查询的课程计划集合中第一个计划的结点id
        return teachplanList.get(0).getId();
    }
}
```

##### 3.3.4.3. controller 层

修改xc-service-manage-course工程的CourseController，增加添加课程计划的方法

```java
/**
 * 添加课程计划
 *
 * @param teachplan 课程计划实体类
 * @return 响应结果
 */
@Override
@PostMapping("/teachplan/add")
public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
    return courseService.addTeachplan(teachplan);
}

```

##### 3.3.4.4. 测试

使用swagger-ui或postman测试上边的课程计划添加接口；

#### 3.3.5. 页面前端
##### 3.3.5.1. Api调用

1. 修改src\module\course\api\course.js，添加课程计划api方法

```js
/*添加课程计划*/
export const addTeachplan = teachplah => {
  return http.requestPost(apiUrl + '/course/teachplan/add', teachplah)
}
```

2. 页面调用api

```js
// 提交课程计划
addTeachplan() {
    //校验表单
    this.$refs.teachplanForm.validate((valid) => {
        if (valid) {
            //调用api方法
            //将课程id设置到teachplanActive
            this.teachplanActive.courseid = this.courseid
            courseApi.addTeachplan(this.teachplanActive).then(res => {
                if (res.success) {
                    this.$message.success("添加成功")
                    //刷新树
                    this.findTeachplan()
                    // 关闭dialog窗口
                    this.teachplayFormVisible = false
                } else {
                    this.$message.error(res.message)
                }

            })
        }
    })
},
// 重置表单
resetForm() {
    this.teachplanActive = {}
},
```

#### 3.3.6. 测试

- 测试流程：
1. 新建一个课程
2. 向新建课程中添加课程计划
- 添加一级结点
- 添加二级结点
