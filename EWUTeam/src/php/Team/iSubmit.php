<!DOCTYPE html>
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

			$langarray = $server->getLanguages($_SESSION['cid']);
			$JavaLanguages  = java_cast($langarray , "array");

		} catch(JavaException $exception) {
			//$error = "Could not get problems!";
			//echo "error";
		}//end catch
	}//end if(is_resource(. . .)) {}
?>

<html>
<link href="tab_style/sentStyle.css" rel="stylesheet" type="text/css" />
<script src="JQuery/jquery-1.9.1.js"></script>
<script src="JQuery/jquery-ui.js"></script>


<script>


function send(){

    var formData = new FormData($("#data")[0]);
    var submitButton = document.getElementById('subbutton');	
    submitButton.value="Submitting...";

    $.ajax({
        url: "submitProblem.php",
        type: 'POST',
        data: formData,
        success: function (data) {
		if(data.indexOf("ContestStopped") > -1) {
			parent.alert("The contest is stopped. Unable to submit your solution.");
		}
		else {
           		$("#sent-box").fadeIn(1000);
			setTimeout('$("#sent-box").fadeOut();',3000);
		}
		//return to default values
		$("#probs").val($("#probs option:first").val());
		$("#lang").val($("#lang option:first").val());
		$("#file").val("");
		setTimeout('parent.window.frames[\'runViewFrame\'].location.reload();',1000);
		submitButton.disabled=false;
		submitButton.value="Submit Problem";
        },
 error: function(msg){
		 //does not return values to give a chance to copy message before resubmission
		alert("Your session has expired. Please log back in and resubmit your run.");
		submitButton.disabled=false;
		submitButton.value="Submit Problem";
		parent.window.location="../index.html";
    },
        cache: false,
        contentType: false,
        processData: false
    });

    return false;
}


</script> 


<body style="background:white;">

<form id="data" method="post" onsubmit="document.getElementById('subbutton').disabled=1;return send()" enctype="multipart/form-data">
	<table>
		<tr><td style="text-align:left;"><b>Problem:</b></td></tr>
		<tr><td>
        		<select required id="probs" name="probs">
            			<option style="border:none;" value="">Choose a problem</option>
				<?php foreach ($JavaProblems as $value) { echo "<option value='".urlencode($value->getName())."'>".$value->getName()."</option>"; } ?>
			</select>
		</td></tr>

		<tr><td style="text-align:left"><b>Language:</b></td></tr>
		<tr><td>
	       		<select required id="lang" name="lang">
		    			<option value="">Choose a language</option>
					<?php foreach ($JavaLanguages as $value) { echo "<option value='".urlencode($value->getName())."'>".$value->getName()."</option>"; } ?>
	       		</select>
		</td></tr>

		<tr><td style="text-align:left; width:200px;"><b>Main File:</b></td></tr>
		<tr><td>
          		<input type="file" name="file" id="file" size="9" required />
          	</td></tr>
	<tr><td>
	<input type="submit" id=subbutton value="Submit Problem" class="sendButton" >
			 <div id="sent-box">
				<p id="sent-message">Sent</p>
			</div>
	</td></tr>
	
	<tr><td>
	<?php 
		if(isset($_SESSION['error'])) {
			echo '<div style="color:red; font-style:italic; font-size:75%;">'.$_SESSION['error'].'</div>'; 
			unset($_SESSION['error']);
		}
	?>
	</td></tr>
	</table>
</form>



</body>
</html>
