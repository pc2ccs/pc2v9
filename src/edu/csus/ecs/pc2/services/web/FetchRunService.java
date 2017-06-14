package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.exports.ccs.RunFilesJSON;


/**
 * WebService for handling fetch run requests.
 * 
 * @author john
 *
 */
@Path("/submission_files")
@Produces(MediaType.APPLICATION_JSON)
public class FetchRunService {

    private IInternalContest contest;
    private IInternalController controller;
    
    private Run run;
    private RunFiles runFiles;
    private boolean serverReplied;
    
    public FetchRunService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
        
        contest.addRunListener(new RunListenerImplementation());

    }

    /**
     * This method returns a JSON representation of the submitted run identified by {id}. 
     * 
     * @return a {@link Response} object containing the submitted run in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchRun(@QueryParam("id") String runID, @Context SecurityContext sc) {
        
        if (!sc.isUserInRole("admin")) {
            // do not return runs if the requestor is not an admin  (this might need to be relaxed later, e.g. by including wider credential...)
            return Response.status(Status.FORBIDDEN).build();
        }

        //make sure we got a valid integer runID
        int intRunID ;
        if (runID == null) {
            return Response.status(Status.BAD_REQUEST).entity("Missing runID parameter").build();
        } else {
            try {
                intRunID = Integer.parseInt(runID);
                if (intRunID <= 0) {
                    return Response.status(Status.BAD_REQUEST).entity("Invalid runID (must be positive)").build();
                }
            } catch (NumberFormatException e) {
                return Response.status(Status.BAD_REQUEST).entity("Malformed runID").build();
            }
        }
        
        //if we get here we know the user has provided a valid runID greater than zero
        run = null;
        try {
            //search the local list of runs for the requested run
            Run [] runs = contest.getRuns();
            for (int i=0; i<runs.length; i++) {
                if (runs[i].getNumber() == intRunID) {
                    run = runs[i];
                    break;
                }
            }
            //check if we found the requested run in the list or not
            if (run == null) {
                
                //not found
                controller.getLog().log(Log.INFO, "Unable to fetch run " + runID + " from server: not found");
                return Response.status(Status.NOT_FOUND).build();
                
            } else {
                
                //we found the run in the list; try getting it from the server
                controller.getLog().log(Log.INFO, "Requesting run " + runID + " from server");
                
                controller.checkOutRun(run, true, false);  //checkoutRun(run, isReadOnlyRequest, isComputerJudgedRequest)
                
                //the following might be better -- but it requires adding "Fetch_Run" permission to the Feeder account (or changing the account defaults).
//                controller.fetchRun(run);
               
                int waitedMS = 0;
                serverReplied = false;
                
                //wait for callback to run listener -- but only for up to 30 sec
                while (!serverReplied && waitedMS < 30000) {
                    Thread.sleep(100);
                    waitedMS += 100;
                }
                
                if (serverReplied) {
                    
                    controller.getLog().log(Log.INFO, "Got a reply from the server...");

                    if (run != null && runFiles != null) {
                        controller.getLog().log(Log.INFO, "Returning runFiles: " + runFiles.toString());
                        
                        //map the runFiles object into JSON 
                        // (the problem with this approach is that it produces JSON for the ENTIRE CLASS, which is not what the CLICS spec requires...)
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        String runFileJSON = objectMapper.writeValueAsString(runFiles);
                        
                        //use the internal PC2 class to generate a CLICS-specific JSON representation of the RunFiles object
                        RunFilesJSON jsonGenerator = new RunFilesJSON();
                        String json = jsonGenerator.createJSON(runFiles);

//                        System.err.println(json);
                        
                        //return the JSON version of runFiles (the submission files)
                        return Response.ok(json, MediaType.APPLICATION_JSON).build();
                        
                    } else {
                        
                        controller.getLog().log(Log.INFO, "Returned run or runFiles was null; returning 'NOT_FOUND'");
                        return Response.status(Status.NOT_FOUND).build();
                    }
                    
                } else {
                    controller.getLog().log(Log.INFO, "No response from server after " + waitedMS + "ms");
                    controller.getLog().log(Log.INFO, "Unable to fetch run " + runID + " from server: not found");
                    controller.getLog().log(Log.INFO, "Returning 'NOT_FOUND'");
                    return Response.status(Status.NOT_FOUND).build();
                }
            }

        } catch (Exception e) {
            //log exception
            controller.getLog().log(Log.WARNING, "FetchRunService: problem fetching run from server ", e);
            e.printStackTrace();

            // return HTTP error response code
            return Response.serverError().entity(e.getMessage()).build();
        }

    }
    
    /**
     * Run Listener for FetchRun Service.
     * 
     * @author pc2@ecs.csus.edu
     */

    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // ignore
        }
        
        public void refreshRuns(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            
            Action action = event.getAction();
            Action details = event.getDetailedAction();
            Run aRun = event.getRun();
            RunFiles aRunFiles = event.getRunFiles();
            String msg = event.getMessage();
            
            controller.getLog().log(Log.INFO, "RunListener: Action=" + action + "; DetailedAction=" + details + "; msg=" + msg
                                    + "; run=" + aRun + "; runFiles=" + aRunFiles);

            
            if (event.getRun() != null) {
                    
                    // RUN_NOT_AVAILABLE is undirected (sentToClient is null)
                    if (event.getAction().equals(Action.RUN_NOT_AVAILABLE)) {
                        
                        controller.getLog().log(Log.INFO, "Reply from server: requested run not available");
                        
                    } else {
                        
                        //make sure this RunEvent was meant for me
                        if (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId())) {
                            
                            controller.getLog().log(Log.INFO, "Reply from server: " + "Run Status=" + event.getAction()
                                                    + "; run=" + event.getRun() + ";  runFiles=" + event.getRunFiles());
                            
                            run = event.getRun();
                            runFiles = event.getRunFiles();    
                            
                        } else {
                            
                            ClientId toClient = event.getSentToClientId() ;
                            ClientId myID = contest.getClientId();

                            controller.getLog().log(Log.INFO, "Event not for me: sent to " + toClient + " but my ID is " + myID);

                            //TODO:  this needs to be reconsidered; why are we continuing when the event wasn't sent to us?  (Why wasn't it sent to us?)
                            run = event.getRun();
                            runFiles = event.getRunFiles();
                        }
                    }
                    
            } else {
                //run from server was null
                controller.getLog().log(Log.INFO, "Run received from server was null");
                run = null;
                runFiles = null;
            }
            
            
            serverReplied = true;
        }

        public void runRemoved(RunEvent event) {
            // ignore
        }
    }



}
