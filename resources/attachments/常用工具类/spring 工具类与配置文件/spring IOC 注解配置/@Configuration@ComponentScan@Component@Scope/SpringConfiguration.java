package com.moonzero.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ����bean.xml�ļ���������
 * 
 * @author MoonZero
 */
// ����ָ����ǰ����һ�������࣬��Ӹ����ϼ���ע�⡣��ȡ������@ComponentScanע���ʼ��spring������
@Configuration
// ����ָ��spring�ڳ�ʼ������ʱҪɨ��İ�
// basePackages������ָ��Ҫɨ��İ�
// @ComponentScan(basePackages = {"com.moonzero"})
// ��valueһ�������Բ�ʡ�Բ�д���������ֻ��һ����{}Ҳ����ʡ��
@ComponentScan("com.moonzero")
public class SpringConfiguration {

}
