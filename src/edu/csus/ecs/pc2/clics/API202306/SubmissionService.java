// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.SubmissionRejectedException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IFileImpl;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;

/**
 * WebService to handle submissions
 *
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/submissions")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class SubmissionService implements Feature {
    // How long to wait for the submission to be entered into the system before returning error
    private long WAIT_SUBMISSION_TIMEOUT_SECS = 20;
    private static final int MAX_RUNFILE_RETRIES = 3;

    private IInternalContest model;

    private IInternalController controller;

    private RunFiles runFiles = null;

    private boolean serverReplied = false;

    private Semaphore submissionWaitSem = null;

    public SubmissionService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        model.addRunListener(new RunListenerImplementation());
    }

    private class PendingSubmissionInfo {
        private ClientId submitterId;
        private ElementId languageId;
        private ElementId problemId;
        private Run submission;

        public PendingSubmissionInfo(ClientId submitterId, ElementId languageId, ElementId problemId) {
            this.submitterId = submitterId;
            this.languageId = languageId;
            this.problemId = problemId;
            submission = null;
        }

        boolean checkMatch(ClientId submitterId, ElementId languageId, ElementId problemId) {
            return(submitterId.equals(this.submitterId) &&
                   languageId.equals(this.languageId) &&
                   problemId.equals(this.problemId));
        }

        Run getSubmission() {
            return(submission);
        }

        void setSubmission(Run sub) {
            submission = sub;
        }
    }
    private PendingSubmissionInfo pendingSub = null;

    /**
     * Run Listener
     *
     * @author pc2@ecs.csus.edu
     */

    public class RunListenerImplementation implements IRunListener {

        @Override
        public void runAdded(RunEvent event) {
            // only care about added runs if we're waiting for one
            if(pendingSub != null && submissionWaitSem != null) {
                synchronized(pendingSub) {
                    // only set it the first time.
                    if(pendingSub.getSubmission() == null) {
                        Run run = event.getRun();
                        if(pendingSub.checkMatch(run.getSubmitter(), run.getLanguageId(), run.getProblemId())) {
                            if(Utilities.isDebugMode()) {
                                System.out.println("SubmissionService: Got back submission with run ID " + run.getNumber());
                            }
                            pendingSub.setSubmission(run);
                            submissionWaitSem.release();
                        }
                    }
                }
            }
        }

        @Override
        public void refreshRuns(RunEvent event) {
            // ignore
        }

        @Override
        public void runChanged(RunEvent event) {
            // server replied, aka our model has been updated :)
            serverReplied = true;
        }

        @Override
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
    public Response getSubmissions(@Context HttpServletRequest servletRequest, @PathParam("contestId") String contestId, @Context SecurityContext sc) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ContestTime ct = model.getContestTime();
        boolean isFrozen = (Utilities.getFreezeTime(model) <= ct.getElapsedSecs() && !model.getContestInformation().isUnfrozen());
        Set<String> exceptProps = new HashSet<String>();

        // get an object which can be used to map the Submission descriptions into JSON form
        ObjectMapper mapper = JSONUtilities.getObjectMapper();

        // set up shortcuts for access policy
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);
        boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
                sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isPublic = sc.isUserInRole(WebServer.WEBAPI_ROLE_PUBLIC);

        // can't see any submission before contest is started (eg. judge's submissions)
        if(!isStaff && ct.isContestStarted() == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String user = sc.getUserPrincipal().getName();
        CLICSSubmission cSub;
        // We have to build the array of json objects by hand due to the exception filter
        StringJoiner allSubs = new StringJoiner(",");

        // Public can never see files or entry_point.  Analysts can't after freeze.
        // Note: teams can see their own files/entry_point/language_id handled below in the loop
        if(isPublic || (isAnalyst && isFrozen)) {
            exceptProps.add("files");
            exceptProps.add("entry_point");
        }

        // get the runs (submissions, really) from the contest
        Run[] runs = model.getRuns();

        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (!submission.isDeleted()) {
                cSub = new CLICSSubmission(model, submission);
                // Teams have restrictions on language, files and entry_point
                if(isTeam) {
                    // Teams can only see their own files/entry_point/language
                    if(!submission.getSubmitter().getName().equals(user)){
                        exceptProps.add("language_id");
                        exceptProps.add("files");
                        exceptProps.add("entry_point");
                    } else {
                        exceptProps.remove("language_id");
                        exceptProps.remove("files");
                        exceptProps.remove("entry_point");
                    }
                }
                try {
                    // for this judgment, create filter to omit unused/bad properties (files and/or entry_point in this case)
                    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                    FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
                    // generate json with only properties we want and add to CSV list.
                    allSubs.add(mapper.writer(fp).writeValueAsString(cSub));
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for submission " + submission.getNumber() + " " + e.getMessage()).build();
                }
            }
        }

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok("[" + allSubs.toString() + "]", MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces("application/zip")
    @Path("{submissionId}/files/")
    public synchronized Response getSubmissionFiles(@Context SecurityContext sc, @PathParam("contestId") String contestId, @PathParam("submissionId") String submissionId) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Look up to see if we have a pc2 account for the client making the request.
        // If account remains null, then the client was in the realms.properties file
        Account account = null;

        ClientId clientId = getClientIdFromUser(sc.getUserPrincipal().getName());
        if(clientId != null) {
            account = model.getAccount(clientId);
        }
        // admins and analysts are authorized to access this endpoint, and analyst is restricted in that
        // they can't see it during freeze period.
        boolean allowed = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
            sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) ||
            (sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST) &&
                Utilities.getFreezeTime(model) > model.getContestTime().getElapsedSecs() &&
                !model.getContestInformation().isUnfrozen());

        // only teams with a real account can fetch their runs
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM) && (account != null);

        // In order to get source code for a run, the user role must be allowed AND if it's
        // a pc2 account, the account has to have permission to fetch the run.
        if ((!allowed && !isTeam) || (account != null && !account.isAllowed(Permission.Type.ALLOWED_TO_FETCH_RUN))) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        // get the submissions from the contest
        Run[] runs = model.getRuns();

        //check each submission to see if it's the one that was requested
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (IJSONTool.getSubmissionId(submission).equals(submissionId)) {

                if(isTeam && !submission.getSubmitter().equals(clientId)) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
                //we found the requested Submission ID in the list of runs returned from the model; try to get the runfiles for Submission
                runFiles = null;
                try {
                    controller.getLog().log(Log.INFO, "Requesting run files for submission " + submission.getNumber() + " from local client model");
                    runFiles = model.getRunFiles(submission);
                } catch (ClassNotFoundException | IOException | FileSecurityException e2) {
                    controller.getLog().log(Log.INFO, "Exception attempting to get run files for submission "
                            + submissionId + " from local model", e2);
                }

                // We try to fetch the runfiles a few times
                for(int nTry = 1; runFiles == null && nTry <= MAX_RUNFILE_RETRIES; nTry++) {
                    // we failed to get the runfiles from the local model
                    // try getting the submission from the server
                    controller.getLog().log(Log.INFO, "No runfiles for submission " + submission.getNumber() + " found locally; requesting Submission from server - Try " + nTry);

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
                    if (serverReplied) {
                        controller.getLog().log(Log.INFO, "Got a reply from the server...");
                    }
                    // see if the runfiles showed up
                    try {
                        runFiles = model.getRunFiles(submission);
                    } catch (ClassNotFoundException | IOException | FileSecurityException e1) {
                        controller.getLog().throwing("SubmissionService", "getSubmissionFiles", e1);
                    }
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
                        ResponseBuilder responseBuilder = Response.ok(file);
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
        controller.getLog().log(Log.INFO, "Unable to find submission " + submissionId + "; returning 'NOT_FOUND'");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{submissionId}/")
    public Response getSubmission(@Context HttpServletRequest servletRequest, @PathParam("contestId") String contestId, @Context SecurityContext sc, @PathParam("submissionId") String submissionId) {
        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ContestTime ct = model.getContestTime();
        boolean isFrozen = (Utilities.getFreezeTime(model) <= ct.getElapsedSecs() && !model.getContestInformation().isUnfrozen());
        Set<String> exceptProps = new HashSet<String>();

        // set up shortcuts for access policy
        boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);
        boolean isAnalyst = sc.isUserInRole(WebServer.WEBAPI_ROLE_ANALYST);
        boolean isStaff = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
                sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        boolean isPublic = sc.isUserInRole(WebServer.WEBAPI_ROLE_PUBLIC);

        // can't see any submission before contest is started (eg. judge's submissions)
        if(!isStaff && ct.isContestStarted() == false) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Public can never see files or entry_point.  Analysts can't after freeze.
        // Note: teams can see their own files/entry_point/language_id handled below in the loop
        if(isPublic || (isAnalyst && isFrozen)) {
            exceptProps.add("files");
            exceptProps.add("entry_point");
        }

        // get the runs (submissions, really) from the contest
        Run[] runs = model.getRuns();

        // Look for submission
        for (int i = 0; i < runs.length; i++) {
            Run submission = runs[i];
            if (!submission.isDeleted() && IJSONTool.getSubmissionId(submission).equals(submissionId)) {
                // Teams have restrictions on language, files and entry_point
                if(isTeam) {
                    // Teams can only see their own files/entry_point/language
                    if(!submission.getSubmitter().getName().equals(sc.getUserPrincipal().getName())){
                        exceptProps.add("language_id");
                        exceptProps.add("files");
                        exceptProps.add("entry_point");
                    } else {
                        exceptProps.remove("language_id");
                        exceptProps.remove("files");
                        exceptProps.remove("entry_point");
                    }
                }
                try {
                    ObjectMapper mapper = JSONUtilities.getObjectMapper();
                    // for this judgment, create filter to omit unused/bad properties (could be files and/or entry_point)
                    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
                    FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
                    String json = mapper.writer(fp).writeValueAsString(new CLICSSubmission(model, submission));
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                } catch (Exception e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for submission " + submission.getNumber() + " " + e.getMessage()).build();
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Post a new submission
     *
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString For non-admin, must not include id, team_id (unless it matches the submitter,
     *      time or contest_time.  For admin, must not include id.
     * @return json for the new submission, including the (new) id
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response addNewSubmission(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            // check for empty request
            if (jsonInputString == null || jsonInputString.length() == 0) {
                // return HTTP 400 response code per CLICS spec
                return Response.status(Status.BAD_REQUEST).entity("empty json").build();
            }

            CLICSSubmission sub = CLICSSubmission.fromJSON(jsonInputString);
            if(sub == null) {
                // return HTTP 400 response code per CLICS spec
                return Response.status(Status.BAD_REQUEST).entity("invalid json supplied").build();
            }

            // These next three are for admin users only
            long overrideTimeMS = -1;
            long overrideSubmissionID = -1;

            Log log = controller.getLog();
            String user = sc.getUserPrincipal().getName();
            ClientId clientId = getClientIdFromUser(user);
            if(clientId == null) {
                log.info("User " + user + " attempted to POST submission from an invalid client");
                // Client must be a valid account, not from realm.properties, we need to
                // check permissions, this is why.
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "No client for user " + user).build();
            }
            Account account = model.getAccount(clientId);
            if(account == null) {
                log.info("User " + user + " attempted to POST submission from a non-existing account");
                // Client must be a valid account, not from realm.properties, we need to
                // check permissions, this is why.
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "No account for user " + user).build();
            }
            ContestTime ct = model.getContestTime();
            boolean isTeam = sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM);
            if(isTeam) {
                // If there's a permission error here, we keep detailed track of what was trying to be done
                // since a team is the one trying to do it.
                boolean bad = false;
                StringBuilder msg = new StringBuilder("User ");
                msg.append(user);
                msg.append(" attempted to POST submission:");
                // Team must be allowed to submit and
                // Team may not provide certain properties (can supply team id if it's the caller)
                if(!account.isAllowed(Permission.Type.SUBMIT_RUN)) {
                    bad = true;
                    msg.append(" no SUBMIT_RUN permission,");
                }
                if(sub.getTeam_id() != null && !sub.getTeam_id().equals("" + clientId.getClientNumber())) {
                    bad = true;
                    msg.append(" to team ");
                    msg.append(sub.getTeam_id());
                    msg.append(",");
                }
                if(sub.getTime() != null) {
                    bad = true;
                    msg.append(" time property illegal,");
                }
                if(sub.getContest_time() != null) {
                    bad = true;
                    msg.append(" contest_time property illegal,");
                }
                if(sub.getId() != null) {
                    bad = true;
                    msg.append(" id property is illegal,");
                }
                if(!ct.isContestRunning()) {
                    bad = true;
                    msg.append(" contest not running, ");
                }
                if(bad) {
                    msg.append(" DENIED.");
                    log.info(msg.toString());
                    return Response.status(Response.Status.FORBIDDEN.getStatusCode(), msg.toString()).build();
                }
                // Force team id for team submission
                sub.setTeam_id("" + clientId.getClientNumber());
            } else if(account.isAllowed(Permission.Type.SHADOW_PROXY_TEAM)) {
                if(sub.getTime() != null || sub.getContest_time() != null || sub.getId() != null) {
                    log.info(user + " attempted to POST submission as PROXY but specified time, contest_time or id");
                    return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "No proxy permission").build();
                }
            } else if((!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) ||
                       (!account.isAllowed(Permission.Type.SUBMIT_RUN) && !account.isAllowed(Permission.Type.SHADOW_PROXY_TEAM))) {
                // non admins can't post anything
                log.info(user + " attempted to POST submission without permission");
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "This account does not have permission to submit.").build();
            } else {
                // ** Departure from CLICS Spec - they say this should be done only by PUT
                // I happen to disagree. --JB
                // For the admin user, copy properties the admin is permitted to set.
                // Also construct the override values to be used.
                overrideTimeMS = Utilities.convertCLICSContestTimeToMS(sub.getContest_time());
                if(overrideTimeMS < 0) {
                    overrideTimeMS = -1;
                }
                overrideSubmissionID = Utilities.stringToLong(sub.getId());
                if(overrideSubmissionID < 0) {
                    overrideSubmissionID = -1;
                }
            }

            // at this point, the team_id must be valid
            String team_id = sub.getTeam_id();
            if(team_id == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no team_id").build();
            }
            ClientId teamClientId = getTeamClientId(team_id);
            if(teamClientId == null){
                return Response.status(Response.Status.BAD_REQUEST).entity("bad team_id" + team_id).build();
            }

            // Check that problem is ok
            Problem prob = getProblemFromId(sub.getProblem_id());
            if(prob == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("bad problem_id").build();
            }
            // Check that language is ok
            Language lang = getLanguageFromId(sub.getLanguage_id());
            if(lang == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("bad language id").build();
            }
            // Make sure we have files
            CLICSFileReference [] files = sub.getFiles();
            if(files.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no file specified").build();
            }

            List<IFile> srcFiles = new ArrayList<IFile>();
            for(CLICSFileReference file : files) {
                String fileName = file.getFilename();
                if("".equals(fileName)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("no file name specified").build();
                }
                String fileData = file.getData();
                if(fileData == null || fileData.length() == 0) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("no file data specified for " + fileName).build();
                }
                IFile iFile = new IFileImpl(file.getFilename(), fileData);
                srcFiles.add(iFile);
            }
            String entry = sub.getEntry_point();
            IFile mainFile = srcFiles.get(0);

            log.info("SubmissionService: Invoking submitRun() for team " + team_id
                + " problem:" + prob.getShortName()
                + " language:" +  lang.getID()
                + " main file:" + mainFile.getFileName()
                + " entry_point:" + entry
                + " overrideTime:" + overrideTimeMS
                + " overrideSubmissionID:" + overrideSubmissionID);

            try {
                // Convert files to serialized files
                SerializedFile mainSubmissionFile = new SerializedFile(mainFile);
                // remainder of list are extra files
                srcFiles.remove(0);
                int nSrcFiles = srcFiles.size();
                SerializedFile[] additionalFiles = new SerializedFile[nSrcFiles];
                for (int i = 0; i < nSrcFiles; i++) {
                    additionalFiles[i] = new SerializedFile(srcFiles.get(i));
                }

                submissionWaitSem = null;
                pendingSub = new PendingSubmissionInfo(teamClientId, lang.getElementId(), prob.getElementId());
                submissionWaitSem = new Semaphore(1);
                // steal the one an only semaphore, a response will give it back immediately
                submissionWaitSem.acquire();

                if(Utilities.isDebugMode()) {
                    System.out.println("SubmissionService: " + user + " submitting Run for " + teamClientId.getName() + " problem:" + sub.getProblem_id() + " language:" + sub.getLanguage_id());
                }

                controller.submitRun(teamClientId, prob, lang, entry, mainSubmissionFile, additionalFiles, overrideTimeMS, overrideSubmissionID);
                boolean gotSub = submissionWaitSem.tryAcquire(WAIT_SUBMISSION_TIMEOUT_SECS, TimeUnit.SECONDS);
                submissionWaitSem = null;
                if(gotSub) {
                    Run newRun = pendingSub.getSubmission();
                    synchronized(pendingSub) {
                        pendingSub = null;
                    }
                    // make sure we got a run; not sure how we couldn't have one at this point
                    if(newRun != null) {
                        // Normal return path.
                        CLICSSubmission newSub = new CLICSSubmission(model, newRun);
                        return Response.ok(newSub.toJSON(), MediaType.APPLICATION_JSON).build();
                    }
                }
                if(Utilities.isDebugMode()) {
                    System.out.println("SubmissionService: User: " + user + " didn't get submission back for " + teamClientId.getName() + " problem:" + sub.getProblem_id() + " language:" + sub.getLanguage_id());
                }
                // No run entered, this is really really bad
                log.log(Level.WARNING, "No Run added after submitting CLICS API run for team " + team_id + " by " + user);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to add submission").build();
            } catch (SubmissionRejectedException sje) {
                log.log(Level.WARNING, "SubmissionRejectedException submitting CLICS API run for team " + team_id + " by " + user);
                return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("Unable to submit run: " + sje.getLocalizedMessage()).build();
            } catch (Exception e) {
                log.log(Level.WARNING, "Exception submitting CLICS API run for team " + team_id + " by " + user, e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to submit run: " + e.getLocalizedMessage()).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).entity(contestId + " not found").build();
    }

    /**
     * Put a new submission
     *
     * @param servletRequest details of request
     * @param sc requesting user's authorization info
     * @param contestId The contest
     * @param jsonInputString For non-admin, must not include id, to_team_id, time or contest_time.  For admin, must not include id.
     * @return json for the submission
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putNewSubmission(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId, String jsonInputString) {

        controller.getLog().log(Log.INFO, "User " + sc.getUserPrincipal().getName() + " tried to PUT a submission");
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    private void createZip(Run submission, java.nio.file.Path tmpDir, HashMap<Integer, String> filesToWrite, String zipFileName) throws FileNotFoundException, IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileName));
        String comment = "files for submission" + submission.getNumber();
        zip.setComment(comment);
        byte[] b = new byte[1024];
        for (Iterator<Integer> iterator = filesToWrite.keySet().iterator(); iterator.hasNext();) {
            Integer fileIndex = iterator.next();
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
     * Returns a ClientId based on the user supplied.  eg. "team99", "administrator1", etc.
     * @param user eg. team99
     * @return The ClientId created, or null if the user is bad
     */
    private ClientId getClientIdFromUser(String user) {
        ClientId clientId = null;

        // basically, need to match lower case letters followed by digits
        Matcher matcher = Pattern.compile("^([a-z]+)([0-9]+)$").matcher(user);
        if(matcher.matches()) {
            try {
                clientId = new ClientId(model.getSiteNumber(), ClientType.Type.valueOf(matcher.group(1).toUpperCase()), Integer.parseInt(matcher.group(2)));
            } catch (Exception e) {
                controller.getLog().log(Log.WARNING, "Can not convert the supplied user " + user + " to a ClientId", e);
            }
        }
        return clientId;
    }

    private ClientId getTeamClientId(String id) {
        ClientId clientId = null;
        try {
            clientId = new ClientId(model.getSiteNumber(), ClientType.Type.TEAM, Integer.parseInt(id));
            if(clientId != null) {
                // an account must exist for this client id or it's invalid.
                if(model.getAccount(clientId) == null) {
                    clientId = null;
                }
            }
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Can not convert the supplied team id " + id + " to a ClientId", e);
        }
        return clientId;
    }

    /**
     * Returns the Problem object for supplied id (short name) or null if none found
     *
     * @param id shortname of problem
     * @return Problem object or null
     */
    private Problem getProblemFromId(String id) {
        for(Problem problem : model.getProblems()) {
            if(problem.getShortName().equals(id)) {
                return(problem);
            }
        }
        return(null);
    }

    /**
     * Returns the the Language object for supplied id or null if none found
     *
     * @param id of the language
     * @return Language object or null
     */
    private Language getLanguageFromId(String id) {
        // get the languages, one-at-a-time from the model
        for(Language language: model.getLanguages()) {
            if (language.isActive() && language.getID().equals(id)) {
                return language;
            }
        }
        return(null);
    }
    /**
     * Check if the supplied user has a team or admin role, if so they can make team submissions
     *
     * @param sc User's security context
     * @return true if the user is allowed to make team submissions
     */
    public static boolean isTeamSubmitAllowed(SecurityContext sc) {
        return(sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) ||
               sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE) ||
               sc.isUserInRole(WebServer.WEBAPI_ROLE_TEAM));
    }

    /**
     * Check if the supplied user is an admin, if so they can make submissions on behalf of a team
     *
     * @param sc User's security context
     * @return true if the user is allowed to make team submissions
     */
    public static boolean isProxySubmitAllowed(SecurityContext sc, IInternalContest model) {
        // Look up to see if we have a pc2 account for the client making the request.
        // If account remains null, then the client was in the realms.properties file
        // and we can only base the value on the role
        Account account = null;

        String user = sc.getUserPrincipal().getName();
        ClientId clientId = null;

        // basically, need to match lower case letters followed by digits
        Matcher matcher = Pattern.compile("^([a-z]+)([0-9]+)$").matcher(user);
        if(matcher.matches()) {
            try {
                clientId = new ClientId(model.getSiteNumber(), ClientType.Type.valueOf(matcher.group(1).toUpperCase()), Integer.parseInt(matcher.group(2)));
            } catch (Exception e) {
                // Don't care - we'll base it on the role in this case (python: pass)
            }
        }
        boolean isAllowed = sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) || sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE);
        if(clientId != null) {
            account = model.getAccount(clientId);
            if(!account.isAllowed(Permission.Type.SUBMIT_RUN) && !account.isAllowed(Permission.Type.SHADOW_PROXY_TEAM))
                isAllowed = false;
        }
        return(isAllowed);
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

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param contest The contest is included in case the inclusion of a property depends on the permissions
     *        set for the connected client.  It is included for uniformity since this method is called as a result
     *        of introspection, and the caller does not know what the callee may need.  Therefore, the contest is
     *        always included, as is the SecurityContext below (for the same reason).
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(IInternalContest contest, SecurityContext sc) {
        return(new CLICSEndpoint("submissions", JSONUtilities.getJsonProperties(CLICSSubmission.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
