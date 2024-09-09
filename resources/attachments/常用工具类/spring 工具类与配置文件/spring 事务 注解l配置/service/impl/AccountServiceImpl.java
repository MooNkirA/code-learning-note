package com.moon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moon.dao.IAccountDao;
import com.moon.entity.Account;
import com.moon.service.IAccountService;

/**
 * 业务逻辑层实现类
 * 
 * @author MoonZero
 */
// 配置注解让spring容器管理
@Service("as")
// 开启全局事务
// 默认值@Transactional(transactionManager="transactionManager",propagation=Propagation.REQUIRED,readOnly=false,isolation=Isolation.DEFAULT)
@Transactional
public class AccountServiceImpl implements IAccountService {

	// 使用注解属性注入
	@Autowired
	private IAccountDao dao;

	/**
	 * 查询一个数据
	 */
	@Override
	// 开启局部事务。三个位置的优先级：方法>类>接口。
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
