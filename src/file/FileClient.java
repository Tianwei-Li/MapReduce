package file;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import message.FileRequestMessage;
import message.Message;

public class FileClient {
	public static void main(String[] args) throws IOException {
		Iterable<String> input = getInputIterator("127.0.0.1",15217, "test.txt", 9 , 18);
		for (String line : input) {
			System.out.println(line);
		}
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
