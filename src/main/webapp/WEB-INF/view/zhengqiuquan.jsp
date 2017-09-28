<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>导表选择</title>
</head>
<body>
	<form action="${pageContext.request.contextPath}/excelUpdate1.do" method="post">
		表名 :
		<textarea name="excelName" cols="60" rows="10"></textarea>
		<br>导入服务器:<label><input name="resoure" type="checkbox" value="1" />1服 </label>
		<label><input name="resoure" type="checkbox" value="2" />2服</label>
		<label><input name="resoure" type="checkbox" value="3" />3服 </label>
		<label><input name="resoure" type="checkbox" value="4" />4服</label>
		<label><input name="resoure" type="checkbox" value="5" />5服</label>
		<label><input name="resoure" type="checkbox" value="8" />gve服(8服)</label>
		<label><input name="resoure" type="checkbox" value="9" />V1.5.0服(9服)</label>
		<br>excel目录库:<label><input name="excel" type="radio" checked="checked" value="1" />2.0.0 </label>
		<label><input name="excel" type="radio" value="2" />develop </label>
		<label><input name="excel" type="radio" value="3" />gve </label> 
		<label><input name="excel" type="radio" value="4" />horsezqq</label>
		<label><input name="excel" type="radio" value="7" />horsewfb</label>
		<label><input name="excel" type="radio" value="5" />newmap</label>
		<br>数据是否可删除<input name="delete" type="radio" value="1" />是</label>
		<label><input name="delete" checked="checked" type="radio" value="0" />否</label>
		<br>用户名 :<input type="text" name="username" value=""><br>
		<br>
		<!-- 打开数据可删除   把数据可删除的注释去掉，然后在action地址中的excelUpdate改为excelUpdate1 即可 -->
		<input type="submit" value="导表" onclick="return check(this.form)"><br>
	</form>
</body>
<script type="text/javascript">
         function check(form) {

          if(form.username.value=='') {
                alert("请输入用户帐号!");
                form.username.focus();
                return false;
           }
         return true;
         }
</script>
</html>