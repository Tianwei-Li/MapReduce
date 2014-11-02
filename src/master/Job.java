package master;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import configuration.*;

public class Job {
	public ConcurrentHashMap<String, ArrayList<RunningTask>> runningTaskMap;
	public BlockingQueue<Task> waitingMapTasks;
	public BlockingQueue<Task> waitingReduceTasks; 
	public JobConf jobConf = null;


	public Job(JobConf conf) {
		jobConf = conf;
		runningTaskMap = new ConcurrentHashMap<String, ArrayList<RunningTask>>();
		waitingMapTasks = new LinkedBlockingQueue<Task>();
		waitingReduceTasks = new LinkedBlockingQueue<Task>();
	}

	/**
	 * cancel all tasks running on that slave because the slave is down
	 * @param slaveName
	 */
	public void cancelTasks(String slaveName) {
		if (runningTaskMap.containsKey(slaveName)) {
			ArrayList<RunningTask> runningTasks = runningTaskMap.remove(slaveName);
			for (RunningTask runningTask : runningTasks) {
				if (runningTask.getTaskType() == TaskType.MAP_TASK) {
					waitingMapTasks.add(runningTask.task);
				} else {
					waitingReduceTasks.add(runningTask.task);
				}
			}
		}
	}
	
	
	public void freeFinishedTasks() {
		for (ArrayList<RunningTask> runningTasks : runningTaskMap.values()) {
			for (RunningTask runningTask : runningTasks) {
				runningTask.isFinished() == ture
			}
		}
	}





}
