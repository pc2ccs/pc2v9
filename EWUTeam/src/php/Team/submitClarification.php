<?php
	session_start();
	include("../lib/Java.inc");
	
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
