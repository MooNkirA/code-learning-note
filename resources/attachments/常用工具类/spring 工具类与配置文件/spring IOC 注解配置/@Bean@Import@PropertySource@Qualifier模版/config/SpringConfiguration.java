package com.moonzero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

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
// 导入其他配置类
@Import({ JdbcConfig.class, DS.class })
// 导入properties属性文件
@PropertySource(value = { "classpath:/com/moonzero/config/jdbc.properties" })
public class SpringConfiguration {
	/**
	 * @PropertySource ： 导入其他的属性文件 value的属性就是属性文件的位置 
	 * 		1）如果是spring4.3版本之后，这样就可以注入了
	 *      2）如果是spring4.3版本之前，还需要一个占位符类，这个类，没有什么作用，
	 *         只是告诉spring框架，我们开启Spring的El表达式 此占位符类，需要放入spring的容器中
	 */
	// 创建占位符类，4.3版本后不需要提供，默认开启
	@Bean(name = "pspc")
	public PropertySourcesPlaceholderConfigurer getPSPC() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
