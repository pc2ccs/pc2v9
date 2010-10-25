package edu.csus.ecs.pc2.core.archive;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * Tests for PacketFormatter.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketFormatterTest extends TestCase {
    
    public void testGetAccountBreakdownTest() {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(2, 2, 22, 12, true);

        String s = PacketFormatter.getAccountBreakdown(contest.getAccounts());
        
        assertEquals("22 TEAM 12 JUDGE", s);
    }
    
    public void testGetAccountBreakdownTestBySite() {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(2, 5, 22, 12, true);
        
        contest.setSiteNumber(1);
        contest.generateNewAccounts(Type.TEAM.toString(), 10, true);
        contest.generateNewAccounts(Type.SCOREBOARD.toString(), 1, true);
        
        contest.setSiteNumber(4);
        contest.generateNewAccounts(Type.TEAM.toString(), 40, true);
        contest.generateNewAccounts(Type.SCOREBOARD.toString(), 4, true);
        
        String [] results = {
                "10 TEAM 1 SCOREBOARD", //
                "22 TEAM 12 JUDGE", //
                "no accounts", //
                "40 TEAM 4 SCOREBOARD", //
                "no accounts",
        };

        for (Site site : contest.getSites()) {
            String s = PacketFormatter.getAccountBreakdown(contest.getAccounts(), site.getSiteNumber());
            int resultIdx = site.getSiteNumber() - 1;
            assertEquals("Breakdown for site " + site.getSiteNumber(), results[resultIdx], s);
        }
        
        int count = PacketFormatter.getNumberOfSites(contest.getAccounts());
        
        assertEquals ("Sites in accounts ", 4, count);
        assertEquals ("Sites defined in contest ", 5, contest.getSites().length);
    }
    
}
