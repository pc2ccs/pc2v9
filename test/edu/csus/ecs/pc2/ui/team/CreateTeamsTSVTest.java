package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu> F
 */
public class CreateTeamsTSVTest extends AbstractTestCase {

    /**
     * Test loadAccounts
     * 
     * @throws Exception
     */
    public void testloadAccounts() throws Exception {

        String dataDir = getDataDirectory(this.getName());
        String name = dataDir + File.separator + "teams.json";

//        ensureDirectory(dataDir);
//        editFile(name);

        String[] args = new String[0];
        CreateTeamsTSV creator = new CreateTeamsTSV(args);

        List<TeamAccount> list = creator.loadAccounts(name);

        assertNotNull(list);
        assertEquals("Expected teams", 171, list.size());

    }

}
