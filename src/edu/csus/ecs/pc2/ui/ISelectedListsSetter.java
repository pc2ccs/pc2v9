// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.List;

/**
 * Interface for callbacks/observers.
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public interface ISelectedListsSetter  {

	/**
	 * Provide observers a list of selected values.
	 *
	 * @param selectedValuesList list of selected values
	 * @param selectedIndices list of selected indexes
	 */
	void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices);

}
