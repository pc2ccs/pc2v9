package edu.csus.pc2.ewuteam;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import edu.csus.ecs.pc2.core.Utilities;

/**
 * System Version Information.
 * 
 * Class will read the VERSION file (which contains version information for the entire system) and print out that version information. It also provides methods for accessing the version information
 * which can be invoked by other classes. <br>
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
 * @version $Id$
 * 
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/VersionInfo.java$
public class VersionInfo {

    /**
     * Name of file that contains version information.
     */
    private static final String VERSION_FILENAME = "VERSION";

    /**
     * Human readable version.
     */
    private static String versionNumber = "2.x";

    /**
     * Date the code was changed.
     */
    private static String versionDate = "no ver. date set/found";

    /**
     * email for Maintainer of this system.
     */
    private String contactEMail = "mailto:pc2@ecs.csus.edu";

    /**
     * Title for system/project.
     */
    private String systemName = "CSUS PC^2 Web Client";

    /**
     * build number
     */
    private static String buildNumber = "";

    public VersionInfo() {
        
        loadVersionInfoFromFile(locateHome() + File.separator + VERSION_FILENAME);

        // Sample MANIFEST.INF
        // Manifest-Version: 1.0
        // Ant-Version: Apache Ant 1.9.3
        // Created-By: 1.7.0_40-b43 (Oracle Corporation)
        // Main-Class: PC2JavaMiniserver
        // Specification-Version: 2.1
        // Implementation-Title: EWU Web Team Client
        // Implementation-Version: 108
        // Built-On: Wednesday, October 22 2014 08:38 UTC
        // Class-Path: pc2.jar JavaBridge.jar

        setBuildNumber(getManifestValue("Implementation-Version", "23"));
        setVersionDate(getManifestValue("Built-On", versionDate));
        setVersionNumber(getManifestValue("Specification-Version", "2.xx"));
        setSystemName(getManifestValue("Implementation-Title", systemName));

        String tempVerString = getVersionNumber();
        tempVerString = tempVerString.replaceFirst("beta", "");
        tempVerString = tempVerString.replaceFirst("DEBUG", "2");
        setVersionNumber(tempVerString);
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
        return systemName;
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

        return "Version " + versionNumber + " (" + versionDate + ") Java ver " + javaVer + " build " + buildNumber + " " + osName + " " + osVer + " (" + osArch + ") ";
    }

    public String[] getSystemVersionInfoMultiLine() {
        VersionInfo versionInfo = new VersionInfo();

        // CSUS Programming Contest System
        // Version 9.2 20101009 (Saturday, October 9th 2010 02:19 UTC) Build 2184
        // Java ver 1.5.0_05
        // Windows XP 5.1 (x86)

        String[] lines = { versionInfo.getSystemName(), "Version " + versionInfo.getPC2Version() + " Build " + versionInfo.getBuildNumber(), "Java ver " + versionInfo.getJavaVersion(),
                versionInfo.getOperatingSystemInformation(), };

        return lines;
    }

    public String getOperatingSystemInformation() {
        String osName = System.getProperty("os.name", "?");
        String osArch = System.getProperty("os.arch", "?");
        String osVer = System.getProperty("os.version", "?");
        return osName + " " + osVer + " (" + osArch + ") ";
    }

    public String getPC2Version() {
        return versionNumber + " (" + versionDate + ")";
    }

    public String getJavaVersion() {
        String javaVer = System.getProperty("java.version", "?");
        return javaVer;
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
     *            arguments to control what is printed.
     */
    public static void main(java.lang.String[] args) {
        String javaVer = System.getProperty("java.version", "?");
        String osName = System.getProperty("os.name", "?");
        String osArch = System.getProperty("os.arch", "?");
        String osVer = System.getProperty("os.version", "?");

        VersionInfo sri = new VersionInfo();

        System.out.println();
        System.out.println(sri.getSystemName());
        System.out.print("Version " + sri.getVersionNumber());
        if (!sri.getVersionDate().trim().equals("")) {
            System.out.print(" (" + sri.getVersionDate() + ")");
        }
        if (!sri.getBuildNumber().trim().equals("")) {
            System.out.print(" build " + sri.getBuildNumber());
        }
        System.out.println();
        System.out.println("Java version " + javaVer);
        System.out.println("OS: " + osName + " " + osVer + " (" + osArch + ") ");
        System.out.println();
        System.out.println(sri.getContactEMail());
        System.out.println();
        System.out.println("(This class: $Id$)");
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
//
//    /**
//     * List memory used to log.
//     * 
//     * @param log
//     *            What Log to print to.
//     */
//    public static void printMemoryToLog(Log log) {
//
//        long mem = Runtime.getRuntime().freeMemory();
//
//        log.info("Java freeMemory   " + mem + " " + mem / 1000 + "k");
//
//        mem = Runtime.getRuntime().totalMemory();
//        log.info("Java totalMemory  " + mem + " " + mem / 1000 + "k");
//
//        mem = Runtime.getRuntime().maxMemory();
//        log.info("Java maxMemory    " + mem + " " + mem / 1000 + "k");
//
//    }

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

        try {
            if (!new File(filename).exists()) {
                // api usage
                return;
            }
            String[] lines = Utilities.loadFile(filename);

            if (lines.length == 0) {
                System.err.println("Unable to read " + filename + " version information unavailable");
            } else if (lines.length < 2) {
                System.err.println("Unable to find version information in " + filename + ", version information unavailable");
            } else {
                String[] fields = lines[1].split("\\s+");

                if (fields.length == 10) {
                    setVersionNumber(fields[1] + " " + fields[2]);
                    String versionString = fields[4] + " " + fields[5] + " " + fields[6] + addNumberEnding(fields[6]) + " " + fields[7] + " " + fields[8] + " " + fields[9];
                    setVersionDate(versionString.substring(1, versionString.length() - 1));
                    setBuildNumber(fields[3]);
                    // versionFileRead = true;
                } else {
                    System.err.println("Incorrect number of fields on line 2 in file: \"" + filename + "\", version information unavailable");
                    System.err.println("Expecting 10 fields, found " + fields.length + " fields.");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception loading " + filename);
            e.printStackTrace(System.err);
        }
    }

    /**
     * Attempts to locate pc2home/lib/pc2.jar, returns pc2home.
     * 
     * @return location of VERSION file
     */
    public String locateHome() {
        String pc2home = "."; // default to current directory
        try {
            String cp = System.getProperty("java.class.path");
            StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File dir = new File(token);
                if (dir.exists() && dir.isFile() && dir.toString().endsWith("pc2.jar")) {
                    pc2home = new File(dir.getParent() + File.separator + "..").getCanonicalPath();
                    break;
                }
                // running under eclipse
                if (dir.exists() && dir.isDirectory() && dir.toString().endsWith(".classes")) {
                    pc2home = new File(dir.toString() + File.separator + "..").getCanonicalPath();
                }
            }
        } catch (IOException e) {
            System.err.println("Trouble locating pc2home: " + e.getMessage());
            pc2home = ".";
        }
        return (pc2home);
    }
//
//    public String getManifestValue(String key, String defaultValue) {
//        
//        String value = defaultValue;
//        
//         String manifestPath = "/META-INF/MANIFEST.MF";
//         Manifest manifest = new Manifest(new URL(manifestPath).openStream());
//         Attributes attr = manifest.getMainAttributes();
//         String value = attr.getValue("Manifest-Version");
//     
//        return value;
//    }

    public String getManifestValue(String key, String defaultValue) {

        String value = defaultValue;

        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                
                /**
                 * Only return manifest info from EWU Team manifest.
                 */
                if (manifestContains(manifest, "Implementation-Title", "EW")) {
                    Attributes mainAttribs = manifest.getMainAttributes();
                    String attribValue = mainAttribs.getValue(key);

                    if (attribValue != null) {
                        value = attribValue;
                    }
                }
                    
            }
        } catch (Exception e) {
            // ignore exception, use defautl value if value cannot be fetched/found.
            e.printStackTrace(); // TODO remove this
        }
        return value;
    }

    /**
     * If manifest value for key contains searchForString return true.
     * 
     * @param manifest
     * @param key name in manifest
     * @param searchForString
     * @return if manifest value for key contains searchForString return true.
     */
    private boolean manifestContains(Manifest manifest, String key, String searchForString) {
        
        Attributes mainAttribs = manifest.getMainAttributes();
        String value = mainAttribs.getValue(key);
        if (value != null) {
            return value.indexOf(searchForString) != -1;
        }
        return false;
    }

    public void setBuildNumber(String newBuildNumber) {
        buildNumber = newBuildNumber;
    }

    public void setVersionDate(String newVersionDate) {
        versionDate = newVersionDate;
    }

    public void setVersionNumber(String newVersionNumber) {
        versionNumber = newVersionNumber;
    }
    
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemURL() {
        return "http://pc2.ecs.csus.edu/";
    }
}
