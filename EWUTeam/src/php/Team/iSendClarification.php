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
		

		try {

			$probarray = $server->getProblems($_SESSION['cid']);
			$JavaProblems  = java_cast($probarray , "array");

		} catch(JavaException $exception) {
			$error = "Could not get problems!";
			//echo "error";
		}//end catch
	}//end if(is_resource(. . .)) {}
?>

<html>
<link href="tab_style/sentStyle.css" rel="stylesheet" type="text/css" />
<script src="JQuery/jquery-1.9.1.js"></script>
<script src="JQuery/jquery-ui.js"></script>

<script>
setInterval('clarSubmissionVerification()',5000);

function clarSubmissionVerification() {
         $.ajax({
                 type: 'POST',
                 url: 'VerifyClarificationSubmission.php',
                 data: 'SESSION_NAME=<?php echo session_name(); ?>',
                 dataType: 'json',
                 cache: false,
                 success: function(result) {
                         if(result == 'SubmissionOccurred')
                         {
                                 var submitButton = document.getElementById('clarsubbutton');
                                 submitButton.disabled=false;
                                 submitButton.value="Submit Clarification";
                                 $("#sent-box").fadeIn(1000);
                                 setTimeout('$("#sent-box").fadeOut();',3000);
                                 setTimeout('parent.window.frames[\'clarViewFrame\'].location.reload();',1000);
                         }
                 }
       });
}

function send()
{
    var submitButton = document.getElementById('clarsubbutton');	
    submitButton.value="Submitting...";

	var cp = document.getElementById("clarProbs").value;
	var ta = document.getElementById("clarificationTextArea");
	var text = ta.value;
	
	$.ajax({    //create an ajax request to load_page.php
    type: "POST",
    url: "submitClarification.php",
    data: {SESSION_NAME: '<?php echo session_name(); ?>',  clarProbs: cp, clarificationTextArea: text},
   dataType: 'json',
  cache: false,
   success: function(msg){
			if(msg.indexOf("ContestStopped") > -1) {
				parent.alert("The contest is stopped. Unable to submit your clarification.");
			}
			else {
				$("#sent-box").fadeIn(1000);
				setTimeout('$("#sent-box").fadeOut();',3000);
			}
			//return to default values
			ta.value ="";
			$("#clarProbs").val($("#clarProbs option:first").val());
// 			setTimeout('parent.window.frames[\'clarViewFrame\'].location.reload();',1000);
// 			submitButton.disabled=false;
// 			submitButton.value="Submit Clarification";

    },
     error: function(msg){
		 //does not return values to give a chance to copy message before resubmition
			alert("Your session has expired. Please log back in and resubmit your clarification.");
			submitButton.disabled=false;
			submitButton.value="Submit Clarification";
			parent.window.location="../index.html";
    }

	});
	
	
	return false;
}

</script>
<body style="background:white;">
<form onsubmit="document.getElementById('clarsubbutton').disabled=1;return send()" target="_top" >
<table><tr>
    	<td style="text-align:left;"><b>Problem:<b></td></tr><tr><td>
	<select required id="clarProbs" name="clarProbs">
       		<option value="">Choose a problem</option>
		<?php foreach ($JavaProblems as $value) { echo "<option value='".urlencode($value->getName())."'>".$value->getName()."</option>"; } ?>
        </select></td></tr>
    
	<tr><td><TEXTAREA required maxlength="300" style="resize:none;" id="clarificationTextArea" ROWS="9" COLS="30" onBlur="blurHandlerRouting" name="clarificationTextArea"></TEXTAREA><br/>
       </td></tr><tr><td>
		   <input type="submit" id=clarsubbutton value="Submit Clarification" class="sendButton" >
		  <div id="sent-box">
				<p id="sent-message">Sent</p>
			</div>
			</td>
		  </tr>
	
</table>
</form>

</body>
</html>
