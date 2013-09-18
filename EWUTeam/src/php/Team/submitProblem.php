<!DOCTYPE html>
<!-- $Id$ -->
<?php
	session_start();
	include("../lib/Java.inc");
	$server = java("ServerInterface")->getInstance();
	
	$error_types = array(
        0=>"There is no error, the file uploaded with success",
        1=>"The uploaded file exceeds the upload_max_filesize directive in php.ini",
        2=>"The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form",
        3=>"The uploaded file was only partially uploaded",
        4=>"No file was uploaded",
        6=>"Missing a temporary folder"
    ); 

	if($_FILES["file"]["size"] > 512000) {
		$_SESSION['error'] = "Input file larger than max input size (512KB).";
	} elseif($_FILES["file"]["error"] > 0) {
	    $_SESSION['error'] = $error_types[ $_FILES["file"]["error"] ];
	} elseif($_FILES['file']['size'] > 0) {

		//echo "Upload: " . $_FILES["file"]["name"]."<br>";
		//echo "Stored in " . $_FILES["file"]["tmp_name"]."<br>";
		
		$fname = realpath("../uploads").'/'.$_SESSION["cid"].time().".".$_FILES["file"]["name"];
		//echo $fname;
		move_uploaded_file($_FILES["file"]["tmp_name"], $fname);

		try { 
			$server->submitProblem($_SESSION["cid"],$_POST["probs"], $_POST["lang"], $fname, NULL);
			header("Location: iSubmit.php");
		} catch(JavaException $ex) {
			$_SESSION['error'] = "java stuff failed!";
		}

		//echo 'File size: ' . $_FILES["file"]["size"] . '<br/>';

		//echo "moved file to " . $_FILES["file"]["name"]."<br>";
		//echo "moved file to (tmp_name)" . $_FILES["file"]["tmp_name"]."<br>";
		//echo time();
	} else {
		$_SESSION['error'] = "Input file larger than max input size (512KB).";
	}
	header("Location: iSubmit.php");
?>
