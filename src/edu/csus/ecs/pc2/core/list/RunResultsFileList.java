package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.Serializable;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;

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
     * If true then writes and reads run files from db directory.
     * This is intended for pc2 server modules.
     * <P>
     * if false then only caches a single RunResultFiles for the last
     * added run files.  This is intended for non-server pc2 modules.
     * 
     * 
     */
    private boolean writeToDisk = false;
    
    private RunResultFiles singleRunResultFiles = null;

    /**
     * Directory where files are written
     */
    private String dirname = "db";

    private RunResultsFileList(String dirname) {
        this.dirname = dirname;
        Utilities.insureDir(dirname);
        writeToDisk = true;
    }

    public RunResultsFileList(int siteNumber) {
        this("db." + siteNumber);
    }
    
    private String getDirectoryName() {
        return dirname;
    }
    
    protected String stripChar (String s, char ch){
        int idx = s.indexOf(ch);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(ch+"");
            while (idx > -1){
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch+"");
            }
            return sb.toString();
        }
        return s;
    }
    private String stripChars (String s) {
        return stripChar(s, ' ');
    }

    private String getFileName(int siteNumber, int runNumber, JudgementRecord judgementRecord) {
        return getDirectoryName() + File.separator + "s" + siteNumber + "r"
                + runNumber + "." + stripChars(judgementRecord.getElementId().toString()) + ".files";
    }

    public String getFileName(Run run, JudgementRecord judgementRecord) {
        return getFileName(run.getSiteNumber(), run.getNumber(), judgementRecord);
    }

    public RunResultFiles add(Run run, JudgementRecord judgementRecord, RunResultFiles runFiles) {
        if (writeToDisk) {
            String filename = getFileName(run, judgementRecord);
            try {
                Utilities.writeObjectToFile(filename, runFiles);
                return runFiles;
            } catch (Exception e) {
                // TODO log could not write object to file.
                System.err.println("Unable to write file " + filename);
                e.printStackTrace();
                return null;
            }
        } else {
            singleRunResultFiles = runFiles;
            return singleRunResultFiles;
        }
    }

    private RunResultFiles getRunResultFiles(int siteNumber, int runNumber, JudgementRecord judgementRecord) {
        if (writeToDisk) {
            String filename = getFileName(siteNumber, runNumber, judgementRecord);
            try {
                Object obj = Utilities.readObjectFromFile(filename);
                return (RunResultFiles) obj;
            } catch (Exception e) {
                // TODO log info - could not read RunResultFiles from disk.
                System.err.println("Unable to read object from file " + filename);
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * return a run results file.
     * @param run
     *            Run
     * @return RunResultFiles files and statistics 
     */
    public RunResultFiles getRunResultFiles(Run run, JudgementRecord judgementRecord) {

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
}
