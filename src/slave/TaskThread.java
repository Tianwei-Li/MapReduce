package slave;

import master.Task;
import util.Peer;
import file.FileClient;

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
		Peer fileServer = slave.fileServer;
		String split = null;
		try {
			split = FileClient.requestFileFromServer(fileServer.getIp(), fileServer.getPort(), task.getInPath(), task.getIndex(), task.getLen());
			
			// run the mapper task or reducer task
			task.runTask(split);
			byte[] result = task.getResultBytes();
			FileClient.sendFile(fileServer.getIp(), fileServer.getPort(), task.getOutPath(), result);
			finished = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finished = true;
	}
	
	public boolean checkProgress() {
		return finished;
	}
	
	
	

}
