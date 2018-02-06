package com.itheima.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itheima.bos.domain.base.SubArea;

public interface SubAreaRepository extends JpaRepository<SubArea, Long> {
	// 查找没有关联定区的分区
	List<SubArea> findByFixedAreaIsNull();

	// 查找关联到指定定区的分区
	@Query("select s from SubArea s inner join s.fixedArea f where f.id = ?")
	List<SubArea> findSubAreasAssociated2FixedArea(Long fixedAreaID);

	//获取HighCharts图表数据
	@Query(value="SELECT C_PROVINCE,count(*) FROM T_SUB_AREA,T_AREA WHERE T_SUB_AREA.C_AREA_ID=T_AREA.C_ID GROUP BY C_PROVINCE",nativeQuery=true)
	List<Object[]> exportCharts();

   // List<SubArea> findByFixedAreaIsNotNull();

   

	

}
