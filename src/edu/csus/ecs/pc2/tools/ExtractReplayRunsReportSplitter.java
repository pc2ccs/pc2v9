// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ExtractReplayRunsReportSplitter {

    private int division;
    private Map<String,String> savedSubmissionActions = new HashMap<String,String>();
    
    /**
     * Constructs an ExtractReplayRunsReportSplitter that removes from the specified pc2ReplayReport file
     * any ExtractReplayRuns action lines which are not associated with the specified "division", assumed
     * to be either "div1" or "div2".  Writes action lines which ARE associated with the specified division
     * to the specified output file, or to stdout if "outputFileName" is null.
     * 
     * @param pc2ReplayReport a PC2 ExtractReplayRuns Report file
     * @param division a String identifying the desired division, either "div1" or "div2"
     * @param outputFileName the name of the output file (stdout if null)
     */
    public ExtractReplayRunsReportSplitter(File pc2ReplayReport, String division, String outputFileName) {
        
        System.out.println ("Running ExtractReplayRunsReportSplitter...");
        
        //redirect output if a file name was given
        if (outputFileName!=null) {
            try {
                boolean append = true;  boolean autoFlush = true;
                PrintStream out = new PrintStream(new FileOutputStream(outputFileName, append), autoFlush);
                System.setOut(out);                
            } catch (FileNotFoundException e) {
                System.err.println("Error opening specified result file for output");
                e.printStackTrace();
            }
        }
        
        //fetch the division number of interest.  "division" has already been verified to be either "div1" or "div2"
        switch (division) {
            case "div1":
                this.division = 1;
                break;
            case "div2":
                this.division = 2;
                break;
        }
            
        BufferedReader br=null;
        try {
            br = new BufferedReader (new FileReader(pc2ReplayReport));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println ("Exiting...");
        }
        
        try {
            String actionString = br.readLine();
            
            while (actionString!=null) {
                
                //skip lines which are not actions (e.g. blank lines or report header/trailer comments
                if (actionString.startsWith("action")) {
                    
                    //get a map containing the Action fields
                    Map<String,String> actionMap = getActionMap(actionString);
                    
                    String actionType = actionMap.get("action");
                    
                    if (actionType==null) {
                        System.err.println("Warning: found null action type in action string: " + actionString);
                    } else {
                        
                        //process the action based on its type
                        boolean keep = true;
                        switch (actionType) {
                            
                            case "RUN_SUBMIT":
                                
                                keep = checkRunSubmitAction(actionString);
                                break;
                                
                            case "RUN_JUDGEMENT":
                                
                                keep = checkRunJudgementAction(actionString);
                                break;
                                
                            default:
                                System.err.println ("Warning: found unknown action type: " + actionType);
                        }
                        
                        if (keep) {
                            System.out.println(actionString);
                        }
                    }
                    
                    
                }
                
                actionString = br.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    
    /**
     * Returns true if the specified action string should remain part of the
     * generated ExtractReplayRunsReport (i.e., survives the splitting).
     * Surviving the split is defined as having a team number whose lower two digits are in the range 
     * corresponding to the "division" filter:  01-49 for div1, 50-99 for div2.
     * 
     * @param actionString a String defining a RUN_SUBMIT action from an ExtractReplayRuns Report
     * @return true if the action string should remain part of the output
     */
    private boolean checkRunSubmitAction(String actionString) {
        
        Map<String,String> actionMap = getActionMap(actionString);
        
        //check if the team is in the division of interest
        String teamNumString = actionMap.get("submitclient").substring(4);
        
        Integer teamNum = new Integer(teamNumString);
        teamNum = teamNum % 100 ;  //extract just the lowest two digits
        
        if ( (teamNum<50 && division==2) || (teamNum>=50 && division==1) ) {
            //this is not a submission for the division we're interested in
            return false ;
        } 
        
        //this is a submission for the division of interest; see if we've already seen such a submission
        String submissionID = actionMap.get("id");
        if (savedSubmissionActions.keySet().contains(submissionID)) {
            //we've already seen a submission with this number -- that should never happen!
            System.err.println ("Error:  encountered duplicate submission with ID: " + submissionID);
            return false;
        }
        
        //new submission; save it
        savedSubmissionActions.put(submissionID, actionString) ;
 
        return true;
    }
    
    /**
     * Returns true if the specified action string should remain part of the
     * generated ExtractReplayRunsReport (i.e., survives the splitting).
     * Surviving the split is defined as being a judgement for a submission which has previously
     * been marked as surviving the split; i.e. which has been saved in the global map of saved submission actions.
     * 
     * @param actionString a String defining a RUN_JUDGEMENT action from an ExtractReplayRuns Report
     * @return true if the action string should remain part of the output
     */
    private boolean checkRunJudgementAction(String actionString) {
        
        Map<String,String> actionMap = getActionMap(actionString);
        
        String submissionID = actionMap.get("id");
        
        if (savedSubmissionActions.keySet().contains(submissionID)) {
            return true;
        } else {
            return false;            
        }

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
    private Map<String, String> getActionMap(String action) {
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
     * Run the ExtractReplayRunsReportSplitter tool.  Reads the specified input file, assumed to be a "report"
     * generated by the ExtracReplayRunsReport tool, and "splits" the report based on teams being in one of two
     * "divisions" (such as at the PacNW contest).  Splitting is based on team number:  teams whose lower two digits
     * are in the range 01..49 are assumed to be in Division 1; teams with lower two digits in range 50-99 are assumed
     * to be in Division 2.  (This is consistent with current PacNW team number assignments, where teams at (e.g.)
     * Site 1 are assigned numbers 101-149 for Division 1, 150-199 for Division 2; teams at Site 2 are assigned team
     * numbers 201-249 for Division 1, 250-299 for Division 2, etc.)
     * 
     * Produces a new ExtractReplayRuns report, either on stdout or to
     * an optionally specified file, containing only those Replay Action lines corresponding to a specified division
     * ("div1" or "div2").  
     * 
     * @param args 
     *          [0]: the path to a file containing a PC2 Extract Replay Runs report
     *          [1]: either "div1" or "div2", the division whose actions should be retained in the output 
     *          [2]: optional path to a newly-created ExtractReplayRuns Action File (defaults to stdout).  
     *          
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        if (args.length<2) {
            usage();
            System.exit(1);
        }
        
        File pc2ReplayReport = new File(args[0]);
        
        if (!pc2ReplayReport.exists()) {
            System.out.println ("Cannot find required ExtractReplayRunsReport input file");
            usage();
            System.exit(2);
        }
        
        String division = args[1];
        if (division==null || (!division.equalsIgnoreCase("div1") && !division.equalsIgnoreCase("div2"))) {
            System.out.println ("Invalid second argument: " + division + "  (must be either \"div1\" or \"div2\"");
            usage();
            System.exit(3);
        }
        
        String outputFileName = null;
        if (args.length>2) {
            outputFileName = args[2];
        }
        
        new ExtractReplayRunsReportSplitter(pc2ReplayReport, division, outputFileName);
        
    }

        
        /**
         * Prints the expected command-line usage of the class.
         */
        private static void usage() {
            System.out.println("Usage:  java ExtractReplayRunsReportSplitter pc2ExtractReplayRunsFile {div1 | div2} [splitOutputFile]");
            System.out.println ("  where");
            System.out.println ("    pc2ExtractReplayRunsFile is a file containing the output of a PC2 ExtractReplayRuns report");
            System.out.println ("    {div1 | div2} is either the string \"div1\" or \"div2\"");
            System.out.println ("    splitOutputFile is an optional file where the actions for the specified division will be written (defaults to stdout)");   
        }

}
