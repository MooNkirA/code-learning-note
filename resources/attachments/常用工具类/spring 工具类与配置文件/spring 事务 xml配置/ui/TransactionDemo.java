package com.moon.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.moon.service.IAccountService;

public class TransactionDemo {
	public static void main(String[] args) {
		// 创建spring容器操作对象
		ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
		// 根据id获取对象
		IAccountService jt = (IAccountService) context.getBean("as");

		// 调用查询方法
		System.out.println(jt.findById(1));
		// 调用转账方法
		jt.transfer("敌法师", "露娜", 100F);
	}
}