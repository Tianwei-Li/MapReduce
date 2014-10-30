package file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import message.FileRequestMessage;
import message.Message;

public class FileClient {
	public static void main(String[] args) throws IOException {
		int len = 18;
		Message message = new FileRequestMessage("test.txt", 9, len);
		final Socket socket = send(message, "127.0.0.1", 15217);
		byte[] mybytearray = new byte[len];
		InputStream is = socket.getInputStream();
		FileOutputStream fos = new FileOutputStream("received.txt");
		int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		int current = bytesRead;

		do {
			bytesRead = is.read(mybytearray, current,
					(mybytearray.length - current));
			if (bytesRead > 0)
				current += bytesRead;
		} while (bytesRead > 0);

		String input = new String(mybytearray);
		System.out.println(input);
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
}
