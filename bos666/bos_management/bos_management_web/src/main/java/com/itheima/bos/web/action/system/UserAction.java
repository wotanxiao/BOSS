package com.itheima.bos.web.action.system;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
import com.itheima.bos.domain.system.User;
import com.itheima.bos.service.system.UserService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class UserAction extends CommonAction<User> {

	public UserAction() {
		super(User.class);
	}

	@Action(value = "userAction_login", results = {
			@Result(name = "success", location = "/index.html", type = "redirect"),
			@Result(name = "login", location = "/login.html", type = "redirect") })
	public String login() {

		String serverCode = (String) ServletActionContext.getRequest().getSession().getAttribute("key");

		if (StringUtils.isNotEmpty(serverCode) && StringUtils.isNotEmpty(checkcode) && serverCode.equals(checkcode)) {

			// 当前用户
			Subject subject = SecurityUtils.getSubject();
			// 创建令牌
			AuthenticationToken token = new UsernamePasswordToken(getModel().getUsername(), getModel().getPassword());

			try {
				// 执行登录
				// 参数就传递给Realm中doGetAuthenticationInfo方法的参数
				subject.login(token);
				// 该方法的返回值是由Realm中创建SimpleAuthenticationInfo对象时,传入的第一个参数决定的
				User user = (User) subject.getPrincipal();
				// 把用户存入session
				ServletActionContext.getRequest().getSession().setAttribute("user", user);

				return SUCCESS;
			} catch (UnknownAccountException e) {
				System.out.println("账户不存在");
				e.printStackTrace();
			} catch (IncorrectCredentialsException e) {
				System.out.println("密码错误");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("其他异常:" + e.getMessage());
			}
		}

		return LOGIN;
	}

	@Action(value = "userAction_logout", results = {
			@Result(name = "success", location = "/login.html", type = "redirect") })
	public String logout() {
		// 注销
		SecurityUtils.getSubject().logout();
		// 清空Session
		ServletActionContext.getRequest().getSession().removeAttribute("user");
		return SUCCESS;
	}

	// 使用属性驱动获取用户输入的验证码
	private String checkcode;

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	// 使用属性驱动获取角色的ID
	private Long[] roleIds;

	public void setRoleIds(Long[] roleIds) {
		this.roleIds = roleIds;
	}

	@Autowired
	private UserService userService;

	@Action(value = "userAction_save", results = {
			@Result(name = "success", location = "/pages/system/userlist.html", type = "redirect") })
	public String save() {
		userService.save(getModel(), roleIds);
		return SUCCESS;
	}

	@Action(value = "userAction_pageQuery")
	public String pageQuery() throws IOException {
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<User> page = userService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "roles" });

		page2json(page, jsonConfig);
		return NONE;
	}

}
