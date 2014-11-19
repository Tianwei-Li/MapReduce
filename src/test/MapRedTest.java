package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import mapred.Mapper;
import master.Job;
import master.Task;
import type.Context;
import type.DoubleWritable;
import type.IntWritable;
import type.LongWritable;
import type.Text;
import configuration.JobConf;

public class MapRedTest {
	public static void shuffleTest() throws IOException, InterruptedException, ClassNotFoundException {
		JobConf jobConf = new JobConf("test job");
		jobConf.setJobId("job007");
		Job job = new Job(jobConf);
		job.shuffle();
	}
	
	public static void createTaskTest() throws IOException, InterruptedException, ClassNotFoundException {
		JobConf jobConf = new JobConf("test job");
		jobConf.setJobId("job007");
		jobConf.setInputPath("received.txt");
		Job job = new Job(jobConf);
		String prefix = "job007/task_00";
		int id = 0;
		for (Task task : job.waitingMapTasks) {
			byte[] mybytearray = new byte[task.getLen()];
			RandomAccessFile file = new RandomAccessFile(task.getInPath(), "r");
			System.out.println(task);
			file.seek(task.getIndex());
			file.read(mybytearray, 0, task.getLen());
			file.close();
			
			FileOutputStream fos = new FileOutputStream(prefix + id );
			fos.write(mybytearray);
			fos.close();
			id++;
		}
	}
	
	static class testMapper extends Mapper<Text, IntWritable, LongWritable, DoubleWritable> {


		@Override
		public void map(Text k, IntWritable v, Context context) {
			// TODO Auto-generated method stub
			System.out.println("key:" + k.getText());
			System.out.println("value:" + v.getValue());
		}


	
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, InstantiationException, IllegalAccessException {
		testMapper tm = new testMapper();
		//tm.run("231", new Context(testMapper.class, "job007", "m_001"));
	}
}
