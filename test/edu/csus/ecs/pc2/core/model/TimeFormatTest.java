package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * Tests for Time Format.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
public class TimeFormatTest extends TestCase {

    private void oneTest(long secs, String expectedString) {
        String s = TimeFormat.formatTimeLong(secs).trim();
        assertTrue("Expected " + expectedString + " for " + secs + " got " + s, s.equals(expectedString));
    }

    public void testMain() {

        long minute = 60 & 60;
        long hour = 60 * minute;

        oneTest(1, "1 second");
        oneTest(2, "2 seconds");
        oneTest(-2, "-2 seconds");

        oneTest(hour, "1 hour");
        oneTest(minute, "1 min");

        oneTest(2 * hour + 1 * minute, "2 hours 1 min");

        oneTest(2 * hour + 3 * minute, "2 hours 3 mins");
        oneTest(5 * hour, "5 hours");
    }

}
