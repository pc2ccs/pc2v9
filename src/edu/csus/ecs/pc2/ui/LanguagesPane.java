package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;

/**
 * View Languages pane.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class LanguagesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -172535000039944166L;

    private JPanel languageButtonPane = null;

    private MCLB languageListBox = null;

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
        this.add(getLanguageButtonPane(), java.awt.BorderLayout.SOUTH);

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
            languageButtonPane = new JPanel();
            languageButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
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

            // Sorters
            HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());

            // Display Name
            languageListBox.setColumnSorter(0, sorter, 1);
            // Compiler Command Line
            languageListBox.setColumnSorter(1, sorter, 2);
            // Exe Name
            languageListBox.setColumnSorter(2, sorter, 3);
            // Execute Command Line
            languageListBox.setColumnSorter(3, sorter, 4);

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
                languageListBox.sort();
            }
        });
    }

    protected Object[] buildLanguageRow(Language language) {

        // Object[] cols = { "Display Name", "Compiler Command Line", "Exe Name", "Execute Command Line" };

        int numberColumns = languageListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = language.toString();
        c[1] = language.getCompileCommandLine();
        c[2] = language.getExecutableIdentifierMask();
        c[3] = language.getProgramExecuteCommandLine();
        return c;
    }

    private void reloadListBox() {
        languageListBox.removeAllRows();
        Language[] languages = getModel().getLanguages();

        for (Language language : languages) {
            addLanguageRow(language);
        }
    }

    private void addLanguageRow(Language language) {
        Object[] objects = buildLanguageRow(language);
        languageListBox.addRow(objects, language.getElementId());
        languageListBox.autoSizeAllColumns();
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);

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
            // TODO Auto-generated method stub
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
