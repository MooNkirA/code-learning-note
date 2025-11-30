package com.moonzero.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * 方法拦截需要继承方法拦截器类MethodFilterInterceptor
 * 
 * @author MoonZero
 */
public class VaildataInterceptor extends MethodFilterInterceptor {
	private static final long serialVersionUID = 1L;

	/**
	 * 重写拦截方法
	 */
	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		// 表示执行action前要执行的代码
		System.out.println("-----Action方法执行之前的代码-----");

		// 调用Action。返回的是视图映射字符串
		String result = invocation.invoke();
		// 表示执行完action后执行的代码
		System.out.println("-----Action方法执行之后代码-----");
		// 返回视图映射字符串
		return result;
	}
}
