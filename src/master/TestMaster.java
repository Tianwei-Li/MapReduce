package master;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ServerSocket;

import org.yaml.snakeyaml.Yaml;

import util.Peer;


public class TestMaster {
	public ServerSocket listenSock;
	public ListenThread listenThread;
	private static TestMaster inst = new TestMaster();

	public ConcurrentHashMap<String, Peer> slaveMap;
	private Peer me;

	private TestMaster() {}

	public static TestMaster getInstance() {
		return inst;
	}

	public void init(String configFileName, String localName) {

		// parse the configuration file
		if (parseConfig(configFileName, localName) == false) {
			return;
		}

		// start listening thread
		try {
			listenSock = new ServerSocket(me.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		listenThread = new ListenThread();
		listenThread.start();
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

		if (me == null || slaveMap == null) {
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

		slaveMap = new ConcurrentHashMap<String, Peer>();
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




	public static void main(String[] args) throws IOException {
		inst = new TestMaster();
		inst.init(args[0], args[1]);
	}


}
