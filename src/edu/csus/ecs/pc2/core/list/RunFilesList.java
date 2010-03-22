package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

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

    private IStorage storage;

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
    public RunFilesList(IStorage storage) {
        this.storage = storage;
        writeToDisk = true;
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
        return storage.getDirectoryName();
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

    public RunFiles add(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException {
        if (writeToDisk) {
            String filename = getFileName(run);
            storage.store(filename, runFiles);
            return runFiles;
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

    private RunFiles getRunFiles(int siteNumber, int runNumber) throws IOException, ClassNotFoundException, FileSecurityException {

        if (writeToDisk) {
            String filename = getFileName(siteNumber, runNumber);
            Object obj = storage.load(filename);
            return (RunFiles) obj;
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public RunFiles getRunFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException {

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


    public void clone(IStorage storage2) {

        if (writeToDisk) {

            File dir = new File(storage.getDirectoryName());
            if (dir.isDirectory()) {
                FileFilter filter = new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.getName().toLowerCase().endsWith(RunResultsFileList.EXTENSION);
                    }
                };
                File[] entries = dir.listFiles(filter);

                for (File file : entries) {

                    try {
                        // Only clone files with JUDGEMENT_RECORD_ID

                        if (file.getCanonicalFile().toString().indexOf(JudgementRecord.JUDGEMENT_RECORD_ID) == -1) {

                            Serializable serializable = storage.load(file.getCanonicalPath());
                            storage2.store(file.getName(), serializable);
                        }
                    } catch (Exception e) {
                        logException("Unable to copy file " + file.getName() + " to " + storage2.getDirectoryName(), e);
                    }
                }
                dir = null;
                entries = null;
            }
        } 
    }

    private void logException(String string, Exception e) {
        if (StaticLog.getLog() == null) {
            StaticLog.getLog().log(Log.WARNING, string, e);
        } else {
            System.err.println(string + " " +e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
