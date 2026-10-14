// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.util.ArrayList;
import java.util.Map;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Applies the optional {@code division-groups} list from PC2 YAML ({@code system.pc2.yaml}).
 *
 * <p>The list adjusts scoreboard-related flags on groups that already exist from
 * groups.tsv / CMS import. It does not create groups or change team membership.</p>
 */
public class DivisionScoreboardYamlLoader {

    /**
     * Updates existing groups from a {@code division-groups} list in the parsed YAML map.
     * Groups must already have been loaded. A missing list is a no-op.
     *
     * @param contest loaded contest model
     * @param content parsed YAML document (contest.yaml concatenated with system.pc2.yaml)
     * @param sourceDescription name used in warning messages, typically {@code system.pc2.yaml}
     */
    @SuppressWarnings("unchecked")
    void applyOverlay(IInternalContest contest, Map<String, Object> content, String sourceDescription) {
        if (contest == null || content == null) {
            return;
        }

    	//get info from YAML on which groups represent entire divisions
        ArrayList<Map<String, Object>> divisionGroups = fetchList(content, IContestLoader.DIVISION_GROUPS_KEY);
        if (divisionGroups == null || divisionGroups.isEmpty()) {
            return;
        }

        if (contest.getGroups().length == 0) {
            StaticLog.warning("Found division-groups in " + sourceDescription
                    + " but no groups are loaded; load groups.tsv first");
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

            //update the current group's flags based on the division-groups entry
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
}
