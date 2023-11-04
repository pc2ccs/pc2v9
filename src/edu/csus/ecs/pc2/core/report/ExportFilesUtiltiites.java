// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAwardUtilities;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.services.core.ScoreboardJson;

public class ExportFilesUtiltiites {

    /**
     * Write contest results files to directory.
     * 
     * Creates contest result files
     * <li>{@link ResultsFile.RESULTS_FILENAME}
     * <li>{@link Constants.SCOREBOARD_JSON_FILENAME}
     * <li>{@link Constants.AWARDS_JSON_FILENAME}
     * 
     * @param contest
     * @param outputDirectory
     *            directory to write reports to.
     * @return list of created files
     */
    public static String[] writeResultsFiles(IInternalContest contest, String outputDirectory) {

        List<String> filesCreated = new ArrayList<String>();

        String resultsFilename = outputDirectory + File.separator + ResultsFile.RESULTS_FILENAME;

        String scoreboardJsonFilename = outputDirectory + File.separator + Constants.SCOREBOARD_JSON_FILENAME;

        String awardsFileName = outputDirectory + File.separator + Constants.AWARDS_JSON_FILENAME;

        try {
            ResultsFile resultsFile = new ResultsFile();
            String[] resultTSVLines = resultsFile.createTSVFileLines(contest);
            FileUtilities.writeFileContents(resultsFilename, resultTSVLines);

            filesCreated.add(resultsFilename);

        } catch (Exception e) {
            StaticLog.getLog().log(Level.WARNING, "Problem writing results file", e);
            Utilities.printStackTrace(System.out, e);
            throw ExecuteUtilities.rethrow(e);
        }

        try {
            ScoreboardJson scoreboardJson = new ScoreboardJson();
            String json = scoreboardJson.createJSON(contest);
            String[] scoreboardJsonLines = { json };
            FileUtilities.writeFileContents(scoreboardJsonFilename, scoreboardJsonLines);

            filesCreated.add(scoreboardJsonFilename);

        } catch (Exception e) {
            StaticLog.getLog().log(Level.WARNING, "Problem writing scorebord file", e);
            Utilities.printStackTrace(System.out, e);
            throw ExecuteUtilities.rethrow(e);
        }

        try {
            List<CLICSAward> awards = CLICSAwardUtilities.createAwardsList(contest);
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(awardsFileName, false), true);
            CLICSAwardUtilities.writeAwardsJSONFile(printWriter, awards);
            printWriter.close();

            filesCreated.add(awardsFileName);

        } catch (Exception e) {
            StaticLog.getLog().log(Level.WARNING, "Problem writing awards file", e);
            Utilities.printStackTrace(System.out, e);

            throw ExecuteUtilities.rethrow(e);
        }

        return (String[]) filesCreated.toArray(new String[filesCreated.size()]);
    }
}
