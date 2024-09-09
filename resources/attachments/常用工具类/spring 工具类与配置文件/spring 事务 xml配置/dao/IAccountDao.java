package com.moon.dao;

import com.moon.entity.Account;

/**
 * 账户持久层接口
 * 
 * @author MoonZero
 *
 */
public interface IAccountDao {

	/**
	 * 根据id查询一个对象
	 * 
	 * @param id
	 * @return
	 */
	public Account findById(Integer id);

	/**
	 * 根据名称查询一个对象
	 * 
	 * @param name
	 * @return
	 */
	public Account findByName(String name);

	/**
	 * 更新账户信息
	 * 
	 * @param a
	 */
	public void updateAccount(Account a);
}
