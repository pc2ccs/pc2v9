package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Implementation for IProblem.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemImplementation implements IProblem {

    private String name;

    private String judgesDataFileName;

    private byte[] judgesDataFileContents;

    private String judgesAnswerFileName;

    private byte[] judgesAnswerFileContents;

    private String validatorCommandLine;

    private String validatorFileName;
    
    private ElementId elementId;

    public ProblemImplementation(ElementId problemId, IInternalContest internalContest) {
        this(internalContest.getProblem(problemId), internalContest);
    }

    public ProblemImplementation(Problem problem, IInternalContest internalContest) {
        elementId = problem.getElementId();
        name = problem.getDisplayName();
        judgesDataFileName = problem.getDataFileName();
        judgesAnswerFileName = problem.getAnswerFileName();
        validatorFileName = problem.getValidatorProgramName();
        
        
        ProblemDataFiles problemDataFiles = internalContest.getProblemDataFile(problem);
        if (problemDataFiles != null) {

            SerializedFile serializedFile = problemDataFiles.getJudgesDataFile();
            if (serializedFile != null){
                judgesDataFileContents = serializedFile.getBuffer();
            }
            
            serializedFile = problemDataFiles.getJudgesAnswerFile();
            if (serializedFile != null){
                judgesAnswerFileContents = serializedFile.getBuffer();
            }
            
        }

    }

    public String getName() {
        return name;
    }

    public String getJudgesDataFileName() {
        return judgesDataFileName;
    }

    public byte[] getJudgesDataFileContents() {
        return judgesDataFileContents;
    }

    public String getJudgesAnswerFileName() {
        return judgesAnswerFileName;
    }

    public byte[] getJudgesAnswerFileContents() {
        return judgesAnswerFileContents;
    }

    public String getValidatorFileName() {
        return validatorFileName;
    }

    public String getValidatorCommandLine() {
        return validatorCommandLine;
    }

    public byte[] getValidatorFileContents() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasExternalValidator() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean readsInputFromFile() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean readsInputFromStdIn() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasDataFile() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasAnswerFile() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof IProblem){
            
        } else {
            false;
        }
    }
    
}
