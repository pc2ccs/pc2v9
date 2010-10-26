package edu.csus.ecs.pc2.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.exception.ProfileCloneException;
import edu.csus.ecs.pc2.core.exception.ProfileException;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Profiles Manager.
 * 
 * Provides ways to:
 * <li>load/store text profile information
 * <li>a way to determine whether to prompt user for contest password
 * <li>a ways to save/get the default (current) profile
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileManager {

    public static final String DEFAULT_PROFILE_KEY = "current";

    /**
     * Filename for index of profiles.
     */
    public static final String PROFILE_INDEX_FILENAME = "profiles.properties";

    private static final String ACTIVE_PREFIX = "active=";

    private String propertiesFileName = PROFILE_INDEX_FILENAME;

    private String delimiter = ",";

    public ProfileManager() {
        propertiesFileName = PROFILE_INDEX_FILENAME;
    }

    
    public ProfileManager(String filename) {
        propertiesFileName = filename;
    }

    /**
     * Create Profile from data in filename.
     * 
     * 
     * 
     * @param filename
     * @return
     * @throws IOException
     * @throws ProfileLoadException
     */
    public Profile[] load(String filename) throws IOException, ProfileLoadException {

        if (new File(filename).exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(filename));
            return toProfiles(properties);
        } else {
            throw new FileNotFoundException(filename);
        }
    }
    
    public Profile[] load() throws IOException, ProfileLoadException {
        return load(propertiesFileName);
    }
    
    /**
     * Add a new profile to existing profiles.
     * 
     * Will create and set as default the new profile if not
     * profiles are stored in the profiles.properties file.      
     * @param filename
     * @param profile
     * @throws IOException
     * @throws ProfileLoadException
     */
    public void add (String filename, Profile profile) throws IOException, ProfileLoadException{
        
        if (hasDefaultProfile()) {
            
            Profile[] profiles = load(filename);
            Profile defaultProfile = getDefaultProfile(filename);
            store(filename, profiles, defaultProfile);
            
        } else {
            storeDefaultProfile(filename, profile);
        }
    }
    
    /**
     * Add a new profile to existing profiles.
     * 
     * Will create and set as default the new profile if not
     * profiles are stored in the profiles.properties file. 
     * 
     * @param profile
     * @throws IOException
     * @throws ProfileLoadException
     */
    public void add (Profile profile) throws IOException, ProfileLoadException{
        add (propertiesFileName, profile);
    }

    /**
     * Determines whether the input profile can be read using contest password.
     * 
     * @param profile
     * @return true if profile
     * @throws ProfileException
     */
    public boolean isProfileAvailable(Profile profile, char[] contestPassword) throws ProfileException {
        return getProfileStorage(profile, contestPassword) != null;
    }
    
    /**
     * Returns storage location for input profile.
     * 
     * Can be used to check whether contest password is valid.
     * 
     * @param profile
     * @param contestPassword
     * @return
     * @throws ProfileException
     */
    public IStorage getProfileStorage(Profile profile, char[] contestPassword) throws ProfileException {

        if (profile == null) {
            throw new IllegalArgumentException("Profile can not be null");
        }

        String profilePath = profile.getProfilePath();

        if (new File(profilePath).isDirectory()) {

            String dbDirectory = profilePath + File.separator + "db." + profile.getSiteNumber();
            if (new File(dbDirectory).isDirectory()) {
                profilePath = dbDirectory;
            } else {
                throw new ProfileException(profile, "Profile DB directory does not exist: " + dbDirectory);
            }

            FileSecurity fileSecurity = new FileSecurity(profilePath);

            try {
                boolean validPassword = fileSecurity.verifyPassword(contestPassword);
                if (validPassword) {
                    return fileSecurity;
                } else {
                    throw new ProfileException(profile, "Profile contest password is not correct");
                }
            } catch (FileSecurityException e) {
                throw new ProfileException(profile, e);
            }
        } else {
            throw new ProfileException(profile, "Profile directory does not exist: " + profilePath);
        }
    }
    
    public boolean hasDefaultProfile (){
        return hasDefaultProfile(propertiesFileName);
    }
    /**
     * Is there a default profile defined ?
     * @return
     */
    public boolean hasDefaultProfile (String filename){
        if (new File(filename).exists()) {
            try {
                return getDefaultProfile() != null;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public Profile getDefaultProfile() throws IOException, ProfileLoadException {
        return getDefaultProfile(propertiesFileName);
    }
    
    public Profile getDefaultProfile(String filename) throws IOException, ProfileLoadException {
        if (new File(filename).exists()) {

            Profile profile = null;

            Properties properties = new Properties();
            properties.load(new FileInputStream(filename));

            String key = properties.getProperty(DEFAULT_PROFILE_KEY);
            if (key != null) {
                String profileLine = properties.getProperty(key);
                return toProfile(key, profileLine);
            }

            return profile;

            //           
            // # General PPF Form
            // #
            // [profiles]
            // current=<prof_id>
            // <prof_id>="<title>","<description>","<profile path>",<client_type>[=first]
            // # eof
            //

        } else {
            throw new FileNotFoundException(filename);
        }
    }

    /**
     * Create a profile form an input line/String.
     * 
     * <prof_id>="<title>","<description>","<profile path>",<client_type>[=first]
     * 
     * @param profileLine
     * @param profileLine 
     * @return
     * @throws ProfileLoadException
     */
    private Profile toProfile(String key, String profileLine) throws ProfileLoadException {

        if (profileLine == null) {
            return null;
        }

        String[] fields = profileLine.split(delimiter);

        if (fields.length < 3) {
            throw new IllegalArgumentException("Too few fields for line: " + profileLine);
        }

        // 0 "<title>",
        // 1 "<description>",
        // 2 "<profile path>",
        // 3 active=<true|false>

        String title = stripQuote(fields[0]);
        String description = stripQuote(fields[1]);
        String path = stripQuote(fields[2]);
        String activeField = null;

        if (fields.length > 3) {
            activeField = fields[3];
        }

        if (title == null || title.length() == 0) {
            throw new ProfileLoadException("No title found in: " + profileLine);
        }

        Profile profile = new Profile(title);

        if (description != null) {
            profile.setDescription(description);
        }

        if (path == null || path.length() == 0) {
            throw new ProfileLoadException("No path found in: " + profileLine);
        }
        
        if (activeField == null) {
            profile.setActive(true);
        } else if (activeField.startsWith(ACTIVE_PREFIX)) {
            String newValue = activeField.replaceFirst(ACTIVE_PREFIX, "");
            profile.setActive(Boolean.parseBoolean(newValue));
        } else {
            assert false : "Invalid " + ACTIVE_PREFIX + " field, " + ACTIVE_PREFIX + " not found";
        }

        profile.setProfilePath(path);
        profile.setContestId(key);

        return profile;
    }

    /**
     * Strip all double quotes from start and end of string.
     * 
     * @param string
     * @return
     */
    protected String stripQuote(String string) {

        if (string == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(string);

        if (buffer.charAt(0) == '"') {
            buffer.deleteCharAt(0);
        }

        int idx = buffer.lastIndexOf("\"");

        if (idx > -1) {
            buffer.deleteCharAt(idx);
        }

        return buffer.toString();
    }

    /**
     * Return a list of Profiles.
     * 
     * @param properties
     * @return
     * @throws ProfileLoadException
     */
    private Profile[] toProfiles(Properties properties) throws ProfileLoadException {

        Vector<Profile> list = new Vector<Profile>();

        String[] keys = (String[]) properties.keySet().toArray(new String[properties.keySet().size()]);
        for (String key : keys) {
            String profileLine = properties.getProperty(key);
            String[] fields = profileLine.split(delimiter);
            if (fields.length > 2) {
                Profile profile = toProfile(key, profileLine);
                list.add(profile);
            }
        }

        return (Profile[]) list.toArray(new Profile[list.size()]);
    }
    
    /**
     * Store the profiles at the default location.
     * 
     * @param profiles
     * @param defaultProfile
     * @return
     * @throws IOException
     */
    public boolean store( Profile[] profiles, Profile defaultProfile) throws IOException {
        return this.store(propertiesFileName, profiles, defaultProfile);
    }
    
    public boolean storeDefaultProfile(Profile defaultProfile) throws IOException {
        return storeDefaultProfile(propertiesFileName, defaultProfile);
    }
    
    public boolean storeDefaultProfile(String filename, Profile defaultProfile) throws IOException {
        Profile[] profiles = null;

        try {
            profiles = load();
        } catch (Exception e) {
            profiles = new Profile[1];
            profiles[0] = defaultProfile;
        }

        return this.store(filename, profiles, defaultProfile);
    }


    /**
     * Store list of profiles.
     * 
     * @param filename
     * @param profiles
     * @param defaultProfile
     * @return
     * @throws IOException
     */
    public boolean store(String filename, Profile[] profiles, Profile defaultProfile) throws IOException {
        Properties properties = new Properties();

        VersionInfo versionInfo = new VersionInfo();

        for (Profile profile : profiles) {
            String line = createProfileLine(profile);
            
            properties.put(profile.getElementId().toString(), line);
        }
        
        // Add default profile into list if it isn't already there.
        if (! properties.containsKey(defaultProfile.getElementId().toString())){
            String line = createProfileLine(defaultProfile);
            
            properties.put(defaultProfile.getElementId().toString(), line);
        }

        properties.put(DEFAULT_PROFILE_KEY, defaultProfile.getElementId().toString());

        properties.store(new FileOutputStream(filename), "Created by PC^2 Version " + versionInfo.getVersionNumber() + " " + versionInfo.getBuildNumber()
                + " $Id$ ");

        return true;
    }

    /**
     * For a profile create a line.
     * @param profile
     * @return
     */
    private String createProfileLine(Profile profile) {
        // <prof_id>="<title>","<description>","<profile path>",active=<true|false>,<client_type>[=first]
        return quoteString(profile.getName()) + delimiter + quoteString(profile.getDescription()) + delimiter + quoteString(profile.getProfilePath()) + delimiter + ACTIVE_PREFIX + profile.isActive()
                + delimiter;
    }


    private String quoteString(String name) {
        // TODO handle " in line, someday.
        
        return "\"" + name + "\"";
    }

    /**
     * Merges profiles (merges profiles into currentProfiles).
     * 
     * @param currentProfiles
     * @param profiles
     * @return
     */
    public Profile[] mergeProfiles(Profile[] currentProfiles, Profile[] profiles) {

        Hashtable<String, Profile> table = new Hashtable<String, Profile>();

        if (currentProfiles != null) {
            for (Profile profile : currentProfiles) {
                table.put(profile.getProfilePath(), profile);
            }
        }

        if (profiles != null) {
            for (Profile profile : profiles) {
                table.put(profile.getProfilePath(), profile);
            }
        }

        return (Profile[]) table.values().toArray(new Profile[table.values().size()]);
    }
    
    /**
     * Profile present in contest?.
     * 
     * Checked for equality using getProfilePath(), not equals.
     * 
     * @param contest
     * @param aProfile
     * @return
     */
    private boolean exists(IInternalContest contest, Profile aProfile) {

        for (Profile profile : contest.getProfiles()) {
            if (aProfile.getProfilePath().equals(profile.getProfilePath())) {
                return true;
            }
            if (aProfile.getContestId().equals(profile.getContestId())) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Merge profiles.
     * 
     * @param contest
     *            contest to add profiles to.
     * @throws IOException
     * @throws ProfileLoadException
     */
    public void mergeProfiles(IInternalContest contest) throws IOException, ProfileLoadException {

        if (hasDefaultProfile()) {
            Profile[] profiles = load();

            for (Profile profile : profiles) {
                if (!exists(contest, profile)) {
                    if (new File(profile.getProfilePath()).isDirectory()) {
                        contest.addProfile(profile);
                    }
                    // } else { exists - so no update/add

                }
            }
        }
        // else no profiles to load.
    }

    
    public static Profile createNewProfile() {
        Profile profile = new Profile("Default");
        profile.setDescription("Default Contest");
        return profile;
    }


    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }
    
    
    /**
     * Insure that profile exists and create contest key file.
     * 
     * @param newProfile
     * @param password
     * @throws FileSecurityException
     * @throws ProfileCloneException 
     */
    public boolean createProfilesPathandFiles (Profile newProfile, String password) throws FileSecurityException, ProfileCloneException {

        int siteNumber = newProfile.getSiteNumber();

        String profilePath = newProfile.getProfilePath();
        if (!new File(profilePath).isDirectory()) {

            /**
             * Create Profile dir
             */

            try {
                new File(profilePath).mkdirs();
            } catch (Exception e) {
                throw new ProfileCloneException("Unable to create profile dir " + profilePath, e);
            }

            if (!new File(profilePath).isDirectory()) {
                throw new ProfileCloneException("Unable to use profile dir " + profilePath);
            }

            String databaseDirectoryName = profilePath + File.separator + "db." + siteNumber;

            /**
             * Create database directory
             */

            try {
                new File(databaseDirectoryName).mkdirs();
            } catch (Exception e) {
                throw new ProfileCloneException("Unable to create DB dir " + profilePath, e);
            }

            /**
             * Create storage/security files.
             */
            FileSecurity fileSecurity = new FileSecurity(databaseDirectoryName);
            fileSecurity.saveSecretKey(password.toCharArray());
            fileSecurity = null;
            
            return true;
            
        } else {
            return false;
        }
    }
}
