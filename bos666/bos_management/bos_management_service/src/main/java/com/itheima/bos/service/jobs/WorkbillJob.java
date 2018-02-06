package com.itheima.bos.service.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itheima.bos.dao.take_delivery.WorkbillRepository;
import com.itheima.bos.domain.take_delivery.WorkBill;
import com.itheima.utils.MailUtils;

@Component
public class WorkbillJob {
	@Autowired
	private WorkbillRepository workbillRepository;

	public void sendWorkBillMail() {
		List<WorkBill> list = workbillRepository.findAll();

		String emailBody = "编号\t工单类型\t取件状态\t快递员<br>";
		for (WorkBill workBill : list) {
			emailBody += workBill.getId() + "\t" + workBill.getType() + "\t" + workBill.getPickstate() + "\t"
					+ workBill.getCourier().getName() + "<br>";
		}

		MailUtils.sendMail("tom@store.com", "工单统计", emailBody);
	}
}
