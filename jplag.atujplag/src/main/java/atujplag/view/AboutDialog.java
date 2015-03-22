/*
 * Author: Moritz Kroll
 * Created: 26.10.2005
 */
package atujplag.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import atujplag.ATUJPLAG;
import atujplag.util.Messages;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = -7851720218708507562L;
	private JPanel jContentPane = null;
	private JLabel jLogoLabel = null;
	private JLabel jProgramLabel = null;
	private JLabel jCopyrightLabel = null;
	private JLabel jAuthorLabel = null;
	private JButton jCloseButton = null;
    private JPanel jTranslationPanel = null;
    private JLabel jTranslationLabel = null;
    
    private String[][] translations = {
            { "French", "Emeric Kwemou" },
            { "German", "Moritz Kroll" },
			{ "Spanish", "Ruben David Gil Ramos" },
            { "BrazilianPortuguese", "Rodrigo Flores" }
    };
    
    /**
	 * This is the default constructor
	 */
	public AboutDialog(JFrame parent) {
		super(parent);
		initialize();
	}

    /**
     * Make dialog close on ESCAPE
     */
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
		this.setSize(414, 290);
		this.setResizable(false);
		this.setTitle(Messages.getString("AboutDialog.About_JPlag")); //$NON-NLS-1$
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jAuthorLabel = new JLabel(Messages.getString("AboutDialog.Authors")); //$NON-NLS-1$
			jAuthorLabel.setAlignmentX(0.5F);
			jCopyrightLabel = new JLabel(Messages.getString("AboutDialog.Copyright")); //$NON-NLS-1$
			jCopyrightLabel.setAlignmentX(0.5F);
			jProgramLabel = new JLabel(ATUJPLAG.programName);
			jProgramLabel.setAlignmentX(0.5F);
			jLogoLabel = new JLabel(new ImageIcon(getClass().getResource("/atujplag/data/biglogo.gif"))); //$NON-NLS-1$
			jLogoLabel.setBorder(BorderFactory.createLineBorder(Color.gray,1));
			jLogoLabel.setAlignmentX(0.5F);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			jContentPane.add(jLogoLabel, null);
			jContentPane.add(Box.createRigidArea(new Dimension(0,10)));
			jContentPane.add(jProgramLabel, null);
			jContentPane.add(jCopyrightLabel, null);
			jContentPane.add(Box.createRigidArea(new Dimension(0,10)));
			jContentPane.add(jAuthorLabel, null);
            jContentPane.add(Box.createRigidArea(new Dimension(0,10)));
			jContentPane.add(getJTranslationPanel(), null);
			jContentPane.add(Box.createRigidArea(new Dimension(0,15)));
			jContentPane.add(getJCloseButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if(jCloseButton == null) {
			jCloseButton = JPlagCreator.createButton(
				Messages.getString("AboutDialog.Close_about_box"), //$NON-NLS-1$
				Messages.getString("AboutDialog.Close_about_box_TIP"), //$NON-NLS-1$
				100, 20);
			jCloseButton.setMinimumSize(new Dimension(100,20));
			jCloseButton.setMaximumSize(new Dimension(100,20));
			jCloseButton.setAlignmentX(0.5F);
			jCloseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

    /**
     * This method initializes jTranslationPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJTranslationPanel() {
        if(jTranslationPanel == null) {
			/*
			 * GridBagConstraints gridBagConstraints4 = new
			 * GridBagConstraints(); gridBagConstraints4.gridx = 2;
			 * gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			 * gridBagConstraints4.gridy = 1; jMoritzKrollLabel = new JLabel();
			 * jMoritzKrollLabel.setText("Moritz Kroll"); GridBagConstraints
			 * gridBagConstraints3 = new GridBagConstraints();
			 * gridBagConstraints3.gridx = 1; gridBagConstraints3.anchor =
			 * java.awt.GridBagConstraints.WEST; gridBagConstraints3.gridy = 1;
			 * jGermanLabel = new JLabel();
			 * jGermanLabel.setText(Messages.getString("AboutDialog.German"));
			 * GridBagConstraints gridBagConstraints21 = new
			 * GridBagConstraints(); gridBagConstraints21.gridx = 2;
			 * gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			 * gridBagConstraints21.gridy = 0; jEmericKwemouLabel = new
			 * JLabel(); jEmericKwemouLabel.setText("Emeric Kwemou");
			 * GridBagConstraints gridBagConstraints11 = new
			 * GridBagConstraints(); gridBagConstraints11.gridx = 1;
			 * gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			 * gridBagConstraints11.gridy = 0; jFrenchLabel = new JLabel();
			 * jFrenchLabel.setText(Messages.getString("AboutDialog.French"));
			 * GridBagConstraints gridBagConstraints2 = new
			 * GridBagConstraints(); gridBagConstraints2.gridx = 2;
			 * gridBagConstraints2.insets = new java.awt.Insets(0,0,0,30);
			 * gridBagConstraints2.gridy = 2; jRubenGilLabel = new JLabel();
			 * jRubenGilLabel.setText("Rub�n David Gil Ramos");
			 * GridBagConstraints gridBagConstraints1 = new
			 * GridBagConstraints(); gridBagConstraints1.gridx = 1;
			 * gridBagConstraints1.insets = new java.awt.Insets(0,0,0,40);
			 * gridBagConstraints1.gridy = 2; jSpanishLabel = new JLabel();
			 * jSpanishLabel.setText(Messages.getString("AboutDialog.Spanish"));
			 */
            jTranslationPanel = new JPanel();
            jTranslationPanel.setLayout(new GridBagLayout());
            jTranslationLabel = new JLabel();
            jTranslationLabel.setText(Messages.getString("AboutDialog.Translations"));
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0,0,0,20);
            jTranslationPanel.add(jTranslationLabel, gridBagConstraints);
            
            Insets langInsets = new Insets(0, 0, 0, 40);
            Insets nameInsets = new Insets(0, 0, 0, 30);
			for (int i = 0; i < translations.length; i++) {
				JLabel langLabel = new JLabel(Messages.getString("AboutDialog." + translations[i][0]));
				jTranslationPanel.add(langLabel, new GridBagConstraints(2, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						langInsets, 0, 0));

				JLabel nameLabel = new JLabel(translations[i][1]);
				jTranslationPanel.add(nameLabel, new GridBagConstraints(3, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						nameInsets, 0, 0));
			}
/*            jTranslationPanel.add(jSpanishLabel, gridBagConstraints1);
            jTranslationPanel.add(jRubenGilLabel, gridBagConstraints2);
            jTranslationPanel.add(jFrenchLabel, gridBagConstraints11);
            jTranslationPanel.add(jEmericKwemouLabel, gridBagConstraints21);
            jTranslationPanel.add(jGermanLabel, gridBagConstraints3);
            jTranslationPanel.add(jMoritzKrollLabel, gridBagConstraints4);*/
        }
        return jTranslationPanel;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
