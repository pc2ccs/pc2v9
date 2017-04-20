package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
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
    public Response fetchRun(@PathParam("id") String runID) {
        
        serverReplied = false;
        
        //create a dummy hardcoded run for testing
//        String fakeRun = "[{\"runID\":" + runID + ",\"filename\":\"a.java\",\"content\":\"<base64_string>\"},{\"filename\":\"helper.java\",\"content\":\"<base64_string>\"}]" ;
        
        run = null;
        try {
            Run [] runs = contest.getRuns();
            for (int i=0; i<runs.length; i++) {
                if (runs[i].getNumber() == Integer.parseInt(runID)) {
                    run = runs[i];
                    break;
                }
            }
            if (run == null) {
                //not found
                return Response.noContent().build();
            } else {
                controller.checkOutRun(run, true, false);  //checkoutRun(run, isReadOnlyRequest, isComputerJudgedRequest)
                
                int waitedMS = 0;
                serverReplied = false;
                
                //wait for callback to run listener -- but only for up to 10 sec
                while (!serverReplied && waitedMS++ < 10000) {
                    Thread.sleep(100);
                }
                
                if (serverReplied) {
                    
                    if (run != null && runFiles != null) {
                        return Response.ok(runFiles, MediaType.APPLICATION_JSON).build();
                    } else {
                        return Response.status(Status.NO_CONTENT).build();
                    }
                    
                } else {
                    controller.getLog().log(Log.INFO, "Unable to fetch run " + runID + " from server: not found");
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
            
            if (run != null) {
                if (event.getRun().getElementId().equals(run.getElementId())) {
                    
                    // RUN_NOT_AVAILABLE is undirected (sentToClient is null)
                    if (event.getAction().equals(Action.RUN_NOT_AVAILABLE)) {
                        
                        runStatus = Action.RUN_NOT_AVAILABLE;
                        controller.getLog().log(Log.INFO, "Requested run not available");
                        
                    } else {
                        
                        if (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId())) {
                            
                            run = event.getRun();
                            runFiles = event.getRunFiles();                          
                        }
                    }
                }
            }
            
            serverReplied = true;
        }

        public void runRemoved(RunEvent event) {
            // ignore
        }
    }



}
