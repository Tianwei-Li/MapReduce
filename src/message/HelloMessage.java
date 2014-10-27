package message;

import java.io.Serializable;

/**
 * A basic String message
 * @author tianweil
 *
 */
public class HelloMessage extends Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1360329399138105515L;
	final String name;
	final String ip;
	final int port;
	public HelloMessage(String ip, int port, String name) {
		super(MessageType.HELLO_MSG);
		this.ip = ip;
		this.port = port;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "HelloMessage [name=" + name + ", ip=" + ip + ", port=" + port
				+ "]";
	}
}
