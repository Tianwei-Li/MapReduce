package master;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import type.Context;
import util.Pair;

public class Task implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -807407476086709707L;
	final TaskType type;
	final String inPath;
	final String outPath;
	final int index;
	final int len;
	Context context;
	public Task(String url, TaskType type, String inPath, int index, int len, String workClass, String outputPath) throws ClassNotFoundException, MalformedURLException {
		this.type = type;
		this.inPath = inPath;
		this.index = index;
		this.len = len;
		this.outPath = outputPath;
		context = new Context(url, workClass);
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
		

	}
	
	public byte[] getResultBytes() {
		StringBuilder sb = new StringBuilder();
		for (Pair<?, ?> outPair : context.getResult()) {
			sb.append(outPair.getK() + "\t" + outPair.getV() + '\n');
		}
		return sb.toString().getBytes();
	}
	public String getOutPath() {
		return outPath;
	}

}
