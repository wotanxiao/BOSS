package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.service.base.StandardService;
import com.itheima.bos.web.action.CommonAction;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class StandardAction extends CommonAction<Standard> {

	public StandardAction() {
		super(Standard.class);
	}

	private static final long serialVersionUID = 5059676695054900556L;

	@Autowired
	private StandardService standardService;

	// 保存派送标准
	// Action中的value等价于以前struts.xml中<action>节点的name
	// Result中的name等价于以前struts.xml中<result>节点的name
	// Result中的location等价于以前struts.xml中<result>节点之间的内容
	@Action(value = "standardAction_save", results = {
			@Result(name = "success", location = "/pages/base/standard.html", type = "redirect") })
	public String save() {
		standardService.save(getModel());
		return SUCCESS;
	}

	// 分页查询,EasyUI所有的控件发请求的方式是AJAX,本方法返回的数据应该是JSON格式
	@Action(value = "standardAction_pageQuery")
	public String pageQuery() throws IOException {
		Sort sort = new Sort(Direction.DESC, "minWeight");
		// EasyUI的page是从1开始的, PageRequest的page是从0开始的,所以要做-1的操作
		Pageable pageable = new PageRequest(page - 1, rows, sort);
		// 调用业务层进行查询
		Page<Standard> page = standardService.findAll(pageable);

		page2json(page, null);
		return NONE;
	}

	// 查询所有的标准,提供给添加快递员页面使用
	@Action(value = "standard_findAll")
	public String findAll() throws IOException {
		Page<Standard> page = standardService.findAll(null);

		// 所有的标准
		List<Standard> content = page.getContent();

		list2json(content, null);
		return NONE;
	}

}
