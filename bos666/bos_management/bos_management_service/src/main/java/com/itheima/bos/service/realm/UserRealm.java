package com.itheima.bos.service.realm;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itheima.bos.dao.system.PermissionRepository;
import com.itheima.bos.dao.system.RoleRepository;
import com.itheima.bos.dao.system.UserRepository;
import com.itheima.bos.domain.system.Permission;
import com.itheima.bos.domain.system.Role;
import com.itheima.bos.domain.system.User;

@Component
public class UserRealm extends AuthorizingRealm {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private RoleRepository roleRepository;

	// 授权方法
	// 该方法会在用户每次请求需要权限校验的资源时调用
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		// 查询当前用户对应的权限和角色
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		if ("admin".equals(user.getUsername())) {
			// 超级管理员
			List<Permission> permissions = permissionRepository.findAll();
			for (Permission permission : permissions) {
				info.addStringPermission(permission.getKeyword());
			}
			// 角色是权限的集合
			List<Role> roles = roleRepository.findAll();
			for (Role role : roles) {
				info.addRole(role.getKeyword());
			}
		} else {
			// 普通用户
			List<Permission> permissions = permissionRepository.findUid(user.getId());
			for (Permission permission : permissions) {
				info.addStringPermission(permission.getKeyword());
			}
			// 角色是权限的集合
			List<Role> roles = roleRepository.findUid(user.getId());
			for (Role role : roles) {
				info.addRole(role.getKeyword());
			}
		}
		return info;
	}

	// 认证方法
	// token就是Subject执行login方法传递进来的参数
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String username = usernamePasswordToken.getUsername();
		// 根据用户名查找用户
		User user = userRepository.findByUsername(username);

		// 找到,比对密码
		if (user != null) {
			/**
			 * 
			 * @param principal
			 *            当事人,主体.往往传递从数据库中查询出来的用户对象
			 * @param credentials
			 *            凭证,密码(是从数据库中查询出来的密码)
			 * @param realmName
			 * 
			 */
			AuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
			// 比对成功 处理后续逻辑
			// 比对失败 抛异常
			return info;
		}

		// 找不到 抛异常
		return null;
	}

}
