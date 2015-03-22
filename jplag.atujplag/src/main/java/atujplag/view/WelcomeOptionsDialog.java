package atujplag.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import atujplag.ATUJPLAG;
import atujplag.util.Messages;
import atujplag.util.TagParser;

public class WelcomeOptionsDialog extends JDialog {
	private static final long serialVersionUID = -6372261277476829706L;
	private JPanel reportLocPanel = null;
	private JTextField resultDirField = null;
	private JButton confirmResultLocButton = null;
	private JButton resultDirButton = null;
	private JComboBox<String> jLanguageCB = null;
    
    private String resultDir = null;
    private String language = null;

	public WelcomeOptionsDialog(JFrame parent) {
		super(parent);
		initialize();
	}
	
	private void initialize() {
		setTitle("Welcome to the JPlag Web Start client!"); //$NON-NLS-1$
		setContentPane(getReportLocPanel());
		setResizable(false);
		setModal(true);
		pack();
		setLocationRelativeTo(null);
	}
	
	private JPanel getReportLocPanel() {
		if(reportLocPanel == null) {
            reportLocPanel = JPlagCreator.createPanel(500, 136, 10, 10,
                    FlowLayout.CENTER);

            JLabel reportLocLabel = new JLabel(Messages.getString(
                    "Preferences.Report_location_DESC") + ":");  //$NON-NLS-1$  //$NON-NLS-2$
            reportLocLabel.setPreferredSize(new Dimension(290 + 150 + 10 + 24,20));
            reportLocPanel.add(reportLocLabel);
            
			resultDirField = JPlagCreator.createTextField(290 + 150, 20, null);
			String home_directory = System.getProperty("user.home") //$NON-NLS-1$
					+ File.separator + "JPlag_Results"; //$NON-NLS-1$
			resultDirField.setText(home_directory);
            reportLocPanel.add(resultDirField);
            
			resultDirButton = JPlagCreator.createOpenFileButton(
                    Messages.getString("Preferences.Select_report_location")); //$NON-NLS-1$
			resultDirButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setDialogTitle(Messages.getString(
                    		"Preferences.Select_report_location")); //$NON-NLS-1$
					int retval = chooser.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION) {
						resultDirField.setText(chooser.getSelectedFile().getPath());
					}
				}});
            reportLocPanel.add(resultDirButton);
            
            JLabel jLanguageLabel = new JLabel(Messages.getString(
                    "Preferences.Language") + ":");  //$NON-NLS-1$  //$NON-NLS-2$
            jLanguageLabel.setPreferredSize(new Dimension(90 + 150 + 24,20));
            reportLocPanel.add(jLanguageLabel, null);
            
            jLanguageCB = JPlagCreator.createJComboBox(
                    ATUJPLAG.COUNTRY_LANGUAGES, 200, 20,
                    Messages.getString("Preferences.Language_TIP")); //$NON-NLS-1$
            reportLocPanel.add(jLanguageCB);
            
			confirmResultLocButton = JPlagCreator.createButton(
                    Messages.getString("Preferences.Save_report_location"), //$NON-NLS-1$
					Messages.getString("Preferences.Save_report_location_TIP"), //$NON-NLS-1$
					150, 20);
			reportLocPanel.add(confirmResultLocButton, confirmResultLocButton.getName());
			confirmResultLocButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					File f = new File(resultDirField.getText());
					if (!f.exists()) {
						int rep = JPlagCreator.showConfirmDialog(
								Messages.getString(
									"Preferences.Directory_does_not_exist"), //$NON-NLS-1$
								TagParser.parse(Messages.getString(
									"Preferences.Directory_does_not_exist_DESC_{1_PATH}"), //$NON-NLS-1$
									new String[] { f.getPath() }));
						if (rep == JOptionPane.NO_OPTION)
                            return;
                        
						f.mkdirs();
					}
                    else if(!f.isDirectory()) {
                        JPlagCreator.showMessageDialog(
                                Messages.getString(
                                    "Preferences.File_is_no_directory"), //$NON-NLS-1$
                                TagParser.parse(Messages.getString(
                                    "Preferences.File_is_no_directory_DESC_{1_PATH}"), //$NON-NLS-1$
                                    new String[] { f.getPath() }));
                        return;
                    }
					resultDir = resultDirField.getText();
                    language = jLanguageCB.getSelectedItem().toString();
					dispose();
				}
			});
			getRootPane().setDefaultButton(confirmResultLocButton);
        }

		return reportLocPanel;
	}
	
	public String askForResultDir() {
		setVisible(true);
		return resultDir;
	}
    
    public String getResultDir() {
        return resultDir;
    }
    
    public String getLanguage() {
        return language;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
