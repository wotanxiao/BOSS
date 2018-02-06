package com.itheima.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.domain.take_delivery.Promotion;
import com.itheima.bos.service.take_delivery.PromotionService;
import com.itheima.bos.web.action.CommonAction;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class PromotionAction extends CommonAction<Promotion> {

	public PromotionAction() {
		super(Promotion.class);
	}

	// 使用属性驱动获取封面图片
	private File titleImgFile;
	private String titleImgFileFileName;

	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}

	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	@Autowired
	private PromotionService promotionService;

	@Action(value = "promotionAction_save", results = {
			@Result(name = "success", location = "/pages/take_delivery/promotion.html", type = "redirect") })
	public String save() {

		Promotion promotion = getModel();

		try {
			// 保存文件的文件夹的名字
			String savePath = "upload";
			ServletContext servletContext = ServletActionContext.getServletContext();
			// 保存文件的文件夹的绝对磁盘路径
			String saveRealPath = servletContext.getRealPath(savePath);
			// 获取文件的后缀名
			// a.jpg .jpg jpg
			String suffix = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
			// 使用UUID获取文件名
			String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

			File destFile = new File(saveRealPath + "/" + fileName);
			FileUtils.copyFile(titleImgFile, destFile);

			// 文件在服务器上的地址 /bos_management_web/upload/a.jpg

			promotion.setTitleImg("/upload/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
			promotion.setTitleImg(null);
		}

		promotion.setStatus("1");

		promotionService.save(promotion);
		return SUCCESS;
	}

	@Action(value = "promotionAction_pageQuery")
	public String pageQuery() throws IOException {

		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<Promotion> page = promotionService.findAll(pageable);

		page2json(page, null);
		return NONE;
	}

}
