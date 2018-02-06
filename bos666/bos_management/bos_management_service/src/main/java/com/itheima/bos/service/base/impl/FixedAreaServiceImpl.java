package com.itheima.bos.service.base.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.CourierRepository;
import com.itheima.bos.dao.base.FixedAreaRepository;
import com.itheima.bos.dao.base.SubAreaRepository;
import com.itheima.bos.dao.base.TakeTimeRepository;
import com.itheima.bos.domain.base.Area;
import com.itheima.bos.domain.base.Courier;
import com.itheima.bos.domain.base.FixedArea;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.domain.base.TakeTime;
import com.itheima.bos.service.base.FixedAreaService;

@Service // 代表本类属于Service层
@Transactional // 代表本类中的方法需要使用事务
public class FixedAreaServiceImpl implements FixedAreaService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	@Autowired
	private CourierRepository courierRepository;
	@Autowired
	private TakeTimeRepository takeTimeRepository;
	@Autowired
	private SubAreaRepository subAreaRepository;

	@Override
	public Page<FixedArea> findAll(Pageable pageable) {
		return fixedAreaRepository.findAll(pageable);
	}

	@Override
	public void save(FixedArea fixedArea) {
		fixedAreaRepository.save(fixedArea);
	}

	// 关联快递员到定区
	@Override
	public void associationCourierToFixedArea(Long fixedAreaId, Long courierId, Long takeTimeId) {
		// 持久态的对象
		FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
		Courier courier = courierRepository.findOne(courierId);
		TakeTime takeTime = takeTimeRepository.findOne(takeTimeId);

		// 建立快递员和时间的关联
		courier.setTakeTime(takeTime);
		// 建立快递员和定区的关联
		// 在建立关联的时候只能使用定区关联快递员,不能使用快递员关联定区
		// 原因是因为Courier类中fixedAreas属性使用了mappedBy属性,代表快递员放弃了对中间表的维护
		fixedArea.getCouriers().add(courier);
		// 下面的代码是错误的
		// courier.getFixedAreas().add(fixedArea);

	}

	// 关联分区到定区
	@Override
	public void assignSubArea2FixedArea(Long fixedAreaId, Long[] subAreaIds) {
		// 持久态的对象
		FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
		// 将和本定区关联的分区全部解绑
		Set<SubArea> subareas = fixedArea.getSubareas();
		for (SubArea subArea : subareas) {
			subArea.setFixedArea(null);
		}
		// 将指定的分区绑定到本定区
		for (Long subAreaId : subAreaIds) {
			SubArea subArea = subAreaRepository.findOne(subAreaId);
			// 建立分区和定区的关联,只能使用如下方式
			subArea.setFixedArea(fixedArea);
			// 不可以用定区去关联分区,因为FixedArea类的subareas属性使用了mappedBy属性,代表放弃了对关系的维护
		}

	}

    @Override
    public List<SubArea> findSubArea(String id) {
          
        if(id!=null){
            
            return subAreaRepository.findSubAreasAssociated2FixedArea(Long.parseLong(id));
            
        }
        return null;
    }

	@Override
	public List<Courier> findCourier(Long id) {
		return courierRepository.findCourier(id);
	}
}
