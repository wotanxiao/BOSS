package com.itheima.bos.fore.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.take_delivery.PageBean;
import com.itheima.bos.domain.take_delivery.Promotion;
import com.opensymphony.xwork2.ActionSupport;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class PromotionAction extends ActionSupport {

	private int pageIndex;// 第几页
	private int pageSize;// 每一页显示多少条数据

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Action(value = "promotionAction_pageQuery")
	public String pageQuery() throws IOException {

		PageBean<Promotion> pageBean = WebClient
				.create("http://localhost:8080/bos_management_web/webService/promotionService/findAll")//
				.type(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON)//
				.query("pageIndex", pageIndex)//
				.query("pageSize", pageSize)//
				.get(PageBean.class);

		String json = JSONObject.fromObject(pageBean).toString();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(json);

		return NONE;
	}

}
