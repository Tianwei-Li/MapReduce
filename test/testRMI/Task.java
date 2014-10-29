package testRMI;

public class Task extends Thread {
	private int progress = 0;
	public Task() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10000);
				progress++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int checkProgress() {
		return progress;
	}
	
	
	

}
