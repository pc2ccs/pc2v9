// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.csus.ecs.pc2.clics.CLICSJudgementType;
import edu.csus.ecs.pc2.clics.CLICSJudgementType.CLICS_JUDGEMENT_ACRONYM;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.services.core.ScoreboardJson;
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
 * the facade to obtain team submissions as well as scoreboard information from the remote system.
 * It uses the provided PC2 controller to perform local operations as if teams on the remote CCS were submitting to the local PC2
 * CCS.
 * 
 * The class (facade) presumes that the remote system implements the 
 * <A href="https://clics.ecs.baylor.edu/index.php?title=Contest_API">CLICS Contest API</a>; 
 * this is how it communicates with the remote system to obtain team submissions and remote scoreboard information. 
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
    private IRemoteContestAPIAdapter remoteContestAPIAdapter;

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
        remoteContestAPIAdapter = createRemoteContestAPIAdapter(remoteCCSURL, remoteCCSLogin, remoteCCSPassword);
        
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

            if (! remoteContestAPIAdapter.testConnection()){
                
                return false;
            }
            
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
            log.info("Constructing Shadow Judgement comparisons");
                       
            //get the runs (submissions) currently in PC2 (i.e. runs already received from the remote CCS and submitted to the PC2 server)
            Run[] pc2Runs = localContest.getRuns();
                        
            //get a map from submissionID to ShadowJudgementInfo for each submission which PC2 knows about
            Map<String,ShadowJudgementInfo> judgementsMap = getJudgementsMap(pc2Runs);
            
            //At this point we have a Map ("judgementsMap") which maps every submissionId that PC2 knows about to a ShadowJudgementInfo object
                        
            //get the submission judgements which have been reported by the remote CCS
            Map<String,String> remoteSubmissionsToJudgementsMap = getRemoteSubmissionsToJudgementsMap();
                        
            //check every submission reported to have a judgement by the remote CCS
            for (String key : remoteSubmissionsToJudgementsMap.keySet()) {
                
                //check if PC2 has seen this submission (it might not have yet due to lags with RemoteEventFeedMonitor submitting to PC2)
                if (judgementsMap.containsKey(key)) {
                    
                    //we found a submission, known to PC2, for which the remote CCS has reported a judgement; update the remote judgement info under that key
                    ShadowJudgementInfo info = judgementsMap.get(key);
                    ShadowJudgementPair pair = info.getShadowJudgementPair();
                    pair.setRemoteCCSJudgement(remoteSubmissionsToJudgementsMap.get(key));
                }
            }
 
            //we now have a complete map from submissions known to PC2 to ShadowJudgementInfo objects containing known PC2 judgements and known remote CCS judgements
            
            //if specified, convert judgements to "Big 5"
            if (isConvertJudgementsToBig5()) {
                log.info("Converting judgements to 'CLICS Big 5'");
                convertMapToBig5(judgementsMap);
            }
            
            return judgementsMap;
        }
        
    }
    
 
    /**
     * Returns an array of {@link ShadowScoreboardRowComparison}s, each element of which contains a comparison of
     * corresponding rows of the current PC2 and remote CCS scoreboards.
     * 
     * Each ShadowScoreboardRowComparison contains a pair of {@link TeamScoreRow}s, one each from the PC2 and remote CCS
     * scoreboards, along with a boolean flag indicating whether the rows are equal or not (that is, whether or not the rows
     * contain the same rank, teamId, number of problems solved, and total (penalty) points).  If for some reason a row exists
     * in one of the scoreboards but not the other, the corresponding entry in the ShadowScoreboardRowComparison will be null 
     * (an indication of scoreboards which inherently are not equal).
     * 
     * This method is only operative while Shadow operations are running; if shadowing is not running
     * then the method returns an empty array (that is, an array of size zero).
     * 
     * The method also returns an empty array if an error occurs when fetching either the PC2 or the remote CCS 
     * JSON scoreboard  strings, or when parsing those strings.
     * 
     * @return an array of ShadowScoreboardRowComparison information, or an empty array if shadowing isn't running
     *          or an error occurs during processing either of the JSON strings representing the two scoreboards.
     */
    public ShadowScoreboardRowComparison[] getScoreboardComparisonInfo() {

        //initialize return to empty
        ShadowScoreboardRowComparison[] emptyArray = new ShadowScoreboardRowComparison[0];

        if (getStatus() != SHADOW_CONTROLLER_STATUS.SC_RUNNING) {
            log.warning("Shadow Controller 'getScoreboardComparisonInfo()' called when Shadow controller is not running");
            return emptyArray;
        }

        // get the PC2 scoreboard JSON
        String pc2Json = getPC2ScoreboardJSON();
        if (pc2Json==null) {
            log.warning("Got empty or null JSON scoreboard from PC2");
            return emptyArray;
        }

        // get the remote CCS scoreboard JSON
        String remoteJson = getRemoteScoreboardJSON();
        if (remoteJson==null) {
            log.warning("Got empty or null JSON scoreboard from remote CCS");
            return emptyArray;
        }

        // construct a comparator to compare the two JSON strings
        ShadowScoreboardComparisonGenerator comparator = new ShadowScoreboardComparisonGenerator(this);

        //use the comparator to obtain a row-by-row comparison of the two JSON scoreboards
        ShadowScoreboardRowComparison[] results = comparator.compare(pc2Json, remoteJson);

        return results;
    }

   /**
     * This method constructs a PC2 {@link ScoreboardJson} object and uses it to return a String
     * containing the current PC2 scoreboard in CLICS Contest API format. 
     * 
     * @return a String containing the PC2 scoreboard JSON.
     */
    private String getPC2ScoreboardJSON() {
        
        ScoreboardJson sbJsonObject = new ScoreboardJson();
        String pc2Json ;
        try {
            pc2Json = sbJsonObject.createJSON(localContest, log);
        } catch (JsonProcessingException | IllegalContestState | JAXBException e) {
            log.warning("Exception creating PC2 scoreboard JSON: " + e.getMessage());
            return null;
        }
        
        return pc2Json;
    }

    /**
     * This method returns a String containing the current remote CCS scoreboard as obtained from the
     * remote CCS Contest API "/scoreboard" endpoint. 
     * 
     * @return a String containing the remote CCS scoreboard JSON.
     */
    private String getRemoteScoreboardJSON() {
        
        //get scoreboard from remoteAPIAdaptor
        String remoteJson = remoteContestAPIAdapter.getRemoteJSON("/scoreboard");
        
        //return scoreboard
        return remoteJson;
        
    }

    /**
     * This method returns a String containing the current PC2 team list as obtained from the
     * PC2 server, converted into CLICS Contest API "/teams" endpoint format 
     * (see https://ccs-specs.icpc.io/contest_api#teams). 
     * 
     * @return a String containing the PC2 team list JSON.
     */
    public String getPC2TeamsJSON() {
        
        log.info("Fetching PC2 team JSON");
        
        //get the PC2 team account list from the InternalContest model
        Vector<Account> teamAccounts = localContest.getAccounts(Type.TEAM);
        
        //open the return string as an array of teams
        String retJson = "["; 
        
        //convert PC2 team accounts into CLICS JSON format (see https://ccs-specs.icpc.io/contest_api#teams)
        boolean first = true;
        for (Account teamAcct : teamAccounts) {
            
            //get the required values out of the team account
            ClientId clientId = teamAcct.getClientId();
            String teamId = "" + clientId.getClientNumber();
            String teamName = teamAcct.getDisplayName();
            
            //add this team element to the return JSON, preceded by a comma if it's not the first element
            if (!first) {
                retJson += ",";
            }
            retJson += "{" ;
            retJson += "\"id\":\"" + teamId + "\"";
            retJson += ",";
            retJson += "\"name\":\"" + teamName + "\"";
            retJson += "}";
            
            first = false ;
        }
        
        //close the array of teams
        retJson += "]";
                
        //return the PC2 teams JSON
        return retJson;
    }


    /**
     * This method returns a String containing the current remote CCS team list as obtained from the
     * remote CCS Contest API "/teams" endpoint. 
     * 
     * @return a String containing the remote CCS team list JSON.
     */
    public String getRemoteTeamsJSON() {
        
        log.info("Fetching Remote CCS team JSON");
        
        //get team list from remoteAPIAdaptor
        String remoteJson = remoteContestAPIAdapter.getRemoteJSON("/teams");
                
        //return team list
        return remoteJson;
        
    }

   /**
     * Returns a Map which maps remote CCS submissionIds to a judgement acronym.
     * 
     * Any judgements from the Remote CCS which are null, which have values which are null or empty, which 
     * have submissionIds which are null or empty, or which have judgement acronyms which are null or empty,
     * are silently ignored (not represented in the map returned by this method).
     * 
     * @return a Map with entries mapping a submissionId String to an acronym String.
     */
    private Map<String, String> getRemoteSubmissionsToJudgementsMap() {
        
        //get a copy of the RemoteEventFeedMonitor's Judgements map, which maps "judgementIds" to a String of the form "submissionId:acronym"
        Map<String, String> remoteJudgementsMap = RemoteEventFeedMonitor.getRemoteJudgementsMapSnapshot();

        // convert the RemoteEFMonitor Map to one with submission ID as key and acronym (judgement) as value
        Map<String, String> remoteSubmissionsToJudgementsMap = new HashMap<String, String>();
        for (String key : remoteJudgementsMap.keySet()) {
            if (key != null) {
                String value = remoteJudgementsMap.get(key);
                if (value != null) {
                    String submissionID = value.substring(0, value.lastIndexOf(':'));
                    if (submissionID != null && !submissionID.contentEquals("")) {
                        String judgementAcronym = value.substring(value.lastIndexOf(':') + 1);
                        if (judgementAcronym != null && !judgementAcronym.contentEquals("")) {
                            remoteSubmissionsToJudgementsMap.put(submissionID, judgementAcronym);
                        }
                    }
                }
            }
        }
        return remoteSubmissionsToJudgementsMap;
    }
    
    
    /**
     * Returns a Map which maps submissionIds to {@link ShadowJudgementInfo} objects for every submission (run) in the
     * specified array of PC2 Runs.
     */
    private Map<String, ShadowJudgementInfo> getJudgementsMap(Run[] pc2Runs) {

        Map<String, ShadowJudgementInfo> pc2JudgementInfoMap = new HashMap<String, ShadowJudgementInfo>();

        // check each PC2 run (submission)
        for (Run run : pc2Runs) {

            // avoid any "null" runs which might be returned in the PC2 RunList
            if (run != null) {

                //get the submissionId of the run (basically, the Run number)
                String submissionId = String.valueOf(run.getNumber());

                // get the team/problem/language info corresponding to the run
                String teamID = new Integer(run.getSubmitter().getClientNumber()).toString();

                ElementId probElementID = run.getProblemId();
                String problemID = localContest.getProblem(probElementID).getShortName();

                ElementId langElementID = run.getLanguageId();
                String languageID = localContest.getLanguage(langElementID).getID();

                if (run.isJudged()) {

                    // get the judgement assigned to the run by PC2
                    JudgementRecord jr = run.getJudgementRecord();

                    if (jr != null) {

                        // try to determine the judgement string assigned to the run
                        String judgementString;

                        if (jr.isUsedValidator() && jr.getValidatorResultString() != null) {
                            judgementString = jr.getValidatorResultString();
                        } else {
                            // no validator result; fall back to using judgementId (remembering that this
                            // defaults to "RTE" for all "no" judgements -- see V9 bug list)
                            ElementId judgementId = jr.getJudgementId();
                            Judgement judgement = localContest.getJudgement(judgementId);
                            judgementString = judgement.getDisplayName();
                            if (judgementString.startsWith("No - ")) {
                                judgementString = judgementString.substring(5); // strip off the "No - "
                            }
                        }

                        if (judgementString == null) {
                            log.warning("Null judgement string for run " + run.getNumber());
                        }

                        // try to convert the judgement string text to a corresponding acronym (returns null if judgementString is null or not found)
                        CLICS_JUDGEMENT_ACRONYM acronym = CLICSJudgementType.getCLICSAcronym(judgementString);

                        if (acronym == null) {
                            // we couldn't find a CLICS judgement matching the string;
                            // try to use the judgement record (which may be incorrect; see V9 bug list regarding defaulting to RTE)
                            ElementId judgementId = jr.getJudgementId();
                            Judgement judgement = localContest.getJudgement(judgementId);
                            judgementString = judgement.getDisplayName();
                            if (judgementString.startsWith("No - ")) {
                                judgementString = judgementString.substring(5); // strip off the "No - "
                            }

                            acronym = CLICSJudgementType.getCLICSAcronym(judgementString);

                        }

                        if (acronym != null) {

                            // assign PC2 judgement, plus a "pending" for remote judgement (to be filled in later)
                            ShadowJudgementPair pair = new ShadowJudgementPair(submissionId, acronym.name(), "<pending>");

//                            System.out.print("Debug: adding to judgementsMap: ");
//                            System.out.println("  submissionID=" + submissionId + " teamID=" + teamID + " problemID=" + problemID + " languageID=" + languageID 
//                                    + " pc2Judgement=" + acronym.name() + " remoteJudgement=" + "<pending>");

                            ShadowJudgementInfo info = new ShadowJudgementInfo(submissionId, teamID, problemID, languageID, pair);

                            pc2JudgementInfoMap.put(submissionId, info);

                        } else {
                            // we've exhausted methods of obtaining an acronym
                            log.warning("Null judgement acronym for run " + run.getNumber() + ", judgement string " + judgementString + "; skipping");
                        }

                    } else {
                        // we got a null judgment record from the run, but it's supposedly been judged -- error!
                        log.severe("Error: found a (supposedly) judged PC2 run with no PC2 JudgementRecord!" + " (Submission id = " + run.getNumber() + ")");
                    }

                } else {
                    
                    // we have a run which has not yet been judged by PC2; assign "pending" for both the PC2 and remote judgement
                    ShadowJudgementPair pair = new ShadowJudgementPair(submissionId, "<pending>", "<pending>");
                    ShadowJudgementInfo info = new ShadowJudgementInfo(submissionId, teamID, problemID, languageID, pair);
                    
//                    System.out.print("Debug: adding to judgementsMap: ");
//                    System.out.println("  submissionID=" + submissionId + " teamID=" + teamID + " problemID=" + problemID + " languageID=" + languageID 
//                            + " pc2Judgement=" + "<pending>" + " remoteJudgement=" + "<pending>");
                    
                    pc2JudgementInfoMap.put(submissionId, info);
                }

            } else {
                log.warning("Encountered null run in PC2 RunList; skipping");
            }

        } // end for each PC2 run
                        
        return pc2JudgementInfoMap;
        
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
    
//    /**
//     * Converts the given map so that all judgement acronyms are CLICS "Big 5" acronyms if a corresponding CLICS Big 5 acronym 
//     * can be found.  If no corresponding acronym can be found, then a check is made to see if the initial acronym contains the
//     * substring "pending"; if so, the original acronym is retained, if not then the acronym is replaced with a string of the form
//     * "<NoBig5:xxx>" where "xxx" is the original acronym.
//     * 
//     * @param map the map to be converted
//     */
//    private void convertMapToBig5 (Map<String,String> map) {
//        //process each submission in the specified map
//        for (String submissionID : map.keySet()) {
//            String judgementAcronym = map.get(submissionID) ;
//            //construct a CLICSJudgement for the acronym
//            CLICSJudgementType clicsJudgement = new CLICSJudgementType(judgementAcronym, "dummy", false, false);
//            //check if the judgement is already a "Big 5"
//            if (!clicsJudgement.isBig5()) {
//                //no it's not a Big 5; get a corresponding Big 5 acronym in the map
//                String newAcronym = clicsJudgement.getBig5EquivalentAcronym();
//                //the above method returns null if there is no matching Big 5 acronym
//                if (newAcronym==null) {
//                    //there's no Big5 for the judgement acronym; see if it's "pending"
//                    if (!judgementAcronym.toLowerCase().contains("pending")) {
//                        //not pending; replace with message
//                        newAcronym = "<NoBig5:" + judgementAcronym + ">";
//                    } else {
//                        //it does contain pending; keep the original acronym
//                        newAcronym = judgementAcronym ;
//                    }
//                }
//                map.put(submissionID, newAcronym);
//            }
//        }
//
//    }

    /**
     * Converts the given map so that all judgement acronyms are CLICS "Big 5" acronyms if a corresponding CLICS Big 5 acronym 
     * can be found.  If no corresponding acronym can be found, then a check is made to see if the initial acronym contains the
     * substring "pending"; if so, the original acronym is retained, if not then the acronym is replaced with a string of the form
     * "<NoBig5:xxx>" where "xxx" is the original acronym.
     * 
     * @param map the map to be converted
     */
    private void convertMapToBig5 (Map<String,ShadowJudgementInfo> map) {
        
        //process each submission in the specified map
        for (String submissionID : map.keySet()) {
            
            //get the judgement info out of the submission
            ShadowJudgementInfo info = map.get(submissionID) ;
            
            //get the ShadowJudgementPair out of the judgement info
            ShadowJudgementPair pair = info.getShadowJudgementPair();
            
            String judgementAcronym = pair.getPc2Judgement();
            pair.setPc2Judgement(convertJudgementToCLICSBig5(judgementAcronym));
            
            judgementAcronym = pair.getRemoteCCSJudgement();
            pair.setRemoteCCSJudgement(convertJudgementToCLICSBig5(judgementAcronym));
            
        }
    }
    
    /**
     * Returns a String representing the "CLICS Big5 acronym" (one of WA/RTE/TLE/CE/AC) corresponding to the specified judgement acronym tring.
     * If there is no corresponding conversion (as defined by {@link CLICSJudgementType#getBig5EquivalentAcronym()},
     * then if the specified judgement string contains the substring "pending" then the specified judgement string is returned,
     * otherwise a String of the form "<NoBig5:xxx>" (where xxx is the input string) is returned.
     * 
     * @param judgementAcronym the string to be converted to its "CLICS Big 5" equivalent.
     * 
     * @return a String containing a CLICS Big5 acronym if there is such an acronym matching the input judgement; otherwise 
     *              returns the input judgement or a string containing the input judgement.
     */
    private String convertJudgementToCLICSBig5(String judgementAcronym) {

        String retStr ;

        //construct dummy "JudgementType" representing the input judgement string
        CLICSJudgementType judgement = new CLICSJudgementType (judgementAcronym, "dummy", false, false) ;
        
        //try to get a "BIG5" acronym string corresponding to the input acronym judgement string
        String big5AcronymString = judgement.getBig5EquivalentAcronym();

        // the above method returns null if there is no matching Big 5 acronym string
        if (big5AcronymString == null) {

            // there's no Big5 for the input judgement string; see if it's "pending"
            if (!judgementAcronym.toLowerCase().contains("pending")) {

                // not pending; replace with message
                retStr = "<NoBig5:" + judgementAcronym + ">";

            } else {
                // it does contain pending; keep the original judgement string
                retStr = judgementAcronym;
            }
            
        } else {
            //there was a CLICS Big5 acronym matching the input string; return the CLICS Big5 string
            retStr = big5AcronymString;
        }

        return retStr;
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
