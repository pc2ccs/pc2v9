// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
                        
                        /*
                         * Add code here to:
                         * 
                         *    parse JSON event and see if it is a "submission" event
                         *      (keep in mind that it could instead be eithe another type of event - e.g. "update" -
                         *       or could be a simple "keep-alive" empty-line)
                         *    
                         *    if (event.getType() == submissionEvent) {
                         *      
                         *      String teamID = event.getTeamID();
                         *      String problemID = event.getProblemID();
                         *      String languageID = event.getLanguageID();
                         *      String submissionID = event.getSubmissionID();
                         *      String time = event.getTime();
                         *      String entry_point = event.getEntryPoint();
                         *      
                         */
                        /**
                         *
Sample submissions JSON from finals 2019 dom judge event feed.
{"id":"279029","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.162+01:00","contest_time":"-17:02:04.837","id":"10547","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10547/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.191+01:00"}
{"id":"279030","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.209+01:00","contest_time":"-17:02:04.790","id":"10548","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10548/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.216+01:00"}
{"id":"279031","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.228+01:00","contest_time":"-17:02:04.771","id":"10549","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10549/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.235+01:00"}
{"id":"279032","type":"submissions","op":"create","data":{"language_id":"java","time":"2019-04-03T18:57:55.244+01:00","contest_time":"-17:02:04.755","id":"10550","externalid":null,"team_id":"148","problem_id":"azulejos","entry_point":null,"files":[{"href":"contests/finals/submissions/10550/files","mime":"application/zip"}]},"time":"2019-04-03T18:57:55.251+01:00"}
                         */

                        Map<String, String> map = getMap(event);
                        if (map == null) {
                            // could not parse.
                            System.out.println("Could not parse event: " + event);

                        } else {

                            // find event type
                            String eventType = map.get("type");
                            System.out.print("\nfound event: " + eventType + ":" + event); // TODO log this.

                            if ("submissions".equals(eventType)) {
                                System.out.println("debug 22 found submission event");

                                try {
                                    Object obj = map.get("data");

                                    Map<String, String> eventDataMap = (Map<String, String>) obj;
                                    /**
                                     * Convert metadata into ShadowRunSubmission
                                     */
                                    ShadowRunSubmission runSubmission = createRunSubmission(eventDataMap);

                                    if (runSubmission == null) {
                                        throw new Exception("Error parsing submission data " + event);
                                    } else {
                                        System.out.println("Found run " + runSubmission.getId() + " from " + runSubmission.getTeam_id());

                                        long overrideTimeMS = Utilities.stringToLong(runSubmission.getTime());
                                        long overrideSubmissionID = Utilities.stringToLong(runSubmission.getId());

                                        List<IFile> files = remoteContestAPIAdapter.getRemoteSubmissionFiles("" + overrideSubmissionID);

                                        IFile mainFile = files.get(0);

                                        List<IFile> auxFiles = files.subList(1, files.size() - 2);

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
    
    @SuppressWarnings("unchecked")
    protected static Map<String, String> getMap(String jsonString) {
        
        if (jsonString == null){
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    ShadowRunSubmission createRunSubmission2(String jsonString){
        
        Map<String, String> map = getMap(jsonString);
        if (map == null) {
            // could not parse.
            System.out.println("Could not parse event: " + jsonString);

        } else {
            String eventType = map.get("type");

            System.out.print("\nfound event: " + eventType + ":" + jsonString);

            // found event: submissions:{"type":"submissions", "id":"pc2-165", "op":"update", "data": {"id":"3","language_id":"java","problem_id":"a","team_id":"3","time":"2019-11-30T20:41:36.809+02","contest_time":"00:20:00.000","entry_point":"ISumitWA","files":[{"href":"https://localhost:50443/contest/submissions/3/files"}]}}

            if ("submissions".equals(eventType)) {
                System.out.println("found submission event");

                Object obj = map.get("data");

                Map<String, String> eventDataMap = ( Map<String, String> ) obj;
                ShadowRunSubmission runSubmission = createRunSubmission(eventDataMap);
                return runSubmission;
            }
        }
        return null;
    }

    protected static ShadowRunSubmission createRunSubmission(Map<String, String> eventDataMap) {
        return new ObjectMapper().convertValue(eventDataMap, ShadowRunSubmission.class);
    }
 
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }

}
