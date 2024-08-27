package com.moonzero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

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
// ��������������
@Import({ JdbcConfig.class, DS.class })
// ����properties�����ļ�
@PropertySource(value = { "classpath:/com/moonzero/config/jdbc.properties" })
public class SpringConfiguration {
	/**
	 * @PropertySource �� ���������������ļ� value�����Ծ��������ļ���λ�� 
	 * 		1�������spring4.3�汾֮�������Ϳ���ע����
	 *      2�������spring4.3�汾֮ǰ������Ҫһ��ռλ���࣬����࣬û��ʲô���ã�
	 *         ֻ�Ǹ���spring��ܣ����ǿ���Spring��El���ʽ ��ռλ���࣬��Ҫ����spring��������
	 */
	// ����ռλ���࣬4.3�汾����Ҫ�ṩ��Ĭ�Ͽ���
	@Bean(name = "pspc")
	public PropertySourcesPlaceholderConfigurer getPSPC() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
