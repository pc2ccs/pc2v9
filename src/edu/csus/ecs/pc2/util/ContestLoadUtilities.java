package edu.csus.ecs.pc2.util;

import java.io.File;

import edu.csus.ecs.pc2.core.imports.LoadICPCTSVData;
import edu.csus.ecs.pc2.core.model.IInternalContest;

public class ContestLoadUtilities {

    /**
     * Load groups.tsv and teams.tsv.
     * 
     * @param contest
     * @param cdpConfigDirectory
     * @return 
     * @throws Exception
     */
    public static boolean loadCCSTSVFiles(IInternalContest contest, File cdpConfigDirectory) throws Exception {

        boolean loaded = false;

        String teamsTSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.TEAMS_FILENAME;
        String groupsTSVFile = cdpConfigDirectory.getAbsolutePath() + File.separator + LoadICPCTSVData.GROUPS_FILENAME;

        // only load if both tsv files are present.

        if (new File(teamsTSVFile).isFile() && new File(groupsTSVFile).isFile()) {
            LoadICPCTSVData loadTSVData = new LoadICPCTSVData();
            loadTSVData.setContestAndController(contest, null);
            loaded = loadTSVData.loadFiles(teamsTSVFile, false, false);
        }

        return loaded;
    }

}
