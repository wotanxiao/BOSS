package com.itheima.bos.web.action.system;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.shiro.SecurityUtils;
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

import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.User;
import com.itheima.bos.service.system.MenuService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class MenuAction extends CommonAction<Menu> {

	public MenuAction() {
		super(Menu.class);
	}

	@Autowired
	private MenuService menuService;

	// 查询所有的一级菜单,二级菜单是通过一级菜单的children获取出来的
	@Action(value = "menuAction_findLevelOne")
	public String findLevelOne() throws IOException {

		List<Menu> list = menuService.findLevelOne();
		/*Collections.sort(list, new Comparator<Menu>() {
			@Override
			public int compare(Menu o1, Menu o2) {
				if (o1.getId() > o2.getId()) {
					return 1;
				}
				return -1;
			}
		});*/
		for (Menu menu : list) {
			TreeSet<Menu> treeSet = new TreeSet<>(new Comparator<Menu>() {
				@Override
				public int compare(Menu o1, Menu o2) {
					if (o1.getId() > o2.getId()) {
						return 1;
					}
					return -1;
				}
			});
			for (Menu menu1 : menu.getChildren()) {
				treeSet.add(menu1);
			}
			menu.setChildrenMenus(treeSet);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles", "childrenMenus", "parentMenu" });
		list2json(list, jsonConfig);
		return NONE;
	}

	@Action(value = "menuAction_save", results = {
			@Result(name = "success", location = "/pages/system/menu.html", type = "redirect") })
	public String save() {
		menuService.save(getModel());
		return SUCCESS;
	}

	// 分页查询
	@Action(value = "menuAction_pageQuery")
	public String pageQuery() throws IOException {
		// Struts框架在封装数据的时候会优先封装给模型对象,会导致属性驱动中的page对象无法获取数据
		Pageable pageable = new PageRequest(Integer.parseInt(getModel().getPage()) - 1, rows);
		// 调用业务层进行查询
		Page<Menu> page = menuService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles", "childrenMenus", "parentMenu" });

		page2json(page, jsonConfig);
		return NONE;
	}

	// 根据当前用户获取菜单
	@Action(value = "menuAction_findMenuByUid")
	public String findMenuByUid() throws IOException {

		// 获取当前登录的用户
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		List<Menu> list = menuService.findMenuByUid(user);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles", "childrenMenus", "parentMenu", "children" });
		list2json(list, jsonConfig);

		return NONE;
	}

}
