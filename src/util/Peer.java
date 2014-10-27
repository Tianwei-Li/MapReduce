package util;

import java.net.*;
import java.io.*;

/**
 * 
 * @author tianweil
 * This is equivalent to a Node in the distributed system.
 * Each Peer object has a name, ip address and port number.
 *
 */
public class Peer {
	private String name = "";
	private String ip = "";
	private int port = 0;
	private long timeStamp = 0;
	
	private Socket sendSock = null;
	private ObjectOutputStream outStream = null;
	
	public Peer(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
	
	public Socket createSendSock() {
        try {
            sendSock = new Socket(ip, port);
        } catch (UnknownHostException e) {
            System.out.println(name + " is offline!");
            sendSock = null;
             
        } catch (IOException e) {
            System.out.println(name + " is offline!");
            sendSock = null;
        }
        return sendSock;
    }
     
    public ObjectOutputStream createOutStream() {
        if (sendSock == null) {
            createSendSock();
        }
         
        if (sendSock == null) {
            // createSendSock failed
            return null;
        }
        try {
            outStream = new ObjectOutputStream(sendSock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream;
    }
      
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
     
    public Socket getSendSock() {
        if (sendSock == null) {
            createSendSock();
        }
        return sendSock;
    }
 
    public void setSendSock(Socket sendSock) {
        this.sendSock = sendSock;
    }
 
    public ObjectOutputStream getOutStream() {
        if (outStream == null) {
            createOutStream();
        }
        return outStream;
    }
 
    public void setOutStream(ObjectOutputStream outStream) {
        this.outStream = outStream;
    }
    
    public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
    public String toString() {
        return "Peer [name=" + name + ", ip=" + ip + ", port=" + port
                + ", sendSock=" + sendSock + ", outStream=" + outStream + "]";
    }
     
    public void closeSock() {
        if (sendSock != null) {
            try {
                sendSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
}
