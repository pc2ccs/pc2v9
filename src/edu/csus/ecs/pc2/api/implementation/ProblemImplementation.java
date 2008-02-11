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

    public ProblemImplementation(ElementId problemId, IInternalContest internalContest) {
        this(internalContest.getProblem(problemId), internalContest);
    }

    public ProblemImplementation(Problem problem, IInternalContest internalContest) {
        name = problem.getDisplayName();
        judgesDataFileName = problem.getDataFileName();
        judgesAnswerFileName = problem.getAnswerFileName();
        
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
}
