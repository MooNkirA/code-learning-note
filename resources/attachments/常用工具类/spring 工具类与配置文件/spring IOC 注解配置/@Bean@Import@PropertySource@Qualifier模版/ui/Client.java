package com.moonzero.ui;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.moonzero.config.SpringConfiguration;

/**
 * 测试@Bean/@PropertySource/@Import注解
 * 
 * @author MoonZero
 */
public class Client {
	public static void main(String[] args) throws SQLException {
		// 获取Spring容器对象
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		// ==方式一：
		// 根据id获取对象
		// JdbcConfig jc = (JdbcConfig) context.getBean("jdbc");
		// 获取数据库的操作对象
		// QueryRunner qr = new QueryRunner(jc.getDs());

		// ==方式二：
		// DataSource ds = (DataSource) context.getBean("ds");
		// QueryRunner qr = new QueryRunner(ds);

		// ===方式三：
		QueryRunner qr = (QueryRunner) context.getBean("qr");

		// 新增数据
		String sql = "insert into user (name,gender) values (?,?);";
		qr.update(sql, "影魔", "x");
	}
}
