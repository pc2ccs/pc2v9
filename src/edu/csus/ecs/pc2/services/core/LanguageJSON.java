package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class LanguageJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Language language, int languageNumber) {
        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", Integer.toString(languageNumber));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", language.getDisplayName());

        return stringBuilder.toString();
    }
}
