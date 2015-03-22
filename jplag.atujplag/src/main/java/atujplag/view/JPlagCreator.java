/*
 * Created on Aug 4, 2005
 */
package atujplag.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * @author Emeric Kwemou
 */
public class JPlagCreator {
	public static java.awt.Font SYSTEM_FONT = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);

	public static java.awt.Font BIG_FONT = new java.awt.Font("Dialog", java.awt.Font.BOLD, 14);

	public static final Color BUTTON_BACKGROUND = new java.awt.Color(236, 233, 216);

	public static final Color BUTTON_FOREGROUND = Color.BLACK;

	public static final Color OPTION_PANEL_FOREGROUND = java.awt.Color.black;

	public static final Border LINE = javax.swing.BorderFactory.createLineBorder(java.awt.Color.black, 1);

	private static Color LINE_COLOR = Color.BLACK;

	public static Color SYSTEMCOLOR = new java.awt.Color(236, 233, 216);

	public static JButton createButton(String text, String toolTip, int width, int height) {
		JButton button = new JButton();
		button.setText(text);
		button.setPreferredSize(new java.awt.Dimension(width, height));
		button.setToolTipText(toolTip);
		button.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		return button;
	}

	public static JLabel createLabel(String text, int width, int height) {
		JLabel label = new JLabel();
		label.setText(text);
		label.setPreferredSize(new java.awt.Dimension(width, height));
		return label;
	}

	public static void showError(Component parent, String title, String message) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static int showConfirmDialog(String title, String message) {
		return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
	}

	public static final int WITH_LINEBORDER = 0;

	public static final int WITH_TITLEBORDER = 0;

	public static final Color BLINK_COLOR = Color.ORANGE;

	public static JPanel createPanel(String title, int width, int height, int vGap, int hGap, int alignment, int type) {
		FlowLayout flowLayout1 = new FlowLayout();
		JPanel controlPanel = new JPanel();

		controlPanel.setLayout(flowLayout1);
		flowLayout1.setAlignment(alignment);
		controlPanel.setPreferredSize(new java.awt.Dimension(width, height));

		controlPanel.setBackground(JPlagCreator.SYSTEMCOLOR);
		if (type == WITH_LINEBORDER)
			controlPanel.setBorder(JPlagCreator.LINE);
		if (type == WITH_TITLEBORDER)
			controlPanel.setBorder(JPlagCreator.titleBorder(title, Color.BLACK, Color.BLACK));
		flowLayout1.setVgap(vGap);
		flowLayout1.setHgap(hGap);
		return controlPanel;
	}

	public static JPanel createPanelWithoutBorder(int width, int height, int vGap, int hGap, int alignment) {
		FlowLayout flowLayout1 = new FlowLayout(alignment, hGap, vGap);
		JPanel controlPanel = new JPanel();

		controlPanel.setLayout(flowLayout1);
		controlPanel.setPreferredSize(new java.awt.Dimension(width, height));
		controlPanel.setBackground(JPlagCreator.SYSTEMCOLOR);

		return controlPanel;
	}

	/**
	 * If width or height is not greater than 0, preferred size will not be set
	 */
	public static JPanel createPanel(int width, int height, int vGap, int hGap, int alignment) {
		FlowLayout flowLayout1 = new FlowLayout();
		JPanel controlPanel = new JPanel();

		controlPanel.setLayout(flowLayout1);
		flowLayout1.setAlignment(alignment);
		if (width > 0 && height > 0)
			controlPanel.setPreferredSize(new java.awt.Dimension(width, height));

		controlPanel.setBackground(JPlagCreator.SYSTEMCOLOR);
		controlPanel.setBorder(JPlagCreator.LINE);
		flowLayout1.setVgap(vGap);
		flowLayout1.setHgap(hGap);
		return controlPanel;
	}

	public static JPanel createPanel(int width, int height, int vGap, int hGap) {
		return JPlagCreator.createPanel(width, height, vGap, hGap, FlowLayout.LEFT);
	}

	public static JSpinner createSpinner(int width, int height, String toolTip) {
		SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
		JSpinner spinner = new JSpinner(model);
		spinner.setFont(JPlagCreator.SYSTEM_FONT);
		spinner.setPreferredSize(new java.awt.Dimension(width, height));
		spinner.setEnabled(true);
		spinner.setBackground(java.awt.Color.WHITE);
		if (toolTip != null && !toolTip.equals(""))
			((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setToolTipText(toolTip);
		return spinner;
	}

	public static JComboBox<String> createJComboBox(String[] items, int width, int height, String toolTip) {

		JComboBox<String> comboBox = new JComboBox<String>(items);
		comboBox.setPreferredSize(new java.awt.Dimension(width, height));
		comboBox.setBackground(java.awt.Color.white);
		comboBox.setFont(JPlagCreator.SYSTEM_FONT);
		if (toolTip != null)
			comboBox.setToolTipText(toolTip);
		return comboBox;

	}

	public static JButton createOpenFileButton(String toolTip) {
		JButton button = new JButton();
		button.setText("");
		button.setToolTipText(toolTip);
		button.setIcon(new ImageIcon(JPlagCreator.class.getResource("/atujplag/data/open.gif")));
		button.setPreferredSize(new java.awt.Dimension(24, 24));
		button.setBackground(JPlagCreator.SYSTEMCOLOR);

		return button;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	public static JMenuItem createJMenuItem(String text) {
		JMenuItem jMenuItem = new JMenuItem();
		jMenuItem.setText(text);
		jMenuItem.setFont(JPlagCreator.SYSTEM_FONT);
		jMenuItem.setBackground(JPlagCreator.SYSTEMCOLOR);
		return jMenuItem;
	}

	public static JCheckBox createCheckBox(String toolTip) {
		JCheckBox box = new JCheckBox();

		box.setFont(JPlagCreator.SYSTEM_FONT);
		box.setPreferredSize(new java.awt.Dimension(20, 20));
		box.setForeground(JPlagCreator.OPTION_PANEL_FOREGROUND);
		box.setBackground(JPlagCreator.SYSTEMCOLOR);
		if (toolTip != null && !toolTip.equals(""))
			box.setToolTipText(toolTip);
		return box;
	}

	public static JCheckBox createCheckBox(String title, int width, String toolTip) {
		JCheckBox box = createCheckBox(toolTip);
		box.setText(title);
		box.setPreferredSize(new java.awt.Dimension(width, 20));
		return box;
	}

	public static JMenu createMenu(String text) {
		JMenu optionsMenu = new JMenu();
		optionsMenu.setText(text);
		optionsMenu.setBackground(JPlagCreator.SYSTEMCOLOR);
		optionsMenu.setFont(SYSTEM_FONT);
		return optionsMenu;
	}

	public static JTextField createTextField(int width, int height, String toolTip) {
		JTextField textField = new JTextField();

		textField.setPreferredSize(new java.awt.Dimension(width, height));
		textField.setFont(JPlagCreator.SYSTEM_FONT);
		if (toolTip != null && !toolTip.equals(""))
			textField.setToolTipText(toolTip);
		return textField;
	}

	public static Border titleBorder(String title) {
		return JPlagCreator.titleBorder(title, LINE_COLOR, LINE_COLOR);
	}

	public static Border titleBorder(String title, Color titleColor, Color lineColor) {
		return javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(lineColor, 1), title,
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(
						"Dialog", java.awt.Font.PLAIN, 12), titleColor);
	}

	public static void showMessageDialog(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	static {
		// first tell SkinLF which theme to use
		try {
			UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		UIManager.put("ToolTip.background", new ColorUIResource(Color.WHITE));
		UIManager.put("Button.background", SYSTEMCOLOR);
		UIManager.put("Button.font", SYSTEM_FONT);
		UIManager.put("Label.background", SYSTEMCOLOR);
		UIManager.put("Label.font", SYSTEM_FONT);
		UIManager.put("ComboBox.background", SYSTEMCOLOR);
		UIManager.put("Toolbar.background", SYSTEMCOLOR);
		UIManager.put("Panel.background", SYSTEMCOLOR);
		UIManager.put("OptionPane.background", SYSTEMCOLOR);
		UIManager.put("MenuItem.background", SYSTEMCOLOR);
		UIManager.put("TabbedPane.selected", SYSTEMCOLOR);
	}
}