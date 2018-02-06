<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<!-- 认证的用户可以看到被标签包裹的内容 -->
	<shiro:authenticated>
	当前用户已经通过认证了
</shiro:authenticated>
	<hr>
	<!-- 检查用户是否拥有对应的权限,如果有就可以看到内容,没有就看不到 -->
	<shiro:hasPermission name="courier_pageQuery">
	你拥有aa权限
</shiro:hasPermission>
	<hr>
	<!-- 检查用户是否拥有对应的角色,如果有就可以看到内容,没有就看不到 -->
	<shiro:hasRole name="admin">
你拥有admin权限
</shiro:hasRole>

</body>
</html>