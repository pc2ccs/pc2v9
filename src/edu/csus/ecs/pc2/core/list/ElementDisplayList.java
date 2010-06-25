package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * A list of elements to be displayed.
 *
 * This list should contain only active elements which are to be displayed.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 *
 *
 */

// $HeadURL$
public class ElementDisplayList extends Vector<IElementObject> implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1699962997232268879L;

    public static final String SVN_ID = "$Id$";

}
