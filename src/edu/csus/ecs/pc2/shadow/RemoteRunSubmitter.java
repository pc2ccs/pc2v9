// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.File;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * This class is used to submit runs, obtained from a remote CCS being shadowed by this instance of PC2, 
 * to the local PC2 server.
 * 
 * The class is instantiated with a PC2 Controller ({@link IInternalController}) which it uses to submit
 * the run to the local server.
 * 
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteRunSubmitter {

    private IInternalController  controller;

    public RemoteRunSubmitter(IInternalController controller) {
        this.controller = controller;
    }
 
    public void submitRun (ClientId id, Problem problem, Language language, File mainfile, File [] auxFiles, int overrideTimeMS, int overrideRunId){
        
        // TODO Bug 1627 code
        
//        controller.submitRun(problem, language, mainfile, auxFiles);  //something like this, but using PC2APIFile
        
        // TODO Use PC2APIFile
    }
    
}
