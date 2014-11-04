package type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class Context implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3546500950581986470L;
	final List<Pair<?, ?>> result;
	final Class<?> workClass;
	final Class<?> inputKeyClass;
	final Class<?> inputValueClass;
	final Class<?> outputKeyClass;
	final Class<?> outputValueClass;
	
	public Context(Class<?> workClass) {
		this.workClass = workClass;
		inputKeyClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 0, 0);
		inputValueClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 1, 0);
		outputKeyClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 2, 0);
		outputValueClass = WritableFacility.getClass(workClass.getGenericSuperclass(), 3, 0);
		result = new ArrayList<>();
	}
	
	public void collect(Pair<?, ?> pair) {
		result.add(pair);
	}

	public List<Pair<?, ?>> getResult() {
		return result;
	}

	public Class<?> getWorkClass() {
		return workClass;
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
