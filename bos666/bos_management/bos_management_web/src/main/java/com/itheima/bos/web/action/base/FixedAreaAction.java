package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.itheima.bos.domain.base.Courier;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.FixedArea;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.service.base.FixedAreaService;
import com.itheima.bos.web.action.CommonAction;
import com.itheima.crm.domain.Customer;

import net.sf.json.JsonConfig;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class FixedAreaAction extends CommonAction<FixedArea> {

	public FixedAreaAction() {
		super(FixedArea.class);
	}

	@Autowired
	private FixedAreaService fixedAreaService;

	@Action(value = "fixedAreaAction_save", results = {
			@Result(name = "success", location = "/pages/base/fixed_area.html", type = "redirect") })
	public String save() {
		fixedAreaService.save(getModel());
		return SUCCESS;
	}

	@Action(value = "fixedAreaAction_pageQuery")
	public String pageQuery() throws IOException {

		Pageable pageable = new PageRequest(page - 1, rows);

		// 调用业务层进行查询
		Page<FixedArea> page = fixedAreaService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "subareas", "couriers" });
		page2json(page, jsonConfig);
		return NONE;
	}

	// 向CRM发起请求,查询未关联的客户
	@Action(value = "fixedAreaAction_findCustomersUnAssociated")
	public String findCustomersUnAssociated() throws IOException {

		List<Customer> list = (List<Customer>) WebClient
				.create("http://localhost:8180/crm/webService/customerService/findCustomersUnAssociated")//
				.type(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON)//
				.getCollection(Customer.class);

		list2json(list, null);

		return NONE;
	}

	// 向CRM发起请求,查询关联到指定定区的客户
	@Action(value = "fixedAreaAction_findCustomersAssociated2FixedArea")
	public String findCustomersAssociated2FixedArea() throws IOException {

		List<Customer> list = (List<Customer>) WebClient
				.create("http://localhost:8180/crm/webService/customerService/findCustomersAssociated2FixedArea")//
				.type(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON)//
				.query("fixedAreaId", getModel().getId()).getCollection(Customer.class);

		list2json(list, null);

		return NONE;
	}

	// 使用属性驱动获取被选中的客户的id
	private Long[] customerIds;

	public void setCustomerIds(Long[] customerIds) {
		this.customerIds = customerIds;
	}

	// 向CRM发起请求,关联客户到指定的定区
	@Action(value = "fixedAreaAction_assignCustomers2FixedArea", results = {
			@Result(name = "success", location = "/pages/base/fixed_area.html", type = "redirect") })
	public String assignCustomers2FixedArea() throws IOException {

		WebClient.create("http://localhost:8180/crm/webService/customerService/assignCustomers2FixedArea")//
				.type(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON)//
				.query("fixedAreaId", getModel().getId())//
				.query("customerIds", customerIds)//
				.put(null);

		return SUCCESS;
	}

	// 使用属性驱动获取快递员和时间的ID
	private Long courierId;
	private Long takeTimeId;

	public void setCourierId(Long courierId) {
		this.courierId = courierId;
	}

	public void setTakeTimeId(Long takeTimeId) {
		this.takeTimeId = takeTimeId;
	}

	// 定区关联快递员时间
	@Action(value = "fixedAreaAction_associationCourierToFixedArea", results = {
			@Result(name = "success", location = "/pages/base/fixed_area.html", type = "redirect") })
	public String associationCourierToFixedArea() throws IOException {

		fixedAreaService.associationCourierToFixedArea(getModel().getId(), courierId, takeTimeId);

		return SUCCESS;
	}

	// 属性驱动获取分区的ID
	private Long[] subAreaIds;

	public void setSubAreaIds(Long[] subAreaIds) {
		this.subAreaIds = subAreaIds;
	}

	// 关联分区
	@Action(value = "fixedAreaAction_assignSubArea2FixedArea", results = {
			@Result(name = "success", location = "/pages/base/fixed_area.html", type = "redirect") })
	public String assignSubArea2FixedArea() throws IOException {

		fixedAreaService.assignSubArea2FixedArea(getModel().getId(), subAreaIds);

		return SUCCESS;
	}

	
	
	//使用属性驱动获取subareaId
	private String subareaId;
	public void setSubareaId(String subareaId) {
        this.subareaId = subareaId;
    }
	/**
	 * 
	 * @return关联分区
	 * @throws IOException
	 */
	
	@Action(value = "fixedAreaAction_findSubArea")
    public String findSubArea() throws IOException {

	  
	    List<SubArea> list = fixedAreaService.findSubArea(subareaId);

      System.out.println("=========================华丽的分割线=======================");
      JsonConfig jsonConfig = new JsonConfig();
      jsonConfig.setExcludes(new String[]{"subareas","couriers"});
      list2json(list, jsonConfig);
        return NONE;
    }

	@Action(value = "fixedAreaAction_findCourier")
	public String findCourier() throws IOException {

		System.out.println("");
		List<Courier> list = fixedAreaService.findCourier(getModel().getId());

		System.out.println("=========================华丽的分割线=======================");
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[]{"fixedAreas","takeTime"});
		list2json(list, jsonConfig);
		return NONE;
	}
	
}
