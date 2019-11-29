// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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
 * the facade to obtain team submissions from the remote system.
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

    private String remoteCCSURLString;

    private String remoteCCSLogin;

    private String remoteCCSPassword;

    private RemoteRunMonitor monitor;
    
    private ShadowContestComparator comparator; 

    
    public enum SHADOW_CONTROLLER_STATUS {SC_NOT_STARTED, SC_STARTING, SC_RUNNING, SC_CONNECTION_FAILED, SC_CONTEST_CONFIG_MISMATCH};
    
    private SHADOW_CONTROLLER_STATUS controllerStatus = SHADOW_CONTROLLER_STATUS.SC_NOT_STARTED ;

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

        System.out.println ("ShadowController: starting shadowing for URL '" + remoteCCSURLString 
                            + "' using login '" + remoteCCSLogin + "' and password '" + remoteCCSPassword + "'");
        
        controllerStatus = SHADOW_CONTROLLER_STATUS.SC_STARTING;
        
        //verify that the current "URL string" is a valid URL
        URL remoteCCSURL = null;
        try {
            remoteCCSURL = new URL(remoteCCSURLString);
        } catch (MalformedURLException e) {
            localController.getLog().log(Level.WARNING, "Malformed Remote CCS URL: \"" + remoteCCSURLString + "\" ", e);
            e.printStackTrace();
            controllerStatus = SHADOW_CONTROLLER_STATUS.SC_CONNECTION_FAILED ;
            return false;
        }
        
        //get an adapter which connects to the remote Contest API
        IRemoteContestAPIAdapter remoteContestAPIAdapter = new MockContestAPIAdapter(remoteCCSURL, remoteCCSLogin, remoteCCSPassword);
        
        //get a string representation of the remote contest from the adapter
        String remoteConfigString  = remoteContestAPIAdapter.getRemoteContestConfiguration();
        
        //construct a remote contest configuration object from the string obtained from the remote adapter
        RemoteContestConfiguration remoteContestConfig = new RemoteContestConfiguration(remoteConfigString);
        
        //construct a comparator for comparing the remote contest with the local contest
        comparator = getComparator(remoteContestConfig);
        
        //check if the local contest has the same config as the remote contest (the one being shadowed)
        if (!comparator.isSameAs(localContest)) {
            
            //get the configuration differences
            List<String> diffs = comparator.diff(localContest);
            
            //log the differences
            Log log = localController.getLog();
            log.log(Level.WARNING, "Local contest configuration does not match configuration of remote CCS; cannot proceed with shadowing");
            logDiffs(log,diffs);
            
            controllerStatus = SHADOW_CONTROLLER_STATUS.SC_CONTEST_CONFIG_MISMATCH ;  
          
            //TODO: It would  be nice for the invoker
            // to be able to obtain a list of the differences which caused the configuration comparison to fail.
            // Perhaps there needs to be a "getReason()" method in this class, along with a "getConfigurationDifferences()" method?
            
            // Or perhaps "throw new ContestsDoNotMatchException" instead of returning false?
            
            return false;
            
        }
        
        //if we get here we know the remote contest configuration matches the local contest configuration
        
        //construct a RunSubmitter that can be used to submit runs (received from the remote contest) to the local PC2 contest
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(localController);

        //construct a RunMonitor for keeping track of the remote CCS
        monitor = new RemoteRunMonitor(remoteContestAPIAdapter, remoteCCSURL, remoteCCSLogin, remoteCCSPassword, submitter);

        //start the RunMonitor listening for runs from the remote CCS
        boolean monitorStarted = monitor.startListening();
        
        return monitorStarted;
    }
    
    /**
     * Returns a singleton instance of a ShadowContestComparator configured with the specified 
     * {@link RemoteContestConfiguration}.
     * 
     * @param remoteContestConfig the remote contest used by the comparator
     * 
     * @return a ShadowContestComparator configured with the specified remote contest configuration
     */
    private ShadowContestComparator getComparator(RemoteContestConfiguration remoteContestConfig) {
        if (comparator==null) {
            comparator =  new ShadowContestComparator(remoteContestConfig);
        }
        
        return comparator;
    }

    /**
     * This method writes the given list of differences between the local and remote contest configurations into the specified log.
     * 
     * @param diffs a List<String> giving the configuration differences
     */
    private void logDiffs(Log log, List<String> diffs) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
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

    /**
     * @return the controllerStatus
     */
    public SHADOW_CONTROLLER_STATUS getControllerStatus() {
        return controllerStatus;
    }

    /**
     * Updates the ShadowController status.
     * 
     * @param controllerStatus the controllerStatus to set
     */
    protected void setControllerStatus(SHADOW_CONTROLLER_STATUS controllerStatus) {
        this.controllerStatus = controllerStatus;
    }
}
