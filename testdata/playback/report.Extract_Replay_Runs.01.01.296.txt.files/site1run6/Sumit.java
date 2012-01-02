import java.io.*;

//
// File:    Sumit.java
// Purpose: to sum the integers in the file sumit.dat
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
// 
// $Id$
//

public class Sumit {

    public static void main(String[] args) 
    {
	try
	{	
		RandomAccessFile file = new RandomAccessFile("sumit.dat", "r");
		String line;
		int sum = 0;
		int rv = 0;
		while((line = file.readLine()) != null) 
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
		System.out.println("Possible trouble reading sumit.dat");
	}			
    }
}

// eof Sumit.java $Id$
