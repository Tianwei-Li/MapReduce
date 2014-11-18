package slave;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import master.Job;
import message.HeartBeatMessage;
import message.HelloMessage;
import message.Message;
import message.SubmitJobMessage;

import org.yaml.snakeyaml.Yaml;

import configuration.JobConf;

import util.Peer;

public class TestSlave {
	Peer me = null;
	Peer master = null;
	Peer fileServer = null;
	String fileServerName = "FileServer";
	int heartBeatInterval = 5;   // in second
	SendHeartBeatThread heartBeatThread;

	TestSlave(String configFileName, String localName, String masterName) {
		// parse the configuration file
		if (parseConfig(configFileName, localName, masterName) == false) {
			return;
		}

		// initialize rmi registry server
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		// set ip address of rmi server
		System.setProperty("java.rmi.server.hostname", ip.getHostAddress());

		// try to register rmi server
		try
		{
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}
		catch (Exception e)
		{
			// ignore
		}

	}

	@SuppressWarnings("unchecked")
	private boolean parseConfig(String configFileName, String localName, String masterName) {
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
				if (parsePeers((List<Map<String, Object>>) entry.getValue(), localName, masterName) == false) {
					return false;
				}
			}
			
			
		}

		if (me == null || master == null) {
			return false;
		}

		return true;
	}

	// parse the peer list
	public boolean parsePeers(List<Map<String, Object>> peers, String localName, String masterName) {
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
			}

			if (name.equalsIgnoreCase(masterName)) {
				master = new Peer(name, ip, port);
			}
			
			if (name.equalsIgnoreCase(fileServerName)) {
				fileServer = new Peer(name, ip, port);
			}
		}
		return true;
	}

	
	private void start() {
		// registry rmi
		try {
			Naming.rebind (me.getName(), new LaunchTask (this));
			System.out.println ("Slave is ready.");
		} catch (Exception e) {
			System.out.println ("Slave failed: " + e);
		}

		// send hello message
		HelloMessage hMsg = new HelloMessage(me.getIp(), me.getPort(), me.getName());
		send(hMsg);
		
		// send the heat beat message
		heartBeatThread = new SendHeartBeatThread(this);
		heartBeatThread.start();
		
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Slave node > ");
			String line = in.nextLine();
			
			String[] args = line.split("\\s+");
			if (args[0].equalsIgnoreCase("help")) {
				printHelp();
			} else if (args[0].equalsIgnoreCase("submit")) {
				submitJob(args);
			} else if (args[0].equalsIgnoreCase("exit")) {
				shutdown();
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
		System.out.println("exit    - exit the application.");
	}
	
	private void shutdown() {
		heartBeatThread.cancel();
	}
	
	public void submitJob(String[] args) {
		StringBuilder strBld = new StringBuilder();
		for (String str : args) {
			strBld.append(str);
			strBld.append(" ");
		}
		SubmitJobMessage msg = new SubmitJobMessage(strBld.toString());
		send(msg);
	}


	public boolean send(Message msg) {

		// setup connection if there is not already existed
		ObjectOutputStream output = master.getOutStream();
		if (output != null) {
			try {
				output.writeObject(msg);
				output.flush();
				output.reset();
			}
			catch (IOException e) {
				e.printStackTrace();
				master.setSendSock(null);
				master.setOutStream(null);
				System.out.println("Error: failed to send hello message to master!");
				return false;
			}
		} else {
			System.out.println("Error: failed to send hello message to master!");
			return false;
		}

		return true;
	}


	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("wrong number of arguments input!");
			return;
		}
		
		 
        
		TestSlave slave = new TestSlave(args[0], args[1], args[2]);
		slave.start();
	}
}
