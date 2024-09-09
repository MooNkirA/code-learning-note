package com.moonzero.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moonzero.dao.IUserDao;
import com.moonzero.service.IUserService;

/**
 * 用户业务逻辑层现实类
 * 
 * @author MoonZero
 *
 */
// 使用@Component 注解配置管理的资源,相当于在xml配置了一个Bean标签
// 如果value不写，默认也当前类名为id，且首字母为小写
@Component(value = "userService")
// 另外一种写法
// @Service(value = "userService")
@Scope(value = "singleton")
public class UserServiceImpl implements IUserService {

	// 使用bean属性注入
	@Resource(name = "userDao2")
	private IUserDao dao;

	// 注入基本数据和String类型
	@Value(value = "com.mysql.jdbc.Driver")
	private String driver;
	@Value(value = "24")
	private Integer count;

	@Override
	public void addUser(String name) {
		dao.addUser(name);
		// 输入对应的属性值
		System.out.println("bean属性注入：" + dao);
		System.out.println("String类型属性注入：" + driver);
		System.out.println("基本数据类型属性注入：" + count);
	}
}
