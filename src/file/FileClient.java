package file;

import java.net.Socket;

public class FileClient {
	public static void main(String[] args) {
		Message message = new FileRequestMessage("test.txt", 9, 18);
		final Socket socket = send(message, ror.IP_adr, ror.Port);
	}
}
