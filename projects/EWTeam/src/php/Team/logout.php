<?php 
		if(version_compare(phpversion(),'4.3.0')>=0) { 
  			  if(!preg_match ( '/^SESS[0-9a-zA-Z]+$/',$_REQUEST['SESSION_NAME'])) { 
  			      header("Location: ../index.html"); 
 			   } 
  			  output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
  			  session_name($_REQUEST['SESSION_NAME']); 
		}

		session_start();
		session_destroy();

		if(is_resource(@fsockopen('localhost', 50005))) {
			include("../lib/Java.inc");
		} else {
			session_unset();
			header("Location: ../Login/login.php");
			exit();
		}

		//require_once("http://localhost:50005/JavaBridge/java/Java.inc");

		$server = java("ServerInterface")->getInstance();
		$error="";

		try {
			$server->logout(new Java("java.lang.String", $_SESSION['cid']));

		} catch (JavaException $exception) {
			$error = "Couldn't log out!";
			header("Location: ../index.html");
			//echo $error;
		}
?>
