package master;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.ServerSocket;

import org.yaml.snakeyaml.Yaml;

import util.Peer;


public class TestMaster {
	public ServerSocket listenSock;
	public ListenThread listenThread;
	private static TestMaster inst = new TestMaster();

	public ConcurrentHashMap<String, Peer> slaveMap;
	
	// record the free mapper and reducer slots in each slave
	// the first integer is mapper slot
	// the second integer is reducer slot
	public ConcurrentHashMap<String, ArrayList<Integer>> slaveCapacity;
	public ConcurrentHashMap<String, Thread> recvThreadMap;
	public BlockingQueue<Job> jobList = new LinkedBlockingQueue<Job>();
	
	public int maxMapperCnt = 0;
	public int maxReducerCnt = 0;
	
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
		
		recvThreadMap = new ConcurrentHashMap<>();
		slaveMap = new ConcurrentHashMap<>(); 
		slaveCapacity = new ConcurrentHashMap<String, ArrayList<Integer>>();

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
		Map<String, Object> data = (Map<String, Object>) yaml.load(input);

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("configuration") == true) {
				if (parsePeers((List<Map<String, Object>>) entry.getValue(), localName) == false) {
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
	
	
	public void start() {
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Master node > ");
			String line = in.nextLine();
			
			String[] args = line.split("\\s+");
			if (args[0].equalsIgnoreCase("help")) {
				printHelp();
			} else if (args[0].equalsIgnoreCase("submit")) {
				submitJob(args);
			} else if (args[0].equalsIgnoreCase("monitor")) {
				//createProcess(args);
			} else if (args[0].equalsIgnoreCase("terminate")) {
				terminateJob(args);
			}else if (args[0].equalsIgnoreCase("exit")) {
				//shutdown();
				break;
			} else {
				System.out.println(line + " is not a valid command!");
				System.out.println("input help for more information.");
			}
			
		}
		in.close();
	}
	
	private void printHelp() {
		System.out.println("submit - submit a job.");
		System.out.println("monitor - check the progress of jobs.");
		System.out.println("terminate - terminate a job.");
		System.out.println("exit    - exit the application.");
	}
	
	private void submitJob(String[] args) {
		String jarName = args[1];
		String inputFolder = args[2];
		String outputFolder = args[3];
		Job job = new Job(jarName, inputFolder, outputFolder);
		jobList.add(job);
	}
	
	private void terminateJob(String[] args) {
		int jobId = Integer.parseInt(args[1]);
		for (Job job : jobList) {
			if (jobId == job.getJobId()) {
				// TODO: terminate all tasks in the job
				
				jobList.remove(job);
			}
		}
	}
	
	




	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("wrong number of arguments input!");
			return;
		}
		inst = new TestMaster();
		inst.init(args[0], args[1]);
		
		inst.start();
	}


}
