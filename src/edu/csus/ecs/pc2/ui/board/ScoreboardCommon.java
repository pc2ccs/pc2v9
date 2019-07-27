/**
 * 
 */
package edu.csus.ecs.pc2.ui.board;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.TransformerConfigurationException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;

/**
 * @author ICPC
 *
 */
public class ScoreboardCommon {

    public void generateOutput(String xmlString, String xslDir, String outputDir, Log log) {
        // FUTUREWORK move to to a common location (currently in both Module and View)
        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            log.warning("xslDir is not a directory");
            return;
        }
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            log.warning("Could not create " + outputDirFile.getAbsolutePath() + ", defaulting to current directory");
            outputDir = ".";
            outputDirFile = new File(outputDir);
        }
        if (!outputDirFile.isDirectory()) {
            log.warning(outputDir + " is not a directory.");
            return;
        } else {
            log.fine("Sending output to " + outputDirFile.getAbsolutePath());
        }
        // TODO consider changing this to use a filenameFilter
        String[] inputFiles = inputDir.list();
        XSLTransformer transformer = new XSLTransformer();
        for (int i = 0; i < inputFiles.length; i++) {
            String xslFilename = inputFiles[i];
            if (xslFilename.endsWith(".xsl")) {
                String outputFilename = xslFilename.substring(0, xslFilename.length() - 4) + ".html";
                try {
                    File output = File.createTempFile("__t", ".htm", outputDirFile);
                    FileOutputStream outputStream = new FileOutputStream(output);
                    transformer.transform(xslDir + File.separator + xslFilename, new ByteArrayInputStream(xmlString.getBytes()), outputStream);
                    outputStream.close();
                    if (output.length() > 0) {
                        File outputFile = new File(outputDir + File.separator + outputFilename);
                        if (xslFilename.equals("pc2export.xsl")) {
                            // change that, we want the pc2export written as a
                            // .dat in the cwd
                            outputFile = new File("pc2export.dat");
                        }
                        // dump json and tsv and csv files in the html directory
                        if (xslFilename.endsWith(".json.xsl") || xslFilename.endsWith(".tsv.xsl") || xslFilename.endsWith(".csv.xsl") || xslFilename.endsWith(".php.xsl")) {
                            outputFile = new File(outputDir + File.separator + xslFilename.substring(0, xslFilename.length() - 4));
                        }
                        // behaviour of renameTo is platform specific, try the
                        // possibly atomic 1st
                        if (!output.renameTo(outputFile)) {
                            // otherwise fallback to the delete then rename
                            outputFile.delete();
                            if (!output.renameTo(outputFile)) {
                                log.warning("Could not create " + outputFile.getCanonicalPath());
                            } else {
                                log.finest("rename2 to " + outputFile.getCanonicalPath() + " succeeded.");
                            }
                        } else {
                            log.finest("rename to " + outputFile.getCanonicalPath() + " succeeded.");
                        }
                    } else {
                        // 0 length file
                        log.warning("output from tranformation " + xslFilename + " was empty");
                        output.delete();
                    }
                    output = null;
                } catch (IOException e) {
                    // TODO re-visit this log message
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                } catch (TransformerConfigurationException e) {
                    // unfortunately this prints the details to stdout (or maybe
                    // stderr)
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                } catch (Exception e) {
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                }
            }
        }
    }

    public void generateResults(IInternalContest contest, IInternalController controller, String xmlString, String xslDir, Log log) {
        try {
            File output = File.createTempFile("__t", ".tmp", new File("."));
            FileOutputStream outputXML = new FileOutputStream(output);
            outputXML.write(xmlString.getBytes());
            outputXML.close();
            if (output.length() > 0) {
                File outputFile = new File("results.xml");
                // behaviour of renameTo is platform specific, try the possibly
                // atomic 1st
                if (!output.renameTo(outputFile)) {
                    // otherwise fallback to the delete then rename
                    outputFile.delete();
                    if (!output.renameTo(outputFile)) {
                        log.warning("Could not create " + outputFile.getCanonicalPath());
                    }
                }
            } else {
                // 0 length file
                log.warning("New results.xml is empty, not updating");
                output.delete();
            }
            output = null;
        } catch (FileNotFoundException e1) {
            log.log(Log.WARNING, "Could not write to " + "results.xml", e1);
        } catch (IOException e) {
            log.log(Log.WARNING, "Problem writing to " + "results.xml", e);
        }
        FinalizeData finalizeData = contest.getFinalizeData();
        String outputDir = ".";
        if (finalizeData != null && finalizeData.isCertified()) {
            File outputResultsDirFile = new File("results");
            if (!outputResultsDirFile.exists() && !outputResultsDirFile.mkdirs()) {
                log.warning("Could not create " + outputResultsDirFile.getAbsolutePath() + ", defaulting to current directory");
                outputDir = ".";
                outputResultsDirFile = new File(outputDir);
            }
            if (!outputResultsDirFile.isDirectory()) {
                log.warning(outputDir + " is not a directory.");
                return;
            } else {
                log.fine("Sending results output to " + outputResultsDirFile.getAbsolutePath());
            }
            try {
                ResultsFile resultsFile = new ResultsFile();
                String[] createTSVFileLines = resultsFile.createTSVFileLines(contest);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + File.separator + "results.tsv");
                for (int i = 0; i < createTSVFileLines.length; i++) {
                    outputFile.write(createTSVFileLines[i] + System.getProperty("line.separator"));
                }
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating results.tsv", e);
            }
            try {
                ScoreboardFile scoreboardFile = new ScoreboardFile();
                String[] createTSVFileLines = scoreboardFile.createTSVFileLines(contest);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + File.separator + "scoreboard.tsv");
                for (int i = 0; i < createTSVFileLines.length; i++) {
                    outputFile.write(createTSVFileLines[i] + System.getProperty("line.separator"));
                }
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating scoreboard.tsv", e);
            }
            StandingsJSON2016 standingsJson = new StandingsJSON2016();
            try {
                String createJSON = standingsJson.createJSON(contest, controller);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + File.separator + "scoreboard.json");
                outputFile.write(createJSON);
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating scoreboard.json", e);
            }
        }
    }

    protected Properties getScoringProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String[] keys = (String[]) defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (!properties.containsKey(key)) {
                properties.put(key, defProperties.get(key));
            }
        }

        return properties;
    }

}
