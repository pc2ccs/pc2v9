<?php
		session_start();
		require_once("http://localhost:3306/JavaBridge/java/Java.inc");

		$server = java("ServerInterface")->getInstance();
		
		try {
			$clock = $server->getClock(new Java("java.lang.String", $_SESSION['cid']));
		} catch (JavaException $exception) {
			echo 'failed!';
		}


		$_SESSION['clock']=$clock->getElapsedSecs();
		echo $_SESSION['clock'];
		echo '<br/>';
		echo $_SESSION['clock']/60;
		echo '<br/>';
		echo ($_SESSION['clock']/60)/60;
?>
