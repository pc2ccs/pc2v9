package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Implementation for IProblem.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemImplementation implements IProblem {

    private String title;

    public ProblemImplementation(ElementId problemId, IInternalContest internalContest) {
        this(internalContest.getProblem(problemId));
    }

    public ProblemImplementation(Problem problem) {
        title = problem.getDisplayName();
    }

    public String getTitle() {
        return title;
    }
}
