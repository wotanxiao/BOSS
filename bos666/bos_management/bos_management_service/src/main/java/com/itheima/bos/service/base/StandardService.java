package com.itheima.bos.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.base.Standard;

public interface StandardService {
	void save(Standard standard);

	Page<Standard> findAll(Pageable pageable);
}
