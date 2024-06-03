// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

/**
 * A single entry of a judge's solution locations.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class SubmissionSampleLocation  implements Comparable<SubmissionSampleLocation> {
	private String title;
	private String shortDirectoryName;

	/**
	 * A judges solution name and location.
	 *  
	 * @param title title for directory, ex. Run Time Error
	 * @param shortDirectoryName base directory name, ex. run_time_error
	 */
	public SubmissionSampleLocation(String title, String shortDirectoryName) {
		this.title = title;
		this.shortDirectoryName = shortDirectoryName;;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getShortDirectoryName() {
		return shortDirectoryName;
	}

	@Override
	public String toString() {
		if (title.length() > 0) {
			return title + " ("+shortDirectoryName+")";
		} else {
			return shortDirectoryName;
		}
	}

	@Override
	public int compareTo(SubmissionSampleLocation o) {
		return o.toString().compareTo(toString());
	}

}
