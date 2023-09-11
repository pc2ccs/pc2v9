package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAwardUtilities;
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
     */
    public static void writeResultsFiles(IInternalContest contest, String outputDirectory) {

        String resultsFilename = outputDirectory + File.separator + ResultsFile.RESULTS_FILENAME;

        String scoreboardJsonFilename = outputDirectory + File.separator + Constants.SCOREBOARD_JSON_FILENAME;

        String awardsFileName = outputDirectory + File.separator + Constants.AWARDS_JSON_FILENAME;

        try {
            ResultsFile resultsFile = new ResultsFile();
            String[] resultTSVLines = resultsFile.createTSVFileLines(contest);
            FileUtilities.writeFileContents(resultsFilename, resultTSVLines);

        } catch (Exception e) {
            e.printStackTrace(System.out); // TODO 760 add context to exception message
//            throw new RuntimeException("Problem generating " + resultsFilename, e.getCause());
            throw ExecuteUtilities.rethrow(e);
        }

        try {
            ScoreboardJson scoreboardJson = new ScoreboardJson();
            String json = scoreboardJson.createJSON(contest);
            String[] scoreboardJsonLines = { json };
            FileUtilities.writeFileContents(scoreboardJsonFilename, scoreboardJsonLines);

        } catch (Exception e) {
            e.printStackTrace(System.out); // TODO 760 add context to exception message
//            throw new RuntimeException("Problem generating " + scoreboardJsonFilename, e.getCause());
            throw ExecuteUtilities.rethrow(e);
        }

        try {
            List<CLICSAward> awards = CLICSAwardUtilities.createAwardsList(contest);
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(awardsFileName, false), true);
            CLICSAwardUtilities.writeAwardsJSONFile(printWriter, awards);
            printWriter.close();

        } catch (Exception e) {
            e.printStackTrace(System.out); // TODO 760 add context to exception message
//            throw new RuntimeException("Problem generating " + awardsFileName, e.getCause());
            throw ExecuteUtilities.rethrow(e);
        }

    }

}
