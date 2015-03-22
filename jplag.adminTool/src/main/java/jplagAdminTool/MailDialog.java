/*
 * Created on 12.06.2005
 * Author: Moritz Kroll
 */

package jplagAdminTool;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
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
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import jplagWsClient.jplagClient.MailTemplate;
import jplagWsClient.jplagClient.RequestData;
import jplagWsClient.jplagClient.SetMailTemplateParams;

public class MailDialog extends JDialog {
	private static final long serialVersionUID = -384670645825122963L;

	public static final String JPLAG_SERVER = "https://www.ipd.kit.edu/jplag";

	public static final int MAIL_ACCEPTED = 0;
	public static final int MAIL_DECLINED = 1;
	public static final int MAIL_SERVER = 2;
    public static final int MAIL_ALL = 3;
	
	public static final int USERNAME=0;
	public static final int PASSWORD=1;
	public static final int EXPIRES=2;
	public static final int REALNAME=3;
	public static final int EMAIL=4;
	public static final int EMAILSECOND=5;
	public static final int HOMEPAGE=6;
	public static final int SERVER=7;
	
	private static final String[] tagNames = { "username", "password", "expires", "realname", "email", "emailSecond", "homepage", "server" };

	/*
	 * public static final int S_USERNAME=0; public static final int
	 * S_PASSWORD=1; public static final int S_EXPIRES=2; public static final
	 * int S_LASTUSAGE=3; public static final int S_NUMOFSUBS=4; public static
	 * final int S_REALNAME=5; public static final int S_EMAIL=6; public static
	 * final int S_EMAILSECOND=7; public static final int S_HOMEPAGE=8; public
	 * static final int S_SERVER=9;
	 * 
	 * private static final String[] serverTagNames = { "username", "password",
	 * "expires", "lastUsage", "numOfSubs", "realname", "email", "emailSecond",
	 * "homepage", "server" };
	 */
    
	private static final String[] typeNames = { "accepted", "declined", "serverMail" };
	
	private javax.swing.JPanel jContentPane = null;
	private JTextArea jTemplateTextArea = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JScrollPane jTemplateScrollPane = null;
	private JScrollPane jPreviewScrollPane = null;
	private JTextArea jPreviewTextArea = null;
	private JPanel jButtonPanel = null;
	private JButton jSaveAndSendButton = null;
	private JButton jSendButton = null;
	private JButton jCancelButton = null;
	private JButton jSaveButton = null;
	private JComboBox<String> jTemplateComboBox = null;
	private JLabel jLabel2 = null;
	private JComboBox<String> jTagComboBox = null;
	
    private Vector<MailTemplate> templates = null;
	private AdminTool adminTool = null;
	private int type = 0;
	private RequestData reqData = null;
    private boolean showSendButtons = true;
	
	private boolean cancelled = true;
	private String preview = "";
	private String subject = "";
	private JTextField jSubjectField = null;
	private JLabel jLabel3 = null;
    private JPanel jTypeChoosePanel = null;
    private JLabel jChooseTypeLabel = null;
    private JComboBox<String> jTypeComboBox = null;
    private JButton jCopyTemplateButton = null;
    private JButton jRenameTemplateButton = null;
    private JButton jDeleteTemplateButton = null;
	
	public MailDialog(int typ, String title, RequestData rd, JDialog parent,
			AdminTool at) {
		super(parent,true);		 // make modal
        init(typ,title,rd,at);
    }
        
	public MailDialog(int typ, String title, RequestData rd, AdminTool at) {
		super(at,true);		     // make modal
        init(typ,title,rd,at);
	}
	
    private void init(int typ, String title, RequestData rd, AdminTool at) {
        adminTool = at;
        type = typ;
        reqData = rd;
        if(type == MAIL_ALL) {
            showSendButtons = false;
            type = MAIL_ACCEPTED;
        }
        try {
            MailTemplate[] temps = at.getJPlagStub().getMailTemplates(type).
                    getItems();
            templates = new Vector<MailTemplate>(temps.length,3);
            for(int i=0;i<temps.length;i++)
                templates.add(temps[i]);
        }
        catch(Exception ex) {
            at.CheckException(ex,at);
        }
        initialize(title);
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

	public boolean isCancelled() {
		return cancelled;
	}
	
	public String getMailString() {
		return preview;
	}
	
	public String getMailSubjectString() {
		return subject;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(String title) {
		this.setSize(640, 400);
		this.setTitle(title);
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
			jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
            if(!showSendButtons)
                jContentPane.add(getJTypeChoosePanel(),
                    java.awt.BorderLayout.NORTH);
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
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 4;
			gridBagConstraints10.insets = new java.awt.Insets(0,5,0,0);
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 3;
			gridBagConstraints1.insets = new java.awt.Insets(0,5,0,0);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.insets = new java.awt.Insets(0,5,0,0);
			gridBagConstraints.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.gridwidth = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.insets = new java.awt.Insets(10,5,0,5);
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.gridy = 1;
			gridBagConstraints51.insets = new java.awt.Insets(10,0,0,0);
			gridBagConstraints51.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3 = new JLabel();
			jLabel3.setText("Subject:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 3;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.insets = new java.awt.Insets(5,5,0,0);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.insets = new java.awt.Insets(10,0,0,0);
			gridBagConstraints8.gridy = 1;
			jLabel2 = new JLabel();
			jLabel2.setText("Insert tag:");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new java.awt.Insets(0,5,0,5);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(5,10,0,0);
			gridBagConstraints5.gridwidth = 6;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.insets = new java.awt.Insets(5,10,5,0);
			gridBagConstraints4.gridwidth = 5;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 3;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridwidth = 4;
			jLabel1 = new JLabel();
			jLabel1.setText("E-Mail preview:");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			jLabel = new JLabel();
			jLabel.setText("Template:");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			jPanel.add(getJPreviewScrollPane(), gridBagConstraints5);
			jPanel.add(getJSubjectField(), gridBagConstraints6);
			jPanel.add(jLabel3, gridBagConstraints51);
			jPanel.add(getJTagComboBox(), gridBagConstraints9);
			jPanel.add(jLabel2, gridBagConstraints8);
			jPanel.add(getJTemplateComboBox(), gridBagConstraints7);
			jPanel.add(jLabel, gridBagConstraints2);
			jPanel.add(jLabel1, gridBagConstraints3);
			jPanel.add(getJTemplateScrollPane(), gridBagConstraints4);
			jPanel.add(getJCopyTemplateButton(), gridBagConstraints);
			jPanel.add(getJRenameTemplateButton(), gridBagConstraints1);
			jPanel.add(getJDeleteTemplateButton(), gridBagConstraints10);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTemplateScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJTemplateScrollPane() {
		if (jTemplateScrollPane == null) {
			jTemplateScrollPane = new JScrollPane();
			jTemplateScrollPane.setViewportView(getJTemplateTextArea());
		}
		return jTemplateScrollPane;
	}

    /**
     * This method initializes jTextArea    
     *  
     * @return javax.swing.JTextArea    
     */    
	private JTextArea getJTemplateTextArea() {
		if (jTemplateTextArea == null) {
			jTemplateTextArea = new JTextArea();
			jTemplateTextArea.setLineWrap(true);
			jTemplateTextArea.setWrapStyleWord(true);
			jTemplateTextArea.getDocument().addDocumentListener(new DocumentListener() {
				private Timer timer = null;

				@SuppressWarnings("serial")
				private Timer getTimer() {
					if (timer == null) {
						timer = new Timer(300, new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								updatePreview();
							}
						});
						timer.setRepeats(false);
					}
					return timer;
				}

				public void changedUpdate(DocumentEvent arg0) {
					getTimer().restart();
				}

				public void insertUpdate(DocumentEvent arg0) {
					getTimer().restart();
				}

				public void removeUpdate(DocumentEvent arg0) {
					getTimer().restart();
				}

			});
		}
		return jTemplateTextArea;
    }
    
	/**
	 * This method initializes jPreviewScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJPreviewScrollPane() {
		if (jPreviewScrollPane == null) {
			jPreviewScrollPane = new JScrollPane();
			jPreviewScrollPane.setViewportView(getJPreviewTextArea());
		}
		return jPreviewScrollPane;
	}

	/**
	 * This method initializes jPreviewTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJPreviewTextArea() {
		if (jPreviewTextArea == null) {
			jPreviewTextArea = new JTextArea();
            jPreviewTextArea.setLineWrap(true);
            jPreviewTextArea.setWrapStyleWord(true);
			jPreviewTextArea.setEditable(false);
		}
		return jPreviewTextArea;
	}

	/**
	 * This method initializes jButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
            if(showSendButtons)
            {
                jButtonPanel.add(getJSendButton(), null);
                jButtonPanel.add(getJCancelButton(), null);
                jButtonPanel.add(Box.createHorizontalStrut(20),null);
                jButtonPanel.add(getJSaveButton(), null);
				jButtonPanel.add(getJSaveAndSendButton(), null);
            }
            else {
                jButtonPanel.add(getJSaveButton(), null);
                jButtonPanel.add(getJCancelButton(), null);
            }
		}
		return jButtonPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJSaveAndSendButton() {
		if (jSaveAndSendButton == null) {
			jSaveAndSendButton = new JButton();
			jSaveAndSendButton.setText("Save and send");
			jSaveAndSendButton.addActionListener(
                new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					subject=getJSubjectField().getText();
					saveTemplate();
					cancelled = false;
					setVisible(false);
                    dispose();
				}
			});
		}
		return jSaveAndSendButton;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJSendButton() {
		if (jSendButton == null) {
			jSendButton = new JButton();
			jSendButton.setText("Send");
			jSendButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					subject=getJSubjectField().getText();
					cancelled = false;
					setVisible(false);
                    dispose();
				}
			});
		}
		return jSendButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setText(showSendButtons ? "Cancel" : "Close");
			jCancelButton.addActionListener(
                new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setVisible(false);
                    dispose();
				}
			});
		}
		return jCancelButton;
	}
	
    private void setMailTemplate(String name, String subject, String data) {
        try {
            adminTool.getJPlagStub().setMailTemplate(new SetMailTemplateParams(
                    type,new MailTemplate(name,subject,data)));
        }
        catch(Exception ex) {
            adminTool.CheckException(ex,this);
        }
    }
    
	private void saveTemplate() {
        String name = (String) getJTemplateComboBox().getSelectedItem();
        String data = getJTemplateTextArea().getText();
        setMailTemplate(name, subject, data);

        // Update template object
        
        int tempNum = jTemplateComboBox.getSelectedIndex();
        if(tempNum < 0)     // if no name selected don't update...
            return;         // (this shouldn't happen...)
        
        MailTemplate tmpl = templates.get(tempNum);
        tmpl.setName(name);
        tmpl.setSubject(subject);
        tmpl.setData(data);
    }

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJSaveButton() {
		if (jSaveButton == null) {
			jSaveButton = new JButton();
			jSaveButton.setText("Save");
			jSaveButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					subject=getJSubjectField().getText();
					saveTemplate();
                }
			});
		}
		return jSaveButton;
	}
	
	private void updatePreview() {
		String template = getJTemplateTextArea().getText();
		String[] tokens = template.split("[{}]");
		// TODO: Check whether every '{' has a following '}'
		preview = tokens[0];
		for(int i=1;i<tokens.length;i+=2)
		{
			int j;
			for(j=0;j<tagNames.length;j++)
			{
				if(tagNames[j].equals(tokens[i]))
					break;
			}
			switch(j)
			{
				case USERNAME: preview += reqData.getUsername(); break;
				case PASSWORD: preview += reqData.getPassword(); break;
				case REALNAME: preview += reqData.getRealName(); break;
				case EMAIL: preview += reqData.getEmail(); break;
				case EMAILSECOND: preview += reqData.getEmailSecond(); break;
				case HOMEPAGE: preview += reqData.getHomepage(); break;
				case SERVER: preview += JPLAG_SERVER; break;
				default: preview += "[N/A]"; break;
			}
			if(i+1<tokens.length) preview += tokens[i+1];
		}
		getJPreviewTextArea().setText(preview);
		try
		{
			// Set preview cursor into the same line as the template cursor  
			int viewline=getJTemplateTextArea().getLineOfOffset(
					getJTemplateTextArea().getCaretPosition());
			getJPreviewTextArea().setCaretPosition(
					getJPreviewTextArea().getLineStartOffset(viewline));
			
			// Let preview viewport start painting at the same line as
			// the template viewport
			Point p=getJTemplateScrollPane().getViewport().getViewPosition();
			getJPreviewScrollPane().getViewport().setViewPosition(p);
		}
		catch(BadLocationException ex) {}	// just don't scroll
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox<String> getJTemplateComboBox() {
		if (jTemplateComboBox == null) {
			String[] templateNames = new String[templates.size()];
			for(int i=0;i<templates.size();i++)
				templateNames[i] = templates.get(i).getName();
			jTemplateComboBox = new JComboBox<String>(templateNames);
			jTemplateComboBox.addActionListener(
                new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int tempNum = jTemplateComboBox.getSelectedIndex();
					if(tempNum < 0)		// if new name selected
						return;			// don't change the text
                    MailTemplate tmpl = templates.get(tempNum);
					getJSubjectField().setText(tmpl.getSubject());
					getJTemplateTextArea().setText(tmpl.getData());
					getJTemplateTextArea().setCaretPosition(0);
					updatePreview();
				}
			});
			if(templates.size()>0)
				jTemplateComboBox.setSelectedIndex(0);
		}
		return jTemplateComboBox;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox<String> getJTagComboBox() {
		if (jTagComboBox == null) {
			jTagComboBox = new JComboBox<String>(tagNames);
			jTagComboBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JTextArea ta = getJTemplateTextArea();
					ta.insert("{" + (String) jTagComboBox.getSelectedItem() +
							"}",ta.getCaretPosition());
				}
			});
		}
		return jTagComboBox;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJSubjectField() {
		if (jSubjectField == null) {
			jSubjectField = new JTextField();
			jSubjectField.setColumns(51);
		}
		return jSubjectField;
	}

    /**
     * This method initializes jTypeChoosePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJTypeChoosePanel() {
        if(jTypeChoosePanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout.setVgap(0);
            jChooseTypeLabel = new JLabel();
            jChooseTypeLabel.setText("Type:");
            jChooseTypeLabel.setPreferredSize(jLabel.getPreferredSize());
            jTypeChoosePanel = new JPanel();
            jTypeChoosePanel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(5,0,0,0));
            jTypeChoosePanel.setLayout(flowLayout);
            jTypeChoosePanel.add(jChooseTypeLabel, null);
            jTypeChoosePanel.add(getJTypeComboBox(), null);
        }
        return jTypeChoosePanel;
    }
    
    private void setButtonsEnabled(boolean enabled) {
        if(showSendButtons) {
            getJSendButton().setEnabled(enabled);
            getJSaveAndSendButton().setEnabled(enabled);
        }
        getJSaveButton().setEnabled(enabled);
    }

    /**
     * This method initializes jTypeComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox<String> getJTypeComboBox() {
        if(jTypeComboBox == null) {
			jTypeComboBox = new JComboBox<String>(typeNames);
            jTypeComboBox.addActionListener(
                new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    final SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                MailTemplate[] temps = adminTool.getJPlagStub().
                                    getMailTemplates(type).getItems();
                                templates =
                                    new Vector<MailTemplate>(temps.length,3);
                                for(int i=0;i<temps.length;i++)
                                    templates.add(temps[i]);
                            }
                            catch(Exception ex)
                            {
                                adminTool.CheckException(ex,adminTool);
                            }
                            return null;
                        }
                        
                        public void finished() {
                            jTemplateComboBox.removeAllItems();
                            for(int i=0;i<templates.size();i++)
                                jTemplateComboBox.addItem(
                                    templates.get(i).getName());

                            if(templates.size()>0)
                                jTemplateComboBox.setSelectedIndex(0);
                            
                            setButtonsEnabled(true);
                        }
                    };
                    int newtype = jTypeComboBox.getSelectedIndex();
                    if(newtype == type) return;
                    setButtonsEnabled(false);
                    type = newtype;
                    worker.start();
                }
            });
        }
        return jTypeComboBox;
    }

    /**
     * This method initializes jCopyTemplateButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJCopyTemplateButton() {
        if(jCopyTemplateButton == null) {
            jCopyTemplateButton = new JButton();
            jCopyTemplateButton.setText("Copy");
            jCopyTemplateButton.addActionListener(
                new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String newname = JOptionPane.showInputDialog(
                        MailDialog.this, "Please enter the new template name" +
                        "\n(You will have to save the new template before\n" +
                        "switching to another one to avoid loosing the text):",
                        "Add new template", JOptionPane.PLAIN_MESSAGE);
                    if(newname.length()==0) {
                        JOptionPane.showMessageDialog(MailDialog.this,
                            "You didn't provide a new template name!",
                            "Adding new template aborted",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    MailTemplate newtemp = new MailTemplate(newname,
                        getJSubjectField().getText(),
                        getJTemplateTextArea().getText());
                    templates.add(newtemp);
                    getJTemplateComboBox().addItem(newname);
                    getJTemplateComboBox().setSelectedItem(newname);
                }
            });
        }
        return jCopyTemplateButton;
    }

    /**
     * This method initializes jRenameTemplateButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJRenameTemplateButton() {
        if(jRenameTemplateButton == null) {
            jRenameTemplateButton = new JButton();
            jRenameTemplateButton.setText("Rename");
            jRenameTemplateButton.addActionListener(
                new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int tempNum = jTemplateComboBox.getSelectedIndex();
                    if(tempNum < 0)     // if no name selected
                        return;         // don't rename
                    MailTemplate tmpl = templates.get(tempNum);
                    
                    String newname = JOptionPane.showInputDialog(
                        MailDialog.this, "The old template name is:\n     "
                            + tmpl.getName() + "\nPlease enter the new "
                            + "template name:",
                        "Rename template", JOptionPane.PLAIN_MESSAGE);

                    setMailTemplate(tmpl.getName(),newname,"");
                    tmpl.setName(newname);
                    jTemplateComboBox.removeItemAt(tempNum);
                    jTemplateComboBox.insertItemAt(newname,tempNum);
                    jTemplateComboBox.setSelectedItem(newname);
                }
            });
        }
        return jRenameTemplateButton;
    }

    /**
     * This method initializes jDeleteTemplateButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJDeleteTemplateButton() {
        if(jDeleteTemplateButton == null) {
            jDeleteTemplateButton = new JButton();
            jDeleteTemplateButton.setText("Delete");
            jDeleteTemplateButton.addActionListener(
                new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int tempNum = jTemplateComboBox.getSelectedIndex();
                    if(tempNum < 0)     // if no name selected
                        return;         // don't rename
                    MailTemplate tmpl = templates.get(tempNum);
                    
                    if(JOptionPane.showConfirmDialog(MailDialog.this,
                            "Do you really want to delete this template:\n     "
                            + tmpl.getName() + "?", "Delete template",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)==JOptionPane.NO_OPTION)
                        return;
                    
                    setMailTemplate(tmpl.getName(),"","");
                    jTemplateComboBox.removeItemAt(tempNum);
                    templates.remove(tempNum);
                }
            });
        }
        return jDeleteTemplateButton;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
