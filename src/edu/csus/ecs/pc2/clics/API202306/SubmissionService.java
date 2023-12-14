// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;

/**
 * WebService to handle submissions
 * 
 * @author ICPC
 *
 */
@Path("/contest/submissions")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class SubmissionService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    private RunFiles runFiles = null;

    private boolean serverReplied = false;

    private JSONTool jsonTool;

    public SubmissionService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        model.addRunListener(new RunListenerImplementation());
        jsonTool = new JSONTool(model, controller);
    }

    /**
     * Run Listener
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
            // server replied, aka our model has been updated :)
            serverReplied = true;
        }

        public void runRemoved(RunEvent event) {
            // ignore
        }
    }

    /**
     * This method returns a JSON representation of all Runs (Submissions). 
     * 
     * @return a {@link Response} object containing the Submissions in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubmissions(@Context HttpServletRequest servletRequest, @Context SecurityContext sc) {

        // get the groups from the contest
        Run[] runs = model.getRuns();

        // get an object which can be used to map the Submission descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        //put each Submission (a.k.a. "Run") into the JSON array
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (!submission.isDeleted()) {
                childNode.add(jsonTool.convertToJSON(submission, servletRequest, sc));
            }
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces("application/zip")
    @Path("{submissionId}/files/")
    public Response getSubmissionFiles(@Context SecurityContext sc, @PathParam("submissionId") String submissionId) {
        // only admin and analyst are authorized to access this endpoint
        if (!(sc.isUserInRole("admin") || sc.isUserInRole("analyst"))) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        // get the submissions from the contest
        Run[] runs = model.getRuns();

        //check each submission to see if it's the one that was requested
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (jsonTool.getSubmissionId(submission).equals(submissionId)) {
                
                //we found the requested Submission ID in the list of runs returned from the model; try to get the runfiles for Submission
                runFiles = null;
                try {
                    controller.getLog().log(Log.INFO, "Requesting run files for submission " + submission.getNumber() + " from local client model");
                    runFiles = model.getRunFiles(submission);
                } catch (ClassNotFoundException | IOException | FileSecurityException e2) {
                    controller.getLog().log(Log.INFO, "Exception attempting to get run files for submission " 
                            + submissionId + " from local model", e2);
                }
                
                //if runFiles is still null we failed to get the runfiles from the local model
                if (runFiles == null) {
                    // try getting the submission from the server
                    controller.getLog().log(Log.INFO, "No runfiles for submission " + submission.getNumber() + " found locally; requesting Submission from server");

                    try {
                        controller.fetchRun(submission);  //note: requires having "Fetch_Run" permission for the Feeder account...
                    } catch (ClassNotFoundException | IOException | FileSecurityException e1) {
                        controller.getLog().log(Log.INFO, "Exception requesting submission (run)  " + submission.getNumber() + " from server: " + e1);
                    }

                    int waitedMS = 0;
                    serverReplied = false;

                    // wait for callback to run listener -- but only for up to 30 sec
                    while (!serverReplied && waitedMS < 30000) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e);
                        }
                        waitedMS += 100;
                    }
                } else {
                    // if we have the runFiles already, bypass fetching the run and waiting on the server
                    serverReplied = true;
                }
                if (serverReplied) {

                    controller.getLog().log(Log.INFO, "Got a reply from the server...");
                    try {
                        runFiles = model.getRunFiles(submission);
                    } catch (ClassNotFoundException | IOException | FileSecurityException e1) {
                        controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e1);
                    }
                    if (runFiles != null) {
                        controller.getLog().log(Log.INFO, "Returning runFiles: " + runFiles.toString());

                        SerializedFile mainFile = runFiles.getMainFile();
                        SerializedFile[] otherFiles = runFiles.getOtherFiles();
                        java.nio.file.Path tmpDir = null;
                        try {
                            tmpDir = Files.createTempDirectory("subService");
                            // dump mainFile and otherFiles to tmpDir
                            HashMap<Integer, String> filesToWrite = new HashMap<Integer, String>();
                            if (mainFile != null) {
                                filesToWrite.put(Integer.valueOf(0), mainFile.getName());
                                mainFile.buffer2file(mainFile.getBuffer(), tmpDir.toAbsolutePath().toString() + File.pathSeparator + mainFile.getName());
                            }
                            if (otherFiles != null) {
                                for (int j = 0; j < otherFiles.length; j++) {
                                    SerializedFile serializedFile = otherFiles[j];
                                    filesToWrite.put(Integer.valueOf(j + 1), serializedFile.getName());
                                    serializedFile.buffer2file(serializedFile.getBuffer(), tmpDir.toAbsolutePath().toString() + File.pathSeparator + serializedFile.getName());
                                }
                            }
                            String zipFileName = tmpDir.toAbsolutePath().toString() + File.pathSeparator + "files.zip";
                            createZip(submission, tmpDir, filesToWrite, zipFileName);
                            // set file (and path) to be download
                            File file = new File(zipFileName);
                            ResponseBuilder responseBuilder = Response.ok((Object) file);
                            responseBuilder.header("Content-Disposition", "attachment; filename=\"files.zip\"");
                            return responseBuilder.build();
                        } catch (IOException e) {
                            controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e);
                        } finally {
                            if (tmpDir != null) {
                                deleteDir(tmpDir);
                            }
                        }
                    } else {
                        controller.getLog().log(Log.INFO, "Returned runFiles was null; returning 'NOT_FOUND'");
                        return Response.status(Status.NOT_FOUND).build();
                    }
                }
            }
        }
        controller.getLog().log(Log.INFO, "Unable to find submission " + submissionId + "; returning 'NOT_FOUND'");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{submissionId}/")
    public Response getSubmission(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("submissionId") String submissionId) {
        // get the submissions from the contest
        Run[] runs = model.getRuns();

        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (!submission.isDeleted() && jsonTool.getSubmissionId(submission).equals(submissionId)) {
                return Response.ok(jsonTool.convertToJSON(submission, servletRequest, sc).toString(), MediaType.APPLICATION_JSON).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private void createZip(Run submission, java.nio.file.Path tmpDir, HashMap<Integer, String> filesToWrite, String zipFileName) throws FileNotFoundException, IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileName));
        String comment = "files for submission" + submission.getNumber();
        zip.setComment(comment);
        byte[] b = new byte[1024];
        for (Iterator<Integer> iterator = filesToWrite.keySet().iterator(); iterator.hasNext();) {
            Integer fileIndex = (Integer) iterator.next();
            String inputFile = filesToWrite.get(fileIndex);
            FileInputStream in = new FileInputStream(tmpDir + File.pathSeparator + inputFile);
            ZipEntry ze = new ZipEntry(inputFile);
            zip.putNextEntry(ze);
            while (in.available() > 0) {
                int count = in.available();
                if (count >= 1024) {
                    count = 1024;
                    in.read(b);
                } else {
                    in.read(b, 0, count);
                }
                zip.write(b, 0, count);
            }
            in.close();
            zip.closeEntry();
        }
        zip.close();
    }

    private void deleteDir(java.nio.file.Path tmpDir) {
        try {
            Files.walkFileTree(tmpDir, new SimpleFileVisitor<java.nio.file.Path>() {
                @Override
                public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException e) throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw e;
                    }
                }
            });
        } catch (IOException e) {
            controller.getLog().throwing("SubmissionService", "deleteDir", e);
        }
    }

    /**
     * Check if the supplied user has a team or admin role, if so they can make team submissions
     * 
     * @param sc User's security context
     * @return true if the user is allowed to make team submissions
     */
    public static boolean isTeamSubmitAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM));
    }

    /**
     * Check if the supplied user has a admin, if so they can make submissions on behalf of a team
     * 
     * @param sc User's security context
     * @return true if the user is allowed to make team submissions
     */
    public static boolean isProxySubmitAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }

    /**
     * Check if the supplied user has the admin role, if so they can make admin submissions
     * 
     * @param sc User's security context
     * @return true if the user is allowed to make team submissions
     */
    public static boolean isAdminSubmitAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN));
    }
    
    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
