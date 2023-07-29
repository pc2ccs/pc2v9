// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ClipboardUtilities;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.list.ListUtilities;
import edu.csus.ecs.pc2.ui.team.QuickSubmitter;

/**
 * A UI that to submit files found in a CDP.
 * 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SubmitSampleRunsPane extends JPanePlugin {

	/**
	 * ClientSettings key for CDP Path
	 */
	private static final String CUSTOM_SUBMIT_SAMPLE_CDP_PATH = "CustomSubmitSampleCDPPath";

	private static final long serialVersionUID = -8862440024499524533L;

	private JTextField cdpTextField;

	private JLabel messageLabel;

	private JCheckBox checkBoxSubmitYesSamples;

	private JCheckBox checkBoxSubmitFailingSamples;

	private QuickSubmitter submitter = new QuickSubmitter();

	private JTextArea textArea;

	private SimpleDateFormat hhMMSSformatter = new SimpleDateFormat("hh:mm:ss");

	private int linenumber = 0;
	/**
	 * @wbp.nonvisual location=162,159
	 */
	private final ButtonGroup judgingTypesButtonGroup = new ButtonGroup();
	
	private final ButtonGroup problemsButtonGroup = new ButtonGroup();
    
	private final ButtonGroup languagesButtonGroup = new ButtonGroup();
    
	
	/**
	 * List of selected solutions names and dirs.
	 */
	private SubmissionSolutionList submissionSolutionList = null;
	
	/**
	 * List of selected Problems.
	 * 
	 */
	private List<Problem> selectedProblemList = null;
	
    private List<Language> selectedLanguageList = null;
    
	private int[] selectedJudgingTypeIndexes;
	
	private int[] selectedProblemsIndexes;
	
	private int[] selectedLanguagesIndexes;
	
	private JLabel selectedNosLabel = new JLabel("None selected");
	
	private JRadioButton submitAllJudgingTypesRadioButton = new JRadioButton("Submit All");
	
	private JRadioButton submitSelectedJudgingTypeRadioButton = new JRadioButton("Submit Selected");
	
    private JCheckBox checkBoxProblems = null;

    private JRadioButton selecteAllProblemsRadioButton = null;
    private JRadioButton submitAllProblemsRadioButton;

    private JRadioButton submitSelectedProblems = null;
    private JRadioButton submitSelectedProblemsRadioButton;

    private JLabel selectedProblemsLabel = null;

    private JButton selectProblemsButton = null;

    private JCheckBox checkBoxLanguage = null;

    private JRadioButton selecteAllLanguages = null;
    private JRadioButton submitAllLanguagesRadioButton;

    private JRadioButton submitSelectedLanguages = null;
    private JRadioButton submitSelectedLanguagesRadioButton;

    private JLabel selectedLanguagesLabel = null;

    private JButton selectLanguageButton = null;


    private Log log;

    private boolean debug22Flag = false;

    // TODO On Admin update of Languages or Update of Problems - clear selected index arrays

	public SubmitSampleRunsPane() {
		super();
		setLayout(new BorderLayout(0, 0));

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BorderLayout());
		add(centerPane, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPane.add(splitPane, BorderLayout.CENTER);

		JPanel controlsPane = new JPanel();
		controlsPane.setPreferredSize(new Dimension(400, 400));

		controlsPane.setLayout(null);

		JLabel cdpConfigDirLabel = new JLabel("CDP config dir");
		cdpConfigDirLabel.setToolTipText("CDP Location for sample source files");
		cdpConfigDirLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		cdpConfigDirLabel.setBounds(10, 60, 86, 14);
		controlsPane.add(cdpConfigDirLabel);

		cdpTextField = new JTextField();
		cdpTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		cdpTextField.setBounds(113, 54, 418, 27);
		controlsPane.add(cdpTextField);
		cdpTextField.setColumns(10);
		
        cdpTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (new File(cdpTextField.getText()).isDirectory()) {
                        updateCDPDirAndFields(cdpTextField.getText());
                    }
                }

            }
        });

		messageLabel = new JLabel("message label");
		messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		messageLabel.setForeground(Color.RED);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setBounds(10, 11, 691, 32);
		controlsPane.add(messageLabel);

		checkBoxSubmitYesSamples = new JCheckBox("Submit Yes Samples");
		checkBoxSubmitYesSamples.setSelected(true);
		checkBoxSubmitYesSamples.setToolTipText("Only submit AC sample source");
		checkBoxSubmitYesSamples.setBounds(33, 102, 265, 23);
		controlsPane.add(checkBoxSubmitYesSamples);

		JButton selectCDPButton = new JButton("...");
		selectCDPButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectNewCDP();
			}
		});
		selectCDPButton.setToolTipText("Select a different CDP");
		selectCDPButton.setBounds(541, 56, 39, 23);
		controlsPane.add(selectCDPButton);

		JPanel LogPanel = new JPanel();
		LogPanel.setSize(new Dimension(400, 400));
		LogPanel.setMinimumSize(new Dimension(400, 400));

		LogPanel.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
		textArea.setSize(new Dimension(360, 360));
		JScrollPane scrollPane = new JScrollPane(textArea);
		LogPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(50, 50));
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setHgap(45);
		LogPanel.add(panel, BorderLayout.SOUTH);

		JButton clearTextAButton = new JButton("Clear");
		clearTextAButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTextArea();
			}

		});
		panel.add(clearTextAButton);

		JButton copyButton = new JButton("Copy");
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClipboardUtilities.put(textArea.getText());
			}
		});
		copyButton.setToolTipText("Copy text into clipboard");
		panel.add(copyButton);

		JPanel bottomPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
		flowLayout.setHgap(125);
		add(bottomPane, BorderLayout.SOUTH);

		JButton submitRunButton = new JButton("Submit");
		submitRunButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    submitSelectedJudgesSolutions();
			}
		});

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetFields(true);
			}
		});
		resetButton.setToolTipText("Reset back to default settings");
		bottomPane.add(resetButton);
		submitRunButton.setToolTipText("Submit selected sample runs");
		bottomPane.add(submitRunButton);

//		centerPane2.add(controlsPane, BorderLayout.CENTER);
		JScrollPane leftSide = new JScrollPane(controlsPane);
		
		JPanel submitNoSamplesPane = new JPanel();
		submitNoSamplesPane.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout fl_submitNoSamplesPane = (FlowLayout) submitNoSamplesPane.getLayout();
		fl_submitNoSamplesPane.setAlignment(FlowLayout.LEFT);
		submitNoSamplesPane.setBounds(33, 131, 650, 32);
		controlsPane.add(submitNoSamplesPane);

		checkBoxSubmitFailingSamples = new JCheckBox("Submit Failing Samples");
		checkBoxSubmitFailingSamples.setVerticalAlignment(SwingConstants.TOP);
		submitNoSamplesPane.add(checkBoxSubmitFailingSamples);
		checkBoxSubmitFailingSamples.setSelected(true);
		checkBoxSubmitFailingSamples.setToolTipText("Submt all non-AC (Yes) submissions");

		judgingTypesButtonGroup.add(submitAllJudgingTypesRadioButton);
		submitAllJudgingTypesRadioButton.setSelected(true);
		judgingTypesButtonGroup.add(submitSelectedJudgingTypeRadioButton);
		
		submitNoSamplesPane.add(submitAllJudgingTypesRadioButton);
		submitNoSamplesPane.add(submitSelectedJudgingTypeRadioButton);
		
		leftSide.setPreferredSize(new Dimension(230, 230));
		leftSide.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		leftSide.setWheelScrollingEnabled(true);
		splitPane.setLeftComponent(leftSide);

		// centerPane2.add(LogPanel, BorderLayout.SOUTH);
		JScrollPane logScrollPanel = new JScrollPane(LogPanel);
		logScrollPanel.setPreferredSize(new Dimension(60, 60));
		logScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		logScrollPanel.setWheelScrollingEnabled(true);
		splitPane.setRightComponent(logScrollPanel);
		
		
		selectedNosLabel.setToolTipText("None selected");
		selectedNosLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			    showJudgementTypeList();
			}
		});
		submitNoSamplesPane.add(selectedNosLabel);
		JButton selectJudementTypesButton = new JButton("Select");
		selectJudementTypesButton.setToolTipText("Select Judgement Types");
		selectJudementTypesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    showJudgementTypeList();
			}
		});
		
		submitNoSamplesPane.add(selectJudementTypesButton);
		
		JLabel lblNewLabel_2 = getWhatsThisOne();
		submitNoSamplesPane.add(lblNewLabel_2);
		
		JPanel submitNoSamplesPane_1 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) submitNoSamplesPane_1.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		submitNoSamplesPane_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		submitNoSamplesPane_1.setBounds(33, 161, 650, 32);
		controlsPane.add(submitNoSamplesPane_1);
		   
        checkBoxProblems = new JCheckBox("Problems");
        checkBoxProblems.setVerticalAlignment(SwingConstants.TOP);
        checkBoxProblems.setToolTipText("Select Problems");
        checkBoxProblems.setSelected(true);
        submitNoSamplesPane_1.add(checkBoxProblems);
        
        submitAllProblemsRadioButton = new JRadioButton("Submit All");
        submitAllProblemsRadioButton.setSelected(true);
        submitNoSamplesPane_1.add(submitAllProblemsRadioButton);
        
        submitSelectedProblemsRadioButton = new JRadioButton("Submit Selected");
        submitNoSamplesPane_1.add(submitSelectedProblemsRadioButton);
        
        selectedProblemsLabel = new JLabel("None selected");
        selectedProblemsLabel.setToolTipText("None selected");
        submitNoSamplesPane_1.add(selectedProblemsLabel);
        
        selectProblemsButton = new JButton("Select");
        selectProblemsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProblemList();
            }
        });
        selectProblemsButton.setToolTipText("Select Problems");
        submitNoSamplesPane_1.add(selectProblemsButton);
        
        JPanel submitLanguages = new JPanel();
        FlowLayout fl_submitLanguages = (FlowLayout) submitLanguages.getLayout();
        fl_submitLanguages.setAlignment(FlowLayout.LEFT);
        submitLanguages.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        submitLanguages.setBounds(33, 191, 650, 32);
        controlsPane.add(submitLanguages);
        
        checkBoxLanguage = new JCheckBox("Languages");
        checkBoxLanguage.setVerticalAlignment(SwingConstants.TOP);
        checkBoxLanguage.setToolTipText("Select Languages");
        checkBoxLanguage.setSelected(true);
        submitLanguages.add(checkBoxLanguage);
        
        submitAllLanguagesRadioButton = new JRadioButton("Submit All Languages");
        submitAllLanguagesRadioButton.setSelected(true);
        submitLanguages.add(submitAllLanguagesRadioButton);
        
        submitSelectedLanguagesRadioButton = new JRadioButton("Submit Selected");
        submitLanguages.add(submitSelectedLanguagesRadioButton);
        
        selectedLanguagesLabel = new JLabel("None selected Lang?");
        selectedLanguagesLabel.setToolTipText("None selected");
        submitLanguages.add(selectedLanguagesLabel);
        
        selectLanguageButton = new JButton("Select");
        selectLanguageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLanguageList();
            }
        });
        selectLanguageButton.setToolTipText("Select Problems");
        submitLanguages.add(selectLanguageButton);
        

        languagesButtonGroup.add(submitAllLanguagesRadioButton);
        languagesButtonGroup.add(submitSelectedLanguagesRadioButton);
        
        problemsButtonGroup.add(submitAllProblemsRadioButton);
        problemsButtonGroup.add(submitSelectedProblemsRadioButton);
        
		VersionInfo info = new VersionInfo();
		String verstring = info.getBuildNumber() + " x " + info.getPC2Version();
		addLineToTextArea(verstring);
		
		
		
	}

	protected void showLanguageList() {

	    // TODO show language selection
	    
        Language[] Languages = getContest().getLanguages();
        
        if (Languages.length == 0) {
            showMessage("No Languagess defined in contest");
            return;
        }
    
        JListFrame selectLanguagessFrame = new JListFrame("Select Languages ", Languages, selectedLanguagesIndexes, new ISelectedListsSetter() {
            
            @Override
            public void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices) {
                selectedLanguagesIndexes = selectedIndices;
                updateLanguagesLabel(selectedValuesList);
            }
        });
        selectLanguagessFrame.setVisible(true);
    }
 

    protected void showProblemList() {
        
        Problem[] problems = getContest().getProblems();
        
        if (problems.length == 0) {
            showMessage("No problems defined in contest");
            return;
        }
    
        JListFrame selectProblemsFrame = new JListFrame("Select Problems ", problems, selectedProblemsIndexes, new ISelectedListsSetter() {
            
            @Override
            public void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices) {
                selectedProblemsIndexes = selectedIndices;
                updateProblemsLabel(selectedValuesList);
            }
        });
        selectProblemsFrame.setVisible(true);
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
	        updateNosLabel(null);
        } catch (Exception e) {
            log.log(Level.WARNING, "Problem updating CDP Dir", e);
        }
    }

    /**
	 * Dialog with question mark ison for info on selecting judgement types.
	 * @return
	 */
	private JLabel getWhatsThisOne() {
		
		String[] messageLines = {
				"Selecting judgement types", //
				"", //
				"Use Select button to select judgement types", //
		};
		
		return FrameUtilities.getWhatsThisLabel("Selecting judgement samples", messageLines);
	}
	
	/**
	 * Repopulate submissionSolutionList.
	 * 
	 * @return 
	 */
	public SubmissionSolutionList getSubmissionSolutionList() {
		String cdpPath = cdpTextField.getText();
		
		SubmissionSolutionList list = new SubmissionSolutionList(new File(cdpPath));
		Collections.reverse(list);
		submissionSolutionList = list;
		
		return submissionSolutionList;
	}

	protected void showJudgementTypeList() {
		
		if (getSubmissionSolutionList() == null || getSubmissionSolutionList().size() == 0) {
			showMessage("No submission judgement types found in dir: "+cdpTextField.getText());
			return;
		}
		
		SubmissionSampleLocation[] listData = toArray(getSubmissionSolutionList());
	
		JListFrame selectNoSolutionsFrame = new JListFrame("Select No/Failed Judges solutions", listData, selectedJudgingTypeIndexes, new ISelectedListsSetter() {
			
			@Override
			public void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices) {
				selectedJudgingTypeIndexes = selectedIndices;
				updateNosLabel(selectedValuesList);
			}
		});
		selectNoSolutionsFrame.setVisible(true);
	}

	private SubmissionSampleLocation[] toArray(SubmissionSolutionList list) {
		return (SubmissionSampleLocation[]) list.toArray(new SubmissionSampleLocation[list.size()]);
	}
	
	/**
	 * Update selected languages label and selected language list
	 * @param valuesList
	 */
	protected void updateLanguagesLabel(List<Object> valuesList) {
	    if (valuesList == null || valuesList.size() == 0) {
	        selectedLanguagesLabel.setText("None selected");
	        selectedLanguagesLabel.setToolTipText("None selected");
        } else {
            selectedLanguagesLabel.setText(valuesList.size()+" selected");
            selectedLanguagesLabel.setToolTipText(Arrays.toString(valuesList.toArray()));
            
            selectedLanguageList = new ArrayList<Language>();
            valuesList.forEach((lang) -> {selectedLanguageList.add((Language)lang);});
            
            // TODO huh debug 22
            
        }
	}

	protected void updateProblemsLabel(List<Object> valuesList) {
	    
	    // TODO write code
	    if (valuesList == null || valuesList.size() == 0) {
            selectedProblemsLabel.setText("None selected");
            selectedProblemsLabel.setToolTipText("None selected");
        } else {
            
            selectedProblemsLabel.setText(valuesList.size()+" selected");
            selectedProblemsLabel.setToolTipText(Arrays.toString(valuesList.toArray()));
            
            selectedProblemList = new ArrayList<Problem>();
            valuesList.forEach((prob) -> {selectedProblemList.add((Problem)prob);});
        }
	}

	protected void updateNosLabel(List<Object> valuesList) {

        if (valuesList == null || valuesList.size() == 0) {
            selectedNosLabel.setText("None selected");
            selectedNosLabel.setToolTipText("None selected");
        } else {

            selectedNosLabel.setText(valuesList.size() + " selected");
            selectedNosLabel.setToolTipText(Arrays.toString(valuesList.toArray()));
            submissionSolutionList = new SubmissionSolutionList();

            // update/load selected items into submissionSolutionList
            valuesList.forEach((subSL) -> {
                submissionSolutionList.add((SubmissionSampleLocation) subSL);
            });

        }
    }

	protected void clearTextArea() {
		textArea.selectAll();
		textArea.replaceSelection("");
		linenumber = 0;
	}

	void addLineToTextArea(String s) {

		linenumber++;
		String fmtLineNumber = String.format("%4d", linenumber);

		textArea.insert(fmtLineNumber + " " + hhMMSSformatter.format(new Date()) + " " + s + "\n", 0);
//		textArea.append(s);
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

	private void xlog(String string) {
		System.out.println(string);
		addLineToTextArea(string);
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
	    
	    
	    selectedJudgingTypeIndexes = new int[0];
	    selectedProblemsIndexes = new int[0];
	    selectedLanguagesIndexes = new int[0];

		checkBoxSubmitYesSamples.setSelected(true);
		checkBoxSubmitFailingSamples.setSelected(true);
		
		checkBoxLanguage.setSelected(true);
        checkBoxProblems.setSelected(true);
        
        submitAllJudgingTypesRadioButton.setSelected(true);
        submitAllProblemsRadioButton.setSelected(true);
        submitAllLanguagesRadioButton.setSelected(true);

		String cdpPath = getContest().getContestInformation().getJudgeCDPBasePath();

		String clientPath = getClientCDPPath();

		if (clientPath != null && isUsingGui) {
			int result = FrameUtilities.yesNoCancelDialog(this, "Overwrite locally saved CDP path with this (default) " + cdpPath,
					"Replace CDP Path?");
			if (result == JOptionPane.NO_OPTION) {
				cdpPath = clientPath;
			}
		}

		cdpTextField.setText(cdpPath);

//		getController().updateClientSettings(settings);;

	}

	/**
	 * Submit sample solutions.
	 */
	protected void submitSelectedJudgesSolutions() {
	    showMessage("");

	    String warningMessage = verifyCDP (cdpTextField.getText());
	    if (warningMessage != null) {
	        // let the user know that the CDP selected may not work
	        FrameUtilities.showMessage(this, "Trying to use an invalid CDP?", warningMessage);
	    }

        List<File> allFiles = ListUtilities.getAllJudgeSampleSubmissionFilenamesFromCDP(getContest(), cdpTextField.getText());

        if (allFiles.size() == 0) {
            FrameUtilities.showMessage(this, "No Runs", "No Runs found under " + cdpTextField.getText());
            return;
        }

        boolean submitYesSamples = checkBoxSubmitYesSamples.isSelected();
        boolean submitNoSamples = checkBoxSubmitFailingSamples.isSelected();

        if (!submitYesSamples && !submitNoSamples) {
            FrameUtilities.showMessage(this, "No Runs", "Select either Yes or Failed runs");
            return;
        }

        List<File> files = new ArrayList<File>();

        files = QuickSubmitter.filterRuns(allFiles, submitYesSamples, submitNoSamples);

        if (submitNoSamples && submitSelectedJudgingTypeRadioButton.isSelected()) {
            if (submitSelectedJudgingTypeRadioButton.isSelected()) {
                // if they selected the radio button for selected, they should select at least
                if (selectedJudgingTypeIndexes == null || selectedJudgingTypeIndexes.length == 0) {
                    FrameUtilities.showMessage(this, "No Judging Types selected", "No Judging Types selected, select at least one");
                    return;
                }

                // fitler by judging type (accepted, wrong_answer, etc.)
                files = ListUtilities.filterByJudgingTypes(files, submissionSolutionList);
            }
        }
	    
        if (checkBoxProblems.isSelected() ) {
            if (submitSelectedProblemsRadioButton.isSelected()) {
                // submissions may be filtered by problem

                if (selectedProblemsIndexes == null || selectedProblemsIndexes.length == 0) {
                    FrameUtilities.showMessage(this, "No Problem selected", "No Problems selected, select at least one");
                    return;
                }

                // filter list by problem name
                files = ListUtilities.filterByProblems (files, selectedProblemList);

            }
        }
	    
	    if (checkBoxLanguage.isSelected()) {
	        // submissions may be filtered by language
	        
	        if (submitSelectedLanguagesRadioButton.isSelected()) {
	            if (selectedLanguagesIndexes == null || selectedLanguagesIndexes.length == 0) {
	                FrameUtilities.showMessage(this, "No Language selected", "No Languages selected, select at least one");
	                return;

	            }

	            files = ListUtilities.filterByLanguages(files, getContest(), selectedLanguageList);
	        }
	    }
	    

	    int count = 0;
	    for (File file : files) {
	        count++;
	        xlog("Will submit #" + count + " file =" + file.getAbsolutePath());
	    }

	    if (count == 0) {
	        showMessage("There are no CDP samples source files  under " + cdpTextField.getText());
	        return;
	    }

	    int result = FrameUtilities.yesNoCancelDialog(this, "Submit " + files.size() + " sample submissions?",
	            "Submit CDP submissions");

	    if (result == JOptionPane.YES_OPTION) {
	        /**
	         * This will submit each file found
	         */
	        submitter.sendSubmissions(files);

	        showMessage("Submitted " + files.size() + " runs.");
	    }


	}

	/**
	 * Check whether cdpDir and model match
	 * @param cdpDir base dir for CDP, parent dir for config/ dir
	 * @return null if no issues, else a warning message about a diffence between model and cdpDir
	 */
	private String verifyCDP(String cdpDir) {
		
		// TODO compare problems in CDP with problems in model 
		
		return null;
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

		xlog("CDP dir is now at " + cdpPath);
		cdpTextField.setText(cdpPath);

		showMessage("");

		submitter.setContestAndController(inContest, inController);
		
		
		log = inController.getLog();

	}

	public void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				xlog(message);
				messageLabel.setText(message);
				messageLabel.setToolTipText(message);
			}
		});
	}


} // @jve:decl-index=0:visual-constraint="10,10"
