// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.admin;

import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.TestingFrame;
import junit.framework.TestCase;

public class GenerateAndMergePasswordPaneTest extends TestCase {
	
	
	public static void main(String[] args) {
		
		JPanePlugin pane = new GenerateAndMergePasswordPane();
		TestingFrame frame = new TestingFrame(pane);
		FrameUtilities.centerFrame(frame);
		frame.setVisible(true);
	}

}
