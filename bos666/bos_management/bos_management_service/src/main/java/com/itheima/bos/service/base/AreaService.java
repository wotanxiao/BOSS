package com.itheima.bos.service.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.base.Area;

public interface AreaService {

	void save(ArrayList<Area> list);

	Page<Area> findAll(Pageable pageable);

	List<Area> findByQ(String q);

	List<Object[]> exportCharts();

}
