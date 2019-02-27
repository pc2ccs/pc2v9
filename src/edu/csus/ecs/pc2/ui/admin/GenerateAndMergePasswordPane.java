package edu.csus.ecs.pc2.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.export.MailMergeFile;
import edu.csus.ecs.pc2.tools.PasswordGenerator;
import edu.csus.ecs.pc2.tools.PasswordType2;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.IntegerDocument;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.MultipleFileViewer;

/**
 * Generate and Merge Passwords Panel.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class GenerateAndMergePasswordPane extends JPanePlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737925511286832544L;

	private JTextField lengthTextBox;

	private JTextField prefixTextField;

	private JTextField numberTextField;

	private JComboBox<PasswordType2> passwordTypeComboBox = null;

	private JLabel samplePasswordLabel;

	public GenerateAndMergePasswordPane() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(null);

		JLabel lblType = new JLabel("Type");
		lblType.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblType.setBounds(10, 18, 91, 14);
		panel.add(lblType);

		passwordTypeComboBox = new JComboBox<PasswordType2>();
		passwordTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSample();
			}
		});
		passwordTypeComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
		passwordTypeComboBox.setBounds(111, 15, 238, 20);
		panel.add(passwordTypeComboBox);

		JLabel lengthLabel = new JLabel("Length");
		lengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lengthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lengthLabel.setBounds(10, 88, 91, 14);
		panel.add(lengthLabel);

		lengthTextBox = new JTextField();

		lengthTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				updateSample();
			}
		});

		lengthTextBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lengthTextBox.setBounds(115, 85, 46, 20);
		panel.add(lengthTextBox);
		lengthTextBox.setColumns(10);
		lengthTextBox.setDocument(new IntegerDocument());
		lengthTextBox.setText("8");

		JLabel lblPrefix = new JLabel("Prefix");
		lblPrefix.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lblPrefix.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrefix.setBounds(10, 123, 91, 14);
		panel.add(lblPrefix);

		prefixTextField = new JTextField();
		prefixTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				updateSample();
			}
		});
		prefixTextField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		prefixTextField.setBounds(111, 120, 100, 20);
		panel.add(prefixTextField);
		prefixTextField.setColumns(10);

		JLabel lblSample = new JLabel("Sample");
		lblSample.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lblSample.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSample.setBounds(10, 161, 91, 20);
		panel.add(lblSample);

		samplePasswordLabel = new JLabel("Sample Password ");
		samplePasswordLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
		samplePasswordLabel.setBounds(115, 161, 277, 20);
		panel.add(samplePasswordLabel);

		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setHgap(45);
		add(buttonPanel, BorderLayout.SOUTH);

		JButton viewSampleButton = new JButton("View Sample");
		viewSampleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewSampleList();
			}
		});
		viewSampleButton.setMnemonic('V');
		buttonPanel.add(viewSampleButton);

		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleExport();
			}
		});
		btnExport.setMnemonic('X');
		buttonPanel.add(btnExport);

		

		JLabel countLabel = new JLabel("Number");
		countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
		countLabel.setBounds(10, 53, 91, 14);
		panel.add(countLabel);

		numberTextField = new JTextField();
		numberTextField.setText("80");
		numberTextField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		numberTextField.setColumns(10);
		numberTextField.setBounds(115, 50, 46, 20);
		panel.add(numberTextField);
		
		loadComboBox(passwordTypeComboBox);
	}

	private void loadComboBox(JComboBox<PasswordType2> comboBox) {

		PasswordType2[] values = PasswordType2.values();

		int letValIndex = 0;
		int idx = 0;
		for (PasswordType2 passwordType2 : values) {

			if (PasswordType2.LETTERS_AND_DIGITS.equals(passwordType2)) {
				letValIndex = idx;
			}

			if (!PasswordType2.NONE.equals(passwordType2)) {
				comboBox.addItem(passwordType2);
				idx++;
			}

		}
		comboBox.setSelectedIndex(letValIndex);

	}

	protected void handleExport() {

		// TODO write to file
	    
	    File file = saveAsFileDialog(this, ".", MailMergeFile.PASSWORD_LIST_FILENNAME);
	    
	    if (file != null) {
            // Save panel to file
            
            if (file.isFile()){
                
                int result = FrameUtilities.yesNoCancelDialog(this, "Overwrite "+file.getName()+" ?", "Overwrite File?");

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            List<String> list = generatePasswords();
            try {
                writeFileContents(file.getAbsolutePath(), list);
            } catch (FileNotFoundException e) {
                showMessage(this, "Cannot write file", "Cannot write file "+file.getAbsolutePath()+". "+e.getMessage());
            }
        }
	}

	/**
	 * Generate passwords based on pane fields.
	 * 
	 */
	protected List<String> generatePasswords() {

		List<String> outList = new ArrayList<String>();

		int count = getCount();

		int passwordLength = getPasswordLength();

		String prefix = getPasswordPrefix();

		PasswordType2 passwordType = getPasswordType();
		
		List<String> list = null;
		if (!PasswordType2.JOE.equals(passwordType)) {
			list = PasswordGenerator.generatePasswords(count, passwordType, passwordLength, prefix);
		}

		for (int i = 0; i < count; i++) {

			if (PasswordType2.JOE.equals(passwordType)) {
				String joePassword = (prefix == null ? "" : prefix) + "team" + (i + 1);
				outList.add(joePassword);
			} else {
				outList.add(list.get(i));
			}
		}

		return outList;
	}

	protected void viewSampleList() {

		System.out.println("debug 22 code viewSampleList ");
		
		MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
		
		List<String> list = generatePasswords();
		String[] lines = (String[]) list.toArray(new String[list.size()]);
        multipleFileViewer.addTextintoPane("Passwords", lines); 
        multipleFileViewer.setTitle(" Passwords (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
	}

	protected void updateSample() {

		List<String> list = generatePasswords();
		samplePasswordLabel.setText(list.get(0));
	}

	private PasswordType2 getPasswordType() {
		return (PasswordType2) passwordTypeComboBox.getSelectedItem();
	}

	private String getPasswordPrefix() {
		String prefix = prefixTextField.getText().trim();
		if (prefix.length() == 0) {
			prefix = null;
		}
		return prefix;
	}

	/**
	 * Number of passwords.
	 */
	private int getCount() {
		String countString = numberTextField.getText();
		int count = 1;
		if (countString.trim().length() > 0) {
			count = Integer.parseInt(countString);
		}
		return count;
	}

	/**
	 * Number of passwords.
	 */
	private int getPasswordLength() {
		String countString = lengthTextBox.getText();
		int count = 1;
		if (countString.trim().length() > 0) {
			count = Integer.parseInt(countString);
		}
		return count;
	}

	@Override
	public String getPluginTitle() {
		return "Generare Passwords and Merge Pane";
	}
}
