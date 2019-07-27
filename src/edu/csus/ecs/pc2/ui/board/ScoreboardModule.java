package edu.csus.ecs.pc2.ui.board;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
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

    private DefaultScoringAlgorithm algo = new DefaultScoringAlgorithm();
    
    /*
     * We set setObeyFrozen = true on this one.
     */
    private DefaultScoringAlgorithm algoFrozen = new DefaultScoringAlgorithm();

    private ScoreboardCommon scoreboardCommon = new ScoreboardCommon();

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
        algoFrozen.setObeyFreeze(true);
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
            Properties scoringProperties = scoreboardCommon.getScoringProperties(getContest().getContestInformation().getScoringProperties());
            String saXML = algo.getStandings(getContest(), scoringProperties, log);
            generateOutput(saXML);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception generating scoreboard output " + e.getMessage(), e);
        }
    }

    private void generateOutput(String xmlString) {
        String outputDir = contest.getContestInformation().getScoringProperties().getProperty(DefaultScoringAlgorithm.JUDGE_OUTPUT_DIR, "html");
        scoreboardCommon.generateOutput(xmlString, xslDir, outputDir, log);
        scoreboardCommon.generateResults(contest, controller, xmlString, xslDir, log);
        try {
            Properties scoringProperties = scoreboardCommon.getScoringProperties(getContest().getContestInformation().getScoringProperties());
            String frozenOutputDir = scoringProperties.getProperty(DefaultScoringAlgorithm.PUBLIC_OUTPUT_DIR);
            if (frozenOutputDir != null && frozenOutputDir.trim().length() > 0 && !frozenOutputDir.equals(outputDir)) {
                String frozenXML = algoFrozen.getStandings(getContest(), scoringProperties, log);
                scoreboardCommon.generateOutput(frozenXML, xslDir, frozenOutputDir, log);
            }
        } catch (Exception e) {
            log.warning("Exception generating frozen html");
        }
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
     * 
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
