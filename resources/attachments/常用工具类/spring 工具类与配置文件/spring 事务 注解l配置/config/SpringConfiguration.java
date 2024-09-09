package com.moon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 配置类代替xml配置文件
 * 
 * @author MoonZero
 */
// 设置当前类为配置类
@Configuration
// 配置容器加载时扫描的基包
@ComponentScan("com.moon")
// 导入jdbc配置类
@Import({ JdbcConfiguration.class })
// 加载属性文件中的配置，数据源4要素配置
@PropertySource({ "classpath:/com/moon/config/jdbc.properties" })
// 开启事务注解的支持
@EnableTransactionManagement
public class SpringConfiguration {

	/**
	 * 占位符类(无任何作用，spring4.3版本之前需要设置)
	 * 
	 * @return
	 */
	@Bean(name = "pspc")
	public static PropertySourcesPlaceholderConfigurer createPspc() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
