package edu.csus.ecs.pc2.core.execute;

import java.util.Hashtable;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Results Parser interface.
 * 
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public interface IResultsParser {

    /**
     * parses file and populates results.
     * 
     * @param string
     * @return true if can parse file and populated results
     */
    boolean parseValidatorResultsFile(String string);

    /**
     * The results of the parsing.
     * 
     * 
     * @return results of parsing.
     */
    Hashtable<String, String> getResults();

    void setLog(Log log);

}
