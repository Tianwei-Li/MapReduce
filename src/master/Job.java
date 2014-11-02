package master;

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
	public ConcurrentHashMap<String, ArrayList<TaskTrackerInterface>> taskTrackerList;
	public JobConf jobConf = null;
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
