package jplagAdminTool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class PassDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel jContentPane = null;
	private JTextField jOldPassTextField = null;
	private JTextField jNewPassTextField = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private javax.swing.JPanel jPanel = null;
	
	private String oldpassword;
	private BackedUserData data;
	private AdminTool adminTool;
	private JPanel jPanel1 = null;

	/**
	 * This is the default constructor
	 */
	public PassDialog(BackedUserData ud, AdminTool parent) {
		super(parent,true);				 // make dialog modal
		data=ud;
		adminTool=parent;
		oldpassword=data.getPassword();
        if(oldpassword.length()==0) oldpassword="*********";
		initialize();
	}
	
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
		this.setSize(288, 133);
		this.setTitle("Change password for user");
		this.setContentPane(getJContentPane());
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJOldPassTextField() {
		if (jOldPassTextField == null) {
			jOldPassTextField = new JTextField();
			jOldPassTextField.setText(oldpassword);
			jOldPassTextField.setEditable(false);
			jOldPassTextField.setPreferredSize(new java.awt.Dimension(120,20));
			jOldPassTextField.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					jOldPassTextField.transferFocus();
				}
				public void focusLost(FocusEvent e) {
					jOldPassTextField.removeFocusListener(this);
				}});
		}
		return jOldPassTextField;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJNewPassTextField() {
		if (jNewPassTextField == null) {
			jNewPassTextField = new JTextField();
		}
		return jNewPassTextField;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Apply");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					data.setPassword(getJNewPassTextField().getText());
					if(adminTool.userDataChanged(data)) {
						setVisible(false);
                        dispose();
                    }
				}
			});
			getRootPane().setDefaultButton(jButton);
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Cancel");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setVisible(false);
                    dispose();
				}
			});
		}
		return jButton1;
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
	
	private JPanel getJPanel() {
		if(jPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.insets = new java.awt.Insets(3,3,1,0);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.insets = new java.awt.Insets(0,3,2,0);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1 = new JLabel();
			jLabel1.setText("New password:");
			jLabel = new JLabel();
			jLabel.setText("Old password:");
			jPanel = new javax.swing.JPanel();
			jPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJNewPassTextField(), gridBagConstraints8);
			jPanel.add(jLabel1, gridBagConstraints7);
			jPanel.add(getJOldPassTextField(), gridBagConstraints6);
			jPanel.add(jLabel, gridBagConstraints5);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getJButton(), null);
			jPanel1.add(getJButton1(), null);
		}
		return jPanel1;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
