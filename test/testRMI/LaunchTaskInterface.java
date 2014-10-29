package testRMI;

import java.rmi.*;
/**
 * Remote Interface for the "Hello, world!" example.
 */
public interface LaunchTaskInterface extends Remote {
  /**
   * Remotely invocable method.
   * @return the message of the remote object, such as "Hello, world!".
   * @exception RemoteException if the remote invocation fails.
   */
  public TaskTrackerInterface createTaskTracker() throws RemoteException;
  
  public String say() throws RemoteException;
}