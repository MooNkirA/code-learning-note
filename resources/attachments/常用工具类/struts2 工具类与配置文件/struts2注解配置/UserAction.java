package com.moonzero.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.ActionContext;

/**
 * 注解配置测试
 * 
 * @author MoonZero
 */

// 继承的父包
@ParentPackage(value = "default")
// 指定命名空间
@Namespace(value = "/user")
// 将同一个类的所有的返回声明在类名上面，公共的
@Results(value = { @Result(name = "login", location = "/result.jsp"),
		@Result(name = "register", location = "/result.jsp") })
// 声明一个userAction对象
@Action(value = "userAction")
// 指定声明的拦截器
@InterceptorRef(value = "myStack")
public class UserAction {

	@Action(value = "login", results = { @Result(name = "login", location = "/result.jsp") })
	public String login() {
		System.out.println("进行登陆操作");
		ActionContext.getContext().put("info", "登陆成功~");
		return "login";
	}

	public String register() {
		System.out.println("进来执行注册操作");
		ActionContext.getContext().put("info", "注册成功~");
		return "register";
	}
}
