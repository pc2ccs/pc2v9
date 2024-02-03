// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
 * @version $Id$
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

    public void loadFile() {
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
    public static void dump() {
        dump(System.out);
    }

    /**
     * Write contents to PrintStream.
     * 
     * @param ps
     *            PrintStream
     */
    public static void dump(PrintStream ps) {
        ps.println("Dumping: " + getIniFileURL());
        // String dump=new String("Dumping IniFile");
        Enumeration<String> enumeration = nameValueHash.keys();
        Object key;
        while (enumeration.hasMoreElements()) {
            key = enumeration.nextElement();
            ps.println("key=" + key + ";value=" + nameValueHash.get(key));
        }
    }

    /**
     * Get input filename.
     * 
     * @return the name of the .ini file.
     */
    public static String getINIFilename() {
        return new String(INI_FILENAME);
    }

    /**
     * Get filename in URL form.
     * 
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
     * (This method is currently not used)
     * 
     * @see #getValue(String)
     * @param sectionName
     * @param keyName
     */

    public static String getValue(String sectionName, String keyName) {
        String key = sectionName + "." + keyName;
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
     * 
     * @see #getIniFileURL()
     * @see #iniFileURL
     */
    private void load() {
        BufferedReader in = null;
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

                setIniFileURL(iniFilefile.toURI().toURL());
            }
            synchronized (nameValueHash) {
                if (nameValueHash.contains("_source") && iniFileURL.toString().equals(nameValueHash.get("_source"))) {
                    // trying to load the same file, do not read it again
                    return;
                } else {
                    iniFileURL.openStream();
                    nameValueHash.clear();
                    nameValueHash.put("_source", iniFileURL.toString());
                    currentSectionName = "";
                    in = new BufferedReader(new InputStreamReader(iniFileURL.openStream()));
                    String line = in.readLine();
                    while (line != null) {
                        parseLine(line);
                        line = in.readLine();
                    }
                    in.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading ini " + e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * This will try newIni as an URL 1st, if it is not a valid URL attempt as a regular file.
     * 
     * @param newIni
     *            name of new .ini file.
     * @throws MalformedURLException
     */
    public static void setIniURLorFile(String newIni) throws MalformedURLException {
        try {
            URL url = new URL(newIni);
            setIniFileURL(url);
        } catch (java.net.MalformedURLException e) {
            // it better be a normal file then...
            setIniFile(newIni);
        }
    }

    /**
     * set a new input filename.
     * 
     * @param newIniFile
     * @throws MalformedURLException
     */

    public static void setIniFile(String newIniFile) throws MalformedURLException {
        File inFilefile = new File(newIniFile);
        if (!inFilefile.exists()) {
            throw new SecurityException(newIniFile + " file not found");
        }

        setIniFileURL(inFilefile.toURI().toURL());
    }

    /**
     * Set a new input filename via URL.
     * 
     * @param newIniFileURL
     *            - new URL
     */
    public static void setIniFileURL(java.net.URL newIniFileURL) {
        iniFileURL = newIniFileURL;
    }

    /**
     * Load the name value pairs from the input file.
     * 
     * @see #getIniFileURL()
     * @see #iniFileURL
     */
    public static void setHashtable(Hashtable<String, String> inHashTable) {
        nameValueHash = inHashTable;
    }

}
