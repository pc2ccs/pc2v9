package edu.csus.ecs.pc2.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.implementation.LanguageImplementation;
import edu.csus.ecs.pc2.api.implementation.ProblemImplementation;
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
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.validator.PC2ValidatorSettings;

/**
 * This class represents a connection to a PC<sup>2</sup> server. Instantiating the class creates a local {@link ServerConnection} object which can then be used to connect to the PC<sup>2</sup> server
 * via the {@link ServerConnection#login(String, String)} method. The PC<sup>2</sup> server must already be running, and the local client must have a <code>pc2v9.ini</code> file specifying valid
 * server connection information, prior to invoking {@link ServerConnection#login(String, String)} method.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
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
    
    private Contest contest = null;
    
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
     * <code>pc2v9.ini</code> must specify legitmate server connection information; otherwise, {@link edu.csus.ecs.pc2.api.exceptions.LoginFailureException} is thrown. See the PC<sup>2</sup> Contest
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
     * @throws LoginFailureException
     *             if login fails, the message contained in the exception will provide and indication of the reason for the failure.
     */
    public IContest login(String login, String password) throws LoginFailureException {

        /**
         * Was contest overriden/set rather than having to login to
         * instanciate the contest?
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
      * @throws Exception
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
     * Submit a clarification.
     * 
     * @param problem 
     * @param question text of question
     * @throws Exception
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

    private void checkWhetherLoggedIn() throws NotLoggedInException {
        if (contest == null || internalContest == null){
            throw new NotLoggedInException ("Not logged in");
        }
    }

    /**
     * Submit a run.
     * 
     * @param problem 
     * @param language
     * @param mainFileName
     * @param additionalFileNames
     * @param overrideSubmissionTimeMS an override elapsed time in ms, only works if contest information CCS test mode is set true.
     * @throws Exception
     */
    public void submitRun(IProblem problem, ILanguage language, String mainFileName, String[] additionalFileNames, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {

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
        
//        Problem submittedProblem = null;
//        Language submittedLanguage = null;
//
//        Problem[] problems = internalContest.getProblems();
//        for (Problem problem2 : problems) {
//            if (problem2.getDisplayName().equals(problem.getName())) {
//                submittedProblem = problem2;
//            }
//        }
//
//        Language[] languages = internalContest.getLanguages();
//        for (Language language2 : languages) {
//            if (language2.getDisplayName().equals(language.getName())) {
//                submittedLanguage = language2;
//            }
//        }
        
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
            controller.submitRun(submittedProblem, submittedLanguage, mainFileName, list, overrideSubmissionTimeMS, overrideRunId);
        } catch (Exception e) {
            throw new Exception("Unable to submit run " + e.getLocalizedMessage());
        }
    }

    /**
     * Logoff/disconnect from the PC<sup>2</sup> server.
     * 
     * @return true if logged off, else false.
     * @throws NotLoggedInException
     *             if attempt to logoff without being logged in
     */
    public boolean logoff() throws NotLoggedInException {

        if (contest == null) {
            throw new NotLoggedInException("Can not log off, not logged in");
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
     * A Connection Event used by ServerConnection.
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
     * @throws Exception 
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
    
    /**
     * Client side permission check.
     * 
     * @param permissionType
     * @param message
     * @throws Exception
     */
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
     * Stops the contest clock, no elapsed time will accrue.  No
     * new runs will be accepted.
     * 
     * 
     * @throws Exception 
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
    protected void setPC2Validator(Problem problem) {

        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator();
        
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
     * @param validated
     *            - is the problem validated using the pc2 internal validator?
     * @param problemProperties
     *            - optional properties, for a list of keys see {@link #getProblemPropertyNames()}, null is allowed.
     */
    public void addProblem(String title, String shortName, File judgesDataFile, File judgesAnswerFile, boolean validated, Properties problemProperties) {

        checkNotEmpty("Problem title", title);
        checkNotEmpty("Problem short name", shortName);
        checkFile("Judges data file", judgesDataFile);
        
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
        
        String judgingType = getProperty(problemProperties, APIConstants.JUDGING_TYPE, null);
        
        if (judgingType != null){
            /**
             * Cannot be manual judged and validated.
             */
            if (APIConstants.MANUAL_JUDGING_ONLY.equals(judgingType) && validated){
                throw new IllegalArgumentException("Problem cannot be validated and not judging type computer judged");
            }
        } else {
            judgingType = APIConstants.MANUAL_JUDGING_ONLY;
        }
        
        switch (judgingType) {
            case APIConstants.MANUAL_JUDGING_ONLY:
                validated = false;
                problem.setManualReview(true);
                break;
            case APIConstants.COMPUTER_JUDGING_ONLY:
                validated = true;
                problem.setComputerJudged(true);
                problem.setManualReview(false);
                break;
            case APIConstants.COMPUTER_AND_MANUAL_JUDGING:
                validated = true;
                problem.setComputerJudged(true);
                problem.setManualReview(true);
                break;

            default:
                throw new IllegalArgumentException("Unknown "+APIConstants.JUDGING_TYPE+" '"+judgingType+"'");
        }
        
        boolean usingPc2Validator = false;
        
        String validatorProgram = getProperty(problemProperties, APIConstants.VALIDATOR_PROGRAM, APIConstants.PC2_VALIDATOR_PROGRAM);
        usingPc2Validator = APIConstants.PC2_VALIDATOR_PROGRAM.equals(validatorProgram);
        
        if (validated){
            
            problem.setValidatedProblem(validated);
            
            String validatorCommandLine = getProperty(problemProperties, APIConstants.VALIDATOR_COMMAND_LINE, APIConstants.DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
            problem.setValidatorCommandLine(validatorCommandLine);
            
            if (usingPc2Validator) {
                setPC2Validator(problem);
            } // else add external validator later
            
        }

        problem.setShowValidationToJudges(false);
        problem.setHideOutputWindow(true); 
        
        /**
         * Add problem data files.
         */
        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
        problemDataFiles.setJudgesDataFile(new SerializedFile(judgesDataFile.getAbsolutePath()));
        problemDataFiles.setJudgesAnswerFile(new SerializedFile(judgesAnswerFile.getAbsolutePath()));
        
        if (validated && ! usingPc2Validator){
            // add external validator
            if (new File(validatorProgram).isFile()){
                SerializedFile validatorFile = new SerializedFile(validatorProgram);
                problemDataFiles.setValidatorFile(validatorFile);
            }
        }
        
        controller.addNewProblem(problem, problemDataFiles);
    }

    /**
     * Return value for property.
     * 
     * if problemProperties is null then will return null
     * 
     * @param problemProperties
     * @param key
     * @param defaultValue
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
     * @param properties
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
     * 
     * @return a list of language names.
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
     * Shutdown the server.
     * 
     * @see #shutdownAllServers()
     */
    public void shutdownServer(){

        checkIsAllowed(Type.SHUTDOWN_SERVER,"Shutdown local server");
        controller.sendShutdownSite(internalContest.getSiteNumber());
        
    }
    
    /**
     * Shutdown all servers.
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

    private static void fatalError(String string) {
        System.err.println(string);
        System.err.println("Program Halted");
        System.exit(43);
    }
    
}
