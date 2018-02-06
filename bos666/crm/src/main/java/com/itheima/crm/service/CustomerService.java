package com.itheima.crm.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.itheima.crm.domain.Customer;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomerService {

	@GET
	@Path("/findAll")
	public List<Customer> findAll();

	// 查找未关联定区的客户
	@GET
	@Path("/findCustomersUnAssociated")
	public List<Customer> findCustomersUnAssociated();

	// 查找关联到指定定区的客户
	@GET
	@Path("/findCustomersAssociated2FixedArea")
	public List<Customer> findCustomersAssociated2FixedArea(@QueryParam("fixedAreaId") String fixedAreaId);

	/**
	 * 关联客户到指定的定区
	 * 
	 * @param fixedAreaId
	 *            : 定区ID
	 * @param customerIds
	 *            : 客户ID
	 */
	@PUT
	@Path("/assignCustomers2FixedArea")
	public void assignCustomers2FixedArea(@QueryParam("fixedAreaId") String fixedAreaId,
			@QueryParam("customerIds") Long[] customerIds);

	@POST
	@Path("/save")
	public void save(Customer customer);

	// 判断用户是否激活
	@GET
	@Path("/findByTelephone")
	public Customer findByTelephone(@QueryParam("telephone") String telephone);

	// 激活用户
	@PUT
	@Path("/active")
	public void active(@QueryParam("telephone") String telephone);

	// 登录
	@GET
	@Path("/login")
	public Customer login(@QueryParam("telephone") String telephone, @QueryParam("password") String password);

	// 根据地址查找定区的ID
	@GET
	@Path("/findFixedAreaIdByAddress")
	public String findFixedAreaIdByAddress(@QueryParam("address") String address);
}
