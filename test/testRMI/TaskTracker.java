package testRMI;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;



public class TaskTracker extends UnicastRemoteObject implements TaskTrackerInterface {
	protected TaskTracker() throws RemoteException {
		
	}

	private Task task = null;
	
	public void createTask() {
		task = new Task();
		task.start();
	}
	
	public int checkProgress() {
		return task.checkProgress();
	}

}
