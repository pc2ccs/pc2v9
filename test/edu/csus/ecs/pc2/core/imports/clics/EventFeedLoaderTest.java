// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.TeamAccountComparator;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class EventFeedLoaderTest extends AbstractTestCase {

    /**
     * Test loading TeamAccount from CLICS event feed JSON.
     * 
     * @throws Exception
     */
    public void testgetTeams() throws Exception {

//        startExplorer(getDataDirectory());

        String file = getDataDirectory() + File.separator + "event-feed-teams.json";
        assertFileExists(file);

//        editFile(file);

        String[] lines = Utilities.loadFile(file);
        List<TeamAccount> list = EventFeedLine.getTeams(lines);
        
        assertNotNull(list);
        assertEquals(155, list.size());
        
        Collections.sort(list, new TeamAccountComparator());

        compareTeamAccount(list.get(0), "1", "1001", "D1 team1", "991001");
        compareTeamAccount(list.get(22), "152", "661486", "D2 Code to Joy (Trinity Western)", "99661486");
        compareTeamAccount(list.get(list.size() - 1), "557", "663782", "D2 Jimmy Stewart's Christmas (BYU Hawaii)", "99663782");
    }
    
    public void testgetTeamsTwo() throws Exception {
        
//        startExplorer(getDataDirectory());
        
        //  C:\contests\2022\2022NAC\practice4\feed\work
        String file = getDataDirectory() + File.separator + "partial-event-feed-0519-1231.json";
        
//      editFile(file);

      String[] lines = Utilities.loadFile(file);
      List<TeamAccount> list = EventFeedLine.getTeams(lines);
      
      assertNotNull(list);
      assertEquals(50, list.size());
      
      Collections.sort(list, new TeamAccountComparator());
        
    }

    private void compareTeamAccount(TeamAccount teamAccount, String id, String icpc_id, String display_name, String organization_id) {

//        System.out.println("\"" + teamAccount.getId() + "\",\"" + teamAccount.getIcpc_id() + "\",\"" + teamAccount.getDisplay_name() + "\",\"" + teamAccount.getOrganization_id() + "\"");

        assertEquals("id", id, teamAccount.getId());
        assertEquals("icpc_id", icpc_id, teamAccount.getIcpc_id());
        assertEquals("display_name", display_name, teamAccount.getDisplay_name());
        assertEquals("organization_id", organization_id, teamAccount.getOrganization_id());
    }

    protected void dumpMap(String string, Map<String, Object> map) {

        map.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " type: " + entry.getValue().getClass().getName() + " = " + entry.getValue());
        });

    }

}
