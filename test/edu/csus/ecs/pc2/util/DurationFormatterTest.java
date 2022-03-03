// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.util;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author John Clevenger <pc2@ecs.csus.edu>
 *
 */
public class DurationFormatterTest extends AbstractTestCase {

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationZero() throws Exception {

        long testVal = 0;
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:00:00.000";
        
        assertEquals("Formatting error: ", expected, actual);

    }


    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan1Second() throws Exception {

        long testVal = 123;  //msec
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:00:00.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan10Seconds() throws Exception {

        long testVal = 8123;  //msec
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:00:08.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }



    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan1Minute() throws Exception {

        long testVal = 58123;  //msec, = 58.123secs
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:00:58.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan10Minutes() throws Exception {

        long testVal = 540123;  //msec, = 9 mins 00.123secs
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:09:00.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan60Minutes() throws Exception {

        long testVal = 3503123;  //msec, = 58mins 23.123secs
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "00:58:23.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }


    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan10Hours() throws Exception {

        long testVal = 32400000;  //9 hours
        testVal += 1380000;         //23 minutes
        testVal += 17123;       //17.123 seconds
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "09:23:17.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }


    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan24Hours() throws Exception {

        long testVal = 82800000;  //23 hours
        testVal += 3540000;         //59 minutes
        testVal += 59123;       //59.123 seconds
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "23:59:59.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationLessThan99Hours() throws Exception {

        long testVal = 194400000;  //54 hours
        testVal += 1380000;         //23 minutes
        testVal += 17123;       //17.123 seconds
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "54:23:17.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationMoreThan99Hours() throws Exception {

        long testVal = 367200000;  //102 hours
        testVal += 1380000;         //23 minutes
        testVal += 17123;       //17.123 seconds
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "102:23:17.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    /**
     * Test formatting of duration specified by method name.
     * 
     * @throws Exception
     */
    public void testFormattingOfDurationMoreThan999Hours() throws Exception {

        long testVal = 4442400000L;  //1,234 hours
        testVal += 1380000;         //23 minutes
        testVal += 17123;       //17.123 seconds
        
        String actual = Utilities.formatDuration(testVal);
        String expected = "1234:23:17.123";
        
        assertEquals("Formatting error: ", expected, actual);

    }

    
}
