// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.report.EventFeedJSONReport;
import edu.csus.ecs.pc2.core.report.ExtractPlaybackLoadFilesReport;

/**
 * This class merges a set of PC2 "replay runs" (as produced by {@link ExtractPlaybackLoadFilesReport},
 * more commonly known as an "Extract Replay Runs" report) with an Event Feed JSON file (such as that produced by
 * {@link EventFeedJSONReport} or by the PC2 WebServer's event-feed endpoint), 
 * producing a single Event Feed JSON file.
 * 
 * This provides a mechanism for simulating the Event Feed which would come from a remote Contest API, 
 * since the current PC2 Event Feed does not contain the CLICS "files:[]" element which is necessary for
 * knowing how to access the remote system files associated with a submission.
 * 
 * The input event feed and runs files are merged into a single updated event feed file which is saved
 * to a specified file (or to stdout if no output file is specified).    
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ReplayRunsFormatter {

    /**
     * Run the ReplayRunsFormatter.  Reads the specified input files and produces a merged
     * output event feed file.
     * 
     * @param args 
     *          [0]: the path to a file containing a PC2 JSON event feed  
     *          [1]: the path to a file containing a PC2 Extract Replay Runs report 
     *          [2]: optional path to a newly-created event feed output file (defaults to stdout).  
     *          
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        if (args.length<2) {
            usage();
            System.exit(1);
        }
        
        File pc2ef = new File(args[0]);
        File pc2Runs = new File(args[1]);
        
        if (!pc2ef.exists() || !pc2Runs.exists()) {
            System.out.println ("Cannot find required input files");
            usage();
            System.exit(2);
        }
        
        String outputFileName = null;
        if (args.length>2) {
            outputFileName = args[2];
        }
        
        Scanner scanner ;
        
        int actionCount = 0;
        //a map to hold submission actions
        Map<String,Map<String,String>> submissionActions = new HashMap<String,Map<String,String>>();
        //a list to hold duplicated submission actions
        List<Map<String,String>> duplicateSubmissionActions = new ArrayList<Map<String,String>>();
        //a map to hold judgement actions
        Map<String,Map<String,String>> judgementActions = new HashMap<String,Map<String,String>>();
        //a map to hold duplicated judgement actions
        List<Map<String,String>> duplicateJudgementActions = new ArrayList<Map<String,String>>();
        
        //read all replay actions into a map, checking for duplicates and saving them separately
        scanner = new Scanner(pc2Runs);

        while (scanner.hasNextLine()) {
           String line = scanner.nextLine();
           //discard any information in the runs report which is not an event           
           if (line.trim().startsWith("action")) {
               
               actionCount ++ ;
               
               //convert the action to a map
               Map<String,String> singleActionMap = getActionMap(line);
               
               //see what kind of action it is
               String actionType = singleActionMap.get("action");
               
               if (actionType!=null && !actionType.equals("")) {
                   switch (actionType) {
                       case "RUN_SUBMIT":
                           //check if we already have a submission action with this ID
                           String submissionID = singleActionMap.get("id");
                           if (submissionActions.containsKey(submissionID)) {
                               //we've already seen such an action; put this one in the duplicates list
                               duplicateSubmissionActions.add(singleActionMap);
                           } else {
                               //save the action in the allSubmissionActions map under the submission ID
                               submissionActions.put(submissionID, singleActionMap);
                           }
                           break;
                           
                       case "RUN_JUDGEMENT":
                           //check if we already have a judgement action with this ID
                           String judgementID = singleActionMap.get("id");
                           if (judgementActions.containsKey(judgementID)) {
                               //we've already seen such an action; put this one in the duplicates list
                               duplicateJudgementActions.add(singleActionMap);
                           } else {
                               //save the action in the allJudgementActions map under the submission ID
                               judgementActions.put(judgementID, singleActionMap);
                           }
                           break;
                       default: 
                           //we have an action we don't know about
                           System.err.println ("Unknown action in Extract Replay Runs file '" + pc2Runs.getName() + "': "
                                               + actionType);
                   }
               }
           }
        }//while scanner.hasNextLine()
        
        scanner.close();

        System.out.println ("Found " + actionCount + " actions in PC2 Extract Replay Runs file '" + pc2Runs.getName() + "'");
        System.out.println ("Put " + submissionActions.keySet().size() + " actions in the submission actions map" );
        System.out.println ("Put " + duplicateSubmissionActions.size() + " actions in the duplicate submission actions list" );
        System.out.println ("Put " + judgementActions.keySet().size() + " actions in the judgement actions map" );
        System.out.println ("Put " + duplicateJudgementActions.size() + " actions in the duplicate judgement actions list" );
        int totalActions = submissionActions.keySet().size() + duplicateSubmissionActions.size() 
                    + judgementActions.keySet().size() + duplicateJudgementActions.size() ;
        System.out.println ("Total actions put in maps/lists = " + totalActions);

        
        if (outputFileName==null) {
            System.out.println ("Sending merged event feed to stdout");
        } else {
            System.out.println ("Sending merged event feed output to file '" + outputFileName + "'");
            //redirect stdout to the file specified by args[2]
            PrintStream outStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFileName)), true);
            System.setOut(outStream);
        }
        
        //note that from this point on, stdout has been redirected if there was a third argument on the command line.
        //Any "console output" after this should be sent to stderr...
        
        //read each event from the pc2ef file
        scanner = new Scanner(pc2ef);
        int totalEventCount = 0;
        int submissionEventCount = 0;
        int updatedSubmissionCount = 0;
        int judgementEventCount = 0;
        int updatedJudgementCount = 0;
        int otherEventCount = 0;
        int errorCount = 0;
        while (scanner.hasNextLine()) {
           String line = scanner.nextLine();
           
           //see if the line matches the structure for events (disregard header lines and comments in the event feed file)
           if (line.trim().startsWith("{") && line.trim().endsWith("}")) {

               totalEventCount++ ;
               
               //convert the pc2 event into a map
               Map<String,Object> eventMap = getEventMap(line);
               
               //get the type of event
               String eventType = (String) eventMap.get("type");
               
               //check if this is an event we may want to update
               if (eventType!=null && (eventType.equals("submissions")||eventType.equals("judgements"))) {
                   
                   switch (eventType) {
                       case "submissions":
                           
                           submissionEventCount++ ;
                           
                           //get the event data out of the submission event
                           Map<String,Object> data = (Map<String,Object>) eventMap.get("data");
                           
                           //get the submission ID out of the event data
                           String eventSubmissionID = (String) data.get("id");
                           
                           //see if we have a valid string for the submission ID from the event feed data
                            if (eventSubmissionID != null && !eventSubmissionID.equals("")) {

                                // see if we have an update for this submission in the actions
                                String actionSubmissionID = submissionActions.get(eventSubmissionID).get("id");
                                if (actionSubmissionID!=null && !actionSubmissionID.equals("")) {

                                    // we found a submission in the event feed with a corresponding action;
                                    // update the submission in the event map
                                    boolean ok = updateSubmissionEventData(eventMap, submissionActions.get(actionSubmissionID));

                                    if (ok) {
                                        updatedSubmissionCount++;
                                    } else {
                                        errorCount++;
                                    }

                                } else {
                                    // we have a submission event with no corresponding action -- does this make sense???
                                    System.err.println("Found submission event in event feed with no corresponding action in ExtractedRuns file!?!");
//                                throw ??? ;  errorCount++ ; ???
                                }

                            } else {
                                //we have a bad submission ID from the event map data
                                System.err.println ("Found bad submission ID in event file data: '" + eventSubmissionID + "'");
                                // throw ??;   errorCount++ ; ???
                            }
                            
                            break; //end of case "submissions"    
                            
                       case "judgements":
                           
                           judgementEventCount++ ;
                           updatedJudgementCount++ ;
                           break;
                           
                       default:
                           System.err.println ("Unexplained condition in main regarding event type " + eventType);
                   }
               } else {
                   otherEventCount++ ;
               }
               
               //get the JSON representation of the (possibly updated) event in the eventMap
               String eventOutputString = getJSON(eventMap);

               if (eventOutputString!=null) {
                //output the JSON event to the output channel
                System.out.println(eventOutputString);
               } else {
                   System.err.println ("Error: converting event to JSON resulted in null");
               }
               
               
           } //if (ef file input line matches event pattern)
        } //while there are more lines in the event feed file
        
        scanner.close();
        
        //reset stdout in case it was redirected to a file
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        
        System.out.println ("Found " + totalEventCount + " events in PC2 Event Feed file '" + pc2ef.getName() + "'");
        System.out.println ("  including " + submissionEventCount + " submission events, " 
                                + judgementEventCount + " judgement events, and " + otherEventCount + " other events");
        System.out.println ("Updated " + updatedSubmissionCount + " submission events");
        System.out.println ("Would have updated " + updatedJudgementCount + " judgement events");
        System.out.println ("Found " + errorCount + " errors during updating");
                
    }

    
    /**
     * Returns a JSON string representing the contents of the specified Map.
     * This method uses the Jackson {@link ObjectMapper#writeValueAsString(Object)} method
     * to generate the JSON string from the specified Map.  That method automatically "escapes"
     * all double-quotes (by prepending a "\" in front of each double-quote), and also automatically
     * escapes all backslashes by prepending them with an additional backslash.
     * This method subsequently removes those additional backslash characters before returning 
     * the resultant JSON.
     * 
     * @param eventMap a Map of String to Objects
     * 
     * @return the JSON corresponding to the given eventMap, or null if an error occurred while converting the specified
     *              eventMap to JSON
     */
    private static String getJSON(Map<String, Object> eventMap) {
        String jsonString;
        
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            jsonString = mapper.writeValueAsString(eventMap);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
        
//      System.out.println ("Before replacement: \n" + eventOutputString);
        
      //replace "backslash-quote" (\") added by Jackson ObjectMapper with just a double-quote (")
        jsonString = jsonString.replaceAll("\\\\\"", "\"");  
      
      //replace double-backslashes (\\) added by Jackson ObjectMapper with just a single backslash (\)
        jsonString = jsonString.replaceAll("\\\\\\\\", "\\\\");   
//      System.out.println ("After replacement: \n" + eventOutputString);


        return jsonString ;
    }


    /**
     * Updates the submission data in the specified eventMap (which was created from a line in the pc2 event feed file)
     * from the data in the specified actionMap (which was created from a line in the PC2 Extract Replay Runs action file).
     *  
     * @param eventMap a map of event feed element names (e.g. "type", "id", "op", or "data") to Objects containing the 
     *                  corresponding value
     * @param actionMap a map of PC2 Extract Replay Runs action element names (e.g. "action", "id", "elapsed", "language",
     *                  "problem", "site", "submitclient", or "mainfile") to Strings containing the corresponding value
     *                  
     * @return true if updating the eventMap was successful; false if an error occurred during updating
     */
    private static boolean updateSubmissionEventData(Map<String, Object> eventMap, Map<String,String> actionMap) {

        //get the data out of the event map
        Map<String,String> eventData = (Map<String,String>) eventMap.get("data");
        
        //verify the event and the action are for the same submission
        if (!eventData.get("id").equals(actionMap.get("id"))) {
            return false ;
        }
        
        //verify the elapsed time in the action map is consistent with the contest time in the event (action map times
        //  are truncated to whole minutes)
        String actionElapsedTime = actionMap.get("elapsed");
        String eventContestTime = eventData.get("contest_time");
        
        Long fullTime = Utilities.convertCLICSContestTimeToMS(eventContestTime);
        if (fullTime!=Long.MIN_VALUE) {
            //truncate the event feed time to whole minutes
            Long efMins = fullTime / (1000*60);  //  1000msec/sec * 60sec/min = whole mins in msec
            
            //compare the event feed minutes with the action minutes
            if (efMins!=Long.parseLong(actionElapsedTime)) {
                System.err.println("Error: event feed time does not match action file time");
                return false;
            }
            
        } else {
            //error in converting event feed contest_time
            System.err.println("Error converting event feed time '" + eventContestTime + "' to seconds");
            return false;
        }
        
        //verify the team id in the action map is consistent with the team id in the event
        String actionTeam = actionMap.get("submitclient");
        String eventTeam = eventData.get("team_id");
        
        //strip the leading "team" off the action id
        int actionTeamNum = Integer.parseInt(actionTeam.substring(4)) ;
        
        //check that the two numbers are the same
        if (actionTeamNum != Integer.parseInt(eventTeam)) {
            System.err.println ("Team numbers in action and event do not match");
            return false;
        }
        
        //copy the action file name(s) into the event
        String actionFileName = actionMap.get("mainfile");
        
        //hack to circumvent the problem that the Extract Replay Runs report fails to include a drive spec on Windows
        if (!actionFileName.startsWith("/") && !actionFileName.matches("^(A-Za-z):\\.*$")) {
            //the file doesn't start with either a "/" or a driveLetter:\ pattern; let's add a default drive letter
            // TODO:  NOTE THAT THIS MAKES THIS CODE ASSUME DRIVE "c:";   THIS NEEDS TO BE FIXED!!!
            actionFileName = "C:\\" + actionFileName ;   
        }
        
        //put the filename from the action into the event
        String updatedFileRef = "[{\"href\":\"" + actionFileName + "\",\"mime\":\"application/zip\"}]" ;
        eventData.put("files", updatedFileRef);
        
        //TODO: *** compare language ids between Action and Event???
        
        //TODO: *** compare problem ids between Action and Event???
        
        
        return true ;
    }


    /**
     * Parses a String containing a PC2 Extract Replay Runs "action" and returns a Map of the elements in the action.
     * 
     * As an example, the action
     * <pre>
     * action=RUN_SUBMIT| id=10001| elapsed=4| language=GNU C++| problem=E| site=1| submitclient=team92| mainfile=.\profiles\Pe19f86b3-a2d5-4097-b74a-a285d5ad2dcd\reports\report.Extract_Replay_Runs.12.15.026.txt.files/site1run10001/template.cpp| 
     * </pre>
     * will be returned as a Map containing
     * <pre>
     *       action  RUN_SUBMIT
     *           id  10001
     *      elapsed  4
     *     language  GNU C++
     *      problem  E
     *         site  1
     * submitclient  team92
     *     mainfile  .\profiles\Pe19f86b3-a2d5-4097-b74a-a285d5ad2dcd\reports\report.Extract_Replay_Runs.12.15.026.txt.files/site1run10001/template.cpp
     * </pre>
     * 
     * If the received action String is null or empty then an empty (but not null) Map is returned.
     * If the action does not split cleanly into fields delimited by a vertical bar ("|"), an empty Map is returned.
     * If any field (separated by "|") does not split cleanly into two sub-fields delimited by an equal sign ("="),
     * that field is omitted from the returned Map.
     * 
     * @param action a String containing a PC2 Extract Replay Runs action
     * 
     * @return a Map mapping action keys to their values
     */
    private static Map<String, String> getActionMap(String action) {
        Map<String,String> retMap = new HashMap<String,String>() ;
        if (action!=null && action.trim().length()>0) {
            String [] fields = action.split("\\|");
            if (fields.length>0) {
                for (String item : fields) {
                    String [] itemFields = item.split("=") ;
                    if (itemFields.length==2) {
                        retMap.put(itemFields[0].trim(), itemFields[1].trim());
                    }
                }
            }
        }
        
        return retMap;
    }


    /**
     * Reads the given JSON string and returns a corresponding Map.
     * Each "key" in the JSON string becomes a key in the returned map; the "value"
     * stored under the key in the Map is the corresponding JSON value.  
     * Operates recursively; each "value" in the returned Map is an Object and any
     * JSON arrays or multi-data elements are returned as sub-Map Objects in the
     * returned Map.
     * 
     * @param jsonString the JSON string to be converted to a Map; must be legal JSON
     * 
     * @return a Map of the JSON keys and corresponding Object values
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> getEventMap(String jsonString) {
        
        if (jsonString == null){
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Prints the expected command-line usage of the class.
     */
    private static void usage() {
        System.out.println("Usage:  java ReplayRunsFormatter pc2eventFeedFile pc2ExtractReplayRunsFile [mergedEventFeedOutputFile]");
    }

}
