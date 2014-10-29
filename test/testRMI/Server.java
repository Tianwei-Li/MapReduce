package testRMI;
import java.rmi.Naming;


public class Server {

	public Server() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	   * Server program for the "Hello, world!" example.
	   * @param argv The command line arguments which are ignored.
	   */
	  public static void main (String[] argv) {
	    try {
	      Naming.rebind ("TaskTracker_Slave1", new LaunchTask ());
	      System.out.println ("Server is ready.");
	    } catch (Exception e) {
	      System.out.println ("Server failed: " + e);
	    }
	  }

}
