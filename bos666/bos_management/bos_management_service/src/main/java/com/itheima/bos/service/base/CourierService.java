package com.itheima.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.itheima.bos.domain.base.Courier;

public interface CourierService {

	void save(Courier model);

	Page<Courier> findAll(Pageable pageable);

	void batchDel(String ids);

	Page<Courier> findAll(Specification<Courier> specification, Pageable pageable);

	List<Courier> findAvalible();

	void batchRestore(String ids);

}
