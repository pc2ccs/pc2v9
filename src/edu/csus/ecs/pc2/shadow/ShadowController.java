// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import edu.csus.ecs.pc2.clics.CLICSJudgementType;
import edu.csus.ecs.pc2.clics.CLICSJudgementType.CLICS_JUDGEMENT_ACRONYM;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class is essentially a <A href="https://en.wikipedia.org/wiki/Facade_pattern"><I>facade</i></a> for the
 * PC2 "Shadow Mode" facility, which can be used to "shadow" a remote Contest Control System -- that is, to
 * monitor the remote CCS by obtaining runs from it and computing standings in parallel for purposes of verification
 * of the remote system.
 * 
 * The class is instantiated with an {@link IInternalController} (a PC2 Controller for a local instance of a contest)
 * along with an {@link IInternalContest} (a PC2 Contest model).
 * It obtains, from the local PC2 Server, information on the remote system to be shadowed, and uses classes behind (comprising) 
 * the facade to obtain team submissions from the remote system.
 * It uses the provided PC2 controller to perform local operations as if teams on the remote CCS were submitting to the local PC2
 * CCS.
 * 
 * The class (facade) presumes that the remote system implements the 
 * <A href="https://clics.ecs.baylor.edu/index.php?title=Contest_API">CLICS Contest API</a>; 
 * this is how it communicates with the remote system to obtain team submissions. 
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowController {

    private IInternalContest localContest;
    private IInternalController localController;
    private Log log;

    private String remoteCCSURLString;

    private String remoteCCSLogin;

    private String remoteCCSPassword;

    private RemoteEventFeedMonitor monitor;

    
    public enum SHADOW_CONTROLLER_STATUS {
        
        SC_NEVER_STARTED("Shadow Controller has never been started"), 
        SC_STARTING("Shadow Controller is starting"), 
        SC_RUNNING("Shadow Controller is running"), 
        SC_STOPPING("Shadow Controller is stopping"), 
        SC_STOPPED("Shadow Controller is stopped"), 
        SC_CONNECTION_FAILED("Shadow Controller failes to connect to remote CCS"),
        SC_INVALID_REMOTE_CONFIG("Shadow Controller received an invalid or null configuration from the remote CCS"),
        SC_CONTEST_CONFIG_MISMATCH("Contest configuration received from remote CCS does not match local PC2 contest configuration"),
        SC_MONITOR_STARTUP_FAILED("Shadow Controller was unable to start a Remote CCS Monitor");
        
        private final String label;
        private SHADOW_CONTROLLER_STATUS(String label) {
            this.label=label;
        }
        public String getLabel() {
            return this.label;
        }
    }
    
    private SHADOW_CONTROLLER_STATUS controllerStatus = null ;
    private RemoteContestConfiguration remoteContestConfig;
    private Thread monitorThread;

    /**
     * a ContestInformation Listener
     * 
     * @author ICPC
     *
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            restartShadowIfNeeded(ci);
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            restartShadowIfNeeded(ci);
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            restartShadowIfNeeded(null);
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            restartShadowIfNeeded(ci);
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            restartShadowIfNeeded(ci);
        }
    }

    /**
     * Constructs a new ShadowController for the remote CCS specified by the data in the 
     * specified {@link IInternalContest} and {@link IInternalContest}. 
     * 
     * @param localContest a PC2 Contest to be used by the Shadow Controller
     * @param localController a PC2 Controller to be used by the Shadow Controller
     */
    public ShadowController(IInternalContest localContest, IInternalController localController) {

        this(localContest, localController, 
                localContest.getContestInformation().getPrimaryCCS_URL(),
                localContest.getContestInformation().getPrimaryCCS_user_login(),
                localContest.getContestInformation().getPrimaryCCS_user_pw());

    }

    /**
     * Constructs a new ShadowController for the remote CCS specified by the input parameters.
     * 
     * @param localContest a PC2 Contest to be used by the Shadow Controller
     * @param localController a PC2 Controller to be used by the Shadow Controller
     * @param remoteURL the URL of the remote CCS
     * @param remoteCCSLogin
     * @param remoteCCSPassword
     */
    public ShadowController(IInternalContest localContest, IInternalController localController, 
                            String remoteURL, String remoteCCSLogin, String remoteCCSPassword) {

        this.localContest = localContest;
        this.localController = localController;
        this.remoteCCSURLString = remoteURL;
        this.remoteCCSLogin = remoteCCSLogin;
        this.remoteCCSPassword = remoteCCSPassword;
        this.localContest.addContestInformationListener(new ContestInformationListenerImplementation());
        this.setStatus(SHADOW_CONTROLLER_STATUS.SC_NEVER_STARTED);
    }

    /**
     * This method starts Shadow Mode operations running.  
     * 
     * It first checks the validity of the URL string which was passed to the Shadow Controller when it was constructed;
     * if the URL is invalid then it logs a warning and returns false.
     * If the URL string is valid then it obtains an adapter with which to access
     * a remote CCS, then uses the adapter to fetch the remote contest configuration.   
     * It then compares the obtained (remote) contest configuration with the local contest configuration,
     * and if they match then it creates and starts a {@link RemoteRunMonitor} listening to the remote contest
     * specified by the remote contest URL provided when the class is constructed.
     * 
     * @return true if remote shadowing operations were started successfully; false if not
     */
    public boolean start() {

        log = getLog();
        
        log.info("Starting shadowing for URL '"+ remoteCCSURLString + "' using login '" + remoteCCSLogin + "'");
        System.out.println ("ShadowController: starting shadowing for URL '" + remoteCCSURLString 
                            + "' using login '" + remoteCCSLogin + "'");
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STARTING);
        
        //verify that the current "URL string" is a valid URL
        URL remoteCCSURL = null;
        try {
            remoteCCSURL = new URL(remoteCCSURLString);
        } catch (MalformedURLException e) {
            log.log(Level.WARNING, "Malformed Remote CCS URL: \"" + remoteCCSURLString + "\" ", e);
            e.printStackTrace();
            controllerStatus = SHADOW_CONTROLLER_STATUS.SC_CONNECTION_FAILED ;
            return false;
        }
        
        //get an adapter which connects to the remote Contest API
        IRemoteContestAPIAdapter remoteContestAPIAdapter = createRemoteContestAPIAdapter(remoteCCSURL, remoteCCSLogin, remoteCCSPassword);
        
//        //get a remote contest configuration from the adapter
//        remoteContestConfig  = remoteContestAPIAdapter.getRemoteContestConfiguration();
//                        
//        //make sure we got a remote config
//        if (remoteContestConfig != null) {
//            
//            //check if the local contest has the same config as the remote contest (the one being shadowed)
//            if (!remoteContestConfig.isSameAs(localContest)) {
//
//                //get the configuration differences
//                List<String> diffs = remoteContestConfig.diff(localContest);
//
//                //log the differences
//                log.log(Level.WARNING, "Local contest configuration does not match configuration of remote CCS; cannot proceed with shadowing");
//                logDiffs(log, diffs);
//
//                setStatus(SHADOW_CONTROLLER_STATUS.SC_CONTEST_CONFIG_MISMATCH);
//
//                return false;
//
//            }
//        } else {
//            //we didn't get a remote config
//            log.log(Level.WARNING, "Contest configuration from remote CCS is null; cannot proceed with shadowing");
// 
//            setStatus(SHADOW_CONTROLLER_STATUS.SC_INVALID_REMOTE_CONFIG);
//            
//            return false;
//        }
        
        
        //if we get here we know the remote contest configuration matches the local contest configuration
        
        //construct a RunSubmitter that can be used to submit runs (received from the remote contest) to the local PC2 contest
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(localController);

        try {
            
            //construct an EventFeedMonitor for keeping track of the remote CCS events
            log.info("Constructing new RemoteEventFeedMonitor");
            monitor = new RemoteEventFeedMonitor(localController, remoteContestAPIAdapter, remoteCCSURL, remoteCCSLogin, remoteCCSPassword, submitter);
 
            //start the monitor running as a thread listening for submissions from the remote CCS
            monitorThread = new Thread(monitor);
            monitorThread.start();
            
            log.info("RemoteEventFeedMonitor started");
            setStatus(SHADOW_CONTROLLER_STATUS.SC_RUNNING);
            return true;
           
            
        } catch (Exception e) {
            // TODO figure out how to return the exception to the caller cleanly
            log.severe("Exception starting RemoteEventFeedMonitor: " + e);
            setStatus(SHADOW_CONTROLLER_STATUS.SC_MONITOR_STARTUP_FAILED);

            e.printStackTrace();
            return false;
        }
                
    }

    private IRemoteContestAPIAdapter createRemoteContestAPIAdapter(URL url, String login, String password) {

        boolean useMockAdapter = StringUtilities.getBooleanValue(IniFile.getValue("shadow.usemockcontestadapter"), false);
        if (useMockAdapter)
        {
            return new MockContestAPIAdapter(url, login, password);
        } else {
            return new RemoteContestAPIAdapter(url, login, password);
        }
    }

    private boolean convertJudgementsToBig5 = true;
    private boolean shadowModeEnabled = false;
    
    /**
     * Returns a Map which maps submissionIDs to {@link ShadowJudgementInfo}s
     * containing information on the comparison of a specific submission by PC2 and 
     * the remote CCS.
     * 
     * Each {@link ShadowJudgementInfo} in the returned map contains the SubmissionID, TeamID, LanguageID, ProblemID, and
     * a {@link ShadowJudgementPair} map giving the PC2 Shadow and Remote CCS judgements.
     * 
     * If the property "convertJudgementsToBig5" is true, this method attempts to convert all judgements
     * to one of the so-called "CLICS Big-5" (the five judgement values defined for ICPC World Finals by the
     * CLICS specification at https://clics.ecs.baylor.edu/index.php?title=Contest_API#Judgement_Types).
     * This means that all judgements would be one of "AC", "WA", "TLE", "RTE", or "CE";  user-defined
     * judgements such as "OLE" (Output Limit Exceeded) would be converted to one of these "Big 5" 
     * using the methods defined in {@link CLICSJudgementType}.
     * 
     * This method is only operative while Shadow operations are running; if shadowing is not running
     * then the method returns null.
     * 
     * @return a Map mapping submissions to shadow judgement information, or null if shadowing isn't running
     */
    public Map<String, ShadowJudgementInfo> getJudgementComparisonInfo() {
        
        if (getStatus()!= SHADOW_CONTROLLER_STATUS.SC_RUNNING) {
//            log.warning("Shadow Controller 'getJudgementComparisonInfo()' called when Shadow controller is not running"); 
            return null;
        } else {
//            log.info("Constructing Shadow Judgement comparisons");
            
            //get a Map of the judgements assigned by the remote CCS to each submission; note that this map uses "remote event id"
            // as the key and combines the submission ID with the Judgement acronym, separated by a colon, as the value
            Map<String,String> remoteJudgementsMap = RemoteEventFeedMonitor.getRemoteJudgementsMap();
            
            //convert the Map to one with submission ID as key and acronym (judgement) as value
            Map<String,String> remoteSubmissionsJudgementMap = new HashMap<String,String>();
            for (String key : remoteJudgementsMap.keySet()) {
                String value = remoteJudgementsMap.get(key) ;
                String submissionID = value.substring(0, value.lastIndexOf(':'));
                String judgementAcronym = value.substring(value.lastIndexOf(':')+1);
                remoteSubmissionsJudgementMap.put(submissionID, judgementAcronym);
            }
            
            //if specified, convert remote judgements to "Big 5"
            if (isConvertJudgementsToBig5()) {
//                log.info("Converting remote judgements to CLICS 'Big 5'");
                convertMapToBig5(remoteSubmissionsJudgementMap);
            }
            
            //create a Map of the judgements assigned by PC2
            Run[] runs = localContest.getRuns();
            
//            //debug loop
//            for (Run run : runs) {
//                if (!run.isJudged()) {
//                    log.warning("Found unjudged run in PC2 Shadow system: " + run);
//                    System.err.println ("Found unjudged run in PC2 Shadow system: ");
//                    System.err.println ("  " + run);
//                }
//            }
            
            //build a map of PC2 Shadow judgements, mapping submissionID to judgement acronym for each submission
            Map<String,String> pc2JudgementsMap = new HashMap<String,String>();
            for (Run run : runs) {
                
                if (run.isJudged()) {
                    
                    JudgementRecord jr = run.getJudgementRecord();
                    
                    if (jr != null) {

                        String judgementString;

                        if (jr.isUsedValidator() && jr.getValidatorResultString() != null) {
                            judgementString = jr.getValidatorResultString();
                        } else {
                            //no validator result; fall back to using judgementId (remembering that this
                            // defaults to "RTE" for all "no" judgements -- see V9 bug list)
                            ElementId judgementId = jr.getJudgementId();
                            Judgement judgement = localContest.getJudgement(judgementId);
                            judgementString = judgement.getDisplayName();
                            if (judgementString.startsWith("No - ")) {
                                judgementString = judgementString.substring(5); //strip off the "No - "
                            }
                        }

                        if (judgementString==null) {
//                            log.warning("null judgement string for run " + run.getNumber());
                            System.err.println ("null judgement string in ShadowController.getJudgementComparisonInfo() for run " + run.getNumber());
                        }
                        
                        //at this point we have the judgement string text; try to convert it to a corresponding acronym
                        CLICS_JUDGEMENT_ACRONYM acronym = CLICSJudgementType.getCLICSAcronym(judgementString);
                        
                        if (acronym==null) {
                            //we couldn't find a CLICS judgement matching the string; 
                            //try to use the judgement record (which may be incorrect; see V9 bug list regarding defaulting to RTE)
                            ElementId judgementId = jr.getJudgementId();
                            Judgement judgement = localContest.getJudgement(judgementId);
                            judgementString = judgement.getDisplayName();
                            if (judgementString.startsWith("No - ")) {
                                judgementString = judgementString.substring(5); //strip off the "No - "
                            }
                            
                            acronym = CLICSJudgementType.getCLICSAcronym(judgementString);
                            
                        }

                        if (acronym!=null) {
                            //put the judgement acronym into the pc2Judgements map under the submissionID
                            String submissionID = String.valueOf(run.getNumber());
                            pc2JudgementsMap.put(submissionID, acronym.name());
                            
                        } else { 
                            //we've exhausted methods of obtaining an acronym
//                            log.warning("null acronym for run " + run.getNumber() + ", judgement string " + judgementString);
                            System.err.println ("null acronym in ShadowController.getJudgementComparision() for "
                                    + "run " + run.getNumber() + ", judgement string " + judgementString);
                        }

                    } else {
                        //we got a null judgment record from the run, but it's supposedly been judged -- error!
//                        log.severe("Error: found a (supposedly) judged run with no JudgementRecord!");
                        System.err.println ("Error in getJudgementComparisonInfo(): found a (supposedly) judged run with no JudgementRecord!");
                    }                    
                    
                } else {
                    //we have an as-yet unjudged run
                    String submissionID = String.valueOf(run.getNumber());
                    pc2JudgementsMap.put(submissionID, "<pending>");
                }
                
            }//end for each run

            //if specified, convert PC2 judgements to "Big 5"
            if (isConvertJudgementsToBig5()) {
//                log.info("Converting PC2 judgements to 'CLICS Big 5'");
                convertMapToBig5(pc2JudgementsMap);
            }
            
            //verify that we have corresponding maps (from the remote vs. the local systems)
            Set<String> remoteKeys = remoteSubmissionsJudgementMap.keySet();
            Set<String> localKeys = pc2JudgementsMap.keySet();
            if (!remoteKeys.equals(localKeys)) {
//                log.warning("Contents of remote judgements map does not match that of local PC2 judgements map"
//                        + " (this could happen if PC2 is not keeping up with remote submissions)");
                System.err.println("Note: contents of remote judgements map does not match that of local PC2 judgements map"
                        + " (this could happen if PC2 is not keeping up with remote submissions)");
            }
            
            //construct a single map containing the ShadowJudgementInfo for each submission ("run" in PC2 terms)
            Map<String,ShadowJudgementInfo> judgementsMap = new HashMap<String,ShadowJudgementInfo>();
            
            //first put into the combined map the judgements from the remote system, with the corresponding PC2 value
            //   (which could be null if PC2 doesn't have a judgement for the corresponding submission)
            String teamID;
            String problemID;
            String languageID;
            
            for (String submissionID : remoteKeys) {
                
                //get the run corresponding to the current submissionID
                Run run = getRun(runs,submissionID);
                
                //get the team/problem/language info corresponding to the run
                teamID = new Integer(run.getSubmitter().getClientNumber()).toString();
                
                ElementId probElementID = run.getProblemId();
                problemID = localContest.getProblem(probElementID).getShortName();
                
                ElementId langElementID = run.getLanguageId();
                languageID = localContest.getLanguage(langElementID).getID();

                ShadowJudgementPair pair = new ShadowJudgementPair(submissionID, pc2JudgementsMap.get(submissionID), 
                                                    remoteSubmissionsJudgementMap.get(submissionID));
                
                System.out.println("Debug: adding to judgementsMap: ") ;
                System.out.println ("  submissionID=" + submissionID + " teamID=" + teamID + " problemID=" 
                        + problemID + " languageID=" + languageID + " pc2Judgement=" + pc2JudgementsMap.get(submissionID)
                        + " remoteJudgement=" + remoteSubmissionsJudgementMap.get(submissionID));
                
                ShadowJudgementInfo info = new ShadowJudgementInfo(submissionID, teamID, problemID, languageID, pair);
                
                judgementsMap.put(submissionID, info);
            }
            
            //now add judgements from the PC2 map that might not have existed in the remote map
            for (String submissionID : localKeys) {
                if (!remoteSubmissionsJudgementMap.containsKey(submissionID)) {
                    
                    //get the run corresponding to the current submissionID
                    Run run = getRun(runs,submissionID);
                                       
//                    teamID = run.getSubmitter().toString();
//                    problemID = run.getProblemId().toString();
//                    languageID = run.getLanguageId().toString();
                    
                    //get the team/problem/language info corresponding to the run
                    teamID = new Integer(run.getSubmitter().getClientNumber()).toString();
                    
                    ElementId probElementID = run.getProblemId();
                    problemID = localContest.getProblem(probElementID).getShortName();
                    
                    ElementId langElementID = run.getLanguageId();
                    languageID = localContest.getLanguage(langElementID).getID();
                    
                    ShadowJudgementPair pair = new ShadowJudgementPair(submissionID, pc2JudgementsMap.get(submissionID), 
                                 remoteSubmissionsJudgementMap.get(submissionID)); //the remote map will always return null
                    
                    System.out.println("Debug: adding to judgementsMap: ") ;
                    System.out.println ("  submissionID=" + submissionID + " teamID=" + teamID + " problemID=" 
                            + problemID + " languageID=" + languageID + " pc2Judgement=" + pc2JudgementsMap.get(submissionID)
                            + " remoteJudgement=" + remoteSubmissionsJudgementMap.get(submissionID));
                    
                    
                    ShadowJudgementInfo info = new ShadowJudgementInfo(submissionID, teamID, problemID, languageID, pair);
                                        
                    judgementsMap.put(submissionID, info);
                }
            }
                        
            return judgementsMap;
        }
        
    }
    
    /**
     * Searches the given array of Runs and returns the run, if any, whose run number matches the specified
     * submissionId; otherwise returns null.
     * 
     * @param runs an array of Runs to be searched
     * @param submissionID the id of the desired run
     * 
     * @return the Run matching the specified submissionID, or null if no Run matches
     */
    private Run getRun(Run[] runs, String submissionID) {
        
        for (Run nextRun : runs) {
            String runNumberString = new Integer(nextRun.getNumber()).toString();
            if (runNumberString.equalsIgnoreCase(submissionID)) {
                return nextRun;
            }
        }
        return null;
    }
    
    /**
     * Returns a list of differences between the currently-configured remote contest
     * and the configuration of the local PC2 contest, or null if no remote contest configuration
     * has been obtained.
     * 
     * @return a List<String> of contest configuration differences, or null if no remote configuration is available
     */
    public List<String> getConfigurationDiffs() {
        
        List<String> diffs = null;
        if (remoteContestConfig != null) {
            diffs = remoteContestConfig.diff(localContest);
        }
        return diffs ;
    }
    
    /**
     * This method writes the given list of differences between the local and remote contest configurations 
     * into the specified log.
     * 
     * @param diffs a List<String> giving the configuration differences
     */
    @SuppressWarnings("unused")
    private void logDiffs(Log log, List<String> diffs) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    /**
     * This method stops the Shadow Mode listener thread.  
     */
    public void stop() {
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STOPPING) ;
        
        if (monitor!=null) {
            
            monitor.stop();
            
            //garbage-collect the monitor
            monitor = null;        
        }
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STOPPED) ;
    }

    /**
     * Returns the current status of this ShadowController.
     * 
     * @return a SHADOW_CONTROLLER_STATUS enum element giving the controller status
     */
    public SHADOW_CONTROLLER_STATUS getStatus() {
        return controllerStatus;
    }

    /**
     * Updates the ShadowController status.
     * 
     * @param controllerStatus the controllerStatus to set
     */
    protected void setStatus(SHADOW_CONTROLLER_STATUS controllerStatus) {
        this.controllerStatus = controllerStatus;
    }
    
    /**
     * Returns the indicator of whether judgements should be restricted to "Big 5" form.
     * The purpose of this method (rather than simply accessing the boolean variable)
     * is to support future extensions which may obtain this property from elsewhere.
     * 
     * TODO:  add a checkbox to the Shadow GUI which allows setting/saving this property;
     *          return the value of that flag instead of a local variable.
     * @return the flag indicating whether judgements should be restricted/converted to 
     *              CLICS "Big 5" judgements
     */
    private boolean isConvertJudgementsToBig5() {
        return convertJudgementsToBig5 ;
    }
    
    /**
     * Converts the given map so that all judgement acronyms are CLICS "Big 5" acronyms.
     * 
     * @param map the map to be converted
     */
    private void convertMapToBig5 (Map<String,String> map) {
        //process each submission judgement from the remote CCS
        for (String submissionID : map.keySet()) {
            String judgementAcronym = map.get(submissionID) ;
            //construct a CLICSJudgement for the acronym
            CLICSJudgementType clicsJudgement = new CLICSJudgementType(judgementAcronym, "dummy", false, false);
            //check if the judgement is already a "Big 5"
            if (!clicsJudgement.isBig5()) {
                //no it's not a Big 5; get a corresponding Big 5 acronym in the map
                String newAcronym = clicsJudgement.getBig5EquivalentAcronym();
                //the above method returns null if there is no matching Big 5 acronym
                if (newAcronym==null) {
                    newAcronym = "<NoBig5:" + judgementAcronym + ">";
                }
                map.put(submissionID, newAcronym);
            }
        }

    }

    public Log getLog() {
        if (log == null) {
            log = localController.getLog();
        }
        return log;
    }

    private void showMessage(String message) {
        System.out.println(new Date() + " " +message);
//        getLog().info(message);
    }

    private void updateCached(ContestInformation ci) {
        if (ci != null) {
            shadowModeEnabled  = ci.isShadowMode();
            remoteCCSURLString = ci.getPrimaryCCS_URL();
            remoteCCSLogin = ci.getPrimaryCCS_user_login();
            remoteCCSPassword = ci.getPrimaryCCS_user_pw();
        } else {
            // there is no ContestInformation, reset to defaults
            shadowModeEnabled = false;
            remoteCCSURLString = "";
            remoteCCSLogin = "";
            remoteCCSPassword = "";
        }
    }

    /**
     * 
     * @param ci
     * @return True if shadow server is started (including restarted), else False.
     */
    private boolean restartShadowIfNeeded(ContestInformation ci) {
        boolean result = false;
        if (ci != null && ci.isShadowMode()) {
            if (shadowModeEnabled) {
                // we are suppose to be running, and we were running
                // check for differences
                String message = "";
                if (ci.getPrimaryCCS_URL() != remoteCCSURLString) {
                    message = "shadowRemoteURL changed from " + remoteCCSURLString + " to " + ci.getPrimaryCCS_URL() + "\n";
                }
                if (ci.getPrimaryCCS_user_login() != remoteCCSLogin) {
                    message = message + "shadowLogin changed from " + remoteCCSLogin + " to " + ci.getPrimaryCCS_user_login() + "\n";
                }
                if (ci.getPrimaryCCS_user_pw() != remoteCCSPassword) {
                    message = message + "shadow_user_password changed from " + remoteCCSPassword + " to " + ci.getPrimaryCCS_user_pw() + "\n";
                }
                if (message != "") {
                    message = message + "Restarting shadow controller";
                    showMessage(message);
                    stop();
                    updateCached(ci);
                    start();
                }
            } else {
                // we are not running, but we are suppose to be running
                if (!getStatus().equals(SHADOW_CONTROLLER_STATUS.SC_RUNNING)) {
                    showMessage("Starting shadow controller");
                    updateCached(ci);
                    start();
                    result = true;
                }
            }
        } else {
            // check if we were running (but we should not be)
            if (!ci.isShadowMode() && getStatus().equals(SHADOW_CONTROLLER_STATUS.SC_RUNNING)) {
                // stop it
                showMessage("Stopping shadow controller");
                updateCached(ci);
                stop();
            }
        }
        return (result);
    }
}
