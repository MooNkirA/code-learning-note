package com.moonzero.config;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 获取数据库操作配置类
 * 
 * @author MoonZero
 */
// 使用注解让spring创建对象
// 把当前类交给spring管理，才能读取到Bean的注解
// @Component("jdbc")
// 但是如果用了此注解，JdbcConfig这个类就会放入spring容器中了，会消耗内存
// 怎么解决这个问题？ 我们不要此注解，在入口的配置类中，引入此类即可：@Import
public class JdbcConfig {

	// 设置成员变量,使用注解给成员变量注入
	// Value注解中使用Spring的EL表达式
	@Value("${jdbc_driver}")
	private String driver;
	@Value("${jdbc_url}")
	private String url;
	@Value("${jdbc_username}")
	private String username;
	@Value("${jdbc_password}")
	private String password;

	/**
	 * 使用注解创建方法返回值对象，交给Spring管理 name属性配置的是对象在容器中的id
	 */
	@Bean(name = "ds")
	public DataSource getDs() {
		System.out.println(driver);
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);

		// 获取c3p0数据源
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try {
			// 设置数据库4要素,一般开发不会将数据写死在程序中，所以使用配置文件
			ds.setDriverClass(driver);
			ds.setJdbcUrl(url);
			ds.setUser(username);
			ds.setPassword(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回对象
		return ds;
	}

	// 单独使用@Qualifier注解，只能写在方法参数列表中， 给参数注入spring容器其他对象
	@Bean(name = "qr")
	public QueryRunner getQueryRunner(DataSource ds) {
		System.out.println(ds);
		return new QueryRunner(ds);
	}

}
