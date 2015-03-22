package jplagAdminTool;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class DetailsDialog extends JDialog {
	private static final long serialVersionUID = -4082825477146193323L;

	private javax.swing.JPanel jContentPane = null;

	private BackedUserData data;
	private AdminTool adminTool;

	private JLabel jLabel = null;

	private JTextField jEmail2ndTextField = null;

	private JLabel jLabel1 = null;

	private JTextField jHomepageTextField = null;

	private JLabel jLabel2 = null;

	private JScrollPane jScrollPane = null;

	private JTextArea jReasonTextArea = null;

	private JLabel jLabel3 = null;

	private JScrollPane jScrollPane1 = null;

	private JTextArea jNotesTextArea = null;

	private JPanel jPanel = null;

	private JButton jApplyButton = null;

	private JButton jCancelButton = null;

    private JButton jShowHomepageButton = null;
	
	/**
	 * This is the default constructor
	 */
	public DetailsDialog(BackedUserData ud, AdminTool parent) {
		super(parent,true);
		data=ud;
		adminTool=parent;
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
		this.setSize(417, 276);
		this.setTitle("Details for user \"" + data.getUsername() + "\"");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 4;
			gridBagConstraints11.gridwidth = 3;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new java.awt.Insets(5,0,0,0);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.weighty = 1.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 3;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel3 = new JLabel();
			jLabel3.setText("Notes:");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.weighty = 1.0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jLabel2 = new JLabel();
			jLabel2.setText("Reason:");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.insets = new java.awt.Insets(0,0,0,5);
			jLabel1 = new JLabel();
			jLabel1.setText("Homepage:");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			jLabel = new JLabel();
			jLabel.setText("2. Email:");
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			jContentPane.add(getJPanel(), gridBagConstraints11);
			jContentPane.add(getJScrollPane1(), gridBagConstraints10);
			jContentPane.add(jLabel3, gridBagConstraints9);
			jContentPane.add(getJScrollPane(), gridBagConstraints8);
			jContentPane.add(jLabel2, gridBagConstraints7);
			jContentPane.add(jLabel, gridBagConstraints3);
			jContentPane.add(getJHomepageTextField(), gridBagConstraints6);
			jContentPane.add(getJEmail2ndTextField(), gridBagConstraints4);
			jContentPane.add(jLabel1, gridBagConstraints5);
			jContentPane.add(getJShowHomepageButton(), gridBagConstraints);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJEmail2ndTextField() {
		if (jEmail2ndTextField == null) {
			jEmail2ndTextField = new JTextField();
			jEmail2ndTextField.setText(data.getEmailSecond());
			jEmail2ndTextField.setPreferredSize(new java.awt.Dimension(300,20));
		}
		return jEmail2ndTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJHomepageTextField() {
		if (jHomepageTextField == null) {
			jHomepageTextField = new JTextField();
			jHomepageTextField.setText(data.getHomepage());
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
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJReasonTextArea() {
		if (jReasonTextArea == null) {
			jReasonTextArea = new JTextArea();
			jReasonTextArea.setText(data.getReason());
			jReasonTextArea.setLineWrap(true);
			jReasonTextArea.setWrapStyleWord(true);
			jReasonTextArea.setRows(7);
            jReasonTextArea.setCaretPosition(0);
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
	 * This method initializes jTextArea1	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJNotesTextArea() {
		if (jNotesTextArea == null) {
			jNotesTextArea = new JTextArea();
			jNotesTextArea.setText(data.getNotes());
			jNotesTextArea.setLineWrap(true);
			jNotesTextArea.setWrapStyleWord(true);
			jNotesTextArea.setRows(6);
            jNotesTextArea.setCaretPosition(0);
		}
		return jNotesTextArea;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout12 = new FlowLayout();
			flowLayout12.setVgap(0);
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout12);
			jPanel.add(getJApplyButton(), null);
			jPanel.add(getJCancelButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJApplyButton() {
		if (jApplyButton == null) {
			jApplyButton = new JButton();
			jApplyButton.setText("Apply");
			jApplyButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					data.setEmailSecond(getJEmail2ndTextField().getText());
					data.setHomepage(getJHomepageTextField().getText());
					data.setReason(getJReasonTextArea().getText());
					data.setNotes(getJNotesTextArea().getText());
					if(adminTool.userDataChanged(data)) {
						setVisible(false);
                        dispose();
                    }
				}
			});
		}
		return jApplyButton;
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
                    dispose();
				}
			});
		}
		return jCancelButton;
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
                        DetailsDialog.this);
                }
            });
        }
        return jShowHomepageButton;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
