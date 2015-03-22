package jplagAdminTool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jplagWsClient.jplagClient.FinishRequestData;
import jplagWsClient.jplagClient.RequestData;

public class RequestDialog extends JDialog {

	private static final long serialVersionUID = 1601751795315331307L;
	private javax.swing.JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jRealNameTextField = null;
	private JLabel jLabel1 = null;
	private JTextField jUsernameTextField = null;
	private JLabel jLabel2 = null;
	private JTextField jPasswordTextField = null;
	private JLabel jLabel3 = null;
	private JTextField jEmailTextField = null;
	private JLabel jLabel4 = null;
	private JTextField jEmailSecondTextField = null;
	private JLabel jLabel5 = null;
	private JTextField jHomepageTextField = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JPanel jPanel1 = null;
	private JButton jAcceptButton = null;
	private JButton jDeclineButton = null;
	private JButton jPreviousButton = null;
	private JButton jNextButton = null;
	private JButton jCloseButton = null;
	private JPanel jPanel2 = null;
	private JLabel jRequestNumLabel = null;
	private JPanel jPanel3 = null;

	private RequestData[] reqdatas;
	private String[] oldUsernames;
	private String[] expireStrs;
    private String[] notesStrs;
	private AdminTool adminTool;
	
	private int reqind=0;
	private JTextField jExpireTextField = null;
	private JLabel jStateLabel = null;
    
	private enum RequestState { open, accepted, declined, processing }
	
	private RequestState[] states=null;
	private JTextArea jReasonTextArea = null;
	private JTextArea jNotesTextArea = null;
	private JScrollPane jScrollPane = null;
	private JScrollPane jScrollPane1 = null;
	private JProgressBar jProgressBar = null;
	private JPanel jPanel4 = null;
	private JLabel jLabel9 = null;
	private JLabel jLabel10 = null;
	private JLabel jLabel8 = null;
	private JTextField jRequestTimeField = null;
	private JScrollPane jNotesServerScrollPane = null;
	private JTextArea jNotesServerTextArea = null;
    private JButton jShowHomepageButton = null;
    private JButton jShowMailProviderButton = null;
    private JLabel jUserStateLabel = null;
    private JComboBox<String> jStateComboBox = null;
	
	/**
	 * This is the default constructor
	 */
	public RequestDialog(RequestData[] rds, AdminTool at) {
		super(at);
		reqdatas = rds;
		states = new RequestState[rds.length];
		for(int i=0; i<states.length; i++) states[i] = RequestState.open;
		GregorianCalendar c = new GregorianCalendar(
            TimeZone.getTimeZone("GMT"));
		c.add(Calendar.YEAR,1);
		String calstr = formatCalendar(c);
        oldUsernames = new String[rds.length];
        notesStrs = new String[rds.length];
        expireStrs = new String[rds.length];
		for(int i=0; i<rds.length; i++) {
			oldUsernames[i] = reqdatas[i].getUsername();
            notesStrs[i] = "";
            expireStrs[i] = calstr;
        }
		adminTool = at;
		initialize();
		setData();
	}
    
    private void doClose() {
        setVisible(false);
        dispose();
        adminTool.updateRequestsWaiting();
    }

	protected JRootPane createRootPane() {
		KeyStroke stroke=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane=new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
                    doClose();
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
		this.setSize(601, 568);
		this.setTitle("Request dialog");
		this.setResizable(false);
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
			jContentPane.add(getJPanel2(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJPanel3(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jRequestNumLabel = new JLabel();
			jRequestNumLabel.setText("Request 1/999");
			jStateLabel = new JLabel();
			jStateLabel.setText("State of request: blub");
			jPanel2 = new JPanel();
			FlowLayout flowLayout19 = new FlowLayout();
			flowLayout19.setHgap(50);
			jPanel2.setLayout(flowLayout19);
			jPanel2.add(jRequestNumLabel, null);
			jPanel2.add(jStateLabel, null);
			jPanel2.add(getJProgressBar(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */    
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new java.awt.Dimension(50,14));
		}
		return jProgressBar;
	}
	
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new BorderLayout());
			jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			jPanel3.add(getJPanel(), java.awt.BorderLayout.NORTH);
			jPanel3.add(getJPanel4(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel3;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 3;
			gridBagConstraints19.gridy = 4;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 6;
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints24.gridx = 2;
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.gridwidth = 2;
			gridBagConstraints24.weightx = 1.0;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
			jLabel8 = new JLabel();
			jLabel8.setText("Request time:");
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints23.gridx = 2;
			gridBagConstraints23.gridy = 8;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.gridwidth = 2;
			gridBagConstraints23.weighty = 1.0;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints22.gridx = 2;
			gridBagConstraints22.gridy = 7;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.gridwidth = 2;
			gridBagConstraints22.weighty = 1.0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 7;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.weighty = 1.0;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints20.gridx = 2;
			gridBagConstraints20.gridy = 6;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.weighty = 1.0;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 8;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel7 = new JLabel();
			jLabel7.setText("Additional notes:");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 7;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel6 = new JLabel();
			jLabel6.setText("Reason:");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.gridy = 6;
			gridBagConstraints12.weightx = 1.0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 6;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			jLabel5 = new JLabel();
			jLabel5.setText("Homepage:");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 5;
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.weightx = 1.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.insets = new java.awt.Insets(0,0,0,5);
			gridBagConstraints9.ipadx = 71;
			jLabel4 = new JLabel();
			jLabel4.setText("Secondary email:");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.gridwidth = 1;
			gridBagConstraints8.weightx = 1.0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 4;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3 = new JLabel();
			jLabel3.setText("Primary email:");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			jLabel2 = new JLabel();
			jLabel2.setText("Password:");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1 = new JLabel();
			jLabel1.setText("Username:");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridwidth = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			jLabel = new JLabel();
			jLabel.setText("Real name:");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			jPanel.add(getJRequestTimeField(), gridBagConstraints24);
			jPanel.add(jLabel8, gridBagConstraints17);
			jPanel.add(jLabel7, gridBagConstraints15);
			jPanel.add(jLabel6, gridBagConstraints13);
			jPanel.add(getJHomepageTextField(), gridBagConstraints12);
			jPanel.add(jLabel5, gridBagConstraints11);
			jPanel.add(getJEmailSecondTextField(), gridBagConstraints10);
			jPanel.add(jLabel4, gridBagConstraints9);
			jPanel.add(getJEmailTextField(), gridBagConstraints8);
			jPanel.add(jLabel3, gridBagConstraints7);
			jPanel.add(getJPasswordTextField(), gridBagConstraints6);
			jPanel.add(jLabel2, gridBagConstraints5);
			jPanel.add(getJUsernameTextField(), gridBagConstraints4);
			jPanel.add(jLabel1, gridBagConstraints3);
			jPanel.add(getJRealNameTextField(), gridBagConstraints2);
			jPanel.add(jLabel, gridBagConstraints1);
			jPanel.add(getJScrollPane(), gridBagConstraints22);
			jPanel.add(getJScrollPane1(), gridBagConstraints23);
			jPanel.add(getJShowHomepageButton(), gridBagConstraints);
			jPanel.add(getJShowMailProviderButton(), gridBagConstraints19);
		}
		return jPanel;
	}

	/**
	 * This method initializes jRequestTimeField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJRequestTimeField() {
		if (jRequestTimeField == null) {
			jRequestTimeField = new JTextField();
			jRequestTimeField.setEditable(false);
		}
		return jRequestTimeField;
	}
	
	/**
	 * This method initializes jRealNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJRealNameTextField() {
		if (jRealNameTextField == null) {
			jRealNameTextField = new JTextField();
		}
		return jRealNameTextField;
	}

	/**
	 * This method initializes jUsernameTextField
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJUsernameTextField() {
		if (jUsernameTextField == null) {
			jUsernameTextField = new JTextField();
		}
		return jUsernameTextField;
	}

	/**
	 * This method initializes jPasswordTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJPasswordTextField() {
		if (jPasswordTextField == null) {
			jPasswordTextField = new JTextField();
		}
		return jPasswordTextField;
	}

	/**
	 * This method initializes jEmailTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJEmailTextField() {
		if (jEmailTextField == null) {
			jEmailTextField = new JTextField();
		}
		return jEmailTextField;
	}

	/**
	 * This method initializes jEmailSecondTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJEmailSecondTextField() {
		if (jEmailSecondTextField == null) {
			jEmailSecondTextField = new JTextField();
		}
		return jEmailSecondTextField;
	}

	/**
	 * This method initializes jHomepageTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJHomepageTextField() {
		if (jHomepageTextField == null) {
			jHomepageTextField = new JTextField();
		}
		return jHomepageTextField;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJReasonTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jReasonTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJReasonTextArea() {
		if (jReasonTextArea == null) {
			jReasonTextArea = new JTextArea();
			jReasonTextArea.setRows(7);
			jReasonTextArea.setLineWrap(true);
			jReasonTextArea.setWrapStyleWord(true);
			jReasonTextArea.setEditable(false);
			jReasonTextArea.setBackground(javax.swing.UIManager
				      .getColor("Button.background"));
		}
		return jReasonTextArea;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJNotesTextArea());
		}
		return jScrollPane1;
	}
	
	/**
	 * This method initializes jNotesTextArea
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJNotesTextArea() {
		if (jNotesTextArea == null) {
			jNotesTextArea = new JTextArea();
			jNotesTextArea.setRows(6);
			jNotesTextArea.setLineWrap(true);
			jNotesTextArea.setWrapStyleWord(true);
			jNotesTextArea.setEditable(false);
			jNotesTextArea.setBackground(javax.swing.UIManager
				      .getColor("Button.background"));
		}
		return jNotesTextArea;
	}

	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints26.gridy = 2;
			gridBagConstraints26.weightx = 1.0;
			gridBagConstraints26.gridx = 1;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints25.gridy = 2;
			jUserStateLabel = new JLabel();
			jUserStateLabel.setText("User state:");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.gridy = 0;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.weighty = 1.0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 0;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.gridy = 1;
			gridBagConstraints14.weightx = 1.0;
			GridBagConstraints gridBagConstraints131 = new GridBagConstraints();
			gridBagConstraints131.gridx = 0;
			gridBagConstraints131.gridy = 1;
			gridBagConstraints131.insets = new java.awt.Insets(0,0,0,5);
			jLabel10 = new JLabel();
			jLabel10.setText("Expiration date (DD.MM.YYYY):");
			jLabel9 = new JLabel();
			jLabel9.setText("Notes:");
			jPanel4 = new JPanel();
			jPanel4.setLayout(new GridBagLayout());
			jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			jPanel4.add(getJNotesServerScrollPane(), gridBagConstraints18);
			jPanel4.add(getJExpireTextField(), gridBagConstraints14);
			jPanel4.add(jLabel10, gridBagConstraints131);
			jPanel4.add(jLabel9, gridBagConstraints16);
			jPanel4.add(jUserStateLabel, gridBagConstraints25);
			jPanel4.add(getJStateComboBox(), gridBagConstraints26);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jExpireTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJExpireTextField() {
		if (jExpireTextField == null) {
			jExpireTextField = new JTextField();
		}
		return jExpireTextField;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getJAcceptButton(), null);
			jPanel1.add(getJDeclineButton(), null);
			jPanel1.add(getJPreviousButton(), null);
			jPanel1.add(getJNextButton(), null);
			jPanel1.add(getJCloseButton(), null);
		}
		return jPanel1;
	}

    public static Calendar parseCalendar(String string)
		throws ParseException
	{
		if(string.length()==0) return null;
		
		Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf2=new SimpleDateFormat("dd.MM.yy");
		SimpleDateFormat sdf4=new SimpleDateFormat("dd.MM.yyyy");
		sdf2.setLenient(false);
		sdf4.setLenient(false);
		Date date;
		try
		{
			date=sdf2.parse(string);
		}
		catch(ParseException e)
		{
			date=sdf4.parse(string);
		}
		cal.setTime(date);
		return cal;
	}

	public static String formatCalendar(Calendar cal) {
		if(cal==null) return "No date";
		DateFormat df=DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.GERMAN);
		return df.format(cal.getTime());
	}
	
	/**
	 * This method initializes jAcceptButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJAcceptButton() {
		if (jAcceptButton == null) {
			jAcceptButton = new JButton();
			jAcceptButton.setText("Accept request...");
			jAcceptButton.addActionListener(new java.awt.event.ActionListener() { 
				private String mailString;
				private String mailSubjectString;
				private Calendar expires;
                private int userstate;
				
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					final SwingWorker worker = new SwingWorker() {
						private FinishRequestData finishData;
						private int curReqInd;
						
						public Object construct() {
							try
							{
								curReqInd=reqind;
                                RequestData rd=reqdatas[curReqInd];
                                finishData=new FinishRequestData(
                                        oldUsernames[curReqInd],
                                        rd.getUsername(), rd.getPassword(),
                                        expires, rd.getRealName(),
                                        rd.getEmail(), rd.getEmailSecond(),
                                        rd.getHomepage(), rd.getReason(),
                                        strToN(notesStrs[curReqInd]),
                                        userstate,
                                        mailSubjectString,
                                        mailString);
								
                                adminTool.getJPlagStub().finishAccountRequest(
										finishData);
								return new Object();
							}
							catch(Exception ex)
							{
								adminTool.CheckException(ex,RequestDialog.this);
							}							
							return null;
						}
						
						public void finished() {
							if(get()!=null)
							{
								states[curReqInd] = RequestState.accepted;
								Calendar cal = Calendar.getInstance(
										TimeZone.getTimeZone("GMT"));
								BackedUserData data = new BackedUserData(
										finishData.getUsername(),
										finishData.getPassword(),
										cal,adminTool.getUsername(),
										finishData.getExpires(),null,0,
										finishData.getRealName(),
										finishData.getEmail(),
										finishData.getEmailSecond(),
										finishData.getHomepage(),
										finishData.getReason(),
										finishData.getNotes(),
										userstate);
								adminTool.getUserTableModel().addNewUser(data);
                                if(reqind<reqdatas.length-1)
                                {
                                    reqind++;
                                    setData();
                                }
                                else updateState();
							}
                            else {
                                states[curReqInd] = RequestState.open;
                                updateState();
                            }
						}
					};
					
					// check and set expires
					try
					{
						expires=parseCalendar(getJExpireTextField().getText());

						if(expires!=null)
						{
							Calendar cal=Calendar.getInstance(
									TimeZone.getTimeZone("GMT"));
							if(cal.after(expires))
							{
								JOptionPane.showMessageDialog(
										RequestDialog.this,
										"Please check the expire date field!",
										"Expire date is already expired!",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
					}
					catch(ParseException ex)
					{
						JOptionPane.showMessageDialog(
								RequestDialog.this,
								"Please check the expire date field!",
								"Illegal expire date!",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					RequestData rd = new RequestData(null,
							getJUsernameTextField().getText(),
							getJPasswordTextField().getText(),
							getJRealNameTextField().getText(),
							getJEmailTextField().getText(),
							strToN(getJEmailSecondTextField().getText()),
							strToN(getJHomepageTextField().getText()),
							getJReasonTextArea().getText(),
							strToN(getJNotesServerTextArea().getText()));
					
					// Let user choose the message to be sent to the user
					MailDialog md = new MailDialog(MailDialog.MAIL_ACCEPTED,
							"Choose mail to be sent to the accepted user...",
							rd, RequestDialog.this, adminTool);
					md.setLocationRelativeTo(RequestDialog.this);
					md.setVisible(true);
					
					if(!md.isCancelled())
					{
						mailString = md.getMailString();
						mailSubjectString = md.getMailSubjectString();
                        states[reqind] = RequestState.processing;
                        userstate = BackedUserData.getStateInt(
                            (String) jStateComboBox.getSelectedItem());
                        updateState();
                        updateChanges();
						worker.start();
					}
				}
			});
		}
		return jAcceptButton;
	}

	/**
	 * This method initializes jDeclineButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJDeclineButton() {
		if (jDeclineButton == null) {
			jDeclineButton = new JButton();
			jDeclineButton.setText("Decline request...");
			jDeclineButton.addActionListener(new java.awt.event.ActionListener() { 
				private String mailString;
				private String mailSubjectString;
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					final SwingWorker worker = new SwingWorker() {
						private int curReqInd;
						
						public Object construct() {
							try
							{
								curReqInd=reqind;
                                RequestData rd=reqdatas[curReqInd];
                                FinishRequestData fd=new FinishRequestData(
                                        oldUsernames[curReqInd],
                                        rd.getUsername(), null, null,
                                        rd.getRealName(), rd.getEmail(),
                                        rd.getEmailSecond(), rd.getHomepage(),
                                        rd.getReason(),
                                        strToN(notesStrs[curReqInd]),
                                        null,
                                        mailSubjectString,
                                        mailString);
										
								adminTool.getJPlagStub().finishAccountRequest(fd);
								return new Object();
							}
							catch(Exception ex)
							{
								adminTool.CheckException(ex,RequestDialog.this);
							}							
							return null;
						}
						
						public void finished() {
							if(get()!=null)
							{
								states[curReqInd] = RequestState.declined;
                                if(reqind<reqdatas.length-1)
                                {
                                    reqind++;
                                    setData();
                                }
                                else updateState();
							}
                            else getJProgressBar().setIndeterminate(false);
						}
					};
					RequestData rd = new RequestData(null,
							getJUsernameTextField().getText(),
							getJPasswordTextField().getText(),
							getJRealNameTextField().getText(),
							getJEmailTextField().getText(),
							strToN(getJEmailSecondTextField().getText()),
							strToN(getJHomepageTextField().getText()),
							getJReasonTextArea().getText(),
							strToN(getJNotesServerTextArea().getText()));
					MailDialog md = new MailDialog(MailDialog.MAIL_DECLINED,
							"Choose mail to be sent to the declined user...",
							rd, RequestDialog.this, adminTool);
                    md.setLocationRelativeTo(RequestDialog.this);
					md.setVisible(true);
                    
					if(!md.isCancelled())
					{
						mailString = md.getMailString();
						mailSubjectString = md.getMailSubjectString();
                        states[reqind] = RequestState.processing;
                        updateState();
                        updateChanges();
						worker.start();
					}
				}
			});
		}
		return jDeclineButton;
	}
	
	/**
	 * "Nillable to string"
	 * @return "" if str==null, str otherwise
	 */
	private String nToStr(String str) {
		if(str==null) return "";
		else return str;
	}
	
	/**
	 * "String to nillable"
	 * @return null if str=="", str otherwise
	 */
	private String strToN(String str) {
		if(str.length()==0) return null;
		else return str;
	}
	
	private void updateState() {
		String stateText;
		switch(states[reqind]) {
			case open: stateText = "open"; break;
			case accepted: stateText = "accepted"; break;
			case declined: stateText = "declined"; break;
			case processing: stateText = "processing"; break;
			default: stateText = "buggy *cough*"; break;
		}
        switch(states[reqind]) {
            case open:
                getJProgressBar().setValue(0);
                getJProgressBar().setIndeterminate(false);
                break;
            case accepted:
            case declined:
                getJProgressBar().setValue(100);
                getJProgressBar().setIndeterminate(false);
                break;
            case processing:
                getJProgressBar().setIndeterminate(true);
                break;
        }
		jStateLabel.setText("State of request: " + stateText);
		getJAcceptButton().setEnabled(states[reqind]==RequestState.open);
		getJDeclineButton().setEnabled(states[reqind]==RequestState.open);
	}
	
	private void setData() {
		RequestData rd = reqdatas[reqind];
		getJRequestTimeField().setText(rd.getValidateTime());
		getJRealNameTextField().setText(rd.getRealName());
		getJUsernameTextField().setText(rd.getUsername());
		getJPasswordTextField().setText(rd.getPassword());
		getJEmailTextField().setText(rd.getEmail());
		getJEmailSecondTextField().setText(nToStr(rd.getEmailSecond()));
		getJHomepageTextField().setText(nToStr(rd.getHomepage()));
		getJReasonTextArea().setText(rd.getReason());
        getJReasonTextArea().setCaretPosition(0);
		getJNotesTextArea().setText(nToStr(rd.getNotes()));
        getJNotesTextArea().setCaretPosition(0);
        getJNotesServerTextArea().setText(notesStrs[reqind]);
        getJNotesServerTextArea().setCaretPosition(0);
		getJExpireTextField().setText(expireStrs[reqind]);
		
		jRequestNumLabel.setText("Request " + (reqind+1) + "/"
				+ reqdatas.length);
		updateState();
		getJPreviousButton().setEnabled(reqind!=0);
		getJNextButton().setEnabled(reqind<reqdatas.length-1);
	}

	private void updateChanges() {
		RequestData rd=reqdatas[reqind];
		rd.setRealName(getJRealNameTextField().getText());
		rd.setUsername(getJUsernameTextField().getText());
		rd.setPassword(getJPasswordTextField().getText());
		rd.setEmail(getJEmailTextField().getText());
		rd.setEmailSecond(strToN(getJEmailSecondTextField().getText()));
		rd.setHomepage(strToN(getJHomepageTextField().getText()));
        notesStrs[reqind]=getJNotesServerTextArea().getText();
		expireStrs[reqind]=getJExpireTextField().getText();
	}

	/**
	 * This method initializes jPreviousButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJPreviousButton() {
		if (jPreviousButton == null) {
			jPreviousButton = new JButton();
			jPreviousButton.setText("Show previous");
			jPreviousButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if(reqind>0)
					{
						updateChanges();
						reqind--;
						setData();
					}
				}
			});
		}
		return jPreviousButton;
	}

	/**
	 * This method initializes jNextButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJNextButton() {
		if (jNextButton == null) {
			jNextButton = new JButton();
			jNextButton.setText("Show next");
			jNextButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if(reqind<reqdatas.length-1)
					{
						updateChanges();
						reqind++;
						setData();
					}
				}
			});
		}
		return jNextButton;
	}

	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText("Close");
			jCloseButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
                    doClose();
				}
			});
		}
		return jCloseButton;
	}

	/**
	 * This method initializes jNotesServerScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJNotesServerScrollPane() {
		if (jNotesServerScrollPane == null) {
			jNotesServerScrollPane = new JScrollPane();
			jNotesServerScrollPane.setViewportView(getJNotesServerTextArea());
		}
		return jNotesServerScrollPane;
	}

	/**
	 * This method initializes jNotesServerTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJNotesServerTextArea() {
		if (jNotesServerTextArea == null) {
			jNotesServerTextArea = new JTextArea();
			jNotesServerTextArea.setRows(3);
			jNotesServerTextArea.setLineWrap(true);
			jNotesServerTextArea.setWrapStyleWord(true);
		}
		return jNotesServerTextArea;
	}

    /**
     * This method initializes jShowHomepageButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJShowHomepageButton() {
        if(jShowHomepageButton == null) {
            jShowHomepageButton = new JButton();
            jShowHomepageButton.setText("Show");
            jShowHomepageButton.setPreferredSize(new java.awt.Dimension(40,18));
            jShowHomepageButton.setMargin(new java.awt.Insets(2,2,2,2));
            jShowHomepageButton.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
            jShowHomepageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    AdminTool.showHomepage(getJHomepageTextField().getText(),
                        RequestDialog.this);
                }
            });
        }
        return jShowHomepageButton;
    }

    /**
     * This method initializes jShowMailProviderButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJShowMailProviderButton() {
        if(jShowMailProviderButton == null) {
            jShowMailProviderButton = new JButton();
            jShowMailProviderButton.setText("Show");
            jShowMailProviderButton.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
            jShowMailProviderButton.setMargin(new java.awt.Insets(2,2,2,2));
            jShowMailProviderButton.setPreferredSize(new java.awt.Dimension(40,18));
            jShowMailProviderButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String provider = getJEmailTextField().getText();
                    provider = "http://www."
                        + provider.substring(provider.indexOf('@')+1);
                    AdminTool.showHomepage(provider, RequestDialog.this);
                }
            });
        }
        return jShowMailProviderButton;
    }

    /**
     * This method initializes jStateComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox<String> getJStateComboBox() {
        if(jStateComboBox == null) {
			jStateComboBox = new JComboBox<String>(BackedUserData.getStateNameArray(
                BackedUserData.USER_JPLAGADMIN));
        }
        return jStateComboBox;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
