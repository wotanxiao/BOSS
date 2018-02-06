package com.itheima.bos.dao.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itheima.bos.domain.system.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	// "from Permission p inner join p.roles r inner join r.users u where u.id = ?"
	// 上面的语句简写了select *
	// 简写了以后JPA去进行查询的时候,会把所有的字段全部查询出来,在本例中查了五张表,对应着本工程的三个实体类
	// 查询以后会把把所有的字段封装给对应的实体类,封装结束以后,会把这些实体类装在一个数组中[Permission,Role,User]
	// 而我们需要的返回值是List<Permission>,这样就会发生类型转换异常
	// 要想解决这个问题,必须限定返回值,即增加select p语句
	@Query("select p from Permission p inner join p.roles r inner join r.users u where u.id = ?")
	List<Permission> findUid(Long uid);

}
