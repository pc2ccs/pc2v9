import java.util.Date;

/**
 * Sumit that will cause a TLE.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SumitTLE {
    public static void main(String[] args)
    {
        try
        {
            //          BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);
            //
            //          String line;
            //          int sum = 0;
            //          int rv = 0;
            //          while ((line = br.readLine()) != null)
            //          {
            //              rv = new Integer(line.trim()).intValue();
            //              if (rv > 0)
            //              {
            //                  sum = sum + rv;
            //              }
            //          }
            //
            //
            //          System.out.print("The sum of the integers is ");
            //          System.out.println(sum);

            // Then do a nice loop and print date every 30 seconds

            long pauseMS = 30 * 1000; // 30 seconds

            System.out.println("Started at " + new Date());

            while (true)
            {
                Thread.sleep(pauseMS);
                System.out.println(new Date());
            }

        } catch (Exception e)
        {
            System.out.println("Possible trouble reading stdin");
            System.out.println("Message: " + e.getMessage());
        }
    }
}

// eof SumitTLE.java
