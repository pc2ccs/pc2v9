package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Problem Display List.
 *
 * Contains a list of Problem, in order, to display for the users.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/ProblemDisplayList.java $
public class ProblemDisplayList extends ElementDisplayList {

    /**
     *
     */
    private static final long serialVersionUID = -6256102346010208716L;

    public static final String SVN_ID = "$Id: ProblemDisplayList.java 762 2006-11-29 09:04:16Z boudreat $";

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
