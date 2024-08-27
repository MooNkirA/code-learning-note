package com.moonzero.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Component("ds")
public class DS {
	private ComboPooledDataSource ds = new ComboPooledDataSource();
	
	// ���ó�Ա����,ʹ��ע�����Ա����ע��
	// Valueע����ʹ��Spring��EL���ʽ
	@Value("${jdbc_driver}")
	private String driver;
	@Value("${jdbc_url}")
	private String url;
	@Value("${jdbc_username}")
	private String username;
	@Value("${jdbc_password}")
	private String password;
	
	
	@Bean(name="dd")
	public DataSource getDs() {
		try {
			ds.setDriverClass(driver);
			ds.setJdbcUrl(url);
			ds.setUser(username);
			ds.setPassword(password);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return ds;
	}
}
