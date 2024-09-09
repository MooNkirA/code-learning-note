package com.moonzero.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.moonzero.config.SpringConfiguration;
import com.moonzero.service.IUserService;

/**
 * 模拟表现层
 * 
 * @author MoonZero
 */
public class UserTest {
	public static void main(String[] args) {
		// 获取Spring容器对象,指定加载配置类
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		// 获取注解配置创建的对象
		IUserService us = (IUserService) context.getBean("userService");
		// 调用对象方法
		us.addUser("注解对象");
	}
}
