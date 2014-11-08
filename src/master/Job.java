package master;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.yaml.snakeyaml.Yaml;

import configuration.JobConf;

public class Job {
	public ConcurrentHashMap<String, BlockingQueue<RunningTask>> runningTaskMap;
	public BlockingQueue<Task> waitingMapTasks;
	public BlockingQueue<Task> waitingReduceTasks; 
	public JobConf jobConf = null;
	public final static String shuffleFilePrefix = "s_000";
	public final static String mapFilePrefix = "m_000";
	public final static String redFilePrefix = "r_000";
	public final int mapTaskNum;
	public final int reduceTaskNum;
	
	// 1 means JOB_MAP, 2 means JOB_SHUFFLE
	// 3 means JOB_REDUCE, 4 means JOB_DONE
	public AtomicInteger jobState = new AtomicInteger(1);
	
	public AtomicInteger finishedMapTaskCnt = new AtomicInteger(0);
	public AtomicInteger finishedReduceTaskCnt = new AtomicInteger(0);


	public Job(JobConf conf) throws IOException, InterruptedException, ClassNotFoundException {
		jobConf = conf;
		runningTaskMap = new ConcurrentHashMap<String, BlockingQueue<RunningTask>>();
		waitingMapTasks = new LinkedBlockingQueue<Task>();
		waitingReduceTasks = new LinkedBlockingQueue<Task>();

		//Setup some configuration via static config file.
		parseConfig(conf, TestMaster.getInstance().configFile);
		//jobConf.setReducerNum(TestMaster.getInstance().slaveMap.size());

		mapTaskNum = createMapTasks();
		reduceTaskNum = conf.getReducerNum();
		
		JobStateMachineThread stateMThread = new JobStateMachineThread(this);
		stateMThread.start();
	}

	public int createMapTasks() throws IOException, InterruptedException, ClassNotFoundException {
		int index = 0;
		int line = jobConf.getMapSplit();
		final String inputFile = jobConf.getInputPath();
		RandomAccessFile reader = new RandomAccessFile(inputFile, "r");
		int count = 0;
		while (reader.readLine() != null) {
			line--;
			if (line == 0) {
				line = jobConf.getMapSplit();
				String outputPath = jobConf.getMRHome() + jobConf.getJobId() + "/" + mapFilePrefix + count;
				waitingMapTasks.put(new Task(jobConf.getJarUrl(), TaskType.MAP_TASK, inputFile, index,(int) reader.getFilePointer() - index, jobConf.getMapperClass(), outputPath));
				index =(int) reader.getFilePointer();
				count++;
			}
		}
		if (line != jobConf.getMapSplit()) {
			String outputPath = jobConf.getMRHome() + jobConf.getJobId() + "/" + mapFilePrefix + count;
			waitingMapTasks.put(new Task(jobConf.getJarUrl(), TaskType.MAP_TASK, inputFile, index,(int) reader.getFilePointer() - index, jobConf.getMapperClass(), outputPath));
			count++;
		}
		reader.close();
		return count;
	}
	
	public void createReduceTasks(List<String> files) throws InterruptedException, ClassNotFoundException, MalformedURLException {
		int count = 0;
		for (String file : files) {
			String outputPath = jobConf.getMRHome() + jobConf.getJobId() + "/" + redFilePrefix + count;
			waitingReduceTasks.put(new Task(jobConf.getJarUrl(), TaskType.REDUCE_TASK, file, 0, (int)new File(file).length(), jobConf.getReducerClass(), outputPath));
			count++;
		}
	}


	/**
	 * cancel all tasks running on that slave because the slave is down
	 * @param slaveName
	 */
	public void cancelTasks(String slaveName) {
		if (runningTaskMap.containsKey(slaveName)) {
			BlockingQueue<RunningTask> runningTasks = runningTaskMap.remove(slaveName);
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
		TestMaster inst = TestMaster.getInstance();
		for (Map.Entry<String, BlockingQueue<RunningTask>> entry : runningTaskMap.entrySet()) {
			String slaveName = entry.getKey();
			BlockingQueue<RunningTask> runningTasks = entry.getValue();
			for (RunningTask runningTask : runningTasks) {
				if (runningTask.isFinished() == true) {
					runningTasks.remove(runningTask);

					// update the slave capacity
					ArrayList<Integer> slots = inst.slaveCapacity.get(slaveName);
					if (runningTask.getTaskType() == TaskType.MAP_TASK) {
						slots.set(0, slots.get(0) + 1);
						finishedMapTaskCnt.incrementAndGet();
					} else {
						slots.set(1, slots.get(1) + 1);
						finishedReduceTaskCnt.incrementAndGet();
					}
				}
			}
		}
	}
	
	public void terminateJob() {
		TestMaster inst = TestMaster.getInstance();
		for (Map.Entry<String, BlockingQueue<RunningTask>> entry : runningTaskMap.entrySet()) {
			String slaveName = entry.getKey();
			BlockingQueue<RunningTask> runningTasks = entry.getValue();
			for (RunningTask runningTask : runningTasks) {
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
					BlockingQueue<RunningTask> runningList = null;
					if (runningTaskMap.containsKey(slaveName) == false) {
						runningList = new LinkedBlockingQueue<RunningTask>();
					} else {
						runningList = runningTaskMap.get(slaveName);
					}
					runningList.add(runningTask);
					runningTaskMap.put(slaveName, runningList);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}










	public void shuffle() throws IOException, InterruptedException, ClassNotFoundException {
		List<String> outputFiles = new ArrayList<>();
		File jobDir = new File(jobConf.getMRHome() + jobConf.getJobId());
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
					System.out.println("Error when writing files in shuffle stage.");
				}
			}
			br.close();
		}
		
		//Generating shuffle file paths
		for (int i = 0; i < reduceNumb; ++i) {
			outputFiles.add(prefix + i);
		}
		
		//fill reduce task in the queue
		createReduceTasks(outputFiles);
	}

	public void parseConfig(JobConf jobConf, String configFile) throws FileNotFoundException {
		InputStream input = null;
		input = new FileInputStream(new File(configFile));
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) yaml.load(input);

		Integer splitNum =(Integer) data.get("split_line_number");
		if (splitNum != null) {
			jobConf.setMapSplit(splitNum);
		}

		String mrHome = (String) data.get("mr_home");
		if (mrHome != null) {
			jobConf.setMRHome(mrHome);
		}
	}



}
