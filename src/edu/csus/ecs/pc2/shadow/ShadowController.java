// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

import edu.csus.ecs.pc2.core.IInternalController;

public class ShadowController {

    private IInternalController controller;

    private URL remoteURL;

    private String login;

    private String password;

    public ShadowController(IInternalController controller) {
        // TODO Bug 1624 code
        
       /*
        * get remoteURL, login, and pw from local contest
        * this(controller,remoteURL,login,pw);
        */

    }

    public ShadowController(IInternalController controller, URL remoteURL, String login, String password) {
        // TODO Bug 1624 code
        this.controller = controller;
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;

    }

    /**
     * This method starts the Shadow Mode operations running.  It obtains an adapter with which to access
     * a remote CCS, then uses the adapter to fetch the remote contest configuration.   
     * It then compares the obtained (remote) contest configuration 
     */
    public void start() {

        IRemoteContestAPIAdapter adapter = new MockContestAPIAdapter(remoteURL, login, password);
        
//        RemoteContest remoteContest = new RemoteContest(ShadowContestComparer.getRemoteContest(remoteURL, login, password));
        RemoteContest remoteContest = new RemoteContest(adapter.getRemoteContestConfiguration());
        
        //TODO: figure out how this comparison should work 
//        ShadowContestComparator comp = new ShadowContestComparator(remoteContest.getContestModel());
//        if (!comp.isSameAs(controller.getContest())) {
//            throw new ContestsDoNotMatchException();
//        }
        
        //if we get here we know the remote contest configuration matches the local contest configuration
        
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(controller);

        RemoteRunMonitor monitor = new RemoteRunMonitor(remoteURL, login, password, (Runnable) submitter);

        monitor.startListening();
    }

}
