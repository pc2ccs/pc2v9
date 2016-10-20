package edu.csus.ecs.pc2.core.util;

import java.util.Date;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.model.Pluralize;

/**
 * Simple Date diff in English.
 * 
 * This class is intended to provide a English description
 * of the difference between two dates. 
 * <br><br>
 * This class will be precise for date differences under a day.
 * It was designed to use  {@link DateFormat#COUNT_DOWN},
 * any difference above a day will be imprecise due to
 * the time for exhaustive testing.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class DateDifferizer {

    private Date firstDate;

    private Date secondDate;

    private DateFormat format = DateFormat.COUNT_DOWN;

    /**
     * Date formats, for use with DateDifferizer.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    public enum DateFormat {
        
        /**
         * YYYY-MM-DD HH24:MM:SS
         */
        YYYYMMDD_FORMAT,
        /**
         * In English all fields, YY years MM months DD days HH hours MM minutes SS seconds
         */
        LONG_FULL_FORMAT,

        /**
         * In English minimal fields, , YY years HH hours SS seconds. <br>
         * Does not output fields when the value is zero.
         */
        LONG_FORMAT,

        /**
         * Count down format.
         * 
         * <pre>
         * Will output in the following format:
         * Over a year: N year(s)
         * Under a year, a month or more: N month(s)
         * Under a month, a day or more: N day(s)
         * Under a day, over an hour: HH:MM:SS
         * Under an hour: HH:SS
         * </pre>
         */
        COUNT_DOWN,
    }

    /**
     * Initialize with two dates.
     * 
     * @param date
     * @param date2
     */
    public DateDifferizer(Date date, Date laterDate) {
        this(DateFormat.COUNT_DOWN, date, laterDate);
    }

    /**
     * Initialize with a date with an offset.
     * @param date
     * @param field - field name from {@link GregorianCalendar#add(int, int)}
     * @param amount
     */
    public DateDifferizer(Date date, int field, int amount) {
        this(DateFormat.COUNT_DOWN, date, field, amount);
    }

    /**
     * Initialize with two dates and a date format.
     * @param format
     * @param date
     * @param date2
     */
    public DateDifferizer(DateFormat format, Date date, Date laterDate) {
        this.format = format;
        this.firstDate = date;
        this.secondDate = laterDate;
    }

    /**
     * Create second date based on increment of first date.
     * 
     * @see GregorianCalendar#add(int, int)
     * @param date
     * @param field
     * @param amount
     */
    public DateDifferizer(DateFormat format, Date date, int field, int amount) {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(field, amount);

        this.firstDate = date;
        this.secondDate = gregorianCalendar.getTime();
    }

    public long diffInSec() {
        return diffInMS() / 1000;
    }

    public long diffInMS() {
        return secondDate.getTime() - firstDate.getTime();
    }

    private String formatTime() {
        return formatTime(format);
    }

    /**
     * 
     * @return and english string of the difference between the dates.
     */
    public String formatTime(DateFormat dateFormat) {

        long totalSeconds = diffInSec();
        

        long days = 0;
        long months = 0;
        long years = 0;

        long seconds = totalSeconds % Constants.SECONDS_PER_MINUTE;
        long minutes = totalSeconds / Constants.SECONDS_PER_MINUTE;

        /**
         * total hours
         */
        long hours = minutes / Constants.MINUTES_PER_HOUR;

        minutes %= Constants.MINUTES_PER_HOUR;

        /**
         * 
         */
        long hours24 = hours % Constants.HOURS_PER_DAY;

        if (totalSeconds >= Constants.SECONDS_PER_HOUR * 24) {

            days = hours / 24;
            months = days / 30;
            years = months / 12;

            days %= 30;
            months = months - (years * 12);
        }
        

        return formatTime(dateFormat, (int) years, (int) months, (int) days, (int) hours24, (int) minutes, (int) seconds);
    }

    public static String formatTime(DateFormat dateFormat, int years, int months, int days, int hours24, int minutes, int seconds) {

        String outs = years + "-" + months + "-" + days + " " + hours24 + ":" + minutes + ":" + seconds;
        

        switch (dateFormat) {
            case LONG_FORMAT:
                outs = pluralStringNotEmpty("year", years) + " " + //
                        pluralStringNotEmpty("month", months) + " " + //
                        pluralStringNotEmpty("day", days) + " " + //
                        pluralStringNotEmpty("hour", hours24) + " " + //
                        pluralStringNotEmpty("minute", minutes) + " " + //
                        pluralStringNotEmpty("second", seconds);
                outs = outs.replaceAll("  ", " ").trim();
                break;
            case LONG_FULL_FORMAT:

                outs = pluralString("year", years) + " " + //
                        pluralString("month", months) + " " + //
                        pluralString("day", days) + " " + //
                        pluralString("hour", hours24) + " " + //
                        pluralString("minute", minutes) + " " + //
                        pluralString("second", seconds);

                break;

            case COUNT_DOWN:
                outs = countDownFormat(years, months, days, hours24, minutes, seconds);
                break;

            case YYYYMMDD_FORMAT:
            default:
                outs = lpad('0', 4, years) + "-" + lpad('0', 2, months) + "-" + lpad('0', 2, days) + " " + //
                        lpad('0', 2, hours24) + ":" + lpad('0', 2, minutes) + ":" + lpad('0', 2, seconds);

                break;
        }
        return outs;
    }

    private static String countDownFormat(int years, int months, int days, int hours24, int minutes, int seconds) {
        if (years > 0) {
            return pluralString("year", years);
        } else if (months > 0) {
            return pluralString("month", months);
        } else if (days > 0) {
            return pluralString("day", days) + " " + hours24 + ":" + lpad('0', 2, minutes) + ":" + lpad('0', 2, seconds);
        } else { 
            return hours24 + ":" + lpad('0', 2, minutes) + ":" + lpad('0', 2, seconds);
        }
    }

    private static String pluralStringNotEmpty(String term, int count) {
        if (count != 0) {
            return pluralString(term, count);
        } else {
            return "";
        }
    }

    /**
     * Output pluralized string.
     * 
     * Ex. pluralString("year", 3), outpout: 3 years <br>
     * pluralString("year", 1), outpout: 1 year <br>
     * 
     * @param term
     * @param count
     * @return
     */
    public static String pluralString(String term, int count) {
        return count + " " + Pluralize.pluralize(term, count);
    }

    /**
     * Left pad string.
     * 
     * @param padChar
     * @param minFieldLength
     * @param value
     * @return
     */
    public static String lpad(char padChar, int minFieldLength, int value) {
        String v = Integer.toString(value);
        int addchars = minFieldLength - v.length();
        if (addchars > 0) {
            v = new String(new char[addchars]).replace('\0', padChar) + v;
        }
        return v;
    }
    
    public void setFormat(DateFormat format) {
        this.format = format;
    }
    
    public DateFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return formatTime();
    }

}
