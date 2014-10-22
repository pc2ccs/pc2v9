<?php

	session_start();
	
	if(is_resource(@fsockopen('localhost', 50005))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();
		try {
			$standingArray = $server->getStandings("");
			$JavaStanding = java_cast($standingArray , "array");
			
			
		} catch(JavaException $exception) {
			//$error = "Could not get problems!";
		}//end catch
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
			sortList: [[0,0]],
			widgets        : ['zebra', 'columns'],
			usNumberFormat : true,
			sortRestart    : true
	});
});
</script>

<body>

<table empty-cells:show; id="veiwStandingTable" class="tablesorter" border=1px" >
		<thead>
			<tr>
				<th>Rank</th>
		 		<th>Team Name</th>
				<th>Solved</th>
				<th>Time</th>
			<?php foreach($JavaStanding[0]->getProblemDetails() as $problem) {echo '<th>' . $problem->getProblem()->getName() . '</th>';}?>
						
			</tr>
		</thead>
		<tbody>
			<?php	foreach ($JavaStanding as $value)  {
					echo "<tr>";
					echo "<td>".$value->getRank()."</td>";
					echo "<td>".$value->getClient()->getDisplayName()."</td>";
					echo "<td>".$value->getNumProblemsSolved()."</td>";
					echo "<td>".$value->getPenaltyPoints()."</td>";

						foreach($value->getProblemDetails() as $problem) {
							if(java_is_true($problem->isSolved()))
								echo '<td style="text-align:center;background:#58FA58;border-color:white;">'.$problem->getAttempts().'/'.$problem->getSolutionTime().'</td>';
							else {
								if( 0 == java_cast($problem->getAttempts(), "long") )
									echo '<td style="text-align:center;border-color:white;">'.$problem->getAttempts().'/--</td>';
								else
									echo '<td style="text-align:center;background:#FF6666; border-color:white;">'.$problem->getAttempts().'/--</td>';
							}
						}
					
					echo "</tr>";
				}
			?>
	</tbody>
</table>

</body>
</html>
