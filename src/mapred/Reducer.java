package mapred;

import type.Context;
import util.RecordReader;

public abstract class Reducer<K1, V1, K2, V2> implements MapRedBase<K1, V1, K2, V2>{
	public abstract void reduce(K1 k, V1 v, Context context);
	public void setup() {
		
	}
	
	public void close() {
		
	}
	
	public void run(String split, Context context) throws InstantiationException, IllegalAccessException {
		setup();
		
		RecordReader<K1, V1> reader = new RecordReader<K1, V1>();
		reader.initializeReduce(split, context);
		
		while (reader.getNextKeyValue()) {
			reduce(reader.getCurrentKey(), reader.getCurrentValue(), context);
		}
		
		close();
	}
}
