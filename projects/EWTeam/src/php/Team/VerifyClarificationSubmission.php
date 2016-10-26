<?php 
		// $Id: getClars.php 51 2013-10-11 22:51:36Z laned $
		if(version_compare(phpversion(),'4.3.0')>=0) {
			if(!preg_match ( '/^SESS[0-9a-zA-Z]+$/',$_REQUEST['SESSION_NAME'])) {
				header("Location: ../index.html");
			}
			output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']);
			session_name($_REQUEST['SESSION_NAME']);
		}
		session_start();
		require_once("../lib/Java.inc");
		
		$server = java("ServerInterface")->getInstance();
		try {
			$clars = $server->clarificationSubmitOccurred($_SESSION['username']);
			if(!java_is_null($clars))
			{
				$arr = 'SubmissionOccurred';
		
				echo json_encode($arr);
			}
			else
				echo json_encode('The clar returned was null.');
		} catch (JavaException $exception) {
			echo json_encode('failed!');
		}
?>