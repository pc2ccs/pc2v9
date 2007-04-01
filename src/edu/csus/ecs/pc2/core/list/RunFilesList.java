package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.Serializable;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;

/**
 * Access to submitted files (@link edu.csus.ecs.pc2.core.RunFiles}.
 * 
 * This stores the run files to disk.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunFilesList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -15984887352160586L;

    public static final String SVN_ID = "$Id$";

    /**
     * Directory where files are written
     */
    private String dirname = "db";

    private RunFilesList(String dirname) {
        this.dirname = dirname;
        Utilities.insureDir(dirname);
    }

    public RunFilesList(int siteNumber) {
        this("db." + siteNumber);
    }
    
    public RunFilesList() {
        this("db");
    }

    private String getDirectoryName() {
        return dirname;
    }

    private String getFileName(int siteNumber, int runNumber) {
        return getDirectoryName() + File.separator + "s" + siteNumber + "r"
                + runNumber + ".files";
    }

    private String getFileName(Run run) {
        return getFileName(run.getSiteNumber(), run.getNumber());
    }

    public RunFiles add(Run run, RunFiles runFiles) {
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
    }

    private RunFiles getRunFiles(int siteNumber, int runNumber) {
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
    }

    /**
     *
     * @param run
     *            Run
     * @return RunFiles submitted files by team for run
     */
    public RunFiles getRunFiles(Run run) {
        return getRunFiles(run.getSiteNumber(), run.getNumber());
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

}
