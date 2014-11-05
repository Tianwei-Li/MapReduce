package master;
import java.rmi.*;

public interface TaskTrackerInterface extends Remote {

	public void createTask(Task task) throws RemoteException;
	
	public boolean checkProgress() throws RemoteException;
}
