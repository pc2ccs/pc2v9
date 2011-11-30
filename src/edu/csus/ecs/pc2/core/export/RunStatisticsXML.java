package edu.csus.ecs.pc2.core.export;

import java.io.IOException;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.exports.ccs.EventFeedXML;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunStatisticsXML {

    public static final String ROOT_TAG = "run_statistics";

    /**
     * Return XML for run statistics.
     * 
     * @param contest
     * @param filter filter to match runs
     * @return XML String for problems, languages and runs.
     * @throws IOException 
     */
    public String toXML(IInternalContest contest, Filter filter) throws IOException {

        EventFeedXML eventFeedXML = new EventFeedXML();

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(ROOT_TAG);

        int idx = 1;
        for (Language language : contest.getLanguages()) {
            if (filter.matches(language)) {
                IMemento memento = mementoRoot.createChild(EventFeedXML.LANGUAGE_TAG);
                eventFeedXML.addMemento(memento, contest, language, idx);
            }
            eventFeedXML.addMemento(mementoRoot, contest, language, idx);
            idx++;
        }

        idx = 1;
        for (Problem problem : contest.getProblems()) {
            if (filter.matches(problem)) {
                IMemento memento = mementoRoot.createChild(EventFeedXML.PROBLEM_TAG);
                eventFeedXML.addMemento(memento, contest, problem, idx);
            }
            idx++;
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                IMemento memento = mementoRoot.createChild(EventFeedXML.RUN_TAG);
                addMemento(memento, contest, run);
            }
        }

        return mementoRoot.saveToString();
    }

    /**
     * For the input number, returns an uppercase letter.
     * 
     * 1 = A, 2 = B, etc.
     * 
     * @param id
     *            problem number, based at one.
     * @return single upper case letter.
     */
    protected String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    /**
     * Return the problem index (starting at/base one)).
     * 
     * @param contest
     * @param inProblem
     * @return one based number for problem.
     */
    private int getProblemIndex(IInternalContest contest, Problem inProblem) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.getElementId().equals(inProblem.getElementId())) {
                return idx + 1;
            }
            idx++;
        }

        return -1;
    }

    protected IMemento addMemento(IMemento memento, IInternalContest contest, Run run) {

        /**
         * In a newer version of the Event Feed wiki page the run element was simplified and lost a number of useful tags: solved and judged.
         */

        memento.putInteger("id", run.getNumber());
        memento.putInteger("team-id", run.getSubmitter().getClientNumber());
        Problem problem = contest.getProblem(run.getProblemId());
        int problemIndex = getProblemIndex(contest, problem);
        memento.putInteger("problem-id", problemIndex);

        memento.putString("problem-name", problem.getDisplayName());
        Language language = contest.getLanguage(run.getLanguageId());
        XMLUtilities.addChild(memento, "language", language.getDisplayName());

        if (run.isJudged()) {
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            String judgement = contest.getJudgement(judgementId).getAcronym();
            XMLUtilities.addChild(memento, "judgement", judgement.toUpperCase().substring(0, 2));
            XMLUtilities.addChild(memento, "result", judgement.toUpperCase().substring(0, 2));
            XMLUtilities.addChild(memento, "solved", run.isSolved());
            XMLUtilities.addChild(memento, "execute-time-ms", run.getJudgementRecord().getExecuteMS());
        }

        XMLUtilities.addChild(memento, "elapsed-Mins", run.getElapsedMins());
        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());

        return memento;
    }

}
