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

    private RemoteEventFeedMonitor monitor;

    
    public enum SHADOW_CONTROLLER_STATUS {
        
        SC_NEVER_STARTED("Shadow Controller has never been started"), 
        SC_STARTING("Shadow Controller is starting"), 
        SC_RUNNING("Shadow Controller is running"), 
        SC_STOPPING("Shadow Controller is stopping"), 
        SC_STOPPED("Shadow Controller is stopped"), 
        SC_CONNECTION_FAILED("Shadow Controller failes to connect to remote CCS"),
        SC_INVALID_REMOTE_CONFIG("Shadow Controller received an invalid or null configuration from the remote CCS"),
        SC_CONTEST_CONFIG_MISMATCH("Contest configuration received from remote CCS does not match local PC2 contest configuration"),
        SC_MONITOR_STARTUP_FAILED("Shadow Controller was unable to start a Remote CCS Monitor");
        
        private final String label;
        private SHADOW_CONTROLLER_STATUS(String label) {
            this.label=label;
        }
        public String getLabel() {
            return this.label;
        }
    }
    
    private SHADOW_CONTROLLER_STATUS controllerStatus = null ;
    private RemoteContestConfiguration remoteContestConfig;
    private Thread monitorThread;
    private boolean listening;

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
        this.setStatus(SHADOW_CONTROLLER_STATUS.SC_NEVER_STARTED);
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
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STARTING);
        
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
        
        //get a remote contest configuration from the adapter
        remoteContestConfig  = remoteContestAPIAdapter.getRemoteContestConfiguration();
                        
        //make sure we got a remote config
        if (remoteContestConfig != null) {
            
            //check if the local contest has the same config as the remote contest (the one being shadowed)
            if (!remoteContestConfig.isSameAs(localContest)) {

                //get the configuration differences
                List<String> diffs = remoteContestConfig.diff(localContest);

                //log the differences
                Log log = localController.getLog();
                log.log(Level.WARNING, "Local contest configuration does not match configuration of remote CCS; cannot proceed with shadowing");
                logDiffs(log, diffs);

                setStatus(SHADOW_CONTROLLER_STATUS.SC_CONTEST_CONFIG_MISMATCH);

                return false;

            }
        } else {
            //we didn't get a remote config
            Log log = localController.getLog();
            log.log(Level.WARNING, "Contest configuration from remote CCS is null; cannot proceed with shadowing");
 
            setStatus(SHADOW_CONTROLLER_STATUS.SC_INVALID_REMOTE_CONFIG);
            
            return false;
        }
        
        
        //if we get here we know the remote contest configuration matches the local contest configuration
        
        //construct a RunSubmitter that can be used to submit runs (received from the remote contest) to the local PC2 contest
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(localController);

        try {
            
            //construct an EventFeedMonitor for keeping track of the remote CCS events
            monitor = new RemoteEventFeedMonitor(localController, remoteContestAPIAdapter, remoteCCSURL, remoteCCSLogin, remoteCCSPassword, submitter);
 
            //start the monitor running as a thread listening for submissions from the remote CCS
            monitorThread = new Thread(monitor);
            monitorThread.start();
            
            listening = true;
            
            setStatus(SHADOW_CONTROLLER_STATUS.SC_RUNNING);
            return true;
           
            
        } catch (Exception e) {
            // TODO figure out how to return the exception to the caller cleanly

            setStatus(SHADOW_CONTROLLER_STATUS.SC_MONITOR_STARTUP_FAILED);

            e.printStackTrace();
            return false;
        }
                
    }
    
    /**
     * Returns a list of differences between the currently-configured remote contest
     * and the configuration of the local PC2 contest, or null if no remote contest configuration
     * has been obtained.
     * 
     * @return a List<String> of contest configuration differences, or null if no remote configuration is available
     */
    public List<String> getDiffs() {
        
        List<String> diffs = null;
        if (remoteContestConfig != null) {
            diffs = remoteContestConfig.diff(localContest);
        }
        return diffs ;
    }
    
    /**
     * This method writes the given list of differences between the local and remote contest configurations 
     * into the specified log.
     * 
     * @param diffs a List<String> giving the configuration differences
     */
    private void logDiffs(Log log, List<String> diffs) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    /**
     * This method stops the Shadow Mode listener thread.  
     */
    public void stop() {
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STOPPING) ;
        
        if (monitor!=null) {
            
            monitor.stop();
            
            //garbage-collect the monitor
            monitor = null;        
        }
        
        setStatus(SHADOW_CONTROLLER_STATUS.SC_STOPPED) ;
    }

    /**
     * Returns the current status of this ShadowController.
     * 
     * @return a SHADOW_CONTROLLER_STATUS enum element giving the controller status
     */
    public SHADOW_CONTROLLER_STATUS getStatus() {
        return controllerStatus;
    }

    /**
     * Updates the ShadowController status.
     * 
     * @param controllerStatus the controllerStatus to set
     */
    protected void setStatus(SHADOW_CONTROLLER_STATUS controllerStatus) {
        this.controllerStatus = controllerStatus;
    }
}
