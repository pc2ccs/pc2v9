package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.Run}s.
 * 
 * This class maintains a list of runs, as well as load/save the runs to disk. <br>
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1446963022315687590L;

    public static final String SVN_ID = "$Id$";

    /**
     * List of runs.
     */
    private Hashtable<String, Run> runHash = new Hashtable<String, Run>(200);

    /**
     * Directory where files are written
     */
    private String dirname = "db";

    /**
     * Save runHash to disk for every update/add.
     */
    private boolean saveToDisk = false;

    private int nextRunNumber = 1;

    public RunList() {
        saveToDisk = false;
    }

    /**
     * Create list and save list to disk on update/add.
     * 
     * @param dirname
     */
    private RunList(String dirname) {
        this.dirname = dirname;
        Utilities.insureDir(dirname);
        saveToDisk = true;
    }

    /**
     * Create list and save list to disk on update/add.
     * 
     * @param siteNumber
     *            site number for this set of runs.
     * @param saveToDisk
     *            boolean on update/add save to disk
     */
    public RunList(int siteNumber, boolean saveToDisk) {
        this("db." + siteNumber);
        this.saveToDisk = saveToDisk;
    }

    private String getDirectoryName() {
        return dirname;
    }

    /**
     * Add new run, increment run number.
     * 
     * @param run
     * @return the run now with the run number.
     */
    public Run addNewRun(Run run) {
        run.setNumber(nextRunNumber++);
        add(run);
        return run;
    }
    
    /**
     * Add a run into the run list, no changes.
     * @param run
     */
    public void add (Run run) {
        runHash.put(getRunKey(run), run);
        if (saveToDisk) {
            writeToDisk();
        }
    }
    

    /**
     * Get a run from the list.
     */
    private Run get(String key) {
        return runHash.get(key);
    }

    public Run get(ElementId id) {
        return runHash.get(id.toString());
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
     */
    public boolean delete(Run run) {

        Run fetchedRun = get(getRunKey(run));
        if (fetchedRun != null) {
            fetchedRun.setDeleted(true);
            writeToDisk();
            return true;
        }
        return false;

    }

    /**
     * Remove all items from list.
     */
    public void clear() {
        runHash = new Hashtable<String, Run>(200);
        writeToDisk();

    }

    /**
     * Update the run with a new run state.
     * 
     * @param run
     * @param newState
     */
    public void updateRun(Run run, RunStates newState) {
        Run theRun = runHash.get(getRunKey(run));
        theRun.getElementId().incrementVersionNumber();
        theRun.setStatus(newState);
        writeToDisk();

    }

    /**
     * Update run, increment version number.
     * @param run
     */
    public void updateRun(Run run) {
        run.getElementId().incrementVersionNumber();
        runHash.put(getRunKey(run), run);
        writeToDisk();
    }

    /**
     * Update run, increment version number add judgement.
     * @param run
     */
    public void updateRun(Run run, JudgementRecord judgement) {
        Run theRun = runHash.get(getRunKey(run));
        theRun.getElementId().incrementVersionNumber();
        theRun.setStatus(RunStates.JUDGED);
        theRun.addJudgement(judgement);
        writeToDisk();
    }

    public Enumeration getRunList() {
        return runHash.elements();
    }

    private String getFileName() {
        return getDirectoryName() + File.separator + "runlist.dat";
    }

    /**
     * Write the run data to disk.
     * 
     * @throws IOException
     * 
     */
    private boolean writeToDisk() {
        if (!isSaveToDisk()) {
            return false;
        }

        try {
            return Utilities.writeObjectToFile(getFileName(), runHash);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Exception writing RunList to disk ");
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    @SuppressWarnings("unchecked")
    public void loadFromDisk(int siteNumber) throws IOException, ClassNotFoundException {
        String filename = getFileName();
        if (Utilities.isFileThere(filename)) {
            runHash = (Hashtable<String, Run>) Utilities.readObjectFromFile(filename);
            nextRunNumber = lastRunNumber(siteNumber) + 1;
        } else {
            // TODO INFO ? No files loaded, log this ?
            StaticLog.info("loadFromDisk:  INFO ? No files loaded, log this ?");
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
        synchronized (runHash) {
            return runHash.size();
        }
    }

    public Run[] getList() {
        if (runHash.size() == 0) {
            return new Run[0];
        }

        return (Run[]) runHash.values().toArray(new Run[size()]);
    }

    public boolean isSaveToDisk() {
        return saveToDisk;
    }

    public void setSaveToDisk(boolean saveToDisk) {
        this.saveToDisk = saveToDisk;
    }

}
