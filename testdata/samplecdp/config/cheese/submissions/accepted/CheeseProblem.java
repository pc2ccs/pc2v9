/* Solution in Java
   for problem Cheese
   for ICPC2015
   author: Peter Kluit
   date  : September 2014
   adapted to Per's revision - March 2015
 */

import java.io.*;
import java.util.*;
public class CheeseProblem{

   public static void main (String []args){
      try{   
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
         Cheese cheese = new Cheese();
         String line = input.readLine();
         Scanner st = new Scanner(line);
         int holeCount = st.nextInt();
         int sliceCount = st.nextInt();
         for (int k = 0; k < holeCount; k++){
             line = input.readLine();
             st = new Scanner(line);
             int r = st.nextInt();
             int x = st.nextInt();
             int y = st.nextInt();
             int z = st.nextInt();
             Hole h = new Hole(r, z);
             cheese.add(h);
         }
         input.close();
         cheese.solve(sliceCount);
      }
      catch (IOException iox){}
   }

}

class Cheese{
    static double EPS = 1.0e-8;
    double volumeOfHoles;
    ArrayList<Hole> holes = new ArrayList<Hole>();

    public Cheese(){}

    public void add(Hole h){
        holes.add(h);
        volumeOfHoles += h.volume;
    }
    
    double last;

    public void solve(int sliceCount){
       last = 0;
       double volume = 100000.0 * 100000.0 * 100000.0 - volumeOfHoles;
       double volumeOfSlice = volume / sliceCount;
       for (int k = 1; k <= sliceCount;  k++){
          double next = solveFor(k * volumeOfSlice);
          System.out.printf("%8.9f\n", (next - last)/1000);
          last = next;
       }
    }

    private double volTo(double z){
       double vol = 100000.0 * 100000.0 * z; //micrometer^3
       for (Hole hole : holes)
          vol -= hole.volumeTo(z);
       return vol;
    }

    private double solveFor(double y){
      // solve: volTo(x) = y, Regula Falsi
      double x0 = last;
      double x1 = 100000;
      double fx0 = volTo(x0) - y;
      double fx1 = volTo(x1) - y;
      while (true){
          double x2 = (x0 * fx1 - x1 * fx0)/(fx1 - fx0);
          double fx2 = volTo(x2) - y;
          if (Math.abs(x2 - x0) < EPS || Math.abs(x2 - x1) < EPS)
             return x2;
          if (fx0 * fx2 < 0){
             x1  = x2;
             fx1 = fx2;
          }
          else{
             x0  = x2;
             fx0 = fx2;
          }
      }
    }

}

class Hole{
   double radius;   //all in micrometer
   double base;  // where the hole starts
   double volume;

   Hole (int r, int z){
       radius = r;
       base = z - radius;
       volume = Math.PI * 4 / 3 * (double) r * r * r;
   }

   double volumeTo (double t){
      t -= base; // adjust to base
      if (t <= 0)
         return 0;
      if (t >= 2 * radius)
         return volume;
      t -= radius; // adjust to center
      return Math.PI * t * (radius * radius  - t * t / 3)  + volume / 2;
   }

}

