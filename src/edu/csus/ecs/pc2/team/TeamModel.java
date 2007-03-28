package edu.csus.ecs.pc2.team;

import java.util.Vector;

import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.LanguageList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.model.AccountListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunListener;
import edu.csus.ecs.pc2.core.model.SubmittedRun;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;

/**
 * Represents the collection of contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamModel implements  IModel {

    public static final String SVN_ID = "$Id$";

    private Vector<RunListener> runListenterList = new Vector<RunListener>();

    private Vector<SubmittedRun> runList = new Vector<SubmittedRun>();
    
    /**
     * List of all defined problems. Contains deleted problems too.
     */
    private ProblemList problemList = new ProblemList();

    /**
     * List of all problems displayed to users, in order. Does not contain deleted problems.
     */
    private ProblemDisplayList problemDisplayList = new ProblemDisplayList();

    /**
     * List of all languages. Contains deleted problems too.
     */
    private LanguageList languageList = new LanguageList();

    /**
     * List of all displayed languages, in order. Does not contain deleted languages.
     */
    private LanguageDisplayList languageDisplayList = new LanguageDisplayList();


    public TeamModel() {
        
        initialize();
    }
    

    /**
     * Initialize Model with data.
     * 
     */
    private void initialize() {

        String[] probNames = { "Sum of Squares", "Sumit", "Hello", "GoodBye" };
        Problem problem = new Problem("None Selected");

        problemDisplayList.add(problem);
        problemList.add(problem);

        for (String problemNames : probNames) {
            problem = new Problem(problemNames);
            problemDisplayList.add(problem);
            problemList.add(problem);
        }

        String[] langNames = { "Java", "BASIC", "C++", "ANSI C", "APL" };
        Language language = new Language("None Selected");

        languageDisplayList.add(language);
        languageList.add(language);

        for (String languageName : langNames) {
            language = new Language(languageName);
            languageList.add(language);
            languageDisplayList.add(language);
        }
    }


    public void addRunListener(RunListener runListener) {
        runListenterList.addElement(runListener);

    }

    public void removeRunListener(RunListener runListener) {
        runListenterList.removeElement(runListener);
    }

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenterList.size(); i++) {

            if (runEvent.getAction() == Action.ADDED) {
                runListenterList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == Action.DELETED) {
                runListenterList.elementAt(i).runRemoved(runEvent);
            } else {
                runListenterList.elementAt(i).runChanged(runEvent);
            }
        }
    }

    public void addRun(SubmittedRun submittedRun) {
        runList.addElement(submittedRun);
        RunEvent runEvent = new RunEvent (Action.ADDED, submittedRun);
        fireRunListener(runEvent);
    }

    public SubmittedRun acceptRun(SubmittedRun submittedRun) throws Exception {
        throw new Exception("who cares");
    }

    public void generateNewAccounts(String clientTypeName, int count, boolean active) {
        // TODO Is there a need for this setting on the team ??
        
    }

    public void addAccountListener(AccountListener accountListener) {
        // TODO Auto-generated method stub
        
    }

    public void removeAccountListener(AccountListener accountListener) {
        // TODO Auto-generated method stub
        
    }
    

    public Problem[] getProblems() {
        return problemDisplayList.getList();
    }

    public Language[] getLanguages() {
        return languageDisplayList.getList();
    }
}
