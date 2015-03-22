/*
 * Created on 06.03.2005
 */
package swingClient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.soap.SOAPFaultException;

import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.LanguageInfo;
import jplagWsClient.jplagClient.Option;
import jplagWsClient.jplagClient.ServerInfo;
import jplagWsClient.jplagClient.Status;
import jplagWsClient.jplagClient.Submission;
import jplagWsClient.jplagClient.UserInfo;
import jplagWsClient.util.JPlagClientAccessHandler;

/**
 * @author Moritz Kroll
 */
public class JplagSwingClient extends JFrame {
	private static final long serialVersionUID = 1L;

	/*
	 * -1 = invalid userid 0 = in queue -> progress=position in queue 100 =
	 * parsing -> progress=percentage 101 =parsing with warning see report file
	 * 200 = comparing -> progress=percentage 201 = packaging result 300 = done,
	 * result is ready for download 400 = unknown Error or many errors occured
	 * see Report for more Information 401 = error =Bad Language 402 = error =
	 * No enough submissions for all states between 400 and 499 see report for
	 * more information
	 */

	public static final int INVALID = -1;
	public static final int INQUEUE = 0;
	public static final int PARSING = 100;
	public static final int PARSING_WARNING = 101;
	public static final int COMPARING = 200;
	//    public static final int WRITING_RESULTS=3;
	//    public static final int PACKAGING_RESULTS=201;
	public static final int GENERATING_RESULT_FILES = 250;
	public static final int DONE = 300;
	public static final int ERROR = 400;
	public static final int ERROR_BADLANG = 401;
	public static final int ERROR_TOOFEWSUBS = 402;

	private Status curStatus = null;
	private Timer statusTimer = null;

	private javax.swing.JPanel jContentPane = null;

	private JLabel resultDir_label = null; //  @jve:decl-index=0:visual-constraint="65,223"
	private JTextField jResultDir = null;
	private JPanel submission_panel = null;
	private JTextField sourceDir = null;
	private JPanel jPanel1 = null;
	private JButton getServerInfo_button = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JButton getResults_button = null;
	private JProgressBar jProgressBar = null;
	private JLabel submissionNumber_label = null;
	private JTextField jSubmissionNum = null;
	private JPanel jPanel4 = null;
	private JTextField jStatusText = null;

	private JLabel compareSourceDir_label = null;
	private JPlagTyp_Stub stub = null;

	private JPanel jPanel = null;
	private JButton showResults_Button = null;
	private JScrollPane jScrollPane = null;
	private JEditorPane jEditorPane = null;

	private JPlagClientAccessHandler accessHandler = null;

	public JPlagTyp_Stub getJplagStub() {
		if (stub == null) {
			stub = (JPlagTyp_Stub) (new JPlagService_Impl().getJPlagServicePort());

			HandlerChain handlerchain = stub._getHandlerChain();
			@SuppressWarnings("unchecked")
			Iterator<Handler> handlers = handlerchain.iterator();
			while (handlers.hasNext()) {
				Handler handler = handlers.next();
				if (handler instanceof JPlagClientAccessHandler) {
					accessHandler = ((JPlagClientAccessHandler) handler);
					break;
				}
			}
		}
		if (accessHandler != null) {
			accessHandler.setUserPassObjects(getJUsernameField().getText(), String.valueOf(getJPasswordField().getPassword()));
		}
		return stub;
	}

	public boolean CheckConnectException(RemoteException re) {
		Throwable cause = re.getCause();
		if (cause != null && cause instanceof com.sun.xml.rpc.client.ClientTransportException) {
			cause = ((com.sun.xml.rpc.util.exception.JAXRPCExceptionBase) cause).getLinkedException();
			if (cause != null) {
				getStatusTextfield().setText(cause.getMessage());
				return true;
			}
		}
		return false;
	}

	private void CheckRemoteException(java.rmi.RemoteException re) {
		if (!CheckConnectException(re)) {
			getJEditorPane().setContentType("text/plain");
			getJEditorPane().setText("Unexpected RemoteException: " + re.getMessage());
			re.printStackTrace();
		}
	}

	private void CheckException(Exception ex) {
		if (ex instanceof JPlagException) {
			JPlagException jex = (JPlagException) ex;

			getJEditorPane().setContentType("text/plain");
			getJEditorPane().setText(jex.getExceptionType() + " at " + new Date() + "\n" + jex.getDescription() + "\n" + jex.getRepair());

			getStatusTextfield().setText(jex.getExceptionType());
		} else if (ex instanceof RemoteException) {
			CheckRemoteException((RemoteException) ex);
		} else if (ex instanceof SOAPFaultException) {
			SOAPFaultException se = (SOAPFaultException) ex;
			getJEditorPane().setContentType("text/plain");
			getJEditorPane().setText(
					"Unexpected SOAPFaultException: " + se.getFaultActor() + "\n" + se.getFaultString() + "\n" + se.getMessage() + "\n"
							+ se.getDetail());
			getStatusTextfield().setText(se.getFaultActor());
		} else {
			getStatusTextfield().setText(ex.getClass().getName() + " : " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * This is the default constructor
	 */
	public JplagSwingClient() {
		super();
		initialize();

		//		Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(650, 440);
		this.setContentPane(getJContentPane());
		this.setTitle("JPlag Webservice Client");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			resultDir_label = new JLabel();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new BorderLayout());
			resultDir_label.setText("Result directory:");
			resultDir_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			resultDir_label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			resultDir_label.setName("jLabel1");
			resultDir_label.setPreferredSize(new java.awt.Dimension(129, 16));
			jContentPane.add(getJPanel4(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jLanguageField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJResultDir() {
		if (jResultDir == null) {
			jResultDir = new JTextField();
			jResultDir.setColumns(15);
			jResultDir.setText((arguments.length >= 2) ? arguments[1] : "i:\\blub\\boob");
		}
		return jResultDir;
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSubmissionPanel() {
		if (submission_panel == null) {
			soapCommLogFile_label = new JLabel();
			submissionTitle_label = new JLabel();
			compareSourceDir_label = new JLabel();
			submissionNumber_label = new JLabel();
			GridLayout gridLayout13 = new GridLayout();
			submission_panel = new JPanel();
			submission_panel.setLayout(gridLayout13);
			gridLayout13.setRows(5);
			gridLayout13.setHgap(5);
			gridLayout13.setVgap(5);
			submission_panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
			submissionNumber_label.setText("Submission number:");
			compareSourceDir_label.setText("Directory containing directories/files to compare:");
			submissionTitle_label.setText("Submission title (user defined):");
			soapCommLogFile_label.setText("SOAP communication log file (for client development):");
			submission_panel.add(compareSourceDir_label, null);
			submission_panel.add(getJPanel2(), null);
			submission_panel.add(submissionTitle_label, null);
			submission_panel.add(getJTitleField(), null);
			submission_panel.add(resultDir_label, null);
			submission_panel.add(getJResultDir(), null);
			submission_panel.add(submissionNumber_label, null);
			submission_panel.add(getJSubmissionNum(), null);
			submission_panel.add(soapCommLogFile_label, null);
			submission_panel.add(getJLogFilePane(), null);
		}
		return submission_panel;
	}

	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSourceDir() {
		if (sourceDir == null) {
			sourceDir = new JTextField();
			sourceDir.setColumns(15);
			sourceDir.setText(arguments.length >= 2 ? arguments[0] : "i:\\blub\\boob\\jplag");
		}
		return sourceDir;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getGetServerInfoButton(), null);
			jPanel1.add(getCompareSourceButton(), null);
			jPanel1.add(getGetStatusButton(), null);
			jPanel1.add(getGetResultsButton(), null);
			jPanel1.add(getShowResultsButton(), null);
			jPanel1.add(getDeleteSubmissionButton(), null);
		}
		return jPanel1;
	}

	private String formatCalendar(Calendar cal) {
		if (cal == null)
			return "No date";
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
		return df.format(cal.getTime());
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGetServerInfoButton() {
		if (getServerInfo_button == null) {
			getServerInfo_button = new JButton();
			getServerInfo_button.setText("Get server info");
			getServerInfo_button.setMargin(new java.awt.Insets(2, 8, 2, 8));
			getServerInfo_button.setPreferredSize(new Dimension(105, 26));
			getServerInfo_button.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getJEditorPane().setContentType("text/plain");
						getJEditorPane().setText("");
						getStatusTextfield().setText("Connecting to server...");
						ServerInfo serverInfo = getJplagStub().getServerInfo();
						//						String usageString=getJplagStub().usage(0);
						getJEditorPane().setContentType("text/plain");
						//						getJEditorPane().setText(usageString);

						String lstr = "";
						LanguageInfo[] langs = serverInfo.getLanguageInfos();
						JComboBox<String> cb = getJLanguageCB();
						for (int i = 0; i < langs.length; i++) {
							lstr += "\nLanguage: " + langs[i].getName() + "\nSuffixes: ";
							cb.addItem(langs[i].getName());
							String[] suf = langs[i].getSuffixes();
							for (int j = 0; j < suf.length; j++)
								lstr += suf[j] + ((j < suf.length - 1) ? "," : "");
							lstr += "\nDefault minimum token length: " + langs[i].getDefMinMatchLen() + "\n";
						}
						cb.setEnabled(true);

						String str = "";
						Submission[] subs = serverInfo.getSubmissions();
						for (int i = 0; i < subs.length; i++) {
							str += "Submission ID: " + subs[i].getSubmissionID() + " Title: " + subs[i].getTitle() + " Date: "
									+ subs[i].getDate() + " Laststate: " + subs[i].getLastState() + "\n";
						}

						UserInfo uinf = serverInfo.getUserInfo();
						getJEditorPane().setText(
								"Left submission slots: " + serverInfo.getUserInfo().getLeftSubmissionSlots() + "\nAccount expires on: "
										+ formatCalendar(uinf.getExpires()) + "\nPrimary email: " + uinf.getEmail() + "\nSecondary email: "
										+ uinf.getEmailSecond() + "\nHomepage: " + uinf.getHomepage() + "\nList of languages:\n" + lstr
										+ "\nList of submissions on server:\n" + str);
						getStatusTextfield().setText("Server info received");
					} catch (Exception ex) {
						CheckException(ex);
					}
				}
			});
		}
		return getServerInfo_button;
	}

	private JButton getCompareSourceButton() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Compare source");
			jButton1.setMargin(new java.awt.Insets(2, 8, 2, 8));
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getJLanguageCB().getSelectedItem() == null) {
						javax.swing.JOptionPane.showMessageDialog(JplagSwingClient.this, "Please select a language!\n"
								+ "You can get a list of available languages\n" + "by pressing the \"Get server info\" button.",
								"No language specified", javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					File tempfile = null;
					{
						File sourceDir = new File(getSourceDir().getText());
						if (!sourceDir.isDirectory()) {
							javax.swing.JOptionPane.showMessageDialog(JplagSwingClient.this, "The source directory \""
									+ getSourceDir().getText() + "\" is not a directory!", "Wrong source given:",
									javax.swing.JOptionPane.ERROR_MESSAGE);
							return;
						}
						getStatusTextfield().setText("Compressing content...");
						try {
							tempfile = ZipUtil.zipTo(sourceDir, File.createTempFile("jplag", ".zip"));
						} catch (java.io.IOException ex) {
							getStatusTextfield().setText("IOException: " + ex.getMessage());
							return;
						}
						System.out.println(tempfile.getName());
					}
					//					MimeMultipart result=null;
					String result = null;

					System.out.println("Entering try block...");

					try {
						FileDataSource fds = new FileDataSource(tempfile);

						// Construct a MimeBodyPart

						// Add Part on the .....

						MimeMultipart mmp = new MimeMultipart();
						MimeBodyPart mbp = new MimeBodyPart();
						mbp.setDataHandler(new DataHandler(fds));
						mbp.setFileName(tempfile.getName());

						mmp.addBodyPart(mbp);

						System.out.println("Creating Option object...");

						// Prepare Options

						// @formatter:off
						Option option = new Option((String) getJLanguageCB().getSelectedItem(), // language
						null, // comparisonMode
						0, // miminum match length
						null, //suffixes
						true, // readSubdirs
						null, // path to files
						null, // basecode dir
						null, // store matches
						"avr", // clustertype
						"en", // country lang (used for message localization)
						getJTitleField().getText(), // title
						getSourceDir().getText() // original dir
						);
						// @formatter:on
						// running Jplag

						System.out.println("Calling compareSource...");

						getStatusTextfield().setText("Calling server...");

						result = getJplagStub().compareSource(option, mmp);
						getJEditorPane().setContentType("text/plain");
						getJEditorPane().setText(
								"\n" + "############# JPLAG-RESULT: REQUEST=compareSource()  ##############" + "\nResult= " + result);
						getStatusTextfield().setText("Call successful, removing temporary data...");
						tempfile.delete();
						getStatusTextfield().setText("Waiting for server status...");

						jSubmissionNum.setText(result);
						if (statusTimer != null)
							statusTimer.stop();
						statusTimer = new Timer(10000, new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent evt) {
								invokeGetStatus();
								if (curStatus != null
										&& (curStatus.getState() == DONE || curStatus.getState() >= ERROR || curStatus.getState() == INVALID)) {
									statusTimer.stop();
									statusTimer = null;
								}
							}
						});
						statusTimer.start();
					} catch (Exception ex) {
						CheckException(ex);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			});
		}
		return jButton1;
	}

	private void invokeGetStatus() {
		try {
			curStatus = getJplagStub().getStatus(getJSubmissionNum().getText());
			String str;
			switch (curStatus.getState()) {
			case INVALID:
				str = "Invalid submission number!";
				break;
			case INQUEUE:
				str = "Submission is waiting in queue...";
				break;
			case PARSING:
				str = "Submission is being parsed...";
				break;
			case COMPARING:
				str = "Server is comparing files...";
				break;
			case GENERATING_RESULT_FILES:
				str = "Server is generating result files...";
				break;
			//				case PACKAGING_RESULTS: str="Results become packaged..."; break;
			case DONE:
				str = "Submission finished!";
				getJEditorPane().setContentType("text/plain");
				getJEditorPane().setText(curStatus.getReport());
				break;
			case ERROR_BADLANG:
				str = "Illegal language supplied!";
				getJEditorPane().setContentType("text/plain");
				getJEditorPane().setText(curStatus.getReport());
				break;
			case ERROR_TOOFEWSUBS:
				str = "Not enough valid files!";
				getJEditorPane().setContentType("text/plain");
				getJEditorPane().setText(curStatus.getReport());
				break;
			default:
				if (curStatus.getState() >= ERROR) {
					str = "Unknown error: " + curStatus.getState();
				} else
					str = "Unknown state: " + curStatus.getState();
				getJEditorPane().setContentType("text/plain");
				getJEditorPane().setText(curStatus.getReport());
				break;
			}
			getStatusTextfield().setText(str + " (State=" + curStatus.getState() + ")");
			getJProgressBar().setMaximum(100);
			getJProgressBar().setValue(curStatus.getProgress());
		} catch (Exception ex) {
			CheckException(ex);
			if (curStatus != null)
				curStatus.setState(INVALID);
		}
	}

	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGetStatusButton() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Get status");
			jButton2.setMargin(new java.awt.Insets(2, 8, 2, 8));
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//					javax.swing.SwingUtilities.invokeLater()
					invokeGetStatus();
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGetResultsButton() {
		if (getResults_button == null) {
			getResults_button = new JButton();
			getResults_button.setText("Get results");
			getResults_button.setMargin(new java.awt.Insets(2, 8, 2, 8));
			getResults_button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MimeMultipart inputZipFile = null;

					try {
						inputZipFile = getJplagStub().getResult(getJSubmissionNum().getText());
					} catch (Exception ex) {
						CheckException(ex);
						return;
					}

					File resultdir = new File(getJResultDir().getText());
					resultdir.mkdirs();
					File result = new File(getJResultDir().getText() + "/jplagResult.zip");
					try {
						if (inputZipFile == null)
							return;
						MimeBodyPart bdp = (MimeBodyPart) inputZipFile.getBodyPart(0);
						System.out.println("Content Type  " + bdp.getContentType());

						DataHandler dh = bdp.getDataHandler();
						FileOutputStream os = new FileOutputStream(result);
						dh.writeTo(os);
						os.close();

						ZipUtil.unzip(result, getJResultDir().getText(), "jplagResult");

						getStatusTextfield().setText("Files unzipped!");
					} catch (Exception ex) {
						getStatusTextfield().setText(ex.getMessage());
						ex.printStackTrace();
					}
					String report = "\n"
							+ "\n"
							+ ((result == null) ? "compareSource was not successfull  sorry"
									: "WAOOOOOOOUUUUU  ******** CompareSource was succesfull**********") + "\n" + "\n";

					System.out.println(report);

				}
			});
		}
		return getResults_button;
	}

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jUsernameField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJSubmissionNum() {
		if (jSubmissionNum == null) {
			jSubmissionNum = new JTextField();
		}
		return jSubmissionNum;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			jPanel4.add(getStatusTextfield(), null);
			jPanel4.add(getJProgressBar(), null);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getStatusTextfield() {
		if (jStatusText == null) {
			jStatusText = new JTextField();
			jStatusText.setColumns(30);
		}
		return jStatusText;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
			jPanel.add(getSubmissionPanel(), null);
			jPanel.add(getConfigPanel(), null);
			jPanel.add(getJPanel1(), null);
		}
		return jPanel;
	}

	/*
	 * private static Object getBasicServiceObject() { try { Class
	 * serviceManagerClass=Class.forName("javax.jnlp.ServiceManager");
	 * java.lang.reflect.Method lookupMethod=serviceManagerClass.getMethod(
	 * "lookup",new Class [] { String.class });
	 * 
	 * return lookupMethod.invoke(null, new Object [] {
	 * "javax.jnlp.BasicService" } ); } catch(Exception ex) {
	 * ex.printStackTrace(); return null; } }
	 * 
	 * private static Class getBasicServiceClass() { try { return
	 * Class.forName("javax.jnlp.BasicService"); } catch(Exception ex) {
	 * ex.printStackTrace(); return null; } }
	 */

	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getShowResultsButton() {
		if (showResults_Button == null) {
			showResults_Button = new JButton();
			showResults_Button.setText("Show results");
			showResults_Button.setMargin(new java.awt.Insets(2, 8, 2, 8));
			showResults_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					/*
					 * if(basicServiceObject==null || basicServiceClass==null) {
					 * JOptionPane.showMessageDialog(null,"Cannot use JNLP" +
					 * " to launch your default browser!","Error:",
					 * JOptionPane.WARNING_MESSAGE); return; } try {
					 * java.lang.reflect.Method
					 * method=basicServiceClass.getMethod( "showDocument", new
					 * Class [] { java.net.URL.class } ); Boolean
					 * resultBoolean=(Boolean) method.invoke(
					 * basicServiceObject, new Object [] {
					 * "e:\\blub\\boob\\jplagResult\\index.html" });
					 * if(!resultBoolean.booleanValue()) {
					 * JOptionPane.showMessageDialog(null,"Unable to open" +
					 * " results!"); } } catch(Exception ex) {
					 * getJStatusText().setText(ex.getMessage());
					 * ex.printStackTrace(); }
					 */
					try {
						//						getJEditorPane().setPage("file:\\\\\\" +
						//							getJResultDir().getText()+"\\jplagResult\\index.html");
						File file = new File(getJResultDir().getText() + File.separator + "jplagResult" + File.separator + "index.html");
						/*
						 * getJEditorPane().setPage("file:" + File.separator +
						 * getJResultDir
						 * ().getText()+"\\jplagResult\\index.html");
						 */
						getJEditorPane().setPage(file.toURI().toURL());
					} catch (java.io.IOException ex) {
						getStatusTextfield().setText(ex.getMessage());
						ex.printStackTrace();
					}
				}
			});
		}
		return showResults_Button;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJEditorPane());
		}
		return jScrollPane;
	}

	private String getFrameName(String str) {
		if (str.equals("1"))
			return "top";
		if (str.equals("2"))
			return "0";
		if (str.equals("3"))
			return "1";
		return null;
	}

	/**
	 * This method initializes jEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditable(false);
			jEditorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					/*
					 * java.net.URL dest=e.getURL(); if(dest==null) {
					 * getJStatusText().setText("URL = null!"); } else {
					 * getJStatusText
					 * ().setText(""+e.getURL().getFile()+"  "+e.getURL
					 * ().getRef()); System.out.println(dest.getProtocol());
					 * System.out.println(dest.getHost());
					 * System.out.println(dest.getPath());
					 * System.out.println(dest.getFile()); }
					 */
					getStatusTextfield().setText(e.getDescription());
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						JEditorPane pane = (JEditorPane) e.getSource();
						if (e instanceof HTMLFrameHyperlinkEvent) {
							HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
							HTMLDocument doc = (HTMLDocument) pane.getDocument();
							String url = e.getDescription();
							if (url.startsWith("javascript:ZweiFrames")) {
								String params[] = url.split("\\('|',|,'|\\)");
								for (int i = 0; i < params.length; i++)
									System.out.println(params[i]);
								try {
									HTMLFrameHyperlinkEvent evt1 = new HTMLFrameHyperlinkEvent(e.getSource(),
											HyperlinkEvent.EventType.ACTIVATED, new java.net.URL(new File(getJResultDir().getText()
													+ File.separator + "jplagResult" + File.separator).toURI().toString()
													+ params[1]),
											//													new java.net.URL("file:\\\\\\" +
											//													getJResultDir().getText()+"\\jplagResult\\"+params[1]),
											getFrameName(params[2]));

									/*
									 * System.out.println(new java.net.URL( new
									 * File(getJResultDir().getText()+
									 * File.separator+"jplagResult"+
									 * File.separator)
									 * .toURI().toString()+params[1]));
									 * 
									 * System.out.println(new
									 * java.net.URL("file:\\\\\\" +
									 * getJResultDir
									 * ().getText()+"\\jplagResult\\"
									 * +params[1]));
									 */

									doc.processHTMLFrameHyperlinkEvent(evt1);
									HTMLFrameHyperlinkEvent evt2 = new HTMLFrameHyperlinkEvent(e.getSource(),
											HyperlinkEvent.EventType.ACTIVATED, new java.net.URL(new File(getJResultDir().getText()
													+ File.separator + "jplagResult" + File.separator).toURI().toString()
													+ params[3]),
											//										new java.net.URL("file:\\\\\\" +
											//												getJResultDir().getText()+"\\jplagResult\\"+params[3]),
											getFrameName(params[4]));
									doc.processHTMLFrameHyperlinkEvent(evt2);
									return;
								} catch (java.net.MalformedURLException ex) {
									ex.printStackTrace();
								}
							}
							doc.processHTMLFrameHyperlinkEvent(evt);
						} else {
							try {
								pane.setPage(e.getURL());
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}
			});
		}
		return jEditorPane;
	}

	private static String[] arguments;
	private JButton fileOpenButton = null;
	private JPanel jPanel2 = null;
	private JPanel config_panel = null;
	private JLabel username_label = null;
	private JTextField jUsernameField = null;
	private JLabel password_label = null;
	private JPasswordField jPasswordField = null;
	private JLabel language_label = null;
	private JButton advancedOptions_button = null;
	private JLabel submissionTitle_label = null;
	private JTextField jTitleField = null;
	private JComboBox<String> language_combobox = null;
	private JButton deleteSubmission_button = null;
	private JLabel soapCommLogFile_label = null;
	private JTextField jLogFilePane = null;

	/**
	 * This method initializes jButton5
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFileOpenButton() {
		if (fileOpenButton == null) {
			fileOpenButton = new JButton();
			fileOpenButton.setText("...");
			fileOpenButton.setPreferredSize(new java.awt.Dimension(20, 20));
			fileOpenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser(getSourceDir().getText());
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int retval = chooser.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION) {
						getSourceDir().setText(chooser.getSelectedFile().getPath());
					}
				}
			});
		}
		return fileOpenButton;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.X_AXIS));
			jPanel2.add(getSourceDir(), null);
			jPanel2.add(getFileOpenButton(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConfigPanel() {
		if (config_panel == null) {
			language_label = new JLabel();
			password_label = new JLabel();
			username_label = new JLabel();
			config_panel = new JPanel();
			username_label.setText("Username:");
			password_label.setText("Password:");
			language_label.setText("Langauge:");
			config_panel.add(username_label, null);
			config_panel.add(getJUsernameField(), null);
			config_panel.add(password_label, null);
			config_panel.add(getJPasswordField(), null);
			config_panel.add(language_label, null);
			config_panel.add(getJLanguageCB(), null);
			config_panel.add(getAdvancedOptionsButton(), null);
		}
		return config_panel;
	}

	/**
	 * This method initializes jUsernameField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJUsernameField() {
		if (jUsernameField == null) {
			jUsernameField = new JTextField();
			jUsernameField.setColumns(8);
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
			jPasswordField.setColumns(6);
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jButton6
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAdvancedOptionsButton() {
		if (advancedOptions_button == null) {
			advancedOptions_button = new JButton();
			advancedOptions_button.setText("Advanced options...");
			advancedOptions_button.setPreferredSize(new java.awt.Dimension(132, 20));
			advancedOptions_button.setMargin(new java.awt.Insets(2, 8, 2, 8));

			advancedOptions_button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.err.println("No advanced options implemented yet...");
				}

			});

		}
		return advancedOptions_button;
	}

	/**
	 * This method initializes jTitleField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTitleField() {
		if (jTitleField == null) {
			jTitleField = new JTextField();
		}
		return jTitleField;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getJLanguageCB() {
		if (language_combobox == null) {
			language_combobox = new JComboBox<String>();
			language_combobox.setPreferredSize(new java.awt.Dimension(80, 20));
			language_combobox.setEditable(true);
			language_combobox.setMinimumSize(new java.awt.Dimension(31, 20));
			language_combobox.setEnabled(false);
		}
		return language_combobox;
	}

	/**
	 * This method initializes jButton7
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteSubmissionButton() {
		if (deleteSubmission_button == null) {
			deleteSubmission_button = new JButton();
			deleteSubmission_button.setText("Delete submission");
			deleteSubmission_button.setMargin(new java.awt.Insets(2, 8, 2, 8));
			deleteSubmission_button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getJplagStub().cancelSubmission(getJSubmissionNum().getText());
						getStatusTextfield().setText("Submission successfully deleted");
						if (statusTimer != null)
							statusTimer.stop();
					} catch (Exception ex) {
						CheckException(ex);
					}
				}
			});
		}
		return deleteSubmission_button;
	}

	/**
	 * This method initializes the text field for the log messages
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJLogFilePane() {
		if (jLogFilePane == null) {
			jLogFilePane = new JTextField();
			if (System.getProperty("jplagClient.logfile") == null) {
				System.setProperty("jplagClient.logfile", System.getProperty("user.home") + File.separator + "jplagmessages.log");
				System.out.println("jplagClient.logfile not set! log file name guessed");
			}
			System.out.println("log file: " + System.getProperty("jplagClient.logfile"));
			jLogFilePane.setText(System.getProperty("jplagClient.logfile"));
		}
		return jLogFilePane;
	}

	public static void main(String[] args) {
		arguments = args;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JplagSwingClient me = new JplagSwingClient();
				me.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				me.setVisible(true);
			}
		});
	}
}
