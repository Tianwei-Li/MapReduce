package master;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import configuration.*;

public class Job {
	public ConcurrentHashMap<String, ArrayList<TaskTrackerInterface>> taskTrackerList;
	public JobConf jobConf = null;
	public Job() {
		jobConf = new JobConf(jobName);
		taskList = new ConcurrentHashMap<String, ArrayList<TaskTrackerInterface>>();
	}
	
	
	
	

}
