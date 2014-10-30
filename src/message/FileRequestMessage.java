package message;

import java.io.Serializable;

public class FileRequestMessage extends Message implements Serializable{

	String filePath;
	int index;
	int len;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4622769982547019891L;

	public FileRequestMessage(String filePath, int index, int len) {
		super(MessageType.FILEREQUEST_MSG);
		this.filePath = filePath;
		this.index = index;
		this.len = len;
	}

	public String getFilePath() {
		return filePath;
	}

	public int getIndex() {
		return index;
	}

	public int getLen() {
		return len;
	}

}
