// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.implementation.ClientImplementation;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.implementation.JudgementImplementation;
import edu.csus.ecs.pc2.api.implementation.LanguageImplementation;
import edu.csus.ecs.pc2.api.implementation.ProblemImplementation;
import edu.csus.ecs.pc2.api.implementation.RunImplementation;
import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.customValidator.CustomValidatorSettings;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * This class represents a connection to a PC<sup>2</sup> server. 
 * Instantiating the class creates a local {@link ServerConnection} object which can then be used to connect 
 * to the PC<sup>2</sup> server via the {@link ServerConnection#login(String, String)} method. 
 * The PC<sup>2</sup> server must already be running, and the local client must have a <code>pc2v9.ini</code> 
 * file specifying valid server connection information, prior to invoking {@link ServerConnection#login(String, String)} 
 * method.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnection {

    
    /**
     * Valid Problem Property names.
     */
    private String[] problemPropertyNames = { // 
            APIConstants.JUDGING_TYPE, //
            APIConstants.VALIDATOR_PROGRAM, //
            APIConstants.VALIDATOR_COMMAND_LINE //
    };
    
    protected IInternalController controller;
    
    protected IInternalContest internalContest;
    
    protected Contest contest = null;
    
//    protected MockTestRunImplementation mockTestRunImplementation = new MockTestRunImplementation();
    
    /**
     * Construct a local {@link ServerConnection} object which can subsequently be used to connect to a currently-running PC<sup>2</sup> server.
     * 
     */
    public ServerConnection() {
        super();
    }

    /**
     * Login to the PC<sup>2</sup> server represented by this ServerConnection using the specified login account name and password. If the login is successful, the method returns an
     * {@link edu.csus.ecs.pc2.api.IContest} object which can then be used to obtain information about the contest being controlled by the server. If the login fails the method throws
     * {@link edu.csus.ecs.pc2.api.exceptions.LoginFailureException}, in which case the message contained in the exception can be used to determine the nature of the login failure.
     * <P>
     * Note that invoking {@link ServerConnection#login(String, String)} causes an attempt to establish a network connection to a PC<sup>2</sup> server using the connection information specified in
     * the <code>pc2v9.ini</code> file in the current directory. The PC<sup>2</sup> server must <I>already be running</i> prior to invoking {@link ServerConnection#login(String, String)}, and the
     * <code>pc2v9.ini</code> must specify legitimate server connection information; otherwise, {@link edu.csus.ecs.pc2.api.exceptions.LoginFailureException} is thrown. See the PC<sup>2</sup> Contest
     * Administrator's Guide for information regarding specifying server connection information in <code>pc2v9.ini</code> files.
     * <P>
     * The following code snippet shows typical usage for connecting to and logging in to a PC<sup>2</sup> server. <A NAME="loginsample"></A>
     * 
     * <pre>
     * String login = &quot;team4&quot;;
     * String password = &quot;team4&quot;;
     * try {
     *     ServerConnection serverConnection = new ServerConnection();
     *     IContest contest = serverConnection.login(login, password);
     *     // ... code here to invoke methods in &quot;contest&quot;;
     *     serverConnection.logoff();
     * } catch (LoginFailureException e) {
     *     System.out.println(&quot;Could not login because &quot; + e.getMessage());
     * } catch (NotLoggedInException e) {
     *     System.out.println(&quot;Unable to execute API method&quot;);
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param login
     *            client login name (for example: &quot;team5&quot; or &quot;judge3&quot;)
     * @param password
     *            password for the login name
     * @return
     *            an IContest object representing the Contest which has been logged in to
     *            
     * @throws LoginFailureException
     *             if login fails, the message contained in the exception will provide and indication of the reason for the failure.
     */
    public IContest login(String login, String password) throws LoginFailureException {

        /**
         * Was contest overridden/set rather than having to login to
         * instantiate the contest?
         */
        boolean overrideContestUsed = false;
        
        if (contest != null) {
            throw new LoginFailureException("Already logged in as: " + contest.getMyClient().getLoginName());
        }
        if (internalContest == null){
            internalContest = new InternalContest();
        } else {
            overrideContestUsed = true;
        }
            
        if (controller == null){
            controller = new InternalController(internalContest);
        }

        controller.setUsingGUI(false);
        
        if (controller instanceof InternalController) {
            ((InternalController) controller).setUsingMainUI(false);
            ((InternalController) controller).setHaltOnFatalError(false);
        }
        
        controller.setClientAutoShutdown(false);

        try {
            controller.start(new String[0]);
            if (!overrideContestUsed){
                internalContest = controller.clientLogin(internalContest, login, password);
            }
            
//            mockTestRunImplementation.setContestAndController(internalContest, controller);

            contest = new Contest(internalContest, controller, controller.getLog());
            contest.addConnectionListener(new ConnectionEventListener());
            controller.register(contest);

            return contest;

        } catch (Exception e) {
            throw new LoginFailureException(e.getMessage());
        }
    }
    
//    private Account getAccount(IInternalContest iContest, ClientId clientId) throws Exception {
//
//        Vector<Account> accountList = iContest.getAccounts(clientId.getClientType());
//
//        for (int i = 0; i < accountList.size(); i++) {
//            if (accountList.elementAt(i).getClientId().equals(clientId)) {
//                return accountList.elementAt(i);
//            }
//        }
//
//        /**
//         * This condition should not happen. Every clientId in the system that can login to the system must have a matching account. This Exception is thrown when there is no Account in
//         * internalContest for the input ClientId.
//         */
//        throw new Exception("Internal Error (SC.getAccount) No account found for " + clientId);
//    }
    
     public boolean isValidAccountTypeName (String name){
        try {
            ClientType.Type clientType = ClientType.Type.valueOf(name);
            return clientType != null;
        } catch (java.lang.IllegalArgumentException e) {
            return false;
        }
     }
     
     /**
      * Add a single account (admin feature).
      * <P> 
      * For the input accountTypeName, ex. TEAM, will add the next client number account.
      * If adding a new TEAM account and there are 22 teams, this method will add team23 login account.
      * <P>
      * The current logged in user must be an Administrator user otherwise a SecurityException
      * will be thrown.
      * <P>
      * The following code snippet shows a example for a addAccount invocation.
      * 
      * <pre>
      * String login = &quot;administrator2&quot;;
      * String password = &quot;administrator2&quot;;
      * try {
      *     ServerConnection serverConnection = new ServerConnection();
      *     IContest contest = serverConnection.login(login, password);
      *     connection.addAccount("TEAM", "Team Name", "teampassword");
      *     Thread.sleep(1000); // sleep just long enough before logging off
      *     serverConnection.logoff();
      * } catch (LoginFailureException e) {
      *     System.out.println(&quot;Could not login because &quot; + e.getMessage());
      * } catch (NotLoggedInException e) {
      *     System.out.println(&quot;Unable to execute API method&quot;);
      *     e.printStackTrace();
      * }
      * </pre>      
      * 
      * @param accountTypeName name of account, ex TEAM
      * @param displayName title for account/team, if null will be login name
      * @param password password for account, must not be null or emptystring (string length==0)
      * 
      * @throws IllegalArgumentException if the account type is invalid or the password is null or empty
      * @throws SecurityException if the user is not allowed to perform this action.
      * @throws IllegalArgumentException if the accountTypeName does not specify a valid account type
      * @throws Exception if an error occurs when calling the server to add the account
      */
    public void addAccount(String accountTypeName, String displayName, String password) throws Exception {

        accountTypeName = accountTypeName.toUpperCase();

        if (!isValidAccountTypeName(accountTypeName)) {
            throw new IllegalArgumentException("Invalid account type name '" + accountTypeName + "'");
        }

        checkIsAllowed(Type.ADD_ACCOUNT, "This login/account is not allowed to add an account");

        ClientType.Type clientType = ClientType.Type.valueOf(accountTypeName);

        ClientId clientId = new ClientId(internalContest.getSiteNumber(), clientType, 0);

        if (password == null || password.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid password (null or missing) '" + password + "'");
        }

        if (displayName == null) {
            displayName = clientId.getName();
        }

        Account account = new Account(clientId, password, internalContest.getSiteNumber());
        account.setDisplayName(displayName);
        
        account.clearListAndLoadPermissions(new PermissionGroup().getPermissionList(clientType));

        controller.addNewAccount(account);
    }    
    
    /**
     * Submit a clarification request to the PC2 Server.
     * 
     * @param problem  the Problem for which the clarification request is being submitted
     * @param question text of question
     * @throws NotLoggedInException if the client is not currently logged in to the server
     * @throws SecurityException if the user allowed to perform this action.
     * @throws Exception if the specified Problem is null or the clarification request could not be submitted to the server
     */
    public void submitClarification(IProblem problem, String question) throws Exception {

        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.SUBMIT_CLARIFICATION, "User not allowed to submit clarification");

        ProblemImplementation problemImplementation = (ProblemImplementation) problem;
        Problem submittedProblem = internalContest.getProblem(problemImplementation.getElementId());

        if (submittedProblem == null) {
            throw new Exception("Could not find any problem matching: '" + problem.getName());
        }
        
        if (!contest.isContestClockRunning()) {
            throw new Exception("Contest is STOPPED - no clarifications accepted.");
        }

        try {
            controller.submitClarification(submittedProblem, question);
        } catch (Exception e) {
            throw new Exception("Unable to submit clarifications " + e.getLocalizedMessage());
        }
    }

    /**
     * Submit run judgement for a run.
     * 
     * @param run The run to add a judgement to.
     * @param judgement the judgement to add to run.
     *
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws NotLoggedInException if the user is not logged in.
     * @throws Exception if unable to submit run.
     */
    public void submitRunJudgement(IRun run, IJudgement judgement) throws Exception {

        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.EDIT_ACCOUNT, "User not allowed to edit/judge a run");
  
        try {
            
            RunImplementation runImplementation = (RunImplementation) run;
            Run runToUpdate = internalContest.getRun(runImplementation.getElementId());
            
            JudgementImplementation judgementImplementation = (JudgementImplementation) judgement;
            Judgement internaljudgement = internalContest.getJudgement(judgementImplementation.getElementId());

            if (internaljudgement == null){
                throw new Exception("No such judgement for "+judgement);
            }
            
            ClientImplementation clientImplementation = (ClientImplementation) getMyClient();
            ClientId clientId = clientImplementation.getClientId();
            
            boolean solved = isYesJudgement(internaljudgement);
            JudgementRecord judgementRecord = new JudgementRecord(internaljudgement.getElementId(), clientId, solved, true);
            
            runToUpdate.setStatus(Run.RunStates.JUDGED);
            runToUpdate.addJudgement(judgementRecord);
            
            RunResultFiles runFiles = null;  // coded on master
            // coded on branch RunResultFiles runFiles = new RunResultFiles(runToUpdate, runToUpdate.getProblemId(), judgementRecord, null);
            // dal unsure why runFiles was ever populated when just adding a run judgement, seems like extra work
            controller.updateRun(runToUpdate, judgementRecord, runFiles);
            
        } catch (Exception e) {
            throw new Exception("Unable to submit run " + e.getLocalizedMessage(), e.getCause());
        }
    }
    
    // **** SUBMIT JUDGE RUN methods **** //
    
    //   *** Submit Judge Runs using String file names *** //
    /**
     * Submit a Judge Run (a run which the Judges are expected to evaluate and count for scoring)
     *  using String filenames.
     * 
     * @param problem the Problem for which the run is being submitted
     * @param language the Language used for the Problem submission (Java, C++, etc.)
     * @param mainFileName the name of the main source code file
     * 
     * Calling this method is equivalent to calling {@link #submitJudgeRun(IProblem, ILanguage, String, String[])} 
     * with String[0] as the last parameter (that is, not specifying any additional file names).
     * 
     * @throws NotLoggedInException if the client is not currently logged in to the server
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws Exception if any of the specified files cannot be found, if the Problem or Language is null, 
     *          the contest is not running, or a failure occurred while submitting the run to the server
     *          
     * @see #submitJudgeRun(IProblem, ILanguage, String, String[])
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, String mainFileName) throws Exception {
        submitJudgeRun(problem, language, mainFileName, new String[0]);
    }

    /**
     * Submit a Judge run (a run which the Judges are expected to evaluate and count for scoring)
     * using String filenames.
     * 
     * @param problem the Problem for which the run is being submitted
     * @param language the Language used for the Problem submission (Java, C++, etc.)
     * @param mainFileName the name of the main source code file
     * @param additionalFileNames an array of Strings giving the names of any additional files submitted
     * 
     * Calling this method is equivalent to calling {@link #submitJudgeRun(IProblem, ILanguage, String, String[], long, long)} 
     * with zero as the last two parameters (that is, not specifying override time/runid values).
     * 
     * @throws NotLoggedInException if the client is not currently logged in to the server.
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws Exception if any of the specified files cannot be found, 
     *          if the Problem or Language is null or cannot be found, 
     *          if the contest is not running, 
     *          or if a failure occurred while submitting the run to the server.
     *          
     * @see #submitJudgeRun(IProblem, ILanguage, String, String[], long, long)
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, String mainFileName, String[] additionalFileNames) throws Exception {
        submitJudgeRun(problem, language, mainFileName, additionalFileNames, 0, 0);
    }

    /**
     * Submit a Judge run (a run which the Judges are expected to evaluate and count for scoring)
     * using String filenames.
     * 
     * @param problem the Problem for which the run is being submitted
     * @param language the Language used for the Problem submission (Java, C++, etc.)
     * @param mainFileName the name of the main source code file
     * @param additionalFileNames an array of Strings giving the names of any additional files submitted
     * @param overrideSubmissionTimeMS the submission time which should be assigned to the run; if greater than zero,
     *                  overrides the default (which is the current time).  
     *                  Only has effect if contest information CCS test mode is set true.
     * @param overrideRunId the Run ID which should be assigned to the run; if greater than zero, overrides the
     *                  default (which is that the server assigns the next available RunID to the run).  
     *                  Only has effect if contest information CCS test mode is set true.
     * 
     * @throws NotLoggedInException if the client is not currently logged in to the server
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws Exception if any of the specified files cannot be found, 
     *          if the Problem or Language is null or cannot be found, 
     *          if the contest is not running, 
     *          or if a failure occurred while submitting the run to the server
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, String mainFileName, String[] additionalFileNames, 
                                long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.SUBMIT_RUN, "User not allowed to submit run");
        
        if (! new File(mainFileName).isFile()){
            throw new Exception("File '"+mainFileName+"' no such file (not found)"); 
        }
        
        SerializedFile[] list = new SerializedFile[additionalFileNames.length];
        for (int i = 0; i < additionalFileNames.length; i++) {
            if (new File(additionalFileNames[i]).isFile()) {
                list[i] = new SerializedFile(additionalFileNames[i]);
            } else {
                throw new Exception("File '" + additionalFileNames[i] + "' no such file (not found)");
            }
        }

        ProblemImplementation problemImplementation = (ProblemImplementation) problem;
        Problem submittedProblem = internalContest.getProblem(problemImplementation.getElementId());
        
        LanguageImplementation languageImplementation = (LanguageImplementation) language;
        Language submittedLanguage = internalContest.getLanguage(languageImplementation.getElementId());
        
        if (submittedProblem == null) {
            throw new Exception("Could not find any problem matching: '" + problem.getName());
        }

        if (submittedLanguage == null) {
            throw new Exception("Could not find any language matching: '" + language.getName());
        }
        
        if (! contest.isContestClockRunning()){
            throw new Exception("Contest is STOPPED - no runs accepted.");
        }

        try {
            controller.submitJudgeRun(submittedProblem, submittedLanguage, mainFileName, list, overrideSubmissionTimeMS, overrideRunId);
        } catch (Exception e) {
            throw new Exception("Unable to submit run " + e.getLocalizedMessage());
        }
    }
    
    
    //   *** Submit Judge Runs using IFiles *** //
    
    /**
     * Submit a Judge run (a run which the Judges are expected to evaluate and count for scoring)
     * using {@link IFile}s.
     * 
     * Calling this method is exactly the same as calling {@link #submitJudgeRun(IProblem, ILanguage, IFile, IFile[])}
     * with the same first three parameters and null as the last parameter.
     * 
     * @param problem the problem for which the Judge Run is being submitted
     * @param language the Language used for the Judge Run
     * @param mainFile an {@link IFile} object containing the main program for the Judge Run
     * 
     * @throws Exception if any of the specified files are invalid (have empty names or contain no data), 
     *          if the Problem or Language is null or cannot be found, 
     *          if the contest is not running, 
     *          or if a failure occurred while submitting the run to the server
     *          
     * @see #submitJudgeRun(IProblem, ILanguage, IFile, IFile[])
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, IFile mainFile) throws Exception {
        submitJudgeRun(problem,language,mainFile,null);
    }
    
    /**
     * Submit a Judge run (a run which the Judges are expected to evaluate and count for scoring)
     * using {@link IFile}s.
     * 
     * Calling this method is equivalent to calling {@link #submitJudgeRun(IProblem, ILanguage, IFile, IFile[], long, long)} 
     * with zero as the last two parameters (that is, not specifying override time/runid values).
     * 
     * @param problem the problem for which the Judge Run is being submitted
     * @param language the Language used for the Judge Run
     * @param mainFile an {@link IFile} object containing the main program for the Judge Run
     * @param additionalFiles an array of {@link IFile} objects with each element containing an 
     *                  additional file being submitted as part of the Judge run
     *                  
     * @throws NotLoggedInException if the client is not currently logged in to the server
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws Exception if any of the specified files are invalid (have empty names or contain no data), 
     *          if the Problem or Language is null or cannot be found, 
     *          if the contest is not running, 
     *          or if a failure occurred while submitting the run to the server
     *          
     * @see #submitJudgeRun(IProblem, ILanguage, IFile, IFile[], long, long)
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, IFile mainFile, IFile [] additionalFiles) throws Exception {
        submitJudgeRun(problem, language, mainFile, additionalFiles, 0, 0);
    }

    /**
     * Submit a Judge run (a run which the Judges are expected to evaluate and count for scoring)
     * using {@link IFile}s.
     * 
     * @param problem the problem for which the Judge Run is being submitted
     * @param language the Language used for the Judge Run
     * @param mainFile an {@link IFile} object containing the main program for the Judge Run
     * @param additionalFiles an array of {@link IFile} objects with each element containing an 
     *                  additional file being submitted as part of the Judge run
     * @param overrideSubmissionTimeMS a value of type long which, if non-zero, will be used as the submission time of
     *                  the Judge Run, overriding the actual submission time; only has effect if Contest Information
     *                  CCS Test Mode is true
     * @param overrideRunId a value of type long which, if non-zero, will be used as the RunId of the Judge Run,
     *                  overriding the default (internally-assigned) RunId; only has effect if Contest Information
     *                  CCS Test Mode is true
     *                  
     * @throws NotLoggedInException if the client is not currently logged in to the server
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws Exception if any of the specified files are invalid (have empty names or contain no data), 
     *          if the Problem or Language is null or cannot be found, 
     *          if the contest is not running, 
     *          or if a failure occurred while submitting the run to the server
     */
    public void submitJudgeRun(IProblem problem, ILanguage language, IFile mainFile, IFile [] additionalFiles,
                                long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.SUBMIT_RUN, "User not allowed to submit run");
                
        //validate mainFile param
        if (!validIFile(mainFile)) {
            throw new Exception("Invalid mainFile parameter"); 
        }
        // validate additionalSourceFiles param
        if (additionalFiles != null && additionalFiles.length > 0) {
            for (IFile nextFile : additionalFiles) {
                if (!validIFile(nextFile)) {
                    throw new Exception("Invalid IFile in additionalFiles array"); 
                }
            }
        }

        ProblemImplementation problemImplementation = (ProblemImplementation) problem;
        Problem submittedProblem = internalContest.getProblem(problemImplementation.getElementId());
        
        LanguageImplementation languageImplementation = (LanguageImplementation) language;
        Language submittedLanguage = internalContest.getLanguage(languageImplementation.getElementId());

        if (submittedProblem == null) {
            throw new Exception("Could not find any problem matching: '" + problem.getName());
        }

        if (submittedLanguage == null) {
            throw new Exception("Could not find any language matching: '" + language.getName());
        }
        
        if (! contest.isContestClockRunning()){
            throw new Exception("Contest is STOPPED - no runs accepted.");
        }

        //convert main program IFile to SerializedFile for sending to Controller
        SerializedFile serializedMainFile = new SerializedFile(mainFile);
        //make sure no errors occurred during conversion
        if (serializedMainFile.getErrorMessage()!=null || serializedMainFile.getException()!=null) {
            throw new Exception("Error converting mainFile to SerializedFile: " + serializedMainFile.getErrorMessage(), 
                    serializedMainFile.getException());
        }
        
        //convert any "additionalFiles" IFiles to SerializedFiles for sending to Controller
        SerializedFile [] serializedAdditionalFiles = null;
        if (additionalFiles != null && additionalFiles.length>0) {
            serializedAdditionalFiles = new SerializedFile [additionalFiles.length];
            for (int i=0; i<additionalFiles.length; i++) {
                serializedAdditionalFiles[i] = new SerializedFile(additionalFiles[i]);
                //make sure no errors occurred during conversion
                if (serializedAdditionalFiles[i].getErrorMessage()!=null || serializedAdditionalFiles[i].getException()!=null) {
                    throw new Exception("Error converting additional file to SerializedFile: " + serializedAdditionalFiles[i].getErrorMessage(), 
                            serializedAdditionalFiles[i].getException());
                }
            }
        }

        try {
            controller.submitJudgeRun(submittedProblem, submittedLanguage, serializedMainFile, serializedAdditionalFiles, 
                                        overrideSubmissionTimeMS, overrideRunId);
        } catch (Exception e) {
            throw new Exception("Unable to submit run " + e);
        }
    }


    
    // **** SUBMIT TEST RUN methods **** //
    
    /**
     * Submit a Test run (a run which will be executed but will not affect a team's score)
     * using {@link IFile}s.
     * 
     * The results of a submitted Test Run are returned in a {@link TestRunResults} object via a callback
     * to a registered {@link ITestRunListener}.  It is the caller's responsibility to register an appropriate
     * {@link ITestRunListener} prior to invoking this method.
     * 
     * Invoking this method is equivalent to invoking 
     * {@link #submitTestRun(IProblem, ILanguage, IFile, IFile, IFile[], IFile[])}
     * with null as the last two parameters.
     * 
     * @param problem the problem for which the Test Run is being submitted
     * @param language the Language used for the Test Run
     * @param mainFile an {@link IFile} object containing the main program for the Test Run
     * @param testDataFile an {@link IFile} object containing the input data to be supplied to the Test Run
     * 
     * @throws Exception if an error occurs in submitting the Test Run
     * 
     * @see #submitTestRun(IProblem, ILanguage, IFile, IFile, IFile[], IFile[])
     */
    public void submitTestRun(IProblem problem, ILanguage language, IFile mainFile, IFile testDataFile) throws Exception {
        submitTestRun(problem, language, mainFile, testDataFile, null, null);
    }

    /**
     * Submit a Test run (a run which will be executed but will not affect a team's score)
     * using {@link IFile}s.
     * 
     * NOTE: currently the PC2 Server does not support Test Runs; this method is guaranteed to throw either
     * {@link Exception} (if the received parameters are invalid) or {@link UnsupportedOperationException}.
     * 
     * The results of a submitted Test Run are returned in a {@link TestRunResults} object via a callback
     * to a registered {@link ITestRunListener}.  It is the caller's responsibility to register an appropriate
     * {@link ITestRunListener} prior to invoking this method.
     * 
     * @param problem the problem for which the Test Run is being submitted
     * @param language the Language used for the Test Run
     * @param mainFile an {@link IFile} object containing the main program for the Test Run
     * @param testDataFile an {@link IFile} object containing the file to be used as input data during execution of the Test Run
     * @param additionalSourceFiles an array containing {@link IFile} objects, each representing an additional 
     *                              source code file submitted as part of the Test Run
     * @param additionalTestDataFiles an array containing {@link IFile} objects, each representing an additional
     *                              test data file submitted as part of the Test Run
     * @throws Exception if any of the specified IFiles are invalid (have no name or are zero length), 
     *              if the specified Problem or Language is null,
     *              if the contest clock is not running, 
     *              or if an error occurred while submitting the Test Run to the server
     * @throws UnsupportedOperationException if {@link Exception} is not thrown
     */
    public void submitTestRun(IProblem problem, ILanguage language, IFile mainFile, IFile testDataFile, 
                        IFile [] additionalSourceFiles, IFile [] additionalTestDataFiles) throws Exception, UnsupportedOperationException {
        
        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.SUBMIT_RUN, "User not allowed to submit test run");
        
        //validate mainFile param
        if (!validIFile(mainFile)) {
            throw new Exception("Invalid mainFile parameter"); 
        }
        // validate additionalSourceFiles param
        if (additionalSourceFiles != null && additionalSourceFiles.length > 0) {
            for (IFile nextFile : additionalSourceFiles) {
                if (!validIFile(nextFile)) {
                    throw new Exception("Invalid IFile in additionalSourceFiles array"); 
                }
            }
        }
        
        // validate testDataFile param
        if (!validIFile(testDataFile)) {
            throw new Exception("Invalid testDataFile parameter"); 
        }
        
        // validate additionalTestDataFiles param
        if (additionalTestDataFiles != null && additionalTestDataFiles.length > 0) {
            for (IFile nextFile : additionalTestDataFiles) {
                if (!validIFile(nextFile)) {
                    throw new Exception("Invalid IFile in additionalTestDataFiles array"); 
                }
            }
        }
        
        ProblemImplementation problemImplementation = (ProblemImplementation) problem;
        Problem submittedProblem = internalContest.getProblem(problemImplementation.getElementId());
        
        LanguageImplementation languageImplementation = (LanguageImplementation) language;
        Language submittedLanguage = internalContest.getLanguage(languageImplementation.getElementId());
        
        if (submittedProblem == null) {
            throw new Exception("Could not find any problem matching: '" + problem.getName());
        }

        if (submittedLanguage == null) {
            throw new Exception("Could not find any language matching: '" + language.getName());
        }
        
        if (! contest.isContestClockRunning()){
            throw new Exception("Contest is STOPPED - no test runs accepted.");
        }
        
        try {
            throw new UnsupportedOperationException("Test Runs currently not supported");
            
            //TODO: MOCK TEST RUN Submission  
//            mockTestRunImplementation.submitTestRun(internalContest, contest, submittedProblem, submittedLanguage, 
//                    mainFile, testDataFile, additionalSourceFiles, additionalTestDataFiles);
            
//              controller.submitTestRun(submittedProblem, submittedLanguage, mainFile, additionalSourceFiles, 
//                               testDataFile, additionalTestDataFiles);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Unable to submit test run " + e);
        }
    }

    /**
     * Submit a Test run (a run which will be executed but will not affect a team's score)
     * using String filenames.
     * 
     * The results of a submitted Test Run are returned in a {@link TestRunResults} object via a callback
     * to a registered {@link ITestRunListener}.  It is the caller's responsibility to register an appropriate
     * {@link ITestRunListener} prior to invoking this method.
     * 
     * Calling this method is equivalent to calling 
     *      {@link #submitTestRun(IProblem, ILanguage, String, String, String[], String[])}
     *      with null as the last two parameters.
     * 
     * @param problem the problem for which the Test Run is being submitted
     * @param language the Language used for the Test Run
     * @param mainFileName the name of the file containing the main program for the Test Run
     * @param testDataFileName the data file to be used as input during execution of the Test Run
     * 
     * @throws Exception if an error occurs in submitting the Test Run
     * 
     * @see #submitTestRun(IProblem, ILanguage, String, String, String[], String[])
     */
    public void submitTestRun(IProblem problem, ILanguage language, String mainFileName, String testDataFileName) throws Exception {
        submitTestRun(problem, language, mainFileName, testDataFileName, null, null);
    }

    /**
     * Submit a Test run (a run which will be executed but will not affect a team's score)
     * using String filenames.
     * 
     * NOTE: currently the PC2 Server does not support Test Runs; this method is guaranteed to throw either
     * {@link Exception} (if the received parameters are invalid) or {@link UnsupportedOperationException}.
     * 
     * The results of a submitted Test Run are returned in a {@link TestRunResults} object via a callback
     * to a registered {@link ITestRunListener}.  It is the caller's responsibility to register an appropriate
     * {@link ITestRunListener} prior to invoking this method.
     * 
     * @param problem the problem for which the Test Run is being submitted
     * @param language the Language used for the Test Run
     * @param mainFileName the name of the file containing the main program for the Test Run
     * @param testDataFileName the data file to be used as input during execution of the Test Run
     * @param otherSourceFileNames an array containing the names of additional source code files to be submitted (currently unused)
     * @param otherDataFileNames an array containing the names of other data files to be submitted (currently unused)
     * 
     * @throws NotLoggedInException is the client is not logged in to the server
     * @throws SecurityException if the client is not allowed to perform this action
     * @throws Exception if any of the specified files cannot be found, if the specified Problem or Language is null,
     *              if the contest clock is not running, or if an error occurred while submitting the Test Run to 
     *              the server
     * @throws UnsupportedOperationException if {@link Exception} is not thrown
     */
    public void submitTestRun(IProblem problem, ILanguage language, String mainFileName, String testDataFileName, 
                    String [] otherSourceFileNames, String [] otherDataFileNames) throws Exception, UnsupportedOperationException {
        
        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.SUBMIT_RUN, "User not allowed to submit test run");
        
        if (! new File(mainFileName).isFile()){
            throw new Exception("File '"+mainFileName+"' no such file (not found)"); 
        }
        
        if (testDataFileName != null && ! new File(testDataFileName).isFile()){
            throw new Exception("File '"+testDataFileName+"' no such file (not found)"); 
        }
        
        ProblemImplementation problemImplementation = (ProblemImplementation) problem;
        Problem submittedProblem = internalContest.getProblem(problemImplementation.getElementId());
        
        LanguageImplementation languageImplementation = (LanguageImplementation) language;
        Language submittedLanguage = internalContest.getLanguage(languageImplementation.getElementId());
                
        if (submittedProblem == null) {
            throw new Exception("Could not find any problem matching: '" + problem.getName());
        }

        if (submittedLanguage == null) {
            throw new Exception("Could not find any language matching: '" + language.getName());
        }
        
        if (! contest.isContestClockRunning()){
            throw new Exception("Contest is STOPPED - no test runs accepted.");
        }

        try {
            throw new UnsupportedOperationException("Test Runs currently not supported");
            
            //TODO: MOCK TEST RUN Submission             
//            mockTestRunImplementation.submitTestRun(internalContest, contest, submittedProblem, submittedLanguage, 
//                    mainFileName, testDataFileName, otherSourceFileNames, otherDataFileNames);
            
//            controller.submitTestRun(submittedProblem, submittedLanguage, mainFileName, testDataFileName);
        } catch (Exception e) {
            throw new Exception("Unable to submit test run: " + e.getLocalizedMessage());
        }
        
    }

    
    // ****  MISCELLANEOUS/UTILITY methods **** //
    
    private void checkWhetherLoggedIn() throws NotLoggedInException {
        if (contest == null || internalContest == null){
            throw new NotLoggedInException ("Not logged in");
        }
    }
    
    private boolean isYesJudgement(Judgement judgement) {
        return Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym()); 
    }

    /**
     * Logoff/disconnect from the PC<sup>2</sup> Server.
     * 
     * @return true if logged off, else false. <-- there appears to be no circumstance under which this method can return false (jlc)
     * @throws NotLoggedInException
     *             if attempt to logoff without being logged in
     */
    public boolean logoff() throws NotLoggedInException {

        if (contest == null) {
            throw new NotLoggedInException("Cannot log off, not logged in");
        }

        try {
            controller.logoffUser(internalContest.getClientId());
            contest.setLoggedIn(false);
            contest = null;
            return true;
        } catch (Exception e) {
            throw new NotLoggedInException(e);
        }
    }

    /**
     * Returns a IContest, if not connected to a server throws a NotLoggedInException.
     * 
     * @return contest
     * @throws NotLoggedInException
     *             if attempt to invoke this method without being logged in
     */
    public Contest getContest() throws NotLoggedInException {
        if (contest != null) {
            return contest;
        } else {
            throw new NotLoggedInException("Can not get IContest, not logged in");
        }
    }

    /**
     * Is this ServerConnection connected to a server ?
     * 
     * @return true if connected to server, false if not connected to server.
     */
    public boolean isLoggedIn() {
        return contest != null && contest.isLoggedIn();
    }

    /**
     * Returns a IClient if logged into a server.
     * 
     * @see IContest#getMyClient()
     * @return Client class
     * @throws NotLoggedInException
     *             if attempt to invoke this method without being logged in
     */
    public IClient getMyClient() throws NotLoggedInException {
        if (contest != null) {
            return contest.getMyClient();
        } else {
            throw new NotLoggedInException("Not logged in");
        }
    }

    /**
     * This class defines a Connection Event used by ServerConnection.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ConnectionEventListener implements IConnectionEventListener {

        public void connectionDropped() {
            contest = null;
        }
    }
    
    
    /**
     * Start the contest clock.
     * 
     * If the contest clock is already started this has no effect.
     *
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws NotLoggedInException if the user is not logged in.
     * @throws Exception if unable to start contest.
     */
    public void startContestClock() throws Exception {
        
        checkWhetherLoggedIn();

        checkIsAllowed (Permission.Type.START_CONTEST_CLOCK, "User not allowed to start contest clock");

        try {
            controller.startContest(internalContest.getSiteNumber());
        } catch (Exception e) {
            throw new Exception("Unable to start Contest "+e.getLocalizedMessage());
        }
    }
    
//    /**
//     * Client side permission check.
//     * 
//     * @param permissionType
//     * @param message
//     * @throws Exception
//     */
//    protected void allowedTo(Type permissionType, String message) throws Exception {
//        Account account = getAccount(internalContest, internalContest.getClientId());
//
//        if (!account.isAllowed(permissionType)) {
//            throw new Exception(message +", user="+contest.getMyClient().getLoginName());
//        }
//        
//    }

    /**
     * Stop the contest clock.
     * 
     * Stops the contest clock, meaning that no further elapsed contest time will accrue.  No
     * new runs will be accepted when the contest clock is stopped.
     * 
     * @throws SecurityException if the user is not allowed to perform this action.
     * @throws NotLoggedInException if the user is not logged in.
     * @throws Exception if unable to stop contest
     */
    public void stopContestClock() throws Exception {
        checkWhetherLoggedIn();
        
        checkIsAllowed (Permission.Type.STOP_CONTEST_CLOCK, "User not allowed to start contest clock");
        
        try {
            controller.stopContest(internalContest.getSiteNumber());
        } catch (Exception e) {
            throw new Exception("Unable to stop Contest "+e.getLocalizedMessage());
        }
        
    }
    
    /**
     * Marks the specified Problem as being validated using the PC2 Validator with a default set of Settings values.
     * 
     * @param problem the problem to be marked as being validated by the PC2 Validator
     */
    protected void setPC2ValidatorDefaults(Problem problem) {

        problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
        
        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

        settings.setWhichPC2Validator(1);
        settings.setIgnoreCaseOnValidation(true);
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + settings.getWhichPC2Validator() + " "
                + settings.isIgnoreCaseOnValidation());
        
        problem.setPC2ValidatorSettings(settings);
    }
    
    /**
     * Marks the specified Problem as being validated using the CLICS Validator with a default set of Settings values.
     * 
     * @param problem the problem to be marked as being validated by the CLICS Validator
     */
    protected void setClicsValidatorDefaults(Problem problem) {

        problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        
        ClicsValidatorSettings settings = new ClicsValidatorSettings();
        
        settings.setValidatorCommandLine(Constants.DEFAULT_CLICS_VALIDATOR_COMMAND);
        settings.setValidatorProgramName(Constants.CLICS_VALIDATOR_NAME);
        
        problem.setCLICSValidatorSettings(settings);
    }
    
    /**
     * Marks the specified Problem as being validated using a Custom Validator with a default set of Settings values.
     * 
     * @param problem the problem to be marked as being validated by a Custom Validator
     */
    protected void setCustomValidatorDefaults(Problem problem) {

        problem.setValidatorType(VALIDATOR_TYPE.CUSTOMVALIDATOR);
        
        CustomValidatorSettings settings = new CustomValidatorSettings();
        
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);
        
        problem.setCustomValidatorSettings(settings);
    }
    
    /**
     * Add a Problem definition.
     * 
     * @see APIConstants
     * @param title
     *            - title for problem
     * @param shortName short name/id for problem.
     * @param judgesDataFile
     *            - judges input data file
     * @param judgesAnswerFile
     *            - judges answer file
     * @param validator
     *            - which validator to use (choices are: PC2VALIDATOR, CLICSVALIDATOR, CUSTOMVALIDATOR, NONE)
     * @param problemProperties
     *            - optional properties, for a list of keys see {@link #getProblemPropertyNames()}, null is allowed.
     */
    public void addProblem(String title, String shortName, File judgesDataFile, File judgesAnswerFile, VALIDATOR_TYPE validator, Properties problemProperties) {

        checkNotEmpty("Problem title", title);
        checkNotEmpty("Problem short name", shortName);
        checkFile("Judges data file", judgesDataFile);
        checkNotEmpty("Validator", validator.toString());
        
        checkIsAllowed(Type.ADD_PROBLEM);
        
        Problem problem = new Problem(title);
        problem.setShortName(shortName);
        problem.setDataFileName(judgesDataFile.getName());
        problem.setAnswerFileName(judgesAnswerFile.getName());

        /**
         * Check for valid property names.
         */
        String[] invalids = validateProperties (problemProperties);
        if (invalids.length > 0){
            throw new IllegalArgumentException("Unknown/Invalid property names: "+ Arrays.toString(invalids));
        }
        
        //get a judging type from the properties, or else default to null
        String judgingType = getProperty(problemProperties, APIConstants.JUDGING_TYPE, null);
        
        if (judgingType==null) {
            //null means we will default to manual judging, but we can't do that if a validator was specified
            if (!(validator==VALIDATOR_TYPE.NONE)) {
                throw new IllegalArgumentException("Problem cannot have a validator if no judging type is specified in the properties (because the default is 'manual judging')");
            } else {
                //no judging type specified; default to manual judging
                judgingType = APIConstants.MANUAL_JUDGING_ONLY;
            }
        }
        
        //when we get here, judgingType and validator are both known != null
        
        //we cannot have manual judging and also have a validator
        if (judgingType.equals(APIConstants.MANUAL_JUDGING_ONLY) && validator!=VALIDATOR_TYPE.NONE) {
            throw new IllegalArgumentException("Problem cannot have a validator when manual judging is specified (or defaulted to)");            
        }
        
        //if we DON'T have manual judging, we MUST have a validator
        if ( (!judgingType.equals(APIConstants.MANUAL_JUDGING_ONLY)) && (validator==VALIDATOR_TYPE.NONE) )  {
            throw new IllegalArgumentException("Problem cannot be specified as computer judged (i.e., 'not manual judged') unless a validator is specified");            
        }
        
        
        switch (judgingType) {
            case APIConstants.MANUAL_JUDGING_ONLY:
                problem.setManualReview(true);
                break;
            case APIConstants.COMPUTER_JUDGING_ONLY:
                problem.setComputerJudged(true);
                problem.setManualReview(false);
                break;
            case APIConstants.COMPUTER_AND_MANUAL_JUDGING:
                problem.setComputerJudged(true);
                problem.setManualReview(true);
                break;

            default:
                throw new IllegalArgumentException("Unknown "+APIConstants.JUDGING_TYPE+" '"+judgingType+"'");
        }
        
        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        
        String validatorProgram = getProperty(problemProperties, APIConstants.VALIDATOR_PROGRAM, APIConstants.PC2_VALIDATOR_PROGRAM);
        String validatorCommandLine = getProperty(problemProperties, APIConstants.VALIDATOR_COMMAND_LINE, APIConstants.DEFAULT_PC2_VALIDATOR_COMMAND);

        switch (validator) {
            case PC2VALIDATOR:
                // set default settings
                setPC2ValidatorDefaults(problem);
                // update desired settings
                problem.setOutputValidatorProgramName(validatorProgram);
                problem.setValidatorCommandLine(validatorCommandLine);
                break;
            case CLICSVALIDATOR:
                setClicsValidatorDefaults(problem);
                problem.setOutputValidatorProgramName(validatorProgram);
                problem.setValidatorCommandLine(validatorCommandLine);
                if (new File(validatorProgram).isFile()){
                    SerializedFile validatorFile = new SerializedFile(validatorProgram);
                    problemDataFiles.setOutputValidatorFile(validatorFile);
                }
               break;
            case CUSTOMVALIDATOR:
                setCustomValidatorDefaults(problem);
                problem.setOutputValidatorProgramName(validatorProgram);
                problem.setValidatorCommandLine(validatorCommandLine);
                if (new File(validatorProgram).isFile()){
                    SerializedFile validatorFile = new SerializedFile(validatorProgram);
                    problemDataFiles.setOutputValidatorFile(validatorFile);
                }
                break;
            case NONE:
                problem.setValidatorType(VALIDATOR_TYPE.NONE);
                break;
            default:
                throw new IllegalArgumentException("Unknown Validator Type: '" + validator + "'");
        }

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true); 
        
        /**
         * Add judge's info to problem data files.
         */
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile.getAbsolutePath()));
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(judgesAnswerFile.getAbsolutePath()));
                
        controller.addNewProblem(problem, problemDataFiles);
    }

    /**
     * Return the value for a specified property.
     * 
     * If problemProperties is null then will return null.
     * 
     * @param problemProperties the problem Propteries
     * @param key the key to look for
     * @param defaultValue the default value to return if the key is not found.
     * @return value if found, else returns defaultValue.
     */
    protected String getProperty(Properties problemProperties, String key, String defaultValue) {

        String value = null;

        if (problemProperties != null) {

            value = problemProperties.getProperty(key);
            if (value == null) {
                // not found use default value
                value = defaultValue;
            } // else use value from properties

        } // else return null;

        return value;
    }

    /**
     * Check property names against valid list of names.
     * 
     * @param properties properties to validate
     * @return array of invalid/unknown keys
     */
    protected String[] validateProperties(Properties properties) {

        if (properties == null) {
            // avoid NPE later.
            return new String[0];
        }

        ArrayList<String> unknownKeys = new ArrayList<String>();

        String[] names = getProblemPropertyNames();

        Set<Object> keys = properties.keySet();

        for (Object object : keys) {
            String key = (String) object;
            boolean found = false;
            for (String name : names) {
                if (name.equals(key)) {
                    found = true;
                }
            }
            if (!found) {
                unknownKeys.add(key);
            }

        }
        return (String[]) unknownKeys.toArray(new String[unknownKeys.size()]);
    }

    private void checkFile(String name, File file) {
        if (file == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(name + " does not exist");
        }
        if (file.length() == 0) {
            throw new IllegalArgumentException(name + " must be a non-zero byte (in length) file");
        }

    }

    /**
     * Check for null or empty strings, throw IllegalArgumentException if null or empty.
     * 
     * @param name
     * @param value
     */
    private void checkNotEmpty(String name, String value) {

        if (value == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(name + " cannot be an empty string (or all spaces) '" + value + "'");
        }

    }

    /**
     * Get a list of optional Problem settings names.
     * 
     * Returns a list of all property names that provide a way for additional configuration of a problem.
     * 
     * @see APIConstants
     * @return a list of property names from {@link APIConstants}.
     */
    public String[] getProblemPropertyNames() {
        return problemPropertyNames;
    }

    /**
     * Returns an indication of whether the specified name matches one of the currently-defined languages
     * for which "auto-fill" in the PC2 Admin GUI is supported.
     *  
     * @param name a String giving a language name
     * @return true if the given String matches one of the currently-defined Auto-Fill languages.
     */
    public boolean isValidAutoFillLangauageName(String name) {
        boolean valid = false;
        for (String langName : LanguageAutoFill.getLanguageList()) {
            if (langName.equals(name)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * Get a list of the currently-defined "Auto-fill" languages.
     * 
     * @return an array of Strings containing language names.
     */
    public String[] getAutoFillLanguageList() {
        return LanguageAutoFill.getLanguageList();
    }

    /**
     * Add a language.
     * 
     * The fill title name must be one of the names in the list.
     * The list of names can be retrieved using {@link #getAutoFillLanguageList()}.
     * 
     * @param autoFillTitleName a automatic language fill title name
     */
    public void addLanguage(String autoFillTitleName) {

        checkNotEmpty("Language Name/title", autoFillTitleName);

        if (!isValidAutoFillLangauageName(autoFillTitleName)) {
            throw new IllegalArgumentException("No such language name '" + autoFillTitleName + "'");
        }

        checkIsAllowed(Type.ADD_LANGUAGE);

        Language language = LanguageAutoFill.languageLookup(autoFillTitleName);
        controller.addNewLanguage(language);

    }

    private void checkIsAllowed(Type type) {
        checkIsAllowed(type, null);
    }
    
    private void checkIsAllowed(Type type, String message) {

        if (!internalContest.isAllowed(type)) {
            if (message == null){
                throw new SecurityException("Not allowed to "+ getPermissionDescription(type));
            } else {
                throw new SecurityException("Not allowed to " + message+ "(requires "+ getPermissionDescription(type)+" permission)");
            }
        }

    }
    
    /**
     * Are any of the specified permissions allowed?
     */
    private void checkIsAnyAllowed(Type[] types, String message) {

        boolean allowed = false;
        for (Type type : types) {
            if (internalContest.isAllowed(type)) {
                allowed = true;
            }

        }

        if (!allowed) {
            if (message == null) {
                throw new SecurityException("Not allowed to " + getPermissionDescription(types[0]));
            } else {
                throw new SecurityException("Not allowed to " + message + "(requires " + getPermissionDescription(types[0]) + " permission)");
            }
        }

    }

    private String getPermissionDescription(Type type) {
        return new Permission().getDescription(type);
    }
   
    /**
     * Add a language to the contest.
     * 
     * The following code snippet shows an example to add a language.
     * <pre>
     * String login = &quot;administrator2&quot;;
     * String password = &quot;administrator2&quot;;
     * try {
     *     ServerConnection serverConnection = new ServerConnection();
     *     IContest contest = serverConnection.login(login, password);
     * 
     *     String title = &quot;Ruby&quot;;
     *     String compilerCommandLine = &quot;ruby -c {:mainfile}&quot;;
     *     String executionCommandLine = &quot;ruby {:mainfile}&quot;;
     *     String executableMask = &quot;{:noexe}&quot;;
     *     boolean interpreted = true;
     * 
     *     serverConnection.addLanguage(title, compilerCommandLine, executionCommandLine, interpreted, executableMask);
     * 
     *     // may need to pause for a second or two for the language to be added.
     *     ILanguage[] languages = contest.getLanguages();
     *     System.out.println(&quot;Added language &quot; + languages[languages.length - 1].getName());
     * 
     *     serverConnection.logoff();
     * } catch (LoginFailureException e) {
     *     System.out.println(&quot;Could not login because &quot; + e.getMessage());
     * } catch (NotLoggedInException e) {
     *     System.out.println(&quot;Unable to execute API method&quot;);
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param title
     *            Display Name for the language, ex. Java
     * @param compilerCommandLine
     *            command to compile source code
     * @param executionCommandLine
     *            command to execute program
     * @param interpreted
     *            is this an interpreted language like Perl or Python
     * @param executableMask
     *            an expected output program name
     */
    public void addLanguage(String title, String compilerCommandLine, String executionCommandLine, boolean interpreted, String executableMask) {

        checkNotEmpty("Language Name/title", title);
        checkNotEmpty("Language compilation command", compilerCommandLine);
        checkNotEmpty("Language executable mask", executableMask);
        checkNotEmpty("Language execution comman lLine", executionCommandLine);

        Language language = new Language(title);

        language.setCompileCommandLine(compilerCommandLine);
        language.setInterpreted(interpreted);
        language.setExecutableIdentifierMask(executableMask);
        language.setProgramExecuteCommandLine(executionCommandLine);

        checkIsAllowed(Type.ADD_LANGUAGE);

        controller.addNewLanguage(language);
    }
    
    
    /**
     * Shutdown the PC2 server.
     * 
     * @see #shutdownAllServers()
     */
    public void shutdownServer(){

        checkIsAllowed(Type.SHUTDOWN_SERVER,"Shutdown local server");
        controller.sendShutdownSite(internalContest.getSiteNumber());
        
    }
    
    /**
     * Shutdown all servers (that is, the PC2 Servers at all connected contest sites).
     * 
     *  Will shutdown all servers connected to the current server, then
     *  shutdown the current server.
     *  
     *  @see #shutdownServer()
     */
    public void shutdownAllServers(){

        Type [] allowList = { Type.SHUTDOWN_ALL_SERVERS, Type.SHUTDOWN_SERVER };
        checkIsAnyAllowed(allowList, "Shutdown local server");
        controller.sendShutdownAllSites();
    }
    
    /**
     * Update/set contest clock.
     * 
     * <p>
     * Contest clock must be stopped to change contest length.
     * 
     * @see IContest#isContestClockRunning()
     * 
     * @param contestLengthSeconds
     *            number of seconds contest is long
     * @param contestElapsedSeconds
     *            number of seconds elapsed since start of contest
     * @param contestRemainingSeconds
     *            number of seconds until end of contest
     * @throws IllegalContestState thrown if contest clock is started.
     */
    public void setContestTimes(long contestLengthSeconds, long contestElapsedSeconds, long contestRemainingSeconds) throws IllegalContestState {

        checkIsAllowed(Type.EDIT_CONTEST_CLOCK);
        
        if (internalContest.getContestTime().isContestRunning()){
            throw new IllegalContestState("Cannot set contest times while contest clock is running/started");
        }

        if (contestLengthSeconds != (contestElapsedSeconds + contestRemainingSeconds)) {
            throw new IllegalArgumentException("Contest Length must equal elapsed plus remaining ( " + contestLengthSeconds + " != " + contestElapsedSeconds + " + " + contestRemainingSeconds + " )");
        }

        ContestTime newContestTime = internalContest.getContestTime();
        newContestTime.setContestLengthSecs(contestLengthSeconds);
        newContestTime.setElapsedSecs(contestElapsedSeconds);
        newContestTime.setRemainingSecs(contestRemainingSeconds);

        controller.updateContestTime(newContestTime);
    }

    /**
     * Change contest length.
     * 
     * Will adjust remaining time if necessary.
     * 
     * <p>
     * Contest clock must be stopped to change contest length.
     * 
     * @see IContest#isContestClockRunning()
     * 
     * @param contestLengthSeconds
     *            number of seconds contest is long
     * @throws IllegalContestState thrown if contest clock is started.
     */
    public void setContestLength(long contestLengthSeconds) throws IllegalContestState {
        ContestTime newContestTime = internalContest.getContestTime();
        long newRemain = contestLengthSeconds - newContestTime.getElapsedSecs();
        setContestTimes(contestLengthSeconds, newContestTime.getElapsedSecs(), newRemain);

    }
    
    /**
     * Main method for testing this class.  Constructs a ServerConnection object,
     * logs in to a PC2 Server, and uses the command arguments to manipulate the contest clock.
     *  
     * @param args an array of Strings containing arguments as follows:
     * <ul>
     *   <li>--help             display a help message and exit</li>
     *   <li>--login acct     the account with which to login</li>
     *   <li>--password pass  the account password</li>
     *   <li>--stop             stop the contest clock (if absent, "start clock" is assumed)</li>
     * </ul>
     */
    public static void main(String[] args) {
        
        
        String[] requireArguementArgs = { // 
                "--login", "--password",  //
        };
        
        ParseArguments parseArguments = new ParseArguments(args, requireArguementArgs);
        
        if (parseArguments.isOptPresent("--help")){
            System.out.println("Usage: ServerConnection [--help] --login LOGIN [--passowrd PASS] [--stop]");
            System.out.println("Purpose to start (default) pc2 server contest clock, or stop contest clock");
            System.exit(0);
        }
        
        boolean stopContest = parseArguments.isOptPresent("--stop");
        
        String login = parseArguments.getOptValue("--login");
        String password = parseArguments.getOptValue("--password");
        if (login == null){
            fatalError("Missing login name, use --help for usage");
        }
        if (password == null){
            password = "";
        }
        
        ServerConnection connection = new ServerConnection();
        try {
            IContest contest2 = connection.login(login, password);
            System.out.println("Logged in as " + contest2.getMyClient().getLoginName());
            System.out.println("Contest is running?  " + contest2.isContestClockRunning());
            if (stopContest) {
                System.out.println("Send Stop Contest");
                connection.stopContestClock();
            } else {
                System.out.println("Send Start Contest");
                connection.startContestClock();
            }
            Thread.sleep(1000);
            System.out.println("Contest is running?  " + contest2.isContestClockRunning());
            connection.logoff();
            System.out.println("Logged off");
            System.exit(0);

        } catch ( Exception e) {
            System.err.println("Login failure for login="+login + " "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns an indication of whether the specified {@link IFile} is valid or not.
     * An IFile is considered "valid" if it is not null, has a non-null, non-zero-length name,
     * and has more than zero bytes of data.
     * 
     * @param file the IFile to be checked for validity
     * 
     * @return true if the IFile is not null, has a file name that is not null and not empty, 
     *          and has greater than zero data bytes; false otherwise
     */
    private boolean validIFile(IFile file) {
        if (file==null || file.getFileName()==null || file.getFileName().equals("")) {
            return false;
        }
        if (file.getByteData().length<=0) {
            return false;
        }
        return true;
    }

    private static void fatalError(String string) {
        System.err.println(string);
        System.err.println("Program Halted");
        System.exit(43);
    }

//    /**
//     * Set the Contest and Controller to be used by the MockTestRunImplementation.
//     * This method is temporary and should only be used in support of Mock Test Run operations.
//     * 
//     * @param inContest the contest model used by the the MockTestRunImplementation
//     * @param inController the controller used by the MockTestRunImplementation
//     */
//    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
//        mockTestRunImplementation.setContestAndController(inContest, inController);
//    }
    
}
