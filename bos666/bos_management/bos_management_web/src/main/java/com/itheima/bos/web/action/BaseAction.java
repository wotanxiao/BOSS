package com.itheima.bos.web.action;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.itheima.bos.domain.base.Standard;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

// 通用的Action

// StandardAction extends BaseAction<Standard>
public class BaseAction<T> extends ActionSupport implements ModelDriven<T> {

	private T model;

	public BaseAction() {

		// 获取到了StandardAction
		Class<? extends BaseAction> childClazz = this.getClass();
		// BaseAction
		// childClazz.getSuperclass();
		// 获取到了BaseAction<Standard>
		Type genericSuperclass = childClazz.getGenericSuperclass();
		// 把上面的类型转为具体的类型
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		// 泛型组成的数组
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		// 获取到我们需要的泛型,转为字节码
		Class<T> clazz = (Class<T>) actualTypeArguments[0];
		try {
			model = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public T getModel() {
		return model;
	}

}
