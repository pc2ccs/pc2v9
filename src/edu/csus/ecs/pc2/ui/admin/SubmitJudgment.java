package edu.csus.ecs.pc2.ui.admin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunComparator;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.CommandLineErrorException;
import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * Command line submit judgment.
 * 
 * Allows an external program, with proper Admin credentials, to submit a Judgment for a run.
 * 
 * Uses the API.
 * 
 * @author pc2@ecs.csus.edu
 */
public class SubmitJudgment {

    private ServerConnection serverConnection = null;

    private String login;

    private String password;

    private IContest contest;

    private IClient submittingUser;

    public static final String[] REQUIRED_OPTIONS_LIST = {
            //
            "--login", "--password", // pc2 login password
            "-u", // team id 
            "-i", "-j", "-F",
    };

    private RunEventListener runliEventListener = new RunEventListener();

    /**
     * Successful run exit code.
     *
     * Using a non-zero exit code because if there is a problem in the JVM
     * or elsewhere a zero exit code could be returned.
     */
    private static final int SUCCESS_EXIT_CODE = 5;

    private static final int FAILURE_EXIT_CODE = 4;

    // TODO move to Constants
    private static final String FILE_OPTION_STRING = "-F";

    //    private static final String NL = System.getProperty("line.separator");

    /**
     * print all missing options if command line error.
     */
    private boolean showAllMissingOptions = true;

    /**
     * --check option.
     * 
     */
    @SuppressWarnings("unused")
    private boolean checkArg = false; // TODO implement

    private boolean debugMode = false;

    /**
     * Run number to be judged.
     */
    private long runId;

    private String judgementAcronym;

    /**
     * Team for run to be judged
     */
    private String teamIdString;

    private int teamId = 0;

    public SubmitJudgment(String[] args) throws CommandLineErrorException {
        loadProgramVariables(args, REQUIRED_OPTIONS_LIST);
    }

    /**
     * Expand shortcut names.
     * 
     * ex. 1 - team1, j2 = judge2, etc.
     * 
     * @param loginName
     */
    private void loginShortcutExpansion(String loginName, String inPassword) {

        ClientId id = InternalController.loginShortcutExpansion(1, loginName);
        if (id != null) {

            login = id.getName();
            password = inPassword;

            if (password == null) {
                password = login;
            }
        }
    }

    /**
     * Load program variables from command line arguments.
     * 
     * @param args
     * @param opts
     * @throws CommandLineErrorException
     */
    private void loadProgramVariables(String[] args, String[] opts) throws CommandLineErrorException {

        ParseArguments arguments = new ParseArguments(args, opts);

        if (args.length == 0) {
            usage();
            System.exit(FAILURE_EXIT_CODE);
        }

        if (arguments.isOptPresent(FILE_OPTION_STRING)) {
            String propertiesFileName = arguments.getOptValue(FILE_OPTION_STRING);

            if (propertiesFileName == null) {
                arguments.dumpArgs(System.err);
                fatalError("No file specified after -F option ");
            }

            if (!(new File(propertiesFileName).exists())) {
                fatalError(propertiesFileName + " does not exist (pwd: " + Utilities.getCurrentDirectory() + ")", null);
            }

            try {
                arguments.overRideOptions(propertiesFileName);
            } catch (IOException e) {
                fatalError("Unable to read file " + propertiesFileName, e);
            }
        }

        debugMode = arguments.isOptPresent("--debug");

        if (debugMode) {
            arguments.dumpArgs(System.err);
        }

        //        timeStamp = 0;
        checkArg = arguments.isOptPresent("--check");

        // --login loginname - user login 
        String cmdLineLogin = arguments.getOptValue("--login");
        if (cmdLineLogin == null) {
            throw new CommandLineErrorException("Missing login");
        }

        // --password password - user password
        String cmdLinePassword = arguments.getOptValue("--password");

        loginShortcutExpansion(cmdLineLogin, cmdLinePassword);

        // -i runid       -  run id for submission

        String runIdString = arguments.getOptValue("-i");
        try {
            if (arguments.isOptPresent("-i")) {
                runId = Long.parseLong(runIdString);
            }
        } catch (Exception e) {
            throw new CommandLineErrorException("Invalid number after -i '" + runIdString + "'", e);
        }

        // -j acro         - judgement for run, (judgement acronym)

        if (arguments.isOptPresent("-j")) {
            judgementAcronym = arguments.getOptValue("-j");
        }

        // -u team_id      - team id for the run
        if (arguments.isOptPresent("-u")) {
            teamIdString = arguments.getOptValue("-u");

            teamId = toInt(teamIdString, 0);
            if (teamId < 1) {
                throw new CommandLineErrorException("Invalid team number after -t '" + teamIdString + "'");
            }
        }

        if (password == null) {
            password = login;
        }
    }

    /**
     * Convert to int.
     * 
     * @param string integer in a string
     * @param defaultValue
     * @return defaultValue if parse error.
     */
    private int toInt(String string, int defaultValue) {

        try {
            return Integer.parseInt(string.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected boolean hasAnyCCSArguments(String[] args, String[] requiredOpts) {

        ParseArguments parseArguments = new ParseArguments(args, requiredOpts);

        for (String s : args) {
            if (parseArguments.isRequiredOptPresent(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scans command line, if missing options returns count of missing arguments.
     * 
     * If any one of the required opts is present and any other option is
     * missing then will return the number of missing required options
     * and values for those options.
     * 
     * @param args
     * @param requiredOpts
     * @param requiredOptions 
     * @return if any option is present, and any other is not present return count.
     */
    protected int numberMissingArguments(String[] args, String[] allOptions, String[] requiredOptions) {

        int count = 0;

        ParseArguments parseArguments = new ParseArguments(args, allOptions);

        for (String s : requiredOptions) {
            if (!parseArguments.isOptPresent(s)) {
                count++;
            } else if (!parseArguments.optHasValue(s)) {
                count++;
            }
        }
        return count;
    }

    @SuppressWarnings("unused")
    private void printMissingArguments(String[] args, String[] requiredOpts) {

        ParseArguments parseArguments = new ParseArguments(args, requiredOpts);

        for (String s : requiredOpts) {
            if (!parseArguments.isOptPresent(s)) {
                System.err.println("Missing required command line parameter " + s);
            } else if (!parseArguments.optHasValue(s)) {
                System.err.println("Missing required value after command line parameter " + s);
            }
        }
    }

    protected String findLanguageName(String string) {

        for (ILanguage language : contest.getLanguages()) {
            if (language.getName().equalsIgnoreCase(string)) {
                return language.getName();
            } else if (language.getName().indexOf(string) > -1) {
                return language.getName();
            }
        }
        return string;
    }

    protected String getProblemNameFromFilename(String filename) {

        String baseName = Utilities.basename(filename);

        // Strip extension
        int lastIndex = baseName.lastIndexOf('.');
        if (lastIndex > 1) {
            baseName = baseName.substring(0, lastIndex - 1);
        }

        IProblem problem = matchProblem(baseName);
        if (problem != null) {
            return problem.getName();
        } else {
            return baseName;
        }
    }

    private static void usage() {
        String[] usageMessage = { //
        "", //
                "Usage SubmitJudgement [-F propfile] --login loginname --password password -i runid -j judgement_acronym -u team_id ", //
                "", //
                "Submit judgement (acronym) for run.", //
                "", //
                "--help         - this listing", //
                "", //
                "--login loginname - user login ", //
                "", //
                "--password password - user password", //
                "", //
                "-i runid        - run id for run to be updated ", //
                "", //
                "-u team_id      - team id for the run", //
                "", //
                "-j acro         - judgement for run, (judgement acronym)", //
                "", //
                "-F propfile     - load options from propfile", //
                "", //
                "On success exit code will be " + SUCCESS_EXIT_CODE, //
                "Any other exit code is an error.", //
        };

        for (String s : usageMessage) {
            System.out.println(s);
        }
    }

    private IJudgement findJudgement(IContest contest, String ja) {
        IJudgement[] judgements = contest.getJudgements();
        for (IJudgement iJudgement : judgements) {
            if (iJudgement.getAcronym().equals(ja)) {
                return iJudgement;
            }
        }
        return null;
    }

    private IRun findRun(IContest contest, long runId) {
        IRun[] runs = contest.getRuns();
        for (IRun iRun : runs) {
            if (iRun.getNumber() == runId)
            {
                return iRun;
            }
        }

        return null;
    }

    /**
     * Submit a run.
     * @param args 
     * @throws CommandLineErrorException 
     */
    public void submitJudgement(String[] args) throws CommandLineErrorException {

        boolean success = false;

        try {
            checkRequiredParams();
        } catch (Exception e) {
            fatalError("Error on command line: " + e.getMessage());
        }

        try {

            serverConnection = new ServerConnection();

            contest = serverConnection.login(login, password);
            contest.addRunListener(runliEventListener);

            System.out.println("For: " + contest.getMyClient().getDisplayName() + " (" + contest.getMyClient().getLoginName() + ")");
            System.out.println();

            try {

                IRun run = findRun(contest, runId);
                if (run == null) {
                    throw new Exception("No run " + runId + " exists in contest.   No such run.");
                }

                int accountNumber = run.getTeam().getAccountNumber();
                if (run.getTeam().getAccountNumber() != teamId) {
                    throw new Exception("Team number does not match run, expected team " + accountNumber + " got '" + teamIdString + "'");
                }

                IJudgement judgement = findJudgement(contest, judgementAcronym);

                if (judgement == null) {
                    throw new Exception("No judgement acronym found in contest for '" + judgementAcronym + "'");
                }

                serverConnection.submitRunJudgement(run, judgement);

                waitForRunJudgementConfirmation(runliEventListener, 2);

                IRun newRun = runliEventListener.getRun();

                if (newRun != null) {
                    // got a run
                    success = true;

                    System.out.println("Run " + newRun.getNumber() + " judgement updated now is '" + newRun.getJudgementName() //
                            + "', problem " + newRun.getProblem().getName() + //
                            ", for team: " + newRun.getTeam().getDisplayName() + //
                            " (" + newRun.getTeam().getLoginName() + ")");
                }
                // no else

                serverConnection.logoff();
            } catch (Exception e) {
                System.err.println("Run " + runId + " was not changed.  " + e.getMessage());
                if (debugMode) {
                    e.printStackTrace();
                }
            }

        } catch (LoginFailureException e) {
            System.out.println("Unable to login: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
        }

        if (success) {
            System.exit(SUCCESS_EXIT_CODE);
        } else {
            System.exit(FAILURE_EXIT_CODE);
        }
    }

    /**
     * Check that they have supplied required parameters.
     * 
     * @throws LoginFailureException
     */
    private void checkRequiredParams() throws LoginFailureException {

        if (login == null) {
            throw new LoginFailureException("No login specified");
        }
        if (password == null) {
            throw new LoginFailureException("No password specified");
        }

        if (runId == 0) {
            throw new LoginFailureException("No run id specified");
        }

        if (judgementAcronym == null || judgementAcronym.length() == 0) {
            throw new LoginFailureException("No judgement acronym specified");
        }

        if (teamId == 0) {
            throw new LoginFailureException("No team id specified");
        }
    }

    /**
     * Waits for run judgement confirmation.
     * 
     * @param listener
     * @param seconds seconds to wait for response
     * @throws Exception
     */
    private void waitForRunJudgementConfirmation(RunEventListener listener, int seconds) throws Exception {

        boolean done = false;

        long waittime = seconds * 1000;

        long startTime = new Date().getTime();

        long timeLimit = startTime + waittime;

        while (!done) {

            if (listener.getRun() != null) {
                done = true;
            }

            if (!done && (new Date().getTime() > timeLimit)) {
                break;
            }
            System.out.print("");
        }

        long totalTime = new Date().getTime() - startTime;

        if (debugMode) {
            System.out.println(totalTime + " ms");
            System.out.println();
        }

        if (!done) {
            throw new Exception("Timed out (" + totalTime + " ms) waiting for run update confirmation for run " + runId + " - contact staff ");
        }
    }

    /**
     * List who logged in, problems and languages.
     */
    public void listInfo() {

        try {

            checkRequiredParams();

            serverConnection = new ServerConnection();

            contest = serverConnection.login(login, password);

            try {
                listInfo(contest);

                serverConnection.logoff();

            } catch (Exception e) {
                e.printStackTrace();
                if (debugMode) {
                    e.printStackTrace();
                }

            }

        } catch (LoginFailureException e1) {
            System.out.println("Unable to login: " + e1.getMessage());
            if (debugMode) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Login and output runs for login.
     * 
     */
    public void listRuns() {

        try {
            checkRequiredParams();

            serverConnection = new ServerConnection();

            contest = serverConnection.login(login, password);

            System.out.println();
            System.out.println(contest.getContestTitle());
            System.out.println();
            System.out.println("For: " + contest.getMyClient().getDisplayName() + " (" + contest.getMyClient().getLoginName() + ")");
            System.out.println();

            IRun[] runs = contest.getRuns();
            if (runs.length == 0) {
                System.out.println("No runs submitted");
            } else {
                System.out.println(runs.length + " runs for " + contest.getMyClient().getDisplayName() + " (" + contest.getMyClient().getLoginName() + ")");
                System.out.println();
                Arrays.sort(runs, new IRunComparator());
                for (IRun run : runs) {
                    System.out.println("Run " + run.getNumber() + " at " + run.getSubmissionTime() + " by " + contest.getMyClient().getLoginName() + //
                            " " + run.getJudgementName() + //
                            " " + run.getProblem().getName() + " " + run.getLanguage().getName());
                }
            }
        } catch (LoginFailureException e1) {
            System.out.println("Unable to login: " + e1.getMessage());
            if (debugMode) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * List who logged in, problems and languages.
     * 
     * @param contest2
     */
    private void listInfo(IContest contest2) {

        System.out.println("Logged in as: " + contest2.getMyClient().getDisplayName());

        System.out.println();

        char let = 'A';

        System.out.println("Problems");
        for (IProblem problem : contest.getProblems()) {
            System.out.println(let + " - " + problem.getName());
            let++;
        }

        System.out.println();

        System.out.println("Languages");
        for (ILanguage language : contest.getLanguages()) {
            System.out.println(language.getName());
        }

        System.out.println();
    }

    /**
     * Find IProblem that matches the title.
     * 
     * Will look for an exact match, then look for a single letter used for problem, then looks for a problem title that starts with the input problem title.
     * 
     * @param problemTitle2
     *            title, letter or partial title.
     * @return
     */
    private IProblem matchProblem(String problemTitle2) {

        // check full name

        for (IProblem problem : contest.getProblems()) {
            if (problem.getName().equalsIgnoreCase(problemTitle2)) {
                return problem;
            }
            if (problem.getShortName().equalsIgnoreCase(problemTitle2)) {
                return problem;
            }
        }

        char let = 'A';

        // check letter

        for (IProblem problem : contest.getProblems()) {
            if (problem.getName().equalsIgnoreCase(Character.toString(let))) {
                return problem;
            }
            let++;
        }

        // check start name

        for (IProblem problem : contest.getProblems()) {

            if (problem.getName().toLowerCase().startsWith(problemTitle2.toLowerCase())) {
                return problem;
            }
        }

        return null;
    }

    public IClient getSubmittingUser() {
        return submittingUser;
    }

    /**
     * Listen for run events.
     * 
     * @author pc2@ecs.csus.edu
     */
    protected class RunEventListener implements IRunEventListener, Runnable {

        /**
         * Updated judgement for this run.
         */
        private IRun updatedRun = null;

        public void runSubmitted(IRun run) {
            // ignore
        }

        public void runDeleted(IRun run) {
            // ignore
        }

        public void runCheckedOut(IRun run, boolean isFinal) {
            // ignore

        }

        public void runJudged(IRun run, boolean isFinal) {
            // ignore
            if (run.getNumber() == runId) {
                updatedRun = run;
            }
        }

        public void runUpdated(IRun run, boolean isFinal) {
            // ignore
        }

        public void runCompiling(IRun run, boolean isFinal) {
            // ignore
        }

        public void runExecuting(IRun run, boolean isFinal) {
            // ignore
        }

        public void runValidating(IRun run, boolean isFinal) {
            // ignore
        }

        public void runJudgingCanceled(IRun run, boolean isFinal) {
            // ignore
        }

        public void run() {
            // ignore
        }

        public IRun getRun() {
            return updatedRun;
        }
    }

    public static void main(String[] args) {

        if (args.length == 0 || args[0].equals("--help")) {
            usage();
            ;
            System.exit(FAILURE_EXIT_CODE);
        }

        try {
            SubmitJudgment submitter = new SubmitJudgment(args);

            submitter.submitJudgement(args);

        } catch (CommandLineErrorException e) {
            System.err.println("Error on command line: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error submitting run " + e.getMessage());
            e.printStackTrace(System.err);
        }

    }

    /**
     * 
     * @param showAllMissingOptions true means when exception show messages
     */
    public void setShowAllMissingOptions(boolean showAllMissingOptions) {
        this.showAllMissingOptions = showAllMissingOptions;
    }

    /**
     * 
     * @see #setShowAllMissingOptions(boolean).
     * @return true if show missing options
     */
    public boolean isShowAllMissingOptions() {
        return showAllMissingOptions;
    }

    /**
     * Fatal error - log error and show user message before exiting.
     * 
     * @param message
     * @param ex
     */
    protected void fatalError(String message, Exception ex) {

        if (ex != null) {
            ex.printStackTrace(System.err);
        }
        System.err.println(message);

        System.exit(FAILURE_EXIT_CODE);

    }

    /**
     * 
     * @see #fatalError(String, Exception)
     * @param message
     */
    protected void fatalError(String message) {
        fatalError(message, null);
    }

}
