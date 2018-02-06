package com.itheima.bos.dao.base.impl;

import java.util.List;

import org.bouncycastle.jce.provider.BrokenJCEBlockCipher.BrokePBEWithMD5AndDES;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.StandardRepository;
import com.itheima.bos.dao.system.RoleRepository;
import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.domain.system.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class StandardRepositoryTest {

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void test() {

		List<Role> list = roleRepository.findUid(236L);
		System.out.println(list.size());
	}

	@Test
	public void testAdd() {
		Standard standard = new Standard();
		standard.setName("李四四");
		standard.setMaxLength(100);
		standardRepository.save(standard);
	}

	@Test
	public void testUpdate() {
		Standard standard = new Standard();
		// 进行更改操作,必须传入主键
		standard.setId(1L);
		standard.setName("lisi");
		standard.setMaxLength(500);
		standardRepository.save(standard);
	}

	@Test
	public void testFindone() {

		Standard standard = standardRepository.findOne(1L);
		System.out.println(standard);
	}

	@Test
	public void testDelete() {

		standardRepository.delete(1L);
	}

	@Test
	public void testFindByName() {

		// 使用用户名进行查询

		List<Standard> list = standardRepository.findByName("李四");
		for (Standard standard : list) {
			System.out.println(standard);
		}

	}

	@Test
	public void testFindByNameLike() {

		List<Standard> list = standardRepository.findByNameLike("%李四%");
		for (Standard standard : list) {
			System.out.println(standard);
		}

	}

	@Test
	public void findByNameAndMaxLength() {

		List<Standard> list = standardRepository.findByNameAndMaxLength("李四", 100);
		for (Standard standard : list) {
			System.out.println(standard);
		}

	}

	@Test
	public void findByNameAndMaxLengthxxxxx() {

		List<Standard> list = standardRepository.findByNameAndMaxLengthxxxxx("李四", 100);
		for (Standard standard : list) {
			System.out.println(standard);
		}

	}

	@Test
	public void findByNameAndMaxLengthxxxxx2() {

		List<Standard> list = standardRepository.findByNameAndMaxLengthxxxxx2(100, "李四");
		for (Standard standard : list) {
			System.out.println(standard);
		}
	}

	@Test
	public void findByNameAndMaxLengthxxxxx3() {
		List<Standard> list = standardRepository.findByNameAndMaxLengthxxxxx3("李四", 100);
		for (Standard standard : list) {
			System.out.println(standard);
		}
	}

	@Test
	public void deleteByName() {
		standardRepository.deleteByName("zhangsan");
	}

	@Test
	public void updateMaxLengthByName() {
		standardRepository.updateMaxLengthByName(200, "李四");
	}

}
