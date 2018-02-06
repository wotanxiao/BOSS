package com.itheima.bos.service.take_delivery;

import com.itheima.bos.domain.take_delivery.WayBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WayBillService {

	void save(WayBill wayBill);

	void saveList(List<WayBill> list);

	Page<WayBill> findAll(Pageable pageable);
}
