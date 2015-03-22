/*
 * Created on 20.05.2005
 * Author: Moritz Kroll
 */

package jplagAdminTool;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagUtils.DesktopUtils;
import jplagUtils.PropertiesLoader;
import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.RequestData;
import jplagWsClient.jplagClient.RequestDataArray;
import jplagWsClient.jplagClient.SetUserDataParams;
import jplagWsClient.jplagClient.UserDataArray;
import jplagWsClient.util.JPlagClientAccessHandler;

public class AdminTool extends JFrame implements TableModelListener {
	private static final long serialVersionUID = -8979800871645147201L;

	private static final Properties versionProps = PropertiesLoader.loadProps("jplagAdminTool/version.properties");
	private static final String VERSION_STRING = versionProps.getProperty("version", "devel");
	private static final String TITLE_STRING = "JPlag AdminTool";

	private JPlagTyp_Stub stub = null;
	private JPlagClientAccessHandler accessHandler = null;

	private UserTableModel userTableModel = null;
	private TableSorter tableSorter = null;
	private Vector changedUsersList = new Vector(3, 5);

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

	private javax.swing.JPanel jContentPane = null;

	private JTable jUserTable = null;
	private JPanel jPanel = null;
	private JButton jRefreshButton = null;

	private int reqsWaiting = 0;
	private RequestDialog requestDlg = null;

	private String username = null;
	private int userstate = 0;

	private JButton jChangePassButton = null;

	private JPanel jPanel1 = null;

	private JScrollPane jScrollPane = null;

	private JButton jAddUserButton = null;

	private JButton jDeleteUserButton = null;
	private JButton jRequestButton = null;
	private JButton jServerMailsButton = null;
	private JButton jDetailsButton = null;

	/**
	 * This is the default constructor
	 */
	public AdminTool() {
		super();

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
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
		setSize(800, 450);
		setContentPane(getJContentPane());
		updateTitle();
	}

	/**
	 * Updates the program title using the current username and the current
	 * number of users
	 */
	public void updateTitle() {
		setTitle(TITLE_STRING + " " + VERSION_STRING + " - " + username + " - " + getUserTableModel().getRowCount() + " users");
	}

	/**
	 * Returns a singleton JPlag stub
	 * 
	 * @return The JPlag stub object
	 */
	public JPlagTyp_Stub getJPlagStub() {
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
		return stub;
	}

	public String getUsername() {
		return username;
	}

	public int getUserState() {
		return userstate;
	}

	public void updateLogin(String user, String pass) {
		accessHandler.setUserPassObjects(user, pass);
		username = user;
	}

	/**
	 * Sets the login data in the access handler, gets the user data array and
	 * for JPlag admins the account requests and initializes the main window
	 * starting the real application
	 * 
	 * @param user
	 *            The username
	 * @param pass
	 *            The password
	 * @param window
	 *            Parent window for error messages
	 * @return
	 */
	public boolean setLogin(String user, String pass, Component window) {
		username = user;
		getJPlagStub(); // make sure a stub has been created
		if (accessHandler == null) {
			JOptionPane.showMessageDialog(window, "Unable to set login data!", "Access handler not found!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		accessHandler.setUserPassObjects(user, pass);
		try {
			UserDataArray userarray = getJPlagStub().getUserDataArray(0);
			getUserTableModel().setUserDataArray(userarray);
			userstate = getUserTableModel().getUserState(username);
			getUserTableModel().setAdminState(userstate);
			changedUsersList.clear();
			if ((userstate & BackedUserData.MASK_JPLAGADMIN) != 0) {
				reqsWaiting = getJPlagStub().getAccountRequests(true).getItems().length;
			}
			initialize();
			getJAddUserButton().setEnabled(true);
			return true;
		} catch (Exception ex) {
			CheckException(ex, window);
		}
		return false;
	}

	/*
	 * Some exception evaluation functions
	 */

	public boolean CheckConnectException(RemoteException re, Component comp) {
		Throwable cause = re.getCause();
		if (cause != null && cause instanceof com.sun.xml.rpc.client.ClientTransportException) {
			cause = ((com.sun.xml.rpc.util.exception.JAXRPCExceptionBase) cause).getLinkedException();
			if (cause != null) {
				JOptionPane.showMessageDialog(comp, cause.getMessage(), "Connect exception!", JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
		return false;
	}

	private void CheckRemoteException(java.rmi.RemoteException re, Component comp) {
		if (!CheckConnectException(re, comp)) {
			JOptionPane.showMessageDialog(comp, "Unexpected RemoteException: " + re.getMessage(), "Remote exception!",
					JOptionPane.ERROR_MESSAGE);
			re.printStackTrace();
		}
	}

	public void CheckException(Exception ex, Component comp) {
		if (ex instanceof JPlagException) {
			JPlagException jex = (JPlagException) ex;
			JOptionPane.showMessageDialog(comp, jex.getDescription() + "\n" + jex.getRepair(), jex.getExceptionType(),
					JOptionPane.ERROR_MESSAGE);
		} else if (ex instanceof RemoteException) {
			CheckRemoteException((RemoteException) ex, comp);
		} else {
			JOptionPane.showMessageDialog(comp, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	/* end of exception evaluation functions */

	public UserTableModel getUserTableModel() {
		if (userTableModel == null) {
			userTableModel = new UserTableModel();
		}
		return userTableModel;
	}

	public boolean userDataChanged(BackedUserData bud) {
		if (!bud.checkValid(getUserTableModel(), this, true))
			return false;
		try {
			if (username.equals(bud.getOrigUsername()) && (bud.getState() & BackedUserData.MASK_JPLAGADMIN) == 0) {
				if (JOptionPane.showConfirmDialog(this, "Do you really want to remove your admin rights???\n"
						+ "You won't be able to undo this without help of\n" + "another admin or access to the server!", "Confirm suicide",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					bud.setState(userstate);
					return false;
				}
			}

			getJPlagStub().setUserData(new SetUserDataParams(bud, bud.getOrigUsername()));
			if (username.equals(bud.getOrigUsername()))
				updateLogin(bud.getUsername(), bud.getPassword());

			bud.updateBackup();
			return true;
		} catch (Exception ex) {
			CheckException(ex, AdminTool.this);
			if (ex instanceof JPlagException)
				bud.resetChanges();
		}
		return false;
	}

	public void tableChanged(TableModelEvent e) {
		if (e.getColumn() == TableModelEvent.ALL_COLUMNS)
			return;
		BackedUserData bud = getUserTableModel().getBackedUserData(e.getFirstRow());
		userDataChanged(bud);
	}

	@SuppressWarnings("serial")
	private class InvalidRenderer extends JLabel implements TableCellRenderer {
		private final Color LIGHTRED = new Color(255, 128, 128);
		private final Color VERYLIGHTRED = new Color(255, 204, 204);
		boolean isBordered = true;
		Border badFocusBorder = null;

		public InvalidRenderer(boolean isBordered) {
			this.isBordered = isBordered;
			setOpaque(true);
			setFont(getFont().deriveFont(getFont().getStyle() ^ java.awt.Font.BOLD));
		}

		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int coloumn) {
			String str = "";
			if (obj != null)
				str = obj.toString();
			setBackground(isSelected ? VERYLIGHTRED : LIGHTRED);

			if (hasFocus) {
				if (badFocusBorder == null)
					badFocusBorder = BorderFactory.createLineBorder(LIGHTRED, 2);
				setBorder(badFocusBorder);
			} else
				setBorder(null);
			setText(str);
			return this;
		}
	}

	@SuppressWarnings("serial")
	private class UneditableRenderer extends JLabel implements TableCellRenderer {
		private final Color LIGHTGRAY = new Color(204, 204, 204);
		private final Color VERYLIGHTGRAY = new Color(232, 232, 232);

		private boolean isDate;

		public UneditableRenderer(boolean isDate) {
			this.isDate = isDate;
			setOpaque(true);
			setFont(getFont().deriveFont(getFont().getStyle() ^ java.awt.Font.BOLD));
		}

		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			setBackground(isSelected ? LIGHTGRAY : VERYLIGHTGRAY);
			if (isDate)
				setText(obj == null ? "No date" : dateFormatter.format((Date) obj));
			else
				setText(obj == null ? "" : obj.toString());
			return this;
		}
	}

	@SuppressWarnings("serial")
	private class DateRenderer extends DefaultTableCellRenderer {
		public void setValue(Object value) {
			setText((value == null) ? "No date" : dateFormatter.format((Date) value));
		}
	}

	@SuppressWarnings("serial")
	private class DateEditor extends DefaultCellEditor {
		public DateEditor() {
			super(new JTextField());
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
			return super.getTableCellEditorComponent(table, value == null ? "" : dateFormatter.format((Date) value), isSelected, row,
					column);
		}
	}

	/**
	 * This method initializes jUserTable
	 * 
	 * @return javax.swing.JTable
	 */
	@SuppressWarnings("serial")
	private JTable getJUserTable() {
		if (jUserTable == null) {
			tableSorter = new TableSorter(getUserTableModel());
			jUserTable = new JTable(tableSorter) {
				TableCellRenderer invalidRenderer = new InvalidRenderer(false);
				TableCellRenderer uneditableRenderer[] = new TableCellRenderer[] { new UneditableRenderer(false),
						new UneditableRenderer(true) };
				TableCellRenderer dateRenderer = new DateRenderer();

				public TableCellRenderer getCellRenderer(int row, int column) {
					int modrow = tableSorter.modelIndex(row);
					int modcol = convertColumnIndexToModel(column);
					int isdateind = getUserTableModel().isDate(modcol) ? 1 : 0;
					if (!getUserTableModel().isValid(modrow, modcol))
						return invalidRenderer;
					if (!getUserTableModel().isCellEditable(modrow, modcol))
						return uneditableRenderer[isdateind];
					if (isdateind == 1)
						return dateRenderer;
					return super.getCellRenderer(row, column);
				}

				protected JTableHeader createDefaultTableHeader() {
					return new JTableHeader(columnModel) {
						public String getToolTipText(MouseEvent e) {
							int index = columnModel.getColumnIndexAtX(e.getPoint().x);
							int realIndex = columnModel.getColumn(index).getModelIndex();
							if (realIndex == UserTableModel.NUMSUBS)
								return "The number of submissions this user posted";
							return null;
						}
					};
				}
			};
			jUserTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ListSelectionModel rowSM = jUserTable.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return;
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					getJChangePassButton().setEnabled(!lsm.isSelectionEmpty());
					getJDetailsButton().setEnabled(!lsm.isSelectionEmpty());
					getJDeleteUserButton().setEnabled(!lsm.isSelectionEmpty());
				}
			});
			getUserTableModel().addTableModelListener(this);
			getUserTableModel().addTableModelListener(jUserTable);

			tableSorter.setTableHeader(jUserTable.getTableHeader());

			TableColumnModel tcm = jUserTable.getColumnModel();
			TableColumn col = tcm.getColumn(UserTableModel.STATE);
			JComboBox<String> comboBox = new JComboBox<String>(BackedUserData.getStateNameArray(userstate));
			comboBox.setBackground(Color.WHITE);
			comboBox.setFont(comboBox.getFont().deriveFont(comboBox.getFont().getStyle() ^ java.awt.Font.BOLD));
			col.setCellEditor(new DefaultCellEditor(comboBox));
			col.setMaxWidth(92);
			col.setPreferredWidth(92);

			col = tcm.getColumn(UserTableModel.CREATED);
			col.setCellEditor(new DateEditor());
			col = tcm.getColumn(UserTableModel.EXPIRES);
			col.setCellEditor(new DateEditor());
			col = tcm.getColumn(UserTableModel.LASTUSAGE);
			col.setCellEditor(new DateEditor());

			FontMetrics fm = jUserTable.getFontMetrics(jUserTable.getFont());

			int dateWidth = fm.stringWidth("88.88.8888") + 4;
			col = tcm.getColumn(UserTableModel.CREATED);
			col.setMaxWidth(dateWidth);
			col = tcm.getColumn(UserTableModel.EXPIRES);
			col.setMaxWidth(dateWidth);
			col = tcm.getColumn(UserTableModel.LASTUSAGE);
			col.setMaxWidth(dateWidth);
			col = tcm.getColumn(UserTableModel.NUMSUBS);
			col.setMaxWidth(32);

			col = tcm.getColumn(UserTableModel.CREATEDBY);
			col.setPreferredWidth(50);

			jUserTable.setRowHeight(20);
		}
		return jUserTable;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
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
			jPanel = new JPanel();
			jPanel.add(getJRefreshButton(), null);
			if ((userstate & BackedUserData.MASK_JPLAGADMIN) != 0) {
				jPanel.add(getJRequestButton(), null);
				jPanel.add(getJServerMailsButton(), null);
			}
		}
		return jPanel;
	}

	/**
	 * This method initializes jRefreshButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJRefreshButton() {
		if (jRefreshButton == null) {
			jRefreshButton = new JButton();
			jRefreshButton.setText("Refresh");
			jRefreshButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					final SwingWorker worker = new SwingWorker() {
						private UserDataArray userarray = null;
						private RequestDataArray reqarray = null;

						public Object construct() {
							try {
								userarray = getJPlagStub().getUserDataArray(0);
								reqarray = getJPlagStub().getAccountRequests(true);
							} catch (Exception ex) {
								CheckException(ex, AdminTool.this);
							}
							return null;
						}

						public void finished() {
							// Update user list

							if (userarray != null) {
								getUserTableModel().setUserDataArray(userarray);
								updateTitle();
							}
							changedUsersList.clear();
							getJAddUserButton().setEnabled(true);

							if ((userstate & BackedUserData.MASK_JPLAGADMIN) != 0) {
								// Update "Check requests" button

								if (reqarray != null) {
									RequestData[] rds = reqarray.getItems();
									if (rds != null)
										reqsWaiting = rds.length;
									else
										reqsWaiting = 0;
								} else
									reqsWaiting = 0;
								jRequestButton.setText("Check requests (" + reqsWaiting + " waiting)");
							}
						}
					};
					worker.start();
				}
			});
		}
		return jRefreshButton;
	}

	/**
	 * This method initializes jChangePassButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJChangePassButton() {
		if (jChangePassButton == null) {
			jChangePassButton = new JButton();
			jChangePassButton.setText("Change user password");
			jChangePassButton.setEnabled(false);
			jChangePassButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = tableSorter.modelIndex(getJUserTable().getSelectedRow());
					int col = getJUserTable().getSelectedColumn();
					if (row < 0 || col < 0) {
						JOptionPane.showMessageDialog(AdminTool.this, "Please select a user first!", "No user selected!",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					PassDialog pd = new PassDialog(getUserTableModel().getBackedUserData(row), AdminTool.this);
					pd.pack();
					pd.setLocationRelativeTo(AdminTool.this);
					pd.setVisible(true);
				}
			});
		}
		return jChangePassButton;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getJChangePassButton(), null);
			jPanel1.add(getJDetailsButton(), null);
			jPanel1.add(getJAddUserButton(), null);
			jPanel1.add(getJDeleteUserButton(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJUserTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jAddUserButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJAddUserButton() {
		if (jAddUserButton == null) {
			jAddUserButton = new JButton();
			jAddUserButton.setText("Add new user");
			jAddUserButton.setEnabled(false);
			jAddUserButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AddUserDialog aud = new AddUserDialog(AdminTool.this);
					aud.pack();
					aud.setLocationRelativeTo(AdminTool.this);
					aud.setVisible(true);
				}
			});
		}
		return jAddUserButton;
	}

	/**
	 * This method initializes jDeleteUserButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJDeleteUserButton() {
		if (jDeleteUserButton == null) {
			jDeleteUserButton = new JButton();
			jDeleteUserButton.setText("Delete user");
			jDeleteUserButton.setEnabled(false);
			jDeleteUserButton.addActionListener(new java.awt.event.ActionListener() {
				BackedUserData bud;

				public void actionPerformed(java.awt.event.ActionEvent e) {
					final SwingWorker worker = new SwingWorker() {
						public Object construct() {
							try {
								bud.setUsername(null);
								getJPlagStub().setUserData(new SetUserDataParams(bud, bud.getOrigUsername()));
							} catch (Exception ex) {
								CheckException(ex, AdminTool.this);
							}
							return null;
						}

						public void finished() {
							getUserTableModel().removeUser(bud.getOrigUsername());
							updateTitle();
						}
					};

					int row = tableSorter.modelIndex(getJUserTable().getSelectedRow());
					int col = getJUserTable().getSelectedColumn();
					if (row < 0 || col < 0) {
						JOptionPane.showMessageDialog(AdminTool.this, "Please select a user first!", "No user selected!",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					bud = getUserTableModel().getBackedUserData(row);
					if (JOptionPane.showConfirmDialog(AdminTool.this, "Do you really want to delete the user \"" + bud.getOrigUsername()
							+ "\"?\nAll files related " + "to this user will be deleted!", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
						worker.start();
				}
			});
		}
		return jDeleteUserButton;
	}

	/**
	 * This method initializes jRequestButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJRequestButton() {
		if (jRequestButton == null) {
			jRequestButton = new JButton();
			jRequestButton.setText("Check requests (" + reqsWaiting + " waiting)");
			jRequestButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					final SwingWorker worker = new SwingWorker() {
						private RequestDataArray reqarray = null;

						public Object construct() {
							try {
								reqarray = getJPlagStub().getAccountRequests(false);
							} catch (Exception ex) {
								CheckException(ex, AdminTool.this);
							}
							return null;
						}

						public void finished() {
							RequestData[] rds = reqarray.getItems();
							if (rds != null)
								reqsWaiting = rds.length;
							else
								reqsWaiting = 0;
							jRequestButton.setText("Check requests (" + reqsWaiting + " waiting)");
							if (reqsWaiting != 0) {
								if (requestDlg != null && requestDlg.isVisible())
									requestDlg.requestFocus();
								else {
									requestDlg = new RequestDialog(rds, AdminTool.this);
									requestDlg.pack();
									requestDlg.setLocationRelativeTo(AdminTool.this);
									requestDlg.setVisible(true);
								}
							} else {
								JOptionPane.showMessageDialog(AdminTool.this, "No request is waiting for you!", "No requests available!",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					};
					worker.start();
				}
			});
		}
		return jRequestButton;
	}

	public void updateRequestsWaiting() {
		final SwingWorker worker = new SwingWorker() {
			private RequestDataArray reqarray = null;

			public Object construct() {
				try {
					reqarray = getJPlagStub().getAccountRequests(true);
				} catch (Exception ex) {
					CheckException(ex, AdminTool.this);
				}
				return null;
			}

			public void finished() {
				RequestData[] rds = reqarray.getItems();
				if (rds != null) {
					reqsWaiting = rds.length;
				} else {
					reqsWaiting = 0;
				}
				jRequestButton.setText("Check requests (" + reqsWaiting + " waiting)");
			}
		};
		worker.start();
	}

	/**
	 * This method initializes jServerMailsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJServerMailsButton() {
		if (jServerMailsButton == null) {
			jServerMailsButton = new JButton();
			jServerMailsButton.setText("Change mail templates");
			jServerMailsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					RequestData rd = new RequestData(null, "Matze", "UlTiMaTePaSs", "Mathias Musterknabe", "Matze@email.server",
							"Mathias@email2.server2", "www.Matze-Musterknabe-Page.xyz", "Toller Grund ohne wenn und aber", "Wenn und aber");
					MailDialog md = new MailDialog(MailDialog.MAIL_ALL, "Change mail templates...", rd, AdminTool.this);
					md.setVisible(true);
				}
			});
		}
		return jServerMailsButton;
	}

	/**
	 * This method initializes jDetailsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJDetailsButton() {
		if (jDetailsButton == null) {
			jDetailsButton = new JButton();
			jDetailsButton.setText("Change user details");
			jDetailsButton.setEnabled(false);
			jDetailsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = tableSorter.modelIndex(getJUserTable().getSelectedRow());
					int col = getJUserTable().getSelectedColumn();
					if (row < 0 || col < 0) {
						JOptionPane.showMessageDialog(AdminTool.this, "Please select a user first!", "No user selected!",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					DetailsDialog dd = new DetailsDialog(getUserTableModel().getBackedUserData(row), AdminTool.this);
					dd.pack();
					dd.setLocationRelativeTo(AdminTool.this);
					dd.setVisible(true);
				}
			});
		}
		return jDetailsButton;
	}

	// TODO #44
	public static void showHomepage(String url, Component parent) {
		if (DesktopUtils.isBrowseSupported()) {
			try {
				DesktopUtils.openWebpage(url);
			} catch (MalformedURLException e) {

			}
		} else {
			JOptionPane.showMessageDialog(parent, "Couldn't open homepage in browser!", "Browser problem", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame owner = new JFrame("JPlag AdminTool login dialog");
				owner.setLocation(-1000, -1000);
				owner.setVisible(true);
				LoginDialog ld = new LoginDialog(owner);
				ld.setLocationRelativeTo(null);
				ld.setVisible(true);
			}
		});
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
