package message;

import java.io.Serializable;

public class HeartBeatMessage extends Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8392248833826305927L;
	final int mapperSlotCnt;
	final int reducerSlotCnt;
	
	public HeartBeatMessage(int mSlotCnt, int rSlotCnt) {
		super(MessageType.HEARTBEAT_MSG);
		this.mapperSlotCnt = mSlotCnt;
		this.reducerSlotCnt = rSlotCnt;
	}

	@Override
	public String toString() {
		return "HeartBeatMessage [mapperSlotCnt=" + mapperSlotCnt
				+ ", reducerSlotCnt=" + reducerSlotCnt + "]";
	}

}
