<?php
	session_start();

	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("http://localhost:3306/JavaBridge/java/Java.inc");
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
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.1/jquery-ui.js"></script>

<link rel="stylesheet" href="/resources/demos/style.css" />

<form method="post" action="submitClarification.php">
<table><tr>
    	<td><b>Problem:<b>
	<select required id="clarProbs" name="clarProbs">
       	<option value="">Choose a problem</option>
		<option>General</option>
		<?php foreach ($JavaProblems as $value) { echo "<option>".$value->getName()."</option>"; } ?>
        </select></td></tr>
    
	<tr><td><TEXTAREA required maxlength="300" style="resize:none;" id="clarificationTextArea" ROWS="5" COLS="50" onBlur="blurHandlerRouting" onclick="clearTextArea()" name="clarificationTextArea"></TEXTAREA><br/>
       </td></tr><tr><td>
	<input type="submit" value="Submit Clarification"/></td></tr>
</table>
</form>

</html>