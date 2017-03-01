import java.io.*;

//
// File:    ISumit.java
// Purpose: to sum the integers from stdin, but produce output which differs from 
//    the traditional Sumit output in that it uses incorrect CASE (to test case-sensitivity in Validators).

// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
// 
// Tue Feb 21 16:56:00 PDT 2017
// 
// $Id: isumit.java 1962 2009-11-25 03:42:12Z boudreat $
//

public class ISumitWrongOutputCase {
    public static void main(String[] args) 
    {
	try
	{	
		BufferedReader br = new BufferedReader (
			new InputStreamReader (System.in), 1);
		
		String line;
		int sum = 0;
		int rv = 0;
		while((line = br.readLine()) != null) 
		{			
			rv = new Integer(line.trim()).intValue();
			if (rv > 0)
				sum = sum + rv;
			// System.out.println(line);
		}
		System.out.print("THE SUM OF THE INTEGERS IS ");
		System.out.println(sum);
	}
	catch(Exception e)
	{
		System.out.println("Possible trouble reading stdin");
		System.out.println("Message: "+ e.getMessage());
	}			
    }
}

// eof isumit.java $Id: isumit.java 1962 2009-11-25 03:42:12Z boudreat $

