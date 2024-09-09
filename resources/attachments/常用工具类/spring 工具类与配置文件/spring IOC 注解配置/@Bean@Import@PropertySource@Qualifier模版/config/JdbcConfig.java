package com.moonzero.config;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * ��ȡ���ݿ����������
 * 
 * @author MoonZero
 */
// ʹ��ע����spring��������
// �ѵ�ǰ�ཻ��spring�������ܶ�ȡ��Bean��ע��
// @Component("jdbc")
// ����������˴�ע�⣬JdbcConfig�����ͻ����spring�������ˣ��������ڴ�
// ��ô���������⣿ ���ǲ�Ҫ��ע�⣬����ڵ��������У�������༴�ɣ�@Import
public class JdbcConfig {

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

	/**
	 * ʹ��ע�ⴴ����������ֵ���󣬽���Spring���� name�������õ��Ƕ����������е�id
	 */
	@Bean(name = "ds")
	public DataSource getDs() {
		System.out.println(driver);
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);

		// ��ȡc3p0����Դ
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try {
			// �������ݿ�4Ҫ��,һ�㿪�����Ὣ����д���ڳ����У�����ʹ�������ļ�
			ds.setDriverClass(driver);
			ds.setJdbcUrl(url);
			ds.setUser(username);
			ds.setPassword(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ���ض���
		return ds;
	}

	// ����ʹ��@Qualifierע�⣬ֻ��д�ڷ��������б��У� ������ע��spring������������
	@Bean(name = "qr")
	public QueryRunner getQueryRunner(DataSource ds) {
		System.out.println(ds);
		return new QueryRunner(ds);
	}

}
