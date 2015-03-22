/*
 * Created on 08.06.2005
 * Author: Moritz Kroll
 */

package jplagAdminTool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = -5951691564111605981L;
	private javax.swing.JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jUsernameField = null;
	private JLabel jLabel1 = null;
	private JPasswordField jPasswordField = null;
	private JPanel jPanel1 = null;
	private JButton jOKButton = null;
	private JButton jCancelButton = null;
	
	private AdminTool adminTool = null;
	private JProgressBar jProgressBar = null;
	/**
	 * This is the default constructor
	 */
	public LoginDialog(JFrame parent) {
		super(parent);
		initialize();

		if (getJUsernameField().getText().length() > 0)
			getJPasswordField().requestFocus();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(231, 131);
        this.setResizable(false);
		this.setTitle("Login dialog...");
		this.setContentPane(getJContentPane());
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new java.awt.Insets(3,3,1,0);
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.ipadx = 100;
			gridBagConstraints4.ipady = 6;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(3,0,1,2);
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new java.awt.Insets(0,3,2,0);
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.ipadx = 100;
			gridBagConstraints21.ipady = 6;
			gridBagConstraints21.weightx = 1.0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.insets = new java.awt.Insets(0,0,2,2);
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1 = new JLabel();
			jLabel1.setText("Password:");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Username:");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			jPanel.add(getJPasswordField(), gridBagConstraints4);
			jPanel.add(jLabel1, gridBagConstraints3);
			jPanel.add(getJUsernameField(), gridBagConstraints21);
			jPanel.add(jLabel, gridBagConstraints11);
		}
		return jPanel;
	}
	
	private String readUsername() {
		String str="";
		try {
			PersistenceService ps = (PersistenceService) ServiceManager.
				lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService) ServiceManager.
				lookup("javax.jnlp.BasicService");
			URL baseURL = bs.getCodeBase();
			URL admintoolURL = new URL(baseURL,"AdminTool.cfg");
			FileContents fc = ps.get(admintoolURL);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(fc.getInputStream()));
			str=reader.readLine();
			reader.close();
		}
		// if an exception is thrown, the username just can't be set
		catch(UnavailableServiceException e) {}
		catch(Exception e) {}
		
		return str;
	}
	
	private void storeUsername()
	{
		try {
			PersistenceService ps = (PersistenceService) ServiceManager.
				lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService) ServiceManager.
				lookup("javax.jnlp.BasicService");
			URL baseURL = bs.getCodeBase();
			URL admintoolURL = new URL(baseURL,"AdminTool.cfg");
			
			// delete file if already exists
			try { ps.delete(admintoolURL); } catch(Exception e) {}
			ps.create(admintoolURL,1024);
			FileContents fc=ps.get(admintoolURL);
			DataOutputStream os=new DataOutputStream(fc.getOutputStream(true));
			os.writeBytes(getJUsernameField().getText() + "\n");
			os.flush();
			os.close();
		}
		// if an exception is thrown, the username just can't be stored
		catch(UnavailableServiceException e) {}
		catch(Exception e) {}
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJUsernameField() {
		if (jUsernameField == null) {
			jUsernameField = new JTextField();
			jUsernameField.setText(readUsername());
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
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getJOKButton(), null);
			jPanel1.add(getJProgressBar(), null);
			jPanel1.add(getJCancelButton(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();
			jOKButton.setText("OK");
			jOKButton.setPreferredSize(new java.awt.Dimension(70,26));
			jOKButton.setMargin(new java.awt.Insets(2,10,2,10));
			jOKButton.addActionListener(new ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					final SwingWorker worker = new SwingWorker() {
						public Object construct() {
							if(adminTool==null)
							{
								adminTool=new AdminTool();
								adminTool.setDefaultCloseOperation(
                                    JFrame.EXIT_ON_CLOSE);
							}
							if(adminTool.setLogin(getJUsernameField().getText(),
									new String(
                                        getJPasswordField().getPassword()),
                                    LoginDialog.this))
							{		
								storeUsername();
								setVisible(false);
								adminTool.setLocationRelativeTo(null);
								adminTool.setVisible(true);
								getOwner().dispose();
							}
							
							return null;
						}
						
						public void finished() {
							getJProgressBar().setIndeterminate(false);
						}
					};
					getJProgressBar().setIndeterminate(true);
					worker.start();
				}
			});
			getRootPane().setDefaultButton(jOKButton);
		}
		return jOKButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setText("Cancel");
			jCancelButton.setPreferredSize(new java.awt.Dimension(70,26));
			jCancelButton.setMargin(new java.awt.Insets(2,10,2,10));
			jCancelButton.addActionListener(new ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					System.exit(0);
				}
			});
		}
		return jCancelButton;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */    
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new java.awt.Dimension(60,14));
		}
		return jProgressBar;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
