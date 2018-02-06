package com.itheima.bos.dao.base;

import com.itheima.bos.domain.base.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itheima.bos.domain.base.FixedArea;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FixedAreaRepository extends JpaRepository<FixedArea, Long> {
}
