package com.itheima.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.base.SubArea;

public interface SubAreaService {

	void save(SubArea subArea);

	Page<SubArea> findAll(Pageable pageable);

	List<SubArea> findSubAreasUnAssociated();

	List<SubArea> findSubAreasAssociated2FixedArea(Long fixedAreaID);

	List<Object[]> exportCharts();

}
