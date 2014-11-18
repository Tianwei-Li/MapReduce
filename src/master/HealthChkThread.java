package master;

import util.Peer;

public class HealthChkThread extends Thread {

	public HealthChkThread() {
		super();
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		// iterate the peer list and check timestamp
		TestMaster inst = TestMaster.getInstance();
		while (thisThread.isInterrupted() == false) {
			long curTime = System.currentTimeMillis();

			for (Peer peer : inst.slaveMap.values()) {
				if (curTime - peer.getTimeStamp() > 10000) {
					// peer is down
					// remove slave from slavemap
					inst.slaveMap.remove(peer.getName());

					// kill recv thread remove recvThread from recvThread map
					RecvThread thread = (RecvThread) inst.recvThreadMap.get(peer.getName());
					thread.cancel();
					inst.recvThreadMap.remove(peer.getName());
					
					// remove the item in slave capacity map
					inst.slaveCapacity.remove(peer.getName());

					// remove the aborted tasks
					// add them back to waiting list
					for (Job job : inst.jobList) {
						job.cancelTasks(peer.getName());
					}

				}
			}
			
			// check finished task and collect the free slot for each slave
			for (Job job : inst.jobList) {
				job.freeFinishedTasks();
			}
			
			// assign waiting tasks to available slave
			for (Job job : inst.jobList) {
				try {
					job.assignTasks();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				thisThread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void cancel() { 
		interrupt(); 
	}

}
