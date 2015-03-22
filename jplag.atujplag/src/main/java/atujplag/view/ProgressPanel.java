/*
 * Created on 09.09.2005
 */
package atujplag.view;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import atujplag.client.Client;
import atujplag.util.Messages;

/**
 * @author ekwemou
 */
public class ProgressPanel extends JPanel {
	private static final long serialVersionUID = 816563293538109899L;
	private JLabel packing = null;
	private JLabel sending = null;
	private JLabel waiting = null;
	private JLabel parsing = null;
	private JLabel comparing = null;
	private JLabel loading = null;

	private int active;

	/**
	 * This is the default constructor
	 */
	public ProgressPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		FlowLayout flowLayout1 = new FlowLayout();

		this.setLayout(flowLayout1);
		packing = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Packing_files"), 200, 20); //$NON-NLS-1$
		sending = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Sending_files"), 200, 20); //$NON-NLS-1$
		waiting = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Waiting_in_queue"),200, 20); //$NON-NLS-1$
		parsing = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Parsing_files"), 200, 20); //$NON-NLS-1$
		comparing = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Comparing_files"), 200, 20); //$NON-NLS-1$
		loading = JPlagCreator.createLabel(Messages.getString(
            "ProgressPanel.Loading_results"), 200, 20); //$NON-NLS-1$
		loading.setBackground(Color.WHITE);

		this.setPreferredSize(new java.awt.Dimension(200, 120));

		// TODO: Upload missing file to repository!!
		packing.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		sending.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		waiting.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		parsing.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		comparing.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		loading.setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/current.gif"))); //$NON-NLS-1$
		flowLayout1.setHgap(50);
		flowLayout1.setVgap(0);
		flowLayout1.setAlignment(java.awt.FlowLayout.CENTER);
		this.setBackground(JPlagCreator.SYSTEMCOLOR);
		this.add(packing);
		this.add(sending);
		this.add(waiting);
		this.add(parsing);
		this.add(comparing);
		this.add(loading);
	}

	private void setActive(int label) {
		if (label == Client.CANCELLING || label == Client.STOPPED)
			return;
		active = label;
		JLabel[] labels = { packing, sending, waiting, parsing, comparing,
                loading };
		for (int i = 0; i < label; i++) {
			labels[i].setIcon(new ImageIcon(getClass().getResource(
					"/atujplag/data/done.gif"))); //$NON-NLS-1$
			labels[i].setFont(JPlagCreator.SYSTEM_FONT);
		}
		if (label != Client.END) {
			labels[label].setIcon(new ImageIcon(getClass().getResource(
					"/atujplag/data/current.gif"))); //$NON-NLS-1$
			labels[label].setFont(JPlagCreator.BIG_FONT);
		}
	}

	private void setError() {
		JLabel[] labels = { packing, sending, waiting, parsing, comparing,
	            loading };
        
        int label = active;
        if(label>5) label = 5;

		labels[label].setIcon(new ImageIcon(getClass().getResource(
				"/atujplag/data/bad.gif"))); //$NON-NLS-1$

		labels[label].setFont(JPlagCreator.BIG_FONT);
		labels[label].setForeground(Color.RED);
	}

    public void setState(int state, boolean noError) {
		if(state < 0) return;
		setActive(state);
		if(!noError) setError();
	}
    
} //  @jve:decl-index=0:visual-constraint="52,10"
