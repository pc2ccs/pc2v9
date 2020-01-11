// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IFileImpl;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.shadow.AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT;
import edu.csus.ecs.pc2.util.ContestConverter;

/**
 * This class provides a "mock" implementation of connections to a Remote CCS CLICS Contest API.
 * It returns data as if it were connected to an actual remote CCS.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class MockContestAPIAdapter implements IRemoteContestAPIAdapter {

    private URL remoteURL;
    private String login;
    private String password;
    /**
     * The location of a CDP.  If the remoteURL can be used to
     * find a CDP, use data from that CDP.
     */
    private File cdpConfigDirectory = null;
    
    // TODO TODAY use sample contest CDP that has event feed and submissions
    // TODO use remoteURL to find CDP.

    /**

     * @param remoteURL
     * @param login
     * @param password
     */
    public MockContestAPIAdapter(URL remoteURL, String login, String password) {
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        
        if ("file".equals(remoteURL.getProtocol())) {
            // CDP on local disk
            cdpConfigDirectory = new File(remoteURL.getFile());
        }
    }
  
    @Override
    /**
     * {@inheritDoc}
     * 
     */
    public RemoteContestConfiguration getRemoteContestConfiguration() {

        Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> remoteConfigMap = new HashMap<AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>>();

        if (cdpConfigDirectory != null){
            // CDP on local disk
            remoteConfigMap = loadFileCDPMap(cdpConfigDirectory, remoteConfigMap);
        } else {
            // TODO TODAY implement me - add mock data into RemoteContestConfiguration
        }

        return new RemoteContestConfiguration(remoteConfigMap);
    }
    
    /**
     * 
     * @param configDirectory config directory for CDP
     * @param remoteConfigMap map to add to, if null creates a new Map
     * @return
     */
    protected Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> loadFileCDPMap(File configDirectory, Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> remoteConfigMap) {

     
        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
        
        IInternalContest contest = loader.fromYaml(null , configDirectory.getAbsolutePath(), false);
        
        return ContestConverter.createConfigMap(remoteConfigMap, contest);
    }

    public InputStream readRemoteCCSEventFeedFromFile(File file) {
        PacedFileInputStream efEventStreamReader;
        try {
            int secondsPauseForEAchLine = 1;
            efEventStreamReader = new PacedFileInputStream(file,secondsPauseForEAchLine);
            return efEventStreamReader;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     * 
     * This implementation returns local data taken from a file.
     */
    public InputStream getRemoteEventFeedInputStream(){
        
        /**
         * Test JSON event feed.
         */
//        String filename = "testdata/PacedFileInputStreamTest/csus-f2019.eventfeed.json";
        String filename = "testdata/PacedFileInputStreamTest/ACPC2019.EventFeed.ReplayEnhanced.json";

    	
        if (! Utilities.fileExists(filename)){
            throw new RuntimeException(new FileNotFoundException(filename));
        }
        return readRemoteCCSEventFeedFromFile(new File(filename));
   
    }

    @Override
    /**
     * {@inheritDoc}
     * 
     */
    public boolean testConnection() {
        return true;
    }

    @Override
    /**
     * {@inheritDoc}
     * 
     */
    public String getRemoteJSON(String endpoint) {

        // TODO Add more data per end point.
        
        // return /contests JSON for now.
        
        return "{\"id\":\"Default--7119618407729322012\",\"name\":\"Sumit8\",\"formal_name\":\"Sumit Eight Problems\"," +
                "\"start_time\":\"2019-12-01T05:49:29.785-08\",\"duration\":\"5:00:00\",\"scoreboard_freeze_duration\":\"01:00:00\",\"penalty_time\":20}";
    }

    @Override
    /**
     * {@inheritDoc}
     * 
     * This Mock implementation method checks the currently-defined "remoteURL"; if it starts with "file://" then the rest of the URL
     * is assumed to be a path to a folder containing "PC2 ExtractReplyRuns" files, and the specified submissionID is assumed to
     * identify a subfolder beneath that path which contains the file(s) to be returned.
     * If the current remoteURL does NOT start with "file://", this method returns the source code for the ISumit.java sample
     * program.
     * 
     * Note: when fetching submissions from a PC2 Extract Reply Runs folder, this Mock implementation method assumes that the
     * specified submissionID is actually (just) the numeric part of the submission ID (e.g. "10001").  However, the 
     * EventFeedReplayRunsMerger tool, used to create an Event Feed and corresponding Extract Replay Runs for use with this Mock
     * Adapter, actually stores all runs under folders named like "site1run10001".  This method therefore adds the string "site1run"
     * to the given submissionID.  This means that it assumes that all runs come from "Site 1", even if the original contest was
     * a multi-site contest.   This works fine as long as the original contest adhered to the requirement to use "baseRunNumber"
     * at each site to insure that run (submission) numbers are unique. If the specified (assumed unique) submissionID cannot
     * be found under "site1runXXX" then the method tries to locate it under "site2runXXX".  If the submission cannot be found
     * in either place then an empty files list is returned.
     * 
     */
    public List<IFile> getRemoteSubmissionFiles(String submissionID) {
        
        List<IFile> files = new ArrayList<IFile>();
        
        //check the current URL
        if (remoteURL!=null && remoteURL.toString().startsWith("file://")) {
            
            //we're sending mock data from a PC2 Extract Replay Runs contest;
            //get the path to the submission folder
            String pathToSourceFiles = remoteURL.toString().substring(7) + "/site1run" + submissionID + "/";
            
            System.out.println("getRemoteSubmissionFiles() looking for files for submission " + submissionID
                                + " in folder '" + pathToSourceFiles + "'");
            
            File folder = new File(pathToSourceFiles);
            File[] listOfFiles = folder.listFiles();
            
            if (listOfFiles==null) {
                //try site 2
                pathToSourceFiles = remoteURL.toString().substring(7) + "/site2run" + submissionID + "/";
                
                System.out.println("no files found; looking instead in folder '" + pathToSourceFiles + "'");

                folder = new File(pathToSourceFiles);
                listOfFiles = folder.listFiles();
            }
            
            //see if we found any files
            if (listOfFiles!=null && listOfFiles.length>0) {
                
                //we found some files; walk the source folder fetching each file and adding it to the list of IFiles to return
                for (File file : listOfFiles) {
                    
                    if (file.isFile()) {
                        String filename = file.getName();
                        System.out.println("  Adding file: " + filename);
                        
                        try {
                            //read the source file
                            String[] src_lines = Utilities.loadFile(pathToSourceFiles + File.separator + filename);
                            String source = String.join(System.lineSeparator(), src_lines);
                            //convert the source to Base64
                            String base64String = Base64.getEncoder().encodeToString(source.getBytes());
                            //construct an IFile containing the source (in Base64)
                            IFile currentFile = new IFileImpl(filename, base64String);
                            //add the file to the list
                            files.add(currentFile);
                        } catch (IOException e) {
                            // TODO log this error
                            e.printStackTrace();
                        }
                    }//end isFile()
                }//end for file:listOfFiles
            } else {
                System.out.println ("No files found for submission " + submissionID + " !?!");
            }
            
            return files ;
            
        } else {
            //we don't know how to use the URL; just return some sample source code
            try {
                String filename = "ISumit.java";
                String[] src_lines = Utilities.loadFile(getSamplesSourceFilename(filename));
                String source = String.join("", src_lines);
                
                String base64String = Base64.getEncoder().encodeToString(source.getBytes());
                IFile mainFile = new IFileImpl(filename, base64String);
                
                files.add(mainFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return files;
        }
    }
    
    /**
     * Get sample source or data filename.
     * 
     * Examples.
     * <pre>
     * String filename = getSamplesSourceFilename("Sumit.java");
     * 
     * String filename = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
     * 
     * </pre>
     *  
     * @param filename the name of a file
     * @return the full path name to the specified file
     */
    public String getSamplesSourceFilename(String filename) {

        String name = getTestSamplesSourceDirectory() + File.separator + filename;
        return name;
    }
 
    public String getTestSamplesSourceDirectory() {
        return "samps" + File.separator + "src";
    }
    
    public File getCdpConfigDirectory() {
        return cdpConfigDirectory;
    }
    
    
}
