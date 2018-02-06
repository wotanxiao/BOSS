package com.itheima.bos.service.system;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.User;

public interface MenuService {

	List<Menu> findLevelOne();

	void save(Menu model);

	Page<Menu> findAll(Pageable pageable);

	List<Menu> findMenuByUid(User user);

}
