package slave;
import java.rmi.*;
import java.rmi.server.*;

import master.TaskTrackerInterface;

import testRMI.LaunchTaskInterface;



/**
 * Remote Class for the "Hello, world!" example.
 */
public class LaunchTask extends UnicastRemoteObject implements LaunchTaskInterface {
  /**
   * Construct a remote object
   * @param msg the message of the remote object, such as "Hello, world!".
   * @exception RemoteException if the object handle cannot be constructed.
   */
	TestSlave slave = null;
  public LaunchTask (TestSlave slave) throws RemoteException {
    this.slave = slave;
  }
  
 
  public TaskTrackerInterface createTaskTracker() throws RemoteException {
	 TaskTracker taskTracker = new TaskTracker(slave);
	 //taskTracker.createTask();
	 return taskTracker;
  }
  
  public String say() throws RemoteException {
	  return "Hello World!";
  }

}