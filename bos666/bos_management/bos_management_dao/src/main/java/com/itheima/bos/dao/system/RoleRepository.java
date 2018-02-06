package com.itheima.bos.dao.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itheima.bos.domain.system.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	// SELECT * FROM T_ROLE r INNER JOIN T_USER_ROLE ur on r.C_ID = ur.C_ROLE_ID
	// INNER JOIN T_USER u on u.C_ID = ur.C_USER_ID WHERE ur.C_USER_ID = 236;

	@Query("select r from Role r inner join r.users u where u.id = ?")
	List<Role> findUid(Long uid);

}
