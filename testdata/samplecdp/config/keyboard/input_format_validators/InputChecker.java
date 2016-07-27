import java.io.*;
import java.util.*;
public class InputChecker{
     static final int KEYBOARD_BOUND = 50;
     static final int TEXT_LENGTH = 10000;
     static final char SPACE = ' ';
     static char [][] layout;
     static int rows, cols;

     public static void main (String [] args){
        check();
		System.exit(42);
     }

     private static void check(){
      try{
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
         //BufferedReader input = new BufferedReader(new FileReader(infile));
         String line = input.readLine();
         Scanner sc = new Scanner(line);
         rows = sc.nextInt();
         sure (rows <= KEYBOARD_BOUND, "rows too large: " + rows);
         cols = sc.nextInt();
         sure (cols <= KEYBOARD_BOUND, "cols too large: " + cols);
         layout = new char[rows][cols];
         for (int k = 0; k < rows; k++){
            line = input.readLine();
            sure (line.length() == cols, "Keyboard line too long /short " + line);
            for (int l = 0; l < cols; l++){
               char cc = line.charAt(l);
               sure (isKeyboardChar(cc), "char "+  cc +  " should not be in keyboard");
               layout[k][l] = cc;
            }
         }
         checkKeyboard();
         String toType = input.readLine();
         checkText (toType);

         input.close();
      }
      catch (IOException iox){
         System.out.println(iox);
      }
    }
    
    static void checkText(String text){
        sure (text.length() <=  TEXT_LENGTH, "text too long :  " + text.length());
        for (int k = 0; k < text.length(); k++)
           sure(isTextChar(text.charAt(k)), "wrong character in text at " + k + ":" + text.charAt(k) + "==");

    }
    
    static private void sure(boolean p, String comment){
       if (!p){
         System.out.println(comment);
         throw new RuntimeException();
       }
    }

    static boolean isTextChar(char cc){
        if ('A' <= cc && cc <= 'Z')
           return true;
        if ('0' <= cc && cc <= '9')
           return true;
        if (cc == '-')
           return true;
        return false;
    }
    
    static boolean isKeyboardChar(char cc){
       return isTextChar(cc) || cc == '*';
    }
    
    static void checkKeyboard(){
       String read = "";
       for (int row = 0; row < rows; row++)
          for (int col = 0; col < cols; col++){
             char cc = layout[row][col];
             if (cc != SPACE){
                sure(read.indexOf(cc) < 0, "char:  " + cc + " has more than one key in keyboard");
                read += cc;
                sweep(row, col);
             }
          }
    }
    
    private static void sweep(int row, int col){
       int cc = layout[row][col];
       layout[row][col] = SPACE;
       if (col > 0)
          if (layout[row][col - 1] == cc)
              sweep(row, col - 1);
       if (row > 0)
          if (layout[row - 1][col ] == cc)
              sweep(row - 1, col);
       if (col < cols -1)
          if (layout[row][col + 1] == cc)
              sweep(row, col + 1);
       if (row < rows -1)
          if (layout[row +1][col ] == cc)
              sweep(row + 1, col);
    }
}