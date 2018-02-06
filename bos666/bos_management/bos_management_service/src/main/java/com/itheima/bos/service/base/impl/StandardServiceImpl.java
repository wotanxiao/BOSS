package com.itheima.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.StandardRepository;
import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.service.base.StandardService;

@Service // 代表本类属于Service层
@Transactional // 代表本类中的方法需要使用事务
public class StandardServiceImpl implements StandardService {

	@Autowired
	private StandardRepository standardRepository;

	// 保存派送标准
	@Override
	public void save(Standard standard) {
		standardRepository.save(standard);
	}

	// 分页查询
	@Override
	public Page<Standard> findAll(Pageable pageable) {
		
	
		return standardRepository.findAll(pageable);
	}

}
