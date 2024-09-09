package com.moonzero.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * 获取JPA操作对象工具类
 * 
 * @author MoonZero
 */
public class JPAUtil {
	// 获取唯一的实体管理工厂
	private static EntityManagerFactory emf;
	// 创建线程共享对象
	private static ThreadLocal<EntityManager> local = new ThreadLocal<EntityManager>();

	// 创建静态代码块，类加载时创建实体管理工厂
	static {
		// 读取配置文件,参数persistenceUnitName是配置文件中persistence-unit的name
		emf = Persistence.createEntityManagerFactory("crm");
	}

	// 获取操作对象的静态方法
	public static EntityManager getEntityManager() {
		// 判断线程共享对象是否有值
		if (local.get() == null) {
			EntityManager em = emf.createEntityManager();
			local.set(em);
		}
		return local.get();
	}

	// 关闭实体管理对象静态方法
	public static void close() {
		// 判断线程共享是否有值
		if (local.get() != null) {
			local.get().close();
			local.remove();
		}
	}

	// 测试是否连接成功
	public static void main(String[] args) {
		EntityManager em = JPAUtil.getEntityManager();
		System.out.println(em);
		em.close();
		emf.close();
	}
}