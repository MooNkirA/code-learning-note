package com.moonzero.shop.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库连接工具类
 * 
 * @author MoonZero
 */
public class C3P0Util {
	// 创建数据库连接池对象
	private static DataSource ds = new ComboPooledDataSource();

	// 创建线程共享局部对象
	private static ThreadLocal<Connection> local = new ThreadLocal<Connection>();

	/**
	 * 开启事务
	 * 
	 * @throws SQLException
	 */
	public static void beginTransaction() throws SQLException {
		// 获取连接对象
		Connection conn = getConnection();
		// 开启事务
		if (conn != null) {
			conn.setAutoCommit(false);
		}
	}

	/**
	 * 提交事务
	 * 
	 * @throws SQLException
	 */
	public static void commitAndRelease() throws SQLException {
		// 获取连接对象
		Connection conn = getConnection();
		// 提交事务
		if (conn != null) {
			conn.commit();
			conn.close();
			local.remove();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		// 获取连接对象
		Connection conn = getConnection();
		// 回滚事务
		if (conn != null) {
			conn.rollback();
		}
	}

	/**
	 * 获取连接池对象
	 * 
	 * @return DataSource
	 */
	public static DataSource getDataSource() {
		return ds;
	}

	/**
	 * 获取数据库连接对象
	 * 
	 * @return Connection
	 */
	public static Connection getConnection() throws SQLException {
		// 从线程共享对象中获取
		Connection conn = local.get();
		// 判断连接对象是否为空
		if (conn == null) {
			conn = ds.getConnection();
			// 将连接对象放到线程共享对象中
			local.set(conn);
		}
		return conn;
	}

	/**
	 * 关闭连接数据库操作相关资源
	 * 
	 * @param rs
	 * @param stmt
	 * @param conn
	 * @throws SQLException
	 */
	public static void close(ResultSet rs, Statement stmt, Connection conn)
			throws SQLException {
		if (rs != null) {
			rs.close();
		}

		close(stmt, conn);
	}

	/**
	 * 关闭连接数据库操作相关资源
	 * 
	 * @param stmt
	 * @param conn
	 * @throws SQLException
	 */
	public static void close(Statement stmt, Connection conn)
			throws SQLException {
		if (stmt != null) {
			stmt.close();
		}

		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * 关闭Connection
	 * 
	 * @throws SQLException
	 */
	public static void closeConnection() throws SQLException {
		Connection conn = getConnection();
		if (conn != null) {
			conn.close();
		}
	}

}
