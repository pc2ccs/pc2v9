// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.Run}s.
 * 
 * This class maintains a list of runs, as well as load/save the runs to disk. <br>
 * 
 * @author pc2@ecs.csus.edu
 */

public class RunList implements Serializable {

    private static final long serialVersionUID = 1446963022315687590L;

    /**
     * List of runs.
     */
    private Hashtable<String, Run> runHash = new Hashtable<String, Run>(200);

    /**
     * Lock for accessing list of runs.
     */
    private final Object runHashLock = new Object();

    /**
     * Save runHash to disk for every update/add.
     */
    private boolean saveToDisk = false;

    private int nextRunNumber = 1;

    private IStorage storage;

    private LinkedList<String> backupList = new LinkedList<String>();
    
    private RunListWriteThread runListWriteThread = null;
    
    private Object runListWriteLock = new Object();

    public RunList() {
        saveToDisk = false;
        nextRunNumber = getINIBaseRunNumber();
        createAndStartRunListWriteThread();
    }

    private int getINIBaseRunNumber() {
        int retVal ;
        if (IniFile.isFilePresent()) {
            try {
                String baseNum = IniFile.getValue("server.baseRunNumber") ;
                if (baseNum == null || baseNum.equals("")) {
                    retVal = 1; 
                } else {
                    //SOMEDAY: remove the following line and log a message instead (note that the problem is lack of access to the controller/log)
                    System.out.println ("Found server.baseRunNumber in .ini file: '" + baseNum + "'");
                    retVal = Integer.parseInt(baseNum);
                    if (retVal < 1) {
                        throw new NumberFormatException("Negative base not allowed: " + baseNum);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println ("Illegal base run number value in INI file: '" + IniFile.getValue("server.baseRunNumber") + "'; defaulting to base run number = 1");
                retVal = 1;
            }
        } else {
            retVal = 1;
        }

        return retVal;
    }

    /**
     * Create list and save list to disk on update/add.
     * 
     * @param dirname
     */
    public RunList(IStorage storage) {
        this();
        this.storage = storage;
        saveToDisk = true;
    }

    /**
     * Add new run, increment run number.
     * 
     * @param run
     * @return the run now with the run number.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run addNewRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        synchronized (runHashLock) {
            run.setNumber(nextRunNumber++);
        }
        add(run);
        return run;
    }

    /**
     * Add a run into the run list, no changes.
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void add (Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        synchronized (runHashLock) {
            runHash.put(getRunKey(run), run);
        }
        if (saveToDisk) {
            writeToDisk();
        }
    }


    /**
     * Get a run from the list.
     */
    private Run get(String key) {
        synchronized (runHashLock) {
            return runHash.get(key);
        }
    }

    public Run get(ElementId id) {
        synchronized (runHashLock) {
            return runHash.get(id.toString());
        }
    }

    /**
     * Get a run from the list.
     */
    public Run get(Run run) {
        return get(getRunKey(run));
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
     * Mark run as deleted.
     * 
     * Does not remove from list simply marks run as deleted.
     * 
     * @param run
     * @return true if deleted, false if not deleted or not found.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public boolean delete(Run run) throws IOException, ClassNotFoundException, FileSecurityException {

        synchronized (runHashLock) {
            Run fetchedRun = get(getRunKey(run));
            if (fetchedRun == null) {
                return false;
            } else {
                fetchedRun.setDeleted(true);
            }
        }
        writeToDisk();
        return true;


    }

    /**
     * Remove all items from list.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void clear() throws IOException, ClassNotFoundException, FileSecurityException {
        synchronized (runHashLock) {
            runHash = new Hashtable<String, Run>(200);
            nextRunNumber = getINIBaseRunNumber();
        }
        writeToDisk();

    }

    /**
     * Update the run with a new run state.
     * 
     * @param run
     * @param newState
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void updateRunStatus(Run run, RunStates newState) throws IOException, ClassNotFoundException, FileSecurityException {

        synchronized (runHashLock) {
            Run theRun = runHash.get(getRunKey(run));
            theRun.getElementId().incrementVersionNumber();
            theRun.setStatus(newState);
        }
        writeToDisk();

    }

    /**
     * Replace run, increment version number.
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void updateRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        synchronized (runHashLock) {
            run.getElementId().incrementVersionNumber();
            runHash.put(getRunKey(run), run);
        }
        writeToDisk();        
    }

    /**
     * Update run, increment version number add judgement.
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void updateRun(Run run, JudgementRecord judgement, boolean manualReview) throws IOException, ClassNotFoundException, FileSecurityException {

        synchronized (runHashLock) {
            Run theRun = runHash.get(getRunKey(run));

            theRun.getElementId().incrementVersionNumber();

            if (theRun.getStatus().equals(RunStates.BEING_JUDGED)) {

                if ((manualReview) && (judgement.isComputerJudgement())) {
                    judgement.setPreliminaryJudgement(true);
                    theRun.setStatus(RunStates.MANUAL_REVIEW);
                } else {
                    theRun.setStatus(RunStates.JUDGED);
                }
            } else {
                theRun.setStatus(RunStates.JUDGED);
            }

            theRun.addJudgement(judgement);
            theRun.replaceTestCases(run.getRunTestCases());
        }
        writeToDisk();
    }

    public Enumeration <Run> getRunList() {
        synchronized (runHashLock) {
            return runHash.elements();
        }
    }

    protected String getFileName() {
        return storage.getDirectoryName() + File.separator + "runlist.dat";
    }


    public String getBackupFilename() {
        return storage.getDirectoryName() + File.separator + "runlist." + Utilities.getDateTime() + "." + System.nanoTime() + ".dat";
    }


    /**
     * Write the run data to disk.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * 
     * @throws IOException
     * 
     */
    private boolean writeToDisk() throws IOException, ClassNotFoundException, FileSecurityException {

        if (!isSaveToDisk()) {
            return false;
        }
        
        synchronized (runListWriteLock) {
            runListWriteLock.notify();
        }

        return true;
    }

    /**
     * 
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileSecurityException 
     * 
     */
    @SuppressWarnings("unchecked")
    public boolean loadFromDisk(int siteNumber) throws IOException, ClassNotFoundException, FileSecurityException  {
        String filename = getFileName();
        if (Utilities.isFileThere(filename)) {
            synchronized (runHashLock) {
                runHash = (Hashtable<String, Run>) storage.load(filename);
                nextRunNumber = lastRunNumber(siteNumber) + 1;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Loop through list and determine the last run number for input site.
     * 
     * @param siteNumber
     */
    private int lastRunNumber(int siteNumber) {
        int lastNumber = 0;

        for (Run run : getList()) {
            if (run.getSiteNumber() == siteNumber) {
                lastNumber = Math.max(lastNumber, run.getNumber());
            }
        }

        return lastNumber;
    }

    public int size() {
        synchronized (runHashLock) {
            return runHash.size();
        }
    }

    public Run[] getList() {
        synchronized (runHashLock) {
            if (runHash.size() == 0) {
                return new Run[0];
            }

            return (Run[]) runHash.values().toArray(new Run[size()]);
        }
    }

    public boolean isSaveToDisk() {
        return saveToDisk;
    }

    public void setSaveToDisk(boolean saveToDisk) {
        this.saveToDisk = saveToDisk;
    }


    public void clone(IStorage storage2) {
        try {
            if (saveToDisk) {
                if (runHash.size() > 0){
                    storage2.store(getFileName(), runHash);
                }
            }
        } catch (Exception e) {
            logException("Unable to copy run info files " + getFileName() + " to " + storage2.getDirectoryName(), e);
        }
    }

    private void logException(String string, Exception e) {
        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, string, e);
        } else {
            System.err.println(string + " " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public int getNextRunNumber() {
        return nextRunNumber;
    }

    /**
     * Backup and store run list.
     * @return true if run list and backup done.
     */
    boolean backupAndWriteRunList() {

        boolean stored = false;

        String fileName = getFileName();

        String backupFilename;

        Hashtable<String, Run> copyofRunHash;

        try {

            synchronized (runHashLock) {
                backupFilename = getBackupFilename();

                try {
                    File currentRunList = new File(fileName);
                    File backupRunList = new File(backupFilename);

                    currentRunList.renameTo(backupRunList);
                } catch (Exception e) {
                    StaticLog.getLog().log(Log.WARNING, "FAILED to rename current " + fileName + " to " + backupFilename, e);
                }

                copyofRunHash = new Hashtable<String, Run>(runHash);

                stored = storage.store(fileName, copyofRunHash);
            }
            stored = true;

            backupList.add(backupFilename);
            while (backupList.size() > 100) {
                String removeBackupFile = backupList.removeFirst();
                File file = new File(removeBackupFile);
                if (file.exists()) {
                    file.delete();
                }
            }

        } catch (Exception e) {
            StaticLog.getLog().log(Log.WARNING, "Problem storing or backing up run list", e);
        }

        return stored;
    }
    
    
    public RunListWriteThread createAndStartRunListWriteThread() {
        if (runListWriteThread == null) {
            runListWriteThread = new RunListWriteThread();
            runListWriteThread.start();
        }
        
        return runListWriteThread;
    }
    
    
    /**
     * Thread that writes that backs up and writes run list file.
     * 
     * @author Douglas A. Lane <pc2@ecs.csus.edu>
     *
     */
    class RunListWriteThread extends Thread {

        @Override
        public void run() {
            try {

                synchronized (runListWriteLock) {
                    while (true) {
                        runListWriteLock.wait();
                        backupAndWriteRunList();
                        
                    }
                }

            } catch (Exception e) {
                StaticLog.getLog().log(Log.WARNING, "Problem storing or backing up run list", e);
            }

        }
    }

}
