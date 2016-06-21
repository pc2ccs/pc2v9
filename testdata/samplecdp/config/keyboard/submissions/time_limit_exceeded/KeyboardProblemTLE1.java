/*
 * Java solution for Keyboard
 * time limit exceeded
 * a problem for ICPC2015
 * author: Peter Kluit
 * date  : December 2014
 */

import java.util.*;
import java.io.*;

public class KeyboardProblemTLE1{

   public static void main (String [] args){
      runSingle();
   }

   private static void runSingle(){
      try{
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

         String line = input.readLine();
         Scanner sc = new Scanner(line);
         int rows = sc.nextInt();
         int cols = sc.nextInt();
         char [] [] layout = new char[rows][cols];
         for (int k = 0; k < rows; k++){
            line = input.readLine();
            for (int l = 0; l < cols; l++)
               layout[k][l] = line.charAt(l);
         }
         Keyboard keyboard = new SolverMF(layout);
         String toType = input.readLine();
         int strokes = keyboard.countStrokes(toType);
         System.out.println(strokes);
         input.close();
      }
      catch (IOException iox){System.out.println(iox);}
    }

}

abstract class Keyboard {
   final static int INF = 100000000;
   char [][] layout;
   int rows, cols;

   public Keyboard(char [][] lo){
      layout = lo;
      rows = lo.length;
      cols = lo[0].length;
   }

   abstract int countStrokes(String tobeTyped);
}

class SolverMF extends Keyboard{
   int [][][][] buurM; // memo of neighbours

   public SolverMF(char [][] lo){
      super(lo);
      makeBuurM();
   }

   private void makeBuurM(){
      buurM = new int [rows][cols][4][];
      for (int row = 0; row < rows; row++)
        for(int col = 0; col < cols; col++){
            buurM[row][col][0] =  leftNext(row, col);
            buurM[row][col][1] = rightNext(row, col);
            buurM[row][col][2] =    upNext(row, col);
            buurM[row][col][3] =  downNext(row, col);
        }
   }

   public int countStrokes(String tobeTyped){
     tobeTyped = tobeTyped + '*';
     int all = tobeTyped.length();
     int strokes = 0;
     boolean done = false;
     int [][] where = new int [rows][cols];
     for (int row = 0; row < rows; row++)
        for(int col = 0; col < cols; col++)
           where[row][col] = -1;
     where[0][0] = 0;
     while (!done){
        int [][] newWhere = new int [rows][cols];
        for (int row = 0; row < rows; row++)
           for(int col = 0; col < cols; col++)
              newWhere[row][col] = -1;
        for (int row = 0; row < rows; row++)
           for (int col = 0; col < cols; col++)
              if (where[row][col] >= 0){
                 int here = where[row][col];
                 if (tobeTyped.charAt(here) == layout[row][col])
                    if (here + 1 > newWhere[row][col]){
                       newWhere[row][col] = here + 1;
                       if (here + 1 == all)
                          done = true;
                 }
                 for (int n = 0; n < 4; n++){
                     int [] buur = buurM[row][col][n];
                     if (buur != null && where[buur[0]][buur[1]] < here && newWhere[buur[0]][buur[1]] < here)
                            newWhere[buur[0]][buur[1]] = here;

                 }
        }
        where = newWhere;
        strokes++;
     }
     return strokes;

   }

   private int [] leftNext(int r, int c){
      int k = c - 1;
      while (k >= 0 && layout[r][k] == layout[r][c])
         k--;
      if (k < 0)
        return null;
      int [] answer ={r,k};
      return answer;
   }

   private int [] rightNext(int r, int c){
      int k = c + 1;
      while (k < cols  && layout[r][k] == layout[r][c])
         k++;
      if (k == cols )
        return null;
      int[] answer ={r,k};
      return answer;
   }

   private int [] upNext(int r, int c){
      int k = r - 1;
      while (k >= 0 && layout[k][c] == layout[r][c])
         k--;
      if (k < 0)
        return null;
      int[] answer = {k, c};
      return answer;
   }

   private int [] downNext(int r, int c){
      int k = r + 1;
      while (k < rows && layout[k][c] == layout[r][c])
         k++;
      if (k == rows)
        return null;
      int [] answer = {k, c};
      return answer;
   }

}
