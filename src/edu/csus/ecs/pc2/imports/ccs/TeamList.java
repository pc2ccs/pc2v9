package edu.csus.ecs.pc2.imports.ccs;

import java.io.IOException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * CCS Team List.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: TeamList.java 181 2011-04-11 03:21:46Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/TeamList.java $
public final class TeamList {

    private TeamList() {
        super();
    }

    // From CCS Specification

    // team.tsv

    // Field Description Example Type
    // 1 Label teams fixed string (always same value)
    // 2 Version number 1 integer
    //
    //
    // Then follow several lines with the following format (one per team).
    // Field Description Example Type
    // 1 Team number 22 integer
    // 2 Reservation ID 24314 integer
    // 3 Group ID 4 integer
    // 4 Team name Hoos string
    // 5 Institution name University of Virginia string
    // 6 Institution short name U Virginia string
    // 7 Country USA string ISO 3166-1 alpha-3

    private static final int TEAM_TSV_FIELDS = 7;

    private static int siteNumber = 1;

    private static Group[] groups = new Group[0];

    /**
     * Load teams.tsv.
     * 
     * {@link #setGroups(Group[])} must be invoked before using this method so groupIds are assigned.
     * 
     * @param filename
     * @return
     * @throws InvalidValueException
     * @throws InvalidFileFormat
     * @throws InvaildNumberFields
     * @throws IOException
     */
    public static Account[] loadList(String filename) throws InvalidValueException, InvalidFileFormat, InvaildNumberFields, IOException {

        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));

        int i = 0;

        String firstLine = lines[i];

        // validate first line
        String[] fields = firstLine.split("\t");

        // 1 Label teams fixed string (always same value)
        // 2 Version number 1 integer
        if (!fields[0].trim().equals("teams")) {
            throw new InvalidValueException("Expecting teams got '" + fields[0] + "' in " + filename);
        }

        i++;

        Account[] accounts = new Account[lines.length - 1];

        int accountCount = 0;

        for (; i < lines.length; i++) {

            fields = lines[i].split("\t");

            if (fields.length != TEAM_TSV_FIELDS && i == 1) {
                throw new InvalidFileFormat("Expected " + TEAM_TSV_FIELDS + " fields, found " + fields.length + " is this a valid team.tsv file? (" + filename + ")");
            }
            if (fields.length != TEAM_TSV_FIELDS) {
                throw new InvaildNumberFields("Expected " + TEAM_TSV_FIELDS + " fields, found " + fields.length + " invalid team.tsv line " + lines[i]);
            }

            int fieldnum = 0;
            String numberStr = fields[fieldnum++];
            String reservationIdStr = fields[fieldnum++];
            String groupIDStr = fields[fieldnum++];
            String teamName = fields[fieldnum++];
            String schoolName = fields[fieldnum++];
            String schoolShortName = fields[fieldnum++];
            String countryCode = fields[fieldnum++];

            // 1 Team number 22 integer
            // 2 Reservation ID 24314 integer
            // 3 Group ID 4 integer
            // 4 Team name Hoos string
            // 5 Institution name University of Virginia string
            // 6 Institution short name U Virginia string
            // 7 Country USA string ISO 3166-1 alpha-3

            int number = 0;

            try {
                number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                throw new InvalidValueException("Expecting team number got '" + numberStr + "' on line " + lines[i], e);
            }

            int groupId = 0; // TODO somehow need to pre-assign group
            try {
                groupId = Integer.parseInt(groupIDStr);
            } catch (NumberFormatException e) {
                throw new InvalidValueException("Expecting group number got '" + numberStr + "' on line " + lines[i], e);
            }

            ClientId clientId = new ClientId(siteNumber, Type.TEAM, number);
            Account account = new Account(clientId, getJoePassword(clientId), siteNumber);

            account.setDisplayName(teamName);
            account.setShortSchoolName(schoolShortName);
            account.setLongSchoolName(schoolName);
            account.setExternalId(reservationIdStr);
            account.setCountryCode(countryCode);

            account.setGroupId(getGroupForNumber(groupId));

            accounts[accountCount] = account;
            accountCount++;
        }

        return accounts;
    }

    private static ElementId getGroupForNumber(int groupId) {

        for (Group group : groups) {
            if (group.getGroupId() == groupId) {
                return group.getElementId();
            }
        }

        return null;
    }

    /**
     * return joe password for clientid.
     * 
     * @param clientId
     * @return
     */
    private static String getJoePassword(ClientId clientId) {
        return clientId.getClientType().toString().toLowerCase() + clientId.getClientNumber();
    }

    public static void setGroups(Group[] groups) {
        TeamList.groups = groups;
    }
}
