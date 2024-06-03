// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * List of judge's solutions
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class SubmissionSolutionList extends ArrayList<SubmissionSampleLocation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6231245379952020474L;
	
	public SubmissionSolutionList() {
		super();
	}
	
	public SubmissionSolutionList(File cdpPath) {
		super();
		loadCDPSampleSubmissionList(cdpPath);
	}
	

    /**
     * get all directories and child directories (recurses).
     * 
     * @param directory
     * @return list of directory names
     */
	// TODO REFACTOR move to FileUtilities.getAllDirectoryEntries
    public static List<String> getAllDirectoryEntries(String directory) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();
        
        if (files != null) {
        	
        	for (File entry : files) {
        		if (entry.isDirectory()) {
        			list.add(directory + File.separator + entry.getName());
        			if (!(entry.getName().equals(".") || entry.getName().equals(".."))) {
        				list.addAll(getAllDirectoryEntries(directory + File.separator + entry.getName()));
        			}
        		}
        	}
        }

        return list;
    }
    


	/**
	 * Load Judge's samples judgement types
	 * @param cdpPath
	 * @throws IOException 
	 */
    // TODO NOW use FileUtilities.getAllDirectoryEntries
	private void loadCDPSampleSubmissionList(File cdpPath)  {
		
		SortedMap<SubmissionSampleLocation, String> uniqItems = new TreeMap<SubmissionSampleLocation, String>();
		try {
			List<String> entries = getAllDirectoryEntries(cdpPath.getCanonicalPath());
			
			String pat = IContestLoader.SUBMISSIONS_DIRNAME;
			
			for (String dirname : entries) {
				try {
				    // pattern found in dirname but not at the end, eliminates submission at end of string
				    // only includes subdirectories under submissions/ and not the submissions directory
					if (dirname.contains(pat) && dirname.lastIndexOf(pat) != dirname.length() - pat.length()){
						String baseName = new File (dirname).getName();
						SubmissionSampleLocation subLoc = new SubmissionSampleLocation("", baseName);
						uniqItems.put(subLoc, subLoc.toString());
					}
				} catch (Exception e) {
					e.printStackTrace(); // debug 22
					return; // debug 22
				}
			}
			
		} catch (Exception e) {
			; // ignore, do not load any items into list because no match
		}
		
		Set<SubmissionSampleLocation> keys = uniqItems.keySet();
		for (SubmissionSampleLocation submissionSampleLocation : keys) {
			super.add(submissionSampleLocation);
		}
		
	}

	public void loadDefaultList() {

		String[] initList = {

				"Bad Format ; badformat", //
				"Run Time Error ; run_time_error", //
				"Run Time Error ; runtime_error", //
				"Security Violation ; security_violation", //
				"Time Limit ; time_limit", //
				"Time Limit Exceeded ; time_limit_exceeded", //
				"Wrong Answer ; wrong_answer", //
				"Wrong Answer ; wronganswer", //

		};

		for (String entry : initList) {
			String[] fields = entry.split(";");
			String title = fields[0].trim();
			String dirName = fields[1].trim();

			super.add(new SubmissionSampleLocation(title, dirName));
		}
	}
}
