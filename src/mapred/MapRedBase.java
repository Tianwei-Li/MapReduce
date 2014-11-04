package mapred;

import type.Context;

public interface MapRedBase<K1, V1, K2, V2> {
	public void setup();
	
	public void close();
	
	public void run(String split, Context context) throws InstantiationException, IllegalAccessException;
}
