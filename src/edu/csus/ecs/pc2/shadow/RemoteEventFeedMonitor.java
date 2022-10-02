// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.ui.ShadowCompareRunsPane;

/**
 * This class listens for submission events in the event feed input stream from a remote CCS CLICS Contest API Event Feed.
 * 
 * Events of type "submissions" result in fetching the submission files from the remote CCS and submitting those files
 * as a submission to the local (Shadow) PC2 system.
 * 
 * Events of type "judgements" result in updating a global map of judgements received from the remote system; this map
 * is subsequently used by the {@link ShadowCompareRunsPane} class to display comparisons between local (shadow) judgements
 * and the corresponding judgements from the remote CCS.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteEventFeedMonitor implements Runnable {

    public static final int REMOTE_EVENT_FEED_DELAYMS = 500;
    
    private IRemoteContestAPIAdapter remoteContestAPIAdapter;
    private URL remoteURL;
    private String login;
    private String password;
    private RemoteRunSubmitter submitter;
    
    private boolean keepRunning ;
    
    //set this to true to filter out all but those submissions/judgements listed in "submissionFilterIDs"
    private boolean respectSubmissionFilter = false; 
    
    //a list of submissions which are the only ones we are interested in if respectSubmissionFilter is true
    private String [] submissionFilterIDs = {

            /* PacNW submissions of interest
             * "286", "414", "908", "1414", "1437", "1460", "1464", "1481", "1531", "1611"
             */        
            
            /* DOMJudge SystestA submissions of interest
             */
            "248603", //cpp, first judgement (which is null)
            "248610", //cpp, different team
            "248611", //java
            "248609", //py2
            "249388"  //py3
            
        } ;
    
    //a list form of the above, created in the class constructor
    private List<String> submissionFilterIDsList;
    
    private boolean listening;
    private IInternalController pc2Controller;
    private InputStream remoteInputStream;
    
    /**
     * A Map mapping remote judgement ids to corresponding submission ids and the judgement applied to that submission.
     */
    private static Map<String,String> remoteJudgements;
    
    /**
     * A lock for synchronizing access to the above map.
     */
    private static Object remoteJudgementsMapLock = new Object();

    /**
     * Constructs a RemoteEventFeedMonitor with the specified values.  The RemoteEventFeedMonitor
     * is used to listen for submission events in the event feed from a remote CCS contest.  
     *
     * @param pc2Controller an {@link IInternalController} for passing error handling back to the local PC2 system
     * @param remoteContestAPIAdapter an adapter for accessing the remote contest API
     * @param remoteURL the URL to the remote CCS
     * @param login the login (account) on the remote CCS
     * @param password the password to the remote CCS account
     * @param submitter a {@link Runnable} which knows how to submit a receive remote run to PC2
     */
    public RemoteEventFeedMonitor(IInternalController pc2Controller, IRemoteContestAPIAdapter remoteContestAPIAdapter, URL remoteURL, String login, String password, RemoteRunSubmitter submitter) {

        this.pc2Controller = pc2Controller ;
        this.remoteContestAPIAdapter = remoteContestAPIAdapter;
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        this.submitter = submitter;
        
        //create a filter for submissions (only used if respectSubmmissionFilter = true)
        submissionFilterIDsList = new ArrayList<String>(submissionFilterIDs.length);
        Collections.addAll( submissionFilterIDsList, submissionFilterIDs);

    }
    
    @Override
    public void run() {
        boolean bDelay;
        
        Thread.currentThread().setName("RemoteEventFeedMonitorThread");

        keepRunning = true;
        
        Log log = pc2Controller.getLog();
        
        //open connection to remoteURL event-feed endpoint
        if (Utilities.isDebugMode()) {
            System.out.println("Opening connection to remote event feed");
        }
        log.info("Opening connection to remote event feed");
        remoteInputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();

        if (remoteInputStream == null) {
            
            //  TODO: improve error handling (more than just logging?)
            System.err.println("Error opening event feed stream");
            log.log(Level.SEVERE, "Error opening event feed stream");
            
        } else {

            String event = "null event";
            try {

                //wrap the event stream (which consists of newline-delimited character strings representing events)
                // in a BufferedReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));

                //read the next event from the event feed stream
                event = reader.readLine();

                //process the next event
                while ((event != null) && keepRunning) {

                    //if the remote system feeds events too fast (which happens when testing with a completed contest/eventfeed, and could
                    // in theory happen during a live contest), this RemoteEventFeedMonitor thread overwhelms the JVM scheduler, causing
                    // things like AWT Dispatch thread GUI updates to become hung.
                    //Let's sleep for a moment to allow other threads to run.
                    //Note1: it might seem more logical to put this sleep at the END of the while-loop; however, there are multiple places
                    // within the loop which invoke "continue", which would SKIP the sleep if it was at the end of the loop...
                    //Note2:  the value "10ms" was experimentally determined using the NADC21 Kattis event feed, which contains over 53,300 
                    // events (lines).  Any value below 10ms caused the GUI to freeze up.
                    //Note3: an attempt was made to change the priority of this thread to a higher number (so, lower priority) than that 
                    // assigned to the AWT Event Dispatch thread, and then to use Thread.yield() here instead of Thread.sleep().  In theory
                    // this should have allowed the AWT thread to gain the CPU when it was ready to run, but in practice GUI lockups were 
                    // still seen.  This could be because the AWT thread has multiple blocking conditions and each one of these allows this
                    // Event Feed Monitor thread to get back to the CPU (and use it for a full scheduling timeslice).
                    // So it was determined that the following was the best solution, at least in the short term...
                    //See also GitHub Issue 267:  https://github.com/pc2ccs/pc2v9/issues/267
                    // Thread.sleep(10);
                    // We now set a flag indicating if we want to delay at the END of the loop.  Any message we want to
                    // delay for will set the bDelay to true, such as submissions and judgements (which is currently all we process anyway)
                    // by default, we will NOT delay.  This lets messages like organizations, runs, teams, etc, fly by quickly.
                    bDelay = false;
                   
                    //skip blank lines and any that do not start/end with "{...}"
                    if ( event.length()>0 && event.trim().startsWith("{") && event.trim().endsWith("}") ) {
                        
//                        System.out.println("Got event string: " + event);
                        log.log(Level.INFO, "Got event string: " + event);
                        try {

                            /**
                             *
                            Sample submissions JSON from finals 2019 dom judge event feed.
                            {"id":"279029","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.162+01:00","contest_time":"-17:02:04.837","id":"10547","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10547/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.191+01:00"}
                            {"id":"279030","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.209+01:00","contest_time":"-17:02:04.790","id":"10548","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10548/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.216+01:00"}
                            {"id":"279031","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.228+01:00","contest_time":"-17:02:04.771","id":"10549","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10549/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.235+01:00"}
                            {"id":"279032","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.244+01:00","contest_time":"-17:02:04.755","id":"10550","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10550/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.251+01:00"}
                             */

                            // extract the event into a map of event element names/values
                            Map<String, Object> eventMap = getMap(event);

                            if (eventMap == null) {
                                // could not parse event
                                log.log(Level.WARNING, "Could not parse event: " + event);
                                if (Utilities.isDebugMode()) {
                                    System.out.println("Could not parse event: " + event);
                                }

                            } else {

                                // find event type
                                String eventType = (String) eventMap.get("type");
                                
                                if (Utilities.isDebugMode()) {
                                    System.out.println("Found event of type " + eventType + ": " + event);
                                }
                                log.info("Found event of type " + eventType + ": " + event);

                                if ("submissions".equals(eventType)) {

                                    if (isReadOnlyClient()) {
                                        log.info("Skipping submission event due to being logged in as a read-only client (not Feeder1)");
                                        event = reader.readLine();
                                        continue;
                                    }

                                    //process a submission event
                                    try {

                                        if (Utilities.isDebugMode()) {
                                            System.out.println("\nProcessing submission event: " + event);
                                        }

                                        log.log(Level.INFO, "Processing submission event: " + event);

                                        //get a map of the data comprising the submission
                                        Map<String, Object> submissionEventDataMap = (Map<String, Object>) eventMap.get("data");

                                        //get the submission ID out of the submission event data map
                                        String submissionID = (String) submissionEventDataMap.get("id");
                                        
                                        //check if the current submission is to be ignored due to filtering
                                        // (submissionFilterIDsList is a list of submissions we WANT TO KEEP; typically used for debugging...)
                                        if (respectSubmissionFilter && !submissionFilterIDsList.contains(submissionID)) {

                                            if (Utilities.isDebugMode()) {
                                                System.out.println ("Ignoring submission " + submissionID + " due to filter");
                                            }
                                            
                                            log.info("Ignoring submission " + submissionID + " due to filter");
                                            event = reader.readLine();
                                            continue;
                                        }

                                        //make sure we haven't seen this submission before (this could happen if
                                        // we've done a restart but already processed this submission on a prior shadow run)
                                        if (RunUtilities.isAlreadySubmitted(pc2Controller.getContest(),submissionID) ) {
                                            
                                            if (Utilities.isDebugMode()) {
                                                System.out.println ("Ignoring submission " + submissionID + " due to it already having been submitted");
                                            }
                                            
                                            log.info("Ignoring submission " + submissionID + " due to it already having been submitted");
                                            event = reader.readLine();
                                            continue;
                                        }

                                        // This is the commit point for a submission, so we will want to delay at the end of the loop
                                        bDelay = true;
                                        
                                        //convert submission data into a ShadowRunSubmission object
                                        ShadowRunSubmission runSubmission = createRunSubmission(submissionEventDataMap);

                                        if (runSubmission == null) {
                                            
                                            if (Utilities.isDebugMode()) {
                                                System.out.println ("Severe error parsing submission data: " + event);
                                            }
                                            
                                            log.log(Level.SEVERE, "Error parsing submission data: " + event);
                                            throw new Exception("Error parsing submission data " + event);
                                            
                                        } else {

                                            if (Utilities.isDebugMode()) {
                                                System.out.println("Found run " + runSubmission.getId() + " from team " + runSubmission.getTeam_id()
                                                                    + ": event= " + event);
                                            }
                                            
                                            log.log(Level.INFO, "Found run " + runSubmission.getId() + " from team " + runSubmission.getTeam_id()
                                                                    + ": event= " + event);

                                            //construct the override values to be used for the shadow submission
                                            long overrideTimeMS = Utilities.convertCLICSContestTimeToMS(runSubmission.getContest_time());
                                            long overrideSubmissionID = Utilities.stringToLong(runSubmission.getId());

    //The entire following block of commented-out code is intended to be used to support fetching submission files from the remote system by using the
    // "href" element found within the "files" element of a submission event.  However, the href element cannot be properly used
    // until the "Primary CCS URL" property is split into "Primary CCS BaseURL" and "Primary CCS ContestID Path" elements.  
    // Until that update is made throughout the code, the following block is commented out and we're using a default URL
    // created by calling the RemoteContestAPIAdapter with just the submissionID; the RemoteContestAPIAdapter constructs the
    // default URL by appending "/submissions/<submissionID>/files" to the current value of "Primary CCS URL" and then
    // fetches the submission files from that URL.
    // See also the comment in interface IRemoteContestAPIAdapter; the additional (commented-out) method there must be uncommented
    // for the following block of code to work.

//                                            //define the path to where the remote zip file containing the submissions files can be found
//                                            String defaultSubmissionFilesURLString = "/submissions/" + runSubmission.getId() + "/files"; //our default path
//                                            String submissionFilesURLString = null;  //this one we hope to pull out of the submission event, below
//                                            
//                                            
//                                            //Note about the next set of code: the CLICS ContestAPI spec says that a submission event has numerous fields, 
//                                            // one of which is "files".  The value found in the "files" element is specified as an ARRAY of "zip file references",
//                                            // where each "zip file reference" has the form  {href:path/to/zip,mime:application/zip}.  Thus there
//                                            // can in principle be multiple zip files on the remote system, with each zip file containing multiple files.
//                                            // In practice however there is no known implementation where there will be more than one such "zip file reference",
//                                            // so what we do is fetch the array (as a List, where each list element is one "zip file reference" (consisting of
//                                            // two parts:  href:path and mime:zip)), then pull the first element out of the array (List), pull the href
//                                            // out of that Map, and use that to form the URL to fetch the zip file containing the submission files.  *whew*
//                          
//                                            //get from the ShadowRunSubmission object the list of references to zip files.
//                                            List<Map<String, String>> filesList = runSubmission.getFiles();
//                                            
//                                            //make sure we got a valid list from the submission
//                                            if (filesList.size() >= 1){
//                                                
//                                                //get out of the list the first zip file reference (which is a map containing "href:path" and "mime:zip" elements)
//                                                Map<String, String> zipFileReferenceMap = filesList.get(0);
//                                                
//                                                //make sure we got a valid zip file reference map (the map should contain exactly "href" and "mime" keys)
//                                                if (zipFileReferenceMap.size() == 2){
//                                                    String filesPath = zipFileReferenceMap.get("href");
//                                                    if (!StringUtilities.isEmpty(filesPath)){
//                                                        Note: the following method getRemoteBaseURL() needs to be coded -- after
//                                                        the split of Primary CCS URL into "BaseURL" and "ContestIDPath" has been done.
//                                                        submissionFilesURLString = getRemoteBaseURL() + "/" + filesPath;
//                                                    }
//                                                }
//                                            }
//
//                                            //check if the above code was able to obtain a URL for the submission files zip
//                                            if (StringUtilities.isEmpty(submissionFilesURLString)) {
//
//                                                //no, we couldn't get a URL from the event; use our default
//                                                submissionFilesURLString = defaultSubmissionFilesURLString;
//                                                System.err.println("Warning: could not find submission file URL for id = " + runSubmission.getId() + //
//                                                        " in submission event; using '" + submissionFilesURLString + "' " + //
//                                                        "(event=" + event + ")");
//                                            }
//
//                                            List<IFile> files = null;
//
//                                            System.out.println("debug 22 fetching file from URL "+submissionFilesURLString);
//                                            //get the files from the remote URL
//                                            URL submissionFilesURL = new URL(submissionFilesURLString);
//                                            files = remoteContestAPIAdapter.getRemoteSubmissionFiles(submissionFilesURL);
//
//                                            if (files == null){
//                                                System.err.println("Unable to retrieve submission files using "+submissionFilesURL);
//                                            } else {
//                                                System.out.println("debug 22 getRemoteSubmissionFiles GOT "+files.size()+" files");
//                                            }

                                            //this block is a temporary substitute for the above commented-out block
                                            List<IFile> files = null;
                                            
                                            if (Utilities.isDebugMode()) {
                                                System.out.println("Fetching files from remote system using id "+overrideSubmissionID);
                                            }
                                            log.info("Fetching files from remote system using id "+overrideSubmissionID);
                                            
                                            //try up to maxTries times to get files without having a SocketTimeout
                                            int tryNum = 1;
                                            int maxTries = 10;
                                            boolean success = false ;
                                            Exception ex = null;
                                            
                                            while (!success && tryNum<=maxTries) {
                                                try {              
                                                    //request files from remote CCS
                                                    files = remoteContestAPIAdapter.getRemoteSubmissionFiles("" + overrideSubmissionID);
                                                    
                                                    //if we get here, no exception was thrown while getting the files
                                                    success = true;

                                                } catch (Exception e) {
                                                    
                                                    //we got an exception attempting to get the files for the submission from the remote CCS;
                                                    //see if the underlying cause of the exception was a socket timeout
                                                    Throwable cause = e.getCause();
                                                    if (cause!=null && cause instanceof SocketTimeoutException) {

                                                            if (Utilities.isDebugMode()) {
                                                                System.out.println("Warning: SocketTimeoutException getting files for submission " + overrideSubmissionID 
                                                                        + " on try " + tryNum + "; trying up to " + maxTries + " times");
                                                            }
                                                            log.warning("SocketTimeoutException getting files for submission " + overrideSubmissionID 
                                                                    + " on try " + tryNum + "; trying up to " + maxTries + " times");
                                                            tryNum++;
                                                            
                                                            //save the exception so we can rethrow it if we never get "success"
                                                            ex = e;
                                                        
                                                    } else {
                                                        //we got an exception other than a socket timeout; rethrow it outward (which will be logged in the catch clause)
                                                        throw e;
                                                    }
                                                }
                                            }
                                            
                                            //if after maxTries we still weren't successful getting files, log it and rethrow the last exception
                                            if (!success) {
                                                if (Utilities.isDebugMode()) {
                                                    System.out.println("Severe: unable to get files for submission " + overrideSubmissionID
                                                                    + " from remote CCS after " + maxTries + " tries; giving up.");
                                                }
                                                log.severe("Unable to get files for submission " + overrideSubmissionID
                                                            + " from remote CCS after " + maxTries + " tries; giving up.");
                                                throw ex;
                                            } else {
                                                //we got files from the remote CCS; log how many tries it took
                                                String pluralizer = tryNum==1 ? " try." : " tries."; 
                                                if (Utilities.isDebugMode()) {
                                                    System.out.println("Got files for submission id " + overrideSubmissionID + " from remote CCS after " 
                                                                    + tryNum + pluralizer);
                                                }
                                                log.info("Got files for submission id " + overrideSubmissionID + " from remote CCS after " + tryNum + pluralizer);
                                            }
                                            
                                            //if we get here we at least know we got a "success" from the above communication with the remote CCS
                                            if (files==null) {
                                                if (Utilities.isDebugMode()) {
                                                    System.err.println("Null file list returned from remote system while processing event: " + event);
                                                }
                                                log.log(Level.SEVERE, "Null file list returned from remote system while processing event: " + event);
                                                throw new Exception("Null file list returned from remote system while processing event: " + event);
                                            }
                                            
                                            IFile mainFile = null;

                                            if (files.size() <= 0) {
                                                
                                                if (Utilities.isDebugMode()) {
                                                    System.err.println("Empty file list returned from remote system while processing event: " + event);
                                                }
                                                log.log(Level.SEVERE, "Empty file list returned from remote system while processing event: " + event);
                                                throw new Exception("Empty file list returned from remote system while processing event: " + event);
                                                
                                            } else {
                                                
                                                if (Utilities.isDebugMode()) {
                                                    System.err.println("Received files from remote system for id " + overrideSubmissionID);
                                                }
                                                log.log(Level.INFO, "Received files from remote system for id " + overrideSubmissionID);

                                                mainFile = files.get(0);
                                            }

                                            List<IFile> auxFiles = null;
                                            if (files.size() > 1) {
                                                auxFiles = files.subList(1, files.size());
                                            }

                                            if (Utilities.isDebugMode()) {
                                                System.out.println ("Invoking submitter.submitRun() for team " + runSubmission.getTeam_id() 
                                                                    + " problem " + runSubmission.getProblem_id() 
                                                                    + " language " +  runSubmission.getLanguage_id()
                                                                    + " time " + overrideTimeMS 
                                                                    + " submissionID " + overrideSubmissionID);
                                            }
                                            log.info("Invoking submitter.submitRun() for team " + runSubmission.getTeam_id()  
                                                                    + " problem " + runSubmission.getProblem_id() 
                                                                    + " language " +  runSubmission.getLanguage_id()
                                                                    + " time " + overrideTimeMS 
                                                                    + " submissionID " + overrideSubmissionID);
                                            try {
                                                submitter.submitRun(runSubmission.getTeam_id(), runSubmission.getProblem_id(), runSubmission.getLanguage_id(), mainFile, auxFiles,
                                                        overrideTimeMS, overrideSubmissionID);
                                            } catch (Exception e) {
                                                // TODO design error handling reporting
                                                if (Utilities.isDebugMode()) {
                                                    System.err.println("Exception submitting run for event: " + event + ": " + e);
                                                }
                                                log.log(Level.WARNING, "Exception submitting run for event: " + event + ": ", e);
                                                e.printStackTrace();
                                            }
                                        }

                                    } catch (Exception e) {
                                        // TODO design error handling reporting (logging?)
                                        if (Utilities.isDebugMode()) {
                                            System.err.println("Exception processing event: " + event + "\n " + e.toString());
                                        }
                                        log.log(Level.WARNING, "Exception processing event: " + event, e);

                                        e.printStackTrace();
                                    }

                                } else if ("judgements".equals(eventType)) {
                                    
                                    // Delay on judgments
                                    bDelay = true;
                                    
                                    if (Utilities.isDebugMode()) {
                                        System.out.println("Found judgement event: " + event);
                                    }
                                    log.log(Level.INFO, "Found judgement event: " + event);

                                    //process a judgment event
                                    try {
                                        if(isDeleteOperation(eventMap)) {
                                            if(!deleteJudgment(eventMap)) {
                                                log.log(Level.WARNING, "Unable to delete judgement for event: " +event);
                                            }
                                        } else {
                                            //it's not a delete; see if there is an actual judgment (acronym) in the event data
                                            // (there might not be such an element; "create" operations do not always have a judgment)
                                            // get a map of the data elements for the judgment
                                            Map<String, Object> judgementEventDataMap = (Map<String, Object>) eventMap.get("data");
                                            
                                            String judgement = (String) judgementEventDataMap.get("judgement_type_id");
                                            if (judgement != null && !judgement.equals("")) {


                                                // there is a judgement; get the relevant IDs
                                                String judgementID = (String) judgementEventDataMap.get("id");
                                                String submissionID = (String) judgementEventDataMap.get("submission_id");

                                                //check if the submission for this judgement is to be ignored due to filtering
                                                // (submissionFilterIDsList is a list of submissions we WANT TO KEEP; typically used for debugging...)
                                               if (respectSubmissionFilter && !submissionFilterIDsList.contains(submissionID)) {
                                                    
                                                    if (Utilities.isDebugMode()) {
                                                        System.out.println ("Ignoring judgement " + judgementID + " for submission " + submissionID + " due to filter");
                                                    }
                                                    log.info("Ignoring judgement " + judgementID + 
                                                            " for submission " + submissionID + " due to filter");
                                                    event = reader.readLine();
                                                    continue;
                                                }
                                                   
                                                   //TODO: make sure this is a judgement for a submission we know about.  
                                                   //  Question: isn't it possible the remote system will send us a "judgement" before it sends us 
                                                   //  the "submission" associated with that judgement?  It seems that this is both allowed by the CLICS
                                                   //  specification, and in fact does happen (e.g. in Kattis) when it sends "judgements" at the beginning
                                                   //  of the event stream which specify a "create" op and have "null" for values such as "judgement_type_id".
         
                                                // this (appears to be) a judgement we want; save it in the global judgements map under a key of
                                                // the judgement ID with value "submissionID:judgement"
//                                                System.out.println ("Adding judgement " + judgementID + " for submission " + submissionID + " with judgement " + judgement + " to RemoteJudgements Map");                                                
                                                synchronized (remoteJudgementsMapLock) {
                                                    getRemoteJudgementsMap().put(judgementID, submissionID + ":" + judgement);
                                                }
                                            }
                                        }

                                    } catch (Exception e) {
                                        // TODO design error handling reporting (logging?)
                                        if (Utilities.isDebugMode()) {
                                            System.err.println("Exception processing event: " + event + "\n" + e.toString());
                                        }
                                        log.log(Level.SEVERE, "Exception processing event: " + event, e);
                                        e.printStackTrace();
                                    }

                                } else {

                                    if (Utilities.isDebugMode()) {
                                        System.out.println("Ignoring " + eventType + " event");
                                    }
                                    log.log (Level.INFO, "Ignoring " + eventType + " event");
                                }

                            } // else
                        } catch (Exception e) {
                            // TODO design error handling reporting (logging?)
                            if (Utilities.isDebugMode()) {
                                System.err.println("Exception processing event: " + event + "\n" + e.toString());
                            }
                            log.log(Level.SEVERE, "Exception processing event: " + event, e);
                            e.printStackTrace();
                        } 
                    } else {
                        
                        //we're skipping an event feed input line -- log the reason
                        if (event.length()<=0) {
                            log.log(Level.INFO, "Skipping event feed input line (length is " + event.length() + ")");
                        } else if (!event.trim().startsWith("{")) {
                            log.log(Level.INFO, "Skipping event feed input line (does not start with \"{\"): " + event.toString());
                        } else if (!event.trim().endsWith("}")) {
                            log.log(Level.INFO, "Skipping event feed input line (does not end with \"}\"): " + event.toString());                            
                        } else {
                            log.log(Level.WARNING, "Skipping event feed input line (sorry - no explanation for why): " + event.toString());
                        }
                    }
                    
                    if(bDelay) {
                        Thread.sleep(REMOTE_EVENT_FEED_DELAYMS);
                    }
                    event = reader.readLine();

                } // while
            } catch (Exception e) {
                // TODO design error handling reporting (logging?)
                if (Utilities.isDebugMode()) {
                    System.err.println("Exception reading event from stream: " + e.toString() + ": event = " + event);
                }
                log.log(Level.SEVERE, "Exception reading event from stream: " + event, e);
                e.printStackTrace();
            }
        } // end else
    }
    
    /**
     * Returns an indication of whether the current client is a "read-only shadow" client.
     * 
     * Currently, account "feeder1" is allowed to be a read-write Shadow (meaning, it has the ability
     * to actually submit runs to the PC2 server when they are received from the Remote CCS); all
     * other accounts are considered "read-only" -- meaning they can look at submissions,
     * the scoreboards, etc. but they will not actually submit runs received from the Remote CCS to
     * the PC2 server.
     * 
     * TODO: extend this function to allow a broader definition and control of when a client
     *      is considered "read-only"; for example, managing this by Permissions and/or via
     *      Admin settings.  See https://github.com/pc2ccs/pc2v9/issues/240.
     * 
     * @return true if the current client is a "read-only Shadow" client; false if the client
     *          is allowed to do read-write operations (such as submitting a run from the Remote CCS
     *          to the PC2 server).
     */
    private boolean isReadOnlyClient() {
        ClientId clientId = pc2Controller.getContest().getClientId(); 
        ClientType.Type clientType = clientId.getClientType();
        int clientNum = clientId.getClientNumber();
        
        if (clientType.equals(Type.FEEDER) && clientNum==1) {
            //client is Feeder1; it is NOT a "read-only" client
            return false;
        } else {
            //client is something other than Feeder1; it IS "read-only"
            return true;
        }
    }

    /**
     * Initializes the Map<String,String> which holds mappings of judgement id's to corresponding submissions and judgement
     * types (acronymns).
     * 
     * The keys to the map are Strings containing the numerical value of a judgement id as received from the remote CCS;
     * the values under each key are the concatenation of the submission id corresponding to the judgement with the
     * judgement type id (i.e., the judgement acronym), separated by a colon (":").
     * 
     * Note that this method is PRIVATE; external clients wanting access to the remoteJudgementsMap should use method {@link #getRemoteJudgementsMapSnapshot()}.
     * 
     * @return a Mapping of judgement id's to the corresponding submission and judgement type (value).
     *              If no remote judgements have yet been received the returned Map will be empty (but not null).
     */
    private static Map<String,String> getRemoteJudgementsMap() {
        synchronized (remoteJudgementsMapLock) {
            if (remoteJudgements == null) {
                remoteJudgements = new HashMap<String, String>();
            }
            return remoteJudgements;
        }
    }

 
    /**
     * Returns a snapshot of the current contents of the Map<String,String> which holds mappings of judgement id's to corresponding 
     * submissions and judgement types (acronymns).
     * 
     * The keys to the map are Strings containing the numerical value of a judgement id as received from the remote CCS;
     * the values under each key are the concatenation of the submission id corresponding to the judgement with the
     * judgement type id (i.e., the judgement acronym), separated by a colon (":").
     * 
     * Note that this method returns a <I>copy</i> which is a <I>snapshot</i>; there is no guarantee that the map will not subsequently be 
     * changed by other threads. However, the method does provide internal synchronization to insure that the map is not simultaneously 
     * altered while the snapshot is being created.
     * 
     * @return a snapshot copy of the current Mapping of judgement id's to the corresponding submission and judgement type (value).
     *              If no remote judgements have yet been received the returned Map will be empty (but not null).
     */
    public static Map<String,String> getRemoteJudgementsMapSnapshot() {
        
        synchronized (remoteJudgementsMapLock) {
            Map<String, String> currentMap = getRemoteJudgementsMap();
            
            Map<String, String> copy = new HashMap<String,String>();
            for (String key : currentMap.keySet()) {
                copy.put(key, currentMap.get(key));
            }
           
            return copy;
        }
    }

    /**
     * Returns a Map containing the key/value elements in the specified JSON string.
     * This method uses the Jackson {@link ObjectMapper} to perform the conversion from the JSON
     * string to a Map.  Note that the ObjectMapper recurses for nested JSON elements, returning
     * a appropriate Object in the Map under the corresponding key string.
     * 
     * @param jsonString a JSON string to be converted to a Map
     * @return a Map mapping the keys in the JSON string to corresponding values, or null if the input
     *          String is null or if an exception occurs while converting the JSON to a Map.
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> getMap(String jsonString) {
        
        if (jsonString == null){
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

    ShadowRunSubmission createRunSubmission2(String jsonString){
        
        Map<String, Object> map = getMap(jsonString);
        if (map == null) {
            // could not parse.
            pc2Controller.getLog().log(Level.SEVERE, "Could not parse event: " + jsonString);
            System.err.println("Could not parse event: " + jsonString);

        } else {
            String eventType = (String) map.get("type");

//            System.out.print("\nfound event: " + eventType + ":" + jsonString);

            // found event: submissions:{"type":"submissions", "id":"pc2-165", "op":"update", "data": {"id":"3","language_id":"java","problem_id":"a","team_id":"3","time":"2019-11-30T20:41:36.809+02","contest_time":"00:20:00.000","entry_point":"ISumitWA","files":[{"href":"https://localhost:50443/contest/submissions/3/files"}]}}

            if ("submissions".equals(eventType)) {
//                pc2Controller.getLog().log(Level.INFO, "Found submission event");
//                System.out.println("found submission event");

                Object obj = map.get("data");

                Map<String, Object> eventDataMap = ( Map<String, Object> ) obj;
                ShadowRunSubmission runSubmission = createRunSubmission(eventDataMap);
                return runSubmission;
            }
        }
        return null;
    }

    /**
     * Return if an event is a delete operation
     * 
     * For older event feed events, the "op" property will be checked for existence
     * and if it's a "delete".
     * For newer event feed (2022-07), there is no "op", but if the "data" object
     * is null, then it's a delete.
     * 
     * @param eventMap map of all properties in the event
     * @return true if this is the event map passed in specifies a delete operation
     */  
    private boolean isDeleteOperation(Map<String, Object> eventMap)
    {
        boolean bDelete = false;
        
        // "op" property, if it's there - old feed format uses this
        String operation = (String) eventMap.get("op");
        
        if(operation != null) {
            // Old feed
            if(operation.equals("delete")) {
                bDelete = true;
            }
        } else if((Map<String, Object>) eventMap.get("data") == null){
            bDelete = true;
        }
        return(bDelete);
    }
    
    /** Delete the judgment specified by the supplied eventMap properties
     * 
     * For older event feed events, the "op" property will be checked for existence
     * and if it's a "delete", we use the eventDataMap's "id" property.
     * For newer event feed (2022-07), there is no "op", and no "data" object
     * but the "id" is in the eventMap.
     * 
     * @param eventMap map of all properties in the event
     * @return true if this is the event map passed in specifies a delete operation
     */  
    private boolean deleteJudgment(Map<String, Object> eventMap)
    {
        String operation = (String) eventMap.get("op");
        String idToDelete = null;
        boolean bDeleted = false;
        
        // Determine feed version, and where to get the judgement id.
        // For the 2022-07 (and 2021-11) clics spec, "operation" will always be null since the "op" field was removed
        // For the 2020-03 the "op" field will be non-null.  This is how we determine the feed type.                                        
       if (operation == null) {
           // 2022-07 (newer) feed - the ID of the judgment is in the notification object since there is
           // no data object.
           idToDelete = (String)eventMap.get("id");
       } else if(operation.equals("delete")) {
           // 2020-03 feed, "op" field present and is an explicit delete, judgment id is in "data" object
           Map<String, Object> judgmentEventDataMap = (Map<String, Object>) eventMap.get("data");
           
           if(judgmentEventDataMap != null) {
               idToDelete = (String) judgmentEventDataMap.get("id");
           }
       }
       // idToDelete obtained from different "id" fields above depending on feed version
       if(idToDelete != null && !idToDelete.isEmpty()) {
           // we have a judgment id; remove from the global map the judgment whose ID is specified
            synchronized (remoteJudgementsMapLock) {
                getRemoteJudgementsMap().remove(idToDelete);
            }
            bDeleted= true;
            //TODO: how do we notify the local PC2 system that this judgment should be deleted??
       }
       return(bDeleted);
    }


    protected static ShadowRunSubmission createRunSubmission(Map<String, Object> eventDataMap) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper.convertValue(eventDataMap, ShadowRunSubmission.class);
    }
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }

}
