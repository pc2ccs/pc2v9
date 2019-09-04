// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
import java.io.*;

//
// File:    SumitFailTC4.java
// Purpose: Fail test 4 for valtest contest sumit problem
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
// 
// Sun Sep 23 13:50:20 2018
// 

/**
 * Produce a WA for sumit4.in file.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ISumitFailTC4
{
    public static void main(String[] args)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);

            String line;
            int sum = 0;
            int rv = 0;
            
            while ((line = br.readLine()) != null)
            {
                rv = new Integer(line.trim()).intValue();
                if (rv > 0) {
                    if (rv != 2715) {
                        /**
                         * Special case only add to sum if input int is NOT 2715, a value found in sumit4.in
                         */
                        sum = sum + rv;

                    }
                }
            }

            System.out.print("The sum of the integers is ");
            System.out.println(sum);
        } catch (Exception e)
        {
            System.out.println("Possible trouble reading stdin");
            System.out.println("Message: " + e.getMessage());
        }
    }
}
