<!DOCTYPE html>
<?php
// $Id$
// session_start();
if (version_compare ( phpversion (), '4.3.0' ) >= 0) {
	if (! isset($_REQUEST ['SESSION_NAME']) || ! ereg ( '^SESS[0-9a-zA-Z]+$', $_REQUEST ['SESSION_NAME'] )) {
		$_REQUEST ['SESSION_NAME'] = 'SESS' . uniqid ( '' );
	}
	output_add_rewrite_var ( 'SESSION_NAME', $_REQUEST ['SESSION_NAME'] );
	session_name ( $_REQUEST ['SESSION_NAME'] );
}

session_start ();

// $Id$
ini_set ( 'display_errors', 'On' );
error_reporting ( E_ALL | E_STRICT );

if (is_resource ( @fsockopen ( 'localhost', 50005 ) )) {
	
	include ("../lib/Java.inc");
	$server = java ( "ServerInterface" )->getInstance ();
	
	if (isset ( $_SESSION ['cid'] ) && java_is_true ( $server->isLoggedIn ( $_SESSION ['cid'] ) ))
		header ( "Location: ../Team/TeamClient.php" );
	
	if ($_SERVER ["REQUEST_METHOD"] == "POST") {
		try {
			$_SESSION ['cid'] = "" . new Java ( "java.lang.String", $server->login ( $_POST ['username'], $_POST ['password'], $_REQUEST ['SESSION_NAME'] ) );
			$_SESSION ['username'] = $_POST ['username'];
			header ( "Location: ../Team/TeamClient.php?SESSION_NAME=" . session_name () );
		} catch ( JavaException $exception ) {
			error_log("POST login.php JavaException for ".$_POST['username']." ".$exception, 0);
			$_SESSION ['error'] = "Couldn't log in! </br>Username / Password are incorrect!";
		}
	} // end if($_SERVER...)
} else {
	$_SESSION ['error'] = "Java bridge could not be established! Contact your site administrator.";
}
?>


<html>

<link href="../Team/tab_style/style.css" rel="stylesheet"
	type="text/css" />
<link href="../Team/tab_style/sentStyle.css" rel="stylesheet"
	type="text/css" />


<body onload="setFocus()">
	<title>PC^2 Contest - Team Login</title>


	<div class="login">Welcome to PC^2!</div>

	<hr />

	<div class="divContainer">
		<table>
			<tr>
				<td valign="top" class="tdLogin" id="loginForm">

					<div class="toolbarBanner">
						<div class="toolborder">LOGIN</div>
					</div>

					<form name="input" method="post" class="inputStyling">
						<table style="font-size: 100%; padding-bottom: 1%;">
							<tr>
								<td><b>Username:</b></td>
								<td><input required type="text" maxlength="10" id="username"
									name="username" style="" />
							
							</tr>
							<tr>
								<td><b>Password:</b></td>
								<td><input type="password" maxlength="20" name="password"
									style="" required />
							
							</tr>
						</table>

						<div style="padding-bottom: 5px;">
							<input type="submit" class="submitButtonLogin" onclick=""
								value="Submit" /> <input type="button" class="clearButtonLogin"
								onclick="reset()" value="Clear" /><br/>
						</div>

						<div class="errorStyling">
							<center><?php if(isset( $_SESSION['error'] )) { echo $_SESSION['error']; unset($_SESSION['error']); } ?></center>
						</div>

						<div class="hrDivider" />
						<br/> <input type="button" style="width: 100%;"
							value="Scoreboard"
							onclick="location.href='../Team/ScoreboardMain.php'" /><br />

					</form>

				</td>
				<td style="width: 1%;"></td>
				<td valign="top" class="tdAbout" id="loginForm">
					<div class="toolbarBanner">
						<div class="toolborder">ABOUT</div>
					</div>

					<p class="aboutStyle">
						<span style="font-size: 150%;">PC^2</span> is the <i>Programming
							Contest Control</i> System developed at <span
							style="color: #32b232;">California State University, Sacramento
							(CSUS)</span> in support of Computer Programming Contest
						activities of the ACM, and in particular the ACM International
						Collegiate Programming Contest (ICPC) and its Regional Contests
						around the world.  <?php if (isset($server)) { echo "Version: "; echo $server->getVersionNumberForPC2(); echo "-"; echo $server->getBuildNumberForPC2(); } ?>
					</p>
					<p class="aboutStyle">
						This web interface was created by <span style="color: #cc2211;">Eastern
							Washington University's</span> PC^2 Senior Project team and is
						still actively being worked on and improved.  <?php if (isset($server)) { echo "Version: "; echo $server->getVersionNumber(); echo "-"; echo $server->getBuildNumber(); } ?>
					</p> <input type="button" id="userGuide" value="User Guide"
					onclick="location.href='../doc/pc2userguide.pdf'" /><br />
				</td>
			</tr>
		</table>
	</div>

	<div id="banner">
		<a href="http://icpc.baylor.edu/"><img
			src="../Team/images/ACM_IBM_Long_logo.PNG" style="width: 47.0%;"></a>
		<a href="http://www.ecs.csus.edu/pc2/"><img
			src="../Team/images/sac_state_logo.png" style="width: 32.0%;"></a> <a
			href="http://www.ewu.edu/"><img id="easternLogo"
			src="../Team/images/EWU_Logo_Red.PNG" style="width: 20.0%;"></a>
	</div>

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

</html>
