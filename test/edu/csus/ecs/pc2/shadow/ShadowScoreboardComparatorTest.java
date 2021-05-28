// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class ShadowScoreboardComparatorTest extends AbstractTestCase {
    
    //test that two identical scoreboards show as "matching"
    public void testScoreboardComparatorMatch () throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
        
        Log log = new Log(outputDirectoryName, getName()+".log");
        StaticLog.setLog(log);
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        
        IInternalController controller = sampleContest.createController(contest, true, false);

        ShadowController shadowController = new ShadowController(contest, controller);
        ShadowScoreboardComparator comparator = new ShadowScoreboardComparator(shadowController);
        
        String pc2Json = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
                + "\"time\":null,\"contest_time\":\"\","
                + "\"state\":{\"started\":\"\",\"ended\":\"\",\"frozen\":\"\",\"thawed\":\"\",\"finalized\":\"\",\"end_of_updates\":\"\"},"
                + "\"rows\":["
                +   "{\"rank\":1,"
                +      "\"team_id\":3,"
                +      "\"score\":{\"total_time\":160,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":2,"
                +      "\"team_id\":5,"
                +      "\"score\":{\"total_time\":170,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":1,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":2,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":5,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":5,"
                +      "\"team_id\":4,"
                +      "\"score\":{\"total_time\":230,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]}"
                +    "]"
                + "}" ; 

        String remoteJson = pc2Json ;
                
        ShadowScoreboardRowComparison [] results = comparator.compare(pc2Json, remoteJson);
        
        for (ShadowScoreboardRowComparison row : results) {
            
            assertTrue("Shadow scoreboard comparator failed", row.isMatch());
        }
        
    }
    
    //test that two scoreboards differing in rank assignments show as "non-matching"
    public void testScoreboardComparatorRankMismatch () throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
        
        Log log = new Log(outputDirectoryName, getName()+".log");
        StaticLog.setLog(log);
            
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        
        IInternalController controller = sampleContest.createController(contest, true, false);

        ShadowController shadowController = new ShadowController(contest, controller);
  
        ShadowScoreboardComparator comparator = new ShadowScoreboardComparator(shadowController);
        
        String pc2Json = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
                + "\"time\":null,\"contest_time\":\"\","
                + "\"state\":{\"started\":\"\",\"ended\":\"\",\"frozen\":\"\",\"thawed\":\"\",\"finalized\":\"\",\"end_of_updates\":\"\"},"
                + "\"rows\":["
                +   "{\"rank\":1,"
                +      "\"team_id\":3,"
                +      "\"score\":{\"total_time\":160,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":2,"
                +      "\"team_id\":5,"
                +      "\"score\":{\"total_time\":170,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":1,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":2,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":5,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":5,"
                +      "\"team_id\":4,"
                +      "\"score\":{\"total_time\":230,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]}"
                +    "]"
                + "}" ; 

        String remoteJson = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
                + "\"time\":null,\"contest_time\":\"\","
                + "\"state\":{\"started\":\"\",\"ended\":\"\",\"frozen\":\"\",\"thawed\":\"\",\"finalized\":\"\",\"end_of_updates\":\"\"},"
                + "\"rows\":["
                +   "{\"rank\":1,"
                +      "\"team_id\":3,"
                +      "\"score\":{\"total_time\":160,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":1,"
                +      "\"team_id\":5,"
                +      "\"score\":{\"total_time\":170,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":1,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":2,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":5,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":5,"
                +      "\"team_id\":4,"
                +      "\"score\":{\"total_time\":230,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]}"
                +    "]"
                + "}" ; 

                
        ShadowScoreboardRowComparison [] results = comparator.compare(pc2Json, remoteJson);
        
        for (int entry=1; entry<=results.length; entry++) {
            
            ShadowScoreboardRowComparison nextRow = results[entry-1];
            
            //debug:
//            System.out.println("Comparison for entry " + entry + ": " + nextRow.isMatch());
            
            if (entry!=2 && entry!=3) {
                assertTrue("Shadow scoreboard comparator failed on row " + entry, nextRow.isMatch());
            } else {
                assertFalse("Shadow scoreboard comparator failed", nextRow.isMatch());
            }
        }
        
    }
    
    //test that two scoreboards with different numbers of rank rows show as "non-matching"
    public void testScoreboardComparatorRankRowCountMismatch () throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
        
        Log log = new Log(outputDirectoryName, getName()+".log");
        StaticLog.setLog(log);
                        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        
        IInternalController controller = sampleContest.createController(contest, true, false);

        ShadowController shadowController = new ShadowController(contest, controller);
        ShadowScoreboardComparator comparator = new ShadowScoreboardComparator(shadowController);
        
        String pc2Json = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
                + "\"time\":null,\"contest_time\":\"\","
                + "\"state\":{\"started\":\"\",\"ended\":\"\",\"frozen\":\"\",\"thawed\":\"\",\"finalized\":\"\",\"end_of_updates\":\"\"},"
                + "\"rows\":["
                +   "{\"rank\":1,"
                +      "\"team_id\":3,"
                +      "\"score\":{\"total_time\":160,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":2,"
                +      "\"team_id\":5,"
                +      "\"score\":{\"total_time\":170,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":1,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":2,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":5,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":5,"
                +      "\"team_id\":4,"
                +      "\"score\":{\"total_time\":230,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]}"
                +    "]"
                + "}" ; 

        String remoteJson = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
                + "\"time\":null,\"contest_time\":\"\","
                + "\"state\":{\"started\":\"\",\"ended\":\"\",\"frozen\":\"\",\"thawed\":\"\",\"finalized\":\"\",\"end_of_updates\":\"\"},"
                + "\"rows\":["
                +   "{\"rank\":1,"
                +      "\"team_id\":3,"
                +      "\"score\":{\"total_time\":160,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":2,"
                +      "\"team_id\":5,"
                +      "\"score\":{\"total_time\":170,\"num_solved\":2},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":3,"
                +      "\"team_id\":1,"
                +      "\"score\":{\"total_time\":23,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":2,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]},"
                +    "{\"rank\":5,"
                +      "\"team_id\":4,"
                +      "\"score\":{\"total_time\":230,\"num_solved\":1},"
                +      "\"problems\":["
                +          "{\"solved\":true,\"num_judged\":1,\"time\":0,\"problem_id\":\"Id = Sumit-3038852649638511321\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Quadrangles-5257258348192707262\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Routing-1293340679224404663\",\"num_pending\":0},"
                +          "{\"solved\":false,\"num_judged\":0,\"time\":0,\"problem_id\":\"Id = Faulty_Towers-8550461639231669238\",\"num_pending\":0}"
                +          "]}"
                +    "]"
                + "}" ; 

                
        ShadowScoreboardRowComparison [] results = comparator.compare(pc2Json, remoteJson);
        
        for (int entry=1; entry<=results.length; entry++) {
            
            ShadowScoreboardRowComparison nextRow = results[entry-1];
            
            //debug:
//            System.out.println("Comparison for entry " + entry + ": " + nextRow.isMatch());
            
            if (entry==4) {
                assertFalse("Shadow scoreboard comparator failed on row " + entry, nextRow.isMatch());
            } else {
                assertTrue("Shadow scoreboard comparator failed", nextRow.isMatch());
            }
        }
        
    }
    
    //test that malformed JSON returns an empty array
    public void testScoreboardComparatorWithBadJSON () throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
        
        Log log = new Log(outputDirectoryName, getName()+".log");
        StaticLog.setLog(log);
                
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        
        IInternalController controller = sampleContest.createController(contest, true, false);

        ShadowController shadowController = new ShadowController(contest, controller);

        ShadowScoreboardComparator comparator = new ShadowScoreboardComparator(shadowController);
        
        String pc2Json = 
                "{\"event_id\":\"5a97a37f-fb6a-4823-99bc-a4ecca321dc2\","
             +    "]"
                + "}" ; 

        String remoteJson = pc2Json ;
                
        ShadowScoreboardRowComparison [] results = comparator.compare(pc2Json, remoteJson);
            
        assertTrue("Shadow scoreboard comparator failed to return empty array when given bad JSON input", results.length==0);
        
    }

}


