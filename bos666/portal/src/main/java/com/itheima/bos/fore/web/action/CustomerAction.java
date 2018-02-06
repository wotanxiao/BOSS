package com.itheima.bos.fore.web.action;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.crm.domain.Customer;
import com.itheima.utils.MailUtils;
import com.itheima.utils.SmsUtils;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class CustomerAction extends ActionSupport implements ModelDriven<Customer> {

	private Customer model = new Customer();

	@Override
	public Customer getModel() {
		return model;
	}

	@Autowired
	private JmsTemplate jmsTemplate;

	@Action(value = "customerAction_sendSMS")
	public String sendSMS() throws IOException {

		// 随机验证码
		final String code = RandomStringUtils.randomNumeric(6);
		System.out.println("code: " + code);
		// 存入Session
		ServletActionContext.getRequest().getSession().setAttribute("serverCode", code);

		// 手机号,内容
		jmsTemplate.send("sms", new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				message.setString("tel", getModel().getTelephone());
				message.setString("code", code);
				return message;
			}
		});

		return NONE;
	}

	// 使用属性驱动获取验证码
	private String checkcode;

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Action(value = "customerAction_regist", results = {
			@Result(name = "success", location = "/signup-success.html", type = "redirect"),
			@Result(name = "error", location = "/signup-fail.html", type = "redirect") })
	public String regist() {
		// 比对验证码
		String serverCode = (String) ServletActionContext.getRequest().getSession().getAttribute("serverCode");
		if (StringUtils.isNotEmpty(serverCode) && StringUtils.isNotEmpty(checkcode) && checkcode.equals(serverCode)) {
			WebClient.create("http://localhost:8180/crm/webService/customerService/save")//
					.type(MediaType.APPLICATION_JSON)//
					.accept(MediaType.APPLICATION_JSON)//
					.post(model);

			// 把激活码存入Redis
			String activeCode = RandomStringUtils.randomNumeric(32);
			redisTemplate.opsForValue().set(getModel().getTelephone(), activeCode, 1, TimeUnit.DAYS);

			// 发送激活邮件
			final String emailBody = "感谢您注册速运快递,请在24小时内点击<a href='http://localhost:8280/portal/customerAction_active.action?activeCode="
					+ activeCode + "&telephone=" + getModel().getTelephone() + "'>本链接</a>激活您的帐号";
			//使用activemq发送注册激活邮件
			jmsTemplate.send("email", new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					MapMessage message = session.createMapMessage();
					message.setString("email",getModel().getEmail());
					message.setString("emailBody",emailBody);
					return message;
				}
			});

			/*MailUtils.sendMail(getModel().getEmail(), "激活邮件", emailBody);*/

			return SUCCESS;
		}

		return ERROR;
	}

	// 使用属性驱动获取激活码
	private String activeCode;

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	// 处理激活
	@Action(value = "customerAction_active", results = {
			@Result(name = "success", location = "/login.html", type = "redirect"),
			@Result(name = "actived", location = "/login.html", type = "redirect"),
			@Result(name = "error", location = "/signup-fail.html", type = "redirect") })
	public String active() {

		// Redis服务器中的激活码
		String serverCode = redisTemplate.opsForValue().get(getModel().getTelephone());
		if (StringUtils.isNotEmpty(serverCode) && StringUtils.isNotEmpty(activeCode) && serverCode.equals(activeCode)) {
			// 判断用户是否已经激活
			Customer customer = WebClient.create("http://localhost:8180/crm/webService/customerService/findByTelephone")//
					.type(MediaType.APPLICATION_JSON)//
					.accept(MediaType.APPLICATION_JSON)//
					.query("telephone", getModel().getTelephone()).get(Customer.class);

			// 用户已经激活
			if (customer != null && customer.getType() != null && customer.getType() == 1) {
				return "actived";
			}

			// 激活用户
			WebClient.create("http://localhost:8180/crm/webService/customerService/active")//
					.type(MediaType.APPLICATION_JSON)//
					.accept(MediaType.APPLICATION_JSON)//
					.query("telephone", getModel().getTelephone())//
					.put(null);

			return SUCCESS;
		}

		return ERROR;
	}

	@Action(value = "customerAction_login", results = {
			@Result(name = "success", location = "/index.html", type = "redirect"),
			@Result(name = "actived", location = "/login.html", type = "redirect"),
			@Result(name = "error", location = "/signup-fail.html", type = "redirect") })
	public String login() {

		String serverCode = (String) ServletActionContext.getRequest().getSession().getAttribute("validateCode");

		if (StringUtils.isNotEmpty(serverCode) && StringUtils.isNotEmpty(checkcode) && serverCode.equals(checkcode)) {
			Customer customer = WebClient.create("http://localhost:8180/crm/webService/customerService/login")//
					.type(MediaType.APPLICATION_JSON)//
					.accept(MediaType.APPLICATION_JSON)//
					.query("telephone", getModel().getTelephone())//
					.query("password", getModel().getPassword())//
					.get(Customer.class);

			if (customer != null && customer.getType() != null) {

				if (customer.getType() == 1) {
					// 登录成功
					return SUCCESS;
				} else {
					// 用户没有激活
					return "actived";
				}

			}
		}

		return ERROR;
	}

}
