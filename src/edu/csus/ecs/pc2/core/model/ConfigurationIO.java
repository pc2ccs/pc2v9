package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;

/**
 * Routines to safe and load configuration/InternalContest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConfigurationIO {

    /**
     * Keys for Configuration settings.
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum ConfigKeys {
        /**
         * 
         */
        SITE_NUMBER,
        /**
         * 
         */
        PROBLEMS,
        /**
         * 
         */
        LANGUAGES,
        /**
         * 
         */
        CONTEST_TIME,
        /**
         * 
         */
        SITES,
        /**
         * 
         */
        ACCOUNTS,
        /**
         * 
         */
        JUDGEMENTS,
        /**
         * 
         */
        PROBLEM_DATA_FILES,
        /**
         * 
         */
        GENERAL_PROBLEM,
        /**
         * Balloon Settings List.
         * 
         * A list per site of Balloon Settings.
         */
        CONTEST_TIME_LIST,
        /**
         * InternalContest Information
         */
        CONTEST_INFORMATION,
        /**
         * InternalContest Client Settings
         */
        CLIENT_SETTINGS_LIST,
        /**
         * Balloon Settings List
         */
        BALLOON_SETTINGS_LIST,
        /**
         * 
         */
        GROUPS,
        /**
         * 
         */
        PROFILE,
    }

    private String directoryName = "db";

    /**
     * 
     * @param dirname
     */
    private ConfigurationIO(String dirname) {
        this.directoryName = dirname;
        Utilities.insureDir(dirname);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public ConfigurationIO(int siteNumber) {
        this("db." + siteNumber);
    }

    public boolean loadFromDisk(int siteNumber, IInternalContest contest, Log log) {

        Configuration configuration = new Configuration();
        
        try {

            if (configuration.loadFromDisk(getFileName())) {
                
                ConfigKeys key;
                
                key = ConfigKeys.SITE_NUMBER;
                if (configuration.containsKey(key)) {
                    Integer diskSiteNumber = (Integer) configuration.get(key.toString());
                    
                    if (diskSiteNumber.intValue() != siteNumber) {
                        System.err.println("FATAL ERROR Attempted to load site "+siteNumber+" from Site "+diskSiteNumber);
                        log.info("FATAL ERROR Attempted to load site "+siteNumber+" from Site "+diskSiteNumber);
                        System.exit(22);
                    }
                    
                    contest.setSiteNumber(diskSiteNumber);
                    log.info("Loading site number " + diskSiteNumber);
                } else {
                    System.err.println("WARNING Attempted to load site "+siteNumber+" but key "+ConfigKeys.SITE_NUMBER+" not found");
                    log.info("WARNING Attempted to load site "+siteNumber+" but key "+ConfigKeys.SITE_NUMBER+" not found");
                }

                try {
                    key = ConfigKeys.LANGUAGES;
                    if (configuration.containsKey(key)) {
                        Language[] languages = (Language[]) configuration.get(key.toString());
                        for (Language language : languages) {
                            contest.addLanguage(language);
                        }
                        log.info("Loaded " + languages.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading languages ", e);
                }

                try {
                    key = ConfigKeys.PROBLEMS;
                    if (configuration.containsKey(key)) {
                        Problem[] problems = (Problem[]) configuration.get(key.toString());
                        for (Problem problem : problems) {
                            contest.addProblem(problem);
                        }
                        log.info("Loaded " + problems.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading problems ", e);
                }

                try {
                    key = ConfigKeys.CONTEST_TIME_LIST;
                    if (configuration.containsKey(key)) {
                        ContestTime[] contestTimes = (ContestTime[]) configuration.get(key.toString());
                        for (ContestTime contestTime : contestTimes) {
                            contest.addContestTime(contestTime);
                        }
                        log.info("Loaded " + contestTimes.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading contest times ", e);
                }
                

                try {
                    key = ConfigKeys.BALLOON_SETTINGS_LIST;
                    if (configuration.containsKey(key)) {
                        BalloonSettings [] balloonSettings = (BalloonSettings []) configuration.get(key.toString());
                        for (BalloonSettings balloonSetting : balloonSettings){
                            contest.addBalloonSettings(balloonSetting);
                        }
                        log.info("Loaded " + balloonSettings.length + " " + key.toString().toLowerCase());
                    } 
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading contest information/title ", e);
                } 

                try {
                    key = ConfigKeys.CONTEST_TIME;
                    if (configuration.containsKey(key)) {
                        ContestTime contestTime = (ContestTime) configuration.get(key.toString());
                        contest.addContestTime(contestTime);
                        log.info("Loaded " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading contest time ", e);
                }

                try {
                    key = ConfigKeys.GENERAL_PROBLEM;
                    if (configuration.containsKey(key)) {
                        Problem genProblem = (Problem)  configuration.get(key.toString());
                        contest.setGeneralProblem(genProblem);
                        log.info("Loaded " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading general problem ", e);
                }
                
                try {
                    key = ConfigKeys.PROFILE;
                    if (configuration.containsKey(key)) {
                        Profile profile = (Profile) configuration.get(key.toString());
                        contest.setProfile(profile);
                        log.info("Loaded " + key.toString().toLowerCase());
                    } else {
                        Profile profile = createNewProfile();
                        contest.setProfile(profile);
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading general problem ", e);
                }
                
                try {
                    key = ConfigKeys.ACCOUNTS;
                    if (configuration.containsKey(key)) {
                        Account[] accounts = (Account[]) configuration.get(key.toString());
                        for (Account account : accounts) {
                            contest.addAccount(account);
                        }
                        log.info("Loaded " + accounts.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading accounts ", e);
                }

                try {
                    key = ConfigKeys.JUDGEMENTS;
                    if (configuration.containsKey(key)) {
                        Judgement[] judgements = (Judgement[]) configuration.get(key.toString());
                        for (Judgement judgement : judgements) {
                            contest.addJudgement(judgement);
                        }
                        log.info("Loaded " + judgements.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading judgements ", e);
                }
                
                try {
                    key = ConfigKeys.PROBLEM_DATA_FILES;
                    if (configuration.containsKey(key)) {
                        ProblemDataFiles[] problemDataFiles = (ProblemDataFiles[]) configuration.get(key.toString());
                        int count = 0;

                        for (ProblemDataFiles problemDataFiles2 : problemDataFiles) {
                            Problem problem = contest.getProblem(problemDataFiles2.getProblemId());
                            if (problem != null) {
                                contest.updateProblem(problem, problemDataFiles2);
                                count++;
                            } else {
                                log.warning("Could not find problem for problemDataFiles problem id=" + problemDataFiles2.getProblemId());
                            }

                            log.info("Loaded " + count + " of " + problemDataFiles.length + " " + key.toString().toLowerCase());
                        }
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading problem data files ", e);
                }
                
                try {
                    key = ConfigKeys.SITES;
                    if (configuration.containsKey(key)) {
                        Site[] sites = (Site[]) configuration.get(key.toString());
                        for (Site site : sites) {
                            contest.addSite(site);
                        }
                        log.info("Loaded " + sites.length + " " + key.toString().toLowerCase());
                    } 
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading sites ", e);
                }   
                
                try {
                    key = ConfigKeys.CONTEST_INFORMATION;
                    if (configuration.containsKey(key)) {
                        ContestInformation contestInformation = (ContestInformation) configuration.get(key.toString());
                        contest.addContestInformation(contestInformation);
                        log.info("Loaded Contest Information " + contestInformation.getContestTitle());
                    } 
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading contest information/title ", e);
                } 
                

                
                try {
                    key = ConfigKeys.CLIENT_SETTINGS_LIST;
                    if (configuration.containsKey(key)) {
                        ClientSettings [] clientSettingsList =  (ClientSettings[]) configuration.get(key.toString());
                        for (ClientSettings clientSettings : clientSettingsList) {
                            contest.addClientSettings(clientSettings);
                        }
                        log.info("Loaded " + clientSettingsList.length + " " + key.toString().toLowerCase());
                    } 
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while updating client settings ", e);
                }   

                try {
                    key = ConfigKeys.GROUPS;
                    if (configuration.containsKey(key)) {
                        Group[] groups = (Group[]) configuration.get(key.toString());
                        for (Group group : groups) {
                            contest.addGroup(group);
                        }
                        log.info("Loaded " + groups.length + " " + key.toString().toLowerCase());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Exception while loading judgements ", e);
                }

                return true;
                
            } else {
                return false;
            }
            
        } catch (FileNotFoundException fileNotFoundException){
            log.info("No configuration file exists "+getFileName());
            return false;
        } catch (Exception e) {
            log.log(Log.WARNING, "Loading configuration from disk", e);
            return false;
        }
    }
    
    private Profile createNewProfile() {
        Profile profile = new Profile("Contest");
        profile.setDescription("(No description, yet)");
        return profile;
    }


    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts(IInternalContest contest) {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (contest.getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = contest.getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accounts = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accounts;
    }

    public void saveToDisk(IInternalContest contest, Log log) throws IOException {

        Configuration configuration = new Configuration();
        
        configuration.add(ConfigKeys.SITE_NUMBER, new Integer(contest.getSiteNumber()));
        configuration.add(ConfigKeys.ACCOUNTS, getAllAccounts(contest));
        configuration.add(ConfigKeys.CONTEST_TIME, contest.getContestTime());
        configuration.add(ConfigKeys.CONTEST_TIME_LIST, contest.getContestTimes());
        configuration.add(ConfigKeys.BALLOON_SETTINGS_LIST, contest.getBalloonSettings());
        if (contest.getGeneralProblem() != null){
            configuration.add(ConfigKeys.GENERAL_PROBLEM, contest.getGeneralProblem());
        }
        configuration.add(ConfigKeys.PROBLEM_DATA_FILES, contest.getProblemDataFiles());
        configuration.add(ConfigKeys.JUDGEMENTS, contest.getJudgements());
        configuration.add(ConfigKeys.LANGUAGES, contest.getLanguages());
        configuration.add(ConfigKeys.PROBLEMS, contest.getProblems());
        configuration.add(ConfigKeys.SITES, contest.getSites());
        
        configuration.add(ConfigKeys.CONTEST_INFORMATION, contest.getContestInformation());
        configuration.add(ConfigKeys.CLIENT_SETTINGS_LIST, contest.getClientSettingsList());
        configuration.add(ConfigKeys.GROUPS, contest.getGroups());
        configuration.add(ConfigKeys.PROFILE, contest.getProfile());

        configuration.writeToDisk(getFileName());

        configuration = null;
    }

    /**
     * Holds Configuration data
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class Configuration {

        private Hashtable<String, Object> configItemHash = new Hashtable<String, Object>();

        public boolean add(ConfigKeys key, Serializable object) {
            if (object == null) {
                return false;
            } else {
                configItemHash.put(key.toString(), object);
                return true;
            }
        }

        public boolean containsKey(ConfigKeys key) {
            return configItemHash.containsKey(key.toString());
        }

        public Object get(String key) {
            return configItemHash.get(key);
        }

        /**
         * Write the run data to disk.
         * 
         * @throws IOException
         */
        public boolean writeToDisk(String fileName) throws IOException {
            return Utilities.writeObjectToFile(getFileName(), configItemHash);
        }

        @SuppressWarnings("unchecked")
        public boolean loadFromDisk(String filename) throws IOException, ClassNotFoundException {
            Object readObject = Utilities.readObjectFromFile(filename);
            if (readObject instanceof Hashtable) {
                configItemHash = (Hashtable) readObject;
                return true;
            }
            return false;
        }
    }

    public String getFileName() {
        return getDirectoryName() + File.separator + "settings.dat";
    }

}
