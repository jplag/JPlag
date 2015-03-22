/*
 * Created on Jul 4, 2005
 *
 */
package atujplag.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jplagWsClient.jplagClient.Option;
import atujplag.ATUJPLAG;
import atujplag.client.SimpleClient;
import atujplag.util.LanguageSetting;
import atujplag.util.Messages;
import atujplag.util.SubmissionManager;
import atujplag.util.TagParser;

/**
 * @author Emeric Kwemou
 */
public class OptionPanel extends JDialog implements ActionListener,
		DocumentListener {

    /*
     * Constants describing error messages of validateOptions()
     */
    
	private static final long serialVersionUID = -6556063048578752374L;
	private static final int NOERROR = 0;
    private static final int GENOPTERROR = 1;
    private static final int ADVOPTERROR = 2;
    
	private static final int SCAN_DELAY = 500; // in milliseconds

    private ATUJPLAG atujplag = null;
    private View view = null;

    private SimpleClient client = null;
    private SubmissionTree treePreview = null;
    private boolean reopenPreview = false;

    private boolean isSubmitted = false;
    private String oldBasecodeDir = ""; //$NON-NLS-1$
    private boolean advOptionsUpToDate = false;

    private Vector<SubmissionManager> previewSubs = null;

    private Thread scanThread;
    private boolean firstScan = false;  // if true, doesn't override oldBasecodeDir
    private boolean updatingSubDirCB = false;

    private Timer validateTitleTimer = null;
    private Timer updatePreviewTimer = null;
    
    /*
     * GUI components
     */
    
    // Content panel

	private JPanel jContentPane = null;
    private JPanel jOptionPanel = null;
    private JPanel jOptionButtonsPanel = null;
    private JPanel jControlPanel = null;
    
    // Option panel
    
    private JPanel jGeneralOptionsPanel = null;
    private JPanel jAdvancedOptionsPanel = null;
    
    // General options panel
    
	private JComboBox<String> jLanguageCB = null;
	private JComboBox<String> jCompModeCB = null;
    private ItemListener languageItemListener = null;
    private JTextField jTitleField = null;
    private DocumentListener titleListener = null;
    private JTextField jSubmissionDirField = null;
    private JButton jFileChooserButton = null;
    private JCheckBox jRecurseDirCBBox = null;
    private ItemListener recurseDirCBListener = null;
    
    // Advanced options panel

    private JTextField jSuffixesField = null;
    private JPanel jSufAndBCPanel = null;
    private DocumentListener suffixesListener = null;
    private JComboBox<String> jBasecodeCB = null;
    private JPanel jMmlAndClusterPanel = null;
    private JSpinner jMinMatchLenSpinner = null;
	private JComboBox<String> jClusterTypeCB = null;
	private JComboBox<String> jStoreCB = null;
    private JSpinner jStoreSpinner = null;
    private JComboBox<String> jSubdirCB = null;
    private ItemListener subdirCBListener = null;
    private JButton jApplyDefaultsButton = null;

    // Option buttons panel
    
	private JButton jGeneralOptionsButton = null;
    private JButton jAdvancedOptionsButton = null;
    private JButton jCancelButton = null;

    // Control panel
    
    private JPanel jStatusCardPanel = null;
    private JProgressBar jProgressBar = null;
    
    // Status card panel
    
    private JPanel jStatusPanel = null;
    private JPanel jErrorPanel = null;
    
    // Status panel
    
    private JTextField jStatusTextField = null;
    private JTextField jNumValidProgramsField = null;
    private JTextField jNumFilesField = null;
    private JTextField jSubmissionSizeField = null;
    private JButton previewButton = null;
    private JButton submitButton = null;

    // Error panel
    
    private JTextArea jErrorArea = null;
    private JButton jOverwriteDirButton = null;

    /**
     * OptionPanel constructor for a new submission with the given title
     */
	public OptionPanel(String title, View view) {
		super(view);
		this.view = view;
		atujplag = view.getATUJPLAG();
		this.client = new SimpleClient(title, atujplag);
		this.client.setSubmissionDirectory(""); //$NON-NLS-1$
		initialize();
	}

    /**
     * OptionPanel constructor for an existing completed submission, which
     * is going to be changed by the user. The title will not be changeable
     */
    public OptionPanel(SimpleClient client, View view) {
        super(view);
        this.overwrite = true;
        this.client = client;
        this.view = view;
        atujplag = view.getATUJPLAG();
        initialize();
    }

    /**
     * Make dialog close on ESCAPE
     */
    protected JRootPane createRootPane() {
        KeyStroke stroke=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane=new JRootPane();
        rootPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                	jCancelButton.doClick();
                }
            }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        return rootPane;
    }
    
    /**
     * This method initializes this
     */
    private void initialize() {
        view.blockNewSubmissions();   // TODO: this shouldn't be done here!
        setResizable(false);
        setContentPane(getJContentPane());
        
        addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent arg0) {
                if (!OptionPanel.this.isSubmitted)
                    OptionPanel.this.jCancelButton.doClick();
            }

            public void windowActivated(WindowEvent arg0) {}
            public void windowClosed(WindowEvent arg0) {}
            public void windowDeactivated(WindowEvent arg0) {}
            public void windowDeiconified(WindowEvent arg0) {}
            public void windowIconified(WindowEvent arg0) {}
            public void windowOpened(WindowEvent arg0) {}
        });
        
        // TODO: Just make the dialog modal?? Would this work with treePreview?
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent arg0) {
                if (arg0.getWindow() == view)
                    requestFocus();
            }

            public void windowLostFocus(WindowEvent arg0) {
                if (arg0.getWindow() == OptionPanel.this) {
                    if (arg0.getOppositeWindow() != treePreview)
                        requestFocus();
                }
                if (arg0.getWindow() == treePreview)
                    requestFocus();
            }
        });
    }
    
    /**
     * @return Returns the jContentPane.
     */
    public JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = JPlagCreator.createPanelWithoutBorder(700, 402, 5, 0,
                    FlowLayout.CENTER);
            jContentPane.add(getJOptionPanel(), null);
            jContentPane.add(getJOptionButtonsPanel());
            jContentPane.add(getJControlPanel(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jOptionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJOptionPanel() {
        if (jOptionPanel == null) {
            jOptionPanel = new JPanel();
            jOptionPanel.setLayout(new CardLayout());
            jOptionPanel.setMinimumSize(new java.awt.Dimension(300, 200));
            jOptionPanel.add(getJGeneralOptionsPanel(), getJGeneralOptionsPanel().getName());
            jOptionPanel.add(getJAdvancedOptionsPanel(), getJAdvancedOptionsPanel().getName());
            jOptionPanel.setPreferredSize(new Dimension(700, 200));
        }
        return jOptionPanel;
    }

    private JPanel getJGeneralOptionsPanel() {
        if (jGeneralOptionsPanel == null) {
            jGeneralOptionsPanel = JPlagCreator.createPanel(
                    Messages.getString("OptionPanel.Basic_options_Panel"), //$NON-NLS-1$
                    700, 130, 10, 20, FlowLayout.LEFT,
                    JPlagCreator.WITH_TITLEBORDER);
            jGeneralOptionsPanel.setName("jGeneralOptionsPanel"); //$NON-NLS-1$

            jGeneralOptionsPanel.addComponentListener(new ComponentListener() {
                public void componentShown(ComponentEvent arg0) {
                    OptionPanel.this.setAdvPanelListenersActive(false);
                }

                public void componentHidden(ComponentEvent arg0) {
                    OptionPanel.this.setAdvPanelListenersActive(true);
                }

                public void componentMoved(ComponentEvent arg0) {}
                public void componentResized(ComponentEvent arg0) {}
            });

            jGeneralOptionsPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Language") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    280, 20), null);
            jLanguageCB = JPlagCreator.createJComboBox(
                    atujplag.getLanguageNames(), 350, 20,
                    Messages.getString(
                        "OptionPanel.Programming_languages_combobox_TIP")); //$NON-NLS-1$
            jGeneralOptionsPanel.add(jLanguageCB, null);

            jGeneralOptionsPanel.add(
                    JPlagCreator.createLabel(
                        Messages.getString("OptionPanel.Comparison_mode") + ":", 280, 20), //$NON-NLS-1$ //$NON-NLS-2$
                    null);
            jCompModeCB = JPlagCreator.createJComboBox(
                    ATUJPLAG.COMPARE_MODES, 350, 20,
                    Messages.getString(ATUJPLAG.COMPARE_MODE_TIPNAMES[0]));
            jCompModeCB.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    jCompModeCB.setToolTipText(Messages.getString(ATUJPLAG.COMPARE_MODE_TIPNAMES[jCompModeCB.getSelectedIndex()]));
                }
            });
            jGeneralOptionsPanel.add(jCompModeCB, null);

            jGeneralOptionsPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Submission_title") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    280, 20), null);
            jTitleField = JPlagCreator.createTextField(350, 20,
                    Messages.getString("OptionPanel.Submission_title_TIP")); //$NON-NLS-1$
            jGeneralOptionsPanel.add(jTitleField, null);

            jGeneralOptionsPanel.add(JPlagCreator.createLabel(
                    Messages.getString("OptionPanel.Submission_directory") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    280, 20), null);
            jSubmissionDirField = JPlagCreator.createTextField(310, 20,
                    Messages.getString("OptionPanel.Submission_directory_TIP")); //$NON-NLS-1$
            jGeneralOptionsPanel.add(jSubmissionDirField, null);

            jFileChooserButton = JPlagCreator.createOpenFileButton(
                    Messages.getString(
                        "OptionPanel.Submission_directory_file_browser_TIP")); //$NON-NLS-1$
            jGeneralOptionsPanel.add(jFileChooserButton, null);

            jGeneralOptionsPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Recurse_into_directories") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    280, 20), null);
            jGeneralOptionsPanel.add(getJRecurseDirCBBox(), null);
        }
        return jGeneralOptionsPanel;
    }
    
    private JCheckBox getJRecurseDirCBBox() {
        if (jRecurseDirCBBox == null) {
            jRecurseDirCBBox = JPlagCreator.createCheckBox(
                Messages.getString("OptionPanel.Recurse_into_directories_TIP")); //$NON-NLS-1$
        }
        return jRecurseDirCBBox;
    }

    /**
     * This method initializes jOptionButtonsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJOptionButtonsPanel() {
        if (jOptionButtonsPanel == null) {
            jOptionButtonsPanel = JPlagCreator.createPanel(null, 696, 25, 0, 50,
                    FlowLayout.CENTER, -1);
            jOptionButtonsPanel.add(getJGeneralOptionsButton(), null);
            jOptionButtonsPanel.add(getJAdvancedOptionsButton(), null);
            jOptionButtonsPanel.add(getJCancelButton(), null);
        }
        return jOptionButtonsPanel;
    }

    /**
     * This method initializes jGeneralOptionsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJGeneralOptionsButton() {
        if (jGeneralOptionsButton == null) {
            jGeneralOptionsButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Basic_options"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Basic_options_TIP"), //$NON-NLS-1$
                    180, 20);
            jGeneralOptionsButton.setEnabled(false);
            jGeneralOptionsButton.addActionListener(this);
        }
        return jGeneralOptionsButton;
    }
    
    /**
     * This method initializes jAdvancedOptionsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJAdvancedOptionsButton() {
        if (jAdvancedOptionsButton == null) {
            jAdvancedOptionsButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Advanced_options"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Advanced_options_TIP"), //$NON-NLS-1$
                    180, 20);
            jAdvancedOptionsButton.setEnabled(false);
            jAdvancedOptionsButton.addActionListener(this);
        }
        return jAdvancedOptionsButton;
    }
    
    /**
     * This method initializes jCancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJCancelButton() {
        if (jCancelButton == null) {
            jCancelButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Cancel"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Cancel_TIP"), //$NON-NLS-1$
                    180, 20);
            jCancelButton.addActionListener(this);
        }
        return jCancelButton;
    }

    /**
     * This method initializes jAdvancedOptionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJAdvancedOptionsPanel() {
        if (jAdvancedOptionsPanel == null) {
            jAdvancedOptionsPanel = JPlagCreator.createPanel(Messages.getString(
                    "OptionPanel.Advanced_options_Panel"), //$NON-NLS-1$
                    700, 250, 10, 5, FlowLayout.CENTER,
                    JPlagCreator.WITH_TITLEBORDER);
            jAdvancedOptionsPanel.setName("advancedOptions"); //$NON-NLS-1$

            jAdvancedOptionsPanel.add(getJSufAndBCPanel(), null);
            jAdvancedOptionsPanel.add(getJMmlAndClusterPanel(), null);

            jStoreCB = JPlagCreator.createJComboBox(new String[] {
                    Messages.getString(
                        "OptionPanel.Maximal_number_of_matches") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    Messages.getString(
                        "OptionPanel.Minimal_similarity_of_matches") + ":" }, //$NON-NLS-1$ //$NON-NLS-2$
                    500, 20, null);
            jStoreCB.setBackground(JPlagCreator.SYSTEMCOLOR);
            jAdvancedOptionsPanel.add(jStoreCB, null);
            jAdvancedOptionsPanel.add(getJStoreSpinner(), null);
            jAdvancedOptionsPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Path_to_files") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    500, 20), null);
            jAdvancedOptionsPanel.add(getJSubdirCB(), null);

            jAdvancedOptionsPanel.add(getJApplyDefaultsButton(), null);
        }
        return this.jAdvancedOptionsPanel;
    }

    /**
     * This method initializes jSufAndBCPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJSufAndBCPanel() {
        if (jSufAndBCPanel == null) {
            jSufAndBCPanel = JPlagCreator.createPanelWithoutBorder(330, 60, 10, 5,
                    FlowLayout.LEFT);
            jSufAndBCPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Suffixes") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    170, 20));
            jSufAndBCPanel.add(getJSuffixesField());
            jSufAndBCPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Basecode_directory") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    170, 20));
            jSufAndBCPanel.add(getJBasecodeCB());
        }
        return jSufAndBCPanel;
    }

    private JTextField getJSuffixesField() {
        if (jSuffixesField == null) {
            jSuffixesField = JPlagCreator.createTextField(150, 20,
                    Messages.getString("OptionPanel.Suffixes_TIP")); //$NON-NLS-1$
        }
        return jSuffixesField;
    }

    private JComboBox<String> getJBasecodeCB() {
        if (this.jBasecodeCB == null) {
            this.jBasecodeCB = JPlagCreator.createJComboBox(new String[] {
                    Messages.getString("OptionPanel.none") }, 150, 20, //$NON-NLS-1$
                    Messages.getString("OptionPanel.Basecode_directory_TIP")); //$NON-NLS-1$
        }
        return this.jBasecodeCB;
    }
    
    /**
     * This method initializes jMmlAndClusterPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJMmlAndClusterPanel() {
        if (jMmlAndClusterPanel == null) {
            jMmlAndClusterPanel = JPlagCreator.createPanelWithoutBorder(330, 60, 10, 5,
                    FlowLayout.RIGHT);
            jMmlAndClusterPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Minimum_match_length") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    200, 20), null);
            jMmlAndClusterPanel.add(getJMinMatchLenSpinner(), null);
            jMmlAndClusterPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Cluster_type") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    200, 20));
            jClusterTypeCB = JPlagCreator.createJComboBox(new String[] {
                    Messages.getString("OptionPanel.none"), //$NON-NLS-1$
                    "min", "avr", "max" }, 100, 20, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    Messages.getString("OptionPanel.Cluster_type_TIP")); //$NON-NLS-1$
            jMmlAndClusterPanel.add(jClusterTypeCB, null);
        }
        return jMmlAndClusterPanel;
    }

    private JSpinner getJMinMatchLenSpinner() {
        if (jMinMatchLenSpinner == null) {
            this.jMinMatchLenSpinner = JPlagCreator.createSpinner(100, 20,
                    Messages.getString("OptionPanel.Minimum_match_length_TIP")); //$NON-NLS-1$
        }
        return jMinMatchLenSpinner;
    }

    private JSpinner getJStoreSpinner() {
        if (jStoreSpinner == null) {
            jStoreSpinner = JPlagCreator.createSpinner(150, 20,
                Messages.getString(
                    "OptionPanel.Maximal_number_or_minimal_similarity_of_matches_to_be_stored_TIP")); //$NON-NLS-1$
        }
        return this.jStoreSpinner;
    }

    private JComboBox<String> getJSubdirCB() {
        if (jSubdirCB == null) {
            jSubdirCB = JPlagCreator.createJComboBox(new String[] {
                    Messages.getString("OptionPanel.none") }, 150, 20, //$NON-NLS-1$
                    Messages.getString("OptionPanel.Path_to_files_TIP")); //$NON-NLS-1$
        }
        return jSubdirCB;
    }
    
    /**
     * This method initializes jApplyDefaultsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJApplyDefaultsButton() {
        if (jApplyDefaultsButton == null) {
            jApplyDefaultsButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Default"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Default_TIP"), 180, 20); //$NON-NLS-1$
            jApplyDefaultsButton.addActionListener(this);
        }
        return jApplyDefaultsButton;
    }
    
    /**
     * This method initializes jControlPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJControlPanel() {
        if (jControlPanel == null) {
            jControlPanel = JPlagCreator.createPanel(696, 160, 5, 0,
                    FlowLayout.CENTER);
            jControlPanel.add(getJStatusCardPanel(), null);
            jControlPanel.add(getJProgressBar(), null);
        }
        return jControlPanel;
    }

    /**
     * This method initializes jStatusCardPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJStatusCardPanel() {
        if (jStatusCardPanel == null) {
            jStatusCardPanel = new JPanel();
            jStatusCardPanel.setLayout(new CardLayout());
            jStatusCardPanel.add(getJStatusPanel(), getJStatusPanel().getName());
            jStatusCardPanel.add(getJErrorPanel(), getJErrorPanel().getName());
        }
        return jStatusCardPanel;
    }
    
    /**
     * This method initializes jStatusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJStatusPanel() {
        if (jStatusPanel == null) {
            jStatusPanel = JPlagCreator.createPanel(null, 690, 140, 7, 20,
                    FlowLayout.CENTER, -1);
            jStatusPanel.setName("ScanStatus"); //$NON-NLS-1$
            jStatusPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Status") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    330, 20), null);
            jStatusPanel.add(getStatusTextField(), null);
            jStatusPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Number_of_programs") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    330, 20), null);
            jStatusPanel.add(getJNumValidProgramsField(), null);
            jStatusPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Total_number_of_files") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    330, 20), null);
            jStatusPanel.add(getJNumFilesField(), null);
            jStatusPanel.add(JPlagCreator.createLabel(Messages.getString(
                    "OptionPanel.Total_size_of_submission") + ":", //$NON-NLS-1$ //$NON-NLS-2$
                    330, 20), null);
            jStatusPanel.add(getJSubmissionSizeField(), null);
            jStatusPanel.add(getPreviewButton(), null);
            jStatusPanel.add(getSubmitButton(), null);
        }
        return jStatusPanel;
    }

    /**
     * This method initializes jStatusTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStatusTextField() {
        if (jStatusTextField == null) {
            jStatusTextField = JPlagCreator.createTextField(300, 20,
                    Messages.getString("OptionPanel.Status_TIP")); //$NON-NLS-1$
            jStatusTextField.setEditable(false);
            jStatusTextField.setBackground(Color.WHITE);
        }
        return jStatusTextField;
    }

    /**
     * This method initializes jNumValidProgramsField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJNumValidProgramsField() {
        if (jNumValidProgramsField == null) {
            jNumValidProgramsField = JPlagCreator.createTextField(300, 20,
                    Messages.getString("OptionPanel.Number_of_programs_TIP")); //$NON-NLS-1$
            jNumValidProgramsField.setEditable(false);
            jNumValidProgramsField.setBackground(Color.WHITE);
        }
        return jNumValidProgramsField;
    }
    
    /**
     * This method initializes jNumFilesField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJNumFilesField() {
        if (jNumFilesField == null) {
            jNumFilesField = JPlagCreator.createTextField(300, 20,
                    Messages.getString(
                        "OptionPanel.Total_number_of_files_TIP")); //$NON-NLS-1$
            jNumFilesField.setEditable(false);
            jNumFilesField.setBackground(Color.WHITE);
        }
        return jNumFilesField;
    }

    /**
     * This method initializes jSubmissionSizeField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJSubmissionSizeField() {
        if (jSubmissionSizeField == null) {
            jSubmissionSizeField = JPlagCreator.createTextField(
                    300, 20, Messages.getString(
                        "OptionPanel.Total_size_of_submission_TIP")); //$NON-NLS-1$
            jSubmissionSizeField.setEditable(false);
            jSubmissionSizeField.setBackground(Color.WHITE);
        }
        return jSubmissionSizeField;
    }
    
    /**
     * This method initializes previewButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPreviewButton() {
        if (previewButton == null) {
            previewButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Files_preview"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Files_preview_TIP"), //$NON-NLS-1$
                    180, 20);

            previewButton.setEnabled(false);
            previewButton.addActionListener(this);
        }
        return previewButton;
    }

    /**
     * This method initializes submitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSubmitButton() {
        if (submitButton == null) {
            submitButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Submit"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Submit_TIP"), 180, 20); //$NON-NLS-1$
            submitButton.setEnabled(false);
            submitButton.addActionListener(this);
        }
        return submitButton;
    }

    /**
     * This method initializes jErrorPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJErrorPanel() {
        if (jErrorPanel == null) {
            jErrorPanel = JPlagCreator.createPanel(null, 690, 140, 7, 20,
                    FlowLayout.CENTER, -1);
            jErrorPanel.setName("status"); //$NON-NLS-1$
            jErrorPanel.add(getJErrorArea(), null);
            jErrorPanel.add(getJOverwriteDirButton(), null);
        }
        return jErrorPanel;
    }

    /**
     * This method initializes jErrorArea
     * 
     * @return javax.swing.JEditorPane
     */
    private JTextArea getJErrorArea() {
        if (jErrorArea == null) {
            jErrorArea = new JTextArea();
            jErrorArea.setPreferredSize(new java.awt.Dimension(656, 95));
            jErrorArea.setLineWrap(true);
            jErrorArea.setWrapStyleWord(true);
            jErrorArea.setEditable(false);
            jErrorArea.setBackground(JPlagCreator.SYSTEMCOLOR);
            jErrorArea.setFont(JPlagCreator.SYSTEM_FONT);
        }
        return jErrorArea;
    }

    private boolean overwrite = false;

    /**
     * This method initializes jOverwriteDirButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJOverwriteDirButton() {
        if (jOverwriteDirButton == null) {
            jOverwriteDirButton = JPlagCreator.createButton(
                    Messages.getString("OptionPanel.Overwrite"), //$NON-NLS-1$
                    Messages.getString("OptionPanel.Overwrite_TIP"), 150, 20); //$NON-NLS-1$
            jOverwriteDirButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    overwrite = true;
                    OptionPanel.this.validateTitleTimer.restart();
                }
            });
        }
        return jOverwriteDirButton;
    }

    /**
     * This method initializes jProgressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getJProgressBar() {
        if (jProgressBar == null) {
            jProgressBar = new JProgressBar();
            jProgressBar.setPreferredSize(new java.awt.Dimension(590, 7));
            jProgressBar.setBackground(JPlagCreator.SYSTEMCOLOR);
            jProgressBar.setForeground(java.awt.Color.black);
            jProgressBar.setIndeterminate(false);
            jProgressBar.setMaximum(100);
        }
        return jProgressBar;
    }
    
    private void selectLanguageCB(String lang) {
        for(int i=0; i<jLanguageCB.getItemCount(); i++) {
            if(jLanguageCB.getItemAt(i).toString().equals(lang)) {
                jLanguageCB.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public void initOptions(boolean isChangingOptions) {
        Option options = client.getOptions();

        jTitleField.setText(client.getClientName());

        if (isChangingOptions) {
            setTitle(Messages.getString("OptionPanel.JPlag_change_options")); //$NON-NLS-1$
            jTitleField.setEditable(false);
            jTitleField.setBackground(JPlagCreator.SYSTEMCOLOR);
            selectLanguageCB(options.getLanguage());
            
            Integer compmode = options.getComparisonMode(); 
            if(compmode != null && compmode >= 0 && compmode < ATUJPLAG.COMPARE_MODES.length)
                jCompModeCB.setSelectedIndex(compmode);

            jSubmissionDirField.setText(client.getSubmissionDirectory());
            changeOptions(new LanguageSetting(options));

            doGenerateAdvancedOptions();

            firstScan = true;
            oldBasecodeDir = options.getBasecodeDir();
            if(oldBasecodeDir == null) oldBasecodeDir = ""; //$NON-NLS-1$
            
            String path = options.getPathToFiles();
            if (path != null && path.length()!=0) {
                for (int i = 0; i < getJSubdirCB().getItemCount(); i++) {
                    if (path.equals(getJSubdirCB().getItemAt(i))) {
                        getJSubdirCB().setSelectedIndex(i);
                        break;
                    }
                }
            }
		}
        else {
            setTitle(Messages.getString("OptionPanel.JPlag_new_submission")); //$NON-NLS-1$
            selectLanguageCB(atujplag.getLastLanguageName());
            changeOptions(atujplag.getLastLanguageSetting());
            options.setTitle(client.getClientName());
        }
        
        validateTitleTimer = new Timer(SCAN_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(OptionPanel.this.checkOptions() && previewSubs == null) {
                    // options were invalid before -> update preview
                    applyOptions();    
                }
            }
        });
        validateTitleTimer.setRepeats(false);

        updatePreviewTimer = new Timer(SCAN_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                applyOptions();
            }
        });
        updatePreviewTimer.setRepeats(false);
        
        createListeners();

        jTitleField.getDocument().addDocumentListener(titleListener);
        jLanguageCB.addItemListener(languageItemListener);
        jRecurseDirCBBox.addItemListener(recurseDirCBListener);
		jSubmissionDirField.getDocument().addDocumentListener(
            new DocumentListener() {
                public void changedUpdate(DocumentEvent arg0) {
                    invalidatePreview();
                }

                public void insertUpdate(DocumentEvent arg0) {
                    changedUpdate(arg0);
                }

                public void removeUpdate(DocumentEvent arg0) {
                    changedUpdate(arg0);
                }
            });
		jFileChooserButton.addActionListener(this);

        invalidatePreview();
	}

    private void createListeners() {
        languageItemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED) {
                    getJRecurseDirCBBox().removeItemListener(recurseDirCBListener);
                    changeOptions(atujplag.getLanguageSettingForName(
                            jLanguageCB.getSelectedItem().toString()));
                    getJRecurseDirCBBox().addItemListener(recurseDirCBListener);
                    OptionPanel.this.changedUpdate(null);
                }
            }
        };
        
        titleListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent arg0) {
                overwrite = false;
                OptionPanel.this.validateTitleTimer.restart();
            }

            public void insertUpdate(DocumentEvent arg0) {
                this.changedUpdate(arg0);
            }

            public void removeUpdate(DocumentEvent arg0) {
                this.changedUpdate(arg0);
            }
        };
        
        subdirCBListener = new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
            	if(!updatingSubDirCB && arg0.getStateChange() == ItemEvent.SELECTED)
                    invalidatePreview();
            }
        };
        recurseDirCBListener = new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                invalidatePreview();
            }
        };
        suffixesListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent arg0) {
                advOptionsUpToDate = false;
                invalidatePreview();
            }

            public void insertUpdate(DocumentEvent arg0) {
                this.changedUpdate(arg0);
            }

            public void removeUpdate(DocumentEvent arg0) {
                this.changedUpdate(arg0);
            }
        };
    }

    public void changedUpdate(DocumentEvent arg0) {
        System.out.println("changedUpdate");
        if(!SwingUtilities.isEventDispatchThread()) {
            System.out.println("OptionPanel.changedUpdate called from a thread"
                    + " other than the event dispatching thread!!!"); // TODO: hmm...
        }

        invalidatePreview();
    }

    public void insertUpdate(DocumentEvent arg0) {
        changedUpdate(arg0);
    }

    public void removeUpdate(DocumentEvent arg0) {
        changedUpdate(arg0);
    }
    
    private void changeOptions(LanguageSetting lang) {
        int setCTyp = 0;
        String clusterType = lang.getClusterType();

        if (clusterType.equals("min")) //$NON-NLS-1$
            setCTyp = 1;
        else if (clusterType.equals("avr")) //$NON-NLS-1$
            setCTyp = 2;
        else if (clusterType.equals("max")) //$NON-NLS-1$
            setCTyp = 3;

        getJMinMatchLenSpinner().setValue(new Integer(lang.getMinMatchLen()));

		String storestr = lang.getStoreMatches();
		if (storestr.endsWith("%")) {
			storestr = storestr.substring(0, storestr.length() - 1);
			jStoreCB.setSelectedIndex(1);
		} else {
			jStoreCB.setSelectedIndex(0);
		}
		Integer storestr_integer;
		if (storestr.length() > 0) {
			storestr_integer = new Integer(storestr);
		} else {
			storestr_integer = new Integer(0);
		}
		getJStoreSpinner().setValue(storestr_integer);
		getJRecurseDirCBBox().setSelected(lang.isReadSubdirs());
		getJSuffixesField().setText(lang.getSuffixes());
		getJSuffixesField().setCaretPosition(0);
		jClusterTypeCB.setSelectedIndex(setCTyp);
    }
    
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();

		if (source == getSubmitButton()) {
			submit();
			System.gc();
			dispose();
		}

		else if (source == this.getPreviewButton()) {
			if (treePreview != null) {
                treePreview.dispose();
                treePreview = null;
            }
            else treePreview = new SubmissionTree(this);
		}

		else if (source == this.jFileChooserButton) {
            String subloc = jSubmissionDirField.getText();
            if (subloc.length() == 0)
                subloc = atujplag.getLastSubmissionLocation();
			String file = new File(subloc).getParent();
			JFileChooser chooser;
			if (file == null || !(new File(file)).exists())
				chooser = new JFileChooser();
			else
				chooser = new JFileChooser(new File(file));
            
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

/*			chooser.setBackground(JPlagCreator.SYSTEMCOLOR);

			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File arg0) {
                    return true; //arg0.isDirectory();
                }

                public String getDescription() {
                    return Messages.getString(
                        "OptionPanel.Directories");
                }
            });
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setSelectedFile(new File(subloc));*/
			chooser.setDialogTitle(Messages.getString(
                    "OptionPanel.Select_the_submission_directory")); //$NON-NLS-1$
			int retval = chooser.showOpenDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				jSubmissionDirField.setText(
                        chooser.getSelectedFile().getPath());
			}
            requestFocus();
		}

		else if (source == getJCancelButton()) {
			view.unblockNewSubmissions();
			if (treePreview != null) {
				treePreview.dispose();
                treePreview = null;
            }
			dispose();
		}

		else if (source == getJGeneralOptionsButton()) {
			CardLayout layout = (CardLayout) getJOptionPanel().getLayout();
			layout.previous(getJOptionPanel());

			getJGeneralOptionsButton().setEnabled(false);
			getJAdvancedOptionsButton().setEnabled(true);
		}

		else if (source == getJAdvancedOptionsButton()) {
			// Collecting options
/*			client.getOptions().setLanguage(
					jLanguageCB.getSelectedItem().toString());
			client.getOptions().setReadSubdirs(
					getJRecurseDirCBBox().isSelected());*/
            
			getJGeneralOptionsButton().setEnabled(true);
			getJAdvancedOptionsButton().setEnabled(false);
            
			CardLayout layout = (CardLayout) this.getJOptionPanel().getLayout();
			generateAdvancedOptions();
			layout.next(getJOptionPanel());
		}

		else if (source == getJApplyDefaultsButton()) {
			changeOptions(atujplag.getDefaultLanguageSettingForName(
					jLanguageCB.getSelectedItem().toString()));
		}
	}
	
	private void submit() {
		if (treePreview != null)
			treePreview.dispose();
		Option option = getClient().getOptions();
		client.getOptions().setLanguage(
				jLanguageCB.getSelectedItem().toString());
		client.setClientName(jTitleField.getText());
		
		option.setComparisonMode(jCompModeCB.getSelectedIndex());

		// Cluster type
		String x = jClusterTypeCB.getSelectedItem().toString();
		if (x.equals(Messages.getString("OptionPanel.none"))) //$NON-NLS-1$
			x = ""; //$NON-NLS-1$
		option.setClustertype(x);

		// Basecode
		String bc = getJBasecodeCB().getSelectedItem().toString();
		if (bc.equals(Messages.getString("OptionPanel.none"))) //$NON-NLS-1$
			bc = ""; //$NON-NLS-1$
		option.setBasecodeDir(bc);
		
		// Minimum match length
		int value = ((Integer) getJMinMatchLenSpinner().getModel().getValue())
				.intValue();
		if (value >= 2 && value <= 100)
			option.setMinimumMatchLength(value);

		// Storage
		int value2 = ((Integer) getJStoreSpinner().getModel().getValue())
				.intValue();
		String typ1 = (jStoreCB.getSelectedIndex() == 0) ? "" //$NON-NLS-1$
				: "%"; //$NON-NLS-1$
		if (value >= 2 && value2 <= 100)
			option.setStoreMatches(value2 + typ1);

		option.setCountryLang(atujplag.getCountryLanguageValue());
		client.setSubmissions(previewSubs);
		option.setReadSubdirs(getJRecurseDirCBBox().isSelected());
		client.getOptions().setOriginalDir(
				client.getSubmissionDirectory());

		atujplag.updateLastSubmissionInfos(option);

		isSubmitted = true;
		if (overwrite) {
			ATUJPLAG.delete(new File(atujplag.getResultLocation(),
					jTitleField.getText()));
			view.updateTable(null);
		}
		InfoPanel inf = new InfoPanel(client, view);
        view.startInfoPanel(inf);
	}

	private void applyOptions() {
		getClient().setSubmissionDirectory(jSubmissionDirField.getText());

		// Suffixes
		getClient().getOptions().setSuffixes(parseCommaString(
            getJSuffixesField().getText()));

		// Recurse
		getClient().getOptions().setReadSubdirs(getJRecurseDirCBBox().isSelected());

		// Path to files
		String subdir = getJSubdirCB().getSelectedItem().toString();

		if (!subdir.equals(Messages.getString("OptionPanel.none"))) { //$NON-NLS-1$
			getClient().getOptions().setPathToFiles(subdir);
		} else
			getClient().getOptions().setPathToFiles(""); //$NON-NLS-1$
		
		if(!firstScan)
			oldBasecodeDir = getJBasecodeCB().getSelectedItem().toString();
		else
			firstScan = false;
		
		previewSubs = null;
		updateBasecodeCombo();
		updateSubdirCombo();
		
		updateScanThread();
	}

    private void requestPreviewUpdate() {
        this.updatePreviewTimer.restart();
    }
    
    /**
     * Invalidates status infos and requests preview update
     */ 
    private void invalidatePreview() {
        updatePreviewTimer.stop();
        if(scanThread != null) {
            scanThread.interrupt();
            try {
                scanThread.join(200);
            } catch(InterruptedException e) {}
            scanThread = null;
        }
        
        getSubmitButton().setEnabled(false);
        getPreviewButton().setEnabled(false);

        getJNumValidProgramsField().setText(""); //$NON-NLS-1$
        getJNumFilesField().setText(""); //$NON-NLS-1$
        getJSubmissionSizeField().setText(""); //$NON-NLS-1$
        getJProgressBar().setValue(0);
        
        if(treePreview != null) {
            treePreview.dispose();
            treePreview = null;
            reopenPreview = true;
        }
        else reopenPreview = false;
        
        previewSubs = null;

        if(!checkOptions()) {     // invalid options?
            return;
        }
        getStatusTextField().setText(Messages.getString("OptionPanel.OK")); //$NON-NLS-1$
        requestPreviewUpdate();
    }
    
	private void updateBasecodeCombo() {
		int selindex = 0, numadded = 1;
		JComboBox<String> cb = getJBasecodeCB();
		cb.removeAllItems();
		cb.addItem(Messages.getString("OptionPanel.none")); //$NON-NLS-1$
		
		if(previewSubs == null) return;
		for(int i = 0; i < previewSubs.size(); i++) {
			SubmissionManager sub = previewSubs.get(i);
			if(sub.isValid() && sub.isDirectory()) {
				cb.addItem(sub.name);
				if(sub.name.equals(oldBasecodeDir)) selindex = numadded;
				numadded++;
			}
		}
		cb.setSelectedIndex(selindex);
	}
    
	/**
	 * Updates the subdirectory combo box.
	 * The data depends on the following options:
	 *  - client.getSubmissionDirectory()
	 *  - client.getOptions().getSuffixes()
	 *  - client.getOptions().isReadSubdirs() (recurse)
	 */
    private void updateSubdirCombo() {
    	updatingSubDirCB = true;
    	
    	JComboBox<String> cb = getJSubdirCB();
    	String selItem = (String) cb.getSelectedItem();       // will be null, if none selected
        cb.removeAllItems();
        cb.addItem(Messages.getString("OptionPanel.none")); //$NON-NLS-1$
        
		File[] files = (new File(client.getSubmissionDirectory())).listFiles();
		if(files == null) {
			System.out.println("No file found");
			System.out.println("Directory: " + jSubmissionDirField.getText());
		}
		genAOSuffixes = getSuffixes();
		if(genAOSuffixes != null) {
			int addedItems = 1, selIndex = 0;
			for(int i = 0; i < files.length; i++) {
				if(files[i].isDirectory()) {
					File[] subfiles = files[i].listFiles();
subfilesloop:		for(int j = 0; j < subfiles.length; j++) {
						if(subfiles[j].isDirectory()) {
		                    // Check whether this directory is already in subdircb
							String dirname = subfiles[j].getName();
		                    
							for(int k = 0; k < cb.getItemCount(); k++) {
								if(dirname.equals(cb.getItemAt(k).toString()))
									continue subfilesloop;		// already in combo box
							}

							if(hasDirValidFiles(subfiles[j])) {
								cb.addItem(dirname);
								if(dirname.equals(selItem)) selIndex = addedItems;
								addedItems++;
							}
						}
					}
				}
			}
			cb.setSelectedIndex(selIndex);
		}
		
		updatingSubDirCB = false;
    }

	String[] genAOSuffixes;
	
	private boolean hasDirValidFiles(File aktDir) {
		String[] newFiles = aktDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (!new File(dir, name).isFile())
					return false;
				for (int i = 0; i < genAOSuffixes.length; i++)
					if (name.endsWith(genAOSuffixes[i]))
						return true;
				return false;
			}
		});
		if(newFiles.length != 0) return true;
		if(client.getOptions().isReadSubdirs()) {
			String[] dirs = aktDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (new File(dir, name).isDirectory()); // all sub-dirs
				}
			});
			if (dirs != null) {
				for (int k = 0; k < dirs.length; k++) {
					if(hasDirValidFiles(new File(aktDir, dirs[k])))
							return true;
				}
			}
		}
		return false;
	}
	
	private void doGenerateAdvancedOptions() {
		if(advOptionsUpToDate) return;

		this.client.setSubmissionDirectory(jSubmissionDirField.getText());
		oldBasecodeDir = "";

		updateBasecodeCombo();

        updateSubdirCombo();
		
        advOptionsUpToDate = true;
	}

	private void generateAdvancedOptions() {
		this.getJProgressBar().setIndeterminate(true);
		doGenerateAdvancedOptions();
		this.getJProgressBar().setIndeterminate(false);
	}

	public SimpleClient getClient() {
		return client;
	}
    
	public Vector<SubmissionManager> getSubmissions() {
		return previewSubs;
	}
    
    private String[] parseCommaString(String str) {
        String tmp;
        if (str.length() == 0)
            return null;
        Vector<String> strs = new Vector<String>();
        try {
            StringTokenizer st = new StringTokenizer(str, ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                tmp = st.nextToken().trim();
                if (tmp.length() == 0)
                    continue;
                strs.addElement(tmp);
            }
        } catch (NoSuchElementException e) { // This should never happen...
            return null;
        }
        String[] strlist = new String[strs.size()];
        strs.copyInto(strlist);
        return strlist;
    }

	private String[] getSuffixes() {
        return client.getOptions().getSuffixes();
	}

    /**
     * @return true and shows the status panel, if options are valid.
     *         false and shows the general options panel and error panel with
     *         an error message, otherwise
     */    
	private boolean checkOptions() {
		CardLayout layout = (CardLayout) getJStatusCardPanel().getLayout();

        int error = validateOptions();
		if (error == NOERROR) {
			layout.show(getJStatusCardPanel(), getJStatusPanel().getName());
            
            // Activate advanced options button, if general options were not
            // valid before
            
            if(!getJGeneralOptionsButton().isEnabled()
                    && !getJAdvancedOptionsButton().isEnabled())
                getJAdvancedOptionsButton().setEnabled(true);
            
            return true;
		} else {
			// Do not overwrite if change options called //TODO was will dieser kommentar sagen??
            
            // General options are invalid -> show error and general options
            // panel and disable option buttons
            
			layout.show(getJStatusCardPanel(), getJErrorPanel().getName());
            
            layout = (CardLayout) getJOptionPanel().getLayout();
            layout.show(getJOptionPanel(), (error == GENOPTERROR)
                    ? getJGeneralOptionsPanel().getName()
                    : getJAdvancedOptionsPanel().getName());
            
            if(error == GENOPTERROR) {
                getJGeneralOptionsButton().setEnabled(false);
                getJAdvancedOptionsButton().setEnabled(false);
            }
            
            return false;
		}
	}

    /**
     * @return true, if the options appear to be valid
     * TODO: shouldn't "validate"Options only "validate" the options and not
     * apply them as "apply"Options should do??
     */
    private int validateOptions() {
        // check title
        String title = this.jTitleField.getText();
        if (title.length() == 0) {
            getJErrorArea().setText(Messages.getString("OptionPanel.Title_missing")); //$NON-NLS-1$
            this.getJOverwriteDirButton().setVisible(false);
            return GENOPTERROR;
        }
//        if(title.matches(".*[\\\\/:\\*\\?\"%&<>\\|].*|\\s.*|.*\\s")) {
        if(!title.matches("[\\w\\.,!-][\\w\\s\\.,!-]*[\\w\\.,!-]")) {
            getJErrorArea().setText(Messages.getString("OptionPanel.Illegal_title")); //$NON-NLS-1$
            this.getJOverwriteDirButton().setVisible(false);
            return GENOPTERROR;
        }

        File f = new File(atujplag.getResultLocation());
        File f1 = null;
        if (f.exists()) {
            if (!f.isDirectory()) {
                getJErrorArea().setText(TagParser.parse(Messages.getString(
                        "OptionPanel.Report_directory_is_not_a_directory_{1_PATH}"), //$NON-NLS-1$
                        new String[] { f.getPath() }));
                this.getJOverwriteDirButton().setVisible(false);
                return GENOPTERROR;
            } else if ((f1 = new File(f, title)).exists()) {
                if (!this.overwrite) {
                    getJErrorArea().setText(TagParser.parse(Messages.getString(
                            "OptionPanel.Title_directory_already_exists_in_Ask_overwrite_{1_TITLE}_{2_PATH}"), //$NON-NLS-1$
                            new String[] { f1.getName(), f.getPath() }));
                    this.getJOverwriteDirButton().setVisible(true);
                    return GENOPTERROR;
                }
            }
        } else {
            getJErrorArea().setText(TagParser.parse(Messages.getString(
                    "OptionPanel.Report_directory_does_not_exist_{1_PATH}"), //$NON-NLS-1$
                    new String[] { f.getPath() }));
            this.getJOverwriteDirButton().setVisible(false);
            return GENOPTERROR;
        }

        // check submission directory
        String t = jSubmissionDirField.getText();
        if (t.length() == 0) {
            getJErrorArea().setText(Messages.getString(
                    "OptionPanel.Submission_directory_No_directory_given")); //$NON-NLS-1$
            this.getJOverwriteDirButton().setVisible(false);
            previewSubs = null;
            return GENOPTERROR;
        }
        f = new File(t);
        if (f.exists()) {
            if (!f.isDirectory()) {
                getJErrorArea().setText(TagParser.parse(Messages.getString(
                        "OptionPanel.Submission_directory_is_not_a_directory_{1_PATH}"), //$NON-NLS-1$
                        new String[] { f.getName() }));
                this.getJOverwriteDirButton().setVisible(false);
                previewSubs = null;
                return GENOPTERROR;
            }
        } else {
            getJErrorArea().setText(TagParser.parse(Messages.getString(
                    "OptionPanel.Submission_directory_does_not_exist_{1_PATH}"), //$NON-NLS-1$
                    new String[] { f.getName() }));
            this.getJOverwriteDirButton().setVisible(false);
            previewSubs = null;
            return GENOPTERROR;
        }

        this.client.getOptions().setLanguage(
                this.jLanguageCB.getSelectedItem().toString());
        
        // check suffixes
        if(getJSuffixesField().getText().length()==0) {
            getJErrorArea().setText(Messages.getString(
                    "OptionPanel.Illegal_suffixes")); //$NON-NLS-1$
            this.getJOverwriteDirButton().setVisible(false);
            return ADVOPTERROR;
        }
        getClient().getOptions().setSuffixes(parseCommaString(
            getJSuffixesField().getText()));
        
        return NOERROR;
    }

    protected void previewClosed() {
		treePreview = null;
	}

	private void setAdvPanelListenersActive(boolean bol) {
		this.getJProgressBar().setIndeterminate(false);
		if (bol) {
			this.getJSubdirCB().addItemListener(
					this.subdirCBListener);
			this.getJSuffixesField().getDocument().addDocumentListener(
					this.suffixesListener);
			this.getJRecurseDirCBBox().addItemListener(this.recurseDirCBListener);
		} else {
			this.getJRecurseDirCBBox().removeItemListener(this.recurseDirCBListener);
			this.getJSubdirCB().removeItemListener(
					this.subdirCBListener);
			this.getJSuffixesField().getDocument().removeDocumentListener(
					this.suffixesListener);
		}
	}

	private synchronized void setStatus(String message) {
		this.getJErrorArea().setText(message);
	}

	private synchronized void setStatus(String[] result) {
		this.getStatusTextField().setText(result[0]);

		this.getJNumValidProgramsField().setText(result[4]);
		this.getJNumFilesField().setText(result[2]);
		this.getJSubmissionSizeField().setText(result[3]);
	}

	protected void updateScanThread() {
		if (this.scanThread != null) {
			scanThread.interrupt();
            try {
                scanThread.join(200);
            } catch(InterruptedException e) {}
        }
		this.scanThread = new ScanThread(this);
		System.gc();
		this.getSubmitButton().setEnabled(false);
		this.getPreviewButton().setEnabled(false);

		getStatusTextField().setText(
				Messages.getString("OptionPanel.Scanning_directories")); //$NON-NLS-1$
        this.getJNumValidProgramsField().setText("");
        this.getJNumFilesField().setText("");
        this.getJSubmissionSizeField().setText("");
		this.scanThread.start();
	}

    private synchronized Vector<SubmissionManager> scanFiles()
            throws InterruptedException
    {
        this.getJProgressBar().setIndeterminate(false);
        Vector<SubmissionManager> res = new Vector<SubmissionManager>();

        String subDir = this.client.getOptions().getPathToFiles();
        if (subDir != null && subDir.length()==0) subDir = null;

        File f = new File(this.client.getSubmissionDirectory());
        if (f == null || !f.isDirectory()) {
            this.setStatus(Messages.getString(
                    "OptionPanel.Unable_to_retrieve_directory") //$NON-NLS-1$
                    + ": " + client.getSubmissionDirectory()); //$NON-NLS-1$
            return null;
        }
        String[] list = null;
        try {
            list = f.list();
        } catch (SecurityException e) {
            setStatus(Messages.getString(
                    "OptionPanel.Unable_to_access_directory") //$NON-NLS-1$
                    + ": " + f.getPath()); //$NON-NLS-1$
            return null;
        }

        String[] suffixes = getSuffixes();
        if(suffixes == null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    getStatusTextField().setText(Messages.getString(
                        "OptionPanel.Illegal_suffixes")); //$NON-NLS-1$
                    doGenerateAdvancedOptions();
                } });
            return null;
        }
        boolean recurse = client.getOptions().isReadSubdirs();
        for (int i = 0; i < list.length; i++) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
            
            File subm_dir = new File(f, list[i]);
            getJProgressBar().setValue((i + 1) * 100 / list.length);
            getStatusTextField().setText(TagParser.parse(
                    Messages.getString("OptionPanel.Scanning_directory_{1_DIR}"), //$NON-NLS-1$
                    new String[] { list[i] }));
            
            if (!subm_dir.isDirectory()) {
                boolean ok = false;
                String name = subm_dir.getName();
                for (int j = 0; j < suffixes.length; j++)
                    if (name.endsWith(suffixes[j])) {
                        ok = true;
                        break;
                    }
                if (ok) {
                    if(subDir != null)
                        res.addElement(new SubmissionManager(name,
                                SubmissionManager.FILENOTINSUBDIR));
                    else
                        res.addElement(new SubmissionManager(name, f));
                }
            }
            else {
                File file_dir = (subDir == null) ? subm_dir : new File(subm_dir,
                        subDir);

                if (file_dir.isDirectory()) {
                    res.addElement(new SubmissionManager(subm_dir.getName(),
                            file_dir, recurse, suffixes));
                }
                else if(subDir != null)
                    res.addElement(new SubmissionManager(subm_dir.getName(),
                            SubmissionManager.DIRHASNOTSUBDIR));
            }
        } // end for

        return res;
    }
    
    private void scanThreadEnds(Vector<SubmissionManager> subs, String[] result) {
        int t = Integer.parseInt(result[4]);
        if (subs != null) {
            this.previewSubs = subs;
            getPreviewButton().setEnabled(true);
            if(t>=2 && result[0] == null) {
                result[0] = Messages.getString("OptionPanel.OK"); //$NON-NLS-1$
                getSubmitButton().setEnabled(true);
            }
            updateBasecodeCombo();
        } else {
            getPreviewButton().setEnabled(false);
            getSubmitButton().setEnabled(false);
        }
        setStatus(result);
        if (scanThread == Thread.currentThread()) {
            scanThread = null;
            if (reopenPreview) {      // was treePreview open?
                treePreview = new SubmissionTree(this);
            }
        }
        else {
            System.out.println("scanThread != currentThread !?!?!"); // TODO: hmm...
        }
    }
    
	class ScanThread extends Thread {
		private static final long FILE_LIMIT = 10000;
		private static final long SIZE_LIMIT = 100 * 1024 * 1024;

		private OptionPanel gui;

		public ScanThread(OptionPanel gui) {
			super();
			this.gui = gui;
		}

		/*
		 * returns in element: 0: error message or null 1: # of files 2: size of
		 * submission
		 */
		public String[] checkSubmissions(Vector<SubmissionManager> subs) {
			String[] result = new String[6];
			int n = subs.size();
			// test size and number of files
			long size = 0;
			int anz = 0;
			int valid = 0;
			result[0] = result[2] = result[3] = result[4] = null;
			result[1] = String.valueOf(subs.size());
			for (int i = 0; i < n; i++) {
				SubmissionManager s = subs.elementAt(i);
				if (s.isValid())
					valid++;
				for (int j = (s.files.length - 1); j >= 0; j--) {
					File f = new File(s.dir, s.files[j]);
					size += f.length();
					anz++;
				}
			}
			result[2] = TagParser.parse(Messages
					.getString("OptionPanel.Files_{1_NUMBER}"), //$NON-NLS-1$
					new String[] { anz + "" }); //$NON-NLS-2$
			result[3] = (size / 1024) + " KB"; //$NON-NLS-1$
			if (anz == 0 || size == 0) {
				result[0] = Messages.getString("OptionPanel.Not_enough_files"); //$NON-NLS-1$
			} else if (anz > FILE_LIMIT) {
				result[0] = TagParser.parse(Messages.getString(
						"OptionPanel.Too_many_files_Current_file_limit_is_{1_MAXFILES}"), //$NON-NLS-1$
					new String[] { FILE_LIMIT + "" }); //$NON-NLS-1$
			} else if (size > SIZE_LIMIT) {
				result[0] = TagParser.parse(Messages.getString(
						"OptionPanel.The_submission_is_too_large_Current_size_limit_is_{1_KBSIZE}"), //$NON-NLS-1$
					new String[] { (SIZE_LIMIT / 1024) + "" }); //$NON-NLS-1$
			} else if (valid < 2)
				result[0] = Messages.getString(
						"OptionPanel.Not_enough_valid_programs"); //$NON-NLS-1$
			result[4] = String.valueOf(valid);
			return result;
		}

		public void run() {
			Vector<SubmissionManager> subs = null;
			String[] result = null;

			try {
				subs = scanFiles();
				if (subs == null) {
/*					gui.setStatus(Messages.getString(
							"OptionPanel.No_files_found")); //$NON-NLS-1$*/
					return;
				}
				result = checkSubmissions(subs);
			} catch (InterruptedException t) {
				// gui.setStatus("(interrupted)", false);
				return;
			}

			// Only apply result when scanning was not interrupted
			if (!Thread.currentThread().isInterrupted())
				gui.scanThreadEnds(subs, result);
		}
	}
}