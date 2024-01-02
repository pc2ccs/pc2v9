// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.util;

import edu.csus.ecs.pc2.core.CommandVariableReplacer;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Substitute scoreboard team values for variables.
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ScoreboardVariableReplacer {

    public static final String COUNTRY_CODE = "{:countrycode}";

    public static final String LONG_SCHOOL_NAME = "{:longschoolname}";

    public static final String SHORT_SCHOOL_NAME = "{:shortschoolname}";

    public static final String GROUP_NAME = "{:groupname}";

    public static final String GROUP_ID = "{:groupid}";

    public static final String EXTERNAL_ID = "{:externalid}";

    public static final String CLIENT_NUMBER = "{:clientnumber}";

    public static final String TEAM_LOGIN_NAME = "{:teamloginname}";

    public static final String SITE_NUMBER = "{:sitenumber}";

    public static final String TEAM_NAME = "{:teamname}";

    public static final String[] VARIABLE_NAMES = { //
            TEAM_NAME, //
            SITE_NUMBER, //
            TEAM_LOGIN_NAME, //
            CLIENT_NUMBER, //
            EXTERNAL_ID, //
            GROUP_ID, //
            GROUP_NAME, //
            SHORT_SCHOOL_NAME, //
            LONG_SCHOOL_NAME, //
            COUNTRY_CODE, //
    };

    /**
     * Substitutes values for variables.
     *
     *
     * Variable names and examples.
     *
     * <pre>
     * Client/Team number - {:clientnumber} = 514
     * Country Code - {:countrycode} = CAN
     * CMS/External ID - {:externalid} = 309407
     * Group Id Number - {:groupid} = 12545
     * Group name - {:groupname} = Canada - University of British Columbia D1
     * Long School name - {:longschoolname} = UBC! (U British Columbia)
     * Short Shool name - {:shortschoolname} = U British Columbia
     * Team site number - {:sitenumber} = 1
     * Team login name - {:teamloginname} = team514
     * Team name - {:teamname} = UBC!
     * </pre>
     *
     * @param origString original string with variables
     * @param account team account
     * @param group team group
     * @return substituted string
     */
    public static String substituteDisplayNameVariables(String origString, Account account, Group group) {

        String newString = origString;

        if (origString.indexOf("{:") != -1) {
            // only need to substitute if there are variables in the string

            ClientId clientId = account.getClientId();

            newString = CommandVariableReplacer.replaceString(newString, TEAM_NAME, account.getTeamName());

            newString = CommandVariableReplacer.replaceString(newString, SITE_NUMBER, toInt(clientId.getSiteNumber()));

            newString = CommandVariableReplacer.replaceString(newString, TEAM_LOGIN_NAME, clientId.getName());

            newString = CommandVariableReplacer.replaceString(newString, CLIENT_NUMBER, toInt(clientId.getClientNumber()));

            newString = CommandVariableReplacer.replaceString(newString, EXTERNAL_ID, account.getExternalId());

            if (group != null) {
                newString = CommandVariableReplacer.replaceString(newString, GROUP_ID, toInt(group.getGroupId()));
                newString = CommandVariableReplacer.replaceString(newString, GROUP_NAME, group.getDisplayName());
            }

            newString = CommandVariableReplacer.replaceString(newString, SHORT_SCHOOL_NAME, account.getShortSchoolName());

            newString = CommandVariableReplacer.replaceString(newString, LONG_SCHOOL_NAME, account.getLongSchoolName());

            newString = CommandVariableReplacer.replaceString(newString, COUNTRY_CODE, account.getCountryCode());

        }

        return newString;

    }

    /**
     *
     * @see #substituteDisplayNameVariables(String, Account, Group)
     *
     * @param origString
     * @param contest
     * @param account
     * @return
     */
    public static String substituteDisplayNameVariables(String origString, IInternalContest contest, Account account) {
        // it is probably OK to use the "primary" group ID here (the one supplied by the CMS).
        // this is used to augment the teamName for display.  Using the CMS group should convey
        // the desired information: eg.  Hawaii - D2  (for example).  Would we want to just show "D2" or "Hawaii" ?
        // probably not - we want the compound group name (eg CMS name).
        if (account.getPrimaryGroupId() != null) {
            return substituteDisplayNameVariables(origString, account, contest.getGroup(account.getPrimaryGroupId()));
        } else {
            return substituteDisplayNameVariables(origString, account, null);
        }
    }

    public static String toInt(int number) {
        return Integer.toString(number);
    }
}
