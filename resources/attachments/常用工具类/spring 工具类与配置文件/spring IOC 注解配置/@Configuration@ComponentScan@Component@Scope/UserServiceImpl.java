package com.moonzero.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moonzero.dao.IUserDao;
import com.moonzero.service.IUserService;

/**
 * �û�ҵ���߼�����ʵ��
 * 
 * @author MoonZero
 *
 */
// ʹ��@Component ע�����ù������Դ,�൱����xml������һ��Bean��ǩ
// ���value��д��Ĭ��Ҳ��ǰ����Ϊid��������ĸΪСд
@Component(value = "userService")
// ����һ��д��
// @Service(value = "userService")
@Scope(value = "singleton")
public class UserServiceImpl implements IUserService {

	// ʹ��bean����ע��
	@Resource(name = "userDao2")
	private IUserDao dao;

	// ע��������ݺ�String����
	@Value(value = "com.mysql.jdbc.Driver")
	private String driver;
	@Value(value = "24")
	private Integer count;

	@Override
	public void addUser(String name) {
		dao.addUser(name);
		// �����Ӧ������ֵ
		System.out.println("bean����ע�룺" + dao);
		System.out.println("String��������ע�룺" + driver);
		System.out.println("����������������ע�룺" + count);
	}
}
