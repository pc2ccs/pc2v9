<?php

	if(version_compare(phpversion(),'4.3.0')>=0) { 
	    if(!ereg('^SESS[0-9a-zA-Z]+$',$_REQUEST['SESSION_NAME'])) { 
	        header("Location: ../index.html");
	    } 
	    output_add_rewrite_var('SESSION_NAME',$_REQUEST['SESSION_NAME']); 
	    session_name($_REQUEST['SESSION_NAME']); 
	}

	session_start();

	if(is_resource(@fsockopen('localhost', 3306))) 
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
function send()
{
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
			//alert("sent");
			$("#sent-box").fadeIn(1000);
			setTimeout('$("#sent-box").fadeOut();',3000);
			//return to default values
			ta.value ="";
			$("#clarProbs").val($("#clarProbs option:first").val());
			setTimeout('parent.window.frames[\'clarViewFrame\'].location.reload();',1000);

    },
     error: function(msg){
		 //does not return values to give a chance to copy message before resubmition
			alert("Failed to send clarification.");
			parent.window.location="../index.html";
    }

	});
	
	
	return false;
}

</script>
<body style="background:white;">
<form onsubmit="return send()" target="_top" >
<table><tr>
    	<td style="text-align:left;"><b>Problem:<b></td></tr><tr><td>
	<select required id="clarProbs" name="clarProbs">
       		<option value="">Choose a problem</option>
		<?php foreach ($JavaProblems as $value) { echo "<option>".$value->getName()."</option>"; } ?>
        </select></td></tr>
    
	<tr><td><TEXTAREA required maxlength="300" style="resize:none;" id="clarificationTextArea" ROWS="9" COLS="30" onBlur="blurHandlerRouting" name="clarificationTextArea"></TEXTAREA><br/>
       </td></tr><tr><td>
		   <input type="submit" value="Submit Clarification" class="sendButton" >
		  <div id="sent-box">
				<p id="sent-message">Sent</p>
			</div>
			</td>
		  </tr>
	
</table>
</form>

</body>
</html>
