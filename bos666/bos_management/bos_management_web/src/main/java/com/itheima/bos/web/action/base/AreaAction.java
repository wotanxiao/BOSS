package com.itheima.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
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
import com.itheima.bos.service.base.AreaService;
import com.itheima.bos.web.action.CommonAction;
import com.itheima.utils.FileDownloadUtils;
import com.itheima.utils.PinYin4jUtils;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.json.JsonConfig;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default") // 等价于struts.xml中package节点中extends属性
public class AreaAction extends CommonAction<Area> {

	public AreaAction() {
		super(Area.class);
	}

	private static final long serialVersionUID = 5059676695054900556L;

	@Autowired
	private AreaService areaService;

	// 使用属性驱动获取用户上传的文件
	private File file;
	// 获取文件名
	private String fileFileName;

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setFile(File file) {
		this.file = file;
	}
	// 加载文件
	// 读取工作簿
	// 读取行
	// 读取列

	@Action(value = "areaAction_importXLS", results = {
			@Result(name = "success", location = "/pages/base/area.html", type = "redirect") })
	public String importXLS() {
		// 用来保存数据的集合
		ArrayList<Area> list = new ArrayList<>();

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
			// 读取工作簿
			HSSFSheet sheet = workbook.getSheetAt(0);
			// 遍历行
			for (Row row : sheet) {
				// 第一行是标题,这一行的数据不要
				if (row.getRowNum() == 0) {
					continue;
				}
				// 读取表格的数据
				String province = row.getCell(1).getStringCellValue();
				String city = row.getCell(2).getStringCellValue();
				String district = row.getCell(3).getStringCellValue();
				String postcode = row.getCell(4).getStringCellValue();

				// 截掉省市区的最后一个字符
				province = province.substring(0, province.length() - 1);
				city = city.substring(0, city.length() - 1);
				district = district.substring(0, district.length() - 1);
				// 获取城市编码
				String citycode = PinYin4jUtils.hanziToPinyin(city, "").toUpperCase();
				String[] headByString = PinYin4jUtils.getHeadByString(province + city + district, true);
				// 获取简码
				String shortcode = PinYin4jUtils.stringArrayToString(headByString);
				// 构造一个Area
				Area area = new Area();
				area.setProvince(province);
				area.setCity(city);
				area.setDistrict(district);
				area.setPostcode(postcode);
				area.setCitycode(citycode);
				area.setShortcode(shortcode);
				// 添加到集合
				list.add(area);
			}
			// 一次性保存区域数据
			areaService.save(list);
			// 释放资源
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

	// 分页查询,EasyUI所有的控件发请求的方式是AJAX,本方法返回的数据应该是JSON格式
	@Action(value = "areaAction_pageQuery")
	public String pageQuery() throws IOException {

		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<Area> page = areaService.findAll(pageable);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "subareas" });
		page2json(page, jsonConfig);

		return NONE;
	}

	// 使用属性驱动获取用户在输入框中输入的内容
	private String q;

	public void setQ(String q) {
		this.q = q;
	}

	// 分页查询,EasyUI所有的控件发请求的方式是AJAX,本方法返回的数据应该是JSON格式
	@Action(value = "areaAction_findAll")
	public String findAll() throws IOException {
		List<Area> list;

		if (StringUtils.isNotEmpty(q)) {
			list = areaService.findByQ(q);
		} else {
			// 调用业务层进行查询
			Page<Area> page = areaService.findAll(null);
			list = page.getContent();
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "subareas" });

		list2json(list, jsonConfig);

		return NONE;
	}

	// 导出Excel

	// 创建文件
	// 创建工作簿
	// 创建行
	// 创建列
	@Action(value = "areaAction_exportExcel")
	public String exportExcel() throws IOException {
		// 查询所有的数据
		Page<Area> page = areaService.findAll(null);
		List<Area> list = page.getContent();
		// 创建文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 创建工作簿
		HSSFSheet sheet = workbook.createSheet("区域数据");
		// 创建标题行
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("省");
		row.createCell(1).setCellValue("市");
		row.createCell(2).setCellValue("区");
		row.createCell(3).setCellValue("邮编");
		row.createCell(4).setCellValue("简码");
		row.createCell(5).setCellValue("城市编码");
		// 创建数据行
		for (Area area : list) {
			HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
			dataRow.createCell(0).setCellValue(area.getProvince());
			dataRow.createCell(1).setCellValue(area.getCity());
			dataRow.createCell(2).setCellValue(area.getDistrict());
			dataRow.createCell(3).setCellValue(area.getPostcode());
			dataRow.createCell(4).setCellValue(area.getShortcode());
			dataRow.createCell(5).setCellValue(area.getCitycode());

		}
		// 设置文件名
		String fileName = "区域数据.xls";
		// 下载需要 : 一个输出流两个信息头
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		ServletContext servletContext = ServletActionContext.getServletContext();

		// 获取浏览器类型
		String agent = request.getHeader("User-Agent");

		// 获取mimeType
		// 该操作要在文件编码前执行
		// 因为该方法是通过文件的后缀名获取mimetype的,编码以后后缀名会丢失
		String mimeType = servletContext.getMimeType(fileName);
		// 设置mimeType
		response.setContentType(mimeType);
		// 对文件名进行重新编码
		fileName = FileDownloadUtils.encodeDownloadFilename(fileName, agent);
		// 设置信息头
		// Content-Disposition: attachment; filename="fname.ext"
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		// 写出文件
		workbook.write(response.getOutputStream());
		// 关流
		workbook.close();
		return NONE;
	}

	// 获取HighCharts图表数据
	@Action(value = "areaAction_exportCharts")
	public String exportCharts() throws IOException {
		List<Object[]> list = areaService.exportCharts();
		list2json(list, null);
		return NONE;
	}

	@Autowired
	private DataSource dataSource;

	// 获取PDF
	@Action(value = "areaAction_exportPDF")
	public String exportPDF() throws Exception {
		// 读取 jrxml 文件
		String jrxml = ServletActionContext.getServletContext().getRealPath("/jasper/area.jrxml");
		// 准备需要数据
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("company", "传智播客");
		// 准备需要数据
		JasperReport report = JasperCompileManager.compileReport(jrxml);
		JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource.getConnection());

		HttpServletResponse response = ServletActionContext.getResponse();
		OutputStream ouputStream = response.getOutputStream();
		// 设置相应参数，以附件形式保存PDF
		response.setContentType("application/pdf");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + FileDownloadUtils
				.encodeDownloadFilename("工作单.pdf", ServletActionContext.getRequest().getHeader("user-agent")));
		// 使用JRPdfExproter导出器导出pdf
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);
		exporter.exportReport();// 导出
		ouputStream.close();// 关闭流
		return NONE;
	}

}
