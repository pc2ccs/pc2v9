package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;

import edu.csus.ecs.pc2.core.model.Filter;

import java.awt.FlowLayout;

/**
 * Edit a filter.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditFilterFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 6498270977601785261L;

    private JPanel mainPane = null;

    private JPanel buttonPane = null;

    private JButton saveButton = null;

    private JButton refreshButton = null;

    private JButton closeButton = null;

    private EditFilterPane editFilterPane = null;
    
    private Filter filter = new Filter();

    /**
     * This method initializes
     * 
     */
    public EditFilterFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(459, 313));
        this.setTitle("Edit Filter");
        this.setContentPane(getMainPane());

        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            mainPane = new JPanel();
            mainPane.setLayout(new BorderLayout());
            mainPane.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
            mainPane.add(getEditFilterPane(), java.awt.BorderLayout.CENTER);
        }
        return mainPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getSaveButton(), null);
            buttonPane.add(getRefreshButton(), null);
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes saveButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save");
            saveButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("saveButton actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return saveButton;
    }

    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("refreshButton actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return refreshButton;
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
            closeButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("closeButton actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return closeButton;
    }

    /**
     * This method initializes editFilterPane
     * 
     * @return edu.csus.ecs.pc2.ui.EditFilterPane
     */
    private EditFilterPane getEditFilterPane() {
        if (editFilterPane == null) {
            editFilterPane = new EditFilterPane();
            editFilterPane.setParentFrame(this);
        }
        return editFilterPane;
    }
    
 
} // @jve:decl-index=0:visual-constraint="10,10"
