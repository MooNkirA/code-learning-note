# Spring Boot Task Scheduling - 定时任务调度

定时任务是企业级开发中必不可少的组成部分，诸如长周期业务数据的计算，例如年度报表，诸如系统脏数据的处理，再比如系统性能监控报告，还有抢购类活动的商品上架，这些都离不开定时任务。Spring Boot 提供了整合第三方任务调度框架的功能

## 1. Quartz 调度器整合

### 1.1. 相关概念

Quartz 是一款比较成熟的定时任务框架，本身使用时配置略微复杂。Spring Boot 对其进行整合后，简化了一系列的配置，多数配置均可采用默认设置。Quartz 涉及的概念如下：

- 工作（Job）：用于定义具体执行的工作
- 工作明细（JobDetail）：用于描述定时工作相关的信息
- 触发器（Trigger）：描述了工作明细与调度器的对应关系
- 调度器（Scheduler）：用于描述触发工作的执行规则，通常使用 cron 表达式定义规则

### 1.2. 整合示例

#### 1.2.1. 项目依赖

在项目的 pom.xml 文件中引入 Spring Boot 整合 Quartz 的 spring-boot-starter-quartz 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

#### 1.2.2. 创建定时任务

按照 Quartz 框架的开发规范，创建定时任务类，该继承 `org.springframework.scheduling.quartz.QuartzJobBean`，实现任务调度时执行的方法 `executeInternal`

```java
public class MyQuartzJob extends QuartzJobBean {
    /**
     * 任务执行的核心方法
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("MyQuartzJob task run...");
    }
}
```

#### 1.2.3. 创建 Quartz 配置类

创建 Quartz 配置类，类中定义工作明细（JobDetail）与触发器（Trigger）的对象实例

```java
@Configuration
public class QuartzConfig {

    /**
     * 创建 JobDetail 工作明细实例
     */
    @Bean
    public JobDetail createJobDetail() {
        // 绑定具体的任务类
        return JobBuilder.newJob(MyQuartzJob.class)
                .storeDurably()
                .build();
    }

    /**
     * 创建 Trigger 任务触发器实例
     */
    @Bean
    public Trigger createJobTrigger() {
        // 使用 cron 表达式定义执行规则
        ScheduleBuilder<CronTrigger> schedBuilder = CronScheduleBuilder.cronSchedule("0/5 * * * * ?");
        // 绑定对应的工作明细对象
        return TriggerBuilder.newTrigger().forJob(createJobDetail())
                .withSchedule(schedBuilder).build();
    }
}
```

在工作明细实例中，要设置对应的具体工作实现类 `MyQuartzJob`，使用 `newJob()` 方法传入对应的工作任务类型即可。

触发器需要绑定任务，使用 `forJob()` 方法传入绑定的工作明细对象。此处可以为工作明细设置名称然后使用名称绑定，也可以直接调用对应方法绑定。触发器中最核心的规则是执行时间，此处使用调度器定义执行时间，执行时间描述方式使用的是 cron表达式。

#### 1.2.4. 功能测试

启动程序，观察控制台是否每5秒输出一次结果

## 2. Spring Task 任务调度整合

### 2.1. 概述

上述的 Quartz 框架将其中的对象划分粒度过细，导致开发的时候有点繁琐，Spring 针对上述规则进行了简化，开发了自己的任务管理组件 - Spring Task。

Spring 根据定时任务的特征，将定时任务的开发简化成只需要定义任务并加入到 Spring 容器，再定义其任务的执行规则即可。

### 2.2. 整合示例

#### 2.2.1. 开启定时任务功能

在启动类或者配置类上，标识 `@EnableScheduling` 注解，开启定时任务功能

```java
@SpringBootApplication
@EnableScheduling // 开启定时任务功能
public class TaskSchedulingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulingApplication.class, args);
    }
}
```

#### 2.2.2. 创建任务类与执行规则

创建任务类，在相应需要定时执行的方法上标识 `@Scheduled` 注解，通过 `cron` 属性定义其执行的时间，执行时间的描述方式还是 cron 表达式

```java
@Component
public class MySpringTask {
    /*
     * @Scheduled 注解用于标识任务调度时执行的方法，通过 cron 属性来定义任务执行的规则
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void doSomething() {
        System.out.println(Thread.currentThread().getName() + " : spring task run...");
    }
}
```

#### 2.2.3. 功能测试

启动程序，观察控制台是否每3秒输出一次结果

### 2.3. Spring Task 相关配置

可以项目配置文件，对 Spring Task 定时任务进行相关配置

```yml
spring:
  task:
    scheduling:
      pool:
        size: 1                           # 任务调度线程池大小 默认 1
      thread-name-prefix: spring_tasks_   # 调度线程名称前缀 默认 scheduling-
      shutdown:
        await-termination: false          # 线程池关闭时等待所有任务完成
        await-termination-period: 10s     # 调度线程关闭前最大等待时间，确保最后一定关闭
```
