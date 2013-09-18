<!DOCTYPE html>
<?php
	session_start();

	if(is_resource(@fsockopen('localhost', 3306))) 
	{
		include("http://localhost:3306/JavaBridge/java/Java.inc");
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
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.1/jquery-ui.js"></script>
<link rel="stylesheet" href="/resources/demos/style.css" /> 


<?php 
	if(isset($_SESSION['error'])) {
		echo '<div style="color: red;font-style:italic;">Error: File size exceeds size limit of 512K.</div>'; 
		unset($_SESSION['error']);
	}
?>

<form action="submitProblem.php" method="post" enctype="multipart/form-data">
	<table>
		<tr><td style="text-align:left"><b>Problem:</b></td><td>  
        		<select required id="probs" name="probs">
            			<option value="">Choose a problem</option>
				<?php foreach ($JavaProblems as $value) { echo "<option>".$value->getName()."</option>"; } ?>
			</select>
		</td></tr>

		<tr><td style="text-align:left"><b>Language:</b></td><td>
       		<select required id="lang" name="lang">
            			<option value="">Choose a language</option>
				<?php foreach ($JavaLanguages as $value) { echo "<option>".$value->getName()."</option>"; } ?>
       		</select>
		</td></tr>

		<tr><td style="text-align:left"><b>Main File:</b></td><td>
          		<input type="file" name="file" id="file"  required/>
          	</td></tr>
	<tr><td>
	<input type="submit" value="Submit"/>
	</td></tr>
	</table>
</form>

</html>
