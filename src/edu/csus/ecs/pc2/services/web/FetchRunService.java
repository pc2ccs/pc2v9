package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.model.RunFiles;


/**
 * WebService for handling fetch run requests.
 * 
 * @author john
 *
 */
@Path("/submission/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class FetchRunService {

    private IInternalContest contest;
    private IInternalController controller;
    
    private Run run;
    private RunFiles runFiles;
    boolean serverReplied;
    Action runStatus ;

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
    public Response fetchRun(@PathParam("id") String runID, @Context SecurityContext sc) {
        
        if (!sc.isUserInRole("admin")) {
            // do not return runs if the requestor is not an admin  (this might need to be relaxed later, e.g. by including wider credential...)
            return Response.status(Status.FORBIDDEN).build();
        }

        //create a dummy hardcoded run for testing
//        String fakeRun = "[{\"runID\":" + runID + ",\"filename\":\"a.java\",\"content\":\"<base64_string>\"},{\"filename\":\"helper.java\",\"content\":\"<base64_string>\"}]" ;
        
        run = null;
        try {
            //search the local list of runs for the requested run
            Run [] runs = contest.getRuns();
            for (int i=0; i<runs.length; i++) {
                if (runs[i].getNumber() == Integer.parseInt(runID)) {
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
                
                int waitedMS = 0;
                serverReplied = false;
                
                //wait for callback to run listener -- but only for up to 10 sec
                while (!serverReplied && waitedMS < 10000) {
                    Thread.sleep(100);
                    waitedMS += 100;
                }
                
                if (serverReplied) {
                    
                    controller.getLog().log(Log.INFO, "Got a reply from the server...");

                    if (run != null && runFiles != null) {
                        controller.getLog().log(Log.INFO, "Returning runFiles: " + runFiles.toString());
                        
                        //map the runFiles object into JSON
                        ObjectMapper objectMapper = new ObjectMapper();
                        String runFileJSON = objectMapper.writeValueAsString(runFiles);
                        System.err.println(runFileJSON);
                        
                        //return the JSON version of runFiles
                        return Response.ok(runFileJSON, MediaType.APPLICATION_JSON).build();
                        
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
                        
                        runStatus = Action.RUN_NOT_AVAILABLE;
                        controller.getLog().log(Log.INFO, "Reply from server: requested run not available");
                        
                    } else {
                        
                        //make sure this RunEvent was meant for me
                        if (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId())) {
                            
                            controller.getLog().log(Log.INFO, "Reply from server: " + "Run Status=" + event.getAction()
                                                    + "; run=" + event.getRun() + ";  runFiles=" + event.getRunFiles());
                            
                            aRun = event.getRun();
                            aRunFiles = event.getRunFiles();                          
                        } else {
                            ClientId toClient = event.getSentToClientId() ;
                            ClientId myID = contest.getClientId();

                            controller.getLog().log(Log.INFO, "Event not for me: sent to " + toClient + " but my ID is " + myID);

                            //this needs to be reconsidered; why are we continuing when the event wasn't sent to us?  (Why wasn't it sent to us?)
                            run = event.getRun();
                            runFiles = event.getRunFiles();
                        }
                    }
                    
            } else {
                //run from server was null
                controller.getLog().log(Log.INFO, "Run received from server was null");
            }
            
            
            serverReplied = true;
        }

        public void runRemoved(RunEvent event) {
            // ignore
        }
    }



}
