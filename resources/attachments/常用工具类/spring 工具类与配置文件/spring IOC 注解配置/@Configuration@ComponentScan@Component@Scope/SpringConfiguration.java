package com.moonzero.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 代替bean.xml文件的配置类
 * 
 * @author MoonZero
 */
// 用于指定当前类是一个配置类，会从该类上加载注解。读取该类上@ComponentScan注解初始化spring容器。
@Configuration
// 用于指定spring在初始化容器时要扫描的包
// basePackages：用于指定要扫描的包
// @ComponentScan(basePackages = {"com.moonzero"})
// 与value一样，可以不省略不写，如果数组只有一个，{}也可以省略
@ComponentScan("com.moonzero")
public class SpringConfiguration {

}
