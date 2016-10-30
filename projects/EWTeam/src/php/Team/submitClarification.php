<?php

	
	if(version_compare(phpversion(),'4.3.0')>=0) { 
  		  if(!preg_match ( '/^SESS[0-9a-zA-Z]+$/',$_REQUEST['SESSION_NAME'])) { 
  		      header("Location: ../index.html"); 
 		   } 
  		  output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
  		  session_name($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	if(is_resource(@fsockopen('localhost', 50005))) 
	{
		include("../lib/Java.inc");
		
		$server = java("ServerInterface")->getInstance();
		
		if( java_is_false( $server->isLoggedIn($_SESSION['cid']) ) )
		{
			
			session_unset();
			$_SESSION['error'] = "Your session has expired. Please log back in and resubmit your clarification.";
			header("Http/1.0 406 Not acceptable");
			exit();
		}
		if (java_is_true($server->isContestStopped($_SESSION['cid']))) {
			echo json_encode("ContestStopped");
			exit();
		}
		try
		{
			$server->submitClarification($_SESSION['cid'], urldecode($_POST['clarProbs']), $_POST['clarificationTextArea']);
			echo json_encode("success");
		}
		catch(JavaException $e)
		{
			echo json_encode("failed");
		}
	}
	
?>
