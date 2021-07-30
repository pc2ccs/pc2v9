// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    private static final String TAB = "\t";

    private static final String TEAMS_TSV_FILENAME = "teams.tsv";

    private static final String TEAMS_JSON_FILENAME = "teams.json";

    private static final String OUTPUT_FILE_KEY = "--of";

    /**
     * Successful run exit code.
     *
     * Using a non-zero exit code because if there is a problem in the JVM or elsewhere a zero exit code could be returned.
     */
    private static final int SUCCESS_EXIT_CODE = 5;

    private static final String NL = System.getProperty("line.separator");

    private static final int FAILURE_EXIT_CODE = 4;

    private boolean debugMode = false;

    /**
     * Default output file name
     */
    private String outputFileName = TEAMS_TSV_FILENAME;

    public CreateTeamsTSV(String[] args) throws CommandLineErrorException {

    }

    @SuppressWarnings("unused")
    private void dprint(String string) {
        if (debugMode) {
            System.out.println(string);
        }
    }

    protected void loadVariables(String[] args) throws CommandLineErrorException {

        ParseArguments arguments = new ParseArguments(args);

        if (arguments.isOptPresent("--help")) {
            usage();
            System.exit(4);
        }

        debugMode = arguments.isOptPresent("--debug");

        if (debugMode) {
            arguments.dumpArgs(System.err);
        }

        if (arguments.isOptPresent(OUTPUT_FILE_KEY)) {
            outputFileName = arguments.getOptValue(OUTPUT_FILE_KEY);
        }

    }

    private void fatalError(String errorMessage) {
        System.err.println("Fatal error " + errorMessage);
        System.exit(FAILURE_EXIT_CODE);

    }

    private static void usage() {
        String[] usage = { //
                "", //
                "Usage CreateTeamsTSV [--help] [--of outputfilename]", //
                "", //
                "Purpose: read teams.json file create teams.tsv file.", //
                "", //
                "where:", //
                "--of file  - alternate output filename, default output filename is " + TEAMS_TSV_FILENAME, "", //
                "On success exit code will be " + SUCCESS_EXIT_CODE, //
                "Any other exit code is an error.", //
                "", //
        };

        for (String s : usage) {
            System.out.println(s);
        }
    }

    private void writeTSVFile() throws Exception {

        if (new File(outputFileName).exists()) {
            fatalError("Output file exists (" + outputFileName + "), will not overwrite.  Use " + OUTPUT_FILE_KEY + " to write to a different file");
        }

        List<TeamAccount> accounts = loadAccounts(TEAMS_JSON_FILENAME);

        String [] header = { "Teams", "1" };

        String headerLine = String.join(TAB, header);

        FileWriter writer = (new FileWriter(new File(outputFileName)));

        writer.write(headerLine + NL);

        for (TeamAccount teamAccount : accounts) {
            // TODO write teams.tsv output file
            //            System.out.println(" debug 22 " + teamAccount.toJSON());

            String groupId = "1";
            if (teamAccount.getGroup_ids().size() > 0) {
                groupId = teamAccount.getGroup_ids().get(0);
            }
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
    
    public String getOutputFileName() {
        return outputFileName;
    }

    public static void main(String[] args) {

        try {
            CreateTeamsTSV creator = new CreateTeamsTSV(args);
            System.out.println("Input: "+TEAMS_JSON_FILENAME);
            creator.writeTSVFile();
            System.out.println("Output: "+creator.getOutputFileName());
            System.exit(SUCCESS_EXIT_CODE);
        } catch (CommandLineErrorException e) {
            System.err.println("Error on command line: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
