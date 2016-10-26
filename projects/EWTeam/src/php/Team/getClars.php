<?php

   // $Id$
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
			$clars = $server->clarificationOccurred($_SESSION['username']);
			if(!java_is_null($clars))
			{
				
				$arr = array(
                java_cast($clars->getSiteNumber(), "int"),
                java_cast($clars->getTeam()->getDisplayName(), "String"),
                java_cast($clars->getNumber(), "int"),
                java_cast($clars->getSubmissionTime(), "int"),
                java_cast($clars->getProblem()->getName(), "String"),
				java_cast($clars->getQuestion(), "String"),
				java_cast($clars->getAnswer(), "String")
                );
        
				echo json_encode($arr);
				
			}
		} catch (JavaException $exception) {
			echo 'failed!';
		}
?>
