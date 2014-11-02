package test;

import java.io.IOException;

import master.Job;
import configuration.JobConf;

public class MapRedTest {
	public static void shuffleTest() throws IOException {
		JobConf jobConf = new JobConf("test job");
		jobConf.setJobId("job007");
		Job job = new Job(jobConf);
		job.shuffle();
	}
	
	public static void main(String[] args) throws IOException {
		shuffleTest();
	}
}
