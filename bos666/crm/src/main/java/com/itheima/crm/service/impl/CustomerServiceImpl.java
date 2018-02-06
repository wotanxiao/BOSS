package com.itheima.crm.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.crm.dao.CustomerRepository;
import com.itheima.crm.domain.Customer;
import com.itheima.crm.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	// 查找未关联定区的客户
	@Override
	public List<Customer> findCustomersUnAssociated() {
		return customerRepository.findByFixedAreaIdIsNull();
	}

	// 查找关联到指定定区的客户
	@Override
	public List<Customer> findCustomersAssociated2FixedArea(String fixedAreaId) {
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}

	// 关联客户到指定的定区
	@Override
	public void assignCustomers2FixedArea(String fixedAreaId, Long[] customerIds) {

		if (StringUtils.isNotEmpty(fixedAreaId)) {
			// 把关联到定区ID的客户全部解绑
			customerRepository.unbindByFixedAreaId(fixedAreaId);
			// 再次进行绑定
			if (customerIds != null && customerIds.length > 0) {
				for (Long id : customerIds) {
					customerRepository.bindFixedAreaById(fixedAreaId, id);
				}
			}
		}

	}

	@Override
	public void save(Customer customer) {
		customerRepository.save(customer);
	}

	@Override
	public Customer findByTelephone(String telephone) {
		return customerRepository.findByTelephone(telephone);
	}

	@Override
	public void active(String telephone) {
		customerRepository.active(telephone);
	}

	@Override
	public Customer login(String telephone, String password) {
		return customerRepository.findByTelephoneAndPassword(telephone, password);
	}

	@Override
	public String findFixedAreaIdByAddress(String address) {
		return customerRepository.findFixedAreaIdByAddress(address);
	}
}
