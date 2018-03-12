package com.example.qiangke.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Created by wenxiangzhou214164 on 2017/9/4
 */
public class MailUtil {

    public static void sendMail(String message) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

        // 设定mail server
        senderImpl.setHost("smtp.163.com");

        // 建立邮件消息
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //设置收件人，寄件人 用数组发送多个邮件
        String[] array = new String[] {"13051570207@163.com","17125682@bjtu.edu.cn"};
        mailMessage.setTo(array);
        mailMessage.setFrom("13051570207@163.com");
        mailMessage.setSubject("抢课");
        mailMessage.setText(message);

        senderImpl.setUsername("13051570207@163.com"); // 根据自己的情况,设置username
        senderImpl.setPassword("sin5163"); // 根据自己的情况, 设置password
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", "25000");
        senderImpl.setJavaMailProperties(prop);
        // 发送邮件
        senderImpl.send(mailMessage);

        System.out.println("邮件发送成功..");
    }

    public static void main(String[] args) {
        sendMail("test");
    }

}
