package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * API IJudgement implementation.  
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JudgementImplementation implements IJudgement {

    private String name;
    private ElementId elementId;
    private String acronym;

    public JudgementImplementation(Judgement judgement) {
        acronym = judgement.getAcronym();
        name = judgement.getDisplayName();
        elementId = judgement.getElementId();
    }

    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof JudgementImplementation) {
            JudgementImplementation judgementImplementation = (JudgementImplementation) obj;
            return (judgementImplementation.elementId.equals(elementId));
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
    public String getAcronym() {
        return acronym;
    }
}
