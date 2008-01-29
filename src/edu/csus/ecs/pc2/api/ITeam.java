package edu.csus.ecs.pc2.api;

/**
 * Team information.
 * 
 * Contains information about a contest team.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ITeam extends IClient {

	/**
	 * Get the display name/title for this team.
	 * 
	 * @return display name for team.
	 */
	String getLongName();

	/**
	 * Get the group for this team.
	 * 
	 * @return group information.
	 */
	IGroup getGroup();

	// TODO code getTeamMemberNames
	// String [] getTeamMemberNames()

}
