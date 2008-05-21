package edu.csus.ecs.pc2.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

import edu.csus.ecs.pc2.core.security.FileSecurity;

/**
 * Various common routines.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public final class Utilities {
    
    private static boolean debugMode = false;

    /**
     * Constructor is private as this is a utility class which
     * should not be extended or invoked.
     */
    private Utilities() {
        super();
    }

    /**
     * Insure directory exists, if does not exist create it.
     *
     * @param dirName
     *            directory to create.
     * @return whether directory exists.
     */
    public static boolean insureDir(String dirName) {
        File dir = null;

        // insure that the ./newExecute directory is there
        dir = new File(dirName);
        if (!dir.exists() && !dir.mkdir()) {
            // TODO show user that couldn't create this directory
            System.out.println("insureDir Directory " + dir.getName()
                    + " could not be created.");
        }

        return dir.isDirectory();
    }

    /**
     * Read serialized object from file.
     *
     * @param filename
     * @return the object
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Object readObjectFromFile(String filename)
            throws IOException, ClassNotFoundException {
        
        try {
            return FileSecurity.readSealedFile(filename);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     *
     * @param filename
     * @param serializable
     * @return true, otherwise throws an exception
     * @throws IOException
     */
    public static boolean writeObjectToFile(String filename,
            Serializable serializable) throws IOException {
        try {
            FileSecurity.writeSealedFile(filename, serializable);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return true;
    }

    /**
     *
     * @param filename
     *            String name of file
     * @return boolean true if file exists.
     */
    public static boolean isFileThere(String filename) {
        File file = new File(filename);
        return file.isFile();
    }

    /**
     * Compares 2 char arrays for equality.
     * 
     * @param oldBuffer
     * @param newBuffer
     * @return true if oldBuffer is the same size and has the same contents of newBuffer
     */
    public static boolean isEquals(char[] oldBuffer, char[] newBuffer) {
        if (oldBuffer == null) {
            return(newBuffer == null);
        } else if (newBuffer == null) {
            // oldBuffer not null, but new buffer is
            return false;
        }
        // sizes no not match
        if (oldBuffer.length != newBuffer.length) {
            return false;
        }
        for (int i = 0; i < newBuffer.length; i++) {
            if (newBuffer[i] != oldBuffer[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns lines from file.
     * 
     * @param filename
     *            String file to load
     * @return lines from file
     * @throws IOException
     */
    public static String[] loadFile(String filename) throws IOException {
        Vector<String> lines = new Vector<String>();

        if (filename == null) {
            throw new IllegalArgumentException("filename is null");
        }

        if (!new File(filename).exists()) {
            return new String[0];
        }

        FileReader fileReader = new FileReader(filename);
        BufferedReader in = new BufferedReader(fileReader);
        String line = in.readLine();
        while (line != null) {
            lines.addElement(line);
            line = in.readLine();
        }
        in.close();
        fileReader.close();
        in = null;
        fileReader = null;

        if (lines.size() == 0) {
            return new String[0];
        }

        String[] out = new String[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            out[i] = lines.elementAt(i);
        }

        return out;

    }

    /**
     * Get Current Working Directory.
     * @return current working directory.
     */
    public static String getCurrentDirectory() {
        File curdir = new File(".");

        try {
            return curdir.getCanonicalPath();
        } catch (Exception e) {
            // ignore exception
            return ".";
        }
    }
    
    /**
     * @return the current dateTime in a local-sensitive manner in the full date/long time style
     */
    public static String getL10nDateTime() {
        Locale currentLocale = Locale.getDefault();
        Date today = new Date();
        DateFormat dateFormatter;
        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, currentLocale);
        return(dateFormatter.format(today));
    }
    
    /**
     * @param dateStyle
     * @param timeStyle
     * @param currentLocale
     * @return current date in localization format
     */
    public static String getL10nDateTime(int dateStyle, int timeStyle, Locale currentLocale) {
        Date today = new Date();
        DateFormat dateFormatter;
        dateFormatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, currentLocale);
        return(dateFormatter.format(today));
    }
    
    /**
     * Returns a date-time String as defined by RFC 2822 section 3.3
     * Useful for mail messages.
     * 
     * @return the date in rfc2822 format
     */
    public static String getRFC2822DateTime() {
        Calendar calendar = new GregorianCalendar();
        // per rfc 2822
        // date-time = [ day-of-week "," ] date time CFWS
        //date-time       =       [ day-of-week "," ] date FWS time [CFWS]
        //day-of-week     =       ([FWS] day-name) / obs-day-of-week
        //day-name        =       "Mon" / "Tue" / "Wed" / "Thu" /
        //                        "Fri" / "Sat" / "Sun"
        //date            =       day month year
        //year            =       4*DIGIT / obs-year
        //month           =       (FWS month-name FWS) / obs-month
        //month-name      =       "Jan" / "Feb" / "Mar" / "Apr" /
        //                        "May" / "Jun" / "Jul" / "Aug" /
        //                        "Sep" / "Oct" / "Nov" / "Dec"
        //day             =       ([FWS] 1*2DIGIT) / obs-day
        //time            =       time-of-day FWS zone
        //time-of-day     =       hour ":" minute [ ":" second ]
        //hour            =       2DIGIT / obs-hour
        //minute          =       2DIGIT / obs-minute
        //second          =       2DIGIT / obs-second
        //zone            =       (( "+" / "-" ) 4DIGIT) / obs-zone

        // Formatter Date/Time Conversions
        // 'a '   Locale-specific short name of the day of the week, e.g. "Sun", "Mon"
        // 'e'    Day of month, formatted as two digits, i.e. 1 - 31.
        // 'b'    Locale-specific abbreviated month name, e.g. "Jan", "Feb".
        // 'Y'    Year, formatted as at least four digits with leading zeros as necessary, e.g. 0092 equals 92 CE for the Gregorian calendar.
        // 'T'    Time formatted for the 24-hour clock as "%tH:%tM:%tS".
        // 'z'    RFC 822  style numeric time zone offset from GMT, e.g. -0800.
        return String.format("%1$ta, %1$te %1$tb %1$tY %1$tT %1$tz", calendar);
    }

    /**
     * Returns Yes if true, No if false.
     * @param b
     * @return Yes or No
     */
    public static String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Load INI file.
     * 
     * This will read a text file and strip out blank/empty lines
     * and lines that start with a hash mark.
     * <P>
     * This will also trim the input lines.
     * 
     * @param filename file to be read
     * @return String [] null if can't read/find file, else lines infile
     */
    public static String[] loadINIFile(String filename) {

        Vector<String> v = new Vector<String>();

        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r");
            String line;

            while ((line = file.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (line.charAt(0) != '#') {
                        v.addElement(line);
                    }
                }
            }

            file.close();
            file = null;
        } catch (Exception e) {
            return null;
        }
        
        return (String[]) v.toArray(new String[v.size()]);
    }

    
    public static String forHTML(String aText) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '&') {
                result.append("&amp;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '(') {
                result.append("&#040;");
            } else if (character == ')') {
                result.append("&#041;");
            } else if (character == '#') {
                result.append("&#035;");
            } else if (character == '%') {
                result.append("&#037;");
            } else if (character == ';') {
                result.append("&#059;");
            } else if (character == '+') {
                result.append("&#043;");
            } else if (character == '-') {
                result.append("&#045;");
            } else if (character == '\n') {
                result.append("<br>");
            } else {
                // the char is not a special one
                // add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Utilities.debugMode = debugMode;
    }
}
