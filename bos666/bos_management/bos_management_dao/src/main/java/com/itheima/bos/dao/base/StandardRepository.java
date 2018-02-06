package com.itheima.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.domain.base.Standard;

// 泛型1 : 操作的数据对应的实体类
// 泛型2 : 操作的数据对应的实体类中主键的类型
public interface StandardRepository extends JpaRepository<Standard, Long> {

	List<Standard> findByName(String name);

	// 根据用户名模糊查询
	List<Standard> findByNameLike(String name);

	// 根据用户名和最大长度进行查询
	List<Standard> findByNameAndMaxLength(String name, Integer maxLength);

	////////////////////////// 自定义查询操作//////////////////////////////
	// 根据用户名和最大长度进行查询
	@Query("from Standard where name = ? and maxLength = ?") // JPQL === HQL
	List<Standard> findByNameAndMaxLengthxxxxx(String name, Integer maxLength);

	// 根据用户名和最大长度进行查询
	@Query("from Standard where name = ?2 and maxLength = ?1") // JPQL === HQL
	List<Standard> findByNameAndMaxLengthxxxxx2(Integer maxLength, String name);

	// 根据用户名和最大长度进行查询
	@Query(value = "select * from T_STANDARD where C_NAME = ? and C_MAX_LENGTH = ?", nativeQuery = true) // JPQL ===
	List<Standard> findByNameAndMaxLengthxxxxx3(String name, Integer maxLength);

	////////////////////////// 自定义更新操作//////////////////////////////
	@Transactional
	@Modifying // 所有的更新数据的操作,都要添加这个注解
	@Query("delete from Standard s  where s.name = ?")
	void deleteByName(String name);

	@Transactional
	@Modifying // 所有的更新数据的操作,都要添加这个注解
	@Query("update from Standard set maxLength = ? where name = ?")
	void updateMaxLengthByName(Integer maxLength, String name);

}
