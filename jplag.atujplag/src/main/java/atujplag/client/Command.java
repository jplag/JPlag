package atujplag.client;

import java.io.File;

import atujplag.ATUJPLAG;
import atujplag.view.InfoPanel;

public class Command {
	private static int TERMINATED = 300;

	/**
	 * 12 seconds
	 */
	private static final long STATUSWAITTIME = 12000;

	public static final int NO_ACTION = 0;
	public static final int COMPARE_SOURCE = 200;
	public static final int GET_STATUS = 300;
	public static final int GET_STATUS_AND_RESULT = 350;
	public static final int GET_RESULT = 400;
	public static final int DELETE_SUBMISSION = 700;
	public static final int UPDATE_USER_INFOS = 800;
	public static final int ALL_STEPS = 1000;

	private int action;

	private InfoPanel gui;
	private Client client;

	private boolean deleted = false;

	public Command(Client client, InfoPanel gui) {
        if(client instanceof SimpleClient)
            this.action = ALL_STEPS;
        else
            this.action = GET_STATUS_AND_RESULT;
		this.client = client;
        this.gui = gui;
	}

	private void compareSource() {
		if(!client.compareSource())
			client.forceStop();
	}

	private boolean getStatus() {
		boolean ok = true;
		while (true) {
			ok &= client.getStatus();
            if(client.isForceStop()) return false;
			if(!ok	|| client.get_getStatusResult().getState() > TERMINATED)
				return false;
			if(client.get_getStatusResult().getState() == TERMINATED) {
				return true;
			}
			try {
				Thread.sleep(STATUSWAITTIME);
			} catch (InterruptedException e) {}
			if(client.isForceStop()) return false;
		}
	}

	private void getResult() {
		if (client.getResult()) {
			File f = new File(client.getEncodedIndex_html());
			ATUJPLAG.show(f);
		}
	}

	private void getStatus_and_Result() {
		if (getStatus())
			getResult();
	}

	private void delete() {
		if(!deleted) {
			client.cancelSubmission(gui);
			deleted = true;
//			gui.run();
		}
	}

	/**
	 * Checks whether the processing should stop by deleting the submission
	 * or the "nextaction" should be executed
	 */
	private void setNextAction(int nextaction) {
		if(client.isForceStop())
			action = DELETE_SUBMISSION;
		else
			action = nextaction;
	}

	public void run() {
		client.setGui(gui);
		
		if (action == COMPARE_SOURCE || action == ALL_STEPS) {
			compareSource();
			setNextAction(GET_STATUS_AND_RESULT);
		}
		
		if (action == GET_STATUS_AND_RESULT) {
			getStatus_and_Result();
			action = DELETE_SUBMISSION;
		}
		
		if (action == DELETE_SUBMISSION) {
			delete();
		}
	}
}