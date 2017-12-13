<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>地图合表选择</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/mergemap.do" method="post" enctype="multipart/form-data">
选择表:<input id="file" type="file" multiple name="file1" onchange="change()"><br>
<textarea type="text" id="example" disabled="disabled" rows="5" cols="30"></textarea>
<br>
<input type="submit" value="合表">
</from>
</div>
</body>
<script type="text/javascript">
function change() {
	var m=document.getElementById("file").files;
	document.getElementById("example").value="";
	for(var i=0;i<m.length;i++){
		document.getElementById("example").value=document.getElementById("example").value+m[i].name+"\r\n";
	}
}
</script>
</html>