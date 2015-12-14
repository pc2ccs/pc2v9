package edu.csus.ecs.pc2.core.report;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Create HTML files
 * 
 * @author $Author$
 * @version $Id$
 */
// SOMEDAY replace generateOutput in ScoreboardView rather with these methods
public class ScoreboardPlugin implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5834251267967257702L;

    private String xslDir;

    private IInternalContest contest = null;

    private IInternalController controller = null;

    private Log log = null;

    /**
     * Write all standings HTML to output directory.
     * 
     * @param outputDir
     * @throws IllegalContestState
     */
    public void writeHTML(String outputDir) throws IllegalContestState {

        DefaultScoringAlgorithm algorithm = new DefaultScoringAlgorithm();
        String standingsXML = algorithm.getStandings(contest, algorithm.getProperties(), log);
        writeHTML(standingsXML, log, outputDir);
    }

    /**
     * Write all standings to output directory.
     * 
     * @param xmlString
     * @param log
     * @param outputDir
     */
    public void writeHTML(String xmlString, Log log, String outputDir) {

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
        try {
            File output = File.createTempFile("__t", ".tmp", new File("."));
            FileOutputStream outputXML = new FileOutputStream(output);
            outputXML.write(xmlString.getBytes());
            outputXML.close();
            if (output.length() > 0) {
                File outputFile = new File("results.xml");
                // behaviour of renameTo is platform specific, try the possibly atomic 1st
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
                            // change that, we want the pc2export written as a .dat in the cwd
                            outputFile = new File("pc2export.dat");
                        }
                        // dump json and tsv and csv files in the html directory
                        if (xslFilename.endsWith(".json.xsl") || xslFilename.endsWith(".tsv.xsl") || xslFilename.endsWith(".csv.xsl") || xslFilename.endsWith(".php.xsl")) {
                            outputFile = new File(outputDir + File.separator + xslFilename.substring(0, xslFilename.length() - 4));
                        }
                        // behaviour of renameTo is platform specific, try the possibly atomic 1st
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
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                } catch (TransformerConfigurationException e) {
                    // unfortunately this prints the details to stdout (or maybe stderr)
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                } catch (Exception e) {
                    log.log(Log.WARNING, "Trouble transforming " + xslFilename, e);
                }
            }
        }
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

        xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }

    }

    @Override
    public String getPluginTitle() {
        return "Scorboard HTML Plugin";
    }

}
