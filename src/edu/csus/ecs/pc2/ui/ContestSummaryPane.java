package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Pluralize;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.report.AccountsReport;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.LanguagesReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.SitesReport;

/**
 * Contest Information Pane.
 * 
 * Can be used to confirm contest settings or view summary of {@link IInternalContest}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSummaryPane extends PluginPane {

    // TODO Bug 635 - add this as part of the import contest.yaml process.
    
    /**
     * 
     */
    private static final long serialVersionUID = -5998522945378354709L;

    private MCLB contestListBox = null;

    /**
     * This method initializes
     * 
     */
    public ContestSummaryPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(459, 217));
        this.add(getContestListBox(), BorderLayout.CENTER);

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });
    }

    protected void populateUI() {

        Language[] languages = getContest().getLanguages();
        Problem[] problems = getContest().getProblems();
        Site[] sites = getContest().getSites();
        // Category[] categories = getContest().getCategories();
        ClientSettings[] settings = getContest().getClientSettingsList();

        // PlaybackInfo info = getPlaybackInfo(getContest());

        addContestRow(sites.length, "site", new SitesReport());

        addContestRow(problems.length, "problem", new ProblemsReport());

        if (problems.length > 0) {
            int ansCount = 0;
            int datCount = 0;

            for (Problem problem : problems) {
                ProblemDataFiles pdfiles = getContest().getProblemDataFile(problem);
                if (pdfiles != null) {
                    ansCount += pdfiles.getJudgesAnswerFiles().length;
                    datCount += pdfiles.getJudgesDataFiles().length;
                    // } else { // nothing to do here, move on
                }
            }
            addContestRow(datCount, "input data files", null);
            addContestRow(ansCount, "answer data files", null);
        }

        addContestRow(languages.length, "language", new LanguagesReport());

        for (Type type : Type.values()) {
            Vector<Account> accounts = getContest().getAccounts(type);
            String accountTypeName = type.toString().toLowerCase();
            
            if (accounts.size() > 0) {

                Filter filter = new Filter();
                Account[] list = (Account[]) accounts.toArray(new Account[accounts.size()]);
                filter.addAccounts(list);

                String s = pluralizeEntry(accounts.size(), accountTypeName, " account");
                addContestRow(accounts.size(), s, new AccountsReport());

            }
        }

        addContestRow(settings.length, "AJ setting", null);
    }

    String pluralizeEntry(int count, String prefix, String entryName) {
        StringBuffer buf = new StringBuffer();
        if (count > 0) {
            // buf.append(count);
            String pluralized = Pluralize.pluralize(entryName, count);
            if (prefix.length() > 0) {
                // buf.append(' ');
                buf.append(prefix);
            }
            buf.append(' ');
            buf.append(pluralized);
        } else {
            return entryName;
        }
        return buf.toString();
    }

    private void addContestRow(int count, String description, IReport report) {

        Object[] cols = new Object[getContestListBox().getColumnCount()];

        cols[0] = Integer.toString(count);
        cols[1] = description;

        if (report == null) {
            cols[2] = "No Report";
        } else {
            cols[2] = getReportButton(report, description);
        }
        
        getContestListBox().addRow(cols);
        getContestListBox().autoSizeAllColumns();
    }

    private JButton getReportButton(final IReport report, final String description) {
        JButton button = new JButton("Report");
        button.setToolTipText(description);
        button.setPreferredSize(new Dimension(35,35));
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Utilities.viewReport(report, description, getContest(), getController());
            }
        });
        return button;
    }

    /**
     * This method initializes contestListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getContestListBox() {
        if (contestListBox == null) {
            contestListBox = new MCLB();

            Object[] cols = { "Count", "Description", "Report" };

            contestListBox.addColumns(cols);
        }
        return contestListBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
