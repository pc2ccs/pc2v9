package edu.csus.ecs.pc2.core.util;

import junit.framework.TestCase;

/**
 * Unit tests for TabSeparatedValueParser.
 * 
 * Testing data has ; which are replaced by TAB characters before parse test is done.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: TabSeparatedValueParserTest.java 244 2011-10-02 16:40:28Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/core/util/TabSeparatedValueParserTest.java $
public class TabSeparatedValueParserTest extends TestCase {

    private boolean debugMode = false;

    public static final char TAB_CHAR = 9;

    public static final String TAB_STRING = String.valueOf(TAB_CHAR);

    /**
     * Takes input fields, creates TSV string, then test back to fields.
     * 
     * @throws Exception
     */
    public void testEncodeThenDecode() throws Exception {

        String[] strings = { "one", "two", "three" };

        String sample = TabSeparatedValueParser.toString(strings);

        String[] afterStrings = TabSeparatedValueParser.parseLine(sample);

        assertEquals("Expecting same number of fields", strings.length, afterStrings.length);

        testStringArrays("TSV fields", strings, afterStrings);
    }

    public void testNULL() throws Exception {

        try {
            TabSeparatedValueParser.parseLine(null);
            assertTrue("Expected IllegalArgumentException for null input", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Expected IllegalArgumentException", true);
        }
    }

    public void testMoreStrings() throws Exception {
        
        String methodName = "testMoreStrings";

        // Testing data has ; which are replaced by TAB characters before parse test is done.
        
        String[] exampleStrings = { //
        "foo", // 1
                "foo;bar", // 2
                "foo;bar;", // 3
                ";foo;bar;", // 4
                ";;;", // 4
                ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;last", // 46
                ";\"foo;\";", // 3
                ";", // 2
                "\"first\";\"second\"", // 2
                "\"first\";\"The \"\"second\"\"\"", // 2
        };

        int[] expectedFieldCounts = { //
        1, //
                2, //
                3, //
                4, //
                4, //
                46, //
                3, //
                2, //
                2, //
                2, //
        };

        for (int i = 0; i < exampleStrings.length; i++) {

            String actualString = exampleStrings[i].replaceAll(";", TAB_STRING);

            String[] parsed = TabSeparatedValueParser.parseLine(actualString);

//            System.out.println("In: '" + actualString + "' " + parsed.length + " fields: " + Arrays.toString(parsed));
            
            if (debugMode) {
                System.err.flush();
                System.out.println();
                System.out.println(methodName + " Input: '" + actualString + "' expected " + expectedFieldCounts[i] + " fields.");
                if (parsed.length == expectedFieldCounts[i]) {
                    System.out.println("     PASSED Test");
                } else {
                    System.out.println("     failed Test, off by " + (expectedFieldCounts[i] - parsed.length));
                }
                int idx = 1;
                for (String field : parsed) {
                    System.out.println("   " + (idx++) + " <" + field + ">");
                }
                System.out.flush();
            } else {
                assertEquals("Parse failure, expecting same number of fields", expectedFieldCounts[i], parsed.length);
                
            }
        }
    }

    // TODO CLEANUP pass these tests
//    /**
//     * Parsing errors unit test.
//     */
//    public void testNegatives() {
//
//        String methodName = "testNegatives";
//        
//        // Testing data has ; which are replaced by TAB characters before parse test is done.
//
//        String[] badExampleStrings = { //
//        "foo\"", // no opening quote
//                "\"", // no closing quote
//                ";\";\"\"", // no closing quote second field
//                "first;\"second\"third;fourth", // no TAB delimiter between first and second
//        };
//
//        for (int i = 0; i < badExampleStrings.length; i++) {
//
//            // replace ; with Tab characters.
//            String actualString = badExampleStrings[i].replaceAll(";", TAB_STRING);
//
//            try {
//                TabSeparatedValueParser.parseLine(actualString);
//                if (debugMode) {
//                    System.err.println("In " + methodName + " Expecting parse Exception for: '" + badExampleStrings[i]+"'");
//                } else {
//                    assertTrue("Expecting Exception thrown for: " + badExampleStrings[i], false);
//                }
//            } catch (Exception e) {
//                assertTrue("Expected Exception", true);
//            }
//        }
//
//    }

    public void testEmptyInputString() throws Exception {

        String[] parsed = TabSeparatedValueParser.parseLine("");
        assertEquals("Miss match in number of fields", 1, parsed.length);

    }

    /**
     * Compare two string arrays.
     * 
     * @param message
     *            message to show if there is a failure
     * @param expected
     *            what should be in the array
     * @param actual
     *            what is in the array
     */
    private void testStringArrays(String message, String[] expected, String[] actual) {

        assertEquals(message + " Expecting same number of fields ", expected.length, actual.length);

        if (expected.length == 0) {
            return; // nothing to compare ======================================== RETURN
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message + " at index " + i, expected[i], actual[i]);
        }

    }

}
