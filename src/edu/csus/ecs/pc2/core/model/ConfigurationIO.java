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
 * Routines to safe and load configuration/Contest.
 * 
 * @author pc2@ecs.csus.edu
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

    public boolean loadFromDisk(int siteNumber, IContest contest, Log log) {

        Configuration configuration = new Configuration();
        
        try {

            if (configuration.loadFromDisk(getFileName())) {
                
                ConfigKeys key;

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
                            Problem problem = contest.getProblem(problemDataFiles2.getElementId());
                            if (problem != null) {
                                contest.addProblem(problem, problemDataFiles2);
                                count++;
                            } else {
                                log.warning("Could not find problem for problemDataFiles id=" + problemDataFiles2.getElementId());
                            }

                            log.info("Loaded " + count + " " + key.toString().toLowerCase());
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

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts(IContest contest) {

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

    public void saveToDisk(int siteNumber, IContest contest, Log log) throws IOException {

        Configuration configuration = new Configuration();

        configuration.add(ConfigKeys.ACCOUNTS, getAllAccounts(contest));
        configuration.add(ConfigKeys.CONTEST_TIME, contest.getContestTime());
        configuration.add(ConfigKeys.CONTEST_TIME_LIST, contest.getContestTimes());
        // configuration.add(ConfigKeys.GENERAL_PROBLEM, contest.huh());
        configuration.add(ConfigKeys.PROBLEM_DATA_FILES, contest.getProblemDataFiles());
        configuration.add(ConfigKeys.JUDGEMENTS, contest.getJudgements());
        configuration.add(ConfigKeys.LANGUAGES, contest.getLanguages());
        configuration.add(ConfigKeys.PROBLEMS, contest.getProblems());
        configuration.add(ConfigKeys.SITES, contest.getSites());

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
