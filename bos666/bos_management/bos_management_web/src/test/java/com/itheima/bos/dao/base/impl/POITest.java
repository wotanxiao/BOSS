package com.itheima.bos.dao.base.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class POITest {

	public static void main(String[] args) throws Exception {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("C:\\Users\\Alpha\\Desktop\\a.xls"));

		// 读取工作簿
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		// 遍历行
		for (Row row : sheet) {
			// 获取行号
			int rowNum = row.getRowNum();
			if (rowNum == 0) {
				continue;
			}

			for (Cell cell : row) {
				String value = cell.getStringCellValue();
				System.out.print(value + "\t");
			}
			System.out.println();
		}

		// 释放资源
		hssfWorkbook.close();

	}

}
