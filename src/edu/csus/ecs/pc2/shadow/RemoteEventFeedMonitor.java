// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
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
            
//            TODO: deal with this case
            
        } else {
            
            //wrap the event stream (which consists of newline-delimited character strings representing events)
            // in a BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));
            
            try {
                //keep reading event strings as long as the input stream is open and we haven't been told to stop
                String event = "";
                while( ((event = reader.readLine()) != null) && keepRunning) {                    

                    
                    System.out.println ("Got event: " + event);
                    
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
                    //hard-coded replacement examples (temporary)
                    String teamID = "team2";
                    String problemID = "sumit";
                    String languageID = "java";
                    String submissionID = "5";

                    Long overrideSubmissionID = Long.parseLong(submissionID);

                    String time = "2019-04-01T09:08:11.687+01:00" ;                        
//                    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
                    Date date = Date.from( Instant.parse(time));
                    Long overrideTimeMS = date.getTime();

                    List<IFile> files = remoteContestAPIAdapter.getRemoteSubmissionFiles(submissionID);

                    List<IFile> auxFiles = files.subList(1, files.size()-2);

                    submitter.submitRun(teamID, problemID, languageID, files.get(0), auxFiles, 
                            overrideTimeMS, overrideSubmissionID);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }

}
