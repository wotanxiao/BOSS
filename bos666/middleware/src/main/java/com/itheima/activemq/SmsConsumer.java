package com.itheima.activemq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.itheima.utils.MailUtils;
import org.springframework.stereotype.Component;

@Component
public class SmsConsumer implements MessageListener {

	@Override
	public void onMessage(Message msg) {
		MapMessage message = (MapMessage) msg;
		try {
			String tel = message.getString("tel");
			String code = message.getString("code");

			//监听发送激活邮件状态
			String email = message.getString("email");
			String emailBody = message.getString("emailBody");

			System.out.println(tel + "=====" + code);
			// SmsUtils.sendSms(tel, code);

			String courier_tel = message.getString("courier_tel");
			String workbill_msg = message.getString("workbill_msg");

			//向快递员发送工单信息
			MailUtils.sendMail(courier_tel,"新的工单信息",workbill_msg);

			//使用工具类发送注册激活邮件
			MailUtils.sendMail(email,"注册激活邮件",emailBody);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
