package com.itheima.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.system.MenuRepository;
import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.User;
import com.itheima.bos.service.system.MenuService;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepository;

	@Override
	public List<Menu> findLevelOne() {
		return menuRepository.findByParentMenuIsNull();
	}

	@Override
	public void save(Menu model) {
		// 判断用户是否要添加一级菜单,父菜单是否id为null
		Menu parentMenu = model.getParentMenu();
		if (parentMenu != null && parentMenu.getId() == null) {
			model.setParentMenu(null);
		}
		menuRepository.save(model);
	}

	@Override
	public Page<Menu> findAll(Pageable pageable) {
		return menuRepository.findAll(pageable);
	}

	@Override
	public List<Menu> findMenuByUid(User user) {
		// 判断超级管理员
		if ("admin".equals(user.getUsername())) {
			return menuRepository.findAll();
		}

		return menuRepository.findMenuByUid(user.getId());
	}
}
