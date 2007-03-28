package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * System Version Information.
 * 
 * Class will read the VERSION file (which contains version information for the entire system) and print out that version
 * information. It also provides methods for accessing the version information which can be invoked by other classes. <br>
 * <b>Input file requirements (VERSION)</b><br>
 * <br>
 * The expected input format for the 2nd line of the VERSION file is: <br>
 * 
 * <pre>
 *        Version # YYYYMMDD SVN_Build# (DOW, Month day[suffix] YYYY HH:MM TZ)
 * </pre>
 * 
 * An example is:
 * 
 * <pre>
 *        Version 9 20070101 555 (Monday, January 1st 2007 19:36 UTC)
 * </pre>
 * 
 * @author pc2@ecs.csus.edu.
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/VersionInfo.java$
public class VersionInfo {

    /**
     * svn id tag.
     */
    public static final String SVN_ID = "$Id$";

    /**
     * Name of file that contains version information.
     */
    private static final String VERSION_FILENAME = "VERSION";

    /**
     * Human readable version.
     */
    private String versionNumber = " 9 YYYYMMDD ";

    /**
     * Date the code was changed.
     */
    private String versionDate = "MMM D, YY HH:MMam";

    /**
     * Maintainer of this system.
     */
    private String contactEMail = "mailto:pc2@ecs.csus.edu";

    private String buildNumber = "BBB";

    public VersionInfo() {
        loadVersionInfoFromFile(VERSION_FILENAME);
    }

    /**
     * 
     * @return contact e-mail, typically (mailto:pc2@ecs.csus.edu).
     */
    public final String getContactEMail() {
        return contactEMail;
    }

    /**
     * 
     * @return system name.
     */
    public final String getSystemName() {
        return "CSUS Programming Contest Control System";
    }

    /**
     * Gets a long list of OS, Java and PC2 information.
     * 
     * 
     * @return long list of version information.
     */
    public final String getSystemVersionInfo() {

        String javaVer = System.getProperty("java.version", "?");
        String osName = System.getProperty("os.name", "?");
        String osArch = System.getProperty("os.arch", "?");
        String osVer = System.getProperty("os.version", "?");

        return "Version " + versionNumber + " (" + versionDate + ") Java ver " + javaVer + " OS: " + osName + " " + osVer + " ("
                + osArch + ") ";

    }

    /**
     * 
     * Returns long form for version date, like: "September 8th, 2006 9:19am".
     * 
     * @return version date in form Month DD, YYYY H:MMa
     */
    public final String getVersionDate() {
        return versionDate;
    }

    /**
     * 
     * @return version number in form: module version YYYYMMDD XX
     */
    public final String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Print list of OS, Java and PC2 version information.
     * 
     * @param args
     *            arguements to control what is printed.
     */
    public static void main(java.lang.String[] args) {
        String javaVer = System.getProperty("java.version", "?");
        String osName = System.getProperty("os.name", "?");
        String osArch = System.getProperty("os.arch", "?");
        String osVer = System.getProperty("os.version", "?");

        VersionInfo sri = new VersionInfo();

        System.out.println();
        System.out.println(sri.getSystemName());
        System.out.println("Version " + sri.getVersionNumber() + " (" + sri.getVersionDate() + ") build " + sri.getBuildNumber());
        System.out.println();
        System.out.println("Java version " + javaVer);
        System.out.println("OS: " + osName + " " + osVer + " (" + osArch + ") ");
        System.out.println();
        System.out.println(sri.getContactEMail());
        System.out.println();
        System.out.println("(This class: " + SVN_ID + ")");
        System.out.println();

        System.exit(0);

    }

    /**
     * Returns the build number from the svn id for this file.
     * 
     * @return build number
     */
    public final String getBuildNumber() {
        return buildNumber;
    }

    /**
     * List memory used to log.
     * 
     * @param log
     *            What Log to print to.
     */
    public static void printMemoryToLog(Log log) {

        long mem = Runtime.getRuntime().freeMemory();

        log.info("Java freeMemory   " + mem + " " + mem / 1000 + "k");

        mem = Runtime.getRuntime().totalMemory();
        log.info("Java totalMemory  " + mem + " " + mem / 1000 + "k");

        mem = Runtime.getRuntime().maxMemory();
        log.info("Java maxMemory    " + mem + " " + mem / 1000 + "k");

    }

    /**
     * Returns the suffix for the number string.
     * 
     * returns th for all items except: 1 returns st, 2 returns nd, 3 returns rd.
     * 
     * @param numberString
     * @return suffix for number
     */
    protected String addNumberEnding(String numberString) {

        if (numberString == null || numberString.trim().length() < 1) {
            return "";
        }

        // If Last character in string is not a digit, return ""

        char lastChar = numberString.charAt(numberString.length() - 1);
        if (!Character.isDigit(lastChar)) {
            return "";
        }

        String ending = "th";

        if (!numberString.endsWith("11") && !numberString.endsWith("12") && !numberString.endsWith("13")) {
            if (numberString.endsWith("1")) {
                return "st";
            } else if (numberString.endsWith("2")) {
                return "nd";
            }
            if (numberString.endsWith("3")) {
                return "rd";
            }
        }

        return ending;
    }

    /**
     * Load version information from file.
     * 
     * Expected format is: <code>Version 9_0_alpha_200612080628 20061208 874 (Friday, December 8 2006 06:28 UTC) </code>
     * 
     * @param filename
     */
    protected void loadVersionInfoFromFile(String filename) {

        String[] lines = Utilities.loadFile(filename);

        if (lines.length == 0) {
            System.err.println("Unable to read " + filename + " version information unavailable");
        } else if (lines.length < 2) {
            System.err.println("Unable to find version information in " + filename + ", version information unavailable");
        } else {
            String[] fields = lines[1].split("\\s+");

            if (fields.length == 10) {
                setVersionNumber(fields[1] + " " + fields[2]);
                String versionString = fields[4] + " " + fields[5] + " " + fields[6] + addNumberEnding(fields[6]) + " " + fields[7]
                        + " " + fields[8] + " " + fields[9];
                setVersionDate(versionString.substring(1, versionString.length() - 1));
                setBuildNumber(fields[3]);
            } else {
                System.err.println("Incorrect number of fields on line 2 in file: \"" + filename
                        + "\", version information unavailable");
                System.err.println("Expecting 10 fields, found " + fields.length + " fields.");
            }
        }
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
}
