package file;
import java.net.*;
import java.io.*;
/**
 * 
 * @author tianweil
 * The listening thread for ProcessManager, listen at configured port, 
 * create socket when getting a communication establish request.
 */
public class FileListenThread extends Thread {
	
	public FileListenThread() {
		super();
	}
	
	@Override
	public void run() {
		FileServer inst = FileServer.getInstance();
		try {
			while (true) {
				Socket skt = inst.listenSocket.accept();
				(new RecvThread(skt)).start();
			}
		} catch (IOException e) {
			System.out.println("listening thread terminated successfully.");
			//e.printStackTrace();
		}
	}

}
