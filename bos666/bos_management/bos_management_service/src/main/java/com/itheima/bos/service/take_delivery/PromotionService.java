package com.itheima.bos.service.take_delivery;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itheima.bos.domain.take_delivery.PageBean;
import com.itheima.bos.domain.take_delivery.Promotion;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PromotionService {

	void save(Promotion promotion);

	Page<Promotion> findAll(Pageable pageable);

	// 
	// 分区查询,提供给前台系统使用
	@GET
	@Path("/findAll")
	PageBean<Promotion> findByPage(@QueryParam("pageIndex") int page, @QueryParam("pageSize") int pageSize);

}
