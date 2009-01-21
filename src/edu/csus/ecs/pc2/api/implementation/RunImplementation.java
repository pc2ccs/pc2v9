package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunJudgement;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
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

//    private boolean judged;

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

    /**
     * 
     * @param run
     * @param internalContest
     */
    public RunImplementation(edu.csus.ecs.pc2.core.model.Run run, IInternalContest internalContest, IInternalController controller) {

        this.controller = controller;
        this.internalContest = internalContest;
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
        
        this.run = run;
        
        if (run.isJudged()){
            setPreliminaryJudgement();
        }

        submitterTeam = new TeamImplementation(run.getSubmitter(), internalContest);

        problem = new ProblemImplementation(run.getProblemId(), internalContest);

        language = new LanguageImplementation(run.getLanguageId(), internalContest);

        number = run.getNumber();

        siteNumber = run.getSiteNumber();

        elapsedMins = run.getElapsedMins();

        elementId = run.getElementId();
    }
    
    /**
     * Set/establish whether this judgement is a preliminary judgement or not.
     * 
     * Determine whether this judgement record is a preliminary or final judgement.
     * 
     */
    private void setPreliminaryJudgement() {

        Problem theProblem = internalContest.getProblem(run.getProblemId());
        if (theProblem.isManualReview() && theProblem.isComputerJudged()) {
            /**
             * Only preliminary possible is if is manual review AND computer judged.
             */

            JudgementRecord[] records = run.getAllJudgementRecords();
            if (records != null) {
                /**
                 * If there are judgements, only the first (computer judged) will be a preliminary judged run.
                 */
                JudgementRecord record = run.getJudgementRecord();
                preliminaryJudged = records[0].getElementId().equals(record.getElementId());
            }
            finalJudged = ! preliminaryJudged;
        } else if (run.getAllJudgementRecords().length > 0) {
            // has judgements
            finalJudged = true;
        }
        // else - the run is not judged.
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

    public IRunJudgement[] getRunJudgements() {
        if (run != null && run.isJudged()) {
            JudgementRecord[] records = run.getAllJudgementRecords();
            RunJudgementImplemenation[] implemenations = new RunJudgementImplemenation[records.length];

            for (int i = 0; i < records.length; i++) {
                implemenations[i] = new RunJudgementImplemenation(records[i], run, internalContest, controller);
            }
            return implemenations;

        } else {
            return new RunJudgementImplemenation[0];
        }
    }
    

    public boolean isFinalJudged() {
        return finalJudged;
    }

    public boolean isPreliminaryJudged() {
        return preliminaryJudged;
    }
}
