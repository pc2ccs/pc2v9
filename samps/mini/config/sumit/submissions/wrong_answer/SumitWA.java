import java.io.*;

//
// File:    ISumit.java
// Purpose: to sum the integers from stdin
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
// 
// Thu Nov  3 16:44:40 PDT 2016
// 
// $Id: isumit.java 1962 2016-11-03 03:42:12Z boudreat $
//

/**
 * Produce Wrong Answer for sumit.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SumitWA {
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

		sum += 1000;

		System.out.print("The sum of the integers is ");
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

