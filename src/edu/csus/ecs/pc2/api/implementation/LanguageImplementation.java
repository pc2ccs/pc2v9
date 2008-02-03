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

    private String title;

    public LanguageImplementation(ElementId languageId, IInternalContest internalContest) {
        this(internalContest.getLanguage(languageId));
    }

    public LanguageImplementation(Language language) {
        title = language.getDisplayName();
    }

    public String getTitle() {
        return title;
    }
}
