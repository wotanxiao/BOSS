package com.itheima.activemq;

import com.itheima.utils.MailUtils;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class EmailConsumer implements MessageListener {

    @Override
    public void onMessage(Message msg) {

        MapMessage message = (MapMessage) msg;
        try {
            String tel = message.getString("tel");
            String email = message.getString("email");
            String activeCode = message.getString("activeCode");

            String emailBody = "感谢您注册速运快递,请在24小时内点击<a href='http://localhost:8280/portal/customerAction_active.action?activeCode="
                    + activeCode + "&telephone=" + tel + "'>本链接</a>激活您的帐号";
            MailUtils.sendMail(email, "激活邮件", emailBody);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
