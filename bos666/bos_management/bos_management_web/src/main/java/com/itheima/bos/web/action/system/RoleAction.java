package com.itheima.bos.web.action.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.itheima.bos.domain.system.Menu;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
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

import com.itheima.bos.domain.system.Role;
import com.itheima.bos.service.system.RoleService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

import javax.servlet.http.HttpServletResponse;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class RoleAction extends CommonAction<Role> {

	public RoleAction() {
		super(Role.class);
	}

	@Autowired
	private RoleService roleService;

	// 使用属性驱动获取菜单和权限的ID
	private String menuIds;
	private Long[] permissionIds;

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

	public void setPermissionIds(Long[] permissionIds) {
		this.permissionIds = permissionIds;
	}

	@Action(value = "roleAction_save", results = {
			@Result(name = "success", location = "/pages/system/role.html", type = "redirect") })
	public String save() {
		roleService.save(getModel(), menuIds, permissionIds);
		return SUCCESS;
	}

	@Action(value = "roleAction_pageQuery")
	public String pageQuery() throws IOException {
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<Role> page = roleService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "users", "permissions", "menus" });

		page2json(page, jsonConfig);
		return NONE;
	}

	@Action(value = "roleAction_findAll")
	public String findAll() throws IOException {

		// 调用业务层进行查询
		Page<Role> page = roleService.findAll(null);

		List<Role> list = page.getContent();

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "users", "permissions", "menus" });

		list2json(list, jsonConfig);
		return NONE;
	}

	@Action(value = "roleAction_findById")
	public void findById() throws IOException {
		Long id = getModel().getId();
		Role role = roleService.findById(id);
		JsonConfig jsonConfig = new JsonConfig();
		// "users", "permissions.roles", "menus.roles"
		jsonConfig.setExcludes(new String[] {"users", "roles", "childrenMenus", "parentMenu"});
		String json = JSONObject.fromObject(role, jsonConfig).toString();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(json);
	}
}
