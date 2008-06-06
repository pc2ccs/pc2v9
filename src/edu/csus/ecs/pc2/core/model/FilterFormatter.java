package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FilterFormatter {

    /**
     * List of languages, comma delimited.
     */
    public static final String LANGUAGES_SPECIFIER = "%L";

    /**
     * Number of languages in filter.
     */
    public static final String NUMBER_LANGUAGES_SPECIFIER = "%#L";

    /**
     * List of problems, comma delimited.
     */
    public static final String PROBLEMS_SPECIFIER = "%P";

    /**
     * Number of languages in filter.
     */
    public static final String NUMBER_PROBLEMS_SPECIFIER = "%#P";

    /**
     * List of judgements, comma delimited.
     */
    public static final String JUDGMENTS_SPECIFIER = "%J";

    /**
     * Number of judgements in filter.
     */
    public static final String NUMBER_JUDGEMENTS_SPECIFIER = "%#J";

    /**
     * Number of accounts in filter.
     */
    public static final String NUMBER_ACCOUNTS_SPECIFIER = "%#T";

    /**
     * List of accounts, comma delimited.
     * 
     * @see #getAccountsList(IInternalContest, Filter)
     */
    public static final String ACCOUNT_SPECIFIER = "%T";

    /**
     * 
     * 
     * @see #getAccountShortNamesList(IInternalContest, Filter)
     */
    public static final String SHORT_ACCOUNT_NAMES_SPECIFIER = "%n";

    /**
     * Return a summary list of team numbers.
     * 
     * @see #getClientsShortList(ClientId[])
     */
    public static final String TEAM_LIST_SPECIFIER = "%t";

    /**
     * Return the list of team client names.
     * 
     * @see #getClientsLongList(ClientId[])
     */
    public static final String TEAM_LONG_LIST_SPECIFIER = "%C";
    
    public static final String START_TIME_RANGE_SPECIFIER = "%s";
    
    public static final String END_TIME_RANGE_SPECIFIER = "%e";
    
    

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * If before string is not found, then returns original string.
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterString
     *            string to replace beforeString
     * @return original string with all beforeString instances replaced with afterString
     */
    private String replaceString(String origString, String beforeString, String afterString) {

        if (origString == null) {
            return origString;
        }

        int startIdx = origString.lastIndexOf(beforeString);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            buf.replace(startIdx, startIdx + beforeString.length(), afterString);
            startIdx = origString.lastIndexOf(beforeString, startIdx - 1);
        }

        return buf.toString();
    }

    /**
     * Fill in fields (specifiers) with values from filter and contest.
     * 
     * @param specifiers
     * @param filter
     * @return
     */
    public String format(String specifiers, IInternalContest contest, Filter filter) {

        String outString = specifiers;
        String formatSpecifier = "%O";

        if (outString.lastIndexOf(formatSpecifier) > -1) {
            String filterOnStr = "Off";
            if (filter.isFilterOn()) {
                filterOnStr = "On";
            }
            outString = replaceString(outString, formatSpecifier, filterOnStr);
        }

        formatSpecifier = PROBLEMS_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getProblemList(contest, filter));
        }

        formatSpecifier = JUDGMENTS_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getJudgementsList(contest, filter));
        }

        formatSpecifier = LANGUAGES_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getLanguagesList(contest, filter));
        }

        formatSpecifier = ACCOUNT_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getAccountsList(contest, filter));
        }

        formatSpecifier = SHORT_ACCOUNT_NAMES_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getAccountShortNamesList(contest, filter));
        }

        formatSpecifier = NUMBER_LANGUAGES_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            String num = filter.getLanguageIdList().length + "";
            outString = replaceString(outString, formatSpecifier, num);
        }

        formatSpecifier = NUMBER_PROBLEMS_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            String num = filter.getProblemIdList().length + "";
            outString = replaceString(outString, formatSpecifier, num);
        }

        formatSpecifier = NUMBER_JUDGEMENTS_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            String num = filter.getJudgementIdList().length + "";
            outString = replaceString(outString, formatSpecifier, num);
        }

        formatSpecifier = NUMBER_ACCOUNTS_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            String num = filter.getAccountList().length + "";
            outString = replaceString(outString, formatSpecifier, num);
        }

        formatSpecifier = TEAM_LONG_LIST_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getClientsLongList(filter.getAccountList()));
        }

        formatSpecifier = TEAM_LIST_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, getClientsShortList(filter.getAccountList()));
        }

        formatSpecifier = START_TIME_RANGE_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, ""+filter.getStartElapsedTime());
        }

        formatSpecifier = END_TIME_RANGE_SPECIFIER;
        if (outString.lastIndexOf(formatSpecifier) > -1) {
            outString = replaceString(outString, formatSpecifier, ""+filter.getEndElapsedTime());
        }

        
        return outString;
    }

    /**
     * Return comma delimited list of judgement names that are being filtered.
     * 
     * @param contest
     * @param filter
     * @return
     */
    private String getJudgementsList(IInternalContest contest, Filter filter) {
        StringBuffer sb = new StringBuffer();
        ElementId[] elementIds = filter.getJudgementIdList();
        if (elementIds.length < 2) {
            if (elementIds.length == 1) {
                sb.append(contest.getJudgement(elementIds[0]));
            }
        } else {
            for (ElementId elementId : elementIds) {
                sb.append(contest.getJudgement(elementId) + ", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // remove last ", "
        }
        return sb.toString();
    }

    /**
     * Return comma delimited list of account names that are being filtered.
     * 
     * @param contest
     * @param filter
     * @return
     */
    private String getAccountsList(IInternalContest contest, Filter filter) {
        StringBuffer sb = new StringBuffer();
        ClientId[] clientIds = filter.getAccountList();
        if (clientIds.length < 2) {
            if (clientIds.length == 1) {
                sb.append(contest.getAccount(clientIds[0]));
            }
        } else {

            Arrays.sort(clientIds, new ClientIdComparator());

            for (ClientId clientId : clientIds) {
                sb.append(contest.getAccount(clientId) + ", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // remove last ", "
        }
        return sb.toString();
    }

    /**
     * Return comma delimited list of client names that are being filtered.
     * 
     * @param contest
     * @param filter
     * @return
     */
    private String getAccountShortNamesList(IInternalContest contest, Filter filter) {
        StringBuffer sb = new StringBuffer();
        ClientId[] clientIds = filter.getAccountList();

        if (clientIds.length < 2) {
            if (clientIds.length == 1) {
                sb.append(clientIds[0].getName());
            }
        } else {

            Arrays.sort(clientIds, new ClientIdComparator());

            for (ClientId clientId : clientIds) {
                sb.append(clientId.getName() + ", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // remove last ", "
        }
        return sb.toString();
    }

    /**
     * Return comma delimited list of Language names that are being filtered.
     * 
     * @param contest
     * @param filter
     * @return
     */
    private String getLanguagesList(IInternalContest contest, Filter filter) {
        StringBuffer sb = new StringBuffer();
        ElementId[] elementIds = filter.getLanguageIdList();
        if (elementIds.length < 2) {
            if (elementIds.length == 1) {
                sb.append(contest.getLanguage(elementIds[0]));
            }
        } else {
            for (ElementId elementId : elementIds) {
                sb.append(contest.getLanguage(elementId) + ", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // remove last ", "
        }
        return sb.toString();
    }

    /**
     * Return comma delimited list of problem names that are being filtered.
     * 
     * @param contest
     * @param filter
     * @return
     */
    private String getProblemList(IInternalContest contest, Filter filter) {
        StringBuffer sb = new StringBuffer();
        ElementId[] elementIds = filter.getProblemIdList();
        
        if (elementIds.length < 2) {
            if (elementIds.length == 1) {
                sb.append(contest.getProblem(elementIds[0]));
            }
        } else {
            Problem [] problems = new Problem[elementIds.length];
            int i = 0;
            for (ElementId elementId : elementIds) {
                problems[i] = contest.getProblem(elementId);
                i++;
            }
            Arrays.sort(problems, new ProblemComparator(contest));
            for (Problem problem : problems){
                sb.append(problem + ", ");
            }
            
            sb.delete(sb.length() - 2, sb.length()); // remove last ", "
        }
        return sb.toString();
    }

    /**
     * List clients in individual comma delimited form.
     * 
     * Prints in the form team#ssite#, ex 9s1 (team 9 site 1)
     * 
     * <pre>
     *        
     *        1s1,2s1,4s1,6s1,8s1,9s1,21s1,22s1,23s1,25s1
     * </pre>
     * 
     * @param printWriter
     * @param clientIds
     */
    public String getClientsLongList(ClientId[] clientIds) {
        ClientId clientId = null;

        StringBuffer stringBuffer = new StringBuffer();
        
        if (clientIds.length < 1){
            return "";
        }

        Arrays.sort(clientIds, new ClientIdComparator());
        
        

        for (int i = 0; i < clientIds.length - 1; i++) {
            clientId = clientIds[i];
            stringBuffer.append(clientId.getClientNumber() + "s" + clientId.getSiteNumber() + ",");
        }
        clientId = clientIds[clientIds.length - 1];
        stringBuffer.append(clientId.getClientNumber() + "s" + clientId.getSiteNumber());

        return stringBuffer.toString();
    }

    /**
     * Get client list with ranges.
     * 
     * Expects sort of clients by site id and client number. <br>
     * returns client list, if 1 through 10 shows 1-10. Before each site of teams, will prefix with the site number.
     * <P>
     * Example:
     * 
     * <pre>
     *        Site 1 team 3,5-7,9,24,26,28-29,31 Site 2 team 1-3,8-17,19-20,23-24 
     * </pre>
     * 
     * @param printWriter
     * @param clientIds
     */
    public String getClientsShortList(ClientId[] clientIds) {

        ClientId clientId = null;

        if (clientIds.length == 0) {
            return "";
        }

        Arrays.sort(clientIds, new ClientIdComparator());

        StringBuffer stringBuffer = new StringBuffer();

        ClientId lastClientId = clientIds[0];
        stringBuffer.append("Site " + lastClientId.getSiteNumber() + " team " + lastClientId.getClientNumber());

        boolean inRange = false;

        for (int i = 1; i < clientIds.length; i++) {
            clientId = clientIds[i];

            if (lastClientId.getSiteNumber() != clientId.getSiteNumber()) {
                if (inRange) {
                    stringBuffer.append(lastClientId.getClientNumber());
                    inRange = false;
                }

                stringBuffer.append(" Site " + clientId.getSiteNumber() + " team " + clientId.getClientNumber());

            } else if (lastClientId.getSiteNumber() == clientId.getSiteNumber() && lastClientId.getClientNumber() + 1 == clientId.getClientNumber()) {
                if (!inRange) {
                    stringBuffer.append("-");
                    inRange = true;
                }
            } else {
                if (inRange) {
                    stringBuffer.append(lastClientId.getClientNumber());
                }
                stringBuffer.append("," + clientId.getClientNumber());
                inRange = false;
            }
            lastClientId = clientId;
        }

        if (inRange) {
            stringBuffer.append(clientId.getClientNumber());
        }

        return stringBuffer.toString();
    }
}
