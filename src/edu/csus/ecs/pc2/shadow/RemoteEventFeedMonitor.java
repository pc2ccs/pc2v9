// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.ui.ShadowComparePane;

/**
 * This class listens for submission events in the event feed input stream from a remote CCS CLICS Contest API Event Feed.
 * 
 * Events of type "submissions" result in fetching the submission files from the remote CCS and submitting those files
 * as a submission to the local (Shadow) PC2 system.
 * 
 * Events of type "judgements" result in updating a global map of judgements received from the remote system; this map
 * is subsequently used by the {@link ShadowComparePane} class to display comparisons between local (shadow) judgements
 * and the corresponding judgements from the remote CCS.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteEventFeedMonitor implements Runnable {

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
    private static Map<String,String> remoteJudgements;

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
        
        Thread.currentThread().setName("RemoteEventFeedMonitorThread");

        keepRunning = true;
        
        Log log = pc2Controller.getLog();
        
        //open connection to remoteURL event-feed endpoint
        remoteInputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();

        if (remoteInputStream == null) {
            
            //  TODO: design error handling (logging?)
            log.log(Level.SEVERE, "Error opening event feed stream");
            System.err.println("Error opening event feed stream");
            
        } else {

            try {

                //wrap the event stream (which consists of newline-delimited character strings representing events)
                // in a BufferedReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));

                //read the next event from the event feed stream
                String event = reader.readLine();

                while ((event != null) && keepRunning) {

                    //skip blank lines and any that do not start/end with "{...}"
                    if ( event.length()>0 && event.trim().startsWith("{") && event.trim().endsWith("}") ) {
                        
                        System.out.println("Got event string: " + event);
//                        log.log(Level.INFO, "Got event string: " + event);
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
                                System.out.println("Could not parse event: " + event);

                            } else {

                                // find event type
                                String eventType = (String) eventMap.get("type");
//                                System.out.println("\nfound event: " + eventType + ":" + event); // TODO log this.

                                if ("submissions".equals(eventType)) {
//                                    System.out.println("debug 22 found submission event");

                                    //process a submission event
                                    try {
//                                        log.log(Level.INFO, "Processing " + eventType + " event");

                                        //get a map of the data comprising the submission
                                        Map<String, Object> submissionEventDataMap = (Map<String, Object>) eventMap.get("data");

                                        //get the submission ID out of the submission event data map
                                        String submissionID = (String) submissionEventDataMap.get("id");
                                        
                                        //check if the current submission is to be ignored due to filtering
                                        if (respectSubmissionFilter && !submissionFilterIDsList.contains(submissionID)) {

                                            log.info("Ignoring submission " + submissionID + " due to filter");
                                            event = reader.readLine();
                                            continue;
                                        }

                                        //make sure we haven't seen this submission before (this could happen if
                                        // we've done a restart but already processed this submission on a prior shadow run)
                                        if (RunUtilities.isAlreadySubmitted(pc2Controller.getContest(),submissionID) ) {
                                            
                                            log.info("Ignoring submission " + submissionID + " due to it already having been submitted");
                                            event = reader.readLine();
                                            continue;
                                        }

                                        //convert submission data into a ShadowRunSubmission object
                                        ShadowRunSubmission runSubmission = createRunSubmission(submissionEventDataMap);

                                        if (runSubmission == null) {
                                            log.log(Level.SEVERE, "Error parsing submission data: " + event);
                                            throw new Exception("Error parsing submission data " + event);
                                        } else {

//                                            log.log(Level.INFO, "Found run " + runSubmission.getId() + " from team " + runSubmission.getTeam_id());
                                            System.out.println("Found run " + runSubmission.getId() + " from team " + runSubmission.getTeam_id());

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
                                            System.out.println("debug 22 get files using id "+overrideSubmissionID);
                                            files = remoteContestAPIAdapter.getRemoteSubmissionFiles("" + overrideSubmissionID);

                                            IFile mainFile = null;
                                            if (files.size() <= 0) {
                                                //TODO: deal with this error -- how to propagate it back to the invoker?
                                                System.err.println("Error: submitted files list is empty");
                                                log.log(Level.WARNING, "Received a submssion with empty files list");
                                            } else {
                                                mainFile = files.get(0);
                                            }

                                            List<IFile> auxFiles = null;
                                            if (files.size() > 1) {
                                                auxFiles = files.subList(1, files.size());
                                            }

                                            try {
                                                submitter.submitRun("team" + runSubmission.getTeam_id(), runSubmission.getProblem_id(), runSubmission.getLanguage_id(), mainFile, auxFiles,
                                                        overrideTimeMS, overrideSubmissionID);
                                            } catch (Exception e) {
                                                // TODO design error handling reporting
                                                System.err.println("Exception submitting run for: " + event);
                                                e.printStackTrace();
                                            }
                                        }

                                    } catch (Exception e) {
                                        // TODO design error handling reporting (logging?)
                                        System.err.println("Exception parsing event: " + event);
                                        e.printStackTrace();
                                    }

                                } else if ("judgements".equals(eventType)) {
                                    
                                    System.out.println("debug 22 recognized judgement event");
//                                    log.log(Level.INFO, "Found " + eventType + " event");

                                    //process a judgement event
                                    try {
                                        //get a map of the data elements for the judgement
                                        Map<String, Object> judgementEventDataMap = (Map<String, Object>) eventMap.get("data");

                                        // check if this is a "delete" event
                                        String operation = (String) eventMap.get("op");
                                        if (operation != null && operation.equals("delete")) {
                                            //it is a delete; remove from the global map the judgement whose ID is specified
                                            String idToDelete = (String) judgementEventDataMap.get("id");
                                            getRemoteJudgementsMap().remove(idToDelete);

                                            //TODO: how do we notify the local PC2 system that this judgement should be deleted??

                                        } else {
                                            //it's not a delete; see if there is an actual judgement (acronym) in the event data
                                            // (there might not be such an element; "create" operations do not always have a judgement)
                                            String judgement = (String) judgementEventDataMap.get("judgement_type_id");
                                            if (judgement != null && !judgement.equals("")) {


                                                // there is a judgement; get the relevant IDs
                                                String judgementID = (String) judgementEventDataMap.get("id");
                                                String submissionID = (String) judgementEventDataMap.get("submission_id");

                                                //check if the submission for this judgement is to be ignored due to filtering
                                                if (respectSubmissionFilter && !submissionFilterIDsList.contains(submissionID)) {
                                                    
                                                    log.info("Ignoring judgement " + judgementID + 
                                                            " for submission " + submissionID + " due to filter");
                                                    event = reader.readLine();
                                                    continue;
                                                }
     
                                                // this is a judgement we want; save it in the global judgements map under a key of
                                                // the judgement ID with value "submissionID:judgement"
                                                System.out.println ("Adding judgement " + judgementID + " for submission " + submissionID + " with judgement " + judgement + " to RemoteJudgements Map");                                                
                                                getRemoteJudgementsMap().put(judgementID, submissionID + ":" + judgement);
                                            }
                                        }

                                    } catch (Exception e) {
                                        // TODO design error handling reporting (logging?)
                                        System.err.println("Exception parsing event: " + event);
                                        log.log(Level.SEVERE, "Exception parsing event: " + event);
                                        e.printStackTrace();
                                    }

                                } else {
//                                    System.out.println("debug 22 - ignoring event " + eventType);
//                                    log.log (Level.INFO, "Ignoring " + eventType + " event");
                                }

                            } // else
                        } catch (Exception e) {
                            // TODO design error handling reporting (logging?)
                            System.err.println("Exception processing event: " + event);
                            log.log(Level.SEVERE, "Exception processing event: " + event);
                            e.printStackTrace();
                        } 
                    }
                    
                    event = reader.readLine();

                } // while
            } catch (Exception e) {
                // TODO design error handling reporting (logging?)
                System.err.println("Exception reading event from stream ");
                log.log(Level.SEVERE, "Exception reading event from stream: " + e.toString());
                e.printStackTrace();
            }
        } // end else
    }
    
    /**
     * Constructs a Map<String,String> to hold mappings of judgement id's to corresponding submissions and judgement
     * types (acronymns).
     * 
     * The keys to the map are Strings containing the numerical value of a judgement id as received from the remote CCS;
     * the values under each key are the concatenation of the submission id corresponding to the judgement with the
     * judgement type id (i.e., the judgement acronym), separated by a colon (":").
     * 
     * @return a Mapping of judgement id's to the corresponding submission and judgement type (value).
     *              If no remote judgements have yet been received the returned Map will be empty (but not null).
     */
    public static Map<String,String> getRemoteJudgementsMap() {
        if (remoteJudgements==null) {
            remoteJudgements = new HashMap<String,String>();
        }
        return remoteJudgements;
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
