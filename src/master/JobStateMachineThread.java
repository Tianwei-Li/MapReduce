package master;

import java.io.IOException;

public class JobStateMachineThread extends Thread {
	private final Job job;
	public JobStateMachineThread(Job job) {
		super();
		this.job = job;
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread.isInterrupted() == false && job.jobState.get() != 4) {
			try {
				thisThread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			
			if (job.jobState.get() == 1) {
				if (job.finishedMapTaskCnt.get() == job.mapTaskNum) {
					job.jobState.incrementAndGet();
				}
			} else if (job.jobState.get() == 2) {
				// shuffle
				try {
					job.shuffle();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				job.jobState.incrementAndGet();
			} else if (job.jobState.get() == 3) {
				if (job.finishedReduceTaskCnt.get() == job.reduceTaskNum) {
					job.jobState.incrementAndGet();
				}
			}
		}
	}
	
	public void cancel() { 
		interrupt(); 
	}

}
