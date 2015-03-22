package atujplag.client;

import java.util.Iterator;

import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.Status;
import jplagWsClient.jplagClient.Submission;
import jplagWsClient.util.JPlagClientAccessHandler;
import atujplag.ATUJPLAG;

public class SubmittedClient extends Client {
	private Submission submission = null;

	public SubmittedClient(ATUJPLAG atujplag, Submission submission) {
		super(submission.getTitle(), atujplag);

		this.status = new Status(submission.getLastState(), 0, "");
		this.submission = submission;

		this.submissionID = submission.getSubmissionID();
		this.setClientName(submission.getTitle());
		this.stub = generateStub();
	}

	/**
	 * For a SubmittedClient this operation has already been performed
	 */
	public boolean compareSource() {
		return false;
	}

	private JPlagTyp_Stub generateStub() {
		if (this.stub == null) {
			stub = (JPlagTyp_Stub) (new JPlagService_Impl()
					.getJPlagServicePort());

			HandlerChain handlerchain = stub._getHandlerChain();
			@SuppressWarnings("unchecked")
			Iterator<Handler> handlers = handlerchain.iterator();
			while (handlers.hasNext()) {
				Handler handler = handlers.next();
				if (handler instanceof JPlagClientAccessHandler) {
					this.accessHandler = ((JPlagClientAccessHandler) handler);
					break;
				}
			}
		}
		if (accessHandler != null) {
			accessHandler.setUserPassObjects(atujplag.getUsername(),
					atujplag.getPassword());
		}
		return this.stub;
	}

	public String getClientName() {
		return super.getClientName();
	}

	protected JPlagTyp_Stub getJPlagStub() {
		return this.generateStub();
	}

	public boolean getResult() {
		this.stub = generateStub();
		return execGetResult();
	}

	public Submission getSubmission() {
		return this.submission;
	}
}
