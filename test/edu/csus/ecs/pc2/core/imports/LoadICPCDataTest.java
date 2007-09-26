package edu.csus.ecs.pc2.core.imports;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author PC2
 *
 */
public class LoadICPCDataTest extends TestCase {

    private String loadDir = "testdata"+File.separator;
    private LoadICPCData importer = new LoadICPCData();
    public LoadICPCDataTest() {
        super();
    }

    public LoadICPCDataTest(String arg0) {
        super(arg0);
    }
    protected void setUp() throws Exception {
        // TODO update loadDir to find the testdata
    }

    public void testOne() {
        try {
            ICPCImportData importData = importer.fromDirectory(loadDir+"icpcimport1", null, null);
            assertEquals("contestTitle", "The 2004 ACM Pacific Northwest Programming Contest", importData.getContestTitle());
            ICPCAccount account = importData.getAccounts()[1];
            assertEquals("2nd account short school", "SFU", account.getShortSchoolName());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }
    public void testTwo() {
        try {
            ICPCImportData importData = importer.fromDirectory(loadDir+"icpcimport2", null, null);
            assertEquals("contestTitle", "The 2004 ACM Pacific Northwest Programming Contest", importData.getContestTitle());
            ICPCAccount account = importData.getAccounts()[1];
            assertEquals("2nd account number", 2, account.getAccountNumber());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("exception", false);
        }
    }
}
