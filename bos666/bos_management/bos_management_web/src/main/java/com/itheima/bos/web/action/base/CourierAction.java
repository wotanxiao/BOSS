package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Courier;
import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.service.base.CourierService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

@Controller
@Scope("prototype")
@Namespace("/")
@ParentPackage("struts-default")
public class CourierAction extends CommonAction<Courier> {

	public CourierAction() {
		super(Courier.class);
	}

	@Autowired
	private CourierService courierService;

	@Action(value = "courierAction_save", results = {
			@Result(name = "success", location = "/pages/base/courier.html", type = "redirect") })
	public String save() {

		courierService.save(getModel());
		return SUCCESS;
	}

	// 使用属性驱动获取用户要作废的快递员的ID
	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 批量删除(作废)
	@Action(value = "courierAction_batchDel", results = {
			@Result(name = "success", location = "/pages/base/courier.html", type = "redirect") })
	public String batchDel() {

		courierService.batchDel(ids);
		return SUCCESS;
	}
	
	// 批量还原
	@Action(value = "courierAction_batchRestore", results = {
			@Result(name = "success", location = "/pages/base/courier.html", type = "redirect") })
	public String batchRestore() {

		courierService.batchRestore(ids);
		return SUCCESS;
	}

	@Action(value = "courierAction_pageQuery")
	public String pageQuery() throws IOException {
		// 构造查询条件
		Specification<Courier> specification = new Specification<Courier>() {
			/**
			 * 构建一个where条件语句
			 * 
			 * @param root
			 *            : 根对象,简单的理解为泛型的对象,
			 * @param cb
			 *            : 构建查询条件的对象
			 * @return a {@link Predicate}, must not be {@literal null}.
			 */
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				String courierNum = getModel().getCourierNum();
				String company = getModel().getCompany();
				String type = getModel().getType();
				Standard standard = getModel().getStandard();

				ArrayList<Predicate> list = new ArrayList<>();

				// 快递员工号不为空,构建一个等值查询
				// where courierNum = ?
				if (StringUtils.isNotEmpty(courierNum)) {
					// 参数1 :
					// 参数2 : 代替?的具体值
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class), courierNum);
					list.add(p1);
				}

				// 公司不为空,构建一个模糊查询
				if (StringUtils.isNotEmpty(company)) {
					// 参数1 :
					// 参数2 : 代替?的具体值
					Predicate p2 = cb.like(root.get("company").as(String.class), "%" + company + "%");
					list.add(p2);
				}
				// 类型不为空,构建一个等值查询
				if (StringUtils.isNotEmpty(type)) {
					// 参数1 :
					// 参数2 : 代替?的具体值
					Predicate p3 = cb.equal(root.get("type").as(String.class), type);
					list.add(p3);
				}

				if (standard != null) {
					String name = standard.getName();
					if (StringUtils.isNotEmpty(name)) {
						// 参数1 :
						// 参数2 : 代替?的具体值
						// 连表查询
						Join<Object, Object> join = root.join("standard");
						Predicate p4 = cb.equal(join.get("name").as(String.class), type);
						list.add(p4);
					}
				}
				// 用户没有输入查询条件
				if (list.size() == 0) {
					return null;
				}
				// 构造数组
				Predicate[] arr = new Predicate[list.size()];
				list.toArray(arr);

				return cb.and(arr);
			}
		};

		Pageable pageable = new PageRequest(page - 1, rows);

		Page<Courier> page = courierService.findAll(specification, pageable);

		JsonConfig jsonConfig = new JsonConfig();
		// 指定在生成json数据的时候要忽略的字段
		jsonConfig.setExcludes(new String[] { "fixedAreas", "takeTime" });
		page2json(page, jsonConfig);

		return NONE;
	}

	// 查询正常在职的快递员
	@Action(value = "courierAction_findAvalible")
	public String findAvalible() throws IOException {

		Specification<Courier> specification = new Specification<Courier>() {

			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 查找作废标记等于null的快递员
				return cb.isNull(root.get("deltag").as(Character.class));
			}
		};

		Page<Courier> page = courierService.findAll(specification, null);

		List<Courier> list = page.getContent();

		JsonConfig jsonConfig = new JsonConfig();
		// 指定在生成json数据的时候要忽略的字段
		jsonConfig.setExcludes(new String[] { "fixedAreas", "takeTime" });

		list2json(list, jsonConfig);

		return NONE;
	}

	// 查询正常在职的快递员
	// 该实现方法需要有Service和DAO的实现
	@Action(value = "courierAction_findAvalible2")
	public String findAvalible2() throws IOException {

		List<Courier> list = courierService.findAvalible();
		JsonConfig jsonConfig = new JsonConfig();
		// 指定在生成json数据的时候要忽略的字段
		jsonConfig.setExcludes(new String[] { "fixedAreas", "takeTime" });

		list2json(list, jsonConfig);

		return NONE;
	}
}
