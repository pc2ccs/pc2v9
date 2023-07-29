// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.List;

/**
 * Set/Return selected valuea or indexes.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
// TODO NOW 232 improve comment per PR 
public interface ISelectedListsSetter  {

	/**
	 * Callback containing list of selected values and indexes.
	 * 
	 * @param selectedValuesList list of selected values
	 * @param selectedIndices list of selected indexes
	 */
	void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices);

}
