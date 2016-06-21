package edu.csus.ecs.pc2.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class EFLoader {

    public List<EventFeedRun> loadFile (String filename) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        
        List<EventFeedRun> runs = EventFeedRun.loadFile(filename);
        
        return runs;
    }
    

    /**
     * Counts number of runs that have submissions.
     * 
     * 
     * @param runs
     * @param submissionsBasePath
     * @return
     */
    public int countRunsWithSubmissions(List<EventFeedRun> runs, String submissionsBasePath) {
        
        int foundCount = 0;
        
        for (EventFeedRun run : runs) {
            
            String dirname = submissionsBasePath + File.separator + run.getId();
            File dir = new File(dirname);
            if (dir.isDirectory()){
                
                String[] entries = dir.list();
                if (entries.length > 0){
                    foundCount ++;
                }
            }
        }
        
        return foundCount ;
    }

    public List<String> findAnyErrors(IInternalContest contest, String cdpPath, List<EventFeedRun> runs) {
        
        List<String> errors = new ArrayList<>();
        String submissionsBasePath = cdpPath + File.separator +  IContestLoader.SUBMISSIONS_DIRNAME;
        
        for (EventFeedRun run : runs) {
            
            List<String> files = EventFeedUtilities.fetchRunFileNames(submissionsBasePath, run.getId());
            
            if (files == null || files.size() < 1){
                errors.add("Missing submitted files for run "+run.getId());
            }
        }
        
        int maxProblemNumber = EventFeedUtilities.getMaxProblem(runs);
        Problem[] problems = contest.getProblems();
        
        // TODO check contest for enough problems

        if (problems.length < maxProblemNumber){
            errors.add("EF has "+maxProblemNumber+" contest has "+problems.length+" need "+(maxProblemNumber-problems.length)+" more problems");
        }
        
//        String[] langs = EventFeedUtilities.getAllLanguages(runs);
//        Language[] languages = contest.getLanguages();
         // TODO check contest for enough languages
         
//        int maxTeamNumber = EventFeedUtilities.getMaxTeam(runs);
         // TODO check contest for enough teams
         
        return errors;
    }



     int getLanguageCount(List<EventFeedRun> runs) {
        // TODO Auto-generated method stub
        return 0;
    }

     int getEFProblemCount(List<EventFeedRun> runs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
