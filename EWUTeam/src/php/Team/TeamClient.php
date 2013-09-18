<!DOCTYPE html>
<?php
	session_start();
	//echo '<font color="white">UN: ' . $_SESSION['username'];
	//echo '<br/>CID: ' . $_SESSION['cid'] . '</font>';

	if(!isset($_SESSION['cid']))
		header("Location: ../index.html");

	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();

		if(java_is_false($server->isLoggedIn($_SESSION['cid'])))
			header("Location: ../index.html");

	}//end if(is_resource(. . .)) {}
?>


<html lang="en">
<head>
<title>PC^2 Team Client <?php
echo " - " . $_SESSION['username'];
?></title>
<meta charset="utf-8" />

<link href="tab_style/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="tab_style/jquery-1.2.6.min.js"></script>
<script type="text/javascript" src="tab_style/jquery-ui-personalized-1.5.2.packed.js"></script>
<script type="text/javascript" src="tab_style/sprinkle.js"></script>


<script type="text/javascript">
function exit() {
	$.post('logout.php', {username: "<?php echo $_SESSION['cid'] ?>"}, 
	function x(){ window.location = "../index.html"; return; });
}

function refreshRunView() {
	document.getElementById('runViewFrame').contentWindow.location.reload(true);
}

function refreshClarView() {
	document.getElementById('clarViewFrame').contentWindow.location.reload(true);
}

function refreshScoreBoard() {
	document.getElementById('scoreFrame').contentWindow.location.reload(true);
}


</script>
</head>

<body link="#ffffff" vlink="#ffffff" alink="#ffffff">
<center>

<div style=" width: 750px; padding: 1px; ">
	<a href="http://www.ecs.csus.edu/pc2/"><img style="background: white; border:0;" src="../Team/images/sac_state.png" href="http://www.ecs.csus.edu/pc2/"></a>
	&nbsp;	
	&nbsp;	
	<a href="http://www.ewu.edu/"><img style="background: white; border:0;" src="../Team/images/EWU.png" ></a>
	&nbsp;	
	&nbsp;	
	<a href="http://icpc.baylor.edu/"><img style="background: white; border:0;" src="../Team/images/acm-icpc-logo.png" ></a>
</div>


<div id="tabs" class="widget" >

  <ul class="tabnav">
    <li><a href="#tabs-1" draggable="true">Submit Run</a></li>
    <li><a href="#tabs-2">View Runs</a></li>
    <li><a href="#tabs-3">Request Clarification</a></li>
    <li><a href="#tabs-4">View Clarifications</a></li>
    <li><a href="#tabs-5">Scoreboard</a></li>
    <li><a href="#tabs-6" style="float:right" onclick="exit()">Logout</a></li>
  </ul>


<div id="tabs-1" class="tabdiv">
	<p style="float: left;font-style:italic;">Select your Problem, Language and File to submit to the judges for review:</p>
	<iframe seamless frameborder="0" name="runSubmitFrame" id="runSubmitFrame" width="100%" height="175" src="iSubmit.php"></iframe>
</div>

<div id="tabs-2" class="tabdiv">	
	<p style="float: left;font-style:italic;">All submitted runs by your team:</p>
	<div style="float:right;"><input type="button" onclick="refreshRunView()" value="Refresh" style="float: right; border: 0px; background-color:transparent;font-weight:bold;"></div>
	<iframe seamless frameborder="0" name="runViewFrame" id="runViewFrame" width="100%" height="400" src="iViewRuns.php"></iframe>
</div>

<div id="tabs-3" class="tabdiv">
	<p style="float: left;font-style:italic;">Send a clarification request to the judges:</p>
	<iframe seamless frameborder="0" name="clarSubmitFrame" id="clarSubmitFrame" width="100%" height="165" src="iSendClarification.php"></iframe>
</div>

<div id="tabs-4" class="tabdiv">
	<p style="float: left;font-style:italic;">All clarification responses and contest-wide broadcasts:</p>
	<div style="float:right;"><input type="button" onclick="refreshClarView()" value="Refresh" style="float: right; border: 0px; background-color:transparent;font-weight:bold;"></div>
	<iframe seamless frameborder="0" name="clarViewFrame" id="clarViewFrame" width="100%" height="400" src="iViewClarifications.php"></iframe>
</div>

<div id="tabs-5" class="tabdiv">
	
	<div style="float:right;"><input type="button" onclick="refreshScoreBoard()" value="Refresh" style="float: right; border: 0px; background-color:transparent;font-weight:bold;"></div>
	<iframe seamless frameborder="0" name="scoreFrame" id="scoreFrame" width="100%" height="400" src="iScoreBoard.php"></iframe>
</div>

</div></center>
</body>
</html>
