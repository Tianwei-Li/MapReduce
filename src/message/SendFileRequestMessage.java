package message;
import java.io.Serializable;



public class SendFileRequestMessage extends Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4151219710911616153L;
	final String jobId;
	final String taskId;
	final int len;

	public SendFileRequestMessage(String jobId, String taskId, int len) {
		super(MessageType.SENDFILEREQUEST_MSG);
		this.jobId = jobId;
		this.taskId = taskId;
		this.len = len;
	}



	public int getLen() {
		return len;
	}


	public String getJobId() {
		return jobId;
	}


	public String getTaskId() {
		return taskId;
	}

}