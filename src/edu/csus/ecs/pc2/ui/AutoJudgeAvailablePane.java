// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.AvailableAJRun;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Pane to show Autoudge Runs and Judges that are available to be autojudged.
 * 
 * @author Douglas A. Lane <laned@csus.edu>
 *
 */
// TODO 496 Add sort/column header
// TODO 496 auto size, esp Problems
// TODO 496 if judge's auto judge settings, problem list or on/off change update judge list
// TODO 496 dynamic update when judge available
// TODO 496 dynamic update runs table
// TODO 496 add count for # judges
// TODO 496 add count for number of problems
// TODO 496 on judges table, get Judge display name or remove column
public class AutoJudgeAvailablePane extends JPanePlugin {
    /**
     * 
     */
    private static final long serialVersionUID = 2645127695835000107L;

    JTableCustomized judgeCustomizeTable = null;

    JTableCustomized runsCustomizeTable = null;

    private DefaultTableModel runsTableModel;

    private DefaultTableModel judgesTableModel;

    public AutoJudgeAvailablePane() {
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        centerPane.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                splitPane.setDividerLocation(splitPane.getHeight() / 2);
            }
        });
        centerPane.add(splitPane);

        JPanel rusPanel = new JPanel();
        splitPane.setRightComponent(rusPanel);
        rusPanel.setLayout(new BorderLayout(0, 0));

        JPanel runNorthPane = new JPanel();
        rusPanel.add(runNorthPane, BorderLayout.NORTH);
        runNorthPane.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel = new JLabel("Available Runs");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        runNorthPane.add(lblNewLabel, BorderLayout.CENTER);

        JPanel runsCenterPanel = new JPanel();
        rusPanel.add(runsCenterPanel, BorderLayout.CENTER);
        runsCustomizeTable = getrunsCustomizeTab1le();
        runsCenterPanel.add(runsCustomizeTable);

        JPanel judgesPanel = new JPanel();
        splitPane.setLeftComponent(judgesPanel);
        judgesPanel.setLayout(new BorderLayout(0, 0));

        JPanel judgesNorthPanel = new JPanel();
        judgesPanel.add(judgesNorthPanel, BorderLayout.NORTH);
        judgesNorthPanel.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel_1 = new JLabel("Available Judges");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        judgesNorthPanel.add(lblNewLabel_1, BorderLayout.NORTH);

        JPanel judgesCEnterPael = new JPanel();
        judgesPanel.add(judgesCEnterPael);

        judgeCustomizeTable = getjudgeCustomizeTable();
        judgesCEnterPael.add(judgeCustomizeTable);

        JPanel buttonPane = new JPanel();
        add(buttonPane, BorderLayout.SOUTH);
    }

    private JTableCustomized getrunsCustomizeTab1le() {
        if (runsCustomizeTable == null) {
            runsCustomizeTable = new JTableCustomized();
            Object[] columns = { "Site", "Team", "Run Id", "Time", "Status", "ElementId" };
            runsTableModel = new DefaultTableModel(columns, 0);
            runsCustomizeTable.setModel(runsTableModel);

            TableColumnModel tableColumnModel = runsCustomizeTable.getColumnModel();
            // Remove ElementID from display - this does not REMOVE the column, just makes it so it doesn't show
            tableColumnModel.removeColumn(tableColumnModel.getColumn(columns.length - 1));
        }

        return runsCustomizeTable;
    }

    private Object[] buildJudgeRow(AvailableAJ availableAJ) {

        // Object[] columns = { "Site", "login", "Display Name", "Problems", "TripletKey" };
        int cols = runsTableModel.getColumnCount();
        Object[] s = new Object[cols];

        ClientId judgeClient = availableAJ.getClientId();

        s[0] = new Integer(judgeClient.getSiteNumber());
        s[1] = judgeClient.getName();
        s[2] = judgeClient.getName(); // TODO 496 get Judge display name or remove column
        s[3] = toString(availableAJ.getProblemList());
        s[4] = judgeClient.getTripletKey();

        return s;

    }

    private Object toString(ProblemList problemList) {
        String s = "";
        Problem[] problems = problemList.getList();
        return Arrays.toString(problems);
    }

    private Object[] buildRunRow(AvailableAJRun availableAJRun) {

//        Object[] columns = { "Site", "Team", "Run Id", "Time", "Status" };

        int cols = runsTableModel.getColumnCount();
        Object[] s = new Object[cols];

        ElementId id = availableAJRun.getRunId();
        Run run = getContest().getRun(id);

        s[0] = new Integer(run.getSiteNumber());
        s[1] = run.getSubmitter().getName();
        s[2] = new Integer(run.getNumber());
        s[3] = new Long(run.getElapsedMins());
        s[4] = run.getStatus().toString();
        s[5] = run.getElementId();

        return s;
    }

    /**
     * This method initializes the runTable
     * 
     * @return JTableCustomized
     */
    private JTableCustomized getjudgeCustomizeTable() {
        if (judgeCustomizeTable == null) {
            judgeCustomizeTable = new JTableCustomized();
            Object[] columns = { "Site", "login", "Display Name", "Problems", "TripletKey" };

            judgesTableModel = new DefaultTableModel(columns, 0);
            judgesTableModel.setRowCount(0);
            judgeCustomizeTable.setModel(judgesTableModel);

            TableColumnModel tableColumnModel = judgeCustomizeTable.getColumnModel();
            // Remove ElementID from display - this does not REMOVE the column, just makes it so it doesn't show
            tableColumnModel.removeColumn(tableColumnModel.getColumn(columns.length - 1));
        }
        return judgeCustomizeTable;
    }

    @Override
    public String getPluginTitle() {
        return "Autojudge Available Runs and Judges";
    }

    public void updateJudgeRow(AvailableAJ availableAJ) {
        Object[] objects = buildJudgeRow(availableAJ);
        judgesTableModel.addRow(objects);
    }

    public void updateRunRow(AvailableAJRun availableAJRun) {
        Object[] objects = buildRunRow(availableAJRun);
        runsTableModel.addRow(objects);
    }

}
