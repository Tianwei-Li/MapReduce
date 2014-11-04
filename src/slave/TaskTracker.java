package slave;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import master.TaskTrackerInterface;



public class TaskTracker extends UnicastRemoteObject implements TaskTrackerInterface {
	TestSlave slave = null;
	protected TaskTracker(TestSlave slave) throws RemoteException {
		this.slave = slave;
	}

	private TaskThread task = null;
	
	public void createTask(jobId, taskId, inputpath, offset, length, true) {
		task = new TaskThread(slave);
		task.start();
		
		// TODO: reduece the mSlotCnt or rSlotCnt of this slave
		
	}
	
	public boolean checkProgress() {
		return task.checkProgress();
	}

}
