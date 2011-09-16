package edu.csus.ecs.pc2.imports.ccs;

import java.io.IOException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * CCS group.tsv list.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: GroupList.java 178 2011-04-08 19:33:23Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/GroupList.java $
public class GroupList {

    // From CCS Specification
    // groups.tsv

    // The first line has the following format
    // Field Description Example Type
    // 1 Label groups fixed string (always same value)
    // 2 Version number 1 integer
    //
    // Then follow several lines with the following format (one per team group).
    // Field Description Example Type
    // 1 Group ID 902 integer
    // 2 Group name North America string

    /**
     * Max fields in group.tsv file.
     * 
     */
    private static final int MAX_GROUP_TSV_FIELDS = 2;

    public Group[] loadlist(String filename) throws InvalidValueException, InvalidFileFormat, InvaildNumberFields, IOException {

        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));

        int i = 0;

        String firstLine = lines[i];

        // validate first line
        String[] fields = firstLine.split("\t");

        // 1 Label groups fixed string (always same value)
        // 2 Version number 1 integer
        if (!fields[0].trim().equals("groups")) {
            throw new InvalidValueException("Expecting groups got '" + fields[0] + "' in " + filename);
        }

        i++;

        Group[] groups = new Group[lines.length - 1];

        int groupCount = 0;

        for (; i < lines.length; i++) {

            fields = lines[i].split("\t");

            if (fields.length != MAX_GROUP_TSV_FIELDS && i == 1) {
                throw new InvalidFileFormat("Expected " + MAX_GROUP_TSV_FIELDS + " fields, found " + fields.length + " is this a valid team.tsv file? (" + filename + ")");
            }
            if (fields.length != MAX_GROUP_TSV_FIELDS) {
                throw new InvaildNumberFields("Expected " + MAX_GROUP_TSV_FIELDS + " fields, found " + fields.length + " invalid team.tsv line " + lines[i]);
            }

            // Field Description Example Type
            // 1 Group ID 902 integer
            // 2 Group name North America string

            String groupIdStr = fields[0];
            String groupName = fields[1];

            int groupId = 0;

            try {
                groupId = Integer.parseInt(groupIdStr);
            } catch (NumberFormatException e) {
                throw new InvalidValueException("Expecting group number got '" + groupIdStr + "' on line " + lines[i], e);
            }

            Group group = new Group(groupName);
            group.setGroupId(groupId);

            groups[groupCount] = group;
            groupCount++;

        }

        return groups;
    }
}
