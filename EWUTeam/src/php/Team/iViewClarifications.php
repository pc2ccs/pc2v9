<?php
	if(version_compare(phpversion(),'4.3.0')>=0) { 
	    if(!ereg('^SESS[0-9a-zA-Z]+$',$_REQUEST['SESSION_NAME'])) { 
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
	if (isset($_SESSION['cid'])) {
		if( java_is_false( $server->isLoggedIn($_SESSION['cid']))) {
				session_unset();
				print "<script type='text/javascript'>alert('Your session has expired. Please log back in.');window.open('../Login/login.php','_parent');</script>";
				opener.location.reload();
				exit();
			}
	
			try {	//Filling in respective arrays. The PHP scripts are down at their respective markup components.
					$clararray = $server->getClarificationsById($_SESSION['cid']);
					if (!java_is_null($clararray)) {
						$JavaClarifications  = java_cast($clararray , "array");
					}
			} catch(JavaException $exception) {
				//$error = "Could not get problems!";
			}//end catch
		}//end if(is_resource(. . .)) {}
	}
?>

<html>
<script src="JQuery/jquery-1.9.1.js"></script>
<script src="JQuery/jquery-ui.js"></script>

<link href="table_sorter/css/theme.default.css" rel="stylesheet">
<script type="text/javascript" src="table_sorter/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="table_sorter/js/jquery.tablesorter.widgets.min.js"></script>
<script type="text/javascript">




$(function() {
	$( "#tabs" ).tabs();
	$('table').tablesorter({
			sortList: [[2,1]],
			widgets        : ['zebra', 'columns'],
			usNumberFormat : true,
			sortRestart    : true
	});
});


</script>

<body>

<table empty-cells:show; id="veiwClarificationTable" class="tablesorter" border=1px">
<thead>
	<tr>
		<th>Site</th>
		<th>Team</th>
		<th>Clar Id</th>
		<th>Time</th>
		<th style="text-align:left">Problem</th>
		<th style="text-align:left">Question</th>
		<th style="text-align:left">Answer</th>
	</tr>
</thead>
<tbody>

	<?php
	if (isset($JavaClarifications)) {
		foreach ($JavaClarifications as $value) {
			echo "<tr>";
			echo "<td>".$value->getSiteNumber()."</td>";
			echo "<td>".$value->getTeam()->getDisplayName()."</td>";
			echo "<td>".$value->getNumber()."</td>";
			echo "<td>".$value->getSubmissionTime()."</td>";
			echo "<td>".$value->getProblem()->getName()."</td>";
			
			//escape
			$question = $value->getQuestion();
			$answer = $value->getAnswer();
			
			$question = str_replace("<","&lt",$question);
			$answer = str_replace("<","&lt",$answer);
			
			echo "<td>".$question."</td>";
			
			if($answer == "")
				echo "<td style = 'font-style:italic;'>Pending...</td>";
			else
				echo "<td>".$answer."</td>";
			echo "</tr>";
		}
	}
	?>
</tbody>
</table>

</body>
</html>
