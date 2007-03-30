package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.model.IElementObject}
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ElementList extends BaseElementList {

    /**
     *
     */
    private static final long serialVersionUID = 3342791703918549522L;

    public static final String SVN_ID = "$Id$";

    /**
     * The key for this list is the elementId.
     *
     * @param elementObject
     * @return the object key
     */
    public String getKey(IElementObject elementObject) {
        return elementObject.getElementId().toString();
    }

}
