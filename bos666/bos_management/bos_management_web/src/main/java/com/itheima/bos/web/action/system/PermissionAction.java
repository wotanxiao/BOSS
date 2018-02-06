package com.itheima.bos.web.action.system;

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
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.Permission;
import com.itheima.bos.service.system.PermissionService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class PermissionAction extends CommonAction<Permission> {

	public PermissionAction() {
		super(Permission.class);
	}

	@Autowired
	private PermissionService permissionService;

	@Action(value = "permissionAction_pageQuery")
	public String pageQuery() throws IOException {
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<Permission> page = permissionService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles" });

		page2json(page, jsonConfig);
		return NONE;
	}

	@Action(value = "permissionAction_save", results = {
			@Result(name = "success", location = "/pages/system/permission.html", type = "redirect") })
	public String save() {
		permissionService.save(getModel());
		return SUCCESS;
	}

	@Action(value = "permissionAction_findAll")
	public String findAll() throws IOException {

		// 调用业务层进行查询
		Page<Permission> page = permissionService.findAll(null);

		List<Permission> list = page.getContent();

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles" });

		list2json(list, jsonConfig);
		return NONE;
	}

}
