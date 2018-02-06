package com.itheima.bos.web.action.take_delivery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JsonConfig;
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

import com.itheima.bos.domain.take_delivery.WayBill;
import com.itheima.bos.service.take_delivery.WayBillService;
import com.itheima.bos.web.action.CommonAction;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class WaybillAction extends CommonAction<WayBill> {

	public WaybillAction() {
		super(WayBill.class);
	}

	@Autowired
	private WayBillService wayBillService;

	@Action("waybillAction_save")
	public String save() throws IOException {
		// 封装结果的对象
		String msg = "1";
		try {
			int i = 1 / 0;
			wayBillService.save(getModel());
		} catch (Exception e) {
			msg = "0";
			e.printStackTrace();
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(msg);
		return NONE;
	}

	//定义属性驱动接收用户上传的文件和
	private File upload;

    public void setUpload(File upload) {
        this.upload = upload;
    }

    //运单批量导入
	@Action(value = "waybill_batchImport")
	public  String batchImport(){
		// 创建操作表格对象
		HSSFWorkbook workbook = null;
		HttpServletResponse response = ServletActionContext.getResponse();
		//创建一个list 存放运单集合对象
		List<WayBill> list = new ArrayList<>();
		try {
			workbook = new HSSFWorkbook(new FileInputStream(upload));
			// 选取第一个工作空间
			HSSFSheet sheetAt = workbook.getSheetAt(0);
			//循坏读取每一行,并取出每个单元格的值
			for (Row row : sheetAt) {
				int rowNum = row.getRowNum();

                if (rowNum == 0){
                    continue;
                }
				String wayBillNum = row.getCell(0).getStringCellValue();
				String goodsType = row.getCell(1).getStringCellValue();
				String sendProNum = row.getCell(2).getNumericCellValue() + "";
				String sendName = row.getCell(3).getStringCellValue();
				String sendMobile = row.getCell(4).getNumericCellValue() + "";
				String sendAddress = row.getCell(5).getStringCellValue();
				String recName = row.getCell(6).getStringCellValue();
				String recMobile = row.getCell(7).getNumericCellValue() + "";
				String recCompany = row.getCell(8).getStringCellValue();
				String recAddress = row.getCell(9).getStringCellValue();

				//封装运单对象
				WayBill wayBill = new WayBill();
				wayBill.setWayBillNum(wayBillNum);
				wayBill.setGoodsType(goodsType);
				wayBill.setSendName(sendName);
				wayBill.setSendMobile(sendMobile);
				wayBill.setSendAddress(sendAddress);
				wayBill.setRecName(recName);
				wayBill.setRecMobile(recMobile);
				wayBill.setRecCompany(recCompany);
				wayBill.setRecAddress(recAddress);
				wayBill.setSendProNum(sendProNum);

				list.add(wayBill);
			}

			wayBillService.saveList(list);
			// 关流,并将结果返回前端
            workbook.close();
			response.getWriter().write("success");
		} catch (IOException e) {
            try {
                workbook.close();
                response.getWriter().write("error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
			e.printStackTrace();
		}
		return NONE;
	}

	//运单分页查询
	@Action(value = "waybillAction_pageQuery")
	public String pageQuery() throws IOException {

		//封装分页条件
		Pageable pageable = new PageRequest(page-1, rows);
		Page<WayBill> pages = wayBillService.findAll(pageable);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String []{"courier"});

		page2json(pages,jsonConfig);
		return NONE;
}

}

