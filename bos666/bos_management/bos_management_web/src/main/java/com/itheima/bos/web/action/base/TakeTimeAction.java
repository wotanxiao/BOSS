package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.domain.base.TakeTime;
import com.itheima.bos.service.base.TakeTimeService;
import com.itheima.bos.web.action.CommonAction;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class TakeTimeAction extends CommonAction<TakeTime> {

	public TakeTimeAction() {
		super(TakeTime.class);
	}

	@Autowired
	private TakeTimeService takeTimeService;

	// 查询所有的时间
	@Action(value = "takeTime_findAll")
	public String findAll() throws IOException {

		List<TakeTime> content = takeTimeService.findAll();

		list2json(content, null);
		return NONE;
	}

}
