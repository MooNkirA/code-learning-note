package com.moon.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * hibernate 工具类
 * 
 * @author MoonZero
 */
public class HibernateUtil {
	// 使用static的关键字，整个项目共享一个对象。
	// 如果一个项目里面出现多个连接池，有可能导致事务处理不同步。事务同步提交的前提是同一个连接。
	// 创建静态Session工厂成员变量
	public static SessionFactory sessionFactory;
	// 创建线程共享对象
	public static ThreadLocal<Session> local = new ThreadLocal<Session>();

	// 使用静态获得会话工厂
	static {
		// 1.创建Configuration对象
		Configuration cfg = new Configuration();
		// 2.读取配置文件，获得框架信息，默认读取classpath根目录hibernate.cfg.xml
		cfg.configure();
		// 3.获得会话工厂,如果要连接数据库必须需要连接数据库的信息（四要素）
		// 获得会话工厂必须要在读取配置文件之后
		sessionFactory = cfg.buildSessionFactory();
	}

	// 获取会话操作对象静态方法
	public static Session getSession() {
		// 先判断共享线程对象是否有值
		if (local.get() == null) {
			// 使用工厂获取Session对象
			Session session = sessionFactory.openSession();
			local.set(session);
		}
		// 返回共享线程对象中值
		return local.get();
	}

	// 关闭会话操作对象和清空共享线程对象
	public static void close() {
		// 判断共享线程对象是否有值
		if (local.get() != null) {
			// 关闭资源
			local.get().close();
			local.remove();
		}
	}

	// 开启事务
	public static void beginTransaction() {
		getSession().beginTransaction();
	}

	// 提交事务
	public static void commit() {
		getSession().beginTransaction().commit();
	}

	// 回滚事务
	public static void rollback() {
		getSession().beginTransaction().rollback();
	}

	// 测试是否连接成功
	public static void main(String[] args) {
		// 获取session操作对象
		Session session = getSession();
		System.out.println(session);
		close();
	}
}