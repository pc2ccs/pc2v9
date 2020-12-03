// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

/**
 * This class implements a listener for Submissions from a remote CLICS Contest API.
 *  
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteEventFeedListenerObsolete implements Runnable {
    
    private boolean keepRunning ;
    private IRemoteContestAPIAdapter remoteContestAPIAdapter;
    private URL remoteURL;
    private String login;
    private String password;
    private RemoteRunSubmitter submitter;


    public RemoteEventFeedListenerObsolete(IRemoteContestAPIAdapter remoteContestAPIAdapter, URL remoteURL, String login, String password, RemoteRunSubmitter submitter) {
        // TODO code (see bug 1626)
        this.remoteContestAPIAdapter = remoteContestAPIAdapter;
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        this.submitter = submitter;            
    }

    @Override
    public void run() {
        // TODO code

        keepRunning = true;
        
        //open connection to remoteURL event-feed endpoint
        
        System.out.println("RemoteEventFeedListner: would have attempted to connect to Remote CCS through adapter " + remoteContestAPIAdapter 
                            + " at URL '" + remoteURL 
                            + "' using login '" + login + "' and password '" + password + "'");
        
        System.out.println ("Would have started listening to remote event feed...");
        System.out.println ("Would have submitted any remote runs to the local PC2 system using submitter " + submitter);
        while (keepRunning) {
            
            //read next event object
            // if (event-type == submission) {
            //   extract data from submission object (the entire submission will be in the event);
            //   invoke RemoteRunSubmitter.submitRun(team,prob,lang,files,time,id);
            // }
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println ("Still 'listening'...");

        }


    }
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }
}
