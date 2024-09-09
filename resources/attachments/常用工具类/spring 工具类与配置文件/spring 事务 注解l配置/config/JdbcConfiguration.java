package com.moon.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Jdbc相关配置
 * 
 * @author MoonZero
 */
public class JdbcConfiguration {
	// 设置数据4要素,使用spring的el表达式读取值
	@Value("${jdbc.driverClass}")
	private String driverClassName;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

	// 使用配置Spring内置数据源给Spring管理
	@Bean(name = "dataSource")
	public DataSource createDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		return ds;
	}

	// 配置数据库JdbcTemplate给spring管理
	@Bean(name = "jdbcTemplate")
	public JdbcTemplate createJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	// 配置创建平台事务管理器给spring管理
	@Bean(name = "tsransactionManager")
	public DataSourceTransactionManager createDSTM(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
