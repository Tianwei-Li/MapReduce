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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.yaml.snakeyaml.Yaml;

import configuration.JobConf;

public class Job {
	public ConcurrentHashMap<String, ArrayList<RunningTask>> runningTaskMap;
	public BlockingQueue<Task> waitingMapTasks;
	public BlockingQueue<Task> waitingReduceTasks; 
	public JobConf jobConf = null;
	public final static String configFile = "MRSetup.yaml";
	public final static String shuffleFilePrefix = "s_000";
	public final static String mapFilePrefix = "m_000";
	public final int mapTaskNum;
	public final int reduceTaskNum;


	public Job(JobConf conf) throws IOException, InterruptedException {
		jobConf = conf;
		runningTaskMap = new ConcurrentHashMap<String, ArrayList<RunningTask>>();
		waitingMapTasks = new LinkedBlockingQueue<Task>();
		waitingReduceTasks = new LinkedBlockingQueue<Task>();
		
		//Setup some configuration via static config file.
		parseConfig(conf, configFile);
		//jobConf.setReducerNum(TestMaster.getInstance().slaveMap.size());
		
		mapTaskNum = createMapTasks();
		reduceTaskNum = conf.getReducerNum();
	}

	public int createMapTasks() throws IOException, InterruptedException {
		int index = 0;
		int line = jobConf.getMapSplit();
		final String inputFile = jobConf.getInputPath();
		RandomAccessFile reader = new RandomAccessFile(inputFile, "r");
		int count = 0;
		while (reader.readLine() != null) {
			line--;
			if (line == 0) {
				line = jobConf.getMapSplit();
				waitingMapTasks.put(new Task(TaskType.MAP_TASK, inputFile, index,(int) reader.getFilePointer() - index, jobConf.getMapperClass(), jobConf.getJobId(), mapFilePrefix + count));
				index =(int) reader.getFilePointer();
				count++;
			}
		}
		if (line != jobConf.getMapSplit()) {
			waitingMapTasks.put(new Task(TaskType.MAP_TASK, inputFile, index,(int) reader.getFilePointer() - index, jobConf.getMapperClass(), jobConf.getJobId(), mapFilePrefix + count));
			count++;
		}
		reader.close();
		return count;
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
	
	




	
	
	public void shuffle() throws IOException {
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
				}
			}
			br.close();
		}
		
		
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
