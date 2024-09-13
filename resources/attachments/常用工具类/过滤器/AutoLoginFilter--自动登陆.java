package com.moonzero.shop.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.moonzero.shop.entity.User;
import com.moonzero.shop.service.UserService;
import com.moonzero.shop.utils.CommonUtil;

/**
 * 自动登录过滤器
 * 
 * @author MoonZero
 */
public class AutoLoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chain) throws IOException, ServletException {
		// 将父类转成子类
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;

		// 从会话域获取用户的登录数据
		HttpSession session = request.getSession();
		User dbUser = (User) session.getAttribute(CommonUtil.LOGIN_USER);

		// 判断获取的用户对象是否为空，用户数据为空，则实现自动登陆
		if (dbUser == null) {
			// 获取浏览器中的cookie数据
			Cookie[] cookies = request.getCookies();
			// 定义用户和用户密码
			String username = null;
			String pwd = null;

			// 判断cookie数组有效性，并获取用户名和密码
			if (cookies != null && cookies.length > 0) {
				// 遍历数组
				for (Cookie cookie : cookies) {
					if ("name".equals(cookie.getName())) {
						username = cookie.getValue();
					}
					if ("pwd".equals(cookie.getName())) {
						pwd = cookie.getValue();
					}
				}
			}

			// 判断获取的用户名和密码的有效性
			if (username != null && pwd != null) {
				// 调用用户业务逻辑层登陆方法，实现自动登陆
				User user = new User();
				user.setUsername(username);
				user.setPassword(pwd);

				try {
					dbUser = new UserService().login(user);

					// 判断从数据库中返回的用户对象是否为空
					if (dbUser != null) {
						// 将用户的登陆信息存储到会话域中
						session.setAttribute(CommonUtil.LOGIN_USER, dbUser);
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

			}
		}

		// 放行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
