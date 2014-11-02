package master;

import java.rmi.RemoteException;

public class RunningTask {
	public Task task;
	public TaskTrackerInterface tracker;
	
	public RunningTask(Task task, TaskTrackerInterface tracker) {
		this.task = task;
		this.tracker = tracker;
	}
	
	public TaskType getTaskType() {
		return task.type;
	}
	
	public boolean isFinished() {
		try {
			return tracker.checkProgress();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return false;
	}

}
