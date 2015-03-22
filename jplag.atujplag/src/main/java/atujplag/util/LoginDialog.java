/*
 * Created on Jun 12, 2005
 */
package atujplag.util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jplagWsClient.jplagClient.ServerInfo;
import atujplag.ATUJPLAG;
import atujplag.view.JPlagCreator;
import atujplag.view.View;


/**
 * @author Emeric Kwemou
 */
public class LoginDialog	extends JDialog {
	private static final long serialVersionUID = -5565346629684084663L;
	private JPanel			jContentPane	= null;
	private JTextField	    jUsernameField	= null;
	private JPasswordField	jPasswordField	= null;
	private JButton			jOKButton	    = null;
	private JButton			jCancelButton	= null;
	private JCheckBox		jCheckBox		= null;
	private JProgressBar 	jProgressBar	= null;

	private boolean			init 			= true;
	private ATUJPLAG		atujplag		= null;

	private ServerInfo      serverInfo = null;

	/**
	 * Sets up the DefaultSettings dialog for ATUJPLAG's first start
	 */
	public LoginDialog(ATUJPLAG atujplag, Frame owner) {
		super(owner);
		this.atujplag = atujplag;
		initialize();
	}

	/**
	 * Sets up the DefaultSettings dialog for users who already used ATUJPLAG If
	 * init is true, the "Cancel" button will exit the whole program
	 */
	public LoginDialog(String username, String password, boolean init,
			ATUJPLAG atujplag, Frame owner) {
		super(owner);
		this.atujplag = atujplag;
		this.init=init;
		initialize();
		if(username.length()>0) {
			getJUsernameField().setText(username);
			getJUsernameField().addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					getJUsernameField().transferFocus();
				}
				public void focusLost(FocusEvent e) {
					getJUsernameField().removeFocusListener(this);
				}});
			getJPasswordField().setText(password);
			if(password.length()!=0) getJCheckBox().setSelected(true);
		}
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
	 * 
	 * @return
	 */
	private void initialize() {
		this.setTitle(Messages.getString("LoginDialog.JPlag_login_dialog_TITLE") + " v" + ATUJPLAG.VERSION_STRING); //$NON-NLS-1$
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		
		// Emulate a click on the cancel button, when the dialog is closed
		addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				jCancelButton.doClick();
			}});
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
			jContentPane.setBackground(JPlagCreator.SYSTEMCOLOR);
			jContentPane.setBorder(JPlagCreator.LINE);
			jContentPane.setPreferredSize(new java.awt.Dimension(400,135));
		
			jContentPane.add(JPlagCreator.createLabel(
				Messages.getString("LoginDialog.Username_label") + ":", //$NON-NLS-1$ //$NON-NLS-2$
				170, 20), null);
			jContentPane.add(getJUsernameField(), null);
			jContentPane.add(JPlagCreator.createLabel(
				Messages.getString("LoginDialog.Password_label") + ":", //$NON-NLS-1$ //$NON-NLS-2$
				170, 20), null);
			jContentPane.add(getJPasswordField(), null);
			jContentPane.add(getJCheckBox(), null);
            Dimension dim = getJCheckBox().getPreferredSize();
            jContentPane.add(javax.swing.Box.createHorizontalStrut(304-dim.width));
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(getJOKButton(), null);
			jContentPane.add(getJCancelButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jUsernameField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJUsernameField() {
		if (jUsernameField == null) {
			jUsernameField = JPlagCreator.createTextField(200,20,null); //$NON-NLS-1$
		}
		return jUsernameField;
	}

	/**
	 * This method initializes jPasswordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setPreferredSize(new Dimension(200, 20));
		}
		return jPasswordField;
	}
	
	private JProgressBar getJProgressBar() {
		if(jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new Dimension(60,14));
			jProgressBar.setForeground(java.awt.Color.black);
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jOKButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = JPlagCreator.createButton(
					Messages.getString("LoginDialog.OK_button"), //$NON-NLS-1$
					Messages.getString("LoginDialog.OK_button_TIP"),150,20); //$NON-NLS-1$
			jOKButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (getJUsernameField().getText().length() == 0 
							|| getJPasswordField().getPassword().length == 0) {
						JPlagCreator.showError(LoginDialog.this,
							Messages.getString("LoginDialog.No_username_or_password"), //$NON-NLS-1$
							Messages.getString("LoginDialog.No_username_or_password_DESC")); //$NON-NLS-1$
						return;
					}
					final SwingWorker worker = new SwingWorker() {
						View mainWindow = null;
						boolean loginOK = false;
						
						public Object construct() {
							if(!atujplag.login(getJUsernameField().getText(),
								new String(getJPasswordField().getPassword()),
								getJCheckBox().isSelected(), LoginDialog.this))
                                return null;
							
							mainWindow = new View(atujplag);
							loginOK = true;
							return null;
						}
						public void finished() {
							if(loginOK) {
								if(init) {
									mainWindow.setDefaultCloseOperation(
										JFrame.EXIT_ON_CLOSE);
									mainWindow.pack();
									mainWindow.setLocationRelativeTo(null);
									LoginDialog.this.dispose();
									mainWindow.setVisible(true);
								}
								else {
									View oldview = (View) getOwner();
									oldview.dispose();
									oldview.destroy();
                                    oldview = null;
									System.gc();
							        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
									mainWindow.pack();
									mainWindow.setLocationRelativeTo(null);
									LoginDialog.this.dispose();
									mainWindow.setVisible(true);
								}
							}
							else {
								getJProgressBar().setIndeterminate(false);
                                getJUsernameField().setEnabled(true);
                                getJPasswordField().setEnabled(true);
                                getJCheckBox().setEnabled(true);
                                getJOKButton().setEnabled(true);
                                
                                // transfer focus from cancel button to
                                // password field
                                
                                getJCancelButton().transferFocus();
                                getJUsernameField().transferFocus();
							}
						}
					};
					getJProgressBar().setIndeterminate(true);
                    getJCheckBox().setEnabled(false);
                    getJOKButton().setEnabled(false);
                    getJUsernameField().setEnabled(false);
                    getJPasswordField().setEnabled(false);
                    
                    // now cancel button will have the focus
                    
					worker.start();
				}
			});
			getRootPane().setDefaultButton(jOKButton);
		}
		return jOKButton;
	}

	/**
	 * This method initializes jCancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = JPlagCreator.createButton(
					Messages.getString("LoginDialog.Cancel"), //$NON-NLS-1$
					init ? Messages.getString("LoginDialog.Cancel_TIP_exit") //$NON-NLS-1$
						 : Messages.getString("LoginDialog.Cancel_TIP_dont_switch"), //$NON-NLS-1$
					150,20);
			jCancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(init) System.exit(0);
					else dispose();
				}
			});
		}
		return jCancelButton;
	}

	/**
	 * 
	 */
	public void setSavePassword(boolean save) {
		getJCheckBox().setSelected(save);
	}
	
	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
            jCheckBox = new JCheckBox();
            jCheckBox.setText(Messages.getString(
                "LoginDialog.Remember_password")); //$NON-NLS-1$
            jCheckBox.setToolTipText(Messages.getString(
                "LoginDialog.Remember_password_TIP")); //$NON-NLS-1$
            jCheckBox.setForeground(JPlagCreator.BUTTON_FOREGROUND);
            jCheckBox.setFont(JPlagCreator.SYSTEM_FONT);
            jCheckBox.setBackground(JPlagCreator.SYSTEMCOLOR);
        }
		return jCheckBox;
	}

	public void setPassword(String current_password) {
		getJPasswordField().setText(current_password);
	}

	public String getPassword() {
		return new String(getJPasswordField().getPassword());
	}
	
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
}