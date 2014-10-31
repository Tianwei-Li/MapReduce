package file;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import message.FileRequestMessage;
import message.Message;
import message.MessageType;
import message.SendFileRequestMessage;


public class RecvThread extends Thread {
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
			
			Message message = (Message) ois.readObject();
			if (message != null) {
				MessageType msgType = message.getType();
				switch (msgType) {
				case FILEREQUEST_MSG:
					FileRequestMessage fMsg = (FileRequestMessage) message;
					FileServer.sendFile(recvSocket, fMsg.getFilePath(), fMsg.getIndex(), fMsg.getLen());
					break;
				case SENDFILEREQUEST_MSG:
					SendFileRequestMessage sfMsg = (SendFileRequestMessage) message;
					//Can go wrong here. 
					recvSocket.getInputStream().read();
					FileServer.receiveFile(recvSocket, sfMsg.getJobId(), sfMsg.getTaskId(), sfMsg.getLen());
					break;
				default:
					break;
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	
	
	/*
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
	*/
	
}
