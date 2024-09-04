package com.moon.service;

import com.moon.entity.Account;

/**
 * 业务层接口
 * 
 * @author MoonZero
 */
public interface IAccountService {
	/**
	 * 根据id查询账户
	 * 
	 * @param id
	 * @return
	 */
	public Account findById(Integer id);

	/**
	 * 转账方法
	 * 
	 * @param outName
	 *            转出账户
	 * @param inName
	 *            转入账户
	 * @param money
	 *            转账金额
	 */
	public void transfer(String outName, String inName, Float money);
}
