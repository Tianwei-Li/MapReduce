package slave;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import master.Task;
import master.TaskTrackerInterface;



public class TaskTracker extends UnicastRemoteObject implements TaskTrackerInterface {
	TestSlave slave = null;
	protected TaskTracker(TestSlave slave) throws RemoteException {
		this.slave = slave;
	}

	private TaskThread taskThread = null;
	
	public void createTask(Task task) {
		taskThread = new TaskThread(slave, task);
		taskThread.start();		
	}
	
	public boolean checkProgress() {
		return taskThread.checkProgress();
	}

}
