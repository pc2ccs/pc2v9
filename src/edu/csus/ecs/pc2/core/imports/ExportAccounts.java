/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.core.util.CommaSeparatedValueParser;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Provide a class that will save the accounts in various formats.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class ExportAccounts {
    /**
     * used by writeCSV and writeTXT and build
     */
    private static String[] titles = { "site", "account", "displayname", "password", "group", "permdisplay", "permlogin", "externalid", "alias", "permpassword" };
    private static Hashtable<ElementId, String> groupHash;
    private static Exception exception = null;

    private ExportAccounts() {
        super();
    }

    /**
     * Formats we can write.
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Formats {
        /**
         * CSV (comma delimited)
         */
        CSV,
        /**
         * Text (tab delimited)
         */
        TXT,
        /**
         * XML document
         */
        XML
    };

    /**
     * 
     * @param outputFile 
     * @return true on success, otherwise returns false and getException() can be used to get the error
     */
    public static boolean saveAccounts(Formats format, Account[] accounts, Group[] groups, File outputFile) {
        setException(null);
        boolean result = false;
        groupHash = new Hashtable<ElementId, String>();
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                Group group = groups[i];
                groupHash.put(group.getElementId(), group.getDisplayName());
            }
        }
        PrintWriter printWriter;
        if (accounts != null) {
            try {
                printWriter = new PrintWriter(new FileOutputStream(outputFile, false), true);
                Arrays.sort(accounts, new AccountComparator());
                if (format.equals(Formats.TXT)) {
                    result = writeTXT(printWriter, accounts);
                }
                if (format.equals(Formats.CSV)) {
                    result = writeCSV(printWriter, accounts);
                }
                if (format.equals(Formats.XML)) {
                    result = writeXML(printWriter, accounts, groups);
                }
                printWriter.close();
            } catch (FileNotFoundException e) {
                setException(e);
                result=false;
            }
        }
        return (result);
    }

    private static boolean writeXML(PrintWriter out, Account[] accounts, Group[] groups) {
        boolean result = true;
        try {
            String xmlString = buildXMLString(accounts, groups);
            if (xmlString.equals("")) {
                // exception will already be set
                result = false;
            }
            out.println(xmlString);
        } catch (Exception e) {
            setException(e);
            result = false;
        }
        return result;
    }

    private static boolean writeCSV(PrintWriter out, Account[] accounts) {
        boolean result = true;
        try {
            out.println(CommaSeparatedValueParser.toString(titles));
            for (int i = 0; i < accounts.length; i++) {
                Account account = accounts[i];
                if (account.getClientId().getClientType().equals(ClientType.Type.SERVER)) {
                    continue;
                }
                String[] a = buildAccountString(account);
                out.println(CommaSeparatedValueParser.toString(a));
            }
        } catch (Exception e) {
            setException(e);
            result = false;
        }
        return result;
    }

    private static boolean writeTXT(PrintWriter out, Account[] accounts) {
        boolean result=true;
        try {
            out.println(TabSeparatedValueParser.toString(titles));
            for (int i = 0; i < accounts.length; i++) {
                Account account = accounts[i];
                if (account.getClientId().getClientType().equals(ClientType.Type.SERVER)) {
                    continue;
                }
                String[] a = buildAccountString(account);
                out.println(TabSeparatedValueParser.toString(a));
            }
        } catch (Exception e) {
            setException(e);
            result = false;
        }
        return result;
    }

    private static String[] buildAccountString(Account account) {
        // titles = { "site", "account", "displayname", "password", "group", "permdisplay", "permlogin", "externalid", "alias", "permpassword" };
        String[] a=new String[titles .length];
        a[0] = Integer.toString(account.getSiteNumber());
        a[1] = account.getClientId().getName();
        a[2] = account.getDisplayName();
        a[3] = account.getPassword();
        if (account.getGroupId() != null) {
            a[4] = groupHash.get(account.getGroupId());
        } else {
            a[4] = ""; //$NON-NLS-1$
        }
        a[5] = Boolean.toString(account.isAllowed(Type.DISPLAY_ON_SCOREBOARD));
        a[6] = Boolean.toString(account.isAllowed(Type.LOGIN));
        a[7] = account.getExternalId();
        a[8] = account.getAliasName();
        a[9] = Boolean.toString(account.isAllowed(Type.CHANGE_PASSWORD));
        return a;
    }

    public static String buildXMLString(Account[] accounts, Group[] groups) {
        Hashtable<ElementId,String> groupsH = new Hashtable<ElementId, String>();
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                Group group = groups[i];
                groupsH.put(group.getElementId(), group.getDisplayName());
            }
        }

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("accounts");
        for (int i = 0; i < accounts.length; i++) {
            Account account = accounts[i];
            if (account.getClientId().getClientType().equals(ClientType.Type.SERVER)) {
                continue;
            }
            addSingleAccountXML(account, groupsH, mementoRoot);
        }
        String xmlString;
        try {
            xmlString = mementoRoot.saveToString();
        } catch (IOException e) {
            setException(e);
            xmlString = "";
        }
        return xmlString;

    }
    private static void addSingleAccountXML(Account account, Hashtable<ElementId,String> groups, XMLMemento mementoRoot) {
        // titles = { "site", "account", "displayname", "password", "group", "permdisplay", "permlogin", "externalid", "alias", "permpassword" };

        // XXX these 1st ones are from teamStanding of the scoreboard xml
        IMemento accountMemento = mementoRoot.createChild("account");
        accountMemento.putString("teamName", account.getDisplayName()); 
        accountMemento.putInteger("teamId", account.getClientId().getClientNumber());
        accountMemento.putInteger("teamSiteId", account.getClientId().getSiteNumber());
        accountMemento.putString("teamKey", account.getClientId().getTripletKey());
        accountMemento.putString("teamExternalId", account.getExternalId());
        accountMemento.putString("teamAlias", account.getAliasName().trim());
        // now the rest of the the data
        String groupName = "";
        if (account.getGroupId() != null) {
            groupName = groups.get(account.getGroupId());
        }
        accountMemento.putString("groupName", groupName);
        accountMemento.putString("accountName", account.getClientId().getName());
        accountMemento.putInteger("siteId", account.getSiteNumber());
        accountMemento.putString("password", account.getPassword());
        accountMemento.putBoolean("permdisplay", account.isAllowed(Type.DISPLAY_ON_SCOREBOARD));
        accountMemento.putBoolean("permlogin", account.isAllowed(Type.LOGIN));
        accountMemento.putBoolean("permpassword", account.isAllowed(Type.CHANGE_PASSWORD));
    }

    /**
     * @param exception the exception to set
     */
    private static void setException(Exception exception) {
        ExportAccounts.exception = exception;
    }

    /**
     * @return the exception
     */
    public static Exception getException() {
        return exception;
    }
}
