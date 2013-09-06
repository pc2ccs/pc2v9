<!DOCTYPE html>
<?php
?>

<html>

<script>
function refresh() {
	document.getElementById('scoreFrame').contentWindow.location.reload(true);
}
</script>

<div id="tabs-5" class="tabdiv">
	<div style="float:right;"><input type="button" onclick="refresh()" value="Refresh" style="float: right; border: 0px; background-color:transparent;font-weight:bold;"></div>
	<iframe seamless frameborder="0" name="scoreFrame" id="scoreFrame" width="100%" height="800" src="iScoreBoard.php"></iframe>
</div>
</html>