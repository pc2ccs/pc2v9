/*
 *
 * File:    isumit.cs
 * Purpose: to sum the positive from stdin
 * pc2@ecs.csus.edu at http://www.ecs.csus.edu/pc2
 *
 * $Id: hello.cs 3159 2015-09-22 20:39:11Z boudreat $
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
            int intval = int.Parse(str);
            int i;

          if (inval < 1)
          {
            for (i = 1; i >= inval; i --)
                   sum += i;
          }
          else
          {
            for (i = 1; i <= inval; i ++)
                   sum += i;
          }

          Console.WriteLine ("The sum of the integers is {0}",sum);
        }
    }
}
