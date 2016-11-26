import java.io.*;

//
// File:    SumitRTE.java
// Purpose: to prodce a RTE (may not be possible in Java)
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
// 
// Thu Nov 03 13:57:01 2016
// 
// $Id: isumit.java 1962 2009-11-25 03:42:12Z boudreat $
//

public class SumitRTE {

    public static void main(String[] args) 
    {
	try
	{	
		BufferedReader br = new BufferedReader ( new InputStreamReader (System.in), 1);
		
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

		System.out.print("The sum of the integers is ");
		System.out.println(sum);
	}
	catch(Exception e)
	{
		System.out.println("Possible trouble reading stdin");
		System.out.println("Message: "+ e.getMessage());
	}			

		System.exit(66);  // non zero exit code

    }
}

// eof isumit.java $Id: isumit.java 1962 2009-11-25 03:42:12Z boudreat $
