// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

/**
 * This class implements a listener for Submissions from a remote CLICS Contest API.
 *  
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteEventFeedListener implements Runnable {
    
    private boolean keepRunning ;
    private URL remoteURL;
    private String login;
    private String password;
    private RemoteRunSubmitter submitter;

    public RemoteEventFeedListener(URL remoteURL, String login, String password, Runnable submitter) {
        // TODO code (see bug 1626)
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        if (submitter instanceof RemoteRunSubmitter) {
            this.submitter = (RemoteRunSubmitter) submitter;            
        } else {
            //there probably is a better exception to throw than this?
            throw new ClassCastException("RemoteEventFeedListener received something other than a RemoteRunSubmitter");
        }

    }

    @Override
    public void run() {
        // TODO code

        keepRunning = true;
        
        //open connection to remoteURL event-feed endpoint
        
        while (keepRunning) {
            //read next event object
            // if (event-type == submission) {
            //   extract data from submission object (the entire submission will be in the event);
            //   invoke RemoteRunSubmitter.submitRun(team,prob,lang,files,time,id);
            // }
        }


    }
    
    /**
     * This method is used to terminate the RemoteEventFeedListener thread.
     */
    public void stop() {
        keepRunning = false;
    }
}
