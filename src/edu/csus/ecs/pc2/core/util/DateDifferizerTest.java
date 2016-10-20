package edu.csus.ecs.pc2.core.util;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.util.DateDifferizer.DateFormat;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class DateDifferizerTest extends TestCase {

    /**
     * Test one second difference.
     * @throws Exception
     */
    public void testOneSecondDiff() throws Exception {

        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff = new DateDifferizer(date, Calendar.SECOND, 1);
        long newsecs = diff.diffInSec();
        assertEquals("Expecting one sec different ", 1, newsecs);

        expectedTimeString  = diff.toString();
        assertEquals("0:01", expectedTimeString);

    }

    public void testOneHourDiff() throws Exception {

        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff ;
        long newsecs ;

        diff = new DateDifferizer(date, Calendar.HOUR, 1);
        newsecs = diff.diffInSec();
        assertEquals("Expecting one hour different ", Constants.SECONDS_PER_HOUR, newsecs);

        expectedTimeString  = diff.toString();
        assertEquals("1:00:00", expectedTimeString);

    }

    public void testOneDayDiff() throws Exception {
        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff ;
        long newsecs ;

        diff = new DateDifferizer(date, Calendar.HOUR, 24);
        newsecs = diff.diffInSec();
        assertEquals("Expecting 24 hours  different ", Constants.SECONDS_PER_DAY, newsecs);

        // long format

        diff.setFormat(DateFormat.LONG_FULL_FORMAT);
        expectedTimeString = diff.toString();
        assertEquals("0 years 0 months 1 day 0 hours 0 minutes 0 seconds", expectedTimeString);

        // Countdown format.

        diff.setFormat(DateFormat.LONG_FORMAT);
        expectedTimeString = diff.toString();
        assertEquals("1 day", expectedTimeString);

    }

    public void testTwentyThreeFiftyNineDiff() throws Exception {
        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff ;
        long newsecs ;

        long diffSeconds = Constants.SECONDS_PER_DAY - 1;

        Date earlierDate = new Date(date.getTime() - (diffSeconds* Constants.MS_PER_SECONDS));

        diff = new DateDifferizer(earlierDate, date);
        newsecs = diff.diffInSec();
        assertEquals("Expecting same diff seconds for 23:59 seconds", diffSeconds, newsecs);

        // Countdown format.
        expectedTimeString = diff.toString();
        assertEquals("23:59:59", expectedTimeString);

        // long format

        diff.setFormat(DateFormat.LONG_FULL_FORMAT);
        expectedTimeString = diff.toString();
        assertEquals("0 years 0 months 0 days 23 hours 59 minutes 59 seconds", expectedTimeString);

        diff.setFormat(DateFormat.LONG_FORMAT);
        expectedTimeString = diff.toString();
        assertEquals("23 hours 59 minutes 59 seconds", expectedTimeString);
    }


    public void testTwoMonths() throws Exception {

        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff ;
        long newsecs ;

        long diffSeconds = 2682000;

        diff = new DateDifferizer(date, Calendar.MONTH, 1);
        newsecs = diff.diffInSec();
        assertEquals("Expecting same diff seconds for 23:59 seconds", diffSeconds, newsecs);

        // Countdown format.
        expectedTimeString = diff.toString();
        assertEquals("1 month", expectedTimeString);
    }

    public void testOneYearDiff() throws Exception {

        Date date = new Date();

        String expectedTimeString ;

        DateDifferizer diff ;
        long newsecs ;

        long diffSeconds = 31536000;

        diff = new DateDifferizer(date, Calendar.YEAR, 1);
        newsecs = diff.diffInSec();
        assertEquals("Expecting same diff seconds for 23:59 seconds", diffSeconds, newsecs);

        // Countdown format.
        expectedTimeString = diff.toString();
        assertEquals("1 year", expectedTimeString);
    }

    /**
     * Test formatTime using {@link DateFormat#COUNT_DOWN}.
     * 
     * @throws Exception
     */
    public void testformatTime() throws Exception {
        String s;

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 1, 2, 3, 4, 5, 6);
        assertEquals("1 year", s);

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 0, 10, 3, 23, 55, 59);
        assertEquals("10 months", s);

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 0, 0, 3, 4, 5, 6);
        assertEquals("3 days", s);

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 0, 0, 0, 4, 1, 12);
        assertEquals("4:01:12", s);

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 0, 0, 0, 0, 1, 12);
        assertEquals("1:12", s);

        s = DateDifferizer.formatTime(DateFormat.COUNT_DOWN, 0, 0, 0, 0, 0, 22);
        assertEquals("0:22", s);
    }

    /**
     * Test other DateFormat values.
     * @throws Exception
     */
    public void testOtherFormatTimesDiff() throws Exception {

        String s;

        s = DateDifferizer.formatTime(DateFormat.YYYYMMDD_FORMAT, 1, 2, 3, 4, 5, 6);
        assertEquals("0001-02-03 04:05:06", s);

        s = DateDifferizer.formatTime(DateFormat.YYYYMMDD_FORMAT, 2016, 10, 3, 23, 55, 59);
        assertEquals("2016-10-03 23:55:59", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FULL_FORMAT, 1, 2, 3, 4, 5, 6);
        assertEquals("1 year 2 months 3 days 4 hours 5 minutes 6 seconds", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FULL_FORMAT, 1, 1, 1, 1, 1, 1);
        assertEquals("1 year 1 month 1 day 1 hour 1 minute 1 second", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FULL_FORMAT, 8, 7, 6, 5, 4, 3);
        assertEquals("8 years 7 months 6 days 5 hours 4 minutes 3 seconds", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FORMAT, 1, 0, 1, 0, 1, 0);
        assertEquals("1 year 1 day 1 minute", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FORMAT, 0, 0, 0, 0, 0, 22);
        assertEquals("22 seconds", s);

        s = DateDifferizer.formatTime(DateFormat.LONG_FORMAT, 1, 0, 0, 0, 0, 0);
        assertEquals("1 year", s);
    }

    public void testPluralizeString() throws Exception {

        String s;

        s = DateDifferizer.pluralString("second", 1);
        assertEquals("Expecting ", "1 second", s);

        s = DateDifferizer.pluralString("year", 5);
        assertEquals("Expecting ", "5 years", s);

        s = DateDifferizer.pluralString("month", 3);
        assertEquals("Expecting ", "3 months", s);
    }

    public void testLPad() throws Exception {

        for (int i = 1; i < 21; i++) {
            String form = DateDifferizer.lpad('0', i, 9);
            assertEquals("Expecting " + i + " length", i, form.length());
        }

        String form = DateDifferizer.lpad('0', 4, 9999);
        int i = 4;
        assertEquals("Expecting " + i + " length, got " + form, i, form.length());

        form = DateDifferizer.lpad('0', 2, 9999);
        i = 4;
        assertEquals("Expecting " + i + " length, got " + form, i, form.length());
    }
}
