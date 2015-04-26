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
    
    private String compilerCommandLine;
    private String executionCommandLine;
    private boolean interpreted;
    private String executableMask;
    

    public LanguageImplementation(ElementId languageId, IInternalContest internalContest) {
        this(internalContest.getLanguage(languageId));
    }

    public LanguageImplementation(Language language) {
        name = language.getDisplayName();
        elementId = language.getElementId();

        compilerCommandLine = language.getCompileCommandLine();
        executionCommandLine = language.getProgramExecuteCommandLine();
        interpreted = language.isInterpreted();
        executableMask = language.getExecutableIdentifierMask();
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
    
    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getCompilerCommandLine() {
        return compilerCommandLine;
    }

    @Override
    public boolean isInterpreted() {
        return interpreted;
    }

    @Override
    public String getExecutionCommandLine() {
        return executionCommandLine;
    }

    @Override
    public String getExecutableMask() {
        return executableMask;
    }
    
}
