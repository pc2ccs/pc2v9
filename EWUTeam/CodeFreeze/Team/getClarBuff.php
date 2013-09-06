<?php
/*
session_start();
	
	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("http://localhost:3306/JavaBridge/java/Java.inc");
		$server = java("ServerInterface")->getInstance();


		try {	//Filling in respective arrays. The PHP scripts are down at their respective markup components.
			$clararray = $server->getClarificationsById($_SESSION['cid']);
			$JavaClarifications  = java_cast($clararray , "array");
			foreach ($JavaRuns as $value) {
				$PHPrunsite[$i]=$value->getSiteNumber();
				$i++;
			}
			echo "var result = new Array("<?=implode("\",\"",".$PHPrunsite."); ?>");";
		} catch(JavaException $exception) {
			//$error = "Could not get problems!";
			echo "error";
		}//end catch
	}//end if(is_resource(. . .)) {}

*/



session_start();
if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("http://localhost:3306/JavaBridge/java/Java.inc");
		$server = java("ServerInterface")->getInstance();


	try {
		$clarbuffarray = $server->getClarBuffer();
		$JavaBuffClarifications  = java_cast($clarbuffarray , "array");
		echo "there";
		if($JavaBuffClarifications )
		{
			echo "stuff in buffer";
			foreach ($JavaClarifications as $value)
			{
				
				echo $value->getAnswer();
				
			}
		}
	} catch(JavaException $exception) {
			//$error = "Could not get problems!";
			echo "error";
			//echo "<script type="text/javascript">";
			//echo "var table = document.getElementById("veiwClarificationTable");";
			//echo "var row= table.insertRow(1);";
			//echo "var cell1=row.insertCell(0);";
			//echo "cell1.innerHTML="newyeah";";
			//echo "</script>";
		}
}//end if(is_resource(. . .)) {}


function say_hello() {
 return 'hello world';
}

echo say_hello();
?>




