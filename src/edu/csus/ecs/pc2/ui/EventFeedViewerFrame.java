package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.EventFeedDefinition;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.EventFeedXML;
import edu.csus.ecs.pc2.exports.ccs.ResolverEventFeedXML;

/**
 * View a selected Event Feed.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// SOMEDAY implement this

// $HeadURL$
public class EventFeedViewerFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4525283106661014975L;

    private JPanel contentPanel = null;

    private JPanel buttonPane = null;

    private JButton closeButton = null;

    private JScrollPane jScrollPane = null;

    private JTextArea eventFeedTextArea = null;

    private IInternalContest contest;

    /**
     * This method initializes
     * 
     */
    public EventFeedViewerFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(488, 247));
        this.setTitle("View Event Feed ");
        this.setContentPane(getContentPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeWindow();
            }
        });

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        contest = inContest;

    }

    public void setEventFeed(EventFeedXML xml) {
        // SOMEDAY CCS
    }

    public String getPluginTitle() {
        return "Event Feed Viewer";
    }

    /**
     * This method initializes contentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(getButtonPane(), BorderLayout.SOUTH);
            contentPanel.add(getJScrollPane(), BorderLayout.CENTER);
        }
        return contentPanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setMnemonic(KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeWindow();
                }
            });
        }
        return closeButton;
    }

    protected void closeWindow() {
        setVisible(false);
        dispose();
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getEventFeedTextArea());
        }
        return jScrollPane;
    }

    /**
     * This method initializes eventFeedTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getEventFeedTextArea() {
        if (eventFeedTextArea == null) {
            eventFeedTextArea = new JTextArea();
        }
        return eventFeedTextArea;
    }

    public void setEventFeedDefinition(EventFeedDefinition definition) {
        
        
        // SOMEDAY implement this
        // SOMEDAY CCS actually read from definition port
        // SOMEDAY CCS update event feed. 
        
        try {
            setTitle("Event Feed: " + definition.getDisplayName());
            ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
            String xml = eventFeedXML.toXML(contest);
            getEventFeedTextArea().setText(xml);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
