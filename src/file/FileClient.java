package file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import message.FileRequestMessage;
import message.Message;
import message.SendFileRequestMessage;

public class FileClient {
	public static void main(String[] args) throws IOException, InterruptedException {
		sendFileTest();
	}

	public static void reqeustFileTest() throws IOException {
		Iterable<String> input = getInputIterable("127.0.0.1",15217, "test.txt", 9 , 18);
		for (String line : input) {
			System.out.println(line);
		}
	}
	
	public static void sendFileTest() throws IOException, InterruptedException {
		sendFile("127.0.0.1", 15217, "compcomp.jar");
	}
	
	
	public static Socket send(Message message, String ip, int port)
			throws IOException {
		// setup connection if there is not already existed
		Socket sendSock;
		try {
			sendSock = new Socket(ip, port);
		} catch (UnknownHostException e) {
			System.out.println("Server is offline!");
			sendSock = null;

		} catch (IOException e) {
			System.out.println("Server is offline!");
			sendSock = null;
		}

		if (sendSock == null) {
			// createSendSock failed
			return null;
		}
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
			} catch (IOException e) {
				e.printStackTrace();
				System.out
						.println("Error: failed to send the message! Client Stub aborted!");
				sendSock.close();
				return null;
			}
		} else {
			System.out
					.println("Error: failed to send the message! Client Stub aborted!");
			sendSock.close();
			return null;
		}
		return sendSock;
	}
	
	public static String requestFileFromServer(String ip, int port, String filePath, int index, int len) throws IOException {
		Message message = new FileRequestMessage(filePath, index, len);
		final Socket socket = send(message, ip, port);
		byte[] mybytearray = new byte[len];
		InputStream is = socket.getInputStream();
		int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		int current = bytesRead;

		do {
			bytesRead = is.read(mybytearray, current,
					(mybytearray.length - current));
			if (bytesRead > 0)
				current += bytesRead;
		} while (bytesRead > 0);

		return new String(mybytearray);
	}
	
	public static Iterable<String> getInputIterable(String ip, int port, String filePath, int index, int len) throws IOException {
		String input = requestFileFromServer(ip, port, filePath, index, len);
		String[] lines = input.split("[\r\n]+");
		return Arrays.asList(lines);
	}
	
	public static void sendFile(String ip, int port, String filePath) throws IOException, InterruptedException {
		int len = (int) new File(filePath).length();
		Message message = new SendFileRequestMessage(filePath, len);
		Thread.sleep(100);
		Socket socket = send(message, ip, port);
		FileServer.sendFile(socket, filePath, 0, len);
		
	}
	
	public static void sendFile(String ip, int port, String filePath, byte[] bytes) throws IOException, InterruptedException {
		Message message = new SendFileRequestMessage(filePath, bytes.length);
		Socket socket = send(message, ip, port);
		Thread.sleep(100);
		FileServer.sendFile(socket, filePath, bytes);
		socket.close();
	}
}
