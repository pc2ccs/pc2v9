<?php

	
	if(version_compare(phpversion(),'4.3.0')>=0) { 
  		  if(!ereg('^SESS[0-9a-zA-Z]+$',$_REQUEST['SESSION_NAME'])) { 
  		      header("Location: ../index.html"); 
 		   } 
  		  output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
  		  session_name($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("../lib/Java.inc");
		
		$server = java("ServerInterface")->getInstance();
		
		if( !java_is_true( $server->isLoggedIn($_SESSION['cid']) ) )
		{
			
			// - Don't usnet the session. Only unset the CID so that we can have the error persist through to the login page.
			
			session_unset();
			$_SESSION['error'] = "Your session has expired. Please log back in and resubmit your clarification.";
			header("Location: ../Login/login.php");
			exit();
		}
		try
		{
			$server->submitClarification($_SESSION['cid'], $_POST['clarProbs'], $_POST['clarificationTextArea']);
			echo json_encode("success");
		}
		catch(JavaException $e)
		{
			echo json_encode("failed");
		}
	}
	
?>
