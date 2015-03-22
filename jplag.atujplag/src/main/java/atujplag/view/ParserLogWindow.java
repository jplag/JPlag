/*
 * Created on Jul 25, 2005
 */
package atujplag.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import atujplag.util.Messages;

/**
 * @author Emeric Kwemou
 */
public class ParserLogWindow extends JDialog {
	private static final long serialVersionUID = 7020008650990374563L;
	private javax.swing.JPanel jContentPane = null;
    private JScrollPane jScrollPane = null;
    private JEditorPane jEditorPane = null;
	private JPanel jPanel = null;
	private JButton jOKButton = null;

	/**
	 * This is the default constructor
	 */
	public ParserLogWindow(String message, String title, JFrame parent) {
		super(parent);
		setTitle(title);
		initialize();
		getJEditorPane().setText(message);
		getJEditorPane().setCaretPosition(0);
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
        this.setContentPane(getJContentPane());
        this.setResizable(true);
    }
    
    /**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = JPlagCreator.createPanelWithoutBorder(600, 400, 0,
					0, FlowLayout.CENTER);
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(),BorderLayout.CENTER);
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
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
            jScrollPane.setPreferredSize(new java.awt.Dimension(500, this.getJContentPane().getPreferredSize().height-30));
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
			jEditorPane.setPreferredSize(new java.awt.Dimension(400, 200));
			jEditorPane.setEditable(false);
			jEditorPane.setContentType("text/html"); //$NON-NLS-1$
		}
		return jEditorPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setPreferredSize(new java.awt.Dimension(400, 30));
			jPanel.setBackground(JPlagCreator.SYSTEMCOLOR);
			jPanel.add(getOKButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOKButton() {
		if (jOKButton == null) {
			jOKButton = JPlagCreator.createButton(
					Messages.getString("ParserLogDialog.Close"), //$NON-NLS-1$
					Messages.getString("ParserLogDialog.Close_TIP"), //$NON-NLS-1$
					100, 20);
			jOKButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ParserLogWindow.this.dispose();
				}
			});
			jOKButton.setFont(JPlagCreator.SYSTEM_FONT);
		}
		return jOKButton;
	}

	public void setMessage(String message) {
		getJEditorPane().setText(message);
		getJEditorPane().setCaretPosition(0);
	}
}