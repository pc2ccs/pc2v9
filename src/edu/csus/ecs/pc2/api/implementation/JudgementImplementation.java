package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * API IJudgement implementation.  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementImplementation implements IJudgement {

    private String title;

    public JudgementImplementation(Judgement judgement) {
        title = judgement.getDisplayName();
    }

    public String getTitle() {
        return title;
    }

}
