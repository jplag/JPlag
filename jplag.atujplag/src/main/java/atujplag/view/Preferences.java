/*
 * Created on Jul 25, 2005
 */
package atujplag.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jplagWsClient.jplagClient.UpdateUserInfoParams;
import jplagWsClient.jplagClient.UserInfo;
import atujplag.ATUJPLAG;
import atujplag.client.SimpleClient;
import atujplag.util.Messages;
import atujplag.util.TagParser;

/**
 * @author Emeric Kwemou
 */
public class Preferences extends JDialog {
	private static final long serialVersionUID = -5133488603587416876L;
	private View view = null;
	private ATUJPLAG atujplag = null;

	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jButtonPanel = null;
	private JButton jOK = null;
	private JButton jApply = null;
	private JButton jCancel = null;

	private JPanel emailPanel = null;
	private JTextField emailField = null;
	private JTextField homepageField = null;

	private JComboBox<String> jLanguageCB = null;
	private JPanel reportLocPanel = null;
	private JTextField resultDirField = null;
	private JButton jResultDirButton = null;

	private JPanel passwordPanel = null;
	private JPasswordField jPasswordField = null;
	private JPasswordField jPasswordField1 = null;
	private JPasswordField jPasswordField2 = null;
	private JCheckBox jSavePassCB = null;

	private boolean languageChanged = false;
	private boolean resultLocationChanged = false;
	private boolean secondEmailChanged = false;
	private boolean homepageChanged = false;
	private boolean passwordChanged = false;
	private boolean savePassChanged = false;

	/**
	 * This is the default constructor
	 */
	public Preferences(View view) {
		super(view, true);
		this.view = view;
		atujplag = view.getATUJPLAG();
		this.setFont(JPlagCreator.SYSTEM_FONT);
		this.setTitle(Messages.getString("Preferences.JPlag_Preferences")); //$NON-NLS-1$
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
                    setVisible(false);
                    dispose();
                }
            }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        return rootPane;
    }
    
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new BoxLayout(jContentPane,BoxLayout.PAGE_AXIS));
			jContentPane.add(getJTabbedPane(), null);
			jContentPane.add(getJButtonPanel(), null);
			jContentPane.setBackground(JPlagCreator.SYSTEMCOLOR);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
            jButtonPanel.setBackground(JPlagCreator.SYSTEMCOLOR);
            jButtonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			jButtonPanel.add(getJOK(), null);
			jButtonPanel.add(getJCancel(), null);
            jButtonPanel.add(getJApply(), null);
		}
		return jButtonPanel;
	}

	/**
	 * Creates a new View object and destroys the old one because of
	 * a language change
	 */
	private View createNewLangView() {
		JPlagCreator.showMessageDialog(
				Messages.getString("Preferences.New_language_setting"), //$NON-NLS-1$
				Messages.getString("Preferences.New_language_setting_DESC")); //$NON-NLS-1$

		view.dispose();
		view.destroy();
        view=null;
		System.gc();
		atujplag.updateServerInfo(this);	// TODO: is this needed?
        View mainWindow = new View(atujplag);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
		return mainWindow;
	}
	
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOK() {
		if (jOK == null) {
			jOK = JPlagCreator.createButton(
				Messages.getString("Preferences.OK"), //$NON-NLS-1$
				Messages.getString("Preferences.OK_TIP"), //$NON-NLS-1$ 
				150, 20);

			jOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(applyChanges())
					{
						if (languageChanged) createNewLangView();
						else Preferences.this.dispose();
					}
				}
			});
		}
		return jOK;
	}

	/**
	 * This method initializes jApply
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJApply() {
		if (jApply == null) {
			jApply = JPlagCreator.createButton(
				Messages.getString("Preferences.Apply"), //$NON-NLS-1$
				Messages.getString("Preferences.Apply_TIP"), //$NON-NLS-1$
				150, 20);
			jApply.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					applyChanges();
					if (languageChanged)
						createNewLangView().openPreferences();
				}
			});
			jApply.setEnabled(false);
		}
		return jApply;
	}
	
	private void updateApplyButton() {
		jApply.setEnabled(languageChanged || resultLocationChanged
			|| secondEmailChanged || homepageChanged || passwordChanged
			|| savePassChanged);
	}
	
	private JButton getResultDirButton() {
		if(jResultDirButton == null) {
			jResultDirButton = JPlagCreator.createOpenFileButton(
					Messages.getString("Preferences.Select_report_location")); //$NON-NLS-1$
			jResultDirButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setDialogTitle(
						Messages.getString("Preferences.Select_report_location")); //$NON-NLS-1$
					int retval = chooser.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION)
						resultDirField.setText(chooser.getSelectedFile().getPath());
				}
			});
		}
		return jResultDirButton;
	}
	
	/**
	 * This method initializes jCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancel() {
		if (jCancel == null) {
			jCancel = JPlagCreator.createButton(
				Messages.getString("Preferences.Cancel"), //$NON-NLS-1$
				Messages.getString("Preferences.Cancel_TIP"), //$NON-NLS-1$
				150, 20);
			jCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Preferences.this.dispose();
				}
			});
		}
		return jCancel;
	}
	
	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setFont(JPlagCreator.SYSTEM_FONT);
			jTabbedPane.add(Messages.getString(
				"Preferences.Basic_settings"), //$NON-NLS-1$
                getReportLocPanel());
			jTabbedPane.add(Messages.getString(
				"Preferences.Change_password"), //$NON-NLS-1$
				getPasswordPanel()); 
			jTabbedPane.add(Messages.getString(
				"Preferences.User_information"), //$NON-NLS-1$
                getEmailPanel());
			jTabbedPane.setBackground(JPlagCreator.SYSTEMCOLOR);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
    private JPanel getReportLocPanel() {
        if(reportLocPanel == null) {
            reportLocPanel = JPlagCreator.createPanelWithoutBorder(500, 136, 10, 10,
                    FlowLayout.CENTER);
            JLabel reportLocLabel = new JLabel(Messages.getString(
                    "Preferences.Report_location_DESC") + ":");  //$NON-NLS-1$  //$NON-NLS-2$
            reportLocLabel.setPreferredSize(new Dimension(440 + 10 + 24,20));
            reportLocPanel.add(reportLocLabel);
            
            resultDirField = JPlagCreator.createTextField(440, 20, null);
            resultDirField.setText(atujplag.getResultLocation());
            resultDirField.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent arg0) {
                        resultLocationChanged = 
                            !atujplag.getResultLocation().equals(
                                resultDirField.getText());
                        updateApplyButton();
                    }

                    public void insertUpdate(DocumentEvent arg0) {
                        changedUpdate(arg0);
                    }

                    public void removeUpdate(DocumentEvent arg0) {
                        changedUpdate(arg0);
                    }
                });
            reportLocPanel.add(resultDirField);
            
            reportLocPanel.add(getResultDirButton());
            
            JLabel jLanguageLabel = new JLabel(Messages.getString(
                    "Preferences.Language") + ":");  //$NON-NLS-1$  //$NON-NLS-2$
            jLanguageLabel.setPreferredSize(new Dimension(90 + 150 + 24,20));
            reportLocPanel.add(jLanguageLabel, null);
            
            jLanguageCB = JPlagCreator.createJComboBox(
                    ATUJPLAG.COUNTRY_LANGUAGES, 200, 20,
                    Messages.getString("Preferences.Language_TIP")); //$NON-NLS-1$
            jLanguageCB.setSelectedIndex(atujplag.getCountryLanguageIndex());
            jLanguageCB.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent arg0) {
                    languageChanged = !atujplag.getCountryLanguage().equals(
                            jLanguageCB.getSelectedItem().toString());
                    updateApplyButton();
                }
            });
            reportLocPanel.add(jLanguageCB);
        }

        return reportLocPanel;
    }
    
	private JPanel getEmailPanel() {
		if (this.emailPanel == null) {
			this.emailPanel = JPlagCreator.createPanelWithoutBorder(500, 100, 10, 10,
					FlowLayout.CENTER);
            
			this.emailPanel.add(JPlagCreator.createLabel(
                    Messages.getString("Preferences.Email") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					214, 20));
            
			this.emailPanel.add(JPlagCreator.createLabel(
                    view.getServerInfos().getUserInfo().getEmail(), 250, 20));
            
			this.emailPanel.add(JPlagCreator.createLabel(
                    Messages.getString("Preferences.Second_email") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					214, 20));
            
			this.emailField = JPlagCreator.createTextField(250, 20,
				    Messages.getString("Preferences.Second_email_TIP")); //$NON-NLS-1$
			this.emailField.setText(
                    view.getServerInfos().getUserInfo().getEmailSecond());
			this.emailField.getDocument().addDocumentListener(
					new DocumentListener() {
						public void changedUpdate(DocumentEvent arg0) {
							String oldemail = view.getServerInfos().
                                    getUserInfo().getEmailSecond();
							if(oldemail==null) oldemail = ""; //$NON-NLS-1$
							secondEmailChanged = !emailField.getText().equals(
									oldemail);
							updateApplyButton();
						}

						public void insertUpdate(DocumentEvent arg0) {
							changedUpdate(arg0);
						}

						public void removeUpdate(DocumentEvent arg0) {
							changedUpdate(arg0);
						}
					});
			this.emailPanel.add(emailField);

			this.emailPanel.add(JPlagCreator.createLabel(
                    Messages.getString("Preferences.Homepage") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					214, 20));
			
			this.homepageField = JPlagCreator.createTextField(250, 20,
					Messages.getString("Preferences.Homepage_TIP")); //$NON-NLS-1$
			this.homepageField.setText(
                    view.getServerInfos().getUserInfo().getHomepage());
			this.homepageField.getDocument().addDocumentListener(
					new DocumentListener() {
						public void changedUpdate(DocumentEvent arg0) {
							String oldhome = view.getServerInfos().getUserInfo().getHomepage();
							if(oldhome==null) oldhome = ""; //$NON-NLS-1$
							homepageChanged = !homepageField.getText().equals(
									oldhome);
							updateApplyButton();
						}

						public void insertUpdate(DocumentEvent arg0) {
							this.changedUpdate(arg0);
						}

						public void removeUpdate(DocumentEvent arg0) {
							this.changedUpdate(arg0);
						}
					});
			this.emailPanel.add(homepageField);
		}
		return this.emailPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPasswordPanel() {
		if (passwordPanel == null) {
			passwordPanel = JPlagCreator.createPanelWithoutBorder(350, 100, 10, 10,
					FlowLayout.CENTER);

			passwordPanel.add(JPlagCreator.createLabel(
					Messages.getString("Preferences.Old_password") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					264, 20));
			passwordPanel.add(getOldJPasswordField());
			getOldJPasswordField().setToolTipText(
					Messages.getString("Preferences.Old_password_TIP")); //$NON-NLS-1$
			getOldJPasswordField().getDocument().addDocumentListener(
				new PasswordDocumentListener());
			
			passwordPanel.add(JPlagCreator.createLabel(
					Messages.getString("Preferences.New_password") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					264, 20));
			passwordPanel.add(getJPasswordField1());
			getJPasswordField1().setToolTipText(
				Messages.getString("Preferences.New_password_TIP")); //$NON-NLS-1$
			getJPasswordField2().getDocument().addDocumentListener(
				new PasswordDocumentListener());
			
			passwordPanel.add(JPlagCreator.createLabel(
					Messages.getString("Preferences.Reenter_password") + ":", //$NON-NLS-1$ //$NON-NLS-2$
					264, 20));
			passwordPanel.add(getJPasswordField2(), null);
			getJPasswordField2().setToolTipText(
				Messages.getString("Preferences.Reenter_password_TIP")); //$NON-NLS-1$
			getJPasswordField2().getDocument().addDocumentListener(
					new PasswordDocumentListener());
			
			passwordPanel.add(getSavePassCB(), null);
            Dimension dim=getSavePassCB().getPreferredSize();
            passwordPanel.add(javax.swing.Box.createHorizontalStrut(464-dim.width));
		}
		return passwordPanel;
	}

	private class PasswordDocumentListener implements DocumentListener {
		public void changedUpdate(DocumentEvent arg0) {
			String newPass=new String(
				getJPasswordField1().getPassword());
			passwordChanged = 
				new String(getOldJPasswordField().getPassword())
					.length()>0 && newPass.length()>0
					&& newPass.equals(new String(
						getJPasswordField2().getPassword()));
			updateApplyButton();
		}

		public void insertUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}

		public void removeUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}
	}

	/**
	 * This method initializes jPasswordField1
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField1() {
		if (jPasswordField1 == null) {
			jPasswordField1 = new JPasswordField();
			jPasswordField1.setPreferredSize(new java.awt.Dimension(200, 20));
		}
		return jPasswordField1;
	}

	/**
	 * This method initializes jPasswordField2
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField2() {
		if (jPasswordField2 == null) {
			jPasswordField2 = new JPasswordField();
			jPasswordField2.setPreferredSize(new java.awt.Dimension(200, 20));
		}
		return jPasswordField2;
	}

	/**
	 * This method initializes jPasswordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getOldJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setPreferredSize(new java.awt.Dimension(200, 20));
		}
		return jPasswordField;
	}
	
	/**
	 * This method initializes jSavePassCB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSavePassCB() {
		if (jSavePassCB == null) {
			jSavePassCB = new JCheckBox();
			jSavePassCB.setText(Messages.getString(
				"Preferences.Remember_password")); //$NON-NLS-1$
			jSavePassCB.setToolTipText(Messages.getString(
				"Preferences.Remember_password_TIP")); //$NON-NLS-1$
			jSavePassCB.setForeground(JPlagCreator.BUTTON_FOREGROUND);
			jSavePassCB.setFont(JPlagCreator.SYSTEM_FONT);
			jSavePassCB.setBackground(JPlagCreator.SYSTEMCOLOR);
			jSavePassCB.setSelected(atujplag.isSavePassword());
			jSavePassCB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					savePassChanged = jSavePassCB.isSelected()!=atujplag.isSavePassword();
					updateApplyButton();
				}
			});
		}
		return jSavePassCB;
	}
	
	/**
	 * @return True, if the email address fits the pattern "*@*.*"
	 */
	private static boolean isValidEMailAddress(String email) {
		String tokens[]=email.split("@"); //$NON-NLS-1$
		if(tokens.length==2)
		{
			String tokens2[]=tokens[1].split("\\."); //$NON-NLS-1$
			if(tokens2.length>=2)
				return true;
		}
		return false;
	}
	
	private boolean applyChanges() {
		if (resultLocationChanged) {
			File f = new File(resultDirField.getText());
			if (!f.exists()) {
				int rep = JPlagCreator.showConfirmDialog(
					Messages.getString("Preferences.Directory_does_not_exist"), //$NON-NLS-1$
					TagParser.parse(Messages.getString(
							"Preferences.Directory_does_not_exist_DESC_{1_PATH}"), //$NON-NLS-1$
						new String[] { f.getPath() }));
				if (rep == JOptionPane.YES_OPTION)
					f.mkdirs();
				else
					return false;
			}
            else if(!f.isDirectory()) {
                JPlagCreator.showMessageDialog(
                        Messages.getString(
                            "Preferences.File_is_no_directory"), //$NON-NLS-1$
                        TagParser.parse(Messages.getString(
                            "Preferences.File_is_no_directory_DESC_{1_PATH}"), //$NON-NLS-1$
                            new String[] { f.getPath() }));
                return false;
            }
			atujplag.setResultLocation(resultDirField.getText());
			resultLocationChanged = false;
			view.updateTable(null);
		}
		
		if(languageChanged)
			atujplag.setCountryLanguage(
                jLanguageCB.getSelectedItem().toString(), true);
		
		UpdateUserInfoParams para = new UpdateUserInfoParams(null, null, null);
        boolean doUpdate = false;
		if (secondEmailChanged) {
			String newEmail=emailField.getText();
			if (newEmail.length()>0 && !isValidEMailAddress(newEmail)) {
				JPlagCreator.showError(this,
					Messages.getString("Preferences.Invalid_email_address"), //$NON-NLS-1$
					Messages.getString("Preferences.Invalid_email_address_DESC")); //$NON-NLS-1$
				return false;
			} else {
				para.setNewEmailSecond(newEmail);
                doUpdate = true;
			}
		}
		if(homepageChanged) {
			para.setNewHomepage(homepageField.getText());
            doUpdate = true;
		}
		if(passwordChanged){
			if(!atujplag.getPassword().equals(new String(
					getOldJPasswordField().getPassword()))) {
				JPlagCreator.showError(this,
					Messages.getString("Preferences.Wrong_password"), //$NON-NLS-1$
					Messages.getString("Preferences.Wrong_password_DESC")); //$NON-NLS-1$
				return false;
			}
			String newPass = new String(getJPasswordField1().getPassword());
			if (newPass.length() < 6) {
				JPlagCreator.showError(this,
					Messages.getString("Preferences.Password_too_short"), //$NON-NLS-1$
					Messages.getString("Preferences.Password_too_short_DESC")); //$NON-NLS-1$
                return false;
			}
			else if (!newPass.equals(
					new String(getJPasswordField2().getPassword()))) {
				JPlagCreator.showError(this,
					Messages.getString("Preferences.Passwords_not_identical"), //$NON-NLS-1$
					Messages.getString("Preferences.Passwords_not_identical_DESC")); //$NON-NLS-1$
                return false;
			}
			else {
                para.setNewPassword(newPass);
                doUpdate = true;
            }
		}
		if(doUpdate && !SimpleClient.updateUserInfo(atujplag, para, this))
			return false;

		if(para.getNewPassword() != null)
			atujplag.setPassword(para.getNewPassword());
		if(savePassChanged)
			atujplag.setRememberPassword(getSavePassCB().isSelected());
		
		secondEmailChanged = false;
		homepageChanged = false;
		passwordChanged = false;
		savePassChanged = false;

		if(view.getServerInfos() != null) {
			UserInfo userInfo = view.getServerInfos().getUserInfo();
			if(para.getNewEmailSecond() != null)
				userInfo.setEmailSecond(para.getNewEmailSecond());
		
			if(para.getNewHomepage() != null)
				userInfo.setHomepage(para.getNewHomepage());
		}

		getJApply().setEnabled(false);
		return true;
	}
} // @jve:decl-index=0:visual-constraint="123,-7"
