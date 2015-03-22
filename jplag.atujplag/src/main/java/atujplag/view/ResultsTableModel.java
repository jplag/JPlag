/*
 * Created on 20.05.2005
 */
package atujplag.view;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import atujplag.ATUJPLAG;
import atujplag.util.Messages;
import atujplag.util.TagParser;

public class ResultsTableModel extends AbstractTableModel {
	private static final int TITLE = 0;
	private static final int DATE = 1;
	private static final int LANGUAGE = 2;
	private static final int N_OF_PROGRAMS = 3;
	private static final int PARSER_ERRORS = 4;

	private static final long serialVersionUID = 1L;

	private ATUJPLAG atujplag;
	private View view;
	private Vector<Document> results = null;

	private String[] columnNames = {
			Messages.getString("ResultsTableModel.Title"), //$NON-NLS-1$
			Messages.getString("ResultsTableModel.Submitted"), //$NON-NLS-1$
			Messages.getString("ResultsTableModel.Language"), //$NON-NLS-1$
			Messages.getString("ResultsTableModel.Number_of_programs"), //$NON-NLS-1$
			Messages.getString("ResultsTableModel.Parser_errors") }; //$NON-NLS-1$

	public ResultsTableModel(ATUJPLAG atujplag, View view) {
		this.atujplag = atujplag;
		this.view = view;
		init();
	}

	public void delete(int row) {
		if (atujplag.manageResults(ATUJPLAG.DELETE, null, row, view)) {
			results = atujplag.getSubmissions();
			fireTableRowsDeleted(row, row);
		}
	}
	
	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell.
	 */
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	private Element getRowInfoElement(int row) {
		return (Element) results.get(row).getElementsByTagName("infos").item(0); //$NON-NLS-1$
	}

	/**
	 * @return A string containing the whole content of the parser-log.txt
	 * 		   belonging to the given row or null if an error occurred
	 * 		   (which is shown in a message box)
	 */
	public String getLogString(int row) {
		Element elem = getRowInfoElement(row);

		File loc = new File(atujplag.getResultLocation() + File.separator
				+ elem.getAttribute("title") + File.separator + "parser-log.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!loc.exists()) {
			JPlagCreator.showError(view,
				Messages.getString("ResultsTableModel.Parser_log_not_available"), //$NON-NLS-1$
				TagParser.parse( 
					Messages.getString("ResultsTableModel.Parser_log_not_available_DESC_{1_PATH}"), //$NON-NLS-1$
					new String []{loc.getPath()}));
			return null;
		}
		return ATUJPLAG.generateParserLogString(loc, view);
	}

	public int getRowCount() {
		return results.size();
	}

	public Object getValueAt(int row, int col) {
		Element elem = getRowInfoElement(row);
		switch (col) {
			case TITLE:
				return elem.getAttribute("title"); //$NON-NLS-1$
			case DATE: {
				String str = elem.getAttribute("date"); //$NON-NLS-1$
				return new Date(Long.valueOf(str).longValue());
			}
			case N_OF_PROGRAMS:
				return elem.getAttribute("n_of_programs"); //$NON-NLS-1$
			case LANGUAGE:
				return elem.getAttribute("language_name"); //$NON-NLS-1$
			case PARSER_ERRORS:
				return elem.getAttribute("errors"); //$NON-NLS-1$
			default:
				return "Illegal column"; //$NON-NLS-1$
		}
	}

	public void init() {
		this.results = atujplag.getSubmissions();
	}

	public boolean isCellEditable(int row, int col) {
		return col==0;
	}

	public void setValueAt(Object value, int row, int col) {
		if (col != 0) return;
		
		Element elem = getRowInfoElement(row);
		if(!elem.getAttribute("title").equals(value)) { //$NON-NLS-1$
			if(atujplag.manageResults(ATUJPLAG.RENAME, (String) value,
                    row, view)) {
//				this.results = atujplag.getSubmissions();
//				fireTableCellUpdated(row, col);
				view.updateTable((String) value);
			}
		}
	}
    
	public void showResult(int row) {
		// Search result file
		Element elem = getRowInfoElement(row);

		String str = elem.getAttribute("title"); //$NON-NLS-1$
		if (str == null) {
			// File not available
			JPlagCreator.showError(view,
				Messages.getString("ResultsTableModel.File_not_available"), //$NON-NLS-1$
				Messages.getString("ResultsTableModel.File_not_available_DESC")); //$NON-NLS-1$
			return;
		}
		File loc = new File(ATUJPLAG.encodePathForURL(atujplag.getResultLocation()) + File.separator + ATUJPLAG.encodeForURL(str)
				+ File.separator + "index.html"); //$NON-NLS-1$
		ATUJPLAG.show(loc);
	}

	public Document getDocument(int row) {
		return results.get(row);
	}

	public String[] getColumnNames() {
		return columnNames;
	}
	
	/**
	 * @return The row for a given submission title
	 */
	public int getSubmissionRow(String title) {
		for(int i=0;i<results.size();i++) {
			Element elem = getRowInfoElement(i);
			if(elem.getAttribute("title").equals(title))
				return i;
		}
		return -1;
	}
}