package edu.csus.ecs.pc2.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public class UtilitiesTest extends TestCase {

    public void testOne() {
        char[] array1 = null;
        char[] array2 = null;
        assertTrue("arrays are null", Utilities.isEquals(array1, array2));
        array2 = new char[1];
        array2[0] = 'C';
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
        array1 = new char[1];
        array1[0] = 'C';
        assertTrue("arrays are equal", Utilities.isEquals(array1, array2));
        array1[0] = 'D';
        assertFalse("arrays are not equal", Utilities.isEquals(array1, array2));
        array2 = null;
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
    }
    
    public void testBasename(){
//        public static void main(String[] args) {
        
        String [][] entries = {
                { "foo.c", "foo.c"},
                { ";usr;bin;basename", "basename" },
                { ";usr;basename", "basename" },
                { ";bin;ls", "ls" },
        };
        
            for (String [] row: entries){

                String string1 = replaceChar (row[0], ';', File.separatorChar);
                String string2 = replaceChar (row[1], ';', File.separatorChar);
                
//                System.out.println(string1);
//                System.out.println(string2);
                
                string1 = Utilities.basename(string1);
//                System.out.println(string1);
//                System.out.println();
                
                assertEquals (string1, string2);
        }
        
    }
    
    public void testDirname(){
//      public static void main(String[] args) {
      
      String [][] entries = {
              { "foo.c", "foo.c"},
              { ";usr;bin;basename", ";usr;bin" },
              { ";usr;basename", ";usr" },
              { ";bin;ls", ";bin" },
              { ";", ";" },
      };
      
          for (String [] row: entries){

              String string1 = replaceChar (row[0], ';', File.separatorChar);
              String string2 = replaceChar (row[1], ';', File.separatorChar);
              
//              System.out.println(string1);
//              System.out.println(string2);
              
              string1 = Utilities.dirname(string1);
//              System.out.println(string1);
//              System.out.println();
              
              assertEquals (string1, string2);
      }
      
  }

        private static String replaceChar(String string, char c, char separatorChar) {
            
            // Maybe use string buffer sometime
            
            int index = string.indexOf(c);
            while (index > -1){
                string = string.replace(c, separatorChar);
                index = string.indexOf(c);
            }
            return string;
        }
    
        public void testConvertLetter() {
            int[] testCases = {1, 2, 5, 26, 27, 28, 30, 51, 52, 53, 702};
            String[] expectedAnswers = {"A", "B", "E", "Z", "AA", "AB", "AD", "AY", "AZ", "BA", "ZZ"};
            for (int i = 0; i < testCases.length; i++) {
                String result=Utilities.convertNumber(testCases[i]);
                assertEquals("testCase"+i+"("+testCases[i]+")", expectedAnswers[i],result);
            }

        }
        
        public void testgetDateTime() throws Exception {
            
            String actual = Utilities.getDateTime();
            
            // may not match actual milliseconds.
            String nearlyExpected = new SimpleDateFormat(Utilities.DATE_TIME_FORMAT_STRING).format(new Date());
            
            assertEquals("Should be same length ", nearlyExpected.length(), actual.length());
            
            // match pattern up to . where pattern is yyyyddMMhhmmss.SSS
            assertTrue ("First part of string should match ",actual.substring(0,15).equals(nearlyExpected.substring(0,15)));
        }
}
