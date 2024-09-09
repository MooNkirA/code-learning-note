package com.moon.service.impl;

import com.moon.dao.IAccountDao;
import com.moon.entity.Account;
import com.moon.service.IAccountService;

/**
 * 业务逻辑层实现类
 * 
 * @author MoonZero
 */
public class AccountServiceImpl implements IAccountService {

	// 属性注入
	private IAccountDao dao;

	public void setDao(IAccountDao dao) {
		this.dao = dao;
	}

	/**
	 * 查询一个数据
	 */
	@Override
	public Account findById(Integer id) {
		return dao.findById(id);
	}

	/**
	 * 转账方法
	 */
	@Override
	public void transfer(String outName, String inName, Float money) {
		// 调用数据层根据名字查询方法
		Account outAccount = dao.findByName(outName);
		Account inAccount = dao.findByName(inName);

		// 转账，分别调用两个账号的update方法
		outAccount.setMoney(outAccount.getMoney() - money);
		inAccount.setMoney(inAccount.getMoney() + money);

		dao.updateAccount(outAccount);
		// 模拟异常
		// System.out.println(0 / 0);
		dao.updateAccount(inAccount);

		System.out.println("转账成功！！！");
	}

}
