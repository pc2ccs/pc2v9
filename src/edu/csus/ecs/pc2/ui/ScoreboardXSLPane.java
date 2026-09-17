// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * View and select Scoreboard XSL pane.
 *
 * @author John Buck
 * @version $Id$
 */

// $HeadURL$

public class ScoreboardXSLPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    public static final String extensionFilter = "xsl";

    private JPanel xslButtonPane = null;

    private MCLB xslListBox = null;

    private JButton applyButton = null;

    private JButton revertButton = null;

    private JButton selectAllButton = null;

    private JButton deselectAllButton = null;

    private JButton refreshButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private Log log;

    private JScrollPane xslListPane = null;
    private FileTableModel tableModel = null;
    private JTable table = null;
    private File currentFolder = null;

    private String [] modelXslFiles = null;

    /**
     * This method initializes
     *
     */
    public ScoreboardXSLPane() {
        super();
        currentFolder = new File(XMLUtilities.getStyleSheetDirectoryName());
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getXslListPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getXslButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    @Override
    public String getPluginTitle() {
        return "Scoreboard XSL Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        tableModel.setHasPermission(getContest().isAllowed(Permission.Type.ADD_SETTINGS));
        getCurrentXslFilesFromModel();
        loadFiles(currentFolder);
        updateButtons();
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
    }

    /**
     * Fetch list of current XSL filenames being used to generate scoreboards from the contest.
     * To support legacy contests, if the list is null, it means all files in the XSL
     * folder are selected, so we get that list from the table model in that case.
     * Otherwise, we just clone the list we got.  It is possible the list is empty - I'm not sure
     * how useful that is since no scoreboards will be generated in that case.
     *
     * Sets modelXslFiles to the list of current XSL file names.
     */
    private void getCurrentXslFilesFromModel() {
        ContestInformation ci = getContest().getContestInformation();
        String [] curXslFiles = ci.getScoreboardXSLFiles();

        if(curXslFiles == null) {
            // This means all files (legacy support)
            modelXslFiles = tableModel.getAllFileNames();

        } else {
            modelXslFiles = curXslFiles.clone();
        }
    }

    /**
     * This method initializes xslButtonPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getXslButtonPane() {
        if (xslButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            xslButtonPane = new JPanel();
            xslButtonPane.setLayout(flowLayout);
            xslButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            xslButtonPane.add(getApplyButton(), null);
            xslButtonPane.add(getRevertButton(), null);
            xslButtonPane.add(getSelectAllButton(), null);
            xslButtonPane.add(getDeselectAllButton(), null);
            xslButtonPane.add(getRefreshButton(), null);
        }
        return xslButtonPane;
    }

    /**
     * This method initializes xslListPane (a JScrollPane) containing a table of xsl files in
     * the data/xsl folder along with checkboxes indicating if they are active.
     * Another column in the table also provides a description of the xsl file.
     * This is found by examining the first few lines of the xsl for a comment of the
     * form: <!-- Description: This is the description text that will appear -->
     *
     * @return JScrollPane containing the table
     */
    private JScrollPane getXslListPane() {
        if(xslListPane == null) {
            tableModel = new FileTableModel();
            table = new JTable(tableModel);

            // --- Table setup ---
            table.setRowHeight(24);
            table.setShowGrid(true);
            table.setGridColor(new Color(230, 230, 230));
            table.getTableHeader().setReorderingAllowed(false);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableModel.addTableModelListener(e -> {
                if(e.getColumn() == 0 && e.getType() == TableModelEvent.UPDATE) {
                    // Checkbox changed, so see what controls have to be enabled/disabled
                    updateButtons();
                }
            });

            TableColumn fileColumn = table.getColumnModel().getColumn(0);
            fileColumn.setCellRenderer(new CheckBoxCellRenderer());
            fileColumn.setCellEditor(new CheckBoxCellEditor());
            fileColumn.setPreferredWidth(64);

            TableColumn descColumn = table.getColumnModel().getColumn(1);
            descColumn.setPreferredWidth(500);

            // Create the scroll pane now
            xslListPane = new JScrollPane(table);
            loadFiles(currentFolder);
            updateButtons();
        }
        return xslListPane;
    }

    /**
     * Called when a checkbox is clicked.  Update which buttons are active based
     * on what has changed.  This is WAY overly user-friendly for what we're doing here.
     */
    private void updateButtons() {

        // Really, this shouldn't be null
        if(modelXslFiles == null) {
            return;
        }

        boolean hasPermission = isAllowed(Permission.Type.ADD_SETTINGS);

        // Get current selections into modelXslFiles
        String [] selectedXslFilenames = getSelectedFileNames();
        boolean bChange = !StringUtilities.stringArraySameUnordered(selectedXslFilenames, modelXslFiles);
        // Apply and Revert buttons are only enabled if the user selected different filenames than the model has.
        getRevertButton().setEnabled(bChange && hasPermission);
        getApplyButton().setEnabled(bChange && hasPermission);

        boolean selectAll = true && hasPermission;
        boolean deselectAll = true && hasPermission;
        // If everything is selected, then enable deselect all button, and disable select all
        if(selectedXslFilenames.length == tableModel.getRowCount()) {
            selectAll = false;
        } else if(selectedXslFilenames.length == 0) {
            deselectAll = false;
        }
        getSelectAllButton().setEnabled(selectAll);
        getDeselectAllButton().setEnabled(deselectAll);
    }

    /**
     * This method initializes the Apply button
     *
     * @return javax.swing.JButton
     */
    private JButton getApplyButton() {
        if (applyButton == null) {
            applyButton = new JButton("Apply");
            applyButton.addActionListener(e -> { stopEditing(); applyXslChoices(); });
            applyButton.setToolTipText("Apply the selected XSL files for scoreboard HTML generation.");
        }
        return applyButton;
    }

    // Update model's list of desired xsl files to generate HTML scoreboards for.
    protected void applyXslChoices() {

        // Really, this shouldn't be null at this point
        if(modelXslFiles == null) {
            return;
        }

        // Get current selection choices from table model
        String [] selectedXslFilenames = getSelectedFileNames();
        // If the selections are different than the model has, we have to apply the new settings
        if(!StringUtilities.stringArraySameUnordered(selectedXslFilenames, modelXslFiles)){
            ContestInformation ci = getContest().getContestInformation();
            ci.setScoreboardXSLFiles(selectedXslFilenames);
            // Since setScoreboardXSLFiles just assumes ownership of the array we passed, we want our own copy
            // The String [] is the important thing to clone, not the individual String objects since they're
            // immutable anyway.
            modelXslFiles = selectedXslFilenames.clone();
            updateButtons();
            // save ContestInformation to model
            getController().updateContestInformation(ci);
        }
    }

    /**
     * This method initializes revertButton
     *
     * @return javax.swing.JButton
     */
    private JButton getRevertButton() {
        if (revertButton == null) {
            revertButton = new JButton("Revert");
            revertButton.addActionListener(e -> { stopEditing(); revertXslChoices(); });
            revertButton.setToolTipText("Revert to original settings before the recent changes you made.");
        }
        return revertButton;
    }

    /**
     * Repopulate table model with original xsl file names from the contest.
     */
    protected void revertXslChoices() {
        stopEditing();
        getCurrentXslFilesFromModel();
        loadFiles(currentFolder);
        updateButtons();
    }

    /**
     * This method initializes the select all button
     *
     * @return javax.swing.JButton
     */
    private JButton getSelectAllButton() {
        if (selectAllButton == null) {
            selectAllButton = new JButton("Select All");
            selectAllButton.addActionListener(e -> { stopEditing(); tableModel.setAllSelected(true); updateButtons();});
            selectAllButton.setToolTipText("Select all XSL files.");
        }
        return selectAllButton;
    }

    /**
     * This method initializes the deselect all button
     *
     * @return javax.swing.JButton
     */
    private JButton getDeselectAllButton() {
        if (deselectAllButton == null) {
            deselectAllButton = new JButton("Deselect All");
            deselectAllButton.addActionListener(e -> { stopEditing(); tableModel.setAllSelected(false); updateButtons();});
            deselectAllButton.setToolTipText("Deselect all XSL files.");
        }
        return deselectAllButton;
    }

    /**
     * This method initializes the refresh button
     *
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> {
                stopEditing();
                // To refresh, we have to save the current selections, save the
                // xsl file list we got for the contest model, temporarily set the modelXslFiles to the selected files and
                // reload the folder's files, then set the model's files back.  The table model sets
                // selections based on the filenames in modelXslFiles, which we why we have to jump through
                // all these hoops.

                // Save model's original file list
                String [] savedModelXslFiles = modelXslFiles;
                // Temporarily set modelXslFiles to currently selected filenames
                modelXslFiles = getSelectedFileNames();
                loadFiles(currentFolder);
                // Restore model's xsl filename list
                modelXslFiles = savedModelXslFiles;
                updateButtons();
            });
            refreshButton.setToolTipText("Refresh file list in case something was added or deleted.");
        }
        return refreshButton;
    }

    /**
     * Create a list of the currently selected xsl files names from the table model.
     *
     * @return String array of selected file names
     */
    private String [] getSelectedFileNames() {
        List<File> selected = tableModel.getSelectedFiles();
        List<String> selectedFiles = new ArrayList<>();

        for (File f : selected) {
            selectedFiles.add(f.getName());
        }
        return(selectedFiles.stream().toArray(String[]::new));
    }

    /**
     * Show which files are selected.  This is for debugging and is not currently used.
     */
    private void printSelected() {
        List<File> selected = tableModel.getSelectedFiles();
        StringBuilder sb = new StringBuilder("Selected files:\n");
        for (File f : selected) {
            sb.append(f.getName()).append("\n");
        }
        if (selected.isEmpty()) {
            sb.append("(none)");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private static class CheckBoxListRenderer implements ListCellRenderer<JCheckBox> {
        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list,
                                                        JCheckBox value,
                                                        int index,
                                                        boolean isSelected,
                                                        boolean cellHasFocus) {
            value.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            value.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return value;
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
            messagePane.setPreferredSize(new java.awt.Dimension(25,25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * show message to user
     *
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    /**
     * Read the files in the "folder" passed in and populate the table model with the list.
     *
     * @param folder
     */
    private void loadFiles(File folder) {

        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            showMessage("Invalid folder: " + folder);
            return;
        }

        // Capture the current filter value in a local so the lambda below
        // closes over an effectively-final variable.
        final String currentExtensionFilter = extensionFilter;

        FilenameFilter filter = (dir, name) -> {
            if (currentExtensionFilter == null) {
                return true; // no filter -> show everything
            }
            return name.toLowerCase(Locale.ROOT).endsWith("." + currentExtensionFilter);
        };

        File[] files = folder.listFiles(filter);
        if (files == null) {
            files = new File[0];
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        tableModel.setFiles(Arrays.asList(files), modelXslFiles);
    }

    /**
     *  One row of table data: the underlying file plus its current checked state.
     */
    private static class FileRow {
        final File file;
        boolean selected;

        FileRow(File file) {
            this.file = file;
        }
    }

    /**
     * Table model with two columns:
     *   Column 0 ("File")        -> a FileRow, rendered/edited as a checkbox + filename
     *   Column 1 ("Description") -> a plain description string (read from the xsl file's Description comment)
     */
    private static class FileTableModel extends javax.swing.table.AbstractTableModel {
        private final List<FileRow> rows = new ArrayList<>();
        private static final String[] COLUMNS = {"File", "Description"};
        // Pattern to match a line looking like: <!-- Description: Text of the description -->
        private Pattern descPattern = Pattern.compile("^<!--.+(?i:Description):\\s+(.+)\\s+-->$");
        private static final int LINES_TO_CHECK_FOR_DESCRIPTION = 5;
        private boolean hasPermission = false;

        public boolean isHasPermission() {
            return hasPermission;
        }

        public void setHasPermission(boolean hasPermission) {
            this.hasPermission = hasPermission;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FileRow row = rows.get(rowIndex);
            if (columnIndex == 0) {
                return row; // renderer/editor pulls file name + selected state out of this
            }
            return describe(row.file);
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 0 && value instanceof Boolean) {
                rows.get(rowIndex).selected = (Boolean) value;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 && isHasPermission();
        }

        /**
         * Populate the rows and cause the visual component to update
         *
         * @param files
         * @param currentXslFiles
         */
        void setFiles(List<File> files, String [] currentXslFiles) {
            rows.clear();
            String fname;
            boolean bAll;
            HashSet<String> strXslFiles = null;

            if(currentXslFiles == null) {
                bAll = true;
            } else {
                bAll = false;
                strXslFiles = new HashSet<>();
                Collections.addAll(strXslFiles, currentXslFiles);
            }
            for (File f : files) {
                FileRow row = new FileRow(f);
                // Select the row if in the list
                row.selected = (bAll || strXslFiles.contains(row.file.getName()));
                rows.add(row);
            }
            fireTableDataChanged();
        }

        void setAllSelected(boolean selected) {
            for (FileRow row : rows) {
                row.selected = selected;
            }
            fireTableDataChanged();
        }

        List<File> getSelectedFiles() {
            List<File> result = new ArrayList<>();
            for (FileRow row : rows) {
                if (row.selected) {
                    result.add(row.file);
                }
            }
            return result;
        }

        /**
         * Fetch all file names in the xsl folder
         *
         * @return String array of filenames.
         */
        String [] getAllFileNames() {
            String [] result = new String[rows.size()];
            int fileIndex = 0;
            for (FileRow row : rows) {
                result[fileIndex++] = row.file.getName();
            }
            return result;
        }

        private String describe(File file) {
            if (file.isDirectory()) {
                return "Folder";
            }

            // Use try-with-resources to ensure the underlying file stream is closed
            try (Stream<String> lines = Files.lines(file.toPath())) {
                Optional<String> capturedValue = lines
                        .limit(LINES_TO_CHECK_FOR_DESCRIPTION)
                        .map(line -> {
                            Matcher matcher = descPattern.matcher(line);
                            return matcher.find() ? matcher.group(1) : null;
                        })
                        .filter(capturedGroup -> capturedGroup != null) // Ignore lines without a match
                        .findFirst();                                   // Short-circuits & stops reading the file

                if (capturedValue.isPresent()) {
                    return(capturedValue.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "No description";
        }
    }

    /** Displays column 0 as a non-interactive checkbox labeled with the file name. */
    private static class CheckBoxCellRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            FileRow fileRow = (FileRow) value;
            JCheckBox checkBox = new JCheckBox(fileRow.file.getName(), fileRow.selected);
            checkBox.setOpaque(true);
            checkBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            checkBox.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return checkBox;
        }
    }

    /** Lets the user actually click the checkbox in column 0 to toggle selection. */
    private static class CheckBoxCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JCheckBox checkBox = new JCheckBox();

        CheckBoxCellEditor() {
            checkBox.setOpaque(true);
            checkBox.addActionListener(e -> stopCellEditing());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                       int row, int column) {
            FileRow fileRow = (FileRow) value;
            checkBox.setText(fileRow.file.getName());
            checkBox.setSelected(fileRow.selected);
            checkBox.setBackground(table.getSelectionBackground());
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true; // start editing on a single click, not the default double-click
        }
    }

    private void stopEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    // Updates the selections if someone else changes the scoreboard xsl file list
    class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            checkXslFilesChanged(event.getContestInformation());
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            checkXslFilesChanged(event.getContestInformation());
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            checkXslFilesChanged(contestInformationEvent.getContestInformation());
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }

        /**
         * Check if we need to update the list of selected XSL files due to a settings change
         */
        void checkXslFilesChanged(ContestInformation ci) {
            String [] curXslFiles = modelXslFiles;
            getCurrentXslFilesFromModel();
            if(!StringUtilities.stringArraySame(curXslFiles, modelXslFiles)) {
                stopEditing();
                loadFiles(currentFolder);
                updateButtons();
            }
        }
    }


    /**
     * Account Listener Implementation.
     * For permission changes.
     *
     */
    public class AccountListenerImplementation implements IAccountListener {

        @Override
        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        @Override
        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                if(tableModel != null) {
                    tableModel.setHasPermission(getContest().isAllowed(Permission.Type.ADD_SETTINGS));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateButtons();
                        }
                    });
                }

            }
        }

        @Override
        public void accountsAdded(AccountEvent accountEvent) {
            // ignore
        }

        @Override
        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    if(tableModel != null) {
                        tableModel.setHasPermission(getContest().isAllowed(Permission.Type.ADD_SETTINGS));
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                updateButtons();
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void accountsRefreshAll(AccountEvent accountEvent) {
            // They modified us!!
            if(tableModel != null) {
                tableModel.setHasPermission(getContest().isAllowed(Permission.Type.ADD_SETTINGS));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateButtons();
                    }
                });
            }
        }
    }
}
