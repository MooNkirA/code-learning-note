# Spring Boot 整合 MyBatis

## 1. 环境准备

- **第一步：导入数据库表**
- **第二步：加入MyBatis的启动器依赖**

```xml
<!-- 配置MyBatis启动器 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.0</version>
</dependency>
<!-- 配置mysql驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!-- 配置c3p0连接池 -->
<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5.2</version>
</dependency>
```

- **第三步：加入配置文件**
    - 参考spring-boot-autoconfigure-1.5.6.RELEASE.jar中jdbc包中属性文件类**DataSourceProperties**

    ```java
    @ConfigurationProperties(prefix = "spring.datasource")
    public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
    
    	private ClassLoader classLoader;
    
    	/**
    	 * Name of the datasource. Default to "testdb" when using an embedded database.
    	 */
    	private String name;
    
    	/**
    	 * Whether to generate a random datasource name.
    	 */
    	private boolean generateUniqueName;
    
    	/**
    	 * Fully qualified name of the connection pool implementation to use. By default, it
    	 * is auto-detected from the classpath.
    	 */
    	private Class<? extends DataSource> type;
    
    	/**
    	 * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
    	 */
    	private String driverClassName;
    
    	/**
    	 * JDBC URL of the database.
    	 */
    	private String url;
    
    	/**
    	 * Login username of the database.
    	 */
    	private String username;
    
    	/**
    	 * Login password of the database.
    	 */
    	private String password;
       ......
    }
    ```

    - 参考mybatis-spring-boot-autoconfigure-1.3.0.jar中属性文件类**MybatisProperties**

    ```java
    @ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
    public class MybatisProperties {
    
      public static final String MYBATIS_PREFIX = "mybatis";
    
      /**
       * Location of MyBatis xml config file.
       */
      private String configLocation;
    
      /**
       * Locations of MyBatis mapper files.
       */
      private String[] mapperLocations;
    
      /**
       * Packages to search type aliases. (Package delimiters are ",; \t\n")
       */
      private String typeAliasesPackage;
    
      /**
       * Packages to search for type handlers. (Package delimiters are ",; \t\n")
       */
      private String typeHandlersPackage;
    
      /**
       * Indicates whether perform presence check of the MyBatis xml config file.
       */
      private boolean checkConfigLocation = false;
    
      /**
       * Execution mode for {@link org.mybatis.spring.SqlSessionTemplate}.
       */
      private ExecutorType executorType;
    
      /**
       * Externalized properties for MyBatis configuration.
       */
      private Properties configurationProperties;
    
      /**
       * A Configuration object for customize default settings. If {@link #configLocation}
       * is specified, this property is not used.
       */
      @NestedConfigurationProperty
      private Configuration configuration;
      ......
    }
    ```

在src/main/resources下添加application.properties（或application.yml）配置文件，内容如下：

```properties
# 配置数据源
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.type=com.mchange.v2.c3p0.ComboPooledDataSource

# 配置MyBatis3
# 配置类型别名扫描基础包
mybatis.typeAliasesPackage=com.moon.springboot.domain
# 配置SQL语句映射文件
mybatis.mapperLocations=classpath:mappers/**/*Mapper.xml
# 配置核心配置文件
mybatis.configLocation=classpath:mybatis-config.xml
```

使用yml配置文件

```yml
mybatis:
    mapper-locations: classpath:mappers/**/*.xml
    type-aliases-package: com.moon.demo.pojo
    # 开启驼峰映射
    configuration:
        map-underscore-to-camel-case: true
```

**注：传统的ssm框架中，mybatis的总配置文件是mybatis-config.xml，但spring boot推荐少用配置文件，所以，可以将mybatis-config.xml的相关配置写在application.properties(或 application.yml)中**

## 2. application文件相关配置

- 任何其他Spring Boot应用程序一样，MyBatis-Spring-Boot-Application配置参数存储在application.properties（或application.yml）中。
- MyBatis使用前缀mybatis作为其属性

### 2.1. 可用的属性

|         **属性**         |                                                                                                                **描述**                                                                                                                 |
| :----------------------: | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|     config-location      | MyBatis xml配置文件的位置                                                                                                                                                                                                        |
|  check-config-location   | 指示是否执行MyBatis xml配置文件的状态检查                                                                                                                                                                                   |
|     mapper-locations     | Mapper xml映射文件的位置                                                                                                                                                                                                          |
|   type-aliases-package   | 用于搜索类型别名的包。 （包分隔符是“，; \ t \ n”）                                                                                                                                                                      |
|  type-handlers-package   | 用于搜索类型处理程序的包。 （包分隔符是“，; \ t \ n”）                                                                                                                                                                 |
|      executor-type       | 执行者类型：SIMPLE，REUSE，BATCH。                                                                                                                                                                                          |
| configuration-properties | MyBatis配置的外部化属性。指定的属性可以用作MyBatis配置文件和Mapper文件的占位符                                                                                                                              |
|      configuration       | MyBatis相关配置bean。关于可用属性，与mybatis-config.xml配置文件的settings配置属性一致。**注意此属性不能config-location同时使用** |

### 2.2. 配置案例

```properties
# application.properties
mybatis.type-aliases-package=com.example.domain.model
mybatis.type-handlers-package=com.example.typehandler
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.default-fetch-size=100
mybatis.configuration.default-statement-timeout=30
...
```

```yml
# application.yml
mybatis:
    type-aliases-package: com.example.domain.model
    type-handlers-package: com.example.typehandler
    configuration:
        map-underscore-to-camel-case: true
        default-fetch-size: 100
        default-statement-timeout: 30
...
```

## 3. 整合开发Demo

- 使用Spring Boot + Spring MVC + MyBatis实现查询所有公告
- 使用Spring Boot + Spring MVC + MyBatis + EasyUI 实现公告分页查询

- **第一步：创建domain**

```java
public class Notice implements Serializable {
    private static final long serialVersionUID = 5679176319867604937L;
    private Long id;
    private String title;
    private String content;
    /** setter and getter method */
    public Long getId() {
    	return id;
    }
    public void setId(Long id) {
    	this.id = id;
    }
    public String getTitle() {
    	return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    public String getContent() {
    	return content;
    }
    public void setContent(String content) {
    	this.content = content;
    }
}
```

- **第二步：编写NoticeMapper接口**。和之前的方式一样，只是多了@Mapper个注解。@Mapper：声明Mapper接口
    - 注意：`@Mapper`标记该类是一个mybatis的mapper接口，可以被spring boot自动扫描到spring上下文中

```java
@Mapper
public interface NoticeMapper {
    /** 查询所有公告 */
    @Select("select * from notice")
    public List<Notice> findAll();

    /** 统计查询 */
    public Long count();
    /** 分页查询公告 */
    public List<Notice> findByPage(@Param("page")Integer page, @Param("rows")Integer rows);
}
```

- **第三步：编写src/main/resources/mappers/NoticeMapper.xml文件**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.itcast.springboot.mapper.NoticeMapper">

    <!-- 统计查询 -->
    <select id="count" resultType="long">
		select count(*) from notice
	</select>

    <!-- 分页查询公告 -->
    <select id="findByPage" resultType="notice">
		select * from notice limit #{page},#{rows}
	</select>
</mapper>
```

- **第四步：编写Service与实现类**

```java
public interface NoticeService {
    /** 查询所有的公告 */
    public List<Notice> findAll();
    /** 分页查询公告 */
    public Map<String,Object> findByPage(Integer page, Integer rows);
}

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;
    /** 查询所有的公告 */
    public List<Notice> findAll(){
        return noticeMapper.findAll();
    }
    /** 分页查询公告 */
    public Map<String,Object> findByPage(Integer page, Integer rows){
        /** 创建Map集合封装响应数据 */
        Map<String,Object> data = new HashMap<>();
        /** 统计查询 */
        long count = noticeMapper.count();
        data.put("total", count);
        /** 分页查询 */
        List<Notice> notices = noticeMapper.findByPage(page, rows);
        data.put("rows", notices);
        return data;
    }
}
```

- **第五步：编写Controller**

```java
@Controller
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    /** 查询全部公告 */
    @GetMapping("/findAll")
    @ResponseBody
    public List<Notice> findAll(){
        return noticeService.findAll();
    }
    /** 跳转分页查询公告页面 */
    @GetMapping("/show")
    public String show(){
        return "/html/notice.html";
    }
    /** 分页查询公告 */
    @PostMapping("/findByPage")
    @ResponseBody
    public Map<String,Object> findByPage(@RequestParam(value="page", defaultValue="1", required=false)Integer page,
                                         @RequestParam(value="rows", defaultValue="15", required=false)Integer rows){
        return noticeService.findByPage((page - 1) * rows, rows);
    }
}
```

- **第六步：加入静态资源**

src/main/resources/public/html/notice.html
src/main/resources/static/js
src/main/resources/static/css
src/main/resources/static/images

- **第七步：编写启动类**
- **第八步：测试**
    - 浏览器地址栏输入：http://localhost:8080/findAll
    - 浏览器地址栏输入：http://localhost:8080/show



