package mapred;

import type.Context;
import util.RecordReader;

public abstract class Mapper<K1, V1, K2, V2> implements MapRedBase<K1, V1, K2, V2> {
	public abstract void map(K1 k, V1 v, Context context);
	public void setup() {
		
	}
	
	public void close() {
		
	}
	
	public void run(String split, Context context) throws InstantiationException, IllegalAccessException {
		setup();
		
		RecordReader<K1, V1> reader = new RecordReader<K1, V1>();
		reader.initialize(split, context);
		
		while (reader.getNextKeyValue()) {
			map(reader.getCurrentKey(), reader.getCurrentValue(), context);
		}
		
		close();
	}
}
