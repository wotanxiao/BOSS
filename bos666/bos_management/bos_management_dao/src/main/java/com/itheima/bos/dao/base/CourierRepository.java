package com.itheima.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.itheima.bos.domain.base.Courier;

// 查询操作的话,就需要多实现一个接口JpaSpecificationExecutor
// JpaSpecificationExecutor接口不能独立使用,必须和JpaRepository一起使用
public interface CourierRepository extends JpaRepository<Courier, Long>, JpaSpecificationExecutor<Courier> {
	
	//作废
	@Query("update Courier set deltag = 1 where id = ?")
	@Modifying
	void updateDelTagById(Long parseLong);

	// 查询所有在职的快递员
	List<Courier> findByDeltagIsNull();
	
	//还原
	@Query("update Courier set deltag = null where id = ?")
	@Modifying
	void updateDelTagByIdRestore(long parseLong);

	@Query("select c from Courier c inner join c.fixedAreas f where f.id=?1")
	List<Courier> findCourier(Long id);
}
