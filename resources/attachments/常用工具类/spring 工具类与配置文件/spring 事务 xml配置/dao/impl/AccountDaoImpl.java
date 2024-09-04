package com.moon.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.moon.dao.IAccountDao;
import com.moon.dao.rowmapper.AccountRowMapper;
import com.moon.entity.Account;

public class AccountDaoImpl implements IAccountDao {

	// 定义JdbcTemplate对象，使用属性注入方式创建
	private JdbcTemplate jt;

	public void setJt(JdbcTemplate jt) {
		this.jt = jt;
	}

	@Override
	public Account findById(Integer id) {
		// 查询sql语句
		String sql = "select * from account where id=?";
		// 使用jdbcTemplate对象查询
		List<Account> list = jt.query(sql, new AccountRowMapper(), id);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public Account findByName(String name) {
		// 查询sqql语句
		String sql = "select * from account where name=?";
		// 使用jdbcTemplate对象查询
		List<Account> list = jt.query(sql, new AccountRowMapper(), name);

		// 判断返回结果是否为空
		if (list.isEmpty()) {
			// 返回空值
			return null;
		}

		// 判断返回结果是否是多个值
		if (list.size() > 1) {
			throw new RuntimeException("查询结果不唯一");
		}
		// 返回查询结果
		return list.get(0);
	}

	@Override
	public void updateAccount(Account a) {
		// 更新sql语句
		String sql = "update account set money=? where id=?";
		// 使用jdbcTemplate对象更新数据
		jt.update(sql, a.getMoney(), a.getId());
	}
}
