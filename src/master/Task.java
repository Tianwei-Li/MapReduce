package master;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import type.Context;

public class Task implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -807407476086709707L;
	final TaskType type;
	final String inPath;
	final int index;
	final int len;
	Context context;
	public Task(String url, TaskType type, String inPath, int index, int len, String workClass, String taskId, String jobId) throws ClassNotFoundException, MalformedURLException {
		this.type = type;
		this.inPath = inPath;
		this.index = index;
		this.len = len;
		context = new Context(url, workClass, taskId, jobId);
	}
	public TaskType getType() {
		return type;
	}
	public String getInPath() {
		return inPath;
	}
	public int getIndex() {
		return index;
	}
	public int getLen() {
		return len;
	}
	@Override
	public String toString() {
		return "Task [type=" + type + ", inPath=" + inPath + ", index=" + index
				+ ", len=" + len + "]";
	}
	public Context getContext() {
		return context;
	}
	
	public void runTask(String split) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, MalformedURLException, ClassNotFoundException, FileNotFoundException {
		Method method = context.getWorkClass().getMethod("run", String.class, Context.class);
		method.invoke(context.getWorkClass().newInstance(), split, context);
		

		System.out.println(context.getResult());
	}

}
