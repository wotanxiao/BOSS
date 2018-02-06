package com.itheima.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itheima.bos.domain.base.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {

    @Query("from Area where province like ?1 or city like ?1 or district like ?1 or postcode like ?1 or citycode like ?1 or shortcode like ?1 ")
    List<Area> findByQ(String q);
    //11
    // 根据省市区查找区域
    Area findByProvinceAndCityAndDistrict(String province, String city, String district);

    @Query("select a.province,count(*) from Area a group by a.province")
    List<Object[]> exportCharts();
}