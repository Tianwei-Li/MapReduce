package configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import util.Pair;


@SuppressWarnings("rawtypes")
public class JobConf {
	private String jobName;
	private Class inputKeyClass;
	private Class inputValueClass;
	private Class outputKeyClass;
	private Class outputValueClass;
	private Class mapperClass;
	private Class reducerClass;
	private String inputPath;
	private String outputPath;
	
	public JobConf(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Class getInputKeyClass() {
		return inputKeyClass;
	}
	public void setInputKeyClass(Class inputKeyClass) {
		this.inputKeyClass = inputKeyClass;
	}
	public Class getInputValueClass() {
		return inputValueClass;
	}
	public void setInputValueClass(Class inputValueClass) {
		this.inputValueClass = inputValueClass;
	}
	public Class getOutputKeyClass() {
		return outputKeyClass;
	}
	public void setOutputKeyClass(Class outputKeyClass) {
		this.outputKeyClass = outputKeyClass;
	}
	public Class getOutputValueClass() {
		return outputValueClass;
	}
	public void setOutputValueClass(Class outputValueClass) {
		this.outputValueClass = outputValueClass;
	}
	public Class getMapperClass() {
		return mapperClass;
	}
	public void setMapperClass(Class mapperClass) {
		this.mapperClass = mapperClass;
	}
	public Class getReducerClass() {
		return reducerClass;
	}
	public void setReducerClass(Class reducerClass) {
		this.reducerClass = reducerClass;
	}
	public String getInputPath() {
		return inputPath;
	}
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	@SuppressWarnings("unchecked")
	public void runMapper() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Method mapper = mapperClass.getMethod("map", inputKeyClass, inputValueClass, List.class);
		List<Pair<?, ?>> list = new ArrayList<>();
		mapper.invoke(mapperClass.newInstance(), "key1","value1", list);
		for (Pair<?,?> pair : list) {
			System.out.println(pair.getK());
			System.out.println(pair.getV());
		}
		
	}
}
