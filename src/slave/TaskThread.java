package slave;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import file.FileClient;
import master.Task;
import util.Peer;

public class TaskThread extends Thread {
	TestSlave slave = null;
	Task task = null;
	boolean finished = false;
	
	public TaskThread(TestSlave slave, Task task) {
		this.slave = slave;
		this.task = task;
	}
	
	
	@Override
	public void run() {
		Peer master = slave.master;
		String split = null;
		try {
			split = FileClient.requestFileFromServer(master.getIp(), master.getPort(), task.getInPath(), task.getIndex(), task.getLen());
			
			// run the mapper task or reducer task
			task.runTask(split);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finished = true;
	}
	
	public boolean checkProgress() {
		return finished;
	}
	
	
	

}
