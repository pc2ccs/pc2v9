// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;

/**
 * ICPC CCS TSV File Loader.
 * 
 * Loads group and teams.tsv files into Group and Account lists.
 * 
 * @author pc2@ecs.csus.edu
 */
public final class ICPCTSVLoader {

    private ICPCTSVLoader() {
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
    // plus 
    // 8 institute id "INST-" + integer
    private static final int TEAM2_TSV_FIELDS = 8;

    // From CCS Specification

    // groups.tsv

    // Field Description Example Type
    // 1 Label groups fixed string (always same value)
    // 2 Version number 1 integer
    //
    // Then follow several lines with the following format (one per team group).
    // Field Description Example Type
    // 1 Group ID 902 integer
    // 2 Group name North America string

    private static final int GROUP_TSV_FIELDS = 2;

    /**
     * Null string value.
     */
    private static final String NULL_STRING = "null";

    private static int siteNumber = 1;

    private static Group[] groups = new Group[0];
    private static HashMap<String,String[]> institutionsMap = new HashMap<String,String[]>();

    /**
     * Load teams.tsv, use {@link #loadGroups(String)} first.
     * 
     * {@link #setGroups(Group[])} must be invoked before using this method so groupIds are assigned.
     * 
     * @param filename
     * @return
     * @throws Exception
     */
    public static Account[] loadAccounts(String filename) throws Exception {

        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));

        if (lines.length == 0) {
            throw new FileNotFoundException(filename);
        }
        
        int i = 0;

        String firstLine = lines[i];

        /**
         * Read first line, header/version info
         */
        // 1 Label teams fixed string (always same value)
        // 2 Version number 1 integer
        String[] fields = TabSeparatedValueParser.parseLine(firstLine);

        // TODO CCS check for 'teams' when tsv file contains that value.
        // validate first line
        // String[] fields = firstLine.split("\t");
        
//        if (!fields[0].trim().equals("teams")) {
//            throw new InvalidValueException("Expecting 'teams' got '" + fields[0] + "' in " + filename);
//        }

        i++;

        Account[] accounts = new Account[lines.length - 1];

        int accountCount = 0;
        ClientType.Type team = ClientType.Type.TEAM;
        PermissionList teamPermissionList = new PermissionGroup().getPermissionList (team);

        for (; i < lines.length; i++) {

            // fields = lines[i].split("\t");
            fields = TabSeparatedValueParser.parseLine(lines[i]);

            if (!(fields.length == TEAM_TSV_FIELDS || fields.length == TEAM2_TSV_FIELDS) && i == 1) {
                throw new InvalidFileFormat("Expected " + TEAM_TSV_FIELDS + " fields, found " + fields.length + " is this a valid team.tsv file? (" + filename + ")");
            }

            accounts[accountCount] = accountFromFields(fields, accountCount, lines[i]);
            accounts[accountCount].clearListAndLoadPermissions(teamPermissionList);
            accountCount++;
        }

        return accounts;
    }

    private static Account accountFromFields(String[] fields, int accountCount, String originalLine) throws InvaildNumberFields, InvalidValueException {
        
        if (!(fields.length == TEAM_TSV_FIELDS || fields.length == TEAM2_TSV_FIELDS)) {
            throw new InvaildNumberFields("Expected " + TEAM_TSV_FIELDS + " (or "+ TEAM2_TSV_FIELDS + ") fields, found " + fields.length + " invalid team.tsv line: " + originalLine);
        }
        
        int fieldnum = 0;
        String numberStr = fields[fieldnum++];
        String reservationIdStr = fields[fieldnum++];
        String groupIDStr = fields[fieldnum++];
        String teamName = fields[fieldnum++];
        String schoolName = fields[fieldnum++];
        String schoolShortName = fields[fieldnum++];
        String countryCode = fields[fieldnum++];
        String institutionCode = "";
        String institutionName = "";
        String institutionFormalName = "";
        if (fields.length == TEAM2_TSV_FIELDS) {
            institutionCode = fields[fieldnum++];
            String[] fieldArray = getInstitutionNames(institutionCode);
            if (fieldArray != null) {
                institutionFormalName = fieldArray[1];
                institutionName = fieldArray[2];
            }
        }

        // 1 Team number 22 integer
        // 2 Reservation ID 24314 integer
        // 3 Group ID 4 integer
        // 4 Team name Hoos string
        // 5 Institution name University of Virginia string
        // 6 Institution short name U Virginia string
        // 7 Country USA string ISO 3166-1 alpha-3

        int number = 0;
        
        if (numberStr != null && NULL_STRING.equalsIgnoreCase(numberStr.trim())){
            /**
             * If team number field is 'null' then assign a team number.
             * The input teams.tsv from the CMS as of 2/24/2013 contains a constant
             * string 'null' where the team number should be.
             */
            number = accountCount + 1;
        } else {
            try {
                number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                throw new InvalidValueException("Expecting team number got '" + numberStr + "' on line " + originalLine, e);
            }
        }

        int groupNumber = 0; 
        try {
            groupNumber = Integer.parseInt(groupIDStr);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Expecting group number got '" + numberStr + "' on line " + originalLine, e);
        }

        ClientId clientId = new ClientId(siteNumber, ClientType.Type.TEAM, number);
        Account account = new Account(clientId, getJoePassword(clientId), siteNumber);

        account.setDisplayName(schoolName);
        account.setShortSchoolName(schoolShortName);
        account.setLongSchoolName(schoolName);
        account.setExternalName(schoolName);
        account.setTeamName(teamName);
        account.setExternalId(reservationIdStr);
        account.setCountryCode(countryCode);
        if (!institutionCode.equals("")) {
            account.setInstitutionCode(institutionCode);
            if (!institutionName.equals("")) {
                account.setInstitutionName(institutionFormalName);
            }
            if (!institutionFormalName.equals("")) {
                account.setInstitutionShortName(institutionName);
            }
        }
        
        if (groupNumber != 0){
            /**
             * Only lookup group number if not zero.
             */
            ElementId groupId = getGroupForNumber(groupNumber);
            
            if (groupId != null) {
                account.setGroupId(groupId);
            } else {
                throw new InvalidValueException("Unknown group number '" + groupNumber + "' on line " + originalLine);
            }
        }

        return account;
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
        ICPCTSVLoader.groups = groups;
    }
    
    /**
     * Merge/Add group data from filename and authoritativeGroups.
     * 
     * @param filename name of groups.tsv file.
     * @return list of groups 
     * @throws Exception
     */
    public static Group[] loadGroups(String filename) throws Exception {
        return loadGroups(filename, new Group[0]);
    }
    
    /**
     * Merge/Add group data from filename and authoritativeGroups.
     * 
     * @param filename name of groups.tsv file.
     * @param authoritativeGroups an array of groups (from contest/model)
     * @return unique groups from authoritativeGroups and filename's group list.
     * @throws Exception
     */
    public static Group[] loadGroups(String filename, Group[] authoritativeGroups) throws Exception {
        List<Group> groupList = new ArrayList<Group>();
        
        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));
        
        if (lines.length == 0) {
            throw new FileNotFoundException(filename);
        }

        int i = 0;

        String firstLine = lines[i];

        // validate first line
        // String[] fields = firstLine.split("\t");
        String[] fields = TabSeparatedValueParser.parseLine(firstLine);

        // 1 Label groups fixed string (always same value)
        // 2 Version number 1 integer

        i++;

        for (; i < lines.length; i++) {

            // fields = lines[i].split("\t");
            fields = TabSeparatedValueParser.parseLine(lines[i]);

            if (fields.length != GROUP_TSV_FIELDS && i == 1) {
                throw new InvalidFileFormat("Expected " + GROUP_TSV_FIELDS + " fields, found " + fields.length + " is this a valid groups.tsv file? (" + filename + ")");
            }
            if (fields.length != GROUP_TSV_FIELDS) {
                throw new InvaildNumberFields("Expected " + GROUP_TSV_FIELDS + " fields, found " + fields.length + " invalid groups.tsv line " + lines[i]);
            }

            int fieldnum = 0;
            String numberStr = fields[fieldnum++];
            String groupName = fields[fieldnum++];

            // Field Description Example Type
            // 1 Group ID 902 integer
            // 2 Group name North America string

            int number = 0;
            try {
                number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                throw new InvalidValueException("Expecting group number got '" + numberStr + "' on line " + lines[i], e);
            }
            
            Group group = findGroupById(authoritativeGroups, number);
            if (group == null) {
                group = new Group(groupName);
            }
            
            group.setDisplayName(groupName);
            group.setGroupId(number);
            
            groupList.add(group);
        }
        
        /**
         * Add groups from authoritativeGroups if missing from groupList.
         */
        for (Group aGroup : authoritativeGroups) {
            if (! groupList.contains(aGroup)) {
               groupList.add(aGroup);
               break;
           }
        }
        
        groups = (Group[]) groupList.toArray(new Group[groupList.size()]);
        
        return groups;
    }

    /**
     * Search and return group if found.
     * 
     * @param authoritativeGroups list of groups
     * @param groupCMSId CMS group id
     * @return null if not found, else the group
     */
    private static Group findGroupById(Group[] authoritativeGroups, int groupCMSId) {
        for (Group group : authoritativeGroups) {
            if (groupCMSId == group.getGroupId()) {
                return group;
            }
        }
        return null;
    }

    protected  static String extractTeamNumber (String accountString){
        int dash = accountString.indexOf('-');
        if (dash > 0) {
            accountString = accountString.substring(dash + 1);
        } else {
            accountString = accountString.substring(4);
        }
        return accountString;
    }

    public static void loadInstitutions(String filename) throws Exception {
        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));

        institutionsMap  = new HashMap<String,String[]>();
        // do not care about the first line (line 0), so start with 1
        for (int i = 1; i < lines.length; i++) {
            String[] fields = TabSeparatedValueParser.parseLine(lines[i]);
            String icpcId = fields[0];
            if (icpcId.startsWith("INST-U-")) {
                // why do these not use the same ids that are in teams.tsv....
                icpcId = icpcId.replaceFirst("INST-U-", "INST-");
            }
//            String formalName = fields[1];
//            String name = fields[2];
            institutionsMap.put(icpcId, fields);
            
            // We also add the institution ID minus the INST- "Due to the non-specificity of the format of an inst code
            if(icpcId.startsWith("INST-")) {
                institutionsMap.put(icpcId.substring(5), fields);
            }
        }
    }
    
    /**
     * Returns the corresponding institution information for the supplied institution code.
     * If the code is not recognized, a null array is returned.
     * Due to the non-specificity of the format of an institution code, we have to check if
     * it has the INST-U- or INST- prefixes, if so, remove them and try again.
     * The map must be filled in via loadInstitutions() first or it will always return false.
     * 
     * @param instCode (number, INST-U-number or INST-number)
     * @return array of 3 strings: [0]=code, [1]=formal name [2]=short name, or null if not found
     */
    public static String[] getInstitutionNames(String instCode) {
        String[] names = null;
        
        if(institutionsMap.containsKey(instCode)) {
            names = institutionsMap.get(instCode);
        } else {
            // ugh.  because no one can decide on what an institution ID is, we have this nonsense.
            if (instCode.startsWith("INST-U-")) {
                instCode = instCode.substring(7);
            }
            if (instCode.startsWith("INST-")) {
                instCode = instCode.substring(5);
            }
            if(institutionsMap.containsKey(instCode)) {
                names = institutionsMap.get(instCode);
            }
        }
        return(names);
    }
    
    public static HashMap<Integer, String> loadPasswordsFromAccountsTSV(String filename) throws Exception {
        String[] lines = CCSListUtilities.filterOutCommentLines(Utilities.loadFile(filename));

        HashMap<Integer, String> passwordMap = new HashMap<Integer, String>();
        // do not care about the first line (line 0), so start with 1
        for (int i = 1; i < lines.length; i++) {
            String[] fields = TabSeparatedValueParser.parseLine(lines[i]);
            if (fields[0].equals("team")) {
                // need to parse the number out of team-\d\d\d in field 2 and the password from field 3
                String account = extractTeamNumber(fields[2]);
                Integer number = new Integer(account);
                String password = fields[3];
                passwordMap.put(number, password);
            }

        }
        return passwordMap;
    }

  
}
