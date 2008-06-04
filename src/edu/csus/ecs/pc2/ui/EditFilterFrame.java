package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.FilterReport;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

import java.awt.FlowLayout;
import java.io.PrintWriter;

/**
 * Edit a filter.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditFilterFrame extends JFrame implements UIPlugin {

    // TODO code close button
    // TODO on close button if they say yes, invoke callback.

    /**
     * 
     */
    private static final long serialVersionUID = 6498270977601785261L;

    private JPanel mainPane = null;

    private JPanel buttonPane = null;

    private JButton applyButton = null;

    private JButton closeButton = null;

    private EditFilterPane editFilterPane = null;

    private Filter filter = new Filter();

    private IInternalContest contest;

    private IInternalController controller;

    private Runnable refreshCallback = null;

    private JButton okButton = null;

    /**
     * This method initializes
     * 
     */
    public EditFilterFrame() {
        super();
        initialize();
    }

    /**
     * 
     * @param filter
     * @param title
     * @param refreshCallback
     *            when filter changes, invokes this method.
     */
    public EditFilterFrame(Filter filter, String title, Runnable refreshCallback) {
        super();
        initialize();
        this.filter = filter;
        setTitle(title);
        this.refreshCallback = refreshCallback;
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(784, 313));
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
            buttonPane.add(getOkButton(), null);
            buttonPane.add(getApplyButton(), null);
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes saveButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getApplyButton() {
        if (applyButton == null) {
            applyButton = new JButton();
            applyButton.setText("Apply");
            applyButton.setToolTipText("Apply this filter to listbox");
            applyButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            applyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateFilter(editFilterPane.getFilter());
                }
            });
        }
        return applyButton;
    }

    protected void updateFilter(Filter filter2) {

        System.out.println("debug updateFilter " + filter2);
        System.out.flush();

        dumpFilter(filter2);

        if (refreshCallback != null) {
            refreshCallback.run();
        } else {
            System.err.println("Warning: no callback set, no refresh of list based on this filter");
        }
    }

    protected void dumpFilter(Filter filter2) {

        try {
            System.out.println("dumpFilter " + filter2);
            System.out.flush();

            FilterReport filterReport = new FilterReport();
            filterReport.setContestAndController(contest, controller);

            PrintWriter printWriter = new PrintWriter(System.out);
            filterReport.writeReportDetailed(printWriter, filter2);
            printWriter.flush();
            printWriter = null;

            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: log handle exception
            // log.log(Log.WARNING, "Exception logged ", e);
        }

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
            closeButton.setToolTipText("Close this window");
            closeButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    checkForChangesAndExit();
                }
            });
        }
        return closeButton;
    }

    protected void checkForChangesAndExit() {
        
        // TODO check for changes in filter. 
        
        setVisible(false);
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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        editFilterPane.setContestAndController(inContest, inController);
        editFilterPane.setFilter(filter);
    }

    public String getPluginTitle() {
        return "Edit Filter Frame";
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public void setFilter(Filter filter2) {
        getEditFilterPane().setFilter(filter2);
    }

    /**
     * This method initializes okButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("Ok");
            okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateFilter(editFilterPane.getFilter());
                    setVisible(false);
                }
            });
        }
        return okButton;
    }

    /**
     * Show or hide list on edit filter frame.
     * 
     * @param listNames
     *            list to show or hid
     * @param showList
     *            true show list, false do not show list.
     */
    public void showJList(ListNames listNames, boolean showList) {
        editFilterPane.showJList(listNames, showList);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
