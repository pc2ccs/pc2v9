
<?php
	session_start();
	
	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();
		try {
			$runsArray = $server->getRuns($_SESSION['cid']);
			$JavaRuns = java_cast($runsArray, "array");
			
		} catch(JavaException $exception) {
			$error = "Could not get problems!";
		}//end catch
	}//end if
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
		<th>Time</th>
		<th>Status</th>
		<th>Language</th>
	</tr>
	</thead>
	<tbody style="overflow-y: scroll; overflow-x: hidden; height: 30px;">
	<?php	foreach ($JavaRuns as $value)  {
		echo "<tr>";
		echo "<td>".$value->getSiteNumber()."</td>";
		echo "<td>".$value->getNumber()."</td>";
		echo "<td>".$value->getProblem()->getName()."</td>";
		echo "<td>".$value->getSubmissionTime()."</td>";
		echo "<td>".$value->getJudgementName()."</td>";
		echo "<td>".$value->getLanguage()->getName()."</td>";
		echo "</tr>"; }
	?>
	</tbody>
</table>
</body>
</html>
