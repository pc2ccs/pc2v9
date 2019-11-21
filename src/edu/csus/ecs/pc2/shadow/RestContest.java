// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

/**
 * Responsible for starting and controlling Remote Event Feed (processor)
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RestContest {
    
    ShadowData data;
    
    RestContest(ShadowData data){
        this.data = data;
    }
    
    ShadowData fetchContestModel() {
        return data;
    }
    
 
    
}
