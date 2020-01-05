// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IFile;

/**
 * This class listens for submission events in the event feed input stream from a remote CCS CLICS Contest API Event Feed.
 * 
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
    }
    
    @Override
    public void run() {
        
        Thread.currentThread().setName("RemoteEventFeedMonitorThread");

        keepRunning = true;
        
        //open connection to remoteURL event-feed endpoint
        remoteInputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();

        if (remoteInputStream == null) {
            
            //  TODO: design error handling (loggin?)
            System.err.println("Error opening event feed stream");
            
        } else {

            //wrap the event stream (which consists of newline-delimited character strings representing events)
            // in a BufferedReader
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));

                String event = reader.readLine();

                while ((event != null) && keepRunning) {

                    System.out.println("Got event string: " + event);

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
                            System.out.println("Could not parse event: " + event);

                        } else {

                            // find event type
                            String eventType = (String) eventMap.get("type");
                            System.out.println("\nfound event: " + eventType + ":" + event); // TODO log this.

                            if ("submissions".equals(eventType)) {
                                System.out.println("debug 22 found submission event");

                                //process a submission event
                                try {
                                    //get a map of the data comprising the submission
                                    Map<String,Object> submissionEventDataMap = (Map<String,Object>) eventMap.get("data");
                                    
                                    //convert metadata into ShadowRunSubmission
                                    ShadowRunSubmission runSubmission = createRunSubmission(submissionEventDataMap);

                                    if (runSubmission == null) {
                                        throw new Exception("Error parsing submission data " + event);
                                    } else {
                                        System.out.println("Found run " + runSubmission.getId() + " from team " + runSubmission.getTeam_id());

                                        long overrideTimeMS = Utilities.convertCLICSContestTimeToMS(runSubmission.getContest_time());
                                        long overrideSubmissionID = Utilities.stringToLong(runSubmission.getId());

                                        List<IFile> files = remoteContestAPIAdapter.getRemoteSubmissionFiles("" + overrideSubmissionID);

                                        IFile mainFile = null;
                                        if (files.size()<=0) {
                                            //TODO: deal with this error -- how to propagate it back to the invoker
                                            System.err.println ("Error: submitted files list is empty");
                                            pc2Controller.getLog().log(Level.WARNING, "Received a submssion with empty files list");
                                        } else {
                                            mainFile = files.get(0);                                            
                                        }

                                        List<IFile> auxFiles = null;
                                        if (files.size()>1) {
                                            auxFiles = files.subList(1, files.size());
                                        }

                                        try {
                                            submitter.submitRun("team" + runSubmission.getTeam_id(), runSubmission.getProblem_id(), runSubmission.getLanguage_id(),
                                                    mainFile, auxFiles, overrideTimeMS, overrideSubmissionID);
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
                                System.out.println("debug 22 found judgement event");

                                //process a judgement event
                                try {
                                    //get a map of the data elements for the judgement
                                    Map<String,Object> judgementEventDataMap = (Map<String,Object>) eventMap.get("data");
                                    
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

                                            // there is a judgement; save it in the global judgements map under a key of
                                            // the judgement ID with value "submissionID:judgement"
                                            String judgementID = (String) judgementEventDataMap.get("id");
                                            String submissionID = (String) judgementEventDataMap.get("submission_id");

                                            getRemoteJudgementsMap().put(judgementID, submissionID + ":" + judgement);
                                        }
                                    }

                                } catch (Exception e) {
                                    // TODO design error handling reporting (logging?)
                                    System.err.println("Exception parsing event: " + event);
                                    e.printStackTrace();
                                }
                                
                            } else {
                                System.out.println("debug 22 - ignoring event "+eventType);
                            }

                        } // else
                    } catch (Exception e) {
                        // TODO design error handling reporting (logging?)
                        System.err.println("Exception processing event: " + event);
                        e.printStackTrace();
                    }

                    event = reader.readLine();

                } // while
            } catch (Exception e) {
                // TODO design error handling reporting (logging?)
                System.err.println("Exception reading event from stream ");
                e.printStackTrace();
            }
        } // else
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

    //This method was replaced by the following method which returns a Map<String,Object>.
    // This was done because the Jackson ObjectMapper recursively reads objects; if an element
    // is comprised of a sub-element then it returns an object rather than a String representing that
    // object. 
//    @SuppressWarnings("unchecked")
//    protected static Map<String, String> getMap(String jsonString) {
//        
//        if (jsonString == null){
//            return null;
//        }
//        
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            Map<String, String> map = mapper.readValue(jsonString, Map.class);
//            return map;
//        } catch (IOException e) {
//            e.printStackTrace(System.err);
//            return null;
//        }
//    }
 
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
            System.out.println("Could not parse event: " + jsonString);

        } else {
            String eventType = (String) map.get("type");

            System.out.print("\nfound event: " + eventType + ":" + jsonString);

            // found event: submissions:{"type":"submissions", "id":"pc2-165", "op":"update", "data": {"id":"3","language_id":"java","problem_id":"a","team_id":"3","time":"2019-11-30T20:41:36.809+02","contest_time":"00:20:00.000","entry_point":"ISumitWA","files":[{"href":"https://localhost:50443/contest/submissions/3/files"}]}}

            if ("submissions".equals(eventType)) {
                System.out.println("found submission event");

                Object obj = map.get("data");

                Map<String, Object> eventDataMap = ( Map<String, Object> ) obj;
                ShadowRunSubmission runSubmission = createRunSubmission(eventDataMap);
                return runSubmission;
            }
        }
        return null;
    }

    protected static ShadowRunSubmission createRunSubmission(Map<String, Object> eventDataMap) {
        return new ObjectMapper().convertValue(eventDataMap, ShadowRunSubmission.class);
    }
 
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }

}
