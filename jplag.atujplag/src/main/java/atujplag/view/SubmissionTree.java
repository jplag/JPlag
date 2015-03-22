/*
 * Created on Jun 9, 2005
 *
 */
package atujplag.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import atujplag.util.Messages;
import atujplag.util.SubmissionManager;
import atujplag.util.TagParser;

/**
 * @author Emeric Kwemou
 */
class SubmissionTree extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = 8783804082327059648L;

	private OptionPanel gui;

	private JTree invalidTree;
	private DefaultMutableTreeNode invalidRoot = null;
	
	private JTree validTree;
	private DefaultMutableTreeNode validRoot = null;

	public SubmissionTree(OptionPanel gui) {
		this.setTitle(Messages.getString("SubmissionTree.SubmissionTree_Title")); //$NON-NLS-1$
		this.setBackground(JPlagCreator.SYSTEMCOLOR);
		this.gui = gui;

		JPanel contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(350, 550));
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(JPlagCreator.SYSTEMCOLOR);

		JTabbedPane mainPanel = new JTabbedPane();
		mainPanel.setBackground(JPlagCreator.SYSTEMCOLOR);

		makeInvalidTree();
		invalidTree = new JTree(invalidRoot, true);
		invalidTree.setName("JPlag Preview tree"); //$NON-NLS-1$
		JScrollPane invalidScroll = new JScrollPane(invalidTree);
		invalidScroll.getViewport().setBackground(JPlagCreator.SYSTEMCOLOR);
		invalidScroll.getVerticalScrollBar()
				.setBackground(JPlagCreator.SYSTEMCOLOR);
		invalidScroll.setBackground(Color.WHITE);
		invalidScroll.setPreferredSize(new Dimension(350, 500));

		makeValidTree();
		validTree = new JTree(validRoot, true);
		validTree.setName("JPlag Preview tree"); //$NON-NLS-1$

		// This workaround removes all expand controls from empty nodes		
		expandAll();
		collapseAll();
		
		JScrollPane validScroll = new JScrollPane(validTree);
		validScroll.setBackground(JPlagCreator.SYSTEMCOLOR);
		validScroll.getViewport().setBackground(JPlagCreator.SYSTEMCOLOR);
		validScroll.getVerticalScrollBar().setBackground(
				JPlagCreator.SYSTEMCOLOR);
		validScroll.setBackground(Color.WHITE);
		validScroll.setPreferredSize(new Dimension(350, 500));
		mainPanel.setFont(JPlagCreator.SYSTEM_FONT);
		mainPanel.add(Messages.getString("SubmissionTree.Recognized_Structure"), validScroll); //$NON-NLS-1$
		mainPanel.add(Messages.getString("SubmissionTree.Invalid_Items"), invalidScroll); //$NON-NLS-1$

		mainPanel.setBackground(JPlagCreator.SYSTEMCOLOR);
        JPanel pan = JPlagCreator.createPanelWithoutBorder(350,50,10,0,FlowLayout.CENTER);
		JPanel buttons = JPlagCreator.createPanelWithoutBorder(350, 30, 0, 15,FlowLayout.CENTER);
		
		JButton button = JPlagCreator.createButton(
			Messages.getString("SubmissionTree.Expand_all"), //$NON-NLS-1$
			Messages.getString("SubmissionTree.Expand_all_TIP"), //$NON-NLS-1$
			100, 20);
		button.setActionCommand("expand"); //$NON-NLS-1$
		button.addActionListener(this);
		buttons.add(button);
		
		button = JPlagCreator.createButton(
			Messages.getString("SubmissionTree.Collapse_all"), //$NON-NLS-1$
			Messages.getString("SubmissionTree.Collapse_all_TIP"), //$NON-NLS-1$
			100, 20);
		button.setActionCommand("collapse"); //$NON-NLS-1$
		button.addActionListener(this);
		buttons.add(button);

		button = JPlagCreator.createButton(
			Messages.getString("SubmissionTree.Close"), //$NON-NLS-1$
			Messages.getString("SubmissionTree.Close_TIP"), //$NON-NLS-1$
			100, 20);
		button.setActionCommand("close"); //$NON-NLS-1$
		button.addActionListener(this);
		buttons.add(button);
		
		pan.add(buttons);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(pan, BorderLayout.SOUTH);
		setContentPane(contentPane);
		pack();
		setVisible(true);
		addWindowListener(this);
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
            		gui.previewClosed();
                }
            }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        return rootPane;
    }
	
	protected void expandAll() {
		invalidTree.expandRow(0);
		validTree.expandRow(0);
		for (int row = invalidTree.getRowCount() - 1; row > 0; row--) {
			invalidTree.expandRow(row);
		}
		for (int row = validTree.getRowCount() - 1; row > 0; row--) {
			validTree.expandRow(row);
		}
	}
	
	protected void collapseAll() {
		int nrRows = invalidTree.getRowCount();
		for (int row = 1; row < nrRows; row++)
			invalidTree.collapseRow(row);
		int nrRows2 = validTree.getRowCount();
		for (int row = 1; row < nrRows2; row++)
			validTree.collapseRow(row);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("expand")) { //$NON-NLS-1$
			expandAll();
		} else if (command.equals("collapse")) { //$NON-NLS-1$
			collapseAll();
		} else {
			gui.previewClosed();
			setVisible(false);
		}
	}

	private void makeInvalidTree() {
		invalidRoot = new DefaultMutableTreeNode(
			Messages.getString("SubmissionTree.Invalid_Items")); //$NON-NLS-1$

		Vector<SubmissionManager> subs = this.gui.getSubmissions();
		invalidRoot.removeAllChildren();

        // Add all invalid directories
		for (Iterator<SubmissionManager> i = subs.iterator(); i.hasNext();) {
			SubmissionManager sub = i.next();
			if (sub.isValid() || !sub.isDirectory())
				continue;
			String subNodeName = "\"" + sub.name + "\" (" //$NON-NLS-1$ //$NON-NLS-2$
                    + sub.getErrorString() + ")"; //$NON-NLS-1$
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(
					subNodeName);
			invalidRoot.add(subNode);
		}
        
        // Add all invalid files
        for (Iterator<SubmissionManager> i = subs.iterator(); i.hasNext();) {
            SubmissionManager sub = i.next();
            if (sub.isValid() || sub.isDirectory())
                continue;
            String subNodeName = "\"" + sub.name + "\" (" //$NON-NLS-1$ //$NON-NLS-2$
                    + sub.getErrorString() + ")"; //$NON-NLS-1$
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(
                    subNodeName);
            subNode.setAllowsChildren(false);
            invalidRoot.add(subNode);
        }
	}

	private void makeValidTree() {
		validRoot = new DefaultMutableTreeNode(
			Messages.getString("SubmissionTree.Recognized_Structure")); //$NON-NLS-1$

		Vector<SubmissionManager> subs = this.gui.getSubmissions();
		validRoot.removeAllChildren();

		int rootDirLength = ((new File(gui.getClient().getSubmissionDirectory()))
				.getAbsolutePath()).length() + 1;

		for (Iterator<SubmissionManager> i = subs.iterator(); i.hasNext();) {
			SubmissionManager sub = i.next();
			if (!sub.isValid())
				continue;
			String subNodeName = "\"" + sub.name + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
			int nrOfFiles = sub.files.length;
			if (nrOfFiles == 0)
				subNodeName += Messages.getString("SubmissionTree.no_files"); //$NON-NLS-1$
			else if (nrOfFiles == 1)
				subNodeName += Messages.getString("SubmissionTree.one_file"); //$NON-NLS-1$
			else
				subNodeName += TagParser.parse(
					Messages.getString("SubmissionTree.many_files"), //$NON-NLS-1$
					new String []{String.valueOf(nrOfFiles)});
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(
					subNodeName);
			for (int j = 0; j < nrOfFiles; j++) {
				File f = new File(sub.dir, sub.files[j]);
				String parent = (new File(f.getParent())).getAbsolutePath();
				String nodeName = (parent.length() >= rootDirLength
						? f.getAbsolutePath().substring(
								rootDirLength + sub.name.length() + 1)
						: f.getAbsolutePath().substring(rootDirLength));
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						nodeName);
				node.setAllowsChildren(false);
				subNode.add(node);
			}

			validRoot.add(subNode);
		}
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		gui.previewClosed();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
