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
			$clock = $server->getClock($_SESSION['cid']);
		} catch (JavaException $exception) {
			echo 'failed!';
		}
		
		
		$timeleft = java_cast($clock->getRemainingSecs(),"integer");

		$hours = (int)($timeleft / 3600);
		$min = (int)(($timeleft%3600)/60);
		
		$time = array(
    		"hour"  => $hours,
    		"min" => $min,
		);
		
		echo json_encode($time);

		?>
