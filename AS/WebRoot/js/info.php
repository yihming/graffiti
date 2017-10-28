<?php
  if($_POST)
  {
     var_dump($_POST);
	 echo "成功！";
  }
  else{
  
?>

<div align="center" style="width:600px;">

<form action="info.php" method="post">
	<table border="1">
		<tr>
			<td width="65">员工号</td>
			<td width="168">0610210042</td>
			<td width="168" colspan="2" rowspan="3"></td>
		</tr>
		<tr>
			<td>姓名</td>
			<td><input type="text" value="张三" /></td>
		</tr>
		<tr>
			<td>原密码</td><td><input type="password" name="oldpsw" /></td>
		</tr>
		<tr>
			<td>新密码</td><td><input type="password" name="newpsw" /></td>
			<td>确认新密码</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>性别</td>
			<td>
					男<input type="radio" name="male" checked />
					女<input type="radio" name="female" />
			</td>
			<td>出生日期</td><td>&nbsp;</td>
		</tr>
		<tr>
			<td>部门</td>
			<td>
				<select>
					<option selected>人事部</option>
					<option>销售部</option>
					<option>研究所</option>
				</select>
			</td>
			<td>职位</td>
			<td>
				<select>
					<option>主管</option>
					<option>主管助理</option>
					<option>职员</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>办公室</td>
			<td><input type="text" name="office" value="科技楼A区515"/></td>
		</tr>
		<tr>
			<td>座机</td><td><input type="text" name="telephone" value="58807193" /></td>
			<td>传真</td><td><input type="text" name="fax" value="58807194" /></td>
		</tr>
		<tr>
			<td>手机</td><td><input type="text" name="cellphone" value="13800138000" /></td>
			<td>E-mail地址</td><td><input type="text" name="email" value="zhangsan@mail.bnu.edu.cn" /></td>
		</tr>
	</table>
	<input type="password" name="confirmpsw" />
	<br />
  <input type="submit" name="submit1" value="更改信息" />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="button" name="return" value="返回" />
    
</form>
</div>
 <?}?>