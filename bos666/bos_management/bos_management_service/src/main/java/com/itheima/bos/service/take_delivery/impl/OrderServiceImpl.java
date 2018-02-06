package com.itheima.bos.service.take_delivery.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import com.itheima.bos.domain.take_delivery.WayBill;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.AreaRepository;
import com.itheima.bos.dao.base.FixedAreaRepository;
import com.itheima.bos.dao.take_delivery.OrderRepository;
import com.itheima.bos.dao.take_delivery.WorkbillRepository;
import com.itheima.bos.domain.base.Area;
import com.itheima.bos.domain.base.Courier;
import com.itheima.bos.domain.base.FixedArea;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.domain.take_delivery.Order;
import com.itheima.bos.domain.take_delivery.WorkBill;
import com.itheima.bos.service.take_delivery.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	@Autowired
	private WorkbillRepository workbillRepository;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void save(Order order) {

		// 把发件区域和收件区域转换成持久态对象
		// 瞬时态
		Area sendArea = order.getSendArea();
		if (sendArea != null) {
			Area sendAreaDB = areaRepository.findByProvinceAndCityAndDistrict(sendArea.getProvince(),
					sendArea.getCity(), sendArea.getDistrict());

			order.setSendArea(sendAreaDB);
		}

		Area recArea = order.getRecArea();
		if (recArea != null) {
			Area recAreaDB = areaRepository.findByProvinceAndCityAndDistrict(recArea.getProvince(), recArea.getCity(),
					recArea.getDistrict());

			order.setRecArea(recAreaDB);
		}
		// 保存订单
		order.setOrderNum(UUID.randomUUID().toString());
		order.setOrderTime(new Date());
		orderRepository.save(order);
		// 实现分单的功能
		// 1.根据发件地址完全匹配. 前提:客户A要和定区B关联,定区B要和快递员C关联
		// 发件人填写的详细地址
		String sendAddress = order.getSendAddress();
		String fixedAreaId = WebClient
				.create("http://localhost:8180/crm/webService/customerService/findFixedAreaIdByAddress")//
				.type(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON)//
				.query("address", sendAddress).get(String.class);

		if (StringUtils.isNotEmpty(fixedAreaId)) {
			// 获取定区
			FixedArea fixedArea = fixedAreaRepository.findOne(Long.parseLong(fixedAreaId));
			// 和当前定区关联的快递员,根据上下班时间和取派能力查找一个快递员
			Set<Courier> couriers = fixedArea.getCouriers();
			if (!couriers.isEmpty()) {
				Iterator<Courier> iterator = couriers.iterator();
				Courier courier = iterator.next();
				// 为订单指定快递员
				order.setCourier(courier);
				// 设置工单信息
				final WorkBill workBill = new WorkBill();
				workBill.setAttachbilltimes(0);
				workBill.setBuildtime(new Date());
				workBill.setCourier(courier);
				workBill.setOrder(order);
				workBill.setPickstate("新单");
				workBill.setRemark(order.getRemark());
				workBill.setSmsNumber("123");
				workBill.setType("新");

				workbillRepository.save(workBill);
				// 给快递员发个短信,推送一个消息到PDA
				System.out.println("有一个新的订单,快去收货了....");

				//使用activemq发送工单邮件
				jmsTemplate.send("workbill", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						MapMessage message = session.createMapMessage();
						//获取快递员手机号
						String telephone = workBill.getCourier().getTelephone();
						message.setString("courier_tel",telephone);
						message.setString("workbill_msg","有一个新的订单,快去收货了........");

						return message;
					}
				});

				order.setOrderType("自动分单");
				return;
			}
		}
		// 2.根据分区关键字和辅助关键字
		sendArea = order.getSendArea();
		if (sendArea != null) {
			// 获取分区
			Set<SubArea> subareas = sendArea.getSubareas();
			// 遍历分区
			for (SubArea subArea : subareas) {
				// 获取关键字和辅助关键字
				String keyWords = subArea.getKeyWords();
				String assistKeyWords = subArea.getAssistKeyWords();
				// 如果地址中包含关键字获取辅助关键字,就认为找到了区域
				if (sendAddress.contains(keyWords) || sendAddress.contains(assistKeyWords)) {
					FixedArea fixedArea = subArea.getFixedArea();
					// 和当前定区关联的快递员,根据上下班时间和取派能力查找一个快递员
					Set<Courier> couriers = fixedArea.getCouriers();
					if (!couriers.isEmpty()) {
						Iterator<Courier> iterator = couriers.iterator();
						Courier courier = iterator.next();
						// 为订单指定快递员
						order.setCourier(courier);
						// 设置工单信息
						final WorkBill workBill = new WorkBill();
						workBill.setAttachbilltimes(0);
						workBill.setBuildtime(new Date());
						workBill.setCourier(courier);
						workBill.setOrder(order);
						workBill.setPickstate("新单");
						workBill.setRemark(order.getRemark());
						workBill.setSmsNumber("123");
						workBill.setType("新");

						workbillRepository.save(workBill);
						// 给快递员发个短信,推送一个消息到PDA
						System.out.println("有一个新的订单,快去收货了....");

						//使用activemq发送工单邮件
						jmsTemplate.send("workbill", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								MapMessage message = session.createMapMessage();
								//获取快递员手机号
								String telephone = workBill.getCourier().getTelephone();
								message.setString("courier_tel",telephone);
								message.setString("workbill_msg","有一个新的订单,快去收货了........");

								return message;
							}
						});
						order.setOrderType("自动分单");
						return;
					}
				}
			}
		}

		// 自动分单失败,进行人工分单
		order.setOrderType("人工分单");
	}

}
