package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Area;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.domain.base.TakeTime;
import com.itheima.bos.service.base.SubAreaService;
import com.itheima.bos.web.action.CommonAction;
import com.itheima.utils.FileDownloadUtils;

import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class SubAreaAction extends CommonAction<SubArea> {

	public SubAreaAction() {
		super(SubArea.class);
	}

	@Autowired
	private SubAreaService subAreaService;

	@Action(value = "subareaAction_save", results = {
			@Result(name = "success", location = "/pages/base/sub_area.html", type = "redirect") })
	public String save() {
		subAreaService.save(getModel());
		return SUCCESS;
	}

	@Action(value = "subAreaAction_pageQuery")
	public String pageQuery() throws IOException {

		Pageable pageable = new PageRequest(page - 1, rows);

		// 调用业务层进行查询
		Page<SubArea> page = subAreaService.findAll(pageable);

		/*JsonConfig jsonConfig = new JsonConfig();
		// 禁止代码循环调用方式1
		// jsonConfig.setExcludes(new String[] { "subareas" });
		// 禁止代码循环调用方式2
		// SubArea中包含Area,Area又包含SubArea,生成JSON时会循环查询,下面的代码禁止循环查询
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			*//**
			 * source 拥有属性的对象 <br>
			 * name 属性的名字<br>
			 * value 属性的值<br>
			 *//*
			@Override
			public boolean apply(Object source, String name, Object value) {
				if (source instanceof Area && name.equals("subareas")) {
					return true;
				}
				return false;
			}
		});*/

		 JsonConfig jsonConfig = new JsonConfig();
	        jsonConfig.setExcludes(new String[]{"subareas","couriers"});
		page2json(page, jsonConfig);
		return NONE;
	}

	// 查询未关联定区的分区
	@Action(value = "subAreaAction_findSubAreasUnAssociated")
	public String findSubAreasUnAssociated() throws IOException {

		List<SubArea> content = subAreaService.findSubAreasUnAssociated();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setExcludes(new String[] { "subareas" });
		list2json(content, jsonConfig);
		return NONE;
	}

	// 查询已经关联指定定区的分区
	@Action(value = "subAreaAction_findSubAreasAssociated2FixedArea")
	public String findSubAreasAssociated2FixedArea() throws IOException {

		List<SubArea> content = subAreaService.findSubAreasAssociated2FixedArea(getModel().getId());

		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setExcludes(new String[] { "subareas", "couriers" });
		list2json(content, jsonConfig);
		return NONE;
	}
	
	//分区数据导出功能
	@Action(value = "subAreaAction_exportExcel")
	public String exportExcel() throws IOException {
        //第一步：查询所有的分区数据
	    Page<SubArea> page = subAreaService.findAll(null);
	    List<SubArea> list = page.getContent() ;
	    
	    //第二步：使用POI将数据写到EXCEL文件中
	    //1.在内存中创建一个Excel文件
	    HSSFWorkbook workbook = new HSSFWorkbook();
	    
	    //2.创建一个标签页
	    HSSFSheet sheet = workbook.createSheet("分区数据");
	    
	    //3.创建标题行
	    HSSFRow headRow = sheet.createRow(0);
	    headRow.createCell(0).setCellValue("分区编号");
	    headRow.createCell(1).setCellValue("省");
	    headRow.createCell(2).setCellValue("市");
	    headRow.createCell(3).setCellValue("区");
	    headRow.createCell(4).setCellValue("起始号");
	    headRow.createCell(5).setCellValue("终止号");
	    headRow.createCell(6).setCellValue("单双号");
	    headRow.createCell(7).setCellValue("关键字");
	    headRow.createCell(8).setCellValue("辅助关键字");
	            
	    for (SubArea subArea : list) {
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum()+1);
            
            dataRow.createCell(0).setCellValue(subArea.getId());
            dataRow.createCell(1).setCellValue(subArea.getArea().getProvince());
            dataRow.createCell(2).setCellValue(subArea.getArea().getCity());
            dataRow.createCell(3).setCellValue(subArea.getArea().getDistrict());
            dataRow.createCell(4).setCellValue(subArea.getStartNum());
            dataRow.createCell(5).setCellValue(subArea.getEndNum());
            dataRow.createCell(6).setCellValue(subArea.getSingle());
            dataRow.createCell(7).setCellValue(subArea.getKeyWords());
            dataRow.createCell(8).setCellValue(subArea.getAssistKeyWords());
        }
	    //第三步：使用输出流进行文件下载
	    String filename = "分区数据.xls";
	    
	    String contentType = ServletActionContext.getServletContext().getMimeType(filename);
		ServletActionContext.getResponse().setContentType(contentType);
		ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();

	    //获取客户端浏览器类型
	    String agent = ServletActionContext.getRequest().getHeader("User-Agent");
	    filename = FileDownloadUtils.encodeDownloadFilename(filename, agent) ;
	    ServletActionContext.getResponse().setHeader("content-disposition","attachment;filename =" + filename);
	    workbook.write(outputStream);
	    return NONE;
    }
	//获取HighCharts图表数据
	@Action("subAreaAction_exportCharts")
	public String exportCharts() throws IOException{
		List<Object[]> list=subAreaService.exportCharts();
		list2json(list, null);
		return null;
	}
}
