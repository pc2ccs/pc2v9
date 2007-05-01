package edu.csus.ecs.pc2.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Loads and access to property values (.ini file).
 * 
 * Files are in the format of Windows .ini files.<br>
 * Sections are surrounded by [], blank lines and lines starting with # are ignored. The name value pairs are delimited by = marks.
 * <p>
 * Sample:<br>
 * 
 * <pre>
 *     # Sample Comment
 *     [server]
 *     host=56.23.43.12
 *     port=55002
 *     site=Northwest Division
 *     remoteHost=philly
 *     
 *     [client]
 *     site=Northwest Division
 *     
 *     # eof
 * </pre>
 * 
 * <P>
 * 
 * @author pc2@ecs.csus.edu
 */

// TODO change System.out to logging routine

// $HeadURL$
public class IniFile {

    public static final String SVN_ID = "$Id$";

    /**
     * Name/Value pairs.
     */
    private static Hashtable<String, String> nameValueHash = new Hashtable<String, String>();

    /**
     * The current section as input file scanned.
     */
    private static String currentSectionName = "";

    /**
     * Default .ini filename.
     */
    private static final String INI_FILENAME = "pc2v9.ini";

    /**
     * The input file in URL format.
     */
    private static URL iniFileURL = null;

    public IniFile() {
        load();
    }

    /**
     * returns true if key is in file.
     * 
     * @return boolean
     * @param key
     *            java.lang.String
     */
    public static boolean containsKey(String key) {
        String k = key.trim().toLowerCase();
        return nameValueHash.containsKey(k);
    }

    /**
     * returns true if key is in file.
     * 
     * @param sectionName
     * @param keyName
     */
    public static boolean containsKey(String sectionName, String keyName) {
        String key = sectionName + ":" + keyName;
        String k = key.trim().toLowerCase();
        return nameValueHash.containsKey(k);
    }

    /**
     * List contents to System.out.
     */
    public static void dump(){
        dump(System.out);
    }
    
    /**
     * Write contents to PrintStream.
     * @param ps PrintStream
     */
    public static void dump(PrintStream ps) {
        ps.println("Dumping: " + getIniFileURL());
        // String dump=new String("Dumping IniFile");
        Enumeration enumeration = nameValueHash.keys();
        Object key;
        while (enumeration.hasMoreElements()) {
            key = enumeration.nextElement();
            ps.println("key=" + key + ";value=" + nameValueHash.get(key));
        }
    }

    /**
     * Get input filename.
     * @return the name of the .ini file.
     */
    public static String getINIFilename() {
        return new String(INI_FILENAME);
    }

    /**
     * Get filename in URL form.
     * @return the URL for the .ini file.
     */
    public static URL getIniFileURL() {
        return iniFileURL;
    }

    /**
     * Returns the value for the specified key.
     * 
     * For a site key in the server section use: <code>
     * getValue("server.site");  or getValue("server","site");
     * </code>
     * 
     * @return java.lang.String
     * @param key
     *            java.lang.String a section and key value, ex. "sever.site"
     */
    public static String getValue(String key) {
        String ret = null;
        String k = key.trim().toLowerCase();
        String value = nameValueHash.get(k);
        if (value == null) {
            ret = null;
        } else {
            ret = new String(value);
        }
        return ret;
    }

    /**
     * Return value for specified key.
     * 
     * @see #getValue(String)
     * @param sectionName
     * @param keyName
     */

    public static String getValue(String sectionName, String keyName) {
        String key = sectionName + ":" + keyName;
        return getValue(key);
    }

    /**
     * return true if .ini file is present.
     * 
     * @see #getINIFilename()
     * @see #setIniFile(String)
     * @return true if it file is present; false otherwise.
     */
    public static boolean isFilePresent() {
        File iniFilefile = new File(getINIFilename());
        return iniFilefile.exists();
    }

    /**
     * Load the name value pairs from the input file.
     * @see #getIniFileURL()
     * @see #iniFileURL
     */
    private void load() {
        FileReader fileReader = null;
        try {
            // default to reading ini file from current directory
            if (iniFileURL == null) {
                File iniFilefile = new File(getINIFilename());
                if (!iniFilefile.exists()) {
                    File curdir = new File(".");

                    System.out.println("Unable to read " + getINIFilename() + " file not found in " + curdir.getCanonicalPath());
                    (new Exception("Unable to read .ini file ")).printStackTrace();
                    return;
                }

                setIniFileURL(iniFilefile.toURL());
            }
            iniFileURL.openStream();
            nameValueHash.put("_source", iniFileURL.toString());
            nameValueHash.clear();
            currentSectionName = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(iniFileURL.openStream()));
            String line = in.readLine();
            while (line != null) {
                parseLine(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error reading ini " + e.getMessage());
            e.printStackTrace();
            return;
        }
        try {
            if (fileReader != null) {
                fileReader.close();
            }
        } catch (java.io.IOException ioe) {
            System.out.println("Error closing ini " + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    /**
     * parse and load a single line.
     * 
     * @param line
     *            line to be parsed and loaded.
     */
    private static void parseLine(String line) {
        int index;
        try {
            if (line.trim().equals("")) {
                return; // blank line, nothing to parse
            } else if ((line.charAt(0) == ';' || line.charAt(0) == '#')) {
                return; // lihne is a comment, nothing to parse
            } else if (line.charAt(0) == '[') {
                index = line.indexOf("]");
                currentSectionName = line.substring(1, index).trim().toLowerCase();
            } else {
                index = line.indexOf("=");
                if (index == -1) {
                    if (!(line.charAt(0) == ';' || line.charAt(0) == '#')) {
                        System.out.println("Invalid line format (missing =)" + line);
                    } // else line is a comment
                } else {
                    String key = line.substring(0, index).trim().toLowerCase();
                    String value = line.substring(index + 1);
                    // System.out.println("'"+currentSectionName+"'"+key+"'='"+value+"'");
                    if (currentSectionName.equals("")) {
                        nameValueHash.put(key, value);
                    } else {
                        nameValueHash.put(currentSectionName + "." + key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing line: '" + line + "'" + e.getMessage());
        }
    }

    /**
     * set a new input filename. 
     * @param newIniFile
     * @throws MalformedURLException
     */

    public static void setIniFile(String newIniFile) throws MalformedURLException {
        File inFilefile = new File(newIniFile);
        if (!inFilefile.exists()) {
            throw new SecurityException(newIniFile + " file not found");
        }

        setIniFileURL(inFilefile.toURL());
    }

    /**
     * Set a new input filename via URL.
     * @param newIniFileURL -
     *            new URL
     */
    public static void setIniFileURL(java.net.URL newIniFileURL) {
        iniFileURL = newIniFileURL;
    }
}
