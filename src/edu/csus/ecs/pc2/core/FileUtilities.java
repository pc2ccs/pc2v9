package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * File Utilities.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class FileUtilities {

    /**
     * Write array to PrintWriter.
     * 
     * @param writer a PrintWriter to which the specified array of Strings should be written
     * @param datalines a String array containing the lines of data to be written to the specified PrintWriter
     */
    public static void appendLines(PrintWriter writer, String[] datalines) {
        for (String s : datalines) {
            writer.println(s);
        }
    }

    public static void writeFileContents(String filename, String[] datalines) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        appendLines(writer, datalines);
        writer.close();
        writer = null;
    }
    
    /**
     * Search for CDP config/ path.
     * 
     * See {@link #findCDPConfigDirectory(File)}.
     * 
     * @param entry
     * @return null if not found
     * @throws MalformedURLException
     */
    public static URL findCDPConfigDirectoryURL(File entry) throws MalformedURLException {
        File file = findCDPConfigDirectory(entry);
        if (file == null){
            return null;
        } else {
            return new URL("file://"+file.getAbsolutePath());
        }
    }

    /**
     * Search for a CDP config/ directory.
     * 
     * The entry can be:
     * <li> a contest.yaml filename
     * <li> a config/ directory
     * <li> a CDP directory (that contains a config/ subdirectory)
     * <li> a pc2 samps contest/CDP location, ex sumithello or sumitMTC
     * 
     * @param entry a location 
     * @return null if no config directory found, else the CDP config/ directory.
     */
    public static File findCDPConfigDirectory(File entry) {
        File cdpConfigDirectory = null;

        if (entry.isDirectory()) {

            // assume they specified a CDP directory
            File configDir = new File(entry.getAbsoluteFile() + File.separator + IContestLoader.CONFIG_DIRNAME);

            if ( IContestLoader.CONFIG_DIRNAME.equals(entry.getName())) {
                // they specified a config/ directory
                cdpConfigDirectory = entry;
            }
            else if (configDir.isDirectory()) {
                cdpConfigDirectory = configDir;
            }

        } else if (entry.isFile()) {

            // a file

            if (IContestLoader.DEFAULT_CONTEST_YAML_FILENAME.equals(entry.getName())) {

                // the entry was the config/ directory.

                cdpConfigDirectory = entry.getParentFile();
            }

        } else {

            // Seach for a CDP in the samps/contests directory

            String sampleContestYamlFile = findSampleContestYaml(entry.getName());

            if (sampleContestYamlFile != null) {
                File yamlFile = new File(sampleContestYamlFile);
                String configDirPath = yamlFile.getParentFile().getAbsoluteFile().toString();
                cdpConfigDirectory = new File(configDirPath);
            }

        }
        return cdpConfigDirectory;
    }

    /**
     * 
     * @param name CDP sample directory name
     * @return null if dirname has no contest.yaml file.
     */
    public static String findSampleContestYaml(String dirname) {

        String conestYamleFilename = getSampleContesYaml(dirname);

        if (new File(conestYamleFilename).isFile()) {
            return conestYamleFilename;
        } else {
            return null;
        }
    }

    /**
     * 
     * @param dirname
     * @return CDP directory under samps/contests
     */
    public static String getContestSampleYamlFilename(String dirname) {
        String contestConfigDir = getContestSampleCDPConfigDirname(dirname);
        return contestConfigDir + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
    }

    public static String getContestSampleCDPConfigDirname(String dirname) {
        String contestConfigDir = getContestSampleCDPDirname(dirname) + File.separator + IContestLoader.CONFIG_DIRNAME;
        return contestConfigDir;
    }

    public static String getContestSampleCDPDirname(String dirname) {
        String cdpConfig = getSampleContestsDirectory() + File.separator + dirname;
        return cdpConfig;
    }

    /**
     * 
     * @return sample contests directory.
     */
    public static String getSampleContestsDirectory() {
        return "samps" + File.separator + "contests";
    }

    /**
     * 
     * @param dirname
     * @return
     */
    public static String getSampleContesYaml(String dirname) {
        return getContestSampleYamlFilename(dirname);
    }

    /**
     * Returns all filenames for the input directory, recurses by default
     * 
     * Read dir entries in directory, strip off relativeDirectory from any directory entry.
     * 
     * @param directory
     * @param relativeDirectory
     * @return all files names with relative paths.
     */
    public static ArrayList<String> getFileEntries(String directory, String relativeDirectory) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();

        if (relativeDirectory.length() > 0) {
            relativeDirectory += File.separator;
        }

        for (File entry : files) {
            if (entry.isFile()) {
                list.add(relativeDirectory + entry.getName());
            }
        }

        // recurse

        for (File entry : files) {
            if (entry.isDirectory() && !(entry.getName().equals(".") || entry.getName().equals(".."))) {
                list.addAll(getFileEntries(directory + File.separator + entry.getName(), //
                        relativeDirectory + entry.getName()));
            }
        }

        return list;
    }

    /**
     * Get file directory entries (files only) with path.
     * 
     * @param directory
     *            - directory to search and to prepend onto the matching filenames
     * @return a list of file entries with path
     */
    public static ArrayList<String> getFileEntries(String directory) {
        ArrayList<String> list = new ArrayList<>();
        File[] files = new File(directory).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                list.add(directory + File.separator + file.getName());
            }
        }
        return list;
    }

    /**
     * Get file directory entries (directories only) with path.
     * 
     * @param directory
     *            - directory to search and to prepend onto the matching filenames
     * @return a list of directory entries with path
     */
    public static List<String> getDirectoryEntries(String directory, boolean onlyFilenames) {
        ArrayList<String> list = new ArrayList<>();
        File[] files = new File(directory).listFiles();

        for (File file : files) {
            if (file.isDirectory() && (!"..".equals(file.getName()) || !".".equals(file.getName()))) {
                if (onlyFilenames) {
                    list.add(file.getName());
                } else {
                    list.add(directory + File.separator + file.getName());
                }
            }
        }
        return list;
    }

    /**
     * Get Current Working Directory.
     * 
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
     * get all directories and child directories (recurses).
     * 
     * @param directory
     * @return list of directory names
     */
    public static List<String> getAllDirectoryEntries(String directory) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();
        
        if (files != null) {
            
            for (File entry : files) {
                if (entry.isDirectory()) {
                    list.add(directory + File.separator + entry.getName());
                    if (!(entry.getName().equals(".") || entry.getName().equals(".."))) {
                        list.addAll(getAllDirectoryEntries(directory + File.separator + entry.getName()));
                    }
                }
            }
        }

        return list;
    }
    
    
    /**
     * get all file names under directory (recurses).
     * 
     * @param directory
     * @param matchString if not null returns filenames with matchString in file name.
     * @return
     */
    public static List<String> getAllFileEntries(String directory, String matchString) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();

        if (files != null) {

            for (File entry : files) {
                if (entry.isDirectory()) {
                    if (!(entry.getName().equals(".") || entry.getName().equals(".."))) {
                        list.addAll(getAllFileEntries(directory + File.separator + entry.getName(), matchString));
                    }
                } else if (matchString != null) {
                    String filename = entry.getAbsolutePath();
                    if (filename.contains(matchString)) {
                        list.add(entry.getAbsolutePath());
                    }
                } else
                    list.add(entry.getAbsolutePath());
            }
        }

        return list;
    }

    /**
     * return all filenames under directory
     * @param directory
     * @return all file names under directory
     */
    public static List<String> getAllFileEntries(String directory) {
        return getAllFileEntries(directory, null);
    }

}
