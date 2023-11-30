// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IPlugin;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.CommandLineErrorException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.shadow.ShadowRunSubmission;

/**
 * Submit Runs from EF JSON and files. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class SubmitterFromEF {

    private static final String PASSWORD_KEY = "--password";

    private static final String LOGIN_KEY = "--login";

    private IPlugin plugin = null;

    /**
     * Successful run exit code.
     *
     * Using a non-zero exit code because if there is a problem in the JVM
     * or elsewhere a zero exit code could be returned.
     */
    private static final int SUCCESS_EXIT_CODE = 5;

    private String login;

    private String password;

    private static final String NL = System.getProperty("line.separator");

    private static final String[] REQUIRED_CMD_LINE_OPTIONS = { "--login", "--password" };

    private static final int FAILURE_EXIT_CODE = 4;

    private boolean debugMode = false;

    private String clicsJsonFile;

    /**
     * Submit a clarification flag.
     */
    protected SubmitterFromEF() {

    }

    public SubmitterFromEF(String[] args) throws CommandLineErrorException {
        loadVariables(args);

    }

    void submitRunsFromEF() {
        submitRunsFromEF(login, password, clicsJsonFile);
    }

    void submitRunsFromEF(String user, String pass, String effilename) {

        File file = new File(effilename);

        if (!file.isFile()) {
            fatalError("No such file " + effilename);
        }

        List<ShadowRunSubmission> submissionList = checkAndFetchSubmissions(effilename);

        if (submissionList.size() < 1) {
            fatalError("No submissions found in JSON file " + effilename);
        }

        try {
            plugin = ClientUtility.logInToContest(user, pass);
        } catch (Exception e) {
            fatalError("Cannot login as "+user+" "+e.getLocalizedMessage());
        }

        IInternalContest contest = plugin.getContest();

        Account account = contest.getAccount(contest.getClientId());
        if (! account.isAllowed(Permission.Type.SHADOW_PROXY_TEAM)){
            String permissionName = new Permission().getDescription(Permission.Type.SHADOW_PROXY_TEAM);
            fatalError("Account "+account+" must have permission: "+permissionName);
        }

        boolean ccsMode = contest.getContestInformation().isCcsTestMode();
        if (! ccsMode){
            fatalError("Contest must be in CCS Mode");
        }

        boolean running = contest.getContestTime().isContestRunning();
        if (! running){
            fatalError("Contest clock must be running.");
        }

        List<String> errorList = validateSubmissions(submissionList);
        if (!errorList.isEmpty()){

            for (String string : errorList) {
                System.out.println(string);
                
            }
            
            System.err.println("There are "+errorList.size()+" errors in "+effilename);
            System.err.println("No runs submitted");
            System.err.println("Exiting program (exit code "+FAILURE_EXIT_CODE+")");
            System.exit(FAILURE_EXIT_CODE);
        }

        System.out.println("File: " + effilename + " passes JSON submission validation.");

        submitAllRuns(file.getParent(), submissionList);

    }

    private List<String> errors = new ArrayList<>();
    
    /**
     * Scan shadow submission  
     * 
     * @param submissionList
     * @return empty list 
     */
    private List<String> validateSubmissions(List<ShadowRunSubmission> submissionList) {

        int subCount= 0;

        errors = new ArrayList<>();

        for (ShadowRunSubmission runSubmission : submissionList) {

            subCount++;

            if (isEmpty(runSubmission.getLanguage_id())) {
                addError("Submission:" + subCount + " Empty or missing language for run " + runSubmission.getId());
            } else {
                Language language = matchLanguage(runSubmission.getLanguage_id());

                if (language == null) {
                    addError("Submission:" + subCount + " No such language '" + runSubmission.getLanguage_id() + "' for run " + runSubmission.getId());
                }
            }

            if (isEmpty(runSubmission.getProblem_id())) {
                addError("Submission:" + subCount + " Empty or missing problem for run " + runSubmission.getId());
            } else {
                Problem problem = matchProblem(runSubmission.getProblem_id());
                if (problem == null) {
                    addError("Submission:" + subCount + " No such problem '" + runSubmission.getProblem_id() + "' for run " + runSubmission.getId());
                }
            }

            if (isEmpty(runSubmission.getId())) {
                addError("Submission:" + subCount + " Empty or missing id (dang!) for run " + runSubmission.getId());
            } else {
                //                shadowRunSubmission.getId();

            }

            if (isEmpty(runSubmission.getTeam_id())) {
                addError("Submission:" + subCount + " Empty or missing team id for run " + runSubmission.getId());
            } else {
                try {
                    Integer.parseInt(runSubmission.getTeam_id());
                } catch (Exception e) {
                    addError("Submission:" + subCount + " Invalid team id " + runSubmission.getTeam_id() + " for run " + runSubmission.getId() + " error = " + e.getMessage());
                }
            }

            if (isEmpty(runSubmission.getFiles())) {
                addError("Submission:" + subCount + " Empty or missing files for run " + runSubmission.getId());
            } else {
                //                runSubmission.getFiles();
            }
        }

        return errors;
    }

    private boolean isEmpty(List<Map<String, String>> files) {
        return (files == null) || (files.size() == 0);
    }

    private void addError(String string) {
        errors.add(string);
    }

    private boolean isEmpty(String s) {
        return StringUtilities.isEmpty(s);
    }

    private void submitAllRuns(String parentDirectory, List<ShadowRunSubmission> submissionList) {
        
        int count = 0;

        for (ShadowRunSubmission runSubmission : submissionList) {

            long overrideTimeMS = Utilities.convertCLICSContestTimeToMS(runSubmission.getContest_time());
            long overrideSubmissionID = Utilities.stringToLong(runSubmission.getId());

            List<Map<String, String>> filesList = runSubmission.getFiles();

            String submissionFilesURL = "run" + runSubmission.getId();

            if (filesList.size() == 1) {
                Map<String, String> fileMap = filesList.get(0);

                if (fileMap.containsKey("href")) {
                    String filesPath = fileMap.get("href");
                    if (!StringUtilities.isEmpty(filesPath)) {
                        submissionFilesURL = filesPath;
                    }
                }
                else if (fileMap.size() == 1) {
                    String filesPath = fileMap.get(fileMap.keySet().iterator().next());
                    if (!StringUtilities.isEmpty(filesPath)) {
                        submissionFilesURL = filesPath;
                    }
                }
            }

            //            {"type":"submissions","id":"pc2-1667","op":"create","data":{"id":"1464","language_id":"python3","problem_id":"ColoringContention-1","team_id":"106","time":"2019-11-09T18:02:15.283-08","contest_time":"05:00:27.031","entry_point":"c",
            // "files":[{"href":"C:\\clevengr\\contest\\regionals\\PACIFIC\\F19\\ShadowTesting\\PacNW2019Real\\profiles\\Pc8f60d72-d4cb-4965-877b-90fd555f07be\\
            // reports\\report.Extract_Replay_Runs.01.16.462.txt.files/site1run1464/c.py","mime":"application/zip"}]}}
            String mainFileName = parentDirectory + File.separator + submissionFilesURL.replaceFirst(".*txt.files.", "");

            SerializedFile mainFile = new SerializedFile(mainFileName);

            if (mainFile.getBuffer().length == 0) {
                System.err.println("Cannot read/find file " + mainFileName);
            } else {
                
                try {
                    
                    int teamNumber = Integer.parseInt(runSubmission.getTeam_id());
                    ClientId submitter = new ClientId(getContest().getSiteNumber(), ClientType.Type.TEAM, teamNumber);

                    Problem problem = matchProblem(runSubmission.getProblem_id());
                    Language language = matchLanguage(runSubmission.getLanguage_id());

//                    Run debugRun = new Run(submitter, language, problem);
                    SerializedFile[] auxFiles = new SerializedFile[0];

                    getController().submitRun(submitter, problem, language, runSubmission.getEntry_point(), mainFile, auxFiles, overrideTimeMS, overrideSubmissionID);
                    count++;
                    System.out.println("Submission " + count + " run id = " + overrideSubmissionID);
      
                } catch (Exception e) {
                    System.err.println("\nUnable to submit run " + overrideSubmissionID);
                    Utilities.printStackTrace(System.err, e, "csus");
                }
            }

        }

    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> getMap(String jsonString) {

        if (jsonString == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private List<ShadowRunSubmission> checkAndFetchSubmissions(String effilename) {
        List<ShadowRunSubmission> submissionList = new ArrayList<>();

        try {
            String[] lines = Utilities.loadFile(effilename);
            if (lines.length == 0) {
                fatalError("No lines found in file " + effilename);
            }

            for (String line : lines) {

                if (line.length() > 0 && line.startsWith("{") && line.endsWith("}")) {

                    dprint("Got event string: " + line);

                    try {
                        // extract the event into a map of event element names/values
                        Map<String, Object> eventMap = getMap(line);
                        String eventType = (String) eventMap.get("type");

                        if ("submissions".equals(eventType)) {

                            try {
                                //get a map of the data comprising the submission
                                @SuppressWarnings("unchecked")
                                Map<String, Object> submissionEventDataMap = (Map<String, Object>) eventMap.get("data");
                                //convert metadata into ShadowRunSubmission
                                ShadowRunSubmission runSubmission = createRunSubmission(submissionEventDataMap);
                                submissionList.add(runSubmission);

                            } catch (Exception e) {
                                System.err.println("Error processing line: " + line + " : " + e.getMessage());
                                e.printStackTrace(System.err);
                            }
                        }
                        // else ignore non submission events

                    } catch (Exception e) {
                        System.err.println("Error processing line: " + line + " : " + e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(FAILURE_EXIT_CODE);
        }

        return submissionList;
    }

    protected static ShadowRunSubmission createRunSubmission(Map<String, Object> eventDataMap) {
        return new ObjectMapper().convertValue(eventDataMap, ShadowRunSubmission.class);
    }

    private void dprint(String string) {
        if (debugMode) {
            System.out.println(string);
        }
    }

    protected void loadVariables(String[] args) throws CommandLineErrorException {

        if (args.length == 0 || args[0].equals("--help")) {
            usage();
            System.exit(4);
        }

        ParseArguments arguments = new ParseArguments(args, REQUIRED_CMD_LINE_OPTIONS);

        if (arguments.isOptPresent("--help")) {
            usage();
            System.exit(4);
        }

        debugMode = arguments.isOptPresent("--debug");

        if (debugMode) {
            arguments.dumpArgs(System.err);
        }

        if (arguments.isOptPresent(LOGIN_KEY)) {
            login = arguments.getOptValue(LOGIN_KEY);
        } else {
            fatalError("Missing required option --login ");
        }
        
        ClientId cid = InternalController.loginShortcutExpansion(1, login);
        if (cid != null){
            login = cid.getName();
        }

        if (arguments.isOptPresent(PASSWORD_KEY)) {
            password = arguments.getOptValue(PASSWORD_KEY);
        }

        if (password == null) {
            password = login;
        }

        if (arguments.getArgCount() == 0) {
            fatalError("Missing EFJSonFile name");
        }

        clicsJsonFile = arguments.getArg(0);
    }

    private void fatalError(String errorMessage) {
        System.err.println("Fatal error " + errorMessage);
        System.exit(FAILURE_EXIT_CODE);

    }

    private void usage() {
        String[] usage = { //
        "", //
                "Usage SubmitterFromEF [--help] [--password PASSWORD] --login LOGIN EFFile", //
                "", //
                "where:", //
                "  LOGIN - pc2 login ", //
                "  PASSWORD - pc2 password, if joe account then can omit", //
                "  EFJSONFile =  a CLICS JSON event feeed", //
                "", //
                "On success exit code will be " + SUCCESS_EXIT_CODE, //
                "Any other exit code is an error.", //
                "", //
        };

        for (String s : usage) {
            System.out.println(s);
        }
    }

    IInternalContest getContest() {
        return plugin.getContest();
    }

    IInternalController getController() {
        return plugin.getController();
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
    private Problem matchProblem(String title) {

        // check full name

        for (Problem problem : getContest().getProblems()) {
            if (problem.getDisplayName().equalsIgnoreCase(title)) {
                return problem;
            }
            if (problem.getShortName().equalsIgnoreCase(title)) {
                return problem;
            }

        }

        return null;
    }

    private Language matchLanguage(String languageTitle2) {
        for (Language language : getContest().getLanguages()) {
            if (language.getDisplayName().equalsIgnoreCase(languageTitle2)) {
                return language;
            }
            if (language.getID().equalsIgnoreCase(languageTitle2)) {
                return language;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        try {
            SubmitterFromEF submitter = new SubmitterFromEF(args);
            submitter.submitRunsFromEF();
        } catch (CommandLineErrorException e) {
            System.err.println("Error on command line: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error submitting run " + e.getMessage());
            e.printStackTrace(System.err);
        }

    }

}
