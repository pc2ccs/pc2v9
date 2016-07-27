/*
 * Java solution for Keyboard
 * a problem for ICPC2015
 * author: Peter Kluit
 * date  : December 2014
 */

import java.util.*;
import java.io.*;

public class KeyboardProblem{

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
         Keyboard keyboard = new SolverDD(layout);
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

class SolverDD extends Keyboard{

   Node [][] nodes;
   boolean done = false;
   ArrayDeque<Fact> tobedone = new ArrayDeque<Fact>();
   int answer;
   String target;

   public SolverDD(char [][] lo){
      super(lo);
      makeNodes();
   }

   private void makeNodes(){
      nodes = new Node [rows][cols];
      nodes[0][0] = new Node(0,0);
      nodes[0][0].link();
   }

   public int countStrokes(String tobeTyped){
     target = tobeTyped + '*';
     done = false;
     nodes[0][0].update(0, 0);

     while (!done){
        Fact present = tobedone.remove();
        present.visit();
     }
     return answer;
   }

   class Fact{
      Node position;
      int strokes;
      int typed;

      Fact(Node pos, int s, int t){
         position = pos;
         strokes = s;
         typed = t;
         tobedone.add(this);
      }

      void visit(){
         if (target.charAt(typed) == position.character){
            position.update(strokes + 1, typed + 1);
         }
         else
            for (int k = 0; k < 4; k++){
                Node buur = position.neighbours[k];
                if (buur != null)
                   buur.update(strokes + 1, typed);
            }
      }
   }

   class Node{
      int row, col;
      char character;
      Fact fact;
      int typed = -1;
      Node [] neighbours = new Node[4];

      Node(int y, int x){
         row = y;
         col = x;
         character = layout[row][col];
      }

      void update(int strokesIn, int typedIn){
         if (typedIn <= typed)
            return;
         if (typedIn == target.length()){
            answer = strokesIn;
            done = true;
            return;
         }
         typed = typedIn;
         if (fact != null && strokesIn == fact.strokes)
            fact.typed = typedIn;
         else
            fact = new Fact(this, strokesIn, typedIn);
      }

      public String toString(){
         return "node (" +  row + ", " + col + ", "  + character + ")";
      }

      void setLink(int [] buur, int wher){
         if (buur != null){
             int r = buur[0];
	           int c = buur[1];
             if (nodes[r][c] == null){
                 nodes[r][c] = new Node(r,c);
                 nodes[r][c].link();
             }
             neighbours[wher] = nodes[r][c];
         }
      }

      void link(){
        setLink( leftNext(row,col), 0);
        setLink(rightNext(row,col), 1);
	      setLink(   upNext(row,col), 2);
	      setLink( downNext(row,col), 3);
	    }
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