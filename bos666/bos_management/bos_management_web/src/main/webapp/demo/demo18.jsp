<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="../js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="../js/easyui/themes/icon.css">
<!-- 所有的JS插件都是依赖于JQuery,所以导入资源的时候,必须先导入jQuery -->
<script type="text/javascript" src="../js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="../js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="../js/easyui/locale/easyui-lang-zh_CN.js"></script>

<script type="text/javascript">
	$(function() {
		var index;

		$('#dg').datagrid({
			url : 'dg.json', //指定请求地址
			striped : true,//是否显示斑马线效果
			rownumbers : true,//是否显示行号
			pagination : true,//是否显示分页工具条
			singleSelect : true,//是否只允许单选
			onAfterEdit : function(index, row, changes) {//编辑完成以后会触发的函数
				console.log(index) // 被修改的行的行号,从0开始
				console.log(row)//被修改的行,修改后的数据组成的json字符串
				console.log(changes)//被修改的行,发生改变的数据组成的json字符串

			},
			columns : [ [ { // 指定列的属性
				field : 'cb',
				title : 'cb',
				width : 100,
				checkbox : true

			}, { // 指定列的属性
				field : 'id',
				title : '编号',
				width : 100,
				editor : {
					type : "numberbox"//格子里面数据的类型
				}
			}, {
				field : 'name',
				title : '名称',
				width : 100,
				editor : {
					type : "text"//格子里面数据的类型
				}
			}, {
				field : 'age',
				title : '年龄',
				width : 100,
				align : 'right',
				editor : {
					type : "numberbox"//格子里面数据的类型
				}
			} ] ],
			toolbar : [ {
				iconCls : 'icon-add',
				text : "添加",
				handler : function() {
					$('#dg').datagrid('insertRow', {
						index : 0, // 向第几行插入数据,索引从0开始
						row : {}
					//行里面的数据,如果什么都不写,就代表插入一个空行
					});

					// 打开编辑功能
					$('#dg').datagrid('beginEdit', 0)
				}
			}, '-', 
			
			<shiro:hasPermission name="courier_ssspageQuery">
			{
				iconCls : 'icon-remove',
				text : "删除",
				handler : function() {

					var rows = $('#dg').datagrid('getSelections')

					if (rows.length == 1) {
						// 获取选中的行的索引
						index = $('#dg').datagrid('getRowIndex', rows[0])
						// 编辑
						$('#dg').datagrid('deleteRow', index)
					}

				}
			}, '-', 
			</shiro:hasPermission>
			
			
			{
				iconCls : 'icon-edit',
				text : "编辑",
				handler : function() {

					var rows = $('#dg').datagrid('getSelections')

					if (rows.length == 1) {
						// 获取选中的行的索引
						index = $('#dg').datagrid('getRowIndex', rows[0])
						// 编辑
						$('#dg').datagrid('beginEdit', index)
					}

				}
			}, '-', {
				iconCls : 'icon-save',
				text : "保存",
				handler : function() {
					$('#dg').datagrid('endEdit', index)
				}
			}, '-', {
				iconCls : 'icon-help',
				text : "帮助",
				handler : function() {
					alert('帮助按钮')
				}
			} ]

		});
	})
</script>
</head>
<body>
	<!-- 很多控件都可以发起网络请求,AJAX  -->
	<table id="dg">

	</table>
</body>
</html>