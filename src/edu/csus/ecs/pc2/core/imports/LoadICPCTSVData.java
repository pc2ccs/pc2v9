package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.imports.ccs.ICPCCSVLoader;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Load ICPC TSV files into contest.
 * 
 * Read input .tsv files, validate then if valid load into contest.
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

    @SuppressWarnings("unused")
    private IInternalContest contest;

    private IInternalController controller;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
    }

    public boolean loadFiles(String filename) throws Exception {

        if (checkFiles(filename)) {

            Group[] groups = ICPCCSVLoader.loadGroups(groupsFilename);
            Account[] accounts = ICPCCSVLoader.loadAccounts(teamsFilename);

            String nl = System.getProperty("line.separator");

            String message = "Are you sure you want to add " + nl + accounts.length + " accounts and " + nl + groups.length + " groups?";

            int result = FrameUtilities.yesNoCancelDialog(null, message, "Load TSV files");

            if (result == JOptionPane.YES_OPTION) {

                for (Group group : groups) {
                    getController().addNewGroup(group);
                }

                getController().addNewAccounts(accounts);
                
                // TODO 9.3 assign groups to sites or try...

                return true;
            } else {
                return false;
            }

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
            groupsFilename = groupsFilename.replaceFirst(TEAMS_FILENAME, GROUPS_FILENAME);
        } else if (filename.endsWith(GROUPS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            teamsFilename = teamsFilename.replaceFirst(GROUPS_FILENAME, TEAMS_FILENAME);
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

    public IInternalController getController() {
        return controller;
    }

}
