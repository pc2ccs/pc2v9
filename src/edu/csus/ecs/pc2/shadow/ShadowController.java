// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class is essentially a <A href="https://en.wikipedia.org/wiki/Facade_pattern"><I>facade</i></a> for the
 * PC2 "Shadow Mode" facility, which can be used to "shadow" a remote Contest Control System -- that is, to
 * monitor the remote CCS by obtaining runs from it and computing standings in parallel for purposes of verification
 * of the remote system.
 * 
 * The class is instantiated with an {@link IInternalController} (a PC2 Controller for a local instance of a contest).
 * It obtains, from the local PC2 Server, information on the remote system to be shadowed, and uses classes behind (comprising) 
 * the facade to obtain team submissions from the remote sytem.
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

    private URL remoteCCSURL;

    private String remoteCCSLogin;

    private String remoteCCSPassword;

    private RemoteRunMonitor monitor;

    /**
     * Constructs a new ShadowController for the remote CCS specified by the input parameters. ??? 
     * 
     * @param localContest a PC2 Contest to be used by the Shadow Controller
     * @param localController a PC2 Controller to be used by the Shadow Controller
     */
    public ShadowController(IInternalContest localContest, IInternalController localController) {

        //TODO: figure out how to get the Remote CCS info out of the local contest/server
//        this(localContest, localController, localContest.getRemoteCCSURL(), localContest.getRemoteCCSLogin(), getRemoteCCSPasswd);

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
                            URL remoteURL, String remoteCCSLogin, String remoteCCSPassword) {

        this.localContest = localContest;
        this.localController = localController;
        this.remoteCCSURL = remoteURL;
        this.remoteCCSLogin = remoteCCSLogin;
        this.remoteCCSPassword = remoteCCSPassword;
    }

    /**
     * This method starts Shadow Mode operations running.  It obtains an adapter with which to access
     * a remote CCS, then uses the adapter to fetch the remote contest configuration.   
     * It then compares the obtained (remote) contest configuration with the local contest configuration,
     * and if they match then it creates and starts a {@link RemoteRunMonitor} listening to the remote contest
     * specified by the remote contest URL provided when the class is constructed.
     */
    public void start() {

        IRemoteContestAPIAdapter adapter = new MockContestAPIAdapter(remoteCCSURL, remoteCCSLogin, remoteCCSPassword);
        
        //TODO: figure out the relationship between "RemoteContest" and the data returned by "getRemoteContestConfiguration()"
//        RemoteContest remoteContest = new RemoteContest(ShadowContestComparer.getRemoteContest(remoteURL, login, password));
        RemoteContest remoteContest = new RemoteContest(adapter.getRemoteContestConfiguration());
        
        //TODO: figure out how this comparison should work 
//        ShadowContestComparator comp = new ShadowContestComparator(remoteContest.getContestModel());
//        if (!comp.isSameAs(controller.getContest())) {
//            throw new ContestsDoNotMatchException();
//        }
        
        //if we get here we know the remote contest configuration matches the local contest configuration
        
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(localController);

        monitor = new RemoteRunMonitor(remoteCCSURL, remoteCCSLogin, remoteCCSPassword, (Runnable) submitter);

        monitor.startListening();
    }
    
    /**
     * This method stops Shadow Mode operations.  
     */
    public void stop() {
        
        if (monitor!=null) {
            
            monitor.stopListening();
            
            //garbage-collect the monitor
            monitor = null;        
        }
    }
}
