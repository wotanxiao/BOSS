package com.itheima.bos.dao.base.impl;

import java.util.Arrays;

import com.itheima.utils.PinYin4jUtils;

public class PinyinTest {

	public static void main(String[] args) {
		String province = "广东省";
		String city = "深圳市";
		String district = "宝安区";

		// 简码 GDSZBA 城市编码 SHENZHEN

		province = province.substring(0, province.length() - 1);
		city = city.substring(0, city.length() - 1);
		district = district.substring(0, district.length() - 1);

//		String hanziToPinyin = PinYin4jUtils.hanziToPinyin(city, "").toUpperCase();
//		System.out.println(hanziToPinyin);
		
		String[] headByString = PinYin4jUtils.getHeadByString(province+city+district, true);
		
		String stringArrayToString = PinYin4jUtils.stringArrayToString(headByString);
		System.out.println(stringArrayToString);

	}

}
