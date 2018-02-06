package com.itheima.bos.service.base.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.CourierRepository;
import com.itheima.bos.domain.base.Courier;
import com.itheima.bos.service.base.CourierService;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {

	@Autowired
	private CourierRepository courierRepository;

	@Override
	public void save(Courier model) {
		courierRepository.save(model);

	}

	@Override
	public Page<Courier> findAll(Pageable pageable) {
		return courierRepository.findAll(pageable);
	}

	@RequiresPermissions("courier_delete")
	@Override
	public void batchDel(String ids) {

		if (StringUtils.isNotEmpty(ids)) {
			// 切割数据
			String[] split = ids.split(",");
			for (String id : split) {
				// 执行删除. 不要使用物理删除
				courierRepository.updateDelTagById(Long.parseLong(id));
			}
		}
	}

	@Override
	public Page<Courier> findAll(Specification<Courier> specification, Pageable pageable) {
		return courierRepository.findAll(specification, pageable);
	}

	@Override
	public List<Courier> findAvalible() {
		return courierRepository.findByDeltagIsNull();
	}
	
	// 批量还原 快递员
	@Override
	public void batchRestore(String ids) {
		if (StringUtils.isNotEmpty(ids)) {
			// 切割数据
			String[] split = ids.split(",");
			for (String id : split) {
				// 执行还原
				courierRepository.updateDelTagByIdRestore(Long.parseLong(id));
			}
		}
	}
}
