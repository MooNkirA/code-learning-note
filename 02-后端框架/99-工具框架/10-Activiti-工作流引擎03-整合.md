# Activiti 工作流引擎整合开发

## 1. Activiti7 与 Spring 整合开发

### 1.1. pom 依赖配置
 
pom.xml 配置如下：

```xml
<properties>
    <log4j2.version>2.17.2</log4j2.version>
    <activiti.version>7.1.0.M2</activiti.version>
    <druid.version>1.2.6</druid.version>
</properties>

<dependencies>
    <!-- Activiti 引擎核心依赖 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-engine</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- Activiti 与 Spring 整合核心依赖 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-spring</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- bpmn 模型处理 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-bpmn-model</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- bpmn 转换 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-bpmn-converter</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- bpmn json数据转换 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-json-converter</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- bpmn 布局 -->
    <dependency>
        <groupId>org.activiti</groupId>
        <artifactId>activiti-bpmn-layout</artifactId>
        <version>${activiti.version}</version>
    </dependency>
    <!-- activiti 云支持 -->
    <!--<dependency>
        <groupId>org.activiti.cloud</groupId>
        <artifactId>activiti-cloud-services-api</artifactId>
        <version>${activiti.version}</version>
    </dependency>-->

    <!-- mysql驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.26</version>
    </dependency>
    <!-- mybatis -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.9</version>
    </dependency>
    <!-- druid 数据源 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>${druid.version}</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>5.1.4.RELEASE</version>
        <scope>test</scope>
    </dependency>

    <!-- log4j2 日志 -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>${log4j2.version}</version>
    </dependency>

    <dependency>
        <groupId>aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.5.4</version>
    </dependency>
</dependencies>
```


### 1.2. Activiti 与 Spring 整合配置文件

#### 1.2.1. 基于 xml 文件配置

在 Activiti 中核心类的是 `ProcessEngine` 流程引擎，与 Spring 整合就是让 Spring 来管理 `ProcessEngine` 实例。通过 `org.activiti.spring.SpringProcessEngineConfiguration` 与 Spring 整合方式来创建 `ProcessEngine` 对象

在示例项目中，创建 spring 与 activiti 的整合配置文件：activiti-spring.xml（名称自定）

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置数据源，使用阿里druid数据源 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" destroy-method="close" primary="true">
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://127.0.0.1:3306/activiti_sample?useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>

    <!-- 工作流引擎配置bean -->
    <bean id="processEngineConfiguration" class="org.activiti.spring.SpringProcessEngineConfiguration">
        <!-- 数据源 -->
        <property name="dataSource" ref="dataSource"/>
        <!-- 使用spring事务管理器 -->
        <property name="transactionManager" ref="transactionManager"/>
        <!-- 数据库策略 -->
        <property name="databaseSchemaUpdate" value="drop-create"/>
    </bean>
    <!-- 流程引擎 -->
    <bean id="processEngine" class="org.activiti.spring.ProcessEngineFactoryBean">
        <property name="processEngineConfiguration" ref="processEngineConfiguration"/>
    </bean>
    <!-- 资源服务service -->
    <bean id="repositoryService" factory-bean="processEngine" factory-method="getRepositoryService"/>
    <!-- 流程运行service -->
    <bean id="runtimeService" factory-bean="processEngine" factory-method="getRuntimeService"/>
    <!-- 任务管理service -->
    <bean id="taskService" factory-bean="processEngine" factory-method="getTaskService"/>
    <!-- 历史管理service -->
    <bean id="historyService" factory-bean="processEngine" factory-method="getHistoryService"/>
    <!-- 事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    <!-- 通知 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <!-- 传播行为 -->
            <tx:method name="save*" propagation="REQUIRED"/>
            <tx:method name="insert*" propagation="REQUIRED"/>
            <tx:method name="delete*" propagation="REQUIRED"/>
            <tx:method name="update*" propagation="REQUIRED"/>
            <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
            <tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
        </tx:attributes>
    </tx:advice>
    <!-- 切面，根据具体项目修改切点配置 -->
    <!--<aop:config proxy-target-class="true">
        <aop:advisor advice-ref="txAdvice"
                     pointcut="execution(*com.moon.activiti.service.impl..(..))"/>
    </aop:config>-->
</beans>
```

数据库策略 databaseSchemaUpdate 的取值说明：

- flase：默认值。activiti 在启动时，会对比数据库表中保存的版本，如果没有表或者版本不匹配，将抛出异常。（生产环境常用）
- true：activiti 会对数据库中所有表进行更新操作。如果表不存在，则自动创建。（开发时常用）
- create_drop：在 activiti 启动时创建表，在关闭时删除表（必须手动关闭引擎，才能删除表）。（单元测试常用）
- drop-create：在 activiti 启动时删除原来的旧表，然后在创建新表（不需要手动关闭引擎）。

#### 1.2.2. 基于纯注解配置

> 参考 xml 配置，改成使用注解方式即可

1. 创建 Activiti 配置类 `ActivitiConfig`

```java
@Configuration
@ComponentScan("com.moon.activiti")
public class ActivitiConfig {

    /* 配置数据源，使用阿里druid数据源 */
    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/activiti_sample?useSSL=false");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");
        return druidDataSource;
    }

    /* 工作流引擎配置bean */
    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(DruidDataSource dataSource, DataSourceTransactionManager transactionManager) {
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        // 设置数据源
        processEngineConfiguration.setDataSource(dataSource);
        // 设置 spring 事务管理器
        processEngineConfiguration.setTransactionManager(transactionManager);
        // 设置数据库策略
        processEngineConfiguration.setDatabaseSchemaUpdate("drop-create");
        return processEngineConfiguration;
    }

    /* 流程引擎 */
    @Bean
    public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration processEngineConfiguration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(processEngineConfiguration);
        return processEngineFactoryBean;
    }

    /* Spring 事务管理器 */
    @Bean
    public DataSourceTransactionManager transactionManager(DruidDataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
```

2. 通过 `FactoryBean` 接口的方法，创建 `RepositoryService` 实例（*其他的 Service 操作实例同样的方式*）

```java
@Component("taskService")
public class RepositoryServiceFactoryBean implements FactoryBean<TaskService> {

    @Autowired
    private ProcessEngineFactoryBean processEngineFactoryBean;

    @Override
    public TaskService getObject() throws Exception {
        return processEngineFactoryBean.getObject().getTaskService();
    }

    @Override
    public Class<?> getObjectType() {
        return TaskService.class;
    }
}
```

相当于 xml 的配置

```xml
<bean id="taskService" factory-bean="processEngine" factory-method="getTaskService"/>
```

### 1.3. 测试

编写简单的测试代码，如果能获取 Activiti 相关 Service 类实例，即说明与 Spring 的整合成功。

- 基于 xml 配置测试

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-spring.xml")
public class ActivitiSpringXmlTest {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testBasic() {
        System.out.println(repositoryService);
    }
}
```

- 基于注解配置测试

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ActivitiConfig.class)
public class ActivitiSpringAnnotationTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void testBasic() {
        System.out.println(taskService);
    }
}
```

### 1.4. 执行流程分析

Activiti 与 Spring 整合加载的过程分析：

1. 加载 activiti-spring.xml 配置文件（名称自定）
2. 加载 `SpringProcessEngineConfiguration` 对象，该对象需要依赖注入 `DataSource` 对象和 `TransactionManager` 对象。
3. 加载 `ProcessEngineFactoryBean` 工厂来创建 `ProcessEngine` 对象，而 `ProcessEngineFactoryBean` 工厂又需要依赖注入 `ProcessEngineConfiguration` 对象。
4. ProcessEngine 对象来负责创建 Activiti 各种 Service 处理对象，从而简化 Activiti 的开发过程。

## 2. Activiti7 与 SpringBoot 整合开发

Activiti7 发布正式版之后，它与 Spring Boot 2.x 已经完全支持整合开发。

### 2.1. Spring Boot 整合 Activiti7 的配置














