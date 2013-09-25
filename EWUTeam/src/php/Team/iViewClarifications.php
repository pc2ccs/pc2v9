<?php
	session_start();
	
	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();


		try {	//Filling in respective arrays. The PHP scripts are down at their respective markup components.
			$clararray = $server->getClarificationsById($_SESSION['cid']);
			$JavaClarifications  = java_cast($clararray , "array");
			
		} catch(JavaException $exception) {
			//$error = "Could not get problems!";
		}//end catch
	}//end if(is_resource(. . .)) {}
?>

<html>

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.1/jquery-ui.js"></script>

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
		<th>Status</th>
		<th style="text-align:left">Problem</th>
		<th style="text-align:left">Question</th>
		<th style="text-align:left">Answer</th>
	</tr>
</thead>
<tbody>

	<?php
		foreach ($JavaClarifications as $value) {
			echo "<tr>";
			echo "<td>".$value->getSiteNumber()."</td>";
			echo "<td>".$value->getTeam()->getDisplayName()."</td>";
			echo "<td>".$value->getNumber()."</td>";
			echo "<td>".$value->getSubmissionTime()."</td>";

			if ($value->isAnswered() != "") { 
				if ($value->isSendToAll() != "")
					echo "<td>Broadcast</td>";
				else
					echo "<td>Answered</td>";
			}
			else 
				echo "<td>Not Answered</td>";

			echo "<td>".$value->getProblem()->getName()."</td>";
			echo "<td>".$value->getQuestion()."</td>";
			echo "<td>".$value->getAnswer()."</td>";
			echo "</tr>";
		}
	?>
</tbody>
</table>

</body>
</html>
