package com.itheima.bos.domain.take_delivery;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

// 封装分页查询的结果
// 因为Page/Pageable是Spring框架提供的,我们无法在这些类上添加@XmlRootElement
@XmlRootElement(name = "pageBean")
@XmlSeeAlso(Promotion.class)
public class PageBean<T> {

	private List<T> list;
	private long total;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
