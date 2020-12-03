<?php /* Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. */ ?>
<?php
?>

<html>
<link href="tab_style/sentStyle.css" rel="stylesheet" type="text/css" />

<script>
function refresh() {
	document.getElementById('scoreFrame').contentWindow.location.reload(true);
}
function redirect() {
	window.location.replace("../index.html");
}

</script>

<div id="tabs-5" class="tabdiv">
	<p style="font-weight:bold;font-size:125%;text-align:center;">Ranked Scoreboard for the contest</p>
	<input type="button" onclick="redirect()" value="Home" class="scoreboardButton">
	<input type="button" onclick="refresh()" value="Refresh" style="float: right;" class="scoreboardButton">
	<iframe seamless frameborder="0" name="scoreFrame" id="scoreFrame" width="100%" height="800" src="iScoreBoard.php"></iframe>
</div>
</html>
