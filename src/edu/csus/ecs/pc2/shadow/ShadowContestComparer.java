// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.csus.ecs.pc2.core.model.IInternalContest;

public class ShadowContestComparer {
    
    private ShadowData data;

    ShadowContestComparer (ShadowData data){
        this.data = data;
    }
    
    public List<String> diff (IInternalContest contest) {
        // TODO Bug 1261 code
        
        throw new NotImplementedException();
    }
    
    public static ShadowData getRemoteContest(URL restURL, String login, String password) {
        throw new NotImplementedException();
    }
    
    public boolean isSameAs(IInternalContest contest){
        return false;
    }
}
