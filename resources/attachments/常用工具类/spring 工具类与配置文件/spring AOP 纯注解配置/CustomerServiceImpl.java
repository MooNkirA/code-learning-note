package com.moonzero.service.impl;

import org.springframework.stereotype.Service;

import com.moonzero.service.ICustomerService;

/**
 * 客户业务逻辑层实现类
 * 
 * @author MoonZero
 */
// 使用注解配置让spring容器管理
@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {

	/**
	 * 增加客户
	 */
	// @Override
	public void saveCustomer() {
		System.out.println("调用持久层方法，保存客户");
		// 模拟异常
		// int i = 10 / 0;
	}

	/**
	 * 更新客户
	 */
	// @Override
	public void updateCustomer(int i) {
		System.out.println("调用持久层方法，更新编号 " + i + " 的客户");
	}

}
