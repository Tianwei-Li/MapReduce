package master;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import testRMI.LaunchTaskInterface;

import configuration.JobConf;

public class Job {
	public ConcurrentHashMap<String, ArrayList<RunningTask>> runningTaskMap;
	public BlockingQueue<Task> waitingMapTasks;
	public BlockingQueue<Task> waitingReduceTasks; 
	public JobConf jobConf = null;
	public final static String MR_HOME = "";
	public final static String shuffleFilePrefix = "s_000";


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
			TestMaster inst = TestMaster.getInstance();
			for (RunningTask runningTask : runningTasks) {
				ArrayList<Integer> slots = inst.slaveCapacity.get(slaveName);
				if (runningTask.getTaskType() == TaskType.MAP_TASK) {
					waitingMapTasks.add(runningTask.task);
					slots.set(0, slots.get(0) + 1);
				} else {
					waitingReduceTasks.add(runningTask.task);
					slots.set(1, slots.get(1) + 1);
				}
			}
		}
	}
	
	public void freeFinishedTasks() {
		TestMaster inst = TestMaster.getInstance();
		for (Map.Entry<String, ArrayList<RunningTask>> entry : runningTaskMap.entrySet()) {
			String slaveName = entry.getKey();
			ArrayList<RunningTask> runningTasks = entry.getValue();
			for (RunningTask runningTask : runningTasks) {
				if (runningTask.isFinished() == true) {
					runningTasks.remove(runningTask);
					
					// update the slave capacity
					ArrayList<Integer> slots = inst.slaveCapacity.get(slaveName);
					if (runningTask.getTaskType() == TaskType.MAP_TASK) {
						slots.set(0, slots.get(0) + 1);
					} else {
						slots.set(1, slots.get(1) + 1);
					}
				}
			}
		}
	}
	
	
	public void assignTasks() {
		if (waitingReduceTasks.size() != 0) {
			assignTask(waitingReduceTasks, 1);
		} else {
			assignTask(waitingMapTasks, 0);
		}
	}
	
	private void assignTask(BlockingQueue<Task> waitingTasks, int slotIdx) {
		if (waitingTasks.size() == 0) {
			return;
		}
		
		TestMaster inst = TestMaster.getInstance();
		for (Map.Entry<String, ArrayList<Integer>> entry : inst.slaveCapacity.entrySet()) {
			String slaveName = entry.getKey();
			String ip = inst.slaveMap.get(slaveName).getIp();
			ArrayList<Integer> slots = entry.getValue();
			if (slots.get(slotIdx) > 0) {
				if (waitingTasks.size() == 0) {
					return;
				}
				
				Task task = waitingTasks.poll();
				LaunchTaskInterface launcher = null;
				try {
					launcher = (LaunchTaskInterface) Naming.lookup ("rmi://" + ip + "/" + slaveName);
					TaskTrackerInterface tracker = launcher.createTaskTracker();
					tracker.createTask(task);
					RunningTask runningTask = new RunningTask(task, tracker);
					
					// decrease the slot
					slots.set(slotIdx, slots.get(slotIdx) - 1);
					
					// add the running task to runningTaskMap
					ArrayList<RunningTask> runningList = null;
					if (runningTaskMap.containsKey(slaveName) == false) {
						runningList = new ArrayList<RunningTask>();
					} else {
						runningList = runningTaskMap.get(slaveName);
					}
					runningList.add(runningTask);
					runningTaskMap.put(slaveName, runningList);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
				
			}
		}
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
