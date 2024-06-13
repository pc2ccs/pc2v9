// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.StringToNumberComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.list.ListUtilities;
import edu.csus.ecs.pc2.list.SubmissionSample;
import edu.csus.ecs.pc2.list.SubmissionSampleLocation;
import edu.csus.ecs.pc2.list.SubmissionSolutionList;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;
import edu.csus.ecs.pc2.ui.team.QuickSubmitter;

/**
 * A UI that to submit files found in a CDP.
 *
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SubmitSampleRunsPane extends JPanePlugin {

    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;
    private static final int SUBMISSION_WAIT_TIMEOUT_MS = 10000;

	/**
	 * ClientSettings key for CDP Path
	 */
	private static final String CUSTOM_SUBMIT_SAMPLE_CDP_PATH = "CustomSubmitSampleCDPPath";

	private static final long serialVersionUID = -8862440024499524533L;

	private JTextField cdpTextField;

	private JLabel messageLabel;

	private QuickSubmitter submitter = new QuickSubmitter();

	/**
	 * List of selected solutions names and dirs.
	 */
	private SubmissionSolutionList submissionSolutionList = null;

	private JPanel centerPane = null;
    private JButton filterButton = null;

    private JScrollPane scrollPane = null;
    private JTableCustomized runTable = null;
    private DefaultTableModel runTableModel = null;

    private JPanel messagePanel = null;
    private JLabel rowCountLabel = null;

    /**
     * User filter
     */
    private Filter filter = new Filter();

    private EditFilterFrame editFilterFrame = null;

    private String filterFrameTitle = "Judges' Submissions filter";


    private Log log;

    private List<File> submissionFileList = null;
    private int currentSubmission = -1;
    private List<SubmissionSample> submissionList = null;

    private Timer submissionWaitTimer = null;
    private TimerTask submissionTimerTask = null;

    // Lightish green for match
    private Color matchColorSuccess = new Color(128, 255, 128);
    // Lightish red for hard non-match
    private Color matchColorFailure = new Color(255, 128, 128);
    // Lightish yellow for take a look/maybe's/indeterminates
    private Color matchColorMaybe = new Color(255, 255, 128);
    // Lightish cyan for pending
    private Color matchColorPending = new Color(128, 255, 255);


    // TODO 232 remove debug22Flag
    private boolean debug22Flag = false;

    // TODO On Admin update of Languages or Update of Problems - clear selected index arrays

	public SubmitSampleRunsPane() {
		super();
		setLayout(new BorderLayout());

        add(getMessagePanel(), BorderLayout.NORTH);
        add(getCenterPanel(), BorderLayout.CENTER);
        add(getBottomPanel(), BorderLayout.SOUTH);

	}

    /**
     * This method initializes messagePanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            rowCountLabel = new JLabel();
            rowCountLabel.setText("### of ###");
            rowCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            rowCountLabel.setPreferredSize(new java.awt.Dimension(100,16));

            messageLabel = new JLabel("message label");
            messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
            messageLabel.setForeground(Color.RED);
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            messageLabel.setPreferredSize(new Dimension(400, 32));

            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(30, 30));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
            messagePanel.add(rowCountLabel, java.awt.BorderLayout.EAST);
        }
        return messagePanel;
    }

    private JPanel getCenterPanel() {
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BorderLayout());

        JPanel controlsPane = new JPanel();

        controlsPane.setLayout(new FlowLayout());

        JLabel cdpConfigDirLabel = new JLabel("CDP config folder: ");
        cdpConfigDirLabel.setToolTipText("CDP Location for judges' samples source files");
        cdpConfigDirLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        controlsPane.add(cdpConfigDirLabel);

        cdpTextField = new JTextField();
        cdpTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        Dimension cdpDim = cdpTextField.getPreferredSize();
        cdpDim.setSize(cdpDim.getWidth(), 27);
        cdpTextField.setPreferredSize(cdpDim);
        controlsPane.add(cdpTextField);
        cdpTextField.setColumns(40);

        cdpTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (new File(cdpTextField.getText()).isDirectory()) {
                        updateCDPDirAndFields(cdpTextField.getText());
                    }
                }

            }
        });


        JButton selectCDPButton = new JButton("...");
        selectCDPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNewCDP();
            }
        });
        selectCDPButton.setToolTipText("Select a different CDP");
        selectCDPButton.setPreferredSize(new Dimension(56, 27));
//      selectCDPButton.setBounds(541, 56, 39, 23);
        controlsPane.add(selectCDPButton);

        JButton fb = getFilterButton();
        controlsPane.add(fb);


        centerPane.add(controlsPane, BorderLayout.NORTH);

        centerPane.add(getScrollPane(), BorderLayout.SOUTH);;

        return(centerPane);
    }

    /**
     * Builds the bottom panel (buttons)
     *
     * @return JPanel for the bottom area (buttons)
     */
    private JPanel getBottomPanel() {
        JPanel bottomPane = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
        flowLayout.setHgap(125);

        JButton submitRunButton = new JButton("Submit");
        submitRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitSelectedJudgesSolutions();
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields(true);
            }
        });

        resetButton.setToolTipText("Reset back to default settings");
        bottomPane.add(resetButton);
        submitRunButton.setToolTipText("Submit selected sample runs");
        bottomPane.add(submitRunButton);
        return(bottomPane);
    }

    protected void updateCDPDirAndFields(String cdpConfigDir) {
	    try {
            File configFile = new File(cdpConfigDir);
            String cdpRootDirectory = configFile.getAbsolutePath();
//            if (configFile.getAbsolutePath().endsWith(IContestLoader.CONFIG_DIRNAME)) {
//                cdpRootDirectory = configFile.getParent();
//            }

	        cdpTextField.setText(cdpRootDirectory);
	        updateClientCDPPath(cdpRootDirectory);
        } catch (Exception e) {
            log.log(Level.WARNING, "Problem updating CDP Dir", e);
        }
    }

	/**
	 * Repopulate submissionSolutionList.
	 *
	 * @return
	 */
	public SubmissionSolutionList getSubmissionSolutionList() {
		String cdpPath = cdpTextField.getText();

		submissionSolutionList = new SubmissionSolutionList(getContest(), cdpPath);

		return submissionSolutionList;
	}


	private SubmissionSampleLocation[] toArray(SubmissionSolutionList list) {
		return list.toArray(new SubmissionSampleLocation[list.size()]);
	}

	protected void selectNewCDP() {

		File file = selectEntry("Select CDP");

		File cdpDir = FileUtilities.findCDPConfigDirectory(file);

		if (cdpDir != null) {
		    updateCDPDirAndFields(cdpDir.getAbsolutePath());
		} else {

			int result = FrameUtilities.yesNoCancelDialog(this,
					file.getAbsoluteFile() + " may not be a CDP directory, continue anyways?", "Select CDP");
			if (result == JOptionPane.YES_OPTION) {
	            updateCDPDirAndFields(file.getAbsolutePath());
			}
		}
	}

	/**
	 * Select yaml file/entry.
	 *
	 * @param dialogTitle
	 * @return
	 */
	protected File selectEntry(String dialogTitle) {

		JFileChooser chooser = new JFileChooser(cdpTextField.getText());
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filterYAML = new FileNameExtensionFilter("YAML document (*.yaml)", "yaml");
		chooser.addChoosableFileFilter(filterYAML);

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filterYAML);

		int action = chooser.showOpenDialog(this);
		chooser.setDialogTitle(dialogTitle);

		switch (action) {
		case JFileChooser.APPROVE_OPTION:
			File file = chooser.getSelectedFile();
			return file;
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default:
			break;
		}
		return null;

	}

	void updateClientCDPPath(String path) {
		ClientSettings settings = getContest().getClientSettings();
		if (settings == null) {
			settings = new ClientSettings(getContest().getClientId());
		}
		settings.put(CUSTOM_SUBMIT_SAMPLE_CDP_PATH, path);

		getController().updateClientSettings(settings);
	}

	protected String getClientCDPPath() {

		ClientSettings settings = getContest().getClientSettings();
		if (settings != null) {
			String path = settings.getProperty(CUSTOM_SUBMIT_SAMPLE_CDP_PATH);
			if (path != null) {
				return path;
			}
		}

		return null;
	}

	/**
	 * Reset fields back to default values
	 *
	 * @param usingGui
	 */
	protected void resetFields(boolean isUsingGui) {
		String cdpPath = getContest().getContestInformation().getJudgeCDPBasePath();

		String clientPath = getClientCDPPath();

		if (clientPath != null && !clientPath.equals(cdpPath)) {
			int result = FrameUtilities.yesNoCancelDialog(this, "Overwrite locally saved CDP path with this (default) " + cdpPath,
					"Replace CDP Path?");
			if (result == JOptionPane.NO_OPTION) {
				cdpPath = clientPath;
			}
		}

		submissionList = null;
		stopSubmissionTimer();
		clearSubmissionFiles();
		clearAllRuns();


		cdpTextField.setText(cdpPath);

//		getController().updateClientSettings(settings);

	}

	/**
	 * Submit sample solutions.
	 */
	protected void submitSelectedJudgesSolutions() {
	    showMessage("");

	    if(submissionFileList != null) {
	        showMessage("Please wait until the previous files have finished being submitted (" + (submissionList.size() - currentSubmission) + " remain)");
	        return;
	    }
	    String warningMessage = verifyCDP (cdpTextField.getText());
	    if (warningMessage != null) {
	        // let the user know that the CDP selected may not work
	        FrameUtilities.showMessage(this, "Maybe the CDP is invalid?", warningMessage);
	    }

        List<File> files = ListUtilities.getAllJudgeSampleSubmissionFilenamesFromCDP(getContest(), cdpTextField.getText());

        if (files.size() == 0) {
            FrameUtilities.showMessage(this, "No Submissions", "No submissions found under " + cdpTextField.getText());
            return;
        }

        // The actual filtering is done somewhat differently for potential submissions.  We can still use
        // the Filter object (filter) since it has all the criteria we need.
        if(filter.isFilterOn()) {
            // We use the custom filtering list for judge submission types, eg. accepted, wrong_answer, etc
            if(filter.isFilteringCustom()) {
                files = ListUtilities.filterByJudgingTypes(files, filter.getCustomList());
            }

            if(filter.isFilteringProblems()) {
                ElementId[] probElems = filter.getProblemIdList();
                ArrayList<Problem> probList = new ArrayList<Problem>();

                for(ElementId ele : probElems) {
                    Problem prob = getContest().getProblem(ele);
                    if(prob != null) {
                        probList.add(prob);
                    }
                }
                files = ListUtilities.filterByProblems(files, probList);
            }

            if(filter.isFilteringLanguages()) {
                ElementId[] langElems = filter.getLanguageIdList();
                ArrayList<Language> langList = new ArrayList<Language>();

                for(ElementId ele : langElems) {
                    Language lang = getContest().getLanguage(ele);
                    if(lang != null) {
                        langList.add(lang);
                    }
                }
                files = ListUtilities.filterByLanguages(files, getContest(), langList);
            }
        }
	    int count = 0;

	    if(Utilities.isDebugMode()) {
    	    for (File file : files) {
    	        count++;
    	        System.out.println("Will submit #" + count + " file = " + file.getAbsolutePath());
    	    }
	    } else {
	        count = files.size();
	    }
	    if (count == 0) {
	        showMessage("There are no matching CDP sample source files under " + cdpTextField.getText());
	        return;
	    }

	    // check with the user to be sure they want to submit everything.
        int result = FrameUtilities.yesNoCancelDialog(this, "Submit " + count + " judge sample submissions?",
              "Submit CDP submissions");
	    if (result != JOptionPane.YES_OPTION) {
	        return;
	    }
        showMessage("Submitting " + count + " runs.");

        submissionFileList = files;
        // create submissionList to add SubmissionSample's to as we submit them.
        submissionList = new ArrayList<SubmissionSample>();
        currentSubmission = 0;

        submitNextSubmission();
	}

	private void submitNextSubmission() {
	    try {
	        File file = submissionFileList.get(currentSubmission);

	        submissionTimerTask = new TimerTask() {
	            @Override
	            public void run() {
	                // Whoops! This means the submission never came in
	                log.severe("No submission was received from the server for #" + currentSubmission + "; cancelling remaining submissions");
	                showMessage("Submission #" + currentSubmission + " not received from server");
	                clearSubmissionFiles();
                    stopSubmissionTimer();
	            }
	        };

	        submissionWaitTimer = new Timer("Submission Wait Timer " + currentSubmission);
	        submissionWaitTimer.schedule(submissionTimerTask, SUBMISSION_WAIT_TIMEOUT_MS);

            SubmissionSample sub = submitter.sendSubmission(file);
            if(sub != null) {
                submissionList.add(sub);
                updateRunRow(sub, getContest().getClientId(), true);
            }
            if(Utilities.isDebugMode()) {
                System.out.println("Submitted #" + currentSubmission + " for problem " + sub.toString());
    	    }
	    } catch(Exception e) {
	        log.log(Level.WARNING, "Error submitting submission #" + currentSubmission, e);
	        clearSubmissionFiles();
	        stopSubmissionTimer();
	    }
	}

	private void clearSubmissionFiles() {
	    submissionFileList = null;
	    currentSubmission = -1;
	}

	private void stopSubmissionTimer() {
	    if(submissionWaitTimer != null) {
	        submissionWaitTimer.cancel();
	        submissionWaitTimer = null;
	        submissionTimerTask = null;
	    }
	}

	/**
	 * Check whether cdpDir and model match
	 * @param cdpDir base dir for CDP, parent dir for config/ dir
	 * @return null if no issues, else a warning message about a diffence between model and cdpDir
	 */
	private String verifyCDP(String cdpDir) {

        Problem[] problems = getContest().getProblems();
        List<File> files = new ArrayList<>();
        String missingProbs = "";
        String warningMsg = null;
        String configDir = cdpDir + File.separator + IContestLoader.CONFIG_DIRNAME;

        if (new File(configDir).isDirectory()) {
            cdpDir = configDir;
        }

        for (Problem problem : problems) {

            // config\sumit\submissions\accepted\ISumit.java
            if(!(new File(cdpDir + File.separator + problem.getShortName()).isDirectory())) {
                if(!missingProbs.isEmpty()) {
                    missingProbs = missingProbs + ',';
                }
                missingProbs = missingProbs + problem.getShortName();
            }
        }
        if(!missingProbs.isEmpty()) {
            warningMsg = "No problem folder found for: " + missingProbs;
        }
		return warningMsg;
	}

	/**
	 * Returns the list of filenames that end in extension
	 *
	 * @param files
	 * @param extension
	 * @return
	 */
	public static List<File> filterSource(List<File> files, String extension) {

		List<File> list = new ArrayList<File>();
		for (File file : files) {
			if (file.getName().endsWith(extension)) {
				list.add(file);
			}
		}
		return list;
	}

	@Override
	public String getPluginTitle() {
		return "Submitter Pane";
	}

	@Override
	public void setContestAndController(IInternalContest inContest, IInternalController inController) {
		super.setContestAndController(inContest, inController);

		resetFields(false);

		String cdpPath = getContest().getContestInformation().getJudgeCDPBasePath();
		String clientPath = getClientCDPPath();

		if (clientPath != null) {
			cdpPath = clientPath;
		}

//		xlog("CDP dir is now at " + cdpPath);
		cdpTextField.setText(cdpPath);

		showMessage("");

		submitter.setContestAndController(inContest, inController);
        getEditFilterFrame().setContestAndController(inContest, inController);


		log = inController.getLog();

        getContest().addRunListener(new RunListenerImplementation());

	}

	public void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
//				xlog(message);
				messageLabel.setText(message);
				messageLabel.setToolTipText(message);
			}
		});
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
            filterButton.setMnemonic(java.awt.event.KeyEvent.VK_F);
            filterButton.setPreferredSize(new Dimension(80, 27));
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showFilterRunsFrame();
                }
            });
        }
        return filterButton;
    }

    protected void showFilterRunsFrame() {
        getEditFilterFrame().addList(ListNames.CUSTOM_LIST);
        getEditFilterFrame().addCustomItems(getSubmissionSolutionList());
        getEditFilterFrame().setCustomTitle("Solution Type");
        getEditFilterFrame().addList(ListNames.LANGUAGES);
        getEditFilterFrame().addList(ListNames.PROBLEMS);

        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().validate();
        getEditFilterFrame().setVisible(true);
    }

    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null){
            Runnable callback = new Runnable(){
                @Override
                public void run() {
//                    reloadRunList();
                }
            };
            editFilterFrame = new EditFilterFrame(filter, filterFrameTitle,  callback);
        }
        return editFilterFrame;
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

    /**
     * This method initializes scrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getRunTable());
            resetRunsListBoxColumns();
        }
        return scrollPane;
    }

    /**
     * This method initializes the runTable
     *
     * @return JTableCustomized
     */
    private JTableCustomized getRunTable() {
        if (runTable == null) {
            runTable = new JTableCustomized() {
                private static final long serialVersionUID = 1L;

                // override JTable's default renderer to set the background color based on the match status
                // of the sample folder the submission was found in vs. the judgment.
                // Essentially stolen from ShadowCompareScoreboardPane.  Thank you JohnC.
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);

                    //default to normal background
                    c.setBackground(getBackground());

                    if(runTableModel != null) {
                        //map the specified row index number to the corresponding model row (index numbers can change due
                        // to sorting/scrolling; model row numbers never change).
                        // Here are the columns:
                        // Object[] fullColumns = { "Run Id", "Time", "Problem", "Expected", "Status", "Source", "Judge", "Language", "ElementId" };

                        int modelRow = convertRowIndexToModel(row);
                        String submissionAcronym = submissionSolutionList.getAcronymForSubmissionDirectory((String)runTableModel.getValueAt(modelRow, 3));

                        if(submissionAcronym == null) {
                            c.setBackground(matchColorMaybe);
                        } else {
                            String judgment = (String)runTableModel.getValueAt(modelRow, 4);
                            // Format is something like:  "Wrong answer:WA"
                            int idx = judgment.lastIndexOf(":");
                            if(idx != -1) {
                                judgment = judgment.substring(idx+1);
                                if(submissionAcronym.equals(judgment)) {
                                    c.setBackground(matchColorSuccess);
                                } else {
                                    c.setBackground(matchColorFailure);
                                }
                            } else {
                                // No judgment yet; eg "QUEUED_FOR_COMPUTER_JUDGEMENT" or other non-judged status
                                c.setBackground(matchColorPending);
                            }
                        }
                    }

                    return(c);
                }
            };

            runTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                   // If double-click see if we can select the run
                   if (me.getClickCount() == 2) {
                      JTable target = (JTable)me.getSource();
                      if(target.getSelectedRow() != -1 && isAllowed(Permission.Type.JUDGE_RUN)) {
//                          requestSelectedRun();
                      }
                   }
                }
             });
        }
        return runTable;
    }

    public void clearAllRuns() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(runTableModel != null) {
                    // All rows are discarded - the TM will notify the Table
                    runTableModel.setRowCount(0);
                }
            }
        });
    }

    private void resetRunsListBoxColumns() {

        runTable.removeAll();

        Object[] fullColumns = { "Run Id", "Time", "Problem", "Expected", "Status", "Source", "Judge", "Language", "ElementId" };
        Object[] columns;

        columns = fullColumns;
        runTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        runTable.setModel(runTableModel);
        TableColumnModel tcm = runTable.getColumnModel();
        // Remove ElementID from display - this does not REMOVE the column, just makes it so it doesn't show
        tcm.removeColumn(tcm.getColumn(columns.length - 1));

        // Sorters
        TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(runTableModel);

        runTable.setRowSorter(trs);
        runTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ArrayList<SortKey> sortList = new ArrayList<SortKey>();


        /*
         * Column headers left justified
         */
        ((DefaultTableCellRenderer)runTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        runTable.setRowHeight(runTable.getRowHeight() + VERT_PAD);


        StringToNumberComparator numericStringSorter = new StringToNumberComparator();

        int idx = 0;


//      Object[] fullColumns = { "Run Id", "Time", "Problem", "Expected", "Status", "Source", "Judge", "Language" };


        // These are in column order - omitted ones are straight string compare
        trs.setComparator(0, numericStringSorter);
        trs.setComparator(1, numericStringSorter);
        // These are in sort order
        sortList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
        sortList.add(new RowSorter.SortKey(7, SortOrder.ASCENDING));
        trs.setSortKeys(sortList);
        resizeColumnWidth(runTable);
    }

    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
    }

    protected Object[] buildRunRow(SubmissionSample sub, ClientId judgeId) {

        try {
            Run run = sub.getRun();

            int cols = runTableModel.getColumnCount();
            Object[] s = new Object[cols];

            int idx = 0;

//          Object[] fullColumns = { "Run Id", "Time", "Problem", "Expected", "Status", "Source", "Judge", "Language", ["ElementId"] };
            if(run != null) {
                boolean autoJudgedRun = isAutoJudgedRun(run);
                s[idx++] = Integer.toString(run.getNumber());
                s[idx++] = Long.toString(run.getElapsedMins());
                s[idx++] = getProblemTitle(sub.getProblem());
                s[idx++] = sub.getSampleType();
                s[idx++] = getJudgementResultString(run);
                s[idx++] = sub.getSourceFile().getName();
                s[idx++] = getJudgesTitle(run, judgeId, autoJudgedRun);
            } else {
                s[idx++] = "Waiting";
                s[idx++] = "N/A";
                s[idx++] = getProblemTitle(sub.getProblem());
                s[idx++] = sub.getSampleType();
                s[idx++] = "N/A";
                s[idx++] = sub.getSourceFile().getName();
                s[idx++] = "N/A";
            }
            s[idx++] = getLanguageTitle(sub.getLanguage());
            s[idx++] = sub.getElementId();

            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
        }
        return null;
    }

    private String getLanguageTitle(ElementId languageId) {
        Language language = getContest().getLanguage(languageId);
        if(language != null) {
            return(language.getDisplayName());
        }
        return "Language ?";
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = getContest().getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "Problem ?";
    }

    private String getJudgesTitle(Run run, ClientId judgeId, boolean autoJudgedRun) {

        String result = "";

        if (judgeId != null) {
            if (judgeId.equals(getContest().getClientId())) {
                result = "Me";
            } else {
                result = judgeId.getName() + " S" + judgeId.getSiteNumber();
            }
            if (autoJudgedRun) {
                result = result + "/AJ";
            }
        } else {
            result = "";
        }
        return result;
    }

    private boolean isAutoJudgedRun(Run run) {
        if (run.isJudged()) {
            if (!run.isSolved()) {
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
                    return judgementRecord.isUsedValidator();
                }
            }
        }
        return false;
    }

    /**
     * Return the judgement/status for the run.
     *
     * @param run
     * @return a string that represents the state of the run
     */
    protected String getJudgementResultString(Run run) {

        String result = "";
        String acronym = "";

        if (run.isJudged()) {

            if (run.isSolved()) {
                // oh my, this is bad, but, alas, copied from RunTable - gets the "accepted" judgment
                Judgement judgment = getContest().getJudgements()[0];
                acronym = ":" + judgment.getAcronym();

                result = judgment.getDisplayName();
                if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                    result = RunStates.MANUAL_REVIEW + " (" + result + ")";
                }

            } else {
                result = "No";

                JudgementRecord judgementRecord = run.getJudgementRecord();

                if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
                    Judgement judgment = getContest().getJudgement(judgementRecord.getJudgementId());
                    // Get acronym now, because we tack it on later
                    if(judgment != null) {
                        acronym = ":" + judgment.getAcronym();
                    }
                    if (judgementRecord.isUsedValidator() && judgementRecord.getValidatorResultString() != null) {
                        result = judgementRecord.getValidatorResultString();
                    } else {
                        if (judgment != null) {
                            result = judgment.toString();
                        }
                    }

                    if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                        result = RunStates.MANUAL_REVIEW + " (" + result + ")";
                    }

                    if (run.getStatus().equals(RunStates.BEING_RE_JUDGED)) {
                        result = RunStates.BEING_RE_JUDGED.toString();
                    }
                }
            }

        } else {
            result = run.getStatus().toString();
        }

        if (run.isDeleted()) {
            result = "DEL " + result;
        }

        result = result + acronym;


        return result;
    }

    /**
     * Find row that contains the supplied key (in last column)
     * @param value - unique key - really, the ElementId of run
     * @return index of row, or -1 if not found
     */
    private int getRowByKey(Object value) {
        Object o;

        if(runTableModel != null) {
            int col = runTableModel.getColumnCount() - 1;
            for (int i = runTableModel.getRowCount() - 1; i >= 0; --i) {
                o = runTableModel.getValueAt(i, col);
                if (o != null && o.equals(value)) {
                    return i;
                }
            }
        }
        return(-1);
    }

    /**
     * Remove run from grid by removing the data row from the TableModel
     *
     * @param run
     */
    private void removeRunRow(final Run run) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                int rowNumber = getRowByKey(run.getElementId());
                if (rowNumber != -1) {
                    runTableModel.removeRow(rowNumber);
                    updateRowCount();
                }
            }
        });
    }

    /**
     * This updates the rowCountlabel & toolTipText. It should be called only while on the swing thread.
     */
    private void updateRowCount() {
        if (filter.isFilterOn()){
            int totalRuns = getContest().getRuns().length;
            rowCountLabel.setText(runTable.getRowCount()+" of "+totalRuns);
            rowCountLabel.setToolTipText(runTable.getRowCount() + " filtered runs");
        } else {
            rowCountLabel.setText("" + runTable.getRowCount());
            rowCountLabel.setToolTipText(runTable.getRowCount() + " runs");
        }
    }

    public void updateRunRow(final SubmissionSample sub, final ClientId whoModifiedId, final boolean autoSizeAndSort) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                ClientId whoJudgedId = whoModifiedId;
                Run run = sub.getRun();
                if (run != null && run.isJudged()) {
                    JudgementRecord judgementRecord = run.getJudgementRecord();
                    if (judgementRecord != null) {
                        whoJudgedId = judgementRecord.getJudgerClientId();
                    }
                }

                Object[] objects = buildRunRow(sub, whoJudgedId);
                int rowNumber = getRowByKey(sub.getElementId());
                if (rowNumber == -1) {
                    // No row with this key - add new one
                    runTableModel.addRow(objects);
                } else {
                    // Update all fields
                    for(int j = runTableModel.getColumnCount()-1; j >= 0; j--) {
                        runTableModel.setValueAt(objects[j], rowNumber, j);
                    }
                }

                if (autoSizeAndSort) {
                    updateRowCount();
                    resizeColumnWidth(runTable);
                }

//                if (selectJudgementFrame != null) {
                        //TODO the selectJudgementFrame should be placed above all PC2 windows, not working when dblClicking in Windows OS
//                }
            }
        });
    }

    public void reloadRunList() {

        if (filter.isFilterOn()){
            getFilterButton().setForeground(Color.BLUE);
            getFilterButton().setToolTipText("Edit filter - filter ON");
            rowCountLabel.setForeground(Color.BLUE);
        } else {
            getFilterButton().setForeground(Color.BLACK);
            getFilterButton().setToolTipText("Edit filter");
            rowCountLabel.setForeground(Color.BLACK);
        }

        for (SubmissionSample sub : submissionList) {
            ClientId clientId = null;

            Run run = sub.getRun();
            if(run != null) {
                RunStates runStates = run.getStatus();
                if (!(runStates.equals(RunStates.NEW) || run.isDeleted())) {
                    JudgementRecord judgementRecord = run.getJudgementRecord();
                    if (judgementRecord != null) {
                        clientId = judgementRecord.getJudgerClientId();
                    }
                }
            }
            updateRunRow(sub, clientId, false);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateRowCount();
                resizeColumnWidth(runTable);
            }
        });
    }
    /**
     * Run Listener
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        @Override
        public void runAdded(RunEvent event) {
            SubmissionSample sub = null;
            Run run = event.getRun();
            ClientId me = getContest().getClientId();

            // We are only interested in runs we submitted
            if(run.getSubmitter().equals(me)) {

                // JB - think this test for duplicate is inadequate.
                // We should remember the run id in a hashmap or something and ignore it if was seen
                SubmissionSample dupRun = getSubmission(event);
                if(dupRun == null) {
                    try {
                        // This is the last run - it has to be the one that was just added by us
                        sub = submissionList.get(currentSubmission);
                    } catch (Exception e) {
                        log.log(Level.WARNING, "No submission sample for run id " + run.getNumber(), e);
                        if(Utilities.isDebugMode()) {
                            System.out.println("No submission runAdded (" + run.getNumber() + ") - currentSubmission #" + currentSubmission);
                        }
                    }
                } else {
                    log.log(Level.WARNING, "Duplicate runAdded event for Run id " + run.getNumber() + " ignored.");
                    if(Utilities.isDebugMode()) {
                        System.out.println("Duplicate runAdded (" + run.getNumber() + ") ignored - currentSubmission #" + currentSubmission);
                    }
                }
            }
            if(sub != null) {
                stopSubmissionTimer();
                sub.setRun(run);
                if(Utilities.isDebugMode()) {
                    System.out.println("Received runAdded currentSubmission #" + currentSubmission + " for problem " + sub.toString());
                }
                updateRunRow(sub, event.getWhoModifiedRun(), true);
                // setup for next submission; if last one clean things up.
                currentSubmission++;
                if(currentSubmission >= submissionFileList.size()) {
                    clearSubmissionFiles();
                } else {
                    submitNextSubmission();
                }
            }
        }

        @Override
        public void refreshRuns(RunEvent event) {
            reloadRunList();
        }

        @Override
        public void runChanged(RunEvent event) {
            SubmissionSample sub = getSubmission(event);
            if(sub != null) {
                updateRunRow(sub, event.getWhoModifiedRun(), true);
            }
        }

        @Override
        public void runRemoved(RunEvent event) {
            SubmissionSample sub = getSubmission(event);
            if(sub != null) {
                updateRunRow(sub, event.getWhoModifiedRun(), true);
            }
        }

        private SubmissionSample getSubmission(RunEvent event)
        {
            Run run = event.getRun();

            // We are only interested in runs we submitted
            if(run.getSubmitter().equals(getContest().getClientId())) {
                for(SubmissionSample sub : submissionList) {
                    Run subRun = sub.getRun();
                    // Check run numbers if this submission has a run
                    if(subRun != null && subRun.getNumber() == run.getNumber()) {
                        sub.setRun(run);
                        return(sub);
                    }
                }
            }
            return(null);
        }
    }


} // @jve:decl-index=0:visual-constraint="10,10"
