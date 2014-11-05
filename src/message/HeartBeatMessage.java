package message;

import java.io.Serializable;

public class HeartBeatMessage extends Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8392248833826305927L;
	
	public HeartBeatMessage() {
		super(MessageType.HEARTBEAT_MSG);
	}

	@Override
	public String toString() {
		return "HeartBeatMessage []";
	}

}
