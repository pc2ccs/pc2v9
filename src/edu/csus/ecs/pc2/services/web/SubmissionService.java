package edu.csus.ecs.pc2.services.web;

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
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/submissions")
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
     * This method returns a representation of the current contest groups in JSON format. The returned value is a JSON array with one language description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubmissions() {

        // get the groups from the contest
        Run[] runs = model.getRuns();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            childNode.add(jsonTool.convertToJSON(submission));
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

        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (submission.getElementId().toString().equals(submissionId)) {
                try {
                    runFiles = model.getRunFiles(submission);
                } catch (ClassNotFoundException | IOException | FileSecurityException e2) {
                    controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e2);
                }
                if (runFiles == null) {
                    // we found the run in the list; try getting it from the server
                    controller.getLog().log(Log.INFO, "Requesting run " + submission.getNumber() + " from server");

                    // the following might be better -- but it requires adding "Fetch_Run" permission to the Feeder account (or changing the account defaults).
                    try {
                        controller.fetchRun(submission);
                    } catch (ClassNotFoundException | IOException | FileSecurityException e1) {
                        controller.getLog().log(Log.INFO, "Requesting run " + submission.getNumber() + " from server");
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
                        java.nio.file.Path tmp_dir = null;
                        try {
                            tmp_dir = Files.createTempDirectory("subService");
                            // dump mainFile and otherFiles to tmp_dir
                            HashMap<Integer, String> filesToWrite = new HashMap<Integer, String>();
                            if (mainFile != null) {
                                filesToWrite.put(Integer.valueOf(0), mainFile.getName());
                                mainFile.buffer2file(mainFile.getBuffer(), tmp_dir.toAbsolutePath().toString() + File.pathSeparator + mainFile.getName());
                            }
                            if (otherFiles != null) {
                                for (int j = 0; j < otherFiles.length; j++) {
                                    SerializedFile serializedFile = otherFiles[j];
                                    filesToWrite.put(Integer.valueOf(j + 1), serializedFile.getName());
                                    serializedFile.buffer2file(serializedFile.getBuffer(), tmp_dir.toAbsolutePath().toString() + File.pathSeparator + serializedFile.getName());
                                }
                            }
                            String zipFileName = tmp_dir.toAbsolutePath().toString() + File.pathSeparator + "files.zip";
                            createZip(submission, tmp_dir, filesToWrite, zipFileName);
                            // set file (and path) to be download
                            File file = new File(zipFileName);
                            ResponseBuilder responseBuilder = Response.ok((Object) file);
                            responseBuilder.header("Content-Disposition", "attachment; filename=\"files.zip\"");
                            return responseBuilder.build();
                        } catch (IOException e) {
                            controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e);
                        } finally {
                            if (tmp_dir != null) {
                                deleteDir(tmp_dir);
                            }
                        }
                    } else {
                        controller.getLog().log(Log.INFO, "Returned runFiles was null; returning 'NOT_FOUND'");
                        return Response.status(Status.NOT_FOUND).build();
                    }
                }
            }
        }
        return Response.noContent().build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{submissionId}/")
    public Response getSubmission(@PathParam("submissionId") String submissionId) {
        // get the submissions from the contest
        Run[] runs = model.getRuns();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (submission.getElementId().toString().equals(submissionId)) {
                childNode.add(jsonTool.convertToJSON(submission));
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();

    }

    private void createZip(Run submission, java.nio.file.Path tmp_dir, HashMap<Integer, String> filesToWrite, String zipFileName) throws FileNotFoundException, IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileName));
        String comment = "files for submission" + submission.getNumber();
        zip.setComment(comment);
        byte[] b = new byte[1024];
        for (Iterator<Integer> iterator = filesToWrite.keySet().iterator(); iterator.hasNext();) {
            Integer fileIndex = (Integer) iterator.next();
            String inputFile = filesToWrite.get(fileIndex);
            FileInputStream in = new FileInputStream(tmp_dir + File.pathSeparator + inputFile);
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

    private void deleteDir(java.nio.file.Path tmp_dir) {
        try {
            Files.walkFileTree(tmp_dir, new SimpleFileVisitor<java.nio.file.Path>() {
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

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
