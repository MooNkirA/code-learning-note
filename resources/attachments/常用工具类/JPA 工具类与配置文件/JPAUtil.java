package com.moonzero.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * ��ȡJPA�������󹤾���
 * 
 * @author MoonZero
 */
public class JPAUtil {
	// ��ȡΨһ��ʵ�������
	private static EntityManagerFactory emf;
	// �����̹߳������
	private static ThreadLocal<EntityManager> local = new ThreadLocal<EntityManager>();

	// ������̬����飬�����ʱ����ʵ�������
	static {
		// ��ȡ�����ļ�,����persistenceUnitName�������ļ���persistence-unit��name
		emf = Persistence.createEntityManagerFactory("crm");
	}

	// ��ȡ��������ľ�̬����
	public static EntityManager getEntityManager() {
		// �ж��̹߳�������Ƿ���ֵ
		if (local.get() == null) {
			EntityManager em = emf.createEntityManager();
			local.set(em);
		}
		return local.get();
	}

	// �ر�ʵ��������̬����
	public static void close() {
		// �ж��̹߳����Ƿ���ֵ
		if (local.get() != null) {
			local.get().close();
			local.remove();
		}
	}

	// �����Ƿ����ӳɹ�
	public static void main(String[] args) {
		EntityManager em = JPAUtil.getEntityManager();
		System.out.println(em);
		em.close();
		emf.close();
	}
}