package com.moonzero.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

/**
 * 连接池工具类
 * 
 * @author MoonZero
 *
 */
public class DBCPUtil {
	// 声明静态连接池成员变量
	private static DataSource ds = null;
	
	// 创建线程共享局部对象
	private static ThreadLocal<Connection> local = new ThreadLocal<Connection>();

	static {
		// 读取配置文件，获取连接池对象
		Properties info = new Properties();
		try {
			info.load(DBCPUtil.class.getResourceAsStream("/dbcp.properties"));
			ds = BasicDataSourceFactory.createDataSource(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 开启事务
	 */
	public static void beginTransaction() {
		try {
			// 获取连接对象
			Connection conn = getConnection();
			// 开启事务
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 提交事务
	 */
	public static void commit() {
		// 获取连接对象
		Connection conn = getConnection();
		// 使用BdUtils工具类提交事务并关闭连接对象
		DbUtils.commitAndCloseQuietly(conn);
	}

	/**
	 * 回滚事务
	 */
	public static void rollback() {
		// 获取连接对象
		Connection conn = getConnection();
		// 使用BdUtils工具类回滚事务并关闭连接对象
		DbUtils.rollbackAndCloseQuietly(conn);
	}
	
	/**
	 * 获得DataSource连接池对象
	 * 
	 * @return DataSource对象
	 */
	public static DataSource getDataSource() {
		return ds;
	}
	
	/**
	 * 获得连接线程局部连接对象
	 * 
	 * @return Connection连接对象
	 */
	public static Connection getConnection() {
		try {
			// 先从线程局部对象中获得连接对象
			Connection conn = local.get();
			// 进行判断，线程共享局部对象是否已经存在
			if (conn == null) {
				// 从线程中拿连接对象
				conn = ds.getConnection();
				// 将连接对象添加到local中
				local.set(conn);
			}
			return conn;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
