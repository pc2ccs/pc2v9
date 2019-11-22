// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

/**
 * This class encapsulates the configuration obtained from a remote CLICS Contest API.
 * 
 * It is used to hold a representation of a remote contest configuration during Shadow CCS operations.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RemoteContest {
    
    String data;
    
    RemoteContest(String data){
        this.data = data;
    }
    
    String getContestModel() {
        return data;
    }
    
 
    
}
