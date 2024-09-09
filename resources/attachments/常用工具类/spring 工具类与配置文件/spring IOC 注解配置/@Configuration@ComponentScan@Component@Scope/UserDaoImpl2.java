package com.moonzero.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.moonzero.dao.IUserDao;

/**
 * 用户持久层实现类2222
 * 
 * @author MoonZero
 */
// 传统写法
// @Component(value="userDao2")
// 另外一种写法
@Repository(value = "userDao2")
@Scope(value = "singleton")
public class UserDaoImpl2 implements IUserDao {

	@Override
	public void addUser(String name) {
		System.out.println(name + "====执行了用户持久层实现类222222222！");
	}
}
