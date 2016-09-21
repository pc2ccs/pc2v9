<?php

	if(version_compare(phpversion(),'4.3.0')>=0) { 
	    if(!ereg('^SESS[0-9a-zA-Z]+$',$_REQUEST['SESSION_NAME'])) { 
	        header("Location: ../index.html");
	    } 
	    output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
	    session_name($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	require_once("../lib/Java.inc");

	$server = java("ServerInterface")->getInstance();
	try {
		$runs = $server->JudgmentOccurred($_SESSION['username']);
		if(!java_is_null($runs))
			{
				
				$arr = array(
                java_cast($runs->getSiteNumber(), "int"),
                java_cast($runs->getNumber(), "int"),
                java_cast($runs->getProblem()->getName(), "String"),
                java_cast($runs->getLanguage()->getName(), "String"),
                java_cast($runs->getSubmissionTime(), "int"),
		java_cast($runs->getJudgementName(), "String"),
		java_cast($runs->isPreliminaryJudged(),"boolean")	
                );
        
				echo json_encode($arr);
				
			}
	} catch (JavaException $exception) {
		echo 'failed!';
	}
?>
