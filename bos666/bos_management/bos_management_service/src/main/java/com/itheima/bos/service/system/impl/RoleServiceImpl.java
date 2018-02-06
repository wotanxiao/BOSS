package com.itheima.bos.service.system.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.system.MenuRepository;
import com.itheima.bos.dao.system.PermissionRepository;
import com.itheima.bos.dao.system.RoleRepository;
import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.Permission;
import com.itheima.bos.domain.system.Role;
import com.itheima.bos.service.system.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public void save(Role role, String menuIds, Long[] permissionIds) {
		// 保存角色
		Role save = roleRepository.save(role);
		// 建立关联的时候,只能使用角色去关联菜单和权限,不能使用菜单和权限去关联角色
		// 原因在与菜单和权限的实体类中声明了mappedBy属性,意味着它们放弃了对关系的维护

		// 方式 1,查询持久态对象
		// method1(role, menuIds, permissionIds);
		// 方式2 ,使用脱管态
		method2(save, menuIds, permissionIds);
	}

	private void method2(Role role, String menuIds, Long[] permissionIds) {
		if (StringUtils.isNotEmpty(menuIds)) {
			String[] split = menuIds.split(",");
			for (String menuId : split) {

				Menu menu = new Menu();
				menu.setId(Long.parseLong(menuId));

				role.getMenus().add(menu);
			}
		}

		if (permissionIds != null && permissionIds.length > 0) {
			for (Long permissionId : permissionIds) {
				Permission permission = new Permission();
				permission.setId(permissionId);
				role.getPermissions().add(permission);
			}
		}
	}

	private void method1(Role role, String menuIds, Long[] permissionIds) {
		if (StringUtils.isNotEmpty(menuIds)) {
			String[] split = menuIds.split(",");
			for (String menuId : split) {
				// 持久态的菜单
				Menu menu = menuRepository.findOne(Long.parseLong(menuId));
				role.getMenus().add(menu);
			}
		}

		if (permissionIds != null && permissionIds.length > 0) {
			for (Long permissionId : permissionIds) {
				// 持久态的权限
				Permission permission = permissionRepository.findOne(permissionId);
				role.getPermissions().add(permission);
			}
		}
	}

	@Override
	public Page<Role> findAll(Pageable pageable) {
		return roleRepository.findAll(pageable);
	}

	@Override
	public Role findById(Long id) {
		return roleRepository.findOne(id);
	}

	@Override
	public void update(Role role) {
		roleRepository.save(role);
	}
}
