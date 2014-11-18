package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import type.Context;
import type.Writable;
import type.WritableFacility;

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
			String keyString = "";
			if (index != -1)
				keyString = line.substring(0, index);
			String valString = line.substring(index + 1);
			Writable writableKey = ((Writable)context.getInputKeyClass().newInstance()).parse(keyString);
			Writable writableVal =((Writable) context.getInputValueClass().newInstance()).parse(valString);
			
			list.add(new Pair<K, V> ((K)writableKey, (V)writableVal));
		}
		iterator = list.iterator();
	}
	
	/**
	 * Input value type V must be a iterator in reducer class.
	 * @param split
	 * @param context
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public void initializeReduce(String split, Context context) throws InstantiationException, IllegalAccessException {
		if ("".equals(split) || split == null) {
			return;
		}
		
		String[] lines = split.split("[\r\n]+");
		Arrays.sort(lines, new Comparator<String>(){

			@Override
			public int compare(String arg0, String arg1) {
				int index = arg0.indexOf('\t');
				String keyString1 = "";
				if (index != -1)
					keyString1 = arg0.substring(0, index);
				
				index = arg0.indexOf('\t');
				String keyString2 = "";
				if (index != -1)
					keyString2 = arg1.substring(0, index);
				return keyString1.compareTo(keyString2);
			}
			
		});
		
		String key = "";
		List<Writable>  iterable = new ArrayList<>();
		List<Pair<K, V>> list = new ArrayList<>();
		for (final String line : lines) {
			int index = line.indexOf('\t');
			String keyString = "";
			if (index != -1)
				keyString = line.substring(0, index);
			if (key.equals("")) {
				key = keyString;
			}
			if (!key.equals(keyString)) {
				Writable writableKey = ((Writable)context.getInputKeyClass().newInstance()).parse(key);
				list.add(new Pair<K, V>((K) writableKey, (V) iterable.iterator()));
				key = keyString;
				iterable = new ArrayList<>();
			}
			
			String valString = line.substring(index + 1);
			//Get generic type for iterator.
			Writable writableVal = ((Writable)WritableFacility.getClass(context.getInputValueClass().getGenericSuperclass(), 0, 0).newInstance()).parse(valString);
			iterable.add(writableVal);
		}
		
		//Add the last key value pair.
		Writable writableKey = ((Writable)context.getInputKeyClass().newInstance()).parse(key);
		list.add(new Pair<K, V>((K) writableKey, (V) iterable.iterator()));
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
