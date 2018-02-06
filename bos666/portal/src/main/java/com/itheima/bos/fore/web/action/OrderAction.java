package com.itheima.bos.fore.web.action;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Area;
import com.itheima.bos.domain.take_delivery.Order;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class OrderAction extends ActionSupport implements ModelDriven<Order> {

	private Order model = new Order();

	@Override
	public Order getModel() {
		return model;
	}

	// 使用属性驱动获取发件人和收件人的省市区信息
	private String sendAreaInfo;
	private String recAreaInfo;

	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}

	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}

	@Action(value = "orderAction_add", results = {
			@Result(name = "success", location = "/index.html", type = "redirect"),
			@Result(name = "error", location = "/signup-fail.html", type = "redirect") })
	public String save() {

		if (StringUtils.isNotEmpty(sendAreaInfo)) {
			String[] split = sendAreaInfo.split("/");
			String province = split[0];
			String city = split[1];
			String district = split[2];

			province = province.substring(0, province.length() - 1);
			city = city.substring(0, city.length() - 1);
			district = district.substring(0, district.length() - 1);

			Area area = new Area();
			area.setProvince(province);
			area.setCity(city);
			area.setDistrict(district);

			model.setSendArea(area);
		}

		if (StringUtils.isNotEmpty(recAreaInfo)) {
			String[] split = recAreaInfo.split("/");
			String province = split[0];
			String city = split[1];
			String district = split[2];

			province = province.substring(0, province.length() - 1);
			city = city.substring(0, city.length() - 1);
			district = district.substring(0, district.length() - 1);

			Area area = new Area();
			area.setProvince(province);
			area.setCity(city);
			area.setDistrict(district);

			model.setRecArea(area);
		}

		WebClient//
				.create("http://localhost:8080/bos_management_web/webService/orderService/save")//
				.type(MediaType.APPLICATION_JSON)// 指定传输给对方的数据格式
				.post(model);

		return SUCCESS;
	}
}
