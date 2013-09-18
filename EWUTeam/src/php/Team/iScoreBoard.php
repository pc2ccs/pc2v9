<?php
	session_start();
	
	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();
		try {
			$standingArray = $server->getStandings($_SESSION['cid']);
			$JavaStanding = java_cast($standingArray , "array");
			
			
		} catch(JavaException $exception) {
			//$error = "Could not get problems!";
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
			sortList: [[1,0]],
			widgets        : ['zebra', 'columns'],
			usNumberFormat : true,
			sortRestart    : true
	});
});
</script>

<body>
<i>Ranked Scoreboard for the contest:</i>

<table empty-cells:show; id="veiwStandingTable" class="tablesorter" border=1px" >
		<thead>
			<tr>
		 		<th>Team Name</th>
				<th>Rank</th>
				<th>Solved</th>
				<th>Penalty Points</th>		
			</tr>
		</thead>
		<tbody>
			<?php	foreach ($JavaStanding as $value)  {
					echo "<tr>";
					echo "<td>".$value->getClient()->getDisplayName()."</td>";
					echo "<td>".$value->getRank()."</td>";
					echo "<td>".$value->getNumProblemsSolved()."</td>";
					echo "<td>".$value->getPenaltyPoints()."</td>";
					echo "</tr>";
				}
			?>
	</tbody>
</table>

</body>
</html>
