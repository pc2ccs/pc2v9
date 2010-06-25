package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Problem}s which are displayed to the users.
 *
 * Contains a list of Problem, in order, to display for the users.
 *
 * @see edu.csus.ecs.pc2.core.list.ProblemList
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemDisplayList extends ElementDisplayList {

    /**
     *
     */
    private static final long serialVersionUID = -6256102346010208716L;

    public void addElement(Problem problem) {
        super.addElement(problem);
    }

    public void insertElementAt(Problem problem, int idx) {
        super.insertElementAt(problem, idx);
    }

    public void update(Problem problem) {
        for (int i = 0; i < size(); i++) {
            Problem listProblem = (Problem) elementAt(i);

            if (listProblem.getElementId().equals(problem.getElementId())) {
                setElementAt(problem, i);
            }
        }
    }
    /**
     * Get a sorted list of Problems.
     * 
     * @return the array of Problems
     */
    public Problem[] getList() {
        if (size() == 0) {
            return new Problem[0];
        } else {
            return (Problem[]) this.toArray(new Problem[this.size()]);
        }
    }

}
