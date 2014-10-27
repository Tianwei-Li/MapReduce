package master;
import java.net.*;
import java.io.*;
import util.Peer;
import message.*;


public class RecvThread extends Thread {
	String name = "";
	Socket recvSocket = null;   // save the socket and input stream object to re-use
	ObjectInputStream ois = null;
	
	public RecvThread(Socket recvSocket) {
		super();
		this.recvSocket = recvSocket;
		try {
			ois = new ObjectInputStream(recvSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			// the inst.isActive() is false when the node is shut down
			while (true) {
				Message message = (Message) ois.readObject();
				if (message != null) {
					MessageType msgType = message.getType();
					switch (msgType) {
					case HELLO_MSG:
						System.out.println("received " + message.toString());
						// add the slave into slave list
						HelloMessage hMsg = (HelloMessage)message;
						name = hMsg.getName();
						Peer onePeer = new Peer(name, hMsg.getIp(), hMsg.getPort());
						TestMaster.getInstance().slaveMap.put(name, onePeer);
						break;
					case HEARTBEAT_MSG:
						System.out.println("received " + message.toString());
						// update slave timestamp
						TestMaster inst = TestMaster.getInstance();
						Peer slave = inst.slaveMap.get(name);
						slave.setTimeStamp(System.currentTimeMillis());
						break;
					default:
						break;
					}
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			recvSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Socket send(Message message, Socket sendSock) throws IOException {
		// setup connection if there is not already existed
		
        ObjectOutputStream output = null;
        try {
        	output = new ObjectOutputStream(sendSock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (output != null) {
            try {
                output.writeObject(message);
                output.flush();
                output.reset();
            }
            catch (IOException e) {
            	e.printStackTrace();
                System.out.println("Error: failed to send the message! Client Stub aborted!");
                sendSock.close();
                return null;
            }
        } else {
            System.out.println("Error: failed to send the message! Client Stub aborted!");
            sendSock.close();
            return null;
        }
        return sendSock;
	}
	
}
