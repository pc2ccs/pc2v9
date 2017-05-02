package edu.csus.ecs.pc2.core.model.inputValidation;

import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Problem;

/**
 * This class holds the collection of {@link InputValidationResult>s for a specific {@link Problem}.
 * @author John
 *
 */
public class ProblemInputValidationResults {
    
    private Problem problem ;
    private Vector<InputValidationResult> ivResults;
    
    /**
     * Constructs a ProblemInputValidationResults object containing the specified {@link Problem} and the
     * specified set of {@link InputValidationResult}s.
     * 
     * @param theProblem - the Problem for which the {@link InputValidationResult}s apply
     * @param inputValidationResults - the {@link InputValidationResult}s for the specified {@link Problem}
     */
    public ProblemInputValidationResults(Problem theProblem, Iterable<InputValidationResult> inputValidationResults) {
        this.problem = theProblem;
        this.ivResults = new Vector<InputValidationResult>();
        for (InputValidationResult res : inputValidationResults) {
            ivResults.add(res);
        }
    }
    
    /**
     * Constructs a ProblemInputValidationResults object containing the specified {@link Problem} and an empty
     * set of {@link InputValidationResult}s.
     * 
     * @param theProblem - the Problem for which the {@link InputValidationResult}s apply
     * @param inputValidationResults - the {@link InputValidationResult}s for the specified {@link Problem}
     */    
    public ProblemInputValidationResults(Problem theProblem) {
        this.problem = theProblem;
        this.ivResults = new Vector<InputValidationResult>();
    }
    
    /**
     * Returns the {@link Problem} to which this object refers.
     * 
     * @return a {@link Problem}
     */
    public Problem getProblem() {
        return this.problem;
    }
    
    /**
     * Returns an {@link Iterable} over the set of {@link InputValidationResult}s in this object.
     * Note that while the returnd {@link Iterable} may be empty, it will never be null.
     * 
     * @return an {@link Iterable} which can be used to iterate over the {@link InputValidationResult}s
     */
    public Iterable<InputValidationResult> getResults() {
        return ivResults;
    }
    
    /**
     * Adds the specified {@link InputValidationResult} to the set of results in this object.
     * 
     * @param aResult the {@link InputValidationResult} to be added
     */
    public void addResult(InputValidationResult aResult) {
        ivResults.add(aResult);
    }

}
