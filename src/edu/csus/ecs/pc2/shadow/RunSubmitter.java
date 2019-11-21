// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.File;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;

public class RunSubmitter {

    private IInternalController  controller;

    public RunSubmitter(IInternalController controller) {
        this.controller = controller;
    }
 
    public void runSubmit (ClientId id, Problem problem, Language language, File mainfile, File [] auxFiles, int overrideTimeMS, int overrideRunId){
        
        // TODO Bug 1261 code
        
        // TODO Bug 1261 Use PC2File
    }
    
}
