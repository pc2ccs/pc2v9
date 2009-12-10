package edu.csus.ecs.pc2.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Profiles Manager
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

    public Profile[] load(String filename) throws IOException, ProfileLoadException {

        if (new File(filename).exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(filename));
            return toProfiles(properties);
        } else {
            throw new FileNotFoundException(filename);
        }
    }

    public Profile defaultProfile(String filename) throws IOException, ProfileLoadException {
        if (new File(filename).exists()) {

            Profile profile = null;

            Properties properties = new Properties();
            properties.load(new FileInputStream(filename));

            String profileId = properties.getProperty(DEFAULT_PROFILE_KEY);
            if (profileId != null) {
                String profileLine = properties.getProperty(profileId);
                return toProfile(profileLine);
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
     * Create a profile form an input line.
     * 
     * <prof_id>="<title>","<description>","<profile path>",<client_type>[=first]
     * 
     * @param profileLine
     * @return
     * @throws ProfileLoadException
     */
    private Profile toProfile(String profileLine) throws ProfileLoadException {

        if (profileLine == null) {
            return null;
        }

        String[] fields = profileLine.split(",");

        // 0 "<title>",
        // 1 "<description>",
        // 2 "<profile path>",
        // 3 <client_type>[=first]

        String title = stripQuote(fields[0]);
        String description = stripQuote(fields[1]);
        String path = stripQuote(fields[2]);
        // String extraInfo = fields[3];

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

        profile.setProfilePath(path);

        return profile;
    }

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

    private Profile[] toProfiles(Properties properties) throws ProfileLoadException {

        return null;
    }

    public boolean store(String filename, Profile[] profiles, Profile defaultProfile) throws IOException {
        Properties properties = new Properties();

        VersionInfo versionInfo = new VersionInfo();

        for (Profile profile : profiles) {
            // <prof_id>="<title>","<description>","<profile path>",<client_type>[=first]
            String line = quoteString(profile.getName()) + "," + quoteString(profile.getDescription()) + "," + quoteString(profile.getProfilePath()) + ",";
            properties.put(profile.getElementId().toString(), line);
        }

        properties.put(DEFAULT_PROFILE_KEY, defaultProfile.getElementId().toString());

        properties.store(new FileOutputStream(filename), "Created by PC^2 Version " + versionInfo.getVersionNumber() + " " + versionInfo.getBuildNumber() + " $Id$ ");

        return true;
    }

    private String quoteString(String name) {
        return "\"" + name + "\"";
    }
}
