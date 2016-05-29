package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * This class implements a plug-in pane containing various games which can be played by bored 
 * Judges, Admins, etc.
 * 
 * @author john clevenger
 *
 */
public class ImBoredPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JLabel boredLabel = null;
    private JPanel boredButtonPanel = null;
    private JPanel boredEastSpacerPanel = null;
    private JPanel boredWestSpacerPanel = null; 
    private JButton boredPanelClickMeButton = null;
    
    public ImBoredPane() {
        super();
        initialize();
    }
    
    private void initialize() {
        
        setName("BoredPage");
        setToolTipText("Stuff to do when you\'re bored");
        setLayout(new BorderLayout());
        add(getBoredLabel(), "Center");
        add(getBoredButtonPanel(), "South");
        add(getBoredWestSpacerPanel(), "West");
        add(getBoredEastSpacerPanel(), "East");
    }

    private JLabel getBoredLabel() {
        if (boredLabel == null) {
            try {
                boredLabel = new JLabel();
                boredLabel.setName("BoredLabel");
                boredLabel.setFont(new Font("dialog", 1, 18));
                boredLabel.setText("Hey!  Aren\'t you supposed to be JUDGING stuff?");
                boredLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return boredLabel;
    }

    private JPanel getBoredButtonPanel() {
        if (boredButtonPanel == null) {
            try {
                boredButtonPanel = new JPanel();
                boredButtonPanel.setName("BoredButtonPanel");
                boredButtonPanel.setPreferredSize(new Dimension(10, 50));
                boredButtonPanel.setLayout(getBoredButtonPanelFlowLayout());
                boredButtonPanel.add(getBoredPanelClickMeButton());
            } catch (java.lang.Throwable throwable) {
                handleException(throwable);
            }
        }
        return boredButtonPanel;
    }
    
    private FlowLayout getBoredButtonPanelFlowLayout() {
        FlowLayout boredButtonPanelFlowLayout = null;
        try {
            boredButtonPanelFlowLayout = new FlowLayout();
            boredButtonPanelFlowLayout.setVgap(10);
            boredButtonPanelFlowLayout.setHgap(20);
        } catch (java.lang.Throwable throwable) {
            handleException(throwable);
        }
        return boredButtonPanelFlowLayout;
    }

    private JPanel getBoredEastSpacerPanel() {
        if (boredEastSpacerPanel == null) {
            try {
                boredEastSpacerPanel = new JPanel();
                boredEastSpacerPanel.setName("BoredEastSpacerPanel");
                boredEastSpacerPanel.setPreferredSize(new java.awt.Dimension(20, 20));
                boredEastSpacerPanel.setLayout(null);
            } catch (java.lang.Throwable throwable) {
                handleException(throwable);
            }
        }
        return boredEastSpacerPanel;
    }

    private JButton getBoredPanelClickMeButton() {
        if (boredPanelClickMeButton == null) {
            try {
                boredPanelClickMeButton = new JButton();
                boredPanelClickMeButton.setName("BoredPanelClickMeButton");
                boredPanelClickMeButton.setText("Click Me");
                boredPanelClickMeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ClickMeFrame cmFrame = new ClickMeFrame("Click Me");
                        cmFrame.setVisible(true);
                    }
                } );
            } catch (java.lang.Throwable throwable) {
                handleException(throwable);
            }
        }
        return boredPanelClickMeButton;
    }

    private JPanel getBoredWestSpacerPanel() {
        if (boredWestSpacerPanel == null) {
            try {
                boredWestSpacerPanel = new JPanel();
                boredWestSpacerPanel.setName("BoredWestSpacerPanel");
                boredWestSpacerPanel.setPreferredSize(new java.awt.Dimension(20, 20));
                boredWestSpacerPanel.setLayout(null);
            } catch (java.lang.Throwable throwable) {
                handleException(throwable);
            }
        }
        return boredWestSpacerPanel;
    }

    @Override
    public String getPluginTitle() {
        return "I\'m Bored Pane";
    }

    private void handleException(Throwable t) {
        System.err.println("Exception in I\'mBoredPane: " + t);
    }

}
