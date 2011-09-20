package edu.csus.ecs.pc2.core.archive;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.list.ClientSettingsComparator;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Methods to format packet contents.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class PacketFormatter {

    private PacketFormatter() {
        //
    }

    public static String summaryFormat(Packet packet) {
        StringBuffer buffer = new StringBuffer();

        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                String className = prop.get(element).getClass().getName();
                className = className.replaceFirst("edu.csus.ecs.pc2.core.model.", "");
                className = className.replaceFirst("edu.csus.ecs.pc2.core.", "");
                buffer.append(className);
                buffer.append(' ');
                if (prop.size() == 1) {
                    buffer.append(prop.get(element));
                }
            }
        } else {

            buffer.append("  Contains: ");
            buffer.append(obj.toString());
            buffer.append(' ');
            buffer.append(obj);
        }

        return buffer.toString();
    }
    
//    private static String getClassName (Class inClass){
//        StringBuffer buffer = new StringBuffer();
//
//        String className = inClass.getClass().getName();
//        className = className.replaceFirst("edu.csus.ecs.pc2.core.model.", "");
//        className = className.replaceFirst("edu.csus.ecs.pc2.core.", "");
//        buffer.append(className);
//        
//        return buffer.toString();
//    }
    
    /**
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected static class ObjectWrapper {

        private String string;

        private String key;

        protected ObjectWrapper(String key) {
            this.key = key;
        }

        public ObjectWrapper(String key, Object object) {
            this(key);
            if (object instanceof String) {
                string = "(String) " + (String) object;
            } else if (object instanceof Integer) {
                string = "(Integer) " + (Integer) object;
            } else if (object instanceof Date) {
                string = "(Date) " + (Date) object;
            } else {
                string = "Object:" + object.toString();
            }
        }

        @Override
        public String toString() {
            return key + "=" + string;
        }
    }
    
    public static DefaultMutableTreeNode buildContentTree(DefaultMutableTreeNode node, Packet packet){
        
        DefaultMutableTreeNode subNode = null;
        
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            
            String [] keys = (String[]) prop.keySet().toArray(new String[prop.keySet().size()]);
            Arrays.sort(keys);
            
            subNode = new DefaultMutableTreeNode(new ObjectWrapper("Created", packet.getCreateDate()));
            node.add(subNode);
            
            for (String name : keys){
                subNode = createTree (name+" ", prop.get(name));
                if (subNode == null){
                    subNode = new DefaultMutableTreeNode(new ObjectWrapper(name, prop.get(name)));
                }
                node.add(subNode);
            }
        } else {
            subNode = new DefaultMutableTreeNode(new ObjectWrapper(obj.getClass().getName(),obj));
            node.add(subNode);
        }
        
        return node;
        
    }

    /**
     * Create tree. 
     * 
     * @param object
     * @param object2 
     * @param object2 
     * @return null if no object tree found/created.
     */
    private static DefaultMutableTreeNode createTree(String key, Object object) {
        
        if (object instanceof Profile){

            Profile profile = (Profile) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Profile: "+profile.getName());
            DefaultMutableTreeNode child;
            
            child = new DefaultMutableTreeNode("description = "+profile.getDescription());
            node.add(child);
       
            child = new DefaultMutableTreeNode("path = "+profile.getProfilePath());
            node.add(child);
            
            child = new DefaultMutableTreeNode("contest Id = "+profile.getContestId());
            node.add(child);
            
            child = new DefaultMutableTreeNode("ElementId: "+profile.getElementId().toString());
            node.add(child);
            return node;
        }
        
        if (object instanceof Site){

            Site site = (Site) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Site: " + site.getSiteNumber() + " title: " + site.getDisplayName());
            DefaultMutableTreeNode child;

            Properties connectionInfo = site.getConnectionInfo();
            Enumeration<?> enumeration = connectionInfo.keys();

            while (enumeration.hasMoreElements()) {
                String siteKey = (String) enumeration.nextElement();
                String value = (String) connectionInfo.get(siteKey);
                child = new DefaultMutableTreeNode(siteKey + "=" + value);
                node.add(child);
            }

            child = new DefaultMutableTreeNode(key + "ElementId: "+site.getElementId().toString());
            node.add(child);

            return node;
        }
        
        if (object instanceof Profile []){

            Profile [] profiles = (Profile []) object;
            Arrays.sort(profiles, new ProfileComparatorByName());
            
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Profile list: " + profiles.length+" profiles");
            DefaultMutableTreeNode child;
            
            for (Profile profile : profiles){
                
                child = createTree("", profile);
                node.add(child);
            }

            return node;
        }
        
        if (object instanceof Account []){

            Account [] accounts = (Account []) object;
            Arrays.sort(accounts,new AccountComparator());
            
            String breakdown = getAccountBreakdown(accounts);
            
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Account list: " + accounts.length+" accounts total.  "+breakdown);
            DefaultMutableTreeNode child;
            
            int maxSitesForAccounts = getNumberOfSites(accounts);
            if (maxSitesForAccounts > 1){
                for (int i = 0; i < maxSitesForAccounts; i++){
                    breakdown = getAccountBreakdown(accounts, i+1);
                    child = new DefaultMutableTreeNode("  Site: "+(i+1)+" "+breakdown);
                    node.add(child);
                }
            }
            
            for (Account account : accounts){
                
                child = createTree("", account);
                node.add(child);
            }

            return node;
        }

        
        if (object instanceof ClientId []){

            ClientId [] clientIds = (ClientId []) object;
            Arrays.sort(clientIds, new ClientIdComparator());
            
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ClientId list: " + clientIds.length+" ClientIds");
            DefaultMutableTreeNode child;
            
            
            for (ClientId clientId : clientIds){
                
                child = createTree("", clientId);
                node.add(child);
            }

            return node;
        }

        if (object instanceof GregorianCalendar) {

            GregorianCalendar calendar = (GregorianCalendar) object;
            long secondsOffset = calendar.getTime().getTime() / 1000;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Date offset: " + secondsOffset);
            return node;

        }
        
        if (object instanceof Account){

            DefaultMutableTreeNode child;

            Account account = (Account) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Account: " + account);
            
            child = createTree("", account.getClientId());
            node.add(child);
            
            child = new DefaultMutableTreeNode("     Name: " + account.getDisplayName());
            node.add(child);

            child = new DefaultMutableTreeNode("    Group: " + account.getGroupId());
            node.add(child);

            child = new DefaultMutableTreeNode("     Site: " + account.getSiteNumber());
            node.add(child);
            
            child = new DefaultMutableTreeNode("ElementId: " + account.getElementId().toString());
            node.add(child);

            return node;
        }


        
        if (object instanceof ClientId){

            DefaultMutableTreeNode child;

            ClientId clientId = (ClientId) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Client Id: " + clientId.getClientType().toString() + " "+clientId.getClientNumber());
            
            child = new DefaultMutableTreeNode("  Triplet: " + clientId.getTripletKey());
            node.add(child);
            
            child = new DefaultMutableTreeNode("     Site: " + clientId.getSiteNumber());
            node.add(child);

            return node;
        }
        
        if (object instanceof Language) {

            DefaultMutableTreeNode child;

            Language language = (Language) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Language: " + language.getDisplayName());

            child = new DefaultMutableTreeNode("Name: " + language.getDisplayName());
            node.add(child);
            child = new DefaultMutableTreeNode("Compile: " + language.getCompileCommandLine());
            node.add(child);
            child = new DefaultMutableTreeNode("Executable Mask: " + language.getExecutableIdentifierMask());
            node.add(child);
            child = new DefaultMutableTreeNode("Execute: " + language.getProgramExecuteCommandLine());
            node.add(child);
            child = new DefaultMutableTreeNode("ElementId: " + language.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Judgement) {

            DefaultMutableTreeNode child;
            Judgement judgement = (Judgement) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Judgement: " + judgement.getDisplayName());
            child = new DefaultMutableTreeNode("ElementId: " + judgement.getElementId().toString());
            node.add(child);

            return node;
        }
        

        if (object instanceof ContestTime[]) {

            ContestTime[] times = (ContestTime[]) object;
            Arrays.sort(times, new ContestTimeComparator());

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ContestTimes list: " + times.length + " times.");
            DefaultMutableTreeNode child;

            for (ContestTime time : times) {
                child = createTree("", time);
                node.add(child);
            }
            
            return node;
        }
        
        if (object instanceof ContestTime) {

            DefaultMutableTreeNode child;
            ContestTime contestTime = (ContestTime) object;

            String status = "STOPPED";
            if (contestTime.isContestRunning()) {
                status = "RUNNING";
            }
            
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ContestTime: Site " + contestTime.getSiteNumber() + " " + status+" remaining "+contestTime.getRemainingTimeStr());

            child = new DefaultMutableTreeNode("Remaining: " + contestTime.getRemainingTimeStr());
            node.add(child);

            child = new DefaultMutableTreeNode("   Length: " + contestTime.getContestLengthStr());
            node.add(child);

            child = new DefaultMutableTreeNode("  Elapsed: " + contestTime.getElapsedTimeStr());
            node.add(child);

            child = new DefaultMutableTreeNode("ElementId: " + contestTime.getElementId().toString());
            node.add(child);

            child = new DefaultMutableTreeNode("ElementId: " + contestTime.getElementId().toString());
            node.add(child);

            return node;
        }
        
        return createOtherTrees (key, object);
        
    }
    
    protected static void addNonNull(DefaultMutableTreeNode node, DefaultMutableTreeNode child){
        if (child != null){
            node.add(child);
        }
    }

    protected static String getAccountBreakdown(Account[] accounts) {
        return getAccountBreakdown(accounts, 0);
    }
    
    /**
     * Get maximum number of sites from the input accounts.
     * 
     * Unlike using IInternalContest which retrieves the maximum
     * total number of sites, this just finds the maximum site
     * number found in the list of accounts.
     * 
     * @param accounts
     * @return 0 if no accounts, the max site number.
     */
    protected static int getNumberOfSites (Account[] accounts) {
        int num = 0;
        for (Account account : accounts){
            num = Math.max(num, account.getSiteNumber());
        }
        
        return num;
    }
        
    protected static String getAccountBreakdown(Account[] accounts, int siteNumber) {
        
       int [] totals = new int[ClientType.Type.values().length];
       
       for (Account account : accounts){
           if (siteNumber == 0 || siteNumber == account.getSiteNumber()){
               ClientType.Type type = account.getClientId().getClientType();
           totals[type.ordinal()] ++;
           }
       }
       
       StringBuffer sb = new StringBuffer();
       
       int totalAccountsFound = 0;
       
       ClientType.Type[] types = ClientType.Type.values();
       for (ClientType.Type type : types) {
           int count = totals[type.ordinal()];
           if (count > 0){
               totalAccountsFound += count;
               sb.append(count);
               sb.append(' ');
               sb.append(type);
               sb.append(' ');
           }
       }
       
       if (totalAccountsFound == 0){
           return "no accounts";
       }

       return new String(sb).trim();
    }

    /**
     * Create tree. 
     * 
     * @param object
     * @return null if no object tree found/created.
     */
    private static DefaultMutableTreeNode createOtherTrees(String key, Object object) {

        if (object instanceof Run) {

            DefaultMutableTreeNode child;

            Run run = (Run) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + " Run: " + run);

            child = new DefaultMutableTreeNode("      Team: " + run.getSubmitter());
            node.add(child);
            child = new DefaultMutableTreeNode("   elapsed: " + run.getElapsedMins());
            node.add(child);
            child = new DefaultMutableTreeNode("    Solved: " + run.isSolved());
            node.add(child);
            child = new DefaultMutableTreeNode("   Problem: " + run.getProblemId());
            node.add(child);
            child = new DefaultMutableTreeNode("    Judged: " + run.isJudged());
            node.add(child);
            child = new DefaultMutableTreeNode(" Send2Team: " + run.isSendToTeams());
            node.add(child);
            child = new DefaultMutableTreeNode("    Deleted: " + run.isDeleted());
            node.add(child);
            child = new DefaultMutableTreeNode("   Language: " + run.getLanguageId());
            node.add(child);
            child = new DefaultMutableTreeNode("  ElementId: " + run.isDeleted());
            node.add(child);

            return node;
        }

        if (object instanceof Clarification) {

            DefaultMutableTreeNode child;

            Clarification clarification = (Clarification) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Clarification: " + clarification);

            child = new DefaultMutableTreeNode("      Team: " + clarification.getSubmitter());
            node.add(child);
            child = new DefaultMutableTreeNode("   elapsed: " + clarification.getElapsedMins());
            node.add(child);
            child = new DefaultMutableTreeNode("   Problem: " + clarification.getProblemId());
            node.add(child);
            child = new DefaultMutableTreeNode("  Answered: " + clarification.isAnswered());
            node.add(child);
            child = new DefaultMutableTreeNode("   For All: " + clarification.isSendToAll());
            node.add(child);
            child = new DefaultMutableTreeNode("  ElementId: " + clarification.isDeleted());
            node.add(child);

            return node;
        }

        if (object instanceof Problem) {

            DefaultMutableTreeNode child;

            Problem problem = (Problem) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Problem: " + problem.getDisplayName());

            child = new DefaultMutableTreeNode("Name: " + problem.getDisplayName());
            node.add(child);

            child = new DefaultMutableTreeNode("AnswerFileName " + problem.getAnswerFileName());
            node.add(child);
            child = new DefaultMutableTreeNode("DataFileName " + problem.getDataFileName());
            node.add(child);
            child = new DefaultMutableTreeNode("Active " + problem.isActive());
            node.add(child);
            child = new DefaultMutableTreeNode("ComputerJudged " + problem.isComputerJudged());
            node.add(child);
            child = new DefaultMutableTreeNode("ManualReview " + problem.isManualReview());
            node.add(child);
            child = new DefaultMutableTreeNode("PrelimaryNotification " + problem.isPrelimaryNotification());
            node.add(child);
            child = new DefaultMutableTreeNode("ValidatorCommandLine " + problem.getValidatorCommandLine());
            node.add(child);
            child = new DefaultMutableTreeNode("ValidatorProgramName " + problem.getValidatorProgramName());
            node.add(child);

            // TODO code these
//            problem.getTimeOutInSeconds();
//            problem.getWhichPC2Validator();
//            problem.isHideOutputWindow();
//            problem.isIgnoreSpacesOnValidation();
//            problem.isReadInputDataFromSTDIN();
//            problem.isShowCompareWindow();
//            problem.isPrelimaryNotification();

            child = new DefaultMutableTreeNode("ElementId: " + problem.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Language[]) {

            Language[] languages = (Language[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Language list: " + languages.length + " languages");
            DefaultMutableTreeNode child;

            for (Language language : languages) {
                child = createTree("", language);
                node.add(child);
            }

            return node;
        }

        if (object instanceof Run[]) {

            Run[] runs = (Run[]) object;
            Arrays.sort(runs, new RunComparator());

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Run list: " + runs.length + " runs");
            DefaultMutableTreeNode child;

            for (Run run : runs) {
                child = createTree("", run);
                node.add(child);
            }

            return node;
        }

        if (object instanceof Clarification[]) {

            Clarification[] clarifications = (Clarification[]) object;
            Arrays.sort(clarifications, new ClarificationComparator());

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Clarification list: " + clarifications.length + " runs");
            DefaultMutableTreeNode child;

            for (Clarification clarification : clarifications) {
                child = createTree("", clarification);
                node.add(child);
            }

            return node;
        }

        if (object instanceof Judgement[]) {

            Judgement[] judgements = (Judgement[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Judgement list: " + judgements.length + " judgements");
            DefaultMutableTreeNode child;

            for (Judgement judgement : judgements) {

                child = createTree("", judgement);
                node.add(child);
            }

            return node;
        }
        
      if (object instanceof Problem[]) {

            Problem[] problems = (Problem[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Problem list: " + problems.length + " problems");
            DefaultMutableTreeNode child;

            for (Problem problem : problems) {

                child = createTree("", problem);
                node.add(child);
            }

            return node;
        }

        if (object instanceof RunFiles[]) {

            RunFiles[] list = (RunFiles[]) object;
            // TODO RunFilesComparator

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "RunFiles list: " + list.length + " problems");
            DefaultMutableTreeNode child;

            for (RunFiles runFiles : list) {
                child = createTree("", runFiles);
                node.add(child);
            }

            return node;
        }

        if (object instanceof RunFiles) {

            DefaultMutableTreeNode child;

            RunFiles runfiles = (RunFiles) object;
            String mainFileName = runfiles.getMainFile().getName();

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "RunFiles: " + mainFileName + " for " + runfiles.getRunId());

            SerializedFile mainFile = runfiles.getMainFile();

            child = new DefaultMutableTreeNode("mainfile file: " + mainFile.getName() + " " + mainFile.getBuffer().length + " bytes");
            node.add(child);

            if (runfiles.getOtherFiles() != null) {
                for (SerializedFile file : runfiles.getOtherFiles()) {
                    child = new DefaultMutableTreeNode("additional file: " + file.getName() + " " + file.getBuffer().length + " bytes");
                    node.add(child);
                }
            } else {
                child = new DefaultMutableTreeNode("additional file: null ");
                node.add(child);
            }

            child = new DefaultMutableTreeNode("RunID: " + runfiles.getRunId().toString());
            node.add(child);
            child = new DefaultMutableTreeNode("ElementId: " + runfiles.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof ProfileCloneSettings) {

            DefaultMutableTreeNode child;

            ProfileCloneSettings settings = (ProfileCloneSettings) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Clone Settings: " + settings.getName());

            // public char[] getContestPassword());
            // public char[] getNewContestPassword());

            child = new DefaultMutableTreeNode("Name: " + settings.getName());
            node.add(child);
            child = new DefaultMutableTreeNode("Title: " + settings.getContestTitle());
            node.add(child);

            child = new DefaultMutableTreeNode("CopyRuns = =" + settings.isCopyRuns());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyClarifications = =" + settings.isCopyClarifications());
            node.add(child);
            child = new DefaultMutableTreeNode("ResetContestTimes = " + settings.isResetContestTimes());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyAccounts = " + settings.isCopyAccounts());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyContestSettings = =" + settings.isCopyContestSettings());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyGroups = =" + settings.isCopyGroups());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyJudgements = =" + settings.isCopyJudgements());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyLanguages = =" + settings.isCopyLanguages());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyNotifications = =" + settings.isCopyNotifications());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyProblems = =" + settings.isCopyProblems());
            node.add(child);

            return node;
        }

        return createMoreTrees(key, object);
    }
    
    private static DefaultMutableTreeNode createMoreTrees(String key, Object object) {

        if (object instanceof Group) {

            DefaultMutableTreeNode child;

            Group group = (Group) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Group: " + group.getDisplayName());

            child = new DefaultMutableTreeNode("GroupId: " + group.getGroupId());
            node.add(child);
            child = new DefaultMutableTreeNode("Name: " + group.getDisplayName());
            node.add(child);
            child = new DefaultMutableTreeNode("ElementId: " + group.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Group[]) {

            Group[] groups = (Group[]) object;

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Group list: " + groups.length + " Groups");
            DefaultMutableTreeNode child;

            for (Group group : groups) {

                child = createTree("", group);
                node.add(child);
            }

            return node;
        }

        if (object instanceof Site[]) {

            Site[] sites = (Site[]) object;
            Arrays.sort(sites, new SiteComparatorBySiteNumber());

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Site list: " + sites.length + " Sites");
            DefaultMutableTreeNode child;

            for (Site site : sites) {

                child = createTree("", site);
                node.add(child);
            }

            return node;
        }

        return evenMoreTrees(key, object);
        
    }
    
    private static DefaultMutableTreeNode evenMoreTrees(String key, Object object) {

        if (object instanceof ContestInformation) {

            DefaultMutableTreeNode child;

            ContestInformation contestInformation = (ContestInformation) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ContestInformation: " + contestInformation.getContestTitle());

            child = new DefaultMutableTreeNode("Title: " + contestInformation.getContestTitle());
            node.add(child);
            child = new DefaultMutableTreeNode("URL: " + contestInformation.getContestURL());
            node.add(child);

            child = new DefaultMutableTreeNode("Default Clar. Answer: " + contestInformation.getJudgesDefaultAnswer());
            node.add(child);

            NotificationSetting[] notificationSettings = contestInformation.getJudgementNotificationsList().getList();
            child = createTree("", notificationSettings);
            addNonNull(node, child);

            child = new DefaultMutableTreeNode("URL: " + contestInformation.getContestURL());
            node.add(child);

            // TODO code these
            // contestInformation.getMaxFileSize();
            // contestInformation.getScoringProperties();
            // contestInformation.getTeamDisplayMode();
            // contestInformation.isPreliminaryJudgementsTriggerNotifications();
            // contestInformation.isPreliminaryJudgementsUsedByBoard();
            // contestInformation.isSendAdditionalRunStatusInformation();

            return node;
        }
        
        if (object instanceof FinalizeData) {

            DefaultMutableTreeNode child;

            FinalizeData finalizeData = (FinalizeData) object;

            String certified = "No";
            if (finalizeData.isCertified()) {
                certified = "Yes";
            }

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "FinalizeData: certified " + certified);

            child = new DefaultMutableTreeNode("Comment: " + finalizeData.getComment());
            node.add(child);
            child = new DefaultMutableTreeNode("Certified: " + certified);
            node.add(child);

            child = new DefaultMutableTreeNode("Gold rank: " + finalizeData.getGoldRank());
            node.add(child);
            child = new DefaultMutableTreeNode("Silver rank: " + finalizeData.getSilverRank());
            node.add(child);
            child = new DefaultMutableTreeNode("Bronze rank: " + finalizeData.getBronzeRank());
            node.add(child);

            return node;
        }

        if (object instanceof NotificationSetting[]) {

            NotificationSetting[] notificationSettings = (NotificationSetting[]) object;
            // TODO sort

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Client list: " + notificationSettings.length + " NotificationSettings");
            DefaultMutableTreeNode child;

            for (NotificationSetting notificationSetting : notificationSettings) {

                child = createTree("", notificationSetting);
                addNonNull(node, child);
            }

            return node;
        }

        if (object instanceof NotificationSetting) {

            NotificationSetting notificationSetting = (NotificationSetting) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "NotificationSetting: " + notificationSetting);
            DefaultMutableTreeNode child;

            child = new DefaultMutableTreeNode("site = " + notificationSetting.getSiteNumber());
            node.add(child);

            // TODO code
            // notificationSetting.getFinalNotificationNo();
            // notificationSetting.getFinalNotificationYes();
            // notificationSetting.getPreliminaryNotificationNo();
            // notificationSetting.getPreliminaryNotificationYes();

            return node;
        }

        if (object instanceof ClientSettings[]) {

            ClientSettings[] clientSettingss = (ClientSettings[]) object;
            Arrays.sort(clientSettingss, new ClientSettingsComparator());

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ClientSettings list: " + clientSettingss.length + " ClientSettingss");
            DefaultMutableTreeNode child;

            for (ClientSettings clientSettings : clientSettingss) {

                child = createTree("", clientSettings);
                addNonNull(node, child);
            }

            return node;

        }

        if (object instanceof ClientSettings) {

            DefaultMutableTreeNode child;

            ClientSettings setting = (ClientSettings) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ClientSettings: " + setting.getClientId());

            child = new DefaultMutableTreeNode("ClientId: " + setting.getClientId());
            node.add(child);

            child = new DefaultMutableTreeNode("Auto Judging: " + setting.isAutoJudging());
            node.add(child);
            child = new DefaultMutableTreeNode("Site: " + setting.getSiteNumber());
            node.add(child);
            child = createTree("Auto Judging Filter", setting.getAutoJudgeFilter());
            node.add(child);

            // TODO code
            // child = createTree("Balloons", setting.getBalloonList());
            // node.add(child);

            // setting.getNotificationSetting();

            String[] keys = setting.getKeys();
            Arrays.sort(keys);

            for (String name : keys) {
                child = new DefaultMutableTreeNode("Property:" + name + ":" + setting.getProperty(name));
                node.add(child);
            }

            child = new DefaultMutableTreeNode("ElementId: " + setting.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Filter) {

            DefaultMutableTreeNode child;

            Filter filter = (Filter) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Filter: " + filter);

            child = new DefaultMutableTreeNode("Summary: " + filter);
            node.add(child);
            child = new DefaultMutableTreeNode("Filter on: " + filter.isFilterOn());
            node.add(child);

            return node;
        }

        if (object instanceof BalloonSettings[]) {

            BalloonSettings[] balloonSettings = (BalloonSettings[]) object;

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "BalloonSettings list: " + balloonSettings.length + " BalloonSettings");
            DefaultMutableTreeNode child;

            for (BalloonSettings settings : balloonSettings) {

                child = createTree("", settings);
                node.add(child);
            }

            return node;
        }

        if (object instanceof BalloonSettings) {

            DefaultMutableTreeNode child;

            BalloonSettings balloonSettings = (BalloonSettings) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "BalloonSettings: " + balloonSettings);

            child = new DefaultMutableTreeNode("Site Number: " + balloonSettings.getSiteNumber());
            node.add(child);

            child = new DefaultMutableTreeNode("Balloon client: " + balloonSettings.getBalloonClient());
            node.add(child);

            child = new DefaultMutableTreeNode("Enabled: " + balloonSettings.isBalloonsEnabled());
            node.add(child);

            child = new DefaultMutableTreeNode("Include Nos: " + balloonSettings.isIncludeNos());
            node.add(child);

            child = new DefaultMutableTreeNode("Use postscript: " + balloonSettings.isPostscriptCapable());
            node.add(child);

            child = new DefaultMutableTreeNode("Print balloons: " + balloonSettings.isPrintBalloons());
            node.add(child);

            child = new DefaultMutableTreeNode("Email Balloon: " + balloonSettings.isEmailBalloons());
            node.add(child);

            child = new DefaultMutableTreeNode("Balloon email: " + balloonSettings.getEmailContact());
            node.add(child);

            child = createTree("Mail Props", balloonSettings.getMailProperties());
            addNonNull(node, child);

            child = new DefaultMutableTreeNode("Print device:" + balloonSettings.getPrintDevice());
            node.add(child);

            child = new DefaultMutableTreeNode("ElementId: " + balloonSettings.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof ConnectionHandlerID) {

            DefaultMutableTreeNode child;

            ConnectionHandlerID connectionID = (ConnectionHandlerID) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ConnectionId: " + connectionID);

            child = new DefaultMutableTreeNode("Ready to communicate: " + connectionID.isReadyToCommunicate());
            node.add(child);

            return node;
        }

        if (object instanceof ConnectionHandlerID[]) {

            ConnectionHandlerID[] connectionIds = (ConnectionHandlerID[]) object;

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ConnHandId list: " + connectionIds.length + " ConnHandIds");
            DefaultMutableTreeNode child;

            for (ConnectionHandlerID connectionId : connectionIds) {

                child = createTree("", connectionId);
                addNonNull(node, child);
            }

            return node;
        }

        return moreTreesThree(key, object);
    }
    
    private static DefaultMutableTreeNode moreTreesThree(String key, Object object) {

        if (object instanceof ProblemDataFiles) {

            DefaultMutableTreeNode child;

            ProblemDataFiles data = (ProblemDataFiles) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ProblemDataFiles: " + data.getProblemId());

            child = new DefaultMutableTreeNode("problem: " + data.getProblemId());
            node.add(child);

            data.getJudgesDataFile();

            child = createTree("Judge Data File", data.getJudgesDataFile());
            addNonNull(node, child);
            child = createTree("Judge Ans File", data.getJudgesAnswerFile());
            addNonNull(node, child);
            child = createTree("Validator", data.getValidatorFile());
            addNonNull(node, child);

            // TODO code arrays
            // data.getJudgesDataFiles();
            // data.getJudgesAnswerFiles();

            child = new DefaultMutableTreeNode("ElementId: " + data.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof SerializedFile) {
            SerializedFile file = (SerializedFile) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "File: " + file.getName() + " " + file.getBuffer().length + " bytes");
            return node;
        }

        if (object instanceof SerializedFile[]) {

            SerializedFile[] files = (SerializedFile[]) object;

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "SerialFile list: " + files.length + " ProblemDataFiless");
            DefaultMutableTreeNode child;

            for (SerializedFile file : files) {

                child = createTree("", file);
                addNonNull(node, child);
            }

            return node;
        }

        if (object instanceof ProblemDataFiles[]) {

            ProblemDataFiles[] datas = (ProblemDataFiles[]) object;

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "ProblemDataFiles list: " + datas.length + " data files");
            DefaultMutableTreeNode child;

            for (ProblemDataFiles data : datas) {

                child = createTree("", data);
                addNonNull(node, child);
            }

            return node;
        }
        
        if (object instanceof Category[]) {

            Category[] categories = (Category[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Category list: " + categories.length + " categories");
            DefaultMutableTreeNode child;

            for (Category category : categories) {

                child = createTree("", category);
                node.add(child);
            }

            return node;
        }
        
        if (object instanceof Category) {

            DefaultMutableTreeNode child;

            Category category = (Category) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "Category: " + category);
            
            child = new DefaultMutableTreeNode("Name: " + category.getDisplayName());
            node.add(child);

            return node;
        }
        
        return null;
    }
}
