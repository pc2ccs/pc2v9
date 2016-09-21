<?php

?>

<html>
<title>PC^2 Test Page</title>
<link href="../Team/tab_style/style.css" rel="stylesheet" type="text/css" />
<body>
<font color="#f3f1eb">

<div>
<input type="button" value="Login Page" onclick="location.href='../Login/login.php'" style="width:75px'" />
</div>
<br/>

<table style="color:#f3f1eb" border="1px" bordercolor="#f3f1eb" cellpadding="5">
	<tr>
		<td align="right"><b>Login Test:</b></td>
		<td>This test will create 200 logins continuously to test the system.</td>
		<td><input type="button" value="0-199" onclick="location.href='loginTest.php'" style="width:75px"/></td>
	</tr>
	<tr>
		<td align="right"><b>Login Test A:</b></td>
		<td>This test will create 200 logins continuously to test the system.</td>
		<td><input type="button" value="200-399" onclick="location.href='loginTestA.php'" style="width:75px"/></td>
	</tr>
	<tr>
		<td align="right"><b>Login Test B:</b></td>
		<td>This test will create 200 logins continuously to test the system.</td>
		<td><input type="button" value="400-599" onclick="location.href='loginTestB.php'" style="width:75px"/></td>
	</tr>
	<tr>
		<td align="right"><b>Login Test C:</b></td>
		<td>This test will create 2000 logins continuously to test the system.</td>
		<td><input type="button" value="600-799" onclick="location.href='loginTestC.php'" style="width:75px"/></td>
	</tr>
	<tr>
		<td align="right"><b>Submit Test:</b></td>
		<td>This test will send 1000 submissions.</td>
		<td><input type="button" value="test" onclick="submitTest.php" style="width:75px" disabled/></td>
	</tr>

</table>

</font>
</body>
</html>