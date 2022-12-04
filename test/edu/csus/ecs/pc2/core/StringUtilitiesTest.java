// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 */
public class StringUtilitiesTest extends TestCase {

    public void testAppendString() throws Exception {

        String[] data = {
                //
                "a,b", //
                "a,b,c,d,e,f", //
        };

        for (String line : data) {

//            System.out.println("line " + line);
            String[] f = line.split(",");

            String[] results = new String[0];

            for (int i = 0; i < f.length; i++) {
                results = StringUtilities.appendString(results, f[i]);
                compareArrayParts(f, i + 1, results);
            }
        }
    }

    private void compareArrayParts(String[] source, int count, String[] actual) {
        for (int i = 0; i < actual.length; i++) {
            assertEquals(source[i], actual[i]);
        }
    }
    
    public void testTrunc() throws Exception {
        String[] data = { //
                "a;5;a", //
                "abc;5;abc", //
                "abcdefg;5;ab...", //
                "abcdefghijklmopqrstuv;5;ab...", //
                "abcdefghijklm;13;abcdefghijklm", //
        };

        for (String line : data) {
            
            String[] f = line.split(";");

            String source = f[0];
            int maxlen = Integer.parseInt(f[1]);
            String expected = f[2];

            String actual = StringUtilities.trunc(source, maxlen);
            assertEquals("trunc method ", expected, actual);
        }
        
        
    }
    
    /**
     * Test getNumberList
     * @throws Exception
     */
    public void testgetNumberList() throws Exception {

        String [] data = { //
                "1;[1]", // 
                "1,2,3,6-12;[1, 2, 3, 6, 7, 8, 9, 10, 11, 12]", //
                "5-5;[5]", //
                "  5  -  5   ;[5]", //
                "1,3,20-26;[1, 3, 20, 21, 22, 23, 24, 25, 26]", //
                "1, 2 ,3;[1, 2, 3]", //
                "4-18;[4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18]", // 
                "4-12,1,5,6;[4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 5, 6]", //
                "100-102,123, 321 ;[100, 101, 102, 123, 321]", //
                "1,4,12-15,22;[1, 4, 12, 13, 14, 15, 22]", //
                
        };
        
        for (String string : data) {
            String [] values =string.split(";");
            String numberString =values[0];
            String expected =values[1].trim();

            int[] out = StringUtilities.getNumberList(numberString);
//            System.out.println("new data \""+numberString+";"+Arrays.toString(out)+"\", // ");
            assertEquals("Expected range for "+numberString, expected, Arrays.toString(out));
        }
    }
    
    /**
     * Test getNumberList for invalid input number strings.
     * 
     * @throws Exception
     */
    public void testgetNumberListNegative() throws Exception {

        String[] data = { //
                "-", //
                "2,-4", // missing start number
                "1,2,3,55-,6", // missing end number
                "20-12", // start number greater than end range number
                "1,2,4-5-6,20", // too many dashes
                "", // no list at all
                "            ", // lots of space
                "1,2,,4,5", // missing number
                "1,2,3,     ,7,8", // missing number
                "0-5;[5]", //
        };

        for (String inString : data) {
            try {
                String numberString = inString;
                StringUtilities.getNumberList(numberString);

                fail("Expecting failure for string: " + inString);
            } catch (Exception e) {
                ; // Passes unit test
            }
        }

    }
    
}
