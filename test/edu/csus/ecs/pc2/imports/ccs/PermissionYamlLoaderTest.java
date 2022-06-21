// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class PermissionYamlLoaderTest extends AbstractTestCase{

    public void testPermissionsFromData() throws Exception {
        
        String [] lines = {
 
                "permissions:", // 
                "  - account: ADMINISTRATOR", // 
                "    number: All", // 
                "    disable: VIEW_SECURITY_ALERTS", // 
                
                "  - account: ADMINISTRATOR", // 
                "    number: 1,2,3", // 
                "    disable: VIEW_SECURITY_ALERTS", //
                
                "  - account: JUDGE", // 
                "    number: 1", // 
                "    enable: GIVE_RUN", //
                
                "  - account: FEEDER", // 
                "    number: 1", // 
                "    enable: SHADOW_PROXY_TEAM, EDIT_RUN", // 

        };
        
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        sample.generateNewAccounts(contest,ClientType.Type.FEEDER, 1); 
        sample.generateNewAccounts(contest,ClientType.Type.ADMINISTRATOR, 4);
        
        Account[] accounts = contest.getAccounts();
        
        // Test that accounts do not have Permissions

        Account judge1 = sample.getAccount(contest, Type.JUDGE, 1);
        assertNoPermssion(Permission.Type.GIVE_RUN, judge1);

        Account feeder1 = sample.getAccount(contest, Type.FEEDER, 1);
        assertNoPermssion(Permission.Type.EDIT_RUN, feeder1);

        PermissionYamlLoader loader = new PermissionYamlLoader(lines, accounts);
        contest.updateAccounts(loader.getAccountsArray());

        Account admin1 = sample.getAccount(contest, Type.ADMINISTRATOR, 1);
        assertHasPermssion(Permission.Type.GIVE_RUN, admin1);
        
        feeder1 = sample.getAccount(contest, Type.FEEDER, 1);
        assertHasPermssion(Permission.Type.EDIT_RUN, feeder1);

        judge1 = sample.getAccount(contest, Type.JUDGE, 1);
        assertHasPermssion(Permission.Type.GIVE_RUN, judge1);
    }
    
    public void testTestPermissionsFromContestYaml() throws Exception {

        SampleContest sample = new SampleContest();

        String sampleContestDirName = "tenprobs";

        IInternalContest contest = sample.loadSampleContest(null, sampleContestDirName);
        assertNotNull("Expecting to load " + sampleContestDirName + " contest", contest);

        // test for permissions that should be set or not set

        Account admin3 = sample.getAccount(contest, Type.FEEDER, 3);
        assertNoPermssion(Permission.Type.VIEW_SECURITY_ALERTS, admin3);

        Account feeder3 = sample.getAccount(contest, Type.FEEDER, 3);
        assertHasPermssion(Permission.Type.EDIT_RUN, feeder3);
        
        // All Judges have GIVE_RUN for testing only.
        
        Vector<Account> judges = contest.getAccounts(Type.JUDGE);
        
        edu.csus.ecs.pc2.core.security.Permission.Type expectedPerm = Permission.Type.GIVE_RUN;
        
        for (Account account : judges) {
            assertHasPermssion(expectedPerm, account);
        }

    }
}
