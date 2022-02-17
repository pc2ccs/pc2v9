// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;

import java.util.Arrays;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.reports.APIReports;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.ui.team.CreateTeamsTSV;
import edu.csus.ecs.pc2.ui.team.SubmitterFromEF;

/**
 * PC2 Tools - pc2 utilities front end.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PC2Tools {

// Automation tasks left 
// TODO	 PC2Tools --loggedin - list of all servers and clients that are logged in
// TODO fetch information from CCS REST end points 

    private static void usage() {

        String[] usage = { //
        //
                "Usage: PC2Tools [--help] --login LOGIN [--password PASSWORD] --mer | --gen --startnow | --shutdown | --startat DATETIME | --startin TIMESTR ", //
                "Usage: PC2Tools [--help] --sr --login LOGIN [--password PASSWORD] filename ", //
                "", //
                "--gen   - generate passwords to stdout, use --help --gen for usage", //
                "--mer   - prints mail merge file to stdtout, use --help --mer for usage", //
                "--sr   -  submit runs from CLICS JSON EF, use --help --sr for usage", //
                "--ttsv -  convert teams.json to teams.tsv, use --help --ttsv for usage", //
                "--upjdf  -  update judge data file names for a CDP or problem", //
                "",
                
                "--login LOGIN ", //
                "--password PASSWORD ", //
                "", //
                
                "--startat DATETIME - DATETIME can be a date/time in one of these forms: ", //
                "                      HH:mm:ss, HH:mm, MM MM MM yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm", //
                "--startin TIMESTR  - start in seconds or minutes, TIMESTR = #### | ####min", //
                "--shutdown         - shtudown all servers on all sites now.", //
                "--startnow         - start contest now", //
                "",
                
                "--help  - this message", //
                "", };

        for (String s : usage) {
            System.out.println(s);
        }
        
        VersionInfo vi = new VersionInfo();

        String info = "Version " + vi.getPC2Version() + " build " + vi.getBuildNumber();
        System.out.println(info);
    }

    public void run(String[] args) {
        
        if (args.length == 0) {
            usage();
            System.exit(4);
        }

        ParseArguments parseArguments = new ParseArguments(args);
        
        /**
         * Remove first option from command line options.  Shift right.
         */
        String[] options = Arrays.copyOfRange(args, 1, args.length);

        if (parseArguments.isOptPresent("--gen")) {

            PasswordGenerator.main(args);
            
        } else if (parseArguments.isOptPresent("--apir")) {

            APIReports.main(options);
            
        } else if (parseArguments.isOptPresent("--sr")) {

            SubmitterFromEF.main(args);

        } else if (parseArguments.isOptPresent("--ttsv")) {

            CreateTeamsTSV.main(options);
            
        } else if (parseArguments.isOptPresent("--upjdf")) {

            UpdateJudgesDataFileNames.main(options);

        } else if (parseArguments.isOptPresent("--mer")) {

            PrintMergeFile.main(args);
            
        } else if (parseArguments.isOptPresent("--shutdown")) {

            ContestControl.main(args);

        } else if (parseArguments.isOptPresent("--startnow")) {

            ContestControl.main(args);

        } else if (parseArguments.isOptPresent("--startin")) {

        	ContestControl.main(args);

        } else if (parseArguments.isOptPresent("--startat")) {

        	ContestControl.main(args);

        } else if (args.length == 0 || args[0].equals("--help")) {
            usage();
            System.exit(4);
        } else {
            System.err.println("Unknown option, check usage using --help ");
        }
    }

    public static void main(String[] args) {
        new PC2Tools().run(args);
    }
}
