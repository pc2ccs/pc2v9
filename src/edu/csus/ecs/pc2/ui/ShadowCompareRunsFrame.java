// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.shadow.ShadowController;

/**
 * A JFrame which displays a Shadow comparison results pane.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareRunsFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private boolean isOpen = true;
    
    public ShadowCompareRunsFrame(ShadowController shadowController) {
        setMinimumSize(new Dimension(750, 900));
        Dimension size = new Dimension(750,750);
        this.setPreferredSize(size);
//        this.setMinimumSize(size);
        
        ShadowCompareRunsPane runsPane = new ShadowCompareRunsPane(shadowController);
        runsPane.setMinimumSize(new Dimension(750, 900));
        runsPane.setPreferredSize(new Dimension(750, 900));
        
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                // This will be called after the window has closed
                isOpen = false;
            }
        });
//        runsPane.setContestAndController(shadowController.getLocalContest(), shadowController.getLocalController());
        //the above statement was moved into the ShadowCompareRunsPane() constructor, as follows:
        //   this.setContestAndController(shadowController.getLocalContest(), shadowController.getLocalController());
        // This allows the ShadowCompareRunsPane object to reference the contest and controller during construction, 
        // rather than having to wait for some (non-guaranteed) external code to initialize the contest and controller.  
        // (This fixes a Technical Deficit present in most other PC2 panes, which allow themselves to be constructed 
        // in an invalid state -- existing but having no contest or controller with which to work.)

        this.getContentPane().add(runsPane);
    }
    
    public boolean isOpen() {
        return isOpen;
    }

}
