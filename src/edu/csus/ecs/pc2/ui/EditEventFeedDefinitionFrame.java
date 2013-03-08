package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.EventFeedDefinition;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Add/Update an Event Feed definition frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditEventFeedDefinitionFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6272031615463048759L;

    private JPanel editPane = null;

    private EditEventFeedDefinitionPane editEventFeedDefinitionPane = null;

    /**
     * This method initializes
     * 
     */
    public EditEventFeedDefinitionFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(441, 246));
        this.setTitle("Event Feed Definition Edit");
        this.setContentPane(getEditPane());

        FrameUtilities.centerFrame(this);

    }

    /**
     * This method initializes editPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEditPane() {
        if (editPane == null) {
            editPane = new JPanel();
            editPane.setLayout(new BorderLayout());
            editPane.add(getEditEventFeedDefinitionPane(), BorderLayout.CENTER);
        }
        return editPane;
    }

    /**
     * This method initializes eventFeedPane
     * 
     * @return edu.csus.ecs.pc2.ui.EventFeedPane
     */
    private EditEventFeedDefinitionPane getEditEventFeedDefinitionPane() {
        if (editEventFeedDefinitionPane == null) {
            editEventFeedDefinitionPane = new EditEventFeedDefinitionPane();
            editEventFeedDefinitionPane.setParentFrame(this);
        }
        return editEventFeedDefinitionPane;
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getEditEventFeedDefinitionPane().setContestAndController(inContest, inController);
    }
    
    public String getPluginTitle() {
        return "Edit Event Feeds Frame";
    }
    
    public void setEventDefinition(EventFeedDefinition eventFeedDefinition){
        editEventFeedDefinitionPane.setEventDefinition(eventFeedDefinition);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
