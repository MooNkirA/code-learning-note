package com.moonzero.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

/**
 * ���ӳع�����
 * 
 * @author MoonZero
 *
 */
public class DBCPUtil {
	// ������̬���ӳس�Ա����
	private static DataSource ds = null;
	
	// �����̹߳���ֲ�����
	private static ThreadLocal<Connection> local = new ThreadLocal<Connection>();

	static {
		// ��ȡ�����ļ�����ȡ���ӳض���
		Properties info = new Properties();
		try {
			info.load(DBCPUtil.class.getResourceAsStream("/dbcp.properties"));
			ds = BasicDataSourceFactory.createDataSource(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������
	 */
	public static void beginTransaction() {
		try {
			// ��ȡ���Ӷ���
			Connection conn = getConnection();
			// ��������
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * �ύ����
	 */
	public static void commit() {
		// ��ȡ���Ӷ���
		Connection conn = getConnection();
		// ʹ��BdUtils�������ύ���񲢹ر����Ӷ���
		DbUtils.commitAndCloseQuietly(conn);
	}

	/**
	 * �ع�����
	 */
	public static void rollback() {
		// ��ȡ���Ӷ���
		Connection conn = getConnection();
		// ʹ��BdUtils������ع����񲢹ر����Ӷ���
		DbUtils.rollbackAndCloseQuietly(conn);
	}
	
	/**
	 * ���DataSource���ӳض���
	 * 
	 * @return DataSource����
	 */
	public static DataSource getDataSource() {
		return ds;
	}
	
	/**
	 * ��������ֲ߳̾����Ӷ���
	 * 
	 * @return Connection���Ӷ���
	 */
	public static Connection getConnection() {
		try {
			// �ȴ��ֲ߳̾������л�����Ӷ���
			Connection conn = local.get();
			// �����жϣ��̹߳���ֲ������Ƿ��Ѿ�����
			if (conn == null) {
				// ���߳��������Ӷ���
				conn = ds.getConnection();
				// �����Ӷ�����ӵ�local��
				local.set(conn);
			}
			return conn;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
