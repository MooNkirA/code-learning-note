package com.moonzero.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.moonzero.config.SpringConfiguration;
import com.moonzero.service.IUserService;

/**
 * ģ����ֲ�
 * 
 * @author MoonZero
 */
public class UserTest {
	public static void main(String[] args) {
		// ��ȡSpring��������,ָ������������
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		// ��ȡע�����ô����Ķ���
		IUserService us = (IUserService) context.getBean("userService");
		// ���ö��󷽷�
		us.addUser("ע�����");
	}
}
