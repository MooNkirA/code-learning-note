package com.moonzero.shop.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 解决全站乱码的过滤器
 * 
 * @author MoonZero
 * 
 */
public class CharacterFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chain) throws IOException, ServletException {
		// 将请求与响应转成子类
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		
		// 解决响应乱码的问题
		response.setContentType("text/html;charset=utf-8");

		// 获取提交的数据的方法
		String method = request.getMethod();
		if (method.equalsIgnoreCase("post")) {
			// 使用post方式提交数据
			request.setCharacterEncoding("UTF-8");
		} else {
			// 使用get方法提交数据,使用装饰者模式解决乱码问题
			// 将增强后的对象再赋值给父类request
			MyRequest myRequest = new MyRequest(request);
			request = myRequest;
		}

		// 处理后放行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}

// 使用装饰者设计模式增加request的getParameter()方法
// 1 创建一个类，继承于具体要增强的类。抽象类就是：HttpServletRequest，实现类HttpServletRequestWrapper
class MyRequest extends HttpServletRequestWrapper {
	// 2 在装饰类中有一个抽象对象的成员变量，通过构造方法传递进来。
	private HttpServletRequest request;

	public MyRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	// 重写并增强getParameter()方法
	@Override
	public String getParameter(String name) {
		// 先获得提交的乱码的字符
		String value = request.getParameter(name);
		// 再判断获得的字符是否为空并且不为空字符串
		if (value != null && !"".equals(value)) {
			try {
				value = new String(value.getBytes("iso8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return value;
	}
}
