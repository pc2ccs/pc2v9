// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountNameCaseComparator;
import edu.csus.ecs.pc2.core.list.StringToNumberComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

/**
 * Shows clarifications in a list box.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationsTablePane extends JPanePlugin {
   
    /**
     * 
     */
    private static final long serialVersionUID = 3155024092274820185L;
    
    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;

    private JPanel clarificationButtonPane = null;

    private JTableCustomized clarificationTable = null;
    private DefaultTableModel clarificationTableModel = null;

    private JButton giveButton = null;

    private JButton takeButton = null;

    private JButton editButton = null;

    private JButton filterButton = null;

    private JButton requestButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private AnswerClarificationFrame answerClarificationFrame;

    private JPanel centerPane = null;

    private JSplitPane clarificationSplitPane = null;

    private JPanel clarificationPane = null;

    private JTextArea questionTextArea = null;

    private JPanel answerPane = null;

    private JTextArea answerTextArea = null;

    private boolean showNewClarificationsOnly = false;
    
    private JScrollPane scrollPane = null;
    
    /**
     * Filter that does not change.
     * 
     * Used to do things like insure only New clarifications are shown.
     */
    private Filter requiredFilter = new Filter();

    private Filter filter =  new Filter();

    private DisplayTeamName displayTeamName = null;

    private JScrollPane jQuestionScrollPane = null;

    private JScrollPane jAnswerScrollPane = null;
    
    private EditFilterFrame editFilterFrame = null;
    
    private String filterFrameTitle = "Clarification filter";

    /*
     * This should track the lastAnswered clar,
     * and be reset to null when that clar is new or answered or deleted.
     */
    private ElementId lastAnswered = null;
    /**
     * This method initializes
     * 
     */
    public ClarificationsTablePane() {
        super();
        initialize();
    }

    /**
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            // ignore, does not affect this pane
        }

        public void languageChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void languageRemoved(LanguageEvent event) {
            // ignore, does not affect this pane
        }

        public void languageRefreshAll(LanguageEvent event) {
            languageChanged(event);
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            // ignore, does not affect this pane
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

    }

    /**
     * @author pc2@ecs.csus.edu
     * 
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            // ignore, does not affect this pane
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            }); 
        }

    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(622, 327));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getClarificationButtonPane(), java.awt.BorderLayout.SOUTH);

        answerClarificationFrame = new AnswerClarificationFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Clarifications Table Pane";
    }

    /**
     * This method initializes clarificationButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationButtonPane() {
        if (clarificationButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            clarificationButtonPane = new JPanel();
            clarificationButtonPane.setLayout(flowLayout);
            clarificationButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            clarificationButtonPane.add(getRequestButton(), null);
            clarificationButtonPane.add(getGiveButton(), null);
            clarificationButtonPane.add(getTakeButton(), null);
            clarificationButtonPane.add(getFilterButton(), null);
            clarificationButtonPane.add(getEditButton(), null);
        }
        return clarificationButtonPane;
    }
    
    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getClarificationTable());
        }
        return scrollPane;
    }

    /**
     * This method initializes clarificationListBox
     * 
     * @return JTableCustomized
     */
    private JTableCustomized getClarificationTable() {
        if (clarificationTable == null) {
            clarificationTable = new JTableCustomized();

            clarificationTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    JTable target = (JTable)me.getSource();
                    if(target.getSelectedRow() != -1) {
                        // Double click and allowed to answer clarification
                        if (me.getClickCount() == 2 && isAllowed(Permission.Type.ANSWER_CLARIFICATION)) {
                            requestSelectedClarification();
                        } else {
                            showSelectedClarification();
                        }
                    }
                }
            // do the reset of the columns after we have contest
            });
        }
        return clarificationTable;
    }

    private void resetClarsListBoxColumns() {

        clarificationTable.removeAll();
        Object[] fullColumns =        { "Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer", "ElementID" };
        Object[] newColumns =         { "Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question", "ElementID" };
        Object[] teamColumns =        { "Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question", "Answer", "ElementID" };
        Object[] columns;

        if (isTeam(getContest().getClientId())) {
            columns = teamColumns;
        } else {
            if (isShowNewClarificationsOnly()) {
                columns = newColumns;
            } else {
                columns = fullColumns;
            }
        }
        clarificationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        clarificationTable.setModel(clarificationTableModel);
        
        TableColumnModel tcm = clarificationTable.getColumnModel();
        // Remove ElementID from display - this does not REMOVE the column, just makes it so it doesn't show
        tcm.removeColumn(tcm.getColumn(columns.length - 1));

        // Sorters
        TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(clarificationTableModel);
        
        clarificationTable.setRowSorter(trs);
        clarificationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        ArrayList<SortKey> sortList = new ArrayList<SortKey>();
        
        /*
         * Column headers left justified
         */
        ((DefaultTableCellRenderer)clarificationTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        clarificationTable.setRowHeight(clarificationTable.getRowHeight() + VERT_PAD);
                
        StringToNumberComparator numericStringSorter = new StringToNumberComparator();
        AccountNameCaseComparator accountNameSorter = new AccountNameCaseComparator();
        
        int idx = 0;

        if (!isTeam(getContest().getClientId())) {
//          Object[] newColumns =         { "Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question", "ElementID" };
//          Object[] fullColumns =        { "Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer", "ElementID" };
            
            // These are in column order - omitted ones are straight string compare
            trs.setComparator(0, accountNameSorter);
            trs.setComparator(1, accountNameSorter);
            trs.setComparator(2, numericStringSorter);
            trs.setComparator(3, numericStringSorter);
            if (!isShowNewClarificationsOnly()) {
                trs.setComparator(5, accountNameSorter);
                trs.setComparator(6, accountNameSorter);
            }
            
            // These are in sort order
            // Site
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Team
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Clar Id
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Time
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Status
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            

             if (!isShowNewClarificationsOnly()) {
                // Judge
                sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));

                // Sent to
                sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            }

            // Problem
             sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));

            // Question
             sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));

            if (!isShowNewClarificationsOnly()) {
                // Answer
                sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            }
        } else {
//          Object[] teamColumns =        { "Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question", "Answer", "ElementID" };
            // These are in column order - omitted ones are straight string compare
            trs.setComparator(0, accountNameSorter);
            trs.setComparator(1, accountNameSorter);
            trs.setComparator(2, numericStringSorter);
            trs.setComparator(3, numericStringSorter);
            
            // These are in sort order
            // Site
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Team
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Clar Id
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Time
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Status
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Problem
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Question
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
            // Answer
            sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
        }
        trs.setSortKeys(sortList);
        resizeColumnWidth(clarificationTable);
    }
    
    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
    }
    
    /**
     * Find row that contains the supplied key (in last column)
     * @param value - unique key - really, the ElementId of run
     * @return index of row, or -1 if not found
     */
    private int getRowByKey(Object value) {
        Object o;
        
        if(clarificationTableModel != null) {
            int col = clarificationTableModel.getColumnCount() - 1;
            for (int i = clarificationTableModel.getRowCount() - 1; i >= 0; --i) {
                o = clarificationTableModel.getValueAt(i, col);
                if (o != null && o.equals(value)) {
                    return i;
                }
            }
        }
        return(-1);
    }


    protected void showSelectedClarification() {

        int[] selectedIndexes = clarificationTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            getAnswerPane().setVisible(false);
            getAnswerTextArea().setText("");
            getQuestionTextArea().setText("");
            String clarificationTitle = "Clarification ";
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, clarificationTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            return;
        }


        ElementId clarificationId = clarificationTable.getElementIdFromTableRow(selectedIndexes[0]);

        Clarification clarification = getContest().getClarification(clarificationId);

        if (clarification != null) {

            String clarificationTitle = "Clarification " + clarification.getNumber() + "  from " + getTeamDisplayName(clarification.getSubmitter()) + " (Site " + clarification.getSiteNumber() + ")";
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, clarificationTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

            getQuestionTextArea().setText(clarification.getQuestion());
            getQuestionTextArea().setCaretPosition(0);

            if (clarification.getAnswer() != null) {
                getAnswerPane().setVisible(true);
                getAnswerTextArea().setText(clarification.getAnswer());
            } else {
                getAnswerTextArea().setText("Not answered, yet.");
                // Don't show answer pane if no answer & new clars view
                getAnswerPane().setVisible(!isShowNewClarificationsOnly());
            }
            getAnswerTextArea().setCaretPosition(0);
        }

        // Show preview pane
        getClarificationSplitPane().setVisible(true);
    }

    /**
     * 
     * @param clarification
     *            the clarification to show
     */
    private void showClarificationAnswer(Clarification clarification) {
        // do not show deleted clars

        if (clarification.isDeleted()) {
            return;
        }

        String problemName = getProblemTitle(clarification.getProblemId());
        String displayString = "<HTML><FONT SIZE=+1>Judge's Response<BR><BR>" + "Problem: <FONT COLOR=BLUE>" + Utilities.forHTML(problemName) + "</FONT><BR><BR>" + "Clar Id: <FONT COLOR=BLUE>"
                + clarification.getNumber() + "</FONT><BR><BR><BR>" + "Question: <FONT COLOR=BLUE> " + Utilities.forHTML(clarification.getQuestion()) + "</FONT><BR><BR><BR>"
                + "Answer: <FONT COLOR=BLUE>" + Utilities.forHTML(clarification.getAnswer()) + "</FONT><BR><BR><BR>";

        if (clarification.isSendToAll()) {
            displayString = displayString + "* For All Teams *" + "\n";
        }

        FrameUtilities.showMessage(getParentFrame(), "Clarification " + clarification.getNumber(), displayString);
    }

    private void removeClarificationRow(final Clarification clarification) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                int rowNumber = getRowByKey(clarification.getElementId());
                if (rowNumber != -1) {
                    clarificationTableModel.removeRow(rowNumber);
                }
            }
        });
    }

    public void updateClarificationRow(final Clarification clarification, final ClientId whoChangedId) {

        if (filter != null) {

            if (!filter.matches(clarification)) {
                // if clar does not match filter, be sure to remove it from grid
                // This applies when a clar is New then BEING_ANSWERED and other conditions.
                removeClarificationRow(clarification);
                return;
            }
        }
        
        if (requiredFilter != null) {
            if (!requiredFilter.matches(clarification)) {
                // if clar does not match requiredFilter, be sure to remove it from grid
                // This applies when a clar is New then BEING_ANSWERED and other conditions.
                removeClarificationRow(clarification);
                return;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildClarificationRow(clarification, whoChangedId);
                int rowNumber = getRowByKey(clarification.getElementId());
                if (rowNumber == -1) {
                    clarificationTableModel.addRow(objects);
                } else {
                    for(int j = clarificationTableModel.getColumnCount()-1; j >= 0; j--) {
                        clarificationTableModel.setValueAt(objects[j], rowNumber, j);
                    }
                    if (clarificationTable.isRowSelected(rowNumber)) {
                        // refresh the textAreas
                        showSelectedClarification();
                    }
                }
                resizeColumnWidth(clarificationTable);            }
        });
    }

    private Object[] buildClarificationRow(Clarification clar, ClientId clientId) {

        try {
            int cols = clarificationTableModel.getColumnCount();
            Object[] obj = new Object[cols];
    
            // Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer" }; (full)
            // or
            // Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question", "Answer" }; (team)
            // or
            // Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Problem", "Question" }; (new)
   
            int idx = 0;
            // The first 4 are easy and the same for everyone
            obj[idx++] = getSiteTitle(clar.getSubmitter().getSiteNumber());
            obj[idx++] = getTeamDisplayName(clar.getSubmitter());
            obj[idx++] = Integer.toString(clar.getNumber());
            obj[idx++] = Long.toString(clar.getElapsedMins());
    
            // Oh my, really?
            boolean isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);
    
            // Just close your eyes or look away.  This code is necessarily(?) complicated.
            if (isTeam) {
                if (clar.isAnswered()) {
                    if (clar.isSendToAll()) {
                        obj[idx++] = "Broadcast";
                    } else {
                        obj[idx++] = "Answered";
                    }
                } else {
                    obj[idx++] = "New";
                }
            } else {
                obj[idx++] = clar.getState();
            }
    
            if (!isShowNewClarificationsOnly()) {
                if (!isTeam) {
                    // Who judged it and who it was sent to is only shown for non-teams in not-new mode
                    if (clar.isAnswered()) {
        
                        if (clar.getWhoJudgedItId() == null || isTeam) {
                            obj[idx++] = "";
                        } else {
                            obj[idx++] = clar.getWhoJudgedItId().getName();
                        }
                    } else {
                        if (clientId == null) {
                            // eg not being judged
                            obj[idx++] = "";
                        } else {
                            obj[idx++] = clientId.getName();
                        }
                    }
                    if (clar.isSendToAll()) {
                        obj[idx++] = "All Teams";
                    } else {
                        obj[idx++] = getTeamDisplayName(clar.getSubmitter());
                    }
                }
            }
            obj[idx++] = getProblemTitle(clar.getProblemId());
            obj[idx++] = clar.getQuestion();
            if (!isShowNewClarificationsOnly()) {
                obj[idx++] = clar.getAnswer();
            }
            obj[idx++] = clar.getElementId();
            return obj;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildClarificationRow()", exception);
        }
        return null;
    }

    void reloadListBox() {
        
        if (isJudge()) {
            ContestInformation contestInformation = getContest().getContestInformation();
            displayTeamName.setTeamDisplayMask(contestInformation.getTeamDisplayMode());
        }
        
        if (filter.isFilterOn()){
            getFilterButton().setForeground(Color.BLUE);
            getFilterButton().setToolTipText("Edit filter - filter ON");
            // TODO code row count label 
//            rowCountLabel.setForeground(Color.BLUE);
        } else {
            getFilterButton().setForeground(Color.BLACK);
            getFilterButton().setToolTipText("Edit filter");
//            rowCountLabel.setForeground(Color.BLACK);
        }


//        clarificationListBox.removeAllRows();
        Clarification[] clarifications = getContest().getClarifications();

        for (Clarification clarification : clarifications) {

            if (requiredFilter != null) {
                if (!requiredFilter.matches(clarification)) {
                    continue;
                }
            }

            if (filter != null) {
                if (!filter.matches(clarification)) {
                    continue;
                }
            }

            updateClarificationRow(clarification, null);
        }
        resizeColumnWidth(clarificationTable);
    }

    /**
     * Contest Information Listener for Clarifications Pane.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
        
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }
        
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
            if (event.getClarification().isAnswered()) {
                if (getContest().getClientId().getClientType() == ClientType.Type.TEAM) {
                    showClarificationAnswer(event.getClarification());
                }
            }
            if (event.getClarification().isAnswered() || event.getClarification().isNew() || event.getClarification().isDeleted()) {
                if (event.getClarification().getElementId().equals(lastAnswered)) {
                    lastAnswered = null;
                }
            }
        }
        
        public void refreshClarfications(ClarificationEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                    resetClarsListBoxColumns();
                    reloadListBox();
                }
            });
        }

        public void clarificationChanged(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
            if (event.getClarification().isAnswered()) {
                if (getContest().getClientId().getClientType() == ClientType.Type.TEAM) {
                    showClarificationAnswer(event.getClarification());
                }
            }
            if (event.getClarification().isAnswered() || event.getClarification().isNew() || event.getClarification().isDeleted()) {
                if (event.getClarification().getElementId().equals(lastAnswered)) {
                    lastAnswered = null;
                }
            }
        }

        public void clarificationRemoved(ClarificationEvent event) {
            if (event.getClarification().getElementId().equals(lastAnswered)) {
                lastAnswered = null;
            }
        }

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(inContest, inController);

        initializePermissions();
 
        getContest().addClarificationListener(new ClarificationListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addCategoryListener(new CategoryListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                getEditFilterFrame().setContestAndController(getContest(), getController());
                
                answerClarificationFrame.setContestAndController(getContest(), getController());
 
                updateGUIperPermissions();
                resetClarsListBoxColumns();
                reloadListBox();
            }
        });
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = getContest().getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getSiteTitle(int siteNumber) {
        // TODO Auto-generated method stub
        return "Site " + siteNumber;
    }

    private String getTeamDisplayName(ClientId clientId) {
        if (isJudge() && isTeam(clientId)) {
            return displayTeamName.getDisplayName(clientId);
        }

        return clientId.getName();
    }

    /**
     * This method initializes getButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGiveButton() {
        if (giveButton == null) {
            giveButton = new JButton();
            giveButton.setText("Give");
            giveButton.setToolTipText("Give the selected Clarification back to Judges");
            giveButton.setMnemonic(java.awt.event.KeyEvent.VK_G);
            giveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code give
                    showMessage("Give not implemented");
                }
            });
        }
        return giveButton;
    }

    /**
     * This method initializes takeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getTakeButton() {
        if (takeButton == null) {
            takeButton = new JButton();
            takeButton.setText("Take");
            takeButton.setToolTipText("Take the selected Clarification back from the Judges");
            takeButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            takeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code take clar
                    showMessage("Take not implemented");
                }
            });
        }
        return takeButton;
    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setToolTipText("Edit the selected Clarification");
            editButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // TODO code edit clarification
                    showMessage("Edit not implemented");
                }
            });
        }
        return editButton;
    }

    /**
     * This method initializes filterButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFilterButton() {
        if (filterButton == null) {
            filterButton = new JButton();
            filterButton.setText("Filter");
            filterButton.setToolTipText("Edit Filter");
            filterButton.setVisible(true);
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showFilterClarificationsFrame();
                }
            });
        }
        return filterButton;
    }
    
    protected void showFilterClarificationsFrame() {
        
        getEditFilterFrame().addList(ListNames.PROBLEMS);
        
        if (! isTeam(getContest().getClientId())) {
            getEditFilterFrame().addList(ListNames.CLARIFICATION_STATES);
            getEditFilterFrame().addList(ListNames.TEAM_ACCOUNTS);
            getEditFilterFrame().addList(ListNames.SITES);
        }
            
        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().doLayout();
        getEditFilterFrame().setVisible(true);
    }
    
    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null){
            Runnable callback = new Runnable(){
                public void run() {
                    reloadListBox();
                }
            };
            editFilterFrame = new EditFilterFrame(filter, filterFrameTitle,  callback);
            editFilterFrame.setFilteringClarifications(true);
            if (displayTeamName != null){
                editFilterFrame.setDisplayTeamName(displayTeamName);
            }
        }
        return editFilterFrame;
    }

    private boolean isTeam(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.TEAM);
    }

    private boolean isJudge(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.JUDGE);
    }

    private boolean isJudge() {
        return isJudge(getContest().getClientId());
    }

    private void updateGUIperPermissions() {

        if (showNewClarificationsOnly) {
            requestButton.setVisible(isAllowed(Permission.Type.ANSWER_CLARIFICATION));
            editButton.setVisible(false);
            giveButton.setVisible(false);
            takeButton.setVisible(false);

        } else {
            requestButton.setVisible(isAllowed(Permission.Type.ANSWER_CLARIFICATION));
            editButton.setVisible(isAllowed(Permission.Type.EDIT_CLARIFICATION));
            giveButton.setVisible(isAllowed(Permission.Type.GIVE_CLARIFICATION));
            takeButton.setVisible(isAllowed(Permission.Type.TAKE_CLARIFICATION));
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore, doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                        reloadListBox();
                    }
                });

            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        reloadListBox();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if it included this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            final boolean finalTheyModifiedUs = theyModifiedUs;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                    reloadListBox();
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                    resetClarsListBoxColumns();
                    reloadListBox();
                }
            });
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    private class CategoryListenerImplementation implements ICategoryListener {

        public void categoryAdded(CategoryEvent event) {
            // ignored, cannot change listbox
        }

        public void categoryChanged(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void categoryRemoved(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void categoryRefreshAll(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
        
    }

    /**
     * This method initializes requestButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRequestButton() {
        if (requestButton == null) {
            requestButton = new JButton();
            requestButton.setText("Answer");
            requestButton.setToolTipText("Answer the selected Clarification");
            requestButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            requestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    requestSelectedClarification();
                }
            });
        }
        return requestButton;
    }

    protected void requestSelectedClarification() {

        int[] selectedIndexes = clarificationTable.getSelectedRows();

        if (selectedIndexes.length < 1) {
            showMessage("Please select a clarification ");
            return;
        }

        try {
            ElementId elementId = clarificationTable.getElementIdFromTableRow(selectedIndexes[0]);
            Clarification clarificationToAnswer = getContest().getClarification(elementId);
            if (elementId.equals(lastAnswered)) {
                // try to make it visible
                answerClarificationFrame.setVisible(true);
                return;
            }
            if (lastAnswered != null) {
                int showConfirmDialog = JOptionPane.showConfirmDialog(getParentFrame(), "You are already answering a clar, do you want to return to that one (YES) or start answering the new one (NO)", "Already answering a Clarifition", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
                if (showConfirmDialog == JOptionPane.CANCEL_OPTION || showConfirmDialog == JOptionPane.YES_OPTION) {
                    // try to make it visible
                    answerClarificationFrame.setVisible(true);
                    return;
                } else {
                    // NO option
                    getController().cancelClarification(getContest().getClarification(lastAnswered));
                    // now continue the rest of this method
                }
            }
            lastAnswered = elementId;
            if ((!clarificationToAnswer.getState().equals(ClarificationStates.NEW)) || clarificationToAnswer.isDeleted()) {
                showMessage("Not allowed to request clarification, already answered");
                return;
            }

            answerClarificationFrame.setClarification(clarificationToAnswer);
            answerClarificationFrame.setVisible(true);
        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to answer clarification, check log");
        }
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(32, 32));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            centerPane = new JPanel();
            centerPane.setPreferredSize(new java.awt.Dimension(200,400));
            centerPane.setLayout(gridLayout);
            centerPane.add(getScrollPane(), null);
            centerPane.add(getClarificationSplitPane(), null);
        }
        return centerPane;
    }

    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getClarificationSplitPane() {
        if (clarificationSplitPane == null) {
            clarificationSplitPane = new JSplitPane();
            clarificationSplitPane.setPreferredSize(new java.awt.Dimension(200, 200));
            clarificationSplitPane.setDividerLocation(70);
            clarificationSplitPane.setTopComponent(getClarificationPane());
            clarificationSplitPane.setBottomComponent(getAnswerPane());
            clarificationSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            clarificationSplitPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent e) {
                    clarificationSplitPane.setDividerLocation(clarificationSplitPane.getHeight()/2);
                }
            });
        }
        return clarificationSplitPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationPane() {
        if (clarificationPane == null) {
            clarificationPane = new JPanel();
            clarificationPane.setLayout(new BorderLayout());
            clarificationPane.setPreferredSize(new Dimension(10, 40));
            clarificationPane.setBorder(BorderFactory.createTitledBorder(null, "Clarification", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            clarificationPane.add(getJQuestionScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return clarificationPane;
    }

    /**
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getQuestionTextArea() {
        if (questionTextArea == null) {
            questionTextArea = new JTextArea();
            questionTextArea.setLayout(new BorderLayout());
//            questionTextArea.setBorder(BorderFactory.createTitledBorder(null, "Question", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            questionTextArea.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            questionTextArea.add(getAnswerTextArea(), java.awt.BorderLayout.CENTER);

            questionTextArea.setEditable(false);
        }
        return questionTextArea;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerPane() {
        if (answerPane == null) {
            answerPane = new JPanel();
            answerPane.setLayout(new BorderLayout());
            answerPane.setBorder(BorderFactory.createTitledBorder(null, "Answer", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            answerPane.add(getJAnswerScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return answerPane;
    }

    /**
     * This method initializes jTextArea1
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getAnswerTextArea() {
        if (answerTextArea == null) {
            answerTextArea = new JTextArea();
            answerTextArea.setEditable(false);
        }
        return answerTextArea;
    }

    public boolean isShowNewClarificationsOnly() {
        return showNewClarificationsOnly;
    }

    public void setShowNewClarificationsOnly(boolean showNewClarificationsOnly) {
        this.showNewClarificationsOnly = showNewClarificationsOnly;

        if (showNewClarificationsOnly) {
            
            if (requiredFilter == null) {
                requiredFilter = new Filter();
            }
            requiredFilter.addClarificationState(ClarificationStates.NEW);
            
        } else {
            requiredFilter = new Filter();
        }
        // do not show the answer area for new clars view
        getAnswerPane().setVisible(!showNewClarificationsOnly);
    }

    /**
     * This method initializes jQuestionScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJQuestionScrollPane() {
        if (jQuestionScrollPane == null) {
            jQuestionScrollPane = new JScrollPane();
            jQuestionScrollPane.setViewportView(getQuestionTextArea());
        }
        return jQuestionScrollPane;
    }

    /**
     * This method initializes jcAnswerScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJAnswerScrollPane() {
        if (jAnswerScrollPane == null) {
            jAnswerScrollPane = new JScrollPane();
            jAnswerScrollPane.setViewportView(getAnswerTextArea());
        }
        return jAnswerScrollPane;
    }

    /**
     * Set title for the Filter Frame.
     * 
     * @param title
     */
    public void setFilterFrameTitle (String title){
        filterFrameTitle = title;
        if (editFilterFrame != null){
            editFilterFrame.setTitle(title);
        }
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
