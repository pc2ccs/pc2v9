<?php

	// $Id$
	
	if(version_compare(phpversion(),'4.3.0')>=0) { 
    		if(!preg_match ( '/^SESS[0-9a-zA-Z]+$/',$_REQUEST['SESSION_NAME'])) { 
        		//$_REQUEST['SESSION_NAME']='SESS'.uniqid(''); 
			header("Location: ../index.html");
    		} 
    		output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
    		session_name($_REQUEST['SESSION_NAME']);
		//session_id($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	$currSID = session_name();
	//echo '<span id="SessionNAME">SessionNAME:' . $currSID . '</span>';
	
	//echo '<div class="login">CID: <span class="team">' . $_SESSION['cid'] . '</span></div></br>';
	

	if(!isset($_SESSION['cid']))
		header("Location: ../index.html");

	if(is_resource(@fsockopen('localhost', 50005))) 
	{
		include("../lib/Java.inc");
		$server = java("ServerInterface")->getInstance();

		if(java_is_false($server->isLoggedIn($_SESSION['cid'])))
		{
			header("Location: ../index.html");
		}

	}//end if(is_resource(. . .)) {}
?>
<head>
<title>PC^2 Team Client</title>
<meta charset="utf-8" />

<link href="tab_style/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="tab_style/jquery-1.9.1.js"></script>
<script type="text/javascript" src="tab_style/jquery-ui-1.10.2.js"></script>
<script type="text/javascript">

setInterval('timer()',1000);

setInterval('clar()',5000);

setInterval('runs()',5000);


function clar() { 
  $.ajax({
  type: 'POST',
  url: 'getClars.php',
  data: 'SESSION_NAME=<?php echo session_name(); ?>',
  dataType: 'json',
  cache: false,
  success: function(result) {
	$('#dialog-header').html("New Clarification");
	//Escape
	Answer = result[6].replace(/</g,"&lt");
	Answer = Answer.replace(/>/g,"&gt");

	content = result[5].replace(/</g,"&lt");
	content = content.replace(/>/g,"&gt");
	
    	openDialog("Question: ", result[4], content, Answer);
    	var iframe = document.getElementById('clarViewFrame');
    	iframe.contentWindow.location.reload(true);
    	
	//var innerDoc = iframe.contentDocument || iframe.contentWindow.document;
   	//var row = innerDoc.getElementById('veiwClarificationTable').insertRow(1);
    	//for (var i=0;i<result.length;i++) {
	//	var cell = row.insertCell(i);
       // 	cell.innerHTML = result[i];
    	//}
   //refreshClarView();
  },
  });
	
}




function runs()
{
	
  $.ajax({
  type: 'POST',
  url: 'getRuns.php',
  data: 'SESSION_NAME=<?php echo session_name(); ?>',
  dataType: 'json',

  success: function(result) {
	

	$('#dialog-header').html("Run Judged");
	if(result[6] == true) {
		openDialog("Time: ", result[2], result[4], "Preliminary " + result[5]);
	} else {
		openDialog("Time: ", result[2], result[4], result[5]); }
		
    var iframe = document.getElementById('runViewFrame');
    iframe.contentWindow.location.reload(true);
    var innerDoc = iframe.contentDocument || iframe.contentWindow.document;
    //var row = innerDoc.getElementById('veiwRunTable').insertRow(1);
    //for (var i=0;i<result.length;i++) {
//	var cell = row.insertCell(i);
//        cell.innerHTML = result[i];
    //}
   //refreshRunView();
  },

  });
}




function timer()
{

	$.ajax({
  type: 'POST',
  url: 'getClock.php',
  data: 'SESSION_NAME=<?php echo session_name(); ?>',
  dataType: 'json',
  cache: false,
  success: function(result) {

	if(result.hour == 0 && result.min < 10)
		document.getElementById("clock").style.color = "red";

	document.getElementById("clock").innerHTML = "Time remaining: ".bold()  + result.hour + "h "  + result.min + "m";
	
	},
  });
}


function exit() {
	$.post('logout.php?SESSION_NAME=<?php echo session_name(); ?>', {username: "<?php echo $_SESSION['cid'] ?>"}, 
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

function openDialog(type, Problem, content, Answer)
{
	
    // get the screen height and width  
    var maskHeight = $(document).height();  
    var maskWidth = $(window).width();
    
    // calculate the values for center alignment
    var dialogTop =  (maskHeight/2) - ($('#dialog-box').height()/2);  
    var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2); 
    // assign values to the overlay and dialog box
    $('#dialog-overlay').css({height:maskHeight, width:maskWidth}).fadeIn();
    $('#dialog-box').css({top:dialogTop, left:dialogLeft}).fadeIn();
	$('#dialog-message').html("Problem: ".bold() + Problem + "<br><br>"+ type.bold() + content + "<br><br>Answer: ".bold() + Answer);
	
}
function closeDialog()
{
		$('#dialog-overlay, #dialog-box').fadeOut();//.hide({effect: "blind",duration: 1000});        
}
$(function() {
    $( "#tabs" ).tabs({
		hide: 'slideUp',
         show: 'slideDown' 
		});	
});

</script>
</head>

<body link="#ffffff" vlink="#ffffff" alink="#ffffff">


<!-- <img class="title" src="./images/title.png"> -->
<div class="login">
	<p style="font-weight: bold;"> Welcome to PC^2!</p>  
	<p id ="clock"/>
	<p><span style="float:right;"><b>Logged in as:</b> <?php echo $_SESSION['username']; ?></span></p> 
</div>

<hr/>


<div style="position:relative; ">

<div class="widget2">
	<div class="submit">
		<div class="toolbarBanner"><div class="toolborder">SUBMIT RUN</div></div>
		<iframe seamless scrolling="no"frameborder="0" name="runSubmitFrame" id="runSubmitFrame" width="100%" height="250" src="iSubmit.php?SESSION_NAME=<?php echo session_name(); ?>"></iframe>
	</div>
	<div class="submit" style="margin-top:20px;">
		<div class="toolbarBanner"><div class="toolborder">SUBMIT CLARIFICATION</div></div>
		<iframe seamless scrolling="no" frameborder="0" name="clarSubmitFrame" id="clarSubmitFrame" width="100%" height="265" src="iSendClarification.php?SESSION_NAME=<?php echo session_name(); ?>" ></iframe>
	</div>
</div>

<div id="tabs" class="widget">

  <ul class="tabnav">
    <li><a href="#ViewRuns">View Runs</a></li>
    <li><a href="#ViewClar">View Clarifications</a></li>
    <li><a href="#Scoreboard">Scoreboard</a></li>
    <li><span style="float:right;"><a href="#Logout" onclick="exit()">Logout</a></span></li>
  </ul>



<div id="ViewRuns" class="tabdiv">
	<p style="float: left;font-weight:bold;font-size:125%;">All submitted runs by your team:</p>
	<div style="float:right;"><input type="button" onclick="refreshRunView()" value="Refresh" style="float: right;"></div>
	<iframe seamless frameborder="0" name="runViewFrame" id="runViewFrame" width="100%" height="535" src="iViewRuns.php?SESSION_NAME=<?php echo session_name(); ?>"></iframe>
</div>



<div id="ViewClar" class="tabdiv">
	<p style="float: left;font-weight:bold;font-size:125%;">All clarification responses and contest-wide broadcasts:</p>
	<div style="float:right;"><input type="button" onclick="refreshClarView()" value="Refresh" style="float: right;"></div>
	<iframe seamless frameborder="0" name="clarViewFrame" id="clarViewFrame" width="100%" height="535" src="iViewClarifications.php?SESSION_NAME=<?php echo session_name(); ?>"></iframe>
</div>

<div id="Scoreboard" class="tabdiv">
	<p style="float: left;font-weight:bold;font-size:125%;">Ranked Scoreboard for the contest (# of attempts / penalty time):</p>
	<div style="float:right;"><input type="button" onclick="refreshScoreBoard()" value="Refresh" style="float: right;"></div>
	<iframe seamless frameborder="0" name="scoreFrame" id="scoreFrame" width="100%" height="535" src="iScoreBoard.php?SESSION_NAME=<?php echo session_name(); ?>"></iframe>
</div>

</div>

</div>




<div id="dialog-overlay"></div>
	<div id="dialog-box">
		<div class="dialog-content">
			<div id="dialog-header"></div>
				<p id="dialog-message">
				</p>
			<button id="close" onclick="closeDialog()">Close</button>
    </div>
</div>


</body>
</html>

