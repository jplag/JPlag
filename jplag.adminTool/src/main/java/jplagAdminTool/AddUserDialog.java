package jplagAdminTool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jplagWsClient.jplagClient.SetUserDataParams;

public class AddUserDialog extends JDialog {

	private static final long serialVersionUID = 7452010241285343412L;
	private javax.swing.JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jUsernameField = null;
	private JLabel jLabel1 = null;
	private JTextField jPasswordField = null;
	private JLabel jLabel2 = null;
	private JTextField jRealnameField = null;
	private JLabel jLabel3 = null;
	private JTextField jEmailField = null;
	private JLabel jLabel4 = null;
	private JTextField jEmailSecondField = null;
	private JLabel jLabel5 = null;
	private JTextField jHomepageField = null;
	private JLabel jLabel6 = null;
	private JTextArea jNotesTextArea = null;
	private JLabel jLabel7 = null;
	private JComboBox<String> jStateComboBox = null;
	private JPanel jPanel1 = null;
	private JButton jOKButton = null;
	private JButton jCancelButton = null;
	
	private AdminTool adminTool = null;
	private JLabel jLabel8 = null;
	private JTextField jExpiresField = null;
	private JScrollPane jScrollPane = null;
	private JLabel jLabel9 = null;
	private JScrollPane jScrollPane1 = null;
	private JTextArea jReasonTextArea = null;
	/**
	 * This is the default constructor
	 */
	public AddUserDialog(AdminTool adTool) {
		super(adTool,true);
		adminTool=adTool;
		initialize();
	}

	protected JRootPane createRootPane() {
		KeyStroke stroke=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane=new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					setVisible(false);
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
		this.setSize(396, 325);
		this.setTitle("Add a new user...");
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
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints23.gridx = 1;
			gridBagConstraints23.gridy = 6;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.weighty = 1.0;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.gridy = 6;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel9 = new JLabel();
			jLabel9.setText("Reason:");
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints111.gridx = 1;
			gridBagConstraints111.gridy = 7;
			gridBagConstraints111.weightx = 1.0;
			gridBagConstraints111.weighty = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 9;
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 9;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			jLabel8 = new JLabel();
			jLabel8.setText("Expires:");
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridx = 1;
			gridBagConstraints22.gridy = 8;
			gridBagConstraints22.weightx = 1.0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 8;
			gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			jLabel7 = new JLabel();
			jLabel7.setText("State:");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.gridy = 7;
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel6 = new JLabel();
			jLabel6.setText("Notes:");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 5;
			gridBagConstraints17.weightx = 1.0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 5;
			gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints16.insets = new java.awt.Insets(0,0,0,5);
			jLabel5 = new JLabel();
			jLabel5.setText("Homepage:");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.gridy = 4;
			gridBagConstraints15.weightx = 1.0;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.gridy = 4;
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
			jLabel4 = new JLabel();
			jLabel4.setText("2. Email:");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 3;
			gridBagConstraints13.weightx = 1.0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3 = new JLabel();
			jLabel3.setText("Email:");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.weightx = 1.0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			jLabel2 = new JLabel();
			jLabel2.setText("Realname:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1 = new JLabel();
			jLabel1.setText("Password:");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			jLabel = new JLabel();
			jLabel.setText("Username:");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			jPanel.add(getJScrollPane1(), gridBagConstraints23);
			jPanel.add(jLabel9, gridBagConstraints19);
			jPanel.add(getJScrollPane(), gridBagConstraints111);
			jPanel.add(jLabel, gridBagConstraints6);
			jPanel.add(getJHomepageField(), gridBagConstraints17);
			jPanel.add(getJEmailSecondField(), gridBagConstraints15);
			jPanel.add(getJEmailField(), gridBagConstraints13);
			jPanel.add(getJRealnameField(), gridBagConstraints11);
			jPanel.add(getJPasswordField(), gridBagConstraints9);
			jPanel.add(getJUsernameField(), gridBagConstraints7);
			jPanel.add(jLabel1, gridBagConstraints8);
			jPanel.add(jLabel2, gridBagConstraints10);
			jPanel.add(jLabel3, gridBagConstraints12);
			jPanel.add(jLabel4, gridBagConstraints14);
			jPanel.add(jLabel5, gridBagConstraints16);
			jPanel.add(jLabel6, gridBagConstraints18);
			jPanel.add(jLabel7, gridBagConstraints21);
			jPanel.add(getJStateComboBox(), gridBagConstraints22);
			jPanel.add(jLabel8, gridBagConstraints1);
			jPanel.add(getJExpiresField(), gridBagConstraints2);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJUsernameField() {
		if (jUsernameField == null) {
			jUsernameField = new JTextField();
			jUsernameField.setPreferredSize(new java.awt.Dimension(300,20));
		}
		return jUsernameField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JTextField();
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJRealnameField() {
		if (jRealnameField == null) {
			jRealnameField = new JTextField();
		}
		return jRealnameField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJEmailField() {
		if (jEmailField == null) {
			jEmailField = new JTextField();
		}
		return jEmailField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJEmailSecondField() {
		if (jEmailSecondField == null) {
			jEmailSecondField = new JTextField();
		}
		return jEmailSecondField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJHomepageField() {
		if (jHomepageField == null) {
			jHomepageField = new JTextField();
		}
		return jHomepageField;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJNotesTextArea() {
		if (jNotesTextArea == null) {
			jNotesTextArea = new JTextArea();
			jNotesTextArea.setRows(3);
			jNotesTextArea.setLineWrap(true);
			jNotesTextArea.setWrapStyleWord(true);
		}
		return jNotesTextArea;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJExpiresField() {
		if (jExpiresField == null) {
			jExpiresField = new JTextField();
			GregorianCalendar c=new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			c.add(Calendar.YEAR,1);
			jExpiresField.setText(RequestDialog.formatCalendar(c));
		}
		return jExpiresField;
	}
	
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox<String> getJStateComboBox() {
		if (jStateComboBox == null) {
			jStateComboBox = new JComboBox<String>(
					BackedUserData.getStateNameArray(adminTool.getUserState()));
		}
		return jStateComboBox;
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
			jOKButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String username=getJUsernameField().getText(); 
					if(username.length()<4)
					{
						javax.swing.JOptionPane.showMessageDialog(
								AddUserDialog.this,
								"The username must have at least 4 " +
								"characters!","Username is too short!",
								javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					String password=getJPasswordField().getText();
					if(password.length()<6)
					{
						javax.swing.JOptionPane.showMessageDialog(
								AddUserDialog.this,
								"The password must have at least 6 " +
								"characters! If you still want to set a " +
								"shorter password, you can change it in the " +
								"main dialog after setting a correct one.",
								"Password is too short!",
								javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					String realname=getJRealnameField().getText();
					String email=getJEmailField().getText();
					String emailsecond=getJEmailSecondField().getText();
					String homepage=getJHomepageField().getText();
					String reason=getJReasonTextArea().getText();
					String notes=getJNotesTextArea().getText();
					int state = BackedUserData.stateInts[
                         getJStateComboBox().getSelectedIndex()];
/*					switch(getJStateComboBox().getSelectedIndex())
					{
						case 0: state=BackedUserData.USER_NORMAL; break;
						case 1: state=BackedUserData.USER_EXPIRED; break;
						case 2: state=BackedUserData.USER_DEACTIVATED; break;
						case 3: state=BackedUserData.USER_GROUPADMIN; break;
						case 4: state=BackedUserData.USER_ADMIN; break;
						case 5: state=BackedUserData.USER_ADMINNOTIFY; break;
						case 6: state=BackedUserData.USER_SERVERPAGE; break;
						default:
							// how could this happen?
							javax.swing.JOptionPane.showMessageDialog(
									AddUserDialog.this,
									"You selected an invalid state!",
									"Invalid state!",
									javax.swing.JOptionPane.ERROR_MESSAGE);
							return;
					}*/
                    
					Calendar expires;
					Calendar cal=Calendar.getInstance(
							TimeZone.getTimeZone("GMT"));
					try
					{
						expires=RequestDialog.parseCalendar(
								getJExpiresField().getText());

						if(expires!=null)
						{
							if(cal.after(expires))
							{
								javax.swing.JOptionPane.showMessageDialog(
										AddUserDialog.this,
										"Please check the expire date field!",
										"Expire date is already expired!",
										javax.swing.JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
					}
					catch(ParseException ex)
					{
						JOptionPane.showMessageDialog(
								AddUserDialog.this,
								"Please check the expire date field!",
								"Illegal expire date!",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					
					// Create BackedUserData object and check the rest
					
					BackedUserData data=new BackedUserData(username,password,
							cal,adminTool.getUsername(),expires,null,0,realname,
							email,emailsecond,homepage,reason,notes,state);
					if(!data.checkValid(adminTool.getUserTableModel(),
							AddUserDialog.this,false)) return;
					
					try
					{
						adminTool.getJPlagStub().setUserData(
								new SetUserDataParams(data,null));
						adminTool.getUserTableModel().addNewUser(data);
                        adminTool.updateTitle();
						setVisible(false);
						return;
					}
					catch(Exception ex)
					{
						adminTool.CheckException(ex,AddUserDialog.this);
						return;
					}
				}
			});
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
			jCancelButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setVisible(false);
				}
			});
		}
		return jCancelButton;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJNotesTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJReasonTextArea());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJReasonTextArea() {
		if (jReasonTextArea == null) {
			jReasonTextArea = new JTextArea();
			jReasonTextArea.setLineWrap(true);
			jReasonTextArea.setWrapStyleWord(true);
			jReasonTextArea.setRows(3);
		}
		return jReasonTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
