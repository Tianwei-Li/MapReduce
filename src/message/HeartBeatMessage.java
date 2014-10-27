package message;

import java.io.Serializable;

public class HeartBeatMessage extends Message implements Serializable{
	final int utilization;
	
	public HeartBeatMessage(int util) {
		super(MessageType.HEARTBEAT_MSG);
		this.utilization = util;
	}

	public int getUtilization() {
		return utilization;
	}

	@Override
	public String toString() {
		return "HeartBeatMessage [utilization=" + utilization + "]";
	}
	
	

}
