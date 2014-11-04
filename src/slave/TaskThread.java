package slave;

public class TaskThread extends Thread {
	TestSlave slave = null;
	public TaskThread(TestSlave slave) {
		this.slave = slave;
	}
	
	
	@Override
	public void run() {
		
		// TODO: run the mapper task or reducer task
		
		// TODO: add t he mSlotCnt or rSlotCnt
	}
	
	public boolean checkProgress() {
		// TODO: return fininshed or not
		return false;
	}
	
	
	

}
