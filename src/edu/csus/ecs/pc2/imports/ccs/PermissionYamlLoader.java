// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Class to load "custom" Permissions from yaml file.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class PermissionYamlLoader {

    private List<Account> accountList = new ArrayList<Account>();

    public PermissionYamlLoader(String[] yamlLines, Account[] accounts) {
        if (accounts == null || accounts.length == 0) {
            throw new IllegalArgumentException("Empty list of accounts not allowed");
        }

        Collections.addAll(accountList, accounts);

        updateAccountPermissionsFromYaml(yamlLines);
    }

    ArrayList fetchList(Map<String, Object> content, String key) {
        return (ArrayList) content.get(key);
    }

    private String fetchValue(Map<String, Object> content, String key) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) content.get(key);
        } else {
            return content.get(key).toString();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void updateAccountPermissionsFromYaml(String[] yamlLines) {

        Map<String, Object> content = loadYaml(null, yamlLines);

        ArrayList permissionList = fetchList(content, "permissions");

        for (Object object : permissionList) {

            Map<String, Object> map = (Map<String, Object>) object;

            String accountType = fetchValue(map, "account");
            ClientType.Type clientType = ClientType.Type.valueOf(accountType.trim());

            String numberString = fetchValue(map, "number");

            int siteNumber = accountList.get(0).getClientId().getSiteNumber();

            int[] clientNumbers = getClientNumbers(accountList, numberString, clientType);
            
            System.out.println("debug 22 cli num "+Arrays.toString(clientNumbers));
            for (int i = 0; i < clientNumbers.length; i++) {

                Account account = getAccount(new ClientId(siteNumber, clientType, clientNumbers[i]));
                System.out.println("debug 22 A "+account);
                String stringEnablePermissions = fetchValue(map, "enable");

                if (!StringUtilities.isEmpty(stringEnablePermissions)) {
                    System.out.println("debug 22 en BB "+stringEnablePermissions);
                    List<edu.csus.ecs.pc2.core.security.Permission.Type> enabledPerms = getPermissionList(stringEnablePermissions);

                    for (Permission.Type type : enabledPerms) {
                        account.addPermission(type);
                    }
                }

                String stringDisablePermissions = fetchValue(map, "disable");
                if (!StringUtilities.isEmpty(stringDisablePermissions)) {
                    System.out.println("debug 22 di CC "+stringDisablePermissions+" "+account);
                    List<edu.csus.ecs.pc2.core.security.Permission.Type> disablePerms = getPermissionList(stringDisablePermissions);
                    for (Permission.Type type : disablePerms) {
                        account.removePermission(type);
                    }
                }

            }
        }
    }

    List<Permission.Type> getPermissionList(String string) {

        List<edu.csus.ecs.pc2.core.security.Permission.Type> list = new ArrayList<Permission.Type>();

        String[] arr = string.trim().split(",");
        for (String term : arr) {

            edu.csus.ecs.pc2.core.security.Permission.Type type = Permission.Type.valueOf(term.trim());
            list.add(type);
        }

        return list;
    }

    private Account getAccount(ClientId id) {
        for (Account account : accountList) {
            if (account.getClientId().equals(id)) {
                return account;
            }
        }
        return null;
    }

    int getIntegerValue(String string, int defaultNumber) {

        int number = defaultNumber;

        if (string != null && string.length() > 0) {
            number = Integer.parseInt(string.trim());
        }

        return number;
    }

    int[] getNumberList(String numberString) {

        String[] list = numberString.split(",");
        if (list.length == 1) {
            int[] out = new int[1];
            out[0] = getIntegerValue(list[0], 0);
            // if (out[0] < 1) {
            // // SOMEDAY 669 throw invalid number in list exception
            // }
            return out;
        } else {
            int[] out = new int[list.length];
            int i = 0;
            for (String n : list) {
                out[i] = getIntegerValue(n, 0);
                // if (out[i] < 1) {
                // // SOMEDAY 669 throw invalid number in list exception
                // }
                i++;
            }
            return out;
        }
    }

    private int[] getClientNumbers(List<Account> list, String numberString, Type type) {

        int[] outList = null;

        if ("all".equalsIgnoreCase(numberString)) {
            outList = getAccountNumbers(list, type);
        } else {
            outList = getNumberList(numberString.trim());
        }
        return outList;
    }

    int[] getAccountNumbers(List<Account> list, Type type) {
        List<Integer> intList = new ArrayList<Integer>();
        for (Account account : list) {
            ClientId clientId = account.getClientId();
            if (account.getClientId().getClientType().equals(type)) {
                intList.add(clientId.getClientNumber());
            }
        }

        int[] outArray = toIntArray(intList);
        return outArray;
    }

    private int[] toIntArray(List<Integer> intList) {
        int[] outArray = new int[intList.size()];
        for (int i = 0; i < outArray.length; i++) {
            outArray[i] = intList.get(i);
        }
        return outArray;
    }

    public Account[] getAccountsArray() {
        return (Account[]) accountList.toArray(new Account[accountList.size()]);
    }

    List<Account> getAccounts() {
        return accountList;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadYaml(String filename, String[] yamlLines) {
        try {
            Yaml yaml = new Yaml();
            String fullString = StringUtilities.join("\n", yamlLines);
            InputStream stream = new ByteArrayInputStream(fullString.getBytes(StandardCharsets.UTF_8));
            return (Map<String, Object>) yaml.load(stream);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e), e, filename);
        }
    }

    /**
     * Create a simple string with parse info.
     * 
     * @param markedYAMLException
     * @return
     */

    String getSnakeParserDetails(MarkedYAMLException markedYAMLException) {

        Mark mark = markedYAMLException.getProblemMark();

        int lineNumber = mark.getLine() + 1; // starts at zero
        int columnNumber = mark.getColumn() + 1; // starts at zero

        return "Parse error at line=" + lineNumber + " column=" + columnNumber + " message=" + markedYAMLException.getProblem();

    }

}
