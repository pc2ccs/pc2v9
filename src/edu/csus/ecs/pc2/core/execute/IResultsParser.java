package edu.csus.ecs.pc2.core.execute;

import java.util.Hashtable;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Validator results parser interface.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IResultsParser {

    /**
     * XML Attribute name for validator judgement.
     * 
     * The value is the judgement itself.
     */
    String OUTCOME_KEY = "outcome";

    /**
     * XML element name for validator results.
     */
    String RESULT_KEY = "result";
    
    /**
     * Extra text/information about the judgement from the validator key.
     */
    String CONTENT_KEY = "CONTENT";

    /**
     * Parse input XML file, populate results ({@link #getResults()}).
     * 
     * Parse input XML file, if expected results found (See International Standard for validator results), will populate attributes with XML name value pairs and return true.
     * 
     * @param filename
     *            input International Standard results XML file.
     * @return false if no judgement/outcome found or parse error, else true (and results can be found using {@link #getResults()}
     */
    boolean parseValidatorResultsFile(String filename);

    /**
     * The results of the parsing.
     * 
     * Keys for results are: {@link #OUTCOME_KEY}, {@link #RESULT_KEY}, {@link #CONTENT_KEY}.
     * 
     * @return results of parsing.
     */
    Hashtable<String, String> getResults();

    void setLog(Log log);

}
