package com.moonzero.shop.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moonzero.shop.utils.CommonUtil;

public class LoginCheckFilter implements Filter {

	// 重写doFilter方法，判断并过滤没有登陆直接方法购物车页面
	@Override
	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chain) throws IOException, ServletException {
		// 判读用户是否登录，如果没有登录，跳转到登录页面，如果登录放行
		// 过滤访问CartServlet和cart.jsp

		// 将父类对象转子类
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;

		// 判断用户是否登陆
		if (request.getSession().getAttribute(CommonUtil.LOGIN_USER) == null) {
			// 用户未登陆，跳转到登陆页面
			response.sendRedirect(request.getContextPath() + "/login.jsp");

		} else {
			// 用户已登陆，直接放行
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}
}
