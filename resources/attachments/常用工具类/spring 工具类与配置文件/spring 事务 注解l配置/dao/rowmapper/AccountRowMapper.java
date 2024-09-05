package com.moon.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.moon.entity.Account;

/**
 * 定义jdbcTemplate返回结果实现类
 */
public class AccountRowMapper implements RowMapper<Account> {

	/**
	 * 结果集有几条数据，就执行几次此方法
	 * 		ResultSet：是查询一行的返回的结果集
	 * 		rowNum：这一行结果集，所在集合中的下标(索引)
	 */
	@Override
	public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
		// 根据列名获取值
		Account account = new Account(rs.getInt("id"), rs.getString("name"), rs.getFloat("money"));
		// 根据列索引获取值
		// Account account = new Account(rs.getInt(1), rs.getString(2), rs.getFloat(3));
		return account;
	}

}
