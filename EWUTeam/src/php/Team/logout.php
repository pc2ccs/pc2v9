<?php 
		session_start();
		session_destroy();

		include("../lib/Java.inc");

		$server = java("ServerInterface")->getInstance();
		$error="";

		try {
			$server->logout(new Java("java.lang.String", $_SESSION['cid']));

		} catch (JavaException $exception) {
			$error = "Couldn't log out!";
			echo $error;
		}


?>