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

import edu.csus.ecs.pc2.clics.CLICSJudgementType;
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
public class EventFeedReplayRunsMerger {

    //a map to hold global judgement types defined by "judgement-types" events in the event feed
    static Map<String,CLICSJudgementType> judgementTypes = new HashMap<String,CLICSJudgementType>();

    /**
     * Run the EventFeedReplayRunsMerger tool.  Reads the specified input files and produces a merged
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
        int skippedPreliminaryJudgementActionCount = 0;
        //a map to hold submission actions
        Map<String,Map<String,String>> submissionActions = new HashMap<String,Map<String,String>>();
        //a list to hold duplicated submission actions
        List<Map<String,String>> duplicateSubmissionActions = new ArrayList<Map<String,String>>();
        //a map to hold judgement actions
        Map<String,Map<String,String>> judgementActions = new HashMap<String,Map<String,String>>();
        //a list to hold duplicated judgement actions (because we can't store multiple things under the same id in a map)
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
                           //see if this is a "preliminary" judgement
                           String isPrelim = singleActionMap.get("preliminary");
                           
                           //process only if not prelim judgement
                           if (!(isPrelim==null) && !isPrelim.trim().equalsIgnoreCase("true")) {
                               
                               //check if we already have a judgement action with this ID
                               String judgementID = singleActionMap.get("id");
                               if (!judgementActions.containsKey(judgementID)) {
                                   //save the action in the allJudgementActions map under the judgement ID
                                   judgementActions.put(judgementID, singleActionMap);
                               } else {
                                   //we've already seen such an action; put this one in the duplicates list for later
                                   duplicateJudgementActions.add(singleActionMap);
                               }
                           } else {
                               skippedPreliminaryJudgementActionCount++ ;
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
        System.out.println ("Found (and ignored) " + skippedPreliminaryJudgementActionCount + " preliminary judgement actions");
        System.out.println ("Put " + duplicateJudgementActions.size() + " action(s) in the duplicate judgement actions list" );
        if (duplicateJudgementActions.size()>0) {
            System.out.println ("  Duplicate actions:");
            for (Map<String,String> action : duplicateJudgementActions) {
                System.out.print("    ");
                for (String key : action.keySet()) {
                    System.out.print (key + ":" + action.get(key) + " | ");
                }
                System.out.println();
            }
        }
        int totalActions = submissionActions.keySet().size() + duplicateSubmissionActions.size() 
                    + judgementActions.keySet().size() + duplicateJudgementActions.size() ;
        System.out.println ("Total actions put in maps/lists = " + totalActions);

        
        if (outputFileName==null) {
            System.out.println ("\nSending merged event feed to stdout");
        } else {
            System.out.println ("\nSending merged event feed output to file '" + outputFileName + "'");
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
        int verifiedJudgementCount = 0;
        int judgementTypeEventCount = 0;
        int processedJudgementTypeCount = 0;
        int clarEventCount = 0;
        int clarReplacementCount = 0;
        int otherEventCount = 0;
        int errorCount = 0;
        
        /* TODO:
         * *** how to deal with duplicate actions??? 
         * The ExtractReplayRuns report will contain one "RUN_JUDGEMENT" action for EACH JUDGEMENT applied to a submission 
         * (so there may be multiple judgements for the same submission -- e.g. if it was rejudged).
         * The above code adds any such judgements (that is, additional ExtractReplyRuns judgements for which a previous
         * judgement for the same submission has already been received) to the "duplicateJudgementActions" list, thus saving them.
         * 
         * However, the EventFeed.json output appears to contain only ONE judgement (the FINAL one) for each submission.
         * This means we'll only receive ONE judgement for each submission while reading the pc2ef file 
         * (which is what the following loop does).
         * 
         * Should we just ignore duplicate RUN_JUDGEMENT Actions in the ExtractReplayRuns file?
         * (Can we ignore them? Don't they represent "updates" to run judgements??)
         */
        
        while (scanner.hasNextLine()) {
           String line = scanner.nextLine();
           
           //see if the line matches the structure for events (disregard header lines and comments in the event feed file)
           if (line.trim().startsWith("{") && line.trim().endsWith("}")) {

               totalEventCount++ ;
               
               //convert the pc2 event into a map
               Map<String,Object> eventMap = getEventMap(line);
               
//               System.out.println("Elements in eventMap:");
//               for (String key : eventMap.keySet()) {
//                   System.out.println(key + "  " + eventMap.get(key));
//               }
               
               //get the type of event
               String eventType = (String) eventMap.get("type");
               
               //check if this is an event we want to process
               if (eventType!=null) {
                   
                   String eventSubmissionID ;
                   
                   switch (eventType) {
                       case "submissions":
                           
                           submissionEventCount++ ;
                           
                           //get the event data out of the submission event
                           Map<String,Object> submissionData = (Map<String,Object>) eventMap.get("data");
                           
//                           System.out.println("submissionData map contains:");
//                           for (String key : submissionData.keySet()) {
//                               System.out.println (" " + key + " " + submissionData.get(key));
//                           }
                           
                           //get the submission ID out of the event data
                           eventSubmissionID = (String) submissionData.get("id");
                           
                           //see if we have a valid string for the submission ID from the event feed data
                            if (eventSubmissionID != null && !eventSubmissionID.equals("")) {

                                // see if we have an update for this submission in the actions
                                String actionSubmissionID = submissionActions.get(eventSubmissionID).get("id");
                                if (actionSubmissionID!=null && !actionSubmissionID.equals("")) {

                                    // we found a submission in the event feed with a corresponding action;
                                    // update the submission in the event map
                                    boolean ok = updateSubmissionEventData(eventMap, submissionActions.get(actionSubmissionID));

//                                    System.out.println ("Retrieved from eventMap:");
//                                    System.out.println (((Map<String,Object>)eventMap.get("data")).get("files"));
                                    if (ok) {
                                        updatedSubmissionCount++;
                                    } else {
                                        errorCount++;
                                    }

                                } else {
                                    // we have a submission event with no corresponding action -- does this make sense???
                                    System.err.println("Found submission event in event feed with no corresponding action in ExtractedRuns file!?!");
                                    errorCount++ ; 
                                }

                            } else {
                                //we have a bad submission ID from the event map data
                                System.err.println ("Found bad submission ID in submission event in event file data: '" + eventSubmissionID + "'");
                                errorCount++ ;
                            }
                            
                            break; //end of case "submissions"    
                            
                       case "judgements":
                           
                           judgementEventCount++ ;

                           //get the event data out of the judgement event
                           Map<String,Object> judgementData = (Map<String,Object>) eventMap.get("data");
                           
                           //get the judgement ID out of the event data
                           String eventJudgementID = (String) judgementData.get("id");
                           
                           //get the submission ID out of the event data
                           eventSubmissionID = (String) judgementData.get("submission_id");
                           
                           //see if we have a valid string for both the submissionID and the judgementID from the event feed data
                            if (eventSubmissionID != null && !eventSubmissionID.equals("")
                                    && eventJudgementID!=null && !eventJudgementID.equals("")) {

                                // see if we have a run_judgement action in the actions map for this judgement event
//                                String actionJudgementID = judgementActions.get(eventJudgementID).get("id");
                                String actionJudgementID = judgementActions.get(eventSubmissionID).get("id");
                                
                                if (actionJudgementID!=null && !actionJudgementID.equals("")) {

                                    // we found a judgement in the action map that corresponds to an event feed judgement;
                                    // verify that all corresponding fields match between the action and the event
                                    boolean ok = verifyJudgementEventData(eventMap, judgementActions.get(actionJudgementID));

                                    if (ok) {
                                        verifiedJudgementCount++;
                                    } else {
                                        errorCount++;
                                    }

                                } else {
                                    // we have a submission event with no corresponding action -- does this make sense???
                                    System.err.println("Found judgement event in event feed with no corresponding action in ExtractedRuns file!?!");
                                    errorCount++ ;
                                }

                            } else {
                                //we have a bad submission ID from the event map data
                                System.err.println ("Found bad submission ID or judgement ID in judgement event in event file data: " 
                                            + " submissionID = '"+ eventSubmissionID + "';"
                                            + " judgementID = '" + eventJudgementID + "'");
                                errorCount++ ; 
                            }   
                           
                           break;  //end of case "judgements"
                           
                       case "judgement-types" :
                           
                           judgementTypeEventCount++ ;
                           
                           //check whether we're adding a judgement type, or deleting one
                           String operation = (String) eventMap.get("op");
                           
                           //get the event data out of the judgement-types event
                           Map<String,Object> judgementTypesData = (Map<String,Object>) eventMap.get("data");

                           //get the id (acronym) out of the data
                           String acronym = (String) judgementTypesData.get("id");
                           
                           if (operation!=null) {
                               if (operation.equals("delete")) {
                                   judgementTypes.remove(acronym);
                               } else {
                                   //add a new judgement type to the Map of known judgements
                                   String name = (String) judgementTypesData.get("name");
                                   boolean penalty = (boolean) judgementTypesData.get("penalty");
                                   boolean solved = (boolean) judgementTypesData.get("solved");
                                   CLICSJudgementType newJudgement = new CLICSJudgementType(acronym,name,penalty,solved);
                                   judgementTypes.put(acronym, newJudgement);
                               }
                               
                               processedJudgementTypeCount++ ;  
                               
                           } else {
                               System.err.println ("Found null operation in event feed");
                               errorCount++ ;
                           }
                           
                           break;  //end of case "judgement-types"
                           
                       case "clarifications" :
                           
                           clarEventCount++ ;
                           
                           //need to escape any double-quotes in the clar text
                           
                           //get the event data out of the clarification event
                           Map<String,Object> clarData = (Map<String,Object>) eventMap.get("data");
                           
                           //get the clar text out of the data
                           String clarText = (String) clarData.get("text");
                           
                           if (clarText.contains("\"")) {
                               
                               //send diagnostics to stderr because stdout may have been redirected (per above)
//                               System.err.println("Found clar text: [" + clarText + "]");
                               clarText = clarText.replaceAll("\"", "\\\\\"");
//                               System.err.println("Replacing with : [" + clarText + "'");
                               
                               clarData.put("text", clarText);
                               
                               clarReplacementCount++;
                           }
                           
                           break;  //end of case "clarifications"
                           
                       default:
                           otherEventCount++ ;
                           
                   }
               } else {
                   System.err.println ("Found null event in input");
                   errorCount++ ;
               }
               
               //get the JSON representation of the (possibly updated) event in the eventMap
               String eventOutputString = getJSON(eventMap);
               
//               System.out.println ("JSON from eventMap:");
//               System.out.println (eventOutputString);
               
               //post-process JSON to work around Jackson ObjectMapper's inability to avoid quoting the "files" element
               if (eventType.equals("submissions")) {
                   int index = eventOutputString.indexOf("\"files\":\"");
                   if (index != -1) {
                       eventOutputString = eventOutputString.replaceFirst("\"files\":\"", "\"files\":");
                       eventOutputString = eventOutputString.replaceFirst("]\"", "]");
                   } else {
                       System.err.println ("\"files\":\"" + " not found in eventOutputString");
                   }
               }
               
//               System.out.println("JSON after post-processing:");
//               System.out.println(eventOutputString);

               if (eventOutputString!=null) {
                //output the JSON event to the output channel
                System.out.println(eventOutputString);
               } else {
                   System.err.println ("Error: converting event to JSON resulted in null");
               }
               
//               String type = (String) eventMap.get("type");
//               if (type.equals("submissions")) {
//                   System.exit(1);
//               }
               
               
           } //if (ef file input line matches event pattern)
        } //while there are more lines in the event feed file
        
        scanner.close();
        
        //reset stdout in case it was redirected to a file
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        
        System.out.println ("Found " + totalEventCount + " events in PC2 Event Feed file '" + pc2ef.getName() + "'");
        System.out.println ("  including " + submissionEventCount + " submission events, " 
                                + judgementEventCount + " judgement events, "
                                + judgementTypeEventCount + " judgement-types events, " 
                                + clarEventCount + " clarification events, and "
                                + otherEventCount + " other events");
        int total = submissionEventCount + judgementEventCount + judgementTypeEventCount + clarEventCount + otherEventCount ;
        System.out.println ("Processed " + total + " events, including: ");
        System.out.println ("  Updated " + updatedSubmissionCount + " submission events");
        System.out.println ("  Verified " + verifiedJudgementCount + " judgement events");
        System.out.println ("  Saved " + processedJudgementTypeCount + " judgement-types events");
        System.out.println ("  Edited " + clarReplacementCount + " clarification events");
        System.out.println ("Found " + errorCount + " errors during processing");
                
    }

    
    /**
     * Compares the data in the specified eventMap (which was created from a "submissions" event line in the pc2 
     * event feed file) with the data in the specified actionMap 
     * (which was created from a RUN_JUDGEMENT line in the PC2 Extract Replay Runs action file).
     *  
     * @param eventMap a map of event feed element names ("type", "id", "op", or "data") to Objects containing the 
     *                  corresponding value
     * @param actionMap a map of PC2 Extract Replay Runs "RUN_JUDGEMENT action element names 
     *                  ("action", "id", "site", "solved", "preliminary", "judgeclient", "judgeclientsite",
     *                   "judged_elapsed_time", "computer_judged", "judgement", or "senttoteams")
     *                   to Strings containing the corresponding value
     *                  
     * @return true if all the data in the actionMap is consistent with that in the eventMap; 
     *          false if one or more data values is inconsistent
     * 
     */
    private static boolean verifyJudgementEventData(Map<String, Object> eventMap, Map<String, String> actionMap) {
        
        //get the data out of the event map
        Map<String,String> eventData = (Map<String,String>) eventMap.get("data");
        
        //verify the ids are valid and that at least one of the IDs in the event matches the action ID
        String eventID = eventData.get("id");
        String eventSubmissionID = eventData.get("submission_id");
        String actionID = actionMap.get("id");
        if ( (eventID==null || eventSubmissionID==null || actionID==null)
             ||  (!eventID.equals(actionID) && (!eventSubmissionID.equals(actionID)) ) ) {
            return false ;
        }
        
        //verify that the "solved" field in the action is consistent with the judgement_type_id in the event
        boolean actionIndicatesSolved = Boolean.parseBoolean(actionMap.get("solved"));
        
//        boolean eventIndicatesSolved  = judgementTypes.get(eventData.get("judgement_type_id")).isSolved();
        //the above was replaced by the below to allow stepping through the debugger
        String judgementTypeID = eventData.get("judgement_type_id");
        CLICSJudgementType judgement = judgementTypes.get(judgementTypeID);
        boolean eventIndicatesSolved = judgement.isSolved();
        
        if (eventIndicatesSolved!=actionIndicatesSolved) {
            return false;
        }
        
        return true;
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
        
        Map<String,Object> data = (Map<String,Object>) eventMap.get("data");
        String fileList = (String) data.get("files");
        
//        System.out.println ("files data from eventMap.data:");
//        System.out.println (fileList);
        
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonString = mapper.writeValueAsString(eventMap);
            
//            System.out.println ("JSON eventMap string from mapper:");
//            System.out.println (jsonString);
            
//            String jsonString2 = mapper.writeValueAsString(((Map<String,Object>)eventMap.get("data")).get("files"));
//            System.out.println("JSON dataMap string from mapper:");
//            System.out.println(jsonString2);
                        
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
        
//      System.out.println ("Before replacement: \n" + jsonString);
        
      //replace "backslash-quote" (\") added by Jackson ObjectMapper with just a double-quote (")
        jsonString = jsonString.replaceAll("\\\\\"", "\"");  
      
      //replace double-backslashes (\\) added by Jackson ObjectMapper with just a single backslash (\)
        jsonString = jsonString.replaceAll("\\\\\\\\", "\\\\");   
//      System.out.println ("After replacement: \n" + jsonString);


        return jsonString ;
    }


    /**
     * Updates the submission data in the specified eventMap (which was created from a "submissions" event 
     * line in the pc2 event feed file) from the data in the specified actionMap 
     * (which was created from a RUN_SUBMIT line in the PC2 Extract Replay Runs action file).
     *  
     * @param eventMap a map of event feed element names ("type", "id", "op", or "data") to Objects containing the 
     *                  corresponding value
     * @param actionMap a map of PC2 Extract Replay Runs RUN_SUBMIT action element names 
     *                  ("action", "id", "elapsed", "language", "problem", "site", "submitclient", or "mainfile") 
     *                  to Strings containing the corresponding value
     *                  
     * @return true if updating the eventMap was successful; false if an error occurred during updating
     */
    private static boolean updateSubmissionEventData(Map<String, Object> eventMap, Map<String,String> actionMap) {

        //get the data out of the event map
//        Map<String,String> eventData = (Map<String,String>) eventMap.get("data");
        Map<String,Object> eventData = (Map<String,Object>) eventMap.get("data");
        
        //verify the event and the action are for the same submission
        if (!eventData.get("id").equals(actionMap.get("id"))) {
            return false ;
        }
        
        //verify the elapsed time in the action map is consistent with the contest time in the event (action map times
        //  are truncated to whole minutes)
        String actionElapsedTime = actionMap.get("elapsed");
        String eventContestTime = (String) eventData.get("contest_time");
        
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
        String eventTeam = (String) eventData.get("team_id");
        
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
            //the file doesn't start with either a "/" or a "driveLetter:\" pattern; let's add a default drive letter
            // TODO:  NOTE THAT THIS MAKES THIS CODE ASSUME DRIVE "c:";   THIS NEEDS TO BE FIXED!!!
            actionFileName = "C:\\" + actionFileName ;   
        }
        
        //force all Windows file path "\" characters to be escaped (convert "\" to "\\")
        actionFileName = escapeBackslashes(actionFileName);
        
        //put the filename from the action into the event
        Object updatedFileRef = "[{\"href\":\"" + actionFileName + "\",\"mime\":\"application/zip\"}]" ;
        
//        System.out.println ("Inserting into eventData: ");
//        System.out.println (updatedFileRef.toString());
        
        eventData.put("files", (Object)updatedFileRef);
        
//        System.out.println ("Retrieved from eventData:");
//        System.out.println (eventData.get("files"));
        
        //TODO: *** compare language ids between Action and Event???
        
        //TODO: *** compare problem ids between Action and Event???
        
        
        return true ;
    }


    /**
     * Converts all occurrences of "\" in the input String to "\\" (escaped backslashes).
     * 
     * @param actionFileName a String possibly containing backslash character
     * 
     * @return a new String with every backslash character replaced by a double-backslash
     */
    private static String escapeBackslashes(String actionFileName) {
        
//        System.err.println ("Original string:");
//        System.err.println (actionFileName);
        
        String retStr = actionFileName.replaceAll("\\\\", "\\\\\\\\");
        
//        System.err.println ("Escaped string:");
//        System.err.println (retStr);
        
        return retStr ;
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
        
//        System.out.println ("JSON being converted to Event Map: ");
//        System.out.println (jsonString);
        
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
        System.out.println("Usage:  java EventFeedReplayRunsMerger pc2eventFeedFile pc2ExtractReplayRunsFile [mergedEventFeedOutputFile]");
        System.out.println ("  where");
        System.out.println ("    pc2eventFeedFile is a JSON event feed from PC2");
        System.out.println ("    pc2ExtractReplayRunsFile is a file containing the output of a PC2 ExtractReplayRuns report");
        System.out.println ("    mergedEventFeedOutputFile is an optional file where the merged event feed will be written (defaults to stdout)");
        
        
    }

}
