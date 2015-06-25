
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
	
			try {
				$runsArray = $server->getRuns($_SESSION['cid']);
				if (!java_isnull($runsArray)) {
					$JavaRuns = java_cast($runsArray, "array");
				}
			} catch(JavaException $exception) {
				$error = "Could not get problems!";
			}//end catch
		}
	}//end if
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
			sortList: [[1,1]],
			widgets        : ['zebra', 'columns'],
			usNumberFormat : true,
			sortRestart    : true
	});
});
</script>

<body>


<table empty-cells:show; id="veiwRunTable" class="tablesorter" border=1px; height: 50px;" >
	<thead>
	<tr>	 
		<th>Site</th>
		<th>Run ID</th>
		<th>Problem</th>
		<th>Language</th>
		<th>Time</th>
		<th>Status</th>
	</tr>
	</thead>
	<tbody style="overflow-y: scroll; overflow-x: hidden; height: 30px;">
	<?php	foreach ($JavaRuns as $value)  {
		echo "<tr>";
		echo "<td>".$value->getSiteNumber()."</td>";
		echo "<td>".$value->getNumber()."</td>";
		echo "<td>".$value->getProblem()->getName()."</td>";
		echo "<td>".$value->getLanguage()->getName()."</td>";
		echo "<td>".$value->getSubmissionTime()."</td>";
		if($value->getJudgementName() == "") {
			echo "<td style = 'font-style:italic;'>Pending...</td>";
		} elseif ($value->isPreliminaryJudged() == 1 && $value->isFinalJudged() == "") {
			echo "<td style = 'font-style:italic;'>Preliminary - ".$value->getJudgementName()."</td>";
		} else{
			echo "<td>".$value->getJudgementName()."</td>";
		}
		echo "</tr>"; 
	}
	?>
	</tbody>
</table>
</body>
</html>

