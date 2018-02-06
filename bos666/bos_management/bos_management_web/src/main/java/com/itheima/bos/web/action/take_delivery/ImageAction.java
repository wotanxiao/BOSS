package com.itheima.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

import net.sf.json.JSONObject;

@Controller
@Scope("prototype") // 等价于applicationContext.xml中scope属性
@Namespace("/") // 等价于struts.xml中package节点中namespace属性
@ParentPackage("struts-default")
public class ImageAction extends ActionSupport {

	// 使用属性驱动获取数据
	// 文件
	private File imgFile;
	// 文件名
	private String imgFileFileName;

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}

	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}

	@Action("imageAction_save")
	public String save() throws IOException {

		Map<String, Object> map = new HashMap<>();

		try {
			// 保存文件的文件夹的名字
			String savePath = "upload";
			ServletContext servletContext = ServletActionContext.getServletContext();
			// 保存文件的文件夹的绝对磁盘路径
			String saveRealPath = servletContext.getRealPath(savePath);
			// 获取文件的后缀名
			// a.jpg .jpg jpg
			String suffix = imgFileFileName.substring(imgFileFileName.lastIndexOf("."));
			// 使用UUID获取文件名
			String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

			File destFile = new File(saveRealPath + "/" + fileName);
			FileUtils.copyFile(imgFile, destFile);

			// 文件在服务器上的地址 /bos_management_web/upload/a.jpg
			// 当前项目的路径 : /bos_management_web
			String contextPath = servletContext.getContextPath();
			map.put("error", 0);
			map.put("url", contextPath + "/upload/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
			map.put("error", 1);
			map.put("message", e.getMessage());
		}

		String json = JSONObject.fromObject(map).toString();

		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(json);

		return NONE;
	}

	// 处理图片空间
	@Action("imageAction_manager")
	public String manager() throws IOException {
		// 图片扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };

		// 保存文件的文件夹的名字
		String savePath = "upload";
		ServletContext servletContext = ServletActionContext.getServletContext();
		// 保存文件的文件夹的绝对磁盘路径
		String saveRealPath = servletContext.getRealPath(savePath);

		// 保存文件的文件夹
		File currentPathFile = new File(saveRealPath);

		// 遍历目录取的文件信息
		List<Hashtable> fileList = new ArrayList<Hashtable>();
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}

		JSONObject result = new JSONObject();
		// 保存图片的路径

		String contextPath = servletContext.getContextPath();
		result.put("current_url", contextPath + "/upload/");
		// 图片的信息
		result.put("file_list", fileList);

		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码
		response.setContentType("application/json;charset=UTF-8");
		// 写出数据
		response.getWriter().write(result.toString());

		return NONE;
	}
}
