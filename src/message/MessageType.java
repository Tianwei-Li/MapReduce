package message;

import java.io.Serializable;

public enum MessageType implements Serializable{
	HELLO_MSG,
	HEARTBEAT_MSG,
	FILEREQUEST_MSG,
	SUBMITJOB_MSG,
	SENDFILEREQUEST_MSG;
}
