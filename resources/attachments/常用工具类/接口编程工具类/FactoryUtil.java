package com.moonzero.shop.utils;

import java.util.ResourceBundle;

/**
 * 创建动态类的实例对象
 * 
 * @author MoonZero
 */
public class FactoryUtil {

	private static ResourceBundle resourceBundle;

	static {
		// 自动加载src下面的impl.properties文件
		resourceBundle = ResourceBundle.getBundle("impl");
	}
	
	/**
	 * 根据类名(impl.properties中的key)创建实例对象
	 * @param className
	 * @return
	 */
	public static Object getImplObject(String className){
		
		// 获取要动态实例的类全名
		String classAllName = resourceBundle.getString(className);
		// 使用反射创建实例对象
		try {
			return Class.forName(classAllName).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
