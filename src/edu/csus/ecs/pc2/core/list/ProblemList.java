package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Problem;


/**
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ProblemList extends ElementList {

    /**
     *
     */
    private static final long serialVersionUID = 249172377158067816L;

    public static final String SVN_ID = "$Id$";

//    private Comparator probCompare = new ProblemComparator();

    public void add(Problem problem) {
        super.add(problem);
    }

    /**
     * Get an array of problems in random/hash order.
     * @return
     */
    @SuppressWarnings("unchecked")
    public Problem[] getList() {
        Problem[] theList = new Problem[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (Problem[]) values().toArray(new Problem[size()]);
        return theList;

    }
}
