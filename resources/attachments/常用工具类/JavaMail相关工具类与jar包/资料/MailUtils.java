package com.itheima.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {

	/*
	 * email:收件人
	 * emailMsg：发送的邮件内容
	 * subject:邮件主题
	 */
	public static void sendMail(String email, String emailMsg,String subject){
		
		try {
			// 1.1)创建Properties对象，以指定主机(mail.smtp.host=localhost)和是否验证(mail.smtp.auth=true)。
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "SMTP");//发邮件的协议
			props.setProperty("mail.host", "localhost");//发邮件的邮箱服务器地址
			props.setProperty("mail.smtp.auth", "true");// 指定验证为true  主要验证发邮件的用户名和密码

			// 1.2)创建Authenticator验证器抽象类，重写方法getPasswordAuthentication()，返回它的子类：PasswordAuthentication()，指定用户名和密码进行验证。
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					/*
					 * 第一个参数 发邮件的用户名
					 * 第二个参数 用户名对应的密码
					 */
					return new PasswordAuthentication("admin", "12345");
				}
			};

			// 1.3) 创建Session对象，传入Properties和Authenticator的参数
			Session session = Session.getInstance(props, auth);

			// 2.1) 创建MimeMessage对象
			Message message = new MimeMessage(session);
			// 2.2) 设置发件人，与登录的用户名一致，需要使用到InternetAddress类型
			message.setFrom(new InternetAddress("admin@itheima.com"));
			// 2.3) 设置收件人，指定收件人的类型：RecipientType.TO 发送  CC抄送  BCC密送
			message.setRecipient(RecipientType.TO, new InternetAddress(email));
			// 2.4) 设置邮件的主题
			message.setSubject(subject);
			// 2.5) 设置邮件的正文，需要指定内容类型和编码
			message.setContent(emailMsg, "text/html;charset=utf-8");
			// 3) 创建 Transport用于将邮件发送
			Transport.send(message);
			
			System.out.println("给"+email+"发送邮件成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("给"+email+"发送邮件失败");
			throw new RuntimeException(e);
		} 
	}
}
