// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class PermissionYamlLoaderTest extends AbstractTestCase{

    public void testSampleSection() throws Exception {
        
        String [] lines = {
 
                "permissions:", // 
                "  - account: ADMINISTRATOR", // 
                "    number: All", // 
                "    disable: VIEW_SECURITY_ALERTS", // 
                
                "  - account: ADMINISTRATOR", // 
                "    number: 1,2,3", // 
                "    disable: VIEW_SECURITY_ALERTS", //
                
                "  - account: FEEDER", // 
                "    number: 1", // 
                "    enable: SHADOW_PROXY_TEAM, EDIT_RUN", // 

        };
        
        fail("TODO debug 22 complete JUnit ");
        
//        SampleContest sample = new SampleContest();
//        IInternalContest contest = sample.createStandardContest();
//
//        Account[] accounts = contest.getAccounts();
//
//        PermissionYamlLoader loader = new PermissionYamlLoader(lines, accounts);
//
//        List<Account> accList = loader.getAccounts();
//        for (Account account : accList) {
//
//            System.out.println("debug 22 account =" + account);
//
//        }
        
        
    }
}
