package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import util.Peer;

public class FileServer {
	ServerSocket listenSocket;
	static Peer me;
	static FileServer inst = new FileServer();
	static final String localName = "FileServer";
	public FileServer() {
	}
	
	public void init(String configFileName) throws IOException {
		if (parseConfig(configFileName, localName) == false) {
			return;
		}
		listenSocket = new ServerSocket(me.getPort());
		Thread fileListenThread = new FileListenThread();
		fileListenThread.start();
	}
	
	public static void main(String[] args) throws IOException {
		inst = new FileServer();
		inst.init(args[0]);
		
		RandomAccessFile file = new RandomAccessFile("test.txt", "r");
//		System.out.println(file.readLine());
//		System.out.println(file.readLine());
//		System.out.println(file.readLine());
		long fp = file.getFilePointer();
		file = new RandomAccessFile("test.txt", "r");
		file.seek(9);
		//System.out.println(file.readLine());
		byte[] bytes = new byte[18];
		file.read(bytes, 0, 26 - 9 + 1);
		System.out.println(new String(bytes));
	}
	
	@SuppressWarnings("unchecked")
	private boolean parseConfig(String configFileName, String localName) {
		InputStream input = null;
		try {
			input = new FileInputStream(new File(configFileName));
		} catch (FileNotFoundException e) {
			System.out.println("Can not find configuration file!");
			return false;
		}
		Yaml yaml = new Yaml();
		Map<String, List<Map<String, Object>>> data = (Map<String, List<Map<String, Object>>>) yaml.load(input);

		for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("configuration") == true) {
				if (parsePeers(entry.getValue(), localName) == false) {
					return false;
				}
			}
		}

		if (me == null) {
			return false;
		}

		return true;
	}

	// parse the peer list
	public boolean parsePeers(List<Map<String, Object>> peers, String localName) {
		if (peers == null || peers.size() < 1) {
			System.out.println("Error: there is no peer infomation in the configuration file!");
			return false;
		}

		for (int i = 0; i < peers.size(); ++i) {
			Map<String, Object> peerInfo = peers.get(i);
			String name = null;
			String ip = null;
			int port = -1;
			for (Map.Entry<String, Object> entry : peerInfo.entrySet()) {
				if (entry.getKey().equalsIgnoreCase("name") == true) {
					name = (String) entry.getValue();
				}
				if (entry.getKey().equalsIgnoreCase("ip") == true) {
					ip = (String) entry.getValue();
				}
				if (entry.getKey().equalsIgnoreCase("port") == true) {
					port = (Integer) entry.getValue();
				}
			}
			if (name == null || ip == null || port == -1) {
				System.out.println("Error: there is something wrong in the configuration file!");
				return false;
			}
			// the peer that match the local name is the local peer
			if (name.equalsIgnoreCase(localName)) {
				me = new Peer(name, ip, port);
			} else {
				//Peer onePeer = new Peer(name, ip, port);
				//slaveMap.put(name, onePeer);
			}
		}
		return true;
	}

	public static FileServer getInstance() {
		return inst;
	}
	
	public static void sendFile(Socket socket, String filePath, int index, int len) throws IOException {
		byte[] bytesToSend = new byte[len];
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(index);
		file.read(bytesToSend, 0, len);
		
		OutputStream os = socket.getOutputStream();
		System.out.println("sending file: " + filePath + " from "+ index);
		os.write(bytesToSend);
		os.close();
		file.close();
	}
}
