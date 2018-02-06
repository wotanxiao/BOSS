package com.itheima.crm.dao;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.itheima.crm.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// 查找未关联定区的客户
	List<Customer> findByFixedAreaIdIsNull();

	// 查找关联到指定定区的客户
	List<Customer> findByFixedAreaId(String fixedAreaId);

	// 把关联到定区ID的客户全部解绑
	@Modifying
	@Query("update Customer set fixedAreaId = null where fixedAreaId = ?")
	void unbindByFixedAreaId(String fixedAreaId);

	// 再次进行绑定
	@Modifying
	@Query("update Customer set fixedAreaId = ? where id = ?")
	void bindFixedAreaById(String fixedAreaId, Long id);

	Customer findByTelephone(String telephone);

	@Modifying
	@Query("update Customer set type = 1 where telephone = ?")
	void active(String telephone);

	Customer findByTelephoneAndPassword(String telephone, String password);

	@Query("select fixedAreaId from Customer where address = ?")
	public String findFixedAreaIdByAddress(String address);
}
