<!DOCTYPE html>
<!-- $Id$ -->
<?php
	if(version_compare(phpversion(),'4.3.0')>=0) { 
	    if(!ereg('^SESS[0-9a-zA-Z]+$',$_REQUEST['SESSION_NAME'])) { 
	
		header("Location: ../index.html");
	    } 
	    output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
	    session_name($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	if(is_resource(@fsockopen('localhost', 3306))) {
		include("../lib/Java.inc");
	} else {
		session_unset();
		header("Location: ../Login/login.php");
		exit();
	}

	$server = java("ServerInterface")->getInstance();

	if( !java_is_true( $server->isLoggedIn($_SESSION['cid']))) {
		session_unset();
		$_SESSION['error'] = "Your session has expired. Please log back in and resubmit.";
		header("Location: ../Login/login.php");
		exit();
	}


	if($_FILES["file"]["size"] > 512000) {
	
		$_SESSION['error'] = "Input file larger than max input size (512KB).";
		
	} elseif($_FILES["file"]["error"] > 0 || $_POST["probs"] == "" || $_POST["lang"]=="") {
	
		$_SESSION['error'] = "Please fill out all required fields.";

		//echo "Error: " . $_FILES["file"]["error"];
	} elseif($_FILES['file']['size'] > 0) {
		
		$fname = realpath("../lib/file_uploads").'/'.$_SESSION["cid"].time().".".$_FILES["file"]["name"];
		move_uploaded_file($_FILES["file"]["tmp_name"], $fname);

		try { 
			$server->submitProblem($_SESSION["cid"],$_POST["probs"], $_POST["lang"], $fname, NULL);
			
		} catch(JavaException $ex) {
			$_SESSION['error'] = "File could not be submitted!";
			//$_SESSION['error'] = "" . $ex->toString();
		}
	} else {
		$_SESSION['error'] = "Input file larger than max input size (512KB).";
	}
	header("Location: TeamClient.php?SESSION_NAME=".session_name());
?>
