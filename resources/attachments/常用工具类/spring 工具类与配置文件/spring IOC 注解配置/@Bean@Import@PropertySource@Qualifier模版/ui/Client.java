package com.moonzero.ui;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.moonzero.config.SpringConfiguration;

/**
 * ����@Bean/@PropertySource/@Importע��
 * 
 * @author MoonZero
 */
public class Client {
	public static void main(String[] args) throws SQLException {
		// ��ȡSpring��������
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		// ==��ʽһ��
		// ����id��ȡ����
		// JdbcConfig jc = (JdbcConfig) context.getBean("jdbc");
		// ��ȡ���ݿ�Ĳ�������
		// QueryRunner qr = new QueryRunner(jc.getDs());

		// ==��ʽ����
		// DataSource ds = (DataSource) context.getBean("ds");
		// QueryRunner qr = new QueryRunner(ds);

		// ===��ʽ����
		QueryRunner qr = (QueryRunner) context.getBean("qr");

		// ��������
		String sql = "insert into user (name,gender) values (?,?);";
		qr.update(sql, "Ӱħ", "x");
	}
}
