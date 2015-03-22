/*
 * Created on Jun 8, 2005
 */
package atujplag.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.border.CompoundBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import jplagUtils.DesktopUtils;
import jplagWsClient.jplagClient.Submission;
import atujplag.ATUJPLAG;
import atujplag.client.Client;
import atujplag.client.Command;
import atujplag.client.SubmittedClient;
import atujplag.util.Messages;
import atujplag.util.SwingWorker;

/**
 * @author Emeric Kwemou
 */
public class InfoPanel extends JWindow implements ActionListener, WindowFocusListener {

	private static final long serialVersionUID = 6779125008336844277L;
	private View view = null;
	private ATUJPLAG atujplag = null;
	
	private Vector<Client> clients = null;
    private Client activeClient = null;
    
    private int currentState = 0;
    private boolean cancelling = false;

	private JPanel jTopPanel = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel1 = null; //  @jve:decl-index=0:
	private ProgressPanel progressPan = new ProgressPanel();

	private JButton closeButton = null;
	private JButton cancelButton = null;

	private JProgressBar jProgressBar = null;

	private JScrollPane jScrollPane = null;
	private JEditorPane jEditorPane = null;

    private boolean stop = false;

	/**
	 * This is the default constructor
	 */
	public InfoPanel(Client client, View view) {
		super(view);

		this.clients = new Vector<Client>();
		this.clients.add(client);
		this.view = view;
		atujplag = view.getATUJPLAG();
		initialize();
	}

	/**
	 * This constructor constructs an infoPanel used when loading all
	 * submissions from the server at once
	 * 
	 * @param serverinfo
	 *            contains all submissions to be loaded
	 */
	public InfoPanel(View view, Submission[] submissions) {
		super(view);
		this.view = view;
		atujplag = view.getATUJPLAG();
        // generate vector of submissions
        clients = new Vector<Client>();
        for(int i = 0; i < submissions.length; i++) {
            clients.add(new SubmittedClient(atujplag, submissions[i]));
        }
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.view.blockNewSubmissions();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getJTopPanel(), java.awt.BorderLayout.CENTER);
	}

	/**
	 * This method initializes jTopPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJTopPanel() {
		if (jTopPanel == null) {
			jTopPanel = new JPanel();
			CompoundBorder border = BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(SystemColor.activeCaption,2),
					JPlagCreator.titleBorder(Messages.getString(
						"InfoPanel.JPlag_progress"))), //$NON-NLS-1$
				BorderFactory.createEmptyBorder(0,2,2,2));
			jTopPanel.setBorder(border);
			Insets insets = border.getBorderInsets(jTopPanel);
			jTopPanel.setPreferredSize(
				new java.awt.Dimension(490+insets.left+insets.right, 194));
			jTopPanel.setLayout(new BorderLayout());
			jTopPanel.add(getJPanel2(), java.awt.BorderLayout.NORTH);
			jTopPanel.add(getJProgressBar(), java.awt.BorderLayout.EAST);
		}
		return jTopPanel;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = JPlagCreator.createPanelWithoutBorder(560, 152, 10, 20,
					FlowLayout.CENTER);
			jPanel2.setBorder(BorderFactory.createEmptyBorder(-8,0,0,0));
			jPanel2.add(getJPanel1());
			
			cancelButton = JPlagCreator.createButton(
					Messages.getString("InfoPanel.Cancel_submission"), //$NON-NLS-1$
					Messages.getString("InfoPanel.Cancel_submission_TIP"),//$NON-NLS-1$
					160, 20);
			cancelButton.addActionListener(this);
			jPanel2.add(cancelButton);
			
			closeButton = JPlagCreator.createButton(
					Messages.getString("InfoPanel.Close"), //$NON-NLS-1$
					Messages.getString("InfoPanel.Close_TIP"), 110, 20); //$NON-NLS-1$
			closeButton.setVisible(false);
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					InfoPanel.this.setVisible(false);
					InfoPanel.this.view.closeInfoPanel();
				}
			});
			jPanel2.add(closeButton);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setHgap(0);
			flowLayout2.setVgap(0);
			jPanel1 = new JPanel();
			jPanel1.setLayout(flowLayout2);
			jPanel1.setPreferredSize(new java.awt.Dimension(490, 120));
			jPanel1.add(progressPan, null);
			jPanel1.add(getJScrollPane(), null);
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
			jScrollPane.setViewportView(getJEditorPane());
			jScrollPane.setPreferredSize(new java.awt.Dimension(290, 120));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setPreferredSize(new java.awt.Dimension(290, 120));
			jEditorPane.setEditable(false);
			jEditorPane.setBackground(Color.WHITE);
			
			StyleSheet styleSheet = new StyleSheet();
			styleSheet.addRule("body { font-family: Dialog; font-size: 12; }"); //$NON-NLS-1$
			styleSheet.addRule("a { font-size: 12; font-weight: normal; " //$NON-NLS-1$
				+ "color: #0000ff; text-decoration: underline;}"); //$NON-NLS-1$
			HTMLEditorKit htmlKit = (HTMLEditorKit)(jEditorPane.
					getEditorKitForContentType("text/html")); //$NON-NLS-1$
			htmlKit.setStyleSheet(styleSheet);
			
			jEditorPane.setContentType("text/html"); //$NON-NLS-1$
			jEditorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent arg0) {
					if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						File f = new File(InfoPanel.this.activeClient.getEncodedIndex_html());
						DesktopUtils.openWebpage(f.toURI());
					}
				}
			});
		}
		return jEditorPane;
	}

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setMaximum(100);
			jProgressBar.setMinimum(0);
			jProgressBar.setIndeterminate(false);
			jProgressBar.setPreferredSize(new java.awt.Dimension(100, 5));
			jProgressBar.setForeground(java.awt.Color.black);
			jProgressBar.setBackground(JPlagCreator.SYSTEMCOLOR);
		}
		return jProgressBar;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if (source == cancelButton) {
			if (activeClient == null)
				return;
            
            cancelling = true;
            setClientState(Messages.getString("InfoPanel.Cancelling"),
                "", 0, Client.CANCELLING, true);
            cancelButton.setEnabled(false);
			// Make command thread stop and delete submission (asynchronously)
			activeClient.forceStop();
            submissionWorker.interrupt();
		}
	}
    
    SwingWorker submissionWorker = null;
    
	public void run() {
        submissionWorker = new SwingWorker() {
            public Object construct() {
                for(int i=0; i<clients.size(); i++) {
                    activeClient = clients.get(i);
                    currentState = activeClient.getState();
                    Command com = new Command(activeClient, InfoPanel.this);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            getJProgressBar().setValue(0);
                            cancelButton.setEnabled(true);
                        }
                    });
                    com.run();
                    if((Thread.interrupted() || activeClient.isForceStop())
                            && !activeClient.isErrorOccurred()) {
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                setClientState(Messages.getString(
                                    "InfoPanel.Submission_cancelled"),
                                    "", 100, Client.STOPPED, false);
                            }});
                    }
                    if(stop) return null;
                }
                return null;
            }
            public void finished() {
                if(activeClient.getState() != Client.ERROR_MESSAGE)
                    view.updateTable(activeClient.getClientName());
                view.unblockNewSubmissions();
                cancelButton.setVisible(false);
                closeButton.setVisible(true);
            }
        };
        submissionWorker.start();
	}

	public void invokeSetProgress(final int progress) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getJProgressBar().setValue(progress);
            }
        });
	}
    
    public void invokeSetTextAndProgress(final String message,
            final String details, final int progress) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getJEditorPane().setText(
                    Messages.getString("InfoPanel.Submission") //$NON-NLS-1$
                    + ": " + activeClient.getClientName() //$NON-NLS-1$
                    + "<br>" + message //$NON-NLS-1$
                    + ((details==null) ? "" : "<br>" + details)); //$NON-NLS-1$
                getJProgressBar().setValue(progress);
            }
        });
    }

    private void setClientState(String message, String detail, int progress,
            int state, boolean noError) {
        progressPan.setState(state, noError);
        message = message.replaceAll("\n", "<br>");
        if(detail != null) 
            detail = detail.replaceAll("\n", "<br>");
        getJEditorPane().setText(
            Messages.getString("InfoPanel.Submission") //$NON-NLS-1$
            + ": " + activeClient.getClientName() //$NON-NLS-1$
            + "<br>" + message //$NON-NLS-1$
            + ((detail == null) ? "" : "<br>" + detail)); //$NON-NLS-1$
        if (state == Client.PACKING || state == Client.WAITING
                || state == Client.CANCELLING)
            getJProgressBar().setIndeterminate(true);
        else
            getJProgressBar().setIndeterminate(false);
        if (!noError) {
            getJProgressBar().setIndeterminate(false);
            getJProgressBar().setValue(0);
        }
        if(currentState!=state) {
            currentState = state;
            view.blink();
        }
        
        getJProgressBar().setValue(progress);
    }

	private void invokeSetClientState(final String message, final String detail,
            final int progress, final int state, final boolean noError) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(!cancelling)
                    setClientState(message, detail, progress, state, noError);
            }});
	}

	public void windowGainedFocus(WindowEvent arg0) {
	}

	public void windowLostFocus(WindowEvent arg0) {
		this.requestFocus();
	}

	public void updateStatus() {
		if (activeClient == null)
			return;
		invokeSetClientState(activeClient.getMessage(),
            activeClient.getDetails(),activeClient.getProgress(),
            activeClient.getState(), !activeClient.isErrorOccurred());
	}
    
	public void destroy() {
		stop = true;
        if(activeClient!=null) activeClient.forceStop();
        if(submissionWorker!=null) submissionWorker.interrupt();
	}
} //  @jve:decl-index=0:visual-constraint="-9,21"
