package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * Test Filter class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$ 
 */

// $HeadURL$
public class FilterTest extends TestCase {
    
    // TODO test all other filter types (run, clar states, etc.)

    public void testProblemFilter() {
        Filter filter = new Filter();

        assertFalse("Problem Filter should be disabled ", filter.isFilteringProblems());
        
        filter.setUsingProblemFilter(true);
        assertTrue ("Problem Filter should be enabled ", filter.isFilteringProblems());
        
        filter.setUsingProblemFilter(false);
        assertFalse("Problem Filter should be disabled ", filter.isFilteringProblems());
        
        
        String [] problemTitles = {"Problem A", "Problem B", "Problem C" };
        
        Problem [] problems = new Problem[problemTitles.length];
        
        int i = 0;
        for (String name : problemTitles){
            problems[i] = new Problem(name);
            i++;
        }
        
        Problem notInFilterProblem = problems[2];
        Problem problemInFilter = problems[1];
        
        filter.addProblem(problemInFilter);
        assertTrue ("Problem Filter should be enabled ", filter.isFilteringProblems());
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matchesProblem(problemInFilter));

        filter.addProblem(problems[0]);
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matchesProblem(problemInFilter));
        assertFalse("Problem "+notInFilterProblem+" should not be found in filter ", filter.matchesProblem(notInFilterProblem));
        
        filter.removeProblem(problemInFilter);
        assertFalse("Problem "+problemInFilter+" should not be found in filter ", filter.matchesProblem(problemInFilter));

        filter.addProblem(problemInFilter);
        filter.addProblem(problemInFilter);
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matchesProblem(problemInFilter));
        
    }

    public void testProblemFilterList() {
        
        Filter filter = new Filter();

        String [] problemTitles = {"Problem A", "Problem B", "Problem C" };
        
        Problem [] problems = new Problem[problemTitles.length];
        
        int i = 0;
        for (String name : problemTitles){
            problems[i] = new Problem(name);
            i++;
        }
        
        ElementId [] elementIds = filter.getProblemIdList();
        
        assertTrue ("No elements ids for problems ", elementIds.length == 0);
        
        for (Problem problem : problems){
            filter.addProblem(problem);
        }
        
        elementIds = filter.getProblemIdList();
        
//        for (ElementId elementId : elementIds){
//            System.out.println("Problem in filter: "+elementId);
//        }

        assertTrue ("Should be "+problems.length+" in list found "+elementIds.length, elementIds.length == problems.length);
        
    }
}
