package com.itheima.bos.service.system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.system.Role;

public interface RoleService {

	void save(Role role, String menuIds, Long[] permissionIds);

	Page<Role> findAll(Pageable pageable);

    Role findById(Long id);

    void update(Role role);
}
