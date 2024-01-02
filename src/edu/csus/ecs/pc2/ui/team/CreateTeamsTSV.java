// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.CommandLineErrorException;
import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;

/**
 * Read teams.json write teams.tsv
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class CreateTeamsTSV {

    public static final String GROUPS_TSV_FILENAME = "groups.tsv";

    private static final String TAB = "\t";

    public static final String TEAMS_TSV_FILENAME = "teams.tsv";

    public static final String TEAMS_JSON_FILENAME = "teams.json";

    private static final String OUTPUT_FILE_KEY = "--of";

    private static final String INPUT_FILE_KEY = "--if";

    private static final String OVERWRITE_FILE_KEY = "-f";

    /**
     * Successful run exit code.
     *
     * Using a non-zero exit code because if there is a problem in the JVM or elsewhere a zero exit code could be returned.
     */
    private static final int SUCCESS_EXIT_CODE = 5;

    private static final String NL = System.getProperty("line.separator");

    private static final int FAILURE_EXIT_CODE = 4;

    private boolean debugMode = false;

    private boolean allowOverWrite = false;

    /**
     * Default output file name
     */
    private String outputFileName = TEAMS_TSV_FILENAME;

    /**
     * Count for group ids.
     */
    private Map<String,Integer> groupMap = new HashMap<String, Integer>();

    private String inputFileName = TEAMS_JSON_FILENAME;

    public CreateTeamsTSV(String[] args) throws CommandLineErrorException {
        loadVariables(args);
    }

    @SuppressWarnings("unused")
    private void dprint(String string) {
        if (debugMode) {
            System.out.println(string);
        }
    }

    protected void loadVariables(String[] args) throws CommandLineErrorException {

        String[] argsWithValues = { OUTPUT_FILE_KEY, INPUT_FILE_KEY};
        ParseArguments arguments = new ParseArguments(args, argsWithValues);

        if (arguments.isOptPresent("--help")) {
            usage();
            System.exit(4);
        }

        debugMode = arguments.isOptPresent("--debug");


        allowOverWrite = arguments.isOptPresent(OVERWRITE_FILE_KEY);


        if (debugMode) {
            arguments.dumpArgs(System.err);
        }

        if (arguments.isOptPresent(OUTPUT_FILE_KEY)) {
            outputFileName = arguments.getOptValue(OUTPUT_FILE_KEY);
        }

        if (arguments.isOptPresent(INPUT_FILE_KEY)) {
            inputFileName = arguments.getOptValue(INPUT_FILE_KEY);
        }


    }

    private void fatalError(String errorMessage) {
        System.err.println("Fatal error " + errorMessage);
        System.exit(FAILURE_EXIT_CODE);

    }

    private static void usage() {
        String[] usage = { //
                "", //
                "Usage CreateTeamsTSV [--help] [-f] [--of outputfilename] [--if teams_file.json]", //
                "", //
                "Purpose: read teams.json file create teams.tsv and groups.tsv files.", //
                "", //
                "This will not overwrite output file, use -f to overwrite output file (and "+GROUPS_TSV_FILENAME+")", //
                "", //
                "options:", //
                "--of file  - alternate output filename, default output filename is " + TEAMS_TSV_FILENAME, //
                "--if file  - alternate input JSON filename, default  filename is " + TEAMS_JSON_FILENAME, //
                     "-f   overwrite output file, by default will not overwrite output file.", //
                "", //
                "On success exit code will be " + SUCCESS_EXIT_CODE, //
                "Any other exit code is an error.", //
                "", //
        };

        for (String s : usage) {
            System.out.println(s);
        }
    }

    protected void writeTSVFile() throws Exception {

        if (!allowOverWrite  && new File(outputFileName).exists()) {
            fatalError("Output file exists (" + outputFileName + "), will not overwrite.  Use " + OUTPUT_FILE_KEY + " to write to a different file");
        }

        List<TeamAccount> accounts = loadAccounts(inputFileName);

        String [] header = { "Teams", "1" };

        String headerLine = String.join(TAB, header);

        FileWriter writer = (new FileWriter(new File(outputFileName)));

        writer.write(headerLine + NL);

        for (TeamAccount teamAccount : accounts) {
            // TODO write teams.tsv output file
            //            System.out.println(" debug 22 " + teamAccount.toJSON());

            // No special support for multiple groups as this is a legacy feed and specifies
            // only one group
            String groupId = "1";
            if (teamAccount.getGroup_ids().size() > 0) {
                groupId = teamAccount.getGroup_ids().get(0);
            }

            Integer value = groupMap.get(groupId);
            if (value == null) {
                value = 0;
            }
            groupMap.put(groupId, value.intValue()+1);

            /**
                     1  Team Number     22  integer
2   External ID     24314   integer
3   Group ID    4   integer
4   Team name   Hoos    string
5   Institution name    University of Virginia  string
6   Institution short name  U Virginia  string
7   Country Code    USA     string ISO 3166-1 alpha-3
             */
            String [] fields = { //
                    ""+teamAccount.getId(),
                    teamAccount.getIcpc_id(), //
                    groupId, //
                    teamAccount.getName(), //
                    teamAccount.getDisplay_name(), //
                    "",
                    "XXX", //
            };

            writer.write(String.join(TAB,fields)+ NL);
        }

        writer.close();
        writer = null;

    }

    protected List<TeamAccount> loadAccounts(String jsonFilename) throws JsonParseException, JsonMappingException, IOException {

        if (! new File(jsonFilename).exists()) {
            throw new FileNotFoundException(jsonFilename);
        }

        ObjectMapper mapper = new ObjectMapper();
        String[] lines = Utilities.loadFile(jsonFilename);
        List<TeamAccount> list = Arrays.asList(mapper.readValue(lines[0], TeamAccount[].class));

        return list;
    }

    protected void updateGroupsTSV() throws IOException {

        String outDir = new File(outputFileName).getParent();
        if (outDir == null) {
            outDir = ".";
        }
        String outGroupName = outDir + File.separator + GROUPS_TSV_FILENAME;
        FileWriter writer = (new FileWriter(new File(outGroupName)));

        String[] groupHeaerFields = { "groups", "1" };

        writer.write(String.join(TAB, groupHeaerFields) + NL);

        Set<String> groupIds = groupMap.keySet();
        String[] ids = groupIds.toArray(new String[groupIds.size()]);
        Arrays.sort(ids);

        for (String id : ids) {
            writer.write(id + TAB + "Group " + id + NL);
        }

        writer.close();
        writer = null;

    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public static void main(String[] args) {

        try {
            CreateTeamsTSV creator = new CreateTeamsTSV(args);
            System.out.println("Input: "+creator.getInputFileName());
            creator.writeTSVFile();
            System.out.println("Output: "+creator.getOutputFileName() + ", " + GROUPS_TSV_FILENAME);
            creator.updateGroupsTSV();
            System.exit(SUCCESS_EXIT_CODE);
        } catch (CommandLineErrorException e) {
            System.err.println("Error on command line: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }


}
