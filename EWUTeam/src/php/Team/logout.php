<?php 
		session_start();
		session_destroy();

		require_once("http://localhost:3306/JavaBridge/java/Java.inc");

		$server = java("ServerInterface")->getInstance();
		$error="";

		try {
			$server->logout(new Java("java.lang.String", $_SESSION['cid']));

		} catch (JavaException $exception) {
			$error = "Couldn't log out!";
			echo $error;
		}


?>