package com.moonzero.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.moonzero.dao.IUserDao;

/**
 * �û��־ò�ʵ����2222
 * 
 * @author MoonZero
 */
// ��ͳд��
// @Component(value="userDao2")
// ����һ��д��
@Repository(value = "userDao2")
@Scope(value = "singleton")
public class UserDaoImpl2 implements IUserDao {

	@Override
	public void addUser(String name) {
		System.out.println(name + "====ִ�����û��־ò�ʵ����222222222��");
	}
}
