import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;



public class TaskTracker extends UnicastRemoteObject implements TaskTrackerInterface {
	protected TaskTracker() throws RemoteException {
		
	}

	private Task task = null;
	
	public void createTask(jobId, taskId, inputpath, offset, length, true) {
		task = new Task();
		task.start();
	}
	
	public String checkProgress() {
		return task.checkProgress();
	}

}
