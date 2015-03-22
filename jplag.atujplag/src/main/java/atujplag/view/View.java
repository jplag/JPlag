/*
 * Created on Aug 4, 2005
 */
package atujplag.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import jplagUtils.DesktopUtils;
import jplagUtils.PropertiesLoader;
import jplagWsClient.jplagClient.ServerInfo;

import org.w3c.dom.Document;

import atujplag.ATUJPLAG;
import atujplag.util.Messages;
import atujplag.util.TagParser;

/**
 * @author Emeric Kwemou
 */
public class View extends JFrame implements ActionListener, ListSelectionListener, ComponentListener {
	private static final long serialVersionUID = 4612880650349401754L;
	private static final int BLINK_PERIOD = 1000;

	private static final Properties configProps = PropertiesLoader.loadProps("atujplag/ATUJPLAG.properties");

	private ATUJPLAG atujplag = null;
	private ServerInfo serverInfo = null;
	
	private JMenuBar jJMenuBar = null;
	private JMenu extraMenu = null;
	private JPopupMenu jPopupMenu = null;
	private JMenu optionsMenu = null;
	
	private JMenuItem openItem = null;
	private JMenuItem aboutItem = null;
	private JMenuItem helpItem = null;
	private JMenuItem changeItem = null;
	private JMenuItem deleteItem = null;
	private JMenuItem editPrefItem;
	private JMenuItem exitItem = null;
	private JMenuItem logItem = null;
	private JMenuItem renameItem = null;
	private JMenuItem switch_profile = null;

	private boolean block_all_newSubmission = false;

	private JButton changeButton = null;
	private JButton newSubButton = null;
	private JButton deleteSubButton = null;
	private JButton showParserLogButton = null;
	private JButton viewResultButton = null;
	private JButton renameButton = null;

	private JPanel commandPanel = null;


	private JButton hideInfoPanel = JPlagCreator.createButton(
            Messages.getString("View.Hide_progress"), //$NON-NLS-1$
			Messages.getString("View.Hide_progress_TIP"), //$NON-NLS-1$
			180, 20);
    private boolean isHideButtonBlinking = false;

	private InfoPanel infoPanel = null;

	private boolean dontHideInfoPanelOnFocus = false;

	private JPanel jPanel = null;

	private JPanel jPanel1 = null;

	private JPanel jPanel2 = null;

	private JScrollPane jScrollPane = null;

	private ResultsTable jTable = null;

	private OptionPanel optionPanel = null;

	private ParserLogWindow parserDialog = null;

	private Preferences pref_dialog = null;

	private ResultsTableModel resultTableModel = null; // @jve:decl-index=0:

	private TableSorter tableSorter = null;

	private JPanel topPanel = null;


	/**
	 * This is the default constructor
	 */
	public View(ATUJPLAG atu) {
		super();
		
		atujplag = atu;
		serverInfo = atu.getServerInfo();
        
		initialize();

		if (serverInfo.getSubmissions().length > 0) {
			InfoPanel infoPanel = new InfoPanel(this,
                serverInfo.getSubmissions());
			startInfoPanel(infoPanel);
		}
		enableButtons(false);
	}
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setJMenuBar(getJJMenuBar());

        this.setContentPane(this.getTopPanel());
        this.getTopPanel().setOpaque(true);
        this.setTitle(ATUJPLAG.programName);

        this.getJTable().addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    Point point = me.getPoint();
                    int i = getJTable().rowAtPoint(point);
                    int j = getJTable().columnAtPoint(point);
                    if (j == -1 || i == -1)
                        return;
                    TableCellEditor celleditor = getJTable().getCellEditor();
                    if (celleditor != null)
                        celleditor.stopCellEditing();
                    getJTable().changeSelection(i, j, false, false);
                }
            }

            public void mouseClicked(MouseEvent arg0) {
            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
        this.getJTable().addMouseListener(
                new PopupListener(this.getJPopupMenu()));
        this.aboutItem.addActionListener(this);
        this.editPrefItem.addActionListener(this);
        this.switch_profile.addActionListener(this);
        this.getJTable().getSelectionModel().addListSelectionListener(this);
        this.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent arg0) {
                View.this.resetInfoPanelPosition();
            }

            public void componentMoved(ComponentEvent arg0) {
                View.this.resetInfoPanelPosition();
            }

            public void componentResized(ComponentEvent arg0) {
                View.this.resetInfoPanelPosition();
            }

            public void componentShown(ComponentEvent arg0) {
                View.this.resetInfoPanelPosition();
            }
        });
        hideInfoPanel.setBorder(javax.swing.BorderFactory.createLineBorder(
                java.awt.SystemColor.activeCaption, 2));
        hideInfoPanel.addActionListener(this);
        this.addComponentListener(this);
    }
    
	
	public ATUJPLAG getATUJPLAG() {
		return atujplag;
	}

	public void openItem() {
		int sortedRow = getJTable().getSelectedRow();
		if (sortedRow < 0)
			return;
		int row = tableSorter.modelIndex(sortedRow);
		if (row < 0)
			return;
		resultTableModel.showResult(row);
	}

	public void openPreferences() {
		actionPerformed(new ActionEvent(editPrefItem, 0, null));
	}

	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();

		if (source == switch_profile)
			atujplag.switchUser(this);   // destroys this View if switch is done

		else if (source == hideInfoPanel)
			this.hideInfoPanel();

		else if (source == openItem || source == getViewResultButton())
			openItem();

		else if (source == logItem || source == getShowParserLogButton()) {
			int sortedRow = getJTable().getSelectedRow();
			if (sortedRow < 0)
				return;
			int row = tableSorter.modelIndex(sortedRow);
			if (row < 0)
				return;
			String text = resultTableModel.getLogString(row);
			if (text == null)
				return;
			text = "<html><body ><pre>" + text + "</pre></body></html>"; //$NON-NLS-1$ //$NON-NLS-2$
			if (parserDialog != null)
				parserDialog.dispose();
			parserDialog = new ParserLogWindow(text, Messages
					.getString("View.JPlag_parser_log"), this); //$NON-NLS-1$
			parserDialog.pack();
			parserDialog.setLocationRelativeTo(this);
			parserDialog.setVisible(true);
		}

		else if (source == editPrefItem) {
			if (pref_dialog != null)
				pref_dialog.dispose();
			pref_dialog = new Preferences(this);
			pref_dialog.pack();
			pref_dialog.requestFocus();
			pref_dialog.setLocationRelativeTo(this);
			pref_dialog.setVisible(true);
		}

		else if (source == exitItem)
			System.exit(0);

		else if (source == aboutItem) {
			AboutDialog aboutDlg = new AboutDialog(this);
			aboutDlg.pack();
			aboutDlg.setLocationRelativeTo(this);
			aboutDlg.setVisible(true);
		}

		else if (source == renameItem || source == getRenameButton()) {
			int sortedRow = getJTable().getSelectedRow();
			if (sortedRow < 0)
				return;
			int row = tableSorter.modelIndex(sortedRow);
			if (row < 0)
				return;
			getJTable().directEditCellAt(sortedRow,
					getJTable().convertColumnIndexToView(0));
			getJTable().getEditorTextField().requestFocus();
		}

		else if (source == deleteItem || source == getDeleteSubButton()) {
			int sortedRow = getJTable().getSelectedRow();
			if (sortedRow < 0)
				return;
			resultTableModel.delete(tableSorter.modelIndex(sortedRow));
		}

		else if (source == getNewSubButton()) {
			if (hideInfoPanel.isVisible())
				hideInfoPanel();
			hideInfoPanel.setVisible(false);
			optionPanel = new OptionPanel(atujplag.findNextUnusedTitle(), this);
			optionPanel.pack();
			optionPanel.setLocationRelativeTo(this);
			optionPanel.setVisible(true);
			/*setting the values for this client after it is visible*/
			optionPanel.initOptions(false);
		}

		else if (source == changeItem || source == getChangeButton()) {
			int sortedRow = getJTable().getSelectedRow();
			if (sortedRow < 0)
				return;
			int row = tableSorter.modelIndex(sortedRow);
			Document doc = resultTableModel.getDocument(row);
			optionPanel = atujplag.changeSubmissionValues(doc, this);
			optionPanel.pack();
			optionPanel.setLocationRelativeTo(this);
			optionPanel.setVisible(true);
			/*setting the values for this client after it is visible*/
			optionPanel.initOptions(true);
 		}
	}

	public void blink() {
		if (infoPanel == null || infoPanel.isVisible() || isHideButtonBlinking)
			return;
        
        isHideButtonBlinking = true;
		
		Thread t = new Thread() {
            boolean buttonLit = false;
            
            final Runnable doUpdateBackground = new Runnable() {
                public void run() {
                    hideInfoPanel.setBackground(buttonLit
                        ? JPlagCreator.BLINK_COLOR
                        : JPlagCreator.BUTTON_BACKGROUND);
                }
            };
            
			public void run() {
				for(int i = 0; i < 4; i++) {
                    if(infoPanel.isVisible())
                        break;
				    buttonLit = true;
                    EventQueue.invokeLater(doUpdateBackground);
					try {
						Thread.sleep(BLINK_PERIOD);
					} catch (InterruptedException e) {}
                    buttonLit = false;
					if(infoPanel.isVisible())
                        break;
                    EventQueue.invokeLater(doUpdateBackground);
					try {
						Thread.sleep(BLINK_PERIOD);
					} catch (InterruptedException e) {}
				}
                buttonLit = !infoPanel.isVisible();
                EventQueue.invokeLater(doUpdateBackground);
                isHideButtonBlinking = false;
			}
		};
		t.start();
	}

	public void blockNewSubmissions() {
		this.changeItem.setEnabled(false);
		this.getNewSubButton().setEnabled(false);
		this.getChangeButton().setEnabled(false);
		block_all_newSubmission = true;
	}

	public void closeInfoPanel() {
		this.hideInfoPanel.setVisible(false);
		this.infoPanel = null;
	}

	private void enableButtons(boolean bol) {
		this.getChangeButton().setEnabled(bol && !this.block_all_newSubmission);
		this.changeItem.setEnabled(bol && !this.block_all_newSubmission);
		this.deleteSubButton.setEnabled(bol);
		this.deleteItem.setEnabled(bol);
		this.showParserLogButton.setEnabled(bol);
		this.logItem.setEnabled(bol);
		this.viewResultButton.setEnabled(bol);
		this.openItem.setEnabled(bol);
		this.renameButton.setEnabled(bol);
		this.renameItem.setEnabled(bol);
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getChangeButton() {
		if (changeButton == null) {
			changeButton = JPlagCreator.createButton(Messages
					.getString("View.Change_options"), //$NON-NLS-1$
					Messages.getString("View.Change_options_TIP"), //$NON-NLS-1$
					180, 20);
			changeButton.addActionListener(this);
		}
		return changeButton;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCommandPanel() {
		if (commandPanel == null) {
			commandPanel = JPlagCreator.createPanelWithoutBorder(
					getContentPane().getPreferredSize().width, 90, 5, 120,
					FlowLayout.CENTER);
			commandPanel.setLayout(new BorderLayout());
			commandPanel.add(getJPanel(), java.awt.BorderLayout.NORTH);
			commandPanel.add(getJPanel1(), java.awt.BorderLayout.WEST);
			commandPanel.add(getJPanel2(), java.awt.BorderLayout.EAST);
		}
		return commandPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteSubButton() {
		if (deleteSubButton == null) {
			deleteSubButton = JPlagCreator.createButton(Messages
					.getString("View.Delete_result"), //$NON-NLS-1$
					Messages.getString("View.Delete_result_TIP"), //$NON-NLS-1$
					180, 20);
			deleteSubButton.addActionListener(this);
		}
		return deleteSubButton;
	}

	/**
	 * @return Returns the infoPanel.
	 */
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.setBackground(JPlagCreator.SYSTEMCOLOR);
			jJMenuBar.setForeground(JPlagCreator.SYSTEMCOLOR);
			jJMenuBar.setPreferredSize(new Dimension(500, 22));

			optionsMenu = JPlagCreator.createMenu(Messages
					.getString("View.Main_menu")); //$NON-NLS-1$
			extraMenu = JPlagCreator.createMenu(Messages
					.getString("View.Help_menu")); //$NON-NLS-1$
			jJMenuBar.add(optionsMenu);
			jJMenuBar.add(Box.createHorizontalGlue());
			jJMenuBar.add(extraMenu);

			editPrefItem = JPlagCreator.createJMenuItem(Messages
					.getString("View.Edit_preferences")); //$NON-NLS-1$
			switch_profile = JPlagCreator.createJMenuItem(Messages
					.getString("View.Switch_to_another_user")); //$NON-NLS-1$
			exitItem = JPlagCreator.createJMenuItem(Messages
					.getString("View.Exit_JPlag")); //$NON-NLS-1$
			exitItem.addActionListener(this);
			optionsMenu.add(editPrefItem);
			optionsMenu.add(switch_profile);
			optionsMenu.addSeparator();
			optionsMenu.add(exitItem);

			helpItem = JPlagCreator.createJMenuItem(Messages.getString("View.Show_help_web_page")); //$NON-NLS-1$
			helpItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String helpPageAddress = configProps.getProperty("helpPage.url", "http://www.jplag.de");
					if (DesktopUtils.isBrowseSupported()) {
						try {
							DesktopUtils.openWebpage(helpPageAddress);
						} catch (MalformedURLException ex) {
							ex.printStackTrace();
							JPlagCreator.showMessageDialog(Messages.getString("View.Unable_to_show_help_page"), //$NON-NLS-1$
								TagParser.parse(Messages.getString("View.Unable_to_show_help_page_DESC_{1_HELPURL}"), //$NON-NLS-1$
								new String[] { helpPageAddress }));
						}
					} else {
						JPlagCreator.showMessageDialog(Messages.getString("View.Unable_to_show_help_page"), //$NON-NLS-1$
							TagParser.parse(Messages.getString("View.Unable_to_show_help_page_DESC_{1_HELPURL}"), //$NON-NLS-1$
							new String[] { helpPageAddress }));
					}
				}
			});
			extraMenu.add(helpItem);

			JMenuItem devItem = JPlagCreator.createJMenuItem(Messages.getString("View.Show_dev_web_page")); //$NON-NLS-1$
			devItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String devPageAddress = configProps.getProperty("devPage.url", "http://www.jplag.de");
					if (DesktopUtils.isBrowseSupported()) {
						try {
							DesktopUtils.openWebpage(devPageAddress);
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
							JPlagCreator.showMessageDialog(Messages.getString("View.Unable_to_show_help_page"), //$NON-NLS-1$
								TagParser.parse(Messages.getString("View.Unable_to_show_help_page_DESC_{1_HELPURL}"), //$NON-NLS-1$
								new String[] { devPageAddress }));
						}
					} else {
						JPlagCreator.showMessageDialog(Messages.getString("View.Unable_to_show_help_page"), //$NON-NLS-1$
							TagParser.parse(Messages.getString("View.Unable_to_show_help_page_DESC_{1_HELPURL}"), //$NON-NLS-1$
										new String[] { devPageAddress }));
					}
				}
			});
			extraMenu.add(devItem);

			extraMenu.addSeparator();

			aboutItem = JPlagCreator.createJMenuItem(Messages.getString("View.About_JPlag")); //$NON-NLS-1$
			extraMenu.add(aboutItem);
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = JPlagCreator.createPanelWithoutBorder(750, 30, 5, 50,
					FlowLayout.CENTER);

			jPanel.add(getNewSubButton(), null);
			jPanel.add(getDeleteSubButton(), null);
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
			jPanel1 = JPlagCreator.createPanelWithoutBorder(200, 60, 5, 100,
					FlowLayout.CENTER);
			jPanel1.add(getViewResultButton(), null);
			jPanel1.add(getShowParserLogButton(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = JPlagCreator.createPanelWithoutBorder(200, 60, 5, 100,
					FlowLayout.CENTER);
			jPanel2.add(getChangeButton(), null);
			jPanel2.add(getRenameButton(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPopupMenu
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
            jPopupMenu.setBackground(JPlagCreator.SYSTEMCOLOR);
            
            openItem = JPlagCreator.createJMenuItem(
                    Messages.getString("View.View_result")); //$NON-NLS-1$
            openItem.addActionListener(this);
			jPopupMenu.add(openItem);
            
            logItem = JPlagCreator.createJMenuItem(
                    Messages.getString("View.View_parser_log")); //$NON-NLS-1$
            logItem.addActionListener(this);
			jPopupMenu.add(logItem);
            
			jPopupMenu.addSeparator();
            
            renameItem = JPlagCreator.createJMenuItem(
                    Messages.getString("View.Rename_result")); //$NON-NLS-1$
            renameItem.addActionListener(this);
			jPopupMenu.add(renameItem);
            
            changeItem = JPlagCreator.createJMenuItem(
                    Messages.getString("View.Change_options")); //$NON-NLS-1$
            changeItem.addActionListener(this);
			jPopupMenu.add(changeItem);
            
			jPopupMenu.addSeparator();
            
            deleteItem = JPlagCreator.createJMenuItem(
                    Messages.getString("View.Delete_result")); //$NON-NLS-1$
            deleteItem.addActionListener(this);
			jPopupMenu.add(deleteItem);
		}
		return jPopupMenu;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane(getJTable());
			jScrollPane.setBackground(JPlagCreator.SYSTEMCOLOR);
			jScrollPane.getVerticalScrollBar().setBackground(
					JPlagCreator.SYSTEMCOLOR);
			jScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.getViewport().setBackground(Color.WHITE);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private ResultsTable getJTable() {
		if (jTable == null) {
			tableSorter = new TableSorter(getResultTableModel());
			jTable = new ResultsTable(this, tableSorter);

			tableSorter.setTableHeader(jTable.getTableHeader());
			jTable.setCellSelectionEnabled(false);
			jTable.setRowSelectionAllowed(true);
			jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTable.getTableHeader().setBackground(JPlagCreator.SYSTEMCOLOR);
			jTable.setSelectionBackground(JPlagCreator.SYSTEMCOLOR);
			jTable.setSelectionForeground(Color.BLACK);
			jTable.setPreferredScrollableViewportSize(new Dimension(600, 5000));
			jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth(140);
			jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth(160);
			jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth(100);
			jTable.getColumn(jTable.getColumnName(3)).setPreferredWidth(100);
			jTable.getColumn(jTable.getColumnName(4)).setPreferredWidth(100);
			tableSorter.setSortingStatus(1, TableSorter.DESCENDING);
		}
		return jTable;
	}

	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getNewSubButton() {
		if (newSubButton == null) {
			newSubButton = JPlagCreator.createButton(
                    Messages.getString("View.New_submission"), //$NON-NLS-1$
					Messages.getString("View.New_submission_TIP"), 180, 20); //$NON-NLS-1$

			newSubButton.addActionListener(this);
		}
		return newSubButton;
	}

	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRenameButton() {
		if (renameButton == null) {
			renameButton = JPlagCreator.createButton(
                    Messages.getString("View.Rename_result"), //$NON-NLS-1$
					Messages.getString("View.Rename_result_TIP"), //$NON-NLS-1$
					180, 20);
			renameButton.addActionListener(this);
		}
		return renameButton;
	}

	/**
	 * This method initializes userTableModel
	 * 
	 * @return atujplag.view.UserTableModel
	 */
	private ResultsTableModel getResultTableModel() {
		if (resultTableModel == null) {
			resultTableModel = new ResultsTableModel(atujplag, this);
		}
		return resultTableModel;
	}

	public ServerInfo getServerInfos() {
		return this.serverInfo;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getShowParserLogButton() {
		if (showParserLogButton == null) {
			showParserLogButton = JPlagCreator.createButton(
                    Messages.getString("View.View_parser_log"), //$NON-NLS-1$
					Messages.getString("View.View_parser_log_TIP"), 180, 20); //$NON-NLS-1$
			showParserLogButton.addActionListener(this);
		}
		return showParserLogButton;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			topPanel.add(getJScrollPane(), BorderLayout.CENTER);
			topPanel.add(getCommandPanel(), BorderLayout.SOUTH);
			topPanel.setPreferredSize(new Dimension(780, 400));
			topPanel.setMinimumSize(new Dimension(550, 400));
			if (this.infoPanel != null)
				topPanel.add(this.infoPanel);
		}
		return topPanel;
	}

	private JButton getViewResultButton() {
		if (this.viewResultButton == null) {
			this.viewResultButton = JPlagCreator.createButton(
                    Messages.getString("View.View_result"), //$NON-NLS-1$
					Messages.getString("View.View_result_TIP"), 180, 20); //$NON-NLS-1$
			this.viewResultButton.addActionListener(this);
		}
		return this.viewResultButton;
	}

	public void hideInfoPanel() {
		if (infoPanel == null)
			return;

		if (infoPanel.isVisible()) {
			this.infoPanel.setVisible(false);
			this.hideInfoPanel.setText(
                    Messages.getString("View.Show_Progress")); //$NON-NLS-1$
			this.hideInfoPanel.setToolTipText(
                    Messages.getString("View.Show_Progress_TIP")); //$NON-NLS-1$
		} else {
			this.resetInfoPanelPosition();
            this.hideInfoPanel.setBackground(JPlagCreator.BUTTON_BACKGROUND);
			this.hideInfoPanel.setText(
                    Messages.getString("View.Hide_progress")); //$NON-NLS-1$
			this.hideInfoPanel.setToolTipText(
                    Messages.getString("View.Hide_progress_TIP")); //$NON-NLS-1$
			this.infoPanel.setVisible(true);
		}
	}

	private void resetInfoPanelPosition() {
		if (infoPanel == null)
			return;
		hideInfoPanel.setOpaque(true);
		JScrollPane sp = getJScrollPane();
		int scrollBarWidth = sp.getVerticalScrollBar().isVisible()
                ? sp.getWidth() - sp.getVerticalScrollBar().getX() : 0;
		Point p = new Point(sp.getWidth()
				- hideInfoPanel.getPreferredSize().width - scrollBarWidth,
				getTopPanel().getY() + getCommandPanel().getY() - 20);

		hideInfoPanel.setBounds(p.x, p.y,
				hideInfoPanel.getPreferredSize().width,
                hideInfoPanel.getPreferredSize().height);
		hideInfoPanel.setVisible(true);

		p.x -= infoPanel.getPreferredSize().width
				- hideInfoPanel.getPreferredSize().width;
		p.y -= infoPanel.getPreferredSize().height;

		SwingUtilities.convertPointToScreen(p, getLayeredPane());
		infoPanel.setBounds(p.x, p.y,
				infoPanel.getPreferredSize().width,
                infoPanel.getPreferredSize().height);
	}

	public void startInfoPanel(InfoPanel newInfoPanel) {
		if (infoPanel != null) {
			infoPanel.setVisible(false);
			getLayeredPane().remove(infoPanel);
			getLayeredPane().remove(hideInfoPanel);
			hideInfoPanel = JPlagCreator.createButton(
                    Messages.getString("View.Hide_progress"), //$NON-NLS-1$
					Messages.getString("View.Hide_progress_TIP"), //$NON-NLS-1$
					180, 20);
			hideInfoPanel.addActionListener(this);
		}

		infoPanel = newInfoPanel;
		resetInfoPanelPosition();
		getLayeredPane().add(hideInfoPanel, JLayeredPane.POPUP_LAYER);
		infoPanel.setVisible(true);
		hideInfoPanel.setVisible(true);
		infoPanel.run();
	}
	
	public void unblockNewSubmissions() {
		block_all_newSubmission = false;
		this.changeItem.setEnabled(true);
		this.getNewSubButton().setEnabled(true);
		this.getChangeButton().setEnabled(true);
		this.enableButtons(this.getJTable().getSelectedRow() >= 0);
	}

	public void updateTable(String selectedTitle) {
		this.getResultTableModel().init();
		this.getResultTableModel().fireTableDataChanged();
		if (selectedTitle != null) {
			dontHideInfoPanelOnFocus = true;
			this.getJTable().selectSubmission(selectedTitle);
			dontHideInfoPanelOnFocus = false;
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		int row = arg0.getFirstIndex();
		if (row >= 0) {
			if (getJTable().isRowSelected(row)
					|| getJTable().isRowSelected(arg0.getLastIndex())) {
				enableButtons(true);
				if (!dontHideInfoPanelOnFocus && View.this.infoPanel != null
						&& View.this.infoPanel.isShowing())
					hideInfoPanel();
				return;
			}
		}
		enableButtons(false);
	}

	private static final int MIN_WIDTH = 600;

	private static final int MIN_HEIGHT = 400;

	int lastx1, lastx2, lasty1, lasty2;

	public void componentResized(ComponentEvent arg0) {
		if (this.getSize().width < MIN_WIDTH) {
			if (lastx1 != getLocation().x) { // left side was moved
				setLocation(lastx2 - MIN_WIDTH, getLocation().y);
			}
			this.setSize(MIN_WIDTH, this.getSize().height);
		}
		if (this.getSize().height < MIN_HEIGHT) {
			if (lasty1 != getLocation().y) { // upper side was moved
				setLocation(getLocation().x, lasty2 - MIN_HEIGHT);
			}
			this.setSize(this.getSize().width, MIN_HEIGHT);
		}
		lastx1 = getLocation().x;
		lasty1 = getLocation().y;
		lastx2 = lastx1 + getSize().width;
		lasty2 = lasty1 + getSize().height;
	}

	public void componentShown(ComponentEvent arg0) {
		lastx1 = getLocation().x;
		lasty1 = getLocation().y;
		lastx2 = lastx1 + getSize().width;
		lasty2 = lasty1 + getSize().height;
	}

	public void componentHidden(ComponentEvent arg0) {}

	public void componentMoved(ComponentEvent arg0) {
		// If resizing with the upper and/or left side don't save new positions
		if (getSize().width != lastx2 - lastx1
				|| getSize().height != lasty2 - lasty1)
			return;
		lastx1 = getLocation().x;
		lasty1 = getLocation().y;
		lastx2 = lastx1 + getSize().width;
		lasty2 = lasty1 + getSize().height;
	}

    // TODO: Shouldn't the super implementation be called?
	public void destroy() {
		if (infoPanel != null) {
			infoPanel.destroy();
			infoPanel = null;
		}
	}

	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}
} // @jve:decl-index=0:
