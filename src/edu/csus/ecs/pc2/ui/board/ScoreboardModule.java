package edu.csus.ecs.pc2.ui.board;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.xml.transform.TransformerConfigurationException;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A non-GUI Scoreboard Module.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ScoreboardModule implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5352802558674673586L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private String xslDir;

    private String outputDir = "html";

    private DefaultScoringAlgorithm algo = new DefaultScoringAlgorithm();

    public String getPluginTitle() {
        return "Scoreboard (non-GUI)";
    }

    public ScoreboardModule() {
        VersionInfo versionInfo = new VersionInfo();
        System.out.println(versionInfo.getSystemName());
        System.out.println(versionInfo.getSystemVersionInfo());
        System.out.println("Build " + versionInfo.getBuildNumber());
        System.out.println("Date: " + getL10nDateTime());
        System.out.println("Working directory is " + Utilities.getCurrentDirectory());
        System.out.println();

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        log = controller.getLog();

        if (Utilities.isDebugMode()) {
            if (inController instanceof InternalController) {
                // add console logger
                InternalController cont = (InternalController) inController;
                cont.addConsoleLogging();
                log.info("--debug, added appender to stdout");
            }
        }

        VersionInfo versionInfo = new VersionInfo();
        log.info(versionInfo.getSystemName());
        log.info(versionInfo.getSystemVersionInfo());
        log.info("Build " + versionInfo.getBuildNumber());
        log.info("Date: " + getL10nDateTime());
        log.info("Working directory is " + Utilities.getCurrentDirectory());
        log.info(" Logged in as " + getContest().getClientId());

        startScoreboard();

        getContest().addContestTimeListener(new ContestTimeListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImlementation());

    }

    private void startScoreboard() {

        xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }

        log = controller.getLog();
        log.info("Using XSL from directory " + xslDir);

        generateOutput();
    }

    private void generateOutput() {

        try {
            log.info(" generateOutput() - create HTML ");
            Properties scoringProperties = getScoringProperties();
            String saXML = algo.getStandings(getContest(), scoringProperties, log);
            generateOutput(saXML);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception generating scoreboard output " + e.getMessage(), e);
        }
    }

    private void generateOutput(String xmlString) {
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
					transformer.transform(xslDir + File.separator + xslFilename,
							new ByteArrayInputStream(xmlString.getBytes()), outputStream);
					outputStream.close();
					if (output.length() > 0) {
						File outputFile = new File(outputDir + File.separator + outputFilename);
						if (xslFilename.equals("pc2export.xsl")) {
							// change that, we want the pc2export written as a
							// .dat in the cwd
							outputFile = new File("pc2export.dat");
						}
						// dump json and tsv and csv files in the html directory
						if (xslFilename.endsWith(".json.xsl") || xslFilename.endsWith(".tsv.xsl")
								|| xslFilename.endsWith(".csv.xsl") || xslFilename.endsWith(".php.xsl")) {
							outputFile = new File(
									outputDir + File.separator + xslFilename.substring(0, xslFilename.length() - 4));
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
		FinalizeData finalizeData = contest.getFinalizeData();
		if (finalizeData != null && finalizeData.isCertified()) {
			File outputResultsDirFile = new File("results");
			if (!outputResultsDirFile.exists() && !outputResultsDirFile.mkdirs()) {
				log.warning("Could not create " + outputResultsDirFile.getAbsolutePath()
						+ ", defaulting to current directory");
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

    protected Properties getScoringProperties() {

        Properties properties = getContest().getContestInformation().getScoringProperties();
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

    protected boolean isThisSite(int siteNumber) {
        return contest.getSiteNumber() == siteNumber;
    }

    /**
     * Problem listener
     * 
     * @author ICPC
     *
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            generateOutput();
        }

        public void problemChanged(ProblemEvent event) {
            generateOutput();
        }

        public void problemRemoved(ProblemEvent event) {
            generateOutput();
        }

        public void problemRefreshAll(ProblemEvent event) {
            generateOutput();
        }

    }

    /**
     * Account Listener
     * 
     * @author ICPC
     *
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            generateOutput();
        }

        public void accountModified(AccountEvent event) {
            generateOutput();
        }

        public void accountsAdded(AccountEvent accountEvent) {
            generateOutput();
        }

        public void accountsModified(AccountEvent accountEvent) {
            generateOutput();
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            generateOutput();
        }
    }

    /**
     * ContestTime listener
     * 
     * @author ICPC
     *
     */
    class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            ContestTime contestTime = event.getContestTime();
            if (isThisSite(contestTime.getSiteNumber())) {
                generateOutput();
            }
        }

        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        /**
         * This method exists to support differentiation between manual and automatic starts, in the event this is desired in the future. Currently it just delegates the handling to the
         * contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            generateOutput();
        }

        public void refreshRuns(RunEvent event) {
            generateOutput();
        }

        public void runChanged(RunEvent event) {
            generateOutput();
        }

        public void runRemoved(RunEvent event) {
            generateOutput();
        }
    }

    /**
     * BalloonSettings listener
     * 
     * @author ICPC
     *
     */
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            generateOutput();
        }

        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            generateOutput();
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            generateOutput();
        }

        public void balloonSettingsRefreshAll(BalloonSettingsEvent balloonSettingsEvent) {
            generateOutput();
        }
    }

    /**
     * a ContestInformation Listener
     * 
     * @author ICPC
     *
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            generateOutput();

        }

        public void contestInformationChanged(ContestInformationEvent event) {
            generateOutput();

        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignored
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            generateOutput();
        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            generateOutput();
        }

    }

    protected String getL10nDateTime() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return (dateFormatter.format(new Date()));
    }

    // private void showMessage(final String string) {
    // getLog().info(string);
    // }

    public Log getLog() {
        return log;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

    /**
     * a Language listener
     * @author ICPC
     *
     */
    class LanguageListenerImlementation implements ILanguageListener {

        @Override
        public void languageAdded(LanguageEvent event) {
            generateOutput();
        }

        @Override
        public void languageChanged(LanguageEvent event) {
            generateOutput();
        }

        @Override
        public void languageRemoved(LanguageEvent event) {
            generateOutput();

        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            generateOutput();

        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            generateOutput();

        }

        @Override
        public void languageRefreshAll(LanguageEvent event) {
            // ignored
        }
    }
}
