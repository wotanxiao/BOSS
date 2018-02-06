package com.itheima.bos.web.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.data.domain.Page;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

// 通用的Action
public class CommonAction<T> extends ActionSupport implements ModelDriven<T> {

	private T model;
	private Class<T> clazz;

	public CommonAction(Class<T> clazz) {
		this.clazz = clazz;
		try {
			model = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public T getModel() {
		return model;
	}

	protected int page;// 第几页
	protected int rows;// 每一页显示多少条数据
	// 使用属性驱动获取分页数据

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * 把page对象转为json数据
	 * 
	 * @param page
	 *            : Page对象
	 * @param jsonConfig
	 *            : 转换json的设置
	 */
	public void page2json(Page<T> page, JsonConfig jsonConfig) throws IOException {

		// 总数据条数
		long totalElements = page.getTotalElements();
		// 页面要显示的数据
		List<T> content = page.getContent();
		// 封装数据
		Map<String, Object> map = new HashMap<>();
		map.put("total", totalElements);
		map.put("rows", content);

		// JSONObject 转换对象和Map集合
		// JSONArray 转换数组和List集合

		// ServletContext servletContext = ServletActionContext.getServletContext();
		// servletContext.getRealPath("");
		// servletContext.getMimeType("");

		// 转换对象为json字符串
		String json;

		if (jsonConfig != null) {
			json = JSONObject.fromObject(map, jsonConfig).toString();
		} else {
			json = JSONObject.fromObject(map).toString();
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(json);
	}

	/**
	 * 把list集合转为json字符串
	 * 
	 * @param list
	 *            : 集合
	 * @param jsonConfig
	 *            : 转换json的设置
	 */
	public void list2json(List list, JsonConfig jsonConfig) throws IOException {

		String json;

		if (jsonConfig != null) {
			json = JSONArray.fromObject(list, jsonConfig).toString();
		} else {
			json = JSONArray.fromObject(list).toString();
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(json);
	}

}
