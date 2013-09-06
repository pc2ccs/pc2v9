<?php
	session_start();
	require_once("http://localhost:3306/JavaBridge/java.Java.inc");
	
	$server = java("ServerInterface")->getInstance();
	
	try
	{
		$server->submitClarification($_SESSION['cid'], $_POST['clarProbs'], $_POST['clarificationTextArea']);
	}
	catch(JavaException $e)
	{
		echo "failed";
	}

	header("Location: iSendClarification.php");
?>
