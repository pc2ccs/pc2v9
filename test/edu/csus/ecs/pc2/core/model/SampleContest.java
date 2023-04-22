// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.InternalControllerSpecial;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import edu.csus.ecs.pc2.core.util.NotificationUtilities;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.ui.InvalidFieldValue;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Create Sample contest and controller.
 * 
 * Create contest and controller and add various data values.
 * 
 * @see #createContest(int, int, int, int)
 * @see #createController(IInternalContest, boolean, boolean)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SampleContest {

    private boolean debugMode = false;

    private Random random = new Random();

    public static final int DEFAULT_PORT_NUMBER = 50002;

    private int defaultPortNumber = DEFAULT_PORT_NUMBER;

    private NotificationUtilities notificationUtilities = new NotificationUtilities();

    private String samplesDirectory;
    
    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    private static final String[] W3C_COLORS = { "Alice Blue;F0F8FF", "Antique White;FAEBD7", "Aqua;00FFFF", "Aquamarine;7FFFD4", "Azure;F0FFFF", "Beige;F5F5DC", "Bisque;FFE4C4", "Black;000000",
            "Blanched Almond;FFEBCD", "Blue;0000FF", "Blue Violet;8A2BE2", "Brown;A52A2A", "Burlywood;DEB887", "Cadet Blue;5F9EA0", "Chartreuse;7FFF00", "Chocolate;D2691E", "Coral;FF7F50",
            "Cornflower;6495ED", "Cornsilk;FFF8DC", "Crimson;DC143C", "Cyan;00FFFF", "Dark Blue;00008B", "Dark Cyan;008B8B", "Dark Goldenrod;B8860B", "Dark Gray;A9A9A9", "Dark Green;006400",
            "Dark Khaki;BDB76B", "Dark Magenta;8B008B", "Dark Olive Green;556B2F", "Dark Orange;FF8C00", "Dark Orchid;9932CC", "Dark Red;8B0000", "Dark Salmon;E9967A", "Dark Sea Green;8FBC8F",
            "Dark Slate Blue;483D8B", "Dark Slate Gray;2F4F4F", "Dark Turquoise;00CED1", "Dark Violet;9400D3", "Deep Pink;FF1493", "Deep Sky Blue;00BFFF", "Dim Gray;696969", "Dodger Blue;1E90FF",
            "Firebrick;B22222", "Floral White;FFFAF0", "Forest Green;228B22", "Fuchsia;FF00FF", "Gainsboro;DCDCDC", "Ghost White;F8F8FF", "Gold;FFD700", "Goldenrod;DAA520", "Gray;7F7F7F",
            "Green;007F00", "Green Yellow;ADFF2F", "Honeydew;F0FFF0", "Hot Pink;FF69B4", "Indian Red;CD5C5C", "Indigo;4B0082", "Ivory;FFFFF0", "Khaki;F0E68C", "Lavender;E6E6FA",
            "Lavender Blush;FFF0F5", "Lawn Green;7CFC00", "Lemon Chiffon;FFFACD", "Light Blue;ADD8E6", "Light Coral;F08080", "Light Cyan;E0FFFF", "Light Goldenrod;FAFAD2", "Light Gray;D3D3D3",
            "Light Green;90EE90", "Light Pink;FFB6C1", "Light Salmon;FFA07A", "Light Sea Green;20B2AA", "Light Sky Blue;87CEFA", "Light Slate Gray;778899", "Light Steel Blue;B0C4DE",
            "Light Yellow;FFFFE0", "Lime;00FF00", "Lime Green;32CD32", "Linen;FAF0E6", "Magenta;FF00FF", "Maroon;7F0000", "Medium Aquamarine;66CDAA", "Medium Blue;0000CD", "Medium Orchid;BA55D3",
            "Medium Purple;9370DB", "Medium Sea Green;3CB371", "Medium Slate Blue;7B68EE", "Medium Spring Green;00FA9A", "Medium Turquoise;48D1CC", "Medium Violet Red;C71585", "Midnight Blue;191970",
            "Mint Cream;F5FFFA", "Misty Rose;FFE4E1", "Moccasin;FFE4B5", "Navajo White;FFDEAD", "Navy;000080", "Old Lace;FDF5E6", "Olive;808000", "Olive Drab;6B8E23", "Orange;FFA500",
            "Orange Red;FF4500", "Orchid;DA70D6", "Pale Goldenrod;EEE8AA", "Pale Green;98FB98", "Pale Turquoise;AFEEEE", "Pale Violet Red;DB7093", "Papaya Whip;FFEFD5", "Peach Puff;FFDAB9",
            "Peru;CD853F", "Pink;FFC0CB", "Plum;DDA0DD", "Powder Blue;B0E0E6", "Purple;7F007F", "Red;FF0000", "Rosy Brown;BC8F8F", "Royal Blue;4169E1", "Saddle Brown;8B4513", "Salmon;FA8072",
            "Sandy Brown;F4A460", "Sea Green;2E8B57", "Seashell;FFF5EE", "Sienna;A0522D", "Silver;C0C0C0", "Sky Blue;87CEEB", "Slate Blue;6A5ACD", "Slate Gray;708090", "Snow;FFFAFA",
            "Spring Green;00FF7F", "Steel Blue;4682B4", "Tan;D2B48C", "Teal;008080", "Thistle;D8BFD8", "Tomato;FF6347", "Turquoise;40E0D0", "Violet;EE82EE", "Wheat;F5DEB3", "White;FFFFFF",
            "White Smoke;F5F5F5", "Yellow;FFFF00", "Yellow Green;9ACD32" };
    
    public static final String[] GIRL_NAMES = { "Abigail", "Aimee", "Alexandra", "Alice", "Alisha", "Amber", "Amelia", "Amelie", "Amy", "Anna", "Ava", "Bethany", "Brooke", "Caitlin", "Charlotte",
            "Chloe", "Daisy", "Eleanor", "Elizabeth", "Ella", "Ellie", "Emilia", "Emily", "Emma", "Erin", "Esme", "Eva", "Eve", "Evelyn", "Evie", "Faith", "Florence", "Francesca", "Freya", "Georgia",
            "Grace", "Gracie", "Hannah", "Harriet", "Heidi", "Hollie", "Holly", "Imogen", "Isabel", "Isabella", "Isabelle", "Isla", "Isobel", "Jasmine", "Jessica", "Julia", "Katie", "Keira", "Lacey",
            "Lauren", "Layla", "Leah", "Lexi", "Lexie", "Libby", "Lilly", "Lily", "Lola", "Lucy", "Lydia", "Maddison", "Madison", "Maisie", "Maria", "Martha", "Matilda", "Maya", "Megan", "Mia",
            "Millie", "Molly", "Mya", "Niamh", "Nicole", "Olivia", "Paige", "Phoebe", "Poppy", "Rebecca", "Rose", "Rosie", "Ruby", "Sara", "Sarah", "Scarlett", "Sienna", "Skye", "Sofia", "Sophia",
            "Sophie", "Summer", "Tia", "Tilly", "Zara", "Zoe", };

    public static final String[] BOYS_NAMES = { "Aaron", "Adam", "Aidan", "Aiden", "Alex", "Alexander", "Alfie", "Archie", "Arthur", "Ashton", "Austin", "Bailey", "Ben", "Benjamin", "Bradley",
            "Brandon", "Callum", "Cameron", "Charles", "Charlie", "Christopher", "Connor", "Daniel", "David", "Dylan", "Edward", "Elliot", "Ellis", "Ethan", "Evan", "Ewan", "Finlay", "Finley",
            "Freddie", "Frederick", "Gabriel", "George", "Harley", "Harrison", "Harry", "Harvey", "Hayden", "Henry", "Isaac", "Jack", "Jacob", "Jake", "James", "Jamie", "Jayden", "Joe", "Joel",
            "John", "Joseph", "Joshua", "Jude", "Kai", "Kian", "Kieran", "Kyle", "Leo", "Leon", "Lewis", "Liam", "Logan", "Louie", "Louis", "Luca", "Lucas", "Luke", "Mason", "Matthew", "Max",
            "Michael", "Mohammad", "Mohammed", "Morgan", "Muhammad", "Nathan", "Noah", "Oliver", "Oscar", "Owen", "Reece", "Reuben", "Rhys", "Riley", "Robert", "Ryan", "Sam", "Samuel", "Sebastian",
            "Stanley", "Taylor", "Theo", "Thomas", "Toby", "Tyler", "William", "Zachary", };


    public SampleContest() {
        super();
    }

    /**
     * Create a new Site class instance.
     * 
     * @param siteNumber
     *            site number
     * @param siteName
     *            title for site
     * @param hostName
     *            if null, assigned to localhost
     * @param portNumber
     *            if 0 assigned 50002 + (siteNumber-1)* 1000
     * @return
     */
    public Site createSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            props.put(Site.IP_KEY, "localhost");
        }

        if (portNumber == 0) {
            portNumber = defaultPortNumber + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }

    public IInternalContest createContest(int siteNumber, int numSites, int numTeams, int numJudges, boolean initAsServer) {

        String contestPassword = "Password 101";
        Profile profile = new Profile("Default.");
        return createContest(siteNumber, numSites, numTeams, numJudges, initAsServer, profile, contestPassword, true);
    }

    /**
     * Create an instance of contest with languages, problems, judgements, teams and judges.
     * 
     * @param siteNumber current site number
     * @param numSites 
     * @param numTeams
     * @param numJudges
     * @param initAsServer initialize as server, else initalizes as admin.
     * @param profile
     * @param contestPassword
     * @return
     */
    public IInternalContest createContest(int siteNumber, int numSites, int numTeams, int numJudges, boolean initAsServer, Profile profile, String contestPassword, boolean assignColors) {

        String[] languages = { LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.MSCTITLE, "APL" };
        String[] problemsNames = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };
        String[] judgements = { "Stupid programming error", "Misread problem statement", "Almost there", "You have no clue", "Give up and go home", "Consider switching to another major",
                "How did you get into this place ?", "Contact Staff - you have no hope" };
        String[] acronymns = {"CE", "WA", "TLE", "WA2", "RTE", "OFE", "WA3", "JE" };
        
        InternalContest contest = new InternalContest();

        contest.setSiteNumber(siteNumber);
        
        for (int i = 0; i < numSites; i++) {
            Site site = createSite(i + 1, "Site " + (i + 1), null, 0);
            contest.addSite(site);
        }

        for (String langName : languages) {
            Language language = new Language(langName);
            String[] values = LanguageAutoFill.getAutoFillValues(langName);
            if (values[0].trim().length() != 0) {
                fillLanguage(language, values);
            } else {
                values = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.DEFAULTTITLE);
                fillLanguage(language, values);
                language.setDisplayName(langName);
            }

            language.setSiteNumber(siteNumber);
            if (language.getID() == null || language.getID().trim().length() < 1){
                // if no lang id set, assign a randome one.
                language.setID("lang"+(contest.getLanguages().length+1));
            }
            contest.addLanguage(language);
        }
        
        Problem[] problems = createProblems(problemsNames, 'A', siteNumber);
        for (Problem problem : problems) {
            contest.addProblem(problem);
        }

        Problem generalProblem = getGeneralProblem();
        contest.setGeneralProblem(generalProblem);

        Judgement judgementYes = new Judgement("Yes.", "AC");
        contest.addJudgement(judgementYes);

        int i = 0;
        for (String judgementName : judgements) {
            Judgement judgement = new Judgement(judgementName,acronymns[i]);
            contest.addJudgement(judgement);
            i++;
        }
        
        contest.generateNewAccounts(Type.ADMINISTRATOR.toString(), 1, true);

        if (numTeams > 0) {
            contest.generateNewAccounts(Type.TEAM.toString(), numTeams, true);
        }

        if (numJudges > 0) {
            contest.generateNewAccounts(Type.JUDGE.toString(), numJudges, true);
        }

        ContestTime contestTime = new ContestTime(siteNumber);
        contest.addContestTime(contestTime);

        if (initAsServer) {
            ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
            contest.setClientId(serverId);
        } else {
            ClientId serverId = new ClientId(siteNumber, Type.ADMINISTRATOR, 1);
            contest.setClientId(serverId);
        }

        contest.setProfile(profile);
        contest.setContestPassword(contestPassword);

        if (assignColors) {
            assignColors(contest);
        }

        return contest;
    }

    public final Problem getGeneralProblem() {
        return new Problem("General.");
    }

    public Problem createProblem(String probName, char letter, int siteNumber) {
        Problem problem = new Problem(probName);
        String shortName = probName.split(" ")[0];
        problem.setShortName(shortName.toLowerCase());
        if (! problem.isValidShortName()){
            throw new InvalidFieldValue("Invalid short name '"+problem.getShortName()+"'");
        }
        problem.setLetter(Character.toString(letter));
        problem.setSiteNumber(siteNumber);
        return problem;
    }

    public Problem[] createProblems(String [] problemNames, char startLetter, int siteNumber) {
        Problem [] problems = new Problem[problemNames.length];
        char letter = startLetter;
        int i = 0;
        for (String name : problemNames) {
            problems[i++] = createProblem(name, letter, siteNumber);
            letter ++;
        }
        return problems;
    }
    
    /**
     * Assign colors.
     * 
     * @param contest
     */
    public void assignColors(IInternalContest contest) {

        notificationUtilities.getScoreboardClientSettings(contest); // insure balloon client is created, etc.

        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());

        int problemNumber = 0;

        for (Problem problem : contest.getProblems()) {
            String[] fields = W3C_COLORS[problemNumber].split(";");
            balloonSettings.addColor(problem, fields[0], fields[1]);
            problemNumber++;
            problem.setColorName(fields[0]);
            // TODO we should add RGB too, but then testdata would need to be updated
//            problem.setColorRGB(fields[1]);
        }

    }

    /**
     * Returns 6 character hex RGB string for input color name.
     * 
     * @param colorName
     */
    public String lookupRGB(String colorName) {
        colorName = colorName.trim().toLowerCase();
        for (String line : W3C_COLORS) {
            if (line.toLowerCase().startsWith(colorName)) {
                String[] fields = line.split(";");
                String rgbName = fields[1];
                return rgbName;
            }
        }
        return null;
    }

    private void fillLanguage(Language language, String[] values) {
        // values array
        // 0 Title for Language
        // 1 Compiler Command Line
        // 2 Executable Identifier Mask
        // 3 Execute command line
        // 4 is the Title again????
        // 5 isInterpreted
        // 6 is the ID

        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
        if (LanguageAutoFill.INTERPRETER_VALUE.equals(values[5])) {
            language.setInterpreted(true);
        } else {
            language.setInterpreted(false);
        }
        language.setID(values[6]);
    }

    public IInternalController createController(IInternalContest contest, boolean isServer, boolean isRemote) {
        return createController(contest, null, isServer, isRemote);
    }

    /**
     * Create a InternalController.
     * 
     * @param contest
     *            model for controller
     * @param isServer
     *            is this a server controller ?
     * @param isRemote
     *            is this a remote site ?
     * @param storageDirectory
     *            where this controller would save its packets.
     * @return
     */
    public IInternalController createController(IInternalContest contest, String storageDirectory, boolean isServer, boolean isRemote) {

        // Start site 1
        InternalController controller = new InternalController(contest);
        controller.setUsingMainUI(false);
        
        controller.setLog(null); // creates and opens a default log name 

        if (isServer) {
            controller.setContactingRemoteServer(isRemote);
            String[] argsSiteOne = { "--server", "--skipini" };
            int siteNumber = contest.getSiteNumber();

            // As of 2008-01-20 start sets site number to zero.
            controller.start(argsSiteOne);

            // set InternalContest back to original site number
            contest.setSiteNumber(siteNumber);

            if (storageDirectory != null) {
                FileStorage storage = new FileStorage(storageDirectory);
                contest.setStorage(storage);
                controller.initializeStorage(storage);
            }
        } else {
            controller.start(null);

        }

        return controller;
    }

    
    /**
     * Create a controller which saves packets.
     */
    public InternalControllerSpecial createPacketController(IInternalContest contest, String storageDirectory, boolean isServer, boolean isRemote) {

        // Start site 1
        InternalControllerSpecial controller = new InternalControllerSpecial(contest);
        controller.setUsingMainUI(false);

        if (isServer) {
            controller.setContactingRemoteServer(isRemote);
            String[] argsSiteOne = { "--server", "--skipini" };
            int siteNumber = contest.getSiteNumber();

            // As of 2008-01-20 start sets site number to zero.
            controller.start(argsSiteOne);

            // set InternalContest back to original site number
            contest.setSiteNumber(siteNumber);

            if (storageDirectory != null) {
                FileStorage storage = new FileStorage(storageDirectory);
                contest.setStorage(storage);
                controller.initializeStorage(storage);
            }
        } else {
            controller.start(null);

        }

        return controller;
    }
    
    
    public static String getTestDirectoryName(String subDirName) {
        String testDir = "testout" + File.separator + subDirName;

        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }

    /**
     * Populate Language, Problems and Judgements.
     * 
     * @param contest
     */
    public void populateContest(IInternalContest contest) {

        String[] languages = { "Java", LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.MSCTITLE, "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
            String[] values = LanguageAutoFill.getAutoFillValues(langName);
            if (values[0].trim().length() != 0) {
                fillLanguage(language, values);
            }
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contest.addProblem(problem);
        }

        // Only put judgements in if there are no judgements
        if (contest.getJudgements().length == 0) {

            Judgement judgementYes = new Judgement("Yes");
            contest.addJudgement(judgementYes);

            for (String judgementName : judgements) {
                contest.addJudgement(new Judgement(judgementName));
            }
        }

    }

    /**
     * Get all sites' teams.
     * 
     * @param contest
     * @return
     */
    public static Account[] getTeamAccounts(IInternalContest contest) {
        return getAccounts(contest, Type.TEAM);
    }
    
    public Account[] getJudgeAccounts(IInternalContest contest) {
        return getAccounts(contest, Type.JUDGE);
    }
    
    

    /**
     * Get site's teams.
     * 
     * @param contest
     * @param siteNumber
     * @return
     */
    public static Account[] getTeamAccounts(IInternalContest contest, int siteNumber) {
        Vector<Account> accountVector = contest.getAccounts(ClientType.Type.TEAM, siteNumber);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Create N runs identical to input run, add elapsed time.
     * 
     * @param contest
     * @param numberRuns
     * @param run
     * @return
     */
    public Run[] cloneRun(IInternalContest contest, int numberRuns, Run run) {

        Run[] runs = new Run[numberRuns];
        Problem problem = contest.getProblem(run.getProblemId());
        Language language = contest.getLanguage(run.getLanguageId());
        ClientId teamId = run.getSubmitter();

        // System.out.println("debug22Z " + run + " " + run.getProblemId() + " " + run.getLanguageId());

        for (int i = 0; i < numberRuns; i++) {
            Run newRun = new Run(teamId, language, problem);
            newRun.setElapsedMins(run.getElapsedMins() + 9 + i);
            newRun.setNumber(contest.getRuns().length);
            newRun.setEntryPoint("Foo.class");
            // System.out.println("debug22Z " + run + " " + run.getProblemId() + " " + run.getLanguageId());
            runs[i] = newRun;
        }

        return runs;
    }

    
    
    public Run[] createRandomRuns(IInternalContest contest, int numberRuns, boolean randomTeam, boolean randomProblem, boolean randomLanguage, int siteNumber) {

        Account[] accounts = getTeamAccounts(contest);

        if (siteNumber != 0) {
            accounts = getTeamAccounts(contest, siteNumber);
        }

        if (accounts.length == 0) {
            new Exception("No accounts for site " + siteNumber).printStackTrace();
            return new Run[0];
        }

        ClientId teamId = accounts[0].getClientId();
        
        return createRandomRuns(contest, numberRuns, teamId, randomTeam, randomProblem, randomLanguage, siteNumber);

    }
    
    public Run[] createRandomRuns(IInternalContest contest, int numberRuns, ClientId teamId, boolean randomTeam, boolean randomProblem, boolean randomLanguage, int siteNumber) {

        Run[] runs = new Run[numberRuns];

        Account[] accounts = getTeamAccounts(contest, siteNumber);
        Language[] languages = contest.getLanguages();
        Problem[] problems = contest.getProblems();

        int numRuns = contest.getRuns().length;

        if (siteNumber != 0) {
            accounts = getTeamAccounts(contest, siteNumber);
        }

        if (accounts.length == 0) {
            throw new RuntimeException("No accounts for site " + siteNumber);
        }

        for (int i = 0; i < numberRuns; i++) {
            Problem problem = problems[0];
            Language language = languages[0];
            

            if (randomLanguage) {
                int randomLangIndex = random.nextInt(languages.length);
                language = (Language) languages[randomLangIndex];
            }

            if (randomProblem) {
                int randomProblemIndex = random.nextInt(problems.length);
                problem = (Problem) problems[randomProblemIndex];
            }

            if (randomTeam) {
                teamId = getRandomAccount(contest, Type.TEAM, siteNumber).getClientId();
            }

            Run run = new Run(teamId, language, problem);
            run.setElapsedMins(9 + i);
            run.setNumber(++numRuns);
            run.setSiteNumber(siteNumber);
            run.setEntryPoint("Foo.class");
            runs[i] = run;
        }
        return runs;

    }

    /**
     * Generate a number of new runs.
     * 
     * @param contest
     * @param numberRuns
     * @param randomTeam
     * @param randomProblem
     * @param randomLanguage
     * @return
     */
    public Run[] createRandomRuns(IInternalContest contest, int numberRuns, boolean randomTeam, boolean randomProblem, boolean randomLanguage) {
        return createRandomRuns(contest, numberRuns, randomTeam, randomProblem, randomLanguage, contest.getSiteNumber());
    }

    /**
     * Create new run and add to contest run list.
     * 
     * @param contest
     * @param clientId
     * @param language
     * @param problem
     * @param elapsed
     * @return
     */
    public Run createRun(IInternalContest contest, ClientId clientId, Language language, Problem problem, long elapsed) {
        int numRuns = contest.getRuns().length;
        Run run = new Run(clientId, language, problem);
        run.setElapsedMins(elapsed);
        run.setNumber(++numRuns);
        run.setEntryPoint("Foo.class");
        try {
            contest.addRun(run);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileSecurityException e) {
            e.printStackTrace();
        }
        return run;
    }

    /**
     * Create new run and add to contest run list.
     * 
     * @param contest
     * @param clientId
     * @param problem
     * @return
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Run createRun(IInternalContest contest, ClientId clientId, Problem problem) throws IOException, ClassNotFoundException, FileSecurityException {
        int numRuns = contest.getRuns().length;
        Run run = new Run(clientId, contest.getLanguages()[0], problem);
        run.setEntryPoint("Foo.class");
        run.setElapsedMins(9 + numRuns);
        run.setNumber(++numRuns);
        AbstractTestCase test = new AbstractTestCase();
        RunFiles runFiles = new RunFiles(run, test.getSamplesSourceFilename("hello.java"));
        contest.addRun(run,runFiles);
        return run;
    }

    /**
     * Create a copy of the run (not a clone, but close) and add to contest run list.
     * 
     * This references the input run's JugementRecords instead of cloning them. This run will have a different getElementId() and a getNumber() which represents the next run number.
     * 
     * @param contest
     * @param run
     * @param cloneJudgements
     * @return
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Run copyRun(IInternalContest contest, Run run, boolean cloneJudgements) throws IOException, ClassNotFoundException, FileSecurityException {
        Run newRun = new Run(run.getSubmitter(), contest.getLanguage(run.getLanguageId()), contest.getProblem(run.getProblemId()));
        newRun.setElapsedMS(run.getElapsedMS());
        newRun.setDeleted(run.isDeleted());
        newRun.setNumber(contest.getRuns().length);
        newRun.setStatus(RunStates.NEW);
        newRun.setEntryPoint("Foo.class");
        
        if (cloneJudgements) {
            for (JudgementRecord judgementRecord : newRun.getAllJudgementRecords()) {
                newRun.addJudgement(judgementRecord);
            }
            newRun.setStatus(run.getStatus());
        }
        contest.addRun(newRun);
        return newRun;
    }

    public Site createSite(int nextSiteNumber, String hostName, int port) {
        return createSite(nextSiteNumber, hostName, port, null);
    }

    public Site createSite(int nextSiteNumber, String hostName, int port, String siteName) {
        if (siteName == null) {
            siteName = new String("Site " + nextSiteNumber);
        }
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }

    public Site createSite(IInternalContest contest, String siteName) {
        int nextSiteNumber = contest.getSites().length + 1;
        int newPortNumber = DEFAULT_PORT_NUMBER + (nextSiteNumber - 1) * 1000;
        Site site = createSite(nextSiteNumber, "localhost", newPortNumber, siteName);
        return site;
    }

    public Site[] createSites(IInternalContest contest, int count) {
        Site[] sites = new Site[count];
        for (int i = 0; i < count; i++) {
            int nextSiteNumber = contest.getSites().length + i + 1;
            int newPortNumber = DEFAULT_PORT_NUMBER + (nextSiteNumber - 1) * 1000;
            Site site = createSite(nextSiteNumber, "localhost", newPortNumber, null);
            sites[i] = site;
        }
        return sites;
    }

    /**
     * Create profiles but do not add profiles to contest.
     * 
     * @param contest
     * @param count
     * @return
     */
    public Profile[] createProfiles(IInternalContest contest, int count) {
        Profile[] profiles = new Profile[count];
        for (int i = 0; i < count; i++) {
            int nextProfileNumber = contest.getProfiles().length + i + 1;
            Profile profile = new Profile("Profile " + nextProfileNumber);
            profile.setDescription("Created Profile " + nextProfileNumber);
            profiles[i] = profile;
        }
        return profiles;
    }

    /**
     * add a judged run to list of runs in a contest.
     * 
     * Fields in runInfoLine:
     * 
     * <pre>
     * 0 - run id, int
     * 1 - team id, int
     * 2 - problem letter, char
     * 3 - elapsed, int
     * 4 - solved, String &quot;Yes&quot; or No (or full No judgement text)
     * 5 - send to teams, Yes or No
     * 
     * Example:
     * &quot;6,5,A,12,Yes&quot;
     * &quot;6,5,A,12,Yes,Yes&quot;
     * 
     * </pre>
     * 
     * @param contest
     * @param runInfoLine
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws RunUnavailableException 
     */
    public Run addARun(IInternalContest contest, String runInfoLine) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {

        // get last judge
        Account[] accounts = (Account[]) contest.getAccounts(Type.JUDGE).toArray(new Account[contest.getAccounts(Type.JUDGE).size()]);
        ClientId judgeId = accounts[accounts.length - 1].getClientId();

        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement noJudgement = contest.getJudgements()[1];

        String[] data = runInfoLine.split(",");

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);

        boolean solved = data[4].equals("Yes");

        boolean sendToTeams = true;
        if (data.length > 5) {
            sendToTeams = data[5].equals("Yes");
        }

        int problemIndex = probLet.charAt(0) - 'A';
        Problem problem = problemList[problemIndex];
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

        Run run = new Run(clientId, languageId, problem);
        run.setNumber(runId);
        run.setSiteNumber(contest.getSiteNumber());
        run.setElapsedMins(elapsed);
        run.setEntryPoint("Foo.class");
        
        JudgementRecord judgementRecord = null;

        if (data[4].trim().length() != 0 && (! data[4].equalsIgnoreCase("New"))){
            
            // Use a default No entry
            ElementId judgementId = noJudgement.getElementId();

            if (solved) {
                judgementId = getYesJudgement(contest).getElementId();
            } else {

                // Try to find No judgement

                for (Judgement judgement : contest.getJudgements()) {
                    if (judgement.toString().equalsIgnoreCase(data[4])) {
                        judgementId = judgement.getElementId();
                    }
                }
            }
            
            judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);
            judgementRecord.setSendToTeam(sendToTeams);

        }
        
        contest.addRun(run);

        if (judgementRecord != null){
            checkOutRun(contest, run, judgeId);
            contest.addRunJudgement(run, judgementRecord, null, judgeId);
        }

        if (debugMode) {
            System.out.print("Send to teams " + run.getJudgementRecord().isSendToTeam() + " ");
            System.out.println("Added run " + run);
        }

        return contest.getRun(run.getElementId());

    }

    private boolean isSolved(IInternalContest contest, ElementId judgementId) {
        return contest.getJudgements()[0].getElementId().equals(judgementId);
    }

    /**
     * Add a judgement to a run.
     * 
     * @param contest
     * @param run
     * @param judgement
     * @param judgeId
     * @return run with judgement.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     * @throws RunUnavailableException 
     */
    public Run addJudgement(IInternalContest contest, Run run, Judgement judgement, ClientId judgeId) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {

        ElementId judgementId = judgement.getElementId();
        boolean solved = isSolved(contest, judgementId);

        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);

        checkOutRun(contest, run, judgeId);
        contest.addRunJudgement(run, judgementRecord, null, judgeId);

        return contest.getRun(run.getElementId());
    }

    public static void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) throws RunUnavailableException, IOException, ClassNotFoundException, FileSecurityException {
        
            if (run == null) {
                throw  new IllegalArgumentException("run is null");
            }
            
            if (judgeId == null) {
                throw  new IllegalArgumentException("judge id is null");
            }
            
            contest.checkoutRun(run, judgeId, false, false);
    }
    

    /**
     * Assign a color to a problem.
     * 
     * @param contest
     * @param problem
     * @param color
     * @param rgbColor
     */
    public void assignColor(IInternalContest contest, Problem problem, String color, String rgbColor) {

        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());

        if (balloonSettings == null) {
            balloonSettings = new BalloonSettings("BalloonSettingsSite" + contest.getSiteNumber(), contest.getSiteNumber());
        }

        balloonSettings.addColor(problem, color, rgbColor);
        contest.updateBalloonSettings(balloonSettings);
    }

    /**
     * Add a ballon notification.
     * 
     * @param contest
     * @param scoreboardClientId
     * @param run
     */
    public void addBalloonNotification(IInternalContest contest, Run run) {

        ClientSettings settings = notificationUtilities.getScoreboardClientSettings(contest);

        BalloonDeliveryInfo deliveryInfo = new BalloonDeliveryInfo(run.getSubmitter(), run.getProblemId(), Calendar.getInstance().getTime().getTime());

        String balloonKey = notificationUtilities.getBalloonKey(run.getSubmitter(), run.getProblemId());

        Hashtable<String, BalloonDeliveryInfo> hashtable = settings.getBalloonList();
        hashtable.put(balloonKey, deliveryInfo);
        settings.setBalloonList(hashtable);
    }

    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Add runs to contest (via acceptRun)
     * 
     * @param contest
     * @param runs
     * @param filename
     *            name of file to submit
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void addRuns(IInternalContest contest, Run[] runs, String filename) throws IOException, ClassNotFoundException, FileSecurityException {

        if (!new File(filename).exists()) {
            throw new IllegalArgumentException("filename is null");
        }

        for (Run run : runs) {
            RunFiles runFiles = new RunFiles(run, filename);
            run.setEntryPoint(runFiles.getMainFile().getName());
            contest.acceptRun(run, runFiles);
        }
    }
    
    /**
     * Create a sample RunFiles.
     * 
     * Uses {@link #getSampleFile()} as the filename.
     * 
     * @param run
     * @return 
     */
    public RunFiles createSampleRunFiles (Run run) {
        return new RunFiles(run, getSampleFile());
    }

    /**
     * Print the report to the filename.
     * 
     * @param filename
     * @param selectedReport
     * @param filter
     * @param inContest
     * @param inController
     */
    public void printReport(String filename, IReport selectedReport, Filter filter, IInternalContest inContest, IInternalController inController) {

        if (filter == null) {
            filter = new Filter();
        }

        try {
            selectedReport.setContestAndController(inContest, inController);
            selectedReport.setFilter(filter);
            selectedReport.createReportFile(filename, filter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param contest
     * @return Yes/Solved judgement
     */
    public Judgement getYesJudgement(IInternalContest contest) {
        return contest.getJudgements()[0];
    }

    public int getDefaultPortNumber() {
        return defaultPortNumber;
    }

    public void setDefaultPortNumber(int defaultPortNumber) {
        this.defaultPortNumber = defaultPortNumber;
    }

    public Judgement getRandomJudgement(IInternalContest contest, boolean solved) {

        Judgement[] judgements = contest.getJudgements();

        if (solved) {
            return judgements[0];
        } else {
            int randomJudgement = random.nextInt(judgements.length - 1);
            return judgements[randomJudgement + 1];
        }
    }

    /**
     * Return Sumit source file in test area.
     * 
     * @see #getSampleFile(String)
     * @return
     */
    public String getSampleFile() {
        return getSampleFile("Sumit.java");
    }

    /**
     * Return full filename in file test directory.
     * 
     * Will print Exceptions if test directory is not present or if no such filename found.
     * 
     * @param filename
     * @return filename with path to test data.
     */
    public String getSampleFile(String filename) {
        if (samplesDirectory == null){
            String samps = "samps";
            String projectPath = JUnitUtilities.locate(samps);
            if (projectPath == null) {
                new Exception("Unable to locate " + samps).printStackTrace(System.err);
            }
            samplesDirectory = projectPath + File.separator + samps + File.separator;
        }
        
        String testfilename = samplesDirectory + File.separator + "src" + File.separator + filename;

        if (!new File(testfilename).isFile()) {
            new Exception("No sample file found " + testfilename).printStackTrace(System.err);
        }
        return testfilename;
    }

    /**
     * Create a notification if needed.
     * 
     * Checks for existing notification, only creates a notification if no notification exists.
     * 
     * @param contest2
     * @param run
     */
    public void createNotification(IInternalContest contest2, Run run) {

        BalloonDeliveryInfo info = notificationUtilities.getNotification(contest2, run);

        if (info == null) {
            addNotification(contest2, run);
        }
    }

    /**
     * Unconditionally creates a notification.
     * 
     * @param contest2
     * @param run
     */
    private void addNotification(IInternalContest contest2, Run run) {

        ClientSettings settings = notificationUtilities.getScoreboardClientSettings(contest2);
        Hashtable<String, BalloonDeliveryInfo> balloonList = settings.getBalloonList();
        String key = notificationUtilities.getBalloonKey(run.getSubmitter(), run.getProblemId());

        BalloonDeliveryInfo info = new BalloonDeliveryInfo(run.getSubmitter(), run.getProblemId(), Calendar.getInstance().getTime().getTime());
        balloonList.put(key, info);
        settings.setBalloonList(balloonList);
        contest2.updateClientSettings(settings);
    }
    
    /**
     * Assigns two group names to all teams.
     * 
     * @param contest
     * @param groupNameOne first half of teams assigned this group
     * @param groupNameTwo second half of teams assigned this group
     */
    public void assignSampleGroups (IInternalContest contest, String groupNameOne, String groupNameTwo){
        Group group1 = new Group(groupNameOne);
        group1.setGroupId(1024);
        contest.addGroup(group1);

        Group group2 = new Group(groupNameTwo);
        group2.setGroupId(2048);
        contest.addGroup(group2);

        Account[] teams = getTeamAccounts(contest);

        assignTeamGroup(contest, group1, 0, teams.length / 2);
        assignTeamGroup(contest, group2, teams.length / 2, teams.length - 1);
    }

    public void assignTeamExternalIds(IInternalContest contest, int startId) {
        Account[] teams = getTeamAccounts(contest);
        for (Account account : teams) {
            if (account.getExternalId() == null || "".equals(account.getExternalId())) {
                account.setExternalId(Integer.toString(startId));
                startId++;
            }
        }
    }
    
    /**
     * Quick load of contest with runs and groups.
     * 
     * @param contest
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     * @throws RunUnavailableException 
     */
    public IInternalContest quickLoad(IInternalContest contest, int numberOfRuns) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {

        SampleContest sample = new SampleContest();

        int siteNumber = 2;
        if (contest == null){
            contest = sample.createContest(siteNumber, 1, 22, 12, true);
        }

        addContestInfo(contest, "Contest Title");

        Group group1 = new Group("Mississippi");
        group1.setGroupId(1024);
        contest.addGroup(group1);

        Group group2 = new Group("Arkansas");
        group2.setGroupId(2048);
        contest.addGroup(group2);

        Account[] teams = getTeamAccounts(contest);

        assignTeamGroup(contest, group1, 0, teams.length / 2);
        assignTeamGroup(contest, group2, teams.length / 2, teams.length - 1);
        
        Run[] runs = sample.createRandomRuns(contest, numberOfRuns, true, true, true);
        
        for (Run run : runs) {
            contest.addRun(run);
        }

        /**
         * Add Run Judgements.
         */
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        Judgement judgement;

        for (Run run : runs) {
            RunFiles runFiles = createSampleRunFiles(run);

            contest.acceptRun(run, runFiles);

            run.setElapsedMins((run.getNumber() - 1) * 9);

            judgement = sample.getRandomJudgement(contest, run.getNumber() % 2 == 0); // ever other run is judged Yes.
            sample.addJudgement(contest, run, judgement, judgeId);
        }

        return contest;
    }

    /**
     * Assign group to team startIdx to endIdx.
     * 
     * @param group
     * @param startIdx
     * @param endIdx
     * @param contest2
     */
    public void assignTeamGroup(IInternalContest contest2, Group group, int startIdx, int endIdx) {
        Account[] teams = getTeamAccounts(contest2);
        for (int i = startIdx; i < endIdx; i++) {
            teams[i].setGroupId(group.getElementId());
        }
    }

    public void addContestInfo(IInternalContest contest2, String title) {
        ContestInformation info = new ContestInformation();
        info.setContestTitle(title);
        info.setContestURL("http://pc2.ecs.csus.edu/pc2");
        info.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);

        contest2.addContestInformation(info);
    }

    public String createSampleDataFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = { "25", "50", "-25", "0" };
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        return filename;
    }

    public String createSampleAnswerFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = { "The sum of the integers is 75" };
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        return filename;
    }

    /**
     * Write array to PrintWriter.
     * 
     * @param writer
     * @param datalines
     */
    public void writeLines(PrintWriter writer, String[] datalines) {
        for (String s : datalines) {
            writer.println(s);
        }
    }

    /**
     * Add and Write sample judge's data and answer file for problem.
     * 
     * @param contest
     * @param problem
     * @throws FileNotFoundException
     */
    void addDataFiles(IInternalContest contest, String testDirectory, Problem problem) throws FileNotFoundException {

        String dataDirName = testDirectory + File.separator + "data";
        String dataName = dataDirName + File.separator + "sumit.dat";
        String answerName = dataDirName + File.separator + "sumit.ans";

        new File(dataDirName).mkdirs();

        if (!new File(dataName).exists()) {
            createSampleDataFile(dataName);
        }

        if (!new File(answerName).exists()) {
            createSampleAnswerFile(answerName);
        }

        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        dataFiles.setJudgesDataFile(new SerializedFile(dataName));
        dataFiles.setJudgesAnswerFile(new SerializedFile(answerName));
        problem.setAnswerFileName("sumit.ans");
        problem.setDataFileName("sumit.dat");

        contest.updateProblem(problem, dataFiles);
    }
    
    public String createSumitDataFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = { "25", "50", "-25", "0" };
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        if (debugMode) {
            System.out.println("createSampleDataFile wrote judge's data file to file to " + filename);
        }
        return filename;
    }

    /**
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public String createSumitAnswerFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = { "The sum of the integers is 75" };
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        if (debugMode) {
            System.out.println("createSampleAnswerFile wrote answer file to " + filename);
        }
        return filename;
    }

   public void addInternalValidator(IInternalContest contest, Problem problem, int optionNumber) {
        
       problem.setValidatorType(VALIDATOR_TYPE.PC2VALIDATOR);
       
       PC2ValidatorSettings settings = new PC2ValidatorSettings();
       settings.setWhichPC2Validator(optionNumber);
       settings.setIgnoreCaseOnValidation(true);
       settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND + " -pc2 " + settings.getWhichPC2Validator() 
               + " " + settings.isIgnoreCaseOnValidation());
       settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);

       problem.setPC2ValidatorSettings(settings);
        
        contest.updateProblem(problem);
    }

    /**
     * Add and Write sample judge's data and answer file for problem.
     * 
     * @param contest
     * @param problem
     * @param dataFileName
     *            - name of data file
     * @param answerFileName
     *            - name of answer file
     * @throws FileNotFoundException
     */
   public  void addDataFiles(IInternalContest contest, String testDirectory, Problem problem, String dataFileName, String answerFileName) throws FileNotFoundException {

        String dataDirName = testDirectory + File.separator + "data";
        String dataName = dataDirName + File.separator + dataFileName;
        String answerName = dataDirName + File.separator + answerFileName;

        new File(dataDirName).mkdirs();

        if (!new File(dataName).exists()) {
            createSampleDataFile(dataName);
        }

        if (!new File(answerName).exists()) {
            createSampleAnswerFile(answerName);
        }

        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        dataFiles.setJudgesDataFile(new SerializedFile(dataName));
        dataFiles.setJudgesAnswerFile(new SerializedFile(answerName));
        problem.setDataFileName(dataFileName);
        problem.setAnswerFileName(answerFileName);

        if (debugMode) {
            System.out.println("addDataFiles for problem " + problem);
            System.out.println("addDataFiles   loaded data   file to " + dataName);
            System.out.println("addDataFiles   loaded answer file to " + answerName);
        }

        contest.updateProblem(problem, dataFiles);
    }

    public static Account[] getAccounts(IInternalContest contest, ClientType.Type type) {
        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }
    
    public Account[] getAccounts(IInternalContest contest, ClientType.Type type, int siteNumber) {
        Vector<Account> accountVector = contest.getAccounts(type, siteNumber);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Turn test mode on.
     * 
     * @param contest
     */
    public void setCCSTestMode(IInternalContest contest) {
        ContestInformation info = contest.getContestInformation();
        if (info == null) {
            info = new ContestInformation();
        }
        info.setCcsTestMode(true);
        contest.updateContestInformation(info);
    }

    /**
     * Set for Internal CCS validation.
     * 
     * @param contest
     * @param validatorParameters
     * @param problem
     */
    public void setClicsValidation(IInternalContest contest, String validatorParameters, Problem problem) {

        problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
        problem.setOutputValidatorProgramName(Constants.CLICS_VALIDATOR_NAME);
        
        problem.setReadInputDataFromSTDIN(true);

        if (validatorParameters == null) {
            problem.setOutputValidatorCommandLine(Constants.DEFAULT_CLICS_VALIDATOR_COMMAND);
        } else {
            problem.setOutputValidatorCommandLine(Constants.CLICS_VALIDATOR_NAME +" " +validatorParameters);
        }
    }

    /**
     * Create ISumit class that reads from stdin, basename filename must be ISumit.java.
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public String createSampleSumitStdinSource(String filename) throws FileNotFoundException  {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = getSumitStdinSource();
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        return filename;
    }


    private String[] getSumitStdinSource() {
        String[] lines = {

        "public class ISumit { ", //
                "    public static void main(String[] args) { ", //
                "        try { ", //
                "            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1); ", //
                " ", //
                "            String line; ", //
                "            int sum = 0; ", //
                "            int rv = 0; ", //
                "            while ((line = br.readLine()) != null) { ", //
                "                rv = new Integer(line.trim()).intValue(); ", //
                "                if (rv > 0) ", //
                "                    sum = sum + rv; ", //
                "                // System.out.println(line); ", //
                "            } ", //
                "            System.out.print(\"The sum of the integers is \"); ", //
                "            System.out.println(sum); ", //
                "        } catch (Exception e) { ", //
                "            System.out.println(\"Possible trouble reading stdin\"); ", //
                "            System.out.println(\"Message: \" + e.getMessage()); ", //
                "        } ", //
                "    } ", //
                "} ", //
                " ", //

        };

        return lines;

    }
    
    /**
     * Creates sumit program, must use filename Sumit.java.
     * 
     * @deprecated use getSamplesSourceFilename from AbstractTestCase
     * @param filename source file name.
     * @param inputFilename file name for program to read
     * @return name of filename
     * @throws FileNotFoundException
     */
    public String createSampleSumitSource(String filename, String inputFilename) throws FileNotFoundException  {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        String[] datalines = getSumitSourceLines(inputFilename);
        writeLines(writer, datalines);
        writer.close();
        writer = null;
        return filename;
    }
    

    private String[] getSumitSourceLines(String inputfilename) {
        String [] lines = {
                "public class Sumit {", //
                "", //
                "    public void doSum(String filename) {", //
                "        try {", //
                "            RandomAccessFile file = new RandomAccessFile(filename, \"r\");", //
                "            String line;", //
                "            int sum = 0;", //
                "            int rv = 0;", //
                "            while ((line = file.readLine()) != null) {", //
                "                rv = new Integer(line.trim()).intValue();", //
                "                if (rv > 0) {", //
                "                    sum = sum + rv;", //
                "                    // System.out.println(line);", //
                "                }", //
                "            }", //
                "            System.out.print(\"The sum of the integers is \" + sum);", //
                "        } catch (Exception e) {", //
                "            System.out.println(\"Possible trouble reading Sumit.dat\");", //
                "        }", //
                "    }", //
                "", //
                "    public static void main(String[] args) {", //
                "", //
                "       new Sumit().doSum(\"+inputfilename+\"); ", //
                "", //
                "    }", //
                "}", //
                "", //
        };
        return lines;
    }
    
    
    /**
     * Get problem letter for input integer.
     * 
     * getProblemLetter(1) is 'A'
     * 
     * @param id
     *            a one based problem number.
     * @return
     */
    public static String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    /**
     * Get list of problem letters for filter.
     * 
     * @param contest
     * @param filter
     * @return list of letters joined by ", "
     */
    public static String getProblemLetters(IInternalContest contest, Filter filter) {

        ArrayList<String> list = new ArrayList<String>();

        Problem[] problems = contest.getProblems();

        int id = 1;
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                list.add(getProblemLetter(id));
            }
            id++;
        }

        StringBuffer buffer = join(", ", list);

        return buffer.toString();
    }

    /**
     * Join string together.
     * 
     * @param delimiter
     * @param list
     * @return buffer of list joined using delimiter.
     */
    public static StringBuffer join(String delimiter, List<String> list) {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < list.size() - 1; i++) {
            buffer.append(list.get(i));
            buffer.append(delimiter);
        }
        if (list.size() > 0) {
            buffer.append(list.get(list.size() - 1));
        }
        return buffer;
    }

    /**
     * Create a new judged run with a Yes judgement.
     * 
     * @param contest
     * @return
     * @throws RunUnavailableException 
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run createRandomJudgedRunSolved(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {
        
        Run run = createRandomRuns(contest, 1, true, true, true)[0];
        ClientId judge = getRandomAccount(contest,Type.JUDGE).getClientId();
        Judgement yes = getYesJudgement(contest);
        contest.addRun(run);
        return addJudgement(contest, run, yes, judge); 
    }

    private Account getRandomAccount(IInternalContest contest, Type type) {
        Account[] accounts = getAccounts(contest, type);
        int randomIndex = random.nextInt(accounts.length);
        return accounts[randomIndex];
    }
    
    private Account getRandomAccount(IInternalContest contest, Type type, int siteNumber) {
        Account[] accounts = getAccounts(contest, type, siteNumber);
        int randomIndex = random.nextInt(accounts.length);
        return accounts[randomIndex];
    }

    /**
     * Add run test cases.
     * 
     * @param inContest
     * @param run
     * @param count
     */
    public void addTestCase(IInternalContest inContest, Run run, int count) {
        
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord == null){
            throw new RuntimeException("Run has no judgement records "+run);
        }
        
        for (int i = 0; i < count; i++) {
            RunTestCase runTestCaseResult = new RunTestCase(run, judgementRecord, i+1, run.isSolved());
            run.addTestCase (runTestCaseResult);
        }
    }

    public IInternalContest createStandardContest() {
        return createContest(3, 3, 120, 12, true);
    }

    
    public String getSampleFileName(String baseFileName) {
        return "samps" + File.separator + "src" + File.separator + baseFileName;
    }

    /**
     * Create judge data and answer files as internal files.
     * 
     * @param problem
     *            problem for data files
     * @param testCases
     *            number of data/answer file test sets to create.
     * @param dataFileName
     *            name of data file
     * @param answerFileName
     *            name of answer file
     */
    public ProblemDataFiles createProblemDataFiles(Problem problem, int testCases, String dataFileName, String answerFileName) {
        ProblemDataFiles files = new ProblemDataFiles(problem);

        SerializedFile[] dataFiles = new SerializedFile[testCases];
        SerializedFile[] ansFiles = new SerializedFile[testCases];

        SerializedFile datafile = new SerializedFile(dataFileName);
        SerializedFile answerfile = new SerializedFile(answerFileName);

        for (int i = 0; i < ansFiles.length; i++) {
            dataFiles[i] = datafile;
            ansFiles[i] = answerfile;
        }

        files.setJudgesAnswerFiles(ansFiles);
        files.setJudgesDataFiles(dataFiles);

        return files;
    }

    /**
     * Create testCases number of data files (data and answer) as internal files.
     * 
     * @param problem
     *            problem for data files
     * @param testCases
     *            number of test data sets to create.
     */
    public ProblemDataFiles createProblemDataFiles(Problem problem, int testCases) {

        return createProblemDataFiles(problem, testCases, //
                getSampleFile("sumit.dat"), getSampleFile("sumit.ans"));
    }


    private boolean fileNotThere(String name) {
        return !new File(name).isFile();
    }

    /**
     * Add a single data/answer file set to a problemDataFiles.
     * @param problem
     * @param problemDataFiles
     * @param dataFileBaseDirectory
     * @param dataFileName
     * @param answerFileName
     */
    public void addDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String dataFileBaseDirectory, String dataFileName, String answerFileName) {

        // load judge data file
        if (dataFileName != null) {
            String dataFilePath = dataFileBaseDirectory + File.separator + dataFileName;
            if (fileNotThere(dataFilePath)) {
                throw new RuntimeException("Missing data file " + dataFilePath);
            }

            problem.setDataFileName(dataFileName);
            problem.setReadInputDataFromSTDIN(false);

            SerializedFile serializedFile = new SerializedFile(dataFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesDataFile(serializedFile);
        }

        // load judge answer file
        if (answerFileName != null) {
            String answerFilePath = dataFileBaseDirectory + File.separator + answerFileName;
            if (fileNotThere(answerFilePath)) {
                throw new RuntimeException("Missing data file " + answerFilePath);
            }

            problem.setAnswerFileName(answerFileName);

            SerializedFile serializedFile = new SerializedFile(answerFilePath, problem.isUsingExternalDataFiles());
            problemDataFiles.setJudgesAnswerFile(serializedFile);
        }

    }

    /**
     * Load data files into datafiles and problem from files in dataFileBaseDirectory.
     * 
     *  Will remove test data sets, and add data sets from dataFileBaseDirectory.
     *  <br>
     *  Will create new ProblemDataFiles if input files == null.
     * 
     * @param problem problem for the data files.
     * @param files input data files (null to create new ProblemDataFiles from scratch)
     * @param dataFileBaseDirectory directory for data files
     * @param dataExtension extension for data files, ex. "dat"
     * @param answerExtension extension for answer files, ex "ans"
     * @return
     */
    public ProblemDataFiles loadDataFiles(Problem problem, ProblemDataFiles files, String dataFileBaseDirectory, String dataExtension, String answerExtension) {

        if (files == null) {
            files = new ProblemDataFiles(problem);
        } else {
            /**
             * A check. It makes no sense to update an existing ProblemDataFiles for a different Problem.
             */
            if (!files.getProblemId().equals(problem.getElementId())) {
                throw new RuntimeException("problem and data files are not for the same problem " + problem.getElementId() + " vs " + files.getProblemId());
            }
        }

        String[] inputFileNames = Utilities.getFileNames(dataFileBaseDirectory, dataExtension);

        String[] answerFileNames = Utilities.getFileNames(dataFileBaseDirectory, answerExtension);

        if (inputFileNames.length == 0) {
            throw new RuntimeException("No input files with extension " + dataExtension + " in "+dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new RuntimeException("No answer  files with extension " + answerExtension+ " in "+dataFileBaseDirectory);
        }

        if (answerFileNames.length != inputFileNames.length) {
            throw new RuntimeException("Miss match expecting same " + dataExtension + " and " + answerExtension + " files (" + inputFileNames.length + " vs " + answerFileNames.length);
        }

        SerializedFile[] inputFiles = createSerializedFiles(dataFileBaseDirectory, inputFileNames, problem.isUsingExternalDataFiles());
        SerializedFile[] answertFiles = createSerializedFiles(dataFileBaseDirectory, answerFileNames, problem.isUsingExternalDataFiles());
        files.setJudgesDataFiles(inputFiles);
        files.setJudgesAnswerFiles(answertFiles);

        problem.removeAllTestCaseFilenames();
        for (int i = 0; i < answertFiles.length; i++) {
            problem.addTestCaseFilenames(inputFileNames[i], answerFileNames[i]);
        }

        return files;
    }

    /**
     * Create array of serialized files.
     * @param dataFileBaseDirectory
     * @param inputFileNames
     * @param externalFilesFlag
     * @return
     */
    public static SerializedFile[] createSerializedFiles(String dataFileBaseDirectory, String[] inputFileNames, boolean externalFilesFlag) {

        ArrayList<SerializedFile> outfiles = new ArrayList<SerializedFile>();

        for (String name : inputFileNames) {
            String filename = dataFileBaseDirectory + File.separator + name;
            outfiles.add(new SerializedFile(filename, externalFilesFlag));
        }

        return (SerializedFile[]) outfiles.toArray(new SerializedFile[outfiles.size()]);
    }

    public void setAliases(IInternalContest inContest) {

        int idx = 0;
        for (Account account : getTeamAccounts(inContest)) {
            account.setAliasName(GIRL_NAMES[idx]);
            idx++;
        }
    }
    
    // copied from https://stackoverflow.com/questions/40074840/reading-a-csv-file-into-a-array
    public static String[] loadStringArrayFromCSV(String fileName) {
        File file= new File(fileName);

        List<String> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                lines.add(line);
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[lines.size()]);
    }
    /*
     * converts a results.tsv into a semi-colon delimited list
     */
    public static String[] loadExpectedResultsFromTSV(String filename) {
        File file = new File(filename);
        List<String> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);
            inputStream.useDelimiter("\n");

            int linenum = 0;
            while(inputStream.hasNext()){
                linenum++;
                String line= inputStream.next();
                if (linenum == 1) {
                    continue;
                }
                String[] split = line.split("\t");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    sb.append(split[i]+";");
                }
                sb.deleteCharAt(sb.lastIndexOf(";")); // remove trailing ;
                lines.add(sb.toString());
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[lines.size()]);
        
    }
    
    /**
     * add run to list of runs in a contest.
     * 
     * Files found in runInfoLine, comma delmited
     * 
     * <pre>
     * 0 - run id, int
     * 1 - team id, int
     * 2 - problem letter, char
     * 3 - elapsed, int
     * 4 - solved, String &quot;Yes&quot; or No
     * 5 - send to teams, Yes or No
     * 6 - No Judgement index
     * 7 - Validator judgement string
     * 
     * Example:
     * &quot;6,5,A,12,Yes&quot;
     * &quot;6,5,A,12,Yes,Yes&quot;
     * 
     * </pre>
     * 
     * @param contest
     * @param runInfoLine
     * @param computerJudged 
     * @throws Exception 
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public static void addRunFromInfo(IInternalContest contest, String runInfoLine, boolean computerJudged) throws Exception {

        // get 5th judge
        ClientId judgeId = contest.getAccounts(Type.JUDGE).elementAt(4).getClientId();
        
        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement[] judgement = contest.getJudgements();
        Judgement noJudgement = null;
        
        for (int i = 0; i < judgement.length; i++) {
            if (judgement[i].getAcronym().equals("WA")) {
                noJudgement = judgement[i];
                break;
            }
        }
        
        String[] data = runInfoLine.split(",");
        
        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);
        boolean solved = data[4].equals("Yes");
        
        boolean sendToTeams = true;
        if (data.length > 5){
            sendToTeams = data[5].equals("Yes");
        }
        if (data.length > 6) {
            noJudgement = contest.getJudgements()[getIntegerValue(data[6])];
        }
        
        String validatorJudgementString = null;
        if (data.length > 7) {
            validatorJudgementString = data[7];
        }

        int problemIndex = probLet.charAt(0) - 'A';
        Problem problem = problemList[problemIndex];
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

        Run run = new Run(clientId, languageId, problem);
        run.setNumber(runId);
        run.setElapsedMins(elapsed);
        run.setEntryPoint("Foo.class");
        ElementId judgementId = noJudgement.getElementId();
        if (solved) {
            judgementId = yesJudgement.getElementId();
        }
        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, computerJudged);
        if (validatorJudgementString != null) {
            judgementRecord.setValidatorResultString(validatorJudgementString);
        }
        judgementRecord.setSendToTeam(sendToTeams);

        try {
            contest.addRun(run);

            checkOutRun(contest, run, judgeId);

            contest.addRunJudgement(run, judgementRecord, null, judgeId);

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new Exception("Unable to add run from run info " + runInfoLine);
        }

    }

    public static void addRunFromInfo(IInternalContest contest, String runInfoLine) throws Exception {
        addRunFromInfo(contest, runInfoLine, false);
    }
    
    
    public static void addRunFromInfo(IInternalContest contest, String[] runInfoLines) throws Exception {
        for (String runInfoLine  : runInfoLines) {
            addRunFromInfo(contest, runInfoLine, false);
        }
    }

    /**
     * Assign unique reservation id to account.
     * 
     * Loops through team accounts and assigned external id (Reservation ID) sequential numbers/ids starting at startId
     * 
     * @param contest
     * @param startId
     *            - start with id
     */
    public static void assignReservationIds(IInternalContest contest, int startId) {

        int id = startId;
        for (Account account : getTeamAccounts(contest)) {
            account.setExternalId(Integer.toString(id));
            id++;
        }
    }

    public Vector<Account> generateNewAccounts(IInternalContest contest, Type type, int count) {
        Vector<Account> accounts = contest.generateNewAccounts(type.toString(), count, true);
        return accounts;
    }

    /**
     * Get account at current site.
     * 
     * @param contest
     * @param administrator
     * @param clientNumber
     * @return
     */
    public Account getAccount(IInternalContest contest, Type type, int clientNumber) {
        return   contest.getAccount(new ClientId(contest.getSiteNumber(), type, clientNumber));
    }
    
    public String getTestSampleContestDirectory(String dirname) {
        return getSampleContestsDirectory() + File.separator + dirname;
    }

    public String getSampleContestsDirectory() {
        return "samps" + File.separator + "contests";
    }
    
    /**
     * Load contest from sample contets
     * @param contest
     * @param sampleName name for directory under samps/contests/
     * @return contest model
     * @throws Exception
     */
    public IInternalContest loadSampleContest(IInternalContest contest, String sampleName) throws Exception {

        IContestLoader loader = new ContestSnakeYAMLLoader();
        if (contest == null) {
            contest = new InternalContest();
        }

        try {
            String cdpDir = getTestSampleContestDirectory(sampleName);
            loader.initializeContest(contest, new File( cdpDir));
            return contest;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }
    
}
