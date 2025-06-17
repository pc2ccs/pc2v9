package config;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class tracks the current contest state for the WTI Server -- in particular, whether the contest has started.
 * 
 * The class tracks contest state using an external file; this allows it to properly update contest state if the
 * webserver is restarted.  The class uses the Jackson {@link ObjectMapper} class to save the state information in JSON format.
 * 
 * Note that currently, only the "started" state of the contest is tracked in the file, but the file could easily 
 * be extended to support other contest state values (such as Paused, Frozen, Ended, etc.)
 * 
 * @author John Clevenger (with help from his buddy ChatGPT...)
 *
 */
public class ContestState {
	
    private static final String STATE_FILE = "contest_state.txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Inner static class to hold serializable state
    public static class StateData {
    	
        public boolean contestHasStarted = false;
        public long startTime = 0;  		// millisecs since the Epoch; initialized in method setStarted()
        public String startTimeISO = null;	// ISO 8601 string, e.g. "2025-06-04T18:23:01Z"

        public StateData() {}  // default constructor for Jackson
        
        @Override
        public String toString() {
        	String retStr = "";
        	retStr += "contestHasStarted:" + contestHasStarted ;
        	retStr += "; startTime:" + startTime ;
        	retStr += "; startTimeISO:" + startTimeISO ;
        	retStr += "\n";
        	return retStr ;
        }
    }
    
    //the file holding the contest state
    private final File stateFile;
    //the in-memory copy of the current state
    private volatile StateData currentState;

    /**
     * Constructs a ContestState object which tracks the current contest state.
     * Contest state is initialized by loading a file from disk; if no previously-created state file
     * exists then a new {@link StateData} object is constructed (and saved to disk).
     */
    public ContestState() {
        this.stateFile = new File(STATE_FILE);
        this.currentState = loadStateFromDisk();
    }

    public synchronized boolean hasStarted() {
        return currentState.contestHasStarted;
    }
    
    /**
     * Sets the current state to the specified value and saves the updated state to disk.
     * If this is the FIRST time the started state has been set true, saves the current time
     * as the contest state start time in both Epoch millisecs and ISO format.  
     * Updating the time only when it's the FIRST call preserves the start time across subsequent calls,
     * e.g. on server restart.
     * 
     * @param started a boolean indicating whether or not the contest has started.
     */
    public synchronized void setStarted(boolean started) {
    	//check if this is the FIRST time we've seen "started == true"
        if (started && !currentState.contestHasStarted) {
        	//first start; save the start time
        	long now = System.currentTimeMillis();
        	currentState.startTime = now;
        	currentState.startTimeISO = Instant.ofEpochMilli(now).toString();
        }
        currentState.contestHasStarted = started;
        saveStateToDisk();
    }


    /**
     * If no state file currently exists on disk this method constructs a new StateData object,
     * saves it to disk, and returns the new object; otherwise (meaning there does exist a state file),
     * the method reads the state file into a StateData object and returns that object.
     * 
     * @return a {@link StateData} object describing the current state of the contest from the WTI Server's viewpoint.
     */
    private StateData loadStateFromDisk() {
    	
        if (!stateFile.exists()) {
            StateData freshState = new StateData();
            saveStateToDisk(freshState);  // Save default state immediately
            return freshState;
        }

        try {
        	//there exists a state file; read it in from disk and return its contents
            return objectMapper.readValue(stateFile, StateData.class);
            
        } catch (IOException e) {
            System.err.println("Failed to read contest state from disk, defaulting to fresh state (i.e., contest has not started): " + e.getMessage());
            StateData fallbackState = new StateData();
            saveStateToDisk(fallbackState);  // Attempt to create the state file
            return fallbackState;
        }
    }

    /**
     * This method is called when the first call to {@link #setStarted(boolean)} is made.  This method delegates
     * to method {@link #saveStateToDisk(StateData)}, using it to save the current state.
     */
    private void saveStateToDisk() {
        saveStateToDisk(this.currentState);
    }

    /**
     * Save the specified contest {@link StateData} to disk.
     * 
     * @param stateToSave a StateData object containing the current state of the contest from the WTI Server's point of view.
     */
    private void saveStateToDisk(StateData stateToSave) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(stateFile, stateToSave);
        } catch (IOException e) {
            System.err.println("Failed to write contest state: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
    	return this.currentState.toString();
    }
 
}
