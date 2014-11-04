import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import slave.LaunchTask;



public class Server {

	public Server() {
		// TODO Auto-generated constructor stub
	}


	private void initialize()
	{
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
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

	public void start()
	{
		try {
			Naming.rebind ("TaskTracker_Slave1", new LaunchTask ());
			System.out.println ("Server is ready.");
		} catch (Exception e) {
			System.out.println ("Server failed: " + e);
		}
	}

	/**
	 * Server program for the "Hello, world!" example.
	 * @param argv The command line arguments which are ignored.
	 */
	public static void main (String[] argv) {
		Server testServer = new Server();
		testServer.initialize();
		testServer.start();
	}

}
