package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * Implementation for for ILanguage.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LanguageImplementation implements ILanguage {

    private String name;

    public LanguageImplementation(ElementId languageId, IInternalContest internalContest) {
        this(internalContest.getLanguage(languageId));
    }

    public LanguageImplementation(Language language) {
        name = language.getDisplayName();
    }

    public String getName() {
        return name;
    }
}
