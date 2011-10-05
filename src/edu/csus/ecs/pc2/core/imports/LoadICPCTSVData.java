package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.io.FileNotFoundException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Load ICPC TSV files into contest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadICPCTSVData implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1611218320856176033L;

    public static final String TEAMS_FILENAME = "teams.tsv";

    public static final String GROUPS_FILENAME = "groups.tsv";

    private String teamsFilename = "";

    private String groupsFilename = "";

    private IInternalContest contest;

    private IInternalController controller;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        // TODO CODE THIS
        contest = inContest;
        controller = inController;
    }

    public boolean loadFiles(String filename) throws Exception {

        if (checkFiles(filename)) {
            // TODO codethis
            System.out.println("Should code load files");
            return true; // TODO replace this with the load files code

        } else {
            return false;
        }

    }

    protected boolean checkFiles(String filename) throws Exception {

        File file = new File(filename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        if (filename.endsWith(TEAMS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            groupsFilename.replaceFirst(TEAMS_FILENAME, GROUPS_FILENAME);
        } else if (filename.endsWith(GROUPS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            teamsFilename.replaceFirst(GROUPS_FILENAME, TEAMS_FILENAME);
        } else {
            throw new Exception("Must select either " + TEAMS_FILENAME + " or " + GROUPS_FILENAME);
        }

        file = new File(teamsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + teamsFilename);
        }

        file = new File(groupsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + groupsFilename);
        }

        return true;

    }

    public String getPluginTitle() {
        return "Load TSV Files";
    }

}
