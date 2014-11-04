package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import type.Context;
import type.Writable;

public class RecordReader<K, V> {
	Iterator<Pair<K, V>> iterator;
	K currentKey;
	V currentValue;
	
	@SuppressWarnings("unchecked")
	public void initialize(String split, Context context) throws InstantiationException, IllegalAccessException {
		if ("".equals(split) || split == null) {
			return;
		}
		
		String[] lines = split.split("[\r\n]+");
		List<Pair<K, V>> list = new ArrayList<>();
		for (final String line : lines) {
			int index = line.indexOf('\t');
			String keyString = line.substring(0, index);
			String valString = line.substring(index + 1);
			Writable writableKey = ((Writable)context.getInputKeyClass().newInstance()).parse(keyString);
			Writable writableVal =((Writable) context.getInputValueClass().newInstance()).parse(valString);
			
			list.add(new Pair<K, V> ((K)writableKey, (V)writableVal));
		}
		iterator = list.iterator();
	}
	
	public boolean getNextKeyValue() {
		if (iterator.hasNext()) {
			Pair<K, V> pair = iterator.next();
			currentKey = pair.getK();
			currentValue = pair.getV();
			return true;
		}
		return false;
	}
	
	public K getCurrentKey(){
		return currentKey;
	}
	
	public V getCurrentValue() {
		return currentValue;
	}
}
