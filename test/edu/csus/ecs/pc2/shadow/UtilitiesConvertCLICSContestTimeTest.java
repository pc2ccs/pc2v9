package edu.csus.ecs.pc2.shadow;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * A class to test method Utilities.convertCLICSContestTimeToMS(contest_time).
 * 
 * The method under test is intended to accept a CLICS "contest_time" (aka "RELTIME")
 * String and return the number of milliseconds represented by the string, or Long.MIN_VALUE
 * if the String does not represent a valid CLICS contest_time.
 * Valid CLICS contest_times have the format "(-)?(h)*h:mm:ss(.uuu)?".
 * 
 */

public class UtilitiesConvertCLICSContestTimeTest extends AbstractTestCase{
    
    final long MSECS_PER_HOUR = 1000 * 60 * 60 ;
    final long MSECS_PER_MIN = 1000 * 60 ;
    final long MSECS_PER_SEC = 1000 ;

    
    /**
     * Test method Utilities.convertCLICSContestTimeToMS(contest_time) converting valid times.
     * Valid CLICS contest_times have the format "(-)?(h)*h:mm:ss(.uuu)?".
     * 
     * @throws Exception
     */
    public void testValidTimes() throws Exception {
        
        long expected;
        long timeMS;
        String time;
        
        time = "1:00:00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 1*MSECS_PER_HOUR;
        assertEquals("1:00:00 failed to convert properly: ", expected, timeMS);
        
        time = "10:00:00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 10*MSECS_PER_HOUR;
        assertEquals("10:00:00 failed to convert properly: ", expected, timeMS);
        
        time = "10:10:00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 10*MSECS_PER_HOUR + 10*MSECS_PER_MIN;
        assertEquals("10:10:00 failed to convert properly: ", expected, timeMS);
        
        time = "10:10:10";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 10*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC;
        assertEquals("10:10:10 failed to convert properly: ", expected, timeMS);
        
        time = "10:10:10.525";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 10*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC + 525;
        assertEquals("10:10:10.525 failed to convert properly: ", expected, timeMS);
        
        time = "-10:10:10";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = -(10*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC);
        assertEquals("-10:10:10 failed to convert properly: ", expected, timeMS);
        
        time = "35:10:10";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 35*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC;
        assertEquals("35:10:10 failed to convert properly: ", expected, timeMS);
        
        time = "135:10:10";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = 135*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC;
        assertEquals("135:10:10 failed to convert properly: ", expected, timeMS);
        
        time = "-135:10:10";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = -(135*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC);
        assertEquals("-135:10:10 failed to convert properly: ", expected, timeMS);
        
        time = "-135:10:10.987";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = -(135*MSECS_PER_HOUR + 10*MSECS_PER_MIN + 10*MSECS_PER_SEC + 987);
        assertEquals("-135:10:10.987 failed to convert properly: ", expected, timeMS);
        
    }

    /**
     * Test method Utilities.convertCLICSContestTimeToMS(contest_time) converting invalid times.
     * Valid CLICS contest_times have the format "(-)?(h)*h:mm:ss(.uuu)?".
     * 
     * @throws Exception
     */
    public void testInValidTimes() throws Exception {
        
        long expected;
        long timeMS;
        String time;
        
        time = "00:00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("00:00 failed to return invalid conversion: ", expected, timeMS);
        
        time = "";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("empty-string 'time' failed to return invalid conversion: ", expected, timeMS);
        
        time = null;
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("null-string 'time' failed to return invalid conversion: ", expected, timeMS);
        
        time = "00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("00 failed to return invalid conversion: ", expected, timeMS);
        
        time = "10:62:00";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("10:62:00 failed to return invalid conversion: ", expected, timeMS);
        
        time = "10:12:62";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("10:12:62 failed to return invalid conversion: ", expected, timeMS);
        
        time = "10:12:12.01";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("10:12:12.01 failed to return invalid conversion: ", expected, timeMS);
        
        time = "10:12:12.5555";
        timeMS = Utilities.convertCLICSContestTimeToMS(time);
        expected = Long.MIN_VALUE;
        assertEquals("10:12:12.5555 failed to return invalid conversion: ", expected, timeMS);
                
    }
}
