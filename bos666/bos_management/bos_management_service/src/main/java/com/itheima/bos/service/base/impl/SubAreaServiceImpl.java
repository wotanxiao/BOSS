package com.itheima.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.SubAreaRepository;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.service.base.SubAreaService;

@Service // 代表本类属于Service层
@Transactional // 代表本类中的方法需要使用事务
public class SubAreaServiceImpl implements SubAreaService {

	@Autowired
	private SubAreaRepository subAreaRepository;

	@Override
	public void save(SubArea subArea) {
		subAreaRepository.save(subArea);
	}

	@Override
	public Page<SubArea> findAll(Pageable pageable) {
		return subAreaRepository.findAll(pageable);
	}

	// 查找没有关联定区的分区
	@Override
	public List<SubArea> findSubAreasUnAssociated() {
		return subAreaRepository.findByFixedAreaIsNull();
	}

	// 查找关联到指定定区的分区
	@Override
	public List<SubArea> findSubAreasAssociated2FixedArea(Long fixedAreaID) {
		return subAreaRepository.findSubAreasAssociated2FixedArea(fixedAreaID);
	}

	@Override
	public List<Object[]> exportCharts() {
		  
		// TODO Auto-generated method stub  
		return subAreaRepository.exportCharts();
	}
}
