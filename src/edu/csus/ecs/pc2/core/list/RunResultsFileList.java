package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Access to statistics about a run judgement (@link edu.csus.ecs.pc2.core.RunResultFiles}.
 * 
 * This stores the run retults files to disk if site or dirname specified.
 * 
 * @see edu.csus.ecs.pc2.core.model.RunResultFiles
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunResultsFileList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8277112546999332502L;

    /**
     * Write runs result files files to disk.
     * <P>
     * If true then writes and reads run files from db directory. This is intended for pc2 server modules.
     * <P>
     * if false then only caches a single RunResultFiles for the last added run files. This is intended for non-server pc2 modules.
     * 
     * 
     */
    private boolean writeToDisk = false;

    /**
     * Run Results files end in .files.
     */
    public static final String EXTENSION = ".files";

    private RunResultFiles singleRunResultFiles = null;

    /**
     * Directory where files are written
     */
    private IStorage storage;

    public RunResultsFileList() {
        writeToDisk = false;
    }

    public RunResultsFileList(IStorage storage) {
        this.storage = storage;
        writeToDisk = true;
    }

    protected String stripChar(String s, char ch) {
        int idx = s.indexOf(ch);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(ch + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch + "");
            }
            return sb.toString();
        }
        return s;
    }

    private String stripChars(String s) {
        return stripChar(s, ' ');
    }

    private String getFileName(int siteNumber, int runNumber, JudgementRecord judgementRecord) {
        return storage.getDirectoryName() + File.separator + "s" + siteNumber + "r" + runNumber + "." + stripChars(judgementRecord.getElementId().toString()) + EXTENSION;
    }

    public String getFileName(Run run, JudgementRecord judgementRecord) {
        return getFileName(run.getSiteNumber(), run.getNumber(), judgementRecord);
    }

    public RunResultFiles add(Run run, JudgementRecord judgementRecord, RunResultFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException {
        if (writeToDisk) {
            String filename = getFileName(run, judgementRecord);
            if (storage.store(filename, runFiles)) {
                return runFiles;
            } else {
                return null;
            }
        } else {
            singleRunResultFiles = runFiles;
            return singleRunResultFiles;
        }
    }

    private RunResultFiles getRunResultFiles(int siteNumber, int runNumber, JudgementRecord judgementRecord) throws IOException, ClassNotFoundException, FileSecurityException {
        if (writeToDisk) {
            String filename = getFileName(siteNumber, runNumber, judgementRecord);
            Object obj = storage.load(filename);
            return (RunResultFiles) obj;
        } else {
            return null;
        }
    }

    /**
     * return a run results file.
     * 
     * @param run
     *            Run
     * @return RunResultFiles files and statistics
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public RunResultFiles getRunResultFiles(Run run, JudgementRecord judgementRecord) throws IOException, ClassNotFoundException, FileSecurityException {

        if (writeToDisk) {
            return getRunResultFiles(run.getSiteNumber(), run.getNumber(), judgementRecord);
        } else {
            if (singleRunResultFiles.getJudgementId().equals(judgementRecord.getElementId())) {
                return singleRunResultFiles;
            } else {
                return null;
            }
        }
    }

    /**
     * returns a run result files.
     * 
     * @param run
     * @return
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public RunResultFiles[] getRunResultFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException {

        JudgementRecord[] judgementRecord = run.getAllJudgementRecords();
        RunResultFiles[] runResultFiles = new RunResultFiles[judgementRecord.length];

        for (int i = 0; i < judgementRecord.length; i++) {
            runResultFiles[i] = getRunResultFiles(run.getSiteNumber(), run.getNumber(), judgementRecord[i]);
        }

        return runResultFiles;
    }

    /**
     * Removes all run results files from disk.
     * 
     */
    public void clear() {
        if (writeToDisk) {

            File dir = new File(storage.getDirectoryName());
            if (dir.isDirectory()) {
                FileFilter filter = new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.getName().toLowerCase().endsWith(EXTENSION);
                    }
                };
                File[] entries = dir.listFiles(filter);

                for (File file : entries) {
                    try {
                        file.delete();
                        file = null;
                    } catch (Exception e) {
                        StaticLog.log("Failed to remove " + file.getName(), e);
                    }
                }
                dir = null;
                entries = null;
            }

        } else {
            singleRunResultFiles = null;
        }
    }

    public void clone(IStorage storage2) {

        if (writeToDisk) {

            File dir = new File(storage.getDirectoryName());
            if (dir.isDirectory()) {
                FileFilter filter = new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.getName().toLowerCase().endsWith(EXTENSION);
                    }
                };
                File[] entries = dir.listFiles(filter);

                for (File file : entries) {

                    try {
                        // Only clone files with JUDGEMENT_RECORD_ID

                        if (file.getCanonicalFile().toString().indexOf(JudgementRecord.JUDGEMENT_RECORD_ID) != -1) {

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

        } else {
            singleRunResultFiles = null;
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
