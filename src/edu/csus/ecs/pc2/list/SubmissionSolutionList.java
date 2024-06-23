// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * List of judge's solutions
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class SubmissionSolutionList extends ArrayList<SubmissionSampleLocation> {

    /**
     *
     */
    private static final long serialVersionUID = 6231245379952020474L;

    private Hashtable<String, String> submissionDirectoryToAcronym = new Hashtable<String, String>();

    public SubmissionSolutionList() {
        super();
        createSubmissionDirectoryMap();
    }

    public SubmissionSolutionList(IInternalContest mycontest, String cdpPath) {
        super();
        createSubmissionDirectoryMap();
        loadCDPSampleSubmissionList(mycontest, cdpPath);
    }

    private void createSubmissionDirectoryMap() {
        // Create quick access to look up acronym for folder
        for(String [] subFolderRow : SubmissionSampleLocation.CLICS_SUBMISSION_TO_ACRONYM) {
            submissionDirectoryToAcronym.put(subFolderRow[0], subFolderRow[1]);
        }
    }

    /**
     * get all directories and child directories (recurses).
     *
     * @param directory
     * @return list of directory names
     */
    // TODO REFACTOR move to FileUtilities.getAllDirectoryEntries
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
     * Lookup the CLICS Big5 acronum for a judge's submission folder
     *
     * @param dir the folder name
     * @return the acronym, or null if the folder is non-standard
     */
    public String getAcronymForSubmissionDirectory(String dir) {
        if(submissionDirectoryToAcronym.containsKey(dir)) {
            return submissionDirectoryToAcronym.get(dir);
        }
        return null;
    }

    /**
     * Load Judge's samples judgement types
     * @param cdpPath
     * @throws IOException
     */
    // TODO NOW use FileUtilities.getAllDirectoryEntries
    private void loadCDPSampleSubmissionList(IInternalContest contest, String cdpPath)  {

        List<String> types = ListUtilities.getAllCDPSubmissionTypes(contest, cdpPath);
        for(String type : types) {
            super.add(new SubmissionSampleLocation("", type, getAcronymForSubmissionDirectory(type)));
        }
    }
}
