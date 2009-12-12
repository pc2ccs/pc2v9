package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;

/**
 * View Languages pane.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class LanguagesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5837850150714301616L;

    private JPanel languageButtonPane = null;

    private MCLB languageListBox = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;
    
    private EditLanguageFrame editLanguageFrame = null;
    
    private Log log;

    /**
     * This method initializes
     * 
     */
    public LanguagesPane() {
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
        this.add(getLanguageListBox(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getLanguageButtonPane(), java.awt.BorderLayout.SOUTH);
        
        editLanguageFrame = new EditLanguageFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Languages Pane";
    }

    /**
     * This method initializes languageButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLanguageButtonPane() {
        if (languageButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            languageButtonPane = new JPanel();
            languageButtonPane.setLayout(flowLayout);
            languageButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            languageButtonPane.add(getAddButton(), null);
            languageButtonPane.add(getEditButton(), null);
        }
        return languageButtonPane;
    }

    /**
     * This method initializes languageListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getLanguageListBox() {
        if (languageListBox == null) {
            languageListBox = new MCLB();

            Object[] cols = { "Display Name", "Compiler Command Line", "Exe Name", "Execute Command Line" };
            languageListBox.addColumns(cols);
            
            /**
             * No sorting at this time, the only way to know
             * what order the languages are is to NOT sort them.
             * Later we can add a sorter per LanguageDisplayList somehow.
             */

//            // Sorters
//            HeapSorter sorter = new HeapSorter();
//            // HeapSorter numericStringSorter = new HeapSorter();
//            // numericStringSorter.setComparator(new NumericStringComparator());
//
//            // Display Name
//            languageListBox.setColumnSorter(0, sorter, 1);
//            // Compiler Command Line
//            languageListBox.setColumnSorter(1, sorter, 2);
//            // Exe Name
//            languageListBox.setColumnSorter(2, sorter, 3);
//            // Execute Command Line
//            languageListBox.setColumnSorter(3, sorter, 4);

            languageListBox.autoSizeAllColumns();

        }
        return languageListBox;
    }

    public void updateLanguageRow(final Language language) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildLanguageRow(language);
                int rowNumber = languageListBox.getIndexByKey(language.getElementId());
                if (rowNumber == -1) {
                    languageListBox.addRow(objects, language.getElementId());
                } else {
                    languageListBox.replaceRow(objects, rowNumber);
                }
                languageListBox.autoSizeAllColumns();
//                languageListBox.sort();
            }
        });
    }

    protected Object[] buildLanguageRow(Language language) {

        // Object[] cols = { "Display Name", "Compiler Command Line", "Exe Name", "Execute Command Line" };

        int numberColumns = languageListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = language.toString();
        if (! language.isActive()){
            c[0] = "[DELETED] "+language.toString();
        }
        c[1] = language.getCompileCommandLine();
        c[2] = language.getExecutableIdentifierMask();
        c[3] = language.getProgramExecuteCommandLine();
        return c;
    }

    private void reloadListBox() {
        languageListBox.removeAllRows();
        Language[] languages = getContest().getLanguages();

        for (Language language : languages) {
            addLanguageRow(language);
        }
    }

    private void addLanguageRow(Language language) {
        Object[] objects = buildLanguageRow(language);
        languageListBox.addRow(objects, language.getElementId());
        languageListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        editLanguageFrame.setContestAndController(inContest, inController);
        
        getContest().addLanguageListener(new LanguageListenerImplementation());
        
        log = getController().getLog();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */
    public class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            updateLanguageRow(event.getLanguage());
        }

        public void languageChanged(LanguageEvent event) {
            updateLanguageRow(event.getLanguage());
        }

        public void languageRemoved(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setToolTipText("Add a new Language definition");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewLanguage();
                }
            });
        }
        return addButton;
    }

    protected void addNewLanguage() {
        editLanguageFrame.setLanguage(null);
        editLanguageFrame.setVisible(true);
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
            editButton.setToolTipText("Edit existing Language definition");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedLanguage();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedLanguage() {
        
        int selectedIndex = languageListBox.getSelectedIndex();
        if(selectedIndex == -1){
            showMessage("Select a language to edit");
            return;
        }
        
        try {
            ElementId elementId = (ElementId) languageListBox.getKeys()[selectedIndex];
            Language languageToEdit = getContest().getLanguage(elementId);

            editLanguageFrame.setLanguage(languageToEdit);
            editLanguageFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit language, check log");
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
            public void run() {
                messageLabel.setText(string);
            }
        });

    }
} // @jve:decl-index=0:visual-constraint="10,10"
