package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.MCLB;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClarificationsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel clarificationButtonPane = null;

    private MCLB clarificationListBox = null;

    /**
     * This method initializes
     * 
     */
    public ClarificationsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getClarificationListBox(), java.awt.BorderLayout.CENTER);
        this.add(getClarificationButtonPane(), java.awt.BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
       return "Clarifications Pane";
    }

    /**
     * This method initializes clarificationButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationButtonPane() {
        if (clarificationButtonPane == null) {
            clarificationButtonPane = new JPanel();
            clarificationButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
        }
        return clarificationButtonPane;
    }

    /**
     * This method initializes clarificationListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getClarificationListBox() {
        if (clarificationListBox == null) {
            clarificationListBox = new MCLB();

             Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer" };
            clarificationListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());

            // Site
            clarificationListBox.setColumnSorter(0, sorter, 1);

            // Team
            clarificationListBox.setColumnSorter(1, sorter, 2);

            // Clar Id
            clarificationListBox.setColumnSorter(2, numericStringSorter, 3);

            // Time
            clarificationListBox.setColumnSorter(3, numericStringSorter, 4);

            // Status
            clarificationListBox.setColumnSorter(4, sorter, 5);

            // Judge
            clarificationListBox.setColumnSorter(5, sorter, 6);

            // Sent to
            clarificationListBox.setColumnSorter(6, sorter, 7);

            // Problem
            clarificationListBox.setColumnSorter(7, sorter, 8);

            // Question
            clarificationListBox.setColumnSorter(8, sorter, 9);

            // Answer
            clarificationListBox.setColumnSorter(9, sorter, 10);

            clarificationListBox.autoSizeAllColumns();


        }
        return clarificationListBox;
    }

    public void updateClarificationRow(final Clarification clarification, final ClientId whoChangedId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildClarificationRow(clarification, whoChangedId);
                int rowNumber = clarificationListBox.getIndexByKey(clarification.getElementId());
                if (rowNumber == -1) {
                    clarificationListBox.addRow(objects, clarification.getElementId());
                } else {
                    clarificationListBox.replaceRow(objects, rowNumber);
                }
                clarificationListBox.autoSizeAllColumns();
                clarificationListBox.sort();
            }
        });
    }

    private Object[] buildClarificationRow(Clarification clar, ClientId clientId) {

        int cols = clarificationListBox.getColumnCount();
        Object[] obj = new Object[cols];

        // Object[] cols = {"Site", "Team", "Clar Id", "Time", "Status", "Judge", "Sent to", "Problem", "Question", "Answer" };

        obj[0] = getSiteTitle(clar.getSubmitter().getSiteNumber());
        obj[1] = getTeamDisplayName(clar.getSubmitter());
        obj[2] = clar.getNumber();
        obj[3] = clar.getElapsedMins();
        if (clar.isAnswered()) {
            obj[4] = "Answered";
        } else {
            obj[4] = "Not Answered";
        }

        if (clientId != null) {
            obj[5] = getTeamDisplayName(clientId);
        } else {
            obj[5] = "";
        }

        if (clar.isSendToAll()) {
            obj[6] = "All Teams";
        } else {
            obj[6] = getTeamDisplayName(clar.getSubmitter());
        }
        obj[7] = getProblemTitle(clar.getProblemId());
        obj[8] = clar.getQuestion();
        obj[9] = clar.getAnswer();

        return obj;
    }

    private void reloadListBox() {
        clarificationListBox.removeAllRows();
        Clarification[] clarifications = getModel().getClarifications();

        for (Clarification clarification : clarifications) {
            addClarificationRow(clarification);
        }
    }

    private void addClarificationRow(Clarification clarification) {
        Object[] objects = buildClarificationRow(clarification, null);
        clarificationListBox.addRow(objects, clarification.getElementId());
        clarificationListBox.autoSizeAllColumns();
        clarificationListBox.sort();
    }
    
    /**
     * 
     *
     * @author pc2@ecs.csus.edu
     */
    
    // $HeadURL$
    public class ClarificationListenerImplementation implements IClarificationListener{

        public void clarificationAdded(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
        }

        public void clarificationChanged(ClarificationEvent event) {
            updateClarificationRow(event.getClarification(), event.getWhoModifiedClarification());
        }

        public void clarificationRemoved(ClarificationEvent event) {
            // TODO Auto-generated method stub
        }
        
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        getModel().addClarificationListener(new ClarificationListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }
    
    private String getProblemTitle(ElementId problemId) {
        Problem problem = getModel().getProblem(problemId);
        if (problem != null){
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getSiteTitle(int siteNumber) {
        // TODO Auto-generated method stub
        return "Site "+siteNumber;
    }

    private String getTeamDisplayName(ClientId clientId) {
        Account account = getModel().getAccount(clientId);
        if (account != null ){
            return account.getDisplayName();
        }
        
        return clientId.getName(); 
    }


} // @jve:decl-index=0:visual-constraint="10,10"
