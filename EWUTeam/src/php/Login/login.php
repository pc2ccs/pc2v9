<?php
	session_start();

	// $Id$

	//echo '<font color="white">UN: ' . $_SESSION['username'];
	//echo '<br/>CID: ' . $_SESSION['cid'] . '</font>';

	ini_set('display_errors', 'On');
	error_reporting(E_ALL | E_STRICT);	

	$error="";
	if(is_resource(@fsockopen('localhost', 3306))) {

		include("http://localhost:3306/JavaBridge/java/Java.inc");
		$server = java("ServerInterface")->getInstance();

		if(isset($_SESSION['cid']) && java_is_true($server->isLoggedIn($_SESSION['cid'])))
				header("Location: ../Team/TeamClient.php");

		if($_SERVER["REQUEST_METHOD"] == "POST") {
				try {
					$_SESSION['cid'] = "" . new Java("java.lang.String", $server->login($_POST['username'], $_POST['password']));
					$_SESSION['username'] = $_POST['username'];
					header("Location: ../Team/TeamClient.php");
				} catch (JavaException $exception) { $error = "Couldn't log in!"; }
		}//end if($_SERVER...)
	} else {
		$error = "Java bridge could not be established! Contact your site administrator.";
	}
?>


<html>
<title>PC^2 Contest - Team Login</title>
<link href="../Team/tab_style/style.css" rel="stylesheet" type="text/css" />

<center>
<body onload="setFocus()">
<div id="menu" style="width:500px;">
       <h1 style="color:#f3f1eb; font-size:48px;font-family:sans-serif; line-height: 36px;" >PC^2 Login Page</h1><hr />
</div>

<div style="padding-bottom:5px;">
	<div style=" width: 450px; padding: 5px">
		<a href="http://www.ewu.edu/"><img style="background: white; border:0;" src="../Team/images/EWU.png" WIDTH=55 HEIGHT=55></a>
		<a href="http://www.ecs.csus.edu/pc2/"><img style="background: white; border:0;" src="../Team/images/sac_state.png" WIDTH=55 HEIGHT=55 href="http://www.ecs.csus.edu/pc2/"></a>
		<a href="http://icpc.baylor.edu/"><img style="background: white; border:0;" src="../Team/images/acm-icpc-logo.png" WIDTH=auto HEIGHT=55></a>
	</div>
</div><hr style="width:500px;"/>

<div style="border:groove; width:275px; height:auto; padding:10px; border-color:white;">
	<form name="input" method="post">
	<table style="color:#f3f1eb">
	     <tr><td>Username:</td><td> <input type="text" maxlength="10" id="username" name="username" style="width:150" required /></td></tr>
            <tr><td>Password:</td> <td><input type="password" maxlength="20" name="password" style="width:150" required /></td></tr>				
            <tr><td/><td><input type="submit"  style="width:75" onclick="" value="Submit"/>
	        	    <input type="button"  style="width:75" onclick="reset()" value="Clear"/></td></tr>
	</table>
	</form>
	<div style="color:#ffffff"><?php echo $error; ?></div>
</div>

<input type="button" value="Testing" onclick="location.href='../Test/testMain.php'" />
<input type="button" value="Scoreboard" onclick="location.href='../Team/ScoreboardMain.php'" />

<script>
	function reset() {
		document.getElementById("username").reset();
		document.getElementById("password").reset();
	}

	function setFocus() {
		document.getElementById("username").focus();
	}
</script>
</body>
</center>
</html>



