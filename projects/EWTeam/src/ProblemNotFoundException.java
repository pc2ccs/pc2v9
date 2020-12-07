// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemNotFoundException extends Exception {
	/**
     * 
     */
	private static final long serialVersionUID = 6792166764752870808L;

	public ProblemNotFoundException() {
		super("Problem does not exist.");
	}
}
