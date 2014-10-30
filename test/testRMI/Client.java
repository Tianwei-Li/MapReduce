package testRMI;

import java.rmi.Naming;


public class Client {

	public Client() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	   * Client program for the "Hello, world!" example.
	   * @param argv The command line arguments which are ignored.
	   */
	  public static void main (String[] argv) {
	    try {
	      LaunchTaskInterface hello = 
	        (LaunchTaskInterface) Naming.lookup ("rmi://127.0.0.1:1099/TaskTracker_Slave1");
	      
	      System.out.println(hello.say());
	      
	      
	      TaskTrackerInterface tracker = hello.createTaskTracker();
	      tracker.createTask();
	      
	      Thread.sleep(20000);
	      
	      TaskTrackerInterface tracker2 = (TaskTrackerInterface) hello.createTaskTracker();
	      tracker2.createTask();
	      while (true) {
	    	  
	    	  System.out.println("task1 progress " + tracker.checkProgress());
	    	  System.out.println("task2 progress " + tracker2.checkProgress());
	    	  
	    	  Thread.sleep(10000);
	    	  
	      }
	      
	    } catch (Exception e) {
	      System.out.println ("Client exception: " + e);
	    }
	  }

}