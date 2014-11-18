package slave;

import message.HeartBeatMessage;

public class SendHeartBeatThread extends Thread {
	TestSlave slave = null;
	
	public SendHeartBeatThread(TestSlave slave) {
		this.slave = slave;
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		// send the heat beat message
		while (thisThread.isInterrupted() == false) {
			HeartBeatMessage heartBeatMsg = new HeartBeatMessage();
			slave.send(heartBeatMsg);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void cancel() { 
		interrupt(); 
	}

}
