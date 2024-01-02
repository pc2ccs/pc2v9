// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api;

import java.util.HashMap;

import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Team</i>.
 * Note that every <I>Team</i> in the API view is a subclass of {@link IClient},
 * so a &quot;team view&quot; also contains the general information described by
 * {@link IClient}.
 *
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ITeam extends IClient {

   /**
     * Get the group associate with this team.
     *
     * @see IGroup
     * @return group information.
     */
    public HashMap<ElementId, IGroup> getGroups();

    /**
     * Get the primary (cms) group for a team
     *
     * @return id of the CMS supplied group (so-called primary group)
     */
    public IGroup getPrimaryGroup();
}
