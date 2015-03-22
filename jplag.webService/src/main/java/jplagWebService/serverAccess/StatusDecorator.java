/*
 * Created on 06.03.2005
 */
package jplagWebService.serverAccess;

import jplagWebService.server.Status;

/**
 * Redirects status changes to AccessStructure.status object
 * 
 * @author Emeric Kwemou
 */

public class StatusDecorator {
    public static final int UPLOADING = 0;
	public static final int WAITING_IN_QUEUE = 50;
	public static final int COMPARE_SOURCE_DONE = 300;

	private Status status;
	
	public StatusDecorator(Status status) {
		this.status = status;
		ensureSerializable();
	}

	public Status getStatus() {
		return status;
	}
	
	public int getState() {
		return status.getState();
	}

	public void setState(int state) {
		status.setState(state);
	}

	public int getProgress() {
		return status.getProgress();
	}

	public void setProgress(int progress) {
		status.setProgress(progress);
	}

	public java.lang.String getReport() {
		return status.getReport();
	}

	public void setReport(java.lang.String report) {
		this.status.setReport(report);
	}
	
	public void addReport(String report) {
		if(status.getReport().length()==0) status.setReport(report);
		else status.setReport(status.getReport() + "\n" + report); 
	}

	/**
	 * @param state
	 * 		New state to be added, only added if there wasn't a previous error
	 * @param report
	 *      New message to be added to the report
	 */
	public void add(int state, String report) {
		if (getState() < 400) setState(state);
		addReport(report);
	}

	public void add(int state, int progress, String report) {
		if(getState() < 400) setState(state);
		setProgress(progress);
		addReport(report);
	}

	public void add(jplag.ExitException ex) {
		add(ex.getState(), ex.getReport());
	}

	/**
	 * Ensure that nothing is null
	 */
	private void ensureSerializable() {
		if (status == null) {
			status = new Status(0, 0, "");
			return;
		}
		if (status.getReport() == null)
			status.setReport("");
	}
}
