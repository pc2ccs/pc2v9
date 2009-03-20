package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunJudgement;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Implementation for IRun.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunImplementation implements IRun {

    private boolean solved;

    private boolean deleted;

    private String judgementTitle = "";

    private ITeam submitterTeam;

    private IProblem problem;

    private ILanguage language;

    private int number;

    private int siteNumber;

    private long elapsedMins;

    private ElementId elementId;

    private RunFiles runFiles = null;

    private Boolean listening = new Boolean(true);

    private IInternalController controller = null;

    private FetchRunListenerImplemenation fetchRunListenerImplemenation = null;

    private IInternalContest internalContest = null;

    /**
     * Have we got the RunFiles from the server.
     * 
     */
    private boolean answerReceived = false;

    private Run run = null;

    private boolean preliminaryJudged = false;

    private boolean finalJudged = false;
    
    private PermissionList permissionList = new PermissionList();
    
    /**
     * Respect Send to Team Permission.
     * 
     * true means if {@link JudgementRecord#isSendToTeam()} is true then process run as a NEW run.
     * <br>
     * false means process all records per usual. 
     */
    private boolean respectSendToTeam = false;
    
    public RunImplementation(Run inRun, IInternalContest internalContest, IInternalController controller) {

        this.controller = controller;
        this.internalContest = internalContest;
        
        respectSendToTeam = isAllowed (internalContest, internalContest.getClientId(), Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        
        this.run = inRun;
              
        if ( respectSendToTeam && inRun.getAllJudgementRecords().length > 0 ){
            /**
             * There are judgements and we need to check the send to team (notify team) flag
             */
            if (! inRun.getJudgementRecord().isSendToTeam()){
                /**
                 * If not send to team, then change run to a new run with status NEW
                 */
                this.run = RunUtilities.createNewRun(inRun, internalContest);
            }
        }
        
        deleted = run.isDeleted();

        submitterTeam = new TeamImplementation(run.getSubmitter(), internalContest);

        problem = new ProblemImplementation(run.getProblemId(), internalContest);

        language = new LanguageImplementation(run.getLanguageId(), internalContest);

        number = run.getNumber();

        siteNumber = run.getSiteNumber();

        elapsedMins = run.getElapsedMins();

        elementId = run.getElementId();
        
        IRunJudgement [] judgements = getRunJudgements();
        
        if (judgements.length > 0) {
            IRunJudgement lastRunJudgement = judgements[judgements.length - 1];
            solved = lastRunJudgement.isSolved();
            preliminaryJudged = lastRunJudgement.isPreliminaryJudgement();
            finalJudged = !preliminaryJudged;
            judgementTitle = getJudgementTitle(run);
        }
    }
    
    private void initializePermissions(IInternalContest theContest, ClientId clientId) {
        Account account = theContest.getAccount(clientId);
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        } else {
            // Set default conditions
            permissionList.clearAndLoadPermissions(new PermissionGroup().getPermissionList(clientId.getClientType()));
        }
    }

    /**
     * Is Client allowed to do permission type
     * 
     * @param theContest
     * @param clientId
     * @param respect_notify_team_setting
     * @return true if permission/type.
     */
    private boolean isAllowed(IInternalContest theContest, ClientId clientId, Type type) {
        initializePermissions(theContest, clientId);
        return permissionList.isAllowed(type);
    }
    
    private String getJudgementTitle(Run run2) {

        JudgementRecord judgementRecord = run2.getJudgementRecord();
        if (judgementRecord != null) {
            String judgementText = internalContest.getJudgement(judgementRecord.getJudgementId()).toString();
            String validatorJudgementName = judgementRecord.getValidatorResultString();
            if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                if (validatorJudgementName.trim().length() == 0) {
                    validatorJudgementName = "undetermined";
                }
                judgementText = validatorJudgementName;
            }
            return judgementText;
        }

        return "";
    }
 
    public boolean isSolved() {
        return solved;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public ITeam getTeam() {
        return submitterTeam;
    }

    public String getJudgementName() {
        return judgementTitle;
    }

    public IProblem getProblem() {
        return problem;
    }

    public ILanguage getLanguage() {
        return language;
    }

    public int getNumber() {
        return number;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public long getSubmissionTime() {
        return elapsedMins;
    }

    public String[] getSourceCodeFileNames() {

        if (runFiles == null) {
            fetchRunFiles();
        }

        try {
            if (runFiles != null) {

                String[] names = new String[1];
                names[0] = runFiles.getMainFile().getName();

                SerializedFile[] files = runFiles.getOtherFiles();
                if (files != null) {
                    if (files.length > 0) {
                        names = new String[1 + files.length];
                        names[0] = runFiles.getMainFile().getName();
                        for (int i = 0; i < files.length; i++) {
                            SerializedFile file = files[i];
                            names[i + 1] = file.getName();
                        }
                        return names;
                    } else {
                        return names;
                    }
                } else {
                    // only main file
                    return names;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    public byte[][] getSourceCodeFileContents() {
        if (runFiles == null) {
            fetchRunFiles();
        }

        if (runFiles != null) {

            byte[][] fileContents = new byte[1][64000];

            fileContents[0] = runFiles.getMainFile().getBuffer();

            SerializedFile[] files = runFiles.getOtherFiles();
            if (files != null) {
                if (files.length > 0) {
                    fileContents = new byte[1 + files.length][];
                    fileContents[0] = runFiles.getMainFile().getBuffer();
                    for (int i = 0; i < files.length; i++) {
                        SerializedFile file = files[i];
                        fileContents[i + 1] = file.getBuffer();
                    }
                    return fileContents;
                } else {
                    return fileContents;
                }
            } else {
                // only main file
                return fileContents;
            }
        }
        return new byte[0][0];
    }

    private void fetchRunFiles() {

        if (runFiles != null) {
            return;
        }

        if (fetchRunListenerImplemenation == null) {
            fetchRunListenerImplemenation = new FetchRunListenerImplemenation();
            internalContest.addRunListener(fetchRunListenerImplemenation);
        }
        controller.fetchRun(run);
        synchronized (listening) {
            while (!answerReceived) {
                try {
                    listening.wait();
                } catch (InterruptedException e) {
                    // ok, just loop again
                    listening.booleanValue(); // terrible kludge because empty block not allowed.
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof RunImplementation) {
            RunImplementation runImplementation = (RunImplementation) obj;
            return (runImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }

    // public void info(String s) {
    // System.out.println(new Date() + " " +Thread.currentThread().getName() + " " + s);
    // System.out.flush();
    // }

    /**
     * Listener for run fetched from server.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    protected class FetchRunListenerImplemenation implements IRunListener {

        public void runAdded(RunEvent event) {
            runChanged(event);
        }

        public void runChanged(RunEvent event) {

            if (event.getRun().getElementId().equals(elementId)) {
                // found the run we requested
                runFiles = event.getRunFiles();
                synchronized (listening) {
                    try {
                        answerReceived = true;
                        listening.notify();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // run not removed, ignored
        }
    }
    
    /**
     * Is this client allowed to do the permission.
     *  
     * @param type
     * @return true of allowed, false otherwise.
     */
    protected boolean isAllowed(Permission.Type type) {
        PermissionList newPermissionList = new PermissionList();
        
        Account account = internalContest.getAccount(internalContest.getClientId());
        if (account != null) {
            newPermissionList.clearAndLoadPermissions(account.getPermissionList());
        } else {
            // Set default conditions
            newPermissionList.clearAndLoadPermissions(new PermissionGroup().getPermissionList(internalContest.getClientId().getClientType()));
        }
        return newPermissionList.isAllowed(type);
    }

    public IRunJudgement[] getRunJudgements() {
        
        Vector<RunJudgementImplemenation> vector = new Vector<RunJudgementImplemenation>();
        if (run != null && run.isJudged()) {
            JudgementRecord[] records = run.getAllJudgementRecords();
            
            ContestInformation contestInformation = internalContest.getContestInformation();
            JudgementNotificationsList judgementNotificationsList = contestInformation.getJudgementNotificationsList();
            ContestTime contestTime = internalContest.getContestTime();
            
            boolean respectEOCSetting = isAllowed(Permission.Type.RESPECT_EOC_SUPPRESSION);
            
            for (int i = 0; i < records.length; i++) {
                if (!respectEOCSetting) {
                    vector.addElement(new RunJudgementImplemenation(records[i], run, internalContest, controller));
                } else {
                    if (!RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime)) {
                        vector.addElement(new RunJudgementImplemenation(records[i], run, internalContest, controller));
                    }
                }
            }
        }
        
        return (RunJudgementImplemenation[]) vector.toArray(new RunJudgementImplemenation[vector.size()]);
    }

    public boolean isFinalJudged() {
        return finalJudged;
    }

    public boolean isPreliminaryJudged() {
        return preliminaryJudged;
    }
}
