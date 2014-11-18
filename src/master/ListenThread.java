package master;
import java.net.*;
import java.io.*;
/**
 * 
 * @author tianweil
 * The listening thread for ProcessManager, listen at configured port, 
 * create socket when getting a communication establish request.
 */
public class ListenThread extends Thread {
	
	public ListenThread() {
		super();
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		TestMaster inst = TestMaster.getInstance();
		try {
			while (thisThread.isInterrupted() == false) {
				Socket skt = inst.listenSock.accept();
				(new RecvThread(skt)).start();
			}
		} catch (IOException e) {
			//System.out.println("listening thread terminated successfully.");
			//e.printStackTrace();
		}
	}
	
	public void cancel() { 
		interrupt(); 
	}

}
