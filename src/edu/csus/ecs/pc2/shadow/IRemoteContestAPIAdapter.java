// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.InputStream;
import java.util.List;

import edu.csus.ecs.pc2.core.model.IFile;

/**
 * This interface defines the methods which implementations of adapters to be used in Shadow Mode for connecting to
 * Remote CCS Contest API endpoints must implement.
 * 
 * At least two implementations are expected:  MockRemoteContestAPIAdapter, which provides a local "mock"
 * implementation of a Remote CCS CLICS Contest API, and RemoteContestAPIAdapter, which provides an
 * implementation of an actual connection to a remote CCS.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
interface IRemoteContestAPIAdapter {
    
    /**
     * Returns a {@link RemoteContestConfiguration} object containing the configuration of a contest in a
     * remote CCS, including:
     * <pre>
     *   - judgement types
     *   - languages
     *   - problems
     *   - groups
     *   - organizations
     *   - teams
     *   - contest state
     * </pre>.
     * 
     * @return the configuration of a remote CCS contest
     */
    public RemoteContestConfiguration getRemoteContestConfiguration();
    
    /**
     * Returns an {@link InputStream} from which CLICS events from a remote
     * contest can be read.
     * 
     * @return the remote system event feed input stream
     */
    public InputStream getRemoteEventFeedInputStream() ;
    
    /**
     * Returns a {@link List} of PC2 {@link IFile} objects associated with the specified
     * submission on the remote CCS.
     * 
     * The submission files are obtained by doing a GET operation on the URL defined
     * by concatenating the remote system base URL with the String "/submissions/<id>/files",
     * where <id> is the specified submissionID.
     * 
     * @param the submission ID whose files are to be fetched from the remote system
     * 
     * @return a List of IFile objects containing the files from the submission in 
     *          the remote system
     * 
     */
    public List<IFile> getRemoteSubmissionFiles(int submissionID) ;

    
    /**
     * Test connection to remote CCS API.
     * @return true if could connect, otherwise false;
     */    
    public boolean testConnection();
    
    
    /**
     * Returns the JSON string obtained by doing a GET on the specified URL endpoint.
     * 
     * If the specified endpoint String starts with a valid protocol (e.g., "http"), 
     * then the String is assumed to be a complete URL and is used in the GET operation 
     * as given.  If the endpoint String does not start with a valid protocol then the
     * endpoint string is appended to the current Remote System URL contained in the
     * corresponding implementation, and the resulting URL is used to perform the GET.
     * If the specified endpoint String starts with an unsupported protocol then null
     * is returned.
     * 
     * @param endpoint the endpoint of the URL to be used
     * 
     * @return the JSON String returned from a GET on the URL, or null
     */
    public String getRemoteJSON(String endpoint);
    
}
