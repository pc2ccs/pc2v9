package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission;
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
    
    // TODO move this to package pc2.core ?

    public static final String CONTEST_TAG = "contest";

    public static final String SETTINGS_TAG = "settings";

    public static final String INFO_TAG = "info";

    public static final String PROBLEM_TAG = "problem";

    public static final String LANGUAGE_TAG = "language";

    public static final String ACCOUNT_TAG = "account";

    public static final String CLARIFICATION_TAG = "clarification";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";
    
    public static final String FINALIZE_TAG = "finalize";

    public static final String JUDGEMENT_RECORD_TAG = "judgement_record";

    public static final String FILTER_TAG = "filter";

    public static final String PROFILES_TAG = "profiles";

    public static final String BALLOON_COLORS_LIST_TAG = "ballonlist";

    public static final String BALLOON_COLOR_TAG = "ballooncolors";

    public static final String SITES_TAG = "site";

    public static final String VERSION_TAG = "version";

    public static final String FILE_INFO_TAG = "fileinfo";

    private boolean showPasswords = true;

    public String toXML(IInternalContest contest) throws IOException {
        return toXML(contest, new Filter());
    }

    public String toXML(IInternalContest contest, Filter filter) throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(CONTEST_TAG);

        IMemento parent = mementoRoot.createChild(SETTINGS_TAG);
        ContestInformation contestInformation = contest.getContestInformation();
        addContestInfo(parent, contestInformation);

        Language[] languages = contest.getLanguages();
        int num = 1;
        for (Language language : languages) {
            if (filter.matches(language)) {
                addLanguageMemento(mementoRoot, num, language);
            }
            num++;
        }

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                addProblemMemento(mementoRoot, problem);
            }
        }

        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            if (filter.matches(account)) {
                addAccountMemento(mementoRoot, contest, account);
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                addRunMemento(mementoRoot, contest, run);
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            if (filter.matches(clarification)) {
                addClarificationMemento(mementoRoot, contest, clarification);
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
        XMLMemento mementoRoot = XMLMemento.createWriteRoot(INFO_TAG);
        addInfo(mementoRoot, contest);
        return mementoRoot;
    }

    /**
     * Add Event Feed info tag info.
     * 
     * @param mementoRoot
     * @param contest
     * @return
     */
    protected IMemento addInfo(XMLMemento mementoRoot, IInternalContest contest) {

        IMemento memento = mementoRoot.createChild(INFO_TAG);

        ContestTime time = contest.getContestTime();

        addChild (memento, "title", contest.getContestInformation().getContestTitle());
        addChild (memento,"length", time.getContestLengthStr());
        
        addChild(memento, "penalty", DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO));
        addChild(memento, "started", time.isContestRunning());

        String formattedSeconds = "0.0";
        if (time.getContestStartTime() != null) {
            formattedSeconds = formatSeconds(time.getContestStartTime().getTimeInMillis());
        }
        addChild (memento,"started", formattedSeconds);
        return memento;
    }

    private IMemento addChild(IMemento mementoRoot, String name, boolean value) {
        return addChild(mementoRoot, name, Boolean.toString(value));
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

    public IMemento addContestInfo(IMemento memento, ContestInformation contestInformation) {
        memento.putString("title", contestInformation.getContestTitle());
        memento.putString("url", contestInformation.getContestURL());
        memento.putLong("maxFileSize", contestInformation.getMaxFileSize());
        memento.putString("defaultAnswer", contestInformation.getJudgesDefaultAnswer());
        memento.putString("teamDisplayMode", contestInformation.getTeamDisplayMode().toString());
        return memento;
    }


    public XMLMemento createElement(IInternalContest contest, Language language, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(LANGUAGE_TAG);
        memento.putInteger("id", id);
        memento.putString("name", language.toString());
        return memento;
    }

    public IMemento addLanguageMemento(IMemento mementoRoot, int id, Language language) {
        IMemento memento = mementoRoot.createChild(LANGUAGE_TAG);
        memento.putInteger("id", id);
        memento.putString("name", language.toString());
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Problem problem, int id) {
        // TODO Auto-generated method stub
        
//        <problem id="1" state="enabled">
//        <label>A</label>
//        <name>APL Lives!</name>
//        <balloon-color rgb="#ffff00">yellow</balloon-color>
//        </problem>

        XMLMemento memento = XMLMemento.createWriteRoot(LANGUAGE_TAG);
        memento.putInteger("id", id);
        memento.putBoolean("enabled", problem.isActive());
        
        char let = 'A';
        let += (id -1);
        
        memento.createChildNode("label", ""+let);
        memento.createChildNode("name", problem.toString());
        IMemento balloonColor = memento.createChildNode("name", "TODO: color");
        balloonColor.putString("rgb", "TODO: rgb color");
        
        return memento;
    }
    
    public IMemento addProblemMemento(IMemento mementoRoot, Problem problem) {

        // TODO update this code to match Event Feed XML
        IMemento memento = mementoRoot.createChild(PROBLEM_TAG);
        memento.putString("name", problem.toString());
        return memento;

    }
    
    public XMLMemento createElement(IInternalContest contest, Account account) {
        // TODO Auto-generated method stub
        return null;
    }

    public IMemento addAccountMemento(IMemento mementoRoot, IInternalContest contest, Account account) {

        // TODO update this code to match Event Feed XML
        IMemento accountMemento = mementoRoot.createChild(ACCOUNT_TAG);
        ClientId clientId = account.getClientId();
        accountMemento.putString("name", account.getDisplayName());
        if (account.getGroupId() != null) {
            accountMemento.putString("group", contest.getGroup(account.getGroupId()).toString());
        }

        accountMemento.putString("type", clientId.getClientType().toString());
        accountMemento.putInteger("number", clientId.getClientNumber());
        accountMemento.putInteger("site", clientId.getSiteNumber());

        if (showPasswords) {
            accountMemento.putString("password", account.getPassword());
        }

        accountMemento.putBoolean("allowlogin", account.isAllowed(Permission.Type.LOGIN));
        accountMemento.putBoolean("showonscoreboard", account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD));
        accountMemento.putString("pc2triplet", clientId.getTripletKey());

        return accountMemento;
    }
    
    public XMLMemento createElement(IInternalContest contest, Clarification clarification) {
        // TODO Auto-generated method stub
        return null;
    }


    public IMemento addClarificationMemento(IMemento mementoRoot, IInternalContest contest, Clarification clarification) {

        // TODO update this code to match Event Feed XML
        String problemName = contest.getProblem(clarification.getProblemId()).toString();

        IMemento clarificationMemento = mementoRoot.createChild(CLARIFICATION_TAG);
        clarificationMemento.putInteger("site", clarification.getSiteNumber());
        clarificationMemento.putInteger("number", clarification.getNumber());
        clarificationMemento.putString("problem", problemName);

        clarificationMemento.putString("question", clarification.getQuestion());
        if (clarification.getAnswer() != null && clarification.isAnswered()) {
            clarificationMemento.putString("answer", clarification.getAnswer());
            clarificationMemento.putBoolean("sendToAll", clarification.isSendToAll());
        }
        clarificationMemento.putString("pc2probid", contest.getProblem(clarification.getProblemId()).getElementId().toString());

        return clarificationMemento;

    }

    public XMLMemento createElement(IInternalContest contest, Run run) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public IMemento addRunMemento(IMemento mementoRoot, IInternalContest contest, Run run) {

        // TODO update this code to match Event Feed XML
        
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

        IMemento memento = mementoRoot.createChild(RUN_TAG);
        
//        String languageName = contest.getLanguage(run.getLanguageId()).toString();
//        String problemName = contest.getProblem(run.getProblemId()).toString();
//
//        IMemento memento = mementoRoot.createChild(RUN_TAG);
//        memento.putInteger("site", run.getSiteNumber());
//        memento.putInteger("number", run.getNumber());
//        memento.putInteger("site", run.getSiteNumber());
//        memento.putLong("elapsed", run.getElapsedMins());
//
//        memento.putString("languageName", languageName);
//        memento.putString("problemName", problemName);
//
//        if (run.isJudged()) {
//            memento.putBoolean("solved", run.isSolved());
//        }
//
//        JudgementRecord[] judgementRecords = run.getAllJudgementRecords();
//
//        if (judgementRecords.length > 0) {
//            for (int idx = judgementRecords.length - 1; idx >= 0; idx--) {
//                addJudgementMemento(memento, contest, judgementRecords[idx], idx);
//            }
//        }
//
//        try {
//            RunFiles runFiles = contest.getRunFiles(run);
//            if (runFiles != null) {
//                String filename = runFiles.getMainFile().getName();
//                if (filename != null) {
//                    memento.putString("filename", filename);
//                }
//            }
//        } catch (IOException e) {
//            memento.putString("no_filename", "(missing)");
//        } catch (ClassNotFoundException e) {
//            memento.putString("no_filename", "(missing)");
//        } catch (FileSecurityException e) {
//            memento.putString("no_filename", "(missing)");
//        }
//
//        memento.putString("pc2id", run.getElementId().toString());
        return memento;
    }
    
    /**
     * @throws IOException
     */
    private String toXML(XMLMemento mementoRoot) throws IOException {
        return mementoRoot.saveToString();
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
        
        // TODO fix all 
        
        /**
         * A general implementation for logging errors needs to be established.
         * Perhaps something with log4j ?   The goal should be to standardized
         * logging in such a way that Exceptions are not lost.
         */

        // TODO add all configuration information
        
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
            idx ++;
        }
        
        idx = 0;
        for (Problem problem : contest.getProblems()) {
            try {
                sb.append(toXML(createElement(contest, problem, idx)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            idx ++;
        }

        for (Judgement judgement : contest.getJudgements()) {
            try {
                sb.append(toXML(createElement(contest, judgement)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
                

        // TODO add all clarifications
        
        Clarification [] clarifications  = contest.getClarifications();
        for (Clarification clarification : clarifications) {
            try {
                sb.append(toXML(createElement(contest, clarification)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // TODO add all runs

        Run [] runs = contest.getRuns();
        Arrays.sort(runs,new RunComparator());
        
        for (Run run: runs ) {
            try {
                sb.append(toXML(createElement(contest, run)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    
    public String createFinalizeXML (IInternalContest contest) {
        
        StringBuffer sb = new StringBuffer();
        
        // huh
        
        XMLMemento memento = XMLMemento.createWriteRoot(FINALIZE_TAG);
        
        try {
            sb.append(toXML(memento));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        sb.append("</");
        sb.append (CONTEST_TAG);
        sb.append(">");
        return sb.toString();
        

        
    }
    
    public XMLMemento createElement(IInternalContest contest, Judgement judgement) {
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>
        
        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_TAG);
        String name = judgement.getDisplayName();
        
        addChild(memento, "acronym", "TODO:");         // TODO Need to add Judgement.getAcronym();
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
        
        addChild(memento, "acronym", "TODO:");         // TODO Need to add Judgement.getAcronym();
        addChild(memento, "name", name);
        return memento;
    }
}
