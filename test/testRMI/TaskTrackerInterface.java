package testRMI;
import java.rmi.*;

public interface TaskTrackerInterface extends Remote {

	public void createTask() throws RemoteException;
	
	public int checkProgress() throws RemoteException;

}
