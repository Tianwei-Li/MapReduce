package master;

import java.io.Serializable;

public class Task implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -807407476086709707L;
	final TaskType type;
	final String inPath;
	final int index;
	final int len;
	public Task(TaskType type, String inPath, int index, int len) {
		this.type = type;
		this.inPath = inPath;
		this.index = index;
		this.len = len;
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

}
