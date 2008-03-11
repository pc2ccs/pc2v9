package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Implementation for IRun.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunImplementation implements IRun {

    private boolean judged;

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
    
    private Boolean listening = new Boolean (true);

    private IInternalController controller = null;
    
    private FetchRunListenerImplemenation fetchRunListenerImplemenation = null;

    private IInternalContest internalContest = null;
    
    private Run run = null;

    /**
     * 
     * @param run
     * @param internalContest
     */
    public RunImplementation(edu.csus.ecs.pc2.core.model.Run run, IInternalContest internalContest, IInternalController controller) {

        this.controller = controller;
        this.internalContest  = internalContest;
        judged = run.isJudged();
        solved = run.isSolved();
        deleted = run.isDeleted();

        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord != null) {
            String judgementText = internalContest.getJudgement(judgementRecord.getJudgementId()).toString();
            String validatorJudgementName = judgementRecord.getValidatorResultString();
            if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                if (validatorJudgementName.trim().length() == 0) {
                    validatorJudgementName = "undetermined";
                }
                judgementText = validatorJudgementName;
            }
            judgementTitle = judgementText;
        }

        submitterTeam = new TeamImplementation(run.getSubmitter(), internalContest);

        problem = new ProblemImplementation(run.getProblemId(), internalContest);

        language = new LanguageImplementation(run.getLanguageId(), internalContest);
        
        number = run.getNumber();
        
        siteNumber = run.getSiteNumber();
        
        elapsedMins = run.getElapsedMins();
        
        elementId = run.getElementId();
        
        this.run = run;

    }

    public boolean isJudged() {
        return judged;
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
        
        if (runFiles != null){
            
            String [] names = new String[0];
            names[0] = runFiles.getMainFile().getName();
            
            SerializedFile [] files = runFiles.getOtherFiles();
            if (files != null){
                if (files.length > 0){
                    names = new String[1 + files.length];
                    names[0] = runFiles.getMainFile().getName();
                    for (int i = 0; i < files.length; i++) {
                        SerializedFile file = files[i];
                        names[i+1] = file.getName();                        
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
        
        return new String[0];
    }

    public byte[][] getSourceCodeFileContents() {
        if (runFiles == null) {
                fetchRunFiles();
        }
        
        if (runFiles != null){
            
            byte [] [] fileContents = new byte[1][1];
            
            fileContents[0] = runFiles.getMainFile().getBuffer();
            
            SerializedFile [] files = runFiles.getOtherFiles();
            if (files != null){
                if (files.length > 0){
                    fileContents = new byte[1 + files.length][];
                    fileContents[0] = runFiles.getMainFile().getBuffer();
                    for (int i = 0; i < files.length; i++) {
                        SerializedFile file = files[i];
                        fileContents[i+1] = file.getBuffer();                        
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
    
    private void fetchRunFiles(){
        
        if (runFiles != null){
            return;
        }
        
        if (fetchRunListenerImplemenation == null){
            fetchRunListenerImplemenation = new FetchRunListenerImplemenation();
            internalContest.addRunListener(fetchRunListenerImplemenation);
        }
        controller.checkOutRun(run, true);
        
        while ( listening.booleanValue()){
            try {
                System.out.println("Waiting "+new java.util.Date());
                listening.wait();
            } catch (InterruptedException e) {
                // ok, just loop again
                listening.booleanValue(); // terrible kludge because empty block not allowed.
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
    
    
    /**
     * Listener for run fetched from server.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    protected class FetchRunListenerImplemenation implements IRunListener {

        public void runAdded(RunEvent event) {
            // run not added, ignored
        }

        public void runChanged(RunEvent event) {

            if (event.getRun().getElementId().equals(elementId)) {
                // found the run we requested
                runFiles = event.getRunFiles();
                listening = new Boolean(false);
                listening.notify();
            }
        }

        public void runRemoved(RunEvent event) {
            // run not removed, ignored
        }
    }
}
