package message;

import java.io.Serializable;

public class SubmitJobMessage extends Message implements Serializable {
	
	private static final long serialVersionUID = 6376531002386982041L;
	final String msg;
	
	public SubmitJobMessage(String msg) {
		super(MessageType.SUBMITJOB_MSG);
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "SubmitJobMessage [msg=" + msg + "]";
	}

	

}
