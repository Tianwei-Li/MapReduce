import java.rmi.*;

public interface TaskTrackerInterface extends Remote {

	public void createTask() throws RemoteException;
	
	public String checkProgress() throws RemoteException;

}
