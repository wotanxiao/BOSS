package com.itheima.bos.service.take_delivery.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.take_delivery.WaybillRepository;
import com.itheima.bos.domain.take_delivery.WayBill;
import com.itheima.bos.service.take_delivery.WayBillService;

import java.util.List;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {

	@Autowired
	private WaybillRepository waybillRepository;

	@Override
	public void save(WayBill wayBill) {
		waybillRepository.save(wayBill);
	}

	@Override
	public void saveList(List<WayBill> list) {
		waybillRepository.save(list);
	}

	@Override
	public Page<WayBill> findAll(Pageable pageable) {
		return waybillRepository.findAll(pageable);
	}

}
