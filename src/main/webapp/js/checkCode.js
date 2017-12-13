			var code1 ; //在全局定义验证码 
			var code2 ; //在全局定义验证码 
			//产生验证码
			window.onload = function createCode(){
				 code1 = ""; 
				 code2 = ""; 
				 var codeLength = 7;//验证码的长度
				 var checkCode1 = document.getElementById("code1"); 
				 var checkCode2 = document.getElementById("code2"); 
				 var random = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
				 'S','T','U','V','W','X','Y','Z');//随机数
				 for(var i = 0; i < codeLength; i++) {//循环操作
					var index = Math.floor(Math.random()*36);//取得随机数的索引（0~35）
					code1 += random[index];//根据索引取得随机数加到code上
				}
				 for(var i = 0; i < codeLength; i++) {//循环操作
						var index = Math.floor(Math.random()*36);//取得随机数的索引（0~35）
						code2 += random[index];//根据索引取得随机数加到code上
					}
				checkCode1.value = code1;//把code值赋给验证码
				checkCode2.value = code2;//把code值赋给验证码
			};
			
			//产生验证码
			function createCode1(){
				 code1 = ""; 
				 var codeLength = 7;//验证码的长度
				 var checkCode1 = document.getElementById("code1"); 
				 var random = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
				 'S','T','U','V','W','X','Y','Z');//随机数
				 for(var i = 0; i < codeLength; i++) {//循环操作
					var index = Math.floor(Math.random()*36);//取得随机数的索引（0~35）
					code1 += random[index];//根据索引取得随机数加到code上
				}
				checkCode1.value = code1;//把code值赋给验证码
			};
			//产生验证码
			function createCode2(){
				 code2 = ""; 
				 var codeLength = 7;//验证码的长度
				 var checkCode2 = document.getElementById("code2"); 
				 var random = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
				 'S','T','U','V','W','X','Y','Z');//随机数
				 for(var i = 0; i < codeLength; i++) {//循环操作
					var index = Math.floor(Math.random()*36);//取得随机数的索引（0~35）
					code2 += random[index];//根据索引取得随机数加到code上
				}
				checkCode2.value = code2;//把code值赋给验证码
			};
			//校验验证码
			function validate1(){
				var inputCode = document.getElementById("inputcode1").value.toUpperCase(); //取得输入的验证码并转化为大写      
				if(inputCode.length <= 0) { //若输入的验证码长度为0
					alert("请输入验证码！"); //则弹出请输入验证码
				}       
				else if(inputCode != code ) { //若输入的验证码与产生的验证码不一致时
					alert("验证码输入错误！@_@"); //则弹出验证码输入错误
					createCode();//刷新验证码
					document.getElementById("code1").value = "";//清空文本框
				}       
				else { //输入正确时
					alert("^-^"); //弹出^-^
				}           
			}
			//校验验证码
			function validate2(){
				var inputCode = document.getElementById("inputcode2").value.toUpperCase(); //取得输入的验证码并转化为大写      
				if(inputCode.length <= 0) { //若输入的验证码长度为0
					alert("请输入验证码！"); //则弹出请输入验证码
				}       
				else if(inputCode != code ) { //若输入的验证码与产生的验证码不一致时
					alert("验证码输入错误！@_@"); //则弹出验证码输入错误
					createCode();//刷新验证码
					document.getElementById("code2").value = "";//清空文本框
				}       
				else { //输入正确时
					alert("^-^"); //弹出^-^
				}           
			}