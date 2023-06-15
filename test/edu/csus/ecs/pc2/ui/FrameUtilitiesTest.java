// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class FrameUtilitiesTest extends AbstractTestCase{

	
    private static String displayFormatWhatsThisMessage = //
            "\nThe Team Scoreboard Display Format field allows you to specify a string which defines the format in which team names " //
            + "will be displayed on the PC^2 Scoreboard." //

            + "\n\nThe format string is a pattern which (typically) contains \"substitution variables\", identified by substrings starting with \"{:\"" //
            + " and ending with \"}\" (for example, {:teamname} )." //
            + "\nPC^2 automatically replaces substitution variables with the corresponding value for each team" //
            + " (for example, the substitution variable {:teamname} "  //
            + "\ngets replaced on the scoreboard with each team's name as defined in the PC^2 Server)." //
            + "\n\nLiteral characters (i.e., anything NOT part of a substituion variable) are displayed exactly as written in the format string." //

            + "\n\nRecognized substitution variables include:" //
            + "\n    {:teamname}                -- the name of the team (for example, \"Hot Coders\")" //
            + "\n    {:teamloginname}       -- the account name which the team uses to login to PC^2 (e.g., \"team102\")" //
            + "\n    {:clientnumber}           -- the PC^2 client (team) number for the team (e.g., \"102\")" //
            + "\n    {:shortschoolname}  -- the short name of the team's school (e.g., \"CSUS\" or \"UCB\")" //
            + "\n    {:longschoolname}    -- the long name of the team's school (e.g., \"California State University, Sacramento\"" //
            + "\n    {:groupname}             -- the name of the group (if any) to which the team is assigned (e.g., \"Upper Division\" or \"Northern Site\")" //
            + "\n    {:groupid}                     -- the id number of the group (if any) to which the team is assigned (e.g., \"1\" or \"201\")" //
            + "\n    {:sitenumber}             -- the PC^2 site number (in a multi-site contest) to which the team logs in (e.g., \"1\" or \"5\")" //
            + "\n    {:countrycode}           -- the ISO Country Code associated with the team (e.g. \"CAN\" or \"USA\")" //
            + "\n    {:externalid}                -- the ICPC CMS id number (if any) associated with the team (e.g., \"309407\")" //
            
            + "\n\nSo for example a display format string like \"{:teamname} ({:shortschoolname}) might display the following on the scoreboard:" //
            + "\n    Hot Coders (CSUS) " //
            + "\n(Notice the addition of the literal parentheses around the short school name.)" //
            
            + "\n\nSubstitution values depend on the corresponding data having been loaded into the PC^2 Server; if there is no value defined for a" //
            + "\nspecified substitution string then the substitution string itself appears in the result."
            + " If the defined value is null or empty then an empty string appears in the result."

            + "\n\n"; //

	
	private static JLabel getTestLabel() {
		
//		String buttonName = "button name";
//		String toolTip = "Tool tip";
//		String message = "Message";
//		String messageTitle = "Message Title";
//		
//		return FrameUtilities2.getToolTipLabel(buttonName, toolTip, messageTitle, message );
		
		String messageTitle = "About Team Scoreboard Display Format Strings";
//		String message = displayFormatWhatsThisMessage;
		
		return FrameUtilities.getWhatsThisLabel(messageTitle, displayFormatWhatsThisMessage);
//		
		
	}

    public class ButtonPane extends JPanePlugin {

        private static final long serialVersionUID = -1023036377267585512L;

        public ButtonPane(JLabel label) {
            JLabel btnNewButton = label;
            add(btnNewButton);
        }

        @Override
        public String getPluginTitle() {
            return "Button Pane";
        }

    }
		
    public static void main(String[] args) {
        FrameUtilitiesTest fut = new FrameUtilitiesTest();
        ButtonPane pane = fut.new ButtonPane(getTestLabel());
        TestingFrame frame = new TestingFrame(pane);
        frame.setSize(800, 800);
        FrameUtilities.centerFrame(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
