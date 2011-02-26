package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Event Feed XML.
 * 
 * Class used <li>to CCS Standard Event Feed output XML based on contest data.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXML {

    // TODO move EventFeedXML to package pc2.core ?

    private static final String CONTEST_TAG = "contest";

    private static final String INFO_TAG = "info";

    private static final String PROBLEM_TAG = "problem";

    private static final String LANGUAGE_TAG = "language";

    private static final String TEAM_TAG = "team";

    private static final String CLARIFICATION_TAG = "clarification";

    private static final String RUN_TAG = "run";

    private static final String JUDGEMENT_TAG = "judgement";

    private static final String FINALIZE_TAG = "finalize";

    private static final String JUDGEMENT_RECORD_TAG = "judgement_record";

    public String toXML(IInternalContest contest) throws IOException {
        return toXML(contest, new Filter());
    }

    public String toXML(IInternalContest contest, Filter filter) throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(CONTEST_TAG);

        IMemento memento = mementoRoot.createChild(INFO_TAG);
        addInfoMemento(memento, contest, filter);
        

        Judgement [] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            memento = mementoRoot.createChild(JUDGEMENT_TAG);
            addMemento(memento, contest, judgement);
        }
        

        memento = mementoRoot.createChild(LANGUAGE_TAG);
        Language[] languages = contest.getLanguages();
        int num = 1;
        for (Language language : languages) {
            if (filter.matches(language)) {
                addMemento(memento, contest, language, num);
            }
            num++;
        }

        memento = mementoRoot.createChild(PROBLEM_TAG);
        num = 1;
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                addMemento(memento, contest, problem, num);
            }
            num++;
        }

        Vector<Account> teams = contest.getAccounts(Type.TEAM);

        Account[] accounts = (Account[]) teams.toArray(new Account[teams.size()]);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            if (filter.matches(account)) {
                memento = mementoRoot.createChild(TEAM_TAG);
                addMemento(memento, contest, account);
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                memento = mementoRoot.createChild(RUN_TAG);
                addMemento(memento, contest, run);
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            if (filter.matches(clarification)) {
                memento = mementoRoot.createChild(CLARIFICATION_TAG);
                addMemento(memento, contest, clarification);
            }
        }

        return mementoRoot.saveToString();
    }

    /**
     * create info XML element
     * 
     * @param contest
     * @param filter
     * @return
     * @throws IOException
     */
    public XMLMemento createInfoElement(IInternalContest contest, Filter filter) throws IOException {
        XMLMemento memento = XMLMemento.createWriteRoot(INFO_TAG);
        addInfoMemento(memento, contest, filter);
        return memento;
    }

    public IMemento addInfoMemento(IMemento memento, IInternalContest contest, Filter filter) throws IOException {
        ContestTime time = contest.getContestTime();

        addChild(memento, "title", contest.getContestInformation().getContestTitle());
        addChild(memento, "length", time.getContestLengthStr());

        addChild(memento, "penalty", DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO));
        addChild(memento, "started", time.isContestRunning());

        String formattedSeconds = "0.0";
        if (time.getContestStartTime() != null) {
            formattedSeconds = formatSeconds(time.getContestStartTime().getTimeInMillis());
        }
        addChild(memento, "started", formattedSeconds);
        return memento;
    }

    private IMemento addChild(IMemento mementoRoot, String name, boolean value) {
        return addChild(mementoRoot, name, Boolean.toString(value));
    }

    private IMemento addChild(IMemento mementoRoot, String name, long value) {
        return addChild(mementoRoot, name, Long.toString(value));
    }

    private IMemento addChild(IMemento mementoRoot, String name, String value) {
        XMLMemento memento = (XMLMemento) mementoRoot.createChildNode(name, value);
        return memento;
    }

    private String formatSeconds(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        long fraction = timeInMillis % 1000;
        return seconds + "." + fraction;
    }

    @SuppressWarnings("unused")
    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public XMLMemento createElement(IInternalContest contest, Filter filter) throws IOException {
        XMLMemento memento = XMLMemento.createWriteRoot(LANGUAGE_TAG);
        addInfoMemento(memento, contest, filter);
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Language language, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(LANGUAGE_TAG);
        addMemento(memento, contest, language, id);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Language language, int id) {

        // <language>
        // <name>C++</name>
        // </language>

        memento.putInteger("id", id);
        addChild(memento, "name", language.toString());
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Problem problem, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(PROBLEM_TAG);
        addMemento(memento, contest, problem, id);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Problem problem, int id) {

        // <problem id="1" state="enabled">
        // <label>A</label>
        // <name>APL Lives!</name>
        // <balloon-color rgb="#ffff00">yellow</balloon-color>
        // </problem>

        memento.putInteger("id", id);
        memento.putBoolean("enabled", problem.isActive());

        char let = 'A';
        let += (id - 1);

        memento.createChildNode("label", "" + let);
        memento.createChildNode("name", problem.toString());
        IMemento balloonColor = memento.createChildNode("name", "TODO: color"); // TODO color name
        balloonColor.putString("rgb", "TODO: rgb color"); // TODO RGB color

        return memento;
    }
    



    public XMLMemento createElement(IInternalContest contest, Account account) {
        XMLMemento memento = XMLMemento.createWriteRoot(TEAM_TAG);
        addMemento(memento, contest, account);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Account account) {

        // <team id="1" external-id="23412">
        // <name>American University of Beirut</name>
        // <nationality>LBN</nationality>
        // <university>American University of Beirut</university>
        // <region>Europe</region>
        // </team>

        memento.putInteger("id", account.getClientId().getClientNumber());
        memento.putString("external-id", account.getExternalId());

        addChild(memento, "name", account.getDisplayName());
        addChild(memento, "nationality", "TODO:"); // TODO need to add Account.getNationality();

        String regionName = "";
        if (account.getGroupId() != null) {
            Group group = contest.getGroup(account.getGroupId());
            regionName = group.getDisplayName();
        }

        addChild(memento, "region", regionName);
        return memento;
    }
    
//    TODO Add Test Case
//    
//    public XMLMemento createElement(IInternalContest contest, Testcase testcase) {
//        XMLMemento memento = XMLMemento.createWriteRoot(CLARIFICATION_TAG);
//        addMemento(memento, contest, testcase);
//        return memento;
//    }

    public XMLMemento createElement(IInternalContest contest, Clarification clarification) {
        XMLMemento memento = XMLMemento.createWriteRoot(CLARIFICATION_TAG);
        addMemento(memento, contest, clarification);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Clarification clarification) {

        // <clar id="1" team-id="0" problem-id="1">
        // <answer>The number of pieces will fit in a signed 32-bit integer.
        // </answer>
        // <question>What is the upper limit on the number of pieces of chocolate
        // requested by the friends?</question>
        // <to-all>true</to-all>
        // <contest-time>118.48</contest-time>
        // <timestamp>1265335256.74</timestamp>
        // </clar>

        memento.putInteger("id", clarification.getNumber());
        memento.putInteger("team-id", clarification.getNumber());

        Problem problem = contest.getProblem(clarification.getProblemId());
        memento.putInteger("problem-id", getProblemIndex(contest, problem));

        String answer = clarification.getAnswer();
        if (answer == null) {
            answer = "";
        }
        addChild(memento, "answer", answer);
        addChild(memento, "question", clarification.getQuestion());
        addChild(memento, "to-all", clarification.isSendToAll());
        addChild(memento, "contest-time", formatSeconds(clarification.getElapsedMins() * 1000));
        addChild(memento, "timestamp", getTimeStamp());
        return memento;
    }

    private int getProblemIndex(IInternalContest contest, Problem inProblem) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.getElementId().equals(inProblem.getElementId())) {
                return idx;
            }
            idx++;
        }

        return -1;
    }

    public XMLMemento createElement(IInternalContest contest, Run run) {
        XMLMemento memento = XMLMemento.createWriteRoot(RUN_TAG);
        addMemento(memento, contest, run);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Run run) {
        // <run time="1265353100290">
        // <id>1410</id>
        // <judged>True</judged>
        // <language>C++</language>
        // <penalty>True</penalty>
        // <problem>4</problem>
        // <result>WA</result>
        // <solved>False</solved>
        // <team>74</team>
        // <time>17960.749403</time>
        // <timestamp>1265353100.29</timestamp>
        // </run>

        memento.putInteger("id", run.getNumber());

        addChild(memento, "judged", run.isJudged());

        Language language = contest.getLanguage(run.getLanguageId());
        addChild(memento, "language", language.getDisplayName());

        // TODO What is penalty ??
        addChild(memento, "penalty", "TODO");

        Problem problem = contest.getProblem(run.getProblemId());
        addChild(memento, "problem", problem.getDisplayName());

        // TODO replace this with the acronym Judgement.getAcronym();
        // String judgement = contest.getJudgement(run.getJudgementRecord().getJudgementId()).getAcronym();
        String judgement = contest.getJudgement(run.getJudgementRecord().getJudgementId()).toString();

        addChild(memento, "result", judgement.toUpperCase().substring(0, 2));

        addChild(memento, "solved", run.isSolved());
        addChild(memento, "team", run.getSubmitter().getClientNumber());
        addChild(memento, "timestamp", getTimeStamp());

        return memento;
    }

    /**
     * Return the current time in seconds, with millis mattisa.
     * 
     * @return
     */
    public String getTimeStamp() {
        return formatSeconds(System.currentTimeMillis());
    }

    /**
     * @throws IOException
     */
    private String toXML(XMLMemento mementoRoot) throws IOException {
        return mementoRoot.saveToString(true);
    }

    /**
     * Starts contest XML and adds all configuration data/values.
     * 
     * @param contest
     * @param judgement
     * @return
     */
    public String createStartupXML(IInternalContest contest) {

        StringBuffer sb = new StringBuffer("<" + CONTEST_TAG + ">");

        /**
         * A general implementation for logging errors needs to be established. Perhaps something with log4j ? The goal should be to standardized logging in such a way that Exceptions are not lost.
         */

        try {
            sb.append(toXML(createInfoElement(contest, null)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int idx;

        idx = 1;
        for (Language language : contest.getLanguages()) {
            try {
                sb.append(toXML(createElement(contest, language, idx)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            idx++;
        }

        idx = 0;
        for (Problem problem : contest.getProblems()) {
            try {
                sb.append(toXML(createElement(contest, problem, idx)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            idx++;
        }

        for (Judgement judgement : contest.getJudgements()) {
            try {
                sb.append(toXML(createElement(contest, judgement)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        for (Clarification clarification : clarifications) {
            try {
                sb.append(toXML(createElement(contest, clarification)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            try {
                sb.append(toXML(createElement(contest, run)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    // TODO add FinalData class
    
//    public IMemento addMemento(IMemento memento, IInternalContest contest, FinalData finalData) {
//        
//    }
        
    public String createFinalizeXML(IInternalContest contest) {

        StringBuffer sb = new StringBuffer();

        XMLMemento memento = XMLMemento.createWriteRoot(FINALIZE_TAG);
        
//        addMemento (memento, contest, finalData);  // TODO add Finalize data

        try {
            sb.append(toXML(memento));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sb.append("</");
        sb.append(CONTEST_TAG);
        sb.append(">");
        return sb.toString();
    }

    public XMLMemento createElement(IInternalContest contest, Judgement judgement) {
        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_TAG);
        addMemento(memento, contest, judgement);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Judgement judgement) {
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>
        String name = judgement.getDisplayName();

        addChild(memento, "acronym", "TODO:"); // TODO Need to add Judgement.getAcronym();
        addChild(memento, "name", name);
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, JudgementRecord judgementRecord) {
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>

        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_RECORD_TAG);
        String name = contest.getJudgement(judgementRecord.getJudgementId()).getDisplayName();

        addChild(memento, "acronym", "TODO:"); // TODO Need to add Judgement.getAcronym();
        addChild(memento, "name", name);
        return memento;
    }
}
