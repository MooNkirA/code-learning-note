package com.moonzero.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 纯注解的配置类
 * 
 * @author MoonZero
 */
// 配置为纯注解配置类
@Configuration
// 配置创建spring容器需要扫描的基础包
@ComponentScan("com.moonzero")
// 开户对AOP注解的支持
@EnableAspectJAutoProxy
public class SpringConfiguration {
	
}
