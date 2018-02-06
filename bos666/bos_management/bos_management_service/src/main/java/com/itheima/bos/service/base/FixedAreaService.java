package com.itheima.bos.service.base;

import java.util.List;

import com.itheima.bos.domain.base.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.base.FixedArea;
import com.itheima.bos.domain.base.SubArea;

public interface FixedAreaService {

	void save(FixedArea fixedArea);

	Page<FixedArea> findAll(Pageable pageable);

	void associationCourierToFixedArea(Long fixedAreaId, Long courierId, Long takeTimeId);

	void assignSubArea2FixedArea(Long fixedAreaId, Long[] subAreaIds);

    List<SubArea> findSubArea(String id);

    List<Courier> findCourier(Long id);
}
