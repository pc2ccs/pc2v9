package edu.csus.ecs.pc2.core.model;

/**
 * Formats seconds into time strings.
 * 
 * To format into HH:MM:SS use {@link #format(long)} <br>
 * To format into HH hours MM mins, MM mins, or SS seconds use {@link #formatTimeLong(long)}.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
public final class TimeFormat {

    private TimeFormat() {

    }

    /**
     * Format in the form [-]HH:MM:SS.
     * 
     * @param seconds
     * @return time in format [-]HH:MM:SS.
     */
    public static String format(long seconds) {

        boolean negative = seconds < 0;

        if (negative) {
            seconds = seconds * -1; // absolute value it ..
        }

        long hours = seconds / 3600;
        long mins = (seconds / 60) % 60;
        long secs = (seconds % 60);

        String hourStr = new Long(hours).toString();

        String minStr = new Long(mins).toString();
        if (mins < 10) {
            minStr = '0' + minStr;
        }

        String secStr = new Long(secs).toString();
        if (secs < 10) {
            secStr = '0' + secStr;
        }

        if (negative) {
            hourStr = "-" + hourStr;
        }

        return (hourStr + ':' + minStr + ':' + secStr);
    }

    /**
     * Output format HH hours MM mins.
     * <P>
     * Will not output certain fields if there are zero values.
     * <P>
     * If there are 2:10:00 then will output 2 hours 10 mins
     * <P>
     * If there are 10:30 then will output 10 minutes 30 secs
     * <P>
     * 
     * @param seconds
     * @return
     */
    public static String formatTimeLong(long seconds) {
        boolean negative = seconds < 0;

        if (negative) {
            seconds = seconds * -1; // absolute value it ..
        }

        long hours = seconds / 3600;
        long mins = (seconds / 60) % 60;
        long secs = (seconds % 60);

        String outputString = "";
        if (negative) {
            outputString = "-";
        }

        if (hours == 1) {
            outputString += " " + hours + " hour";
        } else if (hours > 1) {
            outputString += " " + hours + " hours";
        }

        if (mins == 1) {
            outputString += " " + mins + " min";
        } else if (mins > 1) {
            outputString += " " + mins + " mins";
        }

        if (hours == 0 && mins == 0) {

            outputString = secs + " seconds";
            if (secs == 1) {
                outputString = secs + " second";
            } else if (secs > 1) {
                outputString = secs + " seconds";
            }

            if (negative) {
                outputString = "-" + outputString;
            }

        } else {
            if (secs == 1) {
                outputString += " " + secs + " sec";
            } else if (secs > 1) {
                outputString += " " + secs + " secs";
            }
        }

        return (outputString);
    }
}
