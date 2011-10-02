<?
//
// File:    sumit.php
// Purpose: to sum the integers in the file sumit.dat
// Author:  pc2@ecs.csus.edu at http://www.ecs.csus.edu/pc2
//
// $Id$
//
  // Turn off any extraneous error messages
  error_reporting(0); 

  $filename = "sumit.dat";
  
  $fh = fopen ($filename, "r");

  if ( ! isset($fh) )
  {
    die ("Cannot open/find file $filename\n");
  }
  else
  {
    $sum = 0;
    
    while ( $line = fgets($fh, 128) )
    {
      $num = intval($line);
      if ( $num > 0 )
      {
	$sum += $num;
      }
    }
    
    print "Sum of positive integers is $sum\n";

  }
?>
