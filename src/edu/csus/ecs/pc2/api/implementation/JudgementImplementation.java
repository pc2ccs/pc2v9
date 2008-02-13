package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * API IJudgement implementation.  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementImplementation implements IJudgement {

    private String name;
    private ElementId elementId;

    public JudgementImplementation(Judgement judgement) {
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

}
