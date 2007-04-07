package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.BaseElementList;

/**
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ProblemDataFilesList extends BaseElementList {

    /**
     *
     */
    private static final long serialVersionUID = -4839513516529441799L;

    public static final String SVN_ID = "$Id$";

    @Override
    public String getKey(IElementObject elementObject) {
        if (elementObject instanceof Problem) {
            return elementObject.getElementId().toString();
        } else {
            ProblemDataFiles files = (ProblemDataFiles) elementObject;
            return files.getProblemId().toString();
        }
    }
}
