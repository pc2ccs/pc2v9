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

    private byte[] validatorFileContents;

    private boolean externalValidator = false;

    private boolean readsInputFromSTDIN = false;

    private String shortName;

    private boolean deleted = false;

    public ProblemImplementation(ElementId problemId, IInternalContest internalContest) {
        this(internalContest.getProblem(problemId), internalContest);
    }

    public ProblemImplementation(Problem problem, IInternalContest internalContest) {
        elementId = problem.getElementId();
        name = problem.getDisplayName();
        shortName = problem.getShortName();
        if (shortName == null || shortName.isEmpty()) {
            shortName = name.toLowerCase();
            int space = shortName.indexOf(" ");
            if (space > 0) {
                shortName = shortName.substring(0, space);
            }
        }
        judgesDataFileName = problem.getDataFileName();
        judgesAnswerFileName = problem.getAnswerFileName();
        validatorFileName = problem.getValidatorProgramName();
        
        if (problem.isValidatedProblem()){
            if (! problem.isUsingPC2Validator()){
                externalValidator = true;
            }
        }
        
        readsInputFromSTDIN = problem.isReadInputDataFromSTDIN();
        deleted = ! problem.isActive();
        
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

            serializedFile = problemDataFiles.getValidatorFile();
            if (serializedFile != null){
                validatorFileContents = serializedFile.getBuffer();
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
        return validatorFileContents;
    }

    public boolean hasExternalValidator() {
        return externalValidator;
    }

    public boolean readsInputFromFile() {
        if (hasDataFile()) {
            return !readsInputFromStdIn();
        }
        return false;
    }

    public boolean readsInputFromStdIn() {
        if (hasDataFile()) {
            return readsInputFromSTDIN;
        }
        return false;
    }

    public boolean hasDataFile() {
        return (judgesDataFileContents != null);
    }

    public boolean hasAnswerFile() {
        return (judgesAnswerFileContents != null);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ProblemImplementation) {
            ProblemImplementation problemImplementation = (ProblemImplementation) obj;
            return (problemImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
    
    public ElementId getElementId() {
        return elementId;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
}
