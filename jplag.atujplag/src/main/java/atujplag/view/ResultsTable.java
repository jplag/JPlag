package atujplag.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ResultsTable extends JTable {
	private static final long serialVersionUID = 6522287295639118881L;
	private DateRenderer dateRenderer = new DateRenderer();
	private EditableCell editor = new EditableCell();
	private UneditableRenderer uneditableRenderer = new UneditableRenderer();

	private View view;

	public ResultsTable(View view, TableSorter tableSorter) {
		super(tableSorter);
		this.view = view;
	}

	public void selectSubmission(String title) {
		TableSorter tableSorter = (TableSorter) getModel();
		int row = ((ResultsTableModel) tableSorter.getTableModel()).getSubmissionRow(title);
		if (row < 0)
			return;
		row = tableSorter.viewIndex(row);
		setRowSelectionInterval(row, row);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		switch (convertColumnIndexToModel(column)) {
		case 0:
			return super.getCellRenderer(row, column);
		case 1:
			return dateRenderer;
		default:
			return uneditableRenderer;
		}
	}

	public TableCellEditor getCellEditor(int row, int column) {
		if (convertColumnIndexToModel(column) == 0)
			return editor;
		return super.getCellEditor(row, column);
	}

	/*
	 * The following code starts the editing 300ms after the editTimer has been
	 * started to allow the user to place a second click resulting in a double
	 * click
	 */

	int lasti, lastj;
	EventObject lastobject;

	@SuppressWarnings("serial")
	javax.swing.Action editAction = new javax.swing.AbstractAction() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			ResultsTable.super.editCellAt(lasti, lastj, lastobject);
			((JTextField) editor.getComponent()).requestFocus();
			editTimer.stop(); // setRepeats(false) doesn't work...
		}
	};
	private Timer editTimer = new Timer(300, editAction);

	/**
	 * If the user placed a doubleclick on the given cell, the result files are
	 * opened in a browser. Else if the cell was not selected, nothing happens.
	 * Else editing the cell is started after a delay of 300 ms to allow double
	 * clicks
	 */
	public boolean editCellAt(int i, int j, EventObject object) {
		if (object instanceof MouseEvent && ((MouseEvent) object).getClickCount() == 2) {
			editTimer.stop();
			if (cellEditor != null)
				cellEditor.stopCellEditing();
			view.openItem(); // open result files in browser
			return false;
		}
		if (!isCellSelected(i, j)) {
			editTimer.stop();
			if (cellEditor != null)
				cellEditor.stopCellEditing();
			return false;
		}
		lasti = i; // save parameters
		lastj = j;
		lastobject = object;
		editTimer.start(); // delay the start of editing
		return false;
	}

	/**
	 * Starts editing a cell without a delay
	 */
	public boolean directEditCellAt(int i, int j) {
		return super.editCellAt(i, j, null);
	}

	public JTextField getEditorTextField() {
		return (JTextField) editor.getComponent();
	}

	@SuppressWarnings("serial")
	private class DateRenderer extends DefaultTableCellRenderer {
		private final Color LIGHTGRAY = new Color(204, 204, 204);
		private final Color VERYLIGHTGRAY = new Color(244, 244, 244);
		private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$

		public void setValue(Object value) {
			setText((value == null) ? "" : dateFormatter.format(value)); //$NON-NLS-1$
		}

		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, obj, isSelected, /* hasFocus */false, row, column);
			comp.setBackground(isSelected ? LIGHTGRAY : VERYLIGHTGRAY);
			return comp;
		}
	}

	@SuppressWarnings("serial")
	private class EditableCell extends DefaultCellEditor {
		public EditableCell() {
			super(new FocussedTextField());
			setClickCountToStart(1);
		}
	}

	@SuppressWarnings("serial")
	private class FocussedTextField extends JTextField {
		FocussedTextField() {
			addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					selectAll();
				}

				public void focusLost(FocusEvent arg0) {
				}
			});
		}
	}

	@SuppressWarnings("serial")
	private class UneditableRenderer extends JLabel implements TableCellRenderer {
		private final Color LIGHTGRAY = new Color(204, 204, 204);
		private final Color VERYLIGHTGRAY = new Color(244, 244, 244);

		public UneditableRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			setBackground(isSelected ? LIGHTGRAY : VERYLIGHTGRAY);
			setText(obj == null ? "" : obj.toString()); //$NON-NLS-1$
			return this;
		}
	}
}
