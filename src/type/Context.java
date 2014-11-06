package type;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class Context implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3546500950581986470L;
	final List<Pair<?, ?>> result;
	final String workClass;
	final Class<?> inputKeyClass;
	final Class<?> inputValueClass;
	final Class<?> outputKeyClass;
	final Class<?> outputValueClass;
	final String jobId;
	final String taskId;
	final String jarURL;
	
	public Context(String jarURL, String workClassName, String jobId, String taskId) throws ClassNotFoundException, MalformedURLException {
		this.jarURL = jarURL;
		this.workClass = workClassName;
		ClassLoader loader = URLClassLoader.newInstance(
			    new URL[] { new URL("file:" + jarURL) }
			);
		Class<?> workClass = Class.forName(workClassName, false, loader);
		inputKeyClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 0, 0);
		inputValueClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 1, 0);
		outputKeyClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 2, 0);
		outputValueClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 3, 0);
		result = new ArrayList<>();
		this.jobId = jobId;
		this.taskId = taskId;
	}
	
	public void collect(Pair<?, ?> pair) {
		result.add(pair);
	}

	public List<Pair<?, ?>> getResult() {
		return result;
	}

	public Class<?> getWorkClass() throws MalformedURLException, ClassNotFoundException {
		ClassLoader loader = URLClassLoader.newInstance(
			    new URL[] { new URL("file:" + jarURL) }
			);
		return Class.forName(this.workClass, false, loader);
	}

	public Class<?> getInputKeyClass() {
		return inputKeyClass;
	}

	public Class<?> getInputValueClass() {
		return inputValueClass;
	}

	public Class<?> getOutputKeyClass() {
		return outputKeyClass;
	}

	public Class<?> getOutputValueClass() {
		return outputValueClass;
	}
}
