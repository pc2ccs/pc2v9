/*
 * File:    isumit.cs
 * Purpose: to sum the positive from stdin
 * pc2@ecs.csus.edu at http://www.ecs.csus.edu/pc2
 *
 * $Id: hello.cs 3159 2015-09-22 20:39:11Z boudreat $
 * TODO cause tle by sleeping/running for a long time
*/
using System;
 
public class iSumit
{
    static public void Main ()
    {
        int sum = 0;
        string str;
        while((str = Console.ReadLine()) != null) 
        {
            int num = int.Parse(str);
            if (num > 0)
            {
                sum = sum + num;
            }
        }

		sum = sum + 1000;
 
        Console.WriteLine ("The sum of the integers is {0}",sum);
    }
}
