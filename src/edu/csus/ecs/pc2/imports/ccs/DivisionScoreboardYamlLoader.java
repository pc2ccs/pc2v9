// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Loads optional division-scoreboard.yaml overlay for division-type contests.
 *
 * <p>This file adjusts scoreboard-related flags on groups that already exist from
 * groups.tsv / CMS import. It does not create groups or change team membership.</p>
 */
public class DivisionScoreboardYamlLoader {

    private final Yaml yaml = new Yaml();

    /**
     * Apply division-scoreboard.yaml if present in the CDP config directory.
     * The content of the file "division-scoreboard.yaml" defines "overlays"
     * on any existing group information; that is, it updates existing groups
     * with information regarding which groups represent "division scoreboards".
     * Groups must have already been defined before attempting to apply overlay/update
     * information.
     *
     * @param contest loaded contest model
     * @param cdpConfigDirectory CDP config directory
     * @return true if the file existed and was read
     */
    public boolean loadDivisionScoreboardYamlOverlay(IInternalContest contest, File cdpConfigDirectory) {
        if (contest == null) {
            throw new IllegalArgumentException("contest is null");
        }
        if (cdpConfigDirectory == null) {
            throw new IllegalArgumentException("cdpConfigDirectory is null");
        }

        File overlayFile = new File(cdpConfigDirectory, IContestLoader.DIVISION_SCOREBOARD_YAML_FILENAME);
        if (!overlayFile.isFile()) {
            return false;
        }

        if (contest.getGroups().length == 0) {
            StaticLog.warning("Found " + overlayFile.getAbsolutePath()
                    + " but no groups are loaded; load groups.tsv first");
            return true;
        }

        Map<String, Object> content = loadYaml(overlayFile);
        if (content == null) {
            return true;
        }

        applyOverlay(contest, content, overlayFile.getAbsolutePath());
        return true;
    }

    /**
     * Updates existing groups based on information obtained from division-scoreboard.yaml ("sourceDescription" file).
     * If the YAML file indicates a group is a "division scoreboard", the corresponding Group is
     */
    @SuppressWarnings("unchecked")
    void applyOverlay(IInternalContest contest, Map<String, Object> content, String sourceDescription) {
    	
    	//get info from YAML on which groups represent entire divisions
        ArrayList<Map<String, Object>> divisionGroups = fetchList(content, IContestLoader.DIVISION_GROUPS_KEY);
        if (divisionGroups == null || divisionGroups.isEmpty()) {
            return;
        }

        Group[] groups = contest.getGroups();
        for (Map<String, Object> entry : divisionGroups) {
            Integer groupId = fetchInteger(entry, IContestLoader.DIVISION_GROUP_ID_KEY);
            String groupName = fetchString(entry, IContestLoader.DIVISION_GROUP_NAME_KEY);

            if (groupId == null && groupName == null) {
                StaticLog.warning("Skipping division-groups entry in " + sourceDescription
                        + ": must specify id and/or name");
                continue;
            }

            //get the next group
            Group group = findGroup(groups, groupId, groupName, sourceDescription);
            if (group == null) {
            	//no such group was found
                StringBuilder message = new StringBuilder("No matching group for division-groups entry in ");
                message.append(sourceDescription);
                if (groupId != null) {
                    message.append(" id=").append(groupId);
                }
                if (groupName != null) {
                    message.append(" name=").append(groupName);
                }
                StaticLog.warning(message.toString());
                continue;
            }

            //update the current group's flags based on the values from the division-scoreboard YAML file
            if (entry.containsKey(IContestLoader.DISPLAY_ON_SCOREBOARD_KEY)) {
                group.setDisplayOnScoreboard(fetchBoolean(entry, IContestLoader.DISPLAY_ON_SCOREBOARD_KEY, true));
            }
            if (entry.containsKey(IContestLoader.DIVISION_WIDE_SCOREBOARD_KEY)) {
                group.setDivisionWideScoreboard(fetchBoolean(entry, IContestLoader.DIVISION_WIDE_SCOREBOARD_KEY, false));
            }
        }
    }

    /**
     * Returns a {@link Group} which matches either the specified {@code groupId} or {@code groupName},
     * or null if no matching group can be found.
     */
    private Group findGroup(Group[] groups, Integer groupId, String groupName, String sourceDescription) {
        Group byId = null;
        if (groupId != null) {
            byId = findGroupById(groups, groupId.intValue());
        }

        Group byName = null;
        if (groupName != null) {
            byName = findGroupByName(groups, groupName);
        }

        if (groupId != null && groupName != null) {
            if (byId == null && byName == null) {
                return null;
            }
            if (byId != null && byName != null && byId != byName) {
                StaticLog.warning("division-groups entry in " + sourceDescription + " id=" + groupId
                        + " and name=" + groupName + " match different groups");
                return null;
            }
            return byId != null ? byId : byName;
        }

        return byId != null ? byId : byName;
    }

    /**
     * Returns the {@link Group} with the specified {@code groupId}, or null
     * if no such group exists.
     */
    private Group findGroupById(Group[] groups, int groupId) {
        for (Group group : groups) {
            if (group.getGroupId() == groupId) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Group} with the specified {@code groupName}, or null
     * if no such group exists.
     */
    private Group findGroupByName(Group[] groups, String groupName) {
        for (Group group : groups) {
            if (groupName.equals(group.getDisplayName())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns the {@code Map value} corresponding to the specified {@code key} in the specified {@code Map},
     * or null if no such value is found under that specified {@code key}.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Map<String, Object>> fetchList(Map<String, Object> content, String key) {
        Object value = content.get(key);
        if (value == null) {
            return null;
        }
        return (ArrayList<Map<String, Object>>) value;
    }

    /**
     * Returns the String value corresponding to the specified {@code key} in the specified {@code Map},
     * or null if no such value is found under that specified {@code key}.
     */
   private String fetchString(Map<String, Object> content, String key) {
        Object value = content.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return value.toString().trim();
    }

   /**
    * Returns the {@link Integer} value corresponding to the specified {@code key} in the specified {@code Map},
    * or null if no such value is found under that specified {@code key}.
    */
    private Integer fetchInteger(Map<String, Object> content, String key) {
        Object value = content.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return Integer.valueOf(((Long) value).intValue());
        }
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the boolean value corresponding to the specified {@code key} in the specified {@code Map},
     * or {@code defaultValue} if no such value is found under that specified {@code key} or if the found
     * value is a String which is neither the string "true" nor the string "false".
     */
    private boolean fetchBoolean(Map<String, Object> content, String key, boolean defaultValue) {
        Object value = content.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        String stringValue = value.toString().trim().toLowerCase();
        if ("true".equals(stringValue)) {
            return true;
        }
        if ("false".equals(stringValue)) {
            return false;
        }
        return defaultValue;
    }

    /**
     * Reads the specified file, assumed to contain YAML, and returns a {@link Map} of the
     * YAML entries.  
     * 
     * @return a Map containing the YAML keys and corresponding values.
     * @throws {@link YamlLoadException} if the file contains YAML errors.
     * @throws {@link FileNotFoundException} if the specified file cannot be found.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadYaml(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            return (Map<String, Object>) yaml.load(stream);
        } catch (MarkedYAMLException e) {
            ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
            throw new YamlLoadException(loader.getSnakeParserDetails(e), e, file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new YamlLoadException("File not found " + file.getAbsolutePath(), e, file.getAbsolutePath());
        }
    }
}
