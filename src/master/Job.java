package master;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import configuration.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import configuration.JobConf;

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




	public final static String MR_HOME = "";
	public final static String shuffleFilePrefix = "s_000";
	public Job(JobConf jobConf) {
		this.jobConf = jobConf;
		taskTrackerList = new ConcurrentHashMap<String, ArrayList<TaskTrackerInterface>>();
	}
	
	public void shuffle() throws IOException {
		File jobDir = new File(MR_HOME + jobConf.getJobId());
		final int reduceNumb = jobConf.getReducerNum();
		final String prefix = jobDir + "/" + shuffleFilePrefix;
		//split the mapper files into pieces.
		for (File file : jobDir.listFiles()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				int hashcode = line.split("\t")[0].hashCode();
				hashcode = hashcode < 0 ? hashcode * -1 : hashcode;
				String reducer = String.valueOf(hashcode % reduceNumb);
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(prefix + reducer , true)))) {
				    out.println(line);
				}catch (IOException e) {
				}
			}
			br.close();
		}
		
		
	}
	
	

}
