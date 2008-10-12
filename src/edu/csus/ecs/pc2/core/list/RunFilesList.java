package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;

/**
 * Access to submitted files (@link edu.csus.ecs.pc2.core.model.RunFiles}
 * <P>
 * There are three different ways that RunFiles are stored using this class.
 * <ol>
 * <li>Single Run File only - only one RunFiles is stored for the last Run that used the {@link #add(Run, RunFiles)} method.
 * <li>Cached in memory - all RunFiles kept in memory, see {@link #setCacheRunFiles(boolean)} or {@link #RunFilesList(boolean)}
 * <li>Written to disk - all RunFiles are written to disk under a specified directory, see {@link #RunFilesList(int)} and {@link #RunFilesList(String)}.
 * </ol>
 * 
 * Since there are conflicting options from the list above, the precedence is as follows: written to disk, then cached in memory, then single run file saved. Note that written to disk must be done
 * with constructors, whereas cached can be done via a constructor or a set method {@link #setCacheRunFiles(boolean)}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFilesList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -15984887352160586L;

    public static final String SVN_ID = "$Id$";

    /**
     * Write runs files to disk.
     * <P>
     * If true then writes and reads run files from db directory. This is intended for pc2 server modules.
     * <P>
     * if false then only caches a single RunFiles for the last added run files. This is intended for non-server pc2 modules.
     * 
     * 
     */
    private boolean writeToDisk = false;

    private boolean cacheRunFiles = false;

    private RunFiles singleRunFiles = null;

    /**
     * Cached RunFiles
     */
    private HashMap<String, RunFiles> runFilesHash = new HashMap<String, RunFiles>();

    /**
     * Directory where files are written
     */
    private String dirname = "db";

    /**
     * Create run files list which are cached in memory.
     * 
     * @see #isCacheRunFiles()
     * @see #clearCache()
     * @param cacheRunFiles
     */
    public RunFilesList(boolean cacheRunFiles) {
        super();
        this.cacheRunFiles = cacheRunFiles;
    }

    /**
     * Create list of runfiles, write and read files off disk.
     * 
     * 
     * @param dirname
     */
    public RunFilesList(String dirname) {
        this.dirname = dirname;
        Utilities.insureDir(dirname);
        writeToDisk = true;
    }

    /**
     * Create list of runfiles, write and read files off disk.
     * 
     * @param dirname
     */
    public RunFilesList(int siteNumber) {
        this("db." + siteNumber);
    }

    /**
     * Creates a class that only stores a single RunFiles entry.
     * 
     * Each time the {@link #add(Run, RunFiles)} method is used, the new RunFiles overwrites the existing RunFiles.
     * 
     * To cache all runs one must pass true {@link #setCacheRunFiles(boolean)}
     * 
     * @see #add(Run, RunFiles)
     * @see #isCacheRunFiles()
     */
    public RunFilesList() {

    }

    /**
     * Base Directory name where runFileList files are stored.
     * 
     * @return directory where RunFiles are stored.
     */
    public String getDirectoryName() {
        return dirname;
    }

    private String getFileName(int siteNumber, int runNumber) {
        return getDirectoryName() + File.separator + "s" + siteNumber + "r" + runNumber + ".files";
    }

    private String getFileName(Run run) {
        return getFileName(run.getSiteNumber(), run.getNumber());
    }

    protected String getRunKey(int siteNumber, int runNumber) {
        return "s" + siteNumber + "r" + runNumber;
    }

    public RunFiles add(Run run, RunFiles runFiles) {
        if (writeToDisk) {
            String filename = getFileName(run);
            try {
                Utilities.writeObjectToFile(filename, runFiles);
                return runFiles;
            } catch (Exception e) {
                // TODO log could not write object to file.
                System.err.println("Unable to write file " + filename);
                e.printStackTrace();
                return null;
            }
        } else if (cacheRunFiles) {

            // Add to cache
            String key = getRunKey(run.getSiteNumber(), run.getNumber());
            runFilesHash.put(key, runFiles);
            return runFiles;

        } else {

            singleRunFiles = runFiles;
            return singleRunFiles;
        }
    }

    private RunFiles getRunFiles(int siteNumber, int runNumber) {

        // TODO need to throw an exception instead of capturing exception and printing
        // to stdout

        if (writeToDisk) {
            String filename = getFileName(siteNumber, runNumber);
            try {
                Object obj = Utilities.readObjectFromFile(filename);
                return (RunFiles) obj;
            } catch (Exception e) {
                // TODO log info - could not read RunFiles from disk.
                System.err.println("Unable to read object from file " + filename);
                e.printStackTrace();
                return null;
            }
        } else if (cacheRunFiles) {

            String key = getRunKey(siteNumber, runNumber);
            return runFilesHash.get(key);

        } else {

            return null;
        }
    }

    /**
     * Get the RunFiles for the input run.
     * 
     * If no RunFiles for this run are found, will return null.
     * 
     * @param run
     *            Run
     * @return RunFiles null if none found, else returns the RunFiles for the input run
     */
    public RunFiles getRunFiles(Run run) {

        if (writeToDisk) {
            return getRunFiles(run.getSiteNumber(), run.getNumber());
        } else if (cacheRunFiles) {
            return getRunFiles(run.getSiteNumber(), run.getNumber());
        } else {
            if (singleRunFiles != null && run.getElementId().equals(singleRunFiles.getRunId())) {
                return singleRunFiles;
            } else {
                return null;
            }
        }
    }

    /**
     * Get a key for an input run.
     * 
     * This key is used to save a Run into the list.
     * 
     * @param run
     * @return String which uniquely identifies a run.
     */

    public String getRunKey(Run run) {
        return run.getElementId().toString();
    }

    /**
     * This clears the in memory copy of the cache.
     * 
     */
    public void clearCache() {
        runFilesHash = new HashMap<String, RunFiles>();
        singleRunFiles = null;
    }

    public boolean isCacheRunFiles() {
        return cacheRunFiles;
    }

    /**
     * Set to true to cache all runs in memory.
     * 
     * 
     * 
     * @param cacheRunFiles
     */
    public void setCacheRunFiles(boolean cacheRunFiles) {
        this.cacheRunFiles = cacheRunFiles;
    }

    /**
     * Are RunFiles being written to disk ?.
     * 
     * @see #isWriteToDisk()
     * @return
     */
    public boolean isWriteToDisk() {
        return writeToDisk;
    }

}
