package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.ILanguage;

/**
 * Languages.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintLanguages extends APIAbstractTest {

    @Override
    public void printTest() {
        ILanguage[] languages = getContest().getLanguages();

        println("There are " + languages.length + " languages");
        for (ILanguage language : languages) {
            println("Language " + language.getName());
        }
    }

    @Override
    public String getTitle() {
        return "getLanguages";
    }
}
