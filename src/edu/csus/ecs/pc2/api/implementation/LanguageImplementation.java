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
    
    private ElementId elementId;

    public LanguageImplementation(ElementId languageId, IInternalContest internalContest) {
        this(internalContest.getLanguage(languageId));
    }

    public LanguageImplementation(Language language) {
        name = language.getDisplayName();
        elementId = language.getElementId();
    }

    public String getName() {
        return name;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof LanguageImplementation) {
            LanguageImplementation languageImplementation = (LanguageImplementation) obj;
            return (languageImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
    
    public ElementId getElementId() {
        return elementId;
    }
}
