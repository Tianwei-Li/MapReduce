package message;
import java.io.Serializable;



public class SendFileRequestMessage extends Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4151219710911616153L;
	final String filePath;
	final int len;

	public SendFileRequestMessage(String filePath, int len) {
		super(MessageType.SENDFILEREQUEST_MSG);
		this.filePath = filePath;
		this.len = len;
	}



	public int getLen() {
		return len;
	}



	public String getFilePath() {
		return filePath;
	}


}